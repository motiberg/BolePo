package com.bergerlavy.bolepo.maps;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

import com.bergerlavy.bolepo.R;
import com.bergerlavy.bolepo.shareddata.GlobalData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPositionCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetingLocationSelectionActivity extends Activity
{
	// *************
	// Data Members
	// *************
	private static GoogleMap mMap;
	private static Marker    mMarker;
	
	
	// ******************************************************
	//					Main methods
	// ******************************************************
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_location_selection);
		
		// Validates and initialize the map
		ValidateMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_meeting_location_selection, menu);
		return true;
	}
	
	/***
	 * The main function of this class.
	 * Initialize this view and sets makes the user chose a location.
	 * @return the bolelocation chosen by the user.
	 */
	public static BoleLocation getMeetingLocation()
	{
		return null;
	}
	
	// ******************************************************
	//					Initialization methods
	// ******************************************************
	
	/** 
	 * This function will be used to ensure that the GoogleMap object
	 * is never null.
	 */
	private void ValidateMap()
	{
		// Checks if the map is not yet initalized
		if (mMap == null)
		{
			// Gets the map from the fragment
			mMap = ((MapFragment)(getFragmentManager().findFragmentById(R.id.selection_map))).getMap();
			
			// Initialize the map values
			InitialMapValues();
			
			// Attach the marker to the map
			FirstTimeAddMarker();
			
			// Ensures that the proccess succeeded
			if (mMap == null)
			{
				// TODO : Print Error
			}
		}
	}
	
	/**
	 * This function customize the map for our needs
	 */
	private void InitialMapValues()
	{
		// Sets map type
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		// Enables the "My location"
		mMap.setMyLocationEnabled(true);
		
		// Gets the handler for the map options
		UiSettings lSettings = mMap.getUiSettings();
		
		// Sets zoom level to street level
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GetBestLocation(), 
														  GlobalData.STREET_LEVEL_ZOOM));
	
		// Enables all options
		lSettings.setAllGesturesEnabled(true);
		lSettings.setCompassEnabled(true);
		lSettings.setMyLocationButtonEnabled(true);
		
		// Setting the listener for clicks on the map
		mMap.setOnMapClickListener(MarkerListener);
		
	}

	// This method adds the marker to the map when it is initialized
	private void FirstTimeAddMarker()
	{
		// Enables the marker to be dragged
		mMarker.setDraggable(true);
		
		// Gets the best available location of the map
		LatLng CurPos = GetBestLocation();
		
		// Adds marker to map.
		mMarker = mMap.addMarker(new MarkerOptions().position(CurPos)
													.title("BolePo"));
	}
	
	// ******************************************************
	//					Supporting methods
	// ******************************************************
	
	/**
	 * This function is used to initialize the marker.
	 * @return The best updated location value.
	 */
	private LatLng GetBestLocation()
	{
		// Defines a return value
		LatLng ReturnValue = null;
		
		
		
		// If no location was retrieved, used default location
		if (ReturnValue == null)
		{
			ReturnValue = GlobalData.DEFAULT_LOCATION;
		}
		
		// Returns the best found location
		return (ReturnValue);
	}
	
	/**
	 * A listener that handles the movement of the marker
	 */
	OnMapClickListener MarkerListener = new OnMapClickListener()
	{
		
		/**
		 * Moves the marker to the location of the click
		 */
		public void onMapClick(LatLng arg0) 
		{
			// Changes the location of the marker.
			mMarker.setPosition(arg0);
		}
	};
	
}
