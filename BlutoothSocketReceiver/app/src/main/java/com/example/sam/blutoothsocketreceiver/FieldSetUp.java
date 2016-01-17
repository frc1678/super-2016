package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FieldSetUp extends ActionBarActivity {
    List<ToggleButton> toggleButtonList;
    String matchName;
    String teamOneNumber;
    String teamTwoNumber;
    String teamThreeNumber;
    String alliance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fieldsetup);
        Intent intent = getIntent();
        matchName = intent.getExtras().getString("matchNumber");
        teamOneNumber = intent.getExtras().getString("teamNumberOne");
        teamTwoNumber = intent.getExtras().getString("teamNumberTwo");
        teamThreeNumber = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");

        toggleButtonList = new ArrayList<>();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ArrayList<String> defenses = new ArrayList<>(Arrays.asList("P.C", "S.P", "D.B", "C.D.F", "R.T", "R.P", "R.W", "M.T"));

        LinearLayout layout = (LinearLayout) findViewById(R.id.row1_of_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 4; i++) {
            TextView column_number = new TextView(this);
            column_number.setText(Integer.toString(i + 1));
            LinearLayout columns = new LinearLayout(this);
            columns.setOrientation(LinearLayout.HORIZONTAL);
            columns.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            column_number.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            for (int j = 0; j < 7; j++) {
                ToggleButton defenseButton = new ToggleButton(this);
                defenseButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                defenseButton.setText(defenses.get(j).toString());
                defenseButton.setTextOn(defenses.get(j).toString());
                defenseButton.setTextOff(defenses.get(j).toString());
                columns.addView(defenseButton);
                toggleButtonList.add(defenseButton);

            }
            layout.addView(column_number);
            layout.addView(columns);
            System.out.println("10");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.superdata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nextButton) {
            JSONObject data = new JSONObject();
            Intent next = new Intent(this, Super_Scouting.class);
            for(int i = 0; i < toggleButtonList.size(); i++){
                if(toggleButtonList.get(i).isChecked()){
                    try {
                        data.put("defenseChecked", toggleButtonList.get(i).getText().toString());
                    }catch (JSONException JE){
                        Log.e("check", "failed to convert to json object");
                    }

                }
            }
            next.putExtra("defenseChecked", data.toString());
            next.putExtra("matchNumber", matchName);
            next.putExtra("teamNumberOne", teamOneNumber);
            next.putExtra("teamNumberTwo", teamTwoNumber);
            next.putExtra("teamNumberThree", teamThreeNumber);
            next.putExtra("alliance", alliance);
            System.out.println(data.toString());
            startActivity(next);

        }
            return super.onOptionsItemSelected(item);
        }

    }



