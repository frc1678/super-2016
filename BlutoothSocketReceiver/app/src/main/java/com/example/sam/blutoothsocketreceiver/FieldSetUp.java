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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldSetUp extends ActionBarActivity {

    List<ToggleButton> toggleButtonList;
    String numberOfMatch;
    String teamOneNumber;
    String teamTwoNumber;
    String teamThreeNumber;
    String alliance;
    String dataBaseUrl;
    ArrayList<String> defensesPicked;
    ArrayList<String> checkDefensesPicked;
    ArrayList<String> sameDefenses;
    ArrayList<String> defenses;
    ToggleButton defenseButton;
    Boolean isMute;
    Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fieldsetup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent = getIntent();
        //get the data the previous activity passed to this one.
        numberOfMatch = intent.getExtras().getString("matchNumber");
        teamOneNumber = intent.getExtras().getString("teamNumberOne");
        teamTwoNumber = intent.getExtras().getString("teamNumberTwo");
        teamThreeNumber = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");
        dataBaseUrl = intent.getExtras().getString("dataBaseUrl");
        isMute = intent.getExtras().getBoolean("mute");

        firebaseRef = new Firebase(dataBaseUrl);
        toggleButtonList = new ArrayList<>();
        defensesPicked = new ArrayList<>();
        checkDefensesPicked = new ArrayList<>();
        sameDefenses = new ArrayList<>();
        defenses = new ArrayList<>(Arrays.asList("PC", "CDF", "DB", "SP", "RT", "RW", "RP", "MT"));

        LinearLayout layout = (LinearLayout) findViewById(R.id.row1_of_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        //create the 4 by 7 of buttons
        for (int i = 0; i < 4; i++) {
            TextView column_number = new TextView(this);
            column_number.setText("Defense" + " " + Integer.toString(i + 2));
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
            Intent next = new Intent(this, ScoutingPage.class);
            defensesPicked.clear();
            checkDefensesPicked.clear();
            for (int i = 0; i < toggleButtonList.size(); i++) {
                if (toggleButtonList.get(i).isChecked()) {
                    defensesPicked.add((toggleButtonList.get(i).getText().toString()).toLowerCase());
                    //checkDefensesPicked.add(toggleButtonList.get(i).getText().toString());
                }
            }
            for(int i = 0; i < defensesPicked.size(); i++){
                if ( (Collections.frequency(defensesPicked, defensesPicked.get(i))) > 1 ){
                    Toast.makeText(this, "You put " + defensesPicked.get(i) + " more than once!", Toast.LENGTH_SHORT).show();
                    checkDefensesPicked.add(defensesPicked.get(i));
                }
            }
            if (checkDefensesPicked.size() > 0){
                Toast.makeText(this, "input different defenses", Toast.LENGTH_SHORT);
            }
            else if (defensesPicked.size() < 4) {
                Toast.makeText(this, "Input four defenses!!", Toast.LENGTH_LONG).show();
            }
            else if(defensesPicked.contains("pc") && defensesPicked.contains("cdf")){
                Toast.makeText(this, "PC and CDF can't exist together!", Toast.LENGTH_LONG).show();
            }
            else if(defensesPicked.contains("db") && defensesPicked.contains("sp")){
                Toast.makeText(this, "DB and SP can't exist together!", Toast.LENGTH_LONG).show();
            }
            else if(defensesPicked.contains("rt") && defensesPicked.contains("rw")){
                Toast.makeText(this, "RT and RW can't exist together!", Toast.LENGTH_LONG).show();
            }
            else if(defensesPicked.contains("rp") && defensesPicked.contains("mt")){
                Toast.makeText(this, "RP and MT can't exist together!", Toast.LENGTH_LONG).show();
            }
            else if(defensesPicked.size() == 4) {
                String split[] = alliance.split(" ");
                final String allianceColor = split[0].toLowerCase();
                new Thread() {
                    @Override
                    public void run() {
                        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceColor + "DefensePositions").child("0").setValue("lb");
                        for(int i =0; i < 4; i++){
                            firebaseRef.child("/Matches").child(numberOfMatch).child(allianceColor + "DefensePositions").child(Integer.toString(i + 1)).setValue(defensesPicked.get(i));
                        }
                    }
                }.start();
                    next.putExtra("matchNumber", numberOfMatch);
                    next.putExtra("teamNumberOne", teamOneNumber);
                    next.putExtra("teamNumberTwo", teamTwoNumber);
                    next.putExtra("teamNumberThree", teamThreeNumber);
                    next.putExtra("firstDefensePicked", defensesPicked.get(0));
                    Log.e("first defense", defensesPicked.get(0));
                    next.putExtra("secondDefensePicked", defensesPicked.get(1));
                    Log.e("first defense", defensesPicked.get(1));
                    next.putExtra("thirdDefensePicked", defensesPicked.get(2));
                    Log.e("first defense", defensesPicked.get(2));
                    next.putExtra("fourthDefensePicked", defensesPicked.get(3));
                    Log.e("first defense", defensesPicked.get(3));
                    next.putExtra("alliance", alliance);
                    next.putExtra("dataBaseUrl", dataBaseUrl);
                    next.putExtra("mute", isMute);
                    startActivity(next);
                 } else {
                Log.i("test", "here");
            }

            }
            return super.onOptionsItemSelected(item);
        }

    }




