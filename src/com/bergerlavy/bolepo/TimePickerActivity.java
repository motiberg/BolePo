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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_picker);
		
		mTimePicker = (TimePicker) findViewById(R.id.time_picker_time);
		mTimePicker.setIs24HourView(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_picker, menu);
		return true;
	}
	
	public void saveAndClose(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_HOUR, mTimePicker.getCurrentHour());
		intent.putExtra(EXTRA_MINUTES, mTimePicker.getCurrentMinute());
		setResult(RESULT_OK, intent);
		finish();
	}
}
