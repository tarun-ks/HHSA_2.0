package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * Specifically where the provided Integers are expected to be equal.
 */
public class IntegerEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is equal to
	 * the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpressionIntegerEquals Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionIntegerEquals) throws ApplicationException
	{

		validateChannel(aoExpressionIntegerEquals);

		Channel loChannel = (Channel) aoExpressionIntegerEquals.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpressionIntegerEquals.getChannelVariable())
					.toString());

			if (aoExpressionIntegerEquals.getChannelVariable2() != null && !aoExpressionIntegerEquals.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpressionIntegerEquals.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpressionIntegerEquals.getChannelVariable2() + "' which was never set in channel.");
				}
				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpressionIntegerEquals.getChannelVariable2())
						.toString());

				if (lsChannelVar1Value.equals(lsChannelVar2Value))
				{
					return true;
				}
				return false;
			}
			else if (aoExpressionIntegerEquals.getValue() != null)
			{
				if (lsChannelVar1Value.equals(Integer.parseInt(aoExpressionIntegerEquals.getValue().toString())))
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
