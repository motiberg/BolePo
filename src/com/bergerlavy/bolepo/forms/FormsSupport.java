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
	public static boolean createMeetingInputValidation(Meeting data) {

		CreateMeetingValidation vStatus = new CreateMeetingValidation();
		return vStatus.isOK();
	}


	
}

