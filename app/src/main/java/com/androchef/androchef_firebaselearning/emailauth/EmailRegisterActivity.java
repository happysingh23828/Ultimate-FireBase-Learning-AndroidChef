package com.androchef.androchef_firebaselearning.emailauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EmailRegisterActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    EditText edtEmail, edtPassword, edtName, edtCity;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);
        init();
        onClicks();
    }

    private void init() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtName = findViewById(R.id.edt_name);
        edtCity = findViewById(R.id.edt_city);
        progressDialog = new ProgressDialog(this);
    }

    private void onClicks() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = edtEmail.getText().toString();
                String enteredPassword = edtPassword.getText().toString();
                String enteredCity = edtCity.getText().toString();
                String enteredName = edtName.getText().toString();
                if (validateData()) {
                    registerUserWithEmail(enteredName, enteredEmail, enteredPassword, enteredCity);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailRegisterActivity.this,EmailLoginActivity.class));
                finish();
            }
        });

    }

    private void registerUserWithEmail(final String enteredName, final String enteredEmail, String enteredPassword, final String enteredCity) {
        progressDialog.setMessage("Registering..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserDetailsToDatabase(enteredName, enteredEmail, enteredCity);
                } else {
                    Toast.makeText(EmailRegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void saveUserDetailsToDatabase(String enteredName, String enteredEmail, String enteredCity) {
        String userUniqueId = mAuth.getCurrentUser().getUid();

        HashMap<String, String> map = new HashMap<>();
        map.put("Name", enteredName);
        map.put("Email", enteredEmail);
        map.put("City", enteredCity);

        mDataBaseRef.child("Users").child(userUniqueId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailRegisterActivity.this, "Account Registered Successfully..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailRegisterActivity.this, UserDetailActivity.class));
                } else {
                    Toast.makeText(EmailRegisterActivity.this, "Registration Failed..", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private Boolean validateData() {

        if (edtEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Email..", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Name..", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Password..", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtPassword.getText().toString().length()<6){
            Toast.makeText(this, "Please Enter 6 Character Long Password..", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (edtCity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter City..", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
