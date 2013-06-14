package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.R;

public class BolePoContactsAdapter extends ArrayAdapter<BolePoContact> implements Filterable {

	private Context mContext;
	private List<BolePoContact> mAllContacts;

	public BolePoContactsAdapter(Context context, int textViewResourceId,
			List<BolePoContact> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mAllContacts = new ArrayList<BolePoContact>();
		mAllContacts.addAll(objects);
	}



	@Override
	public void add(BolePoContact contact) {
		mAllContacts.add(contact);
		super.add(contact);
	}



	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_bolepo_contact, null);
		}

		BolePoContact contact = mAllContacts.get(position);
		if (contact != null) {
			TextView contactName = (TextView) v.findViewById(R.id.name);
			TextView contactPhone = (TextView) v.findViewById(R.id.phone);
			ImageView contactImage = (ImageView) v.findViewById(R.id.picture);

			contactName.setTextColor(mContext.getResources().getColor(android.R.color.white));
			contactPhone.setTextColor(mContext.getResources().getColor(android.R.color.white));

			contactName.setText(contact.getName());
			contactPhone.setText(contact.getPhone());
			Bitmap b = BolePoMisc.getBolePoContactPhoto(mContext, contact.getId());
			
			if (b != null) {
				Bitmap c = Bitmap.createScaledBitmap(b, 192, 192, true);
				contactImage.setImageBitmap(c);
			}
			else {
				contactImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_contact_picture));
			}
		}
		return v;

	}



	@Override
	public int getCount() {
		if (mAllContacts != null) {
			return mAllContacts.size();
		}
		return 0;
	}

}
