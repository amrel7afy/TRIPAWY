package com.example.tripawy;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditTripActivity extends AppCompatActivity {

    private Button btn_datePicker;
    private Button btn_timePicker;
    private EditText editTxtTripName;
    private EditText editTxtStartPoint;
    private EditText editTxtEndPoint;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonType;
    private String tripType;


    private Trip trip;
    private int year, month, dayOfMonth, hour, minute;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initializeComponent();

        if (getIntent().getExtras() != null) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        }

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(trip.getDate());

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        editTxtTripName.setText(trip.getName());
        editTxtStartPoint.setText(trip.getFrom());
        editTxtEndPoint.setText(trip.getTo());

        if (trip.getTripType().equals(TripType.ROUND.name())) {
            radioButtonType = findViewById(R.id.roundEdit);
        } else {
            radioButtonType = findViewById(R.id.oneWayEdit);
        }

        radioButtonType.setChecked(true);

        updateTime(calendar);
        updateDate(calendar);

    }

    private void initializeComponent() {
        btn_datePicker = findViewById(R.id.btnDatePickerEdit);
        btn_timePicker = findViewById(R.id.btnTimePickerEdit);
        editTxtTripName = findViewById(R.id.editTxtTripNameEdit);
        editTxtStartPoint = findViewById(R.id.editTxtStartPointEdit);
        editTxtEndPoint = findViewById(R.id.editTxtEndPointEdit);
        radioGroupType = findViewById(R.id.radioGroupTypeEdit);
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

    private void updateDate(Calendar cDate) {
        String dateString = DateFormat.getDateInstance().format(cDate.getTime());
        btn_datePicker.setText(dateString);
    }

    private void updateTime(Calendar c) {
        String timeString = DateFormat.getTimeInstance().format(c.getTime());
        btn_timePicker.setText(timeString);
    }

    public void editSave(View v) {
        if (!checkTripComponents()) {
            trip.setName(editTxtTripName.getText().toString());
            trip.setFrom(editTxtStartPoint.getText().toString());
            trip.setTo(editTxtEndPoint.getText().toString());
            trip.setDate(calendar.getTimeInMillis());
            trip.setTime(calendar.getTimeInMillis());
            trip.setTripType(tripType);


            Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).update(trip));
            finish();
        }
    }


    public void close(View v) {
        finish();
    }

    public boolean checkTripComponents() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        radioButtonType = findViewById(selectedId);
        if (editTxtTripName.getText().toString().isEmpty()
                || editTxtStartPoint.getText().toString().isEmpty()
                || editTxtEndPoint.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return true;
        }
        if ((calendar.getTimeInMillis() - System.currentTimeMillis()) < 0) {
            Toast.makeText(getApplicationContext(), "Date and Time Cannot be in the past", Toast.LENGTH_SHORT).show();
            return true;
        }
        setType();
        return false;
    }

    private void setType() {
        if (radioButtonType.getId() == R.id.oneWayEdit) {
            tripType = TripType.ONE_WAY.name();
        } else {
            tripType = TripType.ROUND.name();
        }
    }

}