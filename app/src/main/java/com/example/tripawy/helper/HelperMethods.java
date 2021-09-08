package com.example.tripawy.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tripawy.AlarmService;
import com.example.tripawy.Trip;
import com.example.tripawy.pinnednotificatoin.Notification;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HelperMethods extends Activity {
    Context context;
    public static String ACTION_PENDING = "action";
    int LOCATION_ACCESS_CODE = 1000;
    FusedLocationProviderClient fusedLocationProviderClient;
    static Double lat, longs;
    static String stringLat, stringLong;

    public static void showCalender(Context context,
                                    String title, final TextView text_view_data, final DateModel data1) {

        DatePickerDialog mDatePicker = new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat mFormat = new DecimalFormat("00", symbols);
                String data = selectedYear + "-" + String.format(new Locale("en"), mFormat.format(Double.valueOf((selectedMonth + 1)))) + "-"
                        + mFormat.format(Double.valueOf(selectedDay));
                data1.setDateTxt(data);
                data1.setDay(mFormat.format(Double.valueOf(selectedDay)));
                data1.setMonth(mFormat.format(Double.valueOf(selectedMonth + 1)));
                data1.setYear(String.valueOf(selectedYear));
                if (text_view_data != null) {
                    text_view_data.setText(data);
                }
            }
        }, Integer.parseInt(data1.getYear()), Integer.parseInt(data1.getMonth()) - 1, Integer.parseInt(data1.getDay()));
        mDatePicker.setTitle(title);
        mDatePicker.show();
    }

    public static void showTimePicker(Context context) {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                Date date = new Date();
                date.setHours(selectedHour);
                date.setMinutes(selectedMinute);
                date.setSeconds(0);

            }
        }, 5,
                50,
                false); //Yes 24 hour time
        mTimePicker.setTitle("select time");
        mTimePicker.show();
    }

    public static void setSpinner(Activity activity, Spinner spinner, List<String> names) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, names);

        spinner.setAdapter(adapter);
    }

    public static void startScheduling(Context context, Trip data,long snoozeSeconds) {
        long time = data.getTime();
        long timeInSec = 0;


         if(snoozeSeconds==0) {
             timeInSec = time - System.currentTimeMillis();
             AlarmService.trip = data;
         }
         if(timeInSec >=0){
             Intent intent = new Intent(context, AlarmService.class);
             PendingIntent pendingIntent = PendingIntent.getService(
                     context.getApplicationContext(), 234, intent, 0);
             AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
             alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInSec+snoozeSeconds, pendingIntent);
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

    public interface OnButton {
        void onClicked();
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    longs = location.getLongitude();
                    stringLat = Double.toString(lat);
                    stringLong = Double.toString(longs);

                }
            }
        });
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("MianActivity", "askLocationPermission:");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , LOCATION_ACCESS_CODE);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , LOCATION_ACCESS_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_ACCESS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }




    //NETWORK
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void openWifiSettings(Context context){

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity( intent);
    }

}
