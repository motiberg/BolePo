package com.bergerlavy.bolepo.forms;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.bergerlavy.bolepo.R;
import com.google.android.gcm.GCMRegistrar;


public class MeetingCreationActivity extends Activity {

	private EditText mPurpose;
	private EditText mDate;
	private EditText mTime;
	private EditText mLocation;
	private EditText mShareLocationTime;
	private List<Participant> mParticipants;

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	public static final int meetingLocationRQ = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_creation);

		mPurpose = (EditText) findViewById(R.id.create_meeting_purpose_edittext);
		mDate = (EditText) findViewById(R.id.create_meeting_date_edittext);
		mTime = (EditText) findViewById(R.id.create_meeting_time_edittext);
		mLocation = (EditText) findViewById(R.id.create_meeting_location_edittext);
		mShareLocationTime = (EditText) findViewById(R.id.create_meeting_share_locations_time_edittext);

		/* verifies that the device supports GCM */
		GCMRegistrar.checkDevice(this);

		/* verifies that the application manifest contains meets all the requirements */
		//TODO remove this line when publishing the application
		GCMRegistrar.checkManifest(this);

		
		/* check if the device is already registered */
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		/* turns out the device isn't registered yet */
		if (regId.equals("")) {
			
			/* register the device, passing the SENDER_ID as received when signed up for GCM */
			GCMRegistrar.register(this, MeetMeConstants.SENDER_ID);
			
		} else {
			//"Already registered"
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @param view
	 */
	public void createMeeting(View view) {
		CreatingMeetingData cmData = new CreatingMeetingData(
				mPurpose.getText().toString(), 
				mDate.getText().toString(), 
				mTime.getText().toString(), 
				mLocation.getText().toString(), 
				mShareLocationTime.getText().toString(),
				mParticipants
				);

		/* checking if the input is valid according to decided rules */
		if (FormsSupport.createMeetingInputValidation(cmData)) {

			/* checking if the sending of the meeting data to the server SUCCEEDED */
			if (FormsSupport.uploadCreatingMeetingData(cmData, new Metadata(new Participant("")))) {

			} else {
				/* sending of the meeting data to the server FAILED */

			}
		} else {
			/* input is invalid*/


		}

	}

	public void setParticipants(View view) {
		//		Intent intent = new Intent(this, .class);
		//		startActivityForResult(intent, meetingLocationRQ);
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
		getMenuInflater().inflate(R.menu.activity_meeting_creation, menu);
		return true;
	}

}
