package com.example.tripawy.broad_cast_reciever;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.tripawy.R;
import com.example.tripawy.RoomDB;
import com.example.tripawy.Trip;
import com.example.tripawy.methods.Methods;

import java.util.concurrent.Executors;

public class MyReceiver extends BroadcastReceiver {

    private Trip trip;
    private MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        mp = MediaPlayer.create(context.getApplicationContext(), R.raw.alarm);
        try {
            mp.start();
            showAlertDialog(context);
        } catch (Exception e) {
        }

        //Getting Trip Instance
        Bundle args = intent.getBundleExtra("Trip");
        trip = (Trip) args.getSerializable("TripObj");

    }


    //Alarm Alert Dialog
    public void showAlertDialog(Context context) {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("TRIPAWY")
                .setCancelable(false)
                .setMessage("Reminder for your trip !!!")
                .setPositiveButton("start", new DialogInterface.OnClickListener() {
                    @SuppressLint("QueryPermissionsNeeded")
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO  intent to go to google maps
                        mp.stop();
                        trip.setTripState("DONE");
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(context.getApplicationContext()).update(trip);
                        });
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + trip.getTo()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mp.stop();
                        trip.setTripState("CANCELED");
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(context.getApplicationContext()).update(trip);
                        });

                    }
                }).setNeutralButton("snooze", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mp.stop();
                        long seconds = 1000;
                        Methods.startService(context.getApplicationContext(), trip);
                        Methods.startScheduling(context, trip, seconds);

                        dialog.dismiss();
                    }
                }).create();
        alertDialog.getWindow().setType(LAYOUT_FLAG);
        alertDialog.show();
    }

}






