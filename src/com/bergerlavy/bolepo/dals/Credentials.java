package com.bergerlavy.bolepo.dals;

/* every change in the string of an ENUM element obligates a corresponding change in the server software */

public enum Credentials {
	READ ("read"),
	ROOT ("root");
	
	private final String mCredentials;
	
	private Credentials(String credentials) {
		mCredentials = credentials;
	}
	
	public String getCredentials() {
		return mCredentials;
	}
}
