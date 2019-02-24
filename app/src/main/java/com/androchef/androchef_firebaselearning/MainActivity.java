package com.androchef.androchef_firebaselearning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androchef.androchef_firebaselearning.emailauth.EmailLoginActivity;
import com.androchef.androchef_firebaselearning.facebookauth.FacebookLoginActivity;
import com.androchef.androchef_firebaselearning.googleauth.GoogleLoginActivity;
import com.androchef.androchef_firebaselearning.phoneauth.PhoneLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFireBaseAuth;
    TextView loginEmail, loginPhone, loginTwitter, loginGMail, loginGithub, loginFaceBook;
    FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkUserLoggedIn();
        onClicks();
    }

    private void onClicks() {
        loginEmail.setOnClickListener(this);
        loginPhone.setOnClickListener(this);
        loginFaceBook.setOnClickListener(this);
        loginTwitter.setOnClickListener(this);
        loginGithub.setOnClickListener(this);
        loginGMail.setOnClickListener(this);
    }

    private void checkUserLoggedIn() {
        if (mFireBaseUser != null) {
            startActivity(new Intent(this, UserDetailActivity.class));
            finish();
        }
    }

    private void init() {
        mFireBaseAuth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.btn_login_email);
        loginPhone = findViewById(R.id.btn_login_phone);
        loginFaceBook = findViewById(R.id.btn_login_facebook);
        loginTwitter = findViewById(R.id.btn_login_twitter);
        loginGMail = findViewById(R.id.btn_login_gmail);
        loginGithub = findViewById(R.id.btn_login_github);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_email:
                startActivity(new Intent(this, EmailLoginActivity.class));
                break;
            case R.id.btn_login_facebook:
                startActivity(new Intent(this, FacebookLoginActivity.class));
                break;
            case R.id.btn_login_phone:
                startActivity(new Intent(this, PhoneLoginActivity.class));
                break;
            case R.id.btn_login_twitter:
                Toast.makeText(this, "Please wait until  Next Tutorials!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_login_gmail:
                startActivity(new Intent(this, GoogleLoginActivity.class));
                break;
            case R.id.btn_login_github:
                Toast.makeText(this, "Please wait until  Next Tutorials!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
