package com.nyc.hhs.service.filenetmanager.p8dataprovider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.EngineSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.collection.PageMark;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.CustomObject;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.apiimpl.query.RepositoryRowImpl;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.BulkDownloadBean;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class is added for release 5 This class is generally used to perform all
 * P8 content operations on file net this will be executed through
 * P8ContentServices.
 */

public class P8ContentOperations extends P8HelperServices
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ContentOperations.class);

	/**
	 * This method is used for creating new document. this method will firstly
	 * create new document instance, set the content, set the properties and
	 * filing document into a folder
	 * <ul>
	 * <li>Method updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param aoIS input stream
	 * @param asClassName name of the class
	 * @param asDocType string
	 * @param aoPropertyMap property that is required or is mandatory
	 * @param asFolderpath path where the file is to be stored
	 * @return String name of the document
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap<String, Object> createDocument(ObjectStore aoObjStr, FileInputStream aoIS, String asClassName,
			String asDocType, HashMap aoPropertyMap, String asFolderpath) throws ApplicationException
	{
		Document loNewDoc = null;
		// Adding Boolean initialization for add to dictionary issue in Emergency Release 6.0.1
		Boolean lbLinkageFlag = false;
		HashMap loHmReqExceProp = new HashMap();
		String lsParentPathName = null;
		Folder loFlder = null;
		// Added for Release 5
		HashMap<String, Object> loMappingMap = new HashMap<String, Object>();
		List<FolderMappingBean> loMappingBean = new ArrayList<FolderMappingBean>();
		String lsCustomFolderId = (String) aoPropertyMap.get(HHSR5Constants.CUSTOM_FLDR_ID);
		// End Release 5
		String lsDocumentName = (String) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE);
		String lsMimeType = (String) aoPropertyMap.get(P8Constants.MIME_TYPE);
		String lsRetrievalName = (String) aoPropertyMap.get(HHSR5Constants.FILE_NAME_PARAMETER);
		loHmReqExceProp.put(HHSR5Constants.LS_DOCUMENT_NAME, lsDocumentName);
		loHmReqExceProp.put(HHSR5Constants.LS_MIME_TYPE, lsMimeType);
		loHmReqExceProp.put(HHSR5Constants.AS_FOLDER_PATH, asFolderpath);
		loHmReqExceProp.put(HHSR5Constants.AS_CLASS_NAME, asClassName);
		loHmReqExceProp.put(HHSConstants.AS_DOC_TYPE, asDocType);
		loHmReqExceProp.put(HHSConstants.BULK_UPLOAD_FILE_PROPS, aoPropertyMap);
		LOG_OBJECT.Info("Entered P8ContentOperations.createDocument() with parameters::" + loHmReqExceProp.toString());
		String lsOriginalDocumentType = null;
		Boolean lbCustomFileFlag = true;
		String lsSharingFlagForParent = null;
		try
		{
			// Fetching required parameters from property maps
			if (lsDocumentName.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
					|| lsMimeType.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
					|| asFolderpath.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
			{
				ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.FILE_UPLOAD_FAIL_MESSAGE));
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
			// Creating new instance of FileNet Document Objects
			loNewDoc = Factory.Document.createInstance(aoObjStr, asClassName);
			if (loNewDoc == null)
			{
				ApplicationException loAppex = new ApplicationException(PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.FILE_UPLOAD_FAIL_MESSAGE));
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
			// Setting Content of Document Objects
			loNewDoc = setDocContent(loNewDoc, aoIS, lsMimeType, lsRetrievalName);
			if (null != lsCustomFolderId && !lsCustomFolderId.isEmpty()
					&& !lsCustomFolderId.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID)
					&& !lsCustomFolderId.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
			{
				if (checkFolderExistsById(aoObjStr, lsCustomFolderId))
				{
					loFlder = Factory.Folder.fetchInstance(aoObjStr, lsCustomFolderId, null);
					lsParentPathName = loFlder.get_PathName();
				}
				else
				{
					String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.PARENT_FOLDER_NOT_EXISTS);
					throw new ApplicationException(lsMessage);
				}
			}
			else if (null != lsCustomFolderId
					&& (lsCustomFolderId.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID) || lsCustomFolderId
							.isEmpty()) || asDocType.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_BAFO_DOCUMENT))
			{
				lsParentPathName = FileNetOperationsUtils.setFolderPath(
						(String) aoPropertyMap.get(HHSR5Constants.ORGANIZATION_ID_KEY),
						(String) aoPropertyMap.get(HHSR5Constants.Org_Id), HHSR5Constants.DOCUMENT_VAULT);
				if (!checkFolderExists(aoObjStr, lsParentPathName))
				{
					loFlder = getFolderByName(aoObjStr, lsParentPathName, HHSR5Constants.HHS_CUSTOM_FOLDER,
							loMappingBean, (String) aoPropertyMap.get(HHSR5Constants.Org_Id));
					String lsRecyclePath = FileNetOperationsUtils.setFolderPath(
							(String) aoPropertyMap.get(HHSR5Constants.ORGANIZATION_ID_KEY),
							(String) aoPropertyMap.get(HHSR5Constants.Org_Id), HHSR5Constants.RECYCLE_BIN);
					getFolderByName(aoObjStr, lsRecyclePath, HHSR5Constants.HHS_CUSTOM_FOLDER, null,
							(String) aoPropertyMap.get(HHSR5Constants.Org_Id));

				}
				else
				{
					loFlder = Factory.Folder.fetchInstance(aoObjStr, lsParentPathName, null);
				}
			}
			// Setting Document Properties
			aoPropertyMap.remove(HHSR5Constants.Org_Id);
			// Adding null check for add to dictionary issue in Emergency Release 6.0.1
			if(null != aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION))
			{
				lbLinkageFlag = (Boolean) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION);
			}
			// End Changes
			setDocProperties(aoObjStr, loNewDoc, aoPropertyMap, asClassName, asDocType, lsParentPathName, lbLinkageFlag);
			loNewDoc.save(RefreshMode.REFRESH);
			// Added for defect 7409
			if (null != lsParentPathName)
			{
				setParentModifiedDateProperty(aoObjStr, lsParentPathName);
			}
			// Added for defect 7409
			String lsMaxDocCountKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + HHSConstants.UNDERSCORE
					+ P8Constants.DOCUMENT_VAULT_RESTRICTED_DOCTYPE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsRestrictedDocType = loApplicationSettingMap.get(lsMaxDocCountKey);
			// Code fix for 6918
			lbCustomFileFlag = checkFilingToCustomRequired(asDocType, lbCustomFileFlag, lsRestrictedDocType);
			if (lbCustomFileFlag)
			{
				fileDocToCustomFolder(aoObjStr, loNewDoc, lsCustomFolderId, asClassName, aoPropertyMap, asDocType,
						loFlder);
				loMappingMap.put(HHSR5Constants.PARENT_SHARING_FLAG,
						loFlder.getProperties().getStringValue(HHSR5Constants.SHARING_FLAG));
			}
			// Setting Properties of Document Objects
			// Saving documents into folder
			fileDocToFolder(aoObjStr, loNewDoc, asFolderpath, asClassName);
		}
		catch (EngineRuntimeException aoEc)
		{
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.FILE_UPLOAD_FAIL_MESSAGE);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEc);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDocument()::", aoEc);
			throw loAppex;
		}
		catch (ApplicationException aoEx)
		{
			String lsMessage = aoEx.getMessage();
			if (lsMessage.startsWith("Runtime Error in Fetching Filenet"))
			{
				lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
						HHSR5Constants.FILE_UPLOAD_FAIL_MESSAGE);
				throw new ApplicationException(lsMessage, aoEx);
			}
			aoEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Runtime Error in Fetching Filenet", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.FILE_UPLOAD_FAIL_MESSAGE);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.createDocument()::", aoEx);
			throw loAppex;
		}

		if (null != loMappingBean && !loMappingBean.isEmpty())
		{
			loMappingBean.get(0).setDocumentId(loNewDoc.get_Id().toString());
		}
		loMappingMap.put(HHSR5Constants.DOC_ID, loNewDoc.get_Id().toString());
		loMappingMap.put(HHSR5Constants.FILED_TO_CUSTOM_FOLDER, lbCustomFileFlag);
		loMappingMap.put(HHSR5Constants.FOLDER_MAPPING_BEAN_ID, loMappingBean);
		LOG_OBJECT.Info("Exited P8ContentOperations.createDocument().");
		return loMappingMap;
	}

	/**
	 * @param asDocType
	 * @param lbCustomFileFlag
	 * @param lsRestrictedDocType
	 * @return
	 */
	private Boolean checkFilingToCustomRequired(String asDocType, Boolean lbCustomFileFlag, String lsRestrictedDocType)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.checkFilingToCustomRequired().");
			if (null != asDocType && !asDocType.isEmpty())
			{
				if (null != lsRestrictedDocType && lsRestrictedDocType.contains(HHSR5Constants.COMMA))
				{
					String[] loCustomArray = lsRestrictedDocType.split(HHSR5Constants.COMMA);
					for (int i = 0; i < loCustomArray.length; i++)
					{
						if (loCustomArray[i].equalsIgnoreCase(asDocType))
						{
							lbCustomFileFlag = false;
							break;
						}
					}
				}
				else
				{
					if (lsRestrictedDocType.equalsIgnoreCase(asDocType))
					{
						lbCustomFileFlag = false;
					}
				}
				return lbCustomFileFlag;
			}
			else
			{
				lbCustomFileFlag = false;
			}
		}
		catch (Exception appex)
		{
			throw new ApplicationException("Internal Error Occured While Processing Your Request", appex);
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.checkFilingToCustomRequired().");
		return lbCustomFileFlag;

	}

	// Start of change for defect 6218

	// createNewDocVersion method deleted

	// End of change for defect 6218

	/**
	 * This method is internally used for setting document content and document
	 * mime type
	 * 
	 * @param aoDoc the document whose contents are to be set
	 * @param aoIS input stream
	 * @param asMimeType specifies the MIME type of the document
	 * @param asDocName string
	 * @return Document aoDoc Document bean object containing required
	 *         properties
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Document setDocContent(Document aoDoc, FileInputStream aoIS, String asMimeType, String asDocName)
			throws ApplicationException
	{
		HashMap loReqExceProp = new HashMap();
		loReqExceProp.put(HHSR5Constants.AS_MIME_TYPE, asMimeType);
		loReqExceProp.put(HHSR5Constants.AO_DOC, aoDoc);
		LOG_OBJECT.Info("Entered P8ContentOperations.setDocContent() with parameters::" + loReqExceProp.toString());

		if (aoDoc == null || asMimeType == null || asMimeType.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setDocContent Method. Required Fields are missing");
			loAppex.setContextData(loReqExceProp);
			throw loAppex;
		}
		try
		{
			// Creating ContentElementList object for setting content.
			ContentElementList loCEL = Factory.ContentElement.createList();
			ContentTransfer loCT = Factory.ContentTransfer.createInstance();
			loCT.setCaptureSource(aoIS);
			loCT.set_RetrievalName(asDocName);
			// Setting MimeType of the content which will help in viewing
			// document
			loCT.set_ContentType(asMimeType);
			loCEL.add(loCT);
			aoDoc.set_ContentElements(loCEL);

			// Check-in document back into FileNet Repository
			aoDoc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

			LOG_OBJECT.Info("Exited P8ContentOperations.setDocContent()");
			return aoDoc;

		}
		catch (EngineRuntimeException aoEc)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setDocContent Method. Required Fields are missing", aoEc);
			loAppex.setContextData(loReqExceProp);
			LOG_OBJECT.Error("Error in setDocContent Method. Required Fields are missing", aoEc);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setDocContent Method. Required Fields are missing", aoEx);
			loAppex.setContextData(loReqExceProp);
			LOG_OBJECT.Error("Error in setDocContent Method. Required Fields are missing", aoEx);
			throw loAppex;
		}

	}

	/**
	 * This method creates filenet property filter object from filter hashmap.
	 * 
	 * @param aoRequiredProps the filter map containing all the property
	 * @return PropertyFilter loPF filenet property filter
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public PropertyFilter createPropertyFilter(HashMap aoRequiredProps) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered P8ContentOperations.createPropertyFilter()");

		// Define property filter
		PropertyFilter loPF = new PropertyFilter();

		try
		{
			if (aoRequiredProps == null)
			{
				// Creating propertyFilter for fetching properties associated
				// with document
				loPF.addIncludeType(0, null, Boolean.TRUE, FilteredPropertyType.ANY_SINGLETON, null);
			}
			else
			{
				Set loPropKeySet = aoRequiredProps.keySet();
				Iterator loIt = loPropKeySet.iterator();
				while (loIt.hasNext())
				{
					String lsPropName = (String) loIt.next();
					// Define filter element
					FilterElement loFE = new FilterElement(null, null, null, lsPropName, null);
					loPF.addIncludeProperty(loFE);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in createPropertyFilter Method", aoEx);
			loAppex.setContextData(aoRequiredProps);
			LOG_OBJECT.Error("Error in createPropertyFilter Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.createPropertyFilter()");
		return loPF;
	}

	/**
	 * Changed in 3.1.0 . Added check for Enhancement 6021 - char500 - extension
	 * This method is internally used for setting the document properties. It
	 * will fetch the property values from the hash map and set to the document
	 * property
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param aoDoc the document whose contents are to be set
	 * @param aoPropertyMap property that is required or is mandatory
	 * @param asDocClass the document class
	 * @param asDocType type of the document
	 * @return Document object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Document setDocProperties(ObjectStore aoObjStr, Document aoDoc, HashMap aoPropertyMap, String asDocClass,
			String asDocType, String asFolderPath, boolean lbLinkage) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.AS_DOC_CLASS, asDocClass);
		loHmReqExceProp.put(HHSR5Constants.AS_DOC_TYPE, asDocType);
		LOG_OBJECT
				.Info("Entered P8ContentOperations.setDocProperties() with parameters::" + loHmReqExceProp.toString());
		try
		{
			// Added for Linkage Icon
			aoDoc.getProperties().putValue(HHSR5Constants.PARENT_PATH, asFolderPath);
			if (null != aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION))
			{
				Boolean lbIsLinkToApp = (Boolean) aoPropertyMap.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION);
				if (null != asFolderPath && !asFolderPath.isEmpty())
				{
					resetFolderLinkAndShareStatus(aoObjStr, asFolderPath,
							P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, String.valueOf(lbIsLinkToApp), lbLinkage);
				}

			}
			// End

			if (asDocClass.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING) || aoDoc == null)
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in setDocProperties Method. Required Parameter are missing");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
			// Release 5- Removed Date validation steps from below, added in
			// ULPOAD(step 2) in FileNetOperationsUtil.java
			// Release 5- Sending True in abIsValidDate below because date
			// validation step has been removed from here.
			aoDoc = checkDocumentProperties(aoDoc, aoObjStr, aoPropertyMap, asDocClass, true);
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in setDocProperties Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in setDocProperties Method", aoEx);
			throw loAppex;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDocProperties()::", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in setDocProperties Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in setDocProperties Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.setDocProperties()");

		return aoDoc;
	}

	/**
	 * This method is used for checking the if the document properties exist in
	 * aoPropertyMap and calling the method setProperty() to update the
	 * properties
	 * 
	 * @param aoDoc A Document object whose properties have to be checked and
	 *            updated
	 * @param aoObjStr An ObjectStore object
	 * @param aoPropertyMap An hashmap containing the properties to be set
	 * @param asDocClass A sting containing the document class name
	 * @param abIsValidDate A boolean indicating whether the date is valid
	 * @return The Document object with the properties set
	 * @throws ApplicationException
	 */

	private Document checkDocumentProperties(Document aoDoc, ObjectStore aoObjStr, HashMap aoPropertyMap,
			String asDocClass, boolean abIsValidDate) throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.AS_DOC_CLASS, asDocClass);
		loHmReqExceProp.put(HHSR5Constants.BULK_UPLOAD_FILE_PROPS, aoPropertyMap);
		loHmReqExceProp.put(HHSR5Constants.AB_IS_VALID_DATE, abIsValidDate);
		LOG_OBJECT.Info("Entered P8ContentOperations.checkDocumentProperties() with parameters::"
				+ loHmReqExceProp.toString());

		try
		{
			// Fetch selected class description from the server
			ClassDescription loClassDesc = Factory.ClassDescription.fetchInstance(aoObjStr, asDocClass, null);
			PropertyDescriptionList loPropDescsList = loClassDesc.get_PropertyDescriptions();

			Properties loProps = aoDoc.getProperties();

			// Get PropertyDescriptions property from the property cache
			Iterator loPDescIt = loPropDescsList.iterator();
			PropertyDescription loPropDesc = null;

			// Loop until property description found
			while (loPDescIt.hasNext())
			{
				loPropDesc = (PropertyDescription) loPDescIt.next();

				// Get SymbolicName property from the property cache
				String lsPropDescName = loPropDesc.get_SymbolicName();
				String lsPropDisplayName = loPropDesc.get_DisplayName();
				if (aoPropertyMap.containsKey(lsPropDescName) && abIsValidDate)
				{
					loHmReqExceProp.put(HHSR5Constants.SETTING_PROPERTY, lsPropDescName);
					TypeID loDataType = loPropDesc.get_DataType();
					// Adding null check in Release 5
					if (null != aoPropertyMap.get(lsPropDescName))
					{
						String lsPropVal = aoPropertyMap.get(lsPropDescName).toString();
						if (null == lsPropVal || lsPropVal.trim().equals(HHSConstants.EMPTY_STRING))
						{
							ApplicationException loAppex = new ApplicationException("Please Fill " + lsPropDisplayName
									+ " as it is a required field for this type of document");
							throw loAppex;

						}
						loProps = setProperty(loProps, loDataType, lsPropDescName, lsPropVal);
					}

				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkDocumentProperties()::", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Exception Occured while setting Document Metadata", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception Occured while setting Document Metadata", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.checkDocumentProperties()");
		return aoDoc;

	}

	/**
	 * This Method is for setting property value to Content Engine Property
	 * Object based on property type.
	 * 
	 * @param aoProps the properties needed to be set
	 * @param aoType the type id of the specified property
	 * @param asPropName the name of the property
	 * @param asPropVal the value of the property
	 * @return Properties object which are being set
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Properties setProperty(Properties aoProps, TypeID aoType, String asPropName, String asPropVal)
			throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.AS_PROP_NAME, asPropName);
		loHmReqExceProp.put(HHSR5Constants.AS_PROP_VAL, asPropVal);
		LOG_OBJECT.Info("Entered P8ContentOperations.setProperty() with parameters::" + loHmReqExceProp.toString());
		//Updated in release 4.0.1- for removing mismatch in modified date
		SimpleDateFormat loDateFormat = null;
		try
		{
			// for type cast values of properties
			if (aoType == TypeID.DATE)
			{
				if(null != asPropName && asPropName.equalsIgnoreCase(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE))
					loDateFormat = new SimpleDateFormat(HHSR5Constants.NFCTH_TIMESTAMP_FORMAT);
				else
					loDateFormat = new SimpleDateFormat(P8Constants.DATE_FORMAT);
				Date loConvertedDate = loDateFormat.parse(asPropVal);
				aoProps.putValue(asPropName, loConvertedDate);
			}
			//Updated in release 4.0.1- for removing mismatch in modified date end
			else if (aoType == TypeID.STRING)
			{
				aoProps.putValue(asPropName, asPropVal);
			}
			else if (aoType == TypeID.DOUBLE)
			{
				aoProps.putValue(asPropName, Double.parseDouble(asPropVal));
			}
			else if (aoType == TypeID.LONG)
			{
				aoProps.putValue(asPropName, Integer.parseInt(asPropVal));
			}
			else if (aoType == TypeID.BOOLEAN)
			{
				aoProps.putValue(asPropName, Boolean.valueOf(asPropVal));
			}
			else
			{
				aoProps.putValue(asPropName, asPropVal);
			}
		}
		catch (ParseException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While parsing " + asPropName, aoEx);
			loAppex.setContextData(loHmReqExceProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error While parsing", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setting property value to Content Engine Property Object", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error in setting property value to Content Engine Property Object", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.setProperty() ");
		return aoProps;
	}

	/**
	 * This Method is for filing document into the folder. Based on the folder
	 * path, it will file the document into the folder
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param aoDoc the document whose contents are to be set
	 * @param asFolderPath the path of the folder where the file is to be stored
	 * @param asDocClassName the name of the document class
	 * @throws ApplicationException
	 */

	private void fileDocToFolder(ObjectStore aoObjStr, Document aoDoc, String asFolderPath, String asDocClassName)
			throws ApplicationException
	{
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		loHmReqExceProp.put(HHSR5Constants.AS_FOLDERS_PATH, asFolderPath);
		loHmReqExceProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		LOG_OBJECT.Info("Entered P8ContentOperations.fileDocToFolder() with parameters::" + loHmReqExceProp.toString());
		try
		{

			if (asFolderPath == null || asFolderPath.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					|| asDocClassName == null || asDocClassName.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in fileDocToFolder Method. Required Parameters are missing.");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
			// getting folder object from FileNet Repository
			// Adding null as last parameter in method getFolderByName for
			// Release 5
			Folder loFldr = getFolderByName(aoObjStr, asFolderPath, null, null, null);
			if (loFldr == null)
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in fileDocToFolder Method. Not able to fetch required folder");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}

			// Setting ReferentialContainment relationship in between folder and
			// documents
			ReferentialContainmentRelationship loRCR = loFldr.file(aoDoc, AutoUniqueName.AUTO_UNIQUE, aoDoc.get_Id()
					.toString(), DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
			LOG_OBJECT.Info("Entered ReferentialContainmentRelationship.save() with parameters:"
					+ loHmReqExceProp.toString());
			loRCR.save(RefreshMode.NO_REFRESH);
			LOG_OBJECT.Info("Entered ReferentialContainmentRelationship.save() with parameters:"
					+ loHmReqExceProp.toString());
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fileDocToFolder()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in fileDocToFolder Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in fileDocToFolder Method::", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in fileDocToFolder Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in fileDocToFolder Method::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.fileDocToFolder() ");
	}

	/**
	 * This Method is for checking whether any specific folder is exist in the
	 * FILENET or not
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param asPath the path of the folder where the file is to be stored
	 * @return boolean status flag
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean checkFolderExists(ObjectStore aoObjectStore, String asPath) throws ApplicationException
	{
		boolean lbFlag = false;
		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put(HHSR5Constants.AS_PATH, asPath);
		LOG_OBJECT.Debug("Entered P8ContentOperations.checkFolderExists() with parameters::"
				+ loHmRequiredProp.toString());
		SearchSQL loSqlObject = new SearchSQL();

		try
		{
			if (asPath == null || asPath.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in checkFolderExists Method.Required Parameters are missing.");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}
			String[] bits = asPath.split(HHSConstants.FORWARD_SLASH);
			String lsFolderName = bits[bits.length - 1];
			String lsParentFolderPath = asPath.substring(0, asPath.lastIndexOf(HHSConstants.FORWARD_SLASH));
			String lsSQLQuery;
			if (asPath.contains(HHSR5Constants.DOCUMENT_VAULT))
			{
				if (lsParentFolderPath.isEmpty())
				{
					lsSQLQuery = "Select This,id From HHS_CUSTOM_FOLDER where Parent = Object('/') AND FolderName='"
							+ lsFolderName + "' AND DELETE_FLAG = 0";
				}
				else
				{
					lsSQLQuery = "Select This,id From HHS_CUSTOM_FOLDER where This INFOLDER '" + lsParentFolderPath
							+ "' AND FolderName='" + lsFolderName + "' AND DELETE_FLAG = 0";
				}
			}
			else if (asPath.contains(HHSR5Constants.RECYCLE_BIN))
			{

				if (lsParentFolderPath.isEmpty())
				{
					lsSQLQuery = "Select This,id From HHS_CUSTOM_FOLDER where Parent = Object('/') AND FolderName='"
							+ lsFolderName + "' AND (DELETE_FLAG = 2 OR DELETE_FLAG = 1)";
				}
				else
				{
					lsSQLQuery = "Select This,id From HHS_CUSTOM_FOLDER where This INFOLDER '" + lsParentFolderPath
							+ "' AND FolderName='" + lsFolderName + "'";
				}

			}
			else
			{
				if (lsParentFolderPath.isEmpty())
				{
					lsSQLQuery = "Select This,id From Folder where Parent = Object('/') AND FolderName='"
							+ lsFolderName + "'";
				}
				else
				{
					lsSQLQuery = "Select This,id From Folder where This INSUBFOLDER '" + lsParentFolderPath
							+ "' AND FolderName='" + lsFolderName + "'";
				}
			}
			// Checking whether folder instance exists or not
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjectStore);
			IndependentObjectSet loRowSet = (IndependentObjectSet) loSearchScope.fetchObjects(loSqlObject, null, null,
					Boolean.FALSE);
			if (null != loRowSet && loRowSet.iterator().hasNext())
			{
				lbFlag = true;
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkFolderExists()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEc)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkFolderExists()::", aoEc);
			return lbFlag;

		}
		catch (Exception aoEx)
		{
			ApplicationException loExp = new ApplicationException(
					"Error in checkFolderExists Method.Required Parameters are missing.", aoEx);
			loExp.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Error in checkFolderExists Method.Required Parameters are missing", aoEx);
			throw loExp;
		}

		LOG_OBJECT.Debug("Exited P8ContentOperations.checkFolderExists() " + lbFlag);
		return lbFlag;
	}

	/**
	 * This Method is used for fetching folder from the FileNet Repository. If
	 * Folder does not exist, it will create the folder.
	 * <ul>
	 * <li>Method updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param asPath the path of the folder where the file is to be stored
	 * @return Folder is the P8 folder object
	 * @throws ApplicationException
	 */
	// Added one parameter aoTempList for Release 5
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Folder getFolderByName(ObjectStore aoObjectStore, String asPath, String asFolderClassType,
			List<FolderMappingBean> aoMappingBeanList, String asOrgId) throws ApplicationException
	{
		Folder loFldr = null;

		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put(HHSR5Constants.AS_PATH, asPath);
		LOG_OBJECT
				.Info("Entered P8ContentOperations.getFolderByName() with parameters::" + loHmRequiredProp.toString());

		try
		{
			if (asPath.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in getFolderByName Method.Required Parameters are missing.");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}

			// checking whether folder exists or not. if no then creating new
			// folder instance
			if (checkFolderExists(aoObjectStore, asPath))
			{

				// if folder path exist, then returning the existing folder
				loFldr = Factory.Folder.fetchInstance(aoObjectStore, asPath, null);
			}
			else
			{
				// if folder path doesn't exist then creating new folder. sample
				// creating folder path array to check whether all parents are
				// exist or not.
				String[] loSubFolders = asPath.split(P8Constants.STRING_SINGLE_SLASH);

				int liLength = 1;
				String lsParentFolderPath = HHSConstants.EMPTY_STRING;
				String lsChildFolderPath = HHSConstants.EMPTY_STRING;

				// doing looping to check whether all parents folder are exist
				// or not.
				while (liLength < loSubFolders.length)
				{
					Boolean lbClassFlag = true;
					lsChildFolderPath = lsParentFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(
							loSubFolders[liLength]);
					FolderMappingBean loFolderBeanChild = new FolderMappingBean();
					// checking child folder exist or not. if no then creating
					// new folder
					if (!checkFolderExists(aoObjectStore, lsChildFolderPath))
					{
						Folder loParent;
						if (lsParentFolderPath.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
						{
							loParent = aoObjectStore.get_RootFolder();
						}
						else
						{
							loParent = Factory.Folder.fetchInstance(aoObjectStore, lsParentFolderPath, null);
						}
						// Added if condition for Release 5
						if (loParent.get_PathName().equalsIgnoreCase(HHSR5Constants.SLASH_PROVIDER)
								|| loParent.get_PathName().equalsIgnoreCase(HHSR5Constants.SLASH_AGENCY))
						{
							lbClassFlag = false;
						}
						if (null != asFolderClassType && !asFolderClassType.isEmpty() && lbClassFlag)
						{
							loFldr = Factory.Folder.createInstance(aoObjectStore, asFolderClassType);
							Properties loProp = loFldr.getProperties();
							loProp.putValue(P8Constants.PROPERTY_CE_PROVIDER_ID, asOrgId);
							loProp.putValue(HHSR5Constants.IS_FOLDER_SHARED, ApplicationConstants.ZERO);
							loProp.putValue(HHSR5Constants.SHARING_FLAG, ApplicationConstants.ZERO);
							loProp.putValue(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
									HHSUtil.getCurrentTimestampDate());
							loFldr.set_Parent(loParent);
							loFldr.set_FolderName(loSubFolders[liLength]);
							// Changing No_REFRESH to REFRESH in Release 5
							loFldr.save(RefreshMode.REFRESH);

							ReferentialContainmentRelationship loRCR = loParent.file(loFldr,
									AutoUniqueName.AUTO_UNIQUE, loFldr.get_Id().toString(),
									DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
							loRCR.save(RefreshMode.REFRESH);

						}
						else
						{
							loFldr = Factory.Folder.createInstance(aoObjectStore, ClassNames.FOLDER);
							loFldr.set_Parent(loParent);
							loFldr.set_FolderName(loSubFolders[liLength]);

							// Changing No_REFRESH to REFRESH in Release 5
							loFldr.save(RefreshMode.REFRESH);
						}

						if (null != asFolderClassType && !asFolderClassType.isEmpty() && lbClassFlag)
						{

							// FolderMappingBean
							loFolderBeanChild.setFolderFilenetId(loFldr.get_Id().toString());
							loFolderBeanChild.setDocumentCount(HHSR5Constants.INT_ZERO);
							loFolderBeanChild.setOrganizationId(asOrgId);
							loFolderBeanChild.setFolderName(loFldr.get_FolderName());
							if (loFldr.get_FolderName().equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT))
								loFolderBeanChild.setParentFolderFilenetId(HHSR5Constants.DELIMITER_SINGLE_HASH);
							else
								loFolderBeanChild.setParentFolderFilenetId(loParent.get_Id().toString());
							loFolderBeanChild.setFolderCount(HHSR5Constants.INT_ZERO);
							loFolderBeanChild.setSharedFlag(Integer.toString(HHSR5Constants.INT_ZERO));
							loFolderBeanChild.setAttachmentFlag(Integer.toString(HHSR5Constants.INT_ZERO));
							loFolderBeanChild.setType(Integer.toString(HHSR5Constants.INT_ONE));
							loFolderBeanChild.setMovedToRecycleBin(Integer.toString(HHSR5Constants.INT_ZERO));
							if (null != aoMappingBeanList)
							{
								aoMappingBeanList.add(loFolderBeanChild);
							}
						}

					}

					// setting parent folder path value to child folder path
					// value so that check all levels of folder structure
					lsParentFolderPath = lsChildFolderPath;
					liLength++;
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getFolderByName()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in getFolderByName Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in getFolderByName Method", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception while in getFolderByName Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception while in getFolderByName Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getFolderByName()");
		return loFldr;
	}

	/**
	 * This Method is used for fetching Document GUID from the FileNET
	 * Repository for specific document based on Document Title
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asDocClassName the name of the document class
	 * @param asFolderPath the path of the folder where document is stored
	 * @param asDocTitle title of the document
	 * @return String id of the document
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String getDocumentId(ObjectStore aoObjStore, String asDocClassName, String asFolderPath, String asDocTitle,
			String asDocType) throws ApplicationException
	{
		String lsDocId = null;
		SearchSQL loSqlObject = new SearchSQL();
		HashMap loHmRequiredProp = new HashMap();

		loHmRequiredProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmRequiredProp.put(HHSR5Constants.AS_FOLDERS_PATH, asFolderPath);
		loHmRequiredProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentId() with parameters::" + loHmRequiredProp.toString());

		String lsSQLQuery = HHSConstants.EMPTY_STRING;
		if (asDocClassName == null || asDocClassName.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
				|| asFolderPath == null || asFolderPath.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocumentId method. Required Parameters are missing.");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;
		}
		// Creating SQL query for fetching documents.
		if (asDocClassName.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_CLASS_SYSTEM_TERMS_CONDITIONS)
				|| asDocClassName.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_CLASS_APPLICATION_TERMS_CONDITIONS)
				|| asDocClassName.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_CLASS_APPENDIX_A)
				|| asDocClassName.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_CLASS_STANDARD_CONTRACT))
		{
			lsSQLQuery = "SELECT doc.Id FROM " + asDocClassName + " doc WHERE doc.This INFOLDER '" + asFolderPath
					+ "' AND doc.DELETE_FLAG = 0";
		}
		else
		{
			lsSQLQuery = "SELECT doc.Id FROM " + asDocClassName + " doc WHERE  doc.DocumentTitle = '" + asDocTitle
					+ "' and doc.This INFOLDER '" + asFolderPath + "' AND doc.DELETE_FLAG = 0";
		}

		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loRowSet.iterator();

			// Iterating rows retrieved from the query
			if (loIt.hasNext())
			{
				Document loDoc = (Document) loIt.next();
				lsDocId = loDoc.getProperties().getIdValue(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString();
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while fetching documents from folder "
					+ asFolderPath, aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Error while fetching documents from folder", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentId() ");
		return lsDocId;
	}

	/**
	 * This Method is used for fetching list of Provider Id with whom one
	 * specific document is shared
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asDocId id of the document
	 * @return List provider id list
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List getProviderId(ObjectStore aoObjStore, String asDocId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.getProviderId() with asDocId::" + asDocId);

		ArrayList<String> loArrProviderId = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		String lsDocType = null;
		Properties loProp = null;

		if (null != asDocId && !asDocId.isEmpty() && asDocId.contains(HHSConstants.COMMA))
		{
			String[] loTemp = asDocId.split(HHSConstants.COMMA);
			asDocId = loTemp[0];
			lsDocType = loTemp[1];
		}
		if (asDocId == null || asDocId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getProviderId Method. Required Parameters are missing");
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			throw loAppex;
		}
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id

		if (null != asDocId & !asDocId.isEmpty())
		{
			PropertyFilter loPF = new PropertyFilter();
			FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.SHARED_ENTITY_ID, null);
			loPF.addIncludeProperty(loFE);

			if (null != lsDocType && !lsDocType.isEmpty() && !lsDocType.equalsIgnoreCase(HHSConstants.EMPTY_STRING)
					&& !lsDocType.equalsIgnoreCase(HHSConstants.NULL))
			{
				Document loDoc = Factory.Document.fetchInstance(aoObjStore, asDocId, loPF);
				loProp = loDoc.getProperties();
			}
			else
			{
				Folder loFldr = Factory.Folder.fetchInstance(aoObjStore, asDocId, loPF);
				loProp = loFldr.getProperties();
			}

			if (null != loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
					&& !loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString().isEmpty())
			{
				asDocId = loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString();
			}
		}

		String lsSQLQuery = "SELECT " + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
				+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " FROM "
				+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
				+ " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + " = '" + asDocId + "' Order By "
				+ P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + "," + P8Constants.PROPERTY_CE_SHARED_AGENCY_ID;
		// }
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);

			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);

			// Iterating the result set
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{

				CustomObject loObj = (CustomObject) loIt.next();
				String lsProviderId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID);
				String lsAgencyId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID);
				// adding provider id to array list
				if (null != lsProviderId)
				{
					loArrProviderId.add("PROVIDER:" + lsProviderId);
				}
				else if (null != lsAgencyId)
				{
					loArrProviderId.add("AGENCY:" + lsAgencyId);
				}
			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching provider id for a document :", aoEx);
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			LOG_OBJECT.Error("Error fetching provider id for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getProviderId()");
		return loArrProviderId;
	}

	/**
	 * This Method is used for removing document sharing with provider. This
	 * method will accept a hash map which has key as document id and value as
	 * list of provider id
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param aoShareDocuments the hash map containing the shared documents
	 * @param asAction a string which has value of action	
	 * @param aoShareDocuments the hash map containing organization type and id
	 * @return boolean variable signifying the status whether or not the
	 *         document sharing is removed
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean removeDocumentsSharing(ObjectStore aoObjStore, HashMap aoShareDocuments, String asAction) throws ApplicationException
	{
		boolean lbSharingRemoved = true;
		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put("aoShareDocuments", aoShareDocuments);
		LOG_OBJECT.Info("Entered P8ContentOperations.removeDocumentsSharing() with parameters::"
				+ loHmRequiredProp.toString());

		String lsCustomObjectClassName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_SHARED_DOC_PROVIDER_CUSTOM_OBJECT);
		if (lsCustomObjectClassName == null || lsCustomObjectClassName.equalsIgnoreCase(""))
		{
			lbSharingRemoved = false;
			ApplicationException loAppex = new ApplicationException(
					"Error in removeDocumentsSharing method. Required Parameters SHARED_DOC_PROVIDER_CUSTOM_OBJECT is missing.");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;
		}
		String lsDocumentId = null;
		String lsKeyData = null;
		String lsDocType = null;
		String lsFolderDocId = null;
		String lsFolderId = null;
		List loUnshareProviderList = null;
		String lsSQLQuery = null;
		int liStartIndex = 0;
		int liLastIndex = 0;
		List<String> lsDocIdList = new ArrayList<String>();
		List<String> lsFolderIdList = new ArrayList<String>();
		List<String> lsFolderDocIdList = new ArrayList<String>();
		try
		{
			// Iterating the HashMap for fetching document and provide id
			// details/
			Iterator loKeyIt = aoShareDocuments.keySet().iterator();
			SearchSQL loSqlObject = new SearchSQL();
			while (loKeyIt.hasNext())
			{
				lsKeyData = (String) loKeyIt.next();
				if (!lsKeyData.equalsIgnoreCase(HHSR5Constants.LS_MESSAGE_LIST))
				{
					if (lsKeyData.contains(HHSConstants.COMMA))
					{
						String[] loTemp = lsKeyData.split(HHSConstants.COMMA);
						lsDocumentId = loTemp[0];
						lsDocType = loTemp[1];
						if (null != aoShareDocuments.get(lsKeyData))
						{
							if (!(aoShareDocuments.get(lsKeyData) instanceof String))
								loUnshareProviderList = (List) aoShareDocuments.get(lsKeyData);
						}

					}
					else
					{
						lsDocumentId = lsKeyData;
						lsDocType = (String) aoShareDocuments.get(lsKeyData);
					}
					if (null != lsDocType && lsDocType.equalsIgnoreCase(HHSR5Constants.FOLDER))
					{
						lsFolderIdList.add(lsDocumentId);
					}
					else
					{
						lsDocIdList.add(lsDocumentId);
					}
				}
			}
			lsFolderDocIdList.addAll(lsDocIdList);
			lsFolderDocIdList.addAll(lsFolderIdList);

			lsDocumentId = StringUtils.join(lsDocIdList, StringEscapeUtils.escapeHtml("','"));
			lsFolderId = StringUtils.join(lsFolderIdList, StringEscapeUtils.escapeHtml("','"));
			lsFolderDocId = StringUtils.join(lsFolderDocIdList, StringEscapeUtils.escapeHtml("','"));

			if (null != loUnshareProviderList && !loUnshareProviderList.isEmpty())
			{
				// Creating sql for fetching custom object based on
				// document
				// id
				// and provider id
				String lsProviderId = StringUtils.join(loUnshareProviderList, StringEscapeUtils.escapeHtml("','"));
				lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
						+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " FROM " + lsCustomObjectClassName + " WHERE "
						+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " in ( '" + lsFolderDocId + "') and ("
						+ P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + " in (  '" + lsProviderId + "') OR "
						+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " in (  '" + lsProviderId + "'))";
				loSqlObject.setQueryString(lsSQLQuery);
				//parameter passed to remove sharing from child folders 4.0.2.0
				getAndRemoveCustomObjects(aoObjStore, loSqlObject, lsDocType, false,asAction);
				//parameter passed to remove sharing from child folders 4.0.2.0
			}
			else
			{
				// made changes for PT Test for folder ids
				if (lsFolderId != null && !lsFolderId.isEmpty())
				{
					int liFinalPageCount = (int) Math.floor(lsFolderIdList.size()
							/ ApplicationConstants.IN_QUERY_BREAK_LIMIT);
					if (lsFolderIdList.size() % ApplicationConstants.IN_QUERY_BREAK_LIMIT > 0)
						liFinalPageCount++;
					for (int i = 0; i < liFinalPageCount; i++)
					{
						liStartIndex = i * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
						liLastIndex = (i + 1) * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
						if (liLastIndex > lsFolderIdList.size())
							liLastIndex = lsFolderIdList.size();
						List loTempFolderList = lsFolderIdList.subList(liStartIndex, liLastIndex);
						lsFolderId = StringUtils.join(loTempFolderList, StringEscapeUtils.escapeHtml("','"));
						lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
								+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " FROM " + lsCustomObjectClassName
								+ " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + "  in ('" + lsFolderId + "')";
						loSqlObject.setQueryString(lsSQLQuery);
						//parameter passed to remove sharing from child folders 4.0.2.0
						getAndRemoveCustomObjects(aoObjStore, loSqlObject, HHSR5Constants.FOLDER, true, asAction);
						//parameter passed to remove sharing from child folders 4.0.2.0
					}

				}
				// made changes for PT Test for document ids
				if (lsDocumentId != null && !lsDocumentId.isEmpty())
				{
					int liFinalPageCount = (int) Math.floor(lsDocIdList.size()
							/ ApplicationConstants.IN_QUERY_BREAK_LIMIT);
					if (lsDocIdList.size() % ApplicationConstants.IN_QUERY_BREAK_LIMIT > 0)
						liFinalPageCount++;
					for (int i = 0; i < liFinalPageCount; i++)
					{
						liStartIndex = i * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
						liLastIndex = (i + 1) * ApplicationConstants.IN_QUERY_BREAK_LIMIT;
						if (liLastIndex > lsDocIdList.size())
							liLastIndex = lsDocIdList.size();
						List loTempDocList = lsDocIdList.subList(liStartIndex, liLastIndex);
						lsDocumentId = StringUtils.join(loTempDocList, StringEscapeUtils.escapeHtml("','"));
						lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
								+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " FROM " + lsCustomObjectClassName
								+ " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + "  in ('" + lsDocumentId + "')";
						loSqlObject.setQueryString(lsSQLQuery);
						//parameter passed to remove sharing from child folders 4.0.2.0
						getAndRemoveCustomObjects(aoObjStore, loSqlObject, HHSR5Constants.DOCUMENT, true, asAction);
						//parameter passed to remove sharing from child folders 4.0.2.0
					} 
				}
			}
		}
		catch (EngineRuntimeException aoEx)
		{
			lbSharingRemoved = false;
			ApplicationException loAppex = new ApplicationException(
					"FileNet RunTime Exception in removeDocumentsSharing method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet RunTime Exception in removeDocumentsSharing method", aoEx);
			throw loAppex;
		}
		catch (ApplicationException aoAppex)
		{
			lbSharingRemoved = false;
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.removeDocumentsSharing()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			lbSharingRemoved = false;
			ApplicationException loAppex = new ApplicationException("Exception in removeDocumentsSharing method ", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in removeDocumentsSharing method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.removeDocumentsSharing()");
		return lbSharingRemoved;
	}

	/**
	 * This method will be used to get all the custom objects and then remove
	 * them according to the search SQL passed to it
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param aoSearchSqlObj to search custom objects
	 * @param asAction a string with action value
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void getAndRemoveCustomObjects(ObjectStore aoObjStore, SearchSQL aoSearchSqlObj, String lsDocType,
			boolean isDeleteFlag, String asAction) throws ApplicationException
	{
		HashMap loHmExcepRequiredProp = new HashMap();
		HashMap loPropertyMap = new HashMap();
		HashMap loPropertyMapForChildDoc = new HashMap();
		List loRemainingPropList = null;
		PropertyFilter loPropFilter = null;
		Folder loFolderObj = null;
		List<com.nyc.hhs.model.Document> loFolderChildList = new ArrayList<com.nyc.hhs.model.Document>();
		List<String> lsDocumentIdList = new ArrayList<String>();
		try
		{
			loPropertyMap.put(HHSR5Constants.SHARING_FLAG, "");
			loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, "");
			loPropertyMap.put(HHSR5Constants.DELETE_FLAG, HHSR5Constants.EMPTY_STRING);
			loHmExcepRequiredProp.put("searchsqlObj", aoSearchSqlObj);
			LOG_OBJECT.Info("Entered P8ContentOperations.getAndRemoveCustomObjects() with parameters::"
					+ loHmExcepRequiredProp.toString());
			// Executes the search for retrieving custom objects
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(aoSearchSqlObj, null, null, Boolean.TRUE);
			Iterator loIt = loSet.iterator();
			String lsParentFolderPath = null;
			while (loIt.hasNext())
			{
				// type casting the custom object and deleting the same.
				CustomObject loObj = (CustomObject) loIt.next();
				String lsDocumentId = loObj.getProperties().getIdValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID)
						.toString();
				loObj.delete();
				loObj.save(RefreshMode.REFRESH);
				if (!lsDocumentIdList.contains(lsDocumentId))
				{
					lsDocumentIdList.add(lsDocumentId);
				}
			}
			for (Iterator iterator = lsDocumentIdList.iterator(); iterator.hasNext();)
			{
				String lsDocumentId = (String) iterator.next();
				loRemainingPropList = getProviderIdForSharedDocument(aoObjStore, lsDocumentId);
				if (loRemainingPropList.size() <= 0)
				{
					loPropFilter = createPropertyFilter(loPropertyMap);
					if (null != lsDocType && lsDocType.equalsIgnoreCase(HHSR5Constants.FOLDER))
					{

						loFolderObj = Factory.Folder.fetchInstance(aoObjStore, lsDocumentId, loPropFilter);
						Properties loProperties = loFolderObj.getProperties();
						loProperties.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.STRING_ZERO);
						loProperties.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
						loFolderObj.setUpdateSequenceNumber(null);
						loFolderObj.save(RefreshMode.REFRESH);
						// For child properties
						//parameter passed to remove sharing from child folders 4.0.2.0
						loFolderChildList = getChildList(aoObjStore, lsDocumentId, HHSR5Constants.INSUBFOLDER, null, asAction);
						//parameter passed to remove sharing from child folders 4.0.2.0
						Iterator loIterChild = loFolderChildList.iterator();
						while (loIterChild.hasNext())
						{
							updateChildObjectsSharingDetails(aoObjStore, loPropertyMap, loPropertyMapForChildDoc,
									loIterChild);
						}
						// For parent properties
						if (loFolderObj.getProperties().getInteger32Value(HHSR5Constants.DELETE_FLAG) != 0)
						{
							lsParentFolderPath = loFolderObj.getProperties().getStringValue(
									HHSR5Constants.FILENET_MOVE_FROM);
						}
						else
						{
							lsParentFolderPath = loFolderObj.getProperties().getStringValue(HHSR5Constants.PATH_NAME);
						}
						String lsParentPath = lsParentFolderPath.substring(0,
								lsParentFolderPath.lastIndexOf(P8Constants.STRING_SINGLE_SLASH));
						setParentPropertyForUnsharing(aoObjStore, lsParentPath, lsDocumentId);
					}
					else
					{
						removeSharingForDocument(aoObjStore, loPropertyMap, lsDocumentId);
					}
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getAndRemoveCustomObjects()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in getAndRemoveCustomObjects method ",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in getAndRemoveCustomObjects method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getAndRemoveCustomObjects() ");
	}

	/**
	 * The method is added in Release 5. It will update the sharing flag and
	 * sharing entity id of the document object and its parent during
	 * unshare/remove all/remove selected scenarios.
	 * @param aoObjStore
	 * @param asDocType
	 * @param aoReqMap
	 * @param aoPropertyMap
	 * @param asDocumentId
	 * @throws ApplicationException
	 */
	private void removeSharingForDocument(ObjectStore aoObjStore, HashMap aoPropertyMap, String asDocumentId)
			throws ApplicationException
	{
		PropertyFilter loPropFilter;
		Document loDocObj;
		aoPropertyMap.put(HHSR5Constants.FOLDERS_FILED_IN, ApplicationConstants.EMPTY_STRING);
		loPropFilter = createPropertyFilter(aoPropertyMap);
		loDocObj = Factory.Document.fetchInstance(aoObjStore, asDocumentId, loPropFilter);
		Properties loProperties = loDocObj.getProperties();
		loProperties.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.STRING_ZERO);
		loProperties.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
		loDocObj.setUpdateSequenceNumber(null);
		loDocObj.save(RefreshMode.REFRESH);
		// For Parent
		String lsDocParentPath = null;
		FolderSet loFolderSet = (FolderSet) loProperties.getObjectValue(HHSR5Constants.FOLDERS_FILED_IN);
		for (Iterator loFolderItr = loFolderSet.iterator(); loFolderItr.hasNext();)
		{
			Folder type = (Folder) loFolderItr.next();
			if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
			{
				lsDocParentPath = type.get_PathName();
			}
		}
		setParentPropertyForUnsharing(aoObjStore, lsDocParentPath, asDocumentId);
	}

	/**
	 * The method is added in Release 5. It will update the sharing flag and
	 * sharing entity id of the children object during unshare/remove all/remove
	 * selected scenarios.
	 * @param aoObjStore
	 * @param aoPropertyMap
	 * @param aoPropertyMapForChildDoc
	 * @param aoIterChild
	 * @throws ApplicationException
	 */
	private void updateChildObjectsSharingDetails(ObjectStore aoObjStore, HashMap aoPropertyMap,
			HashMap aoPropertyMapForChildDoc, Iterator aoIterChild) throws ApplicationException
	{
		PropertyFilter loPropFilter;
		PropertyFilter loPropFilterChildDoc;
		com.nyc.hhs.model.Document loChild = (com.nyc.hhs.model.Document) aoIterChild.next();
		String lsChildDocId = loChild.getDocumentId();
		if (null == loChild.getDocType() || loChild.getDocType().isEmpty()
				|| loChild.getDocType().equalsIgnoreCase(HHSR5Constants.FOLDER))
		{
			loPropFilter = createPropertyFilter(aoPropertyMap);
			Folder loChildFolderObj = Factory.Folder.fetchInstance(aoObjStore, lsChildDocId, loPropFilter);
			loChildFolderObj.getProperties().get(HHSR5Constants.SHARING_FLAG)
					.setObjectValue(HHSR5Constants.STRING_ZERO);
			loChildFolderObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(null);
			loChildFolderObj.save(RefreshMode.REFRESH);
		}
		else
		{
			aoPropertyMapForChildDoc.put(HHSR5Constants.SHARING_FLAG, HHSR5Constants.EMPTY_STRING);
			aoPropertyMapForChildDoc.put(HHSR5Constants.SHARED_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loPropFilterChildDoc = createPropertyFilter(aoPropertyMapForChildDoc);
			Document loChildDocObj = Factory.Document.fetchInstance(aoObjStore, lsChildDocId, loPropFilterChildDoc);

			loChildDocObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(null);
			loChildDocObj.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSR5Constants.STRING_ZERO);
			loChildDocObj.save(RefreshMode.REFRESH);
		}
	}

	/**
	 * This Method is used for filing custom object(sharing relationship object)
	 * into a folder.
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param aoCustObj custom object type for shared documents
	 * @param asFolderPath the path of the folder where document is stored
	 * @param asCustClassName name of the Custom class
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void fileCustomObjectToFolder(ObjectStore aoObjStr, CustomObject aoCustObj, String asFolderPath,
			String asCustClassName) throws ApplicationException
	{

		HashMap loHmRequiredProp = new HashMap();

		loHmRequiredProp.put(HHSR5Constants.AS_FOLDERS_PATH, asFolderPath);
		loHmRequiredProp.put("asCustClassName", asCustClassName);
		LOG_OBJECT.Error("Entered P8ContentOperations.fileCustomObjectToFolder() with parameters::"
				+ loHmRequiredProp.toString());

		try
		{

			if (asFolderPath == null || asFolderPath.equalsIgnoreCase("") || asCustClassName == null
					|| asCustClassName.equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in fileCustomObjectToFolder Method. Required fields are missing.");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}
			// fetching folder for filing custom object
			Folder loFldr = Factory.Folder.getInstance(aoObjStr, null, asFolderPath);

			// Setting referential Containment relationship
			ReferentialContainmentRelationship loRCR = loFldr.file(aoCustObj, AutoUniqueName.AUTO_UNIQUE, aoCustObj
					.get_Id().toString(), DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
			loRCR.save(RefreshMode.NO_REFRESH);
		}
		catch (EngineRuntimeException aoEc)
		{
			ApplicationException loAppex = new ApplicationException(
					"Filenet Exception in fileCustomObjectToFolder Method", aoEc);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fileCustomObjectToFolder()::", aoEc);
			throw loAppex;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fileCustomObjectToFolder()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fileCustomObjectToFolder Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Error in fileCustomObjectToFolder Method", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Error("Exited P8ContentOperations.fileCustomObjectToFolder()");

	}

	/**
	 * This Method will give full folder path in FILENET Repository for a
	 * particular doc type value
	 * 
	 * @param aoDocTypeXML document type XML configuration DOM
	 * @param asProviderID specifies the ID of the provider
	 * @param asDocType specifies the type of the document
	 * @param asDocCategory specifies the Category of the document
	 * @param asOrganizationId Organization Id
	 * @return String the path of the folder
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String getFolderPath(Object aoDocTypeXML, String asProviderID, String asDocType, String asDocCategory,
			String asOrganizationId) throws ApplicationException
	{
		HashMap loHmRequiredProp = new HashMap();
		String lsFullFolderPath = null;
		String lsSubFolderPath = null;
		Element loElt = null;
		loHmRequiredProp.put("aoDocTypeDOM", aoDocTypeXML);
		loHmRequiredProp.put("asProviderID", asProviderID);
		loHmRequiredProp.put(HHSR5Constants.AS_DOC_TYPE, asDocType);
		LOG_OBJECT.Info("Entered P8ContentOperations.getFolderPath() with parameters::" + loHmRequiredProp.toString());

		if (null != asOrganizationId && asOrganizationId.equalsIgnoreCase(P8Constants.APPLICATION_PROVIDER_ORG))
		{
			lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PROP_FILE_PREDEFINED_FOLDER_PATH_PROVIDER);
			lsFullFolderPath = lsFullFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(asProviderID);
			loHmRequiredProp.put("preDefinedFolderPath", lsFullFolderPath);
		}
		else if (null != asOrganizationId && (asOrganizationId.equalsIgnoreCase(P8Constants.APPLICATION_CITY_ORG)))
		{
			lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PROP_FILE_PREDEFINED_FOLDER_PATH_CITY);
			loHmRequiredProp.put("preDefinedFolderPath", lsFullFolderPath);
		}
		else if (null != asOrganizationId && (asOrganizationId.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
		{
			lsFullFolderPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					P8Constants.PROP_FILE_PREDEFINED_FOLDER_PATH_AGENCY);
			lsFullFolderPath = lsFullFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(asProviderID);
			loHmRequiredProp.put("preDefinedFolderPath", lsFullFolderPath);
		}
		if (aoDocTypeXML == null || "".equalsIgnoreCase(asProviderID) || "".equalsIgnoreCase(asDocType)
				|| "".equalsIgnoreCase(lsFullFolderPath))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getFolderPath Method. Required Parameters are missing");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;
		}
		org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;
		try
		{
			if (null == asDocCategory || asDocCategory.isEmpty())
			{
				loElt = XMLUtil.getElement("//DocType [@name=\"" + asDocType + "\"]", loDocTypeXML);
			}
			else
			{
				loElt = XMLUtil.getElement("//DocumentCategory [@name=\"" + asDocCategory + "\"] //DocType [@name=\""
						+ asDocType + "\"]", loDocTypeXML);
			}
			if (null == loElt)
			{
				ApplicationException loAppex = new ApplicationException("Could not fetch Element for goven DocType");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}
			else
			{
				Element loFolderPath = loElt.getChild("foldername");
				lsSubFolderPath = loFolderPath.getText();
			}
			if (lsSubFolderPath == null)
			{
				ApplicationException loAppex = new ApplicationException("Could not fetch folder Path for given DocType");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}
			lsFullFolderPath = lsFullFolderPath.concat(lsSubFolderPath);

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getFolderPath()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Could not fetch folder Path for given DocType",
					aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Could not fetch folder Path for given DocType", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getFolderPath() ");
		return lsFullFolderPath;
	}

	/**
	 * This Method will give document class name in FILENET Repository for a
	 * particular doc type value
	 * 
	 * @param aoDocTypeXML document type XML configuration DOM
	 * @param asDocType the type of the document
	 * @param asDocCategory the Category of the document
	 * @return String the name of the Document class
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String getDocClassName(Object aoDocTypeXML, String asDocType, String asDocCategory)
			throws ApplicationException
	{

		HashMap loHmRequiredProp = new HashMap();
		Element loElt = null;
		loHmRequiredProp.put(HHSR5Constants.AS_DOC_TYPE, asDocType);
		LOG_OBJECT
				.Info("Entered P8ContentOperations.getDocClassName() with parameters::" + loHmRequiredProp.toString());
		String lsDocClassName = null;
		if (aoDocTypeXML == null)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocClassName Method : Required Parameters are missing");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;

		}
		else if (asDocType.equalsIgnoreCase(""))
		{
			lsDocClassName = "HHS_ACCELERATOR";
		}
		else
		{
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;

			try
			{
				if (null != asDocCategory && !asDocCategory.isEmpty())
				{
					loElt = XMLUtil.getElement("//DocumentCategory [@name=\"" + asDocCategory
							+ "\"] //DocType [@name=\"" + asDocType + "\"]", loDocTypeXML);
				}
				else if (asDocType != null && asDocType.startsWith(ApplicationConstants.DOC_SAMPLE))
				{
					// R5: updated for sample documents
					lsDocClassName = "HELP_SAMPLE";
					// R5: updated for sample documents end
				}
				else if (asDocType != null)
				{
					loElt = XMLUtil.getElement("//DocType [@name=\"" + asDocType + "\"]", loDocTypeXML);
				}
				if (null != loElt)
				{
					Element loDoccumentClass = loElt.getChild("DoccumentClass");
					lsDocClassName = loDoccumentClass.getText();
				}
				if (lsDocClassName == null)
				{
					ApplicationException loAppex = new ApplicationException(
							"Could not fetch Document Class for given DocType");
					loAppex.setContextData(loHmRequiredProp);
					throw loAppex;
				}

			}
			catch (ApplicationException aoAppex)
			{
				aoAppex.setContextData(loHmRequiredProp);
				LOG_OBJECT.Error("Exception in P8ContentOperations.getDocClassName()::", aoAppex);
				throw aoAppex;
			}
			catch (Exception aoEx)
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in fetching Document Class for given DocType", aoEx);
				loAppex.setContextData(loHmRequiredProp);
				LOG_OBJECT.Error("Error in fetching Document Class for given DocType", aoEx);
				throw loAppex;
			}

		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocClassName() ");
		return lsDocClassName;
	}

	/**
	 * Updated for 3.1.0, enhancement 6021 - Added checks for char 500 extension
	 * This method returns document properties
	 * 
	 * @param aoUserSession P8 user session object
	 * @param aoObjStr active filenet object store session
	 * @param asDocClassName name of the document class
	 * @param aoHmRequiredProps map containing the required properties
	 * @param aoFilterMap Filter Map
	 * @param aoOrderByMap aoOrder By Map
	 * @param abFilterIncluded Filter Included
	 * @param asPagesize Pagesize
	 * @return List containing the document properties
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getDocsProperties(P8UserSession aoUserSession, ObjectStore aoObjStr, String asDocClassName,
			HashMap aoHmRequiredProps, HashMap aoFilterMap, HashMap aoOrderByMap, boolean abFilterIncluded,
			String asPagesize) throws ApplicationException
	{
		String lsIdentificationKey = null;
		// Updated for 3.1.0, Enhancement 6021, adding check for CHAR500
		// Extension - Select From Vault Check
		boolean lbCharExtensionSelectFromVaultFlag = false;
		String lsSharedFlag = (String) aoFilterMap.get("sharedFlag");
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmExcepRequiredProp.put("aoHmRequiredProps", aoHmRequiredProps);
		loHmExcepRequiredProp.put("aoFilterMap", aoFilterMap);
		loHmExcepRequiredProp.put("aoOrderByMap", aoOrderByMap);
		loHmExcepRequiredProp.put("abFilterIncluded", abFilterIncluded);
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocsProperties() with parameters::"
				+ loHmExcepRequiredProp.toString());
		String lsIsFilter = null;
		boolean lbContinuable = true;
		int liPageSize = 0;
		int liMaxDocCount = 0;
		List loDocPropsList = null;
		String lsWhereClause = null;
		SearchSQL loSqlObject = new SearchSQL();
		String lsSQLQuery = "";
		// Updated for 3.1.0, Enhancement 6021, adding check for CHAR500
		// Extension
		if (aoHmRequiredProps.get("identificationKey") != null)
			if (aoHmRequiredProps.get("identificationKey") != null)
			{
				lsIdentificationKey = (String) aoHmRequiredProps.get("identificationKey");
				aoHmRequiredProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, "DOC");
			}
		aoHmRequiredProps.remove("identificationKey");
		if (aoFilterMap != null && !aoFilterMap.isEmpty())
		{
			lbCharExtensionSelectFromVaultFlag = setFilterCategoryForMaskedType(aoFilterMap,
					lbCharExtensionSelectFromVaultFlag);
			lsSharedFlag = (String) aoFilterMap.get("sharedFlag");
			lsIsFilter = (String) aoFilterMap.get(HHSR5Constants.IS_FILTER);
		}
		if (asDocClassName.equalsIgnoreCase("")
				|| (!lbCharExtensionSelectFromVaultFlag && asDocClassName
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXTENSION_DOC_CLASS)))
		{
			asDocClassName = P8Constants.PROPERTY_CE_ROOT_DOCUMENT_CLASS_NAME;
		}
		try
		{
			if (null == asPagesize || asPagesize.isEmpty())
			{
				liPageSize = Integer.valueOf(aoUserSession.getObjectsAllowedPerPage());
			}
			else
			{
				liPageSize = Integer.valueOf(asPagesize);
			}
			if (aoUserSession.getPageIterator() == null)
			{
				String lsMaxDocCountKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
						+ P8Constants.DOCUMENT_VAULT_MAXIMUM_COUNT;
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				liMaxDocCount = Integer.valueOf(loApplicationSettingMap.get(lsMaxDocCountKey));

				// Added for Release 5
				if (null != aoFilterMap.get("selectVault")
						&& aoFilterMap.get("selectVault").toString().equalsIgnoreCase("true"))
				{
					abFilterIncluded = false;
				}
				String lsSelectClause = createSelectClause(aoHmRequiredProps, null);
				String lsOrderByClause = createOrderByClause(aoOrderByMap, P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS);
				aoFilterMap.put("objectStrore", aoObjStr);
				lsWhereClause = createWhereClause(aoFilterMap, P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS,
						abFilterIncluded);

				if (abFilterIncluded)
				{
					if ((null != aoFilterMap.get(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID) && !"".equals(aoFilterMap
							.get(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID).toString().trim()))
							|| (null != aoFilterMap.get(P8Constants.PROPERTY_CE_FILTER_NYC_ORG) && !""
									.equals(aoFilterMap.get(P8Constants.PROPERTY_CE_FILTER_NYC_ORG).toString().trim())))
					{
						lsSQLQuery = lsSelectClause
								+ " FROM "
								+ asDocClassName
								+ HHSConstants.SPACE
								+ P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS
								+ " INNER JOIN "
								+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
										"SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
								+ P8Constants.PROPERTY_CE_SHARED_OBJECT_TABLE_ALIAS + " ON "
								+ P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS + "." + P8Constants.PROPERTY_CE_DOCUMENT_ID
								+ " = " + P8Constants.PROPERTY_CE_SHARED_OBJECT_TABLE_ALIAS + "."
								+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + lsWhereClause + lsOrderByClause;
					}
					else if ((lsIdentificationKey != null && lsIdentificationKey.equalsIgnoreCase("selectDocFromVault"))
							&& (asDocClassName != null && asDocClassName.equalsIgnoreCase("KeyStaffResume")))
					{
						lsSQLQuery = lsSelectClause
								+ " FROM HHS_ACCELERATOR DOC"
								+ lsWhereClause
								+ " AND (IsClass(DOC, CEO_RESUME) OR IsClass(DOC,CFO_RESUME) OR IsClass(DOC,KEYSTAFFRESUME)) "
								+ lsOrderByClause;
					}
					// Below block will be executed when sharing details
					// selected from document vault screen and agency searched
					// for shared documents
					else if ((null != aoFilterMap && !aoFilterMap.isEmpty() && (null != lsIsFilter && !lsIsFilter
							.isEmpty()))
							&& ((aoFilterMap.get(HHSR5Constants.IS_SHARED) != null && ((Boolean) aoFilterMap
									.get(HHSR5Constants.IS_SHARED))) || (null != lsSharedFlag
									&& !lsSharedFlag.isEmpty() && lsSharedFlag.equalsIgnoreCase(HHSR5Constants.TRUE))))
					{
						lsSQLQuery = lsSelectClause
								+ " FROM ((( ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) full join HHSCustomFolder fo on rcr.head = object(fo.id))"
								+ "left join HHSSharedDocument SHR on doc.SHARED_ENTITY_ID = SHR.SHARED_DOCUMENT_ID )"
								+ "left join HHSSharedDocument SHR1 on FO.SHARED_ENTITY_ID = SHR1.SHARED_DOCUMENT_ID "

								+ lsWhereClause + lsOrderByClause;

						if (null != aoFilterMap.get("lsFindOrgDocFlag")
								&& aoFilterMap.get("lsFindOrgDocFlag").toString().equalsIgnoreCase("true")
								&& null != aoFilterMap.get("sharedFlag")
								&& !aoFilterMap.get("sharedFlag").toString().equalsIgnoreCase("true"))
						{
							lsSelectClause = lsSelectClause
									.concat(", sud.ORG_LEGAL_NAME,sud1.ORG_LEGAL_NAME as ORG_LEGAL_NAME_FOLDER ");
							if (null != aoFilterMap.get("agencyList"))
							{
								lsSQLQuery = lsSelectClause
										+ "FROM (((((ReferentialContainmentRelationship rcr "
										+ "left join hhs_accelerator doc on rcr.Head = object(doc.id))"
										+ "full join HHSCustomFolder fo on rcr.head = object(fo.id))"
										+ "left join HHSSharedDocument SHR on doc.SHARED_ENTITY_ID = SHR.SHARED_DOCUMENT_ID)"
										+ "left join HHSSharedDocument SHR1 on FO.SHARED_ENTITY_ID = SHR1.SHARED_DOCUMENT_ID)"
										+ "left join ORGANIZATION_DETAILS sud on doc.PROVIDER_ID = sud.PROVIDER_ID )"
										+ "left join ORGANIZATION_DETAILS sud1 on FO.PROVIDER_ID = sud1.PROVIDER_ID"
										+ lsWhereClause + lsOrderByClause;
							}
							else
							{
								lsSQLQuery = lsSelectClause
										+ "FROM (((ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) "
										+ "full join HHSCustomFolder fo on rcr.head = object(fo.id)) "
										+ "left join ORGANIZATION_DETAILS sud on doc.PROVIDER_ID = sud.PROVIDER_ID ) "
										+ "left join ORGANIZATION_DETAILS sud1 on FO.PROVIDER_ID = sud1.PROVIDER_ID "
										+ lsWhereClause + lsOrderByClause;
							}

						}
						else if (null != aoFilterMap.get("lsFindOrgDocFlag")
								&& aoFilterMap.get("lsFindOrgDocFlag").toString().equalsIgnoreCase("true")
								&& null != aoFilterMap.get("sharedFlag")
								&& aoFilterMap.get("sharedFlag").toString().equalsIgnoreCase("true"))
						{
							lsSelectClause = lsSelectClause
									.concat(", sud.ORG_LEGAL_NAME,sud1.ORG_LEGAL_NAME as ORG_LEGAL_NAME_FOLDER ");
							lsSQLQuery = lsSelectClause
									+ "FROM (((((ReferentialContainmentRelationship rcr "
									+ "left join hhs_accelerator doc on rcr.Head = object(doc.id))"
									+ "full join HHSCustomFolder fo on rcr.head = object(fo.id))"
									+ "left join HHSSharedDocument SHR on doc.SHARED_ENTITY_ID = SHR.SHARED_DOCUMENT_ID)"
									+ "left join HHSSharedDocument SHR1 on FO.SHARED_ENTITY_ID = SHR1.SHARED_DOCUMENT_ID)"
									+ "left join ORGANIZATION_DETAILS sud on doc.PROVIDER_ID = sud.PROVIDER_ID )"
									+ "left join ORGANIZATION_DETAILS sud1 on FO.PROVIDER_ID = sud1.PROVIDER_ID"
									+ lsWhereClause + lsOrderByClause;
						}

					}
					else
					{
						lsSQLQuery = lsSelectClause
								+ " FROM (ReferentialContainmentRelationship rcr left join hhs_accelerator "
								+ "doc on rcr.Head = object(doc.id)) full join HHSCustomFolder fo on rcr.head = object(fo.id) "
								+ lsWhereClause + lsOrderByClause;
						// Adding if condition for ManageOrg/Select Org chnages
						// for Defect # 8150
						if (null != aoFilterMap.get("ControllerName")
								&& aoFilterMap.get("ControllerName").toString().equalsIgnoreCase("selectOrgnization"))
						{
							lsSQLQuery = lsSelectClause
									+ " FROM ((ReferentialContainmentRelationship rcr left join hhs_accelerator "
									+ "doc on rcr.Head = object(doc.id)) full join HHSCustomFolder fo on rcr.head = object(fo.id)) left join HHSSharedDocument shr on rcr.head = object(shr.SHARED_DOCUMENT_ID)  "
									+ lsWhereClause + lsOrderByClause;
						}

						if (null != aoFilterMap.get("lsFindOrgDocFlag")
								&& aoFilterMap.get("lsFindOrgDocFlag").toString().equalsIgnoreCase("true"))
						{
							if (null != aoFilterMap.get("agencyList"))
							{
								lsSQLQuery = lsSelectClause
										+ "FROM (((((ReferentialContainmentRelationship rcr "
										+ "left join hhs_accelerator doc on rcr.Head = object(doc.id))"
										+ "full join HHSCustomFolder fo on rcr.head = object(fo.id))"
										+ "left join HHSSharedDocument SHR on doc.SHARED_ENTITY_ID = SHR.SHARED_DOCUMENT_ID)"
										+ "left join HHSSharedDocument SHR1 on FO.SHARED_ENTITY_ID = SHR1.SHARED_DOCUMENT_ID)"
										+ "left join ORGANIZATION_DETAILS sud on doc.PROVIDER_ID = sud.PROVIDER_ID )"
										+ "left join ORGANIZATION_DETAILS sud1 on FO.PROVIDER_ID = sud1.PROVIDER_ID"
										+ lsWhereClause + lsOrderByClause;
							}
							else
							{
								lsSelectClause = lsSelectClause
										.concat(", sud.ORG_LEGAL_NAME,sud1.ORG_LEGAL_NAME as ORG_LEGAL_NAME_FOLDER ");
								lsSQLQuery = lsSelectClause
										+ "FROM (((ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) "
										+ "full join HHSCustomFolder fo on rcr.head = object(fo.id)) "
										+ "left join ORGANIZATION_DETAILS sud on doc.PROVIDER_ID = sud.PROVIDER_ID ) "
										+ "left join ORGANIZATION_DETAILS sud1 on FO.PROVIDER_ID = sud1.PROVIDER_ID "
										+ lsWhereClause + lsOrderByClause;
							}

						}
					}
					// End
				}
				else
				{
					lsSelectClause = lsSelectClause.replace("DISTINCT", "");
					lsSQLQuery = lsSelectClause + " FROM " + asDocClassName + P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS;
					// Release 5 ends
					lsSQLQuery = lsSQLQuery + lsWhereClause + lsOrderByClause;
				}

				if (null != aoFilterMap.get(HHSConstants.IS_BULK_UPLOAD)
						&& (Boolean) aoFilterMap.get(HHSConstants.IS_BULK_UPLOAD))
				{
					lsSQLQuery = getSqlQueryForBulkUpload(aoFilterMap);

				}
				LOG_OBJECT.Debug("Query String to fetch objects::::::::" + lsSQLQuery);
				loSqlObject.setQueryString(lsSQLQuery);
				// Executes the content search.
				SearchScope loSearchScope = new SearchScope(aoObjStr);

				if (null != lsSQLQuery && lsSQLQuery.toLowerCase().contains("where"))
				{
					int liPageCount = 0;
					aoUserSession.setCurrentlyFetchedPageCount(0);
					fetchTotalPageCount(aoObjStr, aoUserSession, lsSQLQuery, liPageSize);

					RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, liPageSize, null, lbContinuable);
					PageIterator loPageItr = loRowSet.pageIterator();

					List<PageMark> loPageMarks = new ArrayList<PageMark>();
					aoUserSession.setAllPageMark(loPageMarks);
					aoUserSession.setPageIterator(loPageItr);
					loDocPropsList = getCurrentPageAttributes(aoUserSession, aoHmRequiredProps, loPageItr, liPageSize);
				}
				else
				{
					loDocPropsList = null;
					aoUserSession.setAllPageMark(null);
					aoUserSession.setPageIterator(null);
					aoUserSession.setTotalPageCount(0);
				}
			}
			else
			{
				loDocPropsList = getCurrentPageAttributes(aoUserSession, aoHmRequiredProps,
						aoUserSession.getPageIterator(), liPageSize);
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			aoAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocsProperties()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDocProperties Method", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error in getDocProperties Method", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getDocsProperties() ");
		return loDocPropsList;
	}

	/**
	 * @return
	 */
	private HashMap<String, String> createSelectClauseForSharing()
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.createSelectClauseForSharing().");
		HashMap<String, String> loHmReqProps = new HashMap<String, String>();
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "DOC");
		loHmReqProps.put(HHSR5Constants.ORGANIZATION_ID_KEY, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID + " AS HHS_SHARED_BY", "DOC");
		loHmReqProps.put(HHSR5Constants.DELETED_DATE, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "DOC");
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE, "DOC");
		loHmReqProps.put(HHSConstants.DATE_LAST_MODIFIED + " as DOC_LAST_MODIFIED", "DOC");
		loHmReqProps.put("DateCreated as DOC_DATE_CREATED", "DOC");
		loHmReqProps.put("ID", "DOC");
		loHmReqProps.put("SHARING_FLAG AS DOC_SHARING_FLAG", "DOC");
		loHmReqProps.put("LINK_TO_APPLICATION", "DOC");
		loHmReqProps.put(HHSR5Constants.SHARED_ENTITY_ID, "DOC");
		LOG_OBJECT.Info("Exited P8ContentOperations.createSelectClauseForSharing().");
		return loHmReqProps;
	}

	/**
	 * This method will execute the query to get the required file list from p8
	 * server
	 * 
	 * @param aoUserSession P8 user session object
	 * @param aoObjStr active filenet object store session
	 * @param asDocClassName name of the document class
	 * @param aoHmRequiredProps map containing the required properties
	 * @param aoFilterMap a map containing values for filtering the query
	 * @param aoOrderByMap a map containing values to form order by clause of
	 *            query
	 * @param abFilterIncluded a boolean value indicating to include or not
	 * @return List loDocPropsList list containing document properties
	 *         information for help document category
	 * @throws ApplicationException
	 */
	public List getHelpDocsProperties(P8UserSession aoUserSession, ObjectStore aoObjStr, String asDocClassName,
			HashMap aoHmRequiredProps, HashMap aoFilterMap, HashMap aoOrderByMap, boolean abFilterIncluded)
			throws ApplicationException
	{

		HashMap<String, Object> loHmExcepRequiredProp = new HashMap<String, Object>();
		loHmExcepRequiredProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmExcepRequiredProp.put("aoHmRequiredProps", aoHmRequiredProps);
		loHmExcepRequiredProp.put("aoFilterMap", aoFilterMap);
		loHmExcepRequiredProp.put("aoOrderByMap", aoOrderByMap);
		loHmExcepRequiredProp.put("abFilterIncluded", abFilterIncluded);
		LOG_OBJECT.Info("Entered P8ContentOperations.getHelpDocsProperties() with parameters::"
				+ loHmExcepRequiredProp.toString());

		List loDocPropsList = new ArrayList();
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			String lsSelectClause = createSelectClause(aoHmRequiredProps, P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS);
			String lsOrderByClause = createOrderByClause(aoOrderByMap, P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS);
			String lsSQLQuery = "";
			String lsWhereClause = createWhereClause(aoFilterMap, P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS,
					abFilterIncluded);
			lsSQLQuery = lsSelectClause + " FROM " + asDocClassName + P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS
					+ lsWhereClause + lsOrderByClause;
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator<Object> loIt = loRowSet.iterator();
			while (loIt.hasNext())
			{
				Document loDoc = (Document) loIt.next();
				HashMap loHmProps = new HashMap();

				// Retrieving property map for the document
				Properties loProps = loDoc.getProperties();
				Iterator loProprtiesItr = loProps.iterator();

				while (loProprtiesItr.hasNext())
				{
					Property loProp = (Property) loProprtiesItr.next();
					// setting value to the output hash map
					loHmProps.put(loProp.getPropertyName(), loProp.getObjectValue());
				}

				loDocPropsList.add(loHmProps);
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			aoAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getHelpDocsProperties()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getHelpDocProperties Method", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error in getHelpDocProperties Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getHelpDocsProperties() ");
		return loDocPropsList;
	}

	/**
	 * This Method is for Creating Select Clause
	 * 
	 * @param aoHmProps a map containing information about properties to be
	 *            fetched
	 * @param asTableAlias Table Alias
	 * @return String select clause as output
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String createSelectClause(HashMap aoHmProps, String asTableAlias) throws ApplicationException
	{

		StringBuffer lsBfSelectClause = new StringBuffer();
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put("asTableAlias", asTableAlias);
		LOG_OBJECT.Info("Entered P8ContentOperations.createSelectClause() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{
			// changes for Release 5
			lsBfSelectClause.append("SELECT DISTINCT ");
			if (aoHmProps != null)
			{
				if (!aoHmProps.isEmpty())
				{
					Iterator loIt = aoHmProps.entrySet().iterator();
					while (loIt.hasNext())
					{
						Entry lsPropName = (Entry) loIt.next();
						lsBfSelectClause.append(lsPropName.getValue());
						lsBfSelectClause.append("." + lsPropName.getKey());
						if (loIt.hasNext())
						{
							lsBfSelectClause.append(",");
						}
					}
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Generating the select clause", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While Generating the select clause", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.createSelectClause() ");
		return lsBfSelectClause.toString();
	}

	/**
	 * This Method is for Creating WhereClause for any SQL
	 * 
	 * @param aoFilterMap a map containing values for filtering the query
	 * @param asConstantName name of the constant
	 * @param abFilterIncluded a boolean value indicating to include or not
	 * @return String the created where clause
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String createWhereClause(HashMap aoFilterMap, String asConstantName, boolean abFilterIncluded)
			throws ApplicationException
	{
		StringBuffer lsBfWhereClause = new StringBuffer(" WHERE ");
		HashMap loHmExcepRequiredProp = new HashMap();
		HashMap<String, List<String>> loSharedMap = new HashMap<String, List<String>>();
		String lsPresentOrgId = null;
		int liCounter = 1;
		String lsCustomOrg = null;
		String lsSharedFlag = null;
		boolean lbProviderNycFlag = false;
		boolean lbOrCondition = false;
		boolean lbFindOrgDocFlag = false;
		String lsSelectFromDV = null;
		loHmExcepRequiredProp.put("asConstantName", asConstantName);
		loHmExcepRequiredProp.put("abFilterIncluded", abFilterIncluded);
		List<String> loDbList = new ArrayList<String>();
		String lsEntityType = null;
		LOG_OBJECT.Info("Entered EnahncedP8ContentOperations.createWhereClause() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{
			lsSharedFlag = (String) aoFilterMap.get("sharedFlag");
			lsEntityType = (String) aoFilterMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID);
			lsCustomOrg = (String) aoFilterMap.get("CustomOrg");
			lsPresentOrgId = (String) aoFilterMap.get("presentOrgId");
			loDbList = (List<String>) aoFilterMap.get("dBReturnList");
			lsSelectFromDV = (String) aoFilterMap.get("selectVault");
			if (null == aoFilterMap.get(HHSR5Constants.IS_FIND_ORG_DOC_FLAG))
			{
				lbFindOrgDocFlag = false;
			}
			else
				lbFindOrgDocFlag = Boolean.valueOf(aoFilterMap.get(HHSR5Constants.IS_FIND_ORG_DOC_FLAG).toString());

			// Removing some entries from filterMap, so that they will not be
			// part of where clause
			// removeEntriesFromFilterMap(aoFilterMap);
			if ((null != lsEntityType && !lsEntityType.isEmpty() && (null == loDbList || loDbList.isEmpty()))
					&& !lsEntityType.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_CORPORATE_STRUCTURE))
			{
				return new String(HHSR5Constants.EMPTY_STRING);
			}
			if (null != lsSharedFlag && !lsSharedFlag.isEmpty() && (null == loDbList || loDbList.isEmpty()))
			{
				lsBfWhereClause = createWhereClauseForSharingFlag(aoFilterMap, asConstantName, lsBfWhereClause,
						lsSharedFlag, lbOrCondition, lbProviderNycFlag, liCounter, abFilterIncluded, lbFindOrgDocFlag,
						loDbList, lsEntityType);
			}
			// added for selectdocfromvault- Release 5
			else if (null != lsSelectFromDV && lsSelectFromDV.equalsIgnoreCase(HHSR5Constants.TRUE))
			{
				lsBfWhereClause = createWhereClauseForSelectFromVault(aoFilterMap, asConstantName, lsBfWhereClause,
						lsSelectFromDV);
			}
			// SelectVault ends
			else if (null != aoFilterMap
					&& !aoFilterMap.isEmpty()
					&& null != aoFilterMap.get(HHSR5Constants.IS_FILTER)
					&& !(String.valueOf(aoFilterMap.get(HHSR5Constants.IS_FILTER))
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)))
			{
				aoFilterMap.remove("HHSProviderID");
				lsBfWhereClause = createWhereClauseForFilterFlag(aoFilterMap, asConstantName, abFilterIncluded,
						lsBfWhereClause, loHmExcepRequiredProp, liCounter, lbProviderNycFlag, lbOrCondition, loDbList,
						lsEntityType);

			}
			else
			{
				if ((null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH) && !((String) aoFilterMap
						.get(HHSR5Constants.FOLDER_PATH)).isEmpty())
						|| (null != aoFilterMap.get(HHSR5Constants.FOLDER_ID) && !((String) aoFilterMap
								.get(HHSR5Constants.FOLDER_ID)).isEmpty()))
				{
					addFolderPathOrFolderId(aoFilterMap, lsBfWhereClause);
				}

				if (null != aoFilterMap.get("ControllerName")
						&& aoFilterMap.get("ControllerName").toString().equalsIgnoreCase("selectOrgnization"))
				{// Changing if for Defect # 8150
					if (null != lsPresentOrgId && !lsPresentOrgId.isEmpty()
							&& !lsPresentOrgId.equalsIgnoreCase("city_org"))
					{
						createWhereClauseForSharedOrg(lsBfWhereClause, aoFilterMap);
					}
				}

			}

			if (null != lsBfWhereClause && !lsBfWhereClause.toString().isEmpty()
					&& !lsBfWhereClause.toString().contains("DOC.DELETE_FLAG"))
			{
				setDeleteFlagInWhereClause(aoFilterMap, lsBfWhereClause);
			}
			if (null != aoFilterMap
					&& !aoFilterMap.isEmpty()
					&& ((null != aoFilterMap.get("HELP_CATEGORY") && !aoFilterMap.get("HELP_CATEGORY").toString()
							.isEmpty())
							|| (null != aoFilterMap.get("HELP_DOCUMENT_FOR_AGENCY") && !aoFilterMap
									.get("HELP_DOCUMENT_FOR_AGENCY").toString().isEmpty()) || (null != aoFilterMap
							.get("HELP_DOCUMENT_FOR_PROVIDER") && !aoFilterMap.get("HELP_DOCUMENT_FOR_PROVIDER")
							.toString().isEmpty())))
			{
				return createWhereClauseForHelp(aoFilterMap);
			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Generating Where Clause", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While Generating Where Clause", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited EnahncedP8ContentOperations.createWhereClause() ");
		return lsBfWhereClause.toString();
	}

	/**
	 * This method is used to set delete flag in where clause, to differentiate
	 * the documents from document vault and recyclebin
	 * @param aoFilterMap filter map
	 * @param asBfWhereClause where clause.
	 */
	private static void setDeleteFlagInWhereClause(HashMap aoFilterMap, StringBuffer asBfWhereClause)
	{
		if (!asBfWhereClause.toString().trim().endsWith("AND"))
		{
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
					&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(HHSR5Constants.RECYCLE_BIN))
			{
				if (null != aoFilterMap.get("addedOnlyTitle") && (Boolean) aoFilterMap.get("addedOnlyTitle"))
				{
					/*Changes made for defect id 8386 in release 4.0.2.0 DELETE_FLAG check updated from <> 0  to = 2 */
					asBfWhereClause.append(" AND (DOC.DELETE_FLAG  = 2 OR FO.DELETE_FLAG = 2)");
				}
				else
				{
					/*Changes made for defect id 8386 in release 4.0.2.0 DELETE_FLAG check updated from <> 0  to = 2 */
					asBfWhereClause.append(" AND DOC.DELETE_FLAG = 2");
				}
			}
			else
			{
				if (null != aoFilterMap.get("addedOnlyTitle") && (Boolean) aoFilterMap.get("addedOnlyTitle"))
				{
					asBfWhereClause.append(" AND (DOC.DELETE_FLAG  = 0 OR FO.DELETE_FLAG=0)");
				}
				else
				{
					asBfWhereClause.append(" AND DOC.DELETE_FLAG  = 0");
				}
			}
		}
		else
		{
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
					&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(HHSR5Constants.RECYCLE_BIN))
			{
				if (null != aoFilterMap.get("addedOnlyTitle") && (Boolean) aoFilterMap.get("addedOnlyTitle"))
				{
					asBfWhereClause
							.append(" (DOC.DELETE_FLAG  = 2 OR FO.DELETE_FLAG=2) AND (FO.IS_ARCHIVE = true OR DOC.IS_ARCHIVE = true)");
				}
				else
				{
					asBfWhereClause.append(" DOC.DELETE_FLAG  = 2 AND DOC.IS_ARCHIVE = true");
				}
			}
			else
			{
				if (null != aoFilterMap.get("addedOnlyTitle") && (Boolean) aoFilterMap.get("addedOnlyTitle"))
				{
					asBfWhereClause
							.append(" (DOC.DELETE_FLAG  = 0 OR FO.DELETE_FLAG=0) AND (FO.IS_ARCHIVE = false OR DOC.IS_ARCHIVE = false)");
				}
				else
				{
					asBfWhereClause.append(" DOC.DELETE_FLAG  = 0 AND DOC.IS_ARCHIVE = false");
				}
			}
		}
	}

	/**
	 * @param lsBfWhereClause
	 * @param loSharedObjId
	 * @throws ApplicationException
	 */
	private static void createWhereClauseForSharedOrg(StringBuffer lsBfWhereClause, HashMap aoFilterMap)
			throws ApplicationException
	{
		// Changing method body for Defect # 8150
		if (null != aoFilterMap.get("folderPath")
				|| (null != aoFilterMap.get("parentId") && aoFilterMap.get("parentId").toString()
						.equalsIgnoreCase("root")))
		{
			lsBfWhereClause.append(" AND (SHR.HHS_AGENCY_ID = '");
			lsBfWhereClause.append(aoFilterMap.get("presentOrg"));
			lsBfWhereClause.append("' OR SHR.HHSProviderID='");
			lsBfWhereClause.append(aoFilterMap.get("presentOrg"));
			lsBfWhereClause.append("') AND SHR.HHS_SHARED_BY = '");
			lsBfWhereClause.append(aoFilterMap.get("CustomOrg"));
			lsBfWhereClause.append("' AND (FO.SHARING_FLAG = '2' OR DOC.SHARING_FLAG  = '2')");
		}
		else
		{
			ObjectStore loObj = (ObjectStore) aoFilterMap.get("objectStrore");
			String lsFolderId = (String) aoFilterMap.get("folderId");
			String lsSharedEntityId = getSharingIdForFolder(loObj, lsFolderId);
			lsBfWhereClause.append(" AND (DOC.SHARED_ENTITY_ID = '");
			lsBfWhereClause.append(lsSharedEntityId);
			lsBfWhereClause.append("' OR FO.SHARED_ENTITY_ID = '");
			lsBfWhereClause.append(lsSharedEntityId);
			lsBfWhereClause.append("') ");

		}

	}

	/**
	 * @param aoFilterMap
	 * @param lsBfWhereClause
	 */
	private static void addFolderPathOrFolderId(HashMap aoFilterMap, StringBuffer lsBfWhereClause)
	{

		if (null != aoFilterMap.get("ControllerName")
				&& aoFilterMap.get("ControllerName").toString().equalsIgnoreCase("selectOrgnization")
				&& (null != aoFilterMap.get("parentId") && aoFilterMap.get("parentId").toString()
						.equalsIgnoreCase("root")))
		{
			lsBfWhereClause.append("rcr.head INSUBFOLDER");
		}
		else
		{
			lsBfWhereClause.append("rcr.head INFOLDER");
		}
		lsBfWhereClause.append(" '");
		if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH))
		{
			lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_PATH));
		}
		else
		{
			lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_ID));
		}
		lsBfWhereClause.append("'");
		if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
				&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(HHSR5Constants.RECYCLE_BIN))
		{
			lsBfWhereClause
					.append(" AND ((FO.DELETE_FLAG = 2 OR DOC.DELETE_FLAG = 2 ) OR (FO.IS_ARCHIVE = true OR DOC.IS_ARCHIVE = true))");
		}
		else
		{
			lsBfWhereClause.append(" AND (FO.DELETE_FLAG = 0 OR DOC.DELETE_FLAG  = 0) ");
		}
	}

	/**
	 * @param aoFilterMap
	 * @return
	 */
	private static String createWhereClauseForHelp(HashMap aoFilterMap)
	{
		StringBuffer loQueryString = new StringBuffer();
		loQueryString.append(" where doc.DOC_CATEGORY = 'HELP' ");
		if (null != aoFilterMap.get("HELP_CATEGORY") && !aoFilterMap.get("HELP_CATEGORY").toString().isEmpty())
		{
			loQueryString.append(" AND DOC.HELP_CATEGORY = '");
			loQueryString.append((String) aoFilterMap.get("HELP_CATEGORY")).append("'");

		}
		if (null != aoFilterMap.get("HELP_DOCUMENT_FOR_AGENCY")
				&& !aoFilterMap.get("HELP_DOCUMENT_FOR_AGENCY").toString().isEmpty())
		{
			loQueryString.append(" AND DOC.HELP_DOCUMENT_FOR_AGENCY =");
			loQueryString.append(aoFilterMap.get("HELP_DOCUMENT_FOR_AGENCY"));
		}
		if (null != aoFilterMap.get("HELP_DOCUMENT_FOR_PROVIDER")
				&& !aoFilterMap.get("HELP_DOCUMENT_FOR_PROVIDER").toString().isEmpty())
		{
			loQueryString.append(" AND DOC.HELP_DOCUMENT_FOR_PROVIDER =");
			loQueryString.append(aoFilterMap.get("HELP_DOCUMENT_FOR_PROVIDER"));
		}

		if (!loQueryString.toString().endsWith("AND"))
		{
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
					&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(HHSR5Constants.RECYCLE_BIN))
			{
				loQueryString.append(" AND DOC.DELETE_FLAG  = 2 ");
			}
			else
			{
				loQueryString.append(" AND DOC.DELETE_FLAG  = 0 ");
			}
		}
		else
		{
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
					&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(HHSR5Constants.RECYCLE_BIN))
			{
				loQueryString.append(" DOC.DELETE_FLAG  = 2 ");
			}
			else
			{
				loQueryString.append(" DOC.DELETE_FLAG  = 0 ");
			}
		}
		return loQueryString.toString();
	}

	/**
	 * @param aoFilterMap
	 * @param asConstantName
	 * @param abFilterIncluded
	 * @param lsBfWhereClause
	 * @param loHmExcepRequiredProp
	 * @param liCounter
	 * @param lbProviderNycFlag
	 * @param lbOrCondition
	 * @param loDbList
	 * @param lsEntityType
	 * @return
	 * @throws ApplicationException
	 */
	private static StringBuffer createWhereClauseForFilterFlag(HashMap aoFilterMap, String asConstantName,
			boolean abFilterIncluded, StringBuffer lsBfWhereClause, HashMap loHmExcepRequiredProp, int liCounter,
			boolean lbProviderNycFlag, boolean lbOrCondition, List<String> loDbList, String lsEntityType)
			throws ApplicationException
	{
		Iterator loFilterKeyItr;
		String lsFilterKey;
		lsBfWhereClause = createWhereClauseforSearchFields(aoFilterMap, asConstantName, lsBfWhereClause, loDbList,
				lsEntityType, false, null, null);
		if (String.valueOf(aoFilterMap.get(HHSR5Constants.IS_FILTER)).equalsIgnoreCase("false")
				&& (null == loDbList || loDbList.isEmpty()))
		{
			lsBfWhereClause.append("This INFOLDER");
			lsBfWhereClause.append(" '");
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH))
			{

				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_PATH));

			}
			else
			{
				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_ID));
			}

			lsBfWhereClause.append("'");
			lsBfWhereClause.append(" AND ");
		}
		else if (null == loDbList || loDbList.isEmpty())
		{
			lsBfWhereClause.append("rcr.head INSUBFOLDER");
			lsBfWhereClause.append(" '");
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH))
			{

				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_PATH));

			}
			else
			{
				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_ID));
			}

			lsBfWhereClause.append("'");
			lsBfWhereClause.append(" AND ");
		}

		return lsBfWhereClause;
	}

	/**
	 * This method is updated in release 5 to create where clause while adding
	 * document from vault
	 * @param aoFilterMap contains filter keys
	 * @param asConstantName
	 * @param lsBfWhereClause
	 * @param lsSelectFromDV
	 * @return
	 * @throws ApplicationException
	 */
	private static StringBuffer createWhereClauseForSelectFromVault(HashMap aoFilterMap, String asConstantName,
			StringBuffer lsBfWhereClause, String lsSelectFromDV) throws ApplicationException
	{
		if ((null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH) && !((String) aoFilterMap
				.get(HHSR5Constants.FOLDER_PATH)).isEmpty())
				|| null != aoFilterMap.get(HHSR5Constants.FOLDER_ID)
				&& !((String) aoFilterMap.get(HHSR5Constants.FOLDER_ID)).isEmpty())
		{
			if (null != aoFilterMap.get(HHSConstants.SELECT_ALL_FLAG)
					&& !((String) aoFilterMap.get(HHSConstants.SELECT_ALL_FLAG)).isEmpty())
				lsBfWhereClause.append(" This INSUBFOLDER");
			else
				lsBfWhereClause.append(" This INFOLDER");
			lsBfWhereClause.append(" '");
			if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH))
			{

				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_PATH));

			}
			else
			{
				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.FOLDER_ID));
			}

			lsBfWhereClause.append("' AND ");
		}
		createWhereClauseforSearchFields(aoFilterMap, asConstantName, lsBfWhereClause, null, null, false, null,
				lsSelectFromDV);
		return lsBfWhereClause;
	}

	/**
	 * @param aoFilterMap
	 * @param asConstantName
	 * @param lsBfWhereClause
	 * @return
	 * @throws ApplicationException
	 */
	private static StringBuffer createWhereClauseForSharingFlag(HashMap aoFilterMap, String asConstantName,
			StringBuffer lsBfWhereClause, String lsSharedFlag, boolean lbOrCondition, boolean lbProviderNycFlag,
			Integer liCounter, boolean abFilterIncluded, boolean abFindOrgDocFlag, List<String> aoDBList,
			String asEntityType) throws ApplicationException
	{
		// Creating where clause for search fields
		lsBfWhereClause = createWhereClauseforSearchFields(aoFilterMap, asConstantName, lsBfWhereClause, aoDBList,
				asEntityType, abFindOrgDocFlag, lsSharedFlag, null);
		boolean lbOnlyTitleAdded = (Boolean) aoFilterMap.get("addedOnlyTitle");
		if (null != lsSharedFlag && lsSharedFlag.equalsIgnoreCase(HHSR5Constants.TRUE))
		{

			if (null != aoFilterMap.get("agencyList"))
			{
				lsBfWhereClause.append(" (DOC.SHARING_FLAG = '1' OR DOC.SHARING_FLAG = '2')");
				lsBfWhereClause.append(" AND DOC.ORGANIZATION_ID IS NOT NULL");
			}
		}
		return lsBfWhereClause;
	}

	/**
	 * @param aoFilterMap
	 * @param asConstantName
	 * @param lsBfWhereClause
	 * @return
	 * @throws ApplicationException
	 */
	private static StringBuffer createWhereClauseforSearchFields(HashMap aoFilterMap, String asConstantName,
			StringBuffer lsBfWhereClause, List<String> aoDbList, String asEntityType, boolean abFindOrgDocFlag,
			String lsSharedFlag, String asSelectFromDV) throws ApplicationException
	{
		boolean lbFolderSearch = true;
		if (null != asEntityType && asEntityType.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_CORPORATE_STRUCTURE))
		{
			aoFilterMap.put(HHSR5Constants.DOC_TYPE, P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE);
		}
		if ((null != aoFilterMap.get(HHSR5Constants.DOC_TYPE) && !aoFilterMap.get(HHSR5Constants.DOC_TYPE).toString()
				.isEmpty())
				|| (null != aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY) && !aoFilterMap
						.get(P8Constants.PROPERTY_CE_DOC_CATEGORY).toString().isEmpty())
				|| null != aoFilterMap.get("agencyList")
				|| null != aoDbList
				&& !aoDbList.isEmpty()
				|| (null != asSelectFromDV && asSelectFromDV.equalsIgnoreCase("true")))
		{
			lbFolderSearch = false;
		}

		if (null != aoDbList && !aoDbList.isEmpty())
		{
			lsBfWhereClause.append(" DOC.Id IN (");
			for (Iterator iterator = aoDbList.iterator(); iterator.hasNext();)
			{
				String lsId = (String) iterator.next();

				lsBfWhereClause.append("'" + lsId + "'");
				if (iterator.hasNext())
				{
					lsBfWhereClause.append(",");
				}

			}
			lsBfWhereClause.append(") AND ");
		}
		if (null != aoFilterMap.get("agencyList"))
		{
			String[] loArray = (String[]) aoFilterMap.get("agencyList");
			List loTempList = Arrays.asList(loArray);
			Iterator loItr = loTempList.iterator();
			lsBfWhereClause.append("(SHR.HHS_AGENCY_ID IN(");
			while (loItr.hasNext())
			{
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(loItr.next() + "'");
				if (loItr.hasNext())
				{
					lsBfWhereClause.append(",");
				}
			}

			lsBfWhereClause.append(") OR ");
			lsBfWhereClause.append("SHR1.HHS_AGENCY_ID IN(");
			Iterator loItr1 = loTempList.iterator();
			while (loItr1.hasNext())
			{
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(loItr1.next() + "'");
				if (loItr1.hasNext())
				{
					lsBfWhereClause.append(",");
				}
			}
			lsBfWhereClause.append(")) AND ");

		}
		if (null != aoFilterMap.get(HHSR5Constants.DOC_TYPE)
				&& !aoFilterMap.get(HHSR5Constants.DOC_TYPE).toString().isEmpty())
		{
			String lsDocType = aoFilterMap.get(HHSR5Constants.DOC_TYPE).toString();
			if (lsDocType.contains(","))
			{
				String[] loDocTypeArray = lsDocType.split(",");
				lsBfWhereClause.append("(");
				for (int liCount = 0; liCount < loDocTypeArray.length; liCount++)
				{
					lsBfWhereClause.append("DOC.DOC_TYPE = '");
					lsBfWhereClause.append(loDocTypeArray[liCount]).append("'");
					if (liCount < loDocTypeArray.length - 1)
						lsBfWhereClause.append(" OR ");

				}
				lsBfWhereClause.append(")");
			}
			else
			{
				lsBfWhereClause.append("DOC.DOC_TYPE = '");
				lsBfWhereClause.append(aoFilterMap.get(HHSR5Constants.DOC_TYPE)).append("'");
			}
			lsBfWhereClause.append(" AND ");
		}
		if ((null != aoFilterMap.get(HHSR5Constants.IS_SHARED) && Boolean.valueOf(aoFilterMap.get(
				HHSR5Constants.IS_SHARED).toString()))
				|| (null != lsSharedFlag && !lsSharedFlag.isEmpty() && !lsSharedFlag.equalsIgnoreCase("false") && abFindOrgDocFlag)
				|| (null != aoFilterMap.get("HHSProviderID") && !aoFilterMap.get("HHSProviderID").toString().isEmpty()))
		{
			if (lbFolderSearch)
			{
				lsBfWhereClause
						.append(" (DOC.SHARING_FLAG = '1' OR DOC.SHARING_FLAG = '2' OR FO.SHARING_FLAG = '2') AND ");
			}
			else
			{
				lsBfWhereClause.append(" (DOC.SHARING_FLAG = '1' OR DOC.SHARING_FLAG = '2') AND ");
			}
			if (null != aoFilterMap.get("HHSProviderID") && !aoFilterMap.get("HHSProviderID").toString().isEmpty())
			{
				lsBfWhereClause.append("(SHR.HHS_AGENCY_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("HHSProviderID"));
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(" OR SHR.HHSProviderID='");
				lsBfWhereClause.append(aoFilterMap.get("HHSProviderID"));
				lsBfWhereClause.append("' OR SHR1.HHS_AGENCY_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("HHSProviderID"));
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(" OR SHR1.HHSProviderID='");
				lsBfWhereClause.append(aoFilterMap.get("HHSProviderID"));
				lsBfWhereClause.append("') AND ");

			}
			if (abFindOrgDocFlag && aoFilterMap.get("PROVIDER_ID") != null
					&& !((String) aoFilterMap.get("PROVIDER_ID")).isEmpty()
					&& !aoFilterMap.get("PROVIDER_ID").toString().equalsIgnoreCase(HHSR5Constants.USER_CITY))
			{

				lsBfWhereClause.append("(SHR.HHS_AGENCY_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(" OR SHR.HHSProviderID='");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("' OR SHR1.HHS_AGENCY_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("'");
				lsBfWhereClause.append(" OR SHR1.HHSProviderID='");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("') AND ");

			}
			if (!abFindOrgDocFlag
					&& aoFilterMap.get("PROVIDER_ID") != null
					&& !((String) aoFilterMap.get("PROVIDER_ID")).isEmpty()
					&& (aoFilterMap.get("sharedSearchOrgId") == null || ((String) aoFilterMap.get("sharedSearchOrgId"))
							.isEmpty()))
			{
				lsBfWhereClause.append("(SHR.HHS_SHARED_BY = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("' OR SHR1.HHS_SHARED_BY = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("') AND ");
			}
			else if (!abFindOrgDocFlag
					&& (aoFilterMap.get("sharedSearchOrgId") != null && !((String) aoFilterMap.get("sharedSearchOrgId"))
							.isEmpty()))
			{
				lsBfWhereClause.append("(SHR.HHS_SHARED_BY = '");
				lsBfWhereClause.append(aoFilterMap.get("sharedSearchOrgId"));
				lsBfWhereClause.append("' OR SHR1.HHS_SHARED_BY = '");
				lsBfWhereClause.append(aoFilterMap.get("sharedSearchOrgId"));
				lsBfWhereClause.append("') AND ");
			}

		}
		if (null != aoFilterMap.get(HHSR5Constants.IS_SHARED)
				&& !Boolean.valueOf(aoFilterMap.get(HHSR5Constants.IS_SHARED).toString()))
		{
			if (lbFolderSearch)
			{
				lsBfWhereClause.append(" (DOC.SHARING_FLAG = '0' OR FO.SHARING_FLAG = '0') ");
			}
			else
			{
				lsBfWhereClause.append(" DOC.SHARING_FLAG = '0' ");
			}
			lsBfWhereClause.append("AND ");
		}
		if (null != aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY)
				&& !aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY).toString().isEmpty())
		{
			lsBfWhereClause.append("DOC.DOC_CATEGORY = '");
			lsBfWhereClause.append(aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
			lsBfWhereClause.append("' AND ");
		}
		// Release 5: Added for Sample documents in FAQ
		if (null != aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY)
				&& !aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY).toString().isEmpty())
		{
			lsBfWhereClause.append("DOC.SAMPLE_CATEGORY = '");
			lsBfWhereClause.append(aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
			lsBfWhereClause.append("' AND ");
		}
		if (null != aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE)
				&& !aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE).toString().isEmpty())
		{
			lsBfWhereClause.append("DOC.SAMPLE_TYPE = '");
			lsBfWhereClause.append(aoFilterMap.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
			lsBfWhereClause.append("' AND ");
		}
		// Release 5: Added for Sample documents end
		if (null != aoFilterMap.get(P8Constants.PROPERTY_MODIFIED_FROM)
				&& !aoFilterMap.get(P8Constants.PROPERTY_MODIFIED_FROM).toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, P8Constants.PROPERTY_MODIFIED_FROM,
					asConstantName, aoFilterMap, lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get(P8Constants.PROPERTY_MODIFIED_TO)
				&& !aoFilterMap.get(P8Constants.PROPERTY_MODIFIED_TO).toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, P8Constants.PROPERTY_MODIFIED_TO,
					asConstantName, aoFilterMap, lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get(HHSR5Constants.PROPERTY_DELETED_FROM)
				&& !aoFilterMap.get(HHSR5Constants.PROPERTY_DELETED_FROM).toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, HHSR5Constants.PROPERTY_DELETED_FROM,
					asConstantName, aoFilterMap, lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get(HHSR5Constants.PROPERTY_DELETED_TO)
				&& !aoFilterMap.get(HHSR5Constants.PROPERTY_DELETED_TO).toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, HHSR5Constants.PROPERTY_DELETED_TO,
					asConstantName, aoFilterMap, lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get("SHARED_FROM") && !aoFilterMap.get("SHARED_FROM").toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, "SHARED_FROM", "SHR", aoFilterMap,
					lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get("SHARED_TO") && !aoFilterMap.get("SHARED_TO").toString().isEmpty())
		{
			lsBfWhereClause = createWhereClauseAppendDate(lsBfWhereClause, "SHARED_TO", "SHR", aoFilterMap,
					lbFolderSearch);
			lsBfWhereClause.append(" AND ");
		}
		if (null != aoFilterMap.get("IS_SYSTEM_DOCS") && !aoFilterMap.get("IS_SYSTEM_DOCS").toString().isEmpty())
		{
			lsBfWhereClause.append("DOC.IS_SYSTEM_DOCS =");
			lsBfWhereClause.append(aoFilterMap.get("IS_SYSTEM_DOCS"));
			lsBfWhereClause.append(" AND ");
		}

		if (null != aoFilterMap.get(HHSConstants.DOC_TITLE)
				&& !aoFilterMap.get(HHSConstants.DOC_TITLE).toString().isEmpty())
		{

			if (lbFolderSearch)
			{
				lsBfWhereClause.append("( ");
				lsBfWhereClause.append("DOC.DocumentTitle like '%");
				lsBfWhereClause.append(aoFilterMap.get(HHSConstants.DOC_TITLE));
				if (null != aoFilterMap.get(HHSR5Constants.FOLDER_PATH)
						&& String.valueOf(aoFilterMap.get(HHSR5Constants.FOLDER_PATH)).contains(
								HHSR5Constants.RECYCLE_BIN))
				{

					lsBfWhereClause.append("%' OR FO.ORIGINAL_FOLDER_NAME like '%");
					lsBfWhereClause.append(aoFilterMap.get(HHSConstants.DOC_TITLE));
					lsBfWhereClause.append("%' ) AND ");

				}
				else
				{
					lsBfWhereClause.append("%' OR FO.FolderName like '%");
					lsBfWhereClause.append(aoFilterMap.get(HHSConstants.DOC_TITLE));
					lsBfWhereClause.append("%' ) AND ");
				}

			}
			else
			{
				lsBfWhereClause.append("DOC.DocumentTitle like '%");
				lsBfWhereClause.append(aoFilterMap.get(HHSConstants.DOC_TITLE));
				lsBfWhereClause.append("%' AND ");
			}
		}
		if (aoFilterMap.get("PROVIDER_ID") != null
				&& !((String) aoFilterMap.get("PROVIDER_ID")).isEmpty()
				&& ((null != lsSharedFlag && lsSharedFlag.equalsIgnoreCase("false")) || (null != aoFilterMap
						.get(HHSR5Constants.IS_SHARED) && Boolean.valueOf(aoFilterMap.get(HHSR5Constants.IS_SHARED)
						.toString())))
				&& (aoFilterMap.get("sharedSearchOrgId") == null || ((String) aoFilterMap.get("sharedSearchOrgId"))
						.isEmpty()) && !abFindOrgDocFlag)
		{
			if (lbFolderSearch)
			{
				lsBfWhereClause.append(" (DOC.PROVIDER_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("' OR FO.PROVIDER_ID='");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("')");
			}
			else
			{
				lsBfWhereClause.append(" (DOC.PROVIDER_ID = '");
				lsBfWhereClause.append(aoFilterMap.get("PROVIDER_ID"));
				lsBfWhereClause.append("')");
			}
		}
		aoFilterMap.put("addedOnlyTitle", lbFolderSearch);
		return lsBfWhereClause;
	}

	/**
	 * @param aoFilterMap
	 */
	private static void removeEntriesFromFilterMap(HashMap aoFilterMap)
	{
		aoFilterMap.remove("presentOrgId");
		aoFilterMap.remove("amendmentepinTitle");
		aoFilterMap.remove("ENTITY_TYPE");
		aoFilterMap.remove("procurementId");
		aoFilterMap.remove("CustomOrg");
		aoFilterMap.remove("awardepinTitle");
		aoFilterMap.remove("organizationId");
		aoFilterMap.remove("invoiceNumber");
		aoFilterMap.remove("submittedFrom");
		aoFilterMap.remove("submittedTo");
		aoFilterMap.remove("dBReturnList");
		aoFilterMap.remove("sharedFlag");
		aoFilterMap.remove("contractawardepinTitle");
		aoFilterMap.remove("selectVault");
		aoFilterMap.remove(HHSR5Constants.IS_FIND_ORG_DOC_FLAG);
	}

	/**
	 * Updated for 3.1.0, enhancement 6021 - Added checks for char 500 extension
	 * This method is used for appending to the string buffer
	 * lsAppendedBfWhereClause based on modified dates
	 * 
	 * @param asBfWhereClause : A StringBuffer containing the where clause
	 * @param asFilterKey : A String containing the filter key
	 * @param asConstantName : A String containing the constant name
	 * @param aoFilterMap : A hash-map containing the where clause filter keys
	 * @return : A StringBuffer of the where clause
	 * @throws ApplicationException
	 */
	public static StringBuffer createWhereClauseAppendDate(StringBuffer asBfWhereClause, String asFilterKey,
			String asConstantName, HashMap aoFilterMap, boolean abFolderSearch) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered P8ContentOperations.createWhereClauseAppendDate() ");
		String lsDate = "";
		StringBuffer lsAppendedBfWhereClause = asBfWhereClause;
		String lsDateArray[] = aoFilterMap.get(asFilterKey).toString().split("/");
		Calendar loCalendar = new GregorianCalendar(Integer.valueOf(lsDateArray[2]),
				Integer.valueOf(lsDateArray[0]) - 1, Integer.valueOf(lsDateArray[1]));
		
		//Begin--modified for qc_9184
		Date now = loCalendar.getTime();
		Date beginTimeOfDate=DateUtil.setBeginTimeOfDate(now);
		Date endTimeOfDate=DateUtil.setEndTimeOfDate(now);
		//End--modified for qc_9184
		
		if (asFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_MODIFIED_FROM))
		{
			
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(beginTimeOfDate);
			//End--modified for qc_9184
			if (abFolderSearch)
			{
				lsAppendedBfWhereClause.append("(DOC.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(" OR FO.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(")");
			}
			else
			{
				lsAppendedBfWhereClause.append("DOC.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
			}
		}
		else if (asFilterKey.equalsIgnoreCase(HHSR5Constants.SHARED_FROM))
		{
			
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(beginTimeOfDate);
			//End--modified for qc_9184

			// End of changes done for defect QC: 6162
			lsAppendedBfWhereClause.append(asConstantName);
			lsAppendedBfWhereClause.append(".");
			lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_DATE_CREATED);
			lsAppendedBfWhereClause.append(" >= ");
			lsAppendedBfWhereClause.append(lsDate);
		}

		else if (asFilterKey.equalsIgnoreCase(HHSR5Constants.SHARED_TO))
		{
			
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(endTimeOfDate);
			//End--modified for qc_9184
			
			// End of changes done for defect QC: 6162
			lsAppendedBfWhereClause.append(asConstantName);
			lsAppendedBfWhereClause.append(".");
			lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_DATE_CREATED);
			lsAppendedBfWhereClause.append(" <= ");
			lsAppendedBfWhereClause.append(lsDate);
		}
		else if (asFilterKey.equalsIgnoreCase(P8Constants.PROPERTY_MODIFIED_TO))
		{
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(endTimeOfDate);
			//End--modified for qc_9184
			
			if (abFolderSearch)
			{
				// End of changes done for defect QC: 6162
				lsAppendedBfWhereClause.append("(DOC.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(" OR FO.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(")");
			}
			else
			{
				lsAppendedBfWhereClause.append("DOC.");
				lsAppendedBfWhereClause.append(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
			}
		}
		else if (asFilterKey.equalsIgnoreCase(HHSR5Constants.DELETED_FROM))
		{
			
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(beginTimeOfDate);
			//End--modified for qc_9184
			
			if (abFolderSearch)
			{
				lsAppendedBfWhereClause.append("(DOC.");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(" OR FO.");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(")");
			}
			else
			{
				lsAppendedBfWhereClause.append(asConstantName);
				lsAppendedBfWhereClause.append(".");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" >= ");
				lsAppendedBfWhereClause.append(lsDate);
			}

		}
		else if (asFilterKey.equalsIgnoreCase(HHSR5Constants.DELETED_TO))
		{
					
			//Begin--modified for qc_9184			
			lsDate = DateUtil.getFilenetUtcDateTimeFormat(endTimeOfDate);
			//End--modified for qc_9184
			
			if (abFolderSearch)
			{
				lsAppendedBfWhereClause.append("(DOC.");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(" OR FO.");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
				lsAppendedBfWhereClause.append(")");
			}
			else
			{
				lsAppendedBfWhereClause.append(asConstantName);
				lsAppendedBfWhereClause.append(".");
				lsAppendedBfWhereClause.append(HHSR5Constants.DELETED_DATE);
				lsAppendedBfWhereClause.append(" <= ");
				lsAppendedBfWhereClause.append(lsDate);
			}
		}
		// Modified for 3.1.0, adding check for new CHAR500 type documents
		else
		{
			if (aoFilterMap.get(asFilterKey) instanceof String)
			{
				lsAppendedBfWhereClause.append(asConstantName);
				lsAppendedBfWhereClause.append(".");
				lsAppendedBfWhereClause.append(asFilterKey);
				lsAppendedBfWhereClause.append(" = '");
				lsAppendedBfWhereClause.append(aoFilterMap.get(asFilterKey));
				lsAppendedBfWhereClause.append("'");
			}
			else
			{
				lsAppendedBfWhereClause.append(asConstantName);
				lsAppendedBfWhereClause.append(".");
				lsAppendedBfWhereClause.append(asFilterKey);
				lsAppendedBfWhereClause.append(" = ");
				lsAppendedBfWhereClause.append(aoFilterMap.get(asFilterKey));
			}
		}

		LOG_OBJECT.Info("Exited EnahncedP8ContentOperations.createWhereClauseAppendDate() ");
		return lsAppendedBfWhereClause;

	}

	/**
	 * This method fetches document properties for all versions
	 * 
	 * @param aoOS filenet object store session
	 * @param asDocId Document Id as input
	 * @param aoRequiredMap hash map containing the required properties of
	 *            document to be fetched
	 * @return HashMap map of document properties
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static HashMap<String, HashMap> getDocPropertiesAllVersions(ObjectStore aoOS, String asDocId,
			HashMap aoRequiredMap) throws ApplicationException
	{
		Document loNewDoc;
		VersionableSet loAllVersionSet = null;
		Iterator<Object> loVersionItr = null;
		HashMap<String, HashMap> loResultMap = null;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, asDocId);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		loHmExcepRequiredProp.put("aoRequiredMap", aoRequiredMap);
		LOG_OBJECT.Info("Entered EnahncedP8ContentOperations.getDocPropertiesAllVersions() with parameters::"
				+ loHmExcepRequiredProp.toString());

		Properties loDocProperty = null;
		try
		{
			loResultMap = new HashMap<String, HashMap>();
			loNewDoc = Factory.Document.fetchInstance(aoOS, asDocId, null);
			loAllVersionSet = loNewDoc.get_Versions();
			loVersionItr = loAllVersionSet.iterator();

			while (loVersionItr.hasNext())
			{
				Versionable loVer = (Versionable) loVersionItr.next();
				Document loVerdoc = (Document) loVer;
				loDocProperty = loVerdoc.getProperties();
				Iterator loDocPropIterator = loDocProperty.iterator();
				HashMap loPropMap = new HashMap();
				while (loDocPropIterator.hasNext())
				{
					Property loProp = (Property) loDocPropIterator.next();
					if (aoRequiredMap.containsKey(loProp.getPropertyName()))
					{
						loPropMap.put(loProp.getPropertyName(), loProp.getObjectValue());
					}

				}
				loResultMap.put(loVerdoc.get_MajorVersionNumber().toString(), loPropMap);
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting properties for All version",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting properties for All version", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocPropertiesAllVersions()");
		return loResultMap;
	}

	/**
	 * This method fetches document content
	 * 
	 * @param aoOS filenet object store session
	 * @param asDocId Document Id as input
	 * @return HashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentContent(ObjectStore aoOS, String asDocId) throws ApplicationException
	{
		HashMap loOutputHashMap = new HashMap();
		loOutputHashMap.put("MimeType", "");
		loOutputHashMap.put("ContentElements", "");
		loOutputHashMap.put("FILE_TYPE", "");

		//Start Emergency Build 4.0.1 defect 8365
		loOutputHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);
		//End Emergency Build 4.0.1 defect 8365
		InputStream loContent = null;
		Document loDocObject;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, asDocId);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentContent() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{

			loDocObject = Factory.Document.fetchInstance(aoOS, asDocId, createPropertyFilter(loOutputHashMap));
			loContent = loDocObject.accessContentStream(0);
			Properties loDocumentProperty = loDocObject.getProperties();
			loOutputHashMap.put("ContentElements", loContent);
			loOutputHashMap.put("MimeType", loDocumentProperty.getObjectValue("MimeType"));
			loOutputHashMap.put("FileType", loDocumentProperty.getObjectValue("FILE_TYPE"));
			//Start Emergency Build 4.0.1 defect 8365
			loOutputHashMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, loDocumentProperty.getStringValue(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			//End Emergency Build 4.0.1 defect 8365
		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoErEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentContent()");
		return loOutputHashMap;
	}

	/**
	 * This method gives document content by type
	 * 
	 * @param aoOS filenet object store session
	 * @param aoDocTypeXML document type XML configuration DOM
	 * @param asDocType Type of document
	 * @return HashMap loOutput map containing the content by type
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentContentByType(ObjectStore aoOS, Object aoDocTypeXML, String asDocType)
			throws ApplicationException
	{
		InputStream loContent = null;
		Document loDocObject;
		HashMap loHmExcepRequiredProp = new HashMap();
		String lsDocFolderPath = null;
		Folder loFolderObj = null;
		DocumentSet loDocSet = null;
		Iterator loDocIter = null;
		HashMap loOutput = new HashMap();
		loHmExcepRequiredProp.put("documentType", asDocType);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		loHmExcepRequiredProp.put("folderpath", lsDocFolderPath);
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentContentByType() with parameters::"
				+ loHmExcepRequiredProp.toString());

		try
		{
			lsDocFolderPath = getFolderPath(aoDocTypeXML, null, asDocType, null, P8Constants.APPLICATION_CITY_ORG);
			loFolderObj = Factory.Folder.fetchInstance(aoOS, lsDocFolderPath, null);
			if (null != loFolderObj)
			{
				loDocSet = loFolderObj.get_ContainedDocuments();
				loDocIter = loDocSet.iterator();
				if (loDocIter.hasNext())
				{
					loDocObject = (Document) loDocIter.next();
					loContent = loDocObject.accessContentStream(0);
					loOutput.put(loDocObject.get_Id().toString(), loContent);
				}
			}
			else
			{
				ApplicationException loAppex = new ApplicationException(
						"Error While getting document content By Type No Such folder Exists in Filenet");
				loAppex.setContextData(loHmExcepRequiredProp);
				throw loAppex;
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocumentContentByType()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content By Type",
					aoErEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content By Type", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content By Type",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content By Type", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentContentByType()");
		return loOutput;
	}

	/**
	 * This method fetches document content
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asPrintViewId Id of the print view
	 * @param asOrgId the Id of the organization
	 * @return InputStream print document content
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public InputStream getPrintDocumentContent(ObjectStore aoObjStore, String asPrintViewId, String asOrgId)
			throws ApplicationException
	{
		InputStream loContent = null;
		Document loDocObject;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put("asPrintViewId", asPrintViewId);
		loHmExcepRequiredProp.put("organizationId", asOrgId);
		loHmExcepRequiredProp.put("objectstore", aoObjStore.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.getPrintDocumentContent() with parameters::"
				+ loHmExcepRequiredProp.toString());

		StringBuffer loSQLQueryBuffer = new StringBuffer();
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			loSQLQueryBuffer.append("SELECT * FROM ");
			loSQLQueryBuffer.append(P8Constants.PROPERTY_CE_PPRINTER_FRINDLY_TYPE);
			loSQLQueryBuffer.append(" DOC WHERE DOC.");
			loSQLQueryBuffer.append(P8Constants.PROPERTY_CE_ORGANIZATION_ID);
			loSQLQueryBuffer.append("='");
			loSQLQueryBuffer.append(asOrgId);
			loSQLQueryBuffer.append("' AND DOC.");
			loSQLQueryBuffer.append(P8Constants.PROPERTY_CE_PRINT_VIEW_ID);
			loSQLQueryBuffer.append("='");
			loSQLQueryBuffer.append(asPrintViewId);
			loSQLQueryBuffer.append("' ORDER BY DateLastModified DESC");

			loSqlObject.setMaxRecords(1);
			loSqlObject.setQueryString(loSQLQueryBuffer.toString());
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loRowSet.iterator();
			if (loIt.hasNext())
			{
				loDocObject = (Document) loIt.next();
				loContent = loDocObject.accessContentStream(0);
			}
		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoErEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getPrintDocumentContent() ");
		return loContent;
	}

	/**
	 * This method deletes the document
	 * 
	 * @param aoOS filenet object store session
	 * @param asDocId document Id as input
	 * @return boolean variable specifying whether or not the deletion is
	 *         successful
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean deleteDocument(ObjectStore aoOS, String asDocId) throws ApplicationException
	{

		boolean lbIsDeleted = true;
		Document loDocObject;
		VersionSeries loDocVersionSeries = null;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, asDocId);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.deleteDocument() with parameters::"
				+ loHmExcepRequiredProp.toString());

		SearchSQL loSqlObject = new SearchSQL();
		String lsCustomObjectClassName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_SHARED_DOC_PROVIDER_CUSTOM_OBJECT);
		try
		{
			HashMap loDocList = new HashMap();
			List loProviderList = null;
			loDocList.put(asDocId, loProviderList);
			loDocObject = Factory.Document.fetchInstance(aoOS, asDocId, null);
			loDocVersionSeries = loDocObject.get_VersionSeries();
			loDocVersionSeries.delete();
			loDocVersionSeries.save(RefreshMode.NO_REFRESH);

			// prepare query to get all the documents objects shared with the
			// provided provider
			String lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_DOC_ID + " FROM "
					+ lsCustomObjectClassName + " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + " =  '" + asDocId
					+ "'";
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for retrieving custom objects
			SearchScope loSearchScope = new SearchScope(aoOS);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				CustomObject loObj = (CustomObject) loIt.next();
				loObj.delete();
				loObj.save(RefreshMode.NO_REFRESH);
			}

		}
		catch (EngineRuntimeException aoEx)
		{
			lbIsDeleted = false;
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.deleteDocument()::", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			lbIsDeleted = false;
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.deleteDocument()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.deleteDocument() ");
		return lbIsDeleted;
	}

	/**
	 * This method sets document properties
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param asDocId document Id as input
	 * @param aoPropertyMap property map as input
	 * @param asDocClass class of the document
	 * @param asDocType type of the document
	 * @return boolean variable specifying whether or not the doc properties are
	 *         set
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean setDocProperties(ObjectStore aoObjStr, String asDocId, HashMap aoPropertyMap, String asDocType)
			throws ApplicationException
	{

		boolean lbFlag = false;
		HashMap loHmExcepRequiredProp = new HashMap();
		String lsDocClass = null;
		loHmExcepRequiredProp.put(HHSR5Constants.AS_DOC_TYPE, asDocType);
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, asDocId);
		loHmExcepRequiredProp.put("objectstore", aoObjStr.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.setDocProperties() with parameters::"
				+ loHmExcepRequiredProp.toString());
		String lsFolderPath = null;
		try
		{
			if (null == asDocId)
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in setDocProperties.Required Parameters are missing. ");

				throw loAppex;
			}
			aoPropertyMap.put(HHSR5Constants.FOLDERS_FILED_IN, "");
			aoPropertyMap.put("ClassDescription", "");
			Document loDoc = Factory.Document.fetchInstance(aoObjStr, asDocId, createPropertyFilter(aoPropertyMap));
			lsDocClass = loDoc.get_ClassDescription().get_SymbolicName();
			Properties loProp = loDoc.getProperties();
			FolderSet loFldrSet = (FolderSet) loProp.getObjectValue(HHSR5Constants.FOLDERS_FILED_IN);
			for (Iterator iterator = loFldrSet.iterator(); iterator.hasNext();)
			{
				Folder type = (Folder) iterator.next();
				if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
				{
					lsFolderPath = type.get_PathName();
				}
			}
			aoPropertyMap.remove("ClassDescription");
			aoPropertyMap.remove(HHSR5Constants.FOLDERS_FILED_IN);
			loDoc = setDocProperties(aoObjStr, loDoc, aoPropertyMap, lsDocClass, asDocType, lsFolderPath, true);
			loDoc.setUpdateSequenceNumber(null);
			loDoc.save(RefreshMode.NO_REFRESH);
			// Added for 7911
			if (StringUtils.isNotBlank(lsFolderPath))
			{
				setParentModifiedDateProperty(aoObjStr, lsFolderPath);
			}
			// Added for 7911
			lbFlag = true;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While setting document property", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDocProperties()::", aoEx);
			throw loAppex;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDocProperties()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While setting document property", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While setting document property", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.setDocProperties()");
		return lbFlag;
	}

	/**
	 * This method creates orderby clause for the query
	 * 
	 * @param aoOrderByMap a map containing values to form order by clause of
	 *            query
	 * @param asConstantName name of the constant is passed
	 * @return String the created 'order-by' clause
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String createOrderByClause(HashMap aoOrderByMap, String asConstantName) throws ApplicationException
	{
		StringBuffer lsBfOrderBy = new StringBuffer();
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put("asConstantName", asConstantName);
		LOG_OBJECT.Info("Entered P8ContentOperations.createOrderByClause() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			if (aoOrderByMap != null && !aoOrderByMap.isEmpty())
			{
				lsBfOrderBy.append(" Order By ");
				int liIteratorSize = aoOrderByMap.keySet().size();
				for (int liOrderCount = 1; liOrderCount <= liIteratorSize; liOrderCount++)
				{
					String lsOrderByClause = (String) aoOrderByMap.get(liOrderCount);
					if (null != lsOrderByClause)
					{
						if (lsOrderByClause.equalsIgnoreCase("HHS_SHARED_BY ASC,SHR"))
						{
							lsOrderByClause = "PROVIDER_ID ASC,DOC";
						}
						else if (lsOrderByClause.equalsIgnoreCase("HHS_SHARED_BY DESC,SHR"))
						{
							lsOrderByClause = "PROVIDER_ID DESC,DOC";
						}
						String[] loTemp = lsOrderByClause.split(",");
						if (liOrderCount < liIteratorSize)
						{
							lsBfOrderBy.append(loTemp[1]);
							lsBfOrderBy.append(".");
							lsBfOrderBy.append(loTemp[0]);
							lsBfOrderBy.append(",");
						}
						else
						{
							lsBfOrderBy.append(loTemp[1]);
							lsBfOrderBy.append(".");
							lsBfOrderBy.append(loTemp[0]);
						}
					}
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While Generating the OrderBy clause", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While Generating the OrderBy clause", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.createOrderByClause()");
		return lsBfOrderBy.toString();
	}

	/**
	 * This method gets current page attributes
	 * 
	 * @param aoUserSession P8UserSession object
	 * @param aoPropertiesMap hash map containing all the properties
	 * @param aoPageIterator the PageIterator object
	 * @return List loListDocumentDetails is the document details list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List getCurrentPageAttributes(P8UserSession aoUserSession, HashMap aoPropertiesMap,
			PageIterator aoPageIterator, Integer liPageSize) throws ApplicationException
	{
		List loListDocumentDetails = new ArrayList();
		HashMap loHmExcepRequiredProp = new HashMap();

		try
		{
			loHmExcepRequiredProp.put("propertymap", aoPropertiesMap);
			loHmExcepRequiredProp.put("pageiterator", aoPageIterator);
			LOG_OBJECT.Info("Entered P8ContentOperations.getCurrentPageAttributes() with parameters::"
					+ loHmExcepRequiredProp.toString());

			if (null == aoPropertiesMap || null == aoPageIterator)
			{
				ApplicationException loAppex = new ApplicationException(
						"Error While getting the current page attributes missing parameters");
				loAppex.setContextData(loHmExcepRequiredProp);
				throw loAppex;
			}
			int liRequiredPage = aoUserSession.getNextPageIndex();
			List<PageMark> loPageMark = aoUserSession.getAllPageMark();

			if (liRequiredPage >= 0)
			{
				if (aoUserSession.getAllPageMark().size() == 0)
				{
					aoPageIterator.nextPage();
					loPageMark.add(aoPageIterator.getPageMark());
				}
				else if (aoUserSession.getAllPageMark().size() <= liRequiredPage)
				{
					if (liRequiredPage % 5 == 0)
					{
						fetchTotalPageCount(null, aoUserSession, null, liPageSize);
					}
					for (int i = aoUserSession.getAllPageMark().size(); i <= liRequiredPage; i++)
					{
						aoPageIterator.nextPage();
						loPageMark.add(aoPageIterator.getPageMark());
					}
				}
				aoPageIterator.reset(aoUserSession.getAllPageMark().get(liRequiredPage));
			}
			if (aoPageIterator.nextPage())
			{
				for (int liCount = 0; liCount < aoPageIterator.getCurrentPage().length; liCount++)
				{
					// Commenting below line for Release 5
					// Document loDoc = (Document)
					// aoPageIterator.getCurrentPage()[liCount];
					HashMap loHmProps = new HashMap();
					// Retrieving property map for the document
					// Properties loProps = loDoc.getProperties();
					RepositoryRow loSet = ((RepositoryRow) aoPageIterator.getCurrentPage()[liCount]);
					Properties loProps = loSet.getProperties();
					Iterator loProprtiesItr = loProps.iterator();

					while (loProprtiesItr.hasNext())
					{
						Property loProp = (Property) loProprtiesItr.next();
						if (loProp.getPropertyName().equalsIgnoreCase(HHSR5Constants.FOLDERS_FILED_IN))
						{
							FolderSet loFldrSet = (FolderSet) loProp.getObjectValue();
							for (Iterator iterator = loFldrSet.iterator(); iterator.hasNext();)
							{
								Folder type = (Folder) iterator.next();
								if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
								{
									loHmProps.put(loProp.getPropertyName(), type.get_PathName());
								}
							}
						}
						else
						{
							// setting value to the output hash map
							loHmProps.put(loProp.getPropertyName(), loProp.getObjectValue());
						}
					}

					loListDocumentDetails.add(loHmProps);
				}

				return loListDocumentDetails;
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getCurrentPageAttributes()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting the current page attributes",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting the current page attributes", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getCurrentPageAttributes()");
		return loListDocumentDetails;
	}

	/**
	 * This method gets the document properties from filenet on the basis of
	 * document Id
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param aoHmRequiredProps hash map specifying the required properties map
	 * @param aoDocumentsIdList list of document-id
	 * @return HashMap map of document properties by id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getBRDcoumentPropertiesById(ObjectStore aoObjectStore, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, HashMap<String, String>> loHmDocumentDetails = new HashMap<String, HashMap<String, String>>();
		loHmReqExceProp.put("aoDocIdList", aoDocumentsIdList);
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		LOG_OBJECT.Info("Entered P8ContentOperations.getBRDcoumentPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		List loProviderList = new ArrayList();
		List<String> loSharedEntityList = new ArrayList<String>();
		StringBuffer lsSharedOrgName = new StringBuffer();

		try
		{
			if (null == aoDocumentsIdList || aoDocumentsIdList.size() <= 0)
			{
				return null;
			}
			//Added [Emergency Build 6.0.1] null check for aoHmRequiredProps
			//Changes done because of failure in ProcurementDocsZipBatch batch
			if(null != aoHmRequiredProps)
			{
				aoHmRequiredProps.put("DOC_TYPE", "DOC_TYPE");
			}
			//End [Emergency Build 6.0.1] 
			PropertyFilter loPF = createPropertyFilter(aoHmRequiredProps);
			for (Iterator loDocIterator = aoDocumentsIdList.iterator(); loDocIterator.hasNext();)
			{
				HashMap loHmProps = new HashMap();
				String lsObjId = (String) loDocIterator.next();
				// Fetching Document Instance from FileNet repository
				Document loDoc = Factory.Document.fetchInstance(aoObjectStore, lsObjId, loPF);
				if (null != loDoc)
				{
					// Retrieving property map for the document
					Properties loProps = loDoc.getProperties();
					Iterator loIt = loProps.iterator();
					while (loIt.hasNext())
					{
						Property loProp = (Property) loIt.next();
						if (loProp.getPropertyName().equalsIgnoreCase(HHSR5Constants.FOLDERS_FILED_IN))
						{
							FolderSet loFldrSet = (FolderSet) loProp.getObjectValue();
							for (Iterator iterator = loFldrSet.iterator(); iterator.hasNext();)
							{
								Folder type = (Folder) iterator.next();
								if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
								{
									loHmProps.put(loProp.getPropertyName(), type.get_PathName());
								}
							}
						}
						else
						{
							// setting value to the output hash map
							loHmProps.put(loProp.getPropertyName(), loProp.getObjectValue());
						}

					}
					loHmDocumentDetails.put(lsObjId, loHmProps);
				}
				else
				{
					loHmDocumentDetails.put(lsObjId, null);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getBRDcoumentPropertiesById()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error getting properties of document", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error getting properties of document", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getBRDcoumentPropertiesById()");
		return loHmDocumentDetails;

	}

	/**
	 * This method is for sharing documents
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param aoDocumentsList list of the document as input
	 * @param aoAgencyProviderMap Agency Provider Map
	 * @param asSharedBy string specifying the shared by Id
	 * @param aoDocTypeMap Map for docType
	 * @param aoReqMap Required Property Map
	 * @return boolean variable true if doc is shared
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean shareDocuments(ObjectStore aoObjStore, List aoDocumentsList, HashMap aoAgencyProviderMap,
			String asSharedBy, HashMap aoReqMap) throws ApplicationException
	{
		boolean lbSetShared = true;
		HashMap loPropertyMap = new HashMap();
		HashMap loHmRequiredProp = new HashMap();
		String loUserOrgType = (String) aoReqMap.get(HHSConstants.LS_USER_ORG_TYPE);
		String loUserOrg = (String) aoReqMap.get(HHSR5Constants.ORG_ID);
		loHmRequiredProp.put("aoShareDocuments", aoDocumentsList);
		loHmRequiredProp.put("aoProviderandagency", aoAgencyProviderMap);
		String lsCustomObjectClassName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_SHARED_DOC_PROVIDER_CUSTOM_OBJECT);
		String lsCustomObjectFolderName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_SHARED_CUSTOM_OBJECT_FOLDER_NAME);
		loHmRequiredProp.put("lsCustomObjectClassName", lsCustomObjectClassName);
		loHmRequiredProp.put("lsCustomObjectFolderName", lsCustomObjectFolderName);
		LOG_OBJECT.Info("Entered P8ContentOperations.shareDocuments() with parameters::" + loHmRequiredProp.toString());

		if (lsCustomObjectClassName == null || lsCustomObjectClassName.equalsIgnoreCase("")
				|| lsCustomObjectFolderName == null || lsCustomObjectFolderName.equalsIgnoreCase("")
				|| aoAgencyProviderMap == null)
		{
			lbSetShared = false;
			ApplicationException loAppex = new ApplicationException(
					"Error in setDocumentsShared method. Required Parameters SHARED_DOC_PROVIDER_CUSTOM_OBJECT,SHARED_CUSTOM_OBJECT_FOLDER_NAME are missing.");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;
		}

		try
		{
			loPropertyMap.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "");
			loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, "");
			loPropertyMap.put(HHSR5Constants.SHARING_FLAG, "");

			// Added for Release5 to check if it is doc or folder
			Iterator loIter = aoDocumentsList.iterator();
			while (loIter.hasNext())
			{
				com.nyc.hhs.model.Document loDocObj = (com.nyc.hhs.model.Document) loIter.next();

				if (null != loDocObj.getDocType() && !loDocObj.getDocType().isEmpty()
						&& !loDocObj.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{

					shareFile(aoObjStore, loDocObj.getDocumentId(), aoAgencyProviderMap, asSharedBy, aoReqMap,
							loPropertyMap, loUserOrgType, loUserOrg, lsCustomObjectClassName, lsCustomObjectFolderName,
							loDocObj.getDocType());
				}
				else
				{
					shareFolder(aoObjStore, loDocObj.getDocumentId(), aoAgencyProviderMap, asSharedBy, aoReqMap,
							loPropertyMap, lsCustomObjectClassName, lsCustomObjectFolderName, loDocObj.getDocumentId(),
							loDocObj.getDocType());
				}
			}
		}
		catch (EngineRuntimeException aoEx)
		{
			lbSetShared = false;
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in setDocumentsShared Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in setDocumentsShared Method", aoEx);
			throw loAppex;
		}
		catch (ApplicationException aoAppex)
		{
			lbSetShared = false;
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.shareDocuments()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			lbSetShared = false;
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in setDocumentsShared Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in setDocumentsShared Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.shareDocuments()");
		return lbSetShared;
	}

	/**
	 * This method is for folder sharing
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param aoFolderId list of the document as input
	 * @param aoAgencyProviderMap Agency Provider Map
	 * @param asSharedBy string specifying the shared by Id
	 * @param aoPropertyMap Map for Folder properties
	 * @param aoReqMap Required Property Map
	 * @param asCustomObjectClassName String
	 * @param asCustomObjectFolderName String
	 * @param asKey String
	 * @param asDocType String
	 * @return boolean variable true if doc is shared
	 * @throws ApplicationException - if any exception occurs
	 */
	private void shareFolder(ObjectStore aoObjStore, String aoFolderId, HashMap aoAgencyProviderMap, String asSharedBy,
			HashMap aoReqMap, HashMap aoPropertyMap, String asCustomObjectClassName, String asCustomObjectFolderName,
			String asKey, String asDocType) throws ApplicationException
	{
		boolean lbIsDocumentSharedBefore;
		String lsDocId;
		List loProvidersIdList;
		List loAgencyIdList;
		CustomObject loCustObj;
		PropertyFilter loPropFilter;
		Folder loFolderObj;
		List<com.nyc.hhs.model.Document> loFolderChildList;
		loProvidersIdList = (List) aoAgencyProviderMap.get("PROVIDER");
		loAgencyIdList = (List) aoAgencyProviderMap.get("AGENCY");
		for (Iterator loIterator = loProvidersIdList.iterator(); loIterator.hasNext();)
		{
			String lsProviderId = (String) loIterator.next();
			// check if the document is already shared with the
			// provider
			lbIsDocumentSharedBefore = getDocumentSharedStatus(lsProviderId, aoFolderId, "PROVIDER", aoObjStore);
			if (!lbIsDocumentSharedBefore)
			{
				loCustObj = Factory.CustomObject.createInstance(aoObjStore, asCustomObjectClassName);

				// Setting custom object properties.
				Properties loProps = loCustObj.getProperties();
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsProviderId);
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID, new Id(aoFolderId));
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_BY_ID, asSharedBy);
				loCustObj.save(RefreshMode.REFRESH);
				fileCustomObjectToFolder(aoObjStore, loCustObj, asCustomObjectFolderName, asCustomObjectClassName);
			}
		}
		for (Iterator loIterator = loAgencyIdList.iterator(); loIterator.hasNext();)
		{
			String lsProviderId = (String) loIterator.next();
			// check if the is already shared with the provider
			lbIsDocumentSharedBefore = getDocumentSharedStatus(lsProviderId, aoFolderId, "AGENCY", aoObjStore);
			if (!lbIsDocumentSharedBefore)
			{
				loCustObj = Factory.CustomObject.createInstance(aoObjStore, asCustomObjectClassName);
				// Setting custom object properties.
				Properties loProps = loCustObj.getProperties();
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, lsProviderId);
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID, new Id(aoFolderId));
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_BY_ID, asSharedBy);
				loCustObj.save(RefreshMode.REFRESH);
				fileCustomObjectToFolder(aoObjStore, loCustObj, asCustomObjectFolderName, asCustomObjectClassName);
			}
		}
		loPropFilter = createPropertyFilter(aoPropertyMap);
		loFolderObj = Factory.Folder.fetchInstance(aoObjStore, aoFolderId, loPropFilter);

		loFolderObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(new Id(aoFolderId));
		loFolderObj.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSConstants.TWO);
		loFolderObj.save(RefreshMode.REFRESH);

		// For parent properties
		String lsParentFolderPath = loFolderObj.getProperties().getStringValue(HHSR5Constants.PATH_NAME);
		String lsParentPath = lsParentFolderPath.substring(0, lsParentFolderPath.lastIndexOf("/"));
		setParentProperty(aoObjStore, lsParentPath, aoReqMap, aoPropertyMap, aoFolderId, asDocType);
		// For Child Properties
		setFolderChildSharingProperty(aoObjStore, aoFolderId, aoPropertyMap, asKey, HHSR5Constants.TWO);

	}

	/**
	 * The method will set the sharing property of children of a folder.
	 * @param aoObjStore
	 * @param aoFolderId as SHARING_ENTITY_ID
	 * @param aoPropertyMap
	 * @param asKey as PARENT folder ID
	 * @param aoSharingFlag as sharing flag
	 * @throws ApplicationException
	 */
	private void setFolderChildSharingProperty(ObjectStore aoObjStore, String aoFolderId, HashMap aoPropertyMap,
			String asKey, String aoSharingFlag) throws ApplicationException
	{
		PropertyFilter loPropFilter;
		List<com.nyc.hhs.model.Document> loFolderChildList;
		//null passed to remove sharing from child folders 4.0.2.0
		loFolderChildList = getChildList(aoObjStore, asKey, HHSR5Constants.INSUBFOLDER, null, null);
		//null passed to remove sharing from child folders 4.0.2.0
		Iterator loIterChild = loFolderChildList.iterator();
		while (loIterChild.hasNext())
		{

			com.nyc.hhs.model.Document loChild = (com.nyc.hhs.model.Document) loIterChild.next();
			String lsChildDocId = loChild.getDocumentId();
			if (null != loChild.getDocType() && !loChild.getDocType().isEmpty()
					&& !loChild.getDocType().equalsIgnoreCase(""))
			{
				loPropFilter = createPropertyFilter(aoPropertyMap);
				Document loChildDocObj = Factory.Document.fetchInstance(aoObjStore, lsChildDocId, loPropFilter);
				loChildDocObj.getProperties().get(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED).setObjectValue(true);
				if (HHSR5Constants.TWO.equals(aoSharingFlag))
				{
					loChildDocObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID)
							.setObjectValue(new Id(aoFolderId));
					loChildDocObj.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSConstants.TWO);
				}
				else
				{
					loChildDocObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(null);
					loChildDocObj.getProperties().get(HHSR5Constants.SHARING_FLAG)
							.setObjectValue(HHSR5Constants.STRING_ZERO);
				}

				loChildDocObj.save(RefreshMode.REFRESH);
			}
			else
			{
				loPropFilter = createPropertyFilter(aoPropertyMap);
				Folder loChildFolderObj = Factory.Folder.fetchInstance(aoObjStore, lsChildDocId, loPropFilter);
				if (HHSR5Constants.TWO.equals(aoSharingFlag))
				{
					loChildFolderObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID)
							.setObjectValue(new Id(aoFolderId));
					loChildFolderObj.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSConstants.TWO);
				}
				else
				{
					loChildFolderObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(null);
					loChildFolderObj.getProperties().get(HHSR5Constants.SHARING_FLAG)
							.setObjectValue(HHSR5Constants.STRING_ZERO);
				}

				loChildFolderObj.save(RefreshMode.REFRESH);
			}
		}
	}

	/**
	 * This method is for file sharing
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param aoDocId String
	 * @param aoAgencyProviderMap Agency Provider Map
	 * @param asSharedBy string specifying the shared by Id
	 * @param aoPropertyMap Map for Folder properties
	 * @param aoUserOrg String
	 * @param aoUserOrgType String
	 * @param aoReqMap Required Property Map
	 * @param asCustomObjectClassName String
	 * @param asCustomObjectFolderName String
	 * @param asKey String
	 * @param asDocType String
	 * @return boolean variable true if doc is shared
	 * @throws ApplicationException - if any exception occurs
	 */
	private void shareFile(ObjectStore aoObjStore, String aoDocId, HashMap aoAgencyProviderMap, String asSharedBy,
			HashMap aoReqMap, HashMap aoPropertyMap, String aoUserOrgType, String aoUserOrg,
			String asCustomObjectClassName, String asCustomObjectFolderName, String asDocType)
			throws ApplicationException
	{
		boolean lbIsDocumentSharedBefore;
		String lsDocId;
		List loProvidersIdList;
		List loAgencyIdList;
		CustomObject loCustObj;
		PropertyFilter loPropFilter;
		Document loDocObj;
		// Iterating the hashmap for creating seperate custom object
		// for all
		// documents.
		loProvidersIdList = (List) aoAgencyProviderMap.get("PROVIDER");
		loAgencyIdList = (List) aoAgencyProviderMap.get("AGENCY");
		for (Iterator loIterator = loProvidersIdList.iterator(); loIterator.hasNext();)
		{
			String lsProviderId = (String) loIterator.next();
			// check if the document is already shared with the
			// provider
			lbIsDocumentSharedBefore = getDocumentSharedStatus(lsProviderId, aoDocId, "PROVIDER", aoObjStore);
			if (!lbIsDocumentSharedBefore)
			{
				loCustObj = Factory.CustomObject.createInstance(aoObjStore, asCustomObjectClassName);
				// Setting custom object properties.
				Properties loProps = loCustObj.getProperties();
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsProviderId);
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID, new Id(aoDocId));
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_BY_ID, asSharedBy);
				loCustObj.save(RefreshMode.REFRESH);
				fileCustomObjectToFolder(aoObjStore, loCustObj, asCustomObjectFolderName, asCustomObjectClassName);
			}
		}
		for (Iterator loIterator = loAgencyIdList.iterator(); loIterator.hasNext();)
		{
			String lsProviderId = (String) loIterator.next();
			// check if the document is already shared with the
			// provider
			lbIsDocumentSharedBefore = getDocumentSharedStatus(lsProviderId, aoDocId, "AGENCY", aoObjStore);
			if (!lbIsDocumentSharedBefore)
			{
				loCustObj = Factory.CustomObject.createInstance(aoObjStore, asCustomObjectClassName);
				// Setting custom object properties.
				Properties loProps = loCustObj.getProperties();
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, lsProviderId);
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID, new Id(aoDocId));
				loProps.putValue(P8Constants.PROPERTY_CE_SHARED_BY_ID, asSharedBy);

				loCustObj.save(RefreshMode.REFRESH);
				fileCustomObjectToFolder(aoObjStore, loCustObj, asCustomObjectFolderName, asCustomObjectClassName);
			}
		}
		loPropFilter = createPropertyFilter(aoPropertyMap);
		loDocObj = Factory.Document.fetchInstance(aoObjStore, aoDocId, loPropFilter);
		loDocObj.getProperties().get(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED).setObjectValue(true);
		loDocObj.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(new Id(aoDocId));
		loDocObj.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSConstants.TWO);
		loDocObj.save(RefreshMode.REFRESH);

		// For Parent

		String lsDocParentPath = loDocObj.getProperties().getStringValue(HHSR5Constants.PARENT_PATH);
		if (!lsDocParentPath.equals(FileNetOperationsUtils.setFolderPath(aoUserOrgType, aoUserOrg, "Document Vault")))
		{
			setParentProperty(aoObjStore, lsDocParentPath, aoReqMap, aoPropertyMap, aoDocId, asDocType);
		}

		// for creating custom objects in case of folders and
		// setting the meta data of its sub folders and document and
		// its parent
	}

	/**
	 * The method will set the parent property of the entity shared
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asPath path of the parent
	 * @param aoReqMap Map having required properties
	 * @param loPropertyMap Property Map
	 * @param lsDocId entity id
	 * @param lsDocType entity type
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setParentProperty(ObjectStore aoObjStore, String asPath, HashMap aoReqMap, HashMap loPropertyMap,
			String lsDocId, String lsDocType) throws ApplicationException
	{

		PropertyFilter loPropFilter;
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoDocId", lsDocId);
		loHmReqExceProp.put("aoHmRequiredProps", aoReqMap);
		Folder loDocParent = null;
		String loUserOrgType = (String) aoReqMap.get(HHSConstants.LS_USER_ORG_TYPE);
		LOG_OBJECT.Info("Entered P8ContentOperations.setParentProperty() ");
		String loUserOrg = (String) aoReqMap.get(HHSR5Constants.ORG_ID);
		loPropFilter = createPropertyFilter(loPropertyMap);
		try
		{
			String lsDocumentVaultPath = FileNetOperationsUtils.setFolderPath(loUserOrgType, loUserOrg,
					HHSR5Constants.DOCUMENT_VAULT);
			if (!asPath.equalsIgnoreCase(lsDocumentVaultPath))
			{
				if (null != lsDocType && !lsDocType.isEmpty())
				{
					loDocParent = Factory.Folder.fetchInstance(aoObjStore, asPath, loPropFilter);
				}
				else
				{

					loDocParent = Factory.Folder.fetchInstance(aoObjStore, asPath, loPropFilter);
				}
				loDocParent.getProperties().get(HHSR5Constants.SHARED_ENTITY_ID).setObjectValue(new Id(lsDocId));
				loDocParent.getProperties().get(HHSR5Constants.SHARING_FLAG).setObjectValue(HHSConstants.ONE);
				loDocParent.save(RefreshMode.REFRESH);
				String lsParentPathNext = asPath.substring(0, asPath.lastIndexOf(P8Constants.STRING_SINGLE_SLASH));

				if (!lsParentPathNext.equalsIgnoreCase(lsDocumentVaultPath))
				{

					setParentProperty(aoObjStore, lsParentPathNext, aoReqMap, loPropertyMap, lsDocId, lsDocType);
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setParentProperty()::", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"The Parent property of the shared entity may not be properly, please relogin and try again", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"The Parent property of the shared entity may not be properly, please relogin and try again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setParentProperty() ");
	}

	/**
	 * This Method is used for fetching list of Provider Id with whom one
	 * specific document is shared
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asDocId document id as input
	 * @return ArrayList list of provider id for shared document
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getProviderIdForSharedDocument(ObjectStore aoObjStore, String asDocId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.getProviderIdForSharedDocument() with asDocId::" + asDocId);

		ArrayList<String> loArrProviderIdList = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		if (null == asDocId || asDocId.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getProviderId Method. Required Parameters are missing");
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			throw loAppex;
		}
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id
		String lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
				+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " FROM "
				+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
				+ " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + " = '" + asDocId + "'";
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			// Iterating the result set
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				CustomObject loObj = (CustomObject) loIt.next();
				String lsProviderId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID);
				String lsAgencyId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID);
				// adding provider id to array list
				if (StringUtils.isNotBlank(lsProviderId))
				{
					loArrProviderIdList.add(lsProviderId);
				}
				else if (StringUtils.isNotBlank(lsAgencyId))
				{
					loArrProviderIdList.add(lsAgencyId);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching id for a document :", aoEx);
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			LOG_OBJECT.Error("Error fetching id for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getProviderIdForSharedDocument() ");
		return loArrProviderIdList;
	}

	/**
	 * This method removes shared document by provider
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asProviderId provider id as input
	 * @param asSharingOrgId Sharing Org Id
	 * @return boolean variable if its true then sharing removal is success
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean removeSharedDocumentsByProvider(ObjectStore aoObjStore, String asProviderId, String asSharingOrgId)
			throws ApplicationException
	{
		boolean lbSharingRemoved = true;
		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put("providerId", asProviderId);
		LOG_OBJECT.Info("Entered P8ContentOperations.removeSharedDocumentsByProvider() with parameters::"
				+ loHmRequiredProp.toString());
		Document loDocObj = null;
		PropertyFilter loPropFilter = null;
		HashMap loPropertyMap = new HashMap();
		List loLstRemainProvList = null;
		String lsCustomObjectClassName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				P8Constants.PROP_FILE_SHARED_DOC_PROVIDER_CUSTOM_OBJECT);
		if (lsCustomObjectClassName == null || lsCustomObjectClassName.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in removeDocumentsSharing method. Required Parameters SHARED_DOC_PROVIDER_CUSTOM_OBJECT is missing.");
			loAppex.setContextData(loHmRequiredProp);
			throw loAppex;
		}
		try
		{
			SearchSQL loSqlObject = new SearchSQL();
			// prepare query to get all the documents objects shared with the
			// provided provider
			String lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
					+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " FROM " + lsCustomObjectClassName + " WHERE ("
					+ P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + " =  '" + asProviderId + "' OR "
					+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " =  '" + asProviderId + "') and "
					+ P8Constants.PROPERTY_CE_SHARED_BY_ID + "= '" + asSharingOrgId + "'";

			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for retrieving custom objects
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{

				// type casting the custom object and deleting the same.
				CustomObject loObj = (CustomObject) loIt.next();
				String lsDocumentId = loObj.getProperties().getIdValue(P8Constants.PROPERTY_CE_SHARED_DOC_ID)
						.toString();
				loObj.delete();
				loObj.save(RefreshMode.NO_REFRESH);
				loLstRemainProvList = getProviderIdForSharedDocument(aoObjStore, lsDocumentId);
				if (loLstRemainProvList.size() <= 0)
				{
					loPropFilter = createPropertyFilter(loPropertyMap);
					loDocObj = Factory.Document.fetchInstance(aoObjStore, lsDocumentId, loPropFilter);
					loDocObj.getProperties().get("IS_SHARED").setObjectValue(false);
					loDocObj.save(RefreshMode.NO_REFRESH);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			lbSharingRemoved = false;
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.removeSharedDocumentsByProvider()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			lbSharingRemoved = false;
			ApplicationException loAppex = new ApplicationException(
					"FileNet RunTime Exception in removeDocumentsSharing method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet RunTime Exception in removeDocumentsSharing method", aoEx);
			throw loAppex;
		}
		catch (Exception aoE)
		{
			lbSharingRemoved = false;
			ApplicationException loAppex = new ApplicationException("Exception in removeDocumentsSharing method ", aoE);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in removeDocumentsSharing method", aoE);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.removeSharedDocumentsByProvider() ");
		return lbSharingRemoved;
	}

	/**
	 * Below method will check and confirm whether a document with same title
	 * category and type is linked to any application or not.
	 * 
	 * @param aoOs filenet object store session
	 * @param asDocumentId document id as input
	 * @return boolean lbIsLinkedToApp it its true the document is linked to
	 *         application
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean checkDocumentLinkedToApplication(ObjectStore aoOs, String asDocumentId) throws ApplicationException
	{

		HashMap<String, String> loPropertyMap = new HashMap<String, String>();
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("documentid", asDocumentId);
		LOG_OBJECT.Info("Entered P8ContentOperations.checkDocumentLinkedToApplication() with parameters::"
				+ loHmReqExceProp.toString());
		Document loDoc = null;
		PropertyFilter loPropFilter = null;
		boolean lbIsLinkedToApp = false;
		try
		{
			loPropertyMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, "");
			loPropFilter = createPropertyFilter(loPropertyMap);
			loDoc = Factory.Document.fetchInstance(aoOs, asDocumentId, loPropFilter);
			lbIsLinkedToApp = loDoc.getProperties().getBooleanValue(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION);

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkDocumentLinkedToApplication()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while checking link to application for a document : ", aoEx);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, "ERR_LINKED_TO_APP");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking link to application for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.checkDocumentLinkedToApplication()");
		return lbIsLinkedToApp;
	}

	/**
	 * This method fetches shared agencies and provider list
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asAgencyType type of the agency
	 * @param asProviderId specifies the Id of the provider
	 * @return TreeSet loAgencyProviderIdSet Shared Agencies Provider List
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public TreeSet getSharedAgenciesProviderList(ObjectStore aoObjStore, String asAgencyType, String asProviderId)
			throws ApplicationException
	{
		TreeSet loAgencyProviderIdSet = new TreeSet();
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asAgencyType", asAgencyType);
		loHmReqExceProp.put("asProviderId", asProviderId);
		LOG_OBJECT.Info("Entered P8ContentOperations.getSharedAgenciesProviderList() with parameters::"
				+ loHmReqExceProp.toString());
		SearchSQL loSqlObject = new SearchSQL();
		if (asAgencyType == null || asAgencyType.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getSharedAgenciesProviderList Method. Required Parameters are missing");
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id

		String lsSQLQuery = "SELECT DISTINCT " + asAgencyType + " FROM "
				+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
				+ P8Constants.PROPERTY_CE_SHARED_OBJECT_TABLE_ALIAS + " INNER JOIN " + " HHS_ACCELERATOR "
				+ P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS + " ON " + P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS + "."
				+ P8Constants.PROPERTY_CE_DOCUMENT_ID + " = " + P8Constants.PROPERTY_CE_SHARED_OBJECT_TABLE_ALIAS + "."
				+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + " where " + P8Constants.PROPERTY_CE_DOC_TABLE_ALIAS
				+ ".PROVIDER_ID  = '" + asProviderId + "'";
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			// Iterating the result set
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				CustomObject loObj = (CustomObject) loIt.next();
				String lsProviderId = loObj.getProperties().getStringValue(asAgencyType);
				// adding provider id to array list
				if (null != lsProviderId)
				{
					loAgencyProviderIdSet.add(lsProviderId);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching provider id for a document :", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getSharedAgenciesProviderList()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getSharedAgenciesProviderList() ");
		return loAgencyProviderIdSet;
	}

	/**
	 * This method fetches shared agencies and provider list <li>Making changes
	 * for Release 5</li>
	 * @param aoObjStore active filenet object store session
	 * @param asAgencyType type of the agency
	 * @param asProviderId Id of the provider
	 * @return TreeSet loAgencyProviderIdSe Shared Documents Owner List
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public TreeSet getSharedDocumentsOwnerList(ObjectStore aoObjStore, String asAgencyType, String asProviderId)
			throws ApplicationException
	{
		TreeSet loAgencyProviderIdSet = new TreeSet();
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, List<String>> loSharedIdMap = new HashMap<String, List<String>>();
		loHmReqExceProp.put("asProviderId", asProviderId);
		LOG_OBJECT.Info("Entered P8ContentOperations.getSharedDocumentsOwnerList() with parameters::"
				+ loHmReqExceProp.toString());
		SearchSQL loSqlObject = new SearchSQL();
		if (asProviderId.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getSharedDocumentsOwnerList Method. Required Parameters are missing");
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id

		String lsSQLQuery = "SELECT HHS_SHARED_BY ,SHARED_DOCUMENT_ID FROM HHSSharedDocument WHERE " + asAgencyType
				+ " ='" + asProviderId + "'";
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			// Iterating the result set
			Iterator loIt = loRowSet.iterator();
			while (loIt.hasNext())
			{
				RepositoryRow loRow = (RepositoryRow) loIt.next();
				Properties loProp = loRow.getProperties();
				// adding provider id to array list
				if (null != loProp && !loProp.getStringValue("HHS_SHARED_BY").isEmpty())
				{
					loAgencyProviderIdSet.add(loProp.getStringValue("HHS_SHARED_BY"));
				}
				if (null != loProp.getIdValue("SHARED_DOCUMENT_ID")
						&& !loProp.getIdValue("SHARED_DOCUMENT_ID").toString().isEmpty())
				{
					List<String> loList = new ArrayList<String>();
					if (loSharedIdMap.containsKey(loProp.getStringValue("HHS_SHARED_BY")))
					{
						loList = loSharedIdMap.get(loProp.getStringValue("HHS_SHARED_BY"));
						loList.add(loProp.getIdValue("SHARED_DOCUMENT_ID").toString());
						loSharedIdMap.put(loProp.getStringValue("HHS_SHARED_BY"), loList);
					}
					else
					{
						loList.add(loProp.getIdValue("SHARED_DOCUMENT_ID").toString());
						loSharedIdMap.put(loProp.getStringValue("HHS_SHARED_BY"), loList);
					}

				}
			}
			BaseCacheManagerWeb.getInstance().putCacheObject("SharedIdMap", loSharedIdMap);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching provider id for a document :", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error fetching provider id for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getSharedDocumentsOwnerList()");
		return loAgencyProviderIdSet;
	}

	/**
	 * This method is used to get count of documents for the active provider
	 * 
	 * @param aoUserSession P8UserSession object
	 * @param aoObjStr active filenet object store session
	 * @param asProviderId Id of the provider
	 * @return int liTotalDocumentCount Document Count For Provider
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public int getDocumentCountForProvider(P8UserSession aoUserSession, ObjectStore aoObjStr, String asProviderId)
			throws ApplicationException
	{

		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentCountForProvider() with asProviderId::" + asProviderId);

		HashMap loHmExcepRequiredProp = new HashMap();
		String lsSQLQuery;
		int liTotalDocumentCount = 0;
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			lsSQLQuery = "Select " + P8Constants.PROPERTY_CE_DOCUMENT_ID + " From "
					+ P8Constants.PROPERTY_CE_ROOT_DOCUMENT_CLASS_NAME + " where "
					+ P8Constants.PROPERTY_CE_PROVIDER_ID + " = '" + asProviderId + "'";
			loSqlObject.setQueryString(lsSQLQuery);

			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);

			PageIterator loPageItr = loEngineSet.pageIterator();
			while (loPageItr.nextPage())
			{
				liTotalDocumentCount = liTotalDocumentCount + loPageItr.getElementCount();
			}
		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while getting document count", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error while getting document count", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentCountForProvider()");
		return liTotalDocumentCount;
	}

	/**
	 * This method will check whether the document is already shared with the
	 * selected provider or agency
	 * 
	 * @param asOrganizationId Id of the organization
	 * @param asDocumentId Id of the document
	 * @param asOrganizationType type of the Organization
	 * @param aoObjStr active filenet object store session
	 * @return lbIsDocumentSharedBefore true if object is shared before
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private boolean getDocumentSharedStatus(String asOrganizationId, String asDocumentId, String asOrganizationType,
			ObjectStore aoObjStr) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentSharedStatus()");

		HashMap loHmExcepRequiredProp = new HashMap();
		String lsSQLQuery = null;
		boolean lbIsDocumentSharedBefore = false;
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			loHmExcepRequiredProp.put("organizationId", asOrganizationId);
			loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, asDocumentId);
			LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentSharedStatus() with parameters::"
					+ loHmExcepRequiredProp.toString());
			if (asOrganizationType.equalsIgnoreCase("PROVIDER"))
			{
				lsSQLQuery = "Select " + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + " From "
						+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
						+ " where " + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + " = '" + asOrganizationId + "' AND "
						+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + "='" + asDocumentId + "'";
			}
			else
			{
				lsSQLQuery = "Select " + P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " From "
						+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
						+ " where " + P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " = '" + asOrganizationId + "' AND "
						+ P8Constants.PROPERTY_CE_SHARED_DOC_ID + "='" + asDocumentId + "'";
			}
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			if (!loRowSet.isEmpty())
			{
				lbIsDocumentSharedBefore = true;
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDocumentSharedStatus Method", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error in getDocumentSharedStatus Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentSharedStatus()");
		return lbIsDocumentSharedBefore;
	}

	/**
	 * This method sets document properties
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param aoDomain Domain
	 * @param aoObjectStore Object Store
	 * @param aoDocIdList Doc Id List
	 * @param aoPropertyMap Property Map
	 * @return boolean variable specifying whether or not the doc properties are
	 *         set
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean setLinkToApplicationDocPropertyThoroughBatch(Domain aoDomain, ObjectStore aoObjectStore,
			List<String> aoDocIdList, HashMap aoPropertyMap) throws ApplicationException
	{

		boolean lbFlag = false;
		Document loNewDoc = null;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, aoDocIdList);
		loHmExcepRequiredProp.put("aoDomain", aoDomain.get_Name());
		loHmExcepRequiredProp.put("aoObjectStore", aoObjectStore.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.setDocPropertyThoroughBatch() with parameters::"
				+ loHmExcepRequiredProp.toString());
		List<String> loTempDocIdList = new ArrayList();

		try
		{
			// The RefreshMode parameter is set to REFRESH to indicate that the
			// property cache for
			// this instance is to be refreshed with the updated data.
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoDomain, RefreshMode.REFRESH);

			// Add object updates to the batch.
			// Assume, in this case, that these documents have already been
			// checked out.
			// No property filters are used (filter parameters are null).
			for (String lsDocId : aoDocIdList)
			{
				if (!loTempDocIdList.contains(lsDocId))
				{
					loNewDoc = Factory.Document.fetchInstance(aoObjectStore, new Id(lsDocId),
							createPropertyFilter(aoPropertyMap));

					// Sets the property and assigns the
					// specified property values (Properties.putValue) to the
					// retrieved properties for the
					// doc (the inherited EngineObject.getProperties).
					Properties loProperties = loNewDoc.getProperties();
					for (Iterator loIterator = aoPropertyMap.keySet().iterator(); loIterator.hasNext();)
					{
						String lsKey = (String) loIterator.next();
						loProperties.putObjectValue(lsKey, aoPropertyMap.get(lsKey));
					}
					// Adds all updates (to the two Document objects) to the
					// UpdatingBatch instance.
					loUpdateBatch.add(loNewDoc, null);
					lbFlag = true;
					loTempDocIdList.add(lsDocId);
				}
			}
			if (lbFlag)
			{
				// Execute the batch update operation.
				if (loUpdateBatch.hasPendingExecute())
				{
					loUpdateBatch.updateBatch();
				}
			}

		}
		catch (EngineRuntimeException aoEx)
		{
			lbFlag = false;
			ApplicationException loAppex = new ApplicationException("Error While setting document property", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDocPropertyThoroughBatch()::", aoEx);
			throw loAppex;
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDocPropertyThoroughBatch()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While setting document property", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While setting document property", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.setDocPropertyThoroughBatch()");
		return lbFlag;
	}

	/**
	 * <ul>
	 * <li>This method gets finanicals document Id</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoObjStore ObjectStore object
	 * @param asDocClassName String object
	 * @param asFolderPath String object
	 * @param asDocTitle String object
	 * @return List of String og Document Ids
	 * @throws ApplicationException
	 */
	public List<String> getFinancialsDocumentId(ObjectStore aoObjStore, String asDocClassName, String asFolderPath,
			String asDocTitle) throws ApplicationException
	{
		List<String> loListDocId = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		HashMap<String, String> loHmRequiredProp = new HashMap<String, String>();
		loHmRequiredProp.put(P8Constants.AS_DOC_CLASS_NAME, asDocClassName);
		loHmRequiredProp.put(HHSR5Constants.AS_FOLDERS_PATH, asFolderPath);
		loHmRequiredProp.put(P8Constants.AS_DOC_TITLE, asDocTitle);
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentId() with parameters::" + loHmRequiredProp.toString());
		String lsSQLQuery = "";

		try
		{
			// Creating SQL query for fetching documents.

			lsSQLQuery = "SELECT doc.Id FROM " + asDocClassName + " doc WHERE  doc.DocumentTitle in( " + asDocTitle
					+ ") and doc.This INFOLDER '" + asFolderPath + "'";

			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loRowSet.iterator();

			// Iterating rows retrieved from the query
			while (loIt.hasNext())
			{
				Document loDoc = (Document) loIt.next();
				loListDocId.add(loDoc.getProperties().getIdValue(P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while fetching documents from folder : "
					+ asFolderPath, aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Error while fetching documents from folder :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentId() ");
		return loListDocId;
	}

	// Added for Release 5

	/**
	 * This method will create folder in filenet
	 * 
	 * @param aoObjStr
	 * @param aoUserSession P8User session bean object
	 * @param asnewFolderPath Path to create new folder
	 * @param asnewFoldername new Folder Name to be created
	 * @param asUserOrgType Organization Type
	 * @param asUserOrg Organization User
	 * @param asUserName login user name
	 * @return loreturnBean contains metadata of created folder
	 * @throws ApplicationException
	 */
	public Folder createFolder(ObjectStore aoObjStr, String asNewFolderName, String asUserOrgType,
			Folder aoParentFolder, String asUserName) throws ApplicationException
	{
		ReferentialContainmentRelationship loRCR = null;
		Folder loNewFolder = null;
		String lsParentPath = null;
		Properties loParentFolderProp = null;
		LOG_OBJECT.Info("Entered P8ContentOperations.createFolder() ");
		String lsParentPathFromVault = null;
		String lsNewFolderPath = null;
		try
		{
			loParentFolderProp = aoParentFolder.getProperties();
			lsParentPath = loParentFolderProp.getStringValue(HHSR5Constants.PATH_NAME);
			lsParentPathFromVault = lsParentPath.substring(lsParentPath.indexOf(HHSR5Constants.DOCUMENT_VAULT));
			lsNewFolderPath = lsParentPathFromVault + HHSR5Constants.FORWARD_SLASH + asNewFolderName;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsMaxFolderPath = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
					+ HHSR5Constants.DOCUMENT_VAULT_MAX_FOLDER_PATH;
			int liMaxFolderPath = Integer.parseInt(loApplicationSettingMap.get(lsMaxFolderPath));
			if (lsNewFolderPath.length() < liMaxFolderPath)
			{
				if (checkFolderExists(aoObjStr, lsParentPath + HHSR5Constants.FORWARD_SLASH + asNewFolderName))
				{
					String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
							HHSR5Constants.FOLDER_ALREADY_EXISTS);
					ApplicationException loAppex = new ApplicationException(lsMessage);
					throw loAppex;
				}
				else
				{
					loNewFolder = Factory.Folder.createInstance(aoObjStr, HHSR5Constants.HHS_CUSTOM_FOLDER);
					loNewFolder.set_Parent(aoParentFolder);
					if (null != asNewFolderName && !asNewFolderName.equals(HHSR5Constants.EMPTY_STRING))
					{
						loNewFolder.set_FolderName(asNewFolderName);
						Properties loFolderProps = loNewFolder.getProperties();
						if (null != loParentFolderProp.getStringValue(HHSR5Constants.SHARING_FLAG)
								&& !loParentFolderProp.getStringValue(HHSR5Constants.SHARING_FLAG).isEmpty()
								&& loParentFolderProp.getStringValue(HHSR5Constants.SHARING_FLAG).equalsIgnoreCase(
										HHSR5Constants.TWO))
						{
							loFolderProps.putValue(HHSR5Constants.SHARING_FLAG,
									loParentFolderProp.getStringValue(HHSR5Constants.SHARING_FLAG));
							loFolderProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID,
									loParentFolderProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID));
						}
						else
						{
							loFolderProps.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.ZERO);
							loFolderProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
						}
						loParentFolderProp.putValue(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
								HHSUtil.getCurrentTimestampDate());
						aoParentFolder.setUpdateSequenceNumber(null);
						aoParentFolder.save(RefreshMode.REFRESH);
						if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
							loFolderProps.putObjectValue(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrgType);
						else
							loFolderProps.putObjectValue(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserName);
						loFolderProps.putObjectValue(HHSR5Constants.ORGANIZATION_ID_KEY, asUserOrgType);
						loFolderProps.putValue(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
								HHSUtil.getCurrentTimestampDate());
						loNewFolder.setUpdateSequenceNumber(null);
						loNewFolder.save(RefreshMode.REFRESH);

						loRCR = aoParentFolder.file(loNewFolder, AutoUniqueName.AUTO_UNIQUE, loNewFolder.get_Id()
								.toString(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
						loRCR.save(RefreshMode.NO_REFRESH);
					}
					else
					{
						String lsMessage = "Can not Create a Folder With Empty Name.";
						ApplicationException loAppex = new ApplicationException(lsMessage);
						throw loAppex;
					}
				}
			}
			else
			{
				String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.FOLDER_PATH_EXCEEDS_LIMIT);
				ApplicationException loAppex = new ApplicationException(lsMessage);
				throw loAppex;
			}

		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.createFolder()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while creating folder", loAppEx);
			throw new ApplicationException("Internal Error Occured While Processing Your Request", loAppEx);
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.createFolder() ");
		return loNewFolder;
	}

	/**
	 * This method will create a folderMapping Bean for insertion in DataBase
	 * @param asNewFolderName
	 * @param asUserOrg
	 * @param lsUserName
	 * @param loMappingBean
	 * @param loFldr
	 * @param loProp
	 * @throws ApplicationException
	 */
	public void getFolderBeanMappingForDB(String asNewFolderName, String asUserOrg, String lsUserName,
			FolderMappingBean aoFolderBeanChild, Folder loFldr, String asParentFolderID) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.getFolderBeanMappingForDB() ");
			aoFolderBeanChild.setOrganizationId(asUserOrg);
			if (null != loFldr.get_Id())
			{
				aoFolderBeanChild.setFolderFilenetId(loFldr.get_Id().toString());
			}
			aoFolderBeanChild.setDocumentCount(HHSR5Constants.INT_ZERO);
			aoFolderBeanChild.setFolderName(asNewFolderName);
			aoFolderBeanChild.setParentFolderFilenetId(asParentFolderID);
			aoFolderBeanChild.setModifiedBy(lsUserName);
			aoFolderBeanChild.setCreatedBy(lsUserName);
			aoFolderBeanChild.setFolderCount(HHSR5Constants.INT_ZERO);
			aoFolderBeanChild.setSharedFlag(Integer.toString(HHSR5Constants.INT_ZERO));
			aoFolderBeanChild.setAttachmentFlag(Integer.toString(HHSR5Constants.INT_ZERO));
			aoFolderBeanChild.setType(Integer.toString(HHSR5Constants.INT_ONE));
			aoFolderBeanChild.setMovedToRecycleBin(Integer.toString(HHSR5Constants.INT_ZERO));
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("! Error occurre while creating Folder.", aoEx);
			LOG_OBJECT.Error("FileNet Runtime Exception in getFolderBeanMappingForDB Method::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getFolderBeanMappingForDB() ");
	}

	/**
	 * This method will create initial Document Vault and RecycleBin folder
	 * @param aoObjStr
	 * @param asParentFolderID
	 * @param asUserOrgType
	 * @param asUserOrg
	 * @param lsUserName
	 * @param loMappingBean
	 * @return
	 * @throws ApplicationException
	 */
	public Folder getFolderObjectById(ObjectStore aoObjStr, String asParentFolderID,
			HashMap<String, String> aoPropertyMap) throws ApplicationException
	{
		String lsParentPath = null;
		LOG_OBJECT.Info("Entered P8ContentOperations.getFolderObjectById() ");
		PropertyFilter loPropFilter = null;
		Folder loFolderObj = null;
		try
		{
			if (null != asParentFolderID && !asParentFolderID.isEmpty())
			{
				if (null != aoPropertyMap && !aoPropertyMap.isEmpty())
				{
					loPropFilter = createPropertyFilter(aoPropertyMap);
				}
				loFolderObj = Factory.Folder.fetchInstance(aoObjStr, new Id(asParentFolderID), loPropFilter);
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.createInitialFoldersInFilenetAndDB()::", aoAppex);
			String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PARENT_FOLDER_NOT_EXISTS);
			throw new ApplicationException(lsMessage);
		}
		catch (Exception aoEx)
		{
			String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSR5Constants.PARENT_FOLDER_NOT_EXISTS);
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			LOG_OBJECT.Error("FileNet Runtime Exception in createInitialFoldersInFilenetAndDB Method::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getFolderObjectById() ");
		return loFolderObj;
	}

	/**
	 * This method will file a document to chosen folder
	 * @param aoObjStr
	 * @param aoDoc
	 * @param asFolderId
	 * @param asDocClassName
	 * @param aoPropertyMap
	 * @param asParentFolder
	 * @return
	 * @throws ApplicationException
	 */
	private void fileDocToCustomFolder(ObjectStore aoObjStr, Document aoDoc, String asFolderId, String asDocClassName,
			HashMap<String, String> aoPropertyMap, String asDocType, Folder aoFldr) throws ApplicationException
	{
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		try
		{
			Boolean lbCustomFileFlag = true;
			String lsRestrictedDocTypeKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
					+ P8Constants.DOCUMENT_VAULT_RESTRICTED_DOCTYPE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsRestrictedDocType = loApplicationSettingMap.get(lsRestrictedDocTypeKey);
			// Code fix for 6918
			lbCustomFileFlag = checkFilingToCustomRequired(asDocType, lbCustomFileFlag, lsRestrictedDocType);

			if (lbCustomFileFlag)
			{
				ReferentialContainmentRelationship loRCR = aoFldr.file(aoDoc, AutoUniqueName.AUTO_UNIQUE, aoDoc
						.get_Id().toString(), DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
				// Folder id to update document count
				aoPropertyMap.put(HHSR5Constants.CUSTOM_FLDR_ID, aoFldr.get_Id().toString());
				LOG_OBJECT.Info("Entered ReferentialContainmentRelationship.save() with parameters:"
						+ loHmReqExceProp.toString());
				loRCR.save(RefreshMode.NO_REFRESH);
				LOG_OBJECT.Info("Entered ReferentialContainmentRelationship.save() with parameters:"
						+ loHmReqExceProp.toString());

			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fileDocToCustomFolder()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in fileDocToCustomFolder Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in fileDocToCustomFolder Method::", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in fileDocToCustomFolder Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in fileDocToCustomFolder Method::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.fileDocToCustomFolder() ");
	}

	/**
	 * This method will check whether to create folder in Filenet or not
	 * 
	 * @param aoObjectStore
	 * @param asPath
	 * @param asFolderClassType
	 * @return Folder Flag
	 * @throws ApplicationException
	 */
	public FolderMappingBean checkFolderByName(P8UserSession aoUserSession, String asPath, String asUserOrg,
			String asFolderClassType) throws ApplicationException
	{
		ObjectStore loOS = filenetConnection.getObjectStore(aoUserSession);
		FolderMappingBean loFolderBean = new FolderMappingBean();
		LOG_OBJECT.Info("Entered P8ContentOperations.checkFolderByName() ");
		List<Folder> loTempList = new ArrayList<Folder>();
		Folder lofolder = getFolderByName(loOS, asPath, asFolderClassType, null, asUserOrg);
		if (loTempList.size() > 0)
		{
			loFolderBean.setOrganizationId(asUserOrg);
			loFolderBean.setFolderFilenetId(lofolder.get_Id().toString());
			loFolderBean.setDocumentCount(HHSR5Constants.INT_ZERO);
			loFolderBean.setFolderName(HHSR5Constants.DOCUMENT_VAULT);
			loFolderBean.setParentFolderFilenetId(HHSConstants.DELIMITER_SINGLE_HASH);
			loFolderBean.setModifiedBy(aoUserSession.getUserId());
			loFolderBean.setCreatedBy(aoUserSession.getUserId());
			loFolderBean.setFolderCount(HHSR5Constants.INT_ZERO);
			loFolderBean.setSharedFlag(Integer.toString(HHSR5Constants.INT_ZERO));
			loFolderBean.setAttachmentFlag(Integer.toString(HHSR5Constants.INT_ZERO));
			loFolderBean.setType(Integer.toString(HHSR5Constants.INT_ONE));
			loFolderBean.setMovedToRecycleBin(Integer.toString(HHSR5Constants.INT_ZERO));
			LOG_OBJECT.Info("Exited P8ContentOperations.checkFolderByName() ");
			return loFolderBean;
		}
		else
		{
			return loFolderBean;
		}
	}

	// Added for Release 5
	/**
	 * This method will fetch folder properties from Filenet
	 * @param aoObjectStore
	 * @param aoFolderId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public HashMap<String, Object> getBRFolderPropertiesById(ObjectStore aoObjectStore, String aoFolderId,
			PropertyFilter aoPF) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered P8ContentOperations.getBRDcoumentPropertiesById()");
		HashMap<String, Object> loHmDocumentDetails = new HashMap<String, Object>();
		try
		{
			if (null != aoFolderId && !aoFolderId.isEmpty())
			{
				Folder loFolder = Factory.Folder.fetchInstance(aoObjectStore, aoFolderId, aoPF);
				Properties loProp = loFolder.getProperties();
				if (null != loFolder)
				{
					Iterator loIt = loProp.iterator();
					while (loIt.hasNext())
					{
						Property loPropItr = (Property) loIt.next();
						// setting value to the output hash map
						loHmDocumentDetails.put(loPropItr.getPropertyName(), loPropItr.getObjectValue());
					}
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in folder fetching properties", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in folder fetching properties", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getBRDcoumentPropertiesById()");
		return loHmDocumentDetails;
	}

	/**
	 * This Method will set folder properties into Filenet
	 * @param loOS
	 * @param hmReqProps
	 * @return
	 * @throws ApplicationException
	 */
	public boolean setFolderProperties(ObjectStore loOS, HashMap<String, String> hmReqProps)
			throws ApplicationException
	{

		String lsNewFolderPath = null;
		LOG_OBJECT.Info("Entered P8ContentOperations.setFolderProperties()");
		PropertyFilter loPF = new PropertyFilter();
		List<com.nyc.hhs.model.Document> loFolderChildList = new ArrayList<com.nyc.hhs.model.Document>();
		try
		{	//null passed to remove sharing from child folders 4.0.2.0
			loFolderChildList = getChildList(loOS, (String) hmReqProps.get(HHSR5Constants.FOLDER_ID),
					HHSR5Constants.INSUBFOLDER, null, null);
			//null passed to remove sharing from child folders 4.0.2.0	
			int liAllChildsLength = getLongestPath(loFolderChildList,
					(String) hmReqProps.get(HHSR5Constants.FOLDER_ID), loOS);
			FilterElement loParentPathFE = new FilterElement(null, null, null, HHSR5Constants.PATH_NAME, null);
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsMaxFolderPath = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
					+ HHSR5Constants.DOCUMENT_VAULT_MAX_FOLDER_PATH;
			int liMaxFolderPath = Integer.parseInt(loApplicationSettingMap.get(lsMaxFolderPath));
			loPF = createPropertyFilter(hmReqProps);
			loPF.addIncludeProperty(loParentPathFE);
			Folder loFolder = Factory.Folder.fetchInstance(loOS, (String) hmReqProps.get(HHSR5Constants.FOLDER_ID),
					loPF);
			Properties loProp = loFolder.getProperties();
			String lsOldPathName = loFolder.get_PathName();
			String lsOldFolderPath = lsOldPathName.substring(lsOldPathName.indexOf(HHSR5Constants.DOCUMENT_VAULT));
			String liOldFolderName = lsOldPathName.substring(lsOldPathName.lastIndexOf(HHSConstants.FORWARD_SLASH) + 1,
					(lsOldPathName.length()));
			int liNewFolderLength = ((String) hmReqProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE)).length();
			Integer liNewMaxFolderPath = lsOldFolderPath.length() - liOldFolderName.length() + liAllChildsLength
					+ liNewFolderLength;
			if (liNewMaxFolderPath < liMaxFolderPath)
			{
				loProp.putValue(HHSR5Constants.FILENET_FOLDER_NAME,
						(String) hmReqProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
				loFolder.save(RefreshMode.REFRESH);
			}
			else
			{
				String lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSR5Constants.FOLDER_PATH_EXCEEDS_LIMIT);
				ApplicationException loAppex = new ApplicationException(lsMessage);
				throw loAppex;
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.setFolderProperties::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Could not able to save folder properties.", aoEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setFolderProperties::", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setFolderProperties()");
		return true;
	}

	/**
	 * This method will give us the exact count of children in any folder
	 * 
	 * @param loObjStr
	 * @param asFolderId
	 * @return children count
	 * @throws ApplicationException
	 */
	public int getEntityCountInParent(ObjectStore loObjStr, String asFolderId) throws ApplicationException
	{
		int liEntityCount = 0;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.getEntityCountInParent()");
			SearchSQL loSqlObject = new SearchSQL();
			String lsSQLQuery;
			lsSQLQuery = "SELECT DISTINCT DOC.ID,FO.ID as FOLDER_ID FROM (ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) full join HHSCustomFolder fo on rcr.head = object(fo.id)  WHERE rcr.head INSUBFOLDER '"
					+ asFolderId + HHSR5Constants.STR;
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(loObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			PageIterator loPageItr = loEngineSet.pageIterator();
			while (loPageItr.nextPage())
			{
				liEntityCount = liEntityCount + loPageItr.getElementCount();
			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in fetching entity Count", aoEx);
			LOG_OBJECT.Error("Error in fetching entity Count", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getEntityCountInParent()");
		return liEntityCount;
	}

	/**
	 * This method will move the entities depends upon entity type
	 * @param aoObjStr
	 * @param lomoveDocumentList
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean move(ObjectStore aoObjStr, List<com.nyc.hhs.model.Document> lomoveDocumentList)
			throws ApplicationException
	{
		Boolean lbSharedFlag = false;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.move()");
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
					RefreshMode.REFRESH);
			if (null != lomoveDocumentList && !lomoveDocumentList.isEmpty())
			{
				HashMap<String, String> loPropMap = new HashMap<String, String>();
				loPropMap.put(HHSR5Constants.PATH_NAME, HHSR5Constants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.PARENT_PATH, HHSR5Constants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.SHARING_FLAG, HHSR5Constants.EMPTY_STRING);
				loPropMap.put(HHSR5Constants.SHARED_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
				PropertyFilter loPf = createPropertyFilter(loPropMap);
				// checkParentChildRelationShip(aoObjStr, lomoveDocumentList);
				for (Iterator iterator = lomoveDocumentList.iterator(); iterator.hasNext();)
				{
					com.nyc.hhs.model.Document loDocument = (com.nyc.hhs.model.Document) iterator.next();
					Folder loDestinationFolder = Factory.Folder.fetchInstance(aoObjStr, loDocument.getMoveToPath(),
							loPf);
					Properties loPropFldr = loDestinationFolder.getProperties();
					IndependentObjectSet loRowSet = fetchFileInObject(aoObjStr, loDocument);
					Iterator itr = loRowSet.iterator();
					while (itr.hasNext())
					{
						ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) itr.next();
						if (rcr.get_Tail().getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
						{

							rcr.set_Tail(loDestinationFolder);
							loUpdateBatch.add(rcr, null);
						}

					}
					if (loUpdateBatch.hasPendingExecute())
					{
						loUpdateBatch.updateBatch();
					}
					if (null != loDocument && null != loDocument.getDocType() && !loDocument.getDocType().isEmpty()
							&& !loDocument.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
					{
						lbSharedFlag = moveDocumentObject(aoObjStr, loDocument, loPropFldr);

					}
					else
					{
						lbSharedFlag = moveFolderObject(aoObjStr, loDocument, loDestinationFolder);
					}

				}

			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("File Cannot be moved, Please try Again", aoEx);
			LOG_OBJECT.Error("File Cannot be moved, Please try Again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.move()");
		return lbSharedFlag;
	}

	/**
	 * This Method will move the folders
	 * @param aoObjStr
	 * @param loSourceFolderObj
	 * @param loFolder
	 * @param loDestinationFolderProps
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private Boolean moveFolderObject(ObjectStore aoObjStr, com.nyc.hhs.model.Document loSourceFolderObj,
			Folder aoDestinationFolder) throws ApplicationException
	{
		Boolean lbSharedFlag = false;
		HashMap loPropertyMap = new HashMap();
		try
		{
			Properties loDestinationFolderProps = aoDestinationFolder.getProperties();
			PropertyFilter loPf1 = new PropertyFilter();
			FilterElement loFE5 = new FilterElement(null, null, null, HHSR5Constants.SHARED_ENTITY_ID, null);
			FilterElement loFE6 = new FilterElement(null, null, null, HHSR5Constants.SHARING_FLAG, null);
			FilterElement loFE7 = new FilterElement(null, null, null, "Parent", null);
			FilterElement loFE8 = new FilterElement(null, null, null, HHSR5Constants.PATH_NAME, null);
			loPf1.addIncludeProperty(loFE5);
			loPf1.addIncludeProperty(loFE6);
			loPf1.addIncludeProperty(loFE7);
			loPf1.addIncludeProperty(loFE8);
			loPropertyMap.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "");
			loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, "");
			loPropertyMap.put(HHSR5Constants.SHARING_FLAG, "");
			Folder loMovingFolder = Factory.Folder.fetchInstance(aoObjStr, new Id(loSourceFolderObj.getDocumentId()),
					loPf1);
			Properties loSourceFolderProps = loMovingFolder.getProperties();
			// if destination folder is shared
			if (null != loDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG)
					&& loDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG).equalsIgnoreCase(
							HHSR5Constants.TWO))
			{
				@SuppressWarnings("rawtypes")
				HashMap loHashMap = new HashMap();
				loHashMap.put(loSourceFolderObj.getDocumentId(), HHSR5Constants.FOLDER);
				if (null != loSourceFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& loSourceFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString()
								.equalsIgnoreCase(loSourceFolderObj.getDocumentId()))
				{	//null passed to remove sharing from child folders 4.0.2.0
					removeDocumentsSharing(aoObjStr, loHashMap, null);
					//null passed to remove sharing from child folders 4.0.2.0
				}
				loSourceFolderProps.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.TWO);
				loSourceFolderProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID,
						loDestinationFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID));

				setFolderChildSharingProperty(aoObjStr,
						loDestinationFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString(), loPropertyMap,
						loSourceFolderObj.getDocumentId(), HHSR5Constants.TWO);
				lbSharedFlag = true;
			}
			// if destination folder is not shared
			else if (null != loSourceFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG)
					&& !loSourceFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG).equalsIgnoreCase(
							HHSR5Constants.ZERO)
					&& null != loDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG)
					&& loDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG).equalsIgnoreCase(
							HHSR5Constants.ZERO))
			{
				if (null != loSourceFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& !loSourceFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString()
								.equalsIgnoreCase(loSourceFolderObj.getDocumentId()))
				{
					loSourceFolderProps.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.ZERO);
					loSourceFolderProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
					setFolderChildSharingProperty(aoObjStr, null, loPropertyMap, loSourceFolderObj.getDocumentId(),
							HHSR5Constants.ZERO);
				}
				else
				{
					UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
							RefreshMode.REFRESH);
					updateFolderSharedStatus(aoObjStr, HHSR5Constants.ONE, aoDestinationFolder.get_PathName(),
							loUpdateBatch);
					if (loUpdateBatch.hasPendingExecute())
						loUpdateBatch.updateBatch();
					resetFolderLinkAndShareStatus(aoObjStr, loMovingFolder.get_Parent().get_PathName(),
							HHSR5Constants.SHARING_FLAG, HHSR5Constants.ALL_SHARED, true);
				}
			}
			loMovingFolder.move(aoDestinationFolder);
			loMovingFolder.setUpdateSequenceNumber(null);
			loMovingFolder.save(RefreshMode.REFRESH);

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in moving folder Object", aoEx);
			LOG_OBJECT.Error("Exception in moving folder Object", aoEx);
			throw loAppex;
		}
		return lbSharedFlag;
	}

	/**
	 * This Method will move the documents
	 * @param aoObjStr
	 * @param loDocument
	 * @param aoDestinationFolderProps
	 * @throws ApplicationException
	 */
	private Boolean moveDocumentObject(ObjectStore aoObjStr, com.nyc.hhs.model.Document loDocument,
			Properties aoDestinationFolderProps) throws ApplicationException
	{
		Boolean sharedFlag = false;
		try
		{
			LOG_OBJECT.Info("Enter P8ContentOperations.moveDocumentObject()");
			FilterElement loFE5 = new FilterElement(null, null, null, HHSR5Constants.PARENT_PATH, null);
			FilterElement loFE6 = new FilterElement(null, null, null, HHSR5Constants.SHARING_FLAG, null);
			PropertyFilter loPf2 = new PropertyFilter();
			loPf2.addIncludeProperty(loFE5);
			loPf2.addIncludeProperty(loFE6);
			Document loMovingDoc = Factory.Document.fetchInstance(aoObjStr, new Id(loDocument.getDocumentId()), null);
			Properties loMovingDocProps = loMovingDoc.getProperties();
			// In Case Of recycle bin, parent_path value will be
			// moveFrom
			loMovingDocProps.putValue(HHSR5Constants.PARENT_PATH,
					aoDestinationFolderProps.getStringValue(HHSR5Constants.PATH_NAME));
			// added check for parent sharing flag

			if (null != aoDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG)
					&& aoDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG).equalsIgnoreCase(
							HHSR5Constants.TWO))
			{
				HashMap loHashMap = new HashMap();
				loHashMap.put(loDocument.getDocumentId(), HHSR5Constants.DOCUMENT);
				loMovingDocProps.putValue(HHSR5Constants.SHARING_FLAG,
						aoDestinationFolderProps.getStringValue(HHSR5Constants.SHARING_FLAG));
				loMovingDocProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID,
						aoDestinationFolderProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID));
				//null passed to remove sharing from child folders 4.0.2.0		
				removeDocumentsSharing(aoObjStr, loHashMap, null);
				//null passed to remove sharing from child folders 4.0.2.0
				sharedFlag = true;
			}
			else
			{
				if (null != loMovingDocProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& !loMovingDocProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString()
								.equalsIgnoreCase(loDocument.getDocumentId()))
				{
					loMovingDocProps.putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.ZERO);
					loMovingDocProps.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
				}
				else if (null != loMovingDocProps.getIdValue(HHSR5Constants.SHARED_ENTITY_ID))
				{
					UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
							RefreshMode.REFRESH);
					updateFolderSharedStatus(aoObjStr, HHSR5Constants.ONE,
							aoDestinationFolderProps.getStringValue(HHSR5Constants.PATH_NAME), loUpdateBatch);
					if (loUpdateBatch.hasPendingExecute())
						loUpdateBatch.updateBatch();
					Folder loMovingFromFolder = Factory.Folder.fetchInstance(aoObjStr,
							new Id(loDocument.getMoveFromPath()), null);
					resetFolderLinkAndShareStatus(aoObjStr, loMovingFromFolder.get_PathName(),
							HHSR5Constants.SHARING_FLAG, HHSR5Constants.ALL_SHARED, true);
				}
			}
			loMovingDoc.setUpdateSequenceNumber(null);
			loMovingDoc.save(RefreshMode.REFRESH);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in moving document Object", aoEx);
			LOG_OBJECT.Error("Exception in moving document Object", aoEx);
			throw loAppex;
		}
		return sharedFlag;
	}

	/**
	 * This method will fetch objects filed in through entity going to be moved
	 * @param aoObjStr
	 * @param lomoveDocumentList
	 * @return
	 * @throws ApplicationException
	 */
	public IndependentObjectSet fetchFileInObject(ObjectStore aoObjStr, com.nyc.hhs.model.Document lomoveDocument)
			throws ApplicationException
	{
		String lsSQLQuery = null;
		LOG_OBJECT.Info("Entered P8ContentOperations.fetchFileInObject()");
		String lswhereClause = null;
		IndependentObjectSet loRowSet = null;
		try
		{
			lswhereClause = createRCRWhereClause(lomoveDocument);

			lsSQLQuery = "select rcr.tail from ReferentialContainmentRelationship rcr" + lswhereClause;
			SearchSQL loSqlObject = new SearchSQL();
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, true);
		}
		catch (ApplicationException aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in fetchFileInObject", aoEx);
			LOG_OBJECT.Error("Exception in fetchFileInObject", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.fetchFileInObject()");
		return loRowSet;
	}

	/**
	 * This method will create rcr where clause for dynamic Query
	 * @param aomoveItemList
	 * @return
	 * @throws ApplicationException
	 */
	private String createRCRWhereClause(com.nyc.hhs.model.Document aomoveItem) throws ApplicationException
	{
		StringBuffer whereClause = new StringBuffer();
		try
		{
			whereClause.append(" where ");
			whereClause.append("rcr.head =object(");
			whereClause.append(aomoveItem.getDocumentId() + ")");
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in creating where clause for RCR", aoEx);
			LOG_OBJECT.Error("Exception in creating where clause for RCR", aoEx);
			throw loAppex;
		}
		return whereClause.toString();
	}

	/**
	 * This method will fetch the list of children depends on level
	 * @param aoObjStr
	 * @param asEntityId
	 * @param aoLevel
	 * @param asAction	
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<com.nyc.hhs.model.Document> getChildList(ObjectStore aoObjStr, String asEntityId, String aoLevel,
			List<com.nyc.hhs.model.Document> aoFolderIdList, String asAction) throws ApplicationException
	{

		SearchSQL loSqlObject = new SearchSQL();
		LOG_OBJECT.Info("Entered P8ContentOperations.getChildList()");
		String lsSQLQuery = null;
		List<com.nyc.hhs.model.Document> loDocumentList = new ArrayList<com.nyc.hhs.model.Document>();
		//variable added for setting delete flag accordingly 4.0.2.0
		int liDeleteFlag = 0;
		//variable added for setting delete flag accordingly 4.0.2.0
		try
		{
			if (null == aoFolderIdList)
			{
				aoFolderIdList = new ArrayList<com.nyc.hhs.model.Document>();
			}
			//null check added to set detete flag 4.0.2.0
			if(null!=asAction && HHSR5Constants.DELETE.equalsIgnoreCase(asAction)){
				liDeleteFlag = 1;
			}
			//null check added to set detete flag 4.0.2.0
			
			//Change in query to fetch childs according to delete flag accordingly 4.0.2.0
			lsSQLQuery = "SELECT rcr.tail as PARENT_OBJECT,DOC.ID,FO.ID as FOLDER_ID ,DOC.DOC_TYPE ,FO.PathName, FO.LINK_TO_APPLICATION AS FOLDER_LINKAGE, DOC.LINK_TO_APPLICATION FROM (ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) full join HHSCustomFolder fo on rcr.head = object(fo.id)  WHERE rcr.head "
					+ aoLevel + " '" + asEntityId + "' AND (FO.DELETE_FLAG = "+liDeleteFlag+" OR DOC.DELETE_FLAG  = "+liDeleteFlag+")";
			//Change in query to fetch childs according to delete flag accordingly 4.0.2.0	
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			RepositoryRowSet loRepositoryRowSet = (RepositoryRowSet) loSearchScope.fetchRows(loSqlObject, null, null,
					Boolean.TRUE);
			Iterator loItr = loRepositoryRowSet.iterator();
			while (loItr.hasNext())
			{
				com.nyc.hhs.model.Document loDoc = new com.nyc.hhs.model.Document();
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();
				IndependentObject loIndObj = (IndependentObject) loProp.getObjectValue(HHSR5Constants.PARENT_OBJECT);
				if (!loIndObj.get_ClassDescription().get_DisplayName().equalsIgnoreCase(HHSR5Constants.FILENET_FOLDER))
				{
					Folder loFldrObj = (Folder) loIndObj;
					if (null != loProp.getObjectValue(HHSR5Constants.TEMPLATE_IDEN))
					{
						loDoc.setDocumentId(loProp.getObjectValue(HHSR5Constants.TEMPLATE_IDEN).toString());
						loDoc.setDocType(loProp.getObjectValue(P8Constants.PROPERTY_CE_DOC_TYPE).toString());
						loDoc.setLinkToApplication(loProp.getBooleanValue("LINK_TO_APPLICATION"));
						loDoc.setParent(loFldrObj);
						loDocumentList.add(loDoc);
					}
					else if (aoFolderIdList.toString().indexOf(
							loProp.getObjectValue(HHSR5Constants.FILENET_FOLDER_ID).toString()) < 0)
					{
						loDoc.setDocumentId(loProp.getObjectValue(HHSR5Constants.FILENET_FOLDER_ID).toString());
						loDoc.setDocType(HHSR5Constants.EMPTY_STRING);
						loDoc.setFolderLocation(loProp.getStringValue(HHSR5Constants.PATH_NAME));
						loDoc.setLinkToApplication(loProp.getBooleanValue("FOLDER_LINKAGE"));
						loDoc.setParent(loFldrObj);
						loDocumentList.add(loDoc);
					}

				}

			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in fetching child list", aoEx);
			LOG_OBJECT.Error("Exception in fetching child list", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getChildList()");
		return loDocumentList;
	}

	/**
	 * This method will fetch the sharing status for Filenet used in Move
	 * functionality
	 * @param aoUserSession
	 * @param aoDoc
	 * @return
	 * @throws ApplicationException
	 */
	public String getSharingStatus(P8UserSession aoUserSession, com.nyc.hhs.model.Document aoDoc)
			throws ApplicationException
	{
		ObjectStore loOS = null;
		LOG_OBJECT.Info("Entered P8ContentOperations.getSharingStatus()");
		String lsStatus = null;
		try
		{
			loOS = filenetConnection.getObjectStore(aoUserSession);
			FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.SHARING_FLAG, null);
			PropertyFilter loPf = new PropertyFilter();
			loPf.addIncludeProperty(loFE);
			if (null != aoDoc && null != aoDoc.getDocumentId() && !aoDoc.getDocumentId().isEmpty())
			{
				Folder loFolder = Factory.Folder.fetchInstance(loOS, aoDoc.getDocumentId(), loPf);
				Properties loPropFldr = loFolder.getProperties();
				lsStatus = loPropFldr.getStringValue(HHSR5Constants.SHARING_FLAG);

			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in getSharingStatus", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in getSharingStatus", aoEx);
			LOG_OBJECT.Error("Exception in getSharingStatus", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getSharingStatus()");
		return lsStatus;

	}

	/**
	 * This method will set document /folder properties while deleting from
	 * document vault
	 * @param aoObjStr
	 * @param aoDelList
	 * @param aoReqMap
	 * @param aoType
	 * @return
	 * @throws ApplicationException
	 */
	public HashMap<String, Object> softDeleteFromFilenet(ObjectStore aoObjStr,
			List<com.nyc.hhs.model.Document> aoDelList, HashMap<String, String> aoReqMap, String aoType,
			HashMap<String, List<String>> aoEntityMap) throws ApplicationException
	{
		List<String> lsEntityIdForDeletion = new ArrayList<String>();
		HashMap<String, String> lsMessageMap = new HashMap<String, String>();
		HashMap<String, Object> loDataMap = new HashMap<String, Object>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.softDeleteFromFilenet()");
			String lsPath = FileNetOperationsUtils.setFolderPath(
					(String) aoReqMap.get(HHSR5Constants.ORGANIZATION_TYPE),
					(String) aoReqMap.get(HHSR5Constants.ORGANIZATION_ID), HHSR5Constants.RECYCLE_BIN);
			if (null != aoDelList && !aoDelList.isEmpty())
			{
				Folder loFldrRB = Factory.Folder.fetchInstance(aoObjStr, lsPath, null);
				FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.DELETE_FLAG, null);
				PropertyFilter loPf = new PropertyFilter();
				loPf.addIncludeProperty(loFE);
				if (null != aoType && !aoType.isEmpty() && aoType.equalsIgnoreCase(HHSR5Constants.DOCUMENT))
				{
					lsMessageMap = softDeleteDocProp(aoObjStr, aoDelList, aoReqMap, lsEntityIdForDeletion, loFldrRB,
							loPf, aoEntityMap);
				}
				else
				{
					lsMessageMap = softDeleteFolderProp(aoObjStr, aoDelList, aoReqMap, lsEntityIdForDeletion, loFldrRB,
							loPf, aoEntityMap);
				}
			}
			LOG_OBJECT.Info("Exited P8ContentOperations.softDeleteFromFilenet()");
			loDataMap.put(HHSR5Constants.ENTITY_ID_FOR_DELETION, lsEntityIdForDeletion);
			loDataMap.put("lsMessage", lsMessageMap);
			return loDataMap;
		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Exception in softDeleteFromFilenet while deletion", aoEx);
			LOG_OBJECT.Error("Exception in softDeleteFromFilenet while deletion", aoEx);
			throw loAppex;
		}
	}

	/**
	 * The method is added in Release 5 which will update the meta data of
	 * folder during delete operation.
	 * @param aoObjStr
	 * @param aoDelList
	 * @param aoReqMap
	 * @param lsEntityIdForDeletion
	 * @param aoFldrRB
	 * @param aoPropertyFilter
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, String> softDeleteFolderProp(ObjectStore aoObjStr,
			List<com.nyc.hhs.model.Document> aoDelList, HashMap<String, String> aoReqMap,
			List<String> lsEntityIdForDeletion, Folder aoFldrRB, PropertyFilter aoPropertyFilter,
			HashMap<String, List<String>> aoEntityMap) throws ApplicationException
	{

		LOG_OBJECT.Info("Enter P8ContentOperations.softDeleteFolderProp()");
		HashMap<String, String> loPropertyMap = new HashMap<String, String>();
		HashMap<String, String> lsMessgetoDisplayMap = new HashMap<String, String>();
		String lsPropertyPath = P8Constants.ERROR_PROPERTY_FILE;
		String lsMessgetoDisplay = null;
		try
		{

			loPropertyMap.put(HHSR5Constants.IS_FOLDER_SHARED, "");
			loPropertyMap.put(HHSR5Constants.SHARING_FLAG, "");
			loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, "");
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
					RefreshMode.REFRESH);
			HashMap<String, String> loMap = new HashMap<String, String>();
			loMap.put(HHSR5Constants.PATH_NAME, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.DELETED_DATE, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.FILENET_DELETED_BY, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.FILENET_FOLDER_NAME, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.SHARED_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.SHARING_FLAG, HHSR5Constants.EMPTY_STRING);
			aoPropertyFilter = createPropertyFilter(loMap);
			List<String> loTemp = new ArrayList<String>();
			int lsDeleteCount = 0;
			for (Iterator iterator = aoDelList.iterator(); iterator.hasNext();)
			{
				com.nyc.hhs.model.Document loFolderBean = (com.nyc.hhs.model.Document) iterator.next();
				Folder loFolder = Factory.Folder.fetchInstance(aoObjStr, new Id(loFolderBean.getDocumentId()),
						aoPropertyFilter);
				Properties loProp = loFolder.getProperties();
				if (null != loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& loFolderBean.getDocumentId().equalsIgnoreCase(
								loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString())
						&& loFolderBean.getDeleteFlag() > 0)
				{
					aoEntityMap.put(loFolderBean.getDocumentId() + HHSR5Constants.FOLDER_WITH_COMMA, loTemp);
				}
				loProp.putValue(HHSR5Constants.FILENET_MOVE_FROM, loProp.getStringValue(HHSR5Constants.PATH_NAME));
				loProp.putValue(HHSR5Constants.DELETE_FLAG, loFolderBean.getDeleteFlag());
				// reseting sharing flag of parent folder has been moved to last
				// service of this transaction.
				loProp.putValue(HHSR5Constants.IS_ARCHIVE, loFolderBean.getShowInRB());
				loProp.putValue(HHSR5Constants.DELETE_ENTITY_ID, loFolderBean.getDeletionEntityId());
				loProp.putValue(HHSR5Constants.DELETED_DATE, HHSUtil.getCurrentTimestampDate());
				loProp.putValue(HHSR5Constants.FILENET_DELETED_BY,
						(String) aoReqMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
				loProp.putValue(HHSR5Constants.ORIGINAL_FOLDER_NAME,
						loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));
				loProp.putValue(HHSR5Constants.ORIGINAL_FOLDER_NAME,
						loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));

				if (loFolderBean.getDeleteFlag() > 0)
				{
					lsDeleteCount++;
					lsEntityIdForDeletion.add(loFolderBean.getDocumentId());
					loUpdateBatch.add(loFolder, null);
					if (loFolderBean.getDeleteFlag() == 2)
					{
						loFolder.set_FolderName(loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME)
								+ System.currentTimeMillis());
						loFolder.move(aoFldrRB);
						IndependentObjectSet loRowSet = fetchFileInObject(aoObjStr, loFolderBean);
						Iterator itr = loRowSet.iterator();
						while (itr.hasNext())
						{
							ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) itr.next();
							if (rcr.get_Tail().getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
							{
								rcr.set_Tail(aoFldrRB);
								loUpdateBatch.add(rcr, null);
							}
						}
					}
				}
				else if (loFolderBean.getShowInRB())
				{
					loProp.putValue(HHSR5Constants.REPLICATION_ID, loFolderBean.getDocumentId());
					loUpdateBatch.add(loFolder, null);
					Folder loFldr = Factory.Folder.createInstance(aoObjStr, HHSR5Constants.HHS_CUSTOM_FOLDER);
					loFldr.set_Parent(aoFldrRB);
					String lsName = loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME);
					Properties loPropNew = loFldr.getProperties();
					loPropNew.putValue(HHSR5Constants.ORIGINAL_FOLDER_NAME, lsName);
					loPropNew.putValue(HHSR5Constants.FILENET_MOVE_FROM,
							loProp.getStringValue(HHSR5Constants.PATH_NAME));
					loPropNew.putValue(HHSR5Constants.DELETE_FLAG, 2);
					loPropNew.putValue(HHSR5Constants.IS_ARCHIVE, true);
					loPropNew.putValue(HHSR5Constants.DELETED_DATE, HHSUtil.getCurrentTimestampDate());
					loPropNew.putValue(HHSR5Constants.FILENET_DELETED_BY,
							(String) aoReqMap.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
					loPropNew.putValue(P8Constants.PROPERTY_CE_PROVIDER_ID,
							(String) aoReqMap.get(HHSR5Constants.ORGANIZATION_ID));
					loPropNew.putValue(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
							HHSUtil.getCurrentTimestampDate());
					loPropNew.putValue(HHSR5Constants.DELETE_ENTITY_ID, loFolderBean.getDocumentId());
					loPropNew.putValue(HHSR5Constants.REPLICATION_ID, loFolderBean.getDocumentId());
					if (checkFolderExists(
							aoObjStr,
							FileNetOperationsUtils.setFolderPath(
									(String) aoReqMap.get(HHSR5Constants.ORGANIZATION_TYPE),
									(String) aoReqMap.get(HHSR5Constants.ORGANIZATION_ID), HHSR5Constants.RECYCLE_BIN)
									+ "/" + lsName))
					{
						loFldr.set_FolderName(lsName + System.currentTimeMillis());
					}
					else
					{
						loFldr.set_FolderName(lsName);
					}

					loFldr.save(RefreshMode.REFRESH);
					loUpdateBatch.updateBatch();
					ReferentialContainmentRelationship loRCR = aoFldrRB.file(loFldr, AutoUniqueName.AUTO_UNIQUE, loFldr
							.get_Id().toString(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
					loUpdateBatch.add(loRCR, null);
				}
			}

			int liNonDeleteCount = aoDelList.size() - lsDeleteCount;
			if (null != aoDelList && aoDelList.size() == lsDeleteCount)
			{
				lsMessgetoDisplayMap.put("message",
						PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.SUCCESS_DELETED));
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else if (liNonDeleteCount == aoDelList.size() && aoDelList.size() > 1)
			{
				lsMessgetoDisplay = PropertyLoader.getProperty(lsPropertyPath, HHSR5Constants.NON_DELETION_MESSAGE);
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			else if (liNonDeleteCount == 1 && aoDelList.size() == 1)
			{

				lsMessgetoDisplay = aoDelList.get(0).getMsEntityId();
				if (null == lsMessgetoDisplay || lsMessgetoDisplay.isEmpty())
				{
					lsMessgetoDisplay = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M10);
				}
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_FAIL_TYPE);

			}
			// defect 8043
			else if (liNonDeleteCount > 0)
			{
				lsMessgetoDisplay = PropertyLoader.getProperty(lsPropertyPath, HHSR5Constants.PARTIALLY_DELETED);
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_PASS_TYPE);
			}

			if (loUpdateBatch.hasPendingExecute())
			{
				loUpdateBatch.updateBatch();
			}
			LOG_OBJECT.Info("Exit P8ContentOperations.softDeleteFolderProp()");
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in softDeleteFolderProp while Deleting",
					aoEx);
			LOG_OBJECT.Error("Exception in softDeleteFolderProp while Deleting", aoEx);
			throw loAppex;
		}
		return lsMessgetoDisplayMap;
	}

	/**
	 * This method will set document properties for delete operation
	 * @param aoObjStr
	 * @param aoDelList
	 * @param aoReqMap
	 * @param lsEntityIdForDeletion
	 * @param loFldrRB
	 * @param loPf
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private HashMap<String, String> softDeleteDocProp(ObjectStore aoObjStr, List<com.nyc.hhs.model.Document> aoDelList,
			HashMap<String, String> aoReqMap, List<String> lsEntityIdForDeletion, Folder loFldrRB, PropertyFilter loPf,
			HashMap<String, List<String>> aoEntityMap) throws ApplicationException
	{
		HashMap<String, String> loPropertyMap = new HashMap<String, String>();
		String lsPropertyPath = P8Constants.ERROR_PROPERTY_FILE;
		HashMap<String, String> lsMessgetoDisplayMap = new HashMap<String, String>();
		String lsMessgetoDisplay = null;
		LOG_OBJECT.Info("Enter P8ContentOperations.softDeleteDocProp()");
		try
		{
			loPropertyMap.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, "");
			loPropertyMap.put(HHSR5Constants.SHARING_FLAG, "");
			loPropertyMap.put(HHSR5Constants.SHARED_ENTITY_ID, "");
			String lsFolderId = null;
			int liDeleteCount = 0;
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
					RefreshMode.REFRESH);
			HashMap<String, String> loMap = new HashMap<String, String>();
			loMap.put(HHSR5Constants.PARENT_PATH, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.DELETED_DATE, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.FILENET_DELETED_BY, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.SHARED_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.SHARING_FLAG, HHSR5Constants.EMPTY_STRING);
			loMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSR5Constants.EMPTY_STRING);
			loMap.put(HHSR5Constants.FOLDERS_FILED_IN, "");
			// Changes for making linkage false in filenet after 4.0.2 as we were not making the Linkage false in filenet on document deletion
			loMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, HHSR5Constants.EMPTY_STRING);
			loPf = createPropertyFilter(loMap);
			List<String> loTemp = new ArrayList<String>();
			for (Iterator iterator = aoDelList.iterator(); iterator.hasNext();)
			{
				com.nyc.hhs.model.Document loDocBean = (com.nyc.hhs.model.Document) iterator.next();
				Document loDoc = Factory.Document.fetchInstance(aoObjStr, new Id(loDocBean.getDocumentId()), loPf);
				String lsSharingFlag = null;

				Properties loProp = loDoc.getProperties();
				if (null != loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID)
						&& loDocBean.getDocumentId().equalsIgnoreCase(
								loProp.getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString())
						&& null != loDocBean.getDeleteFlag() && loDocBean.getDeleteFlag() > 0)
				{
					aoEntityMap.put(loDocBean.getDocumentId() + HHSR5Constants.DOC_WITH_COMMA, loTemp);
				}
				FolderSet loFolderSet = (FolderSet) loProp.getObjectValue(HHSR5Constants.FOLDERS_FILED_IN);
				for (Iterator iterator1 = loFolderSet.iterator(); iterator1.hasNext();)
				{
					Folder type = (Folder) iterator1.next();
					if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
					{
						loProp.putValue(HHSR5Constants.PARENT_PATH, type.get_PathName());
						lsFolderId = type.get_Id().toString();
					}

				}
				//Adding and condition for New Filling Check after 4.0.2 so that only deletable entry will be deleted.
				if (loDocBean.isChar500Flag() && loDocBean.getDeleteFlag() > 0)
				{
					liDeleteCount++;
					loDoc.delete();
					lsEntityIdForDeletion.add(lsFolderId);
					loUpdateBatch.add(loDoc, null);
				}
				lsSharingFlag = loProp.getStringValue(HHSR5Constants.SHARING_FLAG);
				//Adding not char 500 condition for New Filling Check after 4.0.2 so that only deletable entry will be deleted.
				if (null != loDocBean.getDeleteFlag() && loDocBean.getDeleteFlag() > 0 && !loDocBean.isChar500Flag())
				{
					liDeleteCount++;
					lsEntityIdForDeletion.add(lsFolderId);
					loProp.putValue(HHSR5Constants.DELETE_FLAG, loDocBean.getDeleteFlag());
					loProp.putValue(HHSR5Constants.DELETE_ENTITY_ID, loDocBean.getDeletionEntityId());
					loProp.putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, null);
					//Change in value for defect 8385 Emergency build no 4.0.2.0 
					loProp.putValue(HHSR5Constants.SHARING_FLAG,HHSR5Constants.ZERO);
					//Change in value for defect 8385 Emergency build no 4.0.2.0
					loProp.putValue(HHSR5Constants.IS_ARCHIVE, loDocBean.getShowInRB());
					// Added for 7771
					loProp.putValue(HHSR5Constants.DELETED_DATE, HHSUtil.getCurrentTimestampDate());
					// Changes for making linkage false in filenet after 4.0.2 as we were not making the Linkage false in filenet on document deletion
					loProp.putValue(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
					loProp.putValue(HHSR5Constants.FILENET_DELETED_BY, (String) aoReqMap.get("HHS_DOC_MODIFIED_BY"));
					loDoc.save(RefreshMode.REFRESH);
					if (loDocBean.getDeleteFlag() == 2)
					{
						com.nyc.hhs.model.Document loDocObj = new com.nyc.hhs.model.Document();
						loDocObj.setDocumentId(loDocBean.getDocumentId());
						IndependentObjectSet loRowSet = fetchFileInObject(aoObjStr, loDocObj);
						Iterator itr = loRowSet.iterator();
						while (itr.hasNext())
						{
							ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) itr.next();
							if (rcr.get_Tail().getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
							{
								Folder loFldr = (Folder) rcr.get_Tail();
								rcr.set_Tail(loFldrRB);
								rcr.save(RefreshMode.REFRESH);
							}

						}
						if (null != lsSharingFlag && !lsSharingFlag.equalsIgnoreCase(HHSR5Constants.ZERO))
						{
							setParentPropertyForUnsharing(aoObjStr, loDocBean.getParent().get_PathName(),
									loDocBean.getDocumentId());
						}
					}

				}
			}
			int liNonDeleteCount = aoDelList.size() - liDeleteCount;
			if (null != aoDelList && aoDelList.size() == liDeleteCount)
			{
				lsMessgetoDisplayMap.put("message",
						PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.SUCCESS_DELETED));
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			else if (liNonDeleteCount == aoDelList.size() && aoDelList.size() > 1)
			{
				lsMessgetoDisplay = PropertyLoader.getProperty(lsPropertyPath, HHSR5Constants.NON_DELETION_MESSAGE);
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			else if (liNonDeleteCount == 1 && aoDelList.size() == 1)
			{
				lsMessgetoDisplay = aoDelList.get(0).getMsEntityId();
				if (null == lsMessgetoDisplay || lsMessgetoDisplay.isEmpty())
				{
					lsMessgetoDisplay = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M10);
				}
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_FAIL_TYPE);

			}
			// defect 8043
			else if (liNonDeleteCount > 0)
			{
				lsMessgetoDisplay = PropertyLoader.getProperty(lsPropertyPath, HHSR5Constants.PARTIALLY_DELETED);
				lsMessgetoDisplayMap.put("message", lsMessgetoDisplay);
				lsMessgetoDisplayMap.put("type", ApplicationConstants.MESSAGE_PASS_TYPE);
			}
			if (loUpdateBatch.hasPendingExecute())
			{
				loUpdateBatch.updateBatch();
			}
			LOG_OBJECT.Info("Exit P8ContentOperations.softDeleteDocProp()");
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in softDeleteDocProp while Deleting",
					aoEx);
			LOG_OBJECT.Error("Exception in softDeleteDocProp while Deleting", aoEx);
			throw loAppex;
		}
		return lsMessgetoDisplayMap;
	}

	/**
	 * This method will fetch document property from filenet based on Document
	 * Id
	 * @param aoObjStr
	 * @param asDocId
	 * @param aoType
	 * @return
	 * @throws ApplicationException
	 */
	public com.nyc.hhs.model.Document getDocProp(ObjectStore aoObjStr, String asDocId, String aoType,
			PropertyFilter aoPF) throws ApplicationException
	{
		com.nyc.hhs.model.Document loDocument = new com.nyc.hhs.model.Document();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.getDocProp()");
			if (null != aoType && !aoType.isEmpty() && aoType.equalsIgnoreCase(HHSR5Constants.DOCUMENT))
			{
				Document loDoc = Factory.Document.fetchInstance(aoObjStr, new Id(asDocId), aoPF);
				Properties loProp = loDoc.getProperties();
				loDocument.setFilePath(loProp.getStringValue(HHSR5Constants.PARENT_PATH));
				loDocument.setDocName(loProp.getStringValue(HHSConstants.DOCUMENT_TITLE));
				loDocument.setDocType(loProp.getStringValue(HHSR5Constants.DOC_TYPE));
				loDocument.setOrganizationId(loProp.getStringValue(P8Constants.PROPERTY_CE_PROVIDER_ID));
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in getDocProperties while Restoring",
					aoEx);
			LOG_OBJECT.Error("Exception in getDocProperties while Restoring", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getDocProp()");
		return loDocument;

	}

	/**
	 * This method will set document property while restoring documents/Folder
	 * from recycleBin
	 * @param aoObjStr
	 * @param asDocId
	 * @param loPropMap
	 * @param aoType
	 * @param loFlder
	 * @return
	 * @throws ApplicationException
	 */
	public com.nyc.hhs.model.Document setPropertiesForRestoration(ObjectStore aoObjStr, String asDocId,
			HashMap<String, Object> loPropMap, String aoType, Folder loFlder) throws ApplicationException
	{
		com.nyc.hhs.model.Document loDocument = new com.nyc.hhs.model.Document();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setPropertiesForRestoration()");
			if (null != aoType && !aoType.isEmpty() && aoType.equalsIgnoreCase(HHSR5Constants.DOCUMENT))
			{
				setDocPropForRestore(aoObjStr, asDocId, loPropMap, loFlder);
			}
			else
			{
				setFolderPropForRestore(aoObjStr, asDocId, loPropMap, loFlder);
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in setDocProperties for Restore", aoEx);
			LOG_OBJECT.Error("Exception in setDocProperties for Restore", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setPropertiesForRestoration()");
		return loDocument;

	}

	/**
	 * This method will set Folder Property in Restoring process
	 * @param aoObjStr
	 * @param asFolderId
	 * @param loPropMap
	 * @param loFlder
	 * @throws ApplicationException
	 */
	private void setFolderPropForRestore(ObjectStore aoObjStr, String asFolderId, HashMap<String, Object> loPropMap,
			Folder loFlder) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setFolderPropForRestore()");
			// Defect fix for 7250
			Folder loFldr = Factory.Folder.fetchInstance(aoObjStr, new Id(asFolderId), null);
			Properties loProp = loFldr.getProperties();
			loProp.putValue(HHSR5Constants.FILENET_FOLDER_NAME,
					loProp.getStringValue(HHSR5Constants.ORIGINAL_FOLDER_NAME));
			if (null != loPropMap && !loPropMap.isEmpty())
			{
				for (Map.Entry<String, Object> entry : loPropMap.entrySet())
				{
					if (null != entry.getKey() && !entry.getKey().equalsIgnoreCase(HHSR5Constants.ORGANIZATION_TYPE)
							&& !entry.getKey().equalsIgnoreCase(HHSR5Constants.ORGANIZATION_ID_KEY))
					{
						loProp.putObjectValue(entry.getKey(), entry.getValue());
					}

				}
			}
			com.nyc.hhs.model.Document loDocObj = new com.nyc.hhs.model.Document();
			loDocObj.setDocumentId(loFldr.get_Id().toString());
			IndependentObjectSet loRowSet = fetchFileInObject(aoObjStr, loDocObj);
			Iterator itr = loRowSet.iterator();
			while (itr.hasNext())
			{
				ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) itr.next();
				if (rcr.get_Tail().getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
				{
					rcr.set_Tail(loFlder);
					rcr.save(RefreshMode.REFRESH);
				}

			}
			loFldr.move(loFlder);
			loFldr.save(RefreshMode.REFRESH);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in setFolderPropForRestore", aoEx);
			LOG_OBJECT.Error("Exception in setFolderPropForRestore", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setFolderPropForRestore()");
	}

	/**
	 * This method will set Document Property in Restoring process
	 * @param aoObjStr
	 * @param asDocId
	 * @param loPropMap
	 * @param loFlder
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private void setDocPropForRestore(ObjectStore aoObjStr, String asDocId, HashMap<String, Object> loPropMap,
			Folder loFlder) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setDocPropForRestore()");
			// Defect fix for 7250
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
					RefreshMode.REFRESH);
			Document loDoc = Factory.Document.fetchInstance(aoObjStr, new Id(asDocId), null);
			Properties loProp = loDoc.getProperties();
			if (null != loPropMap && !loPropMap.isEmpty())
			{
				for (Map.Entry<String, Object> entry : loPropMap.entrySet())
				{
					if (null != entry.getKey() && !entry.getKey().equalsIgnoreCase(HHSR5Constants.ORGANIZATION_TYPE)
							&& !entry.getKey().equalsIgnoreCase(HHSR5Constants.ORGANIZATION_ID_KEY))
					{
						loProp.putObjectValue(entry.getKey(), entry.getValue());
					}

				}
			}
			loUpdateBatch.add(loDoc, null);
			if (null != loFlder)
			{
				com.nyc.hhs.model.Document loDocObj = new com.nyc.hhs.model.Document();
				loDocObj.setDocumentId(asDocId);
				IndependentObjectSet loRowSet = fetchFileInObject(aoObjStr, loDocObj);
				Iterator itr = loRowSet.iterator();
				while (itr.hasNext())
				{
					ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship) itr.next();
					if (rcr.get_Tail().getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
					{

						rcr.set_Tail(loFlder);
						loUpdateBatch.add(rcr, null);
					}

				}
			}
			if (loUpdateBatch.hasPendingExecute())
			{
				loUpdateBatch.updateBatch();
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in setDocPropForRestore", aoEx);
			LOG_OBJECT.Error("Exception in setDocPropForRestore", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setDocPropForRestore()");
	}

	/**
	 * This method will fetch deleted children deleted due to parent folder
	 * @param aoObjStr
	 * @param aoFldrId
	 * @param aoType
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public List<com.nyc.hhs.model.Document> fetchDeletedChild(ObjectStore aoObjStr, String aoFldrId, String aoType)
			throws ApplicationException
	{
		SearchSQL loSqlObject = new SearchSQL();
		String lsSQLQuery = null;
		List<com.nyc.hhs.model.Document> loDocList = new ArrayList<com.nyc.hhs.model.Document>();
		try
		{	//Query updated for defect 8374 Emergency release 4.0.1.0
			LOG_OBJECT.Info("Entered P8ContentOperations.fetchDeletedChild()");
			if (null != aoType && !aoType.isEmpty() && aoType.equalsIgnoreCase(HHSR5Constants.DOCUMENT))
			{
				lsSQLQuery = "select doc.PARENT_PATH,doc.DOC_TYPE,doc.DocumentTitle,doc.Id FROM HHS_ACCELERATOR doc WHERE doc.DELETE_ENTITY_ID='"
						+ aoFldrId + HHSR5Constants.STR+" and doc.delete_flag<>0";
			}
			else
			{
				lsSQLQuery = "select fo.PathName,fo.MOVE_FROM,fo.FolderName,fo.Id as FOLDER_ID FROM HHS_CUSTOM_FOLDER fo WHERE fo.DELETE_ENTITY_ID='"
						+ aoFldrId + HHSR5Constants.STR+" and fo.delete_flag<>0";
			}
			//Query updated for defect 8374 Emergency release 4.0.1.0
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loItr = loEngineSet.iterator();
			while (loItr.hasNext())
			{
				com.nyc.hhs.model.Document loDoc = new com.nyc.hhs.model.Document();
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();

				if (loProp.isPropertyPresent(HHSR5Constants.DOC_TYPE))
				{
					loDoc.setFilePath(loProp.getStringValue(HHSR5Constants.PARENT_PATH));
					loDoc.setDocType(loProp.getStringValue(HHSR5Constants.DOC_TYPE));
					loDoc.setDocName(loProp.getStringValue(HHSR5Constants.DOCUMENT_TITLE));
					loDoc.setDocumentId(String.valueOf(loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN)));
				}
				else
				{
					loDoc.setFilePath(loProp.getStringValue(HHSR5Constants.FILENET_MOVE_FROM));
					loDoc.setDocType(HHSR5Constants.EMPTY_STRING);
					loDoc.setDocName(loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));
					loDoc.setFolderName(loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));
					loDoc.setDocumentId(String.valueOf(loProp.getIdValue(HHSR5Constants.FILENET_FOLDER_ID)));
					loDoc.setRBPath(String.valueOf(loProp.getStringValue(HHSR5Constants.PATH_NAME)));
				}
				if (!loDoc.getDocumentId().equalsIgnoreCase(aoFldrId))
				{
					loDocList.add(loDoc);
				}

			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in fetching deleted Children", aoEx);
			LOG_OBJECT.Error("Exception in fetching deleted Children", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.fetchDeletedChild()");
		return loDocList;
	}

	public List<com.nyc.hhs.model.Document> fetchChildListForRestore(ObjectStore aoObjStr, String aoFldrId,
			String aoType) throws ApplicationException
	{
		SearchSQL loSqlObject = new SearchSQL();
		String lsSQLQuery = null;
		List<com.nyc.hhs.model.Document> loDocList = new ArrayList<com.nyc.hhs.model.Document>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.fetchDeletedChild()");
			if (null != aoType && !aoType.isEmpty() && aoType.equalsIgnoreCase(HHSR5Constants.DOCUMENT))
			{
				lsSQLQuery = "select doc.PARENT_PATH,doc.DOC_TYPE,doc.DocumentTitle,doc.Id FROM HHS_ACCELERATOR doc WHERE this INSUBFOLDER '"
						+ aoFldrId + HHSR5Constants.STR;
			}
			else
			{
				lsSQLQuery = "select fo.PathName,fo.MOVE_FROM,fo.FolderName,fo.Id as FOLDER_ID FROM HHS_CUSTOM_FOLDER fo WHERE this INSUBFOLDER '"
						+ aoFldrId + HHSR5Constants.STR;
			}

			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loItr = loEngineSet.iterator();
			while (loItr.hasNext())
			{
				com.nyc.hhs.model.Document loDoc = new com.nyc.hhs.model.Document();
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();

				if (loProp.isPropertyPresent(HHSR5Constants.DOC_TYPE))
				{
					loDoc.setFilePath(loProp.getStringValue(HHSR5Constants.PARENT_PATH));
					loDoc.setDocType(loProp.getStringValue(HHSR5Constants.DOC_TYPE));
					loDoc.setDocName(loProp.getStringValue(HHSR5Constants.DOCUMENT_TITLE));
					loDoc.setDocumentId(String.valueOf(loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN)));
				}
				else
				{
					loDoc.setFilePath(loProp.getStringValue(HHSR5Constants.FILENET_MOVE_FROM));
					loDoc.setDocType(HHSR5Constants.EMPTY_STRING);
					loDoc.setDocName(loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));
					loDoc.setFolderName(loProp.getStringValue(HHSR5Constants.FILENET_FOLDER_NAME));
					loDoc.setDocumentId(String.valueOf(loProp.getIdValue(HHSR5Constants.FILENET_FOLDER_ID)));
					loDoc.setRBPath(String.valueOf(loProp.getStringValue(HHSR5Constants.PATH_NAME)));
				}
				if (!loDoc.getDocumentId().equalsIgnoreCase(aoFldrId))
				{
					loDocList.add(loDoc);
				}

			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in fetching deleted Children", aoEx);
			LOG_OBJECT.Error("Exception in fetching deleted Children", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.fetchDeletedChild()");
		return loDocList;
	}

	/**
	 * This method will delete folder hierarchy form RecycleBin recursively
	 * @param aoObjStr
	 * @param aoFldrPathName
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public void deleteFolderhierarchy(ObjectStore aoObjStr, String aoFldrPathName, UpdatingBatch loUpdateBatch,
			List<String> aoDeletedFolders) throws ApplicationException
	{
		SearchSQL loSqlObject = new SearchSQL();
		String lsSQLQuery = null;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.deleteFolderhierarchy()");

			lsSQLQuery = "select fo.PathName,fo.Id as FOLDER_ID FROM HHS_CUSTOM_FOLDER fo WHERE this INFOLDER '"
					+ aoFldrPathName + HHSR5Constants.STR;

			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchRows(loSqlObject, null, null, Boolean.FALSE);
			Iterator loItr = loEngineSet.iterator();
			while (loItr.hasNext())
			{
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();
				deleteFolderhierarchy(aoObjStr, loProp.getStringValue(HHSR5Constants.PATH_NAME), loUpdateBatch,
						aoDeletedFolders);
			}
			Folder loFldr = Factory.Folder.fetchInstance(aoObjStr, aoFldrPathName, null);
			if (!aoDeletedFolders.contains(aoFldrPathName))
			{
				loFldr.delete();
				aoDeletedFolders.add(aoFldrPathName);
				loUpdateBatch.add(loFldr, null);
			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"The Folder May be deleted please relogin and try again", aoEx);
			LOG_OBJECT.Error("The Folder May be deleted please relogin and try again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.deleteFolderhierarchy()");
	}

	/**
	 * This method will delete folder if it is moving form one place to another
	 * and on destination a folder present with same name.
	 * @param aoObjStr
	 * @param aoDeleteitems
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public static void deleteFolder(ObjectStore aoObjStr, List<String> aoDeleteitems) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.deleteFolder()");
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoObjStr.get_Domain(),
					RefreshMode.REFRESH);
			for (Iterator iterator = aoDeleteitems.iterator(); iterator.hasNext();)
			{
				String loDocId = (String) iterator.next();

				Folder loFldr = Factory.Folder.fetchInstance(aoObjStr, loDocId, null);
				loFldr.delete();
				loUpdateBatch.add(loFldr, null);

			}
			if (loUpdateBatch.hasPendingExecute())
			{
				loUpdateBatch.updateBatch();
			}

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"The Document May be deleted please relogin and try again", aoEx);
			LOG_OBJECT.Error("The Document May be deleted please relogin and try again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.deleteFolder()");
	}

	/**
	 * This Method will set the parent property of Entity for which we are going
	 * to remove sharing
	 * @param aoObjStore
	 * @param asPath
	 * @param aoReqMap
	 * @param loPropertyMap
	 * @param lsDocId
	 * @param lsDocType
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void setParentPropertyForUnsharing(ObjectStore aoObjStore, String asPath, String lsDocId)
			throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		LOG_OBJECT.Info("Entered P8ContentOperations.setParentPropertyForUnsharing()");
		try
		{
			String asFolderName = asPath.substring(asPath.lastIndexOf("/") + 1, asPath.length());

			if (null != asFolderName && !asFolderName.equals(HHSR5Constants.DOCUMENT_VAULT))
			{
				resetFolderLinkAndShareStatus(aoObjStore, asPath, HHSR5Constants.SHARING_FLAG,
						HHSR5Constants.ALL_SHARED, true);
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setParentProperty()::", aoAppex);
			throw aoAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"The Parent property of the shared entity may not be properly, please relogin and try again", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"The Parent property of the shared entity may not be properly, please relogin and try again", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setParentPropertyForUnsharing()");
	}

	/**
	 * The method will delete the entity forever from filenet
	 * Adding one extra param aoAction for Emergency Relase 4.0.1
	 * @param aoOS
	 * @param aoHashMapEntity containing entity_id and its type
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean deleteDocumentForever(ObjectStore aoOS, HashMap aoHashMapEntity,String aoAction) throws ApplicationException
	{

		boolean lbIsDeleted = true;
		Document loDocObject;
		Folder loFolderObject;
		VersionSeries loDocVersionSeries = null;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, aoHashMapEntity);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		List<String> loDeletedFodlerId = new ArrayList<String>();
		LOG_OBJECT.Info("Entered P8ContentOperations.deleteDocumentForever() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{
			UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(aoOS.get_Domain(),
					RefreshMode.REFRESH);
			if (null != aoHashMapEntity && !aoHashMapEntity.isEmpty())
			{
				Iterator loIter = aoHashMapEntity.keySet().iterator();
				while (loIter.hasNext())
				{
					String lsKey = (String) loIter.next();
					String lsDocType = (String) aoHashMapEntity.get(lsKey);

					if (null != lsDocType && !lsDocType.isEmpty()
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& !lsDocType.equalsIgnoreCase(HHSR5Constants.NULL))
					{
						loDocObject = Factory.Document.fetchInstance(aoOS, lsKey, null);
						loDocVersionSeries = loDocObject.get_VersionSeries();
						loDocVersionSeries.delete();
						loUpdateBatch.add(loDocVersionSeries, null);
					}
					else
					{
						loFolderObject = Factory.Folder.fetchInstance(aoOS, lsKey, null);
						deleteFolderhierarchy(aoOS, loFolderObject.get_PathName(), loUpdateBatch, loDeletedFodlerId);
						// Adding below code for Defect 8368(Emergency Release 4.0.1)
						if(null != aoAction && !aoAction.isEmpty() && aoAction.equalsIgnoreCase(HHSR5Constants.DELETE_DOCUMENT_FILENET))
						{
							List<com.nyc.hhs.model.Document> loDocList = new ArrayList<com.nyc.hhs.model.Document>();
							loDocList = fetchDeletedChild(aoOS, loFolderObject.get_Id().toString(), HHSR5Constants.DOCUMENT);
							if(null != loDocList && !loDocList.isEmpty())
							{
								for (Iterator iterator = loDocList.iterator(); iterator
										.hasNext();) {
									com.nyc.hhs.model.Document loDoc = (com.nyc.hhs.model.Document) iterator.next();
									Document loDocFilenet = Factory.Document.fetchInstance(aoOS, loDoc.getDocumentId(), null);
									loDocVersionSeries = loDocFilenet.get_VersionSeries();
									loDocVersionSeries.delete();
									loUpdateBatch.add(loDocVersionSeries, null);
								}
							}
						}
					}
				}
				if (loUpdateBatch.hasPendingExecute())
				{
					loUpdateBatch.updateBatch();
				}
			}
		}
		catch (EngineRuntimeException aoEx)
		{
			lbIsDeleted = false;
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.deleteDocumentForever()::", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			lbIsDeleted = false;
			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.deleteDocumentForever()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.deleteDocumentForever() ");
		return lbIsDeleted;
	}

	/**
	 * The method will give all the folders/document present in recycle bin
	 * @param aoOS
	 * @param asfolderPath
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getListOfEntityFromRecycleBin(ObjectStore aoOS, String asfolderPath) throws ApplicationException
	{

		SearchSQL loSqlObject = new SearchSQL();
		HashMap<String, String> loReqMap = new HashMap<String, String>();
		LOG_OBJECT.Info("Entered P8ContentOperations.getListOfEntityFromRecycleBin()");
		HashMap loHmExcepRequiredProp = new HashMap();
		try
		{
			String lsSQLQuery = "SELECT rcr.this,DOC.ID,FO.ID as FOLDER_ID,DOC.DOC_TYPE"
					+ " FROM "
					+ "(ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) "
					+ "full join HHSCustomFolder fo on rcr.head = object(fo.id)" + " WHERE rcr.head INSUBFOLDER " + "'"
					+ asfolderPath + "'";
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(aoOS);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loItr = loRowSet.iterator();
			while (loItr.hasNext())
			{
				RepositoryRowImpl loRowImpl = (RepositoryRowImpl) loItr.next();
				Properties loProp = loRowImpl.getProperties();
				if (null != loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN)
						&& !loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN).toString().isEmpty())
				{
					loReqMap.put(loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN).toString(),
							loProp.getStringValue(HHSR5Constants.DOC_TYPE));
				}
				else if (null != loProp.getIdValue(HHSR5Constants.FILENET_FOLDER_ID))
				{
					loReqMap.put(loProp.getIdValue(HHSR5Constants.FILENET_FOLDER_ID).toString(), null);
				}
			}

		}
		catch (EngineRuntimeException aoEx)
		{

			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getListOfEntityFromRecycleBin()::", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{

			String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M14");
			ApplicationException loAppex = new ApplicationException(lsMessage, aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getListOfEntityFromRecycleBin()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getListOfEntityFromRecycleBin() ");
		return loReqMap;

	}

	// Batch code
	/**
	 * The method will give the contents and other properties of document ids
	 * from filenet
	 * @param aoOS
	 * @param aoMyBatisSession as sql session
	 * @param aoDocIds as list of document Ids
	 * @return hashmap containing all the properties of document ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocumentListContent(ObjectStore aoOS, List<String> aoDocIds, HashMap<String, String> aoOrgMap)
			throws ApplicationException
	{

		HashMap<String, HashMap> loOutputMap = new HashMap<String, HashMap>();

		Document loDocObject;
		HashMap loHmExcepRequiredProp = new HashMap();
		loHmExcepRequiredProp.put(HHSR5Constants.DOC_ID, aoDocIds);
		loHmExcepRequiredProp.put("objectstore", aoOS.get_DisplayName());
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocumentListContent() with parameters::"
				+ loHmExcepRequiredProp.toString());
		try
		{

			Iterator loItr = aoDocIds.iterator();
			HashMap loOutputHashMap = new HashMap();
			loOutputHashMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(HHSR5Constants.CONTENT_ELEMENT, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(HHSR5Constants.DOCUMENT_TITLE, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(HHSR5Constants.PROVIDER_ID_DOCUMENT, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(HHSR5Constants.ORG_LEGAL_NAME_DOCUMENT, HHSR5Constants.EMPTY_STRING);
			loOutputHashMap.put(HHSR5Constants.CONTENT_SIZE, HHSR5Constants.EMPTY_STRING);
			PropertyFilter loPF = createPropertyFilter(loOutputHashMap);
			while (loItr.hasNext())
			{
				InputStream loContent = null;
				String loDocId = (String) loItr.next();
				HashMap loDocOutputHashMap = new HashMap();
				loDocObject = Factory.Document.fetchInstance(aoOS, loDocId, loPF);
				loContent = loDocObject.accessContentStream(0);
				Properties loDocumentProperty = loDocObject.getProperties();
				loDocOutputHashMap.put(HHSR5Constants.CONTENT_ELEMENT, loContent);
				loDocOutputHashMap.put(P8Constants.PROPERTY_CE_MIME_TYPE,
						loDocumentProperty.getObjectValue(P8Constants.PROPERTY_CE_MIME_TYPE));
				loDocOutputHashMap.put(P8Constants.PROPERTY_CE_FILE_TYPE,
						loDocumentProperty.getObjectValue(P8Constants.PROPERTY_CE_FILE_TYPE));
				loDocOutputHashMap.put(HHSR5Constants.DOCUMENT_TITLE,
						loDocumentProperty.getObjectValue(HHSR5Constants.DOCUMENT_TITLE));
				loDocOutputHashMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,
						loDocumentProperty.getObjectValue(P8Constants.PROPERTY_CE_ORGANIZATION_ID));
				loDocOutputHashMap.put(HHSR5Constants.PROVIDER_ID_DOCUMENT,
						loDocumentProperty.getObjectValue(HHSR5Constants.PROVIDER_ID_DOCUMENT));
				loDocOutputHashMap.put(HHSR5Constants.CONTENT_SIZE,
						loDocumentProperty.getObjectValue(HHSR5Constants.CONTENT_SIZE));
				String lsOrgName = (String) loDocumentProperty.getObjectValue(HHSR5Constants.PROVIDER_ID_DOCUMENT);
				if (aoOrgMap.containsKey(lsOrgName))
				{
					loDocOutputHashMap.put(HHSR5Constants.ORG_LEGAL_NAME_DOCUMENT, aoOrgMap.get(lsOrgName));
				}
				loOutputMap.put(loDocId, loDocOutputHashMap);
			}

		}
		catch (ApplicationException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoErEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting document content", aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Error While getting document content", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocumentListContent()");
		return loOutputMap;
	}

	/**
	 * The method is used for getting the list of document ids.
	 * @param aoOS
	 * @param aoDownloadList as list of document to download as per search query
	 * @return list of document ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List getDocIdforDownload(ObjectStore aoOS, BulkDownloadBean aoDownloadBean) throws ApplicationException
	{

		SearchSQL loSqlObject = new SearchSQL();
		List loReqList = new ArrayList();
		LOG_OBJECT.Info("Entered P8ContentOperations.getDocIdforDownload() ");
		HashMap loHmExcepRequiredProp = new HashMap();
		StringBuffer loQuery = new StringBuffer();
		try
		{
			if (null != aoDownloadBean)
			{
				createSqlQuery(aoDownloadBean, loQuery);
				if (null != loQuery)
				{
					loSqlObject.setQueryString(loQuery.toString());
					SearchScope loSearchScope = new SearchScope(aoOS);
					RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
					Iterator loItr = loRowSet.iterator();
					while (loItr.hasNext())
					{
						RepositoryRowImpl loRowImpl = (RepositoryRowImpl) loItr.next();
						Properties loProp = loRowImpl.getProperties();
						if (!loReqList.contains(loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN).toString()))
						{
							loReqList.add(loProp.getIdValue(HHSR5Constants.TEMPLATE_IDEN).toString());
						}

					}
				}
			}
		}
		catch (ApplicationException aoEx)
		{

			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocIdforDownload()::", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocIdforDownload()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocIdforDownload() ");
		return loReqList;

	}

	/**
	 * This method will create Sql query for downloading pending request data
	 * for filenet used in Download All functionality from Accelerator User
	 * @param aoBean bean populated form DB table
	 * @param aoSqlQuery
	 * @throws ApplicationException
	 */
	public static void createSqlQuery(BulkDownloadBean aoBean, StringBuffer aoSqlQuery) throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.createSqlQuery() " + aoBean);
			if (null != aoBean.getMsAgentId() && !aoBean.getMsAgentId().isEmpty())
			{
				createSqlQueryForSharedAgency(aoBean, aoSqlQuery);

			}
			else
			{
				createSqlQueryForDocTypeSearch(aoBean, aoSqlQuery);

			}
			LOG_OBJECT.Info("Query:::::::::::" + aoSqlQuery);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error while creating Sql Query in createSqlQuery",
					aoEx);
			LOG_OBJECT.Error("Error while creating Sql Query in createSqlQuery", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.createSqlQuery() ");
	}

	/**
	 * This method will create Sql Query in Download All functionality if there
	 * is no value in Shared With criteria.
	 * @param aoBean
	 * @param aoSqlQuery
	 * @throws ApplicationException
	 */
	private static void createSqlQueryForDocTypeSearch(BulkDownloadBean aoBean, StringBuffer aoSqlQuery)
			throws ApplicationException
	{
		try
		{
			aoSqlQuery
					.append("SELECT DISTINCT DOC.PROVIDER_ID AS HHS_SHARED_BY,DOC.HHS_LAST_MODIFIED_DATE,DOC.DocumentTitle,DOC.Id "
							+ "from hhs_accelerator doc  WHERE DOC.DELETE_FLAG  = 0 AND ");
			if (null != aoBean.getMsModifiedFrom() && !aoBean.getMsModifiedFrom().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_FROM, aoBean.getMsModifiedFrom().toString());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_FROM,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsModifiedTo() && !aoBean.getMsModifiedTo().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_TO, aoBean.getMsModifiedTo().toString());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_TO,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsFilterDocName() && !aoBean.getMsFilterDocName().isEmpty())
			{
				aoSqlQuery.append("DOC.DocumentTitle like '%" + aoBean.getMsFilterDocName() + "%' AND ");
			}

			aoSqlQuery.append(" ORGANIZATION_ID IS NOT NULL AND DOC.DOC_TYPE = '");
			aoSqlQuery.append(aoBean.getMsFilterDocType() + HHSR5Constants.STR);
			if (null != aoBean.getOrgId() && !aoBean.getOrgId().isEmpty())
			{
				aoSqlQuery.append(" AND PROVIDER_ID ='" + aoBean.getOrgId() + HHSR5Constants.STR);
			}
		}
		catch (ApplicationException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While creating SQL Query", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This Method will create Sql Query for Download All functionality if
	 * search criteria contains shared With values
	 * @param aoBean
	 * @param aoSqlQuery
	 * @throws ApplicationException
	 */
	private static void createSqlQueryForSharedAgency(BulkDownloadBean aoBean, StringBuffer aoSqlQuery)
			throws ApplicationException
	{
		String lsWhereClause = null;
		try
		{

			aoSqlQuery
					.append("SELECT DISTINCT DOC.PROVIDER_ID AS HHS_SHARED_BY,DOC.HHS_LAST_MODIFIED_DATE,DOC.DocumentTitle,DOC.Id "
							+ "from hhs_accelerator doc left join HHSSharedDocument shr  on doc.SHARED_ENTITY_ID = shr.SHARED_DOCUMENT_ID  "
							+ "WHERE DOC.DELETE_FLAG  = 0 AND ");

			if (null != aoBean.getMsModifiedFrom() && !aoBean.getMsModifiedFrom().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_FROM, aoBean.getMsModifiedFrom());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_FROM,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsModifiedTo() && !aoBean.getMsModifiedTo().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_TO, aoBean.getMsModifiedTo().toString());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_TO,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsFilterDocName() && !aoBean.getMsFilterDocName().isEmpty())
			{
				aoSqlQuery.append("DOC.DocumentTitle like '%" + aoBean.getMsFilterDocName() + "%' AND ");
			}
			String[] loagencyObject = aoBean.getMsAgentId().split(HHSR5Constants.COMMA);
			List<String> loAgencyList = new ArrayList<String>();
			loAgencyList = Arrays.asList(loagencyObject);
			lsWhereClause = StringUtils.join(loAgencyList, StringEscapeUtils.escapeHtml("','"));

			aoSqlQuery.append("SHR.HHS_AGENCY_ID IN('");
			aoSqlQuery.append(lsWhereClause);
			aoSqlQuery.append("') AND");
			aoSqlQuery.append(" ORGANIZATION_ID IS NOT NULL AND DOC.DOC_TYPE = '");
			aoSqlQuery.append(aoBean.getMsFilterDocType() + HHSR5Constants.STR);
			if (null != aoBean.getOrgId() && !aoBean.getOrgId().isEmpty())
			{
				aoSqlQuery.append(" AND PROVIDER_ID ='" + aoBean.getOrgId() + HHSR5Constants.STR);
			}

		}
		catch (ApplicationException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While creating SQL Query in createSqlQueryForSharedAgency", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForSharedAgency", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While creating SQL Query", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForSharedAgency", aoEx);
			throw loAppex;
		}
	}

	/**
	 * The method will check the folder path exists or not. If the folder is
	 * deleted from filenet, it will throw ApplicationException.
	 * @param aoOS
	 * @param aoFilterMap
	 * @return aoFilterMap
	 * @throws ApplicationException
	 */
	public void checkFolderPath(ObjectStore aoOS, String asFolderId, String asEntityType, String asJsp)
			throws ApplicationException
	{
		String lsSQLQuery = "";
		SearchSQL loSqlObject = new SearchSQL();

		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.checkFolderPath() ");
			if (null != asFolderId && !asFolderId.isEmpty()
					&& !asFolderId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
					&& !asFolderId.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID)
					&& !asFolderId.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID))
			{
				if (null == asEntityType || asEntityType.isEmpty()
						|| asEntityType.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
				{
					if (null == asJsp || !asJsp.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
					{
						lsSQLQuery = "SELECT Id from HHS_CUSTOM_FOLDER where Id='" + asFolderId
								+ "' and DELETE_FLAG  = 0";
					}
					else if (null != asJsp && asJsp.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
					{
						lsSQLQuery = "SELECT Id from HHS_CUSTOM_FOLDER where Id='" + asFolderId + "'";
					}
				}

				else if (null != asEntityType && !asEntityType.isEmpty())
				{
					if (null == asJsp || !asJsp.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
					{
						lsSQLQuery = "SELECT Id from HHS_ACCELERATOR where Id='" + asFolderId
								+ "' and DELETE_FLAG  = 0";
					}
					else if (null != asJsp && asJsp.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_ID))
					{
						lsSQLQuery = "SELECT Id from HHS_ACCELERATOR where Id='" + asFolderId + "'";
					}
				}
				loSqlObject.setQueryString(lsSQLQuery);
				SearchScope loSearchScope = new SearchScope(aoOS);
				IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
				Iterator loItr = loRowSet.iterator();
				if (!loItr.hasNext())
				{
					throw new ApplicationException("Internal Error Occured While Processing Your Request");
				}
			}
		}

		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("The selected folder has been deleted", aoEx);
			LOG_OBJECT.Error("The selected folder has been deleted", aoEx);
			throw loAppex;
		}
	}

	// Added for filenet batch
	/**
	 * This method will create DV and RB folder
	 * 
	 * @param aoObjStr a custom user bean having information about user
	 * @param abFlag Boolean
	 * @param asPath String
	 * @param aoName String
	 * @return tempList List of FolderMappingBean
	 * @throws ApplicationException if there is any exception
	 */
	public HashMap<String,Object> createDVandRBfolder(ObjectStore aoObjStr, Boolean abFlag, String asPath,
			String aoName) throws ApplicationException
	{
		List<FolderMappingBean> tempList = new ArrayList<FolderMappingBean>();
		HashMap<String,Object> loReturnMap = new HashMap<String, Object>();
		HashMap<String,Object> loDbMapFinal = new HashMap<String, Object>();
		List<String> loFolderName = new ArrayList<String>();
		LOG_OBJECT.Info("Entered P8ContentOperations.createDVandRBfolder() ");
		List<String> loType = new ArrayList<String>();
		List<String> loPath = new ArrayList<String>();
		List<String> loName = new ArrayList<String>();
		PropertyFilter loPF = new PropertyFilter();
		try
		{
			LOG_OBJECT.Error("In Service file::" + abFlag + ":::" + asPath + "::::" + aoName);
			loFolderName.add(HHSConstants.DOCUMENT_VAULT);
			loFolderName.add("Recycle Bin");
			if (abFlag)
			{
				loType.add("/" + HHSConstants.PROVIDER);
				loType.add("/" + HHSConstants.AGENCY);
				createList(aoObjStr, loType, loName, loPath);
				loName.add("city_org");
				loPath.add("/City");
			}
			else
			{
				loPath.add(asPath);
				loName.add(aoName);
			}

			for (int j = 0; j < loPath.size(); j++)
			{
				List<String> aoDocIdList = new ArrayList<String>();
				List<String> aoSharingDocIdList = new ArrayList<String>();
				for (int i = 0; i < loFolderName.size(); i++)
				{
					Folder loFldr = createFolderInstance(aoObjStr, loFolderName, loPath, loName, loPF, j, i, tempList,aoDocIdList,aoSharingDocIdList);
					if (loFolderName.get(i).equals(HHSConstants.DOCUMENT_VAULT) && null != loFldr)
					{
						HashMap<String,Object> loDbMap = new HashMap<String, Object>();
						aoDocIdList.add(loFldr.get_Id().toString());
						loDbMap.put("DocIdList", aoDocIdList);
						loDbMap.put("parentFolderFilenetId", loFldr.get_Id().toString());
						loDbMap.put("entityName", "r5initialdocumentfilling");
						loDbMap.put("newVal", loName.get(j));
						loDbMap.put("SharingDocIdList", aoSharingDocIdList);
						loDbMapFinal.put(loName.get(j), loDbMap);
					}
				}
				
			}

		}
		catch (ApplicationException apAppEx)
		{
			throw apAppEx;
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Internal Error Occured While Processing Your Request", aoEx);
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.createDVandRBfolder() ");
		loReturnMap.put("folderList", tempList);
		loReturnMap.put("DbMap", loDbMapFinal);
		return loReturnMap;
	}

	/**
	 * This method will create List
	 * 
	 * @param aoObjStr a custom user bean having information about user
	 * @param loType List of String
	 * @param loName List of String
	 * @param loPath List of String
	 * @throws ApplicationException if there is any exception
	 */
	private void createList(ObjectStore aoObjStr, List<String> loType, List<String> loName, List<String> loPath)
			throws ApplicationException
	{
		PropertyFilter loPF = new PropertyFilter();
		loPF.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.SUB_FOLDERS, null));
		try
		{
			for (int z = 0; z < loType.size(); z++)
			{

				Folder lofolder = Factory.Folder.fetchInstance(aoObjStr, loType.get(z), loPF);
				FolderSet loFolderSet = lofolder.get_SubFolders();
				if (null != loFolderSet && !loFolderSet.isEmpty())
				{
					Iterator loFolderIterator = loFolderSet.iterator();
					while (loFolderIterator.hasNext())
					{
						Folder loFolder = (Folder) loFolderIterator.next();
						loName.add(loFolder.get_Name());
						loPath.add(loFolder.get_PathName());
					}
				}

			}
		}
		catch (Exception e)
		{
			throw new ApplicationException("Internal Error Occured While Processing Your Request", e);
		}
	}

	/**
	 * This method will create Folder Instance
	 * 
	 * @param aoObjStr a custom user bean having information about user
	 * @param loFolderName List of String
	 * @param loName List of String
	 * @param loPath List of String
	 * @param loPF PropertyFilter
	 * @param aiPathCount int
	 * @param aiFolderCount int
	 * @param tempList List of FolderMappingBean
	 * @throws ApplicationException if there is any exception
	 */
	private Folder createFolderInstance(ObjectStore aoObjStr, List<String> loFolderName, List<String> loPath,
			List<String> loName, PropertyFilter loPF, int aiPathCount, int aiFolderCount,
			List<FolderMappingBean> tempList,List<String> aoDocIdList,List<String> aoSharingDocIdList) throws ApplicationException
	{
		Folder loFldr = null;
		Folder loParent;
		try
		{
			String lsFolderPath = loPath.get(aiPathCount) + "/" + loFolderName.get(aiFolderCount);
			if (!checkFolderExists(aoObjStr, lsFolderPath))
			{
				loFldr = Factory.Folder.createInstance(aoObjStr, HHSR5Constants.HHS_CUSTOM_FOLDER);
				loParent = getFolderByNameForBatch(aoObjStr, loPath.get(aiPathCount), loFolderName.get(aiFolderCount));
				loFldr.set_Parent(loParent);
				loFldr.set_FolderName(loFolderName.get(aiFolderCount));
				loFldr.save(RefreshMode.REFRESH);
				if (loFolderName.get(aiFolderCount).equals(HHSConstants.DOCUMENT_VAULT))
				{
					fileDocToDocumentVault(aoObjStr, loFldr, loParent, loPath, loName, loPF, aiPathCount, tempList,aoDocIdList,aoSharingDocIdList);
				}
			}
		}
		catch (ApplicationException apAppEx)
		{
			throw apAppEx;
		}
		catch (Exception e)
		{
			throw new ApplicationException("Internal Error Occured While Processing Your Request", e);
		}
		return loFldr;
	}

	/**
	 * This Method is used for fetching folder from the FileNet Repository. If
	 * file Doc To Document Vault
	 * 
	 * @param aoObjStr active filenet object store session
	 * @param loFldr Folder
	 * @param loParent Folder
	 * @param loPath List of strings
	 * @param loName List of strings
	 * @param loPF PropertyFilter
	 * @param aiFolderCount int
	 * @param tempList List FolderMappingBean
	 * @return Folder is the P8 folder object
	 * @throws ApplicationException
	 */
	private void fileDocToDocumentVault(ObjectStore aoObjStr, Folder loFldr, Folder loParent, List<String> loPath,
			List<String> loName, PropertyFilter loPF, int aiFolderCount, List<FolderMappingBean> tempList,
			List<String> aoDocIdList,List<String> aoSharingDocIdList)
			throws ApplicationException
	{
		try
		{
			ReferentialContainmentRelationship loRCR;
			int count = 0;
			int batchCount = 0;
			boolean lbelseCheck = false;
			String lspath = loParent.get_PathName();
			SearchSQL loSqlObject = new SearchSQL();
			String lsOrgType = null;

			String lsSQLQuery = "select this,ORGANIZATION_ID,id,IS_SHARED,SHARING_FLAG,PARENT_PATH,IS_ARCHIVE,DOC_TYPE,DOC_CATEGORY,DELETE_FLAG from HHS_ACCELERATOR where PROVIDER_ID= '"
					+ loName.get(aiFolderCount) + "'";
			LOG_OBJECT.Debug("QUERY TO FETCH DOCUMENT::" + lsSQLQuery);
			loSqlObject.setQueryString(lsSQLQuery);
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			if (null != loEngineSet && !loEngineSet.isEmpty())
			{
				Iterator loDocumentIterator = loEngineSet.iterator();

				while (loDocumentIterator.hasNext())
				{
					count++;
					Document loDoc = (Document) loDocumentIterator.next();
					boolean flag = fileinOldDocs(loFldr, loPath, loName, aiFolderCount, loDoc,aoSharingDocIdList);
					if (flag)
					{
						LOG_OBJECT.Debug("Migrate the mentioned Document Id in Document Vault successful::::::"
								+ loDoc.get_Id().toString() + "for Organization:::" + loName.get(aiFolderCount));
					}
					else
					{
						LOG_OBJECT.Debug("Unable to Migrate the mentioned Document Id in Document Vault::::::"
								+ loDoc.get_Id().toString() + "for Organization:::" + loName.get(aiFolderCount));
					}
					lsOrgType = loDoc.getProperties().getStringValue("ORGANIZATION_ID");
					aoDocIdList.add(loDoc.get_Id().toString());
				}
				//loDbMap.put("DocIdList", aoDocIdList);
				//loDbMap.put("parentFolderFilenetId", loFldr.get_Id().toString());
				//loDbMap.put("entityName", "r5initialdocumentfilling");
				//loDbMap.put("newVal", loName.get(aiFolderCount));
				//loDbMap.put("SharingDocIdList", aoSharingDocIdList);
				
			}

			LOG_OBJECT.Debug("Count of migrated documents::::::"
					+ count + "for Organization:::" + loName.get(aiFolderCount));
			
			String path = loPath.get(aiFolderCount) + "/" + HHSConstants.DOCUMENT_VAULT;
			if(null != path && path.startsWith("/City"))
			{
				lsOrgType = "city_org";
			}
			else if(null != path && path.startsWith("/Agency"))
			{
				lsOrgType = "agency_org";
			}
			else if(null != path && path.startsWith("/Provider"))
			{
				lsOrgType = "provider_org";
			}
			
			loPF.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.ID, null));
			Folder lofolder = Factory.Folder.fetchInstance(aoObjStr, path, loPF);
			String id = lofolder.get_Id().toString();
			FolderMappingBean lodocument = new FolderMappingBean();
			lodocument.setOrganizationId(loName.get(aiFolderCount));
			lodocument.setFolderFilenetId(id);
			lodocument.setDocumentCount(count);
			lodocument.setOrganizationType(lsOrgType);
			tempList.add(lodocument);

		}
		catch (ApplicationException apAppEx)
		{
			throw apAppEx;
		}
		catch (Exception e)
		{
			throw new ApplicationException("Internal Error Occured While Processing Your Request", e);
		}
		LOG_OBJECT.Debug("NO OF DOCUMENT FILED IN FOR ORG::" + loName.get(aiFolderCount) + tempList.size());
	}

	/**
	 * @param loUpdateBatch
	 * @param loFldr
	 * @param loPath
	 * @param loName
	 * @param aiPathCount
	 * @param count
	 * @param loDocumentIterator
	 * @return boolean flag
	 * @throws ApplicationException
	 */
	private boolean fileinOldDocs(Folder loFldr, List<String> loPath, List<String> loName, int aiPathCount,
			Document aoDocument,List<String> aoSharingIdList) throws ApplicationException
	{
		ReferentialContainmentRelationship loRCR;
		boolean flag = true;
		try
		{
			Properties loProps = aoDocument.getProperties();
			setProperty(loProps, TypeID.LONG, "DELETE_FLAG", "0");

			if (loProps.getBooleanValue("IS_SHARED"))
			{
				setProperty(loProps, TypeID.STRING, "SHARING_FLAG", "2");
				loProps.putValue(HHSR5Constants.SHARED_ENTITY_ID, aoDocument.get_Id());
				aoSharingIdList.add(aoDocument.get_Id().toString());
			}
			else
			{
				setProperty(loProps, TypeID.STRING, "SHARING_FLAG", "0");
			}

			setProperty(loProps, TypeID.BOOLEAN, "IS_ARCHIVE", "false");
			if (loName.get(aiPathCount).equalsIgnoreCase("city_org"))
			{
				setProperty(loProps, TypeID.STRING, "PARENT_PATH", "/City/Document Vault");
			}
			else
			{
				setProperty(loProps, TypeID.STRING, "PARENT_PATH", loPath.get(aiPathCount) + "/Document Vault");
			}
			aoDocument.save(RefreshMode.REFRESH);
			loRCR = loFldr.file(aoDocument, AutoUniqueName.AUTO_UNIQUE, aoDocument.get_Id().toString(),
					DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
			loRCR.save(RefreshMode.REFRESH);

		}
		catch (ApplicationException apAppEx)
		{
			flag = false;

		}
		catch (Exception e)
		{
			flag = false;

		}
		return flag;
	}

	/**
	 * This Method is used for fetching folder from the FileNet Repository. If
	 * Folder does not exist, it will create the folder.
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param asPath the path of the folder where the file is to be stored
	 * @return Folder is the P8 folder object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Folder getFolderByNameForBatch(ObjectStore aoObjectStore, String asPath, String asDocType)
			throws ApplicationException
	{
		Folder loFldr = null;

		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put(HHSR5Constants.AS_PATH, asPath);
		LOG_OBJECT
				.Info("Entered P8ContentOperations.getFolderByName() with parameters::" + loHmRequiredProp.toString());

		try
		{
			if (asPath.equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in getFolderByName Method.Required Parameters are missing.");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}

			// checking whether folder exists or not. if no then creating new
			// folder instance
			if (checkFolderExists(aoObjectStore, asPath))
			{

				// if folder path exist, then returning the existing folder
				loFldr = Factory.Folder.fetchInstance(aoObjectStore, asPath, null);
			}
			else
			{
				// if folder path doesn't exist then creating new folder. sample
				// creating folder path array to check whether all parents are
				// exist or not.
				String[] loSubFolders = asPath.split(P8Constants.STRING_SINGLE_SLASH);

				int liLength = 1;
				String lsParentFolderPath = "";
				String lsChildFolderPath = "";

				// doing looping to check whether all parents folder are exist
				// or not.
				while (liLength < loSubFolders.length)
				{
					lsChildFolderPath = lsParentFolderPath.concat(P8Constants.STRING_SINGLE_SLASH).concat(
							loSubFolders[liLength]);

					// checking child folder exist or not. if no then creating
					// new folder
					if (!checkFolderExists(aoObjectStore, lsChildFolderPath))
					{
						Folder loParent;
						if (lsParentFolderPath.equalsIgnoreCase(""))
						{
							loParent = aoObjectStore.get_RootFolder();
						}
						else
						{
							loParent = Factory.Folder.getInstance(aoObjectStore, ClassNames.FOLDER, lsParentFolderPath);
						}
						if (null != asDocType && asDocType.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT))
						{
							loFldr = Factory.Folder.createInstance(aoObjectStore, HHSR5Constants.HHS_CUSTOM_FOLDER);
						}
						else
						{
							loFldr = Factory.Folder.createInstance(aoObjectStore, ClassNames.FOLDER);
						}

						loFldr.set_Parent(loParent);
						loFldr.set_FolderName(loSubFolders[liLength]);
						// POC starts
						Properties loFolderProps = loFldr.getProperties();
						loFolderProps.putValue("FOLDER_TITLE", asDocType);
						// POC End

						loFldr.save(RefreshMode.NO_REFRESH);
					}
					// setting parent folder path value to child folder path
					// value so that check all levels of folder structure
					lsParentFolderPath = lsChildFolderPath;
					liLength++;
				}
			}

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getFolderByName()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in getFolderByName Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("FileNet Runtime Exception in getFolderByName Method", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception while in getFolderByName Method", aoEx);
			loAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception while in getFolderByName Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getFolderByName()");
		return loFldr;
	}

	/**
	 * This method is added as a part of release 5, which will be used to update
	 * Folder Linkage Status.
	 * 
	 * @param aoObjectStore ObjectStore
	 * @param abIsLinkToApp boolean
	 * @param asParentPath String
	 * @throws ApplicationException if there is any exception
	 */
	private void updateFolderLinkStatus(ObjectStore aoObjectStore, boolean abIsLinkToApp, String asParentPath)
			throws ApplicationException
	{
		Folder loFolder = null;
		Properties loProps = null;
		String lsParentPathNext = null;
		try
		{
			if (null != asParentPath && !asParentPath.endsWith(HHSR5Constants.DOCUMENT_VAULT))
			{
				loFolder = Factory.Folder.fetchInstance(aoObjectStore, asParentPath, null);
				loProps = loFolder.getProperties();
				loProps = setProperty(loFolder.getProperties(), TypeID.BOOLEAN,
						P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, String.valueOf(abIsLinkToApp));
				loFolder.save(RefreshMode.NO_REFRESH);
				lsParentPathNext = asParentPath.substring(0, asParentPath.lastIndexOf(P8Constants.STRING_SINGLE_SLASH));
				if (null != lsParentPathNext && !lsParentPathNext.endsWith(HHSR5Constants.DOCUMENT_VAULT))
				{
					updateFolderLinkStatus(aoObjectStore, abIsLinkToApp, lsParentPathNext);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.updateFolderLinkStatus()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			LOG_OBJECT.Error("FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception while in updateFolderLinkStatus Method",
					aoEx);
			LOG_OBJECT.Error("Exception while in updateFolderLinkStatus Method", aoEx);
			throw loAppex;
		}

	}

	/**
	 * This method is added as a part of release 5, which will be used to reset
	 * Folder Link And Share Status.
	 * 
	 * @param aoObjectStore ObjectStore
	 * @param asParentPath String
	 * @param asPropertyName String
	 * @param asPropertyValue String
	 * @throws ApplicationException if there is any exception
	 */
	public void resetFolderLinkAndShareStatus(ObjectStore aoObjectStore, String asParentPath, String asPropertyName,
			String asPropertyValue, boolean abLinkage) throws ApplicationException
	{
		String lsParentPath = null;
		int liCount = 0;
		StringBuffer loQueryBuffer = new StringBuffer();
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.resetFolderLinkAndShareStatus() ");
			if (abLinkage)
			{
				lsParentPath = fetchParent(aoObjectStore, asParentPath);
				if (asPropertyName != null && asPropertyName.equalsIgnoreCase(HHSR5Constants.SHARING_FLAG))
				{
					loQueryBuffer = loQueryBuffer
							.append("SELECT rcr.this FROM (ReferentialContainmentRelationship rcr left join hhs_accelerator doc on rcr.Head = object(doc.id)) "
									+ "full join HHSCustomFolder fo on rcr.head = object(fo.id) "
									+ "WHERE rcr.head INSUBFOLDER '");
					loQueryBuffer.append(lsParentPath).append("' and ");
					if (asPropertyValue.equalsIgnoreCase(HHSR5Constants.ALL_SHARED))
					{
						loQueryBuffer.append("(DOC.SHARED_ENTITY_ID = DOC.ID OR FO.SHARED_ENTITY_ID = FO.ID)");
					}
					else
					{
						loQueryBuffer.append(" (DOC." + asPropertyName);
						loQueryBuffer.append("= '").append(asPropertyValue).append(HHSR5Constants.STR);
						loQueryBuffer.append(" OR FO." + asPropertyName);
						loQueryBuffer.append("= '").append(asPropertyValue).append(HHSR5Constants.STR + ")");
					}

				}
				else
				{

					loQueryBuffer = loQueryBuffer.append("select id from hhs_accelerator where this INSUBFOLDER'")
							.append(asParentPath).append(HHSR5Constants.STR).append(" and ").append(asPropertyName)
							.append("= ").append(true);
				}
				loSqlObject.setQueryString(loQueryBuffer.toString());
				SearchScope loSearchScope = new SearchScope(aoObjectStore);
				IndependentObjectSet loRowSet = loSearchScope.fetchObjects(loSqlObject, new Integer(1), null,
						Boolean.TRUE);
				Iterator loItr = loRowSet.iterator();
				while (loItr.hasNext())
				{
					liCount++;
					break;
				}
				if (asPropertyName != null && asPropertyName.equalsIgnoreCase(HHSR5Constants.SHARING_FLAG))
				{
					if (liCount == 0)
					{
						UpdatingBatch loUpdateBatch = UpdatingBatch.createUpdatingBatchInstance(
								aoObjectStore.get_Domain(), RefreshMode.REFRESH);
						updateFolderSharedStatus(aoObjectStore, HHSR5Constants.ZERO, lsParentPath, loUpdateBatch);
						if (loUpdateBatch.hasPendingExecute())
						{
							loUpdateBatch.updateBatch();
						}
					}
				}
				else
				{
					if ((liCount == 0 || Boolean.valueOf(asPropertyValue)))
					{
						updateFolderLinkStatus(aoObjectStore, Boolean.valueOf(asPropertyValue), lsParentPath);
					}
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.updateFolderLinkStatus()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			LOG_OBJECT.Error("FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception while in updateFolderLinkStatus Method",
					aoEx);
			LOG_OBJECT.Error("Exception while in updateFolderLinkStatus Method", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.resetFolderLinkAndShareStatus() ");
	}

	/**
	 * This method is added as a part of release 5, which will be used to fetch
	 * Parent path.
	 * 
	 * @param aoObjectStore a custom user bean having information about user
	 * @param asParentPath String
	 * @return lsParentPath String
	 * @throws ApplicationException if there is any exception
	 */
	private String fetchParent(ObjectStore aoObjectStore, String asParentPath) throws ApplicationException
	{

		String lsParentPath;
		try
		{

			if (null != asParentPath && asParentPath.contains("/"))
			{
				lsParentPath = asParentPath;
			}
			else
			{
				Folder loFldr = Factory.Folder.fetchInstance(aoObjectStore, asParentPath, null);
				lsParentPath = loFldr.get_PathName();
			}

		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in P8ContentOperations.fetchParent:",
					aoErEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fetchParent:", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception in P8ContentOperations.fetchParent:",
					aoEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.fetchParent:", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.fetchParent:");
		return lsParentPath;
	}

	// R5 Starts
	/**
	 * This method is added as a part of release 5, which will be used to set
	 * Dictionary Content.
	 * 
	 * @param aoUser String
	 * @param aoOS ObjectStore
	 * @param aoDocTypeXML Object
	 * @param asDocType String
	 * @param aoIS FileInputStream
	 * @return lbDataSet boolean
	 * @throws ApplicationException if there is any exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean setDictionaryContent(String aoUser, ObjectStore aoOS, Object aoDocTypeXML, String asDocType,
			FileInputStream aoIS) throws ApplicationException
	{
		String lsDocFolderPath = null;
		Folder loFolderObj = null;
		DocumentSet loDocSet = null;
		Iterator loDocIter = null;
		boolean lbDataSet = false;
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		String lsDocId = null;

		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setDictionaryContent() ");
			lsDocFolderPath = getFolderPath(aoDocTypeXML, null, asDocType, null, P8Constants.APPLICATION_CITY_ORG);
			loFolderObj = Factory.Folder.fetchInstance(aoOS, lsDocFolderPath, null);
			if (null != loFolderObj)
			{
				loDocSet = loFolderObj.get_ContainedDocuments();
				loDocIter = loDocSet.iterator();
				if (loDocIter.hasNext())
				{
					Document loNewDoc = (Document) loDocIter.next();
					deleteDocument(aoOS, loNewDoc.get_Id().toString());
					HashMap aoPropertyMap = new HashMap();
					aoPropertyMap.put(P8Constants.DOCUMENT_ID, loNewDoc.get_Id().toString());
					aoPropertyMap.put(P8Constants.MIME_TYPE, HHSR5Constants.TEXT_MIME);
					aoPropertyMap.put(ApplicationConstants.FILE_NAME, HHSR5Constants.EN_US);
					aoPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.EN_US);
					aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, aoUser);
					aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, aoUser);
					loReturnMap = createDocument(aoOS, aoIS, P8Constants.PROPERTY_CE_ROOT_DOCUMENT_CLASS_NAME,
							asDocType, aoPropertyMap, lsDocFolderPath);
					lsDocId = (String) loReturnMap.get(HHSR5Constants.DOC_ID);
					LOG_OBJECT.Info("Document Id :" + lsDocId);
					lbDataSet = true;
				}
			}
			else
			{
				ApplicationException loAppex = new ApplicationException(
						"Exception in P8ContentOperations.setDictionaryContent:");
				throw loAppex;
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDictionaryContent:", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoErEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Exception in P8ContentOperations.setDictionaryContent:", aoErEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDictionaryContent:", aoErEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Exception in P8ContentOperations.setDictionaryContent:", aoEx);
			LOG_OBJECT.Error("Exception in P8ContentOperations.setDictionaryContent:", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setDictionaryContent() ");
		return lbDataSet;
	}

	/**
	 * This method is added as a part of release 5, which will be used to update
	 * Shared Folder Status.
	 * 
	 * @param aoObjectStore ObjectStore
	 * @param lsSharedFlag String
	 * @param asParentPath String
	 * @param loUpdateBatch UpdatingBatch
	 * @throws ApplicationException if there is any exception
	 */
	private void updateFolderSharedStatus(ObjectStore aoObjectStore, String lsSharedFlag, String asParentPath,
			UpdatingBatch loUpdateBatch) throws ApplicationException
	{
		Folder loFolder = null;
		Properties loProps = null;
		String lsParentPathNext = null;

		try
		{
			LOG_OBJECT.Info("Enter P8ContentOperations.updateFolderSharedStatus()");
			if (null != asParentPath && !asParentPath.endsWith(HHSR5Constants.DOCUMENT_VAULT))
			{
				loFolder = Factory.Folder.fetchInstance(aoObjectStore, asParentPath, null);
				loProps = loFolder.getProperties();
				String lsSharingFlag = loProps.getStringValue(HHSR5Constants.SHARING_FLAG);
				if (!lsSharingFlag.equalsIgnoreCase("2") && !lsSharingFlag.equals(lsSharedFlag))
				{
					loProps = setProperty(loProps, TypeID.STRING, HHSR5Constants.SHARING_FLAG, lsSharedFlag);
					loProps = setProperty(loProps, TypeID.OBJECT, HHSR5Constants.SHARED_ENTITY_ID, null);
					loUpdateBatch.add(loFolder, null);
					lsParentPathNext = asParentPath.substring(0,
							asParentPath.lastIndexOf(P8Constants.STRING_SINGLE_SLASH));
					if (null != lsParentPathNext && !lsParentPathNext.endsWith(HHSR5Constants.DOCUMENT_VAULT))
					{
						updateFolderSharedStatus(aoObjectStore, lsSharedFlag, lsParentPathNext, loUpdateBatch);
					}
				}
				LOG_OBJECT.Info("Exit P8ContentOperations.updateFolderSharedStatus()");
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.updateFolderLinkStatus()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			LOG_OBJECT.Error("FileNet Runtime Exception in updateFolderLinkStatus Method", aoEx);
			throw loAppex;

		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Exception while in updateFolderLinkStatus Method",
					aoEx);
			LOG_OBJECT.Error("Exception while in updateFolderLinkStatus Method", aoEx);
			throw loAppex;
		}

	}

	/**
	 * This Method is used for fetching list of Provider/Agency name for a
	 * specific organization which has shared document with them.
	 * 
	 * @param aoObjStore active filenet object store session
	 * @param asOrgId org id as input
	 * @return ArrayList list of provider/agency id for shared document
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getProviderAgencySharedList(ObjectStore aoObjStore, String asOrgId, String asPartialDomVal)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.getProviderAgencySharedList() with asOrgId::" + asOrgId);
		ArrayList<String> loArrProviderIdList = new ArrayList<String>();
		List<ProviderBean> loProvList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
				HHSR5Constants.PROV_LIST);
		TreeSet<String> loAgencySet = (TreeSet<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.AGENCY_LIST);
		SearchSQL loSqlObject = new SearchSQL();
		if (null == asOrgId || asOrgId.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getProviderId Method. Required Parameters are missing");
			loAppex.addContextData(HHSR5Constants.AS_ORG_ID, asOrgId);
			throw loAppex;
		}
		// Creating sql query for fetching provider/agency id from custom object
		// corresponding to given organization id
		String lsSQLQuery = "SELECT This," + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
				+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " FROM "
				+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
				+ " WHERE " + "SharedBy" + " = '" + asOrgId + "'";
		if (null != asPartialDomVal && !asPartialDomVal.isEmpty())
		{
			lsSQLQuery = lsSQLQuery + " AND " + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + " like '%"
					+ asPartialDomVal + "%' OR " + P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " like '%"
					+ asPartialDomVal + "%' ";
		}
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			// Iterating the result set
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				CustomObject loObj = (CustomObject) loIt.next();
				String lsProviderId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID);
				String lsAgencyId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID);
				// adding provider id to array list
				if (null != lsProviderId && !lsProviderId.trim().equals(""))
				{
					String loProvName = FileNetOperationsUtils.getProviderName(loProvList, lsProviderId);
					loArrProviderIdList.add(loProvName);
				}
				else if (null != lsAgencyId && !lsAgencyId.trim().equals(""))
				{
					String lsAgencyName = FileNetOperationsUtils.getAgencyName(loAgencySet, lsAgencyId);
					loArrProviderIdList.add(lsAgencyName);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error fetching provider/agency id for an organization :", aoEx);
			loAppex.addContextData(HHSR5Constants.AS_ORG_ID, asOrgId);
			LOG_OBJECT.Error("Error fetching provider/agency id for an organization :", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getProviderAgencySharedList() " + loArrProviderIdList);
		return loArrProviderIdList;
	}

	// R5 Ends

	/**
	 * This Method is for checking whether any specific folder is exist in the
	 * FILENET or not
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param asPath the path of the folder where the file is to be stored
	 * @return boolean status flag
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean checkFolderExistsById(ObjectStore aoObjectStore, String asId) throws ApplicationException
	{
		boolean lbFlag = false;
		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put(HHSR5Constants.AS_PATH, asId);
		LOG_OBJECT.Debug("Entered P8ContentOperations.checkFolderExistsById() with parameters::"
				+ loHmRequiredProp.toString());
		SearchSQL loSqlObject = new SearchSQL();

		try
		{
			if (asId == null || asId.equalsIgnoreCase(""))
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in checkFolderExists Method.Required Parameters are missing.");
				loAppex.setContextData(loHmRequiredProp);
				throw loAppex;
			}
			String lsSQLQuery = "Select This,id From HHSCustomFolder where Id = '" + asId + "'";
			// Checking whether folder instance exists or not
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the content search.
			SearchScope loSearchScope = new SearchScope(aoObjectStore);
			EngineSet loEngineSet = (EngineSet) loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			if (null != loEngineSet && loEngineSet.iterator().hasNext())
			{
				lbFlag = true;
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkFolderExists()::", aoAppex);
			throw aoAppex;
		}
		catch (EngineRuntimeException aoEc)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkFolderExists()::", aoEc);
			return lbFlag;

		}
		catch (Exception aoEx)
		{
			ApplicationException loExp = new ApplicationException(
					"Error in checkFolderExists Method.Required Parameters are missing.", aoEx);
			loExp.setContextData(loHmRequiredProp);
			LOG_OBJECT.Error("Error in checkFolderExists Method.Required Parameters are missing", aoEx);
			throw loExp;
		}

		LOG_OBJECT.Debug("Exited P8ContentOperations.checkFolderExists() " + lbFlag);
		return lbFlag;
	}

	/**
	 * This method is added as a part of release 5, which will be used to get
	 * Shared Organization Details For City.
	 * 
	 * @param aoOS ObjectStore
	 * @param asOrgId String
	 * @return loDataList
	 * @throws ApplicationException if there is any exception
	 */
	public HashMap<String, String> getSharedOrgDetails(ObjectStore aoObj, String asOrgId) throws ApplicationException
	{
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		SearchSQL loSqlObject = new SearchSQL();
		try
		{
			String lsSQLQuery = "SELECT HHSProviderID ,HHS_AGENCY_ID FROM HHSSharedDocument WHERE HHS_SHARED_BY= '"
					+ asOrgId + "'";
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObj);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			Iterator loItr = loRowSet.iterator();
			while (loItr.hasNext())
			{
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();
				if (null != loProp.getStringValue("HHSProviderID") && !loProp.getStringValue("HHSProviderID").isEmpty())
				{
					loDataMap.put(loProp.getStringValue("HHSProviderID"), HHSR5Constants.PROVIDER_ID);
				}
				else
				{
					loDataMap.put(loProp.getStringValue("HHS_AGENCY_ID"), HHSR5Constants.AGENCY);
				}
			}
		}
		catch (EngineRuntimeException aoEc)
		{
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkFolderExists()::", aoEc);

		}
		catch (Exception aoEx)
		{
			ApplicationException loExp = new ApplicationException(
					"Error in checkFolderExists Method.Required Parameters are missing.", aoEx);
			LOG_OBJECT.Error("Error in checkFolderExists Method.Required Parameters are missing", aoEx);
			throw loExp;
		}
		return loDataMap;
	}

	/**
	 * This method is added as a part of release 5, which will be used to get
	 * Org Id for Download.
	 * 
	 * @param aoOS ObjectStore
	 * @param aoSqlQuery StringBuffer
	 * @throws ApplicationException if there is any exception
	 */
	public List getOrgIdforDownload(ObjectStore aoOS, BulkDownloadBean aoDownloadBean) throws ApplicationException
	{

		SearchSQL loSqlObject = new SearchSQL();
		HashMap loHmExcepRequiredProp = new HashMap();
		StringBuffer loQuery = new StringBuffer();
		List<String> loFinalOrgList = new ArrayList<String>();
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.getOrgIdforDownload() ");
			if (null != aoDownloadBean)
			{
				createSqlQuery(aoDownloadBean, loQuery);
				if (null != loQuery)
				{
					LOG_OBJECT.Info("Bean Parameters are:::::::::::::" + aoDownloadBean);
					LOG_OBJECT.Info("Query is:::::::::::::" + loQuery);
					loSqlObject.setQueryString(loQuery.toString());
					SearchScope loSearchScope = new SearchScope(aoOS);
					RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
					Iterator loItr = loRowSet.iterator();
					while (loItr.hasNext())
					{
						RepositoryRowImpl loRowImpl = (RepositoryRowImpl) loItr.next();
						Properties loProp = loRowImpl.getProperties();
						if (!loFinalOrgList.contains(loProp.getStringValue("HHS_SHARED_BY")))
						{
							loFinalOrgList.add(loProp.getStringValue("HHS_SHARED_BY"));
						}
					}
				}
			}
		}
		catch (ApplicationException aoEx)
		{

			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocIdforDownload()::", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While getting list of DocIds for download",
					aoEx);
			loAppex.setContextData(loHmExcepRequiredProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getDocIdforDownload()::", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getDocIdforDownload() ");

		return loFinalOrgList;

	}

	/**
	 * This method is added as a part of release 5, which will be used to create
	 * Sql Query For Organization Search.
	 * 
	 * @param aoBean BulkDownloadBean
	 * @param aoSqlQuery StringBuffer
	 * @throws ApplicationException if there is any exception
	 */
	private static void createSqlQueryForOrganizationSearch(BulkDownloadBean aoBean, StringBuffer aoSqlQuery)
			throws ApplicationException
	{
		try
		{
			aoSqlQuery.append("SELECT DISTINCT DOC.PROVIDER_ID AS HHS_SHARED_BY "
					+ "from hhs_accelerator doc  WHERE DOC.DELETE_FLAG  = 0 AND ");
			if (null != aoBean.getMsModifiedFrom() && !aoBean.getMsModifiedFrom().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_FROM, aoBean.getMsModifiedFrom().toString());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_FROM,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsModifiedTo() && !aoBean.getMsModifiedTo().toString().isEmpty())
			{
				HashMap<String, String> loFilterMap = new HashMap<String, String>();
				loFilterMap.put(P8Constants.PROPERTY_MODIFIED_TO, aoBean.getMsModifiedTo().toString());
				aoSqlQuery = createWhereClauseAppendDate(aoSqlQuery, P8Constants.PROPERTY_MODIFIED_TO,
						HHSR5Constants.DOCUMENT_CLASS_ALIAS, loFilterMap, false);
				aoSqlQuery.append(" AND ");
			}
			if (null != aoBean.getMsFilterDocName() && !aoBean.getMsFilterDocName().isEmpty())
			{
				aoSqlQuery.append("DOC.DocumentTitle like '%" + aoBean.getMsFilterDocName() + "%' AND ");
			}

			aoSqlQuery.append(" ORGANIZATION_ID IS NOT NULL AND DOC.DOC_TYPE = '");
			aoSqlQuery.append(aoBean.getMsFilterDocType() + HHSR5Constants.STR);
		}
		catch (ApplicationException aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			throw loAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error While creating SQL Query", aoEx);
			LOG_OBJECT.Error("Error While creating SQL Query in createSqlQueryForDocTypeSearch", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method is added as a part of release 5, which will be used to
	 * calculate the maximum path length for all children folders.
	 * 
	 * @param aoDocumentList list of document bean
	 * @param asMovingFolderId moving folder id
	 * @param aoObjStore
	 * @return liMaxLength int
	 * @throws ApplicationException if there is any exception
	 */
	public int getLongestPath(List<com.nyc.hhs.model.Document> aoDocumentList, String asMovingFolderId,
			ObjectStore aoObjStore) throws ApplicationException
	{
		int liMaxLength = 0;
		String lsFolderPath = null;
		String lsOnlyChildrenFolderPath = null;
		String lsFolderName = null;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.getLongestPath() ");
			Folder loFldrObj = Factory.Folder.fetchInstance(aoObjStore, asMovingFolderId, null);
			for (com.nyc.hhs.model.Document loDocument : aoDocumentList)
			{
				lsFolderPath = loDocument.getFolderLocation();
				if (null != lsFolderPath && !lsFolderPath.isEmpty())
					lsOnlyChildrenFolderPath = lsFolderPath.replace(loFldrObj.get_PathName(), "");
				if (null != lsOnlyChildrenFolderPath && !lsOnlyChildrenFolderPath.isEmpty()
						&& lsOnlyChildrenFolderPath.length() > liMaxLength)
				{
					liMaxLength = lsOnlyChildrenFolderPath.length();
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error Occured while calculating maximum path length", aoEx);
			LOG_OBJECT.Error("Error Occured while calculating maximum path length", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.getLongestPath() ");
		return liMaxLength;

	}

	/**
	 * This method will inherit the folder properties if the document is getting
	 * uploaded into a custom folder
	 * 
	 * @param aoObjectStore Object store
	 * @param aoPropertyMap property map of the document
	 * @param asDocumentId document id
	 * @throws ApplicationException if there is any exception
	 */
	public void setParentFolderProprty(ObjectStore aoObjectStore, HashMap<String, Object> aoPropertyMap,
			String asDocumentId) throws ApplicationException
	{
		Folder loCustomParentFldr = null;
		PropertyFilter loPF = new PropertyFilter();
		FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.SHARED_ENTITY_ID, null);
		FilterElement loSharedFlag = new FilterElement(null, null, null, HHSR5Constants.SHARING_FLAG, null);
		String lsCustomFolderId = null;
		Document loDocumentObj = null;
		Id loSharedEntityId = null;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setParentFolderProprty() ");
			lsCustomFolderId = (String) aoPropertyMap.get(HHSR5Constants.CUSTOM_FLDR_ID);
			loPF.addIncludeProperty(loFE);
			loPF.addIncludeProperty(loSharedFlag);
			loCustomParentFldr = Factory.Folder.fetchInstance(aoObjectStore, lsCustomFolderId, loPF);
			if (null != loCustomParentFldr.getProperties().getStringValue(HHSR5Constants.SHARING_FLAG)
					&& Integer.valueOf(loCustomParentFldr.getProperties().getStringValue(HHSR5Constants.SHARING_FLAG)) == 2)
			{
				loSharedEntityId = loCustomParentFldr.getProperties().getIdValue(HHSR5Constants.SHARED_ENTITY_ID);
				loDocumentObj = Factory.Document.fetchInstance(aoObjectStore, asDocumentId, loPF);
				loDocumentObj.getProperties().putObjectValue(HHSR5Constants.SHARED_ENTITY_ID, loSharedEntityId);
				loDocumentObj.getProperties().putValue(HHSR5Constants.SHARING_FLAG, HHSR5Constants.ONE);
				loDocumentObj.save(RefreshMode.REFRESH);
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error Occured while uploading your Document", aoEx);
			LOG_OBJECT.Error("Error Occured while uploading your Document", aoEx);
			throw loAppex;
		}
	}

	/**
	 * This method gets the document properties from filenet on the basis of
	 * document Id
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param aoHmRequiredProps hash map specifying the required properties map
	 * @param aoDocumentsIdList list of document-id
	 * @return HashMap map of document properties by id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getSharePropertiesById(ObjectStore aoObjectStore, HashMap aoHmRequiredProps, List aoDocumentsIdList)
			throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, HashMap<String, String>> loHmDocumentDetails = new HashMap<String, HashMap<String, String>>();
		loHmReqExceProp.put("aoDocIdList", aoDocumentsIdList);
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		LOG_OBJECT.Info("Entered P8ContentOperations.getSharePropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		List loProviderList = new ArrayList();
		List<String> loSharedEntityList = new ArrayList<String>();
		StringBuffer lsSharedOrgName = new StringBuffer();

		try
		{
			if (null == aoDocumentsIdList || aoDocumentsIdList.size() <= 0)
			{
				return null;
			}
			HashMap loHmPropFilter = new HashMap();
			loHmPropFilter.put(HHSR5Constants.DOC_TYPE, HHSR5Constants.DOC_TYPE);
			PropertyFilter loPF = createPropertyFilter(loHmPropFilter);
			for (Iterator loDocIterator = aoDocumentsIdList.iterator(); loDocIterator.hasNext();)
			{
				HashMap loHmProps = new HashMap();
				String lsObjId = (String) loDocIterator.next();
				Document loDoc = Factory.Document.fetchInstance(aoObjectStore, lsObjId, loPF);
				if (null != loDoc)
				{
					Properties loProps = loDoc.getProperties();
					Iterator loIt = loProps.iterator();
					while (loIt.hasNext())
					{
						Property loProp = (Property) loIt.next();
						loHmProps.put(loProp.getPropertyName(), loProp.getObjectValue());
					}
					String lsDocType = loProps.getStringValue(P8Constants.PROPERTY_CE_DOC_TYPE);
					String docId = lsObjId + HHSConstants.COMMA + lsDocType;
					loProviderList = getProviderId(aoObjectStore, docId);
					if (null != loProviderList && !loProviderList.isEmpty())
					{
						loSharedEntityList = FileNetOperationsUtils.getProviderAndAgencyNameListById(loProviderList);
						Iterator loItr = loSharedEntityList.iterator();
						while (loItr.hasNext())
						{
							String loNext = (String) loItr.next();
							lsSharedOrgName.append(loNext);
							if (loItr.hasNext())
							{
								lsSharedOrgName.append(",");
							}
						}
						String lsSharing = lsSharedOrgName.toString();
						((HashMap) aoHmRequiredProps.get(lsObjId)).put(HHSR5Constants.SHARE_LIST, lsSharing);

					}
				}
				else
				{
					aoHmRequiredProps.put(lsObjId, null);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getSharePropertiesById()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getting Share document Propeties", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Share document Propeties", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getSharePropertiesById()");
		return aoHmRequiredProps;

	}

	/**
	 * This method gets the document properties from filenet on the basis of
	 * document Id
	 * 
	 * @param aoObjectStore active filenet object store session
	 * @param aoHmRequiredProps hash map specifying the required properties map
	 * @param aoDocumentsIdList list of document-id
	 * @return HashMap map of document properties by id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getShareFolderPropertiesById(ObjectStore aoObjectStore, HashMap aoHmRequiredProps,
			List aoDocumentsIdList) throws ApplicationException
	{

		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, HashMap<String, String>> loHmDocumentDetails = new HashMap<String, HashMap<String, String>>();
		loHmReqExceProp.put("aoDocIdList", aoDocumentsIdList);
		loHmReqExceProp.put("aoHmRequiredProps", aoHmRequiredProps);
		LOG_OBJECT.Info("Entered P8ContentOperations.getShareFolderPropertiesById() with parameters::"
				+ loHmReqExceProp.toString());
		List loProviderList = new ArrayList();
		List<String> loSharedEntityList = new ArrayList<String>();
		StringBuffer lsSharedOrgName = new StringBuffer();

		try
		{
			if (null == aoDocumentsIdList || aoDocumentsIdList.size() <= 0)
			{
				return null;
			}
			HashMap loHmPropFilter = new HashMap();
			loHmPropFilter.put(HHSR5Constants.DOC_TYPE, HHSR5Constants.DOC_TYPE);
			PropertyFilter loPF = createPropertyFilter(loHmPropFilter);
			for (Iterator loDocIterator = aoDocumentsIdList.iterator(); loDocIterator.hasNext();)
			{
				HashMap loHmProps = new HashMap();
				String lsObjId = (String) loDocIterator.next();
				Folder loDoc = Factory.Folder.fetchInstance(aoObjectStore, lsObjId, loPF);
				if (null != loDoc)
				{
					loProviderList = getProviderId(aoObjectStore, lsObjId);
					if (null != loProviderList && !loProviderList.isEmpty())
					{
						loSharedEntityList = FileNetOperationsUtils.getProviderAndAgencyNameListById(loProviderList);
						Iterator loItr = loSharedEntityList.iterator();
						while (loItr.hasNext())
						{
							String loNext = (String) loItr.next();
							lsSharedOrgName.append(loNext);
							if (loItr.hasNext())
							{
								lsSharedOrgName.append(",");
							}
						}
						String lsSharing = lsSharedOrgName.toString();
						((HashMap) aoHmRequiredProps.get(lsObjId)).put(HHSR5Constants.SHARE_LIST, lsSharing);
					}
				}
				else
				{
					aoHmRequiredProps.put(lsObjId, null);
				}
			}
		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.getShareFolderPropertiesById()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getting Share folder Propeties", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error in getting Share folder Propeties", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getShareFolderPropertiesById()");
		return aoHmRequiredProps;

	}

	/**
	 * This method is added in release 5 , this will set the document category
	 * and document category in filter map for newly added document ypes in
	 * release 5
	 * @param aoFilterMap filter map
	 * @param abCharExtensionSelectFromVaultFlag is document ype is CHAR500 type
	 *            document
	 * @return updated filter map
	 * @throws ApplicationException
	 */
	private boolean setFilterCategoryForMaskedType(HashMap aoFilterMap, boolean abCharExtensionSelectFromVaultFlag)
			throws ApplicationException
	{
		if (aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_TYPE) != null
				&& !((String) aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_TYPE)).isEmpty())
		{
			String lsDocumentType = aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_TYPE).toString();
			aoFilterMap.put(P8Constants.PROPERTY_CE_DOC_TYPE, lsDocumentType.replace("'", "''"));

		}
		if (aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY) != null
				&& aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY).toString().contains("'"))
		{
			aoFilterMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, aoFilterMap.get(P8Constants.PROPERTY_CE_DOC_CATEGORY)
					.toString().replace("'", "''"));
		}
		// Updated for 3.1.0, Enhancement 6021, adding Select From Vault
		// Check
		if (aoFilterMap.get(HHSConstants.SELECT_FROM_VAULT_BUSINESS_APLLICATION_FLAG) != null
				&& aoFilterMap.get(HHSConstants.SELECT_FROM_VAULT_BUSINESS_APLLICATION_FLAG).toString()
						.equalsIgnoreCase(ApplicationConstants.TRUE))
		{
			abCharExtensionSelectFromVaultFlag = true;
			aoFilterMap.remove("selectFromVaultFlag");
		}
		return abCharExtensionSelectFromVaultFlag;
	}

	/**
	 * This method is added as a part of release 5, which will be used to fetch
	 * Total Page Count.
	 * 
	 * @param aoObjStr filenet object store session
	 * @param lsQuery String
	 * @param liPageSize int
	 * @return liFinalPageCount int to save page count
	 * @throws ApplicationException if there is any exception
	 */
	public void fetchTotalPageCount(ObjectStore aoObjStr, P8UserSession aoUserSession, String lsQuery, int liPageSize)
	{
		int liCount = 0;
		if (aoUserSession.getPageIteratorForTotal() == null)
		{
			SearchScope loSearchScope = new SearchScope(aoObjStr);
			SearchSQL loSqlObject = new SearchSQL();
			LOG_OBJECT.Info("Entered P8ContentOperations.fetchTotalPageCount()");
			loSqlObject.setQueryString(lsQuery);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, Integer.valueOf(liPageSize * 6), null,
					Boolean.valueOf(true));
			PageIterator loPageItr = loRowSet.pageIterator();
			if (loPageItr.nextPage() == true)
			{
				liCount = liCount + loPageItr.getCurrentPage().length;
			}
			int liFinalPageCount = (int) Math.floor(liCount / liPageSize);
			if (liCount % liPageSize > 0)
				liFinalPageCount++;

			aoUserSession.setPageIteratorForTotal(loPageItr);
			aoUserSession.setTotalPageCount(liFinalPageCount);
		}
		else
		{
			PageIterator loPageItr = aoUserSession.getPageIteratorForTotal();
			if (loPageItr.nextPage() == true)
			{
				liCount = liCount + loPageItr.getCurrentPage().length;
			}
			if (liCount > 0)
			{
				int liFinalPageCount = (int) Math.floor(liCount / liPageSize);
				if (liCount % liPageSize > 0)
					liFinalPageCount++;
				aoUserSession.setTotalPageCount(aoUserSession.getTotalPageCount() + liFinalPageCount);
			}
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.fetchTotalPageCount()");
	}

	/**
	 * Below method will check and confirm whether a document with same title
	 * category and type is linked to any application or not.
	 * 
	 * @param aoOs filenet object store session
	 * @param asDocumentId document id as input
	 * @return boolean lbIsLinkedToApp it its true the document is linked to
	 *         application
	 * @throws ApplicationException if there is any exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void getDocumentsLinkFoldersFiledInProp(ObjectStore aoOs, com.nyc.hhs.model.Document loDoc)
			throws ApplicationException
	{

		HashMap<String, String> loPropertyMap = new HashMap<String, String>();
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("documentid", loDoc.getDocumentId());
		LOG_OBJECT.Info("Entered P8ContentOperations.checkDocumentLinkedToApplication() with parameters::"
				+ loHmReqExceProp.toString());
		Document loDoc1 = null;
		PropertyFilter loPropFilter = null;
		try
		{
			loPropertyMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, "");
			loPropertyMap.put(HHSR5Constants.FOLDERS_FILED_IN, "");
			loPropFilter = createPropertyFilter(loPropertyMap);
			loDoc1 = Factory.Document.fetchInstance(aoOs, loDoc.getDocumentId(), loPropFilter);
			Properties loProps = loDoc1.getProperties();
			FolderSet loFolderSet = (FolderSet) loProps.getObjectValue(HHSR5Constants.FOLDERS_FILED_IN);
			for (Iterator iterator = loFolderSet.iterator(); iterator.hasNext();)
			{
				Folder type = (Folder) iterator.next();
				if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
				{
					loDoc.setParent(type);
				}

			}
			loDoc.setLinkToApplication(loProps.getBooleanValue(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION));

		}
		catch (ApplicationException aoAppex)
		{
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in P8ContentOperations.checkDocumentLinkedToApplication()::", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error while checking link to application for a document : ", aoEx);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE, "ERR_LINKED_TO_APP");
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error while checking link to application for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.checkDocumentLinkedToApplication()");
	}

	/**
	 * This method will change the modified date property in Filenet
	 * 
	 * @param aoObjectStore Object store
	 * @param asParentPath String parentpath
	 * @throws ApplicationException if there is any exception
	 */
	public void setParentModifiedDateProperty(ObjectStore aoObjectStore, String asParentPath)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.setParentModifiedDateProperty()");
			if (null != asParentPath && !asParentPath.endsWith(HHSR5Constants.DOCUMENT_VAULT))
			{
				Folder loParentFolder = Factory.Folder.fetchInstance(aoObjectStore, asParentPath, null);
				Properties loParentFolderProp = loParentFolder.getProperties();
				loParentFolderProp.putValue(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
						HHSUtil.getCurrentTimestampDate());
				loParentFolder.setUpdateSequenceNumber(null);
				loParentFolder.save(RefreshMode.REFRESH);
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Error Occured while P8ContentOperations.setParentModifiedDateProperty()", aoAppex);
			throw aoAppex;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error Occured P8ContentService.setParentModifiedDateProperty()", aoEx);
			LOG_OBJECT.Error("Error Occured while P8ContentOperations.setParentModifiedDateProperty()", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.setParentModifiedDateProperty()");
	}

	/**
	 * This method will create Custom Obj In Filenet
	 * 
	 * @param aoObjectStore a custom user bean having information about user
	 * @param aoList list of ProviderBean
	 * @return loCount count of Custom Obj created In Filenet
	 * @throws ApplicationException if there is any exception
	 */
	public int createCustomObjInFilenet(ObjectStore aoObjectStore, List<ProviderBean> aoList)
			throws ApplicationException
	{
		Integer loCount = 0;
		String lsSqlQuery = null;
		SearchSQL loSqlObject = new SearchSQL();
		CustomObject loCustObj;
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.createCustomObjInFilenet()");
			for (Iterator iterator = aoList.iterator(); iterator.hasNext();)
			{
				ProviderBean providerBean = (ProviderBean) iterator.next();
				lsSqlQuery = "select this,id from ORGANIZATION_DETAILS WHERE PROVIDER_ID = '"
						+ providerBean.getHiddenValue() + "'";
				loSqlObject.setQueryString(lsSqlQuery);
				SearchScope loSearchScope = new SearchScope(aoObjectStore);
				RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, true);
				if (!loRowSet.iterator().hasNext())
				{
					createCustomObjectForOrganization(aoObjectStore, providerBean);
					loCount++;
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
		LOG_OBJECT.Info("Exited P8ContentOperations.createCustomObjInFilenet()");
		return loCount;

	}

	/**
	 * This method is added as a part of release 5, which will be used to create
	 * Custom Object For Organization.
	 * 
	 * @param aoObjectStore a custom user bean having information about user
	 * @param providerBean ProviderBean
	 * @throws ApplicationException if there is any exception
	 */
	public void createCustomObjectForOrganization(ObjectStore aoObjectStore, ProviderBean providerBean)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Error("Entered P8ContentOperations.createCustomObjectForOrganization()");
			CustomObject loCustObj;
			loCustObj = Factory.CustomObject.createInstance(aoObjectStore, "ORGANIZATION_DETAILS");
			Properties loProps = loCustObj.getProperties();
			loProps.putValue("PROVIDER_ID", providerBean.getHiddenValue());
			loProps.putValue("ORG_LEGAL_NAME", providerBean.getDisplayValue());
			loCustObj.save(RefreshMode.REFRESH);
			// Adding below line for Emergency Release 4.0.1
			loCustObj.setUpdateSequenceNumber(null);
			fileCustomObjectToFolder(aoObjectStore, loCustObj, "/ORGANIZATION_DETAILS", "ORGANIZATION_DETAILS");
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
		LOG_OBJECT.Error("Exited P8ContentOperations.createCustomObjectForOrganization()");
	}

	/**
	 * This method is added as a part of release 5, which will be used to update
	 * Custom Object For Organization.
	 * 
	 * @param aoObjectStore a custom user bean having information about user
	 * @param providerBean ProviderBean
	 * @throws ApplicationException if there is any exception
	 */
	public void updateCustomObjectForOrganization(ObjectStore aoObjectStore, ProviderBean providerBean)
			throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Info("Entered P8ContentOperations.updateCustomObjectForOrganization()");
			String lsSqlQuery = null;
			SearchSQL loSqlObject = new SearchSQL();
			lsSqlQuery = "select this,id from ORGANIZATION_DETAILS WHERE PROVIDER_ID = '"
					+ providerBean.getHiddenValue() + "'";
			loSqlObject.setQueryString(lsSqlQuery);
			SearchScope loSearchScope = new SearchScope(aoObjectStore);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, true);
			Iterator loItr = loRowSet.iterator();
			while (loItr.hasNext())
			{
				RepositoryRow loRow = (RepositoryRow) loItr.next();
				Properties loProp = loRow.getProperties();
				CustomObject loObj = Factory.CustomObject.fetchInstance(aoObjectStore, loProp.getIdValue("Id"), null);
				loObj.getProperties().putValue("ORG_LEGAL_NAME", providerBean.getDisplayValue());
				// Adding below line for Emergency Release 4.0.1
				loObj.setUpdateSequenceNumber(null);
				loObj.save(RefreshMode.REFRESH);
			}
		}
		catch (Exception aoEx)
		{
			setMoState("Error in getting Share folder Propeties ");
			ApplicationException loAppex = new ApplicationException("Error in getting Share folder Propeties : ", aoEx);
			LOG_OBJECT.Error("Error in getting folder document Propeties", aoEx);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exited P8ContentOperations.updateCustomObjectForOrganization()");
	}

	/**
	 * This method will fetch shared document Id's form Filenet for defect 8150
	 * @param aoObjStore
	 * @param asAgencyType
	 * @param asProviderId
	 * @param asShareWith
	 * @return
	 * @throws ApplicationException
	 */
	public HashMap<String, List<String>> getSharedDocumentsIdList(ObjectStore aoObjStore, String asAgencyType,
			String asProviderId, String asShareWith) throws ApplicationException
	{
		HashMap loHmReqExceProp = new HashMap();
		HashMap<String, List<String>> loSharedIdMap = new HashMap<String, List<String>>();
		loHmReqExceProp.put("asProviderId", asProviderId);
		LOG_OBJECT.Info("Entered P8ContentOperations.getSharedDocumentsIdList() with parameters::"
				+ loHmReqExceProp.toString());
		SearchSQL loSqlObject = new SearchSQL();
		if (asProviderId.equalsIgnoreCase(""))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getSharedDocumentsOwnerList Method. Required Parameters are missing");
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		String lsSQLQuery = "SELECT HHS_SHARED_BY,SHARED_DOCUMENT_ID FROM HHSSharedDocument shr left join HHS_CUSTOM_FOLDER fo on shr.SHARED_DOCUMENT_ID = fo.id  WHERE fo.SHARING_FLAG = '2' AND fo.DELETE_FLAG = 0 AND HHS_SHARED_BY = '"
				+ asProviderId + "' AND " + asAgencyType + " ='" + asShareWith + "'";
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);
			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			RepositoryRowSet loRowSet = loSearchScope.fetchRows(loSqlObject, null, null, Boolean.TRUE);
			// Iterating the result set
			Iterator loIt = loRowSet.iterator();
			while (loIt.hasNext())
			{
				RepositoryRow loRow = (RepositoryRow) loIt.next();
				Properties loProp = loRow.getProperties();
				if (null != loProp.getIdValue("SHARED_DOCUMENT_ID")
						&& !loProp.getIdValue("SHARED_DOCUMENT_ID").toString().isEmpty())
				{
					List<String> loList = new ArrayList<String>();
					if (loSharedIdMap.containsKey(loProp.getStringValue("HHS_SHARED_BY")))
					{
						loList = loSharedIdMap.get(loProp.getStringValue("HHS_SHARED_BY"));
						loList.add(loProp.getIdValue("SHARED_DOCUMENT_ID").toString());
						loSharedIdMap.put(loProp.getStringValue("HHS_SHARED_BY"), loList);
					}
					else
					{
						loList.add(loProp.getIdValue("SHARED_DOCUMENT_ID").toString());
						loSharedIdMap.put(loProp.getStringValue("HHS_SHARED_BY"), loList);
					}
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching provider id for a document :", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error fetching provider id for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getSharedDocumentsIdList()");
		return loSharedIdMap;
	}

	/**
	 * This will give the sharing entity Id
	 * @param aoObjStore
	 * @param asFolderId
	 * @return
	 * @throws ApplicationException
	 */
	private static String getSharingIdForFolder(ObjectStore aoObjStore, String asFolderId) throws ApplicationException
	{
		String lsSharingEntityId = null;

		try
		{
			PropertyFilter loPF = new PropertyFilter();
			FilterElement loFE = new FilterElement(null, null, null, HHSR5Constants.SHARED_ENTITY_ID, null);
			loPF.addIncludeProperty(loFE);
			Folder loFldr = Factory.Folder.fetchInstance(aoObjStore, asFolderId, loPF);
			lsSharingEntityId = loFldr.getProperties().getIdValue(HHSR5Constants.SHARED_ENTITY_ID).toString();
		}
		catch (Exception e)
		{
			throw new ApplicationException(e.getMessage());
		}
		return lsSharingEntityId;
	}

	/**
	 * This method will get Sql Query For Bulk Upload.
	 * @param aoFilterMap HashMap<String, Object>
	 * @return lsSqlQuery String
	 * @throws ApplicationException - if any exception occurs
	 */
	private String getSqlQueryForBulkUpload(HashMap<String, Object> aoFilterMap) throws ApplicationException
	{
		String lsSqlQuery = null;
		try
		{
			lsSqlQuery = "select TEMPLATE_VERSION_NO,LAST_MODIFIED_DATE,id from BULK_UPLOAD_TEMPLATE where IS_BULK_UPLOAD = true";
		}
		catch (Exception e)
		{
			throw new ApplicationException(e.getMessage());
		}
		return lsSqlQuery;
	}

	/**
	 * This method is used to update Linkage In Filenet.
	 * @param aoObjStore ObjectStore
	 * @param asDocumentId String
	 * @return lbLinked Boolean
	 * @throws ApplicationException - if any exception occurs
	 */
	public Boolean updateLinkageInFilenet(ObjectStore aoObjStore, String asDocumentId) throws ApplicationException
	{
		boolean lbLinked = false;

		try
		{
			PropertyFilter loPF = new PropertyFilter();
			FilterElement loFE = new FilterElement(null, null, null, P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
					null);
			loPF.addIncludeProperty(loFE);
			Document loDoc = Factory.Document.fetchInstance(aoObjStore, asDocumentId, loPF);
			loDoc.getProperties().putValue(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loDoc.save(RefreshMode.REFRESH);
			lbLinked = true;
		}

		catch (Exception e)
		{
			throw new ApplicationException(e.getMessage());
		}

		return lbLinked;
	}

	/**
	 * The method will check if there is document present in the document vault
	 * or not for the logged in user.
	 * @param aoObj
	 * @param asFolderPath
	 * @return
	 * @throws ApplicationException
	 */
	public boolean checkDocumentPresent(ObjectStore aoObj, String asFolderPath) throws ApplicationException
	{
		List<String> lsDocIdListInner = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		boolean lbDocPresent = false;
		try
		{
			String lsQuery = "select Id from HHS_ACCELERATOR where This INSUBFOLDER '" + asFolderPath
					+ "' and DELETE_FLAG  = 0";
			loSqlObject.setQueryString(lsQuery);
			SearchScope loSearchScope = new SearchScope(aoObj);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{
				lbDocPresent = true;
				break;
			}
		}
		catch (Exception aoEx)
		{
			setMoState("getDataFromFilenet");
			ApplicationException loAppex = new ApplicationException("getTotalDocumentCount", aoEx);
			LOG_OBJECT.Error("getTotalDocumentCount", aoEx);
			throw loAppex;
		}
		return lbDocPresent;
	}

	/**
	 * The method will check if there is document or folder present in the
	 * document vault or not for the logged in user.
	 * @param aoObjectStore ObjectStore
	 * @param aoSelectedObjectsMap HashMap<String,String>
	 * @param asJspName String
	 * @return lbAllObjectsPresent boolean
	 * @throws ApplicationException - if any exception occurs
	 */
	public boolean checkFolderAndDocumentExist(ObjectStore aoObjectStore, HashMap<String, String> aoSelectedObjectsMap,
			String asJspName) throws ApplicationException
	{
		boolean lbAllObjectsPresent = false;
		StringBuffer lsDocumentIdString = new StringBuffer();
		StringBuffer lsFolderIdString = new StringBuffer();
		StringBuffer lsDocumentSqlString = new StringBuffer();
		StringBuffer lsFolderSqlString = new StringBuffer();
		boolean lbAddComa = false;
		boolean lbAddComaInFolder = false;
		int liRenderedObjects = 0;
		SearchSQL loDocSqlObject = new SearchSQL();
		SearchSQL loFolderSqlObject = new SearchSQL();
		int liDocCount = 0;
		int liFolderCount = 0;
		boolean lbExecuteDocQuery = false;
		boolean lbExecuteFolderQuery = false;
		try
		{
			for (Map.Entry<String, String> entry : aoSelectedObjectsMap.entrySet())
			{
				liRenderedObjects++;
				if (null != entry.getValue() && !entry.getValue().isEmpty()
						&& !entry.getValue().equalsIgnoreCase(HHSConstants.NULL))
				{
					if (liRenderedObjects > 0 && lbAddComa)
						lsDocumentIdString.append(",");
					lsDocumentIdString = lsDocumentIdString.append("'").append(entry.getKey()).append("'");
					lbAddComa = true;
					lbExecuteDocQuery = true;
				}
				else
				{
					if (liRenderedObjects > 0 && lbAddComaInFolder)
						lsFolderIdString.append(",");
					lsFolderIdString.append("'").append(entry.getKey()).append("'");
					lbAddComaInFolder = true;
					lbExecuteFolderQuery = true;
				}
			}
			// For documents
			if (lbExecuteDocQuery)
			{
				lsDocumentSqlString.append("select Id from HHS_ACCELERATOR where id in ( ");
				lsDocumentSqlString.append(lsDocumentIdString).append(") and ");
				if (asJspName != null && asJspName.startsWith(HHSR5Constants.RECYCLE_BIN_ID))
				{
					lsDocumentSqlString.append("(delete_flag=1 or delete_flag=2)");
				}
				else
				{
					lsDocumentSqlString.append("delete_flag=0");
				}
				loDocSqlObject.setQueryString(lsDocumentSqlString.toString());
				SearchScope loSearchScope = new SearchScope(aoObjectStore);
				RepositoryRowSet loRowSet = loSearchScope.fetchRows(loDocSqlObject, null, null, Boolean.TRUE);
				PageIterator loDocItr = loRowSet.pageIterator();
				if (loDocItr.nextPage())
					liDocCount = loDocItr.getCurrentPage().length;
			}
			// For folder
			if (lbExecuteFolderQuery)
			{
				lsFolderSqlString.append("select Id from HHS_CUSTOM_FOLDER where id in ( ");
				lsFolderSqlString.append(lsFolderIdString).append(") and ");
				if (asJspName != null && asJspName.startsWith(HHSR5Constants.RECYCLE_BIN_ID))
				{
					lsFolderSqlString.append("(delete_flag=1 or delete_flag=2)");
				}
				else
				{
					lsFolderSqlString.append("delete_flag=0");
				}
				loDocSqlObject.setQueryString(lsFolderSqlString.toString());
				SearchScope loFolderSearchScope = new SearchScope(aoObjectStore);
				RepositoryRowSet loFolderRowSet = loFolderSearchScope.fetchRows(loDocSqlObject, 50, null, Boolean.TRUE);
				PageIterator loFolderItr = loFolderRowSet.pageIterator();
				if (loFolderItr.nextPage())
					liFolderCount = loFolderItr.getCurrentPage().length;
			}
			if (liDocCount + liFolderCount == aoSelectedObjectsMap.size())
				lbAllObjectsPresent = true;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"!Some of the selected items has been deleted please change your selection and try again.", aoEx);
			throw loAppex;
		}
		return lbAllObjectsPresent;
	}

	/**
	 * The method is used to check Parent Child folder RelationShip
	 * @param aoObjectStore ObjectStore
	 * @param aoSelectedObjectsMap HashMap<String, String>
	 * @return loPathList List<String>
	 * @throws ApplicationException - if any exception occurs.
	 */
	public List<com.nyc.hhs.model.Document> checkParentChildRelationShip(ObjectStore aoObjectStore,
			List<com.nyc.hhs.model.Document> aoSelectedList, String asActionName) throws ApplicationException
	{
		HashMap<String, Object> loPathMap = new HashMap<String, Object>();
		List<com.nyc.hhs.model.Document> loFinalList = new ArrayList<com.nyc.hhs.model.Document>();
		List<com.nyc.hhs.model.Document> loRemoveList = new ArrayList<com.nyc.hhs.model.Document>();
		try
		{
			HashMap<String, String> loMap = new HashMap<String, String>();
			for (Iterator<com.nyc.hhs.model.Document> aoObjItr = aoSelectedList.iterator(); aoObjItr.hasNext();)
			{
				com.nyc.hhs.model.Document loSelectedObj = aoObjItr.next();
				if (null == loSelectedObj.getDocType() || loSelectedObj.getDocType().isEmpty()
						|| loSelectedObj.getDocType().equalsIgnoreCase(HHSR5Constants.NULL))
				{
					loMap.put(HHSR5Constants.PATH_NAME, HHSR5Constants.EMPTY_STRING);
					loMap.put(HHSR5Constants.FILENET_MOVE_FROM, HHSR5Constants.EMPTY_STRING);
					loMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
					loMap.put("Parent", HHSR5Constants.EMPTY_STRING);
					PropertyFilter loPf = createPropertyFilter(loMap);
					loSelectedObj.setDocType(null);
					Folder loFldr = Factory.Folder.fetchInstance(aoObjectStore, loSelectedObj.getDocumentId(), loPf);
					loRemoveList = uniqueFileNetObjectIds(loPathMap, asActionName, loSelectedObj, loFldr, null,
							loFinalList);
					loFinalList.removeAll(loRemoveList);
				}
				else
				{
					loMap.put(HHSR5Constants.FOLDERS_FILED_IN, HHSR5Constants.EMPTY_STRING);
					loMap.put(HHSR5Constants.PARENT_PATH, HHSR5Constants.EMPTY_STRING);
					loMap.put(HHSR5Constants.DELETE_ENTITY_ID, HHSR5Constants.EMPTY_STRING);
					PropertyFilter loPf = createPropertyFilter(loMap);
					Document loDoc = Factory.Document.fetchInstance(aoObjectStore, loSelectedObj.getDocumentId(), loPf);
					loRemoveList = uniqueFileNetObjectIds(loPathMap, asActionName, loSelectedObj, null, loDoc,
							loFinalList);
					loFinalList.removeAll(loRemoveList);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"!Some of the selected items has been deleted please change your selection and try again.", aoEx);
			throw loAppex;
		}
		loFinalList = FileNetOperationsUtils.getUniqueListVal(loFinalList);
		return loFinalList;
	}

	/**
	 * This method will give the unique entity id's form a set of data.
	 * @param loPathMap
	 * @param aoRemoveList
	 * @param loSelectedObj
	 * @param loFldr
	 */
	private List<com.nyc.hhs.model.Document> uniqueFileNetObjectIds(HashMap<String, Object> loPathMap,
			String asActionName, com.nyc.hhs.model.Document loSelectedObj, Folder aoFldr, Document aoDoc,
			List<com.nyc.hhs.model.Document> aoFinalList)
	{
		Iterator loIt = aoFinalList.iterator();
		List<com.nyc.hhs.model.Document> loRemovalListTemp = new ArrayList<com.nyc.hhs.model.Document>();
		List<com.nyc.hhs.model.Document> aoRemoveList = new ArrayList<com.nyc.hhs.model.Document>();
		String lsCheckValue = "";
		String lsFolderFiledInPath = null;
		com.nyc.hhs.model.Document loDocument = null;
		String lsDeleteEntityId = "";
		if (null != aoFldr)
		{
			if (null != asActionName && !asActionName.isEmpty()
					&& !asActionName.equalsIgnoreCase(HHSR5Constants.RESTORE))
			{
				lsCheckValue = aoFldr.get_PathName() + "/";
			}
			else
			{
				lsCheckValue = aoFldr.getProperties().getStringValue(HHSR5Constants.FILENET_MOVE_FROM) + "/";
			}
			lsDeleteEntityId = aoFldr.getProperties().getStringValue(HHSR5Constants.DELETE_ENTITY_ID);
			lsFolderFiledInPath = aoFldr.get_Parent().get_PathName() + "/";
			loSelectedObj.setFiledInPath(lsFolderFiledInPath);
			loSelectedObj.setDeletionEntityId(lsDeleteEntityId);
			loSelectedObj.setFilePath(lsCheckValue);
		}
		else if (null != aoDoc)
		{
			if (null != asActionName && !asActionName.isEmpty()
					&& !asActionName.equalsIgnoreCase(HHSR5Constants.RESTORE))
			{
				FolderSet loFolderSet = (FolderSet) aoDoc.getProperties().getObjectValue(
						HHSR5Constants.FOLDERS_FILED_IN);
				for (Iterator loFolderItr = loFolderSet.iterator(); loFolderItr.hasNext();)
				{
					Folder type = (Folder) loFolderItr.next();
					if (type.getClassName().equalsIgnoreCase(HHSR5Constants.HHS_CUSTOM_FOLDER))
					{
						lsCheckValue = type.get_PathName() + "/";
					}
				}
			}
			else
			{
				lsCheckValue = aoDoc.getProperties().getStringValue(HHSR5Constants.PARENT_PATH) + "/";
			}
			lsDeleteEntityId = aoDoc.getProperties().getStringValue(HHSR5Constants.DELETE_ENTITY_ID);
			loSelectedObj.setFiledInPath(lsCheckValue);
			loSelectedObj.setDeletionEntityId(lsDeleteEntityId);
			loSelectedObj.setFilePath(lsCheckValue);

		}
		boolean lbFlag = true;
		boolean lbRemoveFlag = false;
		boolean lbSelfDeleteFlag = false;
		while (loIt.hasNext())
		{
			lbFlag = false;
			loDocument = (com.nyc.hhs.model.Document) loIt.next();
			String lsDocType = loDocument.getDocType();
			String lsPathInList = loDocument.getFilePath();
			if (asActionName.equalsIgnoreCase(HHSR5Constants.RESTORE))
			{
				if (lsDeleteEntityId.equalsIgnoreCase(loSelectedObj.getDocumentId()))
				{
					lbSelfDeleteFlag = true;
				}
			}
			// If new Object is folder
			if (null != aoFldr)
			{
				// if new object is child of already added object, then break
				// the iteration and iterate next item
				if (lsCheckValue.contains(lsPathInList) && !lsFolderFiledInPath.equalsIgnoreCase(lsPathInList)
						&& null != lsDocType)
				{
					lbFlag = true;
					loRemovalListTemp.add(loDocument);
					// aoFinalList.remove(loDocument);
					break;
				}
				// if new object is parent of added element, then remove added
				// element and add put flag true to add parent into list
				else
				{	// condition added for multiselect same name folder restore 4.0.2.0
					if (lsPathInList.contains(lsCheckValue) && !lsPathInList.equalsIgnoreCase(lsCheckValue))
					{
						loRemovalListTemp.add(loDocument);
						// aoFinalList.remove(loDocument);
					}
					if (!lsFolderFiledInPath.contains(lsPathInList))
					{
						lbFlag = true;
					}

				}
			}
			else if (null != aoDoc)
			{
				if (lsPathInList.equalsIgnoreCase(lsCheckValue) && null != lsDocType)
				{
					lbFlag = true;
				}
				else if (loDocument.getFiledInPath().equalsIgnoreCase(lsCheckValue) && null == lsDocType)
				{
					lbFlag = true;
				}
			}

		}
		if (lbFlag)
		{
			aoFinalList.add(loSelectedObj);
		}
		if (!lbFlag && lbSelfDeleteFlag)
		{
			aoFinalList.add(loSelectedObj);
		}
		return loRemovalListTemp;
	}

	
	/*[Start] R9.3.2 QC9665 **/
	@SuppressWarnings("rawtypes")
	public List getProviderAgencyId(ObjectStore aoObjStore, String asDocId) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered P8ContentOperations.getProviderId() with asDocId::" + asDocId);

		ArrayList<String> loArrProviderId = new ArrayList<String>();
		SearchSQL loSqlObject = new SearchSQL();
		String lsDocType = null;
		Properties loProp = null;

		if (asDocId == null || asDocId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getProviderId Method. Required Parameters are missing");
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			throw loAppex;
		}
		// Creating sql query for fetching provider id from custom object
		// corresponding to given document id

		String lsSQLQuery = "SELECT " + P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + ","
				+ P8Constants.PROPERTY_CE_SHARED_AGENCY_ID + " FROM "
				+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "SHARED_DOC_PROVIDER_CUSTOM_OBJECT")
				+ " WHERE " + P8Constants.PROPERTY_CE_SHARED_DOC_ID + " = '" + asDocId + "' Order By "
				+ P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID + "," + P8Constants.PROPERTY_CE_SHARED_AGENCY_ID;
		// }
		try
		{
			loSqlObject.setQueryString(lsSQLQuery);

			// Executes the search for fetching custom object.
			SearchScope loSearchScope = new SearchScope(aoObjStore);
			IndependentObjectSet loSet = loSearchScope.fetchObjects(loSqlObject, null, null, Boolean.TRUE);

			// Iterating the result set
			Iterator loIt = loSet.iterator();
			while (loIt.hasNext())
			{

				CustomObject loObj = (CustomObject) loIt.next();
				String lsProviderId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID);
				String lsAgencyId = loObj.getProperties().getStringValue(P8Constants.PROPERTY_CE_SHARED_AGENCY_ID);
				// adding provider id to array list
				if (null != lsProviderId)
				{
					loArrProviderId.add( lsProviderId);
				}
				else if (null != lsAgencyId)
				{
					loArrProviderId.add( lsAgencyId);
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error fetching provider id for a document :", aoEx);
			loAppex.addContextData(HHSConstants.BULK_UPLOAD_DOC_ID, asDocId);
			LOG_OBJECT.Error("Error fetching provider id for a document :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited P8ContentOperations.getProviderId()");
		return loArrProviderId;
	}
	/*End R9.3.2 QC9665 **/

	
	
}
