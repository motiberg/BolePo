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

import com.bergerlavy.bolepo.BolePoConstants;

public class SDAL {

	public static List<Message> getOfflineUpdates(String userHash) {
		return null;
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	/**
	 * @param m
	 * @return
	 */
	public static ServerResponse serverCreateMeeting(Meeting m) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.createMeetingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

//			 Adding the meeting data to the HTTout.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");out.println("<Action>" + action + "</Action>");P request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7 + m.getParticipantsNum());
			nameValuePairs.add(new BasicNameValuePair("action", "create"));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", "mail@gmail.com"));
			nameValuePairs.add(new BasicNameValuePair("purpose", m.getName()));
			nameValuePairs.add(new BasicNameValuePair("date", m.getDate()));
			nameValuePairs.add(new BasicNameValuePair("time", m.getTime()));
			nameValuePairs.add(new BasicNameValuePair("location", m.getLocation()));
			nameValuePairs.add(new BasicNameValuePair("sharelocationtime", m.getShareLocationTime()));
			int participantsNum = m.getParticipantsNum();
			nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
			for (int i = 0 ; i < participantsNum ; i++) {
				nameValuePairs.add(new BasicNameValuePair("participant_" + i, m.getParticipant(i).toString()));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting creation request */
			serverResponse = AnalyzeServerResponse.analyze(response, Action.CREATE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return serverResponse;
	}

	public static ServerResponse editMeeting(Meeting m, String meetingHash) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.createMeetingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			// Adding the meeting data to the HTTP request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8 + m.getParticipantsNum());
			nameValuePairs.add(new BasicNameValuePair("action", "edit"));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", "mail@gmail.com"));
			nameValuePairs.add(new BasicNameValuePair("hash", m.getHash()));
			nameValuePairs.add(new BasicNameValuePair("purpose", m.getName()));
			nameValuePairs.add(new BasicNameValuePair("date", m.getDate()));
			nameValuePairs.add(new BasicNameValuePair("time", m.getTime()));
			nameValuePairs.add(new BasicNameValuePair("location", m.getLocation()));
			nameValuePairs.add(new BasicNameValuePair("sharelocationtime", m.getShareLocationTime()));
			int participantsNum = m.getParticipantsNum();
			nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
			for (int i = 0 ; i < participantsNum ; i++) {
				nameValuePairs.add(new BasicNameValuePair("participant_" + i, m.getParticipant(i).toString()));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting creation request */
			serverResponse = AnalyzeServerResponse.analyze(response, Action.EDIT);
		}
		catch (Exception e) { }
		return serverResponse;
	}

	public static ServerResponse removeMeeting(String meetingHash) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.createMeetingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		ServerResponse serverResponse = null;
		try {

			// Adding the meeting data to the HTTP request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("action", "edit"));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", "mail@gmail.com"));
			nameValuePairs.add(new BasicNameValuePair("hash", meetingHash));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			/* analyzing the server response to the meeting creation request */
			serverResponse = AnalyzeServerResponse.analyze(response, Action.REMOVE);
		}
		catch (Exception e) { }
		return serverResponse;
	}

}
