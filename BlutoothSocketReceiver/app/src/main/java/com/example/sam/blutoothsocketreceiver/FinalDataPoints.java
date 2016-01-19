package com.example.sam.blutoothsocketreceiver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.ArrayList;

public class FinalDataPoints extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String alliance;
    ArrayList <String> teamOneDataName;
    ArrayList <String> teamOneDataScore;
    ArrayList <String> teamTwoDataName;
    ArrayList <String> teamTwoDataScore;
    ArrayList <String> teamThreeDataName;
    ArrayList <String> teamThreeDataScore;
    EditText allianceScore;
    ToggleButton captureCheck;

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
        teamOneDataName = intent.getStringArrayListExtra("dataNameOne");
        teamOneDataScore = intent.getStringArrayListExtra("ranksOfOne");
        teamTwoDataName = intent.getStringArrayListExtra("dataNameTwo");
        teamTwoDataScore = intent.getStringArrayListExtra("ranksOfTwo");
        teamThreeDataName = intent.getStringArrayListExtra("dataNameThree");
        teamThreeDataScore = intent.getStringArrayListExtra("ranksOfThree");

        allianceScore = (EditText)findViewById(R.id.finalScoreEditText);
        captureCheck = (ToggleButton)findViewById(R.id.captureToggleButton);

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
            Log.e("dataNames", teamOneDataName.get(0));
            Log.e("matchNUmber", numberOfMatch);
            Log.e("team1", teamNumberOne);
            Log.e("Key", (teamNumberOne + "Q" + numberOfMatch));
            firebaseRef.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumberOne));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));

            for (int i = 0; i <= 4; i++){
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child(teamOneDataName.get(i)).setValue(Integer.parseInt(teamOneDataScore.get(i)));
            }
            for (int i = 0; i <= 4; i++){
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child(teamTwoDataName.get(i)).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
            }
            for (int i = 0; i <= 4; i++){
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child(teamThreeDataName.get(i)).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
            }
            for (int i = 5; i <= 9; i++){
                String teamOneDefenseEff = (teamOneDataName.get(i).replace("Cross Eff", "")).toLowerCase();
                String teamTwoDefenseEff = (teamTwoDataName.get(i)).replace("Cross Eff", "").toLowerCase();
                String teamThreeDefenseEff = (teamThreeDataName.get(i)).replace("Cross Eff", "").toLowerCase();
                firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamOneDefenseEff).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamTwoDefenseEff).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamThreeDefenseEff).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
            }
            if(captureCheck.isChecked()){
                if(alliance.equals("Blue Alliance")) {
                    firebaseRef.child("/Matches").child(numberOfMatch).child("blueAllianceDidCapture").setValue("true");
                    firebaseRef.child("/Matches").child(numberOfMatch).child("blueScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
                }else if(alliance.equals("Red Alliance")){
                    firebaseRef.child("/Matches").child(numberOfMatch).child("redAllianceDidCapture").setValue("true");
                    firebaseRef.child("/Matches").child(numberOfMatch).child("redScore").setValue(Integer.parseInt(allianceScore.getText().toString()));

                }
            }
                         Toast.makeText(this, "Sent Match Data", Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);

    }
}

