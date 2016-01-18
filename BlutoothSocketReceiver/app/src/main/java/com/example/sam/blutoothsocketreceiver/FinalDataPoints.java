package com.example.sam.blutoothsocketreceiver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import android.widget.Toast;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class FinalDataPoints extends ActionBarActivity {
    String scoreNameOne;
    String scoreOne;
    String scoreNameTwo;
    String scoreTwo;
    String scoreNameThree;
    String scoreThree;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String alliance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finaldatapoints);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent = getIntent();
        numberOfMatch = intent.getExtras().getString("matchNumber");
        teamNumberOne = intent.getExtras().getString("teamNumberOne");
        teamNumberTwo = intent.getExtras().getString("teamNumberTwo");
        teamNumberThree = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Submit) {
            Firebase firebaseRef = new Firebase("https://1678-dev-2016.firebaseio.com");
            Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // Do nothing if authenticated
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid Permissions.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            };

            firebaseRef.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
            firebaseRef.child("/teamInMatchDatas").child("114Q7").child("teamNumber").setValue(111);
            /*firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("teamNumber").setValue(1);
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("matchNumber").setValue(1);*/
            Toast.makeText(this, "Sent Match Data", Toast.LENGTH_SHORT).show();

            LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
            LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
            LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);
            for(int i = 0; i < teamOneRelativeLayout.getChildCount(); i++){
                View teamOneLayout = teamOneRelativeLayout.getChildAt(i);
                scoreNameOne = (teamOneLayout.findViewById(R.id.dataName).toString());
                scoreOne = (teamOneLayout.findViewById(R.id.scoreCounter).toString());
            }
            for(int j = 0; j < teamTwoRelativeLayout.getChildCount(); j++ ){
                View teamTwoLayout = teamTwoRelativeLayout.getChildAt(j);
                scoreNameTwo = (teamTwoLayout.findViewById(R.id.dataName).toString());
                scoreTwo = (teamTwoLayout.findViewById(R.id.scoreCounter).toString());
            }
            for(int k = 0; k < teamThreeRelativeLayout.getChildCount(); k++){
                View teamThreeLayout = teamThreeRelativeLayout.getChildAt(k);
                scoreNameThree = (teamThreeLayout.findViewById(R.id.dataName).toString());
                scoreThree = (teamThreeLayout.findViewById(R.id.scoreCounter).toString());
            }

        }
        return super.onOptionsItemSelected(item);

    }
}

