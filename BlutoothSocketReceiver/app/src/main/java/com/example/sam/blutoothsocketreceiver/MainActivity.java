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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    Activity context;
    EditText numberOfMatch;
    EditText teamNumberOne;
    EditText teamNumberTwo;
    EditText teamNumberThree;
    TextView alliance;
    Boolean isRed = new Boolean(false);
    Integer matchNumber = new Integer(0);
    ListView superList;
    Firebase dataBase;
    String firstKey;
    String keys;
    String scoutAlliance;
    int matchNum;
    int stringIndex;
    int intIndex;
    ArrayList <String> keysInKey;
    ArrayList<String> valueOfKeys;
    ArrayList<String> checkNumKeys;
    ArrayList<String> checkStringKeys;
    JSONObject jsonUnderKey;
    JSONArray successDefenseTele;
    JSONArray failedDefenseTele;
    JSONArray successDefenseAuto;
    JSONArray failedDefenseAuto;
    Map<String, String> defenseCategories = new HashMap<String, String>(){
        //each defense with is own category
        {
            put("cdf", "a");
            put("pc", "a");
            put("mt", "b");
            put("rp", "b");
            put("db", "c");
            put("sp", "c");
            put("rt", "d");
            put("rw", "d");
            put("lb", "e");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("test", "Logcat is up and running!");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        accept_loop loop = new accept_loop(context);
        loop.start();
        Intent backToHome = getIntent();
        numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        teamNumberOne = (EditText) findViewById(R.id.teamOneNumber);
        teamNumberTwo = (EditText) findViewById(R.id.teamTwoNumber);
        teamNumberThree = (EditText) findViewById(R.id.teamThreeNumber);
        alliance = (TextView) findViewById(R.id.allianceName);
        jsonUnderKey = new JSONObject();
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
            }
        };
        dataBase = new Firebase("https://1678-dev-2016.firebaseio.com/");
        dataBase.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
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
        updateUI();
        numberOfMatch.setText(matchNumber.toString());
        matchNumber = Integer.parseInt(numberOfMatch.getText().toString());

        disenableEditTestEditing();
        updateSuperData();

        listenForSuperFileClick();
        listenForScoutFileClick();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI();
            }
        }, new IntentFilter("matches_updated"));

        //Change team numbers as the user changes the match number
        changeTeamsByMatchName();
        commitSharedPreferences();
    }

    public void getScoutData(View view) {
        updateScoutData();
    }

    public void getSuperData(View view) {
        updateSuperData();
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
            //check to see if all data inputs were filled out before continuing
            if (numberOfMatch.getText().toString().equals("")) {
                Toast.makeText(context, "Input match name!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberOne.getText().toString().equals("")) {
                Toast.makeText(context, "Input team one number!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberTwo.getText().toString().equals("")) {
                Toast.makeText(context, "Input team two number!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberThree.getText().toString().equals("")) {
                Toast.makeText(context, "Input team three number!", Toast.LENGTH_SHORT).show();
            } else {
                //write to file
                Intent intent = new Intent(this, FieldSetUp.class);
                intent.putExtra("matchNumber", numberOfMatch.getText().toString());
                intent.putExtra("teamNumberOne", teamNumberOne.getText().toString());
                intent.putExtra("teamNumberTwo", teamNumberTwo.getText().toString());
                intent.putExtra("teamNumberThree", teamNumberThree.getText().toString());
                intent.putExtra("alliance", alliance.getText().toString());
                Log.e("start alliance", alliance.getText().toString());
                startActivity(intent);
            }
        } else if (id == R.id.action_override) {
            enableEditTextEditing();

        } else if (id == R.id.unoverride) {
            updateUI();
            disenableEditTestEditing();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSuperData() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
                if (!dir.mkdir()) {
                    Log.i("File Info", "Failed to make Directory. Unimportant");
                }
                File[] files = dir.listFiles();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
                for (File tmpFile : files) {
                    adapter.add(tmpFile.getName());
                }
                superList = (ListView) context.findViewById(R.id.view_files_received);
                superList.setAdapter(adapter);
            }
        });
    }

    public void updateScoutData() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File scoutFile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data");
                if (!scoutFile.mkdir()) {
                    Log.i("File Info", "Failed to make Directory. Unimportant");
                }
                File[] files = scoutFile.listFiles();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
                for (File tmpFile : files) {
                    adapter.add(tmpFile.getName());
                }
                ListView listView = (ListView) context.findViewById(R.id.view_files_received);
                listView.setAdapter(adapter);
            }
        });
    }

    private void updateUI() {
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
            teamNumberOne.setText("");
            teamNumberTwo.setText("");
            teamNumberThree.setText("");
        }
    }

    public void commitSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("match_number", matchNumber);
        editor.putBoolean("allianceColor", isRed);
        editor.commit();
    }

    public void changeTeamsByMatchName() {
        EditText numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        numberOfMatch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

    public void disenableEditTestEditing() {

        numberOfMatch.setFocusable(false);
        teamNumberOne.setFocusable(false);
        teamNumberTwo.setFocusable(false);
        teamNumberThree.setFocusable(false);
    }

    public void listenForSuperFileClick() {
        superList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("test", "at 1");
                final String superFileName = parent.getItemAtPosition(position).toString();
                //read data from file
                String text = readSuperFile(superFileName);
                if (text != null) {
                    try {
                        JSONObject object = new JSONObject(text);
                        System.out.println(object.toString());
                        String matchAndTeamOne = object.get("teamOne") + "Q" + object.get("matchNumber");
                        String matchAndTeamTwo = object.get("teamTwo") + "Q" + object.get("matchNumber");
                        String matchAndTeamThree = object.get("teamThree") + "Q" + object.get("matchNumber");
                        if (object.get("alliance").equals("Blue Alliance")) {

                            //dataBase.child("Matches").child((object.getString("matchNumber"))).child()
                        }
                    } catch (JSONException JE) {
                        Log.e("json error", "failed to get super json");
                    }
                    // new ConnectThread(context, superName, uuid, name, text).start();
                }
            }
        });
    }

    public String readSuperFile(String name) {
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name))));
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Open File");
            Toast.makeText(context, "Failed To Open File", Toast.LENGTH_LONG).show();
            return null;
        }
        String dataOfSuper = "";
        String buf;
        try {
            while ((buf = file.readLine()) != null) {
                dataOfSuper = dataOfSuper.concat(buf + "\n");
            }
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Read From File");
            Toast.makeText(context, "Failed To Read From File", Toast.LENGTH_LONG).show();
            return null;
        }
        Log.i("SUPER", dataOfSuper);
        return dataOfSuper;
    }

    public void listenForScoutFileClick() {
        final Activity activity = this;
        superList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String scoutFileName = parent.getItemAtPosition(position).toString();
                new AlertDialog.Builder(activity)
                        .setTitle("Resend Data?")
                        .setMessage("RESEND SCOUT DATA?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //read data from file
                                String scoutData = readScoutFile(scoutFileName);
                                System.out.println("scoutData: " + scoutData);
                                if (scoutData != null) {
                                    try {
                                        JSONObject scoutDataJson = new JSONObject(scoutData);
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
                                    } catch (JSONException JE) {
                                        Log.e("json error", "failed to get super json");
                                    }// new ConnectThread(context, superName, uuid, name, text).start();
                                    try{
                                        //get arrays of the keys in the json object
                                        keysInKey = new ArrayList<>();
                                        JSONObject keyNames = new JSONObject(jsonUnderKey.toString());
                                        Iterator getRestOfKeys = keyNames.keys();
                                        while(getRestOfKeys.hasNext()){
                                            keys = (String) getRestOfKeys.next();
                                            keysInKey.add(keys);
                                        }
                                        System.out.println("keys in the first key:" + keysInKey.toString());

                                    }catch(JSONException JE){
                                        Log.e("json failure", "Failed to get keys in the first key");
                                        return;
                                    }
                                    valueOfKeys= new ArrayList<String>();
                                    for(int i = 0; i < keysInKey.size(); i++){
                                        String nameOfKeys = keysInKey.get(i);
                                        try {
                                            valueOfKeys.add(jsonUnderKey.get(nameOfKeys).toString());
                                        }catch (JSONException JE){
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
                                    final Firebase dataBase = new Firebase("https://1678-dev-2016.firebaseio.com/");
                                    for(int i = 0; i < checkNumKeys.size(); i++){
                                        stringIndex = (keysInKey.indexOf(checkNumKeys.get(i)));
                                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(stringIndex)).setValue(Integer.parseInt(valueOfKeys.get(stringIndex)));
                                    }
                                    for(int i = 0; i < checkStringKeys.size(); i++){
                                        intIndex = (keysInKey.indexOf(checkStringKeys.get(i)));
                                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(intIndex)).setValue(valueOfKeys.get(intIndex));
                                    }
                                    try {
                                        JSONArray balls = jsonUnderKey.getJSONArray("ballsIntakedAuto");
                                        for (int i = 0; i < balls.length(); i++) {
                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("ballsIntakedAuto").setValue(jsonArrayToArray(balls));

                                        }
                                    }catch(JSONException JE){
                                        Log.e("Json failure", "failed to get balls intaked");
                                        return;
                                    }

                                    try {
                                        //get json array containing success and fail times for defense crossing of auto and tele
                                        successDefenseTele = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesTele");
                                        failedDefenseTele = jsonUnderKey.getJSONArray("failedDefenseCrossTimesTele");
                                        successDefenseAuto = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesAuto");
                                        failedDefenseAuto = jsonUnderKey.getJSONArray("failedDefenseCrossTimesAuto");
                                        dataBase.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                //if the scout data is based on blue alliance
                                                if(scoutAlliance.equals("blue")) {
                                                    List<String> defenses = new ArrayList<>();
                                                    for (int i = 0; i < 5; i++) {
                                                        String tmp = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child(Integer.toString(i)).getValue().toString()).toLowerCase();
                                                        defenses.add(tmp);
                                                    }
                                                    try {
                                                        for (int i = 0; i < successDefenseAuto.length(); i++) {
                                                            Log.i("i", Integer.toString(i));
                                                            Log.e("Test 1", firstKey);
                                                            Log.e("Test 2", defenseCategories.toString());
                                                            Log.e("Test 3", defenseCategories.get(defenses.get(i)));
                                                            Log.e("Test 4", jsonArrayToArray((JSONArray) successDefenseAuto.get(i)).toString());

                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesAuto").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray) successDefenseAuto.get(i)));
                                                        }
                                                        for (int i = 0; i < failedDefenseAuto.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesAuto").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)failedDefenseAuto.get(i)));
                                                        }
                                                        for (int i = 0; i < successDefenseTele.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesTele").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)successDefenseTele.get(i)));
                                                        }
                                                        for (int i = 0; i < failedDefenseTele.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesTele").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)failedDefenseTele.get(i)));
                                                        }
                                                    }catch(JSONException JE){
                                                        Log.e("json failure", "failed loop red");
                                                        return;
                                                    }

                                                }else if(scoutAlliance.equals("red")){
                                                    List<String> defenses = new ArrayList<>();
                                                    for (int i = 0; i < 5; i++) {
                                                        String tmp = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("redDefensePositions").child(Integer.toString(i)).getValue().toString()).toLowerCase();
                                                        defenses.add(tmp);
                                                    }
                                                    try {
                                                        for (int i = 0; i < successDefenseAuto.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesAuto").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)successDefenseAuto.get(i)));
                                                        }
                                                        for (int i = 0; i < failedDefenseAuto.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesAuto").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)failedDefenseAuto.get(i)));
                                                        }
                                                        for (int i = 0; i < successDefenseTele.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesSuccessfulCrossedDefensesTele").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)successDefenseTele.get(i)));
                                                        }
                                                        for (int i = 0; i < failedDefenseTele.length(); i++) {
                                                            dataBase.child("TeamInMatchDatas").child(firstKey).child("timesFailedCrossedDefensesTele").child(defenseCategories.get(defenses.get(i))).child(defenses.get(i)).setValue(jsonArrayToArray((JSONArray)failedDefenseTele.get(i)));
                                                        }
                                                    }catch(JSONException JE){
                                                        Log.e("json failure", "failed loop red");
                                                        return;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {
                                                System.out.println("The read failed: " + firebaseError.getMessage());
                                            }
                                        });
                                    }catch (JSONException JE){
                                        Log.e("change", "cant send jsonarray");
                                        return;
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Log.i("test", "at 1");

            }
        });
}

    public String readScoutFile(String name) {
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data/" + name))));
            Log.e("Error 1", "1");
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Open File");
            Toast.makeText(context, "Failed To Open File", Toast.LENGTH_LONG).show();
            return null;
        }
        String dataOfScout = "";
        String buf;
        Log.e("Error 2", "2");
        try {
            while ((buf = file.readLine()) != null) {
                dataOfScout = dataOfScout.concat(buf + "\n");
                Log.e("Error 3", "3");
            }
            Log.e("Error 4", "4");
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Read From File");
            Toast.makeText(context, "Failed To Read From File", Toast.LENGTH_LONG).show();
            return null;
        }
        Log.i("SCOUT", dataOfScout);
        Log.e("Error 5", "5");
        return dataOfScout;
    }

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
}


