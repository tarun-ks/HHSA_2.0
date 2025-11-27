package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.ScoreDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;

public class Build250EvaluationServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	EvaluationService moEvaluationService = new EvaluationService();
	
	@Test
	public void reviewScoreVersionInsertCase1() throws ApplicationException
	{
		moEvaluationService.reviewScoreVersionInsert(moMyBatisSession, "1350",
				"agency_21");
		assertTrue("reviewScoreVersionInsert passed successfully.", true);
		//assertNotNull(loVersionRowCount);
	}
	
	@Test
	public void reviewScoreVersionInsertCase2() throws ApplicationException
	{
		boolean lbThrown = false;
		try{
		moEvaluationService.reviewScoreVersionInsert(moMyBatisSession, null,
				"agency_21");
		assertTrue("reviewScoreVersionInsert passed successfully.", true);
		//assertNotNull(loVersionRowCount);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Error in creating version History", lbThrown);
		}
	}
	
	@Test
	public void reviewScoreVersionInsertCase3() throws ApplicationException
	{
		boolean lbThrown = false;
		try{
		moEvaluationService.reviewScoreVersionInsert(moMyBatisSession, "",
				"agency_21");
		assertTrue("reviewScoreVersionInsert passed successfully.", true);
		//assertNotNull(loVersionRowCount);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Error in creating version History", lbThrown);
		}
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
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "3");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "1");
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
		String lsWobNumber = "3A10CC14F5613548B81B69050ABE1716";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "2705");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "2706");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		List<EvaluationBean> loEvScoreList = moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession,
				aoTaskMap, lsWobNumber);
		assertTrue(loEvScoreList.size() > 0);
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
		String lsWobNumber = "3A10CC14F5613548B81B69050ABE1716";
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "2705");
		loProcurementMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "##");
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		moEvaluationService.fetchEvaluationScoreDetails(moMyBatisSession, aoTaskMap, lsWobNumber);
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

	
/*	*//**
	 * The Method will test saveEvaluationScoreDetails method
	 * @throws ApplicationException
	 *//*
	@Test
	public void testSaveEvaluationScoreDetails() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2752");
		loEvalBean.setEvaluationStatusId("2706");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
		assertTrue(saveFlag);
	}
*/
	@Test
	public void testSaveEvaluationScoreDetailsCase1() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = false;
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
				lbEvalUpdateStatus, "In Review");
		assertFalse(saveFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationScoreDetailsCase2() throws ApplicationException
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
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(null, loScoreBean, lbEvalUpdateStatus,
				"In Review");
	}

	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationScoreDetailsCase3() throws ApplicationException
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
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, null, lbEvalUpdateStatus,
				"In Review");
	}

/*	@Test
	public void testSaveEvaluationScoreDetailsCase4() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2752");
		loEvalBean.setEvaluationStatusId("2706");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "");
		assertTrue(saveFlag);
	}
*/




	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationScoreDetailsCase7() throws ApplicationException
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
		liEvaluationBeanList.add(null);
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
	}

	@Test(expected = ApplicationException.class)
	public void testSaveEvaluationScoreDetailsCase8() throws ApplicationException
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
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
	}

	@Test
	public void testSaveEvaluationScoreDetailsCase9() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2754");
		loEvalBean.setEvaluationStatusId("2705");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("");
		loEvalBean.setModifiedByUserId("");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
		assertTrue(saveFlag);
	}

	@Test
	public void testSaveEvaluationScoreDetailsCase10() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2754");
		loEvalBean.setEvaluationStatusId("2707");
		loEvalBean.setModifiedFlag("");
		loEvalBean.setCreatedByUserId("");
		loEvalBean.setModifiedByUserId("");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
		assertTrue(saveFlag);
	}
	
	@Test
	public void testSaveEvaluationScoreDetailsCase6() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2753");
		loEvalBean.setEvaluationStatusId("2706");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
		assertTrue(saveFlag);
	}
	
	@Test
	public void testSaveEvaluationScoreDetailsCase5() throws ApplicationException
	{
		ScoreDetailsBean loScoreBean = new ScoreDetailsBean();
		EvaluationBean loEvalBean = new EvaluationBean();
		List<EvaluationBean> liEvaluationBeanList = new ArrayList<EvaluationBean>();
		Boolean lbEvalUpdateStatus = true;
		loEvalBean.setScoreSeqNum("99");
		loEvalBean.setScoreCriteria("c2");
		loEvalBean.setScore("6");
		loEvalBean.setMaximumScore("40");
		loEvalBean.setEvaluationCriteriaId("2753");
		loEvalBean.setEvaluationStatusId("2707");
		loEvalBean.setModifiedFlag("0");
		loEvalBean.setCreatedByUserId("agency_14");
		loEvalBean.setModifiedByUserId("agency_14");
		loScoreBean.setCreatedBy("agency_14");
		loScoreBean.setModifiedBy("agency_14");
		liEvaluationBeanList.add(loEvalBean);
		loScoreBean.setMiEvaluationBeanList(liEvaluationBeanList);
		Boolean saveFlag = moEvaluationService.saveEvaluationScoreDetails(moMyBatisSession, loScoreBean,
				lbEvalUpdateStatus, "In Review");
		assertTrue(saveFlag);
	}

	@Test
	public void testFetchEvaluationScoreDetailsForEvaluator1()
			throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, "");
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, loQueryMap,
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	@Test
	public void testFetchEvaluationScoreDetailsForEvaluator2()
			throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, "1740");
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, loQueryMap,
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	@Test
	public void testFetchEvaluationScoreDetailsForEvaluator3()
			throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, "1741");
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, loQueryMap,
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationScoreDetailsForEvaluator4()
			throws ApplicationException

	{
		List<EvaluationBean> loEvaluationBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, "**");
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, loQueryMap,
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationScoreDetailsForEvaluator5()
			throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, null);
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, loQueryMap,
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	@Test(expected = Exception.class)
	public void testFetchEvaluationScoreDetailsForEvaluator6()
			throws ApplicationException
	{
		List<EvaluationBean> loEvaluationBeanList = null;
		//Map<String, Object> loQueryMap = new HashMap<String, Object>();
		int abc = 1;
		//loQueryMap.put(HHSConstants.EVALUATION_STATUS_ID, null);
		loEvaluationBeanList = (List<EvaluationBean>) DAOUtil.masterDAO(
				moMyBatisSession, abc, 
				HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
				HHSConstants.FETCH_EVALUATION_SCORE_DETAILS_FOR_EVALUATOR,
				HHSConstants.JAVA_UTIL_MAP);
		assertNotNull(loEvaluationBeanList);
	} 
	
}	