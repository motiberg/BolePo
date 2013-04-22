package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.forms.BolePoContactsAdapter.BolePoContactsComparator;

public class BolePoContactsActivity extends ListActivity {

	private int mContactsCount;
	private TextView mNumberOfContacts;
	private BolePoContactsAdapter mArrayAdapter;
	private List<BolePoContact> mAllContacts;
	//	private List<Integer> mSelectedContacts;
	private List<String> mSelectedContacts;

	public static final String EXTRA_PHONE_NUMBERS = "EXTRA_PHONE_NUMBERS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bole_po_contacts);

		mNumberOfContacts = (TextView) findViewById(R.id.bolepo_contacts_number_of_contacts);
		mAllContacts = new LinkedList<BolePoContact>();
		mSelectedContacts = new ArrayList<String>();
		ContentResolver cr = getContentResolver();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(AddParticipantsActivity.EXTRA_PARTICIPANTS)) {
				String ps[] = bundle.getStringArray(AddParticipantsActivity.EXTRA_PARTICIPANTS);
				if (ps != null)
					for (String s : ps) {
						mSelectedContacts.add(s);
					}
			}
		}
		if (mSelectedContacts != null) {
			mContactsCount = mSelectedContacts.size();
		}
		else {
			mSelectedContacts = new ArrayList<String>();
			mContactsCount = 0;
		}
		mNumberOfContacts.setText(mContactsCount + "");

		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer.parseInt(cur.getString(
						cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
							new String[]{id}, null);
					while (pCur.moveToNext()) {
						//TODO check if this number s register to BolePo. only if it does, add it.
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						if (!isAlreadyAdded(phoneNo)) {
							if (isContactSelected(phoneNo))
								mAllContacts.add(new BolePoContact(name, phoneNo, true));
							else mAllContacts.add(new BolePoContact(name, phoneNo));
						}
					}
					pCur.close();
				}
			}
		}
		cur.close();
		mArrayAdapter = new BolePoContactsAdapter(this, R.layout.item_bolepo_contact, mAllContacts);
		mArrayAdapter.sort(new BolePoContactsAdapter.BolePoContactsComparator());
		getListView().setAdapter(mArrayAdapter);
	}

	/**
	 * Checks if the input phone number is one of the phone numbers that received from the calling activity.
	 * If it does, that means that this phone number belongs to a contact that is already invited to the meeting.
	 * Further more, that means that the calling activity is in MODIFY mode.
	 * @param phoneNumber phone number to check for existence in the already invited contacts phone numbers.
	 * @return <ul><li><code>true</code> if the contact that phoneNumber is his phone number is already invited, <code>false</code> otherwise.</li></ul>
	 */
	private boolean isContactSelected(String phoneNumber) {
		for (String s : mSelectedContacts) {
			if (phoneNumber.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	private boolean isAlreadyAdded(String phoneNumber) {
		for (BolePoContact s : mAllContacts) {
			if (phoneNumber.equalsIgnoreCase(s.getPhone()))
				return true;
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView phone = (TextView) v.findViewById(R.id.item_bolepo_contact_phone);
		Toast.makeText(this, "position = " + position, Toast.LENGTH_LONG).show();
		
		if (!mSelectedContacts.contains(phone.getText().toString())) {
			mSelectedContacts.add(phone.getText().toString());
			mAllContacts.get(position).select();
			mContactsCount++;
		}
		else {
			mSelectedContacts.remove(phone.getText().toString());
			mAllContacts.get(position).unselect();
			mContactsCount--;
		}

		/* displaying the updated number of contacts selected */
		mNumberOfContacts.setText(mContactsCount + "");

		/* refreshing the list so the chosen contacts will appear first */
		mArrayAdapter.notifyDataSetChanged();
		Collections.sort(mAllContacts, new BolePoContactsComparator());
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void finish() {
		int i = 0;
		Intent data = new Intent();
		String[] phones = new String[mSelectedContacts.size()];
		for (String phone : mSelectedContacts) {
			phones[i++] = phone;
		}
		data.putExtra(EXTRA_PHONE_NUMBERS, phones);
		setResult(RESULT_OK, data); 

		super.finish();
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bole_po_contacts, menu);
		return true;
	}

}
