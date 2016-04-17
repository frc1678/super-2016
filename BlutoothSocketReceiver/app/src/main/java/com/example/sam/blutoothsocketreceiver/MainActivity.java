package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.firebase.client.Firebase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    protected SuperScoutApplication app;
    Activity context;
    EditText numberOfMatch;
    EditText teamNumberOne;
    EditText teamNumberTwo;
    EditText teamNumberThree;
    EditText searchBar;
    TextView alliance;
    Boolean isRed = false;
    Integer matchNumber = 0;
    Firebase dataBase;
    String firstKey;
    String keys;
    String scoutAlliance;
    String changeAllianceScore;
    String previousScore;
    final static String dataBaseUrl = Constants.dataBaseUrl;
    int matchNum;
    int stringIndex;
    int intIndex;
    ArrayList<String> keysInKey;
    ArrayList<String> valueOfKeys;
    ArrayList<String> checkNumKeys;
    ArrayList<String> checkStringKeys;
    ArrayList<String> defenses;
    JSONObject jsonUnderKey;
    JSONArray successDefenseTele;
    JSONArray failedDefenseTele;
    JSONArray successDefenseAuto;
    JSONArray failedDefenseAuto;
    private boolean scoutOrSuperFiles;
    boolean isMute = false;
    ToggleButton mute;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("test", "Logcat is up and running!");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        //app = (SuperScoutApplication)getApplication();
        //Start the class that continuosly accepts connection from scout
        accept_loop loop = new accept_loop(context, dataBaseUrl);
        loop.start();
        Intent backToHome = getIntent();
        numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        teamNumberOne = (EditText) findViewById(R.id.teamOneNumber);
        teamNumberTwo = (EditText) findViewById(R.id.teamTwoNumber);
        teamNumberThree = (EditText) findViewById(R.id.teamThreeNumber);
        mute = (ToggleButton) findViewById(R.id.mute);
        alliance = (TextView) findViewById(R.id.allianceName);
        jsonUnderKey = new JSONObject();
        dataBase = new Firebase(dataBaseUrl);
        //If got intent from the last activity
        if (backToHome.hasExtra("number")) {
            matchNumber = Integer.parseInt(backToHome.getExtras().getString("number")) + 1;
        } else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            matchNumber = prefs.getInt("match_number", 1);
        }
        if (backToHome.hasExtra("shouldBeRed")) {
            isRed = getIntent().getBooleanExtra("shouldBeRed", false);
        } else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            isRed = prefs.getBoolean("allianceColor", false);
        }
        if (!backToHome.hasExtra("mute")) {
            mute.setChecked(false);
        } else if (backToHome.hasExtra("mute")) {
            mute.setChecked(true);
        }
        updateUI();
        numberOfMatch.setText(matchNumber.toString());
        matchNumber = Integer.parseInt(numberOfMatch.getText().toString());

        disenableEditTextEditing();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.view_files_received);
        listView.setAdapter(adapter);
        updateListView();

        scoutOrSuperFiles = true;
        updateListView();


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    updateUI();
                } catch (NullPointerException NPE) {
                    toasts("Teams not available", true);
                }
            }
        }, new IntentFilter("matches_updated"));

        //Change team numbers as the user changes the match number
        changeTeamsByMatchName();
        commitSharedPreferences();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                if (scoutOrSuperFiles) {
                    name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name;
                } else {
                    name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data/" + name;
                }
                final String fileName = name;
                final String[] nameOfResendMatch = name.split("Q");
                new AlertDialog.Builder(context)
                        .setTitle("RESEND DATA?")
                        .setMessage("RESEND " + "Q" + nameOfResendMatch[1] + "?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (scoutOrSuperFiles) {
                                    String content = readFile(fileName);
                                    JSONObject superData;
                                    try {
                                        superData = new JSONObject(content);
                                    } catch (JSONException jsone) {
                                        Log.e("File Error", "no valid JSON in the file");
                                        Toast.makeText(context, "Not a valid JSON", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    List<JSONObject> dataPoints = new ArrayList<>();
                                    dataPoints.add(superData);
                                    resendSuperData(dataPoints);
                                } else {
                                    String content = readFile(fileName);
                                    JSONObject data;
                                    try {
                                        data = new JSONObject(content);
                                    } catch (JSONException jsone) {
                                        Log.e("File Error", "no valid JSON in the file");
                                        Toast.makeText(context, "Not a valid JSON", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    List<JSONObject> dataPoints = new ArrayList<>();
                                    dataPoints.add(data);
                                    resendScoutData(dataPoints);
                                }
                            }
                        }).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String name = parent.getItemAtPosition(position).toString();
                String splitName[] = name.split("_");
                final String editMatchNumber = splitName[0].replace("Q", "");
                Log.e("matchNameChange", editMatchNumber);
                String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name;
                String content = readFile(filePath);
                final JSONObject superData;
                try {
                    superData = new JSONObject(content);
                    if(isRed) {
                        previousScore = superData.get("Red Alliance Score").toString();
                    }else{
                        previousScore = superData.get("Blue Alliance Score").toString();
                        Log.e("score to change", previousScore);
                    }
                }catch (JSONException JE){
                    Log.e("read Super Data", "failed");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Alliance Score for " + name + ": ");
                final EditText input = new EditText(context);
                input.setText(previousScore);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setGravity(1);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        previousScore = input.getText().toString();
                        if (isRed) {
                            dataBase.child("Matches").child(editMatchNumber).child("redScore").setValue(Integer.parseInt(previousScore));
                        } else {
                            dataBase.child("Matches").child(editMatchNumber).child("blueScore").setValue(Integer.parseInt(previousScore));
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

//resends all data on the currently viewed list of data
    public void resendAllClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("RESEND ALL?")
                .setMessage("RESEND ALL DATA?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        List<JSONObject> dataPoints = new ArrayList<>();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            String content;
                            if (scoutOrSuperFiles) {
                                String name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + adapter.getItem(i);
                                content = readFile(name);
                            } else {
                                String name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data/" + adapter.getItem(i);
                                content = readFile(name);
                            }
                            if (content != null) {
                                try {
                                    JSONObject data = new JSONObject(content);
                                    dataPoints.add(data);
                                    //we dont implement this because we dont care if one file is not JSON object
                                } catch (JSONException jsone) {
                                    Log.i("JSON info", "Failed to parse JSON for resend all. unimportant");
                                }
                            }
                        }
                        if (scoutOrSuperFiles) {
                            resendSuperData(dataPoints);
                        } else {
                            resendScoutData(dataPoints);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getScoutData(View view) {
        searchBar = (EditText) findViewById(R.id.searchEditText);
        searchBar.setFocusable(false);
        scoutOrSuperFiles = false;
        //listenForFileListClick();
        updateListView();
        searchBar.setFocusableInTouchMode(true);
    }

    public void getSuperData(View view) {
        searchBar = (EditText) findViewById(R.id.searchEditText);
        searchBar.setFocusable(false);
        scoutOrSuperFiles = true;
        //listenForFileListClick();
        updateListView();
        searchBar.setFocusableInTouchMode(true);
    }

    public void catClicked(View view){
        if(mute.isChecked()){
           //Don't Do anything
            isMute = true;
        }else {
            isMute = false;
            int randNum = (int) (Math.random() * 3);
            playSound(randNum);
            Log.e("number", randNum + "");
            Log.e("cat", "sound");
        }
    }
    public void playSound(int playTrak){
        if (playTrak == 0){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.catsound);
            mp.start();
        }else if(playTrak == 1){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.catsound2);
            mp.start();
        }else if(playTrak == 2){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.dog);
            mp.start();
        }else if(playTrak == 3){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.kittenmeow);
            mp.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.changeAlliance) {
            isRed = !isRed;
            commitSharedPreferences();
            updateUI();
        }
        if (id == R.id.scout) {
            if (!FirebaseLists.matchesList.getKeys().contains(matchNumber.toString())){
                Toast.makeText(context, "This Match Does Not Exist!", Toast.LENGTH_LONG).show();
                disenableEditTextEditing();
            }else{
                if (numberOfMatch.getText().toString().equals("")) {
                    Toast.makeText(context, "Input match name!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberOne.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team one number!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberTwo.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team two number!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberThree.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team three number!", Toast.LENGTH_SHORT).show();
                } else if(teamNumberOne.getText().toString().equals("Not Available")){
                    Toast.makeText(context, "This Match Does Not Exist!", Toast.LENGTH_SHORT).show();
                }
                else {
                    commitSharedPreferences();
                    Intent intent = new Intent(context, FieldSetUp.class);
                    intent.putExtra("matchNumber", numberOfMatch.getText().toString());
                    intent.putExtra("teamNumberOne", teamNumberOne.getText().toString());
                    intent.putExtra("teamNumberTwo", teamNumberTwo.getText().toString());
                    intent.putExtra("teamNumberThree", teamNumberThree.getText().toString());
                    intent.putExtra("alliance", alliance.getText().toString());
                    intent.putExtra("dataBaseUrl", dataBaseUrl);
                    intent.putExtra("mute", isMute);
                    Log.e("start alliance", alliance.getText().toString());
                    startActivity(intent);
                }
            }

        } else if (id == R.id.action_override) {
            if (item.getTitle().toString().equals("Override Match and Team Number")) {
                enableEditTextEditing();
                item.setTitle("Automate");
            } else if (item.getTitle().toString().equals("Automate")) {
                View view = context.getCurrentFocus();
                updateUI();
                commitSharedPreferences();
                disenableEditTextEditing();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                item.setTitle("Override Match and Team Number");
            }

        }
            return super.onOptionsItemSelected(item);
    }


    public void updateListView() {
        final EditText searchBar = (EditText)findViewById(R.id.searchEditText);
        final File dir;
        if (scoutOrSuperFiles) {
            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
        } else {
            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data");
        }
        if (!dir.mkdir()) {
            Log.i("File Info", "Failed to make Directory. Unimportant");
        }
        final File[] files = dir.listFiles();
        adapter.clear();
        for (File tmpFile : files) {
            adapter.add(tmpFile.getName());
        }
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (searchBar.getText().toString().equals("")){
                    adapter.clear();
                    searchBar.setFocusable(false);
                    for (File tmpFile : files) {
                        adapter.add(tmpFile.getName());
                    }
                    searchBar.setFocusableInTouchMode(true);
                    adapter.sort(new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            File lhsFile = new File(dir, lhs);
                            File rhsFile = new File(dir, rhs);
                            Date lhsDate = new Date(lhsFile.lastModified());
                            Date rhsDate = new Date(rhsFile.lastModified());
                            return rhsDate.compareTo(lhsDate);
                        }
                    });
                }else{
                    for (int i = 0; i < adapter.getCount();){
                        if(adapter.getItem(i).startsWith((searchBar.getText().toString()).toUpperCase()) || adapter.getItem(i).contains((searchBar.getText().toString()).toUpperCase())){
                            i++;
                        }else{
                            adapter.remove(adapter.getItem(i));
                        }
                    }
                }
            }
        });
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                File lhsFile = new File(dir, lhs);
                File rhsFile = new File(dir, rhs);
                Date lhsDate = new Date(lhsFile.lastModified());
                Date rhsDate = new Date(rhsFile.lastModified());
                return rhsDate.compareTo(lhsDate);
            }
        });
        adapter.notifyDataSetChanged();
    }
