package com.nyc.hhs.handlersTest;

import static org.junit.Assert.assertTrue;

import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.handlers.FloatLessThanHandler;
import com.nyc.hhs.handlers.FloatNotEqualsHandler;
import com.nyc.hhs.rule.Expression;

/**
 * This is a test class for FloatLessThanHandler class
 * 
 */
public class FloatLessThanHandlerTest
{

	private Channel moChannel = new Channel();
	private Element moExpressionElement = new Element("expression");

	private void setElementAttribute(String asAttributeName, String aoAttributeValue)
	{
		moExpressionElement.setAttribute(new Attribute(asAttributeName, aoAttributeValue));
	}

	/**
	 * This method tests for a true return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) is less than the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnTrue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "10.90");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "19.98");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().", aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) is not less than the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnFalse() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "15.9");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "13.3");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().", aoEx);
		}
	}

	/**
	 * This method tests for a true return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and value corresponding to
	 * 'channel_variable'(in channel) is less than value corresponding to
	 * 'channel_variable2'(in channel)
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnTrue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "16.0");
			moChannel.setData("testChVar2", "17.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			// setElementAttribute("value", "17");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2Available().", aoEx);
		}
	}

	/**
	 * This method tests for a true return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and value corresponding to
	 * 'channel_variable'(in channel) is less than value corresponding to
	 * 'channel_variable2'(in channel)
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnTrueScennario2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "1.0");
			moChannel.setData("testChVar2", "157.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			// setElementAttribute("value", "17");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2Available().", aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and value corresponding to
	 * 'channel_variable'(in channel) is not less than value corresponding to
	 * 'channel_variable2'(in channel)
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnFalse() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "19.0");
			moChannel.setData("testChVar2", "18.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "19.0");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and value corresponding to
	 * 'channel_variable'(in channel) is not less than value corresponding to
	 * 'channel_variable2'(in channel)
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnFalseScennario2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "199.0");
			moChannel.setData("testChVar2", "188.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "199.0");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

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
			// moChannel.setData("testChVar", "177.0");
			moChannel.setData("testChVar2", "999.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "177.0");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
			moChannel.setData("testChVar", "18.0");
			// moChannel.setData("testChVar2", "17.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
	public void testEvaluateBusinessRuleChannelVar2ExpectedButNotSetScennario2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "18.0");
			// moChannel.setData("testChVar2", "17.0");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
			moChannel.setData("testChVar", "172.0");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172nonFloat");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
	public void testEvaluateBusinessRuleForTypeMismatchInValueScennario2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "1722.0");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "nonFloat");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
			moChannel.setData("testChVar", "172NonFloatValue");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172.0.");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
			moChannel.setData("testChVar", "172.0");
			moChannel.setData("testChVar2", "172NonFloatValue");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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

			FloatLessThanHandler loFloatLessThanHandler = new FloatLessThanHandler();
			boolean loResult = (Boolean) loFloatLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

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
