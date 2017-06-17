package com.example.mylibrary_wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by afsahulsyed on 12/6/17.
 */

public class ClientReceiveData extends AsyncTask<Void, Void, String> {

    private static final String TAG = "sMess";
    Socket socket;
    ClientSocketConnect clientSocketConnect;
    String str = null;

    public ClientReceiveData(){
        Log.d(TAG,"empty ClientReceiveData");
    }

    public ClientReceiveData(ClientSocketConnect clientSocketConnect){
        Log.d(TAG,"1 param ClientReceiveData constructor");
        this.clientSocketConnect = clientSocketConnect;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG,"ClientReceiveData doInBackground");
        try {
            socket = clientSocketConnect.getClientSocket();

            InputStream inputstream = socket.getInputStream();
            Log.d(TAG,"ClientReceiveData data received complete");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.d(TAG,"baos -: "+ baos);
            int i;
            while ((i = inputstream.read()) != -1) {
                baos.write(i);
            }

            str = baos.toString();
            Log.d(TAG, "result string "+ str);
            return str;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            clientSocketConnect.closeSocket();
        }
    }

    public String getData(){
        return str;
    }
}