//updates the team numbers in the front screen according to the match number and the alliance;
    private void updateUI() {
        try {
            if (FirebaseLists.matchesList.getKeys().contains(matchNumber.toString())) {
                Match match = FirebaseLists.matchesList.getFirebaseObjectByKey(matchNumber.toString());

                List<Integer> teamsOnAlliance = new ArrayList<>();
                teamsOnAlliance.addAll((isRed) ? match.redAllianceTeamNumbers : match.blueAllianceTeamNumbers);
                alliance.setTextColor((isRed) ? Color.RED : Color.BLUE);
                alliance.setText((isRed) ? "Red Alliance" : "Blue Alliance");

                teamNumberOne.setText(teamsOnAlliance.get(0).toString());
                teamNumberTwo.setText(teamsOnAlliance.get(1).toString());
                teamNumberThree.setText(teamsOnAlliance.get(2).toString());

            } else {
                teamNumberOne.setText("Not Available");
                teamNumberTwo.setText("Not Available");
                teamNumberThree.setText("Not Available");

            }
        }catch(NullPointerException NPE){
            toasts("Teams not available", true);
        }
    }

    public void commitSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("match_number", matchNumber);
        editor.putBoolean("allianceColor", isRed);
        editor.commit();
    }

    //changes the team numbers while the user changes the match number
    public void changeTeamsByMatchName() {
        EditText numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        numberOfMatch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    matchNumber = Integer.parseInt(s.toString());
                } catch (NumberFormatException NFE) {
                    matchNumber = 0;
                }
                updateUI();
            }
        });
    }

    public void enableEditTextEditing() {

        numberOfMatch.setFocusableInTouchMode(true);
        teamNumberOne.setFocusableInTouchMode(true);
        teamNumberTwo.setFocusableInTouchMode(true);
        teamNumberThree.setFocusableInTouchMode(true);
    }

    public void disenableEditTextEditing() {

        numberOfMatch.setFocusable(false);
        teamNumberOne.setFocusable(false);
        teamNumberTwo.setFocusable(false);
        teamNumberThree.setFocusable(false);
    }
