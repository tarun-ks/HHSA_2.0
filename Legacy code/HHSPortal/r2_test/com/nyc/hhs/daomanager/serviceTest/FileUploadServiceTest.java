package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.nyc.hhs.daomanager.service.FileUploadService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class FileUploadServiceTest
{
	private static SqlSession moMyBatisSession = null; //Sqlsession
	private static P8UserSession session = null; // FileNet session
	private FileUploadService moFileUploadService = new FileUploadService();
	private String msDocumentId1= "{4FCB1DA2-1AF6-49DF-92B9-BAAE45FCEEAC}";
	private String msDocumentId2 = "{785CA58E-1B1D-4657-A38E-AD7C4A503011}";
	private String msDocumentId3 = "{8C86FD0E-0D2A-4061-BFB3-C1422609FE5D}";
	private String msDocumentId4 = "{13D0BF6A-BED5-4248-A714-55404E40D08D}";
	private String msOrgId = "test";
	private String msComponentName = "ApplicationDocs";
	private String msContentName = "ObjectsPerPage";
	private String msUserId = "";
	private String moWobNo = "";
	private String msModifiedBy = "city_43";
	private String msDocumentTitle = "";
	private String msNextExpectedDocType = "CHAR500";
	private String msNextStartFiscalMonth = "Apr";
	private String msNextEndFiscalMonth = "May";
	private int msNextStartFiscalYear = 2011;
	private int msNextEndFiscalYear = 2012;
	private String msApplicationId="br_1380527742475";
	private String mstableName = "Document";
	private String msServiceApplicationId = "sr_13813034627701";
	private String msApplicationId1="";
	private String msServiceApplicationId1 = "";
	private String msProviderId1 = "provider";
	private String msDocType = "CHAR500";
	private String msProcStatus = "Approved";
	private String msIsShortFiling = "false";
	private String msDocumentId5 = "{C57CF785-5914-4E1B-9DA7-1392562D169E}";
	private String msDocumentId6 = "{3478BB10-C90A-4FD4-944A-72868DF28183}";
	private String msDocumentId7 = "{13D0BF6A-BED5-4248-A714-55404E40D08D}";
	private String msUser = "283";
	private String msDocumentId8 = "{8A1D7A19-B60B-4333-9A26-198E10D3CB67}";
	private String msDocumentId9 = "{23154558-F0BE-4A7F-942B-3C68B0CAB3A4}";
	private String msDocumentId10 = "{B1C06A99-9468-482B-A60A-94CE0B5CB4B1}";
	

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
   		       session = (P8UserSession)getFileNetSession();
          }
           catch (Exception loEx)
           {
                 lbThrown = true;
                 assertTrue("Exception thrown", lbThrown);
           }
     }
    
     /** returns filenet session
      * 
      * @return
      * @throws ApplicationException
      */
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
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
		"CONNECTION_POINT_NUMBER"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
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

	@Test
	public void testGetLinkedToObjectName1() throws ApplicationException
	{
		String lsLinkedObjName = null;
		lsLinkedObjName = moFileUploadService.getLinkedToObjectName(moMyBatisSession, msDocumentId8);
		assertNotNull(lsLinkedObjName);
	}

	@Test
	public void testGetLinkedToObjectName2() throws ApplicationException
	{
		String lsLinkedObjName = null;
		lsLinkedObjName = moFileUploadService.getLinkedToObjectName(moMyBatisSession, msDocumentId9);
		assertNotNull(lsLinkedObjName);
	}
	
	@Test
	public void testGetLinkedToObjectName3() throws ApplicationException
	{
		String lsLinkedObjName = null;
		lsLinkedObjName = moFileUploadService.getLinkedToObjectName(moMyBatisSession, msDocumentId10 );
		assertNotNull(lsLinkedObjName);
	}


	@Test(expected = ApplicationException.class)
	public void testGetLinkedToObjectNameNegative() throws ApplicationException
	{
		moFileUploadService.getLinkedToObjectName(null, msDocumentId2);
	}

	@Test
	public void testRemoveSMAndFinanceDocuments() throws ApplicationException
	{
		Integer liTotalRowsDeleted = 0;
		liTotalRowsDeleted = moFileUploadService.removeSMAndFinanceDocuments(moMyBatisSession, msDocumentId2);
		assertTrue(liTotalRowsDeleted > 0);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveSMAndFinanceDocumentsNegative() throws ApplicationException
	{
		moFileUploadService.removeSMAndFinanceDocuments(null, msDocumentId2);
	}
	
	@Test
	public void testStatusDocumentIdCount() throws ApplicationException
	{
		int liTotalRowsDeleted = 0;
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId2);
		liTotalRowsDeleted = moFileUploadService.statusDocumentIdCount(moMyBatisSession, loApplicationStatusMap);
		assertNotNull(liTotalRowsDeleted);
	}

	@Test
	public void testDocumentIdCount() throws ApplicationException
	{
		int liTotalRowsDeleted = 0;
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId2);
		liTotalRowsDeleted = moFileUploadService.documentIdCount(moMyBatisSession, loApplicationStatusMap);
		assertNotNull(liTotalRowsDeleted);
	}

	@Test
	public void testCheckServiceStatus() throws ApplicationException
	{
		String lsStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId3);
		lsStatus = moFileUploadService.checkServiceStatus(moMyBatisSession, loApplicationStatusMap);
		assertNull(lsStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckServiceStatusNegative() throws ApplicationException
	{
		HashMap loApplicationStatusMap = new HashMap();
		moFileUploadService.checkServiceStatus(null, loApplicationStatusMap);
	}

	@Test
	public void testCheckSectionStatus() throws ApplicationException
	{
		String lsStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId3);
		lsStatus = moFileUploadService.checkSectionStatus(moMyBatisSession, loApplicationStatusMap);
		assertNull(lsStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testCheckSectionStatusNegative() throws ApplicationException
	{
		HashMap loApplicationStatusMap = new HashMap();
		moFileUploadService.checkSectionStatus(null, loApplicationStatusMap);
	}

	@Test
	public void testUpdateOldDocumentId() throws ApplicationException
	{
		int lsStatus = 0;
		Map<String, String> lsDocumentIds = new HashMap<String, String>();
		lsDocumentIds.put("oldDocumentId", msDocumentId4);
		lsDocumentIds.put("newDocumentId", "{13SADF6A-BED5-4248-A714-55404E40D08D}");
		lsDocumentIds.put("asOrgId", msOrgId);
		lsStatus = moFileUploadService.updateOldDocumentId(lsDocumentIds, moMyBatisSession);
		assertNotNull(lsStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUpdateOldDocumentIdNegative() throws ApplicationException
	{
		Map<String, String> lsDocumentIds = new HashMap<String, String>();
		moFileUploadService.updateOldDocumentId(lsDocumentIds, null);
	}

	@Test
	public void testGetDocumentDetails() throws ApplicationException
	{
		List<HashMap<String, String>> lsDocDetails = new ArrayList<HashMap<String,String>>();
		lsDocDetails = moFileUploadService.getDocumentDetails(moMyBatisSession, msDocumentId4);
		assertNotNull(lsDocDetails);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetDocumentDetailsNegative() throws ApplicationException
	{
		moFileUploadService.getDocumentDetails(null, null);
	}
	
	@Test
	public void testDeleteDueDateReminderEntry() throws ApplicationException
	{
		int liTotalRowsDeleted = 0;
		liTotalRowsDeleted = moFileUploadService.deleteDueDateReminderEntry(moMyBatisSession, msOrgId);
		assertNotNull(liTotalRowsDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testDeleteDueDateReminderEntryNegative() throws ApplicationException
	{
		int liTotalRowsDeleted = 0;
		liTotalRowsDeleted = moFileUploadService.deleteDueDateReminderEntry(null, msOrgId);
		assertNotNull(liTotalRowsDeleted);
	}
	
	@Test
	public void testGetProviderBRAppStatusDetails() throws ApplicationException
	{
		List<String> loOrganizationBrAppStatus = null;
		loOrganizationBrAppStatus = moFileUploadService.getProviderBRAppStatusDetails(moMyBatisSession, msOrgId);
		assertNotNull(loOrganizationBrAppStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProviderBRAppStatusDetailsNegative() throws ApplicationException
	{
		List<String> loOrganizationBrAppStatus = null;
		loOrganizationBrAppStatus = moFileUploadService.getProviderBRAppStatusDetails(null, msOrgId);
		assertNotNull(loOrganizationBrAppStatus);
	}

	@Test
	public void testGetProviderStatusDetailsBatch() throws ApplicationException
	{
		HashMap<String, String> loProviderStatus = new HashMap<String, String>();
		HashMap<String, Object> loParametersMap = new HashMap<String, Object>();
		loParametersMap.put("asOrganizationId", msOrgId);
		loParametersMap.put("asApplicationStatus", "");
		loProviderStatus = moFileUploadService.getProviderStatusDetailsBatch(moMyBatisSession, loParametersMap);
		assertNotNull(loProviderStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusDetailsBatchNegative() throws ApplicationException
	{
		HashMap<String, String> loProviderStatus = new HashMap<String, String>();
		HashMap<String, Object> loParametersMap = new HashMap<String, Object>();
		loParametersMap.put("asOrganizationId", msOrgId);
		loParametersMap.put("asApplicationStatus", "");
		loProviderStatus = moFileUploadService.getProviderStatusDetailsBatch(null, loParametersMap);
		assertNotNull(loProviderStatus);
	}
	
	@Test
	public void testGetProviderStatusDetails() throws ApplicationException
	{
		HashMap<String, String> loProviderStatus = new HashMap<String, String>();
		HashMap<String, String> loParametersMap = new HashMap<String, String>();
		loParametersMap.put("asOrganizationId", msOrgId);
		loParametersMap.put("asApplicationStatus", "");
		loProviderStatus = moFileUploadService.getProviderStatusDetails(moMyBatisSession, loParametersMap);
		assertNull(loProviderStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusDetailsNegative() throws ApplicationException
	{
		HashMap<String, String> loProviderStatus = new HashMap<String, String>();
		HashMap<String, String> loParametersMap = new HashMap<String, String>();
		loParametersMap.put("asOrganizationId", msOrgId);
		loParametersMap.put("asApplicationStatus", "");
		loProviderStatus = moFileUploadService.getProviderStatusDetails(null, loParametersMap);
		assertNotNull(loProviderStatus);
	}
	
	@Test
	public void testGetApplicationStatus() throws ApplicationException
	{
		String lsStatus = "";
		HashMap loParametersMap = new HashMap();
		loParametersMap.put("asOrgId", msOrgId);
		lsStatus = moFileUploadService.getApplicationStatus(moMyBatisSession, loParametersMap);
		assertNotNull(lsStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetApplicationStatusNegative() throws ApplicationException
	{
		String lsStatus = "";
		HashMap loParametersMap = new HashMap();
		loParametersMap.put("asOrgId", msOrgId);
		lsStatus = moFileUploadService.getApplicationStatus(null, loParametersMap);
		assertNotNull(lsStatus);
	}
	
	@Test
	public void testUpdateDeletedDocumentDetails() throws ApplicationException
	{
		List<HashMap<String, String>> loDocumentList = new ArrayList<HashMap<String,String>>();
		int liUpdatedRowsCount = 0;
		liUpdatedRowsCount = moFileUploadService.updateDeletedDocumentDetails(loDocumentList,moMyBatisSession, msDocumentId1);
		assertNotNull(liUpdatedRowsCount);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateDeletedDocumentDetailsNegative() throws ApplicationException
	{
		List<HashMap<String, String>> loDocumentList = new ArrayList<HashMap<String,String>>();
		int liUpdatedRowsCount = 0;
		liUpdatedRowsCount = moFileUploadService.updateDeletedDocumentDetails(loDocumentList,null, msDocumentId1);
		assertNotNull(liUpdatedRowsCount);
	}

	@Test
	public void testUpdateTermaAndConditionFlag() throws ApplicationException
	{
		int liUpdatedUserCount = 0;
		liUpdatedUserCount = moFileUploadService.updateTermaAndConditionFlag(moMyBatisSession);
		assertNotNull(liUpdatedUserCount);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateTermaAndConditionFlagNegative() throws ApplicationException
	{
		int liUpdatedUserCount = 0;
		liUpdatedUserCount = moFileUploadService.updateTermaAndConditionFlag(null);
		assertNotNull(liUpdatedUserCount);
	}
	
	@Test
	public void testGetDueDate() throws ApplicationException
	{
		String lsDueDate = null;
		lsDueDate = moFileUploadService.getDueDate(moMyBatisSession,msProviderId1);
		assertNotNull(lsDueDate);
	}

	@Test(expected = ApplicationException.class)
	public void testGetDueDateNegative() throws ApplicationException
	{
		String lsDueDate = null;
		lsDueDate = moFileUploadService.getDueDate(null,msProviderId1);
		assertNotNull(lsDueDate);
	}
	
	@Test
	public void testGetApplicationSettings() throws ApplicationException
	{
		long llPermittedContent = 0;
		HashMap aoGetContentMap = new HashMap();
		aoGetContentMap.put("componentName", msComponentName);
		aoGetContentMap.put("contentName", msContentName);
		llPermittedContent = moFileUploadService.getApplicationSettings(moMyBatisSession,aoGetContentMap);
		assertNotNull(llPermittedContent);
	}


	@Test(expected = ApplicationException.class)
	public void testGetApplicationSettingsNegative() throws ApplicationException
	{
		long llPermittedContent = 0;
		HashMap aoGetContentMap = new HashMap();
		aoGetContentMap.put("componentName", msComponentName);
		aoGetContentMap.put("contentName", msContentName);
		llPermittedContent = moFileUploadService.getApplicationSettings(null,aoGetContentMap);
		assertNotNull(llPermittedContent);
	}

	@Test
	public void testGetUserName() throws ApplicationException
	{
		String lsUserName = null;
		lsUserName = moFileUploadService.getUserName(msUserId,moMyBatisSession);
		assertNull(lsUserName);
	}

	@Test(expected = ApplicationException.class)
	public void testGetUserNameNegative() throws ApplicationException
	{
		String lsUserName = null;
		lsUserName = moFileUploadService.getUserName(msUserId,null);
		assertNotNull(lsUserName);
	}
	
	@Test
	public void testGetLawType() throws ApplicationException
	{
		String lsLawType = null;
		lsLawType = moFileUploadService.getLawType(moMyBatisSession,msOrgId);
		assertNull(lsLawType);
	}

	@Test(expected = ApplicationException.class)
	public void testGetLawTypeNegative() throws ApplicationException
	{
		String lsLawType = null;
		lsLawType = moFileUploadService.getLawType(null,msOrgId);
		assertNotNull(lsLawType);
	}
	
	@Test
	public void testUpdateProcStatusForTerminatedWobNo() throws ApplicationException
	{
		Boolean loProcStatus = false;
		loProcStatus = moFileUploadService.updateProcStatusForTerminatedWobNo(moMyBatisSession,moWobNo,msUserId);
		assertTrue(loProcStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateProcStatusForTerminatedWobNoNegative() throws ApplicationException
	{
		Boolean loProcStatus = false;
		loProcStatus = moFileUploadService.updateProcStatusForTerminatedWobNo(null,moWobNo,msUserId);
		assertFalse(loProcStatus);
	}

	@Test
	public void testGetDocTypeAndWorkFlowID() throws ApplicationException
	{
		HashMap<String, Object> loHashMap = null;
		loHashMap = moFileUploadService.getDocTypeAndWorkFlowID(moMyBatisSession,msProviderId1);
		assertNotNull(loHashMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetDocTypeAndWorkFlowIDNegative() throws ApplicationException
	{
		HashMap<String, Object> loHashMap = null;
		loHashMap = moFileUploadService.getDocTypeAndWorkFlowID(null,msProviderId1);
		assertNotNull(loHashMap);
	}

	@Test
	public void testUpdateDocModifiedInfo() throws ApplicationException
	{
		Boolean loDocumentUpdateStatus = false;
		HashMap loModifiedInfoMap = new HashMap();
		loModifiedInfoMap.put("modifiedDate", new Date());
		loModifiedInfoMap.put("modifiedBy", msModifiedBy);
		loModifiedInfoMap.put("DocumentTitle", msDocumentTitle);
		loModifiedInfoMap.put("documentId", msDocumentId1);
		loDocumentUpdateStatus = moFileUploadService.updateDocModifiedInfo(moMyBatisSession,loModifiedInfoMap);
		assertTrue(loDocumentUpdateStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateDocModifiedInfoNegative() throws ApplicationException
	{
		Boolean loDocumentUpdateStatus = false;
		HashMap loModifiedInfoMap = new HashMap();
		loModifiedInfoMap.put("modifiedDate", new Date());
		loModifiedInfoMap.put("modifiedBy", msModifiedBy);
		loModifiedInfoMap.put("DocumentTitle", msDocumentTitle);
		loModifiedInfoMap.put("documentId", msDocumentId1);
		loDocumentUpdateStatus = moFileUploadService.updateDocModifiedInfo(null,loModifiedInfoMap);
		assertFalse(loDocumentUpdateStatus);
	}

	@Test
	public void testCheckApplicationStatus1() throws ApplicationException
	{
		String lsAppStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId5);
		lsAppStatus = moFileUploadService.checkApplicationStatus(moMyBatisSession,loApplicationStatusMap);
		assertNotNull(lsAppStatus);
	}

	@Test
	public void testCheckApplicationStatus2() throws ApplicationException
	{
		String lsAppStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId6);
		lsAppStatus = moFileUploadService.checkApplicationStatus(moMyBatisSession,loApplicationStatusMap);
		assertNotNull(lsAppStatus);
	}

	@Test
	public void testCheckApplicationStatus3() throws ApplicationException
	{
		String lsAppStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", msDocumentId7);
		lsAppStatus = moFileUploadService.checkApplicationStatus(moMyBatisSession,loApplicationStatusMap);
		assertNotNull(lsAppStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckApplicationStatusNegative() throws ApplicationException
	{
		String lsAppStatus = "";
		HashMap loApplicationStatusMap = new HashMap();
		loApplicationStatusMap.put("documentId", null);
		lsAppStatus = moFileUploadService.checkApplicationStatus(null,loApplicationStatusMap);
		assertNotNull(lsAppStatus);
	}

	@Test
	public void testUpdateDocLapsingMaster() throws ApplicationException
	{
		Boolean lbStatus = false;
		lbStatus = moFileUploadService.updateDocLapsingMaster(moMyBatisSession,msProviderId1);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateDocLapsingMasterNegative() throws ApplicationException
	{
		Boolean lbStatus = false;
		lbStatus = moFileUploadService.updateDocLapsingMaster(null,null);
		assertTrue(lbStatus);
	}

	@Test
	public void testDeleteShortFilingEntry() throws ApplicationException
	{
		Boolean lbStatus = false;
		lbStatus = moFileUploadService.deleteShortFilingEntry(moMyBatisSession,msProviderId1);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testDeleteShortFilingEntryNegative() throws ApplicationException
	{
		Boolean lbStatus = false;
		lbStatus = moFileUploadService.deleteShortFilingEntry(null,null);
		assertFalse(lbStatus);
	}
	
	@Test
	public void testUpdateDocLapsingMasterDueDate() throws ApplicationException
	{
		Integer liStatus = 0;
		HashMap loDocLapsingMasterMap = new HashMap();
		loDocLapsingMasterMap.put("dueDate", new Date(new Date().getMonth()+1));
		loDocLapsingMasterMap.put("nextExpectedDocType", msNextExpectedDocType);
		loDocLapsingMasterMap.put("nextStartFiscalMonth", msNextStartFiscalMonth);
		loDocLapsingMasterMap.put("nextEndFiscalMonth", msNextEndFiscalMonth);
		loDocLapsingMasterMap.put("nextStartFiscalYear", msNextStartFiscalYear);
		loDocLapsingMasterMap.put("nextEndFiscalYear", msNextEndFiscalYear);
		loDocLapsingMasterMap.put("modifiedBy", msModifiedBy);
		loDocLapsingMasterMap.put("providerId", msProviderId1);
		liStatus = moFileUploadService.updateDocLapsingMasterDueDate(moMyBatisSession,loDocLapsingMasterMap);
		assertNotNull(liStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateDocLapsingMasterDueDateNegative() throws ApplicationException
	{
		Integer liStatus = 0;
		HashMap loDocLapsingMasterMap = new HashMap();
		loDocLapsingMasterMap.put("dueDate", new Date(new Date().getMonth()+1));
		loDocLapsingMasterMap.put("nextExpectedDocType", msNextExpectedDocType);
		loDocLapsingMasterMap.put("nextStartFiscalMonth", msNextStartFiscalMonth);
		loDocLapsingMasterMap.put("nextEndFiscalMonth", msNextEndFiscalMonth);
		loDocLapsingMasterMap.put("nextStartFiscalYear", msNextStartFiscalYear);
		loDocLapsingMasterMap.put("nextEndFiscalYear", msNextEndFiscalYear);
		loDocLapsingMasterMap.put("modifiedBy", msModifiedBy);
		loDocLapsingMasterMap.put("providerId", msProviderId1);
		liStatus = moFileUploadService.updateDocLapsingMasterDueDate(null,loDocLapsingMasterMap);
		assertNotNull(liStatus);
	}

	@Test
	public void testGetAccountingPeriodForProviderFromOrg() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getAccountingPeriodForProviderFromOrg(moMyBatisSession,msProviderId1);
		assertNotNull(loProviderList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetAccountingPeriodForProviderFromOrgNegative() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getAccountingPeriodForProviderFromOrg(null,null);
		assertNotNull(loProviderList);
	}

	@Test
	public void testGetEndYearForChar500() throws ApplicationException
	{
		Integer liEndYear = 0;
		liEndYear = (Integer)moFileUploadService.getEndYearForChar500(moMyBatisSession,msProviderId1);
		assertNotNull(liEndYear);
	}

	@Test(expected = ApplicationException.class)
	public void testGetEndYearForChar500Negative() throws ApplicationException
	{
		Integer liEndYear = 0;
		liEndYear = moFileUploadService.getEndYearForChar500(null,null);
		assertNotNull(liEndYear);
	}
	
	@Test
	public void testCheckExtension() throws ApplicationException
	{
		String lsExtension = null;
		lsExtension = moFileUploadService.checkExtension(moMyBatisSession,"");
		assertNull(lsExtension);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckExtensionNegative() throws ApplicationException
	{
		String lsExtension = null;
		lsExtension = moFileUploadService.checkExtension(null,null);
		assertNotNull(lsExtension);
	}

	@Test
	public void testInsertDocLapsingTrans() throws ApplicationException
	{
		Integer liStatus = 0;
		HashMap loInsertPropMap = new HashMap();
		loInsertPropMap.put("providerId", msProviderId1);
		loInsertPropMap.put("docType", msDocType);
		loInsertPropMap.put("user", msModifiedBy);
		loInsertPropMap.put("operationTime",new Date());
		loInsertPropMap.put("nextStartFiscalMonth", msNextStartFiscalMonth);
		loInsertPropMap.put("nextEndFiscalMonth", msNextEndFiscalMonth);
		loInsertPropMap.put("approvedForStartYear", msNextStartFiscalYear);
		loInsertPropMap.put("approvedForEndYear", msNextEndFiscalYear);
		loInsertPropMap.put("documentId", msDocumentId1);
		loInsertPropMap.put("workflowId", "");
		loInsertPropMap.put("procStatus", msProcStatus);
		loInsertPropMap.put("isShortFiling", msIsShortFiling);
		liStatus = moFileUploadService.insertDocLapsingTrans(moMyBatisSession,loInsertPropMap);
		assertNotNull(liStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertDocLapsingTransNegative() throws ApplicationException
	{
		Integer liStatus = 0;
		HashMap loInsertPropMap = new HashMap();
		loInsertPropMap.put("providerId", msProviderId1);
		loInsertPropMap.put("docType", msDocType);
		loInsertPropMap.put("user", msModifiedBy);
		loInsertPropMap.put("operationTime",null);
		loInsertPropMap.put("nextStartFiscalMonth", msNextStartFiscalMonth);
		loInsertPropMap.put("nextEndFiscalMonth", msNextEndFiscalMonth);
		loInsertPropMap.put("approvedForStartYear", msNextStartFiscalYear);
		loInsertPropMap.put("approvedForEndYear", msNextEndFiscalYear);
		loInsertPropMap.put("documentId", msDocumentId1);
		loInsertPropMap.put("workflowId", "");
		loInsertPropMap.put("procStatus", msProcStatus);
		loInsertPropMap.put("isShortFiling", msIsShortFiling);
		liStatus = moFileUploadService.insertDocLapsingTrans(null,loInsertPropMap);
		assertNotNull(liStatus);
	}

	@Test
	public void testGetProviderListAjaxCall() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getProviderListAjaxCall(moMyBatisSession);
		assertNotNull(loProviderList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProviderListAjaxCallNegative() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getProviderListAjaxCall(null);
		assertNotNull(loProviderList);
	}

	@Test
	public void testGetNYCAgencyList() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getNYCAgencyList(moMyBatisSession);
		assertNotNull(loProviderList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetNYCAgencyListNegative() throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		loProviderList = moFileUploadService.getNYCAgencyList(null);
		assertNotNull(loProviderList);
	}

	@Test
	public void testGetDocumentIds() throws ApplicationException
	{
		List<String> loDocIdList = new ArrayList<String>();
		List<Document> loResultList = null;
		loResultList = moFileUploadService.getDocumentDetailsServiceSummary(msServiceApplicationId,msOrgId,msApplicationId,moMyBatisSession);
		loDocIdList = moFileUploadService.getDocumentIds(loResultList);
		assertNotNull(loDocIdList);
	}

	@Test
	public void testGetAccountingPeriodForProvider() throws ApplicationException
	{
		List<ProviderBean> loResultList = null;
		loResultList = moFileUploadService.getAccountingPeriodForProvider(moMyBatisSession,msProviderId1 );
		assertNotNull(loResultList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetAccountingPeriodForProviderNegative() throws ApplicationException
	{
		List<ProviderBean> loResultList = null;
		loResultList = moFileUploadService.getAccountingPeriodForProvider(null,null);
		assertNotNull(loResultList);
	}

	@Test
	public void testGetFormInformation() throws ApplicationException
	{
		HashMap<String, Object> loFormInfoMap = null;
		loFormInfoMap = moFileUploadService.getFormInformation(msApplicationId,msOrgId,mstableName,moMyBatisSession);
		assertNotNull(loFormInfoMap);
	}

	@Test
	public void testGetDocumentDetailsServiceSummary1() throws ApplicationException
	{
		List<Document> loResultList = null;
		loResultList = moFileUploadService.getDocumentDetailsServiceSummary(msServiceApplicationId,msOrgId,msApplicationId,moMyBatisSession);
		assertNotNull(loResultList);
	}

	@Test
	public void testGetDocumentDetailsServiceSummary2() throws ApplicationException
	{
		List<Document> loResultList = null;
		loResultList = moFileUploadService.getDocumentDetailsServiceSummary(msServiceApplicationId1,msOrgId,msApplicationId1,moMyBatisSession);
		assertNotNull(loResultList);
	}

	@Test
	public void testUpdatefileUploadDetails1() throws ApplicationException
	{
		boolean lbInsertStatus = true;
		lbInsertStatus = moFileUploadService.updatefileUploadDetails1("br_1397560115436", "{C0C834AE-68A8-4C7E-99EE-AF7365623531}", "Audit",
				"A-133", "Filings", "0", msProviderId1, "OverWriteTest",
				msUser, new Date().toString(), msUser, new Date().toString(),
				null, "filings", null, moMyBatisSession);
		assertTrue(lbInsertStatus);
	}

	@Test
	public void testSetDocIdWithSameDocName() throws ApplicationException
	{
		boolean lbInsertStatus = true;
		lbInsertStatus = moFileUploadService.setDocIdWithSameDocName("br_1397560115436", "{C0C834AE-68A8-4C7E-99EE-AF7365623531}", "Audit",
				"A-133", "Filings", "0", msProviderId1, "OverWriteTest",
				null, "filings", null, moMyBatisSession);
		assertTrue(lbInsertStatus);
	}
	
	@Test
	public void testCheckForDocId() throws ApplicationException
	{
		String lsGetdocIdForDocType = "";
		lsGetdocIdForDocType = moFileUploadService.checkForDocId("br_1397560115436", null, "{C0C834AE-68A8-4C7E-99EE-AF7365623531}", "Audit",
				"A-133", msProviderId1, "OverWriteTest",
				"filings", null, session, moMyBatisSession);
		assertNotNull(lsGetdocIdForDocType);
	}

	@Test
	public void testGetDocumentDetails1() throws ApplicationException
	{
		List<Document> loDocumentList= null;
		HashMap loFormInfo = new HashMap();
		loFormInfo.put("FORM_NAME", "Filings");
		loFormInfo.put("FORM_VERSION", "0");
		loDocumentList = moFileUploadService.getDocumentDetails("br_1397560115436",loFormInfo,msProviderId1,moMyBatisSession);
		assertNotNull(loDocumentList);
	}

}
