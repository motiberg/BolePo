package com.bergerlavy.bolepo;

/**
 * 
 * @author Moti
 *
 */
public class BolePoConstants {
	
	public static final String BolePoServerBaseUrl = "http://appbolepo.appspot.com/";
	public static final String MeetingsManagingServletRelativeUrl = "meetings";
	public static final String GpsServletRelativeUrl = "gps";
	public static final String GcmServletRelativeUrl = "gcm";
	
	/* This is your project number, and it will be used later on as the GCM sender ID. */
	public static final String SENDER_ID = "299167364499";
	
	public static String[] mActionStrings = { "retrieve", "create", "modify", "remove" };
	public static final int ACTION_RETRIEVE_INDEX = 0;
	public static final int ACTION_CREATE_INDEX = 1;
	public static final int ACTION_MODIFY_INDEX = 2;
	public static final int ACTION_REMOVE_INDEX = 3;
}
