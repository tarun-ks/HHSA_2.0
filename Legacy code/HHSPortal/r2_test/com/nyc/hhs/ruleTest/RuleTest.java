package com.nyc.hhs.ruleTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.rule.Rule;

public class RuleTest
{

	public Channel moChannel = new Channel();

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and valid Channel variable set in Channel
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVar() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVarString", "helloTest");
			String lsRuleId = "testRuleSet1";

			String lsExpectedResult = "Hello Test";

			Object loResult = Rule.evaluateRule(lsRuleId, moChannel);
			assertNotNull(loResult);
			assertTrue(loResult.equals(lsExpectedResult));

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithChanVar() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with valid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithoutChanVar() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleSet1";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithoutChanVar() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);
		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and Channel variable set but business
	 * rule is not defined in XML (business-rule.xml) - Desired result would be
	 * an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarButInvalidRule() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleId2WithoutValueOrAltRuleSet";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and valid Channel variable set in Channel
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarScenerio2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVarString", "helloTest");
			String lsRuleId = "testRuleSet1";

			String lsExpectedResult = "Hello Test";

			Object loResult = Rule.evaluateRule(lsRuleId, moChannel);
			assertNotNull(loResult);
			assertTrue(loResult.equals(lsExpectedResult));

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithChanVarScenerio2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with valid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithoutChanVarScenerio2() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleSet1";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithoutChanVarScenerio2() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);
		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and Channel variable set but business
	 * rule is not defined in XML (business-rule.xml) - Desired result would be
	 * an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarButInvalidRuleScenerio2() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleId2WithoutValueOrAltRuleSet";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and valid Channel variable set in Channel
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarScenerio3() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVarString", "helloTest");
			String lsRuleId = "testRuleSet1";

			String lsExpectedResult = "Hello Test";

			Object loResult = Rule.evaluateRule(lsRuleId, moChannel);
			assertNotNull(loResult);
			assertTrue(loResult.equals(lsExpectedResult));

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithChanVarScenerio3() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with valid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithoutChanVarScenerio3() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleSet1";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithoutChanVarScenerio3() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);
		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and Channel variable set but business
	 * rule is not defined in XML (business-rule.xml) - Desired result would be
	 * an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarButInvalidRuleScenerio3() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleId2WithoutValueOrAltRuleSet";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and valid Channel variable set in Channel
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarScenerio4() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVarString", "helloTest");
			String lsRuleId = "testRuleSet1";

			String lsExpectedResult = "Hello Test";

			Object loResult = Rule.evaluateRule(lsRuleId, moChannel);
			assertNotNull(loResult);
			assertTrue(loResult.equals(lsExpectedResult));

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithChanVarScenerio4() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with valid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithoutChanVarScenerio4() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleSet1";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with valid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleFOrTLDUsage() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleSet1";
			Rule.evaluateRule(lsRuleId, moChannel, true);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with an Invalid RuleId and Channel variable not set - Desired
	 * result would be an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithoutRuleIdAndWithoutChanVarScenerio4() throws ApplicationException
	{
		try
		{
			// moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "";
			Rule.evaluateRule(lsRuleId, moChannel);
		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithoutChanVar()");
		}
	}

	/**
	 * This method will test for evaluation of the business rule defined in the
	 * ruleset.xml with a valid RuleId and Channel variable set but business
	 * rule is not defined in XML (business-rule.xml) - Desired result would be
	 * an ApplicationException thrown
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testEvaluateRuleWithRuleIdAndWithChanVarButInvalidRuleScenerio4() throws ApplicationException
	{
		try
		{
			moChannel.setData("testChVar", "helloTest");
			String lsRuleId = "testRuleId2WithoutValueOrAltRuleSet";
			Rule.evaluateRule(lsRuleId, moChannel);

		}
		catch (ApplicationException aoAe)
		{
			assertTrue(true);
		}
		catch (Exception aoAe)
		{
			throw new ApplicationException("Unit Test Failed - testEvaluateRuleWithoutRuleIdAndWithChanVar()");
		}
	}

}