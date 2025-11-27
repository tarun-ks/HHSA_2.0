package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.PaymentListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.FiscalYear;
import com.nyc.hhs.model.NycAgencyDetails;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.StatusR3;

public class PaymentListServiceTest
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

	PaymentListService moPaymentListService = new PaymentListService();
	/**
	 * Method used to set the required fields in the bean for testing scenarios
	 * 
	 * This method to be used while testing positive scenarios only. If these
	 * required fields not set on the Bean, AppEx is thrown (Consider while
	 * writing Exception Handling Junit cases)
	 * 
	 * @return
	 */
	private PaymentSortAndFilter setPaymentSortAndFilterBean()
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		//loPaymentSortAndFilter.setPaymentDisNum("Pending");
		return loPaymentSortAndFilter;
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgNullSuccess() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for org type null
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(null);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession,
					loPaymentSortAndFilter1);
			assertNull(loPaymentList);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCitySucess() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);

	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCityWithPaymentValueFromComma() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			loPaymentSortAndFilter1.setPaymentValueFrom("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCityWithPaymentValueToComma() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			loPaymentSortAndFilter1.setPaymentValueTo("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryCityException() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.CITY_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgProviderPaymentValueFromToSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			loPaymentSortAndFilter1.setPaymentValueFrom("10,000");
			loPaymentSortAndFilter1.setPaymentValueTo("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgProviderSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList.size() > 0);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryProviderException() throws ApplicationException
	{		
			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.PROVIDER_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}
	
	

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryAgencySuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.AGENCY_ORG);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList.size() != 0);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryAgencyFromToSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.AGENCY_ORG);
			loPaymentSortAndFilter1.setPaymentValueFrom("10,000");
			loPaymentSortAndFilter1.setPaymentValueTo("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryAgencyException() throws ApplicationException
	{
			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.AGENCY_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryAgencyDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.AGENCY_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);

	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryCityDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.CITY_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryProviderDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);
	}

	/**
	 * This method get the Payment List count on list screens.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetPaymentCountOrgNull() throws ApplicationException
	{
			// Positive Scenario -- returns a list with size greater than 0
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(null);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(StringUtils.equals(liPaymentCount.toString(), "0"));
	}
	
	/**
	 * This method get the Payment List count on list screens.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetPaymentCountCitySuccess() throws ApplicationException
	{
			// Positive Scenario -- returns a list with size greater than 0
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() > 0);
	}
	
	/**
	 * This method get the Payment List count on list screens.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetPaymentCountCityPaymentListNullSuccess() throws ApplicationException
	{
			// Positive Scenario -- returns a list with size greater than 0
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			loPaymentSortAndFilter1.setPaymentStatusList(null);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() == 0);
	}
	
	/**
	 * This method get the Payment List count on list screens.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetPaymentCountCity() throws ApplicationException
	{
			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.CITY_ORG);
			moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter2);
	}
	
	@Test
	public void testGetPaymentCountProviderSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() > 0);
	}
	
	@Test
	public void testGetPaymentCountProviderPaymentListNullSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = new PaymentSortAndFilter();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			loPaymentSortAndFilter1.setPaymentStatusList(null);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() == 0);
	}


	@Test(expected = ApplicationException.class)
	public void testGetPaymentCountProvider() throws ApplicationException
	{
			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.PROVIDER_ORG);
			moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter2);
	}

	@Test
	public void testGetPaymentCountAgencySuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 =  setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.AGENCY_ORG);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() > 0);
	}
	
	@Test
	public void testGetPaymentCountAgencyPaymentListSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = new PaymentSortAndFilter();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.AGENCY_ORG);
			loPaymentSortAndFilter1.setPaymentStatusList(null);
			Integer liPaymentCount = moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter1);
			assertTrue(liPaymentCount.intValue() == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetPaymentCountAgency() throws ApplicationException
	{
			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.AGENCY_ORG);
			moPaymentListService.getPaymentCount(moSession, loPaymentSortAndFilter2);
	}

	/**
	 * This method get the Payment List count on list screens.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetPaymentCountException() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
		loPaymentSortAndFilter1.setOrgType(ApplicationConstants.AGENCY_ORG);

		// Negative scenario with null session
		loPaymentListService.getPaymentCount(null, loPaymentSortAndFilter1);

	}

	@Test
	public void testGetFiscalInformation() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();

		// Positive Scenario -- Fiscal Information for the given user type
		// and ID is
		// fetched
		List<FiscalYear> loFiscalInfo = loPaymentListService.getFiscalInformation(moSession,
				ApplicationConstants.CITY_ORG, "accenture");
		assertTrue(loFiscalInfo.size() > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetFiscalInformationException() throws ApplicationException
	{

		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getFiscalInformation(null, ApplicationConstants.AGENCY_ORG, "accenture");

	}

	@Test(expected = ApplicationException.class)
	public void testGetFiscalInformationException2() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getFiscalInformation(null, null, null);

	}

	@Test
	public void testGetSatusList() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		String PAYMENT = "Payment";

		// Positive Scenario -- Status for the corresponding User Type, Org
		// ID and Process Type is returned
		List<StatusR3> loStatusList = loPaymentListService.getSatusList(moSession, ApplicationConstants.PROVIDER_ORG,
				"accenture", PAYMENT);
		assertTrue(loStatusList.size() > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetSatusListException() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		String PAYMENT = "Payment";
		// Negative Scenario -- ApplicationException is thrown for null
		// Process Type
		loPaymentListService.getSatusList(null, ApplicationConstants.PROVIDER_ORG, "accenture", PAYMENT);

	}

	@Test(expected = ApplicationException.class)
	public void testGetSatusListException2() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		// Negative Scenario -- ApplicationException is thrown for null
		// Process Type
		loPaymentListService.getSatusList(moSession, ApplicationConstants.PROVIDER_ORG, "accenture", null);

	}

	/**
	 * Method used to test getAgencyList method in PaymentListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetAgencyList() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();

		// Positive Scenario -- Agency List corresponding to the User Type,
		// Org ID is returned
		List<NycAgencyDetails> loAgencyList = loPaymentListService.getAgencyList(moSession,
				ApplicationConstants.AGENCY_ORG, "accenture");
		assertTrue(loAgencyList.size() > 0);

	}

	/**
	 * Method used to test getAgencyList method in PaymentListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetAgencyListException() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		// Negative Scenario -- ApplicationException is thrown for null SQL
		// session
		loPaymentListService.getAgencyList(null, ApplicationConstants.AGENCY_ORG, "accenture");

	}

	/**
	 * Method used to test getAgencyList method in PaymentListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetAgencyListException2() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		// Negative Scenario -- ApplicationException is thrown for null SQL
		// session
		loPaymentListService.getAgencyList(null, null, null);

	}

	@Test
	public void testGetProgramName() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();

		// Positive Scenario -- Agency List corresponding to the User Type,
		// Org ID is returned
		List<ProgramNameInfo> loProgramName = loPaymentListService.getProgramName(moSession,
				ApplicationConstants.AGENCY_ORG, "accenture");
		assertTrue(loProgramName.size() > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetProgramNameException() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		// Negative Scenario -- ApplicationException is thrown for null
		// session
		loPaymentListService.getProgramName(null, ApplicationConstants.AGENCY_ORG, "accenture");

	}

	@Test(expected = ApplicationException.class)
	public void testGetProgramNameException2() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		// Negative Scenario -- ApplicationException is thrown for null
		// session
		loPaymentListService.getProgramName(null, null, null);

	}
	
	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicefetchPaymentListSummary0Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.fetchPaymentListSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetPaymentCount1Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getPaymentCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetFiscalInformation2Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getFiscalInformation(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetSatusList3Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getSatusList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetAgencyList4Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getAgencyList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetProgramName5Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getProgramName(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicefetchBudgetIdListFromContractId6Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.fetchBudgetIdListFromContractId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}


}
