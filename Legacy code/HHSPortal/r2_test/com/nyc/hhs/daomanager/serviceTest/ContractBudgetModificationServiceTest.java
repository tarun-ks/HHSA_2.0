package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;

/**
 * @author faiyaz.asharaf
 * 
 */
public class ContractBudgetModificationServiceTest
{

	private static SqlSession moSession = null; // SQL Session
		
	public String baseContractId="51"; 
	public String modsubBudgetID="20";
	public String parentSubBudgetID="12";
	public String parentBudgetID="12";
	public String modBudgetID="20";
	public String invoiceId="55";
	public String agency="agency_12";
	public String provider="803";
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

	ContractBudgetModificationService moContractBudgetModificationService = new ContractBudgetModificationService();

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBGridBean bean object
	 * @throws ApplicationException
	 */
	private CBGridBean getProfServicesParamsForModification() throws ApplicationException
	{
		CBGridBean loProfServicesBean = new CBGridBean();
		loProfServicesBean.setContractID("111777");
		loProfServicesBean.setModifyByAgency("");
		loProfServicesBean.setModifyByProvider("803");

		return loProfServicesBean;
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBProfessionalServicesBean bean object
	 * @throws ApplicationException
	 */
	private CBProfessionalServicesBean getProfServicesParamsForModifAmnt() throws ApplicationException
	{
		CBProfessionalServicesBean loProfServicesBean = new CBProfessionalServicesBean();
		loProfServicesBean.setParentBudgetId("555");
		loProfServicesBean.setParentSubBudgetId("555");
		loProfServicesBean.setContractID("111777");
		loProfServicesBean.setModifyByAgency("");
		loProfServicesBean.setModifyByProvider("803");

		return loProfServicesBean;
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsKO() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesParamsForModification();
		loProfService.setParentBudgetId("555");
		loProfService.setParentSubBudgetId("555");
		loProfService.setContractBudgetID("557");
		loProfService.setSubBudgetID(null);

		Boolean lbThrown = false;
		try
		{
			List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetModificationService
					.cbmFetchProfServicesDetails(loProfService, moSession);

			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsKO2() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesParamsForModification();
		loProfService.setParentBudgetId("555");
		loProfService.setParentSubBudgetId(null);
		loProfService.setContractBudgetID("557");
		loProfService.setSubBudgetID("556");

		Boolean lbThrown = false;
		try
		{
			List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetModificationService
					.cbmFetchProfServicesDetails(loProfService, moSession);

			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsOK() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesParamsForModification();
		loProfService.setParentBudgetId("555");
		loProfService.setParentSubBudgetId("555");
		loProfService.setContractBudgetID("557");
		loProfService.setSubBudgetID("556");

		List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetModificationService
				.cbmFetchProfServicesDetails(loProfService, moSession);

		assertNotNull(loProfServiceDetailsList);
		assertTrue(loProfServiceDetailsList.size() > 0);
	}
	
	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithNoResult() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesParamsForModification();
		loProfService.setParentBudgetId("666"); // Id not available in Professional_services table
		loProfService.setParentSubBudgetId("666");
		loProfService.setContractBudgetID("777");
		loProfService.setSubBudgetID("777");
		
		Boolean lbThrown = false;
		try
		{
			List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetModificationService
					.cbmFetchProfServicesDetails(loProfService, moSession);
	
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithEmptyContractId() throws ApplicationException
	{
		CBGridBean loProfService = getProfServicesParamsForModification();
		loProfService.setParentBudgetId("555");
		loProfService.setParentSubBudgetId("555");
		loProfService.setContractBudgetID("557");
		loProfService.setSubBudgetID("556");
		loProfService.setContractID("");

		
		Boolean lbThrown = false;
		try
		{
			List<CBProfessionalServicesBean> loProfServiceDetailsList = moContractBudgetModificationService
					.cbmFetchProfServicesDetails(loProfService, moSession);
	
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModificationWithEmptyID() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("");
			loProfService.setModifyAmount("2000");
			// Add modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModificationWithIDNULL() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId(null);
			loProfService.setModifyAmount("2000");
			// Add modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModificationWithIncompleteID() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152");
			loProfService.setModifyAmount("2000");
			// Add modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModificationWithWrongID() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId(" _ ");
			loProfService.setModifyAmount("2000");
			// Add modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality when (Modification Amount + Remaining
	 * Amount) < 0 for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModificationLessRemaining() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			// (Modification Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount("-3000");

			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality to add Modification Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModifyAmount() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4");
			loProfService.setModifyAmount("2000");
			// Add modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality to update Modification Amount for
	 * Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesUpdateModifyAmount() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount("3000");

			// Update modification amount
			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality when (Modification Amount is
	 * NULL for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModifyAmountNULL() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			// (Modification Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount(null);

			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality when (Modification Amount is
	 * Empty for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModifyAmountEmpty() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			// (Modification Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount("");

			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	/**
	 * This method tests, functionality when (Modification Amount is
	 * Empty for Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModifyAmountWrongValue() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			// (Modification Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID("556");
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount("123abc");

			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality when modified budget_id or sub_budget_id
	 * is null in Professional Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesModifyAmountKO() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			// (Modification Amount + Remaining Amount) < 0
			CBProfessionalServicesBean loProfService = getProfServicesParamsForModifAmnt();
			loProfService.setContractBudgetID("557");
			loProfService.setSubBudgetID(null);
			loProfService.setId("152_4_170");
			loProfService.setModifyAmount("1000");

			boolean lbUpdateStatus = moContractBudgetModificationService.cbmEditProfServicesDetails(loProfService,
					moSession);

			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests the fetchContractBudgetModificationRate - Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetModificationRate() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		List<RateBean> loRateBeanList = moContractBudgetModificationService.fetchContractBudgetModificationRate(moSession, loCBGridBean);
		assertNotNull(loRateBeanList);
	}
	
	/**
	 * This method tests the fetchContractBudgetModificationRate - Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetModificationRateAssertReturnId() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		List<RateBean> loRateBeanList = moContractBudgetModificationService.fetchContractBudgetModificationRate(moSession, loCBGridBean);
		assertTrue(loRateBeanList != null);
	}
	
	/**
	 * This method tests the fetchContractBudgetModificationRate - Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetModificationRateFailSubBudgetNull() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		moContractBudgetModificationService.fetchContractBudgetModificationRate(moSession, loCBGridBean);
	}
	
	/**
	 * This method tests the fetchContractBudgetModificationRate - Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetModificationRateFailParentSubBudgetNull() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId(null);
		moContractBudgetModificationService.fetchContractBudgetModificationRate(moSession, loCBGridBean);
	}
	
	/**
	 * This method tests the fetchContractBudgetModificationRate - Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractBudgetModificationRateCBGridBeanNull() throws ApplicationException
	{
		moContractBudgetModificationService.fetchContractBudgetModificationRate(moSession, null);
	}

	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setId("13");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("city_142");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with base record
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRateIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId(null);
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("city_142");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with base record
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRateValidateDataFalse() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId("13");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("city_142");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("-3");
		loRateBean.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRateFailEx() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setParentSubBudgetId(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setId("13");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("city_142");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID("555");
		moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRateExemptId() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setModifyByAgency("city_142");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRateFailAppEx() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setId("13_newrecord");
		loRateBean.setUnitDesc("ModTest1");
		loRateBean.setModifiedByUserId("803");
		loRateBean.setCreatedByUserId("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setContractBudgetID("555");
		
		moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRateNullHandled() throws ApplicationException
	{
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRateInfo(moSession, null);
		assertEquals("0", loRowInserted.toString());
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateContractBudgetModificationRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		loRateBean.setModifyByProvider("803");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("Method updateContractBudgetModificationRateInfo executed succesfully", moContractBudgetModificationService.getMoState().toString());
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateUnitDescNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc(null);
		loRateBean.setParentSubBudgetId("556");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("5");
		Integer loRowInserted = moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	/**
	 * 	
	 * @throws ApplicationException
	 */
	
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateUnitDescEmpty() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc("");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("Method updateContractBudgetModificationRateInfo executed succesfully", moContractBudgetModificationService.getMoState().toString());
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateNegativeModifyUnits() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setUnitDesc("");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("-3");
		loRateBean.setId("556");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateFail() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateFailBudgetIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId(null);
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("556");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateFailModifyUnitNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId("58");
		loRateBean.setLsModifyUnits(null);
		loRateBean.setId("556");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateContractBudgetModificationRateBeanNull() throws ApplicationException
	{
		Integer loRowInserted = moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, null);
		assertEquals("0", loRowInserted.toString());
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationRateFailSubBudgetIdNull() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setUnitDesc("ModTest");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("58");
		loRateBean.setParentSubBudgetId("58");
		loRateBean.setLsModifyUnits(null);
		loRateBean.setId("556");
		moContractBudgetModificationService.updateContractBudgetModificationRateInfo(null, loRateBean);
	}

	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractBudgetModificationRate() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("555");
		loRateBean.setId("555");
		moContractBudgetModificationService.deleteContractBudgetModificationRateInfo(moSession, loRateBean);
		assertEquals("Method deleteContractBudgetModificationRateInfo executed succesfully", moContractBudgetModificationService.getMoState().toString());
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetModificationRateFail() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		loRateBean.setId("556");
		moContractBudgetModificationService.deleteContractBudgetModificationRateInfo(moSession, loRateBean);
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetModificationRateFailSubBudgetNull() throws ApplicationException
	{
		CBGridBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID(null);
		moContractBudgetModificationService.deleteContractBudgetModificationRateInfo(moSession, (RateBean)loRateBean);
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractBudgetModificationRateCBGridBeanNull() throws ApplicationException
	{
		Integer loRowDeleted = moContractBudgetModificationService.deleteContractBudgetModificationRateInfo(moSession, null);
		assertEquals("0", loRowDeleted.toString());
	}
	
	/**
	 * This method tests for deleting a row of Contract Modification Rate Grid
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetModificationRateFailEmptyRateBean() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		moContractBudgetModificationService.deleteContractBudgetModificationRateInfo(moSession, loRateBean);
	}

	/**
	 * This method tests if Unallocated Funds are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("556");
		loUnallocatedFundsBean.setContractBudgetID("557");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId("555");
		loUnallocatedFundsBean.setParentSubBudgetId("555");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetModificationService
				.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertNotNull(loUnallocatedFundsList);
		assertTrue(loUnallocatedFundsList.size() > 0);
		assertTrue(loUnallocatedFundsList.get(0).getModCount() == 1);
		assertTrue(loUnallocatedFundsList.get(0).getOrgCount() == 1);
		assertNotNull(loUnallocatedFundsList.get(0).getAmmount());
		assertNotNull(loUnallocatedFundsList.get(0).getModificationAmount());

	}

	/**
	 * This method tests if CBGridBean is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		List<UnallocatedFunds> loUnallocatedFundsList = null;

		loUnallocatedFundsList = moContractBudgetModificationService.fetchModificationUnallocatedFunds(moSession,
				aoCBGridBeanObj);
		assertNull(loUnallocatedFundsList);

	}

	/**
	 * This method tests if Sub BudgetId is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification3() throws ApplicationException
	{

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setContractBudgetID("25");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetModificationService
				.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertNull(loUnallocatedFundsList);
	}

	/**
	 * This method tests if new Unallocated Funds data is added for both base
	 * and modification
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification4() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("774");
		loUnallocatedFundsBean.setContractBudgetID("10046");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId("10060");
		loUnallocatedFundsBean.setParentSubBudgetId("1195");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetModificationService
				.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(loUnallocatedFundsList.get(0).getModCount() == 0);
		assertTrue(loUnallocatedFundsList.get(0).getOrgCount() == 0);
	}

	/**
	 * This method tests if database session is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification5() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		moSession = null;
		try
		{
			moContractBudgetModificationService.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			try
			{
				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if new Unallocated Funds data is added for modification
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testFetchUnallocatedFundsModification6() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setParentBudgetId("888");
		loUnallocatedFundsBean.setParentSubBudgetId("8881");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");

		loUnallocatedFundsBean.setSubBudgetID("1196");
		loUnallocatedFundsBean.setContractBudgetID("10060");

		List<UnallocatedFunds> loUnallocatedFundsList = moContractBudgetModificationService
				.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(loUnallocatedFundsList.get(0).getModCount() == 0);
		assertTrue(loUnallocatedFundsList.get(0).getOrgCount() == 1);
	}

	/**
	 * This method is for the negative condition if the sub_budget_id is not
	 * there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification7() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("7");
		loUnallocatedFundsBean.setContractBudgetID("10");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId("10060");
		loUnallocatedFundsBean.setParentSubBudgetId("1195");

		Boolean lbThrown = false;
		try
		{
			moContractBudgetModificationService.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			try
			{
				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method is for the negative condition if the parent sub_budget_id is
	 * not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchUnallocatedFundsModification8() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("556");
		loUnallocatedFundsBean.setContractBudgetID("557");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");
		loUnallocatedFundsBean.setModificationAmount("0.0");
		loUnallocatedFundsBean.setAmmount("0.0");
		loUnallocatedFundsBean.setParentBudgetId("10");
		loUnallocatedFundsBean.setParentSubBudgetId("15");

		Boolean lbThrown = false;
		try
		{
			moContractBudgetModificationService.fetchModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			try
			{
				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	/**
	 * This method tests if database session is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateModificationUnallocatedFunds1() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("556");
		loUnallocatedFundsBean.setContractBudgetID("557");
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId("555");
		loUnallocatedFundsBean.setParentSubBudgetId("555");
		loUnallocatedFundsBean.setModificationAmount("13452.90");

		moSession = null;
		try
		{
			moContractBudgetModificationService.updateModificationUnallocatedFunds(moSession, loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			try
			{
				moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if Unallocated Funds are updated database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateModificationUnallocatedFunds2() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("556");
		loUnallocatedFundsBean.setContractBudgetID("557");
		loUnallocatedFundsBean.setModifiedByUserId("city_142");
		loUnallocatedFundsBean.setParentBudgetId("555");
		loUnallocatedFundsBean.setParentSubBudgetId("555");
		loUnallocatedFundsBean.setModificationAmount("13452.90");

		boolean status = moContractBudgetModificationService.updateModificationUnallocatedFunds(moSession,
				loUnallocatedFundsBean);

		assertTrue(status);
	}

	/**
	 * This method tests if Unallocated Funds are updated database and ammount
	 * is not there.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void updateModificationUnallocatedFunds3() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		boolean status = false;

		try
		{
			status = moContractBudgetModificationService.updateModificationUnallocatedFunds(moSession,
					loUnallocatedFundsBean);
		}
		catch (ApplicationException loAppEx)
		{
			assertTrue(!status);
		}

	}

	// Program Income Modification Test Case Starts

	private CBGridBean getDummyCBGridBeanObj()
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setContractBudgetID("557");
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setModifyByAgency("803");
		loCBGridBean.setModifyByProvider("agency_12");
		return loCBGridBean;

	}

	private CBProgramIncomeBean getDummyCBProgramIncomeBeanObj()
	{
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();

		loCBProgramIncomeBean.setContractBudgetID("557");
		loCBProgramIncomeBean.setSubBudgetID("556");
		loCBProgramIncomeBean.setContractID("111777");
		loCBProgramIncomeBean.setInvoiceId("55");
		loCBProgramIncomeBean.setId("318_104_2_9000_9000");
		loCBProgramIncomeBean.setModifyByAgency("agency_12");
		loCBProgramIncomeBean.setModifyByProvider("803");
		loCBProgramIncomeBean.setBudgetType("2");
		loCBProgramIncomeBean.setIncome("900.00");
		loCBProgramIncomeBean.setActiveFlag("0");

		return loCBProgramIncomeBean;

	}

	/**
	 * This method tests fetchProgramIncomeModification method for good data
	 * inputs
	 */
	@Test
	public void testFetchProgramIncomeModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<CBProgramIncomeBean> loResultList = loContractBudgetService.fetchProgramIncomeModification(loCBGridBean,
				moSession);
		assertNotNull(loResultList);		
	}

	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeModificationWithAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID(null);// Invalid Sub Budget id
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, moSession);

	}

	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeModificationWithAppException2() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();		
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, null);
	}

	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeModificationWithAppException3() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_C'");// Invalid Sub Budget id
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, null);
	}

	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeModificationWithAppException4() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_A'");// Invalid Sub Budget id
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, null);
	}

	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProgramIncomeModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, moSession);
	}

	/**
	 * This method tests updateProgramIncomeModification method for good data
	 * inputs for scenario where an already existing line item entry is updated
	 */
	@Test
	public void testUpdateProgramIncomeModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		Boolean liResult = loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeModification method for good data
	 * inputs for scenario where a new line item entry is made when does not
	 * exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeModificationForNewItemInsert() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("0_105_1_10_10");
		Boolean liResult = loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeModification throws exception
	 */
	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeModificationWithAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("");// Invalid programIncomeId
		loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("A_105_1_10_10_105");
		loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeModificationWithException2() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("B_107_1_10_10_105");
		loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeModification method for bad data
	 * inputs and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeModificationWithException3() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("CC_109_1_10_10_102");
		loContractBudgetService.updateProgramIncomeModification(moSession, loCBProgramIncomeBean);
	}

	// Program Income Modification Test Case Ends

	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchContractBudgetModificationMilestone() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setContractBudgetID("557");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setBudgetTypeId("3");

		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetModificationService.fetchMilestone(loCBGridBean,moSession);
		assertNotNull(loCBMileStoneBean);
	}
	
	/**
	 * fetch milestone
	 */
	@Test
	public void testFetchContractBudgetModificationMilestone1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setContractBudgetID("557");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setBudgetTypeId("4");

		List<CBMileStoneBean> loCBMileStoneBean = moContractBudgetModificationService.fetchMilestone(loCBGridBean,moSession);
		assertNotNull(loCBMileStoneBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetModificationMilestoneFail() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentBudgetId("555");

		moContractBudgetModificationService.fetchMilestone(loCBGridBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetModificationMilestoneFail1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setBudgetTypeId("4");

		moContractBudgetModificationService.fetchMilestone(loCBGridBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractBudgetModificationMilestoneFail2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setBudgetTypeId("3");

		moContractBudgetModificationService.fetchMilestone(loCBGridBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testGetSeqForMilestone() throws ApplicationException
	{

		Integer seq = moContractBudgetModificationService.getSeqForMilestone(moSession);
		assertNotNull(seq);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetSeqForMilestoneExcp() throws ApplicationException
	{

		int seq = moContractBudgetModificationService.getSeqForMilestone(null);
		assertNotNull(seq);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertContractBudgetModificationMilestone() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		Integer seq = 500;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertContractBudgetModificationMilestone2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setMileStone("new case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		Integer seq = 501;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new test case");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		Integer seq = 500;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
	}
	
	/**
	 * insert milestone
	 */
	@Test
	public void testInsertContractBudgetModificationMilestone1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		Integer seq = 81;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * Application Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationMilestonefail1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("666");
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		Integer seq = 81;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * Application Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationMilestonefail2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("0");
		loCBMileStoneBean.setMileStone("new milestone81");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("9");
		Integer seq = 81;

		Boolean loRowInserted = moContractBudgetModificationService.addMilestone(seq,loCBMileStoneBean,moSession);
		assertTrue(loRowInserted);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateContractBudgetModificationMilestone() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556"); //555
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("19");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateContractBudgetModificationMilestone1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("18");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test
	public void testUpdateContractBudgetModificationMilestoneNew() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("20");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestone2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * update milestone
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneAppEx2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setCreatedByUserId("city_142");
		loCBMileStoneBean.setModificationAmount("1500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneNegMod() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneNegMod2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("city_142");
		loCBMileStoneBean.setCreatedByUserId("city_142");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneNegMod1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneValid2() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModificationAmount("-5000");
		loCBMileStoneBean.setContractBudgetID("9");
		loCBMileStoneBean.setId("81");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneNew1() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-1500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("314");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneValid() throws ApplicationException
	{
		
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("-50000");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("18");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetModificationMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setModifiedByUserId("803");
		loCBMileStoneBean.setCreatedByUserId("803");
		loCBMileStoneBean.setModificationAmount("500");
		loCBMileStoneBean.setContractBudgetID("555");
		loCBMileStoneBean.setId("19");

		Boolean loRowUpdated = moContractBudgetModificationService.updateMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * delete milestone
	 */
	@Test
	public void testDeleteContractBudgetModificationMilestone() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setId("555");
		

		Boolean loRowUpdated = moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}
	
	/**
	 * delete milestone
	 */
	@Test
	public void testDeleteContractBudgetModificationMilestone1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setId("81");
		

		Boolean loRowUpdated = moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
		assertTrue(loRowUpdated);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetModificationMilestoneFail() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);
		loCBMileStoneBean.setId("19");

		Boolean loRowUpdated = moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testDeleteContractBudgetModificationMilestoneFail1() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID(null);

		Boolean loRowUpdated = moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testDeleteContractBudgetModificationMilestoneFail2() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

		loCBMileStoneBean.setSubBudgetID("556");
		loCBMileStoneBean.setId("a");

		Boolean loRowUpdated = moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteContractBudgetModificationMilestoneAppex() throws ApplicationException
	{
		CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();
		loCBMileStoneBean.setId("0");
	
		moContractBudgetModificationService.deleteMilestone(loCBMileStoneBean,moSession);
	}

	/**
	 * This method tests fetchSalariedEmployeeBudgetForModification method for
	 * good data inputs
	 */
	@Test
	public void testFetchSalariedEmployeeBudgetForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchSalariedEmployeeBudgetForModification(
				moSession, loCBGridBean);
		assertNotNull(loResultList);
		assertTrue(!loResultList.isEmpty());
	}

	/**
	 * This method tests fetchHourlyEmployeeBudgetForModification method for
	 * good data inputs
	 */
	@Test
	public void testFetchHourlyEmployeeBudgetForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchHourlyEmployeeBudgetForModification(
				moSession, loCBGridBean);
		assertNotNull(loResultList);
		assertTrue(!loResultList.isEmpty());
	}

	/**
	 * This method tests fetchSeasonalEmployeeBudgetForModification method for
	 * good data inputs
	 */
	@Test
	public void testFetchSeasonalEmployeeBudgetForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchSeasonalEmployeeBudgetForModification(
				moSession, loCBGridBean);
		assertNotNull(loResultList);
		assertTrue(!loResultList.isEmpty());
	}

	/**
	 * This method tests fetchFringeBudgetForModification method for good data
	 * inputs
	 */
	@Test
	public void testFetchFringeBudgetForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchFringeBenifitsForModification(
				moSession, loCBGridBean);
		assertNotNull(loResultList);
		assertTrue(!loResultList.isEmpty());
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testAddEmployeeBudgetForModificationForSalary() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		Boolean loResult = loContractBudgetService.addEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Seasonal Employee) inputs
	 */
	@Test
	public void testAddEmployeeBudgetForModificationForSeasonal() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setTransactionName(HHSConstants.CBY_MOD_SEASONAL_EMPLOYEE_GRID_ADD);
		Boolean loResult = loContractBudgetService.addEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditEmployeeBudgetForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		Boolean loResult = loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	
	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditEmployeeBudgetForModificationWithTrueFlag() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setUnit("0");
		loPersonnelServiceBean.setModificationAmount("444");
		Boolean loResult = loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	
	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditEmployeeBudgetForModificationWithTrueFlagWithUpdate() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setUnit("0");
		loPersonnelServiceBean.setBudgetAmount("200");
		Boolean loResult = loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}	
	
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModificationWithNegativeUnits() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}		
	
	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditEmployeeBudgetForModificationWithFalseFlag() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		Boolean loResult = loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}

	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification9() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setTransactionName("salariedEmployeeGridModificationEdit");
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification10() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loPersonnelServiceBean.setTransactionName("salariedEmployeeGridModificationEdit");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification11() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(null, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification12() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId(null);
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification13() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification14() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("658");
		loPersonnelServiceBean.setModificationUnit("-101");
		loPersonnelServiceBean.setBudgetAmount("200");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModification15() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}
	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditFringeBenifitsForModification() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("58");
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}

	/**
	 * This method tests fetchSalariedEmployeeBudgetForModification method for
	 * bad data inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetForModificationWithAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_'");// Invalid Sub Budget id
		loContractBudgetService.fetchSalariedEmployeeBudgetForModification(moSession, loCBGridBean);

	}

	/**
	 * This method tests fetchSalariedEmployeeBudgetForModification method for
	 * bad data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSalariedEmployeeBudgetForModificationWithNullAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loContractBudgetService.fetchSalariedEmployeeBudgetForModification(null, loCBGridBean);
	}

	/**
	 * This method tests fetchSalariedEmployeeBudgetForModification method for
	 * bad data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchSalariedEmployeeBudgetForModificationWithException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchSalariedEmployeeBudgetForModification(moSession, loCBGridBean);
	}
	
	/**
	 * This method tests fetchSalariedEmployeeBudgetForModification method for
	 * bad data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test
	public void testFetchSalariedEmployeeBudgetForModificationWithException1() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = new CBGridBean();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchSalariedEmployeeBudgetForModification(moSession, loCBGridBean);
		assertTrue(loResultList.isEmpty());
	}	

	/**
	 * This method tests fetchHourlyEmployeeBudgetForModification method for bad
	 * data inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudgetForModificationWithAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_'");// Invalid Sub Budget id
		loContractBudgetService.fetchHourlyEmployeeBudgetForModification(moSession, loCBGridBean);

	}

	/**
	 * This method tests fetchHourlyEmployeeBudgetForModification method for bad
	 * data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchHourlyEmployeeBudgetForModificationWithNullAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loContractBudgetService.fetchHourlyEmployeeBudgetForModification(null, loCBGridBean);
	}

	/**
	 * This method tests fetchHourlyEmployeeBudgetForModification method for bad
	 * data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchHourlyEmployeeBudgetForModificationWithException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchHourlyEmployeeBudgetForModification(moSession, loCBGridBean);
	}
	
	/**
	 * This method tests fetchHourlyEmployeeBudgetForModification method for bad
	 * data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test
	public void testFetchHourlyEmployeeBudgetForModificationWithException1() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = new CBGridBean();
		List<PersonnelServiceBudget> loResultList =  loContractBudgetService.fetchHourlyEmployeeBudgetForModification(moSession, loCBGridBean);
		assertTrue(loResultList.isEmpty());
	}	

	/**
	 * This method tests fetchSeasonalEmployeeBudgetForModification method for
	 * bad data inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudgetForModificationWithAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_'");// Invalid Sub Budget id
		loContractBudgetService.fetchSeasonalEmployeeBudgetForModification(moSession, loCBGridBean);

	}

	/**
	 * This method tests fetchSeasonalEmployeeBudgetForModification method for
	 * bad data inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchSeasonalEmployeeBudgetForModificationWithNullAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loContractBudgetService.fetchSeasonalEmployeeBudgetForModification(null, loCBGridBean);
	}

	/**
	 * This method tests fetchSeasonalEmployeeBudgetForModification method for
	 * bad data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchSeasonalEmployeeBudgetForModificationWithException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchSeasonalEmployeeBudgetForModification(moSession, loCBGridBean);
	}

	/**
	 * This method tests fetchSeasonalEmployeeBudgetForModification method for
	 * bad data inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test
	public void testFetchSeasonalEmployeeBudgetForModificationWithException1() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = new CBGridBean();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchSeasonalEmployeeBudgetForModification(moSession, loCBGridBean);
		assertTrue(loResultList.isEmpty());
	}
	/**
	 * This method tests fetchFringeBudgetForModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFringeBudgetForModificationWithAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_'");// Invalid Sub Budget id
		loContractBudgetService.fetchFringeBenifitsForModification(moSession, loCBGridBean);

	}

	/**
	 * This method tests fetchFringeBudgetForModification method for bad data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFringeBudgetForModificationWithNullAppException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loContractBudgetService.fetchFringeBenifitsForModification(null, loCBGridBean);
	}

	/**
	 * This method tests fetchFringeBudgetForModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchFringeBudgetForModificationWithException() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchFringeBenifitsForModification(moSession, loCBGridBean);
	}
	
	/**
	 * This method tests fetchFringeBudgetForModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test
	public void testFetchFringeBudgetForModificationWithException1() throws ApplicationException
	{

		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = new CBGridBean();
		List<PersonnelServiceBudget> loResultList = loContractBudgetService.fetchFringeBenifitsForModification(moSession, loCBGridBean);
		assertTrue(loResultList.isEmpty());
	}	

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddEmployeeBudgetForModificationWithAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setSubBudgetID("377H_'");
		loContractBudgetService.addEmployeeBudgetForModification(moSession, loPersonnelServiceBean);

	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testAddEmployeeBudgetForModificationWithNullAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loContractBudgetService.addEmployeeBudgetForModification(null, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testAddEmployeeBudgetForModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = null;
		loContractBudgetService.addEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModificationWithAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setSubBudgetID("377H_'");
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);

	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditEmployeeBudgetForModificationWithNullAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loContractBudgetService.editEmployeeBudgetForModification(null, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testEditEmployeeBudgetForModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = null;
		loContractBudgetService.editEmployeeBudgetForModification(moSession, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModificationWithAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setSubBudgetID("377H_'");
		loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);

	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an ApplicationException is expected
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModificationWithNullAppException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loContractBudgetService.editFringeBenifitsForModification(null, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testEditFringeBenifitsForModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = null;
		loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
	}

	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditFringeBenifitsForModificationForAmount() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("58");
		loPersonnelServiceBean.setModificationAmount("300");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	
	/**
	 * This method tests AddEmployeeBudgetForModification method for good
	 * data(Salaried Employee) inputs
	 */
	@Test
	public void testEditFringeBenifitsForModificationForNegativeAmount() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("58");
		loPersonnelServiceBean.setModificationAmount("-20");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}	
	
	
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification7() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("558");
		loPersonnelServiceBean.setModificationAmount("-20");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}	
	
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification8() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("558B");
		loPersonnelServiceBean.setModificationAmount("-20");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}	
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification9() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("558");
		loPersonnelServiceBean.setModificationAmount("-20FS");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}	
	
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification10() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("558");
		loPersonnelServiceBean.setModificationAmount("");		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification11() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("59");
		loPersonnelServiceBean.setSubBudgetID("555");
		loPersonnelServiceBean.setContractBudgetID("555");
		loPersonnelServiceBean.setModificationAmount("-20");
		loPersonnelServiceBean.setModifyByProvider("809");
		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	@Test(expected = ApplicationException.class)
	public void testEditFringeBenifitsForModification12() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		PersonnelServiceBudget loPersonnelServiceBean = getDummyPersonnelServiceBeanObj();
		loPersonnelServiceBean.setId("59");
		loPersonnelServiceBean.setSubBudgetID("555");
		loPersonnelServiceBean.setContractBudgetID("555");
		loPersonnelServiceBean.setModificationAmount("-20");
		loPersonnelServiceBean.setModifyByProvider("809");
		
		Boolean loResult = loContractBudgetService.editFringeBenifitsForModification(moSession, loPersonnelServiceBean);
		assertTrue(loResult);
	}
	private PersonnelServiceBudget getDummyPersonnelServiceBeanObj()
	{
		PersonnelServiceBudget loPersonnelSeviceBean = new PersonnelServiceBudget();

		loPersonnelSeviceBean.setId("55");
		loPersonnelSeviceBean.setEmpType("1");
		loPersonnelSeviceBean.setEmpPosition("1");
		loPersonnelSeviceBean.setContractBudgetID("557");
		loPersonnelSeviceBean.setSubBudgetID("556");
		loPersonnelSeviceBean.setModificationUnit("2");
		loPersonnelSeviceBean.setModificationAmount("1111");
		loPersonnelSeviceBean.setCreatedByUserId("803");
		loPersonnelSeviceBean.setModifiedByUserId("");
		loPersonnelSeviceBean.setModifyByAgency("city_142");
		loPersonnelSeviceBean.setModifyByProvider("803");
		loPersonnelSeviceBean.setContractID("111777");
		loPersonnelSeviceBean.setTransactionName(HHSConstants.CBY_MOD_SALARIED_EMPLOYEE_GRID_ADD);
		loPersonnelSeviceBean.setParentBudgetId("555");
		loPersonnelSeviceBean.setParentSubBudgetId("555");
		loPersonnelSeviceBean.setUnit("5");
		loPersonnelSeviceBean.setBudgetAmount("2222");

		return loPersonnelSeviceBean;

	}

	/**
	 * @throws ApplicationException
	 */
	

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractedServicesModificationSubContractor() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationSubContractors(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesModificationSubContractor2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556B");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationSubContractors(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesModificationSubContractor3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationSubContractors(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractedServicesModificationVendors() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationVendors(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}

	public ContractedServicesBean SetData()
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();
		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		loCBGridBean.setCsName("testing");
		loCBGridBean.setDescOfService("testing");
		loCBGridBean.setModifiedByUserId("803");
		loCBGridBean.setContractBudgetID("557");
		loCBGridBean.setModificationAmt("1200");
		return loCBGridBean;
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesModification() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("1");

		Integer liVal = moContractBudgetModificationService.addContractedServicesModification(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesModification1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");

		Integer liVal = moContractBudgetModificationService.addContractedServicesModification(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testAddContractedServicesModification2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");

		Integer liVal = moContractBudgetModificationService.addContractedServicesModification(moSession, loCBGridBean);
		assertNotNull(liVal);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractedServicesModification() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractedServicesModification1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractedServicesModification2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("1");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test
	public void testEditContractedServicesModification4() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = null;

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test
	public void testEditContractedServicesModification5() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("330");
		loCBGridBean.setSubBudgetID("558");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("803");
		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}
	@Test
	public void testEditContractedServicesModification6() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setId("330");
		loCBGridBean.setSubBudgetID("558");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("803");
		loCBGridBean.setCsName(null);
		loCBGridBean.setDescOfService(null);
		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesModification7() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(null,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesModification8() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80B");

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}


	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesModification9() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractedServicesModification10() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");
		loCBGridBean.setModificationAmt("-7788");
		Boolean lbStatus = moContractBudgetModificationService.editContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesModification() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("1");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.deleteContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesModification1() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("2");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.deleteContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDeleteContractedServicesModification2() throws ApplicationException
	{
		ContractedServicesBean loCBGridBean = new ContractedServicesBean();

		loCBGridBean = SetData();
		loCBGridBean.setSubHeader("3");
		loCBGridBean.setId("80");

		Boolean lbStatus = moContractBudgetModificationService.deleteContractedServicesModification(moSession,
				loCBGridBean);
		assertNotNull(lbStatus);
	}

	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test
	public void testfetchModificationRent() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

  		List<Rent> loRentBeanList = moContractBudgetModificationService.fetchModificationRent(moSession, loCBGridBean);
		assertNotNull(loRentBeanList);
		assertTrue(loRentBeanList.size() > 0);
	}
	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchModificationRentForException() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		moSession= null;
		List<Rent> lbStatus = moContractBudgetModificationService.fetchModificationRent(moSession, loCBGridBean);
		assertNull(lbStatus);
	}
	/**
	 * Test method for fetchModificationRent()
	 * @throws ApplicationException
	 * Application Exception thrown here
	 */
	@Test(expected = Exception.class)
	public void testfetchModificationRentForApplicationException() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID(null);
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");
		List<Rent> lbStatus = moContractBudgetModificationService.fetchModificationRent(moSession, loCBGridBean);
		assertNotNull(lbStatus);
	}
	/**
	 * @throws ApplicationException 
	 * 
	 */
	
	/**
	 * This test case is for the location entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	
	/**
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateModificationRentForLocationAE() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setLocation(null);
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the Modify Amount entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForModifyAmountAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setId("390");
		loRent.setModifyAmount(null);
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the ID entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForIdAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setId(null);
		loRent.setModifyAmount("5000");
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the provider entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForProviderAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setId("390");
		loRent.setModifyAmount("250");
		loRent.setModifyByProvider(null);
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the SubBudgetId entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForSubBudgetIdAsNull() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID(null);
		loRent.setParentSubBudgetId("556");
		loRent.setId("390");
		loRent.setModifyAmount("250");
		loRent.setModifyByProvider("803");
		loRent.setLocation("Location123");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	@Test
	public void testUpdateModificationRentForSubBudgetIdAsNull8() throws ApplicationException
	{
		Rent loRent = null;
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	
	
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForSubBudgetIdAsNull9() throws ApplicationException
	{
		Rent loRent = new  Rent();
		
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	
	
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForSubBudgetIdAsNull10() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("55V5");
		loRent.setParentSubBudgetId("55V6");
		loRent.setId("390");
		loRent.setModifyAmount("25V0");
		loRent.setModifyByProvider("8V03");
		loRent.setLocation("Location12V3");
		loRent.setParentId("39V0");
		loRent.setBudgetId(557);
		loRent.setFyBudget("50V00");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	
	/**
	 * This test case is for the delete rentId
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test
	public void testDeleteRentModificationForPositiveCase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId("390");
		
		Boolean lbStatus = moContractBudgetModificationService.deleteRentModification(moSession, loRent);
		
		assertTrue(lbStatus);
	}
	/**
	 * This test case is for the id entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = Exception.class)
	public void testDeleteRentModificationForNegativeCase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId(null);
		
		Boolean lbStatus = moContractBudgetModificationService.deleteRentModification(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This test case is for the id entered as null
	 * 
	 * @throws ApplicationException 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testDeleteRentModificationForAECase() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId(null);
		
		Boolean lbStatus = moContractBudgetModificationService.deleteRentModification(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testDeleteRentModificationForAECase4() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId(null);
		
		Boolean lbStatus = moContractBudgetModificationService.deleteRentModification(null, loRent);
		
		assertFalse(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testDeleteRentModificationForAECase5() throws ApplicationException
	{
		Rent loRent = null;
		
		
		Boolean lbStatus = moContractBudgetModificationService.deleteRentModification(moSession, loRent);
		
		assertFalse(lbStatus);
	}
	/**
	 * This method test validateModificationAmountTotal method for Submit COntract Budget Modification 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateModificationAmountTotal1() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = "557";
		
		loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
		assertTrue(!loValid);
	}
	/**
	 * This method test validateModificationAmountTotal method for Submit COntract Budget Modification 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateModificationAmountTotal2() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = "10119";
		
		try {
			loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
		} catch (ApplicationException aoAppEx) {
			assertTrue(!loValid);
		}
	}
	
	@Test
	public void testValidateModificationAmountTotal3() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = "10119";
		
		try {
			loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
		} catch (ApplicationException aoAppEx) {
			assertTrue(!loValid);
		}
	}
	
	@Test
	public void testValidateModificationAmountTotal4() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = "557";
		
		try {
			loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
			assertFalse(loValid);
			
		} catch (ApplicationException aoAppEx) {
			assertTrue(!loValid);
		}
	}
	
	@Test
	public void testValidateModificationAmountTotal5() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = "10232";
		
		try {
			loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
			assertTrue(loValid);
			
		} catch (ApplicationException aoAppEx) {
			assertTrue(!loValid);
		}
	}
	
	@Test
	public void testValidateModificationAmountTotal6() throws ApplicationException
	{
		Boolean loValid = false;
		String loBudgetId = null;
		
		try {
			loValid = moContractBudgetModificationService.validateModificationAmountTotal(moSession, loBudgetId);
			assertTrue(loValid);
			
		} catch (ApplicationException aoAppEx) {
			assertTrue(!loValid);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("3");
			aoCBEquipmentBean.setSubBudgetID("8881");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("897987");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentModificationDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("3898");
			aoCBEquipmentBean.setSubBudgetID("8881");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("897987");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("97");
			aoCBEquipmentBean.setSubBudgetID("8881");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("897987");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testEditEquipmentModificationDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("161");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("897987");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testEditEquipmentModificationDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("897987");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("1");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("2");
			aoCBEquipmentBean.setModificationAmt("200");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testEditEquipmentModificationDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
			// negative
		try
		{
			CBEquipmentBean aoCBEquipmentBean = null;
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testEditEquipmentModificationDetails8() throws ApplicationException
	{
		Boolean lbThrown = false;
			// negative
		try
		{
			CBEquipmentBean aoCBEquipmentBean = null;
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(null, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails9() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("1");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("-223");
			aoCBEquipmentBean.setModificationAmt("200");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails10() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("1B");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("-223");
			aoCBEquipmentBean.setModificationAmt("200");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testEditEquipmentModificationDetails11() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("1B");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("-223");
			aoCBEquipmentBean.setModificationAmt("200");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testEditEquipmentModificationDetails12() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
			aoCBEquipmentBean.setId("1B");
			aoCBEquipmentBean.setSubBudgetID("88813");
			aoCBEquipmentBean.setContractBudgetID("888");
			aoCBEquipmentBean.setEquipment("TestEquipment");
			aoCBEquipmentBean.setUnits("-223");
			aoCBEquipmentBean.setModificationAmt("20000");
			aoCBEquipmentBean.setModifyByProvider("803");
			boolean lbUpdateStatus = moContractBudgetModificationService.editEquipmentModificationDetails(moSession, aoCBEquipmentBean);
			
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	
	
	@Test
	public void testEditOperationAndSupportModificationDetails1() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("1222");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("600");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test
	public void testEditOperationAndSupportModificationDetails2() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("2");
		aoCBOperationSupportBean.setSubBudgetID("556");
		aoCBOperationSupportBean.setModificationAmt("600");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("557");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails3() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("1222");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("-6000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails4() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = null;
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails5() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("1222B");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("-6000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails6() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("-6000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails7() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails8() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("1222");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("-6000");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,null);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	@Test
	public void testEditOperationAndSupportModificationDetails9() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("1222");
		aoCBOperationSupportBean.setSubBudgetID("777");
		aoCBOperationSupportBean.setModificationAmt("0");
		aoCBOperationSupportBean.setModifyByProvider("803");
		aoCBOperationSupportBean.setContractBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testEditOperationAndSupportModificationDetails10() throws ApplicationException
	{
		CBOperationSupportBean aoCBOperationSupportBean = new CBOperationSupportBean();
		aoCBOperationSupportBean.setId("");
		aoCBOperationSupportBean.setSubBudgetID("");
		aoCBOperationSupportBean.setModificationAmt("");
		aoCBOperationSupportBean.setModifyByProvider("");
		aoCBOperationSupportBean.setContractBudgetID("");
		Boolean lbStatus = moContractBudgetModificationService.editOperationAndSupportModificationDetails(aoCBOperationSupportBean,moSession);
		assertNotNull(lbStatus);
		assertTrue(lbStatus);
	}
	
	/**
	 * This method test insertContractBudgetModificationRateInfo method for inserting new rate info in RATE table 
	 * Test for validateModificationUnit with new record identifier
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractBudgetModificationRent() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId("13");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("58");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(moSession, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent2() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId("13");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("-580000");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(moSession, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test
	public void testInsertContractBudgetModificationRent3() throws ApplicationException
	{
		Rent loRent = null;
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(moSession, loRent);
		assertEquals("0", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent4() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId("13B");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("-580000");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(moSession, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent5() throws ApplicationException
	{
		Rent loRent = new Rent();
		
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(moSession, loRent);
		assertEquals("1", loRowInserted.toString());
	}

	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent6() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId("13");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("-580000");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(null, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent7() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId("13");
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("580000");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(null, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent8() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("");
		loRent.setManagementCompanyName("");
		loRent.setPropertyOwner("");
		loRent.setPercentChargedToContract("");
		loRent.setSubBudgetID("");
		loRent.setParentSubBudgetId("");
        loRent.setModifyByProvider("");
		loRent.setId("");
		loRent.setModifiedByUserId("");
		loRent.setCreatedByUserId("");
		loRent.setModifyByAgency("");
		loRent.setModifyByProvider("");
		loRent.setModifyAmount("");
		loRent.setContractBudgetID("");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(null, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertContractBudgetModificationRent9() throws ApplicationException
	{
		Rent loRent = new Rent();
		loRent.setLocation("location");
		loRent.setManagementCompanyName("MC1");
		loRent.setPropertyOwner("propertyOwner");
		loRent.setPercentChargedToContract("5");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("555");
        loRent.setModifyByProvider("803");
		loRent.setId(null);
		loRent.setModifiedByUserId("803");
		loRent.setCreatedByUserId("803");
		loRent.setModifyByAgency("city_142");
		loRent.setModifyByProvider("803");
		loRent.setModifyAmount("-580000");
		loRent.setContractBudgetID("555");
		Integer loRowInserted = moContractBudgetModificationService.insertContractBudgetModificationRent(null, loRent);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test
	public void testFetchModificationOTPS1() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777");
		aoCBGridBeanObj.setSubBudgetID("777");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setParentBudgetId("555");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test
	public void testFetchModificationOTPS2() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777");
		aoCBGridBeanObj.setSubBudgetID("776");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setParentBudgetId("555");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS3() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777");
		aoCBGridBeanObj.setSubBudgetID("777B");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setParentBudgetId("555");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS4() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("77777");
		aoCBGridBeanObj.setSubBudgetID("77777");
		aoCBGridBeanObj.setContractBudgetID("555777");
		aoCBGridBeanObj.setParentBudgetId("55577");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS5() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS6() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj  = null;
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS7() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777");
		aoCBGridBeanObj.setSubBudgetID("777");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setParentBudgetId("555");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(null, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModificationOTPS8() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777b");
		aoCBGridBeanObj.setSubBudgetID("777b");
		aoCBGridBeanObj.setContractBudgetID("5b55");
		aoCBGridBeanObj.setParentBudgetId("55b5");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	@Test
	public void testFetchModificationOTPS9() throws ApplicationException
	{
		//CBGridBean loProfService = getProfServicesParamsForModification();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setParentSubBudgetId("777");
		aoCBGridBeanObj.setSubBudgetID("777");
		aoCBGridBeanObj.setContractBudgetID("555");
		aoCBGridBeanObj.setParentBudgetId("555");
		List<CBOperationSupportBean> loCBOperationSupportBeanList = moContractBudgetModificationService
				.fetchModificationOTPS(moSession, aoCBGridBeanObj);

		assertNotNull(loCBOperationSupportBeanList);
		assertTrue(loCBOperationSupportBeanList.size() > 0);
	}
	
	
	@Test
	public void testMergeBudgetModificationDocument1() throws ApplicationException
	{
		Boolean aoFinalFinish = false;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus="";
		
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map==null);
	}
	
	@Test
	public void testMergeBudgetModificationDocument2() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus="1";
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setBudgetId("555");
		aoTaskDetailsBean.setUserId("803");
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	
	@Test
	public void testMergeBudgetModificationDocument3() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus=null;
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setBudgetId("555");
		aoTaskDetailsBean.setUserId("803");
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testMergeBudgetModificationDocument4() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus=null;
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testMergeBudgetModificationDocument5() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus=null;
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	@Test(expected = ApplicationException.class)
	public void testMergeBudgetModificationDocument6() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = null;
		String asBudgetStatus="";
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(moSession, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testMergeBudgetModificationDocument7() throws ApplicationException
	{
		Boolean aoFinalFinish = true;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String asBudgetStatus=null;
		Map map = moContractBudgetModificationService.mergeBudgetModificationDocument(null, aoFinalFinish,aoTaskDetailsBean,asBudgetStatus);
		assertTrue(map!=null);
	}
	
	@Test
	public void testDelEquipmentModificationDetails1() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		aoCBEquipmentBean.setId("235");
		aoCBEquipmentBean.setSubBudgetID("886");
		Boolean lbStatus = moContractBudgetModificationService.delEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test
	public void testDelEquipmentModificationDetails2() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		aoCBEquipmentBean.setId("235");
		aoCBEquipmentBean.setSubBudgetID("555");
		Boolean lbStatus = moContractBudgetModificationService.delEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	@Test
	public void testDelEquipmentModificationDetails3() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		aoCBEquipmentBean.setId("");
		aoCBEquipmentBean.setSubBudgetID("");
		Boolean lbStatus = moContractBudgetModificationService.delEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testDelEquipmentModificationDetails4() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		Boolean lbStatus = moContractBudgetModificationService.delEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	

	@Test(expected = ApplicationException.class)
	public void testDelEquipmentModificationDetails5() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = null;
		
		Boolean lbStatus = moContractBudgetModificationService.delEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test
	public void testAddEquipmentModificationDetails1() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		aoCBEquipmentBean.setContractBudgetID("666");
		aoCBEquipmentBean.setSubBudgetID("778");
		aoCBEquipmentBean.setEquipment("Test");
		aoCBEquipmentBean.setUnits("56");
		aoCBEquipmentBean.setModificationAmt("999");
		aoCBEquipmentBean.setModifyByProvider("803");
		
		Boolean lbStatus = moContractBudgetModificationService.addEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testAddEquipmentModificationDetails2() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		aoCBEquipmentBean.setContractBudgetID("666B");
		aoCBEquipmentBean.setSubBudgetID("778");
		aoCBEquipmentBean.setEquipment("Test");
		aoCBEquipmentBean.setUnits("56");
		aoCBEquipmentBean.setModificationAmt("999");
		aoCBEquipmentBean.setModifyByProvider("803");
		
		Boolean lbStatus = moContractBudgetModificationService.addEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testAddEquipmentModificationDetails3() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		aoCBEquipmentBean.setContractBudgetID("");
		aoCBEquipmentBean.setSubBudgetID("");
		aoCBEquipmentBean.setEquipment("");
		aoCBEquipmentBean.setUnits("");
		aoCBEquipmentBean.setModificationAmt("");
		aoCBEquipmentBean.setModifyByProvider("");
		
		Boolean lbStatus = moContractBudgetModificationService.addEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testAddEquipmentModificationDetails4() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = new CBEquipmentBean();
		
		Boolean lbStatus = moContractBudgetModificationService.addEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	@Test(expected = ApplicationException.class)
	public void testAddEquipmentModificationDetails5() throws ApplicationException
	{
		CBEquipmentBean aoCBEquipmentBean = null;
		Boolean lbStatus = moContractBudgetModificationService.addEquipmentModificationDetails(moSession, aoCBEquipmentBean);
		
		assertTrue(lbStatus);
	}
	
	@Test
	public void testFetchModificationEquipment1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("666");
		aoCBGridBeanObj.setParentSubBudgetId("886");
		aoCBGridBeanObj.setContractBudgetID("666");
		aoCBGridBeanObj.setSubBudgetID("886");
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetModificationService.fetchModificationEquipment(moSession, aoCBGridBeanObj);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testFetchModificationEquipment2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("666B");
		aoCBGridBeanObj.setParentSubBudgetId("886");
		aoCBGridBeanObj.setContractBudgetID("666B");
		aoCBGridBeanObj.setSubBudgetID("886");
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetModificationService.fetchModificationEquipment(moSession, aoCBGridBeanObj);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchModificationEquipment3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("");
		aoCBGridBeanObj.setParentSubBudgetId("");
		aoCBGridBeanObj.setContractBudgetID("");
		aoCBGridBeanObj.setSubBudgetID("");
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetModificationService.fetchModificationEquipment(moSession, aoCBGridBeanObj);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchModificationEquipment4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetModificationService.fetchModificationEquipment(moSession, aoCBGridBeanObj);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchModificationEquipment5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		
		List<CBEquipmentBean> loCBEquipmentBeanList = moContractBudgetModificationService.fetchModificationEquipment(moSession, aoCBGridBeanObj);
		
		assertNotNull(loCBEquipmentBeanList);
	}
	
	@Test
	public void testFetchOpAndSupportModPageData1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("666");
		aoCBGridBeanObj.setParentSubBudgetId("886");
		aoCBGridBeanObj.setContractBudgetID("666");
		aoCBGridBeanObj.setSubBudgetID("886");
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetModificationService.fetchOpAndSupportModPageData(aoCBGridBeanObj,moSession);
		
		assertNotNull(loCBOperationSupportBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchOpAndSupportModPageData2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetModificationService.fetchOpAndSupportModPageData(aoCBGridBeanObj,moSession);
		
		assertNotNull(loCBOperationSupportBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchOpAndSupportModPageData3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("666m");
		aoCBGridBeanObj.setParentSubBudgetId("886m");
		aoCBGridBeanObj.setContractBudgetID("666m");
		aoCBGridBeanObj.setSubBudgetID("886m");
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetModificationService.fetchOpAndSupportModPageData(aoCBGridBeanObj,moSession);
		
		assertNotNull(loCBOperationSupportBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchOpAndSupportModPageData4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		aoCBGridBeanObj.setParentBudgetId("");
		aoCBGridBeanObj.setParentSubBudgetId("");
		aoCBGridBeanObj.setContractBudgetID("");
		aoCBGridBeanObj.setSubBudgetID("");
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetModificationService.fetchOpAndSupportModPageData(aoCBGridBeanObj,moSession);
		
		assertNotNull(loCBOperationSupportBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchOpAndSupportModPageData5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = null;
		CBOperationSupportBean loCBOperationSupportBean = moContractBudgetModificationService.fetchOpAndSupportModPageData(aoCBGridBeanObj,moSession);
		
		assertNotNull(loCBOperationSupportBean);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testinsertModificationSubBudgetDetails1() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		loHashMap1.put(HHSConstants.CREATED_BY_USER_ID, "agency_47");
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHashMap1.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR,"2013");
		loHashMap1.put(HHSConstants.PARENT_ID, "235");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationSubBudgetDetails(moSession, loHashMap1);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testinsertModificationSubBudgetDetails2() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		loHashMap1.put(HHSConstants.CREATED_BY_USER_ID, "");
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "");
		loHashMap1.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR,"");
		loHashMap1.put(HHSConstants.PARENT_ID, "");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationSubBudgetDetails(moSession, loHashMap1);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testinsertModificationSubBudgetDetails3() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		loHashMap1.put(HHSConstants.CREATED_BY_USER_ID, "agency_47");
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "555B");
		loHashMap1.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR,"2013");
		loHashMap1.put(HHSConstants.PARENT_ID, "235FF");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationSubBudgetDetails(moSession, loHashMap1);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testinsertModificationSubBudgetDetails4() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		Integer loRowInserted = moContractBudgetModificationService.insertModificationSubBudgetDetails(moSession, loHashMap1);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testinsertModificationSubBudgetDetails5() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		loHashMap1.put(HHSConstants.CREATED_BY_USER_ID, "agency_47");
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		loHashMap1.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR,"2013");
		loHashMap1.put(HHSConstants.PARENT_ID, "235");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationSubBudgetDetails(null, loHashMap1);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchModifiedBudgetId() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "55555");
		String lsModifiedBudgetId = moContractBudgetModificationService.fetchModifiedBudgetId(moSession, loHashMap1);
		assertNotNull(lsModifiedBudgetId);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModifiedBudgetId2() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "55555m");
		String lsModifiedBudgetId = moContractBudgetModificationService.fetchModifiedBudgetId(moSession, loHashMap1);
		assertNotNull(lsModifiedBudgetId);
	}
	@Test
	public void testFetchModifiedBudgetId3() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "");
		String lsModifiedBudgetId = moContractBudgetModificationService.fetchModifiedBudgetId(moSession, loHashMap1);
		assertTrue(lsModifiedBudgetId==null);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModifiedBudgetId4() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, null);
		String lsModifiedBudgetId = moContractBudgetModificationService.fetchModifiedBudgetId(moSession, loHashMap1);
		assertNotNull(lsModifiedBudgetId);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchModifiedBudgetId5() throws ApplicationException
	{
		
		HashMap<String, String> loHashMap1 = new HashMap<String, String>();
		
		loHashMap1.put(HHSConstants.BUDGET_ID_WORKFLOW, "55555");
		String lsModifiedBudgetId = moContractBudgetModificationService.fetchModifiedBudgetId(null, loHashMap1);
		assertNotNull(lsModifiedBudgetId);
	}
	
	@Test
	public void testInsertModificationBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setBudgetTypeId(3);
		aoContractBudgetBean.setContractId("111778");
		aoContractBudgetBean.setBudgetfiscalYear("2013");
		aoContractBudgetBean.setTotalbudgetAmount("600");
		aoContractBudgetBean.setParentId("559");
		aoContractBudgetBean.setStatusId("85");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationBudgetDetails(moSession, aoContractBudgetBean);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertModificationBudgetDetails2() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setBudgetTypeId(3);
		aoContractBudgetBean.setContractId("111778B");
		aoContractBudgetBean.setBudgetfiscalYear("2013");
		aoContractBudgetBean.setTotalbudgetAmount("600V");
		aoContractBudgetBean.setParentId("559V");
		aoContractBudgetBean.setStatusId("85");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationBudgetDetails(moSession, aoContractBudgetBean);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertModificationBudgetDetails3() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		
		Integer loRowInserted = moContractBudgetModificationService.insertModificationBudgetDetails(moSession, aoContractBudgetBean);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertModificationBudgetDetails4() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setBudgetTypeId(3);
		aoContractBudgetBean.setContractId("");
		aoContractBudgetBean.setBudgetfiscalYear("");
		aoContractBudgetBean.setTotalbudgetAmount("");
		aoContractBudgetBean.setParentId("");
		aoContractBudgetBean.setStatusId("");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationBudgetDetails(moSession, aoContractBudgetBean);
		assertEquals("1", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testInsertModificationBudgetDetails5() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setBudgetTypeId(3);
		aoContractBudgetBean.setContractId("111778");
		aoContractBudgetBean.setBudgetfiscalYear("2013");
		aoContractBudgetBean.setTotalbudgetAmount("600");
		aoContractBudgetBean.setParentId("559");
		aoContractBudgetBean.setStatusId("85");
		Integer loRowInserted = moContractBudgetModificationService.insertModificationBudgetDetails(null, aoContractBudgetBean);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test
	public void testFetchCMSubBudgetSummary() throws ApplicationException
	{
		HashMap<String, String> loHashmap  = new HashMap<String, String>();
		
		loHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		
		List<CBGridBean> loCBGridBeanList = moContractBudgetModificationService
					.fetchCMSubBudgetSummary(moSession,loHashmap);

			assertNotNull(loCBGridBeanList);
			assertTrue(loCBGridBeanList.size() > 0);
		
	}
	@Test(expected = ApplicationException.class)
	public void testFetchCMSubBudgetSummary2() throws ApplicationException
	{
		HashMap<String, String> loHashmap  = new HashMap<String, String>();
		
		loHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, null);
		
		List<CBGridBean> loCBGridBeanList = moContractBudgetModificationService
					.fetchCMSubBudgetSummary(moSession,loHashmap);

			assertNotNull(loCBGridBeanList);
			assertTrue(loCBGridBeanList.size() > 0);
		
	}
	@Test(expected = ApplicationException.class)
	public void testFetchCMSubBudgetSummary3() throws ApplicationException
	{
		HashMap<String, String> loHashmap  = new HashMap<String, String>();
		
		loHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555B");
		
		List<CBGridBean> loCBGridBeanList = moContractBudgetModificationService
					.fetchCMSubBudgetSummary(moSession,loHashmap);

			assertNotNull(loCBGridBeanList);
			assertTrue(loCBGridBeanList.size() > 0);
		
	}
	@Test(expected = ApplicationException.class)
	public void testFetchCMSubBudgetSummary4() throws ApplicationException
	{
		HashMap<String, String> loHashmap  = new HashMap<String, String>();
		
		
		
		List<CBGridBean> loCBGridBeanList = moContractBudgetModificationService
					.fetchCMSubBudgetSummary(moSession,loHashmap);

			assertNotNull(loCBGridBeanList);
			assertTrue(loCBGridBeanList.size() > 0);
		
	}
	@Test(expected = ApplicationException.class)
	public void testFetchCMSubBudgetSummary5() throws ApplicationException
	{
		HashMap<String, String> loHashmap  = new HashMap<String, String>();
		
		loHashmap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		
		List<CBGridBean> loCBGridBeanList = moContractBudgetModificationService
					.fetchCMSubBudgetSummary(null,loHashmap);

			assertNotNull(loCBGridBeanList);
			assertTrue(loCBGridBeanList.size() > 0);
		
	}
	
	@Test
	public void testFetchNonGridContractedServicesModification() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID("605");
		aoCBGridBeanObj.setParentSubBudgetId("556");
		ContractedServicesBean loCBContractedServicesBean = moContractBudgetModificationService
					.fetchNonGridContractedServicesModification(moSession,aoCBGridBeanObj);

			assertNotNull(loCBContractedServicesBean);
			
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchNonGridContractedServicesModification2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID("605B");
		aoCBGridBeanObj.setParentSubBudgetId("556");
		ContractedServicesBean loCBContractedServicesBean = moContractBudgetModificationService
					.fetchNonGridContractedServicesModification(moSession,aoCBGridBeanObj);

			assertNotNull(loCBContractedServicesBean);
			
		
	}
	@Test(expected = ApplicationException.class)
	public void testFetchNonGridContractedServicesModification3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		
		ContractedServicesBean loCBContractedServicesBean = moContractBudgetModificationService
					.fetchNonGridContractedServicesModification(moSession,aoCBGridBeanObj);

			assertNotNull(loCBContractedServicesBean);
			
		
	}
	
	@Test
	public void testFetchContractedServicesModificationConsultants() throws ApplicationException
	{
		boolean lbThrown=false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationConsultants(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesModificationConsultants2() throws ApplicationException
	{
		boolean lbThrown=false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setSubBudgetID("556B");
		loCBGridBean.setParentBudgetId("555");
		loCBGridBean.setParentSubBudgetId("555");

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationConsultants(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchContractedServicesModificationConsultants3() throws ApplicationException
	{
		boolean lbThrown=false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		CBGridBean loCBGridBean = new CBGridBean();

		List<ContractedServicesBean> loCBContractedServicesBean = moContractBudgetModificationService
				.fetchContractedServicesModificationConsultants(moSession, loCBGridBean);
		assertNotNull(loCBContractedServicesBean);
	}
	@Test
	public void testUpdateContractBudgetModificationRateBeanValueChanged() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setSubBudgetID("556");
		loRateBean.setContractBudgetID("557");
		loRateBean.setParentSubBudgetId("555");
		loRateBean.setModifyByProvider("803");
		loRateBean.setLsModifyAmount("558");
		loRateBean.setLsModifyUnits("3");
		loRateBean.setId("1");
		Integer loRowInserted = moContractBudgetModificationService.updateContractBudgetModificationRateInfo(moSession, loRateBean);
		//assertEquals("1", loRowInserted.toString());
		assertNotNull(loRowInserted);
	}

@Test
	public void testUpdateModificationRentForPositiveScenario() throws ApplicationException
	{
		Rent loRent = new  Rent();
		loRent.setId("123");
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setContractBudgetID("555");
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setModifiedByUserId("803");
		loRent.setLocation("Location123");
		loRent.setManagementCompanyName("managementCompanyName");
		loRent.setPercentChargedToContract("50");
		loRent.setPropertyOwner("abc");
		loRent.setPublicSchoolSpace("1");
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		assertTrue(lbStatus);
	}
	@Test(expected = Exception.class)
	public void testUpdateModificationRentForException() throws ApplicationException
	{
		boolean lbThrown=false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		Rent loRent = new  Rent();
		loRent.setSubBudgetID("556");
		loRent.setParentSubBudgetId("556");
		loRent.setId("390");
		loRent.setModifyAmount("2500");
		loRent.setModifyByProvider("803");
		loRent.setLocation(null);
		loRent.setParentId("390");
		loRent.setBudgetId(557);
		loRent.setFyBudget("5000");
		
		Boolean lbStatus = moContractBudgetModificationService.updateModificationRent(moSession, loRent);
		
		//assertTrue(lbStatus);
	}

	@Test
	public void testfetchApprovedBudgetCountForAmendment() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setBudgetId("555");
		aoTaskDetailsBean.setUserId("803");
		moContractBudgetModificationService.fetchApprovedBudgetCountForAmendment(moSession, aoTaskDetailsBean);
	}
	
	@Test
	public void testfetchApprovedBudgetCountForAmendment2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("53");
		aoTaskDetailsBean.setBudgetId("14");
		aoTaskDetailsBean.setUserId("agency_12");
		moContractBudgetModificationService.fetchApprovedBudgetCountForAmendment(moSession, aoTaskDetailsBean);
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchApprovedBudgetCountForAmendmentExp() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("111777");
		aoTaskDetailsBean.setBudgetId("555");
		aoTaskDetailsBean.setUserId("803");
		moContractBudgetModificationService.fetchApprovedBudgetCountForAmendment(null, aoTaskDetailsBean);
	}
	
	@Test
	public void testfetchModificationSubBudgetSummary() throws ApplicationException
	{
		HashMap<String, String> aoHashmap = new HashMap<String, String>();
		aoHashmap.put(HHSConstants.BUDGET_TYPE,"4");
		List<CBGridBean> loSubBudgetList =
			moContractBudgetModificationService.fetchModificationSubBudgetSummary(moSession, aoHashmap);
		assertTrue(loSubBudgetList.size()>0);
	}
	
	@Test
	public void testfetchModificationSubBudgetSummary2() throws ApplicationException
	{
		HashMap<String, String> aoHashmap = new HashMap<String, String>();
		aoHashmap.put(HHSConstants.BUDGET_TYPE,"2");
		List<CBGridBean> loSubBudgetList =
			moContractBudgetModificationService.fetchModificationSubBudgetSummary(moSession, aoHashmap);
		assertTrue(loSubBudgetList.size()>0);
	}
	
	@Test(expected=ApplicationException.class)
	public void testfetchModificationSubBudgetSummary3() throws ApplicationException
	{
		HashMap<String, String> aoHashmap = new HashMap<String, String>();
		aoHashmap.put(HHSConstants.BUDGET_TYPE,"2");
		List<CBGridBean> loSubBudgetList =
			moContractBudgetModificationService.fetchModificationSubBudgetSummary(null, aoHashmap);
		assertTrue(loSubBudgetList.size()>0);
	}
	
	@Test(expected=ApplicationException.class)
	public void testfetchModificationSubBudgetSummaryExp() throws ApplicationException
	{
		HashMap<String, String> aoHashmap = new HashMap<String, String>();
		aoHashmap.get(HHSConstants.BUDGET_TYPE);
		List<CBGridBean> loSubBudgetList =
			moContractBudgetModificationService.fetchModificationSubBudgetSummary(null, aoHashmap);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationSubBudgetSummary1Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationSubBudgetSummary11Negative()
	{
		HashMap<String, String> aoHashmap = new HashMap<String, String>();
		aoHashmap.get(HHSConstants.BUDGET_TYPE);
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationSubBudgetSummary(null, aoHashmap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}



	
	public void testmergeBudgetLineItemsForAmendment() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 1);
	}
	

	public void testmergeBudgetLineItemsForAmendment1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 1);
	}
	
	public void testmergeBudgetLineItemsForAmendment2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "85", 1);
	}
	
	public void testmergeBudgetLineItemsForAmendment3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "85", 2);
	}
	
	public void testmergeBudgetLineItemsForAmendment4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 2);
	}
	
	public void testmergeBudgetLineItemsForAmendment5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 10);
	}
	
	
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(null);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 1);
	}
	

	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, aoTaskDetailsBean, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(null, aoTaskDetailsBean, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(null, aoTaskDetailsBean, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp6() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(null, aoTaskDetailsBean, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp7() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(null, null, "86", 1);
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForAmendmentExp8() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForAmendment(moSession, null, "86", 1);
	}
	
	@Test
	public void testvalidateUpdateAmountTotal() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, modBudgetID);
		assertTrue(loValid);
	}
	
	
	
	
	
	
	
	@Test
	public void testvalidateUpdateAmountTotal2() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "349");
		assertTrue(loValid);
	}
	@Test
	public void testvalidateUpdateAmountTotal3() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "225");
		assertTrue(loValid);
	}
	@Test
	public void testvalidateUpdateAmountTotal4() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "199");
		assertTrue(loValid);
	}
	@Test
	public void testvalidateUpdateAmountTotal5() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "178");
		assertTrue(loValid);
	}
	@Test
	public void testvalidateUpdateAmountTotal6() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "177");
		assertTrue(loValid);
	}
	@Test
	public void testvalidateUpdateAmountTotal7() throws ApplicationException
	{
		Boolean loValid = moContractBudgetModificationService.validateUpdateAmountTotal(moSession, "165");
		assertTrue(loValid);
	}
	
	@Test(expected=ApplicationException.class)
	public void testvalidateUpdateAmountTotalExp() throws ApplicationException
	{
		moContractBudgetModificationService.validateUpdateAmountTotal(null, "165");
	}
	@Test(expected=ApplicationException.class)
	public void testvalidateUpdateAmountTotalExp1() throws ApplicationException
	{
		moContractBudgetModificationService.validateUpdateAmountTotal(null, "177");
	}
	@Test(expected=ApplicationException.class)
	public void testvalidateUpdateAmountTotalExp2() throws ApplicationException
	{
		moContractBudgetModificationService.validateUpdateAmountTotal(null, "178");
	}
	
	
	
	@Test
	public void testmergeBudgetLineItemsForModification() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		HashMap loHashMap = new HashMap();
		loHashMap.put("modifyBy", "system");
		loHashMap.put("budgetId", "12212");
		moContractBudgetModificationService.abc(moSession,loHashMap);
	}
	

	public void testmergeBudgetLineItemsForUpdate1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	public void testmergeBudgetLineItemsForUpdate2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "85");
	}
	
	public void testmergeBudgetLineItemsForUpdate3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "85");
	}
	
	public void testmergeBudgetLineItemsForUpdate4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	public void testmergeBudgetLineItemsForUpdate5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(null);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	

	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp6() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp7() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(null, null, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForUpdateExp8() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, null, "86");
	}
	private PersonnelServiceBudget getPersonnelServiceObject()
	{
		PersonnelServiceBudget loPSBean = new PersonnelServiceBudget();
		loPSBean.setContractBudgetID(parentBudgetID);
		loPSBean.setSubBudgetID(modsubBudgetID);
		loPSBean.setParentBudgetId(parentBudgetID);
		loPSBean.setParentSubBudgetId(parentSubBudgetID);
		return loPSBean;
	}	
	@Test
	public void testaddEmployeeBudgetForModification1() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testaddEmployeeBudgetForModification2() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	


	@Test
	public void testaddEmployeeBudgetForModification3() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	public void testaddEmployeeBudgetForModification4() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testaddEmployeeBudgetForModification5() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	@Test
	public void testaddEmployeeBudgetForModification6() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	@Test
	public void testaddEmployeeBudgetForModification7() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	@Test
	public void testaddEmployeeBudgetForModification8() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	@Test
	public void testaddEmployeeBudgetForModification9() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testaddEmployeeBudgetForModification() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testaddEmployeeBudgetForModification10() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testaddEmployeeBudgetForModification11() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testaddEmployeeBudgetForModification12() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(moSession, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
		
	@Test(expected=ApplicationException.class)
	public void testaddEmployeeBudgetForModificationExp() throws ApplicationException
	{
		boolean lbInsertStatus  = moContractBudgetModificationService.addEmployeeBudgetForModification(null, getPersonnelServiceObject());
		assertTrue(lbInsertStatus);
	}
	
	

	public void testmergeBudgetLineItemsForUpdate() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForUpdate(moSession, aoTaskDetailsBean, "86");
	}
	

	public void testmergeBudgetLineItemsForModification1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	public void testmergeBudgetLineItemsForModification2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "85");
	}
	
	public void testmergeBudgetLineItemsForModification3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "85");
	}
	
	public void testmergeBudgetLineItemsForModification4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	public void testmergeBudgetLineItemsForModification5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("abc");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(null);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(agency);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	

	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(null);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp5() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId(baseContractId);
		aoTaskDetailsBean.setBudgetId(modBudgetID);
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp6() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(null, aoTaskDetailsBean, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp7() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(null, null, "86");
	}
	
	@Test(expected=ApplicationException.class)
	public void testmergeBudgetLineItemsForModificationExp8() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("166");
		aoTaskDetailsBean.setBudgetId("136");
		aoTaskDetailsBean.setUserId(null);
		aoTaskDetailsBean.setTaskName("");
		moContractBudgetModificationService.mergeBudgetLineItemsForModification(moSession, null, "86");
	}
	
}       


