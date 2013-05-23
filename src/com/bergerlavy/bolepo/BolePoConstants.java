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
	public static final String ACTION_BOLEPO_INFORM_NO_INTERNET_CONNECTION = "com.bergerlavy.bolepo.inform_no_internet_connection";
	
	public static final String CONTACTS_FILE_NAME = "contacts_file";
	
	public enum RSVP {
		YES ("yes"),
		NO ("no"),
		MAYBE ("maybe"),
		UNKNOWN ("unknown"),
		DECLINE ("decline");

		private final String mRsvp;

		private RSVP(String rsvp) {
			mRsvp = rsvp;
		}

		@Override
		public String toString() {
			return mRsvp;
		}
		
		public static RSVP getEnum(String str) {
			for (RSVP r : values()) {
				if (r.toString().equalsIgnoreCase(str))
					return r;
			}
			throw new IllegalArgumentException();
		}
	}
	
	public enum Credentials {
		REGULAR ("regular"),
		MANAGER ("manager");
		
		private final String mCredentials;
		
		private Credentials(String credentials) {
			mCredentials = credentials;
		}
		
		@Override
		public String toString() {
			return mCredentials;
		}
		
		public static Credentials getEnum(String str) {
			for (Credentials c : values()) {
				if (c.toString().equalsIgnoreCase(str))
					return c;
			}
			throw new IllegalArgumentException();
		}
	}
	
	public enum GCM_NOTIFICATION {
		NEW_MEETING ("new_meeting"),
		UPDATED_MEETING ("updated_meeting"),
		MEETING_CANCLED ("meeting_cancled"),
		NEW_MANAGER ("new_manager"),
		NEW_MANAGER_REMOVE_OLDER ("new_manager_remove_older"),
		REMOVED_FROM_MEETING ("removed_from_meeting"),
		PARTICIPANT_ATTENDED ("participant_attended"),
		PARTICIPANT_DECLINED ("participant_declined");
		
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
		PARTICIPANT_DATA ("participant_data"),
		PARTICIPANT_ATTENDANCE ("participant_attends"),
		PARTICIPANT_DECLINING ("participant_declining"),
		REMOVED_BY_MANAGER ("removed_by_manager"),
		NEW_MANAGER_NEW_HASH ("new_manager_hash"),
		OLD_MANAGER_NEW_HASH ("old_manager_hash"),
		OLD_MEETING_HASH ("old_meeting_hash"),
		NEW_MANAGER_PHONE ("new_manager_phone");
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
	
	public enum ServerResponseStatus {
		OK ("ok"),
		ERROR ("error");
		
		private final String mStr;

		private ServerResponseStatus(String str) {
			mStr = str;
		}

		@Override
		public String toString() {
			return mStr;
		}
		
		public static ServerResponseStatus getEnum(String str) {
			for (ServerResponseStatus srs : values()) {
				if (srs.toString().equalsIgnoreCase(str))
					return srs;
			}
			throw new IllegalArgumentException();
		}
	}
}
