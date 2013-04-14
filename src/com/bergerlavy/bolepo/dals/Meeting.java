package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;


public class Meeting {

	private String mName;
	private String mDate;
	private String mTime;
	private String mHash;
	private String mCreator;
	private String mLocation;
	private String mShareLocationTime;
	private List<String> mParticipants;
	
	public Meeting(String name, String date, String time, String creator, String location, String shareLocationTime, List<String> participants) {
		mName = name;
		mDate = date;
		mTime = time;
		mCreator = creator;
		mLocation = location;
		mShareLocationTime = shareLocationTime;
		mParticipants = new ArrayList<String>();
		if (participants != null)
			mParticipants.addAll(participants);
	}

	public String getName() {
		return mName;
	}

	public String getDate() {
		return mDate;
	}

	public String getTime() {
		return mTime;
	}
	
	public String getCreator() {
		return mCreator;
	}

	public String getLocation() {
		return mLocation;
	}

	public String getShareLocationTime() {
		return mShareLocationTime;
	}
	
	public List<String> getParticipants() {
		return mParticipants;
	}
	
	public String getParticipant(int location) {
		if (location < 0 || location >= mParticipants.size()) 
			return mParticipants.get(location);
		return null;
	}
	
	public int getParticipantsNum() {
		return mParticipants.size();
	}
	
	public String getHash() {
		return mHash;
	}
	
	public void setHash(String hash) {
		mHash = hash;
	}
	
}
