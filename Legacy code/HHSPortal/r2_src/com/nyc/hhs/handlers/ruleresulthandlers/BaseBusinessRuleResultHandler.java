/**
 * 
 */
package com.nyc.hhs.handlers.ruleresulthandlers;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This is an abstract base class for Business Rule Result formatter handlers -
 * will contain the common functionalities for all result handlers
 * 
 */
public abstract class BaseBusinessRuleResultHandler implements BusinessRuleResultHandler
{

	/*
	 * This method will again be overridden by child classes for specific
	 * requirements
	 */
	@Override
	public abstract Object formatRuleResult(String asSeparator, String asResultData) throws ApplicationException;

}