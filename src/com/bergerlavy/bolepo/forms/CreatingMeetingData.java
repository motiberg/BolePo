package com.bergerlavy.bolepo.forms;

import java.util.List;

public class CreatingMeetingData {

	private String mPurpose;
	private String mDate;
	private String mTime;
	private String mLocation;
	private String mShareLocationTime;
	private List<Participant> mParticipants;
	
	public CreatingMeetingData(String purpose, String date, String time, String location, String shareLocationTime, List<Participant> participants) {
		mPurpose = purpose;
		mDate = date;
		mTime = time;
		mLocation = location;
		mShareLocationTime = shareLocationTime;
		mParticipants.addAll(participants);
	}

	public String getPurpose() {
		return mPurpose;
	}

	public String getDate() {
		return mDate;
	}

	public String getTime() {
		return mTime;
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
