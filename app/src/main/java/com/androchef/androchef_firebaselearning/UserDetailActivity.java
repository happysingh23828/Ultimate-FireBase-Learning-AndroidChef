package com.androchef.androchef_firebaselearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androchef.androchef_firebaselearning.phoneauth.SubmitUserDetailActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    TextView name, email, city;

    //Google SignIN
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        init();
        setUserData();
        onClicks();
        setUpGoogleSignOut();
    }


    private void init() {
        btnLogout = findViewById(R.id.btn_logout);
        name = findViewById(R.id.tv_name);
        email = findViewById(R.id.tv_email);
        city = findViewById(R.id.tv_city);
    }

    private void setUserData() {
        final String currentUniqueId = mAuth.getCurrentUser().getUid();
        mDatabaseRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUniqueId).exists()) {
                    String userName = dataSnapshot.child(currentUniqueId).child("Name").getValue().toString();
                    String userCity = dataSnapshot.child(currentUniqueId).child("City").getValue().toString();
                    name.setText(userName);
                    city.setText(userCity);
                    showEmailOrPassword(dataSnapshot.child(currentUniqueId));
                } else {
                    startActivity(new Intent(UserDetailActivity.this, SubmitUserDetailActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showEmailOrPassword(DataSnapshot userDataSnapshot) {
        if (userDataSnapshot.child("Email").exists() && !userDataSnapshot.child("Email").getValue().toString().equals("")) {
            String userEmail = userDataSnapshot.child("Email").getValue().toString();
            email.setText(userEmail);
        } else if(userDataSnapshot.child("Phone").exists() && !userDataSnapshot.child("Phone").getValue().toString().equals("")) {
            String userPhone = userDataSnapshot.child("Phone").getValue().toString();
            email.setText(userPhone);
        }
    }

    private void onClicks() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(UserDetailActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void setUpGoogleSignOut(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}
