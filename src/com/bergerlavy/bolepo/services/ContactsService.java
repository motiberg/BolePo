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
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRGcmRegistrationCheck;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.bolepo.forms.BolePoContact;

public class ContactsService extends IntentService {

	private HashMap<String, BolePoContact> mAllContacts;

	public ContactsService() {
		super("ContactsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mAllContacts = new HashMap<String, BolePoContact>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
							null, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneNo = BolePoMisc.chopeNonDigitsFromPhoneNumber(phoneNo);
						if (!mAllContacts.containsKey(phoneNo)) {
							mAllContacts.put(phoneNo, new BolePoContact.Builder(name, phoneNo).build());
						}
					}
					pCur.close();
				}
			}
		}
		cur.close();

		/* requesting the server to check if the contacts are registered to the application */
		ServerResponse servResp = SDAL.checkForGcmRegistration(mAllContacts.keySet());
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
				BolePoContact contact = mAllContacts.get(phone);
				fos.write(contact.getName().getBytes());
				fos.write(new byte[] { '#', '@', '%' });
				fos.write(contact.getPhone().getBytes());
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
