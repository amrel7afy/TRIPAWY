package com.example.tripawy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_signUp;
    private TextView signIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeComponent();
        mAuth = FirebaseAuth.getInstance();


    }

    private void initializeComponent() {
        btn_signUp = findViewById(R.id.btn_signUp);
        signIn = findViewById(R.id.txtsignIn);
        editTextEmail = findViewById(R.id.editTextEmailSU);
        editTextPassword = findViewById(R.id.editTextPasswordSU);
        editTextFirstName = findViewById(R.id.editTextFirstNameSU);
        editTextLastName = findViewById(R.id.editTextLastNameSU);
    }

    public void signIn(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void setBtn_signUp(View v) {
        if (editTextFirstName.getText().toString().isEmpty() && editTextLastName.getText().toString().isEmpty()
                && editTextEmail.getText().toString().isEmpty() && editTextPassword.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextLastName.setError("Last Name is required!");
            editTextEmail.setError("Email is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextFirstName.getText().toString().isEmpty() && editTextLastName.getText().toString().isEmpty()
                && editTextEmail.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextLastName.setError("Last Name is required!");
            editTextEmail.setError("Email is required!");
        } else if (editTextFirstName.getText().toString().isEmpty() && editTextEmail.getText().toString().isEmpty()
                && editTextPassword.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextEmail.setError("Email is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextFirstName.getText().toString().isEmpty() && editTextLastName.getText().toString().isEmpty()
                && editTextPassword.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextLastName.setError("Last Name is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextLastName.getText().toString().isEmpty() && editTextEmail.getText().toString().isEmpty()
                && editTextPassword.getText().toString().isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            editTextEmail.setError("Email is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextFirstName.getText().toString().isEmpty() && editTextLastName.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextLastName.setError("Last Name is required!");
        } else if (editTextFirstName.getText().toString().isEmpty() && editTextEmail.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextEmail.setError("Email is required!");

        } else if (editTextFirstName.getText().toString().isEmpty() && editTextPassword.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextLastName.getText().toString().isEmpty() && editTextEmail.getText().toString().isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            editTextEmail.setError("Email is required!");
        } else if (editTextLastName.getText().toString().isEmpty() && editTextPassword.getText().toString().isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextEmail.getText().toString().isEmpty() && editTextPassword.getText().toString().isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextPassword.setError("Password is required!");
        } else if (editTextFirstName.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
        } else if (editTextLastName.getText().toString().isEmpty()) {
            editTextLastName.setError("Last Name is required!");
        } else if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Email is required!");
        } else if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("Password is required!");
        } else {
            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String Name = editTextFirstName.getText().toString() +
                                        " " + editTextLastName.getText().toString();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(Name).build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(SignUpActivity.this, "Wrong Email Address",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }


}