/**
 * 
 */
package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.ContractBudgetControllerUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.junit.util.JUnitUtil;

/**
 * @author naman.chopra
 * 
 */
public class ContractBudgetControllerUtilsTest
{

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getlsJspPath(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testGetlsJspPath() throws ApplicationException
	{
		String asPrintView = HHSConstants.PRINTER_VIEW;
		String asActionReqParam = HHSConstants.CONTRACT_BUDGET_REVIEW_TASK;
		String lsJspPath = ContractBudgetControllerUtils.getlsJspPath(asPrintView, asActionReqParam);
		assertTrue(null != lsJspPath);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getlsJspPath(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testPositiveGetlsJspPath() throws ApplicationException
	{
		String asPrintView = HHSConstants.CONTRACT_BUDGET_REVIEW_TASK;
		String asActionReqParam = HHSConstants.CONTRACT_BUDGET_REVIEW_TASK;
		String lsJspPath = ContractBudgetControllerUtils.getlsJspPath(asPrintView, asActionReqParam);
		assertTrue(null != lsJspPath);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getlsJspPath(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNextGetlsJspPath() throws ApplicationException
	{
		String asPrintView = null;
		String asActionReqParam = HHSConstants.CONTRACT_BUDGET_REVIEW_TASK;
		String lsJspPath = ContractBudgetControllerUtils.getlsJspPath(asPrintView, asActionReqParam);
		assertTrue(null != lsJspPath);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getlsJspPath(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeGetlsJspPath() throws ApplicationException
	{
		String asPrintView = null;
		String asActionReqParam = null;
		String lsJspPath = ContractBudgetControllerUtils.getlsJspPath(asPrintView, asActionReqParam);
		assertTrue(null != lsJspPath);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getlsJspPath(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeActionGetlsJspPath() throws ApplicationException
	{
		String asPrintView = HHSConstants.CONTRACT_BUDGET_REVIEW_TASK;
		String asActionReqParam = null;
		String lsJspPath = ContractBudgetControllerUtils.getlsJspPath(asPrintView, asActionReqParam);
		assertTrue(null != lsJspPath);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#validateBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeValidateBudgetStatus() throws ApplicationException
	{
		String asBudgetId = "555";
		Map<String, String> loMap = ContractBudgetControllerUtils.validateBudgetStatus(asBudgetId);
		assertTrue(null != loMap);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#validateBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testPositiveValidateBudgetStatus() throws ApplicationException
	{
		String asBudgetId = "561";
		Map<String, String> loMap = ContractBudgetControllerUtils.validateBudgetStatus(asBudgetId);
		assertTrue(null != loMap);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#validateBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNextValidateBudgetStatus() throws ApplicationException
	{
		String asBudgetId = "562";
		Map<String, String> loMap = ContractBudgetControllerUtils.validateBudgetStatus(asBudgetId);
		assertTrue(null != loMap);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#validateBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateBudgetStatus() throws ApplicationException
	{
		String asBudgetId = "55555";
		Map<String, String> loMap = ContractBudgetControllerUtils.validateBudgetStatus(asBudgetId);
		assertTrue(null != loMap);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#fetchErrorMsg(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchErrorMsg() throws ApplicationException
	{
		String aoProperty = HHSConstants.PROPERTIES_STATUS_CONSTANT;
		String aoReturn = HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION;

		String lsError = ContractBudgetControllerUtils.fetchErrorMsg(aoProperty, aoReturn);
		assertTrue(null != lsError);
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#fetchErrorMsg(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeFetchErrorMsg() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			String aoProperty = null;
			String aoReturn = HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION;
			ContractBudgetControllerUtils.fetchErrorMsg(aoProperty, aoReturn);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#fetchErrorMsg(java.lang.String, java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeNextFetchErrorMsg() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			String aoProperty = HHSConstants.PROPERTIES_STATUS_CONSTANT;
			String aoReturn = null;
			ContractBudgetControllerUtils.fetchErrorMsg(aoProperty, aoReturn);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#fetchCurrentBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchCurrentBudgetStatus() throws ApplicationException
	{
		String asBudgetId = "55555";
		String lsStatus = ContractBudgetControllerUtils.fetchCurrentBudgetStatus(asBudgetId);
		assertTrue(null != lsStatus);

	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#fetchCurrentBudgetStatus(java.lang.String)}
	 * .
	 * @throws ApplicationException
	 */
	@Test
	public void testNegativeFetchCurrentBudgetStatus() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			String asBudgetId = null;
			String lsStatus = ContractBudgetControllerUtils.fetchCurrentBudgetStatus(asBudgetId);
			assertTrue(null != lsStatus);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getbudgetStatus(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetbudgetStatus()
	{
		boolean lbThrown = false;
		try
		{
			String asBudgetId = "561";
			String lsStatus = ContractBudgetControllerUtils.getbudgetStatus(asBudgetId);
			assertTrue(null != lsStatus);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getbudgetStatus(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetbudgetStatusPositive()
	{
		boolean lbThrown = false;
		try
		{
			String asBudgetId = "83";
			String lsStatus = ContractBudgetControllerUtils.getbudgetStatus(asBudgetId);
			assertTrue(lsStatus == HHSConstants.FALSE);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getbudgetStatus(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetbudgetStatusPositive2()
	{
		boolean lbThrown = false;
		try
		{
			String asBudgetId = "84";
			String lsStatus = ContractBudgetControllerUtils.getbudgetStatus(asBudgetId);
			assertTrue(lsStatus == HHSConstants.FALSE);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Test method for
	 * {@link com.nyc.hhs.controllers.util.ContractBudgetControllerUtils#getbudgetStatus(java.lang.String)}
	 * .
	 */
	@Test
	public void testNegativeGetbudgetStatus()
	{
		boolean lbThrown = false;
		try
		{
			String asBudgetId = null;
			String lsStatus = ContractBudgetControllerUtils.getbudgetStatus(asBudgetId);
			assertTrue(null != lsStatus);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

}
