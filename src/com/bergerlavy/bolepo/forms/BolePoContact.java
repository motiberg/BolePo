package com.bergerlavy.bolepo.forms;

public class BolePoContact {

	private String mName;
	private String mPhone;
	private boolean mSelected;
	
	private BolePoContact(Builder builder) {
		mName = builder.mName;
		mPhone = builder.mPhone;
		mSelected = builder.mSelected;
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
	
	public static class Builder {
		/* required */
		private final String mName;
		private final String mPhone;
		
		/* optional */
		private boolean mSelected;
		
		public Builder(String name, String phone) {
			mName = name;
			mPhone = phone;
		}
		
		public Builder select() {
			mSelected = true;
			return this;
		}
		
		public BolePoContact build() {
			return new BolePoContact(this);
		}
	}
}
