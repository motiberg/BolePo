package com.bergerlavy.bolepo.forms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoConstants;
import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.DatePickerActivity;
import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.TimePickerActivity;
import com.bergerlavy.bolepo.dals.Action;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.MeetingsDbAdapter;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRMeetingCreation;
import com.bergerlavy.bolepo.dals.SRMeetingModification;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.bolepo.maps.MeetingLocationSelectionActivity;
import com.bergerlavy.bolepo.shareddata.Misc;


public class MeetingManagementActivity extends Activity {

	private MeetingsDbAdapter mDbAdapter;

	private EditText mName;
	private TextView mDate;
	private TextView mTime;
	private TextView mLocation;
	private TextView mShareLocationTime;
	private AutoCompleteTextView mParticipantsSearch;
	private ArrayList<BolePoContact> mParticipants;
	private Button mCommitActionButton;
	private ListView mParticipantsListView;


	private BolePoContactsAdapter mParticipantsListAdapter;
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

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	private static final int RQ_MEETING_LOCATION = 1;
	private static final int RQ_MEETING_TIME = 2;
	private static final int RQ_MEETING_SHARETIME = 4;
	private static final int RQ_MEETING_DATE = 5;

	/***********************************************************/
	/************************* EXTRAS **************************/
	/***********************************************************/
	public static final String EXTRA_PARTICIPANTS = "EXTRA_PARTICIPANTS";

	/***********************************************************/
	/********************* SAVED INSTANCES *********************/
	/***********************************************************/
	public static final String SAVED_INSTANCE_PARTICIPANTS = "SAVED_INSTANCE_PARTICIPANTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_management);

		mDbAdapter = new MeetingsDbAdapter(this);
		mDbAdapter.open();

		if (savedInstanceState != null) {
			ArrayList<BolePoContact> participants = savedInstanceState.getParcelableArrayList(SAVED_INSTANCE_PARTICIPANTS);
			mParticipants = new ArrayList<BolePoContact>(participants);
			mParticipants.remove(removeSelfDeviceFromParticipants(mParticipants));
		}
		else {
			mParticipants = new ArrayList<BolePoContact>();
		}
		mName = (EditText) findViewById(R.id.meeting_management_purpose_edittext);
		mDate = (TextView) findViewById(R.id.meeting_management_date_edittext);
		mTime = (TextView) findViewById(R.id.meeting_management_time_edittext);
		mLocation = (TextView) findViewById(R.id.meeting_management_location_edittext);
		mShareLocationTime = (TextView) findViewById(R.id.meeting_management_share_locations_time_edittext);
		mCommitActionButton = (Button) findViewById(R.id.meeting_managment_commit_action_button);
		mParticipantsSearch = (AutoCompleteTextView) findViewById(R.id.meeting_management_contact_search);
		mParticipantsListView = (ListView) findViewById(R.id.meeting_management_participants_list);

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

		HashMap<Long, String> contacts = BolePoMisc.readContacts(this);
		List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

		for(Map.Entry<Long, String> entry : contacts.entrySet()){
			HashMap<String, String> hm = new HashMap<String,String>();
			String s = BolePoMisc.getBolePoContactName(this, entry.getKey());

			hm.put("name", s);
			hm.put("phone", entry.getValue());
			hm.put("image", Integer.toString(android.R.drawable.ic_dialog_email));
			hm.put("id", String.valueOf(entry.getKey()));
			aList.add(hm);
		}

		// Keys used in Hashmap
		String[] from = { "name", "phone", "image"};

		// Ids of views in listview_layout
		int[] to = { R.id.name, R.id.phone, R.id.picture};

