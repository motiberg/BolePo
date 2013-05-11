package com.bergerlavy.bolepo;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bergerlavy.bolepo.dals.DAL;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.Participant;
import com.bergerlavy.bolepo.dals.SDAL;

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

		String messageType = intent.getStringExtra(BolePoConstants.GCM_DATA.MESSAGE_TYPE.toString());
		List<Participant> participants = new ArrayList<Participant>();
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setContentTitle("Bolepo");
		
		switch (BolePoConstants.GCM_NOTIFICATION.getEnum(messageType)) {
		case MEETING_CANCLED:
			break;
		case NEW_MANAGER:
			break;
		case NEW_MEETING:
			String participantsCount = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_PARTICIPANTS_COUNT.toString());
			int particpantsCountInt = Integer.parseInt(participantsCount);
			for (int i = 0 ; i < particpantsCountInt ; i++) 
				participants.add(parseParticipantData(intent.getStringExtra(BolePoConstants.GCM_DATA.PARTICIPANT_DATA.toString() + i)));
			Meeting meeting = new Meeting(intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString()),
					intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_DATE.toString()),
					intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_TIME.toString()),
					intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_MANAGER.toString()),
					intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_LOCATION.toString()),
					intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_SHARE_LOCATION_TIME.toString()),
					null);
			if (DAL.createMeeting(meeting, participants)) {
				Intent refreshListIntent = new Intent();
				refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
				sendBroadcast(refreshListIntent); 
			
				notificationBuilder.setContentText(meeting.getManager() + " invites you to " + meeting.getName())
				        .setSmallIcon(R.drawable.ic_launcher);

				Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
				notificationBuilder.setContentIntent(contentIntent);

				mNotificationManager.notify(NEW_MEETING_NOTIFICATION_ID, notificationBuilder.build());
			}
			participants.clear();
			break;
		case UPDATED_MEETING:
			
			break;
		case REMOVED_FROM_MEETING:
			
			break;
		default:
			break;
		
		}
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
	
	private Participant parseParticipantData(String participant) {
		String[] data = participant.split("::");
		return new Participant.Builder(data[0]).setName(data[1]).setCredentials(data[2]).setRsvp(data[3]).setHash(data[4]).build();
	}

}
