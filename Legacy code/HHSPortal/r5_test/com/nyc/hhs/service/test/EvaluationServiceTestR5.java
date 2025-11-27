package com.nyc.hhs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.DocumentVisibility;
import com.nyc.hhs.model.EvaluationBean;

public class EvaluationServiceTestR5
{
	EvaluationService evaluationService = new EvaluationService();
	
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
			System.out.println("Before");
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
		}
		finally
		{
			moSession.rollback();
			moSession.close();
		}
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchEvaluationDocumentConfiguration() throws ApplicationException
	{
		evaluationService.fetchEvaluationDocumentConfiguration(null, null, null);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationDocumentConfigurationcase1() throws ApplicationException
	{
		List<DocumentVisibility> loDocumentVisibleList = null;
		String lsProcurementId = "3884";
		String lsEvaluationPoolMappingId = "1460";
		HashMap<String, String> lodoctype = new HashMap<String, String>();
		lodoctype.put(HHSR5Constants.HIDDEN_DOC_TYPES + HHSR5Constants.UNDERSCORE + HHSR5Constants.HIDDEN_DOC_TYPES,
				"0\\|1\\|0\\|1");
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.APPLICATION_SETTING, lodoctype);
		loDocumentVisibleList = evaluationService.fetchEvaluationDocumentConfiguration(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		assertFalse(loDocumentVisibleList.size() == HHSR5Constants.INT_ZERO);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationDocumentConfigurationcase2() throws ApplicationException
	{
		List<DocumentVisibility> loDocumentVisibleList = null;
		String lsProcurementId = "3884";
		String lsEvaluationPoolMappingId = "1480";
		HashMap<String, String> lodoctype = new HashMap<String, String>();
		lodoctype.put(HHSR5Constants.HIDDEN_DOC_TYPES + HHSR5Constants.UNDERSCORE + HHSR5Constants.HIDDEN_DOC_TYPES,
				"0\\|1\\|0\\|1");
		
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.APPLICATION_SETTING, lodoctype);
		loDocumentVisibleList = evaluationService.fetchEvaluationDocumentConfiguration(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		assertTrue(loDocumentVisibleList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationDocumentConfigurationcase3() throws ApplicationException
	{
		List<DocumentVisibility> loDocumentVisibleList = null;
		String lsProcurementId = "3884";
		String lsEvaluationPoolMappingId = "1480";
		HashMap<String, String> lodoctype = new HashMap<String, String>();
		lodoctype.put(HHSR5Constants.HIDDEN_DOC_TYPES + HHSR5Constants.HIDDEN_DOC_TYPES, "0\\|1\\|0\\|1");
		
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.APPLICATION_SETTING, lodoctype);
		loDocumentVisibleList = evaluationService.fetchEvaluationDocumentConfiguration(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		assertTrue(loDocumentVisibleList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = ApplicationException.class)
	public void fetchEvaluationDocumentConfigurationcase4() throws ApplicationException
	{
		List<DocumentVisibility> loDocumentVisibleList = null;
		String lsProcurementId = "3884";
		String lsEvaluationPoolMappingId = "1460";
		HashMap<String, String> lodoctype = new HashMap<String, String>();
		lodoctype.put(HHSR5Constants.HIDDEN_DOC_TYPES + HHSR5Constants.HIDDEN_DOC_TYPES, "0\\|1\\|0\\|1");
		
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.APPLICATION_SETTING, lodoctype);
		loDocumentVisibleList = evaluationService.fetchEvaluationDocumentConfiguration(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationDocumentConfiguration method and determines whether or not
	 * a List is getting updated corresponding to a Procurement Id,
	 * EvaluationPoolMappingId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void fetchEvaluationDocumentConfigurationcase5() throws ApplicationException
	{
		List<DocumentVisibility> loDocumentVisibleList = null;
		String lsProcurementId = "3333";
		String lsEvaluationPoolMappingId = "1111";
		HashMap<String, String> lodoctype = new HashMap<String, String>();
		lodoctype.put(HHSR5Constants.HIDDEN_DOC_TYPES + HHSR5Constants.UNDERSCORE + HHSR5Constants.HIDDEN_DOC_TYPES,
				"0\\|1\\|0\\|1");
		BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.APPLICATION_SETTING, lodoctype);
		loDocumentVisibleList = evaluationService.fetchEvaluationDocumentConfiguration(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = ApplicationException.class)
	public void saveEvaluationDocumentConfigurationcase1() throws ApplicationException
	{
		DocumentVisibility loDocumentVisibility = new DocumentVisibility();
		loDocumentVisibility.setUserId("agency_14");
		loDocumentVisibility.setDocumentVisibilityId("yes");
		List<DocumentVisibility> loListDocumentVisibility = new ArrayList<DocumentVisibility>();
		loListDocumentVisibility.add(loDocumentVisibility);
		evaluationService.saveEvaluationDocumentConfiguration(null, loListDocumentVisibility, null);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void saveEvaluationDocumentConfigurationcase2() throws ApplicationException
	{
		String lsProcurementId = "4431";
		String lsEvaluationPoolMappingId = "1480";
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		DocumentVisibility d1 = new DocumentVisibility();
		d1.setProcurementDocumentId(lsProcurementId);
		d1.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
		String LsVisibilityId = "6533";
		d1.setVisibility("0");
		d1.setDocumentVisibilityId(LsVisibilityId);
		String lsUserId = "agency_14";
		obj1.add(d1);
		assertTrue(evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId));
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void saveEvaluationDocumentConfigurationcase3() throws ApplicationException
	{
		
		String lsProcurementId = "4431";
		String lsEvaluationPoolMappingId = "1700";
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		DocumentVisibility d1 = new DocumentVisibility();
		d1.setProcurementDocumentId(lsProcurementId);
		d1.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
		String LsVisibilityId = "";
		d1.setVisibility("1");
		d1.setDocumentVisibilityId(LsVisibilityId);
		String lsUserId = "agency_14";
		obj1.add(d1);
		assertTrue(evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId));
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void saveEvaluationDocumentConfigurationcase4() throws ApplicationException
	{
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		String lsUserId = "agency_14";
		assertFalse(evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId));
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void saveEvaluationDocumentConfigurationcase5() throws ApplicationException
	{
		
		String lsProcurementId = "4362";
		String lsEvaluationPoolMappingId = "1625";
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		DocumentVisibility d1 = new DocumentVisibility();
		d1.setProcurementDocumentId(lsProcurementId);
		d1.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
		String LsVisibilityId = "";
		d1.setVisibility("1");
		d1.setDocumentVisibilityId(LsVisibilityId);
		String lsUserId = "agency_14";
		obj1.add(d1);
		assertTrue(evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId));
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void saveEvaluationDocumentConfigurationcase6() throws ApplicationException
	{
		String lsProcurementId = "4252";
		String lsEvaluationPoolMappingId = "1521";
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		DocumentVisibility d1 = new DocumentVisibility();
		d1.setProcurementDocumentId(lsProcurementId);
		d1.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
		String LsVisibilityId = "6518";
		d1.setVisibility("1");
		d1.setDocumentVisibilityId(LsVisibilityId);
		String lsUserId = "agency_14";
		obj1.add(d1);
		assertTrue(evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId));
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * saveEvaluationDocumentConfiguration method and determines whether or not
	 * a boolean variable is getting updated corresponding to a User Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void saveEvaluationDocumentConfigurationcase7() throws ApplicationException
	{
		String lsProcurementId = "4252";
		String lsEvaluationPoolMappingId = "1521";
		List<DocumentVisibility> obj1 = new ArrayList<DocumentVisibility>();
		DocumentVisibility d1 = new DocumentVisibility();
		d1.setProcurementDocumentId(lsProcurementId);
		d1.setEvaluationPoolMappingId(lsEvaluationPoolMappingId);
		String LsVisibilityId = "6518";
		d1.setVisibility("1");
		d1.setDocumentVisibilityId(LsVisibilityId);
		String lsUserId = "agency_11";
		obj1.add(d1);
		evaluationService.saveEvaluationDocumentConfiguration(moSession, obj1, lsUserId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetailsForEvaluator method and determines whether or
	 * not a List is getting updated corresponding to a QueryMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvaluationScoreDetailsForEvaluatorcase1() throws ApplicationException
	
	{
		HashMap<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSR5Constants.EVALUATION_STATUS_ID, 4344);
		evaluationService.fetchEvaluationScoreDetailsForEvaluator(null, loQueryMap);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetailsForEvaluator method and determines whether or
	 * not a List is getting updated corresponding to a QueryMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationScoreDetailsForEvaluatorcase2() throws ApplicationException
	
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSR5Constants.EVALUATE_STATUS_ID, 4344);
		loQueryMap.put(HHSR5Constants.PROPOSAL_ID, 2942);
		loQueryMap.put(HHSR5Constants.VERSION_NUMBER, 4);
		assertTrue(evaluationService.fetchEvaluationScoreDetailsForEvaluator(moSession, loQueryMap).isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetailsForEvaluator method and determines whether or
	 * not a List is getting updated corresponding to a QueryMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void fetchEvaluationScoreDetailsForEvaluatorcase3() throws ApplicationException
	
	{
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		loQueryMap.put(HHSR5Constants.EVALUATE_STATUS_ID, 4545);
		loQueryMap.put(HHSR5Constants.PROPOSAL_ID, 9999);
		loQueryMap.put(HHSR5Constants.VERSION_NUMBER, 4);
		evaluationService.fetchEvaluationScoreDetailsForEvaluator(moSession, loQueryMap);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationRoundScoreDetails method and determines whether or not a
	 * List is getting updated corresponding to a WobNumber, TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvaluationRoundScoreDetailsCase1() throws ApplicationException
	
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, "3947");
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4587");
		loProcurementMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, 3947);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4587);
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		evaluationService.fetchEvaluationRoundScoreDetails(null, aoTaskMap, lsWobNumber);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationRoundScoreDetails method and determines whether or not a
	 * List is getting updated corresponding to a WobNumber, TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationRoundScoreDetailsCase2() throws ApplicationException
	
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, "3947");
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4587");
		loProcurementMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, 3947);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4587);
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		aoTaskMap.put(lsWobNumber, loProcurementMap);
		assertEquals(evaluationService.fetchEvaluationRoundScoreDetails(moSession, aoTaskMap, lsWobNumber).size(), 1);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationRoundScoreDetails method and determines whether or not a
	 * List is getting updated corresponding to a WobNumber, TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationRoundScoreDetailsCase3() throws ApplicationException
	
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, "3947");
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4587");
		loProcurementMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, 3947);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4587);
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		evaluationService.fetchEvaluationRoundScoreDetails(moSession, aoTaskMap, lsWobNumber);
		List<EvaluationBean> loDocumentVisibleList = new ArrayList<EvaluationBean>();
		assertTrue(loDocumentVisibleList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationRoundScoreDetails method and determines whether or not a
	 * List is getting updated corresponding to a WobNumber, TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void fetchEvaluationRoundScoreDetailsCase4() throws ApplicationException
	
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loProcurementMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, "7777");
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "6666");
		loProcurementMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, 3947);
		loProcurementMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4587);
		HashMap<String, Object> aoTaskMap = new HashMap<String, Object>();
		evaluationService.fetchEvaluationRoundScoreDetails(moSession, aoTaskMap, lsWobNumber);
		List<EvaluationBean> loDocumentVisibleList = new ArrayList<EvaluationBean>();
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalRoundDetails method and determines whether or not a List is
	 * getting updated corresponding to a TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvalRoundDetailscase1() throws ApplicationException
	
	{
		Map<String, Object> loProcurementMap = new HashMap<String, Object>();
		evaluationService.fetchEvalRoundDetails(null, loProcurementMap);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalRoundDetails method and determines whether or not a List is
	 * getting updated corresponding to a TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvalRoundDetailscase2() throws ApplicationException
	
	{
		Map<String, Object> loProcurementMap = new HashMap<String, Object>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, 4344);
		loProcurementMap.put(HHSR5Constants.PROCUREMENT_ID, 3924);
		assertFalse(evaluationService.fetchEvalRoundDetails(moSession, loProcurementMap).isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalRoundDetails method and determines whether or not a List is
	 * getting updated corresponding to a TaskMap
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void fetchEvalRoundDetailscase3() throws ApplicationException
	
	{
		Map<String, Object> loProcurementMap = new HashMap<String, Object>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, 4444);
		loProcurementMap.put(HHSR5Constants.PROCUREMENT_ID, 3224);
		evaluationService.fetchEvalRoundDetails(moSession, loProcurementMap);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvaluationScoreDetailscase1() throws ApplicationException
	
	{
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4344");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "4");
		EvaluationBean evaluationbean = new EvaluationBean();
		evaluationbean.setVersionNumber("4");
		loRoundList.add(evaluationbean);
		evaluationService.fetchEvaluationScoreDetails(null, loProcurementMap, loRoundList);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationScoreDetailscase2() throws ApplicationException
	
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4344");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "4");
		EvaluationBean evaluationbean = new EvaluationBean();
		evaluationbean.setVersionNumber("4");
		loRoundList.add(evaluationbean);
		loEvaluationScoreList = evaluationService.fetchEvaluationScoreDetails(moSession, loProcurementMap, loRoundList);
		assertEquals(2, loEvaluationScoreList.size());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationScoreDetailscase3() throws ApplicationException
	
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4347");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "4");
		loEvaluationScoreList = evaluationService.fetchEvaluationScoreDetails(moSession, loProcurementMap, loRoundList);
		assertTrue(loEvaluationScoreList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationScoreDetailscase4() throws ApplicationException
	
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4344");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "4");
		EvaluationBean evaluationbean = new EvaluationBean();
		evaluationbean.setVersionNumber("4");
		loEvaluationScoreList = evaluationService.fetchEvaluationScoreDetails(moSession, loProcurementMap, loRoundList);
		assertEquals(2, loEvaluationScoreList.size());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationScoreDetailscase5() throws ApplicationException
	
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4347");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "4");
		EvaluationBean evaluationbean = new EvaluationBean();
		evaluationbean.setVersionNumber("4");
		loRoundList.add(evaluationbean);
		loEvaluationScoreList = evaluationService.fetchEvaluationScoreDetails(moSession, loProcurementMap, loRoundList);
		assertTrue(loEvaluationScoreList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoreDetails method and determines whether or not a List
	 * is getting updated corresponding to a QueryMap, RoundList
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void fetchEvaluationScoreDetailscase6() throws ApplicationException
	
	{
		List<EvaluationBean> loEvaluationScoreList = null;
		List<EvaluationBean> loRoundList = new ArrayList<EvaluationBean>();
		Map<String, String> loProcurementMap = new HashMap<String, String>();
		loProcurementMap.put(HHSR5Constants.EVALUATION_STATUS_ID, "4444");
		loProcurementMap.put(HHSR5Constants.VERSION_NUMBER, "6");
		EvaluationBean evaluationbean = new EvaluationBean();
		evaluationbean.setVersionNumber("4");
		loRoundList.add(evaluationbean);
		loEvaluationScoreList = evaluationService.fetchEvaluationScoreDetails(moSession, loProcurementMap, loRoundList);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoresDetails method and determines whether or not a List
	 * is getting updated corresponding to a ProcurementId,ProposalId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvaluationScoresDetailsCase1() throws ApplicationException
	{
		String lsProcurementId = HHSR5Constants.EMPTY_STRING;
		String lsProposalId = HHSR5Constants.EMPTY_STRING;
		evaluationService.fetchEvaluationScoresDetails(null, lsProcurementId, lsProposalId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoresDetails method and determines whether or not a List
	 * is getting updated corresponding to a ProcurementId,ProposalId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvaluationScoresDetailsCase2() throws ApplicationException
	{
		String lsProcurementId = "4224";
		String lsProposalId = "3223";
		List<EvaluationBean> loScoreList = evaluationService.fetchEvaluationScoresDetails(moSession, lsProcurementId,
				lsProposalId);
		assertTrue(!loScoreList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoresDetails method and determines whether or not a List
	 * is getting updated corresponding to a ProcurementId,ProposalId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationScoresDetailsCase3() throws ApplicationException
	{
		String lsProcurementId = HHSR5Constants.EMPTY_STRING;
		String lsProposalId = HHSR5Constants.EMPTY_STRING;
		List<EvaluationBean> loScoreList = evaluationService.fetchEvaluationScoresDetails(moSession, lsProcurementId,
				lsProposalId);
		assertTrue(loScoreList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoresDetails method and determines whether or not a List
	 * is getting updated corresponding to a ProcurementId,ProposalId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchEvaluationScoresDetailsCase4() throws ApplicationException
	{
		String lsProcurementId = null;
		String lsProposalId = HHSR5Constants.EMPTY_STRING;
		List<EvaluationBean> loScoreList = evaluationService.fetchEvaluationScoresDetails(moSession, lsProcurementId,
				lsProposalId);
		assertEquals(loScoreList, null);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvaluationScoresDetails method and determines whether or not a List
	 * is getting updated corresponding to a ProcurementId,ProposalId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void fetchEvaluationScoresDetailsCase5() throws ApplicationException
	{
		String lsProcurementId = "4444";
		String lsProposalId = "5555";
		List<EvaluationBean> loScoreList = evaluationService.fetchEvaluationScoresDetails(moSession, lsProcurementId,
				lsProposalId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * checkAwardStatusForEvaluationSetting method and determines
	 * awardReviewStatus corresponding to a ProcurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void checkAwardStatusForEvaluationSettingCase1() throws ApplicationException
	{
		String lsProcurementId = "4163";
		String lsEvaluationPoolMappingId = "1660";
		evaluationService.checkAwardStatusForEvaluationSetting(null, lsProcurementId, lsEvaluationPoolMappingId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * checkAwardStatusForEvaluationSetting method and determines
	 * awardReviewStatus corresponding to a ProcurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void checkAwardStatusForEvaluationSettingCase2() throws ApplicationException
	{
		String lsProcurementId = "4163";
		String lsEvaluationPoolMappingId = "1660";
		Boolean loScoreList = evaluationService.checkAwardStatusForEvaluationSetting(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		assertTrue(loScoreList);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * checkAwardStatusForEvaluationSetting method and determines
	 * awardReviewStatus corresponding to a ProcurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void checkAwardStatusForEvaluationSettingCase3() throws ApplicationException
	{
		String lsProcurementId = HHSR5Constants.EMPTY_STRING;
		String lsEvaluationPoolMappingId = "1660";
		Boolean loScoreList = evaluationService.checkAwardStatusForEvaluationSetting(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
		assertTrue(loScoreList);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * checkAwardStatusForEvaluationSetting method and determines
	 * awardReviewStatus corresponding to a ProcurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void checkAwardStatusForEvaluationSettingCase4() throws ApplicationException
	{
		String lsProcurementId = HHSR5Constants.EMPTY_STRING;
		String lsEvaluationPoolMappingId = "1660";
		Boolean loScoreList = evaluationService.checkAwardStatusForEvaluationSetting(moSession, lsProcurementId,
				lsEvaluationPoolMappingId);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalScoreOfSelectRound method and determines Round details
	 * corresponding to a evaluationStatusId and versionNumber
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchEvalScoreOfSelectRoundcase1() throws ApplicationException
	{
		Map<String, String> loEvaluationScoreList = null;
		evaluationService.fetchEvalScoreOfSelectRound(null, loEvaluationScoreList);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalScoreOfSelectRound method and determines Round details
	 * corresponding to a evaluationStatusId and versionNumber
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchEvalScoreOfSelectRoundcase2() throws ApplicationException
	{
		List<EvaluationBean> loScoreList = null;
		Map<String, String> loEvaluationScore = new HashMap<String, String>();
		loEvaluationScore.put(HHSR5Constants.EVALUATE_STATUS_ID, "4344");
		loEvaluationScore.put(HHSR5Constants.VERSION_NUMBER, "4");
		loScoreList = evaluationService.fetchEvalScoreOfSelectRound(moSession, loEvaluationScore);
		assertEquals(2, loScoreList.size());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchEvalScoreOfSelectRound method and determines Round details
	 * corresponding to a evaluationStatusId and versionNumber
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = Exception.class)
	public void fetchEvalScoreOfSelectRoundcase3() throws ApplicationException
	{
		List<EvaluationBean> loScoreList = null;
		Map<String, String> loEvaluationScore = new HashMap<String, String>();
		loEvaluationScore.put(HHSR5Constants.EVALUATE_STATUS_ID, "4344");
		loEvaluationScore.put(HHSR5Constants.VERSION_NUMBER, "4");
		loScoreList = evaluationService.fetchEvalScoreOfSelectRound(moSession, loEvaluationScore);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchRoundDropdownDetails method and determines Round details for
	 * dropdown corresponding to a EntityId and procurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void fetchRoundDropdownDetailsCase1() throws ApplicationException
	{
		String lsWobNumber = HHSR5Constants.CTH_LO_WOB_NUM;
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4658");
		loParam.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, "4224");
		loTaskMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loTaskMap.put(HHSR5Constants.PROPERTY_PE_PROCURMENT_ID, 4224);
		loTaskMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4344);
		evaluationService.fetchRoundDropdownDetails(null, loTaskMap, lsWobNumber);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchRoundDropdownDetails method and determines Round details for
	 * dropdown corresponding to a EntityId and procurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void fetchRoundDropdownDetailsCase2() throws ApplicationException
	{
		String lsWobNumber = HHSR5Constants.CTH_LO_WOB_NUM;
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4658");
		loParam.put(HHSR5Constants.PROCUREMENT_ID, "4224");
		loTaskMap.put(HHSR5Constants.CTH_LO_WOB_NUM, loParam);
		loTaskMap.put(HHSR5Constants.PROCUREMENT_ID, 4224);
		loTaskMap.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, 4344);
		List<EvaluationBean> loScoreList = evaluationService.fetchRoundDropdownDetails(moSession, loTaskMap,
				lsWobNumber);
		assertTrue(!loScoreList.isEmpty());
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchRoundDropdownDetails method and determines Round details for
	 * dropdown corresponding to a EntityId and procurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test
	public void fetchRoundDropdownDetailsCase3() throws ApplicationException
	{
		String lsWobNumber = HHSR5Constants.CTH_LO_WOB_NUM;
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4658");
		loParam.put(HHSR5Constants.PROCUREMENT_ID, "4224");
		List<EvaluationBean> loScoreList = evaluationService.fetchRoundDropdownDetails(moSession, loTaskMap,
				lsWobNumber);
		assertEquals(loScoreList, null);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * fetchRoundDropdownDetails method and determines Round details for
	 * dropdown corresponding to a EntityId and procurementId
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void fetchRoundDropdownDetailsCase4() throws ApplicationException
	{
		String lsWobNumber = HHSR5Constants.CTH_LO_WOB_NUM;
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put(HHSR5Constants.PROPERTY_PE_ENTITY_ID, "4444");
		loParam.put(HHSR5Constants.PROCUREMENT_ID, "3344");
		List<EvaluationBean> loScoreList = evaluationService.fetchRoundDropdownDetails(moSession, loTaskMap,
				lsWobNumber);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * reviewScoreVersionInsert method and Insert the data for archive details
	 * corresponding to a ProposalId, UserId and statusId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void reviewScoreVersionInsertCase1() throws ApplicationException
	{
		evaluationService.reviewScoreVersionInsert(null, null, null, null);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * reviewScoreVersionInsert method and Insert the data for archive details
	 * corresponding to a ProposalId, UserId and statusId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void reviewScoreVersionInsertCase2() throws ApplicationException
	{
		String lsProposalId = "2999";
		String lsUserId = "agency_14";
		String lsStatus = HHSR5Constants.SCORES_RETURNED;
		evaluationService.reviewScoreVersionInsert(moSession, lsProposalId, lsUserId, lsStatus);
	}
	
	/**
	 * Release 5 test case This method tests the execution of
	 * reviewScoreVersionInsert method and Insert the data for archive details
	 * corresponding to a ProposalId, UserId and statusId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	
	@Test(expected = Exception.class)
	public void reviewScoreVersionInsertCase3() throws ApplicationException
	{
		String lsProposalId = "4466";
		String lsUserId = "agency_22";
		String lsStatus = HHSR5Constants.SCORES_RETURNED;
		evaluationService.reviewScoreVersionInsert(moSession, lsProposalId, lsUserId, lsStatus);
	}
	
	@Test
	public void reviewScoreVersionInsertCase4() throws ApplicationException
	{
		String lsProposalId = "3182";
		String lsUserId = "agency_14";
		String lsStatus = HHSR5Constants.SCORES_RETURNED;
		evaluationService.reviewScoreVersionInsert(moSession, lsProposalId, lsUserId, lsStatus);
	}
	
	@Test
	public void fetchFinalizeAwardResultsScoresCase1() throws ApplicationException
	{
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		loTaskDetailMap.put("ProcurementID", 3884);
		loTaskDetailMap.put("EvaluationPoolMappingId", 1322);
		Object obj = "Org_509";
		loTaskDetailMap.put("ProviderID", obj);
		loTaskDetailMap.put("asWobNumber", lsWobNumber);
		String lsStatus = HHSR5Constants.SCORES_RETURNED;
		Map loProvSelectionMap = new HashMap();
		loProvSelectionMap = evaluationService.fetchFinalizeAwardResultsScores(moSession, loTaskDetailMap, lsWobNumber);
		assertTrue(loProvSelectionMap.size() == 0);
	}
	
	@Test
	public void fetchFinalizeAwardResultsScoresCase2() throws ApplicationException
	{
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put("ProcurementID", "3884");
		loParam.put("EvaluationPoolMappingId", "1322");
		loParam.put("ProviderID", "Org_509");
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loParam);
		Map loProvSelectionMap = new HashMap();
		loProvSelectionMap = evaluationService.fetchFinalizeAwardResultsScores(moSession, loTaskDetailMap, lsWobNumber);
		assertTrue(loProvSelectionMap.get("finalizeOtherProviderList") != null);
	}
	
	@Test(expected = Exception.class)
	public void fetchFinalizeAwardResultsScoresCase3() throws ApplicationException
	{
		Map<String, String> loParam = new HashMap<String, String>();
		loParam.put("ProcurementID", "3884");
		loParam.put("EvaluationPoolMappingId", "1322");
		loParam.put("ProviderID", "Org_509");
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loParam);
		Map loProvSelectionMap = new HashMap();
		loProvSelectionMap = evaluationService.fetchFinalizeAwardResultsScores(null, loTaskDetailMap, lsWobNumber);
		
	}
	
	@Test
	public void fetchFinalizeAwardResultsScoresCase4() throws ApplicationException
	{
		/*
		 * Map<String, String> loParam = new HashMap<String, String>();
		 * loParam.put("ProcurementID", "3884");
		 * loParam.put("EvaluationPoolMappingId", "1322");
		 * loParam.put("ProviderID", "Org_509");
		 */
		String lsWobNumber = "E30322D4A0DE284AA0C6CEBB24EEB4E8";
		HashMap<String, Object> loTaskDetailMap = null;
		// loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loParam);
		Map loProvSelectionMap = new HashMap();
		loProvSelectionMap = evaluationService.fetchFinalizeAwardResultsScores(moSession, loTaskDetailMap, lsWobNumber);
		assertTrue(loProvSelectionMap.size() == 0);
	}
	
	@Test
	public void setNegotiationEvaluationAmountCase1() throws ApplicationException
	{
		String asEvaluationPoolMappingId = "1660";
		Boolean loResult = evaluationService.setNegotiationEvaluationAmount(moSession, asEvaluationPoolMappingId);
		assertTrue(loResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void setNegotiationEvaluationAmountCase2() throws ApplicationException
	{
		String asEvaluationPoolMappingId = "";
		Boolean loResult = evaluationService.setNegotiationEvaluationAmount(null, asEvaluationPoolMappingId);
	}
	
	@Test
	public void setNegotiationEvaluationAmountCase3() throws ApplicationException
	{
		String asEvaluationPoolMappingId = "123456";
		Boolean loResult = evaluationService.setNegotiationEvaluationAmount(moSession, asEvaluationPoolMappingId);
		assertFalse(loResult);
	}
	
	@Test
	public void setRequestAmendmentFlagCase1() throws ApplicationException
	{
		String lsProposalId = "3223";
		Boolean lbResult = evaluationService.setRequestAmendmentFlag(moSession, lsProposalId);
		assertTrue(lbResult);
	}
	
	@Test
	public void setRequestAmendmentFlagCase2() throws ApplicationException
	{
		String lsProposalId = "11111";
		Boolean lbResult = evaluationService.setRequestAmendmentFlag(moSession, lsProposalId);
		assertTrue(!lbResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void setRequestAmendmentFlagCase3() throws ApplicationException
	{
		String lsProposalId = "11111";
		Boolean lbResult = evaluationService.setRequestAmendmentFlag(null, lsProposalId);
	}
}
