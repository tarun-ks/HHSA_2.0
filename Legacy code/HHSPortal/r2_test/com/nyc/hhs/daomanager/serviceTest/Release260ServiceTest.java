package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.util.DocumentLapsingUtility;

public class Release260ServiceTest
{

	static SqlSession moMyBatisSession = null;
	ProcurementService moProcurementService = new ProcurementService();
	ConfigurationService moConfigurationService = new ConfigurationService();
	AwardService moAwardService = new AwardService();
	P8ProcessOperationForSolicitationFinancials moP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
	static SqlSession loFilenetPEDBSession = null;
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
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
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
			moMyBatisSession.close();
			loFilenetPEDBSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	public List<ExtendedDocument> listAddForTest (){
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		loED.setDocumentId("003");
		loED.setDocumentTitle("Title003");
		loED.setDocumentType("Addenda");
		loED.setModifiedDate("01/02/2014");
		ExtendedDocument loED1 = new ExtendedDocument();
		loED1.setDocumentId("002");
		loED1.setDocumentTitle("Title002");
		loED1.setDocumentType("Addenda");
		loED1.setModifiedDate("01/05/2014");
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		ExtendedDocument loED2 = new ExtendedDocument();
		loED2.setDocumentId("003");
		loED2.setDocumentTitle("Title003");
		loED2.setDocumentType("Request for Proposals (RFP)");
		ExtendedDocument loED3 = new ExtendedDocument();
		loED3.setDocumentId("002");
		loED3.setDocumentTitle("Title002");
		loED3.setDocumentType("Request for Proposals (RFP)");
		loRfpDocList.add(loED2);
		loRfpDocList.add(loED3);
		return loRfpDocList;
	}
	
	public List<ExtendedDocument> listAddForTest4 (){
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		loED.setDocumentId("003");
		loED.setDocumentTitle("Title003");
		loED.setDocumentType("Addenda");
		loED.setModifiedDate("01/05/2014");
		ExtendedDocument loED1 = new ExtendedDocument();
		loED1.setDocumentId("002");
		loED1.setDocumentTitle("Title002");
		loED1.setDocumentType("Addenda");
		loED1.setModifiedDate("01/02/2014");
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		ExtendedDocument loED2 = new ExtendedDocument();
		loED2.setDocumentId("003");
		loED2.setDocumentTitle("Title003");
		loED2.setDocumentType("Request for Proposals (RFP)");
		ExtendedDocument loED3 = new ExtendedDocument();
		loED3.setDocumentId("002");
		loED3.setDocumentTitle("Title002");
		loED3.setDocumentType("Request for Proposals (RFP)");
		loRfpDocList.add(loED2);
		loRfpDocList.add(loED3);
		return loRfpDocList;
	}
	public List<ExtendedDocument> listAddForTest3 (){
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		loED.setDocumentId("003");
		loED.setDocumentTitle("Title003");
		loED.setDocumentType("Addenda");
		loED.setModifiedDate("01/02/2014");
		loED.setReferenceDocSeqNo("111");
		ExtendedDocument loED1 = new ExtendedDocument();
		loED1.setDocumentId("002");
		loED1.setDocumentTitle("Title002");
		loED1.setDocumentType("Addenda");
		loED1.setModifiedDate("01/02/2014");
		loED1.setReferenceDocSeqNo("222");
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		ExtendedDocument loED2 = new ExtendedDocument();
		loED2.setDocumentId("003");
		loED2.setDocumentTitle("Title003");
		loED2.setDocumentType("Request for Proposals (RFP)");
		ExtendedDocument loED3 = new ExtendedDocument();
		loED3.setDocumentId("002");
		loED3.setDocumentTitle("Title002");
		loED3.setDocumentType("Request for Proposals (RFP)");
		loRfpDocList.add(loED2);
		loRfpDocList.add(loED3);
		return loRfpDocList;
	}
	
