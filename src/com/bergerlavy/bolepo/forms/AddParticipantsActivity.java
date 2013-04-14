package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.bergerlavy.bolepo.R;

public class AddParticipantsActivity extends ListActivity {

	List<String> mInvitedParticipants;
	ArrayAdapter<String> mAdapter;

	/***********************************************************/
	/********************** REQUEST CODES **********************/
	/***********************************************************/
	private static final int RQ_PARTICIPATIONS_MODIFICATION = 1;
	
	/***********************************************************/
	/************************* EXTRAS **************************/
	/***********************************************************/
	public static final String EXTRA_PARTICIPANTS = "EXTRA_PARTICIPANTS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_participants);

		mInvitedParticipants = new ArrayList<String>();

		Bundle bundle = getIntent().getExtras();
		
		/* bundle isn't null when there already invited participants to the modified meeting */
		if (bundle != null) {
			if (bundle.containsKey(MeetingManagementActivity.EXTRA_PARTICIPANTS)) {
				String[] participants = bundle.getStringArray(MeetingManagementActivity.EXTRA_PARTICIPANTS);
				for (String s : participants)
					mInvitedParticipants.add(s);
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
		int i = 0;
		Intent data = new Intent();
		String[] phones = new String[mInvitedParticipants.size()];
		for (String phone : mInvitedParticipants) {
			phones[i++] = phone;
		}
		data.putExtra(EXTRA_PARTICIPANTS, phones);
		setResult(RESULT_OK, data); 

		super.finish();
	}

}
