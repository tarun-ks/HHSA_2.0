/**
 * 
 */
package com.nyc.hhs.handlers.ruleresulthandlers;

import java.util.ArrayList;
import java.util.List;

import com.nyc.hhs.exception.ApplicationException;

/**
 * This is the handler class for Integer Integer type Array List result
 * formatting in Business Rule evaluation. For the condition
 * ArrayListFloatResult
 */
public class ArrayListFloatResultHandler extends BaseBusinessRuleResultHandler
{
	/**
	 * This method formats the expression into ArrayList of Float from a a
	 * specific separator separated values in the form of String
	 * 
	 * @param asSeparator String object - generally and defaults to a ','
	 * @param asResultDataForFloat String object - a separator separated list of Strings
	 * @return List<Float>
	 * @throws ApplicationException
	 */
	@Override
	public List<Float> formatRuleResult(String asSeparator, String asResultDataForFloat) throws ApplicationException
	{

		List<Float> loReturnListForFloat = new ArrayList<Float>();
		try
		{
			for (String lsItem : asResultDataForFloat.split(asSeparator))
			{
				if (!lsItem.trim().isEmpty())
				{
					loReturnListForFloat.add(Float.parseFloat(lsItem.trim()));
				}
			}
		}
		catch (NumberFormatException loNe)
		{
			throw new ApplicationException("Could not parse the 'value' attribute value into Float.", loNe);
		}
		return loReturnListForFloat;
	}

}
