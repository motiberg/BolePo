package com.bergerlavy.bolepo.forms;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.bergerlavy.bolepo.dals.Participant;

public class Metadata {

	private Participant mMeetingCreator;
	private String mDate;
	private String mTime;
	
	public Metadata(Participant creator) {
		mMeetingCreator = creator;
		
		/* getting the current date and time */
	    Date creationDate = (new GregorianCalendar()).getTime();
	    
	    /* getting the format of a date string like in the UK.
	     * e.g. 17.02.13 */
	    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
	    
	    /* getting the format of time string like in the UK.
	     * e.g. 3:30pm */
	    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK);
	    
	    /* converting the date format into string object */
	    mDate = dateFormat.format(creationDate);
	    
	    /* converting the time format into string object */
	    mTime = timeFormat.format(creationDate);
	}

	public Participant getMeetingCreator() {
		return mMeetingCreator;
	}

	public String getDate() {
		return mDate;
	}

	public String getTime() {
		return mTime;
	}
	
	
}
