package com.example.mylibrary_wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by afsahulsyed on 12/6/17.
 */

public class ServerSendData extends IntentService {

    final String TAG = "sMess";
    Socket mSocket;
    private String sendData;
    ServerSocketConnect serverSocketConnect;
    boolean dataSendComplete = false;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public ServerSendData(String name) {
        super(name);
        Log.d(TAG,"ServerSendData string constructor");
    }


    public ServerSendData() {
        super("ServerSendData");
        Log.d(TAG,"ServerSendData blank constructor");
    }

    public ServerSendData(String name, ServerSocketConnect serverSocketConnect) {
        super(name);
        Log.d(TAG,"ServerSendData 2 para constructor");
        Log.d(TAG,"serverSocketConnect- "+ serverSocketConnect);

        this.serverSocketConnect = serverSocketConnect;
    }

    public void setDataToSend(String str){
        Log.d(TAG,"serverSendData - data set to send "+ str);
        this.sendData = str;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"serverSend Data onHandleIntent");
        Log.d(TAG,"onHandleIntent -serverSocketConnect - " +serverSocketConnect);
        mSocket = serverSocketConnect.getServerSocket();
        Log.d(TAG,"server send data socket - " + mSocket);
        try {
            OutputStream outputStream = mSocket.getOutputStream();
            outputStream.write(sendData.getBytes());

            outputStream.close();
            dataSendComplete = true;
            Log.d(TAG,"Transmission on server side is done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.d(TAG,"ServerSendData closing Socket");
            serverSocketConnect.closeServerSocket();
        }
    }
}
