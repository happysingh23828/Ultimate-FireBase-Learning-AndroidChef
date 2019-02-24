package com.androchef.androchef_firebaselearning.phoneauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androchef.androchef_firebaselearning.R;
import com.androchef.androchef_firebaselearning.UserDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SubmitUserDetailActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();

    EditText edtName, edtCity;
    Button btnSubmit;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_user_detail);
        init();
        onClicks();
    }

    private void init() {
        edtName = findViewById(R.id.edt_user_name);
        edtCity = findViewById(R.id.edt_user_city);
        btnSubmit = findViewById(R.id.btn_submit);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Setting up account..");
        mProgressDialog.setMessage("Please wait while setting up your account");
    }

    private void onClicks() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    mProgressDialog.show();
                    storeDetailInDataBase(edtName.getText().toString(), edtCity.getText().toString());
                }
            }
        });

        edtCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (validateData()) {
                        mProgressDialog.show();
                        storeDetailInDataBase(edtName.getText().toString(), edtCity.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void storeDetailInDataBase(String name, String city) {
        String loggedUserUniqueId = mAuth.getCurrentUser().getUid();
        String loggedUserPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        String loggedUserEmail = mAuth.getCurrentUser().getEmail();
        HashMap<String, String> map = new HashMap<>();
        map.put("Name", name);
        map.put("City", city);
        map.put("Email",loggedUserEmail);
        map.put("Phone", loggedUserPhoneNumber);

        mDataBaseRef.child("Users").child(loggedUserUniqueId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(SubmitUserDetailActivity.this, "Detail Submitted Successfully..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SubmitUserDetailActivity.this, UserDetailActivity.class));
                    finish();
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(SubmitUserDetailActivity.this, "Error While Submitting Detail :", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateData() {
        if (edtName.getText().toString().isEmpty()) {
            Toast.makeText(this, "please enter name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtCity.getText().toString().isEmpty()) {
            Toast.makeText(this, "please enter city", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
