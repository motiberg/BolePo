package com.bergerlavy.bolepo.dals;


public class SRMeetingAttendance extends ServerResponse {
	private String mDescription;
	
	private SRMeetingAttendance(Builder builder) {
		mDescription = builder.mDescription;

		setFailureCode(builder.mFailureCode);
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
		private final int mFailureCode;
		private final String mDescription;
		
		public Builder(int failureCode, String description) {
			this.mFailureCode = failureCode;
			this.mDescription = description;
		}

		public SRMeetingAttendance build() {
			return new SRMeetingAttendance(this);
		}
	}
}
