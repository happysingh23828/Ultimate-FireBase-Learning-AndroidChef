package com.androchef.androchef_firebaselearning.googleauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androchef.androchef_firebaselearning.Constants;
import com.androchef.androchef_firebaselearning.R;
import com.androchef.androchef_firebaselearning.UserDetailActivity;
import com.androchef.androchef_firebaselearning.phoneauth.PhoneLoginActivity;
import com.androchef.androchef_firebaselearning.phoneauth.SubmitUserDetailActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoogleLoginActivity extends AppCompatActivity {

    Button btnGoogleLogin;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference  mDataBaseRef = FirebaseDatabase.getInstance().getReference();

    ProgressDialog mProgressDialog;
    public static final int RC_SIGN_IN = 121;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        init();
        configureGoogleSignIn();
        onClicks();
    }

    private void init() {
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Sign in..");
        mProgressDialog.setMessage("please wait while sign in ..");
    }

    // Configure Google Sign In
    private void configureGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void onClicks() {
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                startGoogleSignIn();
            }
        });
    }

    private void startGoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            checkUserGetLoggedInOrNot(data);
        }
    }

    public void checkUserGetLoggedInOrNot(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            doGoogleLoginWithFireBase(account);
        } catch (ApiException e) {
            mProgressDialog.hide();
            Toast.makeText(this, "Error While Login :", Toast.LENGTH_SHORT).show();
        }
    }

    public void doGoogleLoginWithFireBase(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(GoogleLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    checkUserIsAlreadyExistInDatabase(task);
                } else {
                    mProgressDialog.hide();
                    Toast.makeText(GoogleLoginActivity.this, "Error in Login :", Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(GoogleLoginActivity.this, UserDetailActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(GoogleLoginActivity.this, SubmitUserDetailActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
