package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bergerlavy.bolepo.R;
import com.bergerlavy.db.DbContract;
import com.bergerlavy.db.DbHelper;

public class AddParticipantsActivity extends ListActivity {

	private List<String> mInvitedParticipants;
	private DbHelper mDbHelper;
	private ArrayAdapter<String> mAdapter;
	private boolean mRemoveMeetingChooseContactToManage;
	private boolean mIsContactChosenToManage;

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	private static final int RQ_PARTICIPATIONS_MODIFICATION = 1;

	/***********************************************************/
	/************************* EXTRAS **************************/
	/***********************************************************/
	public static final String EXTRA_PARTICIPANTS = "EXTRA_PARTICIPANTS";
	public static final String EXTRA_CONTACT_TO_MANAGE = "EXTRA_CONTACT_TO_MANAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_participants);

		mInvitedParticipants = new ArrayList<String>();
		
		long meetingId;
		Bundle bundle = getIntent().getExtras();

		/* bundle isn't null when there already invited participants to the modified meeting */
		if (bundle != null) {
			if (bundle.containsKey(MeetingManagementActivity.EXTRA_PARTICIPANTS)) {
				String[] participants = bundle.getStringArray(MeetingManagementActivity.EXTRA_PARTICIPANTS);
				for (String s : participants)
					mInvitedParticipants.add(s);
			}
			if (bundle.containsKey(RemoveMeetingActivity.EXTRA_REMOVE_MEETING_CHOOSE_CONTACT)) {
				mRemoveMeetingChooseContactToManage = true;
				if (bundle.containsKey(RemoveMeetingActivity.EXTRA_REMOVE_MEETING_MEETING_ID)) {
					String manager = null;
					meetingId = bundle.getLong(RemoveMeetingActivity.EXTRA_REMOVE_MEETING_MEETING_ID);
					mDbHelper = new DbHelper(this);
					SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
					Cursor c = readableDb.query(DbContract.Meetings.TABLE_NAME,
							new String[] { DbContract.Meetings._ID, DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER },
							DbContract.Meetings._ID + " = " + meetingId,
							null, null, null, null);
					if (c != null) {
						if (c.moveToFirst())
							manager = c.getString(c.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER));
						c.close();
						c = readableDb.query(DbContract.Participants.TABLE_NAME,
								new String[] { DbContract.Participants._ID, DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE },
								DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " = " + meetingId + " and " + DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " != " + manager,
								null, null, null, null);
						if (c != null) {
							if (c.moveToFirst()) {
								for ( ; !c.isAfterLast() ; c.moveToNext())
									mInvitedParticipants.add(c.getString(c.getColumnIndex(DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE)));
							}
							c.close();
						}
					}
					readableDb.close();
				}
			}
		}

		mAdapter = new ArrayAdapter<String>(this, R.layout.item_add_participants, R.id.item_add_participants_participant_name, mInvitedParticipants);
		setListAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_participants, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView phone = (TextView) v.findViewById(R.id.item_add_participants_participant_name);

		/* if this activity was called from the RemoveMeetingActivity in order to select a participant to become the manager of
		 * the meeting in case the creator of the meeting doesn't want to participant anymore in the meeting but doesn't want 
		 * to cancel it for the other participants */
		if (mRemoveMeetingChooseContactToManage) {
			mIsContactChosenToManage = true;
			Intent intent = new Intent();
			intent.putExtra(EXTRA_CONTACT_TO_MANAGE, phone.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.add_participants_invite_item:
			intent = new Intent(this, BolePoContactsActivity.class);
			String arr[] = new String[mInvitedParticipants.size()];
			int count = 0;
			for (String s : mInvitedParticipants)
				arr[count++] = s;
			intent.putExtra(EXTRA_PARTICIPANTS, arr);
			startActivityForResult(intent, RQ_PARTICIPATIONS_MODIFICATION);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RQ_PARTICIPATIONS_MODIFICATION) {
				if (data.hasExtra(BolePoContactsActivity.EXTRA_PHONE_NUMBERS)) {
					String[] updatedParticipants = data.getStringArrayExtra(BolePoContactsActivity.EXTRA_PHONE_NUMBERS);
					if (updatedParticipants != null) {
						mInvitedParticipants.clear();
						for (String s : updatedParticipants)
							mInvitedParticipants.add(s);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		if (mRemoveMeetingChooseContactToManage && !mIsContactChosenToManage)
			setResult(RESULT_CANCELED);
		else if (!mRemoveMeetingChooseContactToManage) {
			int i = 0;
			Intent data = new Intent();
			String[] phones = new String[mInvitedParticipants.size()];
			for (String phone : mInvitedParticipants) {
				phones[i++] = phone;
			}
			data.putExtra(EXTRA_PARTICIPANTS, phones);
			setResult(RESULT_OK, data); 
		}
		super.finish();
	}

}
