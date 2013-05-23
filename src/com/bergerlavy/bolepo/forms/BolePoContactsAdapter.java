package com.bergerlavy.bolepo.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bergerlavy.bolepo.R;

public class BolePoContactsAdapter extends ArrayAdapter<BolePoContact> {

	private List<BolePoContact> mContacts;

	public BolePoContactsAdapter(Context context, int textViewResourceId,
			List<BolePoContact> objects) {
		super(context, textViewResourceId, objects);
		mContacts = new ArrayList<BolePoContact>();
		mContacts.addAll(objects);
	}

	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_bolepo_contact, null);
		}

		BolePoContact contact = mContacts.get(position);
		if (contact != null) {

			TextView contactName = (TextView) v.findViewById(R.id.item_bolepo_contact_name);
			TextView contactPhone = (TextView) v.findViewById(R.id.item_bolepo_contact_phone);

			if (contact.isSelected()) {
				contactName.setTextColor(Color.RED);
				contactPhone.setTextColor(Color.RED);
			}
			else {
				contactName.setTextColor(Color.BLACK);
				contactPhone.setTextColor(Color.BLACK);
			}
			
			if (contactPhone != null){
				contactPhone.setText(contact.getPhone());
			}

		}
		return v;

	}
	
	@Override
	public void notifyDataSetChanged() {
	    Collections.sort(mContacts, new BolePoContactsComparator());
	    super.notifyDataSetChanged();
	}
	
	public static class BolePoContactsComparator implements Comparator<BolePoContact> {

		@Override
		public int compare(BolePoContact lhs, BolePoContact rhs) {
			if (lhs.isSelected() && !rhs.isSelected())
				return -1;
			if (!lhs.isSelected() && rhs.isSelected())
				return 1;
			if (lhs.getName().equals(rhs.getName()))
				return lhs.getPhone().compareTo(rhs.getPhone());
			return lhs.getName().compareTo(rhs.getName());
		}
		
	}

}
