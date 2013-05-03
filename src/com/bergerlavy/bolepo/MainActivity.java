package com.bergerlavy.bolepo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.bergerlavy.bolepo.dals.Credentials;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.RSVP;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.forms.MeetingManagementActivity;
import com.bergerlavy.bolepo.forms.RefreshMeetingsListListener;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements RefreshMeetingsListListener {

	private ListView mAcceptedList;
	private ListView mNotApprovedYetList;

	private DbHelper mDbHelper;
	private AcceptedMeetingsAdapter mAcceptedAdapter;
	//TODO change the type to NotApprovedYetMeetingsAdapter
	private AcceptedMeetingsAdapter mNotApprovedYetAdapter;

	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_MEETING_CREATION = "EXTRA_MEETING_CREATION";
	public static final String EXTRA_MEETING_MODIFYING = "EXTRA_MEETING_MODIFYING";
	public static final String EXTRA_MEETING_CREATOR_REMOVAL = "EXTRA_MEETING_CREATOR_REMOVAL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DAL.setContext(this);
		SDAL.setContext(this);

		mAcceptedList = (ListView) findViewById(R.id.main_accepted_list);
		mNotApprovedYetList = (ListView) findViewById(R.id.main_not_approved_yet_list);

		if (BolePoMisc.getDevicePhoneNumber(this).equals("")) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.NEW_MEETING_NOTIFICATION_ID);
	}



	@Override
	protected void onResume() {
		super.onResume();

		if (!BolePoMisc.getDevicePhoneNumber(this).equals("")) {
			firstInit();
			mAcceptedAdapter.changeCursor(createAcceptedMeetingsCursor());
			mNotApprovedYetAdapter.changeCursor(createWaitingForApprovalMeetingsCursor());

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("com.bergerlavy.bolepo.refresh");
			MyBroadcastReceiver receiver = new MyBroadcastReceiver();
			registerReceiver(receiver, intentFilter); 
		}
	}

	private void firstInit() {
		mDbHelper = new DbHelper(this);

		mAcceptedAdapter = new AcceptedMeetingsAdapter(this, createAcceptedMeetingsCursor());
		mAcceptedList.setAdapter(mAcceptedAdapter);
		registerForContextMenu(mAcceptedList);

		mNotApprovedYetAdapter = new AcceptedMeetingsAdapter(this, createWaitingForApprovalMeetingsCursor());
		mNotApprovedYetList.setAdapter(mNotApprovedYetAdapter);
		registerForContextMenu(mNotApprovedYetList);

		MeetingManagementActivity.registerForMeetingsUpdate(this);
		RemoveMeetingActivity.registerForMeetingsUpdate(this);

		startService(new Intent(this, ShareLocationsService.class));
	}

	private Cursor createAcceptedMeetingsCursor() {
		SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
		Cursor acceptedMeetingsCursor = null;
		/* getting all the meetings IDs that the user of this device accepted (created meetings are automatically marked as accepted) */
		Cursor c = readableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID }, 
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(this) + "' and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP + " = '" + RSVP.YES.getRsvp() + "'",
						null, null, null, null);
		String selectionStr = "";
		if (c != null) {
			if (c.moveToFirst()) 
				while (!c.isAfterLast()) {
					selectionStr += DbContract.Meetings._ID + " = " + c.getInt(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID)) + " or ";
					c.moveToNext();
				}
			c.close();
		}
		/* removing the last ' or ' */
		if (selectionStr.length() > 4)
			selectionStr = selectionStr.substring(0, selectionStr.length() - 4);

		/* getting all the meetings records that the user of this device accepted */
		acceptedMeetingsCursor = readableDb.query(DbContract.Meetings.TABLE_NAME, null, selectionStr, null, null, null, null);
		if (acceptedMeetingsCursor != null)
			acceptedMeetingsCursor.moveToFirst();

		readableDb.close();
		return acceptedMeetingsCursor;
	}

	private Cursor createWaitingForApprovalMeetingsCursor() {
		SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
		Cursor notApprovedMeetingsCursor = null;
		Cursor c = readableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(this) + "' and " +
						DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP + " = '" + RSVP.MAYBE.getRsvp() + "'",
						null, null, null, null);
		String selectionStr = "";
		if (c != null) {
			if (c.moveToFirst())
				while (!c.isAfterLast()) {
					selectionStr += DbContract.Meetings._ID + " = " + c.getInt(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID)) + " or ";
					c.moveToNext();
				}
			c.close();
		}
		/* removing the last ' or ' */
		if (selectionStr.length() > 4)
			selectionStr = selectionStr.substring(0, selectionStr.length() - 4);

		/* getting all the meetings records that the user of this device accepted */
		notApprovedMeetingsCursor = readableDb.query(DbContract.Meetings.TABLE_NAME, null, selectionStr, null, null, null, null);
		if (notApprovedMeetingsCursor != null)
			notApprovedMeetingsCursor.moveToFirst();

		readableDb.close();
		return notApprovedMeetingsCursor;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.main_menu_create:
			intent = new Intent(this, MeetingManagementActivity.class);
			intent.putExtra(EXTRA_MEETING_CREATION, true);
			startActivity(intent);
			break;
		case R.id.main_menu_gps:
			intent = new Intent(this, GPSActivity.class);
			startActivity(intent);
			break;
		case R.id.main_menu_register:
			/* check if the device is already registered */
			final String regId = GCMRegistrar.getRegistrationId(this);

			/* turns out the device isn't registered yet */
			if (regId.equals("")) {

				/* register the device, passing the SENDER_ID as received when signed up for GCM */
				GCMRegistrar.register(this, BolePoConstants.SENDER_ID);

			} else {
				Toast.makeText(this, "Already Registered !!!", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_menu_unregister:
			/* check if the device is already registered */
			final String regiId = GCMRegistrar.getRegistrationId(this);

			/* turns out the device isn't registered yet */
			if (!regiId.equals("")) {

				/* register the device, passing the SENDER_ID as received when signed up for GCM */
				//				GCMRegistrar.register(this, BolePoConstants.SENDER_ID);
				GCMRegistrar.unregister(this);

			} else {
				Toast.makeText(this, "Not Registered !!!", Toast.LENGTH_LONG).show();
			}
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.accepted_meetings_root_participant_cm_item_view:
		case R.id.accepted_meetings_regular_participant_cm_item_view:
			intent = new Intent(this, MeetingDataActivity.class);
			intent.putExtra(EXTRA_MEETING_ID, info.id);
			break;
		case R.id.accepted_meetings_root_participant_cm_item_edit:
			intent = new Intent(this, MeetingManagementActivity.class);
			intent.putExtra(EXTRA_MEETING_MODIFYING, true);
			intent.putExtra(EXTRA_MEETING_ID, info.id);
			break;
		case R.id.accepted_meetings_root_participant_cm_item_delete:
		case R.id.accepted_meetings_regular_participant_cm_item_delete:
			intent = new Intent(this, RemoveMeetingActivity.class);
			intent.putExtra(EXTRA_MEETING_ID, info.id);
			if (DAL.amIMeetingCreator(info.id))
				intent.putExtra(EXTRA_MEETING_CREATOR_REMOVAL, true);
			else intent.putExtra(EXTRA_MEETING_CREATOR_REMOVAL, false);
			break;
		}
		startActivity(intent);
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
		Cursor c = readableDb.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS },
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + info.id + " and " + DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " = '" + BolePoMisc.getDevicePhoneNumber(this) + "'",
				null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				if (Credentials.ROOT.getCredentials().equalsIgnoreCase(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS)))) {
					inflater.inflate(R.menu.accepted_meetings_root_participant_context_menu, menu);
				}
				else {
					inflater.inflate(R.menu.accepted_meetings_regular_participant_context_menu, menu);
				}
			}
		}
		readableDb.close();

	}

	@Override
	public void onUpdate() {
		mAcceptedAdapter.changeCursor(createAcceptedMeetingsCursor());
		mNotApprovedYetAdapter.changeCursor(createWaitingForApprovalMeetingsCursor());
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			onUpdate();
		}
	} 


}
