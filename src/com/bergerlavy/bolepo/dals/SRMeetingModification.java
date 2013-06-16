package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

public class SRMeetingModification extends ServerResponse {
	private String mDescription;
	
	private String mMeetingHash;
	private List<Participant> mParticipants;
	
	private SRMeetingModification(Builder builder) {
		mMeetingHash = builder.mMeetingHash;
		mParticipants = builder.mParticipants;
		mDescription = builder.mDescription;

		setFailureCode(builder.mFailureCode);
	}
	
	@Override
	public boolean hasData() {
		return mMeetingHash != null && mParticipants != null && mParticipants.size() > 0;
	}
	
	public String getMeetingHash() {
		return mMeetingHash;
	}
	
	public List<Participant> getParticipants() {
		return mParticipants;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public static class Builder {
		/* required */
		private final int mFailureCode;
		private final String mDescription;
		
		/* optional */
		private String mMeetingHash;
		private List<Participant> mParticipants = new ArrayList<Participant>();
		
		public Builder(int failureCode, String description) {
			this.mFailureCode = failureCode;
			this.mDescription = description;
		}
		
		public Builder setParticipants(List<Participant> participants) {
			this.mParticipants = participants;
			return this;
		}
		
		public Builder setMeetingHash(String meetingHash) {
			this.mMeetingHash = meetingHash;
			return this;
		}
		
		public SRMeetingModification build() {
			return new SRMeetingModification(this);
		}
	}
}
