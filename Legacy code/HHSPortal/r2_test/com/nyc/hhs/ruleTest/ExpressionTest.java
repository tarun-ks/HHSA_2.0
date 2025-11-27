package com.nyc.hhs.ruleTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jdom.Attribute;
import org.jdom.Element;
import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Expression;

public class ExpressionTest
{

	private Channel moChannel = new Channel();
	private String msValue=null;

	private Element getDummyChildExpressionElement()
	{
		Element loExpressionElement = new Element("expression");
		loExpressionElement.setAttribute("channel_variable", "testChVar");
		loExpressionElement.setAttribute("value", "helloTest");
		loExpressionElement.setAttribute("type", "String");
		loExpressionElement.setAttribute("method", "equals");

		return loExpressionElement;
	}

	@Test
	public void testEvaluateExpressionAllWell() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "or"));
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();

			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}

	@Test
	public void testEvaluateExpressionAllWellMultipleLevelExpressionWithOrOprator() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			Element loChildExpressionElement2 = getDummyChildExpressionElement();

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "or"));
			loExpressionElement.addContent(loChildExpressionElement);

			loChildExpressionElement2.setAttribute("value", "helloTest1");
			loExpressionElement.addContent(loChildExpressionElement2);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();

			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}

	@Test
	public void testEvaluateExpressionAllWellMultipleLevelExpressionWithAndOprator() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			Element loChildExpressionElement2 = getDummyChildExpressionElement();

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "and"));
			loExpressionElement.addContent(loChildExpressionElement);

			loChildExpressionElement2.setAttribute("value", "helloTest1");
			loExpressionElement.addContent(loChildExpressionElement2);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();

			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}

	@Test
	public void testEvaluateExpressionIfTypeNotDefined() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			loChildExpressionElement.removeAttribute("type");

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "or"));
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();
			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (ApplicationException loAe)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}
	
	@Test
	public void testExpression() throws ApplicationException
	{
		
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			loChildExpressionElement.removeAttribute("type");

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "or"));
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement);
	}

	@Test
	public void testEvaluateExpressionIfMethodNotDefined() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			loChildExpressionElement.removeAttribute("method");

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute(new Attribute("operator", "or"));
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();
			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (ApplicationException loAe)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}

	@Test
	public void testEvaluateExpressionIfTypeWronglyDefined() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			loChildExpressionElement.removeAttribute("type");
			loChildExpressionElement.setAttribute("type", "Stingssssss");

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute("operator", "or");
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();
			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());
			

		}
		catch (ApplicationException loAe)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}

	@Test
	public void testEvaluateExpressionIfMethodWronglyDefined() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			moChannel.setData("testChVar2", "helloTest");

			Element loChildExpressionElement = getDummyChildExpressionElement();
			loChildExpressionElement.removeAttribute("method");
			loChildExpressionElement.setAttribute("method", "equalsssssss");

			Element loExpressionElement = new Element("expression");
			loExpressionElement.setAttribute("operator", "or");
			loExpressionElement.addContent(loChildExpressionElement);

			Expression loExpression = new Expression(loExpressionElement, moChannel);
			loExpression.evaluate();
			assertNotNull(loExpression.isResult());
			assertTrue(loExpression.isResult());

		}
		catch (ApplicationException loAe)
		{
			assertTrue(true);
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured while Unit Testing - testEvaluateExpression().", aoEx);
		}

	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception as ContractId is null in loTaskDetailsBean
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testExceptionEvaluateRuleWithRuleIdAndWithChanVarScenerio() throws ApplicationException
	{
		moChannel.setData("testChVar", "helloTest");
		moChannel.setData("testChVar2", "helloTest");
		new Expression(null, "expression");
	}
	
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception as ContractId is null in loTaskDetailsBean
	 * @throws ApplicationException
	 */
	@Test
	public void testGetValue()throws Exception
	{
		Element loExpressionElement = new Element("expression");
		Expression loExpression = new Expression(loExpressionElement, moChannel);
		loExpression.getValue();
	}
	
	@Test
	public void testGetType()throws Exception
	{
		Element loExpressionElement = new Element("expression");
		Expression loExpression = new Expression(loExpressionElement, moChannel);
		loExpression.getType();
	}
	@Test
	public void testGetHandlerName()throws Exception
	{
		Element loExpressionElement = new Element("expression");
		Expression loExpression = new Expression(loExpressionElement, moChannel);
		loExpression.getHandlerName();
	}
	@Test
	public void testGetMethod()throws Exception
	{
		Element loExpressionElement = new Element("expression");
		Expression loExpression = new Expression(loExpressionElement, moChannel);
		loExpression.getMethod();
	}
	@Test
	public void testGetOperator()throws Exception
	{
		Element loExpressionElement = new Element("expression");
		Expression loExpression = new Expression(loExpressionElement, moChannel);
		loExpression.getOperator();
	}
	
	
	

}
