package com.bergerlavy.bolepo.forms;

import java.util.List;

import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;

public class ModifyMeetingValidation implements ValidationStatus {

	private Meeting mMeeting;
	private boolean mChecked;
	private long mId;
	private InputValidationReport mReport;

	public ModifyMeetingValidation(Meeting m, long id) {
		mMeeting = m;
		mId = id;
		mChecked = false;
	}

	@Override
	public InputValidationReport isOK() {
		if (mChecked)
			return mReport;

		mChecked = true;

		List<Meeting> allAcceptedMeetings = DAL.getAllAcceptedMeetings(mId);
		
		for (Meeting m : allAcceptedMeetings) {
			String date = m.getDate();
			String time = m.getTime();
			String shareTime = m.getShareLocationTime();
			String name = m.getName();
			
			if (mMeeting.getDate().equals(date)) {
				Time dbTime = new Time(time);
				Time dbShareTime = new Time(shareTime);
				Time newTime = new Time(mMeeting.getTime());
				if (Time.substract(dbTime, dbShareTime).isSmallerEqualTo(newTime) && newTime.isSmallerEqualTo(dbTime)) {
					mReport = new InputValidationReport.Builder(false)
					.setError("This meeting is overlaps with another meeting of yours: " + name)
					.build();
					return mReport;
				}
			}
			/* handling the case the meeting are at different dates, but actually handling meetings that are
			 * at sequential dates, i.e. the new meeting is day before or day after the
			 * meeting in the database */
//			else {
//				mReport = new InputValidationReport.Builder(true).build();
//				return mReport;
//			}
		}
		mReport = new InputValidationReport.Builder(true).build();
		return mReport;
	}

}
