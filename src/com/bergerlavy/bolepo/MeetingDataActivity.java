package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.RSVP;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingAttendance;
import com.bergerlavy.bolepo.dals.SRMeetingUnattendance;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;

public class MeetingDataActivity extends Activity {

	private DbHelper mDbHelper;

	private TextView mName;
	private TextView mDate;
	private TextView mTime;
	private TextView mLocation;
	private TextView mSharingTime;
	private TextView mParticipants;

	private boolean mIsManager;
	private long mId;
	private String mHash;

	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_MEETING_CREATOR_REMOVAL = "EXTRA_MEETING_CREATOR_REMOVAL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_data);

		mName = (TextView) findViewById(R.id.meeting_data_name);
		mDate = (TextView) findViewById(R.id.meeting_data_date);
		mTime = (TextView) findViewById(R.id.meeting_data_time);
		mLocation = (TextView) findViewById(R.id.meeting_data_location);
		mSharingTime = (TextView) findViewById(R.id.meeting_data_share_locations_time);

		String meetingManager = null;

		mDbHelper = new DbHelper(this);
		mId = getIntent().getLongExtra(MainActivity.EXTRA_MEETING_ID, -1);
		SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
		Cursor c = readableDb.query(DbContract.Meetings.TABLE_NAME, null,
				DbContract.Meetings._ID + " = " + mId,
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				mName.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)));
				mDate.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)));
				mTime.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)));
				mLocation.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)));
				mSharingTime.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME)));
				meetingManager = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER));
				mHash = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_HASH));
			}
			c.close();
		}
		readableDb.close();

		if (meetingManager.equalsIgnoreCase(BolePoMisc.getDevicePhoneNumber(this))) {

			/* hiding the accept button when the viewer is the meeting manager because obviously he accepted it if he is the manager */
			((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.INVISIBLE);

			mIsManager = true;
		}
		else {
			readableDb = mDbHelper.getReadableDatabase();
			c = readableDb.query(DbContract.Participants.TABLE_NAME,
					new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP },
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + mId + " and " + DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(this) + "'",
					null, null, null, null);
			if (c != null) {
				if (c.moveToFirst()) {
					if (RSVP.YES.getRsvp().equalsIgnoreCase(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP)))) {

						/* hiding the accept button when the viewer already accepted this meeting */
						((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.INVISIBLE);
					}
				}
				c.close();
			}
			readableDb.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user_card, menu);
		return true;
	}

	public void commitAction(View view) {
		boolean toRefresh = false;

		/* if the attend button has been pressed */
		if (view.getId() == R.id.meeting_data_attend_button) {
			SRMeetingAttendance servResp = SDAL.attendAMeeting(mHash);
			if (servResp.isOK()) {
				if (DAL.attendAMeeting(mId)) {
					toRefresh = true;
				}
			}
		}

		/* if the unattend button as been pressed */
		else if (view.getId() == R.id.meeting_data_unattend_button) {
			/* if the user card is being watched by the manager of the meeting, navigating to the meeting removal activity */
			if (mIsManager) {
				Intent intent = new Intent(this, RemoveMeetingActivity.class);
				intent.putExtra(EXTRA_MEETING_ID, mId);
				intent.putExtra(EXTRA_MEETING_CREATOR_REMOVAL, true);
				startActivity(intent);
			}
			else {
				SRMeetingUnattendance servResp = SDAL.unattendAMeeting(mHash);
				if (servResp.isOK()) {
					if (DAL.unattendAMeeting(mId)) {
						toRefresh = true;
					}
				}
			}
		}

		if (toRefresh) {
			Intent intent = new Intent();
			intent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
			sendBroadcast(intent);
		}
	}

}
