package com.example.mylibrary_wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by afsahulsyed on 12/6/17.
 */

public class ServerSocketConnect extends AsyncTask<Void, Void, String> {

    final String TAG = "sMess";
    static ServerSocket serverSocket;
    Socket mSocket;
    int port;

    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "sd_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "sd_go_port";

    public ServerSocketConnect(){
        Log.d(TAG,"ServerSocketConnect blank constructor");
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG,"ServerSocketConnect doInBackground");
            try {
                serverSocket = new ServerSocket(port);
                Log.d(TAG,"waiting for connection to happen......server side " + port);
                mSocket = serverSocket.accept();
                Log.d(TAG,"socket connected - " + mSocket);
            }catch (Exception e){
                Log.d(TAG,e.toString());
                e.printStackTrace();
            }
        return null;
    }

    public void initSocket(final int port){
        Log.d(TAG,"init socket with port " + port);
        this.port = port;

        Log.d(TAG,"ServerSocketConnect calling do in background");
       // this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.doInBackground();

        Log.d(TAG,"exiting initSocket server");
    }

    public Socket getServerSocket(){
        Log.d(TAG,"ServerSocketConnect getServerSocket - "+ mSocket);
        return mSocket;
    }

    public void closeServerSocket(){
        Log.d(TAG,"ServerSocketConnect closeServerSocket " + port);
        if(mSocket!=null) {
            if (!mSocket.isClosed()) {
                try {
                    mSocket.close();
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mSocket = null;
        }
        serverSocket = null;

        Log.d(TAG,"ServerSocketConnect status of this Background task" + this.getStatus());
        Log.d(TAG,"ServerSocketConnect Socket => " + mSocket);
    }
}
