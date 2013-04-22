package com.bergerlavy.bolepo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.SDAL;
import com.bergerlavy.bolepo.dals.SRDataForMeetingManaging;
import com.bergerlavy.bolepo.dals.ServerResponse;
import com.bergerlavy.bolepo.dals.ServerResponseStatus;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService {

	public static final int NEW_MEETING_NOTIFICATION_ID = 1;
	
	public GCMIntentService() {
		super(BolePoConstants.SENDER_ID);
	}

	/** 
	 * Called after a registration intent is received, passes the registration ID assigned by GCM
	 * to that device/application pair as parameter. Typically, you should send the regid to your
	 * server so it can use it to send messages to this device.
	 */
	@Override
	protected void onRegistered(Context context, String regId) {
		Toast.makeText(context, "gcm register", Toast.LENGTH_LONG).show();
		
		/* store registration ID on shared preferences */
    	BolePoMisc.setGcmRegId(this, regId);
		
		/* notify server about the registered ID */
		SDAL.regGCM(regId);

	}

	/**
	 * Called after the device has been unregistered from GCM. Typically, you should send the regid
	 * to the server so it unregisters the device.
	 */
	@Override
	protected void onUnregistered(Context context, String regId) {
		Toast.makeText(context, "gcm unregister", Toast.LENGTH_LONG).show();
		
		/* get old registration ID out of shared preferences */
    	BolePoMisc.removeGcmId(this);

    	/* notify server about the unregistered ID */
    	SDAL.unregGCM(regId);
	}

	/**
	 * Called when your server sends a message to GCM, and GCM delivers it to the device. If the
	 * message has a payload, its contents are available as extras in the intent.
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {

		String meetingHash = intent.getStringExtra("meeting_hash");
		
		ServerResponse servResp = SDAL.retrieveMeeting(meetingHash);
		Meeting m = null;
		if (servResp != null && servResp.getStatus() == ServerResponseStatus.OK) {
			if (servResp.hasData()) {
				
				SRDataForMeetingManaging serverData = servResp.getData();
				m = serverData.getMeeting();
			}
		}
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("Bolepo")
		        .setContentText(m.getCreator() + " invites you to " + m.getName())
		        .setSmallIcon(R.drawable.ic_launcher);

		Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
		mBuilder.setContentIntent(contentIntent);

		mNotificationManager.notify(NEW_MEETING_NOTIFICATION_ID, mBuilder.build());
		
	}

	/**
	 * Called when the device tries to register or unregister, but GCM returned an error.
	 * Typically, there is nothing to be done other than evaluating the error
	 * (returned by errorId) and trying to fix the problem.
	 */
	@Override
	protected void onError(Context context, String errorId) {
		Toast.makeText(context, "gcm error", Toast.LENGTH_LONG).show();

	}

}
