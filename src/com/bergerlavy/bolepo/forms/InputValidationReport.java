package com.bergerlavy.bolepo.forms;

public class InputValidationReport {

	private boolean mStatus;
	private String mError;
	
	private InputValidationReport(Builder builder) {
		mStatus = builder.mStatus;
		mError = builder.mError;
	}
	
	public boolean isOK() {
		return mStatus;
	}
	
	public String getError() {
		return mError;
	}
	
	public static class Builder {
		
		/* Required */
		private final boolean mStatus;
		
		/* Optional */
		private String mError;
		
		public Builder(boolean status) {
			mStatus = status;
		}
		
		public Builder setError(String error) {
			mError = error;
			return this;
		}
		
		public InputValidationReport build() {
			return new InputValidationReport(this);
		}
	}
	
}
