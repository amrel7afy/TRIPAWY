package com.example.tripawy;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.tripawy.helper.HelperMethods;

import java.util.concurrent.Executors;

public class AlarmService extends Service {
    Trip trip;
    private MediaPlayer mp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //trip= (Trip) intent.getSerializableExtra("Trip");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // play your music here
        mp = MediaPlayer.create(this.getApplicationContext(), R.raw.alarm);
        try{mp.start();
            showAlertDialog(this, this::stopSelf);}catch (Exception e){
        }



    }
    public  void showAlertDialog(Context context, HelperMethods.OnButton onButton) {
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
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO  intent to go to google maps
                        Uri gmmIntentUri = Uri.parse("geo:12.22222,22.22222");
                        //Intent intent =new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                        //intent.setPackage("com.google.android.apps.maps");
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=mansoura"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        onButton.onClicked();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*trip.setTripState("CANCLED");
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(getApplicationContext()).update(trip);
                        });*/
                        dialog.dismiss();
                        onButton.onClicked();
                    }
                }).setNeutralButton("snooze", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

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
