package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * For the condition IntegerLessThan
 */
public class IntegerLessThanHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is less than
	 * the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpressionForLessThanOnly Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForLessThanOnly) throws ApplicationException
	{

		validateChannel(aoExpressionForLessThanOnly);

		Channel loChannel = (Channel) aoExpressionForLessThanOnly.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpressionForLessThanOnly.getChannelVariable())
					.toString());

			if (aoExpressionForLessThanOnly.getChannelVariable2() != null && !aoExpressionForLessThanOnly.getChannelVariable2().isEmpty())
			{
				if (loChannel.getData(aoExpressionForLessThanOnly.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForLessThanOnly.getChannelVariable2() + "' which was never set in channel.");
				}
				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpressionForLessThanOnly.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value < lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForLessThanOnly.getValue() != null)
			{
				if (lsChannelVar1Value < (Integer.parseInt(aoExpressionForLessThanOnly.getValue())))
				{
					return true;
				}
				return false;
			}
		}
		catch (Exception loNa)
		{
			throw new ApplicationException(
					"Could not parse  either 'channel_variable' value or 'channel_variable2' value or 'value' attribute into Integer.",
					loNa);
		}
		throw new ApplicationException("Unable to find either 'channel_variable2' or 'value' attribute in expression");
	}
}
