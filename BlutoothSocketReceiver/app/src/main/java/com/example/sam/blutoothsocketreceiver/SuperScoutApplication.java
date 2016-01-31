package com.example.sam.blutoothsocketreceiver;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by sam on 1/31/16.
 */

public class SuperScoutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase("https://1678-dev-2016.firebaseio.com");
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            }
        };

        firebase.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);


        FirebaseLists.matchesList = new FirebaseList<>("https://1678-dev-2016.firebaseio.com/Matches/", new FirebaseList.FirebaseUpdatedCallback() {
            @Override
            public void execute() {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("matches_updated"));
            }
        }, Match.class);
    }
}
