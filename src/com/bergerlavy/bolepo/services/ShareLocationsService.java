package com.bergerlavy.bolepo.services;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class ShareLocationsService extends Service  {

	private Location mLocation;
	private int mGpsSampleRate = 0;
	private Timer mTimer;
	private LocationManager mLocationManager;
	
	private final IBinder mBinder = new ShareLocationsBinder();

	@Override
	public void onCreate() {
		super.onCreate();

		// Acquire a reference to the system Location Manager
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				mLocation = location;
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mGpsSampleRate, 0, locationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mGpsSampleRate, 0, locationListener);
	}

	public class ShareLocationsBinder extends Binder {
		public ShareLocationsService getService() {
			return ShareLocationsService.this;
		}
	}
	
	public IBinder onBind(Intent intent) {
		return mBinder;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		mTimer = new Timer();
//		mTimer.scheduleAtFixedRate(new Task(), 0, 10 * 1000);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		mTimer.cancel();
	}
	
	public Location getLocation() {
		return mLocation;
	}

	class Task extends TimerTask {

		// run is a abstract method that defines task performed at scheduled time.
		public void run() {
//			SQLiteDatabase readableDB = new DbHelper(ShareLocationsService.this).getReadableDatabase();
//			Cursor activeMeetingCursor = readableDB.query(DbContract.Meetings.TABLE_NAME,
//					new String[] { DbContract.Meetings._ID, DbContract.Meetings.COLUMN_NAME_MEETING_HASH },
//					null, null, null, null, null);
//
//			if (activeMeetingCursor != null && activeMeetingCursor.moveToFirst()) {
//				while (!activeMeetingCursor.isAfterLast()) {
//					String hash = activeMeetingCursor.getString(activeMeetingCursor.getColumnIndex(DbContract.Meetings.COLUMN_NAME_MEETING_HASH));
//					if (mLocation != null)
//						SDAL.sendDeviceLocation(hash, mLocation);
//					else
//						SDAL.sendDeviceLocation(hash, mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//					activeMeetingCursor.moveToNext();
//				}
//			}
//			activeMeetingCursor.close();
//			readableDB.close();
//
		}
	}

}
