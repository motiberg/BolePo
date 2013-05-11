package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRMeetingRetrieval extends ServerResponse {
	private List<Participant> mParticipants;
	private Meeting mMeeting;
	private String mDescription;
	
	private SRMeetingRetrieval(Builder builder) {
		mParticipants = builder.mParticipants;
		mMeeting = builder.mMeeting;
		mDescription = builder.mDescription;
		setStatus(builder.mStatus);
	}

	@Override
	public boolean hasData() {
		return mMeeting != null && mParticipants != null && mParticipants.size() > 0;
	}
	
	public List<Participant> getParticipants() {
		return mParticipants;
	}
	
	public Meeting getMeeting() {
		return mMeeting;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public static class Builder {
		/* required */
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		/* optional */
		private List<Participant> mParticipants = new ArrayList<Participant>();
		private Meeting mMeeting;
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
			this.mDescription = description;
		}
		
		public Builder setParticipants(List<Participant> participants) {
			this.mParticipants = participants;
			return this;
		}

		public Builder setMeeting(Meeting meeting) {
			this.mMeeting = meeting;
			return this;
		}
		
		public SRMeetingRetrieval build() {
			return new SRMeetingRetrieval(this);
		}
	}
}
