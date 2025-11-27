package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ApplicationService;
import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.daomanager.service.SectionService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.EvaluationFilterBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class Release3100Test
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session
	private static ProposalService loProposalService = null;
	private static EvaluationService loEvaluationService = null;
	private static AwardService loAwardService = null;
	private static SectionService loSectionService = null;
	private static ApplicationService loApplicationService = null;

	public String lsProcId = "123";
	public String lsEvalGroupId = "345";
	public String lsCompPoolId = "456";
	public String lsEvalPoolId = "678";
	public String lsUserId = "agency_14";
	public String lsProposalId = "5467";
	public String lsContractId = "5467";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		loSectionService = new SectionService();
		loApplicationService = new ApplicationService();
		loProposalService = new ProposalService();
		loEvaluationService = new EvaluationService();
		loAwardService = new AwardService();
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
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
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
	
	
	@Test
	public void testUpdateStatusForCancelComp() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		Boolean lbUpdateStatus = loProposalService.updateStatusForCancelComp(moSession, loStatusInfoMap);
		assertTrue(lbUpdateStatus);
		assertNotNull(lbUpdateStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateStatusForCancelCompException() throws Exception
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		Boolean lbUpdateStatus = loProposalService.updateStatusForCancelComp(null, loStatusInfoMap);
		assertTrue(lbUpdateStatus);
		assertNotNull(lbUpdateStatus);
	}

	@Test
	public void testCheckEvalPoolCancelled() throws ApplicationException
	{
		Boolean lbCheckStatus = loProposalService.checkEvalPoolCancelled(moSession, lsEvalPoolId);
		assertTrue(!lbCheckStatus);
		assertNotNull(lbCheckStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckEvalPoolCancelledException() throws ApplicationException
	{
		Boolean lbCheckStatus = loProposalService.checkEvalPoolCancelled(null, lsEvalPoolId);
		assertTrue(!lbCheckStatus);
		assertNotNull(lbCheckStatus);
	}
	
	@Test
	public void testFetchCompTitleAndProcTitle() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		HashMap<String, String> loInfoMap = loEvaluationService.fetchCompTitleAndProcTitle(moSession, loStatusInfoMap);
		assertNotNull(loInfoMap);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchCompTitleAndProcTitleException() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		HashMap<String, String> loInfoMap = loEvaluationService.fetchCompTitleAndProcTitle(null, loStatusInfoMap);
		assertNotNull(loInfoMap);
	}

	@Test
	public void testFetchProviderIdList() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		List<String> loProviderList = loEvaluationService.fetchProviderIdList(moSession, loStatusInfoMap);
		assertNotNull(loProviderList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchProviderIdListException() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		List<String> loProviderList = loEvaluationService.fetchProviderIdList(null, loStatusInfoMap);
		assertNotNull(loProviderList);
	}
	
	@Test
	public void testCheckCompPoolCancelled() throws ApplicationException
	{
		Boolean lbCheckStatus = loProposalService.checkCompPoolCancelled(moSession, lsProposalId, Boolean.TRUE);
		assertTrue(!lbCheckStatus);
		assertNotNull(lbCheckStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckCompPoolCancelledException() throws ApplicationException
	{
		Boolean lbCheckStatus = loProposalService.checkCompPoolCancelled(null, lsProposalId, Boolean.TRUE);
		assertTrue(!lbCheckStatus);
		assertNotNull(lbCheckStatus);
	}

	@Test
	public void testFetchUpdateAfterApprovalStatus() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setProcurementId(lsProcId);
		loEvalBean.setEvaluationPoolMappingId(lsEvalPoolId);
		String lbCheckStatus = loEvaluationService.fetchUpdateAfterApprovalStatus(moSession, loEvalBean);
		assertNotNull(lbCheckStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchUpdateAfterApprovalStatusException() throws ApplicationException
	{
		EvaluationFilterBean loEvalBean = new EvaluationFilterBean();
		loEvalBean.setProcurementId(lsProcId);
		loEvalBean.setEvaluationPoolMappingId(lsEvalPoolId);
		String lbCheckStatus = loEvaluationService.fetchUpdateAfterApprovalStatus(null, loEvalBean);
		assertNotNull(lbCheckStatus);
	}
	
	@Test
	public void testUpdateAwardReviewStatusToUpdate() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		Boolean lbUpdateStatus = loAwardService.updateAwardReviewStatusToUpdate(moSession, loStatusInfoMap);
		assertTrue(lbUpdateStatus);
		assertNotNull(lbUpdateStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateAwardReviewStatusToUpdateException() throws ApplicationException
	{
		HashMap<String, String> loStatusInfoMap= new HashMap<String, String>();
		loStatusInfoMap.put(HHSConstants.PROCUREMENT_ID, lsProcId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_GROUP_ID, lsEvalGroupId);
		loStatusInfoMap.put(HHSConstants.COMPETITION_POOL_ID, lsCompPoolId);
		loStatusInfoMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loStatusInfoMap.put(HHSConstants.COMMENTS, "acs");
		loStatusInfoMap.put(HHSConstants.USER_ID,lsUserId);
		Boolean lbUpdateStatus = loAwardService.updateAwardReviewStatusToUpdate(null, loStatusInfoMap);
		assertTrue(lbUpdateStatus);
		assertNotNull(lbUpdateStatus);
	}
	
	@Test
	public void testFetchUpdatedContracts() throws ApplicationException
	{
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
		loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcId);
		loInputParamMap.put(HHSConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW));
		List<String> loContractList = loEvaluationService.fetchUpdatedContracts(moSession, loInputParamMap);
		assertNotNull(loContractList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchUpdatedContractsException() throws ApplicationException
	{
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
		loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcId);
		loInputParamMap.put(HHSConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW));
		List<String> loContractList = loEvaluationService.fetchUpdatedContracts(null, loInputParamMap);
		assertNotNull(loContractList);
	}
	
	@Test
	public void testUpdateAwardStatusList() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		Boolean loUpdateStatus = loAwardService.updateAwardStatusList(moSession, loContractList);
		assertNotNull(loUpdateStatus);
		assertTrue(loUpdateStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateAwardStatusListException() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		Boolean loUpdateStatus = loAwardService.updateAwardStatusList(null, loContractList);
		assertNotNull(loUpdateStatus);
		assertTrue(loUpdateStatus);
	}
	
	@Test
	public void testUpdateContractBudgetStatusList() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		HashMap<String, String> loInputParamMap = new HashMap<String, String>();
		loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
		loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcId);
		loInputParamMap.put(HHSConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW));
		Boolean loUpdateStatus = loAwardService.updateContractBudgetStatusList(moSession, loInputParamMap, loContractList);
		assertNotNull(loUpdateStatus);
		assertTrue(loUpdateStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateContractBudgetStatusListException() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		HashMap<String, String> loInputParamMap = new HashMap<String, String>();
		loInputParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolId);
		loInputParamMap.put(HHSConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_EVALUATIONS_COMPLETE));
		loInputParamMap.put(HHSConstants.USER_ID, lsUserId);
		loInputParamMap.put(HHSConstants.PROCUREMENT_ID_KEY, lsProcId);
		loInputParamMap.put(HHSConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW));
		Boolean loUpdateStatus = loAwardService.updateContractBudgetStatusList(moSession, loInputParamMap, loContractList);
		assertNotNull(loUpdateStatus);
		assertTrue(loUpdateStatus);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testFetchContractIdsForUtilityWorkflow() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		HashMap<String, String> loInputParamMap = new HashMap<String, String>();
		HashMap loInputMap = loEvaluationService.fetchContractIdsForUtilityWorkflow(loContractList,loInputParamMap, "100");
		assertNotNull(loInputMap);
	}
	
	@SuppressWarnings("rawtypes")
	@Test(expected = ApplicationException.class)
	public void testFetchContractIdsForUtilityWorkflowException() throws ApplicationException
	{
		List<String> loContractList = new ArrayList<String>();	
		loContractList.add(lsContractId);
		HashMap loInputMap = loEvaluationService.fetchContractIdsForUtilityWorkflow(loContractList,null, "100");
		assertNotNull(loInputMap);
	}
	
	@Test
	public void testGetSubSectionStatusForFilings() throws ApplicationException
	{
		HashMap<String,Object> loParamMap = new HashMap<String, Object>();
		loParamMap = loSectionService.getSubSectionStatusForFilings("accenture", "br_1442408235684" ,moSession);
		assertNotNull(loParamMap);
	}
	
	@SuppressWarnings("rawtypes")
	@Test(expected = ApplicationException.class)
	public void testGetSubSectionStatusForFilingsException() throws ApplicationException
	{
		HashMap<String,Object> loParamMap = new HashMap<String, Object>();
		loParamMap = loSectionService.getSubSectionStatusForFilings("accenture", "br_1442408235684" ,null);
	}
	
	@Test
	public void testDeleteFilingEntriesForCorporateStuctureChange() throws ApplicationException
	{
		HashMap aoHMSection = new HashMap();
		aoHMSection.put("brApplicationId", "br_1442408235684");
		aoHMSection.put("orgId", "accenture");
		Boolean loStatusFlag = loSectionService.deleteFilingEntriesForCorporateStuctureChange(aoHMSection ,moSession);
		assertTrue(loStatusFlag);
	}
	
	@Test(expected = ApplicationException.class)
	public void testDeleteFilingEntriesForCorporateStuctureChangeException() throws ApplicationException
	{
		HashMap aoHMSection = new HashMap();
		aoHMSection.put("brApplicationId", "br_1442408235684");
		aoHMSection.put("orgId", "accenture");
		Boolean loStatusFlag = loSectionService.deleteFilingEntriesForCorporateStuctureChange(aoHMSection ,null);
	}
	
}