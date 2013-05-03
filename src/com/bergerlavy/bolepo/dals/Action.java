package com.bergerlavy.bolepo.dals;

/* every change in the string of an ENUM element obligates a corresponding change in the server software */

public enum Action {
	CREATE ("create"),
	MODIFY ("modify"),
	REMOVE ("remove"),
	RETRIEVE ("retrieve"),
	ATTEND ("attend"),
	UNATTEND ("unattend"),
	GCM_REGISTRATION ("gcm_register"),
	GCM_UNREGISTRATION ("gcm_unregister");
	
	private final String mStr;
	
	private Action(String str) {
		mStr = str;
	}
	
	public String getActionString() {
		return mStr;
	}
}
