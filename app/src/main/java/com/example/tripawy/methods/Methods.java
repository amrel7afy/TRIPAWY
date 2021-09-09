package com.example.tripawy.methods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.tripawy.Trip;
import com.example.tripawy.broad_cast_reciever.MyReceiver;
import com.example.tripawy.pinnednotificatoin.Notification;

public class Methods extends Activity {

    public static void startScheduling(Context context, Trip data, long snoozeSeconds) {
        long time = data.getTime();
        long timeInSec = 0;


        if (snoozeSeconds == 0) {
            timeInSec = time - System.currentTimeMillis();
        }
        if (timeInSec >= 0) {
            Intent intent = new Intent(context, MyReceiver.class);
            Bundle args = new Bundle();
            args.putSerializable("TripObj", data);
            intent.putExtra("Trip", args);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(), data.getId(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInSec + snoozeSeconds, pendingIntent);
            Toast.makeText(context, "Alarm set to after " + timeInSec + " seconds", Toast.LENGTH_LONG).show();
        }

    }

    public static void startService(Context context, Trip trip) {
        Intent serviceIntent = new Intent(context, Notification.class);
        serviceIntent.putExtra("inputExtra", "You are waiting for trip  " + trip.getName() + "");
        ContextCompat.startForegroundService(context, serviceIntent);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //NETWORK
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public static void openWifiSettings(Context context) {

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    //Can Draw Overlays
    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context.getApplicationContext());
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void openDrawSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getApplicationContext().getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
