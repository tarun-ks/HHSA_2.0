package com.nyc.hhs.service.test.com;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.PsrService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;

//import com.nyc.hhs.daomanager.service.ArrayList;

public class PsrServiceTestR5
{
	PsrService psrService = new PsrService();
	private static P8UserSession moP8session = null;
	private static SqlSession moSession = null; // SQL Session
	
	public static P8UserSession getFileNetSession() throws ApplicationException
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
			moP8session = getFileNetSession();
			System.out.println("Before");
		}
		catch (Exception loEx)
		{
			/*
			 * lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 */
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
		/*
		 * catch (Exception loEx) { lbThrown = true;
		 * assertTrue("Exception thrown", lbThrown); }
		 */
		finally
		{
			/*
			 * moSession.rollback(); moSession.close();
			 */
		}
	}
	
	/*
	 * @Test (expected = ApplicationException.class) public void
	 * fetchPsrDetailscase1() throws ApplicationException
	 * 
	 * 
	 * { List<EvaluationBean> loEvaluationScoreList = null; HashMap<String,
	 * String> loProcurementMap = new HashMap<String, String>();
	 * psrService.fetchPsrDetails(null, loProcurementMap);
	 * 
	 * }
	 */
	@Test(expected = ApplicationException.class)
	public void generatePsrPdfCase1() throws ApplicationException
	
	{
		PsrBean loPsrBean = new PsrBean();
		ProcurementCOF loPCOFBean = new ProcurementCOF();
		String loProcurementID = null;
		List<String> loServiceList = new ArrayList<String>();
		List<String> loReturnedGridList = new ArrayList<String>();
		Boolean loShowFinanceGridFlag = true;
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		List<String> loFinanceGridList = new ArrayList<String>();
		psrService.generatePsrPdf(loProcurementID, loPsrBean, loServiceList, loPCOFBean, loReturnedGridList,
				loFinanceGridList, loShowFinanceGridFlag);
		
	}
	
	@Test
	public void generatePsrPdfCase2() throws ApplicationException
	
	{
		PsrBean loPsrBean = new PsrBean();
		ProcurementCOF loPCOFBean = new ProcurementCOF();
		String loProcurementID = "4552";
		List<Procurement> loServiceList = new ArrayList<Procurement>();
		Procurement obj = new Procurement();
		obj.setServiceName("Telecomm");
		List<String> loReturnedGridList = new ArrayList<String>();
		List<String> loFinanceGridList = new ArrayList<String>();
		
		HashMap<String, String> loProcurementMap = new HashMap<String, String>();
		loServiceList.add(0, obj);
		loPsrBean.setUserId("agency_14");
		loPCOFBean.setProcurementTitle("Sample");
		loReturnedGridList.add(0, loProcurementID);
		loPsrBean.setAccPrimaryContact("Username1 || 123456");
		loPsrBean.setAccSecondaryContact("Username2 || 999900");
		loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
		loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
		loPsrBean.setEmail("xyz@www.com");
		loPsrBean.setProcurementDescription("good");
		loPsrBean.setProgramName("name");
		loPsrBean.setAgencyName("agency_18");
		loPsrBean.setProcurementTitle("Sample");
		loPsrBean.setProcurementEpin("EFHUJ8766GTG");
		loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
		loPsrBean.setIsOpenEndedRFP("1");
		loPsrBean.setBasisContractOut("0,1");
		loPsrBean.setAnticipateLevelComp("H");
		loPsrBean.setServiceFilter("1");
		loPsrBean.setMultiYearHumanServContract("test case");
		loPsrBean.setContractTermInfo("6");
		loPsrBean.setCreatedDate("08/20/2012");
		loPsrBean.setApproverUserId("agency_14");
		loPsrBean.setContractStartFrom("08/20/2012");
		// loServiceList.get().getServiceName();
		Boolean loShowFinanceGridFlag = true;
		assertNotNull(psrService.generatePsrPdf(loProcurementID, loPsrBean, loServiceList, loPCOFBean,
				loReturnedGridList, loFinanceGridList, loShowFinanceGridFlag));
		
	}
	
	/*
	 * List generatePsrPdf(String aoProcurementID, PsrBean aoPsrBean, List
	 * aoServiceList, ProcurementCOF aoPCOFBean, List aoReturnedGridList, List
	 * aoFinanceGridList, Boolean aoShowFinanceGridFlag)
	 */
	/*
	 * @Test (expected = ApplicationException.class) public void
	 * createServicesRecordcase1() throws ApplicationException
	 * 
	 * 
	 * { PsrBean loPsrBean = new PsrBean(); com.itextpdf.text.Document
	 * loDocument = null ; String loProcurementID = null; List<Procurement>
	 * loServiceList = new ArrayList<Procurement>(); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * psrService.createServicesRecord(loDocument, loPsrBean,loServiceList); }
	 */
	
	@Test(expected = Exception.class)
	public void getPsrSummaryCase1() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTempMap = new HashMap<String, Object>();
		loTempMap.put("ProcurementID", "3925");
		
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setWorkFlowId("E30322D4A0DE284AA0C6CEBB24EEB4E8");
		loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loTempMap);
		psrService.getPsrSummary(null, loTaskDetailMap, loTaskDetailBean);
	}
	
	@Test
	public void getPsrSummaryCase2() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTempMap = new HashMap<String, Object>();
		loTempMap.put("ProcurementID", "3925");
		
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setWorkFlowId("E30322D4A0DE284AA0C6CEBB24EEB4E8");
		loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loTempMap);
		
		psrService.getPsrSummary(moSession, loTaskDetailMap, loTaskDetailBean);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getPsrSummaryCase3() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTempMap = new HashMap<String, Object>();
		loTempMap.put("ProcurementID", "sgvf.00");
		
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setWorkFlowId("E30322D4A0DE284AA0C6CEBB24EEB4E8");
		loTaskDetailMap.put("E30322D4A0DE284AA0C6CEBB24EEB4E8", loTempMap);
		
		psrService.getPsrSummary(moSession, loTaskDetailMap, loTaskDetailBean);
		
	}
	
	@Test(expected = Exception.class)
	public void getPsrServicesListCase1() throws ApplicationException
	{
		
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityId(null);
		psrService.getPsrServicesList(null, loTaskDetailBean);
	}
	
	@Test
	public void getPsrServicesListCase2() throws ApplicationException
	{
		
		List<Procurement> loserviceList = null;
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityId("3995");
		loserviceList = (List<Procurement>) psrService.getPsrServicesList(moSession, loTaskDetailBean);
		// loserviceList.add((TaskDetailsBean)
		// procurementService.getPsrServicesList(moSession, loTaskDetailBean));
		assertNotNull(loserviceList);
	}
	
	@Test
	public void getPcofPsrdetailsCase1() throws ApplicationException
	{
		
		ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setIsOpenEndedRFP("0");
		loPsrBean.setEstProcurementValue(new java.math.BigDecimal(1));
		
		assertNull(psrService.getPcofPsrdetails(moSession, loPsrBean));
	}
	
	@Test(expected = ApplicationException.class)
	public void getPcofPsrdetailsCase2() throws ApplicationException
	{
		
		ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setIsOpenEndedRFP("0");
		loPsrBean.setEstProcurementValue(new java.math.BigDecimal(1));
		// procurementService.getPcofPsrdetails(moSession, loPsrBean);
		
		psrService.getPcofPsrdetails(null, loPsrBean);
	}
	
	@Test
	public void getPcofPsrdetailsCase3() throws ApplicationException
	{
		
		ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setIsOpenEndedRFP("0");
		loPsrBean.setEstProcurementValue(new java.math.BigDecimal(0));
		
		assertNull(psrService.getPcofPsrdetails(moSession, loPsrBean));
	}
	
	@Test
	public void getPcofPsrdetailsCase4() throws ApplicationException
	{
		
		ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setIsOpenEndedRFP("1");
		loPsrBean.setEstProcurementValue(new java.math.BigDecimal(0));
		
		assertNull(psrService.getPcofPsrdetails(moSession, loPsrBean));
	}
	
	@Test(expected = Exception.class)
	public void savePsrDetailsCase1() throws ApplicationException
	{
		
		ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setBasisContractOut("3");
		loPsrBean.setAnticipateLevelComp("L");
		loPsrBean.setConsiderationPrice("asd");
		loPsrBean.setConceptReportReleaseDt("06/22/16 12:00:00");
		loPsrBean.setRenewalOption("add");
		loPsrBean.setMultiYearHumanServContract("asd");
		loPsrBean.setContractTermInfo("3");
		loPsrBean.setPsrPcofVersionNumber(1);
		loPsrBean.setStatusId("144");
		loPsrBean.setMultiYearHumanServOpt("5");
		loPsrBean.setUserId("agency_14");
		
		psrService.savePsrDetails(null, loPsrBean);
	}
	
	@Test
	public void savePsrDetailsCase2() throws ApplicationException
	{
		
		// ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("3945");
		loPsrBean.setBasisContractOut("3");
		loPsrBean.setAnticipateLevelComp("L");
		loPsrBean.setConsiderationPrice("asd");
		loPsrBean.setConceptReportReleaseDt("06/22/16 12:00:00");
		loPsrBean.setRenewalOption("add");
		loPsrBean.setMultiYearHumanServContract("asd");
		loPsrBean.setContractTermInfo("3");
		loPsrBean.setPsrPcofVersionNumber(1);
		loPsrBean.setStatusId("144");
		loPsrBean.setMultiYearHumanServOpt("5");
		loPsrBean.setUserId("agency_14");
		
		assertTrue(psrService.savePsrDetails(moSession, loPsrBean));
	}
	
	@Test
	public void savePsrDetailsCase3() throws ApplicationException
	{
		
		// ProcurementCOF loProcurementPcofBean = new ProcurementCOF();
		PsrBean loPsrBean = new PsrBean();
		loPsrBean.setProcurementId("4626");
		loPsrBean.setBasisContractOut("0");
		loPsrBean.setAnticipateLevelComp("M");
		loPsrBean.setConsiderationPrice("sdcscd");
		loPsrBean.setConceptReportReleaseDt("06/22/16 12:00:00");
		loPsrBean.setRenewalOption("wsdx");
		loPsrBean.setMultiYearHumanServContract("gfrb");
		loPsrBean.setContractTermInfo("0");
		loPsrBean.setPsrPcofVersionNumber(0);
		loPsrBean.setStatusId("dfvgb");
		loPsrBean.setMultiYearHumanServOpt("0");
		loPsrBean.setUserId("agency_14");
		
		assertTrue(psrService.savePsrDetails(moSession, loPsrBean));
	}
	
	@Test
	public void updatePsrStatusFlagCase1() throws ApplicationException
	{
		String loprocurementId = "4192";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskName("Approve PSR");
		loTaskDetailsBean.setUserId("agency_14");
		String lostatusId = "171";
		assertTrue(psrService.updatePsrStatusFlag(moSession, loprocurementId, lostatusId, loTaskDetailsBean));
	}
	
	@Test
	public void updatePsrStatusFlagCase2() throws ApplicationException
	{
		String loprocurementId = "4192";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskName("Complete PSR");
		loTaskDetailsBean.setUserId("agency_14");
		String lostatusId = "171";
		assertTrue(psrService.updatePsrStatusFlag(moSession, loprocurementId, lostatusId, loTaskDetailsBean));
	}
	
	@Test(expected = Exception.class)
	public void updatePsrStatusFlagCase3() throws ApplicationException
	{
		String loprocurementId = "4192";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskName("Approve PSR");
		loTaskDetailsBean.setUserId("null");
		String lostatusId = "171";
		psrService.updatePsrStatusFlag(null, loprocurementId, lostatusId, loTaskDetailsBean);
	}
	
	@Test
	public void updatePsrStatusFlagCase4() throws ApplicationException
	{
		String loprocurementId = "4192";
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskName("XYZ");
		loTaskDetailsBean.setUserId("null");
		String lostatusId = "171";
		assertFalse(psrService.updatePsrStatusFlag(moSession, loprocurementId, lostatusId, loTaskDetailsBean));
	}
	
	@Test(expected = Exception.class)
	public void updatePsrPdfStatusFlagCase1() throws ApplicationException
	{
		String loprocurementId = "4192";
		String lsPdfFlag = "1";
		psrService.updatePsrPdfStatusFlag(null, loprocurementId, lsPdfFlag);
	}
	
	@Test
	public void updatePsrPdfStatusFlagCase2() throws ApplicationException
	{
		String loprocurementId = "4607";
		String lsPdfFlag = "1";
		assertTrue(psrService.updatePsrPdfStatusFlag(moSession, loprocurementId, lsPdfFlag));
	}
	
	@Test
	public void fetchPCOFFinanceDetailsCase1() throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		String loprocurementId = "4192";
		
		loAccountsAllocationBeanList = psrService.fetchPCOFFinanceDetails(moSession, loprocurementId);
		assertNotNull(loAccountsAllocationBeanList);
	}
	
	@Test(expected = Exception.class)
	public void fetchPCOFFinanceDetailsCase2() throws ApplicationException
	{
		List<AccountsAllocationBean> loAccountsAllocationBeanList = null;
		String loprocurementId = "4192";
		
		loAccountsAllocationBeanList = psrService.fetchPCOFFinanceDetails(null, loprocurementId);
		
	}
	
	@Test(expected = Exception.class)
	public void fetchPdfFlagListCase1() throws ApplicationException
	{
		List<String> loProcurementId = null;
		
		loProcurementId = psrService.fetchPdfFlagList(null);
		
	}
	
	@Test
	public void fetchPdfFlagListCase2() throws ApplicationException
	{
		List<String> loProcurementId = null;
		
		loProcurementId = psrService.fetchPdfFlagList(moSession);
		assertNotNull(loProcurementId);
	}
	
	/*
	 * @Test public void createServicesRecordCase1() throws ApplicationException
	 * 
	 * 
	 * { PsrBean loPsrBean = new PsrBean(); ProcurementCOF loPCOFBean = new
	 * ProcurementCOF(); String loProcurementID = "4552"; List<Procurement>
	 * loServiceList = new ArrayList<Procurement>(); Procurement obj =new
	 * Procurement(); obj.setServiceName("Telecomm"); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * loServiceList.add(0, obj); loPsrBean.setUserId("agency_14");
	 * loPCOFBean.setProcurementTitle("Sample"); loReturnedGridList.add(0,
	 * loProcurementID); loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("1");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012");
	 * //loServiceList.get().getServiceName(); com.itextpdf.text.Document
	 * aoDocument = new com.itextpdf.text.Document(); aoDocument.open();
	 * psrService.createServicesRecord(aoDocument,loPsrBean,loServiceList);
	 * 
	 * }
	 * 
	 * @Test public void createServicesRecordCase2() throws ApplicationException
	 * 
	 * 
	 * { PsrBean loPsrBean = new PsrBean(); ProcurementCOF loPCOFBean = new
	 * ProcurementCOF(); String loProcurementID = "4552"; List<Procurement>
	 * loServiceList = new ArrayList<Procurement>(); Procurement obj =new
	 * Procurement(); obj.setServiceName("Telecomm"); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * loServiceList.add(0, obj); loPsrBean.setUserId("agency_14");
	 * loPCOFBean.setProcurementTitle("Sample"); loReturnedGridList.add(0,
	 * loProcurementID); loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012");
	 * //loServiceList.get().getServiceName(); com.itextpdf.text.Document
	 * aoDocument = new com.itextpdf.text.Document(); aoDocument.open();
	 * psrService.createServicesRecord(aoDocument,loPsrBean,loServiceList);
	 * 
	 * }
	 * 
	 * @Test(expected = Exception.class) public void createServicesRecordCase3()
	 * throws ApplicationException
	 * 
	 * 
	 * { PsrBean loPsrBean = new PsrBean(); ProcurementCOF loPCOFBean = new
	 * ProcurementCOF(); String loProcurementID = "4552"; List<Procurement>
	 * loServiceList = new ArrayList<Procurement>(); Procurement obj =new
	 * Procurement(); obj.setServiceName("Telecomm"); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * loServiceList.add(0, obj); loPsrBean.setUserId("agency_14");
	 * loPCOFBean.setProcurementTitle("Sample"); loReturnedGridList.add(0,
	 * loProcurementID); loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012");
	 * //loServiceList.get().getServiceName(); com.itextpdf.text.Document
	 * aoDocument = new com.itextpdf.text.Document(); //aoDocument.open();
	 * psrService.createServicesRecord(aoDocument,loPsrBean,loServiceList);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createAllocationDataTableCase1() throws
	 * ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * Map<String, String> loFiscalYrMap = new HashMap<String, String>();
	 * loFiscalYrMap.put("liFYcount", "1");
	 * loFiscalYrMap.put("liStartFyCounter", "1"); PsrBean loPsrBean = new
	 * PsrBean(); ProcurementCOF loPCOFBean = new ProcurementCOF(); String
	 * loProcurementID = "4552"; String lsParagraphHeaderText = "HEADER"; String
	 * lsFirstColumnText = "Column First"; String lsLastColumnText =
	 * "Column Last"; List<AccountsAllocationBean> loServiceList = new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj =new
	 * AccountsAllocationBean(); obj.setAmmount("234"); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * loServiceList.add(0, obj); loPsrBean.setUserId("agency_14");
	 * loPCOFBean.setProcurementTitle("Sample"); loReturnedGridList.add(0,
	 * loProcurementID); loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012");
	 * //loServiceList.get().getServiceName(); com.itextpdf.text.Document
	 * aoDocument = new com.itextpdf.text.Document(); aoDocument.open();
	 * psrService
	 * .createAllocationDataTable(lsParagraphHeaderText,lsFirstColumnText
	 * ,lsLastColumnText,loServiceList,loFiscalYrMap,aoDocument);
	 * 
	 * }
	 */
	
	/*
	 * @Test(expected = Exception.class) public void
	 * createAllocationDataTableCase2() throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * Map<String, String> loFiscalYrMap = new HashMap<String, String>(); //
	 * loFiscalYrMap.put("liFYcount", "1");
	 * loFiscalYrMap.put("liStartFyCounter", "1"); PsrBean loPsrBean = new
	 * PsrBean(); ProcurementCOF loPCOFBean = new ProcurementCOF(); String
	 * loProcurementID = "4552"; String lsParagraphHeaderText = "HEADER"; String
	 * lsFirstColumnText = "Column First"; String lsLastColumnText =
	 * "Column Last"; List<AccountsAllocationBean> loServiceList = new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj =new
	 * AccountsAllocationBean(); obj.setAmmount("234"); List<String>
	 * loReturnedGridList = new ArrayList<String>();
	 * 
	 * HashMap<String, String> loProcurementMap = new HashMap<String, String>();
	 * loServiceList.add(0, obj); loPsrBean.setUserId("agency_14");
	 * loPCOFBean.setProcurementTitle("Sample"); loReturnedGridList.add(0,
	 * loProcurementID); //loServiceList.get().getServiceName();
	 * com.itextpdf.text.Document aoDocument = new com.itextpdf.text.Document();
	 * aoDocument.open();
	 * psrService.createAllocationDataTable(lsParagraphHeaderText
	 * ,lsFirstColumnText
	 * ,lsLastColumnText,loServiceList,loFiscalYrMap,aoDocument);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void addEmptyLineCase1() throws ApplicationException {
	 * Paragraph loParagraph = null; int number = 0;
	 * psrService.addEmptyLine(loParagraph,number); }
	 * 
	 * 
	 * @Test(expected = Exception.class) public void
	 * setAllocationDataDetailsCase1() throws ApplicationException { Font
	 * loLabelFont = null; BigDecimal loTotalVal = new BigDecimal("1");
	 * 
	 * NumberFormat loCurrencyFormatter =
	 * NumberFormat.getCurrencyInstance(Locale.US); int liFiscalCount = 1;
	 * PdfPTable loFundAllocationTable = new PdfPTable(2);
	 * 
	 * List<AccountsAllocationBean> loList =new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj = new
	 * AccountsAllocationBean();
	 * 
	 * Iterator loIterator = loList.iterator();
	 * psrService.setAllocationDataDetails
	 * (loLabelFont,loTotalVal,loCurrencyFormatter
	 * ,liFiscalCount,loFundAllocationTable, loIterator); }
	 * 
	 * @Test public void setAllocationDataDetailsCase2() throws
	 * ApplicationException { Font loLabelFont = null; BigDecimal loTotalVal =
	 * new BigDecimal("34");
	 * 
	 * NumberFormat loCurrencyFormatter =
	 * NumberFormat.getCurrencyInstance(Locale.US); int liFiscalCount = 1;
	 * PdfPTable loFundAllocationTable = new PdfPTable(2);
	 * //loFundAllocationTable.set List<AccountsAllocationBean> loList =new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj = new
	 * AccountsAllocationBean(); obj.setUnitOfAppropriation("weer");
	 * obj.setBudgetCode("code"); obj.setObjectCode("coded"); obj.setRc("RC");
	 * obj.setAmmount("2"); loList.add(obj); Iterator loIterator =
	 * loList.iterator();
	 * psrService.setAllocationDataDetails(loLabelFont,loTotalVal
	 * ,loCurrencyFormatter,liFiscalCount,loFundAllocationTable, loIterator); }
	 * 
	 * 
	 * @Test public void setAllocationDataDetailsCase3() throws
	 * ApplicationException { Font loLabelFont = null; BigDecimal loTotalVal =
	 * new BigDecimal("34");
	 * 
	 * NumberFormat loCurrencyFormatter =
	 * NumberFormat.getCurrencyInstance(Locale.US); int liFiscalCount = 1;
	 * PdfPTable loFundAllocationTable = new PdfPTable(2);
	 * //loFundAllocationTable.set List<AccountsAllocationBean> loList =new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj = new
	 * AccountsAllocationBean(); obj.setUnitOfAppropriation("weer");
	 * obj.setBudgetCode("code"); obj.setObjectCode("coded"); obj.setRc("RC");
	 * obj.setAmmount("-1"); loList.add(obj); Iterator loIterator =
	 * loList.iterator();
	 * psrService.setAllocationDataDetails(loLabelFont,loTotalVal
	 * ,loCurrencyFormatter,liFiscalCount,loFundAllocationTable, loIterator); }
	 */
	
	/*
	 * @Test public void setAllocationDataDetailsCase4() throws
	 * ApplicationException { Font loLabelFont = null; BigDecimal loTotalVal =
	 * new BigDecimal("34");
	 * 
	 * NumberFormat loCurrencyFormatter =
	 * NumberFormat.getCurrencyInstance(Locale.US); int liFiscalCount = 1;
	 * PdfPTable loFundAllocationTable = new PdfPTable(2);
	 * //loFundAllocationTable.set List<AccountsAllocationBean> loList =new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj = new
	 * AccountsAllocationBean(); obj.setUnitOfAppropriation("weer");
	 * obj.setBudgetCode("code"); obj.setObjectCode("coded"); obj.setRc("RC");
	 * obj.setAmmount("-50"); loList.add(obj); Iterator loIterator =
	 * loList.iterator();
	 * psrService.setAllocationDataDetails(loLabelFont,loTotalVal
	 * ,loCurrencyFormatter,liFiscalCount,loFundAllocationTable, loIterator); }
	 */
	/*
	 * @Test public void setAllocationDataDetailsOverallCase1() throws
	 * ApplicationException
	 * 
	 * 
	 * { Font loLabelFont = null; BigDecimal loTotalVal = null; NumberFormat
	 * loCurrencyFormatter = null; int liFiscalCount = 0;
	 * //loServiceList.get().getServiceName(); PdfPTable loFundAllocationTable =
	 * null; List<AccountsAllocationBean> loIterator = new
	 * ArrayList<AccountsAllocationBean>(); BigDecimal[][] liNums = null; int
	 * loTableCount = 0;
	 * psrService.setAllocationDataDetailsOverall(loLabelFont,loTotalVal
	 * ,loCurrencyFormatter
	 * ,liFiscalCount,loFundAllocationTable,loIterator,liNums,loTableCount);
	 * 
	 * }
	 * 
	 * @Test(expected = Exception.class) public void
	 * setAllocationDataDetailsOverallCase2() throws ApplicationException
	 * 
	 * 
	 * { Font loLabelFont = null; BigDecimal loTotalVal = null; NumberFormat
	 * loCurrencyFormatter = null; int liFiscalCount = 1;
	 * //loServiceList.get().getServiceName(); PdfPTable loFundAllocationTable =
	 * null; List<AccountsAllocationBean> loIterator = new
	 * ArrayList<AccountsAllocationBean>(); BigDecimal[][] liNums = null; int
	 * loTableCount = 0;
	 * psrService.setAllocationDataDetailsOverall(loLabelFont,loTotalVal
	 * ,loCurrencyFormatter
	 * ,liFiscalCount,loFundAllocationTable,loIterator,liNums,loTableCount);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createMailLabelCase1() throws ApplicationException
	 * 
	 * 
	 * { // com.itextpdf.text.Document loDocument = null; String lsLabel =
	 * "Text"; PdfPTable loPdfTable = null; String[] lsNameMailList = new
	 * String[2]; lsNameMailList[0] ="username"; lsNameMailList[1]="ssdd@j.com";
	 * com.itextpdf.text.Document loDocument = new com.itextpdf.text.Document();
	 * loDocument.open();
	 * 
	 * psrService.createMailLabel(loDocument,lsLabel,loPdfTable,lsNameMailList);
	 * 
	 * }
	 * 
	 * 
	 * @Test(expected = Exception.class) public void createMailLabelCase2()
	 * throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * String lsLabel = ""; PdfPTable loPdfTable = null; String[] lsNameMailList
	 * = new String[0]; // lsNameMailList[0] ="username";
	 * //lsNameMailList[1]="ssdd@j.com"; com.itextpdf.text.Document loDocument =
	 * new com.itextpdf.text.Document(); loDocument.open();
	 * 
	 * psrService.createMailLabel(loDocument,lsLabel,loPdfTable,lsNameMailList);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createPdfHeaderCase1() throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * PsrBean loPsrBean = new PsrBean(); loPsrBean.setUserId("agency_14");
	 * loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012"); com.itextpdf.text.Document
	 * loDocument = new com.itextpdf.text.Document(); loDocument.open();
	 * 
	 * psrService.createPdfHeader(loDocument,loPsrBean);
	 * 
	 * }
	 * 
	 * 
	 * @Test(expected = Exception.class) public void createPdfHeaderCase2()
	 * throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * PsrBean loPsrBean = null; com.itextpdf.text.Document loDocument = new
	 * com.itextpdf.text.Document(); loDocument.open();
	 * 
	 * psrService.createPdfHeader(loDocument,loPsrBean);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createParagraphCase1() throws ApplicationException
	 * 
	 * 
	 * { // com.itextpdf.text.Document loDocument = null; String loParagraphName
	 * = "Text"; Font loFont = psrService.createFont(12, 0); String loAlignType
	 * = "CENTER";
	 * 
	 * assertNotNull(psrService.createParagraph(loParagraphName,loFont,loAlignType
	 * ));
	 * 
	 * }
	 * 
	 * @Test public void createParagraphCase2() throws ApplicationException
	 * 
	 * 
	 * { // com.itextpdf.text.Document loDocument = null; String loParagraphName
	 * = "Text"; Font loFont = psrService.createFont(12, 0); String loAlignType
	 * = "LEFT";
	 * 
	 * assertNotNull(psrService.createParagraph(loParagraphName,loFont,loAlignType
	 * ));
	 * 
	 * }
	 * 
	 * @Test public void createParagraphCase3() throws ApplicationException
	 * 
	 * 
	 * { // com.itextpdf.text.Document loDocument = null; String loParagraphName
	 * = "Text"; Font loFont = psrService.createFont(12, 0); String loAlignType
	 * = "Top";
	 * 
	 * assertNotNull(psrService.createParagraph(loParagraphName,loFont,loAlignType
	 * ));
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createPdfRowCase1() throws ApplicationException
	 * 
	 * 
	 * { String asKey = "Key"; String asValue = "Value"; PdfPTable loTable = new
	 * PdfPTable(2); com.itextpdf.text.Document loDocument = new
	 * com.itextpdf.text.Document(); loDocument.open();
	 * psrService.createPdfRow(loDocument,loTable,asKey,asValue);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Test(expected = Exception.class) public void createPdfRowCase2() throws
	 * ApplicationException
	 * 
	 * 
	 * { String asKey = ""; String asValue = ""; PdfPTable loTable = null;
	 * com.itextpdf.text.Document loDocument = new com.itextpdf.text.Document();
	 * loDocument.open();
	 * psrService.createPdfRow(loDocument,loTable,asKey,asValue);
	 * 
	 * }
	 */
	
	/*
	 * 
	 * 
	 * @Test public void setCellPropertyLeftCase1() throws ApplicationException
	 * 
	 * 
	 * { Font loFontDocTable = psrService.createFont(12, 0); Paragraph
	 * loDocTableParagraph = null; PdfPCell loPdfCell = null;
	 * loDocTableParagraph =
	 * psrService.createParagraph("HHS Accelerator Service Applications Required:"
	 * , loFontDocTable, ""); loPdfCell = new PdfPCell(loDocTableParagraph);
	 * psrService.setCellPropertyLeft(loPdfCell);
	 * 
	 * }
	 * 
	 * @Test public void setCellPropertyCase1() throws ApplicationException
	 * 
	 * 
	 * { Font loFontDocTable = psrService.createFont(12, 0); Paragraph
	 * loDocTableParagraph = null; PdfPCell loPdfCell = null;
	 * loDocTableParagraph =
	 * psrService.createParagraph("HHS Accelerator Service Applications Required:"
	 * , loFontDocTable, ""); loPdfCell = new PdfPCell(loDocTableParagraph);
	 * psrService.setCellProperty(loPdfCell);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Test public void setCheckBoxImageCase1() throws ApplicationException
	 * 
	 * 
	 * { Boolean loImageFlag = true; psrService.setCheckBoxImage(loImageFlag);
	 * 
	 * }
	 * 
	 * @Test public void setCheckBoxImageCase2() throws ApplicationException
	 * 
	 * 
	 * { Boolean loImageFlag = false; psrService.setCheckBoxImage(loImageFlag);
	 * 
	 * }
	 * 
	 * 
	 * @Test(expected = Exception.class) public void setCheckBoxImageCase3()
	 * throws ApplicationException
	 * 
	 * 
	 * { Boolean loImageFlag = null ; psrService.setCheckBoxImage(loImageFlag);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createContractDetailsCase1() throws
	 * ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * PsrBean loPsrBean = new PsrBean();
	 * 
	 * List<Procurement> loServiceList = new ArrayList<Procurement>();
	 * Procurement obj =new Procurement(); loServiceList.add(0, obj);
	 * loPsrBean.setUserId("agency_14");
	 * 
	 * loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012");
	 * 
	 * com.itextpdf.text.Document loDocument = new com.itextpdf.text.Document();
	 * loDocument.open();
	 * psrService.createContractDetails(loDocument,loPsrBean,loServiceList);
	 * 
	 * }
	 * 
	 * 
	 * @Test(expected = Exception.class) public void
	 * createContractDetailsCase2() throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * PsrBean loPsrBean = new PsrBean();
	 * 
	 * List<Procurement> loServiceList = new ArrayList<Procurement>();
	 * Procurement obj =new Procurement();
	 * 
	 * 
	 * com.itextpdf.text.Document loDocument = new com.itextpdf.text.Document();
	 * loDocument.open();
	 * psrService.createContractDetails(loDocument,loPsrBean,loServiceList);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void createSubHeadingCase1() throws ApplicationException
	 * 
	 * 
	 * { com.itextpdf.text.Document loDocument = new
	 * com.itextpdf.text.Document(); loDocument.open(); String lsDocHeading =
	 * "Text"; Font loDocFont = psrService.createFont(12, 0);
	 * 
	 * psrService.createSubHeading(loDocument,lsDocHeading,loDocFont);
	 * 
	 * }
	 * 
	 * @Test(expected = ApplicationException.class) public void
	 * createSubHeadingCase2() throws ApplicationException
	 * 
	 * 
	 * { com.itextpdf.text.Document loDocument = new
	 * com.itextpdf.text.Document(); loDocument.open(); String lsDocHeading =
	 * ""; Font loDocFont = null;
	 * 
	 * psrService.createSubHeading(loDocument,lsDocHeading,loDocFont);
	 * 
	 * }
	 */
	
	/*
	 * @Test public void setPsrDetailsCase1() throws ApplicationException
	 * 
	 * 
	 * { PsrBean loPsrBean = new PsrBean();
	 * 
	 * assertFalse(psrService.setPsrDetails(moSession,loPsrBean));
	 * 
	 * }
	 * 
	 * 
	 * @Test public void getPsrDetailsCase1() throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * HashMap<String, String> loInputParam = new HashMap<String, String>();
	 * assertNull(psrService.getPsrDetails(moSession,loInputParam));
	 * 
	 * }
	 */
	
	@Test
	public void uploadPsrDocumentToFilenetCase1() throws ApplicationException
	
	{
		
		List<String> aoOutputFilePathList = new ArrayList<String>();
		aoOutputFilePathList.add("C:\\PDF\\4483.pdf");
		
		PsrBean loPsrBean = new PsrBean();
		
		List<Procurement> loServiceList = new ArrayList<Procurement>();
		Procurement obj = new Procurement();
		loServiceList.add(0, obj);
		loPsrBean.setUserId("agency_14");
		loPsrBean.setProcurementId("4626");
		loPsrBean.setPsrDetailId("123");
		loPsrBean.setAccPrimaryContact("Username1 || 123456");
		loPsrBean.setAccSecondaryContact("Username2 || 999900");
		loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
		loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
		loPsrBean.setEmail("xyz@www.com");
		loPsrBean.setProcurementDescription("good");
		loPsrBean.setProgramName("name");
		loPsrBean.setAgencyName("agency_18");
		loPsrBean.setProcurementTitle("Sample");
		loPsrBean.setProcurementEpin("EFHUJ8766GTG");
		loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
		loPsrBean.setIsOpenEndedRFP("1");
		loPsrBean.setBasisContractOut("0,1");
		loPsrBean.setAnticipateLevelComp("H");
		loPsrBean.setServiceFilter("0");
		loPsrBean.setMultiYearHumanServContract("test case");
		loPsrBean.setContractTermInfo("6");
		loPsrBean.setCreatedDate("08/20/2012");
		loPsrBean.setApproverUserId("agency_14");
		loPsrBean.setContractStartFrom("08/20/2012");
		
		assertNotNull(psrService.uploadPsrDocumentToFilenet(moP8session, aoOutputFilePathList, loPsrBean));
		
	}
	
	@Test(expected = Exception.class)
	public void uploadPsrDocumentToFilenetCase2() throws ApplicationException
	
	{
		
		List<String> loOutputFilePathList = new ArrayList<String>();
		
		PsrBean loPsrBean = new PsrBean();
		
		List<Procurement> loServiceList = new ArrayList<Procurement>();
		Procurement obj = new Procurement();
		loServiceList.add(0, obj);
		loPsrBean.setUserId("agency_14");
		
		loPsrBean.setAccPrimaryContact("Username1 || 123456");
		loPsrBean.setAccSecondaryContact("Username2 || 999900");
		loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
		loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
		loPsrBean.setEmail("xyz@www.com");
		loPsrBean.setProcurementDescription("good");
		loPsrBean.setProgramName("name");
		loPsrBean.setAgencyName("agency_18");
		loPsrBean.setProcurementTitle("Sample");
		loPsrBean.setProcurementEpin("EFHUJ8766GTG");
		loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
		loPsrBean.setIsOpenEndedRFP("1");
		loPsrBean.setBasisContractOut("0,1");
		loPsrBean.setAnticipateLevelComp("H");
		loPsrBean.setServiceFilter("0");
		loPsrBean.setMultiYearHumanServContract("test case");
		loPsrBean.setContractTermInfo("6");
		loPsrBean.setCreatedDate("08/20/2012");
		loPsrBean.setApproverUserId("agency_14");
		loPsrBean.setContractStartFrom("08/20/2012");
		String obj1 = new String();
		String loProcurementID = "4102";
		String loProcurementTitle = "title";
		loOutputFilePathList.add(0, loProcurementID);
		Iterator loIterator = loOutputFilePathList.iterator();
		
		assertNotNull(psrService.uploadPsrDocumentToFilenet(moP8session, loOutputFilePathList, loPsrBean));
		
	}
	
	/*
	 * @Test(expected = ApplicationException.class) public void
	 * uploadPsrDocumentToFilenetCase3() throws ApplicationException
	 * 
	 * 
	 * {
	 * 
	 * 
	 * List<String> loOutputFilePathList = new ArrayList<String>();
	 * 
	 * PsrBean loPsrBean = new PsrBean();
	 * 
	 * List<Procurement> loServiceList = new ArrayList<Procurement>();
	 * Procurement obj =new Procurement(); loServiceList.add(0, obj);
	 * loPsrBean.setUserId("agency_14");
	 * 
	 * loPsrBean.setAccPrimaryContact("Username1 || 123456");
	 * loPsrBean.setAccSecondaryContact("Username2 || 999900");
	 * loPsrBean.setAgecncyPrimaryContact("Username3 || 242353");
	 * loPsrBean.setAgecncySecondaryContact("Username3 || 9287364");
	 * loPsrBean.setEmail("xyz@www.com");
	 * loPsrBean.setProcurementDescription("good");
	 * loPsrBean.setProgramName("name"); loPsrBean.setAgencyName("agency_18");
	 * loPsrBean.setProcurementTitle("Sample");
	 * loPsrBean.setProcurementEpin("EFHUJ8766GTG");
	 * loPsrBean.setRfpReleaseDate(DateUtil.getDate("08/22/2013"));
	 * loPsrBean.setIsOpenEndedRFP("1"); loPsrBean.setBasisContractOut("0,1");
	 * loPsrBean.setAnticipateLevelComp("H"); loPsrBean.setServiceFilter("0");
	 * loPsrBean.setMultiYearHumanServContract("test case");
	 * loPsrBean.setContractTermInfo("6");
	 * loPsrBean.setCreatedDate("08/20/2012");
	 * loPsrBean.setApproverUserId("agency_14");
	 * loPsrBean.setContractStartFrom("08/20/2012"); String obj1 =new String();
	 * 
	 * String loPath = "C:/Users/tushar.d.singh/backup/important.pdf"; String
	 * loProcurementTitle = "title"; String loUserId = "agency_14";
	 * loOutputFilePathList.add(0, loPath);
	 * 
	 * Iterator loIterator = loOutputFilePathList.iterator();
	 * 
	 * assertNotNull(psrService.uploadPsrDocumentToFilenet(moP8session,
	 * loOutputFilePathList,loPsrBean));
	 * 
	 * }
	 */
	
	/*
	 * @Test(expected = Exception.class) public void
	 * setMultiYearContractTableCase1() throws ApplicationException
	 * 
	 * 
	 * { com.itextpdf.text.Document loDocument = new
	 * com.itextpdf.text.Document(); loDocument.open();
	 * 
	 * PsrBean loPsrBean = new PsrBean();
	 * 
	 * psrService.setMultiYearContractTable(loDocument,loPsrBean);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Test public void calculateOverallCase1() throws ApplicationException {
	 * Font loLabelFont = null; BigDecimal loTotalVal = new BigDecimal("34");
	 * 
	 * NumberFormat loCurrencyFormatter =
	 * NumberFormat.getCurrencyInstance(Locale.US); int liFiscalCount = 1;
	 * PdfPTable loFundAllocationTable = new PdfPTable(2);
	 * //loFundAllocationTable.set List<AccountsAllocationBean> loList =new
	 * ArrayList<AccountsAllocationBean>(); AccountsAllocationBean obj = new
	 * AccountsAllocationBean(); obj.setUnitOfAppropriation("weer");
	 * obj.setBudgetCode("code"); obj.setObjectCode("coded"); obj.setRc("RC");
	 * obj.setAmmount("2"); //loList.add(obj); Iterator loIterator =
	 * loList.iterator();
	 * psrService.calculateOverall(loList,loLabelFont,loTotalVal
	 * ,loCurrencyFormatter,liFiscalCount,loFundAllocationTable); }
	 */
	
	@Test
	public void checkAuditInsertCase1() throws ApplicationException
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setEntityId("2923");
		
		assertTrue(psrService.checkAuditInsert(moSession, "2923"));
	}
	
	@Test
	public void checkAuditInsertCase2() throws ApplicationException
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setEntityId("4626");
		
		assertTrue(psrService.checkAuditInsert(moSession, "4626"));
	}
	
	@Test(expected = ApplicationException.class)
	public void checkAuditInsertCase3() throws ApplicationException
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setEntityId(null);
		
		psrService.checkAuditInsert(moSession, null);
	}
	
	@Test(expected = ApplicationException.class)
	public void checkAuditInsertCase4() throws ApplicationException
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setEntityId("4438");
		
		psrService.checkAuditInsert(null, "4438");
	}
	
	@Test
	public void fetchPsrConfFundingDetailsCase1() throws ApplicationException
	{
		
		String ProcurementID = "3925";
		List<FundingAllocationBean> loList = new ArrayList<FundingAllocationBean>();
		loList = (psrService.fetchPsrConfFundingDetails(moSession, ProcurementID));
		
		assertNotNull(loList);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchPsrConfFundingDetailsCase2() throws ApplicationException
	{
		String ProcurementID = "3925";
		List<FundingAllocationBean> loList = new ArrayList<FundingAllocationBean>();
		loList = (psrService.fetchPsrConfFundingDetails(null, ProcurementID));
		
	}
	
	@Test(expected = ApplicationException.class)
	public void showPsrFundingGridCase1() throws ApplicationException
	{
		
		PsrBean loPsrBean = new PsrBean();
		ProcurementCOF loPCOFBean = new ProcurementCOF();
		String loProcurementID = "4552";
		Procurement obj = new Procurement();
		obj.setServiceName("Telecomm");
		psrService.showPsrFundingGrid(moSession, loPsrBean, loPCOFBean);
		
	}
	
	@Test
	public void showPsrFundingGridCase2() throws ApplicationException
	{
		
		PsrBean loPsrBean = new PsrBean();
		ProcurementCOF loPCOFBean = null;
		assertFalse(psrService.showPsrFundingGrid(moSession, loPsrBean, loPCOFBean));
		
	}
	
	@Test
	public void showPsrFundingGridCase3() throws ApplicationException
	{
		
		PsrBean loPsrBean = new PsrBean();
		ProcurementCOF loPCOFBean = new ProcurementCOF();
		
		loPsrBean.setProcurementId("4483");
		assertTrue(psrService.showPsrFundingGrid(moSession, loPsrBean, loPCOFBean));
		
	}
	
}
