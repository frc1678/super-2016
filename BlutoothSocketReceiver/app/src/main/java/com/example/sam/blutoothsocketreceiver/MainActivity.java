package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    EditText name;
    EditText foul;
    EditText alliance_score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        accept_loop loop = new accept_loop(context);
        loop.start();
        Firebase.setAndroidContext(this);
        changing = (TextView)findViewById(R.id.text);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
        myFirebaseRef.keepSynced(true);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        name = (EditText)findViewById(R.id.userName);
        foul = (EditText)findViewById(R.id.foul_Points);
        alliance_score = (EditText)findViewById(R.id.scoreEdit);
        file = null;
    }
    public void deleteAllFiles(){
        ListView listView = (ListView)findViewById(R.id.view_files_received);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapter.add("");
        listView.setAdapter(adapter);
    }
    public void sendData(){
        try {
            File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MassStringText");
            //can delete when doing the actual thing
            file = new PrintWriter(new FileOutputStream(new File(dir, "Send-Data.txt" + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()))));
        } catch (IOException IOE) {
            Log.e("File error", "Failed to open File");
        }
        String user_name = name.getText().toString();
        String foul_points = foul.getText().toString();
        String alliance = alliance_score.getText().toString();
        file.println(user_name + "\n" + foul_points + "\n" + alliance);
        updateList();
        Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
        myFirebaseRef.child("Super Scout Data").child("Data").setValue(user_name + "\n" + foul_points + "\n" + alliance);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateList(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/MatchData/");
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
}


