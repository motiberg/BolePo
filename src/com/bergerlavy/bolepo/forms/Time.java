package com.bergerlavy.bolepo.forms;

public class Time {

	private int mMinutes;
	private int mHours;

	public Time(String t) {
		if (t != null) {
			mHours = Integer.parseInt(t.substring(0, t.indexOf(':')));
			mMinutes = Integer.parseInt(t.substring(t.indexOf(':') + 1));
		}
	}

	public Time(int hours, int minutes) {
		mHours = hours;
		mMinutes = minutes;
	}

	public int getMinutes() {
		return mMinutes;
	}

	public int getHours() {
		return mHours;
	}

	public boolean isSmallerEqualTo(Time t) {
		if (mHours < t.getHours())
			return true;
		if (mHours > t.getHours())
			return false;
		if (mMinutes > t.getMinutes())
			return false;
		return true;
	}

	public static Time substract(Time t1, Time t2) {
		boolean subOneHour = false;
		int min = t1.getMinutes() - t2.getMinutes();
		if (min < 0) {
			min += 60;
			subOneHour = true;
		}
		int hour = t1.getHours() - t2.getHours() - (subOneHour?1:0);
		if (hour < 0) {
			hour += 24;
		}
		return new Time(min, hour);
	}


}