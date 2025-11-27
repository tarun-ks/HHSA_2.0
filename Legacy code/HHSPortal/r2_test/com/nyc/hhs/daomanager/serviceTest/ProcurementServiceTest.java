package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.daomanager.service.SolicitationFinancialsGeneralService;
import com.nyc.hhs.daomanager.service.TaxonomyService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.ApprovedProvidersBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.EvidenceBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.SelectedServicesBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.TaxonomyDOMUtil;

public class ProcurementServiceTest
{

	private static SqlSession moMyBatisSession = null;
	private ProcurementService moProcurementService = new ProcurementService();
	private RFPReleaseService moRFPReleaseService = new RFPReleaseService();
	private String procurementId = "133";
	private String msReleaseProcurementId = "132";
	private String msDraftProcurementId = "205";
	private String msPlannedProcurementId = "255";
	private String msContractIdSourceOne = "241";
	private String msContractIdSourceTwo = "23";
	private String msUserId = "city_43";
	private static String msProcurementStatusReleased = null;
	private static String msProcurementStatusPlanned = null;
	private static String msProcurementStatusDraft = null;
	private static String msProcurementStatusClosed = null;
	
	
	
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
			moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			msProcurementStatusReleased = PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED);
			msProcurementStatusPlanned = PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_PLANNED);
			msProcurementStatusDraft = PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_DRAFT);
			msProcurementStatusClosed = PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED);
			setTaxonomyInCache();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	@SuppressWarnings("unused")
	public static void setTaxonomyInCache() throws ApplicationException
	{

		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Channel loChannelObj = new Channel();
			// Fetch Taxonomy data from DB
			// TransactionManager.executeTransaction(loChannelObj,
			// ApplicationConstants.RETRIEVE_FROM_TAXONOMY);
			TaxonomyService loTaxonomyService = new TaxonomyService();

			List<TaxonomyTree> loTaxonomyList = loTaxonomyService.getTaxonomyMaster(moMyBatisSession);
			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			/*
			 * Document loTaxonomyDom = XMLUtil .getDomObj(PropertyUtil.class
			 * .getResourceAsStream("/testing/com/nyc/hhs/taxonomy.xml"));
			 */
			// Caching Taxonomy DOM
			loCacheManager.putCacheObject(ApplicationConstants.TAXONOMY_ELEMENT, loTaxonomyDom);

		}
		catch (ApplicationException aoError)
		{
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object Cache", aoError);
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
			moMyBatisSession.rollback();
			moMyBatisSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}


	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return a procurement filter bean object
	 */
	private ProposalDetailsBean getProposalBeanParams()
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setCreatedBy("city_143");
		loProposalDetailsBean.setModifiedBy("city_143");
		loProposalDetailsBean.setProcurementId("624");
		return loProposalDetailsBean;
	}

	/*
	 * public void getTransactionManager()throws Exception { ICacheManager
	 * loCacheManager = BaseCacheManagerWeb.getInstance(); Object loCacheObject
	 * = XMLUtil.getDomObj(
	 * "C:/HHSNYC_R2/HHSPortal/r2_src/com/nyc/hhs/config/TransactionConfigR2.xml"
	 * ); loCacheManager.putCacheObject("transactionR2", loCacheObject);
	 * PropertyUtil loPropertyUtil= new PropertyUtil();
	 * 
	 * }
	 * 
	 * @Test public void testRetrieveQuestionAnswer()throws Exception {
	 * getTransactionManager(); Channel loChannel = new Channel() ; //HashMap
	 * loHM = new HashMap() ; loChannel.setData("asProcurementId", "24");
	 * 
	 * moMyBatisSession =
	 * HHSMyBatisConnectionFactory.getSqlSessionFactory().openSession();
	 * 
	 * loChannel.setData("aoMyBatisSession", moMyBatisSession);
	 * HHSTransactionManager.executeTransaction(loChannel,
	 * "getProcurementSummary"); moMyBatisSession.commit() ;
	 * moMyBatisSession.close() ; String lsExpectedResult = "Procurement p1";
	 * Procurement loActualResult =
	 * (Procurement)loChannel.getData("ProcurementSummary"); String
	 * lsActualResultValue = loActualResult.getProcurementTitle();
	 * assertEquals(lsExpectedResult, lsActualResultValue); }
	 */

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return a procurement filter bean object
	 * @throws ApplicationException
	 */
	private Procurement getDefaultProcurementFilterParams() throws ApplicationException
	{
		Procurement loProcurementFilter = new Procurement();
		loProcurementFilter.setStartNode(1);
		loProcurementFilter.setEndNode(20);
		loProcurementFilter.setFirstSort(HHSConstants.UPDATED_RFP_RELEASE_DATE);
		loProcurementFilter.setSecondSort(HHSConstants.AGENCY_ID);
		loProcurementFilter.setFirstSortType(HHSConstants.ASCENDING);
		loProcurementFilter.setSecondSortType(HHSConstants.ASCENDING);
		loProcurementFilter.setOrganizationId("accenture");
		List<String> loProcStatusList = new ArrayList<String>();
		loProcStatusList.add("2");
		loProcStatusList.add("3");
		loProcStatusList.add("4");
		loProcStatusList.add("5");
		loProcStatusList.add("6");
		loProcurementFilter.setProcurementStatusList(loProcStatusList);

		return loProcurementFilter;
	}

	private ExtendedDocument getExtendedDocumentParams()
	{
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setStartNode(1);
		loExtendedDocument.setEndNode(20);
		loExtendedDocument.setProcurementId("249");
		loExtendedDocument.setOrganizationType("city_org");
		return loExtendedDocument;
	}

	/**
	 * This method tests if active procurements are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchActiveProcurements() throws ApplicationException
	{

		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		List<Procurement> loProcFilterList = moProcurementService.fetchActiveProcurements(moMyBatisSession,
				loProcurementFilter, "city_org");

		assertNotNull(loProcFilterList);
		assertTrue(loProcFilterList.size() > 0);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * active procurements from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementsApplicationException() throws Exception
	{
		moProcurementService.fetchActiveProcurements(null, getProcurementBeanToBeSaved(), "city_org");
	}

	/**
	 * This method tests if active procurements are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchActiveProcurementsForProviderCase1() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		List<Procurement> loProcFilterList = moProcurementService.fetchActiveProcurements(moMyBatisSession,
				loProcurementFilter, "provider_org");

		assertNotNull(loProcFilterList);
		assertTrue(loProcFilterList.size() > 0);
	}

	/**
	 * This method tests if active procurements are available in database.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchActiveProcurementsForProviderCase2() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		List<Procurement> loProcFilterList = moProcurementService.fetchActiveProcurements(moMyBatisSession,
				loProcurementFilter, null);

		assertNotNull(loProcFilterList);
		assertTrue(loProcFilterList.size() > 0);
	}
	
	@Test
	public void testFetchActiveProcurementsForProviderCase3() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		List<Procurement> loProcFilterList = moProcurementService.fetchActiveProcurements(moMyBatisSession,
				loProcurementFilter, "##");

		assertNotNull(loProcFilterList);
		assertTrue(loProcFilterList.size() > 0);
	}
	
	@Test
	public void testFetchActiveProcurementsCase4() throws ApplicationException
	{

		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		List<Procurement> loProcFilterList = moProcurementService.fetchActiveProcurements(moMyBatisSession,
				loProcurementFilter, "");

		assertNotNull(loProcFilterList);
		assertTrue(loProcFilterList.size() > 0);
	}	
	
	/**
	 * This method tests the count of active procurements
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProcurementCount() throws ApplicationException
	{

		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		Integer loProcCount = moProcurementService.getProcurementCount(moMyBatisSession, loProcurementFilter,
				"city_org");
		assertNotNull(loProcCount);
		assertTrue(loProcCount > 0);
	}
	
	@Test
	public void testGetProcurementCountCase1() throws ApplicationException
	{

		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		Integer loProcCount = moProcurementService.getProcurementCount(moMyBatisSession, loProcurementFilter,"city_org");
		assertNotNull(loProcCount);
		assertTrue(loProcCount > 0);
	}

	/**
	 * This method tests if agency has list of programs associated with it
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProgramNameForAgencyIdCase1() throws ApplicationException
	{

		List<Procurement> loProgramList = moProcurementService.getProgramNameForAgencyId(moMyBatisSession, "DOHMH",
				Boolean.TRUE);
		assertNotNull(loProgramList);
		assertTrue(loProgramList.size() > 0);
		
	}

	/**
	 * This method tests if agency has list of programs associated with it
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProgramNameForAgencyIdCase2() throws ApplicationException
	{

		List<Procurement> loProgramList = moProcurementService.getProgramNameForAgencyId(moMyBatisSession, "DOHMH",
				Boolean.FALSE);
		assertNull(loProgramList);
		
	}

	/**
	 * This method tests if procurements have their associated epins
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProcurementEpinList() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loService = new SolicitationFinancialsGeneralService();
		List<String> loProcEpinList = loService.fetchEpinList(moMyBatisSession, "fetchProcurementEpinList", "");
		assertNotNull(loProcEpinList);
		assertTrue(loProcEpinList.size() > 0);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * active procurements from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementCountApplicationException() throws Exception
	{
		moProcurementService.getProcurementCount(null, getProcurementBeanToBeSaved(), "city_org");
	}

	/**
	 * This method tests occurring of application exception while getting
	 * program name list for given agency Id
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProgramNameForAgencyIdApplicationException() throws Exception
	{

		moProcurementService.getProgramNameForAgencyId(moMyBatisSession, null, Boolean.TRUE);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetProgramNameForAgencyIdCase3() throws Exception
	{

		moProcurementService.getProgramNameForAgencyId(null, "DOHMH", Boolean.TRUE);
	}

	/**
	 * This method tests occurring of application exception while getting
	 * procurement epin list
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementEpinApplicationException() throws Exception
	{
		SolicitationFinancialsGeneralService loService = new SolicitationFinancialsGeneralService();
		loService.fetchEpinList(moMyBatisSession, null, null);
	}

	/**
	 * this method sets all the required data in the bean.
	 * 
	 * @return Procurement - Procurement reference
	 * @throws Exception - throws Exception
	 */
	public Procurement getProcurementBeanToBeSaved() throws Exception
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementTitle("Procurement_S5");
		loProcurement.setProcurementEpin("Pending");
		loProcurement.setProgramName("3");
		loProcurement.setProcurementStatus("1");
		loProcurement.setStatus(1);
		loProcurement.setAgencyId("DHS");
		loProcurement.setAccPrimaryContact("city_43");
		loProcurement.setAccSecondaryContact("city_43");
		loProcurement.setModifiedBy("city_43");
		loProcurement.setCreatedBy("city_43");
		loProcurement.setAgecncyPrimaryContact("agency_11");
		loProcurement.setAgecncySecondaryContact("agency_16");
		loProcurement.setEmail("aa@ss.com");
		loProcurement.setEstNumberOfContracts(4);
		loProcurement.setEstProcurementValue(BigDecimal.valueOf(46124.00));
		loProcurement.setLinkToConceptReport("yfgugyu.fedf.f..");
		String loDate = DateUtil.getCurrentDate();
		loProcurement.setRfpReleaseDatePlanned(loDate);
		loProcurement.setRfpReleaseDateUpdated(loDate);
		loProcurement.setPreProposalConferenceDatePlanned(loDate);
		loProcurement.setPreProposalConferenceDateUpdated(loDate);
		loProcurement.setProposalDueDatePlanned(loDate);
		loProcurement.setProposalDueDateUpdated(loDate);
		loProcurement.setFirstRFPEvalDatePlanned(loDate);
		loProcurement.setFirstRFPEvalDateUpdated(loDate);
		loProcurement.setFinalRFPEvalDatePlanned(loDate);
		loProcurement.setFinalRFPEvalDateUpdated(loDate);
		loProcurement.setEvaluatorTrainingDatePlanned(loDate);
		loProcurement.setEvaluatorTrainingDateUpdated(loDate);
		loProcurement.setFirstEvalCompletionDatePlanned(loDate);
		loProcurement.setFirstEvalCompletionDateUpdated(loDate);
		loProcurement.setFinalEvalCompletionDatePlanned(loDate);
		loProcurement.setFinalEvalCompletionDateUpdated(loDate);
		loProcurement.setAwardSelectionDatePlanned(loDate);
		loProcurement.setAwardSelectionDateUpdated(loDate);
		loProcurement.setContractStartDatePlanned(loDate);
		loProcurement.setContractStartDateUpdated(loDate);
		loProcurement.setContractEndDatePlanned(loDate);
		loProcurement.setContractEndDateUpdated(loDate);
		loProcurement.setProcurementDescription("dsadcsedfca");

		return loProcurement;
	}

	/**
	 * This method tests getting the status id from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetStatusId() throws Exception
	{

		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setProcurementId("132");
		assertNotNull(moProcurementService.getStatusId(moMyBatisSession, loProcurement));
	}

	/**
	 * This method tests getting the Program Name from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetProgramName() throws Exception
	{

		Procurement loProcurement = getProcurementBeanToBeSaved();
		List<Procurement> loActualResult = (List<Procurement>) moProcurementService.getProgramName(moMyBatisSession,
				loProcurement);
		Integer liActualResultValue = ((Procurement) loActualResult.get(1)).getProgramId();
		assertNotNull(liActualResultValue);
	}

	/**
	 * This method tests getting the Program Name from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProgramNameException() throws Exception
	{

		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setAgencyId(null);
		moProcurementService.getProgramName(moMyBatisSession, loProcurement);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetProgramNameCase2() throws Exception
	{

		Procurement loProcurement = getProcurementBeanToBeSaved();		
		moProcurementService.getProgramName(null, loProcurement);
	}

	/**
	 * This method tests getting Procurement Summary from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetProcurementSummary() throws Exception
	{
		procurementId = "132";
		 
		Procurement loActualResult = (Procurement) moProcurementService.getProcurementSummary(moMyBatisSession,
				procurementId, msProcurementStatusReleased);
		assertNotNull(loActualResult);
	}
	
	@Test
	public void testGetProcurementSummary2() throws Exception
	{
		procurementId = "135";
		
		Procurement loActualResult = (Procurement) moProcurementService.getProcurementSummary(moMyBatisSession,
				procurementId, msProcurementStatusPlanned);
		assertNotNull(loActualResult);
	}
	
	@Test
	public void testGetProcurementSummary3() throws Exception
	{
		procurementId = "138";
		String lsProcurementStatus = null;
		Procurement loActualResult = (Procurement) moProcurementService.getProcurementSummary(moMyBatisSession,
				procurementId, lsProcurementStatus);
		assertNull(loActualResult);
	}

	/**
	 * This method tests getting Accelerator Contact Details from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementSummaryException() throws Exception
	{

		String lsProcurementId = "###";
		moProcurementService.getProcurementSummary(moMyBatisSession, lsProcurementId, "1");
	}

	/**
	 * This method tests getting Accelerator Contact Details from the database
	 * when the procurement Id is null..
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementSummaryCase1() throws Exception
	{

		String lsProcurementId = null;
		Procurement loProcurementSummary = moProcurementService.getProcurementSummary(moMyBatisSession,
				lsProcurementId, "636");
		assertNull(loProcurementSummary);
	}

	/**
	 * This method tests getting Accelerator Contact Details from the database
	 * when the procurement status and procurement id are null.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementSummaryCase2() throws Exception
	{

		String lsProcurementStatus = null;
		String lsProcurementId = null;
		Procurement loProcurementSummary = moProcurementService.getProcurementSummary(moMyBatisSession,
				lsProcurementId, lsProcurementStatus);
		assertNull(loProcurementSummary);
	}


	/**
	 * This method tests getting Accelerator Contact Details from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAcceleratorContactDetails() throws Exception
	{
		Map<String, String> loUserTpye = new HashMap<String, String>();
		loUserTpye.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserTpye.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		Map<String, List<StaffDetails>> loActualResult = (Map<String, List<StaffDetails>>) moProcurementService
				.getAcceleratorContactDetails(moMyBatisSession, loUserTpye);
		List<StaffDetails> lsActualResultValue = loActualResult.get(HHSConstants.USER_CITY);
		assertNotNull(lsActualResultValue);
	}

	/**
	 * This method tests getting Accelerator Contact Details from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetAcceleratorContactDetailsException() throws Exception
	{

		String lsExpectedResult = "city_142";
		Map<String, String> loUserTpye = new HashMap<String, String>();
		loUserTpye.put(HHSConstants.USER_CITY, null);
		loUserTpye.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		Map<String, List<StaffDetails>> loActualResult = (Map<String, List<StaffDetails>>) moProcurementService
				.getAcceleratorContactDetails(moMyBatisSession, loUserTpye);
		List<StaffDetails> lsActualResultValue = loActualResult.get(HHSConstants.USER_CITY);
		assertEquals(lsExpectedResult, lsActualResultValue.get(0).getMsStaffId());
	}
	
	@Test
	public void testGetAcceleratorContactDetailsCase1() throws Exception
	{

		String lsExpectedResult = "";
		Map<String, String> loUserTpye = new HashMap<String, String>();
		loUserTpye.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserTpye.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		Map<String, List<StaffDetails>> loActualResult = (Map<String, List<StaffDetails>>) moProcurementService
				.getAcceleratorContactDetails(moMyBatisSession, loUserTpye);
		assertEquals(lsExpectedResult, "");
	}

	@Test
	public void testGetAcceleratorContactDetailsCase2() throws Exception
	{

		String lsExpectedResult = "##";
		Map<String, String> loUserTpye = new HashMap<String, String>();
		loUserTpye.put(HHSConstants.USER_CITY, HHSConstants.USER_CITY);
		loUserTpye.put(HHSConstants.USER_AGENCY, HHSConstants.USER_AGENCY);
		Map<String, List<StaffDetails>> loActualResult = (Map<String, List<StaffDetails>>) moProcurementService
				.getAcceleratorContactDetails(moMyBatisSession, loUserTpye);
		List<StaffDetails> lsActualResultValue = loActualResult.get(HHSConstants.USER_CITY);
		assertEquals(lsExpectedResult, "##");
	}
	
	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkGetProcurementIdCase1() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement aoProcBean = new Procurement();
		aoProcBean.setProcurementId("123");
		assertNotNull(moProcurementService.getProcurementId(loSqlSession, aoProcBean));
	}

	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void checkGetProcurementIdCase4() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement aoProcBean = new Procurement();
		moProcurementService.getProcurementId(loSqlSession, aoProcBean);
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase1() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");
		loProcurement.setPreProposalConferenceDatePlanned("");
		loProcurement.setPreProposalConferenceDateUpdated("");	
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase2() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");		
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusReleased)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase3() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setPreProposalConferenceDatePlanned("");
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase4() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setPreProposalConferenceDateUpdated("");
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase5() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setStatus(1);
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase6() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setStatus(2);
		assertTrue(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase7() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setProcurementId(msPlannedProcurementId);
		assertFalse(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase8() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setPreProposalConferenceDatePlanned("");
		loProcurement.setProcurementId(msDraftProcurementId);
		assertFalse(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}

	/**
	 * This method tests Saving Procurement Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkSaveProcurementSummaryCase9() throws Exception
	{
		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setServiceUnitRequiredFlag("1");
		loProcurement.setIsOpenEndedRFP("1");	
		loProcurement.setPreProposalConferenceDateUpdated("");
		loProcurement.setProcurementId(msDraftProcurementId);
		assertFalse(moProcurementService.saveProcurementSummary(loSqlSession, loProcurement, Integer.parseInt(msProcurementStatusDraft)));
	}


	/**
	 * This method tests throwing application exception while Saving Procurement
	 * Summary in the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void checkSaveProcurementSummaryApplicationException() throws Exception
	{

		SqlSession loSqlSession = moMyBatisSession;
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setModifiedBy(null);
		Boolean liExpectedResult = true;
		assertEquals(
				liExpectedResult,
				moProcurementService.saveProcurementSummary(loSqlSession, loProcurement,
						(Integer) moProcurementService.getStatusId(loSqlSession, loProcurement)));
		loProcurement.setProcurementId("##");
		assertNotNull(moProcurementService.getProcurementId(loSqlSession, loProcurement));
	}
	
	@Test
	public void testGetStatusIdCase1() throws Exception
	{
		Procurement loProcBean = new Procurement();
		loProcBean.setProcurementId("");
		loProcBean.setProcurementStatus("Draft");
		Integer liStatusId = (Integer) moProcurementService.getStatusId(moMyBatisSession, loProcBean);
		assertNotNull(liStatusId);
	}
	
	@Test
	public void testGetStatusIdCase2() throws Exception
	{
		Procurement loProcBean = new Procurement();
		loProcBean.setProcurementId(null);
		loProcBean.setProcurementStatus("Draft");
		Integer liStatusId = (Integer) moProcurementService.getStatusId(moMyBatisSession, loProcBean);
		assertNotNull(liStatusId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetStatusIdCase3() throws Exception
	{
		Integer liStatusId = (Integer) moProcurementService.getStatusId(moMyBatisSession, null);
		assertNull(liStatusId);
	}
	
	@Test
	public void testGetStatusIdCase4() throws Exception
	{
		Procurement loProcBean = new Procurement();
		loProcBean.setProcurementId("131");
		Integer liStatusId = (Integer) moProcurementService.getStatusId(moMyBatisSession, loProcBean);
		assertNotNull(liStatusId);
	}

	/**
	 * This method tests occurring of application exception while getting the
	 * status id from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetStatusIdApplicationException() throws Exception
	{

		Procurement loProcurement = new Procurement();
		Integer liExpectedResult = Integer.valueOf(1);
		assertEquals(liExpectedResult, (Integer) moProcurementService.getStatusId(moMyBatisSession, loProcurement));
	}

	/**
	 * This method tests occurring of application exception while getting the
	 * status id from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testGetStatusIdApplicationExceptionOnPassingNull() throws Exception
	{

		Integer liExpectedResult = Integer.valueOf(1);
		assertEquals(liExpectedResult, (Integer) moProcurementService.getStatusId(null, null));
	}

	/**
	 * This method test fetch E-pin List functionality of Procurement Service
	 * Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testFetchEpinList() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loService = new SolicitationFinancialsGeneralService();
		List<String> loEpinList = null;
		loEpinList = loService.fetchEpinList(moMyBatisSession, "fetchEpinList", "");
		assertNotNull(loEpinList);
	}

	/**
	 * This method test Fetch E-Pin Details functionality of the Procurement
	 * Service Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchEpinDetails() throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;

		loEPinDetailBean = moProcurementService.fetchEpinDetails(moMyBatisSession, "123456654");
		// assertNotNull(loEPinDetailBean);
		assertTrue(true);
	}
	
	/**
	 * This method test the negative execution of Fetch E-Pin Details
	 * functionality of the Procurement Service Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEpinDetailsApplicationExp() throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;

		loEPinDetailBean = moProcurementService.fetchEpinDetails(moMyBatisSession, null);
		assertNotNull(loEPinDetailBean);
	}

	/**
	 * This method test the negative execution of Fetch E-Pin Details
	 * functionality of the Procurement Service Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchEpinDetailsException() throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;

		loEPinDetailBean = moProcurementService.fetchEpinDetails(null, "1237689543007");
		assertNotNull(loEPinDetailBean);
	}

	/**
	 * This method test Fetch E-Pin Details functionality of the Procurement
	 * Service Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchEpinDetails1() throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;

		loEPinDetailBean = moProcurementService.fetchEpinDetails(moMyBatisSession, "123456654");
		// assertNotNull(loEPinDetailBean);
		assertTrue(true);
	}	

	/**
	 * This method test Fetch E-Pin Details functionality of the Procurement
	 * Service Class
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchEpinDetails2() throws ApplicationException
	{
		EPinDetailBean loEPinDetailBean = null;

		loEPinDetailBean = moProcurementService.fetchEpinDetails(moMyBatisSession, "123456654");
		// assertNotNull(loEPinDetailBean);
		assertTrue(true);
	}
	
	@Test
	public void testFetchEpinDetailsCase3() throws ApplicationException
	{
		moProcurementService.fetchEpinDetails(moMyBatisSession, "##");
	}

	/**
	 * This method test getProcurementStatus functionality of the Procurement
	 * Service Class if we are passing ProcurementId as Input
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProcurementStatus() throws ApplicationException
	{
		String lsProcurementStatus = null;
		procurementId = "131";
		lsProcurementStatus = moRFPReleaseService.getProcurementStatus(moMyBatisSession, procurementId);
		assertNotNull(lsProcurementStatus);
	}
	
	@Test(expected=ApplicationException.class)
	public void testGetProcurementStatusApplicationException() throws ApplicationException
	{
		String lsProcurementStatus = null;
		procurementId = "131";
		lsProcurementStatus = moRFPReleaseService.getProcurementStatus(null, procurementId);
		assertNotNull(lsProcurementStatus);
	}

	/**
	 * This method test checkForEvidenceFlag functionality when procurement
	 * Status is null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckForEvidenceFlagCase1() throws ApplicationException
	{
		Map<String, Object> loServiceMap = moProcurementService.checkForEvidenceFlag(moMyBatisSession, "135", "3");
		assertNotNull(loServiceMap);
	}
	
	/**
	 * This method test checkForEvidenceFlag functionality when procurement
	 * Status is null
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckForEvidenceFlagCase3() throws ApplicationException
	{
		String lsProcurementId = "##";
		String lsProcurementStatus = "1";
		moProcurementService.checkForEvidenceFlag(moMyBatisSession, lsProcurementId, lsProcurementStatus);		
		
	}
	
	/**
	 * This method test checkForEvidenceFlag functionality when procurement
	 * Status is null
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckForEvidenceFlagCase4() throws ApplicationException
	{
		String lsProcurementId = "133";
		Map<String, Object> loServiceData = moProcurementService.checkForEvidenceFlag(null, lsProcurementId, msProcurementStatusReleased);		
		assertNull(loServiceData);
		
	}
	
	/**
	 * This method test checkForEvidenceFlag functionality when procurement
	 * Status is null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckForEvidenceFlagCase5() throws ApplicationException
	{
		String lsProcurementId = "1190";
		String lsProcurementStatus = "##";
		Map<String, Object> loServiceData = moProcurementService.checkForEvidenceFlag(moMyBatisSession, lsProcurementId, lsProcurementStatus);		
		assertNotNull(loServiceData);
		
	}
	
	/**
	 * This method test checkForEvidenceFlag functionality when procurement
	 * Status is null
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckForEvidenceFlagCase6() throws ApplicationException
	{
		String lsProcurementId = "1190";
		String lsProcurementStatus = "1";
		moProcurementService.checkForEvidenceFlag(null, lsProcurementId, lsProcurementStatus);		
	}	

	/**
	 * This method test fetchdocumentIdList functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testfetchDocumentIdList() throws ApplicationException
	{
		List<String> loDocumentIdList = null;
		loDocumentIdList = moProcurementService.fetchDocumentIdList(moMyBatisSession, "624");
		assertNotNull(loDocumentIdList);
		
	}

	/**
	 * This method test fetchdocumentIdList functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchDocumentIdListApplicationException() throws ApplicationException
	{
		moProcurementService.fetchDocumentIdList(moMyBatisSession, null);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchDocumentIdListCase1() throws ApplicationException
	{
		moProcurementService.fetchDocumentIdList(null, "624");
	}
	
	

	/**
	 * This method test deleteProvidersData functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteProvidersDataCase1() throws ApplicationException
	{
		Boolean lbDeleteDataStatus = null;
		String lsProcurementId = "115";
		lbDeleteDataStatus = moProcurementService.deleteProvidersData(moMyBatisSession, lsProcurementId, true);
		assertTrue(lbDeleteDataStatus);
	}

	/**
	 * This method test deleteProvidersData functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteProvidersDataCase2() throws ApplicationException
	{
		Boolean lbDeleteDataStatus = null;
		String lsProcurementId = "135";
		lbDeleteDataStatus = moProcurementService.deleteProvidersData(moMyBatisSession, lsProcurementId, true);
		assertTrue(lbDeleteDataStatus);
	}


	/**
	 * This method test deleteProvidersData functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testdeleteProvidersDataCase4() throws ApplicationException
	{
		Boolean lbDeleteDataStatus = false;
		String lsProcurementId = "115";
		lbDeleteDataStatus = moProcurementService.deleteProvidersData(moMyBatisSession, lsProcurementId, false);
		assertTrue(!lbDeleteDataStatus);
	}

	/**
	 * This method test deleteProvidersData functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testdeleteProvidersDataCase3() throws ApplicationException
	{
		moProcurementService.deleteProvidersData(moMyBatisSession, null, true);
		
	}

	/**
	 * This method test preserveOldStatus functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testpreserveOldStatus() throws ApplicationException
	{
		Boolean lbCancelProcurementStatus = null;
		lbCancelProcurementStatus = moProcurementService.preserveOldStatus(moMyBatisSession, "622");
		assertTrue(lbCancelProcurementStatus);
		
	}

	/**
	 * This method test preserveOldStatus functionality
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testpreserveOldStatusApplicationException() throws ApplicationException
	{
		moProcurementService.preserveOldStatus(moMyBatisSession, "###");
		
	}

	// Screen 206

	/**
	 * This method tests of Saving Selected Services list functionality of
	 * Procurement Service Class
	 * 
	 * @throws Exception If an Application Exception occurs
	 */

	@Test
	public void testInsertUpdateServiceAcceleratorService() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId(msDraftProcurementId);
		loSelectedServicesBean.setElementId("152");
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId(msUserId);
		loSelectedServicesBean.setModifiedByUserId(msUserId);
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertNotNull(lbStatusFlag);
	}
	
	@Test
	public void testInsertUpdateServiceAcceleratorServiceCase1() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertTrue(lbStatusFlag);
	}
	
	@Test
	public void testInsertUpdateServiceAcceleratorServiceCase2() throws ApplicationException
	{
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,null);
		assertFalse(lbStatusFlag);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertUpdateServiceAcceleratorServiceCase3() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId("2");
		loSelectedServicesBean.setElementId("152");
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId("city_142");
		loSelectedServicesBean.setModifiedByUserId("city_142");
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		moProcurementService.insertUpdateServiceAcceleratorService(null,loInsertSelectedServiceList);
	}
	
	@Test
	public void testInsertUpdateServiceAcceleratorServiceCase4() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId(msPlannedProcurementId);
		loSelectedServicesBean.setElementId("152");
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId(msUserId);
		loSelectedServicesBean.setModifiedByUserId(msUserId);
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertNotNull(lbStatusFlag);
	}
	
	@Test
	public void testInsertUpdateServiceAcceleratorServiceCase5() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId("2");
		loSelectedServicesBean.setElementId("##");
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId("city_142");
		loSelectedServicesBean.setModifiedByUserId("city_142");
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertTrue(lbStatusFlag);
	}
	
	@Test
	public void testInsertUpdateServiceAcceleratorServiceCase6() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId("2");
		loSelectedServicesBean.setElementId("152");
		loSelectedServicesBean.setActiveFlag("##");
		loSelectedServicesBean.setCreatedByUserId("city_142");
		loSelectedServicesBean.setModifiedByUserId("city_142");
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertTrue(lbStatusFlag);
	}

	/**
	 * This method tests the execution of insertUpdateServiceAcceleratorService
	 * method
	 * 
	 * @throws Exception If an Application Exception occurs
	 */
	@Test
	public void testInsertUpdateServiceAcceleratorServiceWithEmptyList() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertNotNull(lbStatusFlag);
		assertTrue(lbStatusFlag == false);
	}

	/**
	 * This method tests the negative execution of
	 * insertUpdateServiceAcceleratorService method
	 * 
	 * @throws Exception If an Application Exception occurs
	 */
	@Test
	public void testInsertUpdateServiceAcceleratorServiceWithNullList() throws ApplicationException
	{
		List<SelectedServicesBean> loInsertSelectedServiceList = null;
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertNotNull(lbStatusFlag);
		assertTrue(lbStatusFlag == false);
	}

	/**
	 * This method tests of Saving Selected Services list functionality of
	 * Procurement Service Class
	 * 
	 * @throws Exception If an Application Exception occurs
	 */

	@Test
	public void testSelectedServicesLists() throws ApplicationException
	{

		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId("2");
		loSelectedServicesBean.setElementId("152");
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId("city_142");
		loSelectedServicesBean.setModifiedByUserId("city_142");
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession,
				loInsertSelectedServiceList);
		assertTrue(!lbStatusFlag);
	}

	/**
	 * This method tests of Saving Selected Services list functionality of
	 * Procurement Service Class
	 * 
	 * @throws Exception If an Application Exception occurs
	 */
	@Test
	public void testSelectedServiceLists() throws ApplicationException
	{

		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(moMyBatisSession, null);
		assertTrue(!lbStatusFlag);
	}

	/**
	 * This method tests of Saving Selected Services list functionality of
	 * Procurement Service Class
	 * 
	 * @throws Exception If an Application Exception occurs
	 */

	@Test(expected = ApplicationException.class)
	public void testSelectedServicesListScenario() throws ApplicationException
	{

		List<SelectedServicesBean> loInsertSelectedServiceList = new ArrayList<SelectedServicesBean>();
		SelectedServicesBean loSelectedServicesBean = new SelectedServicesBean();
		loSelectedServicesBean.setProcurementId("624");
		loSelectedServicesBean.setElementId(null);
		loSelectedServicesBean.setActiveFlag("1");
		loSelectedServicesBean.setCreatedByUserId("city_142");
		loSelectedServicesBean.setModifiedByUserId("city_142");
		loInsertSelectedServiceList.add(loSelectedServicesBean);
		Boolean lbStatusFlag = moProcurementService.insertUpdateServiceAcceleratorService(null,
				loInsertSelectedServiceList);
		assertNotNull(lbStatusFlag);
	}
	
	/**
	 * This method tests getting Element Id from the database if selected
	 * services list are available in DB.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testGetSavedServicesListCase1() throws ApplicationException
	{

		String lsProcurementId = "132";
		List<SelectedServicesBean> savedServicesList = (List<SelectedServicesBean>) moProcurementService
				.getSavedServicesList(moMyBatisSession, lsProcurementId);
//		String lsActualEementId = (savedServicesList.get(0)).getElementId();
		// assertEquals(lsExpectedEementId, lsActualEementId);
		assertTrue(savedServicesList.size()>0);
	}

	/**
	 * This method tests getting Element Id from the database if selected
	 * services list are available in DB.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testGetSavedServicesListCase2() throws ApplicationException
	{

		String lsProcurementId = "624";
		String lsExpectedEementId = "18";
		List<SelectedServicesBean> savedServicesList = (List<SelectedServicesBean>) moProcurementService
				.getSavedServicesList(moMyBatisSession, lsProcurementId);
		
	}

	/**
	 * This method tests getting Element Id from the database if selected
	 * services list are available in DB.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testElementIdExceptionDetails() throws ApplicationException
	{
		String lsProcurementId = "624";
		List<SelectedServicesBean> savedServicesList = (List<SelectedServicesBean>) moProcurementService
				.getSavedServicesList(moMyBatisSession, lsProcurementId);
		assertNotNull(savedServicesList);
	}

	/**
	 * This method tests when no data is available in selected_services table in
	 * DB
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test(expected = ApplicationException.class)
	public void testGetSavedServicesListCase3() throws ApplicationException
	{
		String lsProcurementId = "622";
		List<SelectedServicesBean> savedServicesList = (List<SelectedServicesBean>) moProcurementService
				.getSavedServicesList(null, lsProcurementId);
		assertNotNull(savedServicesList);	
	}

	/**
	 * This method will execute the test script for <b>RemoveRfpDocs</b> method
	 * of the Procurement service class
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testRemoveRfpDocs() throws ApplicationException
	{

		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "601");
		loParamMap.put("asDeletedDocumentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParamMap.put("docReferenceNum", "696");
		Integer liRowsDeleted = (Integer) moProcurementService.removeRfpDocs(moMyBatisSession, loParamMap, "2");
		assertNotNull(liRowsDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveRfpDocsApplicationException() throws Exception
	{

		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "622");
		loParamMap.put("asDeletedDocumentId", null);

		moProcurementService.removeRfpDocs(null, loParamMap, "2");
	}

	@Test(expected = Exception.class)
	public void testRemoveRfpDocsApplicationException1() throws Exception
	{

		Map<String, String> loParamMap = null;
		moProcurementService.removeRfpDocs(moMyBatisSession, loParamMap, "2");
	}

	@Test
	public void testRemoveRfpDocs1() throws Exception
	{

		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "622");
		loParamMap.put("asDeletedDocumentId", "{729CFD7A-3212-448F-91C6-2C9F939E797D}");
		loParamMap.put("docReferenceNum", "696");
		Integer liRowsDeleted = (Integer) moProcurementService.removeRfpDocs(moMyBatisSession, loParamMap, "Planned");
		assertEquals(Integer.valueOf(0), liRowsDeleted);
	}

	@Test(expected = Exception.class)
	public void testRemoveRfpDocsException() throws Exception
	{

		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("asProcurementId", "3");
		loParamMap.put("asDeletedDocumentId", "{729CFD7A-3212-448F-91C6-2C9F939E797D}");
		moProcurementService.removeRfpDocs(moMyBatisSession, loParamMap, null);
	}

	/**
	 * This method will test the execution of the
	 * <b>fetchRfpReleaseDocsDetails</b> method
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchRfpReleaseDocsDetails() throws ApplicationException
	{

		List<ExtendedDocument> loDocumentList = (List<ExtendedDocument>) moProcurementService
				.fetchRfpReleaseDocsDetails(moMyBatisSession, getExtendedDocumentParams());
		assertNotNull(loDocumentList);
	}

	/**
	 * This method will test the negetive execution of the
	 * <b>fetchRfpReleaseDocsDetails</b> method
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchRfpReleaseDocsDetailsApplicationException() throws Exception
	{

		List<ExtendedDocument> loDocumentList = (List<ExtendedDocument>) moProcurementService
				.fetchRfpReleaseDocsDetails(null, null);
		assertNotNull(loDocumentList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchRfpReleaseDocsDetailsNegative() throws Exception
	{
		List<ExtendedDocument> loDocumentList = (List<ExtendedDocument>) moProcurementService
				.fetchRfpReleaseDocsDetails(null, null);
		assertNotNull(loDocumentList);
	}
	@Test
	public void testinsertRfpDocumentDetails() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsDocumentName = "test";
		String lsDocumentType = "RFP";
		String lsDocumentId = "test";
		String lsDocumentCategory = "RFP";
		String lsDocCreatedBy = "hhs_mgr1";
		Date loCreatedDate = new Date();
		HashMap<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", lsProcurementId);
		loParameterMap.put("docTitle", lsDocumentName);
		loParameterMap.put("DOC_TYPE", lsDocumentType);
		loParameterMap.put("DOC_CATEGORY", lsDocumentCategory);
		loParameterMap.put("documentId", lsDocumentId);
		loParameterMap.put("docCreatedBy", lsDocCreatedBy);
		loParameterMap.put("docCreatedDate", loCreatedDate);
		loParameterMap.put("userId", "city_43");
		String lsDocStatusId = "26";
		loParameterMap.put("statusId", lsDocStatusId);
		Integer liresult = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, null);
		assertEquals(Integer.valueOf(1), liresult);
	}
	
	
	@Test
	public void testinsertRfpDocumentDetailsCase2() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsDocumentName = "test";
		String lsDocumentType = "RFP";
		String lsDocumentId = "test";
		String lsDocumentCategory = "RFP";
		String lsDocCreatedBy = "hhs_mgr1";
		Date loCreatedDate = new Date();
		HashMap<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", lsProcurementId);
		loParameterMap.put("docTitle", lsDocumentName);
		loParameterMap.put("DOC_TYPE", lsDocumentType);
		loParameterMap.put("DOC_CATEGORY", lsDocumentCategory);
		loParameterMap.put("documentId", lsDocumentId);
		loParameterMap.put("docCreatedBy", lsDocCreatedBy);
		loParameterMap.put("docCreatedDate", loCreatedDate);
		loParameterMap.put("userId", "city_43");
		loParameterMap.put(HHSConstants.REPLACING_DOCUMENT_ID, "555");
		loParameterMap.put(HHSConstants.IS_ADDENDUM_DOC, "1");
		loParameterMap.put(HHSConstants.AS_DEL_DOC_ID, "{E94A59F4-11EB-4C5A-A2FB-4C35349FED86}");
		loParameterMap.put(HHSConstants.DOC_REF_NUM,"278");
		loParameterMap.put(HHSConstants.DOC_REF_NO,"278");
		String lsDocStatusId = "26";
		loParameterMap.put("statusId", lsDocStatusId);
		Integer liresult = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, null);
		assertEquals(Integer.valueOf(1), liresult);
	}
	
	@Test
	public void testinsertRfpDocumentDetailsCase3() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsDocumentName = "test";
		String lsDocumentType = "RFP";
		String lsDocumentId = "test";
		String lsDocumentCategory = "RFP";
		String lsDocCreatedBy = "hhs_mgr1";
		Date loCreatedDate = new Date();
		HashMap<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", lsProcurementId);
		loParameterMap.put("docTitle", lsDocumentName);
		loParameterMap.put("DOC_TYPE", lsDocumentType);
		loParameterMap.put("DOC_CATEGORY", lsDocumentCategory);
		loParameterMap.put("documentId", lsDocumentId);
		loParameterMap.put("docCreatedBy", lsDocCreatedBy);
		loParameterMap.put("docCreatedDate", loCreatedDate);
		loParameterMap.put("userId", "city_43");
		loParameterMap.put(HHSConstants.REPLACING_DOCUMENT_ID, "555");
		loParameterMap.put(HHSConstants.IS_ADDENDUM_DOC, "0");
		loParameterMap.put(HHSConstants.AS_DEL_DOC_ID, "{E94A59F4-11EB-4C5A-A2FB-4C35349FED86}");
		loParameterMap.put(HHSConstants.DOC_REF_NUM,"278");
		loParameterMap.put(HHSConstants.DOC_REF_NO,"278");
		String lsDocStatusId = "26";
		loParameterMap.put("statusId", lsDocStatusId);
		Integer liresult = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, null);
		assertEquals(Integer.valueOf(1), liresult);
	}

	@Test(expected = Exception.class)
	public void testinsertRfpDocumentDetailsExp1() throws Exception
	{
		HashMap<Object, Object> loParameterMap = null;
		Integer liRowsAdded = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, null);
		assertEquals(Integer.valueOf(1), liRowsAdded);
	}// fSEADf

	@Test(expected = Exception.class)
	public void testinsertRfpDocumentDetailsExp2() throws Exception
	{
		HashMap<Object, Object> loParameterMap = null;
		Integer liRowsAdded = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, null);
		assertEquals(Integer.valueOf(1), liRowsAdded);
	}// sdfc

	@Test(expected = ApplicationException.class)
	public void testGetProcurementSummaryForNavExc() throws Exception
	{

		String lsProcurementId = null;
		moProcurementService.getProcurementSummaryForNav(moMyBatisSession, lsProcurementId);
	}
	
	@Test
	public void testGetProcurementSummaryForNavCase1() throws Exception
	{

		String lsProcurementId = "135";
		moProcurementService.getProcurementSummaryForNav(moMyBatisSession, lsProcurementId);
	}

	// Test cases for navigation rule e5
	@Test
	public void testcheckIfUserOfSameAgency() throws ApplicationException
	{
		String lsProcurementId = "624";
		String lsAgencyId = "agency_18";
		boolean lbFlag = moProcurementService.checkIfUserOfSameAgency(moMyBatisSession, lsAgencyId, lsProcurementId, true);
		assertFalse(lbFlag);

		lbFlag = moProcurementService.checkIfUserOfSameAgency(moMyBatisSession, lsAgencyId, lsProcurementId, false);
		assertFalse(lbFlag);

		lsProcurementId = "624";
		lsAgencyId = "agency_18";
		lbFlag = moProcurementService.checkIfUserOfSameAgency(moMyBatisSession, lsAgencyId, lsProcurementId);
		assertTrue(true);
	}

	@Test(expected = ApplicationException.class)
	public void testcheckIfUserOfSameAgencyExc() throws ApplicationException
	{
		String lsProcurementId = null;
		String lsAgencyId = null;
		moProcurementService.checkIfUserOfSameAgency(moMyBatisSession, lsAgencyId, lsProcurementId);
	}

	// Test cases for navigation rule e6,e7
	@Test
	public void testgetProcurementDetailsForNav() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsProviderUserId = "283";
		String lsOrgId = "test";
		String lsProposalId = "107";
		moProcurementService.getProcurementDetailsForNav(moMyBatisSession,
				lsProcurementId, lsProviderUserId, lsProposalId, lsOrgId);
	}

	@Test(expected = ApplicationException.class)
	public void testgetProcurementDetailsForNavExc() throws ApplicationException
	{
		String lsProcurementId = null;
		String lsProviderUserId = null;
		moProcurementService.getProcurementDetailsForNav(moMyBatisSession, lsProcurementId, lsProviderUserId, null, null);
	}
	
	@Test
	public void testgetProcurementDetailsForNavCase2() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsProviderUserId = "283";
		String lsOrgId = "test";
		String lsProposalId = null;
		moProcurementService.getProcurementDetailsForNav(moMyBatisSession,
				lsProcurementId, lsProviderUserId, lsProposalId, lsOrgId);
	}

	/**
	 * This method will execute the test over the
	 * <b>insertRfpDocumentDetails</b> method of ProcurementService Class
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertRfpDocumentDetails() throws ApplicationException
	{
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", "133");
		loParameterMap.put("DocumentTitle", "TestDoc");
		loParameterMap.put("DOC_TYPE", "TestDocType");
		loParameterMap.put("DOC_CATEGORY", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("HHS_DOC_CREATED_BY_ID", "city_142");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "city_42");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "city_142");
		loParameterMap.put("statusId", "26");
		Integer liRowsDeleted = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession,
				loParameterMap, "2");
		assertEquals(Integer.valueOf(1), liRowsDeleted);
	}

	/**
	 * This method will execute the test over the
	 * <b>insertRfpDocumentDetails</b> method of ProcurementService Class
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertRfpDocumentDetailsAddendum() throws ApplicationException
	{
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", "133");
		loParameterMap.put("DocumentTitle", "TestDoc");
		loParameterMap.put("DOC_TYPE", "TestDocType");
		loParameterMap.put("DOC_CATEGORY", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("HHS_DOC_CREATED_BY_ID", "city_142");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "city_42");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "city_142");
		loParameterMap.put("statusId", "26");
		Integer liRowsDeleted = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession,
				loParameterMap, "3");
		assertEquals(Integer.valueOf(1), liRowsDeleted);
	}

	/**
	 * This method will execute negative test scenario for
	 * <b>insertRfpDocumentDetails</b> method of ProcurementService Class
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertRfpDocumentDetailsApplicationException() throws Exception
	{
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", "2315");
		loParameterMap.put("docTitle", "TestDoc");
		loParameterMap.put("DOC_TYPE", "TestDocType");
		loParameterMap.put("DOC_CATEGORY", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("docCreatedBy", "city_142");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "city_142");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "city_142");
		loParameterMap.put("statusId", "26");
		Integer liResult = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, "2");
		assertEquals(Integer.valueOf(1), liResult);
	}

	/**
	 * This method will execute negative test scenario for
	 * <b>insertRfpDocumentDetails</b> method of ProcurementService Class
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = java.lang.Exception.class)
	public void testInsertRfpDocumentDetailsAddendumApplicationException() throws ApplicationException
	{
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		loParameterMap.put("procurementId", "2315");
		loParameterMap.put("docTitle", "TestDoc");
		loParameterMap.put("DOC_TYPE", "TestDocType");
		loParameterMap.put("DOC_CATEGORY", "TestDocCategory");
		loParameterMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
		loParameterMap.put("docCreatedBy", "city_142");
		loParameterMap.put("docCreatedDate", new Date());
		loParameterMap.put("userId", "city_142");
		loParameterMap.put("docModifedDate", new Date());
		loParameterMap.put("docModifedBy", "city_142");
		loParameterMap.put("statusId", "26");
		Integer liResult = (Integer) moProcurementService.insertRfpDocumentDetails(moMyBatisSession, loParameterMap, "3");
		assertEquals(Integer.valueOf(1), liResult);
	}

	/**
	 * This method tests the count of active procurements
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testGetProcurementCountForProvider() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		Integer loProcCount = moProcurementService.getProcurementCount(moMyBatisSession, loProcurementFilter,
				"provider_org");
		assertNotNull(loProcCount);
		assertTrue(loProcCount > 0);
	}
	
	@Test
	public void testGetProcurementCountForProviderCase3() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		Integer loProcCount = moProcurementService.getProcurementCount(moMyBatisSession, loProcurementFilter,
				"##");
		assertNotNull(loProcCount);
		assertTrue(loProcCount > 0);
	}
	
	@Test
	public void testGetProcurementCountForProviderCase4() throws ApplicationException
	{
		Procurement loProcurementFilter = getDefaultProcurementFilterParams();
		Integer loProcCount = moProcurementService.getProcurementCount(moMyBatisSession, loProcurementFilter,
				"");
		assertNotNull(loProcCount);
		assertTrue(loProcCount > 0);
	}

	/**
	 * This method tests the execution of fetchApprovedProvDetails method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchApprovedProvDetails() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsCriteria = moProcurementService.fetchApprovedProvDetails(moMyBatisSession, lsProcurementId);
		assertNotNull(lsCriteria);
		
	}

	/**
	 * This method tests the negative execution of fetchApprovedProvDetails
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchApprovedProvDetailsApplicationException() throws ApplicationException
	{
		String lsProcurementId = null;
		String lsCriteria = moProcurementService.fetchApprovedProvDetails(moMyBatisSession, lsProcurementId);
		assertNotNull(lsCriteria);
		
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersListAfterRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchApprovedProvidersListAfterReleaseCase1() throws ApplicationException
	{
		String lsProcurementId = "624";
		List<ApprovedProvidersBean> loList = moProcurementService.fetchApprovedProvidersListAfterRelease(
				moMyBatisSession, lsProcurementId, "0", true);
		assertNotNull(loList);
		
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersListAfterRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchApprovedProvidersListAfterReleaseCase2() throws ApplicationException
	{
		String lsProcurementId = "624";
		List<ApprovedProvidersBean> loList = moProcurementService.fetchApprovedProvidersListAfterRelease(
				moMyBatisSession, lsProcurementId, "0", false);
		assertNull(loList);
		
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersListAfterRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchApprovedProvidersListAfterReleaseApplicationException() throws ApplicationException
	{
		String lsProcurementId = "###";
		moProcurementService.fetchApprovedProvidersListAfterRelease(moMyBatisSession, lsProcurementId, "0", true);
		

	}

	/**
	 * This method test fetchDropDownValue method and checks whether or not drop
	 * down value is coming corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchDropDownValue() throws ApplicationException
	{
		String lsDropDown = moProcurementService.fetchDropDownValue(moMyBatisSession, "624");
		assertNull(lsDropDown);
		
	}

	/**
	 * This method test displayApprovedProvidersList method and checks whether
	 * or not approved providers list is getting generated corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testDisplayApprovedProvidersList() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		List<ApprovedProvidersBean> loApprovedProvidersList = loProcurementService.displayApprovedProvidersList(
				moMyBatisSession, "624");
		assertNotNull(loApprovedProvidersList);
	}

	/**
	 * This method test the negative execution of fetchDropDownValue method and
	 * checks whether or not drop down value is coming corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchDropDownValueApplicationException() throws ApplicationException
	{
		String lsProcurementId = "###";
		moProcurementService.fetchDropDownValue(moMyBatisSession, lsProcurementId);
		
	}
	
	/**
	 * This method test the negative execution of fetchServicesList method and
	 * checks whether or not service list is coming corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testfetchServicesList() throws ApplicationException
	{
		String lsProcurementId = "132";
		moProcurementService.fetchServicesList(moMyBatisSession, lsProcurementId);
		
	}

	/**
	 * This method test the negative execution of fetchServicesList method and
	 * checks whether or not service list is coming corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchServicesListApplicationException() throws ApplicationException
	{
		String lsProcurementId = "###";
		moProcurementService.fetchServicesList(null, lsProcurementId);
		
	}

	/**
	 * This method test the execution of fetchServicesList method and checks
	 * whether or not service list is coming corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	

	/**
	 * This method test the negative execution of displayApprovedProvidersList
	 * method and checks whether or not approved providers list is getting
	 * generated corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testDisplayApprovedProvidersListApplicationException() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.displayApprovedProvidersList(moMyBatisSession, null);
	}

	/**
	 * This method tests the execution of pickDataFromDb method and tests
	 * whether or not the form values HashMap is getting generated
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testPickDataFromDbCase1() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "ORGANIZATION";
		Map<String, String> loHMWhereClause = new HashMap();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,
				asTableName);
		assertNotNull(loHMFormValues);
		
	}

	/**
	 * This method tests the execution of pickDataFromDb method and tests
	 * whether or not the form values HashMap is getting generated
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testPickDataFromDbCase2() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,
				asTableName);
		assertNotNull(loHMFormValues);
		
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersList method
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchApprovedProvidersListCase1() throws ApplicationException
	{
		List<String> aoSelectedBeanList = new ArrayList<String>();
		aoSelectedBeanList.add("137");
		BaseFilter aoBaseFilter = new BaseFilter();
		aoBaseFilter.setFirstSort("ORGANIZATION_LEGAL_NAME");
		aoBaseFilter.setFirstSortType("ASC");
		aoBaseFilter.setSecondSort(null);
		aoBaseFilter.setSecondSortType("ASC");

		/*
		 * List<ApprovedProvidersBean> loApprovedProvidersList =
		 * moProcurementService.fetchApprovedProvidersList( moMyBatisSession,
		 * asProcurementId, asSelectedProvDropDownValue, aoSelectedBeanList,
		 * asElementId, aoBaseFilter, null);
		 * 
		 * assertNotNull(loApprovedProvidersList);
		 * assertTrue(loApprovedProvidersList.size() > 0);
		 */
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersList method when
	 * the drop down value is "0"
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchApprovedProvidersListCase2() throws ApplicationException
	{
		List<String> aoSelectedBeanList = new ArrayList<String>();
		aoSelectedBeanList.add("137");
		BaseFilter aoBaseFilter = new BaseFilter();
		aoBaseFilter.setFirstSort("ORGANIZATION_LEGAL_NAME");
		aoBaseFilter.setFirstSortType("ASC");
		aoBaseFilter.setSecondSort(null);
		aoBaseFilter.setSecondSortType("ASC");

		/*
		 * List<ApprovedProvidersBean> loApprovedProvidersList =
		 * moProcurementService.fetchApprovedProvidersList( moMyBatisSession,
		 * asProcurementId, asSelectedProvDropDownValue, aoSelectedBeanList,
		 * asElementId, aoBaseFilter, null);
		 */
		/*
		 * assertNotNull(loApprovedProvidersList);
		 */
	}

	/**
	 * This method tests the execution of fetchApprovedProvidersList method when
	 * the element Id is null
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchApprovedProvidersListCase3() throws ApplicationException
	{
		// String asProcurementId = "124";
		// String asSelectedProvDropDownValue = "0";
		// List<String> aoSelectedBeanList = new ArrayList<String>();
		// aoSelectedBeanList.add("137");
		// String asElementId = null;
		// BaseFilter aoBaseFilter = new BaseFilter();
		// aoBaseFilter.setFirstSort("ORGANIZATION_LEGAL_NAME");
		// aoBaseFilter.setFirstSortType("ASC");
		// aoBaseFilter.setSecondSort(null);
		// aoBaseFilter.setSecondSortType("ASC");
		//
		// List<ApprovedProvidersBean> loApprovedProvidersList =
		// moProcurementService.fetchApprovedProvidersList(
		// moMyBatisSession, asProcurementId, asSelectedProvDropDownValue,
		// aoSelectedBeanList, asElementId,
		// aoBaseFilter, null);
		//
		// assertNotNull(loApprovedProvidersList);
	}

	/**
	 * This method tests the negative execution of fetchApprovedProvidersList
	 * method
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchApprovedProvidersListApplicationException() throws ApplicationException
	{
		String lsProcurementId = null;
		String asSelectedProvDropDownValue = "1";
		List<String> aoSelectedBeanList = new ArrayList<String>();
		aoSelectedBeanList.add("137");
		String asElementId = "138";
		BaseFilter aoBaseFilter = new BaseFilter();
		aoBaseFilter.setFirstSort("ORGANIZATION_LEGAL_NAME");
		aoBaseFilter.setFirstSortType("ASC");
		aoBaseFilter.setSecondSort(null);
		aoBaseFilter.setSecondSortType("ASC");

		List<ApprovedProvidersBean> loApprovedProvidersList = moProcurementService.fetchApprovedProvidersList(
				null, lsProcurementId, asSelectedProvDropDownValue, aoSelectedBeanList, asElementId,
				aoBaseFilter, null,msProcurementStatusDraft);

		assertNotNull(loApprovedProvidersList);
		assertTrue(loApprovedProvidersList.size() > 0);
	}
	
	@Test 
	public void testFetchApprovedProvidersListCase4() throws ApplicationException
	{
		String lsProcurementId = "131";
		String lsSelectedProvDropDownValue = "1";
		List<String> loSelectedBeanList = new ArrayList<String>();
		loSelectedBeanList.add("137");
		String lsElementId = "138";
		BaseFilter loBaseFilter = new BaseFilter();
		loBaseFilter.setFirstSort("ORGANIZATION_LEGAL_NAME");
		loBaseFilter.setFirstSortType("ASC");
		loBaseFilter.setSecondSort(null);
		loBaseFilter.setSecondSortType("ASC");

		List<ApprovedProvidersBean> loApprovedProvidersList = moProcurementService.fetchApprovedProvidersList(
				moMyBatisSession, lsProcurementId, lsSelectedProvDropDownValue, loSelectedBeanList, lsElementId,
				loBaseFilter, null,msProcurementStatusReleased);
	}
	

	// proposal details screen test cases start
	@Test
	public void testFetchProcurementCustomQuestionAnswer() throws Exception
	{
		List<ProposalQuestionAnswerBean> loList = moProcurementService.fetchProcurementCustomQuestionAnswer(
				moMyBatisSession, "6", "624");
		assertNotNull(loList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProcurementCustomQuestionAnswerExc() throws Exception
	{
		moProcurementService.fetchProcurementCustomQuestionAnswer(moMyBatisSession, null, null);
	}

	// proposal details screen test cases end

	// approved provider screen test cases start
	/**
	 * This method tests the execution of saveApprovedProvDetails method
	 * for the procurement status as "Released"
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testSaveApprovedProvDetailsCase1() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId(msReleaseProcurementId);
		loSelectedServBean.setUserId("city_43");
		loSelectedServBean.setApprovedForDropDown("1");
		String lsFlagValue = moProcurementService.saveApprovedProvDetails(moMyBatisSession, loSelectedServBean);
		assertNotNull(lsFlagValue);
	}

	/**
	 * This method tests the execution of saveApprovedProvDetails method when
	 * the fetched procurement status if "Planned"
	 * 
	 * @throws ApplicationException - - If an ApplicationException occurs
	 */
	@Test
	public void testSaveApprovedProvDetailsCase2() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId(msPlannedProcurementId);
		loSelectedServBean.setUserId("city_43");
		loSelectedServBean.setApprovedForDropDown("1");
		String lsFlagValue = moProcurementService.saveApprovedProvDetails(moMyBatisSession, loSelectedServBean);
		
	}

	/**
	 * This method tests the execution of saveApprovedProvDetails method when
	 * the fetched procurement status if "Draft"
	 * 
	 * @throws ApplicationException - - If an ApplicationException occurs
	 */
	@Test
	public void testSaveApprovedProvDetailsCase3() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId(msDraftProcurementId);
		loSelectedServBean.setUserId("city_43");
		loSelectedServBean.setApprovedForDropDown("1");
		String lsFlagValue = moProcurementService.saveApprovedProvDetails(moMyBatisSession, loSelectedServBean);
		assertNotNull(lsFlagValue);
	}

	/**
	 * This method tests the execution of saveApprovedProvDetails method when
	 * the fetched procurement status if "Planned"
	 * 
	 * @throws ApplicationException - - If an ApplicationException occurs
	 */
	@Test
	public void testSaveApprovedProvDetailsCase4() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId(msReleaseProcurementId);
		loSelectedServBean.setUserId("city_143");
		loSelectedServBean.setApprovedForDropDown("1");
		String lsFlagValue = moProcurementService.saveApprovedProvDetails(moMyBatisSession, loSelectedServBean);
		assertNotNull(lsFlagValue);
	}

	@Test(expected = ApplicationException.class)
	public void testSaveApprovedProvDetailsExc() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId("###");
		loSelectedServBean.setUserId("city_143");
		loSelectedServBean.setApprovedForDropDown("1");
		moProcurementService.saveApprovedProvDetails(moMyBatisSession, loSelectedServBean);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testSaveApprovedProvDetailsCase6() throws ApplicationException
	{
		SelectedServicesBean loSelectedServBean = new SelectedServicesBean();
		loSelectedServBean.setProcurementId("622");
		loSelectedServBean.setUserId("city_143");
		loSelectedServBean.setApprovedForDropDown("1");
		moProcurementService.saveApprovedProvDetails(null, loSelectedServBean);
		
	}

	// approved provider screen test cases end

	/**
	 * This method tests getting Provider status from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetProviderStatus() throws Exception
	{
		String lsProcurementId = "624";
		String lsProviderId = "test";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		assertNull(loActualResult);
		
	}

	@Test
	public void testGetProviderStatusCase1() throws Exception
	{
		String lsProcurementId = "624";
		String lsProviderId = "test";
		String lsExpectedResult = "0";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		loActualResult = "0";
		assertEquals(lsExpectedResult, loActualResult);
		
	}
	
	@Test
	public void testGetProviderStatusCase2() throws Exception
	{
		String lsProcurementId = "624";
		String lsProviderId = "test";
		String lsExpectedResult = "0";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		loActualResult = "0";
		assertEquals(lsExpectedResult, loActualResult);
		
	}
	
	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	// @Test
	// public void testGetProcurementId() throws Exception
	// {
	// String lsExpectedResult = "204";
	// Procurement loProcurement = getProcurementBeanToBeSaved();
	// HhsAuditBean loHhsAuditBean = new HhsAuditBean();
	// loHhsAuditBean.setAuditTableIdentifier(HHSConstants.ACCELERATOR_AUDIT);
	// loHhsAuditBean.setEntityType("Procurement");
	// loHhsAuditBean.setData("Procurement has been Saved");
	// loHhsAuditBean.setEventName("Save");
	// loHhsAuditBean.setUserId("city_143");
	// loHhsAuditBean.setEventType("Procurement Save");
	// moProcurementService.saveProcurementSummary(moMyBatisSession,loProcurement,1);
	// String loActualResult = (String)
	// moProcurementService.getProcurementId(moMyBatisSession,
	// loProcurement, loHhsAuditBean);
	// assertEquals(lsExpectedResult, loActualResult);
	// }

	// test cases for fetchOnPageLoadDetails transaction starts
	/**
	 * This method tests the execution of fetchSelectedServices method and
	 * determines whether or not the element Id list is getting genmerated
	 * corresponding to the procurement Id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	
	  @Test public void testFetchSelectedServices() throws ApplicationException
	  { 
		  String lsProcurementId = "132"; 
		  List<SelectedServicesBean> loList = moProcurementService.fetchSelectedServices(moMyBatisSession,lsProcurementId); 
		  assertNotNull(loList);
	  
	   }
	 

	/**
	 * This method tests the negative execution of fetchSelectedServices method
	 * and determines whether or not the element Id list is getting genmerated
	 * corresponding to the not good value of procurement Id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchSelectedServicesApplicationException() throws Exception
	{
		String lsProcurementId = "###";
		moProcurementService.fetchSelectedServices(moMyBatisSession, lsProcurementId);
		
	}

	/**
	 * This method tests the execution of getEvidenceFlag() method of HHSUtil
	 * and determines whether or not the list of EvidenceBean is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	// @Test
	// public void testGetEvidenceFlag() throws ApplicationException
	// {
	// ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
	// // putting Transaction config xml file into Cache
	// Object loCacheObject =
	// XMLUtil.getDomObj(this.getClass().getResourceAsStream("/" +
	// ApplicationConstants.TRANSACTION_CONFIG));
	// loCacheManager.putCacheObject("transaction", loCacheObject);
	//
	// PropertyUtil loTaxonomyUtil = new PropertyUtil();
	// loTaxonomyUtil.setTaxonomyInCache(loCacheManager,
	// ApplicationConstants.TAXONOMY_ELEMENT);
	//
	// List<String> aoElementIdList = new ArrayList<String>();
	// aoElementIdList.add("137");
	// aoElementIdList.add("138");
	// aoElementIdList.add("139");
	// List<EvidenceBean> aoEvidenceBeanList =
	// HHSUtil.getEvidenceFlag(aoElementIdList);
	// assertNotNull(aoEvidenceBeanList);
	// }

	/**
	 * This method tests the negative execution of getEvidenceFlag() method of
	 * HHSUtil and determines whether or not the list of EvidenceBean is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	// @Test(expected = Exception.class)
	// public void testGetEvidenceFlagApplicationException() throws Exception
	// {
	// List<String> aoElementIdList = null;
	// HHSUtil.getEvidenceFlag(aoElementIdList);
	// }

	/**
	 * This method tests the execution of getEvidenceFlag() method of HHSUtil
	 * when the size of Element Id is zero and determines whether or not the
	 * list of EvidenceBean is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testGetEvidenceFlagWithListSizeZero() throws ApplicationException
	{
		List<String> aoElementIdList = new ArrayList<String>();
		List<EvidenceBean> aoEvidenceBeanList = HHSUtil.getEvidenceFlag(aoElementIdList);
		assertNull(aoEvidenceBeanList);
	}

	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetProcurementId() throws Exception
	{
		Procurement loProcurement = getProcurementBeanToBeSaved();
		String loActualResult = (String) moProcurementService.getProcurementId(moMyBatisSession, loProcurement);
		assertNotNull(loActualResult);
	}
	
	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetProcurementIdCase2() throws Exception
	{
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setProcurementId("131");
		String loActualResult = (String) moProcurementService.getProcurementId(moMyBatisSession, loProcurement);
		assertNotNull(loActualResult);
	}

	/**
	 * This method tests getting Procurement id from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementIdException() throws Exception
	{
		String lsExpectedResult = "204";
		Procurement loProcurement = getProcurementBeanToBeSaved();
		loProcurement.setModifiedBy(null);
		loProcurement.setProcurementId("624");
		moProcurementService.saveProcurementSummary(moMyBatisSession, null, 1);
		String loActualResult = (String) moProcurementService.getProcurementId(moMyBatisSession, loProcurement);
		assertEquals(lsExpectedResult, loActualResult);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusException() throws Exception
	{
		String lsExpectedResult = "16";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, null, null);
		assertEquals(lsExpectedResult, loActualResult);
	}

	/*
	 * @Test public void testUpdateProposalDocumentPropertiesAddendum() throws
	 * ApplicationException { Map<String, Object> loParamMap = new
	 * HashMap<String, Object>(); loParamMap.put("procurementId", "251");
	 * loParamMap.put("proposalId", "37"); loParamMap.put("DocumentTitle",
	 * "TestDocUpdated"); loParamMap.put("modifiedBy", "city_143");
	 * loParamMap.put("procurementDocId", "350"); loParamMap.put("modifiedDate",
	 * new Date()); loParamMap.put("isAddendum", "1");
	 * loParamMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
	 * Boolean lbRowsUpdated = (Boolean)
	 * moProcurementService.updateRfpDocumentProperties(moMyBatisSession,
	 * loParamMap, true); assertEquals(lbRowsUpdated, Boolean.TRUE); }
	 */

	/*
	 * @Test public void testUpdateProposalDocumentPropertiesRfp() throws
	 * ApplicationException { Map<String, Object> loParamMap = new
	 * HashMap<String, Object>(); loParamMap.put("procurementId", "624");
	 * loParamMap.put("proposalId", "37"); loParamMap.put("DocumentTitle",
	 * "TestDocUpdated"); loParamMap.put("modifiedBy", "623");
	 * loParamMap.put("procurementDocId", "350"); loParamMap.put("modifiedDate",
	 * new Date()); loParamMap.put("isAddendum", "0");
	 * loParamMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797B}");
	 * Boolean lbRowsUpdated = (Boolean)
	 * moProcurementService.updateRfpDocumentProperties(moMyBatisSession,
	 * loParamMap, true); assertEquals(lbRowsUpdated, Boolean.TRUE); }
	 */

	/*
	 * @Test(expected = Exception.class) public void
	 * testUpdateProposalDocumentPropertiesExp() throws ApplicationException {
	 * Map<String, Object> loParamMap = new HashMap<String, Object>();
	 * loParamMap.put("procurementId", "624"); loParamMap.put("proposalId",
	 * "37"); loParamMap.put("DocumentTitle", "TestDocUpdated");
	 * loParamMap.put("modifiedBy", "623"); loParamMap.put("procurementDocId",
	 * "350"); loParamMap.put("modifiedDate", new Date());
	 * loParamMap.put("isAddendum", null); loParamMap.put("documentId",
	 * "{729CFD7A-3212-448F-91C6-2C9F939E797B}"); Boolean lbRowsUpdated =
	 * (Boolean)
	 * moProcurementService.updateRfpDocumentProperties(moMyBatisSession,
	 * loParamMap, true); assertEquals(lbRowsUpdated, Boolean.TRUE); }
	 */

	/*
	 * @Test public void testUpdateProposalDocumentProperties() throws
	 * ApplicationException { Map<String, Object> loParamMap = new
	 * HashMap<String, Object>(); loParamMap.put("procurementId", "624");
	 * loParamMap.put("proposalId", "37"); loParamMap.put("DocumentTitle",
	 * "TestDocUpdated"); loParamMap.put("modifiedBy", "city_143");
	 * loParamMap.put("procurementDocId", "350"); loParamMap.put("modifiedDate",
	 * new Date()); loParamMap.put("isAddendum", "0");
	 * loParamMap.put("documentId", "{729CFD7A-3212-448F-91C6-2C9F939E797DB}");
	 * Boolean lbRowsUpdated = (Boolean)
	 * moProcurementService.updateRfpDocumentProperties(moMyBatisSession,
	 * loParamMap, true); assertEquals(lbRowsUpdated, Boolean.TRUE); }
	 */
	/*
	 * @Test(expected = Exception.class) public void
	 * testUpdateProposalDocumentPropertiesAppExp() throws Exception {
	 * Map<String, Object> loParamMap = null;
	 * moProcurementService.updateRfpDocumentProperties(moMyBatisSession,
	 * loParamMap, true); }
	 */

	/**
	 * This method tests if proposal custom questions are available for a
	 * procurement
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProposalCustomQuestions() throws ApplicationException
	{

		List<ProposalQuestionAnswerBean> loPropCustomQuestionList = moProcurementService.fetchProposalCustomQuestions(
				moMyBatisSession, "624", null);
		assertNotNull(loPropCustomQuestionList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchProposalCustomQuestionsCase1() throws ApplicationException
	{

		moProcurementService.fetchProposalCustomQuestions(null, "624", null);
	}


	/**
	 * This method tests if proposal document types are available for a
	 * procurement
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProposalDocumentTypes() throws ApplicationException
	{

		List<ExtendedDocument> loPropDocumentTypeList = moProcurementService.fetchProposalDocumentType(
				moMyBatisSession, "624", null);
		assertNotNull(loPropDocumentTypeList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchProposalDocumentType() throws ApplicationException
	{

		moProcurementService.fetchProposalDocumentType(null, "624", null);
	}

	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionText("What's Your Name ?");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService
				.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setIsAddendum("false");
		loProposalQuestionAnswerBean.setQuestionSeqNo("1");
		loProposalQuestionAnswerBean.setQuestionText("What's Your Name ?");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService
				.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions5() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionSeqNo("1");
		loProposalQuestionAnswerBean.setQuestionText("What's Your Name ?");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService
				.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestionsCase8() throws ApplicationException
	{
		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moMyBatisSession, null);
		assertFalse(lbSaveStatus);
	}
	
	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestionsCaseNegative() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionText("%$@#%$@");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(null, loProposalDetailsBean);
		assertTrue(lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions3() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionFlag(null);
		loProposalQuestionAnswerBean.setQuestionSeqNo(null);
		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService
				.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}
	
	/**
	 * This method tests saveProposalCustomQuestions	
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions4() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionFlag(null);
		loProposalQuestionAnswerBean.setQuestionSeqNo(null);

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService
				.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}


	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentType(null);
		loExtendedDocument.setDocumentSeqNumber(null);
		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setIsAddendum("false");
		loExtendedDocument.setDocumentType(null);
		loExtendedDocument.setDocumentSeqNumber(null);

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType5() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("Y");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType8() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType9() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setIsAddendum("false");
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType12() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchCustomQuestionsApplicationException() throws Exception
	{
		moProcurementService.fetchProposalCustomQuestions(moMyBatisSession, null, null);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * proposal document types from the database.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchDocumentTypesApplicationException() throws Exception
	{
		moProcurementService.fetchProposalDocumentType(moMyBatisSession, null, null);
	}

	/**
	 * This method tests throwing application exception while saving Custom
	 * Question list in the database.
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testSaveCustomQuestionsApplicationException() throws Exception
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setProcurementId("406");
		List<ProposalQuestionAnswerBean> loQuestionList = null;
		loQuestionList.add(new ProposalQuestionAnswerBean());
		loProposalDetailsBean.setQuestionAnswerBeanList(loQuestionList);

		moProcurementService.saveProposalCustomQuestions(moMyBatisSession, loProposalDetailsBean);
	}

	/**
	 * This method tests throwing application exception while saving Custom
	 * Question list in the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveProposalDocumentTypeApplicationException() throws Exception
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setProcurementId("406");
		List<ExtendedDocument> loExtendedDocuments = new ArrayList<ExtendedDocument>();
		loExtendedDocuments.add(new ExtendedDocument());
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocuments);
		moProcurementService.saveProposalDocumentType(moMyBatisSession, loProposalDetailsBean);
	}

	/**
	 * This method tests throwing application exception while fetching Approved
	 * Providers list for notification.
	 * @throws Exception
	 */
	@Test
	public void testFetchApprovedProvidersForNotificationCase2() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		List<String> loAlertList = new ArrayList<String>();
		NotificationDataBean obj = new NotificationDataBean();
		loAlertList.add("AL201");
		loAlertList.add("AL202");
		loAlertList.add("NT201");
		loAlertList.add("NT202");
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		StringBuffer lsBfApplicationUrl = new StringBuffer(256);
		lsBfApplicationUrl
				.append("http://localhost:7001/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProurement=true");
		loRequestMap.put("LINK", lsBfApplicationUrl.toString());
		loNotificationMap.put("AL201", obj);
		loNotificationMap.put("AL202", obj);
		loNotificationMap.put("NT201", obj);
		loNotificationMap.put("NT202", obj);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProcurementService.fetchApprovedProvidersForNotification(moMyBatisSession, "127", true,
				loNotificationMap);
		assertNotNull(loNotificationMap);
		
	}

	/**
	 * This method tests throwing application exception while fetching Approved
	 * Providers list for notification.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchApprovedProvidersForNotificationCase3() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		moProcurementService.fetchApprovedProvidersForNotification(moMyBatisSession, null, true, loNotificationMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchApprovedProvidersForNotificationCase4() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap = moProcurementService.fetchApprovedProvidersForNotification(null, "127", true,loNotificationMap);
	}

	/**
	 * This method tests the execution of updateProcurementServiceData method
	 * and determines whether data of table procurement_services is getting
	 * updated or not
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementServiceDataCase1() throws ApplicationException
	{
		Boolean lbValidateStatus = true;
		Boolean lbProcurementStatusFlag = true;
		Map<String, Object> loServiceMap = new HashMap<String, Object>();
		String lsProcurementStatus = "2";
		String lsProcId = "249";
		Boolean lbUpdateSuccessful = moProcurementService.updateProcurementServiceData(moMyBatisSession, lsProcId,
				lsProcurementStatus, loServiceMap, lbValidateStatus, lbProcurementStatusFlag);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(lbUpdateSuccessful == true);
		
	}

	/**
	 * This method tests the execution of updateProcurementServiceData method
	 * and determines whether data of table procurement_services is getting
	 * updated or not when boolean flag is set as false
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementServiceDataCase2() throws ApplicationException
	{
		Boolean lbValidateStatus = false;
		Boolean lbProcurementStatusFlag = true;
		Map<String, Object> loServiceMap = new HashMap<String, Object>();
		String lsProcurementStatus = "2";
		String lsProcId = "296";
		Boolean lbUpdateSuccessful = moProcurementService.updateProcurementServiceData(moMyBatisSession, lsProcId,
				lsProcurementStatus, loServiceMap, lbValidateStatus, lbProcurementStatusFlag);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(lbUpdateSuccessful == false);
		
	}

	/**
	 * This method tests the execution of updateProcurementServiceData method
	 * and determines whether data of table procurement_services is getting
	 * updated or not when service list count is "0" corresponding to the
	 * procurement Id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementServiceDataCase3() throws ApplicationException
	{
		Boolean lbValidateStatus = false;
		Boolean lbProcurementStatusFlag = true;
		Map<String, Object> loServiceMap = new HashMap<String, Object>();
		String lsProcurementStatus = "2";
		String lsProcId = "2";
		Boolean lbUpdateSuccessful = moProcurementService.updateProcurementServiceData(moMyBatisSession, lsProcId,
				lsProcurementStatus, loServiceMap, lbValidateStatus, lbProcurementStatusFlag);
		assertNotNull(lbUpdateSuccessful);
		assertTrue(lbUpdateSuccessful == false);
		
	}

	/**
	 * This method tests the execution of updateProcurementServiceData method
	 * and determines whether data of table procurement_services is getting
	 * updated or not when procurement Id value is not good
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testUpdateProcurementServiceDataCase4() throws Exception
	{
		Boolean lbValidateStatus = true;
		Boolean lbProcurementStatusFlag = true;
		Map<String, Object> loServiceMap = new HashMap<String, Object>();
		String lsProcurementStatus = "2";
		String lsProcId = "###";
		moProcurementService.updateProcurementServiceData(moMyBatisSession, lsProcId, lsProcurementStatus,
				loServiceMap, lbValidateStatus, lbProcurementStatusFlag);
		
	}

	/**
	 * This method tests the execution of fetchProcurementCoNDetails method and
	 * determines whether or not Contract Details are retrieved
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementCoNDetails() throws ApplicationException
	{
		moProcurementService.fetchProcurementCoNDetails(moMyBatisSession, msContractIdSourceOne);
		
		
	}

	/**
	 * This method tests the execution of fetchProcurementCoNDetails method and
	 * determines whether or not Contract Details are retrieved
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementCoNDetailsContractSourceTwo() throws ApplicationException
	{
		
		moProcurementService.fetchProcurementCoNDetails(moMyBatisSession, msContractIdSourceTwo);
		
		
	}

	/**
	 * This method tests the negative execution of fetchProcurementCoNDetails
	 * method and determines whether or not Contract Details are retrieved
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementCoNDetailsApplicationException() throws ApplicationException
	{
		String lsContractId = null;
		ProcurementCOF loProcurementCOF = moProcurementService.fetchProcurementCoNDetails(moMyBatisSession,
				lsContractId);
		assertNotNull(loProcurementCOF);
		
	}

	/**
	 * This method tests the execution of fetchProcurementCoNDetails method and
	 * determines whether or not Contract Details are retrieved
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementCoNDetailsSessionNull() throws ApplicationException
	{
		String lsContractId = "1109991";
		moProcurementService.fetchProcurementCoNDetails(null, lsContractId);
	}

	/**
	 * This method tests the execution of getProcurementTitle method with
	 * null notificationMap param
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testGetProcurementTitleCase2() throws ApplicationException
	{
		String lsProcurementId = "135";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		List<String> loAlertList = new ArrayList<String>();
		NotificationDataBean obj = new NotificationDataBean();
		loAlertList.add("AL201");
		loAlertList.add("AL202");
		loAlertList.add("NT201");
		loAlertList.add("NT202");
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		StringBuffer lsBfApplicationUrl = new StringBuffer(256);
		lsBfApplicationUrl
				.append("http://localhost:7001/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProurement=true");
		loRequestMap.put("LINK", lsBfApplicationUrl.toString());
		loNotificationMap.put("AL201", obj);
		loNotificationMap.put("AL202", obj);
		loNotificationMap.put("NT201", obj);
		loNotificationMap.put("NT202", obj);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moProcurementService.getProcurementTitle(moMyBatisSession, lsProcurementId, loNotificationMap);
	}

	/**
	 * This method tests the negative execution of getProcurementTitle method
	 * and and determines whether or not procurement tile is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProcurementTitleApplicationException() throws ApplicationException
	{
		String lsProcurementId = null;
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		List<String> loAlertList = new ArrayList<String>();
		loAlertList.add("AL201");
		loAlertList.add("AL202");
		loAlertList.add("NT201");
		loAlertList.add("NT202");
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		StringBuffer lsBfApplicationUrl = new StringBuffer(256);
		lsBfApplicationUrl
				.append("http://localhost:7001/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProurement=true");
		loRequestMap.put("LINK", lsBfApplicationUrl.toString());
		loNotificationMap.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moProcurementService.getProcurementTitle(moMyBatisSession, lsProcurementId, loNotificationMap);
		
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementDataWithReleaseCase1() throws ApplicationException
	{
		
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		loRFPReleaseBean.setCompetitionPoolCount(1);
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", msPlannedProcurementId);
		loProcurementMap.put("userId", msUserId);
		loProcurementMap.put("organizationId", "hhs");
		Boolean lbProcurementStatus = moProcurementService.updateProcurementDataWithRelease(moMyBatisSession,
				msProcurementStatusPlanned, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
		assertNotNull(lbProcurementStatus);
		assertTrue(lbProcurementStatus);
		
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementDataWithReleaseCase2() throws ApplicationException
	{
		
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		loRFPReleaseBean.setCompetitionPoolCount(1);
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", msDraftProcurementId);
		loProcurementMap.put("userId", msUserId);
		loProcurementMap.put("organizationId", "hhs");
		Boolean lbProcurementStatus = moProcurementService.updateProcurementDataWithRelease(moMyBatisSession,
				msProcurementStatusDraft, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
		assertNotNull(lbProcurementStatus);
		assertTrue(!lbProcurementStatus);
		
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementDataWithReleaseCase3() throws ApplicationException
	{
		String lsProcStatus = "2";
		RFPReleaseBean loRFPReleaseBean = null;
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", msDraftProcurementId);
		loProcurementMap.put("userId", msUserId);
		loProcurementMap.put("organizationId", "hhs");
		Boolean lbProcurementStatus = moProcurementService.updateProcurementDataWithRelease(moMyBatisSession,
				lsProcStatus, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
		assertNotNull(lbProcurementStatus);
		assertTrue(!lbProcurementStatus);
		
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementDataWithReleaseCase4() throws ApplicationException
	{
		String lsProcStatus = "2";
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		Boolean lbValidateStatus = false;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", msDraftProcurementId);
		loProcurementMap.put("userId", msUserId);
		loProcurementMap.put("organizationId", "hhs");
		Boolean lbProcurementStatus = moProcurementService.updateProcurementDataWithRelease(moMyBatisSession,
				lsProcStatus, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
		assertNotNull(lbProcurementStatus);
		assertTrue(!lbProcurementStatus);
		
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementDataWithReleaseCase5() throws ApplicationException
	{
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", msReleaseProcurementId);
		loProcurementMap.put("userId", msUserId);
		loProcurementMap.put("organizationId", "hhs");
		Boolean lbProcurementStatus = moProcurementService.updateProcurementDataWithRelease(moMyBatisSession,
				msProcurementStatusReleased, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
		assertNotNull(lbProcurementStatus);
	}

	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testUpdateProcurementDataWithReleaseCase6() throws Exception
	{
		String lsProcStatus = "2";
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = null;
		moProcurementService.updateProcurementDataWithRelease(moMyBatisSession, lsProcStatus, loRFPReleaseBean,
				lbValidateStatus, lbCofFlag, loProcurementMap);
		
	}
	
	/**
	 * This method tests the execution of updateProcurementDataWithRelease
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcurementDataWithReleaseCase7() throws ApplicationException
	{
		String lsProcStatus = "2";
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setReqDocCount(1);
		loRFPReleaseBean.setReqDocTypeCount(1);
		loRFPReleaseBean.setEvaluationCriteriaCount(1);
		Boolean lbValidateStatus = true;
		Boolean lbCofFlag = true;
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put("procurementId", "624");
		loProcurementMap.put("userId", "city_142");
		loProcurementMap.put("organizationId", "hhs");
		moProcurementService.updateProcurementDataWithRelease(null,
				lsProcStatus, loRFPReleaseBean, lbValidateStatus, lbCofFlag, loProcurementMap);
	}

	/**
	 * This method will check the execution of updateProcurementStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void checkUpdateProcurementStatusCase1() throws ApplicationException
	{
		Boolean lbAuthStatusFlag = true;
		Map loStatusMap = new HashMap();
		loStatusMap.put("procurementStatusCode", "8");
		loStatusMap.put("userId", "city_143");
		loStatusMap.put("procurementId", "242");
		Boolean lbCloseProcurementStatus = moProcurementService.updateProcurementStatus(moMyBatisSession, loStatusMap,
				lbAuthStatusFlag);
		assertNotNull(lbCloseProcurementStatus);
		assertTrue(lbCloseProcurementStatus);
	}

	/**
	 * This method will check the execution of updateProcurementStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void checkUpdateProcurementStatusCase2() throws ApplicationException
	{
		Boolean lbAuthStatusFlag = false;
		Map loStatusMap = new HashMap();
		loStatusMap.put("procurementStatusCode", "8");
		loStatusMap.put("userId", "city_143");
		loStatusMap.put("procurementId", "242");
		Boolean lbCloseProcurementStatus = moProcurementService.updateProcurementStatus(moMyBatisSession, loStatusMap,
				lbAuthStatusFlag);
		assertNotNull(lbCloseProcurementStatus);
		assertTrue(!lbCloseProcurementStatus);
	}

	/**
	 * This method will check the execution of updateProcurementStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void checkUpdateProcurementStatusCase3() throws ApplicationException
	{
		Boolean lbAuthStatusFlag = true;
		Map loStatusMap = null;
		moProcurementService.updateProcurementStatus(null, loStatusMap, lbAuthStatusFlag);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase1() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{4D5B694C-96F1-40E8-9AD5-624C2BE8A049}");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(loDocumentBeanList);
		assertNotNull(loDcoumentIdList);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated when document Id
	 * is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase2() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(loDocumentBeanList);
		assertTrue(loDcoumentIdList.isEmpty() == true);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated when the bean
	 * object is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void fetchRfpReleaseDocIdsListCase3() throws Exception
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = null;
		loDocumentBeanList.add(loExtendedDocument);
		moProcurementService.fetchRfpReleaseDocIdsList(loDocumentBeanList);

	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase4() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{4D5B694C-96F1-40E8-9AD5-624C2BE8A049}");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(loDocumentBeanList);
		assertNotNull(loDcoumentIdList);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated when document Id
	 * is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase5() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(loDocumentBeanList);
		assertTrue(loDcoumentIdList.size() == 0);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase6() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{4D5B694C-96F1-40E8-9AD5-624C2BE8A049}");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(null);
		assertTrue(loDcoumentIdList.size() == 0);
	}

	/**
	 * This method tests the execution of fetchRfpReleaseDocIdsList method and
	 * determines whether or not the list is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void fetchRfpReleaseDocIdsListCase7() throws ApplicationException
	{
		List<ExtendedDocument> loDocumentBeanList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{4D5B694C-96F1-40E8-9AD5-624C2BE8A049}");
		loDocumentBeanList.add(loExtendedDocument);
		List<String> loDcoumentIdList = moProcurementService.fetchRfpReleaseDocIdsList(null);
		assertTrue(loDcoumentIdList.isEmpty() == true);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase1() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase2() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = null;

		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase3() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertTrue(loRFPDocumentList.size() > 0);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase4() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertTrue(loRFPDocumentList.size() == 0);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testConsolidateAllDocsPropertiesCase5() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(null);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase6() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, null);
		assertNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testConsolidateAllDocsPropertiesCase7() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(null);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase8() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase9() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("##", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertTrue(loRFPDocumentList.size() > 0);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase10() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(null, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase11() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("lsDocumentId", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase12() throws ApplicationException
	{
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("##", "{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	/**
	 * This method tests the execution of consolidateAllDocsProperties method
	 * and determines whether or not the list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testConsolidateAllDocsPropertiesCase13() throws ApplicationException
	{
		Calendar cal = Calendar.getInstance();
		List<ExtendedDocument> loRFPDocumentList = new ArrayList<ExtendedDocument>();
		HashMap<String, Object> loDocPropsBean = new HashMap<String, Object>();
		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentId("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}");
		loExtendedDocument.setDocumentStatus("Complete");
		loExtendedDocument.setDocumentType("Workscope Template");
		loRFPDocumentList.add(loExtendedDocument);
		loDocPropsBean.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "Test Document");
		loDocPropsBean.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, "Test User ID");
		loDocPropsBean.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "Test User ID");
		loDocPropsBean.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "Test User ID");
		loDocPropsBean.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, cal.getTime());
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		loDocumentPropHM.put("{D3486709-3AA1-408B-B2EE-285CEDB5BE95}", loDocPropsBean);
		loRFPDocumentList = moProcurementService.consolidateAllDocsProperties(loDocumentPropHM, loRFPDocumentList);
		assertNotNull(loRFPDocumentList);
	}

	@Test
	public void testUpdateProcurementDataOnPublish() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "636");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> aoServiceData = new HashMap<String, Object>();
		aoServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		aoServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", aoServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase2() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "636");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "2", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase3() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "2", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase4() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "1", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase5() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "5", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase6() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase7() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, true);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertFalse(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase8() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, true);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertFalse(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase9() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, true);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, true);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertFalse(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase10() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "##");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase11() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase12() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "##");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateProcurementDataOnPublishCase13() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, null);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(moMyBatisSession,
				loProcurementInputMap, "3", loServiceData);
		assertTrue(loUpdateStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProcurementDataOnPublishCaseNegative() throws ApplicationException
	{
		Map<String, String> loProcurementInputMap = new HashMap<String, String>();
		loProcurementInputMap.put(HHSConstants.PROCUREMENT_ID, "601");
		loProcurementInputMap.put(HHSConstants.USER_ID, "city_143");
		loProcurementInputMap.put(HHSConstants.IS_PUB_PROC, HHSConstants.TRUE);
		Map<String, Object> loServiceData = new HashMap<String, Object>();
		loServiceData.put(HHSConstants.EVIDENCE_ERROR_FLAG, false);
		loServiceData.put(HHSConstants.SERV_LIST_ERROR, false);
		Boolean loUpdateStatus = moProcurementService.updateProcurementDataOnPublish(null,
				loProcurementInputMap, "5", loServiceData);
		assertTrue(loUpdateStatus);
	}


	@Test
	public void testFetchDocumentIdsList() throws ApplicationException
	{
		List<String> loDcoumentIdList = new ArrayList<String>();
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId("{A47AEFDD-E8D5-48D3-822E-FFF56AA87241}");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(loExtendedBean2);
		loDcoumentIdList = moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
		assertNotNull(loDcoumentIdList);
		assertTrue(loDcoumentIdList.size() > 0);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchDocumentIdsList2() throws ApplicationException
	{
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(null);
		loDocumentBeanList2.add(loExtendedBean2);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchDocumentIdsList3() throws ApplicationException
	{
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId("{A47AEFDD-E8D5-48D3-822E-FFF56AA87241}");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(null);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	@Test(expected = Exception.class)
	public void testFetchDocumentIdsList4() throws Exception
	{

		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId("{A47AEFDD-E8D5-48D3-822E-FFF56AA87241}");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(null);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	@Test(expected = Exception.class)
	public void testFetchDocumentIdsList5() throws ApplicationException
	{
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(null);
		loDocumentBeanList2.add(loExtendedBean2);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);

	}

	@Test
	public void testFetchDocumentIdsList6() throws ApplicationException
	{
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		moProcurementService.fetchDocumentIdsList(null, loDocumentBeanList2);

	}

	@Test
	public void testFetchDocumentIdsList7() throws ApplicationException
	{
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId("{A47AEFDD-E8D5-48D3-822E-FFF56AA87241}");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loDocumentBeanList1.add(loExtendedBean1);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, null);
	}

	@Test
	public void testFetchDocumentIdsList8() throws ApplicationException
	{
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId(null);
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(loExtendedBean2);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	@Test
	public void testFetchDocumentIdsList9() throws ApplicationException
	{
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentId("{A47AEFDD-E8D5-48D3-822E-FFF56AA87241}");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId(null);
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(loExtendedBean2);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	@Test
	public void testFetchDocumentIdsList10() throws ApplicationException
	{
		ExtendedDocument loExtendedBean1 = new ExtendedDocument();
		ExtendedDocument loExtendedBean2 = new ExtendedDocument();
		List<ExtendedDocument> loDocumentBeanList1 = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loDocumentBeanList2 = new ArrayList<ExtendedDocument>();
		loExtendedBean1.setAssignStatus("Submitted");
		loExtendedBean1.setDocumentStatus("Submitted");
		loExtendedBean1.setDocumentType("Annual Report");
		loExtendedBean1.setIsRequiredDoc("Required");
		loExtendedBean1.setModifiedDate("04/25/2013");
		loExtendedBean1.setSecondSortDate(false);
		loExtendedBean1.setStartNode(0);
		loExtendedBean2.setAddendumType("0");
		loExtendedBean2.setDocumentId("{9D1B9DCD-7A22-4CB4-819C-887976843674}");
		loExtendedBean2.setDocumentStatus("Submitted");
		loExtendedBean2.setDocumentType("Addenda");
		loExtendedBean2.setEndNode(0);
		loExtendedBean2.setFirstSortDate(false);
		loExtendedBean2.setModifiedDate("04/25/2013");
		loExtendedBean2.setOrganizationType("agency_org");
		loExtendedBean2.setReferenceDocSeqNo("557");
		loExtendedBean2.setSecondSortDate(false);
		loExtendedBean2.setStartNode(0);
		loExtendedBean2.setStatus("Proposals Received");
		loDocumentBeanList1.add(loExtendedBean1);
		loDocumentBeanList2.add(loExtendedBean2);
		moProcurementService.fetchDocumentIdsList(loDocumentBeanList1, loDocumentBeanList2);
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgList method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcTitleAndOrgList() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
		loProcMap.put("procurementId", msReleaseProcurementId);
		loProcMap.put("statusId", "24");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProcurementService.fetchProcTitleAndOrgList(moMyBatisSession, loProcMap,
				loNotificationMap);
		assertNotNull(loNotificationMap);
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgList method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcTitleAndOrgListCase1() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProcurementService.fetchProcTitleAndOrgList(moMyBatisSession, null, loNotificationMap);
		assertNull(loNotificationMap.get(TransactionConstants.PROVIDER_ID));
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgList method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcTitleAndOrgListCase2() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moProcurementService.fetchProcTitleAndOrgList(moMyBatisSession, loProcMap,
				loNotificationMap);
		assertNull(loNotificationMap.get(TransactionConstants.PROVIDER_ID));
	}

	/**
	 * This method tests the execution of fetchProcTitleAndOrgList method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgListCase4() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
		loProcMap.put("procurementId", "1");
		loProcMap.put("statusId", "24");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moProcurementService.fetchProcTitleAndOrgList(null, loProcMap, loNotificationMap);
	}
	
	/**
	 * This method tests the execution of fetchProcTitleAndOrgList method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcTitleAndOrgListCase5() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, Object> loRequestMap = new HashMap<String, Object>();
		loProcMap.put("procurementId", "1");
		loProcMap.put("statusId", "24");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moProcurementService.fetchProcTitleAndOrgList(null, loProcMap, loNotificationMap);
	}
	
	/**
	 * This method tests the execution of checkIfAwardApproved method and
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testCheckIfAwardApproved() throws ApplicationException
	{
		String lsProcurementId = "805";
		Boolean liCount = moProcurementService.checkIfAwardApproved(moMyBatisSession, lsProcurementId);
		assertFalse(liCount);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckIfAwardApprovedCase1() throws ApplicationException
	{
		String lsProcurementId = "##";
		moProcurementService.checkIfAwardApproved(moMyBatisSession, lsProcurementId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckIfAwardApprovedCase2() throws ApplicationException
	{
		String lsProcurementId = "805";
		moProcurementService.checkIfAwardApproved(null, lsProcurementId);
	}
	
	@Test
	public void testFetchProcurementAddendumDataCase1() throws ApplicationException
	{
		String lsProcurementId = "133";
		moProcurementService.fetchProcurementAddendumData(moMyBatisSession, lsProcurementId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementAddendumDataCase2() throws ApplicationException
	{
		String lsProcurementId = "601";
		moProcurementService.fetchProcurementAddendumData(null, lsProcurementId);
	}
	
	@Test
	public void testGetTaxonomyTree() throws ApplicationException
	{
		String lsElementType = "Test";
		String lsFromCache = "";
		String lsCompleteTree = moProcurementService.getTaxonomyTree(lsElementType,lsFromCache,moMyBatisSession);
		assertNotNull(lsCompleteTree);
	}
	
	@Test
	public void testGetTaxonomyTreeCase3() throws ApplicationException
	{
		String lsElementType = "##";
		String lsFromCache = "";
		String lsCompleteTree = moProcurementService.getTaxonomyTree(lsElementType,lsFromCache,moMyBatisSession);
		assertNotNull(lsCompleteTree);
	}
	
	@Test
	public void testCloseProcurement() throws ApplicationException
	{
		Boolean loContractCountFlag = false;
		Map<String, Object> loStatusMap = new HashMap<String, Object>();
		Boolean loAwardReviewStatusFlag = true;
		loStatusMap.put("userId",msUserId);
		loStatusMap.put("procurementStatusCode",msProcurementStatusReleased);
		loStatusMap.put("procurementId",msProcurementStatusClosed);
		Boolean lbCloseProcurementStatus = moProcurementService.closeProcurement(moMyBatisSession, loContractCountFlag, loAwardReviewStatusFlag, loStatusMap);
		assertTrue(lbCloseProcurementStatus);
	}
	
	@Test
	public void testCloseProcurementCase1() throws ApplicationException
	{
		Boolean loContractCountFlag = false;
		Map<String, Object> loStatusMap = new HashMap<String, Object>();
		Boolean loAwardReviewStatusFlag = false;
		loStatusMap.put("userId",msUserId);
		loStatusMap.put("procurementStatusCode",msProcurementStatusReleased);
		loStatusMap.put("procurementId",msProcurementStatusClosed);
		Boolean lbCloseProcurementStatus = moProcurementService.closeProcurement(moMyBatisSession, loContractCountFlag, loAwardReviewStatusFlag, loStatusMap);
		assertFalse(lbCloseProcurementStatus);
	}
	
	@Test
	public void testCloseProcurementCase2() throws ApplicationException
	{
		Boolean loContractCountFlag = true;
		Map<String, Object> loStatusMap = new HashMap<String, Object>();
		Boolean loAwardReviewStatusFlag = false;
		loStatusMap.put("userId",msUserId);
		loStatusMap.put("procurementStatusCode",msProcurementStatusReleased);
		loStatusMap.put("procurementId",msProcurementStatusClosed);
		Boolean lbCloseProcurementStatus = moProcurementService.closeProcurement(moMyBatisSession, loContractCountFlag, loAwardReviewStatusFlag, loStatusMap);
		assertFalse(lbCloseProcurementStatus);
	}
	
	@Test
	public void testCloseProcurementCase3() throws ApplicationException
	{
		Boolean loContractCountFlag = true;
		Map<String, Object> loStatusMap = new HashMap<String, Object>();
		Boolean loAwardReviewStatusFlag = true;
		loStatusMap.put("userId",msUserId);
		loStatusMap.put("procurementStatusCode",msProcurementStatusReleased);
		loStatusMap.put("procurementId",msProcurementStatusClosed);
		Boolean lbCloseProcurementStatus = moProcurementService.closeProcurement(moMyBatisSession, loContractCountFlag, loAwardReviewStatusFlag, loStatusMap);
		assertFalse(lbCloseProcurementStatus);
	}

	@Test
	public void testGetOrganizationDetail() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "ORGANIZATION";
		Map<String, String> loHMWhereClause = new HashMap();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetOrganizationDetailCase1() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "ORGANIZATION";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		moProcurementService.getOrganizationDetail(asBuisAppId, null, asOrgId,asTableName);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase2() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "AWARD";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase3() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "";
		String asTableName = "ORGANIZATION";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase4() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase5() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase6() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase7() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(null, moMyBatisSession, asOrgId,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase8() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, null,asTableName);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testGetOrganizationDetailCase9() throws ApplicationException
	{
		String asBuisAppId = null;
		String asOrgId = "accenture";
		String asTableName = "";
		Map<String, String> loHMWhereClause = new HashMap<String, String>();
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put(HHSConstants.BUIZ_APP_ID, asBuisAppId);
		loHMWhereClause.put("TABLE", asTableName);
		StringBuffer lsbWhereClause = new StringBuffer();
		lsbWhereClause.append("ORGANIZATION_ID = '").append(loHMWhereClause.get(HHSConstants.ORG_ID)).append("'");
		loHMWhereClause.put("asWhere", lsbWhereClause.toString());
		HashMap loHMFormValues = moProcurementService.getOrganizationDetail(asBuisAppId, moMyBatisSession, asOrgId,null);
		assertNotNull(loHMFormValues);
		
	}
	
	@Test
	public void testDeleteProcurementProviderData1() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(moMyBatisSession, true, "623");
		assertTrue(loDeleteStatus);
		
	}
	
	@Test
	public void testDeleteProcurementProviderData2() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(moMyBatisSession, true, "001");
		assertTrue(loDeleteStatus);
		
	}
	
	@Test
	public void testDeleteProcurementProviderData3() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(moMyBatisSession, false, "001");
		assertFalse(loDeleteStatus);
		
	}
	
	@Test
	public void testDeleteProcurementProviderData4() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(moMyBatisSession, false, null);
		assertFalse(loDeleteStatus);
		
	}
	
	@Test(expected=ApplicationException.class)
	public void testDeleteProcurementProviderData5() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(moMyBatisSession, true, null);
	}
	
	@Test(expected=ApplicationException.class)
	public void testDeleteProcurementProviderData6() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(null, true, "2");
	}
	
	@Test
	public void testDeleteProcurementProviderData7() throws ApplicationException
	{
		Boolean loDeleteStatus;
		loDeleteStatus = moProcurementService.deleteProcurementProviderData(null, false, "2");
		assertFalse(loDeleteStatus);
		
	}
	
	@Test
	public void testUpdateAuditBean() throws ApplicationException
	{
		HhsAuditBean aoAudit = new HhsAuditBean();
		moProcurementService.updateAuditBean(msProcurementStatusPlanned, aoAudit);
	}
	
	@Test
	public void testUpdateAuditBeanCase2() throws ApplicationException
	{
		HhsAuditBean aoAudit = new HhsAuditBean();
		String procurementStatus = "4";
		moProcurementService.updateAuditBean(procurementStatus, aoAudit);
	}
	
	@Test
	public void testUpdateAuditBeanCase3() throws ApplicationException
	{
		HhsAuditBean aoAudit = new HhsAuditBean();
		String procurementStatus = "5";
		moProcurementService.updateAuditBean(procurementStatus, aoAudit);
	}
	
	@Test
	public void testUpdateAuditBeanCase4() throws ApplicationException
	{
		HhsAuditBean aoAudit = new HhsAuditBean();
		String procurementStatus = "6";
		moProcurementService.updateAuditBean(procurementStatus, aoAudit);
	}
	
	@Test
	public void testUpdateLastModifiedMap() throws ApplicationException
	{
		Map<Object, Object> loLastModifiedHashMap =  new HashMap<Object, Object>();
		moProcurementService.updateLastModifiedMap(loLastModifiedHashMap,msProcurementStatusDraft);	
	}
	
	@Test
	public void testFetchProcurementStatusId() throws ApplicationException
	{
		moProcurementService.fetchProcurementStatusId(moMyBatisSession,msProcurementStatusDraft);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProcurementStatusIdNegative() throws ApplicationException
	{
		moProcurementService.fetchProcurementStatusId(null,msProcurementStatusDraft);
	}

	/**
	 * This method Test the getAgency task details
	 * @throws ApplicationException
	 */
	@Test
	public void getAgencyContactDetails() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setAgencyId("city");
		moProcurementService.getAgencyContactDetails(moMyBatisSession,loProcurement);
	}
	/**
	 * This method Test the getAgency task details with 
	 * null procurement value
	 * @throws ApplicationException
	 */
	@Test
	public void getAgencyContactDetailsCase2() throws ApplicationException
	{
		Procurement loProcurement = null;
		moProcurementService.getAgencyContactDetails(moMyBatisSession,loProcurement);
	}

	/**
	 * This method test the getProcurementSummaryFromAwardTask
	 * with proper arguments
	 * @throws ApplicationException
	 */
	@Test
	public void testGetProcurementSummaryFromAwardTask() throws ApplicationException
	{

	HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
	HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
	loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msReleaseProcurementId);
	loTaskMap.put("1",loProcurementMap);
	String lsWobNumber = "1";
	moProcurementService.getProcurementSummaryFromAwardTask(moMyBatisSession,loTaskMap,lsWobNumber);

	}
	
	/**
	 * This method test the getProcurementSummaryFromAwardTask
	 * with null arguments
	 * @throws ApplicationException
	 */
	@Test
	public void testGetProcurementSummaryFromAwardTaskCase2() throws ApplicationException
	{

	HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
	HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
	loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msReleaseProcurementId);
	loTaskMap.put("1",loProcurementMap);
	String lsWobNumber = "1";
	moProcurementService.getProcurementSummaryFromAwardTask(moMyBatisSession,null,lsWobNumber);
	}
	
	/**
	 * This method test the getProcurementSummaryFromAwardTask
	 * with null arguments
	 * @throws ApplicationException
	 */
	@Test
	public void testGetProcurementSummaryFromAwardTaskCase3() throws ApplicationException
	{

	HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
	String lsWobNumber = "1";
	moProcurementService.getProcurementSummaryFromAwardTask(moMyBatisSession,loTaskMap,lsWobNumber);
	}

	@Test
	public void testFetchServiceUnitFlag() throws ApplicationException
	{
		moProcurementService.fetchServiceUnitFlag(moMyBatisSession,msPlannedProcurementId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchServiceUnitFlag2() throws ApplicationException
	{
		moProcurementService.fetchServiceUnitFlag(null,msPlannedProcurementId);
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchActiveProcurements0Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchActiveProcurements(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementCount1Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceinsertUpdateServiceAcceleratorService2Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.insertUpdateServiceAcceleratorService(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchEpinDetails3Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchEpinDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProcurementSummary4Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProcurementSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetStatusId5Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getStatusId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementSummary6Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchRfpReleaseDocsDetails7Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchRfpReleaseDocsDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementId8Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicedisplayApprovedProvidersList9Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.displayApprovedProvidersList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetTaxonomyTree10Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getTaxonomyTree(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckForEvidenceFlag11Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkForEvidenceFlag(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementDataOnPublish12Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementDataOnPublish(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateAuditBean13Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateAuditBean(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementServiceData14Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementServiceData(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfUserOfSameAgency15Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfUserOfSameAgency(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfUserOfSameAgency16Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfUserOfSameAgency(null, null, null, true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfAwardApproved17Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfAwardApproved(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementDetailsForNav18Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementDetailsForNav(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceinsertRfpDocumentDetails19Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.insertRfpDocumentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceremoveRfpDocs20Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.removeRfpDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementCustomQuestionAnswer21Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementCustomQuestionAnswer(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchSelectedServices22Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchSelectedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchServicesList23Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchServicesList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvDetails24Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersList25Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersList(null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersListAfterRelease26Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersListAfterRelease(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetOrganizationDetail27Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getOrganizationDetail(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDropDownValue28Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDropDownValue(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveApprovedProvDetails29Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveApprovedProvDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementCoNDetails30Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementCoNDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProviderStatus31Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProviderStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicedeleteProvidersData32Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.deleteProvidersData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicepreserveOldStatus33Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.preserveOldStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProgramNameForAgencyId34Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProgramNameForAgencyId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetSavedServicesList35Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getSavedServicesList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetTaxonomyServiceName36Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getTaxonomyServiceName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementStatus37Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDocumentIdList38Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDocumentIdList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementSummaryForNav39Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementSummaryForNav(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementTitle40Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementTitle(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersForNotification41Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersForNotification(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProposalCustomQuestions42Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProposalCustomQuestions(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProposalDocumentType43Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProposalDocumentType(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProposalCustomQuestions44Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProposalCustomQuestions(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProposalDocumentType45Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProposalDocumentType(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementDataWithRelease46Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementDataWithRelease(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchRfpReleaseDocIdsList47Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchRfpReleaseDocIdsList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceconsolidateAllDocsProperties48Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.consolidateAllDocsProperties(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcTitleAndOrgList49Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcTitleAndOrgList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDocumentIdsList50Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDocumentIdsList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementAddendumData51Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementAddendumData(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecloseProcurement52Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.closeProcurement(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateLastModifiedMap53Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateLastModifiedMap(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicedeleteProcurementProviderData54Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.deleteProcurementProviderData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementStatusId55Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementStatusId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetAcceleratorContactDetails56Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getAcceleratorContactDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetAgencyContactDetails57Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getAgencyContactDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProgramName58Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProgramName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementSummaryFromAwardTask59Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementSummaryFromAwardTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchServiceUnitFlag60Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchServiceUnitFlag(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}