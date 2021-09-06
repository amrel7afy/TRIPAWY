package com.example.tripawy;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private Button btn_signIn;
    private TextView createNew;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponent();
        mAuth = FirebaseAuth.getInstance();


    }

    private void initializeComponent() {
        btn_signIn = findViewById(R.id.btn_signIn);
        createNew = findViewById(R.id.createNew);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    public void createNew(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void signIn(View v) {

        if (editTextEmail.getText().toString().isEmpty() && editTextPassword.getText().toString().isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("Password is required!");
        } else if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Email is required!");
        } else {
            mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
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
                        }
                    });


        }


    }

    private void getData(FirebaseUser user) {
        // Read from the database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference("Users");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(user.getUid())) {
                    for (int i = 0; i < dataSnapshot.child(user.getUid()).child("Trips").getChildrenCount(); i++) {
                        Trip trip = (Trip) dataSnapshot.child(user.getUid()).child("Trips").child(String.valueOf(i)).getValue(Trip.class);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            RoomDB.getTrips(getApplication()).insert(trip);
                        });
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed 2", Toast.LENGTH_LONG).show();
            }
        });
    }


}