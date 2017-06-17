package com.example.mylibrary_wifidirect;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by afsahulsyed on 13/6/17.
 */

public class ServerSendMedia extends IntentService {

    private static final String TAG = "sMess";
    private static final int SOCKET_TIMEOUT = 500000;
    ServerSocketConnect serverSocketConnect;
    Socket socket;
    String path;
    byte buf[]  = new byte[1024];
    int len;
    private Context mContext;

    public ServerSendMedia(){
        super("ServerSendMedia");
        Log.d(TAG,"ServerSendMedia blank constructor");
    }
    public ServerSendMedia(String name){
        super(name);
        Log.d(TAG,"ServerSendMedia");
    }

    public ServerSendMedia(String name, ServerSocketConnect soc, String path, Context mContext){
        super(name);
        serverSocketConnect = soc;
        this.path = path;
        this.mContext = mContext;
        Log.d(TAG,"ServerSendMedia 4 param constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        //Context context = getApplicationContext();
        Log.d(TAG,"ServerSendMedia socket - before - " + socket);
        socket = serverSocketConnect.getServerSocket();
        Log.d(TAG,"ServerSendMedia socket - after - " + socket);
        String fPath = "file://" + path;

        if (intent.getAction().equals(ServerSocketConnect.ACTION_SEND_FILE)) {
            Log.d(TAG, "ACTION_SEND_FILE");

            try {
                Log.d(TAG,"ServerSendMedia starting transmission " + socket);
                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = mContext.getContentResolver();
                Log.d(TAG,"ServerSendMusic - setting to send media path is - " + fPath);
                Log.d(TAG,"content provider - " + cr);
                InputStream inputStream = cr.openInputStream(Uri.parse(fPath));
                int count = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                    Log.d(TAG,"Sno. - "+(count++) + "written data in output stream - " +buf.toString() + " with length " + len);
                }
                Log.d(TAG, "ServerSendMedia Sending media Finished.");
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, (" FileNotFoundException - " + e.getMessage()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }finally {
                Log.d(TAG,"ServerSendMedia closing socket");
                serverSocketConnect.closeServerSocket();
            }
        }
    }
}
