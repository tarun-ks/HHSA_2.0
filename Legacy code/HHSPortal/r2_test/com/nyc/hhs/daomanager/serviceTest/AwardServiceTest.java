package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.PropertyLoader;

public class AwardServiceTest
{

	private static SqlSession moMyBatisSession = null;
	private AwardService moAwardService = new AwardService();
	private String msProcIdGetContractRegistered = "114";
	private String msActionGetawardAndContractsList = "131";
	private String msProcIdInvalid = "%$^#GF";
	private String msUserId = "283";
	private String msContractId = "75";
	private String msEvalPoolMappingId = "163";
	private String msEvalPoolMappingIdInvalid = "#@$Wd";
	private String msWobNumber = "asWobNumber";
	private String msContractStartDate = "02/02/2014";
	private String msContractEndDate = "02/08/2014";
	private String msProcIdFetchDocForAwardDocTask = "137";
	private String msStartNode = "1";
	private String msEndNode = "5";

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

	/** The Method will test actionGetawardAndContractsList Method */
	@Test
	public void testActionGetawardAndContractsList() throws ApplicationException
	{
		Map<String, Object> loAwardMap = new HashMap<String, Object>();
		loAwardMap.put("procurementId", msActionGetawardAndContractsList);
		loAwardMap.put("evaluationPoolMappingId", msEvalPoolMappingId);
		loAwardMap.put("startNode", msStartNode);
		loAwardMap.put("endNode", msEndNode);
		List<AwardBean> loawardAndContractsList = null;
		loawardAndContractsList = moAwardService.actionGetawardAndContractsList(moMyBatisSession, loAwardMap);
		assertNotNull(loawardAndContractsList);

	}

	/** The Method will test actionGetawardAndContractsList Method */
	@Test(expected = ApplicationException.class)
	public void testActionGetawardAndContractsListCase4() throws ApplicationException
	{
		Map<String, Object> loAwardMap = new HashMap<String, Object>();
		loAwardMap.put("procurementId", "972");
		loAwardMap.put("startNode", msStartNode);
		loAwardMap.put("endNode", msEndNode);
		moAwardService.actionGetawardAndContractsList(null, loAwardMap);
	}

