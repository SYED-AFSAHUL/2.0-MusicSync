package com.example.afsahulsyed.ms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by afsahulsyed on 10/6/17.
 */

public class LastCallClass extends Service {

    private final String TAG = "sMess";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.d(TAG,"onTaskRemoved");
        MusicPlayerFragment.audioLocation = null;
        //MusicPlayerFragment.mediaPlayer.reset();
        MusicPlayerFragment.mediaPlayer.release();
        MusicPlayerFragment.mediaPlayer= null;
        super.onTaskRemoved(rootIntent);
    }
}
