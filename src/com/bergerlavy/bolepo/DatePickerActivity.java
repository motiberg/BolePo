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
	
	public void saveAndClose(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DAY, mDatePicker.getDayOfMonth());
		
		/* getMonth() returns the month as a value in the range of 0 .. 11, hence the increment by 1 */
		intent.putExtra(EXTRA_MONTH, mDatePicker.getMonth() + 1);
		intent.putExtra(EXTRA_YEAR, mDatePicker.getYear());
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
}
