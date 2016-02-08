package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class SuperNotes extends ActionBarActivity {
    Intent notes;
    String alliance;
    String numberOfMatch;
    String teamOne;
    String teamTwo;
    String teamThree;
    TextView superNoteTextView;
    TextView teamOneTextView;
    TextView teamTwoTextView;
    TextView teamThreeTextView;
    EditText superNotesEditText;
    Firebase dataBase;
    boolean isBlue = new Boolean(true);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_notes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        notes = getIntent();
        superNoteTextView = (TextView)findViewById(R.id.allianceNotesTextView);
        teamOneTextView = (TextView)findViewById(R.id.teamOneNoteTextView);
        teamTwoTextView = (TextView)findViewById(R.id.teamTwoNoteTextView);
        teamThreeTextView = (TextView)findViewById(R.id.teamThreeNoteTextView);
        alliance = notes.getExtras().getString("alliance");
        numberOfMatch = notes.getStringExtra("matchNumber");
        teamOne = notes.getStringExtra("teamOne");
        teamTwo = notes.getStringExtra("teamTwo");
        teamThree = notes.getStringExtra("teamThree");
        if(alliance.equals("Red Alliance")){
            superNoteTextView.setTextColor(Color.RED);
            teamOneTextView.setTextColor(Color.RED);
            teamTwoTextView.setTextColor(Color.RED);
            teamThreeTextView.setTextColor(Color.RED);
            isBlue = false;
        }else if(alliance.equals("Blue Alliance")){
            superNoteTextView.setTextColor(Color.BLUE);
            teamOneTextView.setTextColor(Color.BLUE);
            teamTwoTextView.setTextColor(Color.BLUE);
            teamThreeTextView.setTextColor(Color.BLUE);
            isBlue = true;
        }
        teamOneTextView.setText(teamOne);
        teamTwoTextView.setText(teamTwo);
        teamThreeTextView.setText(teamThree);
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
            }
        };
        dataBase = new Firebase("https://1678-dev2-2016.firebaseio.com/");
        dataBase.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notesubmit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.noteSubmit){
            Log.e("matchNumber", numberOfMatch);
            new Thread() {
                public void run() {
                    if (isBlue) {
                        Log.e("matchNumber", numberOfMatch);
                        dataBase.child("Matches").child(numberOfMatch).child("BSNotes").setValue(superNotesEditText.getText().toString());
                    } else {
                        dataBase.child("Matches").child(numberOfMatch).child("RSNotes").setValue(superNotesEditText.getText().toString());
                    }
                }
            }.start();
            final Activity activity = this;
            activity.finish();
        }
        return super.onOptionsItemSelected(item);
        }

    }




