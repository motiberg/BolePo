package com.bergerlavy.bolepo.forms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;

public class ModifyMeetingValidation implements ValidationStatus {

	private Meeting mMeeting;
	private Context mContext;
	private boolean mChecked;
	private long mId;
	private InputValidationReport mReport;

	public ModifyMeetingValidation(Context c, Meeting m, long id) {
		mMeeting = m;
		mContext = c;
		mId = id;
		mChecked = false;
	}

	@Override
	public InputValidationReport isOK() {
		if (mChecked)
			return mReport;

		mChecked = true;
		SQLiteDatabase readableDB = new DbHelper(mContext).getReadableDatabase();
		
		/* getting all accepted meetings besides the modified meeting */
		Cursor allAcceptedMeetings = readableDB.query(DbContract.Meetings.TABLE_NAME,
				new String[] { DbContract.Meetings._ID, DbContract.Meetings.COLUMN_NAME_MEETING_DATE, DbContract.Meetings.COLUMN_NAME_MEETING_TIME, DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME, DbContract.Meetings.COLUMN_NAME_MEETING_NAME },
				DbContract.Meetings._ID + " != " + mId,
				null, null, null, null);

		if (allAcceptedMeetings != null) {
			if (allAcceptedMeetings.moveToFirst()) {
				String date = allAcceptedMeetings.getString(allAcceptedMeetings.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE));
				String time = allAcceptedMeetings.getString(allAcceptedMeetings.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME));
				String shareTime = allAcceptedMeetings.getString(allAcceptedMeetings.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME));
				String name = allAcceptedMeetings.getString(allAcceptedMeetings.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME));
				/* checking whether the new meeting and the meeting from the database are occurring at the same date */
				if (mMeeting.getDate().equals(date)) {
					Time dbTime = new Time(time);
					Time dbShareTime = new Time(shareTime);
					Time newTime = new Time(mMeeting.getTime());
					if (Time.substract(dbTime, dbShareTime).isSmallerEqualTo(newTime) && newTime.isSmallerEqualTo(dbTime)) {
						allAcceptedMeetings.close();
						readableDB.close();
						mReport = new InputValidationReport.Builder(false)
						.setError("This meeting is overlaps with another meeting of yours: " + name)
						.build();
						return mReport;
					}
					allAcceptedMeetings.close();
					readableDB.close();
					mReport = new InputValidationReport.Builder(true).build();
					return mReport;
				}
				/* handling the case the meeting are at different dates, but actually handling meetings that are
				 * at sequential dates, i.e. the new meeting is day before or day after the
				 * meeting in the database */
				else {
					//TODO implements this.
					allAcceptedMeetings.close();
					readableDB.close();
					mReport = new InputValidationReport.Builder(true).build();
					return mReport;
				}
			}
			else {
				allAcceptedMeetings.close();
				readableDB.close();
				mReport = new InputValidationReport.Builder(true).build();
				return mReport;
			}

		}
		readableDB.close();
		mReport = new InputValidationReport.Builder(false).build();
		return mReport;
	}

}
