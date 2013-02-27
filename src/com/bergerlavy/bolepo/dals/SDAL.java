package com.bergerlavy.bolepo.dals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bergerlavy.bolepo.forms.BolePoConstants;

public class SDAL {

	public static List<Message> getOfflineUpdates(String userHash) {
		return null;
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	public static ServerResponse serverCreateMeeting(Meeting m) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(BolePoConstants.BolePoServerBaseUrl + BolePoConstants.createMeetingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "BolePo user-agent");
		try {

			// Adding the meeting data to the HTTP request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
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
			ServerResponse serverResponse = AnalyzeServerResponse.analyze(response, Action.CREATE);

			return serverResponse;

		}
		catch (Exception e) { }
		return null;
	}

	public static boolean editMeeting(Meeting m, String meetingHash) {
		return false;
	}

	public static boolean removeMeeting(String meetingHash) {
		return false;
	}
}
