package com.example.afsahulsyed.ms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicPlayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "sMess";
    private RecyclerView mRecyclerView;
    private static MusicAdapter mAdapter;
    static String audioLocation[];
    static MediaPlayer mediaPlayer = new MediaPlayer();
    List<Music> musicList = new ArrayList<>();
    public static long delayMusic = 0;
    private static Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MusicPlayerFragment() {
        Log.d(TAG,"MusicPlayerFragment constructor");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicPlayerFragment newInstance(String param1, String param2) {
        Log.d(TAG,"blank fragment newInstance");
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"blank fragment onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        String tv1Text = "----******----";
        if(savedInstanceState!=null) {
            tv1Text = savedInstanceState.getString("tv1");
        }
        Log.d(TAG,"tv1Text =->  "+tv1Text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"blank fragment onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_player_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Log.d(TAG,"blank fragment onButtonPressed");
        if (mListener != null) {
            Log.d(TAG,"blank fragment mListener != null");
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "blank fragment onViewCreated");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.aaaa);
        Log.d(TAG, "recycler 2- " + mRecyclerView);
        if (savedInstanceState == null){
            mAdapter = new MusicAdapter(musicList);
            initMusic();
        }
        Log.d(TAG,"blank fragment mAdapter - " + mAdapter);
        Log.d(TAG,"blank fragment recycler 3- " + mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        mAdapter.SetOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                playMusic(audioLocation[position]);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        Log.d(TAG,"blank fragment exiting onViewCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"blank fragment onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG,"onDetach blank fragment");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG,"onSaveInstanceState");
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("tv1", "yoooo yoooo");


        super.onSaveInstanceState(savedInstanceState);
    }

//onRestoreInstanceState

/*    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        Log.d(TAG,"onRestoreInstanceState");
//        tv1Text = savedInstanceState.getString("tv1");
    }*/
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

    private void initMusic(){
        Log.d(TAG,"blank fragment initMusic");
        int i=0;
        Cursor cur;
        int count;

        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String[] projection = {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        cur = cr.query(uri, projection, selection, null, sortOrder);

        try {
            if (cur != null) {
                count = cur.getCount();
                audioLocation = new String[count];
                Music mMusic;
                if (count > 0) {
                    cur.moveToNext();
                    do {
                        Log.i(TAG, "\n*****TRACK****" + i);
                        String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));

                        Log.i(TAG, "data -:" + artist);
                        Log.i(TAG, "title -:" + title);
                        Log.i(TAG,"duration - " + duration);

                        audioLocation[i++] = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));

                        mMusic = new Music(title, artist, duration);
                        musicList.add(mMusic);
                    } while (cur.moveToNext());

                }
                Log.d(TAG,"blank fragment done with music");
            }
            if(cur!=null)
                cur.close();

        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void playMusic(final String location){

        long currTime = System.currentTimeMillis();
        final long longTimeToStart = currTime + delayMusic;
        Log.d(TAG,"delay is "+ delayMusic);
        final Date timeToStart = new Date(longTimeToStart);
        Timer timer = new Timer();

        Thread tt = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"MusicPlayerFragnent play music run to transmit music");
                ConnectFragment.broadcastMusicToClients(location,new MusicPlayerFragment(),longTimeToStart);
            }
        });
        tt.setPriority(2);
        tt.start();
        Log.d(TAG,"blank fragment play Music");

        /*try{
            Log.d(TAG,"server side MusicPlayerFragment preparing music to play");
            mediaPlayer.reset();
            Uri myUri = Uri.parse(location);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), myUri);
            Log.d(TAG,"1111");
            mediaPlayer.prepare();
            Log.d(TAG,"222222");

        }catch (Exception e){
            e.printStackTrace();
        }*/

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    try{
                        Log.d(TAG,"server side MusicPlayerFragment preparing music to play");
                        mediaPlayer.reset();
                        Uri myUri = Uri.parse(location);

                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(getActivity().getApplicationContext(), myUri);
                        Log.d(TAG,"1111");
                        mediaPlayer.prepare();
                        Log.d(TAG,"222222");

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d(TAG,"MusicPlayerFragment server side start to play music");
                    mediaPlayer.start();
                    Log.d(TAG,"music should be playing");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, timeToStart);
        if(delayMusic!=0){
            Toast.makeText(getContext(),"Your Music will start to play in " + delayMusic/1000+ "seconds",Toast.LENGTH_LONG).show();
            /*Snackbar.make(new View(), "Your Music will start to play in " + delayMusic/1000+ "seconds", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        }
    }

    public void playMusicForClient(final String path, Date timeToStart){
        Log.d(TAG,"MusicPlayerFragment playMusicForClient");
        Timer timer = new Timer();

        /*try{
            Log.d(TAG,"Setting music to play");
            mediaPlayer.reset();
            Uri myUri = Uri.parse(path);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.d(TAG,mediaPlayer+ " ; " + mContext+ " ; " + myUri);
            mediaPlayer.setDataSource(mContext, myUri);
            Log.d(TAG,"1111");
            mediaPlayer.prepare();
            Log.d(TAG,"222222");

        }catch (Exception e){
            e.printStackTrace();
        }*/

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    try{
                        Log.d(TAG,"Setting music to play");
                        mediaPlayer.reset();
                        Uri myUri = Uri.parse(path);

                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        Log.d(TAG,mediaPlayer+ " ; " + mContext+ " ; " + myUri);
                        mediaPlayer.setDataSource(mContext, myUri);
                        Log.d(TAG,"1111");
                        mediaPlayer.prepare();
                        Log.d(TAG,"222222");

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d(TAG,"starting to play music");
                    mediaPlayer.start();
                    Log.d(TAG,"music should be playing");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, timeToStart);
    }

    public void stopMusic(){
        Log.d(TAG,"stop music");
        mediaPlayer.reset();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"blank fragment on Pause");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"blank fragment onDestroy");
        //mediaPlayer=null;
        //audioLocation  = null;
    }
}
