package com.example.tripawy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executors;

public class AddNewTripActivity extends AppCompatActivity {

    private Button btn_datePicker;
    private Button btn_timePicker;
    private EditText editTxtTripName;
    private EditText editTxtStartPoint;
    private EditText editTxtEndPoint;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonType;
    private String tripType;

    private int year, month, dayOfMonth, hour, minute;
    private static Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initializeComponent();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

    }


    private void initializeComponent() {
        btn_datePicker = findViewById(R.id.btnDatePicker);
        btn_timePicker = findViewById(R.id.btnTimePicker);
        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtStartPoint = findViewById(R.id.editTxtStartPoint);
        editTxtEndPoint = findViewById(R.id.editTxtEndPoint);
        radioGroupType = findViewById(R.id.radioGroupType);
    }

    public void pickDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    updateDate(calendar);

                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void pickTime(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    updateTime(calendar);
                }, hour, minute, false);

        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateDate(Calendar cDate) {
        String dateString = DateFormat.getDateInstance().format(cDate.getTime());
        btn_datePicker.setText("Date\n" + dateString);
    }

    @SuppressLint("SetTextI18n")
    private void updateTime(Calendar c) {
        String timeString = DateFormat.getTimeInstance().format(c.getTime());
        btn_timePicker.setText("Time\n" + timeString);
    }

    public void add(View v) {
        if (!checkTripComponents()) {
            Trip data = new Trip(
                    editTxtTripName.getText().toString(),
                    calendar.getTimeInMillis(),
                    calendar.getTimeInMillis(),
                    TripState.UPCOMING.name(),
                    tripType,
                    editTxtStartPoint.getText().toString(),
                    editTxtEndPoint.getText().toString(),
                    null
            );
            // to insert item
            Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).insert(data));

            finish();
        }
    }

    public void close(View v) {
        finish();
    }

    //make a method that check if components are empty
    private boolean checkTripComponents() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        radioButtonType = findViewById(selectedId);
        if (editTxtTripName.getText().toString().isEmpty()
                || editTxtStartPoint.getText().toString().isEmpty()
                || editTxtEndPoint.getText().toString().isEmpty() || radioButtonType == null
                || btn_timePicker.getText().toString().isEmpty() || btn_datePicker.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (((calendar.getTimeInMillis() - System.currentTimeMillis()) < 0)) {
            Toast.makeText(getApplicationContext(), "Date and Time Cannot be in the past", Toast.LENGTH_SHORT).show();
            return true;
        }
        setType();
        return false;
    }

    private void setType() {
        if (radioButtonType.getId() == R.id.oneWay) {
            tripType = TripType.ONE_WAY.name();
        } else {
            tripType = TripType.ROUND.name();
        }
    }

}