package com.bergerlavy.db;

import android.provider.BaseColumns;

public class DbContract {

private DbContract() {}
	
	public static abstract class Meetings implements BaseColumns {
		public static final String TABLE_NAME = "meetings";
		public static final String COLUMN_NAME_MEETING_NAME = "meetingname";
		public static final String COLUMN_NAME_MEETING_DATE = "meetingdate";
		public static final String COLUMN_NAME_MEETING_TIME = "meetingtime";
		public static final String COLUMN_NAME_MEETING_LOCATION = "meetinglocation";
		public static final String COLUMN_NAME_MEETING_SHARE_LOCATION_TIME = "meetingshareloctime";
		public static final String COLUMN_NAME_MEETING_MANAGER = "meetingmanager";
		public static final String COLUMN_NAME_MEETING_HASH = "meetinghash";
	}
	
	public static abstract class Participants implements BaseColumns {
		public static final String TABLE_NAME = "participants";
		public static final String COLUMN_NAME_PARTICIPANT_NAME = "participantname";
		public static final String COLUMN_NAME_PARTICIPANT_PHONE = "participantphone";
		public static final String COLUMN_NAME_PARTICIPANT_MEETING_ID = "participantmeetingid";
		public static final String COLUMN_NAME_PARTICIPANT_CREDENTIALS = "participantcredentials";
		public static final String COLUMN_NAME_PARTICIPANT_RSVP = "participantrsvp";
//		public static final String COLUMN_NAME_PARTICIPANT_SHARE_LOCATION_STATUS = "participantsharelocstatus";
		public static final String COLUMN_NAME_PARTICIPANT_HASH = "participanthash";
	}
}
