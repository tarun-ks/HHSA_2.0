package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * For the condition IntegerLessThanEquals
 */

public class IntegerLessThanEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is less than
	 * or equal to the Static Value provided or the value in channel_variable2
	 * of Channel
	 * 
	 * @param aoExpressionForLessThan Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForLessThan) throws ApplicationException
	{

		validateChannel(aoExpressionForLessThan);

		Channel loChannel = (Channel) aoExpressionForLessThan.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpressionForLessThan.getChannelVariable())
					.toString());

			if (aoExpressionForLessThan.getChannelVariable2() != null && !aoExpressionForLessThan.getChannelVariable2().isEmpty())
			{
				if (loChannel.getData(aoExpressionForLessThan.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForLessThan.getChannelVariable2() + "' which was never set in channel.");
				}

				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpressionForLessThan.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value <= lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForLessThan.getValue() != null)
			{
				if (lsChannelVar1Value <= (Integer.parseInt(aoExpressionForLessThan.getValue())))
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
