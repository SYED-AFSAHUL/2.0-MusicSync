package com.example.mylibrary_wifidirect;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Time;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by afsahulsyed on 12/6/17.
 */

public class ClientSocketConnect extends AsyncTask<Void, Void, String> {

    private final String TAG = "sMess";

    private static final int SOCKET_TIMEOUT = 500000;
    Socket socket;
    private ScheduledThreadPoolExecutor socketClientConnSch;// = new ScheduledThreadPoolExecutor(1);
    public boolean SocketConnected = false;
    String GOadd;
    int port;

    ClientSocketConnect(){
        Log.d(TAG,"ClientSocketConnect blank constructor");
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d(TAG,"inside ClientSocketConnect do in background");
            Log.d(TAG,"1 socket = "+ socket);
            Log.d(TAG,"2 connecting to socket.....");
            //socket.bind(null);
            socketClientConnSch = new ScheduledThreadPoolExecutor(1);
            socketClientConnSch.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"inside clientSocketConnect doInBackground run "+ port);

                    socket = new Socket();

                    Log.d(TAG,"SocketConnected variable - " + SocketConnected);
                    try {
                        String host = GOadd;
                        socket.bind(null);
                        socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                    }catch (Exception e){
                        Log.d(TAG,"error at clientSocketConn at run1");
                        Log.d(TAG,e.getMessage());
                        e.printStackTrace();
                    }

                    if(socket.isConnected()){
                        Log.d(TAG,"ClientSocketConnect socket.isConnected() at " + port);
                        SocketConnected = true;
                        socketClientConnSch.shutdownNow();
                    }
                }
            },0,1500, TimeUnit.MILLISECONDS);

            Log.d(TAG, "socketConnected value - >" + SocketConnected);
        } catch (Exception e){
            Log.d(TAG,"error at clientSocketConn at run2");
            e.printStackTrace();
        }
        return null;
    }


    void initSocket(final String GOadd,final int port){
        Log.d(TAG,"init socket " + port);

        /**
         * Create a client socket with the host,
         * port, and timeout information.
         */
        this.port = port;
        this.GOadd = GOadd;
       // this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.doInBackground();
        Log.d(TAG,"exiting initSocket");
    }

    public Socket getClientSocket(){
        Log.d(TAG,"ClientSocketConnect getClientSocket");
        socketClientConnSch.shutdownNow();
        SocketConnected = false;
        return socket;
    }

    public void closeSocket(){
        Log.d(TAG,"ClientSocketConnect closeSocket");
        if(socket!=null) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            socket = null;
        }
    }
}
