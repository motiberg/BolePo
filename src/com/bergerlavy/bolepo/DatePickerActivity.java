package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerActivity extends Activity {

	public static final String EXTRA_DAY = "EXTRA_DAY";
	public static final String EXTRA_MONTH = "EXTRA_MONTH";
	public static final String EXTRA_YEAR = "EXTRA_YEAR";
	
	private DatePicker mDatePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_date_picker);
		
		mDatePicker = (DatePicker) findViewById(R.id.date_picker_date);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.date_picker, menu);
		return true;
	}
	
	public void saveAndClose(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DAY, mDatePicker.getDayOfMonth());
		intent.putExtra(EXTRA_MONTH, mDatePicker.getMonth());
		intent.putExtra(EXTRA_YEAR, mDatePicker.getYear());
		setResult(RESULT_OK, intent);
		finish();
	}

}
