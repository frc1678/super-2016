package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    String teamOneNote;
    String teamTwoNote;
    String teamThreeNote;
    TextView superNoteTextView;
    TextView teamOneTextView;
    TextView teamTwoTextView;
    TextView teamThreeTextView;
    EditText teamOneEditText;
    EditText teamTwoEditText;
    EditText teamThreeEditText;
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
        teamOneEditText = (EditText)findViewById(R.id.teamOneNotesEditText);
        teamTwoEditText = (EditText)findViewById(R.id.teamTwoNotesEditText);
        teamThreeEditText = (EditText)findViewById(R.id.teamThreeNotesEditText);
        getSuperNotesExtra();

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
        teamOneEditText.setText(teamOneNote);
        teamTwoEditText.setText(teamTwoNote);
        teamThreeEditText.setText(teamThreeNote);

        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {}
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {}
        };
        dataBase = new Firebase("https://1678-scouting-2016.firebaseio.com/");
        dataBase.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
    }
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("GO BACK?")
                .setMessage("Do you want to submit the notes first?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            public void run() {
                                Log.e("matchNumber", numberOfMatch);
                                dataBase.child("TeamInMatchDatas").child(teamOne + "Q" + numberOfMatch).child("superNotes").setValue(teamOneEditText.getText().toString());
                                dataBase.child("TeamInMatchDatas").child(teamTwo + "Q" + numberOfMatch).child("superNotes").setValue(teamTwoEditText.getText().toString());
                                dataBase.child("TeamInMatchDatas").child(teamThree + "Q" + numberOfMatch).child("superNotes").setValue(teamThreeEditText.getText().toString());
                            }
                        }.start();
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                    Log.e("matchNumber", numberOfMatch);
                    dataBase.child("TeamInMatchDatas").child(teamOne + "Q" + numberOfMatch).child("superNotes").setValue(teamOneEditText.getText().toString());
                    dataBase.child("TeamInMatchDatas").child(teamTwo + "Q" + numberOfMatch).child("superNotes").setValue(teamTwoEditText.getText().toString());
                    dataBase.child("TeamInMatchDatas").child(teamThree + "Q" + numberOfMatch).child("superNotes").setValue(teamThreeEditText.getText().toString());
                }
            }.start();
            final Activity activity = this;
            activity.finish();
        }
        return super.onOptionsItemSelected(item);
        }

    public void getSuperNotesExtra(){
        alliance = notes.getExtras().getString("alliance");
        numberOfMatch = notes.getStringExtra("matchNumber");
        teamOne = notes.getStringExtra("teamOne");
        teamTwo = notes.getStringExtra("teamTwo");
        teamThree = notes.getStringExtra("teamThree");
        teamOneNote = notes.getStringExtra("teamOneNote");
        teamTwoNote = notes.getStringExtra("teamTwoNote");
        teamThreeNote = notes.getStringExtra("teamThreeNote");
    }

    }




