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
    int stringIndex;
    int intIndex;
    int matchNum;
    BluetoothSocket socket;
    JSONObject jsonUnderKey;
    ArrayList<String> keysInKey;
    ArrayList<String> valueOfKeys;
    ArrayList<String> checkNumKeys;
    ArrayList<String> checkStringKeys;
    PrintWriter file = null;
    JSONArray successDefenseTele;
    JSONArray failedDefenseTele;
    JSONArray successDefenseAuto;
    JSONArray failedDefenseAuto;
    JSONArray firstSuccessDefenseTele;
    JSONArray secondSuccessDefenseTele;
    JSONArray thirdSuccessDefenseTele;
    JSONArray fourthSuccessDefenseTele;
    JSONArray fifthSuccessDefenseTele;
    JSONArray firstFailedDefenseTele;
    JSONArray secondFailedDefenseTele;
    JSONArray thirdFailedDefenseTele;
    JSONArray fourthFailedDefenseTele;
    JSONArray fifthFailedDefenseTele;
    JSONArray firstSuccessDefenseAuto;
    JSONArray secondSuccessDefenseAuto;
    JSONArray thirdSuccessDefenseAuto;
    JSONArray fourthSuccessDefenseAuto;
    JSONArray fifthSuccessDefenseAuto;
    JSONArray firstFailedDefenseAuto;
    JSONArray secondFailedDefenseAuto;
    JSONArray thirdFailedDefenseAuto;
    JSONArray fourthFailedDefenseAuto;
    JSONArray fifthFailedDefenseAuto;
    Firebase database;
    Map<String, String> defenseCategories = new HashMap<String, String>(){
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
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data");
                    dir.mkdir();
                    file = new PrintWriter(new FileOutputStream(new File(dir, "Scout_data" + " " + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()))));
                } catch (IOException IOE) {
                    Log.e("File error", "Failed to open File");
                    return;
                }
                try {
                    text = "";
                    //get the bytesize from the first line of the data
                    byteSize = reader.readLine();
                } catch (IOException e) {
                    System.out.println("Failed to read Data");
                }
                final int size = Integer.parseInt(byteSize);
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
                            System.out.println(blueTeamNumbers.toString());
                            System.out.println(redTeamNumbers.toString());

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
                                out.println(data.toString().length());
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
                            JSONObject scoutData = new JSONObject(data);
                            //System.out.println(scoutData.toString());
                            Iterator getFirstKey = scoutData.keys();
                            while(getFirstKey.hasNext()){
                                firstKey = (String) getFirstKey.next();
                                String[] teamAndMatchNumbers = firstKey.split("Q");
                                matchNum = Integer.parseInt(teamAndMatchNumbers[1]);
                                try{
                                    jsonUnderKey = scoutData.getJSONObject(firstKey);
                                    System.out.println("First Key: " + firstKey);
                                    System.out.println(jsonUnderKey.toString());
                                }catch(Exception e){
                                    Log.e("JSON", "Failed to get first key");
                                }
                            }

                        }catch (JSONException JE){
                            Log.e("json Failure", "Failed to convert json string into json object");
                        }
                        try{
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
                        }
                        valueOfKeys = new ArrayList<>();
                        for(int i = 0; i < keysInKey.size(); i++){
                            String nameOfKeys = keysInKey.get(i);
                            try {
                                valueOfKeys.add(jsonUnderKey.get(nameOfKeys).toString());
                            }catch (JSONException JE){
                                Log.e("json failure", "failed to get value of keys in jsonUnderKey");
                            }
                        }
                        System.out.println(valueOfKeys.toString());

                    }
                    checkNumKeys = new ArrayList<>(Arrays.asList("numHighShotsMissedTele", "numHighShotsMissedAuto",
                            "numHighShotsMadeTele", "numLowShotsMissedTele", "numLowShotsMadeTele",
                            "numBallsKnockedOffMidlineAuto", "numShotsBlockedTele", "numHighShotsMadeAuto",
                            "numLowShotsMissedAuto", "numLowShotsMadeAuto", "numGroundIntakesTele"));
                    checkStringKeys = new ArrayList<>(Arrays.asList("didScaleTele", "didGetDisabled", "didGetIncapacitated",
                            "didChallengeTele", "didReachAuto", "scoutName"));
                    final Firebase dataBase = new Firebase("https://1678-dev-2016.firebaseio.com/");
                    for(int i = 0; i < checkNumKeys.size(); i++){
                        stringIndex = (keysInKey.indexOf(checkNumKeys.get(i)));
                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(stringIndex)).setValue(Integer.parseInt(valueOfKeys.get(stringIndex)));
                        }
                    for(int i = 0; i < checkStringKeys.size(); i++){
                        intIndex = (keysInKey.indexOf(checkStringKeys.get(i)));
                        dataBase.child("TeamInMatchDatas").child(firstKey).child(keysInKey.get(intIndex)).setValue(valueOfKeys.get(intIndex));
                    }
                    System.out.println(valueOfKeys.get(5));
                    String ballIntaked = valueOfKeys.get(5).replace("[", "").replace("]", "");
                    List<String> intakedBalls = new ArrayList<String>(Arrays.asList(ballIntaked.split(",")));
                    System.out.println(intakedBalls.toString());
                    System.out.println(intakedBalls.size());
                    for(int i = 0; i < intakedBalls.size(); i++){
                        dataBase.child("TeamInMatchDatas").child(firstKey).child("ballsIntakedAuto").child(Integer.toString(i)).setValue(intakedBalls.get(i));
                    }
                    try {

                        successDefenseTele = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesTele");
                        failedDefenseTele = jsonUnderKey.getJSONArray("failedDefenseCrossTimesTele");
                        successDefenseAuto = jsonUnderKey.getJSONArray("successfulDefenseCrossTimesAuto");
                        failedDefenseAuto = jsonUnderKey.getJSONArray("failedDefenseCrossTimesTele");
                        firstSuccessDefenseTele = successDefenseTele.getJSONArray(0);
                        firstFailedDefenseTele = failedDefenseTele.getJSONArray(0);
                        secondSuccessDefenseTele = successDefenseTele.getJSONArray(1);
                        secondFailedDefenseTele = failedDefenseTele.getJSONArray(1);
                        thirdSuccessDefenseTele = successDefenseTele.getJSONArray(2);
                        thirdFailedDefenseTele = failedDefenseTele.getJSONArray(2);
                        fourthSuccessDefenseTele = successDefenseTele.getJSONArray(3);
                        fourthFailedDefenseTele = failedDefenseTele.getJSONArray(3);
                        fifthSuccessDefenseTele = successDefenseTele.getJSONArray(4);
                        fifthFailedDefenseTele = failedDefenseTele.getJSONArray(4);

                        firstSuccessDefenseAuto = successDefenseAuto.getJSONArray(0);
                        firstFailedDefenseAuto = failedDefenseAuto.getJSONArray(0);
                        secondSuccessDefenseAuto = successDefenseAuto.getJSONArray(1);
                        secondFailedDefenseAuto = failedDefenseAuto.getJSONArray(1);
                        thirdSuccessDefenseAuto = successDefenseAuto.getJSONArray(2);
                        thirdFailedDefenseAuto = failedDefenseAuto.getJSONArray(2);
                        fourthSuccessDefenseAuto = successDefenseAuto.getJSONArray(3);
                        fourthFailedDefenseAuto = failedDefenseAuto.getJSONArray(3);
                        fifthSuccessDefenseAuto = successDefenseAuto.getJSONArray(4);
                        fifthFailedDefenseAuto = failedDefenseAuto.getJSONArray(4);
                        dataBase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                String nameOfFirstDefense = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child("0").getValue().toString()).toLowerCase();
                                String nameOfSecondDefense = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child("1").getValue().toString()).toLowerCase();
                                String nameOfThirdDefense = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child("2").getValue().toString()).toLowerCase();
                                String nameOfFourthDefense = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child("3").getValue().toString()).toLowerCase();
                                String nameOfFifthDefense = (snapshot.child("Matches").child(Integer.toString(matchNum)).child("blueDefensePositions").child("4").getValue().toString()).toLowerCase();

                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesAuto").child(defenseCategories.get(nameOfFirstDefense)).child(nameOfFirstDefense.toLowerCase()).setValue(firstSuccessDefenseAuto.toString());
                                Log.e("match Number", Integer.toString(matchNum));
                                Log.wtf("name of first defense", nameOfFirstDefense);
                                Log.wtf("name of second defense", nameOfSecondDefense);
                                Log.wtf("name of third defense", nameOfThirdDefense);
                                Log.wtf("name of fourth defense", nameOfFourthDefense);
                                Log.wtf("name of fifth defense", nameOfFifthDefense);
                                Log.wtf("Category of Second Defense", defenseCategories.get(nameOfSecondDefense));
                                Log.wtf("Name of second defense", nameOfSecondDefense);
                                Log.wtf("success times for second defense auto", secondSuccessDefenseAuto.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesAuto").child(defenseCategories.get(nameOfSecondDefense)).child(nameOfSecondDefense).setValue(secondSuccessDefenseAuto.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesAuto").child(defenseCategories.get(nameOfThirdDefense)).child(nameOfThirdDefense).setValue(thirdSuccessDefenseAuto.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesAuto").child(defenseCategories.get(nameOfFourthDefense)).child(nameOfFourthDefense).setValue(fourthSuccessDefenseAuto.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesAuto").child(defenseCategories.get(nameOfFifthDefense)).child(nameOfFifthDefense).setValue(fifthSuccessDefenseAuto.toString());

                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesTele").child(defenseCategories.get(nameOfFirstDefense)).child(nameOfFirstDefense).setValue(firstSuccessDefenseTele.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesTele").child(defenseCategories.get(nameOfSecondDefense)).child(nameOfSecondDefense).setValue(secondSuccessDefenseTele.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesTele").child(defenseCategories.get(nameOfThirdDefense)).child(nameOfThirdDefense).setValue(thirdSuccessDefenseTele.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesTele").child(defenseCategories.get(nameOfFourthDefense)).child(nameOfFourthDefense).setValue(fourthSuccessDefenseTele.toString());
                                dataBase.child("TeamInMatchDatas").child(firstKey).child("timesCrossedDefensesTele").child(defenseCategories.get(nameOfFifthDefense)).child(nameOfFifthDefense).setValue(fifthSuccessDefenseTele.toString());

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });

                    }catch(JSONException JE){

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
}



