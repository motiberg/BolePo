package com.bergerlavy.bolepo.dals;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.xml.sax.SAXException;

public class AnalyzeServerResponse {

	public static ServerResponse analyze(HttpResponse response, Action action) {

		ServerResponse srvResp;
		try {
			InputStream inputStream = response.getEntity().getContent();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			switch (action) {
			case CREATE:
				break;
			case EDIT:
				break;
			case REMOVE:
				break;
			case RETRIEVE:

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


//
//DefaultHandler handler = new DefaultHandler() {
//
//	boolean btext = false;
//
//	public void startElement(String uri, String localName,String qName, 
//			Attributes attributes) throws SAXException {
//		System.out.println("Start Element :" + qName);
//		if (qName.equalsIgnoreCase("text")) {
//			btext = true;
//		}
//	}
//
//	public void endElement(String uri, String localName,
//			String qName) throws SAXException {
//		System.out.println("End Element :" + qName);
//	}
//
//	public void characters(char ch[], int start, int length) throws SAXException {
//		if (btext) {
//			System.out.println("Text : " + new String(ch, start, length));
//			btext = false;
//		}
//	}
//};
//
//saxParser.parse(inputStream, handler);