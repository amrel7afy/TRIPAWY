package com.example.tripawy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

public class EditTripActivity extends AppCompatActivity {

    private Button btn_datePicker;
    private Button btn_timePicker;
    private Button btn_save;
    private ImageButton btn_close;
    private EditText editTxtTripName;
    private EditText editTxtStartPoint;
    private EditText editTxtEndPoint;
    private RadioGroup radioGroupRepeat;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonRepeat;
    private RadioButton radioButtonType;

    private long dateLong, timeLong;
    String timeString = null;
    String dateString = null;
    private Trip trip;
    private int year, month, dayOfMonth, hour, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        getSupportActionBar().hide();
        initializeComponent();

        if (getIntent().getExtras() != null) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        }

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        editTxtTripName.setText(trip.getName());
        editTxtStartPoint.setText(trip.getFrom());
        editTxtEndPoint.setText(trip.getTo());

        btn_datePicker.setText(convertDate(trip.getDate()));
        btn_timePicker.setText(convertTime(trip.getTime()));

    }

    private void initializeComponent() {
        btn_datePicker = findViewById(R.id.btnDatePicker);
        btn_timePicker = findViewById(R.id.btnTimePicker);
        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtStartPoint = findViewById(R.id.editTxtStartPoint);
        editTxtEndPoint = findViewById(R.id.editTxtEndPoint);
        radioGroupRepeat = findViewById(R.id.radioGroupRepeat);
        radioGroupType = findViewById(R.id.radioGroupType);
        btn_save = findViewById(R.id.btn_save);
        btn_close = findViewById(R.id.btn_close);
    }

    public void pickDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        updateDate(year, month, day);

                    }
                },year,month,dayOfMonth );
        datePickerDialog.show();
    }

    public void pickTime(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        updateTime(hourOfDay, minute);
                    }
                }, 0, 0, false);

        timePickerDialog.show();
    }

    private void updateDate(int year, int month, int day) {
        String years = String.valueOf(year);
        String months = "";
        String days = "";

        if ((month + 1) >= 10) {
            months = String.valueOf(month + 1);
        } else {
            months = "0" + (month + 1);
        }

        if (day >= 10) {
            days = String.valueOf(day);
        } else {
            days = "0" + day;
        }

        try {
            dateString = days + "/" + months + "/" + years;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateString);

            dateLong = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        btn_datePicker.setText(dateString);
    }

    private void updateTime(int hour, int minute) {
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String minutes = "";
        if (minute < 10) {
            minutes = "0" + minute;
        } else {
            minutes = String.valueOf(minute);
        }

        //timeString
        try {
            timeString = hour + ":" + minutes + " " + timeSet;
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

            Date date = sdf.parse(timeString);

            timeLong = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        btn_timePicker.setText(timeString);

    }

    //Convert Date From Long To String
    private String convertDate(Long dateLong) {
        Date date = new Date(dateLong);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateText = sdf.format(date);
        return dateText;
    }

    //Convert Time From Long To String
    private String convertTime(Long timeLong) {
        Date date = new Date(timeLong);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String timeText = sdf.format(date);
        return timeText;
    }

    public void editsave(View v) {
        if(!checktripcomponents()){
            trip.setName(editTxtTripName.getText().toString());
            trip.setFrom(editTxtStartPoint.getText().toString());
            trip.setTo(editTxtEndPoint.getText().toString());
            trip.setDate(dateLong);
            trip.setTime(timeLong);

            Executors.newSingleThreadExecutor().execute(() -> {
                RoomDB.getTrips(getApplication()).update(trip);
            });
            finish();}
    }


    public void close(View v) {
        finish();
    }

    public boolean checktripcomponents(){
        if(editTxtTripName.getText().toString().isEmpty()
                ||editTxtStartPoint.getText().toString().isEmpty()
                || editTxtEndPoint.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please fill all fields",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


}