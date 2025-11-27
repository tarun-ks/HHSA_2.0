/**
 * 
 */
package com.nyc.hhs.handlers.ruleresulthandlers;

import java.util.ArrayList;
import java.util.List;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This is the handler class for String type Array List result formatting in
 * Business Rule evaluation. For the condition ArrayListStringResult
 */
public class ArrayListStringResultHandler extends BaseBusinessRuleResultHandler
{

	/**
	 * This method formats the expression into ArrayList of String from a a
	 * specific separator separated values in the form of String
	 * 
	 * @param asSeparator String object - generally and defaults to a ','
	 * @param asResultData String object - a separator separated list of Strings
	 * @return List<String>
	 * @throws ApplicationException
	 */
	@Override
	public List<String> formatRuleResult(String asSeparator, String asResultData) throws ApplicationException
	{

		List<String> loReturnList = new ArrayList<String>();
		for (String lsItem : asResultData.split(asSeparator))
		{
			loReturnList.add(lsItem);
		}
		return loReturnList;
	}

}
