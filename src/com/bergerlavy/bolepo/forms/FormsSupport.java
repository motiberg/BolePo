package com.bergerlavy.bolepo.forms;

import android.content.Context;

import com.bergerlavy.bolepo.dals.Meeting;


/**
 * 
 * @author Moti
 *
 */
public class FormsSupport {

	/**
	 * 
	 * @param purpose
	 * @param date
	 * @param time
	 * @param Location
	 * @param shareLocationTime
	 * @return
	 */
	public static InputValidationReport createMeetingInputValidation(Context c, Meeting data) {

		CreateMeetingValidation vStatus = new CreateMeetingValidation(c, data);
		return vStatus.isOK();
	}
	
	public static InputValidationReport modifyMeetingInputValidation(Context c, Meeting data, long id) {
		ModifyMeetingValidation vStatus = new ModifyMeetingValidation(c, data, id);
		return vStatus.isOK();
	}

	public static String chopeNonDigitsFromPhoneNumber(String phone) {
		String result = "";
		for (int i = 0 ; i < phone.length() ; i++)
			if (phone.charAt(i) >= '0' && phone.charAt(i) <= '9')
				result += phone.charAt(i);
		return result;
	}

	
}

