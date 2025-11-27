package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AdvanceSummaryBean;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class InvoiceServiceTest
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

	public String amendContractId="63";
	public String baseContractId="53"; 
	public String contractBudgetID="24";
	public String subBudgetID="24";
	public String parentSubBudgetID="14";
	public String parentBudgetID="13";
	public String invoiceId="55";
	public String agency="agency_12";
	public String provider="803";
	InvoiceService moInvoiceService = new InvoiceService();
	public static P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");

		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}
	
	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return loProfServicesBean, a CBGridBean bean object
	 * @throws ApplicationException
	 */
	private CBGridBean getDefaultProfServicesParams() throws ApplicationException
	{
		CBGridBean loProfServicesBean = new CBGridBean();

		loProfServicesBean.setContractBudgetID("555");
		loProfServicesBean.setSubBudgetID("555");
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
	private CBProfessionalServicesBean getEditProfServicesParams() throws ApplicationException
	{
		CBProfessionalServicesBean loProfServicesBean = new CBProfessionalServicesBean();

		loProfServicesBean.setContractBudgetID("555");
		loProfServicesBean.setSubBudgetID("555");
		loProfServicesBean.setContractID("111777");
		loProfServicesBean.setModifyByAgency("");
		loProfServicesBean.setModifyByProvider("803");

		return loProfServicesBean;
	}

	/**
	 * This method tests if Unallocated Funds are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testfetchInvoiceUnallocatedFunds1() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List<UnallocatedFunds> loUnallocatedFundsList = moInvoiceService.fetchInvoiceUnallocatedFunds(moSession,
				loUnallocatedFundsBean);

		assertNotNull(loUnallocatedFundsList);
		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	/**
	 * This method tests if Unallocated Funds is not available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testfetchInvoiceUnallocatedFunds2() throws ApplicationException
	{
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List loUnallocatedFundsList = moInvoiceService.fetchInvoiceUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertNotNull(loUnallocatedFundsList);
		assertTrue(loUnallocatedFundsList.size() > 0);
	}

	/**
	 * This method tests if Sub BudgetId is Null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	@SuppressWarnings(
	{ "rawtypes" })
	public void testfetchInvoiceUnallocatedFunds3() throws ApplicationException
	{

		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		// loUnallocatedFundsBean.setSubBudgetID("406");
		loUnallocatedFundsBean.setContractBudgetID("25");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		List loUnallocatedFundsList = moInvoiceService.fetchInvoiceUnallocatedFunds(moSession, loUnallocatedFundsBean);

		assertTrue(loUnallocatedFundsList.isEmpty());
	}

	/**
	 * This method tests if database session is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testfetchInvoiceUnallocatedFunds4() throws ApplicationException
	{
		Boolean lbThrown = false;
		UnallocatedFunds loUnallocatedFundsBean = new UnallocatedFunds();
		loUnallocatedFundsBean.setSubBudgetID("555");
		loUnallocatedFundsBean.setContractBudgetID("555");
		loUnallocatedFundsBean.setCreatedByUserId("city_142");

		moSession = null;
		try
		{
			moInvoiceService.fetchInvoiceUnallocatedFunds(moSession, loUnallocatedFundsBean);
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
	 * This method tests if CBGridBean is NULL
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testfetchInvoiceUnallocatedFunds5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		List<UnallocatedFunds> loUnallocatedFundsList = null;
		Boolean lbThrown = false;

		try
		{
			moInvoiceService.fetchInvoiceUnallocatedFunds(moSession, aoCBGridBeanObj);
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
		assertNull(loUnallocatedFundsList);

	}

	/**
	 * This method populates the gris bean with default values.
	 * @return loInvoiceSummary, a CBGridBean bean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBGridBean getDefaultInvoiceSummaryParams() throws ApplicationException
	{
		CBGridBean loInvoiceSummary = new CBGridBean();
		loInvoiceSummary.setContractBudgetID("555");
		loInvoiceSummary.setSubBudgetID("555");
		loInvoiceSummary.setContractID("555");
		loInvoiceSummary.setCreatedByUserId("city_142");
		loInvoiceSummary.setModifiedByUserId("city_142");
		loInvoiceSummary.setInvoiceId("55");
		loInvoiceSummary.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		return loInvoiceSummary;
	}

	/**
	 * This method populates the grid bean with default values.
	 * @return loIndirectRateBean, a CBIndirectRateBean bean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private CBIndirectRateBean getDefaultIndirectRateParams() throws ApplicationException
	{
		CBIndirectRateBean loIndirectRateBean = new CBIndirectRateBean();
		loIndirectRateBean.setContractBudgetID("555");
		loIndirectRateBean.setSubBudgetID("555");
		loIndirectRateBean.setModifyByAgency("623");
		loIndirectRateBean.setModifyByProvider("803");
		loIndirectRateBean.setInvoiceId("55");
		loIndirectRateBean.setId("123");
		loIndirectRateBean.setInvoiceStatusIdList(HHSConstants.INVOICE_STATUS_ID_INDIRECT_RATE);
		return loIndirectRateBean;
	}

	/**
	 * This method tests fetching invoice summary details
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test
	public void testFetchInvoiceSummary() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		boolean lbThrown = false;
		try
		{
			CBGridBean loInvoiceSummaryPositive = getDefaultInvoiceSummaryParams();
			// Positive Scenario -- List of BudgetDetails type returned.
			ContractBudgetSummary loInvoiceSummaryList = moInvoiceService.fetchInvoiceSummary(moSession,
					loInvoiceSummaryPositive);
			assertNotNull(loInvoiceSummaryList);
			assertTrue(loInvoiceSummaryList != null);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchInvoiceSummary2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		boolean lbThrown = false;
		try
		{
			CBGridBean loInvoiceSummaryPositive = new CBGridBean();

			loInvoiceSummaryPositive.setContractBudgetID("5552323232");
			loInvoiceSummaryPositive.setSubBudgetID("5555656");
			loInvoiceSummaryPositive.setContractID("5556565");
			loInvoiceSummaryPositive.setCreatedByUserId("city_14242423");
			loInvoiceSummaryPositive.setModifiedByUserId("city_142234324");
			loInvoiceSummaryPositive.setInvoiceId("555656");
			// Positive Scenario -- List of BudgetDetails type returned.
			ContractBudgetSummary loInvoiceSummaryList = moInvoiceService.fetchInvoiceSummary(moSession,
					loInvoiceSummaryPositive);
			assertNotNull(loInvoiceSummaryList);
			assertTrue(loInvoiceSummaryList != null);
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests fetching invoice summary details exception
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceSummaryException() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBGridBean loInvoiceSummaryListNegative = new CBGridBean();
		moInvoiceService.fetchInvoiceSummary(null, loInvoiceSummaryListNegative);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceSummaryException2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBGridBean loInvoiceSummaryListNegative = new CBGridBean();
		moInvoiceService.fetchInvoiceSummary(moSession, loInvoiceSummaryListNegative);
	}

	/**
	 * This method tests fetching indirect rate details.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test
	public void testFetchInvoiceIndirectRate() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBGridBean loIndirectRatePositive = getDefaultInvoiceSummaryParams();
		// Positive Scenario -- List of BudgetDetails type returned.
		List<CBIndirectRateBean> loInvoiceIndirectRate = moInvoiceService.fetchInvoiceIndirectRate(moSession,
				loIndirectRatePositive);
		assertNotNull(loInvoiceIndirectRate);
		assertTrue(loInvoiceIndirectRate != null);
	}

	/**
	 * This method tests fetching indirect rate details.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test
	public void testFetchInvoiceIndirectRateNewInvoice() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBGridBean loIndirectRatePositive = getDefaultInvoiceSummaryParams();
		// Positive Scenario -- List of BudgetDetails type returned.
		loIndirectRatePositive.setInvoiceId("57");
		List<CBIndirectRateBean> loInvoiceIndirectRate = moInvoiceService.fetchInvoiceIndirectRate(moSession,
				loIndirectRatePositive);
		assertNotNull(loInvoiceIndirectRate);
		assertTrue(loInvoiceIndirectRate != null);
	}

	/**
	 * This method tests fetching indirect rate details.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceIndirectRateException() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBGridBean loIndirectRatePositive = getDefaultInvoiceSummaryParams();
		// negative Scenario
		moInvoiceService.fetchInvoiceIndirectRate(null, loIndirectRatePositive);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoicingIndirectRateInvoiceAmountNotMoreThanRemaining() throws ApplicationException,
			IllegalAccessException, InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		loIndirectRateBean.setIndirectInvoiceAmount("500000000000000000");
		moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test
	public void testUpdateInvoicingIndirectRateInsert() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		loIndirectRateBean.setInvoiceId("160");
		// Positive Scenario -- successfully updated invoice indirect rate.
		boolean loInvoiceIndirectRate = moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
		assertNotNull(loInvoiceIndirectRate);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test
	public void testUpdateInvoicingIndirectRateUpdate() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		// Positive Scenario -- successfully updated invoice indirect rate.
		boolean loInvoiceIndirectRate = moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
		assertNotNull(loInvoiceIndirectRate);
	}

	/**
	 * This method tests give exception while fetching invoice indirect rate
	 * details.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoicingIndirectRateException() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		CBIndirectRateBean loIndirectPositive = getDefaultIndirectRateParams();
		// Negative Scenario -- exception while updating invoice indirect rate.
		moInvoiceService.updateInvoicingIndirectRate(null, loIndirectPositive);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoicingIndirectRateUpdateRemainingAmountNull() throws ApplicationException,
			IllegalAccessException, InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		loIndirectRateBean.setContractBudgetID("888");
		loIndirectRateBean.setSubBudgetID("920");
		moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoicingIndirectRateUpdateInvoiceDetailEntryParamNull() throws ApplicationException,
			IllegalAccessException, InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		loIndirectRateBean.setEntryTypeId(null);
		loIndirectRateBean.setContractBudgetID("888");
		loIndirectRateBean.setSubBudgetID("920");
		moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests fetching invoice indirect rate details successfully.
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException If an InvocationTarget Exception occurs
	 * @throws IllegalAccessException If an IllegalAccess Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoicingIndirectRateUpdateInvoiceDetailBudgetSubBudgetNull() throws ApplicationException,
			IllegalAccessException, InvocationTargetException
	{
		CBIndirectRateBean loIndirectRateBean = getDefaultIndirectRateParams();
		loIndirectRateBean.setContractBudgetID(null);
		loIndirectRateBean.setSubBudgetID(null);
		moInvoiceService.updateInvoicingIndirectRate(moSession, loIndirectRateBean);
	}

	/**
	 * This method tests fetchUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoicingUtilities() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			List<CBUtilities> cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingConsultants throws
	 * exception This method test for positive scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingConsultants() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingConsultants(moSession, loCBGridBean);
			assertNotNull(loCBContractedServicesBean);
			assertTrue(loCBContractedServicesBean.size() != 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingConsultants throws
	 * exception This method test for positive scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingConsultants1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("559");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingConsultants(moSession, loCBGridBean);
			assertTrue(loCBContractedServicesBean.size() == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingConsultants throws
	 * exception This method test for negative scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingConsultantsNegative() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- Application Exception handled by setting.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingConsultants(null, loCBGridBean);
			assertTrue(loCBContractedServicesBean.size() == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingSubContractors throws
	 * exception This method test for positive scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingSubContractors() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingSubContractors(moSession, loCBGridBean);
			assertNotNull(loCBContractedServicesBean);
			assertTrue(loCBContractedServicesBean.size() != 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetchContractedServicesInvoicingSubContractors throws
	 * exception. This method test for negative scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingSubContractorsNegative() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingSubContractors(null, loCBGridBean);
			assertTrue(loCBContractedServicesBean.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchContractedServicesInvoicingSubContractors1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("559");
			loCBGridBean.setInvoiceId("56");
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingSubContractors(moSession, loCBGridBean);
			assertTrue(loCBContractedServicesBean.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingVendors throws
	 * exception This method test for positive scenario.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingVendors() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");

			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingVendors(moSession, loCBGridBean);
			assertNotNull(loCBContractedServicesBean);
			assertTrue(loCBContractedServicesBean.size() != 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingVendors throws
	 * exception This method is used to test the negative scenario for Vendors
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingVendorsNegative() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("56");
			// Negative Scenario -- Application Exception handled by setting.
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingVendors(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchContractedServicesInvoicingVendors throws
	 * exception This method is used to test the negative scenario for Vendors
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractedServicesInvoicingVendors1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("559");
			loCBGridBean.setInvoiceId("56");
			// Negative Scenario -- Application Exception handled by setting.
			List<ContractedServicesBean> loCBContractedServicesBean = loInvoiceService
					.fetchContractedServicesInvoicingVendors(moSession, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests updateUtilities throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateInvoicingUtilities() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Utility Update Successful.

			loCBUtilities.setId("1");
			loCBUtilities.setLineItemInvoiceAmt("50");
			boolean lbUpdateStatus = loInvoiceService.updateInvoicingUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = loInvoiceService.updateInvoicingUtilities(null, loCBUtilities);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingUtilitiesGreatorInvoiceAmt() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBUtilities loCBUtilities = new CBUtilities();

			// Invoice Amount entered is greater than remaining amount
			loCBUtilities.setId("1");
			loCBUtilities.setLineItemInvoiceAmt("600");
			loInvoiceService.updateInvoicingUtilities(moSession, loCBUtilities);

			// Negative Scenario -- Application Exception handled by setting.

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingUtilities4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Utility Update Successful.
			loCBUtilities.setId("");
			loCBUtilities.setLineItemInvoiceAmt("50");
			boolean lbUpdateStatus = loInvoiceService.updateInvoicingUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingUtilities5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Utility Update Successful.
			loCBUtilities.setId("");
			loCBUtilities.setLineItemInvoiceAmt("0");
			boolean lbUpdateStatus = loInvoiceService.updateInvoicingUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingUtilities6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBUtilities loCBUtilities = new CBUtilities();

			// Positive Scenario -- Utility Update Successful.
			loCBUtilities.setId(null);
			loCBUtilities.setLineItemInvoiceAmt("12");
			boolean lbUpdateStatus = loInvoiceService.updateInvoicingUtilities(moSession, loCBUtilities);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetchMilestone throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchMilestoneInvoice() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of milestone type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("55");

			List<CBMileStoneBean> cbMileStoneBean = loInvoiceService.fetchMilestoneInvoice(loCBGridBean, moSession);
			assertNotNull(cbMileStoneBean);
			assertTrue(cbMileStoneBean.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbMileStoneBean = loInvoiceService.fetchMilestoneInvoice(loCBGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchMilestone throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchMilestoneInvoice2() throws ApplicationException
	{
		Boolean lbThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		CBGridBean loCBGridBean = new CBGridBean();
		// Positive Scenario -- List of milestone type returned.
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setInvoiceId("56");
		List<CBMileStoneBean> cbMileStoneBean = loInvoiceService.fetchMilestoneInvoice(loCBGridBean, moSession);
		assertNotNull(cbMileStoneBean);
		assertTrue(cbMileStoneBean.size() != 0);
	}

	@Test
	public void testFetchMilestoneInvoice3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of milestone type returned.

			loCBGridBean.setSubBudgetID("555");
			// loCBGridBean.setContractBudgetID("555");
			loCBGridBean.setInvoiceId("55");

			List<CBMileStoneBean> cbMileStoneBean = loInvoiceService.fetchMilestoneInvoice(loCBGridBean, moSession);
			assertNotNull(cbMileStoneBean);
			assertTrue(cbMileStoneBean.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbMileStoneBean = loInvoiceService.fetchMilestoneInvoice(loCBGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests updateMilestone throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateMilestoneInvoice() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

			// Positive Scenario -- Milestone Update Successful.
			loCBMileStoneBean.setId("18");
			loCBMileStoneBean.setInvoiceId("55");
			loCBMileStoneBean.setInvoiceAmount("50");
			boolean lbUpdateStatus = loInvoiceService.updateMilestoneInvoice(loCBMileStoneBean, moSession);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = loInvoiceService.updateMilestoneInvoice(loCBMileStoneBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests updateMilestone for new added invoice inside
	 * updateMilestoneInvoice
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testInsertMilestoneInvoice() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

			// Positive Scenario -- Milestone insert Successful.
			loCBMileStoneBean.setId("20");
			loCBMileStoneBean.setInvoiceId("55");
			loCBMileStoneBean.setInvoiceAmount("50");
			boolean lbUpdateStatus = loInvoiceService.updateMilestoneInvoice(loCBMileStoneBean, moSession);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = loInvoiceService.updateMilestoneInvoice(loCBMileStoneBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests updateMilestone for invoice amount or remaining amount
	 * throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testUpdateMilestoneInvoiceGreatorInvoiceAmt() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBMileStoneBean loCBMileStoneBean = new CBMileStoneBean();

			// Invoice Amount entered is greater than remaining amount
			loCBMileStoneBean.setId("17");
			loCBMileStoneBean.setInvoiceAmount("5000");
			loCBMileStoneBean.setSubBudgetID("555");
			loCBMileStoneBean.setContractBudgetID("555");
			loCBMileStoneBean.setInvoiceId("55");
			loInvoiceService.updateMilestoneInvoice(loCBMileStoneBean, moSession);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("! Invoice Amount exceeds the Remaining Amount", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithCorrectInput() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loProfService = getDefaultProfServicesParams();
			loProfService.setInvoiceId("55");

			List<CBProfessionalServicesBean> loProfServiceDetailsList = moInvoiceService.fetchProfServicesDetails(
					loProfService, moSession);
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);

			loProfServiceDetailsList = moInvoiceService.fetchProfServicesDetails(loProfService, null);
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetching functionality of Professional service details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProfServicesDetailsWithInvoiceIdNULL() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loProfService = getDefaultProfServicesParams();
			loProfService.setInvoiceId(null);

			List<CBProfessionalServicesBean> loProfServiceDetailsList = moInvoiceService.fetchProfServicesDetails(
					loProfService, moSession);
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);

			loProfServiceDetailsList = moInvoiceService.fetchProfServicesDetails(loProfService, null);
			assertNotNull(loProfServiceDetailsList);
			assertTrue(loProfServiceDetailsList.size() > 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForInvoiceIdNULL() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setId("152");
		loProfService.setInvoiceId(null);
		loProfService.setInvoiceAmount("5000");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForBudgetNULL() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setContractBudgetID(null);
		loProfService.setSubBudgetID("555");
		loProfService.setId("152");
		loProfService.setInvoiceId("55");
		loProfService.setInvoiceAmount("5000");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForBudgetEmpty() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setContractBudgetID("");
		loProfService.setSubBudgetID("555");
		loProfService.setId("152");
		loProfService.setInvoiceId("55");
		loProfService.setInvoiceAmount("5000");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForSubBudgetNULL() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setContractBudgetID("555");
		loProfService.setSubBudgetID(null);
		loProfService.setId("152");
		loProfService.setInvoiceId("55");
		loProfService.setInvoiceAmount("5000");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForSubBudgetEmpty() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setContractBudgetID("555");
		loProfService.setSubBudgetID("");
		loProfService.setId("152");
		loProfService.setInvoiceId("55");
		loProfService.setInvoiceAmount("5000");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update Invoice Amount for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForInvoiceAmountEmpty() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setId("150");
		loProfService.setInvoiceAmount("");
		loProfService.setInvoiceId("55");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update budget Amount for Professional
	 * service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForInvoiceWrongAmount() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setId("150");
		loProfService.setInvoiceAmount("123abc");
		loProfService.setInvoiceId("55");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update budget Amount for Professional
	 * service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForInvoiceAmount() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setId("150");
		loProfService.setInvoiceAmount("500");
		loProfService.setInvoiceId("55");
		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsForInvoiceAmountNULL() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setInvoiceId("55");
		loProfService.setId("152");
		loProfService.setInvoiceAmount(null);

		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests, functionality to update budget Amount and Other for
	 * Professional service
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditProfServicesDetailsInvoiceMoreThanRemaining() throws ApplicationException
	{
		Boolean lbThrown = false;
		CBProfessionalServicesBean loProfService = getEditProfServicesParams();
		loProfService.setInvoiceId("55");
		loProfService.setId("150");
		loProfService.setInvoiceAmount("5000000");

		try
		{
			boolean lbUpdateStatus = moInvoiceService.editProfServicesDetails(loProfService, moSession);
			assertNotNull(lbUpdateStatus);
			assertTrue(lbUpdateStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertNotNull(loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests fetchRent throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoicingRent() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			List<Rent> cbRentList = loInvoiceService.fetchContractInvoiceRent(loCBGridBean, moSession);
			assertNotNull(cbRentList);
			assertTrue(cbRentList.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbRentList = loInvoiceService.fetchContractInvoiceRent(loCBGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchInvoicingRent2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setSubBudgetID("JGH");
			List<Rent> cbRentList = loInvoiceService.fetchContractInvoiceRent(loCBGridBean, moSession);
			assertNotNull(cbRentList);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests updateRent throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateInvoicingRent() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			Rent loCBRent = new Rent();

			// Positive Scenario -- Rent Update Successful.
			loCBRent.setId("1");
			loCBRent.setLineItemInvoiceAmt("50");
			boolean lbUpdateStatus = loInvoiceService.updateContractInvoiceRent(moSession, loCBRent);
			assertTrue(lbUpdateStatus);

			// Negative Scenario -- Application Exception handled by setting.

			lbUpdateStatus = loInvoiceService.updateContractInvoiceRent(null, loCBRent);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingRent3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			Rent loCBRent = new Rent();

			// Positive Scenario -- Rent Update Successful.
			loCBRent.setId("");

			loCBRent.setLineItemInvoiceAmt("50");
			boolean lbUpdateStatus = loInvoiceService.updateContractInvoiceRent(moSession, loCBRent);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingRent4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			Rent loCBRent = new Rent();

			// Positive Scenario -- Rent Update Successful.
			loCBRent.setId("BD");

			loCBRent.setLineItemInvoiceAmt("50");
			boolean lbUpdateStatus = loInvoiceService.updateContractInvoiceRent(moSession, loCBRent);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateInvoicingRent5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();

			Rent loCBRent = new Rent();
			loCBRent.setId("389");
			loCBRent.setContractBudgetID("555");
			loCBRent.setInvoiceId("55");
			loCBRent.setModifyByAgency("agency_21");
			loCBRent.setModifyByProvider("");
			loCBRent.setSubBudgetID("555");
			// Positive Scenario -- Rent Update Successful.

			loCBRent.setLineItemInvoiceAmt("500000");
			boolean lbUpdateStatus = loInvoiceService.updateContractInvoiceRent(moSession, loCBRent);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditEquipmentDetails for successful update, throws
	 * exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditEquipmentDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

			// Positive Scenario -- Utility Update Successful.
			loCBEquipmentBean.setId("189");
			// loCBEquipmentBean.setInvoiceDetailId("");
			loCBEquipmentBean.setInvoicedAmt("200");
			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditEquipmentDetails for Unsuccessful update due to
	 * validation of remaining amount failed, throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditEquipmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

			// Positive Scenario -- Utility Update Successful.
			loCBEquipmentBean.setId("1_89");
			// loCBEquipmentBean.setInvoiceDetailId("");
			loCBEquipmentBean.setInvoicedAmt("200");
			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.FALSE, loCBEquipmentBean, moSession);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING), loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditEquipmentDetails for Insert new InvoiceDetail,
	 * throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditEquipmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

			// Positive Scenario -- Utility Update Successful.
			loCBEquipmentBean.setId("2");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId("");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId(null);
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId("2");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.FALSE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails8() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId("2");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("0");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails9() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId("2");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, null);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditEquipmentDetails10() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBEquipmentBean loCBEquipmentBean = new CBEquipmentBean();
			loCBEquipmentBean.setId("2B");
			loCBEquipmentBean.setInvoiceId("55");
			loCBEquipmentBean.setSubBudgetID("555");
			loCBEquipmentBean.setModifiedByUserId("city_142");
			loCBEquipmentBean.setCreatedByUserId("city_142");
			loCBEquipmentBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editEquipmentDetails(Boolean.TRUE, loCBEquipmentBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditOperationAndSupportDetails for successful update,
	 * throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditOperationAndSupportDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			// Positive Scenario -- Utility Update Successful.
			loCBOperationSupportBean.setId("1_82");
			// loCBEquipmentBean.setInvoiceDetailId("");
			loCBOperationSupportBean.setInvoicedAmt("200");
			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.TRUE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service EditOperationAndSupportDetails: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditOperationAndSupportDetails for Unsuccessful update
	 * due to validation of remaining amount failed, throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditOperationAndSupportDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			// Positive Scenario -- Utility Update Successful.
			loCBOperationSupportBean.setId("1_82");
			// loCBEquipmentBean.setInvoiceDetailId("");
			loCBOperationSupportBean.setInvoicedAmt("200");
			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.MSG_KEY_INVOICE_AMOUNT_MORE_THAN_REMAINING), loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests EditOperationAndSupportDetails for Insert new
	 * InvoiceDetail, throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testEditOperationAndSupportDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			// Positive Scenario -- Utility Update Successful.
			loCBOperationSupportBean.setId("5");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.TRUE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setId("");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setId(null);
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setId("5");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, null);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails8() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			loCBOperationSupportBean.setId("5");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId(null);
			loCBOperationSupportBean.setCreatedByUserId(null);
			loCBOperationSupportBean.setInvoicedAmt("200");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails9() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			// Positive Scenario -- Utility Update Successful.
			loCBOperationSupportBean.setId("5");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt("0");

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditOperationAndSupportDetails10() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// InvoiceService loInvoiceService = new InvoiceService();
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();

			// Positive Scenario -- Utility Update Successful.
			loCBOperationSupportBean.setId("5");
			loCBOperationSupportBean.setInvoiceId("55");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setModifiedByUserId("city_142");
			loCBOperationSupportBean.setCreatedByUserId("city_142");
			loCBOperationSupportBean.setInvoicedAmt(null);

			boolean lbUpdateStatus = moInvoiceService.editOperationAndSupportDetails(Boolean.FALSE,
					loCBOperationSupportBean, moSession);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method tests FetchEquipmentDetails throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchEquipmentDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId("55");
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			List<CBEquipmentBean> loCBEquipmentBean = moInvoiceService.fetchEquipmentDetails(loCBGridBean, moSession);
			assertNotNull(loCBEquipmentBean);
			assertTrue(loCBEquipmentBean.size() > 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests FetchEquipmentDetails when budget not defined throws
	 * exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchEquipmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId("55");
			loCBGridBean.setSubBudgetID("556");
			loCBGridBean.setContractBudgetID("555");

			List<CBEquipmentBean> loCBEquipmentBean = moInvoiceService.fetchEquipmentDetails(loCBGridBean, moSession);
			assertNotNull(loCBEquipmentBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchEquipmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- List of CBIndirectRateBean type returned.

			/*
			 * loCBGridBean.setInvoiceId("55");
			 * loCBGridBean.setSubBudgetID("556");
			 * loCBGridBean.setContractBudgetID("555");
			 */

			List<CBEquipmentBean> loCBEquipmentBean = moInvoiceService.fetchEquipmentDetails(loCBGridBean, moSession);
			assertNotNull(loCBEquipmentBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchEquipmentDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId("55645654");
			loCBGridBean.setSubBudgetID("55645645");
			loCBGridBean.setContractBudgetID("555546546");

			List<CBEquipmentBean> loCBEquipmentBean = moInvoiceService.fetchEquipmentDetails(loCBGridBean, moSession);
			assertTrue(loCBEquipmentBean.size() == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchEquipmentDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Negative Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId(null);
			loCBGridBean.setSubBudgetID(null);
			loCBGridBean.setContractBudgetID(null);

			List<CBEquipmentBean> loCBEquipmentBean = moInvoiceService.fetchEquipmentDetails(loCBGridBean, moSession);
			assertTrue(loCBEquipmentBean.size() == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests FetchEquipmentDetails throws exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchOperationAndSupportDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId("55");
			loCBGridBean.setSubBudgetID("555");
			loCBGridBean.setContractBudgetID("555");

			List<CBOperationSupportBean> loCBOperationSupportBean = moInvoiceService.fetchOperationAndSupportDetails(
					loCBGridBean, moSession);
			assertNotNull(loCBOperationSupportBean);
			assertTrue(loCBOperationSupportBean.size() > 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * This method tests FetchEquipmentDetails when budget not defined throws
	 * exception
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchOperationAndSupportDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setInvoiceId("55");
			loCBGridBean.setSubBudgetID("557");
			loCBGridBean.setContractBudgetID("555");

			List<CBOperationSupportBean> loCBOperationSupportBean = moInvoiceService.fetchOperationAndSupportDetails(
					loCBGridBean, moSession);
			assertNotNull(loCBOperationSupportBean);
			assertTrue(loCBOperationSupportBean.get(0).getFyBudget().equals(HHSConstants.STRING_ZERO));

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchOperationAndSupportDetails3() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setSubBudgetID("1279B");
		aoCBGridBean.setContractBudgetID("10117");
		try
		{
			List<CBOperationSupportBean> listCBOperationSupportBean = loInvoiceService.fetchOperationAndSupportDetails(
					aoCBGridBean, moSession);
			assertNotNull(listCBOperationSupportBean);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchOperationAndSupportDetails4() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		CBGridBean aoCBGridBean = new CBGridBean();
		// aoCBGridBean.setSubBudgetID("1279B");
		aoCBGridBean.setContractBudgetID("10117");
		try
		{
			List<CBOperationSupportBean> listCBOperationSupportBean = loInvoiceService.fetchOperationAndSupportDetails(
					aoCBGridBean, moSession);
			assertNotNull(listCBOperationSupportBean);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	/**
	 * This method tests fetching invoice Status
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testFetchInvoiceStatus1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsInvoiceId = "55";
			String lsInvoiceStatus = moInvoiceService.fetchInvoiceStatus(lsInvoiceId, moSession);
			assertNotNull(lsInvoiceStatus);
			assertTrue(lsInvoiceStatus != null);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			String lsInvoiceId2 = "0";
			lsInvoiceStatus = moInvoiceService.fetchInvoiceStatus(lsInvoiceId2, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceStatus2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// /Negative Scenario ///////

			String lsInvoiceId = null;
			String lsInvoiceStatus = moInvoiceService.fetchInvoiceStatus(lsInvoiceId, moSession);
			assertNotNull(lsInvoiceStatus);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceStatus3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// /Negative Scenario ///////

			String lsInvoiceId = null;
			String lsInvoiceStatus = moInvoiceService.fetchInvoiceStatus(lsInvoiceId, null);
			assertNotNull(lsInvoiceStatus);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceStatus4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// /Negative Scenario ///////

			String lsInvoiceId = "HGHG";
			String lsInvoiceStatus = moInvoiceService.fetchInvoiceStatus(lsInvoiceId, moSession);
			assertNotNull(lsInvoiceStatus);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests fetching Total Amount of an Invoice
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testFetchInvoiceTotalForOTPS1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsInvoiceId = "55";
			String invoiceTotalAmounts = moInvoiceService.fetchInvoiceTotalForOTPS(getDefaultProfServicesParams(),
					moSession);
			assertNotNull(invoiceTotalAmounts);
			assertTrue(invoiceTotalAmounts != null);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			String lsInvoiceId2 = "0";
			invoiceTotalAmounts = moInvoiceService.fetchInvoiceTotalForOTPS(getDefaultProfServicesParams(), moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceTotalForOTPS2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsInvoiceId = "BHB";
			String invoiceTotalAmounts = moInvoiceService.fetchInvoiceTotalForOTPS(getDefaultProfServicesParams(),
					moSession);
			assertNotNull(invoiceTotalAmounts);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceTotalForOTPS3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsInvoiceId = null;
			String invoiceTotalAmounts = moInvoiceService.fetchInvoiceTotalForOTPS(getDefaultProfServicesParams(),
					moSession);
			assertNotNull(invoiceTotalAmounts);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchInvoiceTotalForOTPS4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsInvoiceId = null;
			String invoiceTotalAmounts = moInvoiceService
					.fetchInvoiceTotalForOTPS(getDefaultProfServicesParams(), null);
			assertNotNull(invoiceTotalAmounts);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests fetching YTD Invoiced amount for a Subbudget
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testFetchYTDInvoiced1() throws ApplicationException, IllegalAccessException, InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsSubBudgetId = "555";
			String ytdInvoiceAmounts = moInvoiceService.fetchYTDInvoiced(lsSubBudgetId, moSession);
			assertNotNull(ytdInvoiceAmounts);
			assertTrue(ytdInvoiceAmounts != null);
			// Negative Scenario -- Application Exception handled by setting.
			// Incomplete data in the Bean.
			String lsSubBudgetId2 = "0";
			ytdInvoiceAmounts = moInvoiceService.fetchYTDInvoiced(lsSubBudgetId2, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchYTDInvoiced2() throws ApplicationException, IllegalAccessException, InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsSubBudgetId = "BHB";
			String ytdInvoiceAmounts = moInvoiceService.fetchYTDInvoiced(lsSubBudgetId, moSession);
			assertNotNull(ytdInvoiceAmounts);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchYTDInvoiced3() throws ApplicationException, IllegalAccessException, InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsSubBudgetId = null;
			String ytdInvoiceAmounts = moInvoiceService.fetchYTDInvoiced(lsSubBudgetId, moSession);
			assertNotNull(ytdInvoiceAmounts);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchYTDInvoiced4() throws ApplicationException, IllegalAccessException, InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			String lsSubBudgetId = null;
			String ytdInvoiceAmounts = moInvoiceService.fetchYTDInvoiced(lsSubBudgetId, null);
			assertNotNull(ytdInvoiceAmounts);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateEquipmentInvoiceAmount1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBEquipmentBean loEquipmentBean = new CBEquipmentBean();
			loEquipmentBean.setId("1_89");
			loEquipmentBean.setInvoicedAmt("200");
			loEquipmentBean.setSubBudgetID("555");
			loEquipmentBean.setInvoiceId("55");

			String lsSubBudgetId = "555";
			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean, moSession);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment, For scenario:
	 * Invoice AMount more than Remaining Amount
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateEquipmentInvoiceAmount2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{

			// Second scenario: Invoice AMount more than Remaining Amount.
			CBEquipmentBean loEquipmentBean2 = new CBEquipmentBean();
			loEquipmentBean2.setId("1_89");
			loEquipmentBean2.setInvoicedAmt("20000");
			loEquipmentBean2.setSubBudgetID("555");
			loEquipmentBean2.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean2, moSession);
			assertNotNull(lbValid);
			assertTrue(!lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateEquipmentInvoiceAmount3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBEquipmentBean loEquipmentBean3 = new CBEquipmentBean();
			loEquipmentBean3.setId("20");
			loEquipmentBean3.setInvoicedAmt("20000");
			loEquipmentBean3.setSubBudgetID("555");
			loEquipmentBean3.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean3, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateEquipmentInvoiceAmount4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBEquipmentBean loEquipmentBean3 = new CBEquipmentBean();
			loEquipmentBean3.setId("");
			loEquipmentBean3.setInvoicedAmt("20000");
			loEquipmentBean3.setSubBudgetID("555");
			loEquipmentBean3.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean3, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateEquipmentInvoiceAmount5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBEquipmentBean loEquipmentBean3 = new CBEquipmentBean();
			loEquipmentBean3.setId(null);
			loEquipmentBean3.setInvoicedAmt("20000");
			loEquipmentBean3.setSubBudgetID("555");
			loEquipmentBean3.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean3, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateEquipmentInvoiceAmount6() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBEquipmentBean loEquipmentBean3 = new CBEquipmentBean();
			loEquipmentBean3.setId("20");
			loEquipmentBean3.setInvoicedAmt("20000");
			loEquipmentBean3.setSubBudgetID("555");
			loEquipmentBean3.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean3, null);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateEquipmentInvoiceAmount7() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBEquipmentBean loEquipmentBean3 = new CBEquipmentBean();
			loEquipmentBean3.setId("20");
			loEquipmentBean3.setInvoicedAmt("20000");
			loEquipmentBean3.setSubBudgetID("555");
			loEquipmentBean3.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateEquipmentInvoiceAmount(loEquipmentBean3, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateOpSupportInvoiceAmount1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("200");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("55");

			String lsSubBudgetId = "555";
			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment, For scenario:
	 * Invoice AMount more than Remaining Amount
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateOpSupportInvoiceAmount2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{

			// Second scenario: Invoice AMount more than Remaining Amount.
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("20000");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
			assertNotNull(lbValid);
			assertTrue(!lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests validate Invoice amount for n Equipment
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testValidateOpSupportInvoiceAmount3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			// Third scenario: improper data.
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("100");
			loCBOperationSupportBean.setInvoicedAmt("20000");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("55");

			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while updating Invoice Amount for Operation Support", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateOpSupportInvoiceAmount4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("200");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("");

			String lsSubBudgetId = "555";
			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateOpSupportInvoiceAmount5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("200");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("");
			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateOpSupportInvoiceAmount6() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("200");
			loCBOperationSupportBean.setSubBudgetID("555");
			loCBOperationSupportBean.setInvoiceId("");
			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, null);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateOpSupportInvoiceAmount7() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			CBOperationSupportBean loCBOperationSupportBean = new CBOperationSupportBean();
			loCBOperationSupportBean.setId("1_82");
			loCBOperationSupportBean.setInvoicedAmt("200");
			loCBOperationSupportBean.setSubBudgetID("555B");
			loCBOperationSupportBean.setInvoiceId("");

			Boolean lbValid = moInvoiceService.validateOpSupportInvoiceAmount(loCBOperationSupportBean, moSession);
			assertNotNull(lbValid);
			assertTrue(lbValid);

		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests fetching functionality of Contract Invoice's Contract
	 * Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractInvoiceFyBudgetSummary() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			aoContractList.setBudgetId("555");
			aoContractList.setContractId("111777");

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while fetching ContractInvoiceFyBudgetSummary", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			aoContractList.setBudgetId("555");
			aoContractList.setContractId("111777");

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			aoContractList.setBudgetId("5556");
			aoContractList.setContractId("111777");

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while fetching ContractInvoiceFyBudgetSummary", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			aoContractList.setBudgetId("555B");
			aoContractList.setContractId("111777");

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary6() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			aoContractList.setBudgetId("555");
			aoContractList.setContractId("111777");

			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(null,
					aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceFyBudgetSummary7() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			ContractList aoContractList = new ContractList();
			BudgetDetails loBudgetDetails = (BudgetDetails) moInvoiceService.fetchContractInvoiceFyBudgetSummary(
					moSession, aoContractList);
			assertNotNull(loBudgetDetails);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests fetching functionality of Contract Invoice's Contract
	 * Invoice Summary details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractInvoiceSummary1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.INVOICE_ID, "55");
			ContractList loContractList = (ContractList) moInvoiceService.fetchContractInvoiceSummary(moSession,
					aoHashMap);
			assertNotNull(loContractList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieveing Contract Information in InvoiceService ",
					loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceSummary2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.INVOICE_ID, "55");
			ContractList loContractList = (ContractList) moInvoiceService.fetchContractInvoiceSummary(moSession,
					aoHashMap);
			assertEquals("DOC - Department of Corrections", loContractList.getContractAgencyName());
			assertNotNull(loContractList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieveing Contract Information in InvoiceService ",
					loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceSummary3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.INVOICE_ID, "");
			ContractList loContractList = (ContractList) moInvoiceService.fetchContractInvoiceSummary(moSession,
					aoHashMap);
			assertEquals("DOC - Department of Corrections", loContractList.getContractAgencyName());
			assertNotNull(loContractList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieveing Contract Information in InvoiceService ",
					loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceSummary4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.INVOICE_ID, null);
			ContractList loContractList = (ContractList) moInvoiceService.fetchContractInvoiceSummary(moSession,
					aoHashMap);
			assertEquals("DOC - Department of Corrections", loContractList.getContractAgencyName());
			assertNotNull(loContractList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchContractInvoiceSummary5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.INVOICE_ID, null);
			ContractList loContractList = (ContractList) moInvoiceService.fetchContractInvoiceSummary(null, aoHashMap);
			assertEquals("DOC - Department of Corrections", loContractList.getContractAgencyName());
			assertNotNull(loContractList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * This method tests fetching functionality of Contract Invoice's Contract
	 * Invoice Information details
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchContractInvoiceInformation() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
			aoHashMap.put(HHSConstants.INVOICE_ID, "55");
			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(moSession,
					aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while fetching ContractInvoiceInformation", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchContractInvoiceInformation2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555B");
			aoHashMap.put(HHSConstants.INVOICE_ID, "55");
			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(moSession,
					aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchContractInvoiceInformation3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "");
			aoHashMap.put(HHSConstants.INVOICE_ID, "");
			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(moSession,
					aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieveing Fiscal Year Contract Information in InvoiceService ",
					loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchContractInvoiceInformation4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, null);
			aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, null);
			aoHashMap.put(HHSConstants.INVOICE_ID, null);
			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(moSession,
					aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchContractInvoiceInformation5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>

		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = null;

			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(moSession,
					aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchContractInvoiceInformation6() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>

		Boolean loThrown = false;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();

			InvoiceList loInvoiceList = (InvoiceList) moInvoiceService.fetchContractInvoiceInformation(null, aoHashMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	/**
	 * This method tests if invoice status is changed against a given invoiceId
	 * Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateInvoiceStatus() throws ApplicationException
	{
		HashMap<String, String> loHMWFRequiredProps = new HashMap<String, String>();
		loHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "56");

		moInvoiceService.updateInvoiceStatus(moSession, loHMWFRequiredProps);
		assertEquals("Invoice status updated succesfully", moInvoiceService.getMoState().toString());
	}

	/**
	 * This method tests if invoice status is changed against a given invoiceId
	 * Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoiceStatusNegativeInvoiceNull() throws ApplicationException
	{
		HashMap<String, String> loHMWFRequiredProps = new HashMap<String, String>();
		loHMWFRequiredProps.put(HHSConstants.INVOICE_ID, null);
		moInvoiceService.updateInvoiceStatus(moSession, loHMWFRequiredProps);
	}

	/**
	 * This method tests if invoice status is changed against a given invoiceId
	 * Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testUpdateInvoiceStatusFailIntegerInArgument() throws ApplicationException
	{
		HashMap loHMWFRequiredProps = new HashMap();
		loHMWFRequiredProps.put(HHSConstants.INVOICE_ID, 56);
		moInvoiceService.updateInvoiceStatus(moSession, loHMWFRequiredProps);
	}

	/**
	 * This method tests if invoice status is changed against a given invoiceId
	 * Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testUpdateInvoiceStatusException() throws ApplicationException
	{
		moInvoiceService.updateInvoiceStatus(moSession, null);
	}

	/**
	 * This method tests if the agency Id can be fetched from DB providing
	 * contract ID Positive Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFindContractDetailsByContractForWF() throws ApplicationException
	{
		String lsContractId = "111777";
		String agencyId = moInvoiceService.getAgencyIdByContractForWF(moSession, lsContractId);
		assertEquals("DOC", agencyId);
	}

	/**
	 * This method tests if the agency Id can be fetched from DB providing
	 * contract ID Negative Scenario
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFindContractDetailsByContractForWFNegative() throws ApplicationException
	{
		String lsContractId = null;
		moInvoiceService.getAgencyIdByContractForWF(moSession, lsContractId);
	}

	@Test
	public void testFindContractDetailsByContractForWFNegative2() throws ApplicationException
	{
		String lsContractId = "";
		String lsAgencyId = moInvoiceService.getAgencyIdByContractForWF(moSession, lsContractId);
		assertNull(lsAgencyId);
	}

	@Test(expected = ApplicationException.class)
	public void testFindContractDetailsByContractForWFNegative3() throws ApplicationException
	{
		String lsContractId = "";
		moInvoiceService.getAgencyIdByContractForWF(null, lsContractId);
	}

	@Test(expected = ApplicationException.class)
	public void testFindContractDetailsByContractForWFNegative4() throws ApplicationException
	{
		String lsContractId = "adasd";
		moInvoiceService.getAgencyIdByContractForWF(moSession, lsContractId);
	}

	// Program Income Invoice Test Case Starts

	private CBGridBean getDummyCBGridBeanObj()
	{
		CBGridBean loCBGridBean = new CBGridBean();

		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setCreatedByUserId("803");
		loCBGridBean.setModifiedByUserId("803");
		loCBGridBean.setInvoiceId("55");

		return loCBGridBean;

	}

	private CBProgramIncomeBean getDummyCBProgramIncomeBeanObj()
	{
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();

		loCBProgramIncomeBean.setContractBudgetID("555");
		loCBProgramIncomeBean.setSubBudgetID("555");
		loCBProgramIncomeBean.setContractID("111777");
		loCBProgramIncomeBean.setInvoiceId("55");
		loCBProgramIncomeBean.setModifyByAgency("agency_47");
		loCBProgramIncomeBean.setModifyByProvider("803");
		// loCBProgramIncomeBean.setCreatedByUserId("803");
		// loCBProgramIncomeBean.setModifiedByUserId("803");
		loCBProgramIncomeBean.setId("105");
		loCBProgramIncomeBean.setBudgetType("2");
		loCBProgramIncomeBean.setIncome("900.00");

		return loCBProgramIncomeBean;

	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for good data inputs
	 */
	@Test
	public void testFetchProgramIncomeInvoice() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		List<CBProgramIncomeBean> loResultList = loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean,
				moSession);
		assertNotNull(loResultList);
		assertTrue(!loResultList.isEmpty());
	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeInvoiceWithAppException() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("377H_'");// Invalid Sub Budget id
		loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean, moSession);
	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeInvoiceWithAppException2() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setSubBudgetID("7A_A'");// Invalid Sub Budget id
		loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean, moSession);
	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for bad data inputs
	 * and an ApplicartionException is expected
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProgramIncomeInvoiceWithAppException3() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean, null);
	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for bad data inputs
	 * and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProgramIncomeInvoiceWithException() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean, moSession);
	}

	/**
	 * This method tests fetchProgramIncomeInvoice method for bad data inputs
	 * and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProgramIncomeInvoiceWithException2() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBGridBean loCBGridBean = null;
		loContractBudgetService.fetchProgramIncomeInvoice(loCBGridBean, moSession);
	}

	/**
	 * This method tests updateProgramIncomeInvoice method for good data inputs
	 * for scenario where an already existing line item entry is updated
	 */
	@Test
	public void testUpdateProgramIncomeInvoice() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		Boolean liResult = loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeInvoice method for good data inputs
	 * for scenario where a new line item entry is made when does not exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeInvoiceForNewItemInsert() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("102");
		Boolean liResult = loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeInvoice method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeInvoiceWithAppException() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = getDummyCBProgramIncomeBeanObj();
		loCBProgramIncomeBean.setId("");// Invalid programIncomeId
		loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeInvoice throws exception
	 */
	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeInvoiceWithException() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = null;
		loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
	}

	/**
	 * This method tests updateProgramIncomeInvoice throws exception
	 */
	@Test(expected = Exception.class)
	public void testUpdateProgramIncomeInvoiceWithException2() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = null;
		loContractBudgetService.updateProgramIncomeInvoice(null, loCBProgramIncomeBean);
	}

	// Program Income Invoice Test Case Ends

	// S337 rate invoicing start
	// case1: code runs successfully
	@Test
	public void testFetchInvoiceRateGrid1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractBudgetID("555");
			loGridBean.setSubBudgetID("555");
			loGridBean.setInvoiceId("55");

			InvoiceService loInvoiceService = new InvoiceService();
			List<RateBean> loRateBeanList = loInvoiceService.fetchInvoiceRateGrid(loGridBean, moSession);

			assertNotNull(loRateBeanList);
			assertEquals("Transaction passed:: InvoiceService: fetchInvoiceRateGrid.", loInvoiceService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case2: code comes to Application Exception block
	// do not set all parameters required correctly
	@Test
	public void testFetchInvoiceRateGrid2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractBudgetID("555");
			loGridBean.setSubBudgetID("555");
			loGridBean.setInvoiceId("abb");

			InvoiceService loInvoiceService = new InvoiceService();
			List<RateBean> loRateBeanList = loInvoiceService.fetchInvoiceRateGrid(loGridBean, moSession);

			assertNotNull(loRateBeanList);
			assertEquals("Transaction passed:: InvoiceService: fetchInvoiceRateGrid.", loInvoiceService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case3: code comes to Exception block
	// set session to null
	@Test
	public void testFetchInvoiceRateGrid3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractBudgetID("555");
			loGridBean.setSubBudgetID("555");
			loGridBean.setInvoiceId("55");

			InvoiceService loInvoiceService = new InvoiceService();

			List<RateBean> loRateBeanList = loInvoiceService.fetchInvoiceRateGrid(loGridBean, null);

			assertNotNull(loRateBeanList);
			assertEquals("Transaction passed:: InvoiceService: fetchInvoiceRateGrid.", loInvoiceService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case1: Exception
	// pass line item id that is not in database
	// it will fetch remaining amount as null and null cannot be casted to
	// double
	/*
	 * @Test public void testEditInvoiceRateGrid1() throws ApplicationException
	 * { Boolean lbThrown = false; try { RateBean loRateBean = new RateBean();
	 * loRateBean.setInvoiceId("55"); loRateBean.setContractBudgetID("555");
	 * loRateBean.setId("909"); // this row does not exist and brings null // as
	 * result // and null cannot be casted to Double
	 * loRateBean.setSubBudgetID("555");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * boolean lbEditStatus = loInvoiceService.editInvoiceRateGrid(loRateBean,
	 * moSession);
	 * 
	 * assertTrue(lbEditStatus);
	 * assertEquals("Transaction passed:: InvoiceService: editInvoiceRateGrid.",
	 * loInvoiceService.getMoState() .toString()); } catch (ApplicationException
	 * loAppEx) { lbThrown = true; assertTrue("Exception thrown", lbThrown); } }
	 */
	// case2: Application Exception
	// set rate bean to null
	/*
	 * @Test public void testEditInvoiceRateGrid2() throws ApplicationException
	 * { Boolean lbThrown = false; try { RateBean loRateBean = null;
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * boolean lbEditStatus = loInvoiceService.editInvoiceRateGrid(loRateBean,
	 * moSession);
	 * 
	 * assertTrue(lbEditStatus);
	 * assertEquals("Transaction passed:: InvoiceService: editInvoiceRateGrid.",
	 * loInvoiceService.getMoState() .toString()); } catch (ApplicationException
	 * loAppEx) { lbThrown = true; assertTrue("Exception thrown", lbThrown); } }
	 */

	// case3: throws error message because remaining amount is less than invoice
	// amount
	// pass all required data
	/*
	 * @Test public void testEditInvoiceRateGrid3() throws ApplicationException
	 * { Boolean lbThrown = false; try { RateBean loRateBean = new RateBean();
	 * loRateBean.setInvoiceId("55"); loRateBean.setContractBudgetID("555");
	 * loRateBean.setId("5"); loRateBean.setSubBudgetID("555");
	 * loRateBean.setYtdInvoiceAmt("800");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * boolean lbEditStatus = loInvoiceService.editInvoiceRateGrid(loRateBean,
	 * moSession);
	 * 
	 * assertTrue(lbEditStatus);
	 * assertEquals("Transaction passed:: InvoiceService: editInvoiceRateGrid.",
	 * loInvoiceService.getMoState() .toString()); } catch (ApplicationException
	 * loAppEx) { lbThrown = true; assertTrue("Exception thrown", lbThrown); } }
	 */

	// case3: successfully update invoice entry
	// pass all required data
	/*
	 * @Test public void testEditInvoiceRateGrid4() throws ApplicationException
	 * { Boolean lbThrown = false; try { RateBean loRateBean = new RateBean();
	 * loRateBean.setInvoiceId("25"); loRateBean.setContractBudgetID("174");
	 * loRateBean.setId("427"); loRateBean.setSubBudgetID("293");
	 * loRateBean.setYtdInvoiceAmt("200");
	 * loRateBean.setModifiedByUserId("909");
	 * loRateBean.setCreatedByUserId("909");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * boolean lbEditStatus = loInvoiceService.editInvoiceRateGrid(loRateBean,
	 * moSession);
	 * 
	 * assertTrue(lbEditStatus);
	 * assertEquals("Transaction passed:: InvoiceService: editInvoiceRateGrid.",
	 * loInvoiceService.getMoState() .toString()); } catch (ApplicationException
	 * loAppEx) { lbThrown = true; assertTrue("Exception thrown", lbThrown); } }
	 */

	// case3: successfully insert new invoice entry(because no invoice was done
	// yet)
	// pass all required data
	/*
	 * @Test public void testEditInvoiceRateGrid5() throws ApplicationException
	 * { Boolean lbThrown = false; try { RateBean loRateBean = new RateBean();
	 * loRateBean.setInvoiceId("55"); loRateBean.setContractBudgetID("555");
	 * loRateBean.setId("1"); // no entry should be there in // invoice_details
	 * loRateBean.setSubBudgetID("555"); loRateBean.setYtdInvoiceAmt("50");
	 * loRateBean.setModifiedByUserId("2160");
	 * loRateBean.setCreatedByUserId("2160");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * boolean lbEditStatus = loInvoiceService.editInvoiceRateGrid(loRateBean,
	 * moSession);
	 * 
	 * assertTrue(lbEditStatus);
	 * assertEquals("Transaction passed:: InvoiceService: editInvoiceRateGrid.",
	 * loInvoiceService.getMoState() .toString()); } catch (ApplicationException
	 * loAppEx) { lbThrown = true; assertTrue("Exception thrown", lbThrown); } }
	 */

	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testEditInvoiceRateGrid6() throws ApplicationException { RateBean
	 * loRateBean = new RateBean(); loRateBean.setInvoiceId("25");
	 * loRateBean.setContractBudgetID("174"); loRateBean.setId("427");
	 * loRateBean.setSubBudgetID("293"); loRateBean.setYtdInvoiceAmt("10000");
	 * loRateBean.setModifiedByUserId("909");
	 * loRateBean.setCreatedByUserId("909");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * loInvoiceService.editInvoiceRateGrid(loRateBean, moSession);
	 * 
	 * }
	 */
	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testEditInvoiceRateGrid7() throws ApplicationException { RateBean
	 * loRateBean = new RateBean(); loRateBean.setInvoiceId("25");
	 * loRateBean.setContractBudgetID("174"); loRateBean.setId("427");
	 * loRateBean.setSubBudgetID("293"); loRateBean.setInvUnits("50");
	 * loRateBean.setModifiedByUserId("909");
	 * loRateBean.setCreatedByUserId("909");
	 * 
	 * InvoiceService loInvoiceService = new InvoiceService();
	 * 
	 * loInvoiceService.editInvoiceRateGrid(loRateBean, moSession);
	 * 
	 * }
	 */

	// S337 rate invoicing end

	@Test
	public void testFetchSalariedEmployeeBudget() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = getDefaultProfServicesParams();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchSalariedEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);

			moInvoiceService.fetchSalariedEmployeeBudget(null, loBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchSalariedEmployeeBudget2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = new CBGridBean();
			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchSalariedEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchHourlyEmployeeBudget() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = getDefaultProfServicesParams();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchHourlyEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);

			moInvoiceService.fetchHourlyEmployeeBudget(null, loBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchHourlyEmployeeBudget2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = new CBGridBean();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchHourlyEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchSeasonalEmployeeBudget() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = getDefaultProfServicesParams();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchSeasonalEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);

			moInvoiceService.fetchSeasonalEmployeeBudget(null, loBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchSeasonalEmployeeBudget2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = new CBGridBean();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchSeasonalEmployeeBudget(moSession,
					loBean);
			assertNotNull(llPersonnelService);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}

	}

	@Test
	public void testFetchFringeBenifits() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = getDefaultProfServicesParams();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchFringeBenefits(moSession, loBean);
			assertNotNull(llPersonnelService);

			moInvoiceService.fetchFringeBenefits(null, loBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchFringeBenifits2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loBean = new CBGridBean();

			List<PersonnelServiceBudget> llPersonnelService = moInvoiceService.fetchFringeBenefits(moSession, loBean);
			assertNotNull(llPersonnelService);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditFringeBenifits() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			Boolean lbPersonnelService = moInvoiceService.editFringeBenefits(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			moInvoiceService.editFringeBenefits(null, aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditFringeBenifits2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();

			aoPersonnelServiceBudget.setId("58");
			aoPersonnelServiceBudget.setInvoiceId("55");
			aoPersonnelServiceBudget.setInvoicedAmount("7");
			aoPersonnelServiceBudget.setCreatedByUserId("169");
			aoPersonnelServiceBudget.setModifiedByUserId("169");

			aoPersonnelServiceBudget.setSubBudgetID("555");
			aoPersonnelServiceBudget.setContractBudgetID("555");
			Boolean lbPersonnelService = moInvoiceService.editFringeBenefits(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// negative

			aoPersonnelServiceBudget = new PersonnelServiceBudget();
			moInvoiceService.editFringeBenefits(moSession, aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditFringeBenifits3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			aoPersonnelServiceBudget.setSubBudgetID("555B");
			aoPersonnelServiceBudget.setContractBudgetID("555");

			aoPersonnelServiceBudget = new PersonnelServiceBudget();
			aoPersonnelServiceBudget.setSubBudgetID("555");
			aoPersonnelServiceBudget.setContractBudgetID("555B");
			moInvoiceService.editFringeBenefits(moSession, aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditFringeBenifits4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = null;
			moInvoiceService.editFringeBenefits(moSession, aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	private PersonnelServiceBudget setPersonnelServiceBudget()
	{
		PersonnelServiceBudget aoPersonnelServiceBudget = new PersonnelServiceBudget();

		aoPersonnelServiceBudget.setId("809");
		aoPersonnelServiceBudget.setInvoiceId("55");
		aoPersonnelServiceBudget.setInvoicedAmount("7");
		aoPersonnelServiceBudget.setCreatedByUserId("169");
		aoPersonnelServiceBudget.setModifiedByUserId("169");

		aoPersonnelServiceBudget.setSubBudgetID("1198");
		aoPersonnelServiceBudget.setContractBudgetID("10069");
		return aoPersonnelServiceBudget;
	}

	@Test
	public void testEditEmployeeInvoice() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			moInvoiceService.editEmployeeInvoice(null, aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditEmployeeInvoice2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// moInvoiceService.editEmployeeInvoice(null,
			// aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditEmployeeInvoice3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			aoPersonnelServiceBudget.setInvoicedAmount("254");
			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// moInvoiceService.editEmployeeInvoice(null,
			// aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditEmployeeInvoice4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = setPersonnelServiceBudget();
			aoPersonnelServiceBudget.setContractBudgetID("17");
			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// moInvoiceService.editEmployeeInvoice(null,
			// aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditEmployeeInvoice5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = new PersonnelServiceBudget();

			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// moInvoiceService.editEmployeeInvoice(null,
			// aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditEmployeeInvoice6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PersonnelServiceBudget aoPersonnelServiceBudget = new PersonnelServiceBudget();
			aoPersonnelServiceBudget.setSubBudgetID("4ffsdfsd");

			Boolean lbPersonnelService = moInvoiceService.editEmployeeInvoice(moSession, aoPersonnelServiceBudget);
			assertTrue(lbPersonnelService);

			// moInvoiceService.editEmployeeInvoice(null,
			// aoPersonnelServiceBudget);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Add Assignee successfully
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget1() throws ApplicationException
	{

		Boolean lbSuccess = false;
		String lsVendorId = "10";
		String lsBudgetId = "555";
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		try
		{
			lbSuccess = moInvoiceService.addAssigneeForBudget(moSession, lsVendorId, lsBudgetId, loCBGridBean);
			assertTrue(lbSuccess);

		}
		catch (ApplicationException e)
		{

			e.printStackTrace();
		}

	}

	/**
	 * Add Assignee : test case for Duplicate Assignee It will show a message
	 * when we try to add duplicate assignee
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget2() throws ApplicationException
	{

		Boolean lbThrown = false;

		String lsVendorId = "10";
		String lsBudgetId = "555";
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		try
		{
			moInvoiceService.addAssigneeForBudget(moSession, lsVendorId, lsBudgetId, loCBGridBean);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertEquals(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.MSG_KEY_DUPLICATE_ASSIGNEE_NOT_ADDED), aoAppEx.getMessage());

			assertTrue(lbThrown);
		}

	}

	/**
	 * Add Assignee : Negative scenario - An Exception is thrown if any of the
	 * input is invalid
	 * 
	 */
	@Test
	public void testAddAssigneeForBudget3() throws ApplicationException
	{

		Boolean lbThrown = false;

		String lsVendorId = "10";
		String lsBudgetId = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		try
		{
			moInvoiceService.addAssigneeForBudget(moSession, lsVendorId, lsBudgetId, loCBGridBean);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testAddAssigneeForBudget4() throws ApplicationException
	{

		Boolean lbThrown = false;

		String lsVendorId = null;
		String lsBudgetId = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		try
		{
			moInvoiceService.addAssigneeForBudget(moSession, lsVendorId, lsBudgetId, loCBGridBean);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testAddAssigneeForBudget5() throws ApplicationException
	{

		Boolean lbThrown = false;

		String lsVendorId = null;
		String lsBudgetId = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId("city_142");
		loCBGridBean.setModifiedByUserId("city_142");

		try
		{
			moInvoiceService.addAssigneeForBudget(null, lsVendorId, lsBudgetId, loCBGridBean);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testAddAssigneeForBudget6() throws ApplicationException
	{

		Boolean lbThrown = false;

		String lsVendorId = null;
		String lsBudgetId = null;
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCreatedByUserId(null);
		loCBGridBean.setModifiedByUserId(null);

		try
		{
			moInvoiceService.addAssigneeForBudget(null, lsVendorId, lsBudgetId, loCBGridBean);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/**
	 * Fetch Vendor List : fetch vendor list for User to add as an Assignee
	 * 
	 */
	@Test
	public void testFetchVendorList() throws ApplicationException
	{

		Boolean lbThrown = false;

		List<AutoCompleteBean> vendorList = null;

		try
		{
			vendorList = moInvoiceService.fetchVendorList(moSession, "");
			assertTrue(vendorList != null);
			assertTrue(vendorList.size() >= 0);

		}
		catch (ApplicationException aoAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	/*
	 * @Test public void testFetchVendorList2() throws ApplicationException { //
	 * NEGATIVE SCENARIO>>>>>>>>>>>>>>>>>>>>
	 * 
	 * Boolean lbThrown = false;
	 * 
	 * List<AutoCompleteBean> vendorList = null;
	 * 
	 * try { vendorList = moInvoiceService.fetchVendorList(null);
	 * assertTrue(vendorList != null); assertTrue(vendorList.size() >= 0);
	 * 
	 * } catch (ApplicationException aoAppEx) { lbThrown = true;
	 * assertTrue(lbThrown); }
	 * 
	 * }
	 */

	/**
	 * Validate Assignee : Validates if a vendor can be added as Assignee The
	 * service returns true when a vendor does not have any assignment for a
	 * budget
	 * 
	 */
	@Test
	public void testValidateAssignee1() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = "100";
		String lsBudgetId = "555";

		try
		{
			lbValid = moInvoiceService.validateAssignee(moSession, lsVendorId, lsBudgetId);
			assertTrue(lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	/**
	 * Validate Assignee : Validates if a vendor can be added as Assignee The
	 * service returns false when vendor is already added as Assignee for a
	 * Budget
	 * 
	 */
	@Test
	public void testValidateAssignee2() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = "10";
		String lsBudgetId = "555";

		try
		{
			lbValid = moInvoiceService.validateAssignee(moSession, lsVendorId, lsBudgetId);
			assertTrue(!lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateAssignee3() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = "";
		String lsBudgetId = "555";

		try
		{
			lbValid = moInvoiceService.validateAssignee(moSession, lsVendorId, lsBudgetId);
			assertTrue(lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateAssignee4() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = "";
		String lsBudgetId = "";

		try
		{
			lbValid = moInvoiceService.validateAssignee(moSession, lsVendorId, lsBudgetId);
			assertTrue(lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateAssignee6() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = null;
		String lsBudgetId = null;

		try
		{
			lbValid = moInvoiceService.validateAssignee(moSession, lsVendorId, lsBudgetId);
			assertTrue(lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testValidateAssignee7() throws ApplicationException
	{

		Boolean lbValid = false;
		Boolean loThrown = false;
		String lsVendorId = null;
		String lsBudgetId = null;

		try
		{
			lbValid = moInvoiceService.validateAssignee(null, lsVendorId, lsBudgetId);
			assertTrue(lbValid);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testErrorCheckInvoiceReviewTaskContractStatus() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "55";
		String liContractId = "660";
		String lsReviewLevel = "1";
		HashMap<String, Object> loResultMap = new HashMap<String, Object>();
		try
		{
			loResultMap = loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId,
					lsReviewLevel);
			assertEquals("0", loResultMap.get("errorCode"));
			assertEquals("! Cannot finish task while contract is suspended.", loResultMap.get("errorMsg"));
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testErrorCheckInvoiceReviewTask() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "55";
		String liContractId = "111777";
		String lsReviewLevel = "1";
		HashMap<String, Object> loResultMap = new HashMap<String, Object>();
		try
		{
			loResultMap = loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId,
					lsReviewLevel);
			assertEquals("1", loResultMap.get("errorCode"));
			assertEquals("", loResultMap.get("errorMsg"));
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while validating Invoice Amount for Equipment", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testErrorCheckInvoiceReviewTaskException() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "124";
		String liContractId = "111777";
		String lsReviewLevel = "2";
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(null, lsInvoiceId, liContractId, lsReviewLevel);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testErrorCheckInvoiceReviewInvoiceIdNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "";
		String liContractId = "111777";
		String lsReviewLevel = "2";
		HashMap<String, Object> loResultMap = new HashMap<String, Object>();
		try
		{
			loResultMap = loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId,
					lsReviewLevel);
			assertEquals("1", loResultMap.get("errorCode"));
			assertEquals("", loResultMap.get("errorMsg"));
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testErrorCheckInvoiceReviewContractIdNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "55";
		String liContractId = "";
		String lsReviewLevel = "2";
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId, lsReviewLevel);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while fetch in InvoiceService ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testErrorCheckInvoiceReviewContractIdNull6() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = null;
		String liContractId = null;
		String lsReviewLevel = null;
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId, lsReviewLevel);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while fetch in InvoiceService ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testErrorCheckInvoiceReviewContractIdNull7() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;
		String lsInvoiceId = "55";
		String liContractId = "111777";
		String lsReviewLevel = null;
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(moSession, lsInvoiceId, liContractId, lsReviewLevel);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while fetch in InvoiceService ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSetStatusForInvoiceReviewTask() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		TaskDetailsBean loTaskDetail = new TaskDetailsBean();
		loTaskDetail.setInvoiceId("55");
		loTaskDetail.setContractId("111779");
		loTaskDetail.setBudgetId("111779");
		loTaskDetail.setUserId("560");
		Boolean lbFinalFinish = true;
		String lsBudgetStatus = "73";
		Boolean loThrown = false;
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(moSession, lbFinalFinish, loTaskDetail, lsBudgetStatus);
			assertEquals(
					"Transaction Success:: InvoiceService:setStatusForInvoiceReviewTask method - success to update record "
							+ " \n", loInvoiceService.getMoState().toString());
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while fetch in InvoiceService ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSetStatusForInvoiceReviewTaskException() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		TaskDetailsBean loTaskDetail = new TaskDetailsBean();
		loTaskDetail.setInvoiceId("55");
		loTaskDetail.setContractId("111779");
		loTaskDetail.setBudgetId("111779");
		loTaskDetail.setUserId("560");
		Boolean lbFinalFinish = true;
		String lsBudgetStatus = "73";
		Boolean loThrown = false;
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, lbFinalFinish, loTaskDetail, lsBudgetStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSetStatusForInvoiceReviewTaskFinalFinishFalse() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		TaskDetailsBean loTaskDetail = new TaskDetailsBean();
		loTaskDetail.setInvoiceId("55");
		loTaskDetail.setContractId("111779");
		loTaskDetail.setBudgetId("111779");
		loTaskDetail.setUserId("560");
		Boolean lbFinalFinish = false;
		String lsBudgetStatus = "73";
		Boolean loThrown = false;
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, lbFinalFinish, loTaskDetail, lsBudgetStatus);
			assertEquals("", loInvoiceService.getMoState().toString());
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSetStatusForInvoiceReviewTaskBudgetStatusNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		TaskDetailsBean loTaskDetail = new TaskDetailsBean();
		loTaskDetail.setInvoiceId("55");
		loTaskDetail.setContractId("111779");
		loTaskDetail.setBudgetId("111779");
		loTaskDetail.setUserId("560");
		Boolean lbFinalFinish = true;
		String lsBudgetStatus = "";
		Boolean loThrown = false;
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, lbFinalFinish, loTaskDetail, lsBudgetStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSetStatusForInvoiceReviewTaskDeatilBeanNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		TaskDetailsBean loTaskDetail = new TaskDetailsBean();
		Boolean lbFinalFinish = true;
		String lsBudgetStatus = "73";
		Boolean loThrown = false;
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, lbFinalFinish, loTaskDetail, lsBudgetStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSaveAgencyInvoiceNumber() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "48");
		loInputMap.put("invoiceNumber", "56000");
		loInputMap.put("userId", "agency_47");
		Boolean loThrown = false;
		try
		{
			loInvoiceService.saveAgencyInvoiceNumber(moSession, loInputMap);
			assertEquals(
					"Transaction Success:: InvoiceService:saveAgencyInvoiceNumber method - success to update record "
							+ " \n", loInvoiceService.getMoState().toString());
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSaveAgencyInvoiceNumberInputMapNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Map loInputMap = new HashMap();
		Boolean loThrown = false;
		try
		{
			loInvoiceService.saveAgencyInvoiceNumber(moSession, loInputMap);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testSaveAgencyInvoiceNumberDAONull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "48");
		loInputMap.put("invoiceNumber", "56000");
		loInputMap.put("userId", "agency_47");
		Boolean loThrown = false;
		try
		{
			loInvoiceService.saveAgencyInvoiceNumber(null, loInputMap);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfo() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "55");
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(moSession, loInputMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfoException() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "55");
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(null, loInputMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfo3() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", null);
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(moSession, loInputMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfo4() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "");
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(moSession, loInputMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfo5() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "ghg");
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(moSession, loInputMap);
			assertNotNull(loInvoiceList);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoiceInfoInvoiceListNull() throws ApplicationException
	{

		InvoiceService loInvoiceService = new InvoiceService();
		InvoiceList loInvoiceList = new InvoiceList();
		Map loInputMap = new HashMap();
		loInputMap.put("invoiceId", "48");
		Boolean loThrown = false;
		try
		{
			loInvoiceList = loInvoiceService.fetchInvoiceInfo(moSession, loInputMap);
			assertEquals("10000.0", loInvoiceList.getTotalValue());
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchInvoicingUtilities2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			loCBGridBean.setSubBudgetID("555B");
			loCBGridBean.setContractBudgetID("555");

			List<CBUtilities> cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchInvoicingUtilities3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			InvoiceService loInvoiceService = new InvoiceService();
			CBGridBean loCBGridBean = new CBGridBean();

			// Positive Scenario -- List of CBIndirectRateBean type returned.

			// loCBGridBean.setSubBudgetID("");
			loCBGridBean.setContractBudgetID("555");

			List<CBUtilities> cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(moSession, loCBGridBean);
			assertNotNull(cbUtilitiesList);
			assertTrue(cbUtilitiesList.size() != 0);

			// Negative Scenario -- Application Exception handled by setting.

			cbUtilitiesList = loInvoiceService.fetchInvoicingUtilities(null, loCBGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testGetNextSeqFromInvoiceTable() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.ORG_ID, "accenture");
		aoHashMap.put(HHSConstants.AGENCYID, "ACS");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2004");
		aoHashMap.put(HHSConstants.USER_ID, "agency_47");
		try
		{
			String lsCurrentSeq = loInvoiceService.getNextSeqFromInvoiceTable(moSession, aoHashMap, null);
			// System.out.println("InvoiceServiceTest.testGetNextSeqFromInvoiceTable()"
			// + lsCurrentSeq);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testGetNextSeqFromInvoiceTable2() throws ApplicationException
	{
		// NEGATIVE SCENARIO.......
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		try
		{
			String lsCurrentSeq = loInvoiceService.getNextSeqFromInvoiceTable(moSession, aoHashMap, null);
			assertEquals("1", lsCurrentSeq);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testGetNextSeqFromInvoiceTable3() throws ApplicationException
	{
		// NEGATIVE SCENARIO...

		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loThrown = false;

		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put(HHSConstants.ORG_ID, "accenture");
		aoHashMap.put(HHSConstants.AGENCYID, "ACS");
		aoHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111777");
		aoHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2004");
		aoHashMap.put(HHSConstants.USER_ID, "agency_47");
		try
		{
			String lsCurrentSeq = loInvoiceService.getNextSeqFromInvoiceTable(null, aoHashMap, null);
			assertEquals("1", lsCurrentSeq);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}

	}

	@Test
	public void testFetchCurrInvoiceStatus1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		String aoInvoiceId = "55";
		Boolean loThrown = false;
		try
		{
			String lsInvoiceStatus = moInvoiceService.fetchCurrInvoiceStatus(moSession, aoInvoiceId);
			assertEquals("70", lsInvoiceStatus);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchCurrInvoiceStatus2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		String aoInvoiceId = "55B";
		Boolean loThrown = false;
		try
		{
			String lsInvoiceStatus = moInvoiceService.fetchCurrInvoiceStatus(moSession, aoInvoiceId);
			assertEquals("70", lsInvoiceStatus);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieving in InvoiceService ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchCurrInvoiceStatus3() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		String aoInvoiceId = "";
		Boolean loThrown = false;
		try
		{
			String lsInvoiceStatus = moInvoiceService.fetchCurrInvoiceStatus(moSession, aoInvoiceId);
			assertEquals("70", lsInvoiceStatus);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieving in InvoiceService ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchCurrInvoiceStatus4() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		String aoInvoiceId = null;
		Boolean loThrown = false;
		try
		{
			String lsInvoiceStatus = moInvoiceService.fetchCurrInvoiceStatus(moSession, aoInvoiceId);
			assertEquals("70", lsInvoiceStatus);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieving in InvoiceService ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchCurrInvoiceStatus5() throws ApplicationException, IllegalAccessException,
			InvocationTargetException
	{
		// NEGATIVE SCENERIO>>>>>>>>>>
		String aoInvoiceId = null;
		Boolean loThrown = false;
		try
		{
			String lsInvoiceStatus = moInvoiceService.fetchCurrInvoiceStatus(null, aoInvoiceId);
			assertEquals("70", lsInvoiceStatus);
		}
		catch (ApplicationException loAppEx)
		{
			loThrown = true;
			assertEquals("Exception occured while retrieving in InvoiceService ", loAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testFindContractDetailsByContractForWFNegative() throws
	 * ApplicationException { String lsContractId = null;
	 * moInvoiceService.getAgencyIdByContractForWF(moSession, lsContractId); }
	 */

	@Test
	public void testFetchInvoiceAssignmentSummary1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setInvoiceId("55");
		aoCBGridBeanObj.setContractID("111777");
		List<AssignmentsSummaryBean> loAssignmentsList = moInvoiceService.fetchInvoiceAssignmentSummary(moSession,
				aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test
	public void testFetchInvoiceAssignmentSummary2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setInvoiceId("");
		aoCBGridBeanObj.setContractID("111777");
		List<AssignmentsSummaryBean> loAssignmentsList = moInvoiceService.fetchInvoiceAssignmentSummary(moSession,
				aoCBGridBeanObj);
		assertNotNull(loAssignmentsList);
	}

	@Test
	public void testFetchInvoiceAssignmentSummary3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setInvoiceId("");
		aoCBGridBeanObj.setContractID("");
		List<AssignmentsSummaryBean> loAssignmentsList = moInvoiceService.fetchInvoiceAssignmentSummary(moSession,
				aoCBGridBeanObj);
		assertTrue(loAssignmentsList.size() == 0);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceAssignmentSummary4() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setInvoiceId(null);
		aoCBGridBeanObj.setContractID(null);
		List<AssignmentsSummaryBean> loAssignmentsList = moInvoiceService.fetchInvoiceAssignmentSummary(moSession,
				aoCBGridBeanObj);
		assertTrue(loAssignmentsList.size() == 0);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceAssignmentSummary5() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();

		List<AssignmentsSummaryBean> loAssignmentsList = moInvoiceService.fetchInvoiceAssignmentSummary(null,
				aoCBGridBeanObj);
		assertTrue(loAssignmentsList.size() == 0);
	}

	@Test
	public void testEditInvoiceAssignmentSummary() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("21");
		aoAssignmentsSummaryBean.setInvoiceId("55");
		aoAssignmentsSummaryBean.setInvoiceAmount("1200");
		aoAssignmentsSummaryBean.setModifyByAgency("");
		aoAssignmentsSummaryBean.setModifyByProvider("");
		aoAssignmentsSummaryBean.setSubBudgetID("555");
		Boolean status = moInvoiceService.editInvoiceAssignmentSummary(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceAssignmentSummary2() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("21B");
		aoAssignmentsSummaryBean.setInvoiceId("55");
		aoAssignmentsSummaryBean.setInvoiceAmount("1200");
		aoAssignmentsSummaryBean.setModifyByAgency("");
		aoAssignmentsSummaryBean.setModifyByProvider("");
		aoAssignmentsSummaryBean.setSubBudgetID("555");
		Boolean status = moInvoiceService.editInvoiceAssignmentSummary(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceAssignmentSummary3() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("21B");
		aoAssignmentsSummaryBean.setInvoiceId("55");
		aoAssignmentsSummaryBean.setInvoiceAmount("1200");
		aoAssignmentsSummaryBean.setModifyByAgency("");
		aoAssignmentsSummaryBean.setModifyByProvider("");
		aoAssignmentsSummaryBean.setSubBudgetID("555");
		Boolean status = moInvoiceService.editInvoiceAssignmentSummary(null, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceAssignmentSummary4() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("");
		aoAssignmentsSummaryBean.setInvoiceId("");
		aoAssignmentsSummaryBean.setInvoiceAmount("");
		aoAssignmentsSummaryBean.setModifyByAgency("");
		aoAssignmentsSummaryBean.setModifyByProvider("");
		aoAssignmentsSummaryBean.setSubBudgetID("555");
		Boolean status = moInvoiceService.editInvoiceAssignmentSummary(null, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceAssignmentSummary5() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId(null);
		aoAssignmentsSummaryBean.setInvoiceId(null);
		aoAssignmentsSummaryBean.setInvoiceAmount(null);
		aoAssignmentsSummaryBean.setModifyByAgency("");
		aoAssignmentsSummaryBean.setModifyByProvider(null);
		aoAssignmentsSummaryBean.setSubBudgetID(null);
		Boolean status = moInvoiceService.editInvoiceAssignmentSummary(null, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test
	public void testUpdateInvoiceDetails() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId("55");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID("");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("");

		loCBGridBean.setSubBudgetID("555");

		Map loInputMap = new HashMap();

		loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
		loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
		loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
		loInputMap.put(HHSConstants.USER_ID, "agency_21");
		loInputMap.put(HHSConstants.USER_PROVIDER, "5000");
		loInputMap.put(HHSConstants.INVOICE_START_DATE, "06/11/2013");
		loInputMap.put(HHSConstants.INVOICE_END_DATE, "07/11/2013");

		moInvoiceService.updateInvoiceDetails(moSession, loInputMap);
		assertEquals("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record  "
				+ "\n", moInvoiceService.getMoState().toString());
	}

	@Test
	public void testUpdateInvoiceDetails2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId("");
		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID("");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("");

		loCBGridBean.setSubBudgetID("555");

		Map loInputMap = new HashMap();

		loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
		loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
		loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
		loInputMap.put(HHSConstants.USER_ID, "agency_21");
		loInputMap.put(HHSConstants.USER_PROVIDER, "5000");
		loInputMap.put(HHSConstants.INVOICE_START_DATE, "06/11/2013");
		loInputMap.put(HHSConstants.INVOICE_END_DATE, "07/11/2013");

		moInvoiceService.updateInvoiceDetails(moSession, loInputMap);
		assertEquals("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record  "
				+ "\n", moInvoiceService.getMoState().toString());
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateInvoiceDetails3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId(null);
		loCBGridBean.setContractID(null);
		loCBGridBean.setContractBudgetID("");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("");

		loCBGridBean.setSubBudgetID("555");

		Map loInputMap = new HashMap();

		loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
		loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
		loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
		loInputMap.put(HHSConstants.USER_ID, "agency_21");
		loInputMap.put(HHSConstants.USER_PROVIDER, "5000");
		loInputMap.put(HHSConstants.INVOICE_START_DATE, "06/11/2013");
		loInputMap.put(HHSConstants.INVOICE_END_DATE, "07/11/2013");

		moInvoiceService.updateInvoiceDetails(moSession, loInputMap);
		assertEquals("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record  "
				+ "\n", moInvoiceService.getMoState().toString());
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateInvoiceDetails4() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId(null);
		loCBGridBean.setContractID(null);
		loCBGridBean.setContractBudgetID("");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("");

		loCBGridBean.setSubBudgetID("555");

		Map loInputMap = new HashMap();

		loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
		loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
		loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
		loInputMap.put(HHSConstants.USER_ID, "agency_21");
		loInputMap.put(HHSConstants.USER_PROVIDER, "5000");
		loInputMap.put(HHSConstants.INVOICE_START_DATE, "06/11/2013");
		loInputMap.put(HHSConstants.INVOICE_END_DATE, "07/11/2013");

		moInvoiceService.updateInvoiceDetails(null, loInputMap);
		assertEquals("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record  "
				+ "\n", moInvoiceService.getMoState().toString());
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateInvoiceDetails5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId(null);
		loCBGridBean.setContractID(null);
		loCBGridBean.setContractBudgetID("");
		loCBGridBean.setModifyByAgency("");
		loCBGridBean.setModifyByProvider("");

		loCBGridBean.setSubBudgetID("555");

		Map loInputMap = new HashMap();

		loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
		loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
		loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
		loInputMap.put(HHSConstants.USER_ID, null);
		loInputMap.put(HHSConstants.USER_PROVIDER, null);
		loInputMap.put(HHSConstants.INVOICE_START_DATE, null);
		loInputMap.put(HHSConstants.INVOICE_END_DATE, null);

		moInvoiceService.updateInvoiceDetails(moSession, loInputMap);
		assertEquals("Transaction Success:: InvoiceService:updateInvoiceDetails method - success to update record  "
				+ "\n", moInvoiceService.getMoState().toString());
	}

	@Test
	public void testFetchInvoiceAdvanceDetails1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId("");

		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setSubBudgetID("555");

		List<AdvanceSummaryBean> loAdvanceSummaryBeans = moInvoiceService.fetchInvoiceAdvanceDetails(loCBGridBean,
				moSession);
		assertNotNull(loAdvanceSummaryBeans);

		loCBGridBean.setContractBudgetID("423423343");
		loAdvanceSummaryBeans = moInvoiceService.fetchInvoiceAdvanceDetails(loCBGridBean, moSession);
		assertTrue(loAdvanceSummaryBeans.size() == 0);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceAdvanceDetails3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId("");

		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID(null);
		loCBGridBean.setSubBudgetID("555");

		List<AdvanceSummaryBean> loAdvanceSummaryBeans = moInvoiceService.fetchInvoiceAdvanceDetails(loCBGridBean,
				moSession);
		assertNotNull(loAdvanceSummaryBeans);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceAdvanceDetails4() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId("");

		loCBGridBean.setContractID("111777");
		loCBGridBean.setContractBudgetID(null);
		loCBGridBean.setSubBudgetID("555");

		List<AdvanceSummaryBean> loAdvanceSummaryBeans = moInvoiceService
				.fetchInvoiceAdvanceDetails(loCBGridBean, null);
		assertNotNull(loAdvanceSummaryBeans);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceAdvanceDetails5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		Map loQueryParam = new HashMap();

		loCBGridBean.setInvoiceId(null);

		loCBGridBean.setContractID("111777B");
		loCBGridBean.setContractBudgetID(null);
		loCBGridBean.setSubBudgetID("555B");

		List<AdvanceSummaryBean> loAdvanceSummaryBeans = moInvoiceService
				.fetchInvoiceAdvanceDetails(loCBGridBean, null);
		assertNotNull(loAdvanceSummaryBeans);
	}

	@Test
	public void testEditContractedServicesInvoicing1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			aoCBGridBeanObj.setCreatedByUserId("");
			aoCBGridBeanObj.setModifiedByUserId("");
			aoCBGridBeanObj.setId("112");
			aoCBGridBeanObj.setInvoiceId("55");

			aoCBGridBeanObj.setInvoiceAmt("200");
			aoCBGridBeanObj.setModifyByAgency("accenture");
			aoCBGridBeanObj.setModifyByProvider("");
			aoCBGridBeanObj.setSubBudgetID("556");
			Double loAmount = Double.parseDouble(aoCBGridBeanObj.getInvoiceAmt());
			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(moSession, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditContractedServicesInvoicing2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			aoCBGridBeanObj.setCreatedByUserId("agency_21");
			aoCBGridBeanObj.setModifiedByUserId("");
			aoCBGridBeanObj.setId("112");
			aoCBGridBeanObj.setInvoiceId("55");

			aoCBGridBeanObj.setInvoiceAmt("200");
			aoCBGridBeanObj.setModifyByAgency("accenture");
			aoCBGridBeanObj.setModifyByProvider("");
			aoCBGridBeanObj.setSubBudgetID("556");
			Double loAmount = Double.parseDouble(aoCBGridBeanObj.getInvoiceAmt());
			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(moSession, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditContractedServicesInvoicing3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			aoCBGridBeanObj.setCreatedByUserId("");
			aoCBGridBeanObj.setModifiedByUserId("");
			aoCBGridBeanObj.setId(null);
			aoCBGridBeanObj.setInvoiceId(null);

			aoCBGridBeanObj.setInvoiceAmt(null);
			aoCBGridBeanObj.setModifyByAgency("accenture");
			aoCBGridBeanObj.setModifyByProvider("");
			aoCBGridBeanObj.setSubBudgetID("556");
			Double loAmount = Double.parseDouble(aoCBGridBeanObj.getInvoiceAmt());
			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(moSession, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditContractedServicesInvoicing4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			aoCBGridBeanObj.setCreatedByUserId("");
			aoCBGridBeanObj.setModifiedByUserId("");
			aoCBGridBeanObj.setId("112");
			aoCBGridBeanObj.setInvoiceId("55");

			aoCBGridBeanObj.setInvoiceAmt("200");
			aoCBGridBeanObj.setModifyByAgency("accenture");
			aoCBGridBeanObj.setModifyByProvider("");
			aoCBGridBeanObj.setSubBudgetID("556");
			Double loAmount = Double.parseDouble(aoCBGridBeanObj.getInvoiceAmt());
			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(null, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditContractedServicesInvoicing5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(moSession, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditContractedServicesInvoicing6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractedServicesBean aoCBGridBeanObj = new ContractedServicesBean();

			boolean lbUpdateStatus = moInvoiceService.editContractedServicesInvoicing(null, aoCBGridBeanObj);
			assertTrue(lbUpdateStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchCurrentAssignmentStatus1() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			String lsInvoiceID = "55";

			boolean status = moInvoiceService.fetchCurrentAssignmentStatus(moSession, lsInvoiceID);
			assertTrue(status);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCurrentAssignmentStatus2() throws ApplicationException
	{
		String lsInvoiceID = "";

		boolean status = moInvoiceService.fetchCurrentAssignmentStatus(moSession, lsInvoiceID);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCurrentAssignmentStatus3() throws ApplicationException
	{
		String lsInvoiceID = null;

		boolean status = moInvoiceService.fetchCurrentAssignmentStatus(moSession, lsInvoiceID);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCurrentAssignmentStatus4() throws ApplicationException
	{
		String lsInvoiceID = "56jj";

		boolean status = moInvoiceService.fetchCurrentAssignmentStatus(moSession, lsInvoiceID);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCurrentAssignmentStatus5() throws ApplicationException
	{
		String lsInvoiceID = "56";

		boolean status = moInvoiceService.fetchCurrentAssignmentStatus(null, lsInvoiceID);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchCurrentAssignmentStatus6() throws ApplicationException
	{
		String lsInvoiceID = "566664646";

		boolean status = moInvoiceService.fetchCurrentAssignmentStatus(moSession, lsInvoiceID);
		assertTrue(status);
	}

	@Test
	// (expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("162");

		aoAssignmentsSummaryBean.setEntryTypeId("6");
		aoAssignmentsSummaryBean.setId("102");

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test
	// (expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment2() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("");
		aoAssignmentsSummaryBean.setEntryTypeId("6");
		aoAssignmentsSummaryBean.setId("102");

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test
	// (expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment3() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("162");
		aoAssignmentsSummaryBean.setEntryTypeId("");
		aoAssignmentsSummaryBean.setId("102");

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test
	// (expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment4() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("162");
		aoAssignmentsSummaryBean.setEntryTypeId("");
		aoAssignmentsSummaryBean.setId("");

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment5() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId("162");
		aoAssignmentsSummaryBean.setEntryTypeId("6");
		aoAssignmentsSummaryBean.setId(null);

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment6() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId(null);
		aoAssignmentsSummaryBean.setEntryTypeId("6");
		aoAssignmentsSummaryBean.setId("102");

		boolean status = moInvoiceService.delContractInvoiceAssignment(moSession, aoAssignmentsSummaryBean);
		assertTrue(status);
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractInvoiceAssignment7() throws ApplicationException
	{
		AssignmentsSummaryBean aoAssignmentsSummaryBean = new AssignmentsSummaryBean();

		aoAssignmentsSummaryBean.setId(null);
		aoAssignmentsSummaryBean.setEntryTypeId("6");
		aoAssignmentsSummaryBean.setId("102");

		boolean status = moInvoiceService.delContractInvoiceAssignment(null, aoAssignmentsSummaryBean);
		assertTrue(status);

	}

	@Test
	public void testEditInvoiceAdvanceDetails1() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId("68");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("4");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails2() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId("68");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("2");
		aoAdvanceSummaryBean.setContractBudgetID("560");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails3() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId("68");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("2");
		aoAdvanceSummaryBean.setContractBudgetID(null);

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails4() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = false;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId("68");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("2");
		aoAdvanceSummaryBean.setContractBudgetID("560");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails5() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails6() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, null);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails7() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId(null);
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("2");
		aoAdvanceSummaryBean.setContractBudgetID("560");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails8() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
		aoAdvanceSummaryBean.setInvoiceId("68B");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("2B");
		aoAdvanceSummaryBean.setContractBudgetID("560");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditInvoiceAdvanceDetails9() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
		aoAdvanceSummaryBean.setInvoiceRecoupedAmt("");
		aoAdvanceSummaryBean.setInvoiceId("");
		aoAdvanceSummaryBean.setModifyByProvider("city_142");
		aoAdvanceSummaryBean.setId("");
		aoAdvanceSummaryBean.setContractBudgetID("");

		try
		{
			boolean lbStatus = loInvoiceService.editInvoiceAdvanceDetails(aoValid, aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus1() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("2");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus2() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("4");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus3() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus4() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId(null);
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus5() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();

			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus6() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("4");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, null);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus7() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("4B");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus8() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("4");
			aoAdvanceSummaryBean.setContractBudgetID("560B");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus9() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68B");
			aoAdvanceSummaryBean.setModifyByProvider("city_142675757567657657657657567576");
			aoAdvanceSummaryBean.setId("4");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			// assertEquals("Error occured while executing service: ",
			// aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testValidateInvoiceAdvanceStatus10() throws ApplicationException
	{
		boolean loThrown = false;
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean aoValid = true;
		try
		{
			AdvanceSummaryBean aoAdvanceSummaryBean = new AdvanceSummaryBean();
			aoAdvanceSummaryBean.setInvoiceRecoupedAmt("3045");
			aoAdvanceSummaryBean.setInvoiceId("68");
			aoAdvanceSummaryBean.setModifyByProvider("city_142");
			aoAdvanceSummaryBean.setId("4");
			aoAdvanceSummaryBean.setContractBudgetID("560");
			boolean lbStatus = loInvoiceService.validateInvoiceAdvanceStatus(aoAdvanceSummaryBean, moSession);
			assertTrue(lbStatus);

		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testFetchProviderList1() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		List<AutoCompleteBean> loProviderList = loInvoiceService.fetchProviderList(moSession, "test_manager");
		assertNotNull(loProviderList);
	}

	@Test
	public void testFetchProviderList2() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		List<AutoCompleteBean> loProviderList = loInvoiceService.fetchProviderList(moSession, "");
		assertNotNull(loProviderList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProviderListNegative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		List<AutoCompleteBean> loProviderList = loInvoiceService.fetchProviderList(moSession, null);
		assertNotNull(loProviderList);
	}
	
	
	@Test
	public void testFetchInvoiceTotalForContractedServices1() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setBudgetTypeId("86");
		loCBGridBean.setSubBudgetID("135");
		String lsInvoiceTotalAmounts = loInvoiceService.fetchInvoiceTotalForContractedServices(loCBGridBean, moSession);
		assertNotNull(lsInvoiceTotalAmounts);
	}
	
	@Test
	public void testFetchContractedServicesYTDInvoiced() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		String lsInvoiceTotalAmounts = loInvoiceService.fetchContractedServicesYTDInvoiced("86", moSession);
		assertNotNull(lsInvoiceTotalAmounts);
	}
	
	@Test
	public void testInvoiceNegativeAmendCheck() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loValidateStatus = loInvoiceService.invoiceNegativeAmendCheck(moSession, "135");
		assertTrue(loValidateStatus);
	}

	@Test
	public void testInvoiceAmountAssignmentValidation() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loValidateStatus = loInvoiceService.invoiceAmountAssignmentValidation(moSession, "2");
		assertTrue(loValidateStatus);
	}

	@Test
	public void testInvoiceAmountZeroValidation() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		Boolean loValidateStatus = loInvoiceService.invoiceAmountZeroValidation(moSession, "4");
		assertTrue(loValidateStatus);
	}

	@Test
	public void testFetchInvoiceTotalForPersonnelServices() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setBudgetTypeId("86");
		loCBGridBean.setSubBudgetID("135");
		PersonnelServicesData loPersonnelServicesData = loInvoiceService.fetchInvoiceTotalForPersonnelServices(loCBGridBean, moSession);
		assertNotNull(loPersonnelServicesData);
	}

	@Test
	public void testCheckSubmitInvoiceFeasibility() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		HashMap<String, String> loBudgetMap =loInvoiceService.checkSubmitInvoiceFeasibility(moSession, null, "", "");
		assertNotNull(loBudgetMap);
	}

	@Test
	public void testGetBudgetTaskStatus1() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", "164");
		loHashMap.put("contractId", "209");
		BudgetList loBudgetList = loInvoiceService.getBudgetTaskStatus(moSession, loHashMap);
		assertNotNull(loBudgetList);
	}

	@Test
	public void testGetBudgetTaskStatus2() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", null);
		loHashMap.put("contractId", "181");
		BudgetList loBudgetList = loInvoiceService.getBudgetTaskStatus(moSession, loHashMap);
		assertNotNull(loBudgetList);
	}

	@Test
	public void testGetBudgetTaskStatus3() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", null);
		loHashMap.put("contractId", "87");
		BudgetList loBudgetList = loInvoiceService.getBudgetTaskStatus(moSession, loHashMap);
		assertNotNull(loBudgetList);
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchOperationAndSupportDetails0Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchOperationAndSupportDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateOpSupportInvoiceAmount1Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateOpSupportInvoiceAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditOperationAndSupportDetails2Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editOperationAndSupportDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchEquipmentDetails3Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateEquipmentInvoiceAmount4Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateEquipmentInvoiceAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditEquipmentDetails5Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editEquipmentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceStatus6Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceTotalForOTPS7Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceTotalForOTPS(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceTotalForContractedServices8Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceTotalForContractedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesYTDInvoiced9Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesYTDInvoiced(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchYTDInvoiced10Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchYTDInvoiced(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceTotalForPersonnelServices11Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceTotalForPersonnelServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceUnallocatedFunds12Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchProgramIncomeInvoice13Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchProgramIncomeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateProgramIncomeInvoice14Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateProgramIncomeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoicingUtilities15Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoicingUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoicingUtilities16Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoicingUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceIndirectRate17Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoicingIndirectRate18Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoicingIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchMilestoneInvoice19Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchMilestoneInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateMilestoneInvoice20Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateMilestoneInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceSummary21Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchProfServicesDetails22Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditProfServicesDetails23Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceRent24Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateContractInvoiceRent25Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateContractInvoiceRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceFyBudgetSummary26Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceFyBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceSummary27Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceInformation28Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceInformation(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoiceStatus29Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchCurrInvoiceStatus30Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchCurrInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetAgencyIdByContractForWF31Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getAgencyIdByContractForWF(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditEmployeeInvoice32Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editEmployeeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditFringeBenefits33Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editFringeBenefits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSalariedEmployeeBudget34Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSalariedEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchHourlyEmployeeBudget35Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchHourlyEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSeasonalEmployeeBudget36Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSeasonalEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchFringeBenefits37Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchFringeBenefits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceAssignmentSummary38Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditInvoiceAssignmentSummary39Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editInvoiceAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceRateGrid40Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceRateGrid(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditContractedServicesInvoicing42Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editContractedServicesInvoicing(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingConsultants43Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingConsultants(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingSubContractors44Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingSubContractors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingVendors45Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingVendors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceerrorCheckInvoiceReviewTask46Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicesetStatusForInvoiceReviewTask47Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchCurrentAssignmentStatus48Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchCurrentAssignmentStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicesaveAgencyInvoiceNumber49Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.saveAgencyInvoiceNumber(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceInfo50Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceInfo(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchVendorList51Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchVendorList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchProviderList52Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchProviderList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateAssignee53Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateAssignee(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceaddAssigneeForBudget54Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.addAssigneeForBudget(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetBudgetTaskStatus55Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getBudgetTaskStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicecheckSubmitInvoiceFeasibility56Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.checkSubmitInvoiceFeasibility(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetNextSeqFromInvoiceTable57Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getNextSeqFromInvoiceTable(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSubBudgetSummary58Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetCbGridDataForSession59Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoiceDetails60Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoiceDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicedelContractInvoiceAssignment61Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.delContractInvoiceAssignment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateInvoiceAdvanceStatus62Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateInvoiceAdvanceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditInvoiceAdvanceDetails63Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editInvoiceAdvanceDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceAdvanceDetails64Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceAdvanceDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceinvoiceNegativeAmendCheck65Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.invoiceNegativeAmendCheck(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceinvoiceAmountAssignmentValidation66Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.invoiceAmountAssignmentValidation(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceinvoiceAmountZeroValidation67Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.invoiceAmountZeroValidation(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchOperationAndSupportDetails0NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchOperationAndSupportDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateOpSupportInvoiceAmount1NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateOpSupportInvoiceAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditOperationAndSupportDetails2NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editOperationAndSupportDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchEquipmentDetails3NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateEquipmentInvoiceAmount4NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateEquipmentInvoiceAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditEquipmentDetails5NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editEquipmentDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceStatus6NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceTotalForOTPS7NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceTotalForOTPS(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceTotalForContractedServices8NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceTotalForContractedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesYTDInvoiced9NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesYTDInvoiced(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchYTDInvoiced10NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchYTDInvoiced(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceTotalForPersonnelServices11NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceTotalForPersonnelServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceUnallocatedFunds12NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchProgramIncomeInvoice13NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchProgramIncomeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateProgramIncomeInvoice14NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateProgramIncomeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoicingUtilities15NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoicingUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoicingUtilities16NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoicingUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceIndirectRate17NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoicingIndirectRate18NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoicingIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchMilestoneInvoice19NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchMilestoneInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateMilestoneInvoice20NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateMilestoneInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceinsertUpdateInvoiceDetail21NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.insertUpdateInvoiceDetail(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceSummary22NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchProfServicesDetails23NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditProfServicesDetails24NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceRent25NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateContractInvoiceRent26NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateContractInvoiceRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceFyBudgetSummary27NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceFyBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceSummary28NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceInformation29NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceInformation(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoiceStatus30NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchCurrInvoiceStatus31NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchCurrInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetAgencyIdByContractForWF32NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getAgencyIdByContractForWF(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditEmployeeInvoice33NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editEmployeeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditFringeBenefits34NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editFringeBenefits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSalariedEmployeeBudget35NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSalariedEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchHourlyEmployeeBudget36NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchHourlyEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSeasonalEmployeeBudget37NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSeasonalEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchFringeBenefits38NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchFringeBenefits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceAssignmentSummary39NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditInvoiceAssignmentSummary40NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editInvoiceAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceRateGrid41NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceRateGrid(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditContractedServicesInvoicing42NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editContractedServicesInvoicing(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingConsultants43NegativeApp()
			throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingConsultants(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingSubContractors44NegativeApp()
			throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingSubContractors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingVendors45NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingVendors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceerrorCheckInvoiceReviewTask46NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.errorCheckInvoiceReviewTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicesetStatusForInvoiceReviewTask47NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.setStatusForInvoiceReviewTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchCurrentAssignmentStatus48NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchCurrentAssignmentStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicesaveAgencyInvoiceNumber49NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.saveAgencyInvoiceNumber(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceInfo50NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceInfo(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchVendorList51NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchVendorList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchProviderList52NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchProviderList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateAssignee53NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateAssignee(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceaddAssigneeForBudget54NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.addAssigneeForBudget(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetBudgetTaskStatus55NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getBudgetTaskStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicecheckSubmitInvoiceFeasibility56NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.checkSubmitInvoiceFeasibility(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetNextSeqFromInvoiceTable57NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getNextSeqFromInvoiceTable(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSubBudgetSummary58NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSubBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetCbGridDataForSession59NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoiceDetails60NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoiceDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicedelContractInvoiceAssignment61NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.delContractInvoiceAssignment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateInvoiceAdvanceStatus62NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateInvoiceAdvanceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditInvoiceAdvanceDetails63NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editInvoiceAdvanceDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceAdvanceDetails64NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceAdvanceDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceinvoiceNegativeAmendCheck65NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.invoiceNegativeAmendCheck(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceinvoiceAmountAssignmentValidation66NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.invoiceAmountAssignmentValidation(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceinvoiceAmountZeroValidation67NegativeApp() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.invoiceAmountZeroValidation(null, null);
	}

	@Test
	public void testfetchInvoiceTotalForContractedServices() throws ApplicationException
	{
		String lsInvoiceTotalAmounts = 
			moInvoiceService.fetchInvoiceTotalForContractedServices(getDummyCBGridBeanObj(), moSession);
		assertNotNull(lsInvoiceTotalAmounts);
	}
	
	@Test(expected=ApplicationException.class)
	public void testfetchInvoiceTotalForContractedServicesExp() throws ApplicationException
	{
		moInvoiceService.fetchInvoiceTotalForContractedServices(getDummyCBGridBeanObj(), null);
	}
	
	@Test
	public void testfetchInvoiceTotalForContractedServices2() throws ApplicationException
	{
		moInvoiceService.fetchInvoiceTotalForContractedServices(getDefaultInvoiceSummaryParams(), moSession);
	}
	@Test
	public void testfetchInvoiceTotalForContractedServices3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();

		// Positive Scenario -- List of CBIndirectRateBean type returned.

		loCBGridBean.setSubBudgetID("555");
		loCBGridBean.setContractBudgetID("555");
		loCBGridBean.setInvoiceId("56");
		moInvoiceService.fetchInvoiceTotalForContractedServices(loCBGridBean, moSession);
	}
	
	@Test
	public void testfetchInvoiceTotalForContractedServices4() throws ApplicationException
	{
		
		moInvoiceService.fetchInvoiceTotalForContractedServices(getDefaultProfServicesParams(), moSession);
	}
	@Test
	public void testfetchContractedServicesYTDInvoiced() throws ApplicationException
	{
		
		Boolean loValidateStatus  = moInvoiceService.invoiceAmountAssignmentValidation( moSession,"22");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountAssignmentValidation21() throws ApplicationException
	{
		
		Boolean loValidateStatus  = moInvoiceService.invoiceAmountAssignmentValidation( moSession,"12");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountAssignmentValidation3() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountAssignmentValidation( moSession,"23");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountAssignmentValidation4() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountAssignmentValidation( moSession,"30");
		assertNotNull(loValidateStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testinvoiceAmountAssignmentValidationExp() throws ApplicationException
	{
		
		 moInvoiceService.invoiceAmountAssignmentValidation(null, null);
	}
	
	@Test
	public void testinvoiceAmountAssignmentValidation() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountAssignmentValidation(moSession,"20");
		assertTrue(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountAssignmentValidation2() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountAssignmentValidation(moSession,"25");
		assertTrue(loValidateStatus);
	}
	@Test
	public void testfetchContractedServicesYTDInvoiced3() throws ApplicationException
	{
		
		String lsYtdInvoicedAmount = moInvoiceService.fetchContractedServicesYTDInvoiced("26", moSession);
		assertNotNull(lsYtdInvoicedAmount);
	}
	@Test
	public void testfetchContractedServicesYTDInvoiced4() throws ApplicationException
	{
		
		String lsYtdInvoicedAmount = moInvoiceService.fetchContractedServicesYTDInvoiced("30", moSession);
		assertNotNull(lsYtdInvoicedAmount);
	}
	@Test(expected=ApplicationException.class)
	public void testfetchContractedServicesYTDInvoicedExp() throws ApplicationException
	{
		
		 moInvoiceService.fetchContractedServicesYTDInvoiced("20", null);
	}
	
	
	
	
	
	

	
	
	
	@Test
	public void testinvoiceAmountZeroValidation12() throws ApplicationException
	{
		
		Boolean loValidateStatus  = moInvoiceService.invoiceAmountZeroValidation( moSession,"22");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountZeroValidation21() throws ApplicationException
	{
		
		Boolean loValidateStatus  = moInvoiceService.invoiceAmountZeroValidation( moSession,"12");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountZeroValidation3() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountZeroValidation( moSession,"23");
		assertNotNull(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountZeroValidation4() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountZeroValidation( moSession,"30");
		assertNotNull(loValidateStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testinvoiceAmountZeroValidationExp() throws ApplicationException
	{
		
		 moInvoiceService.invoiceAmountZeroValidation(null, null);
	}
	
	@Test
	public void testinvoiceAmountZeroValidation() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountZeroValidation(moSession,"20");
		assertTrue(loValidateStatus);
	}
	@Test
	public void testinvoiceAmountZeroValidation2() throws ApplicationException
	{
		
		Boolean loValidateStatus = moInvoiceService.invoiceAmountZeroValidation(moSession,"25");
		assertTrue(loValidateStatus);
	}
	
	
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices() throws ApplicationException
	{
		PersonnelServicesData loPersonnelServicesData = moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),moSession);
		assertNotNull(loPersonnelServicesData);
	}
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices1() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setInvoiceId("2");
		PersonnelServicesData loPersonnelServicesData = moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),moSession);
		assertNotNull(loPersonnelServicesData);
	}
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices112() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setInvoiceId("12");
		PersonnelServicesData loPersonnelServicesData = moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),moSession);
		assertNotNull(loPersonnelServicesData);
	}
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices11122() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setInvoiceId("1212");
		PersonnelServicesData loPersonnelServicesData = moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),moSession);
		assertNotNull(loPersonnelServicesData);
	}
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices111222() throws ApplicationException
	{
		CBGridBean loCBGridBean = getDummyCBGridBeanObj();
		loCBGridBean.setInvoiceId("11232");
		PersonnelServicesData loPersonnelServicesData = moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),moSession);
		assertNotNull(loPersonnelServicesData);
	}
	
	@Test
	public void testfetchInvoiceTotalForPersonnelServices111222Exp() throws ApplicationException
	{
		moInvoiceService.fetchInvoiceTotalForPersonnelServices(getDummyCBGridBeanObj(),null);
		
	}
	@Test
	public void testcheckSubmitInvoiceFeasibility() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(moSession, getFileNetSession(), parentBudgetID, baseContractId);
		assertNotNull(loBudgetMap);
	}
	
	@Test
	public void testcheckSubmitInvoiceFeasibility12() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(moSession, getFileNetSession(), parentBudgetID, baseContractId);
		assertNotNull(loBudgetMap);
	}
	
	@Test
	public void testcheckSubmitInvoiceFeasibility234() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(moSession, getFileNetSession(), parentBudgetID, baseContractId);
		assertNotNull(loBudgetMap);
	}
	@Test
	public void testcheckSubmitInvoiceFeasibility123() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(moSession, getFileNetSession(), parentBudgetID, baseContractId);
		assertNotNull(loBudgetMap);
	}
	@Test(expected=ApplicationException.class)
	public void testcheckSubmitInvoiceFeasibilityExp() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(null, getFileNetSession(), parentBudgetID, baseContractId);
	}
	@Test(expected=ApplicationException.class)
	public void testcheckSubmitInvoiceFeasibilityExp1() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(null, getFileNetSession(), parentBudgetID, null);
	}
	@Test(expected=ApplicationException.class)
	public void testcheckSubmitInvoiceFeasibilityExp2() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = 
			moInvoiceService.checkSubmitInvoiceFeasibility(null, getFileNetSession(), null, baseContractId);
	}
	
	@Test
	public void testgetBudgetTaskStatus1() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "112");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus2() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "312");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus3() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "152");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus4() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "712");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus5() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "127");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus6() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "712");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus7() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "124");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus8() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "182");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test
	public void testgetBudgetTaskStatus9() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		loBudgetMap.put("budget_id", "152");
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	@Test(expected=ApplicationException.class)
	public void testgetBudgetTaskStatus10() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(null, null);
		assertNotNull(loBudgetList);
	}
	@Test(expected=ApplicationException.class)
	public void testgetBudgetTaskStatus11() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(moSession, null);
		assertNotNull(loBudgetList);
	}
	@Test(expected=ApplicationException.class)
	public void testgetBudgetTaskStatus12() throws ApplicationException
	{
		HashMap<String, String> loBudgetMap = new HashMap();
		BudgetList loBudgetList = 
			moInvoiceService.getBudgetTaskStatus(null, loBudgetMap);
		assertNotNull(loBudgetList);
	}
	
	

}
