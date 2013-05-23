package com.bergerlavy.bolepo.forms;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.bergerlavy.bolepo.BolePoConstants;
import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingManagerReplacement;
import com.bergerlavy.bolepo.dals.SRMeetingManagerReplacementAndRemoval;
import com.bergerlavy.bolepo.dals.ServerResponse;

public class RemoveMeetingActivity extends Activity {

	private long mMeetingIdToRemove;
	private String mMeetingManager;

	public static final String EXTRA_REMOVE_MEETING_CHOOSE_CONTACT = "EXTRA_REMOVE_MEETING_CHOOSE_CONTACT";
	public static final String EXTRA_REMOVE_MEETING_MEETING_ID = "EXTRA_REMOVE_MEETING_MEETING_ID";

	private static final int RQ_GET_CONTACT_TO_MANAGE_MEETING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_meeting);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		TextView messageTV = (TextView) findViewById(R.id.remove_meeting_message);

		boolean meetingCreatorKindOfRemoval = false;

		Bundle bundle = getIntent().getExtras();
		mMeetingIdToRemove = bundle.getLong(MainActivity.EXTRA_MEETING_ID);
		if (bundle.containsKey(MainActivity.EXTRA_MEETING_CREATOR_REMOVAL))
			meetingCreatorKindOfRemoval = bundle.getBoolean(MainActivity.EXTRA_MEETING_CREATOR_REMOVAL, false);

		String message;
		if (meetingCreatorKindOfRemoval)
			message = "Would you like to remove this meeting permanently, or to pass the leadership to one of the participants ?";
		else { 
			message = "Would you like to remove this meeting ?";
			((Button) findViewById(R.id.remove_meeting_pass_ownship)).setVisibility(View.INVISIBLE);
		}

		messageTV.setText(message);
	}

	public void remove(View view) {
		switch (view.getId()) {
		case R.id.remove_meeting_confirm_remove:
			Meeting meeting = DAL.getMeetingById(mMeetingIdToRemove);
			mMeetingManager = meeting.getManager();

			new MeetingRemovalTask().execute(meeting.getHash());

			setResult(RESULT_OK);
			finish();
			break;
		case R.id.remove_meeting_pass_ownship:
			Intent intent = new Intent(this, AddParticipantsActivity.class);
			intent.putExtra(EXTRA_REMOVE_MEETING_CHOOSE_CONTACT, true);
			intent.putExtra(EXTRA_REMOVE_MEETING_MEETING_ID, mMeetingIdToRemove);
			startActivityForResult(intent, RQ_GET_CONTACT_TO_MANAGE_MEETING);
			break;
		case R.id.remove_meeting_cancel_remove:
			setResult(RESULT_CANCELED);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RQ_GET_CONTACT_TO_MANAGE_MEETING) {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra(AddParticipantsActivity.EXTRA_CONTACT_TO_MANAGE)) {
					String contactPhone = data.getStringExtra(AddParticipantsActivity.EXTRA_CONTACT_TO_MANAGE);
					new ReplaceAndRemoveMeetingManagerTask().execute(contactPhone);
				}
			}
			if (resultCode == RESULT_CANCELED) {
				//TODO user didn't select a future manager, consider what to do in this case 
			}
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class MeetingRemovalTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			boolean shouldUpdate = false;
			ServerResponse servResp = SDAL.removeMeeting(params[0]);

			if (servResp.isOK()) {
				if (DAL.removeMeeting(mMeetingIdToRemove))
					shouldUpdate = true;
			}
			return shouldUpdate;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent refreshListIntent = new Intent();
				refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
				sendBroadcast(refreshListIntent); 
			}
			super.onPostExecute(result);
		}

	}
	
	public class ReplaceAndRemoveMeetingManagerTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			ServerResponse servResp = null;
			String oldMeetingHash = DAL.getMeetingHashById(mMeetingIdToRemove);
			servResp = (SRMeetingManagerReplacementAndRemoval) SDAL.replaceAndRemoveMeetingManager(oldMeetingHash,
					DAL.getMeetingManager(mMeetingIdToRemove).getHash(),
					DAL.getParticipantHashByPhone(mMeetingIdToRemove, params[0]));
			if (servResp.isOK())
				return DAL.replaceAndRemoveMeetingManager(oldMeetingHash, params[0], (SRMeetingManagerReplacement) servResp);
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent refreshListIntent = new Intent();
				refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
				sendBroadcast(refreshListIntent); 
			}
			super.onPostExecute(result);
		}

	}
}
