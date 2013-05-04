package com.bergerlavy.bolepo.dals;

public enum RSVP {
	YES ("yes"),
	NO ("no"),
	MAYBE ("maybe"),
	UNKNOWN ("unknown");
	
	private final String mRsvp;
	
	private RSVP(String rsvp) {
		mRsvp = rsvp;
	}
	
	public String getRsvp() {
		return mRsvp;
	}
}
