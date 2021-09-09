package com.example.tripawy;

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

import com.example.tripawy.helper.HelperMethods;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_signUp;
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
        Objects.requireNonNull(getSupportActionBar()).hide();


    }

    private void initializeComponent() {
        btn_signUp = findViewById(R.id.btn_signUp);
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
        if (HelperMethods.isNetworkConnected(this)) {
            if (checkEmpty()) {
                mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String Name = editTextFirstName.getText().toString() +
                                        " " + editTextLastName.getText().toString();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(Name).build();
                                if (user != null) {
                                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }

                            } else {
                                Toast.makeText(SignUpActivity.this, "Wrong Email Address",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }

        } else {
            View vee = findViewById(R.id.coordinatorSignUp);
            Snackbar snackbar = Snackbar
                    .make(vee, "Network Error", Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                    .setTextColor(ContextCompat.getColor(this, R.color.sky))
                    .setActionTextColor(ContextCompat.getColor(this, R.color.sky))
                    .setAction("FIX", view -> HelperMethods.openWifiSettings(SignUpActivity.this));
            snackbar.show();
        }
    }

    public boolean checkEmpty() {
        int count = 0;
        if (editTextFirstName.getText().toString().isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            count++;
        }
        if (editTextLastName.getText().toString().isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            count++;
        }
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();
        editTextFirstName.setOnTouchListener((arg0, arg1) -> {
            editTextFirstName.setHint("");
            return false;
        });
        editTextFirstName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                editTextLastName.setCursorVisible(true);
                editTextLastName.setHint("");
                return true;
            }
            return false;
        });

        editTextLastName.setOnTouchListener((arg0, arg1) -> {
            editTextLastName.setHint("");
            return false;
        });
        editTextLastName.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                editTextEmail.setCursorVisible(true);
                editTextEmail.setHint("");
                return true;
            }
            return false;
        });

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
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {


                btn_signUp.performClick();
                btn_signUp.setPressed(true);
                btn_signUp.invalidate();
                btn_signUp.setPressed(false);
                btn_signUp.invalidate();
                closeKeyboard();
                return true;
            }
            return false;
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

