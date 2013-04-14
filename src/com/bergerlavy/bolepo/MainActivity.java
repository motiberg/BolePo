package com.bergerlavy.bolepo;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.forms.MeetingManagementActivity;
import com.bergerlavy.bolepo.forms.RefreshMeetingsListListener;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends ListActivity implements RefreshMeetingsListListener {

	private static final String NOT_REGISTERED = "not registered";
	public static final String GENERAL_PREFERENCES = "GENERAL_PREFERENCES";
	public static final String DEVICE_USER_NAME = "DEVICE_USER_NAME";
	public static final String INITIALIZED = "INITIALIZED";
	public static final String GCM_REGISTRATION_ID = "GCM_REGISTRATION_ID";
	private DbHelper mDbHelper;
	private AcceptedMeetingsAdapter mAdapter;
	private SharedPreferences mGeneralPrefs;

	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_MEETING_CREATION = "EXTRA_MEETING_CREATION";
	public static final String EXTRA_MEETING_MODIFYING = "EXTRA_MEETING_MODIFYING";
	public static final String EXTRA_MEETING_CREATOR_REMOVAL = "EXTRA_MEETING_CREATOR_REMOVAL";
	
	private ShareLocationsService mShareLocationsService;
	//	private boolean mShareLocationsBounded;

	//	/** Defines callbacks for service binding, passed to bindService() */
	//    private ServiceConnection mShareLocationsServiceConnection = new ServiceConnection() {
	//
	//        @Override
	//        public void onServiceConnected(ComponentName className,
	//                IBinder service) {
	//            // We've bound to LocalService, cast the IBinder and get LocalService instance
	//        	ShareLocationsBinder binder = (ShareLocationsBinder) service;
	//        	mShareLocationsService = binder.getService();
	//            mShareLocationsBounded = true;
	//        }
	//
	//        @Override
	//        public void onServiceDisconnected(ComponentName arg0) {
	//        	mShareLocationsBounded = false;
	//        }
	//    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DAL.setContext(this);
		SDAL.setContext(this);

		mGeneralPrefs = getSharedPreferences(GENERAL_PREFERENCES, MODE_PRIVATE);

		if (mGeneralPrefs.getString(DEVICE_USER_NAME, "").equals("")) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.NEW_MEETING_NOTIFICATION_ID);
	}



	@Override
	protected void onResume() {
		super.onResume();

		if (!mGeneralPrefs.getString(DEVICE_USER_NAME, "").equals("")) {
//			if (!mGeneralPrefs.getBoolean(INITIALIZED, false)) 
				firstInit();
			mAdapter.changeCursor(createAcceptedMeetingsCursor());
		}
	}

	private void firstInit() {
		mDbHelper = new DbHelper(this);

		mAdapter = new AcceptedMeetingsAdapter(this, createAcceptedMeetingsCursor());
		setListAdapter(mAdapter);
		registerForContextMenu(getListView());

		MeetingManagementActivity.registerForMeetingsUpdate(this);

		startService(new Intent(this, ShareLocationsService.class));
		
//		SharedPreferences.Editor editor = mGeneralPrefs.edit();
//		editor.putBoolean(INITIALIZED, true);
//		editor.commit();
	}

	private Cursor createAcceptedMeetingsCursor() {
		SQLiteDatabase readableDB = mDbHelper.getReadableDatabase();
		Cursor meetings = null;
		Cursor c = readableDB.query(DbContract.Participants.TABLE_NAME,
				new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID }, 
				DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME + " = '" + mGeneralPrefs.getString(DEVICE_USER_NAME, "") + "'",
				null, null, null, null);
		String selectionStr = "";
		if (c != null && c.moveToFirst()) {
			while (!c.isAfterLast()) {
				selectionStr += DbContract.Meetings._ID + " = " + c.getInt(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID)) + " or ";
				c.moveToNext();
			}

			selectionStr = selectionStr.substring(0, selectionStr.length() - 4);
			meetings = readableDB.query(DbContract.Meetings.TABLE_NAME, null, null, null, null, null, null);
			if (meetings != null)
				meetings.moveToFirst();
		}
		c.close();
		readableDB.close();
		return meetings;
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
		case R.id.accepted_meetings_cm_item_edit:
			intent = new Intent(this, MeetingManagementActivity.class);
			intent.putExtra(EXTRA_MEETING_MODIFYING, true);
			intent.putExtra(EXTRA_MEETING_ID, info.id);
			break;
		case R.id.accepted_meetings_cm_item_delete:
			if (DAL.amIMeetingCreator(info.id)) {
				
			}
		}
		startActivity(intent);
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.accepted_meetings_context_menu, menu);
	}

	@Override
	public void onUpdate() {
		mAdapter.changeCursor(createAcceptedMeetingsCursor());
	}




}
