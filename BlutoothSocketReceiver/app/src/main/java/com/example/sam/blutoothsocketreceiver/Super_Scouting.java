package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Super_Scouting extends ActionBarActivity {
    TextView teamNumber1;
    TextView teamNumber2;
    TextView teamNumber3;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String firstDefense;
    String secondDefense;
    String thirdDefense;
    String fourthDefense;
    String alliance;
    String teamOneNote = "";
    String teamTwoNote = "";
    String teamThreeNote = "";
    String dataBaseUrl;
    String allianceScoreData;
    String chosenCatergoryADefense;
    ArrayList<String> defenses;
    ArrayList<String> dataScore;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    ArrayList<String> data;
    ArrayList<String> teamOneDefenseARanks;
    ArrayList<String> teamTwoDefenseARanks;
    ArrayList<String> teamThreeDefenseARanks;
    Boolean breached = false;
    Boolean captured = false;
    Boolean isMute;
    JSONObject object;
    Intent next;
    Firebase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        next = getIntent();
        object = new JSONObject();
        getExtrasForScouting();
        //Get authorized to use the database

        Log.e("Super Scouting", dataBaseUrl);
        dataBase = new Firebase(dataBaseUrl);
        teamNumber1 = (TextView) findViewById(R.id.team1);
        teamNumber2 = (TextView) findViewById(R.id.team2);
        teamNumber3 = (TextView) findViewById(R.id.team3);
        //set team numbers and their alliance colors on the top
        if (alliance.equals("Blue Alliance")) {
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.BLUE);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.BLUE);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.BLUE);
        } else if (alliance.equals("Red Alliance")) {
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.RED);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.RED);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.RED);
        }
        defenses = new ArrayList<>(Arrays.asList(firstDefense, secondDefense, thirdDefense, fourthDefense));
        dataScore = new ArrayList<>();
        teamOneDataName = new ArrayList<>();
        teamOneDataScore = new ArrayList<>();
        teamTwoDataName = new ArrayList<>();
        teamTwoDataScore = new ArrayList<>();
        teamThreeDataName = new ArrayList<>();
        teamThreeDataScore = new ArrayList<>();
        teamOneDefenseARanks = new ArrayList<>();
        teamTwoDefenseARanks = new ArrayList<>();
        teamThreeDefenseARanks = new ArrayList<>();
        data = new ArrayList<>(Arrays.asList("Speed", "Torque", "Defense", "Agility", "Ball Control"));

        setUpDataRanking();

    }

    //Dialog pops up and lets the user input notes for each team
    public void teamOneNoteClick(View view) {

        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText note = (EditText) dialogView.findViewById(R.id.note);
        note.setText(teamOneNote);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamOneNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberOne + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public void teamTwoNoteClick(View view) {
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText note = (EditText) dialogView.findViewById(R.id.note);
        note.setText(teamTwoNote);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTwoNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberTwo + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public void teamThreeNoteClick(View view) {
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText note = (EditText) dialogView.findViewById(R.id.note);
        note.setText(teamThreeNote);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamThreeNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberThree + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }

    //warns the user that going back will change data
    @Override
    public void onBackPressed() {
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

    //a view that will be added in a loop
    private View createCounter(String title) {

        LayoutInflater inflater = getLayoutInflater();
        View counter = inflater.inflate(R.layout.counter, null);
        TextView dataNameTextView = (TextView) counter.findViewById(R.id.dataName);
        dataNameTextView.setText(title);
        final TextView incrementor = (TextView) counter.findViewById(R.id.scoreCounter);
        Button plusButton = (Button) counter.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current++;
                if (current > 4) {
                    incrementor.setText(Integer.toString(4));
                } else {
                    incrementor.setText(Integer.toString(current));
                }
            }
        });
        Button minusButton = (Button) counter.findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current--;
                if (current < 0) {
                    incrementor.setText(Integer.toString(0));
                } else {
                    incrementor.setText(Integer.toString(current));
                }
            }
        });
        return counter;
    }
    //Added Stuff
    private View createDefenseACounter() {
        LayoutInflater inflater = getLayoutInflater();
        View counter = inflater.inflate(R.layout.defense_a_counter, null);
        final TextView rankOneIncrementor = (TextView) counter.findViewById(R.id.rankOneCounter);
        final TextView rankTwoIncrementor = (TextView) counter.findViewById(R.id.rankTwoCounter);
        final TextView rankThreeIncrementor = (TextView) counter.findViewById(R.id.rankThreeCounter);

        Button rankOne = (Button) counter.findViewById(R.id.rank1);
        Button rankTwo = (Button) counter.findViewById(R.id.rank2);
        Button rankThree = (Button) counter.findViewById(R.id.rank3);

        TextView defenseAName = (TextView) counter.findViewById(R.id.categoryADefenseName);
        ArrayList<String> defenses = new ArrayList<>(Arrays.asList(firstDefense, secondDefense, thirdDefense, fourthDefense));
        if (defenses.contains("pc")){
            chosenCatergoryADefense = "PC";
        }else if(defenses.contains("cdf")){
            chosenCatergoryADefense = "CDF";
        }
        defenseAName.setText(chosenCatergoryADefense);

        setIncrementor(rankOneIncrementor, rankOne);
        setIncrementor(rankTwoIncrementor, rankTwo);
        setIncrementor(rankThreeIncrementor, rankThree);

        setDecrementor(rankOneIncrementor, rankOne);
        setDecrementor(rankTwoIncrementor, rankTwo);
        setDecrementor(rankThreeIncrementor, rankThree);

        return counter;
    }
    public void setIncrementor(final TextView view, Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(view.getText().toString());
                current++;
                view.setText(Integer.toString(current));
            }
        });
    }
    public void setDecrementor(final TextView view, Button button){
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int current = Integer.parseInt(view.getText().toString());
                current--;
                if (current < 0) {
                    view.setText(Integer.toString(0));
                } else {
                    view.setText(Integer.toString(current));
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finaldata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.getAllianceScore){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Input Alliance Score: ");
            final EditText input = new EditText(this);
            input.setText(allianceScoreData);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setGravity(1);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    allianceScoreData = input.getText().toString();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        if(id == R.id.scoutDidBreach){
            if(item.getTitle().equals("Breached")){
                item.setTitle("didBreach?");
                breached = false;
            }else{
                item.setTitle("Breached");
                breached = true;
            }
        }
        if(id == R.id.scoutDidCapture){
            if(item.getTitle().equals("Captured")){
                item.setTitle("didCapture?");
                captured = false;
            }else{
                item.setTitle("Captured");
                captured = true;
            }
        }
        if (id == R.id.finalNext) {
            Intent intent = new Intent(this, FinalDataPoints.class);
            intent.putExtra("matchNumber", numberOfMatch);
            intent.putExtra("teamNumberOne", teamNumberOne);
            intent.putExtra("teamNumberTwo", teamNumberTwo);
            intent.putExtra("teamNumberThree", teamNumberThree);
            intent.putExtra("firstDefensePicked", firstDefense);
            intent.putExtra("secondDefensePicked", secondDefense);
            intent.putExtra("thirdDefensePicked", thirdDefense);
            intent.putExtra("fourthDefensePicked", fourthDefense);
            intent.putExtra("alliance", alliance);
            intent.putExtra("teamOneNote", teamOneNote);
            intent.putExtra("teamTwoNote", teamTwoNote);
            intent.putExtra("teamThreeNote", teamThreeNote);
            intent.putExtra("dataBaseUrl", dataBaseUrl);
            intent.putExtra("allianceScore", allianceScoreData);
            intent.putExtra("scoutDidBreach", breached);
            intent.putExtra("scoutDidCapture", captured);
            intent.putExtra("mute", isMute);

            getEachDataNameAndValue();

            intent.putStringArrayListExtra("dataNameOne", teamOneDataName);
            intent.putStringArrayListExtra("ranksOfOne", teamOneDataScore);
            intent.putStringArrayListExtra("dataNameTwo", teamTwoDataName);
            intent.putStringArrayListExtra("ranksOfTwo", teamTwoDataScore);
            intent.putStringArrayListExtra("dataNameThree", teamThreeDataName);
            intent.putStringArrayListExtra("ranksOfThree", teamThreeDataScore);
            intent.putStringArrayListExtra("teamOneDefenseARanks", teamOneDefenseARanks);
            intent.putStringArrayListExtra("teamTwoDefenseARanks", teamTwoDefenseARanks);
            intent.putStringArrayListExtra("teamThreeDefenseARanks", teamThreeDefenseARanks);
            if(!teamOneNote.equals("")) {
                sendNotes(teamNumberOne, teamOneNote);
            }else {
                sendNotes(teamNumberOne, "No_Notes");
            }
            if(!teamTwoNote.equals("")) {
                sendNotes(teamNumberTwo, teamTwoNote);
            }else {
                sendNotes(teamNumberTwo, "No_Notes");
            }
            if(!teamThreeNote.equals("")) {
                sendNotes(teamNumberThree, teamThreeNote);
            }else {
                sendNotes(teamNumberThree, "No_Notes");
            }
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendNotes(final String teamNumber, final String note){
        new Thread(){
            public void run(){
                dataBase.child("TeamInMatchDatas").child(teamNumber + "Q" + numberOfMatch).child("superNotes").setValue(note);
            }
        }.start();
    }
//Get all the data names and their values
    public void getEachDataNameAndValue() {
        LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
        LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
        LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);
        //Added Stuff
        View defenseATeamOneId = teamOneRelativeLayout.getChildAt(teamOneRelativeLayout.getChildCount() - 1);
        View defenseATeamTwoId = teamTwoRelativeLayout.getChildAt(teamTwoRelativeLayout.getChildCount() - 1);
        View defenseATeamThreeId = teamThreeRelativeLayout.getChildAt(teamThreeRelativeLayout.getChildCount() - 1);
        //Added Stuff
        TextView rankOneIncrementor1 = (TextView) defenseATeamOneId.findViewById(R.id.rankOneCounter);
        TextView rankTwoIncrementor1 = (TextView) defenseATeamOneId.findViewById(R.id.rankTwoCounter);
        TextView rankThreeIncrementor1 = (TextView) defenseATeamOneId.findViewById(R.id.rankThreeCounter);
        TextView rankOneIncrementor2 = (TextView) defenseATeamTwoId.findViewById(R.id.rankOneCounter);
        TextView rankTwoIncrementor2 = (TextView) defenseATeamTwoId.findViewById(R.id.rankTwoCounter);
        TextView rankThreeIncrementor2 = (TextView) defenseATeamTwoId.findViewById(R.id.rankThreeCounter);
        TextView rankOneIncrementor3 = (TextView) defenseATeamThreeId.findViewById(R.id.rankOneCounter);
        TextView rankTwoIncrementor3 = (TextView) defenseATeamThreeId.findViewById(R.id.rankTwoCounter);
        TextView rankThreeIncrementor3 = (TextView) defenseATeamThreeId.findViewById(R.id.rankThreeCounter);

        teamOneDataName.clear();
        teamOneDataScore.clear();
        teamTwoDataName.clear();
        teamTwoDataScore.clear();
        teamThreeDataName.clear();
        teamThreeDataScore.clear();
        teamOneDefenseARanks.clear();
        teamTwoDefenseARanks.clear();
        teamThreeDefenseARanks.clear();
        //Added "-1"
        for (int i = 0; i < teamOneRelativeLayout.getChildCount() - 1; i++) {
            View teamOneLayout = teamOneRelativeLayout.getChildAt(i);
            TextView nameOfData1 = (TextView) teamOneLayout.findViewById(R.id.dataName);
            TextView scoreOfData1 = (TextView) teamOneLayout.findViewById(R.id.scoreCounter);
            teamOneDataName.add(("rank" + (nameOfData1.getText().toString())).replace(" ", ""));
            teamOneDataScore.add(scoreOfData1.getText().toString());
        }
        Log.e("TeamOneDataNames", teamOneDataName.toString());
        Log.e("TeamOneDataScore", teamOneDataScore.toString());
        //Added Stuff
        teamOneDefenseARanks.add(rankOneIncrementor1.getText().toString());
        teamOneDefenseARanks.add(rankTwoIncrementor1.getText().toString());
        teamOneDefenseARanks.add(rankThreeIncrementor1.getText().toString());
        Log.e("teamOneDefenseARanks", teamOneDefenseARanks.toString());

        for (int j = 0; j < teamTwoRelativeLayout.getChildCount() - 1; j++) {
            View teamTwoLayout = teamTwoRelativeLayout.getChildAt(j);
            TextView nameOfData2 = (TextView) teamTwoLayout.findViewById(R.id.dataName);
            TextView scoreOfData2 = (TextView) teamTwoLayout.findViewById(R.id.scoreCounter);
            teamTwoDataName.add(("rank" + (nameOfData2.getText().toString())).replace(" ", ""));
            teamTwoDataScore.add(scoreOfData2.getText().toString());
        }
        //Added Stuff
        teamTwoDefenseARanks.add(rankOneIncrementor2.getText().toString());
        teamTwoDefenseARanks.add(rankTwoIncrementor2.getText().toString());
        teamTwoDefenseARanks.add(rankThreeIncrementor2.getText().toString());
        Log.e("teamTwoDefenseARanks", teamTwoDefenseARanks.toString());


        for (int k = 0; k < teamThreeRelativeLayout.getChildCount() - 1; k++) {
            View teamThreeLayout = teamThreeRelativeLayout.getChildAt(k);
            TextView nameOfData3 = (TextView) teamThreeLayout.findViewById(R.id.dataName);
            TextView scoreOfData3 = (TextView) teamThreeLayout.findViewById(R.id.scoreCounter);
            teamThreeDataName.add(("rank" + (nameOfData3.getText().toString())).replace(" ", ""));
            teamThreeDataScore.add(scoreOfData3.getText().toString());
        }
        //Added Stuff
        teamThreeDefenseARanks.add(rankOneIncrementor3.getText().toString());
        teamThreeDefenseARanks.add(rankTwoIncrementor3.getText().toString());
        teamThreeDefenseARanks.add(rankThreeIncrementor3.getText().toString());
        Log.e("teamThreeDefenseARanks", teamThreeDefenseARanks.toString());

    }
    //sets up the counters to add value
    public void setUpDataRanking(){
        LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
        LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
        LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);
        //Added Stuff
        View defenseACounterTeamOne = createDefenseACounter();
        View defenseACounterTeamTwo = createDefenseACounter();
        View defenseACounterTeamThree = createDefenseACounter();
        //

        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(1 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamOneRelativeLayout.addView(counter);
        }
        //Added Stuff
        LinearLayout.LayoutParams paramForTeamOne = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
        defenseACounterTeamOne.setLayoutParams(paramForTeamOne);
        teamOneRelativeLayout.addView(defenseACounterTeamOne);

        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(2 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamTwoRelativeLayout.addView(counter);
        }
        //Added Stuff
        LinearLayout.LayoutParams paramForTeamTwo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
        defenseACounterTeamTwo.setLayoutParams(paramForTeamTwo);
        teamTwoRelativeLayout.addView(defenseACounterTeamTwo);

        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(3 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamThreeRelativeLayout.addView(counter);
        }
        //Added Stuff
        LinearLayout.LayoutParams paramForTeamThree = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
        defenseACounterTeamThree.setLayoutParams(paramForTeamThree);
        teamThreeRelativeLayout.addView(defenseACounterTeamThree);
    }
    public void getExtrasForScouting(){

        numberOfMatch = next.getExtras().getString("matchNumber");
        teamNumberOne = next.getExtras().getString("teamNumberOne");
        teamNumberTwo = next.getExtras().getString("teamNumberTwo");
        teamNumberThree = next.getExtras().getString("teamNumberThree");
        alliance = next.getExtras().getString("alliance");
        firstDefense = next.getExtras().getString("firstDefensePicked");
        secondDefense = next.getExtras().getString("secondDefensePicked");
        thirdDefense = next.getExtras().getString("thirdDefensePicked");
        fourthDefense = next.getExtras().getString("fourthDefensePicked");
        dataBaseUrl = next.getExtras().getString("dataBaseUrl");
        isMute = next.getExtras().getBoolean("mute");
    }
}



