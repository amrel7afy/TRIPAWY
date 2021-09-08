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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class EditTripActivity extends AppCompatActivity {

    private Button btn_datePicker;
    private Button btn_timePicker;
    private Button btn_save;
    private ImageButton btn_close;
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
        getSupportActionBar().hide();
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
        }else {
            radioButtonType = findViewById(R.id.oneWayEdit);
        }

        radioButtonType.setChecked(true);

        updateTime(calendar);
        updateDate(calendar);

    }

    private void initializeComponent() {
        btn_datePicker = findViewById(R.id.btnDatePicker);
        btn_timePicker = findViewById(R.id.btnTimePicker);
        editTxtTripName = findViewById(R.id.editTxtTripName);
        editTxtStartPoint = findViewById(R.id.editTxtStartPoint);
        editTxtEndPoint = findViewById(R.id.editTxtEndPoint);
        radioGroupType = findViewById(R.id.radioGroupTypeEdit);
        btn_save = findViewById(R.id.btn_save);
        btn_close = findViewById(R.id.btn_close);
    }

    public void pickDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        updateDate(calendar);
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
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        updateTime(calendar);
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

    public void editSave(View v) {
        if (!checkTripComponents()) {
            trip.setName(editTxtTripName.getText().toString());
            trip.setFrom(editTxtStartPoint.getText().toString());
            trip.setTo(editTxtEndPoint.getText().toString());
            trip.setDate(calendar.getTimeInMillis());
            trip.setTime(calendar.getTimeInMillis());
            trip.setTripType(tripType);


            Executors.newSingleThreadExecutor().execute(() -> {
                RoomDB.getTrips(getApplication()).update(trip);
            });
            finish();
        }
    }


    public void close(View v) {
        finish();
    }

    public boolean checkTripComponents() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        radioButtonType = (RadioButton) findViewById(selectedId);
        if (editTxtTripName.getText().toString().isEmpty()
                || editTxtStartPoint.getText().toString().isEmpty()
                || editTxtEndPoint.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
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