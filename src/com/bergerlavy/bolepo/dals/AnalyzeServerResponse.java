package com.bergerlavy.bolepo.dals;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

public class AnalyzeServerResponse {

	private static String mAction;
	private static String mState;
	private static String mDescription;
	private static String mHash;
	
	public static ServerResponse analyze(HttpResponse response, Action action) {

		ServerResponse srvResp = null;
		try {
			InputStream inputStream = response.getEntity().getContent();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = null;

			
			switch (action) {
			case CREATE:
				
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean hash = false;

					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						System.out.println("Start Element :" + qName);
						
						if (qName.equalsIgnoreCase("action")) {
							action = true;
						}
						
						if (qName.equalsIgnoreCase("state")) {
							state = true;
						}
						
						if (qName.equalsIgnoreCase("desc")) {
							desc = true;
						}
						
						if (qName.equalsIgnoreCase("hash")) {
							hash = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						System.out.println("End Element :" + qName);
					}

					public void characters(char ch[], int start, int length) throws SAXException {
						if (action) {
							mAction = new String(ch, start, length);
							action = false;
						}
						
						if (state) {
							mState = new String(ch, start, length);
							state = false;
						}
						
						if (desc) {
							mDescription = new String(ch, start, length);
							desc = false;
						}
						
						if (hash) {
							mHash = new String(ch, start, length);
							hash = false;
						}
					}
				};
				break;
			case EDIT:
				break;
			case REMOVE:
				break;
			case RETRIEVE:

			}
			
			saxParser.parse(inputStream, handler);
			
			if (mState.equalsIgnoreCase("ok")) {
				SRDataForMeetingManaging data = new SRDataForMeetingManaging();
				data.setMeetingHash(mHash);
				srvResp = new ServerResponse(ServerResponseStatus.OK, data);
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return srvResp;
	}
}


