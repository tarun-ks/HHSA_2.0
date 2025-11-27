package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.batch.bulkupload.BulkUploadBatch;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.daomanager.service.FinancialsInvoiceListService;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.daomanager.service.PaymentListService;
import com.nyc.hhs.daomanager.service.PaymentModuleService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlersTest.AdvancePaymentReviewTaskHandlerTest;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class Release340Test
{
	private static SqlSession moSession = null; // SQL Session
	
	private InvoiceList moInvoiceList;
	
	@Before
	public void setUp() throws Exception
	{
		moInvoiceList = new InvoiceList();
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setDateSubmittedTo("dateSubmittedTo");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setAgency("accenture");
		moInvoiceList.setOrgId("accenture");

		moFinancialsInvoiceListService = new FinancialsInvoiceListService();
		moPaymentListService = new PaymentListService();
		moFinancialsService = new FinancialsService();
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		JUnitUtil.getTransactionManager();
		loadTaskAuditXml();
	}

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

	private static void loadTaskAuditXml() throws ApplicationException
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			AdvancePaymentReviewTaskHandlerTest loAdvancePaymentReviewTaskHandlerTest = new AdvancePaymentReviewTaskHandlerTest();
			Object loCacheNotificationObject3 = XMLUtil.getDomObj(loAdvancePaymentReviewTaskHandlerTest.getClass()
					.getResourceAsStream(HHSConstants.BULK_UPLOAD_TASK_AUDIT_ELEMENT_PATH));
			loCacheManager.putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, loCacheNotificationObject3);
			synchronized (BulkUploadBatch.class)
			{
				loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING,
						HHSUtil.getApplicationSettingsBulk());
			}
		}
		catch (Exception loEx)
		{
			// LOG_OBJECT.Error("Error Occured while loading Transaction Xml: loadTaskAuditXml",
			// loEx);
			throw new ApplicationException("Error Occured while loading Transaction Xml", loEx);
		}
	}

	PaymentModuleService moPaymentModuleService = new PaymentModuleService();
	String lsPaymentId = "525";
	String lsContractId = "3431";
	String lsBudgetId = "11286";
	String lsBudgetAdvanceId = "316";
	String lsInvoiceId = "583";
	String lsAssignmentId = "507";

	String lsAssignmentId2 = "648";
	String lsBudgetAdvanceId2 = "395";
	String lsBudgetId2 = "11287";

	private FinancialsInvoiceListService moFinancialsInvoiceListService;
	private PaymentListService moPaymentListService;
	private FinancialsService moFinancialsService;

	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.CITY_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.CITY_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException4() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moInvoiceList.setOrgId(null);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				null);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException2() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.AGENCY_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.AGENCY_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException3() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.PROVIDER_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testFetchInvoiceListSummaryForException5() throws ApplicationException
	{
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, null,
				ApplicationConstants.PROVIDER_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException6() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.AGENCY_ORG);
		moInvoiceList.setAgency(null);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				null);
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
	private PaymentSortAndFilter setPaymentSortAndFilterBean()
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort(HHSConstants.DISBURSEMENT_DATE);
		loPaymentSortAndFilter.setSecondSort(HHSConstants.PAYMENT_VOUCHER_NUMBER);
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("65");
		loPaymentSortAndFilter.setOrgId("accenture");
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
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList.size() != 0);

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
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchAmendmentListSummaryDbDownException() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moFinancialsService.fetchAmendmentListSummary(null, loContractList,"agency_org");
	}
	
	/**
	 * This method tests fetchAgencyAwardDocuments in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAgencyAwardDocuments() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put("procurementId", "2538");
			loParamMap.put("evaluationPoolMappingId", "2538");
			List<ExtendedDocument> loAgencyAwardList = loawardService.fetchAgencyAwardDocuments(moSession, loParamMap, "");
			assertTrue(loAgencyAwardList != null);
	}
	
	/**
	 * This method tests fetchAgencyAwardDocumentIds in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAgencyAwardDocumentIds() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			List<String> loAgencyAwardDocIdList = loawardService.fetchAgencyAwardDocumentIds(moSession, "585", "r3_org", "2538", "2538");
			assertTrue(loAgencyAwardDocIdList != null);
	}
	
	/**
	 * This method tests insertAgencyAwardDocsDetails in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAgencyAwardDocsDetails() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			HashMap<String, String> aoDocPropsMap = new HashMap<String, String>();
			aoDocPropsMap.put("documentId", "{G7EA3AEA-26CE-4F3C-843B-BF35AB0BF3BB}");
			aoDocPropsMap.put("procurementId", "2538");
			aoDocPropsMap.put("awardId", "585");
			aoDocPropsMap.put("docCategory", "Agency Document");
			aoDocPropsMap.put("docType", "Board Resolution Template");
			aoDocPropsMap.put("status", "29");
			aoDocPropsMap.put("userId", "agency_14");
			aoDocPropsMap.put("evaluationPoolMappingId", "2538");
			Boolean loInsertStatus = loawardService.insertAgencyAwardDocsDetails(moSession, aoDocPropsMap);
			assertTrue(loInsertStatus);
	}
	
	/**
	 * This method tests removeAgencyAwardDocs in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testRemoveAgencyAwardDocs() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			Map<String, String> aoDocPropsMap = new HashMap<String, String>();
			aoDocPropsMap.put("documentId", "{F8EA3AEA-26CE-4F3C-843B-BF35AB0BF3BB}");
			Boolean loRemoveStatus = loawardService.removeAgencyAwardDocs(moSession, aoDocPropsMap);
			assertTrue(loRemoveStatus);
	}
	
	/**
	 * This method tests fetchAgencyAwardDocuments in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAgencyAwardDocumentsExc() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put("procurementId", "2538");
			loParamMap.put("evaluationPoolMappingId", "2538");
			List<ExtendedDocument> loAgencyAwardList = loawardService.fetchAgencyAwardDocuments(null, loParamMap, "");
			assertTrue(loAgencyAwardList != null);
	}
	
	/**
	 * This method tests fetchAgencyAwardDocumentIds in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAgencyAwardDocumentIdsExc() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			List<String> loAgencyAwardDocIdList = loawardService.fetchAgencyAwardDocumentIds(null, "585", "r3_org", "2538", "2538");
			assertTrue(loAgencyAwardDocIdList != null);
	}
	
	/**
	 * This method tests insertAgencyAwardDocsDetails in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertAgencyAwardDocsDetailsExc() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			HashMap<String, String> aoDocPropsMap = new HashMap<String, String>();
			aoDocPropsMap.put("documentId", "{G7EA3AEA-26CE-4F3C-843B-BF35AB0BF3BB}");
			aoDocPropsMap.put("procurementId", "2538");
			aoDocPropsMap.put("awardId", "585");
			aoDocPropsMap.put("docCategory", "Agency Document");
			aoDocPropsMap.put("docType", "Board Resolution Template");
			aoDocPropsMap.put("status", "29");
			aoDocPropsMap.put("userId", "agency_14");
			aoDocPropsMap.put("evaluationPoolMappingId", "2538");
			Boolean loInsertStatus = loawardService.insertAgencyAwardDocsDetails(null, aoDocPropsMap);
			assertTrue(loInsertStatus);
	}
	
	/**
	 * This method tests removeAgencyAwardDocs in AwardService
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testRemoveAgencyAwardDocsExc() throws ApplicationException
	{
			AwardService loawardService = new AwardService();
			Map<String, String> aoDocPropsMap = new HashMap<String, String>();
			aoDocPropsMap.put("documentId", "{F8EA3AEA-26CE-4F3C-843B-BF35AB0BF3BB}");
			Boolean loRemoveStatus = loawardService.removeAgencyAwardDocs(null, aoDocPropsMap);
			assertTrue(loRemoveStatus);
	}
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchAmendmentListSummaryDbDownException2() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moFinancialsService.fetchAmendmentListSummary(null, loContractList,"city_org");
	}
	
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test
	public void testfetchAmendmentListSummary() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgType("agency_org");
		loContractList.setStartNode(1);
		loContractList.setEndNode(20);
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		List<ContractList> loAmendmentContractList = moFinancialsService.fetchAmendmentListSummary(moSession, loContractList,"agency_org");
		assertTrue(loAmendmentContractList.size()>1);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test
	public void testfetchAmendmentListSummary2() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgType("city_org");
		loContractList.setStartNode(1);
		loContractList.setEndNode(20);
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		List<ContractList> loAmendmentContractList = moFinancialsService.fetchAmendmentListSummary(moSession, loContractList,"city_org");
		assertTrue(loAmendmentContractList.size()>1);
	}
}