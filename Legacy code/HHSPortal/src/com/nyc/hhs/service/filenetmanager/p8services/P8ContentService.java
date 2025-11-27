package com.nyc.hhs.service.filenetmanager.p8services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BulkDownloadBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ContentOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is added for Release 5 This class is used to execute the p8 CE
 * operations and this will be called from controller class and execute the
 * respective operations to complete the transactions
 * 
 */

public class P8ContentService extends P8HelperServices
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ContentService.class);

	/**
	 * This is the method which will be used for uploading any document into
	 * Document Vault. This method will call other internal methods to check
	 * whether another version of this document is exist in system or not. This
	 * method is using FIleNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession : A bean which will have details about user
	 * @param aoIS : FileInputStream having actual content of the document
	 * @param aoPropertyMap : A HashMap having information about document
	 *            properties
	 * @param abDocExist : A boolean Field which will represent whether document
	 *            already exist in system or not
	 * @param abCheckExist : Another boolean Field which tells about the
	 *            significance of abDocExist Field
	 * @return a String which will content Document ID
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, Object> createDVdocument(P8UserSession aoUserSession, FileInputStream aoIS,
			HashMap aoPropertyMap, Boolean abDocExist, Boolean abCheckExist) throws ApplicationException
	{
		boolean lbLinkedToApp = false;
		String lsDocId = null;
		String lsDocType = null;
		String lsDocTitle = null;
		String lsProviderId = null;
		String lsOrgType = null;
		String lsDocCategory = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.IS, aoIS);
		loHmReqExceProp.put(P8Constants.DB_EXIST, abDocExist);
		loHmReqExceProp.put(P8Constants.CHECK_LIST, abCheckExist);
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		LOG_OBJECT.Info("Entered P8ContentService.createDVdocument() with parameters::" + loHmReqExceProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching ObjectStore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

			lsDocType = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_TYPE);
			lsDocCategory = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY);
			lsDocTitle = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
			lsProviderId = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID);
			lsOrgType = (String) aoPropertyMap.get(HHSR5Constants.ORGANIZATION_ID_KEY);
			lbLinkedToApp = (Boolean) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION);

			loHmReqExceProp.put(P8Constants.DOC_TYPE, lsDocType);
			loHmReqExceProp.put(P8Constants.DOC_TITLE, lsDocTitle);
			loHmReqExceProp.put(P8Constants.PROVIDER_ID, lsProviderId);

			if (lsDocType == null || lsDocTitle == null || lsProviderId == null)
			{
				ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE, P8Constants.MO4));
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}

			// Getting folder Path for new document
			String lsFldPath = contentOperationHelper.getFolderPath(loXmlDoc, lsProviderId, lsDocType, lsDocCategory,
					lsOrgType);
			loHmReqExceProp.put(P8Constants.FIELD_PATH, lsFldPath);

			// Getting document class for new document
			String lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, lsDocType, lsDocCategory);
			loHmReqExceProp.put(P8Constants.LS_PROVIDER_ID, lsDocClassName);

			// Start of change for defect 6218

			// if FlagCheck in not exist then checking document manually
			if (!abCheckExist)
			{
				lsDocId = checkDocumentExist(loXmlDoc, loOS, lsDocClassName, lsProviderId, lsDocTitle, lsDocType,
						lsDocCategory, lsOrgType);
				loReturnMap = createDocument(lsDocId, lbLinkedToApp, aoPropertyMap, lsDocType, lsFldPath,
						lsDocClassName, lsOrgType, loOS, aoIS);

				if ((Boolean) loReturnMap.get("filedToCustomFolder"))
				{
					setParentFolderProprty(loOS, aoPropertyMap, (String) loReturnMap.get(HHSR5Constants.DOC_ID));
				}

			}
			// End of change for defect 6218
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document has been Created" + lsDocId);
		}
		catch (ApplicationException loEx)
		{
			String lsMessage = loEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
						HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
				throw new ApplicationException(lsMessage, loEx);
			}
			loEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDVdocument()::", loEx);
			throw loEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSConstants.FILE_UPLOAD_FAIL_MESSAGE), aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Info("P8ContentService: document uploaded. method:createDVdocument. Time Taken(seconds):: "
				+ liTimediff);
		LOG_OBJECT.Info("Exiting P8ContentService.createDVdocument() ");
		return loReturnMap;
	}

	/**
	 * This method is used for creating a new version of a document if it is
	 * linked to an application This method is using FIleNet session for
	 * performing actions in filenet using FileNet API
	 * 
	 * @param asDocId : A string having the document id
	 * @param abLinkedToApp : A boolean Field which will represent if the
	 *            document is linked to an application
	 * @param aoPropertyMap : An hashmap containing the document properties
	 * @param asDocType : A string representing the doc type
	 * @param asFldPath : A string representing the folder path
	 * @param asDocClassName : A string representing the doc class name
	 * @param asOrgId : A string representing the org id
	 * @param aoOS : An object store object
	 * @param aoIS : FileInputStream having actual content of the document
	 * @return : String containing the document id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap<String, Object> createDocument(String asDocId, boolean abLinkedToApp, HashMap aoPropertyMap,
			String asDocType, String asFldPath, String asDocClassName, String asOrgId, ObjectStore aoOS,
			FileInputStream aoIS) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		loHmReqExceProp.put(P8Constants.LINKED_APP, abLinkedToApp);
		loHmReqExceProp.put(P8Constants.DOC_TYPE, asDocType);
		loHmReqExceProp.put(P8Constants.FIELD_PATH, asFldPath);
		loHmReqExceProp.put(P8Constants.DOC_CLASS_NAME, asDocClassName);
		loHmReqExceProp.put(P8Constants.ORG_ID, asOrgId);
		LOG_OBJECT.Info("Entered P8ContentService.createDocument() with parameters::" + loHmReqExceProp.toString());
		try
		{
			if (asDocId != null)
			{
				// now check whether the document is linked to
				// application or not
				if (!abLinkedToApp)
				{
					// delete the document
					contentOperationHelper.deleteDocument(aoOS, asDocId);
					// create the new document
					aoPropertyMap.put(P8Constants.DOCUMENT_ID, asDocId);
					loReturnMap = contentOperationHelper.createDocument(aoOS, aoIS, asDocClassName, asDocType,
							aoPropertyMap, asFldPath);
				}
				else
				{
					ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSR5Constants.MESSAGE_SAME_TYPE_EXIST));
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
			}
			else
			{
				// if document id is not present then creating new
				// document.
				loReturnMap = contentOperationHelper.createDocument(aoOS, aoIS, asDocClassName, asDocType,
						aoPropertyMap, asFldPath);
			}
			LOG_OBJECT.Info("Exiting P8ContentService.createDocument() ");
			return loReturnMap;
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While creating document");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDocument()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(
					"Error while uploading new document into document vault : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while uploading new document into document vault :", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method is used for checking whether any particular document exist in
	 * the system or not This method is using FileNet session for performing
	 * actions in filenet using FileNet API
	 * 
	 * @param aoUserSession a user bean having details about users
	 * @param asProviderId a string value having information about provider id
	 * @param asDocTitle a string value which is used as document title and
	 *            referred for searching document.
	 * @param asDocType a string value having relationship between business and
	 *            FILENET classification
	 * @param asDocCategory Document category of the document
	 * @param aoOrgId a string value having organization Id
	 * @return a string value which is id of the document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String checkDocumentExist(P8UserSession aoUserSession, String asProviderId, String asDocTitle,
			String asDocType, String asDocCategory, String aoOrgId) throws ApplicationException
	{
		String lsDocId = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() with parameters::" + loHmReqExceProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			// Fetching DocumentClass Name from DocType
			String lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, asDocType, asDocCategory);
			loHmReqExceProp.put(P8Constants.DOC_CLASS_NAME, lsDocClassName);
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			loHmReqExceProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
			loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
			loHmReqExceProp.put(P8Constants.AO_ORG_ID, aoOrgId);
			LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() with parameters::"
					+ loHmReqExceProp.toString());
			lsDocId = checkDocumentExist(loXmlDoc, loOS, lsDocClassName, asProviderId, asDocTitle, asDocType,
					asDocCategory, aoOrgId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Exists in the system" + lsDocId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while checking if the document exists");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists: ", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking if the document exists");
			ApplicationException loAppex = new ApplicationException("Error while checking if the document exists : ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkDocumentExist() ");
		return lsDocId;
	}

	/**
	 * A private method for checking whether document exist in FILENET or not.
	 * 
	 * @param aoXMLDOM : a XML dom object
	 * @param aoObjStore : FILENET ObjectStore object
	 * @param asDocClassName : Document Class Name
	 * @param asProviderID : a String value for provider id
	 * @param asDocTitle : a String Value for document Title
	 * @param asDocType : a String value for DocType
	 * @param asDocCategory : A string value for document Category
	 * @param aoOrgId a string value having organisation Id
	 * @return document Id : a string value for document id
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String checkDocumentExist(Object aoXMLDom, ObjectStore aoObjStore, String asDocClassName,
			String asProviderID, String asDocTitle, String asDocType, String asDocCategory, String asOrgId)
			throws ApplicationException
	{
		String lsDocId;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoObjStore.get_Name());
		loHmReqExceProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmReqExceProp.put(P8Constants.AS_PROVIDER_IDS, asProviderID);
		loHmReqExceProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
		loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
		loHmReqExceProp.put(P8Constants.AS_ORG_ID, asOrgId);
		LOG_OBJECT.Info("Entered P8ContentService.checkDocumentExist() " + loHmReqExceProp.toString());

		try
		{
			// Fetching Folder Path for XMLDOM
			String lsFolderPath = contentOperationHelper.getFolderPath(aoXMLDom, asProviderID, asDocType,
					asDocCategory, asOrgId);
			loHmReqExceProp.put(P8Constants.LS_FOLDER_PATH, lsFolderPath);

			// Fetching DocumentId
			lsDocId = contentOperationHelper.getDocumentId(aoObjStore, asDocClassName, lsFolderPath, asDocTitle,
					asDocType);
			setMoState("Document Exists in the filenet" + lsDocId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while checking if the document exists in filenet");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists in filenet ", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking if the document exists in filenet");
			ApplicationException loAppex = new ApplicationException(
					"Error while checking if the document exists in filenet: ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking if the document exists in filenet: ", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkDocumentExist()");
		return lsDocId;
	}

	/**
	 * This method is used for checking whether any document is shared with any
	 * other provider or not. If yes, then it will retrieve list of providers
	 * with whom this document is shared This method is using FileNet session
	 * for performing actions in filenet using FileNet API
	 * 
	 * @param aoUserSession : a user bean having information about user
	 * @param asDocId : a UNIQUE document GUID
	 * @return a array list having list of provider id with whom this document
	 *         is shared.
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getDocumentSharedWith(P8UserSession aoUserSession, String asDocId) throws ApplicationException
	{

		List loProviderIdList = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
		LOG_OBJECT.Info("Entered P8ContentService.getDocumentSharedWith() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{

			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loProviderIdList = contentOperationHelper.getProviderId(loOS, asDocId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("If document is shared with any other provider,retrieving the list of providers with whom this it is shared"
					+ loProviderIdList);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in document Sharing with other provider");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in document Sharing with other provider", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in document Sharing with other provider");
			ApplicationException loAppex = new ApplicationException("Error in document Sharing with other provider : ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in document Sharing with other provider", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentService.getDocumentSharedWith()");
		return loProviderIdList;
	}

	/**
	 * This method is used for sharing documents with different providers. This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession : a user bean having information about user
	 * @param aoDocumentsList : a string value having document list
	 * @param aoProviderAgencyMap : a hashmap having provider agency details
	 * @return a boolean variable which will confirm whether shared successfully
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean shareDocuments(P8UserSession aoUserSession, List aoDocumentsList, HashMap aoProviderAgencyMap,
			String asSharedBy, HashMap aoReqMap) throws ApplicationException
	{

		boolean lbFlagShared = false;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.DOCUMENTS_LIST, aoDocumentsList);
		loHmReqExceProp.put(P8Constants.PROVIDER_AGENCY_MAP, aoProviderAgencyMap);
		LOG_OBJECT.Info("Entered P8ContentService.shareDocuments() with parameters::" + loHmReqExceProp.toString());

		try
		{
			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			aoDocumentsList = contentOperationHelper.checkParentChildRelationShip(loOS, aoDocumentsList, "share");
			lbFlagShared = contentOperationHelper.shareDocuments(loOS, aoDocumentsList, aoProviderAgencyMap,
					asSharedBy, aoReqMap);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Sharing documents with different providers." + lbFlagShared);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in share documents with different providers");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in share documents with different providers", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in share documents with different providers");
			ApplicationException loAppex = new ApplicationException("Error in share documents : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in share documents with different providers", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentService.shareDocuments()");
		return lbFlagShared;
	}

	/**
	 * This method is used for removing document sharing with provider This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoShareDocuments a Hash Map with key as provider and value as
	 *            document id
	 * @param asAction a string having information about the action           
	 * @return a boolean variable whether the sharing is remove successfully
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean removeSharedDocuments(P8UserSession aoUserSession, HashMap aoShareDocuments, HashMap aoReqMap,String asAction)
			throws ApplicationException
	{
		boolean lbFlagShared = false;

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.SHARE_DOCUMENTS, aoShareDocuments);
		LOG_OBJECT.Info("Entered P8ContentService.removeSharedDocuments() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			if (null != aoShareDocuments && !aoShareDocuments.isEmpty())
			{
				// Fetching FILENET objectstore from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				//parameter added in method to remove sharing of child 4.0.2.0
				lbFlagShared = contentOperationHelper.removeDocumentsSharing(loOS, aoShareDocuments,asAction);
				//parameter added in method to remove sharing of child 4.0.2.0
				filenetConnection.popSubject(aoUserSession);
				setMoState("Removing document sharing with the provider" + lbFlagShared);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in removing document sharing with provider");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in removing document sharing with provider", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in removing document sharing with provider");
			ApplicationException loAppex = new ApplicationException("Error in document sharing with provider: ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in removing document sharing with provider", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentService.removeSharedDocuments()");
		return lbFlagShared;
	}

	/**
	 * This method is used for fetching Document Properties This method is using
	 * FileNet session for performing actions in filenet using FileNet API
	 * <ul>
	 * <li>Method updated in Release 5, adding one extra parameter in arguments
	 * for archiving flag</li>
	 * </ul>
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocType a string value used to denote document type.
	 * @param aoHmReqProps a hash map containing the properties name value pair
	 * @param aoFilterMap a hash map containing the attributes to filter the
	 *            query
	 * @param aoOrderByMap a hash map containing the details about the shoring
	 *            of the fields
	 * @param abFilterIncluded a boolean value which indicate whether we need a
	 *            filter or not
	 * @param asPagesize a string value having page size
	 * @return List loPropsList a list containing all the properties values for
	 *         the documents
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getBRApplicationDocsProps(P8UserSession aoUserSession, String asDocType, HashMap aoHmReqProps,
			HashMap aoFilterMap, HashMap aoOrderByMap, Boolean abFilterIncluded, String asPagesize)
			throws ApplicationException
	{
		String lsDocCategory = null;
		List loPropsList = null;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.getBRApplicationDocsProps() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			if (null != aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY))
			{
				lsDocCategory = (String) aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY);
			}
			loPropsList = getDocumentProperties(aoUserSession, asDocType, aoHmReqProps, aoFilterMap, aoOrderByMap,
					abFilterIncluded, asPagesize, lsDocCategory, loHmExcepRequiredProp);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in fetching Document Properties");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in fetching Document Properties");
			ApplicationException loAppex = new ApplicationException("Error in fetching Document Properties : ", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getBRApplicationDocsProps()");
		return loPropsList;
	}

	/**
	 * This method is used to get the help type documents from P8 server This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocType a string value used to denote document type.
	 * @param aoHmReqProps a hash map containing the properties name value pair
	 * @param aoFilterMap a hash map containing the attributes to filter the
	 *            query
	 * @param aoOrderByMap a hash map containing the details about the shoring
	 *            of the fields
	 * @param abFilterIncluded a boolean value which indicate whether we need a
	 *            filter or not
	 * @return loPropsList loPropsList a list containing all the properties
	 *         values for the documents
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getHelpDocsProps(P8UserSession aoUserSession, String asDocType, HashMap aoHmReqProps,
			HashMap aoFilterMap, HashMap aoOrderByMap, Boolean abFilterIncluded) throws ApplicationException
	{
		List loPropsList = null;
		String lsDocClassName = ApplicationConstants.EMPTY_STRING;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.getHelpDocsProps() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			if (null != asDocType)
			{
				// Fetching DocumentClass Name from DocType
				lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, asDocType, null);
				loHmExcepRequiredProp.put(P8Constants.DOC_CLASS_NAME, lsDocClassName);
				loHmExcepRequiredProp.put(P8Constants.AS_DOC_TYPE, asDocType);
				loHmExcepRequiredProp.put(P8Constants.HM_REQ_PROPS, aoHmReqProps);
				loHmExcepRequiredProp.put(P8Constants.FILTER_MAP, aoFilterMap);
				loHmExcepRequiredProp.put(P8Constants.ORDER_BY_MAP, aoOrderByMap);
				loHmExcepRequiredProp.put(P8Constants.FILTER_INCLUDED, abFilterIncluded);
			}
			loPropsList = contentOperationHelper.getHelpDocsProperties(aoUserSession, loOS, lsDocClassName,
					aoHmReqProps, aoFilterMap, aoOrderByMap, abFilterIncluded);

			filenetConnection.popSubject(aoUserSession);
			setMoState(P8Constants.DOCUMENT_PROPERTIES + loPropsList);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in fetching Document Properties");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in fetching Document Properties");
			ApplicationException loAppex = new ApplicationException("Error in fetching Document Properties : ", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getHelpDocsProps()");
		return loPropsList;
	}

	// As of now the function will delete the entire document series of the
	// document
	/**
	 * This method is for deleting the document for the given document ID This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @return boolean variable a value to confirm the delete of document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean deleteDocument(P8UserSession aoUserSession, String asDocId) throws ApplicationException
	{
		boolean lbIsDeleted = false;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.deleteDocument() with asDocId::" + asDocId);
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				lbIsDeleted = contentOperationHelper.deleteDocument(loOS, asDocId);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Deleting The document from FileNet" + lbIsDeleted);
		}
		catch (ApplicationException aoAppex)
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.DOCUMENT_IDS, asDocId);
			String lsMessage = aoAppex.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
				throw new ApplicationException(lsMessage, aoAppex);
			}
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Runtime Error in Fetching Filenet", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.DOCUMENT_IDS, asDocId);
			setMoState("Error While Deleting The document from FileNet");
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While Deleting The document from FileNet", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.deleteDocument()");
		return lbIsDeleted;
	}

	/**
	 * This Method gets all versions of documents for the given document ID.
	 * This method is using FileNet session for performing actions in filenet
	 * using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @param aoRequiredMap a hash map containing the properties name value pair
	 * @return HashMap a hash map containing document id as key and property map
	 *         as value
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap<String, HashMap> getDocVersionsDetails(P8UserSession aoUserSession, String asDocId,
			HashMap aoRequiredMap) throws ApplicationException
	{
		HashMap<String, HashMap> loResultMap = null;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
			loHmReqExceProp.put(HHSR5Constants.AO_REQUIRED_MAP, aoRequiredMap);
			LOG_OBJECT.Info("Entered P8ContentService.getDocVersionsDetails() with parameters::"
					+ loHmReqExceProp.toString());

			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loResultMap = P8ContentOperations.getDocPropertiesAllVersions(loOS, asDocId, aoRequiredMap);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Properties for All version" + loResultMap);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting properties for All version");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting properties for All version", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting properties for All version");
			ApplicationException loAppex = new ApplicationException("Error While getting properties for All version",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting properties for All version", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocVersionsDetails()");
		return loResultMap;
	}

	/**
	 * This method is used to get document content from P8 server This method is
	 * using FileNet session for performing actions in filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @return HashMap containing the content of the document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentContent(P8UserSession aoUserSession, String asDocId) throws ApplicationException
	{
		InputStream loContent = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap loOutputHashMap = null;
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
			LOG_OBJECT.Info("Entered P8ContentService.getDocumentContent() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loOutputHashMap = contentOperationHelper.getDocumentContent(loOS, asDocId);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Content" + loContent);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentContent()");
		return loOutputHashMap;
	}

	/**
	 * This is used to get the content of document according to the type passed
	 * to it This method is using FileNet session for performing actions in
	 * filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocType a string value used to denote document type.
	 * @return HashMap containing the content of the document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentContentByType(P8UserSession aoUserSession, String asDocType) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap loOutput = null;
		try
		{
			if (null == aoUserSession)
			{
				throw new ApplicationException("Internal Error Occured While Processing Your Request");
			}

			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
			LOG_OBJECT.Info("Entered P8ContentService.getDocumentContentByType() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
				loOutput = new P8ContentOperations().getDocumentContentByType(loOS, loXmlDoc, asDocType);

				filenetConnection.popSubject(aoUserSession);
				setMoState("Document content by type" + loOutput);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting document content by type");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting document content by type", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting document content by type");
			ApplicationException loAppex = new ApplicationException("Error While getting document content by type",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting document content by type", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentContentByType()");
		return loOutput;
	}

	/**
	 * This method is used to save the document properties in file net This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @param asDocType a string value used to denote document Type.
	 * @param aoHm a hash map containing properties as key
	 * @return boolean value to confirm save successful
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean saveDocumentProperties(P8UserSession aoUserSession, String asDocId, String asDocType, HashMap aoHm)
			throws ApplicationException
	{
		boolean lbFlag = false;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmExcepRequiredProp.put(P8Constants.AS_DOC_ID, asDocId);
		loHmExcepRequiredProp.put(P8Constants.AS_DOC_TYPE, asDocType);
		loHmExcepRequiredProp.put(P8Constants.AO_HM, aoHm);
		LOG_OBJECT.Info("Entered P8ContentService.saveDocumentProperties() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{
			if (null != asDocId)
			{
				BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.FILENETDOCTYPE);
				// Fetching FILENET object store from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

				lbFlag = contentOperationHelper.setDocProperties(loOS, asDocId, aoHm, asDocType);
				filenetConnection.popSubject(aoUserSession);
				setMoState("Saving the document properties in file net" + lbFlag);
			}

		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in saving document properties in file net");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in saving document properties in file net", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in saving document properties in file net");
			ApplicationException loAppex = new ApplicationException(
					"Error in saving document properties in file net: ", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in saving document properties in file net", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.saveDocumentProperties()");
		return lbFlag;
	}

	/**
	 * This method is used to save the document properties in file net for
	 * Contract and Budget Related Documents .This method is using FileNet
	 * session for performing actions in filenet using FileNet API
	 * 
	 * This method is added as part of Enhancement 6000 for Release 3.8.0
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @param asDocType a string value used to denote document Type.
	 * @param aoHm a hash map containing properties as key
	 * @return boolean value to confirm save successful
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean saveContractDocumentProps(P8UserSession aoUserSession, List<String> asDocIdList, HashMap aoHm)
			throws ApplicationException
	{
		boolean lbFlag = false;
		LOG_OBJECT.Info("Entered P8ContentService.saveContractDocumentProps()");

		try
		{
			if (null != asDocIdList && !asDocIdList.isEmpty())
			{
				BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.FILENETDOCTYPE);
				// Fetching FILENET object store from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				for (String lsDocId : asDocIdList)
				{
					lbFlag = contentOperationHelper.setDocProperties(loOS, lsDocId, aoHm, HHSConstants.EMPTY_STRING);
				}
				filenetConnection.popSubject(aoUserSession);
				setMoState("Saving the document properties in file net" + lbFlag);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in saving document properties in file net");
			LOG_OBJECT.Error(
					"P8ContentService.saveContractDocumentProps() :: Error in saving document properties in file net",
					aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in saving document properties in file net");
			ApplicationException loAppex = new ApplicationException(
					"Error in saving document properties in file net: ", aoEx);
			LOG_OBJECT.Error(
					"P8ContentService.saveContractDocumentProps() :: Error in saving document properties in file net",
					aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.saveContractDocumentProps()");
		return lbFlag;
	}

	/**
	 * This method will return the properties with values and takes list of
	 * document ids This method is using FileNet session for performing actions
	 * in filenet using FileNet API <li>Need to check lock status before any
	 * execution in this method - Release 5</li>
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoHmRequiredProps a hash map containing properties as key
	 * @param aoDocumentsIdList list of documents
	 * @return HashMap a hash map containing properties as key value as key
	 *         value pair
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getBRDcoumentPropertiesById(P8UserSession aoUserSession, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{
		HashMap loHmProps = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSR5Constants.HM_REQUIRED_PROPS, aoHmRequiredProps);
		loHmReqExceProp.put(HHSR5Constants.DOCUMENTS_ID_LIST, aoDocumentsIdList);
		LOG_OBJECT.Info("Entered P8ContentService.getBRDcoumentPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loHmProps = contentOperationHelper.getBRDcoumentPropertiesById(loOS, aoHmRequiredProps, aoDocumentsIdList);

			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Properties" + loHmProps);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting BR document Propeties ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting BR document Propeties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting BR document Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting BR document Propeties : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting BR document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getBRDcoumentPropertiesById()");
		return loHmProps;
	}
	
	/**
	 * This method is used for removing document sharing with provider This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoUserSession a custom user bean having information about user
	 * @param asProviderId a string value having provider id
	 * @param aoShareDocuments a Hash Map with key as provider and value as
	 *            document id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean removeSharedDocumentsByProvider(P8UserSession aoUserSession, String asProviderId,
			String asSharingOrgId) throws ApplicationException
	{
		boolean lbFlagShared;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
		LOG_OBJECT.Info("Entered P8ContentService.removeSharedDocumentsByProvider() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			lbFlagShared = contentOperationHelper.removeSharedDocumentsByProvider(loOS, asProviderId, asSharingOrgId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document sharing with the provider" + lbFlagShared);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in removing document sharing with provider");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in removing document sharing with provider", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in removing document sharing with provider");
			ApplicationException loAppex = new ApplicationException(
					"Error in removing document sharing with provider : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in removing document sharing with provider", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.removeSharedDocumentsByProvider()");
		return lbFlagShared;
	}

	/**
	 * This is the method to check if the document is linked to application This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoPropertyMap a hash map containing properties as key
	 * @return boolean Variable
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean checkDocumentLinkedToApplication(P8UserSession aoUserSession, HashMap aoPropertyMap)
			throws ApplicationException
	{
		boolean lbLinkedToApp = false;
		String lsDocId = null;
		String lsDocType = null;
		String lsDocTitle = null;
		String lsProviderId = null;
		String lsOrgId = null;
		String lsDocCategory = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.checkDocumentLinkedToApplication() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// Fetching ObjectStore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

			lsDocType = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_TYPE);
			lsDocCategory = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY);
			lsDocTitle = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
			lsProviderId = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID);
			lsOrgId = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_ORGANIZATION_ID);
			lsDocId = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_ID);

			if (null == lsDocId)
			{
				loHmReqExceProp.put(P8Constants.DOC_TYPE, lsDocType);
				loHmReqExceProp.put(P8Constants.DOC_TITLE, lsDocTitle);
				loHmReqExceProp.put(P8Constants.LS_PROVIDER_ID, lsProviderId);

				if (lsDocType == null || lsDocTitle == null || lsProviderId == null || lsDocCategory == null)
				{
					ApplicationException loAppex = new ApplicationException(
							"Error in uploadDocument Method. Required fields are missing");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}

				// Getting document class for new document
				String lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, lsDocType, lsDocCategory);
				loHmReqExceProp.put(P8Constants.DOC_CLASS_NAME, lsDocClassName);
				// if document id is not present in property collection then
				// checking document existance
				lsDocId = checkDocumentExist(loXmlDoc, loOS, lsDocClassName, lsProviderId, lsDocTitle, lsDocType,
						lsDocCategory, lsOrgId);

			}
			if (lsDocId != null)
			{
				lbLinkedToApp = contentOperationHelper.checkDocumentLinkedToApplication(loOS, lsDocId);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Link to an application for a document" + lsDocId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Sharing documents with different providers");
			aoAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, "ERR_LINKED_TO_APP");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Sharing documents with different providers", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while checking link to application for a document");
			ApplicationException loAppex = new ApplicationException(
					"Error while checking link to application for a document : ", aoEx);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, "ERR_LINKED_TO_APP");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking link to application for a document", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkDocumentLinkedToApplication()");
		return lbLinkedToApp;
	}

	/**
	 * This is the method to get list of agency and provider with whom document
	 * is shared. This method is using FileNet session for performing actions in
	 * filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asAgencyType a string value representing agency type
	 * @param asProviderId a string value representing provider id
	 * @return TreeSet list of agencies with whom document is shared
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public TreeSet getSharedAgencyProviderList(P8UserSession aoUserSession, String asAgencyType, String asProviderId)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		TreeSet loSharedAgencyProviderSet = null;
		try
		{
			loHmReqExceProp.put(HHSR5Constants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSR5Constants.AGENCY_TYPE, asAgencyType);
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			LOG_OBJECT.Info("Entered P8ContentService.getSharedAgencyProviderList() with parameters::"
					+ loHmReqExceProp.toString());

			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loSharedAgencyProviderSet = contentOperationHelper.getSharedAgenciesProviderList(loOS, asAgencyType,
					asProviderId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Shared agency provider list" + loSharedAgencyProviderSet);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while getting shared agency and provider list");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while getting shared agency and provider list");
			ApplicationException loAppex = new ApplicationException(
					"Error while getting shared agency and provider list : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getSharedAgencyProviderList()");
		return loSharedAgencyProviderSet;
	}

	/**
	 * This is the method to get shared agency and provider list This method is
	 * using FileNet session for performing actions in filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asAgencyType a string value representing agency type
	 * @param asProviderId a string value representing provider id
	 * @return TreeSet list of agencies with whom document is shared
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public TreeSet getSharedDocumentsOwnerList(P8UserSession aoUserSession, String asAgencyType, String asProviderId)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		TreeSet loSharedAgencyProviderSet = null;
		try
		{
			loHmReqExceProp.put(HHSR5Constants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			LOG_OBJECT.Info("Entered P8ContentService.getSharedDocumentsOwnerList() with parameters::"
					+ loHmReqExceProp.toString());
						
            if(aoUserSession != null) {
            	// Start QC 8998 R 8.8  remove password from logs
            	String param = CommonUtil.maskPassword(aoUserSession);
            	        	
                LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]" + param); //aoUserSession.toString());
             // End QC 8998 R 8.8  remove password from logs
            }else{
                LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]loUserSession is NULL" );
            }
            LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]asAgencyType is "  + asAgencyType);
            LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]asProviderId is "  + asProviderId);



			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			
            if(loOS != null) {
                LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList] Object Store:\n" + loOS.toString());
                LOG_OBJECT.Debug("###################################################################");

            }else{
                LOG_OBJECT.Debug("##########################TRACE[getSharedDocumentsOwnerList]loOS is NULL" );
            }

            
			loSharedAgencyProviderSet = contentOperationHelper.getSharedDocumentsOwnerList(loOS, asAgencyType, asProviderId);
						
			filenetConnection.popSubject(aoUserSession);
			
			
			setMoState("Shared agency provider list" + loSharedAgencyProviderSet);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while getting shared agency and provider list");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while getting shared agency and provider list");
			ApplicationException loAppex = new ApplicationException(
					"Error while getting shared agency and provider list : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getSharedDocumentsOwnerList()");
		return loSharedAgencyProviderSet;
	}

	/**
	 * This method is used to get the count of the documents for a specific
	 * provider This method is using FileNet session for performing actions in
	 * filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asProviderId a string value representing provider id
	 * @return liTotalCount count of documents
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Integer getDocumentCountForProvider(P8UserSession aoUserSession, String asProviderId)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		int liTotalCount = 0;
		try
		{
			loHmReqExceProp.put(HHSR5Constants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			LOG_OBJECT.Info("Entered P8ContentService.getDocumentCountForProvider() with parameters::"
					+ loHmReqExceProp.toString());
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			liTotalCount = contentOperationHelper.getDocumentCountForProvider(aoUserSession, loOS, asProviderId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("getDocumentCountForProvider" + liTotalCount);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while getting shared agency and provider list");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while getting shared agency and provider list");
			ApplicationException loAppex = new ApplicationException(
					"Error while getting shared agency and provider list : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentCountForProvider()");
		return liTotalCount;
	}

	/**
	 * This method is used to get the content of the printer friendly documents
	 * This method is using FileNet session for performing actions in filenet
	 * using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asProviderId String value of provider id
	 * @param asOrgId String value of organization id
	 * @return InputStream input stream containing content of document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public InputStream getPrintDocumentContent(P8UserSession aoUserSession, String asPrintViewID, String asOrgId)
			throws ApplicationException
	{
		InputStream loContent = null;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSR5Constants.PRINT_VIEW_ID, asPrintViewID);
			loHmReqExceProp.put(P8Constants.XML_DOC_ORG_ID_PROPERTY, asOrgId);
			LOG_OBJECT.Info("Entered P8ContentService.getPrintDocumentContent() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loContent = contentOperationHelper.getPrintDocumentContent(loOS, asPrintViewID, asOrgId);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Content" + loContent);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getPrintDocumentContent()");
		return loContent;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault It will
	 * update the linkToApplication property of list of document id's in batch.
	 * @param P8UserSession aoUserSession object
	 * @param List<String> asDocIdList object
	 * @param HashMap<String, Boolean> aoUpdateProperties object
	 * @return boolean lbFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean updateLinkToApplicationThroughBatch(P8UserSession aoUserSession, List<String> asDocIdList,
			HashMap<String, Boolean> aoUpdateProperties) throws ApplicationException
	{
		boolean lbFlag = false;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmExcepRequiredProp.put(P8Constants.AS_DOC_ID, asDocIdList);
		loHmExcepRequiredProp.put(P8Constants.AO_HM, aoUpdateProperties);
		LOG_OBJECT.Info("Entered P8ContentService.updateLinkToApplicationThroughBatch() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{
			if (asDocIdList != null && !asDocIdList.isEmpty())
			{
				// Fetching FILENET object store from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				Domain loDomain = loOS.get_Domain();
				lbFlag = contentOperationHelper.setLinkToApplicationDocPropertyThoroughBatch(loDomain, loOS,
						asDocIdList, aoUpdateProperties);

			}
			else
			{
				lbFlag = true;
			}
			setMoState("Saving the document properties in file net" + lbFlag);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.updateLinkToApplicationThroughBatch()");

		return lbFlag;
	}

	/**
	 * This method is used to save the document properties in file net This
	 * method is using FileNet session for performing actions in filenet using
	 * FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a string value used to denote document Id.
	 * @param asDocType a string value used to denote document Type.
	 * @param aoHm a hash map containing properties as key
	 * @param abDocExists boolean value whether document exist for any other
	 *            type in database
	 * @return boolean value to confirm save successful
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public boolean saveDocumentProperties(P8UserSession aoUserSession, String asDocId, String asDocType, HashMap aoHm,
			Boolean abDocExists) throws ApplicationException
	{
		boolean lbFlag = false;
		if (!abDocExists)
		{
			lbFlag = saveDocumentProperties(aoUserSession, asDocId, asDocType, aoHm);
		}
		else
		{
			lbFlag = true;
		}
		return lbFlag;

	}

	/**
	 * This method is used upload the Financial document. This method is using
	 * FileNet session for performing actions in filenet using FileNet API
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoUserSession hold the user details who have logged into the
	 *            system
	 * 
	 * @param aoIS holds the content of Financial document.
	 * 
	 * @param aoPropertyMap is map that holds the properties of the document to
	 *            be uploaded
	 * 
	 * @param asFolderPath contains path on filenetDB where document will be
	 *            uploaded.
	 * 
	 * @return This method returns the document id of the document uploaded into
	 *         filenet DB
	 * 
	 * @@return This method returns the document id of the document uploaded
	 *          into filenet DB
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String financialPDFDocumentCreation(P8UserSession aoUserSession, FileInputStream aoIS,
			HashMap aoPropertyMap, String asFolderPath) throws ApplicationException
	{
		String lsDocId = null;
		String lsDocType = P8Constants.FINANCIAL_PDF_DOC;
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		HashMap loHmReqExceProp = new HashMap();
		String lsFileName = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.IS, aoIS);
			LOG_OBJECT.Info("Entered P8ContentService.financialPDFDocumentCreation() with parameters::"
					+ loHmReqExceProp.toString());

			// Fetching ObjectStore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			// Getting document class for new document
			lsDocId = contentOperationHelper.getDocumentId(loOS, lsDocType, asFolderPath, lsFileName, lsDocType);
			// If document exists before delete it before creating new document
			if (null != lsDocId && !lsDocId.isEmpty())
			{
				contentOperationHelper.deleteDocument(loOS, lsDocId);
			}
			// if document does not exist then creating new document
			loReturnMap = contentOperationHelper.createDocument(loOS, aoIS, lsDocType, lsDocType, aoPropertyMap,
					asFolderPath);
			lsDocId = (String) loReturnMap.get(P8Constants.DOCUMENT_IDS);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document has been Created" + lsDocId);

		}
		catch (ApplicationException loEx)
		{
			String lsMessage = loEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, P8Constants.MO4);
				throw new ApplicationException(lsMessage, loEx);
			}
			loEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.financialPDFDocumentCreation()::", loEx);
			throw loEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, P8Constants.MO4), aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Info("P8ContentService: document uploaded. method:financialPDFDocumentCreation. Time Taken(seconds):: "
						+ liTimediff);
		LOG_OBJECT.Info("Exiting P8ContentService.financialPDFDocumentCreation() ");
		return lsDocId;
	}

	/**
	 * This Method is used to fetch the Financial document id's on the basis of
	 * Document Title and Contract Id.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoUserSession P8UserSession - Object
	 * @param asFolderPath String - Object
	 * @param asDocumentTitle String - Object
	 * @param asContractID String - Object
	 * @return loListOfDocId List<String> - Object
	 * @throws ApplicationException
	 */

	public List<String> getFinancialDocumentId(P8UserSession aoUserSession, String asFolderPath,
			String asDocumentTitle, String asContractID) throws ApplicationException
	{
		List<String> loListOfDocId = null;
		LOG_OBJECT.Info("Entered P8ContentService.financialPDFDocumentCreation()");
		try
		{
			if (null != asContractID)
			{
				// Fetching ObjectStore from FILENET Domain
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				// Fetching DocumentId
				loListOfDocId = contentOperationHelper.getFinancialsDocumentId(loOS, P8Constants.FINANCIAL_PDF_DOC,
						asFolderPath, asDocumentTitle);
				setMoState("Document Exists in the filenet" + loListOfDocId);
			}
		}
		catch (ApplicationException loEx)
		{
			String lsMessage = loEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				throw new ApplicationException("Internal Error Occured While Processing Your Request", loEx);
			}
			LOG_OBJECT.Error("Exception in P8ContentOperations.getFinancialDocumentId()::", loEx);
			throw loEx;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While creating document");
			ApplicationException loAppex = new ApplicationException("Error Occured While Getting document id", aoEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getFinancialDocumentId()::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getFinancialDocumentId() ");
		return loListOfDocId;
	}

	/**
	 * This method is used to fetch the document properties for the document
	 * filtered by Document type and document category.
	 * <ul>
	 * <li>Get document type from parameter.</li>
	 * <li>Get document Category</li>
	 * <li>Get the document class for the document type and document category</li>
	 * </ul>
	 * @param aoUserSession P8User session bean object
	 * @param asDocType Document type of the document
	 * @param asDocCategory Category of the document
	 * @param aoHmReqProps Required parameter of the document
	 * @param aoFilterMap filter map to filter the document
	 * @param aoOrderByMap Order by map used to order the list of the documents
	 * @param abFilterIncluded boolean value whether to include filter or not
	 * @param asPagesize page size of the screen
	 * @return list of the document bean with all required properties.
	 * @throws ApplicationException when ever any exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getBRApplicationDocsProps(P8UserSession aoUserSession, String asDocType, String asDocCategory,
			HashMap aoHmReqProps, HashMap aoFilterMap, HashMap aoOrderByMap, Boolean abFilterIncluded, String asPagesize)
			throws ApplicationException
	{
		List loPropsList = null;
		String lsDocCategory = asDocCategory;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		LOG_OBJECT.Info("Entered P8ContentService.getBRApplicationDocsProps() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			loPropsList = getDocumentProperties(aoUserSession, asDocType, aoHmReqProps, aoFilterMap, aoOrderByMap,
					abFilterIncluded, asPagesize, lsDocCategory, loHmExcepRequiredProp);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in fetching Document Properties");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in fetching Document Properties");
			ApplicationException loAppex = new ApplicationException("Error in fetching Document Properties : ", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getBRApplicationDocsProps()");
		return loPropsList;
	}

	/**
	 * This method is used to fetch the document properties for the document
	 * filtered by Document type and document category.
	 * <ul>
	 * <li>Get document type from parameter.</li>
	 * <li>Get document Category</li>
	 * <li>Get the document class for the document type and document category</li>
	 * </ul>
	 * @param aoUserSession P8User session bean object
	 * @param asDocType Document type of the document
	 * @param asDocCategory Category of the document
	 * @param aoHmReqProps Required parameter of the document
	 * @param aoFilterMap filter map to filter the document
	 * @param aoOrderByMap Order by map used to order the list of the documents
	 * @param abFilterIncluded boolean value whether to include filter or not
	 * @param asPagesize page size of the screen
	 * @param aoHmExcepRequiredProp map to contain information to log error
	 * @return list of the document bean with all required properties.
	 * @throws ApplicationException when ever any exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private List getDocumentProperties(P8UserSession aoUserSession, String asDocType, HashMap aoHmReqProps,
			HashMap aoFilterMap, HashMap aoOrderByMap, Boolean abFilterIncluded, String asPagesize,
			String asDocCategory, HashMap aoHmExcepRequiredProp) throws ApplicationException
	{
		List loPropsList = null;
		String lsDocClassName = ApplicationConstants.EMPTY_STRING;
		try
		{
			org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			aoHmExcepRequiredProp.put(P8Constants.AS_DOC_TYPE, asDocType);
			aoHmExcepRequiredProp.put(P8Constants.HM_REQ_PROPS, aoHmReqProps);
			aoHmExcepRequiredProp.put(P8Constants.FILTER_MAP, aoFilterMap);
			aoHmExcepRequiredProp.put(P8Constants.ORDER_BY_MAP, aoOrderByMap);
			aoHmExcepRequiredProp.put(P8Constants.FILTER_INCLUDED, abFilterIncluded);
			LOG_OBJECT.Info("Entered P8ContentService.getDocumentProperties() with parameters::"
					+ aoHmExcepRequiredProp.toString());
			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

			if (null != asDocType)
			{
				// Fetching DocumentClass Name from DocType
				lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, asDocType, asDocCategory);
			}
			aoHmExcepRequiredProp.put(P8Constants.DOC_CLASS_NAME, lsDocClassName);

			loPropsList = contentOperationHelper.getDocsProperties(aoUserSession, loOS, lsDocClassName, aoHmReqProps,
					aoFilterMap, aoOrderByMap, abFilterIncluded, asPagesize);

			filenetConnection.popSubject(aoUserSession);
			setMoState(P8Constants.DOCUMENT_PROPERTIES + loPropsList);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in fetching Document Properties");
			aoAppex.setContextData(aoHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in fetching Document Properties");
			ApplicationException loAppex = new ApplicationException("Error in fetching Document Properties : ", aoEx);
			loAppex.setContextData(aoHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in fetching Document Properties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentProperties()");
		return loPropsList;
	}

	// Added for Release 5
	/**
	 * This method will check lockStatus of a folder id/Document Id from Cache
	 * @param lsFolderId
	 * @return boolean value based on value present in cache or not
	 */
	public Boolean checkLock(String lsFolderId)
	{
		return false;
	}

	// Added below methods for Release 5
	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will give the folder properties from Filenet
	 * @param aoUserSession
	 * @param aoHmRequiredProps
	 * @param aoDocumentsIdList
	 * @return loHmProps
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap getFolderPropertiesById(P8UserSession aoUserSession, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{
		HashMap loHmProps = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSR5Constants.HM_REQUIRED_PROPS, aoHmRequiredProps);
		loHmReqExceProp.put(HHSR5Constants.DOCUMENTS_ID_LIST, aoDocumentsIdList);
		LOG_OBJECT.Info("Entered P8ContentService.getFolderPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{

			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			HashMap<String, String> loPropMap = new HashMap<String, String>();
			loPropMap.put(HHSR5Constants.PATH_NAME, HHSConstants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.TEMPLATE_IDEN, HHSConstants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.FILENET_FOLDER_NAME, HHSConstants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSConstants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.DELETED_DATE, HHSConstants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.FILENET_DELETED_BY, HHSConstants.EMPTY_STRING);
			// added for 7823
			loPropMap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, HHSConstants.EMPTY_STRING);
			PropertyFilter loProp = contentOperationHelper.createPropertyFilter(loPropMap);
			loHmProps = contentOperationHelper.getBRFolderPropertiesById(loOS, (String) aoDocumentsIdList.get(0),
					loProp);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Properties" + loHmProps);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting BR document Propeties ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Folder Properties By Id", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting BR document Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Folder Properties By Id : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Folder Properties By Id", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getFolderPropertiesById()");
		return loHmProps;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method is saving Folder properties in filenet
	 * @param HashMap hmReqProps
	 * @param P8UserSession aoFilenetSession
	 * @return boolean lbFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public boolean saveFolderProperties(P8UserSession aoFilenetSession, HashMap hmReqProps) throws ApplicationException
	{
		boolean lbFlag = false;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.OBJECT_STORE_NAME, aoFilenetSession.getObjectStoreName());
		loHmExcepRequiredProp.put(P8Constants.AO_HM, hmReqProps);
		LOG_OBJECT.Info("Entered P8ContentService.saveFolderProperties() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{

			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoFilenetSession);

			lbFlag = contentOperationHelper.setFolderProperties(loOS, hmReqProps);
			filenetConnection.popSubject(aoFilenetSession);

			setMoState("Saving the folder properties in file net" + lbFlag);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in saving folder properties in file net");
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in folder document properties in file net", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in saving folder properties in file net");
			ApplicationException loAppex = new ApplicationException(
					"Error in saving document properties in file net: ", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error in folder document properties in file net", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.saveFolderProperties()");
		return lbFlag;
	}

	/**
	 * This method will move documents/Folders from one location to another
	 * @param aomoveDocumentList
	 * @param aoUserSession
	 * @param asNewFolderPath
	 * @param asMoveFrom
	 * @return lbFlag
	 */
	public HashMap<String, HashMap<String, Object>> moveDocumentFolders(P8UserSession aoUserSession,
			Boolean abServiceFlag, List<Document> aoListItems, String asMoveTo, String asMoveFrom,
			HashMap<String, HashMap<String, Object>> loDbMap, List<Document> loFinalObjectList,
			List<String> loFinalDeleteList) throws ApplicationException
	{
		List<Document> loDocumentIdList = new ArrayList<Document>();
		List<Document> loFolderIdList = new ArrayList<Document>();
		Map<String, Object> loFolderMapDestination = new HashMap<String, Object>();
		Map<String, Object> loMoveToMap = new HashMap<String, Object>();
		Map<String, Object> loFolderMapSource = new HashMap<String, Object>();
		List<Document> loDocument = new ArrayList<Document>();
		Map<String, String> loCheckedFolderPaths = new HashMap<String, String>();
		LOG_OBJECT.Info("Entered P8ContentService.move()");
		try
		{
			if (abServiceFlag)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				HashMap<String, String> loPropMap = new HashMap<String, String>();
				loPropMap.put(HHSR5Constants.PATH_NAME, HHSConstants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.TEMPLATE_IDEN, HHSConstants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.FILENET_FOLDER_NAME, HHSConstants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSConstants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.DELETED_DATE, HHSConstants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.FILENET_DELETED_BY, HHSConstants.EMPTY_STRING);
				PropertyFilter loProp = contentOperationHelper.createPropertyFilter(loPropMap);
				loFolderMapDestination = contentOperationHelper.getBRFolderPropertiesById(loOS, asMoveTo, loProp);

				Iterator<Document> iterator = aoListItems.iterator();
				while (iterator.hasNext())
				{
					Document loDoc = (Document) iterator.next();

					if (null != loDoc && StringUtils.isNotBlank(loDoc.getDocType())
							&& !loDoc.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
					{

						loDocumentIdList.add(loDoc);
						loDoc.setMoveToPath(asMoveTo);
						loDoc.setMoveFromPath(asMoveFrom);
						loFinalObjectList.add(loDoc);

					}
					else
					{
						loFolderMapSource = contentOperationHelper.getBRFolderPropertiesById(loOS,
								loDoc.getDocumentId(), loProp);
						loFolderIdList.add(loDoc);
						String lsMoveTo = loFolderMapDestination.get(HHSR5Constants.PATH_NAME)
								+ HHSR5Constants.FORWARD_SLASH
								+ loFolderMapSource.get(HHSR5Constants.FILENET_FOLDER_NAME);
						boolean IsFolderExist = checkFolderExist(aoUserSession, lsMoveTo);
						if (IsFolderExist || loCheckedFolderPaths.containsKey(lsMoveTo))
						{	//null passed as parameter in modified method 4.0.2.0
							loDocument = contentOperationHelper.getChildList(loOS, loDoc.getDocumentId(),
									HHSR5Constants.INFOLDER, loFolderIdList, null);
							//null passed as parameter in modified method 4.0.2.0
							if (IsFolderExist)
							{
								loMoveToMap = contentOperationHelper.getBRFolderPropertiesById(loOS, lsMoveTo, loProp);
							}
							else
							{
								loMoveToMap = contentOperationHelper.getBRFolderPropertiesById(loOS,
										loCheckedFolderPaths.get(lsMoveTo), loProp);
							}
							loFinalDeleteList.add(loDoc.getDocumentId());
							if (null != loDocument && !loDocument.isEmpty())
							{
								moveDocumentFolders(aoUserSession, abServiceFlag, loDocument,
										loMoveToMap.get(HHSR5Constants.TEMPLATE_IDEN).toString(),
										loDoc.getDocumentId(), loDbMap, loFinalObjectList, loFinalDeleteList);
							}
						}
						else
						{
							loDoc.setMoveToPath(asMoveTo);
							loFinalObjectList.add(loDoc);
							loCheckedFolderPaths.put(lsMoveTo, loDoc.getDocumentId());
						}

					}
					if (null != loDocumentIdList && !loDocumentIdList.isEmpty() && loDocumentIdList.size() > 0)
					{
						loDbMap.put(HHSR5Constants.DOC_COUNT_DECREASE,
								generateHashMap(asMoveFrom, Integer.toString(loDocumentIdList.size())));
						loDbMap.put(HHSR5Constants.DOC_COUNT_INCREASE,
								generateHashMap(asMoveTo, Integer.toString(loDocumentIdList.size())));
					}
					if (null != loFolderIdList && !loFolderIdList.isEmpty() && loFolderIdList.size() > 0)
					{
						loDbMap.put(HHSR5Constants.FOLDER_COUNT_DECREASE,
								generateHashMap(asMoveFrom, Integer.toString(loFolderIdList.size())));
						loDbMap.put(HHSR5Constants.FOLDER_COUNT_INCREASE,
								generateHashMap(asMoveTo, Integer.toString(loFolderIdList.size())));
					}
					loDbMap.put(loDoc.getDocumentId(), generateHashMap(HHSR5Constants.FOLDER_PARENT_CHANGE, asMoveTo));
				}

				// Added for defect 7409
				contentOperationHelper.setParentModifiedDateProperty(loOS, asMoveFrom);
				contentOperationHelper.setParentModifiedDateProperty(loOS, asMoveTo);
				// Added for defect 7409
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.move()");
		return loDbMap;
	}

	/**
	 * This method will check folder linkage to application
	 * @param aoUserSession
	 * @param aoPropertyMap
	 * @return linking Flag
	 * @throws ApplicationException
	 */
	public boolean checkFolderLinkedToApplication(P8UserSession aoUserSession, HashMap aoPropertyMap)
			throws ApplicationException
	{
		boolean lbLinkedToApp = false;

		return lbLinkedToApp;
	}

	/**
	 * This method will move objects into RecycleBin
	 * @param aoUserSession
	 * @param asMoveTo
	 * @param asMoveFrom
	 * @return boolean flag for delete success
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<String>> delete(P8UserSession aoUserSession, HashMap aoDataMap, HashMap aoReqMap)
			throws ApplicationException
	{
		List<Document> loDocList = null, loFolderList = null;
		List<String> loFolderRemovalFromDBList = null, loDocCountDecreaseFromDBList = null, loTemp = new ArrayList<String>();
		HashMap<String, Object> loMap = new HashMap<String, Object>();
		HashMap<String, List<String>> loEntityMap = new HashMap<String, List<String>>();
		List<String> loRetainedFolderList = new ArrayList<String>();
		List<String> lsMessageList = new ArrayList<String>();
		HashMap<String, String> lsMessageMap = new HashMap<String, String>();
		HashMap<String, String> lsMessageDocumentMap = new HashMap<String, String>();
		HashMap<String, String> lsMessageFolderMap = new HashMap<String, String>();
		LOG_OBJECT.Info("Entered P8ContentService.delete()");
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			aoReqMap.put(HHSR5Constants.FOLDERS_FILED_IN, HHSConstants.EMPTY_STRING);
			if (null != aoDataMap && !aoDataMap.isEmpty())
			{
				loDocList = (List<Document>) aoDataMap.get(HHSR5Constants.DOC_LIST);
				loFolderList = (List<Document>) aoDataMap.get(HHSR5Constants.FOLDER_LIST);
				List<String> loTempList = new ArrayList<String>();
				for (Iterator iterator = loDocList.iterator(); iterator.hasNext();)
				{
					Document loDocument = (Document) iterator.next();
					if (loDocument.getDeleteFlag() == 0)
					{
						createFolderListForDeletion(loRetainedFolderList, loDocument.getParent());
					}
					else
					{
						// added for 7409
						if (loDocument.getDeleteFlag() == 2)
						{
							String lsFolderPath = loDocument.getParent().get_PathName();
							String[] loFoldersSplitPath = lsFolderPath.split(P8Constants.STRING_SINGLE_SLASH);
							if (!loFoldersSplitPath[loFoldersSplitPath.length - 1]
									.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT))
							{
								if (!loTempList.contains(lsFolderPath))
								{
									loTempList.add(lsFolderPath);
									contentOperationHelper.setParentModifiedDateProperty(loOS, lsFolderPath);
								}
							}
						}
						// added for 7409
					}
				}
				for (Iterator iterator = loFolderList.iterator(); iterator.hasNext();)
				{
					Document loFolder = (Document) iterator.next();
					if (loRetainedFolderList.contains(loFolder.getDocumentId()))
					{
						loFolder.setDeleteFlag(0);
					}
					// added for 7409
					if (null != loFolder.getDocumentId())
					{
						HashMap<String, String> loPropertyMap = new HashMap<String, String>();
						loPropertyMap.put(HHSR5Constants.PATH_NAME, ApplicationConstants.EMPTY_STRING);
						Folder loParentFolder = contentOperationHelper.getFolderObjectById(loOS,
								loFolder.getDocumentId(), loPropertyMap);
						Properties loParentFolderProp = loParentFolder.getProperties();
						String lsParentFolderPath = loParentFolderProp.getStringValue(HHSR5Constants.PATH_NAME);
						String lsConcatPath = lsParentFolderPath.substring(0, lsParentFolderPath.lastIndexOf("/"));
						contentOperationHelper.setParentModifiedDateProperty(loOS, lsConcatPath);
					}
					// added for 7409
				}
				if (null != loDocList && !loDocList.isEmpty())
				{
					loMap = contentOperationHelper.softDeleteFromFilenet(loOS, loDocList, aoReqMap,
							HHSR5Constants.DOCUMENT, loEntityMap);
					loDocCountDecreaseFromDBList = (List) loMap.get(HHSR5Constants.ENTITY_ID_FOR_DELETION);
					lsMessageDocumentMap = (HashMap<String, String>) loMap.get(HHSR5Constants.LS_MESSAGE);
					loEntityMap.put(HHSR5Constants.DOC_COUNT_DECREASE_FROM_DB_LIST, loDocCountDecreaseFromDBList);
				}
				if (null != loFolderList && !loFolderList.isEmpty())
				{
					loMap = contentOperationHelper.softDeleteFromFilenet(loOS, loFolderList, aoReqMap,
							HHSR5Constants.FOLDER, loEntityMap);
					loFolderRemovalFromDBList = (List) loMap.get(HHSR5Constants.ENTITY_ID_FOR_DELETION);
					lsMessageFolderMap = (HashMap<String, String>) loMap.get(HHSR5Constants.LS_MESSAGE);
					loEntityMap.put(HHSR5Constants.FOLDER_REMOVAL_FROM_DB_LIST, loFolderRemovalFromDBList);
				}
				if (null != lsMessageFolderMap
						&& !lsMessageFolderMap.isEmpty()
						&& lsMessageFolderMap.get(HHSConstants.TYPE).toString()
								.equalsIgnoreCase(ApplicationConstants.MESSAGE_PASS_TYPE)
						&& null != lsMessageDocumentMap
						&& !lsMessageDocumentMap.isEmpty()
						&& lsMessageDocumentMap.get(HHSConstants.TYPE).toString()
								.equalsIgnoreCase(ApplicationConstants.MESSAGE_FAIL_TYPE))
				{
					String lsPropertyPath = P8Constants.ERROR_PROPERTY_FILE;
					lsMessageMap.put(HHSConstants.CBL_MESSAGE,
							PropertyLoader.getProperty(lsPropertyPath, HHSR5Constants.PARTIALLY_DELETED));
					lsMessageMap.put(HHSConstants.TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
				}
				else if (null != lsMessageDocumentMap && !lsMessageDocumentMap.isEmpty())
				{
					lsMessageMap.put(HHSConstants.CBL_MESSAGE, lsMessageDocumentMap.get(HHSConstants.CBL_MESSAGE));
					lsMessageMap.put(HHSConstants.TYPE, lsMessageDocumentMap.get(HHSConstants.TYPE));
				}
				else if (null != lsMessageFolderMap && !lsMessageFolderMap.isEmpty())
				{
					lsMessageMap.put(HHSConstants.CBL_MESSAGE, lsMessageFolderMap.get(HHSConstants.CBL_MESSAGE));
					lsMessageMap.put(HHSConstants.TYPE, lsMessageFolderMap.get(HHSConstants.TYPE));
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at deletion", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at deletion", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.delete()");
		lsMessageList.add(lsMessageMap.get(HHSConstants.CBL_MESSAGE));
		lsMessageList.add(lsMessageMap.get(HHSConstants.TYPE));
		loEntityMap.put(HHSR5Constants.LS_MESSAGE_LIST, lsMessageList);
		return loEntityMap;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will create Document Vault folder in Filenet
	 * @param aoUserSession
	 * @param asUserOrgType
	 * @param asUserOrg
	 * @return lsFolderBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public FolderMappingBean createDocumentVaultFolder(P8UserSession aoUserSession, String asUserOrgType,
			String asUserOrg) throws ApplicationException
	{
		String lsFolderPath = null;
		FolderMappingBean lsFolderBean = null;
		LOG_OBJECT.Info("Entered P8ContentService.createDocumentVaultFolder()");
		try
		{
			if (null != asUserOrgType)
			{
				if (asUserOrgType.equalsIgnoreCase(P8Constants.APPLICATION_CITY_ORG))
				{
					lsFolderPath = (P8Constants.STRING_SINGLE_SLASH).concat(ApplicationConstants.CITY)
							.concat(P8Constants.STRING_SINGLE_SLASH).concat(HHSR5Constants.DOCUMENT_VAULT);
				}
				else if (asUserOrgType.equalsIgnoreCase(P8Constants.APPLICATION_PROVIDER_ORG))
				{
					lsFolderPath = (P8Constants.STRING_SINGLE_SLASH).concat(HHSR5Constants.PROVIDER).concat(asUserOrg)
							.concat(P8Constants.STRING_SINGLE_SLASH).concat(HHSR5Constants.DOCUMENT_VAULT);
				}
				else if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					lsFolderPath = (P8Constants.STRING_SINGLE_SLASH).concat(HHSR5Constants.AGENCY).concat(asUserOrg)
							.concat(P8Constants.STRING_SINGLE_SLASH).concat(HHSR5Constants.DOCUMENT_VAULT);
				}
			}
			lsFolderBean = contentOperationHelper.checkFolderByName(aoUserSession, lsFolderPath, asUserOrg,
					HHSR5Constants.HHS_CUSTOM_FOLDER);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.createDocumentVaultFolder()");
		return lsFolderBean;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will get Folder Properties
	 * @param aoUserSession
	 * @param aoReqProps
	 * @param aoDocIdList
	 * @param loFolderInfo
	 * @return loMap
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public HashMap getFolderProperties(P8UserSession aoUserSession, HashMap aoReqProps, List aoDocIdList,
			FolderMappingBean loFolderInfo) throws ApplicationException
	{
		HashMap<String, HashMap<String, String>> loMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loInnerMap = new HashMap<String, String>();
		LOG_OBJECT.Info("Entered P8ContentService.getFolderProperties()");
		try
		{
			if (null != loFolderInfo)
			{
				generateMapFromBean(loInnerMap, loFolderInfo);
			}
			HashMap lofolderProp = getFolderPropertiesById(aoUserSession, aoReqProps, aoDocIdList);
			loInnerMap.put(HHSR5Constants.FOLDER_PATH, (String) lofolderProp.get(HHSR5Constants.PATH_NAME));
			loInnerMap.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, DateUtil
					.getDateMMDDYYYYZZFormat((Date) lofolderProp.get(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE)));
			loInnerMap.put(HHSR5Constants.FILENET_MOVE_FROM,
					(String) lofolderProp.get(HHSR5Constants.FILENET_MOVE_FROM));
			loInnerMap.put(HHSR5Constants.DELETED_DATE,
					DateUtil.getDateMMDDYYYYZZFormat((Date) lofolderProp.get(HHSR5Constants.DELETED_DATE)));
			loInnerMap.put(HHSR5Constants.FILENET_DELETED_BY,
					(String) lofolderProp.get(HHSR5Constants.FILENET_DELETED_BY));
			// Added to get Share With list - Fix for Defect # 7493
			if (null != lofolderProp.get(HHSR5Constants.SHARE_LIST)
					&& !lofolderProp.get(HHSR5Constants.SHARE_LIST).toString().isEmpty())
			{
				loInnerMap.put(HHSR5Constants.SHARE_LIST, (String) lofolderProp.get(HHSR5Constants.SHARE_LIST));
			}
			if (null != aoReqProps.get(HHSConstants.USER_ORG_ID)
					&& !aoReqProps.get(HHSConstants.USER_ORG_ID).toString().isEmpty())
			{
				loInnerMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, (String) aoReqProps.get(HHSConstants.USER_ORG_ID));
			}
			// Fixed for Defect # 7493 end
			loMap.put((String) aoDocIdList.get(0), loInnerMap);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in getFolderProperties in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getFolderProperties()");
		return loMap;
	}

	// End Release 5
	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will generate Map From Bean
	 * @param aoMap
	 * @param aoBean
	 * @return lbfolderFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	private void generateMapFromBean(HashMap aoMap, FolderMappingBean aoBean)
	{
		if (null != aoBean)
		{
			aoMap.put(HHSR5Constants.FOLDER_NAME, aoBean.getFolderName());
			aoMap.put(HHSR5Constants.FOLDER_COUNT, aoBean.getFolderCount());
			aoMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, aoBean.getModifiedBy());
			aoMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, aoBean.getCreatedBy());
			aoMap.put(P8Constants.PROPERTY_CE_DATE_CREATED, aoBean.getCreatedDate());
			aoMap.put(P8Constants.PROPERTY_CE_DATE_CREATED, aoBean.getCreatedDate());
			aoMap.put(HHSR5Constants.FOLDER_COUNT, aoBean.getDocumentCount());
		}
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will check if Folder already Exist
	 * @param aoUserSession
	 * @param asPath
	 * @return lbfolderFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public boolean checkFolderExist(P8UserSession aoUserSession, String asPath) throws ApplicationException
	{
		boolean lbfolderFlag = false;
		LOG_OBJECT.Info("Entered P8ContentService.checkFolderExist()");
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			if (null != asPath && !asPath.isEmpty())
			{
				lbfolderFlag = contentOperationHelper.checkFolderExists(loOS, asPath);
			}

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  at creating Document Vault folder for new user", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkFolderExist()");
		return lbfolderFlag;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will check Entity Count
	 * @param aoUserSession
	 * @param aoEntityList
	 * @return lbEntityFlag boolean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public boolean checkEntityCount(P8UserSession aoUserSession, List<Document> aoEntityList)
			throws ApplicationException
	{
		int liEntityCount = 0;
		boolean lbEntityFlag = true;
		LOG_OBJECT.Info("Entered P8ContentService.checkEntityCount()");
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			int liCount;
			for (Iterator<Document> loIterator = aoEntityList.iterator(); loIterator.hasNext();)
			{
				Document loDocument = (Document) loIterator.next();
				liEntityCount++;
				if (null == loDocument || null == loDocument.getDocType() || loDocument.getDocType().isEmpty()
						|| loDocument.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{
					liCount = contentOperationHelper.getEntityCountInParent(loOS, loDocument.getDocumentId());
					liEntityCount += liCount;
				}
			}
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			if (liEntityCount > Integer.parseInt(loApplicationSettingMap.get(HHSR5Constants.DOC_VAULT_MAX_BULK_SIZE)))
			{
				lbEntityFlag = false;
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  in checking Count", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  in checking Count", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkEntityCount()");
		return lbEntityFlag;
	}

	/**
	 * This method will generate HashMap based on passed Key and Value
	 * @param asKey
	 * @param asValue
	 * @return hashMap
	 */
	public HashMap<String, Object> generateHashMap(String asKey, Object asValue)
	{
		HashMap<String, Object> aoMap = new HashMap<String, Object>();
		aoMap.put(asKey, asValue);
		return aoMap;
	}

	/**
	 * This method will remove sharing objects and calling removeSharedDocuments
	 * method
	 * @param aoUserSession
	 * @param aoDbMap
	 * @return sharedItemsdeletedflag
	 * @throws ApplicationException
	 */
	public boolean removeSharing(P8UserSession aoUserSession, HashMap<String, HashMap<String, Object>> aoDbMap,
			HashMap<String, String> aoMapOrg) throws ApplicationException
	{
		boolean lbSharedDeleted = false;
		LOG_OBJECT.Info("Entered P8ContentService.removeSharing()");
		try
		{
			if (null != aoDbMap && !aoDbMap.isEmpty() && aoDbMap.containsKey(HHSR5Constants.REMOVE_SHARING_LIST))
			{
				HashMap<String, String> loSharedIdMap = new HashMap<String, String>();
				HashMap loTempMap = aoDbMap.get(HHSR5Constants.REMOVE_SHARING_LIST);
				List<Document> loTempList = (List<Document>) loTempMap.get(HHSR5Constants.REMOVE_SHARING_LIST);
				for (Iterator iterator = loTempList.iterator(); iterator.hasNext();)
				{
					Document document = (Document) iterator.next();
					loSharedIdMap.put(document.getDocumentId(), document.getDocType());
				}

				// added 1 paramter for parent property in unsharing
				//null passed as parameter in modified method 4.0.2.0
				lbSharedDeleted = removeSharedDocuments(aoUserSession, loSharedIdMap, aoMapOrg, null);
				//null passed as parameter in modified method 4.0.2.0
			}	
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  in removeSharing", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured  in removeSharing", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.removeSharing()");
		return lbSharedDeleted;
	}

	/**
	 * This method is added to check linkage of documents in filenet
	 * @param aoUserSession filenet session
	 * @param abEntityFlag Boolean flag
	 * @param aoRequiredMap HashMap
	 * @param aoDeleteItems List
	 * @return loDataMap
	 * @throws ApplicationException
	 */
	public HashMap<String, List<Document>> checkLinkageinFilenet(P8UserSession aoUserSession, Boolean abEntityFlag,
			HashMap<String, String> aoRequiredMap, List<Document> aoDeleteItems) throws ApplicationException
	{

		HashMap<String, List<Document>> loDataMap = new HashMap<String, List<Document>>();
		String lsPropertyPath = P8Constants.ERROR_PROPERTY_FILE;
		String lsMessgetoDisplay = null;
		boolean lbTempFlag = false;
		LOG_OBJECT.Info("Entered P8ContentService.checkLinkageinFilenet()");
		if (abEntityFlag)
		{

			boolean lbFlagTemp = false;
			List<Document> loDocList = new ArrayList<Document>();
			List<Document> loFolderList = new ArrayList<Document>();
			try
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				aoDeleteItems = contentOperationHelper.checkParentChildRelationShip(loOS, aoDeleteItems, "delete");
				if (null != aoDeleteItems && !aoDeleteItems.isEmpty())
				{
					// Iterating Over each checked Item
					for (Iterator iterator = aoDeleteItems.iterator(); iterator.hasNext();)
					{
						Document loDocument = (Document) iterator.next();
						if (null != loDocument.getDocumentId() && !loDocument.getDocumentId().isEmpty())
						{
							if (StringUtils.isNotBlank(loDocument.getDocType())
									&& !loDocument.getDocType().equalsIgnoreCase(HHSR5Constants.NULL)
									&& StringUtils.isNotBlank(loDocument.getDocumentId()))
							{ // Checking documents linkage of checked documents
								// only

								contentOperationHelper.getDocumentsLinkFoldersFiledInProp(loOS, loDocument);
								boolean lbFlag = loDocument.isLinkToApplication();
								if (lbFlag)
								{
									lbTempFlag = true;
									loDocument.setDeleteFlag(0);
									loDocList.add(loDocument);
								}
								else
								{
									lbFlagTemp = true;
									loDocument.setDeleteFlag(2);
									loDocument.setShowInRB(true);
									loDocument.setDeletionEntityId(loDocument.getDocumentId());
									loDocList.add(loDocument);
								}
							}
							else
							{
								lbFlagTemp = true;
								// Fetching complete child List containing
								// documents and folders
								List<Document> loFolderChildList = new ArrayList<Document>();
								List<Document> loLinkedDocList = new ArrayList<Document>();
								//null passed as parameter in modified method 4.0.2.0
								loFolderChildList = contentOperationHelper.getChildList(loOS,
										loDocument.getDocumentId(), HHSR5Constants.INSUBFOLDER, null, null);
								//null passed as parameter in modified method 4.0.2.0
								// Iterating over children list
								if (null == loFolderChildList || loFolderChildList.isEmpty())
								{
									loDocument.setDeletionEntityId(loDocument.getDocumentId());
									loDocument.setDeleteFlag(2);
									loDocument.setShowInRB(true);
									loFolderList.add(loDocument);
									lsMessgetoDisplay = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
											HHSR5Constants.SUCCESS_DELETED);
								}
								else
								{
									for (Iterator iterator2 = loFolderChildList.iterator(); iterator2.hasNext();)
									{
										Document loDocumentBean = (Document) iterator2.next();
										//New Filling Check after 4.0.2, to make delete flag 0 or 1
										loDocumentBean.setDeletedViaParent(true);
										if (StringUtils.isNotBlank(loDocumentBean.getDocType())
												&& !loDocumentBean.getDocType().equalsIgnoreCase(HHSR5Constants.NULL)
												&& StringUtils.isNotBlank(loDocumentBean.getDocumentId()))
										{
											lbTempFlag = checkLinkageinFilenetForDocumentChild(lbTempFlag, loDocList,
													loDocument, loLinkedDocList, loDocumentBean);
										}
										else
										{
											lbTempFlag = checkLinkageinFilenetForDocumentChild(lbTempFlag,
													loFolderList, loDocument, loLinkedDocList, loDocumentBean);

										}
									}
									if (loLinkedDocList.isEmpty())
									{
										loDocument.setDeletionEntityId(loDocument.getDocumentId());
										loDocument.setDeleteFlag(2);
										loDocument.setShowInRB(true);
										loFolderList.add(loDocument);
									}
									else
									{
										loDocument.setDeletionEntityId(loDocument.getDocumentId());
										loDocument.setDeleteFlag(0);
										if (loLinkedDocList.size() == loFolderChildList.size())
										{
											loDocument.setShowInRB(false);
										}
										else
										{
											loDocument.setShowInRB(true);
										}
										loFolderList.add(loDocument);
									}
								}
							}
						}
					}
					// add elements to al, including duplicates

					loDocList = FileNetOperationsUtils.getUniqueListVal(loDocList);
					loFolderList = FileNetOperationsUtils.getUniqueListVal(loFolderList);
					loDataMap.put(HHSR5Constants.DOC_LIST, loDocList);
					loDataMap.put(HHSR5Constants.FOLDER_LIST, loFolderList);
				}
			}
			catch (ApplicationException loAppEx)
			{
				LOG_OBJECT.Error("Exception occured  in checkLinkageInFilenet", loAppEx);
				throw loAppEx;
			}
			catch (Exception loAppEx)
			{
				LOG_OBJECT.Error("Exception occured  in checkLinkageInFilenet", loAppEx);
				throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
			}
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkLinkageinFilenet()");
		return loDataMap;
	}

	/**
	 * @param abTempFlag
	 * @param aoDocList
	 * @param aoDocument
	 * @param aoLinkedDocList
	 * @param aoChildFolderList
	 * @param aoDocumentBean
	 * @return lbReturnFlag
	 * @throws ApplicationException
	 */
	private boolean checkLinkageinFilenetForDocumentChild(boolean abTempFlag, List<Document> aoDocList,
			Document aoDocument, List<Document> aoLinkedDocList, Document aoDocumentBean) throws ApplicationException
	{
		boolean lbReturnFlag = abTempFlag;
		boolean lbFlag = aoDocumentBean.isLinkToApplication();
		LOG_OBJECT.Info("Entered P8ContentService.checkLinkageinFilenetForDocumentChild()");
		if (lbFlag)
		{
			aoDocumentBean.setDeleteFlag(0);
			aoLinkedDocList.add(aoDocumentBean);
			aoDocList.add(aoDocumentBean);
			lbReturnFlag = true;
		}
		else
		{
			aoDocumentBean.setDeleteFlag(1);
			aoDocumentBean.setDeletionEntityId(aoDocument.getDocumentId());
			aoDocList.add(aoDocumentBean);

		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkLinkageinFilenetForDocumentChild()");
		return lbReturnFlag;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will restore Document or folder from recycle bin
	 * @param aoUserSession
	 * @param aoRequiredMap
	 * @param aoRestoreList
	 * @return loDBMap
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap<String, Object> restore(P8UserSession aoUserSession, Boolean lbServiceFlag,
			HashMap<String, String> aoRequiredMap, List<Document> aoRestoreList) throws ApplicationException

	{
		Integer liDocUpdateCount = 0;
		Integer liDocUpdateCountTemp = 0;
		HashMap<String, Object> loDbMap = new HashMap<String, Object>();
		HashMap<String, Object> loPropMap = new HashMap<String, Object>();
		HashMap<String, Object> loDocCountMap = new HashMap<String, Object>();
		HashMap<String, Integer> loDocCountIntermediateMap = new HashMap<String, Integer>();
		loDocCountMap.put(HHSR5Constants.UPDATE_DOC_COUNT, loDocCountIntermediateMap);
		List<Document> loDeleteList = new ArrayList<Document>();
		List<Document> loDBList = new ArrayList<Document>();
		List<FolderMappingBean> loMappingBeanList = new ArrayList<FolderMappingBean>();
		LOG_OBJECT.Info("Entered P8ContentService.restore()");
		List<String> loDeletedFolders = new ArrayList<String>();
		try
		{
			if (lbServiceFlag)
			{
				loPropMap.put(HHSR5Constants.ORGANIZATION_ID_KEY, aoRequiredMap.get(HHSR5Constants.ORGANIZATION_TYPE));
				// Adding a new key for Emergency Release 4.0.1
				loPropMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, aoRequiredMap.get(HHSR5Constants.ORGANIZATION_ID));
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				aoRestoreList = contentOperationHelper.checkParentChildRelationShip(loOS, aoRestoreList, "restore");
				org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
				DateFormat loDateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
				Date loDate = new Date();
				Map<String, Integer> loNameExistMap = new HashMap<String, Integer>();
				List<Document> loTempRestoreList = new ArrayList<Document>();
				List<String> loRestoredFolderPath = new ArrayList<String>();
				for (Iterator iterator = aoRestoreList.iterator(); iterator.hasNext();)
				{
					Document loDoc = (Document) iterator.next();
					/*
					 * if (!loTempRestoreList.contains(loDoc)) {
					 */
					if (null == loDoc.getDocType() || loDoc.getDocType().isEmpty()
							|| loDoc.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
					{
						HashMap<String, String> loMap = new HashMap<String, String>();
						loMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSR5Constants.EMPTY_STRING);
						loMap.put(HHSR5Constants.PATH_NAME, HHSR5Constants.EMPTY_STRING);
						loMap.put(HHSR5Constants.FOLDER_NAME, HHSR5Constants.EMPTY_STRING);
						loMap.put(HHSR5Constants.ORIGINAL_FOLDER_NAME, HHSR5Constants.EMPTY_STRING);
						loMap.put(HHSR5Constants.REPLICATION_ID, HHSR5Constants.EMPTY_STRING);
						PropertyFilter loPf = contentOperationHelper.createPropertyFilter(loMap);
						Folder loFolder = Factory.Folder.fetchInstance(loOS, new Id(loDoc.getDocumentId()), loPf);
						Properties loProp = loFolder.getProperties();
						Document loTemp = new Document();
						String lsReplicationId = loProp.getStringValue(HHSR5Constants.REPLICATION_ID);
						if (null != lsReplicationId && !lsReplicationId.isEmpty())
						{
							loTemp.setReplicationId(lsReplicationId);
						}
						loTemp.setDocumentId(loDoc.getDocumentId());
						loTemp.setFolderName(loProp.getStringValue(HHSR5Constants.ORIGINAL_FOLDER_NAME));
						loTemp.setFilePath(loProp.getStringValue(HHSR5Constants.FILENET_MOVE_FROM));

						if (!loRestoredFolderPath.contains(loTemp.getFilePath()))
						{
							loDeleteList = restoreFolder(loPropMap, loOS, loTemp, loDBList, loMappingBeanList,
									aoRequiredMap, loDocCountMap, loTempRestoreList);
							//line commented to enable same name folder to get restored 4.0.2.0
							//loRestoredFolderPath.add(loTemp.getFilePath());
						}
					}
					else
					{
						loDocCountMap = restoreDocument(loPropMap, loOS, loXmlDoc, loDoc, loMappingBeanList,
								loDocCountMap, loDateFormat.format(loDate), loNameExistMap);
						liDocUpdateCountTemp = (Integer) loDocCountMap.get(HHSR5Constants.RENAMED_DOC_COUNT);
					}

					// delete folder hierarchy from RB
					if (null != loDeleteList && !loDeleteList.isEmpty())
					{
						UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(loOS.get_Domain(),
								RefreshMode.REFRESH);
						contentOperationHelper.deleteFolderhierarchy(loOS, loDoc.getDocumentId(), loUpdateBatch,
								loDeletedFolders);
						if (loUpdateBatch.hasPendingExecute())
						{
							loUpdateBatch.updateBatch();
						}
					}
					// }
				}
				for (Iterator iterator = loMappingBeanList.iterator(); iterator.hasNext();)
				{
					FolderMappingBean loFolderMappingBean = (FolderMappingBean) iterator.next();

					loFolderMappingBean.setOrganizationId(aoRequiredMap.get(HHSR5Constants.ORGANIZATION_ID));
					loFolderMappingBean.setModifiedBy(aoRequiredMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
					loFolderMappingBean.setCreatedBy(aoRequiredMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));

				}
				liDocUpdateCount = liDocUpdateCount + liDocUpdateCountTemp;
				List<Integer> loTemp = new ArrayList<Integer>();
				loTemp.add(liDocUpdateCount);
				loDbMap.put(HHSR5Constants.INSERTION_LIST, loMappingBeanList);
				loDbMap.put(HHSR5Constants.STATUS_LIST, loTemp);
				loDbMap.put(HHSR5Constants.DELETION_LIST, loDBList);
				loDbMap.put(HHSR5Constants.DOC_UPDATE_COUNT, loDocCountMap);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreFolder in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.restore()");
		return loDbMap;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method will restore Document or folder from recycle bin
	 * @param loPropMap
	 * @param aoOS
	 * @param loFolderData
	 * @return loFolderList
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private List<Document> restoreFolder(HashMap<String, Object> loPropMap, ObjectStore aoOS, Document loFolderData,
			List<Document> aoDBList, List<FolderMappingBean> aoMappingBeanList, HashMap<String, String> aoRequiredMap,
			HashMap<String, Object> aoDocCountMap, List<Document> aoRestoreList) throws ApplicationException
	{
		List<Document> loFolderList = new ArrayList<Document>();
		LOG_OBJECT.Info("Foder Path coming is ::::" + loFolderData.getFilePath());
		LOG_OBJECT.Info("Entered P8ContentService.restoreFolder()");
		try
		{
			if (contentOperationHelper.checkFolderExists(aoOS, loFolderData.getFilePath()))
			{
				mergeFoldersWhileRestoringFolder(loPropMap, aoOS, loFolderData, aoDBList, aoMappingBeanList,
						aoRequiredMap, aoDocCountMap, loFolderList, aoRestoreList);
			}
			else
			{
				restoringFoldersWithoutMerging(loPropMap, aoOS, loFolderData, aoDBList, aoMappingBeanList,
						aoDocCountMap, aoRestoreList);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreFolder in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.restoreFolder()");
		return loFolderList;
	}

	/**
	 * @param loPropMap
	 * @param aoOS
	 * @param loFolderData
	 * @param aoDBList
	 * @param aoMappingBeanList
	 * @throws ApplicationException
	 */
	private void restoringFoldersWithoutMerging(HashMap<String, Object> loPropMap, ObjectStore aoOS,
			Document loFolderData, List<Document> aoDBList, List<FolderMappingBean> aoMappingBeanList,
			HashMap<String, Object> aoDocCountMap, List<Document> aoRestoreList) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.restoringFoldersWithoutMerging()");
			String lsFolderParentPath = null;
			loPropMap.put(HHSR5Constants.DELETE_FLAG, 0);
			loPropMap.put(HHSR5Constants.IS_ARCHIVE, false);
			//Added for defect 8374 for Emergency release 4.0.1.0
			loPropMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			//Added for defect 8374 for Emergency release 4.0.1.0
			// Changing get key for Emergency Release 4.0.1
			String lsOrgId = (String) loPropMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID);
			lsFolderParentPath = loFolderData.getFilePath().substring(0,
					loFolderData.getFilePath().lastIndexOf(P8Constants.STRING_SINGLE_SLASH));
			Folder loParentFlder = contentOperationHelper.getFolderByName(aoOS, lsFolderParentPath,
					HHSR5Constants.HHS_CUSTOM_FOLDER, aoMappingBeanList, lsOrgId);
			loFolderData.setParent(loParentFlder);
			aoDBList.add(loFolderData);
			if (null != loParentFlder)
			{
				if (null != loParentFlder.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& loParentFlder.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID).toString()
								.equalsIgnoreCase(loParentFlder.get_Id().toString()))
				{
					loPropMap.put(HHSR5Constants.SHARING_FLAG,
							loParentFlder.getProperties().getStringValue(HHSR5Constants.SHARING_FLAG));
					loPropMap.put(HHSR5Constants.SHARED_ENTITY_ID,
							loParentFlder.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID));
				}
				contentOperationHelper.setPropertiesForRestoration(aoOS, loFolderData.getDocumentId(), loPropMap,
						HHSR5Constants.FOLDER, loParentFlder);
			}
			restoringChildFoldersAndDocs(loPropMap, aoOS, loFolderData, aoDocCountMap, aoRestoreList);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
	}

	/**
	 * @param loPropMap
	 * @param aoOS
	 * @param loFolderData
	 * @param loUpdateBatch
	 * @throws ApplicationException
	 */
	private void restoringChildFoldersAndDocs(HashMap<String, Object> loPropMap, ObjectStore aoOS,
			Document loFolderData, HashMap<String, Object> aoDocCountMap, List<Document> aoRestoreList)
			throws ApplicationException
	{
		HashMap<String, Integer> loDocCountIntermediateMap = new HashMap<String, Integer>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.restoringChildFoldersAndDocs()");
			List<Document> loFolderTempList;
			List<Document> loDocList;
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoOS.get_Domain(),
					RefreshMode.REFRESH);

			loDocCountIntermediateMap = (HashMap<String, Integer>) aoDocCountMap.get(HHSR5Constants.UPDATE_DOC_COUNT);
			String lsId = null;
			if (null != loFolderData.getReplicationId() && !loFolderData.getReplicationId().isEmpty())
			{
				lsId = loFolderData.getReplicationId();
			}
			else
			{
				lsId = loFolderData.getDocumentId();
			}
			loFolderTempList = contentOperationHelper.fetchDeletedChild(aoOS, lsId, HHSR5Constants.FOLDER);
			loDocList = contentOperationHelper.fetchDeletedChild(aoOS, lsId, HHSR5Constants.DOCUMENT);
			aoRestoreList.removeAll(loDocList);
			aoRestoreList.removeAll(loFolderTempList);
			for (Iterator iterator = loFolderTempList.iterator(); iterator.hasNext();)
			{
				restoreDeletedFolders(loPropMap, aoOS, iterator, loUpdateBatch);
			}
			if (loUpdateBatch.hasPendingExecute())
			{
				loUpdateBatch.updateBatch();
			}
			LOG_OBJECT.Info("Updated Folder batch complete");

			int liFinalPageCount = (int) Math.floor(loDocList.size() / ApplicationConstants.IN_QUERY_BREAK_LIMIT);
			if (loDocList.size() % ApplicationConstants.IN_QUERY_BREAK_LIMIT > 0)
				liFinalPageCount++;
			for (int i = 0; i < liFinalPageCount; i++)
			{
				int liStartIndex = i * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
				int liLastIndex = (i + 1) * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
				if (liLastIndex > loDocList.size())
					liLastIndex = loDocList.size();
				List loTempFolderList = loDocList.subList(liStartIndex, liLastIndex);
				loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoOS.get_Domain(), RefreshMode.REFRESH);
				for (Iterator iterator = loTempFolderList.iterator(); iterator.hasNext();)
				{
					restoreDeletedDocs(loPropMap, aoOS, iterator, loUpdateBatch);
				}
				if (loUpdateBatch.hasPendingExecute())
				{
					loUpdateBatch.updateBatch();
				}
			}

			LOG_OBJECT.Info("Updated Document batch complete");
			loDocCountIntermediateMap.put(loFolderData.getDocumentId(), loDocList.size());
			aoDocCountMap.put(HHSR5Constants.UPDATE_DOC_COUNT, loDocCountIntermediateMap);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
	}

	/**
	 * @param loPropMap
	 * @param aoOS
	 * @param loFolderData
	 * @param aoDBList
	 * @param aoMappingBeanList
	 * @param aoRequiredMap
	 * @param aoDocCountMap
	 * @param loFolderList
	 * @throws ApplicationException
	 */
	private void mergeFoldersWhileRestoringFolder(HashMap<String, Object> loPropMap, ObjectStore aoOS,
			Document loFolderData, List<Document> aoDBList, List<FolderMappingBean> aoMappingBeanList,
			HashMap<String, String> aoRequiredMap, HashMap<String, Object> aoDocCountMap, List<Document> loFolderList,
			List<Document> aoRestoreList) throws ApplicationException
	{
		List<Document> loFolderTempList;
		List<Document> loDocList;
		LOG_OBJECT.Info("Entered P8ContentService.mergeFoldersWhileRestoringFolder()");
		// merging
		try
		{
			String lsId = null;
			if (null != loFolderData.getReplicationId() && !loFolderData.getReplicationId().isEmpty())
			{
				lsId = loFolderData.getReplicationId();
				loFolderData.setPermamantDeleteFromDb(false);
			}
			else
			{
				lsId = loFolderData.getDocumentId();
				loFolderData.setPermamantDeleteFromDb(true);
			}
			loFolderTempList = contentOperationHelper.fetchDeletedChild(aoOS, lsId, HHSR5Constants.FOLDER);
			loDocList = contentOperationHelper.fetchDeletedChild(aoOS, lsId, HHSR5Constants.DOCUMENT);
			aoRestoreList.addAll(loFolderTempList);
			aoRestoreList.addAll(loDocList);
			if (null != loFolderTempList && !loFolderTempList.isEmpty())
			{
				for (Iterator iterator = loFolderTempList.iterator(); iterator.hasNext();)
				{
					Document loDoc = (Document) iterator.next();
					restoreFolder(loPropMap, aoOS, loDoc, aoDBList, aoMappingBeanList, aoRequiredMap, aoDocCountMap,
							aoRestoreList);
				}
			}
			if (null != loDocList && !loDocList.isEmpty())
			{
				org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
				DateFormat loDateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
				Date loDate = new Date();
				Map<String, Integer> loNameExistMap = new HashMap<String, Integer>();
				for (Iterator iterator = loDocList.iterator(); iterator.hasNext();)
				{
					Document loDoc = (Document) iterator.next();
					restoreDocument(loPropMap, aoOS, loXmlDoc, loDoc, aoMappingBeanList, aoDocCountMap,
							loDateFormat.format(loDate), loNameExistMap);
				}
			}

			aoDBList.add(loFolderData);
			Document loDocTemp = new Document();
			String lsFolderRBPath = FileNetOperationsUtils.setFolderPath(
					(String) aoRequiredMap.get(HHSR5Constants.ORGANIZATION_TYPE),
					(String) aoRequiredMap.get(HHSR5Constants.ORGANIZATION_ID), HHSR5Constants.RECYCLE_BIN);
			lsFolderRBPath = lsFolderRBPath + "/" + loFolderData.getFolderName();
			loDocTemp.setRBPath(lsFolderRBPath);
			loFolderList.add(loDocTemp);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}
	}

	/**
	 * @param loPropMap
	 * @param loOS
	 * @param iterator
	 */
	private void restoreDeletedDocs(HashMap<String, Object> loPropMap, ObjectStore loOS, Iterator iterator,
			UpdatingBatch aoUpdateBatch)
	{
		Document loDoc = (Document) iterator.next();
		com.filenet.api.core.Document loDocObj = Factory.Document.fetchInstance(loOS, new Id(loDoc.getDocumentId()),
				null);
		Properties loProp = loDocObj.getProperties();
		if (null != loPropMap && !loPropMap.isEmpty())
		{
			for (Map.Entry<String, Object> entry : loPropMap.entrySet())
			{
				loProp.putObjectValue(entry.getKey(), entry.getValue());
			}
		}
		loDocObj.setUpdateSequenceNumber(null);
		aoUpdateBatch.add(loDocObj, null);
	}

	/**
	 * @param loPropMap
	 * @param loOS
	 * @param iterator
	 */
	private void restoreDeletedFolders(HashMap<String, Object> loPropMap, ObjectStore loOS, Iterator iterator,
			UpdatingBatch aoUpdateBatch)
	{
		Document loDoc = (Document) iterator.next();
		Folder loFldr = Factory.Folder.fetchInstance(loOS, new Id(loDoc.getDocumentId()), null);
		Properties loProp = loFldr.getProperties();
		if (null != loPropMap && !loPropMap.isEmpty())
		{

			for (Map.Entry<String, Object> entry : loPropMap.entrySet())
			{
				loProp.putObjectValue(entry.getKey(), entry.getValue());
			}
		}
		aoUpdateBatch.add(loFldr, null);
	}

	/**
	 * @param loPropMap
	 * @param loOS
	 * @param loXmlDoc
	 * @param loDoc
	 * @throws ApplicationException
	 */
	private HashMap<String, Object> restoreDocument(HashMap<String, Object> loPropMap, ObjectStore loOS,
			org.jdom.Document loXmlDoc, Document loDoc, List<FolderMappingBean> aoMappingBeanList,
			HashMap<String, Object> aoDocCountMap, String asFormattedDate, Map<String, Integer> aoNameExistMap)
			throws ApplicationException
	{
		Document loDocument;
		String lsDocClassName;
		String lsDocId;
		int liRenamedDocCount = 0;
		if (aoDocCountMap.get(HHSR5Constants.RENAMED_DOC_COUNT) != null)
		{
			liRenamedDocCount = (Integer) aoDocCountMap.get(HHSR5Constants.RENAMED_DOC_COUNT);
		}
		HashMap<String, Integer> loDocCountIntermediateMap;
		LOG_OBJECT.Info("Entered P8ContentService.restoreDocument()");
		try
		{
			List<String> loDocCategory = FileNetOperationsUtils.getDocCategoryFromXML(loXmlDoc, loDoc.getDocType(),
					(String) loPropMap.get("ORGANIZATION_ID"));
			if (null != loDocCategory && loDocCategory.size() > 0)
			{
				lsDocClassName = contentOperationHelper.getDocClassName(loXmlDoc, loDoc.getDocType(),
						loDocCategory.get(0));
			}
			else
			{
				lsDocClassName = P8Constants.PROPERTY_CE_ROOT_DOCUMENT_CLASS_NAME;

			}
			loDocCountIntermediateMap = (HashMap<String, Integer>) aoDocCountMap.get(HHSR5Constants.UPDATE_DOC_COUNT);
			HashMap<String, String> loMap = new HashMap<String, String>();
			loMap.put(HHSR5Constants.PARENT_PATH, HHSConstants.EMPTY_STRING);
			loMap.put(HHSConstants.DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);
			loMap.put(HHSR5Constants.DOC_TYPE, HHSConstants.EMPTY_STRING);
			loMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, HHSConstants.EMPTY_STRING);
			PropertyFilter loPF = contentOperationHelper.createPropertyFilter(loMap);
			loDocument = contentOperationHelper.getDocProp(loOS, loDoc.getDocumentId(), HHSR5Constants.DOCUMENT, loPF);
			//Added for defect 8374 for Emergency release 4.0.1.0
			loPropMap.put(HHSR5Constants.DELETE_FLAG, 0);
			loPropMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loPropMap.put(HHSR5Constants.IS_ARCHIVE, false);
			//Added for defect 8374 for Emergency release 4.0.1.0
			// if the destination folder exists in DV
			if (contentOperationHelper.checkFolderExists(loOS, loDocument.getFilePath()))
			{
				liRenamedDocCount = restoreDocToExistingFolder(loPropMap, loOS, loDoc, asFormattedDate, aoNameExistMap,
						loDocument, lsDocClassName, liRenamedDocCount, loDocCountIntermediateMap, loDocCategory.get(0));
			}
			else
			{
				// Changing get key for Emergency Release 4.0.1
				String lsOrgId = (String) loPropMap.get(P8Constants.PROPERTY_CE_PROVIDER_ID);
				Folder loFldr = contentOperationHelper.getFolderByName(loOS, loDocument.getFilePath(),
						HHSR5Constants.HHS_CUSTOM_FOLDER, aoMappingBeanList, lsOrgId);
				// need to file in the document into new folder
				contentOperationHelper.setPropertiesForRestoration(loOS, loDoc.getDocumentId(), loPropMap,
						HHSR5Constants.DOCUMENT, loFldr);
				if (loDocCountIntermediateMap.containsKey(loFldr.get_Id().toString()))
				{
					Integer loCountVal = loDocCountIntermediateMap.get(loFldr.get_Id().toString());
					loCountVal++;
					loDocCountIntermediateMap.put(loFldr.get_Id().toString(), loCountVal);
				}
				else
				{
					loDocCountIntermediateMap.put(loFldr.get_Id().toString(), 1);
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception in Document Vault", loAppEx);
			throw loAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in restoreDocument in Document Vault", aoExp);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoExp);
		}

		aoDocCountMap.put(HHSR5Constants.RENAMED_DOC_COUNT, liRenamedDocCount);
		aoDocCountMap.put(HHSR5Constants.UPDATE_DOC_COUNT, loDocCountIntermediateMap);
		LOG_OBJECT.Info("Exiting P8ContentService.restoreDocument()");
		return aoDocCountMap;
	}

	/**
	 * This method is added in release 5, which will restore the document into
	 * an existing folder in document vault this method will apply the sharing
	 * details into the document.
	 * @param aoPropMap document property map
	 * @param aoOS object store object
	 * @param aoDoc document object
	 * @param asFormattedDate formatted date
	 * @param aoNameExistMap merge list
	 * @param aoDocument document bean
	 * @param asDocClassName document class
	 * @param aiRenamedDocCount document counter
	 * @param aoDocCountIntermediateMap document counter map
	 * @return
	 * @throws ApplicationException
	 */
	private int restoreDocToExistingFolder(HashMap<String, Object> aoPropMap, ObjectStore aoOS, Document aoDoc,
			String asFormattedDate, Map<String, Integer> aoNameExistMap, Document aoDocument, String asDocClassName,
			int aiRenamedDocCount, HashMap<String, Integer> aoDocCountIntermediateMap, String asDocCategory)
			throws ApplicationException
	{
		String lsDocId;
		String lsFilePath = null;
		org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		Folder loFldr = Factory.Folder.fetchInstance(aoOS, aoDocument.getFilePath(), null);
		if (null != aoPropMap.get("ORGANIZATION_ID"))
		{
			lsFilePath = contentOperationHelper.getFolderPath(loXmlDoc, aoDocument.getOrganizationId(),
					aoDocument.getDocType(), asDocCategory, aoPropMap.get("ORGANIZATION_ID").toString());
		}

		lsDocId = contentOperationHelper.getDocumentId(aoOS, asDocClassName, lsFilePath, aoDocument.getDocName(),
				aoDocument.getDocType());
		//if the folder where the document is getting restore is shared
		if(null != loFldr.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID) &&
				loFldr.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID).toString().equalsIgnoreCase(loFldr.get_Id().toString()))
		{
			aoPropMap.put(HHSR5Constants.SHARING_FLAG, loFldr.getProperties().getStringValue(HHSR5Constants.SHARING_FLAG));
			aoPropMap.put(HHSR5Constants.SHARED_ENTITY_ID,loFldr.getProperties().getObjectValue(HHSR5Constants.SHARED_ENTITY_ID));
		}
		
		// if same name document already exists in the restoring folder

		if (null != lsDocId && !lsDocId.isEmpty())
		{
			if (!lsDocId.equalsIgnoreCase(aoDoc.getDocumentId()))
			{
				String lsNewDocPath = getNewDocumentName(aoDocument.getDocName(), asFormattedDate, aoNameExistMap);
				aoPropMap.put(HHSR5Constants.DOCUMENT_TITLE, lsNewDocPath);
				contentOperationHelper.setPropertiesForRestoration(aoOS, aoDoc.getDocumentId(), aoPropMap,
						HHSR5Constants.DOCUMENT, loFldr);
				if (aoDocCountIntermediateMap.containsKey(loFldr.get_Id().toString()))
				{
					Integer loCountVal = aoDocCountIntermediateMap.get(loFldr.get_Id().toString());
					loCountVal++;
					aoDocCountIntermediateMap.put(loFldr.get_Id().toString(), loCountVal);
				}
				else
				{
					aoDocCountIntermediateMap.put(loFldr.get_Id().toString(), 1);
				}

				aiRenamedDocCount++;
			}
		}
		else
		{
			contentOperationHelper.setPropertiesForRestoration(aoOS, aoDoc.getDocumentId(), aoPropMap,
					HHSR5Constants.DOCUMENT, loFldr);
			if (aoDocCountIntermediateMap.containsKey(loFldr.get_Id().toString()))
			{
				Integer loCountVal = aoDocCountIntermediateMap.get(loFldr.get_Id().toString());
				loCountVal++;
				aoDocCountIntermediateMap.put(loFldr.get_Id().toString(), loCountVal);
			}
			else
			{
				aoDocCountIntermediateMap.put(loFldr.get_Id().toString(), 1);
			}
		}
		return aiRenamedDocCount;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * will get New Document Name.
	 * @param asDocName String to store document name
	 * @param asFormattedDate String
	 * @param aoNameExistMap Map<String, Integer>
	 * @return lsNewName String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */

	private String getNewDocumentName(String asDocName, String asFormattedDate, Map<String, Integer> aoNameExistMap)
	{
		String lsNewName;
		int liCounter = 0;
		LOG_OBJECT.Info("Entered P8ContentService.getNewDocumentName()");
		if (aoNameExistMap.containsKey(asDocName))
		{
			liCounter = aoNameExistMap.get(asDocName);
			lsNewName = asDocName + HHSR5Constants.UNDERSCORE + "R_" + asFormattedDate + HHSConstants.UNDERSCORE
					+ ++liCounter;
			aoNameExistMap.put(asDocName, liCounter);
		}
		else
		{
			liCounter = 0;
			lsNewName = asDocName + HHSR5Constants.UNDERSCORE + "R_" + asFormattedDate;
			aoNameExistMap.put(asDocName, liCounter);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getNewDocumentName()");
		return lsNewName;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * will move folder.
	 * @param aoUserSession
	 * @param abServiceFlag
	 * @param aoListItems
	 * @param asMoveTo
	 * @param asMoveFrom
	 * @return loDbMap
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Object>> moveItems(P8UserSession aoUserSession, Boolean abServiceFlag,
			List<Document> aoListItems, String asMoveTo, String asMoveFrom) throws ApplicationException
	{
		Boolean lbSharedFlag = false;
		HashMap<String, HashMap<String, Object>> loDbMap = new HashMap<String, HashMap<String, Object>>();
		ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
		List<Document> loFinalObjectList = new ArrayList<Document>();
		List<String> loFinalDeleteList = new ArrayList<String>();
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.DOCUMENT_IDS, aoListItems);
		// Fetching moving items list and Deleting Items List

		LOG_OBJECT.Info("Entered P8ContentService.moveItems()" + loHmReqExceProp);
		try
		{
			loDbMap = moveDocumentFolders(aoUserSession, abServiceFlag, aoListItems, asMoveTo, asMoveFrom, loDbMap,
					loFinalObjectList, loFinalDeleteList);
			loDbMap.put(HHSR5Constants.MOVING_ITEMS_LIST,
					generateHashMap(HHSR5Constants.MOVING_ITEMS_LIST, loFinalObjectList));
			loDbMap.put(HHSR5Constants.DELETE_ITEMS_LIST,
					generateHashMap(HHSR5Constants.DELETE_ITEMS_LIST, loFinalDeleteList));
			setLinkageForMove(aoListItems, asMoveTo, asMoveFrom, loOS);
			if (null != loDbMap && !loDbMap.isEmpty())
			{
				HashMap<String, Object> loMoveMap = (HashMap<String, Object>) loDbMap
						.get(HHSR5Constants.MOVING_ITEMS_LIST);
				lbSharedFlag = contentOperationHelper.move(loOS,
						(List<Document>) loMoveMap.get(HHSR5Constants.MOVING_ITEMS_LIST));
				HashMap<String, Object> loDeleteMap = (HashMap<String, Object>) loDbMap
						.get(HHSR5Constants.DELETE_ITEMS_LIST);
				contentOperationHelper.deleteFolder(loOS,
						(List<String>) loDeleteMap.get(HHSR5Constants.DELETE_ITEMS_LIST));
				HashMap<String, Object> loSharedMap = new HashMap<String, Object>();
				loSharedMap.put(HHSR5Constants.SHARED_FLAG, lbSharedFlag);
				loDbMap.put(HHSR5Constants.SHARED_FLAG, loSharedMap);
			}
			
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in share documents with different providers");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in share documents with different providers", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in share documents with different providers");
			ApplicationException loAppex = new ApplicationException("Error in share documents : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in share documents with different providers", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.moveItems()");
		return loDbMap;
	}

	/**
	 * @param aoListItems
	 * @param asMoveTo
	 * @param asMoveFrom
	 * @param loOS
	 * @throws ApplicationException
	 */
	private void setLinkageForMove(List<Document> aoListItems, String asMoveTo, String asMoveFrom, ObjectStore loOS)
			throws ApplicationException
	{
		Iterator<Document> iterator = aoListItems.iterator();
		while (iterator.hasNext())
		{
			Document loDoc = (Document) iterator.next();

			if (null != loDoc && StringUtils.isNotBlank(loDoc.getDocType())
					&& !loDoc.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
			{
				com.filenet.api.core.Document loDocObj = Factory.Document.fetchInstance(loOS, loDoc.getDocumentId(),
						null);
				loDoc.setLinkToApplication(loDocObj.getProperties().getBooleanValue(
						P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION));
				String lsPropertyName = P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION;
				if (loDoc.isLinkToApplication())
				{
					contentOperationHelper.resetFolderLinkAndShareStatus(loOS, asMoveTo, lsPropertyName,
							HHSConstants.STRING_TRUE, true);
					contentOperationHelper.resetFolderLinkAndShareStatus(loOS, asMoveFrom, lsPropertyName,
							HHSConstants.STRING_FALSE, true);
				}
			}
			else
			{
				com.filenet.api.core.Folder loDocObj = Factory.Folder.fetchInstance(loOS, loDoc.getDocumentId(), null);
				loDoc.setLinkToApplication(loDocObj.getProperties().getBooleanValue(
						P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION));
				String lsPropertyName = P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION;
				if (loDoc.isLinkToApplication())
				{
					contentOperationHelper.resetFolderLinkAndShareStatus(loOS, asMoveTo, lsPropertyName,
							HHSConstants.STRING_TRUE, true);
					contentOperationHelper.resetFolderLinkAndShareStatus(loOS, asMoveFrom, lsPropertyName,
							HHSConstants.STRING_FALSE, true);
				}

			}
		}
	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * will delete forever all the selected folders/document from Recycle Bin
	 * from filenet
	 * @param aoUserSession
	 * Adding one extra param aoAction for Emergency Release 4.0.1
	 * @param aoHashMapEntity
	 * @param aoDeleteFlag - Delete Flag
	 * @return delete status as boolean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean deleteDocumentForever(P8UserSession aoUserSession, HashMap aoHashMapEntity, Boolean aoDeleteFlag,String aoAction)
			throws ApplicationException
	{
		boolean lbIsDeleted = false;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.DOCUMENT_IDS, aoHashMapEntity);
			LOG_OBJECT.Info("Entered P8ContentService.deleteDocumentForever() with parameters::"
					+ loHmReqExceProp.toString() + "DeleteFlag::" + aoDeleteFlag);
			// Added for defect 7678
			if (null != aoUserSession && aoDeleteFlag)
			{
				if (aoHashMapEntity.isEmpty())
				{
					return lbIsDeleted;
				}
				else
				{
					ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

					lbIsDeleted = contentOperationHelper.deleteDocumentForever(loOS, aoHashMapEntity,aoAction);
				}
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Deleting The document from FileNet" + lbIsDeleted);
		}
		catch (ApplicationException aoAppex)
		{
			String lsMessage = aoAppex.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
				throw new ApplicationException(lsMessage, aoAppex);
			}
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Runtime Error in Fetching Filenet", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While Deleting The document from FileNet");
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While Deleting The document from FileNet", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.deleteDocumentForever()");
		return lbIsDeleted;
	}

	/**
	 * The method will get the list of the folders/file present in the Recycle
	 * Bin
	 * @param aoUserSession
	 * @param asfolderPath
	 * @return HashMap containing entity_id and its type
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getListOfEntityFromRecycleBin(P8UserSession aoUserSession, String asfolderPath)
			throws ApplicationException
	{

		HashMap loHmReqList = new HashMap();
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSR5Constants.FOLDER_PATH, asfolderPath);
			LOG_OBJECT.Info("Entered P8ContentService.getListOfEntityFromRecycleBin() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loHmReqList = contentOperationHelper.getListOfEntityFromRecycleBin(loOS, asfolderPath);

			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Deleting The document from FileNet" + loHmReqList);
		}
		catch (ApplicationException aoAppex)
		{
			String lsMessage = aoAppex.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
				throw new ApplicationException(lsMessage, aoAppex);
			}
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Runtime Error in Fetching Filenet", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While Deleting The document from FileNet");
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While Deleting The document from FileNet", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getListOfEntityFromRecycleBin()");

		return loHmReqList;

	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * will get Replication Id's
	 * @param aoUserSession
	 * @param aoFolderIdList
	 * @return lsFolderId
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public String getReplicationId(P8UserSession aoUserSession, List<String> aoFolderIdList)
			throws ApplicationException
	{
		String lsFolderId = null;
		LOG_OBJECT.Info("Entered P8ContentService.getReplicationId()");
		try
		{
			if (null != aoFolderIdList && !aoFolderIdList.isEmpty())
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				lsFolderId = aoFolderIdList.get(0);
				PropertyFilter loPf = new PropertyFilter();
				FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.REPLICATION_ID, null);
				loPf.addIncludeProperty(loFE);
				Folder loFldr = Factory.Folder.fetchInstance(loOS, lsFolderId, loPf);
				Properties loProp = loFldr.getProperties();
				String lsReplicationId = loProp.getStringValue(HHSR5Constants.REPLICATION_ID);
				if (null != lsReplicationId && !lsReplicationId.isEmpty())
				{
					lsFolderId = lsReplicationId;
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Runtime Error in Fetching Filenet", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While Deleting The document from FileNet");
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_M14);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			LOG_OBJECT.Error("Error While Deleting The document from FileNet", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getReplicationId()");
		return lsFolderId;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * will check Entity Length
	 * @param aoUserSession
	 * @param asMoveTo
	 * @param asMoveFrom
	 * @param lsFlag
	 * @return lbEntityCountFlag
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public boolean checkEntityLength(P8UserSession aoUserSession, String asMoveTo, List<Document> aoMoveListItems,
			Boolean lsFlag) throws ApplicationException
	{
		boolean lbEntityCountFlag = true;
		List<Document> loFolderChildList = new ArrayList<Document>();
		String lsDocOrFolderName = null;
		if (lsFlag)
		{
			try
			{

				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				for (Iterator iterator = aoMoveListItems.iterator(); iterator.hasNext();)
				{
					Document loDoc = (Document) iterator.next();
					//null passed as parameter in modified method 4.0.2.0
					loFolderChildList = contentOperationHelper.getChildList(loOS, loDoc.getDocumentId(),
							HHSR5Constants.INSUBFOLDER, null, null);
					//null passed as parameter in modified method 4.0.2.0
					LOG_OBJECT.Info("Child List:::::" + loFolderChildList);
					Folder loFldr = Factory.Folder.fetchInstance(loOS, asMoveTo, null);
					String lsMoveToPath = loFldr.get_PathName();
					String lsMoveToPathFromDV = lsMoveToPath.substring(lsMoveToPath
							.indexOf(HHSR5Constants.DOCUMENT_VAULT));
					int liMoveToPathlength = lsMoveToPathFromDV.length();
					HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
							.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
					String lsMaxFolderPath = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
							+ HHSR5Constants.DOCUMENT_VAULT_MAX_FOLDER_PATH;
					int liMaxFolderPath = Integer.parseInt(loApplicationSettingMap.get(lsMaxFolderPath));
					if (loDoc.getDocType() == null || (loDoc.getDocType().equalsIgnoreCase(HHSR5Constants.NULL)))
					{
						Folder loFldrIns = Factory.Folder.fetchInstance(loOS, loDoc.getDocumentId(), null);
						lsDocOrFolderName = loFldrIns.get_FolderName();
					}
					else
					{
						continue;
					}
					if (null == loFolderChildList || loFolderChildList.isEmpty())
					{
						lbEntityCountFlag = liMoveToPathlength + lsDocOrFolderName.length() + 1 <= liMaxFolderPath;
					}
					else
					{
						int limovingMaxLength = contentOperationHelper.getLongestPath(loFolderChildList,
								loDoc.getDocumentId(), loOS);
						LOG_OBJECT.Info("Longest Path:::::" + limovingMaxLength);
						LOG_OBJECT.Info("Longest Path:::::" + lsDocOrFolderName.length());
						LOG_OBJECT.Info("Move to Path Length:::::" + liMoveToPathlength);
						LOG_OBJECT.Info("Maximum count from DataBase:::::" + liMaxFolderPath);
						LOG_OBJECT.Info("Folder_count length::::::::" + limovingMaxLength + liMoveToPathlength);
						lbEntityCountFlag = ((limovingMaxLength + lsDocOrFolderName.length() + 1 + liMoveToPathlength) <= liMaxFolderPath);
					}
				}
			}
			catch (ApplicationException loAppex)
			{
				setMoState("Error in getting entity length.");
				LOG_OBJECT.Error("Error in getting entity length.", loAppex);
				throw loAppex;
			}
			catch (Exception loAppex)
			{
				setMoState("Error in getting entity length.");
				LOG_OBJECT.Error("Error in getting entity length.", loAppex);
				throw new ApplicationException(loAppex.getMessage(), loAppex);
			}

		}
		return lbEntityCountFlag;
	}

	// Batch
	/**
	 * This method is used to get document content from P8 server This method is
	 * using FileNet session for performing actions in filenet using FileNet API
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocId a list of dtring value used to denote document Ids.
	 * @return HashMap containing the content of the document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentListContent(P8UserSession aoUserSession, List<String> asDocId,
			HashMap<String, String> aoOrgMap) throws ApplicationException
	{
		InputStream loContent = null;
		HashMap loHmReqExceProp = new HashMap();
		HashMap loOutputHashMap = null;
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
			LOG_OBJECT.Info("Entered P8ContentService.getDocumentListContent() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loOutputHashMap = contentOperationHelper.getDocumentListContent(loOS, asDocId, aoOrgMap);
			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Content" + loContent);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentListContent()");
		return loOutputHashMap;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * is used to get document ids for download
	 * @param aoUserSession
	 * @param aoDownloadBean
	 * @return list of docIds
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getDocIdforDownload(P8UserSession aoUserSession, BulkDownloadBean aoDownloadBean)
			throws ApplicationException
	{

		List loHmReqList = new ArrayList();
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			LOG_OBJECT.Info("Entered P8ContentService.getDocIdforDownload() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loHmReqList = contentOperationHelper.getDocIdforDownload(loOS, aoDownloadBean);

			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Error While getting Bulk Download list" + loHmReqList);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting list of DocIds for download");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Bulk Download list", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting list of DocIds for download", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocIdforDownload()");

		return loHmReqList;

	}

	/**
	 * This method will recursively provide the list of entities ready for
	 * retention on Document Vault Screen
	 * @param asListItems
	 * @param aoFolder
	 * @return List of Id's
	 * @throws ApplicationException
	 */
	public List<String> createFolderListForDeletion(List<String> asListItems, Folder aoFolder)
			throws ApplicationException
	{

		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.createFolderListForDeletion()");
			if (null != aoFolder && null != aoFolder.get_Parent()
					&& !aoFolder.get_FolderName().equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT)
					&& !asListItems.contains(aoFolder.get_Id().toString()))
			{
				asListItems.add(aoFolder.get_Id().toString());
				createFolderListForDeletion(asListItems, aoFolder.get_Parent());
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"The Document May be deleted please relogin and try again", aoEx);
			LOG_OBJECT.Error("The Document May be deleted please relogin and try again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.createFolderListForDeletion()");
		return asListItems;
	}

	/**
	 * The method will check whether folder id exists or not
	 * @param aoUserSession
	 * @param aoFilterMap
	 * @return llStat
	 * @throws ApplicationException
	 */
	public boolean checkFolderPath(P8UserSession aoUserSession, String asFolderId, Boolean abOpenFolder,
			String asEntityType, String asJsp) throws ApplicationException
	{
		LOG_OBJECT.Info("Starting P8ContentService.checkFolderPath() Folder  :" + asFolderId + "    abOpenFolder ");

		boolean llStat = false;
		try
		{
			if (null != abOpenFolder && abOpenFolder)
			{
				LOG_OBJECT.Info("Getting Connection of CE ");
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				LOG_OBJECT.Info("After connection, checking Folder  :" + asFolderId + "    asEntityType:" + asEntityType  + "      asJsp:" + asJsp);
				contentOperationHelper.checkFolderPath(loOS, asFolderId, asEntityType, asJsp);
				LOG_OBJECT.Info("Folder Path : " + abOpenFolder);
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("The selected folder has been deleted", aoEx);
			LOG_OBJECT.Error("The selected folder has been deleted", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.checkFolderPath()");
		return llStat;
	}

	// Added for filenet Batch
	/**
	 * This method is added as a part of Release 5 for Document vault The method
	 * is used to create Document Vault and Recycle Bin folder
	 * @param aoP8UserSession
	 * @return list of FolderMappingBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap<String,Object> createDVandRBfolder(P8UserSession aoP8UserSession, List<String> aoDataList)
			throws ApplicationException
	{
		HashMap<String,Object> loreturnMap = new HashMap<String, Object>();
		try
		{
			LOG_OBJECT.Info("In Service file::" + aoDataList);
			ObjectStore loOS = filenetConnection.getObjectStore(aoP8UserSession);
			Boolean lbFlag = Boolean.valueOf(aoDataList.get(0));
			String lsPath = aoDataList.get(1);
			String lsName = aoDataList.get(2);
			loreturnMap = contentOperationHelper.createDVandRBfolder(loOS, lbFlag, lsPath, lsName);
		}
		catch (ApplicationException apAppEx)
		{
			LOG_OBJECT.Error("Error occurred while creating DV and RB folder ", apAppEx);
			throw apAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while creating DV and RB folder ", aoEx);
			throw new ApplicationException(aoEx.getMessage(), aoEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.createDVandRBfolder()");
		return loreturnMap;
	}

	/**
	 * This method is added as a part of Release 5 for Document vault This
	 * method is used to fetch doctype and category from Id
	 * @param aoSession
	 * @param aoDocId
	 * @return Map of String
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public HashMap<String, String> getdocTypeAndCategoryFromId(P8UserSession aoSession, String aoDocId)
			throws ApplicationException
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.getdocTypeAndCategoryFromId()");
			ObjectStore loOS = filenetConnection.getObjectStore(aoSession);
			com.filenet.api.core.Document loDoc = Factory.Document.fetchInstance(loOS, aoDocId, null);
			Properties loProp = loDoc.getProperties();
			loDataMap.put(HHSR5Constants.DOCTYPE, loProp.getStringValue(P8Constants.PROPERTY_CE_DOC_TYPE));
			loDataMap
					.put(HHSR5Constants.DOCUMENT_CATEGORY, loProp.getStringValue(P8Constants.PROPERTY_CE_DOC_CATEGORY));

		}

		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error in fetching doctype", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetching doctype", aoEx);
			LOG_OBJECT.Error("Error in fetching doctype", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getdocTypeAndCategoryFromId()");
		return loDataMap;
	}

	/**
	 * This method is added as a part of release 5 for adding custom words into
	 * the dictionary.
	 * @param aoUser user id
	 * @param aoUserSession p8user session object
	 * @param asDocType document type
	 * @param aoIS document content
	 * @return boolean status for addition of word
	 * @throws ApplicationException when any exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean setDictionaryContent(String aoUser, P8UserSession aoUserSession, String asDocType,
			FileInputStream aoIS) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		boolean lbDataSet = false;
		try
		{
			if (null == aoUserSession)
			{
				throw new ApplicationException("Internal Error Occured While Processing Your Request");
			}

			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_TYPE, asDocType);
			LOG_OBJECT.Info("Entered P8ContentService.setDictionaryContent() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
				lbDataSet = contentOperationHelper.setDictionaryContent(aoUser, loOS, loXmlDoc, asDocType, aoIS);

				filenetConnection.popSubject(aoUserSession);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting document content by type");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting document content by type", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting document content by type");
			ApplicationException loAppex = new ApplicationException("Error While getting document content by type",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting document content by type", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.setDictionaryContent()");
		return lbDataSet;
	}

	/**
	 * The method will give list of agency/provider for a specific organization
	 * which has shared document with them.
	 * @param aoUserSession
	 * @param asOrgId as logged in organization
	 * @return list of provider/agency with whom logged in organization has
	 *         shared document.
	 * @throws ApplicationException
	 */
	public ArrayList<String> getProviderAgencySharedList(P8UserSession aoUserSession, String asOrgId,
			String asPartialDomVal) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		ArrayList<String> loSharedWithList = new ArrayList<String>();
		LOG_OBJECT.Info("Entered P8ContentService.getProviderAgencySharedList()");
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loSharedWithList = contentOperationHelper.getProviderAgencySharedList(loOS, asOrgId, asPartialDomVal);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting list of agency/provider ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting document content by type", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting list of agency/provider ");
			ApplicationException loAppex = new ApplicationException("Error While getting list of agency/provider ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting list of agency/provider ", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getProviderAgencySharedList()");
		return loSharedWithList;

	}

	/**
	 * The method will get Folder Path From Filenet
	 * @param aoUserSession P8User session bean object
	 * @param aoDocIdMap HashMap<String, String>
	 * @param asAction String
	 * @return loPathMap HashMap containing Folder Path
	 * @throws ApplicationException when ever any exception occurred
	 */

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getFolderPathFromFilenet(P8UserSession aoUserSession,
			HashMap<String, String> aoDocIdMap, String asAction) throws ApplicationException
	{
		HashMap<String, Object> loPathMap = new HashMap<String, Object>();
		HashMap<String, Object> loPathMapTemp = new HashMap<String, Object>();
		LOG_OBJECT.Info("Entered P8ContentService.getFolderPathFromFilenet()");
		try
		{
			boolean loflag = true;
			HashMap loMap = new HashMap();
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			for (Map.Entry<String, String> entry : aoDocIdMap.entrySet())
			{
				if (null != entry.getValue() && !entry.getValue().isEmpty()
						&& !entry.getValue().equalsIgnoreCase(HHSConstants.NULL))
				{
					loMap.put(HHSR5Constants.PARENT_PATH, HHSConstants.EMPTY_STRING);
					loMap.put(HHSR5Constants.DELETE_FLAG, HHSConstants.EMPTY_STRING);
					loMap.put(HHSR5Constants.FOLDERS_FILED_IN, HHSConstants.EMPTY_STRING);
					PropertyFilter loPf = contentOperationHelper.createPropertyFilter(loMap);
					LOG_OBJECT.Info("PropoertyFilter from createPropertyFilter : " + loPf);
					List<String> lsDocumentList = new ArrayList<String>();
					lsDocumentList.add(entry.getKey());
					loPathMapTemp = contentOperationHelper.getBRDcoumentPropertiesById(loOS, loMap, lsDocumentList);
					for (Map.Entry<String, Object> entryMap : loPathMapTemp.entrySet())
					{
						List<String> loLockList = new ArrayList<String>();
						HashMap<String, Object> loTempMap = new HashMap<String, Object>();
						loTempMap = (HashMap<String, Object>) entryMap.getValue();
						String lsPath = (String) loTempMap.get(HHSR5Constants.FOLDERS_FILED_IN);
						if (null != lsPath)
						{
							if (lsPath.contains(HHSR5Constants.RECYCLE_BIN))
							{
								loLockList.add("/" + HHSR5Constants.RECYCLE_BIN);
							}
							else
							{
								lsPath = lsPath.substring(lsPath.indexOf("/" + HHSR5Constants.DOCUMENT_VAULT));
							}
							loLockList.add(lsPath);
						}

						String lsPathRecycle = null;
						Integer lsDeleteFlag = (Integer) loTempMap.get(HHSR5Constants.DELETE_FLAG);
						if (null != lsDeleteFlag && Integer.valueOf(lsDeleteFlag) > 0)
						{
							lsPathRecycle = "/" + HHSR5Constants.RECYCLE_BIN;
							loLockList.add(lsPathRecycle);
						}
						loPathMap.put(entryMap.getKey(), loLockList);
					}
				}
				else
				{
					List<String> loLockList = new ArrayList<String>();
					LOG_OBJECT.Info("Folder Key for Locking::::::::::::" + entry.getKey());
					if (null != entry.getKey() && !entry.getKey().isEmpty()
							&& !entry.getKey().toLowerCase().contains(HHSR5Constants.RECYCLE_BIN_ID.toLowerCase()))
					{
						loflag = contentOperationHelper.checkFolderExistsById(loOS, entry.getKey());
					}
					if (loflag)
					{
						if (StringUtils.isNotBlank(asAction) && asAction.equalsIgnoreCase(HHSR5Constants.RESTORE))
						{
							loMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSConstants.NULL);
						}
						else
						{
							loMap.put(HHSR5Constants.PATH_NAME, HHSConstants.NULL);
						}

						String lsPath = null;
						String lsPathRecycle = null;
						loMap.put(HHSR5Constants.DELETE_FLAG, HHSConstants.NULL);
						PropertyFilter loPf = contentOperationHelper.createPropertyFilter(loMap);
						HashMap loFolderMap = new HashMap();
						if (entry.getKey().toLowerCase().contains(HHSR5Constants.RECYCLE_BIN_ID.toLowerCase()))
						{
							loLockList.add("/" + HHSR5Constants.RECYCLE_BIN);
						}
						else
						{
							loFolderMap = contentOperationHelper.getBRFolderPropertiesById(loOS, entry.getKey(), loPf);
							lsPath = (String) loFolderMap.get(HHSR5Constants.PATH_NAME);
							if (null == lsPath)
							{
								lsPath = (String) loFolderMap.get(HHSR5Constants.FILENET_MOVE_FROM);
							}
							if (!lsPath.contains(HHSR5Constants.RECYCLE_BIN))
							{
								lsPath = lsPath.substring(lsPath.indexOf("/" + HHSR5Constants.DOCUMENT_VAULT));
							}

							loLockList.add(lsPath);
							Integer lsDeleteFlag = (Integer) loFolderMap.get(HHSR5Constants.DELETE_FLAG);
							if (null != lsDeleteFlag && Integer.valueOf(lsDeleteFlag) > 0)
							{
								lsPathRecycle = "/" + HHSR5Constants.RECYCLE_BIN;
								loLockList.add(lsPathRecycle);
							}
						}
						loPathMap.put(entry.getKey(), loLockList);
					}
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Folder Path From Filenet");
			LOG_OBJECT.Error("Error While getting Folder Path From Filenet", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error While getting Folder Path From Filenet", aoEx);
			throw new ApplicationException(aoEx.getMessage(), aoEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getFolderPathFromFilenet()");
		return loPathMap;
	}

	// R5 Ends
	/**
	 * This method will create new folder in filenet
	 * @param aoUserSession P8User session bean object
	 * @param asnewFolderPath Path to create new folder
	 * @param asnewFoldername new Folder Name to be created
	 * @param asUserOrgType Organization Type
	 * @param asUserOrg Organization User
	 * @param lsUserName login user name
	 * @return loreturnBean contains metadata of created folder
	 * @throws ApplicationException when ever any exception occurred
	 */
	public List<FolderMappingBean> createFolder(P8UserSession aoUserSession, String asParentFolderId,
			String asNewFolderName, String asUserOrgType, String asUserOrg, String lsUserName)
			throws ApplicationException
	{
		Folder loParentFolder;
		boolean lbCheckFolderExist = false;
		String lsParentFolderId = null;
		FolderMappingBean loFolderMappingBean = new FolderMappingBean();
		List<FolderMappingBean> loFolderBean = new ArrayList<FolderMappingBean>();
		HashMap<String, String> loPropertyMap = new HashMap<String, String>();
		LOG_OBJECT.Info("Entered P8ContentService.createFolder()");
		Folder loNewFolder = null;
		try
		{
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				lbCheckFolderExist = contentOperationHelper.checkFolderExistsById(loOS, asParentFolderId);
				if (!lbCheckFolderExist && asParentFolderId.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID))
				{
					// Create DocumentVault and RecyleBin Folder
					contentOperationHelper.getFolderByName(loOS,
							FileNetOperationsUtils.setFolderPath(asUserOrgType, asUserOrg, HHSR5Constants.RECYCLE_BIN),
							HHSR5Constants.HHS_CUSTOM_FOLDER, null, asUserOrg);
					loParentFolder = contentOperationHelper.getFolderByName(loOS, FileNetOperationsUtils.setFolderPath(
							asUserOrgType, asUserOrg, HHSR5Constants.DOCUMENT_VAULT), HHSR5Constants.HHS_CUSTOM_FOLDER,
							loFolderBean, asUserOrg);
					lsParentFolderId = HHSR5Constants.DELIMITER_SINGLE_HASH;
				}
				else if (lbCheckFolderExist && !asParentFolderId.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID))
				{
					loPropertyMap.put(HHSR5Constants.IS_FOLDER_SHARED, ApplicationConstants.EMPTY_STRING);
					loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, ApplicationConstants.EMPTY_STRING);
					loPropertyMap.put(HHSR5Constants.PATH_NAME, ApplicationConstants.EMPTY_STRING);
					loPropertyMap.put(HHSConstants.TEMPLATE_IDEN, ApplicationConstants.EMPTY_STRING);
					loPropertyMap.put(HHSR5Constants.SHARING_FLAG, ApplicationConstants.EMPTY_STRING);
					loParentFolder = contentOperationHelper.getFolderObjectById(loOS, asParentFolderId, loPropertyMap);
				}
				else
				{
					String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.PARENT_FOLDER_NOT_EXISTS);
					ApplicationException loAppex = new ApplicationException(lsMessage);
					throw loAppex;
				}
				// create new folder in CE
				loNewFolder = contentOperationHelper.createFolder(loOS, asNewFolderName, asUserOrgType, loParentFolder,
						asUserOrg);
				lsParentFolderId = loParentFolder.get_Id().toString();
				loFolderMappingBean.setOrganizationType(asUserOrgType);
				// Prepare Bean for Folder mapping Table
				contentOperationHelper.getFolderBeanMappingForDB(asNewFolderName, asUserOrg, lsUserName,
						loFolderMappingBean, loNewFolder, lsParentFolderId);
				loFolderBean.add(loFolderMappingBean);

			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in creating Folder", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in creating Folder", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exiting P8ContentService.createFolder()");
		return loFolderBean;
	}

	/**
	 * This method is added in release 5 , this will roll back the create folder
	 * transaction by deleting the folder if Data base transaction fails due to
	 * some reason.
	 * @param aoUserSession user bean with filenet connection details
	 * @param aoFolderBean folder bean with all details
	 * @param liRowsInserted number of rows inserted in DB
	 * @throws ApplicationException
	 * Changing Method parameters for createfolder issues in 4.0.2 for Defect # 8408
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public void rollBackCreateFolder(P8UserSession aoUserSession, List<FolderMappingBean> aoFolderBeanList,
			Integer liRowsInserted,Integer aiRowsUpdated,Integer aiRowsInsertedInAudit) throws ApplicationException
	{
		List<String> loRollBackList = new ArrayList<String>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.rollBackCreateFolder()");
			if(null != aoFolderBeanList && !aoFolderBeanList.isEmpty())
			{
				ObjectStore loObjStr = filenetConnection.getObjectStore(aoUserSession);
				if (liRowsInserted < 1 || aiRowsUpdated < 1 || aiRowsInsertedInAudit < 1)
				{
					for (Iterator iterator = aoFolderBeanList.iterator(); iterator.hasNext();)
					{
						FolderMappingBean loFolderBean = (FolderMappingBean) iterator.next();
						loRollBackList.add(loFolderBean.getFolderFilenetId());
						
					}
					contentOperationHelper.deleteFolder(loObjStr, loRollBackList);
					throw new ApplicationException("Internal Error Occured While Processing Your Request");
				}
			}
			
			

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while rolling back folder creation", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in creating Folder", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
	}

	/**
	 * This method is added to get Shared Organization Details For City users
	 * @param aoUserSession user bean with filenet connection details
	 * @param asOrgId String
	 * @return loDataList List
	 * @throws ApplicationException if there is any exception
	 */
	public HashMap<String, String> getSharedOrgDetails(P8UserSession aoUserSession, String asOrgId)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentService.getSharedOrgDetails");
		try
		{
			ObjectStore loObjStr = filenetConnection.getObjectStore(aoUserSession);
			return contentOperationHelper.getSharedOrgDetails(loObjStr, asOrgId);

		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while rolling back folder creation", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in creating Folder", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
	}

	/**
	 * This method is added to get list of DocIds for download from filenet
	 * @param aoUserSession user bean with filenet connection details
	 * @param aoBean BulkDownloadBean
	 * @return loOrgListData List
	 * @throws ApplicationException if there is any exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> orgListContentFilenet(P8UserSession aoUserSession, BulkDownloadBean aoBean)
			throws ApplicationException
	{
		List<String> loOrgListData = new ArrayList<String>();
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			LOG_OBJECT.Info("Entered P8ContentService.getDocIdforDownload() with parameters::"
					+ loHmReqExceProp.toString());
			if (null != aoUserSession)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				loOrgListData = contentOperationHelper.getOrgIdforDownload(loOS, aoBean);

			}
			filenetConnection.popSubject(aoUserSession);
			setMoState("Error While getting Bulk Download list" + loOrgListData);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting list of DocIds for download");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Bulk Download list", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting list of DocIds for download", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocIdforDownload()");

		return loOrgListData;
	}

	/**
	 * This method will inherit the folder properties if the document is getting
	 * uploaded into a custom folder
	 * @param aoObjectStore Object store
	 * @param aoPropertyMap property map of the document
	 * @param asDocumentId document id
	 * @throws ApplicationException if there is any exception
	 */
	public void setParentFolderProprty(ObjectStore aoObjectStore, HashMap<String, Object> aoPropertyMap,
			String asDocumentId) throws ApplicationException
	{

		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.setParentFolderProprty()");
			contentOperationHelper.setParentFolderProprty(aoObjectStore, aoPropertyMap, asDocumentId);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error Occured while uploading your Document");
			LOG_OBJECT.Error("Error Occured while uploading your Document", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error Occured while uploading your Document", aoEx);
			LOG_OBJECT.Error("Error Occured while uploading your Document", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method will return the hashmap containing share list for a document
	 * id passed as input.
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoHmRequiredProps a hash map containing properties as key
	 * @param aoDocumentsIdList list of documents
	 * @return HashMap a hash map containing properties as key value as key
	 *         value pair
	 * @throws ApplicationException if there is any exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getShareDcoumentPropertiesById(P8UserSession aoUserSession, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{
		HashMap loHmProps = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSR5Constants.HM_REQUIRED_PROPS, aoHmRequiredProps);
		loHmReqExceProp.put(HHSR5Constants.DOCUMENTS_ID_LIST, aoDocumentsIdList);
		LOG_OBJECT.Info("Entered P8ContentService.getShareDcoumentPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			// Fetching FILENET object store from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loHmProps = contentOperationHelper.getSharePropertiesById(loOS, aoHmRequiredProps, aoDocumentsIdList);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Properties" + loHmProps);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting Share document Propeties ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Share document Propeties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting Share document Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Share document Propeties : ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Share document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getShareDcoumentPropertiesById()");
		return loHmProps;
	}

	/**
	 * This method will return the hashmap containing share list for a folder id
	 * passed as input.
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoHmRequiredProps a hash map containing properties as key
	 * @param aoDocumentsIdList list of documents
	 * @return HashMap a hash map containing properties as key value as key
	 *         value pair
	 * @throws ApplicationException if there is any exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getShareFolderPropertiesById(P8UserSession aoUserSession, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{
		HashMap loHmProps = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(HHSR5Constants.HM_REQUIRED_PROPS, aoHmRequiredProps);
		loHmReqExceProp.put(HHSR5Constants.DOCUMENTS_ID_LIST, aoDocumentsIdList);
		LOG_OBJECT.Info("Entered P8ContentService.getShareFolderPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loHmProps = contentOperationHelper.getShareFolderPropertiesById(loOS, aoHmRequiredProps, aoDocumentsIdList);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Document Properties" + loHmProps);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting Share document Propeties ");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Share folder Propeties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting Share folder Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Share folder Propeties : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting folder document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getShareFolderPropertiesById()");
		return loHmProps;
	}

	/**
	 * This method will create Custom Obj In Filenet
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoOrgList list of ProviderBean
	 * @return loCount count of Custom Obj created In Filenet
	 * @throws ApplicationException if there is any exception
	 */
	public int createCustomObjInFilenet(P8UserSession aoUserSession, List<ProviderBean> aoOrgList)
			throws ApplicationException
	{
		Integer loCount = 0;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentService.createCustomObjInFilenet()");
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loCount = contentOperationHelper.createCustomObjInFilenet(loOS, aoOrgList);
			filenetConnection.popSubject(aoUserSession);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting Share document Propeties ");
			LOG_OBJECT.Error("Error in getting Share folder Propeties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting Share folder Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Share folder Propeties : ", aoEx);
			LOG_OBJECT.Error("Error in getting folder document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.createCustomObjInFilenet()");
		return loCount;
	}

	/**
	 * This method will update Custom Obj In Filenet
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoMap HashMap of type <String, String>
	 * @param aoAction String
	 * @return loCount count of Custom Obj created In Filenet
	 * @throws ApplicationException if there is any exception
	 */
	public int updateCustomObject(P8UserSession aoUserSession, HashMap<String, String> aoMap, String aoAction)
			throws ApplicationException
	{
		Integer loCount = 0;
		try
		{
			if (null != aoMap
					&& (aoMap.get(HHSConstants.STATUS_COLUMN).toString()
							.equalsIgnoreCase(ApplicationConstants.SYSTEM_YES) || aoMap.get(HHSConstants.STATUS_COLUMN)
							.toString().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)))
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				ProviderBean loProviderBean = new ProviderBean();
				loProviderBean.setDisplayValue(aoMap.get(HHSR5Constants.NEW_NAME));
				loProviderBean.setHiddenValue(aoMap.get(HHSR5Constants.PROVIDERS_ID));
				loCount = 1;
				if (null != aoAction && aoAction.equalsIgnoreCase(HHSConstants.OPERATION_ADD))
				{
					contentOperationHelper.createCustomObjectForOrganization(loOS, loProviderBean);

				}
				else if (null != aoAction && aoAction.equalsIgnoreCase(HHSR5Constants.UPDATE))
				{
					contentOperationHelper.updateCustomObjectForOrganization(loOS, loProviderBean);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in getting Share document Propeties ");
			LOG_OBJECT.Error("Error in getting Share folder Propeties", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting Share folder Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Share folder Propeties : ", aoEx);
			LOG_OBJECT.Error("Error in getting folder document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.updateCustomObject()");
		return loCount;
	}

	/**
	 * This method will fetch shared document Id's form Filenet for defect 8150
	 * @param aoUserSession
	 * @param asAgencyType
	 * @param asProviderId
	 * @param asShareWith
	 * @return
	 * @throws ApplicationException
	 */
	public HashMap<String, List<String>> getSharedDocumentsIdList(P8UserSession aoUserSession, String asAgencyType,
			String asProviderId, String asShareWith) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, List<String>> loSharedAgencyProviderSet = null;
		try
		{
			loHmReqExceProp.put(HHSR5Constants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_PROVIDER_ID, asProviderId);
			LOG_OBJECT.Info("Entered P8ContentService.getSharedDocumentsIdList with parameters::"
					+ loHmReqExceProp.toString());
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loSharedAgencyProviderSet = contentOperationHelper.getSharedDocumentsIdList(loOS, asAgencyType,
					asProviderId, asShareWith);
			filenetConnection.popSubject(aoUserSession);
			setMoState("Shared agency provider list" + loSharedAgencyProviderSet);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while getting shared agency and provider list");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while getting shared agency and provider list");
			ApplicationException loAppex = new ApplicationException(
					"Error while getting shared agency and provider list : ", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while getting shared agency and provider list", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getSharedDocumentsIdList()");
		return loSharedAgencyProviderSet;
	}

	/**
	 * This method is used to update Linkage In Filenet.
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asDocumentId String
	 * @return Boolean lbLinked
	 * @throws ApplicationException If an Exception occurs
	 */
	public Boolean updateLinkageInFilenet(P8UserSession aoUserSession, String asDocumentId) throws ApplicationException
	{
		boolean lbLinked = false;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(HHSR5Constants.FILE_NET_SESSION, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(HHSConstants.DOC_ID, asDocumentId);
			LOG_OBJECT.Info("Entered P8ContentService.updateLinkageInFilenet with parameters::"
					+ loHmReqExceProp.toString());
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			lbLinked = contentOperationHelper.updateLinkageInFilenet(loOS, asDocumentId);
			filenetConnection.popSubject(aoUserSession);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while updating linked status for doc id :::" + asDocumentId);
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating linked status for doc id :::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error while updating linked status for doc id :::");
			ApplicationException loAppex = new ApplicationException(
					"Error while updating linked status for doc id :::", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while updating linked status for doc id :::", aoEx);
			throw loAppex;
		}
		return lbLinked;
	}

	/**
	 * This method is used to update Organization Legal Name.
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param asOrgId String
	 * @param aoParam HashMap
	 * @return Integer lbcount
	 * @throws ApplicationException If an Exception occurs
	 */
	public Integer updateOrgLegalName(P8UserSession aoUserSession, String asOrgId, HashMap aoParam)
			throws ApplicationException
	{
		int lbcount = 0;
		HashMap<String, String> loMap = new HashMap<String, String>();
		try
		{
			if (null != aoParam
					&& !aoParam.isEmpty()
					&& aoParam.get(HHSConstants.SECTION).toString()
							.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
					&& aoParam.containsKey("OLN"))
			{
				loMap.put(HHSConstants.STATUS_COLUMN, HHSConstants.STR_BUDGET_APPROVED);
				loMap.put(HHSR5Constants.NEW_NAME, (String) aoParam.get("OLN"));
				loMap.put(HHSR5Constants.PROVIDERS_ID, asOrgId);
				lbcount = updateCustomObject(aoUserSession, loMap, HHSConstants.UPDATE);
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while updateOrgLegalName");
			LOG_OBJECT.Error("updateOrgLegalName", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("updateOrgLegalName");
			ApplicationException loAppex = new ApplicationException("updateOrgLegalName", aoEx);
			LOG_OBJECT.Error("updateOrgLegalName", aoEx);
			throw loAppex;
		}
		return lbcount;
	}

	/**
	 * This method is used to update Linkage In Filenet For Batch.
	 * 
	 * @param aoUserSession a custom user bean having information about user
	 * @param aoDocIdList List<String>
	 * @return Integer lsCount
	 * @throws ApplicationException If an Exception occurs
	 */
	public HashMap<String,Object> updateLinkageInFilenetForBatch(P8UserSession aoUserSession, List<String> aoDocIdList)
			throws ApplicationException
	{
		HashMap<String,Object> loDbMap = new HashMap<String, Object>();
		int lsCount = 0;
		List<String> lsDocIdListFromFileNet = new ArrayList<String>();
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			HashMap<String, String> loMap = new HashMap<String, String>();
			LOG_OBJECT.Error("doc id list coming from Database is size:::" + aoDocIdList.size());
			LOG_OBJECT.Error("doc id list coming from Database is elements:::" + aoDocIdList);
			lsDocIdListFromFileNet = getDataFromFilenet(loOS, aoDocIdList);
			LOG_OBJECT.Error("doc id list coming size from filenet is: for flag false is::"
					+ lsDocIdListFromFileNet.size());
			LOG_OBJECT.Error("doc id list coming elements from filenet is: for flag false is::"
					+ lsDocIdListFromFileNet);
			if (null != lsDocIdListFromFileNet && !lsDocIdListFromFileNet.isEmpty())
			{
				loMap.put("LINK_TO_APPLICATION", null);

				for (Iterator iterator = lsDocIdListFromFileNet.iterator(); iterator.hasNext();)
				{
					String lsDocId = (String) iterator.next();
					com.filenet.api.core.Document loDoc = Factory.Document.fetchInstance(loOS, lsDocId,
							contentOperationHelper.createPropertyFilter(loMap));
					loDoc.getProperties().putValue("LINK_TO_APPLICATION", false);
					loDoc.save(RefreshMode.REFRESH);
					lsCount++;
				}

			}

		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error while updateOrgLegalName");
			LOG_OBJECT.Error("updateOrgLegalName", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("updateOrgLegalName");
			ApplicationException loAppex = new ApplicationException("updateOrgLegalName", aoEx);
			LOG_OBJECT.Error("updateOrgLegalName", aoEx);
			throw loAppex;
		}
		loDbMap.put("rowCount", lsCount);
		loDbMap.put("DocIdList", lsDocIdListFromFileNet);
		loDbMap.put("entityName", "r5removelinkage");
		return loDbMap;
	}

	/**
	 * This method is used to get Data From Filenet.
	 * 
	 * @param aoObj ObjectStore
	 * @param aoDocIdList List
	 * @return List<String> lsDocIdListInner
	 * @throws ApplicationException If an Exception occurs
	 */
	public List<String> getDataFromFilenet(ObjectStore aoObj, List<String> asDocIdList) throws ApplicationException
	{
		List<String> lsDocIdListInner = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			String lsQuery = "select this,id from HHS_ACCELERATOR where LINK_TO_APPLICATION = true";
			loSqlObject.setQueryString(lsQuery);
			SearchScope loSearchScope = new SearchScope(aoObj);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				// type casting the custom object and deleting the same.
				com.filenet.api.core.Document loObj = (com.filenet.api.core.Document) loIt.next();
				String lsDocumentId = loObj.getProperties().getIdValue(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString();
				lsDocIdListInner.add(lsDocumentId);
			}
			LOG_OBJECT.Error("List from filenet with link to app true is:::::::::" + lsDocIdListInner.size());
			LOG_OBJECT.Error("List from filenet with link to app true is elements::::::::::::::::" + lsDocIdListInner);
			lsDocIdListInner.removeAll(asDocIdList);
		}
		catch (Exception aoEx)
		{
			setMoState("getDataFromFilenet");
			ApplicationException loAppex = new ApplicationException("getDataFromFilenet", aoEx);
			LOG_OBJECT.Error("getDataFromFilenet", aoEx);
			throw loAppex;
		}
		return lsDocIdListInner;
	}

	/**
	 * The method will check if there is document present in the document vault
	 * or not for the logged in user.
	 * @param aoObj
	 * @param asFolderPath
	 * @return
	 * @throws ApplicationException
	 */
	public boolean checkDocumentPresent(P8UserSession aoUserSession, String asFolderPath) throws ApplicationException
	{
		boolean lbDocPresent;
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			lbDocPresent = contentOperationHelper.checkDocumentPresent(loOS, asFolderPath);
		}
		catch (Exception aoEx)
		{
			setMoState("getDataFromFilenet");
			ApplicationException loAppex = new ApplicationException("getTotalDocumentCount", aoEx);
			LOG_OBJECT.Error("getDataFromFilenet", aoEx);
			throw loAppex;
		}
		return lbDocPresent;
	}

	/**
	 * The method will check if there is document or folder present in the
	 * document vault or not for the logged in user.
	 * @param aoUserSession P8UserSession
	 * @param aoSelectedObjectsMap HashMap<String,String>
	 * @param asJspName String
	 * @return lbDocPresent boolean
	 * @throws ApplicationException - if any exception occurs
	 */
	public boolean checkFolderAndDocumentExist(P8UserSession aoUserSession,
			HashMap<String, String> aoSelectedObjectsMap, String asJspName) throws ApplicationException
	{
		boolean lbDocPresent;
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);

			lbDocPresent = contentOperationHelper.checkFolderAndDocumentExist(loOS, aoSelectedObjectsMap, asJspName);
		}
		catch (Exception aoEx)
		{
			setMoState("getDataFromFilenet");
			ApplicationException loAppex = new ApplicationException("getTotalDocumentCount", aoEx);
			LOG_OBJECT.Error("getDataFromFilenet", aoEx);
			throw loAppex;
		}
		return lbDocPresent;
	}

	/**
	 * The method is used to check Parent Child folder RelationShip
	 * @param aoUserSession P8UserSession
	 * @param aoSelectedObjectList HashMap<String,String>
	 * @param lbServiceFlag Boolean
	 * @return boolean lbProcessFlag
	 * @throws ApplicationException - if any exception occurs.
	 */
	public boolean checkParentChildRelationShip(P8UserSession aoUserSession, List<Document> aoSelectedObjectList,
			Boolean lbServiceFlag) throws ApplicationException
	{
		Boolean lbProcessFlag = true;
		HashMap<String, String> loPathMap = new HashMap<String, String>();
		try
		{
			if (lbServiceFlag)
			{
				ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
				// loPathMap =
				// contentOperationHelper.checkParentChildRelationShip(loOS,
				// aoSelectedObjectList);
				// lbProcessFlag =
				// contentOperationHelper.checkParentChildRelationShipFlag(loOS,
				// loPathMap, aoSelectedObjectList);
			}
		}
		catch (Exception aoEx)
		{
			setMoState("getDataFromFilenet");
			ApplicationException loAppex = new ApplicationException("getTotalDocumentCount", aoEx);
			LOG_OBJECT.Error("getDataFromFilenet", aoEx);
			throw loAppex;
		}
		return lbProcessFlag;
	}

	/**
	 * This method is added as a part of Release 5 for Document Vault The method
	 * will update the folder count of document bean
	 * @param aoMyBatisSession Sql session object
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoshareDocumentList as list of document bean
	 * @param aoOrgId as organisation id
	 * @return update list of document bean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public List<Document> getFolderCount(P8UserSession aoUserSession, List<Document> aoshareDocumentList, String aoOrgId)
			throws ApplicationException
	{

		HashMap<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSR5Constants.ORG_ID, aoOrgId);
		FolderMappingBean loFolderBean;
		List<Document> loList = new ArrayList<Document>();
		try
		{
			Iterator loItr = aoshareDocumentList.iterator();
			while (loItr.hasNext())
			{
				Integer loCount = 0;
				Document loListElement = (Document) loItr.next();
				if (null == loListElement.getDocType()
						|| loListElement.getDocType().equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
						|| loListElement.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{
					loMap.put(HHSR5Constants.FLDR_ID, loListElement.getDocumentId());
					ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
					//null passed as parameter in modified method 4.0.2.0
					loList = contentOperationHelper.getChildList(loOS, loListElement.getDocumentId(),
							HHSR5Constants.INSUBFOLDER, null, null);
					//null passed as parameter in modified method 4.0.2.0
					if (null != loList)
					{
						loCount = loList.size();
						loListElement.setFolderCount(loCount);
					}
				}
			}

		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getting FolderCount in foldermapping ", loExp);
			LOG_OBJECT.Error("error occured while getting FolderCount in foldermapping ", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting FolderCount in DocumentVaultFolderService ", loAppEx);
			throw new ApplicationException(
					"Exception occured while getting FolderCount in DocumentVaultFolderService ", loAppEx);
		}
		return aoshareDocumentList;
	}
	/**
	 * this method is used to establish the filenet connection as soon as the user logged in into the system.
	 * This method is added to avoid the ce connection loss while user click on document vault Tab too quick.
	 * @param aoUserSession
	 * @return P8UserSession
	 * @throws ApplicationException
	 */
	public P8UserSession getFileNetSession(P8UserSession aoUserSession)throws ApplicationException
	{
		try
		{
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured while getting FileNet Connection ", loExp);
			LOG_OBJECT.Error("Exception occured while getting FileNet Connection ", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while getting FileNet Connection ", loAppEx);
			throw new ApplicationException("Exception occured while getting FileNet Connection ", loAppEx);
		}
		return aoUserSession;
	}
	
	/** This method will set linkage of provided Document ID
	 * Added for Defect fix # 8455 Scenario 1
	 * @param aoUserSession
	 * @param aoDataMap
	 * @return
	 * @throws ApplicationException
	 */
	public Integer setDocumentLinkage(P8UserSession aoUserSession,Map<String,Boolean> aoDataMap) throws ApplicationException
	{
		ObjectStore loOS=null;
		Integer loDocCount = 0;
		try {
			if(null != aoDataMap && !aoDataMap.isEmpty() && aoDataMap.containsValue(false))
			{
				loOS = filenetConnection.getObjectStore(aoUserSession);
				
				for (Map.Entry<String, Boolean> entry : aoDataMap.entrySet())
				{
					if(entry.getValue() == false)
					{
						com.filenet.api.core.Document loDoc = Factory.Document.fetchInstance(loOS, entry.getKey(), null);
					    loDoc.getProperties().putValue(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, entry.getValue());
					    loDoc.save(RefreshMode.REFRESH);
					    loDocCount++;
					}
				    
				}
			}
			
		}
		catch (ApplicationException loExp)
		{
			loExp.addContextData("Exception occured in setDocumentLinkage ", loExp);
			LOG_OBJECT.Error("Exception occured in setDocumentLinkage ", loExp);
			throw loExp;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in setDocumentLinkage ", loAppEx);
			throw new ApplicationException("Exception occured in setDocumentLinkage ", loAppEx);
		}
		return loDocCount;
	}
	// End Release 5

	/**[Start]
	 * R9.3.2 QC9665
	 * Check the Document is belong to the Organization or Not
	 **/
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean checkPermision4Document(P8UserSession aoUserSession, String asDocId, String asOrgId , String asOrgType) throws ApplicationException
	{
		List  loProviderIdList = null;
		HashMap loHmReqExceProp = new HashMap();
		try
		{
			loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
			loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
			LOG_OBJECT.Info("Entered P8ContentService.checkPermision4Document() with Dic Id::"
					+ asDocId + "    asOrgId:" + asOrgId  );
			if (null != aoUserSession)
			{
				HashMap aoRequiredMap = new HashMap<String, Object>();
				aoRequiredMap.put(HHSR5Constants.UPPER_PROVIDER_ID, null);
				aoRequiredMap.put(HHSR5Constants.UPPER_ORGANIZATION_TYPE, null);
				aoRequiredMap.put(HHSR5Constants.UPPER_IS_SHARED, null);

				HashMap<String, HashMap> hMap = getDocVersionsDetails(aoUserSession,   asDocId, aoRequiredMap);
				
				LOG_OBJECT.Info("[Trace: checkPermision4Document]!!!!!!!!" );

				boolean isShared = false;
			    for (Map.Entry<String, HashMap> entry : hMap.entrySet()) {
			    	LOG_OBJECT.Info("Version:"+ entry.getKey() + ":" );

			        HashMap<String, Object> subMap = entry.getValue();
				    for (Map.Entry<String, Object> subEnt : subMap.entrySet()) {
				    	//LOG_OBJECT.Info(""+subEnt.getKey() + ":" + subEnt.getValue());
				    	if( aoRequiredMap.containsKey(subEnt.getKey().toString())) {
				    		aoRequiredMap.put(subEnt.getKey().toString(), subEnt.getValue().toString());
				    	}
				    }
				    //LOG_OBJECT.Info(":::::::::::::::::::::::::::::::" + aoRequiredMap);
			    }

			    if(asOrgId.equalsIgnoreCase(aoRequiredMap.get(HHSR5Constants.UPPER_PROVIDER_ID).toString())) {
			    	return true;
			    }else {
			    	if( aoRequiredMap.get(HHSR5Constants.UPPER_IS_SHARED) != null 
			    			&& aoRequiredMap.get(HHSR5Constants.UPPER_IS_SHARED).toString().equalsIgnoreCase(HHSConstants.STRING_TRUE)) {
				    	loProviderIdList = getAllAgencyOrg4Doc(aoUserSession, asDocId);
					    if(loProviderIdList != null ) {
					    	//LOG_OBJECT.Info("Provider Id(s);"+ loProviderIdList) 	;
					    	for(int loop = 0; loop <  loProviderIdList.size()      ; loop++) {
					    		String ele =  (String) loProviderIdList.get(loop);
					    		//LOG_OBJECT.Info("-->" +ele );
					    		if(ele.equalsIgnoreCase(asOrgId) ) {
					    			return true;
					    		}
					    	}
					    }
				    }
			    }
			}
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error While getting Document Content");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error While getting Document Content");
			ApplicationException loAppex = new ApplicationException("Error While getting Document Content", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error While getting Document Content", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting P8ContentService.getDocumentContent()");
		return false;
	 
	}
	
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getAllAgencyOrg4Doc(P8UserSession aoUserSession, String asDocId) throws ApplicationException
	{

		List loProviderIdList = null;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(P8Constants.OBJECT_STORE_NAME, aoUserSession.getObjectStoreName());
		loHmReqExceProp.put(P8Constants.AS_DOC_ID, asDocId);
		LOG_OBJECT.Info("Entered P8ContentService.getAllAgencyOrg4Doc() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			// Fetching FILENET objectstore from FILENET Domain
			ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
			loProviderIdList = contentOperationHelper.getProviderAgencyId(loOS, asDocId);
			filenetConnection.popSubject(aoUserSession);
			setMoState("If document is shared with any other provider,retrieving the list of providers with whom this it is shared"
					+ loProviderIdList);
		}
		catch (ApplicationException aoAppex)
		{
			setMoState("Error in document Sharing with other organizations");
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in document Sharing with other provider", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			setMoState("Error in document Sharing with other organizations");
			ApplicationException loAppex = new ApplicationException("Error in document Sharing with other provider : ",
					aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in document Sharing with other organizations", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentService.getAllAgencyOrg4Doc()");
		return loProviderIdList;
	}
	
	
	/**[End]
	 * R9.3.2 QC9665
	 * Check the Document is belong to the Organization or Not
	 **/
}