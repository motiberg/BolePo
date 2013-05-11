package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

public class SDAL {

	private static Context mContext;
	private static HttpClient mHttpClient = new DefaultHttpClient();

	public static void setContext(Context context) {
		mContext = context;
	}

	public static List<Message> getOfflineUpdates(String userHash) {
		return null;
	}

	public static SRMeetingRetrieval retrieveMeeting(String meetingHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.RETRIEVE, meetingHash);
		if (sr instanceof SRMeetingRetrieval)
			return (SRMeetingRetrieval) sr;
		throw new ClassCastException();
	}

	public static SRMeetingCreation createMeeting(Meeting m) {
		ServerResponse sr = executeServerMeetingCommand(Action.CREATE, m);
		if (sr instanceof SRMeetingCreation)
			return (SRMeetingCreation) sr;
		throw new ClassCastException();
	}

	public static SRMeetingModification editMeeting(Meeting m) {
		ServerResponse sr = executeServerMeetingCommand(Action.MODIFY, m);
		if (sr instanceof SRMeetingModification)
			return (SRMeetingModification) sr;
		throw new ClassCastException();
	}

	public static SRMeetingRemoval removeMeeting(String meetingHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.REMOVE, meetingHash);
		if (sr instanceof SRMeetingRemoval)
			return (SRMeetingRemoval) sr;
		throw new ClassCastException();
	}
	
	public static SRMeetingAttendance attendAMeeting(String meetingHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.ATTEND, meetingHash);
		if (sr instanceof SRMeetingAttendance)
			return (SRMeetingAttendance) sr;
		throw new ClassCastException();
	}
	
	public static SRMeetingUnattendance unattendAMeeting(String meetingHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.UNATTEND, meetingHash);
		if (sr instanceof SRMeetingUnattendance)
			return (SRMeetingUnattendance) sr;
		throw new ClassCastException();
	}
	
	public static SRGcmRegistration regGCM(String regId) {
		ServerResponse sr = executeServerGcmCommand(Action.GCM_REGISTRATION, regId);
		if (sr instanceof SRGcmRegistration)
			return (SRGcmRegistration) sr;
		throw new ClassCastException();
	}

	public static SRGcmUnregistration unregGCM(String regId) {
		ServerResponse sr = executeServerGcmCommand(Action.GCM_UNREGISTRATION, regId);
		if (sr instanceof SRGcmUnregistration)
			return (SRGcmUnregistration) sr;
		throw new ClassCastException();
	}
	
	public static SRGcmRegistrationCheck checkForGcmRegistration(Set<String> contacts) {
		ServerResponse sr = executeServerGcmCommand(Action.GCM_CHECK_REGISTRATION, contacts);
		if (sr instanceof SRGcmRegistrationCheck)
			return (SRGcmRegistrationCheck) sr;
		throw new ClassCastException();
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
	
	@SuppressWarnings("unchecked")
	private static ServerResponse executeServerGcmCommand(Action action, Object data) {
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.GcmServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		
		String regId = null;
		Set<String> contacts = null;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", action.getActionString()));
		
		switch (action) {
		case GCM_REGISTRATION:
			regId = (String) data;
			nameValuePairs.add(new BasicNameValuePair("userphone", BolePoMisc.getDevicePhoneNumber(mContext)));
			nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
			break;
		case GCM_UNREGISTRATION:
			regId = (String) data;
			nameValuePairs.add(new BasicNameValuePair("userphone", BolePoMisc.getDevicePhoneNumber(mContext)));
			nameValuePairs.add(new BasicNameValuePair("gcmid", regId));
			break;
		case GCM_CHECK_REGISTRATION:
			contacts = (Set<String>) data;
			int vla = contacts.size();
			nameValuePairs.add(new BasicNameValuePair("contactsCount", contacts.size() + ""));
			int counter = 1;
			for (String phone : contacts)
				nameValuePairs.add(new BasicNameValuePair("contact_" + counter++, phone));
			break;
		default:
			break;
			
		}
		
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
			nameValuePairs.add(new BasicNameValuePair("participant_" + counter++, BolePoMisc.chopeNonDigitsFromPhoneNumber(s)));
		}
		return nameValuePairs;
	}

}
