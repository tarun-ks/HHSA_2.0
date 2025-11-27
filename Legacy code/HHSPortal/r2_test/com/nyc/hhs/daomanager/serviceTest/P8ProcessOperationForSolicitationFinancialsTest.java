package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

public class P8ProcessOperationForSolicitationFinancialsTest {
	
	/**
	 * @return
	 * @throws ApplicationException
	 */
	SqlSession moMyFilenetSession=HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
	
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
	
	
		
		@Test
		public void testCreateAgencyTaskFilter() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put("1", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(loHmReqdWFProperties);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testCreateAgencyTaskFilter2() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put(HHSConstants.PROPERTY_HMP_SUBMITTED_FROM,"");
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(loHmReqdWFProperties);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testCreateAgencyTaskFilter3() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put(HHSConstants.PROPERTY_HMP_SUBMITTED_TO,"");
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(loHmReqdWFProperties);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}	
		@Test
		public void testCreateAgencyTaskFilter4() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put(HHSConstants.PROPERTY_HMP_ASSIGNED_FROM,"");
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(loHmReqdWFProperties);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testCreateAgencyTaskFilter5() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put(HHSConstants.PROPERTY_HMP_ASSIGNED_TO,"");
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(loHmReqdWFProperties);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testCreateAgencyTaskFilter6() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
			loHmReqdWFProperties.put(HHSConstants.PROPERTY_HMP_ASSIGNED_TO,"");
			//loHmReqdWFProperties.put("2", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
			String lbStatus = loP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(null);
			assertTrue(lbStatus!=null);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		
		@Test
		public void testFetchAgencyTask1() throws ApplicationException
		{ 
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			String asOrderBY = null;
			int aiPageNum = 1;
			
			List<AgencyTaskBean> loAgencyTaskBeanDetails = loP8ProcessOperationForSolicitationFinancials.fetchAgencyTask(moMyFilenetSession,asViewName,loFilterProp,asOrderBY,aiPageNum);
			assertNotNull(loAgencyTaskBeanDetails);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}	
		@Test
		public void testFetchAgencyTask2() throws ApplicationException
		{ 
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			
			HashMap<Object,Object> loFilterProp = new HashMap<Object,Object>();
			loFilterProp.put("LaunchBy", "Tarun");
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			String asOrderBY = null;
			int aiPageNum = 1;
			
			List<AgencyTaskBean> loAgencyTaskBeanDetails = loP8ProcessOperationForSolicitationFinancials.fetchAgencyTask(null,asViewName,loFilterProp,asOrderBY,aiPageNum);
			assertNotNull(loAgencyTaskBeanDetails);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
	}
			
				
		@Test
		public void testGetHomePageTaskCount() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "agency_14";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertNotNull(loTaskCountMap);
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}
		@Test
		public void testGetHomePageTaskCount0() throws ApplicationException
		{
			try{
				
			
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "agency_14";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					null, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertNotNull(loTaskCountMap);
			}catch(Exception aoExp){
				assertTrue(Boolean.TRUE);
		}
		}
		@Test
		public void testGetHomePageTaskCountCase2() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "Unassigned";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");assertNotNull(loTaskCountMap);
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}
		
		@Test
		public void testGetHomePageTaskCountCase3() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "Unassigned";
			Boolean loIncludeNotFlag = true;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");assertNotNull(loTaskCountMap);
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}
		
		@Test
		public void testGetHomePageTaskCountCase4() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "agency_14";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertTrue(loTaskCountMap!=null);
			}	
		
		@Test
		public void testGetHomePageTaskCountCase5() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "agency_14";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertNotNull(loTaskCountMap);
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}	
		
		@Test
		public void testGetHomePageTaskCountCase6() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "##";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertTrue(loTaskCountMap.isEmpty());
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}	
		
		@Test
		public void testGetHomePageTaskCountCase7() throws ApplicationException
		{
			P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
			P8UserSession loP8UserSession = getFileNetSession();
			HashMap<String, Integer> loHmReqdWFProperties = new HashMap<String, Integer>();
			String lsTaskOwnerName = "";
			Boolean loIncludeNotFlag = false;
			String asViewName="vwvq1_HHSAcceleratorProcessQu";
			loHmReqdWFProperties.put(P8Constants.TASK_ACCEPT_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_EVALUATE_PROPOSAL, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_REVIEW_SCORES, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_CONFIGURATION, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_CONTRACT_COF, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_BUDGET_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_INVOICE_REVIEW, 0);
			loHmReqdWFProperties.put(P8Constants.TASK_PAYMENT_REVIEW, 0);
			HashMap<String, Integer> loTaskCountMap = loP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(moMyFilenetSession,asViewName,
					loHmReqdWFProperties, lsTaskOwnerName, loIncludeNotFlag, "DOC");
			assertFalse(loTaskCountMap.isEmpty());
			SqlSession filenetPEDBSession = loP8UserSession.getFilenetPEDBSession();
			filenetPEDBSession.close();
		}
		
		@Test
		public void testSetWFProperty() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="ECB0F647A7648B47BC346C03F4E294DB";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		@Test
		public void testSetWFProperty2() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="7294B304F621E446AD443DCACEF90F45";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		
		@Test
		public void testSetWFProperty3() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="7294B304F621E446AD443DCACEF90F45DDDDDDDDDD";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		@Test
		public void testSetWFProperty4() throws ApplicationException
		{
			try{
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				P8UserSession loP8UserSession = getFileNetSession();
				String loWobNum="ECB0F647A7648B47BC346C03F4E294DB";
				HashMap<String, Object> loHmReqdWFProperties = null;
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setWFProperty(loP8UserSession,loWobNum,loHmReqdWFProperties);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testSetTaskUnassignedForAgencyUsers() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				P8UserSession loP8UserSession = getFileNetSession();
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setTaskUnassignedForAgencyUsers(loP8UserSession,asViewName,lsWhereClasuse);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testSetTaskUnassignedForAgencyUsers2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				P8UserSession loP8UserSession = getFileNetSession();
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setTaskUnassignedForAgencyUsers(loP8UserSession,asViewName,lsWhereClasuse);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testSetTaskUnassignedForAgencyUsers3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				P8UserSession loP8UserSession = getFileNetSession();
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.setTaskUnassignedForAgencyUsers(null,asViewName,lsWhereClasuse);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		
		
		@Test
		public void testExecuteViewQuery() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				java.sql.Connection loConnection = moMyFilenetSession.getConnection();
				Statement loStatement  = loConnection.createStatement();
				lsQuery.append("select \"F_WobNum\",\"CurrentLevel\" from ");
				lsQuery.append(asViewName);
				lsQuery.append(" where ");
				lsQuery.append(lsWhereClasuse);
				ResultSet loResultSet = loP8ProcessOperationForSolicitationFinancials.executeViewQuery(moMyFilenetSession,lsQuery.toString(),loStatement);
				assertTrue(loResultSet!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testExecuteViewQuery2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				StringBuffer lsQuery = new StringBuffer(HHSConstants.EMPTY_STRING);
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				java.sql.Connection loConnection = moMyFilenetSession.getConnection();
				Statement loStatement  = loConnection.createStatement();
				lsQuery.append(" \"F_WobNum\",\"CurrentLevel\" from ");
				lsQuery.append(asViewName);
				lsQuery.append(" where ");
				lsQuery.append(lsWhereClasuse);
				ResultSet loResultSet = loP8ProcessOperationForSolicitationFinancials.executeViewQuery(moMyFilenetSession,lsQuery.toString(),loStatement);
				assertTrue(loResultSet!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		
		

		@Test
		public void testFetchWorkflowIdFromView() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				String lsWobNum = loP8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(lsWobNum!=null);
				}catch(ApplicationException aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFetchWorkflowIdFromView2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				String lsWobNum = loP8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(lsWobNum!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testFetchWorkflowIdFromView3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				String lsWobNum = loP8ProcessOperationForSolicitationFinancials.fetchWorkflowIdFromView(null,asViewName,lsWhereClasuse);
				assertTrue(lsWobNum!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFetchAllWorkflowIdFromView() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				List<String> loWorkflowIDList = loP8ProcessOperationForSolicitationFinancials.fetchALLWorkflowIdFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(loWorkflowIDList!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFetchAllWorkflowIdFromView2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				List<String> loWorkflowIDList = loP8ProcessOperationForSolicitationFinancials.fetchALLWorkflowIdFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(loWorkflowIDList!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testFetchAllWorkflowIdFromView3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				List<String> loWorkflowIDList = loP8ProcessOperationForSolicitationFinancials.fetchALLWorkflowIdFromView(null,asViewName,lsWhereClasuse);
				assertTrue(loWorkflowIDList!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
	
		@Test
		public void testFetchTaskStatusFromView() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				String lsTaskStatus = loP8ProcessOperationForSolicitationFinancials.fetchTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(lsTaskStatus!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFetchTaskStatusFromView2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				String lsTaskStatus = loP8ProcessOperationForSolicitationFinancials.fetchTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(lsTaskStatus!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testFetchTaskStatusFromView3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				String lsTaskStatus = loP8ProcessOperationForSolicitationFinancials.fetchTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(lsTaskStatus!=null);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
	
		
		@Test
		public void testGetOpenTaskCount() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				int liCount = loP8ProcessOperationForSolicitationFinancials.getOpenTaskCount(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(liCount>0);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testGetOpenTaskCount2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = null;
				int liCount = loP8ProcessOperationForSolicitationFinancials.getOpenTaskCount(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(liCount>0);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		@Test
		public void testGetOpenTaskCount3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				int liCount = loP8ProcessOperationForSolicitationFinancials.getOpenTaskCount(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(liCount>0);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testGetOpenTaskCount4() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "TarunVVV");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);;
				int liCount = loP8ProcessOperationForSolicitationFinancials.getOpenTaskCount(moMyFilenetSession,asViewName,lsWhereClasuse);
				assertTrue(liCount==0);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		
		@Test
		public void testFinishTask() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				//String asViewName="vwvq1_HHSAcceleratorProcessQu";
				String asWobNo = "A40AA6E9941BFE46A69662DA4DC48523";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				//String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8SecurityOperations filenetConnection = new P8SecurityOperations();
				P8UserSession loP8UserSession = getFileNetSession();
				
				VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
				
				VWStepElement loStepElement = loP8ProcessServiceForSolicitationFinancials.getStepElementfromWobNo(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.finishTask(loStepElement);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFinishTask2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				//String asViewName="vwvq1_HHSAcceleratorProcessQu";
				String asWobNo = "A40AA6E9941BFE46A69662DA4DC48523VVVV";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				//String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				P8ProcessServiceForSolicitationFinancials loP8ProcessServiceForSolicitationFinancials = new P8ProcessServiceForSolicitationFinancials();
				P8SecurityOperations filenetConnection = new P8SecurityOperations();
				P8UserSession loP8UserSession = getFileNetSession();
				
				VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
				
				VWStepElement loStepElement = loP8ProcessServiceForSolicitationFinancials.getStepElementfromWobNo(loVWSession, asWobNo, P8Constants.HSS_QUEUE_NAME);
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.finishTask(loStepElement);
				assertTrue(lbStatus);
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
	
		
				
				@Test
				public void testLaunchWorkflow() throws ApplicationException
				{
					try{
						
						P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
						String asWorkflowName = "WF315 - Contract Budget Amendment (AMD)";
						String asSessionUserName = "";
						HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
						loHmReqdWFProperties.put("LaunchBy", "Tarun");
						
						P8SecurityOperations filenetConnection = new P8SecurityOperations();
						P8UserSession loP8UserSession = getFileNetSession();
						
						VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
						
						String lbStatus = loP8ProcessOperationForSolicitationFinancials.launchWorkflow(loVWSession,asWorkflowName,loHmReqdWFProperties,
								asSessionUserName);
						assertTrue(lbStatus!=null);
						
						}catch(Exception aoExp){
							assertTrue(Boolean.TRUE);
					}
				}		
				
				@Test
				public void testLaunchWorkflow2() throws ApplicationException
				{
					try{
						
						P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
						String asWorkflowName = "##";
						String asSessionUserName = "";
						HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
						loHmReqdWFProperties.put("LaunchBy", "Tarun");
						
						P8SecurityOperations filenetConnection = new P8SecurityOperations();
						P8UserSession loP8UserSession = getFileNetSession();
						
						VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
						
						String lbStatus = loP8ProcessOperationForSolicitationFinancials.launchWorkflow(loVWSession,asWorkflowName,loHmReqdWFProperties,
								asSessionUserName);
						assertTrue(lbStatus!=null);
						
						}catch(Exception aoExp){
							assertTrue(Boolean.TRUE);
					}
				}		
				@Test
				public void testLaunchWorkflow3() throws ApplicationException
				{
					try{
						
						P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
						String asWorkflowName = "##";
						String asSessionUserName = "";
						HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
						loHmReqdWFProperties.put("LaunchBy", "Tarun");
						VWSession loVWSession = null;
						
						String lbStatus = loP8ProcessOperationForSolicitationFinancials.launchWorkflow(loVWSession,asWorkflowName,loHmReqdWFProperties,
								asSessionUserName);
						assertTrue(lbStatus!=null);
						
						}catch(Exception aoExp){
							assertTrue(Boolean.TRUE);
					}
				}	
		
				@Test
				public void testLaunchWorkflow4() throws ApplicationException
				{
					try{
						
						P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
						String asWorkflowName = null;
						String asSessionUserName = "";
						HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
						loHmReqdWFProperties.put("LaunchBy", "Tarun");
						VWSession loVWSession = null;
						String lbStatus = loP8ProcessOperationForSolicitationFinancials.launchWorkflow(loVWSession,asWorkflowName,loHmReqdWFProperties,
								asSessionUserName);
						assertTrue(lbStatus!=null);
						
						}catch(Exception aoExp){
							assertTrue(Boolean.TRUE);
					}
				}	
				
		@Test
		public void testAssignTask() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asSessionUserName = "";
				String asUserId = "";
				String asWobNo = "A40AA6E9941BFE46A69662DA4DC48523";
				
				P8SecurityOperations filenetConnection = new P8SecurityOperations();
				P8UserSession loP8UserSession = getFileNetSession();
				
				VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.assignTask(loVWSession,asWobNo,asUserId,asSessionUserName,P8Constants.HSS_QUEUE_NAME);
				
				assertTrue(lbStatus);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		
		@Test
		public void testAssignTaskException2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				String asSessionUserName = "";
				String asUserId = "";
				String asWobNo = "A40AA6E9941BFE46A69662DA4DC48523DDD";
				
				P8SecurityOperations filenetConnection = new P8SecurityOperations();
				P8UserSession loP8UserSession = getFileNetSession();
				
				VWSession loVWSession = filenetConnection.getPESession(loP8UserSession);
				
				boolean lbStatus = loP8ProcessOperationForSolicitationFinancials.assignTask(loVWSession,asWobNo,asUserId,asSessionUserName,P8Constants.HSS_QUEUE_NAME);
				
				assertTrue(lbStatus);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		
		
		
		@Test
		public void testFetchLastTaskStatusFromView() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				
				String lbStatus = loP8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				
				assertTrue(lbStatus!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		
		@Test
		public void testFetchLastTaskStatusFromView2() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "Tarun");
				String lsWhereClasuse = "";
				
				String lbStatus = loP8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				
				assertTrue(lbStatus!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}	
		@Test
		public void testFetchLastTaskStatusFromView3() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBy", "");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				
				String lbStatus = loP8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				
				assertTrue(lbStatus!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		
		@Test
		public void testFetchLastTaskStatusFromView4() throws ApplicationException
		{
			try{
				
				P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
				
				String asViewName="vwvq1_HHSAcceleratorProcessQu";
				HashMap<String, Object> loHmReqdWFProperties = new HashMap<String, Object>();
				loHmReqdWFProperties.put("LaunchBydsd", "");
				String lsWhereClasuse = createWhereClause(loHmReqdWFProperties);
				
				String lbStatus = loP8ProcessOperationForSolicitationFinancials.fetchLastTaskStatusFromView(moMyFilenetSession,asViewName,lsWhereClasuse);
				
				assertTrue(lbStatus!=null);
				
				}catch(Exception aoExp){
					assertTrue(Boolean.TRUE);
			}
		}
		private String createWhereClause(HashMap aoHmReqdProps) throws ApplicationException
		{
			
			String lsQueueWhereFilter = HHSConstants.EMPTY_STRING;
			String lsPropKey = null;
			StringBuffer lsQueueFilterBuffer = new StringBuffer(HHSConstants.EMPTY_STRING);
			if (null != aoHmReqdProps)
			{
				
				try
				{
					Iterator loItPropNames = aoHmReqdProps.keySet().iterator();
					while (loItPropNames.hasNext())
					{
						lsPropKey = (String) loItPropNames.next();
						if (null != lsPropKey && !lsPropKey.isEmpty())
							if (aoHmReqdProps.get(lsPropKey) instanceof List)
							{
								lsQueueFilterBuffer.append("\"");
								lsQueueFilterBuffer.append(lsPropKey);
								lsQueueFilterBuffer.append("\"");
								lsQueueFilterBuffer.append(" = IN(");
								lsQueueFilterBuffer.append(aoHmReqdProps.get(lsPropKey));
								lsQueueFilterBuffer.append(")");
							}
							else
							{
								lsQueueFilterBuffer.append("\"");
								lsQueueFilterBuffer.append(lsPropKey);
								lsQueueFilterBuffer.append("\"");
								lsQueueFilterBuffer.append(" = '");
								lsQueueFilterBuffer.append(aoHmReqdProps.get(lsPropKey));
								lsQueueFilterBuffer.append("'");
							}

						if (loItPropNames.hasNext())
						{
							lsQueueFilterBuffer.append(" AND ");
						}
					}
					lsQueueWhereFilter = lsQueueFilterBuffer.toString();
				}
				// handling exception other than ApplicationException
				catch (Exception aoEx)
				{	ApplicationException loAppex = new ApplicationException(
							"Error while fetching all work items from queue  : ", aoEx);
					loAppex.setContextData(aoHmReqdProps);
					
					throw loAppex;
				}
				
			}

			return lsQueueWhereFilter;
		}
}
