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

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

public class AddNewTripActivity extends AppCompatActivity {

    private Button btn_datePicker;
    private Button btn_timePicker;
    private Button btn_add;
    private ImageButton btn_close;
    private EditText editTxtTripName;
    private EditText editTxtStartPoint;
    private EditText editTxtEndPoint;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonType;
    private String tripType;

    private int year, month, dayOfMonth, hour, minute;
    private Calendar c, cDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        getSupportActionBar().hide();

        initializeComponent();

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        updateTime(calendar);
        updateDate(calendar);


    }

    private void initializeComponent() {
        btn_datePicker = findViewById(R.id.btnDatePicker);
        btn_timePicker = findViewById(R.id.btnTimePicker);
        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtStartPoint = findViewById(R.id.editTxtStartPoint);
        editTxtEndPoint = findViewById(R.id.editTxtEndPoint);
        radioGroupType = findViewById(R.id.radioGroupType);
        btn_add = findViewById(R.id.btn_add);
        btn_close = findViewById(R.id.btn_close);
    }

    public void pickDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        cDate = Calendar.getInstance();
                        cDate.set(Calendar.YEAR, year);
                        cDate.set(Calendar.MONTH, month);
                        cDate.set(Calendar.DAY_OF_MONTH, day);
                        updateDate(cDate);

                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void pickTime(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                        updateTime(c);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    private void updateDate(Calendar cDate) {
        String dateString = DateFormat.getDateInstance().format(cDate.getTime());
        btn_datePicker.setText(dateString);
    }

    private void updateTime(Calendar c) {
        String timeString = DateFormat.getTimeInstance().format(c.getTime());
        btn_timePicker.setText(timeString);
    }

    public void add(View v) {


        if (!checkTripComponents()) {
            ArrayList<String> notes = new ArrayList<>();
            // to insert item
            Executors.newSingleThreadExecutor().execute(() -> {
                RoomDB.getTrips(getApplication()).insert(

                        new Trip(
                                editTxtTripName.getText().toString(),
                                cDate.getTimeInMillis(),
                                c.getTimeInMillis(),
                                TripState.UPCOMING.name(),
                                tripType,
                                editTxtStartPoint.getText().toString(),
                                editTxtEndPoint.getText().toString(),
                                notes
                        )
                );
            });
            finish();
        }
    }

    public void close(View v) {
        finish();
    }

    //make a method that check if components are empty
    private boolean checkTripComponents() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        radioButtonType = (RadioButton) findViewById(selectedId);
        if (editTxtTripName.getText().toString().isEmpty()
                || editTxtStartPoint.getText().toString().isEmpty()
                || editTxtEndPoint.getText().toString().isEmpty() || radioButtonType == null) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return true;
        }
        setType();
        return false;
    }

    private void setType() {
        if(radioButtonType.getId() == R.id.oneWay){
            tripType=TripType.ONE_WAY.name();
        }else{
            tripType=TripType.ROUND.name();
        }
    }

}