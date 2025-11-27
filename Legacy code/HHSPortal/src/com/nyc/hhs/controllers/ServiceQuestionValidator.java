package com.nyc.hhs.controllers;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nyc.hhs.model.ServiceQuestions;

/**
 * This class is used to implement the spring validator framework for the service question 
 *
 */
public class ServiceQuestionValidator implements Validator {
	
	/**
	 * This is the spring override method
	 * 
	 * @param loClassObj - an object of Class type
	 * @returns boolean whether or not this object can validate objects of the given class
	 */
	@Override
	public boolean supports(Class loClassObj) {
		return ServiceQuestions.class.isAssignableFrom(loClassObj);
	}
	
	/**
	 * This is the spring override method to validate the form properties
	 * 
	 * @param loTarget - Populated object to validate
	 * @param loErrors -  Errors object that are build which may contain errors 
	 * 					for this field relating to types
	 */
	@Override
	public void validate(Object loTarget, Errors loErrors) {
		// object
		ServiceQuestions loServiceQuestion = (ServiceQuestions)loTarget;
		final String lsServiceQuestion1 = loServiceQuestion.getMsQuestion1();
		final String lsServiceQuestion2 = loServiceQuestion.getMsQuestion2();
		final String lsServiceQuestion3 = loServiceQuestion.getMsQuestion3();
		// check if empty
		if(lsServiceQuestion1!=null && lsServiceQuestion1.equalsIgnoreCase("nothing")){
			loErrors.rejectValue("msQuestion1", "! Field name is required.", "! Field name is required.");
		}
		// check if empty
		if(lsServiceQuestion2!=null && lsServiceQuestion2.equalsIgnoreCase("nothing")){
			loErrors.rejectValue("msQuestion2", "! Field name is required.", "! Field name is required.");
		}
		// check if empty
		if(lsServiceQuestion3!=null && lsServiceQuestion3.equalsIgnoreCase("nothing")){
			loErrors.rejectValue("msQuestion3", "! Field name is required.", "! Field name is required.");
		}
		// check other conditions
		if(lsServiceQuestion1!=null && lsServiceQuestion2!=null && lsServiceQuestion3!=null){
			if(lsServiceQuestion1.equalsIgnoreCase("no") && lsServiceQuestion2.equalsIgnoreCase("no") && lsServiceQuestion3.equalsIgnoreCase("no")){
				loErrors.rejectValue("msQuestion3", "", "");
			}else if(lsServiceQuestion1.equalsIgnoreCase("no") && lsServiceQuestion2.equalsIgnoreCase("no") && lsServiceQuestion3.equalsIgnoreCase("Yes")){
				loErrors.rejectValue("msQuestion3", "", "");
			}
		}
	}
}
