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

import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FieldSetUp extends ActionBarActivity {
    String matchNumber;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fieldsetup);
        Intent intent = getIntent();
        matchNumber = (String) savedInstanceState.getSerializable("MATCH_NUMBER");
        teamNumberOne = (String) savedInstanceState.getSerializable("TEAM_NUMBER_ONE");
        teamNumberTwo = (String) savedInstanceState.getSerializable("TEAM_NUMBER_TWO");
        teamNumberThree = (String) savedInstanceState.getSerializable("TEAM_NUMBER_THREE");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ArrayList<String> defenses = new ArrayList<>(Arrays.asList("P.C", "S.P", "D.B", "C.D.F", "R.T", "R.P", "R.W", "M.T"));
        /*defenses.add(0, "P.C");
        defenses.add(1, "S.P");
        defenses.add(2, "D.B");
        defenses.add(3, "C.D.F");
        defenses.add(4, "R.T");
        defenses.add(5, "R.P");
        defenses.add(6, "R.W");
        defenses.add(7, "M.T");
*/
            LinearLayout layout = (LinearLayout) findViewById(R.id.row1_of_buttons);
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < 4; i++) {
                TextView column_number = new TextView(this);
                column_number.setText(Integer.toString(i + 1));
                column_number.setId(i);
                LinearLayout columns = new LinearLayout(this);
                columns.setOrientation(LinearLayout.HORIZONTAL);
                columns.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                column_number.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                for (int j = 0; j < 7; j++) {
                    Button defenseButton = new Button(this);
                    defenseButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    defenseButton.setText(defenses.get(j).toString());
                    defenseButton.setId(j);
                    columns.addView(defenseButton);
                }
                layout.addView(column_number);
                layout.addView(columns);
                System.out.println("10");
            }

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
            Intent next = new Intent(this,Super_Scouting.class);
            next.putExtra("MATCH_NUMBER", matchNumber.toString());
            next.putExtra("TEAM_NUMBER_ONE", teamNumberOne);
            next.putExtra("TEAM_NUMBER_TWO", teamNumberTwo);
            next.putExtra("TEAM_NUMBER_THREE", teamNumberThree);
            startActivity(next);
        }


        return super.onOptionsItemSelected(item);
    }

}