	/**
	 * The Method will test fetchAwardsDetails method i.e whether it is
	 * returning populated Award Bean by passing valid contractID
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardsDetails() throws ApplicationException
	{
		AwardBean loAwardBean = moAwardService.fetchAwardsDetails(moMyBatisSession, "111777");
		assertNull(loAwardBean);
	}

	/**
	 * The Method will test fetchAwardsDetails method i.e when we are not
	 * passing ContractId i.e will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardsDetailsApplicationException() throws ApplicationException
	{
		moAwardService.fetchAwardsDetails(moMyBatisSession, null);
	}

	/**
	 * The Method will test fetchAwardsDetails method i.e when we are not
	 * passing ContractId i.e will throw Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardsDetailsApplicationExceptionCase1() throws ApplicationException
	{
		moAwardService.fetchAwardsDetails(null, "111777");
	}

	/**
	 * The Method will test updateAwardStatus method i.e by passing valid
	 * parameter Award Status will be successfully updated
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateAwardStatus() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.STATUS_ID,
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "AWARD_STATUS_CANCELLED"));
		loStatusInfo.put(HHSConstants.USER_ID, "city_142");
		loStatusInfo.put(HHSConstants.CONTRACT_ID, "777");
		Boolean lbUpdateStatus = moAwardService.updateAwardStatus(moMyBatisSession, loStatusInfo);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * The Method will test updateAwardStatus method i.e by passing valid
	 * parameter Award Status will be successfully updated
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateAwardStatusCase3() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.STATUS_ID,
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "AWARD_STATUS_CANCELLED"));
		loStatusInfo.put(HHSConstants.USER_ID, null);
		loStatusInfo.put(HHSConstants.CONTRACT_ID, null);
		moAwardService.updateAwardStatus(moMyBatisSession, loStatusInfo);
	}

	/**
	 * The Method will test updateAwardStatus method i.e by passing valid
	 * parameter Award Status will be successfully updated
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateAwardStatusCase4() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.STATUS_ID,
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "AWARD_STATUS_CANCELLED"));
		loStatusInfo.put(HHSConstants.USER_ID, "##");
		loStatusInfo.put(HHSConstants.CONTRACT_ID, "##");
		moAwardService.updateAwardStatus(moMyBatisSession, loStatusInfo);
	}

	/**
	 * The Method will test updateRelatedProposal method i.e by passing valid
	 * parameter Related Proposal Status will be successfully updated
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateRelatedProposal() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.USER_ID, "city_142");
		loStatusInfo.put(HHSConstants.CONTRACT_ID, "111777");
		loStatusInfo.put(HHSConstants.ORGANIZATION_ID, "accenture");
		loStatusInfo.put("procurementId", msActionGetawardAndContractsList);
		loStatusInfo.put("evaluationPoolMappingId", msEvalPoolMappingId);
		Boolean lbUpdateStatus = moAwardService.updateRelatedProposal(moMyBatisSession, loStatusInfo);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * The Method will test updateRelatedProposal method i.e by passing invalid
	 * parameter Related Proposal Status will not be updated and throw
	 * Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateRelatedProposal2() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.STATUS_ID,
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "PROPOSAL_NOT_SELECTED"));
		loStatusInfo.put(HHSConstants.USER_ID, "city_142");
		loStatusInfo.put(HHSConstants.CONTRACT_ID, "111777");
		loStatusInfo.put(HHSConstants.ORGANIZATION_ID, "##");
		loStatusInfo.put(HHSConstants.PROCUREMENT_ID, "##");
		loStatusInfo.put(HHSConstants.MODIFIED_FLAG, HHSConstants.YES_UPPERCASE);
		moAwardService.updateRelatedProposal(null, loStatusInfo);
	}

	/**
	 * The Method will test updateRelatedProposal method i.e by passing valid
	 * parameter Related Proposal Status will be successfully updated
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateAwardReviewStatus1() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.USER_ID, "agency_12");
		loStatusInfo.put("procurementId", msActionGetawardAndContractsList);
		loStatusInfo.put("evaluationPoolMappingId", msEvalPoolMappingId);
		Boolean lbUpdateStatus = moAwardService.updateAwardReviewStatus(moMyBatisSession, loStatusInfo);
		assertTrue(lbUpdateStatus);
	}

	/**
	 * The Method will test updateRelatedProposal method i.e by passing invalid
	 * parameter Related Proposal Status will not be updated and throw
	 * Application Exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateAwardReviewStatus2() throws ApplicationException
	{
		Map<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put(HHSConstants.STATUS_ID,
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "PROPOSAL_NOT_SELECTED"));
		loStatusInfo.put(HHSConstants.USER_ID, "city_142");
		loStatusInfo.put(HHSConstants.CONTRACT_ID, "111777");
		loStatusInfo.put(HHSConstants.ORGANIZATION_ID, "##");
		loStatusInfo.put(HHSConstants.PROCUREMENT_ID, "##");
		loStatusInfo.put(HHSConstants.MODIFIED_FLAG, HHSConstants.YES_UPPERCASE);
		moAwardService.updateAwardReviewStatus(null, loStatusInfo);
	}

	/**
	 * This method tests the execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardDetailsCase1() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "1");
		loParamMap.put("userOrgId", "accenture");
		AwardBean loAwardBean = moAwardService.fetchAwardDetails(moMyBatisSession, loParamMap, true);
		assertNull(loAwardBean);

	}

	/**
	 * This method tests the execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated when the map is
	 * null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardDetailsCase2() throws ApplicationException
	{
		Map<String, String> loParamMap = null;
		AwardBean loAwardBean = moAwardService.fetchAwardDetails(moMyBatisSession, loParamMap, true);
		assertNull(loAwardBean);

	}

	/**
	 * This method tests the execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated when the map is
	 * null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardDetailsCase3() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "834");
		loParamMap.put("userOrgId", "accenture");
		AwardBean loAwardBean = moAwardService.fetchAwardDetails(moMyBatisSession, loParamMap, false);
		assertNull(loAwardBean);

	}

	/**
	 * This method tests the negative execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardDetailsApplicationException() throws Exception
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "###");
		loParamMap.put("userOrgId", "###");
		AwardBean loAwardBean = moAwardService.fetchAwardDetails(moMyBatisSession, loParamMap, true);

	}

	/**
	 * This method tests the execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardDetailsCase5() throws ApplicationException
	{
		AwardBean loAwardBean = moAwardService.fetchAwardDetails(moMyBatisSession, null, true);
		assertNull(loAwardBean);

	}

	/**
	 * This method tests the execution of fetchAwardDetails method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardDetailsCase6() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "1");
		loParamMap.put("userOrgId", "accenture");
		moAwardService.fetchAwardDetails(null, loParamMap, true);
	}

	/**
	 * This method tests the execution of fetchAwardId method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardIdCase1() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "805");
		loParamMap.put("userOrgId", "accenture");
		String lsAwardId = moAwardService.fetchAwardId(moMyBatisSession, loParamMap);
		assertNull(lsAwardId);

	}

	/**
	 * This method tests the execution of fetchAwardId method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardIdCase2() throws ApplicationException
	{
		Map<String, String> loParamMap = null;
		String lsAwardId = moAwardService.fetchAwardId(moMyBatisSession, loParamMap);
		assertNull(lsAwardId);

	}

	/**
	 * This method tests the negative execution of fetchAwardId method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardIdApplicationException() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "###");
		loParamMap.put("userOrgId", "accenture");
		moAwardService.fetchAwardId(moMyBatisSession, loParamMap);

	}

	/**
	 * This method tests the execution of fetchAwardId method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardIdCase4() throws ApplicationException
	{
		String lsAwardId = moAwardService.fetchAwardId(moMyBatisSession, null);
		assertNull(lsAwardId);

	}

	/**
	 * This method tests the execution of fetchAwardId method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardIdCase5() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "1");
		loParamMap.put("userOrgId", "accenture");
		moAwardService.fetchAwardId(null, loParamMap);
	}

	/**
	 * This method tests the execution of fetchAwardDocuments method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test
	public void testFetchAwardDocumentsCase1() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", msActionGetawardAndContractsList);
		loParamMap.put("evaluationPoolMappingId", msEvalPoolMappingId);
		List<ExtendedDocument> loAwardDocumentList = moAwardService.fetchAwardDocuments(moMyBatisSession, loParamMap);
		assertNotNull(loAwardDocumentList);

	}

	/**
	 * This method tests the execution of fetchAwardDocuments method and
	 * determines whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardDocumentsCase5() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "1");
		loParamMap.put("userOrgId", "accenture");
		moAwardService.fetchAwardDocuments(null, loParamMap);
	}

	/**
	 * This method tests the execution of aptProgressView method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testViewAptProgressCase1() throws ApplicationException
	{
		Map<String, String> loViewProgressMap = new HashMap<String, String>();
		loViewProgressMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "1234567890125");
		AwardBean viewAptDetails = moAwardService.aptProgressView(moMyBatisSession, loViewProgressMap);
		assertNull(viewAptDetails);

	}

	/**
	 * This method tests the execution of aptProgressView method and determines
	 * whether or not AwardBean is getting populated when the map is null
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testViewAptProgressCase2() throws ApplicationException
	{
		Map<String, String> loViewProgressMap = null;
		moAwardService.aptProgressView(moMyBatisSession, loViewProgressMap);
	}

	/**
	 * This method tests the execution of aptProgressView method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testViewAptProgressCase3() throws ApplicationException
	{
		Map<String, String> loViewProgressMap = new HashMap<String, String>();
		loViewProgressMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "##");
		AwardBean viewAptDetails = moAwardService.aptProgressView(moMyBatisSession, loViewProgressMap);
		assertNull(viewAptDetails);
	}

	/**
	 * This method tests the execution of aptProgressView method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testViewAptProgressCase4() throws ApplicationException
	{
		Map<String, String> loViewProgressMap = new HashMap<String, String>();
		loViewProgressMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "");
		AwardBean viewAptDetails = moAwardService.aptProgressView(moMyBatisSession, loViewProgressMap);
		assertNull(viewAptDetails);
	}

	/**
	 * This method tests the execution of aptProgressView method and determines
	 * whether or not AwardBean is getting populated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test(expected = ApplicationException.class)
	public void testViewAptProgressCase5() throws ApplicationException
	{
		Map<String, String> loViewProgressMap = new HashMap<String, String>();
		loViewProgressMap.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "1234567890125");
		moAwardService.aptProgressView(null, loViewProgressMap);
	}

	/**
	 * This method tests the execution of fetchDocumentsForAwardDocTask method
	 * and determines whether award documents exists for given procurement Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchDocumentsForAwardDocTask() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcIdFetchDocForAwardDocTask);
		loTaskDetailMap.put(msWobNumber, loTaskPropsMap);
		loTaskDetailMap.put("EvaluationPoolMappingId", null);
		List<ExtendedDocument> loAwardDocList = moAwardService.fetchDocumentsForAwardDocTask(moMyBatisSession,
				loTaskDetailMap, msWobNumber);
		assertNotNull(loAwardDocList);

	}

	/**
	 * This method tests the execution of fetchDocumentsForAwardDocTask method
	 * and determines whether award documents exists for given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchDocumentsForAwardDocTaskCase4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moAwardService.fetchDocumentsForAwardDocTask(null, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of fetchDocumentsForAwardDocTask method
	 * and determines whether award documents exists for given procurement Id
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testFetchDocumentsForAwardDocTaskCase5() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moAwardService.fetchDocumentsForAwardDocTask(null, loTaskDetailMap, lsWobNumber);
	}

	/**
	 * This method tests the execution of fetchDocumentsForAwardDocTask method
	 * and determines whether award documents exists for given procurement Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchDocumentsForAwardDocTask6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loTaskDetailMap.put("##", loTaskPropsMap);
		List<ExtendedDocument> loAwardDocList = moAwardService.fetchDocumentsForAwardDocTask(moMyBatisSession,
				loTaskDetailMap, lsWobNumber);
		assertNull(loAwardDocList);

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test
	public void testRemoveAwardTaskDocs() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		String lsDocSeqNo = "1";
		Boolean lbRemoveFlag = moAwardService.removeAwardTaskDocs(moMyBatisSession, lsDocumentId, lsDocSeqNo);
		assertTrue(lbRemoveFlag);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testRemoveAwardTaskDocsCase1() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		String lsDocSeqNo = "1";
		Boolean lbRemoveFlag = moAwardService.removeAwardTaskDocs(null, lsDocumentId, lsDocSeqNo);
		assertTrue(lbRemoveFlag);
	}

	/*	*//**
	 * This method tests the execution of removeAwardTaskDocs method and
	 * removes award document based on document Id input
	 * @throws ApplicationException
	 */
	/*
	 * @Test public void testRemoveAwardTaskDocsCase2() throws
	 * ApplicationException { Boolean lbRemoveFlag =
	 * moAwardService.removeAwardTaskDocs(moMyBatisSession, "##","##");
	 * assertFalse(lbRemoveFlag); moMyBatisSession.commit();
	 * 
	 * }
	 *//**
	 * This method tests the execution of removeAwardTaskDocs method and
	 * removes award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testRemoveAwardTaskDocsCase3() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		String lsDocSeqNo = "1";
		moAwardService.removeAwardTaskDocs(null, lsDocumentId, lsDocSeqNo);
	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test
	public void testRemoveAwardTaskDocsCase4() throws ApplicationException
	{
		Boolean lbRemoveFlag = moAwardService.removeAwardTaskDocs(moMyBatisSession, null, "1");
		assertTrue(lbRemoveFlag);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertAwardTaskDocDetails() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
		loDocPropsMap.put("procurementId", msActionGetawardAndContractsList);
		loDocPropsMap.put("evaluationPoolMappingId", msEvalPoolMappingId);
		loDocPropsMap.put("userId", "agency_14");
		loDocPropsMap.put("status",
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "DOCUMENT_SUBMITTED"));
		loDocPropsMap.put("documentId", lsDocumentId);
		loDocPropsMap.put("docCategory", "Solicitation");
		loDocPropsMap.put("docName", "My Document");
		loDocPropsMap.put("docType", "Agency Document");
		Boolean lbInsertFlag = moAwardService.insertAwardTaskDocDetails(moMyBatisSession, loDocPropsMap);
		assertTrue(lbInsertFlag);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertAwardTaskDocDetailsCase1() throws ApplicationException
	{
		moAwardService.insertAwardTaskDocDetails(moMyBatisSession, null);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testInsertAwardTaskDocDetailsCase2() throws ApplicationException
	{
		moAwardService.insertAwardTaskDocDetails(moMyBatisSession, null);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertAwardTaskDocDetailsCase3() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
		loDocPropsMap.put("procurementId", "3");
		loDocPropsMap.put("userId", "agency_14");
		loDocPropsMap.put("status",
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "DOCUMENT_SUBMITTED"));
		loDocPropsMap.put("documentId", lsDocumentId);
		loDocPropsMap.put("docCategory", "Solicitation");
		loDocPropsMap.put("docName", "My Document");
		loDocPropsMap.put("docType", "Agency Document");
		moAwardService.insertAwardTaskDocDetails(null, loDocPropsMap);
	}

	/**
	 * This method tests the execution of removeAwardTaskDocs method and removes
	 * award document based on document Id input
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testInsertAwardTaskDocDetailsCase4() throws ApplicationException
	{
		String lsDocumentId = "{72C00B69-71BA-4F4B-B5C3-9EEBDAD6B4E0}";
		HashMap<String, String> loDocPropsMap = new HashMap<String, String>();
		loDocPropsMap.put("procurementId", "3");
		loDocPropsMap.put("userId", "agency_14");
		loDocPropsMap.put("status",
				PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, "DOCUMENT_SUBMITTED"));
		loDocPropsMap.put("documentId", lsDocumentId);
		loDocPropsMap.put("docCategory", "Solicitation");
		loDocPropsMap.put("docName", "My Document");
		loDocPropsMap.put("docType", "Agency Document");
		moAwardService.insertAwardTaskDocDetails(null, loDocPropsMap);
	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveAwardDocumentConfig() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		loProposalBean.setProcurementId(msActionGetawardAndContractsList);
		loProposalBean.setEvaluationPoolMappingId(msEvalPoolMappingId);
		loProposalBean.setModifiedBy("agency_14");
		loProposalBean.setCreatedBy("agency_14");
		Boolean lbInsertFlag = moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		assertTrue(lbInsertFlag);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase1() throws ApplicationException
	{
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, null);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCase2() throws ApplicationException
	{
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, null);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase3() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loProposalBean.setRequiredDocumentList(null);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase4() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loProposalBean.setOptionalDocumentList(null);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCase5() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loProposalBean.setRequiredDocumentList(null);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCase6() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loProposalBean.setOptionalDocumentList(null);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase7() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(null, loProposalBean);
	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCase8() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(null, loProposalBean);
	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase9() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(null);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testSaveAwardDocumentConfigCase10() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(null);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCaseCase11() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(null);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testSaveAwardDocumentConfigCase12() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(null);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveAwardDocumentConfigCase13() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		loExtDoc.setDocumentType("Board Resolution");
		loExtDoc.setDocumentSeqNumber("6");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loProposalBean.setEvaluationPoolMappingId("239");
		loExtDoc = new ExtendedDocument();
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		Boolean lbInsertFlag = moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		assertTrue(lbInsertFlag);
		moMyBatisSession.commit();

	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * saves award document configuration
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveAwardDocumentConfigCase14() throws ApplicationException
	{
		ProposalDetailsBean loProposalBean = new ProposalDetailsBean();
		List<ExtendedDocument> loRequiredDocList = new ArrayList<ExtendedDocument>();
		List<ExtendedDocument> loOptionalDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loExtDoc = new ExtendedDocument();
		// loExtDoc.setDocumentType("Board Resolution");
		// loExtDoc.setDocumentSeqNumber("6");
		loRequiredDocList.add(loExtDoc);
		loProposalBean.setRequiredDocumentList(loRequiredDocList);
		loProposalBean.setProcurementId("805");
		loProposalBean.setCreatedBy("agency_14");
		loProposalBean.setModifiedBy("agency_14");
		loProposalBean.setEvaluationPoolMappingId("239");
		loExtDoc = new ExtendedDocument();
		// loExtDoc.setDocumentType("Board Resolution");
		// loExtDoc.setDocumentSeqNumber("6");
		loExtDoc.setDocumentType("Annual Report");
		loExtDoc.setDocumentSeqNumber("1");
		loExtDoc.setRequiredFlag("1");
		loExtDoc.setCreatedBy("agency_14");
		loExtDoc.setModifiedDate("agency_14");
		// loExtDoc.setDocumentSeqNumber("1");
		loOptionalDocList.add(loExtDoc);
		loProposalBean.setOptionalDocumentList(loOptionalDocList);
		Boolean lbInsertFlag = moAwardService.saveAwardDocumentConfig(moMyBatisSession, loProposalBean);
		assertTrue(lbInsertFlag);
		moMyBatisSession.commit();

	}

	@Test
	public void testInsertAwardDocumentDetails1() throws ApplicationException
	{
		Integer liRowsInserted = 0;
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("awardId", "7");
		aoParamMap.put("userId", "283");
		aoParamMap.put("statusId", "50");
		aoParamMap.put("documentId", "100");
		aoParamMap.put("docReferenceNo", "16");
		aoParamMap.put("organizationId", "accenture");
		liRowsInserted = moAwardService.insertAwardDocumentDetails(moMyBatisSession, aoParamMap, null);

		assertTrue(liRowsInserted != 0);
	}

	@Test
	public void testInsertAwardDocumentDetails2() throws ApplicationException
	{
		Integer liRowsInserted = 0;
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("awardId", "7");
		aoParamMap.put("userId", "283");
		aoParamMap.put("statusId", "50");
		aoParamMap.put("documentId", "100");
		aoParamMap.put("docReferenceNo", "16");
		aoParamMap.put("organizationId", "accenture");
		liRowsInserted = moAwardService.insertAwardDocumentDetails(moMyBatisSession, aoParamMap, "null");

		assertTrue(liRowsInserted != 0);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAwardDocumentDetails3() throws ApplicationException
	{
		Integer liRowsInserted = 0;
		String lsReplacingDocId = "100";
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("awardId", "7");
		aoParamMap.put("userId", "283");
		aoParamMap.put("statusId", "50");
		aoParamMap.put("documentId", "100");
		aoParamMap.put("docReferenceNo", "16");
		aoParamMap.put("organizationId", "accenture");
		liRowsInserted = moAwardService.insertAwardDocumentDetails(null, aoParamMap, lsReplacingDocId);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertAwardDocumentDetails4() throws ApplicationException
	{
		Integer liRowsInserted = 0;
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("awardId", "28");
		aoParamMap.put("userId", "2383");
		aoParamMap.put("statusId", "50");
		aoParamMap.put("documentId", "100");
		aoParamMap.put("docReferenceNo", "100");
		liRowsInserted = moAwardService.insertAwardDocumentDetails(moMyBatisSession, aoParamMap, "");

	}

	@Test
	public void testRemoveAwardDocuments() throws ApplicationException
	{
		Integer liRowsDeleted = null;
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("documentId", "1");
		aoParamMap.put("awardId", "1");
		aoParamMap.put("docReferenceNo", "");
		liRowsDeleted = moAwardService.removeAwardDocuments(moMyBatisSession, aoParamMap);
		assertNotNull(liRowsDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveAwardDocumentsCase1() throws ApplicationException
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("documentId", null);
		aoParamMap.put("awardId", null);
		aoParamMap.put("docReferenceNo", null);
		moAwardService.removeAwardDocuments(moMyBatisSession, aoParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveAwardDocumentsCase2() throws ApplicationException
	{
		moAwardService.removeAwardDocuments(moMyBatisSession, null);
	}

	@Test
	public void testUpdateAwardDetailsFromTask() throws ApplicationException
	{

		HashMap<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put("awardStatusId", "35");
		loAwardMap.put("procurementId", "3");
		Boolean lbUpdateStatus = moAwardService.updateAwardDetailsFromTask(moMyBatisSession, loAwardMap);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdateAwardDetailsFromTaskCase3() throws ApplicationException
	{

		HashMap<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put("awardStatusId", "35");
		loAwardMap.put("procurementId", "3");
		Boolean lbUpdateStatus = moAwardService.updateAwardDetailsFromTask(moMyBatisSession, loAwardMap);
		assertTrue(lbUpdateStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateAwardDetailsFromTaskCase4() throws ApplicationException
	{
		HashMap<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put("awardStatusId", "35");
		loAwardMap.put("procurementId", "3");
		moAwardService.updateAwardDetailsFromTask(null, loAwardMap);
	}

	/**
	 * This method tests the execution of saveAwardDocumentConfig method and
	 * @throws ApplicationException
	 */
	@Test
	public void testAwardAndContractsCount() throws ApplicationException
	{
		Map<String, Object> loAwardMap = new HashMap<String, Object>();
		loAwardMap.put("contactTypeId1", "1");
		loAwardMap.put("contactTypeId5", "3");
		loAwardMap.put("procurementId", "3");
		Integer loAwardCount = moAwardService.awardAndContractsCount(moMyBatisSession, loAwardMap);
		assertNotNull(loAwardCount);

	}

