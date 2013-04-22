package com.bergerlavy.bolepo;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bergerlavy.db.DbContract;

public class AcceptedMeetingsAdapter extends CursorAdapter {

	private Context mContext;
	
	public AcceptedMeetingsAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView meetingName = (TextView) view.findViewById(R.id.item_am_meeting_name);
		meetingName.setText(cursor.getString(cursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_NAME)));

		TextView meetingWhen = (TextView) view.findViewById(R.id.item_am_meeting_date_and_time);
		meetingWhen.setText(cursor.getString(cursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_DATE)) + " " +
				cursor.getString(cursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_TIME)));
		
		TextView meetingWhere = (TextView) view.findViewById(R.id.item_am_meeting_location);
		meetingWhere.setText(cursor.getString(cursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION)));
		
		TextView meetingCreator = (TextView) view.findViewById(R.id.item_am_meeting_creator);
		meetingCreator.setText(cursor.getString(cursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER)));
		
		String devicePhoneNumber = BolePoMisc.getDevicePhoneNumber(mContext);
		
		if (!devicePhoneNumber.equals("") && devicePhoneNumber.equals(meetingCreator.getText().toString())) {
			meetingCreator.setText(devicePhoneNumber + " (You)");
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View v = LayoutInflater.from(context).inflate(R.layout.item_accepted_meeting, parent, false);
		return v;
	}

}
