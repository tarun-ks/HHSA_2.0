package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractDetailsBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class FinancialsListServiceTest
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session
	private static FinancialsListService loFinancialsListService = null;

	public String amendContractId = "63";
	public String updateContractId = "561";
	public String baseContractId = "53";
	public String contractBudgetID = "24";
	public String subBudgetID = "24";
	public String parentSubBudgetID = "14";
	public String parentBudgetID = "13";
	public String invoiceId = "55";
	public String agency = "agency_12";
	public String provider = "803";
	public String vendorFmsId = "999";

	// base line items id
	public String contractedServiceId = "13";
	public String rateId = "8";
	public String milestoneId = "7";
	public String personalServiceSalariedId = "11";
	public String programIncomeId = "84";
	public String unallocatedId = "12";
	public String equipmentId = "5";
	public String rentId = "8";
	public String IndRateId = "12";
	public String utilitiesId = "100";

	public String validEpin = "91600L0911CNVA163";
	public String fiscalYear = "2014";
	public String invalidContractId = "522222222223";

	public String amendContractIdApproved = "60";
	public String contractBudgetIDApproved = "22";
	public String subBudgetIDApproved = "22";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		loFinancialsListService = new FinancialsListService();
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		session = getFileNetSession();
	}

	public P8UserSession getFileNetSession() throws ApplicationException
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
		loUserSession.setIsolatedRegionNumber("3");
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		moSession.close();
		session.getFilenetPEDBSession().close();
		moSession.rollback();
	}

	/**
	 * Method used to set the required fields in the bean for testing scenarios
	 * 
	 * This method to be used while testing positive scenarios only. If these
	 * required fields not set on the Bean, AppEx is thrown (Consider while
	 * writing Exception Handling Junit cases)
	 * 
	 * @return
	 */
	private ContractList setContractListFilterParams()
	{
		ContractList loContractList = new ContractList();
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("61");
		loContractStatusList.add("67");
		loContractStatusList.add("58");
		loContractStatusList.add("60");
		loContractStatusList.add("59");
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortDate(true);
		loContractList.setSecondSortDate(true);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setSortColumnName(HHSConstants.CONTRACT_STATUS);
		loContractList.setOrgName("accenture");
		loContractList.setOrgType("city_org");
		return loContractList;
	}

	/**
	 * This method test whether Contract List is fetched successfully or not for
	 * a CITY User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractListCity() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- List of loContractList type returned
			// for CITY
			ContractList loContractList = setContractListFilterParams();

			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.CITY_ORG);
			assertTrue(loContractListReturned.size() != 0);

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean

			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, null,
					ApplicationConstants.CITY_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractListProvider1() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider12() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider13() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider14() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider15() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider16() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider17() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider18() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider19() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider20() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListAgency21() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.AGENCY_ORG);
		loContractList.setOrgName("DOC");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.AGENCY_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListCity1() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- List of loContractList type returned
			// for CITY
			ContractList loContractList = setContractListFilterParams();

			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.PROVIDER_ORG);
			assertTrue(loContractListReturned.size() != 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method test whether Contract List is fetched successfully or not for
	 * a provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractListProvider() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- List of loContractList type returned
			// for CITY
			ContractList loContractList = setContractListFilterParams();
			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.PROVIDER_ORG);
			assertTrue(loContractListReturned.size() != 0);

			loContractList.setOrgName("provider");
			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, loContractList,
					ApplicationConstants.PROVIDER_ORG);
			assertTrue(loContractListReturned.size() == 0);

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean

			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, null,
					ApplicationConstants.PROVIDER_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method test whether Contract List is fetched successfully or not for
	 * an agency User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractListAgency() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- List of loContractList type returned
			// for CITY
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("DOC");
			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() != 0);

			loContractList.setOrgName("DHS");
			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, loContractList,
					ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() == 0);

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean

			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, null,
					ApplicationConstants.AGENCY_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractListAgency9() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("DOC");
			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(null,
					loContractList, ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() != 0);

			loContractList.setOrgName("DHS");
			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, loContractList,
					ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() == 0);

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean

			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, null,
					ApplicationConstants.AGENCY_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractListAgency14() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("accenture");

			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() != 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractListAgency15() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("accenture");
			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() != 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method get the Contract List count on list screen.
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetContractCountCity() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		Integer loContractListReturned = loFinancialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.CITY_ORG);
		assertNotNull(loContractListReturned);
	}

	/**
	 * This method get the Contract List count on list screen.
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetContractCountProvider123() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgName("r3_org");
		loContractList.setAgencyId("DOC");
		Integer loContractListReturned = loFinancialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.PROVIDER_ORG);
		assertNotNull(loContractListReturned);
	}

	@Test
	public void testGetContractCountProvider() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgName("r3_org");
		loContractList.setAgencyId("DOC");
		Integer loContractListReturned = loFinancialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.PROVIDER_ORG);
		assertNotNull(loContractListReturned);
	}

	@Test
	public void testGetContractCountAgency() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgName("DHS");
		Integer loContractListReturned = loFinancialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.AGENCY_ORG);
		assertTrue(loContractListReturned == 0);
	}

	/**
	 * Method used to test getAgencyList method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetAgencyList() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- Agency List corresponding to the User Type,
			// Org ID is returned
			List<HashMap<String, String>> loAgencyList = loFinancialsListService.fetchAgencyNames(moSession,
					ApplicationConstants.PROVIDER_ORG);
			assertTrue(loAgencyList.size() > 0);

			// Negative Scenario -- ApplicationException is thrown for wrong Org
			SqlSession session = null;
			loAgencyList = loFinancialsListService.fetchAgencyNames(session, ApplicationConstants.PROVIDER_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetAgencyList3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			List<HashMap<String, String>> loAgencyList = loFinancialsListService.fetchAgencyNames(moSession,
					ApplicationConstants.AGENCY_ORG);
			assertTrue(loAgencyList.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetAgencyList4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			List<HashMap<String, String>> loAgencyList = loFinancialsListService.fetchAgencyNames(moSession,
					ApplicationConstants.AGENCY_ORG);
			assertTrue(loAgencyList.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetAgencyList5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			List<HashMap<String, String>> loAgencyList = loFinancialsListService.fetchAgencyNames(moSession, null);
			assertTrue(loAgencyList.size() == 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error while fetching agency names", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetAgencyList6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			List<HashMap<String, String>> loAgencyList = loFinancialsListService.fetchAgencyNames(moSession, "HGH");
			assertTrue(loAgencyList.size() > 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test get contracts value method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testgetContractsValue() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			// Positive Scenario -- Contracts Amount corresponding to the User
			// Type,
			String loContractAmount = loFinancialsListService.getContractsValue(moSession, loContractList,
					ApplicationConstants.PROVIDER_ORG);
			//assertTrue(loContractAmount > 0);

			loContractList.setOrgName("null");
			loContractAmount = loFinancialsListService.getContractsValue(moSession, loContractList,
					ApplicationConstants.PROVIDER_ORG);
		//	assertTrue(loContractAmount == 0);

			// Negative Scenario -- ApplicationException is thrown for wrong Org
			loContractAmount = loFinancialsListService.getContractsValue(moSession, null,
					ApplicationConstants.PROVIDER_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testgetContractsValue4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			// Negative Scenario -- Contracts Amount corresponding to the User

			String loContractAmount = loFinancialsListService.getContractsValue(moSession, loContractList,
					ApplicationConstants.ACCELERATOR);
		//	assertTrue(loContractAmount == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testgetContractsValue5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			// Negative Scenario -- Contracts Amount corresponding to the User

			String loContractAmount = loFinancialsListService.getContractsValue(null, loContractList,
					ApplicationConstants.PROVIDER_ORG);
			//assertTrue(loContractAmount == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testgetContractsValue6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			// Negative Scenario -- Contracts Amount corresponding to the User

			String loContractAmount = loFinancialsListService.getContractsValue(moSession, loContractList, "");
			//assertTrue(loContractAmount == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testgetContractsValue7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			// Negative Scenario -- Contracts Amount corresponding to the User

			String loContractAmount = loFinancialsListService.getContractsValue(moSession, loContractList, "");
			//assertTrue(loContractAmount == 0);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a false
	 * Authentication flag
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleWithFalseFlag() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = false;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a false
	 * Authentication flag Expected result success
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag2() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag3() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag4() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag5() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag6() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag7() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testupdateConfigurationErrorCheckRuleWithTrueFlag8() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a true
	 * Authentication flag and everything else okay Expected result success
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRule() throws ApplicationException
	{
		boolean lbThrown = false;

		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = updateContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a true
	 * Authentication flag and an Invalid ContractId Expected result success (as
	 * it checks the availability of the Contract already in DB table )
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleWithInvalidContractId() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = "1109899564654";

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a true
	 * Authentication flag and The Contract Status is other than Update Expected
	 * result success
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleContractTaskStatusOtherThanUpdate() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = baseContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a true
	 * Authentication flag and The Contract Status is Update Expected result
	 * error
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleContractTaskStatusUpdate() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = baseContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("error", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test updateConfigurationErrorCheckRule method with a true
	 * Authentication flag and the business rules failure against the Contract
	 * (ContractId provided) Expected result error
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testupdateConfigurationErrorCheckRuleRulesFailure() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean loAuthFlag = true;
			String loContractId = baseContractId;

			HashMap loHMError = loFinancialsListService.updateConfigurationErrorCheckRule(moSession, session,
					loContractId, loAuthFlag);

			assertNotNull(loHMError);
			assertEquals("error", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}


	/**
	 * Method used to test ValidateRenewContractDetails method in
	 * FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateRenewContractDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;

			// positive
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails("1237689543002", moSession);
			assertTrue(lbSuccessStatus);

			// negative
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails("dummy", null);
			assertTrue(!lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateRenewContractDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;

			// positive
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails("1237689543012", moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateRenewContractDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails("1237689543012BB", moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateRenewContractDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{ // Negative
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails("", moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateRenewContractDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{ // Negative
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			lbSuccessStatus = loFinancialsListService.validateRenewContractDetails(null, moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test ValidateRenewalRecordExist method in
	 * FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testRenewalRecordExist() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			HashMap aoContractStatus = new HashMap();

			// negative
			aoContractStatus.put("contract_Id", baseContractId);
			List<String> asContractStatus = new ArrayList<String>();
			asContractStatus.add("68");
			aoContractStatus.put("status_Id", asContractStatus);
			aoContractStatus.put("contract_type_Id", "1");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, moSession);
			assertTrue(lbSuccessStatus);

			// positive
			aoContractStatus.put("contract_type_Id", "2");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, moSession);
			assertTrue(!lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testRenewalRecordExistExp() throws Exception
	{
		Boolean lbThrown = false;

		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus;
		HashMap aoContractStatus = new HashMap();

		// negative
		aoContractStatus.put("contract_Id", baseContractId);
		List<String> asContractStatus = new ArrayList<String>();
		asContractStatus.add("68");
		aoContractStatus.put("status_Id", asContractStatus);
		aoContractStatus.put("contract_type_Id", "1");
		lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, null);
		assertTrue(lbSuccessStatus);

	}

	@Test
	public void testRenewalRecordExist3() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			HashMap aoContractStatus = new HashMap();
			List<String> loStatusList = new ArrayList<String>();
			loStatusList.add("67");
			loStatusList.add("68");
			loStatusList.add("58");
			// positive
			aoContractStatus.put("contract_Id", baseContractId);
			aoContractStatus.put("status_Id", loStatusList);
			aoContractStatus.put("contract_type_Id", "3");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, moSession);
			assertFalse(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewalRecordExist4() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			HashMap aoContractStatus = new HashMap();
			List<String> loStatusList = new ArrayList<String>();
			loStatusList.add("");
			loStatusList.add("");
			loStatusList.add("");
			// positive
			aoContractStatus.put("contract_Id", baseContractId);
			aoContractStatus.put("status_Id", loStatusList);
			aoContractStatus.put("contract_type_Id", "3");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewalRecordExist5() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			HashMap aoContractStatus = new HashMap();
			List<String> loStatusList = new ArrayList<String>();
			loStatusList.add("");
			loStatusList.add("");
			loStatusList.add("");
			// positive
			aoContractStatus.put("contract_Id", null);
			aoContractStatus.put("status_Id", null);
			aoContractStatus.put("contract_type_Id", "3");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewalRecordExist6() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			HashMap aoContractStatus = new HashMap();
			List<String> loStatusList = new ArrayList<String>();
			loStatusList.add("67");
			loStatusList.add("68");
			loStatusList.add("58");
			// positive
			aoContractStatus.put("contract_Id", null);
			aoContractStatus.put("status_Id", null);
			aoContractStatus.put("contract_type_Id", "3");
			lbSuccessStatus = loFinancialsListService.renewalRecordExist(aoContractStatus, null);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test GetNextSeqFromTable method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetNextSeqFromTable() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = true;
		int aiCurrentSeq;
		aiCurrentSeq = loFinancialsListService.getNextSeqFromTable(moSession);
		if (aiCurrentSeq <= 0)
		{
			lbSuccessStatus = false;
		}
		assertTrue(lbSuccessStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testGetNextSeqFromTableExp() throws ApplicationException
	{
		Boolean lbThrown = false;

		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = true;
		int aiCurrentSeq;
		aiCurrentSeq = loFinancialsListService.getNextSeqFromTable(moSession);
		if (aiCurrentSeq <= 0)
		{
			lbSuccessStatus = false;
		}
		assertTrue(lbSuccessStatus);

		try
		{
			loFinancialsListService.getNextSeqFromTable(null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	/**
	 * Method used to test ValidateUpdateWorkFlowRenewContract method in
	 * FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testRenewContractDetails1() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
		aoContractDetailByEpin.setContractTypeId("3");
		aoContractDetailByEpin.setContractId("2222222222");
		Integer aiCurrentSeq = 222222;
		aoContractDetailByEpin.setAgencyId("DOC");
		try
		{
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testRenewContractDetails2() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId("");
			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewContractDetails3() throws ApplicationException
	{// negative
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId("B");
			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewContractDetails4() throws ApplicationException
	{ // negative
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setAwardEpin("BH");
			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewContractDetails5() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId(null);
			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testRenewContractDetails6() throws ApplicationException
	{ // negative
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			Integer aiCurrentSeq = 2342342;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testRenewContractDetails6Exp() throws ApplicationException
	{ // negative
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;

		EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
		Integer aiCurrentSeq = 2342342;
		HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
				aoContractDetailByEpin, null);

	}

	@Test
	public void testRenewContractDetails7() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		boolean lbSuccessStatus = false;
		try
		{
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId("B");
			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.renewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, null);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	private EPinDetailBean setEpinDetailBean()
	{
		EPinDetailBean aoContractDetailByEpin = new EPinDetailBean();
		aoContractDetailByEpin.setContractId(amendContractId);
		aoContractDetailByEpin.setContractTypeId("2");
		aoContractDetailByEpin.setAmendValue("100");
		aoContractDetailByEpin.setAmendmentStart("12/12/2014");
		aoContractDetailByEpin.setAmendmentEnd("12/12/2014");
		aoContractDetailByEpin.setProposedContractEnd("12/12/2014");
		aoContractDetailByEpin.setParentContractId(baseContractId);
		aoContractDetailByEpin.setEpinId(validEpin);
		aoContractDetailByEpin.setProjProg("123");
		aoContractDetailByEpin.setAmendmentReason("asas");
		aoContractDetailByEpin.setProcurementStartDate("01/01/2013");
		aoContractDetailByEpin.setAgencyDiv(baseContractId);
		aoContractDetailByEpin.setProcMethod("meth");
		aoContractDetailByEpin.setProgramId("1");
		aoContractDetailByEpin.setProcDescription("desc");
		aoContractDetailByEpin.setContractTitle("title");
		aoContractDetailByEpin.setVendorFmsId(vendorFmsId);
		aoContractDetailByEpin.setAwardAgencyId(agency);
		aoContractDetailByEpin.setVendorFmsName("name");
		aoContractDetailByEpin.setProviderLegalName("O_name");
		aoContractDetailByEpin.setContractValue("100");
		aoContractDetailByEpin.setContractStart("01/01/2013");
		aoContractDetailByEpin.setContractEnd("11/01/2013");
		aoContractDetailByEpin.setContractSourceId(HHSConstants.TWO);
		aoContractDetailByEpin.setRegistrationFlag("0");
		aoContractDetailByEpin.setUpdateFlag("0");
		aoContractDetailByEpin.setCreateByUserId(agency);
		aoContractDetailByEpin.setModifyByUserId(agency);
		aoContractDetailByEpin.setStatusId("62");
		aoContractDetailByEpin.setDeleteFlag("0");
		aoContractDetailByEpin.setDiscrepancyFlag("0");
		aoContractDetailByEpin.setAgencyId(agency);
		return aoContractDetailByEpin;
	}

	/**
	 * This method Validate close contract when Authentication is true
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testvalidateCloseContract() throws ApplicationException
	{
		boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Boolean lbAuthFlag = true;
			String lsContractId = baseContractId;
			HashMap loHMError = loFinancialsListService.validateCloseContract(moSession, session, lsContractId,
					lbAuthFlag);

			assertNotNull(loHMError);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;

			assertTrue(lbThrown);
		}
	}

	/**
	 * This method Validate close contract when Authentication is false
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testvalidateCloseContractAuthFail() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();

		Boolean lbAuthFlag = false;
		String lsContractId = baseContractId;
		HashMap loHMError = loFinancialsListService.validateCloseContract(moSession, session, lsContractId, lbAuthFlag);

		assertNotNull(loHMError);
		assertEquals(1, loHMError.get("errorCode"));

	}

	/**
	 * This method Validate close contract when Exception is expected
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testvalidateCloseContractException() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();

		Boolean lbAuthFlag = true;
		String lsContractId = null;
		HashMap loHMError = loFinancialsListService.validateCloseContract(moSession, session, lsContractId, lbAuthFlag);
	}

	/**
	 * This method close contract when no error is set
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCloseContract() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();

		HashMap loHMErrorRule = new HashMap();
		loHMErrorRule.put("errorCode", 0);
		String lsContractId = baseContractId;
		boolean lbResult = loFinancialsListService.closeContract(moSession, lsContractId, loHMErrorRule);
		assertTrue(lbResult);

	}

	/**
	 * This method close contract when error is set
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCloseContractWithError() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap loHMErrorRule = new HashMap();
		loHMErrorRule.put("errorCode", 1);
		String lsContractId = baseContractId;
		boolean lbResult = loFinancialsListService.closeContract(moSession, lsContractId, loHMErrorRule);
		assertFalse(lbResult);

	}

	/**
	 * This method close contract when Exception is expected
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCloseContractException() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap loHMErrorRule = new HashMap();
		loHMErrorRule.put("errorCode", 0);
		String lsContractId = null;
		loFinancialsListService.closeContract(moSession, lsContractId, loHMErrorRule);
	}

	/**
	 * This method test for suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractAuthValid() throws ApplicationException
	{
		// Positive Scenario -- update DB in case of suspend Contract for
		// valid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonSuspend";
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, agency, "city_org");
		assertNotNull(lbUnsuspendStatusList);
	}

	@Test(expected = ApplicationException.class)
	public void testSuspendContractExp() throws ApplicationException
	{
		// Positive Scenario -- update DB in case of suspend Contract for
		// valid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonSuspend";
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, null, loAuthStatusFlag, agency, "city_org");
	}

	/**
	 * This method test for suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractAuthValid1() throws ApplicationException
	{
		String lsContractId = baseContractId;
		String lsContractReason = "reasonSuspend";
		Boolean loAuthStatusFlag = true;
		// Positive Scenario -- Do not update DB in case of suspend Contract
		// when authentication fail.
		loAuthStatusFlag = null;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractAuthValid2() throws ApplicationException
	{
		String lsContractId = baseContractId;
		String lsContractReason = "reasonSuspend";
		Boolean loAuthStatusFlag = false;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for suspend Contract.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSuspendContractAuthValidException() throws ApplicationException
	{
		String lsContractId = null;
		Boolean loAuthStatusFlag = true;
		String lsContractReason = "reasonSuspend";
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for suspend Contract.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSuspendContractAuthValidException1() throws ApplicationException
	{
		String lsContractId = null;
		Boolean loAuthStatusFlag = true;
		String lsContractReason = null;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.suspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckStatusIdForSuspended() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckStatusIdForSuspendedExp() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, null);
		assertFalse(lbSuspendStatus);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckStatusIdForSuspended1() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckStatusIdForSuspended2() throws ApplicationException
	{
		// Positive Scenario -- Do not update DB in case of suspend Contract
		// for invalid user.
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckStatusIdForSuspended4() throws ApplicationException
	{
		// Positive Scenario -- Do not update DB in case of suspend Contract
		// for invalid user.
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckStatusIdForSuspended3() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckStatusIdForSuspendedException() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
	}

	/**
	 * This method Check exception Status Id For Suspended Exception.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckStatusIdForSuspendedException2() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		loFinancialsListService.checkStatusIdForSuspended(lsContractId, null);
	}

	@Test
	public void testCheckStatusIdForSuspendedException8() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckStatusIdForSuspendedException9() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "111777B";
		loFinancialsListService.checkStatusIdForSuspended(lsContractId, moSession);
	}

	/**
	 * This method test for un suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnSuspendContractAuthValid() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario -- update DB in case of unsuspend Contract for
		// valid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonUnSuspend";
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, agency, "city_org");
		assertNotNull(lbUnsuspendStatusList);
	}

	@Test(expected = ApplicationException.class)
	public void testUnSuspendContractExp() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario -- update DB in case of unsuspend Contract for
		// valid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonUnSuspend";
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, null, loAuthStatusFlag, agency, "city_org");

	}

	/**
	 * This method test for un suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnSuspendContractAuthValid1() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario -- update DB in case of unsuspend Contract for
		// valid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonUnSuspend";
		Boolean loAuthStatusFlag = true;
		loAuthStatusFlag = null;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for un suspend Contract.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnSuspendContractAuthValid2() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario -- Do not update DB in case of unsuspend
		// Contract for invalid user.
		String lsContractId = baseContractId;
		String lsContractReason = "reasonUnSuspend";
		Boolean loAuthStatusFlag = false;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for un suspend Contract.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUnSuspendContractAuthException() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		String lsContractReason = "reasonUnSuspend";
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, null, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * This method test for un suspend Contract.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUnSuspendContractAuthException1() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		String lsContractReason = null;
		Boolean loAuthStatusFlag = true;
		List<HhsAuditBean> lbUnsuspendStatusList = loFinancialsListService.unSuspendContract(lsContractId,
				lsContractReason, moSession, loAuthStatusFlag, "", "");
		assertNotNull(lbUnsuspendStatusList);
	}

	/**
	 * Method used to test amendContractDetail method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testAmendContractDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();

			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId("");
			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId("BH");
			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractTypeId(null);
			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setAwardEpin(null);
			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setEpinId(null);
			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAmendContractDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();

			Integer aiCurrentSeq = 14;
			HashMap aoHMWFRequiredProps = loFinancialsListService.amendContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, null);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test addNewContractDetails method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testAddNewContractDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setAgencyId("DOC");
			Integer aiCurrentSeq = 992222;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAddNewContractDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setEpinId(null);
			aoContractDetailByEpin.setProcurementStartDate("01/01/2013");
			aoContractDetailByEpin.setAgencyDiv(baseContractId);
			aoContractDetailByEpin.setAgencyId("ACS");
			aoContractDetailByEpin.setProcMethod("meth");
			Integer aiCurrentSeq = 1;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAddNewContractDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setEpinId("WERONG");
			aoContractDetailByEpin.setProcurementStartDate("01/01/2013");
			aoContractDetailByEpin.setAgencyDiv(baseContractId);
			aoContractDetailByEpin.setAgencyId("ACS");
			aoContractDetailByEpin.setProcMethod("meth");
			Integer aiCurrentSeq = 1;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAddNewContractDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = new EPinDetailBean();
			Integer aiCurrentSeq = 1;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, null);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAddNewContractDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();

			Integer aiCurrentSeq = 2;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testAddNewContractDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{

			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus = false;
			EPinDetailBean aoContractDetailByEpin = setEpinDetailBean();
			aoContractDetailByEpin.setContractId(null);
			Integer aiCurrentSeq = 1;
			HashMap aoHMWFRequiredProps = loFinancialsListService.addNewContractDetails(aiCurrentSeq,
					aoContractDetailByEpin, moSession);

			if (aoHMWFRequiredProps != null)
			{
				lbSuccessStatus = true;
			}
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateAmenBudgetStatus() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();
			loContractBean.setAmendReason("pass");
			loContractBean.setContractId("1888888");
			loContractBean.setContractTypeId(HHSConstants.TWO);
			loContractBean.setModifyBy("city_142");
			loContractBean.setContractStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"CONTRACT_CANCELLED"));
			loContractBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"BUDGET_CANCELLED"));
			loContractBean.setTaskStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_CANCELLED"));
			loContractBean.setProcStatusIds(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_COMPLETE"));
			loContractBean.setBudgetTypeId(HHSConstants.ONE);

			Boolean lbStatus = loFinancialsListService.updateAmenBudgetStatus(moSession, loContractBean);
			assertTrue(lbStatus);

			loFinancialsListService.updateAmenBudgetStatus(null, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateAmenBudgetStatus3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();
			loContractBean.setAmendReason("pass");
			loContractBean.setContractId("56B");
			loContractBean.setContractTypeId(HHSConstants.TWO);
			loContractBean.setModifyBy("city_142");
			loContractBean.setContractStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"CONTRACT_CANCELLED"));
			loContractBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"BUDGET_CANCELLED"));
			loContractBean.setTaskStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_CANCELLED"));
			loContractBean.setProcStatusIds(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_COMPLETE"));
			loContractBean.setBudgetTypeId(HHSConstants.ONE);

			Boolean lbStatus = loFinancialsListService.updateAmenBudgetStatus(moSession, loContractBean);
			assertTrue(lbStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateAmenBudgetStatus4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();
			loContractBean.setAmendReason("pass");
			loContractBean.setContractId(null);
			loContractBean.setContractTypeId(HHSConstants.TWO);
			loContractBean.setModifyBy("city_142");
			loContractBean.setContractStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"CONTRACT_CANCELLED"));
			loContractBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"BUDGET_CANCELLED"));
			loContractBean.setTaskStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_CANCELLED"));
			loContractBean.setProcStatusIds(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_COMPLETE"));
			loContractBean.setBudgetTypeId(HHSConstants.ONE);

			Boolean lbStatus = loFinancialsListService.updateAmenBudgetStatus(moSession, loContractBean);
			assertTrue(lbStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateAmenBudgetStatus5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();
			loContractBean.setAmendReason("pass");
			loContractBean.setContractId(baseContractId);
			loContractBean.setContractTypeId(HHSConstants.TWO);
			loContractBean.setModifyBy("city_142");
			loContractBean.setContractStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"CONTRACT_CANCELLED"));
			loContractBean.setBudgetStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"BUDGET_CANCELLED"));
			loContractBean.setTaskStatus(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_CANCELLED"));
			loContractBean.setProcStatusIds(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					"TASK_COMPLETE"));
			loContractBean.setBudgetTypeId("B");

			Boolean lbStatus = loFinancialsListService.updateAmenBudgetStatus(moSession, loContractBean);
			assertTrue(lbStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test validateProvider method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testValidateProvider() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;

			// positive
			lbSuccessStatus = loFinancialsListService.validateProvider("10", moSession);
			assertFalse(lbSuccessStatus);

			// negative
			lbSuccessStatus = loFinancialsListService.validateProvider("10", null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateProvider3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			// positive
			lbSuccessStatus = loFinancialsListService.validateProvider(baseContractId, moSession);
			assertTrue(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateProvider4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			// positive
			lbSuccessStatus = loFinancialsListService.validateProvider("", moSession);
			assertFalse(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateProvider5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			// positive
			lbSuccessStatus = loFinancialsListService.validateProvider("DB", moSession);
			assertFalse(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateProvider6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			boolean lbSuccessStatus;
			// positive
			lbSuccessStatus = loFinancialsListService.validateProvider(null, moSession);
			assertFalse(lbSuccessStatus);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}


	/**
	 * Method used to test business rule method cancelContractErrorCheckRule
	 * with Invalid contractId- in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithException() throws ApplicationException
	{

		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "99999999";
		loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId, ApplicationConstants.CITY_ORG);

	}

	/**
	 * Method used to test business rule method cancelContractErrorCheckRule
	 * with Invalid contractId- in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithInvalidContractId() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId, ApplicationConstants.CITY_ORG);
		// assertFalse(lbReturnValue);

	}

	/**
	 * Method used to test business rule method cancelContractErrorCheckRule
	 * with Invalid contractId- in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@Test
	public void testCancelContractErrorCheckRuleWithValidContractId() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.CITY_ORG);
		assertTrue(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithValidContractId4() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "66345345345345";
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.CITY_ORG);
		assertTrue(lbReturnValue);

	}

	@Test
	public void testCancelContractErrorCheckRuleWithValidContractId5() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.AGENCY_ORG);
		assertFalse(lbReturnValue);

	}

	@Test
	public void testCancelContractErrorCheckRuleWithValidContractId6() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "660";
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.AGENCY_ORG);
		assertFalse(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithValidContractId7() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "660B";
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.AGENCY_ORG);
		assertFalse(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithValidContractId8() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.AGENCY_ORG);
		assertFalse(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithValidContractId9() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(null, lsContractId,
				ApplicationConstants.AGENCY_ORG);
		assertFalse(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractErrorCheckRuleWithValidContractId10() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean lbReturnValue = loFinancialsListService.cancelContractErrorCheckRule(moSession, lsContractId,
				ApplicationConstants.CITY_ORG);
		assertFalse(lbReturnValue);

	}

	/**
	 * Method used to test cancelContract with businessRuleValidation flag as
	 * False-in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */

	/*@Test
	public void testCancelContractWithFalseFlagWithValidContractId() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.FALSE;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, "city_org", "");
		assertFalse(lbRuleFlag);

	}

	@Test
	public void testCancelContractWithFalseFlagWithInValidContractId() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.FALSE;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = "99999999";
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, "city_org", "");
		assertFalse(lbRuleFlag);
	}

	@Test
	public void testCancelContractWithTrueFlagWithValidContractId() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.TRUE;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, agency, "city_org");
		assertFalse(lbRuleFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractForInValidContractWithException() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.TRUE;
		String lsContractId = "99999999";
		FinancialsListService loFinancialsListService = new FinancialsListService();
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, "city_org", "");
		assertFalse(lbRuleFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractForInValidContractWithException6() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.TRUE;
		String lsContractId = "99999999";
		FinancialsListService loFinancialsListService = new FinancialsListService();
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, "city_org", "");
		assertFalse(lbRuleFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testCancelContractForInValidContractWithException7() throws ApplicationException
	{
		Boolean lbRuleFlag = Boolean.TRUE;
		String lsContractId = null;
		FinancialsListService loFinancialsListService = new FinancialsListService();
		lbRuleFlag = loFinancialsListService.cancelContract(moSession, lsContractId, lbRuleFlag, "city_org", "");
		assertFalse(lbRuleFlag);
	}*/

	/**
	 * Method used to test validateAmendContract method in FinancialsListService
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testValidateAmendContract() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		Map loHashMap = new HashMap();
		loHashMap.put("contract_type_Id", "2");
		List loList = new ArrayList();
		loList.add("59");
		loList.add("60");
		loList.add("61");
		loHashMap.put("status_Id", loList);
		loHashMap.put("contract_Id", baseContractId);
		boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(moSession, (HashMap) loHashMap);
		assertEquals(true, lbpendingRegistration);
	}

	@Test(expected = ApplicationException.class)
	public void testValidateAmendContractExp() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		Map loHashMap = new HashMap();
		loHashMap.put("contract_type_Id", "2");
		List loList = new ArrayList();
		loList.add("59");
		loList.add("60");
		loList.add("61");
		loHashMap.put("status_Id", loList);
		loHashMap.put("contract_Id", baseContractId);
		boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(null, (HashMap) loHashMap);
		assertEquals(true, lbpendingRegistration);
	}

	@Test
	public void testValidateAmendContract3() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Map loHashMap = new HashMap();
			loHashMap.put("contract_type_Id", "2");
			List loList = new ArrayList();
			loList.add("");
			loList.add("");
			loList.add("");
			loHashMap.put("status_Id", loList);
			loHashMap.put("contract_Id", baseContractId);
			boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(moSession,
					(HashMap) loHashMap);
			assertEquals(true, lbpendingRegistration);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateAmendContract4() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Map loHashMap = new HashMap();
			loHashMap.put("contract_type_Id", "2");
			List loList = new ArrayList();

			loHashMap.put("status_Id", null);
			loHashMap.put("contract_Id", baseContractId);
			boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(moSession,
					(HashMap) loHashMap);
			assertEquals(true, lbpendingRegistration);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateAmendContract5() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Map loHashMap = new HashMap();
			loHashMap.put("contract_type_Id", "2");
			List loList = new ArrayList();
			loList.add("45");
			loList.add("56");
			loHashMap.put("status_Id", loList);
			loHashMap.put("contract_Id", baseContractId);
			boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(moSession,
					(HashMap) loHashMap);
			assertEquals(true, lbpendingRegistration);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateAmendContract6() throws ApplicationException
	{
		// Negative
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			Map loHashMap = new HashMap();
			loHashMap.put("contract_type_Id", "2");
			List loList = new ArrayList();

			loHashMap.put("status_Id", loList);
			loHashMap.put("contract_Id", baseContractId);
			boolean lbpendingRegistration = loFinancialsListService.validateAmendContract(null, (HashMap) loHashMap);
			assertEquals(true, lbpendingRegistration);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Positive Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testGetFinancialWFProperty() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty21() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty3() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty4() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty5() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty6() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test
	public void testGetFinancialWFProperty7() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	@Test(expected = ApplicationException.class)
	public void testGetFinancialWFPropertyExp() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(null, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Positive Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testGetFinancialWFProperty1() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Positive Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testGetFinancialWFProperty2() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, null);
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, null);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Positive Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testGetFinancialWFProperty33() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Negative Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testGetFinancialWFProperty43() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111767");
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "10");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Negative Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testGetFinancialWFProperty53() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111767");
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, null);
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, null);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method tests for GetFinancialWFProperty - Negative Scenario
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testGetFinancialWFProperty63() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "111767");
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, null);
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_PROCUREMENT_CERTIFICATION_FUND);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "555");
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, "55");
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);

		loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
	}

	/**
	 * This test method is added in Release 6.
	 * It is for GetFinancialWFProperty
	 * @throws ApplicationException
	 */
	@Test
	public void testGetFinancialWFPropertyForReturnedPayment() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		HashMap aoHMWFRequiredProps = new HashMap();
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, "11632");
		aoHMWFRequiredProps.put(HHSConstants.PROCUREMENT_ID, "1");
		aoHMWFRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_RETURNED_PAYMENT_REVIEW);
		aoHMWFRequiredProps.put(HHSConstants.SUBMITTED_BY, "city_142");
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, parentBudgetID);
		aoHMWFRequiredProps.put(HHSConstants.INVOICE_ID, invoiceId);
		aoHMWFRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, "2017");
		aoHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, Boolean.TRUE);
		aoHMWFRequiredProps.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID,"38");

		HashMap aoHMWFRequiredProps1 = loFinancialsListService.getFinancialWFProperty(moSession, aoHMWFRequiredProps);
		assertNotNull(aoHMWFRequiredProps1);
	}
	@Test
	public void testNewFYConfigErrorCheckRule() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			String loContractId = baseContractId;

			HashMap loHMError = loFinancialsListService.newFYConfigErrorCheckRule(loContractId, moSession, session);

			assertNotNull(loHMError);
			assertEquals("success", loHMError.get("errorCheck"));
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testNewFYConfigErrorCheckRuleException() throws ApplicationException
	{
		// Current date does not lies between contract start date and contract
		// end date

		FinancialsListService loFinancialsListService = new FinancialsListService();

		String loContractId = baseContractId;
		try
		{
			loFinancialsListService.newFYConfigErrorCheckRule(loContractId, null, session);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testNewFYConfigErrorCheckRuleException1() throws ApplicationException
	{
		// Current date does not lies between contract start date and contract
		// end date

		FinancialsListService loFinancialsListService = new FinancialsListService();

		String loContractId = baseContractId;
		try
		{
			loFinancialsListService.newFYConfigErrorCheckRule(null, moSession, session);
		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractRelatedWorkflow() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, agency,
				moSession);
		assertTrue(lbSuspendStatus);
	}

	@Test
	public void testSuspendContractRelatedWorkflowExp() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, agency,
				null);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractRelatedWorkflowStatusCheckNull() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = null;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testSuspendContractRelatedWorkflowStatusCheckFalse() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		// Positive Scenario -- Do not update DB in case of suspend Contract
		// for invalid user.
		Boolean aoStatusCheck = false;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow Exception.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSuspendContractRelatedWorkflowExceptionContractIdNull() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow Exception.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSuspendContractRelatedWorkflowExceptionSessionNull() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.suspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnsuspendContractRelatedWorkflowStatusCheck() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(session, lsContractId,
				agency, moSession);
		assertTrue(lbSuspendStatus);
	}

	@Test
	public void testUnsuspendContractRelatedWorkflowStatusCheckNullExp() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(null, lsContractId, agency,
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnsuspendContractRelatedWorkflowStatusCheckFalse() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		aoStatusCheck = false;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnsuspendContractRelatedWorkflow() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow.
	 * @throws ApplicationException
	 */
	@Test
	public void testUnsuspendContractRelatedWorkflowSessionNull() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	/**
	 * This method Check Suspend Contract Related Workflow Exception.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUnsuspendContractRelatedWorkflowExceptionContractIdNull() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		String lsContractId = null;
		Boolean aoStatusCheck = true;
		Boolean lbSuspendStatus = loFinancialsListService.unsuspendContractRelatedWorkflow(session, lsContractId, "",
				moSession);
		assertTrue(lbSuspendStatus);
	}

	@Test
	public void testUpdateContractAmendmentStatus() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();
		loContractBean.setContractStatusId("78");
		loContractBean.setModifyBy("agency_47");
		loContractBean.setContractId(baseContractId);
		loContractBean.setContractTypeId("1");

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(moSession, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateContractAmendmentStatusException() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();
		loContractBean.setContractStatusId("78");
		loContractBean.setModifyBy("agency_47");
		loContractBean.setContractId(baseContractId);
		loContractBean.setContractTypeId("1");

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(null, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateContractAmendmentStatusException5() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();
		loContractBean.setContractStatusId("78B");
		loContractBean.setModifyBy("agency_47");
		loContractBean.setContractId(baseContractId);
		loContractBean.setContractTypeId("1");

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(moSession, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateContractAmendmentStatusException6() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();
		loContractBean.setContractStatusId(null);
		loContractBean.setModifyBy("agency_47");
		loContractBean.setContractId(baseContractId);
		loContractBean.setContractTypeId("1");

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(moSession, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateContractAmendmentStatusException7() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(moSession, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testUpdateContractAmendmentStatus1() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		ContractList loContractBean = new ContractList();
		loContractBean.setContractStatusId("78");
		loContractBean.setModifyBy("agency_47");
		loContractBean.setContractId("1234");
		loContractBean.setContractTypeId("1");

		try
		{
			loFinancialsListService.updateContractAmendmentStatus(moSession, loContractBean);

		}
		catch (ApplicationException loAppEx)
		{
			boolean lbThrown = true;
			assertEquals("Exception occured IN updateContractAmendmentStatus  ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSelectContractAmendmentId1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();

			loContractBean.setContractId(baseContractId);
			loContractBean.setContractTypeId(HHSConstants.THREE);

			loContractBean.setModifyBy("city_142");
			Map loContractMap = loFinancialsListService.selectContractAmendmentId(moSession, loContractBean);
			assertNotNull(loContractMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSelectContractAmendmentId2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();

			loContractBean.setContractId("");
			loContractBean.setContractTypeId(HHSConstants.THREE);

			loContractBean.setModifyBy("city_142");
			Map loContractMap = loFinancialsListService.selectContractAmendmentId(moSession, loContractBean);
			assertNull(loContractMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSelectContractAmendmentId3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();

			loContractBean.setContractId("");
			loContractBean.setContractTypeId("");

			loContractBean.setModifyBy("city_142");
			Map loContractMap = loFinancialsListService.selectContractAmendmentId(moSession, loContractBean);
			assertNull(loContractMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSelectContractAmendmentId4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();

			loContractBean.setContractId(null);
			loContractBean.setContractTypeId("");

			loContractBean.setModifyBy("city_142");
			Map loContractMap = loFinancialsListService.selectContractAmendmentId(moSession, loContractBean);
			assertNull(loContractMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSelectContractAmendmentId5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			ContractList loContractBean = new ContractList();

			loContractBean.setContractId("");
			loContractBean.setContractTypeId("");

			loContractBean.setModifyBy("city_142");
			Map loContractMap = loFinancialsListService.selectContractAmendmentId(null, loContractBean);
			assertNull(loContractMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended1() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
		assertTrue(lbSuspendStatus);
	}

	@Test
	public void testCheckStatusIdForUnSuspended1Exp() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, null);
		assertTrue(lbSuspendStatus);
	}

	@Test
	public void testCheckStatusIdForUnSuspended2() throws ApplicationException
	{
		// Positive Scenario
		String lsContractId = baseContractId;
		Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
		assertFalse(lbSuspendStatus);
	}

	@Test
	public void testCheckStatusIdForUnSuspended3() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = "111777B";
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended4() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = "";
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(null, moSession);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended5() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = baseContractId;
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended6() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = baseContractId;
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, null);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended7() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = null;
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testCheckStatusIdForUnSuspended8() throws ApplicationException
	{
		boolean lbThrown = false;
		// NEgative Scenario
		String lsContractId = baseContractId;
		try
		{
			Boolean lbSuspendStatus = loFinancialsListService.checkStatusIdForUnSuspended(lsContractId, moSession);
			assertTrue(lbSuspendStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testfetchBaseAmendmentContractDetails() throws ApplicationException
	{
		loFinancialsListService.fetchBaseAmendmentContractDetails(null, null);
	}

	@Test
	public void testfetchBaseAmendmentContractDetails1() throws ApplicationException
	{
		loFinancialsListService.fetchBaseAmendmentContractDetails(moSession, baseContractId);
	}

	@Test(expected = ApplicationException.class)
	public void testterminateCancelContractWorkFlows() throws ApplicationException
	{
		loFinancialsListService.terminateCancelContractWorkFlows(null, null, null);
	}

	@Test
	public void testterminateCancelContractWorkFlows1() throws ApplicationException
	{
		loFinancialsListService.terminateCancelContractWorkFlows(session, false, baseContractId);
	}

	@Test
	public void testterminateCancelContractWorkFlows2() throws ApplicationException
	{
		loFinancialsListService.terminateCancelContractWorkFlows(session, true, baseContractId);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck3() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck4() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck5() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck6() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck7() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testcancellingNegativeAmendmentCheck8() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test(expected = ApplicationException.class)
	public void testcancellingNegativeAmendmentCheckExp() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheck(moSession, loHashMap, session, 1);
	}

	@Test
	public void testisNegativeAmendment() throws ApplicationException
	{
		HashMap loMap = new HashMap();
		loMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, baseContractId);
		loFinancialsListService.isNegativeAmendment(moSession, loMap);
	}

	@Test
	public void testisNegativeAmendmentExp() throws ApplicationException
	{
		HashMap loMap = new HashMap();
		loMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, baseContractId);
		loFinancialsListService.isNegativeAmendment(null, loMap);
		
	}

	@Test
	public void testcancellingNegativeAmendmentCheckSecond() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, "3469");
		loFinancialsListService.cancellingNegativeAmendmentCheckSecond(moSession, loHashMap, true, 1);
	}

	@Test(expected = ApplicationException.class)
	public void testcancellingNegativeAmendmentCheckSecondExp2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheckSecond(moSession, loHashMap, false, 1);
	}

	@Test(expected = ApplicationException.class)
	public void testcancellingNegativeAmendmentCheckSecondExp3() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheckSecond(moSession, loHashMap, false, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testcancellingNegativeAmendmentCheckSecondExp4() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_AMENDMENT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheckSecond(moSession, loHashMap, true, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testcancellingNegativeAmendmentCheckSecondExp() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CLC_CONTRACT_ID_UNDERSCORE, amendContractId);
		loFinancialsListService.cancellingNegativeAmendmentCheckSecond(moSession, loHashMap, true, 1);
	}

	@Test
	public void testfetchCountractBudgetCountForFY() throws ApplicationException
	{
		Integer loCount = loFinancialsListService.fetchCountractBudgetCountForFY(moSession, baseContractId, fiscalYear);
		assertTrue(loCount > 1);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchCountractBudgetCountForFYExp() throws ApplicationException
	{
		loFinancialsListService.fetchCountractBudgetCountForFY(null, baseContractId, fiscalYear);
	}

	@Test
	public void testrevertContractToBaseValue() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put("submittedBy", agency);
		loFinancialsListService.revertContractToBaseValue(moSession, loHashMap, true);
	}

	@Test(expected = ApplicationException.class)
	public void testrevertContractToBaseValueExp2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, invalidContractId);
		loHashMap.put("submittedBy", agency);
		loFinancialsListService.revertContractToBaseValue(moSession, loHashMap, true);
	}

	@Test(expected = ApplicationException.class)
	public void testrevertContractToBaseValueExp() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, baseContractId);
		loHashMap.put("submittedBy", agency);
		loFinancialsListService.revertContractToBaseValue(null, loHashMap, true);
	}

	@Test
	public void testfetchBaseAmendmentContractDetailsAmendmentList() throws ApplicationException
	{
		List<ContractList> loContractList = loFinancialsListService.fetchBaseAmendmentContractDetailsAmendmentList(
				moSession, baseContractId, amendContractId);
		assertTrue(loContractList.size() > 0);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchBaseAmendmentContractDetailsAmendmentListExp() throws ApplicationException
	{
		List<ContractList> loContractList = loFinancialsListService.fetchBaseAmendmentContractDetailsAmendmentList(
				null, baseContractId, amendContractId);
	}

	@Test
	public void testfetchBaseAwardEpin() throws ApplicationException
	{
		String lsBaseAwardEpin = loFinancialsListService.fetchBaseAwardEpin(moSession, baseContractId);
		assertTrue(lsBaseAwardEpin != null);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchBaseAwardEpinExp() throws ApplicationException
	{
		String lsBaseAwardEpin = loFinancialsListService.fetchBaseAwardEpin(null, baseContractId);
	}

	@Test
	public void testrenewContractDateValidation() throws ApplicationException
	{
		Boolean lbSuccessStatus = loFinancialsListService.renewContractDateValidation(moSession, setEpinDetailBean());
		assertTrue(lbSuccessStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testrenewContractDateValidationExp() throws ApplicationException
	{
		Boolean lbSuccessStatus = loFinancialsListService.renewContractDateValidation(null, setEpinDetailBean());
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchContractListSummary0Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchContractListSummary(null, setContractListFilterParams(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchAgencyNames1Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchAgencyNames(null, "agency_org");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetContractsCount2Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getContractsCount(null, setContractListFilterParams(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetContractsValue3Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getContractsValue(null, setContractListFilterParams(), "provider_org");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceselectContractAmendmentId4Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.selectContractAmendmentId(null, setContractListFilterParams());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateContractAmendmentStatus5Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateContractAmendmentStatus(null, setContractListFilterParams());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateAmenBudgetStatus6Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateAmenBudgetStatus(null, setContractListFilterParams());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefindContractDetailsByEPIN7Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.findContractDetailsByEPIN(setEpinDetailBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefindContractDetailsByEPINforNew8Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.findContractDetailsByEPINforNew(setEpinDetailBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceaddNewContractDetails9Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.addNewContractDetails(null, setEpinDetailBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateCloseContract10Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateCloseContract(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecloseContract11Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.closeContract(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancelContract12Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancelContract(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateRenewContractDetails13Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateRenewContractDetails(validEpin, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateProvider14Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateProvider(vendorFmsId, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerenewContractDetails15Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.renewContractDetails(null, setEpinDetailBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicesuspendContract16Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.suspendContract(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateConfigurationErrorCheckRule17Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateConfigurationErrorCheckRule(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetFinancialWFProperty18Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getFinancialWFProperty(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetNextSeqFromTable19Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getNextSeqFromTable(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceunSuspendContract20Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.unSuspendContract(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicesuspendContractRelatedWorkflow21Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.suspendContractRelatedWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceunsuspendContractRelatedWorkflow22Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.unsuspendContractRelatedWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecheckStatusIdForSuspended23Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.checkStatusIdForSuspended(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecheckStatusIdForUnSuspended24Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.checkStatusIdForUnSuspended(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerenewalRecordExist25Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.renewalRecordExist(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateAmendContract26Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateAmendContract(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancellingNegativeAmendmentCheck27Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancellingNegativeAmendmentCheck(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceisNegativeAmendment28Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.isNegativeAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancellingNegativeAmendmentCheckSecond29Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancellingNegativeAmendmentCheckSecond(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceamendContractDetails31Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.amendContractDetails(null, setEpinDetailBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancelContractErrorCheckRule32Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancelContractErrorCheckRule(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceterminateCancelContractWorkFlows33Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.terminateCancelContractWorkFlows(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicenewFYConfigErrorCheckRule34Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.newFYConfigErrorCheckRule(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchBaseAmendmentContractDetails35Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchBaseAmendmentContractDetails(null, baseContractId);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchBaseAmendmentContractDetailsAmendmentList36Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchBaseAmendmentContractDetailsAmendmentList(null, baseContractId, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchBaseAwardEpin37Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchBaseAwardEpin(null, baseContractId);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerenewContractDateValidation38Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.renewContractDateValidation(null, setEpinDetailBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchCountractBudgetCountForFY39Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchCountractBudgetCountForFY(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerevertContractToBaseValue40Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.revertContractToBaseValue(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	//Added for Release 6 Apt interface
	@Test
	public void testValidateEpinIsUnique() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("03412L0314001");
		loContractDetail.setAgencyId("ACS");
			Boolean lbResult = loFinancialsListService.validateEpinIsUnique(loContractDetail, moSession);
			assertNotNull(lbResult);
	}
	@Test(expected = ApplicationException.class)
	public void testValidateEpinIsUniqueForAppExp() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("03412L0314001");
		loContractDetail.setAgencyId("ACS");
        Boolean lbResult = loFinancialsListService.validateEpinIsUnique(loContractDetail, null);
			assertNotNull(lbResult);
	}
	
	@Test(expected = Exception.class)
	public void testValidateEpinIsUniqueForAppExp2() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("");
		loContractDetail.setAgencyId("");
			Boolean lbResult = loFinancialsListService.validateEpinIsUnique(loContractDetail, null);
			assertNotNull(lbResult);
	}
	@Test(expected = Exception.class)
	public void testValidateEpinIsUniqueForAppExp3() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("");
		loContractDetail.setAgencyId("");
			Boolean lbResult = loFinancialsListService.validateEpinIsUnique(null, null);
			assertNotNull(lbResult);
	}
	@Test
	public void testFetchContractDetailsForUpdate() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("01110I0006772A001");
		loContractDetail.setAgencyDiv("ACS");
		loFinancialsListService.fetchContractDetailsForUpdate(loContractDetail, moSession);
	}
	@Test
	public void testFetchContractDetailsForUpdatePositive() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("01110I0006772A001");
		loContractDetail.setAgencyDiv("ACS");
		loContractDetail.setContractId("11501");
		loContractDetail.setAgencyId("ACS");
		loFinancialsListService.fetchContractDetailsForUpdate(loContractDetail, moSession);
	}
	@Test
	public void testFetchContractDetailsForUpdatePositive2() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("01110I0006772A001");
		loContractDetail.setAgencyDiv("ACS");
		loContractDetail.setContractId("11501");
		loFinancialsListService.fetchContractDetailsForUpdate(loContractDetail, moSession);
	}
	@Test
	public void testFetchContractDetailsForUpdate1() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("03412L0314001");
		loContractDetail.setAgencyDiv("ACS");
		loContractDetail.setContractId("11501");
		loFinancialsListService.fetchContractDetailsForUpdate(null, moSession);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchContractDetailsForUpdate2() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("03412L0314001");
		loContractDetail.setAgencyDiv("ACS");
		loContractDetail.setContractId("11501");
		loFinancialsListService.fetchContractDetailsForUpdate(loContractDetail, null);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchContractDetailsForUpdate3() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("03412L0314001");
		loContractDetail.setAgencyDiv("ACS");
		loContractDetail.setContractId("11501");
		loFinancialsListService.fetchContractDetailsForUpdate(null, moSession);
	}
	
	@Test
	public void fetchContractEpinTest1() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId("11501");
		loFinancialsListService.fetchContractEpin(aoContractDetails, moSession);
	}
	@Test
	public void fetchContractEpinTest2() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId("");
		loFinancialsListService.fetchContractEpin(aoContractDetails, moSession);
	}
	@Test
	public void fetchContractEpinTest3() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId(null);
		loFinancialsListService.fetchContractEpin(aoContractDetails, moSession);
	}
	@Test(expected = ApplicationException.class)
	public void fetchContractEpinTest4() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId(null);
		loFinancialsListService.fetchContractEpin(null, moSession);
	}
	@Test(expected = ApplicationException.class)
	public void fetchContractEpinTest5() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId("11501");
		loFinancialsListService.fetchContractEpin(null, moSession);
	}
	@Test(expected = ApplicationException.class)
	public void fetchContractEpinTest6() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId(null);
		loFinancialsListService.fetchContractEpin(null, moSession);
	}
	@Test(expected = ApplicationException.class)
	public void fetchContractEpinTest7() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = new EPinDetailBean();
		aoContractDetails.setContractId("11501");
		loFinancialsListService.fetchContractEpin(aoContractDetails, null);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractEpinTest8() throws ApplicationException
	{
		EPinDetailBean aoContractDetails = null;
		loFinancialsListService.fetchContractEpin(aoContractDetails, moSession);
	}
//	@Test
	public void testFindContractDetailsByEPIN() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			EPinDetailBean loContractDetail = new EPinDetailBean();

			loContractDetail.setEpinId("1237689543001-TEM");
			loContractDetail.setContractId(baseContractId);
			// positive
			loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);

			assertNull(loContractDetail);

			// exception
			loContractDetail = loFinancialsListService.findContractDetailsByEPIN(null, moSession);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFindContractDetailsByEPINValidBase() throws ApplicationException
	{
		Boolean lbThrown = false;

		FinancialsListService loFinancialsListService = new FinancialsListService();

		EPinDetailBean loContractDetail = new EPinDetailBean();

		loContractDetail.setEpinId("07112L0055001 - ACS");
		loContractDetail.setContractId(baseContractId);
		// positive
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);

	}

	@Test
	public void testFindContractDetailsByEPINValidAmend() throws ApplicationException
	{
		Boolean lbThrown = false;

		FinancialsListService loFinancialsListService = new FinancialsListService();

		EPinDetailBean loContractDetail = new EPinDetailBean();

		loContractDetail.setEpinId("81603L0007CNVA001 - DOC");
		loContractDetail.setContractId(amendContractId);
		// positive
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);

	}

	@Test(expected=ApplicationException.class)
	public void testFindContractDetailsByEPIN3() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("81603L0007CNVA001 - DOC");
		loContractDetail.setContractTypeId("2");
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, null);
//		assertNotNull(loContractDetail);
	}

	@Test
	public void testFindContractDetailsByEPIN4() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("1234567890 - DOC");
		loContractDetail.setContractId("11167");
		loContractDetail.setRefAptEpinId("200000305");
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);
//		assertNotNull(loContractDetail);
	}
	@Test
	public void testFindContractDetailsByEPIN7() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId("1234567890 - DOC");
		loContractDetail.setContractId("11167");
		loContractDetail.setContractTypeId("2");
		loContractDetail.setRefAptEpinId("200000305");
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);
//		assertNotNull(loContractDetail);
	}

	@Test
	public void testFindContractDetailsByEPIN5() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId(null);
		loContractDetail.setContractId("111333 - DOC");
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);
//		assertNotNull(loContractDetail);
	}

	@Test
	public void testFindContractDetailsByEPIN6() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId(null);
		loContractDetail.setContractId(null);
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(loContractDetail, moSession);
//		assertNotNull(loContractDetail);
	}
	@Test(expected=Exception.class)
	public void testFindContractDetailsByEPIN8() throws ApplicationException
	{
		EPinDetailBean loContractDetail = new EPinDetailBean();
		loContractDetail.setEpinId(null);
		loContractDetail.setContractId(null);
		loContractDetail = loFinancialsListService.findContractDetailsByEPIN(null, moSession);
//		assertNotNull(loContractDetail);
	}

@Test(expected=Exception.class)
	public void testFindContractDetailsByEPINforNew() throws ApplicationException
	{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			EPinDetailBean loContractDetail = new EPinDetailBean();
			loContractDetail.setEpinId("03412L0314001 - DOC");
			loContractDetail.setAgencyDiv("ACS");
			// positive
			loContractDetail = loFinancialsListService.findContractDetailsByEPINforNew(loContractDetail, moSession);
			assertNotNull(loContractDetail);
	
	}
	@Test(expected=Exception.class)
	public void testFindContractDetailsByEPINforNew2() throws ApplicationException
	{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			EPinDetailBean loContractDetail = new EPinDetailBean();
			loContractDetail.setEpinId("03412L0314001 - DOC");
			loContractDetail.setAgencyDiv("ACS");
			// positive
			loContractDetail = loFinancialsListService.findContractDetailsByEPINforNew(loContractDetail, moSession);
			assertNotNull(loContractDetail);
	
	}
	@Test(expected=Exception.class)
	public void testFindContractDetailsByEPINforNew3() throws ApplicationException
	{
		// NEGATIVE
			FinancialsListService loFinancialsListService = new FinancialsListService();
			EPinDetailBean loContractDetail = new EPinDetailBean();
			loContractDetail.setEpinId("03412L0314001 - DOC");
			loContractDetail.setEpinId("VBV");
			loContractDetail = loFinancialsListService.findContractDetailsByEPINforNew(loContractDetail, moSession);
	}

	@Test
	public void testFindContractDetailsByEPINforNew4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		// NEGATIVE
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			EPinDetailBean loContractDetail = new EPinDetailBean();
			loContractDetail.setEpinId(null);
			loContractDetail = loFinancialsListService.findContractDetailsByEPINforNew(loContractDetail, moSession);

			assertNull(loContractDetail);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test(expected = ApplicationException.class)
	public void testFindContractDetailsByEPINforNew5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		// NEGATIVE
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			EPinDetailBean loContractDetail = new EPinDetailBean();
			loContractDetail.setEpinId("1237689543001 - DOC");
			loContractDetail = loFinancialsListService.findContractDetailsByEPINforNew(loContractDetail, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
		}

	}
	
	
	//Release 6 Apt Interface changes end
}
