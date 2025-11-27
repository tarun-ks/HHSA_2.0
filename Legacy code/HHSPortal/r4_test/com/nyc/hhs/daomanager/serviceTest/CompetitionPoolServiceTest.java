package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import com.nyc.hhs.daomanager.service.CompetitionPoolService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AwardsContractSummaryBean;
import com.nyc.hhs.model.EvaluationGroupAwardBean;
import com.nyc.hhs.model.EvaluationGroupsProposalBean;
import com.nyc.hhs.model.EvaluationSummaryBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.SelectionDetailsSummaryBean;

public class CompetitionPoolServiceTest
{
	CompetitionPoolService competitionPoolService = new CompetitionPoolService();
	private static SqlSession moSession = null; // SQL Session
	private String msInputParam = "procurement_s3";;
	private String msProcId = "171";
	private String msProcIdInvalid = "^%$SAD";
	private SelectionDetailsSummaryBean moSelectionDetailsSummaryBean = new SelectionDetailsSummaryBean();
	private int miContractSourceId = 2;
	private int miContractTypeId = 2;
	private int miStartNode = 2;
	private int miEndNode = 2;
	private String msOrganizationId = "r3_org";
	private AwardsContractSummaryBean moAwardsContractSummaryBean = new AwardsContractSummaryBean();
	private String msProcId1 = "157";
	private String msProcId2 = "132";
	private String msEvalGroupId = "232";
	private String msEvalGroupId1 = "155";
	private String msEvalGroupId2 = "90";
	private String msCompPoolId = "150";
	private EvaluationGroupAwardBean moEvaluationGroupAwardBean = new EvaluationGroupAwardBean();
	private Map<String, Object> moInputParam = new HashMap<String, Object>();
	private String msCompPoolStatus = "136";
	private String msEvalPoolMappingId = "144";
	private String msEvalPoolMappingIdInvalid = "%$#FDR$";
	private String msProcId3 = "112";
	private String msProcId4 = "114";
	private String msUserId = "city_43";

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
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}finally{
			moSession.rollback();
			moSession.close();
		}
	}

	/**
	 * Tests the method to fetch EvaluationGroupId
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationGroupId() throws ApplicationException
	{
		String lsEvaluationGroupId = competitionPoolService.fetchEvaluationGroupId(moSession, msProcId3);
		assertNotNull(lsEvaluationGroupId);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvaluationGroupId
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationGroupIdApplicationException() throws Exception
	{
		competitionPoolService.fetchEvaluationGroupId(moSession, null);
	}

	/**
	 * Tests the method to fetch EvalGroupProposalCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvalGroupProposalCount() throws ApplicationException
	{
		EvaluationGroupsProposalBean loEvalBean = new EvaluationGroupsProposalBean();
		loEvalBean.setProcurementId(msProcId3);
		Integer loEvalGroupProposalCount = competitionPoolService.fetchEvalGroupProposalCount(moSession, loEvalBean);
		assertNotNull(loEvalGroupProposalCount);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvalGroupProposalCount
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvalGroupProposalCountApplicationException() throws Exception
	{
		EvaluationGroupsProposalBean loEvalBean = new EvaluationGroupsProposalBean();
		loEvalBean.setProcurementId(msProcId3);
		competitionPoolService.fetchEvalGroupProposalCount(null, loEvalBean);
	}

	/**
	 * Tests the method to fetch EvaluationSummaryCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationSummaryCount() throws ApplicationException
	{
		EvaluationSummaryBean loEvalBean = new EvaluationSummaryBean();
		loEvalBean.setProcurementId(msProcId3);
		loEvalBean.setEvaluationGroupId(msEvalGroupId2);
		Integer loEvalGroupProposalCount = competitionPoolService.fetchEvaluationSummaryCount(moSession, loEvalBean);
		assertNotNull(loEvalGroupProposalCount);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvaluationSummaryCount
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationSummaryCountApplicationException() throws Exception
	{
		EvaluationSummaryBean loEvalBean = new EvaluationSummaryBean();
		loEvalBean.setProcurementId(msProcId3);
		loEvalBean.setEvaluationGroupId(msEvalGroupId2);
		competitionPoolService.fetchEvaluationSummaryCount(null, loEvalBean);
	}

	/**
	 * Tests the method to update Competition Pool Status
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateCompPoolStatus1() throws ApplicationException
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.IS_EVAL_GRP, HHSConstants.ONE);
		loDataMap.put(HHSConstants.EVENT_NAME, "Update Comp Pool");
		loDataMap.put(HHSConstants.EVENT_TYPE, "Update Comp Pool");
		loDataMap.put(HHSConstants.USER_ID, msUserId);
		loDataMap.put(HHSConstants.EVALUATION_GROUP_ID, msEvalGroupId2);
		loDataMap.put(HHSConstants.COMPETITION_POOL_ID_COL, msCompPoolId);
		loDataMap.put("asProposalStatus", "18");
		loDataMap.put("asProcurementId", msProcId3);
		loDataMap.put(HHSConstants.COMPETITION_POOL_PROPOSAL_RECEIVED, 140);
		loDataMap.put(HHSConstants.COMPETITION_POOL_NO_PROPOSALS, 136);
		Boolean loCompPoolStatus = competitionPoolService.updateCompPoolStatus(moSession, loDataMap);
		assertTrue(loCompPoolStatus);
	}

	/**
	 * This method tests occurring of application exception while updating
	 * Competition Pool Status
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateCompPoolStatusApplicationException() throws Exception
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put(HHSConstants.IS_EVAL_GRP, HHSConstants.ONE);
		loDataMap.put(HHSConstants.EVENT_NAME, "Update Comp Pool");
		loDataMap.put(HHSConstants.EVENT_TYPE, "Update Comp Pool");
		loDataMap.put(HHSConstants.USER_ID, msUserId);
		loDataMap.put(HHSConstants.EVALUATION_GROUP_ID, "@ESQA#$");
		loDataMap.put(HHSConstants.COMPETITION_POOL_ID_COL, msCompPoolId);
		loDataMap.put("asProposalStatus", "18");
		loDataMap.put("asProcurementId", msProcId3);
		loDataMap.put(HHSConstants.COMPETITION_POOL_PROPOSAL_RECEIVED, 140);
		loDataMap.put(HHSConstants.COMPETITION_POOL_NO_PROPOSALS, 136);
		competitionPoolService.updateCompPoolStatus(null, loDataMap);
	}

	/**
	 * Tests the method to insert Evaluation Group
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertEvaluationGroup1() throws ApplicationException
	{
		Procurement loProcBean = new Procurement();
		loProcBean.setProcurementId(msProcId2);
		loProcBean.setCreatedBy("agency_14");
		loProcBean.setModifiedBy("agency_14");
		loProcBean.setEvaluationGroupTitle("Evaluation Group");
		loProcBean.setEvalGroupStatus("131");
		Boolean lsCloseGroupFlag = Boolean.TRUE;
		Boolean loCompPoolStatus = competitionPoolService
				.insertEvaluationGroup(moSession, loProcBean, lsCloseGroupFlag);
		assertTrue(loCompPoolStatus);
	}

	/**
	 * Tests the method to insert Evaluation Group
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertEvaluationGroup2() throws ApplicationException
	{
		Procurement loProcBean = new Procurement();
		Boolean lsCloseGroupFlag = Boolean.FALSE;
		Boolean loCompPoolStatus = competitionPoolService
				.insertEvaluationGroup(moSession, loProcBean, lsCloseGroupFlag);
		assertFalse(loCompPoolStatus);
	}

	/**
	 * Tests the method to insert Evaluation Group
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertEvaluationGroup3() throws ApplicationException
	{
		Procurement loProcBean = new Procurement();
		Boolean lsCloseGroupFlag = null;
		Boolean loCompPoolStatus = competitionPoolService
				.insertEvaluationGroup(moSession, loProcBean, lsCloseGroupFlag);
		assertFalse(loCompPoolStatus);
	}

	/**
	 * This method tests occurring of application exception while inserting
	 * EvaluationGroup
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertEvaluationGroupApplicationException() throws Exception
	{
		Boolean loCloseGroupFlag = Boolean.TRUE;
		Procurement loProcBean = new Procurement();
		competitionPoolService.insertEvaluationGroup(null, loProcBean, loCloseGroupFlag);
	}

	/**
	 * Tests the method to update EvalGroup For CloseSubmissions
	 * 
	 * @throws ApplicationException
	 */
	/*
	 * @Test public void testUpdateEvalGroupForCloseSubmissions() throws
	 * ApplicationException { Map<String, Object> loInputParam = new
	 * HashMap<String, Object>(); loInputParam.put("userId", "city_43");
	 * loInputParam.put("evaluationGroupId", 89); Boolean loCompPoolStatus =
	 * competitionPoolService.updateEvalGroupForCloseSubmissions(moSession,
	 * loInputParam); assertTrue(loCompPoolStatus); }
	 *//**
	 * This method tests occurring of application exception while updating
	 * EvalGroup For CloseSubmissions
	 * 
	 * @throws Exception
	 */
	/*
	 * @Test(expected = Exception.class) public void
	 * testUpdateEvalGroupForCloseSubmissionsApplicationException() throws
	 * Exception {
	 * competitionPoolService.updateEvalGroupForCloseSubmissions(moSession,
	 * null); }
	 */

	/**
	 * Test method to update the status of EvaluationGroup
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateEvalGroupStatus() throws ApplicationException
	{
		moInputParam.put(HHSConstants.IS_EVAL_GRP, HHSConstants.ONE);
		moInputParam.put(HHSConstants.EVENT_NAME, "Update Eval Group");
		moInputParam.put(HHSConstants.EVENT_TYPE, "Update Eval Group");
		moInputParam.put(HHSConstants.USER_ID, msUserId);
		moInputParam.put(HHSConstants.EVALUATION_GROUP_ID, msEvalGroupId1);
		Boolean loIsUpdated = competitionPoolService.updateEvalGroupStatus(moSession, moInputParam);
		assertFalse(loIsUpdated);
	}

	/**
	 * Test method to update the status of EvaluationGroup
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateEvalGroupStatus1() throws ApplicationException
	{
		moInputParam.put(HHSConstants.IS_EVAL_GRP, HHSConstants.ONE);
		moInputParam.put(HHSConstants.EVENT_NAME, "Update Eval Group");
		moInputParam.put(HHSConstants.EVENT_TYPE, "Update Eval Group");
		moInputParam.put(HHSConstants.USER_ID, msUserId);
		moInputParam.put(HHSConstants.EVALUATION_GROUP_ID, msEvalGroupId1);
		Boolean loIsUpdated = competitionPoolService.updateEvalGroupStatus(moSession, moInputParam, true);
		assertFalse(loIsUpdated);
	}

	/**
	 * Test method to check for application Exception while updating Group
	 * status
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvalGroupStatusApplicationException() throws ApplicationException
	{
		Map<String, Object> loInputParam = new HashMap<String, Object>();
		loInputParam.put(HHSConstants.EVALUATION_GROUP_ID, 155);
		competitionPoolService.updateEvalGroupStatus(null, loInputParam);
	}

	/**
	 * Tests the method to insert Evaluation Group Competition Mapping when
	 * loUpdateStatusFlag = true
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertGroupCompetitionMapping1() throws ApplicationException
	{
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		loRFPReleaseBean.setProcurementId("137");
		loRFPReleaseBean.setEvalGroupStatus("136");
		loRFPReleaseBean.setCreatedByUserId("city_43");
		loRFPReleaseBean.setModifiedByUserId("city_43");
		Boolean loUpdateStatusFlag = Boolean.TRUE;
		Boolean loCompPoolStatus = competitionPoolService.insertGroupCompetitionMapping(moSession, loRFPReleaseBean,
				loUpdateStatusFlag);
		assertTrue(loCompPoolStatus);
	}

	/**
	 * Tests the method to insert Evaluation Group Competition Mapping when
	 * loUpdateStatusFlag = false
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertGroupCompetitionMapping2() throws ApplicationException
	{
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		Boolean loUpdateStatusFlag = Boolean.FALSE;
		Boolean loCompPoolStatus = competitionPoolService.insertGroupCompetitionMapping(moSession, loRFPReleaseBean,
				loUpdateStatusFlag);
		assertFalse(loCompPoolStatus);
	}

	/**
	 * This method tests occurring of application exception while inserting
	 * Evaluation Group Competition Mapping
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertGroupCompetitionMappingApplicationException() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = new RFPReleaseBean();
		Boolean loUpdateStatusFlag = Boolean.TRUE;
		competitionPoolService.insertGroupCompetitionMapping(null, loRFPReleaseBean, loUpdateStatusFlag);
	}

	/**
	 * Tests the method to assign Evaluation Group
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testAssignEvaluationGroup1() throws ApplicationException
	{
		String lsProposalId = "101";
		String lsProcurementId = "14";
		Boolean loValidateStatus = true;
		Boolean loAssignStatus = competitionPoolService.assignEvaluationGroup(moSession, lsProposalId, lsProcurementId,
				loValidateStatus);
		assertTrue(loAssignStatus);
	}

	/**
	 * Tests the method to assign Evaluation Group when loValidateStatus = null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testAssignEvaluationGroup3() throws ApplicationException
	{
		String lsProposalId = "";
		String lsProcurementId = "";
		Boolean loValidateStatus = null;
		Boolean loAssignStatus = competitionPoolService.assignEvaluationGroup(moSession, lsProposalId, lsProcurementId,
				loValidateStatus);
		assertFalse(loAssignStatus);
	}

	/**
	 * Tests the method to assign Evaluation Group when loValidateStatus = false
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testAssignEvaluationGroup4() throws ApplicationException
	{
		String lsProposalId = "";
		String lsProcurementId = "";
		Boolean loValidateStatus = false;
		Boolean loAssignStatus = competitionPoolService.assignEvaluationGroup(moSession, lsProposalId, lsProcurementId,
				loValidateStatus);
		assertFalse(loAssignStatus);
	}

	/**
	 * This method tests occurring of application exception while assigning
	 * Evaluation Group
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testAssignEvaluationGroupApplicationException() throws Exception
	{
		String lsProposalId = "101";
		String lsProcurementId = "14";
		boolean loValidateStatus = true;
		competitionPoolService.assignEvaluationGroup(null, lsProposalId, lsProcurementId, loValidateStatus);
	}

	/**
	 * Tests the method to fetch GroupTitle And DateGroupId
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupTitleAndDateGroupId() throws ApplicationException
	{
		Map<String, String> loGroupTitleAndDate = new HashMap<String, String>();
		loGroupTitleAndDate = competitionPoolService.fetchGroupTitleAndDateGroupId(moSession, "114", "101");
		assertNotNull(loGroupTitleAndDate);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * GroupTitle And DateGroupId
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testFetchGroupTitleAndDateGroupIdApplicationException() throws Exception
	{
		competitionPoolService.fetchGroupTitleAndDateGroupId(moSession, null, null);
	}

	/**
	 * Tests the method to fetch GroupTitle And Date
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupTitleAndDate() throws ApplicationException
	{
		Map<String, String> loGroupTitleAndDate = new HashMap<String, String>();
		loGroupTitleAndDate = competitionPoolService.fetchGroupTitleAndDate(moSession, "129");
		assertNotNull(loGroupTitleAndDate);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * GroupTitle And Date
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testFetchGroupTitleAndDateApplicationException() throws Exception
	{
		competitionPoolService.fetchGroupTitleAndDate(moSession, null);
	}

	/**
	 * Tests the method to fetch CompetitionPool Title
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchCompetitionPoolTitle1() throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = competitionPoolService.fetchCompetitionPoolTitles(moSession,
				"114", "101");
		assertNotNull(loCompetitionPoolList);
	}

	/**
	 * Tests the method to fetch CompetitionPool Title when procurement id is
	 * null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchCompetitionPoolTitle3() throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = competitionPoolService.fetchCompetitionPoolTitles(moSession,
				"114", null);
		assertNotNull(loCompetitionPoolList);
	}

	/**
	 * Tests the method to fetch CompetitionPool Title
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchCompetitionPoolTitle2() throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = competitionPoolService.fetchCompetitionPoolTitles(moSession,
				null, msProcId4);
		assertNotNull(loCompetitionPoolList);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * CompetitionPool Title
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testFetchCompetitionPoolTitleApplicationException() throws Exception
	{
		competitionPoolService.fetchCompetitionPoolTitles(moSession, null, null);
	}

	/**
	 * Tests the method to fetch All EvaluationGroups
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAllEvaluationGroups() throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = competitionPoolService.fetchAllEvaluationGroups(moSession,
				msProcId4);
		assertNotNull(loCompetitionPoolList);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvaluationGroups
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testFetchAllEvaluationGroupsApplicationException() throws Exception
	{
		competitionPoolService.fetchAllEvaluationGroups(moSession, null);
	}

	/**
	 * Tests the method to fetch EvaluationGroupProposal
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationGroupProposal() throws ApplicationException
	{
		EvaluationGroupsProposalBean loEvalGroupProposalBean = new EvaluationGroupsProposalBean();
		loEvalGroupProposalBean.setProcurementId(msProcId4);
		List<EvaluationGroupsProposalBean> loEvaluationGroupProposalList = competitionPoolService
				.fetchEvaluationGroupProposal(moSession, loEvalGroupProposalBean);
		assertNotNull(loEvaluationGroupProposalList);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvaluationGroupProposal
	 * 
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationGroupProposalApplicationException() throws Exception
	{
		EvaluationGroupsProposalBean loEvalGroupProposalBean = new EvaluationGroupsProposalBean();
		loEvalGroupProposalBean.setProcurementId(msProcId4);
		competitionPoolService.fetchEvaluationGroupProposal(null, loEvalGroupProposalBean);
	}

	/**
	 * Tests the method to fetch EvaluationSummary
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationSummary() throws ApplicationException
	{
		EvaluationSummaryBean loEvaluationSummaryBean = new EvaluationSummaryBean();
		loEvaluationSummaryBean.setProcurementId(msProcId4);
		List<EvaluationSummaryBean> loEvaluationGroupProposalList = competitionPoolService.fetchEvaluationSummary(
				moSession, loEvaluationSummaryBean);
		assertNotNull(loEvaluationGroupProposalList);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * EvaluationSummary
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationSummaryApplicationException() throws Exception
	{
		EvaluationSummaryBean loEvaluationSummaryBean = new EvaluationSummaryBean();
		loEvaluationSummaryBean.setProcurementId(msProcId4);
		List<EvaluationSummaryBean> loEvaluationGroupProposalList = competitionPoolService.fetchEvaluationSummary(null,
				loEvaluationSummaryBean);
	}

	/**
	 * Tests the method to fetch AllCompetitionPools
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchAllCompetitionPools() throws ApplicationException
	{
		List<Map<String, String>> loCompetitionPoolList = competitionPoolService.fetchAllCompetitionPools(moSession,
				msProcId4);
		assertNotNull(loCompetitionPoolList);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * AllCompetitionPools
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testFetchAllCompetitionPoolsApplicationException() throws Exception
	{
		competitionPoolService.fetchAllCompetitionPools(moSession, null);
	}

	/**
	 * Tests the method to fetch CompetitionPoolData
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetCompetitionPoolData() throws ApplicationException
	{
		List<String> loSelectedPool = competitionPoolService.getCompetitionPoolData(moSession, msProcId4);
		assertNotNull(loSelectedPool);
	}

	/**
	 * Tests the method to fetch CompetitionPoolData with procurement Id = null
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetCompetitionPoolData2() throws ApplicationException
	{
		competitionPoolService.getCompetitionPoolData(moSession, null);
	}

	/**
	 * This method tests occurring of application exception while fetching
	 * CompetitionPoolData
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testGetCompetitionPoolDataApplicationException() throws Exception
	{
		competitionPoolService.getCompetitionPoolData(moSession, "abc");
	}

	/**
	 * Tests the method to save CompetitionPool
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveCompetitionPool1() throws ApplicationException
	{
		moSession.rollback();
		List<String> loSelectedPool = new ArrayList<String>();
		loSelectedPool.add("Compt Pool1");
		loSelectedPool.add("Compt Pool2");
		Boolean loSaveFlag = competitionPoolService.saveCompetitionPool(moSession, "132", "city_43", loSelectedPool);
		assertTrue(loSaveFlag);
	}

	@Test
	public void testSaveCompetitionPool3() throws ApplicationException
	{
		List<String> loSelectedPool = new ArrayList<String>();
		loSelectedPool.add("pool_11");
		loSelectedPool.add("pool_l2");
		Boolean loSaveFlag = competitionPoolService.saveCompetitionPool(moSession, null, "city_43", loSelectedPool);
		assertFalse(loSaveFlag);
	}

	/**
	 * Tests the method to save CompetitionPool
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testSaveCompetitionPool2() throws ApplicationException
	{
		List<String> loSelectedPool = new ArrayList<String>();
		loSelectedPool.add("pool1");
		loSelectedPool.add("pool2");
		Boolean loSaveFlag = competitionPoolService.saveCompetitionPool(moSession, null, null, loSelectedPool);
		assertFalse(loSaveFlag);
	}

	/**
	 * This method tests occurring of application exception while saving
	 * CompetitionPool
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void testSaveCompetitionPoolApplicationException() throws Exception
	{
		List<String> loSelectedPool = new ArrayList<String>();
		loSelectedPool.add("pool3");
		competitionPoolService.saveCompetitionPool(moSession, msProcId3, "", loSelectedPool);
	}

	/**
	 * This test method is to fetch the proposal details
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchProposalAndOrgName() throws ApplicationException
	{
		String lsProposalId = "101";
		Map<String, String> loHeaderMap = competitionPoolService.fetchProposalAndOrgName(moSession, lsProposalId);
		assertNotNull(loHeaderMap);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalAndOrgNameApplicationException() throws ApplicationException
	{
		String lsProposalId = "101";
		competitionPoolService.fetchProposalAndOrgName(null, lsProposalId);
	}

	/**
	 * Test function to fetch RFP release status before R4 flag
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchRfpReleasedBeforeR4Flag() throws ApplicationException
	{
		String lsProcurementId = "132";
		String loRfpBeforeR4Flag = competitionPoolService.fetchRfpReleasedBeforeR4Flag(moSession, lsProcurementId);
		assertNotNull(loRfpBeforeR4Flag);
	}

	/**
	 * Test method to cover the Application exception
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchRfpReleasedBeforeR4FlagApplicationException() throws ApplicationException
	{
		String lsProcurementId = "132";
		competitionPoolService.fetchRfpReleasedBeforeR4Flag(null, lsProcurementId);
	}

	/**
	 * Test method to cover the Application Exception while updating the
	 * procurement status based on the Evaluation Group
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcurementStatusBasedOnGroupApplicationException() throws ApplicationException
	{
		moInputParam.put(HHSConstants.PROCUREMENT_ID, msProcId2);
		Boolean loUpdateStatus = competitionPoolService.updateProcurementStatusBasedOnGroup(null, moInputParam);
		assertFalse(loUpdateStatus);
	}

	/**
	 * 
	 * Test method to check whether the given Procurement is open ended or not
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckIfOpenEndedZeroValue() throws ApplicationException
	{
		Boolean loIsOpenEnded = competitionPoolService.checkIfOpenEndedZeroValue(moSession, msProcId2);
		assertTrue(loIsOpenEnded);
	}

	/**
	 * 
	 * Test method to cover application exception while checking whether the
	 * given Procurement is open ended or not
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckIfOpenEndedZeroValueApplicationException() throws ApplicationException
	{
		Boolean loIsOpenEnded = competitionPoolService.checkIfOpenEndedZeroValue(null, msProcId2);
		assertFalse(loIsOpenEnded);
	}

	/**
	 * 
	 * Test method to fetch TypeAheadNameList
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchTypeAheadNameList() throws ApplicationException
	{
		String lsQueryId = "fetchProcurementContractTitleList";
		List<Map<String, String>> resultList = competitionPoolService.fetchTypeAheadNameList(moSession, lsQueryId,
				msInputParam, msProcId);
		assertNotNull(resultList);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * TypeAheadNameList
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchTypeAheadNameListApplicationException() throws ApplicationException
	{
		List<Map<String, String>> resultList = competitionPoolService.fetchTypeAheadNameList(moSession, null, null,
				null);
		assertNotNull(resultList);
	}

	/**
	 * 
	 * Test method to fetch GroupSelectionDetailsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupSelectionDetailsCount() throws ApplicationException
	{
		Integer result = competitionPoolService.fetchGroupSelectionDetailsCount(moSession, msProcId);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * GroupSelectionDetailsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchGroupSelectionDetailsCountApplicationException() throws ApplicationException
	{
		Integer result = competitionPoolService.fetchGroupSelectionDetailsCount(moSession, msProcIdInvalid);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to fetch GroupSelectionDetailsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupSelectionDetailsCount1() throws ApplicationException
	{
		moSelectionDetailsSummaryBean.setProcurementId(null);
		moSelectionDetailsSummaryBean.setContractSourceId(miContractSourceId);
		moSelectionDetailsSummaryBean.setContractTypeId(miContractTypeId);
		moSelectionDetailsSummaryBean.setOrganizationId(msOrganizationId);
		moSelectionDetailsSummaryBean.setStartNode(miStartNode);
		moSelectionDetailsSummaryBean.setEndNode(miEndNode);
		List<SelectionDetailsSummaryBean> resultList = competitionPoolService.fetchGroupSelectionDetails(moSession,
				moSelectionDetailsSummaryBean);
		assertNotNull(resultList);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * GroupSelectionDetailsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchGroupSelectionDetailsCountApplicationException1() throws ApplicationException
	{
		List<SelectionDetailsSummaryBean> resultList = competitionPoolService.fetchGroupSelectionDetails(moSession,
				moSelectionDetailsSummaryBean);
		assertNotNull(resultList);
	}

	/**
	 * 
	 * Test method to fetch GroupAwardsContractsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupAwardsContractsCount() throws ApplicationException
	{
		moAwardsContractSummaryBean.setProcurementId(msProcId1);
		moAwardsContractSummaryBean.setEvaluationGroupId(msEvalGroupId);
		Integer result = competitionPoolService.fetchGroupAwardsContractsCount(moSession, moAwardsContractSummaryBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * GroupAwardsContractsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchGroupAwardsContractsCountApplicationException1() throws ApplicationException
	{
		Integer result = competitionPoolService.fetchGroupAwardsContractsCount(moSession, moAwardsContractSummaryBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to fetch GroupAwardsContracts
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchGroupAwardsContracts() throws ApplicationException
	{
		moAwardsContractSummaryBean.setProcurementId(msProcId1);
		moAwardsContractSummaryBean.setEvaluationGroupId(msEvalGroupId);
		List<AwardsContractSummaryBean> result = competitionPoolService.fetchGroupAwardsContracts(moSession,
				moAwardsContractSummaryBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * GroupAwardsContracts
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchGroupAwardsContractsApplicationException1() throws ApplicationException
	{
		List<AwardsContractSummaryBean> result = competitionPoolService.fetchGroupAwardsContracts(moSession,
				moAwardsContractSummaryBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to fetch EvaluationGroupAwardsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationGroupAwardsCount() throws ApplicationException
	{
		Integer result = competitionPoolService.fetchEvaluationGroupAwardsCount(moSession, msProcId);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * EvaluationGroupAwardsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationGroupAwardsCountApplicationException1() throws ApplicationException
	{
		Integer result = competitionPoolService.fetchEvaluationGroupAwardsCount(moSession, msProcIdInvalid);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to fetch EvaluationGroupAwards
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEvaluationGroupAwards() throws ApplicationException
	{
		moEvaluationGroupAwardBean.setProcurementId(msProcId);
		moEvaluationGroupAwardBean.setStartNode(miStartNode);
		moEvaluationGroupAwardBean.setEndNode(miEndNode);
		List<EvaluationGroupAwardBean> result = competitionPoolService.fetchEvaluationGroupAwards(moSession,
				moEvaluationGroupAwardBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to cover application exception while fetching
	 * EvaluationGroupAwardsCount
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationGroupAwardsApplicationException1() throws ApplicationException
	{
		moEvaluationGroupAwardBean.setProcurementId(msProcIdInvalid);
		List<EvaluationGroupAwardBean> result = competitionPoolService.fetchEvaluationGroupAwards(moSession,
				moEvaluationGroupAwardBean);
		assertNotNull(result);
	}

	/**
	 * 
	 * Test method to fetch EvaluationGroupAwards
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateProcurementStatusBasedOnGroup() throws ApplicationException
	{
		moInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, msProcId2);
		Boolean result = competitionPoolService.updateProcurementStatusBasedOnGroup(moSession, moInputParam, true);
		assertTrue(result);
	}

	/**
	 * Test method to update EvalPoolMappingStatus
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateEvalPoolMappingStatus() throws ApplicationException
	{
		moInputParam.put(HHSConstants.IS_EVAL_GRP, null);
		moInputParam.put(HHSConstants.EVENT_NAME, "Update Eval Mapping");
		moInputParam.put(HHSConstants.EVENT_TYPE, "Update Eval Mapping");
		moInputParam.put(HHSConstants.USER_ID, msUserId);
		moInputParam.put(HHSConstants.COMPETITION_POOL_STATUS, msCompPoolStatus);
		moInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvalPoolMappingId);
		Boolean loIsUpdated = competitionPoolService.updateEvalPoolMappingStatus(moSession, moInputParam);
		assertTrue(loIsUpdated);
	}

	/**
	 * Test method to update EvalPoolMappingStatus
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateEvalPoolMappingStatus1() throws ApplicationException
	{
		moInputParam.put(HHSConstants.IS_EVAL_GRP, null);
		moInputParam.put(HHSConstants.EVENT_NAME, "Update Eval Mapping");
		moInputParam.put(HHSConstants.EVENT_TYPE, "Update Eval Mapping");
		moInputParam.put(HHSConstants.USER_ID, msUserId);
		moInputParam.put(HHSConstants.COMPETITION_POOL_STATUS, msCompPoolStatus);
		moInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvalPoolMappingId);
		Boolean loIsUpdated = competitionPoolService.updateEvalPoolMappingStatus(moSession, moInputParam, true);
		assertTrue(loIsUpdated);
	}

	/**
	 * 
	 * Test method to cover application exception while updating
	 * EvalPoolMappingStatus
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvalPoolMappingStatusApplicationException1() throws ApplicationException
	{
		moInputParam.put(HHSConstants.IS_EVAL_GRP, null);
		moInputParam.put(HHSConstants.EVENT_NAME, "Update Eval Mapping");
		moInputParam.put(HHSConstants.EVENT_TYPE, "Update Eval Mapping");
		moInputParam.put(HHSConstants.USER_ID, msUserId);
		moInputParam.put(HHSConstants.COMPETITION_POOL_STATUS, msCompPoolStatus);
		moInputParam.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, msEvalPoolMappingIdInvalid);
		Boolean result = competitionPoolService.updateEvalPoolMappingStatus(moSession, moInputParam);
		assertNotNull(result);
	}

	/**
	 * Test method to checkIfAwardApprovedForEvalPool
	 * @throws ApplicationException
	 */
	@Test
	public void testCheckIfAwardApprovedForEvalPool() throws ApplicationException
	{
		Boolean loIsUpdated = competitionPoolService.checkIfAwardApprovedForEvalPool(moSession, msEvalPoolMappingId);
		assertFalse(loIsUpdated);
	}

	/**
	 * Test method to checkIfAwardApprovedForEvalPool
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckIfAwardApprovedForEvalPoolApplicationException() throws ApplicationException
	{
		Boolean loIsUpdated = competitionPoolService.checkIfAwardApprovedForEvalPool(moSession,
				msEvalPoolMappingIdInvalid);
		assertTrue(loIsUpdated);
	}

	/**
	 * Test method to getEvaluationGroupCount
	 * @throws ApplicationException
	 */
	@Test
	public void testGetEvaluationGroupCount() throws ApplicationException
	{
		Integer loIsUpdated = competitionPoolService.getEvaluationGroupCount(moSession, msProcId4);
		assertNotNull(loIsUpdated);
	}

	/**
	 * Test method to getEvaluationGroupCount
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetEvaluationGroupCountApplicationException() throws ApplicationException
	{
		Integer loIsUpdated = competitionPoolService.getEvaluationGroupCount(moSession, "@#ADSF");
		assertNotNull(loIsUpdated);
	}

	/**
	 * Test method to getCompetitionPoolStatus
	 * @throws ApplicationException
	 */
	@Test
	public void testGetCompetitionPoolStatus() throws ApplicationException
	{
		String loIsUpdated = competitionPoolService.getCompetitionPoolStatus(moSession, msEvalPoolMappingId);
		assertNotNull(loIsUpdated);
	}

	/**
	 * Test method to getCompetitionPoolStatus
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetCompetitionPoolStatusApplicationException() throws ApplicationException
	{
		String loIsUpdated = competitionPoolService.getCompetitionPoolStatus(moSession, msEvalPoolMappingIdInvalid);
		assertNotNull(loIsUpdated);
	}

	/**
	 * Test method to getCompetitionPoolStatus
	 * @throws ApplicationException
	 */
	@Test
	public void testUpdateCompPoolStatusInPropResubmit() throws ApplicationException
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put("proposalId", "93");
		loDataMap.put("PROPOSAL_DRAFT", "17");
		loDataMap.put(HHSConstants.EVENT_NAME, "Update Comp Pool");
		loDataMap.put(HHSConstants.EVENT_TYPE, "Update Comp Pool");
		loDataMap.put(HHSConstants.USER_ID, msUserId);
		loDataMap.put(HHSConstants.EVALUATION_GROUP_ID, msEvalGroupId2);
		loDataMap.put(HHSConstants.COMPETITION_POOL_ID_COL, msCompPoolId);
		loDataMap.put("asProposalStatus", "18");
		loDataMap.put("asProcurementId", msProcId3);
		loDataMap.put("COMPETITION_POOL_PROPOSALS_RECEIVED", 140);
		loDataMap.put("COMPETITION_POOL_NO_PROPOSALS", 136);
		Boolean loIsUpdated = competitionPoolService.updateCompPoolStatusInPropResubmit(moSession, loDataMap, true);
		assertTrue(loIsUpdated);
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicesaveCompetitionPool0Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.saveCompetitionPool(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicegetCompetitionPoolData1Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.getCompetitionPoolData(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchAllCompetitionPools2Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchAllCompetitionPools(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationSummary3Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationGroupProposal4Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationGroupProposal(null, new EvaluationGroupsProposalBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchAllEvaluationGroups5Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchAllEvaluationGroups(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchCompetitionPoolTitles6Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchCompetitionPoolTitles(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupTitleAndDate7Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupTitleAndDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupTitleAndDateGroupId8Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupTitleAndDateGroupId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceassignEvaluationGroup9Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.assignEvaluationGroup(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceinsertGroupCompetitionMapping10Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.insertGroupCompetitionMapping(null, new RFPReleaseBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateEvalGroupStatus11Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		Map<String, Object> loInputParam = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolService.updateEvalGroupStatus(null, loInputParam);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceinsertEvaluationGroup12Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.insertEvaluationGroup(null, new Procurement(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateCompPoolStatus13Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolService.updateCompPoolStatus(null, loDataMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationSummaryCount14Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationSummaryCount(null, new EvaluationSummaryBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvalGroupProposalCount15Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvalGroupProposalCount(null, new EvaluationGroupsProposalBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationGroupId16Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationGroupId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchProposalAndOrgName17Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchProposalAndOrgName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchRfpReleasedBeforeR4Flag18Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchRfpReleasedBeforeR4Flag(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateProcurementStatusBasedOnGroup19Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		Map<String, Object> loInputParam = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolService.updateProcurementStatusBasedOnGroup(null, loInputParam);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicecheckIfOpenEndedZeroValue20Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.checkIfOpenEndedZeroValue(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateEvalPoolMappingStatus21Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolService.updateEvalPoolMappingStatus(null, loDataMap);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateEvalPoolMappingStatus22Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.updateEvalPoolMappingStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateEvalGroupStatus23Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.updateEvalGroupStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateProcurementStatusBasedOnGroup24Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.updateProcurementStatusBasedOnGroup(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationGroupAwards25Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationGroupAwards(null, new EvaluationGroupAwardBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchEvaluationGroupAwardsCount26Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchEvaluationGroupAwardsCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupAwardsContracts27Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupAwardsContracts(null, new AwardsContractSummaryBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupAwardsContractsCount28Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupAwardsContractsCount(null, new AwardsContractSummaryBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupSelectionDetails29Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupSelectionDetails(null, new SelectionDetailsSummaryBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchGroupSelectionDetailsCount30Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchGroupSelectionDetailsCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicefetchTypeAheadNameList31Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.fetchTypeAheadNameList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServiceupdateCompPoolStatusInPropResubmit32Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		try
		{
			loCompetitionPoolService.updateCompPoolStatusInPropResubmit(null, loDataMap, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicegetCompetitionPoolStatus33Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.getCompetitionPoolStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicegetEvaluationGroupCount34Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.getEvaluationGroupCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicecheckIfAwardApprovedForEvalPool35Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.checkIfAwardApprovedForEvalPool(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicesaveCompetitionPool0NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.saveCompetitionPool(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicegetCompetitionPoolData1NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.getCompetitionPoolData(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchAllCompetitionPools2NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchAllCompetitionPools(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationSummary3NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationGroupProposal4NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationGroupProposal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchAllEvaluationGroups5NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchAllEvaluationGroups(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchCompetitionPoolTitles6NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchCompetitionPoolTitles(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupTitleAndDate7NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupTitleAndDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupTitleAndDateGroupId8NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupTitleAndDateGroupId(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceassignEvaluationGroup9NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.assignEvaluationGroup(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceinsertGroupCompetitionMapping10NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.insertGroupCompetitionMapping(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateEvalGroupStatus11NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateEvalGroupStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceinsertEvaluationGroup12NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.insertEvaluationGroup(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateCompPoolStatus13NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateCompPoolStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationSummaryCount14NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationSummaryCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvalGroupProposalCount15NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvalGroupProposalCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationGroupId16NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationGroupId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchProposalAndOrgName17NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchProposalAndOrgName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchRfpReleasedBeforeR4Flag18NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchRfpReleasedBeforeR4Flag(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateProcurementStatusBasedOnGroup19NegativeApp()
			throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateProcurementStatusBasedOnGroup(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicecheckIfOpenEndedZeroValue20NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.checkIfOpenEndedZeroValue(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateEvalPoolMappingStatus21NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateEvalPoolMappingStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateEvalPoolMappingStatus22NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateEvalPoolMappingStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateEvalGroupStatus23NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateEvalGroupStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateProcurementStatusBasedOnGroup24NegativeApp()
			throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateProcurementStatusBasedOnGroup(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationGroupAwards25NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationGroupAwards(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchEvaluationGroupAwardsCount26NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchEvaluationGroupAwardsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupAwardsContracts27NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupAwardsContracts(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupAwardsContractsCount28NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupAwardsContractsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupSelectionDetails29NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupSelectionDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchGroupSelectionDetailsCount30NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchGroupSelectionDetailsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicefetchTypeAheadNameList31NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.fetchTypeAheadNameList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServiceupdateCompPoolStatusInPropResubmit32NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.updateCompPoolStatusInPropResubmit(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicegetCompetitionPoolStatus33NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.getCompetitionPoolStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicegetEvaluationGroupCount34NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.getEvaluationGroupCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testCompetitionPoolServicecheckIfAwardApprovedForEvalPool35NegativeApp() throws ApplicationException
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		loCompetitionPoolService.checkIfAwardApprovedForEvalPool(null, null);
	}

	@Test
	public void testmoveCompetitionPoolIdFromTemp1() throws ApplicationException
	{
		String lsProposalId = "447";
		String lsProcurementId = "564";
		Boolean loValidateStatus = true;
		Boolean loAssignStatus = competitionPoolService.moveCompetitionPoolIdFromTemp(moSession, lsProposalId,
				lsProcurementId, loValidateStatus);
		assertTrue(loAssignStatus);
	}

	@Test
	public void testmoveCompetitionPoolIdFromTemp3() throws ApplicationException
	{
		String lsProposalId = "";
		String lsProcurementId = "";
		Boolean loValidateStatus = null;
		Boolean loAssignStatus = competitionPoolService.moveCompetitionPoolIdFromTemp(moSession, lsProposalId,
				lsProcurementId, loValidateStatus);
		assertFalse(loAssignStatus);
	}

	@Test
	public void testmoveCompetitionPoolIdFromTemp4() throws ApplicationException
	{
		String lsProposalId = "";
		String lsProcurementId = "";
		Boolean loValidateStatus = false;
		Boolean loAssignStatus = competitionPoolService.moveCompetitionPoolIdFromTemp(moSession, lsProposalId,
				lsProcurementId, loValidateStatus);
		assertFalse(loAssignStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testmoveCompetitionPoolIdFromTempApplicationException() throws Exception
	{
		String lsProposalId = "101";
		String lsProcurementId = "14";
		boolean loValidateStatus = true;
		competitionPoolService.moveCompetitionPoolIdFromTemp(null, lsProposalId, lsProcurementId, loValidateStatus);
	}

	@Test(expected = java.lang.Exception.class)
	public void testCompetitionPoolServicemoveCompetitionPoolIdFromTemp9Negative()
	{
		CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
		try
		{
			loCompetitionPoolService.moveCompetitionPoolIdFromTemp(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
}
