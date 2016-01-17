package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Date;
import com.firebase.client.Firebase;

public class MainActivity extends ActionBarActivity {
    Activity context;
    TextView changing;
    PrintWriter file;
    EditText matchNumber;
    EditText teamNumberOne;
    EditText teamNumberTwo;
    EditText teamNumberThree;
    TextView alliance;
    File dir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        accept_loop loop = new accept_loop(context);
        loop.start();
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
        myFirebaseRef.keepSynced(true);
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
        file = null;
        changing = (TextView) findViewById(R.id.text);
        matchNumber = (EditText)findViewById(R.id.matchNumber);
        teamNumberOne = (EditText)findViewById(R.id.teamOneNumber);
        teamNumberTwo = (EditText)findViewById(R.id.teamTwoNumber);
        teamNumberThree = (EditText)findViewById(R.id.teamThreeNumber);
        alliance = (TextView)findViewById(R.id.allianceName);
        updateSuperData();
        updateScoutData();
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
        if (id == R.id.scout) {
            try {
                dir.mkdir();
                //can delete when doing the actual thing
                file = new PrintWriter(new FileOutputStream(new File(dir, matchNumber.getText().toString() + " " + new SimpleDateFormat("dd-H:mm:ss").format(new Date()))));
            } catch (IOException IOE) {
                Log.e("File error", "Failed to open File");
                return false;
            }
            /*Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
            myFirebaseRef.child("Super Scout Data").child("Data").setValue("");
            System.out.println("sent to firebase");*/
            if (matchNumber.getText().toString().equals("")) {
                Toast.makeText(context, "Input match name!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberOne.getText().toString().equals("")) {
                Toast.makeText(context, "Input team one number!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberTwo.getText().toString().equals("")) {
                Toast.makeText(context, "Input team two number!", Toast.LENGTH_SHORT).show();
            } else if (teamNumberThree.getText().toString().equals("")) {
                Toast.makeText(context, "Input team three number!", Toast.LENGTH_SHORT).show();
            } else {
                file.println();
                Toast.makeText(context, "Sent to file", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FieldSetUp.class);
                intent.putExtra("matchNumber", matchNumber.getText().toString());
                intent.putExtra("teamNumberOne", teamNumberOne.getText().toString());
                intent.putExtra("teamNumberTwo", teamNumberTwo.getText().toString());
                intent.putExtra("teamNumberThree", teamNumberThree.getText().toString());
                intent.putExtra("alliance", alliance.getText().toString());
                startActivity(intent);
            }
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
                    adapter.add(tmpFile.getName() + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()));
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
                File scoutFile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MassStringText");
                if (!scoutFile.mkdir()) {
                    Log.i("File Info", "Failed to make Directory. Unimportant");
                }
                File[] files = scoutFile.listFiles();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
                for (File tmpFile : files) {
                    adapter.add(tmpFile.getName() + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()));
                }
                ListView listView = (ListView)context.findViewById(R.id.view_files_received);
                listView.setAdapter(adapter);
            }
        });



    }

}


