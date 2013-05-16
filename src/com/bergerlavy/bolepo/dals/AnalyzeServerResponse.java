package com.bergerlavy.bolepo.dals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public class AnalyzeServerResponse {

	private Action mActionAnalyzed;

	private String mAction;
	private String mState;
	private String mDescription;
	private String mHash;
	private String mCreator;
	protected String mShareLocationTime;
	protected String mLocation;
	protected String mTime;
	protected String mDate;
	protected String mName;
	protected String mParticipantHash;
	protected String mParticipantCredentials;
	protected String mParticipantRSVP;
	protected String mParticipantPhone;
	protected String mParticipantName;
	protected String mDelivered;
	protected String mTotal;
	protected String mFailDelivered;
	protected String mPhone;
	protected String mOldParticipantHash;
	protected String mNewParticipantHash;
	
	protected List<Participant> mParticipants;
	protected List<String> mPhones;

	public ServerResponse analyze(HttpResponse response, Action action) {
		mActionAnalyzed = action;
		action.getActionString();
		ServerResponse serverResponse = null;

		DefaultHandler handlerForStatusOnly = new DefaultHandler() {

			boolean action = false;
			boolean state = false;
			boolean desc = false;

			public void startElement(String uri, String localName,String qName, 
					Attributes attributes) throws SAXException {
				//TODO remove syso
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
			}

			public void endElement(String uri, String localName,
					String qName) throws SAXException {
				//TODO remove syso
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
					System.out.println("Description = " + mDescription);
					desc = false;
				}
			}
		};

		try {
			InputStream inputStream = response.getEntity().getContent();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = null;


			switch (action) {
			case CREATE:
				mParticipants = new ArrayList<Participant>();
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean meetinghash = false;
					boolean participantphone = false;
					boolean participantname = false;
					boolean participanthash = false;
					boolean participantrsvp = false;
					boolean participantcredentials = false;
					boolean delivered = false;
					boolean faildelivered = false;
					boolean total = false;

					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						//TODO remove syso
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

						if (qName.equalsIgnoreCase("mhash")) {
							meetinghash = true;
						}

						if (qName.equalsIgnoreCase("phone")) {
							participantphone = true;
						}

						if (qName.equalsIgnoreCase("name")) {
							participantname = true;
						}

						if (qName.equalsIgnoreCase("rsvp")) {
							participantrsvp = true;
						}

						if (qName.equalsIgnoreCase("credentials")) {
							participantcredentials = true;
						}

						if (qName.equalsIgnoreCase("phash")) {
							participanthash = true;
						}

						if (qName.equalsIgnoreCase("delivered")) {
							delivered = true;
						}

						if (qName.equalsIgnoreCase("faildelivered")) {
							faildelivered = true;
						}

						if (qName.equalsIgnoreCase("total")) {
							total = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						//TODO remove syso
						System.out.println("End Element :" + qName);
						if (qName.equalsIgnoreCase("participant")) {
							mParticipants.add(new Participant.Builder(mParticipantPhone)
							.setCredentials(mParticipantCredentials)
							.setRsvp(mParticipantRSVP)
							.setHash(mParticipantHash)
							.build());
							mParticipantPhone = null;
							mParticipantCredentials = null;
							mParticipantRSVP = null;
							mParticipantHash = null;
						}
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

						if (meetinghash) {
							mHash = new String(ch, start, length);
							meetinghash = false;
						}

						if (participantphone) {
							mParticipantPhone = new String(ch, start, length);
							participantphone = false;
						}

						if (participantname) {
							mParticipantName = new String(ch, start, length);
							participantname = false;
						}

						if (participantrsvp) {
							mParticipantRSVP = new String(ch, start, length);
							participantrsvp = false;
						}

						if (participantcredentials) {
							mParticipantCredentials = new String(ch, start, length);
							participantcredentials = false;
						}

						if (participanthash) {
							mParticipantHash = new String(ch, start, length);
							participanthash = false;
						}

						if (delivered) {
							mDelivered = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Delivered :" + mDelivered);
							delivered = false;
						}

						if (faildelivered) {
							mFailDelivered = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Fail Delivered :" + mFailDelivered);
							faildelivered = false;
						}

						if (total) {
							mTotal = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Total :" + mTotal);
							total = false;
						}
					}
				};
				break;
			case MODIFY:
				mParticipants = new ArrayList<Participant>();
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean hash = false;
					boolean participantphone = false;
					boolean participantname = false;
					boolean participanthash = false;
					boolean participantrsvp = false;
					boolean participantcredentials = false;
					boolean delivered = false;
					boolean faildelivered = false;
					boolean total = false;

					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						//TODO remove syso
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

						if (qName.equalsIgnoreCase("phone")) {
							participantphone = true;
						}

						if (qName.equalsIgnoreCase("name")) {
							participantname = true;
						}

						if (qName.equalsIgnoreCase("rsvp")) {
							participantrsvp = true;
						}

						if (qName.equalsIgnoreCase("credentials")) {
							participantcredentials = true;
						}

						if (qName.equalsIgnoreCase("phash")) {
							participanthash = true;
						}

						if (qName.equalsIgnoreCase("delivered")) {
							delivered = true;
						}

						if (qName.equalsIgnoreCase("faildelivered")) {
							faildelivered = true;
						}

						if (qName.equalsIgnoreCase("total")) {
							total = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						//TODO remove syso
						System.out.println("End Element :" + qName);
						if (qName.equalsIgnoreCase("participant")) {
							mParticipants.add(new Participant.Builder(mParticipantPhone)
							.setCredentials(mParticipantCredentials)
							.setRsvp(mParticipantRSVP)
							.setHash(mParticipantHash)
							.build());
							mParticipantPhone = null;
							mParticipantCredentials = null;
							mParticipantRSVP = null;
							mParticipantHash = null;
						}
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
							//TODO remove syso
							System.out.println("Meeting Hash :" + mHash);
							hash = false;
						}

						if (participantphone) {
							mParticipantPhone = new String(ch, start, length);
							participantphone = false;
						}

						if (participantname) {
							mParticipantName = new String(ch, start, length);
							participantname = false;
						}

						if (participantrsvp) {
							mParticipantRSVP = new String(ch, start, length);
							participantrsvp = false;
						}

						if (participantcredentials) {
							mParticipantCredentials = new String(ch, start, length);
							participantcredentials = false;
						}

						if (participanthash) {
							mParticipantHash = new String(ch, start, length);
							participanthash = false;
						}

						if (delivered) {
							mDelivered = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Delivered :" + mDelivered);
							delivered = false;
						}

						if (faildelivered) {
							mFailDelivered = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Fail Delivered :" + mFailDelivered);
							faildelivered = false;
						}

						if (total) {
							mTotal = new String(ch, start, length);
							//TODO remove syso
							System.out.println("Total :" + mTotal);
							total = false;
						}
					}
				};
				break;
			case REMOVE:
				handler = handlerForStatusOnly;
				break;
			case RETRIEVE:
				mParticipants = new ArrayList<Participant>();
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean creator = false;
					boolean name = false;
					boolean date = false;
					boolean time = false;
					boolean location = false;
					boolean sharelocationtime = false;
					boolean partname = false;
					boolean partrsvp = false;
					boolean partcredentials = false;
					boolean parthash = false;

					boolean here = false;


					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						//TODO remove syso
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

						if (qName.equalsIgnoreCase("creator")) {
							creator = true;
						}

						if (qName.equalsIgnoreCase("name")) {
							name = true;
						}

						if (qName.equalsIgnoreCase("date")) {
							date = true;
						}

						if (qName.equalsIgnoreCase("time")) {
							time = true;
						}

						if (qName.equalsIgnoreCase("location")) {
							location = true;
						}

						if (qName.equalsIgnoreCase("sharelocationtime")) {
							sharelocationtime = true;
						}

						if (qName.equalsIgnoreCase("participantname")) {
							partname = true;
						}

						if (qName.equalsIgnoreCase("rsvp")) {
							partrsvp = true;
						}

						if (qName.equalsIgnoreCase("credentials")) {
							partcredentials = true;
						}

						if (qName.equalsIgnoreCase("participanthash")) {
							parthash = true;
						}

						if (qName.equalsIgnoreCase("here")) {
							here = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						//TODO remove syso
						System.out.println("End Element :" + qName);

						if (qName.equalsIgnoreCase("participant")) {
							mParticipants.add(new Participant.Builder(mParticipantPhone)
							.setCredentials(mParticipantCredentials)
							.setRsvp(mParticipantRSVP)
							.setHash(mParticipantHash)
							.build());
							mParticipantPhone = null;
							mParticipantCredentials = null;
							mParticipantRSVP = null;
							mParticipantHash = null;
						}
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

						if (creator) {
							mCreator = new String(ch, start, length);
							creator = false;
						}

						if (name) {
							mName = new String(ch, start, length);
							name = false;
						}

						if (date) {
							mDate = new String(ch, start, length);
							date = false;
						}

						if (time) {
							mTime = new String(ch, start, length);
							time = false;
						}

						if (location) {
							mLocation = new String(ch, start, length);
							location = false;
						}

						if (sharelocationtime) {
							mShareLocationTime = new String(ch, start, length);
							sharelocationtime = false;
						}

						if (partname) {
							mParticipantPhone = new String(ch, start, length);
							partname = false;
						}

						if (partrsvp) {
							mParticipantRSVP = new String(ch, start, length);
							partrsvp = false;
						}

						if (partcredentials) {
							mParticipantCredentials = new String(ch, start, length);
							partcredentials = false;
						}

						if (parthash) {
							mParticipantHash = new String(ch, start, length);
							parthash = false;
						}

						if (here) {
							System.out.println(new String(ch, start, length));
							here = false;
						}

					}
				};
				break;
			case GCM_REGISTRATION:
				handler = handlerForStatusOnly;
				break;
			case GCM_UNREGISTRATION:
				handler = handlerForStatusOnly;
				break;
			case GCM_CHECK_REGISTRATION:
				mPhones = new ArrayList<String>();
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean phone = false;

					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						//TODO remove syso
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

						if (qName.equalsIgnoreCase("phone")) {
							phone = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						//TODO remove syso
						System.out.println("End Element :" + qName);
						if (qName.equalsIgnoreCase("contact")) {
							if (mPhone != null)
								mPhones.add(mPhone);
							mPhone = null;
						}
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

						if (phone) {
							mPhone = new String(ch, start, length);
							System.out.println("Phone = " + mPhone);
							phone = false;
						}
					}
				};
				break;
			case ATTEND:
				handler = handlerForStatusOnly;
				break;
			case UNATTEND:
				break;
			case REPLACE_MANAGER:
				handler = new DefaultHandler() {

					boolean action = false;
					boolean state = false;
					boolean desc = false;
					boolean meetingHash = false;
					boolean oldParticipantHash = false;
					boolean newParticipantHash = false;

					public void startElement(String uri, String localName,String qName, 
							Attributes attributes) throws SAXException {
						//TODO remove syso
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

						if (qName.equalsIgnoreCase("meetinghash")) {
							meetingHash = true;
						}
						
						if (qName.equalsIgnoreCase("oldparticipanthash")) {
							oldParticipantHash = true;
						}
						
						if (qName.equalsIgnoreCase("newparticipanthash")) {
							newParticipantHash = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
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

						if (meetingHash) {
							mHash = new String(ch, start, length);
							meetingHash = false;
						}
						
						if (oldParticipantHash) {
							mOldParticipantHash = new String(ch, start, length);
							oldParticipantHash = false;
						}
						
						if (newParticipantHash) {
							mNewParticipantHash = new String(ch, start, length);
							newParticipantHash = false;
						}
					}
				};
				break;
			case REMOVE_PARTICIPANT:
				handler = handlerForStatusOnly; 
				break;
			}

			saxParser.parse(inputStream, handler);

			ServerResponseStatus status = ServerResponseStatus.getEnum(mState);

			if (isActionConsistent(mAction)) {
				switch (action) {
				case CREATE:
					serverResponse = new SRMeetingCreation.Builder(status, mDescription)
					.setMeetingHash(mHash)
					.setParticipants(mParticipants)
					.build();
					break;
				case MODIFY:
					serverResponse = new SRMeetingModification.Builder(status, mDescription)
					.setMeetingHash(mHash)
					.setParticipants(mParticipants)
					.build();
					break;
				case REMOVE:
					serverResponse = new SRMeetingRemoval.Builder(status, mDescription)
					.build();
					break;
				case RETRIEVE:
					serverResponse = new SRMeetingRetrieval.Builder(status, mDescription)
					.setMeeting(new Meeting(mName, mDate, mTime, mCreator, mLocation, mShareLocationTime, null))
					.setParticipants(mParticipants)
					.build();
					break;
				case GCM_REGISTRATION:
					serverResponse = new SRGcmRegistration.Builder(status, mDescription)
					.build();
					break;
				case GCM_UNREGISTRATION:
					serverResponse = new SRGcmUnregistration.Builder(status, mDescription)
					.build();
					break;
				case GCM_CHECK_REGISTRATION:
					serverResponse = new SRGcmRegistrationCheck.Builder(status, mDescription)
					.setRegisteredContactsPhones(mPhones)
					.build();
					break;
				case ATTEND:
					serverResponse = new SRMeetingAttendance.Builder(status, mDescription)
					.build();
					break;
				case UNATTEND:
					//TODO
					break;
				case REPLACE_MANAGER:
					serverResponse = new SRMeetingManagerReplacement.Builder(status, mDescription)
					.setMeetingHash(mHash)
					.setOldManagerHash(mOldParticipantHash)
					.setNewManagerHash(mNewParticipantHash)
					.build();
					break;
				case REMOVE_PARTICIPANT:
					serverResponse = new SRParticipantRemoval.Builder(status, mDescription)
					.build();
					break;
				default:
					break;
				}
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

		return serverResponse;
	}

	/**
	 * Checks if the server's response which is being analyzed is a response for the same action as
	 * the user expects it to be.
	 * For instance, the user made a request to the server for meeting creation and then he received a
	 * response. This check is to ensure that the server response is for to meeting creation request. 
	 * @param serverAction the action on which the server respond.
	 * @return <code>true</code> if the server respond to the same action as the expected action to be receive, <code>false</code> otherwise
	 */
	private boolean isActionConsistent(String serverAction) {
		return serverAction.equalsIgnoreCase(mActionAnalyzed.getActionString());
	}
}


