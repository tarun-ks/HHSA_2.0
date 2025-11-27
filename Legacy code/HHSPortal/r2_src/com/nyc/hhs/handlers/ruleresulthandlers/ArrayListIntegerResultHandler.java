/**
 * 
 */
package com.nyc.hhs.handlers.ruleresulthandlers;

import java.util.ArrayList;
import java.util.List;

import com.nyc.hhs.exception.ApplicationException;

/**
 * @author ramesh.kumar.jangra
 * 
 */
public class ArrayListIntegerResultHandler extends BaseBusinessRuleResultHandler
{
	/**
	 * This method formats the expression into ArrayList of Integer from a a
	 * specific separator separated values in the form of String
	 * 
	 * @param asSeparator String object - generally and defaults to a ','
	 * @param asResultDataForInterger String object - a separator separated list of Strings
	 * @return List<Integer>
	 * @throws ApplicationException
	 */
	@Override
	public List<Integer> formatRuleResult(String asSeparator, String asResultDataForInterger) throws ApplicationException
	{

		List<Integer> loReturnListForInteger = new ArrayList<Integer>();
		try
		{
			for (String lsItem : asResultDataForInterger.split(asSeparator))
			{
				if (!lsItem.trim().isEmpty())
				{
					loReturnListForInteger.add(Integer.parseInt(lsItem.trim()));
				}
			}
		}
		catch (NumberFormatException loNe)
		{
			throw new ApplicationException("Could not parse the 'value' attribute value into Integer.", loNe);
		}
		return loReturnListForInteger;
	}

}
