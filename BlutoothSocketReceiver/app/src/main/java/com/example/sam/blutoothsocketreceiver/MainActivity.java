package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    Activity context;
    EditText numberOfMatch;
    EditText teamNumberOne;
    EditText teamNumberTwo;
    EditText teamNumberThree;
    TextView alliance;
    String matchNumber;
    String chosenAlliance;
    Boolean isRed = new Boolean(false);
    String matchName;

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

        //If got intent from the last activity
        Integer match = 1;
        if (backToHome.hasExtra("number")) {
            match = Integer.parseInt(backToHome.getExtras().getString("number")) + 1;

            SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
            editor.putInt("match_number", match);
            editor.commit();
        } else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            match = prefs.getInt("match_number", 1);
        }
        numberOfMatch.setText(Integer.toString(match));
        matchNumber = numberOfMatch.getText().toString();
        //Set the editTexts so that the user can't change it unless override
        numberOfMatch.setFocusable(false);
        teamNumberOne.setFocusable(false);
        teamNumberTwo.setFocusable(false);
        teamNumberThree.setFocusable(false);
        matchName = numberOfMatch.getText().toString();

        updateSuperData();


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI();
            }
        }, new IntentFilter("matches_updated"));

        //Change team numbers as the user changes the match number
        EditText editText = (EditText) findViewById(R.id.matchNumber);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                matchName = s.toString();
                updateUI();
            }
        });
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
        if (id == R.id.changeAlliance){
            isRed = !isRed;
            updateUI();
        }
        if (id == R.id.scout) {
            //check to see if all data inputs were filled out before continuing
            if (numberOfMatch.
                    getText().toString().equals("")) {
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
                startActivity(intent);
            }
        }else if (id == R.id.action_override) {
            numberOfMatch.setFocusableInTouchMode(true);
            teamNumberOne.setFocusableInTouchMode(true);
            teamNumberTwo.setFocusableInTouchMode(true);
            teamNumberThree.setFocusableInTouchMode(true);
        }else if(id == R.id.unoverride){
            updateUI();
            numberOfMatch.setFocusable(false);
            teamNumberOne.setFocusable(false);
            teamNumberTwo.setFocusable(false);
            teamNumberThree.setFocusable(false);
        }
            return super.onOptionsItemSelected(item);
    }


    public void updateSuperData(){
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
                ListView listView = (ListView)context.findViewById(R.id.view_files_received);
                listView.setAdapter(adapter);
            }
        });
    }
    public void updateScoutData(){
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
                ListView listView = (ListView)context.findViewById(R.id.view_files_received);
                listView.setAdapter(adapter);
            }
        });

    }

    private void updateUI() {
        if (FirebaseLists.matchesList.getKeys().contains(matchName)) {
            Match match = FirebaseLists.matchesList.getFirebaseObjectByKey(matchName);

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
}


