package com.bergerlavy.bolepo.dals;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRMeetingManagerReplacementAndRemoval extends ServerResponse {
	private String mDescription;
	private String mMeetingNewHash;
	private String mOldManagerNewHash;
	private String mNewManagerNewHash;
	
	private SRMeetingManagerReplacementAndRemoval(Builder builder) {
		mDescription = builder.mDescription;
		mMeetingNewHash = builder.mMeetingNewHash;
		mOldManagerNewHash = builder.mOldManagerNewHash;
		mNewManagerNewHash = builder.mNewManagerNewHash;
		setStatus(builder.mStatus);
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
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		/* optional */
		private String mMeetingNewHash;
		private String mOldManagerNewHash;
		private String mNewManagerNewHash;
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
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

		public SRMeetingManagerReplacementAndRemoval build() {
			return new SRMeetingManagerReplacementAndRemoval(this);
		}
	}
}
