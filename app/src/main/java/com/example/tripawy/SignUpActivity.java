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

public class SignUpActivity extends AppCompatActivity {

    private Button btn_signUp;
    private TextView signIn;
    private EditText editTextUserName;
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
        editTextUserName = findViewById(R.id.editTextEmailSU);
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
        saveData();
        mAuth.createUserWithEmailAndPassword(editTextUserName.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveData(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        editor.putString("FirstName", firstName);
        editor.putString("LastName", lastName);
        editor.commit();
    }

}