		// Instantiating an adapter to store each items
		// R.layout.listview_layout defines the layout of each item
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.item_bolepo_contact, from, to);

		mParticipantsSearch.setAdapter(adapter);        

		mParticipantsSearch.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> hm = (HashMap<String, String>) parent.getAdapter().getItem(position);
				BolePoContact newContact = new BolePoContact.Builder(hm.get("name"), hm.get("phone")).setId(Long.parseLong(hm.get("id"))).build();
				mParticipants.add(newContact);
				mParticipantsListAdapter.add(newContact);
				mParticipantsListAdapter.notifyDataSetChanged();
				adjustParticipantsListViewHeight();
				mParticipantsSearch.setText("");
			}

		});

		mParticipantsListAdapter = new BolePoContactsAdapter(this, R.layout.item_bolepo_contact, mParticipants);
		mParticipantsListView.setAdapter(mParticipantsListAdapter);


		//		mParticipantsListView.setOnTouchListener(new OnTouchListener() {
		//
		//			@Override
		//			public boolean onTouch(View v, MotionEvent event) {
		//				if (event.getAction() == MotionEvent.ACTION_MOVE) {
		//					return true;
		//				}
		//				return false;
		//			}
		//		});

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

			Meeting meeting = mDbAdapter.getMeetingById(mModifiedMeetingID);
			mName.setText(meeting.getName());
			mDate.setText(meeting.getDate());
			mTime.setText(meeting.getTime());
			mLocation.setText(meeting.getLocation());
			mShareLocationTime.setText(meeting.getShareLocationTime());
			mModifiedMeetingHash = meeting.getHash();

			mParticipants = mDbAdapter.getParticipantsAsBolePoContacts(mModifiedMeetingID);
		}
		if (mAction == Action.CREATE) {

			/* setting the primary button to have a title that matches the creation mode */
			mCommitActionButton.setText(getResources().getString(R.string.meeting_managment_create_button_text));

			/* adding the meeting's creator to the participants list */
			BolePoContact newContact = BolePoMisc.getDeviceUserAsBolePoContact(this);
			mParticipants.add(newContact);

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
			mDate.setText(dateFormat.format(new Date()));

			mShareLocationTime.setText("01:00");
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
		List<String> participantsPhones = new ArrayList<String>();
		for (BolePoContact contact : mParticipants) {
			participantsPhones.add(contact.getPhone());
		}
		final Meeting meeting = new Meeting(
				mName.getText().toString(), 
				mDate.getText().toString(), 
				mTime.getText().toString(), 
				BolePoMisc.getDevicePhoneNumber(this),
				//				mLocation.getText().toString(),
				"dfdfdf",
				mShareLocationTime.getText().toString(),
				participantsPhones
				);

		/* checking if the input is valid according to decided rules */
		InputValidationReport report = null;
		if (mAction == Action.CREATE)
			report = BolePoMisc.createMeetingInputValidation(this, meeting);
		else if (mAction == Action.MODIFY)
			report = BolePoMisc.modifyMeetingInputValidation(this, meeting, mModifiedMeetingID);
		if (report.isOK()) {


			new ManageMeetingOnServer().execute(meeting);


			//			Intent intent = new Intent(this, ShareLocationsService.class);
			//			stopService(intent);
			//			startService(intent);
			finish();



		} else {
			/* input is invalid*/
			Toast.makeText(this, report.getError(), Toast.LENGTH_LONG).show();

		}

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

	private void adjustParticipantsListViewHeight() {
		int totalHeight = 0;
		for (int size = 0; size < mParticipantsListAdapter.getCount(); size++) {
			View listItem = mParticipantsListAdapter.getView(size, null, mParticipantsListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		//setting listview item in adapter
		ViewGroup.LayoutParams params = mParticipantsListView.getLayoutParams();
		params.height = totalHeight + (mParticipantsListView.getDividerHeight() * (mParticipantsListAdapter.getCount() - 1));
		mParticipantsListView.setLayoutParams(params);
	}

	public class ManageMeetingOnServer extends AsyncTask<Meeting, Void, Boolean> {

		ServerResponse servResp = null;
		SRMeetingCreation servRespCreation = null;
		SRMeetingModification servRespModification = null;

		@Override
		protected Boolean doInBackground(Meeting... params) {
			boolean shouldUpdate = false;
			Meeting meeting = params[0];
			if (mAction == Action.CREATE) {
				try {
					servResp = SDAL.createMeeting(meeting);
				}
				catch (ClassCastException e) {
					Intent intent = new Intent(MeetingManagementActivity.this, ErrorDialogActivity.class);
					intent.putExtra(ErrorDialogActivity.EXTRA_ERROR_MESSAGE, getResources().getString(R.string.server_error_create_meeting));
					startActivity(intent);
					return shouldUpdate;
				}
			}
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
					if (mDbAdapter.createMeeting(meeting, servRespCreation))
						shouldUpdate = true;
				}
				if (mAction == Action.MODIFY) {
					if (servResp instanceof SRMeetingModification)
						servRespModification = (SRMeetingModification) servResp;
					else throw new ClassCastException();
					if (mDbAdapter.editMeeting(mModifiedMeetingID, meeting, servRespModification)) {
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
			if (result) {
				Intent intent = new Intent();
				intent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
				sendBroadcast(intent);
			}
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(SAVED_INSTANCE_PARTICIPANTS, mParticipants);
		super.onSaveInstanceState(outState);
	}	
	
	private BolePoContact removeSelfDeviceFromParticipants(ArrayList<BolePoContact> participants) {
		for (BolePoContact contact : participants) {
			if (contact.getPhone().equals(BolePoMisc.getDeviceUserAsBolePoContact(this).getPhone())) {
				return contact;
			}
		}
		return null;
	}

}