	public List<ExtendedDocument> listAddForTest5 (){
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		loED.setDocumentId("003");
		loED.setDocumentTitle("Title003");
		loED.setDocumentType("Addenda");
		loED.setModifiedDate("01/02/2014");
		loED.setReferenceDocSeqNo("555");
		ExtendedDocument loED1 = new ExtendedDocument();
		loED1.setDocumentId("002");
		loED1.setDocumentTitle("Title002");
		loED1.setDocumentType("Addenda");
		loED1.setModifiedDate("01/02/2014");
		loED1.setReferenceDocSeqNo("111");
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		ExtendedDocument loED2 = new ExtendedDocument();
		loED2.setDocumentId("003");
		loED2.setDocumentTitle("Title003");
		loED2.setDocumentType("Request for Proposals (RFP)");
		ExtendedDocument loED3 = new ExtendedDocument();
		loED3.setDocumentId("002");
		loED3.setDocumentTitle("Title002");
		loED3.setDocumentType("Request for Proposals (RFP)");
		loRfpDocList.add(loED2);
		loRfpDocList.add(loED3);
		return loRfpDocList;
	}
	
	public List<ExtendedDocument> listAddForTest2 (){
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		loED.setDocumentId("003");
		loED.setDocumentTitle("Title003");
		loED.setDocumentType("Other");
		ExtendedDocument loED1 = new ExtendedDocument();
		loED1.setDocumentId("002");
		loED1.setDocumentTitle("Title002");
		loED1.setDocumentType("Other");
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		ExtendedDocument loED2 = new ExtendedDocument();
		loED2.setDocumentId("005");
		loED2.setDocumentTitle("Title005");
		loED2.setDocumentType("Other");
		loRfpDocList.add(loED2);
		return loRfpDocList;
	}
	
