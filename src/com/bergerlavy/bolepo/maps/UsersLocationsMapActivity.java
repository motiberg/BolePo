package com.bergerlavy.bolepo.maps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.services.ShareLocationsService;
import com.bergerlavy.bolepo.services.ShareLocationsService.ShareLocationsBinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

public class UsersLocationsMapActivity extends Activity  {

	private GoogleMap map;
	private boolean bounded;
	private ShareLocationsService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_locations_map);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		map.setMyLocationEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_users_locations_map, menu);
		return true;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bounded = false;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			ShareLocationsBinder binder = (ShareLocationsBinder) service;
			UsersLocationsMapActivity.this.service = binder.getService();
			bounded = true;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, ShareLocationsService.class), mConnection, Context.BIND_AUTO_CREATE);
		service.getLocation();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (bounded) {
			unbindService(mConnection);
			bounded = false;
		}
	}
	
	

}
