package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Float comparison in Business Rule evaluation.
 * For the condition FloatLessThan
 */
public class FloatLessThanHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Float type comparison for if channel_variable's value is greater than
	 * the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpressionForFloatLess Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForFloatLess) throws ApplicationException
	{

		validateChannel(aoExpressionForFloatLess);

		Channel loChannel = (Channel) aoExpressionForFloatLess.getChannel();

		try
		{
			Float lsChannelVar1Value = Float
					.parseFloat(loChannel.getData(aoExpressionForFloatLess.getChannelVariable()).toString());

			if (aoExpressionForFloatLess.getChannelVariable2() != null && !aoExpressionForFloatLess.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForFloatLess.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForFloatLess.getChannelVariable2() + "' which was never set in channel.");
				}
				Float lsChannelVar2Value = Float.parseFloat(loChannel.getData(aoExpressionForFloatLess.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value < lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForFloatLess.getValue() != null)
			{
				if (lsChannelVar1Value < (Float.parseFloat(aoExpressionForFloatLess.getValue())))
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
