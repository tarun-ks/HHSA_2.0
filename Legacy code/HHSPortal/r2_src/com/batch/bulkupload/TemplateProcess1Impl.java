package com.batch.bulkupload;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This file is the implementing class to create contracts in bulk. It will
 * validate the file structure, mandatory filed and generate contracts.
 * 
 */
public class TemplateProcess1Impl implements ProcessBulkUploadContracts
{

	private static final LogInfo LOG_OBJECT = new LogInfo(TemplateProcess1Impl.class);

	Object moTemplateFileObj = null;
	Object moDataFileObj = null;
	P8UserSession moUserSession = null;
	AdditionalFiledProcessing moProcessAdditionalFields;
	//String errorMessage = HHSConstants.EMPTY_STRING;
	String fileUploadedByUser = HHSConstants.EMPTY_STRING;
	String fileUploadId = HHSConstants.EMPTY_STRING;
	HashMap<String, String> excelHeaderMap = null;
	int MAX_MESSAGE_LENGTH=2000;
	
	StringBuffer stbErrorMessage = new StringBuffer();

	@Override
	public void setMoUserSession(P8UserSession moUserSession)
	{
		this.moUserSession = moUserSession;
	}

	@Override
	public void setTemplateFileObj(Object aoTemplateFileObj)
	{
		this.moTemplateFileObj = aoTemplateFileObj;
	}

	/*
	 * This Method will set the data file object as a member variable.
	 */
	@Override
	public void setDataFileObj(Object aoDataFileObj)
	{
		this.moDataFileObj = aoDataFileObj;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		//return errorMessage;
		String tmpStr= stbErrorMessage.toString();
		if(tmpStr.trim().length()>MAX_MESSAGE_LENGTH){
			tmpStr=tmpStr.substring(0, MAX_MESSAGE_LENGTH);
		}
		return tmpStr;
	}

	/**
	 * @param excelHeaderMap the excelHeaderMap to set
	 */
	public void setExcelHeaderMap(HashMap<String, String> excelHeaderMap)
	{
		this.excelHeaderMap = excelHeaderMap;
	}

	/**
	 * @return the fileUploadId
	 */
	public String getFileUploadId()
	{
		return fileUploadId;
	}

	/**
	 * @param fileUploadId the fileUploadId to set
	 */
	@Override
	public void setFileUploadId(String fileUploadId)
	{
		this.fileUploadId = fileUploadId;
	}

	/**
	 * @param fileUploadedByUser the fileUploadedByUser to set
	 */
	@Override
	public void setFileUploadedByUser(String fileUploadedByUser)
	{
		this.fileUploadedByUser = fileUploadedByUser;
	}

	/**
	 * @return the fileUploadedByUser
	 */
	public String getFileUploadedByUser()
	{
		return fileUploadedByUser;
	}

	public static int errorRecords;

