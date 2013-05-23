package com.bergerlavy.bolepo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class NetworkSettingsDialogActivity extends Activity {
	
	private static final int RQ_NETWORK_SETTINGS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_settings_dialog);
	}
	
	public void quitApp(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void goToSettings(View view) {
		startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), RQ_NETWORK_SETTINGS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RQ_NETWORK_SETTINGS) {
			if (BolePoMisc.isDeviceOnline(this)) {
				setResult(RESULT_OK);
				finish();
			}
			else {
				setContentView(R.layout.activity_network_settings_dialog_waiting_for_connection);
//				mProgressBar = (ProgressBar) findViewById(R.id.network_settings_dialog_waiting_for_connection_progress_bar);
				new WaitForConnectionTask().execute();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private class WaitForConnectionTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			for (int i = 1 ; i < 12 ; i++) {
				if (BolePoMisc.isDeviceOnline(NetworkSettingsDialogActivity.this))
					return true;
				else
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result)
				NetworkSettingsDialogActivity.this.setContentView(R.layout.activity_network_settings_dialog);
			else {
				setResult(RESULT_OK);
				NetworkSettingsDialogActivity.this.finish();
			}
			super.onPostExecute(result);
		}
		
	}

}
