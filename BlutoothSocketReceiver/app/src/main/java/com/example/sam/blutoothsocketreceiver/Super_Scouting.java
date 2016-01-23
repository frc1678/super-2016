package com.example.sam.blutoothsocketreceiver;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

public class Super_Scouting extends ActionBarActivity {
    TextView teamNumber1;
    TextView teamNumber2;
    TextView teamNumber3;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String firstDefense;
    String secondDefense;
    String thirdDefense;
    String fourthDefense;
    String alliance;
    ArrayList <String> defenses;
    ArrayList <String> dataScore;
    ArrayList <String> teamOneDataName;
    ArrayList <String> teamOneDataScore;
    ArrayList <String> teamTwoDataName;
    ArrayList <String> teamTwoDataScore;
    ArrayList <String> teamThreeDataName;
    ArrayList <String> teamThreeDataScore;

    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent next = getIntent();
        object = new JSONObject();

        numberOfMatch = next.getExtras().getString("matchNumber");
        teamNumberOne = next.getExtras().getString("teamNumberOne");
        teamNumberTwo = next.getExtras().getString("teamNumberTwo");
        teamNumberThree = next.getExtras().getString("teamNumberThree");
        alliance = next.getExtras().getString("alliance");
        firstDefense = next.getExtras().getString("firstDefensePicked");
        secondDefense = next.getExtras().getString("secondDefensePicked");
        thirdDefense = next.getExtras().getString("thirdDefensePicked");
        fourthDefense = next.getExtras().getString("fourthDefensePicked");


        teamNumber1 = (TextView) findViewById(R.id.team1);
        teamNumber2 = (TextView) findViewById(R.id.team2);
        teamNumber3 = (TextView) findViewById(R.id.team3);
        if(alliance.equals("Blue Alliance")) {
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.BLUE);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.BLUE);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.BLUE);
        }else if(alliance.equals("Red Alliance")){
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.RED);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.RED);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.RED);
        }

        defenses = new ArrayList<>(Arrays.asList(firstDefense, secondDefense,thirdDefense, fourthDefense));
        dataScore = new ArrayList<>();
        teamOneDataName = new ArrayList<>();
        teamOneDataScore = new ArrayList<>();
        teamTwoDataName = new ArrayList<>();
        teamTwoDataScore = new ArrayList<>();
        teamThreeDataName = new ArrayList<>();
        teamThreeDataScore = new ArrayList<>();

        ArrayList<String> data = new ArrayList<>(Arrays.asList("rankSpeed", "rankTorque", "rankDefense", "rankEvasion", "rankBallControl"));
        for(int k = 0; k < defenses.size(); k++){
            data.add("Cross Eff" +" " + defenses.get(k));
        }
        data.add("Cross Eff" + " " + "Low Bar");
        LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
        LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
        LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);


        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(1 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamOneRelativeLayout.addView(counter);
        }
        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(2 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamTwoRelativeLayout.addView(counter);
        }
        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(2 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamThreeRelativeLayout.addView(counter);
        }
    }


    private View createCounter(String title) {

        LayoutInflater inflater = getLayoutInflater();
        View counter = inflater.inflate(R.layout.counter, null);
        TextView dataNameTextView = (TextView)counter.findViewById(R.id.dataName);
        dataNameTextView.setText(title);
        final TextView incrementor = (TextView) counter.findViewById(R.id.scoreCounter);
        Button plusButton = (Button)counter.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current++;
                if(current > 3){
                    incrementor.setText(Integer.toString(3));
                }else {
                    incrementor.setText(Integer.toString(current));
                }
            }
        });
        Button minusButton = (Button)counter.findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current--;
                if (current < 0) {
                    incrementor.setText(Integer.toString(0));
                }else{
                    incrementor.setText(Integer.toString(current));
                }

            }
        });
        return counter;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finaldata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.finalNext) {
            Intent intent = new Intent(this, FinalDataPoints.class);
            intent.putExtra("matchNumber", numberOfMatch);
            intent.putExtra("teamNumberOne", teamNumberOne);
            intent.putExtra("teamNumberTwo", teamNumberTwo);
            intent.putExtra("teamNumberThree", teamNumberThree);
            intent.putExtra("alliance", alliance);
            LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
            LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
            LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);
            for(int i = 0; i < teamOneRelativeLayout.getChildCount(); i++){
                View teamOneLayout = teamOneRelativeLayout.getChildAt(i);
                TextView nameOfData1 = (TextView)teamOneLayout.findViewById(R.id.dataName);
                TextView scoreOfData1 = (TextView)teamOneLayout.findViewById(R.id.scoreCounter);
                teamOneDataName.add((nameOfData1.getText().toString()));
                teamOneDataScore.add(scoreOfData1.getText().toString());

            }
            for(int j = 0; j < teamTwoRelativeLayout.getChildCount(); j++ ){
                View teamTwoLayout = teamTwoRelativeLayout.getChildAt(j);
                TextView nameOfData2 = (TextView)teamTwoLayout.findViewById(R.id.dataName);
                TextView scoreOfData2 = (TextView)teamTwoLayout.findViewById(R.id.scoreCounter);
                teamTwoDataName.add(nameOfData2.getText().toString());
                teamTwoDataScore.add(scoreOfData2.getText().toString());

            }
            for(int k = 0; k < teamThreeRelativeLayout.getChildCount(); k++){
                View teamThreeLayout = teamThreeRelativeLayout.getChildAt(k);
                TextView nameOfData3 = (TextView)teamThreeLayout.findViewById(R.id.dataName);
                TextView scoreOfData3 = (TextView)teamThreeLayout.findViewById(R.id.scoreCounter);
                teamThreeDataName.add(nameOfData3.getText().toString());
                teamThreeDataScore.add(scoreOfData3.getText().toString());

            }
            intent.putStringArrayListExtra("dataNameOne", teamOneDataName);
            intent.putStringArrayListExtra("ranksOfOne", teamOneDataScore);

            intent.putStringArrayListExtra("dataNameTwo", teamTwoDataName);
            intent.putStringArrayListExtra("ranksOfTwo", teamTwoDataScore);

            intent.putStringArrayListExtra("dataNameThree", teamThreeDataName);
            intent.putStringArrayListExtra("ranksOfThree", teamThreeDataScore);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}


