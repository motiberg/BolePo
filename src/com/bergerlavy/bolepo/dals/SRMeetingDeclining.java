package com.bergerlavy.bolepo.dals;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRMeetingDeclining extends ServerResponse {
	private String mDescription;
	
	private SRMeetingDeclining(Builder builder) {
		mDescription = builder.mDescription;
		setStatus(builder.mStatus);
	}

	@Override
	public boolean hasData() {
		return false;
	}

	public String getDescription() {
		return mDescription;
	}
	
	public static class Builder {
		/* required */
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
			this.mDescription = description;
		}

		public SRMeetingDeclining build() {
			return new SRMeetingDeclining(this);
		}
	}
}
