package com.bergerlavy.bolepo.dals;



public class SRMeetingRemoval extends ServerResponse {
	private String mDescription;
	
	private SRMeetingRemoval(Builder builder) {
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

		public SRMeetingRemoval build() {
			return new SRMeetingRemoval(this);
		}
	}
}
