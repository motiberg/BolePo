package com.bergerlavy.bolepo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.bergerlavy.bolepo.BolePoConstants.Credentials;
import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.forms.MeetingManagementActivity;
import com.bergerlavy.bolepo.forms.RefreshMeetingsListListener;
import com.bergerlavy.bolepo.forms.RemoveMeetingActivity;
import com.bergerlavy.bolepo.services.ContactsService;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements RefreshMeetingsListListener {

	private ListView mAcceptedList;
	private ListView mNotApprovedYetList;

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
		
		/* starting the contacts service to check which of the contacts stored in this device 
		 * are registered to the application */
		Intent contactsServiceIntent = new Intent(this, ContactsService.class);
		startService(contactsServiceIntent);

		mAcceptedList = (ListView) findViewById(R.id.main_accepted_list);
		mNotApprovedYetList = (ListView) findViewById(R.id.main_not_approved_yet_list);

		/* checking if it is the first time the application starts by checking if the user entered 
		 * the device phone number */
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
			mAcceptedAdapter.changeCursor(DAL.createAcceptedMeetingsCursor());
			mNotApprovedYetAdapter.changeCursor(DAL.createWaitingForApprovalMeetingsCursor());

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("com.bergerlavy.bolepo.refresh");
			MyBroadcastReceiver receiver = new MyBroadcastReceiver();
			registerReceiver(receiver, intentFilter); 
		}
	}

	private void firstInit() {

		mAcceptedAdapter = new AcceptedMeetingsAdapter(this, DAL.createAcceptedMeetingsCursor());
		mAcceptedList.setAdapter(mAcceptedAdapter);
		registerForContextMenu(mAcceptedList);

		mNotApprovedYetAdapter = new AcceptedMeetingsAdapter(this, DAL.createWaitingForApprovalMeetingsCursor());
		mNotApprovedYetList.setAdapter(mNotApprovedYetAdapter);
		registerForContextMenu(mNotApprovedYetList);

		MeetingManagementActivity.registerForMeetingsUpdate(this);
		RemoveMeetingActivity.registerForMeetingsUpdate(this);

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
	public void onUpdate() {
		mAcceptedAdapter.changeCursor(DAL.createAcceptedMeetingsCursor());
		mNotApprovedYetAdapter.changeCursor(DAL.createWaitingForApprovalMeetingsCursor());
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			onUpdate();
		}
	} 


}
