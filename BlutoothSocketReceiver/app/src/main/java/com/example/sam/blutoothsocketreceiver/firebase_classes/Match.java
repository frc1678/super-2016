package com.example.sam.blutoothsocketreceiver.firebase_classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by citruscircuits on 1/17/16
 */

public class Match extends Object {
	public Integer number;
	public CalculatedMatchData calculatedData;
	public List<Integer> redAllianceTeamNumbers;
	public List<Integer> blueAllianceTeamNumbers;
	public Integer redScore;
	public Integer blueScore;
	public List<String> redDefensePositions;
	public List<String> blueDefensePositions;
	public Boolean redAllianceDidCapture;
	public Boolean blueAllianceDidCapture;
	public Boolean redAllianceDidBreach;
	public Boolean blueAllianceDidBreach;
	public String BSNotes;
	public String RSNotes;
}