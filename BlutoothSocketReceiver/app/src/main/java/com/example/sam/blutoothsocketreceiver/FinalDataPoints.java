package com.example.sam.blutoothsocketreceiver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FinalDataPoints extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String firstDefense;
    String secondDefense;
    String thirdDefense;
    String fourthDefense;
    String alliance;
    TextView finalScore;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    EditText allianceScore;
    ToggleButton captureCheck;
    File dir;
    PrintWriter file;

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
        firstDefense = intent.getExtras().getString("firstDefensePicked");
        secondDefense = intent.getExtras().getString("secondDefensePicked");
        thirdDefense = intent.getExtras().getString("thirdDefensePicked");
        fourthDefense = intent.getExtras().getString("fourthDefensePicked");
        alliance = intent.getExtras().getString("alliance");
        finalScore = (TextView)findViewById(R.id.finalScoreTextView);
        if(alliance.equals("Blue Alliance")){
            finalScore.setTextColor(Color.BLUE);
        }else if(alliance.equals("Red Alliance")){
            finalScore.setTextColor(Color.RED);
        }
        teamOneDataName = intent.getStringArrayListExtra("dataNameOne");
        teamOneDataScore = intent.getStringArrayListExtra("ranksOfOne");
        teamTwoDataName = intent.getStringArrayListExtra("dataNameTwo");
        teamTwoDataScore = intent.getStringArrayListExtra("ranksOfTwo");
        teamThreeDataName = intent.getStringArrayListExtra("dataNameThree");
        teamThreeDataScore = intent.getStringArrayListExtra("ranksOfThree");

        allianceScore = (EditText) findViewById(R.id.finalScoreEditText);
        captureCheck = (ToggleButton) findViewById(R.id.captureToggleButton);
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");

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
            try {

                file = null;
                //make the directory of the file
                dir.mkdir();
                //can delete when doing the actual thing
                file = new PrintWriter(new FileOutputStream(new File(dir, ("Q" + numberOfMatch + " "  + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date())))));
            } catch (IOException IOE) {
                Log.e("File error", "Failed to open File");
                return false;
            }
            firebaseRef.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumberOne));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumberTwo));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumberThree));
            firebaseRef.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
            if(alliance.equals("Blue Alliance")) {
                firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("0").setValue(firstDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("1").setValue(secondDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("2").setValue(thirdDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("3").setValue(fourthDefense);
            }else if(alliance.equals("Red Alliance")){
                firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("0").setValue(firstDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("1").setValue(secondDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("2").setValue(thirdDefense);
                firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("3").setValue(fourthDefense);
            }
            file.println("Match Number:" + numberOfMatch);
            file.println("Team 1:" + teamNumberOne);
            file.println("Team 2" + teamNumberTwo);
            file.println("Team 3" + teamNumberThree);
            file.println("first Defense: " + firstDefense);
            Log.e("second defense", secondDefense);
            file.println("second Defense: " + secondDefense);
            file.println("third Defense: " + thirdDefense);
            file.println("fourth Defense: " + fourthDefense);

            for (int i = 0; i <= 4; i++) {
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child(teamOneDataName.get(i)).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                file.println(teamOneDataName.get(i) + ":" + teamOneDataScore.get(i));
            }
            for (int i = 0; i <= 4; i++) {
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child(teamTwoDataName.get(i)).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                file.println(teamTwoDataName.get(i) + ":" + teamTwoDataScore.get(i));
            }
            for (int i = 0; i <= 4; i++) {
                firebaseRef.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child(teamThreeDataName.get(i)).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
                file.println(teamThreeDataName.get(i) + ":" + teamThreeDataScore.get(i));
            }
            for (int i = 5; i <= 9; i++) {
                String teamOneDefenseEff = (teamOneDataName.get(i).replace("Cross Eff ", "")).toLowerCase();
                String teamTwoDefenseEff = (teamTwoDataName.get(i)).replace("Cross Eff ", "").toLowerCase();
                String teamThreeDefenseEff = (teamThreeDataName.get(i)).replace("Cross Eff ", "").toLowerCase();
                
                firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamOneDefenseEff).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamTwoDefenseEff).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("rankDefenseCrossingEffectiveness").child(teamThreeDefenseEff).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
                file.println(teamNumberOne + " " + "defense Eff." + ":" + teamOneDataScore.get(i));
                file.println(teamNumberTwo + " " + "defense Eff." + ":" + teamTwoDataScore.get(i));
                file.println(teamNumberThree + " " + "defense Eff." + ":" + teamThreeDataScore.get(i));
            }
            if (captureCheck.isChecked()) {
                if (alliance.equals("Blue Alliance")) {
                    firebaseRef.child("/Matches").child(numberOfMatch).child("blueAllianceDidCapture").setValue("true");
                    firebaseRef.child("/Matches").child(numberOfMatch).child("blueScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
                    file.println("blueAllianceCapture :" + "true");
                } else if (alliance.equals("Red Alliance")) {
                    firebaseRef.child("/Matches").child(numberOfMatch).child("redAllianceDidCapture").setValue("true");
                    firebaseRef.child("/Matches").child(numberOfMatch).child("redScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
                    file.println("blueAllianceCapture :" + "false");
                }
            }

            Toast.makeText(this, "Sent Match Data", Toast.LENGTH_SHORT).show();
            Intent backToHome = new Intent(this, MainActivity.class);
            backToHome.putExtra("alliance", alliance);
            Log.e("alliance", alliance);
            startActivity(backToHome);
        }
        return super.onOptionsItemSelected(item);
    }
}



