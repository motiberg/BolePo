package com.bergerlavy.bolepo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MeetingDataActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_meeting_data, menu);
		return true;
	}

}
