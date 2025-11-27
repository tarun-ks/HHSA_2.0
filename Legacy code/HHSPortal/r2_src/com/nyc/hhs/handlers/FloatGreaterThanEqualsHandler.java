package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Float comparison in Business Rule evaluation.
 * For the condition FloatGreaterThanEquals
 */

public class FloatGreaterThanEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Float type comparison for if channel_variable's value is greater than
	 * or equal to the Static Value provided or the value in channel_variable2
	 * of Channel
	 * 
	 * @param aoExpressionForFloat Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForFloat) throws ApplicationException
	{

		validateChannel(aoExpressionForFloat);

		Channel loChannel = (Channel) aoExpressionForFloat.getChannel();

		try
		{
			Float lsChannelVar1Value = Float
					.parseFloat(loChannel.getData(aoExpressionForFloat.getChannelVariable()).toString());

			if (aoExpressionForFloat.getChannelVariable2() != null && !aoExpressionForFloat.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForFloat.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForFloat.getChannelVariable2() + "' which was never set in channel.");
				}
				Float lsChannelVar2Value = Float.parseFloat(loChannel.getData(aoExpressionForFloat.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value >= lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForFloat.getValue() != null)
			{
				if (lsChannelVar1Value >= (Float.parseFloat(aoExpressionForFloat.getValue())))
				{
					return true;
				}
				return false;
			}
		}
		catch (Exception loNa)
		{
			throw new ApplicationException(
					"Could not parse  either 'channel_variable' value or 'channel_variable2' value or 'value' attribute into Float.",
					loNa);
		}
		throw new ApplicationException("Unable to find either 'channel_variable2' or 'value' attribute in expression");
	}
}
