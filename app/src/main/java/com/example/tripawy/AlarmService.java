package com.example.tripawy;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tripawy.helper.HelperMethods;

import java.util.concurrent.Executors;

public class AlarmService extends Service {
    public static Trip trip;
    private MediaPlayer mp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // play your music here
        mp = MediaPlayer.create(this.getApplicationContext(), R.raw.alarm);
        try {
            mp.start();
            showAlertDialog(this, this::stopSelf);
        } catch (Exception e) {
        }


    }

    public void showAlertDialog(Context context, HelperMethods.OnButton onButton) {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("TRIPAWY")
                .setCancelable(false)
                .setMessage("Reminder for your trip!!!")
                .setPositiveButton("start", new DialogInterface.OnClickListener() {
                    @SuppressLint("QueryPermissionsNeeded")
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO  intent to go to google maps
                        trip.setTripState("DONE");
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(getApplicationContext()).update(trip);
                        });
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=mansoura"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                        // }
                        onButton.onClicked();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        trip.setTripState("CANCELED");
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(getApplicationContext()).update(trip);
                        });
                        onButton.onClicked();
                    }
                }).setNeutralButton("snooze", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        long seconds = 1000;
                        HelperMethods.startService(context.getApplicationContext(), trip);
                        HelperMethods.startScheduling(context,trip,seconds);
                        onButton.onClicked();

                        dialog.dismiss();
                        onButton.onClicked();
                    }
                }).create();
        alertDialog.getWindow().setType(LAYOUT_FLAG);
        alertDialog.show();
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
