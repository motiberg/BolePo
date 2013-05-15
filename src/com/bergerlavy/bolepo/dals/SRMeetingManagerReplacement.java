package com.bergerlavy.bolepo.dals;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRMeetingManagerReplacement extends ServerResponse {

private String mDescription;
	private String mMeetingHash;
	private String mOldManagerHash;
	private String mNewManagerHash;
	
	private SRMeetingManagerReplacement(Builder builder) {
		mDescription = builder.mDescription;
		mMeetingHash = builder.mMeetingHash;
		mOldManagerHash = builder.mOldManagerHash;
		mNewManagerHash = builder.mNewManagerHash;
		setStatus(builder.mStatus);
	}
	
	@Override
	public boolean hasData() {
		return mMeetingHash != null || mOldManagerHash != null || mNewManagerHash != null;
	}

	public String getDescription() {
		return mDescription;
	}
	
	public String getMeetingHash() {
		return mMeetingHash;
	}
	
	public String getOldManagerHash() {
		return mOldManagerHash;
	}
	
	public String getNewManagerHash() {
		return mNewManagerHash;
	}
	
	public static class Builder {
		/* required */
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		/* optional */
		private String mMeetingHash;
		private String mOldManagerHash;
		private String mNewManagerHash;
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
			this.mDescription = description;
		}
		
		public Builder setMeetingHash(String meetingHash) {
			this.mMeetingHash = meetingHash;
			return this;
		}
		
		public Builder setOldManagerHash(String hash) {
			this.mOldManagerHash = hash;
			return this;
		}
		
		public Builder setNewManagerHash(String hash) {
			this.mNewManagerHash = hash;
			return this;
		}

		public SRMeetingManagerReplacement build() {
			return new SRMeetingManagerReplacement(this);
		}
	}
}
