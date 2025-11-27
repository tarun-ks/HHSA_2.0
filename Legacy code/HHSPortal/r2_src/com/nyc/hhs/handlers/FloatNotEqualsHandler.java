package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Float comparison in Business Rule evaluation.
 * Specifically where the provided values are expected to be not equal.
 */
public class FloatNotEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Float type comparison for if channel_variable's value is not equal to
	 * the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpressionForFloatEqual Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForFloatEqual) throws ApplicationException
	{

		validateChannel(aoExpressionForFloatEqual);

		Channel loChannel = (Channel) aoExpressionForFloatEqual.getChannel();
		try
		{
			Float lsChannelVar1Value = Float
					.parseFloat(loChannel.getData(aoExpressionForFloatEqual.getChannelVariable()).toString());

			if (aoExpressionForFloatEqual.getChannelVariable2() != null && !aoExpressionForFloatEqual.getChannelVariable2().isEmpty())
			{
				if (loChannel.getData(aoExpressionForFloatEqual.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForFloatEqual.getChannelVariable2() + "' which was never set in channel.");
				}
				Float lsChannelVar2Value = Float.parseFloat(loChannel.getData(aoExpressionForFloatEqual.getChannelVariable2())
						.toString());

				if (!lsChannelVar1Value.equals(lsChannelVar2Value))
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForFloatEqual.getValue() != null)
			{
				if (!lsChannelVar1Value.equals(Float.parseFloat(aoExpressionForFloatEqual.getValue())))
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