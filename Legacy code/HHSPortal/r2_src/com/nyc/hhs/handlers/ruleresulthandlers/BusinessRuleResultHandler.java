package com.nyc.hhs.handlers.ruleresulthandlers;

import com.nyc.hhs.exception.ApplicationException;

/*
 * This abstract Interface will be implemented by class/handlers to format the evaluate business rules result in to specific format.
 * */
public interface BusinessRuleResultHandler
{

	/*
	 * This method will be implemented by the the implementing class handlers
	 */
	public Object formatRuleResult(String asSeparator, String asResultData) throws ApplicationException;
}
