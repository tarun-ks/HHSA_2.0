package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.batch.impl.PDFGenerationBatch;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetAmendmentService;
import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * @author faiyaz.asharaf
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ContractBudgetAmendmentServiceTest
{

	private static SqlSession moSession = null; // SQL Session
	


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
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			String lsClassName = (new PDFGenerationBatch()).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;
			String lsCastorPath = ((new PDFGenerationBatch()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING).replace(
					HHSConstants.PDF_CLASS, HHSConstants.CASTOR_MAPPING);

			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
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
			moSession.rollback();
			moSession.close();			
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	ContractBudgetAmendmentService moContractBudgetAmendmentService = new ContractBudgetAmendmentService();

	/**
	 * This method tests mergeCancelledAmendmentBudget method for true condition
	 */
	@Test
	public void testMergeCancelledAmendmentBudget() throws ApplicationException
	{	
	
		boolean lbStatus= moContractBudgetAmendmentService.mergeCancelledAmendmentBudget(moSession, "11180", true);
		//assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	
	/**
	 * This method tests mergeCancelledAmendmentBudget method for false condition
	 */
	@Test(expected= ApplicationException.class)
	public void testMergeCancelledAmendmentBudget1() throws ApplicationException
	{	
	
		boolean lbStatus= moContractBudgetAmendmentService.mergeCancelledAmendmentBudget(moSession, "11180", false);
		//assertNotNull(lbStatus);
		assertFalse(lbStatus);
	}
	
	@Test
	public void testAddProgramIncomeAmendment1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbInsertStatus = false;
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56779");
		aoCBProgramIncomeBean.setContractBudgetID("10730");
		aoCBProgramIncomeBean.setSubBudgetID("14124");
		aoCBProgramIncomeBean.setEmpPosition("1");
		aoCBProgramIncomeBean.setAmendmentAmount("500");
		aoCBProgramIncomeBean.setCreatedByUserId("agency_14");
		aoCBProgramIncomeBean.setModifiedByUserId("system");
	    aoCBProgramIncomeBean.setActiveFlag("1");
	    aoCBProgramIncomeBean.setDescription("");
	    aoCBProgramIncomeBean.setEntryTypeId("10");
		try
		{
			 lbInsertStatus =  loContractBudgetAmendmentService.addProgramIncomeAmendment(moSession, aoCBProgramIncomeBean);
			assertTrue(lbInsertStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testAddProgramIncomeAmendment2() throws ApplicationException
	{
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		boolean lbInsertStatus = false;
		boolean lbStatus = loContractBudgetAmendmentService.addProgramIncomeAmendment(null, aoCBProgramIncomeBean);
	}
	
	@Test(expected=ApplicationException.class)
	public void testAddProgramIncomeAmendment3() throws ApplicationException
	{
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		boolean lbInsertStatus = false;
		aoCBProgramIncomeBean.setProgramIncomeId("");
		aoCBProgramIncomeBean.setContractBudgetID("");
		boolean lbStatus = loContractBudgetAmendmentService.addProgramIncomeAmendment(moSession, aoCBProgramIncomeBean);
	}
	

	
}
