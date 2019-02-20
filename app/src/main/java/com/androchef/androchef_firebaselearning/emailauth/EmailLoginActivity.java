package com.androchef.androchef_firebaselearning.emailauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androchef.androchef_firebaselearning.R;
import com.androchef.androchef_firebaselearning.UserDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    EditText edtEmail, edtPassword;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        init();
        onClicks();
    }

    private void init() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        progressDialog = new ProgressDialog(this);
    }

    private void onClicks() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    String enteredEmail = edtEmail.getText().toString();
                    String enteredPassword = edtPassword.getText().toString();
                    login(enteredEmail, enteredPassword);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailLoginActivity.this, EmailRegisterActivity.class));
            }
        });

    }

    private void login(String enteredEmail, String enteredPassword) {
        progressDialog.setMessage("Logging....");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailLoginActivity.this, "Logged in successfully..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailLoginActivity.this, UserDetailActivity.class));
                    finish();
                } else
                    Toast.makeText(EmailLoginActivity.this, "Logging in Error: ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private Boolean validateData() {

        if (edtEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Email..", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Password..", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
