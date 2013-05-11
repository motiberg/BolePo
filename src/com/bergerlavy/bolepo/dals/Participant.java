package com.bergerlavy.bolepo.dals;

import com.bergerlavy.bolepo.BolePoConstants.Credentials;

public class Participant {

	private String mPhone;
	private String mName;
//	private long mMeetingID;
	private String mCredentials;
	private String mRSVP;
//	private String mShareLocationStatus;
	private String mHash;
	
	private Participant(Builder builder) {
		mPhone = builder.phone;
		mName = builder.name;
		mCredentials = builder.credentials;
		mRSVP = builder.rsvp;
		mHash = builder.hash;
//		mShareLocationStatus = ShareLocationStatus;
	}
	
	public String getPhone() {
		return mPhone;
	}
	
	public String getName() {
		return mName;
	}
	
//	public long getMeetingID() {
//		return mMeetingID;
//	}
	
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
		private String phone;

		/* Optional */
		private String name;
		private String credentials = Credentials.REGULAR.toString();
		private String rsvp = RSVP.UNKNOWN.toString();
		private String hash;
		
		public Builder(String phone) {
			this.phone = phone;
		}
		
		public Builder setName(String name) {
			if (name != null)
				this.name = name;
			return this;
		}
		
		public Builder setCredentials(String credentials) {
			if (credentials != null)
				this.credentials = credentials;
			return this;
		}
		
		public Builder setRsvp(String rsvp) {
			if (rsvp != null)
				this.rsvp = rsvp;
			return this;
		}
		
		public Builder setHash(String hash) {
			if (hash != null)
				this.hash = hash;
			return this;
		}
		
		public Participant build() {
			return new Participant(this);
		}
	}
}