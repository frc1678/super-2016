package com.example.sam.blutoothsocketreceiver.firebase_classes;

import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */

public class TeamInMatchData extends Object {
	public Integer teamNumber;
	public Integer matchNumber;

	public Boolean didGetIncapacitated;
	public Boolean didGetDisabled;

	public Integer rankTorque;
	public Integer rankSpeed;
	public Integer rankEvasion;
	public Integer rankDefense;
	public Integer rankBallControl;

	//Auto
	public List<Integer> ballsIntakedAuto;
	public Integer numBallsKnockedOffMidlineAuto;
	public Map<String, Map<String, Integer>> timesDefensesCrossedAuto;
	public Integer numHighShotsMadeAuto;
	public Integer numLowShotsMadeAuto;
	public Integer numHighShotsMissedAuto;
	public Integer numLowShotsMissedAuto;
	public Boolean didReachAuto;

	//Tele
	public Integer numHighShotsMadeTele;
	public Integer numLowShotsMadeTele;
	public Integer numHighShotsMissedTele;
	public Integer numLowShotsMissedTele;
	public Integer numGroundIntakesTele;
	public Integer numShotsBlockedTele;
	public Boolean didScaleTele;
	public Boolean didChallengeTele;
	public Map<String, Map<String, Integer>> timesDefensesCrossedTele;
}