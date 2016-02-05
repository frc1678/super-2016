package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.blutoothsocketreceiver.firebase_classes.TeamInMatchData;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.shaded.fasterxml.jackson.core.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Created by sam on 1/7/16.
 */

    public class AcceptThread extends  Thread {
    Activity context;
    String text;
    String byteSize;
    String data;
    String firstKey;
    String keys;
    String scoutAlliance;
    int stringIndex;
    int intIndex;
    int matchNum;
    BluetoothSocket socket;
    ArrayList<String> keysInKey;
    ArrayList<String> valueOfKeys;
    ArrayList<String> checkNumKeys;
    ArrayList<String> checkStringKeys;
    PrintWriter file = null;
    JSONObject jsonUnderKey;
    JSONObject scoutData;
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
    public AcceptThread(Activity context, BluetoothSocket socket) {
        this.socket = socket;
        this.context = context;
    }

    public void run() {
        // If a connection was accepted
        if (socket != null) {
            //socket opened with connection
            // Do work to manage the connection (in a separate thread)
            try {
                PrintWriter out;
                out = new PrintWriter(socket.getOutputStream(), true);
                //set the out printWriter to send data to scout
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                try {
                    text = "";
                    //get the bytesize from the first line of the data
                    byteSize = reader.readLine();
                } catch (IOException e) {
                    System.out.println("Failed to read Data");
                    return;
                }
                final int size = Integer.parseInt(byteSize);
                //If the scout requests the schedule
                if (size == -1) {
                    Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {}
                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {}
                    };
                    final Firebase dataBase = new Firebase("https://1678-dev-2016.firebaseio.com/");
                    dataBase.authWithPassword("1678programming@gmail.com", "Squeezecrush1", authResultHandler);
                    dataBase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            JSONObject blueTeamNumbers = new JSONObject();
                            JSONObject redTeamNumbers = new JSONObject();
                            Iterator<DataSnapshot> iterator = snapshot.child("Matches").getChildren().iterator();

                            while (iterator.hasNext()) {
                                DataSnapshot tmp = iterator.next();
                                String matchKeys = tmp.getKey();
                                try {
                                    String blueTeams = snapshot.child("Matches").child(matchKeys).child("blueAllianceTeamNumbers").getValue().toString();
                                    String redTeams = snapshot.child("Matches").child(matchKeys).child("redAllianceTeamNumbers").getValue().toString();
                                    blueTeamNumbers.put(matchKeys, new JSONArray(blueTeams));
                                    redTeamNumbers.put(matchKeys, new JSONArray(redTeams));
                                } catch (JSONException JE) {
                                    Log.e("schedule", "Failed to put to matches");
                                    return;
                                }
                            }
                            JSONObject data = new JSONObject();
                            try {
                                data.put("redTeamNumbers", redTeamNumbers);
                                data.put("blueTeamNumbers", blueTeamNumbers);
                            }catch (JSONException JE){
                                Log.e("JSON Error", "Failed to put redTeamNumbers and blueTeamNumbers to data");
                                return;
                            }
                            try {
                                PrintWriter out;
                                out = new PrintWriter(socket.getOutputStream(), true);
                                //Send the schedule length to scout to check
                                out.println(data.toString().length());
                                //send the schedule to scout
                                out.println(data.toString());
                                out.println("\0");
                                out.flush();
                                toasts("Schedule sent to Scout");

                            } catch (IOException IOE) {
                                toasts("Failed to send schedule to scout");
                                return;
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });

                }
                if (socket != null) {
                    data = "";
                    while (true) {
                        text = reader.readLine();
                        //If all the data is sent then break
                        if (text.equals("\0")) {
                            break;
                        }
                        //append data to the variable "data"
                        data = data.concat(text + "\n");
                    }
                    //if the actual byte size is different from the byte size received..
                    if (size != data.length()) {
                        //send error message to scout.
                        //0 = no error, 1 = ERROR!
                        out.println("1");
                        out.flush();
                        toasts("ERROR message sent");
                        Log.e("Error", "Error message sent");
                        //I the byte size of actual is equal to the byte size received
                    } else {
                        out.println("0");
                        out.flush();
                        toasts("Data transfer Success!");
                        //System.out.println(data);
                        updateScoutData();
                        try {
                            //get first key of the scout data that contains the match and the team number
                            scoutData = new JSONObject(data);
                            Iterator getFirstKey = scoutData.keys();
                            while(getFirstKey.hasNext()){
                                firstKey = (String) getFirstKey.next();
                                //split first key to get only match number
                                String[] teamAndMatchNumbers = firstKey.split("Q");
                                matchNum = Integer.parseInt(teamAndMatchNumbers[1]);
                                try{
                                    jsonUnderKey = scoutData.getJSONObject(firstKey);
                                    System.out.println("First Key: " + firstKey);
                                    System.out.println(jsonUnderKey.toString());
                                }catch(Exception e){
                                    Log.e("JSON", "Failed to get first key");
                                    return;
                                }
                            }

                        }catch (JSONException JE){
                            Log.e("json Failure", "Failed to convert json string into json object");
                        }
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
                        valueOfKeys = new ArrayList<>();
                        //get all the value of the keys in an array list
                        for(int i = 0; i < keysInKey.size(); i++){
                            String nameOfKeys = keysInKey.get(i);
                            try {
                                valueOfKeys.add(jsonUnderKey.get(nameOfKeys).toString());
                            }catch (JSONException JE){
                                Log.e("json failure", "failed to get value of keys in jsonUnderKey");
                                return;
                            }
                        }
                        System.out.println(valueOfKeys.toString());

                    }
                    //seperate all keys with int values and string values
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
                                    } catch (NullPointerException npe) {
                                        toasts("Tell scout not to send it yet!");
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

                    }catch(JSONException JE){
                        Log.e("change", "cant send jsonarray");
                        return;
                    }
                    try {
                        //make file and directory for Scout data
                        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data");
                        dir.mkdir();
                        file = new PrintWriter(new FileOutputStream(new File(dir, "Q" + matchNum + "_" + (valueOfKeys.get(keysInKey.indexOf(checkStringKeys.get(5)))).toUpperCase() + "_" + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()))));
                        file.println(scoutData.toString());
                        file.close();
                    } catch (IOException IOE) {
                        Log.e("File error", "Failed to open File");
                        return;
                    }

                }
                    System.out.println("end");
                    return;
                //file.close();
                // socket.close();
            } catch (IOException e) {
                System.out.println("Failed to handle data");
                Log.getStackTraceString(e);
                return;
            }
        }
    }

    public void toasts(final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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



