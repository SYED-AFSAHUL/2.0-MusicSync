package com.example.afsahulsyed.ms;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mylibrary_wifidirect.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "sMess";
    private TextView textView;
    static InitializeWifiDConn cInitializeWifiDConn;
    private static RecyclerView cRecyclerView;
    private static DeviceAdapter cAdapter;
    static List<WifiP2pDevice> deviceList = null;///new ArrayList<>();
    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConnectFragment() {
        Log.d(TAG,"ConnectFragment emp constructor");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectFragment newInstance(String param1, String param2) {
        Log.d(TAG,"ConnectFragment newInstance");
        ConnectFragment fragment = new ConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"connectFragment onCreate");
        if (getArguments() != null) {
            Log.d(TAG, "connectFragment getArguments() != null");
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        cInitializeWifiDConn = new InitializeWifiDConn(this.getContext());
        cInitializeWifiDConn.setWiFiOn();
        cInitializeWifiDConn.initConn();
        cInitializeWifiDConn.DiscoverPeer();
        CheckDeviceAvailability();
        Log.d(TAG,"connectFragment exiting onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"connect fragment onCreateView");
        return inflater.inflate(R.layout.connect_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "ConnectFragment onViewCreated");
        cRecyclerView = (RecyclerView) view.findViewById(R.id.R2id);
//        cRecyclerView.setVisibility(View.INVISIBLE);
        textView = (TextView)view.findViewById(R.id.textView5);
//        textView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "recycler 2- " + cRecyclerView);

        deviceList = cInitializeWifiDConn.getDeviceList();
        Log.d(TAG,"device list in fragment :-  "+ deviceList);
        updateUI();

        /*if (savedInstanceState == null){
            cAdapter = new DeviceAdapter(deviceList);
        }
        Log.d(TAG,"blank fragment mAdapter - " + cAdapter);
        Log.d(TAG,"blank fragment recycler 3- " + cRecyclerView);

        cRecyclerView.setAdapter(cAdapter);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        cAdapter.SetOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {}

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });*/
        Log.d(TAG,"ConnectFragment exiting onViewCreate");
    }

    public void CheckDeviceAvailability(){
        Log.d(TAG,"ConnectFragment CheckDeviceAvailability");
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // code to execute repeatedly
                try {
                    Log.d(TAG,"ConnectFragment inside run");
                    cInitializeWifiDConn.DiscoverPeer();
                    if(cInitializeWifiDConn.DeviceListUpdated){
                        deviceList = cInitializeWifiDConn.getDeviceList();
                        Log.d(TAG,"device in run -:  "+ deviceList);
                        cInitializeWifiDConn.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI();
                                if (WiFiDirectBroadCastReceiver.isConnected){
                                    deviceConnectedEvent();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
    public void updateUI(){
        Log.d(TAG,"ConnectFragment updateUI");
        while (cRecyclerView==null);
        Log.d(TAG,"connectFragment crossed while");
        if(deviceList.isEmpty()){
            Log.d(TAG,"connectFragment device list is empty");
            textView.setText("No device is in your range.");
            textView.setVisibility(View.VISIBLE);
        }else {
            Log.d(TAG,"connectFragment device list not empty");
            textView.setVisibility(View.GONE);
            cAdapter = new DeviceAdapter(deviceList);
            cRecyclerView.setAdapter(cAdapter);
            cRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            cRecyclerView.setVisibility(View.VISIBLE);
            cAdapter.SetOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    Log.d(TAG,"connectFragment OnItemClick");
                    cInitializeWifiDConn.ConnectToDevice(deviceList.get(position));
                }

                @Override
                public void OnItemLongClick(View view, int position) {
                    cInitializeWifiDConn.ConnectToDevice(deviceList.get(position));
                }
            });
        }
    }

    public void deviceConnectedEvent(){
        Log.d(TAG,"ConnectFragment deviceConnectedEvent");
        cRecyclerView.setVisibility(View.GONE);
        cAdapter = null;
        textView.setText("You are connected to a device");
        textView.setVisibility(View.VISIBLE);
        // cInitializeWifiDConn.StopDiscoverPeers();
        exec.shutdownNow();
        if(cInitializeWifiDConn.isGO()) {
            Log.d(TAG,"ConnectFragment deviceConnectEvent isGroupOwner");
            MusicPlayerFragment.delayMusic = 15000;     //10 sec delay
        }else{
            Log.d(TAG,"ConnectFragment deviceConnectEvent notGroupOwner");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"ConnectFragment deviceConnectEvent inside run checking receiving complete");
                    while (cInitializeWifiDConn.path1 == null){
                        Log.d(TAG,"ConnectFragment deviceConnectEvent inside while waiting for receive to complete");
                        SystemClock.sleep(100);
                    }
                    Log.d(TAG,"ConnectFragment deviceConnectEvent receive complete.");
                    new MusicPlayerFragment().playMusicForClient(cInitializeWifiDConn.path1,
                                                                     cInitializeWifiDConn.timeToStart1);
                    Log.d(TAG,"ConnectFragment deviceConnectEvent playMusicForClient triggered");
                }
            }).start();

        }
    }

    public static void broadcastMusicToClients(String path, MusicPlayerFragment mMusicPlayerFragment, long timeToStart){
        Log.d(TAG,"ConnectFragment broadcastMusicToClients " + path);
        Log.d(TAG,"ConnectFragment broadcastMusicToClients " + timeToStart);
        if(WiFiDirectBroadCastReceiver.isConnected){
            Log.d(TAG,"broadcastMusicToClients device is connected");
            cInitializeWifiDConn.BroadcastMusic(path,timeToStart);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"ConnectFragment onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"ConnectFragment onDetach");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
