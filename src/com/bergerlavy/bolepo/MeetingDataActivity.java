package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bergerlavy.bolepo.BolePoConstants.RSVP;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.MeetingsDbAdapter;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingAttendance;
import com.bergerlavy.bolepo.dals.SRMeetingDeclining;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;

public class MeetingDataActivity extends Activity {

	private MeetingsDbAdapter mDbAdapter;
	
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
	
	private static final int RQ_LEAVE_MEETING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_data);

		mDbAdapter = new MeetingsDbAdapter(this);
		mDbAdapter.open();
		
		mName = (TextView) findViewById(R.id.meeting_data_name);
		mDate = (TextView) findViewById(R.id.meeting_data_date);
		mTime = (TextView) findViewById(R.id.meeting_data_time);
		mLocation = (TextView) findViewById(R.id.meeting_data_location);
		mSharingTime = (TextView) findViewById(R.id.meeting_data_share_locations_time);
		mParticipants = (TextView) findViewById(R.id.meeting_data_participants);

		String meetingManager = null;

		mId = getIntent().getLongExtra(MainActivity.EXTRA_MEETING_ID, -1);
		Meeting m = mDbAdapter.getMeetingById(mId);
		mName.setText(m.getName());
		mDate.setText(m.getDate());
		mTime.setText(m.getTime());
		mLocation.setText(m.getLocation());
		mSharingTime.setText(m.getShareLocationTime());
		meetingManager = m.getManager();
		mHash = m.getHash();

		boolean showLeaveTitle = false;
		/* checking if the current user is the meeting manager */
		if (meetingManager.equalsIgnoreCase(BolePoMisc.getDevicePhoneNumber(this))) {

			/* hiding the accept button when the viewer is the meeting manager because obviously he accepted it if he is the manager */
			((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.GONE);

			mIsManager = true;
			showLeaveTitle = true;
		}
		else {
			RSVP r = mDbAdapter.getMyRsvp(mId);
			if (r == RSVP.YES) {

				/* hiding the accept button when the viewer already accepted this meeting */
				((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.GONE);
				
				showLeaveTitle = true;
			}
		}
		
		/* if the user accepted to attend the meeting in the past, displaying a different text on the "unattend" button */
		if (showLeaveTitle)
			((Button) findViewById(R.id.meeting_data_decline_button)).setText(getString(R.string.meeting_data_leave_button_text));
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
				if (mDbAdapter.attendAMeeting(mId)) {
					toRefresh = true;
				}
			}
		}

		/* if the decline button as been pressed */
		else if (view.getId() == R.id.meeting_data_decline_button) {
			/* if the user card is being watched by the manager of the meeting, navigating to the meeting removal activity */
			if (mIsManager) {
				Intent intent = new Intent(this, RemoveMeetingActivity.class);
				intent.putExtra(EXTRA_MEETING_ID, mId);
				intent.putExtra(EXTRA_MEETING_CREATOR_REMOVAL, true);
				startActivityForResult(intent, RQ_LEAVE_MEETING);
			}
			else {
				SRMeetingDeclining servResp = SDAL.declineAMeeting(mHash);
				if (servResp.isOK()) {
					if (mDbAdapter.declineAMeeting(mId)) {
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
		
		/* in case the user is the meeting manager we don't want to close the activity because a 
		 * dialog box is shown to him, and until he doesn't make his choice, he should remain 
		 * in the activity */
		if (!mIsManager)
			finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RQ_LEAVE_MEETING) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	

}