//reads the data of the clicked file
    public String readFile(String name) {
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(name))));
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Open File");
            Toast.makeText(context, "Failed To Open File", Toast.LENGTH_LONG).show();
            return null;
        }
        String dataOfFile = "";
        String buf;
        try {
            while ((buf = file.readLine()) != null) {
                dataOfFile = dataOfFile.concat(buf + "\n");
            }
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Read From File");
            Toast.makeText(context, "Failed To Read From File", Toast.LENGTH_LONG).show();
            return null;
        }
        Log.i("fileData", dataOfFile);
        return dataOfFile;
    }

//converts jsonArrays to arrays
    public List<Object> jsonArrayToArray(JSONArray array) {
        List<Object> os = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                os.add(array.get(i));
            } catch (Exception e) {
                //do nothing
            }
        }
        return os;
    }

    public void resendSuperData(final List<JSONObject> dataPoints) {
        new Thread() {
            @Override
            public void run() {
                //read data from file
                for (int j = 0; j < dataPoints.size(); j++) {

                    Log.e("Beginning", "Test here");
                    Log.e("Test 1", "super file is not null!");
                    try {
                        Log.e("Test 2", "assign file data to Json");
                        JSONObject superData = dataPoints.get(j);
                        defenses = new ArrayList<>(Arrays.asList("defenseOne", "defenseTwo", "defenseThree", "defenseFour"));
                        String matchNum = superData.get("matchNumber").toString();
                        String matchAndTeamOne = superData.get("teamOne") + "Q" + matchNum;
                        String matchAndTeamTwo = superData.get("teamTwo") + "Q" + matchNum;
                        String matchAndTeamThree = superData.get("teamThree") + "Q" + matchNum;
                        String teamOneNumber = superData.getString("teamOne");
                        String teamTwoNumber = superData.getString("teamTwo");
                        String teamThreeNumber = superData.getString("teamThree");

                        JSONArray teamOneDefenseARanks = superData.getJSONArray("teamOneDefenseARanks");
                        JSONArray teamTwoDefenseARanks = superData.getJSONArray("teamTwoDefenseARanks");
                        JSONArray teamThreeDefenseARanks = superData.getJSONArray("teamThreeDefenseARanks");

                        JSONObject teamOneData = superData.getJSONObject(teamOneNumber);
                        JSONObject teamTwoData = superData.getJSONObject(teamTwoNumber);
                        JSONObject teamThreeData = superData.getJSONObject(teamThreeNumber);

                        JSONObject teamOneKeyNames = new JSONObject(teamOneData.toString());
                        JSONObject teamTwoKeyNames = new JSONObject(teamTwoData.toString());
                        JSONObject teamThreeKeyNames = new JSONObject(teamThreeData.toString());

                        Iterator getTeamOneKeys = teamOneKeyNames.keys();
                        Iterator getTeamTwoKeys = teamTwoKeyNames.keys();
                        Iterator getTeamThreeKeys = teamThreeKeyNames.keys();

                        ArrayList<String> rankNames = new ArrayList<>(Arrays.asList("numTimesBeached", "numTimesSlowed", "numTimesUnaffected"));
                        ArrayList<String> teamNumbers = new ArrayList<>(Arrays.asList(teamOneNumber, teamTwoNumber, teamThreeNumber));
                        for (int i = 0; i < teamNumbers.size(); i++){
                            //Log.e("path", teamNumbers.get(i) + "Q" + numberOfMatch.toString());
                            dataBase.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + matchNum).child("teamNumber").setValue(Integer.parseInt(teamNumbers.get(i)));
                            dataBase.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + matchNum).child("matchNumber").setValue(Integer.parseInt(matchNum));
                        }

                        for (int i = 0; i < 3; i++) {
                            dataBase.child("TeamInMatchDatas").child(teamOneNumber + "Q" + matchNum).child(rankNames.get(i)).setValue(Integer.parseInt((jsonArrayToArray(teamOneDefenseARanks)).get(i).toString()));
                        }
                        for (int i = 0; i < 3; i++) {
                            dataBase.child("TeamInMatchDatas").child(teamTwoNumber + "Q" + matchNum).child(rankNames.get(i)).setValue(Integer.parseInt((jsonArrayToArray(teamTwoDefenseARanks)).get(i).toString()));
                        }
                        for (int i = 0; i < 3; i++) {
                            dataBase.child("TeamInMatchDatas").child(teamThreeNumber + "Q" + matchNum).child(rankNames.get(i)).setValue(Integer.parseInt((jsonArrayToArray(teamThreeDefenseARanks)).get(i).toString()));
                        }
                        while (getTeamOneKeys.hasNext()) {
                            String teamOneKeys = (String) getTeamOneKeys.next();
                            dataBase.child("TeamInMatchDatas").child(matchAndTeamOne).child(teamOneKeys).setValue(Integer.parseInt(teamOneData.get(teamOneKeys).toString()));
                        }
                        while (getTeamTwoKeys.hasNext()) {
                            String teamTwoKeys = (String) getTeamTwoKeys.next();
                            dataBase.child("TeamInMatchDatas").child(matchAndTeamTwo).child(teamTwoKeys).setValue(Integer.parseInt(teamTwoData.get(teamTwoKeys).toString()));
                        }
                        while (getTeamThreeKeys.hasNext()) {
                            String teamThreeKeys = (String) getTeamThreeKeys.next();
                            dataBase.child("TeamInMatchDatas").child(matchAndTeamThree).child(teamThreeKeys).setValue(Integer.parseInt(teamThreeData.get(teamThreeKeys).toString()));
                            Log.e("teamThreeDataName", teamThreeKeys);
                            Log.e("teamThreeValues", teamThreeData.get(teamThreeKeys).toString());
                        }
                        if (!isRed){
                            for (int i = 0; i < defenses.size(); i++){
                                dataBase.child("Matches").child(matchNum).child("blueDefensePositions").child(Integer.toString(i + 1)).setValue(superData.get(defenses.get(i)));
                                Log.e("defense position: " + Integer.toString(i), superData.get(defenses.get(i)).toString());
                                System.out.println(superData);
                            }
                            dataBase.child("Matches").child(matchNum).child("blueDefensePositions").child("0").setValue("lb");
                            dataBase.child("Matches").child(matchNum).child("blueScore").setValue(Integer.parseInt(superData.get("Blue Alliance Score").toString()));
                            dataBase.child("Matches").child(matchNum).child("blueAllianceDidCapture").setValue(superData.get("didCapture"));
                            dataBase.child("Matches").child(matchNum).child("blueAllianceDidBreach").setValue(superData.get("didBreach"));
                        }else{
                            for (int i = 0; i < defenses.size(); i++){
                                dataBase.child("Matches").child(matchNum).child("redDefensePositions").child(Integer.toString(i + 1)).setValue(superData.get(defenses.get(i)));
                            }
                            dataBase.child("Matches").child(matchNum).child("redDefensePositions").child("0").setValue("lb");
                            dataBase.child("Matches").child(matchNum).child("redScore").setValue(Integer.parseInt(superData.get("Red Alliance Score").toString()));
                            dataBase.child("Matches").child(matchNum).child("redAllianceDidCapture").setValue(superData.get("didCapture"));
                            dataBase.child("Matches").child(matchNum).child("redAllianceDidBreach").setValue(superData.get("didBreach"));
                        }

                    } catch (JSONException JE) {
                        Log.e("json error", "failed to get super json");
                    }
                    // new ConnectThread(context, superName, uuid, name, text).start();
                }
                toasts("Resent Super data!", false);
            }
        }.start();
    }

    public void resendScoutData(final List<JSONObject> datapoints) {
        //read data from file
        toasts("Please Wait...Don't do anything", false);
        new Thread() {
            @Override
            public void run() {
                for (int j = 0; j < datapoints.size(); j++) {
                    JSONObject scoutDataJson = datapoints.get(j);
                    System.out.println("scoutDataJson: " + scoutDataJson.toString());
                    Iterator getFirstKey = scoutDataJson.keys();
                    while (getFirstKey.hasNext()) {
                        firstKey = (String) getFirstKey.next();
                        //split first key to get only match number
                        String[] teamAndMatchNumbers = firstKey.split("Q");
                        matchNum = Integer.parseInt(teamAndMatchNumbers[1]);
                        try {
                            jsonUnderKey = scoutDataJson.getJSONObject(firstKey);
                            System.out.println("First Key: " + firstKey);
                            System.out.println(jsonUnderKey.toString());
                        } catch (Exception e) {
                            Log.e("JSON", "Failed to get first key");
                            return;
                        }
                    }
                    try {
                        //get arrays of the keys in the json object
                        keysInKey = new ArrayList<>();
                        JSONObject keyNames = new JSONObject(jsonUnderKey.toString());
                        Iterator getRestOfKeys = keyNames.keys();
                        while (getRestOfKeys.hasNext()) {
                            keys = (String) getRestOfKeys.next();
                            keysInKey.add(keys);
                        }
                        System.out.println("keys in the first key:" + keysInKey.toString());

                    } catch (JSONException JE) {
                        Log.e("json failure", "Failed to get keys in the first key");
                        return;
                    }
                    valueOfKeys = new ArrayList<>();
                    for (int i = 0; i < keysInKey.size(); i++) {
                        String nameOfKeys = keysInKey.get(i);
                        try {
                            valueOfKeys.add(jsonUnderKey.get(nameOfKeys).toString());
                        } catch (JSONException JE) {
                            Log.e("json failure", "failed to get value of keys in jsonUnderKey");
                            return;
                        }
                    }
                    checkNumKeys = new ArrayList<>(Arrays.asList("numHighShotsMissedTele", "numHighShotsMissedAuto",
                            "numHighShotsMadeTele", "numLowShotsMissedTele", "numLowShotsMadeTele",
                            "numBallsKnockedOffMidlineAuto", "numShotsBlockedTele", "numHighShotsMadeAuto",
                            "numLowShotsMissedAuto", "numLowShotsMadeAuto", "numGroundIntakesTele"));

                    checkStringKeys = new ArrayList<>(Arrays.asList("didScaleTele", "didGetDisabled", "didGetIncapacitated",
                            "didChallengeTele", "didReachAuto", "scoutName"));

                    scoutAlliance = valueOfKeys.get(keysInKey.indexOf("alliance"));
                    for (int i = 0; i < checkNumKeys.size(); i++) {
                        stringIndex = (keysInKey.indexOf(checkNumKeys.get(i)));
                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(stringIndex)).setValue(Integer.parseInt(valueOfKeys.get(stringIndex)));
                    }
                    for (int i = 0; i < checkStringKeys.size(); i++) {
                        intIndex = (keysInKey.indexOf(checkStringKeys.get(i)));
                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(intIndex)).setValue(valueOfKeys.get(intIndex));
                    }
                    try {
                        Firebase pathToBallsIntakedAuto = new Firebase(dataBaseUrl + "TeamInMatchDatas/" + firstKey + "/ballsIntakedAuto");
                        JSONArray balls = jsonUnderKey.getJSONArray("ballsIntakedAuto");
                        if (jsonArrayToArray(balls).size() < 1) {
                            Log.e("balls", "is Null!");
                            pathToBallsIntakedAuto.removeValue();
                            Log.e("ballsIntakedAuto", "Has been removed!");
                        } else {
                            for (int i = 0; i < balls.length(); i++) {
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("ballsIntakedAuto").setValue(jsonArrayToArray(balls));
                            }
                        }
                    } catch (JSONException JE) {
                        Log.e("Json failure", "failed to get balls intaked");
                        return;
                    }
                }
                Log.e("Test", "After ballsIntakedAuto");
                //get json array containing success and fail times for defense crossing of auto and tele
                for (int j = 0; j < datapoints.size(); j++) {
                    JSONObject scoutDataJson = datapoints.get(j);
                    Iterator getFirstKey = scoutDataJson.keys();
                    while (getFirstKey.hasNext()) {
                        firstKey = (String) getFirstKey.next();
                        //split first key to get only match number
                        String[] teamAndMatchNumbers = firstKey.split("Q");
                        matchNum = Integer.parseInt(teamAndMatchNumbers[1]);
                        try {
                            jsonUnderKey = scoutDataJson.getJSONObject(firstKey);
                            System.out.println("First Key: " + firstKey);
                            System.out.println(jsonUnderKey.toString());
                        } catch (Exception e) {
                            Log.e("JSON", "Failed to get first key");
                            return;
                        }
                    }
                    try {
                        successDefenseTele = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesTele");
                        failedDefenseTele = jsonUnderKey.getJSONArray("failedDefenseCrossTimesTele");
                        successDefenseAuto = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesAuto");
                        failedDefenseAuto = jsonUnderKey.getJSONArray("failedDefenseCrossTimesAuto");
                    } catch (JSONException jsone) {
                        Log.e("Json error", "could not get key json");
                        return;
                    }
                    //if the scout data is based on blue alliance
                    if (scoutAlliance.equals("blue")) {
                        try{
                        List<String> defenses = new ArrayList<>();
                        List<String> blueDefenseList = FirebaseLists.matchesList.getFirebaseObjectByKey(Integer.toString(matchNum)).blueDefensePositions;
                        try {
                            for (int i = 0; i < 5; i++) {
                                String tmp = (blueDefenseList.get(i)).toLowerCase();
                                defenses.add(tmp);
                                Log.e("defenses", defenses.toString());
                            }
                            for (int i = 0; i < successDefenseAuto.length(); i++) {
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesAuto").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) successDefenseAuto.get(i)));
                            }
                            for (int i = 0; i < failedDefenseAuto.length(); i++) {
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesAuto").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) failedDefenseAuto.get(i)));
                            }
                            for (int i = 0; i < successDefenseTele.length(); i++) {
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesTele").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) successDefenseTele.get(i)));
                            }
                            for (int i = 0; i < failedDefenseTele.length(); i++) {
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesTele").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) failedDefenseTele.get(i)));
                            }
                            toasts("Resent Scout data", false);
                        } catch (JSONException JE) {
                            Log.e("json failure", "failed loop blue");
                            toasts("Failed to resend scout data", false);
                            return;
                        } catch (NullPointerException NPE) {
                            Log.e("If Blue", "9");
                            toasts("Input defenses for Match " + Integer.toString(matchNum) + " And resend scout data!", true);
                            Log.e("Toast", "should have been seen");
                            return;
                        }
                    }catch (IndexOutOfBoundsException IOB) {
                            Log.e("FirebaseException", "blueMain");
                            toasts("Resent scout data match number does not exist!", true);
                        }
                    } else if (scoutAlliance.equals("red")) {
                        try {
                            List<String> defenses = new ArrayList<>();
                            List<String> redDefenseList = FirebaseLists.matchesList.getFirebaseObjectByKey(Integer.toString(matchNum)).redDefensePositions;
                            try {
                                for (int i = 0; i < 5; i++) {
                                    String tmp = (redDefenseList.get(i)).toLowerCase();
                                    defenses.add(tmp);
                                }
                                for (int i = 0; i < successDefenseAuto.length(); i++) {
                                    dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesAuto").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) successDefenseAuto.get(i)));
                                }
                                for (int i = 0; i < failedDefenseAuto.length(); i++) {
                                    dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesAuto").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) failedDefenseAuto.get(i)));
                                }
                                for (int i = 0; i < successDefenseTele.length(); i++) {
                                    dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesTele").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) successDefenseTele.get(i)));
                                }
                                for (int i = 0; i < failedDefenseTele.length(); i++) {
                                    dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesTele").child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) failedDefenseTele.get(i)));
                                }
                            } catch (JSONException JE) {
                                Log.e("json failure", "failed loop red");
                                toasts("Failed to resend Scout Data", false);
                                return;
                            } catch (NullPointerException npe) {
                                toasts("Input defenses for Match " + Integer.toString(matchNum) + " And resend scout data!", true);
                                return;
                            }
                            Log.e("reached", "toast");
                            toasts("Resent Scout Data", false);
                        }catch(IndexOutOfBoundsException IOB){
                            Log.e("FirebaseException", "redMain");
                            toasts("Resent scout data match number does not exist!", true);
                        }
                    }
                }
            }

        }.start();
    }
    public void toasts(final String message, boolean isLongMessage) {
        if (!isLongMessage) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}



