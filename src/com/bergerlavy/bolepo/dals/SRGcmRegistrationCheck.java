package com.bergerlavy.bolepo.dals;

import java.util.List;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class SRGcmRegistrationCheck extends ServerResponse {
	private String mDescription;
	
	private List<String> mPhones;
	
	private SRGcmRegistrationCheck(Builder builder) {
		mDescription = builder.mDescription;
		mPhones = builder.mPhones;
		setStatus(builder.mStatus);
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
		private final ServerResponseStatus mStatus;
		private final String mDescription;
		
		/* optional */
		private List<String> mPhones;
		
		public Builder(ServerResponseStatus status, String description) {
			this.mStatus = status;
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
