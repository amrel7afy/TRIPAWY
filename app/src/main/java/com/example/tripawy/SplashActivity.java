package com.example.tripawy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();


        //Check User Authentication
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent intent;
            if (currentUser != null) {
                intent = new Intent(getApplicationContext(), HomeActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), SignUpActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1000);
    }
}