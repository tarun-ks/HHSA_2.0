package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

public abstract class BaseBusinessRuleHandler implements BusinessRuleHandler
{

	@Override
	public abstract Object evaluateBusinessRule(Expression aoExpression) throws ApplicationException;

	protected void validateChannel(Expression aoExpression) throws ApplicationException
	{

		if (aoExpression.getChannel() == null)
		{
			throw new ApplicationException("Unable to find value for compare for handler "
					+ aoExpression.getHandlerName());
		}

		Channel loChannel = (Channel) aoExpression.getChannel();

		if (aoExpression.getChannelVariable() == null)
		{
			throw new ApplicationException("Unable to locate 'channel_variable' attibute in expression for handler "
					+ aoExpression.getHandlerName());
		}

		if (aoExpression.getChannelVariable().isEmpty())
		{
			throw new ApplicationException(
					"Unable to parse a blank 'channel_variable' attribute value for compare for handler "
							+ aoExpression.getHandlerName());
		}

		Object lsChannelVar1Value = (Object) loChannel.getData(aoExpression.getChannelVariable());

		if (lsChannelVar1Value == null)
		{
			throw new ApplicationException("Found a key 'channel_variable' with value '"
					+ aoExpression.getChannelVariable() + "' which was never set in channel.");
		}

	}

}