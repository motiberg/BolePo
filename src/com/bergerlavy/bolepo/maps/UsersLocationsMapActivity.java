package com.bergerlavy.bolepo.maps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.bergerlavy.bolepo.R;

public class UsersLocationsMapActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_locations_map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_users_locations_map, menu);
		return true;
	}

}
