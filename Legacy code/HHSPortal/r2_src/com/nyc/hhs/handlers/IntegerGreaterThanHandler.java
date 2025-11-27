package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * For the condition IntegerGreaterThan
 */
public class IntegerGreaterThanHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is greater
	 * than to the Static Value provided or the value in channel_variable2 of
	 * Channel
	 * 
	 * @param aoExpressionForIntGreaterOnly Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForIntGreaterOnly) throws ApplicationException
	{

		validateChannel(aoExpressionForIntGreaterOnly);

		Channel loChannel = (Channel) aoExpressionForIntGreaterOnly.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpressionForIntGreaterOnly.getChannelVariable())
					.toString());

			if (aoExpressionForIntGreaterOnly.getChannelVariable2() != null && !aoExpressionForIntGreaterOnly.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionForIntGreaterOnly.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionForIntGreaterOnly.getChannelVariable2() + "' which was never set in channel.");
				}
				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpressionForIntGreaterOnly.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value > lsChannelVar2Value)
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionForIntGreaterOnly.getValue() != null)
			{
				if (lsChannelVar1Value > (Integer.parseInt(aoExpressionForIntGreaterOnly.getValue())))
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
