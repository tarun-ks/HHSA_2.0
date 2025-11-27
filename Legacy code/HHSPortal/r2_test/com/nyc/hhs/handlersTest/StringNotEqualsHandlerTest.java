package com.nyc.hhs.handlersTest;

import static org.junit.Assert.assertTrue;

import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.handlers.StringEqualsHandler;
import com.nyc.hhs.handlers.StringNotEqualsHandler;
import com.nyc.hhs.rule.Expression;

/**
 * This is a test class for StringNotEqualsHandler class
 * 
 */
public class StringNotEqualsHandlerTest {

	private Channel moChannel = new Channel();
	private Element moExpressionElement = new Element("expression");

	private void setElementAttribute(String asAttributeName,
			String aoAttributeValue) {
		moExpressionElement.setAttribute(new Attribute(asAttributeName,
				aoAttributeValue));
	}

	/**
	 * This method tests for a true return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) does not equal the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnTrue()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTestDifferent");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleReturnTrue().",
					aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) equals the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnFalse()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleReturnFalse().",
					aoEx);
		}
	}

	/**
	 * This method tests for a true return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and and their values(in channel)
	 * does not equal.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnTrue()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTestDifferent");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			// setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnTrue().",
					aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and and their values(in channel)
	 * are equal.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2AvailableReturnFalse()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		} catch (Exception aoEx) {
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
	public void testEvaluateBusinessRuleChannelVarExpectedButNotSet()
			throws ApplicationException {
		try {
			// moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			// setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException aoAppEx) {
			assertTrue(true);
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVarExpectedButNotSet().",
					aoEx);
		}
	}

	/**
	 * This method tests for a Exception expectation when when
	 * 'channel_variable2' attributes is not set or equals to a blank
	 * considering the value attribute is also not available for comparison.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar2ExpectedButNotSet()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			// moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			// setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException aoAppEx) {
			assertTrue(true);
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2ExpectedButNotSet().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when
	 * Channel Object is null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSet()
			throws ApplicationException {
		try {
			Expression loExpression = new Expression(moExpressionElement,
					null);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		}
		catch (ApplicationException aoAppEx) {
			assertTrue(true);
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelExpectedButNotSet().",
					aoEx);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when neither 'Value' nor
	 * 'channel_variable2' attributes is set
	 * */
	@Test
	public void testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			// moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar");
			//setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			//setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException aoAppEx) {
			assertTrue(true);
		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet().",
					aoEx);
		}
	}
	/**
	 * This method tests for a true return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) does not equal the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnTrueCase2()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTestDifferent");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleReturnTrue().",
					e);
		}
	}
	/**
	 * This method tests for a Exception expectation when when
	 * Channel Object is null
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelExpectedButNotSetCase2()
			throws ApplicationException {
		try {
			Expression loExpression = new Expression(moExpressionElement,
					null);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException e) {
			assertTrue(true);
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelExpectedButNotSet().",
					e);
		}
	}
	
	/**
	 * This method tests for a Exception expectation when when neither 'Value' nor
	 * 'channel_variable2' attributes is set
	 * */
	@Test
	public void testEvaluateBusinessRuleEitherValueOrChannelVar3ExpectedButNotSet()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			// moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar1");
			//setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			//setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException e) {
			assertTrue(true);
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleEitherValueOrChannelVar2ExpectedButNotSet().",
					e);
		}
	}
	/**
	 * This method tests for a true return when 'channel_variable' and
	 * 'channel_variable2' attributes are set and and their values(in channel)
	 * does not equal.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVar3AvailableReturnTrue()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTestDifferent1");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable2", "testChVar2");
			setElementAttribute("type", "String");
			// setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVar2AvailableReturnTrue().",
					e);
		}
	}
	/**
	 * This method tests for a Exception expectation when when
	 * 'channel_variable' attributes is either not set or equals to a blank.
	 * */
	@Test
	public void testEvaluateBusinessRuleChannelVarExpectedButNotSetCase2()
			throws ApplicationException {
		try {
			// moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("channel_variable3", "testChVar2");
			setElementAttribute("type", "String");
			// setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (ApplicationException e) {
			assertTrue(true);
		} catch (Exception e) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleChannelVarExpectedButNotSet().",
					e);
		}
	}
	/**
	 * This method tests for a true return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) does not equal the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnTrueCase3()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTestDifferent");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(loResult);

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleReturnTrue().",
					aoEx);
		}
	}

	/**
	 * This method tests for a false return when 'channel_variable' attribute is
	 * set and it's value(in channel variable) equals the value set in
	 * expression's 'value' attribute.
	 * */
	@Test
	public void testEvaluateBusinessRuleReturnFalseCase2()
			throws ApplicationException {
		try {
			moChannel.setData("testChVar", "helloTest");
			setElementAttribute("channel_variable", "testChVar");
			setElementAttribute("type", "String");
			setElementAttribute("value", "helloTest");
			Expression loExpression = new Expression(moExpressionElement,
					moChannel);

			StringNotEqualsHandler loStringNotEqualsHandler = new StringNotEqualsHandler();
			boolean loResult = (Boolean) loStringNotEqualsHandler
					.evaluateBusinessRule(loExpression);
			assertTrue(!loResult);

		} catch (Exception aoEx) {
			throw new ApplicationException(
					"Exception occured while Unit Testing - testEvaluateBusinessRuleReturnFalse().",
					aoEx);
		}
	}



}
