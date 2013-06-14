package com.bergerlavy.bolepo.forms;

import java.util.List;

import android.content.Context;

import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.MeetingsDbAdapter;

public class CreateMeetingValidation implements ValidationStatus {

	private MeetingsDbAdapter mDbAdapter;
	private Meeting mMeeting;
	private boolean mChecked;
	private Context mContext;
	private InputValidationReport mReport;

	public CreateMeetingValidation(Context c, Meeting m) {
		mMeeting = m;
		mChecked = false;
		mContext = c;
	}

	@Override
	public InputValidationReport isOK() {
		if (mChecked)
			return mReport;

		mDbAdapter = new MeetingsDbAdapter(mContext);
		mDbAdapter.open();

		mReport = new InputValidationReport.Builder(true).build();
		mChecked = true;

		if (mMeeting.getName().equals("")) {
			mReport = new InputValidationReport.Builder(false)
			.setError("Please provide meeting name")
			.build();
			return mReport;
		}

		if (mMeeting.getTime().equals("")) {
			mReport = new InputValidationReport.Builder(false)
			.setError("Please provide meeting time")
			.build();
			return mReport;
		}

		List<Meeting> allAcceptedMeetings = mDbAdapter.getAllAcceptedMeetings();

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

		if (mMeeting.getParticipantsNum() < 2) {
			mReport = new InputValidationReport.Builder(false)
			.setError("Please invite at least one participant")
			.build();
		}
		mDbAdapter.close();

		return mReport;
	}

}