	/**
	 * @param errorMessage the errorMessage to set
	 */
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public void setErrorMessage(String errorMessage)
	{
		stbErrorMessage.append(errorMessage).append("<br>");
		//this.errorMessage = errorMessage;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	/*
	 * This function will validate uploaded datafile's structure (moDataFileObj)
	 * with respective template structure (moTemplateFileObj) from FileNet. this
	 * will return false if any column added/deleted or sequence of column
	 * changes in template
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean validateBulkContractSpreadsheet() throws ApplicationException
	{
		boolean lbValidSheet = false;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);

		String lsBulkTemplateSheetNameKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_TEMPLATE_SHEET_NAME;
		String lsBulkTemplateSheetName = loApplicationSettingMap.get(lsBulkTemplateSheetNameKey);
		String lsBulkUploadFirstRowKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_EXCEL_FIRST_ROW;
		String lsBulkUploadFirstColKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_EXCEL_FIRST_COLUMN;
		String lsBulkFirstRow = loApplicationSettingMap.get(lsBulkUploadFirstRowKey);
		String lsBulkFirstCol = loApplicationSettingMap.get(lsBulkUploadFirstColKey);
		int liFirstRow = Integer.parseInt(lsBulkFirstRow);
		int liFirstCol = Integer.parseInt(lsBulkFirstCol);

		ArrayList<String> loDataFileHeaderList = null;
		ArrayList<String> loTemplateHeaderList = null;
		try
		{
			loTemplateHeaderList = setExcelHeaderList(this.moTemplateFileObj, lsBulkTemplateSheetName, liFirstRow,
					liFirstCol);
			loDataFileHeaderList = setExcelHeaderList(this.moDataFileObj, lsBulkTemplateSheetName, liFirstRow,
					liFirstCol);
			if (loTemplateHeaderList == null || loDataFileHeaderList == null)
			{
				LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateBulkContractSpreadsheet method::");
				throw new ApplicationException(
						"Error in TemplateProcess1Impl: validateBulkContractSpreadsheet method::");

			}

			if (loTemplateHeaderList.size() != loDataFileHeaderList.size())
			{
				lbValidSheet = false;

			}
			else
			{
				for (int liCount = 0; liCount < loTemplateHeaderList.size(); liCount++)
				{
					if (!(loTemplateHeaderList.get(liCount).equals(loDataFileHeaderList.get(liCount))))
					{
						lbValidSheet = false;
						break;
					}else{
						lbValidSheet = true;
					}
				}
				
			}
			

		}
		catch (ApplicationException aoFileEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateBulkContractSpreadsheet method::",
					aoFileEx);
			throw aoFileEx;

		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateBulkContractSpreadsheet method::",
					ex);
			throw new ApplicationException(
					"Error in TemplateProcess1Impl: validateBulkContractSpreadsheet method::");

		}

		return lbValidSheet;
	}

	/**
	 * This method is used to check the value of the cell
	 * <ul>
	 * <li>It will check the cell value whether it is empty or a white space</li>
	 * </ul>
	 * @param row row of the excel sheet
	 * @param fcell first cell of the excel sheet
	 * @param lcell last cell of the excel sheet
	 * @return boolean value whether empty or not
	 */
	private boolean containsValue(Row row, int fcell, int lcell)
	{
		boolean flag = false;
		for (int i = fcell; i < lcell; i++)
		{
			if (StringUtils.isEmpty(String.valueOf(row.getCell(i))) == true
					|| StringUtils.isWhitespace(String.valueOf(row.getCell(i))) == true
					|| StringUtils.isBlank(String.valueOf(row.getCell(i))) == true
					|| String.valueOf(row.getCell(i)).length() == 0 || row.getCell(i) == null)
			{
			}
			else
			{
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * This function will read record from data file and start processing. First
	 * it will validate the file structure Second it will check mandatory filed
	 * value provided or not Third it will process the optional fields Fourth it
	 * will create the contract and generate the task.
	 * R6: NON APT EPINS- added validateEpinIsUnique
	 * @throws ApplicationException if any exception occurred
	 */
	 //[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	@Override
	public String processData() throws ApplicationException
	{
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String lsBulkUploadFirstRowKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_EXCEL_FIRST_ROW;
		String lsBulkUploadFirstColKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_EXCEL_FIRST_COLUMN;

		String lsBulkTemplateSheetNameKey = HHSConstants.BULK_UPLOAD_APP_SETTING_NAME + HHSConstants.UNDERSCORE
				+ HHSConstants.BULK_UPLOAD_TEMPLATE_SHEET_NAME;
		String lsBulkTemplateSheetName = loApplicationSettingMap.get(lsBulkTemplateSheetNameKey);

		String lsBulkFirstRow = loApplicationSettingMap.get(lsBulkUploadFirstRowKey);
		String lsBulkFirstCol = loApplicationSettingMap.get(lsBulkUploadFirstColKey);
		int liFirstRow = Integer.parseInt(lsBulkFirstRow);
		int liFirstCol = Integer.parseInt(lsBulkFirstCol);

		boolean lbIsAnyRecordSuccess = false;
		boolean lbIsAnyRecordFail = false;
		Channel loChannel = null;
		Workbook loWorkbook;
		String lsMsg = HHSConstants.BULK_UPLOAD_FILE_STATUS_SUCCESS;
		
		boolean checkMandatoryFieldsFlag=false;
		boolean loBulkUploadContractInfoFlag=false;
		boolean processOptionalFieldsFlag=false;
		boolean validateAgencyFlag=false;
		boolean validateProviderFlag=false;
		boolean validateDateFlag=false;
		boolean validateReviewLevelForCertificationOfFundsFlag=false;
		boolean validateProgramNameFlag=false;
		boolean validateBudgetFiscalYearStartYearFlag=false;
		
		try
		{
			FileInputStream loFileInputStream = (FileInputStream) moDataFileObj;
			loWorkbook = WorkbookFactory.create(loFileInputStream);
			Sheet loSheet = loWorkbook.getSheet(lsBulkTemplateSheetName);
			Row loHeaderRow = loSheet.getRow(liFirstRow);
			setExcelHeader(loHeaderRow, liFirstCol);
			Iterator<Row> loRowIterator = loSheet.iterator();
		
			while (loRowIterator.hasNext())
			{
				stbErrorMessage = new StringBuffer();  // reset
				checkMandatoryFieldsFlag=false;
				loBulkUploadContractInfoFlag=false;
				processOptionalFieldsFlag=false;
				validateAgencyFlag=false;
				validateProviderFlag=false;
				validateDateFlag=false;
				validateReviewLevelForCertificationOfFundsFlag=false;
				validateProgramNameFlag=false;
				validateBudgetFiscalYearStartYearFlag=false;			
			
				ArrayList<String> loRecordExcelRow = new ArrayList<String>();
				BulkUploadContractInfo loBulkUploadContractInfo = new BulkUploadContractInfo();
				try
				{
					Row loRow = loRowIterator.next();
					if (loRow.getRowNum() <= liFirstRow)
					{
						continue;
					}
					if (!containsValue((Row) loRow, loRow.getFirstCellNum(), loRow.getLastCellNum()))
					{
						continue;
					}
	
					for (int liColumn = liFirstCol; liColumn <= loHeaderRow.getLastCellNum(); liColumn++)
					{
						Cell loCell = loRow.getCell(liColumn, loRow.RETURN_BLANK_AS_NULL);
						if (loCell == null)
						{
							loRecordExcelRow.add(HHSConstants.EMPTY_STRING);
						}
						else if (loCell.getCellType() == HHSConstants.INT_ZERO
							&& org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(loCell))
						{
							String lsDate = DateUtil.getDateMMDDYYYYFormat(loCell.getDateCellValue());
							loRecordExcelRow.add(lsDate);
						}
						else
						{
							DataFormatter loDf = new DataFormatter();
							String lsCellElement=	loDf.formatCellValue(loCell);
							loCell.setCellType(Cell.CELL_TYPE_STRING);
							loRecordExcelRow.add(lsCellElement);
						}
					}//for
					if (!validateEmptyRecord(loRecordExcelRow))
					{
						continue;
					}
					loBulkUploadContractInfo = getExcelDataBean(loRecordExcelRow);
					
					int liRowNumber = loRow.getRowNum() + 1;
					loBulkUploadContractInfo.setRowNumber(String.valueOf(liRowNumber));
					loBulkUploadContractInfo.setFileUploadId(getFileUploadId());
					loChannel = new Channel();
					checkMandatoryFieldsFlag=checkMandatoryFields(loBulkUploadContractInfo);
					loBulkUploadContractInfoFlag=validateEpin(loBulkUploadContractInfo);
					processOptionalFieldsFlag=processOptionalFields(loBulkUploadContractInfo);
					validateAgencyFlag=validateAgency(loBulkUploadContractInfo);
					validateProviderFlag=validateProvider(loBulkUploadContractInfo);
					validateDateFlag=validateDate(loBulkUploadContractInfo);
					validateReviewLevelForCertificationOfFundsFlag=validateReviewLevelForCertificationOfFunds(loBulkUploadContractInfo);
					validateProgramNameFlag=validateProgramName(loBulkUploadContractInfo);
					validateBudgetFiscalYearStartYearFlag=validateBudgetFiscalYearStartYear(loBulkUploadContractInfo);
				
					if (checkMandatoryFieldsFlag && loBulkUploadContractInfoFlag
						&& processOptionalFieldsFlag
						&& validateAgencyFlag
						&& validateProviderFlag && validateDateFlag
						&& validateReviewLevelForCertificationOfFundsFlag
						&& validateProgramNameFlag
						&& validateBudgetFiscalYearStartYearFlag)
					{
						loBulkUploadContractInfo.setUploadFlag(HHSConstants.BULK_UPLOAD_FLAG_ONE);
						loBulkUploadContractInfo.setErrorMessage(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_FILE_STATE_SUCCESS));
						boolean lsRecordSuccess = saveContract(loBulkUploadContractInfo);
						if(lsRecordSuccess){ //just need set once 
							lbIsAnyRecordSuccess = true;
						}
					}
					//with some error
					else
					{
						loBulkUploadContractInfo.setErrorMessage(getErrorMessage());
						loBulkUploadContractInfo.setUploadFlag(HHSConstants.BULK_UPLOAD_FLAG_ZERO);
						loChannel.setData(HHSConstants.BULK_UPLOAD_CONTRACT_INFO, loBulkUploadContractInfo);
						HHSTransactionManager.executeTransaction(loChannel,
							HHSConstants.TRANSACTION_CONTRACT_BULK_UPLOAD_STATUS);
						lbIsAnyRecordFail = true;
					}
				}catch(Exception ex){
					loBulkUploadContractInfo.setErrorMessage(getErrorMessage());
					loBulkUploadContractInfo.setUploadFlag(HHSConstants.BULK_UPLOAD_FLAG_ZERO);
					loChannel.setData(HHSConstants.BULK_UPLOAD_CONTRACT_INFO, loBulkUploadContractInfo);
					HHSTransactionManager.executeTransaction(loChannel,
						HHSConstants.TRANSACTION_CONTRACT_BULK_UPLOAD_STATUS);
					LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: Process method::", ex);
					lbIsAnyRecordFail = true;			
				}
			}//while (loRowIterator.hasNext())	
		}
		catch (IOException aoFileEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: Process method::", aoFileEx);
			lbIsAnyRecordFail = true;
			throw new ApplicationException("Error in TemplateProcess1Impl: Process method::", aoFileEx);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: Process method::", aoAppEx);
			lbIsAnyRecordFail = true;
			throw aoAppEx;
		}
		catch (InvalidFormatException aoFileEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: Process method::", aoFileEx);
			lbIsAnyRecordFail = true;
			throw new ApplicationException("Error in TemplateProcess1Impl: Invalid format::", aoFileEx);
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: Process method::", ex);
			lbIsAnyRecordFail = true;
			throw new ApplicationException("Error in TemplateProcess1Impl::", ex);
		}
		finally{
		
			if (lbIsAnyRecordFail && lbIsAnyRecordSuccess)
			{
				lsMsg = HHSConstants.BULK_UPLOAD_FILE_STATUS_SUCCESS_WITH_ERROR;
			}
			else if (!lbIsAnyRecordSuccess)
			{
				lsMsg = HHSConstants.BULK_UPLOAD_FILE_STATUS_FAILED;
			}
		}

		return lsMsg;
	}
    //[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	/**
	 * This method is used validate if a complete line of the excel is empty.
	 * @param loRecordExcelRow
	 * @return
	 */
	private boolean validateEmptyRecord(ArrayList<String> loRecordExcelRow)
	{
		boolean lbValidRecord = false;

		for (int liCount = 0; liCount < loRecordExcelRow.size(); liCount++)
		{

			if (!(loRecordExcelRow.get(liCount).equals("") && loRecordExcelRow.get(liCount) != null))
			{
				lbValidRecord = true;
			}
		}

		return lbValidRecord;
	}

	/**
	 * This method will exctract out the column names of excel file aoHeaderRow-
	 * is the row of excel containing the column names aiFirstColumn - is the
	 * column index of first column of excel
	 * @param aoHeaderRow header row of the excell sheet
	 * @param aiFirstColumn first column of the excel sheet
	 */
	private void setExcelHeader(Row aoHeaderRow, int aiFirstColumn) throws ApplicationException
	{
		HashMap<String, String> loExcelHeader = new HashMap<String, String>();
		//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		StringBuilder stbErrorMessage = new StringBuilder();
				
		if(aoHeaderRow.getCell(aiFirstColumn)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn).getStringCellValue()))
		{	loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_INFLIGHT_OPTION,
				(aoHeaderRow.getCell(aiFirstColumn)).getStringCellValue());
		}else{		
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_INFLIGHT_OPTION).append("\n");
		}
		if(aoHeaderRow.getCell(aiFirstColumn + 1)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn + 1).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN,
					(aoHeaderRow.getCell(aiFirstColumn + 1)).getStringCellValue());	
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN).append("\n");
		}
		if(aoHeaderRow.getCell(aiFirstColumn + 2)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn + 2).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY,
					(aoHeaderRow.getCell(aiFirstColumn + 2)).getStringCellValue());
		}else{
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY).append("\n");
		}
		if(aoHeaderRow.getCell(aiFirstColumn + 3)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn + 3).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME,
					(aoHeaderRow.getCell(aiFirstColumn + 3)).getStringCellValue());
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME).append("\n");
		}
		
		if(aoHeaderRow.getCell(aiFirstColumn + 4)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn +4).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE,
					(aoHeaderRow.getCell(aiFirstColumn + 4)).getStringCellValue());
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE).append("\n");
		}
		if(aoHeaderRow.getCell(aiFirstColumn + 5)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn +5).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE,
					(aoHeaderRow.getCell(aiFirstColumn + 5)).getStringCellValue());
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE).append("\n");
		}		
		if(aoHeaderRow.getCell(aiFirstColumn + 6)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn +6).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE,
					(aoHeaderRow.getCell(aiFirstColumn + 6)).getStringCellValue());
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE).append("\n");
		}	
		if(aoHeaderRow.getCell(aiFirstColumn + 7)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn +7).getStringCellValue()))
		{
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE,
					(aoHeaderRow.getCell(aiFirstColumn + 7)).getStringCellValue());
		}else{			
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE).append("\n");
		}		
		
		if(aoHeaderRow.getCell(aiFirstColumn + 8)!=null && !StringUtils.isEmpty(aoHeaderRow.getCell(aiFirstColumn +8).getStringCellValue())){
			loExcelHeader.put(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME,
				(aoHeaderRow.getCell(aiFirstColumn + 8)).getStringCellValue());
		}else{
			stbErrorMessage.append( "missing header" +  HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME).append("\n");
		}
		if(stbErrorMessage.toString().length()>0){
			throw new ApplicationException(stbErrorMessage.toString());
		}
		//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		setExcelHeaderMap(loExcelHeader);

	}

	/**
	 * This method extracts out the header of the excel files for validation
	 * @param aoIS File input stream of the excel file content
	 * @param asSheetName sheet name of the excel sheet
	 * @param aiFirstRow first row of the excel sheet
	 * @param aiFirstColumn first column of the excel sheet
	 * @return list of the header of the excel
	 * @throws ApplicationException if any exception occurred.
	 */
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	private ArrayList<String> setExcelHeaderList(Object aoIS, String asSheetName, int aiFirstRow, int aiFirstColumn)
			throws ApplicationException
	{
		ArrayList<String> loExcelHeader = null;
		FileInputStream loFileInputStream = null;
		Workbook loWorkbook;
		Sheet loSheet = null;
		Row loHeaderRow = null;
		try
		{
			loFileInputStream = (FileInputStream) aoIS;
			loWorkbook = WorkbookFactory.create(loFileInputStream);
			loSheet = loWorkbook.getSheet(asSheetName);
			if (loSheet == null)
			{
				LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: setExcelHeaderList method:: sheet not found, sheet name:" +asSheetName);
				throw new ApplicationException("Error in TemplateProcess1Impl: setExcelHeaderList method:: sheet not found, sheet name:" +asSheetName);

			}
			loHeaderRow = loSheet.getRow(aiFirstRow);
			loExcelHeader = new ArrayList<String>();

			for (int liColumn = aiFirstColumn; liColumn <= loHeaderRow.getLastCellNum(); liColumn++)
			{
				Cell loCell = loHeaderRow.getCell(liColumn, loHeaderRow.RETURN_BLANK_AS_NULL);
				if (loCell == null)
				{
					loExcelHeader.add(HHSConstants.EMPTY_STRING);
				}
				else
				{
					loCell.setCellType(Cell.CELL_TYPE_STRING);
					String lsCellElement = loCell.getStringCellValue();
					loExcelHeader.add(lsCellElement);

				}
			}

		}
		catch (IOException aoFileEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateBulkContractSpreadsheet method::",
					aoFileEx);
			throw new ApplicationException("Error in TemplateProcess1Impl: validateBulkContractSpreadsheet method::",
					aoFileEx);

		}
		catch (InvalidFormatException aoFileEx)
		{
			throw new ApplicationException("Error in TemplateProcess1Impl: Invalid format::", aoFileEx);
		}
		catch (Exception aoFileEx)
		{
			throw new ApplicationException("Error in TemplateProcess1Impl::", aoFileEx);
		}

		return loExcelHeader;

	}
	
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

	/**
	 * This method will exctract out the column names of excel file aoHeaderRow-
	 * is the row of excel containing the column names aiFirstColumn - is the
	 * column index of first column of excel
	 * @param aoBulkUploadContractInfo bulk upload contract info bean object
	 * @return
	 * @throws ApplicationException
	 */
	public boolean validateReviewLevelForCertificationOfFunds(BulkUploadContractInfo aoBulkUploadContractInfo)
			throws ApplicationException
	{
		boolean lbValidReviewLevel = true;
		String lsReviewProcId = HHSConstants.REVIEW_LEVEL_CERTIFICATION_OF_FUNDS;
		String lsAgencyId = aoBulkUploadContractInfo.getAgency();
		int liReviewLevelsAssgnd;
		int liReviewProcId = Integer.parseInt(lsReviewProcId);
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.AS_AGENCY_ID, lsAgencyId);
		loChannelObj.setData(HHSConstants.AI_REVIEW_PROC_ID, liReviewProcId);
		String lsErrorMessage = null;
		try
		{
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.TRANSACTION_CONTRACT_BULK_UPLOAD_REVIEW_LEVEL);
			liReviewLevelsAssgnd = (Integer) loChannelObj.getData(HHSConstants.AI_REVIEW_LEVELS);
			if (liReviewLevelsAssgnd <= 0)
			{
				lsErrorMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.NO_LEVEL_CONFIGURED_COF_BULK_UPLOAD_ERROR_MSG);
				setErrorMessage(lsErrorMessage);
				//return false;
				lbValidReviewLevel=false;
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in TemplateProcess1Impl: validateReviewLevelForCertificationOfFunds method:: ",
					aoAppEx);			
			lbValidReviewLevel=false;
			throw aoAppEx;
		}
		catch (Exception aoFileEx)
		{
			throw new ApplicationException("Error in TemplateProcess1Impl: validateReviewLevelForCertificationOfFunds method::", aoFileEx);
		}
		return lbValidReviewLevel;
	}

	/**
	 * This method will validate the dates provided in excel for a contract.
	 * @param loBulkUploadContractInfo-defines the one record from excel
	 * @return boolean validated date
	 * @throws ApplicationException
	 */
	public boolean validateDate(BulkUploadContractInfo loBulkUploadContractInfo) throws ApplicationException
	{
		boolean lbValidDate = true;
		String lsStartDate = loBulkUploadContractInfo.getContractStartDate();
		String lsEndDate = loBulkUploadContractInfo.getContractEndDate();
		double loDateDiff = 0;
		try
		{
			if (!DateUtil.validateDate(lsStartDate))
			{
				String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NOT_IN_CORRECT_FORMAT),
						HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE);
				setErrorMessage(lsMessage);
				//return false;
				lbValidDate=false;
			}
			if (!DateUtil.validateDate(lsEndDate))
			{
				String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.NOT_IN_CORRECT_FORMAT),
						HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE);
				setErrorMessage(lsMessage);
				//return false;
				lbValidDate=false;
			}
			if(lsStartDate!=null && StringUtils.isNotEmpty(lsStartDate.trim()) && lsEndDate!=null && StringUtils.isNotEmpty(lsEndDate.trim())){
				loDateDiff = DateUtil.calculateDateDifference(DateUtil.getDate(lsStartDate), DateUtil.getDate(lsEndDate));
				if (loDateDiff <= 0)
				{
					setErrorMessage(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.START_DATE_CANNOT_BE_GREATER_THAN_END_DATE));
					//return false;
					lbValidDate=false;
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateDate method:: ", aoAppEx);			
			lbValidDate=false;
			throw aoAppEx;
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateDate method:: ", ex);			
			lbValidDate=false;
			throw new ApplicationException("Error in TemplateProcess1Impl: validateDate method::", ex);
		}
		return lbValidDate;
	}

	/**
	 * This method validate the provider associated with a contract
	 * Updated in R6 - Existing transaction fetchContractDetailsByEPINforNewBulk updated
	 * @param aoContractDetails-defines the one record from excel
	 * @return
	 * @throws ApplicationException
	 */
	public boolean validateProvider(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidProvider = false;
		Channel loChannel = null;
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		/* R6 : fetchContractDetailsByEPINforNewBulk transaction was updated as part of release 6, 
		 and thus requires combination of epin and agency in EpinId field of  EPinDetailBean*/
		loEPinDetailBean.setEpinId(aoContractDetails.getEpin() + HHSConstants.HYPHEN + aoContractDetails.getAgency());
		loChannel = new Channel();
		loChannel.setData(HHSConstants.LO_EPIN_DETAIL, loEPinDetailBean);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRANSACTION_FETCH_CONTRACT_DETAILS_BY_EPIN);
		loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
		if(loEPinDetailBean!=null){
			//R6: Below is the unchanged transaction and hence setting it back to only epin
			loEPinDetailBean.setEpinId(aoContractDetails.getEpin());
			loChannel.setData(HHSConstants.AS_VENDOR_FMS_ID, loEPinDetailBean.getVendorFmsId());
			lbValidProvider = ContractListUtils.validateProviderAcceleratorBulk(loChannel);
			if (!lbValidProvider)
			{
				String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.PROVIDER_DOES_NOT_EXIST);
				setErrorMessage(lsMessage);
			}
		}
		return lbValidProvider;
	}

	/**
	 * This method validates the agency associated with a contract
	 * @param aoContractDetails
	 * @return
	 * @throws ApplicationException
	 */
	private boolean validateAgency(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidAgency = true;
		String lsAgencyId = HHSConstants.EMPTY_STRING;
		String lsAgencyName = HHSConstants.EMPTY_STRING;
		if (aoContractDetails.getAgency() != null)
		{
			lsAgencyId = aoContractDetails.getAgency();
		}

		Channel aoChannel = new Channel();
		Map loHMap = new HashMap();
		loHMap.put(HHSConstants.BULK_AS_AGENCY_ID, lsAgencyId);
		aoChannel.setData(HHSConstants.BULK_LOCAL_HASH_MAP, loHMap);
		try{
			HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.TRANSACTION_FETCH_AGENCY_NAME_BULK);
		
			lsAgencyName = (String) aoChannel.getData(HHSConstants.FETCH_AGENCY_NAME_RESULT);
			if (lsAgencyName == null || lsAgencyName.equals(HHSConstants.EMPTY_STRING))
			{
				lbValidAgency = false;
			}
			if (!lbValidAgency)
			{
				String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY));
			
				setErrorMessage(lsMessage);
			}
	   } catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateAgency method:: ", aoAppEx);
			lbValidAgency=false;
			throw aoAppEx;
		}

		return lbValidAgency;
	}

	/**
	 * This method check if the mendatory field of a record in excel are
	 * available
	 * @param aoContractDetails BulkUploadContractInfo object
	 * @throws ApplicationException when any exception occurred
	 */
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	@Override
	public boolean checkMandatoryFields(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidData = true;			
		if (aoContractDetails.getContractType() == null
				|| aoContractDetails.getContractType().equals(HHSConstants.EMPTY_STRING))
		{
		
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_MENDATORY_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_INFLIGHT_OPTION));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
			
		}
		if (aoContractDetails.getEpin() == null || aoContractDetails.getEpin().equals(HHSConstants.EMPTY_STRING))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_MENDATORY_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN));

			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		if (aoContractDetails.getAgency() == null || aoContractDetails.getAgency().equals(HHSConstants.EMPTY_STRING))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_MENDATORY_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY));

			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		if (aoContractDetails.getAccProgramName() == null
				|| aoContractDetails.getAccProgramName().equals(HHSConstants.EMPTY_STRING))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_MENDATORY_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME));

			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}		
		
		//budgetFiscalYearStartYear required field
		if (aoContractDetails.getBudgetFiscalYearStartYear() == null
				|| aoContractDetails.getBudgetFiscalYearStartYear().equals(HHSConstants.EMPTY_STRING))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_MENDATORY_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME));

			setErrorMessage(lsMessage);			
			lbValidData=false;
		}
				
		if (!(validateFieldLenth(aoContractDetails)))		
		{
			lbValidData=false;
		}
		
		if (!(validateFieldType(aoContractDetails)))
		{
			lbValidData=false;
		}
		
		if (!(validateFieldValue(aoContractDetails)))
		{
			lbValidData=false;
		}

		return lbValidData;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	/**
	 * It will process all optional filed If they do not exist and are not
	 * contained within the spreadsheet, then the record is rejected. If any
	 * optional value is not specified in the spreadsheet, the value is taken
	 * from the database. If any optional value is specified in the spreadsheet,
	 * the value is taken from the spreadsheet.
	 * @param aoContractDetails BulkUploadContractInfo object
	 * @throws ApplicationException when any exception occurred
	 */
	@Override
	public boolean processOptionalFields(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidOptionFields = true;
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		loEPinDetailBean.setEpinId(aoContractDetails.getEpin() + HHSConstants.HYPHEN + aoContractDetails.getAgency());
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.LO_EPIN_DETAIL, loEPinDetailBean);
			HHSTransactionManager
					.executeTransaction(loChannel, HHSConstants.TRANSACTION_FETCH_CONTRACT_DETAILS_BY_EPIN);
			loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (aoContractDetails.getContractTitle() == null
					|| aoContractDetails.getContractTitle().isEmpty())
			{
				if (loEPinDetailBean!=null && loEPinDetailBean.getProcDescription() != null && !loEPinDetailBean.getProcDescription().isEmpty())
				{
					aoContractDetails.setContractTitle(loEPinDetailBean.getProcDescription());
				}
				else
				{
					String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_DATA_NOT_SHEET_DB),
							this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE));
					setErrorMessage(lsMessage);
					//return false;
					lbValidOptionFields=false;
				}
			}
			if (aoContractDetails.getContractValue() == null
					|| aoContractDetails.getContractValue().isEmpty())
			{
				if ( loEPinDetailBean!=null && !(loEPinDetailBean.getContractValue() == null)
						&& !(loEPinDetailBean.getContractValue().equals(HHSConstants.EMPTY_STRING)))
				{
					aoContractDetails.setContractValue(loEPinDetailBean.getContractValue());
				}
				else
				{
					String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_DATA_NOT_SHEET_DB),
							this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE));
					setErrorMessage(lsMessage);
					//return false;
					lbValidOptionFields=false;
				}
			}
			if (aoContractDetails.getContractStartDate() == null
					|| aoContractDetails.getContractStartDate().isEmpty())
			{
				if (loEPinDetailBean!=null && loEPinDetailBean.getContractStart() != null
						&& !(loEPinDetailBean.getContractStart().equals(HHSConstants.EMPTY_STRING)))
				{
					aoContractDetails.setContractStartDate((loEPinDetailBean.getContractStart()).toString());
				}
				else
				{
					String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_DATA_NOT_SHEET_DB),
							this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE));
					setErrorMessage(lsMessage);
					//return false;
					lbValidOptionFields=false;
				}
			}
			if (aoContractDetails.getContractEndDate() == null
					|| aoContractDetails.getContractEndDate().isEmpty())
			{
				if (loEPinDetailBean!=null && !(loEPinDetailBean.getContractEnd() == null)
						&& !(loEPinDetailBean.getContractEnd().equals(HHSConstants.EMPTY_STRING)))
				{
					aoContractDetails.setContractEndDate((loEPinDetailBean.getContractEnd()).toString());
				}
				else
				{
					String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_DATA_NOT_SHEET_DB),
							this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE));
					setErrorMessage(lsMessage);
					//return false;
					lbValidOptionFields=false;
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: processOptionalFields method:: ", aoAppEx);
			lbValidOptionFields=false;
			throw aoAppEx;
		}
		catch (Exception ex)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: processOptionalFields method:: ", ex);
			lbValidOptionFields=false;
			throw new ApplicationException("Error in TemplateProcess1Impl: Process method::processOptionalFields::", ex);
		}
		return lbValidOptionFields;
	}

	/**
	 * It will create the contract and save the values in DB and generate the
	 * required workflow task.
	 * @param aoContractDetails BulkUploadContractInfo object
	 * @throws ApplicationException when any exception occurred
	 */
	@Override
	public boolean saveContract(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbContractSaved = false;
		Channel loChannel = null;
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		try
		{
			loEPinDetailBean.setEpinId(aoContractDetails.getEpin() + HHSConstants.HYPHEN + aoContractDetails.getAgency());
			loChannel = new Channel();
			loChannel.setData(HHSConstants.LO_EPIN_DETAIL, loEPinDetailBean);
			HHSTransactionManager
					.executeTransaction(loChannel, HHSConstants.TRANSACTION_FETCH_CONTRACT_DETAILS_BY_EPIN);
			loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			loEPinDetailBean.setEpinId(aoContractDetails.getEpin());
			populateEpinBeanFromExcel(aoContractDetails, loEPinDetailBean);
			loChannel.setData(HHSConstants.AS_EPIN, loEPinDetailBean.getAwardEpin());
			/* R6: changed validation rules for new non apt epins */
			boolean lbIsEpinValid = validateEpinIsUnique(aoContractDetails);
			if (lbIsEpinValid)
			{
				loChannel.setData(HHSConstants.AS_VENDOR_FMS_ID, loEPinDetailBean.getVendorFmsId());
				boolean lbProviderRegistered = ContractListUtils.validateProviderAcceleratorBulk(loChannel);
				if (lbProviderRegistered)
				{
					loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
					loChannel.setData(HHSConstants.BULK_UPLOAD_CONTRACT_INFO, aoContractDetails);
					loChannel.setData(HHSConstants.AO_CONTRACT_DETAILS_BY_EPIN, loEPinDetailBean);
					loChannel.setData(HHSConstants.AO_FILENET_SESSION, moUserSession);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.ADD_CONTRACT_DETAILS_BULK);
					lbContractSaved = true;
				}

			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: saveContract method:: ", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			ApplicationException loAppExp = new ApplicationException(
					"Exception occured in TemplateProcess1Impl: saveContract method::", aoAppEx);
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: saveContract method:: ", loAppExp);
			throw loAppExp;
		}
		return lbContractSaved;
	}

	/**
	 * This method will populate the EPinDetailBean by the contract attributes
	 * from excel and DB
	 * @param aoContractDetails BulkUploadContractInfo object
	 * @param loEPinDetailBean EPinDetailBean object
	 * @throws ApplicationException if any exception occurred
	 */
	private void populateEpinBeanFromExcel(BulkUploadContractInfo aoContractDetails, EPinDetailBean loEPinDetailBean)
			throws ApplicationException
	{
		loEPinDetailBean.setContractTypeId(aoContractDetails.getContractType());
		loEPinDetailBean.setAgencyId(aoContractDetails.getAgency());
		loEPinDetailBean.setProgramId(aoContractDetails.getAccProgramId());
		loEPinDetailBean.setContractTitle(aoContractDetails.getContractTitle());
		loEPinDetailBean.setContractValue(aoContractDetails.getContractValue());
		loEPinDetailBean.setContractStart(aoContractDetails.getContractStartDate());
		loEPinDetailBean.setContractEnd(aoContractDetails.getContractEndDate());
		loEPinDetailBean.setStatusId(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_CONFIGURATION));
		loEPinDetailBean.setContractTypeId(HHSConstants.ONE);
		loEPinDetailBean.setRegistrationFlag(HHSConstants.STRING_ZERO);
		loEPinDetailBean.setUpdateFlag(HHSConstants.STRING_ZERO);
		loEPinDetailBean.setDeleteFlag(HHSConstants.STRING_ZERO);
		loEPinDetailBean.setDiscrepancyFlag(HHSConstants.STRING_ZERO);
		loEPinDetailBean.setContractSourceId(HHSConstants.TWO);
		loEPinDetailBean.setCreateByUserId(getFileUploadedByUser());
		loEPinDetailBean.setModifyByUserId(getFileUploadedByUser());
		//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		//if ((aoContractDetails.getContractType()).equals(HHSConstants.CONTRACT_TYPE_DESCRETIONARY))
		if ((aoContractDetails.getContractType()).equals(HHSConstants.INFLIGHT_WITH_COF))
		{
			loEPinDetailBean.setChkContractCertFundsFlag(HHSConstants.ONE);
			loEPinDetailBean.setLaunchCOF(true);
		}
		//if ((aoContractDetails.getContractType()).equals(HHSConstants.CONTRACT_TYPE_INFLIGHT))
		else if ((aoContractDetails.getContractType()).equals(HHSConstants.INFLIGHT_WITHOUT_COF))
		{
			loEPinDetailBean.setChkContractCertFundsFlag(HHSConstants.STRING_ZERO);
			loEPinDetailBean.setLaunchCOF(false);
		}	
		if (loEPinDetailBean.getChkContractCertFundsFlag() == null
				|| loEPinDetailBean.getChkContractCertFundsFlag().equals(HHSConstants.EMPTY_STRING))
		{
			loEPinDetailBean.setChkContractCertFundsFlag(HHSConstants.STRING_ZERO);
			loEPinDetailBean.setLaunchCOF(false);
		}
		
		loEPinDetailBean.setBudgetStartYear(aoContractDetails.getBudgetFiscalYearStartYear());
		//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

	}

	/**
	 * This Method can impose additional business rules on record. If for any
	 * new template few business rules added then we can create new class which
	 * will implement the AdditionalFiledProcessing interface and write down the
	 * new business rules. We will set AdditionalFiledProcessing object by using
	 * this method. After applying basic business rules application will check
	 * if AdditionalFiledProcessing object is found then it will call
	 * processAdditionalFields method and apply new business rules.
	 */
	@Override
	public void setAdditionalFieldProcessObj(AdditionalFiledProcessing aoBusinessRule)
	{
	}

	/**
	 * This method validates the types for the different field in excel
	 * 
	 * @param loBulkUploadContractInfo-defines a row from excel
	 * @return boolean validate field type
	 * @throws ApplicationException
	 */
	private boolean validateFieldType(BulkUploadContractInfo loBulkUploadContractInfo) throws ApplicationException
	{
		boolean lbValidData = true;


		if (!(((String) loBulkUploadContractInfo.getEpin()).matches(HHSConstants.PATTERN_BULK_UPLOAD_CONTRACT_EPIN)))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (!(((String) loBulkUploadContractInfo.getContractTitle())
				.matches(HHSConstants.PATTERN_BULK_UPLOAD_CONTRACT_TITLE)))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		if (!(loBulkUploadContractInfo.getContractValue().equals(""))
				&& !(((String) loBulkUploadContractInfo.getContractValue())
						.matches(HHSConstants.PATTERN_BULK_UPLOAD_CONTRACT_VALUE_LATEST_NEW)))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		if (!(loBulkUploadContractInfo.getContractStartDate().equals(""))
				&& !(DateUtil.validateDate(((String) loBulkUploadContractInfo.getContractStartDate()).trim())))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		if (!(loBulkUploadContractInfo.getContractEndDate().equals(""))
				&& !(DateUtil.validateDate(((String) loBulkUploadContractInfo.getContractEndDate()).trim())))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		try{
			Integer.parseInt(loBulkUploadContractInfo.getBudgetFiscalYearStartYear().trim());
		}catch(Exception e){
			//failed to parsing
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_TYPE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		
		//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

		return lbValidData;
	}
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	private boolean validateFieldValue(BulkUploadContractInfo loBulkUploadContractInfo) throws ApplicationException
	{
		Boolean lbValidData = Boolean.TRUE;
		if (!((loBulkUploadContractInfo.getContractType()).trim()).equals(HHSConstants.INFLIGHT_WITH_COF)
					&& !((loBulkUploadContractInfo.getContractType()).trim()).equals(HHSConstants.INFLIGHT_WITHOUT_COF))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_INFLIGHT_OPTION));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}	
		
		return lbValidData;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

	/**
	 * This method validates the length for the different field in excel
	 * 
	 * @param loBulkUploadContractInfo-defines a row from excel
	 * @return boolean validate field length
	 * @throws ApplicationException
	 */
	private boolean validateFieldLenth(BulkUploadContractInfo loBulkUploadContractInfo) throws ApplicationException
	{
		boolean lbValidData = true;

		if (((String) loBulkUploadContractInfo.getEpin()).length() > 20)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AWARD_EPIN));
			loBulkUploadContractInfo.setEpin(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (((String) loBulkUploadContractInfo.getAgency()).length() > 5)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_AGENCY));
			loBulkUploadContractInfo.setAgency(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (((String) loBulkUploadContractInfo.getAccProgramName()).length() > 250)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME));
			loBulkUploadContractInfo.setAccProgramName(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (((String) loBulkUploadContractInfo.getContractTitle()).length() > 120)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_TITLE));
			loBulkUploadContractInfo.setContractTitle(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if ((((String) loBulkUploadContractInfo.getContractValue())
				.matches(HHSConstants.PATTERN_BULK_UPLOAD_CONTRACT_VALUE_LATEST_NEW)) &&
				((loBulkUploadContractInfo.getContractValue().lastIndexOf(HHSConstants.DOT)!=-1) &&
				(((String) loBulkUploadContractInfo.getContractValue()).substring(HHSConstants.INT_ZERO , loBulkUploadContractInfo.getContractValue().lastIndexOf(HHSConstants.DOT)).length() > 16
				|| ((String) loBulkUploadContractInfo.getContractValue()).substring(loBulkUploadContractInfo.getContractValue().lastIndexOf(HHSConstants.DOT),loBulkUploadContractInfo.getContractValue().length()-1).length() > 2))
				||	
				(loBulkUploadContractInfo.getContractValue().lastIndexOf(HHSConstants.DOT)==-1 &&
						((String) loBulkUploadContractInfo.getContractValue()).substring(HHSConstants.INT_ZERO , loBulkUploadContractInfo.getContractValue().length()).length() > 16))
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE));
			loBulkUploadContractInfo.setContractValue(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (loBulkUploadContractInfo.getContractValue()!=null &&
				!loBulkUploadContractInfo.getContractValue().isEmpty()&&
				(((String) loBulkUploadContractInfo.getContractValue())
						.matches(HHSConstants.PATTERN_BULK_UPLOAD_CONTRACT_VALUE)) &&
				(new BigDecimal(loBulkUploadContractInfo.getContractValue()).compareTo(new BigDecimal(0))) < 0)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_CONTRACT_LESS_THAN_ZERO_CHECK),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_VALUE));
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		
		if (((String) loBulkUploadContractInfo.getContractStartDate()).length() > 10)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE));
			loBulkUploadContractInfo.setContractStartDate(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}

		if (((String) loBulkUploadContractInfo.getContractEndDate()).length() > 10)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE));
			loBulkUploadContractInfo.setContractEndDate(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		if (((String) loBulkUploadContractInfo.getBudgetFiscalYearStartYear()).length() > 4)
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_LENGTH_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME));
			loBulkUploadContractInfo.setBudgetFiscalYearStartYear(HHSConstants.EMPTY_STRING);
			setErrorMessage(lsMessage);
			//return false;
			lbValidData=false;
		}
		//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		
		return lbValidData;
	}

	/**
	 * R6 APT: This method validates combination of Epin and Agency is unique across all tables
	 * 
	 * @param loBulkUploadContractInfo-defines a row from excel
	 * @return boolean validate epin
	 * @throws ApplicationException
	 */
	private boolean validateEpinIsUnique(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidEpin = false;
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		loEPinDetailBean.setEpinId(aoContractDetails.getEpin());
		loEPinDetailBean.setAgencyId(aoContractDetails.getAgency());
		/*
		 * R6: EPIN uniqueness for EPIn and Agency ID
		 */
		try
		{
			lbValidEpin = ContractListUtils.validateEpinUnique(loEPinDetailBean);
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateEpinIsUnique method:: ", aoAppEx);
			throw aoAppEx;
		}
		return lbValidEpin;
	}
	
	/**
	 * This method validates Epin associated with a contract
	 * 
	 * @param loBulkUploadContractInfo-defines a row from excel
	 * @return boolean validate epin
	 * @throws ApplicationException
	 */

	public boolean validateEpin(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidEpin = false;
		EPinDetailBean loEPinDetailBean = new EPinDetailBean();
		Channel loChannel = new Channel();
		try
		{
			loEPinDetailBean.setEpinId(aoContractDetails.getEpin() + HHSConstants.HYPHEN + aoContractDetails.getAgency());
			loChannel.setData(HHSConstants.LO_EPIN_DETAIL, loEPinDetailBean);

			HHSTransactionManager
					.executeTransaction(loChannel, HHSConstants.TRANSACTION_FETCH_CONTRACT_DETAILS_BY_EPIN);
			loEPinDetailBean = (EPinDetailBean) loChannel.getData(HHSConstants.AO_CONTRACT_DETAIL);
			if (!(loEPinDetailBean == null))
			{
				lbValidEpin = true;
			}
			if (!lbValidEpin)
			{
				String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.EPIN_DOES_NOT_EXIST);
				setErrorMessage(lsMessage);
				//return false;
				lbValidEpin=false;
			}else{
				loChannel.setData(HHSConstants.AS_EPIN, aoContractDetails.getEpin());
				/* R6: changed validation rules for new non apt epins*/
				lbValidEpin = validateEpinIsUnique(aoContractDetails);
				if (!lbValidEpin)
				{
					String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.EPIN_ALREADY_USE);
					setErrorMessage(lsMessage);
					//return false;
					lbValidEpin=false;
				}
			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateEpin method:: ", aoAppEx);
			lbValidEpin=false;
			throw aoAppEx;			
		}
		catch (Exception aoAppEx)
		{
			ApplicationException loAppExp = new ApplicationException(
					"Exception occured in TemplateProcess1Impl: validateEpin method::", aoAppEx);
			LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateEpin method:: ", loAppExp);
			lbValidEpin=false;
			throw loAppExp;
		}
		//setErrorMessage("");
		return lbValidEpin;
	}

	/**
	 * This method populates the BulkUploadContractInfo bean from a row of
	 * excel.
	 * @param record list of rows to be proccessed
	 * @return BulkUploadContractInfo bean object
	 * @throws ApplicationException if any exception occurred
	 */
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public BulkUploadContractInfo getExcelDataBean(ArrayList<String> record) throws ApplicationException
	{
		BulkUploadContractInfo loBulkUploadContractInfo = new BulkUploadContractInfo();

		String lsContractType = record.get(0);
		if (lsContractType == null || StringUtils.isEmpty(lsContractType))
		{
			loBulkUploadContractInfo.setContractType(HHSConstants.EMPTY_STRING);
		}
		else
		{
			lsContractType=HHSUtil.replaceWordSpace((String)lsContractType);
			loBulkUploadContractInfo.setContractType(lsContractType.trim());
		}
		
		String lsEpin = record.get(1);
		if (lsEpin == null || StringUtils.isEmpty(lsEpin) )
		{
			loBulkUploadContractInfo.setEpin(HHSConstants.EMPTY_STRING);
		}else{
			lsEpin=HHSUtil.replaceWordSpace((String)lsEpin);
			lsEpin = lsEpin.replaceAll("[^a-zA-Z0-9]",""); // remove the special characters, space too, 
			loBulkUploadContractInfo.setEpin(lsEpin.trim());
		}
	
		String lsAgency = record.get(2);
		if (lsAgency == null || StringUtils.isEmpty(lsAgency))
		{
			loBulkUploadContractInfo.setAgency(HHSConstants.EMPTY_STRING);	
		}
		else{
			lsAgency=HHSUtil.replaceWordSpace((String)lsAgency);
			loBulkUploadContractInfo.setAgency(lsAgency.trim());
		}
		String lsAccProgramName = record.get(3);		
		if (lsAccProgramName == null || StringUtils.isEmpty(lsAccProgramName))
		{
			loBulkUploadContractInfo.setAccProgramName(HHSConstants.EMPTY_STRING);
		}else{
			lsAccProgramName=HHSUtil.replaceWordSpace((String)lsAccProgramName);
			loBulkUploadContractInfo.setAccProgramName(lsAccProgramName.trim());
		}
		String lsContractTitle = record.get(4);	
		if (lsContractTitle == null || StringUtils.isEmpty(lsContractTitle)){
			loBulkUploadContractInfo.setContractTitle(HHSConstants.EMPTY_STRING);
		}else{
		/** [Start] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
			lsContractTitle = HHSUtil.convertSpecialCharactersHTMLGlobal(lsContractTitle);		
			loBulkUploadContractInfo.setContractTitle(lsContractTitle.trim());
		/** [End] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
		}
		String lsContractValue =  record.get(5);
		if(lsContractValue==null || StringUtils.isEmpty(lsContractValue)){
			loBulkUploadContractInfo.setContractValue(HHSConstants.EMPTY_STRING);
		}else{
			
			if(null!=lsContractValue && (lsContractValue.contains(HHSConstants.BULK_DOLLOR_SIGN)
					|| lsContractValue.contains(HHSConstants.COMMA)))
			{
				lsContractValue = lsContractValue.replace(HHSConstants.BULK_DOLLOR_SIGN,
						HHSConstants.EMPTY_STRING);
				lsContractValue = lsContractValue.replace(HHSConstants.COMMA,
						HHSConstants.EMPTY_STRING);
			}
			lsContractValue=HHSUtil.replaceWordSpace(lsContractValue);
			loBulkUploadContractInfo.setContractValue(lsContractValue.trim());
		}
		String lsContractStartDate =  record.get(6);
		if(lsContractStartDate==null || StringUtils.isEmpty(lsContractStartDate)){
			loBulkUploadContractInfo.setContractStartDate(HHSConstants.EMPTY_STRING);
		}else{
			lsContractStartDate=HHSUtil.replaceWordSpace((String)lsContractStartDate);
			loBulkUploadContractInfo.setContractStartDate(lsContractStartDate.trim());	
		}	
		String lsContractEndDate =  record.get(7);
		if(lsContractEndDate==null || StringUtils.isEmpty(lsContractEndDate)){
			loBulkUploadContractInfo.setContractEndDate(HHSConstants.EMPTY_STRING);
		}else{
			lsContractEndDate=HHSUtil.replaceWordSpace((String)lsContractEndDate);
			loBulkUploadContractInfo.setContractEndDate((String) record.get(7).trim());
		}
		
		//for budget fiscal start year
		String lsBudgetFiscalYearStartYear =  record.get(8);
		if (lsBudgetFiscalYearStartYear == null || StringUtils.isEmpty(lsBudgetFiscalYearStartYear))		{
			loBulkUploadContractInfo.setBudgetFiscalYearStartYear(HHSConstants.EMPTY_STRING);
		}else{
			lsBudgetFiscalYearStartYear=HHSUtil.replaceWordSpace((String)lsBudgetFiscalYearStartYear);
			loBulkUploadContractInfo.setBudgetFiscalYearStartYear(lsBudgetFiscalYearStartYear.trim());
		}
	
		
		loBulkUploadContractInfo.setCreatedByUserId(HHSConstants.BULK_UPLOAD_SYSTEM_USER);
		loBulkUploadContractInfo.setModifiedByUserId(HHSConstants.BULK_UPLOAD_SYSTEM_USER);
		return loBulkUploadContractInfo;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

	/**
	 * This method fetches program name id on the basis of agency id and program
	 * name .if value does not exist set error message true.
	 * 
	 * @param aoContractDetails ContractDetails bean object
	 * @return true or false
	 * @throws ApplicationException
	 */
	public boolean validateProgramName(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidProgramName = false;
		Channel loChannel = null;
		loChannel = new Channel();
		String lsProgramId = null;

		loChannel.setData(HHSConstants.LO_CONTRACT_DETAILS, aoContractDetails);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROGRAM_NAME_ID);
		lsProgramId = (String) loChannel.getData(HHSConstants.PROGRAM_ID);
		if (null == lsProgramId || lsProgramId.isEmpty())
		{
			String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
					this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_ACC_PROGRAM_NAME));
			setErrorMessage(lsMessage);
			lbValidProgramName = false;
		}
		else
		{
			aoContractDetails.setAccProgramId(lsProgramId);
			lbValidProgramName = true;
		}

		return lbValidProgramName;
	}
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	public boolean validateBudgetFiscalYearStartYear(BulkUploadContractInfo aoContractDetails) throws ApplicationException
	{
		boolean lbValidBudgetFiscalYearStartYear = false;
		String datePattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
		
		//for budget Fiscal Year StartYear
		
		Channel loChannelObj = new Channel();
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_NEW_FY_TASK_DAYS_VALUE);
		Integer loNumOfDays = (Integer) loChannelObj.getData(HHSR5Constants.DURATION_IN_DAYS);
				
		if( aoContractDetails.getContractStartDate()!=null && aoContractDetails.getContractEndDate()!=null){
			    	String lsContractStartDate =aoContractDetails.getContractStartDate();
			    	String lsContractEndDate =aoContractDetails.getContractEndDate();
			    	Calendar calendarStart = Calendar.getInstance();
			    	try{
			    		calendarStart.setTime(simpleDateFormat.parse(lsContractStartDate));
			    	}catch(Exception e){
			    		String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
								this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_START_DATE));
			    		setErrorMessage(lsMessage);
			    		lbValidBudgetFiscalYearStartYear=false;
			    	}
	
					Calendar calendarEnd = Calendar.getInstance();
					try{
						calendarEnd.setTime(simpleDateFormat.parse(lsContractEndDate));   
					}catch(Exception e){
			    		String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
								this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_CONTRACT_END_DATE));
			    		setErrorMessage(lsMessage);
			    		lbValidBudgetFiscalYearStartYear=false;
			    	}
					//Generate FY list. 
					ArrayList<Integer> lsBudgetYears = (new HHSUtil()).FYListInt( calendarStart , calendarEnd , loNumOfDays) ;
					if(aoContractDetails.getBudgetFiscalYearStartYear()!=null){
						String lsBudgetFiscalYearStartYear = aoContractDetails.getBudgetFiscalYearStartYear();
						int budgetFiscalYearStartYear =0;
						try{
							budgetFiscalYearStartYear = Integer.parseInt(lsBudgetFiscalYearStartYear.trim());
							if(lsBudgetYears.contains(budgetFiscalYearStartYear)){
								lbValidBudgetFiscalYearStartYear=true;
							}else{
								String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
										HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
										this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME));					
								LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateFieldValue method::" +",calendarStart:"+calendarStart +",calendarEnd:" +calendarEnd +",loNumOfDays:"+loNumOfDays +",lsBudgetYears:" +lsBudgetYears + ",budgetFiscalYearStartYear:"+budgetFiscalYearStartYear);
								setErrorMessage(lsMessage);
					    		lbValidBudgetFiscalYearStartYear=false;
							}
						}catch(Exception e){
							String lsMessage = MessageFormat.format(PropertyLoader.getProperty(
									HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.BULK_UPLOAD_VALUE_ERROR),
									this.excelHeaderMap.get(HHSConstants.BULK_UPLOAD_EXCEL_COLUMN_BUDGET_FY_START_YEAR_NAME));					
							setErrorMessage(lsMessage);
							LOG_OBJECT.Error("Exception occured in TemplateProcess1Impl: validateFieldValue method::" +",calendarStart:"+calendarStart +",calendarEnd:" +calendarEnd +",loNumOfDays:"+loNumOfDays +",lsBudgetYears:" +lsBudgetYears + ",budgetFiscalYearStartYear:"+budgetFiscalYearStartYear);
				
							lbValidBudgetFiscalYearStartYear =false;
						}
						
					}
			    }		
				
		return lbValidBudgetFiscalYearStartYear;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS

}
