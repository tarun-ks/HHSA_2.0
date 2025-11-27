package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

/**
 * This is the handler class for String comparison in Business Rule evaluation.
 * Specifically where the provided Strings are expected to be equal.
 */
public class StringEqualsHandler extends BaseBusinessRuleHandler
{
	/**
	 * This method overrides the base functionality and evaluates the Expression
	 * for String type comparison for equal Strings
	 * 
	 * @param aoExpressionForStingEqual Expression object
	 * @return boolean
	 * @throws ApplicationException
	 */
	@Override
	public Object evaluateBusinessRule(Expression aoExpressionForStingEqual) throws ApplicationException
	{

		validateChannel(aoExpressionForStingEqual);

		Channel loChannel = (Channel) aoExpressionForStingEqual.getChannel();
		String lsChannelVar1Value = (String) loChannel.getData(aoExpressionForStingEqual.getChannelVariable());

		if (aoExpressionForStingEqual.getChannelVariable2() != null && !aoExpressionForStingEqual.getChannelVariable2().isEmpty())
		{
			String lsChannelVar2Value = (String) loChannel.getData(aoExpressionForStingEqual.getChannelVariable2());
			if (lsChannelVar2Value == null)
			{
				throw new ApplicationException("Found a key 'channel_variable2' with value '"
						+ aoExpressionForStingEqual.getChannelVariable2() + "' which was never set in channel.");
			}
			if (lsChannelVar1Value.equalsIgnoreCase(lsChannelVar2Value))
			{
				return true;
			}
			return false;
		}
		else if (aoExpressionForStingEqual.getValue() != null)
		{
			if (lsChannelVar1Value.equalsIgnoreCase(aoExpressionForStingEqual.getValue()))
			{
				return true;
			}
			return false;
		}
		throw new ApplicationException("Unable to find either 'channel_variable2' or 'value' attribute in expression");
	}
}