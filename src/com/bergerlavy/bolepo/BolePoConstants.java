package com.bergerlavy.bolepo;

/**
 * 
 * @author Moti
 *
 */
public class BolePoConstants {
	
	public static final String BolePoServerBaseUrl = "http://appbolepo.appspot.com/";
	public static final String MeetingsManagingServletRelativeUrl = "meetings";
	public static final String GpsServletRelativeUrl = "gps";
	public static final String GcmServletRelativeUrl = "gcm";
	
	/* This is your project number, and it will be used later on as the GCM sender ID. */
	public static final String SENDER_ID = "299167364499";
	
	public static String[] mActionStrings = { "retrieve", "create", "modify", "remove" };
	public static final int ACTION_RETRIEVE_INDEX = 0;
	public static final int ACTION_CREATE_INDEX = 1;
	public static final int ACTION_MODIFY_INDEX = 2;
	public static final int ACTION_REMOVE_INDEX = 3;
	
	public static final String ACTION_BOLEPO_REFRESH_LISTS = "com.bergerlavy.bolepo.refresh";
	
	public enum GCM_NOTIFICATION {
		NEW_MEETING ("new_meeting"),
		UPDATED_MEETING ("updated_meeting"),
		MEETING_CANCLED ("meeting_cancled"),
		NEW_MANAGER ("new_manager");
		
		private final String mStr;

		private GCM_NOTIFICATION(String str) {
			mStr = str;
		}

		@Override
		public String toString() {
			return mStr;
		}
		
		public static GCM_NOTIFICATION getEnum(String str) {
			for (GCM_NOTIFICATION gcmNotification : values())
				if (gcmNotification.toString().equals(str))
					return gcmNotification;
			throw new IllegalArgumentException();
		}
	}
	
	public enum GCM_DATA {
		MESSAGE_TYPE ("bolepo_message_type"),
		MEETING_NAME ("meeting_name"),
		MEETING_MANAGER ("meeting_manager"),
		MEETING_DATE ("meeting_date"),
		MEETING_TIME ("meeting_time"),
		MEETING_LOCATION ("meeting_location"),
		MEETING_SHARE_LOCATION_TIME ("meeting_share_location_time"),
		MEETING_PARTICIPANTS_COUNT ("meeting_participants_count"),
		MEETING_HASH ("meeting_hash"),
		PARTICIPANT_DATA ("participant_data");
//		PARTICIPANT_PHONE ("participant_phone"),
//		PARTICIPANT_NAME ("participant_name"),
//		PARTICIPANT_RSVP ("participant_rsvp"),
//		PARTICIPANT_CREDENTIALS ("participant_credentials"),
//		PARTICIPANT_HASH ("participant_hash");
		
		private final String mStr;

		private GCM_DATA(String str) {
			mStr = str;
		}

		@Override
		public String toString() {
			return mStr;
		}
	}
}
