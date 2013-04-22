package com.bergerlavy.bolepo;

import android.content.Context;
import android.content.SharedPreferences;

public class BolePoMisc {
	
	/* SharedPreferences */
	public static final String GENERAL_PREFERENCES = "GENERAL_PREFERENCES";
	
	/* key-value pairs in SharedPreferences */
	public static final String GENERAL_PREFS_DEVICE_PHONE_NUMBER = "GENERAL_PREFS_DEVICE_PHONE_NUMBER";
	public static final String GENERAL_PREFS_GCM_REGISTRATION_ID = "GENERAL_PREFS_GCM_REGISTRATION_ID";
	
	/**
	 * Returns the device phone number.
	 * @param context required to get the SharedPreferences instance.
	 * @return the phone number the user entered the first time he/she started the application.
	 */
	public static String getDevicePhoneNumber(Context context) {
		SharedPreferences generalPrefs = context.getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);
		return generalPrefs.getString(GENERAL_PREFS_DEVICE_PHONE_NUMBER, "");
	}
	
	public static void setDevicePhoneNumber(Context context, String phone) {
		SharedPreferences genePreferences = context.getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = genePreferences.edit();
		editor.putString(GENERAL_PREFS_DEVICE_PHONE_NUMBER, phone);
		editor.commit();
	}
	
	public static void setGcmRegId(Context context, String regId) {
		SharedPreferences generalPrefs = context.getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = generalPrefs.edit();
    	editor.putString(GENERAL_PREFS_GCM_REGISTRATION_ID, regId);
    	editor.commit();
	}
	
	public static void removeGcmId(Context context) {
		SharedPreferences generalPrefs = context.getSharedPreferences(GENERAL_PREFERENCES, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = generalPrefs.edit();
    	editor.remove(GENERAL_PREFS_GCM_REGISTRATION_ID);
    	editor.commit();
	}
}
