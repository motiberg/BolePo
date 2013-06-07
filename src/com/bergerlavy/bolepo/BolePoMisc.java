package com.bergerlavy.bolepo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.forms.CreateMeetingValidation;
import com.bergerlavy.bolepo.forms.InputValidationReport;
import com.bergerlavy.bolepo.forms.ModifyMeetingValidation;
import com.google.android.gms.maps.model.LatLng;

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

	public static ArrayList<LatLng> geocoding(String address) throws IOException, JSONException {
		address = URLEncoder.encode(address, "UTF-8");
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?" +
				"address=" + address + "&sensor=true");
		
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setConnectTimeout(10000000);
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
		JSONObject json = new JSONObject(builder.toString());
		if (json.getString("status").equalsIgnoreCase("ok") == true) {
			JSONArray results = json.getJSONArray("results");
			if (results != null) {
				JSONObject firstResult = results.getJSONObject(0);
				JSONObject geometry = firstResult.getJSONObject("geometry");
				if (geometry != null) {
					JSONObject bounds = geometry.getJSONObject("bounds");
					if (bounds != null) {
						JSONObject northeast = bounds.getJSONObject("northeast");
						JSONObject southwest = bounds.getJSONObject("southwest");
						if (northeast != null && southwest != null) {
							coordinates.add(new LatLng(northeast.getDouble("lat"), northeast.getDouble("lng")));
							coordinates.add(new LatLng(southwest.getDouble("lat"), southwest.getDouble("lng")));
						}
					}
					JSONObject location = geometry.getJSONObject("location");
					if (location != null) {
						coordinates.add(new LatLng(location.getDouble("lat"), location.getDouble("lng"))); 
					}
				}
			}
		}

		return coordinates;
	}
}
