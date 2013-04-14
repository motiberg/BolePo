package com.bergerlavy.bolepo.dals;

public class Participant {

	private String mName;
	private long mMeetingID;
	private String mCredentials;
	private String mRSVP;
//	private String mShareLocationStatus;
	private String mHash;
	
	private Participant(Builder builder) {
		mName = builder.name;
		mCredentials = builder.credentials;
		mRSVP = builder.rsvp;
		mHash = builder.hash;
//		mShareLocationStatus = ShareLocationStatus;
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
	
//	public String getShareLocationStatus() {
//		return mShareLocationStatus;
//	}
	
	public String getHash() {
		return mHash;
	}
	
//	public void setMeetingID(long id) {
//		mMeetingID = id;
//	}
	
	public void setHash(String hash) {
		mHash = hash;
	}
	
	public static class Builder {
		
		/* Required */
		private String name;

		/* Optional */
		private String credentials = "read";
		private String rsvp = "unknown";
		private String hash;
		
		public Builder(String name, String hash) {
			this.name = name;
			this.hash = hash;
		}
		
		public Builder setCredentials(String credentials) {
			if (credentials == null)
				return this;
			this.credentials = credentials;
			return this;
		}
		
		public Builder setRsvp(String rsvp) {
			if (rsvp == null)
				return this;
			this.rsvp = rsvp;
			return this;
		}
		
//		public Builder setHash(String hash) {
//			this.hash = hash;
//			return this;
//		}
		
		public Participant build() {
			return new Participant(this);
		}
	}
}