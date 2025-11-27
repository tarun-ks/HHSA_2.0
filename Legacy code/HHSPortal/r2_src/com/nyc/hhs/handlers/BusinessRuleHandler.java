package com.nyc.hhs.handlers;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.rule.Expression;

/*
 * This abstract Interface will be implemented by class to evaluate business rules.
 * */
public interface BusinessRuleHandler
{

	public Object evaluateBusinessRule(Expression aoExpression) throws ApplicationException;
}
