package com.batch.bulkupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class BulkUploadBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(BulkUploadBatch.class);

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@SuppressWarnings("rawtypes")
	public List<CityUserDetailsBeanForBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * This is the main method of the batch, where execution starts. This method
	 * picks up
	 * 
	 * files from file system which has the status of "Not processed" or
	 * "In Progress" and passess these files to
	 * 
	 * TemplateProcessImpl object one by one which in turn process the records
	 * of the files passed to it.
	 * 
	 * @param a
	 */	
	@SuppressWarnings("unchecked")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{

		Channel loChannel = null;
		P8UserSession loP8UserSession = null;
		String lsFileStatus = HHSConstants.EMPTY_STRING;
		String lsFileUploadId = HHSConstants.EMPTY_STRING;
		String lsFileUploadedByUser = HHSConstants.EMPTY_STRING;
		FileInputStream loFileInputStream = null;
		FileInputStream loTemplateFile = null;
		Boolean loNotExecuted = false;
		String lsFileId = HHSConstants.EMPTY_STRING;
		String lsCompleteFileName = HHSConstants.EMPTY_STRING;
		
		try
		{
			BulkUploadBatch loBulkUploadBatch = new BulkUploadBatch();
			loBulkUploadBatch.loadTaskAuditXml();
			loP8UserSession = getFileNetSession();
			loChannel = new Channel();
			ArrayList<BulkUploadFileInformation> loAllUnProcessedFile = null;
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRANSACTION_GET_BULK_UNPROCESSED_FILE);
			loAllUnProcessedFile = (ArrayList<BulkUploadFileInformation>) loChannel
					.getData(HHSConstants.LIST_BULK_UNPROCESSED_FILE);
			for (BulkUploadFileInformation loFile : loAllUnProcessedFile)
			{
				try
				{
					loNotExecuted = false;
					boolean lbValidDataFile = false;
					loChannel = new Channel();
					loTemplateFile = downloadTemplateFile(loP8UserSession);
					lsFileId = loFile.getDocumentId();
					lsFileStatus = loFile.getFileStatus();
					lsFileUploadId = loFile.getBulkUploadId();
					lsFileUploadedByUser = loFile.getCreatedBy();
					lsCompleteFileName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH) + lsFileId;
					loFileInputStream = new FileInputStream(lsCompleteFileName);
					String lsVersion = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.BULK_UPLOAD_IMPL_VERSION);
					ProcessBulkUploadContracts loTemplateProcess = TemplateProcessFactory
							.getTemplateProcessObj(lsVersion);
					loTemplateProcess.setDataFileObj(loFileInputStream);
					loTemplateProcess.setTemplateFileObj(loTemplateFile);
					
					try{
						lbValidDataFile = loTemplateProcess.validateBulkContractSpreadsheet();
					}catch(Exception e){
						LOG_OBJECT.Error("Exception occured in BulkUploadBatch::lbValidDataFile", e.getMessage());						
					}					
					if (!lbValidDataFile)
					{
						lsFileStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.BULK_UPLOAD_TEMPLATE_MISMATCH);
						//changedFileStatus(loChannel, lsFileId, lsFileStatus);
					}
					else
					{
						if (null != loFileInputStream)
						{
							loFileInputStream.close();
						}
						// We are again creating this input stream because jvm
						// unable to
						// perform any operation on the same input stream twice.
						loFileInputStream = new FileInputStream(lsCompleteFileName);
						loTemplateProcess.setDataFileObj(loFileInputStream);
						loTemplateProcess.setFileUploadedByUser(lsFileUploadedByUser);
						loTemplateProcess.setFileUploadId(lsFileUploadId);
						if (!(lsFileStatus.equalsIgnoreCase(HHSConstants.BULK_UPLOAD_FILE_STATUS_IN_PROGRESS)))
						{
							lsFileStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_BULK_UPLOAD_IN_PROGRESS);
							changedFileStatus(loChannel, lsFileId, lsFileStatus);
						}
						HHSTransactionManager.executeTransaction(loChannel,
								HHSConstants.TRANSACTION_BULK_CONTRACT_UPLOAD_FILE_LOCK_STATUS_UPDATE);
						loTemplateProcess.setMoUserSession(loP8UserSession);

						String lsProcessStatus = loTemplateProcess.processData();
						lsFileStatus = HHSConstants.EMPTY_STRING;
						lsFileStatus = setFileStatus(lsFileStatus, lsProcessStatus);
						
					}
				}
				catch (IOException aoFileEx)
				{
					loNotExecuted = true;
					lsFileStatus= PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,HHSConstants.STATUS_BULK_UPLOAD_FAIL);
					LOG_OBJECT.Error("Exception occured in BulkUploadBatch: Main method::", aoFileEx.getMessage());
				}
				catch (ApplicationException aoAppEx)
				{
					lsFileStatus=PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,HHSConstants.STATUS_BULK_UPLOAD_FAIL);
					LOG_OBJECT.Error("Exception occured in BulkUploadBatch: Main method::", aoAppEx.getMessage());
				}
				catch (Exception aoAppEx)
				{
					lsFileStatus=PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,HHSConstants.STATUS_BULK_UPLOAD_FAIL);
					loNotExecuted = true;
					LOG_OBJECT.Error("Exception occured in BulkUploadBatch: Main method::", aoAppEx.getMessage());
				}
				//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
				finally
				{
					changedFileStatus(loChannel, lsFileId, lsFileStatus);
					setNotificationToUsers(lsFileStatus, lsFileUploadId, loFile);
					LOG_OBJECT.Debug("Excel read successfully at path:", lsCompleteFileName);				
					try
					{
						if (null != loFileInputStream)
						{
							loFileInputStream.close();
						}
					}
					catch (IOException aoIOEx)
					{
						LOG_OBJECT.Error(
								"Exception occured in BulkUploadBatch: Main method while closing Input Stream::",
								aoIOEx);
					}
				}
				if (loNotExecuted)
				{
					loChannel.setData(HHSConstants.BULK_UPLOAD_APP_SETTING_NAME, lsFileUploadId);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRANSACTION_BULK_SYSTEM_FAILURE);
				}
				//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in BulkUploadBatch: Main method::", aoAppEx);
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadBatch: Main method::", aoAppEx);
		}

	}
	
	/**
	 * This method is used to send the notification for bulk upload success or
	 * failure.
	 * <ul>
	 * <li>it will execute the transaction <b>insertNotificationDetailBulk</b>
	 * and insert all notification details into notification table</li>
	 * </ul>
	 * 
	 * @param asFileStatus Status of the file processed or not
	 * @param asFileUploadId uploaded file id
	 * @param aoFile uploaded file information
	 * @throws ApplicationException if any exception occurred
	 */
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	private void setNotificationToUsers(String asFileStatus, String asFileUploadId, BulkUploadFileInformation aoFile)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		
		loChannel.setData(HHSConstants.BULK_UPLOAD_DOC_ID, asFileUploadId);		
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			
		if (asFileStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BULK_UPLOAD_FAIL))
				|| asFileStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BULK_UPLOAD_SUCCESS_WITH_ERROR))
				|| asFileStatus.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BULK_UPLOAD_SUCCESS)))
		{
			loNotificationAlertList.add(HHSConstants.NT_BULK_UPLOAD);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSConstants.NT_BULK_UPLOAD, loNotificationDataBean);
		} 
		//other status, like template mismatch, unknown system error, use a different email template
		else{
		
			loNotificationAlertList.add(HHSConstants.NT_BULK_UPLOAD_ERR);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSConstants.NT_BULK_UPLOAD_ERR, loNotificationDataBean);
		}
				
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, asFileUploadId);
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BULK_UPLOAD_ENTITY_TYPE);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		loRequestMap.put(HHSConstants.BULK_FILE_NOTIFICATION_NAME, aoFile.getFileName());
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.TRANSACTION_BULK_SEND_NOTIFICATION);

	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	/**
	 * This method is used to decide status for the bulk upload file
	 * 
	 * @param asFileStatus String file status whether processed or not
	 * @param asProcessStatus String process status
	 * @return String status of the document after calculation
	 * @throws ApplicationException if any exception occurred
	 */
	private static String setFileStatus(String asFileStatus, String asProcessStatus) throws ApplicationException
	{
		if (asProcessStatus.equals(HHSConstants.BULK_UPLOAD_FILE_STATUS_SUCCESS))
		{
			asFileStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BULK_UPLOAD_SUCCESS);
		}
		else if (asProcessStatus.equals(HHSConstants.BULK_UPLOAD_FILE_STATUS_SUCCESS_WITH_ERROR))
		{
			asFileStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BULK_UPLOAD_SUCCESS_WITH_ERROR);
		}
		else if (asProcessStatus.equals(HHSConstants.BULK_UPLOAD_FILE_STATUS_FAILED))
		{
			asFileStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.STATUS_BULK_UPLOAD_FAIL);

		}
		return asFileStatus;
	}

	/**
	 * This method change the status of file for which the file id is passed
	 * 
	 * @param aoChannel -Channel object
	 * @param lsFileId -File id of the file whose status to be changed
	 * @param lsFileStatus -Status that need to be updated.
	 * @throws ApplicationException
	 */
	private static void changedFileStatus(Channel aoChannel, String asFileId, String asFileStatus)
			throws ApplicationException
	{
		try
		{
			aoChannel.setData(HHSConstants.BULK_UPLOAD_DOC_ID, asFileId);
			aoChannel.setData(HHSConstants.BULK_UPLOAD_FILE_STATUS, asFileStatus);
			HHSTransactionManager.executeTransaction(aoChannel,
					HHSConstants.TRANSACTION_BULK_CONTRACT_UPLOAD_FILE_STATUS_UPDATE);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured in BulkUploadBatch: changedFileStatus method while closing Input Stream::",
					aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This static method provides the File net session to the batch.
	 * 
	 * @return P8Usersession bean object with all details to create P8Session
	 * @throws ApplicationException if any exception occurred
	 */
	@SuppressWarnings("unchecked")
	public static P8UserSession getFileNetSession() throws ApplicationException
	{

		SqlSession loFilenetPEDBSession = null;
		P8UserSession loUserSession = null;
		try
		{
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loUserSession = new P8SecurityOperations().setP8SessionVariables();
			loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
			String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + HHSConstants.UNDERSCORE
					+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			loUserSession.setObjectsAllowedPerPage(loApplicationSettingMap.get(lsAppSettingMapKey));
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadBatch: getFileNetSession method::", aoAppEx);
			throw aoAppEx;
		}

		return loUserSession;
	}

	/**
	 * This method down load the Template for bulk upload batch against the bulk
	 * upload file has to validated
	 * 
	 * @param aoP8UserSession P8Usersession bean object
	 * @return inputstream of the file content from filenet
	 * @throws ApplicationException when any exception occurred
	 */
	@SuppressWarnings("unchecked")
	private static FileInputStream downloadTemplateFile(P8UserSession aoP8UserSession) throws ApplicationException
	{
		Channel loChannel1 = null;
		InputStream loFiledata = null;
		FileInputStream loFileInputStream = null;
		FileOutputStream loFileOutput = null;
		try
		{
			loChannel1 = new Channel();
			loChannel1.setData(HHSConstants.AO_FILENET_SESSION, aoP8UserSession);
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			loFilterProps.put(HHSConstants.IS_BULK_UPLOAD, true);
			HashMap<String, String> loHmReqProps = new HashMap<String, String>();
			loHmReqProps.put(HHSConstants.TEMPLATE_VERSION_NO, HHSConstants.EMPTY_STRING);
			loHmReqProps.put(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE, HHSConstants.EMPTY_STRING);
			loHmReqProps.put(HHSConstants.TEMPLATE_ID, HHSConstants.EMPTY_STRING);
			List<HashMap<String, Object>> loDocumentList = getDocumentList(loChannel1, null, loHmReqProps,
					loFilterProps, true);
			if (null != loDocumentList)
			{
				Map loDocProps = loDocumentList.get(0);
				String lsTemplateVersion = loDocProps.get(HHSConstants.TEMPLATE_VERSION_NO).toString();
				String lsLastModDate = DateUtil.getDateMMddYYYYFormat((Date) loDocProps
						.get(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE));
				String lsDocId = (String) loDocProps.get(HHSConstants.TEMPLATE_IDEN).toString();
				loHmReqProps.put(HHSConstants.TEMPLATE_VERSION_NO, lsTemplateVersion);
				loHmReqProps.put(HHSConstants.TEMPLATE_LAST_MODIFIED_DATE, lsLastModDate);
				loHmReqProps.put(HHSConstants.TEMPLATE_ID, lsDocId);
			}

			String asFileId = loHmReqProps.get(HHSConstants.TEMPLATE_ID);
			loChannel1.setData(HHSConstants.BULK_UPLOAD_DOCUMENT_ID, asFileId);
			loChannel1.setData(HHSConstants.AO_USER_SESSION, aoP8UserSession);
			HHSTransactionManager.executeTransaction(loChannel1, HHSConstants.TRANSACTION_GET_BULK_DOCUMENT_FILENET);
			HashMap loMap = (HashMap) loChannel1.getData(HHSConstants.BULK_UPLOAD_DOCUMENT_CONTENT);
			String lsFileExtn = (String) loMap.get(HHSConstants.FILE_TYPE);
			loFiledata = (InputStream) loMap.get(HHSConstants.CONTENT_ELEMENT);
			String lsTempDownloadDir = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH);
			File loTempDir = new File(lsTempDownloadDir);
			if (!loTempDir.exists())
			{
				loTempDir.mkdirs();
			}
			String lsTempDownloadFileName = HHSConstants.TEMPLATE_TEMP_FILE_NAME + HHSConstants.DOT + lsFileExtn;
			String lsCompleteFileName = lsTempDownloadDir + lsTempDownloadFileName;
			File loTempFile = new File(lsCompleteFileName);
			if (!loTempFile.exists())
			{
				loTempFile.createNewFile();
			}
			loFileInputStream = new FileInputStream(loTempFile);
			loFileOutput = new FileOutputStream(loTempFile);
			byte loByteStream[] = FileNetOperationsUtils.convert(loFiledata);
			loFileOutput.write(loByteStream);
			loFileOutput.flush();
		}
		catch (IOException aoIOEx)
		{
			LOG_OBJECT.Error("IOException occured in BulkUploadBatch: downloadTemplateFile method::", aoIOEx);

			throw new ApplicationException("IOException occured in BulkUploadBatch: downloadTemplateFile method::",
					aoIOEx);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in BulkUploadBatch: downloadTemplateFile method::", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in BulkUploadBatch: downloadTemplateFile method::", aoAppEx);
			throw new ApplicationException("Exception occured in BulkUploadBatch: downloadTemplateFile method::",
					aoAppEx);
		}

		return loFileInputStream;
	}

	/**
	 * This method is used to fetch the document property from filenet
	 * <ul>
	 * <li>create the required parameter map with empty string values and set it
	 * to channel object</li>
	 * <li>execute transaction <b>displayDocList_filenet_bulk</b> to fetch the
	 * document properties from filenet</li>
	 * </ul>
	 * 
	 * @param aoChannel channel object to execute transaction
	 * @param asDocType document type string value
	 * @param aoRequiredProps required property map
	 * @param aoFilterProps filter property map
	 * @param abIncludeFilter include filter
	 * @return list of documents with property
	 * @throws ApplicationException if exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	private static List getDocumentList(Channel aoChannel, String asDocType, HashMap aoRequiredProps,
			HashMap aoFilterProps, boolean abIncludeFilter) throws ApplicationException
	{
		aoChannel.setData(HHSConstants.DOCTYPE, asDocType);
		aoChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, aoRequiredProps);
		aoChannel.setData(HHSConstants.FILENET_FILTER_MAP, aoFilterProps);
		aoChannel.setData(HHSConstants.INCLUDE_FILENET_FILTER, abIncludeFilter);
		HHSTransactionManager.executeTransaction(aoChannel, HHSConstants.TRANSACTION_BULK_DISPLAY_DOC_LIST);
		return (List) aoChannel.getData(HHSConstants.BULK_DISPLAY_DOC_LIST);
	}

	/**
	 * This method puts the TaskAuditConfiguration object in Cache.
	 * 
	 * @throws ApplicationException
	 */
	private void loadTaskAuditXml() throws ApplicationException
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheNotificationObject3 = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSConstants.BULK_UPLOAD_TASK_AUDIT_ELEMENT_PATH));
			loCacheManager.putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, loCacheNotificationObject3);
			synchronized (BulkUploadBatch.class)
			{
				loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING,
						HHSUtil.getApplicationSettingsBulk());
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error Occured while loading Transaction Xml: loadTaskAuditXml", loEx);
			throw new ApplicationException("Error Occured while loading Transaction Xml", loEx);
		}
	}
}
