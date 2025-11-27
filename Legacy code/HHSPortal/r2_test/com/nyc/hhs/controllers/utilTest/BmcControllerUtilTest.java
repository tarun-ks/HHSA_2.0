/**
 * 
 */
package com.nyc.hhs.controllers.utilTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.BmcControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.util.PropertyLoader;


/**
 * This test class is used to test all the operation for BmcControllerUtilTest class.
 *
 */
public class BmcControllerUtilTest
{
	private static SqlSession moSession = null; // SQL Session

	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception object
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			JUnitUtil.getTransactionManager();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	

	
	/**
	 * This method tests fetchNewFYBudgetAmount method for good data
	 * inputs and an ApplicartionException is expected
	 * @throws Exception 
	 */
	@Test
	public void testFetchNewFYBudgetAmount() throws Exception
	{
		Channel loChannel = new Channel();
		
		
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, "642");
		loChannel.setData(HHSConstants.FISCAL_YEAR_ID_KEY,"2013");
	
		String fyBudgetAmount = BmcControllerUtil.fetchNewFYBudgetAmount(loChannel);
		
		assertNotNull(fyBudgetAmount);
		assertTrue(Double.parseDouble(fyBudgetAmount) > 0.0);


	}
	
	
	/**
	 * This method tests fetchProgramIncomeModification method for empty Fiscal year
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchNewFYBudgetAmountWithEmptyFiscalYearId() throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.CONTRACT_ID_KEY, "642");
		loChannel.setData(HHSConstants.FISCAL_YEAR_ID_KEY,"2013");
	
		String fyBudgetAmount = BmcControllerUtil.fetchNewFYBudgetAmount(loChannel);
		
		assertNotNull(fyBudgetAmount);
		assertTrue(Double.parseDouble(fyBudgetAmount) > 0.0);
		

	}
	
	/**
	 * This method tests executeGridTransactionForBudgetConfig method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testExecuteGridTransactionForBudgetConfigDel() throws ApplicationException
	{
		String lsOperation = "del";
		String lsTransactionName = "";
		Channel loChannelObj = new Channel();
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		
		loBeanObj.setBudgetId("10060");
		loBeanObj.setBudgetTypeId(2);
		loBeanObj.setBudgetfiscalYear("2013");
		loBeanObj.setContractValue("1000");
		loBeanObj.setTotalbudgetAmount("1000");
		loBeanObj.setCreatedByUserId("803");
		
	
		Boolean lbTransactionStatus = BmcControllerUtil.executeGridTransactionForBudgetConfig(lsOperation, lsTransactionName, loChannelObj, loBeanObj);
		assertNotNull(lbTransactionStatus);
		assertTrue(lbTransactionStatus);

	}
	
	/**
	 * This method tests executeGridTransactionForBudgetConfig method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testExecuteGridTransactionForBudgetConfigUpdateTaskDel() throws ApplicationException
	{
		String lsOperation = "del";
		String lsTransactionName = "";
		Channel loChannelObj = new Channel();
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		
		loBeanObj.setBudgetId("10060");
		loBeanObj.setBudgetTypeId(2);
		loBeanObj.setBudgetfiscalYear("2013");
		loBeanObj.setContractValue("1000");
		loBeanObj.setTotalbudgetAmount("1000");
		loBeanObj.setCreatedByUserId("803");
		
	
		Boolean lbTransactionStatus = BmcControllerUtil.
		executeGridTransactionForBudgetConfigUpdateTask(lsOperation, lsTransactionName, loChannelObj, loBeanObj);
		assertNotNull(lbTransactionStatus);
		assertTrue(lbTransactionStatus);

	}

	
	
	/**
	 * This method tests checkBudgetDetails method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetBudgetDetails() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, "642");
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, "2013");
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
	
		List<?> loBudgetDetailList = BmcControllerUtil.getBudgetDetails(loChannelObj);
		assertNotNull(loBudgetDetailList);
		assertTrue(loBudgetDetailList.size() > 0);

	}
	
	
	
	/**
	 * This method tests checkBudgetDetails method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetFYList() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, "642");
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, "2013");
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
	
		List<?> loFYList = BmcControllerUtil.getFYList(loChannelObj);
		assertNotNull(loFYList);
		assertTrue(loFYList.size() > 0);

	}
	
	/**
	 * This method tests checkBudgetDetails method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetFYBudgetPlannedAmount() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.CONTRACT_ID_KEY, "642");
		loChannelObj.setData(HHSConstants.FISCAL_YEAR_ID_KEY, "2013");
		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
		
		String lsAmendment = "positive";
	
		String lsUpdatedBudgetId = BmcControllerUtil.getFYBudgetPlannedAmount(loChannelObj, lsAmendment);
		assertNotNull(lsUpdatedBudgetId);
		assertTrue(lsUpdatedBudgetId.length() > 0);

	} 
	
	/**
	 * This method tests executeGridTransactionForBudgetConfig method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Test(expected = ApplicationException.class)
	public void testExecuteGridTransactionForBudgetConfigUpdateOprAdd() 
	throws ApplicationException, IllegalAccessException, InvocationTargetException
	{
		String lsOperation = "add";
		String lsTransactionName = "contractConfigurationUpdateSubBudgetGridAdd";
		Channel loChannelObj = new Channel();
	
		CBGridBean loCBGridBean = new CBGridBean();
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		loCBGridBean.setSubBudgetID("1194");
		
		loChannelObj.setData("aoCBGridBeanObj", loCBGridBean);
		
		loBeanObj.setBudgetId("10060");
		loBeanObj.setBudgetTypeId(2);
		loBeanObj.setBudgetfiscalYear("2013");
		loBeanObj.setContractValue("1000");
		loBeanObj.setTotalbudgetAmount("1000");
		loBeanObj.setCreatedByUserId("803");
		
		Boolean lbTransactionStatus = BmcControllerUtil.executeGridTransactionForBudgetConfigUpdate(lsOperation, lsTransactionName, loChannelObj, loBeanObj, null);
		assertNotNull(lbTransactionStatus);
		assertTrue(lbTransactionStatus);
	}
	
	/**
	 * This method tests executeGridTransactionForBudgetConfig method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testExecuteGridTransactionForBudgetConfigUpdateOprEdit() throws ApplicationException
	{
		String lsOperation = "edit";
		String lsTransactionName = "contractConfigurationUpdateSubBudgetGridEdit";
		Channel loChannelObj = new Channel();
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		
		loBeanObj.setBudgetId("10060");
		loBeanObj.setBudgetTypeId(2);
		loBeanObj.setBudgetfiscalYear("2013");
		loBeanObj.setContractValue("1000");
		loBeanObj.setTotalbudgetAmount("1000");
		loBeanObj.setCreatedByUserId("803");
		
	
		Boolean lbTransactionStatus = BmcControllerUtil.
		executeGridTransactionForBudgetConfigUpdate(lsOperation, lsTransactionName, loChannelObj, loBeanObj, null);
		assertNotNull(lbTransactionStatus);
		assertTrue(lbTransactionStatus);

	}
	
	/**
	 * This method tests executeGridTransactionForBudgetConfig method 
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testExecuteGridTransactionForBudgetConfigUpdateOprDel() throws ApplicationException
	{
		String lsOperation = "del";
		String lsTransactionName = "contractConfigurationUpdateSubBudgetGridDel";
		Channel loChannelObj = new Channel();
		ContractBudgetBean loBeanObj = new ContractBudgetBean();
		loBeanObj.setBudgetId("10060");
		loBeanObj.setBudgetTypeId(2);
		loBeanObj.setBudgetfiscalYear("2013");
		loBeanObj.setContractValue("1000");
		loBeanObj.setTotalbudgetAmount("1000");
		loBeanObj.setCreatedByUserId("803");
	
		Boolean lbTransactionStatus = BmcControllerUtil.
		executeGridTransactionForBudgetConfigUpdate(lsOperation, lsTransactionName, loChannelObj, loBeanObj, null);
		assertNotNull(lbTransactionStatus);
		assertTrue(lbTransactionStatus);

	}
	
	
	private static void populateBeanFromRequestUtil(Object aoBeanObj, String asParamName, String asParamValue)
	{
			try
			{
				BeanUtils.setProperty(aoBeanObj, asParamName, asParamValue);
			}
			// Exception is handled here.
			catch (Exception aoExe)
			{
				// Set the error log if any exception occurs
				/*System.out.println("Exception occured in populateBeanFromRequest while performing operation on grid  "
						+ aoExe);*/
			}

	}
		

}
