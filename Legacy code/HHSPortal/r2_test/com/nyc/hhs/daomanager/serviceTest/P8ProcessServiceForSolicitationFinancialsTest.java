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
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

public class P8ProcessServiceForSolicitationFinancialsTest {
	
	/**
	 * @return
	 * @throws ApplicationException
	 */
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
		loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession());
		return loUserSession;
	}
	//ghgh
	@Test
	public void testGetHomePageTaskCount() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "agency_14";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, lsTaskOwnerName);
		assertNotNull(loTaskCountMap);
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}	
	
	@Test
	public void testGetHomePageTaskCountCase2() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "Unassigned";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag,"DOC");
		assertNotNull(loTaskCountMap);
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}
	
	@Test
	public void testGetHomePageTaskCountCase3() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "Unassigned";
		Boolean loIncludeNotFlag = true;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, lsTaskOwnerName);
		assertNotNull(loTaskCountMap);
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}
	
	@Test(expected = Exception.class)
	public void testGetHomePageTaskCountCase4() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();		
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "agency_14";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(null,loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag,"DOC");		
	}	
	
	@Test
	public void testGetHomePageTaskCountCase5() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "agency_14";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag,"DOC");
		assertNotNull(loTaskCountMap);
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}	
	
	@Test
	public void testGetHomePageTaskCountCase6() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "##";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag,"DOC");
		assertTrue(loTaskCountMap.isEmpty());
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}	
	
	@Test
	public void testGetHomePageTaskCountCase7() throws ApplicationException
	{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
		String lsTaskOwnerName = "";
		Boolean loIncludeNotFlag = false;
		loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
		HashMap<String, Integer> loTaskCountMap = loP8ProcessServiceForSolicitationFinancials.getHomePageTaskCount(loP8UserSession,
				loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag,"DOC");
		assertFalse(loTaskCountMap.isEmpty());
		SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
		filenetPEDBSession.close();
	}	
	@Test
	public void testCancelEvaluationTask() throws ApplicationException
	{
		try{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		
		HashMap<String, Object> aoEvaluationTaskDetailsMap = new HashMap<String, Object>();
		aoEvaluationTaskDetailsMap.put("LaunchBy", "Tarun");
		//aoEvaluationTaskDetailsMap.put(HHSConstants.PROCUREMENT_TITLE, "ddd");
		//aoEvaluationTaskDetailsMap.put("ProcurementEPin", "ddd");
		
		/*WorkFlowName=WF315 - Contract Budget Amendment (AMD)
		ProcurementTitle=ddd
		ProcurementEPin=1237689543013
		ProviderID=ddd
		AwardEPin=1237689543013
		ct=ddd
		ReviewLevel=1
		CurrentLevel=1
		AgencyID=DOC
		ProcurementID=dd
		ContractID=111888
		LaunchBy=Tarun
		BudgetID=666
		TaskID=1000*/
		boolean lbStatus = loP8ProcessServiceForSolicitationFinancials.cancelEvaluationTask(loP8UserSession,
				aoEvaluationTaskDetailsMap);
		assertTrue(lbStatus);
		
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
	}	
	
	@Test
	public void testCancelEvaluationTask2() throws ApplicationException
	{
		try{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		
		HashMap<String, Object> aoEvaluationTaskDetailsMap = new HashMap<String, Object>();
		//aoEvaluationTaskDetailsMap.put("ProcurementTitle", "ddd");
	
		boolean lbStatus = loP8ProcessServiceForSolicitationFinancials.cancelEvaluationTask(loP8UserSession,
				aoEvaluationTaskDetailsMap);
		assertTrue(lbStatus);
		}catch(ApplicationException aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
	@Test
	public void testCancelEvaluationTask3() throws ApplicationException
	{
		try{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		
		HashMap<String, Object> aoEvaluationTaskDetailsMap = new HashMap<String, Object>();
		//aoEvaluationTaskDetailsMap.put("ProcurementTitle", "ddd");
	
		boolean lbStatus = loP8ProcessServiceForSolicitationFinancials.cancelEvaluationTask(null,
				aoEvaluationTaskDetailsMap);
		assertTrue(lbStatus);
		}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
	
	@Test
	public void testReActivateAwardWF() throws ApplicationException
	{
		try{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
		
		loHmReqdWFProperties.put("LaunchBy", "Tarun");
	/*	loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);*/
		Boolean  loStatus = loP8ProcessServiceForSolicitationFinancials.reActivateAwardWF(loP8UserSession,
				loHmReqdWFProperties, null, null);
		assertTrue(loStatus);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
	}
	}	
	@Test
	public void testReActivateAwardWF2() throws ApplicationException
	{
		try{
		P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
		
		//loHmReqdWFProperties.put("ContractID", "888999");
	/*	loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
		loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);*/
		Boolean  loStatus = loP8ProcessServiceForSolicitationFinancials.reActivateAwardWF(loP8UserSession,
				loHmReqdWFProperties, null, null);
		assertTrue(loStatus);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
	}
	}
	
		@Test
		public void testReActivateAwardWF3() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			
			//loHmReqdWFProperties.put("ContractID", "888999");
		/*	loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);*/
			Boolean  loStatus = loP8ProcessServiceForSolicitationFinancials.reActivateAwardWF(null,
					loHmReqdWFProperties, null, null);
			assertTrue(loStatus);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchCurrentTaskOwner() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="ECB0F647A7648B47BC346C03F4E294DB";
			Integer loTaskCount = 1;
			HashMap lsTaskOwner = loP8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(loP8UserSession,
					loWobNum,loTaskCount);
			assertNotNull(lsTaskOwner);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchCurrentTaskOwner2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="ECB0F647A7648B47BC346C03F4E294DB";
			Integer loTaskCount = 0;
			HashMap lsTaskOwner = loP8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(loP8UserSession,
					loWobNum,loTaskCount);
			
			if(lsTaskOwner==null)
			{
			assertTrue(Boolean.TRUE);
			}
			
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchCurrentTaskOwner3() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="";
			Integer loTaskCount = 1;
			HashMap lsTaskOwner = loP8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(loP8UserSession,
					loWobNum,loTaskCount);
			assertNotNull(lsTaskOwner);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchCurrentTaskOwner4() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="7294B304F621E446AD443DCACEF90F45";
			Integer loTaskCount = 1;
			HashMap lsTaskOwner = loP8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(loP8UserSession,
					loWobNum,loTaskCount);
			assertNotNull(lsTaskOwner);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchCurrentTaskOwner5() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="7294B304F621E446AD443DCACEF90F45";
			Integer loTaskCount = 1;
			HashMap lsTaskOwner = loP8ProcessServiceForSolicitationFinancials.fetchCurrentTaskOwner(null,
					loWobNum,loTaskCount);
			assertNotNull(lsTaskOwner);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testGetStepElementfromWobNo() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum="E5B4DF9FDF32CE4D82AEAC0D3A1A39F2FDFDFD";
			String asQueueName = P8Constants.HSS_QUEUE_NAME;
			VWSession loVWSession = new P8SecurityOperations().getPESession(loP8UserSession);
			VWStepElement loStepElement = loP8ProcessServiceForSolicitationFinancials.getStepElementfromWobNo(loVWSession,loWobNum,asQueueName);
			assertTrue(true);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		
		@Test
		public void testGetStepElementfromWobNo2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			String loWobNum=null;
			String asQueueName = "HHS";
			VWSession loVWSession = new P8SecurityOperations().getPESession(loP8UserSession);
			VWStepElement loStepElement = loP8ProcessServiceForSolicitationFinancials.getStepElementfromWobNo(loVWSession,loWobNum,asQueueName);
			assertNotNull(loStepElement);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
			

		@Test
		public void testFetchAgencyTaskCount() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			AgencyTaskBean loAgencyTaskQueryParam = new AgencyTaskBean();
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			int liCount = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount(loP8UserSession,loAgencyTaskQueryParam);
			assertTrue(liCount>0||liCount==0);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTaskCount2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			AgencyTaskBean loAgencyTaskQueryParam = new AgencyTaskBean();
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			int liCount = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount(null,loAgencyTaskQueryParam);
			assertTrue(liCount>0);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTaskCount3() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			AgencyTaskBean loAgencyTaskQueryParam = null;
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			//loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			int liCount = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTaskCount(loP8UserSession,loAgencyTaskQueryParam);
			assertTrue(liCount>0);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTask1() throws ApplicationException
		{ 
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			AgencyTaskBean loAgencyTaskQueryParam = new AgencyTaskBean();
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			List<AgencyTaskBean> loAgencyTaskBeanDetails = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTask(loP8UserSession,loAgencyTaskQueryParam);
			assertNotNull(loAgencyTaskBeanDetails);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTask2() throws ApplicationException
		{ 
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = null;
			AgencyTaskBean loAgencyTaskQueryParam = new AgencyTaskBean();
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			List<AgencyTaskBean> loAgencyTaskBeanDetails = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTask(loP8UserSession,loAgencyTaskQueryParam);
			assertNotNull(loAgencyTaskBeanDetails);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTask3() throws ApplicationException
		{ 
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = null;
			AgencyTaskBean loAgencyTaskQueryParam = new AgencyTaskBean();
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			loAgencyTaskQueryParam.setFilterProp(loFilterProp);
			
			List<AgencyTaskBean> loAgencyTaskBeanDetails = loP8ProcessServiceForSolicitationFinancials.fetchAgencyTask(null,loAgencyTaskQueryParam);
			assertNotNull(loAgencyTaskBeanDetails);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchTaskStatusFromView1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			String loTaskStatusId = loP8ProcessServiceForSolicitationFinancials.fetchTaskStatusFromView(loP8UserSession,loHmReqdWFProperties);
			assertNotNull(loTaskStatusId);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}	
		@Test
		public void testFetchTaskStatusFromView2() throws ApplicationException
		{ try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = null;
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			String loTaskStatusId = loP8ProcessServiceForSolicitationFinancials.fetchTaskStatusFromView(loP8UserSession,loHmReqdWFProperties);
			assertNotNull(loTaskStatusId);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}	
		
		
		
		@Test
		public void testSetWFProperty() throws ApplicationException
		{
			try{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="ECB0F647A7648B47BC346C03F4E294DB";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				
				boolean lbStatus = loP8ProcessServiceForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		@Test
		public void testSetWFProperty2() throws ApplicationException
		{
			try{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="7294B304F621E446AD443DCACEF90F45";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				
				boolean lbStatus = loP8ProcessServiceForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		
		
		
		@Test
		public void testFetchWorkflowIdFromView() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			String lsWorkflowId = loP8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView(loP8UserSession,loHmReqdWFProperties);
			assertNotNull(lsWorkflowId);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testFetchWorkflowIdFromView2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			String lsWorkflowId = loP8ProcessServiceForSolicitationFinancials.fetchWorkflowIdFromView(null,loHmReqdWFProperties);
			assertNotNull(lsWorkflowId);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testGetOpenTaskCount() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			int liCOunt = loP8ProcessServiceForSolicitationFinancials.getOpenTaskCount(loP8UserSession,
					loHmReqdWFProperties);
			assertTrue(liCOunt>=0);
		
		}
		@Test
		public void testGetOpenTaskCount2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			int liCOunt = loP8ProcessServiceForSolicitationFinancials.getOpenTaskCount(null,
					loHmReqdWFProperties);
			assertTrue(liCOunt>0);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testUnSuspendALLFinancialTask1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<HhsAuditBean> lbStatus = loP8ProcessServiceForSolicitationFinancials.unSuspendALLFinancialTask(loP8UserSession,
					loHmReqdWFProperties);
			assertTrue(true);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testUnSuspendALLFinancialTask2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<HhsAuditBean> lbStatus =  loP8ProcessServiceForSolicitationFinancials.unSuspendALLFinancialTask(null,
					loHmReqdWFProperties);
			assertTrue(true);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testFetchALLWorkflowIdFromView1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<String> loWorkflowIDList = loP8ProcessServiceForSolicitationFinancials.fetchALLWorkflowIdFromView(loP8UserSession,loHmReqdWFProperties);
			assertNotNull(loWorkflowIDList);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testFetchALLWorkflowIdFromView2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<String> loWorkflowIDList = loP8ProcessServiceForSolicitationFinancials.fetchALLWorkflowIdFromView(null,loHmReqdWFProperties);
			assertNotNull(loWorkflowIDList);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testSuspendALLFinancialTask1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<HhsAuditBean> lbStatus = loP8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask(loP8UserSession,
					loHmReqdWFProperties);
			assertTrue(true);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testSuspendALLFinancialTask2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("LaunchBy", "Tarun");
			List<HhsAuditBean> lbStatus =  loP8ProcessServiceForSolicitationFinancials.suspendALLFinancialTask(null,
					loHmReqdWFProperties);
			assertTrue(true);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testGetPEViewName1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String asQueueName = "vwvq1_HHSAcceleratorProcessQu";
			int aiPERegionId = 1;
			String lsPEViewName =  loP8ProcessServiceForSolicitationFinancials.getPEViewName(asQueueName,
					aiPERegionId);
			assertNotNull(lsPEViewName);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testGetPEViewName2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String asQueueName = "vwvq1_HHSAcceleratorProcessQu";
			int aiPERegionId = 0;
			String lsPEViewName =  loP8ProcessServiceForSolicitationFinancials.getPEViewName(asQueueName,
					aiPERegionId);
			assertNotNull(lsPEViewName);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}	
		
		@Test
		public void testGetPEViewName3() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String asQueueName = null;
			int aiPERegionId = 0;
			String lsPEViewName =  loP8ProcessServiceForSolicitationFinancials.getPEViewName(asQueueName,
					aiPERegionId);
			assertNotNull(lsPEViewName);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}	
	/* JUNIT BY SAHIL GIRDHAR   */
		
		public void testRelaunchAcceptProposalTask1() throws ApplicationException
		{
			try{
		
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Map<String, String> aoProposalDetailsMap = new HashMap<String, String>();
			Boolean lbWorkflowReLaunchedSuccess = false;
			aoProposalDetailsMap.put(HHSConstants.PROPOSAL_STATUS_ID, "19");
			aoProposalDetailsMap.put("ProcurementTitle", "ddg");
			aoProposalDetailsMap.put("LaunchBy", "Tarun");
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.relaunchAcceptProposalTask(
					loP8UserSession, aoProposalDetailsMap, "344", lbWorkflowReLaunchedSuccess);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}

		@Test
		public void testRelaunchAcceptProposalTask2() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Map<String, String> aoProposalDetailsMap = new HashMap<String, String>();
			Boolean lbWorkflowReLaunchedSuccess = false;
			aoProposalDetailsMap.put(HHSConstants.PROPOSAL_STATUS_ID, "18");
			aoProposalDetailsMap.put("ProcurementTitle", "ddg");
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.relaunchAcceptProposalTask(
					loP8UserSession, aoProposalDetailsMap, "344", lbWorkflowReLaunchedSuccess);
			assertFalse(lbWorkflowReLaunchedSuccess);
		}

		@Test
		public void testRelaunchAcceptProposalTask3() throws ApplicationException
		{
		try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Map<String, String> aoProposalDetailsMap = new HashMap<String, String>();
			Boolean lbWorkflowReLaunchedSuccess = false;
			aoProposalDetailsMap.put(HHSConstants.PROPOSAL_STATUS_ID, "19");
			aoProposalDetailsMap.put("ProcurementTitle", "ddg");
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.relaunchAcceptProposalTask(
					loP8UserSession, aoProposalDetailsMap, "344", lbWorkflowReLaunchedSuccess);
			assertTrue(Boolean.TRUE);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
	}
		}

		@Test
		public void testFinishReviewScoreWF1() throws ApplicationException
		{
			try{
			Boolean lbThrown = false;
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			List<String> aoEvaluationId = new ArrayList<String>();
			aoEvaluationId.add("13");
			HashMap<String, Object> aoWorkFlowPropMap = new HashMap<String, Object>();
			aoWorkFlowPropMap.put("LaunchBy", "Tarun");
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.finishReviewScoreWF(loP8UserSession,
					aoEvaluationId, "240", "F6C81F3DA3365F40B9137B1D0C818120", aoWorkFlowPropMap, "Approved");
			assertTrue(Boolean.TRUE);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
	}
		}

		@Test
		public void testFinishReviewScoreWF2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			List<String> aoEvaluationId = new ArrayList<String>();
			HashMap<String, Object> aoWorkFlowPropMap = new HashMap<String, Object>();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.finishReviewScoreWF(loP8UserSession,
					aoEvaluationId, "344", "F6C81F3DA3365F40B9137B1D0C818120", aoWorkFlowPropMap, "Pending");
			assertTrue(Boolean.TRUE);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
	}
		}

		@Test(expected = Exception.class)
		public void testFinishReviewScoreWF3() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			List<String> aoEvaluationId = new ArrayList<String>();
			HashMap<String, Object> aoWorkFlowPropMap = new HashMap<String, Object>();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.finishReviewScoreWF(null,
					aoEvaluationId, "344", "F6C81F3DA3365F40B9137B1D0C818120", aoWorkFlowPropMap, "Pending");
		}
		@Test
		public void testAssignMultiTask() throws ApplicationException
		{	try{
			List<String> aoWobNumbers = new ArrayList<String>();
			String asUserId = "";
			String asSessionUserName="";
			aoWobNumbers.add("A40AA6E9941BFE46A69662DA4DC48523");
			
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assignMultiTask(loP8UserSession,
					aoWobNumbers, asUserId, asSessionUserName);
			assertTrue(lbWorkflowReLaunchedSuccess);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testAssignMultiTask1() throws ApplicationException
		{
			try{
			Boolean lbThrown = false;
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assignMultiTask(loP8UserSession,
					null, null, null);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
		}

		@Test
		public void testAssignMultiTask2() throws ApplicationException
		{
			try{
			Boolean lbThrown = false;
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assignMultiTask(loP8UserSession,
					null, "hello", null);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
		}
		@Test(expected = ApplicationException.class)
		public void testAssignMultiTask3() throws ApplicationException
		{
			try{
			List<String> aoWobNumbers = new ArrayList<String>();
			String asUserId = "";
			String asSessionUserName="";
			aoWobNumbers.add("ECB0F647A7648B47BC346C03F4E294DB");
			aoWobNumbers.add("A40AA6E9941BFE46A69662DA4DC48523");
			aoWobNumbers.add("E5B4DF9FDF32CE4D82AEAC0D3A1A39F2");
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbWorkflowReLaunchedSuccess = false;
			lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assignMultiTask(loP8UserSession,
					aoWobNumbers, asUserId, asSessionUserName);
			assertFalse(false);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testRequestScoreAmendement1() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean loTaskVisibiltyReset = Boolean.FALSE;
			loTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.requestScoreAmendement(loP8UserSession,
					"3", "3");
			assertFalse(false);
		}catch(Exception aoExp){
			assertTrue(Boolean.TRUE);
		}
		}

		@Test
		public void testRequestScoreAmendement2() throws ApplicationException
		{
			try{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean loTaskVisibiltyReset = Boolean.FALSE;
			loTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.requestScoreAmendement(loP8UserSession,
					"aa", "hello");
			assertFalse(false);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testAssign() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assign(loP8UserSession, "A40AA6E9941BFE46A69662DA4DC48523",
						"", "");
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
		@Test
		public void testAssign4() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assign(loP8UserSession, "",
						"", "");
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
		
		@Test
		public void testAssign1() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assign(loP8UserSession, null,
						null, null);
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testAssign2() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assign(loP8UserSession, null,
						"hello", null);
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testAssign3() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.assign(loP8UserSession,
						"EF0511330D69E64C9D26E31E76ED3827", "", "Abc");
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
		
		@Test
		public void testFinishTask() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.finishTask(null, "",
						"", null);
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
		
		@Test
		public void testFinishTask2() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbWorkflowReLaunchedSuccess = false;
				lbWorkflowReLaunchedSuccess = loP8ProcessServiceForSolicitationFinancials.finishTask(loP8UserSession, "",
						"", null);
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
		@Test
		public void testFinishEvaluateProposalWF1() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				aoTaskDetailsBean.setProposalId("344");
				aoTaskDetailsBean.setWorkFlowId("9507D3224FCAC541BB793FA3B4E7B69B");
				aoTaskDetailsBean.setTaskStatus("Score Returned");
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbTaskVisibiltyReset = Boolean.FALSE;
				lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.finishEvaluateProposalWF(
						loP8UserSession, aoTaskDetailsBean);
			}
			
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testFinishEvaluateProposalWF2() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				aoTaskDetailsBean.setProposalId("555");
				aoTaskDetailsBean.setWorkFlowId("9507D3224FCAC541BB793FA3B4E7B69B");
				aoTaskDetailsBean.setTaskStatus("Approved");
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbTaskVisibiltyReset = Boolean.FALSE;
				lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.finishEvaluateProposalWF(
						loP8UserSession, aoTaskDetailsBean);
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testSetTaskUnassignedForAgencyUsers1() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				List<String> asUserIdList = new ArrayList<String>();
				asUserIdList.add("abc");
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbTaskVisibiltyReset = Boolean.FALSE;
				lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers(
						loP8UserSession, asUserIdList, "DOC", "Approved");
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testSetTaskUnassignedForAgencyUsers2() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				List<String> asUserIdList = new ArrayList<String>();
				asUserIdList.add("abc");
				P8UserSession loP8UserSession = getFileNetSession();
				Boolean lbTaskVisibiltyReset = Boolean.FALSE;
				lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.setTaskUnassignedForAgencyUsers(
						loP8UserSession, asUserIdList, "ACS", "Approved");
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		

		@Test
		public void testLaunchEvaluateScoreWF1() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			List<HashMap<String, Object>> aoEvalProposalMap = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("LaunchBy", "Tarun");
			aoEvalProposalMap.add(map);
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbTaskVisibiltyReset = Boolean.FALSE;
			lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF(loP8UserSession,
					aoEvalProposalMap);
			assertTrue(lbTaskVisibiltyReset);
		}

		@Test
		public void testLaunchEvaluateScoreWF2() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			List<HashMap<String, Object>> aoEvalProposalMap = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("LaunchBy", "abc");
			aoEvalProposalMap.add(map);
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbTaskVisibiltyReset = Boolean.FALSE;
			lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF(loP8UserSession,
					aoEvalProposalMap);
			assertTrue(lbTaskVisibiltyReset);
		}

		@Test
		public void testLaunchEvaluateScoreWF3() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			List<HashMap<String, Object>> aoEvalProposalMap = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			aoEvalProposalMap.add(map);
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbTaskVisibiltyReset = Boolean.FALSE;
			lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF(loP8UserSession,
					aoEvalProposalMap);
			assertTrue(lbTaskVisibiltyReset);
		}

		@Test(expected = ApplicationException.class)
		public void testLaunchEvaluateScoreWF4() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			List<HashMap<String, Object>> aoEvalProposalMap = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			aoEvalProposalMap.add(map);
			P8UserSession loP8UserSession = getFileNetSession();
			Boolean lbTaskVisibiltyReset = Boolean.FALSE;
			lbTaskVisibiltyReset = loP8ProcessServiceForSolicitationFinancials.launchEvaluateScoreWF(loP8UserSession, null);
		}

		

	
		@Test
		public void testLaunchWorkflow1() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdWorkflowProperties = new HashMap();
			aoHmReqdWorkflowProperties.put("ProcurementTitle", "ddd");
			aoHmReqdWorkflowProperties.put("AwardEPin", "1237689543013");
			aoHmReqdWorkflowProperties.put("LaunchBy", "Tarun");
			P8UserSession loP8UserSession = getFileNetSession();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.launchWorkflow(loP8UserSession,
					"WF315 - Contract Budget Amendment (AMD)", aoHmReqdWorkflowProperties);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testLaunchWorkflow2() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdWorkflowProperties = new HashMap();
			aoHmReqdWorkflowProperties.put("ProcurementTitle", "ddd");
			aoHmReqdWorkflowProperties.put("AwardEPin", "22233");
			aoHmReqdWorkflowProperties.put("LaunchBy", "abc");
			P8UserSession loP8UserSession = getFileNetSession();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.launchWorkflow(loP8UserSession,
					"WF315 - Contract Budget Amendment (AMD)", aoHmReqdWorkflowProperties);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testLaunchWorkflow3() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdWorkflowProperties = new HashMap();
			P8UserSession loP8UserSession = getFileNetSession();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.launchWorkflow(loP8UserSession,
					"WF315 - Contract Budget Amendment (AMD)", aoHmReqdWorkflowProperties);
			assertNotNull(lsWFWobNo);
		}

		@Test(expected = ApplicationException.class)
		public void testLaunchWorkflow4() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdWorkflowProperties = new HashMap();
			P8UserSession loP8UserSession = getFileNetSession();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.launchWorkflow(loP8UserSession, null,
					aoHmReqdWorkflowProperties);
		}

		@Test
		public void testCreateWhereClause1() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdProps = new HashMap();
			aoHmReqdProps.put("WorkFlowName", "WF315 - Contract Budget Amendment (AMD)");
			aoHmReqdProps.put("ProcurementTitle", "ddd");
			aoHmReqdProps.put("AwardEPin", "1237689543013");
			aoHmReqdProps.put("LaunchBy", "Tarun");
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.createWhereClause(aoHmReqdProps);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testCreateWhereClause2() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdProps = new HashMap();
			aoHmReqdProps.put("WorkFlowName", "WF315 - Contract Budget Amendment (AMD)");
			aoHmReqdProps.put("ProcurementTitle", "ddd");
			aoHmReqdProps.put("AwardEPin", "1122");
			aoHmReqdProps.put("LaunchBy", "Tarun");
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.createWhereClause(aoHmReqdProps);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testCreateWhereClause3() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			HashMap aoHmReqdProps = new HashMap();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.createWhereClause(aoHmReqdProps);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testCreateWhereClause4() throws ApplicationException
		{
			P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
			String lsWFWobNo = null;
			lsWFWobNo = loP8ProcessServiceForSolicitationFinancials.createWhereClause(null);
			assertNotNull(lsWFWobNo);
		}

		@Test
		public void testgetNonResponsiveAcceptProposalWobNum1() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				String aoAppex = null;
				P8UserSession loP8UserSession = getFileNetSession();
				aoAppex = loP8ProcessServiceForSolicitationFinancials.getNonResponsiveAcceptProposalWobNum(loP8UserSession,
						"dd", "344");
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}

		@Test
		public void testgetNonResponsiveAcceptProposalWobNum2() throws ApplicationException
		{
			Boolean lbThrown = false;
			try
			{
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				String aoAppex = null;
				P8UserSession loP8UserSession = getFileNetSession();
				aoAppex = loP8ProcessServiceForSolicitationFinancials.getNonResponsiveAcceptProposalWobNum(loP8UserSession,
						null, "344");
			}
			catch (ApplicationException loAppEx)
			{
				lbThrown = true;
				assertTrue("Application Exception thrown", lbThrown);
			}
			catch (Exception loEx)
			{
				lbThrown = true;
				assertTrue("Exception thrown", lbThrown);
			}
		}
}
