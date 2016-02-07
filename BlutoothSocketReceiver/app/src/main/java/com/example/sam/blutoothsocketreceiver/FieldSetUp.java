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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FieldSetUp extends ActionBarActivity {
    List<ToggleButton> toggleButtonList;
    String numberOfMatch;
    String teamOneNumber;
    String teamTwoNumber;
    String teamThreeNumber;
    String alliance;
    ArrayList<String> defensesPicked;
    ToggleButton defenseButton;
    Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fieldsetup);
        Intent intent = getIntent();
        //get the data the previous activity passed to this one.
        numberOfMatch = intent.getExtras().getString("matchNumber");
        teamOneNumber = intent.getExtras().getString("teamNumberOne");
        teamTwoNumber = intent.getExtras().getString("teamNumberTwo");
        teamThreeNumber = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");
        firebaseRef = new Firebase("https://1678-dev-2016.firebaseio.com");

        toggleButtonList = new ArrayList<>();
        defensesPicked = new ArrayList<>();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ArrayList<String> defenses = new ArrayList<>(Arrays.asList("PC", "CDF", "DB", "SP", "RT", "RW", "RP", "MT"));

        LinearLayout layout = (LinearLayout) findViewById(R.id.row1_of_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        //create the 4 by 7 of buttons
        for (int i = 0; i < 4; i++) {
            TextView column_number = new TextView(this);
            column_number.setText("Defense" + " " + Integer.toString(i + 1));
            LinearLayout columns = new LinearLayout(this);
            columns.setOrientation(LinearLayout.HORIZONTAL);
            columns.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            column_number.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            for (int j = 0; j <= 7; j++) {
                defenseButton = new ToggleButton(this);
                defenseButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                defenseButton.setText(defenses.get(j));
                defenseButton.setTextOn(defenses.get(j));
                defenseButton.setTextOff(defenses.get(j));
                defenseButton.setTag(Integer.toString(i));
                defenseButton.setBackgroundColor(Color.LTGRAY);
                defenseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < ((LinearLayout) v.getParent()).getChildCount(); i++) {
                            ((ToggleButton) ((LinearLayout) v.getParent()).getChildAt(i)).setChecked(false);
                            ((ToggleButton) ((LinearLayout) v.getParent()).getChildAt(i)).setBackgroundColor(Color.LTGRAY);
                        }
                        ((ToggleButton) v).setChecked(true);
                        ((ToggleButton) v).setBackgroundColor(Color.GREEN);
                    }
                });

                columns.addView(defenseButton);
                toggleButtonList.add(defenseButton);

            }
            layout.addView(column_number);
            layout.addView(columns);
        }

    }
    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING")
                .setMessage("GOING BACK WILL CAUSE LOSS OF DATA")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.superdata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nextButton) {
            Intent next = new Intent(this, Super_Scouting.class);
            defensesPicked.clear();
            for (int i = 0; i < toggleButtonList.size(); i++) {
                if (toggleButtonList.get(i).isChecked()) {
                    defensesPicked.add(toggleButtonList.get(i).getText().toString());
                }
            }
            if (defensesPicked.size() < 4) {
                Toast.makeText(this, "Input four defenses!!", Toast.LENGTH_LONG).show();

            } else if(defensesPicked.size() == 4) {
                new Thread() {
                    @Override
                    public void run() {
                        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                            }
                        };
                        firebaseRef.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
                        if (alliance.equals("Blue Alliance")) {
                            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("0").setValue(defensesPicked.get(0));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("1").setValue(defensesPicked.get(1));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("2").setValue(defensesPicked.get(2));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("3").setValue(defensesPicked.get(3));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDefensePositions").child("4").setValue("LB");
                            Log.e("blue alliance", "Sent defense position of Blue");

                        } else if (alliance.equals("Red Alliance")) {
                            firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("0").setValue(defensesPicked.get(0));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("1").setValue(defensesPicked.get(1));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("2").setValue(defensesPicked.get(2));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("3").setValue(defensesPicked.get(3));
                            firebaseRef.child("/Matches").child(numberOfMatch).child("redDefensePositions").child("4").setValue("LB");
                            Log.e("red alliance", "Sent defense position of Red");
                        }
                    }
                }.start();
                    next.putExtra("matchNumber", numberOfMatch);
                    next.putExtra("teamNumberOne", teamOneNumber);
                    next.putExtra("teamNumberTwo", teamTwoNumber);
                    next.putExtra("teamNumberThree", teamThreeNumber);
                    next.putExtra("firstDefensePicked", defensesPicked.get(0));
                    next.putExtra("secondDefensePicked", defensesPicked.get(1));
                    next.putExtra("thirdDefensePicked", defensesPicked.get(2));
                    next.putExtra("fourthDefensePicked", defensesPicked.get(3));
                    next.putExtra("alliance", alliance);
                    startActivity(next);
                 } else {
                Log.i("test", "here");
            }

            }
            return super.onOptionsItemSelected(item);
        }

    }




