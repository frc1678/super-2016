package com.example.sam.blutoothsocketreceiver.firebase_classes;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */

public class CalculatedMatchData extends Object {
    public Float predictedRedScore;
    public Float predictedBlueScore;
    public Integer numDefensesCrossedByBlue;
    public Integer numDefensesCrossedByRed;
    public Integer numDefenseCrossesByBlue;
    public Integer numDefenseCrossesByRed;
    public Integer blueRPs;
    public Integer redRPs;
    public Float predictedBlueRPs;
    public Float predictedRedRPs;
    public Integer actualBlueRPs;
    public Integer actualRedRPs;
    public Boolean redAllianceDidBreach;
    public Boolean blueAllianceDidBreach;
    public Boolean wasDisfunctional;
    public Map<String, Float> redScoresForDefenses;
    public Map<String, Float> redWinningChanceForDefenses;
    public Map<String, Float> redBreachChanceForDefenses;
    public Map<String, Float> redRPsForDefenses;
    public Map<String, Float> blueScoresForDefenses;
    public Map<String, Float> blueWinningChanceForDefenses;
    public Map<String, Float> blueBreachChanceForDefenses;
    public Map<String, Float> blueRPsForDefenses;
    public CalculatedMatchData calculatedData;
    public Float redWinChance;
    public Float blueWinChance;
    public Float redBreachChance;
    public Float blueBreachChance;
    public Float redCaptureChance;
    public Float blueCaptureChance;
    public Float scoreContribution;
    public Float sdPredictedRedScore;
    public Float sdPredictedBlueScore;
    public Float autoAbilityExcludeD;
    public Float autoAbilityExcludeLb;
    public Float avgNumCrossingsAuto;
    public List<String> optimalRedDefenses;
    public List<String> optimalBlueDefenses;

}