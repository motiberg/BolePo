package com.bergerlavy.bolepo.dals;

import java.io.IOException;
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
import com.bergerlavy.bolepo.NoInternetConnectionBolePoException;

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

	public static SRMeetingDeclining declineAMeeting(String meetingHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.DECLINE, meetingHash);
		if (sr instanceof SRMeetingDeclining)
			return (SRMeetingDeclining) sr;
		throw new ClassCastException();
	}

	public static SRMeetingManagerReplacementAndRemoval replaceAndRemoveMeetingManager(String meetingHash, String oldManagerHash, String newManagerHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.REPLACE_AND_REMOVE_MANAGER, new String[] { meetingHash, oldManagerHash, newManagerHash });
		if (sr instanceof SRMeetingManagerReplacementAndRemoval)
			return (SRMeetingManagerReplacementAndRemoval) sr;
		throw new ClassCastException();
	}

	public static SRMeetingManagerReplacement replaceMeetingManager(String meetingHash, String oldManagerHash, String newManagerHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.REPLACE_MANAGER, new String[] { meetingHash, oldManagerHash, newManagerHash });
		if (sr instanceof SRMeetingManagerReplacement)
			return (SRMeetingManagerReplacement) sr;
		throw new ClassCastException();
	}

	public static SRParticipantRemoval removeParticipant(String participantHash) {
		ServerResponse sr = executeServerMeetingCommand(Action.REMOVE_PARTICIPANT, participantHash);
		if (sr instanceof SRParticipantRemoval)
			return (SRParticipantRemoval) sr;
		throw new ClassCastException();
	}

	public static SRGcmRegistration regGCM(String regId) throws NoInternetConnectionBolePoException {
		ServerResponse sr = executeServerGcmCommand(Action.GCM_REGISTRATION, regId);
		if (sr instanceof SRGcmRegistration)
			return (SRGcmRegistration) sr;
		throw new ClassCastException();
	}

	public static SRGcmUnregistration unregGCM(String regId) throws NoInternetConnectionBolePoException {
		ServerResponse sr = executeServerGcmCommand(Action.GCM_UNREGISTRATION, regId);
		if (sr instanceof SRGcmUnregistration)
			return (SRGcmUnregistration) sr;
		throw new ClassCastException();
	}

	public static SRGcmRegistrationCheck checkForGcmRegistration(Set<String> contacts) throws NoInternetConnectionBolePoException {
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
			if (BolePoMisc.isDeviceOnline(mContext)) {
				/* Execute HTTP Post Request */
				HttpResponse response = mHttpClient.execute(httppost);
				response.getEntity().consumeContent();
			}
			else throw new NoInternetConnectionBolePoException();
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
		String participantHash = null;

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
		case DECLINE:
			meetingHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			nameValuePairs.add(new BasicNameValuePair("user", BolePoMisc.getDevicePhoneNumber(mContext)));
			break;
		case REPLACE_MANAGER:
		case REPLACE_AND_REMOVE_MANAGER:
			String[] replaceManagerData = (String []) data;
			nameValuePairs.add(new BasicNameValuePair("meeting_hash", replaceManagerData[0]));
			nameValuePairs.add(new BasicNameValuePair("old_manager_hash", replaceManagerData[1]));
			nameValuePairs.add(new BasicNameValuePair("new_manager_hash", replaceManagerData[2]));
			break;
		case REMOVE_PARTICIPANT:
			participantHash = (String) data;
			nameValuePairs.add(new BasicNameValuePair("participant_hash", participantHash));
			break;
		default:
			break;
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = null;
			if (BolePoMisc.isDeviceOnline(mContext)) {
				/* Execute HTTP Post Request */
				mHttpClient.getConnectionManager().closeExpiredConnections();
				response = mHttpClient.execute(httppost);
			}
			else throw new NoInternetConnectionBolePoException();
			if (BolePoMisc.isDeviceOnline(mContext)) {
				/* analyzing the server response to the meeting retrieval request */
				serverResponse = new AnalyzeServerResponse().analyze(response, action);
				response.getEntity().consumeContent();

			}
			else {
				response.getEntity().consumeContent();
				throw new NoInternetConnectionBolePoException();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return serverResponse;
	}

	@SuppressWarnings("unchecked")
	private static ServerResponse executeServerGcmCommand(Action action, Object data) throws NoInternetConnectionBolePoException {
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
			nameValuePairs.add(new BasicNameValuePair("contactsCount", contacts.size() + ""));
			int counter = 1;
			for (String phone : contacts)
				nameValuePairs.add(new BasicNameValuePair("contact_" + counter++, phone));
			break;
		default:
			break;

		}

		HttpResponse response = null;
		if (BolePoMisc.isDeviceOnline(mContext)) {
			try {

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				/* Execute HTTP Post Request */
				response = mHttpClient.execute(httppost);


			}
			catch (Exception e) { 
				e.printStackTrace();
			}
		}
		else throw new NoInternetConnectionBolePoException();

		if (BolePoMisc.isDeviceOnline(mContext))
			serverResponse = new AnalyzeServerResponse().analyze(response, action);
		else {
			try {
				/* releasing the connection */
				response.getEntity().consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw new NoInternetConnectionBolePoException();
		}

		try {
			/* releasing the connection */
			response.getEntity().consumeContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
