package com.example.tripawy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;

public class AddNoteActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    private EditText editTextNote;

    private int counter = 0;
    private View view1;
    private Trip trip;
    private ArrayList<String> arrayListNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayout = findViewById(R.id.linear);

        view1 = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.notes, linearLayout, false);
        linearLayout.addView(view1);

        if (getIntent().getExtras() != null) {
            trip = (Trip) getIntent().getSerializableExtra("Trip");
        }

        if (trip.getNotes() != null) {
            arrayListNotes = trip.getNotes();
        }


    }

    public void add(View v) {
        if ((counter < 10)) {
            editTextNote = view1.findViewById(R.id.editTextNote);
            arrayListNotes.add(editTextNote.getText().toString());
            view1 = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.notes, linearLayout, false);
            linearLayout.addView(view1);
            counter++;
        }
    }

    public void delete(View v) {
        if (counter != 0) {
            arrayListNotes.remove(counter - 1);
            linearLayout.removeViewAt(counter);
            counter--;
        }
    }

    public void save(View v) {
        editTextNote = view1.findViewById(R.id.editTextNote);
        if (!(editTextNote.getText().toString().isEmpty())) {
            arrayListNotes.add(editTextNote.getText().toString());
        }
        trip.setNotes(arrayListNotes);
        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(this.getApplicationContext()).update(trip));
        finish();
    }

    public void close(View v) {
        finish();
    }

}