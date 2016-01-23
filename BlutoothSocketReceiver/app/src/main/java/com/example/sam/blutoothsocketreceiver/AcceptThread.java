package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

/**
 * Created by sam on 1/7/16.
 */

    public class AcceptThread extends  Thread {
    String text;
    Activity context;
    String byteSize;
    String data;
    BluetoothSocket socket;
    DataSnapshot snapshot;
    JSONObject scoutData = new JSONObject();


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
                PrintWriter file = null;
                try {
                    File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scout_data");
                    dir.mkdir();
                    //can delete when doing the actual thing
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
                        public void onAuthenticated(AuthData authData) {
                            // Do nothing if authenticated
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            CharSequence text = "Invalid Permissions.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    };
                    final Firebase dataBase = new Firebase("https://1678-dev-2016.firebaseio.com/");
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
                                    blueTeamNumbers.put(matchKeys, snapshot.child("Matches").child(matchKeys).child("blueAllianceTeamNumbers").getValue());
                                    redTeamNumbers.put(matchKeys, snapshot.child("Matches").child(matchKeys).child("redAllianceTeamNumbers").getValue());
                                } catch (JSONException JE) {
                                    Log.e("schedule", "Failed to put to matches");
                                }
                            }
                            try {
                                PrintWriter out;
                                out = new PrintWriter(socket.getOutputStream(), true);
                                out.println(blueTeamNumbers.toString());
                                out.flush();
                                out.println(redTeamNumbers.toString());
                                out.flush();
                                toasts("Schedule sent to Scout");

                            } catch (IOException IOE) {
                                toasts("Failed to send schedule to scout");
                            }
                                /*System.out.println(blueTeamNumbers.toString());
                                System.out.println(redTeamNumbers.toString());*/

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
                        System.out.println(data);

                        //text(Integer.toString(size));
                    }
                    //data = data.concat("asdf");
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
                        //can delete when doing actual thing
                        //file.println(text);
                        System.out.println(text);
                        out.println("0");
                        out.flush();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Data trasnfer success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        file.println(text);
                        toasts("Sent scout data to file");
                        updateScoutData();

                            /*try {
                                scoutData = new JSONObject(text);
                                Iterator<String> keys = scoutData.keys();
                                if( keys.hasNext() ){
                                    String scoutKeyName = (String)keys.next();// First key in scout's json object
                                    //dataBase.child("TeamInMatchDatas").child(scoutKeyName).
                                }

                            }catch(JSONException JE){
                                toasts("Failed to send scout data to firebase");
                                return;
                            }

                            System.out.println(" Data Sent to Firebase");
                            toasts("Sent to Firebase");
*/
                    }
                    System.out.println("end");
                    return;
                }
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
                File scoutFile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MassStringText");
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



