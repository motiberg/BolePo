package com.bergerlavy.bolepo.dals;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;



public class DAL {

	private static DbHelper mDbHelper;
	
	public static void setContext(Context context) {
		mDbHelper = new DbHelper(context);
	}
	
	public static List<Meeting> getAllMeetingData(String hash) {
		return null;
	}
	
	public static List<Participant> getMeetingParticipants(String hash) {
		return null;
	}
	
	public static boolean createMeeting(Meeting m, String hash) {
		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_NAME, m.getName());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_DATE, m.getDate());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_TIME, m.getTime());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION, m.getLocation());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME, m.getShareLocationTime());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_CREATOR, m.getCreator());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, hash);
		
		long meetingID = writableDB.insert(DbContract.Meetings.TABLE_NAME, "null", values);
		
		List<Participant> partLst = m.getParticipants();
		for (Participant p : partLst) {
			values = new ContentValues();
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME, p.getName());
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID, meetingID);
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, p.getCredentials());
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP, p.getRSVP());
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_SHARE_LOCATION_STATUS, m.getShareLocationTime());
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, p.getHash());
			writableDB.insert(DbContract.Participants.TABLE_NAME, "null", values);
		}
		writableDB.close();
		return true;
	}
	
	public static boolean editMeeting(long id, Meeting newMeeting, String newHash) {
		return false;
	}
	
	public static boolean removeMeeting(long id) {
		return false;
	}
}
