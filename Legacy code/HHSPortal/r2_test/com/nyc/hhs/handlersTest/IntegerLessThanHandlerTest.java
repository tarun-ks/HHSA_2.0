package com.nyc.hhs.handlersTest;

import static org.junit.Assert.assertTrue;

import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.handlers.IntegerLessThanHandler;
import com.nyc.hhs.handlers.IntegerNotEqualsHandler;
import com.nyc.hhs.rule.Expression;

/**
 * This is a test class for IntegerLessThanHandler class
 * 
 */
public class IntegerLessThanHandlerTest
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
			moChannel.setData("testChVar", "10");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "19");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (Exception e)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().", e);
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
			moChannel.setData("testChVar", "10");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("value", "3");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		}
		catch (Exception e)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateBusinessRule().", e);
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
			moChannel.setData("testChVar", "18");
			moChannel.setData("testChVar2", "19");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			// setElementAttribute("value", "19");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2Available().", e);
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
			moChannel.setData("testChVar", "19");
			moChannel.setData("testChVar2", "18");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "19");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
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
			// moChannel.setData("testChVar", "17");
			moChannel.setData("testChVar2", "17");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			setElementAttribute("value", "19");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
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
			moChannel.setData("testChVar", "17");
			// moChannel.setData("testChVar2", "17");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable is set to a non Integer value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInValue() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172nonInteger");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable (value in channel at channel_variable's value key) is
	 * set to a non Integer value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInFirstChannelVar() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172NonIntegerValue");

			setElementAttribute("channel_variable", "testChVar");

			setElementAttribute("value", "172");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in second
	 * channel variable (value in channel at channel_variable2's value key) is
	 * set to a non Integer value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInChannelVar2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172");
			moChannel.setData("testChVar2", "172NonIntegerValue");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");

			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
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

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
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

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when Channel Object is
	 * null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSetScenerio2() throws ApplicationException
	{
		try
		{
			Expression loExpression = new Expression(moExpressionElement, null);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when neither 'Value'
	 * nor 'channel_variable2' attributes is set
	 * */
	@Test
	public void testEvaluateBusinessRuleEitherValueOrChannelVar3ExpectedButNotSet() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "");
			// moChannel.setData("testChVar2", "12");

			setElementAttribute("channel_variable1", "testChVar1");
			// setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "Integer");
			// setElementAttribute("value", "31");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable (value in channel at channel_variable's value key) is
	 * set to a non Integer value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInFirstChannelVarScenerio2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "172IntegerValue");

			setElementAttribute("channel_variable1", "testChVar1");

			setElementAttribute("value", "172");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

	/**
	 * This method tests for a Exception expectation when when value in first
	 * channel variable is set to a non Integer value.
	 * */
	@Test
	public void testEvaluateBusinessRuleForTypeMismatchInValueScenerio2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "2");

			setElementAttribute("channel_variable1", "testChVar1");

			setElementAttribute("value", "172nonInteger");
			Expression loExpression = new Expression(moExpressionElement, moChannel);

			IntegerLessThanHandler loIntegerLessThanHandler = new IntegerLessThanHandler();
			boolean loResult = (Boolean) loIntegerLessThanHandler.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnFalse().",
					e);
		}
	}

}
