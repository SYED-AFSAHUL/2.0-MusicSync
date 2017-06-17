package com.example.mylibrary_wifidirect;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by afsahulsyed on 13/6/17.
 */

public class ClientReceiveMedia extends AsyncTask<Void, Void, String> {

    private static final int SOCKET_TIMEOUT = 5000;
    private ClientSocketConnect clientSocketConnect;
    Socket socket;
    private static final String TAG = "sMess";
    private String mFileExtension;
    boolean audioTransferComplete = false;
    Context mContext;
    File f;
    File path;

    public ClientReceiveMedia(){
        Log.d(TAG,"empty ClientReceiveMedia constructor");
    }

    public ClientReceiveMedia(ClientSocketConnect clientSocketConnect, String ext, Context mContext){
        Log.d(TAG,"2 para ClientReceiveMedia constructor");
        this.clientSocketConnect = clientSocketConnect;
        mFileExtension = ext;
    }

    @Override
    protected String doInBackground(Void... params) {
        socket = clientSocketConnect.getClientSocket();

        try {
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            f = new File(path,"aaa-MS--wifiP2p"+System.currentTimeMillis()+ mFileExtension);

            File dirs = new File(f.getParent());
            try {
                if (!dirs.exists()) {
                    dirs.mkdirs();
                    Log.d(TAG, "!dirs.exists()");
                }
                f.createNewFile();
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            if (f.exists()) {
                Log.d(TAG, "dir exist");
            }else{
                Log.d(TAG, "dir still doesn't exist");
            }

            InputStream inputstream = socket.getInputStream();
            Log.d(TAG,"ClientReceiveMusic got input stream - " + inputstream.toString());
            if(copyFile(inputstream, new FileOutputStream(f))){
                Log.d(TAG,"successfully copied");
            } else {
                Log.d(TAG,"error in coping");
            }
            Log.d(TAG,"ClientReceive Music transmission complete, setting variable audioTransferComplete to true");
            audioTransferComplete = true;
            Log.d(TAG,"exiting doInBackGround");
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public boolean copyFile(InputStream inputStream, OutputStream out) {
        Log.d(TAG,"ClientReceiveData copyFile");
        byte buf[] = new byte[1024];
        int len;
        Log.d(TAG,"going inside try");
        try {
            Log.d(TAG,"copy file starting copy.....");
            int count = 0;
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                Log.d(TAG," Sno. - " + count++ +"copy file inside while with data - " + buf.toString() + " and length - "+ len);
            }
            Log.d(TAG,"Copy complete");
            out.close();
            inputStream.close();
            Log.d(TAG,"exiting copy file");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"error at copying file.....exiting");
            Log.d(TAG,e.getMessage());
            return false;
        }
        Log.d(TAG,"exiting copy file with true");
        return true;
    }

    public String getPathData(){
        Log.d(TAG,"ClientReceiveMedia setting audioTransferComplete to false and returning path");
        audioTransferComplete = false;
        return f.getAbsolutePath();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"ClientReceiveMusic onPostExecute " + result);
        clientSocketConnect.closeSocket();
    }
}
