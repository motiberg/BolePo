package com.bergerlavy.bolepo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoConstants.Credentials;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.forms.MeetingManagementActivity;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;
import com.bergerlavy.bolepo.services.ContactsService;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	private ListView mAcceptedList;
	private ListView mNotApprovedYetList;

	private AcceptedMeetingsAdapter mAcceptedAdapter;
	//TODO change the type to NotApprovedYetMeetingsAdapter
	private AcceptedMeetingsAdapter mNotApprovedYetAdapter;

	private refreshListsReceiver mRefreshListsReceiver;
	private internetConnectionLostBroadcastReceiver mInternetConnectionLostReceiver;


	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_MEETING_CREATION = "EXTRA_MEETING_CREATION";
	public static final String EXTRA_MEETING_MODIFYING = "EXTRA_MEETING_MODIFYING";
	public static final String EXTRA_MEETING_CREATOR_REMOVAL = "EXTRA_MEETING_CREATOR_REMOVAL";

	private static final int RQ_NETWORK_SETTINGS_DIALOG = 1;
	
	private OnItemClickListener MeetingDataListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Intent intent = new Intent(MainActivity.this, MeetingDataActivity.class);
			intent.putExtra(EXTRA_MEETING_ID, id);
			startActivity(intent);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAcceptedList = (ListView) findViewById(R.id.main_accepted_list);
		mNotApprovedYetList = (ListView) findViewById(R.id.main_not_approved_yet_list);
		
		/* checking if the device is connected to the internet */
		if (BolePoMisc.isDeviceOnline(this)) {

			/* starting the contacts service to check which of the contacts stored in this device 
			 * are registered to the application */
//			Intent contactsServiceIntent = new Intent(this, ContactsService.class);
//			startService(contactsServiceIntent);

			/* setting an onClick listener to the accepted meetings list to allow the user to watch the meeting's details */
			mAcceptedList.setOnItemClickListener(MeetingDataListener);

			/* setting an onClick listener to the waiting-for-approval meetings list to allow the user to watch the meeting's details */
			mNotApprovedYetList.setOnItemClickListener(MeetingDataListener);

			/* checking if it is the first time the application starts by checking if the user entered 
			 * the device phone number */
			if (BolePoMisc.getDevicePhoneNumber(this).equals("")) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.NEW_MEETING_NOTIFICATION_ID);
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.PARTICIPANT_ATTENDANCE_NOTIFICATION_ID);
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.PARTICIPANT_DECLINING_NOTIFICATION_ID);
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.PARTICIPANT_REMOVED_FROM_MEETING_NOTIFICATION_ID);
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.MEETING_CANCELLED_NOTIFICATION_ID);
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(GCMIntentService.NEW_MANAGER_NOTIFICATION_ID);
		}
		else notifyInternetConnectionLost();
	}
	
	private void notifyInternetConnectionLost() {
		startActivityForResult(new Intent(this, NetworkSettingsDialogActivity.class), RQ_NETWORK_SETTINGS_DIALOG);
	}


	@Override
	protected void onResume() {
		super.onResume();
		
		if (BolePoMisc.isDeviceOnline(this) && !BolePoMisc.getDevicePhoneNumber(this).equals("")) {
			firstInit();
			Cursor acceptedCursor = mAcceptedAdapter.swapCursor(DAL.createAcceptedMeetingsCursor());
			acceptedCursor.close();
			Cursor notApprovedCursor = mNotApprovedYetAdapter.swapCursor(DAL.createWaitingForApprovalMeetingsCursor());
			notApprovedCursor.close();

			/* defining the Broadcast Receiver for refreshing the lists */
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
			mRefreshListsReceiver = new refreshListsReceiver();
			registerReceiver(mRefreshListsReceiver, intentFilter); 
			
			/* defining the Broadcast Receiver for no internet connection notification */
			IntentFilter noInternetConnectionIntentFilter = new IntentFilter();
			noInternetConnectionIntentFilter.addAction(BolePoConstants.ACTION_BOLEPO_INFORM_NO_INTERNET_CONNECTION);
			mInternetConnectionLostReceiver = new internetConnectionLostBroadcastReceiver();
			registerReceiver(mInternetConnectionLostReceiver, noInternetConnectionIntentFilter);
			
		}
	}

	@Override
	protected void onPause() {
		if (mRefreshListsReceiver != null) {
			unregisterReceiver(mRefreshListsReceiver);
			mRefreshListsReceiver = null;
		}
		if (mInternetConnectionLostReceiver != null) {
			unregisterReceiver(mInternetConnectionLostReceiver);
			mInternetConnectionLostReceiver = null;
		}
		super.onPause();
	}

	@Override
	protected void onStart() {
		DAL.setContext(this);
		SDAL.setContext(this);
		super.onStart();
	}


	@Override
	protected void onDestroy() {
		DAL.close();
		super.onDestroy();
	}

	private void firstInit() {
		
		mAcceptedAdapter = new AcceptedMeetingsAdapter(this, DAL.createAcceptedMeetingsCursor());
		mAcceptedList.setAdapter(mAcceptedAdapter);
		registerForContextMenu(mAcceptedList);

		mNotApprovedYetAdapter = new AcceptedMeetingsAdapter(this, DAL.createWaitingForApprovalMeetingsCursor());
		mNotApprovedYetList.setAdapter(mNotApprovedYetAdapter);
		registerForContextMenu(mNotApprovedYetList);

		startService(new Intent(this, ShareLocationsService.class));
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

		if (DAL.getMyCredentials(info.id) == Credentials.MANAGER)
			inflater.inflate(R.menu.accepted_meetings_root_participant_context_menu, menu);	
		else inflater.inflate(R.menu.accepted_meetings_regular_participant_context_menu, menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RQ_NETWORK_SETTINGS_DIALOG) {
			if (resultCode == RESULT_CANCELED) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private class refreshListsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Cursor acceptedCursor = mAcceptedAdapter.swapCursor(DAL.createAcceptedMeetingsCursor());
			acceptedCursor.close();
			Cursor notApprovedCursor = mNotApprovedYetAdapter.swapCursor(DAL.createWaitingForApprovalMeetingsCursor());
			notApprovedCursor.close();
		}
	} 
	
	private class internetConnectionLostBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			notifyInternetConnectionLost();
		}
	}


}
