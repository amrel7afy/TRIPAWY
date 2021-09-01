package com.example.tripawy;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class AddNewTripFragment extends DialogFragment {
Button btn_datePicker;
Button btn_timePicker;
    private View root_view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.add_new_trip_dialog,container,false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    btn_datePicker =root_view.findViewById(R.id.btnDatePicker);
    btn_timePicker=root_view.findViewById(R.id.btnTimePicker);
      btn_datePicker.setOnClickListener(new View.OnClickListener() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onClick(View view) {
              pickDate();
          }
      });
      btn_timePicker.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              pickTime();
          }
      });
      

        return root_view;

    }

    private void pickTime() {
    }

    public void pickDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        btn_datePicker.setText(day + "/" + month + "/" + year);

                    }
                }, 2021, 9, 12);
        datePickerDialog.show();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    }
                }, year, month, dayOfMonth);
    }
}
