package com.androchef.androchef_firebaselearning.facebookauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.androchef.androchef_firebaselearning.R;
import com.androchef.androchef_firebaselearning.UserDetailActivity;
import com.androchef.androchef_firebaselearning.phoneauth.SubmitUserDetailActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FacebookLoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();
    ProgressDialog mProgressDialog;

    LoginButton loginButton;
    CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        init();
        setFaceBookConfiguration();
    }


    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Sign in..");
        mProgressDialog.setMessage("please wait while sign in ..");
    }


    private void setFaceBookConfiguration() {
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.btn_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        setCallBackAfterLogin();
    }

    private void  setCallBackAfterLogin(){
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                doFaceBookLoginWithFireBase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                mProgressDialog.hide();
                Toast.makeText(FacebookLoginActivity.this, "Error Facebook Login :", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                mProgressDialog.hide();
                Toast.makeText(FacebookLoginActivity.this, "Error Facebook Login :", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doFaceBookLoginWithFireBase(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(FacebookLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    checkUserIsAlreadyExistInDatabase(task);
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(FacebookLoginActivity.this, "Error in Login :", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserIsAlreadyExistInDatabase(Task<AuthResult> task) {
        final String loggedUserUniqueId = task.getResult().getUser().getUid();
        mDataBaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(loggedUserUniqueId).exists()) {
                    startActivity(new Intent(FacebookLoginActivity.this, UserDetailActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(FacebookLoginActivity.this, SubmitUserDetailActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.hide();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgressDialog.show();
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
