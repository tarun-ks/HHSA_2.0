package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * For the condition IntegerGreaterThanEquals
 */
public class IntegerGreaterThanEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is greater
	 * than or equal to the Static Value provided or the value in
	 * channel_variable2 of Channel
	 * 
	 * @param aoExpressionForIntGreater Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForIntGreater) throws ApplicationException
	{

		validateChannel(aoExpressionForIntGreater);

		Channel loChannel = (Channel) aoExpressionForIntGreater.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpressionForIntGreater.getChannelVariable())
					.toString());

			if (aoExpressionForIntGreater.getChannelVariable2() != null && !aoExpressionForIntGreater.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForIntGreater.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForIntGreater.getChannelVariable2() + "' which was never set in channel.");
				}
				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpressionForIntGreater.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value >= lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForIntGreater.getValue() != null)
			{
				if (lsChannelVar1Value >= (Integer.parseInt(aoExpressionForIntGreater.getValue())))
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
