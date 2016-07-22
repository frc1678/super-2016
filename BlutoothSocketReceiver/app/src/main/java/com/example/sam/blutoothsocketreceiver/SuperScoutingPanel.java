package com.example.sam.blutoothsocketreceiver;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 5/12/16.
 */
public class SuperScoutingPanel extends Fragment {
    Boolean isRed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.super_scouting_panel, container, false);
    }

    public void setAllianceColor(boolean allianceColor) {
        TextView teamNumberTextView = (TextView)getView().findViewById(R.id.teamNumberTextView);
        this.isRed = allianceColor;
        if (isRed){
            teamNumberTextView.setTextColor(Color.RED);
        }else {
            teamNumberTextView.setTextColor(Color.BLUE);
        }
    }

    public void setTeamNumber(String teamNumber) {
        TextView teamNumberTextView = (TextView)getView().findViewById(R.id.teamNumberTextView);
        teamNumberTextView.setText(teamNumber);
    }

    public int getDataNameCount(){
        int numOfDataName = ((LinearLayout)getView()).getChildCount();
        Log.e("dataNameCount", Integer.toString(numOfDataName));
        return numOfDataName;
    }


    public Map getData(){
        Map<String, Integer> mapOfData = new HashMap<>();
        LinearLayout rootLayout = (LinearLayout)getView();
        Counter counter;
        for (int i = 0; i < ((LinearLayout)getView()).getChildCount() - 1; i++) {
            counter = (Counter)rootLayout.getChildAt(i + 1);
            String dataName = counter.getDataName();
            Integer dataScore = counter.getDataValue();
            mapOfData.put(dataName, dataScore);
        }

        return mapOfData;
    }

}

