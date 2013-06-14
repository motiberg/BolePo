package com.bergerlavy.bolepo.forms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bergerlavy.bolepo.R;

public class ErrorDialogActivity extends Activity {

	private TextView errorMessageTv;
	
	public static final String EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_dialog);
		
		errorMessageTv = (TextView) findViewById(R.id.error_dialog_message);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(EXTRA_ERROR_MESSAGE)) {
			errorMessageTv.setText(bundle.getString(EXTRA_ERROR_MESSAGE));
		}
	}
	
	public void closeDialog(View view) {
		finish();
	}

}
