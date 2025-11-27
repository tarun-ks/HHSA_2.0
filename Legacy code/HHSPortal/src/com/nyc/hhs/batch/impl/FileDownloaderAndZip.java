package com.nyc.hhs.batch.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.BulkDownloadBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * Added for Release 5 This class is used to create zip of documents for
 * downloading.
 */
public class FileDownloaderAndZip extends P8HelperServices implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(FileDownloaderAndZip.class);

	@Override
	public List getQueue(Map aoMParameters)
	{

		return null;
	}

	/**
	 * This method adds folder
	 * @param zos ZipOutputStream object
	 * @param folderName
	 * @param baseFolderName
	 * @throws Exception
	 */
	private static void addFolder(ZipOutputStream zos, String folderName, String baseFolderName) throws Exception
	{
		File f = new File(folderName);
		if (f.isDirectory())
		{
			if (!folderName.equalsIgnoreCase(baseFolderName))
			{
				String entryName = folderName.substring(baseFolderName.length() + 1, folderName.length())
						+ File.separatorChar;
				ZipEntry ze = new ZipEntry(entryName);
				zos.putNextEntry(ze);
			}
			File f2[] = f.listFiles();
			for (int i = 0; i < f2.length; i++)
			{
				addFolder(zos, f2[i].getAbsolutePath(), baseFolderName);
			}
		}
		else
		{
			String entryName = folderName.substring(baseFolderName.length() + 1, folderName.length());
			ZipEntry ze = new ZipEntry(entryName);
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(folderName);
			int len;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) > 0)
			{
				zos.write(buffer, 0, len);
			}
			in.close();
			zos.closeEntry();
		}
	}

	/**
	 * This method execute queue by taking a list as input aoLQueue a List
	 * loChannelZip a channel object loExp an Exception object
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		HashMap<String, HashMap> loDocumentFileNetContentMap = new HashMap<String, HashMap>();
		HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
		HashMap<String, String> loOrgMap = new HashMap<String, String>();

		List<String> loNotificationAlertList = new ArrayList<String>();
		List<BulkDownloadBean> aoDownloadBean = new ArrayList<BulkDownloadBean>();

		Channel loChannel = new Channel();

		// filter to check folder in a folder
		FileFilter loFolderF = new FileFilter()
		{
			@Override
			public boolean accept(File loBaseFolder)
			{
				return loBaseFolder.isDirectory();
			}
		};

		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			fetchProviderAndAgencyDetails(loOrgMap, loChannel);

			loChannel.setData("aoFilenetSession", loFilenetSession);
			loChannel.setData("lsStatusId", "2");

			HHSTransactionManager.executeTransaction(loChannel, "getDownloadStatList",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			aoDownloadBean = (List<BulkDownloadBean>) loChannel.getData("loBulkDownloadList");

			Iterator loBeanItr = aoDownloadBean.iterator();
			while (loBeanItr.hasNext())
			{
				BulkDownloadBean loDownLoadRequestBean = (BulkDownloadBean) loBeanItr.next();
				List<DefaultAssignment> loFinalOutputBean = null;
				HashMap<String,Object> loMap = new HashMap<String, Object>();
				long lsTimeStamp = System.currentTimeMillis();
				File loBaseFolder = new File(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.ZIP_DOCUMENTS_PATH) +File.separator+"DownloadAllZip"+loDownLoadRequestBean.getMsDownloadId()+lsTimeStamp);
				int liFolderCounter = 0;
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(HHSR5Constants.APPLICATION_SETTING);
				Double loMaxFolderSize = Double.valueOf(loApplicationSettingMap
						.get(HHSR5Constants.MAX_FOLDER_SIZE_FOR_BATCH));

				DefaultAssignment loDocumentDetailsBean = new DefaultAssignment();
				

				// Fetch org details from search criteria
				loChannel.setData("aoFilenetSession", loFilenetSession);
				loChannel.setData("bulkDownloadBean", loDownLoadRequestBean);
				HHSTransactionManager.executeTransaction(loChannel, "OrgListcontent_filenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				List<String> loOrgListData = (List<String>) loChannel.getData("loOrgListData");
				SimpleDateFormat loMyFormat = new SimpleDateFormat("MMddyyyy-HHmmss");
				String lsDate = loMyFormat.format(loDownLoadRequestBean.getMsCreatedDate());
				File loDirPath = new File(loBaseFolder.getAbsolutePath() + File.separator
						+ HHSR5Constants.ZIP_FILE_NAME + lsDate);
				loDirPath.mkdir();
				String lsDirAbsolutePath = loDirPath.getAbsolutePath();
				for (Iterator iterator = loOrgListData.iterator(); iterator.hasNext();)
				{
					String lsOrgId = (String) iterator.next();
					loDownLoadRequestBean.setOrgId(lsOrgId);
					loChannel.setData("bulkDownloadBean", loDownLoadRequestBean);
					loChannel.setData("OrganizationMap", loOrgMap);
					HHSTransactionManager.executeTransaction(loChannel, "documentListcontent_filenet",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					loDocumentFileNetContentMap = (HashMap) loChannel.getData("loOutputHashMap");

					Iterator loDocItr = loDocumentFileNetContentMap.keySet().iterator();
					loFinalOutputBean = new ArrayList<DefaultAssignment>();
					HashMap<File, Double> loHashMapData = new HashMap<File, Double>();
					
					while (loDocItr.hasNext())
					{
						String loElement = (String) loDocItr.next();
						HashMap loHashMap = loDocumentFileNetContentMap.get(loElement);
						loDocumentDetailsBean = new DefaultAssignment();
						loDocumentDetailsBean.setDocTitle((String) loHashMap.get(HHSR5Constants.DOCUMENT_TITLE));
						loDocumentDetailsBean.setFileType((String) loHashMap.get("FILE_TYPE"));
						loDocumentDetailsBean.setOrgName((String) loHashMap.get("Org_Legal_Name"));
						loDocumentDetailsBean.setContentSize((Double) loHashMap.get("ContentSize"));
						loDocumentDetailsBean.setDocumentId(loElement);
						loDocumentDetailsBean.setContentElements((InputStream) loHashMap.get("ContentElements"));
						loDocumentDetailsBean.setBaseFolderName(lsDirAbsolutePath);
						loFinalOutputBean.add(loDocumentDetailsBean);
					}
					// Collections.sort(loFinalOutputBean);

					loMap = createFolderForZip(loDirPath, loMaxFolderSize, loFinalOutputBean, loHashMapData, liFolderCounter);
					liFolderCounter = (Integer)loMap.get("folderCounter");
					loDirPath = (File)loMap.get("dirPath");
					LOG_OBJECT.Info("CreateFolderFor" + liFolderCounter);
					// Adding entry into Zip Table

				}
				List<String> loZipList = createZipFile(loFolderF, loBaseFolder);
				Channel loChannelZip = new Channel();
				loChannelZip.setData("requestID", loDownLoadRequestBean.getMsDownloadId());
				loChannelZip.setData("zipFilePath", loZipList);
				HHSTransactionManager.executeTransaction(loChannelZip, "setentryintoZipTable",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				// For sending notification

				loNotificationAlertList.add(HHSR5Constants.NT232);
				loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
				NotificationDataBean loNotificationAL232 = new NotificationDataBean();
				HashMap<String, String> loRequestMap = new HashMap<String, String>();
				HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
				createMapForNotification(loDownLoadRequestBean, loRequestMap);
				Channel loChannelZipp = new Channel();
				loChannelZipp.setData("requestId", loDownLoadRequestBean.getMsDownloadId());
				HHSTransactionManager.executeTransaction(loChannelZipp, "getZipIdFromDb",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				List<String> loZipIdList = (List<String>) loChannelZipp.getData("loZipList");
				StringBuffer loSbMessage = new StringBuffer();
				String lsAppProtocol = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						HHSConstants.PROP_CITY_URL);
				int liCounter = 1;
				for (Iterator iterator = loZipIdList.iterator(); iterator.hasNext();)
				{

					String lsZipId = (String) iterator.next();
					loSbMessage.append("<a href=");
					loSbMessage.append(lsAppProtocol);
					loSbMessage.append("/DownloadAllBatch");
					loSbMessage.append("?requestId=");
					loSbMessage.append(loDownLoadRequestBean.getMsDownloadId());
					loSbMessage.append("&requestedZipId=");
					loSbMessage.append(lsZipId);
					loSbMessage.append(">Zip File ");
					loSbMessage.append(liCounter);
					loSbMessage.append("</a>");
					loSbMessage.append("</BR>");
					liCounter++;
				}

				StringBuffer loSbMessageUrl = new StringBuffer();
				String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						HHSConstants.PROP_CITY_URL);
				loSbMessageUrl.append("<a href=").append(lsServerName)
						.append(ApplicationConstants.PORTAL_URL);
				loSbMessageUrl.append("\">HHS Accelerator</a>");

				loRequestMap.put("ACCELERATORLINK", loSbMessageUrl.toString());
				loRequestMap.put("DOCUMENTTYPE", "Document Type: "+loDownLoadRequestBean.getMsFilterDocType());
				loRequestMap.put("DOWNLOADLINK", loSbMessage.toString());
				loNotificationAL232.setAgencyLinkMap(loAgencyLinkMap);
				loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
				loHmNotifyParam.put(ApplicationConstants.ENTITY_ID, loDownLoadRequestBean.getMsDownloadId());
				loHmNotifyParam.put(ApplicationConstants.ENTITY_TYPE, "");
				loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, "system");
				loHmNotifyParam.put(HHSConstants.MODIFIED_BY, "system");
				loHmNotifyParam.put(HHSR5Constants.AL232, loNotificationAL232);
				loHmNotifyParam.put(HHSR5Constants.NT232, loNotificationAL232);
				loChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loHmNotifyParam);
				TransactionManager.executeTransaction(loChannel, "sendFileDownloadNotification",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				Boolean lbStatus = (Boolean) loChannel.getData("insertStatus");
				if (lbStatus)
				{
					Channel loChannelObj = new Channel();
					loChannelObj.setData("ReqId", loDownLoadRequestBean.getMsDownloadId());
					TransactionManager.executeTransaction(loChannelObj, "updateSuccessFlag",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);

				}
			}
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			throw loExp;
		}
		catch (FileNotFoundException loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			new ApplicationException("Exception in FileDownloadedAndZipBatch : " + loExp.getMessage());
		}
		catch (IOException loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			new ApplicationException("Exception in FileDownloadedAndZipBatch : " + loExp.getMessage());
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			new ApplicationException("Exception in FileDownloadedAndZipBatch : " + loExp.getMessage());
		}
	}

	/**
	 * This method create map for notification
	 * @param loDownLoadRequestBean a BulkDownloadBean object
	 * @param loRequestMap a Hashmap
	 * @throws Exception
	 */
	private void createMapForNotification(BulkDownloadBean loDownLoadRequestBean, HashMap<String, String> loRequestMap)
			throws Exception
	{
		try
		{
			if (StringUtils.isBlank(loDownLoadRequestBean.getMsFilterDocName()))
			{
				loRequestMap.put("DOCUMENTNAME", "");
			}
			else
			{
				loRequestMap.put("DOCUMENTNAME", "Document Name: "+loDownLoadRequestBean.getMsFilterDocName());
			}

			if (null != loDownLoadRequestBean.getMsModifiedFrom()
					&& !loDownLoadRequestBean.getMsModifiedFrom().toString().isEmpty())
			{
				loRequestMap.put("MODIFIEDFROM", "Modified Date Range: "+loDownLoadRequestBean.getMsModifiedFrom().toString());
			}
			else
			{
				loRequestMap.put("MODIFIEDFROM", "");
			}

			if (null != loDownLoadRequestBean.getMsModifiedTo()
					&& !loDownLoadRequestBean.getMsModifiedTo().toString().isEmpty())
			{

				loRequestMap.put("MODIFIEDTO", " to "+loDownLoadRequestBean.getMsModifiedTo().toString());
			}
			else
			{
				loRequestMap.put("MODIFIEDTO", "");
			}

			if (StringUtils.isBlank(loDownLoadRequestBean.getMsAgentId()))
			{
				loRequestMap.put("SHAREDWITH", "");
			}
			else
			{
				loRequestMap.put("SHAREDWITH", "Shared With: "+loDownLoadRequestBean.getMsAgentId());
			}
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(loExp.getMessage());
			throw loExp;
		}
	}

	/**
	 * This method create folder for zip
	 * @param loBaseFolder a File object
	 * @param liFolderCounter
	 * @param loMaxFolderSize
	 * @param loFinalOutputBean
	 * @param loHashMap
	 * @return liFolderCounter
	 * @throws Exception
	 */

	private HashMap<String,Object> createFolderForZip(File loBaseFolder, double loMaxFolderSize,
			List<DefaultAssignment> loFinalOutputBean, HashMap<File, Double> loHashMap, int aiFolderCounter) throws Exception
	{
		File loCurrentFolder;
		HashMap<String,Object> loMap = new HashMap<String, Object>();
		try
		{
			LOG_OBJECT.Error("BaseFolderPath:::::::::::"+loBaseFolder.getAbsolutePath());
			for (DefaultAssignment loFinalOutputBeanObj : loFinalOutputBean)
			{
				if (null != loFinalOutputBeanObj)
				{
					loCurrentFolder = new File(loBaseFolder.getAbsolutePath());
					if (loFinalOutputBeanObj.getOrgName() != null && !loFinalOutputBeanObj.getOrgName().isEmpty())
					{
						File loDirPath = new File(loCurrentFolder.getAbsolutePath() + File.separator
								+ loFinalOutputBeanObj.getOrgName());
						LOG_OBJECT.Error("Directory_Path::::::::"+loDirPath.getAbsolutePath());
						if (!loDirPath.exists())
						{
							loDirPath.mkdirs();
						}
						
						LOG_OBJECT.Error("base folder size::::::"+new BigDecimal(FileUtils.sizeOfDirectory(loBaseFolder)));
						LOG_OBJECT.Error("Content Size::::::::"+new BigDecimal(loFinalOutputBeanObj.getContentSize()));
						LOG_OBJECT.Error("Content Size after addition::::::::"+new BigDecimal(FileUtils.sizeOfDirectory(loBaseFolder)).add(new BigDecimal(loFinalOutputBeanObj.getContentSize())));
						LOG_OBJECT.Error("Cache size:::::"+loMaxFolderSize);
						int val = new BigDecimal(FileUtils.sizeOfDirectory(loBaseFolder)).add(new BigDecimal(loFinalOutputBeanObj.getContentSize())).compareTo(new BigDecimal(loMaxFolderSize));
						if (val == 1) 
						{
							aiFolderCounter++;
							loDirPath = new File(loFinalOutputBeanObj.getBaseFolderName() + "_" + aiFolderCounter
									+ File.separator + loFinalOutputBeanObj.getOrgName());
							loDirPath.mkdirs();
							loBaseFolder = new File(loFinalOutputBeanObj.getBaseFolderName() + "_" + aiFolderCounter);
							LOG_OBJECT.Error("new Base Folder::::::"+loBaseFolder.getAbsolutePath());
						}

						createFileWithContent(loFinalOutputBeanObj, loDirPath);
					}
				}
			}
			loMap.put("folderCounter", aiFolderCounter);
			loMap.put("dirPath", loBaseFolder);
		}
		catch (Exception loExp)
		{
			throw loExp;
		}
		return loMap;
	}

	/**
	 * This method create zip file
	 * @param loFolderF
	 * @param loBaseFolder
	 * @return loZipPathList
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	private List<String> createZipFile(FileFilter loFolderF, File loBaseFolder) throws FileNotFoundException,
			Exception, IOException
	{
		// Start of Zip

		File[] loFolderList = loBaseFolder.listFiles(loFolderF);
		List<String> loZipPathList = new ArrayList<String>();
		if (null != loFolderList && loFolderList.length > 0)
		{
			for (int l = 0; l < loFolderList.length; l++)
			{
				String lsSrc = loFolderList[l].getAbsolutePath();
				String lsDest = lsSrc + ".zip";
				FileOutputStream fos;
				fos = new FileOutputStream(lsDest);
				ZipOutputStream zos = new ZipOutputStream(fos);
				addFolder(zos, lsSrc, lsSrc);
				zos.close();
				loZipPathList.add(lsDest);
			}
		}
		return loZipPathList;
	}

	/**
	 * This method create file with content
	 * @param loFinalOutputBeanObj
	 * @param loDirPath
	 * @return bytesRead
	 * @throws Exception
	 */
	private int createFileWithContent(DefaultAssignment loFinalOutputBeanObj, File loDirPath) throws Exception
	{
		int bytesRead;
		try
		{
			File loFile = new File(loDirPath.getAbsolutePath() + "/" + loFinalOutputBeanObj.getDocTitle() + "."
					+ loFinalOutputBeanObj.getFileType().toLowerCase());

			OutputStream loOutputStream = new FileOutputStream(loFile.getAbsolutePath());
			InputStream loInputStream = loFinalOutputBeanObj.getContentElements();
			byte[] buffer = new byte[1024];

			while ((bytesRead = loInputStream.read(buffer)) != -1)
			{
				loOutputStream.write(buffer, 0, bytesRead);
			}
			loOutputStream.close();
			loInputStream.close();
		}
		catch (Exception loExp)
		{
			throw loExp;
		}
		return bytesRead;
	}

	/**
	 * This method fetch provider and agency details
	 * @param loOrgMap
	 * @param loChannel
	 * @throws ApplicationException
	 */
	private void fetchProviderAndAgencyDetails(HashMap loOrgMap, Channel loChannel) throws ApplicationException
	{
		List<OrganizationBean> loOrgBeanList;
		TransactionManager.executeTransaction(loChannel, "getOrg_DBForBatch", HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loOrgBeanList = (List<OrganizationBean>) loChannel.getData("loOrganizationBean");

		Iterator loOrgBeanItr = loOrgBeanList.iterator();
		while (loOrgBeanItr.hasNext())
		{
			OrganizationBean loOrgBean = (OrganizationBean) loOrgBeanItr.next();
			String lsName = loOrgBean.getMsOrgLegalName().concat(ApplicationConstants.UNDERSCORE)
					.concat(loOrgBean.getMsEinId());
			lsName = lsName.replaceAll("[.\\/:*?\"<>|]?[\\\\/:*?\"<>|]*", "");
			loOrgMap.put(loOrgBean.getMsOrgId(), lsName);
		}

		TransactionManager.executeTransaction(loChannel, "getAgencyList_DBForBatch",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		List<ProviderBean> loAgencyList = (List) loChannel.getData("agencyList");
		if (!CollectionUtils.isEmpty(loAgencyList))
		{
			Iterator<ProviderBean> loIter = loAgencyList.iterator();
			while (loIter.hasNext())
			{
				ProviderBean loProviderBean = loIter.next();
				String lsName = loProviderBean.getDisplayValue();
				lsName = lsName.replaceAll("[.\\\\/:*?\"<>|]?[\\\\/:*?\"<>|]*", "");
				loOrgMap.put(loProviderBean.getHiddenValue(), lsName);
			}
		}
	}
}
