package com.bergerlavy.bolepo;

import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.forms.CreateMeetingValidation;
import com.bergerlavy.bolepo.forms.InputValidationReport;
import com.bergerlavy.bolepo.forms.ModifyMeetingValidation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	
	public static String chopeNonDigitsFromPhoneNumber(String phone) {
		String result = "";
		for (int i = 0 ; i < phone.length() ; i++)
			if (phone.charAt(i) >= '0' && phone.charAt(i) <= '9')
				result += phone.charAt(i);
		return result;
	}
	
	/**
	 * Checks if the device is connected to the internet.
	 * @param context A context to use to get a reference to the ConnectivityManager.
	 * @return <code>true</code> if the device is connected to the internet, <code>false</code> otherwise.
	 */
	public static boolean isDeviceOnline(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    /* if the device is in airplane mode (or presumably in other situations where there's no available network)
	     * the function "getActiveNetworkInfo" will return null - hence the checking for null */
	    if (netInfo != null && netInfo.isConnectedOrConnecting())
	        return true;
	    return false;
	}
	
	public static InputValidationReport createMeetingInputValidation(Context context, Meeting data) {
		CreateMeetingValidation vStatus = new CreateMeetingValidation(context, data);
		return vStatus.isOK();
	}
	
	public static InputValidationReport modifyMeetingInputValidation(Context context, Meeting data, long id) {
		ModifyMeetingValidation vStatus = new ModifyMeetingValidation(context, data, id);
		return vStatus.isOK();
	}
}
