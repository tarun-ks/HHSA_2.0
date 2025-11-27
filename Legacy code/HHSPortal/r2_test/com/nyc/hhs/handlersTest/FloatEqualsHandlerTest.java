package com.nyc.hhs.handlersTest;

import static org.junit.Assert.assertTrue;

import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.handlers.FloatEqualsHandler;
import com.nyc.hhs.rule.Expression;

/**
 * This is a test class for FloatEqualsHandler class
 * 
 */
public class FloatEqualsHandlerTest
{

	private Channel moChannel = new Channel();
	private Element moExpressionElement = new Element("expression");

	private void setElementAttribute(String asAttributeName, String aoAttributeValue)
	{
		moExpressionElement.setAttribute(new Attribute(asAttributeName, aoAttributeValue));
	}

	/**
	 * This method tests for a true return when 'channel_variable' attribute is
	 * set and it's value(in channel variable)equals the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnTrue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", 10.2F);
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "10.2F");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().",  aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) does not equal the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnFalse() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "1.08");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "13.54");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(!liResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().",  aoEx);
		}
	}

	/**
	 * This method tests for a true return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and and their values(in channel)
	 * equals.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnTrue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "17.4");
			moChannel.setData("testChVar2", "17.4");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			// setElementAttribute("value", "17");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2Available().",  aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and and their values(in channel)
	 * are not equal.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnFalse() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "17.23");
			moChannel.setData("testChVar2", "18.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "17");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(!liResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					 aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when
	 * 'channel_variable' attributes is either not set or equals to a blank.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVarExpectedButNotSet() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "17.3");
			moChannel.setData("testChVar2", "17.3");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "17.3");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when
	 * 'channel_variable2' attributes is not set or equals to a blank
	 * considering the value attribute is also not available for comparison.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2ExpectedButNotSet() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "17.34");
			// moChannel.setData("testChVar2", "17.34");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable is set to a non Float value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInValue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172.34");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172.34NonFloatValue"); // Type
																	// Mismatch
																	// -
																	// expected
																	// a Float
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable (value in channel at channel_variable's value key) is
	 * set to a non Float value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInFirstChannelVar() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172.34NonFloatValue");// Type
																	// Mismatch
																	// -
																	// expected
																	// a Float

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172.34");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in second
	 * channel variable (value in channel at channel_variable2's value key) is
	 * set to a non Float value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInChannelVar2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172.34");
			moChannel.setData("testChVar2", "172.34NonFloatValue");// Type
																	// Mismatch
																	// -
																	// expected
																	// a Float

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet1() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet2() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet3() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet4() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean liResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(liResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when neither 'Value'
	 * nor 'channel_variable2' attributes is set
	 * */
	@Test
	public void testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "17");
			// moChannel.setData("testChVar2", "12");

			setElementAttribute("channel_variable", "testChVar");
			// setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "Integer");
			// setElementAttribute("value", "31");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatEqualsHandler loFloatEqualsHandler = new FloatEqualsHandler();
			boolean lbResult = (Boolean) loFloatEqualsHandler.evaluateBusinessRule(loExpression);
			assertTrue(lbResult);

		}
		catch (ApplicationException aoAppEx)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet().",
					aoEx);
		}
	}

}
