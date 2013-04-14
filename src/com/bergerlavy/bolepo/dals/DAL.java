package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;



public class DAL {

	private static Context mContext;
	private static DbHelper mDbHelper;

	public static void setContext(Context context) {
		mContext = context;
		mDbHelper = new DbHelper(context);
	}

	public static List<Meeting> getAllMeetingsData(String hash) {
		return null;
	}

	public static List<Participant> getMeetingParticipants(long meetingID) {
		List<Participant> parts = new ArrayList<Participant>();
		SQLiteDatabase readableDB = mDbHelper.getReadableDatabase();

		Cursor participantsFromDBCursor = readableDB.query(DbContract.Participants.TABLE_NAME,
				null,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingID,
				null, null, null, null);

		readableDB.close();

		participantsFromDBCursor.moveToFirst();
		while (!participantsFromDBCursor.isAfterLast()) {
			parts.add(new Participant.Builder(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME)),
					participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH)))
			.setCredentials(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS)))
			.setRsvp(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP)))
			.build());

			participantsFromDBCursor.moveToNext();
		}

		return parts;
	}

	public static boolean createMeeting(Meeting m, SRDataForMeetingManaging serverData) {
		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();

		/* inserting the meeting into the meetings table */

		ContentValues values = meetingToValues(m);
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, serverData.getMeetingHash());

		long meetingID = writableDB.insert(DbContract.Meetings.TABLE_NAME, "null", values);
		writableDB.close();

		/* checking whether an error occurred during the insert to the database */
		if (meetingID == -1)
			return false;

		/* inserting the participants into the participants table */

		if (insertParticipants(meetingID, m, serverData) == m.getParticipantsNum())
			return true;
		return false;
	}

	public static boolean editMeeting(long id, Meeting newMeeting, SRDataForMeetingManaging serverData) {

		/* removing old participants */

		removeParticipants(id);

		/* updating meetings table with the modified meeting */

		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();
		ContentValues values = meetingToValues(newMeeting);
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, serverData.getMeetingHash());

		writableDB.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + id, null);
		writableDB.close();

		/* inserting the new participants into the participants table */

		if (insertParticipants(id, newMeeting, serverData) == newMeeting.getParticipantsNum() + 1)
			return true;
		return false;
	}

	public static boolean amIMeetingCreator(long id) {
		SQLiteDatabase readableDB = mDbHelper.getReadableDatabase();
		Cursor c = readableDB.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME + " = " + mContext.getSharedPreferences(MainActivity.DEVICE_USER_NAME, Context.MODE_PRIVATE).getString(MainActivity.DEVICE_USER_NAME, "") + " and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS + " = root",
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				c.close();
				readableDB.close();
				return true;
			}
			c.close();
		}
		readableDB.close();
		return false;
	}

	public static boolean removeMeeting(long id) {

		/* removing old participants */

		removeParticipants(id);

		/* removing meeting's other data */

		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();
		int res = writableDB.delete(DbContract.Meetings.TABLE_NAME, DbContract.Meetings._ID + " = " + id, null);
		writableDB.close();

		/* checking if at least one row in the database affected from the delete operation. 
		 * if no rows affected, that means that there is no meeting with the given id. */
		if (res != 0)
			return true;
		return false;
	}

	/**
	 * Extracts data from a Meeting object and making it ready to be used for database operations.
	 * @param m the meeting instance to extract data from.
	 * @return ContentValues instance which holds the data from the Meeting instance. The keys
	 * are the column names of the Meetings table in the database.
	 */
	private static ContentValues meetingToValues(Meeting m) {
		ContentValues values = new ContentValues();
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_NAME, m.getName());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_DATE, m.getDate());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_TIME, m.getTime());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION, m.getLocation());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME, m.getShareLocationTime());
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_CREATOR, m.getCreator());
		return values;
	}

	/**
	 * Inserts participants data to the database.
	 * @param id the row ID in the database of the meeting that the participants are belong to.
	 * @param m the Meeting instance that holds the participants info.
	 * @param serverData the hash values calculated on the participants data by the server.
	 * @return the number of successfully added participants.
	 */
	private static int insertParticipants(long id, Meeting m, SRDataForMeetingManaging serverData) {
		List<Participant> participants = serverData.getParticipants();
		int count = 0;
		for (Participant p : participants) {
			long res = insertParticipant(id, m, p);
			if (res != -1)
				count++;
		}
		return count;
	}

	private static long insertParticipant(long id, Meeting m, Participant p) {
		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME, p.getName());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID, id);
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, p.getCredentials());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP, p.getRSVP());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_SHARE_LOCATION_STATUS, m.getShareLocationTime());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, p.getHash());
		long res = writableDB.insert(DbContract.Participants.TABLE_NAME, "null", values);
		writableDB.close();
		return res;
	}

	/**
	 * Removes participants from the database
	 * @param meetingID the ID of the meeting row in the database that it's participants are going to be removed.
	 */
	private static void removeParticipants(long meetingID) {
		SQLiteDatabase writableDB = mDbHelper.getWritableDatabase();
		writableDB.delete(DbContract.Participants.TABLE_NAME, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingID, null);
		writableDB.close();
	}


}
