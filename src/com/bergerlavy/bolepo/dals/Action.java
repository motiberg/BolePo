package com.bergerlavy.bolepo.dals;

public enum Action {
	CREATE ("create"),
	MODIFY ("modify"),
	REMOVE ("remove"),
	RETRIEVE ("retrieve"),
	GCM_REGISTRATION ("gcm_register"),
	GCM_UNREGISTRATION ("gcm_unregister");
	
	private final String mStr;
	
	Action(String str) {
		mStr = str;
	}
	
	public String getActionString() {
		return mStr;
	}
}
