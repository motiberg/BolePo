package com.bergerlavy.bolepo.forms;

public class BolePoContact {

	private String mName;
	private String mPhone;
	private long mId;
	
	private BolePoContact(Builder builder) {
		mName = builder.mName;
		mPhone = builder.mPhone;
		mId = builder.mId;
	}

	public String getName() {
		return mName;
	}

	public String getPhone() {
		return mPhone;
	}
	
	public long getId() {
		return mId;
	}
	
	public static class Builder {
		/* required */
		private final String mName;
		private final String mPhone;
		
		/* optional */
		private long mId;
		
		public Builder(String name, String phone) {
			mName = name;
			mPhone = phone;
		}
		
		public Builder setId(long id) {
			mId = id;
			return this;
		}
		
		public BolePoContact build() {
			return new BolePoContact(this);
		}
	}
}
