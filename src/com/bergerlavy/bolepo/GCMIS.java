package com.bergerlavy.bolepo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.bergerlavy.bolepo.dals.SDAL;

public class GCMIS extends IntentService {

	
	/**
	 * 
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public GCMIS(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	private static PowerManager.WakeLock sWakeLock;
    private static final Object LOCK = GCMIS.class;
    
    static void runIntentInService(Context context, Intent intent) {
        synchronized(LOCK) {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
            }
        }
        sWakeLock.acquire();
        intent.setClassName(context, GCMIS.class.getName());
        context.startService(intent);
    }
    
    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
                handleRegistration(intent);
            } else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                handleMessage(intent);
            }
        } finally {
            synchronized(LOCK) {
                sWakeLock.release();
            }
        }
    }
    
    private void handleRegistration(Intent intent) {
        String registrationId = intent.getStringExtra("registration_id");
        String error = intent.getStringExtra("error");
        String unregistered = intent.getStringExtra("unregistered");
        
        /* registration succeeded */
//        if (registrationId != null) {
////            /* store registration ID on shared preferences */
////        	SharedPreferences generalPrefs = getSharedPreferences(MainActivity.GENERAL_PREFERENCES, MODE_PRIVATE);
////        	SharedPreferences.Editor editor = generalPrefs.edit();
////        	editor.putString(MainActivity.GCM_REGISTRATION_ID, registrationId);
////        	editor.commit();
//        	
//            /* notify server about the registered ID */
//        	SDAL.regGCM(registrationId);
//        }
//            
//        /* unregistration succeeded */
//        if (unregistered != null) {
//            /* get old registration ID from shared preferences */
//        	SharedPreferences generalPrefs = getSharedPreferences(MainActivity.GENERAL_PREFERENCES, MODE_PRIVATE);
//        	SharedPreferences.Editor editor = generalPrefs.edit();
//        	editor.remove(MainActivity.GCM_REGISTRATION_ID);
//        	editor.commit();
//        	
//            /* notify server about the unregistered ID */
//        	SDAL.unregGCM(registrationId);
//        } 
            
        // last operation (registration or unregistration) returned an error;
        if (error != null) {
            if ("SERVICE_NOT_AVAILABLE".equals(error)) {
               // optionally retry using exponential back-off
               // (see Advanced Topics)
            } else {
                // Unrecoverable error, log it
                //Log.i(TAG, "Received error: " + error);
            }
        }
    }
    
    private void handleMessage(Intent intent) {
        String meetingHash = intent.getStringExtra("meeting_hash");
        
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("BolePo")
		        .setContentText("New Meeting !");

//		PendingIntent in = PendingIntent.getActivity(getApplicationContext(), 0, getIntent(), 0);
//		mBuilder.setContentIntent(in);

		mNotificationManager.notify(0, mBuilder.build());
        // generates a system notification to display the score and time
    }

}
