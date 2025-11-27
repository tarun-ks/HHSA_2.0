package com.nyc.hhs.daomanager.serviceTest;

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

import com.nyc.hhs.batch.impl.PDFGenerationBatch;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.CreatePDFService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.PDFBatch;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class CreatePDFServiceTest
{
	private final CreatePDFService moCreatePDFService = new CreatePDFService();
	private static SqlSession moMyBatisSession = null;
	private final String msContractId = "27";
	private final String msContractIdInvalid = "#@$27";
	private final String msProcurementId = "135";
	private final String msBudgetId = "1";
	private final String msSubBudgetId = "1";
	private final String msStatusId = "In Progress";
	private final String msNewEntityType = "Contract";
	private final String msStatus = "Not Started";
	static P8UserSession moUserSession = new P8UserSession();

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
			String lsClassName = (new PDFGenerationBatch()).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;
			String lsCastorPath = ((new PDFGenerationBatch()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING)
					.replace(HHSConstants.PDF_CLASS, HHSConstants.CASTOR_MAPPING);

			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
			moUserSession = getFileNetSession();
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
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		loP8SecurityService.getPESession(loUserSession);
		loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	/**
	 * Method to test fetchContractCofDocDetails
	 * 
	 * @throws ApplicationException
	 */
	/*
	 * @Test public void testFetchContractCofDocDetails() throws
	 * ApplicationException { ProcurementCOF loProcurementCOF =
	 * moCreatePDFService.fetchContractCofDocDetails(moMyBatisSession,
	 * msContractId); assertNotNull(loProcurementCOF); }
	 *//**
	 * Method to test fetchContractCofDocDetails ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testFetchContractCofDocDetailsNegative() throws ApplicationException {
	 * ProcurementCOF loProcurementCOF =
	 * moCreatePDFService.fetchContractCofDocDetails(moMyBatisSession,
	 * msContractIdInvalid); assertNotNull(loProcurementCOF); }
	 */

	/**
	 * Method to test fetchContractFYDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchContractFYDetails() throws ApplicationException
	{
		Map<String, String> loContractFyMap = moCreatePDFService.fetchContractFYDetails(moMyBatisSession, msContractId);
		assertNotNull(loContractFyMap);
	}

	/**
	 * Method to test fetchContractFYDetails ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchContractFYDetailsNegative() throws ApplicationException
	{
		Map<String, String> loContractFyMap = moCreatePDFService.fetchContractFYDetails(moMyBatisSession,
				msContractIdInvalid);
		assertNotNull(loContractFyMap);
	}

	/**
	 * Method to test getGridBeanDetailforProcAllocation
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetGridBeanDetailforProcAllocation() throws ApplicationException
	{
		Map<String, String> loContractFyMap = new HashMap<String, String>();
		CBGridBean loCBGridBean = moCreatePDFService.getGridBeanDetailforProcAllocation(moMyBatisSession,
				msProcurementId, loContractFyMap);
		assertNotNull(loCBGridBean);
	}

	/**
	 * Method to test getGridBeanDetailforProcAllocation ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetGridBeanDetailforProcAllocationNegative() throws ApplicationException
	{
		Map<String, String> loContractFyMap = new HashMap<String, String>();
		CBGridBean loCBGridBean = moCreatePDFService.getGridBeanDetailforProcAllocation(moMyBatisSession, "$$#$",
				loContractFyMap);
		assertNotNull(loCBGridBean);
	}

	/**
	 * Method to test getSubBudgetDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetSubBudgetDetails1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", msBudgetId);
		loHashMap.put("subBudgetId", msSubBudgetId);
		List<CBGridBean> loSubBudgetList = moCreatePDFService.getSubBudgetDetails(moMyBatisSession, loHashMap);
		assertNotNull(loSubBudgetList);
	}

	/**
	 * Method to test getSubBudgetDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetSubBudgetDetails2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", msBudgetId);
		loHashMap.put("subBudgetId", null);
		List<CBGridBean> loSubBudgetList = moCreatePDFService.getSubBudgetDetails(moMyBatisSession, loHashMap);
		assertNotNull(loSubBudgetList);
	}

	/**
	 * Method to test getSubBudgetDetails ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testGetSubBudgetDetailsNegative() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("budgetId", "41$!#@");
		loHashMap.put("subBudgetId", "$#QA#");
		List<CBGridBean> loSubBudgetList = moCreatePDFService.getSubBudgetDetails(moMyBatisSession, loHashMap);
		assertNotNull(loSubBudgetList);
	}

	/**
	 * Method to test fetchEntityIdAndUpdateStatusForPdf
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchEntityIdAndUpdateStatusForPdf() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("statusId", msStatusId);
		loHashMap.put("newEntityType", msNewEntityType);
		loHashMap.put("status", msStatus);
		List<PDFBatch> loFetchEntityIdForPdf = moCreatePDFService.fetchEntityIdAndUpdateStatusForPdf(moMyBatisSession,
				loHashMap);
		assertNotNull(loFetchEntityIdForPdf);
	}

	/**
	 * Method to test fetchEntityIdAndUpdateStatusForPdf ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEntityIdAndUpdateStatusForPdfNegative() throws ApplicationException
	{

		List<PDFBatch> loFetchEntityIdForPdf = moCreatePDFService.fetchEntityIdAndUpdateStatusForPdf(moMyBatisSession,
				null);
		assertNotNull(loFetchEntityIdForPdf);
	}

	/**
	 * Method to test fetchDocumentsAndConvertToPDF
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchDocumentsAndConvertToPDF1() throws ApplicationException
	{
		moMyBatisSession.rollback();
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("680");
		loPDFBatch.setSubEntityId("313");
		loPDFBatch.setSubEntityType("Budget Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		String lspath = "C:\\PDF\\";
		ArrayList<String> loList = moCreatePDFService.fetchDocumentsAndConvertToPDF(moMyBatisSession, moUserSession,
				lspath, loPDFBatch);
		assertNotNull(loList);

	}

	/**
	 * Method to test fetchDocumentsAndConvertToPDF
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchDocumentsAndConvertToPDF2() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("672");
		loPDFBatch.setSubEntityId("672");
		loPDFBatch.setSubEntityType("Contract Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		ArrayList<String> loList = moCreatePDFService.fetchDocumentsAndConvertToPDF(moMyBatisSession, moUserSession,
				"C:\\PDF\\", loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testFetchDocumentsAndConvertToPDF1232() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract");
		loPDFBatch.setEntityType("Contract");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testuploadFinancialDocumentToFilenet() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract");
		loPDFBatch.setEntityType("Contract");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService
				.uploadFinancialDocumentToFilenet(moUserSession, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetBudget() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract Budget");
		loPDFBatch.setEntityType("Contract");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService
				.uploadFinancialDocumentToFilenet(moUserSession, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetAmendmentBudget() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Budget Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService
				.uploadFinancialDocumentToFilenet(moUserSession, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetAmendmentCOF() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService
				.uploadFinancialDocumentToFilenet(moUserSession, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetExp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract");
		loPDFBatch.setEntityType("Contract");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService.uploadFinancialDocumentToFilenet(null, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetBudgetExp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract Budget");
		loPDFBatch.setEntityType("Contract");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService.uploadFinancialDocumentToFilenet(null, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetAmendmentBudgetExp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Budget Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService.uploadFinancialDocumentToFilenet(null, loList, null, "abc");
		assertNotNull(loListNew);
	}

	@Test
	public void testuploadFinancialDocumentToFilenetAmendmentCOFExp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("667");
		loPDFBatch.setSubEntityId("667");
		loPDFBatch.setSubEntityType("Contract Amendment");
		loPDFBatch.setEntityType("Contract Amendment");
		String lspath = "C:\\PDF\\";
		List<String> loList = moCreatePDFService.addContractCertificationOfFundDetails(moMyBatisSession, lspath, "667",
				loPDFBatch);
		List<String> loListNew = moCreatePDFService.uploadFinancialDocumentToFilenet(null, loList, null, "abc");
		assertNotNull(loListNew);
	}

	/**
	 * Method to test fetchDocumentsAndConvertToPDF ApplicationException
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchDocumentsAndConvertToPDFNegative() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("568");
		loPDFBatch.setSubEntityId("241");
		loPDFBatch.setSubEntityType("Budget Amendment");
		ArrayList<String> loList = moCreatePDFService.fetchDocumentsAndConvertToPDF(moMyBatisSession, moUserSession,
				"C:\\PDF\\", loPDFBatch);
		assertNotNull(loList);
	}

	/**
	 * Method to test getContractBudgetSummaryDetails
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetContractBudgetSummaryDetails() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Budget");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(moMyBatisSession, "C:\\PDF\\", "75",
				loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails1() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Contract Budget");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(moMyBatisSession, "C:\\PDF\\", "75",
				loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails2() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract Amendment");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Budget Amendment");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(moMyBatisSession, "C:\\PDF\\", "75",
				loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails5() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract Amendment");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Contract Amendment");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(moMyBatisSession, "C:\\PDF\\", "75",
				loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetailsExp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Budget");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(null, "C:\\PDF\\", "75", loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails1Exp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Contract Budget");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(null, "C:\\PDF\\", "75", loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails2Exp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract Amendment");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Budget Amendment");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(null, "C:\\PDF\\", "75", loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetContractBudgetSummaryDetails5Exp() throws ApplicationException
	{
		PDFBatch loPDFBatch = new PDFBatch();
		loPDFBatch.setEntityId("75");
		loPDFBatch.setEntityType("Contract Amendment");
		loPDFBatch.setSubEntityId("28");
		loPDFBatch.setSubEntityType("Contract Amendment");
		List<String> loList = moCreatePDFService.getContractBudgetSummaryDetails(null, "C:\\PDF\\", "75", loPDFBatch);
		assertNotNull(loList);
	}

	@Test
	public void testGetAmendmentFiscialEPINDetails() throws ApplicationException
	{
		HashMap loMap = new HashMap();
		loMap.put("contractId", "24");
		loMap.put("budgetId", "1");
		ContractList loContract = moCreatePDFService.getAmendmentFiscialEPINDetails(moMyBatisSession, loMap);
		assertNotNull(loContract);
	}

	@Test(expected = ApplicationException.class)
	public void testGetAmendmentFiscialEPINDetailsnegative() throws ApplicationException
	{
		moCreatePDFService.getAmendmentFiscialEPINDetails(moMyBatisSession, null);

	}

	@Test
	public void testUpdateStatusForPdfAfterUpload() throws ApplicationException
	{
		HashMap loMap = new HashMap();
		loMap.put("status", "Generated");
		loMap.put("subEntityId", "670");
		boolean lbStatus = moCreatePDFService.updateStatusForPdfAfterUpload(moMyBatisSession, loMap);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateStatusForPdfAfterUploadNegative() throws ApplicationException
	{
		HashMap loMap = new HashMap();
		loMap.put("status", "Generated");
		loMap.put("subEntityId", "ssssssss");
		moCreatePDFService.updateStatusForPdfAfterUpload(moMyBatisSession, loMap);

	}

	@Test
	public void testgetFinalListPath() throws ApplicationException
	{
		ArrayList<String> loPathListR3 = new ArrayList<String>();
		ArrayList<String> loPathListR2 = new ArrayList<String>();
		ArrayList<String> loPathListR2Contract = new ArrayList<String>();
		ArrayList<String> loList = moCreatePDFService
				.getFinalListPath(loPathListR3, loPathListR2, loPathListR2Contract);
		assertNotNull(loList);
	}

	@Test
	public void testcheckPdfCreated() throws ApplicationException
	{
		String lsContractId = "669";
		Boolean loIsFinacialDocRequired = true;
		boolean lbStatus = moCreatePDFService.checkPdfCreated(moMyBatisSession, lsContractId, loIsFinacialDocRequired);
		assertTrue(lbStatus);
	}

	@Test(expected = Exception.class)
	public void testcheckPdfCreatedNegative() throws ApplicationException
	{
		Boolean loIsFinacialDocRequired = false;
		String lsContractId = "";
		moCreatePDFService.checkPdfCreated(moMyBatisSession, lsContractId, loIsFinacialDocRequired);

	}

	@Test(expected = ApplicationException.class)
	public void testgetContractFiscalYearsUtilnegative() throws ApplicationException
	{
		String asContractStartDate = "12/12/14";
		String asContractEndDate = "12/14/14";
		Map aoContractMap = new HashMap();
		moCreatePDFService.getContractFiscalYearsUtil(asContractStartDate, asContractEndDate, aoContractMap);

	}

	@Test
	public void testgetContractFiscalYearsUtil() throws ApplicationException
	{
		String asContractStartDate = "12/12/2012";
		String asContractEndDate = "12/14/2014";
		Map aoContractMap = new HashMap();
		moCreatePDFService.getContractFiscalYearsUtil(asContractStartDate, asContractEndDate, aoContractMap);

	}

	@Test
	public void testfetchAllDocumentsIds2() throws ApplicationException
	{
		List<String> loListOfDocId = new ArrayList<String>();
		List<String> loAwardDocumentList = new ArrayList<String>();
		List<String> loRFPDocumentList = new ArrayList<String>();
		// loAwardDocumentList.add("11");
		loListOfDocId.add("22");
		loRFPDocumentList.add("10");
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(loAwardDocumentList, loListOfDocId,
				loRFPDocumentList);
		assertNotNull(loList);
	}

	@Test
	public void testfetchAllDocumentsIds3() throws ApplicationException
	{
		List<String> loListOfDocId = new ArrayList<String>();
		List<String> loRFPDocumentList = new ArrayList<String>();
		loListOfDocId.add("22");
		loRFPDocumentList.add("10");
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(null, loListOfDocId, loRFPDocumentList);
		assertNotNull(loList);
	}

	@Test
	public void testfetchAllDocumentsIds4() throws ApplicationException
	{
		List<String> loRFPDocumentList = new ArrayList<String>();
		List<String> loAwardDocumentList = new ArrayList<String>();
		// loAwardDocumentList.add("11");
		loRFPDocumentList.add("22");
		loRFPDocumentList.add("10");
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(loAwardDocumentList, null, loRFPDocumentList);
		assertNotNull(loList);
	}

	@Test
	public void testfetchAllDocumentsIds7() throws ApplicationException
	{
		List<String> loListOfDocId = new ArrayList<String>();
		List<String> loAwardDocumentList = new ArrayList<String>();
		// loAwardDocumentList.add("11");
		loAwardDocumentList.add("22");
		loListOfDocId.add("10");
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(loAwardDocumentList, loListOfDocId, null);
		assertNotNull(loList);
	}

	@Test
	public void testfetchAllDocumentsIds5() throws ApplicationException
	{
		List<String> loListOfDocId = new ArrayList<String>();
		loListOfDocId.add("22");
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(null, null, null);
		assertNotNull(loList);
	}

	@Test
	public void testfetchAllDocumentsIds6() throws ApplicationException
	{
		List<String> loListOfDocId = new ArrayList<String>();
		List<String> loAwardDocumentList = new ArrayList<String>();
		List<String> loRFPDocumentList = new ArrayList<String>();
		List<String> loList = moCreatePDFService.fetchAllDocumentsIds(loAwardDocumentList, loListOfDocId,
				loRFPDocumentList);
		assertNotNull(loList);
	}

	@Test
	public void testgetDocumentTitle() throws ApplicationException
	{
		String asContractId = "11";
		String asProcurementId = "22";
		String asBudgetId = "268";
		String lsString = moCreatePDFService.getDocumentTitle(moMyBatisSession, asContractId, asProcurementId,
				asBudgetId);
		assertNotNull(lsString);
	}

	@Test
	public void testfetchAwardDocuments() throws ApplicationException
	{
		String asAwardId = "43";
		List<ExtendedDocument> loExtDoc = moCreatePDFService.fetchAwardDocuments(moMyBatisSession, asAwardId);
		assertNotNull(loExtDoc);
	}

	@Test
	public void testfetchRFPDocuments() throws ApplicationException
	{
		String asProcurementId = "164";
		List<ExtendedDocument> loExtDoc = moCreatePDFService.fetchRFPDocuments(moMyBatisSession, asProcurementId);
		assertNotNull(loExtDoc);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchAwardDocumentsnegative() throws ApplicationException
	{
		String asAwardId = "ffffff";
		moCreatePDFService.fetchAwardDocuments(moMyBatisSession, asAwardId);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchRFPDocumentssnegative() throws ApplicationException
	{
		String asProcurementId = null;
		moCreatePDFService.fetchRFPDocuments(moMyBatisSession, asProcurementId);

	}

	/*
	 * @Test(expected = java.lang.Exception.class) public void
	 * testCreatePDFServicefetchContractCofDocDetails0Negative() {
	 * CreatePDFService loCreatePDFService = new CreatePDFService(); try {
	 * loCreatePDFService.fetchContractCofDocDetails(null, null); } catch
	 * (ApplicationException e) { e.printStackTrace(); } }
	 */

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchContractFYDetails1Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchContractFYDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetGridBeanDetailforProcAllocation2Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getGridBeanDetailforProcAllocation(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetSubBudgetDetails3Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetAmendmentFiscialEPINDetails4Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getAmendmentFiscialEPINDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetContractBudgetSummaryDetails7Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getContractBudgetSummaryDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServiceuploadFinancialDocumentToFilenet8Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.uploadFinancialDocumentToFilenet(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchAwardDocuments9Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchAwardDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchRFPDocuments9Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchRFPDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetDocumentTitle10Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getDocumentTitle(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchAllDocumentsIds11Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchAllDocumentsIds(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServiceaddContractCertificationOfFundDetails12Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.addContractCertificationOfFundDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetContractFiscalYearsUtil13Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getContractFiscalYearsUtil(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchDocumentsAndConvertToPDF14Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchDocumentsAndConvertToPDF(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicefetchEntityIdAndUpdateStatusForPdf15Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.fetchEntityIdAndUpdateStatusForPdf(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServiceupdateStatusForPdfAfterUpload16Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.updateStatusForPdfAfterUpload(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicegetFinalListPath17Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.getFinalListPath(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testCreatePDFServicecheckPdfCreated18Negative()
	{
		CreatePDFService loCreatePDFService = new CreatePDFService();
		try
		{
			loCreatePDFService.checkPdfCreated(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
