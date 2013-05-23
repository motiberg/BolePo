package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerActivity extends Activity {

	private TimePicker mTimePicker;
	
	public static final String EXTRA_HOUR = "EXTRA_HOUR";
	public static final String EXTRA_MINUTES = "EXTRA_MINUTES";
	public static final String EXTRA_INIT_HOUR = "EXTRA_INIT_HOUR";
	public static final String EXTRA_INIT_MINUTES = "EXTRA_INIT_MINUTES";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_picker);
		
		mTimePicker = (TimePicker) findViewById(R.id.time_picker_time);
		mTimePicker.setIs24HourView(true);
		
		/* checking if there is an hour value to set as default */
		if (getIntent().hasExtra(EXTRA_INIT_HOUR))
			mTimePicker.setCurrentHour(getIntent().getIntExtra(EXTRA_INIT_HOUR, 0));
		
		/* checking if there is a minute value to set as default */
		if (getIntent().hasExtra(EXTRA_INIT_MINUTES))
			mTimePicker.setCurrentMinute(getIntent().getIntExtra(EXTRA_INIT_MINUTES, 0));
	}
	
	public void saveAndClose(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_HOUR, mTimePicker.getCurrentHour());
		intent.putExtra(EXTRA_MINUTES, mTimePicker.getCurrentMinute());
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
	
}
