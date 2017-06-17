package com.example.mylibrary_wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by afsahulsyed on 27/5/17.
 */

public class InitializeWifiDConn extends AppCompatActivity{

    /**
     * class members
     */
    private boolean mConnSuccess = false;
    private final String TAG = "sMess";
    private Context mContext;
    private WifiP2pInfo info;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private ServerSide mServerSide;
    private boolean WiFiDirectIsEnable = false;
    public Boolean DeviceListUpdated;
    private ClientSocketConnect clientSocketConnect;
    ScheduledThreadPoolExecutor checkSocketClientConnSch;// = new ScheduledThreadPoolExecutor(1);
    public Date timeToStart1 = null;
    public String path1 = null;

    public InitializeWifiDConn(){}

    public InitializeWifiDConn(Context context){
        mContext = context;
    }


    /**
     *
     * ------------------PUBLIC METHODS-----------------
     *
     *
     */

    /** 1
     * Initializes the device/app WiFi Direct Connection to be established
     *
     * @return boolean returns false if error occurred during initializing
     */
    public boolean initConn(){
        Log.d(TAG,"InitializeWifiDConn initConn");
        try {
            initIntentFilter();
            initReceiver();
            DeviceListUpdated = false;
            this.UnRegisterReceiver();
            this.RegisterReceiver();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        Toast.makeText(mContext.getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
        StopConnect();
        return true;
    }

    /** 2
     * Check if WiFi direct is enabled on the device
     *
     * @return true if enabled , false if not enabled
     */
    public boolean CheckWiFiDState(){
        if(WiFiDirectIsEnable){
            Log.d(TAG,"WiFiDirectEnabled--true");
            return true;
        }else{
            Log.d(TAG,"WiFiDirectEnabled--false");
            return false;
        }
    }

    /** 3
     * notify wifi direct status
     *
     *access modifier - default
     * @param WiFiDState- status
     */
    void SetWiFiDEnable(Boolean WiFiDState){
        this.WiFiDirectIsEnable = WiFiDState;
    }

    public void setWiFiOn(){
        Log.d(TAG,"set wifi on");
        //switch on wifi
        WifiManager wifiManager = (WifiManager)mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            Log.d(TAG,"wifi was off enabling wifi");
            wifiManager.setWifiEnabled(true);
            Log.d(TAG, String.valueOf(wifiManager.isWifiEnabled()));
        }
    }

    /**
     *
     *
     */
    public void DiscoverPeer(){
        Log.d(TAG,"discover peer.......");
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"peer Discover Successful");
            }
            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"peer Discover failed");
            }
        });
    }

    /*public boolean SelectDevice(){
        mDevice = (WifiP2pDevice) peers.get(0);
        ConnectToDevice();
        return mConnSuccess;
    }
*/
    /**
     * creates connection with device passed in parameter
     */
    public void ConnectToDevice(WifiP2pDevice mDevice){

        final WifiP2pConfig config = new WifiP2pConfig();
        //mDevice = (WifiP2pDevice) peers.get(0);
        Log.d(TAG,"inside connect/n connecting to "+ mDevice.deviceName);
        config.deviceAddress = mDevice.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"Success");
                ConnectSuccess(true);
            }
            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"Failure" + reason);
                ConnectSuccess(false);
            }
        });//connect method ends

        Log.d(TAG,"exiting CreateConnection");
    }


    public void SendData(String data){
        Log.d(TAG,"sendData");

        Intent serviceIntent = new Intent(mContext, ClientSide.class);
        serviceIntent.setAction(ClientSide.ACTION_SEND_FILE);
        serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());

        Log.d(TAG, "ownership is " + info.groupOwnerAddress.getHostAddress());

        serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_PORT, 8888);
        serviceIntent.putExtra(ClientSide.EXTRAS_DATA, data);
        serviceIntent.putExtra(ClientSide.EXTRAS_DATA_TYPE,"data");
        mContext.startService(serviceIntent);
    }

    public void SendPicture(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("images/*");
        startActivityForResult(intent, 20);
    }

    public void SendAudio(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult");
        Intent serviceIntent = new Intent(mContext, ClientSide.class);

        Uri uri = data.getData();
        Log.d(TAG,"uri - "+ uri);
        serviceIntent.putExtra(ClientSide.EXTRAS_FILE_PATH, uri.toString());

        serviceIntent.setAction(ClientSide.ACTION_SEND_FILE);
        serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());


        serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_PORT, 8988);
        serviceIntent.putExtra(ClientSide.EXTRAS_DATA_TYPE,"audio");

        ClientSide.getContext(mContext);
        mContext.startService(serviceIntent);
    }

    public void ReceiveData(){
        if(info.groupFormed && info.isGroupOwner) {

            mServerSide = new ServerSide(mContext);
            mServerSide.SetDetails("audio",".mp3");
            mServerSide.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void UnRegisterReceiver(){
        try {
            mContext.unregisterReceiver(mReceiver);
            Log.d(TAG, "unregisterReceiver(mReceiver)");
        }catch (Exception e){e.printStackTrace();}
    }

    public void RegisterReceiver(){
        UnRegisterReceiver();
        mContext.registerReceiver(mReceiver, mIntentFilter);
        Log.d(TAG,"RegisterReceiver");
    }

    public void StopDiscoverPeers() {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    public void StopConnect() {
        //if() {
        Log.d(TAG,"InitializeWiFiDConn StopConnect");
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG,"StopConnect success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "failed stop connection - " + reason);
                }
            });
        //}
    }

    public void BeGroupOwener() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    //public WifiP2pDevice getConnectedDevice(){
    //    return null;
    //}

