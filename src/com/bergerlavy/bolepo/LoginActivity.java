package com.bergerlavy.bolepo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	private EditText mPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mPhone = (EditText) findViewById(R.id.login_phone_number);
		
		LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		
	}
	
	public void login(View view) {
		BolePoMisc.setDevicePhoneNumber(this, mPhone.getText().toString());
		finish();
	}

}
