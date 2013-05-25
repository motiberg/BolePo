package com.bergerlavy.bolepo.forms;

import android.app.Activity;
import android.content.Context;
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
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.MeetingsDbAdapter;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingManagerReplacementAndRemoval;
import com.bergerlavy.bolepo.dals.ServerResponse;

public class RemoveMeetingActivity extends Activity {

	private MeetingsDbAdapter mDbAdapter;
	private long mMeetingIdToRemove;

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
			Meeting meeting = mDbAdapter.getMeetingById(mMeetingIdToRemove);
			new MeetingRemovalTask(this).execute(meeting.getHash());
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
					new ReplaceAndRemoveMeetingManagerTask(this).execute(contactPhone);
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

		private Context mContext;
		
		public MeetingRemovalTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			boolean shouldUpdate = false;
			ServerResponse servResp = SDAL.removeMeeting(params[0]);

			System.out.println("Opening DB on MeetingRemovalTask");
			mDbAdapter = new MeetingsDbAdapter(mContext);
			mDbAdapter.open();
			
			if (servResp.isOK()) {
				if (mDbAdapter.removeMeeting(mMeetingIdToRemove))
					shouldUpdate = true;
			}
			System.out.println("Closing DB on MeetingRemovalTask");
			mDbAdapter.close();
			
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

		private Context mContext;
		
		public ReplaceAndRemoveMeetingManagerTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			System.out.println("Opening DB on ReplaceAndRemoveMeetingManagerTask");
			mDbAdapter = new MeetingsDbAdapter(mContext);
			mDbAdapter.open();
			
			ServerResponse servResp = null;
			String oldMeetingHash = mDbAdapter.getMeetingHashById(mMeetingIdToRemove);
			servResp = (SRMeetingManagerReplacementAndRemoval) SDAL.replaceAndRemoveMeetingManager(oldMeetingHash,
					mDbAdapter.getMeetingManager(mMeetingIdToRemove).getHash(),
					mDbAdapter.getParticipantHashByPhone(mMeetingIdToRemove, params[0]));
			if (servResp.isOK()) {
				boolean result = mDbAdapter.replaceAndRemoveMeetingManager(oldMeetingHash, params[0], (SRMeetingManagerReplacementAndRemoval) servResp);
				System.out.println("Closing DB on ReplaceAndRemoveMeetingManagerTask");
				mDbAdapter.close();
				return result;
			}
			System.out.println("Closing DB on ReplaceAndRemoveMeetingManagerTask");
			mDbAdapter.close();
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
	
	@Override
	protected void onStart() {
		System.out.println("Opening DB on RemoveMeetingActivity");
		mDbAdapter = new MeetingsDbAdapter(this);
		mDbAdapter.open();
		SDAL.setContext(this);
		super.onStart();
	}
	

	@Override
	protected void onStop() {
		System.out.println("Closing DB on RemoveMeetingActivity");
		if (mDbAdapter != null) {
			mDbAdapter.close();
			mDbAdapter = null;
		}
		super.onStop();
	}
}
