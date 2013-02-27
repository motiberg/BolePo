package com.bergerlavy.bolepo.dals;

public class Participant {

	private String mName;
	private long mMeetingID;
	private String mCredentials;
	private String mRSVP;
	private String mShareLocationStatus;
	private String mHash;
	
	public Participant(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public long getMeetingID() {
		return mMeetingID;
	}
	
	public String getCredentials() {
		return mCredentials;
	}
	
	public String getRSVP() {
		return mRSVP;
	}
	
	public String getShareLocationStatus() {
		return mShareLocationStatus;
	}
	
	public String getHash() {
		return mHash;
	}
}