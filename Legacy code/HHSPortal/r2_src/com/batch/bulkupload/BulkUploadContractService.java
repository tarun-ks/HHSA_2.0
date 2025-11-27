/**
 * 
 */
package com.batch.bulkupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <ul>
 * <li>* This class is used by the Bulk Upload Batch for executing tasks like
 * getting unprocessed file <br>
 * from DB and updating status for them in DB.</li>
 * </ul>
 */
public class BulkUploadContractService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(BulkUploadContractService.class);

	/**
	 * <ul>
	 * <li>This method is used to save one record per line in data excel file
	 * with</li>
	 * <li>The query used is : insertBulkUploadStatusByRecord</li>
	 * </ul>
	 * 
	 * status weather its processed or failed
	 * 
	 * @param aoBulkUploadContractInfo -Defines the bean corresponding one line
	 *            in excel file
	 * @param aoMyBatisSession - SQL session used
	 * @return boolean value of the updated status.
	 * @throws ApplicationException
	 */
	public boolean saveBulkUploadStatusByRecord(BulkUploadContractInfo aoBulkUploadContractInfo,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbRecordSaved = false;
		lbRecordSaved = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoBulkUploadContractInfo, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.QUERY_INSERT_BULK_UPLOAD_STATUS_BY_RECORD, HHSConstants.BEAN_BULK_UPLOAD_CONTRACT_INFO);
			lbRecordSaved = true;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadContractService: saveBulkUploadStatusByRecord method:: ",
					aoAppEx);
			throw aoAppEx;
		}
		return lbRecordSaved;
	}

	/**
	 * <ul>
	 * <li>This method return the list of files with status 'Unprocessed' or 'In
	 * progress'</li>
	 * <li>The query used is : getBulkUploadFileInfo</li>
	 * </ul>
	 * 
	 * @param aoSession - defines the SQL session
	 * @return the list of the entries for which upload will be done.
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<BulkUploadFileInformation> getBulkUploadFileInformation(SqlSession aoSession)
			throws ApplicationException
	{

		ArrayList<BulkUploadFileInformation> loList = null;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();
		try
		{
			loReqdMap.put(HHSConstants.BULK_UPLOAD_STATUS_NOT_PROCESSED, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BULK_UPLOAD_NOT_PROCESSED));
			loReqdMap.put(HHSConstants.BULK_UPLOAD_STATUS_IN_PROGRESS, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BULK_UPLOAD_IN_PROGRESS));
			loReqdMap.put(HHSConstants.STATUS_BULK_UPLOAD_FAIL, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BULK_UPLOAD_FAIL));
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_LOCK_PERIOD_MULTIPLY, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGE_PROP_FILE, HHSConstants.BULK_UPLOAD_FILE_LOCK_PERIOD));
			loList = (ArrayList<BulkUploadFileInformation>) DAOUtil.masterDAO(aoSession, loReqdMap,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.QUERY_GET_BULK_UPLOAD_FILE_INFO,
					HHSConstants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadContractService: getBulkUploadFileInformation method:: ",
					aoAppEx);
			throw aoAppEx;
		}

		return loList;

	}

	/**
	 * <ul>
	 * <li>This method uploads the file to file system</li>
	 * </ul>
	 * 
	 * @param aoIS - defines the InputStream for the file to be uploaded
	 * @param aoPropertyMap - defined collection for holding document properties
	 * @return String name of the file created in the local drive
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String saveBulkUploadFileToFileSystem(InputStream aoIS, HashMap aoPropertyMap) throws ApplicationException
	{
		String lsFileExtn = HHSConstants.EMPTY_STRING;
		String lsFileNameWithoutExtn = HHSConstants.EMPTY_STRING;
		String lsInitialFileName = HHSConstants.EMPTY_STRING;
		String lsFileName = HHSConstants.EMPTY_STRING;
		String lsFileNameWithDir = HHSConstants.EMPTY_STRING;
		FileOutputStream loFileOutput = null;
		try
		{
			String lsFileDir = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH);
			File loFileDir = new File(lsFileDir);
			if (!loFileDir.exists())
			{
				loFileDir.mkdirs();
			}
			lsInitialFileName = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
			int liExtension = lsInitialFileName.lastIndexOf(HHSConstants.DOT);
			lsFileExtn = lsInitialFileName.substring(liExtension + 1);
			lsFileNameWithoutExtn = FilenameUtils.removeExtension(lsInitialFileName);
			long lsTimeStamp = System.currentTimeMillis();
			lsFileName = lsFileNameWithoutExtn + HHSConstants.UNDERSCORE
					+ (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY) + HHSConstants.UNDERSCORE
					+ lsTimeStamp + HHSConstants.DOT + lsFileExtn;
			lsFileNameWithDir = lsFileDir + lsFileName;
			File loFileToSave = new File(lsFileNameWithDir);
			if (!loFileToSave.exists())
			{
				loFileToSave.createNewFile();
			}
			loFileOutput = new FileOutputStream(loFileToSave);
			byte lbByteStream[] = FileNetOperationsUtils.convert(aoIS);
			loFileOutput.write(lbByteStream);
			loFileOutput.flush();
		}
		catch (IOException aoFileEX)
		{
			LOG_OBJECT.Error(
					"IOException occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method:: ",
					aoFileEX);
			throw new ApplicationException(
					"IOException occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method:: ",
					aoFileEX);

		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method::",
					aoEx);
			throw new ApplicationException(aoEx.getMessage()
					+ "Exception occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method:: ", aoEx);

		}
		finally
		{
			try
			{
				if (null != loFileOutput)
				{
					loFileOutput.flush();
					loFileOutput.close();
				}
			}
			catch (IOException aoIoEx)
			{
				LOG_OBJECT.Error(
						"Exception occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method::",
						aoIoEx);
				throw new ApplicationException(aoIoEx.getMessage()
						+ "Exception occured in BulkUploadContractService: saveBulkUploadFileToFileSystem method:: ",
						aoIoEx);

			}
		}

		return lsFileName;
	}

	/**
	 * <ul>
	 * <li>This method will fetch the total no of contracts fetched for that
	 * particular upload file by executing query<b>getTotalContracts</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Session
	 * @param asBulkUploadId Uploaded file ID
	 * @return
	 * @throws ApplicationException
	 */
	public Integer getTotalContracts(SqlSession aoMyBatisSession, String asBulkUploadId) throws ApplicationException
	{

		Integer loTotalContracts = 0;
		try
		{
			loTotalContracts = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asBulkUploadId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_TOTAL_CONTRACTS,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoAppExp)
		{
			throw new ApplicationException("Exception occured in getting total contracts", aoAppExp);
		}

		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured in getting total contracts", aoEx);
		}

		return loTotalContracts;
	}

	/**
	 * <ul>
	 * <li>This method will fetch the error messages for that particular
	 * uploaded file by executing the query <b>getErrorList</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession SQL Session
	 * @param asBulkUploadId Uploaded file ID
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<BulkUploadContractInfo> getErrorList(SqlSession aoMyBatisSession, String asBulkUploadId)
			throws ApplicationException
	{

		List<BulkUploadContractInfo> loErrorList = null;
		try
		{
			loErrorList = (List<BulkUploadContractInfo>) DAOUtil.masterDAO(aoMyBatisSession, asBulkUploadId,
					HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSConstants.GET_ERROR_LIST,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoAppExp)
		{
			throw new ApplicationException("Exception occured in getting Error List", aoAppExp);
		}

		catch (Exception aoEx)
		{
			throw new ApplicationException("Exception occured in getting Error List", aoEx);
		}

		return loErrorList;
	}

	/**
	 * This method fetch program name id required for contract generation on the
	 * basis of program name and agency id
	 * 
	 * @param aoMybatisSession SQL Session
	 * @param aoContractDetails ContractDetails bean object
	 * @return program name id
	 * @throws ApplicationException
	 */
	public String getProgramNameId(SqlSession aoMybatisSession, BulkUploadContractInfo aoContractDetails)
			throws ApplicationException
	{
		String lsProgramNameId = null;
		try
		{
			lsProgramNameId = (String) DAOUtil.masterDAO(aoMybatisSession, aoContractDetails,
					HHSConstants.MAPPER_CLASS_FINANCIAL_PAYMENT_MAPPER, HHSConstants.GET_PROGRAM_NAME_ID,
					HHSConstants.BEAN_BULK_UPLOAD_CONTRACT_INFO);

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData(HHSConstants.PROGRAM_NAME_LOWERCASE, aoContractDetails.getAccProgramName());
			LOG_OBJECT.Error("Error While fetching programe name id ", loAppEx);
			setMoState("Error While fetching programe name id " + aoContractDetails.getAccProgramName());
			throw loAppEx;
		}
		return lsProgramNameId;
	}

	/**
	 * @param aoMyBatisSession
	 * @param asBulkUploadId
	 * @return
	 * @throws ApplicationException
	 */
	public boolean bulkUploadSystemFailure(SqlSession aoMyBatisSession, String asBulkUploadId)
			throws ApplicationException
	{
		boolean lbStatus = false;
		HashMap<String, String> loReqdMap = new HashMap<String, String>();

		try
		{
			loReqdMap.put(HHSConstants.BULK_UPLOAD_FILE_MODIFIED_BY, HHSConstants.BULK_UPLOAD_SYSTEM_USER);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_APP_SETTING_NAME, asBulkUploadId);
			loReqdMap.put(HHSConstants.BULK_UPLOAD_SYSTEM_FAILURE, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.BULK_UPLOAD_SYSTEM_FAILURE));
			DAOUtil.masterDAO(aoMyBatisSession, loReqdMap, HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
					HHSConstants.TRANSACTION_BULK_SYSTEM_FAILURE, HHSConstants.JAVA_UTIL_HASH_MAP);
			lbStatus = true;
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception occured in FinancialsService: updateDocumentStatusInDB method:: ", loExp);
			setMoState("ApplicationException while executing updateDocumentStatusInDB method ");
			ApplicationException loEx = new ApplicationException("Error occured while executing service.", loExp);
			throw loEx;
		}
		return lbStatus;
	}

}
