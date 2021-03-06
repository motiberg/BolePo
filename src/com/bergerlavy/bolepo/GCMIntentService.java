package com.bergerlavy.bolepo;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoConstants.RSVP;
import com.bergerlavy.bolepo.dals.MeetingsDbAdapter;
import com.bergerlavy.bolepo.dals.Meeting;
import com.bergerlavy.bolepo.dals.Participant;
import com.bergerlavy.bolepo.dals.SDAL;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService {

	private MeetingsDbAdapter mDbAdapter;

	public static final int NEW_MEETING_NOTIFICATION_ID = 1;
	public static final int PARTICIPANT_ATTENDANCE_NOTIFICATION_ID = 2;
	public static final int PARTICIPANT_DECLINING_NOTIFICATION_ID = 3;
	public static final int PARTICIPANT_REMOVED_FROM_MEETING_NOTIFICATION_ID = 4;
	public static final int MEETING_CANCELLED_NOTIFICATION_ID = 5;
	public static final int NEW_MANAGER_NOTIFICATION_ID = 6;

	private static PowerManager.WakeLock sWakeLock;
	private static final Object LOCK = GCMIntentService.class;

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
		try {
			SDAL.regGCM(regId);
		} catch (NoInternetConnectionBolePoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		try {
			SDAL.unregGCM(regId);
		} catch (NoInternetConnectionBolePoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Called when your server sends a message to GCM, and GCM delivers it to the device. If the
	 * message has a payload, its contents are available as extras in the intent.
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {

		synchronized(LOCK) {
			if (sWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
			}
		}
		boolean toWakeLock = true;
		try {
			mDbAdapter = new MeetingsDbAdapter(context);
			mDbAdapter.open();

			String messageType = intent.getStringExtra(BolePoConstants.GCM_DATA.MESSAGE_TYPE.toString());
			List<Participant> participants = new ArrayList<Participant>();
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setContentTitle("Bolepo");
			notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			.setLights(Color.BLUE, 100, 100);

			switch (BolePoConstants.GCM_NOTIFICATION.getEnum(messageType)) {
			case MEETING_CANCLED:
			{
				String meetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());
				String meetingManager = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_MANAGER.toString());

				if (mDbAdapter.expelFromMeeting(meetingHash)) {

					Intent refreshListIntent = new Intent();
					refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
					sendBroadcast(refreshListIntent); 

					notificationBuilder.setContentText(meetingManager + " has cancelled " + meetingName)
					.setSmallIcon(R.drawable.ic_launcher);

					//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
					Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
					notificationBuilder.setContentIntent(contentIntent);

					mNotificationManager.notify(MEETING_CANCELLED_NOTIFICATION_ID, notificationBuilder.build());
				}
				break;
			}
			case NEW_MANAGER:
			{
				String oldMeetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.OLD_MEETING_HASH.toString());
				String newMeetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String oldManagerNewHash = intent.getStringExtra(BolePoConstants.GCM_DATA.OLD_MANAGER_NEW_HASH.toString());
				String newManagerPhone = intent.getStringExtra(BolePoConstants.GCM_DATA.NEW_MANAGER_PHONE.toString());
				String newManagerNewHash = intent.getStringExtra(BolePoConstants.GCM_DATA.NEW_MANAGER_NEW_HASH.toString());

				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());
				if (mDbAdapter.appointNewManager(oldMeetingHash, newMeetingHash, newManagerPhone, newManagerNewHash)) {

					Intent refreshListIntent = new Intent();
					refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
					sendBroadcast(refreshListIntent); 

					notificationBuilder.setContentText("You have been appointed to be the new manager of " + meetingName)
					.setSmallIcon(R.drawable.ic_launcher);

					//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
					Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
					notificationBuilder.setContentIntent(contentIntent);

					mNotificationManager.notify(NEW_MANAGER_NOTIFICATION_ID, notificationBuilder.build());
				}
				break;
			}
			case NEW_MANAGER_REMOVE_OLDER:
			{
				String oldMeetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.OLD_MEETING_HASH.toString());
				String newMeetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String newManagerPhone = intent.getStringExtra(BolePoConstants.GCM_DATA.NEW_MANAGER_PHONE.toString());
				String newManagerNewHash = intent.getStringExtra(BolePoConstants.GCM_DATA.NEW_MANAGER_NEW_HASH.toString());

				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());
				if (mDbAdapter.appointNewManagerAndRemoveOldOne(oldMeetingHash, newMeetingHash, newManagerPhone, newManagerNewHash)) {

					Intent refreshListIntent = new Intent();
					refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
					sendBroadcast(refreshListIntent); 

					notificationBuilder.setContentText("You have been appointed to be the new manager of " + meetingName)
					.setSmallIcon(R.drawable.ic_launcher);

					//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
					Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
					notificationBuilder.setContentIntent(contentIntent);

					mNotificationManager.notify(NEW_MANAGER_NOTIFICATION_ID, notificationBuilder.build());
				}
				break;
			}
			case NEW_MEETING:
				String participantsCount = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_PARTICIPANTS_COUNT.toString());
				int particpantsCountInt = Integer.parseInt(participantsCount);
				List<String> partsPhones = new ArrayList<String>();
				for (int i = 0 ; i < particpantsCountInt ; i++) { 
					Participant p = parseParticipantData(intent.getStringExtra(BolePoConstants.GCM_DATA.PARTICIPANT_DATA.toString() + i));
					participants.add(p);
					partsPhones.add(p.getPhone());
				}			
				Meeting meeting = new Meeting(intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString()),
						intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_DATE.toString()),
						intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_TIME.toString()),
						intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_MANAGER.toString()),
						intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_LOCATION.toString()),
						intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_SHARE_LOCATION_TIME.toString()),
						partsPhones);
				meeting.setHash(intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString()));
				if (mDbAdapter.createMeeting(meeting, participants)) {
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
			{
				String meetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());

				if (mDbAdapter.expelFromMeeting(meetingHash)) {

					Intent refreshListIntent = new Intent();
					refreshListIntent.setAction(BolePoConstants.ACTION_BOLEPO_REFRESH_LISTS);
					sendBroadcast(refreshListIntent); 

					notificationBuilder.setContentText("You are no longer invited to " + meetingName)
					.setSmallIcon(R.drawable.ic_launcher);

					//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
					Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
					notificationBuilder.setContentIntent(contentIntent);

					mNotificationManager.notify(PARTICIPANT_REMOVED_FROM_MEETING_NOTIFICATION_ID, notificationBuilder.build());
				}
				break;
			}

			case PARTICIPANT_ATTENDED:
			{
				String attendedParticipant = intent.getStringExtra(BolePoConstants.GCM_DATA.PARTICIPANT_ATTENDANCE.toString());
				String meetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());
				mDbAdapter.updateParticipantAttendance(meetingHash, attendedParticipant, RSVP.YES);

				notificationBuilder.setContentText(attendedParticipant + " attends: " + meetingName)
				.setSmallIcon(R.drawable.ic_launcher);

				//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
				Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
				notificationBuilder.setContentIntent(contentIntent);

				mNotificationManager.notify(PARTICIPANT_ATTENDANCE_NOTIFICATION_ID, notificationBuilder.build());
				break;
			}
			case PARTICIPANT_DECLINED:
			{
				String declinedParticipant = intent.getStringExtra(BolePoConstants.GCM_DATA.PARTICIPANT_DECLINING.toString());
				String meetingHash = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_HASH.toString());
				String meetingName = intent.getStringExtra(BolePoConstants.GCM_DATA.MEETING_NAME.toString());

				//TODO change the RSVP from NO to DECLINE
				mDbAdapter.updateParticipantAttendance(meetingHash, declinedParticipant, RSVP.NO);

				notificationBuilder.setContentText(declinedParticipant + " declines: " + meetingName)
				.setSmallIcon(R.drawable.ic_launcher);

				//TODO change the destination activity - then don't forget to remove the notification cancel in the MainActivity
				Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
				notificationBuilder.setContentIntent(contentIntent);

				mNotificationManager.notify(PARTICIPANT_DECLINING_NOTIFICATION_ID, notificationBuilder.build());
				break;
			}
			default:
				toWakeLock = false;
				break;

			}
			
			if (toWakeLock) {
				sWakeLock.acquire(8000);
			}
		}
		finally {
			synchronized(LOCK) {
				sWakeLock.release();
			}
			mDbAdapter.close();
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
