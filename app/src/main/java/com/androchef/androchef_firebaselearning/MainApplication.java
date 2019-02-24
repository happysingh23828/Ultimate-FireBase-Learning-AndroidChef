package com.androchef.androchef_firebaselearning;

import android.app.Application;

import com.facebook.FacebookSdk;


public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setApplicationId(Constants.FACEBOOK_APP_ID);
    }

}
