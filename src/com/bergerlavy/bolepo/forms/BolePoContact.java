package com.bergerlavy.bolepo.forms;

import android.os.Parcel;
import android.os.Parcelable;

public class BolePoContact implements Parcelable {

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { mName, mPhone });
		dest.writeLong(mId);
	}

	public static final Parcelable.Creator<BolePoContact> CREATOR = new Parcelable.Creator<BolePoContact>() {
		public BolePoContact createFromParcel(Parcel in) {
			return new BolePoContact(in);
		}

		public BolePoContact[] newArray(int size) {
			return new BolePoContact[size];
		}
	};

	private BolePoContact(Parcel in) {
		String[] strData = new String[2];
		in.readStringArray(strData);
		mName = strData[0];
		mPhone = strData[1];
		mId = in.readLong();
	}
}
