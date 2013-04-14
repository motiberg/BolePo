package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.location.Location;

import com.bergerlavy.bolepo.BolePoConstants;
import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.forms.FormsSupport;

public class SDAL {

	private static Context mContext;

	public static void setContext(Context context) {
		mContext = context;
	}

	public static List<Message> getOfflineUpdates(String userHash) {
		return null;
	}

	public static ServerResponse retrieveMeeting(String meetingHash) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.MeetingsManagingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("action", Action.RETRIEVE.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting retrieval request */
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.RETRIEVE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return serverResponse;
	}

	public static ServerResponse createMeeting(Meeting m) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.MeetingsManagingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8 + m.getParticipantsNum());
			nameValuePairs.add(new BasicNameValuePair("action", Action.CREATE.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("name", m.getName()));
			nameValuePairs.add(new BasicNameValuePair("date", m.getDate()));
			nameValuePairs.add(new BasicNameValuePair("time", m.getTime()));
			nameValuePairs.add(new BasicNameValuePair("location", m.getLocation()));
			nameValuePairs.add(new BasicNameValuePair("sharelocationtime", m.getShareLocationTime()));
			int participantsNum = m.getParticipantsNum();
			nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
//			nameValuePairs.add(new BasicNameValuePair("participant_0", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			int counter = 0;
			List<String> parts = m.getParticipants();
			for (String s : parts) {
				nameValuePairs.add(new BasicNameValuePair("participant_" + counter++, FormsSupport.chopeNonDigitsFromPhoneNumber(s)));
			}
//			for (int i = 1 ; i <= participantsNum ; i++) {
//				nameValuePairs.add(new BasicNameValuePair("participant_" + i, m.getParticipant(i - 1)));
//			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting creation request */
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.CREATE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return serverResponse;
		
	}

	public static ServerResponse editMeeting(Meeting m) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.MeetingsManagingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10 + m.getParticipantsNum());
			nameValuePairs.add(new BasicNameValuePair("action", Action.MODIFY.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("actionmaker",  mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("hash", m.getHash()));
			nameValuePairs.add(new BasicNameValuePair("name", m.getName()));
			nameValuePairs.add(new BasicNameValuePair("date", m.getDate()));
			nameValuePairs.add(new BasicNameValuePair("time", m.getTime()));
			nameValuePairs.add(new BasicNameValuePair("location", m.getLocation()));
			nameValuePairs.add(new BasicNameValuePair("sharelocationtime", m.getShareLocationTime()));
			int participantsNum = m.getParticipantsNum();
			nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
			nameValuePairs.add(new BasicNameValuePair("participant_0", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			for (int i = 1 ; i <= participantsNum ; i++) {
				//TODO make sure the getParticipant returns here HASH value.
				//notich that this function returns participant name for the createMeeting function.
				nameValuePairs.add(new BasicNameValuePair("participant_" + i, m.getParticipant(i - 1)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			//* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting modification request */
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.MODIFY);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		return serverResponse;
	}

	public static ServerResponse removeMeeting(String meetingHash) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.MeetingsManagingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("action", Action.REMOVE.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting removal request */
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.REMOVE);
		}
		catch (Exception e) { }
		return serverResponse;
	}

	public static void sendDeviceLocation(String meetingHash, Location l) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GpsServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("user", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("lat", l.getLatitude() + ""));
			nameValuePairs.add(new BasicNameValuePair("lon", l.getLongitude() + ""));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			httpclient.execute(httppost);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}

	}

	public static ServerResponse regGCM(String regId) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GcmServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("action", Action.GCM_REGISTRATION.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("user", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));



			/* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.GCM_REGISTRATION);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		return serverResponse;
	}

	public static ServerResponse unregGCM(String regId) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GcmServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("action", Action.GCM_UNREGISTRATION.getActionString()));
			nameValuePairs.add(new BasicNameValuePair("user", mContext.getSharedPreferences(MainActivity.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "")));
			nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = httpclient.execute(httppost);
			serverResponse = new AnalyzeServerResponse().analyze(response, Action.GCM_UNREGISTRATION);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		return serverResponse;
	}

}
