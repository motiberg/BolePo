package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoConstants.RSVP;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingAttendance;
import com.bergerlavy.bolepo.dals.SRMeetingUnattendance;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;
import com.bergerlavy.db.DbHelper;

public class MeetingDataActivity extends Activity {

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
		mParticipants = (TextView) findViewById(R.id.meeting_data_participants);

		String meetingManager = null;

		mId = getIntent().getLongExtra(MainActivity.EXTRA_MEETING_ID, -1);
		Meeting m = DAL.getMeetingById(mId);
		mName.setText(m.getName());
		mDate.setText(m.getDate());
		mTime.setText(m.getTime());
		mLocation.setText(m.getLocation());
		mSharingTime.setText(m.getShareLocationTime());
		meetingManager = m.getManager();
		mHash = m.getHash();

		if (meetingManager.equalsIgnoreCase(BolePoMisc.getDevicePhoneNumber(this))) {

			/* hiding the accept button when the viewer is the meeting manager because obviously he accepted it if he is the manager */
			((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.GONE);

			mIsManager = true;
		}
		else {
			RSVP r = DAL.getMyRsvp(mId);
			if (r == RSVP.YES) {

				/* hiding the accept button when the viewer already accepted this meeting */
				((Button) findViewById(R.id.meeting_data_attend_button)).setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user_card, menu);
		return true;
	}

	public void commitAction(View view) {
		Toast.makeText(this, "dfdfd", Toast.LENGTH_LONG).show();
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

		/* if the decline button as been pressed */
		else if (view.getId() == R.id.meeting_data_decline_button) {
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