	/**
	 * This method tests the execution of awardAndContractsCount method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testAwardAndContractsCountCase1() throws ApplicationException
	{
		moAwardService.awardAndContractsCount(moMyBatisSession, null);

	}

	/**
	 * This method tests the execution of awardAndContractsCount method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testAwardAndContractsCountCase2() throws ApplicationException
	{
		Map<String, Object> loAwardMap = new HashMap<String, Object>();
		loAwardMap.put("contactTypeId1", "1");
		loAwardMap.put("contactTypeId5", "3");
		loAwardMap.put("procurementId", "3");
		moAwardService.awardAndContractsCount(null, loAwardMap);
	}

	/**
	 * This method tests the execution of fetchAwardReviewStatus method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardReviewStatus() throws ApplicationException
	{
		String lsProcurementId = "805";
		EvaluationBean loEvalBean = moAwardService.fetchAwardReviewStatus(moMyBatisSession, lsProcurementId, null);
		assertNull(loEvalBean);

	}

	/**
	 * This method tests the execution of fetchAwardReviewStatus method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardReviewStatusCase1() throws ApplicationException
	{
		EvaluationBean loEvalBean = moAwardService.fetchAwardReviewStatus(moMyBatisSession, null, null);
		assertNull(loEvalBean);

	}

	/**
	 * This method tests the execution of fetchAwardReviewStatus method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardReviewStatusCase2() throws ApplicationException
	{
		String lsProcurementId = null;
		EvaluationBean loEvalBean = moAwardService.fetchAwardReviewStatus(moMyBatisSession, lsProcurementId, null);
		assertNull(loEvalBean);

	}

	/**
	 * This method tests the execution of fetchAwardReviewStatus method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardReviewStatusCase3() throws ApplicationException
	{
		String lsProcurementId = "805";
		moAwardService.fetchAwardReviewStatus(null, lsProcurementId, null);

	}

	/**
	 * This method tests the execution of fetchAwardEPinDetails method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardEPinDetails() throws ApplicationException
	{
		String lsProcurementId = "972";
		List<AwardBean> loAwardBeanList = moAwardService.fetchAwardEPinDetails(moMyBatisSession, lsProcurementId);
		assertNotNull(loAwardBeanList);
		assertFalse(loAwardBeanList.size() > 0);

	}

	/**
	 * This method tests the execution of fetchAwardEPinDetails method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAwardEPinDetailsCase1() throws ApplicationException
	{
		String lsProcurementId = "";
		List<AwardBean> loAwardBeanList = moAwardService.fetchAwardEPinDetails(moMyBatisSession, lsProcurementId);
		assertTrue(loAwardBeanList.isEmpty() == true);

	}

	/**
	 * This method tests the execution of fetchAwardEPinDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardEPinDetailsCase2() throws ApplicationException
	{
		String lsProcurementId = "##";
		moAwardService.fetchAwardEPinDetails(moMyBatisSession, lsProcurementId);

	}

	/**
	 * This method tests the execution of fetchAwardEPinDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardEPinDetailsCase3() throws ApplicationException
	{
		String lsProcurementId = null;
		moAwardService.fetchAwardEPinDetails(moMyBatisSession, lsProcurementId);

	}

	/**
	 * This method tests the execution of fetchAwardEPinDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAwardEPinDetailsCase4() throws ApplicationException
	{
		String lsProcurementId = "972";
		moAwardService.fetchAwardEPinDetails(null, lsProcurementId);
	}

	/**
	 * This method tests the execution of fetchAmountProviderDetails method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAmountProviderDetails() throws ApplicationException
	{
		String lsContractId = "660";
		AwardBean loAwardBean = moAwardService.fetchAmountProviderDetails(moMyBatisSession, lsContractId);
		assertNotNull(loAwardBean);

	}

	/**
	 * This method tests the execution of fetchAmountProviderDetails method and
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAmountProviderDetailsCase1() throws ApplicationException
	{
		String lsContractId = "";
		AwardBean loAwardBean = moAwardService.fetchAmountProviderDetails(moMyBatisSession, lsContractId);
		assertNull(loAwardBean);

	}

	/**
	 * This method tests the execution of fetchAmountProviderDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAmountProviderDetailsCase2() throws ApplicationException
	{
		String lsContractId = "##";
		moAwardService.fetchAmountProviderDetails(moMyBatisSession, lsContractId);

	}

	/**
	 * This method tests the execution of fetchAmountProviderDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAmountProviderDetailsCase3() throws ApplicationException
	{
		String lsContractId = null;
		moAwardService.fetchAmountProviderDetails(moMyBatisSession, lsContractId);

	}

	/**
	 * This method tests the execution of fetchAmountProviderDetails method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchAmountProviderDetailsCase4() throws ApplicationException
	{
		String lsContractId = "660";
		moAwardService.fetchAmountProviderDetails(null, lsContractId);

	}

	/**
	 * This method tests the execution of assignAwardEpin method and
	 * @throws ApplicationException
	 */
	@Test
	public void testAssignAwardEpin() throws ApplicationException
	{
		Map<String, String> loAwardEpinMap = new HashMap<String, String>();
		loAwardEpinMap.put("lsAwardEpin", "1237689543012");
		loAwardEpinMap.put("asContractId", "663");
		Boolean lbAwardEPinStatus = moAwardService.assignAwardEpin(moMyBatisSession, loAwardEpinMap);
		assertTrue(lbAwardEPinStatus);

	}

