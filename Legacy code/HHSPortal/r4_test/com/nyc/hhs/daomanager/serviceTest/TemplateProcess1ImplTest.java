package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.batch.bulkupload.BulkUploadContractInfo;
import com.batch.bulkupload.BulkUploadContractService;
import com.batch.bulkupload.BulkUploadFileInformation;
import com.batch.bulkupload.TemplateProcess1Impl;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class TemplateProcess1ImplTest
{

	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session
	static TemplateProcess1Impl loTemplateProcess = null;
	String filePath = HHSConstants.EMPTY_STRING;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		loTemplateProcess = new TemplateProcess1Impl();
		loTemplateProcess.setMoUserSession((P8UserSession) getFileNetSession());

		setExcelHeader();
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

	/**
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
	}

	@Test
	public void testValidateMedatoryFieldContractType() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(1, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Required field is missing in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_COTRACT_TYPE,
				lsErrorMessage);
	}

	@Test
	public void testValidateMedatoryFieldEpin() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(2, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Required field is missing in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN, lsErrorMessage);
	}

	@Test
	public void testValidateMedatoryFieldAgency() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(3, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Required field is missing in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY, lsErrorMessage);
	}

	@Test
	public void testValidateMedatoryFieldProgram() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(4, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Required field is missing in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME,
				lsErrorMessage);
	}

	@Test
	public void testValidateEpinNegative1() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.validateEpin((BulkUploadContractInfo) getOneRowFromExcel(5, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("EPIN associated with other contract", lsErrorMessage);
	}

	@Test
	public void testValidateEpinNegative2() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.validateEpin((BulkUploadContractInfo) getOneRowFromExcel(6, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("EPIN does not exist", lsErrorMessage);
	}

	@Test
	public void testValidateEpinNegative3() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.validateEpin((BulkUploadContractInfo) getOneRowFromExcel(2, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("EPIN does not exist", lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeContractType() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";

		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(7, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_COTRACT_TYPE + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeEpin() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(8, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeAgency() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(9, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY + " is incorrect", lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeProgramName() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(10, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeContractTitle() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(11, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeContractValue() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(12, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeContractStartDate() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(13, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE + " is incorrect",
				lsErrorMessage);
	}

	@Test
	public void testValidateFieldTypeContractEndDate() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(14, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Type in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE + " is incorrect",
				lsErrorMessage);
	}

	/*
	 * @Test public void testValidateFieldLengthContractType() throws
	 * ApplicationException { filePath = "C:\\test_data\\Test1.xlsx";
	 * loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo)
	 * getOneRowFromExcel(15, filePath)); String lsErrorMessage =
	 * loTemplateProcess.getErrorMessage(); assertEquals("Field Length in " +
	 * HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_COTRACT_TYPE +
	 * " is not within the acceptable length", lsErrorMessage); }
	 */
	@Test
	public void testValidateFieldLengthEpin() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(16, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractAgency() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(17, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractProgramName() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		// BulkUploadContractInfo
		// loBulkUploadContractInfo=(BulkUploadContractInfo)
		// getOneRowFromExcel(18, filePath);
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(18, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractTitle() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(19, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractValue() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(20, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractStartDate() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(21, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateFieldLengthContractEndDate() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.checkMandatoryFields((BulkUploadContractInfo) getOneRowFromExcel(22, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Field Length in " + HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE
				+ " is not within the acceptable length", lsErrorMessage);
	}

	@Test
	public void testValidateDateNegative2() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.validateDate((BulkUploadContractInfo) getOneRowFromExcel(23, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Contract Start Date cannot be greater than the Contract End Date", lsErrorMessage);
	}

	@Test
	public void testValidateReviewLevel() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		boolean test = loTemplateProcess
				.validateReviewLevelForCertificationOfFunds((BulkUploadContractInfo) getOneRowFromExcel(3, filePath));
		assertFalse(test);
	}

	@Test
	public void testValidateProvider() throws ApplicationException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		loTemplateProcess.validateProvider((BulkUploadContractInfo) getOneRowFromExcel(22, filePath));
		String lsErrorMessage = loTemplateProcess.getErrorMessage();
		assertEquals("Provider does not exist in the system", lsErrorMessage);
	}

	@Test
	public void testGetBulkUploadFileInformation() throws ApplicationException, IOException
	{

		ArrayList<BulkUploadFileInformation> list = null;
		BulkUploadContractService loBulkUploadContractService = new BulkUploadContractService();
		list = loBulkUploadContractService.getBulkUploadFileInformation(moSession);
		assertFalse(list.isEmpty());

	}

	@Test
	public void testInsertDocoumentPropertiesInDB() throws ApplicationException, IOException
	{

		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, "xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "Test");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "city_43");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "city_43");
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.insertBulkuploadDocumentInDB(moSession, loParamMap,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE77C}");
		assertTrue(recordInserted);
	}

	@Test
	public void testUpdateDocStatusInDB() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.updateDocumentStatusInDB(moSession,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE76C}", "144");
		assertTrue(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsOne() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(28, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertTrue(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsTwo() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(29, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertTrue(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsThree() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(30, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertFalse(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsFour() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(31, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertFalse(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsFive() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(42, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertFalse(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsSix() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(43, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertFalse(recordInserted);
	}

	@Test
	public void testProcessOptionalFieldsSeven() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(44, filePath);
		boolean recordInserted = loTemplateProcess.processOptionalFields(loBulkUploadContractInfo);
		assertFalse(recordInserted);
	}

	@Test
	public void testProcess() throws ApplicationException, IOException
	{

		FileInputStream loFileInputStream = new FileInputStream("C:\\test_data\\Test2.xlsx");
		loTemplateProcess.setDataFileObj(loFileInputStream);
		loTemplateProcess.setFileUploadedByUser("city_43");
		loTemplateProcess.setFileUploadId("2002");
		String lsProcessOut = loTemplateProcess.processData();

	}

	@Test(expected = ApplicationException.class)
	public void testProcessOne() throws ApplicationException, IOException
	{

		FileInputStream loFileInputStream = new FileInputStream("C:\\test_data\\Test2.xlsx");
		loTemplateProcess.setDataFileObj(loFileInputStream);
		loTemplateProcess.setFileUploadedByUser("city_43");
		String lsProcessOut = loTemplateProcess.processData();

	}

	@Test
	public void testSaveContract() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(39, filePath);
		loTemplateProcess.setFileUploadedByUser("city_43");
		loBulkUploadContractInfo.setFileUploadId("2002");
		loBulkUploadContractInfo.setUploadFlag("1");
		boolean recordSaved = loTemplateProcess.saveContract(loBulkUploadContractInfo);
		assertTrue(recordSaved);

	}

	@Test(expected = ApplicationException.class)
	public void testSaveContractTwo() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(2, filePath);
		loTemplateProcess.setFileUploadedByUser("city_43");
		// loBulkUploadContractInfo.setFileUploadId("950");
		loBulkUploadContractInfo.setUploadFlag("1");
		boolean recordSaved = loTemplateProcess.saveContract(loBulkUploadContractInfo);
	}

	@Test(expected = ApplicationException.class)
	public void testSaveContractThree() throws ApplicationException, IOException
	{
		filePath = "C:\\test_data\\Test1.xlsx";
		BulkUploadContractInfo loBulkUploadContractInfo = (BulkUploadContractInfo) getOneRowFromExcel(41, filePath);
		// loTemplateProcess.setFileUploadedByUser("city_43");
		// loBulkUploadContractInfo.setFileUploadId("950");
		// loBulkUploadContractInfo.setUploadFlag("1");
		boolean recordSaved = loTemplateProcess.saveContract(loBulkUploadContractInfo);
		assertTrue(recordSaved);
	}

	@Test
	public void testValidateSpreadSheet() throws ApplicationException, IOException
	{
		loTemplateProcess.setDataFileObj(new FileInputStream("C:\\test_data\\Test1.xlsx"));
		loTemplateProcess.setTemplateFileObj(new FileInputStream("C:\\test_data\\Test3.xlsx"));
		assertTrue(loTemplateProcess.validateBulkContractSpreadsheet());
	}

	@Test(expected = ApplicationException.class)
	public void testValidateSpreadSheetOne() throws ApplicationException, IOException
	{
		loTemplateProcess.setDataFileObj(new FileInputStream("C:\\test_data\\Test1.xlsx"));
		loTemplateProcess.setTemplateFileObj(new FileInputStream("C:\\test_data\\Test4.xlsx"));
		assertTrue(loTemplateProcess.validateBulkContractSpreadsheet());
	}

	public BulkUploadContractInfo getOneRowFromExcel(int aiRowNumber, String asfilePath)
	{

		XSSFWorkbook loWorkbook;
		FileInputStream loFileInputStream = null;
		BulkUploadContractInfo loBulkUploadContractInfo = null;
		try
		{

			loFileInputStream = new FileInputStream(asfilePath);
			// loFileInputStream = new
			// FileInputStream("C:\\test_data\\Test1.xlsx");
			loWorkbook = new XSSFWorkbook(loFileInputStream);
			XSSFSheet loSheet = loWorkbook.getSheetAt(0);
			Row loRow = loSheet.getRow(aiRowNumber);
			ArrayList<String> loRecordExcelRow = new ArrayList<String>();

			for (int liColumn = 0; liColumn <= loRow.getLastCellNum(); liColumn++)
			{

				Cell loCell = loRow.getCell(liColumn, loRow.RETURN_BLANK_AS_NULL);
				if (loCell == null)
				{
					loRecordExcelRow.add(HHSConstants.EMPTY_STRING);
				}
				else
				{
					loCell.setCellType(Cell.CELL_TYPE_STRING);
					String lsCellElement = loCell.getStringCellValue();
					loRecordExcelRow.add(lsCellElement);
				}
			}
			loBulkUploadContractInfo = getExcelDataBean(loRecordExcelRow);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return loBulkUploadContractInfo;
	}

	private BulkUploadContractInfo getExcelDataBean(ArrayList<String> record)
	{
		BulkUploadContractInfo loBulkUploadContractInfo = new BulkUploadContractInfo();
		if ((String) record.get(0) == null)
		{
			loBulkUploadContractInfo.setContractType(HHSConstants.EMPTY_STRING);
		}
		else
		{
			loBulkUploadContractInfo.setContractType((String) record.get(0));
		}
		loBulkUploadContractInfo.setEpin((String) record.get(1));
		loBulkUploadContractInfo.setAgency((String) record.get(2));
		loBulkUploadContractInfo.setAccProgramName((String) record.get(3));
		loBulkUploadContractInfo.setContractTitle((String) record.get(4));
		loBulkUploadContractInfo.setContractValue((String) record.get(5));
		loBulkUploadContractInfo.setContractStartDate((String) record.get(6));
		loBulkUploadContractInfo.setContractEndDate((String) record.get(7));
		loBulkUploadContractInfo.setCreatedByUserId(HHSConstants.BULK_UPLOAD_SYSTEM_USER);
		loBulkUploadContractInfo.setModifiedByUserId(HHSConstants.BULK_UPLOAD_SYSTEM_USER);
		return loBulkUploadContractInfo;
	}

	private static void setExcelHeader()
	{
		HashMap<String, String> loExcelHeader = new HashMap<String, String>();

		loExcelHeader.put("Contract Type", "Contract Type");
		loExcelHeader.put("Award EPIN", "Award EPIN");
		loExcelHeader.put("Agency", "Agency");
		loExcelHeader.put("Accelerator Program Name", "Accelerator Program Name");
		loExcelHeader.put("Contract Title", "Contract Title");
		loExcelHeader.put("Contract Value", "Contract Value");
		loExcelHeader.put("Contract Start Date", "Contract Start Date");
		loExcelHeader.put("Contract End Date", "Contract End Date");
		loTemplateProcess.setExcelHeaderMap(loExcelHeader);

	}

}
