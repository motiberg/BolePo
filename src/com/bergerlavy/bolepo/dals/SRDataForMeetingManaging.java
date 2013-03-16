package com.bergerlavy.bolepo.dals;

import java.util.HashMap;

public class SRDataForMeetingManaging {
	
	private String mMeetingHash;
	private HashMap<String, String> mParticipantsHash;
	
	public SRDataForMeetingManaging() {
		mMeetingHash = null;
		mParticipantsHash = new HashMap<String, String>();
	}
	
	public String getMeetingHash() {
		return mMeetingHash;
	}
	
	public void setMeetingHash(String mMeetingHash) {
		this.mMeetingHash = mMeetingHash;
	}
	
	public HashMap<String, String> getParticipantsHash() {
		return mParticipantsHash;
	}
	
	public void setParticipantsHash(HashMap<String, String> participantsHash) {
		this.mParticipantsHash = participantsHash;
	}
	
}
