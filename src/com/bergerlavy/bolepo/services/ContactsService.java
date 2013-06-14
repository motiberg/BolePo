package com.bergerlavy.bolepo.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.bergerlavy.bolepo.BolePoConstants;
import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.NoInternetConnectionBolePoException;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRGcmRegistrationCheck;
import com.bergerlavy.bolepo.dals.ServerResponse;

public class ContactsService extends IntentService {

	private HashMap<String, Long> mAllContacts;

	public ContactsService() {
		super("ContactsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mAllContacts = new HashMap<String, Long>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				long id = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
							null, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneNo = BolePoMisc.chopeNonDigitsFromPhoneNumber(phoneNo);
						
						/* avoiding from adding the device phone to the list in case that the user stored 
						 * his own phone number as a contact */
						if (!phoneNo.equalsIgnoreCase(BolePoMisc.getDevicePhoneNumber(this))) {
							if (!mAllContacts.containsKey(phoneNo)) {
								mAllContacts.put(phoneNo, id);
							}
						}
					}
					pCur.close();
				}
			}
		}
		cur.close();

		/* requesting the server to check if the contacts are registered to the application */
		ServerResponse servResp = null;
		try {
			servResp = SDAL.checkForGcmRegistration(mAllContacts.keySet());
		} catch (NoInternetConnectionBolePoException e) {
			Intent notifyIntent = new Intent();
			notifyIntent.setAction(BolePoConstants.ACTION_BOLEPO_INFORM_NO_INTERNET_CONNECTION);
			sendBroadcast(notifyIntent);
			return;
		}
		List<String> registeredContactsPhones = new ArrayList<String>();
		if (servResp.isOK()) {
			if (servResp instanceof SRGcmRegistrationCheck) {
				SRGcmRegistrationCheck response = (SRGcmRegistrationCheck) servResp;

				/* getting a subset of contacts that are registered to the application */
				List<String> phones = response.getRegistersContactsPhones();
				for (String phone : phones)
					registeredContactsPhones.add(phone);
			}
		}

		/* writing the registered contacts to a file to be used in invitation of contacts to a meeting */
		FileOutputStream fos;
		try {
			fos = openFileOutput(BolePoConstants.CONTACTS_FILE_NAME, Context.MODE_PRIVATE);
			for (String phone : registeredContactsPhones) {
				Long contactId = mAllContacts.get(phone);
				fos.write(String.valueOf(contactId).getBytes());
				fos.write(new byte[] { '#', '@', '%' });
				fos.write(phone.getBytes());
				fos.write(new byte[] { '%', '@', '#' });
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
