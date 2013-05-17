package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.bergerlavy.bolepo.DatePickerActivity;
import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.TimePickerActivity;
import com.bergerlavy.bolepo.dals.Action;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingCreation;
import com.bergerlavy.bolepo.dals.SRMeetingModification;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.bolepo.maps.MeetingLocationSelectionActivity;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.bergerlavy.bolepo.shareddata.Misc;


public class MeetingManagementActivity extends Activity {

	private EditText mName;
	private TextView mDate;
	private TextView mTime;
	private TextView mLocation;
	private TextView mShareLocationTime;
	private List<String> mParticipants;
	private Button mCommitActionButton;

	private Action mAction;
	private String mModifiedMeetingHash;
	private long mModifiedMeetingID;

	/**
	 * An indicator for the meeting time choosing activity status. A 'true' value means
	 * the activity is at the front of the screen. A 'false' value means it doesn't. The
	 * primary purpose of this indicator is to disable multiple instances of the activity.
	 */
	private static boolean mTimeDialogIsOn;

	/**
	 * An indicator for the meeting date choosing activity status. A 'true' value means
	 * the activity is at the front of the screen. A 'false' value means it doesn't. The
	 * primary purpose of this indicator is to disable multiple instances of the activity.
	 */
	private static boolean mDateDialogIsOn;

	/**
	 * An indicator for the meeting location sharing time choosing activity status.
	 * A 'true' value means the activity is at the front of the screen.
	 * A 'false' value means it doesn't. The primary purpose of this indicator is
	 * to disable multiple instances of the activity.
	 */
	private static boolean mShareLocationTimeDialogIsOn;

	private static RefreshMeetingsListListener mRefreshMeetingsListListener;

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	private static final int RQ_MEETING_LOCATION = 1;
	private static final int RQ_MEETING_TIME = 2;
	private static final int RQ_MEETING_PARTICIPANTS = 3;
	private static final int RQ_MEETING_SHARETIME = 4;
	private static final int RQ_MEETING_DATE = 5;

	/***********************************************************/
	/************************* EXTRAS **************************/
	/***********************************************************/
	public static final String EXTRA_PARTICIPANTS = "EXTRA_PARTICIPANTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_management);

		mParticipants = new ArrayList<String>();

		mName = (EditText) findViewById(R.id.meeting_management_purpose_edittext);
		mDate = (TextView) findViewById(R.id.meeting_management_date_edittext);
		mTime = (TextView) findViewById(R.id.meeting_management_time_edittext);
		mLocation = (TextView) findViewById(R.id.meeting_management_location_edittext);
		mShareLocationTime = (TextView) findViewById(R.id.meeting_management_share_locations_time_edittext);
		mCommitActionButton = (Button) findViewById(R.id.meeting_managment_commit_action_button);

		mTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mTimeDialogIsOn) {
					Intent intent = new Intent(MeetingManagementActivity.this, TimePickerActivity.class);
					startActivityForResult(intent, RQ_MEETING_TIME);
					mTimeDialogIsOn = true;
				}
			}
		});

		mShareLocationTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mShareLocationTimeDialogIsOn) {
					Intent intent = new Intent(MeetingManagementActivity.this, TimePickerActivity.class);
					intent.putExtra(TimePickerActivity.EXTRA_INIT_HOUR, 1);
					intent.putExtra(TimePickerActivity.EXTRA_INIT_MINUTES, 0);
					startActivityForResult(intent, RQ_MEETING_SHARETIME);
					mShareLocationTimeDialogIsOn = true;
				}
			}
		});

		mDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mDateDialogIsOn) {
					Intent intent = new Intent(MeetingManagementActivity.this, DatePickerActivity.class);
					startActivityForResult(intent, RQ_MEETING_DATE);
					mDateDialogIsOn = true;
				}
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

			/* setting the primary button to have a title that matches the modification mode */
			mCommitActionButton.setText(getResources().getString(R.string.meeting_managment_modify_button_text));

			mModifiedMeetingID = extras.getLong(MainActivity.EXTRA_MEETING_ID);

			Meeting meeting = DAL.getMeetingById(mModifiedMeetingID);
			mName.setText(meeting.getName());
			mDate.setText(meeting.getDate());
			mTime.setText(meeting.getTime());
			mLocation.setText(meeting.getLocation());
			mShareLocationTime.setText(meeting.getShareLocationTime());
			mModifiedMeetingHash = meeting.getHash();

			mParticipants = DAL.getParticipantsPhonesAsList(mModifiedMeetingID);
		}
		if (mAction == Action.CREATE) {

			/* setting the primary button to have a title that matches the creation mode */
			mCommitActionButton.setText(getResources().getString(R.string.meeting_managment_create_button_text));

			/* adding the meeting's creator to the participants list */
			mParticipants.add(BolePoMisc.getDevicePhoneNumber(this));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RQ_MEETING_TIME) {
			if (resultCode == RESULT_OK) {
				mTime.setText(Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_HOUR, 0) + "") + ":" + Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_MINUTES, 0) + ""));
			}
			mTimeDialogIsOn = false;
		}
		if (requestCode == RQ_MEETING_LOCATION) {
			//TODO
		}
		if (requestCode == RQ_MEETING_PARTICIPANTS) {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra(AddParticipantsActivity.EXTRA_PARTICIPANTS)) {
					String[] participants = data.getStringArrayExtra(AddParticipantsActivity.EXTRA_PARTICIPANTS);
					if (participants != null) {
						mParticipants.clear();
						for (String s : participants)
							mParticipants.add(s);

					}
				}
			}
		}
		if (requestCode == RQ_MEETING_SHARETIME) {
			if (resultCode == RESULT_OK) {
				mShareLocationTime.setText(Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_HOUR, 0) + "") + ":" + Misc.pumpStrToDoubleCharacters(data.getIntExtra(TimePickerActivity.EXTRA_MINUTES, 0) + ""));
			}
			mShareLocationTimeDialogIsOn = false;
		}
		if (requestCode == RQ_MEETING_DATE) {
			if (resultCode == RESULT_OK) {
				mDate.setText(Misc.pumpStrToDoubleCharacters(data.getIntExtra(DatePickerActivity.EXTRA_DAY, 0) + "") + "/" + 
						Misc.pumpStrToDoubleCharacters(data.getIntExtra(DatePickerActivity.EXTRA_MONTH, 0) + "") + "/" + 
						data.getIntExtra(DatePickerActivity.EXTRA_YEAR, 0));
			}
			mDateDialogIsOn = false;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @param view
	 */
	public void commitAction(View view) {
		final Meeting meeting = new Meeting(
				mName.getText().toString(), 
				mDate.getText().toString(), 
				mTime.getText().toString(), 
				BolePoMisc.getDevicePhoneNumber(this),
				//				mLocation.getText().toString(),
				"dfdfdf",
				mShareLocationTime.getText().toString(),
				mParticipants
				);

		/* checking if the input is valid according to decided rules */
		InputValidationReport report = null;
		if (mAction == Action.CREATE)
			report = FormsSupport.createMeetingInputValidation(meeting);
		else if (mAction == Action.MODIFY)
			report = FormsSupport.modifyMeetingInputValidation(meeting, mModifiedMeetingID);
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
		String[] strs = new String[mParticipants.size()];
		strs = mParticipants.toArray(strs);
		intent.putExtra(EXTRA_PARTICIPANTS, strs);
		startActivityForResult(intent, RQ_MEETING_PARTICIPANTS);
	}

	public void setLocation(View view) {
		Intent intent = new Intent(this, MeetingLocationSelectionActivity.class);
		startActivityForResult(intent, RQ_MEETING_LOCATION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_meeting_managing, menu);
		return true;
	}

	public class ManageMeetingOnServer extends AsyncTask<Meeting, Void, Boolean> {

		ServerResponse servResp = null;
		SRMeetingCreation servRespCreation = null;
		SRMeetingModification servRespModification = null;

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
			if (servResp.isOK()) {
				if (mAction == Action.CREATE) {
					if (servResp instanceof SRMeetingCreation)
						servRespCreation = (SRMeetingCreation) servResp;
					else throw new ClassCastException();
					if (DAL.createMeeting(meeting, servRespCreation))
						shouldUpdate = true;
				}
				if (mAction == Action.MODIFY) {
					if (servResp instanceof SRMeetingModification)
						servRespModification = (SRMeetingModification) servResp;
					else throw new ClassCastException();
					if (DAL.editMeeting(mModifiedMeetingID, meeting, servRespModification)) {
						shouldUpdate = true;
					}
				}
			} else {
				/* server returned some error */
				//TODO
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
