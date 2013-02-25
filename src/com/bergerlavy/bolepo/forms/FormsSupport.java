package com.bergerlavy.bolepo.forms;

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


/**
 * 
 * @author Moti
 *
 */
public class FormsSupport {

	/**
	 * 
	 * @param purpose
	 * @param date
	 * @param time
	 * @param Location
	 * @param shareLocationTime
	 * @return
	 */
	public static boolean createMeetingInputValidation(CreatingMeetingData data) {

		CreateMeetingValidation vStatus = new CreateMeetingValidation();
		return vStatus.isOK();
	}

	public static boolean uploadCreatingMeetingData(CreatingMeetingData data, Metadata metadata) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(MeetMeConstants.meetMeServerBaseUrl + MeetMeConstants.createMeetingServletRelativeUrl);
		httppost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "MeetMe user-agent");
		try {

			// Adding the meeting data to the HTTP request
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("action", "create"));
			nameValuePairs.add(new BasicNameValuePair("actionmaker", "mail@gmail.com"));
			nameValuePairs.add(new BasicNameValuePair("purpose", data.getPurpose()));
			nameValuePairs.add(new BasicNameValuePair("date", data.getDate()));
			nameValuePairs.add(new BasicNameValuePair("time", data.getTime()));
			nameValuePairs.add(new BasicNameValuePair("location", data.getLocation()));
			nameValuePairs.add(new BasicNameValuePair("sharelocationtime", data.getShareLocationTime()));
			int participantsNum = data.getParticipantsNum();
			nameValuePairs.add(new BasicNameValuePair("participantsnumber", Integer.toString(participantsNum)));
			for (int i = 0 ; i < participantsNum ; i++) {
				nameValuePairs.add(new BasicNameValuePair("participant_" + i, data.getParticipant(i).toString()));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			InputStream inputStream = response.getEntity().getContent();

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean btext = false;

				public void startElement(String uri, String localName,String qName, 
						Attributes attributes) throws SAXException {

					System.out.println("Start Element :" + qName);

					if (qName.equalsIgnoreCase("text")) {
						btext = true;
					}


				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {

					System.out.println("End Element :" + qName);

				}

				public void characters(char ch[], int start, int length) throws SAXException {

					if (btext) {
						System.out.println("Text : " + new String(ch, start, length));
						btext = false;
					}


				}

			};

			saxParser.parse(inputStream, handler);
		}
		catch (Exception e) {

		}
		return false;

	}

	
}

