package com.bergerlavy.bolepo.forms;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.bergerlavy.bolepo.MainActivity;
import com.bergerlavy.bolepo.R;

public class RemoveMeetingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_meeting);
		
		LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        TextView messageTV = (TextView) findViewById(R.id.remove_meeting_message);
        
        boolean meetingCreatorKindOfRemoval = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(MainActivity.EXTRA_MEETING_CREATOR_REMOVAL))
        	meetingCreatorKindOfRemoval = bundle.getBoolean(MainActivity.EXTRA_MEETING_CREATOR_REMOVAL, false);
        
        String message;
        if (meetingCreatorKindOfRemoval)
        	message = "Would you like to remove this meeting permanently, or to pass the leadership to one of the participants ?";
        else { 
        	message = "Would you like to remove this meeting ?";
        	((Button) findViewById(R.id.remove_meeting_pass_ownship)).setVisibility(View.INVISIBLE);
        }
  
        messageTV.setText(message);
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remove_meeting, menu);
		return true;
	}

}
