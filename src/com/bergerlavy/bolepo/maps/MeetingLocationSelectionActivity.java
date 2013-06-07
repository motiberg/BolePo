package com.bergerlavy.bolepo.maps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoConstants;
import com.bergerlavy.bolepo.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MeetingLocationSelectionActivity extends Activity {

	private EditText searchBox;
	private GoogleMap map;

	private static final int RQ_GEOCODING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_location_selection);

		searchBox = (EditText) findViewById(R.id.meeting_location_map_search_box);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		searchBox.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					Intent intent = new Intent(MeetingLocationSelectionActivity.this, GeocodeActivity.class);
					intent.putExtra(GeocodeActivity.EXTRA_ADDRESS, searchBox.getText().toString());
					startActivityForResult(intent, RQ_GEOCODING);
					return true;
				}
				return false;
			}
		});
	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RQ_GEOCODING) {
			if (resultCode == RESULT_OK) {
				ArrayList<LatLng> coordinates = data.getParcelableArrayListExtra(GeocodeActivity.EXTRA_COORDINATES);
				if (coordinates != null) {
//					CameraUpdate cu = CameraUpdateFactory.newLatLng(coordinates.get(0));
//					map.moveCamera(cu);
					DisplayMetrics displayMetrics = new DisplayMetrics();
					WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
					wm.getDefaultDisplay().getMetrics(displayMetrics);
					int screenw = displayMetrics.widthPixels;
					int screenh = displayMetrics.heightPixels;
					Toast.makeText(this, "width = " + screenw + " height = " + screenh, Toast.LENGTH_SHORT).show();
					int R = 6371; // radius of the earch
					LatLng northEast = coordinates.get(0);
					LatLng southWest = coordinates.get(1);
					double dLat = Math.toRadians(southWest.latitude - northEast.latitude);
					double dLong = Math.toRadians(southWest.longitude - northEast.longitude);
					double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
							Math.sin(dLong / 2) * Math.sin(dLong / 2) *
							Math.cos(northEast.latitude) * Math.cos(southWest.latitude);
					double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
					double d = R * c * 1000;
					Toast.makeText(this, "area dist = " + d, Toast.LENGTH_SHORT).show();
					
					double screenDiagonalInPixels = Math.sqrt(screenw * screenw + screenh * screenh);
					for (int i = 1 ; i < BolePoConstants.MAP_SCALES_PER_ZOOM_LEVEL.length ; i++) {
						if (screenDiagonalInPixels * BolePoConstants.MAP_SCALES_PER_ZOOM_LEVEL[i] < d) {
							CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(coordinates.get(2), i - 1);
							map.animateCamera(cu);
							break;
						}
					}
					
				}
			}
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
			}
		}
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meeting_location_selection, menu);
		return true;
	}

}
