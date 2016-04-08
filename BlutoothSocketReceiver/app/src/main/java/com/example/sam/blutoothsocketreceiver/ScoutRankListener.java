package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class ScoutRankListener extends Thread {
    Activity context;
    Gson gson;
    final static String dataBaseUrl = Constants.dataBaseUrl;
    public ScoutRankListener(Activity context) {
        this.context = context;
        gson = new Gson();
    }
    public void run() {

        Firebase firebase = new Firebase(dataBaseUrl + "Scout%20Scores");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LeaderBoard leaderBoard = dataSnapshot.getValue(LeaderBoard.class);
                Log.e("scoutStatData", leaderBoard.toString());
                //Change scout name for red alliance
                List<String> scoutNames = Arrays.asList("blue 4", "blue 5", "blue 6");
                for (String name : scoutNames) {
                    new scoutrank(context, name, gson.toJson(leaderBoard));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Listener", "canceled");
            }
        });
    }
}
