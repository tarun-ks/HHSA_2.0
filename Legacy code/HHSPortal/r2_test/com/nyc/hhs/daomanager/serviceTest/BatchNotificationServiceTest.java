package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.BatchNotificationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.SMAlertNotificationBean;

public class BatchNotificationServiceTest
{
	BatchNotificationService moBatchNotificationService = new BatchNotificationService();
	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	
	@Test
	public void testFetchProposalDueDateAlertDetails() throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		HashMap<String, HashMap<String, String>> aoHashMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		aoHashMap .put("loHMap",loHMap);
		loHMap.put("NoOfDays", "2");
		loResultList = moBatchNotificationService.fetchProposalDueDateAlertDetails(moMyBatisSession, aoHashMap);
		assertNotNull(loResultList);
	}
	@Test
	public void testFetchApprovedProvidersList() throws ApplicationException
	{
		String lsProcurementId = "624";
		List<String> loResultList = null;
		HashMap<String, HashMap<String, String>> aoParamMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		aoParamMap .put("loHMap",loHMap);
		loHMap.put("procurementId",lsProcurementId);
		loResultList = moBatchNotificationService.fetchApprovedProvidersList(moMyBatisSession, aoParamMap);
		assertNotNull(loResultList);
		
		
		
	}
	@Test
	public void testFetchRfpReleaseDueDateAlertDetails() throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		HashMap<String, HashMap<String, String>> aoHashMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		aoHashMap .put("loHMap",loHMap);
		loHMap.put("NoOfDays", "2");
		loResultList = moBatchNotificationService.fetchRfpReleaseDueDateAlertDetails(moMyBatisSession, aoHashMap);
		assertNotNull(loResultList);
	}
	@Test
	public void testFetchFirstRoundEvaluationDueDateAlertDetails() throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		HashMap<String, HashMap<String, String>> aoHashMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		aoHashMap .put("loHMap",loHMap);
		loHMap.put("NoOfDays", "2");
		loResultList = moBatchNotificationService.fetchFirstRoundEvaluationDueDateAlertDetails(moMyBatisSession, aoHashMap);
		assertNotNull(loResultList);
	}
	
	
	@Test
	public void testFetchFinalEvaluationDueDateAlertDetails()throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		HashMap<String, HashMap<String, String>> aoHashMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		aoHashMap .put("loHMap",loHMap);
		loHMap.put("NoOfDays", "2");
		loResultList = moBatchNotificationService.fetchFinalEvaluationDueDateAlertDetails(moMyBatisSession, aoHashMap);
		assertNotNull(loResultList);
	}
	/**
	 * The method will retrieve the External & Internal Evaluators
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	@Test
	public void testFetchExtAndIntEvaluator() throws ApplicationException
	{
		String lsProcurementId = "623";
		new HashMap<Object, Object>();
		HashMap<String, String> aoParameterMap = new HashMap<String, String>();
		aoParameterMap .put("procurementId",lsProcurementId);
		HashMap<String, List<String>> loEvaluatorEmailList = moBatchNotificationService.fetchExtAndIntEvaluator(moMyBatisSession, aoParameterMap);
		assertNotNull(loEvaluatorEmailList);
		assertTrue(loEvaluatorEmailList.size() > 0);
		moMyBatisSession.close();
	}

	/**
	 * The method will retrieve the External & Internal Evaluators
	 * @throws ApplicationException - If an ApplicationException occurs
	 */

	
	@Test(expected = ApplicationException.class)
	public void testFetchExtAndIntEvaluator2() throws ApplicationException
	{
		String lsProcurementId = "623";
		new HashMap<Object, Object>();
		HashMap<String, String> aoParameterMap = new HashMap<String, String>();
		aoParameterMap .put("procurementId",lsProcurementId);
		moBatchNotificationService.fetchExtAndIntEvaluator(null, aoParameterMap);
	}
	@Test(expected = ApplicationException.class)
	public void testFetchExtAndIntEvaluator3() throws ApplicationException
	{
		String lsProcurementId = "623";
		new HashMap<Object, Object>();
		HashMap<String, String> aoParameterMap = new HashMap<String, String>();
		aoParameterMap .put("procurementId",lsProcurementId);
		moBatchNotificationService.fetchExtAndIntEvaluator(moMyBatisSession, null);
	}
	@Test(expected = Exception.class)
	public void testFetchExtAndIntEvaluator4() throws ApplicationException
	{
		String lsProcurementId = "623";
		new HashMap<Object, Object>();
		HashMap<String, String> aoParameterMap = new HashMap<String, String>();
		aoParameterMap .put("procurementId",lsProcurementId);
		HashMap<String, List<String>> loEvaluatorEmailList = moBatchNotificationService.fetchExtAndIntEvaluator(moMyBatisSession, aoParameterMap);
		assertNotNull(loEvaluatorEmailList);
		assertTrue(loEvaluatorEmailList.size() > 0);
		moMyBatisSession.close();
	}

}
	
	
	



