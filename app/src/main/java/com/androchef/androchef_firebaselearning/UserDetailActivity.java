package com.androchef.androchef_firebaselearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    Button btnLogout;
    TextView name,email,city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        init();
        setUserData();
        onClicks();
    }


    private void init() {
        btnLogout = findViewById(R.id.btn_logout);
        name = findViewById(R.id.tv_name);
        email = findViewById(R.id.tv_email);
        city = findViewById(R.id.tv_city);
    }

    private void setUserData() {
        String currentUniqueId = mAuth.getCurrentUser().getUid();
        mDatabaseRef.child("Users").child(currentUniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("Name").getValue().toString();
                String userEmail = dataSnapshot.child("Email").getValue().toString();
                String userCity = dataSnapshot.child("City").getValue().toString();
                name.setText(userName);
                email.setText(userEmail);
                city.setText(userCity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onClicks(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(UserDetailActivity.this,MainActivity.class));
                finish();
            }
        });
    }
}
