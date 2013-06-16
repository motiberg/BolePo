package com.bergerlavy.bolepo.dals;

import java.util.List;

public class SRGcmRegistrationCheck extends ServerResponse {
	private String mDescription;
	
	private List<String> mPhones;
	
	private SRGcmRegistrationCheck(Builder builder) {
		mDescription = builder.mDescription;
		mPhones = builder.mPhones;

		setFailureCode(builder.mFailureCode);
	}

	@Override
	public boolean hasData() {
		return false;
	}

	public String getDescription() {
		return mDescription;
	}
	
	public List<String> getRegistersContactsPhones() {
		return mPhones;
	}
	
	public static class Builder {
		/* required */
		private final int mFailureCode;
		private final String mDescription;
		
		/* optional */
		private List<String> mPhones;
		
		public Builder(int failureCode, String description) {
			this.mFailureCode = failureCode;
			this.mDescription = description;
		}
		
		public Builder setRegisteredContactsPhones(List<String> phones) {
			this.mPhones = phones;
			return this;
		}

		public SRGcmRegistrationCheck build() {
			return new SRGcmRegistrationCheck(this);
		}
	}
}
