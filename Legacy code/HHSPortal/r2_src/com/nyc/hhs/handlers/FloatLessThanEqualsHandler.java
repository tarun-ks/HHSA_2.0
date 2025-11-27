package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Float comparison in Business Rule evaluation.
 * For the condition FloatLessThanEquals
 */
public class FloatLessThanEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Float type comparison for if channel_variable's value is less than or
	 * equal to the Static Value provided or the value in channel_variable2 of
	 * Channel
	 * 
	 * @param aoExpressionForFloatLessEqual Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForFloatLessEqual) throws ApplicationException
	{

		validateChannel(aoExpressionForFloatLessEqual);

		Channel loChannel = (Channel) aoExpressionForFloatLessEqual.getChannel();

		try
		{
			Float lsChannelVar1Value = Float
					.parseFloat(loChannel.getData(aoExpressionForFloatLessEqual.getChannelVariable()).toString());

			if (aoExpressionForFloatLessEqual.getChannelVariable2() != null && !aoExpressionForFloatLessEqual.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForFloatLessEqual.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForFloatLessEqual.getChannelVariable2() + "' which was never set in channel.");
				}
				Float lsChannelVar2Value = Float.parseFloat(loChannel.getData(aoExpressionForFloatLessEqual.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value <= lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForFloatLessEqual.getValue() != null)
			{
				if (lsChannelVar1Value <= (Float.parseFloat(aoExpressionForFloatLessEqual.getValue())))
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