public void BroadcastMusic(String path, long timeToStart){
    Log.d(TAG,"InitializeWiFiDConn Broadcast music");
    if(info.groupFormed && info.isGroupOwner){    //server -send music
        Log.d(TAG,"InitializeWiFiDConn info.groupFormed && info.isGroupOwner");
        final ServerSocketConnect serverSocketConnect = new ServerSocketConnect();

        Log.d(TAG,"waiting for async task to finish...."+info.groupOwnerAddress.getHostAddress());
      /*  while (!serverSocketConnect.getStatus().equals(AsyncTask.Status.FINISHED)){
            SystemClock.sleep(500);
            Log.d(TAG,"8888 inside while");
            Log.d(TAG,serverSocketConnect.getStatus() + "-----+----"+ AsyncTask.Status.FINISHED);
            Log.d(TAG,"while condition - " + !serverSocketConnect.getStatus().equals(AsyncTask.Status.FINISHED));
        }*/
      Log.d(TAG,"InitializeWiFiDConn before while pending 8888");
       // while (serverSocketConnect.getStatus().equals(AsyncTask.Status.PENDING));
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                serverSocketConnect.initSocket(8888);
            }
        });
        t1.setPriority(4);
        t1.start();
        try {
            t1.join();
        }catch (Exception e){
            e.printStackTrace();
        }

       // SystemClock.sleep(1000);
        try {
            Log.d(TAG, serverSocketConnect + " ----==-==--=- " + serverSocketConnect.mSocket);
            Log.d(TAG,"InitializeWiFiDConn going inside while till socket for 8888 is connected");
            while (serverSocketConnect.mSocket == null) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG,"canceling async task.....");
        serverSocketConnect.cancel(true);
        Log.d(TAG,"async task finished.");

        final ServerSendData serverSendData = new ServerSendData("mainbb", serverSocketConnect);

        final Intent serviceIntent = new Intent(mContext, ServerSendData.class);
        serviceIntent.setAction(ServerSocketConnect.ACTION_SEND_FILE);

        Log.d(TAG, "ownership is " + info.groupOwnerAddress.getHostAddress());

        Log.d(TAG,"time is --- " + timeToStart +"\n sending data");

        serverSendData.setDataToSend("qwerty" + timeToStart + "qwerty"+ "fmt");

        //serviceIntent.putExtra("ss",serverSocketConnect);
        Log.d(TAG,"InitializeWiFiDConn starting thread to send data");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"InitializeWiFiDConn (start data Send service) inside run");
        //        mContext.startService(serviceIntent);
                serverSendData.onHandleIntent(serviceIntent);
            }
        }).start();

        //SystemClock.sleep(1000);
        while (!serverSendData.dataSendComplete){
            Log.d(TAG,"InitializeWiFiDConn data send not complete inside while");
            SystemClock.sleep(500);
        }
        Log.d(TAG,"InitializeWiFiDConn data send complete");

        /**
         *  audio transmission start
         **/

        Log.d(TAG,"InitializeWiFiDConn init socket with 8988 for audio");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"InitializeWiFiDConn init socket 8988");
                serverSocketConnect.initSocket(8988);
            }
        });
        t2.setPriority(3);
        t2.start();

        Log.d(TAG,"InitializeWiFiDConn waiting for connection 8988");
        while (serverSocketConnect.mSocket == null){
            Log.d(TAG,"InitializeWiFiDConn waiting for socket to connect inside while");
            SystemClock.sleep(500);
        }

        Log.d(TAG,"InitializeWiFiDConn socket for 8988 connected");
        final ServerSendMedia serverSendMedia = new ServerSendMedia("main",serverSocketConnect,path, mContext);

        final Intent serviceIntent2 = new Intent(mContext, ServerSendMedia.class);
        serviceIntent2.setAction(ServerSocketConnect.ACTION_SEND_FILE);

        Log.d(TAG, "8988 ownership is " + info.groupOwnerAddress.getHostAddress());
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"InitializeWiFiDConn sending audio starting..");
                serverSendMedia.onHandleIntent(serviceIntent2);
            }
        });
        t3.setPriority(3);
        t3.start();

        //serverSocketConnect.closeServerSocket();
        Log.d(TAG,"InitializeWiFiDConn exiting Broadcast music");

    }else if(info.groupFormed){                   //client-receive music
        //do nothing
        Log.d(TAG,"Broadcast music -client-- do nothing");
    }
}
    public void ReceiveMusicClient1(){
        Log.d(TAG,"InitializeWiFiDConn ReceiveMusicClient");
        try {
            clientSocketConnect = new ClientSocketConnect();
            clientSocketConnect.initSocket(info.groupOwnerAddress.getHostAddress(), 8888);

           // final ScheduledThreadPoolExecutor exe1y = new ScheduledThreadPoolExecutor(1);
            checkSocketClientConnSch = new ScheduledThreadPoolExecutor(1);
            checkSocketClientConnSch.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.d(TAG,"checkSocketClientConnSch running - to check if socket is connected for 8888");
                    if(clientSocketConnect.socket!=null && clientSocketConnect.socket.isConnected()){
                        Log.d(TAG,"exe1 socket connected");
                        ReceiveMusicClient2();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);


        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
    }

    public void ReceiveMusicClient2(){
        Log.d(TAG,"ReceiveMusicClient2");
        checkSocketClientConnSch.shutdownNow();
        clientSocketConnect.SocketConnected = false;
        final ClientReceiveData clientReceiveData = new ClientReceiveData(clientSocketConnect);
        //String s1 = clientReceiveData.doInBackground();
        //Log.d(TAG,"received data in initcomm - "+ s1);
        Log.d(TAG,"InitializeWiFiDConn - starting receive data thread");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"inside ReceiveMucicClient2 run to receive data");
                clientReceiveData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        t2.setPriority(4);
        t2.start();
        try {
            Log.d(TAG,"waiting to join t2");
//            t2.join();
        }catch (Exception e){
                e.printStackTrace();
        }

       // while(!clientReceiveData.getStatus().equals(AsyncTask.Status.FINISHED)){
         //   SystemClock.sleep(500);
           // Log.d(TAG,"data receive inside while");
       // }
        Log.d(TAG,"InitializeWiFiDConn waiting to receive data");
        while(clientReceiveData.str==null){
            SystemClock.sleep(100);
            Log.d(TAG,"InitializeWiFiDConn inside while waiting for data");
            Log.d(TAG,"str - " + clientReceiveData.str);
        }
        Log.d(TAG,"InitializeWiFiDConn data receive complete");
        String sData = clientReceiveData.getData();
        Log.d(TAG,"data "+ sData);
       // Toast.makeText(mContext,"data - " +s1,Toast.LENGTH_LONG).show();
        //clientSocketConnect.closeSocket();

        String[] words = sData.split("qwerty");
        for(String s:words){
            Log.d(TAG,"word - " + s);
        }

        if(words!=null) {
            Log.d(TAG,"words!=null");
            long ln = Long.parseLong(words[1]);
            timeToStart1 = new Date(ln);
            Log.d(TAG,"time - "+ ln + " ----iii---- "+ timeToStart1);
        }
        //audio receive
        Log.d(TAG,"InitializeWiFiDConn init socket for audio");
        clientSocketConnect.initSocket(info.groupOwnerAddress.getHostAddress(), 8988);

        try {
            checkSocketClientConnSch = new ScheduledThreadPoolExecutor(1);
            checkSocketClientConnSch.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.d(TAG, "InitializeWiFiDConn running to check if socket is connected 8988");
                    if (clientSocketConnect.socket!=null && clientSocketConnect.socket.isConnected()) {
                        Log.d(TAG,"InitializeWiFiDConn socket connected 8988");
                        ReceiveMusicClient3();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ReceiveMusicClient3(){
        checkSocketClientConnSch.shutdownNow();
        Log.d(TAG,"ReceiveMusicClient3");

        ClientReceiveMedia clientReceiveMedia = new ClientReceiveMedia(clientSocketConnect, ".mp3",mContext);
        //String s2 = clientReceiveMedia.doInBackground();
        //Log.d(TAG,"path received - " + s2);
        Log.d(TAG,"InitializeWiFiDConn starting receiving media....");
        clientReceiveMedia.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //clientSocketConnect.closeSocket();
        while (!clientReceiveMedia.audioTransferComplete){
            Log.d(TAG,"InitializeWiFiDConn inside while media receive in process");
            Log.d(TAG,"while condition - " + !clientReceiveMedia.audioTransferComplete);
            SystemClock.sleep(500);
        }
        Log.d(TAG, "audio transmission complete on client side");
        path1 = clientReceiveMedia.getPathData();
        Log.d(TAG,"path is -- "+ path1);
    }

    public boolean isGO(){
        if(info != null) {
            if (info.groupFormed && info.isGroupOwner)
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     *
     * -------------------PRIVATE METHODS---------------------
     *
     *
     **/

    /**
     * Initializes intent filter
     **/
    private void initIntentFilter() {
        Log.d(TAG,"inside initIntentFilter");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


    private void initReceiver() {
        Log.d(TAG,"initReceiver");
        mManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        Log.d(TAG,"mManager - " + mManager);
        mChannel = mManager.initialize(mContext, mContext.getMainLooper(), null);

        WifiP2pManager.PeerListListener mPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                try {
                    Log.d(TAG, "OnPeersAvailable");
                    if (!peers.isEmpty())
                        peers.clear();

                    Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                    peers.addAll(aList);

                    Log.d(TAG, "Device List :- ");
                    for (int i = 0; i < aList.size(); i++) {
                        WifiP2pDevice a = peers.get(i);
                        Log.d(TAG, "device name -; " + a.deviceName + "  address -: " + a.deviceAddress);
                    }
                    DeviceListUpdated = true;
                    Log.d(TAG,"exiting onPeerListener");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo mInfo) {

                Log.d(TAG, "InfoAvailable is on");

                info = mInfo;
                //TextView view = (TextView) findViewById(R.id.textView);
                if (info.groupFormed && info.isGroupOwner) {       //server
                    Log.d(TAG, "init receive - Group formed and you are the owner");

                   // mServerSide = new ServerSide(InitializeWifiDConn.this);
                    //mServerSide.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    // mDataTask = new ServerService(getApplicationContext(), view);
                    // mDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else if (info.groupFormed) {        //client
                    Log.d(TAG,"initReceive - group Formed & you are not the owner");
//                    clientSocketConnect = new ClientSocketConnect();
//
//                    clientSocketConnect.initSocket(info.groupOwnerAddress.getHostAddress(),8888);
                  //  while(WiFiDirectBroadCastReceiver.isConnected){
                        ReceiveMusicClient1();
                   // }
                }
            }
        };

        mReceiver = new WiFiDirectBroadCastReceiver(mManager, mChannel,mPeerListListener,
                                                            mInfoListener,this,mContext);
    }

    /*public boolean DeviceListisUpdated(){
        return DeviceListUpdated;
    }*/
    public List<WifiP2pDevice> getDeviceList(){
        DeviceListUpdated = false;
        return peers;
    }

    private void ConnectSuccess(boolean state){
        mConnSuccess = state;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*Log.d(TAG, "onActivityResult");
        Intent serviceIntent = new Intent(mContext, ClientSide.class);

        Uri uri = data.getData();
        Log.d(TAG,"uri - "+ uri);
        serviceIntent.putExtra(ClientSide.EXTRAS_FILE_PATH, uri.toString());

        serviceIntent.setAction(ClientSide.ACTION_SEND_FILE);
        serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());

        if (requestCode == 20) {
            serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_PORT, 8888);
            serviceIntent.putExtra(ClientSide.EXTRAS_DATA_TYPE,"image");
        }else if(requestCode == 30){
            serviceIntent.putExtra(ClientSide.EXTRAS_GROUP_OWNER_PORT, 8988);
            serviceIntent.putExtra(ClientSide.EXTRAS_DATA_TYPE,"audio");
        }
        mContext.startService(serviceIntent);*/
    }

    @Override
    protected void onStop()
    {
        this.UnRegisterReceiver();
        this.RegisterReceiver();
        super.onStop();
        Log.d(TAG,"InitializeWiFiDonn onStop");
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        this.UnRegisterReceiver();
        this.RegisterReceiver();
        Log.d(TAG,"InitializeWiFiDonn onResume");
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        this.UnRegisterReceiver();
        Log.d(TAG,"InitializeWiFiDonn onPause");
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.UnRegisterReceiver();
        Log.d(TAG,"InitializeWiFiDonn onDestroy");
    }
}
