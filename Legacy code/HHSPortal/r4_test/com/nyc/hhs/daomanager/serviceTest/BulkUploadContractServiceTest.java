package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.batch.bulkupload.BulkUploadContractInfo;
import com.batch.bulkupload.BulkUploadContractService;
import com.batch.bulkupload.BulkUploadFileInformation;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class BulkUploadContractServiceTest
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();

		loadTransactionXml();
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

	@Test
	public void testGetBulkUploadFileInformation() throws ApplicationException, IOException
	{

		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		list = loBulkUploadContractService.getBulkUploadFileInformation(moSession);
		assertFalse(list.isEmpty());

	}

	@Test(expected = ApplicationException.class)
	public void testGetBulkUploadFileInformationOne() throws ApplicationException, IOException
	{

		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		list = loBulkUploadContractService.getBulkUploadFileInformation(null);
		assertFalse(list.isEmpty());

	}

	@Test
	public void testSaveBulkUploadStatusByRecord() throws ApplicationException, IOException
	{
		BulkUploadContractInfo loBulkUploadContractInfo = new BulkUploadContractInfo();
		loBulkUploadContractInfo.setFileUploadId("1999");
		loBulkUploadContractInfo.setRowNumber("50");
		loBulkUploadContractInfo.setContractType("Inflight");
		loBulkUploadContractInfo.setEpin("85012I0025001");
		loBulkUploadContractInfo.setAgency("DOC");
		loBulkUploadContractInfo.setAccProgramName("Test Program");
		loBulkUploadContractInfo.setContractTitle("test contract");
		loBulkUploadContractInfo.setContractValue("5000");
		loBulkUploadContractInfo.setContractStartDate("05/05/2014");
		loBulkUploadContractInfo.setContractEndDate("05/05/2014");
		loBulkUploadContractInfo.setUploadFlag("1");
		loBulkUploadContractInfo.setErrorMessage("The pin is already associated with some other contract");
		loBulkUploadContractInfo.setCreatedByUserId("system");
		loBulkUploadContractInfo.setModifiedByUserId("system");
		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		assertTrue(loBulkUploadContractService.saveBulkUploadStatusByRecord(loBulkUploadContractInfo, moSession));

	}

	@Test(expected = ApplicationException.class)
	public void testSaveBulkUploadStatusByRecordNegative() throws ApplicationException, IOException
	{
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		assertTrue(loBulkUploadContractService.saveBulkUploadStatusByRecord(null, moSession));

	}

	@Test
	public void testSaveBulkUploadFileToFileSystem() throws ApplicationException, IOException
	{
		InputStream loInputStream = new FileInputStream("C:\\test_data\\test_template.xlsx");
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, "xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "test_template.xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "system");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "system");
		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		String lsFileSaved = loBulkUploadContractService.saveBulkUploadFileToFileSystem(loInputStream, loParamMap);

		assertFalse(lsFileSaved.equals(""));

	}

	@Test(expected = ApplicationException.class)
	public void testSaveBulkUploadFileToFileSystemOne() throws ApplicationException, IOException
	{
		InputStream loInputStream = new FileInputStream("C:\\test_data\\test_template.xlsx");
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		String lsFileSaved = loBulkUploadContractService.saveBulkUploadFileToFileSystem(loInputStream, loParamMap);

	}

	@Test(expected = ApplicationException.class)
	public void testSaveBulkUploadFileToFileSystemTwo() throws ApplicationException, IOException
	{
		InputStream loInputStream = new FileInputStream("C:\\test_data\\test.txt");
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, "xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "test_template.xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "system");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "system");
		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		String lsFileSaved = loBulkUploadContractService.saveBulkUploadFileToFileSystem(loInputStream, null);

	}

	@Test
	public void testGetTotalContracts() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		int liRecords = loBulkUploadContractService.getTotalContracts(moSession, "1999");
		assertTrue(liRecords > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetTotalContractsOne() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		int liRecords = loBulkUploadContractService.getTotalContracts(moSession, null);
		assertTrue(liRecords > 0);

	}

	@Test(expected = ApplicationException.class)
	public void testGetTotalContractsTwo() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		int liRecords = loBulkUploadContractService.getTotalContracts(null, null);
		assertTrue(liRecords > 0);

	}

	@Test
	public void testGetErrorList() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		List<BulkUploadContractInfo> loErrorList = loBulkUploadContractService.getErrorList(moSession, "1999");

		assertTrue(loErrorList != null);

	}

	@Test(expected = ApplicationException.class)
	public void testGetErrorListOne() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		List<BulkUploadContractInfo> loErrorList = loBulkUploadContractService.getErrorList(moSession, null);

		assertTrue(loErrorList != null);

	}

	@Test(expected = ApplicationException.class)
	public void testGetErrorListtwo() throws ApplicationException, IOException
	{

		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		List<BulkUploadContractInfo> loErrorList = loBulkUploadContractService.getErrorList(null, null);

		assertTrue(loErrorList != null);

	}

	private static P8UserSession getFileNetSession() throws ApplicationException
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
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	private static void loadTransactionXml() throws ApplicationException
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheNotificationObject3 = XMLUtil.getDomObj(BulkUploadContractServiceTest.class
					.getResourceAsStream("/com/nyc/hhs/config/TransactionConfigR2.xml"));
			loCacheManager.putCacheObject(HHSConstants.TRANSACTION_ELEMENT, loCacheNotificationObject3);

			Object loCacheNotificationObject4 = XMLUtil.getDomObj(BulkUploadContractServiceTest.class
					.getResourceAsStream("/com/nyc/hhs/config/TransactionConfig.xml"));
			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheNotificationObject4);
			synchronized (BulkUploadContractServiceTest.class)
			{
				loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING,
						HHSUtil.getApplicationSettingsBulk());
			}
		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Error Occured while loading Transaction Xml", loEx);
		}
	}

}
