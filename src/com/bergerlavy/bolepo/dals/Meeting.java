package com.bergerlavy.bolepo.dals;

import java.util.List;


public class Meeting {

	private String mName;
	private String mDate;
	private String mTime;
	private String mCreator;
	private String mLocation;
	private String mShareLocationTime;
	private List<Participant> mParticipants;
	
	public Meeting(String name, String date, String time, String creator, String location, String shareLocationTime, List<Participant> participants) {
		mName = name;
		mDate = date;
		mTime = time;
		mCreator = creator;
		mLocation = location;
		mShareLocationTime = shareLocationTime;
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
	
	public List<Participant> getParticipants() {
		return mParticipants;
	}
	
	public Participant getParticipant(int location) {
		if (location < 0 || location >= mParticipants.size()) 
			return mParticipants.get(location);
		return null;
	}
	
	public int getParticipantsNum() {
		return mParticipants.size();
	}
	
}
