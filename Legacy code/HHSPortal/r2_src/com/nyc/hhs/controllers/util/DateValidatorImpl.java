package com.nyc.hhs.controllers.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nyc.hhs.constants.HHSConstants;

public class DateValidatorImpl implements IDateValidator {

private String dateFormat;	

	public DateValidatorImpl(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public boolean isValid(String dateStr) {
		Date d1;
		Date d2;
		DateFormat sdf = new SimpleDateFormat(this.dateFormat);
		sdf.setLenient(false);
		try {
			d1 = sdf.parse(HHSConstants.DATE_ORIGIN);
			d2 = sdf.parse(dateStr);
			if(d2.before(d1)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isDateOneBeforeDateTwo(String dateOneStr, String dateTwoStr) {
		Date d1;
		Date d2;
		
		DateFormat sdf = new SimpleDateFormat(this.dateFormat);
		sdf.setLenient(false);
		
		if(!isValid(dateOneStr)) {
			return false;
		}
		
		if(!isValid(dateTwoStr)) {
			return false;
		}
		
		try {
			d1 = sdf.parse(dateOneStr);
			d2 = sdf.parse(dateTwoStr);
			if(d2.before(d1)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
