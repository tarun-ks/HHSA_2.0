package com.nyc.hhs.daomanager.serviceTest;

/**
 * 
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationDetailBean;
import com.nyc.hhs.model.EvaluationFilterBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalFilterBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.ScoreDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class EvaluationServiceTest
{

	EvaluationService moEvaluationService = new EvaluationService();
	private static SqlSession moMyBatisSession = null;
	private final String msProcurementId = "131";
	private final String msProcurementId2 = "164";
	private final String msEvaluationPoolMappingId = "165";
	private final String msEvaluationPoolMappingId2 = "237";
	private final String msIsEvaluationSend = "1";
	private final String msProcurementIdSendEvalTask = "131";
	private final String msProcurementIdNotSendEvalTask = "115";
	private final String msProposalId = "103";
	private final String msProposalIdForComments = "103";
	private final String msProposalIdForScores1 = "103";
	private final String msProposalIdForScores2 = "107";
	private final String msProcurementIdForScores1 = "131";
	private final String msEvaluationPoolMappingIdForScores1 = "163";
	private final String msProcurementIdForAwardReturned = "131";
	private final String msEvaluationPoolMappingIdForAwardReturned = "163";
	private static String msAwardReviewStatusInProgress = "";
	private static String msAwardReviewStatusReturned = "";
	private final String msProposalIdForAccoComments = "103";
	private final String msProcurementIdForAccoComments = "131";
	private final String msProcurementIdForAwards = "131";
	private final String msEvaluationPoolMappingIdForAward = "163";
	private final String msEvaluationCriteriaId = "173";
	private final String msEvaluationStatusId = "175";
	private final String msProposalIdNonResult = "217";
	private final String msProcurementIdNonResposive = "164";
	private final String msEvaluationGroupIdNonRes = "240";

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

			msAwardReviewStatusInProgress = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS);

			msAwardReviewStatusReturned = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_AWARD_REVIEW_RETURNED);
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
	 * Method will Test the FetchProposalDetails Implementation
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalDetailsCase1() throws ApplicationException
	{
		Map<String, String> loHeaderMap = moEvaluationService.fetchProposalDetails(moMyBatisSession, msProposalId);
		assertNotNull(loHeaderMap.get("PROCUREMENT_TITLE"));
		assertNotNull(loHeaderMap.get("ORGANIZATION_LEGAL_NAME"));
		assertNotNull(loHeaderMap.get("PROPOSAL_TITLE"));
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsCase2() throws ApplicationException
	{
		moEvaluationService.fetchProposalDetails(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsCase3() throws ApplicationException
	{
		String lsProposalId = "1";
		moEvaluationService.fetchProposalDetails(null, lsProposalId);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalDetailsCase4() throws ApplicationException
	{
		moEvaluationService.fetchProposalDetails(null, null);
	}

	/**
	 * This method tests the execution of fetchReqProposalDetails method and
	 * determines whether or not a bean is getting populated corresponding to a
	 * proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchReqProposalDetails() throws ApplicationException
	{
		String lsProposalId = msProposalId;
		EvaluationBean loProposalDetails = moEvaluationService.fetchReqProposalDetails(moMyBatisSession, lsProposalId);
		assertNotNull(loProposalDetails);

	}

	/**
	 * This method tests the execution of fetchReqProposalDetails method and
	 * determines whether or not a bean is getting populated corresponding to a
	 * null proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchReqProposalDetailsCase2() throws ApplicationException
	{
		String lsProposalId = null;
		EvaluationBean loProposalDetails = moEvaluationService.fetchReqProposalDetails(moMyBatisSession, lsProposalId);
		assertNull(loProposalDetails);

	}

	/**
	 * This method tests the negative execution of fetchReqProposalDetails
	 * method and determines whether or not a bean is getting populated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchReqProposalDetailsApplicationException() throws ApplicationException
	{
		String lsProposalId = "###";
		moEvaluationService.fetchReqProposalDetails(moMyBatisSession, lsProposalId);

	}

	/**
	 * This method tests the execution of updateSelectedProposalDetails method
	 * and determines whether or not proposal details are getting updated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateSelectedProposalDetailsCase1() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		aoEvalBean.setAwardAmount("70000");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(23);
		aoEvalBean.setModifiedByUserId("city_43");
		Integer loCount = moEvaluationService.updateSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	@Test
	public void testUpdateSelectedProposalDetailsCase3() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId("900");
		aoEvalBean.setAwardAmount("70000");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(23);
		aoEvalBean.setModifiedByUserId("city_43");
		Integer loCount = moEvaluationService.updateSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	/**
	 * This method tests the execution of updateSelectedProposalDetails method
	 * and determines whether or not proposal details are getting updated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateSelectedProposalDetailsCase2() throws ApplicationException
	{
		EvaluationBean aoEvalBean = null;
		Integer loCount = moEvaluationService.updateSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNull(loCount);

	}

	/**
	 * This method tests the negative execution of updateSelectedProposalDetails
	 * method and determines whether or not proposal details are getting updated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateSelectedProposalDetailsApplicationException() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId("###");
		aoEvalBean.setAwardAmount("10000");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(23);
		moEvaluationService.updateSelectedProposalDetails(moMyBatisSession, aoEvalBean);

	}

	/**
	 * This method tests the execution of updateNotSelectedProposalDetails
	 * method and determines whether or not proposal details are getting updated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateNotSelectedProposalDetailsCase1() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		aoEvalBean.setAwardAmount("70000");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(23);
		aoEvalBean.setModifiedByUserId("city_43");
		Integer loCount = moEvaluationService.updateNotSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	@Test
	public void testUpdateNotSelectedProposalDetailsCase3() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId("900");
		aoEvalBean.setAwardAmount("70000");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(23);
		aoEvalBean.setModifiedByUserId("city_43");
		Integer loCount = moEvaluationService.updateNotSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	/**
	 * This method tests the execution of updateNotSelectedProposalDetails
	 * method and determines whether or not proposal details are getting updated
	 * corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateNotSelectedProposalDetailsCase2() throws ApplicationException
	{
		EvaluationBean aoEvalBean = null;
		Integer liCount = moEvaluationService.updateNotSelectedProposalDetails(moMyBatisSession, aoEvalBean);
		assertNull(liCount);

	}

	/**
	 * This method tests the negative execution of
	 * updateNotSelectedProposalDetails method and determines whether or not
	 * proposal details are getting updated corresponding to a proposal Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateNotSelectedProposalDetailsApplicationException() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId("###");
		aoEvalBean.setAwardAmount("0");
		aoEvalBean.setComments("abc");
		aoEvalBean.setProposalStatusId(24);
		moEvaluationService.updateNotSelectedProposalDetails(moMyBatisSession, aoEvalBean);

	}

	// proposal details screen test cases start
	@Test
	public void testFetchProposalComments() throws ApplicationException
	{
		List<ProposalFilterBean> loList = moEvaluationService.fetchProposalComments(moMyBatisSession,
				msProposalIdForComments);
		assertNotNull(loList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalCommentsCase1() throws ApplicationException
	{
		moEvaluationService.fetchProposalComments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalCommentsExc() throws ApplicationException
	{
		moEvaluationService.fetchProposalComments(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalCommentsExcCase1() throws ApplicationException
	{
		moEvaluationService.fetchProposalComments(null, "6");
	}

	// proposal details screen test cases end

	/**
	 * This method tests the execution of fetchAwardReviewComments method and
	 * determines whether or not the award details(award review comments and
	 * modified date)Map is getting generated corresponding to the award Id
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test
	public void testFetchReviewAwardComments() throws ApplicationException
	{
		Map loViewAwardComment = moEvaluationService.fetchReviewAwardComments(moMyBatisSession,
				msEvaluationPoolMappingId, null, null);
		assertNull(loViewAwardComment);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchReviewAwardCommentsCase1() throws ApplicationException
	{
		String lsProcurementId = "805";
		moEvaluationService.fetchReviewAwardComments(null, lsProcurementId, null, null);
	}

	/**
	 * This method tests the negative execution of fetchAwardReviewComments
	 * method and determines whether or not the award details(award review
	 * comments and modified date)Map is getting generated corresponding to the
	 * award Id
	 * 
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchReviewAwardCommentsApplicationException() throws ApplicationException
	{
		String lsAwardId = "1";
		moEvaluationService.fetchReviewAwardComments(null, lsAwardId, null, null);
	}

	/**
	 * The Method will Test success scenario of delete Evaluation Setting Data
	 * .It will test if all evaluations Tasks WorkFlow has been terminated then
	 * data will be deleted success fully
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	@Test
	public void testDeleteEvaluationSettingData() throws ApplicationException
	{
		Boolean lbDeleteStatus = moEvaluationService.deleteEvaluationSettingData(moMyBatisSession, msProcurementId2,
				msEvaluationPoolMappingId2);
		assertNotNull(lbDeleteStatus);
		assertTrue(lbDeleteStatus);
	}

	/**
	 * This method will test of fetching Provider Names List when search
	 * performed with valid data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProviderNameList() throws ApplicationException
	{
		List<String> loProviderNameList = moEvaluationService.fetchProviderNameList(moMyBatisSession, "test_manager");
		assertNotNull(loProviderNameList);
		assertTrue(loProviderNameList.size() >= 1);
	}

	@Test
	public void testFetchProviderNameListCase2() throws ApplicationException
	{
		List<String> loProviderNameList = moEvaluationService.fetchProviderNameList(moMyBatisSession, null);
		assertNull(loProviderNameList);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchProviderListWithValidData2() throws ApplicationException
	{
		List<String> loProviderNameList = moEvaluationService.fetchProviderNameList(null, "test_manager");
		assertNull(loProviderNameList);
	}

	/**
	 * This method will test of fetching Provider Names List when search
	 * performed with Invalid data
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProviderListWithInValidData() throws ApplicationException
	{
		List<String> loProviderNameList = moEvaluationService.fetchProviderNameList(moMyBatisSession, "TES");
		assertFalse(loProviderNameList.size() == 0);
	}

	/**
	 * This method will test of fetching Provider Names List when no data passed
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProviderListWithNoData() throws ApplicationException
	{
		List<String> loProviderNameList = moEvaluationService.fetchProviderNameList(moMyBatisSession, null);
		assertEquals(loProviderNameList, null);
	}

	/**
	 * The Method will test getCancelEvalTaskVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetCancelEvalTaskVisibiltyStatus() throws ApplicationException
	{
		Boolean loFlag = moEvaluationService.getCancelEvalTaskVisibiltyStatus(moMyBatisSession,
				msEvaluationPoolMappingId, "1", 0);
		assertFalse(loFlag);
	}

	@Test()
	public void testGetCancelEvalTaskVisibiltyStatus1() throws ApplicationException
	{
		Boolean loFlag = moEvaluationService.getCancelEvalTaskVisibiltyStatus(moMyBatisSession,
				msEvaluationPoolMappingId, "0", 0);
		assertFalse(loFlag);
	}

	/**
	 * The Method will test getDownloadDBDDocsVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetDownloadDBDDocsVisibiltyStatus() throws ApplicationException
	{
		List<EvaluationBean> loEvalutionDetailsList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvBean1 = new EvaluationBean();
		loEvBean1.setSendEvaluationStatus("YES");
		loEvBean1.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		loEvBean1.setProcurementId("132");
		loEvalutionDetailsList.add(loEvBean1);
		Boolean loFlag = moEvaluationService.getDownloadDBDDocsVisibiltyStatus(moMyBatisSession, "132",
				loEvalutionDetailsList);
		assertTrue(loFlag);
	}

	/**
	 * The Method will test getDownloadDBDDocsVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetDownloadDBDDocsVisibiltyStatus1() throws ApplicationException
	{
		Boolean loFlag = moEvaluationService.getDownloadDBDDocsVisibiltyStatus(moMyBatisSession, "132", null);
		assertFalse(loFlag);
	}

	/**
	 * The Method will test getDownloadDBDDocsVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetDownloadDBDDocsVisibiltyStatus2() throws ApplicationException
	{
		List<EvaluationBean> loEvalutionDetailsList = new ArrayList<EvaluationBean>();
		Boolean loFlag = moEvaluationService.getDownloadDBDDocsVisibiltyStatus(moMyBatisSession, "132",
				loEvalutionDetailsList);
		assertFalse(loFlag);
	}

	/**
	 * The Method will test getDownloadDBDDocsVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetDownloadDBDDocsVisibiltyStatus3() throws ApplicationException
	{
		List<EvaluationBean> loEvalutionDetailsList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvBean1 = new EvaluationBean();
		loEvBean1.setSendEvaluationStatus("YES");
		loEvBean1.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		loEvBean1.setProcurementId("132");
		loEvalutionDetailsList.add(loEvBean1);
		moEvaluationService.getDownloadDBDDocsVisibiltyStatus(null, "132", loEvalutionDetailsList);
	}

	/**
	 * The Method will test getTotalEvaluationData method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetTotalEvaluationData() throws ApplicationException
	{
		List<EvaluationBean> loEvalutionDetailsList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvBean1 = new EvaluationBean();
		loEvBean1.setSendEvaluationStatus("YES");
		loEvalutionDetailsList.add(loEvBean1);

		EvaluationBean loEvBean = new EvaluationBean();
		loEvBean.setProcurementId(msProcurementId);
		loEvBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		moEvaluationService.getTotalEvaluationData(moMyBatisSession, loEvBean, loEvalutionDetailsList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetTotalEvaluationDataCase2() throws ApplicationException
	{
		List<EvaluationBean> loEvalutionDetailsList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvBean1 = new EvaluationBean();
		loEvBean1.setSendEvaluationStatus("YES");
		loEvalutionDetailsList.add(loEvBean1);

		EvaluationBean loEvBean = new EvaluationBean();
		loEvBean.setProcurementId(msProcurementId);
		loEvBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		moEvaluationService.getTotalEvaluationData(null, loEvBean, loEvalutionDetailsList);
		assertNotNull(loEvBean);
	}

	/**
	 * *The Method will test Fetch Evaluation Details method.
	 * @throws ApplicationException
	 */

	/**
	 * *The Method will test Fetch Evaluation Details method when the Evaluation
	 * bean is null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationDetailsCase2() throws ApplicationException
	{
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcurementId(msProcurementIdForScores1);
		loEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForScores1);
		loEvalBean.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		moEvaluationService.fetchEvaluationDetails(moMyBatisSession, loEvalBean);

	}

	/**
	 * *The Method will test Fetch Evaluation Details method when the Evaluation
	 * bean is not null and contains organization Id
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationDetailsApplicationException() throws ApplicationException
	{
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcurementId("###");
		loEvalBean.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		loEvalBean.setOrganizationName("HHS");
		moEvaluationService.fetchEvaluationDetails(moMyBatisSession, loEvalBean);

	}

	/**
	 * *The Method will test Fetch Evaluation Details method when the Evaluation
	 * bean is not null and contains organization Id
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchEvaluationDetailsException() throws Exception
	{
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcurementId("abc");
		loEvalBean.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		loEvalBean.setOrganizationName("HHS");
		moEvaluationService.fetchEvaluationDetails(moMyBatisSession, loEvalBean);

	}

	/**
	 * The Method will test Fetch Evaluation Details method while pagination
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationDetailsWhilePagination() throws ApplicationException
	{
		EvaluationBean loEvalBean = new EvaluationBean();
		List<String> evaluationStatusList = new ArrayList<String>();
		List<String> proposalStatusList = new ArrayList<String>();
		evaluationStatusList.add((HHSConstants.EMPTY_STRING));
		evaluationStatusList.add("41");
		evaluationStatusList.add("104");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole(HHSConstants.ACCO_MANAGER_ROLE);
		loEvalBean.setPaginationEnable("Y");
		loEvalBean.setStartNode(1);
		loEvalBean.setEndNode(5);
		loEvalBean.setProposalTitle("Proposal 2");
		loEvalBean.setOrganizationName("HHS");
		loEvalBean.setProposalId("101");
		loEvalBean.setEvaluationStatusList(evaluationStatusList);
		proposalStatusList.add("20");
		loEvalBean.setProposalStatusList(proposalStatusList);
		loEvalBean.setProcurementId("114");
		loEvalBean.setEvaluationPoolMappingId("144");
		loEvalBean.setOrganizationName("test");
		loEvalBean.setProposalTitle("abc");
		loEvalBean.setFirstSort("Evalution_Completed");
		loEvalBean.setSecondSort("ORGANIZATION_LEGAL_NAME");
		loEvalBean.setFirstSortType(ApplicationConstants.SORT_ASCENDING);
		loEvalBean.setSecondSortType(ApplicationConstants.SORT_ASCENDING);
		loEvalBean.setSortColumnName("evalutionsCompleted");
		loEvalBean.setFirstSortDate(false);
		loEvalBean.setSecondSortDate(false);
		loEvalBean.setStartNode(1);
		loEvalBean.setEndNode(5);
		loEvalBean.setUserRole("abc");
		List<EvaluationBean> loEvList = moEvaluationService.fetchEvaluationDetails(moMyBatisSession, loEvalBean);
		assertNotNull(loEvList);

	}

	/**
	 * The Method will test fetchEvaluationScoreDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationScoreDetails() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, msEvaluationPoolMappingIdForScores1);
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		List<EvaluationBean> loEvScoreList = moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession,
				aoTaskMap, lsWobNumber);
		assertNotNull(loEvScoreList);
	}

	/**
	 * The Method will test fetchEvaluationScoreDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationScoreDetails1() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		List<EvaluationBean> loEvScoreList = moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession,
				aoTaskMap, lsWobNumber);
		assertNull(loEvScoreList);
	}

	/**
	 * The Method will test fetchEvaluationScoreDetails method
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationScoreDetails2() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "##");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "1");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
	}

	/**
	 * The Method will test fetchEvaluationScoreDetails method
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationScoreDetails3() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "##");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		moEvaluationService.fetchEvaluationScoreDetails(null, aoTaskMap, lsWobNumber);
	}

	/**
	 * The Method will test fetchEvaluationScoreDetails method
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationScoreDetails4() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "1");
		aoTaskMap.put("##", loProcurementMap);
		moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
	}

	/**
	 * The Method will test saveEvaluationScoreDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testSaveEvaluationScoreDetails() throws ApplicationException
	{
		moMyBatisSession.rollback();
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("Test");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("10");
		loEvalBean.setEvaluationCriteriaId(msEvaluationCriteriaId);
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		loScoreBean.setAction(HHSConstants.TASK_FINISHED);
		liEvaluationBeanList.add(loEvalBean);
		String lsProposalReviewStatus = HHSConstants.SCORES_RETURNED;
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, lsProposalReviewStatus);
		assertTrue(saveFlag);
	}

	@Test
	public void testSaveEvaluationScoreDetailsCase1() throws ApplicationException
	{
		moMyBatisSession.rollback();
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("Test");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("10");
		loEvalBean.setEvaluationCriteriaId(msEvaluationCriteriaId);
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		String lsProposalReviewStatus = HHSConstants.SCORES_RETURNED;
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, lsProposalReviewStatus);
		assertTrue(saveFlag);
	}

	@Test()
	public void testSaveEvaluationScoreDetailsCase2() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = false;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("Test");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("10");
		loEvalBean.setEvaluationCriteriaId(msEvaluationCriteriaId);
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		String lsProposalReviewStatus = HHSConstants.SCORES_RETURNED;
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, lsProposalReviewStatus);
		assertFalse(saveFlag);
	}

	@Test()
	public void testSaveEvaluationScoreDetailsCase3() throws ApplicationException
	{
		moMyBatisSession.rollback();
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("Test");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("10");
		loEvalBean.setEvaluationCriteriaId(msEvaluationCriteriaId);
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setAction(HHSConstants.TASK_FINISHED);
		String lsProposalReviewStatus = HHSConstants.SCORES_ACCEPTED;
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, lsProposalReviewStatus);
		assertTrue(saveFlag);

	}

	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationScoreDetailsCase4() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("Test");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("10");
		loEvalBean.setEvaluationCriteriaId("1529");
		loEvalBean.setEvaluationStatusId("211");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("Test_User");
		loEvalBean.setModifiedByUserId("Test_User");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "");
		assertTrue(saveFlag);
	}

	/**
	 * This method tests the execution of fetchEvaluationCriteriaDetails method
	 * and determines whether or not evaluation criteria list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationCriteriaDetailsCase1() throws ApplicationException
	{
		List<EvaluationBean> loEvalCriteriaList = moEvaluationService.fetchEvaluationCriteriaDetails(moMyBatisSession,
				msProcurementId);
		assertNotNull(loEvalCriteriaList);

	}

	/**
	 * This method tests the execution of fetchEvaluationCriteriaDetails method
	 * and determines whether or not evaluation criteria list is getting
	 * generated when the procurement Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationCriteriaDetailsCase2() throws ApplicationException
	{
		List<EvaluationBean> loEvalCriteriaList = moEvaluationService.fetchEvaluationCriteriaDetails(moMyBatisSession,
				null);
		assertNull(loEvalCriteriaList);

	}

	@Test
	public void testfetchEvaluationScoreDetailsForEvaluator() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();

		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put("evaluationStatusId", "153");
		loEvaluationService.fetchEvaluationScoreDetailsForEvaluator(moMyBatisSession, loQueryMap);

	}

	@Test
	public void testreviewScoreVersionInsert() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		String loProposalId = "103";
		String loProposalId1 = "344";
		String loUserlId = "city_43";
		loEvaluationService.reviewScoreVersionInsert(moMyBatisSession, loProposalId1, loUserlId);

	}

	@Test(expected = ApplicationException.class)
	public void testreviewScoreVersionInsertNegative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();

		loEvaluationService.reviewScoreVersionInsert(null, null, null);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchEvaluationScoreDetailsForEvaluatorException() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();

		loEvaluationService.fetchEvaluationScoreDetailsForEvaluator(null, null);

	}

	/**
	 * This method tests the negative execution of
	 * fetchEvaluationCriteriaDetails method and determines whether or not
	 * evaluation criteria list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationCriteriaDetailsApplicationException() throws ApplicationException
	{
		String lsProcurementId = "###";
		moEvaluationService.fetchEvaluationCriteriaDetails(moMyBatisSession, lsProcurementId);

	}

	/**
	 * This method tests the execution of fetchAccoComments method and
	 * determines whether or not ACCO comments map is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAccoCommentsCase1() throws ApplicationException
	{

		moEvaluationService.fetchAccoComments(moMyBatisSession, msProcurementIdForAccoComments,
				msProposalIdForAccoComments);
	}

	/**
	 * This method tests the execution of fetchAccoComments method and
	 * determines whether or not ACCO comments map is getting generated when the
	 * procurement Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAccoCommentsCase2() throws ApplicationException
	{

		moEvaluationService.fetchAccoComments(moMyBatisSession, msProcurementIdForAccoComments, null);
	}

	/**
	 * This method tests the execution of fetchAccoComments method and
	 * determines whether or not ACCO comments map is getting generated when the
	 * proposal Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAccoCommentsCase3() throws ApplicationException
	{

		moEvaluationService.fetchAccoComments(moMyBatisSession, null, msProposalIdForAccoComments);
	}

	/**
	 * This method tests the negative execution of fetchAccoComments method and
	 * determines whether or not ACCO comments map is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAccoCommentsApplicationException() throws ApplicationException
	{
		String lsProcurementId = "3";
		String lsProposalId = "###";
		moEvaluationService.fetchAccoComments(null, lsProcurementId, lsProposalId);

	}

	/**
	 * This method tests the negative execution of fetchAccoComments method and
	 * determines whether or not ACCO comments map is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchAccoCommentsException() throws Exception
	{
		String lsProcurementId = "3";
		String lsProposalId = "###";
		moEvaluationService.fetchAccoComments(moMyBatisSession, lsProcurementId, lsProposalId);

	}

	/**
	 * This method tests the execution of fetchEvaluatorCommentsDetails method
	 * and determines whether or not evaluator's comments list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluatorCommentsDetailsCase1() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluatorCommentsDetails(
				moMyBatisSession, loHmRequiredProps, "1");
		assertNotNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the execution of fetchEvaluatorCommentsDetails method
	 * and determines whether or not evaluator's comments list is getting
	 * generated when the procurement Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluatorCommentsDetailsCase2() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluatorCommentsDetails(
				moMyBatisSession, loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the execution of fetchEvaluatorCommentsDetails method
	 * and determines whether or not evaluator's comments list is getting
	 * generated when the proposal Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluatorCommentsDetailsCase3() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluatorCommentsDetails(
				moMyBatisSession, loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the negative execution of fetchEvaluatorCommentsDetails
	 * method and determines whether or not evaluator's comments list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluatorCommentsDetailsCase4() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = null;
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluatorCommentsDetails(
				moMyBatisSession, loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the negative execution of fetchEvaluatorCommentsDetails
	 * method and determines whether or not evaluator's comments list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = java.lang.Exception.class)
	public void testFetchEvaluatorCommentsDetailsException() throws Exception
	{
		moEvaluationService.fetchEvaluatorCommentsDetails(null, "", "");
	}

	@Test(expected = java.lang.Exception.class)
	public void testFetchEvaluatorCommentsDetails2Exception() throws Exception
	{
		HashMap loHmRequiredProps = new HashMap();
		moEvaluationService.fetchEvaluatorCommentsDetails(null, loHmRequiredProps, "");
	}

	/**
	 * This method tests the execution of fetchEvaluatorCommentsDetails method
	 * and determines whether or not evaluator's comments list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluatorCommentsDetailsCase5() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluatorCommentsDetails(null,
				loHmRequiredProps, "1");
		assertNotNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the execution of fetchEvaluationScoresDetails method
	 * and determines whether or not evaluator's score list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationScoresDetailsCase1() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluationScoresDetails(moMyBatisSession,
				loHmRequiredProps, "1");
		assertNotNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the execution of fetchEvaluationScoresDetails method
	 * and determines whether or not evaluator's score list is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationScoresDetailsCase2() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = null;
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluationScoresDetails(moMyBatisSession,
				loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the execution of fetchEvaluationScoresDetails method
	 * and determines whether or not evaluator's score list is getting generated
	 * when the procurement Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationScoresDetailsCase3() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluationScoresDetails(moMyBatisSession,
				loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * For Map as an argument
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationScoresDetailsCase4() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluationScoresDetails(moMyBatisSession,
				loHmRequiredProps, "1");
		assertNull(loEvaluationScoreList);

	}

	/**
	 * This method tests the negative execution of fetchEvaluationScoresDetails
	 * method and determines whether or not evaluator's score list is getting
	 * generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationScoresDetailsApplicationException() throws ApplicationException
	{
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcurementIdForScores1);
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, msProposalIdForScores1);
		loHmRequiredProps.put("1", loProcurementMap);
		List<EvaluationBean> loEvaluationScoreList = moEvaluationService.fetchEvaluationScoresDetails(null,
				loHmRequiredProps, "1");
		assertNotNull(loEvaluationScoreList);

	}

	/**
	 * This method tests estimated procurement value corresponding to the
	 * procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementValue() throws ApplicationException
	{
		String lsEstimatedProcurementValue = moEvaluationService.fetchProcurementValue(moMyBatisSession,
				msProcurementIdForAwards);
		assertNotNull(lsEstimatedProcurementValue);

	}

	/**
	 * This method tests Provider and award count corresponding to the
	 * procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchProcurementValue1() throws ApplicationException
	{
		String lsProcurementId = "3";
		moEvaluationService.fetchProcurementValue(null, lsProcurementId);

	}

	/**
	 * This method tests estimated procurement value corresponding to the
	 * procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchProcurementValue2() throws ApplicationException
	{
		String lsProcurementId = "3";
		String lsEstimatedProcurementValue = moEvaluationService.fetchProcurementValue(moMyBatisSession,
				lsProcurementId);
		assertNull(lsEstimatedProcurementValue);

	}

	/**
	 * This method tests Provider and award count corresponding to the
	 * procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testCountFinalizeProcurementDetails() throws ApplicationException
	{

		Integer loEvaluationScoreList = moEvaluationService.countFinalizeProcurementDetails(moMyBatisSession,
				msEvaluationPoolMappingIdForScores1);
		assertNotNull(loEvaluationScoreList);

	}

	@Test(expected = ApplicationException.class)
	public void testCountFinalizeProcurementDetailsCase1() throws ApplicationException
	{
		String lsProcurementId = "###";
		moEvaluationService.countFinalizeProcurementDetails(moMyBatisSession, lsProcurementId);

	}

	@Test(expected = ApplicationException.class)
	public void testCountFinalizeProcurementDetailsCase2() throws ApplicationException
	{
		String lsProcurementId = "739";
		moEvaluationService.countFinalizeProcurementDetails(null, lsProcurementId);

	}

	@Test(expected = ApplicationException.class)
	public void testCountFinalizeProcurementDetailsCase3() throws ApplicationException
	{
		moEvaluationService.countFinalizeProcurementDetails(moMyBatisSession, null);

	}

	/**
	 * This method tests Procurement status corresponding to the procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateProcurementStatus() throws ApplicationException
	{
		Boolean loProcurementStatus = moEvaluationService.updateProcurementStatus(moMyBatisSession, "111");
		assertTrue(loProcurementStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProcurementStatusCase1() throws ApplicationException
	{
		String lsProcurementId = "3";
		moEvaluationService.updateProcurementStatus(null, lsProcurementId);

	}

	/**
	 * This method tests while checking status for Proposal
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testMarkProposalNonResponsive() throws ApplicationException
	{
		Boolean loMarkProposalNonResponsive = moEvaluationService.markProposalNonResponsive(moMyBatisSession,
				msProposalId);
		assertTrue(loMarkProposalNonResponsive);

	}

	@Test(expected = ApplicationException.class)
	public void testMarkProposalNonResponsiveCase1() throws ApplicationException
	{
		moEvaluationService.markProposalNonResponsive(moMyBatisSession, null);

	}

	@Test(expected = ApplicationException.class)
	public void testMarkProposalNonResponsiveCase2() throws ApplicationException
	{
		String lsProcurementId = "3";
		moEvaluationService.markProposalNonResponsive(null, lsProcurementId);

	}

	@Test(expected = ApplicationException.class)
	public void testMarkProposalNonResponsiveCase3() throws ApplicationException
	{
		moEvaluationService.markProposalNonResponsive(null, null);

	}

	/**
	 * This method tests while fetch details for Internal/External evaluator id
	 * and details for status id, proposal id on basis of procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchIntExtProposalDetails() throws ApplicationException
	{
		List<EvaluationDetailBean> loIntExtProposalDetails = moEvaluationService.fetchIntExtProposalDetails(
				moMyBatisSession, msProcurementIdForScores1, msEvaluationPoolMappingIdForScores1);
		assertNotNull(loIntExtProposalDetails);

	}

	/**
	 * This method tests while finsert details in Evaluation_STATUS table
	 * against procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateEvaluationStatus() throws ApplicationException
	{
		HashMap<String, Object> loEvalMap = new HashMap<String, Object>();
		loEvalMap.put("statusId", "43");
		loEvalMap.put("userId", "city_43");
		loEvalMap.put("evaluationStatusId", "4");
		Boolean loUpdateEvaluationStatus = moEvaluationService.updateEvaluationStatus(moMyBatisSession, loEvalMap);
		assertTrue(loUpdateEvaluationStatus);

	}

	/**
	 * This method tests while finsert details in Evaluation_STATUS table
	 * against procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationStatusCase1() throws ApplicationException
	{
		HashMap<String, Object> loEvalMap = new HashMap<String, Object>();
		loEvalMap.put("statusId", "43");
		loEvalMap.put("userId", "city_142");
		loEvalMap.put("evaluationStatusId", "##");
		moEvaluationService.updateEvaluationStatus(moMyBatisSession, loEvalMap);

	}

	/**
	 * This method tests while finsert details in Evaluation_STATUS table
	 * against procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationStatusCase2() throws ApplicationException
	{
		HashMap<String, Object> loEvalMap = new HashMap<String, Object>();
		loEvalMap.put("statusId", "43");
		loEvalMap.put("userId", "city_142");
		loEvalMap.put("evaluationStatusId", "4");
		moEvaluationService.updateEvaluationStatus(null, loEvalMap);
	}

	/**
	 * This method tests to get all the internal evaluator from the database
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetInternalEvaluationsList() throws ApplicationException
	{
		List<Evaluator> loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		assertNotNull(loInternalEvaluationsList);

	}

	@Test
	public void testGetInternalEvaluationsListCase2() throws ApplicationException
	{
		List<Evaluator> loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, false);
		assertNull(loInternalEvaluationsList);

	}

	@Test(expected = ApplicationException.class)
	public void testGetInternalEvaluationsListCase3() throws ApplicationException
	{
		List<Evaluator> loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(null,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		assertNull(loInternalEvaluationsList);

	}

	/**
	 * This method tests to get all the internal evaluator from the database
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	/**
	 * This method tests to get Procurement AgencyId on basis of procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetProcurementAgencyId() throws ApplicationException
	{
		moEvaluationService.getProcurementAgencyId(moMyBatisSession, msProcurementId);

	}

	/**
	 * This method is getting called when you are trying to find whether
	 * evaluation task has been send or not on the basis of procurement Id.
	 */

	@Test
	public void testFindEvaluationTaskSent() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.findEvaluationTaskSent(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId);
		assertTrue(loEvaluationReviewScoreStatus);

	}

	@Test
	public void testFindEvaluationTaskSentCase2() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.findEvaluationTaskSent(moMyBatisSession,
				msProcurementIdNotSendEvalTask, msEvaluationPoolMappingId);
		assertFalse(loEvaluationReviewScoreStatus);

	}

	/**
	 * This method tests EvaluationReviewScore on basis of procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetEvaluationReviewScore() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.getEvaluationReviewScore(moMyBatisSession,
				msProcurementId, msEvaluationPoolMappingId, msIsEvaluationSend);
		assertTrue(loEvaluationReviewScoreStatus);

	}

	@Test()
	public void testGetEvaluationReviewScoreCase2() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.getEvaluationReviewScore(moMyBatisSession,
				msProcurementId, msEvaluationPoolMappingId, null);
		assertTrue(loEvaluationReviewScoreStatus);
	}

	@Test()
	public void testGetEvaluationReviewScoreCase3() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.getEvaluationReviewScore(moMyBatisSession,
				msProcurementId, msEvaluationPoolMappingId, "0");
		assertTrue(loEvaluationReviewScoreStatus);
	}

	@Test()
	public void testGetEvaluationReviewScoreCase4() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.getEvaluationReviewScore(moMyBatisSession, "557",
				"451", msIsEvaluationSend);
		assertFalse(loEvaluationReviewScoreStatus);
	}

	@Test()
	public void testGetEvaluationReviewScoreCase5() throws ApplicationException
	{
		Boolean loEvaluationReviewScoreStatus = moEvaluationService.getEvaluationReviewScore(moMyBatisSession, "557",
				"452", msIsEvaluationSend);
		assertFalse(loEvaluationReviewScoreStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testGetEvaluationReviewScoreCase6() throws ApplicationException
	{
		moEvaluationService.getEvaluationReviewScore(null, "557", "452", msIsEvaluationSend);
	}

	/**
	 * This method tests to fetch the evaluation users internal on basis of
	 * procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testFetchInternalEvaluatorUsers() throws ApplicationException
	{
		String lsAgencyId = "city";
		String lsInputParam = "hhs";
		List<AutoCompleteBean> loInternalEvaluatorUsers = moEvaluationService.fetchInternalEvaluatorUsers(
				moMyBatisSession, lsAgencyId, lsInputParam);
		assertNotNull(loInternalEvaluatorUsers);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchInternalEvaluatorUsersCase2() throws ApplicationException
	{
		String lsAgencyId = "city";
		String lsInputParam = "hhs";
		List<AutoCompleteBean> loInternalEvaluatorUsers = moEvaluationService.fetchInternalEvaluatorUsers(null,
				lsAgencyId, lsInputParam);
		assertNotNull(loInternalEvaluatorUsers);

	}

	/**
	 * This method tests to fetch the evaluation users external on basis of
	 * procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testFetchExternalEvaluatorUsers() throws ApplicationException
	{

		String lsAgencyId = "city";
		String lsInputParam = "hhs";
		List<ProviderBean> loExternalEvaluatorList = moEvaluationService.fetchExternalEvaluatorUsers(moMyBatisSession,
				lsAgencyId, lsInputParam);
		assertNotNull(loExternalEvaluatorList);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchExternalEvaluatorUsersCase2() throws ApplicationException
	{

		String lsAgencyId = "city";
		String lsInputParam = "hhs";
		List<ProviderBean> loExternalEvaluatorList = moEvaluationService.fetchExternalEvaluatorUsers(null, lsAgencyId,
				lsInputParam);
		assertNotNull(loExternalEvaluatorList);

	}

	/**
	 * This method tests to get EvaluationCount on basis of procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetEvaluationCount() throws ApplicationException
	{
		Integer liEvaluationCount = moEvaluationService.getEvaluationCount(moMyBatisSession, msEvaluationPoolMappingId,
				true);
		assertNotNull(liEvaluationCount);

	}

	@Test
	public void testGetEvaluationCountCase2() throws ApplicationException
	{

		moEvaluationService.getEvaluationCount(moMyBatisSession, msEvaluationPoolMappingId, false);

	}

	@Test(expected = ApplicationException.class)
	public void testGetEvaluationCountCase3() throws ApplicationException
	{

		moEvaluationService.getEvaluationCount(null, msEvaluationPoolMappingId, true);

	}

	/**
	 * This method tests to to get all the internal evaluator from the database
	 * on basis of procurement id
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetExternalEvaluationsListCase1() throws ApplicationException
	{
		List<Evaluator> loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		assertNotNull(loExternalEvaluationsList);

	}

	@Test
	public void testGetExternalEvaluationsListCase2() throws ApplicationException
	{
		List<Evaluator> loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, false);
		assertNull(loExternalEvaluationsList);

	}

	@Test(expected = ApplicationException.class)
	public void testGetExternalEvaluationsListCase3() throws ApplicationException
	{
		List<Evaluator> loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(null,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		assertNull(loExternalEvaluationsList);

	}

	/**
	 * The method will retrieve the Award Document Info i.e Document
	 * Title,Type,last Modified,Modified By *
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testGetEvaluationScores() throws ApplicationException
	{
		List<ProposalDetailsBean> loProposalDetailsBeanList = new ArrayList<ProposalDetailsBean>();
		ProposalDetailsBean loProposalDetailsBean1 = new ProposalDetailsBean();
		ProposalDetailsBean loProposalDetailsBean2 = new ProposalDetailsBean();
		loProposalDetailsBean1.setProposalId(msProposalIdForScores1);
		loProposalDetailsBean2.setProposalId(msProposalIdForScores2);
		loProposalDetailsBeanList.add(loProposalDetailsBean1);
		loProposalDetailsBeanList.add(loProposalDetailsBean2);
		List<EvaluationBean> loEvalScoreBeanList = moEvaluationService.getEvaluationScores(moMyBatisSession,
				loProposalDetailsBeanList);
		assertNotNull(loEvalScoreBeanList);
		assertTrue(loEvalScoreBeanList.size() > 0);

	}

	/**
	 * The method will retrieve the Award Document Info i.e Document
	 * Title,Type,last Modified,Modified By *
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test(expected = ApplicationException.class)
	public void testGetEvaluationScores2() throws ApplicationException
	{
		moEvaluationService.getEvaluationScores(moMyBatisSession, null);

	}

	/**
	 * The method will retrieve the Award Document Info i.e Document
	 * Title,Type,last Modified,Modified By *
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test(expected = ApplicationException.class)
	public void testGetEvaluationScores3() throws ApplicationException
	{
		List<ProposalDetailsBean> loProposalDetailsBeanList = new ArrayList<ProposalDetailsBean>();
		ProposalDetailsBean loProposalDetailsBean1 = new ProposalDetailsBean();
		ProposalDetailsBean loProposalDetailsBean2 = new ProposalDetailsBean();
		loProposalDetailsBean1.setProposalId("240");
		loProposalDetailsBean2.setProposalId("7");
		loProposalDetailsBeanList.add(loProposalDetailsBean1);
		loProposalDetailsBeanList.add(loProposalDetailsBean2);
		moEvaluationService.getEvaluationScores(null, loProposalDetailsBeanList);
	}

	/**
	 * The method will retrieve the External & Internal Evaluators
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testFetchExtAndIntEvaluator() throws ApplicationException
	{
		List<String> loEvaluatorsList = moEvaluationService.fetchExtAndIntEvaluator(moMyBatisSession, msProcurementId);
		assertNotNull(loEvaluatorsList);

	}

	/**
	 * The method will retrieve the External & Internal Evaluators
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testFetchExtAndIntEvaluator1() throws ApplicationException
	{
		String lsProcurementId = "1";
		moEvaluationService.fetchExtAndIntEvaluator(moMyBatisSession, lsProcurementId);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchExtAndIntEvaluator2() throws ApplicationException
	{
		String lsProcurementId = "3";
		moEvaluationService.fetchExtAndIntEvaluator(null, lsProcurementId);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchExtAndIntEvaluator3() throws ApplicationException
	{
		moEvaluationService.fetchExtAndIntEvaluator(moMyBatisSession, null);

	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "178");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
		assertNotNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails1() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = null;
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
		assertNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails2() throws Exception
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails3() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
		assertNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails4() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "5555");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
		assertNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails5() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "1");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, null, lsWobNumber);
		assertNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluatorDetails method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluatorDetails6() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "1");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(moMyBatisSession, aoTaskMap, null);
		assertNull(lsEvaluatorDetails);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchEvaluatorDetailsAppException() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "178");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		String lsEvaluatorDetails = moEvaluationService.fetchEvaluatorDetails(null, aoTaskMap, lsWobNumber);
		assertNotNull(lsEvaluatorDetails);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationReviewScores() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "723");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "241");
		aoHmRequiredProps.put(lsWobNumber, loProcurementMap);
		List<EvaluationBean> loEvalutionBeanList = moEvaluationService.fetchEvaluationReviewScores(moMyBatisSession,
				aoHmRequiredProps, lsWobNumber);
		assertNotNull(loEvalutionBeanList);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationReviewScoresCase1() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "805");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "308");
		aoHmRequiredProps.put(lsWobNumber, loProcurementMap);
		List<EvaluationBean> loEvalutionBeanList = moEvaluationService.fetchEvaluationReviewScores(moMyBatisSession,
				aoHmRequiredProps, lsWobNumber);
		assertFalse(loEvalutionBeanList.size() > 0);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationReviewScoresCase2() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "##");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "241");
		aoHmRequiredProps.put(lsWobNumber, loProcurementMap);
		moEvaluationService.fetchEvaluationReviewScores(moMyBatisSession, aoHmRequiredProps, lsWobNumber);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationReviewScoresCase3() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "723");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "##");
		aoHmRequiredProps.put(lsWobNumber, loProcurementMap);
		moEvaluationService.fetchEvaluationReviewScores(null, aoHmRequiredProps, lsWobNumber);
	}

	@Test(expected = java.lang.Exception.class)
	public void testFetchEvaluationReviewScoresCaseExcep() throws ApplicationException
	{
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		moEvaluationService.fetchEvaluationReviewScores(null, aoHmRequiredProps, null);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationReviewScoresCase4() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		List<EvaluationBean> loEvalutionBeanList = moEvaluationService.fetchEvaluationReviewScores(moMyBatisSession,
				null, lsWobNumber);
		assertNull(loEvalutionBeanList);
	}

	/**
	 * The Method will test fetchEvaluationReviewScores method
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchEvaluationReviewScoresCase5() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, HashMap<String, String>> aoHmRequiredProps = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "723");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "241");
		aoHmRequiredProps.put(lsWobNumber, loProcurementMap);
		List<EvaluationBean> loEvalutionBeanList = moEvaluationService.fetchEvaluationReviewScores(moMyBatisSession,
				aoHmRequiredProps, null);
		assertNull(loEvalutionBeanList);
	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateEvaluationReviewsStatus() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcStatusId("1");
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvalBean.setProposalId(msProposalId);
		loEvaluationBeanList.add(loEvalBean);
		Boolean lbUpdateFlag = moEvaluationService
				.updateEvaluationReviewsStatus(moMyBatisSession, loEvaluationBeanList);
		assertTrue(lbUpdateFlag);
	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateEvaluationReviewsStatusCase1() throws ApplicationException
	{
		Boolean lbUpdateFlag = moEvaluationService.updateEvaluationReviewsStatus(moMyBatisSession, null);
		assertFalse(lbUpdateFlag);
	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateEvaluationReviewsStatusCase2() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbUpdateFlag = moEvaluationService
				.updateEvaluationReviewsStatus(moMyBatisSession, loEvaluationBeanList);
		assertTrue(lbUpdateFlag);
	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationReviewsStatusCase3() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcStatusId("1");
		loEvalBean.setEvaluationStatusId("4");
		loEvalBean.setProposalId("203");
		loEvaluationBeanList.add(loEvalBean);
		moEvaluationService.updateEvaluationReviewsStatus(null, loEvaluationBeanList);

	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationReviewsStatusCase4() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcStatusId("1");
		loEvalBean.setEvaluationStatusId("##");
		loEvalBean.setProposalId("203");
		loEvaluationBeanList.add(loEvalBean);
		moEvaluationService.updateEvaluationReviewsStatus(moMyBatisSession, loEvaluationBeanList);
	}

	/**
	 * The Method will test updateEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationReviewsStatusCase5() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcStatusId("1");
		loEvalBean.setEvaluationStatusId("4");
		loEvalBean.setProposalId("##");
		loEvaluationBeanList.add(loEvalBean);
		moEvaluationService.updateEvaluationReviewsStatus(null, loEvaluationBeanList);
	}

	/**
	 * The Method will test finishEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testFinishEvaluationReviewsStatus() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		EvaluationBean loEvalBean = new EvaluationBean();
		loEvalBean.setProcStatusId("42");
		loEvalBean.setProposalId(msProposalId);
		loEvalBean.setEvaluationStatusId(msEvaluationStatusId);
		loEvaluationBeanList.add(loEvalBean);
		moEvaluationService.finishEvaluationReviewsStatus(moMyBatisSession, loEvaluationBeanList);
	}

	/**
	 * The Method will test finishEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testFinishEvaluationReviewsStatusCase1() throws ApplicationException
	{
		moEvaluationService.finishEvaluationReviewsStatus(moMyBatisSession, null);

	}

	/**
	 * The Method will test finishEvaluationReviewsStatus method
	 * @throws ApplicationException
	 */

	@Test
	public void testFinishEvaluationReviewsStatusCase2() throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = new ArrayList<EvaluationBean>();
		moEvaluationService.finishEvaluationReviewsStatus(moMyBatisSession, loEvaluationBeanList);

	}

	/**
	 * The Method will test updateEvaluationResult method
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateEvaluationResult() throws ApplicationException
	{
		String lsStatus = "Scores Accepted";
		String lsUserId = "city_43";
		String lsScore = "5";
		Boolean lbUpdateFlag = moEvaluationService.updateEvaluationResult(moMyBatisSession, lsStatus, lsUserId,
				lsScore, msProposalId, msEvaluationPoolMappingId);
		assertTrue(lbUpdateFlag);
	}

	/**
	 * The Method will test updateEvaluationResult method
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateEvaluationResultCase1() throws ApplicationException
	{
		String lsStatus = "Scores Accepted";
		String lsUserId = "city_43";
		String lsScore = "5";
		Boolean lbUpdateFlag = moEvaluationService.updateEvaluationResult(moMyBatisSession, lsStatus, lsUserId,
				lsScore, msProposalIdNonResult, msEvaluationPoolMappingId);
		assertTrue(lbUpdateFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationResultCase2() throws ApplicationException
	{
		String lsStatus = "Scores Accepted";
		String lsUserId = "city_43";
		String lsScore = "5";
		Boolean lbUpdateFlag = moEvaluationService.updateEvaluationResult(null, lsStatus, lsUserId, lsScore,
				msProposalId, msEvaluationPoolMappingId);
		assertTrue(lbUpdateFlag);
	}

	/**
	 * The Method will test getSendEvaluationTasksVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test
	public void testGetSendEvaluationTasksVisibiltyStatus() throws ApplicationException
	{

		EvaluationBean loEvaluationBean = new EvaluationBean();
		loEvaluationBean.setUserRole(HHSConstants.AGENCY_ACCO_MANAGER);
		loEvaluationBean.setProcurementId("700");
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);

		Map<String, Boolean> loFlagMap = moEvaluationService.getSendEvaluationTasksVisibiltyStatus(moMyBatisSession,
				"1", loEvaluationBean);
		assertNotNull(loFlagMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetSendEvaluationTasksVisibiltyStatusCase2() throws ApplicationException
	{

		EvaluationBean loEvaluationBean = new EvaluationBean();
		loEvaluationBean.setUserRole(HHSConstants.AGENCY_ACCO_MANAGER);
		loEvaluationBean.setProcurementId("700");
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);

		Map<String, Boolean> loFlagMap = moEvaluationService.getSendEvaluationTasksVisibiltyStatus(null, "1",
				loEvaluationBean);
		assertNotNull(loFlagMap);
	}

	/**
	 * This method tests the execution of confirmReturnForAction method
	 * @throws Exception
	 */
	@Test
	public void testConfirmReturnForActionCase1() throws ApplicationException, Exception
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementId);
		loStatusMap.put(HHSConstants.PROPOSAL_ID_KEY, msProposalId);
		loStatusMap.put("asStatusId", "19");
		Boolean loUpdateConfirmReturnForAction = moEvaluationService.confirmReturnForAction(moMyBatisSession,
				loStatusMap);
		assertTrue(loUpdateConfirmReturnForAction);

	}

	/**
	 * This method tests the execution of confirmReturnForAction method
	 * @throws Exception
	 */
	@Test
	public void testConfirmReturnForActionCase2() throws ApplicationException, Exception
	{
		Map<String, String> loStatusMap = null;
		Boolean loUpdateConfirmReturnForAction = moEvaluationService.confirmReturnForAction(moMyBatisSession,
				loStatusMap);
		assertFalse(loUpdateConfirmReturnForAction);

	}

	/**
	 * This method tests the execution of confirmReturnForAction method
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testConfirmReturnForActionApplicationException() throws ApplicationException, Exception
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap.put("asProcurementId", "###");
		loStatusMap.put("asProposalId", "2");
		loStatusMap.put("asStatusId", "19");
		moEvaluationService.confirmReturnForAction(null, loStatusMap);

	}

	/**
	 * This method tests the execution of confirmReturnForAction method
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testConfirmReturnForActionException() throws ApplicationException, Exception
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap.put("procurementId", "3");
		loStatusMap.put("asProposalId", "2");
		loStatusMap.put("asStatusId", "19");
		moEvaluationService.confirmReturnForAction(moMyBatisSession, loStatusMap);

	}

	/**
	 * This method tests the execution of modifyProposalStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testModifyProposalStatusCase1() throws ApplicationException, Exception
	{
		String lsProposalId = msProposalId;
		Boolean loUpdateProposalFlag = moEvaluationService.modifyProposalStatus(moMyBatisSession, lsProposalId);
		assertTrue(loUpdateProposalFlag);

	}

	/**
	 * This method tests the execution of modifyProposalStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testModifyProposalStatusCase2() throws ApplicationException, Exception
	{
		String lsProposalId = null;
		Boolean loUpdateProposalFlag = moEvaluationService.modifyProposalStatus(moMyBatisSession, lsProposalId);
		assertTrue(!loUpdateProposalFlag);

	}

	/**
	 * This method tests the execution of modifyProposalStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testModifyProposalStatusApplicationException() throws ApplicationException, Exception
	{
		String lsProposalId = "###";
		moEvaluationService.modifyProposalStatus(moMyBatisSession, lsProposalId);

	}

	/**
	 * This method tests the execution of modifyProposalStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testModifyProposalStatusException() throws Exception
	{
		String lsProposalId = "###";
		moEvaluationService.modifyProposalStatus(moMyBatisSession, lsProposalId);

	}

	/**
	 * This method tests the execution of insertEvaluationStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testInsertEvaluationStatusCase1() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		List<EvaluationDetailBean> loIntExtProposalDetails = new ArrayList<EvaluationDetailBean>();
		EvaluationDetailBean loEvaluationDetailBean = new EvaluationDetailBean();
		loEvaluationDetailBean.setProposalId(msProposalId);
		loEvaluationDetailBean.setOrganizationId("test");
		loEvaluationDetailBean.setStatusId("41");
		loEvaluationDetailBean.setProcStatusId("");
		loEvaluationDetailBean.setProcurementId(msProcurementId);
		loIntExtProposalDetails.add(loEvaluationDetailBean);
		loProcMap.put("asProcurementId", msProcurementId);
		loProcMap.put("lsUserId", "agency_14");
		Boolean loUpdateEvaluationStatus = moEvaluationService.insertEvaluationStatus(moMyBatisSession, loProcMap,
				loIntExtProposalDetails);
		assertTrue(loUpdateEvaluationStatus);

	}

	/**
	 * This method tests the execution of insertEvaluationStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertEvaluationStatusApplicationException() throws ApplicationException
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		List<EvaluationDetailBean> loIntExtProposalDetails = new ArrayList<EvaluationDetailBean>();
		EvaluationDetailBean loEvaluationDetailBean = new EvaluationDetailBean();
		loEvaluationDetailBean.setProposalId("3");
		loEvaluationDetailBean.setOrganizationId("accenture");
		loEvaluationDetailBean.setStatusId("41");
		loEvaluationDetailBean.setProcStatusId("");
		loIntExtProposalDetails.add(loEvaluationDetailBean);
		loProcMap.put("asProcurementId", "3");
		loProcMap.put("lsUserId", "agency_14");
		moEvaluationService.insertEvaluationStatus(null, loProcMap, loIntExtProposalDetails);

	}

	/**
	 * This method tests the execution of insertEvaluationStatus method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testInsertEvaluationStatusException() throws Exception
	{
		HashMap<String, String> loProcMap = new HashMap<String, String>();
		List<EvaluationDetailBean> loIntExtProposalDetails = new ArrayList<EvaluationDetailBean>();
		EvaluationDetailBean loEvaluationDetailBean = new EvaluationDetailBean();
		loEvaluationDetailBean.setProposalId("3");
		loEvaluationDetailBean.setOrganizationId("accenture");
		loEvaluationDetailBean.setStatusId("41");
		loEvaluationDetailBean.setProcStatusId("");
		loIntExtProposalDetails.add(loEvaluationDetailBean);
		loProcMap.put("procurementId", "3");
		loProcMap.put("lsUserId", "agency_14");
		moEvaluationService.insertEvaluationStatus(moMyBatisSession, loProcMap, loIntExtProposalDetails);

	}

	/**
	 * This method tests Award Amount value corresponding to the procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardAmountCase1() throws ApplicationException
	{
		Map<String, String> loAwardAmount = moEvaluationService.fetchAwardAmount(moMyBatisSession,
				msProcurementIdForAwards);
		assertNotNull(loAwardAmount);

	}

	/**
	 * This method tests Award Amount value corresponding to the procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardAmountCase2() throws ApplicationException
	{
		String lsProcurementId = "###";
		moEvaluationService.fetchAwardAmount(moMyBatisSession, lsProcurementId);

	}

	/**
	 * This method tests Award Amount value corresponding to the procurement Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchAwardAmountCaseException() throws Exception
	{
		String lsProcurementId = "###";
		moEvaluationService.fetchAwardAmount(moMyBatisSession, lsProcurementId);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchAwardAmountCase3() throws ApplicationException
	{
		String lsProcurementId = "3";
		moEvaluationService.fetchAwardAmount(null, lsProcurementId);

	}

	/**
	 * This method tests Award Review status corresponding to the procurement
	 * Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateAwardReviewStatusCase1() throws ApplicationException
	{
		Map<String, Object> loProcurementInfoMap = new HashMap<String, Object>();
		loProcurementInfoMap.put(HHSConstants.AWARD_STATUS_ID, msAwardReviewStatusInProgress);
		loProcurementInfoMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementIdForAwards);
		loProcurementInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvaluationPoolMappingId);
		loProcurementInfoMap.put("userId", "agency_14");
		Boolean loProcurementStatus = true;
		loProcurementStatus = moEvaluationService.updateAwardReviewStatus(moMyBatisSession, loProcurementInfoMap);
		assertTrue(loProcurementStatus);

	}

	/**
	 * This method tests Award Review status corresponding to the procurement
	 * Id.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testUpdateAwardReviewStatusCase2() throws ApplicationException
	{
		Map<String, Object> loProcurementInfoMap = new HashMap<String, Object>();
		loProcurementInfoMap.put(HHSConstants.AWARD_STATUS_ID, msAwardReviewStatusInProgress);
		loProcurementInfoMap.put(HHSConstants.PROCUREMENT_ID_KEY, "191");
		loProcurementInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, "263");
		loProcurementInfoMap.put("userId", "agency_14");
		Boolean loProcurementStatus = true;
		loProcurementStatus = moEvaluationService.updateAwardReviewStatus(moMyBatisSession, loProcurementInfoMap);
		assertTrue(loProcurementStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testUpdateAwardReviewStatusCase3() throws ApplicationException
	{
		Map<String, Object> loProcurementInfoMap = new HashMap<String, Object>();
		loProcurementInfoMap.put(HHSConstants.AWARD_STATUS_ID, msAwardReviewStatusInProgress);
		loProcurementInfoMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementIdForAwards);
		loProcurementInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvaluationPoolMappingId);
		loProcurementInfoMap.put("userId", "agency_14");
		Boolean loProcurementStatus = true;
		loProcurementStatus = moEvaluationService.updateAwardReviewStatus(null, loProcurementInfoMap);
		assertTrue(loProcurementStatus);

	}

	/**
	 * The Method will test downloadDBDDocumentsAndZip method.
	 * @throws ApplicationException
	 */
	@Test
	public void testDownloadDBDDocumentsAndZip() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String, String>>();
		String lsUserId = "";
		String lsContextPath = "";
		String lsZipFilePath = moEvaluationService.downloadDBDDocumentsAndZip(loUserSession, loDBDDocList,
				msProcurementId, lsUserId, lsContextPath, "");
		assertNull(lsZipFilePath);
	}

	@Test
	public void testDownloadDBDDocumentsAndZip2() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String, String>>();
		String lsProcurementId = "###";
		String lsUserId = "###";
		String lsContextPath = "###";
		String lsZipFilePath = moEvaluationService.downloadDBDDocumentsAndZip(loUserSession, loDBDDocList,
				lsProcurementId, lsUserId, lsContextPath, "");
		assertNull(lsZipFilePath);
	}

	@Test
	public void testDownloadDBDDocumentsAndZip3() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String, String>>();
		String lsProcurementId = "123";
		String lsUserId = "";
		String lsContextPath = "";
		String lsZipFilePath = moEvaluationService.downloadDBDDocumentsAndZip(loUserSession, loDBDDocList,
				lsProcurementId, lsUserId, lsContextPath, "");
		assertNull(lsZipFilePath);
	}

	@Test
	public void testDownloadDBDDocumentsAndZip4() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String, String>>();
		String lsProcurementId = "";
		String lsUserId = "city_142";
		String lsContextPath = "";
		String lsZipFilePath = moEvaluationService.downloadDBDDocumentsAndZip(loUserSession, loDBDDocList,
				lsProcurementId, lsUserId, lsContextPath, "");
		assertNull(lsZipFilePath);
	}

	@Test
	public void testDownloadDBDDocumentsAndZip5() throws ApplicationException
	{
		P8UserSession loUserSession = new P8UserSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String, String>>();
		String lsProcurementId = "";
		String lsUserId = "";
		String lsContextPath = "";
		String lsZipFilePath = moEvaluationService.downloadDBDDocumentsAndZip(loUserSession, loDBDDocList,
				lsProcurementId, lsUserId, lsContextPath, "");
		assertNull(lsZipFilePath);
	}

	/**
	 * The Method will test fetchEvaluationResultsCount method.
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationResultsCount() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsValidUser(true);
		loEvalBean.setProposalTitle("Proposal 1");
		loEvalBean.setOrganizationName("test_manager");
		loEvalBean.setProposalId(msProposalIdForScores1);
		loEvalBean.setProcurementId(msProcurementIdForScores1);
		loEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForScores1);
		moEvaluationService.fetchEvaluationResultsCount(moMyBatisSession, loEvalBean);
	}

	/**
	 * The Method will test fetchEvaluationResultsCount method.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsCountApplicationException() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> loProposalStatusList = new ArrayList<String>();
		loEvalBean.setIsValidUser(true);
		loEvalBean.setProcurementId("775");
		loProposalStatusList.add("20");
		loEvalBean.setProposalStatusList(loProposalStatusList);
		loEvalBean.setScoreRangeFrom(1);
		loEvalBean.setScoreRangeTo(100);
		loEvalBean.setProposalTitle("Accenture Proposal");
		loEvalBean.setOrganizationName("HHS");
		moEvaluationService.fetchEvaluationResultsCount(null, loEvalBean);
	}

	/**
	 * The Method will test fetchProposalDetailsToLaunchEvaluationTask method.
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchFinalizeResultsVisibiltyStatus() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId(msProcurementIdForScores1);
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		loEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForScores1);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(moMyBatisSession, loEvalBean);
	}

	/**
	 * The Method will test fetchProposalDetailsToLaunchEvaluationTask method.
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchFinalizeResultsVisibiltyStatusCase1() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId("131");
		loEvalBean.setEvaluationPoolMappingId("163");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(moMyBatisSession, loEvalBean);
	}

	/**
	 * The Method will test fetchProposalDetailsToLaunchEvaluationTask method.
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchFinalizeResultsVisibiltyStatusCase5() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId("155");
		loEvalBean.setEvaluationPoolMappingId("213");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(moMyBatisSession, loEvalBean);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchFinalizeResultsVisibiltyStatusCase4() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId("");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(moMyBatisSession, loEvalBean);
	}

	/**
	 * The Method will test fetchFinalizeResultsVisibiltyStatus method.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFinalizeResultsVisibiltyStatusCase2() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId("100");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, loEvalBean);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchFinalizeResultsVisibiltyStatusCase3() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId("100");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
	}

	/**
	 * The Method will test saveEvaluationDetails method.
	 * @throws Exception
	 */
	@Test
	public void testSaveEvaluationDetails() throws Exception
	{
		Map<String, List<Evaluator>> loParamMap = new HashMap<String, List<Evaluator>>();
		Integer loEvaluatorsCountNew = 3;
		Boolean lbIsEvaluationSend = true;
		Integer loEvaluatorsCountOld = 2;
		List<Evaluator> loInternalEvaluationsList = new ArrayList<Evaluator>();
		List<Evaluator> loExternalEvaluationsList = new ArrayList<Evaluator>();
		loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loParamMap.put(HHSConstants.INTERNAL_LIST, loInternalEvaluationsList);
		loParamMap.put(HHSConstants.EXTERNAL_LIST, loExternalEvaluationsList);
		Map<String, Evaluator> loAllEvaluatorsMap = new HashMap<String, Evaluator>();
		Map<String, String> loReturnMap = moEvaluationService.saveEvaluationDetails(moMyBatisSession, loParamMap,
				msProcurementIdSendEvalTask, loEvaluatorsCountNew, lbIsEvaluationSend, loEvaluatorsCountOld,
				loInternalEvaluationsList, loExternalEvaluationsList, loAllEvaluatorsMap, msEvaluationPoolMappingId);
		assertNotNull(loReturnMap);
	}

	@Test
	public void testSaveEvaluationDetailsCase2() throws Exception
	{
		Map<String, List<Evaluator>> loParamMap = new HashMap<String, List<Evaluator>>();
		Integer loEvaluatorsCountNew = 2;
		Boolean lbIsEvaluationSend = true;
		Integer loEvaluatorsCountOld = 3;
		List<Evaluator> loInternalEvaluationsList = new ArrayList<Evaluator>();
		List<Evaluator> loExternalEvaluationsList = new ArrayList<Evaluator>();
		loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loParamMap.put(HHSConstants.INTERNAL_LIST, loInternalEvaluationsList);
		loParamMap.put(HHSConstants.EXTERNAL_LIST, loExternalEvaluationsList);
		Map<String, Evaluator> loAllEvaluatorsMap = new HashMap<String, Evaluator>();
		Map<String, String> loReturnMap = moEvaluationService.saveEvaluationDetails(moMyBatisSession, loParamMap,
				msProcurementIdSendEvalTask, loEvaluatorsCountNew, lbIsEvaluationSend, loEvaluatorsCountOld,
				loInternalEvaluationsList, loExternalEvaluationsList, loAllEvaluatorsMap, msEvaluationPoolMappingId);
		assertNotNull(loReturnMap);
	}

	@Test
	public void testSaveEvaluationDetailsCase3() throws Exception
	{
		Map<String, List<Evaluator>> loParamMap = new HashMap<String, List<Evaluator>>();
		Integer loEvaluatorsCountNew = 2;
		Boolean lbIsEvaluationSend = true;
		Integer loEvaluatorsCountOld = 2;
		List<Evaluator> loInternalEvaluationsList = new ArrayList<Evaluator>();
		List<Evaluator> loExternalEvaluationsList = new ArrayList<Evaluator>();
		loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loParamMap.put(HHSConstants.INTERNAL_LIST, loInternalEvaluationsList);
		loParamMap.put(HHSConstants.EXTERNAL_LIST, loExternalEvaluationsList);
		Map<String, Evaluator> loAllEvaluatorsMap = new HashMap<String, Evaluator>();
		Map<String, String> loReturnMap = moEvaluationService.saveEvaluationDetails(moMyBatisSession, loParamMap,
				msProcurementIdSendEvalTask, loEvaluatorsCountNew, lbIsEvaluationSend, loEvaluatorsCountOld,
				loInternalEvaluationsList, loExternalEvaluationsList, loAllEvaluatorsMap, msEvaluationPoolMappingId);
		assertNotNull(loReturnMap);
	}

	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationDetailsCase4() throws Exception
	{
		Map<String, List<Evaluator>> loParamMap = new HashMap<String, List<Evaluator>>();
		Integer loEvaluatorsCountNew = 3;
		Boolean lbIsEvaluationSend = false;
		Integer loEvaluatorsCountOld = 2;
		List<Evaluator> loInternalEvaluationsList = new ArrayList<Evaluator>();
		List<Evaluator> loExternalEvaluationsList = new ArrayList<Evaluator>();
		loInternalEvaluationsList = moEvaluationService.getInternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loExternalEvaluationsList = moEvaluationService.getExternalEvaluationsList(moMyBatisSession,
				msProcurementIdSendEvalTask, msEvaluationPoolMappingId, true);
		loParamMap.put(HHSConstants.INTERNAL_LIST, loInternalEvaluationsList);
		loParamMap.put(HHSConstants.EXTERNAL_LIST, loExternalEvaluationsList);
		Map<String, Evaluator> loAllEvaluatorsMap = new HashMap<String, Evaluator>();
		Map<String, String> loReturnMap = moEvaluationService.saveEvaluationDetails(moMyBatisSession, loParamMap,
				msProcurementIdSendEvalTask, loEvaluatorsCountNew, lbIsEvaluationSend, loEvaluatorsCountOld,
				loInternalEvaluationsList, loExternalEvaluationsList, loAllEvaluatorsMap, msEvaluationPoolMappingId);
		assertNotNull(loReturnMap);
	}

	/**
	 * The Method will test fetchUpdateResultsVisibiltyStatus method.
	 * @throws Exception
	 */
	@Test
	public void testFetchUpdateResultsVisibiltyStatus() throws Exception
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("100");
		loEvalBean.setUserRole(HHSConstants.ACCO_ADMIN_STAFF_ROLE);
		loEvalBean.setProcurementId(msProcurementIdForScores1);
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		loEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForScores1);
		String lsUpdateResultStatus = moEvaluationService.fetchUpdateResultsVisibiltyStatus(moMyBatisSession,
				loEvalBean);
		assertNotNull(lsUpdateResultStatus);
	}

	/**
	 * The Method will test fetchUpdateResultsVisibiltyStatus method.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchUpdateResultsVisibiltyStatusCase1() throws Exception
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setUserRole(HHSConstants.ACCO_STAFF_ROLE);
		loEvalBean.setProcurementId("805");
		moEvaluationService.fetchUpdateResultsVisibiltyStatus(null, loEvalBean);
	}

	/**
	 * The Method will test fetchProcurementDetailsForAwardWF method.
	 * @throws Exception
	 */
	@Test
	public void testFetchProcurementDetailsForAwardWF() throws Exception
	{
		HashMap<String, Object> loHmReqProposMap = new HashMap<String, Object>();
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvaluationPoolMappingId);
		moEvaluationService.fetchProcurementDetailsForAwardWF(moMyBatisSession, loDataMap, loHmReqProposMap);
		assertNotNull(loHmReqProposMap);
	}

	/**
	 * The Method will test fetchProcurementDetailsForAwardWF method.
	 * @throws Exception
	 */
	@Test
	public void testFetchProcurementDetailsForAwardWFCase1() throws Exception
	{
		HashMap<String, Object> loHmReqProposMap = new HashMap<String, Object>();
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, "1");
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvaluationPoolMappingId);
		moEvaluationService.fetchProcurementDetailsForAwardWF(moMyBatisSession, loDataMap, loHmReqProposMap);
		assertNotNull(loHmReqProposMap);
	}

	/**
	 * The Method will test updateProposalStatus method.
	 * @throws Exception
	 */
	@Test
	public void testUpdateProposalStatus() throws Exception
	{
		String lsUserId = "city_43";
		Boolean loUpdateEvaluationFlag = moEvaluationService.updateProposalStatus(moMyBatisSession, msProcurementId,
				lsUserId, msEvaluationPoolMappingId);
		assertTrue(loUpdateEvaluationFlag);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationResultsSelectionsCase1() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAccoUser("true");
		loEvalBean.setIsFiltered("true");
		loEvalBean.setProposalId(msProposalIdForScores1);
		loEvalBean.setProposalTitle("Proposal 1");
		loEvalBean.setProcurementId(msProcurementIdForScores1);
		loEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForScores1);
		loEvalBean.setUserRole("abc");
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);

	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsSelectionsCase3() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAccoUser("true");
		loEvalBean.setProposalId("3");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		loEvalBean.setFilteredCheck(false);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);

	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsSelectionsCase4() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAccoUser("true");
		loEvalBean.setProposalId("3");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		loEvalBean.setFilteredCheck(true);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsSelectionsCase5() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAgencyAccoUser("true");
		loEvalBean.setIsFiltered("true");
		loEvalBean.setProposalId("3");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		String lsProposalStatusId = "23";
		List<String> loList = new ArrayList<String>();
		loList.add(lsProposalStatusId);
		loEvalBean.setProposalStatusIdList(loList);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsSelectionsCase6() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAgencyAccoUser("true");
		loEvalBean.setProposalId("3");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		String lsProposalStatusId = "23";
		List<String> loList = new ArrayList<String>();
		loList.add(lsProposalStatusId);
		loEvalBean.setProposalStatusIdList(loList);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationResultsSelectionsCase7() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsAgencyAccoUser("true");
		loEvalBean.setProposalId("3");
		loEvalBean.setIsFiltered("true");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		String lsProposalStatusId = "";
		List<String> loList = new ArrayList<String>();
		loList.add(lsProposalStatusId);
		loEvalBean.setProposalStatusIdList(loList);
		loEvalBean.setEvaluationPoolMappingId("163");

		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults != null);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationResultsSelectionsCase8() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setProposalId("3");
		loEvalBean.setIsFiltered("true");
		loEvalBean.setProposalTitle("BProposal Title -23");
		loEvalBean.setProcurementId("3");
		loEvalBean.setUserRole("abc");
		String lsProposalStatusId = "";
		List<String> loList = new ArrayList<String>();
		loList.add(lsProposalStatusId);
		loEvalBean.setProposalStatusIdList(loList);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(
				moMyBatisSession, loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults == null);
	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationResultsSelectionsCase14() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsValidUser(true);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(null,
				loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults == null);

	}

	/**
	 * This method tests the execution of fetchEvaluationResultsSelections
	 * method
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationResultsSelectionsCase15() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setIsValidUser(false);
		EvaluationBean loEvalAwardReviewStatusBean = new EvaluationBean();
		List<EvaluationFilterBean> loEvalResults = moEvaluationService.fetchEvaluationResultsSelections(null,
				loEvalBean, loEvalAwardReviewStatusBean);
		assertTrue(loEvalResults == null);

	}

	/**
	 * The Method will test Get Close Button Visibilty Status method.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCloseButtonVisibiltyStatusCase1() throws ApplicationException, Exception
	{
		Map<String, String> loCloseButtonVisibleStatus = moEvaluationService.getCloseButtonVisibiltyStatus(
				moMyBatisSession, "252", "324");
		assertNotNull(loCloseButtonVisibleStatus);

	}

	/**
	 * The Method will test Get Close Button Visibilty Status method.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testupdateModifiedFlag1() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		Map<String, Object> loAwardStatusId = new HashMap<String, Object>();
		loAwardStatusId.put(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY, new BigDecimal(msAwardReviewStatusReturned));
		loAwardStatusId.put(HHSConstants.AWARD_APPROVAL_DATE, new Date());
		Integer loCount = moEvaluationService.updateModifiedFlag(moMyBatisSession, loAwardStatusId, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	@Test()
	public void testupdateModifiedFlag2() throws ApplicationException
	{
		EvaluationBean aoEvalBean = null;
		Map<String, Object> loAwardStatusId = new HashMap<String, Object>();
		loAwardStatusId.put(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY, new BigDecimal(msAwardReviewStatusReturned));
		loAwardStatusId.put(HHSConstants.AWARD_APPROVAL_DATE, new Date());
		moEvaluationService.updateModifiedFlag(moMyBatisSession, loAwardStatusId, aoEvalBean);

	}

	@Test()
	public void testupdateModifiedFlag3() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		Map<String, Object> loAwardStatusId = new HashMap<String, Object>();
		loAwardStatusId.put(HHSConstants.AWARD_APPROVAL_DATE, new Date());
		Integer loCount = moEvaluationService.updateModifiedFlag(moMyBatisSession, loAwardStatusId, aoEvalBean);
		assertNull(loCount);
	}

	@Test()
	public void testupdateModifiedFlag4() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		Map<String, Object> loAwardStatusId = new HashMap<String, Object>();
		loAwardStatusId.put(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY, null);
		loAwardStatusId.put(HHSConstants.AWARD_APPROVAL_DATE, new Date());
		Integer loCount = moEvaluationService.updateModifiedFlag(moMyBatisSession, loAwardStatusId, aoEvalBean);
		assertNull(loCount);
	}

	@Test(expected = ApplicationException.class)
	public void testupdateModifiedFlag5() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProposalId(msProposalId);
		Map<String, Object> loAwardStatusId = new HashMap<String, Object>();
		loAwardStatusId.put(HHSConstants.AWARD_REVIEW_STATUS_ID_KEY, new BigDecimal(msAwardReviewStatusReturned));
		loAwardStatusId.put(HHSConstants.AWARD_APPROVAL_DATE, new Date());
		Integer loCount = moEvaluationService.updateModifiedFlag(null, loAwardStatusId, aoEvalBean);
		assertNotNull(loCount);
		assertTrue(loCount > 0);

	}

	@Test
	public void testGetDBDDocsList() throws ApplicationException
	{
		List<Map<String, String>> loDBDDocList = null;
		loDBDDocList = moEvaluationService.getDBDDocsList(moMyBatisSession, msProcurementId, msEvaluationPoolMappingId);
		assertNotNull(loDBDDocList);

	}

	@Test
	public void testFetchNoOfProviders() throws ApplicationException
	{
		Map<String, Object> loInputParam = new HashMap<String, Object>();
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementId);
		loInputParam.put(HHSConstants.PROCUREMENT_STATUS_KEY, "23");
		int liNoOfProviders = moEvaluationService.fetchNoOfProviders(moMyBatisSession, loInputParam);
		assertTrue(liNoOfProviders != 0);
	}

	@Test
	public void testFetchNoOfProposals() throws ApplicationException
	{

		HashMap<String, Object> loInputParam = new HashMap<String, Object>();
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementId);
		loInputParam.put(HHSConstants.PROCUREMENT_STATUS_KEY, "23");
		Integer loNoOfProposals = moEvaluationService.fetchNoOfProposals(moMyBatisSession, loInputParam);
		assertTrue(loNoOfProposals > 0);
	}

	@Test
	public void testFetchAwardStatusId() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProcurementId(msProcurementIdForAwardReturned);
		aoEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForAwardReturned);
		moEvaluationService.fetchAwardStatusId(moMyBatisSession, aoEvalBean);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchAwardStatusIdCase1() throws ApplicationException
	{
		EvaluationBean aoEvalBean = new EvaluationBean();
		aoEvalBean.setProcurementId(msProcurementIdForAwardReturned);
		aoEvalBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForAwardReturned);
		moEvaluationService.fetchAwardStatusId(null, aoEvalBean);

	}

	@Test
	public void testFetchProposalCount() throws ApplicationException
	{
		EvaluationBean loEvaluationBean = new EvaluationBean();
		List<String> proposalStatusList = new ArrayList<String>();
		List<String> evaluationStatusList = new ArrayList<String>();
		proposalStatusList.add("1");
		evaluationStatusList.add("41");
		loEvaluationBean.setUserRole("agency");
		loEvaluationBean.setProcurementId(msProcurementId);
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		loEvaluationBean.setProposalTitle("Proposal 1");
		loEvaluationBean.setOrganizationName("test_manager");
		loEvaluationBean.setProposalStatusList(proposalStatusList);
		loEvaluationBean.setEvaluationStatusList(evaluationStatusList);
		Integer loProposalCount = moEvaluationService.fetchProposalCount(moMyBatisSession, loEvaluationBean);

	}

	@Test
	public void testFetchProposalCountCase1() throws ApplicationException
	{
		EvaluationBean loEvaluationBean = new EvaluationBean();
		List<String> proposalStatusList = new ArrayList<String>();
		List<String> evaluationStatusList = new ArrayList<String>();
		proposalStatusList.add("1");
		evaluationStatusList.add("41");
		loEvaluationBean.setUserRole("agency");
		loEvaluationBean.setProcurementId(msProcurementId);
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		loEvaluationBean.setProposalStatusList(proposalStatusList);
		loEvaluationBean.setEvaluationStatusList(evaluationStatusList);
		Integer loProposalCount = moEvaluationService.fetchProposalCount(moMyBatisSession, loEvaluationBean);

	}

	@Test
	public void testFetchProposalCountCase3() throws ApplicationException
	{
		EvaluationBean loEvaluationBean = new EvaluationBean();
		List<String> proposalStatusList = new ArrayList<String>();
		List<String> evaluationStatusList = new ArrayList<String>();
		proposalStatusList.add("20");
		evaluationStatusList.add("41");
		loEvaluationBean.setUserRole("agency");
		loEvaluationBean.setProcurementId(msProcurementId);
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		loEvaluationBean.setProposalStatusList(proposalStatusList);
		loEvaluationBean.setEvaluationStatusList(evaluationStatusList);
		Integer loProposalCount = moEvaluationService.fetchProposalCount(moMyBatisSession, loEvaluationBean);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalCountCase2() throws ApplicationException
	{
		EvaluationBean loEvaluationBean = new EvaluationBean();
		List<String> proposalStatusList = new ArrayList<String>();
		List<String> evaluationStatusList = new ArrayList<String>();
		proposalStatusList.add("20");
		evaluationStatusList.add("41");
		loEvaluationBean.setUserRole("agency");
		loEvaluationBean.setProcurementId("#$$$##$");
		loEvaluationBean.setEvaluationPoolMappingId(msEvaluationPoolMappingId);
		loEvaluationBean.setProposalStatusList(proposalStatusList);
		loEvaluationBean.setEvaluationStatusList(evaluationStatusList);
		Integer loProposalCount = moEvaluationService.fetchProposalCount(moMyBatisSession, loEvaluationBean);
	}

	@Test
	public void testFetchSelectionCommentsForAwardTask() throws ApplicationException
	{
		HashMap<String, String> loCommentsMap = new HashMap<String, String>();
		loCommentsMap.put("entityId", "3");
		loCommentsMap.put("entityType", "Evaluation");
		EvaluationBean loEvaluationBean = moEvaluationService.fetchSelectionCommentsForAwardTask(moMyBatisSession,
				loCommentsMap);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchSelectionCommentsForAwardTaskCase1() throws ApplicationException
	{
		HashMap<String, String> loCommentsMap = new HashMap<String, String>();
		loCommentsMap.put("entityId", "#@##");
		loCommentsMap.put("entityType", "$#@#dws");
		EvaluationBean loEvaluationBean = moEvaluationService.fetchSelectionCommentsForAwardTask(moMyBatisSession,
				loCommentsMap);
		assertNull(loEvaluationBean);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchSelectionCommentsForAwardTaskCase2() throws ApplicationException
	{
		HashMap<String, String> loCommentsMap = new HashMap<String, String>();
		loCommentsMap.put("entityId", "3");
		loCommentsMap.put("entityType", "Evaluation");
		moEvaluationService.fetchSelectionCommentsForAwardTask(null, loCommentsMap);
	}

	@Test
	public void testFetchEvaluationScores() throws ApplicationException
	{
		Map<String, String> loProposalDetails = new HashMap<String, String>();
		loProposalDetails.put("asProcurementId", "805");
		loProposalDetails.put("asProposalId", "308");
		List<EvaluationBean> loEvalutionBeanList = moEvaluationService.fetchEvaluationScores(moMyBatisSession,
				loProposalDetails);
		assertNotNull(loEvalutionBeanList);
	}

	@Test
	public void testFetchAwardAppDate() throws ApplicationException
	{
		List<EvaluationBean> loEvalResults = new ArrayList<EvaluationBean>();
		EvaluationBean loEvaluationBean = new EvaluationBean();
		loEvalResults.add(loEvaluationBean);
		EvaluationFilterBean loEvalFilterBean = new EvaluationFilterBean();
		loEvalFilterBean.setProcurementId(msProcurementIdForAwards);
		loEvalFilterBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForAward);
		List<EvaluationBean> loEvalResults1 = moEvaluationService.fetchAwardAppDate(moMyBatisSession, loEvalFilterBean,
				loEvalResults);
		assertNotNull(loEvalResults1);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchAwardAppDateCase2() throws ApplicationException
	{
		List<EvaluationBean> loEvalResults = new ArrayList<EvaluationBean>();
		EvaluationBean loEvaluationBean = new EvaluationBean();
		loEvalResults.add(loEvaluationBean);
		EvaluationFilterBean loEvalFilterBean = new EvaluationFilterBean();
		loEvalFilterBean.setProcurementId(msProcurementIdForAwards);
		loEvalFilterBean.setEvaluationPoolMappingId(msEvaluationPoolMappingIdForAward);
		List<EvaluationBean> loEvalResults1 = moEvaluationService.fetchAwardAppDate(null, loEvalFilterBean,
				loEvalResults);
		assertNotNull(loEvalResults1);

	}

	/**
	 * This method tests the execution of fetchEvaluatorsList method and
	 * determines whether or not the list of evaluators is getting generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluatorsListCase1() throws ApplicationException
	{
		List<EvaluationDetailBean> loEvaluatorsList = moEvaluationService.fetchEvaluatorsList(moMyBatisSession,
				msProcurementId, msEvaluationPoolMappingId);
		assertNotNull(loEvaluatorsList);

	}

	/**
	 * This method tests the execution of fetchUserEmailIds method and
	 * determines whether or not the list of user email ids is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchUserEmailIdsCase1() throws ApplicationException
	{
		String lsProposalId = msProposalId;
		String lsProcurementTitle = "proc 4th mar";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moEvaluationService.fetchUserEmailIds(moMyBatisSession, lsProposalId, lsProcurementTitle,
				loNotificationMap);
		assertTrue(loNotificationMap != null);

	}

	/**
	 * This method tests the execution of fetchUserEmailIds method and
	 * determines whether or not the list of user email ids is getting generated
	 * when the proposal Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchUserEmailIdsCase2() throws ApplicationException
	{
		String lsProposalId = null;
		String lsProcurementTitle = "abc";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moEvaluationService.fetchUserEmailIds(moMyBatisSession, lsProposalId, lsProcurementTitle,
				loNotificationMap);
		assertTrue(loNotificationMap.get("userId") == null);

	}

	/**
	 * This method tests the execution of fetchUserEmailIds method and
	 * determines whether or not the list of user email ids is getting generated
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchUserEmailIdsCase3() throws ApplicationException
	{
		String lsProposalId = "7";
		String lsProcurementTitle = "abc";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap = moEvaluationService.fetchUserEmailIds(moMyBatisSession, lsProposalId, lsProcurementTitle,
				loNotificationMap);
		assertTrue(loNotificationMap.get("userId") == null);

	}

	/**
	 * This method tests the execution of fetchUserEmailIds method and
	 * determines whether or not the list of user email ids is getting generated
	 * when the proposal Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchUserEmailIdsCase4() throws ApplicationException
	{
		String lsProposalId = "7";
		String lsProcurementTitle = "abc";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moEvaluationService.fetchUserEmailIds(null, lsProposalId, lsProcurementTitle, loNotificationMap);

	}

	/**
	 * This method tests the execution of fetchUserEmailIds method and
	 * determines whether or not the list of user email ids is getting generated
	 * when the proposal Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchUserEmailIdsCase5() throws Exception
	{
		String lsProposalId = "###";
		String lsProcurementTitle = "abc";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moEvaluationService.fetchUserEmailIds(moMyBatisSession, lsProposalId, lsProcurementTitle, loNotificationMap);

	}

	/**
	 * This method tests the execution of fetchEvaluationStatusCount method and
	 * determines whether or not the value of boolean flag is true;
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationStatusCountCase1() throws ApplicationException
	{
		Boolean loLaunchWorkflow = moEvaluationService.fetchEvaluationStatusCount(moMyBatisSession, msProcurementId,
				msEvaluationPoolMappingId);
		assertTrue(loLaunchWorkflow);

	}

	/**
	 * This method tests the execution of fetchEvaluationStatusCount method and
	 * determines whether or not the value of boolean flag is true when the
	 * procurement Id is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationStatusCountCase2() throws ApplicationException
	{
		Boolean loLaunchWorkflow = moEvaluationService.fetchEvaluationStatusCount(moMyBatisSession, msProcurementId,
				null);
		assertFalse(loLaunchWorkflow);

	}

	/**
	 * This method tests the execution of fetchEvaluationStatusCount method and
	 * determines whether or not the value of boolean flag is true
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchEvaluationStatusCountCase3() throws ApplicationException
	{
		Boolean loLaunchWorkflow = moEvaluationService.fetchEvaluationStatusCount(moMyBatisSession, null,
				msEvaluationPoolMappingId);
		assertFalse(loLaunchWorkflow);

	}

	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationStatusCountCase4() throws ApplicationException
	{
		Boolean loLaunchWorkflow = moEvaluationService.fetchEvaluationStatusCount(null, msProcurementId,
				msEvaluationPoolMappingId);
		assertTrue(loLaunchWorkflow);

	}

	@Test
	public void testFetchProposalAndOrgName() throws ApplicationException
	{
		Map<String, String> loHeaderMap = moEvaluationService.fetchProposalAndOrgName(moMyBatisSession, msProposalId);
		assertNotNull(loHeaderMap);

	}

	@Test
	public void testUpdateEvaluatorCount() throws ApplicationException
	{
		Integer loEvaluatorsCountNew = 3;
		Map<String, String> loInputMap = new HashMap<String, String>();
		moEvaluationService.updateEvaluatorCount(moMyBatisSession, msEvaluationPoolMappingId, loEvaluatorsCountNew,
				true, loInputMap);
	}

	@Test
	public void testUpdateEvaluatorCountCase2() throws ApplicationException
	{
		Integer loEvaluatorsCountNew = 3;
		Map<String, String> loInputMap = new HashMap<String, String>();
		loInputMap.put(HHSConstants.MORE_EVALUATOR_ERROR_MESSAGE, "Error");
		moEvaluationService.updateEvaluatorCount(moMyBatisSession, msEvaluationPoolMappingId, loEvaluatorsCountNew,
				true, loInputMap);
	}

	@Test
	public void testUpdateEvaluatorCountCase3() throws ApplicationException
	{
		Integer loEvaluatorsCountNew = 3;
		moEvaluationService.updateEvaluatorCount(moMyBatisSession, msEvaluationPoolMappingId, loEvaluatorsCountNew,
				true, null);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluatorCountCase4() throws ApplicationException
	{
		Integer loEvaluatorsCountNew = 3;
		Map<String, String> loInputMap = new HashMap<String, String>();
		moEvaluationService.updateEvaluatorCount(null, msEvaluationPoolMappingId, loEvaluatorsCountNew, true,
				loInputMap);
	}

	@Test
	public void testFetchEvaluationResultsScores() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		loTaskMap.put("ProcurementID", msProcurementId);
		loTaskMap.put("EvaluationPoolMappingId", msEvaluationPoolMappingId);
		loTaskDetailMap.put(lsWobNumber, loTaskMap);
		moEvaluationService.fetchEvaluationResultsScores(moMyBatisSession, loTaskDetailMap, lsWobNumber);
	}

	@Test
	public void testFetchEvaluationResultsScores4() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		loTaskMap.put("ProcurementID", "115");
		loTaskMap.put("EvaluationPoolMappingId", HHSConstants.EMPTY_STRING);
		loTaskDetailMap.put(lsWobNumber, loTaskMap);
		List<EvaluationBean> lbEvbean = moEvaluationService.fetchEvaluationResultsScores(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
	}

	@Test
	public void testFetchEvaluationResultsScoresCase2() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		moEvaluationService.fetchEvaluationResultsScores(moMyBatisSession, null, lsWobNumber);
	}

	@Test
	public void testFetchEvaluationResultsScoresCase3() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		moEvaluationService.fetchEvaluationResultsScores(moMyBatisSession, loTaskDetailMap, lsWobNumber);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationResultsScoresAppException() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		loTaskMap.put("ProcurementID", msProcurementId);
		loTaskMap.put("EvaluationPoolMappingId", msEvaluationPoolMappingId);
		loTaskDetailMap.put(lsWobNumber, loTaskMap);
		moEvaluationService.fetchEvaluationResultsScores(null, loTaskDetailMap, lsWobNumber);
	}

	@Test
	public void testFetchReturnedUserEmailIds() throws ApplicationException
	{
		String lsProcurementTitle = "proc 4th mar";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		List<String> loEvalStatusIdList = new ArrayList<String>();
		loEvalStatusIdList.add(msEvaluationStatusId);
		moEvaluationService.fetchReturnedUserEmailIds(moMyBatisSession, loEvalStatusIdList, lsProcurementTitle,
				loNotificationMap);
	}

	@Test
	public void testFetchReturnedUserEmailIdsCase2() throws ApplicationException
	{
		String lsProcurementTitle = "proc 4th mar";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		List<String> loEvalStatusIdList = new ArrayList<String>();
		moEvaluationService.fetchReturnedUserEmailIds(moMyBatisSession, loEvalStatusIdList, lsProcurementTitle,
				loNotificationMap);
	}

	@Test
	public void testFetchReturnedUserEmailIdsCase3() throws ApplicationException
	{
		String lsProcurementTitle = "proc 4th mar";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		moEvaluationService.fetchReturnedUserEmailIds(moMyBatisSession, null, lsProcurementTitle, loNotificationMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchReturnedUserEmailIdsCase4() throws ApplicationException
	{
		String lsProcurementTitle = "proc 4th mar";
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.LINK, "http://");
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		List<String> loEvalStatusIdList = new ArrayList<String>();
		loEvalStatusIdList.add(msEvaluationStatusId);
		moEvaluationService.fetchReturnedUserEmailIds(null, loEvalStatusIdList, lsProcurementTitle, loNotificationMap);
	}

	@Test
	public void testUpdateAddDelFlag() throws ApplicationException
	{
		Boolean isUpdated = moEvaluationService.updateAddDelFlag(moMyBatisSession, msProcurementId,
				msEvaluationPoolMappingId);
		assertTrue(isUpdated);
	}

	@Test
	public void testGetUpdatedProposalDueDate() throws ApplicationException
	{
		moEvaluationService.getUpdatedProposalDueDate(moMyBatisSession, msProcurementId);
	}

	@Test
	public void testFetchDBDDocIdsList() throws ApplicationException
	{
		List<Map<String, String>> loDocIdsList = new ArrayList<Map<String, String>>();
		Map<String, String> loDocMap = new HashMap<String, String>();
		loDocMap.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, "1");
		loDocIdsList.add(loDocMap);
		moEvaluationService.fetchDBDDocIdsList(loDocIdsList);
	}

	@Test
	public void testFetchDBDDocIdsListCase2() throws ApplicationException
	{
		moEvaluationService.fetchDBDDocIdsList(null);
	}

	@Test
	public void testConsolidateAllDocsProperties() throws ApplicationException
	{
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		List<Map<String, String>> loDocDetailList = new ArrayList<Map<String, String>>();
		Map<String, String> loDocMap = new HashMap<String, String>();
		loDocMap.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, "1");
		HashMap<String, Object> loDocPropsBean = new HashMap<String, Object>();
		loDocPropsBean.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "2");
		loDocumentPropHM.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, loDocPropsBean);
		loDocDetailList.add(loDocMap);
		moEvaluationService.consolidateAllDocsProperties(loDocumentPropHM, loDocDetailList);
	}

	@Test
	public void testConsolidateAllDocsPropertiesCase2() throws ApplicationException
	{
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		Map<String, String> loDocMap = new HashMap<String, String>();
		loDocMap.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, "1");
		HashMap<String, Object> loDocPropsBean = new HashMap<String, Object>();
		loDocPropsBean.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "2");
		loDocumentPropHM.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, loDocPropsBean);
		moEvaluationService.consolidateAllDocsProperties(loDocumentPropHM, null);
	}

	@Test
	public void testConsolidateAllDocsPropertiesCase3() throws ApplicationException
	{
		HashMap<String, Object> loDocumentPropHM = new HashMap<String, Object>();
		List<Map<String, String>> loDocDetailList = new ArrayList<Map<String, String>>();
		Map<String, String> loDocMap = new HashMap<String, String>();
		HashMap<String, Object> loDocPropsBean = new HashMap<String, Object>();
		loDocPropsBean.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "2");
		loDocumentPropHM.put(HHSConstants.DOCUMENT_IDENTIFIER_ID, loDocPropsBean);
		loDocDetailList.add(loDocMap);
		moEvaluationService.consolidateAllDocsProperties(loDocumentPropHM, loDocDetailList);
	}

	@Test
	public void testFetchNotNonResponsiveCount() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementIdNonResposive);
		loParamMap.put(HHSConstants.EVALUATION_GROUP_ID, msEvaluationGroupIdNonRes);
		loParamMap.put(HHSConstants.COMPETITION_POOL_ID, msEvaluationGroupIdNonRes);
		moEvaluationService.fetchNotNonResponsiveCount(moMyBatisSession, loParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchNotNonResponsiveCountCase2() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementIdNonResposive);
		loParamMap.put(HHSConstants.EVALUATION_GROUP_ID, msEvaluationGroupIdNonRes);
		loParamMap.put(HHSConstants.COMPETITION_POOL_ID, msEvaluationGroupIdNonRes);
		moEvaluationService.fetchNotNonResponsiveCount(null, loParamMap);
	}

	@Test
	public void testUpdateEvaluationSentFlag() throws ApplicationException
	{
		moEvaluationService.updateEvaluationSentFlag(moMyBatisSession, msEvaluationPoolMappingId);
	}

	@Test
	public void testUpdateEvaluationSentFlagCase2() throws ApplicationException
	{
		moEvaluationService.updateEvaluationSentFlag(moMyBatisSession, "-1");
	}

	@Test
	public void testGetEvaluationSentFlag() throws ApplicationException
	{
		moEvaluationService.getEvaluationSentFlag(moMyBatisSession, msEvaluationPoolMappingId);
	}

	@Test
	public void testCheckScoresReturnedStatus() throws ApplicationException
	{
		String lsStatus = HHSConstants.SCORES_RETURNED;
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		moEvaluationService.checkScoresReturnedStatus(lsStatus, loNotificationMap);
	}

	@Test
	public void testCheckScoresReturnedStatusCase2() throws ApplicationException
	{
		String lsStatus = null;
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		moEvaluationService.checkScoresReturnedStatus(lsStatus, loNotificationMap);
	}

	@Test
	public void testCheckScoresReturnedStatusCase3() throws ApplicationException
	{
		String lsStatus = null;
		HashMap<String, Object> loNotificationMap = null;
		moEvaluationService.checkScoresReturnedStatus(lsStatus, loNotificationMap);
	}

	@Test
	public void testCheckScoresReturnedStatusCase4() throws ApplicationException
	{
		String lsStatus = HHSConstants.SCORES_ACCEPTED;
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		moEvaluationService.checkScoresReturnedStatus(lsStatus, loNotificationMap);
	}

	@Test
	public void testFetchUpdatedAwardAmount() throws ApplicationException
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, msProcurementId);
		loDataMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvaluationPoolMappingId);
		moEvaluationService.fetchUpdatedAwardAmount(moMyBatisSession, loDataMap);
	}

	@Test
	public void testUpdateProposalStatusCase2() throws Exception
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("PROPOSAL_PENDING_REASSIGNMENT", "22");
		loParamMap.put(HHSConstants.PROPOSAL_ID, msProposalId);
		Boolean loUpdateEvaluationFlag = moEvaluationService.updateProposalStatus(moMyBatisSession, loParamMap);
		assertTrue(loUpdateEvaluationFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScores0Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationReviewScore2Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationReviewScore(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefindEvaluationTaskSent3Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.findEvaluationTaskSent(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationCount4Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetInternalEvaluationsList5Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getInternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetExternalEvaluationsList6Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getExternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluatorCount7Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluatorCount(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetProcurementAgencyId9Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getProcurementAgencyId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalDetails10Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalComments11Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalComments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationScores12Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchFinalizeResultsVisibiltyStatus15Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUpdateResultsVisibiltyStatus16Negative1() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUpdateResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementValue33Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementValue(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardAmount34Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicecountFinalizeProcurementDetails35Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.countFinalizeProcurementDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProcurementStatus36Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProcurementStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicemarkProposalNonResponsive37Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.markProposalNonResponsive(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchIntExtProposalDetails42Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchIntExtProposalDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceinsertEvaluationStatus43Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.insertEvaluationStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicedeleteEvaluationSettingData44Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.deleteEvaluationSettingData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus45Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetDBDDocsList47Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getDBDDocsList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchExtAndIntEvaluator50Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchExtAndIntEvaluator(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCloseButtonVisibiltyStatus52Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCloseButtonVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCancelEvalTaskVisibiltyStatus54Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCancelEvalTaskVisibiltyStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicesaveEvaluationScoreDetails57Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProviders58Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProviders(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProposals59Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProposals(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalCount61Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationStatus64Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementDetailsForAwardWF65Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementDetailsForAwardWF(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorsList73Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorsList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateAddDelFlag76Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateAddDelFlag(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetUpdatedProposalDueDate77Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getUpdatedProposalDueDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNotNonResponsiveCount81Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNotNonResponsiveCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationSentFlag82Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationSentFlag(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationSentFlag83Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationSentFlag(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUpdatedAwardAmount85Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUpdatedAwardAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalAndOrgName86Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalAndOrgName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus87Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null);
	}

	@Test
	public void testFetchUpdateResultsVisibiltyStatus1() throws Exception
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setProcurementId("131");
		loEvalBean.setEvaluationPoolMappingId("163");
		String lsUpdateResultStatus = moEvaluationService.fetchUpdateResultsVisibiltyStatus(moMyBatisSession,
				loEvalBean);
		assertNotNull(lsUpdateResultStatus);
	}

	@Test
	public void testFetchFinalizeResultsVisibiltyStatusCase6() throws ApplicationException

	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		List<String> proposalStatusIdList = new ArrayList<String>();
		proposalStatusIdList.add("155");
		loEvalBean.setUserRole("ACCO_MANAGER");
		loEvalBean.setProcurementId("131");
		loEvalBean.setEvaluationPoolMappingId("163");
		loEvalBean.setProposalStatusIdList(proposalStatusIdList);
		moEvaluationService.fetchFinalizeResultsVisibiltyStatus(moMyBatisSession, loEvalBean);

	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScores0NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationReviewScores1NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationReviewScores(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationReviewScore2NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationReviewScore(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefindEvaluationTaskSent3NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.findEvaluationTaskSent(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationCount4NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetInternalEvaluationsList5NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getInternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetExternalEvaluationsList6NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getExternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluatorCount7NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluatorCount(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicesaveEvaluationDetails8NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.saveEvaluationDetails(null, null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetProcurementAgencyId9NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getProcurementAgencyId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalDetails10NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalComments11NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalComments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationScores12NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsSelections13NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsSelections(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsCount14NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchFinalizeResultsVisibiltyStatus15NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUpdateResultsVisibiltyStatus16NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUpdateResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchInternalEvaluatorUsers17NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchInternalEvaluatorUsers(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchExternalEvaluatorUsers18NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchExternalEvaluatorUsers(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchReqProposalDetails19NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchReqProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardStatusId20NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardStatusId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateModifiedFlag21NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateModifiedFlag(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateSelectedProposalDetails22NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateSelectedProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateNotSelectedProposalDetails23NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateNotSelectedProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationDetails24NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAccoComments25NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAccoComments(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementValue30NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementValue(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardAmount31NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicecountFinalizeProcurementDetails32NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.countFinalizeProcurementDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProcurementStatus33NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProcurementStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicemarkProposalNonResponsive34NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.markProposalNonResponsive(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateAwardReviewStatus35NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateAwardReviewStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchReviewAwardComments36NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchReviewAwardComments(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchIntExtProposalDetails37NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchIntExtProposalDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceinsertEvaluationStatus38NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.insertEvaluationStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicedeleteEvaluationSettingData39NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.deleteEvaluationSettingData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus40NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationCriteriaDetails41NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationCriteriaDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetDBDDocsList42NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getDBDDocsList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicedownloadDBDDocumentsAndZip43NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.downloadDBDDocumentsAndZip(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProviderNameList44NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProviderNameList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchExtAndIntEvaluator45NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchExtAndIntEvaluator(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScoreDetails46NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScoreDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCloseButtonVisibiltyStatus47NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCloseButtonVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetDownloadDBDDocsVisibiltyStatus48NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getDownloadDBDDocsVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCancelEvalTaskVisibiltyStatus49NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCancelEvalTaskVisibiltyStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetSendEvaluationTasksVisibiltyStatus50NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getSendEvaluationTasksVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetTotalEvaluationData51NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getTotalEvaluationData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicesaveEvaluationScoreDetails52NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProviders53NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProviders(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProposals54NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProposals(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalCount55NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceconfirmReturnForAction56NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.confirmReturnForAction(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicemodifyProposalStatus57NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.modifyProposalStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationStatus58NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementDetailsForAwardWF59NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementDetailsForAwardWF(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorDetails60NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationReviewsStatus61NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationReviewsStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefinishEvaluationReviewsStatus62NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.finishEvaluationReviewsStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationResult63NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationResult(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsScores64NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsScores(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchSelectionCommentsForAwardTask65NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchSelectionCommentsForAwardTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardAppDate66NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardAppDate(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorsList67NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorsList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUserEmailIds68NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUserEmailIds(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchReturnedUserEmailIds69NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchReturnedUserEmailIds(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateAddDelFlag70NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateAddDelFlag(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetUpdatedProposalDueDate71NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getUpdatedProposalDueDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationStatusCount72NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationStatusCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchDBDDocIdsList73NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchDBDDocIdsList(null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceconsolidateAllDocsProperties74NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.consolidateAllDocsProperties(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNotNonResponsiveCount75NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNotNonResponsiveCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationSentFlag76NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationSentFlag(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationSentFlag77NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationSentFlag(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicecheckScoresReturnedStatus78NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.checkScoresReturnedStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUpdatedAwardAmount79NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUpdatedAwardAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalAndOrgName80NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalAndOrgName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScoreDetailsForEvaluator81NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScoreDetailsForEvaluator(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicereviewScoreVersionInsert82NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.reviewScoreVersionInsert(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus83NegativeApp() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScores0Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScores(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFetchEvaluationReviewScoresNegative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		HashMap aoHmRequiredProps = new HashMap();
		try
		{
			loEvaluationService.fetchEvaluationReviewScores(null, aoHmRequiredProps, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationReviewScore2Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationReviewScore(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefindEvaluationTaskSent3Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.findEvaluationTaskSent(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationCount4Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetInternalEvaluationsList5Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getInternalEvaluationsList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetExternalEvaluationsList6Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getExternalEvaluationsList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluatorCount7Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluatorCount(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicesaveEvaluationDetails8Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.saveEvaluationDetails(null, null, null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetProcurementAgencyId9Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getProcurementAgencyId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalDetails10Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalComments11Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalComments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationScores12Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationScores(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsSelections13Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsSelections(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsCount14Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsCount(null, new EvaluationFilterBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchFinalizeResultsVisibiltyStatus15Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchUpdateResultsVisibiltyStatus16Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchUpdateResultsVisibiltyStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchInternalEvaluatorUsers17Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchInternalEvaluatorUsers(null, null, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchExternalEvaluatorUsers18Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchExternalEvaluatorUsers(null, null, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchReqProposalDetails19Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchReqProposalDetails(null, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardStatusId20Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardStatusId(null, new EvaluationBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateModifiedFlag21Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateModifiedFlag(null, null, new EvaluationBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateSelectedProposalDetails22Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateSelectedProposalDetails(null, new EvaluationBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateNotSelectedProposalDetails23Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateNotSelectedProposalDetails(null, new EvaluationBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationDetails24Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAccoComments25Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAccoComments(null, "", "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProcurementValue30Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProcurementValue(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardAmount31Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicecountFinalizeProcurementDetails32Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.countFinalizeProcurementDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProcurementStatus33Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProcurementStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicemarkProposalNonResponsive34Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.markProposalNonResponsive(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateAwardReviewStatus35Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		Map<String, Object> loProcurementInfoMap = new HashMap<String, Object>();
		try
		{
			loEvaluationService.updateAwardReviewStatus(null, loProcurementInfoMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchReviewAwardComments36Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchReviewAwardComments(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchIntExtProposalDetails37Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchIntExtProposalDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceinsertEvaluationStatus38Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.insertEvaluationStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicedeleteEvaluationSettingData39Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.deleteEvaluationSettingData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProposalStatus40Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProposalStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationCriteriaDetails41Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationCriteriaDetails(null, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetDBDDocsList42Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getDBDDocsList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicedownloadDBDDocumentsAndZip43Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.downloadDBDDocumentsAndZip(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProviderNameList44Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProviderNameList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchExtAndIntEvaluator45Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchExtAndIntEvaluator(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoreDetails46Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		try
		{
			loEvaluationService.fetchEvaluationScoreDetails(null, loTaskMap, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetCloseButtonVisibiltyStatus47Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getCloseButtonVisibiltyStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetDownloadDBDDocsVisibiltyStatus48Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getDownloadDBDDocsVisibiltyStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetCancelEvalTaskVisibiltyStatus49Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getCancelEvalTaskVisibiltyStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetSendEvaluationTasksVisibiltyStatus50Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getSendEvaluationTasksVisibiltyStatus(null, "", new EvaluationBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetTotalEvaluationData51Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getTotalEvaluationData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicesaveEvaluationScoreDetails52Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchNoOfProviders53Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchNoOfProviders(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchNoOfProposals54Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchNoOfProposals(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalCount55Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceconfirmReturnForAction56Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		Map<String, String> loStatusMap = new HashMap<String, String>();
		try
		{
			loEvaluationService.confirmReturnForAction(null, loStatusMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicemodifyProposalStatus57Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.modifyProposalStatus(null, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationStatus58Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProcurementDetailsForAwardWF59Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProcurementDetailsForAwardWF(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluatorDetails60Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluatorDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationReviewsStatus61Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationReviewsStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefinishEvaluationReviewsStatus62Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.finishEvaluationReviewsStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationResult63Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationResult(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsScores64Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsScores(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchSelectionCommentsForAwardTask65Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		HashMap<String, String> loCommentsMap = new HashMap<String, String>();
		try
		{
			loEvaluationService.fetchSelectionCommentsForAwardTask(null, loCommentsMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardAppDate66Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardAppDate(null, new EvaluationFilterBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluatorsList67Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluatorsList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchUserEmailIds68Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		try
		{
			loEvaluationService.fetchUserEmailIds(null, null, null, loNotificationMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchReturnedUserEmailIds69Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		List<String> loEvalStatusIdList = new ArrayList<String>();
		loEvalStatusIdList.add("ashdga");
		try
		{
			loEvaluationService.fetchReturnedUserEmailIds(null, loEvalStatusIdList, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateAddDelFlag70Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateAddDelFlag(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetUpdatedProposalDueDate71Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getUpdatedProposalDueDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationStatusCount72Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationStatusCount(null, "", "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchDBDDocIdsList73Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchDBDDocIdsList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceconsolidateAllDocsProperties74Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.consolidateAllDocsProperties(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchNotNonResponsiveCount75Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchNotNonResponsiveCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationSentFlag76Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationSentFlag(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationSentFlag77Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationSentFlag(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicecheckScoresReturnedStatus78Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.checkScoresReturnedStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchUpdatedAwardAmount79Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchUpdatedAwardAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalAndOrgName80Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalAndOrgName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoreDetailsForEvaluator81Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScoreDetailsForEvaluator(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicereviewScoreVersionInsert82Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.reviewScoreVersionInsert(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProposalStatus83Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProposalStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoresDetails83Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScoresDetails(null, "", "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoresDetails84Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		HashMap loHmRequiredProps = new HashMap();
		try
		{
			loEvaluationService.fetchEvaluationScoresDetails(null, loHmRequiredProps, "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