	/**
	 * This method tests the execution of assignAwardEpin method and
	 * @throws ApplicationException
	 */
	@Test
	public void testAssignAwardEpinCase1() throws ApplicationException
	{
		Map<String, String> loAwardEpinMap = new HashMap<String, String>();
		loAwardEpinMap.put("lsAwardEpin", "1237689543012");
		loAwardEpinMap.put("asContractId", "");
		Boolean lbAwardEPinStatus = moAwardService.assignAwardEpin(moMyBatisSession, loAwardEpinMap);
		assertTrue(lbAwardEPinStatus);

	}

	/**
	 * This method tests the execution of assignAwardEpin method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testAssignAwardEpinCase2() throws ApplicationException
	{
		Map<String, String> loAwardEpinMap = new HashMap<String, String>();
		loAwardEpinMap.put("lsAwardEpin", "1237689543012");
		loAwardEpinMap.put("asContractId", "##");
		moAwardService.assignAwardEpin(moMyBatisSession, loAwardEpinMap);

	}

	/**
	 * This method tests the execution of assignAwardEpin method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testAssignAwardEpinCase3() throws ApplicationException
	{
		Map<String, String> loAwardEpinMap = new HashMap<String, String>();
		loAwardEpinMap.put("lsAwardEpin", "1237689543012");
		loAwardEpinMap.put("asContractId", null);
		moAwardService.assignAwardEpin(moMyBatisSession, loAwardEpinMap);

	}

	/**
	 * This method tests the execution of assignAwardEpin method and
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testAssignAwardEpinCase4() throws ApplicationException
	{
		Map<String, String> loAwardEpinMap = new HashMap<String, String>();
		loAwardEpinMap.put("lsAwardEpin", "1237689543012");
		loAwardEpinMap.put("asContractId", "663");
		moAwardService.assignAwardEpin(null, loAwardEpinMap);

	}

	/**
	 * This method will fetch no of contracts which are not canceled, closed or
	 * registered and return true if the no of contracts fetched is greater than
	 * 0. ctNotCancelledClosedRegistered returns the no of contracts
	 * 
	 */

