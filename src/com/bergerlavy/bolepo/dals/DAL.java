package com.bergerlavy.bolepo.dals;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bergerlavy.bolepo.BolePoConstants.Credentials;
import com.bergerlavy.bolepo.BolePoConstants.RSVP;
import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;



public class DAL {

	private static Context mContext;
	private static DbHelper mDbHelper;
	private static SQLiteDatabase mReadableDb;
	private static SQLiteDatabase mWritableDb;

	public static void setContext(Context context) {
		mContext = context;
		mDbHelper = new DbHelper(context);
	}

	public static boolean appointNewManagerAndRemoveOldOne(String oldMeetingHash, String newMeetingHash, String newManagerPhone, String newManagerNewHash) {
		//get current manager phone
		//call appintNewManager
		//remove old manager
		return false;
	}
	
	public static boolean appointNewManager(String oldMeetingHash, String newMeetingHash, String oldManagerNewHash, String newManagerPhone, String newManagerNewHash) {
		long meetingId = getMeetingIdByHash(oldMeetingHash);
		Meeting m = getMeetingById(meetingId);
		Participant newManager = getParticipantByPhoneNumber(meetingId, newManagerPhone);

		/* updating the meeting manager and hash */
		ContentValues values = new ContentValues();
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER, BolePoMisc.getDevicePhoneNumber(mContext));
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, newMeetingHash);

		mWritableDb = mDbHelper.getWritableDatabase();
		int recordsAffected = mWritableDb.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + meetingId, null);
		mWritableDb.close();

		if (recordsAffected == 0)
			return false;

		/* updating the new manager data */
		values = new ContentValues();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.MANAGER.toString());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, newManagerNewHash);

		mWritableDb = mDbHelper.getWritableDatabase();
		recordsAffected = mWritableDb.update(DbContract.Participants.TABLE_NAME, values, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " " +
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + newManagerPhone + "'", null);
		mWritableDb.close();

		/* checking if the update of the record of the participant that is going to be the new manager has succeeded */
		if (recordsAffected == 0) {
			/* reverting the modification of the meeting's manager and hash due to failure in the process of modifying the new manager credentials and hash */
			values = new ContentValues();
			values.put(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER, m.getManager());
			values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, oldMeetingHash);

			mWritableDb = mDbHelper.getWritableDatabase();
			mWritableDb.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + meetingId, null);
			mWritableDb.close();
			return false;
		}

		/* updating the old manager data */
		values = new ContentValues();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.REGULAR.toString());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, oldManagerNewHash);

		mWritableDb = mDbHelper.getWritableDatabase();
		recordsAffected = mWritableDb.update(DbContract.Participants.TABLE_NAME, values, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " " + 
		DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + m.getManager() + "'", null);
		mWritableDb.close();

		/* checking if the update of the record of the participant that has gave up on the meeting management has succeeded */
		if (recordsAffected == 0) {
			/* reverting the modification of the meeting's manager and hash due to failure in the process of modifying the old manager credentials and hash */
			values = new ContentValues();
			values.put(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER, m.getManager());
			values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, oldMeetingHash);

			mWritableDb = mDbHelper.getWritableDatabase();
			mWritableDb.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + meetingId, null);
			mWritableDb.close();

			/* reverting the modification of the new manager's credentials and hash due to failure in the process of modifying the old manager credentials and hash */
			values = new ContentValues();
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.REGULAR.toString());
			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, newManager.getHash());

			mWritableDb = mDbHelper.getWritableDatabase();
			recordsAffected = mWritableDb.update(DbContract.Participants.TABLE_NAME, values, DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH + " = '" + newManagerNewHash + "'", null);
			mWritableDb.close();
			return false;
		}
		return true;
	}

	public static boolean expelFromMeeting(String meetingHash) {
		return removeMeeting(getMeetingIdByHash(meetingHash));
	}

	public static List<Meeting> getAllAcceptedMeetings() {
		Cursor c = createAcceptedMeetingsCursor();
		List<Meeting> meetings = new ArrayList<Meeting>();
		if (c != null) {
			while (!c.isAfterLast()) {
				meetings.add(new Meeting(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME)),
						getParticipantsPhonesAsList(c.getLong(c.getColumnIndex(DbContract.Meetings._ID)))));
				c.moveToNext();
			}
		}
		c.close();
		return meetings;
	}

	public static List<Meeting> getAllAcceptedMeetings(long ... excludes) {
		Cursor c = createAcceptedMeetingsCursor();
		List<Meeting> meetings = new ArrayList<Meeting>();
		if (c != null) {
			main_loop: while (!c.isAfterLast()) {
				for (long l : excludes) {
					if (l == c.getLong(c.getColumnIndex(DbContract.Meetings._ID)))
						continue main_loop;
				}
				meetings.add(new Meeting(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME)),
						getParticipantsPhonesAsList(c.getLong(c.getColumnIndex(DbContract.Meetings._ID)))));
				c.moveToNext();
			}
		}
		c.close();
		return meetings;
	}

	private static long getMeetingIdByHash(String meetingHash) {
		mReadableDb = mDbHelper.getReadableDatabase();
		long meetingId = -1;
		Cursor c = mReadableDb.query(DbContract.Meetings.TABLE_NAME,
				new String[] { DbContract.Meetings._ID },
				DbContract.Meetings.COLUMN_NAME_MEETING_HASH + " = '" + meetingHash + "'",
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				meetingId = c.getLong(c.getColumnIndex(DbContract.Meetings._ID));
			c.close();
		}
		mReadableDb.close();

		return meetingId;
	}

	public static boolean updateParticipantAttendance(String meetingHash, String participantPhone, RSVP r) {
		long meetingId = getMeetingIdByHash(meetingHash);
		mWritableDb = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP, r.toString());
		int recordsAffected = mWritableDb.update(DbContract.Participants.TABLE_NAME,
				values,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + participantPhone + "'",
						null);
		mWritableDb.close();

		return recordsAffected == 1;
	}

	public static Cursor createAcceptedMeetingsCursor() {
		mReadableDb = mDbHelper.getReadableDatabase();
		Cursor acceptedMeetingsCursor = null;
		/* getting all the meetings IDs that the user of this device accepted (created meetings are automatically marked as accepted) */
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID }, 
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "' and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP + " = '" + RSVP.YES.toString() + "'",
						null, null, null, null);
		String selectionStr = "";
		if (c != null) {
			if (c.moveToFirst()) 
				while (!c.isAfterLast()) {
					selectionStr += DbContract.Meetings._ID + " = " + c.getInt(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID)) + " or ";
					c.moveToNext();
				}
			c.close();
		}
		/* removing the last ' or ' */
		if (selectionStr.length() > 4)
			selectionStr = selectionStr.substring(0, selectionStr.length() - 4);

		/* getting all the meetings records that the user of this device accepted */
		if (selectionStr.equals(""))
			selectionStr = "0 == 1";
		acceptedMeetingsCursor = mReadableDb.query(DbContract.Meetings.TABLE_NAME, null, selectionStr, null, null, null, null);
		if (acceptedMeetingsCursor != null)
			acceptedMeetingsCursor.moveToFirst();

		mReadableDb.close();
		return acceptedMeetingsCursor;
	}

	public static Cursor createWaitingForApprovalMeetingsCursor() {
		mReadableDb = mDbHelper.getReadableDatabase();
		Cursor notApprovedMeetingsCursor = null;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "' and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP + " = '" + RSVP.UNKNOWN.toString() + "'",
						null, null, null, null);
		String selectionStr = "";
		if (c != null) {
			if (c.moveToFirst())
				while (!c.isAfterLast()) {
					selectionStr += DbContract.Meetings._ID + " = " + c.getInt(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID)) + " or ";
					c.moveToNext();
				}
			c.close();
		}
		/* removing the last ' or ' */
		if (selectionStr.length() > 4)
			selectionStr = selectionStr.substring(0, selectionStr.length() - 4);

		/* getting all the meetings records that the user of this device accepted */
		if (selectionStr.equals(""))
			selectionStr = "0 == 1";
		notApprovedMeetingsCursor = mReadableDb.query(DbContract.Meetings.TABLE_NAME, null, selectionStr, null, null, null, null);
		if (notApprovedMeetingsCursor != null)
			notApprovedMeetingsCursor.moveToFirst();

		mReadableDb.close();
		return notApprovedMeetingsCursor;
	}

	/**
	 * Retrieves the credentials of the user of this device in the meeting denoted by it's ID
	 * @param meetingId the ID of the meeting that is checked for the user's credentials
	 * @return
	 */
	public static Credentials getMyCredentials(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		Credentials credentials = null;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "'",
						null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				credentials = Credentials.getEnum(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS)));
			c.close();
		}
		mReadableDb.close();
		return credentials;
	}

	/**
	 * Retrieves the RSVP status of the user of this device in the meeting denoted by it's ID
	 * @param meetingId the ID of the meeting that is checked for the user's RSVP
	 * @return
	 */
	public static RSVP getMyRsvp(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		RSVP rsvp = null;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "'",
						null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				rsvp = RSVP.getEnum(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP)));
			c.close();
		}
		mReadableDb.close();
		return rsvp;
	}

	public static Meeting getMeetingById(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		Meeting meeting = null;
		Cursor c = mReadableDb.query(DbContract.Meetings.TABLE_NAME, null, DbContract.Meetings._ID + " = " + meetingId, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				meeting = new Meeting(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)),
						c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME)),
						getParticipantsPhonesAsList(meetingId));
				meeting.setHash(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_HASH)));
			}
			c.close();
		}
		mReadableDb.close();
		return meeting;
	}

	public static String getMeetingHashById(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		String meetingHash = null;
		Cursor c = mReadableDb.query(DbContract.Meetings.TABLE_NAME,
				new String[] { DbContract.Meetings._ID, DbContract.Meetings.COLUMN_NAME_MEETING_HASH },
				DbContract.Meetings._ID + " = " + meetingId,
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				meetingHash = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_HASH));
			c.close();
		}
		mReadableDb.close();

		return meetingHash;
	}

	public static Participant getMeetingManager(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		Participant participant = null;
		String managerPhone = null;
		Cursor c = mReadableDb.query(DbContract.Meetings.TABLE_NAME,
				new String[] { DbContract.Meetings._ID, DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER },
				DbContract.Meetings._ID + " = " + meetingId,
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				managerPhone = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER));
			c.close();
		}
		mReadableDb.close();
		if (managerPhone != null)
			participant = getParticipantByPhoneNumber(meetingId, managerPhone);
		return participant;
	}

	public static Participant getParticipantByPhoneNumber(long meetingId, String phone) {
		mReadableDb = mDbHelper.getReadableDatabase();
		Participant p = null;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				null,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + phone + "' and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId,
						null, null, null, null);

		if (c != null) {
			if (c.moveToFirst())
				p = new Participant.Builder(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE)))
			.setName(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME)))
			.setCredentials(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS)))
			.setRsvp(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP)))
			.setHash(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH)))
			.build();

			c.close();
		}
		mReadableDb.close();
		return p;
	}

	public static List<Participant> getParticipantsAsList(long meetingID) {
		List<Participant> parts = new ArrayList<Participant>();
		mReadableDb = mDbHelper.getReadableDatabase();

		Cursor participantsFromDBCursor = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				null,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingID,
				null, null, null, null);

		mReadableDb.close();

		participantsFromDBCursor.moveToFirst();
		while (!participantsFromDBCursor.isAfterLast()) {
			parts.add(new Participant.Builder(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE)))
			.setName(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME)))
			.setCredentials(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS)))
			.setRsvp(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP)))
			.setHash(participantsFromDBCursor.getString(participantsFromDBCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH)))
			.build());

			participantsFromDBCursor.moveToNext();
		}
		participantsFromDBCursor.close();

		return parts;
	}

	public static String getParticipantHashByPhone(long meetingId, String phone) {
		mReadableDb = mDbHelper.getReadableDatabase();
		String participantHash = null;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + phone + "'",
						null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				participantHash = c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH));
			c.close();
		}
		mReadableDb.close();

		return participantHash;
	}

	public static List<String> getParticipantsPhonesAsList(long meetingId) {
		mReadableDb = mDbHelper.getReadableDatabase();
		List<String> participantsPhones = new ArrayList<String>();
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId,
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					participantsPhones.add(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE)));
					c.moveToNext();
				}
			}
			c.close();
		}
		mReadableDb.close();
		return participantsPhones;
	}

	public static String[] getParticipantsPhonesAsArray(long meetingId) {
		List<String> participantsList = getParticipantsPhonesAsList(meetingId);
		String[] participantsArray = new String[participantsList.size()];
		participantsList.toArray(participantsArray);
		return participantsArray;
	}

	public static boolean createMeeting(Meeting m, SRMeetingCreation serverResponse) {
		if (!serverResponse.hasData())
			return false;

		ContentValues values = meetingToValues(m);
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, serverResponse.getMeetingHash());

		mWritableDb = mDbHelper.getWritableDatabase();

		/* inserting the meeting into the meetings table */
		long meetingID = mWritableDb.insert(DbContract.Meetings.TABLE_NAME, "null", values);
		mWritableDb.close();

		/* checking whether an error occurred during the insert to the database */
		if (meetingID == -1)
			return false;

		/* inserting the participants into the participants table */

		if (insertParticipants(meetingID, serverResponse.getParticipants()) == m.getParticipantsNum())
			return true;
		return false;
	}

	public static boolean createMeeting(Meeting m, List<Participant> participants) {
		mWritableDb = mDbHelper.getWritableDatabase();

		ContentValues values = meetingToValues(m);
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, m.getHash());

		/* inserting the meeting into the meetings table */
		long meetingID = mWritableDb.insert(DbContract.Meetings.TABLE_NAME, "null", values);
		mWritableDb.close();

		/* checking whether an error occurred during the insert to the database */
		if (meetingID == -1)
			return false;

		/* inserting the participants into the participants table */

		if (insertParticipants(meetingID, participants) == m.getParticipantsNum())
			return true;
		return false;
	}

	public static boolean editMeeting(long id, Meeting newMeeting, SRMeetingModification serverResponse) {
		if (!serverResponse.hasData())
			return false;

		/* removing old participants */

		removeParticipants(id);

		/* updating meetings table with the modified meeting */

		mWritableDb = mDbHelper.getWritableDatabase();
		ContentValues values = meetingToValues(newMeeting);
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, serverResponse.getMeetingHash());

		mWritableDb.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + id, null);
		mWritableDb.close();

		/* inserting the new participants into the participants table */

		if (insertParticipants(id, serverResponse.getParticipants()) == newMeeting.getParticipantsNum())
			return true;
		return false;
	}

	public static boolean amIMeetingCreator(long id) {
		mReadableDb = mDbHelper.getReadableDatabase();
		boolean isMeetingCreator = false;
		Cursor c = mReadableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "' and " + 
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS + " = '" + Credentials.MANAGER.toString() + "'",
						null, null, null, null);
		if (c != null) {
			if (c.moveToFirst())
				isMeetingCreator = true;
			c.close();
		}
		mReadableDb.close();
		return isMeetingCreator;
	}

	public static boolean removeMeeting(long id) {

		/* removing old participants */

		removeParticipants(id);

		/* removing meeting's other data */

		mWritableDb = mDbHelper.getWritableDatabase();
		int res = mWritableDb.delete(DbContract.Meetings.TABLE_NAME, DbContract.Meetings._ID + " = " + id, null);
		mWritableDb.close();

		/* checking if at least one row in the database affected from the delete operation. 
		 * if no rows affected, that means that there is no meeting with the given id. */
		if (res != 0)
			return true;
		return false;
	}

	public static boolean replaceAndRemoveMeetingManager(String oldMeetingHash, String newManagerPhone, SRMeetingManagerReplacement servResp) {
		long meetingId = getMeetingIdByHash(servResp.getMeetingNewHash());
		return changeMeetingManager(oldMeetingHash, newManagerPhone, servResp) && 
				removeParticipant(meetingId, getParticipantByPhoneNumber(meetingId, newManagerPhone)); 
	}
	
	/**
	 * Substitutes the meeting's manager
	 * @param id the id of the meeting in the local data-base
	 * @param newManager the participant that is going to be the new meeting's manager
	 * @return <code>true</code> if substitutes succeeded, <code>false</code> otherwise
	 */
	public static boolean changeMeetingManager(String oldMeetingHash, String newManagerPhone, SRMeetingManagerReplacement servResp) {
		return appointNewManager(oldMeetingHash, servResp.getMeetingNewHash(), servResp.getOldManagerNewHash(), newManagerPhone, servResp.getNewManagerNewHash());
//		mWritableDb = mDbHelper.getWritableDatabase();
//
//		/* updating only the meeting manager and giving him/her the credentials he/she needs to manage the meeting */
//		ContentValues values = new ContentValues();
//		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.MANAGER.toString());
//		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, servResp.getNewManagerHash());
//
//		/* updating the local database with the new meeting's manager */
//		int updateRes = mWritableDb.update(DbContract.Participants.TABLE_NAME,
//				values,
//				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
//						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + newManager.getPhone() + "'", null);
//
//		mWritableDb.close();
//
//		/* checking if the update of the credentials of the chosen-to-lead participant didn't went well */
//		if (updateRes == 0)
//			return false;
//
//		Participant exManager = getMeetingManager(id);
//
//		values = new ContentValues();
//		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.REGULAR.toString());
//		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, servResp.getOldManagerHash());
//
//		mWritableDb = mDbHelper.getWritableDatabase();
//
//		/* updating the credentials and hash of the replaced manager */
//		updateRes = mWritableDb.update(DbContract.Participants.TABLE_NAME,
//				values,
//				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
//						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + exManager.getPhone() + "'", null);
//
//		mWritableDb.close();
//
//		/* checking if the update of the credentials and hash of the ex-manager didn't went well */
//		if (updateRes == 0) {
//			values = new ContentValues();
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.REGULAR.toString());
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, newManager.getHash());
//
//			mWritableDb = mDbHelper.getWritableDatabase();
//
//			/* reverting the credentials and hash of the replaced manager */
//			updateRes = mWritableDb.update(DbContract.Participants.TABLE_NAME,
//					values,
//					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
//							DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + newManager.getPhone() + "'", null);
//
//			mWritableDb.close();
//			return false;
//		}
//
//		values = new ContentValues();
//		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER, newManager.getPhone());
//		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_HASH, servResp.getMeetingHash());
//
//		mWritableDb = mDbHelper.getWritableDatabase();
//
//		/* updating the local database with the new meeting's manager */
//		updateRes = mWritableDb.update(DbContract.Meetings.TABLE_NAME, values, DbContract.Meetings._ID + " = " + id, null);
//
//		mWritableDb.close();
//
//		/* checking if the update of the meeting's manager didn't went well. if so, reverting the changes */
//		if (updateRes == 0) {
//
//			/* reverting the first change - original manager */
//			values = new ContentValues();
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.MANAGER.toString());
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, exManager.getHash());
//
//			mWritableDb = mDbHelper.getWritableDatabase();
//
//			/* reverting the credentials and hash of the replaced manager */
//			updateRes = mWritableDb.update(DbContract.Participants.TABLE_NAME,
//					values,
//					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
//							DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + exManager.getPhone() + "'", null);
//
//			/* reverting the second change - new manager */
//			values = new ContentValues();
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, Credentials.REGULAR.toString());
//			values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, newManager.getHash());
//
//			mWritableDb = mDbHelper.getWritableDatabase();
//
//			/* reverting the credentials and hash for the participant that was suppose to be the new manager */
//			mWritableDb.update(DbContract.Participants.TABLE_NAME,
//					values,
//					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + id + " and " + 
//							DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + newManager.getPhone() + "'", null);
//		}
//
//		return updateRes == 1;
	}

	public static boolean removeParticipant(long meetingId, Participant p) {

		mWritableDb = mDbHelper.getWritableDatabase();

		/* removing the meeting's manager from the participants list */
		int deleteRes = mWritableDb.delete(DbContract.Participants.TABLE_NAME,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + p.getPhone() + "'",
						null);

		mWritableDb.close();

		return deleteRes == 1;
	}

	public static boolean attendAMeeting(long meetingId) {
		return meetingAttending(meetingId, RSVP.YES);
	}

	public static boolean declineAMeeting(long meetingId) {
		return meetingAttending(meetingId, RSVP.NO);
	}

	private static boolean meetingAttending(long meetignId, RSVP attend) {
		mWritableDb = mDbHelper.getWritableDatabase();

		/* updating only the rsvp attending status of the device's user */
		ContentValues values = new ContentValues();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP, attend.toString());

		/* updating the local data-base with the new status of the device's user */
		int updateRes = mWritableDb.update(DbContract.Participants.TABLE_NAME,
				values,
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetignId + " and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(mContext) + "'",
						null);

		mWritableDb.close();

		return updateRes == 1;
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
		values.put(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER, m.getManager());
		return values;
	}

	/**
	 * Inserts participants data to the database.
	 * @param id the row ID in the database of the meeting that the participants are belong to.
	 * @param m the Meeting instance that holds the participants info.
	 * @param serverData the hash values calculated on the participants data by the server.
	 * @return the number of successfully added participants.
	 */
	private static int insertParticipants(long id, List<Participant> participants) {
		int count = 0;
		for (Participant p : participants) {
			long res = insertParticipant(id, p);
			if (res != -1)
				count++;
		}
		return count;
	}

	private static long insertParticipant(long id, Participant p) {
		mWritableDb = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		String bla = p.getPhone();
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE, p.getPhone());
		//TODO dont forget to change the data for the name to something other than phone number
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME, p.getPhone());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID, id);
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS, p.getCredentials());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP, p.getRSVP());
		//		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_SHARE_LOCATION_STATUS, m.getShareLocationTime());
		values.put(DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH, p.getHash());
		long res = mWritableDb.insert(DbContract.Participants.TABLE_NAME, "null", values);
		mWritableDb.close();
		return res;
	}

	/**
	 * Removes participants from the database
	 * @param meetingID the ID of the meeting row in the database that it's participants are going to be removed.
	 */
	private static void removeParticipants(long meetingID) {
		mWritableDb = mDbHelper.getWritableDatabase();
		mWritableDb.delete(DbContract.Participants.TABLE_NAME, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingID, null);
		mWritableDb.close();
	}

}
