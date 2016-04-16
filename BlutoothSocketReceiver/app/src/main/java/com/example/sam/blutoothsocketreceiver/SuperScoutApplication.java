package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by sam on 1/31/16.
 */

public class SuperScoutApplication extends Application implements Application.ActivityLifecycleCallbacks {
    String url = Constants.dataBaseUrl;
    public Activity currentActivity = null;
    final Thread.UncaughtExceptionHandler originalUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void onCreate() {
        registerActivityLifecycleCallbacks(this);
        super.onCreate();
        new Instabug.Builder(this, "6b6eb576898ed14a78727c3aa68ff738")
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .build();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase firebase = new Firebase(url);
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            }
        };
        if (url.equals("https://1678-scouting-2016.firebaseio.com/")) {
            firebase.authWithCustomToken("qVIARBnAD93iykeZSGG8mWOwGegminXUUGF2q0ee", authResultHandler);
        } else if (url.equals("https://1678-dev3-2016.firebaseio.com/")) {
            firebase.authWithCustomToken("AEduO6VFlZKD4v10eW81u9j3ZNopr5h2R32SPpeq", authResultHandler);
        } else if (url.equals("https://1678-dev-2016.firebaseio.com/")) {
            firebase.authWithCustomToken("j1r2wo3RUPMeUZosxwvVSFEFVcrXuuMAGjk6uPOc", authResultHandler);
        } else if (url.equals("https://1678-dev2-2016.firebaseio.com/")) {
            firebase.authWithCustomToken("hL8fStivTbHUXM8A0KXBYPg2cMsl80EcD7vgwJ1u", authResultHandler);
        }
        FirebaseLists.matchesList = new FirebaseList<>(url + "Matches/", new FirebaseList.FirebaseUpdatedCallback() {
            @Override
            public void execute() {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("matches_updated"));
            }
        }, Match.class);
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException(thread, e);

            }
        });

    }
    public void onActivityCreated(Activity activity, Bundle savedInstanceState){
        currentActivity = activity;
    }
    public void onActivityDestroyed(Activity activity){
        currentActivity = null;
    }
    public void onActivityPaused(Activity activity){

    }
    public void onActivityResumed(Activity activity){

    }
    public void onActivitySaveInstanceState(Activity activity, Bundle outState){

    }
    public void onActivityStarted(Activity activity){

    }
    public void onActivityStopped(Activity activity){

    }

    private void handleUncaughtException (Thread thread, Throwable e)
    {
        List<Class<? extends ActionBarActivity>> activities = Arrays.asList(MainActivity.class, FinalDataPoints.class);
        // The following shows what I'd like, though it won't work like this.
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e("UI thread", "CRASHED!");
            Log.e("Activity crashed", currentActivity.toString());
            takeScreenshot();
            originalUncaughtExceptionHandler.uncaughtException(thread, e);
        }else{
            Log.e("Background thread", "CRASHED");
            Toast.makeText(this, "Background Thread ERROR", Toast.LENGTH_LONG).show();
        }

    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        File dir = new File("/sdcard/Super_scout_backup");
        Log.e("dir", dir.getAbsolutePath());
        dir.mkdir();
        File photoFile = new File(dir, (new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date())) + ".jpg");
        try {
            // image naming and path  to include sd card  appending name you choose for file
            Log.e("test", "1");
            FileOutputStream file = new FileOutputStream(photoFile);
            // create bitmap screen capture
            Log.e("Test", "2");
            View v1 = currentActivity.getWindow().getDecorView().getRootView();
            Log.e("Test", "3");
            v1.setDrawingCacheEnabled(true);
            Log.e("Test", "4");
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            Log.e("Test", "5");
            v1.setDrawingCacheEnabled(false);
            Log.e("Test", "6");
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, file);
            Log.e("Test", "7");
            file.flush();
            Log.e("Test", "8");
            file.close();
            Log.e("Test", "9");
            Log.e("Test", "10");

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
            Log.e("photoSave", "CRASHED");
        }

    }

}