	@Test
	public void testCtNotCancelledClosedRegistered() throws ApplicationException
	{
		String asProcurementId = "636";
		Boolean liNoOfContracts = moAwardService.ctNotCancelledClosedRegistered(moMyBatisSession, asProcurementId);
		assertFalse(liNoOfContracts);
	}

	@Test(expected = ApplicationException.class)
	public void testCtNotCancelledClosedRegisteredCase1() throws ApplicationException
	{
		String asProcurementId = "636";
		Boolean liNoOfContracts = moAwardService.ctNotCancelledClosedRegistered(null, asProcurementId);
		assertTrue(liNoOfContracts);
	}

	@Test
	public void testCtNotCancelledClosedRegisteredCase2() throws ApplicationException
	{
		String asProcurementId = "";
		Boolean liNoOfContracts = moAwardService.ctNotCancelledClosedRegistered(moMyBatisSession, asProcurementId);
		assertFalse(liNoOfContracts);
	}

	@Test(expected = ApplicationException.class)
	public void testCtNotCancelledClosedRegisteredCase3() throws ApplicationException
	{
		String asProcurementId = null;
		moAwardService.ctNotCancelledClosedRegistered(moMyBatisSession, asProcurementId);
	}

	@Test(expected = ApplicationException.class)
	public void testCtNotCancelledClosedRegisteredCase4() throws ApplicationException
	{
		moAwardService.ctNotCancelledClosedRegistered(moMyBatisSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCtNotCancelledClosedRegisteredCase5() throws ApplicationException
	{
		moAwardService.ctNotCancelledClosedRegistered(null, null);
	}

	@Test
	public void testCheckIfAllReqAwardDocsUploaded1() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "39", null);
		assertFalse(loDocumentCompleteStatus);
	}

