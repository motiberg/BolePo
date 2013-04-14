package com.bergerlavy.bolepo.forms;

public class BolePoContact {

	private String mName;
	private String mPhone;
	private boolean mSelected;
	
	public BolePoContact(String name, String phone) {
		mName = name;
		mPhone = phone;
		mSelected = false;
	}
	
	public BolePoContact(String name, String phone, boolean selected) {
		mName = name;
		mPhone = phone;
		mSelected = selected;
	}

	public String getName() {
		return mName;
	}

	public String getPhone() {
		return mPhone;
	}
	
	public void select() {
		mSelected = true;
	}
	
	public void unselect() {
		mSelected = false;
	}
	
	public boolean isSelected() {
		return mSelected;
	}
}
