package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.TimePickerActivity;
import com.bergerlavy.bolepo.dals.Action;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRDataForMeetingManaging;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.bolepo.dals.ServerResponseStatus;
import com.bergerlavy.bolepo.maps.MeetingLocationSelectionActivity;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.bergerlavy.bolepo.shareddata.Misc;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;


public class MeetingManagementActivity extends Activity {

	private EditText mName;
	private EditText mDate;
	private EditText mTime;
	private EditText mLocation;
	private EditText mShareLocationTime;
	//TODO put value in mParticipantsHashes
	private List<String> mParticipants;
	private Button mCommitActionButton;
	private TextView mParticipantsCount;

	private Action mAction;
	private String mModifiedMeetingHash;
	private long mModifiedMeetingID;
	
	private static RefreshMeetingsListListener mRefreshMeetingsListListener;

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	public static final int RQ_MEETING_LOCATION = 1;
	public static final int RQ_MEETING_TIME = 2;
	public static final int RQ_MEETING_PARTICIPANTS = 3;
	public static final int RQ_MEETING_SHARETIME = 4;

	/***********************************************************/
	/************************* EXTRAS **************************/
	/***********************************************************/
	public static final String EXTRA_PARTICIPANTS = "EXTRA_PARTICIPANTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_management);

		mParticipants = new ArrayList<String>();
		mParticipantsCount = (TextView) findViewById(R.id.create_meeting_participants_textview);

		mName = (EditText) findViewById(R.id.create_meeting_purpose_edittext);
		mDate = (EditText) findViewById(R.id.create_meeting_date_edittext);
		mTime = (EditText) findViewById(R.id.create_meeting_time_edittext);
		mLocation = (EditText) findViewById(R.id.create_meeting_location_edittext);
		mShareLocationTime = (EditText) findViewById(R.id.create_meeting_share_locations_time_edittext);
		mCommitActionButton = (Button) findViewById(R.id.meeting_managment_commit_action_button);

		mTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MeetingManagementActivity.this, TimePickerActivity.class);
				startActivityForResult(intent, RQ_MEETING_TIME);
			}
		});
		
		mShareLocationTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MeetingManagementActivity.this, TimePickerActivity.class);
				startActivityForResult(intent, RQ_MEETING_SHARETIME);				
			}
		});

		Bundle extras = getIntent().getExtras();

		/* extracting the which operation the user intending to make */
		if (extras.getBoolean(MainActivity.EXTRA_MEETING_CREATION)) {
			mAction = Action.CREATE;
		}
		if (extras.getBoolean(MainActivity.EXTRA_MEETING_MODIFYING)) {
			mAction = Action.MODIFY;
		}

		/* checking whether the activity started for modifying an existing meeting */
		if (mAction == Action.MODIFY) {
			mCommitActionButton.setText(getResources().getString(R.string.meeting_managment_modify_button_text));

			DbHelper dbHelper = new DbHelper(this);
			SQLiteDatabase readableDB = dbHelper.getReadableDatabase();

			mModifiedMeetingID = extras.getLong(MainActivity.EXTRA_MEETING_ID);

			/* getting meeting's details by its ID (local dababase ID) */
			Cursor c = readableDB.query(DbContract.Meetings.TABLE_NAME, null, DbContract.Meetings._ID + " = " + mModifiedMeetingID, null, null, null, null);
			if (c != null) {
				c.moveToFirst();

				/* extracting data from the cursor into the TextView objects */
				mName.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)));
				mDate.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)));
				mTime.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)));
				mLocation.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)));
				mShareLocationTime.setText(c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME)));
				mModifiedMeetingHash = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_HASH));
				c.close();
			}

			Cursor p = readableDB.query(DbContract.Participants.TABLE_NAME, new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME },
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = '" + mModifiedMeetingHash + "'",
					null, null, null, null);

			if (p != null && p.moveToFirst()) {
				while (!p.isAfterLast()) {
					mParticipants.add(p.getString(p.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME)));
					p.moveToNext();
				}
				p.close();
			}

			if (mParticipants.size() == 0)
				mParticipantsCount.setText(R.string.create_meeting_no_participants);
			else mParticipantsCount.setText(mParticipants.size() + " " + getString(R.string.create_meeting_participants));

			readableDB.close();
		}
		if (mAction == Action.CREATE) {
			mCommitActionButton.setText(getResources().getString(R.string.meeting_managment_create_button_text));
			mParticipantsCount.setText(R.string.create_meeting_no_participants);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RQ_MEETING_TIME) {
				mTime.setText(Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_HOUR, 0) + "") + ":" + Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_MINUTES, 0) + ""));
			}
			if (requestCode == RQ_MEETING_LOCATION) {
				//TODO
			}
			if (requestCode == RQ_MEETING_PARTICIPANTS) {
				if (data.hasExtra(AddParticipantsActivity.EXTRA_PARTICIPANTS)) {
					String[] participants = data.getStringArrayExtra(AddParticipantsActivity.EXTRA_PARTICIPANTS);
					if (participants != null) {
						if (participants.length > 0) {
							mParticipantsCount.setText(participants.length + " " + getString(R.string.create_meeting_participants));
							mParticipants.clear();
							for (String s : participants)
								mParticipants.add(s);
						}
						else mParticipantsCount.setText(R.string.create_meeting_no_participants);
					}
				}
			}
			if (requestCode == RQ_MEETING_SHARETIME) {
				mShareLocationTime.setText(Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_HOUR, 0) + "") + ":" + Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_MINUTES, 0) + ""));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @param view
	 */
	public void commitAction(View view) {
		mParticipants.add(BolePoMisc.getDevicePhoneNumber(this));
		final Meeting meeting = new Meeting(
				mName.getText().toString(), 
				mDate.getText().toString(), 
				mTime.getText().toString(), 
				BolePoMisc.getDevicePhoneNumber(this),
				mLocation.getText().toString(), 
				mShareLocationTime.getText().toString(),
				mParticipants
				);

		/* checking if the input is valid according to decided rules */
		InputValidationReport report = null;
		if (mAction == Action.CREATE)
			report = FormsSupport.createMeetingInputValidation(this, meeting);
		else if (mAction == Action.MODIFY)
			report = FormsSupport.modifyMeetingInputValidation(this, meeting, mModifiedMeetingID);
		if (report.isOK()) {


			new ManageMeetingOnServer().execute(meeting);


			Intent intent = new Intent(this, ShareLocationsService.class);
			stopService(intent);
			startService(intent);
			finish();



		} else {
			/* input is invalid*/
			Toast.makeText(this, report.getError(), Toast.LENGTH_LONG).show();

		}

	}

	public void setParticipants(View view) {
		Intent intent = new Intent(this, AddParticipantsActivity.class);

		/* in case the kind of action is modifying an existing meeting, its needed to pass the already
		 * invited participants so the user will be able to remove invited participants if he would like to, or 
		 * just to see the participants he already invited. */
		if (mAction == Action.MODIFY) {
			SQLiteDatabase readableDB = new DbHelper(this).getReadableDatabase();

			/* getting all the participants that belong to the modified meeting using the meeting id in the 
			 * local data-base */
			Cursor participantsCursor = readableDB.query(DbContract.Participants.TABLE_NAME,
					new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME },
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + mModifiedMeetingID,
					null, null, null, null);

			/* in case the query went well and its not empty */
			if (participantsCursor != null && participantsCursor.moveToFirst()) {
				List<String> participants = new ArrayList<String>();

				/* iterating through the cursor's records and getting the participants name and adding to a list */
				while (!participantsCursor.isAfterLast()) {
					participants.add(participantsCursor.getString(participantsCursor.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME)));
					participantsCursor.moveToNext();
				}

				String[] parts = new String[participants.size()];
				parts = participants.toArray(parts);
				intent.putExtra(EXTRA_PARTICIPANTS, parts);

				/* closing cursor and data-base */
				participantsCursor.close();
				readableDB.close();
			}
		}
		else if (mAction == Action.CREATE) {
			String[] strs = new String[mParticipants.size()];
			strs = mParticipants.toArray(strs);
			intent.putExtra(EXTRA_PARTICIPANTS, strs);
		}
		startActivityForResult(intent, RQ_MEETING_PARTICIPANTS);
	}

	public void setLocation(View view) {
		Intent intent = new Intent(this, MeetingLocationSelectionActivity.class);
		startActivityForResult(intent, RQ_MEETING_LOCATION);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_meeting_managing, menu);
		return true;
	}

	public class ManageMeetingOnServer extends AsyncTask<Meeting, Void, Boolean> {

		ServerResponse servResp = null;

		@Override
		protected Boolean doInBackground(Meeting... params) {
			boolean shouldUpdate = false;
			Meeting meeting = params[0];
			if (mAction == Action.CREATE)
				servResp = SDAL.createMeeting(meeting);
			if (mAction == Action.MODIFY) {
				meeting.setHash(mModifiedMeetingHash);
				servResp = SDAL.editMeeting(meeting);
			}

			/* checking if the sending of the meeting data to the server SUCCEEDED */
			if (servResp != null && servResp.getStatus() == ServerResponseStatus.OK) {

				if (servResp.hasData()) {

					SRDataForMeetingManaging serverData = servResp.getData();
					if (mAction == Action.CREATE) {
						/* checking if the meetings has been successfully inserted to the DB */
						if (DAL.createMeeting(meeting, serverData))
							shouldUpdate = true;
					}
					if (mAction == Action.MODIFY) {
						if (DAL.editMeeting(mModifiedMeetingID, meeting, serverData)) {
							shouldUpdate = true;
						}
					}
				}
			} else {
				/* server returned some error */

			}

			return shouldUpdate;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result)
				mRefreshMeetingsListListener.onUpdate();
			super.onPostExecute(result);
		}

	}
	
	public static void registerForMeetingsUpdate(RefreshMeetingsListListener l) {
		mRefreshMeetingsListListener = l;
	}

}
