package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import com.nyc.hhs.daomanager.service.ProcurementDocsZipService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;


public class ProcurementDocsZipServiceTest
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet Session
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();

	}

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
	}
	
	public P8UserSession getFileNetSession() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.FILENET_URI));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.FILENET_URI));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.OBJECT_STORE_NAME));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NAME));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				HHSConstants.CONNECTION_POINT_NUMBER));
		loUserSession.setUserId(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_USER_ID));
		loUserSession.setPassword(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, HHSConstants.CE_PASSWORD));
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);

		return loUserSession;

	}
	
	@Test
	public void testFetchAwardDocDetails() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> asOrgDetailsMap = new HashMap<String, String>();
		Map<String, String> asParametersMap = new HashMap<String, String>();
		asOrgDetailsMap.put(HHSConstants.ORGANIZATION_NAME_CAPS, "R3 dev team's organizations");
		asParametersMap.put(HHSConstants.PROCUREMENT_ID, "2889");
		asParametersMap.put(HHSConstants.USER_ORG_ID, "r3_org");
		asParametersMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.fetchAwardDocDetails(moSession,
				asOrgDetailsMap, asParametersMap);
		assertNotNull(loAwardDocDetails);
	}

	@Test
	public void testFetchDocumentsDetailsforZipBatchProcess() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		HashMap loHMArgs = new HashMap();
		loHMArgs.put("statusId", "156");
		List<ExtendedDocument> loDocumentDetailsList = loProcurementDocsZipService
				.fetchDocumentsDetailsforZipBatchProcess(moSession, loHMArgs);
		assertNotNull(loDocumentDetailsList);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchDocumentsDetailsforZipBatchProcessException() throws ApplicationException
	{

		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		HashMap loHMArgs = new HashMap();
		loHMArgs.put("statusId", "156");
		List<ExtendedDocument> loDocumentDetailsList = loProcurementDocsZipService
				.fetchDocumentsDetailsforZipBatchProcess(null, loHMArgs);
	}

	@Test
	public void testUpdateDocumentStatus() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Integer loRowsModified = loProcurementDocsZipService.updateDocumentStatus(moSession, "2889", "r3_org", "2889");
		assertTrue(loRowsModified >= 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateDocumentStatusException() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Integer loRowsModified = loProcurementDocsZipService.updateDocumentStatus(null, "2889", "r3_org", "2889");
	}

	@Test
	public void testUpdateProcStatusForDocs() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Integer loRowsModified = loProcurementDocsZipService.updateProcStatusForDocs(moSession, "2889", "agency_14",
				"7");
		assertTrue(loRowsModified >= 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateProcStatusForDocsException() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Integer loRowsModified = loProcurementDocsZipService.updateProcStatusForDocs(null, "2889", "agency_14",
				"7");
	}

	@Test
	public void testDeleteZipProcurementDocuments() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Integer loRowsModified = loProcurementDocsZipService.deleteZipProcurementDocuments(moSession);
		assertTrue(loRowsModified >= 0);
	}

	@Test
	public void testRequestNewZipFileAction() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSConstants.AS_FILE_NAME_PARAMETER, "R3 dev team's organizations_Award_Documents");
		loInputParam.put(HHSConstants.AS_DOC_STATUS, "155");
		loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, "r3_org");
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, "2889");
		loInputParam.put(HHSConstants.AS_PROC_STATUS, "6");
		loInputParam.put(HHSConstants.AS_USER_ID, "agency_14");
		loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.requestNewZipFileAction(moSession,
				loInputParam);
		assertNotNull(loAwardDocDetails);
	}
	
	@Test(expected = ApplicationException.class)
	public void testRequestNewZipFileAction2() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSConstants.AS_DOC_STATUS, "157");
		loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, "r3_org");
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, "2890");
		loInputParam.put(HHSConstants.AS_USER_ID, "agency_14");
		loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.requestNewZipFileAction(moSession,
				loInputParam);
	}

	@Test
	public void testRequestNewZipFileAction1() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSConstants.AS_DOC_STATUS, "157");
		loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, "r3_org");
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, "2889");
		loInputParam.put(HHSConstants.AS_USER_ID, "agency_14");
		loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.requestNewZipFileAction(moSession,
				loInputParam);
		assertNotNull(loAwardDocDetails);
	}
	
	@Test(expected = ApplicationException.class)
	public void testRequestNewZipFileActionException() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSConstants.AS_FILE_NAME_PARAMETER, "R3 dev team's organizations_Award_Documents");
		loInputParam.put(HHSConstants.AS_DOC_STATUS, "155");
		loInputParam.put(HHSConstants.AS_PROVIDER_ORG_ID, "r3_org");
		loInputParam.put(HHSConstants.PROCUREMENT_ID_KEY, "2889");
		loInputParam.put(HHSConstants.AS_PROC_STATUS, "6");
		loInputParam.put(HHSConstants.AS_USER_ID, "agency_14");
		loInputParam.put(HHSConstants.AS_EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.requestNewZipFileAction(null,
				loInputParam);
	}

	@Test
	public void testFetchAwardDocDetails1() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> asOrgDetailsMap = new HashMap<String, String>();
		Map<String, String> asParametersMap = new HashMap<String, String>();
		asOrgDetailsMap.put(HHSConstants.ORGANIZATION_NAME_CAPS, "R3 dev team's organizations");
		asParametersMap.put(HHSConstants.PROCUREMENT_ID, "2890");
		asParametersMap.put(HHSConstants.USER_ORG_ID, "r3_org");
		asParametersMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, "2890");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.fetchAwardDocDetails(moSession,
				asOrgDetailsMap, asParametersMap);
		assertNotNull(loAwardDocDetails);
	}

	@Test
	public void testFetchAwardDocDetails2() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> asOrgDetailsMap = null;
		Map<String, String> asParametersMap = new HashMap<String, String>();
		asParametersMap.put(HHSConstants.PROCUREMENT_ID, "2890");
		asParametersMap.put(HHSConstants.USER_ORG_ID, "r3_org");
		asParametersMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, "2890");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.fetchAwardDocDetails(moSession,
				asOrgDetailsMap, asParametersMap);
		assertNull(loAwardDocDetails);
	}

	@Test
	public void testFetchAwardDocDetails3() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> asOrgDetailsMap = new HashMap<String, String>();
		Map<String, String> asParametersMap = null;
		asOrgDetailsMap.put(HHSConstants.ORGANIZATION_NAME_CAPS, "R3 dev team's organizations");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.fetchAwardDocDetails(moSession,
				asOrgDetailsMap, asParametersMap);
		assertNull(loAwardDocDetails);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchAwardDocDetailsException() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		Map<String, String> asOrgDetailsMap = new HashMap<String, String>();
		Map<String, String> asParametersMap = new HashMap<String, String>();
		asOrgDetailsMap.put(HHSConstants.ORGANIZATION_NAME_CAPS, "R3 dev team's organizations");
		asParametersMap.put(HHSConstants.PROCUREMENT_ID, "2889");
		asParametersMap.put(HHSConstants.USER_ORG_ID, "r3_org");
		asParametersMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, "2889");
		ExtendedDocument loAwardDocDetails = loProcurementDocsZipService.fetchAwardDocDetails(null,
				asOrgDetailsMap, asParametersMap);
	}
	
	@Test
	public void testFinalZipProcess() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		String lsMsg = loProcurementDocsZipService.finalZipProcess("'zakshi_Award_Documents", "3125_486_accenture");
	}
	
	@Test
	public void testDownloadProcurementDocuments() throws ApplicationException
	{
		ProcurementDocsZipService loProcurementDocsZipService = new ProcurementDocsZipService();
		P8UserSession loFilenetSession = getFileNetSession();
		List<Map<String, String>> loDBDDocList = new ArrayList<Map<String,String>>();
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("DOCUMENT_IDENTIFIER_ID", "296C3ADB-2416-4C44-A3CA-D79A7904A914");
		loDBDDocList.add(loParamMap);
		String lsMsg = loProcurementDocsZipService.downloadProcurementDocuments(loFilenetSession, loDBDDocList, "3125", "accenture", "486", "C:/Zip_Documents_Folder", "/'zakshi_Award_Documents/'zakshi_RFP_Documents", false);
	}


}
