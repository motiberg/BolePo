package com.bergerlavy.bolepo.dals;

/* every change in the string of an ENUM element obligates a corresponding change in the server software */

public enum Action {
	CREATE ("create"),
	MODIFY ("modify"),
	REMOVE ("remove"),
	RETRIEVE ("retrieve"),
	ATTEND ("attend"),
	DECLINE ("decline"),
	REPLACE_MANAGER ("replace_manager"),
	REPLACE_AND_REMOVE_MANAGER ("replace_and_remove_manager"),
	REMOVE_PARTICIPANT ("remove_participant"),
	GCM_REGISTRATION ("gcm_register"),
	GCM_UNREGISTRATION ("gcm_unregister"),
	GCM_CHECK_REGISTRATION ("gcm_check_registration");
	
	private final String mStr;
	
	private Action(String str) {
		mStr = str;
	}
	
	public String getActionString() {
		return mStr;
	}
}
