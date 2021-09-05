package com.example.tripawy;

import static com.example.tripawy.helper.HelperMethods.showAlertDialog;

import static com.example.tripawy.helper.HelperMethods.showAlertDialog;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.tripawy.helper.HelperMethods;

public class AlarmService extends Service {

    private MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        // play your music here
        mp = MediaPlayer.create(this.getApplicationContext(), R.raw.alarm);
        try{mp.start();
            showAlertDialog(this, this::stopSelf);}catch (Exception e){
            e.getMessage();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ((mp.isPlaying())) {
            mp.stop();
        }
        mp.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
