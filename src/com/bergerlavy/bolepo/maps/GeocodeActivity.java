package com.bergerlavy.bolepo.maps;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.bergerlavy.bolepo.BolePoMisc;
import com.bergerlavy.bolepo.R;
import com.google.android.gms.maps.model.LatLng;

public class GeocodeActivity extends Activity {

	public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";
	public static final String EXTRA_COORDINATES = "EXTRA_COORDINATES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geocode);

		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(EXTRA_ADDRESS)) {
			new GeocodingTask().execute(bundle.getString(EXTRA_ADDRESS));
		}
		else {
			Toast.makeText(this, "Intenal Error", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private class GeocodingTask extends AsyncTask<String, Void, ArrayList<LatLng>> {

		@Override
		protected ArrayList<LatLng> doInBackground(String... params) {
			ArrayList<LatLng> coordinates = null;
			try {
				coordinates = BolePoMisc.geocoding(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return coordinates;
		}

		@Override
		protected void onPostExecute(ArrayList<LatLng> result) {
			super.onPostExecute(result);
			returnCoordinates(result);
		}


	}

	private void returnCoordinates(ArrayList<LatLng> result) {
		Intent intent = new Intent();
		if (result != null) {
			ArrayList<LatLng> coordinate = new ArrayList<LatLng>();
			coordinate.addAll(result);
			intent.putParcelableArrayListExtra(EXTRA_COORDINATES, coordinate);
			setResult(RESULT_OK, intent);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		finish();
	}
}

