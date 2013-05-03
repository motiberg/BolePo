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
import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.forms.FormsSupport;

public class SDAL {

	private static Context mContext;
	private static HttpClient mHttpClient = new DefaultHttpClient();

	public static void setContext(Context context) {
		mContext = context;
	}

	public static List<Message> getOfflineUpdates(String userHash) {
		return null;
	}

	public static ServerResponse retrieveMeeting(String meetingHash) {
		return executeServerMeetingCommand(Action.RETRIEVE, meetingHash);
	}

	public static ServerResponse createMeeting(Meeting m) {
		return executeServerMeetingCommand(Action.CREATE, m);
	}

	public static ServerResponse editMeeting(Meeting m) {
		return executeServerMeetingCommand(Action.MODIFY, m);
	}

	public static ServerResponse removeMeeting(String meetingHash) {
		return executeServerMeetingCommand(Action.REMOVE, meetingHash);
	}
	
	public static ServerResponse attendAMeeting(String meetingHash) {
		return executeServerMeetingCommand(Action.ATTEND, meetingHash);
	}
	
	public static ServerResponse unattendAMeeting(String meetingHash) {
		return executeServerMeetingCommand(Action.UNATTEND, meetingHash);
	}
	
	public static ServerResponse regGCM(String regId) {
		return executeServerGcmCommand(Action.GCM_REGISTRATION, regId);
	}

	public static ServerResponse unregGCM(String regId) {
		return executeServerGcmCommand(Action.GCM_UNREGISTRATION, regId);
	}

	public static void sendDeviceLocation(String meetingHash, Location l) {
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GpsServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		try {

			/* Adding the meeting data to the HTTP request */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("user", BolePoMisc.getDevicePhoneNumber(mContext)));
			nameValuePairs.add(new BasicNameValuePair("lat", l.getLatitude() + ""));
			nameValuePairs.add(new BasicNameValuePair("lon", l.getLongitude() + ""));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			mHttpClient.execute(httppost);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private static ServerResponse executeServerMeetingCommand(Action action, Object data) {
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.MeetingsManagingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");

		ServerResponse serverResponse = null;
		Meeting meeting = null;
		String meetingHash = null;

		/* adding default parameters that should be in every server request for meetings handling */
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", action.getActionString()));
		nameValuePairs.add(new BasicNameValuePair("actionmaker", BolePoMisc.getDevicePhoneNumber(mContext)));

		switch (action) {
		case CREATE:
			meeting = (Meeting) data;
			nameValuePairs = fillMeetingData(meeting, nameValuePairs);
			break;
		case MODIFY:
			meeting = (Meeting) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meeting.getHash()));
			nameValuePairs = fillMeetingData(meeting, nameValuePairs);
			break;
		case REMOVE:
			meetingHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			break;
		case RETRIEVE:
			meetingHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			break;
		case ATTEND:
			meetingHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			nameValuePairs.add(new BasicNameValuePair("user", BolePoMisc.getDevicePhoneNumber(mContext)));
			break;
		case UNATTEND:
			meetingHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			nameValuePairs.add(new BasicNameValuePair("user", BolePoMisc.getDevicePhoneNumber(mContext)));
			break;
		default:
			break;
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = mHttpClient.execute(httppost);

			/* analyzing the server response to the meeting retrieval request */
			serverResponse = new AnalyzeServerResponse().analyze(response, action);
			
			mHttpClient = new DefaultHttpClient();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return serverResponse;
	}
	
	private static ServerResponse executeServerGcmCommand(Action action, String regId) {
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GcmServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("action", action.getActionString()));
		nameValuePairs.add(new BasicNameValuePair("userphone", BolePoMisc.getDevicePhoneNumber(mContext)));
		nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			/* Execute HTTP Post Request */
			HttpResponse response = mHttpClient.execute(httppost);
			serverResponse = new AnalyzeServerResponse().analyze(response, action);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		return serverResponse;
	}

	private static List<NameValuePair> fillMeetingData(Meeting meeting, List<NameValuePair> nameValuePairs) {
		nameValuePairs.add(new BasicNameValuePair("name", meeting.getName()));
		nameValuePairs.add(new BasicNameValuePair("date", meeting.getDate()));
		nameValuePairs.add(new BasicNameValuePair("time", meeting.getTime()));
		nameValuePairs.add(new BasicNameValuePair("location", meeting.getLocation()));
		nameValuePairs.add(new BasicNameValuePair("sharelocationtime", meeting.getShareLocationTime()));
		int participantsNum = meeting.getParticipantsNum();
		nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
		int counter = 0;
		List<String> parts = meeting.getParticipants();
		for (String s : parts) {
			nameValuePairs.add(new BasicNameValuePair("participant_" + counter++, FormsSupport.chopeNonDigitsFromPhoneNumber(s)));
		}
		return nameValuePairs;
	}

}
