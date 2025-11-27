package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Float comparison in Business Rule evaluation.
 * For the condition FloatGreaterThan
 */
public class FloatGreaterThanHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Float type comparison for if channel_variable's value is greater than
	 * to the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpressionForFloatGreater Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForFloatGreater) throws ApplicationException
	{

		validateChannel(aoExpressionForFloatGreater);

		Channel loChannel = (Channel) aoExpressionForFloatGreater.getChannel();

		try
		{
			Float lsChannelVar1Value = Float
					.parseFloat(loChannel.getData(aoExpressionForFloatGreater.getChannelVariable()).toString());

			if (aoExpressionForFloatGreater.getChannelVariable2() != null && !aoExpressionForFloatGreater.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForFloatGreater.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForFloatGreater.getChannelVariable2() + "' which was never set in channel.");
				}
				Float lsChannelVar2Value = Float.parseFloat(loChannel.getData(aoExpressionForFloatGreater.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value > lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForFloatGreater.getValue() != null)
			{
				if (lsChannelVar1Value > (Float.parseFloat(aoExpressionForFloatGreater.getValue())))
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
