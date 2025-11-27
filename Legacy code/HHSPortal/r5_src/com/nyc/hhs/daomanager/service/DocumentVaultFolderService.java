package com.nyc.hhs.daomanager.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;

import com.filenet.api.exception.EngineRuntimeException;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BulkDownloadBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is added for Release 5 This class is basically used to handle all
 * the DB based actions related to folder structure and its meta data performed
 * along with file net
 */
public class DocumentVaultFolderService extends ServiceState
{

	/**
	 * Logger Object Declared for DocumentVaultFolderService
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(DocumentVaultFolderService.class);

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert folder structure hierarchy in folder mapping table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderMapList list of folders
	 * @param aoOrganizationType type of organisation
	 * @param aoParameter List of type FolderMappingBean
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 *             commenting throw exception in release 4.0.2 to fix create folder issue for defect # 8408
	 */
	@SuppressWarnings("rawtypes")
	public int insertForFilenet(SqlSession aoMybatisSession, List<FolderMappingBean> aoFolderMapList,
			String aoOrganizationType) throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if (null != aoFolderMapList)
			{
				for (Iterator iterator = aoFolderMapList.iterator(); iterator.hasNext();)
				{
					//Changes for Ad-Hoc first time folder creation in provider issue
					FolderMappingBean folderMappingBean = (FolderMappingBean) iterator.next();
					if (null != folderMappingBean && null != folderMappingBean.getFolderFilenetId())
					{
						if(null != folderMappingBean.getParentFolderFilenetId() && folderMappingBean.getParentFolderFilenetId().equalsIgnoreCase("#"))
						{
							folderMappingBean.setCreatedBy(HHSConstants.SYSTEM_USER);
							folderMappingBean.setModifiedBy(HHSConstants.SYSTEM_USER);
						}
					//Changes for Ad-Hoc first time folder creation in provider issue
						insertForFilenet(aoMybatisSession, folderMappingBean, aoOrganizationType);
						loRowCount++;
					}

				}
			}
			// Adding if check for creation of Document Vault Folder

		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While creating Folder", loExp);
			//throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: deleteFromFolderMapping method - failed to delete"
					+ " \n");
			//throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert folder structure hierarchy in folder mapping table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoParameter List of type FolderMappingBean
	 * @param aoOrganizationType type of organisation
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("rawtypes")
	public int insertForFilenet(SqlSession aoMybatisSession, FolderMappingBean aoParameter, String aoOrganizationType)
			throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if (null != aoParameter && null != aoParameter.getFolderFilenetId())
			{
				aoParameter.setOrganizationType(aoOrganizationType);
				loRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameter,
						HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INSERT_FOR_FILENET_BATCH,
						HHSR5Constants.FOLDER_MAPPING_BEAN);
			}

		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While creating Folder", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: deleteFromFolderMapping method - failed to delete"
					+ " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is deleting data from folderMapping Table
	 * @param aoMybatiSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap HashMap<String, String>
	 * @return lbDeleteFolderStatus as a boolean flag which indicates successful
	 *         deletion
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public boolean deleteFromFolderMapping(SqlSession aoMybatiSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		boolean lbDeleteFolderStatus = true;
		Integer loDeleteFolderCount = HHSR5Constants.INT_ZERO;
		HashMap<String, Object> loMap = new HashMap<String, Object>();
		List<String> lofileNetIdList = new ArrayList<String>();
		try
		{
			Iterator loIter = aoHashMap.keySet().iterator();
			while (loIter.hasNext())
			{
				String lsKey = (String) loIter.next();
				lofileNetIdList.add(lsKey);
			}
			loMap.put("docIdList", lofileNetIdList);
			loDeleteFolderCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, loMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DELETE_FOLDER,
					HHSR5Constants.JAVA_UTIL_HASHMAP);
			// Check, if the integer is updated as '1' after doing the
			// modification, else will return as false status update
			if (loDeleteFolderCount <= HHSR5Constants.INT_ZERO)
			{
				lbDeleteFolderStatus = false;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while delete in foldermapping for " + aoHashMap);
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("error occured while delete in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: deleteFromFolderMapping method - failed to delete"
					+ aoHashMap + " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}
		return lbDeleteFolderStatus;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is inserting folder information into folderMapping table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aofolderMappingBean - FolderMappingBean object
	 * @return loInsertFolderMapping status of row inserted
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean createFolder(SqlSession aoMybatisSession, FolderMappingBean aofolderMappingBean)
			throws ApplicationException
	{
		Boolean loInsertFolderMapping = true;
		Integer loInsertCount = HHSR5Constants.INT_ZERO;
		try
		{
			loInsertCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aofolderMappingBean,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.CREATE_FOLDER,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
			if (loInsertCount <= HHSR5Constants.INT_ZERO)
			{
				loInsertFolderMapping = false;
			}
			// checking for create status
			if (loInsertFolderMapping)
			{
				setMoState("DocumentVaultFolderService: createFolder FolderMapping create Successfully");
			}
			else
			{
				setMoState("DocumentVaultFolderService: createFolder() failed to create.");
				throw new ApplicationException(
						"Error occured while edit at DocumentVaultFolderService: createFolder() for :"
								+ aofolderMappingBean.toString());
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while createFolder in foldermapping for " + aofolderMappingBean.toString());
			loExp.addContextData("Exception occured while createFolder in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while createFolder in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while createFolder in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: createFolder method - failed to create"
					+ aofolderMappingBean.toString() + " \n");
			throw new ApplicationException("Exception occured while create in DocumentVaultFolderService ", loAppEx);
		}
		return loInsertFolderMapping;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will provide the absolute path of a folder
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap HashMap<String, String>
	 * @return lspath absolute folder path
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public String getFolderMappingPath(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		String lspath = HHSR5Constants.EMPTY_STRING;
		try
		{
			lspath = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.GET_PATH, HHSR5Constants.JAVA_LANG_STRING);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while getFolderMappingPath in foldermapping for " + aoHashMap);
			loExp.addContextData("Exception occured while getFolderMappingPath in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while getFolderMappingPath in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getFolderMappingPath in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: getFolderMappingPath method - failed to getFolderMappingPath"
					+ aoHashMap + " \n");
			throw new ApplicationException(
					"Exception occured while getFolderMappingPath in DocumentVaultFolderService ", loAppEx);
		}
		return lspath;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will fetch tree hierarchy from DataBase
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap - Hash map of String
	 * @return lolistFolderMapping which is a List of type FolderMappingBean
	 * @throws ApplicationException - when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<FolderMappingBean> getJstreeData(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{

		List<FolderMappingBean> lolistFolderMapping = new ArrayList<FolderMappingBean>();
		try
		{
			lolistFolderMapping = (ArrayList<FolderMappingBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_JS_TREE,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while getFolderMappingPath in foldermapping for " + aoHashMap);
			loExp.addContextData("Exception occured while getFolderMappingPath in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while getFolderMappingPath in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getFolderMappingPath in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: getFolderMappingPath method - failed to getFolderMappingPath"
					+ aoHashMap + " \n");
			throw new ApplicationException(
					"Exception occured while getFolderMappingPath in DocumentVaultFolderService ", loAppEx);
		}
		return lolistFolderMapping;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will fetch lock status of a entity, currently we are using cache
	 * for fetching lock status
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap Hash map of string
	 * @return lolistLockInfo which is a List of type FolderMappingBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<FolderMappingBean> getLockInfo(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{

		List<FolderMappingBean> lolistLockInfo = new ArrayList<FolderMappingBean>();
		try
		{
			lolistLockInfo = (ArrayList<FolderMappingBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_LOCK_INFO,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while getLockInfo in foldermapping for " + aoHashMap);
			loExp.addContextData("Exception occured while getLockInfo in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while getLockInfo in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getLockInfo in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: getLockInfo method - failed to getLockInfo"
					+ aoHashMap + " \n");
			throw new ApplicationException("Exception occured while getLockInfo in DocumentVaultFolderService ",
					loAppEx);
		}
		return lolistLockInfo;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will update entries into FolderMapping Table
	 * <ul>
	 * <li>Set multiple input parameters to aofolderMappingBean</li>
	 * <li>get the update count <b>updateFolderMapping</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param hmReqProps - HashMap<String, String> shows folder properties
	 * @return loUpdateFolderMapping flag to indicate that count is greater then
	 *         zero
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean updateFolderMapping(SqlSession aoMybatisSession, HashMap<String, String> hmReqProps)
			throws ApplicationException
	{
		Boolean loUpdateFolderMapping = true;
		Integer loUpdateCount = HHSR5Constants.INT_ZERO;
		FolderMappingBean aofolderMappingBean = new FolderMappingBean();
		aofolderMappingBean.setFolderName((String) hmReqProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
		aofolderMappingBean.setModifiedBy((String) hmReqProps.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
		aofolderMappingBean.setModifiedDate((String) hmReqProps.get(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE));
		aofolderMappingBean.setFolderFilenetId((String) hmReqProps.get(HHSR5Constants.FOLDER_ID));
		aofolderMappingBean.setOrganizationId((String) hmReqProps.get(P8Constants.XML_DOC_ORG_ID_PROPERTY));
		// change after build 4.0.2 for modified date in edit properties for folder
		aofolderMappingBean.setOrganizationType(hmReqProps.get(HHSConstants.ORGANIZATION_TYPE));
		try
		{
			loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aofolderMappingBean,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.UPDATE_FOLDER_MAPPING,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
			if (loUpdateCount <= HHSR5Constants.INT_ZERO)
			{
				loUpdateFolderMapping = false;
			}
			// checking for update status
			if (loUpdateFolderMapping)
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping FolderMapping create Successfully");
			}
			else
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping() failed to udpate.");
				throw new ApplicationException(
						"Error occured while edit at DocumentVaultFolderService: updateFolderMapping() for :"
								+ aofolderMappingBean.toString());
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updateFolderMapping in foldermapping for " + aofolderMappingBean.toString());
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: updateFolderMapping method - failed to create"
					+ aofolderMappingBean.toString() + " \n");
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loUpdateFolderMapping;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will give us the folder and document count from FolderMapping
	 * Table <li>put fldrId id into map</li> <li>get organisation id
	 * <b>getOrgId</b></li> <li>put organisation id into map</li> <li>get
	 * loCountInfo <b>getFolderProp</b></li>
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderId String
	 * @param aoHashMap hash map of string
	 * @return loCountInfo FolderMappingBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public FolderMappingBean getFolderMappingCount(SqlSession aoMyBatisSession, String aoFolderId,
			HashMap<String, String> aoHashMap) throws ApplicationException
	{

		FolderMappingBean loCountInfo = new FolderMappingBean();
		HashMap<String, String> loMap = new HashMap<String, String>();
		String lsOrgId = null;
		loMap.put(HHSR5Constants.FLDR_ID, aoFolderId);
		if (null != aoFolderId && !aoFolderId.isEmpty())
		{
			lsOrgId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoFolderId, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.GET_ORG_ID, HHSConstants.JAVA_LANG_STRING);

			loMap.put(HHSR5Constants.ORG_ID, lsOrgId);
		}

		try
		{
			loCountInfo = (FolderMappingBean) DAOUtil.masterDAO(aoMyBatisSession, loMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_FOLDER_PROPERTIES,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while getCount in foldermapping for " + aoHashMap);
			loExp.addContextData("Exception occured while getCount in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while getCount in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getCount in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: getCount method - failed to getCount"
					+ aoHashMap + " \n");
			throw new ApplicationException("Exception occured while getCount in DocumentVaultFolderService ", loAppEx);
		}
		return loCountInfo;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * methos will insert audit entries into audit table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap hash map object
	 * @return loInsertFolderMappingAudit status
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean insertFolderMappingAudit(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		Boolean loInsertFolderMappingAudit = true;
		Integer loInsertCount = HHSR5Constants.INT_ZERO;
		try
		{
			loInsertCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INSERT_FOLDER_MAPPING_AUDIT,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
			if (loInsertCount <= HHSR5Constants.INT_ZERO)
			{
				loInsertFolderMappingAudit = false;
			}
			// checking for insert status
			if (loInsertFolderMappingAudit)
			{
				setMoState("DocumentVaultFolderService: insertFolderMappingAudit create Successfully");
			}
			else
			{
				setMoState("DocumentVaultFolderService: insertFolderMappingAudit() failed to insert.");
				throw new ApplicationException(
						"Error occured while edit at DocumentVaultFolderService: insertFolderMappingAudit() for :"
								+ aoHashMap);
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while insertFolderMappingAudit in foldermapping for " + aoHashMap);
			loExp.addContextData("Exception occured while insertFolderMappingAudit in folderMappingAudit ", loExp);
			LOG_OBJECT.Error("error occured while insertFolderMappingAudit in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while insertFolderMappingAudit in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: insertFolderMappingAudit method - failed to folderMappingAudit"
					+ aoHashMap + " \n");
			throw new ApplicationException(
					"Exception occured while insertFolderMappingAudit in DocumentVaultFolderService ", loAppEx);
		}
		return loInsertFolderMappingAudit;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will fetch folder mapping from FolderMapping table to show folder
	 * hierarchy <li>get FolderMappingBean List <b>fetchFolderMapList</b></li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap - HashMap<String, String> object
	 * @return list of FolderMappingBean bean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<FolderMappingBean> fetchFolderMapping(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		List<FolderMappingBean> loJsTreeBeanList = null;
		try
		{
			loJsTreeBeanList = (List<FolderMappingBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_FOLDER_MAPPING_LIST,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error in fetchFolderMapping method for Organization", loAppEx);
			setMoState("Error in fetchFolderMapping method for Organization " + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		return loJsTreeBeanList;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will fetch folder mapping from FolderMapping table to show folder
	 * hierarchy <li>get FolderMappingBean List <b>fetchFolderMapListOrg</b></li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMap - HashMap<String, String> object
	 * @return list of FolderMappingBean bean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<FolderMappingBean> fetchFolderMappingOrg(SqlSession aoMybatisSession, HashMap<String, String> aoHashMap)
			throws ApplicationException
	{
		List<FolderMappingBean> loJsTreeBeanList = null;
		try
		{
			loJsTreeBeanList = (List<FolderMappingBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_Folder_MAP_LIST_ORG,
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error in fetchFolderMapping method for Organization", loAppEx);
			setMoState("Error in fetchFolderMapping method for Organization " + " \n");
			throw loAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving in ContractBudgetService ", loAppEx);
			throw new ApplicationException("Exception occured while fetch in ContractBudgetService ", loAppEx);
		}
		return loJsTreeBeanList;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will update folder count of parent folder while creating any new
	 * folder into Database
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoBeanList list of folder
	 * @return integer count of rows updated in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 *             commenting throw exception in release 4.0.2 to fix create folder issue for defect # 8408
	 */
	public Integer updateFolderCount(SqlSession aoMybatisSession, List<FolderMappingBean> aoBeanList)
			throws ApplicationException
	{
		Integer liCount = 0;
		try
		{
			/*
			 * for (int i = 0; i < aoBean.size(); i++) {
			 */
			for (Iterator iterator = aoBeanList.iterator(); iterator.hasNext();)
			{
				FolderMappingBean aoBean = (FolderMappingBean) iterator.next();
				if (!aoBean.getFolderName().equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT))
				{
					liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoBean,
							HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.UPDATE_FOLDER_COUNT,
							HHSR5Constants.FOLDER_MAPPING_BEAN);
				}
			}

			// }

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at fetching updateFolderCount", loAppEx);
			//throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving updateFolderCount", loAppEx);
			//throw new ApplicationException("Exception occured while fetch in updateFolderCount", loAppEx);
		}
		return liCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will update folder count of parent folder while creating any new
	 * folder into Database <li>Add multiple parameters to a Bean</li> <li>get
	 * liCount by calling <b>updateDocCount</b></li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoReqMap request mapping object
	 * @return integer count of rows updated in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer updateDocCount(SqlSession aoMybatisSession, HashMap<String, String> aoReqMap)
			throws ApplicationException
	{
		Integer liCount = 0;
		try
		{
			FolderMappingBean loBean = new FolderMappingBean();
			loBean.setFolderFilenetId((String) aoReqMap.get(HHSR5Constants.CUSTOM_FLDR_ID));
			loBean.setOrganizationId((String) aoReqMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID));
			loBean.setModifiedBy((String) aoReqMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
			loBean.setOrganizationType((String) aoReqMap.get(HHSConstants.ORGANIZATION_ID_KEY));
			liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.UPDATE_DOC_COUNT, HHSR5Constants.FOLDER_MAPPING_BEAN);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at fetching updateDocCount", loAppEx);
			throw loAppEx;
		}

		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving updateDocCount", loAppEx);
			throw new ApplicationException("Exception occured while fetch in updateDocCount", loAppEx);
		}
		return liCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will fetch linkage entites from View in Database
	 * @param aoSqlSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashmap - HashMap<String, String> object
	 * @return entity list
	 */
	public List<String> getObjectLinkage(SqlSession aoSqlSession, HashMap<String, String> aoHashmap)
	{
		List<String> loreturnList = new ArrayList<String>();

		return loreturnList;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert data into DOWNLOAD_ALL Table with Pending Status
	 * @param aoSqlSession Sql session object
	 * @param aoHashmap 
	 * @return
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public int downloadAllDocuments(SqlSession aoSqlSession, HashMap<String, String> aoHashmap)
	{
		return 0;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will change folder properties when it is moved <li>get
	 * loUpdateCount by calling <b>decreaseCount</b></li> <li>get loUpdateCount
	 * by calling <b>increaseCount</b></li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param hmReqProps - HashMap<String, Object> object
	 * @param aoMapOrg - HashMap<String, String> object
	 * @return integer count of update Folder Mapping For Move
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public int updateFolderMappingForMove(SqlSession aoMybatisSession,
			HashMap<String, HashMap<String, Object>> hmReqProps, HashMap<String, String> aoMapOrg)
			throws ApplicationException
	{
		Integer loUpdateCount = 0;
		FolderMappingBean loBean = new FolderMappingBean();
		try
		{
			if (null != hmReqProps && !hmReqProps.isEmpty())
			{
				loBean.setModifiedBy((String) aoMapOrg.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
				loBean.setOrganizationId((String) aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
				loBean.setOrganizationType((String) aoMapOrg.get(HHSConstants.LS_USER_ORG_TYPE));
				Iterator loItr = hmReqProps.entrySet().iterator();
				while (loItr.hasNext())
				{
					Entry<String, HashMap<String, Object>> loEntry = (Entry<String, HashMap<String, Object>>) loItr
							.next();
					HashMap<String, Object> loMap = loEntry.getValue();
					Iterator loInnerItr = loMap.entrySet().iterator();
					while (loInnerItr.hasNext())
					{
						Entry<String, Object> loInnerEntry = (Entry<String, Object>) loInnerItr.next();
						if (null != loEntry
								&& loEntry.getKey().equalsIgnoreCase(HHSR5Constants.DOC_COUNT_DECREASE))
						{
							loBean.setFolderFilenetId(loInnerEntry.getKey());
							loBean.setDocumentCount(Integer.parseInt(loInnerEntry.getValue().toString()));
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DECREASE_COUNT,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}
						else if (null != loEntry
								&& loEntry.getKey().equalsIgnoreCase(HHSR5Constants.DOC_COUNT_INCREASE))
						{
							loBean.setFolderFilenetId(loInnerEntry.getKey());
							loBean.setDocumentCount(Integer.parseInt(loInnerEntry.getValue().toString()));
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INCREASE_COUNT,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}
						else if (null != loEntry
								&& loEntry.getKey().equalsIgnoreCase(HHSR5Constants.FOLDER_COUNT_DECREASE))
						{
							loBean.setFolderFilenetId(loInnerEntry.getKey());
							loBean.setFolderCount(Integer.parseInt(loInnerEntry.getValue().toString()));
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DECREASE_COUNT,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}
						else if (null != loEntry
								&& loEntry.getKey().equalsIgnoreCase(HHSR5Constants.FOLDER_COUNT_INCREASE))
						{
							loBean.setFolderFilenetId(loInnerEntry.getKey());
							loBean.setFolderCount(Integer.parseInt(loInnerEntry.getValue().toString()));
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INCREASE_COUNT,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}
						else if (null != loInnerEntry
								&& loInnerEntry.getKey().equalsIgnoreCase(HHSR5Constants.FOLDER_PARENT_CHANGE))
						{
							loBean.setFolderFilenetId(loEntry.getKey());
							loBean.setParentFolderFilenetId((String) loInnerEntry.getValue());
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.UPDATE_FOLDER_MAPPING,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}

						else if (null != loInnerEntry
								&& loInnerEntry.getKey().equalsIgnoreCase(HHSR5Constants.DELETE_ITEMS_LIST))
						{

							List<String> loDeleteingList = (List<String>) loInnerEntry.getValue();
							for (Iterator iterator = loDeleteingList.iterator(); iterator.hasNext();)
							{
								String lsDocId = (String) iterator.next();
								HashMap<String, String> loMapDel = new HashMap<String, String>();
								loMapDel.put(HHSR5Constants.FOLDER_FILENET_ID, lsDocId);
								loMapDel.put(HHSR5Constants.ORG_ID,
										(String) aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
								loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMapDel,
										HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DELETE_FOLDER_SINGLE_ROW,
										HHSR5Constants.JAVA_UTIL_HASH_MAP);

							}

						}

					}
				}

			}
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving updateFolderCount", loAppEx);
			throw new ApplicationException("Exception occured while fetch in updateFolderCount", loAppEx);
		}
		return loUpdateCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will change folder properties when it is moved <li>get
	 * loUpdateCount by calling <b>decreaseCount</b></li> <li>get loUpdateCount
	 * by calling <b>increaseCount</b></li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param hmReqProps list of properties
	 * @param aoMapOrg hashmap of string
	 * @return integer count of update Folder Mapping For Move
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, List<String>> updateFolderMapingForDelete(SqlSession aoMybatisSession,
			HashMap<String, List<String>> hmReqProps, HashMap<String, String> aoMapOrg) throws ApplicationException
	{
		Iterator loKeyIt = hmReqProps.keySet().iterator();
		List<String> lsFolderIdList = new ArrayList<String>();
		List<String> lsFolderIdListForFolderDeletion = new ArrayList<String>();
		HashMap<String,Integer> loDataMap = new HashMap<String, Integer>();
		FolderMappingBean loBean = new FolderMappingBean();
		Integer loUpdateCount = null;
		try
		{
			while (loKeyIt.hasNext())
			{

				String loKey = (String) loKeyIt.next();

				if (null != loKey && !loKey.isEmpty()
						&& loKey.equalsIgnoreCase(HHSR5Constants.DOC_COUNT_DECREASE_FROM_DB_LIST))
				{
					loDataMap = new HashMap<String, Integer>();
					lsFolderIdList = hmReqProps.get(loKey);
					for (Iterator iterator = lsFolderIdList.iterator(); iterator.hasNext();)
					{
						String lsFolderFineletId = (String) iterator.next();
						if(loDataMap.containsKey(lsFolderFineletId))
						{
							int liVal = loDataMap.get(lsFolderFineletId);
							liVal = liVal+1;
							loDataMap.put(lsFolderFineletId, liVal);
						}
						else
						{
							loDataMap.put(lsFolderFineletId, 1);
						}
					}
					loBean.setOrganizationId(aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
					loBean.setModifiedBy(aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
					for (Map.Entry<String, Integer> entry : loDataMap.entrySet())
					{
						loBean.setDocumentCount(entry.getValue());
						loBean.setFolderFilenetId(entry.getKey());
						loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
								HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DECREASE_COUNT,
								HHSR5Constants.FOLDER_MAPPING_BEAN);
					}
				}

				if (null != loKey && !loKey.isEmpty()
						&& loKey.equalsIgnoreCase(HHSR5Constants.FOLDER_REMOVAL_FROM_DB_LIST))
				{
					loDataMap = new HashMap<String, Integer>();
					lsFolderIdListForFolderDeletion = hmReqProps.get(loKey);
					if(null != lsFolderIdListForFolderDeletion && !lsFolderIdListForFolderDeletion.isEmpty())
					{
						
						for (Iterator iterator = lsFolderIdListForFolderDeletion.iterator(); iterator.hasNext();)
						{
							String lsFolderFilenetId = (String) iterator.next();
							if(loDataMap.containsKey(lsFolderFilenetId))
							{
								int liVal = loDataMap.get(lsFolderFilenetId);
								liVal = liVal+1;
								loDataMap.put(lsFolderFilenetId, liVal);
							}
							else
							{
								loDataMap.put(lsFolderFilenetId, 1);
							}
						}
						for (Map.Entry<String, Integer> entry : loDataMap.entrySet())
						{
							loBean.setFolderCount(entry.getValue());
							loBean.setFolderFilenetId(entry.getKey());
							loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loBean,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DECREASE_COUNT,
									HHSR5Constants.FOLDER_MAPPING_BEAN);
						}
						HashMap<String, Object> loMap = new HashMap<String, Object>();
						loMap.put(HHSR5Constants.ORGA_ID, aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
						loMap.put(HHSR5Constants.FILE_NET_ID_LIST, lsFolderIdListForFolderDeletion);
						DAOUtil.masterDAO(aoMybatisSession, loMap, HHSR5Constants.FOLDER_MAPPING_MAPPER,
								HHSR5Constants.SET_TREE_FLAG, HHSR5Constants.JAVA_UTIL_HASH_MAP);
					}
					
				}

			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving data:"+loUpdateCount, loAppEx);
			throw loAppEx;

		}
		hmReqProps.remove(HHSR5Constants.DOC_COUNT_DECREASE_FROM_DB_LIST);
		hmReqProps.remove(HHSR5Constants.FOLDER_REMOVAL_FROM_DB_LIST);
		return hmReqProps;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will update intermediate Service For Data Handling if a folder is
	 * moved particular documentId in document vault. Added for release 5
	 * @param aoReqMap HashMap<String, Object>
	 * @return loReturnList List of FolderMappingBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	@SuppressWarnings("unchecked")
	public List<FolderMappingBean> intermediateServiceForDataHandling(HashMap<String, Object> aoReqMap)
	{
		List<FolderMappingBean> loReturnList = new ArrayList<FolderMappingBean>();
		if (null != aoReqMap && !aoReqMap.isEmpty())
		{
			loReturnList = (List<FolderMappingBean>) aoReqMap.get(HHSR5Constants.FOLDER_MAPPING_BEAN_ID);
		}
		return loReturnList;
	}

	/**
	 * This method will get history of date and entity type from database for
	 * particular documentId in document vault. Added for release 5
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoHashMapParam hash map of parameters
	 * @param documentId ID of selected document
	 * @return loListDoc List of bean containing information of date and entity
	 *         type of document
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentBean> displayLinkageInformation(SqlSession aoMybatisSession, HashMap aoHashMapParam)
			throws ApplicationException
	{
		List<DocumentBean> loListDoc = new ArrayList<DocumentBean>();
		try
		{
			loListDoc = (List<DocumentBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMapParam,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_DATA, HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving data", loAppEx);
			throw new ApplicationException("Exception occured while fetch in database", loAppEx);

		}

		return loListDoc;

	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will update Folder Mapping properties when a folder is Deleted
	 * <ul>
	 * <li>Add multiple parameters to a Map</li>
	 * <li>Update Folder Mapping properties when a folder is Deleted
	 * <b>loUpdateCount</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asFolderIdList List of type Document
	 * @param aoMapOrg - HashMap<String, String>
	 * @return lbFlag indicating if folder mapping is updated on folder delete
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	public Boolean updateFolderMappingDeleteFlag(SqlSession aoMybatisSession, List<Document> asFolderIdList,
			HashMap<String, String> aoMapOrg) throws ApplicationException
	{
		boolean lbFlag = false;
		int loUpdateCount = 0;

		HashMap<String, Object> loMap = new HashMap<String, Object>();
		try
		{
			for (Iterator iterator = asFolderIdList.iterator(); iterator.hasNext();)
			{
				Document loDoc = (Document) iterator.next();
				if (null == loDoc.getDocType() || loDoc.getDocType().isEmpty()
						|| loDoc.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{

					if (loDoc.isPermamantDeleteFromDb())
					{
						loMap.put(HHSR5Constants.ORG_ID, aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
						loMap.put(HHSR5Constants.FOLDER_FILENET_ID, loDoc.getDocumentId());
						loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
								HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.DELETE_FOLDER_SINGLE_ROW,
								HHSR5Constants.JAVA_UTIL_HASHMAP);
					}
					loMap.put(HHSR5Constants.ORGANIZATION_ID, aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
					loMap.put(HHSR5Constants.FILE_NET_ID, loDoc.getDocumentId());
					loMap.put(HHSR5Constants.MOVE_TO_RECYCLE_BIN_FLAG, ApplicationConstants.ZERO);
					loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
							HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.SET_MOVE_FLAG,
							HHSR5Constants.JAVA_UTIL_HASHMAP);
				}

				if (null != loDoc.getParent() && !loDoc.getParent().get_Id().toString().isEmpty())
				{
					loMap.put(HHSR5Constants.PARENT_ID, loDoc.getParent().get_Id().toString());
					loMap.put(HHSR5Constants.ORGANIZATION_ID, aoMapOrg.get(HHSR5Constants.ORGANIZATION_ID));
					loMap.put(HHSR5Constants.FILE_NET_ID, loDoc.getDocumentId());
					loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
							HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.SET_PARENT_ID,
							HHSR5Constants.JAVA_UTIL_HASHMAP);
				}

			}
			if (asFolderIdList.isEmpty() || loUpdateCount > 0)
			{
				lbFlag = true;
			}

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving data", loAppEx);
			throw new ApplicationException("Exception occured while fetch in database", loAppEx);

		}
		return lbFlag;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called after restoring a method to update document count
	 * <ul>
	 * <li>Add multiple input parameters to a HashMap</li>
	 * <li>Update document count using <b>updateDocCountDynamic</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoParameterMap HashMap<String, Object>
	 * @param aoMapOrg HashMap<String, String>
	 * @return loUpdateCount Integer
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public Integer intermediateServiceForInsertionInDB(SqlSession aoMybatisSession,
			HashMap<String, Object> aoParameterMap, HashMap<String, String> aoMapOrg) throws ApplicationException
	{
		List<Document> aoDBList = new ArrayList<Document>();
		HashMap<String, Object> loDbMap = new HashMap<String, Object>();
		List<FolderMappingBean> aoDBListInsertion = new ArrayList<FolderMappingBean>();
		HashMap<String, Integer> loDocCountIntermediateMap = new HashMap<String, Integer>();
		HashMap<String, Object> loDocUpdateMap = new HashMap<String, Object>();
		Integer loUpdateCount = 0;
		try
		{
			if (null != aoParameterMap && !aoParameterMap.isEmpty())
			{
				aoDBList = (List) aoParameterMap.get(HHSR5Constants.DELETION_LIST);
				aoDBListInsertion = (List) aoParameterMap.get(HHSR5Constants.INSERTION_LIST);
				loDocUpdateMap = (HashMap<String, Object>) aoParameterMap.get(HHSR5Constants.DOC_UPDATE_COUNT);
				loDocCountIntermediateMap = (HashMap<String, Integer>) loDocUpdateMap
						.get(HHSR5Constants.UPDATE_DOC_COUNT);
				if (null != aoDBList && !aoDBList.isEmpty())
				{
					updateFolderMappingDeleteFlag(aoMybatisSession, aoDBList, aoMapOrg);
				}
				if (null != aoDBListInsertion && !aoDBListInsertion.isEmpty())
				{
					loUpdateCount = insertForFilenet(aoMybatisSession, aoDBListInsertion,
							(String) aoMapOrg.get(HHSR5Constants.ORGANIZATION_TYPE));
				}
				if (null != loDocCountIntermediateMap && !loDocCountIntermediateMap.isEmpty())
				{
					for (Entry<String, Integer> entry : loDocCountIntermediateMap.entrySet())
					{
						loDbMap.put(HHSR5Constants.FOLDER_ID, entry.getKey());
						loDbMap.put(HHSR5Constants.DOC_COUNT, entry.getValue());
						loDbMap.put(HHSR5Constants.ORGANIZATION_TYPE, aoMapOrg.get(HHSR5Constants.ORGANIZATION_TYPE));
						loDbMap.put(HHSR5Constants.MODIFIED_BY,
								aoMapOrg.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));

						loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDbMap,
								HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.UPDATE_DOC_COUNT_DYNAMIC,
								HHSR5Constants.JAVA_UTIL_HASHMAP);
					}
				}

			}
		}
		catch (ApplicationException loAppEx)
		{
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreFolder in Document Vault", aoExp);
			throw new ApplicationException("Error Occured while Processing your Request", aoExp);
		}
		return loUpdateCount;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to check if Folder Name already Exists
	 * <ul>
	 * <li>Update FolderMappingBean and check if folder name already exists
	 * using <b>checkFolderNameExists</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param hmReqProps HashMap object
	 * @return loUpdateFolderMapping Boolean flag to show if folder name already
	 *         exists
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Boolean checkFolderNameExists(SqlSession aoMybatisSession, HashMap hmReqProps) throws ApplicationException
	{
		Boolean loUpdateFolderMapping = true;
		FolderMappingBean loBean = new FolderMappingBean();
		try
		{
			loBean = (FolderMappingBean) DAOUtil.masterDAO(aoMybatisSession, hmReqProps,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.CHECK_FOLDER_NAME_EXISTS,
					HHSR5Constants.JAVA_UTIL_HASHMAP);
			if (null != loBean && !loBean.getFolderFilenetId().isEmpty())
			{
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,HHSR5Constants.MESSAGE_SAME_NAME_EXIST));
			}
			// checking for update status
			if (loUpdateFolderMapping)
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping FolderMapping create Successfully");
			}
			else
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping() failed to udpate.");
				throw new ApplicationException(
						"Error occured while edit at DocumentVaultFolderService: updateFolderMapping() for");
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updateFolderMapping in foldermapping for " + loBean.toString());
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: updateFolderMapping method - failed to create"
					+ loBean.toString() + " \n");
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loUpdateFolderMapping;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to check if Folder Name already Exists
	 * <ul>
	 * <li>Get values of lsProcurementId, lsAmendmentId, lsAwardepinTitle,
	 * lsSubmittedFrom, lsSubmittedTo, lsInvoiceNumber from aoFilterMap</li>
	 * <li>put variables into map</li>
	 * <li>Get loselectedDocIdListby using<b>getProcurementInfo</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFilterMap HashMap<String, Object> object
	 * @return aoFilterMap 
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataForLinkage(SqlSession aoMybatisSession, HashMap<String, Object> aoFilterMap)
			throws ApplicationException
	{
		String lsProcurementId = null;
		String lsAmendmentId = null;
		String lsAwardepinTitle = null;
		Date lsSubmittedFrom = null;
		Date lsSubmittedTo = null;
		String lsInvoiceNumber = null;
		List<String> loselectedDocIdList = new ArrayList<String>();
		String lsOrgId = null;
		HashMap<String, Object> loMapDB = new HashMap<String, Object>();
		try
		{
			if (null != aoFilterMap && null != aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID)
					&& !aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString().isEmpty())
			{
				if (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(HHSR5Constants.AWARD_UPPER_CASE)
						|| aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(HHSR5Constants.PROCUREMENT)
						|| aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(HHSR5Constants.PROPOSAL)
						|| aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString().equalsIgnoreCase("providerAward"))
				{
					lsProcurementId = (String) aoFilterMap.get(HHSR5Constants.PROCUREMENT_ID);
				}
				else if (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(HHSR5Constants.AMENDMENT_RENDER))
				{
					lsAmendmentId = (String) aoFilterMap.get(HHSR5Constants.AMEND_EPIN_TITLE);
				}
				else if (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(HHSR5Constants.AGENCY_AWARD)
						|| aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(HHSR5Constants.BUDGETLIST_BUDGET))
				{
					lsAwardepinTitle = (String) aoFilterMap.get(HHSR5Constants.AWARD_EPIN_TITLE);
				}
				//Added for Release 6: Returned Payment linked entity search
				else if (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(HHSR5Constants.CONTRACT)
						||aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(HHSR5Constants.STRING_RETURNED_PAYMENT))
				{
					lsAwardepinTitle = (String) aoFilterMap.get(HHSR5Constants.CONTRACT_AWARD_EPIN_TITLE);
				}
				//Added for Release 6: Returned Payment linked entity search end
				else if (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
						.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION)
						|| aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION))
				{
					lsSubmittedFrom = DateUtil.getSqlDate((String) aoFilterMap.get(HHSR5Constants.SUBMITTED_From));
					lsSubmittedTo = DateUtil.getSqlDate((String) aoFilterMap.get(HHSR5Constants.SUBMITTED_TO));
				}
				else if (null != aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID)
						&& (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(HHSR5Constants.INVOICE)))
				{
					lsInvoiceNumber = (String) aoFilterMap.get(HHSR5Constants.INVOICE_NUMBER);
				}
				else if (null != aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID)
						&& (aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID).toString()
								.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_CORPORATE_STRUCTURE)))
				{
					return aoFilterMap;
				}
				loMapDB.put(ApplicationConstants.ENTITY_TYPE, aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID));
				loMapDB.put(HHSR5Constants.PROCUREMENT_ID, lsProcurementId);
				loMapDB.put(HHSR5Constants.AWARD_EPIN_TITLE, lsAwardepinTitle);
				loMapDB.put(HHSR5Constants.INVOICE_NUMBER, lsInvoiceNumber);
				loMapDB.put(HHSR5Constants.AMENDMENT_ID, lsAmendmentId);
				loMapDB.put(HHSR5Constants.ORG_ID, aoFilterMap.get(HHSR5Constants.ORGANIZATION_ID));
				loMapDB.put(HHSR5Constants.PROVIDER_ID, aoFilterMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID));
				loMapDB.put(HHSR5Constants.SUBMITTED_From, lsSubmittedFrom);
				loMapDB.put(HHSR5Constants.SUBMITTED_TO, lsSubmittedTo);
				loselectedDocIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loMapDB,
						HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_PROCUREMENT_INFO,
						HHSR5Constants.JAVA_UTIL_HASHMAP);
				aoFilterMap.put(HHSR5Constants.DB_RETURN_LIST, loselectedDocIdList);
				return aoFilterMap;
				/*
				 * } else {
				 * 
				 * aoFilterMap.put(HHSR5Constants.DB_RETURN_LIST, null); return
				 * aoFilterMap; }
				 */
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return aoFilterMap;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Procurement Title
	 * <ul>
	 * <li>put variables into map</li>
	 * <li>Get loProcurementList using<b> getProcurementTitle</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoOrgId - String of orgainization id
	 * @param aoPartialString - sting of partial string 
	 * @return loProcurementList - List of type ExtendedDocument
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> getProcurementTitle(SqlSession aoMybatisSession, String aoOrgId,
			String aoPartialString) throws ApplicationException
	{
		List<ExtendedDocument> loProcurementList = new ArrayList<ExtendedDocument>();
		HashMap<String, Object> loMapDB = new HashMap<String, Object>();
		List<String> loStatusIdList = new ArrayList<String>();
		try
		{
			loMapDB.put(HHSR5Constants.PARTIAL_STRING, "%" + aoPartialString + "%");
			loStatusIdList.add(ApplicationConstants.ONE);
			if (null != aoOrgId && !aoOrgId.isEmpty() && aoOrgId.equalsIgnoreCase(HHSR5Constants.PROVIDER_ORG))
			{
				loStatusIdList.add(HHSR5Constants.TWO);
			}
			loMapDB.put(HHSR5Constants.STATUS_ID, loStatusIdList);
			loProcurementList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, loMapDB,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_PROC_TITLE,
					HHSR5Constants.JAVA_UTIL_HASHMAP);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loProcurementList;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Award EPin
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asUserOrgType - User organization type String
	 * @param asOrgId - Organization id string
	 * @param asPartialString - Partial String
	 * @return loAwardEPinList - List of type String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAwardEPin(SqlSession aoMybatisSession, String asUserOrgType, String asOrgId,
			String asPartialString) throws ApplicationException
	{
		List<String> loAwardEPinList = new ArrayList<String>();
		HashMap<String, String> loMapDB = new HashMap<String, String>();
		try
		{
			loMapDB.put(HHSR5Constants.PARTIAL_STRING, "%" + asPartialString + "%");
			loMapDB.put(HHSR5Constants.ORG_ID, asOrgId);
			loMapDB.put(HHSR5Constants.ORG_TYPES, asUserOrgType);
			loAwardEPinList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loMapDB,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_AWARD_EPIN,
					HHSR5Constants.JAVA_UTIL_HASHMAP);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loAwardEPinList;
	}
/**
 * This method will get list of award E pin for a Contract
 * @param aoMybatisSession SqlSession object
 * @param asUserOrgType User organisation type string
 * @param asOrgId organisation id string
 * @param asPartialString parameter to be passed in like condition
 * @return List<String> loAwardEPinList
 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
 */
	@SuppressWarnings("unchecked")
	public List<String> getContractAwardEPin(SqlSession aoMybatisSession, String asUserOrgType, String asOrgId,
			String asPartialString) throws ApplicationException
	{
		List<String> loAwardEPinList = new ArrayList<String>();
		HashMap<String, String> loMapDB = new HashMap<String, String>();
		try
		{
			loMapDB.put(HHSR5Constants.PARTIAL_STRING, "%" + asPartialString + "%");
			loMapDB.put(HHSR5Constants.ORG_ID, asOrgId);
			loMapDB.put(HHSR5Constants.ORG_TYPES, asUserOrgType);
			loAwardEPinList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loMapDB,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_CONTRACT_AWARD_EPIN,
					HHSR5Constants.JAVA_UTIL_HASHMAP);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loAwardEPinList;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to save Data For Download All
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFilterMap HashMap<String, String>
	 * @return lbStatus - flag to indicate if a row or more are inserted
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	public Boolean saveDataForDownloadAll(SqlSession aoMybatisSession, HashMap<String, String> aoFilterMap)
			throws ApplicationException
	{
		Boolean lbStatus = false;
		int liRowCount = 0;
		try
		{
			if (null != aoFilterMap)
			{
				liRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoFilterMap,
						HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INSERT_FOR_DOWNLOAD_ALL,
						HHSR5Constants.JAVA_UTIL_HASHMAP);
				if (liRowCount > 0)
				{
					lbStatus = true;
				}

			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return lbStatus;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault The method
	 * will fetch rows from BULK_DOCUMENT_DOWNLOAD whose status is in pending
	 * state.
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoStatusId - Status id String
	 * @return list of BulkDownloadBean
	 * @throws ApplicationException - when any exception occurred wrap it into
	 *             application exception.
	 */

	@SuppressWarnings("unchecked")
	public List<BulkDownloadBean> getDownloadList(SqlSession aoMyBatisSession, String aoStatusId)
			throws ApplicationException
	{

		List<BulkDownloadBean> loBulkDownloadList = new ArrayList<BulkDownloadBean>();
		HashMap loHmExcepRequiredProp = new HashMap();
		LOG_OBJECT.Info("Entered P8ContentOperations.getDownloadList() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			loBulkDownloadList = (List<BulkDownloadBean>) DAOUtil.masterDAO(aoMyBatisSession, aoStatusId,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_DOWNLOAD_LIST,
					HHSR5Constants.JAVA_LANG_STRING);
			DAOUtil.masterDAO(aoMyBatisSession, aoStatusId, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					"updateStatusFlagDownload", HHSR5Constants.JAVA_LANG_STRING);
		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting Bulk Download list", aoErEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting Bulk Download list", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting Bulk Download list", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting Bulk Download list", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDownloadList()");
		return loBulkDownloadList;

	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Organization Details
	 * <ul>
	 * <li>Get List of type OrganizationBean using<b>
	 * fetchOrganizationDetails</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @return loOrganizationBean - List of type OrganizationBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	@SuppressWarnings("unchecked")
	public List<OrganizationBean> getOrgDetails(SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<OrganizationBean> loOrganizationBean = new ArrayList<OrganizationBean>();
		try
		{
			loOrganizationBean = (List<OrganizationBean>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_ORGANIZATION_DETAILS, null);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loOrganizationBean;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to update Success Flag
	 * <ul>
	 * <li>Get Count using<b> updateStatusFlag</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoReqId String
	 * @param aoMybatisSession - mybatis SQL session
	 * @return loCount - Integer
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer updateSuccessFlag(SqlSession aoMyBatisSession, String aoReqId) throws ApplicationException
	{
		Integer loCount = 0;
		try
		{
			loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoReqId, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.UPDATE_STATUS_FLAG, HHSR5Constants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loCount;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to update Success Flag
	 * <ul>
	 * <li>Get Count using<b> updateStatusFlag</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoReqId String
	 * @param aoMybatisSession - mybatis SQL session
	 * @return loCount - Integer
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer updateExportSuccessFlag(SqlSession aoMyBatisSession, String aoReqId) throws ApplicationException
	{
		Integer loCount = 0;
		try
		{
			loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoReqId, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.UPDATE_EXPORT_STATUS_FLAG, HHSR5Constants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loCount;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Zip Path From Data base
	 * <ul>
	 * <li>put requestId, requestedZipId in map</li>
	 * <li>Get lsZipPath using<b> getZipPath</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoReqId String
	 * @param aoZipId String
	 * @return lsZipPath - String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap<String, Object> getZipPathFromDB(SqlSession aoMyBatisSession, String aoReqId, String aoZipId)
			throws ApplicationException
	{
		HashMap<String, Object> loZipDetails = null;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			loMap.put(ApplicationConstants.REQUEST_ID, aoReqId);
			loMap.put(HHSR5Constants.REQUESTED_ZIP_ID, aoZipId);
			loZipDetails = (HashMap<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_ZIP_PATH,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loZipDetails;
	}
/**
 * This method gets Export information from database
 * @param aoMyBatisSession SqlSession object
 * @param aoReqId request id string
 * @return HashMap<String, Object> loZipDetails
 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
 */
	public HashMap<String, Object> getExportInfoFromDB(SqlSession aoMyBatisSession, String aoReqId)
			throws ApplicationException
	{
		HashMap<String, Object> loZipDetails = null;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			loMap.put(ApplicationConstants.REQUEST_ID, aoReqId);
			loZipDetails = (HashMap<String, Object>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.TRANSACTION_GET_EXPORT_INFO_FROM_DB,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loZipDetails;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Zip Path From Data base
	 * <ul>
	 * <li>put requestId in map</li>
	 * <li>Get loZipList using<b> getZipId</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoReqId - Request id String
	 * @return loZipList - list of String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getZipIdFromDb(SqlSession aoMyBatisSession, String aoReqId) throws ApplicationException
	{
		List<String> loZipList = null;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			loMap.put(HHSR5Constants.REQUEST_ID, aoReqId);
			loZipList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoReqId,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_ZIP_ID, HHSR5Constants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loZipList;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to set entry into Zip Table
	 * <ul>
	 * <li>set entry into Zip Table using<b> setentryintoZipTable</b></li>
	 * </ul>
	 * </p>
	 * @param aoMyBatisSession Sql session object
	 * @param aoRequestId request id string
	 * @param aoZipFilePath list of zip file path
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoZipMap HashMap
	 * @return loCount - Integer
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer setentryintoZipTable(SqlSession aoMyBatisSession, String aoRequestId, List<String> aoZipFilePath)
			throws ApplicationException
	{
		Integer loCount = 0;
		try
		{

			for (Iterator iterator = aoZipFilePath.iterator(); iterator.hasNext();)
			{
				String lsZipPath = (String) iterator.next();
				HashMap<String, String> loZipMap = new HashMap<String, String>();
				loZipMap.put("requestID", aoRequestId);
				loZipMap.put("zipFilePath", lsZipPath);
				loZipMap.put("createdBy", "DownloadBatch");
				loZipMap.put("modifiedBy", "DownloadBatch");
				loCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loZipMap, HHSR5Constants.FOLDER_MAPPING_MAPPER,
						HHSR5Constants.SET_ENTRY_INTO_ZIP_TABLE, HHSR5Constants.JAVA_UTIL_HASH_MAP);

			}

		}
		catch (ApplicationException aoEx)
		{
			throw aoEx;
		}
		return loCount;
	}

	

	/**
	 * Release 5 Contract Restriction This service will fetch all the document
	 * list which are restricted to contracts.
	 * @param aoMybatisSession SQL session as input
	 * @param asUserId user id string
	 * @param loFilterMap hashmap 
	 * @param aoUserId String
	 * @return List of String with restricted access
	 * @throws ApplicationException Exception in case a query fails
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<Document> fetchDocumentListRestrictedContracts(SqlSession aoMybatisSession, String asUserId,
			HashMap loFilterMap) throws ApplicationException
	{
		List<Document> loDocumentContractRestriction = null;
		String lsPermissionType = null;
		HashMap loHashMap = new HashMap();
		try
		{
			if (asUserId != null && !asUserId.isEmpty())
			{
				loHashMap.put(HHSConstants.TT_USERID, asUserId);
				loHashMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
						(String) loFilterMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID));
				loHashMap.put(HHSConstants.DOCUMENT_TYPE, HHSR5Constants.DOCUMENT_TYPE_CONTRACT_RESTRICTION);
				lsPermissionType = (String) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
						HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER, HHSR5Constants.GET_PERMISSION_TYPE,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (lsPermissionType != null)
				{
					if ((lsPermissionType.equalsIgnoreCase("F") || lsPermissionType.equalsIgnoreCase("FP")))
					{
						loDocumentContractRestriction = (List<Document>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.FETCH_DOCUMENT_LIST_FOR_RESTRICTED_CONTRACTS,
								HHSConstants.JAVA_UTIL_HASH_MAP);
					}
					else if (lsPermissionType.equalsIgnoreCase("P"))
					{
						loDocumentContractRestriction = (List<Document>) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
								HHSConstants.MAPPER_CLASS_CONTRACT_MAPPER,
								HHSR5Constants.FETCH_DOCUMENT_LIST_FOR_PROCUREMENT_USER,
								HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching document list for retricted contracts", loAppEx);
			setMoState("Exception occured while fetching document list for retricted contracts");
			throw loAppEx;
		}
		return loDocumentContractRestriction;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is added for migration batch
	 * <ul>
	 * <li>get tempCount by using<b> insertForFilenetInBatch</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoParameterList - List of FolderMappingBean
	 * @return rowCount - Integer value of row count
	 * @throws Exception to handle exceptions
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public int insertForFilenetForBatch(SqlSession aoMybatisSession, HashMap<String,Object> loMap)
			throws Exception
	{
		int rowCount = 0;
		List<FolderMappingBean> aoParameterList = new ArrayList<FolderMappingBean>();
		try
		{
			if(null != loMap)
			{
			aoParameterList = (List<FolderMappingBean>)loMap.get("folderList");
			Iterator loItr = aoParameterList.iterator();
			int tempCount = 0;

			while (loItr.hasNext())
			{
				FolderMappingBean loDocument = (FolderMappingBean) loItr.next();

				if (null != loDocument.getOrganizationType()
						&& !loDocument.getOrganizationType().isEmpty()
						&& (loDocument.getOrganizationType().equalsIgnoreCase("city_org")
								|| loDocument.getOrganizationType().equalsIgnoreCase("agency_org") || loDocument
								.getOrganizationType().equalsIgnoreCase("provider_org")))
				{
					FolderMappingBean lsFilenetId = getDuplicateIdDetail(aoMybatisSession,
							loDocument.getOrganizationId());
					if (null == lsFilenetId)
					{
						tempCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loDocument,
								HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INSERT_FOR_FILENET_IN_BATCH,
								HHSR5Constants.FOLDER_MAPPING_BEAN);
					}
					else
					{
						updateFolderMappingForBatch(aoMybatisSession, loDocument);
					}
					rowCount = rowCount + tempCount;
				}
				else
				{
					LOG_OBJECT.Debug("skipping document with Id::::" + loDocument.getFolderFilenetId()
							+ "::::for invalid Organization Type");
				}

			}
			}
		}
		catch (Exception loAppEx)
		{
			throw new ApplicationException(
					"Exception occured while getting tempCount in DocumentVaultFolderService ", loAppEx);
		}

		return rowCount;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is added to get Org Details i.e. organisation id
	 * <ul>
	 * <li>get lsOrgId by using<b> getOrgId</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderId - Folder id String
	 * @return lsOrgId - Organization id String
	 * @throws Exception to handle all exceptions
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	public String getOrgDetails(SqlSession aoMybatisSession, String aoFolderId) throws Exception
	{
		String lsOrgId = null;
		try
		{
			lsOrgId = (String) DAOUtil.masterDAO(aoMybatisSession, aoFolderId, HHSR5Constants.FOLDER_MAPPING_MAPPER,
					HHSR5Constants.GET_ORG_ID, HHSConstants.JAVA_LANG_STRING);
		}
		catch (Exception loAppEx)
		{
			throw new ApplicationException(
					"Exception occured while getting organisation id in DocumentVaultFolderService ", loAppEx);
		}

		return lsOrgId;
	}
/**
 * This method is used to get duplicate id details for an organisation id
 * @param aoMybatisSession SqlSession object
 * @param aoOrgId Organisation id string
 * @return FolderMappingBean object loBean
 * @throws Exception when any exception occurred wrap it into
	 *             application exception.
 */
	@SuppressWarnings("unchecked")
	public FolderMappingBean getDuplicateIdDetail(SqlSession aoMybatisSession, String aoOrgId) throws Exception
	{
		FolderMappingBean loBean = new FolderMappingBean();
		try
		{
			loBean = (FolderMappingBean) DAOUtil.masterDAO(aoMybatisSession, aoOrgId,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_DUPLICATE_ID,
					HHSConstants.JAVA_LANG_STRING);
		}
		catch (Exception loAppEx)
		{
			throw new ApplicationException(
					"Exception occured while getting FolderMappingBean in DocumentVaultFolderService ", loAppEx);
		}

		return loBean;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is added to get Amendment EPin i.e. a List of Amendment EPin
	 * <ul>
	 * <li>get Amendment Epin list by using<b> getAmendmentEpin</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoDataMap - Map<String, String> object
	 * @return loEpinList - List of String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public List<String> getAmendmentEPin(SqlSession aoMybatisSession, Map<String, String> aoDataMap)
			throws ApplicationException
	{
		List<String> loEpinList = new ArrayList<String>();
		try
		{
			loEpinList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoDataMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_AMENDMENT_EPINS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loEpinList;
	}
/**
 * This method is used to get the status of folder deletion
 * 
 * @param aoMybatiSession SqlSession object
 * @param aoOrgId Organisation id string
 * @return boolean value lbDeleteFolderStatus 
 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
 */
	public boolean emptyRecycleBin(SqlSession aoMybatiSession, String aoOrgId) throws ApplicationException
	{
		boolean lbDeleteFolderStatus = true;
		Integer loDeleteFolderCount = HHSR5Constants.INT_ZERO;
		try
		{
			loDeleteFolderCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoOrgId,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, "emptyRecycleBin", HHSR5Constants.JAVA_LANG_STRING);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error Occured"+loDeleteFolderCount);
			throw aoAppEx;
		}
		// End Release 5
		return lbDeleteFolderStatus;
	}
/**
 * This method is used for updating folder mapping
 * 
 * @param aoMybatisSession SqlSession object
 * @param aofolderMappingBean FolderMappingBean object
 * @return boolean value loUpdateFolderMapping
 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
 */
	public Boolean updateFolderMappingForBatch(SqlSession aoMybatisSession, FolderMappingBean aofolderMappingBean)
			throws ApplicationException
	{
		Boolean loUpdateFolderMapping = true;
		Integer loUpdateCount = HHSR5Constants.INT_ZERO;
		try
		{
			loUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aofolderMappingBean,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.UPDATE_FOLDER_MAPPING,
					HHSR5Constants.FOLDER_MAPPING_BEAN);
			if (loUpdateCount <= HHSR5Constants.INT_ZERO)
			{
				loUpdateFolderMapping = false;
			}
			// checking for update status
			if (loUpdateFolderMapping)
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping FolderMapping create Successfully");
			}
			else
			{
				setMoState("DocumentVaultFolderService: updateFolderMapping() failed to udpate.");
				throw new ApplicationException(
						"Error occured while edit at DocumentVaultFolderService: updateFolderMapping() for :"
								+ aofolderMappingBean.toString());
			}
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("error occured while updateFolderMapping in foldermapping for " + aofolderMappingBean.toString());
			loExp.addContextData("Exception occured while updateFolderMapping in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while updateFolderMapping in foldermapping ", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while updateFolderMapping in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: updateFolderMapping method - failed to create"
					+ aofolderMappingBean.toString() + " \n");
			throw new ApplicationException("Exception occured while update in DocumentVaultFolderService ", loAppEx);
		}
		return loUpdateFolderMapping;
	}
/**
 * This method is used to display Char500 linkage information
 * @param aoMybatisSession SqlSession object
 * @param asDocData document data Hashmap 
 * @param aoDocumentPropHM document properties hashmap
 * @return DocumentBean list loListDoc
 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<DocumentBean> displayChar500LinkageInformation(SqlSession aoMybatisSession, HashMap asDocData,
			HashMap aoDocumentPropHM) throws ApplicationException
	{
		List<DocumentBean> loListDoc = new ArrayList<DocumentBean>();
		List<DocumentBean> loListDocument = new ArrayList<DocumentBean>();
		try
		{
			HashMap loHashMap = new HashMap();
			loHashMap.putAll((HashMap) aoDocumentPropHM.get(asDocData.get(HHSR5Constants.DOC_DATA)));
			loHashMap.put(HHSConstants.DOC_ID, asDocData.get(HHSR5Constants.DOC_DATA));
			List lsDateList = (List) DAOUtil.masterDAO(aoMybatisSession, loHashMap,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.FETCH_CHAR_500_LINKAGE_INFO,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);

			if(null != lsDateList && !lsDateList.isEmpty())
			{
			for (Iterator iterator = lsDateList.iterator(); iterator.hasNext();)
			{
				String lsDate = (String) iterator.next();
			DocumentBean loDocumentBean = new DocumentBean();
			loDocumentBean.setEntityLinked(HHSR5Constants.CHAR_500_SUBMISSION + HHSConstants.SPACE);
			loDocumentBean.setParentEntityName(loHashMap.get(ApplicationConstants.PERIOD_COVER_FROM_MONTH)
					+ HHSConstants.SPACE + loHashMap.get(ApplicationConstants.PERIOD_COVER_FROM_YEAR)
					+ HHSConstants.DATE_SPACE + loHashMap.get(ApplicationConstants.PERIOD_COVER_TO_MONTH)
					+ HHSConstants.SPACE + loHashMap.get(ApplicationConstants.PERIOD_COVER_TO_YEAR));
			loDocumentBean.setDate(lsDate);
			loListDoc.add(loDocumentBean);
			}
			}
			
			HashMap<String, String> loReqProps = new HashMap<String, String>();
			loReqProps.put(HHSR5Constants.USER_ORG_TYPE, asDocData.get(HHSR5Constants.USER_ORG_TYPE).toString());
			loReqProps.put(HHSR5Constants.DOC_DATA, asDocData.get(HHSR5Constants.DOC_DATA).toString());
			loListDocument = displayLinkageInformation(aoMybatisSession, loReqProps);
			loListDoc.addAll(loListDocument);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving data", loAppEx);
			throw new ApplicationException("Exception occured while fetch in database", loAppEx);
		}
		return loListDoc;
	}
	/**
	 * This method is used to merge two lists
	 * @param aoMybatisSession SqlSession object
	 * @param aoAgencyList ProviderBean list
	 * @param aoProviderList ProviderBean list
	 * @return ProviderBean list aoAgencyList
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public List<ProviderBean> mergeTwoLists(SqlSession aoMybatisSession, List<ProviderBean> aoAgencyList, List<ProviderBean> aoProviderList) throws ApplicationException
	{
		try
		{
			if(null != aoAgencyList && null != aoProviderList)
			{
				aoAgencyList.addAll(aoProviderList);
				ProviderBean loProvBeanForCity = new ProviderBean();
				loProvBeanForCity.setHiddenValue("city_org");
				loProvBeanForCity.setDisplayValue("HHS Accelerator");
				aoAgencyList.add(loProvBeanForCity);
			for (Iterator iterator = aoAgencyList.iterator(); iterator.hasNext();)
			{
				ProviderBean providerBean = (ProviderBean) iterator.next();
				LOG_OBJECT.Error(providerBean.getHiddenValue()+":::::::::"+providerBean.getDisplayValue());
				
			}
			}
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while retrieving data", loAppEx);
			throw new ApplicationException("Exception occured while fetch in database", loAppEx);
		}
		return aoAgencyList;
	}
	
	/**
	 * <p>
	 * This method is added as a part of Release 5 for Document Vault This
	 * method is called to get Invoice Number
	 * <ul>
	 * <li>put variables into map</li>
	 * <li>Get loProcurementList using<b> getInvoiceDetails</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoOrgId - Organization id String
	 * @param aoPartialString - String object of Partial String
	 * @return loInvoiceList - List of type ExtendedDocument
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getInvoiceNumber(SqlSession aoMybatisSession, String aoOrgId,
			String aoPartialString) throws ApplicationException
	{
		List<String> loInvoiceList = new ArrayList<String>();
		HashMap<String, Object> loMapDB = new HashMap<String, Object>();
		try
		{
			loMapDB.put(HHSR5Constants.PARTIAL_STRING, "%" + aoPartialString + "%");
			loMapDB.put(HHSR5Constants.USER_ORG, aoOrgId);
			loInvoiceList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loMapDB,
					HHSConstants.MAPPER_CLASS_INVOICE_MAPPER, HHSConstants.GET_INVOICE_DETAILS,
					HHSR5Constants.JAVA_UTIL_HASHMAP);

		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getInvoiceNumber", loExp);
			LOG_OBJECT.Error("error occured while getInvoiceNumber", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getInvoiceNumber", loAppEx);
			throw new ApplicationException("Exception occured while getInvoiceNumber", loAppEx);
		}
		return loInvoiceList;
	}
	/**
	 * This method is used to get Legal name of Organisation 
	 * @param aoHMSection Hashmap 
	 * @param aoMybatiSession SqlSession object
	 * @return hashmap aoHMSection
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap getOrgLegalName(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{
		try
		{
			String lsProviderId = (String)aoHMSection.get("providerID");
			String lsOrgLegalName = (String)DAOUtil.masterDAO(aoMybatiSession, lsProviderId,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.GET_ORG_LEGAL_NAME,
					HHSR5Constants.JAVA_LANG_STRING);
			
			aoHMSection.put("newName", lsOrgLegalName);
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getInvoiceNumber", loExp);
			LOG_OBJECT.Error("error occured while getInvoiceNumber", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getInvoiceNumber", loAppEx);
			throw new ApplicationException("Exception occured while getInvoiceNumber", loAppEx);
		}
		return aoHMSection;
	}
	/**
	 * This method is used to check if Folder Exists In DB
	 * @param aoMybatiSession SqlSession 
	 * @param aoFolderId String
	 * @return hashmap liMoveFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public String checkFolderExistsInDB(SqlSession aoMybatiSession, String aoFolderId) throws ApplicationException
	{
		String liMoveFlag = null;
		try
		{
			if(null != aoFolderId && !aoFolderId.isEmpty() && !aoFolderId.contains(HHSR5Constants.RECYCLE_BIN_ID))
			{
				liMoveFlag = (String)DAOUtil.masterDAO(aoMybatiSession, aoFolderId,
						HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.CHECK_FOLDER_EXISTS_IN_DB,
						HHSR5Constants.JAVA_LANG_STRING);
			}
			else if(null != aoFolderId && !aoFolderId.isEmpty() && aoFolderId.contains(HHSR5Constants.RECYCLE_BIN_ID))
			{
				liMoveFlag = "0";
			}
				
			
			 
		}

		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getInvoiceNumber", loExp);
			LOG_OBJECT.Error("error occured while getInvoiceNumber", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getInvoiceNumber", loAppEx);
			throw new ApplicationException("Exception occured while getInvoiceNumber", loAppEx);
		}
		
		return liMoveFlag;
	}
	/**
	 * This method is used to get Document Id For Linkage
	 * @param aoMybatiSession SqlSession object
	 * @return hashmap aoHMSection
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDocumentIdForLinkage(SqlSession aoMybatiSession) throws ApplicationException
	{
		List<String> lsDocIdList = new ArrayList<String>();
		try
		{
			lsDocIdList = (List<String>)DAOUtil.masterDAO(aoMybatiSession, null,
					HHSR5Constants.FOLDER_MAPPING_MAPPER, "docIdForLinkage",
					null);
			LOG_OBJECT.Error("Doc Size fetched from Database::::::::::::::::"+lsDocIdList.size());
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getDocumentIdForLinkage", loExp);
			LOG_OBJECT.Error("error occured while getDocumentIdForLinkage", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getDocumentIdForLinkage", loAppEx);
			throw new ApplicationException("Exception occured while getDocumentIdForLinkage", loAppEx);
		}
		
		return lsDocIdList;
	}
	
	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert folder structure hierarchy in folder mapping table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderMapList list of folders
	 * @param aoEntityName Operation performed on entity
	 * @param loRowCount No of rows inserted
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 *             
	 *		commenting throw exception in release 4.0.2 to fix create folder issue for defect # 8408
	 */
	@SuppressWarnings("rawtypes")
	public int insertFilenetAudit(SqlSession aoMybatisSession, List<FolderMappingBean> aoFolderMapList,
			String aoEntityName) throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if (null != aoFolderMapList)
			{
				for (Iterator iterator = aoFolderMapList.iterator(); iterator.hasNext();)
				{

					FolderMappingBean folderMappingBean = (FolderMappingBean) iterator.next();
					if (null != folderMappingBean && null != folderMappingBean.getFolderFilenetId())
					{
						insertFilenetAudit(aoMybatisSession, folderMappingBean,aoEntityName);
						loRowCount++;
					}

				}
			}
			// Adding if check for creation of Document Vault Folder

		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While insertFilenetAudit", loExp);
			//throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: insertFilenetAudit method - failed to Insert"
					+ " \n");
			//throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}
	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert audit into folder mapping audit for File Creation table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderMapList list of folders
	 * @param aoOrganizationType type of organisation
	 * @param aoParameter List of type FolderMappingBean
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 *             using return type of insertFilenetAudit in loRowCount parameter for defect # 8408.
	 */
	@SuppressWarnings("rawtypes")
	public int insertFileUploadAudit(SqlSession aoMybatisSession, HashMap<String, String> aoReqMap,HashMap<String, String> aoDocMap,
			String aoEntityName) throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if (null != aoReqMap)
			{
				FolderMappingBean loBean = new FolderMappingBean();
				loBean.setParentFolderFilenetId((String) aoReqMap.get(HHSR5Constants.CUSTOM_FLDR_ID));
				loBean.setFolderFilenetId((String) aoDocMap.get(HHSR5Constants.DOCUMENT_DATA));
				loBean.setModifiedBy((String) aoReqMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
				loBean.setCreatedBy((String) aoReqMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
				loBean.setOrganizationType((String) aoReqMap.get(HHSConstants.ORGANIZATION_ID_KEY));		
				loRowCount = insertFilenetAudit(aoMybatisSession, loBean, aoEntityName);
			}

		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While creating Folder", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: insertFileUploadAudit method - failed to insert"
					+ " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}
	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert audit in  folder mapping audit table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoParameter List of type FolderMappingBean
	 * @param aoOrganizationType type of organisation
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("rawtypes")
	public int insertFilenetAudit(SqlSession aoMybatisSession, FolderMappingBean aoParameter,
			String aoEntityName)
			throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if (null != aoParameter && null != aoParameter.getFolderFilenetId())
			{	//Changes for Ad-Hoc first time folder creation in provider issue
				aoParameter.setEntityName(aoEntityName);
				if(null != aoParameter.getParentFolderFilenetId() && aoParameter.getParentFolderFilenetId().equalsIgnoreCase(HHSConstants.DELIMITER_SINGLE_HASH))
				{
					aoParameter.setCreatedBy(HHSConstants.SYSTEM_USER);
					aoParameter.setModifiedBy(HHSConstants.SYSTEM_USER);
				}
				//Changes for Ad-Hoc first time folder creation in provider issue
				loRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParameter,
						HHSR5Constants.FOLDER_MAPPING_MAPPER, HHSR5Constants.INSERT_FILENET_AUDIT,
						HHSR5Constants.FOLDER_MAPPING_BEAN);
			}

		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While insertFilenetAudit", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: insertFilenetAudit method - failed to Insert"
					+ " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}
	/**
	 * This method is added as a part of Release 5 for Document Vault This
	 * method will insert audit into folder mapping audit for File Creation table
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoFolderMapList list of folders
	 * @param aoOrganizationType type of organisation
	 * @param aoParameter List of type FolderMappingBean
	 * @return no. of rows inserted in table
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("rawtypes")
	public int insertFileOperationAudit(SqlSession aoMybatisSession, List<Document> aoListItems, String asParentId,
			String aoMoveFromId, List<String> aoEntityName, HashMap<String, String> aoMapOrg) throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		List<Document> loAuditList = new ArrayList<Document>();
		try
		{
			if(null !=aoMoveFromId)
			{
				asParentId = aoMoveFromId + HHSR5Constants.MOVE_ARROW + asParentId  ;
			}
			loAuditList.addAll(aoListItems);
			int liCount = 1;
			for (Iterator<Document> loIterator = loAuditList.iterator(); loIterator.hasNext();)
			{
				liCount = 1;
				Document loDocument = (Document) loIterator.next();
				if (null == loDocument.getDocType() || loDocument.getDocType().isEmpty()
						|| loDocument.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{
					liCount = 0;
				}
				loRowCount = insertOperationAudit(aoMybatisSession, asParentId, aoEntityName, aoMapOrg, loDocument,
						liCount);
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While creating Folder", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: deleteFromFolderMapping method - failed to delete"
					+ " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}

	/** This method is called from insertFileOperationAudit method. It is
	 *  inserting audit Bean for Filenet Audit on operations
	 * @param aoMybatisSession - Sql Session
	 * @param asParentId - Parent filenetId
	 * @param aoEntityName - Filenet EntityName
	 * @param aoMapOrg - User Info Map
	 * @param aoIterator - Iterator Object
	 * @param aiCount - Int count
	 * @return
	 * @throws ApplicationException
	 */
	private Integer insertOperationAudit(SqlSession aoMybatisSession, String asParentId, List<String> aoEntityName,
			HashMap<String, String> aoMapOrg, Document aoDocument, int aiCount) throws ApplicationException
	{
		Integer loRowCount;
		FolderMappingBean loBean = new FolderMappingBean();
		if (null != asParentId)
		{
			loBean.setParentFolderFilenetId(asParentId);
		}
		loBean.setFolderFilenetId(aoDocument.getDocumentId());
		loBean.setModifiedBy((String) aoMapOrg.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
		loBean.setCreatedBy((String) aoMapOrg.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
		loRowCount = insertFilenetAudit(aoMybatisSession, loBean, aoEntityName.get(aiCount));
		return loRowCount;
	}

	/**
	 * This method is used to Create Document List for Audit Insert
	 * 
	 * @param aoMybatiSession SqlSession object
	 * @param aoHashMapEntity HashMap
	 * @return List value loRecycleBinIds
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public List<Document> createRecycleBinList(HashMap aoHashMapEntity) throws ApplicationException
	{
		List<Document> loRecycleBinIds = new ArrayList<Document>();
		Document loDocBean = null;
		try
		{
			if (null != aoHashMapEntity && !aoHashMapEntity.isEmpty())
			{
				Iterator loIter = aoHashMapEntity.keySet().iterator();
				while (loIter.hasNext())
				{
					loDocBean = new Document();
					String lsKey = (String) loIter.next();
					String lsDocType = (String) aoHashMapEntity.get(lsKey);

					if (null != lsDocType && !lsDocType.isEmpty()
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.NULL))
					{
						loDocBean.setDocumentId(lsKey);
						loDocBean.setDocType(lsDocType);
					}
					else
					{
						loDocBean.setDocumentId(lsKey);
					}
					loRecycleBinIds.add(loDocBean);
				}
			}
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error Occured in createRecycleBinList()");
		}
		return loRecycleBinIds;
	}
	
	/**
	 * This method is used to Create Document/Folder List for Audit Insert
	 * 
	 * @param aoMybatiSession SqlSession object
	 * @param aoHashMapEntity HashMap
	 * @return List value loRecycleBinIds
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public List<Document> createUnShareList(HashMap aoHashMapEntity) throws ApplicationException
	{
		List<Document> loUnShareIds = new ArrayList<Document>();
		Document loDocBean = null;
		try
		{
			if (null != aoHashMapEntity && !aoHashMapEntity.isEmpty())
			{
				Iterator loIter = aoHashMapEntity.keySet().iterator();
				while (loIter.hasNext())
				{
					loDocBean = new Document();
					String lsKey = (String) loIter.next();
					String lsDocType = (String) aoHashMapEntity.get(lsKey);
					if (lsKey.contains(HHSConstants.COMMA))
					{
						lsKey = lsKey.substring(0, lsKey.indexOf(HHSConstants.COMMA));
					}
					if (null != lsDocType && !lsDocType.isEmpty()
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.NULL)
							&& lsDocType.equalsIgnoreCase(HHSR5Constants.FOLDER))
					{
						loDocBean.setDocumentId(lsKey);
					}
					else
					{
						loDocBean.setDocumentId(lsKey);
						loDocBean.setDocType(lsDocType);
					}
					loUnShareIds.add(loDocBean);
				}
			}
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error Occured in createRecycleBinList()");
		}
		return loUnShareIds;
	}
	
	
	@SuppressWarnings("unchecked")
	public int insertInAuditForFilenetForBatch(SqlSession aoMybatisSession, HashMap<String,Object> loMap)
			throws ApplicationException
	{
		Integer loRowCount = HHSR5Constants.INT_ZERO;
		try
		{
			if(null != loMap)
			{
				if(loMap.containsKey("entityName"))
				{
					List<String> loDbList = (List<String>)loMap.get("DocIdList");
					for (Iterator iterator = loDbList.iterator(); iterator.hasNext();)
					{
						String lsDocId = (String) iterator.next();
						loMap.put("folderFilenetId", lsDocId);
						DAOUtil.masterDAO(aoMybatisSession, loMap,
								HHSR5Constants.FOLDER_MAPPING_MAPPER, "insertFilenetAuditForBatch",
								HHSR5Constants.JAVA_UTIL_HASH_MAP);
						loRowCount++;
					}
				}
				else
				{
					
					HashMap<String,Object> loDbMap = (HashMap<String,Object>)loMap.get("DbMap");
					for (Map.Entry<String, Object> loEntry : loDbMap.entrySet())
					{
						LOG_OBJECT.Info("Map coming with organization_id is:::"+loEntry.getKey());
						HashMap<String,Object> loDataMap = (HashMap<String,Object>)loEntry.getValue();
						
						List<String> loDbList = (List<String>)loDataMap.get("DocIdList");
						LOG_OBJECT.Info("Document Id list after filing  are:::"+loDbList);
						List<String> loSharingFlagList = (List<String>)loDataMap.get("SharingDocIdList");
						LOG_OBJECT.Info("Sharing Id list after filing  are:::"+loSharingFlagList);
						for (Iterator iterator = loDbList.iterator(); iterator.hasNext();)
						{
							String lsDocId = (String) iterator.next();
							loDataMap.put("folderFilenetId", lsDocId);
							DAOUtil.masterDAO(aoMybatisSession, loDataMap,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, "insertFilenetAuditForBatch",
									HHSR5Constants.JAVA_UTIL_HASH_MAP);
							loRowCount++;
						}
						for (Iterator iterator = loSharingFlagList.iterator(); iterator.hasNext();)
						{
							String lsDocId = (String) iterator.next();
							loDataMap.put("folderFilenetId", lsDocId);
							loDataMap.put("entityName", "sharingTrueFlag");
							DAOUtil.masterDAO(aoMybatisSession, loDataMap,
									HHSR5Constants.FOLDER_MAPPING_MAPPER, "insertFilenetAuditForBatch",
									HHSR5Constants.JAVA_UTIL_HASH_MAP);
							loRowCount++;
							
						}
						
					}
					
				}
				
				
				
				
				
				
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("General Error occured, Please try again Later");
			loExp.addContextData("General Error occured, Please try again Later", loExp);
			LOG_OBJECT.Error("Error Occured While insertFilenetAudit", loExp);
			throw loExp;
		}
		// handling exception other than ApplicationException
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while delete in DocumentVaultFolderService ", loAppEx);
			setMoState("Transaction Failed:: DocumentVaultFolderService: insertFilenetAudit method - failed to Insert"
					+ " \n");
			throw new ApplicationException("General Error occured, Please try again Later", loAppEx);
		}

		return loRowCount;
	}
	// End Release 5
}
