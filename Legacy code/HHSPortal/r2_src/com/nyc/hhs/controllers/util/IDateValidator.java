package com.nyc.hhs.controllers.util;

public interface IDateValidator {
	boolean isValid(String dateStr);
	boolean isDateOneBeforeDateTwo(String dateOneStr, String dateTwoStr);
}
