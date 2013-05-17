package com.bergerlavy.bolepo.forms;

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
	public static InputValidationReport createMeetingInputValidation(Meeting data) {

		CreateMeetingValidation vStatus = new CreateMeetingValidation(data);
		return vStatus.isOK();
	}
	
	public static InputValidationReport modifyMeetingInputValidation(Meeting data, long id) {
		ModifyMeetingValidation vStatus = new ModifyMeetingValidation(data, id);
		return vStatus.isOK();
	}

	

	
}

