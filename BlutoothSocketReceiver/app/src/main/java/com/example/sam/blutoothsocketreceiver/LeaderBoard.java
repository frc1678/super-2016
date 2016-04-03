package com.example.sam.blutoothsocketreceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 4/3/16.
 */
public class LeaderBoard {
    public LeaderBoard(){
        rankedScouts = new ArrayList<>();
    }
    List<Scout> rankedScouts;
    class Scout{
        String name;
        Float score;
        Integer numOfMatches;
    }
}
