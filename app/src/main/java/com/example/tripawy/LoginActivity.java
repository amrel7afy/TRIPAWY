package com.example.tripawy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripawy.methods.Methods;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private Button btn_signIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponent();
        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();

        editTextEmail.setOnTouchListener((arg0, arg1) -> {
            editTextEmail.setHint("");
            return false;
        });
        editTextEmail.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                editTextPassword.setCursorVisible(true);
                editTextPassword.setHint("");
                return true;
            }
            return false;
        });
        editTextPassword.setOnTouchListener((arg0, arg1) -> {
            editTextPassword.setHint("");
            return false;
        });
        editTextPassword.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                btn_signIn.performClick();
                btn_signIn.setPressed(true);
                btn_signIn.invalidate();
                btn_signIn.setPressed(false);
                btn_signIn.invalidate();
                closeKeyboard();
                return true;
            }
            return false;
        });


    }

    private void initializeComponent() {
        btn_signIn = findViewById(R.id.btn_signIn);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    public void createNew(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void signIn(View v) {
        if (Methods.isNetworkConnected(this)) {
            if (checkEmpty()) {
                mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                getData(user);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Wrong Email and Password",
                                        Toast.LENGTH_LONG).show();
                            }
                        });


            }
        } else {
            View view = findViewById(R.id.coordinatorLogIn);
            Snackbar snackbar = Snackbar
                    .make(view, "Network Error", Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                    .setTextColor(ContextCompat.getColor(this, R.color.sky))
                    .setAction("FIX", view1 -> Methods.openWifiSettings(LoginActivity.this));
            snackbar.show();
        }

    }

    private void getData(FirebaseUser user) {
        // Read from the database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference("Users");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(user.getUid())) {
                    for (int i = 0; i < dataSnapshot.child(user.getUid()).child("Trips").getChildrenCount(); i++) {
                        Trip trip = dataSnapshot.child(user.getUid()).child("Trips").child(String.valueOf(i)).getValue(Trip.class);
                        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(getApplication()).insert(trip));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Empty Fields Check
    public boolean checkEmpty() {
        int count = 0;

        if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Email is required!");
            count++;
        }
        if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("Password is required!");
            count++;
        }
        return count == 0;
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}