	/**Below are the JUnits for new method sortOtherDocTypeDocumentList
	 * in ProcurementService
	 *
	 * */
	@Test
	public void testSortOtherDocTypeDocumentList1() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		loRfpDocList = listAddForTest();
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		System.out.println("Input list : " + loRfpDocList);
		System.out.println("Sorted list : " + loRfpDocListSorted);
		assertNotNull(loRfpDocListSorted);
	}

	@Test
	public void testSortOtherDocTypeDocumentList2() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		loRfpDocList = listAddForTest3();		
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		System.out.println("Input list : " + loRfpDocList);
		System.out.println("Sorted list : " + loRfpDocListSorted);
		assertNotNull(loRfpDocListSorted);
	}
	
	@Test
	public void testSortOtherDocTypeDocumentList3() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		loRfpDocList = listAddForTest2();
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		System.out.println("Input list : " + loRfpDocList);
		System.out.println("Sorted list : " + loRfpDocListSorted);
		assertNotNull(loRfpDocListSorted);
	}
	@Test(expected=ApplicationException.class)
	public void testSortOtherDocTypeDocumentList4() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		ExtendedDocument loED = new ExtendedDocument();
		ExtendedDocument loED1 = new ExtendedDocument();
		loRfpDocList.add(loED);
		loRfpDocList.add(loED1);
		
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		assertNotNull(loRfpDocListSorted);
	}
	
	@Test(expected=ApplicationException.class)
	public void testSortOtherDocTypeDocumentList6() throws ApplicationException, java.text.ParseException
	{
		
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(null);
		assertNotNull(loRfpDocListSorted);
	}
	@Test
	public void testSortOtherDocTypeDocumentList7() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		loRfpDocList = listAddForTest4();
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		System.out.println("Input list : " + loRfpDocList);
		System.out.println("Sorted list : " + loRfpDocListSorted);
		assertNotNull(loRfpDocListSorted);
	}
	
	@Test
	public void testSortOtherDocTypeDocumentList8() throws ApplicationException, java.text.ParseException
	{
		List<ExtendedDocument> loRfpDocList = new ArrayList<ExtendedDocument>();
		loRfpDocList = listAddForTest5();
		List<ExtendedDocument> loRfpDocListSorted = moProcurementService.sortOtherDocTypeDocumentList(loRfpDocList);
		System.out.println("Input list : " + loRfpDocList);
		System.out.println("Sorted list : " + loRfpDocListSorted);
		assertNotNull(loRfpDocListSorted);
	}
	
	/**Below are the JUnits for new method procStatusSet
	 * in ConfigurationService
	 *
	 * */
	@Test
	public void testProcStatusSet1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2882");
		Boolean aoFinalFinish = true;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}
	
	@Test
	public void testProcStatusSet2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2881");
		Boolean aoFinalFinish = true;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}
	
	@Test
	public void testProcStatusSet3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("In Review");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2882");
		Boolean aoFinalFinish = true;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}
	
	@Test
	public void testProcStatusSet4() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("In Review");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2882");
		Boolean aoFinalFinish = false;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}
	@Test
	public void testProcStatusSet5() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2882");
		Boolean aoFinalFinish = false;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testProcStatusSet6() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Boolean aoFinalFinish = true;
		boolean loProcStatus = moConfigurationService.procStatusSet(moMyBatisSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testProcStatusSet7() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setProcurementId("2882");
		Boolean aoFinalFinish = true;
		boolean loProcStatus = moConfigurationService.procStatusSet(null, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}
	@Test(expected=Exception.class)
	public void testProcStatusSet8() throws ApplicationException
	{
		moConfigurationService.procStatusSet(null, null, null);
	}
	/**Below are the JUnits for new method delCoaFundingEntrieswhenContractDatesChanged
	 * in ConfigurationService
	 *
	 * */
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged1() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("239");
		//loProcurement.setContractStartFrom("1999");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	
	@Test(expected=Exception.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged2() throws ApplicationException
	{
		moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, null);
	}
	
	@Test(expected=ApplicationException.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged3() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("239");
		moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(null, loProcurement);
	}
	@Test(expected=Exception.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged4() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2882");
		loProcurement.setContractStartFrom("1999");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(null, null);
		assertTrue(loProcStatus);
	}
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged5() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2345");
		loProcurement.setContractStartDateUpdated("08/29/2013");
		loProcurement.setContractEndDateUpdated("08/31/2013");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged6() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2875");
		loProcurement.setContractStartDateUpdated("04/06/2016");
		loProcurement.setContractEndDateUpdated("04/08/2022");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged7() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2305");
		loProcurement.setContractStartDateUpdated("08/07/2013");
		loProcurement.setContractEndDateUpdated("08/08/2013");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged8() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2383");
		loProcurement.setContractStartDateUpdated("09/12/2018");
		loProcurement.setContractEndDateUpdated("09/12/2015");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged9() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2340");
		loProcurement.setContractStartDateUpdated("08/28/2013");
		loProcurement.setContractEndDateUpdated("07/31/2014");
		Boolean loProcStatus = (Boolean) moConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moMyBatisSession, loProcurement);
		assertTrue(loProcStatus);
	}
	/**Below are the JUnits for new method fetchPCOFCoADetails
	 * in ConfigurationService
	 *
	 * */
	@Test
	public void testFetchPCOFCoADetails1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setProcurementID("239");
		loCBGridBean.setFiscalYearID("1999");
		List<AccountsAllocationBean> loProcList = (List<AccountsAllocationBean>) moConfigurationService.fetchPCOFCoADetails(loCBGridBean, moMyBatisSession);
		assertNotNull(loProcList);
	}
	@Test
	public void testFetchPCOFCoADetails2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCoaDocType(true);
		loCBGridBean.setProcurementID("239");
		loCBGridBean.setFiscalYearID("1999");
		List<AccountsAllocationBean> loProcList = (List<AccountsAllocationBean>) moConfigurationService.fetchPCOFCoADetails(loCBGridBean, moMyBatisSession);
		assertNotNull(loProcList);
	}
	@Test
	public void testFetchPCOFCoADetails3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setIsProcCerTaskScreen(true);
		loCBGridBean.setProcurementID("239");
		loCBGridBean.setFiscalYearID("1999");
		List<AccountsAllocationBean> loProcList = (List<AccountsAllocationBean>) moConfigurationService.fetchPCOFCoADetails(loCBGridBean, moMyBatisSession);
		assertNotNull(loProcList);
	}
	@Test(expected=Exception.class)
	public void testFetchPCOFCoADetails4() throws ApplicationException
	{
	    moConfigurationService.fetchPCOFCoADetails(null, moMyBatisSession);
	}
	@Test(expected=ApplicationException.class)
	public void testFetchPCOFCoADetails5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
	    moConfigurationService.fetchPCOFCoADetails(loCBGridBean, null);
	}
	@Test(expected=Exception.class)
	public void testFetchPCOFCoADetails6() throws ApplicationException
	{
	    moConfigurationService.fetchPCOFCoADetails(null, null);
	}
	/**
	 * Below are the JUnits for new method fetchPCOFFundingSourcesDetails
	 * in ConfigurationService
	 *
	 * */
	@Test
	public void testFetchPCOFFundingSourcesDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = "239";

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID(lsProcurementId);
			loGridBean.setFiscalYearID("2013");
			loGridBean.setNoOfyears(2);
			loGridBean.setCreatedByUserId("city_142");
			loGridBean.setModifiedByUserId("city_142");
			loGridBean.setIsProcCerTaskScreen(true);

			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loProcCOAList1 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList1);

			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setCoaDocType(true);
			loGridBean.setIsProcCerTaskScreen(false);
			loGridBean.setFiscalYearID("2030");
			List<FundingAllocationBean> loProcCOAList2 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList2);
			
			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setCoaDocType(true);
			loGridBean.setIsProcCerTaskScreen(false);
			loGridBean.setProcurementID("2882");
			loGridBean.setFiscalYearID("");
			List<FundingAllocationBean> loProcCOAList3 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList3);
			
			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setIsProcCerTaskScreen(true);
			loGridBean.setCoaDocType(false);
			loGridBean.setProcurementID("2882");
			List<FundingAllocationBean> loProcCOAList4 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList4);

			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setIsProcCerTaskScreen(true);
			loGridBean.setCoaDocType(false);
			loGridBean.setProcurementID("2340");
			List<FundingAllocationBean> loProcCOAList5 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList5);
			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setIsProcCerTaskScreen(true);
			loGridBean.setCoaDocType(false);
			loGridBean.setProcurementID("2305");
			List<FundingAllocationBean> loProcCOAList6 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList6);
			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setIsProcCerTaskScreen(true);
			loGridBean.setCoaDocType(false);
			loGridBean.setProcurementID("2383");
			List<FundingAllocationBean> loProcCOAList7 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moMyBatisSession, loGridBean);

			assertNotNull(loProcCOAList7);
			// Negative scenario ---> Null session
			loConfigurationService.fetchPCOFFundingSourcesDetails(null, loGridBean);
			
			// Negative scenario ---> Null bean
			loConfigurationService.fetchPCOFFundingSourcesDetails(moMyBatisSession, null);
			
			// Negative scenario ---> Invalid data
			loGridBean.setProcurementID("000");
			loGridBean.setFiscalYearID("");
			loGridBean.setNoOfyears(0);
			loGridBean.setCreatedByUserId("city_142");
			loGridBean.setModifiedByUserId("city_142");
			loGridBean.setIsProcCerTaskScreen(true);
			loConfigurationService.fetchPCOFFundingSourcesDetails(moMyBatisSession, loGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	/**Below are the JUnits for new method fetchProcurementDetailsForFinancials
	 * in ConfigurationService
	 *
	 * */
	@Test
	public void testFetchProcurementDetailsForFinancials1() throws ApplicationException
	{
		String lsProcurementId = "2882";
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, lsProcurementId);
		assertNotNull(loProcCOF);
	}
	@Test
	public void testFetchProcurementDetailsForFinancials2() throws ApplicationException
	{
		String lsProcurementId = "2880";
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, lsProcurementId);
		assertNotNull(loProcCOF);
	}
	@Test(expected=ApplicationException.class)
	public void testFetchProcurementDetailsForFinancials3() throws ApplicationException
	{
		String lsProcurementId = "1";
		moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, lsProcurementId);
	}
	@Test(expected=Exception.class)
	public void testFetchProcurementDetailsForFinancials4() throws ApplicationException
	{
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, null);
		assertNotNull(loProcCOF);
	}
	@Test(expected=ApplicationException.class)
	public void testFetchProcurementDetailsForFinancials5() throws ApplicationException
	{
		String lsProcurementId = "2882";
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(null, lsProcurementId);
		assertNotNull(loProcCOF);
	}
	@Test
	public void testFetchProcurementDetailsForFinancials6() throws ApplicationException
	{
		String lsProcurementId = "2908";
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, lsProcurementId);
		assertNotNull(loProcCOF);
	}
	@Test
	public void testFetchProcurementDetailsForFinancials7() throws ApplicationException
	{
		String lsProcurementId = "2904";
		ProcurementCOF loProcCOF = moConfigurationService.fetchProcurementDetailsForFinancials(moMyBatisSession, lsProcurementId);
		assertNotNull(loProcCOF);
	}
	/**createAgencyTaskFilter method changes in 
	 * P8ProcessOperationForSolicitationFinancials class
	 */
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter1()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "city");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter001()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "ci'ty");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter002()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "All Staff");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter003()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "Unassigned All Levels");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter2()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "agency");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("SubmittedTo", "agency");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3001()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "Unassign");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3002()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("SubmittedFrom", "agency");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3003()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("AssignedFrom", "agency");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3004()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("AssignedTo", "agency");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3005()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskType", "All Applications");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter3006()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskType", "123");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter4()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("AgencyId", "DOC123");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter5()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("AgencyId", "DOC");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter6()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "agency123");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter7()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "Unassigned123");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter8()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "city123");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	} 
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter9()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("TaskOwner", "Unassigned");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	} 
	@SuppressWarnings({"unchecked","rawtypes" })
	@Test
	public void testcreateAgencyTaskFilter10()
			throws ApplicationException
	{
		String loReturn;
		HashMap aoHmFilter = new HashMap();
		aoHmFilter.put("ProviderName","");
		loReturn = (String) moP8ProcessOperationForSolicitationFinancials.createAgencyTaskFilter(aoHmFilter);
		assertNotNull(loReturn);
	}

	/**getHomePageTaskCount method changes in 
	 * P8ProcessOperationForSolicitationFinancials class
	 * 
	 */
	@Test(expected = Exception.class)
	public void testGetHomePageTaskCount1()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		String asTaskOwnerName = null;
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		String asAgencyId = null;
		String asViewName = null;
		Boolean aoIncludeNotFlag = null;
		SqlSession aoDBSession = null;
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(aoDBSession, asViewName, aoTaskTypeMap, asTaskOwnerName, aoIncludeNotFlag, asAgencyId);
		assertNull(loReturn);
	} 
	@Test
	public void testGetHomePageTaskCount2()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Invoice Review", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		aoTaskTypeMap.put("Payment Review", 0);
		aoTaskTypeMap.put("Contract Certification of Funds", 0);
		aoTaskTypeMap.put("Evaluate Proposal", 0);
		aoTaskTypeMap.put("Accept Proposal", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "agency_14", false, "DOC");
		assertNotNull(loReturn);
	}  
	@Test(expected = ApplicationException.class)
	public void testGetHomePageTaskCount21()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = null;
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "agency_14", false, "DOC");
		assertNotNull(loReturn);
	}  
	@Test
	public void testGetHomePageTaskCount30()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Approve Award", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "Unassigned", false, "DOC");
		assertNotNull(loReturn);
	} 
	@Test
	public void testGetHomePageTaskCount3()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Contract Budget Review", 0);
		aoTaskTypeMap.put("Payment Review", 0);
		aoTaskTypeMap.put("Contract Certification of Funds", 0);
		aoTaskTypeMap.put("Evaluate Proposal", 0);
		aoTaskTypeMap.put("Invoice Review", 0);
		aoTaskTypeMap.put("Accept Proposal", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "agency_14", false, "DOC");
		assertNotNull(loReturn);
	} 
	@Test
	public void testGetHomePageTaskCount4()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Evaluate Proposal", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		aoTaskTypeMap.put("Payment Review", 0);
		aoTaskTypeMap.put("Contract Certification of Funds", 0);
		aoTaskTypeMap.put("Invoice Review", 0);
		aoTaskTypeMap.put("Accept Proposal", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "agency_21", false, "DOC");
		assertNotNull(loReturn);
	} 
	@Test
	public void testGetHomePageTaskCount5()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Service Application", 0);
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 0);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "city_43", false, null);
		assertNotNull(loReturn);
	}
	@Test
	public void testGetHomePageTaskCount6()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Service Application", 77);
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 0);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "city_43", false, "junk");
		assertNotNull(loReturn);
	}
	@Test(expected = Exception.class)
	public void testGetHomePageTaskCount7()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Service Application", 0);
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 88);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(null, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "city_43", false, "junk");
		assertNotNull(loReturn);
	}
	@Test(expected = Exception.class)
	public void testGetHomePageTaskCount8()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Service Application", 0);
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 0);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "junk", aoTaskTypeMap, "city_43", false, "junk");
		assertNotNull(loReturn);
	}
	@Test
	public void testGetHomePageTaskCount9()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 0);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		aoTaskTypeMap.put("Payment Review", 0);
		aoTaskTypeMap.put("Contract Certification of Funds", 0);
		aoTaskTypeMap.put("Evaluate Proposal", 0);
		aoTaskTypeMap.put("Invoice Review", 0);
		aoTaskTypeMap.put("Accept Proposal", 0);
		Boolean aoIncludeNotFlag = false;
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "agency_21", aoIncludeNotFlag, "DOC");
		assertNotNull(loReturn);
	} 
	@Test
	public void testGetHomePageTaskCount10()
			throws ApplicationException
	{
		HashMap<String, Integer> loReturn = new HashMap<String, Integer>();
		HashMap<String, Integer> aoTaskTypeMap = new HashMap<String, Integer>();
		aoTaskTypeMap.put("Service Application", 0);
		aoTaskTypeMap.put("Contract Configuration", 0);
		aoTaskTypeMap.put("Configure Award Documents", 0);
		aoTaskTypeMap.put("Review Scores", 0);
		aoTaskTypeMap.put("Contract Budget Review", 0);
		loReturn = (HashMap<String, Integer>) moP8ProcessOperationForSolicitationFinancials.getHomePageTaskCount(loFilenetPEDBSession, "vwvq1_HHSAcceleratorProcessQu", aoTaskTypeMap, "junk", false, "junk");
		assertNotNull(loReturn);
	}

	/**
	 * getNextFiscalYearStartDateOnRejection method in DocumentLapsingUtility.java
	 * for data inputs
	 */
	@Test
	public void testGetNextFiscalYearStartDateOnRejection1()
			throws ApplicationException {
		Calendar localendar = new GregorianCalendar(2013, 01, 01);
		Date lodate = DocumentLapsingUtility
				.getNextFiscalYearStartDateOnRejection(localendar);
		assertNotNull(lodate);
	}
	@Test(expected=Exception.class)
	public void testGetNextFiscalYearStartDateOnRejection2()
			throws ApplicationException {
		DocumentLapsingUtility
				.getNextFiscalYearStartDateOnRejection(null);
	}
	/**
	 * This method tests rollbackDueDateOnRejectionForChar500 private method
	 * through RollbackDueDateOnRejection method in DocumentLapsingUtility.java for data inputs
	 * 
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Test
	public void testRollbackDueDateOnRejection1() throws ApplicationException {

		Map lsMsg = null;
		Date aoDCurrentDueDate = new Date("01/01/2013");
		String asPeriodCoveredStartMonth = "jul";
		int aiPeriodCoveredStartYear = 1;
		String asPeriodCoveredEndMonth = "jun";
		int aiPeriodCoveredEndYear = 1;
		boolean abafterShortFiling = true;
		boolean abIsShortFiling = false;
		// First Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		System.out.println("First : " + lsMsg);
	}
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Test
	public void testRollbackDueDateOnRejection2() throws ApplicationException {

		Map lsMsg = null;
		Date aoDCurrentDueDate = new Date("01/01/2013");
		String asPeriodCoveredStartMonth = "jul";
		int aiPeriodCoveredStartYear = 1;
		String asPeriodCoveredEndMonth = "jun";
		int aiPeriodCoveredEndYear = 1;
		boolean abafterShortFiling = true;
		boolean abIsShortFiling = false;
		// First Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		System.out.println("First : " + lsMsg);
	}
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Test
	public void testRollbackDueDateOnRejection3() throws ApplicationException {

		Map lsMsg = null;
		Date aoDCurrentDueDate = new Date("01/01/2013");
		String asPeriodCoveredStartMonth = "jul";
		int aiPeriodCoveredStartYear = 1;
		String asPeriodCoveredEndMonth = "jun";
		int aiPeriodCoveredEndYear = 1;
		boolean abafterShortFiling = true;
		boolean abIsShortFiling = false;
		// First Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				asPeriodCoveredStartMonth, aiPeriodCoveredStartYear,
				asPeriodCoveredEndMonth, aiPeriodCoveredEndYear,
				P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE,
				P8Constants.PROPERTY_PE_LAW_TYPE, abIsShortFiling,
				abafterShortFiling);
		assertTrue(!lsMsg.isEmpty());
		System.out.println("First : " + lsMsg);
	}
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Test(expected=ApplicationException.class)
	public void testRollbackDueDateOnRejection4() throws ApplicationException {

		Map lsMsg = null;
		Date aoDCurrentDueDate = new Date("01/01/2013");
		// First Scenario
		lsMsg = DocumentLapsingUtility.rollbackDueDateOnRejection(
				P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, aoDCurrentDueDate,
				null, 0,
				null, 0,
				null,
				P8Constants.PROPERTY_PE_LAW_TYPE, true,
				false);
		assertTrue(!lsMsg.isEmpty());
		System.out.println("First : " + lsMsg);
	}
	
	@Test
	public void testCheckIfAllReqAwardDocsUploaded1() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "724",null,false);
		assertFalse(loDocumentCompleteStatus);
		}
	@Test
	public void testCheckIfAllReqAwardDocsUploaded2() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 0, "724",null,false);
		assertFalse(loDocumentCompleteStatus);
	}
	@Test
	public void testCheckIfAllReqAwardDocsUploaded3() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "704",null,true);
		assertTrue(loDocumentCompleteStatus);
	}
	@Test
	public void testCheckIfAllReqAwardDocsUploaded4() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "704",null,false);
		assertFalse(loDocumentCompleteStatus);
	}
	@Test
	public void testCheckIfAllReqAwardDocsUploaded5() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "704",null,null);
		assertTrue(loDocumentCompleteStatus);
	}
	@Test
	public void testCheckIfAllReqAwardDocsUploaded6() throws ApplicationException
	{
		Boolean loDocumentCompleteStatus;
		loDocumentCompleteStatus = moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, "704","",null);
		assertTrue(loDocumentCompleteStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testCheckIfAllReqAwardDocsUploaded7() throws ApplicationException
	{
		moAwardService.checkIfAllReqAwardDocsUploaded(moMyBatisSession, 1, null,"",null);
	}
	@Test(expected=ApplicationException.class)
	public void testValidateProcValueAndAllocatedValue1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		boolean lbStatus;
		aoTaskDetailsBean.setOldContStartDateTask("05/03/2014");
		aoTaskDetailsBean.setOldContEndDateTask("05/03/2016");
		aoTaskDetailsBean.setOldProcValTask("1000000");
		aoTaskDetailsBean.setProcurementId("2946");
		aoTaskDetailsBean.setTaskStatus("kljsalf");
		lbStatus = moConfigurationService.validateProcValueAndAllocatedValue(moMyBatisSession, aoTaskDetailsBean,true);
		assertTrue(lbStatus);
	}
	@Test
	public void testValidateProcValueAndAllocatedValue2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		boolean lbStatus;
		aoTaskDetailsBean.setOldContStartDateTask("05/03/2014");
		aoTaskDetailsBean.setOldContEndDateTask("05/03/2016");
		aoTaskDetailsBean.setOldProcValTask("1000000");
		aoTaskDetailsBean.setProcurementId("2946");
		aoTaskDetailsBean.setTaskStatus("Returned for Revision");
		lbStatus = moConfigurationService.validateProcValueAndAllocatedValue(moMyBatisSession, aoTaskDetailsBean,true);
		assertTrue(lbStatus);
	}
	@Test(expected=ApplicationException.class)
	public void testValidateProcValueAndAllocatedValue3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		boolean lbStatus;
		aoTaskDetailsBean.setOldContStartDateTask("05/03/2014");
		aoTaskDetailsBean.setOldContEndDateTask("05/03/2016");
		aoTaskDetailsBean.setOldProcValTask("1000000");
		aoTaskDetailsBean.setProcurementId("2946");
		aoTaskDetailsBean.setTaskStatus("kljsalf");
		lbStatus = moConfigurationService.validateProcValueAndAllocatedValue(moMyBatisSession, aoTaskDetailsBean,true);
		assertTrue(lbStatus);
	}
	@Test
	public void testValidateProcValueAndAllocatedValue4() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		boolean lbStatus;
		aoTaskDetailsBean.setOldContStartDateTask("05/03/2014");
		aoTaskDetailsBean.setOldContEndDateTask("05/03/2015");
		aoTaskDetailsBean.setOldProcValTask("1000000");
		aoTaskDetailsBean.setProcurementId("628");
		aoTaskDetailsBean.setTaskStatus("safdas");
		lbStatus = moConfigurationService.validateProcValueAndAllocatedValue(moMyBatisSession, aoTaskDetailsBean,true);
		assertTrue(lbStatus);
	}
	@Test(expected=Exception.class)
	public void testValidateProcValueAndAllocatedValue5() throws ApplicationException
	{
		moConfigurationService.validateProcValueAndAllocatedValue(moMyBatisSession, null,true);
		
	}
}