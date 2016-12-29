package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.Layout;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ScoutingPage extends ActionBarActivity {
    String teamNumberOneNote;
    String teamNumberTwoNote;
    String teamNumberThreeNote;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String firstDefense;
    String secondDefense;
    String thirdDefense;
    String fourthDefense;
    String alliance;
    String teamNote = "";
    String dataBaseUrl;
    String allianceScoreData;
    TextView teamNumberOneTextview;
    TextView teamNumberTwoTextview;
    TextView teamNumberThreeTextview;
    ArrayList<String> defenses;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    ArrayList<String> allTeamNotes;
    Boolean breached = false;
    Boolean captured = false;
    Boolean isMute;
    JSONObject object;
    Intent next;
//    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        next = getIntent();
        object = new JSONObject();
        allTeamNotes = new ArrayList<>();
        getExtrasForScouting();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.super_scouting_panel, null);

//        dataBase = FirebaseDatabase.getInstance().getReference();
        setPanels();
        defenses = new ArrayList<>(Arrays.asList(firstDefense, secondDefense, thirdDefense, fourthDefense));
        initializeTeamTextViews();
        setTeamListeners();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finaldata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.getAllianceScore) {
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
        if (id == R.id.scoutDidBreach) {
            if (item.getTitle().equals("Breached")) {
                item.setTitle("didBreach?");
                breached = false;
            } else {
                item.setTitle("Breached");
                breached = true;
            }
        }
        if (id == R.id.scoutDidCapture) {
            if (item.getTitle().equals("Captured")) {
                item.setTitle("didCapture?");
                captured = false;
            } else {
                item.setTitle("Captured");
                captured = true;
            }
        }
        if (id == R.id.finalNext) {
            final SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
            final SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
            final SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
            listDataValues();
            //new added code
            new Thread() {
                @Override
                public void run() {
                    try {

                        /*for (int i = 0; i < panelOne.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "4");
                            dataBase.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child(teamOneDataName.get(i)).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                        }
                        for (int i = 0; i < panelTwo.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "5");
                            dataBase.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child(teamTwoDataName.get(i)).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                        }
                        for (int i = 0; i < panelThree.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "6");
                            dataBase.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child(teamThreeDataName.get(i)).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
                        }*/
//                    } catch (DatabaseException FBE) {

                    } catch (IndexOutOfBoundsException IOB) {

                    }
                }
            }.start();
            //New Added Code//

            sendExtras();

        }

        return super.onOptionsItemSelected(item);
    }

    public void sendNotes(final String teamNumber, final String note) {

    }

    public void getExtrasForScouting() {

        numberOfMatch = next.getExtras().getString("matchNumber");
        teamNumberOne = next.getExtras().getString("teamNumberOne");
        teamNumberTwo = next.getExtras().getString("teamNumberTwo");
        teamNumberThree = next.getExtras().getString("teamNumberThree");
        alliance = next.getExtras().getString("alliance");
        isMute = next.getExtras().getBoolean("mute");
    }

    public void setPanels() {

        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        panelOne.setAllianceColor(SuperScoutApplication.isRed);
        panelOne.setTeamNumber(teamNumberOne);
        panelTwo.setAllianceColor(SuperScoutApplication.isRed);
        panelTwo.setTeamNumber(teamNumberTwo);
        panelThree.setAllianceColor(SuperScoutApplication.isRed);
        panelThree.setTeamNumber(teamNumberThree);
    }

    public void sendExtras() {
        Intent intent = new Intent(this, FinalDataPoints.class);
        intent.putExtra("matchNumber", numberOfMatch);
        intent.putExtra("teamNumberOne", teamNumberOne);
        intent.putExtra("teamNumberTwo", teamNumberTwo);
        intent.putExtra("teamNumberThree", teamNumberThree);
        intent.putExtra("alliance", alliance);

        intent.putExtra("allianceScore", allianceScoreData);
        intent.putExtra("scoutDidBreach", breached);

        startActivity(intent);
    }

    public void listDataValues() {
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);

        teamOneDataName = new ArrayList<>(panelOne.getData().keySet());
        teamTwoDataName = new ArrayList<>(panelTwo.getData().keySet());
        teamThreeDataName = new ArrayList<>(panelThree.getData().keySet());
        teamOneDataScore = new ArrayList<>();
        teamTwoDataScore = new ArrayList<>();
        teamThreeDataScore = new ArrayList<>();

        for (int i = 0; i < teamOneDataName.size(); i++) {
            teamOneDataScore.add(panelOne.getData().get(teamOneDataName.get(i)).toString());
        }
        for (int i = 0; i < teamTwoDataName.size(); i++) {
            teamTwoDataScore.add(panelTwo.getData().get(teamTwoDataName.get(i)).toString());
        }
        for (int i = 0; i < teamThreeDataName.size(); i++) {
            teamThreeDataScore.add(panelThree.getData().get(teamThreeDataName.get(i)).toString());
        }

    }

    public void setTeamClickedListener(final TextView textview) {
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ScoutingPage.this);
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
                dialog.setContentView(dialogView);
                final EditText note = (EditText) dialogView.findViewById(R.id.note);
                dialog.setTitle("Team " + textview.getText().toString() + " Note:");

                note.setText(teamNote);

                Button ok = (Button) dialogView.findViewById(R.id.OKButton);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String teamNumber = textview.getText().toString();
                        teamNote = note.getText().toString();
                        sendNotes(teamNumber, teamNote);
                        Toast.makeText(getApplicationContext(), "Note Sent", Toast.LENGTH_SHORT).show();
                        if (textview == teamNumberOneTextview) {
                            teamNumberOneNote = teamNote;
                        } else if (textview == teamNumberTwoTextview) {
                            teamNumberTwoNote = teamNote;
                        } else if(textview == teamNumberThreeTextview){
                            teamNumberThreeNote = teamNote;
                        }
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
                dialog.show();
            }
        });
    }
    public void initializeTeamTextViews(){
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        teamNumberOneTextview = (TextView)panelOne.getView().findViewById(R.id.teamNumberTextView);
        teamNumberTwoTextview = (TextView)panelTwo.getView().findViewById(R.id.teamNumberTextView);
        teamNumberThreeTextview = (TextView)panelThree.getView().findViewById(R.id.teamNumberTextView);
    }
    public void setTeamListeners(){
        setTeamClickedListener(teamNumberOneTextview);
        setTeamClickedListener(teamNumberTwoTextview);
        setTeamClickedListener(teamNumberThreeTextview);
    }
}



