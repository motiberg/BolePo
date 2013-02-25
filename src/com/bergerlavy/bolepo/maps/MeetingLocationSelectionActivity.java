package com.bergerlavy.bolepo.maps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.bergerlavy.bolepo.R;

public class MeetingLocationSelectionActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_location_selection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_meeting_location_selection, menu);
		return true;
	}
	
	public static BoleLocation getMeetingLocation() {
		return null;
	}

}
