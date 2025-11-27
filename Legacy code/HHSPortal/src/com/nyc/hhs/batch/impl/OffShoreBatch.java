package com.nyc.hhs.batch.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.ibatis.session.SqlSession;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.XMLUtil;

public class OffShoreBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(OffShoreBatch.class);

	@Override
	public List getQueue(Map aoMParameters)
	{
		String args[] = new String[3];
		args = (String[]) aoMParameters.get("argumentsVal");
		List loQueue = new LinkedList();
		for (String lsArgument : args)
		{
			loQueue.add(lsArgument);
		}
		return loQueue;
	}

	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		Channel loChannel = new Channel();
		P8UserSession loP8UserSession = new P8UserSession();
		FileInputStream fis = null;
		HashMap<String, Object> aoPropertyMap = new HashMap<String, Object>();
		HashMap<String, List<String>> providerAgencyMap = new HashMap<String, List<String>>();
		HashMap<String, String> loDocumentMap = new HashMap<String, String>();
		List<String> temp = new ArrayList<String>();
		String parentFolderId = null;
		HashMap<String, List<String>> lodeleteMap = new HashMap<String, List<String>>();
		P8SecurityOperations filenetConnection = new P8SecurityOperations();
		List<String> folderList = new ArrayList<String>();
		try
		{
			if (null != aoLQueue.get(0) && !aoLQueue.get(0).equals(""))
			{
				if (aoLQueue.get(0).toString().equalsIgnoreCase("bulkFilenetUpload"))
				{
					long lostartTime = System.currentTimeMillis();
					LOG_OBJECT.Info("OffShoreBatch Batch Started:::::::::" + lostartTime);
					BaseCacheManagerWeb.getInstance().putCacheObject(
							ApplicationConstants.FILENETDOCTYPE,
							XMLUtil.getDomObj((new OffShoreBatch()).getClass().getResourceAsStream(
									"/com/nyc/hhs/config/DocType.xml")));
					int lsCreationCount = Integer.parseInt((String) aoLQueue.get(1));
					String lsPathToStore = (String) aoLQueue.get(2);
					String lsUserOrgType = (String) aoLQueue.get(3);
					String lsUserId = (String) aoLQueue.get(4);
					for (int lsIndex = 1; lsIndex <= lsCreationCount; lsIndex++)
					{
						aoPropertyMap = createPropertMap(lsIndex, lsPathToStore, lsUserOrgType, lsUserId);
						
						File loFile = new File("Text_upload_file.txt");
						if (!loFile.exists())
						{
							loFile.createNewFile();
						}

						FileWriter fw = new FileWriter(loFile.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write("File contents");
						bw.close();
						fis = new FileInputStream(loFile);
						P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
						long loFilenetConnectionStartTime = System.currentTimeMillis();
						loP8UserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection
								.setP8SessionVariables());
						LOG_OBJECT.Info("Time Taken to establish a Filenet connection:::::"
								+ (System.currentTimeMillis() - loFilenetConnectionStartTime));
						ObjectStore loOS = filenetConnection.getObjectStore(loP8UserSession);
						PropertyFilter pr = new PropertyFilter();
						FilterElement loFE = new FilterElement(null, null, null, "ID", null);
						pr.addIncludeProperty(loFE);
						Folder lofolder = Factory.Folder.fetchInstance(loOS, lsPathToStore, pr);
						Id folderID = lofolder.get_Id();
						aoPropertyMap.put("customfolderid", folderID.toString());
						folderList.add(folderID.toString());
						lodeleteMap.put("folder", folderList);
						loChannel.setData("aoFilenetSession", loP8UserSession);
						loChannel.setData("aoIS", fis);
						loChannel.setData("aoPropertyMap", aoPropertyMap);
						loChannel.setData("docExist", false);
						loChannel.setData("checkExist", false);
						HHSTransactionManager.executeTransaction(loChannel, "fileNetBulkUpload",
								HHSR5Constants.TRANSACTION_ELEMENT_R5);
					}
				}
				else if (aoLQueue.get(0).toString().equalsIgnoreCase("bulkFolderUpload"))
				{
					long lostartTime = System.currentTimeMillis();
					LOG_OBJECT.Info("bulkFolderUpload Batch Started:::::::::" + lostartTime);
					BaseCacheManagerWeb.getInstance().putCacheObject(
							ApplicationConstants.FILENETDOCTYPE,
							XMLUtil.getDomObj((new OffShoreBatch()).getClass().getResourceAsStream(
									"/com/nyc/hhs/config/DocType.xml")));
					int lsCreationCount = Integer.parseInt((String) aoLQueue.get(1));
					String lsPathToStore = (String) aoLQueue.get(2);
					String lsUserOrgType = (String) aoLQueue.get(3);
					String lsUserId = (String) aoLQueue.get(4);
					P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
					long loFilenetConnectionStartTime = System.currentTimeMillis();
					loP8UserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection
							.setP8SessionVariables());
					LOG_OBJECT.Info("Time Taken to establish a Filenet connection:::::"
							+ (System.currentTimeMillis() - loFilenetConnectionStartTime));
					ObjectStore loOS = filenetConnection.getObjectStore(loP8UserSession);
					PropertyFilter pr = new PropertyFilter();
					FilterElement loFE = new FilterElement(null, null, null, "ID", null);
					pr.addIncludeProperty(loFE);
					Folder lofolder = Factory.Folder.fetchInstance(loOS, lsPathToStore, pr);
					Id loFolderId = lofolder.get_Id();
					String lsCurrentFolderId = loFolderId.toString();
					for (int lsIndex = 1; lsIndex <= lsCreationCount; lsIndex++)
					{
						loChannel.setData("folderId", lsCurrentFolderId);
						loChannel.setData("lsnewFoldername", "NestedFolderabcdefghijklmnopqr");
						loChannel.setData("userName", "system");
						loChannel.setData("loUserSession", loP8UserSession);
						loChannel.setData("lsUserOrgType", lsUserOrgType);
						loChannel.setData("lsUserOrg", lsUserId);
						HHSTransactionManager.executeTransaction(loChannel,
								HHSR5Constants.CREATE_FOLDER_IN_FILENET_AND_DB, HHSR5Constants.TRANSACTION_ELEMENT_R5);
						List<FolderMappingBean> loMappingBean = (List<FolderMappingBean>) loChannel
								.getData(HHSR5Constants.RETURN_BEAN);
						String lsFolderId = null;
						for (Iterator iterator = loMappingBean.iterator(); iterator.hasNext();)
						{
							FolderMappingBean folderMappingBean = (FolderMappingBean) iterator.next();
							if (null != folderMappingBean
									&& !folderMappingBean.getFolderName().equalsIgnoreCase(
											HHSR5Constants.DOCUMENT_VAULT))
							{
								lsFolderId = folderMappingBean.getFolderFilenetId();
							}
						}
						lsCurrentFolderId = lsFolderId;
					}
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (FileNotFoundException aoExps)
		{
			throw new ApplicationException("Exception", aoExps);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Exception", aoExp);
		}

	}

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
			throw aoAppEx;
		}

		return loUserSession;
	}

	@SuppressWarnings("null")
	public HashMap<String, Object> createPropertMap(Integer asDocName, String asPathToStore, String asUserOrgType,
			String asOrganizationId)
	{
		HashMap<String, Object> aoPropertyMap = new HashMap<String, Object>();
		try
		{
			aoPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "NewDocument" + asDocName);
			if(asUserOrgType.equalsIgnoreCase("city_org"))
			{
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, "Addenda");
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "Solicitation");
			}
			else
			{
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, "A-133");
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "Audit");
			}
			aoPropertyMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, "text/plain");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, asUserOrgType);
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "system");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "system");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "system");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, "system");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, "4/13/2016 1:13:17 PM");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, "TXT");
			aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			aoPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asOrganizationId);
			aoPropertyMap.put(HHSR5Constants.Org_Id, asOrganizationId);

			aoPropertyMap.put("fileName", "s");
		}
		catch (Exception e)
		{

		}
		return aoPropertyMap;
	}
	public ObjectStore getObjectStore(P8UserSession aoUserSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered P8SecurityOperations.getObjectStore()");

		ObjectStore loObjectStore = null;
		HashMap loHmRequiredProp = new HashMap();
		String lsObjectStoreName = aoUserSession.getObjectStoreName();
		loHmRequiredProp.put("aoUserSession", aoUserSession);

		if (lsObjectStoreName == null || lsObjectStoreName.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet ObjectStore name is missing");
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT
					.Error("Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet ObjectStore name is missing");
			throw loAppex;
		}

		try
		{

			// Creating FileNet Connection and retrieving domain
			Domain loDomain = getDomain(aoUserSession);

			// Not able to find object store from name. Fetching the new
			// instance from Factory class.
			if (loObjectStore == null)
			{
				loObjectStore = Factory.ObjectStore.fetchInstance(loDomain, lsObjectStoreName, null);
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoE)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin.",
					aoE);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoE);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin. :",
					aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getObjectStore", aoEx);
		}

		LOG_OBJECT.Debug("Exited P8SecurityOperations.getObjectStore()");
		return loObjectStore;
	}

	private Domain getDomain(P8UserSession aoUserSession) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered P8SecurityOperations.getDomain()");

		Domain loDomain = null;

		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		loHmReqExceProp.put("ContentEngineUri", aoUserSession.getContentEngineUri());
		loHmReqExceProp.put("userName", aoUserSession.getUserId());

		Subject loSubject = null;
		UserContext loUC = UserContext.get();
		Connection loConn = null;

		try
		{
			if (aoUserSession.getContentEngineUri().equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Runtime Error in Fetching Filenet CE Connection.FileNet Content Engine URI is missing");
				loAppex.setContextData(loHmReqExceProp);
				LOG_OBJECT
						.Error("Runtime Error in Fetching Filenet CE Connection.Error in getObjectStore Method. FileNet Content Engine URI is missing");
				throw loAppex;
			}

			if (aoUserSession.getConnection() == null)
			{
				loConn = Factory.Connection.getConnection(aoUserSession.getContentEngineUri());
				aoUserSession.setConnection(loConn);
			}

			if (aoUserSession.getSubject() == null)
			{

				if (aoUserSession.getUserId().equalsIgnoreCase("") || aoUserSession.getPassword().equalsIgnoreCase(""))
				{
					ApplicationException loAppex = new ApplicationException(
							"Runtime Error in Fetching Filenet CE Connection.FileNet Credentials are missing");
					LOG_OBJECT.Debug("Runtime Error in Fetching Filenet CE Connection.FileNet Credentials are missing");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}

				// Creating new Subject for the user using FileNet connection
				// object
				loSubject = UserContext.createSubject(loConn, aoUserSession.getUserId(), aoUserSession.getPassword(),
						null);

				// Pushing the subject in UserContext
				loUC.pushSubject(loSubject);
				aoUserSession.setSubject(loSubject);

			}
			else
			{
				loUC.pushSubject(aoUserSession.getSubject());
			}

			loDomain = Factory.Domain.getInstance(aoUserSession.getConnection(), null);// entireNetwork.get_LocalDomain();

		}
		catch (ApplicationException aoAppex)
		{

			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoE)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Not able to Generate Connection Object", aoE);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoE);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Runtime Error in Fetching Filenet CE Connection.Filenet CE Service Might not be running.Please contact Admin. :",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8SecurityOperations.getDomain()", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Debug("Exited P8SecurityOperations.getDomain()");
		return loDomain;
	}

}
