package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

public class SRDataForMeetingManaging {
	
	private String mMeetingHash;
	private List<Participant> mParticipants;
	private Meeting mMeeting;
	private String mDescription;
	
	private SRDataForMeetingManaging(Builder builder) {
		mMeetingHash = builder.mMeetingHash;
		mParticipants = builder.mParticipants;
		mMeeting = builder.mMeeting;
		mDescription = builder.mDescription;
	}
	
	public String getMeetingHash() {
		return mMeetingHash;
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
		private final String mDescription;
		
		/* optional */
		private String mMeetingHash;
		private List<Participant> mParticipants = new ArrayList<Participant>();
		private Meeting mMeeting;
		
		public Builder(String description) {
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
		
		public Builder setMeeting(Meeting meeting) {
			this.mMeeting = meeting;
			return this;
		}
		
		public SRDataForMeetingManaging build() {
			return new SRDataForMeetingManaging(this);
		}
	}
	
}
