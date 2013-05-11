package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRMeetingCreation extends ServerResponse {

	private String mDescription;
	
	private String mMeetingHash;
	private List<Participant> mParticipants;
	
	private SRMeetingCreation(Builder builder) {
		mMeetingHash = builder.mMeetingHash;
		mParticipants = builder.mParticipants;
		mDescription = builder.mDescription;
		setStatus(builder.mStatus);
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
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		/* optional */
		private String mMeetingHash;
		private List<Participant> mParticipants = new ArrayList<Participant>();
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
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
		
		public SRMeetingCreation build() {
			return new SRMeetingCreation(this);
		}
	}
}
