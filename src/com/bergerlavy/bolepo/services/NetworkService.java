package com.bergerlavy.bolepo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class NetworkService extends Service {

	private final IBinder mBinder = new NetworkBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class NetworkBinder extends Binder {
		
		NetworkService getService() {
			return NetworkService.this;
		}
	}
	
	

}