	@Test
	public void testCheckIfAllReqAwardDocsUploaded2() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "39", "");
		assertFalse(loDocumentCompleteStatus);

	}

	@Test
	public void testCheckIfAllReqAwardDocsUploaded3() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "39", "null");
		assertFalse(loDocumentCompleteStatus);

	}

	@Test
	public void testCheckIfAllReqAwardDocsUploaded4() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "0", null);
		assertTrue(loDocumentCompleteStatus);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckIfAllReqAwardDocsUploaded5() throws ApplicationException
	{
		moAwardService.checkIfAllReqAwardDocsUploaded(null, 1, "28", null);
	}
	
	@Test
	public void testCheckIfAllReqAwardDocsUploaded6() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 0, "39", null);
		assertFalse(loDocumentCompleteStatus);
	}
	
	@Test
	public void testCheckIfAllReqAwardDocsUploaded7() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, null, "39", null);
		assertFalse(loDocumentCompleteStatus);
	}
	
	@Test
	public void testCheckIfAllReqAwardDocsUploaded8() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "43", "");
		assertFalse(loDocumentCompleteStatus);
	}

	@Test
	public void testGetAgencyIdForAwardId1() throws ApplicationException
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
		loNotificationMap = moAwardService.getAgencyIdForAwardId(moMyBatisSession, loNotificationMap, "43", true);
		assertNotNull(loNotificationMap);
	}

	/*
	 * @Test public void testGetAgencyIdForAwardId2() throws
	 * ApplicationException { HashMap<String, Object> loNotificationMap = new
	 * HashMap<String, Object>(); loNotificationMap =
	 * moAwardService.getAgencyIdForAwardId(moMyBatisSession, loNotificationMap,
	 * "16",true); assertNotNull(loNotificationMap);
	 * assertTrue(loNotificationMap.size()>0);
	 * 
	 * }
	 */

	@Test
	public void testGetAgencyIdForAwardId3() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap = moAwardService.getAgencyIdForAwardId(moMyBatisSession, loNotificationMap, "35", false);
		assertNotNull(loNotificationMap);

	}

	@Test
	public void testGetAgencyIdForAwardId4() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap = moAwardService.getAgencyIdForAwardId(moMyBatisSession, loNotificationMap, "35", false);
		assertFalse(loNotificationMap.size() > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetAgencyIdForAwardId5() throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap = moAwardService.getAgencyIdForAwardId(null, loNotificationMap, "35", true);
	}

	@Test
	public void testGetContractRegistered1() throws ApplicationException
	{
		Boolean result = moAwardService.getContractRegistered(moMyBatisSession, msProcIdGetContractRegistered);
		assertTrue(result);
	}

	@Test(expected = ApplicationException.class)
	public void testGetContractRegistered2() throws ApplicationException
	{
		Boolean result = moAwardService.getContractRegistered(moMyBatisSession, msProcIdInvalid);
		assertTrue(result);
	}

	@Test
	public void testUpdateContractBudgetStatus1() throws ApplicationException
	{
		HashMap<String, String> loStatusInfo = new HashMap<String, String>();
		loStatusInfo.put("userId", msUserId);
		loStatusInfo.put("contractId", msContractId);
		Boolean result = moAwardService.updateContractBudgetStatus(moMyBatisSession, loStatusInfo);
		assertTrue(result);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetStatus2() throws ApplicationException
	{
		HashMap<String, String> loStatusInfo = new HashMap<String, String>();
		Boolean result = moAwardService.updateContractBudgetStatus(moMyBatisSession, loStatusInfo);
		assertTrue(result);
	}

	@Test
	public void testFetchAwardAmountForSelectedProposals1() throws ApplicationException
	{
		HashMap<String, Object> loHmReqProposMap = new HashMap<String, Object>();
		HashMap<String, Object> resultMap = moAwardService.fetchAwardAmountForSelectedProposals(moMyBatisSession,
				msEvalPoolMappingId, loHmReqProposMap);
		assertNotNull(resultMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAwardAmountForSelectedProposals2() throws ApplicationException
	{
		HashMap<String, Object> loHmReqProposMap = new HashMap<String, Object>();
		HashMap<String, Object> resultMap = moAwardService.fetchAwardAmountForSelectedProposals(moMyBatisSession,
				msEvalPoolMappingIdInvalid, loHmReqProposMap);
		assertNotNull(resultMap);
	}

	@Test
	public void testFetchOrgNameFromContractId1() throws ApplicationException
	{
		String result = moAwardService.fetchOrgNameFromContractId(moMyBatisSession, msContractId);
		assertNotNull(result);
	}

	@Test
	public void testFetchDefaultConfigId1() throws ApplicationException
	{
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcIdGetContractRegistered);
		loTaskMap.put(msWobNumber, loProcurementMap);
		String result = moAwardService.fetchDefaultDocumentConfigId(moMyBatisSession, loTaskMap, msWobNumber);
		assertNull(result);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchDefaultConfigId2() throws ApplicationException
	{
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap();
		loTaskMap.put(msWobNumber, loProcurementMap);
		String result = moAwardService.fetchDefaultDocumentConfigId(moMyBatisSession, loTaskMap, msWobNumber);
		assertNotNull(result);
	}

	@Test
	public void testFetchAwardDocumentTypeForTask1() throws ApplicationException
	{
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, msProcIdGetContractRegistered);
		loTaskMap.put(msWobNumber, loProcurementMap);
		List<ExtendedDocument> resultList = moAwardService.fetchAwardDocumentTypeForTask(moMyBatisSession, loTaskMap,
				msWobNumber);
		assertNotNull(resultList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAwardDocumentTypeForTask2() throws ApplicationException
	{
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap loProcurementMap = new HashMap();
		loTaskMap.put(msWobNumber, loProcurementMap);
		List<ExtendedDocument> resultList = moAwardService.fetchAwardDocumentTypeForTask(moMyBatisSession, loTaskMap,
				msWobNumber);
		assertNotNull(resultList);
	}

	@Test
	public void testCheckAwardReviewStatus1() throws ApplicationException
	{
		Boolean result = moAwardService.checkAwardReviewStatus(moMyBatisSession, msProcIdGetContractRegistered);
		assertTrue(result);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckAwardReviewStatus2() throws ApplicationException
	{
		Boolean result = moAwardService.checkAwardReviewStatus(moMyBatisSession, msProcIdInvalid);
		assertTrue(result);
	}

	@Test
	public void testUpdateContractStartEndDates1() throws ApplicationException
	{
		Boolean result = moAwardService.updateContractStartEndDates(moMyBatisSession, msEvalPoolMappingId,
				msContractId, msContractStartDate, msContractEndDate);
		assertFalse(result);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardDetails0Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardId1Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardDocuments2Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardDocuments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardReviewStatus3Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardReviewStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceremoveAwardDocuments4Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.removeAwardDocuments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceinsertAwardDocumentDetails5Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.insertAwardDocumentDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardsDetails6Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardsDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardStatus7Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateRelatedProposal8Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateRelatedProposal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardReviewStatus9Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardReviewStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceactionGetawardAndContractsList10Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.actionGetawardAndContractsList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchDocumentsForAwardDocTask11Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchDocumentsForAwardDocTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceaptProgressView12Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.aptProgressView(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceremoveAwardTaskDocs13Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.removeAwardTaskDocs(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicesaveAwardDocumentConfig14Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.saveAwardDocumentConfig(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceinsertAwardTaskDocDetails15Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.insertAwardTaskDocDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceawardAndContractsCount16Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.awardAndContractsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardEPinDetails17Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardEPinDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAmountProviderDetails18Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAmountProviderDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceassignAwardEpin19Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.assignAwardEpin(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardDetailsFromTask20Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardDetailsFromTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicectNotCancelledClosedRegistered21Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.ctNotCancelledClosedRegistered(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicecheckIfAllReqAwardDocsUploaded22Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.checkIfAllReqAwardDocsUploaded(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicegetAgencyIdForAwardId23Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.getAgencyIdForAwardId(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicegetContractRegistered24Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.getContractRegistered(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateContractBudgetStatus25Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateContractBudgetStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardAmountForSelectedProposals26Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardAmountForSelectedProposals(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchOrgNameFromContractId27Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchOrgNameFromContractId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchDefaultConfigId28Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchDefaultDocumentConfigId(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardDocumentTypeForTask29Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardDocumentTypeForTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicecheckAwardReviewStatus30Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.checkAwardReviewStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateContractStartEndDates31Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateContractStartEndDates(null, null, null, null, null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardDetails0Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardId1Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardDocuments2Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardReviewStatus3Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardReviewStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceremoveAwardDocuments4Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.removeAwardDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceinsertAwardDocumentDetails5Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.insertAwardDocumentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardsDetails6Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardsDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardStatus7Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateRelatedProposal8Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateRelatedProposal(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardReviewStatus9Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardReviewStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceactionGetawardAndContractsList10Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.actionGetawardAndContractsList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchDocumentsForAwardDocTask11Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchDocumentsForAwardDocTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceaptProgressView12Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.aptProgressView(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceremoveAwardTaskDocs13Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.removeAwardTaskDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicesaveAwardDocumentConfig14Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.saveAwardDocumentConfig(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceinsertAwardTaskDocDetails15Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.insertAwardTaskDocDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceawardAndContractsCount16Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.awardAndContractsCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardEPinDetails17Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardEPinDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAmountProviderDetails18Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAmountProviderDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceassignAwardEpin19Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.assignAwardEpin(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardDetailsFromTask20Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardDetailsFromTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicectNotCancelledClosedRegistered21Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.ctNotCancelledClosedRegistered(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicecheckIfAllReqAwardDocsUploaded22Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.checkIfAllReqAwardDocsUploaded(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicegetAgencyIdForAwardId23Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.getAgencyIdForAwardId(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicegetContractRegistered24Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.getContractRegistered(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateContractBudgetStatus25Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateContractBudgetStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardAmountForSelectedProposals26Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardAmountForSelectedProposals(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchOrgNameFromContractId27Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchOrgNameFromContractId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchDefaultConfigId28Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchDefaultDocumentConfigId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardDocumentTypeForTask29Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardDocumentTypeForTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicecheckAwardReviewStatus30Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.checkAwardReviewStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateContractStartEndDates31Negative2()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateContractStartEndDates(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void updateContractStartEndDates() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractStartDate = "01/01/2001";
		String lsContractEndDate = "01/01/2010";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession,
				lsEvaluationPoolMappingId, lsContractId, lsContractStartDate, lsContractEndDate);
		assertTrue(loUpdateStatus);
	}

	@Test
	public void updateContractStartEndDates1() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractEndDate = "01/01/2010";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession,
				lsEvaluationPoolMappingId, lsContractId, null, lsContractEndDate);
		assertFalse(loUpdateStatus);
	}

	@Test
	public void updateContractStartEndDates2() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractStartDate = "";
		String lsContractEndDate = "01/01/2010";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession,
				lsEvaluationPoolMappingId, lsContractId, lsContractStartDate, lsContractEndDate);
		assertFalse(loUpdateStatus);
	}

	@Test
	public void updateContractStartEndDates3() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractStartDate = "01/01/2010";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession,
				lsEvaluationPoolMappingId, lsContractId, lsContractStartDate, null);
		assertFalse(loUpdateStatus);
	}

	@Test
	public void updateContractStartEndDates4() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractStartDate = "01/01/2010";
		String lsContractEndDate = "";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession,
				lsEvaluationPoolMappingId, lsContractId, lsContractStartDate, lsContractEndDate);
		assertFalse(loUpdateStatus);
	}

	@Test
	public void updateContractStartEndDates5() throws ApplicationException
	{
		String lsContractId = "589";
		String lsContractStartDate = "01/01/2010";
		String lsContractEndDate = "";
		AwardService loAwardService = new AwardService();
		Boolean loUpdateStatus = loAwardService.updateContractStartEndDates(moMyBatisSession, null, lsContractId,
				lsContractStartDate, lsContractEndDate);
		assertFalse(loUpdateStatus);
	}

	@Test(expected = ApplicationException.class)
	public void updateContractStartEndDates6() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "291";
		String lsContractId = "589";
		String lsContractStartDate = "01/01/2001";
		String lsContractEndDate = "01/01/2010";
		AwardService loAwardService = new AwardService();
		loAwardService.updateContractStartEndDates(null, lsEvaluationPoolMappingId, lsContractId, lsContractStartDate,
				lsContractEndDate);
	}

}