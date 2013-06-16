package com.bergerlavy.bolepo.dals;


public class SRMeetingManagerReplacement extends ServerResponse {

private String mDescription;
	private String mMeetingNewHash;
	private String mOldManagerNewHash;
	private String mNewManagerNewHash;
	
	private SRMeetingManagerReplacement(Builder builder) {
		mDescription = builder.mDescription;
		mMeetingNewHash = builder.mMeetingNewHash;
		mOldManagerNewHash = builder.mOldManagerNewHash;
		mNewManagerNewHash = builder.mNewManagerNewHash;

		setFailureCode(builder.mFailureCode);
	}
	
	@Override
	public boolean hasData() {
		return isOK() && (mMeetingNewHash != null || mOldManagerNewHash != null || mNewManagerNewHash != null);
	}

	public String getDescription() {
		return mDescription;
	}
	
	public String getMeetingNewHash() {
		return mMeetingNewHash;
	}
	
	public String getOldManagerNewHash() {
		return mOldManagerNewHash;
	}
	
	public String getNewManagerNewHash() {
		return mNewManagerNewHash;
	}
	
	public static class Builder {
		/* required */
		private final int mFailureCode;
		private final String mDescription;
		
		/* optional */
		private String mMeetingNewHash;
		private String mOldManagerNewHash;
		private String mNewManagerNewHash;
		
		public Builder(int failureCode, String description) {
			this.mFailureCode = failureCode;
			this.mDescription = description;
		}
		
		public Builder setMeetingNewHash(String meetingHash) {
			this.mMeetingNewHash = meetingHash;
			return this;
		}
		
		public Builder setOldManagerNewHash(String hash) {
			this.mOldManagerNewHash = hash;
			return this;
		}
		
		public Builder setNewManagerNewHash(String hash) {
			this.mNewManagerNewHash = hash;
			return this;
		}

		public SRMeetingManagerReplacement build() {
			return new SRMeetingManagerReplacement(this);
		}
	}
}
