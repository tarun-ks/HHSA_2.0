package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for Integer comparison in Business Rule evaluation.
 * For the condition IntegerNotEquals
 */
public class IntegerNotEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for Integer type comparison for if channel_variable's value is not equal
	 * to the Static Value provided or the value in channel_variable2 of Channel
	 * 
	 * @param aoExpression Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpression) throws ApplicationException
	{

		validateChannel(aoExpression);

		Channel loChannel = (Channel) aoExpression.getChannel();

		try
		{
			Integer lsChannelVar1Value = Integer.parseInt(loChannel.getData(aoExpression.getChannelVariable())
					.toString());

			if (aoExpression.getChannelVariable2() != null && !aoExpression.getChannelVariable2().isEmpty())
			{

				if (loChannel.getData(aoExpression.getChannelVariable2()) == null)
				{
					throw new ApplicationException("Found a key 'channel_variable2' with value '"
							+ aoExpression.getChannelVariable2() + "' which was never set in channel.");
				}

				Integer lsChannelVar2Value = Integer.parseInt(loChannel.getData(aoExpression.getChannelVariable2())
						.toString());

				if (!lsChannelVar1Value.equals(lsChannelVar2Value))
				{
					return true;
				}
				return false;
			}
			else if (aoExpression.getValue() != null)
			{
				if (!lsChannelVar1Value.equals(Integer.parseInt(aoExpression.getValue().toString())))
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