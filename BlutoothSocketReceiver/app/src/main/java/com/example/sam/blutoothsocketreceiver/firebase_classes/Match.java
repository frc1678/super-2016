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
	private Boolean redAllianceDidCapture;
	private Boolean blueAllianceDidCapture;
	private Boolean redAllianceDidBreach;
	private Boolean blueAllianceDidBreach;

	public Match(){

	}

	public void setRedAllianceDidCapture(Object redAllianceDidCapture){	//TODO: TEST THESE
		try {
			this.redAllianceDidCapture = (Boolean) redAllianceDidCapture;
		} catch (ClassCastException cce){
			this.redAllianceDidCapture = Boolean.valueOf((String) redAllianceDidCapture);
		}
	}

	public void setBlueAllianceDidCapture(Object blueAllianceDidCapture){
		try{
			this.blueAllianceDidCapture = (Boolean) blueAllianceDidCapture;
		} catch (ClassCastException cce){
			this.blueAllianceDidCapture = Boolean.valueOf((String) blueAllianceDidCapture);
		}
	}

	public void setRedAllianceDidBreach(Object redAllianceDidBreach){
		try{
			this.redAllianceDidBreach = (Boolean) redAllianceDidBreach;
		} catch (ClassCastException cce) {
			this.redAllianceDidBreach = Boolean.valueOf((String) redAllianceDidBreach);
		}
	}

	public void setBlueAllianceDidBreach(Object blueAllianceDidBreach){
		try{
			this.blueAllianceDidBreach = (Boolean) blueAllianceDidBreach;
		}catch (ClassCastException cce){
			this.blueAllianceDidBreach = Boolean.valueOf((String) blueAllianceDidBreach);
		}
	}
}