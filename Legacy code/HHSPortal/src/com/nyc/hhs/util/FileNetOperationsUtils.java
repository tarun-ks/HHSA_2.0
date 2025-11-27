package com.nyc.hhs.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.jdom.Element;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ApplicationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.grid.SortComparator;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.JsonTreeBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.model.WorkItemInbox;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ContentOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

import filenet.vw.api.VWSession;

/**
 * This class is added for Release 5 This utility class exposes document
 * handling operations such as getting document list,uploading documents etc.
 * which are being used by multiple modules like business application, document
 * vault, organization information etc.
 * 
 */

public class FileNetOperationsUtils extends P8HelperServices {
	private static final LogInfo LOG_OBJECT = new LogInfo(
			FileNetOperationsUtils.class);
	private static final String ENVIRONMENT = System
			.getProperty(HHSConstants.HHS_ENV);

	/**
	 * This method will get a list of document types from XML DOM object
	 * 
	 * @param aoDocTypeXML
	 *            a doc type XML object
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asOrgId
	 *            a string value of organization Id
	 * @param asRequestingOrg
	 *            a string value of requesting Organization
	 * @return a list of document types for input document category
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getDoctypesFromXML(Object aoDocTypeXML, String asDocCategory, String asOrgId, String asRequestingOrg)
			throws ApplicationException {
		HashMap<String, String> loReqExceProp = new HashMap<String, String>();
		List<String> loDoctypesList = new ArrayList<String>();
		loReqExceProp.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOC_CATEGORY, asDocCategory);
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.saveFolderProperties()");
		if (aoDocTypeXML == null
				|| asDocCategory.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
				|| aoDocTypeXML.toString().equals(HHSR5Constants.EMPTY_STRING)) {

			ApplicationException loAppex = new ApplicationException(
					"Error in getDoctypesFromXML Method. Required Parameters are missing");
			loAppex.setContextData(loReqExceProp);
			throw loAppex;
		}
		loReqExceProp.put(HHSConstants.AO_DOC_TYPE_XML, aoDocTypeXML.toString());
		loReqExceProp.put(HHSConstants.AO_DOC_TYPE_DOM, aoDocTypeXML.toString());
		try {
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;

			// returning document category node from DocType.xml File
			Element loElt = XMLUtil.getElement("//"
					+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\""
					+ asOrgId + "\"] //"
					+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
					+ asDocCategory.trim() + "\"]", loDocTypeXML);
			if (null != loElt) {
				List<Element> loChildrenElementList = loElt
						.getChildren(P8Constants.XML_DOC_TYPE_NODE);
				for (int liChildCount = 0; liChildCount < loChildrenElementList
						.size(); liChildCount++) {
					if (null != asRequestingOrg
							&& asRequestingOrg
									.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
						// CHAR 500 - Extension and CHAR500 - 2nd Extension
						// Document check added for release 3.10.0 enhancement
						// 6572
						if ((null == loChildrenElementList.get(liChildCount)
								.getAttributeValue(
										HHSR5Constants.IS_SAMPLE_TYPE) || loChildrenElementList
								.get(liChildCount)
								.getAttributeValue(
										HHSR5Constants.IS_SAMPLE_TYPE)
								.equalsIgnoreCase(HHSConstants.STRING_TRUE))
								&& !(loChildrenElementList.get(liChildCount)
										.getAttributeValue(HHSConstants.NAME)
										.equalsIgnoreCase(HHSR5Constants.CHAR_500_EXTENSION))
								&& !(loChildrenElementList.get(liChildCount)
										.getAttributeValue(HHSConstants.NAME)
										.equalsIgnoreCase(HHSR5Constants.CHAR_500_2ND_EXTENSION))) {
							String lsElementText = loChildrenElementList.get(
									liChildCount).getAttributeValue(
									HHSConstants.NAME);
							loDoctypesList.add(lsElementText);
						}
					} else {
						if ((null == loChildrenElementList.get(liChildCount)
								.getAttributeValue(
										HHSR5Constants.IS_SAMPLE_TYPE) || !loChildrenElementList
								.get(liChildCount)
								.getAttributeValue(
										HHSR5Constants.IS_SAMPLE_TYPE)
								.equalsIgnoreCase(HHSConstants.STRING_TRUE))
								&& !HHSConstants.FALSE
										.equalsIgnoreCase(loChildrenElementList
												.get(liChildCount)
												.getAttributeValue(
														HHSR5Constants.DISPLAY_IN_APP))) {
							String lsElementText = loChildrenElementList.get(
									liChildCount).getAttributeValue(
									HHSConstants.NAME);
							loDoctypesList.add(lsElementText);
						}
					}
				}
			} else {
				ApplicationException loAppex = new ApplicationException(
						"Error in getDoctypesFromXML Method Element object is null");
				loAppex.setContextData(loReqExceProp);
				throw loAppex;
			}
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Error in getDoctypesFromXML Method.", aoEx);
			LOG_OBJECT.Error("Error in getDoctypesFromXML Method.", aoEx);
			loAppex.setContextData(loReqExceProp);
			throw loAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDoctypesFromXML()");
		Collections.sort(loDoctypesList);
		return loDoctypesList;
	}

	/**
	 * This method will get a list of document categories from XML DOM object
	 * 
	 * @param aoDocTypeXML
	 *            a doc type XML object
	 * @param asDocTypes
	 *            a string value of document type
	 * @param asOrgId
	 *            a string value of organization Id
	 * @return a list of document categories for input document type
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static List<String> getDocCategoryFromXML(Object aoDocTypeXML, String asDocTypes, String asOrgId) 
			throws ApplicationException 
	{
		//LOG_OBJECT.Debug("Entered FileNetOperationsUtils.getDocCategoryFromXML() with parameters::"	);
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocCategoryFromXML() with parameters::"	);
		LOG_OBJECT.Info("aoDocTypeXML :: "+aoDocTypeXML);
		LOG_OBJECT.Info("aoDocTypeXML :: "+aoDocTypeXML.getClass());
		LOG_OBJECT.Info("aoDocTypeXML.toString() :: "+aoDocTypeXML.toString());
		LOG_OBJECT.Info("asDocTypes :: "+asDocTypes);
		
		HashMap<String, String> loHMReqExceProp = new HashMap<String, String>();
		List<String> loDocCategoryList = new ArrayList<String>();
		loHMReqExceProp.put(HHSConstants.AS_DOC_TYPE, asDocTypes);
		if (null == aoDocTypeXML
				|| aoDocTypeXML.toString().equalsIgnoreCase(HHSR5Constants.EMPTY_STRING) 
				|| asOrgId == null
				|| asOrgId.trim().equals(HHSR5Constants.EMPTY_STRING)) 
		{
			ApplicationException loAppex = new ApplicationException("Error in getDocCategoryFromXML Method. Required Parameters are missing");
			loHMReqExceProp.put(HHSConstants.ORGANIZATION_ID, asOrgId);
			loAppex.setContextData(loHMReqExceProp);
			throw loAppex;
		}
		
		loHMReqExceProp.put(HHSConstants.AO_DOC_TYPE_XML,	aoDocTypeXML.toString());
		
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocumentContent() with parameters::"	+ loHMReqExceProp.toString());
		String lsDocCategory = null;
		try {
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;
			if (null != asDocTypes	&& !asDocTypes.trim().equals(HHSR5Constants.EMPTY_STRING)) {
				List<Element> loElementsList = XMLUtil.getElementList("//"
						+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\""
						+ asOrgId + "\"] //"
						+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
						+ asDocTypes + "\"]", loDocTypeXML);
				if (null != loElementsList && !loElementsList.isEmpty()) {
					for (int liCount = 0; liCount < loElementsList.size(); liCount++) {
						Element loParentElement = loElementsList.get(liCount)
								.getParentElement();
						lsDocCategory = loParentElement
								.getAttributeValue(HHSConstants.NAME);
						if (!HHSConstants.FALSE.equalsIgnoreCase(loElementsList
								.get(liCount).getAttributeValue(
										HHSR5Constants.DISPLAY_IN_APP))) {
							loDocCategoryList.add(lsDocCategory);
						}
					}
				} else {
					ApplicationException loAppex = new ApplicationException(
							"Please Select a valid Document Type");
					loAppex.setContextData(loHMReqExceProp);
					throw loAppex;
				}
			} else {
				List<Element> loElementsList = XMLUtil.getElementList("/"
						+ P8Constants.ROOT_NODE + "/"
						+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\""
						+ asOrgId + "\"]/"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY, loDocTypeXML);
				for (int liCount = 0; liCount < loElementsList.size(); liCount++) {
					lsDocCategory = loElementsList.get(liCount)
							.getAttributeValue(HHSConstants.NAME);
					if (!HHSConstants.FALSE.equalsIgnoreCase(loElementsList
							.get(liCount).getAttributeValue(
									HHSR5Constants.DISPLAY_IN_APP))) {
						loDocCategoryList.add(lsDocCategory);
					}
				}
			}
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocCategoryFromXML Method", aoEx);
			loAppex.setContextData(loHMReqExceProp);
			LOG_OBJECT.Error("Error in getDoctypesFromXML Method.", aoEx);
			throw loAppex;
		}
		Collections.sort(loDocCategoryList);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocCategoryFromXML()");
		return loDocCategoryList;
	}

	/**
	 * This method will get a list of document properties from XML DOM object
	 * 
	 * @param aoDocTypeXML
	 *            a doc type XML object
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asDocType
	 *            a string value of document type
	 * @param asOrgId
	 *            a string value of organization Id
	 * @return a list of document properties for input document category and
	 *         document type
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getDocPropertiesFromXML(Object aoDocTypeXML,
			String asDocCategory, String asDocType, String asOrgId)
			throws ApplicationException {
		LOG_OBJECT.Info("===getDocPropertiesFromXML param===="); 
		LOG_OBJECT.Info("=== param :: aoDocTypeXML :; "+aoDocTypeXML.toString());
		LOG_OBJECT.Info("=== param :: asDocCategory :; "+asDocCategory);
		LOG_OBJECT.Info("=== param :: asDocType :; "+asDocType);
		LOG_OBJECT.Info("=== param :: asOrgId :; "+asOrgId);
		List loRequiredDocPropsList = new ArrayList();
		HashMap<String, String> loReqExcePropHM = new HashMap<String, String>();
		loReqExcePropHM
				.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOC_CATEGORY,
						asDocCategory);
		loReqExcePropHM.put(HHSConstants.AS_DOC_TYPE, asDocType);
		loReqExcePropHM.put(HHSConstants.AS_ORGANIZATION_ID, asOrgId);
		DocumentPropertiesBean loDocPropObject = null;
		Element loElement = null;
		if (null == aoDocTypeXML || null == asOrgId
				|| asOrgId.trim().equals(HHSR5Constants.EMPTY_STRING)) {
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocPropertiesFromXML Method. Required Parameters are missing");
			loAppex.setContextData(loReqExcePropHM);
			throw loAppex;
		}
		loReqExcePropHM.put(HHSConstants.AO_DOC_TYPE_XML,
				aoDocTypeXML.toString());
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocPropertiesFromXML() with parameters::"
						+ loReqExcePropHM.toString());
		try {
			LOG_OBJECT.Info("===create loDocTypeXML");
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;
			if (null != asDocType
					&& !asDocType.trim().equals(HHSR5Constants.EMPTY_STRING)
					&& null != asDocCategory
					&& !asDocCategory.trim()
							.equals(HHSR5Constants.EMPTY_STRING)) {
				loElement = XMLUtil.getElement("//"
						+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\""
						+ asOrgId + "\"]" + "//"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
						+ asDocCategory + "\"]" + "//"
						+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
						+ asDocType + "\"]", loDocTypeXML);
				
				if (null == loElement) 
				{
					LOG_OBJECT.Info("===loElement is null");
					if (asDocCategory
							.equalsIgnoreCase(ApplicationConstants.DOC_SAMPLE)) {
						loElement = XMLUtil.getElement("//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\"" + asOrgId + "\"]" + "//"
								+ P8Constants.XML_DOC_CATEGORY_PROPERTY
								+ "[@name=\"" + ApplicationConstants.DOC_SAMPLE
								+ "\"]" + "//"
								+ P8Constants.XML_DOC_TYPE_PROPERTY
								+ "[/sampledoccategory=\"" + asDocCategory
								+ "\" and /sampledoctype=\"" + asDocType
								+ "\"]", loDocTypeXML);
					}
					if (!asDocCategory
							.equalsIgnoreCase(ApplicationConstants.DOC_SAMPLE)) {
						loElement = XMLUtil.getElement("//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\"" + asOrgId + "\"]" + "//"
								+ P8Constants.XML_DOC_CATEGORY_PROPERTY
								+ "[@name=\"" + asDocCategory + "\"]" + "//"
								+ P8Constants.XML_DOC_TYPE_PROPERTY
								+ "[/originalDocType=\"" + asDocType + "\"]",
								loDocTypeXML);
					} else {
						ApplicationException loAppex = new ApplicationException(
								"Not able to find document properties for selected Document type");
						throw loAppex;
					}

				}
				if (null != loElement) 
				{   LOG_OBJECT.Info("===loElement has value===");
					Element loPropertiesElement = loElement.getChild(HHSR5Constants.PROPERTIES);
					List<Element> loChildPropertiesElements = loPropertiesElement.getChildren();
					LOG_OBJECT.Info("===loPropertiesElement :: "+loPropertiesElement.toString());
					LOG_OBJECT.Info("===loChildPropertiesElements :: "+loChildPropertiesElements.toString());
					for (int liChildCount = 0; liChildCount < loChildPropertiesElements.size(); liChildCount++) 
					{
						loDocPropObject = new DocumentPropertiesBean();
						loDocPropObject
								.setPropDisplayName(loChildPropertiesElements
										.get(liChildCount)
										.getAttributeValue(
												P8Constants.XML_DOC_PROP_DISPLAY_NAME));
						loDocPropObject
								.setPropSymbolicName(loChildPropertiesElements
										.get(liChildCount)
										.getAttributeValue(
												P8Constants.XML_DOC_PROP_SYMBOLIC_NAME));
						loDocPropObject.setPropertyId(loChildPropertiesElements
								.get(liChildCount).getAttributeValue(
										P8Constants.XML_DOC_PROP_ID));
						loDocPropObject
								.setPropertyType(loChildPropertiesElements.get(
										liChildCount).getAttributeValue(
										P8Constants.XML_DOC_PROP_TYPE));
						loDocPropObject.setIsdisabled(Boolean
								.valueOf(loChildPropertiesElements.get(
										liChildCount).getAttributeValue(
										P8Constants.XML_DOC_PROP_IS_DISABLED)));
						loDocPropObject.setIsdropdown(Boolean
								.valueOf(loChildPropertiesElements.get(
										liChildCount).getAttributeValue(
										P8Constants.XML_DOC_PROP_IS_DROPDOWN)));
						loRequiredDocPropsList.add(loDocPropObject);

					}
				} else {
					ApplicationException loAppex = new ApplicationException(
							"Not able to find document properties for selected Document type");
					loAppex.setContextData(loReqExcePropHM);
					throw loAppex;
				}
			} else {
				ApplicationException loAppex = new ApplicationException(
						"Error in getDocPropertiesFromXML Method. Required Parameters are missing");
				loAppex.setContextData(loReqExcePropHM);
				throw loAppex;
			}
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Not able to find document properties for selected Document type",
					aoEx);
			loAppex.setContextData(loReqExcePropHM);
			LOG_OBJECT.Error("Error in getDocPropertiesFromXML Method.", aoEx);
			throw loAppex;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocPropertiesFromXML()");
		return loRequiredDocPropsList;
	}

	/**
	 * This method will get document properties based on document ID
	 * <ul>
	 * <li>
	 * This method executed the transaction 'displayDocProp_filenet'</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asUserOrgType
	 *            a string value of Organization type
	 * @param asDocumentId
	 *            a string value of document Id
	 * @return a document object containing document properties
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Document viewDocumentInfo(P8UserSession aoUserSession,
			String asUserOrgType, String asDocumentId, String asDocType,
			String asUserOrg, String asDocCategory, String asJspName)
			throws ApplicationException {
		HashMap loReqProps = new HashMap();
		String lsDocType = asDocType;
		Document loDocument = new Document();
		List<DocumentPropertiesBean> loPropBeanList = new ArrayList<DocumentPropertiesBean>();
		loReqProps.put(HHSR5Constants.USER_ORG_ID, asUserOrg);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.EMPTY_STRING);
		// loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE,
		// HHSR5Constants.EMPTY_STRING);
		// Updated in release 4.0.1- for removing mismatch in modified date
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				HHSR5Constants.EMPTY_STRING);
		// Updated in release 4.0.1- for removing mismatch in modified date end
		loReqProps.put(P8Constants.PROPERTY_CE_FILE_TYPE,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(HHSR5Constants.PARENT_PATH, HHSR5Constants.EMPTY_STRING);
		// Added for Release 5
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY,
				HHSR5Constants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED,
				HHSR5Constants.EMPTY_STRING);
		loReqProps
				.put(HHSR5Constants.DELETED_DATE, HHSR5Constants.EMPTY_STRING);
		loReqProps.put(HHSR5Constants.FILENET_DELETED_BY,
				HHSR5Constants.EMPTY_STRING);
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.viewDocumentInfo()");
		if (null != asDocCategory && !asDocCategory.isEmpty()
				&& !asDocCategory.equalsIgnoreCase(HHSR5Constants.NULL)) {
			loPropBeanList = getDocumentProperties(asDocCategory, lsDocType,
					asUserOrgType);
			for (Iterator iterator = loPropBeanList.iterator(); iterator
					.hasNext();) {
				DocumentPropertiesBean loDocumentPropertiesBean = (DocumentPropertiesBean) iterator
						.next();
				loReqProps.put(loDocumentPropertiesBean.getPropSymbolicName(),
						HHSR5Constants.EMPTY_STRING);
			}
		}
		List loDocIdList = new ArrayList();
		loDocIdList.add(asDocumentId);
		Channel loChannel = new Channel();
		if (null != asJspName && !asJspName.isEmpty()
				&& !asJspName.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
			loChannel.setData("jsp", asJspName);
		}
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData("entityId", asDocumentId);
		loChannel.setData("entityExistCheck", true);
		loChannel.setData(HHSConstants.AO_DOC_ID_LIST, loDocIdList);
		if (null == lsDocType || lsDocType.equals(HHSConstants.NULL)) {
			loReqProps.put(HHSR5Constants.FOLDER_NAME,
					HHSR5Constants.EMPTY_STRING);
			loReqProps.put(HHSR5Constants.FOLDER_PATH,
					HHSR5Constants.EMPTY_STRING);
			loReqProps.put(HHSR5Constants.FOLDER_COUNT,
					HHSR5Constants.EMPTY_STRING);
			loChannel.setData("entityType", HHSR5Constants.EMPTY_STRING);
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
					loReqProps);
			TransactionManager.executeTransaction(loChannel,
					"displayFolderProp_filenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

		} else {
			loReqProps.put(HHSR5Constants.FOLDERS_FILED_IN,
					HHSR5Constants.EMPTY_STRING);
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
					loReqProps);
			loChannel.setData("entityType", asDocType);
			TransactionManager.executeTransaction(loChannel,
					"displayDocProp_filenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}

		HashMap loHmDocProps = (HashMap) loChannel
				.getData(HHSConstants.DOCUMENT_PROPERTY_HASH_MAP);
		loDocument.setUserOrg((String) ((HashMap) loHmDocProps
				.get(asDocumentId)).get(P8Constants.PROPERTY_CE_PROVIDER_ID));
		getDocumentBeanObjectForId(loDocument, asDocumentId, asUserOrgType,
				loHmDocProps);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.viewDocumentInfo()");
		return loDocument;
	}

	/**
	 * This method will generate a document bean object from map containing
	 * document properties
	 * 
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param asUserOrg
	 *            a string value of User Organization
	 * @param aoPropsMap
	 *            a map containing document required properties
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	private static void getDocumentBeanObjectForId(Document aoDocument,
			String asDocumentId, String asUserOrg, HashMap aoPropsMap)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocumentBeanObjectForId()");
		if (null != asDocumentId) {
			HashMap loDocProps = (HashMap) aoPropsMap.get(asDocumentId);
			if (null != loDocProps
					&& null != loDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE)
					&& !loDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE)
							.equals(HHSConstants.NULL)
					&& !loDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE)
							.equals(HHSR5Constants.EMPTY_STRING)) {
				getDocumentObjectBean(aoDocument, asDocumentId, asUserOrg,
						loDocProps);
			} else {
				getFolderObjectBean(aoDocument, asDocumentId, loDocProps);

			}

		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocumentBeanObjectForId()");
	}

	/**
	 * The method will set the properties of document object
	 * 
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param aoDocProps
	 *            a map containing document required properties
	 * @throws ApplicationException
	 */
	private static void getFolderObjectBean(Document aoDocument,
			String asDocumentId, HashMap aoDocProps)
			throws ApplicationException {
		aoDocument.setDocumentId(asDocumentId);
		aoDocument.setFolderCount((Integer) (aoDocProps
				.get(HHSR5Constants.FOLDER_COUNT)));
		aoDocument.setDocName((String) aoDocProps
				.get(HHSR5Constants.FOLDER_NAME));

		aoDocument.setCreatedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
		aoDocument.setCreatedDate(DateUtil.getDateByFormat(
				HHSConstants.NFCTH_DATE_FORMAT,
				HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
				(String) aoDocProps.get(P8Constants.PROPERTY_CE_DATE_CREATED)));
		// changes in date method R5
		aoDocument.setDate((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE));
		// changes in date method R5
		aoDocument.setLastModifiedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getFolderObjectBean()");
		if (null != aoDocProps.get(HHSR5Constants.FILENET_DELETED_BY)) {

			aoDocument.setDeletedBy((String) aoDocProps
					.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
			aoDocument.setDeletedDate((String) aoDocProps
					.get(HHSR5Constants.DELETED_DATE));

		}
		if (null != aoDocProps.get(HHSR5Constants.FOLDER_PATH)) {
			String lsPath = (String) aoDocProps.get(HHSR5Constants.FOLDER_PATH);
			aoDocument.setFolderLocation(getFolderLocation(lsPath));
		}
		if (null != aoDocProps.get(HHSR5Constants.FILENET_MOVE_FROM)) {
			String lsPath = (String) aoDocProps
					.get(HHSR5Constants.FILENET_MOVE_FROM);
			aoDocument.setMoveFromPath(getFolderLocation(lsPath));
		}
		// sharing list--Fix for Defect # 7493 end
		if (null != aoDocProps
				&& null != aoDocProps.get(HHSR5Constants.SHARE_LIST)
				&& !aoDocProps.get(HHSR5Constants.SHARE_LIST).equals(
						HHSConstants.NULL)
				&& !aoDocProps.get(HHSR5Constants.SHARE_LIST).equals(
						HHSR5Constants.EMPTY_STRING)) {
			aoDocument.setSharingOrgName((String) aoDocProps
					.get(HHSR5Constants.SHARE_LIST));
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getFolderObjectBean()");
		// Fix for Defect # 7493 end
	}

	/**
	 * The method will set the properties of document object
	 * 
	 * @param aoDocument
	 *            a document object containing folder properties
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param aoDocProps
	 *            a map containing folder required properties
	 * @throws ApplicationException
	 */
	private static void getDocumentObjectBean(Document aoDocument,
			String asDocumentId, String asUserOrg, HashMap aoDocProps)
			throws ApplicationException {
		String lsProviderName = (String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY);
		aoDocument.setDocumentId(asDocumentId);
		aoDocument.setDocCategory((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));

		aoDocument.setDocName((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
		// Added for Release 5- creator info
		String lsPath = (String) aoDocProps
				.get(HHSR5Constants.FOLDERS_FILED_IN);
		String lsDocType = (String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOC_TYPE);
		String lsOriginalDocType = getOriginalDocumentType(lsDocType);
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocumentObjectBean()");
		if (lsDocType.equalsIgnoreCase(lsOriginalDocType)) {
			aoDocument.setDocType(lsOriginalDocType);
		} else {
			aoDocument.setDocType(lsDocType);
		}
		if (null != lsPath && !lsPath.isEmpty()) {
			aoDocument.setFilePath(getDocLocation(lsPath));
		} else {
			aoDocument.setFilePath(HHSR5Constants.BACK_SLASHES_DV);
		}

		// end
		// sharing list
		if (null != aoDocProps
				&& null != aoDocProps.get(HHSR5Constants.SHARE_LIST)
				&& !aoDocProps.get(HHSR5Constants.SHARE_LIST).equals(
						HHSConstants.NULL)
				&& !aoDocProps.get(HHSR5Constants.SHARE_LIST).equals(
						HHSR5Constants.EMPTY_STRING)) {
			aoDocument.setSharingOrgName((String) aoDocProps
					.get(HHSR5Constants.SHARE_LIST));
		}
		// Updated in release 4.0.1- for removing mismatch in modified date
		aoDocument.setDate(DateUtil.getDateByFormat(
				HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
				HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
				aoDocProps.get(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE)
						.toString()));
		// Updated in release 4.0.1- for removing mismatch in modified date end
		aoDocument.setFileType((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_FILE_TYPE));
		aoDocument.setSampleCategory((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
		aoDocument.setSampleType((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
		aoDocument.setLastModifiedBy(lsProviderName);
		aoDocument.setCreatedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
		aoDocument.setCreatedDate(DateUtil
				.getDateByFormat(HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
						HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
						aoDocProps.get(P8Constants.PROPERTY_CE_DATE_CREATED)
								.toString()));
		if (!(aoDocument.getDocType().equalsIgnoreCase(
				ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT) || aoDocument
				.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A))) {
			aoDocument.setDocumentProperties(getDocumentProperties(
					aoDocument.getDocCategory(), aoDocument.getDocType(),
					asUserOrg));
		}
		if (null != aoDocProps.get(HHSR5Constants.FILENET_DELETED_BY)) {

			String lsParentPath = (String) aoDocProps
					.get(HHSR5Constants.PARENT_PATH);
			if (null != lsParentPath && !lsParentPath.isEmpty()) {
				aoDocument.setFilePath(getDocLocation(lsParentPath));
			} else {
				aoDocument.setFilePath(HHSR5Constants.BACK_SLASHES_DV);
			}
			aoDocument.setDeletedBy((String) aoDocProps
					.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));

			aoDocument.setDeletedDate(DateUtil.getDateByFormat(
					HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
					HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
					aoDocProps.get("DELETED_DATE").toString()));

		}
		List<DocumentPropertiesBean> loDocPropBean = aoDocument
				.getDocumentProperties();
		if (null != loDocPropBean) {
			for (Iterator iterator = loDocPropBean.iterator(); iterator
					.hasNext();) {
				DocumentPropertiesBean loDocumentPropertiesBean = (DocumentPropertiesBean) iterator
						.next();
				loDocumentPropertiesBean.setPropValue(aoDocProps
						.get(loDocumentPropertiesBean.getPropSymbolicName()));

			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocumentObjectBean()");
	}

	/**
	 * This method is added in release 5 , this is used to get the folder
	 * location , It will split the folder path and then will iterate through
	 * the array to get the folder name.
	 * 
	 * @param asPath
	 *            folder path
	 */
	private static String getFolderLocation(String asPath) {

		StringBuffer lsFolderPath = new StringBuffer();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getFolderLocation()");
		if (null != asPath
				&& asPath.indexOf(P8Constants.STRING_SINGLE_SLASH) >= 0) {
			String[] loTemp = asPath.split(P8Constants.STRING_SINGLE_SLASH);
			int liLength = loTemp.length;
			if (loTemp[1].equalsIgnoreCase(ApplicationConstants.CITY_TYPE)) {
				for (int i = 2; i < liLength - 1; i++) {
					lsFolderPath.append(loTemp[i]);
					lsFolderPath.append(HHSConstants.STRING_BACKSLASH);
				}
			} else {
				for (int i = 3; i < liLength - 1; i++) {
					lsFolderPath.append(loTemp[i]);
					lsFolderPath.append(HHSConstants.STRING_BACKSLASH);
				}
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getFolderObjectBean()");
		return lsFolderPath.toString();

	}

	/**
	 * This method is added in release 5 , this is used to get the File location
	 * , It will split the folder path and then will iterate through the array
	 * to get the folder name.
	 * 
	 * @param asPath
	 *            folder path
	 */
	private static String getDocLocation(String asPath) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocLocation()");
		StringBuffer lsFolderPath = new StringBuffer();
		if (null != asPath
				&& asPath.indexOf(P8Constants.STRING_SINGLE_SLASH) >= 0) {
			String[] loTemp = asPath.split(P8Constants.STRING_SINGLE_SLASH);
			int liLength = loTemp.length;
			if (loTemp[1].equalsIgnoreCase(ApplicationConstants.CITY_TYPE)) {
				for (int i = 2; i < liLength; i++) {
					lsFolderPath.append(loTemp[i]);
					lsFolderPath.append(HHSConstants.STRING_BACKSLASH);
				}
			} else {
				for (int i = 3; i < liLength; i++) {
					lsFolderPath.append(loTemp[i]);
					lsFolderPath.append(HHSConstants.STRING_BACKSLASH);
				}
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocLocation()");
		return lsFolderPath.toString();
	}

	/**
	 * This method will get list of document properties based on document
	 * category and document type
	 * 
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asDocType
	 *            a string value of document type
	 * @param asUserOrg
	 *            a string value of document user organization
	 * @return a list of document properties bean
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public static List<DocumentPropertiesBean> getDocumentProperties(
			String asDocCategory, String asDocType, String asUserOrg)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocumentProperties()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocumentProperties()");
		return getDocPropertiesFromXML(loXMLDoc, asDocCategory, asDocType,
				asUserOrg);
	}

	/**
	 * This method will get list of document object containing required
	 * properties information for each document object
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'displayDocList_filenet'</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 *            a channel object
	 * @param asDocType
	 *            a string value of document type
	 * @param aoRequiredProps
	 *            a map containing document required properties
	 * @param aoFilterProps
	 *            a map containing document filter properties
	 * @param abIncludeFilter
	 *            a boolean value indicating to include filter or not
	 * @return a list of HashMap containing document properties
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static List getDocumentList(Channel aoChannel, String asDocType,
			HashMap aoRequiredProps, HashMap aoFilterProps,
			boolean abIncludeFilter, String aoUserId)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocumentList()");
		aoChannel.setData(ApplicationConstants.DOC_TYPE, asDocType);
		aoRequiredProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		aoChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
				aoRequiredProps);
		aoChannel.setData(HHSConstants.FILENET_FILTER_MAP, aoFilterProps);
		aoChannel.setData(HHSConstants.INCLUDE_FILENET_FILTER, abIncludeFilter);
		aoChannel.setData(HHSR5Constants.Is_ARCHIVE, false);
		aoChannel.setData(HHSR5Constants.AO_USER_ID, aoUserId);

		TransactionManager
				.executeTransaction(aoChannel, "displayDocList_filenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocumentList()");
		return (List) aoChannel.getData("documentList");
	}

	/**
	 * This method will set order by parameters for document list
	 * 
	 * @param aoChannel
	 *            a channel object
	 * @param asSortBy
	 *            a string value of sort By
	 * @param asSortType
	 *            a string value of sort Type
	 */
	@SuppressWarnings("rawtypes")
	public static void setOrderByParameter(Channel aoChannel, String asSortBy,
			String asSortType) {
		boolean lbSearchingRecycleBin = false;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setOrderByParameter()");
		boolean lbFindOrgFlag;
		lbFindOrgFlag = (Boolean) aoChannel.getData("findOrgFlag");
		if (null != aoChannel.getData(HHSR5Constants.JSP_NAME)
				&& aoChannel.getData(HHSR5Constants.JSP_NAME).toString()
						.contains(HHSR5Constants.RECYCLE_BIN_ID)) {
			lbSearchingRecycleBin = true;
		}
		if (asSortBy != null
				&& !asSortBy.equalsIgnoreCase(P8Constants.STRING_SINGLE_SLASH)) {
			HashMap loHmOrdrBy = new HashMap();
			if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.PROPERTY_TYPE_DATE)) {
				if (lbFindOrgFlag) {
					loHmOrdrBy = requiredOrderByClauseForSharedSearch(
							P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
							asSortType, null);
				} else if (lbSearchingRecycleBin) {
					loHmOrdrBy = requiredOrderByClauseForRecycleBin(asSortBy,
							asSortType);
				} else {
					loHmOrdrBy = requiredOrderByClause(
							P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
							asSortType);
				}

			} else if (asSortBy.equalsIgnoreCase(ApplicationConstants.DOC_NAME)) {
				if (lbFindOrgFlag) {
					loHmOrdrBy = requiredOrderByClauseForSharedSearch(
							P8Constants.PROPERTY_CE_DOCUMENT_TITLE, asSortType,
							null);
				} else if (lbSearchingRecycleBin) {
					loHmOrdrBy = requiredOrderByClauseForRecycleBin(asSortBy,
							asSortType);
				} else {
					loHmOrdrBy = requiredOrderByClause(
							P8Constants.PROPERTY_CE_DOCUMENT_TITLE, asSortType);
				}
			}

			else if (asSortBy
					.equalsIgnoreCase(HHSR5Constants.ORGANIZATION_NAME)) {
				loHmOrdrBy = requiredOrderByClauseForSharedSearch(
						"ORG_LEGAL_NAME", asSortType, null);

			}

			else if (asSortBy.equalsIgnoreCase(ApplicationConstants.DOC_TYPE)) {
				if (lbFindOrgFlag) {
					loHmOrdrBy = requiredOrderByClauseForSharedSearch(
							P8Constants.PROPERTY_CE_DOC_TYPE, asSortType, null);
				} else if (lbSearchingRecycleBin) {
					loHmOrdrBy = requiredOrderByClauseForRecycleBin(asSortBy,
							asSortType);
				} else {
					loHmOrdrBy = requiredOrderByClause(
							P8Constants.PROPERTY_CE_DOC_TYPE, asSortType);
				}
			} else if (asSortBy.equalsIgnoreCase(HHSR5Constants.LINK_STATUS)) {
				loHmOrdrBy = requiredOrderByClause(
						P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
						asSortType);
			} else if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.DOC_CATEGORY)) {
				loHmOrdrBy = requiredOrderByClause(
						P8Constants.PROPERTY_CE_DOC_CATEGORY, asSortType);
			} else if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.LAST_MODIFIED_BY)) {
				loHmOrdrBy = requiredOrderByClause(
						P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, asSortType);
			} else if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.SHARE_DOCUMENT_STATUS)) {
				loHmOrdrBy = requiredOrderByClause(HHSR5Constants.SHARING_FLAG,
						asSortType);
			} else if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.SAMPLE_CATEGORY)) {
				loHmOrdrBy = requiredOrderByClause(
						P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, asSortType);
			} else if (asSortBy
					.equalsIgnoreCase(ApplicationConstants.SAMPLE_TYPE)) {
				loHmOrdrBy = requiredOrderByClause(
						P8Constants.PROPERTY_CE_SAMPLE_TYPE, asSortType);
			}

			aoChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP, loHmOrdrBy);
		} else {
			if (lbFindOrgFlag) {
				aoChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP,
						requiredOrderByClauseForSharedSearch(null, null, null));
				aoChannel.setData("defaultFlag", true);
			} else if (lbSearchingRecycleBin) {
				aoChannel
						.setData(
								HHSConstants.FILENET_ORDER_BY_MAP,
								requiredOrderByClauseForRecycleBin(asSortBy,
										asSortType));
			} else {
				aoChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP,
						requiredOrderByClause(null, null));
			}

		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setOrderByParameter()");
	}

	/**
	 * The method will set the order by clause for shared search.
	 * 
	 * @param asRequiredKey
	 *            a string value of required key
	 * @param asSortType
	 *            a string value of sort type
	 * @param asSharingFlag
	 *            a boolean value
	 * @return hashmap containing orderBy clause
	 */
	private static HashMap<Integer, String> requiredOrderByClauseForSharedSearch(
			String asRequiredKey, String asSortType, String asSharingFlag) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredOrderByClauseForSharedSearch()");
		int liStartOrder = 2;
		HashMap<Integer, String> loRequiredOrderByMap = new HashMap<Integer, String>();
		if (null != asRequiredKey && null != asSortType) {

			if (asRequiredKey.equalsIgnoreCase("ORG_LEGAL_NAME")) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + ",sud");
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + ",sud1");
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
								+ " DESC " + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
								+ " DESC " + HHSR5Constants.COMMA_WITH_FO);
			}

			else if (asRequiredKey
					.equalsIgnoreCase(HHSR5Constants.DOCUMENT_TITLE)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " " + asSortType
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
								+ " DESC " + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
								+ " DESC " + HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
			} else if (asRequiredKey
					.equalsIgnoreCase(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, "ORG_LEGAL_NAME"
						+ " ASC " + ",sud");
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, "ORG_LEGAL_NAME"
						+ " ASC " + ",sud1");
				liStartOrder++;
			}
		} else {
			loRequiredOrderByMap.put(liStartOrder, "ORG_LEGAL_NAME" + " ASC "
					+ ",sud");
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder, "ORG_LEGAL_NAME" + " ASC "
					+ ",sud1");
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " DESC "
							+ HHSR5Constants.DOC_WITH_COMMA);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " DESC "
							+ HHSR5Constants.COMMA_WITH_FO);
			liStartOrder++;
		}
		loRequiredOrderByMap.put(1, HHSR5Constants.OBJECT_TYPE + " ASC "
				+ HHSR5Constants.COMMA_WITH_FO);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredOrderByClauseForSharedSearch()");
		return loRequiredOrderByMap;

	}

	/**
	 * This method will generate order by clause for query
	 * 
	 * @param asRequiredKey
	 *            a string value of required key
	 * @param asSortType
	 *            a string value of sort Type
	 * @return a list of Hashmap containing required properties to be fetched
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap requiredOrderByClauseForRecycleBin(
			String asRequiredKey, String asSortType) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredOrderByClauseForRecycleBin()");
		int liStartOrder = 2;
		List loReqOdrByParamList = new ArrayList();
		HashMap loRequiredOrderByMap = new HashMap();
		if (asRequiredKey != null && asSortType != null) {
			if (asRequiredKey.equalsIgnoreCase("date")) {
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DELETED_DATE + " " + asSortType
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DELETED_DATE + " " + asSortType
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;

			} else if (asRequiredKey.equalsIgnoreCase("docName")) {
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " " + asSortType
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " " + asSortType
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DELETED_DATE + " "
								+ HHSConstants.DESCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DELETED_DATE + " "
								+ HHSConstants.DESCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
			} else if (asRequiredKey.equalsIgnoreCase("docType")) {
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_DOC_TYPE + " " + asSortType
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_DOC_TYPE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
			}
		} else {
			loRequiredOrderByMap.put(liStartOrder,
					HHSR5Constants.FILENET_FOLDER_NAME + " "
							+ HHSConstants.ASCENDING
							+ HHSR5Constants.COMMA_WITH_FO);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					HHSR5Constants.DOCUMENT_TITLE + " "
							+ HHSConstants.ASCENDING
							+ HHSR5Constants.DOC_WITH_COMMA);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder, HHSR5Constants.DELETED_DATE
					+ " " + HHSConstants.DESCENDING
					+ HHSR5Constants.COMMA_WITH_FO);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder, HHSR5Constants.DELETED_DATE
					+ " " + HHSConstants.DESCENDING
					+ HHSR5Constants.DOC_WITH_COMMA);
			liStartOrder++;

		}
		loRequiredOrderByMap.put(1, HHSR5Constants.OBJECT_TYPE + " ASC "
				+ HHSR5Constants.COMMA_WITH_FO);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredOrderByClauseForRecycleBin()");
		return loRequiredOrderByMap;
	}

	/**
	 * This method will generate order by clause for query
	 * 
	 * @param asRequiredKey
	 *            a string value of required key
	 * @param asSortType
	 *            a string value of sort Type
	 * @return a list of Hashmap containing required properties to be fetched
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap requiredOrderByClause(String asRequiredKey,
			String asSortType) {

		int liStartOrder = 2;
		List loReqOdrByParamList = new ArrayList();
		HashMap loRequiredOrderByMap = new HashMap();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredOrderByClause()");
		if (asRequiredKey != null && asSortType != null) {
			if (asRequiredKey
					.equalsIgnoreCase(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;

			} else if (asRequiredKey
					.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOCUMENT_TITLE)) {
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " " + asSortType
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " "
								+ HHSConstants.DESCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " "
								+ HHSConstants.DESCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
			} else if (asRequiredKey
					.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;

			} else if (asRequiredKey
					.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
			} else if (asRequiredKey
					.equalsIgnoreCase(HHSR5Constants.SHARING_FLAG)) {
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder, asRequiredKey + " "
						+ asSortType + HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.FILENET_FOLDER_NAME + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.COMMA_WITH_FO);
				liStartOrder++;
				loRequiredOrderByMap.put(liStartOrder,
						HHSR5Constants.DOCUMENT_TITLE + " "
								+ HHSConstants.ASCENDING
								+ HHSR5Constants.DOC_WITH_COMMA);
				liStartOrder++;

			}
		} else {
			loRequiredOrderByMap.put(liStartOrder,
					HHSR5Constants.FILENET_FOLDER_NAME + " "
							+ HHSConstants.ASCENDING
							+ HHSR5Constants.COMMA_WITH_FO);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					HHSR5Constants.DOCUMENT_TITLE + " "
							+ HHSConstants.ASCENDING
							+ HHSR5Constants.DOC_WITH_COMMA);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " "
							+ HHSConstants.DESCENDING
							+ HHSR5Constants.COMMA_WITH_FO);
			liStartOrder++;
			loRequiredOrderByMap.put(liStartOrder,
					P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE + " "
							+ HHSConstants.DESCENDING
							+ HHSR5Constants.DOC_WITH_COMMA);
			liStartOrder++;

		}
		loRequiredOrderByMap.put(1, HHSR5Constants.OBJECT_TYPE + " ASC "
				+ HHSR5Constants.COMMA_WITH_FO);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredOrderByClause()");
		return loRequiredOrderByMap;
	}

	/**
	 * This is the method which will be used for uploading any document into
	 * Document Vault.
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'fileupload_filenet'</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 *            a channel object
	 * @param aoStreamUploadFile
	 *            an input stream for file upload
	 * @param aoPropertyMap
	 *            a map containing required properties
	 * @param aoDocExist
	 *            a boolean value indicating document already exist
	 * @param aoCheckExist
	 *            a boolean value indicating significance of doc Exist field
	 * @return a String of Document ID
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap<String, String> uploadDocument(Channel aoChannel,
			FileInputStream aoStreamUploadFile, HashMap aoPropertyMap,
			boolean aoDocExist, boolean aoCheckExist)
			throws ApplicationException {
		HashMap<String, String> loReturnMapData = new HashMap<String, String>();
		HashMap<String, Object> loReturnMap = new HashMap<String, Object>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.uploadDocument()");
		try {
			aoChannel.setData(P8Constants.IS, aoStreamUploadFile);
			aoChannel.setData(HHSConstants.BULK_UPLOAD_FILE_PROPS,
					aoPropertyMap);
			aoChannel.setData(HHSR5Constants.ORGANIZATION_TYPE,
					(String) aoPropertyMap
							.get(P8Constants.PROPERTY_CE_ORGANIZATION_ID));
			aoChannel.setData(HHSR5Constants.DOC_EXIST, aoDocExist);
			aoChannel.setData(HHSR5Constants.CHECK_EXIST, aoCheckExist);
			TransactionManager
					.executeTransaction(aoChannel, "fileupload_filenet",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loReturnMap = (HashMap<String, Object>) aoChannel
					.getData(HHSR5Constants.LO_RETURN_MAP);
			loReturnMapData.put(HHSConstants.DOC_ID,
					(String) loReturnMap.get(HHSConstants.DOC_ID));
			loReturnMapData.put(HHSR5Constants.PARENT_SHARING_FLAG,
					(String) loReturnMap
							.get(HHSR5Constants.PARENT_SHARING_FLAG));
			/* change start for defect : 7869 */
		} catch (ApplicationException loEx) {
			String lsMessage = loEx.getMessage();

			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
			LOG_OBJECT.Error(
					"Exception in FileNetOperationsUtils.createDVdocument()::",
					loEx);
			throw new ApplicationException(lsMessage);
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSConstants.FILE_UPLOAD_FAIL_MESSAGE), aoEx);
			LOG_OBJECT.Error("Error While creating document", aoEx);
			throw loAppex;
		}
		/* change end for defect : 7869 */
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.uploadDocument()");
		return loReturnMapData;
	}

	/**
	 * this method will get list of active providers from DB
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @return a list of Provider Bean
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<ProviderBean> getProviderList()
			throws ApplicationException {

		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getProviderList()");
		List<ProviderBean> loProviderList = null;
		loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.PROV_LIST);
		// R4 Homepage changes: fetching agency list from cache.
		BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.AGENCY_LIST);
		if (null == loProviderList) {
			loProviderList = getProviderList(true);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getProviderList()");
		return loProviderList;
	}

	/**
	 * this method will get list of active providers from DB
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'getProviderListAjaxCall_DB'</li>
	 * </ul>
	 * 
	 * @param boolean abFlag object
	 * @return List<ProviderBean> object
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<ProviderBean> getProviderList(boolean abFlag)
			throws ApplicationException {
		List<ProviderBean> loProviderList = new ArrayList<ProviderBean>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getProviderList()");
		Channel loChannel = new Channel();
		if (abFlag) {
			TransactionManager.executeTransaction(loChannel,
					"getProviderListAjaxCall_DB");
			List<ProviderBean> loProviderTempList = (List<ProviderBean>) loChannel
					.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
			if (!CollectionUtils.isEmpty(loProviderTempList)) {
				Iterator<ProviderBean> loIter = loProviderTempList.iterator();
				while (loIter.hasNext()) {
					ProviderBean loProviderBean = loIter.next();
					String lsDisplayValue = loProviderBean.getDisplayValue();
					lsDisplayValue = StringEscapeUtils
							.escapeJavaScript(lsDisplayValue);
					loProviderBean.setDisplayValue(lsDisplayValue);
					loProviderList.add(loProviderBean);
				}
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getProviderList()");
		return loProviderList;
	}

	/**
	 * This method will get provider name from provider list based on provider
	 * Id
	 * 
	 * @param aoProviderList
	 *            a list of Provider Bean
	 * @param asAgencyName
	 *            a string value of agency name
	 * @return a string value of provider name
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static String getProviderName(List<ProviderBean> aoProviderList,
			String asProviderName) throws ApplicationException {
		String lsAgencyId = HHSR5Constants.EMPTY_STRING;
		ProviderBean loProviderBean;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getProviderName()");
		for (int liCount = 0; liCount < aoProviderList.size(); liCount++) {
			loProviderBean = aoProviderList.get(liCount);
			String lsCurrValue = (String) loProviderBean.getHiddenValue();
			if (null != asProviderName
					&& asProviderName.equalsIgnoreCase(lsCurrValue)) {
				lsAgencyId = (String) loProviderBean.getDisplayValue();
				break;
			}
		}
		if (HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsAgencyId)) {
			lsAgencyId = asProviderName;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getProviderName()");
		return lsAgencyId;
	}

	/**
	 * This method will get provider Id from provider list based on provider
	 * Name
	 * 
	 * @param aoProviderList
	 *            a list of Provider Bean
	 * @param asAgencyName
	 *            a string value of agency name
	 * @return a string value of provider Id
	 */
	@SuppressWarnings("rawtypes")
	public static String getProviderId(List aoProviderList, String asAgencyName) {
		String lsProviderId = HHSR5Constants.EMPTY_STRING;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getProviderId()");
		if (null != aoProviderList) {
			for (int liProviderCount = 0; liProviderCount < aoProviderList
					.size(); liProviderCount++) {
				ProviderBean loBeanIterator = (ProviderBean) aoProviderList
						.get(liProviderCount);
				String lsCurrValue = (String) loBeanIterator.getDisplayValue();
				lsCurrValue = StringEscapeUtils.unescapeJavaScript(lsCurrValue);
				if (null != asAgencyName && asAgencyName.equals(lsCurrValue)) {
					lsProviderId = (String) loBeanIterator.getHiddenValue();
					break;
				}
			}
		}
		if (HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsProviderId)) {
			lsProviderId = asAgencyName;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getProviderId()");
		return lsProviderId;
	}

	/**
	 * This method will get agency Id from agency list based on agency Name
	 * 
	 * @param aoAgencyList
	 *            a list of Agencies
	 * @param asAgencyName
	 *            a string value of agency name
	 * @return a string value of agency Id
	 */
	@SuppressWarnings("rawtypes")
	public static String getAgencyId(TreeSet aoAgencyList, String asAgencyName) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getAgencyId()");
		String lsAgencyId = HHSR5Constants.EMPTY_STRING;
		for (Iterator loIterator = aoAgencyList.iterator(); loIterator
				.hasNext();) {
			String lsAgencyString = (String) loIterator.next();
			String lsAgencyName = lsAgencyString.substring(
					lsAgencyString.indexOf(ApplicationConstants.TILD) + 1,
					lsAgencyString.length());
			lsAgencyName = StringEscapeUtils.unescapeJavaScript(lsAgencyName);
			if (null != lsAgencyName
					&& lsAgencyName.equalsIgnoreCase(StringEscapeUtils
							.unescapeJavaScript(asAgencyName))) {
				lsAgencyId = lsAgencyString.substring(0,
						lsAgencyString.indexOf(ApplicationConstants.TILD));
				break;
			}
			// changes for getting agency name
			else if (null != asAgencyName
					&& lsAgencyName.contains(StringEscapeUtils
							.unescapeJavaScript(asAgencyName))) {

				lsAgencyId = lsAgencyName.substring(lsAgencyName
						.indexOf(HHSConstants.HYPHEN) + 1);

			}
			// changes for getting agency name
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getAgencyId()");
		return lsAgencyId;
	}

	/**
	 * This method will get agency Name from agency list based on agency Id
	 * 
	 * @param aoAgencyList
	 *            a list of Agencies
	 * @param asAgencyID
	 *            a string value of agency Id
	 * @return a string value of agency name
	 */
	@SuppressWarnings("rawtypes")
	public static String getAgencyName(TreeSet aoAgencyList, String asAgencyID) {
		String lsAgencyName = HHSR5Constants.EMPTY_STRING;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getAgencyName()");
		for (Iterator loIterator = aoAgencyList.iterator(); loIterator
				.hasNext();) {
			String lsAgencyString = (String) loIterator.next();
			if (null != lsAgencyString
					&& !lsAgencyString.equals(HHSR5Constants.EMPTY_STRING)) {
				String lsAgencyID = lsAgencyString.substring(0,
						lsAgencyString.indexOf(ApplicationConstants.TILD));
				if (null != lsAgencyID
						&& lsAgencyID.equalsIgnoreCase(asAgencyID)) {
					lsAgencyName = lsAgencyString
							.substring(lsAgencyString
									.indexOf(ApplicationConstants.TILD) + 1,
									lsAgencyString.length());
					break;
				}
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getAgencyName()");
		return lsAgencyName;
	}

	/**
	 * The method will give the agency name.
	 * 
	 * @param aoAgencyList
	 *            a list of Agency
	 * @param asAgencyID
	 *            as agencyId
	 * @return agency name
	 */
	public static String getAgencyNameForManage(TreeSet aoAgencyList,
			String asAgencyID) {
		String lsAgencyName = HHSR5Constants.EMPTY_STRING;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getAgencyNameForManage()");
		if (null != asAgencyID) {
			for (Iterator loIterator = aoAgencyList.iterator(); loIterator
					.hasNext();) {
				String lsAgencyString = (String) loIterator.next();
				if (null != lsAgencyString
						&& !lsAgencyString.equals(HHSR5Constants.EMPTY_STRING)) {
					String[] lsAgencyID = lsAgencyString
							.split(HHSConstants.HYPHEN);
					String[] loTemp1 = lsAgencyID[0]
							.split(ApplicationConstants.TILD);
					String[] loTemp2 = asAgencyID
							.split(ApplicationConstants.TILD);
					if (null != loTemp1[0]
							&& loTemp1[0].trim().equalsIgnoreCase(loTemp2[0])) {
						lsAgencyName = lsAgencyID[1];
						break;
					}
				}
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getAgencyNameForManage()");
		return lsAgencyName;
	}

	/**
	 * This method will get master Data from DB
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'getMasterData_DB'</li>
	 * </ul>
	 * 
	 * @param aoMethodName
	 *            a string value of method name
	 * @return a list of status from master table
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getMasterData(String aoMethodName)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getMasterData()");
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.AO_METHOD_NAME, aoMethodName);
		TransactionManager.executeTransaction(loChannel, "getMasterData_DB");
		List<String> loStatusList = new ArrayList<String>();
		loStatusList.add(0, HHSR5Constants.EMPTY_STRING);
		loStatusList.addAll((List<String>) loChannel
				.getData(HHSR5Constants.MASTER_LIST));
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getMasterData()");
		return loStatusList;
	}

	/**
	 * This method will generate Error messages if document size is zero
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @param aoSession
	 *            a PortletSession object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	public static void generateErrorMessageForEmptyFile(PortletSession aoSession)
			throws ApplicationException, IOException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.generateErrorMessageForEmptyFile()");
		aoSession.setAttribute(HHSConstants.TYPE, HHSConstants.CLC_CAP_ERROR,
				PortletSession.APPLICATION_SCOPE);
		String lsMessage = PropertyLoader.getProperty(
				P8Constants.ERROR_PROPERTY_FILE,
				ApplicationConstants.MESSAGE_M500);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage,
				PortletSession.PORTLET_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_FAIL_TYPE,
				PortletSession.PORTLET_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.generateErrorMessageForEmptyFile()");
		throw new ApplicationException(lsMessage);
	}

	/**
	 * This method will upload valid document after performing size check for
	 * document to be uploaded
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @param aoSession
	 *            a PortletSession object
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param abIsLinkedToApp
	 *            a boolean value indicating link to application
	 * @param aoMimepartsNewVersion
	 *            a MimeBodyPart object
	 * @param aoFilterHiddenMap
	 *            a map containing filter hidden properties
	 * @param aoFiledata
	 *            a MimeBodyPart object
	 * @param asFileType
	 *            a string value of file type
	 * @param asDocType
	 *            a string value of document type
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoChannel
	 *            a channel object
	 * @param aoGetContentMap
	 *            a map containing content information
	 * @param aoInputstream
	 *            an Input Stream for file upload
	 * @param asBfRealPath
	 *            a string value of real path
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 * @throws MessagingException
	 *             If an Messaging Exception occurs
	 * @throws FileNotFoundException
	 *             If an FileNotFound Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public static void uploadNonEmptyDocument(ActionRequest aoRequest,
			ActionResponse aoResponse, PortletSession aoSession,
			String asUserOrgType, P8UserSession aoUserSession,
			String asUserOrg, boolean abIsLinkedToApp,
			MimeBodyPart[] aoMimepartsNewVersion,
			HashMap<String, String> aoFilterHiddenMap, MimeBodyPart aoFiledata,
			String asFileType, String asDocType, Document aoDocument,
			Channel aoChannel, HashMap<String, String> aoGetContentMap,
			InputStream aoInputstream, StringBuffer asBfRealPath)
			throws ApplicationException, MessagingException, IOException,
			FileNotFoundException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.uploadNonEmptyDocument()");
		String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME
				+ ApplicationConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_CONTENT_SIZE_NAME;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.APPLICATION_SETTING);
		long llAllowedDocSize = Long.valueOf(loApplicationSettingMap
				.get(lsAppSettingMapKey));
		LOG_OBJECT.Debug("Uploaded File Size: " + aoFiledata.getSize());
		if (aoFiledata.getSize() <= llAllowedDocSize) {
			uploadValidSizeDocument(aoRequest, aoResponse, aoSession,
					asUserOrgType, aoUserSession, asUserOrg, abIsLinkedToApp,
					aoMimepartsNewVersion, aoFilterHiddenMap, aoFiledata,
					asFileType, asDocType, aoDocument, aoChannel,
					aoInputstream, asBfRealPath);
		} else {
			generateErrorMessageForBiggerSizeDocument(aoSession);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.uploadNonEmptyDocument()");
	}

	/**
	 * This method will generate Error messages if document size is greater than
	 * allowed limit
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @param aoSession
	 *            a PortletSession object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	public static void generateErrorMessageForBiggerSizeDocument(
			PortletSession aoSession) throws ApplicationException, IOException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.generateErrorMessageForBiggerSizeDocument()");
		aoSession.setAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				HHSConstants.CLC_CAP_ERROR, PortletSession.APPLICATION_SCOPE);
		String lsMessage = PropertyLoader.getProperty(
				P8Constants.ERROR_PROPERTY_FILE,
				ApplicationConstants.MESSAGE_M52);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage,
				PortletSession.PORTLET_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_FAIL_TYPE,
				PortletSession.PORTLET_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.generateErrorMessageForBiggerSizeDocument()");
		throw new ApplicationException(lsMessage);
	}

	/**
	 * This method will validate document for allowed size limit and then
	 * proceeds further
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @param aoSession
	 *            a PortletSession object
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param aoUserSession
	 *            a PortletSession object
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param abIsLinkedToApp
	 *            a boolean value indicating link to application
	 * @param aoMimepartsNewVersion
	 *            a MimeBodyPart object
	 * @param aoFilterHiddenMap
	 *            a map containing filter hidden properties
	 * @param aoFiledata
	 *            a MimeBodyPart object
	 * @param asFileType
	 *            a string value of file type
	 * @param asDocType
	 *            a string value of document type
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoChannel
	 *            a channel object
	 * @param aoInputstream
	 *            an Input Stream for file upload
	 * @param asBfRealPath
	 *            a string value of real path
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 * @throws MessagingException
	 *             If an Messaging Exception occurs
	 * @throws FileNotFoundException
	 *             If an FileNotFound Exception occurs
	 */
	public static void uploadValidSizeDocument(ActionRequest aoRequest,
			ActionResponse aoResponse, PortletSession aoSession,
			String asUserOrgType, P8UserSession aoUserSession,
			String asUserOrg, boolean abIsLinkedToApp,
			MimeBodyPart[] aoMimepartsNewVersion,
			HashMap<String, String> aoFilterHiddenMap, MimeBodyPart aoFiledata,
			String asFileType, String asDocType, Document aoDocument,
			Channel aoChannel, InputStream aoInputstream,
			StringBuffer asBfRealPath) throws ApplicationException,
			IOException, MessagingException, FileNotFoundException {
		String lsPermittedFormats;
		lsPermittedFormats = PropertyLoader.getProperty(
				P8Constants.ERROR_PROPERTY_FILE, "ALLOWEDEXT");
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.uploadValidSizeDocument()");
		if (aoDocument.getDocCategory().equals(
				ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocCategory()
						.equals(ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)) {
			lsPermittedFormats = PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"ALLOWED_TERMS_CONDITIONS");
		}
		/*
		 * This section verifies whether the uploading document is of valid type
		 * It will check the mime type of the document
		 */
		if (lsPermittedFormats.contains(asFileType)) {
			uploadValidFormatFile(aoRequest, aoResponse, aoSession,
					asUserOrgType, aoUserSession, asUserOrg, abIsLinkedToApp,
					aoMimepartsNewVersion, aoFilterHiddenMap, aoFiledata,
					asDocType, aoDocument, aoChannel, aoInputstream,
					asBfRealPath, asFileType);
		} else {
			generateErrorMessageForInvalidFormatDocument(aoSession, aoDocument);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.uploadValidSizeDocument()");
	}

	/**
	 * This method will generate error messages for invalid file extensions
	 * 
	 * @param aoSession
	 *            an PortletSession object
	 * @param aoDocument
	 *            a document object containing document properties
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	public static void generateErrorMessageForInvalidFormatDocument(
			PortletSession aoSession, Document aoDocument)
			throws ApplicationException, IOException {
		String lsMessage = HHSR5Constants.EMPTY_STRING;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.generateErrorMessageForInvalidFormatDocument()");
		if (aoDocument.getDocCategory().equals(
				ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocCategory()
						.equals(ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.MESSAGE_M503);
		} else {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.MESSAGE_M51);
		}
		aoSession.setAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				HHSConstants.CLC_CAP_ERROR, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.MESSAGE, lsMessage,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_FAIL_TYPE,
				PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.generateErrorMessageForInvalidFormatDocument()");
		throw new ApplicationException(lsMessage);
	}

	/**
	 * This method will upload validate document for allowed file extensions
	 * 
	 * <ul>
	 * <li>
	 * This method further calls the method 'validateAndProceedUpload' for
	 * further validation and upload.</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @param aoSession
	 *            an PortletSession object
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param aoUserSession
	 *            a string value of P8 user session
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param abIsLinkedToApp
	 *            a boolean value indicating link to application
	 * @param aoMimepartsNewVersion
	 *            a MimeBodyPart object
	 * @param aoFilterHiddenMap
	 *            a map containing filter hidden properties
	 * @param aoFiledata
	 *            a MimeBodyPart object
	 * @param asDocType
	 *            a string value of document type
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoChannel
	 *            a channel object
	 * @param aoInputstream
	 *            an Input Stream containing file details
	 * @param asBfRealPath
	 *            a string value of real path
	 * @param asFileType
	 *            a string value of file type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 * @throws MessagingException
	 *             If an Messaging Exception occurs
	 * @throws FileNotFoundException
	 *             If an File not Found Exception occurs
	 */
	public static void uploadValidFormatFile(ActionRequest aoRequest,
			ActionResponse aoResponse, PortletSession aoSession,
			String asUserOrgType, P8UserSession aoUserSession,
			String asUserOrg, boolean abIsLinkedToApp,
			MimeBodyPart[] aoMimepartsNewVersion,
			HashMap<String, String> aoFilterHiddenMap, MimeBodyPart aoFiledata,
			String asDocType, Document aoDocument, Channel aoChannel,
			InputStream aoInputstream, StringBuffer asBfRealPath,
			String asFileType) throws ApplicationException, IOException,
			MessagingException, FileNotFoundException

	{
		// Changes made as part of release 2.4.1 starts::
		// This reads the Enviornmental variable & decide which is the current
		// enviornment . This varible is splitted in two parts . First part
		// contains
		// the enviornment name (Prod, staging ,test) & second part defines
		// which is the user type either city of provider. Here we are
		// extracting the
		// user type in lsEnviornmentType variable.
		String lsEnviornmentType = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.uploadValidFormatFile()");
		if (ENVIRONMENT != null
				&& ENVIRONMENT.indexOf(ApplicationConstants.UNDERSCORE) > 0) {
			String lsEnvArray[] = ENVIRONMENT
					.split(ApplicationConstants.UNDERSCORE);
			lsEnviornmentType = lsEnvArray[1];
		}
		// Changes made as part of release 2.4.1 ends::
		// document checked for virus scan
		byte[] loByteStream = convert(aoInputstream);
		String lsLoginEnvironment = PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE,
				HHSConstants.BASENEW_PROPERTY_LOGIN_ENVIRONMENT);
		int liResultCode = 0;
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		HttpServletRequest loHttpRequest = PortalUtil
				.getServletRequest(aoRequest);
		String lsClientIp = getClientIpAddr(loHttpRequest);

		/* R9.6.2 QC9695 the IP Address was assigned to closed DEV sever 
		lsClientIp = HHSConstants.CLIENT_IP;
		*/

		LOG_OBJECT.Debug("Check for Enviornment type  for Virus Scanning: lsEnviornmentType:"
				 + lsEnviornmentType + ",lsLoginEnvironment:" +lsLoginEnvironment + "\tlsClientIp is " + lsClientIp );
		
		// Changes made as part of release 2.4.1 ends::
		/* Start R8.5.0 qc_9495 Update McAfee Gateway endpoints in HHS Accelerator properties files (removed checking only providers to scan)*/
		// Start R9.6.1 qc_9692 Update document anti-virus scanning connection point
		//VirusScanUtility loVirusScanUtility = new VirusScanUtility();
		VirusScanUtility loVirusScanUtility = new VirusScanUtility(asUserOrgType);		
		String lsResultString = loVirusScanUtility.scanFile(loByteStream,
					aoFiledata.getSize(), aoFiledata.getFileName(), lsClientIp);
		LOG_OBJECT.Info("scan lsResultString:" +lsResultString);
		// End R9.6.1 qc_9692 Update document anti-virus scanning connection point
		liResultCode = loVirusScanUtility.resultCode(lsResultString);
		/* End R8.5.0 qc_9495 Update McAfee Gateway endpoints in HHS Accelerator properties files */
		
		if (liResultCode > 0) {
			// code for file infected

			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					HHSConstants.CLC_CAP_ERROR,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.MESSAGE, PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"FILE_CONTAINS_VIRUS"),
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.sendRedirect(aoRequest.getContextPath()
					+ ApplicationConstants.ERROR_HANDLER);
			LOG_OBJECT.Error("Virus scan error, result code:" +liResultCode +", file Name:" +aoFiledata.getFileName() + ",lsClientIp:" +lsClientIp);
		} else if (liResultCode == 0) {

			validateAndProceedUpload(aoRequest, aoResponse, aoSession,
					asUserOrgType, aoUserSession, asUserOrg, abIsLinkedToApp,
					aoMimepartsNewVersion, aoFilterHiddenMap, aoFiledata,
					asDocType, aoDocument, aoChannel, loByteStream,
					asBfRealPath, asFileType);

		}
		if (liResultCode < 0) {
			// error occurred file scanning file
			aoSession.setAttribute(
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					HHSConstants.CLC_CAP_ERROR,
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.MESSAGE, PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"FILE_CONTAINS_VIRUS"),
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.sendRedirect(aoRequest.getContextPath()
					+ ApplicationConstants.ERROR_HANDLER);
			LOG_OBJECT.Error("Virus scan error, result code:" +liResultCode +", file Name:" +aoFiledata.getFileName() + ",lsClientIp:" +lsClientIp);
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(
				CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		if (liTimediff > 1) {
			LOG_OBJECT.Debug("!!!!!Ending Virus Scanning, TIME LAPSED,"
					+ liTimediff);
		}
		LOG_OBJECT
				.Debug("FileNetOperationsUtils: scanning documents for virus. method:uploadValidFormatFile. Time Taken(seconds):: "
						+ liTimediff);
	}

	/**
	 * This method is used to get the help category list configured in the
	 * extended doctype xml file
	 * 
	 * @param asOrgType
	 *            a string value containing organization type
	 * @param asDocCategory
	 *            a string value containing doc category
	 * @param asDocType
	 *            a string value containing doc typ
	 * @return Array list of help category
	 * @throws ApplicationException
	 */
	public static ArrayList<String> getHelpCategory(String asOrgType,
			String asDocCategory, String asDocType) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getHelpCategory()");
		ArrayList<String> loHelpCategoryList = new ArrayList<String>();
		loHelpCategoryList.add(HHSR5Constants.EMPTY_STRING);
		getHelpCategoryFromXml(loHelpCategoryList, asOrgType, asDocCategory,
				asDocType);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getHelpCategory()");
		return loHelpCategoryList;
	}

	/**
	 * This method is used to get the help category list configured in the
	 * extended doctype xml file
	 * 
	 * @param aoHelpCategoryList
	 *            help category list
	 * @param asOrgType
	 *            organization type
	 * @param asDocCategory
	 *            document category
	 * @param asDocType
	 *            document type
	 * @throws ApplicationException
	 */
	private static void getHelpCategoryFromXml(
			ArrayList<String> aoHelpCategoryList, String asOrgType,
			String asDocCategory, String asDocType) throws ApplicationException {
		String lsExPath = null;
		org.jdom.Document loXMLDoc = null;
		List<Element> loHelpCategoryElementList = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getHelpCategoryFromXml()");
		try {
			loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance()
					.getCacheObject(
							ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
			if (null != asDocType && !asDocType.isEmpty()
					&& !asDocType.equalsIgnoreCase(ApplicationConstants.HELP)) {
				lsExPath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgType + "\"] //"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
						+ asDocCategory + "\"] //"
						+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
						+ asDocType + "\"]/HelpCategory";
			} else {
				lsExPath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgType + "\"] //"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
						+ asDocCategory + "\"]/DocType/HelpCategory";
			}

			loHelpCategoryElementList = XMLUtil.getElementList(lsExPath,
					loXMLDoc);
			if (null != loHelpCategoryElementList
					&& !loHelpCategoryElementList.isEmpty()) {
				for (int liChildCount = 0; liChildCount < loHelpCategoryElementList
						.size(); liChildCount++) {
					String lsElementText = loHelpCategoryElementList.get(
							liChildCount).getAttributeValue(HHSConstants.NAME);
					aoHelpCategoryList.add(lsElementText);
				}
			}
		} catch (Exception aoEx) {
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: getHelpCategoryFromXml method::",
							aoEx);
			throw new ApplicationException(
					"Exception occured in FileNetOperationsUtils: getHelpCategoryFromXml method::",
					aoEx);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getHelpCategoryFromXml()");
	}

	/**
	 * This method will set document categories and document type in document
	 * object based on document category
	 * 
	 * @param aoDoc
	 *            a document object containing document properties
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asUserOrg
	 *            a string value of user organization
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static void setDocCategorynDocType(Document aoDoc,
			String asDocCategory, String asUserOrg) throws ApplicationException {
		try {
			LOG_OBJECT
					.Info("Entered FileNetOperationsUtils.setDocCategorynDocType()");
			if (null == asDocCategory
					|| (HHSR5Constants.EMPTY_STRING)
							.equalsIgnoreCase(asDocCategory)) {
				ArrayList<String> loCategoryList = new ArrayList<String>();
				loCategoryList = (ArrayList<String>) getDocCategoryList(asUserOrg);
				loCategoryList.add(0, HHSR5Constants.EMPTY_STRING);
				aoDoc.setCategoryList(loCategoryList);
			} else {
				if (CollectionUtils.isEmpty(aoDoc.getCategoryList())) {
					ArrayList<String> loCategoryList = new ArrayList<String>();
					loCategoryList = (ArrayList<String>) getDocCategoryList(asUserOrg);
					loCategoryList.add(0, HHSR5Constants.EMPTY_STRING);
					aoDoc.setCategoryList(loCategoryList);
				}
				ArrayList<String> loTypeList = new ArrayList<String>();
				loTypeList = (ArrayList<String>) getDocTypeForDocCategory(
						asDocCategory, asUserOrg, null);
				aoDoc.setDocCategory(asDocCategory);
				loTypeList.add(0, HHSR5Constants.EMPTY_STRING);
				aoDoc.setTypeList(loTypeList);
			}
		} catch (ApplicationException aoAppex) {
			throw aoAppex;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setDocCategorynDocType()");
	}

	/**
	 * This method will get a list of document categories from XML DOM object
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @return a list of document categories
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static List<String> getDocCategoryList(String asUserOrg)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocCategoryList()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocCategoryList()");
		return getDocCategoryFromXML(loXMLDoc, null, asUserOrg);
	}

	/**
	 * This method will get a list of document types from XML DOM object based
	 * on document category
	 * 
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asUserOrg
	 *            a string value of user organization
	 * @return a list of document types
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static List<String> getDocTypeForDocCategory(String asDocCategory,
			String asUserOrg, String asRequestingOrg)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocTypeForDocCategory()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocTypeForDocCategory()");
		return getDoctypesFromXML(loXMLDoc, asDocCategory, asUserOrg,
				asRequestingOrg);
	}

	/**
	 * This method will get list of implementation status from XML DOM object
	 * 
	 * @return a list of implementation status
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static ArrayList<String> getImplementationStatusList()
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getImplementationStatusList()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
		ArrayList<String> loSampleCategoryList = null;
		loSampleCategoryList = (ArrayList<String>) getDoctypesFromXML(loXMLDoc,
				ApplicationConstants.IMPLEMENTATION_STATUS,
				ApplicationConstants.UNSHARE_BY_ALL, null);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getImplementationStatusList()");
		return loSampleCategoryList;
	}

	/**
	 * This method will get expected document type for provider Id
	 * 
	 * @param asProviderId
	 *            a string value of provider ID
	 * @return a string value of expected document type
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static String getNextExpectedDocType(String asProviderId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getNextExpectedDocType()");
		String lsExpectedDocType = HHSR5Constants.EMPTY_STRING;
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROVIDER_ID, asProviderId);
		TransactionManager.executeTransaction(loChannel,
				"checkExtensionType_DB");
		lsExpectedDocType = (String) loChannel
				.getData(HHSR5Constants.LS_EXTENSIONS);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getNextExpectedDocType()");
		return lsExpectedDocType;
	}

	/**
	 * This method will get exception messages during CHAR500 extension document
	 * upload
	 * 
	 * @param asExpectedDocType
	 *            a string value of expected document type
	 * @param asDocumentType
	 *            a string value of document type
	 * @return a string value of exception message
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	private static String getExpMsgForExtUpload(String asExpectedDocType,
			String asDocumentType) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getExpMsgForExtUpload()");
		String lsMessageToDisplay = HHSR5Constants.EMPTY_STRING;
		String[] loExtensionsArr = asExpectedDocType
				.split(HHSConstants.DOUBLE_HHSUTIL_DELIM_PIPE);
		List loExtensionsList = Arrays.asList(loExtensionsArr);
		if (!loExtensionsList.contains(asDocumentType)) {
			lsMessageToDisplay = PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M56);
		}
		if (asDocumentType
				.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE)
				&& loExtensionsList
						.contains(P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)) {
			lsMessageToDisplay = PropertyLoader
					.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_M56);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getNextExpectedDocType()");
		return lsMessageToDisplay;
	}

	/**
	 * This method will indicate if document is linked to application or not
	 * 
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'checkLinkToApplication_filenet'</li>
	 * <li>
	 * This method executed the transaction 'displayDocProp_filenet'</li>
	 * </ul>
	 * Updated for Release 3.3.0, Defect 6443 Fix
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoDocumentId
	 *            a string value of document Id
	 * @return a boolean value indication if document is linked or not
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean checkLinkToApplication(ActionRequest aoRequest,
			String aoDocumentId) throws ApplicationException {
		boolean lbLinked = false;
		String lsDocType = HHSR5Constants.EMPTY_STRING;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkLinkToApplication()");
		if (null != aoDocumentId) {
			List<String> loDocIdList = new ArrayList<String>();
			loDocIdList.add(aoDocumentId);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest
					.getPortletSession().getAttribute(
							ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUploadingDocumentType = PortalUtil.parseQueryString(
					aoRequest, ApplicationConstants.UPLOAD_DOC_TYPE);
			HashMap<String, String> loPropertyMap = new HashMap<String, String>();
			HashMap loRequiredPropMap = new HashMap();
			HashMap<String, String> loFinalRequiredPropMap = null;
			loPropertyMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,
					lsUserOrgType);
			loPropertyMap
					.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, aoDocumentId);
			loRequiredPropMap.put(P8Constants.PROPERTY_CE_DOC_TYPE,
					HHSR5Constants.EMPTY_STRING);
			loRequiredPropMap.put(
					P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
					HHSR5Constants.EMPTY_STRING);
			if (ApplicationConstants.PROVIDER_ORG
					.equalsIgnoreCase(lsUserOrgType)
					|| ApplicationConstants.AGENCY_ORG
							.equalsIgnoreCase(lsUserOrgType)) {
				loPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
						lsUserOrg);
			} else if (ApplicationConstants.CITY_ORG
					.equalsIgnoreCase(lsUserOrgType)) {
				loPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
						lsUserOrgType);
			}
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.BULK_UPLOAD_FILE_PROPS,
					loPropertyMap);
			loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
					loRequiredPropMap);
			loChannel.setData(HHSConstants.AO_DOC_ID_LIST, loDocIdList);
			TransactionManager.executeTransaction(loChannel,
					"checkLinkToApplication_filenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			TransactionManager.executeTransaction(loChannel,
					"displayDocProp_filenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbLinked = (Boolean) loChannel.getData(HHSR5Constants.LINKED);
			loRequiredPropMap = (HashMap) loChannel
					.getData(HHSConstants.DOCUMENT_PROPERTY_HASH_MAP);
			loFinalRequiredPropMap = (HashMap) loRequiredPropMap
					.get(aoDocumentId);
			lsDocType = loFinalRequiredPropMap
					.get(P8Constants.PROPERTY_CE_DOC_TYPE);
			if (!lbLinked) {
				ApplicationSession.setAttribute(
						HHSR5Constants.CHECK_APP_STATUS, aoRequest,
						String.valueOf(lbLinked));
				Document loDocument = new Document();
				loDocument.setLinkToApplication(lbLinked);
				loDocument.setDocumentId(aoRequest
						.getParameter(ApplicationConstants.DOCUMENT_ID));
				ApplicationSession.setAttribute(loDocument, aoRequest,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				return lbLinked;
			} else {
				if ((null == lsUploadingDocumentType || lsUploadingDocumentType
						.isEmpty())
						&& ApplicationConstants.PROVIDER_ORG
								.equalsIgnoreCase(lsUserOrgType)) {
					lbLinked = checkApplicationStatusForLinkedDoc(aoRequest,
							aoDocumentId, lbLinked, lsDocType);
					ApplicationSession.setAttribute(ApplicationConstants.TRUE,
							aoRequest, HHSR5Constants.CHECK_APP_STATUS);
					// Updated for Release 3.3.0, Defect 6443 Fix Starts
					String lsDocumentUpload = PortalUtil.parseQueryString(
							aoRequest, HHSR5Constants.UPLOAD_FLAG);
					if (null != lsDocumentUpload
							&& !lsDocumentUpload.isEmpty()
							&& HHSConstants.STRING_TRUE
									.equalsIgnoreCase(lsDocumentUpload)
							&& null != lsDocType
							&& (ApplicationConstants.CONTRACT_GRANT_DOC_TYPE
									.equalsIgnoreCase(lsDocType)
									|| ApplicationConstants.KEY_STAFF_DOC_TYPE
											.equalsIgnoreCase(lsDocType) || ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE
									.equalsIgnoreCase(lsDocType))) {
						lbLinked = true;
					}
					// Updated for Release 3.3.0, Defect 6443 Fix Ends
				} else {
					ApplicationSession.setAttribute(ApplicationConstants.TRUE,
							aoRequest,
							ApplicationConstants.IS_SM_FINANCE_DOCUMENT);
				}
			}
			Document loDocument = new Document();
			loDocument.setLinkToApplication(lbLinked);
			loDocument.setDocumentId(aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_ID));
			ApplicationSession.setAttribute(loDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkLinkToApplication()");
		return lbLinked;
	}

	/**
	 * This Method was modified for release 2.6.0 to fix defect 5661
	 * 
	 * This method will check the application status if a document is linked to
	 * application
	 * 
	 * <ul>
	 * <li>1. Passes Document ID and Document Type</li>
	 * <li>2. This method executes transaction checkSection_DB ,
	 * documentIdCount, checkApplicationStatus_DB to fetch the status</li>
	 * <li>3. It then returns the flag true of false depending on diffrent
	 * combinations.
	 * <li>
	 * 
	 * This method executed the transaction 'documentIdCount'</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoDocumentId
	 *            document id of the selected document
	 * @param abLinked
	 *            is linked to application boolean
	 * @param asDocType
	 *            type of the document selected
	 * @return boolean status of the application
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static boolean checkApplicationStatusForLinkedDoc(
			ActionRequest aoRequest, String aoDocumentId, boolean abLinked,
			String asDocType) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkApplicationStatusForLinkedDoc()");
		HashMap loAppStatusDocMap = new HashMap();
		Channel loHMChannel = new Channel();
		loAppStatusDocMap.put(ApplicationConstants.DOCUMENT_ID, aoDocumentId);
		loHMChannel.setData(HHSConstants.AO_APP_STATUS_DOC_MAP,
				loAppStatusDocMap);
		TransactionManager.executeTransaction(loHMChannel, "documentIdCount",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		int liDocIdCount = (Integer) loHMChannel
				.getData(HHSConstants.COUNT_DOC);
		if (liDocIdCount == 1 || liDocIdCount == 0) { // Fixed defect 1837
			if (abLinked) {
				abLinked = checkApplicationStatusForLinkedDocFinal(aoRequest,
						aoDocumentId, abLinked, asDocType);
			} else {
				abLinked = false;
			}
		} else if (liDocIdCount > 1) {
			abLinked = statuscheckForMultipleApplications(aoDocumentId);
		} else {
			abLinked = false;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkApplicationStatusForLinkedDoc()");
		return abLinked;

	}

	/**
	 * Changed in 3.1.0 . Added check for Enhancement 6021 - char500 - extension
	 * Changed in 3.2.0 for Enhancement 5641 This method will check the
	 * application status if a document is linked to application
	 * <ul>
	 * <li>
	 * This method executed the transaction 'getBRAppStatusForProvider_DB'</li>
	 * <li>
	 * This method executed the transaction 'checkApplicationStatus_DB'</li>
	 * <li>
	 * This method executed the transaction 'checkSection_DB'</li>
	 * <li>
	 * This method executed the transaction 'checkService_DB'</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param asDocumentId
	 *            DocumentId
	 * @param abLinked
	 *            is linked to application boolean
	 * @param asDocType
	 *            DocType
	 * @return abLinked boolean
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static boolean checkApplicationStatusForLinkedDocFinal(
			ActionRequest aoRequest, String asDocumentId, boolean abLinked,
			String asDocType) throws ApplicationException {
		// String lsDueDate;
		Channel loChannel;
		String lsStatus;
		loChannel = new Channel();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkApplicationStatusForLinkedDocFinal()");
		loChannel.setData(
				HHSConstants.PROVIDER_ID,
				(String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE));

		// Modified as per 3.1.0 . Enhancement 6021 - Updated checks for char
		// 500 - extension
		if (asDocType
				.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)
				|| asDocType
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)
				|| asDocType
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE)
				|| asDocType
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXTENSION)) {
			abLinked = checkApplicationStatusForCHAR500(aoRequest,
					asDocumentId, loChannel);
		} else {
			HashMap loAppStatusMap = new HashMap();
			loAppStatusMap.put(ApplicationConstants.DOCUMENT_ID, asDocumentId);
			loChannel.setData(HHSR5Constants.AO_APP_MAP, loAppStatusMap);
			TransactionManager.executeTransaction(loChannel,
					"checkApplicationStatus_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lsStatus = (String) loChannel.getData(HHSR5Constants.LS_APP_STATUS);
			TransactionManager.executeTransaction(loChannel, "checkSection_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsSectionStatus = (String) loChannel
					.getData(HHSR5Constants.LS_SECTION_STATUS);
			TransactionManager.executeTransaction(loChannel, "checkService_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String lsServiceStatus = (String) loChannel
					.getData(HHSR5Constants.LS_SERVICE_STATUS);
			if (lsStatus != null
					&& !lsStatus.equals(HHSR5Constants.EMPTY_STRING)) {
				if (lsStatus
						.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT)
						|| lsStatus
								.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DEFERRED)
						|| lsStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)) {
					if (lsSectionStatus != null
							&& lsSectionStatus
									.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)) {
						abLinked = true;
					} else {
						abLinked = false;
						ApplicationSession.setAttribute(
								HHSConstants.STRING_TRUE, aoRequest,
								HHSR5Constants.DELETE_DOC);
						// 5661 fix starts. Setting OldDocumentIdReq in session
						ApplicationSession.setAttribute(asDocumentId,
								aoRequest, HHSConstants.OLD_DOCUMENT_ID_REQ);
						// 5661 fix ends
					}
				} else {
					abLinked = true;
				}
				if (lsServiceStatus != null
						&& (lsServiceStatus
								.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DEFERRED)
								|| lsServiceStatus
										.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
								|| lsServiceStatus
										.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT) || lsServiceStatus
								.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))) {
					abLinked = false;
					// 5661 fix starts. Setting OldDocumentIdReq in session
					ApplicationSession.setAttribute(asDocumentId, aoRequest,
							HHSConstants.OLD_DOCUMENT_ID_REQ);
					// 5661 fix ends
				}

			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkApplicationStatusForLinkedDoc()");
		return abLinked;
	}

	/**
	 * The method will check the application status for DOCTYPE- CHAR 500.
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param asDocumentId
	 *            Document Id of the Document
	 * @param aoChannel
	 *            a Channel 0bject
	 * @return status as true if linked.
	 * @throws ApplicationException
	 */
	private static boolean checkApplicationStatusForCHAR500(
			ActionRequest aoRequest, String asDocumentId, Channel aoChannel)
			throws ApplicationException {
		boolean abLinked;
		HashMap loAppStatusMap = new HashMap();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkApplicationStatusForCHAR500()");
		loAppStatusMap.put(ApplicationConstants.DOCUMENT_ID, asDocumentId);
		// Updated for Release 3.2.0, Defect 5641 - Start
		loAppStatusMap.put(
				HHSConstants.PROVIDER_ID,
				(String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE));
		// Updated for Release 3.2.0, Defect 5641 - Ends
		aoChannel.setData(HHSR5Constants.AO_APP_MAP, loAppStatusMap);
		TransactionManager.executeTransaction(aoChannel, "checkSection_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		String lsSectionStatus = (String) aoChannel
				.getData(HHSR5Constants.LS_SECTION_STATUS);
		if (lsSectionStatus != null
				&& lsSectionStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)) {
			abLinked = true;
		} else {
			// Updated for Release 3.2.0, Defect 5641 - Start
			/*
			 * if (null != lsDueDate &&
			 * !lsDueDate.equals(HHSR5Constants.EMPTY_STRING)) { abLinked =
			 * true; } else {
			 */
			// Updated for Release 3.2.0, Defect 5641 - Ends
			TransactionManager.executeTransaction(aoChannel,
					"getBRAppStatusForProvider_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<String> loBrAppStatusList = (List<String>) aoChannel
					.getData(HHSR5Constants.BR_APP_STATUS);
			if ((loBrAppStatusList
					.contains(ApplicationConstants.STATUS_IN_REVIEW) || loBrAppStatusList
					.contains(ApplicationConstants.STATUS_APPROVED))
					// Updated for Release 3.2.0, Defect 5641
					&& !loBrAppStatusList
							.contains(ApplicationConstants.STATUS_RETURNED)) {
				abLinked = true;
			} else {
				abLinked = false;
				// 5661 fix starts. Setting OldDocumentIdReq in session
				ApplicationSession.setAttribute(asDocumentId, aoRequest,
						HHSConstants.OLD_DOCUMENT_ID_REQ);
				// 5661 fix ends
			}
			// Updated for Release 3.2.0, Defect 5641 - Start
			/* } */
			// Updated for Release 3.2.0, Defect 5641 - Ends
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkApplicationStatusForCHAR500()");
		return abLinked;
	}

	/**
	 * This Method Fetches the count the document id against which application
	 * are in "In Review" or Approved Status
	 * <ul>
	 * <li>
	 * This method executed the transaction 'statusDocumentIdCount'</li>
	 * </ul>
	 * 
	 * @param asDocumentId
	 *            Document Id of the Document
	 * @return boolean Object
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */

	private static boolean statuscheckForMultipleApplications(
			String asDocumentId) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.statuscheckForMultipleApplications()");
		boolean lbStatusMultiApplication = false;
		Channel loChannel = new Channel();
		HashMap loAppStatusDocMap = new HashMap();
		loAppStatusDocMap.put(ApplicationConstants.DOCUMENT_ID, asDocumentId);
		loChannel
				.setData(HHSConstants.AO_APP_STATUS_DOC_MAP, loAppStatusDocMap);
		TransactionManager.executeTransaction(loChannel,
				"statusDocumentIdCount", HHSR5Constants.TRANSACTION_ELEMENT_R5);
		int liDocIdSatutsCount = (Integer) loChannel
				.getData(HHSR5Constants.STATUS_DOC_COUNT);
		if (liDocIdSatutsCount > 0) {
			lbStatusMultiApplication = true;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.statuscheckForMultipleApplications()");
		return lbStatusMultiApplication;
	}

	/**
	 * This method will indicate if document already exists and will return the
	 * document ID
	 * 
	 * <ul>
	 * <li>
	 * This method executed the transaction 'checkDocumentExist_filenet'</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asProviderId
	 *            a string value of provider Id
	 * @param asDocTitle
	 *            a string value of document title
	 * @param asDocType
	 *            a string value of document type
	 * @param asDocCategory
	 *            a String value of Document Category.
	 * @param asOrgId
	 *            a string value of organization Id
	 * @return a string value of document Id if document already exists
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static String checkDocExist(P8UserSession aoUserSession,
			String asProviderId, String asDocTitle, String asDocType,
			String asDocCategory, String asOrgId) throws ApplicationException {
		Channel loChannel = new Channel();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.checkDocExist()");
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loChannel.setData(P8Constants.AS_DOC_TITLE, asDocTitle);
		loChannel.setData(HHSConstants.AS_DOC_TYPE, asDocType);
		loChannel
				.setData(
						ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOC_CATEGORY,
						asDocCategory);
		loChannel.setData(P8Constants.AO_ORG_ID, asOrgId);
		TransactionManager.executeTransaction(loChannel,
				"checkDocumentExist_filenet",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);

		String lsDocId = (String) loChannel
				.getData(ApplicationConstants.DOCUMENT_ID);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.checkDocExist()");
		return lsDocId;
	}

	/**
	 * This method is modified for release 3.1.0 . Added check for Enhancement
	 * 6021 - Char500 - extension This Method was modified for release 2.6.0 to
	 * fix defect 5661
	 * <ul>
	 * <li>This method will get upload document to filenet and get the document
	 * Id for the uploaded document</li>
	 * <li>1. It first sets all the properties of the document</li>
	 * <li>2. It Then upload the document to filenet .</li>
	 * <li>3. It then updates the document table with proper parameters.</li>
	 * <li>Method Updated in R4</li>
	 * <li>Method Updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @return a string value of uploaded document Id
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	public static String actionFileUpload(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException, IOException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.actionFileUpload()");
		String lsTransactionStartTime = CommonUtil.getCurrentTimeInMilliSec();
		PortletSession loSession = aoRequest.getPortletSession();
		// BAFO Document upload change so as to read the BAFO Document details
		// from DOCTYPE.xml as the login user is agency user
		String lsUserName = HHSR5Constants.EMPTY_STRING;
		String lsUserID = HHSR5Constants.EMPTY_STRING;
		String lsUserOrgType = HHSR5Constants.EMPTY_STRING;
		String lsUserOrg = HHSR5Constants.EMPTY_STRING;
		if (HHSConstants.BAFO.equalsIgnoreCase(HHSPortalUtil.parseQueryString(
				aoRequest, HHSConstants.UPLOAD_DOC_TYPE))) {
			lsUserName = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.USER);
			lsUserID = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.STAFF_ID);
			lsUserOrgType = HHSConstants.PROVIDER_ORG;
			lsUserOrg = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.ORGA_ID);
		} else {
			lsUserName = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_NAME,
					PortletSession.APPLICATION_SCOPE);
			lsUserID = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			lsUserOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			lsUserOrg = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
		}
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT,
				PortletSession.APPLICATION_SCOPE);
		Document loDocument = (Document) ApplicationSession.getAttribute(
				aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		Channel loChannel = new Channel();
		String lsFilePath = loDocument.getFilePath();
		File loFile = new File(lsFilePath);
		String lsFileRetrievalName = lsFilePath.substring(
				lsFilePath.lastIndexOf(P8Constants.STRING_SINGLE_SLASH) + 1,
				lsFilePath.lastIndexOf(ApplicationConstants.UNDERSCORE));
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		String lsDocumentId = null;
		FileInputStream loStreamUploadFile = null;
		HashMap<String, Object> loPropertyMap = new HashMap<String, Object>();
		HashMap<String, Object> loInsertPropMap = new HashMap<String, Object>();
		List<DocumentPropertiesBean> loDocPropsList = loDocument
				.getDocumentProperties();
		// Updated in release 4.0.1- for removing mismatch in modified date
		String lsCurrentDate = DateUtil.getCurrentDateWithTimeStamp();
		// Updated in release 4.0.1- for removing mismatch in modified date end
		try {
			// Added if condition for Release 5
			if (null != aoRequest.getParameter(HHSR5Constants.CUSTOM_FLDR_ID)) {
				loPropertyMap.put(HHSR5Constants.CUSTOM_FLDR_ID,
						aoRequest.getParameter(HHSR5Constants.CUSTOM_FLDR_ID));
				loChannel.setData("folderId",
						aoRequest.getParameter(HHSR5Constants.CUSTOM_FLDR_ID));
			}
			// R5: added for Defect 8315
			if (HHSConstants.BAFO.equalsIgnoreCase(HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE))) {
				loPropertyMap.put(HHSR5Constants.CUSTOM_FLDR_ID,
						HHSR5Constants.DOCUMENT_VAULT_ID);
			}
			// R5: added for Defect 8315 end
			loChannel.setData("folderExistCheck", true);
			loChannel.setData("entityType", HHSR5Constants.EMPTY_STRING);
			loChannel.setData("filenetEntityName", "File Creation");
			// End Release 5
			setUploadProperties(lsUserName, lsUserID, lsUserOrgType,
					loDocument, lsCurrentDate, loPropertyMap,
					lsFileRetrievalName, lsUserOrg);
			setPropertyBeanForFileUpload(aoRequest, loDocument, loPropertyMap);
			if (!CollectionUtils.isEmpty(loDocPropsList)) {
				Iterator<DocumentPropertiesBean> loDocPropsIt = loDocPropsList
						.iterator();
				while (loDocPropsIt.hasNext()) {
					DocumentPropertiesBean loDocProps = loDocPropsIt.next();
					loInsertPropMap
							.put(loDocProps.getPropSymbolicName(),
									loPropertyMap.get(loDocProps
											.getPropSymbolicName()));
				}
			}
			if (ApplicationConstants.PROVIDER_ORG
					.equalsIgnoreCase(lsUserOrgType)
					|| ApplicationConstants.AGENCY_ORG
							.equalsIgnoreCase(lsUserOrgType)) {
				loPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
						lsUserOrg);
			} else if (ApplicationConstants.CITY_ORG
					.equalsIgnoreCase(lsUserOrgType)) {
				setUploadPropertiesForAccelerator(aoRequest, lsUserOrgType,
						loDocument, lsCurrentDate, loPropertyMap,
						lsFileRetrievalName);
			}
			String lsNextExpectedDocType = getNextExpectedDocType(lsUserOrg);
			// Updated for 3.1.0, enhancement 6021 - Added checks for char 500
			// extension
			// Release 5 Changes.
			if (loDocument.getDocType().equalsIgnoreCase(
					P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)
					|| loDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)
					|| loDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE)
					|| loDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXTENSION)) {
				if (null != lsNextExpectedDocType
						&& !lsNextExpectedDocType.trim().equals(
								HHSR5Constants.EMPTY_STRING)) {
					lsDocumentId = getNextDocType(aoRequest, aoResponse,
							loPropertyMap, lsNextExpectedDocType);
				} else {
					double ldMonthGap = validatePeriodCoveredDateChar500ForDraftApp(loPropertyMap);
					if ((null == lsNextExpectedDocType || lsNextExpectedDocType
							.trim().equals(HHSR5Constants.EMPTY_STRING))
							&& (ldMonthGap >= 0 && ldMonthGap < Double
									.valueOf("11"))) {
						ApplicationException loCustomExp = new ApplicationException(
								PropertyLoader.getProperty(
										P8Constants.ERROR_PROPERTY_FILE,
										"PERIOD_COVERED_VALIDATION"));
						throw loCustomExp;
					} else {
						loStreamUploadFile = new FileInputStream(loFile);
						loChannel.setData(HHSConstants.AO_FILENET_SESSION,
								loUserSession);
						loDataMap = uploadDocument(loChannel,
								loStreamUploadFile, loPropertyMap, false, false);
						lsDocumentId = (String) loDataMap
								.get(HHSConstants.DOC_ID);
						loStreamUploadFile.close();
					}

				}
			} else {
				if (loDocument.getDocType().equalsIgnoreCase(
						P8Constants.PROPERTY_CE_SYSTEM_TERMS_CONDITION_TYPE)) {
					TransactionManager.executeTransaction(loChannel,
							"updateTermaAndConditionFlag",
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
				}
				loStreamUploadFile = new FileInputStream(loFile);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);
				loDataMap = uploadDocument(loChannel, loStreamUploadFile,
						loPropertyMap, false, false);
				lsDocumentId = (String) loDataMap.get(HHSConstants.DOC_ID);
				// fetching parent Flag from channel and set it into request
				loStreamUploadFile.close();
			}
			if (null != lsDocumentId
					&& !lsDocumentId
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
				getNewDocument(aoRequest, lsUserOrg, loUserSession, loDocument,
						loChannel, lsDocumentId);
			} else {
				String lsMessage = PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE,
						HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoResponse.setRenderParameter(
						ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// added for defect 1795
			loSession.removeAttribute("OldDocumentId",
					PortletSession.APPLICATION_SCOPE);
		} catch (ApplicationException aoEx) {
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileUpload method::",
							aoEx);
			throw aoEx;
		} catch (IOException aoEx) {
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileUpload method::",
							aoEx);
			throw new ApplicationException(
					"Exception occured in FileNetOperationsUtils: actionFileUpload method::",
					aoEx);
		} finally {
			if (null != loStreamUploadFile) {
				loStreamUploadFile.close();
			}
		}
		String lsTransactionEndTime1 = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(
				CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
				CommonUtil.getItemDateInMIlisec(lsTransactionEndTime1));
		if (liTimediff > 3) {
			LOG_OBJECT
					.Debug("!!!!!Ending actionFileUpload() in FilenetOperationsUtils, TIME LAPSED,"
							+ liTimediff);
		} else {
			LOG_OBJECT
					.Debug("Ending method actionFileUpload in FilenetOperationsUtils :: TIME LAPSED :"
							+ liTimediff);
		}

		// Adding new message
		String lsSharingFlagForParent = (String) loDataMap
				.get(HHSR5Constants.PARENT_SHARING_FLAG);
		if (null != lsSharingFlagForParent
				&& (lsSharingFlagForParent.equalsIgnoreCase("2"))) {
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"M031"));
			aoResponse.setRenderParameter(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		} else {
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"M03"));
			aoResponse.setRenderParameter(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.actionFileUpload()");
		return lsDocumentId;
	}

	/**
	 * This method return the new document.
	 * 
	 * @param aoRequest
	 *            an action request
	 * @param lsUserOrg
	 *            as user organization
	 * @param loUserSession
	 *            as user session
	 * @param loDocument
	 *            document object
	 * @param loChannel
	 *            channel object
	 * @param lsDocumentId
	 *            as document id
	 * @throws ApplicationException
	 */
	private static void getNewDocument(ActionRequest aoRequest,
			String asUserOrg, P8UserSession aoUserSession, Document aoDocument,
			Channel aoChannel, String asDocumentId) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getNewDocument()");
		// 5661 fix starts. If condition to get parameter "OldDocumentIdReq".
		if (null != aoRequest.getParameter(HHSConstants.OLD_DOCUMENT_ID_REQ)
				&& !((String) (aoRequest
						.getParameter(HHSConstants.OLD_DOCUMENT_ID_REQ)))
						.isEmpty()) {
			// 5661 fix ends
			setLinkToApplicationForDocument(aoRequest, asUserOrg,
					aoUserSession, aoDocument, aoChannel, asDocumentId);
		}
		File loFilePath = new File(aoDocument.getFilePath());
		loFilePath.delete();
		aoDocument = null;
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getNewDocument()");
	}

	/**
	 * This method returns the Doc Id.
	 * 
	 * @param aoRequest
	 *            as ActionRequest Object
	 * @param aoResponse
	 *            as ActionResponse Object
	 * @param loPropertyMap
	 *            as property map
	 * @param lsNextExpectedDocType
	 * @return document id
	 * @throws ApplicationException
	 * @throws IOException
	 */
	private static String getNextDocType(ActionRequest aoRequest,
			ActionResponse aoResponse, HashMap aoPropertyMap,
			String asNextExpectedDocType) throws ApplicationException,
			IOException {
		String lsDocumentId = null;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getNextDocType()");
		try {
			aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
					true);
			double ldMonthGap = validatePeriodCoveredDateChar500(aoPropertyMap,
					asNextExpectedDocType);
			if (ldMonthGap >= 0 && ldMonthGap < Double.valueOf("11")) {
				lsDocumentId = validateAndUploadChar500(aoRequest, aoResponse,
						aoPropertyMap, true);
			} else {
				lsDocumentId = validateAndUploadChar500(aoRequest, aoResponse,
						aoPropertyMap, false);
			}
		} catch (ApplicationException aoEx) {
			throw aoEx;
		} catch (Exception aoExp) {
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getNextDocType()");
		return lsDocumentId;
	}

	/**
	 * This method is used to set the link to application attribute in the
	 * document object
	 * <ul>
	 * <li>
	 * This method executed the transaction 'saveProperties_filenet'</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            ActionRequest Object
	 * @param asUserOrg
	 *            user organization Name
	 * @param aoUserSession
	 *            P8 user Session
	 * @param aoDocument
	 *            Document Bean Object
	 * @param aoChannel
	 *            Channel Object
	 * @param asDocumentId
	 *            Document Id
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static void setLinkToApplicationForDocument(
			ActionRequest aoRequest, String asUserOrg,
			P8UserSession aoUserSession, Document aoDocument,
			Channel aoChannel, String asDocumentId) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setLinkToApplicationForDocument()");
		updateDocumentTableEntry(aoRequest, aoChannel, asDocumentId, asUserOrg);
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
				true);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		aoChannel.setData(ApplicationConstants.DOCUMENT_ID, asDocumentId);
		aoChannel.setData(ApplicationConstants.DOCS_TYPE,
				aoDocument.getDocType());
		aoChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
				loHmDocReqProps);
		TransactionManager
				.executeTransaction(aoChannel, "saveProperties_filenet",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setLinkToApplicationForDocument()");
	}

	/**
	 * This method updates Document ID in DB on uploading a Document with same
	 * name and type This Method was modified for release 2.6.0 to fix defect
	 * 5661
	 * <ul>
	 * <li>1. New document id is been passed in the method</li>
	 * <li>2.Fetches old document id from request .</li>
	 * <li>3. Updates the new document id in the document table</li>
	 * <li>Execute <b>updateOldDocumentId</b> transaction to update Document ID
	 * in DB</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an ActionRequest object
	 * @param aoChannel
	 *            a Channel object
	 * @param asNewDocumentId
	 *            a string value containing new document id
	 * @param asOrgId
	 *            a string value containing organization id
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	private static void updateDocumentTableEntry(ActionRequest aoRequest,
			Channel aoChannel, String asNewDocumentId, String asOrgId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.updateDocumentTableEntry()");
		// 5661 fix starts. Fetching old document id form request
		String lsOldDocumentId = (String) aoRequest
				.getParameter(HHSConstants.OLD_DOCUMENT_ID_REQ);
		// 5661 fix ends
		Map<String, String> loDocumentIdMap = new HashMap<String, String>();
		loDocumentIdMap.put(HHSR5Constants.OLD_DOCUMENT_ID, lsOldDocumentId);
		loDocumentIdMap.put(HHSR5Constants.NEW_DOCUMENT_ID, asNewDocumentId);
		loDocumentIdMap.put(HHSR5Constants.AS_ORG_ID, asOrgId);
		aoChannel.setData(HHSR5Constants.DOCUMENT_ID_MAP, loDocumentIdMap);
		TransactionManager.executeTransaction(aoChannel, "updateOldDocumentId",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.updateDocumentTableEntry()");
	}

	/**
	 * This method will set property bean for uploading document
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoPropertMap
	 *            a map containing document properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setPropertyBeanForFileUpload(ActionRequest aoRequest,
			Document aoDocument, HashMap aoPropertMap) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setPropertyBeanForFileUpload()");
		List<DocumentPropertiesBean> loDocumentPropsBeans = aoDocument
				.getDocumentProperties();
		Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans
				.iterator();
		Map<String, Object> loMap = (Map<String, Object>) aoRequest
				.getPortletSession().getAttribute(
						HHSR5Constants.PROPERTY_MAP_INFO,
						PortletSession.APPLICATION_SCOPE);
		while (loDocPropsIt.hasNext()) {
			DocumentPropertiesBean loDocProps = loDocPropsIt.next();
			if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN
					.equalsIgnoreCase(loDocProps.getPropertyType())) {
				if (null != aoRequest.getParameter(loDocProps.getPropertyId())) {
					if (HHSConstants.ON.equalsIgnoreCase(aoRequest
							.getParameter(loDocProps.getPropertyId()))) // request.getParameter("accidentalCover").equals("checked"))
					{
						aoPropertMap
								.put(loDocProps.getPropSymbolicName(), true);
					} else {
						aoPropertMap.put(loDocProps.getPropSymbolicName(),
								false);
					}
				} else {
					if (null != loMap.get(loDocProps.getPropertyId())
							&& HHSConstants.ON.equalsIgnoreCase(loMap.get(
									loDocProps.getPropertyId()).toString())) // request.getParameter("accidentalCover").equals("checked"))
					{
						aoPropertMap
								.put(loDocProps.getPropSymbolicName(), true);
					} else {
						aoPropertMap.put(loDocProps.getPropSymbolicName(),
								false);
					}
				}

			} else {
				if (null != aoRequest.getParameter(loDocProps.getPropertyId())) {
					aoPropertMap.put(loDocProps.getPropSymbolicName(),
							aoRequest.getParameter(loDocProps.getPropertyId()));
				} else {
					aoPropertMap.put(loDocProps.getPropSymbolicName(),
							(String) loMap.get(loDocProps.getPropertyId()));
				}

			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setPropertyBeanForFileUpload()");
	}

	/**
	 * <ul>
	 * <li>This method will check whether the entered date is valid date or not</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoInsertPropMap
	 *            HashMap Object
	 * @param asNextExpectedDocType
	 *            String Object
	 * @return double Object
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	@SuppressWarnings("rawtypes")
	public static double validatePeriodCoveredDateChar500(
			HashMap aoInsertPropMap, String asNextExpectedDocType)
			throws ApplicationException {
		int liStartMonth = 0;
		int liStartYear = 0;
		int liEndMonth = 0;
		int liEndYear = 0;
		Calendar loCurrentCalendar = null;
		Calendar loStartCalendar = null;
		Calendar loEndCalendar = null;
		double ldMonthGap = 0;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validatePeriodCoveredDateChar500()");
		if ((null != aoInsertPropMap
				.get(ApplicationConstants.PERIOD_COVER_FROM_YEAR) && !aoInsertPropMap
				.get(ApplicationConstants.PERIOD_COVER_FROM_YEAR).toString()
				.equals(HHSR5Constants.EMPTY_STRING))
				&& (null != aoInsertPropMap
						.get(ApplicationConstants.PERIOD_COVER_TO_YEAR) && !aoInsertPropMap
						.get(ApplicationConstants.PERIOD_COVER_TO_YEAR)
						.toString().equals(HHSR5Constants.EMPTY_STRING))) {
			liStartMonth = DocumentLapsingUtility
					.getMonth((String) aoInsertPropMap.get(
							ApplicationConstants.PERIOD_COVER_FROM_MONTH)
							.toString());
			liEndMonth = DocumentLapsingUtility
					.getMonth((String) aoInsertPropMap.get(
							ApplicationConstants.PERIOD_COVER_TO_MONTH)
							.toString());
			liStartYear = Integer.parseInt((String) aoInsertPropMap.get(
					ApplicationConstants.PERIOD_COVER_FROM_YEAR).toString());
			liEndYear = Integer.parseInt((String) aoInsertPropMap.get(
					ApplicationConstants.PERIOD_COVER_TO_YEAR).toString());
			loStartCalendar = new GregorianCalendar(liStartYear, liStartMonth,
					1);
			loEndCalendar = new GregorianCalendar(liEndYear, liEndMonth, 1);
			loCurrentCalendar = new GregorianCalendar();
			ldMonthGap = DateUtil.calculateDateDifference(
					loStartCalendar.getTime(), loEndCalendar.getTime());
			if ((null == asNextExpectedDocType || asNextExpectedDocType.trim()
					.equals(HHSR5Constants.EMPTY_STRING))
					&& (loStartCalendar.after(loCurrentCalendar) || loEndCalendar
							.after(loCurrentCalendar))) {
				ApplicationException loCustomExp = new ApplicationException(
						PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.FUTURE_DATE_VALIDATION));
				throw loCustomExp;
			} else if ((null == asNextExpectedDocType || asNextExpectedDocType
					.trim().equals(HHSR5Constants.EMPTY_STRING))
					&& (ldMonthGap < 0 || ldMonthGap > Double.valueOf("12"))) {
				ApplicationException loCustomExp = new ApplicationException(
						PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								"PERIOD_COVERED_VALIDATION"));
				throw loCustomExp;
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validatePeriodCoveredDateChar500()");
		return ldMonthGap;
	}

	/**
	 * This method will validate CHAR500 document and uploads the same to
	 * filenet
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @param aoPropertyMap
	 *            a map containing document properties
	 * @param lbIsShortFiling
	 *            a boolean value of short filing
	 * @return a string value of uploaded document Id
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String validateAndUploadChar500(ActionRequest aoRequest,
			ActionResponse aoResponse, HashMap aoPropertyMap,
			boolean lbIsShortFiling) throws ApplicationException, IOException {
		Channel loChannel = new Channel();
		HashMap loInsertPropMap = new HashMap();
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		String lsDocumentId = HHSR5Constants.EMPTY_STRING;
		File loFile = null;
		FileInputStream loStreamUploadFile = null;
		P8UserSession loUserSession = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateAndUploadChar500()");
		try {
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT,
					PortletSession.APPLICATION_SCOPE);
			String lsUserEmailId = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_EMAIL_ID,
					PortletSession.APPLICATION_SCOPE);
			Document loDocument = (Document) ApplicationSession.getAttribute(
					aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			List loDocPropsList = loDocument.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocPropsList
					.iterator();
			Timestamp loCureentTimeSQL = new Timestamp(
					System.currentTimeMillis());
			loFile = new File(loDocument.getFilePath());

			// 1828 : - check PE connection before uploading document
			VWSession loVWSession = filenetConnection
					.getPESession(loUserSession);
			if (null == loVWSession) {
				throw new ApplicationException(
						"FileNet PE is down. New filing not possible");
			}
			loVWSession = null;

			while (loDocPropsIt.hasNext()) {
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				loInsertPropMap.put(loDocProps.getPropSymbolicName(),
						aoPropertyMap.get(loDocProps.getPropSymbolicName()));
			}

			// create document for char500
			loStreamUploadFile = new FileInputStream(loFile);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loDataMap = uploadDocument(loChannel, loStreamUploadFile,
					aoPropertyMap, false, false);
			lsDocumentId = (String) loDataMap.get(HHSConstants.DOC_ID);
			loStreamUploadFile.close();
			fileUploadChar500WorkFlowLaunch(loChannel, aoRequest, aoResponse,
					loUserSession, aoPropertyMap, lsUserOrg, lsUserId,
					lsDocumentId, loDocument.getDocType(), lbIsShortFiling,
					loCureentTimeSQL, loDocument.getDocName(), lsUserEmailId);

		}
		// 1828 : added the below exceptions handling and code for deleting the
		// uploaded error document
		catch (ApplicationException aoEx) {
			if ((null != lsDocumentId)
					&& !(lsDocumentId
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))) {
				// delete document from CE
				Channel loDelChannel = new Channel();
				loDelChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);
				loDelChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
				TransactionManager.executeTransaction(loDelChannel,
						"deleteFilenetDocumentById",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loDelChannel = null;
			}
			throw aoEx;
		} catch (Exception aoExp) {
			if ((null != lsDocumentId)
					&& !(lsDocumentId
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))) {
				// delete document from CE
				Channel loDelChannel = new Channel();
				loDelChannel.setData(HHSConstants.AO_FILENET_SESSION,
						loUserSession);
				loDelChannel.setData(HHSConstants.DOC_ID, lsDocumentId);
				TransactionManager.executeTransaction(loDelChannel,
						"deleteFilenetDocumentById",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loDelChannel = null;
			}
			throw new ApplicationException(aoExp.getMessage(), aoExp);
		} finally {
			try {
				if (null != loStreamUploadFile) {
					loStreamUploadFile.close();
				}
			} catch (IOException aoIoEx) {
				LOG_OBJECT
						.Error("Exception occured in FileNetOperationsUtils: validateAndUploadChar500 method::",
								aoIoEx);
				throw new ApplicationException(aoIoEx.getMessage(), aoIoEx);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validatePeriodCoveredDateChar500()");
		return lsDocumentId;
	}

	/**
	 * This method will launch work flow for CHAR500 document during document
	 * upload
	 * 
	 * @param aoChannel
	 *            a Channel Object
	 * @param aoRequest
	 *            an ActionRequest Object
	 * @param aoResponse
	 *            an ActionResponse Object
	 * @param aoUserSession
	 *            a P8UserSession Object
	 * @param aoPropertyMap
	 *            HashMap Object
	 * @param asUserOrg
	 *            a String Object containing User Org
	 * @param asUserId
	 *            a String Object containing UserId
	 * @param asDocumentId
	 *            a String Object containing Document Id
	 * @param asDocumentType
	 *            a String Object containing Document Type
	 * @param abIsShortFiling
	 *            a boolean Object
	 * @param aoCureentTimeSQL
	 *            a java.sql.Timestamp Object
	 * @param asDocTitle
	 *            a String Object containing Document Title
	 * @param asUserEmailId
	 *            a String Object containing user Email Id
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void fileUploadChar500WorkFlowLaunch(Channel aoChannel,
			ActionRequest aoRequest, ActionResponse aoResponse,
			P8UserSession aoUserSession, HashMap aoPropertyMap,
			String asUserOrg, String asUserId, String asDocumentId,
			String asDocumentType, boolean abIsShortFiling,
			Timestamp aoCureentTimeSQL, String asDocTitle, String asUserEmailId)
			throws ApplicationException {
		String lsWobNo = null;
		String lsDocType = HHSR5Constants.EMPTY_STRING;
		String lsProcStatus = null;
		Calendar loDoclapsingExpireCal = new GregorianCalendar();
		Calendar loCurrentDateCal = new GregorianCalendar();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.fileUploadChar500WorkFlowLaunch()");
		try {
			String lsNextExpectedDocType = getNextExpectedDocType(asUserOrg);
			if (null != lsNextExpectedDocType
					&& !lsNextExpectedDocType.trim().equals(
							HHSR5Constants.EMPTY_STRING)) {
				fileUploadChar500WorkFlowLaunchFinal(aoChannel, aoResponse,
						aoUserSession, aoPropertyMap, asUserOrg, asUserId,
						asDocumentId, asDocumentType, abIsShortFiling,
						aoCureentTimeSQL, asDocTitle, asUserEmailId, lsWobNo,
						lsDocType, lsProcStatus, loDoclapsingExpireCal,
						loCurrentDateCal);
			}
		} catch (Exception aoEx) {
			throw new ApplicationException("Error Occured Uploading CHAR500",
					aoEx);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.fileUploadChar500WorkFlowLaunch()");
	}

	/**
	 * This method will launch work flow for CHAR500 document during document
	 * upload
	 * 
	 * <ul>
	 * <li>Execute <b>getDocTypeAndWorkFlowID_DB</b> transaction to update
	 * Document ID in DB</li>
	 * <li>Execute <b>terminatePreviousWorkFlow</b> transaction to update
	 * Document ID in DB</li>
	 * <li>Execute <b>launchWorkflow</b> transaction to update Document ID in DB
	 * </li>
	 * <li>Execute <b>fileupload_char500</b> transaction to update Document ID
	 * in DB</li>
	 * <li>Execute <b>deleteShortFilingChar500_DB</b> transaction to update
	 * Document ID in DB</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 *            a channel object
	 * @param aoResponse
	 *            an Action Response object
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param aoPropertyMap
	 *            a map containing document properties
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param asUserId
	 *            a string value of user Id
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param asDocumentType
	 *            a string value of document type
	 * @param abIsShortFiling
	 *            a boolean value of short filing
	 * @param aoCureentTimeSQL
	 *            a Timestamp object
	 * @param asDocTitle
	 *            a string value of document title
	 * @param asUserEmailId
	 *            a string value of user email Id
	 * @param lsWobNo
	 *            a string value of workflow number
	 * @param lsDocType
	 *            a string value of doc type
	 * @param lsProcStatus
	 *            a string value of Procurement status
	 * @param loDoclapsingExpireCal
	 *            a string value
	 * @param loCurrentDateCal
	 *            a string value of current date
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	private static void fileUploadChar500WorkFlowLaunchFinal(Channel aoChannel,
			ActionResponse aoResponse, P8UserSession aoUserSession,
			HashMap aoPropertyMap, String asUserOrg, String asUserId,
			String asDocumentId, String asDocumentType,
			boolean abIsShortFiling, Timestamp aoCureentTimeSQL,
			String asDocTitle, String asUserEmailId, String lsWobNo,
			String lsDocType, String lsProcStatus,
			Calendar loDoclapsingExpireCal, Calendar loCurrentDateCal)
			throws ApplicationException {
		String lsLawType;
		Date loDoclapsingExpireDate;
		aoChannel.setData(HHSR5Constants.AS_ORG_ID, asUserOrg);
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.fileUploadChar500WorkFlowLaunchFinal()");
		TransactionManager.executeTransaction(aoChannel, "getLawType_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		lsLawType = (String) aoChannel.getData(HHSR5Constants.AS_LAW_TYPE);
		aoChannel.setData(HHSConstants.PROVIDER_ID, asUserOrg);
		TransactionManager.executeTransaction(aoChannel,
				"getDocTypeAndWorkFlowID_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		HashMap<String, Object> loDocTypeMap = (HashMap<String, Object>) aoChannel
				.getData("resultMap");
		if (null != loDocTypeMap) {
			lsWobNo = (String) loDocTypeMap.get(HHSR5Constants.WORKFLOW_ID);
			lsDocType = (String) loDocTypeMap
					.get(P8Constants.PROPERTY_CE_DOCUMENT_TYPE);
			lsProcStatus = (String) loDocTypeMap
					.get(HHSR5Constants.PROC_STATUS);
		}

		Map loDocLapsingMasterMap = DocumentLapsingUtility
				.calculateDueDateonDocumentUpload(
						asDocumentType,
						(String) aoPropertyMap
								.get(ApplicationConstants.PERIOD_COVER_FROM_MONTH),
						Integer.valueOf((String) aoPropertyMap
								.get(ApplicationConstants.PERIOD_COVER_FROM_YEAR)),
						(String) aoPropertyMap
								.get(ApplicationConstants.PERIOD_COVER_TO_MONTH),
						Integer.valueOf((String) aoPropertyMap
								.get(ApplicationConstants.PERIOD_COVER_TO_YEAR)),
						lsLawType, abIsShortFiling);

		if ((null != lsWobNo && !lsWobNo.isEmpty())
				&& (null != lsProcStatus && lsProcStatus
						.equalsIgnoreCase(HHSConstants.BULK_UPLOAD_FILE_STATUS_IN_PROGRESS))) {
			aoChannel.setData(ApplicationConstants.WORKFLOW_ID, lsWobNo);
			aoChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID,
					asUserId);
			TransactionManager.executeTransaction(aoChannel,
					"terminatePreviousWorkFlow",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			boolean lbUpdateStatus = (Boolean) aoChannel
					.getData(HHSR5Constants.UPDATE_STATUS);
			if (!lbUpdateStatus) {
				aoResponse.setRenderParameter(ApplicationConstants.ACTION,
						ApplicationConstants.ERROR);
			}
			loDocLapsingMasterMap.put(
					P8Constants.PROPERTY_PE_LAST_UPLOADED_DOC_TYPE, lsDocType);
			if (loDocTypeMap != null) {
				loDocLapsingMasterMap.put(
						P8Constants.PROPERTY_PE_AFTER_SHORT_FILING,
						(String) loDocTypeMap
								.get(HHSR5Constants.IS_SHORT_FILING));
			}
		} else {
			if (loDocTypeMap != null) {
				loDocLapsingMasterMap.put(
						P8Constants.PROPERTY_PE_LAST_UPLOADED_DOC_TYPE,
						(String) loDocTypeMap
								.get(P8Constants.PROPERTY_CE_DOCUMENT_TYPE));
			}
			loDocLapsingMasterMap.put(
					P8Constants.PROPERTY_PE_AFTER_SHORT_FILING,
					ApplicationConstants.FALSE);
		}
		List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.PROV_LIST);
		// launch workflow for char500
		setAttributesToLaunchWorkFlow(asUserOrg, asDocumentId, abIsShortFiling,
				asDocTitle, asUserId, loDocLapsingMasterMap, loProviderBeanList);

		aoChannel.setData(HHSConstants.AO_USER_SESSION, aoUserSession);
		aoChannel.setData(HHSConstants.WORK_FLOW_NAME,
				P8Constants.PROPERTY_NEW_FILING_WORKFLOW_NAME);
		aoChannel.setData(HHSConstants.LAUNCH_WORKFLOW_MAP,
				loDocLapsingMasterMap);
		TransactionManager.executeTransaction(aoChannel, "launchWorkflow",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		String lsWorkFlowVobNo = (String) aoChannel
				.getData(HHSR5Constants.LAUNCH_WORKFLOW_OUTPUT);
		// insert and update Document Lapsing Details
		setPropertiesToUploadChar500(asUserOrg, asUserId, asDocumentId,
				asDocumentType, aoCureentTimeSQL, loDocLapsingMasterMap,
				lsWorkFlowVobNo);
		// set data for transaction table
		loDocLapsingMasterMap.put(HHSR5Constants.APPROVED_START_YEAR,
				(String) aoPropertyMap
						.get(ApplicationConstants.PERIOD_COVER_FROM_YEAR));
		loDocLapsingMasterMap.put(HHSR5Constants.APPROVED_END_YEAR,
				(String) aoPropertyMap
						.get(ApplicationConstants.PERIOD_COVER_TO_YEAR));
		loDocLapsingMasterMap.put(HHSR5Constants.APPROVED_START_MONTH,
				(String) aoPropertyMap
						.get(ApplicationConstants.PERIOD_COVER_FROM_MONTH));
		loDocLapsingMasterMap.put(HHSR5Constants.APPROVED_END_MONTH,
				(String) aoPropertyMap
						.get(ApplicationConstants.PERIOD_COVER_TO_MONTH));
		loDocLapsingMasterMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoChannel.setData(HHSR5Constants.AO_DOC_LAPSING_MAP,
				loDocLapsingMasterMap);
		TransactionManager.executeTransaction(aoChannel, "fileupload_char500",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		if (abIsShortFiling) {
			aoChannel.setData(HHSConstants.PROVIDER_ID_KEY, asUserOrg);
			TransactionManager.executeTransaction(aoChannel,
					"deleteShortFilingChar500_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		// Update Provider status
		loDoclapsingExpireDate = (Date) loDocLapsingMasterMap
				.get(HHSR5Constants.SQL_DUE_DATE);
		loDoclapsingExpireCal.setTime(loDoclapsingExpireDate);
		if (loDoclapsingExpireCal.after(loCurrentDateCal)) {
			getAndUpdateProviderStatus(asUserEmailId, asUserOrg);
		}
		deleteDueDateReminder(asUserOrg);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.fileUploadChar500WorkFlowLaunchFinal()");
	}

	/**
	 * This method will set properties in map for CHAR500 document while
	 * uploading
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param asUserId
	 *            a string value of user Id
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param asDocumentType
	 *            a string value of document type
	 * @param aoCureentTimeSQL
	 *            a Timestamp object
	 * @param aoDocLapsingMasterMap
	 *            a map containing doc lapsing master details
	 * @param asWorkFlowVobNo
	 *            a string value of work flow number
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setPropertiesToUploadChar500(String asUserOrg,
			String asUserId, String asDocumentId, String asDocumentType,
			Timestamp aoCureentTimeSQL, Map aoDocLapsingMasterMap,
			String asWorkFlowVobNo) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setPropertiesToUploadChar500()");
		aoDocLapsingMasterMap.put(HHSConstants.PROVIDER_ID, asUserOrg);
		aoDocLapsingMasterMap.put(HHSConstants.DOCTYPE, asDocumentType);
		aoDocLapsingMasterMap.put(ApplicationConstants.DOCUMENT_VAULT_USER,
				asUserId);
		aoDocLapsingMasterMap.put(ApplicationConstants.OPERATION_TIME,
				aoCureentTimeSQL);
		aoDocLapsingMasterMap.put(HHSConstants.DOC_ID, asDocumentId);
		aoDocLapsingMasterMap.put(ApplicationConstants.WORKFLOW_ID,
				asWorkFlowVobNo);
		aoDocLapsingMasterMap.put(HHSR5Constants.PROCUREMENT_STATUS,
				ApplicationConstants.STATUS_IN_PROGRESS);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE,
				aoDocLapsingMasterMap.get(HHSR5Constants.SQL_DUE_DATE));
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setPropertiesToUploadChar500()");
	}

	/**
	 * This method validated whether the filter request from Document Vault
	 * Filter is Authentic or not
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            Action Request Object
	 * @param aoUserSession
	 *            P8 User Session Object
	 * @param asOrgType
	 *            a String object containing Organization type
	 * @param asDocCategory
	 *            a String object containing document category
	 * @param asDocType
	 *            a String object containing document type
	 * @param asAgencyName
	 *            a String object containing Agency Name
	 * @param asProviderName
	 *            a String object containing Provider Name
	 * @param asSampleCategory
	 *            a String object containing sample category
	 * @param asSampleType
	 *            a String object containing sample type
	 * @param asModifiedFrom
	 *            a String object containing modified from date
	 * @param asModifiedTo
	 *            a String object containing modified to date
	 * @param asUserOrg
	 *            a String object containing name of user organization
	 * @param asAccessType
	 *            a String object containing access type
	 * @return True if valid request else False
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean validateFilterRequest(ActionRequest aoRequest,
			P8UserSession aoUserSession, String asOrgType,
			String asDocCategory, String asDocType, String asAgencyName,
			String asProviderName, String asSampleCategory,
			String asSampleType, String asModifiedFrom, String asModifiedTo,
			String asUserOrg, String asAccessType) throws ApplicationException {
		boolean lbValidRequest = false;
		ArrayList loCategoryList = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateFilterRequest()");
		loCategoryList = (ArrayList) getDocCategoryList(asOrgType);
		loCategoryList.add(0, HHSR5Constants.EMPTY_STRING);
		if (null != asDocCategory && !asDocCategory.isEmpty()
				&& loCategoryList.contains(asDocCategory)) {
			lbValidRequest = validateFilterRequestFinal(asOrgType,
					asDocCategory, asDocType, asSampleCategory, asSampleType);
		}
		if (null != asProviderName
				&& !asProviderName.isEmpty()
				&& StringEscapeUtils
						.unescapeJava(
								getSharedAgencyProviderList(
										(List<ProviderBean>) BaseCacheManagerWeb
												.getInstance()
												.getCacheObject(
														ApplicationConstants.PROV_LIST),
										aoUserSession,
										P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID,
										asUserOrg).toString()).contains(
								StringEscapeUtils.unescapeJava(asProviderName))) {
			lbValidRequest = true;
		}
		if (null != asAgencyName
				&& !asAgencyName.isEmpty()
				&& StringEscapeUtils
						.unescapeJava(
								getSharedAgencyProviderList(
										(List<ProviderBean>) BaseCacheManagerWeb
												.getInstance()
												.getCacheObject(
														ApplicationConstants.PROV_LIST),
										aoUserSession,
										P8Constants.PROPERTY_CE_SHARED_AGENCY_ID,
										asUserOrg).toString()).contains(
								StringEscapeUtils.unescapeJava(asAgencyName))) {
			lbValidRequest = true;
		}
		if (null != asModifiedFrom && !asModifiedFrom.isEmpty()) {
			try {
				DateUtil.getDate(aoRequest
						.getParameter(ApplicationConstants.MODIFIED_FROM));
				lbValidRequest = true;
			} catch (Exception aoEx) {
				lbValidRequest = false;
			}
		}
		if (null != asModifiedTo && !asModifiedTo.isEmpty()) {
			try {
				DateUtil.getDate(asModifiedTo);
				lbValidRequest = true;
			} catch (Exception aoEx) {
				lbValidRequest = false;
			}
		}
		if ((null != asOrgType)
				&& (null == asModifiedTo || asModifiedTo.isEmpty())
				&& (null == asModifiedFrom || asModifiedFrom.isEmpty())
				&& (null == asDocCategory || asDocCategory.isEmpty())
				|| (!ApplicationConstants.CITY_ORG.equalsIgnoreCase(asOrgType) && (null == asDocType || asDocType
						.isEmpty()))) {
			if ((null == asProviderName || asProviderName.isEmpty())
					&& (null == asAgencyName || asAgencyName.isEmpty())) {
				lbValidRequest = true;
			} else if (null != asAccessType && !asAccessType.isEmpty()) {
				lbValidRequest = true;
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validateFilterRequest()");
		return lbValidRequest;
	}

	/**
	 * This method validates whether the filter request from Document Vault
	 * Filter is Authentic or not
	 * 
	 * @param asOrgType
	 *            a String object containing organization type
	 * @param asDocCategory
	 *            a String object containing document category
	 * @param asDocType
	 *            a String object containing document type
	 * @param asSampleCategory
	 *            a String object containing sample category
	 * @param asSampleType
	 *            SampleType a String object containing sample type
	 * @return lbValidRequest boolean
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	private static boolean validateFilterRequestFinal(String asOrgType,
			String asDocCategory, String asDocType, String asSampleCategory,
			String asSampleType) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateFilterRequestFinal()");
		boolean lbValidRequest;
		lbValidRequest = true;
		List<String> lsDocTypeList = null;
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		lsDocTypeList = getDoctypesFromXML(loXMLDoc, asDocCategory, asOrgType,
				null);
		if (null != lsDocTypeList && null != asDocType && !asDocType.isEmpty()
				&& lsDocTypeList.contains(asDocType)) {
			lbValidRequest = true;
		} else if (null == asDocType || asDocType.isEmpty()) {
			lbValidRequest = true;
		} else {
			lbValidRequest = false;
		}
		if (ApplicationConstants.DOC_SAMPLE.equals(asDocCategory)) {
			if (null == asSampleCategory || asSampleCategory.isEmpty()) {
				lbValidRequest = true;
			} else {
				List lsSampleCategoryList = null;
				lsSampleCategoryList = getSampleCategoryList();
				if (null != lsSampleCategoryList
						&& lsSampleCategoryList.contains(asSampleCategory)) {
					lbValidRequest = true;
					if (null != asSampleType && !asSampleType.isEmpty()) {
						List lsSampleTypeList = null;
						lsSampleTypeList = getSampleTypeList(asSampleCategory);
						if (null != lsSampleTypeList
								&& lsSampleTypeList.contains(asSampleType)) {
							lbValidRequest = true;
						}
					} else if (null == asSampleType || asSampleType.isEmpty()) {
						lbValidRequest = true;
					} else {
						lbValidRequest = false;
					}
				} else {
					lbValidRequest = false;
				}
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validateFilterRequestFinal()");
		return lbValidRequest;
	}

	/**
	 * This method will get list of documents for filter parameters
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * <li>Method Updated in Release 5</li>
	 * </ul>
	 * <ul>
	 * <li>
	 * calls 'reInitializePageIterator'</li>
	 * <li>
	 * calls 'setFilteredProperties'</li>
	 * <li>
	 * calls 'setReqSessionParameter'</li>
	 * <li>
	 * calls 'generateDocumentBean'</li>
	 * 
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void actionFilterDocument(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		HashMap<String, String> loReqProps = new HashMap<String, String>();
		HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
		String lsDocType = null;
		boolean lbIncludeFilter = true;
		String lsUserOrg = null;
		String lsCustomUserOrg = null;
		String lsAgencyName = null;
		boolean lsFindOrgDocFlag = false;
		boolean lbOpenFolderFlag = false;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.actionFilterDocument()");
		try {
			PortletSession loSession = aoRequest.getPortletSession();
			// loSession.removeAttribute(HHSR5Constants.FOLDER_ID);
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsAccessType = (String) aoRequest
					.getAttribute("SharedReqType");
			String lsOrgName = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_NAME,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsJspName = aoRequest.getParameter(HHSR5Constants.JSP_NAME);
			String lsParentNodeReqParam = (String) loSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER,
					PortletSession.APPLICATION_SCOPE);
			String lsModifiedFrom = aoRequest
					.getParameter(HHSR5Constants.SUBMITTED_FROM);
			String lsModifiedTo = aoRequest
					.getParameter(HHSR5Constants.SUBMITTED_TO);
			if (StringUtils.isEmpty(lsModifiedFrom)) {
				lsModifiedFrom = aoRequest
						.getParameter(HHSR5Constants.MODIFIED_FROM_5);
			}
			if (StringUtils.isEmpty(lsModifiedTo)) {
				lsModifiedTo = aoRequest
						.getParameter(HHSR5Constants.MODIFIED_TO_5);
			}
			String lsSubmittedFrom = aoRequest
					.getParameter(HHSR5Constants.MODIFIED_FROM_2);
			String lsSubmittedTo = aoRequest
					.getParameter(HHSR5Constants.MODIFIED_TO_2);
			// Shared date range
			String lsSharedFrom = aoRequest
					.getParameter(HHSR5Constants.MODIFIED_FROM_4);
			String lsSharedTo = aoRequest
					.getParameter(HHSR5Constants.MODIFIED_TO_4);
			// Shared date range end
			String lsProviderName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.PROVIDER);
			String lsSharedSearchDocument = (String) aoRequest
					.getAttribute(HHSR5Constants.SHARED_SEARCH_DOC);
			// R5 starts
			String lsDocName = aoRequest
					.getParameter(ApplicationConstants.DOC_NAME);
			String lsSharedWith = aoRequest
					.getParameter(HHSR5Constants.SHARED_WITH);
			String lsFilter = aoRequest.getParameter(HHSR5Constants.IS_FILTER);
			String lsSharedFlag = aoRequest
					.getParameter(HHSConstants.SHARED_FLAG);
			String lsShare = aoRequest
					.getParameter(ApplicationConstants.SHARED_STATUS);
			String lsSampleCategory = aoRequest
					.getParameter(ApplicationConstants.FILTER_SAMPLE_CATEGORY);
			String lsSampleType = aoRequest
					.getParameter(ApplicationConstants.FILTER_SAMPLE_TYPE);
			String lsSortByReqParam = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER);
			String lsSortTypeReqParam = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER);
			// Added for selectVault
			String lsSelectVaultFlag = aoRequest
					.getParameter(HHSR5Constants.RENDER_JSP_NAME);
			// end
			if (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
				lsUserOrg = lsUserOrgType;
			} else {
				lsUserOrg = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
			}
			String[] loCheckedAgencyItems = aoRequest
					.getParameterValues(HHSR5Constants.AGENCY_CHECK_BOX);
			String lsDocCategory = aoRequest
					.getParameter(ApplicationConstants.FILTER_CATEGORY);
			if (null != lsJspName
					&& !lsJspName.isEmpty()
					&& lsJspName
							.equalsIgnoreCase(HHSR5Constants.RECYCLE_BIN_JSP_NAME)) {
				lsDocType = aoRequest.getParameter(HHSR5Constants.DOC_TYPE_REC);
			} else if (null != lsJspName
					&& !lsJspName.isEmpty()
					&& lsJspName
							.equalsIgnoreCase(HHSR5Constants.SHARED_SEARCH_AGENCY)) {
				lsDocType = aoRequest
						.getParameter(HHSR5Constants.DOC_TYPE_CITY);
				lsFindOrgDocFlag = true;
			} else if (null != lsSelectVaultFlag
					&& !lsSelectVaultFlag.isEmpty()
					&& lsSelectVaultFlag
							.equalsIgnoreCase(HHSConstants.ADD_DOC_FROM_VAULT)) {
				lsDocType = aoRequest.getParameter(HHSConstants.DOCTYPE);
			} else {
				lsDocType = aoRequest
						.getParameter(HHSR5Constants.DOC_TYPE_CITY);
			}
			String lsSharedOrgId = getProviderId(
					(List) BaseCacheManagerWeb.getInstance().getCacheObject(
							ApplicationConstants.PROV_LIST), lsSharedWith);
			if (null != lsSharedOrgId && !lsSharedOrgId.isEmpty()
					&& lsSharedOrgId.equalsIgnoreCase(lsSharedWith)) {
				lsSharedOrgId = getAgencyId(
						(TreeSet<String>) BaseCacheManagerWeb.getInstance()
								.getCacheObject(
										ApplicationConstants.AGENCY_LIST),
						lsSharedWith);
			}
			if (null != loCheckedAgencyItems && loCheckedAgencyItems.length > 0) {
				lsSharedFlag = HHSConstants.STRING_TRUE;
			}
			String lsLinkedEntity = "";
			// ends
			if (lsSharedSearchDocument != null
					&& !lsSharedSearchDocument
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
				lsProviderName = lsSharedSearchDocument;
			}
			// R4 Homepage changes
			if (lsProviderName != null
					&& lsProviderName.contains(ApplicationConstants.TILD)) {
				lsUserOrgType = (String) lsProviderName
						.split(ApplicationConstants.TILD)[1];
				lsProviderName = (String) lsProviderName
						.split(ApplicationConstants.TILD)[0];
			} else if (lsProviderName != null
					&& !lsProviderName
							.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)
					&& lsUserOrgType
							.equalsIgnoreCase(ApplicationConstants.CITY_ORG)
					&& lsAccessType != null) {
				lsUserOrgType = ApplicationConstants.PROVIDER_ORG;
			} else if (null != PortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.DOCUMENT_ORGINATOR)
					&& !PortalUtil.parseQueryString(aoRequest,
							HHSR5Constants.DOCUMENT_ORGINATOR).isEmpty()) {
				lsUserOrgType = PortalUtil.parseQueryString(aoRequest,
						HHSR5Constants.DOCUMENT_ORGINATOR);
			}

			String[] loRadioArray = aoRequest
					.getParameterValues(ApplicationConstants.SHARED_STATUS);

			if (lsAccessType != null
					&& lsAccessType.equalsIgnoreCase(HHSConstants.AGENCY)) {
				lsAgencyName = lsOrgName;
			} else {
				lsAgencyName = aoRequest
						.getParameter(ApplicationConstants.AGENCY);
			}
			if (null != lsSortByReqParam
					&& !lsSortTypeReqParam
							.equalsIgnoreCase(P8Constants.STRING_SINGLE_SLASH)) {
				loFilterProps = (HashMap<String, Object>) aoRequest
						.getPortletSession().getAttribute(
								HHSConstants.FILENET_FILTER_MAP,
								PortletSession.PORTLET_SCOPE);
			}
			if (null != aoRequest.getAttribute("openFlag")
					&& (Boolean) aoRequest.getAttribute("openFlag")) {
				lsSortByReqParam = null;
				lsSortTypeReqParam = null;
				lbOpenFolderFlag = true;
			}
			boolean lbAuthenticFilterRequest = validateFilterRequest(aoRequest,
					loUserSession, lsUserOrgType, lsDocCategory, lsDocType,
					lsAgencyName, lsProviderName, lsSampleCategory,
					lsSampleType, lsModifiedFrom, lsModifiedTo, lsUserOrg,
					lsAccessType);
			if (!lbAuthenticFilterRequest) {
				throw new ApplicationException("Filter Changed at Runtime");
			}
			Document loDocument = new Document();
			reInitializePageIterator(loSession, loUserSession);
			setFilteredProperties(lsDocCategory, lsDocType, lsModifiedFrom,
					lsModifiedTo, lsProviderName, lsAgencyName,
					lsSampleCategory, lsSampleType, loDocument);
			setReqSessionParameter(loSession, null, lsSortByReqParam,
					lsSortTypeReqParam, null);
			if (null == loFilterProps || loFilterProps.isEmpty()) {
				loFilterProps = new HashMap<String, Object>();
				createAndSetFilteredProperties(lsDocCategory, lsDocType,
						lsModifiedFrom, lsModifiedTo, lsSampleCategory,
						lsSampleType, lsDocName, lsSharedOrgId, lsFilter,
						lsSubmittedFrom, lsSubmittedTo, lsSharedFlag,
						loCheckedAgencyItems, lsFindOrgDocFlag, loFilterProps,
						lsSharedFrom, lsSharedTo);
			}
			Boolean lsManageOrgFlag = (Boolean) aoRequest.getPortletSession()
					.getAttribute("homePageManageOrgFlag",
							PortletSession.APPLICATION_SCOPE);
			if (null != aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId",
					PortletSession.APPLICATION_SCOPE)
					&& null != lsManageOrgFlag && lsManageOrgFlag) {
				lsCustomUserOrg = (String) aoRequest.getPortletSession()
						.getAttribute("cityUserSearchProviderId",
								PortletSession.APPLICATION_SCOPE);
				String[] loTemp = lsCustomUserOrg
						.split(ApplicationConstants.TILD);
				lsUserOrgType = loTemp[1];
				lsUserOrg = loTemp[0];
				loFilterProps.put("ControllerName", "selectOrgnization");
				loFilterProps.put("CustomOrg", lsUserOrg);
				aoResponse.setRenderParameter(ApplicationConstants.SHARED_FLAG,
						"true");
				loFilterProps.put("presentOrgId", (String) loSession
						.getAttribute(
								ApplicationConstants.KEY_SESSION_ORG_TYPE,
								PortletSession.APPLICATION_SCOPE));
				// Adding two properies for defect 8150
				loFilterProps.put("presentOrg", (String) loSession
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE));
				loFilterProps.put("parentId",
						HHSPortalUtil.parseQueryString(aoRequest, "parentId"));

			}
			if (lsAccessType != null
					&& lsAccessType.equalsIgnoreCase(HHSConstants.PROVIDER)) {
				loFilterProps.put(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID,
						lsUserOrg);
				setDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType);
				getOrgTypeFilter(loFilterProps, lsProviderName, lsUserOrgType);

			} else if (lsAccessType != null
					&& lsAccessType
							.equalsIgnoreCase(ApplicationConstants.CITY_TYPE)) {
				setDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType);
				getOrgTypeFilter(loFilterProps, lsProviderName, lsUserOrgType);
			} else if (lsAccessType != null
					&& lsAccessType.equalsIgnoreCase(HHSConstants.AGENCY)) {
				setFilteredPropsForProviderAndAgency(lsDocCategory,
						lsProviderName, lsAgencyName, lsUserOrgType, lsUserOrg,
						loDocument, loFilterProps, lsAccessType);
			} else {
				setDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType);
				getOrgTypeFilter(loFilterProps, lsUserOrg, lsUserOrgType);
				setFilteredPropsForProviderAndAgency(lsDocCategory,
						lsProviderName, lsAgencyName, lsUserOrgType, lsUserOrg,
						loDocument, loFilterProps, lsAccessType);
			}
			if (null != loRadioArray) {
				setFilteredPropertiesForSharedStatus(loRadioArray,
						lsUserOrgType, lsUserOrg, loDocument, loFilterProps);
			}
			// Added for Release 5
			String lsFolderId = PortalUtil.parseQueryString(aoRequest,
					HHSR5Constants.FOLDER_ID);

			if (null != lsFolderId && !lsFolderId.isEmpty()
					&& lsFolderId.contains(HHSR5Constants.RECYCLE_BIN_ID)) {
				if (lsFolderId.contains(ApplicationConstants.TILD)) {
					String[] loTemp = lsFolderId
							.split(ApplicationConstants.TILD);
					lsFolderId = loTemp[0];
					if (loTemp.length > 1
							&& loTemp[1].contains(HHSConstants.HYPHEN)) {
						String[] loTemp1 = loTemp[1].split(HHSConstants.HYPHEN);
						lsUserOrgType = loTemp1[0];
						if (loTemp1.length > 1) {
							lsUserOrg = loTemp1[1];
						}
					}
				}

				aoResponse.setRenderParameter(HHSR5Constants.JSP_NAME,
						HHSR5Constants.RECYCLE_BIN_JSP_NAME);
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.JSP_NAME,
						HHSR5Constants.RECYCLE_BIN_JSP_NAME);
			}
			if (null == lsFolderId) {
				lsFolderId = (String) aoRequest.getPortletSession()
						.getAttribute(HHSR5Constants.FOLDER_ID);
			} else {
				aoRequest.getPortletSession().setAttribute(
						HHSR5Constants.FOLDER_ID, lsFolderId);
			}
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.SHARED_FLAG, lsSharedFlag);
			loChannel.setData(HHSR5Constants.JSP_NAME, lsFolderId);
			loChannel.setData("findOrgFlag", lsFindOrgDocFlag);
			loChannel.setData("folderExistCheck", lbOpenFolderFlag);
			loChannel.setData("jsp", lsJspName);
			loChannel.setData("entityType", HHSR5Constants.EMPTY_STRING);
			setOrderByParameter(loChannel, lsSortByReqParam, lsSortTypeReqParam);
			Boolean loDefaultFlag = false;
			if (null != loChannel.getData("defaultFlag")) {
				loDefaultFlag = (Boolean) loChannel.getData("defaultFlag");
			}

			if (loDefaultFlag) {
				aoRequest.getPortletSession().setAttribute(
						ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER,
						HHSR5Constants.ORGANIZATION_NAME,
						PortletSession.APPLICATION_SCOPE);
				aoRequest
						.getPortletSession()
						.setAttribute(
								ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER,
								"ASC", PortletSession.APPLICATION_SCOPE);
			}
			if (null != aoRequest.getParameter("organizationId")
					&& !aoRequest.getParameter("organizationId").isEmpty()
					&& !aoRequest.getParameter("organizationId")
							.equalsIgnoreCase("undefined")) {
				lsUserOrg = aoRequest.getParameter("organizationId");
				if (null != aoRequest.getParameter("organizationType")
						&& !aoRequest.getParameter("organizationType")
								.isEmpty()) {
					lsUserOrgType = aoRequest.getParameter("organizationType");
				}
				loFilterProps.put("ControllerName", "selectOrgnization");
				loFilterProps.put("CustomOrg", lsUserOrg);
				loFilterProps.put("presentOrgId", (String) loSession
						.getAttribute(
								ApplicationConstants.KEY_SESSION_ORG_TYPE,
								PortletSession.APPLICATION_SCOPE));
				// Adding two properties for defect 8150
				loFilterProps.put("presentOrg", (String) loSession
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE));
				loFilterProps.put("parentId",
						HHSPortalUtil.parseQueryString(aoRequest, "parentId"));

			}
			aoRequest.getPortletSession().setAttribute("currentFolderId",
					lsFolderId);
			String lsFolderPath = null;
			if (null == lsFolderId) {
				lsFolderPath = (String) aoRequest.getPortletSession()
						.getAttribute(HHSR5Constants.FOLDER_PATH);
			}

			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath,
					lsFolderId, lsUserOrgType, lsUserOrg);
			if (null != aoRequest.getParameter("searchFlag")
					&& aoRequest.getParameter("searchFlag").equalsIgnoreCase(
							"true")) {
				FileNetOperationsUtils.setPropFilter(loFilterProps, null,
						lsFolderId, lsUserOrgType, lsUserOrg);
			}
			// End Release 5
			if (null != aoRequest.getParameter(HHSR5Constants.MANAGE_FLAG)
					&& !aoRequest.getParameter(HHSR5Constants.MANAGE_FLAG)
							.isEmpty()
					&& (lsUserOrgType
							.equalsIgnoreCase(HHSConstants.USER_AGENCY) || lsUserOrgType
							.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))) {

				if (null != PortalUtil
						.parseQueryString(aoRequest, "parentFlag")
						&& PortalUtil.parseQueryString(aoRequest, "parentFlag")
								.equalsIgnoreCase("parentflag")) {
					loFilterProps.put(HHSR5Constants.CONTROLLER_NAME,
							HHSR5Constants.SELECT_ORGANIZATION);
				}
				loFilterProps.put(HHSR5Constants.CUSTOM_ORGANIZATION,
						aoRequest.getParameter(HHSR5Constants.MANAGE_FLAG));
			}
			if (null != aoRequest.getParameter(HHSR5Constants.DIV_ID)
					&& !aoRequest.getParameter(HHSR5Constants.DIV_ID).isEmpty()
					&& aoRequest.getParameter(HHSR5Constants.DIV_ID)
							.equalsIgnoreCase(
									HHSR5Constants.SELECT_ORGANIZATION)
					&& null != lsUserOrgType
					&& (lsUserOrgType
							.equalsIgnoreCase(HHSConstants.USER_AGENCY) || lsUserOrgType
							.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))) {
				if (null != aoRequest.getParameter(HHSR5Constants.PARENT_FLAG)
						&& aoRequest.getParameter(HHSR5Constants.PARENT_FLAG)
								.equalsIgnoreCase("parentflag")) {
					loFilterProps.put(HHSR5Constants.CONTROLLER_NAME,
							HHSR5Constants.SELECT_ORGANIZATION);
				}
				loChannel.setData("folderId", lsFolderId);
				HHSTransactionManager.executeTransaction(loChannel,
						"getOrgDetails", HHSR5Constants.TRANSACTION_ELEMENT_R5);
				String lsOrgId = (String) loChannel.getData("lsOrgId");
				loFilterProps.put("CustomOrg", lsOrgId);
				String lsOrgType = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
				loFilterProps.put("presentOrgId", lsOrgType);
				// Adding two prop for defect 8150
				loFilterProps.put("presentOrg", (String) loSession
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE));
				loFilterProps.put("parentId",
						HHSPortalUtil.parseQueryString(aoRequest, "parentId"));
			}
			loReqProps = setFilterAndRequiredParametersForSearch(aoRequest,
					aoResponse, loReqProps, lsDocType, loFilterProps,
					lsUserOrg, lsUserOrgType, lsSelectVaultFlag, lsDocCategory,
					lsFilter, loChannel);
			String lsLinkedItem = aoRequest.getParameter(HHSR5Constants.LINKED);
			if (null != lsLinkedItem && !lsLinkedItem.isEmpty()) {
				setFilterPropsForLinkedEntity(aoRequest, lsUserOrgType,
						loFilterProps, lsLinkedItem);
			}
			String lsUserId = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
							PortletSession.APPLICATION_SCOPE);
			// Added value of lbIncludeFilter - selectVault Release 5
			if ((HHSPortalUtil.parseQueryString(aoRequest,
					"sharedSearchOrgType") != null)
					&& !(HHSPortalUtil.parseQueryString(aoRequest,
							"sharedSearchOrgType").isEmpty())
					&& (null == lsSortByReqParam || lsSortTypeReqParam
							.equalsIgnoreCase(P8Constants.STRING_SINGLE_SLASH))) {
				loFilterProps.put("sharedSearchOrgType", HHSPortalUtil
						.parseQueryString(aoRequest, "sharedSearchOrgType"));
				loFilterProps.put("sharedSearchOrgId", HHSPortalUtil
						.parseQueryString(aoRequest, "sharedSearchOrgId"));
				String lsOrgNameMainScreen = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
				loFilterProps.put("HHSProviderID", lsOrgNameMainScreen);
				loFilterProps.put("PROVIDER_ID", HHSPortalUtil
						.parseQueryString(aoRequest, "sharedSearchOrgId"));
			}
			// Adding undefined check in Emergency Release 4.0.2
			else if (HHSPortalUtil.parseQueryString(aoRequest,
					"normalSearchOrgType") != null
					&& !HHSPortalUtil.parseQueryString(aoRequest,
							"normalSearchOrgType").isEmpty()
					&& !HHSPortalUtil.parseQueryString(aoRequest,
							"normalSearchOrgType")
							.equalsIgnoreCase("undefined")) {
				loFilterProps.put("PROVIDER_ID", HHSPortalUtil
						.parseQueryString(aoRequest, "normalSearchOrgId"));
			}
			aoRequest.getPortletSession().setAttribute(
					HHSConstants.FILENET_FILTER_MAP, loFilterProps,
					PortletSession.PORTLET_SCOPE);
			List loLstDocumentProps = getDocumentList(loChannel, lsDocType,
					loReqProps, loFilterProps, lbIncludeFilter, lsUserId);
			setReqRequestParameterAction(loSession, aoRequest, loUserSession,
					null, lsSortByReqParam, lsSortTypeReqParam,
					lsParentNodeReqParam);
			// End
			List<Document> loDocumentList = new ArrayList<Document>();
			// Release 5 Contract Restriction
			List<Document> loDocumentContractRestriction = (List<Document>) loChannel
					.getData(HHSR5Constants.LO_DOCUMENT_CONTRACT_RESTRICTION);
			// Release 5 Contract Restriction

			String lsUserOrgTypeCheck = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgCheck = null;
			if (lsUserOrgTypeCheck
					.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
				lsUserOrgCheck = lsUserOrgTypeCheck;
			} else {
				lsUserOrgCheck = (String) loSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
			}
			String lsShareStatus = generateDocumentBean(loLstDocumentProps,
					loDocumentList, lsUserOrgType, loUserSession,
					loDocumentContractRestriction, lsUserOrgCheck);
			ApplicationSession.setAttribute(loDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			ApplicationSession.setAttribute(loDocumentList, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_LIST);
			aoRequest.getPortletSession().setAttribute(
					ApplicationConstants.SESSION_DOCUMENT_LIST, loDocumentList,
					PortletSession.APPLICATION_SCOPE);
		}

		catch (ApplicationException aoAppex) {
			aoAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE,
					HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error(
					"Exception in P8ContentOperations.getDocsProperties()::",
					aoAppex);
			throw aoAppex;
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocProperties Method", aoEx);
			loAppex.addContextData(ApplicationConstants.PROPERTY_ERROR_CODE,
					HHSR5Constants.ERROR_E0001);
			LOG_OBJECT.Error("Error in getDocProperties Method", aoEx);
			throw loAppex;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.actionFilterDocument()");
	}

	/**
	 * This method is extracted in release 5 from actionFilterDocument method,
	 * this method will set the filter criteria when a user clicked the searhc
	 * button in filter screen
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoReqProps
	 *            required parameters
	 * @param asDocType
	 *            document type
	 * @param aoFilterProps
	 *            filter criteria map
	 * @param asUserOrg
	 *            user organization id
	 * @param asUserOrgType
	 *            user organization type
	 * @param asSelectVaultFlag
	 *            flag to identify if the search is from select vault screen
	 * @param asDocCategory
	 *            document Category
	 * @param asFilter
	 *            filter parameters
	 * @param aoChannel
	 *            channel object
	 * @return updated required parameters.
	 * @throws ApplicationException
	 */
	private static HashMap<String, String> setFilterAndRequiredParametersForSearch(
			ActionRequest aoRequest, ActionResponse aoResponse,
			HashMap<String, String> aoReqProps, String asDocType,
			HashMap aoFilterProps, String asUserOrg, String asUserOrgType,
			String asSelectVaultFlag, String asDocCategory, String asFilter,
			Channel aoChannel) throws ApplicationException {
		// Added for Release 5- selectVault
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterAndRequiredParametersForSearch()");
		if (null != asSelectVaultFlag
				&& !asSelectVaultFlag.isEmpty()
				&& asSelectVaultFlag
						.equalsIgnoreCase(HHSConstants.ADD_DOC_FROM_VAULT)) {
			// Added for defect# 7901
			String lsBusApp = aoRequest
					.getParameter(HHSR5Constants.SELECT_DOC_RELEASE);
			// End
			aoRequest.getPortletSession().removeAttribute(
					ApplicationConstants.ALERT_VIEW_PAGING_RECORDS);
			setFilterMapForAddDocsFromVault(aoReqProps, asUserOrg,
					aoFilterProps, aoChannel, lsBusApp);
			// Added for defect# 7368
			aoRequest.setAttribute(HHSR5Constants.DOC_TYPE, asDocType);
			aoResponse.setRenderParameter(HHSR5Constants.DOC_TYPE, asDocType);
			// End
			// lbIncludeFilter = false;
		} else {
			aoReqProps = requiredDocsProps(asUserOrgType, asDocCategory);
		}
		// End of Release 5
		if (null != aoFilterProps.get("folderPath")
				&& aoFilterProps.get("folderPath").toString()
						.contains("Recycle Bin")) {
			aoReqProps.remove("FolderName");
			aoReqProps.put("ORIGINAL_FOLDER_NAME AS FolderName", "FO");
			aoReqProps.put("FolderName AS FolderNameOld", "FO");

		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilterAndRequiredParametersForSearch()");
		return aoReqProps;
	}

	/**
	 * The method will set the filter map for linked entity
	 * 
	 * @param aoRequest
	 *            as ActionRequest
	 * @param asUserOrgType
	 *            as user organization Type
	 * @param aoFilterProps
	 *            as filter property map
	 * @param asLinkedItem
	 *            the linked item
	 */
	private static void setFilterPropsForLinkedEntity(ActionRequest aoRequest,
			String asUserOrgType, HashMap aoFilterProps, String asLinkedItem) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterPropsForLinkedEntity()");
		setKeyValueInMap(aoFilterProps,
				HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID, asLinkedItem);
		setKeyValueInMap(aoFilterProps, HHSConstants.PROCUREMENT_ID,
				aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		setKeyValueInMap(aoFilterProps, HHSR5Constants.AWARD_EPIN_TITLE,
				aoRequest.getParameter(HHSR5Constants.AWARD_EPIN_TITLE));
		setKeyValueInMap(aoFilterProps, HHSR5Constants.AMEND_EPIN_TITLE,
				aoRequest.getParameter(HHSR5Constants.AMEND_EPIN_TITLE));
		setKeyValueInMap(aoFilterProps, HHSConstants.ORGANIZATION_ID,
				asUserOrgType);
		setKeyValueInMap(aoFilterProps, HHSConstants.INVOICE_NUMBER,
				aoRequest.getParameter(HHSR5Constants.INVOICE_Number));
		setKeyValueInMap(aoFilterProps, HHSR5Constants.SUBMITTED_From,
				aoRequest.getParameter(HHSR5Constants.SUBMITTED_FROM_1));
		setKeyValueInMap(aoFilterProps, HHSR5Constants.SUBMITTED_TO,
				aoRequest.getParameter(HHSR5Constants.SUBMITTED_TO_1));
		setKeyValueInMap(aoFilterProps, "contractawardepinTitle",
				aoRequest.getParameter("contractawardepinTitle"));
		String lsCustomOrg = aoRequest
				.getParameter("selectedOrgTypeForLinkage");
		if (null != lsCustomOrg && !lsCustomOrg.isEmpty()
				&& !lsCustomOrg.equalsIgnoreCase("null")) {
			aoFilterProps.put(HHSR5Constants.ORGANIZATION_ID, lsCustomOrg);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilterPropsForLinkedEntity()");
	}

	/**
	 * The method will set the filter map when sharing flag is false.
	 * 
	 * @param aoRequest
	 *            as ActionRequest
	 * @param aoFilterProps
	 *            as filter property map
	 */
	private static void setFilterPropsForSharingFlagFalse(
			ActionRequest aoRequest, HashMap aoFilterProps) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterPropsForSharingFlagFalse()");
		setKeyValueInMap(aoFilterProps, HHSR5Constants.DOCUMENT_TITLE,
				aoRequest.getParameter(ApplicationConstants.DOC_NAME));
		if (null == aoFilterProps.get(HHSR5Constants.DOC_TYPE)) {
			aoFilterProps.put(HHSR5Constants.DOC_TYPE,
					aoRequest.getParameter(HHSR5Constants.DOC_TYPE_CITY));
		}
		setKeyValueInMap(aoFilterProps, P8Constants.PROPERTY_MODIFIED_FROM,
				aoRequest.getParameter(HHSR5Constants.MODIFIED_FROM_5));
		setKeyValueInMap(aoFilterProps, P8Constants.PROPERTY_MODIFIED_TO,
				aoRequest.getParameter(HHSR5Constants.MODIFIED_TO_5));
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilterPropsForSharingFlagFalse()");
	}

	/**
	 * The method will set the filter map when sharing flag is true.
	 * 
	 * @param aoRequest
	 *            as ActionRequest
	 * @param aoFilterProps
	 *            as filter property map
	 */
	private static void setFilterPropsForSharingFlagTrue(
			ActionRequest aoRequest, HashMap aoFilterProps) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterPropsForSharingFlagTrue()");
		setFilterPropsForSharingFlagFalse(aoRequest, aoFilterProps);
		setKeyValueInMap(aoFilterProps, HHSR5Constants.SHARED_FROM,
				aoRequest.getParameter(HHSR5Constants.MODIFIED_FROM_4));
		setKeyValueInMap(aoFilterProps, HHSR5Constants.SHARED_TO,
				aoRequest.getParameter(HHSR5Constants.MODIFIED_TO_4));
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilterPropsForSharingFlagTrue()");
	}

	// }

	/**
	 * This method will re - intialize page iterators
	 * 
	 * @param aoPortletSession
	 *            a PortletSession object
	 * @param aoUserSession
	 *            a P8UserSession object
	 */
	public static void reInitializePageIterator(
			PortletSession aoPortletSession, P8UserSession aoUserSession) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.reInitializePageIterator()");
		aoUserSession.setPageIterator(null);
		aoUserSession.setPageIteratorForTotal(null);
		aoUserSession.setAllPageMark(null);
		aoUserSession.setNextPageIndex(0);
		aoPortletSession.setAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, aoUserSession,
				PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute(
				ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER,
				PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.reInitializePageIterator()");
	}

	/**
	 * This method will set required session parameters for sorting and paging
	 * 
	 * @param aoSession
	 *            a PortletSession object
	 * @param asNextPage
	 *            a string value of next page
	 * @param asSortBy
	 *            a string value of sort by
	 * @param asSortType
	 *            a string value of sort type
	 * @param asParentNode
	 *            a string value of parent Node
	 */
	@SuppressWarnings("unchecked")
	public static void setReqSessionParameter(PortletSession aoSession,
			String asNextPage, String asSortBy, String asSortType,
			String asParentNode) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setReqSessionParameter()");
		aoSession.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER,
				asNextPage, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER,
				asSortBy, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER,
				asSortType, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(
				ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER,
				asParentNode, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setReqSessionParameter()");
	}

	/**
	 * This method will set filter properties based on organization type
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoFilterProps
	 *            HashMap Object
	 * @param asUserOrg
	 *            a string value containing name of user organization
	 * @param asUserOrgType
	 *            a string value containing name of user organization type
	 * @return HashMap Object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap getOrgTypeFilter(HashMap aoFilterProps,
			String asUserOrg, String asUserOrgType) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getOrgTypeFilter()");
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType)) {
			aoFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrg);
		} else if (!(null != aoFilterProps
				.get(P8Constants.PROPERTY_CE_PROVIDER_ID) && !aoFilterProps
				.get(P8Constants.PROPERTY_CE_PROVIDER_ID).toString().isEmpty())
				&& ApplicationConstants.CITY_ORG
						.equalsIgnoreCase(asUserOrgType)) {
			aoFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
					asUserOrgType);
		} else if (!(null != aoFilterProps
				.get(P8Constants.PROPERTY_CE_PROVIDER_ID) && !aoFilterProps
				.get(P8Constants.PROPERTY_CE_PROVIDER_ID).toString().isEmpty())
				&& ApplicationConstants.AGENCY_ORG
						.equalsIgnoreCase(asUserOrgType)) {
			aoFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrg);
		}
		aoFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION,
				Boolean.TRUE);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getOrgTypeFilter()");
		return aoFilterProps;
	}

	/**
	 * This method will generate a required properties map to be fetched from
	 * filenet for a document
	 * <ul>
	 * <li>Method updated in Release 5</li>
	 * </ul>
	 * 
	 * @param asUserOrgType
	 *            user organization Type
	 * @param asDocCategory
	 *            document category
	 * @return a map containing required properties
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap requiredDocsProps(String asUserOrgType,
			String asDocCategory) {
		// Adding class alias(DOC for DOCUMENT class in Filenet and FO for
		// HHS_CUSTOM_FOLDER class)
		// as values in loHmReqProps for Release 5, initially it was empty for
		// all keys.
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.requiredDocsProps()");
		HashMap<String, String> loHmReqProps = new HashMap<String, String>();
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.ORGANIZATION_ID_KEY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.DELETED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps
				.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
						+ " as DATE_CREATED_ALIAS",
						HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
				+ " as FOLDER_DATE_MODIFIED", HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.DATE_CREATED_ALIAS,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// Added for Release 5
		loHmReqProps.put(HHSConstants.TEMPLATE_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.FILENET_FOLDER_NAME, HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.DELETED_DATE
				+ " as FOLDER_DELETED_DATE", HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.IS_FOLDER_SHARED, HHSR5Constants.FO);
		loHmReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIER,
				HHSR5Constants.FO);
		// Changes for 7823 starts
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				HHSR5Constants.FO);
		// Changes for 7823 ends
		loHmReqProps.put(HHSR5Constants.ID_AS_FOLDER_ID, HHSR5Constants.FO);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED,
				HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.SHARING_FLAG, HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.OBJECT_TYPE, HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.SHARING_FLAG_AS_DOC_SHARING_FLAG,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.LINK_TO_APPLICATION_AS_FOLDER_LINKAGE,
				HHSR5Constants.FO);
		loHmReqProps.put(
				P8Constants.PROPERTY_CE_PROVIDER_ID + " AS FOLDER_ORG",
				HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.SHARED_ENTITY_ID
				+ " AS FOLDER_SHARED_ENTITY_ID", HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.SHARED_ENTITY_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.OBJECT_TYPE, HHSR5Constants.FO);
		loHmReqProps.put(HHSR5Constants.ORGANIZATION_ID_KEY
				+ " AS FOLDER_ORG_TYPE ", HHSR5Constants.FO);
		// End Release 5
		if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)
				&& (null == asDocCategory || asDocCategory.isEmpty() || !asDocCategory
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_SOLICITATION_CATEGORY))) {
			// Added for Release 5
			loHmReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
					HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loHmReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE,
					HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			// End Release 5
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.requiredDocsProps()");
		return loHmReqProps;
	}

	/**
	 * This method will generate document bean from document list and will get
	 * the string containing share status
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * <li>Method Updated in Release 5</li>
	 * </ul>
	 * 
	 * @param aoDocumentList
	 *            a list of documents
	 * @param aoDocuments
	 *            a document object containing document properties
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @return a String value of document shared status
	 * @throws ApplicationException
	 *             If an application Exception occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String generateDocumentBean(List aoDocumentList,
			List<Document> aoDocuments, String asUserOrg,
			P8UserSession aoUserSession,
			List<Document> loDocumentContractRestriction,
			String asUserOrganization) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.generateDocumentBean()");
		String lsShareStatusString = HHSR5Constants.EMPTY_STRING;
		// Added for Release 5
		List<HashMap> loFolderList = new ArrayList<HashMap>();
		List<HashMap> loDocList = new ArrayList<HashMap>();
		List<HashMap> loDocListSearch = new ArrayList<HashMap>();
		Iterator loIteratorDoc = null;
		// End Release 5
		if (aoDocumentList != null) {
			Iterator loIterator = aoDocumentList.iterator();
			List<String> loDocIdList = new ArrayList<String>();
			while (loIterator.hasNext()) {
				Document loDocument = new Document();
				HashMap loDocProps = (HashMap) loIterator.next();
				// Added if condition for Release 5
				if (!loDocProps.isEmpty()) {
					setFolderAndDocPropsInBean(aoDocuments, loFolderList,
							loDocList, loDocListSearch, loDocument, loDocProps);
				}

			}

			Iterator loIteratorFolder = loFolderList.iterator();
			while (loIteratorFolder.hasNext()) {
				setFolderPropertiesInBean(aoDocuments, asUserOrg, loDocIdList,
						loIteratorFolder, asUserOrganization);
			}
			if (null != loDocListSearch && !loDocListSearch.isEmpty()
					&& loDocListSearch.size() > 0) {
				loIteratorDoc = loDocListSearch.iterator();
			} else {
				loIteratorDoc = loDocList.iterator();
			}

			while (loIteratorDoc.hasNext()) {
				lsShareStatusString = setDocumentPropertiesinBean(aoDocuments,
						asUserOrg, loDocumentContractRestriction,
						lsShareStatusString, loIteratorDoc, loDocIdList,
						asUserOrganization);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.generateDocumentBean()");
		return lsShareStatusString;
	}

	/**
	 * The method will set the properties of folder and document.
	 * 
	 * @param aoDocuments
	 *            as list of document object
	 * @param aoFolderList
	 *            as list of folder for setting folder property
	 * @param aoDocList
	 *            as list of document for setting document property
	 * @param aoDocListSearch
	 * @param aoDocument
	 *            as document object
	 * @param aoDocProps
	 *            as document property map
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private static void setFolderAndDocPropsInBean(List<Document> aoDocuments,
			List<HashMap> aoFolderList, List<HashMap> aoDocList,
			List<HashMap> aoDocListSearch, Document aoDocument,
			HashMap aoDocProps) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFolderAndDocPropsInBean()");
		if (null != (String) aoDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE)) {
			if (null != aoDocProps.get(HHSR5Constants.ROW_TO_DISPLAY)
					&& aoDocProps.get(HHSR5Constants.ROW_TO_DISPLAY).toString()
							.equalsIgnoreCase(HHSConstants.STRING_TRUE)) {
				aoDocListSearch.add(aoDocProps);
			} else {
				aoDocList.add(aoDocProps);
			}

		} else if (null == aoDocProps.get(P8Constants.PROPERTY_CE_SHARED_BY_ID)) {

			aoFolderList.add(aoDocProps);
		} else if (null != aoDocProps.get(P8Constants.PROPERTY_CE_SHARED_BY_ID)
				&& !aoDocProps.get(P8Constants.PROPERTY_CE_SHARED_BY_ID)
						.toString().isEmpty()) {
			aoDocument.setDate(DateUtil.getDateMMddYYYYFormat((Date) aoDocProps
					.get(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE)));
			aoDocument.setDocName((String) aoDocProps
					.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			if (aoDocProps.get(P8Constants.PROPERTY_CE_SHARED_BY_ID).toString()
					.equalsIgnoreCase(HHSConstants.USER_CITY)) {
				aoDocument.setOrgName(ApplicationConstants.CITY_USER_NAME);
			} else {
				List<ProviderBean> loProvList = (List<ProviderBean>) BaseCacheManagerWeb
						.getInstance().getCacheObject(HHSR5Constants.PROV_LIST);
				Boolean flag = true;
				for (Iterator iterator = loProvList.iterator(); iterator
						.hasNext();) {
					ProviderBean loProviderBean = (ProviderBean) iterator
							.next();
					if (null != loProviderBean.getHiddenValue()
							&& loProviderBean
									.getHiddenValue()
									.equalsIgnoreCase(
											aoDocProps
													.get(P8Constants.PROPERTY_CE_SHARED_BY_ID)
													.toString()))

					{
						aoDocument.setOrgName(loProviderBean.getDisplayValue());
						flag = false;
					}
				}

				if (flag) {
					if (null != aoDocProps
							.get(P8Constants.PROPERTY_CE_SHARED_BY_ID)) {
						String lsAgencyId = getAgencyId(
								(TreeSet<String>) BaseCacheManagerWeb
										.getInstance()
										.getCacheObject(
												ApplicationConstants.AGENCY_LIST),
								aoDocProps.get(
										P8Constants.PROPERTY_CE_SHARED_BY_ID)
										.toString());

						aoDocument.setOrgName(lsAgencyId);
					}
				}
			}
			// changes for check of provider and agency
			aoDocument.setDocumentId(aoDocProps.get(
					HHSR5Constants.TEMPLATE_IDEN).toString());
			aoDocuments.add(aoDocument);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFolderAndDocPropsInBean()");
	}

	/**
	 * This method will set Folder Properties In Bean
	 * 
	 * @param aoDocIdList
	 *            a list of document ids
	 * @param aoDocuments
	 *            a document object containing document properties
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param aoIteratorFolder
	 *            Iterator
	 * @param asUserOrganization
	 *            String
	 * @throws ApplicationException
	 *             If an application Exception occurs
	 */
	private static void setFolderPropertiesInBean(List<Document> aoDocuments,
			String asUserOrg, List<String> aoDocIdList,
			Iterator aoIteratorFolder, String asUserOrganization)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFolderPropertiesInBean()");
		Document loDocument = new Document();
		HashMap loDocProps = (HashMap) aoIteratorFolder.next();
		if (!loDocProps.isEmpty()) {
			loDocument.setDeletedDate(DateUtil
					.getDateMMddYYYYFormat((Date) loDocProps
							.get(HHSR5Constants.FOLDER_DELETED_DATE)));
			loDocument.setDocName((String) loDocProps
					.get(HHSR5Constants.FILENET_FOLDER_NAME));
			if (null == loDocument.getDocName()
					|| loDocument.getDocName().isEmpty()) {
				loDocument.setDocName((String) loDocProps.get("FolderNameOld"));
			}
			loDocument.setDocumentId(loDocProps.get(
					HHSR5Constants.FILENET_FOLDER_ID).toString());
			if (null != loDocProps.get(HHSR5Constants.SHARING_FLAG)
					&& !loDocProps.get(HHSR5Constants.SHARING_FLAG).toString()
							.isEmpty()) {
				loDocument.setShareStatus(loDocProps.get(
						HHSR5Constants.SHARING_FLAG).toString());
				loDocument.setDocumentShared(true);
			}
			loDocument.setLastModifiedBy((String) loDocProps
					.get(P8Constants.PROPERTY_CE_LAST_MODIFIER));
			loDocument.setDate(DateUtil.getDateMMddYYYYFormat((Date) loDocProps
					.get("FOLDER_DATE_MODIFIED")));
			// Below line has been added for document linkage check
			loDocument.setLinkToApplication((Boolean) loDocProps
					.get("FOLDER_LINKAGE"));
			aoDocIdList.add(loDocument.getDocumentId());
			loDocument.setUserOrg(asUserOrg);
			loDocument.setOrganizationId((String) loDocProps
					.get("FOLDER_ORG_TYPE"));
			loDocument.setProviderId((String) loDocProps.get("FOLDER_ORG"));
			loDocument.setCurrentOrgId(asUserOrganization);
			if (null != loDocProps.get("FOLDER_SHARED_ENTITY_ID")) {
				loDocument.setSharedEntityId(loDocProps.get(
						"FOLDER_SHARED_ENTITY_ID").toString());
			}

			List aoOrg = getProviderList();
			String lsDisplayName = null;
			String loProvId = (String) loDocProps.get("FOLDER_ORG");
			lsDisplayName = (String) loDocProps.get("ORG_LEGAL_NAME_FOLDER");
			loDocument.setOrgName(lsDisplayName);
			aoDocuments.add(loDocument);

		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFolderPropertiesInBean()");
	}

	/**
	 * @param loDocument
	 * @param aoOrg
	 * @param lsDisplayName
	 * @param loProvId
	 * @throws ApplicationException
	 */
	private static void getOrganizationLegalName(Document loDocument,
			List aoOrg, String lsDisplayName, String loProvId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getOrganizationLegalName()");
		if (null != loProvId && !loProvId.isEmpty()) {
			Iterator loitr = aoOrg.iterator();
			while (loitr.hasNext()) {
				ProviderBean lopb = (ProviderBean) loitr.next();
				if (loProvId.equalsIgnoreCase(lopb.getHiddenValue())) {
					lsDisplayName = StringEscapeUtils.unescapeJavaScript(lopb
							.getDisplayValue());
					break;
				}
			}
			if (null == lsDisplayName || lsDisplayName.isEmpty()) {
				lsDisplayName = getAgencyId(
						(TreeSet<String>) BaseCacheManagerWeb.getInstance()
								.getCacheObject(
										ApplicationConstants.AGENCY_LIST),
						loProvId);
			}
			if (loProvId.equalsIgnoreCase("city_org")
					&& (null == lsDisplayName || lsDisplayName.isEmpty())) {
				lsDisplayName = "HHS Accelerator";
			}

			loDocument.setOrgName(lsDisplayName);

		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getOrganizationLegalName()");
	}

	/**
	 * The method will set the document bean property.
	 * 
	 * @param aoDocuments
	 * @param asUserOrg
	 *            as user organization
	 * @param aoDocumentContractRestriction
	 * @param asShareStatusString
	 * @param aoIteratorDoc
	 * @param aoDocIdList
	 * @return the shared status
	 * @throws ApplicationException
	 */
	private static String setDocumentPropertiesinBean(
			List<Document> aoDocuments, String asUserOrg,
			List<Document> aoDocumentContractRestriction,
			String asShareStatusString, Iterator aoIteratorDoc,
			List<String> aoDocIdList, String asUserOrganization)
			throws ApplicationException {
		Document loDocument = new Document();
		HashMap loDocProps = (HashMap) aoIteratorDoc.next();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setDocumentPropertiesinBean()");
		if (!loDocProps.isEmpty()) {
			loDocument.setDeletedDate(DateUtil
					.getDateMMddYYYYFormat((Date) loDocProps
							.get(HHSR5Constants.DELETED_DATE)));
			String lsProviderName = (String) loDocProps
					.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY);
			loDocument.setDocName((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
			loDocument.setDocType((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOC_TYPE));
			loDocument.setDocCategory((String) loDocProps
					.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
			loDocument.setDate(DateUtil.getDateMMddYYYYFormat((Date) loDocProps
					.get("DATE_CREATED_ALIAS")));
			loDocument.setOrganizationId((String) loDocProps
					.get(HHSR5Constants.ORGANIZATION_ID_KEY));
			if (null != loDocProps.get(P8Constants.PROPERTY_CE_PROVIDER_ID)
					&& !loDocProps.get(P8Constants.PROPERTY_CE_PROVIDER_ID)
							.toString().isEmpty()) {
				loDocument.setProviderId((String) loDocProps
						.get(P8Constants.PROPERTY_CE_PROVIDER_ID));
				loDocument.setLastModifiedBy(lsProviderName);
			}
			loDocument.setDocumentId(loDocProps.get(
					P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
			if (null != loDocProps
					.get(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED)
					&& !loDocProps
							.get(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED)
							.toString().isEmpty()) {
				loDocument
						.setDocumentShared(Boolean.valueOf(loDocProps.get(
								P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED)
								.toString()));
			}
			if (null != loDocProps.get(HHSR5Constants.DOC_SHARING_FLAG)
					&& !loDocProps.get(HHSR5Constants.DOC_SHARING_FLAG)
							.toString().isEmpty()) {
				asShareStatusString = HHSConstants.STRING_TRUE;
				loDocument.setShareStatus(loDocProps.get(
						HHSR5Constants.DOC_SHARING_FLAG).toString());
			}

			else {
				loDocument
						.setShareStatus(ApplicationConstants.DOCUMENT_NOT_SHARED_STATUS);
			}
			if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrg)) {
				loDocument.setSampleCategory((String) loDocProps
						.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
				loDocument.setSampleType((String) loDocProps
						.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
			}
			// Release 5-Added for fetching organization name
			List aoOrg = getProviderList();
			String lsDisplayName = null;
			String loProvId = (String) loDocProps.get("PROVIDER_ID");
			loDocument.setOrganizationId((String) loDocProps
					.get(HHSR5Constants.ORGANIZATION_ID_KEY));
			lsDisplayName = (String) loDocProps.get("ORG_LEGAL_NAME");
			loDocument.setOrgName(lsDisplayName);
			// Release 5-Added for fetching organization name
			aoDocIdList.add(loDocument.getDocumentId());
			loDocument.setUserOrg(asUserOrg);
			// Release 5-Added null check for LINK_TO_APPLICATION-
			// selectVault
			if (null != loDocProps
					.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION)
					&& !loDocProps
							.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION)
							.toString().isEmpty()) {
				loDocument.setLinkToApplication((Boolean) loDocProps
						.get(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION));
			}
			// Release 5 Contract Restriction
			loDocument.setCreatedBy((String) loDocProps
					.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID));
			if (aoDocumentContractRestriction != null) {
				for (Document loDocumentContractRestriction : aoDocumentContractRestriction) {
					if (null != loDocumentContractRestriction
							&& null != loDocument.getDocumentId()
							&& loDocumentContractRestriction.getDocumentId() != null
							&& loDocumentContractRestriction.getUserId() != null
							&& loDocument.getCreatedBy() != null
							&& (loDocumentContractRestriction.getDocumentId()
									.equalsIgnoreCase(
											loDocument.getDocumentId()) && !loDocumentContractRestriction
									.getUserId().equalsIgnoreCase(
											loDocument.getCreatedBy()))) {
						loDocument.setContractAccess(false);
					}
				}
			}
			// Release 5 Contract Restriction
			loDocument.setOrganizationId((String) loDocProps
					.get(HHSR5Constants.ORGANIZATION_ID_KEY));
			loDocument.setCurrentOrgId(asUserOrganization);
			if (null != loDocProps.get("SHARED_ENTITY_ID")) {
				loDocument.setSharedEntityId(loDocProps.get("SHARED_ENTITY_ID")
						.toString());
			}
			aoDocuments.add(loDocument);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setDocumentPropertiesinBean()");
		return asShareStatusString;
	}

	/**
	 * This will get list of documents for provider or accelerator depending
	 * upon the user logged in
	 * 
	 * <ul>
	 * <li>calls 'getDocTypeForDocCategory'</li>
	 * <li>calls 'getDocCategoryList'</li>
	 * <li>calls 'getDocumentList'</li>
	 * <li>calls 'generateDocumentBean'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            a Render Request object
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param aoDocumentList
	 *            a list of documents
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param asUserRole
	 *            a string value of user role
	 * @param aoFilterProps
	 *            a map containing document filter properties
	 * @param asSortBy
	 *            a string value of sort by
	 * @param asSortType
	 *            a string value of sort type
	 * @return a list of documents
	 * @throws ApplicationException
	 *             If an application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getDocumentList(RenderRequest aoRequest,
			P8UserSession aoUserSession, List<Document> aoDocumentList,
			Document aoDocument, String asUserOrg, String asUserRole,
			HashMap aoFilterProps, String asSortBy, String asSortType)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocumentList()");
		String lsFormPath = HHSR5Constants.EMPTY_STRING;
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (null != lsOrgType
				&& lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
			lsUserOrg = lsOrgType;
		}
		String lsFilterCategory = aoDocument.getFilterDocCategory();
		if (null != lsFilterCategory
				&& !lsFilterCategory.trim().equals(HHSR5Constants.EMPTY_STRING)
				&& !lsFilterCategory.trim().equalsIgnoreCase(HHSConstants.NULL)) {
			ArrayList loDocTypeList = (ArrayList) getDocTypeForDocCategory(
					lsFilterCategory, asUserOrg, null);
			loDocTypeList.add(0, HHSR5Constants.EMPTY_STRING);
			aoDocument.setTypeList(loDocTypeList);
		}
		ArrayList loCategoryList = null;
		loCategoryList = (ArrayList) getDocCategoryList((String) aoRequest
				.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE));
		loCategoryList.add(0, HHSR5Constants.EMPTY_STRING);

		aoDocument.setCategoryList(loCategoryList);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		// Adding two values in Channel for Release 5
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ORG, lsUserOrg);
		loChannel.setData(HHSConstants.ORGTYPE, lsOrgType);
		loChannel.setData("findOrgFlag", false);
		// End Release 5

		setOrderByParameter(loChannel, asSortBy, asSortType);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		List loDocumentList = null;
		if (null != aoRequest.getParameter(HHSR5Constants.CONTROLLER_PARAM)
				&& !aoRequest.getParameter(HHSR5Constants.CONTROLLER_PARAM)
						.isEmpty()) {
			// Added for defect# 7901
			String lsBusApp = aoRequest
					.getParameter(HHSR5Constants.SELECT_DOC_RELEASE);
			// End
			String lsFilter = aoRequest.getParameter(HHSR5Constants.IS_FILTER);
			HashMap<String, String> loHmReqProps = new HashMap<String, String>();
			FileNetOperationsUtils
					.setFilterMapForAddDocsFromVault(loHmReqProps, lsUserOrg,
							aoFilterProps, loChannel, lsBusApp);
			loDocumentList = getDocumentList(loChannel,
					aoRequest.getParameter(ApplicationConstants.DOCS_TYPE),
					loHmReqProps, aoFilterProps, true, lsUserId);

		} else {
			// Commenting it for reoving duplicate call on orderByClause.
			HashMap<String, String> loHmReqProps = new HashMap<String, String>();
			if (null != aoFilterProps.get("folderPath")
					&& aoFilterProps.get("folderPath").toString()
							.contains("Recycle Bin")) {
				loHmReqProps = requiredDocsProps(asUserOrg, lsFilterCategory);
				loHmReqProps.remove("FolderName");
				loHmReqProps.put("ORIGINAL_FOLDER_NAME AS FolderName", "FO");
				loHmReqProps.put("FolderName AS FolderNameOld", "FO");

			} else {
				loHmReqProps = requiredDocsProps(asUserOrg, lsFilterCategory);
			}
			loDocumentList = getDocumentList(loChannel, null, loHmReqProps,
					aoFilterProps, true, lsUserId);

		}
		// Release 5 Contract Restriction
		List<Document> loDocumentContractRestriction = (List<Document>) loChannel
				.getData(HHSR5Constants.LO_DOCUMENT_CONTRACT_RESTRICTION);
		// Release 5 Contract Restriction

		String lsShareStatus = generateDocumentBean(loDocumentList,
				aoDocumentList, asUserOrg, aoUserSession,
				loDocumentContractRestriction, lsUserOrg);
		// Release 5 changes done for user type checks
		String lsPermissionType = (String) aoRequest.getPortletSession()
				.getAttribute(HHSConstants.PERMISSION_TYPE,
						PortletSession.APPLICATION_SCOPE);
		String lsRole = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);

		for (Document loDocumentBean : aoDocumentList) {
			loDocumentBean.setPermissionType(lsPermissionType);
			loDocumentBean.setUserRole(lsRole);
		}
		// Release 5 changes done for user type checks

		// R4 document vault changes : request attribute 'shared flag' is set
		// outside if condition
		// since now provider,agency and city would have sharing permissions of
		// their documents. Earlier only provider had shareing permission so the
		// flag was set only for provider
		// Start of Changes done for Defect QC 5997
		if (null != Boolean.valueOf(HHSPortalUtil.parseQueryString(aoRequest,
				"sharedFlag"))) {
			aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
					HHSPortalUtil.parseQueryString(aoRequest, "sharedFlag"));
		} else {
			boolean lbIsShared = setSharedWithAgencyAndProvider(lsUserOrg,
					aoRequest, aoUserSession);

			if (true == lbIsShared) {
				aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
						HHSConstants.STRING_TRUE);

			}
		}

		if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrg)) {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		} else if (ApplicationConstants.PROVIDER_ORG
				.equalsIgnoreCase(asUserOrg)) {

			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		}
		// R4 Document Vault Changes: Agency would also have its Document Vault,
		// so this condition returns the jsp for agency document vault.
		else if (ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(asUserOrg)) {
			lsFormPath = ApplicationConstants.PROV_DOCUMENT_LIST_PAGE;
		}
		if (null != aoRequest.getParameter(HHSR5Constants.JSP_NAME)) {
			lsFormPath = HHSR5Constants.RECYCLE_BIN_JSP_NAME;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocumentList()");
		return lsFormPath;
	}

	/**
	 * This method will get list of providers and agencies based on provider or
	 * agency Id
	 * <ul>
	 * <li>Execute <b>getSharedAgencyProviderList_filenet</b> transaction a list
	 * of provider bean to get active provider list from DB</li>
	 * </ul>
	 * 
	 * @param aoProviderList
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asAgencyType
	 *            a string value for agency type
	 * @param asProviderId
	 *            a string value for provider Id
	 * @return a set containing providers and agencies
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TreeSet<ProviderBean> getSharedAgencyProviderListForUnshareScreen(
			List<ProviderBean> aoProviderList, P8UserSession aoUserSession,
			String asAgencyType, String asProviderId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSharedAgencyProviderListForUnshareScreen()");
		Channel loChannel = new Channel();
		TreeSet<ProviderBean> loProviderSet = new TreeSet<ProviderBean>();

		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSR5Constants.AGENCY_TYPE, asAgencyType);
		loChannel.setData(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		TransactionManager.executeTransaction(loChannel,
				"getSharedAgencyProviderList_filenet",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		TreeSet loShareSet = (TreeSet) loChannel
				.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
		if (P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID
				.equalsIgnoreCase(asAgencyType)) {
			for (Iterator liIt = loShareSet.iterator(); liIt.hasNext();) {
				ProviderBean loProviderBean = new ProviderBean();
				String lsId = (String) liIt.next();
				String lsName = getProviderName(aoProviderList, lsId);
				loProviderBean.setDisplayValue(StringEscapeUtils
						.unescapeJavaScript(lsName));
				loProviderBean.setHiddenValue(lsId);
				loProviderSet.add(loProviderBean);
			}
		} else {
			TreeSet loAgencySet = getNYCAgencyListFromDB();
			for (Iterator<String> loIterator = loShareSet.iterator(); loIterator
					.hasNext();) {
				ProviderBean loProviderBean = new ProviderBean();
				String lsId = (String) loIterator.next();
				String lsName = getAgencyName(loAgencySet, lsId);
				loProviderBean.setDisplayValue(StringEscapeUtils
						.unescapeJavaScript(lsName));
				loProviderBean.setHiddenValue(lsId);
				loProviderSet.add(loProviderBean);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getSharedAgencyProviderListForUnshareScreen()");
		return loProviderSet;
	}

	/**
	 * This method will get list of providers and agencies based on provider or
	 * agency Id
	 * <ul>
	 * <li>Execute <b>getSharedAgencyProviderList_filenet</b> transaction a list
	 * of provider bean to get active provider list from DB</li>
	 * </ul>
	 * 
	 * @param aoProviderList
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asAgencyType
	 *            a string value for agency type
	 * @param asProviderId
	 *            a string value for provider Id
	 * @return a set containing providers and agencies
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TreeSet getSharedAgencyProviderList(
			List<ProviderBean> aoProviderList, P8UserSession aoUserSession,
			String asAgencyType, String asProviderId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSharedAgencyProviderList()");
		Channel loChannel = new Channel();
		TreeSet loProviderSet = new TreeSet();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSR5Constants.AGENCY_TYPE, asAgencyType);
		loChannel.setData(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		TransactionManager.executeTransaction(loChannel,
				"getSharedAgencyProviderList_filenet",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		TreeSet loShareSet = (TreeSet) loChannel
				.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
		if (P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID
				.equalsIgnoreCase(asAgencyType)) {
			for (Iterator liIt = loShareSet.iterator(); liIt.hasNext();) {
				String lsName = (String) liIt.next();
				loProviderSet.add(getProviderName(aoProviderList, lsName));
			}
		} else {
			TreeSet loAgencySet = getNYCAgencyListFromDB();
			for (Iterator<String> loIterator = loShareSet.iterator(); loIterator
					.hasNext();) {
				String lsName = (String) loIterator.next();
				String lsAgencyName = getAgencyName(loAgencySet, lsName);
				loProviderSet.add(lsAgencyName);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getSharedAgencyProviderList()");
		return loProviderSet;
	}

	/**
	 * This method will get list of documents shared with given provider or
	 * agency name
	 * 
	 * @param loUserSession
	 *            a P8UserSession object
	 * @param asProviderAgencyName
	 *            a string value of provider or agency name
	 * @param abIsProvider
	 *            a boolean value of indicating is it is provider
	 * @param asOwnerProvider
	 *            a string value of provider owner
	 * @return a list of shared documents
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getSharedDocumentList(P8UserSession aoUserSession,
			String asProviderAgencyName, boolean abIsProvider,
			String asOwnerProvider, String asUserOrgType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSharedDocumentList()");
		HashMap loFilterProps = new HashMap();
		Channel loChannel = new Channel();
		loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, true);
		if (abIsProvider) {
			loFilterProps.put(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID,
					asProviderAgencyName.trim());
		} else {
			loFilterProps.put(P8Constants.PROPERTY_CE_FILTER_NYC_ORG,
					asProviderAgencyName.trim());
		}
		loFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
				asOwnerProvider.trim());
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData("findOrgFlag", false);
		setOrderByParameter(loChannel, null, null);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getSharedDocumentList()");
		return getDocumentList(loChannel, null,
				requiredDocsProps(asUserOrgType, null), loFilterProps, true,
				null);

	}

	/**
	 * This method will get list of document for provider organization
	 * 
	 * @param aoUserSession
	 *            P8UserSession Object
	 * @param asOwnerProvider
	 *            a string containing owner provider
	 * @param asOrgType
	 *            a string containing organization type
	 * @return List of documents for provide organization
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getProviderViewDocumentList(P8UserSession aoUserSession,
			String asOwnerProvider, String asOrgType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getProviderViewDocumentList()");
		HashMap loFilterProps = new HashMap();
		Channel loChannel = new Channel();
		loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, true);
		loFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
				asOwnerProvider.trim());
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData("findOrgFlag", false);
		setOrderByParameter(loChannel, null, null);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getProviderViewDocumentList()");
		return getDocumentList(loChannel, null,
				requiredDocsProps(asOrgType, null), loFilterProps, true, null);
	}

	/**
	 * This method executes when a user click on the edit document properties
	 * action
	 * 
	 * @param aoRequest
	 *            ActionRequest Object
	 * @param aoResponse
	 *            ActionResponse Object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static void actionViewDocumentInfo(ActionRequest aoRequest,	ActionResponse aoResponse) throws ApplicationException 
	{
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.actionViewDocumentInfo()");
		String lsUserOrgType = null;
		String lsIsAgencyViewingRFPDoc = null;
		lsIsAgencyViewingRFPDoc = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.IS_AGENCY_VIEWING_RFP_DOC);
		if (aoRequest.getAttribute(HHSConstants.ORGTYPE) != null
				&& ((String) aoRequest.getAttribute(HHSConstants.ORGTYPE))
						.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)) {
			lsUserOrgType = ApplicationConstants.PROVIDER_ORG;
		} else if (aoRequest
				.getAttribute(ApplicationConstants.FINANCIAL_ORG_TYPE) != null) {
			lsUserOrgType = (String) aoRequest
					.getAttribute(ApplicationConstants.FINANCIAL_ORG_TYPE);
		} else if (null != lsIsAgencyViewingRFPDoc
				&& !lsIsAgencyViewingRFPDoc.isEmpty()
				&& lsIsAgencyViewingRFPDoc
						.equalsIgnoreCase(ApplicationConstants.TRUE)) {
			lsUserOrgType = ApplicationConstants.CITY_ORG;
		} else {
			lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
		}
		viewDocumentInformation(aoRequest, aoResponse, lsUserOrgType);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.actionViewDocumentInfo()");
	}

	/**
	 * This method will get the document properties details based on document ID
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public static void viewDocumentInformation(ActionRequest aoRequest,	ActionResponse aoResponse, String asUserOrgType)
			throws ApplicationException 
	{
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.viewDocumentInformation()");
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		// Added doc type for differentiating between folder and document
		String lsDocTpye = aoRequest.getParameter(HHSR5Constants.DOC_TYPE_HIDDEN);
		String tsDocTpye = aoRequest.getParameter(HHSR5Constants.DOC_TYPE);
		String lsJspName = (String) aoRequest.getPortletSession().getAttribute(HHSR5Constants.JSP_NAME);
		String lsDocCat = aoRequest.getParameter(HHSR5Constants.DOC_CATEGORY_HIDDEN);
		String lsDocumentId = aoRequest.getParameter(ApplicationConstants.DOCUMENT_ID);
		String lsVersionProp = aoRequest.getParameter(ApplicationConstants.VERSION_PROPERTY);
		String lsOrgId = null;
		lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		if (asUserOrgType != null
				&& asUserOrgType
						.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
			lsOrgId = asUserOrgType;
		}
		Map<String, String> loEditDocumentMap = (Map<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.EDIT_DOC_LIST_MAP);
		
		if (null != aoRequest.getParameter(HHSR5Constants.SHARED_PAGE_ORG)
				&& !aoRequest.getParameter(HHSR5Constants.SHARED_PAGE_ORG).isEmpty()
				&& !aoRequest.getParameter(HHSR5Constants.SHARED_PAGE_ORG).equalsIgnoreCase(HHSConstants.UNDEFINED)
				&& !aoRequest.getParameter(HHSR5Constants.SHARED_PAGE_ORG).equalsIgnoreCase(HHSConstants.STRING_TRUE)) 
		{
			asUserOrgType = aoRequest.getParameter(HHSConstants.ORGANIZATION_TYPE);
			lsOrgId = aoRequest.getParameter(HHSConstants.ORGANIZATION_ID);
		}
		// Added for getting docCategory in case docCatHidden value is null or
		// empty
		if (null == lsDocCat || lsDocCat.isEmpty()) 
		{
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.FILENETDOCTYPE);
			List<String> lsDocCatNew = getDocCategoryFromXML(loXMLDoc, lsDocTpye, asUserOrgType);
			lsDocCat = lsDocCatNew.get(0);
		}
		// End
		Document loDocument = viewDocumentInfo(loUserSession, asUserOrgType, lsDocumentId, lsDocTpye, lsOrgId, lsDocCat, lsJspName);
		String lsDocumentOrgName = loDocument.getUserOrg();
		if (null == lsDocumentOrgName || lsDocumentOrgName.isEmpty()) {
			lsDocumentOrgName = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
		}
		loDocument.setImplementationStatus(getImplementationStatusList());
		loDocument.setSampleCategoryList(getSampleCategoryList());
		if (null != loDocument.getSampleCategory()) {
			loDocument.setSampleTypeList(getSampleTypeList(loDocument
					.getSampleCategory()));
		}
		if (null != loDocument.getDocCategory()
				&& loDocument.getDocCategory().equalsIgnoreCase(
						ApplicationConstants.HELP)) {
			loDocument.setHelpCategoryList(getHelpCategory(asUserOrgType,
					loDocument.getDocCategory(), loDocument.getDocType()));
		}
		setFilterHiddenParams(aoRequest, loDocument, null);
		if (lsDocumentOrgName.equalsIgnoreCase(lsOrgId)) {
			if (null != lsDocTpye && !lsDocTpye.isEmpty()
					&& !lsDocTpye.equalsIgnoreCase(HHSConstants.NULL)) {
				if (!checkLinkToApplication(aoRequest, lsDocumentId)) {
					aoResponse.setRenderParameter(
							ApplicationConstants.EDIT_VERSION_PROP,
							HHSConstants.STRING_TRUE);
				} else {
					aoResponse.setRenderParameter(
							ApplicationConstants.EDIT_VERSION_PROP,
							ApplicationConstants.FALSE);
				}
			}

			if (null != loEditDocumentMap
					&& loEditDocumentMap.containsValue(lsDocumentId)) {
				aoResponse.setRenderParameter(
						ApplicationConstants.IS_LOCKED_STATUS,
						HHSConstants.STRING_TRUE);
			}
		} else {
			aoResponse.setRenderParameter(
					ApplicationConstants.EDIT_VERSION_PROP,
					ApplicationConstants.FALSE);
		}
		ApplicationSession.setAttribute(loDocument, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.viewDocumentInformation()");
	}

	/**
	 * This method will get a list of Sample document category
	 * 
	 * @return a list of Sample document category
	 * @throws ApplicationException
	 *             If an Application Exception occursR
	 */
	public static ArrayList<String> getSampleCategoryList()
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSampleCategoryList()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		ArrayList<String> loSampleCategoryList = null;
		loSampleCategoryList = (ArrayList<String>) getDocCategoryFromXML(
				loXMLDoc, null, ApplicationConstants.PROVIDER_ORG);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getSampleCategoryList()");
		return loSampleCategoryList;
	}

	/**
	 * This method will get a list of Sample document type
	 * 
	 * @return a list of Sample document type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static ArrayList<String> getSampleTypeList(String asSelectedCategory)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getSampleTypeList()");
		ArrayList<String> loSampleTypeList = null;
		loSampleTypeList = (ArrayList<String>) getDocTypeForDocCategory(
				asSelectedCategory, ApplicationConstants.PROVIDER_ORG, null);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getSampleTypeList()");
		return loSampleTypeList;
	}

	/**
	 * This method will get version history details for the given dicument Id
	 * <ul>
	 * <li>Execute <b>documentversions_filenet</b> Transaction in DB</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void actionViewVersionHistory(ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.actionViewVersionHistory()");
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT,
				PortletSession.APPLICATION_SCOPE);
		Document loDocumentObj = null;
		String lsDocumentId = aoRequest
				.getParameter(ApplicationConstants.DOCUMENT_ID);
		HashMap loHmRequiredProp = new HashMap();
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_VERSION_NUMBER,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				HHSR5Constants.EMPTY_STRING);
		loHmRequiredProp.put(P8Constants.PROPERTY_CE_DOCUMENT_ID,
				HHSR5Constants.EMPTY_STRING);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.DOCUMENT_ID, lsDocumentId);
		loChannel.setData(ApplicationConstants.REQ_PROPS_DOCUMENT,
				loHmRequiredProp);
		TransactionManager.executeTransaction(loChannel,
				"documentversions_filenet",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		HashMap loVersionMap = (HashMap) loChannel
				.getData(HHSR5Constants.VERSION_MAP);

		Iterator loIterator = loVersionMap.keySet().iterator();
		List<Document> loDocVersionList = new ArrayList<Document>();
		Document loDocument = null;

		while (loIterator.hasNext()) {
			loDocument = new Document();
			String lsKey = (String) loIterator.next();
			HashMap loDocProps = (HashMap) loVersionMap.get(lsKey);
			loDocument.setVersionNo((Integer) loDocProps
					.get(P8Constants.PROPERTY_CE_VERSION_NUMBER));
			loDocument.setDate(DateUtil.getDateMMddYYYYFormat((Date) loDocProps
					.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)));
			loDocument.setLastModifiedBy((String) loDocProps
					.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
			loDocument.setDocumentId(loDocProps.get(
					P8Constants.PROPERTY_CE_DOCUMENT_ID).toString());
			loDocument.setDocType(loDocProps.get(
					P8Constants.PROPERTY_CE_DOC_TYPE).toString());
			loDocument.setDocCategory(loDocProps.get(
					P8Constants.PROPERTY_CE_DOC_CATEGORY).toString());
			loDocument.setDocName(loDocProps.get(
					P8Constants.PROPERTY_CE_DOCUMENT_TITLE).toString());
			loDocVersionList.add(loDocument);
		}
		sortList(loDocVersionList,
				ApplicationConstants.SORT_LIST_VERSION_NUMBER);
		loDocumentObj = loDocVersionList.get(0);
		setFilterHiddenParams(aoRequest, loDocument, null);
		aoResponse.setRenderParameter(ApplicationConstants.MAX_DOCUMENT_ID,
				loDocVersionList.get(0).getDocumentId());
		ApplicationSession.setAttribute(loDocVersionList, aoRequest,
				ApplicationConstants.DOC_VERSION_LIST);
		ApplicationSession.setAttribute(loDocumentObj, aoRequest,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.actionViewVersionHistory()");
	}

	/**
	 * This method will sort list of objects based on column name
	 * 
	 * @param aolistOfObjects
	 *            a list of object to be sorted
	 * @param ascolumnName
	 *            a string value of column name
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void sortList(List aolistOfObjects, String ascolumnName) {
		Collections.sort(aolistOfObjects,
				Collections.reverseOrder(new SortComparator(ascolumnName)));
	}

	/**
	 * This method will get user name based on user Id
	 * <ul>
	 * <li>Execute <b>getUserNameFromDB</b> in DB</li>
	 * </ul>
	 * 
	 * @param asUserId
	 *            a string value of user Id
	 * @return a string value of user name
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static String getUserName(String asUserId)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getUserName()");
		String lsProviderName = null;
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.LI_PROVIDER, asUserId);
		TransactionManager.executeTransaction(loChannel, "getUserNameFromDB");
		lsProviderName = (String) loChannel.getData(HHSR5Constants.LS_USER);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getUserName()");
		return lsProviderName;

	}

	/**
	 * This Method was modified for release 2.6.0 to fix defect 5661 This method
	 * will set required request parameters
	 * <ul>
	 * <li>1. Passes the required parameters as input</li>
	 * <li>2. This method sets & removes different object in session</li>
	 * </ul>
	 * 
	 * @param aoSession
	 *            a Portlet Session object
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoUserSession
	 *            a P8UserSession Session object
	 * @param asNextPage
	 *            a string value of next page
	 * @param asSortBy
	 *            a string value of sort by
	 * @param asSortType
	 *            a string value of sort type
	 * @param asParentNode
	 *            a string value of parent node
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked" })
	public static void setReqRequestParameter(PortletSession aoSession,
			RenderRequest aoRequest, P8UserSession aoUserSession,
			String asNextPage, String asSortBy, String asSortType,
			String asParentNode) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setReqRequestParameter()");
		String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME
				+ ApplicationConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.APPLICATION_SETTING);
		int liAllowedObjectCount = Integer.valueOf(loApplicationSettingMap
				.get(lsAppSettingMapKey));
		aoSession.setAttribute(ApplicationConstants.ALLOWED_OBJECT_COUNT,
				liAllowedObjectCount, PortletSession.APPLICATION_SCOPE);
		if (null != asNextPage && !asNextPage.isEmpty()) {
			aoUserSession.setNextPageIndex(Integer.valueOf(asNextPage) - 1);

		}
		if (null != asSortBy) {
			aoRequest.setAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER,
					asSortBy);
			aoRequest.setAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER,
					asSortType);
		}
		aoSession.setAttribute(
				ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, asNextPage,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				aoUserSession.getTotalPageCount() * liAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		// 5661 fix starts. Setting OldDocumentIdReq in session
		aoSession.removeAttribute(HHSConstants.OLD_DOCUMENT_ID_REQ,
				PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setReqRequestParameter()");
		// 5661 fix ends
	}

	/**
	 * The method will set the required parameter for action request.
	 * 
	 * @param aoSession
	 *            as Portlet session object
	 * @param aoRequest
	 *            as ActionRequest Object
	 * @param aoUserSession
	 *            a P8UserSession object
	 * @param asNextPage
	 *            as next page param
	 * @param asSortBy
	 *            a string value of sort by
	 * @param asSortType
	 *            a string value of sort type
	 * @param asParentNode
	 *            a string value of parent node
	 * @throws ApplicationException
	 */
	public static void setReqRequestParameterAction(PortletSession aoSession,
			ActionRequest aoRequest, P8UserSession aoUserSession,
			String asNextPage, String asSortBy, String asSortType,
			String asParentNode) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setReqRequestParameterAction()");
		String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME
				+ ApplicationConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.APPLICATION_SETTING);
		int liAllowedObjectCount = Integer.valueOf(loApplicationSettingMap
				.get(lsAppSettingMapKey));
		aoSession.setAttribute(ApplicationConstants.ALLOWED_OBJECT_COUNT,
				liAllowedObjectCount, PortletSession.APPLICATION_SCOPE);
		if (null != asNextPage && !asNextPage.isEmpty()) {
			aoUserSession.setNextPageIndex(Integer.valueOf(asNextPage) - 1);

		}
		if (null != asSortBy) {
			aoRequest.setAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER,
					asSortBy);
			aoRequest.setAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER,
					asSortType);
		}
		aoSession.setAttribute(
				ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, asNextPage,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
				aoUserSession.getTotalPageCount() * liAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		// 5661 fix starts. Setting OldDocumentIdReq in session
		aoSession.removeAttribute(HHSConstants.OLD_DOCUMENT_ID_REQ,
				PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setReqRequestParameterAction()");
		// 5661 fix ends
	}

	/**
	 * This method will get set containing NYC Agencies
	 * <ul>
	 * <li>Execute <b>getAgencyList_DB</b> transaction</li>
	 * </ul>
	 * 
	 * @return a set containing NYC Agencies
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TreeSet getNYCAgencyListFromDB() throws ApplicationException {
		Channel loChannel = new Channel();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getNYCAgencyListFromDB()");
		TransactionManager.executeTransaction(loChannel, "getAgencyList_DB");
		List<ProviderBean> loAgencyList = (List) loChannel
				.getData(ApplicationConstants.AGENCY_LIST);
		TreeSet loAgencySet = new TreeSet<String>();
		if (!CollectionUtils.isEmpty(loAgencyList)) {
			Iterator<ProviderBean> loIter = loAgencyList.iterator();
			while (loIter.hasNext()) {
				ProviderBean loProviderBean = loIter.next();
				String lsDisplayValue = loProviderBean.getDisplayValue();
				lsDisplayValue = StringEscapeUtils
						.escapeJavaScript(lsDisplayValue);
				loAgencySet.add(loProviderBean.getHiddenValue()
						+ ApplicationConstants.TILD + lsDisplayValue);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getNYCAgencyListFromDB()");
		return loAgencySet;
	}

	/**
	 * This method will set filter hidden parameters
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoFilterHiddenMap
	 *            a map containing filter hidden params
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static void setFilterHiddenParams(ActionRequest aoRequest,
			Document aoDocument, HashMap<String, String> aoFilterHiddenMap)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterHiddenParams()");
		String lsDocCategory = null;
		String lsDocType = null;
		String lsModifiedFrom = null;
		String lsModifiedTo = null;
		String lsProviderName = null;
		String lsAgencyName = null;
		String lsShareStatus = null;
		String lsSampleCategory = null;
		String lsSampleType = null;
		String lsDocClassName = null;
		org.jdom.Document loXmlDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		if (aoFilterHiddenMap == null) {
			lsDocCategory = (String) PortalUtil.parseQueryString(aoRequest,
					HHSConstants.BASE_HIDDEN_DOC_CATEGORY);
			lsDocType = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_TYPE);
			if (null != lsDocType) {
				lsDocClassName = new P8ContentOperations().getDocClassName(
						loXmlDoc, lsDocType, lsDocCategory);
			}
			lsModifiedFrom = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_FROM);
			lsModifiedTo = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_TO);
			lsProviderName = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_PROVIDER_ID);
			lsAgencyName = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_NYC_AGENCY);
			lsShareStatus = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_SHARE_STATUS);
			lsSampleCategory = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_SAMPLE_CATEGORY);
			lsSampleType = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_SAMPLE_TYPE);
		} else {
			lsDocCategory = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_DOC_CATEGORY);
			lsDocType = (String) aoRequest
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_TYPE);
			if (null != lsDocType) {
				lsDocClassName = new P8ContentOperations().getDocClassName(
						loXmlDoc, lsDocType, lsDocCategory);
			}
			lsModifiedFrom = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_FROM);
			lsModifiedTo = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_TO);
			lsProviderName = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_FILTER_PROVIDER_ID);
			lsAgencyName = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_FILTER_NYC_AGENCY);
			lsShareStatus = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_DOC_SHARE_STATUS);
			lsSampleCategory = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_SAMPLE_CATEGORY);
			lsSampleType = aoFilterHiddenMap
					.get(HHSConstants.BASE_HIDDEN_SAMPLE_TYPE);
		}
		if (null != lsDocClassName && StringUtils.isNotEmpty(lsDocClassName)) {
			aoDocument
					.setFilterDocType(lsDocClassName
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsDocClassName);
		}
		if (null != lsDocCategory && StringUtils.isNotEmpty(lsDocCategory)) {
			aoDocument
					.setFilterDocCategory(lsDocCategory
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsDocCategory);
		}
		if (null != lsModifiedFrom && StringUtils.isNotEmpty(lsModifiedFrom)) {
			aoDocument
					.setFilterModifiedFrom(lsModifiedFrom
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsModifiedFrom);
		}
		if (null != lsModifiedTo && StringUtils.isNotEmpty(lsModifiedTo)) {
			aoDocument
					.setFilterModifiedTo(lsModifiedTo
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsModifiedTo);
		}
		if (null != lsProviderName && StringUtils.isNotEmpty(lsProviderName)) {
			aoDocument
					.setFilterProviderId(lsProviderName
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: StringEscapeUtils
									.escapeJavaScript(lsProviderName));
		}
		if (null != lsAgencyName && StringUtils.isNotEmpty(lsAgencyName)) {
			aoDocument
					.setFilterNYCAgency(lsAgencyName
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: StringEscapeUtils
									.escapeJavaScript(StringEscapeUtils
											.unescapeJavaScript(lsAgencyName)));
		}
		if (null != lsShareStatus && StringUtils.isNotEmpty(lsShareStatus)) {
			aoDocument
					.setDocSharedStatus(lsShareStatus
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsShareStatus);
		}

		// Start of change of defect 6218
		if (null != lsSampleCategory
				&& StringUtils.isNotEmpty(lsSampleCategory)
				&& aoFilterHiddenMap == null) {
			aoDocument
					.setFilterSampleCategory(lsSampleCategory
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsSampleCategory);
			aoDocument
					.setSampleCategory(lsSampleCategory
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsSampleCategory);
		}
		if (null != lsSampleType && StringUtils.isNotEmpty(lsSampleType)
				&& aoFilterHiddenMap == null) {
			aoDocument
					.setFilterSampleType(lsSampleType
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsSampleType);
			aoDocument
					.setSampleType(lsSampleType
							.equalsIgnoreCase(HHSConstants.NULL) ? HHSR5Constants.EMPTY_STRING
							: lsSampleType);
		}
		// End of change of defect 6218
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getNYCAgencyListFromDB()");
	}

	/**
	 * This will update notification table with organization details to whom
	 * mail is to be sent
	 * 
	 * <ul>
	 * <li>Execute <b>insertNotificationDetail</b> transaction</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoChannel
	 *            ChannelObject
	 * @param aoProviderList
	 *            a List of providers
	 * @param aoAgencyList
	 *            a List of agencies
	 * @param asSharedBy
	 *            a String Object
	 * @param asLink
	 *            a String Object
	 * @param asOrgId
	 *            a String value containing organization id
	 * @param asUserId
	 *            a String Object
	 * @param asOrgType
	 *            a String value containing organization type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sendEmailNotificationToOrgs(Channel aoChannel,
			List aoProviderList, List aoAgencyList, String asSharedBy,
			String asLink, String asOrgId, String asUserId, String asOrgType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.sendEmailNotificationToOrgs()");
		String lsCityUrl = HHSR5Constants.EMPTY_STRING;
		StringBuffer lsBfApplicationUrl = new StringBuffer();

		lsCityUrl = PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE,
				ApplicationConstants.PROPERTY_CITY_URL);
		lsBfApplicationUrl.append(lsCityUrl);
		lsBfApplicationUrl.append("/portal/hhsweb.portal");
		lsBfApplicationUrl
				.append(ApplicationConstants.ORGANIZATION_DOCUMENTS_ALERT_NOTIFICATION_LINK_1);
		lsBfApplicationUrl.append("&provider=");
		lsBfApplicationUrl.append(asOrgId);
		lsBfApplicationUrl.append("&cityUserSearchProviderId=");
		lsBfApplicationUrl.append(asOrgId);
		lsBfApplicationUrl.append("&documentOriginator=");
		lsBfApplicationUrl.append(asOrgType);

		lsCityUrl = lsBfApplicationUrl.toString();

		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();

		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSR5Constants.ALERT_028);
		loNotificationAlertList.add(HHSR5Constants.NOTIFICATION_031A);
		loNotificationAlertList.add(HHSR5Constants.NOTIFICATION_031B);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID,
				loNotificationAlertList);
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();

		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		loNotificationDataBean.setLinkMap(loLinkMap);
		if (null != aoProviderList && !aoProviderList.isEmpty()) {
			loLinkMap.put(HHSConstants.LINK, asLink);
			loNotificationDataBean.setProviderList(aoProviderList);
			loNotificationDataBean.setLinkMap(loLinkMap);
		}
		if (null != aoAgencyList && !aoAgencyList.isEmpty()) {
			asLink = null;
			loAgencyLinkMap.put(HHSConstants.LINK, lsCityUrl);
			loNotificationDataBean.setAgencyList(aoAgencyList);
			loNotificationDataBean.setAgencyLinkMap(loAgencyLinkMap);
		}
		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		if (null != asSharedBy
				&& ApplicationConstants.CITY.equalsIgnoreCase(asSharedBy)) {
			loParamMap.put(ApplicationConstants.DOCUMENT_VAULT_PROVIDER,
					ApplicationConstants.CITY_USER_NAME);
		} else {
			loParamMap.put(ApplicationConstants.DOCUMENT_VAULT_PROVIDER,
					asSharedBy);
		}
		loNotificationMap.put(HHSR5Constants.ALERT_028, loNotificationDataBean);
		loNotificationMap.put(HHSR5Constants.NOTIFICATION_031A,
				loNotificationDataBean);
		loNotificationMap.put(HHSR5Constants.NOTIFICATION_031B,
				loNotificationDataBean);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME,
				loParamMap);
		loNotificationMap.put(TransactionConstants.PROVIDER_ID, aoProviderList);

		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE,
				HHSR5Constants.Shared_By);
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, asOrgId);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoChannel.setData(ApplicationConstants.LO_HM_NOTIFY_PARAM,
				loNotificationMap);
		TransactionManager.executeTransaction(aoChannel,
				"insertNotificationDetail",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.sendEmailNotificationToOrgs()");
	}

	/**
	 * This method will get filter hidden properties
	 * 
	 * @param aoFormData
	 *            a MultipartActionRequestParser object
	 * @return a map containing values for filter hidden properties
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws PortletException
	 *             If an Portlet Exception occurs
	 */
	public static HashMap<String, String> getHiddenFilterMap(
			MultipartActionRequestParser aoFormData)
			throws ApplicationException, PortletException {
		HashMap<String, String> loHiddenFilterMap = new HashMap<String, String>();
		String lsDocCategory = null;
		String lsDocType = null;
		String lsModifiedFrom = null;
		String lsModifiedTo = null;
		String lsProviderName = null;
		String lsAgencyName = null;
		String lsShareStatus = null;
		String lsSampleCategory = null;
		String lsSampleType = null;
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getHiddenFilterMap()");
		if (aoFormData != null) {
			lsDocCategory = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_CATEGORY);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_DOC_CATEGORY,
					lsDocCategory);
			lsDocType = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_TYPE);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_DOC_TYPE, lsDocType);
			lsModifiedFrom = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_FROM);
			loHiddenFilterMap.put(
					HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_FROM,
					lsModifiedFrom);
			lsModifiedTo = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_TO);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_FILTER_MODIFIED_TO,
					lsModifiedTo);
			lsProviderName = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_PROVIDER_ID);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_FILTER_PROVIDER_ID,
					lsProviderName);
			lsAgencyName = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_FILTER_NYC_AGENCY);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_FILTER_NYC_AGENCY,
					lsAgencyName);
			lsShareStatus = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_DOC_SHARE_STATUS);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_DOC_SHARE_STATUS,
					lsShareStatus);
			lsSampleCategory = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_SAMPLE_CATEGORY);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_SAMPLE_CATEGORY,
					lsSampleCategory);
			lsSampleType = aoFormData
					.getParameter(HHSConstants.BASE_HIDDEN_SAMPLE_TYPE);
			loHiddenFilterMap.put(HHSConstants.BASE_HIDDEN_SAMPLE_TYPE,
					lsSampleType);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getHiddenFilterMap()");
		return loHiddenFilterMap;
	}

	/**
	 * This method sorts Lists of Document type on the basis of coloumn name.
	 * 
	 * @param aolistOfObjects
	 *            List of Document type
	 * @param ascolumnName
	 *            a string value containing column name
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sortDocTypeList(List aolistOfObjects, String ascolumnName) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.sortDocTypeList()");
		Collections.sort(aolistOfObjects,
				new com.nyc.hhs.frameworks.sessiongrid.SortComparator(
						ascolumnName, null, HHSConstants.ASC_STRING));
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.sortDocTypeList()");
	}

	/**
	 * This method will set Mime Part value for uploading document
	 * 
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoFormData
	 *            a MultipartActionRequestParser object
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static void setMimePartValueForUpload(Document aoDocument,
			MultipartActionRequestParser aoFormData, String asUserOrgType)
			throws ApplicationException {
		try {
			LOG_OBJECT
					.Info("Entered FileNetOperationsUtils.setMimePartValueForUpload()");
			String lsDoccategory = aoFormData
					.getParameter(ApplicationConstants.GET_DOCUMENT_CATEGORY);
			String lsDocType = aoFormData
					.getParameter(ApplicationConstants.DOC_TYPE_NODE);
			String lsDocName = aoFormData
					.getParameter(ApplicationConstants.DOC_NAME);
			String lsFormName = aoFormData
					.getParameter(ApplicationConstants.FORM_NAME_SMALL_CAPS);
			String lsFormVersion = aoFormData
					.getParameter(ApplicationConstants.FORM_VERSION_SMALL_CAPS);
			String lsServiceAppId = aoFormData
					.getParameter(HHSConstants.BASE_SERVICE_APP_ID);
			String lsSectionId = aoFormData
					.getParameter(HHSConstants.BASE_SECTION_ID);
			aoDocument.setFormName(lsFormName);
			aoDocument.setFormVersion(lsFormVersion);
			aoDocument.setOrganizationId(asUserOrgType);
			aoDocument.setServiceAppID(lsServiceAppId);
			aoDocument.setSectionId(lsSectionId);
			aoDocument.setDocCategory(lsDoccategory);
			aoDocument.setDocType(lsDocType);
			aoDocument.setDocName(lsDocName);
			if (ApplicationConstants.DOCUMENT_TYPE_HELP
					.equalsIgnoreCase(lsDocType)) {
				lsDoccategory = lsDocType;
				aoDocument.setHelpCategoryList(getHelpCategory(asUserOrgType,
						lsDoccategory, lsDocType));
				aoDocument.setDocType(lsDocType);
			}
			if (null != lsDocType
					&& lsDocType.startsWith(ApplicationConstants.DOC_SAMPLE)) {
				aoDocument.setSampleCategory(FileNetOperationsUtils
						.getSampleCategoryAndType(lsDocType,
								ApplicationConstants.SAMPLE_DOC_CATEGORY));
				aoDocument.setSampleType(FileNetOperationsUtils
						.getSampleCategoryAndType(lsDocType,
								ApplicationConstants.SAMPLE_DOC_TYPE));
				// Update for R5 : setting docType for Sample category
				aoDocument.setDocType(lsDocType);
				// Update for R5 end
				aoDocument.setDocCategory(ApplicationConstants.DOC_SAMPLE);
				aoDocument.setMaskedDocType(lsDocType);
			} else if (ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS
					.equalsIgnoreCase(lsDoccategory)
					|| ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS
							.equalsIgnoreCase(lsDoccategory)
					|| ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A
							.equalsIgnoreCase(lsDoccategory)
					|| ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT
							.equalsIgnoreCase(lsDoccategory)) {
				aoDocument.setDocType(lsDoccategory);
			} else if (null != lsDocType && !lsDocType.isEmpty()) {
				String lsOriginalDocType = getOriginalDocumentType(lsDocType);
				if (lsOriginalDocType != lsDocType) {
					aoDocument.setDocCategory(lsDocType.substring(0,
							lsDocType.indexOf("-")).trim());
				}

			}
		} catch (Exception aoEx) {
			ApplicationException aoAppEx = new ApplicationException(
					"Error occured while setting data", aoEx);
			throw aoAppEx;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setMimePartValueForUpload()");
	}

	/**
	 * This method will indicate if document is linked to application
	 * 
	 * <ul>
	 * <li>Execute <b>getAccountingPeriodForProvider_DB</b> in DB</li>
	 * <li>Execute <b>getAccountingPeriodForProviderFromOrg_DB</b> in DB</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an ActionRequest Object
	 * @param aoResponse
	 *            an ActionResponse Object
	 * @param asUserOrg
	 *            a String value containing user organization
	 * @param abIsLinkedToApp
	 *            a boolean value
	 * @param asDocType
	 *            a String value containing doc type
	 * @param aoDocument
	 *            a Document Object
	 * @param aoChannel
	 *            a Channel Object
	 * @param aoFinalDocPropsBean
	 *            a List<DocumentPropertiesBean> Object
	 * @param aoInitialDocPropsBean
	 *            a List<DocumentPropertiesBean> Object
	 * @return String Object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an IOException occurs
	 */
	@SuppressWarnings("unchecked")
	private static String getAccountingPeriodForCharTypeDocuments(
			ActionRequest aoRequest, ActionResponse aoResponse,
			String asUserOrg, boolean abIsLinkedToApp, String asDocType,
			Document aoDocument, Channel aoChannel,
			List<DocumentPropertiesBean> aoFinalDocPropsBean,
			List<DocumentPropertiesBean> aoInitialDocPropsBean)
			throws ApplicationException, IOException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getAccountingPeriodForCharTypeDocuments()");
		String lsNextExpectedDocType;
		String lsAction = HHSR5Constants.EMPTY_STRING;
		PortletSession loSession = aoRequest.getPortletSession();
		List<ProviderBean> loProviderList;
		lsNextExpectedDocType = getNextExpectedDocType(asUserOrg);
		if (null != lsNextExpectedDocType
				&& !lsNextExpectedDocType.trim().equals(
						HHSR5Constants.EMPTY_STRING)) {
			aoChannel.setData(HHSConstants.PROVIDER_ID_KEY, asUserOrg);
			TransactionManager.executeTransaction(aoChannel,
					"getAccountingPeriodForProvider_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loProviderList = (List<ProviderBean>) aoChannel
					.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
			String lsMessageforType = getExpMsgForExtUpload(
					lsNextExpectedDocType, asDocType);
			if (!HHSConstants.EMPTY_STRING.equals(lsMessageforType)) {
				loSession.setAttribute(
						ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						HHSConstants.CLC_CAP_ERROR,
						PortletSession.APPLICATION_SCOPE);
				loSession.setAttribute(ApplicationConstants.MESSAGE,
						lsMessageforType, PortletSession.APPLICATION_SCOPE);
				loSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE,
						PortletSession.APPLICATION_SCOPE);
				loSession.setAttribute(ApplicationConstants.LINKED_TO_APP,
						true, PortletSession.APPLICATION_SCOPE);
				lsAction = ApplicationConstants.UPLOAD_FILE;
				aoResponse.sendRedirect(aoRequest.getContextPath()
						+ ApplicationConstants.ERROR_HANDLER);
			}

		} else {
			aoChannel.setData(HHSConstants.PROVIDER_ID_KEY, asUserOrg);
			TransactionManager.executeTransaction(aoChannel,
					"getAccountingPeriodForProviderFromOrg_DB",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loProviderList = (List<ProviderBean>) aoChannel
					.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
		}
		if (!CollectionUtils.isEmpty(loProviderList)) {
			ProviderBean loProviderBean = loProviderList.iterator().next();
			Iterator<DocumentPropertiesBean> loIt = aoInitialDocPropsBean
					.iterator();
			while (loIt.hasNext()) {
				DocumentPropertiesBean loDocPropBean = (DocumentPropertiesBean) loIt
						.next();
				if (loDocPropBean.getPropSymbolicName().equalsIgnoreCase(
						ApplicationConstants.PERIOD_COVER_FROM_MONTH)) {
					loDocPropBean.setPropValue(loProviderBean.getStartMonth());
				} else if (loDocPropBean.getPropSymbolicName()
						.equalsIgnoreCase(
								ApplicationConstants.PERIOD_COVER_TO_MONTH)) {
					loDocPropBean.setPropValue(loProviderBean.getEndMonth());
				}
				if (loDocPropBean.getPropSymbolicName().equalsIgnoreCase(
						ApplicationConstants.PERIOD_COVER_FROM_YEAR)) {
					loDocPropBean.setPropValue(loProviderBean.getStartYear());
				} else if (loDocPropBean.getPropSymbolicName()
						.equalsIgnoreCase(
								ApplicationConstants.PERIOD_COVER_TO_YEAR)) {
					loDocPropBean.setPropValue(loProviderBean.getEndYear());
				}
				aoFinalDocPropsBean.add(loDocPropBean);
			}
		}
		aoDocument.setDocumentProperties(aoFinalDocPropsBean);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getAccountingPeriodForCharTypeDocuments()");
		return lsAction;
	}

	/**
	 * Updated for 3.1.0, enhancement 6021 - Added checks for char 500 extension
	 * This method will validate and proceed further for uploading the documents
	 * 
	 * 
	 * <ul>
	 * <li>Calls 'getDocumentProperties' method</li>
	 * <li>Calls 'checkDocExist' method</li>
	 * <li>Calls 'validateAndProceedUploadForCharTypeDocs' method</li>
	 * <li>Calls 'validateAndProceedUploadForNonCharTypeDoc' method</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an ActionRequest Object
	 * @param aoResponse
	 *            an ActionResponse Object
	 * @param aoSession
	 *            a PortletSession Object
	 * @param asUserOrgType
	 *            a String value containing user organization type
	 * @param aoUserSession
	 *            P8UserSession Object
	 * @param asUserOrg
	 *            a String value containing user organization
	 * @param abIsLinkedToApp
	 *            boolean value
	 * @param aoMimepartsNewVersion
	 *            MimeBodyPart[] Object
	 * @param aoFilterHiddenMap
	 *            HashMap<String, String> Object
	 * @param aoFiledata
	 *            MimeBodyPart Object
	 * @param asDocType
	 *            a String value containing doc type
	 * @param aoDocument
	 *            a Document Object
	 * @param aoChannel
	 *            a Channel Object
	 * @param abaByteStream
	 *            a byte[]Object Object
	 * @param asBfRealPath
	 *            a StringBuffer Object
	 * @param asFileType
	 *            a String value containing file type
	 * @throws IOException
	 *             If an IOException Exception occurs
	 * @throws MessagingException
	 *             If an MessagingException Exception occurs
	 * @throws FileNotFoundException
	 *             If an FileNotFoundException Exception occurs
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	private static void validateAndProceedUpload(ActionRequest aoRequest,
			ActionResponse aoResponse, PortletSession aoSession,
			String asUserOrgType, P8UserSession aoUserSession,
			String asUserOrg, boolean abIsLinkedToApp,
			MimeBodyPart[] aoMimepartsNewVersion,
			HashMap<String, String> aoFilterHiddenMap, MimeBodyPart aoFiledata,
			String asDocType, Document aoDocument, Channel aoChannel,
			byte[] abaByteStream, StringBuffer asBfRealPath, String asFileType)
			throws IOException, MessagingException, FileNotFoundException,
			ApplicationException {
		FileOutputStream loFileOutput = null;
		try {
			LOG_OBJECT
					.Info("Entered FileNetOperationsUtils.validateAndProceedUpload()");
			File loFile;
			String lsAction = null;
			String lsDocId = null;
			// file scanned and no virus is found, So can be
			// uploaded.
			PortletContext loContext = aoSession.getPortletContext();
			String lsRealpath = loContext.getRealPath("/resources/");
			long llCurrentTimeStamp = System.currentTimeMillis();
			asBfRealPath.append(lsRealpath);
			asBfRealPath.append(P8Constants.STRING_SINGLE_SLASH);
			asBfRealPath.append(aoFiledata.getFileName().substring(0,
					aoFiledata.getFileName().indexOf(".")));
			asBfRealPath.append(ApplicationConstants.UNDERSCORE);
			asBfRealPath.append(String.valueOf(llCurrentTimeStamp));
			asBfRealPath.append(ApplicationConstants.DOT);
			asBfRealPath.append(asFileType);
			loFile = new File(asBfRealPath.toString());
			aoDocument.setFilePath(asBfRealPath.toString());
			loFileOutput = new FileOutputStream(loFile);
			loFileOutput.write(abaByteStream);
			loFileOutput.flush();

			/*
			 * this section executes when the logged in user is belongs to
			 * provider organization
			 */
			List<DocumentPropertiesBean> loFinalDocPropsBean = new ArrayList<DocumentPropertiesBean>();
			List<DocumentPropertiesBean> loInitialDocPropsBean = null;
			org.jdom.Document loDoctypeXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.FILENETDOCTYPE);
			if (null != aoDocument.getDocCategory()
					&& aoDocument.getDocCategory().startsWith(
							ApplicationConstants.DOC_SAMPLE))
				loInitialDocPropsBean = getDocumentProperties(
						aoDocument.getDocCategory(),
						aoDocument.getMaskedDocType(), asUserOrgType);
			else
				loInitialDocPropsBean = getDocumentProperties(
						aoDocument.getDocCategory(), aoDocument.getDocType(),
						asUserOrgType);
			aoDocument.setDocumentProperties(loInitialDocPropsBean);
			// Fix for Enhancement Request 3450
			if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)
					&& (null != aoDocument.getDocCategory() && aoDocument
							.getDocCategory().startsWith(
									ApplicationConstants.DOC_SAMPLE))) {
				lsDocId = checkDocExist(aoUserSession, asUserOrg,
						aoDocument.getDocName(), aoDocument.getMaskedDocType(),
						ApplicationConstants.DOC_SAMPLE, asUserOrgType);
			} else {
				lsDocId = checkDocExist(aoUserSession, asUserOrg,
						aoDocument.getDocName(), aoDocument.getDocType(),
						aoDocument.getDocCategory(), asUserOrgType);
			}
			if (null != lsDocId && !lsDocId.isEmpty()) {
				lsAction = getAnddisplaydocExistsError(aoRequest, aoResponse,
						aoDocument, lsAction);
			}
			// Updated for 3.1.0, enhancement 6021 - Added checks for char 500
			// extension
			if (aoDocument.getDocType().equalsIgnoreCase(
					P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)
					|| aoDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)
					|| aoDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE)
					|| aoDocument.getDocType().equalsIgnoreCase(
							P8Constants.PROPERTY_CE_CHAR500_EXTENSION)) {
				Boolean lbFilingFlag = checkFilingStatus(asUserOrg);
				if (lbFilingFlag) {
					lsAction = validateAndProceedUploadForCharTypeDocs(
							aoRequest, aoResponse, aoSession, asUserOrg,
							abIsLinkedToApp, asDocType, aoDocument, aoChannel,
							lsAction, loFinalDocPropsBean,
							loInitialDocPropsBean, lsDocId);
				} else {
					ApplicationException loCustomExp = new ApplicationException(
							PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE,
									"IN_REVIEW_FILING_ERROR"));
					throw loCustomExp;
				}

			} else {
				lsAction = validateAndProceedUploadForNonCharTypeDoc(aoRequest,
						aoResponse, aoSession, aoFilterHiddenMap, aoDocument,
						lsAction, lsDocId);
			}

			ApplicationSession.setAttribute(aoDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.getPortletSession().setAttribute(
					HHSR5Constants.DOC_SESSION_BEAN, aoDocument);
			ApplicationSession.setAttribute(aoDocument, aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null != aoMimepartsNewVersion) {
				aoResponse.setRenderParameter(
						ApplicationConstants.FROM_UPLOAD_VERSION,
						ApplicationConstants.FROM_UPLOAD_VERSION);
			}
			if (null == lsAction || lsAction.isEmpty()) {
				lsAction = ApplicationConstants.FILE_INFORMATION;
				aoResponse.setRenderParameter(HHSR5Constants.NEXT_PAGE,
						lsAction);
				// below line is added as part of Release 2.6.0 defect:5612
				aoRequest.setAttribute(HHSConstants.MOVE_TO_NEXT_PAGE,
						ApplicationConstants.FILE_INFORMATION);
			}
		} finally {
			if (null != loFileOutput) {
				loFileOutput.close();
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validateAndProceedUpload()");
	}

	/**
	 * The method is added in Release 5. It will generate error message for same
	 * name document during upload Step1.
	 * 
	 * @param aoRequest
	 * @param aoDocument
	 * @param asAction
	 * @return next action
	 * @throws ApplicationException
	 *             in case of uploading same name document of similar doctype
	 */
	private static String getAnddisplaydocExistsError(ActionRequest aoRequest,
			ActionResponse aoResponse, Document aoDocument, String asAction)
			throws ApplicationException {
		String lsMessage;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getAnddisplaydocExistsError()");
		boolean lbisSystemTypedoc = false;
		if (aoDocument.getDocType().equalsIgnoreCase(
				ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE, HHSR5Constants.MESSAGE_49);
			lbisSystemTypedoc = true;
			aoResponse.setRenderParameter(HHSConstants.CLC_CAP_ERROR,
					ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE,
					lsMessage);
			aoResponse.setRenderParameter(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		} else {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSR5Constants.DUPLICATE_DOCUMENT_NAME);
		}

		if (!lbisSystemTypedoc) {
			asAction = null;
			throw new ApplicationException(lsMessage);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getAnddisplaydocExistsError()");
		return asAction;
	}

	/**
	 * This method will validate and proceed further for uploading the CHAR 500
	 * type documents
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoSession
	 *            portlet session object
	 * @param aoFilterHiddenMap
	 *            hidden filter map
	 * @param aoDocument
	 *            document bean object
	 * @param asAction
	 *            action name string
	 * @param asDocId
	 *            document id
	 * @return String path of the jsp to render
	 * @throws ApplicationException
	 *             throws when any application exception occurred
	 * 
	 * @throws IOException
	 *             throws when any IO exception occurred
	 */
	private static String validateAndProceedUploadForNonCharTypeDoc(
			ActionRequest aoRequest, ActionResponse aoResponse,
			PortletSession aoSession,
			HashMap<String, String> aoFilterHiddenMap, Document aoDocument,
			String asAction, String asDocId) throws ApplicationException,
			IOException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateAndProceedUploadForNonCharTypeDoc()");
		String lsUserOrgType = (String) aoSession.getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		String lsUploadingDocumentType = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.UPLOAD_DOC_TYPE);
		String lsMessage = HHSR5Constants.EMPTY_STRING;
		if (null != asDocId) {
			// Added a parameter to identify Document Upload Flow for Defect
			// 6443, Release 3.3.0
			aoRequest.setAttribute(HHSR5Constants.UPLOAD_FLAG,
					HHSConstants.STRING_TRUE);
			boolean lbFlag = checkLinkToApplication(aoRequest, asDocId);
			setFilterHiddenParams(aoRequest, aoDocument, aoFilterHiddenMap);
			String lsProcAndProposalMessage = null;
			if (!lbFlag) {
				boolean lbAppStatusChecked = Boolean
						.valueOf((String) ApplicationSession.getAttribute(
								aoRequest, HHSR5Constants.CHECK_APP_STATUS));
				boolean lbNonApplicationDocs = Boolean
						.valueOf((String) ApplicationSession.getAttribute(
								aoRequest,
								ApplicationConstants.IS_SM_FINANCE_DOCUMENT));
				lsProcAndProposalMessage = checkLinkToAnyOtherObject(
						lsUserOrgType, asDocId,
						P8Constants.ERROR_PROPERTY_FILE, Boolean.TRUE);
				if (lbNonApplicationDocs
						|| (null != lsProcAndProposalMessage && !lsProcAndProposalMessage
								.isEmpty())) {
					if (null != lsProcAndProposalMessage
							&& !lsProcAndProposalMessage.isEmpty()) {
						lsMessage = setMessageForNonCharTypeDoc(lsUserOrgType,
								lsUploadingDocumentType, lsMessage,
								lsProcAndProposalMessage);
						asAction = ApplicationConstants.UPLOAD_FILE;
						ApplicationSession.setAttribute(
								ApplicationConstants.TRUE, aoRequest,
								ApplicationConstants.LINKED_TO_APP);
						generateErrorMessageForAttachedDocument(aoSession,
								lsMessage);

					} else {
						if (lbAppStatusChecked) {
							lsMessage = PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE,
									HHSR5Constants.MESSAGE_49);
						} else if (!lbAppStatusChecked) {
							lsMessage = PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE,
									HHSConstants.M08);
						}
						ApplicationSession.setAttribute(String.valueOf(lbFlag),
								aoRequest, ApplicationConstants.LINKED_TO_APP);
					}
				} else {
					// Removed unnecessary condition : extra check which was the
					// cause of defect id 5545.
					// Error message was not getting displayed if we try to
					// upload same name document.
					if (lbAppStatusChecked) {
						lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSR5Constants.MESSAGE_49);
					} else {
						lsMessage = PropertyLoader.getProperty(
								P8Constants.ERROR_PROPERTY_FILE,
								HHSConstants.M08);
					}
					ApplicationSession.setAttribute(String.valueOf(lbFlag),
							aoRequest, ApplicationConstants.LINKED_TO_APP);
				}
				aoResponse.setRenderParameter(
						ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						HHSConstants.CLC_CAP_ERROR);
				aoResponse.setRenderParameter(ApplicationConstants.MESSAGE,
						lsMessage);
				aoResponse.setRenderParameter(
						ApplicationConstants.MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			} else {

				lsMessage = PropertyLoader.getProperty(
						P8Constants.ERROR_PROPERTY_FILE,
						HHSR5Constants.MESSAGE_50);
				aoResponse.setRenderParameter(
						ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						HHSConstants.CLC_CAP_ERROR);
				aoResponse.setRenderParameter(ApplicationConstants.MESSAGE,
						lsMessage);
				aoResponse.setRenderParameter(
						ApplicationConstants.MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				asAction = ApplicationConstants.UPLOAD_FILE;
				throw new ApplicationException(lsMessage);
			}
		}
		aoDocument.setImplementationStatus(getImplementationStatusList());
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validateAndProceedUploadForNonCharTypeDoc()");
		return asAction;
	}

	/**
	 * This method sets the message for Non char type doc
	 * 
	 * @param lsUserOrgType
	 *            UserOrgType
	 * @param lsUploadingDocumentType
	 *            UploadingDocumentType
	 * @param lsMessage
	 *            Message
	 * @param lsProcAndProposalMessage
	 *            rocAndProposalMessage
	 * @return lsMessage String
	 * @throws ApplicationException
	 *             throws when any application exception occurred
	 */
	private static String setMessageForNonCharTypeDoc(String lsUserOrgType,
			String lsUploadingDocumentType, String lsMessage,
			String lsProcAndProposalMessage) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setMessageForNonCharTypeDoc()");
		if (ApplicationConstants.UPLOADING_DOCUMENT_TYPE_RFP
				.equalsIgnoreCase(lsUploadingDocumentType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_PROCUREMENT);
		} else if (ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL
				.equalsIgnoreCase(lsUploadingDocumentType)
				|| ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BAFO
						.equalsIgnoreCase(lsUploadingDocumentType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_PROPOSAL);
		} else if (ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE
				.equalsIgnoreCase(lsUploadingDocumentType)
				|| ApplicationConstants.AWARD
						.equalsIgnoreCase(lsUploadingDocumentType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_INVOICE);
		} else if (ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET
				.equalsIgnoreCase(lsUploadingDocumentType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_BUDGET);
		} else if (ApplicationConstants.UPLOADING_DOCUMENT_TYPE_CONTRACT
				.equalsIgnoreCase(lsUploadingDocumentType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_CONTRACT);
		} else if ((null == lsUploadingDocumentType || lsUploadingDocumentType
				.isEmpty())
				&& ApplicationConstants.CITY_ORG
						.equalsIgnoreCase(lsUserOrgType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_PROCUREMENT);
		} else if ((null == lsUploadingDocumentType || lsUploadingDocumentType
				.isEmpty())
				&& ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(lsUserOrgType)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_PROPOSAL);
		}
		// Updated If block to display complete error message on Agency Document
		// Vault - Upload Screen
		else if ((null == lsUploadingDocumentType || lsUploadingDocumentType
				.isEmpty())
				&& lsProcAndProposalMessage
						.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_INVOICE);
		} else if ((null == lsUploadingDocumentType || lsUploadingDocumentType
				.isEmpty())
				&& lsProcAndProposalMessage
						.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_BUDGET);
		} else if ((null == lsUploadingDocumentType || lsUploadingDocumentType
				.isEmpty())
				&& lsProcAndProposalMessage
						.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_CONTRACT)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_CONTRACT);
		}
		// Updated If block to display complete error message on Agency Document
		// Vault - Upload Screen End
		// Start || Changes done for Enhancement #6429 for Release 3.4.0
		else if (lsProcAndProposalMessage
				.equalsIgnoreCase(HHSConstants.AGENCY_AWARD_DOC)) {
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					ApplicationConstants.UPLOADING_SAME_NAME_DOC_AGENCY_AWRD);
		}
		// End || Changes done for Enhancement #6429 for Release 3.4.0
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setMessageForNonCharTypeDoc()");
		return lsMessage;
	}

	/**
	 * This method will validate and proceed further for uploading the Non CHAR
	 * 500 type documents
	 * 
	 * @param aoRequest
	 *            action request object
	 * @param aoResponse
	 *            action response object
	 * @param aoSession
	 *            portlet session object
	 * @param asUserOrg
	 *            user organization id
	 * @param abIsLinkedToApp
	 *            is link to application boolean flag
	 * @param asDocType
	 *            document type
	 * @param aoDocument
	 *            document bean object
	 * @param aoChannel
	 *            channel object
	 * @param asAction
	 *            action name parameter
	 * @param aoFinalDocPropsBean
	 *            final doc properties bean
	 * @param aoInitialDocPropsBean
	 *            initial doc properties bean
	 * @param asDocId
	 *            document id
	 * @return String path of the jsp
	 * @throws ApplicationException
	 *             throws when any application exception occurred
	 * @throws IOException
	 *             throws when any IO exception occurred
	 */
	private static String validateAndProceedUploadForCharTypeDocs(
			ActionRequest aoRequest, ActionResponse aoResponse,
			PortletSession aoSession, String asUserOrg,
			boolean abIsLinkedToApp, String asDocType, Document aoDocument,
			Channel aoChannel, String asAction,
			List<DocumentPropertiesBean> aoFinalDocPropsBean,
			List<DocumentPropertiesBean> aoInitialDocPropsBean, String asDocId)
			throws ApplicationException, IOException {
		try {
			LOG_OBJECT
					.Info("Entered FileNetOperationsUtils.validateAndProceedUploadForCharTypeDocs()");
			if (null != asDocId) {
				boolean lbFlag = checkLinkToApplication(aoRequest, asDocId);
				if (lbFlag) {
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_50);
					aoSession.setAttribute(
							ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							HHSConstants.CLC_CAP_ERROR,
							PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.MESSAGE,
							lsMessage, PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE,
							PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.LINKED_TO_APP,
							true, PortletSession.APPLICATION_SCOPE);
					asAction = ApplicationConstants.UPLOAD_FILE;
					throw new ApplicationException(lsMessage);
				} else {
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE, "M08");
					ApplicationSession.setAttribute(HHSConstants.CLC_CAP_ERROR,
							aoRequest,
							ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE);
					ApplicationSession.setAttribute(lsMessage, aoRequest,
							ApplicationConstants.ERROR_MESSAGE);
					ApplicationSession.setAttribute(
							ApplicationConstants.MESSAGE_FAIL_TYPE, aoRequest,
							ApplicationConstants.ERROR_MESSAGE_TYPE);

					// RB 2833
					asAction = getAccountingPeriodForCharTypeDocuments(
							aoRequest, aoResponse, asUserOrg, abIsLinkedToApp,
							asDocType, aoDocument, aoChannel,
							aoFinalDocPropsBean, aoInitialDocPropsBean);

					// End RB 2833

				}
			} else {
				String lsStatus = getApplicationStatus(asUserOrg);

				if (null != lsStatus
						&& !lsStatus.equals(HHSR5Constants.EMPTY_STRING)
						&& lsStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)) {
					String lsMessage = PropertyLoader.getProperty(
							P8Constants.ERROR_PROPERTY_FILE,
							HHSR5Constants.MESSAGE_12);
					aoSession.setAttribute(
							ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							HHSConstants.CLC_CAP_ERROR,
							PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.MESSAGE,
							lsMessage, PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE,
							PortletSession.APPLICATION_SCOPE);
					aoSession.setAttribute(ApplicationConstants.LINKED_TO_APP,
							true, PortletSession.APPLICATION_SCOPE);
					asAction = ApplicationConstants.UPLOAD_FILE;
					throw new ApplicationException(lsMessage);
				} else {
					asAction = getAccountingPeriodForCharTypeDocuments(
							aoRequest, aoResponse, asUserOrg, abIsLinkedToApp,
							asDocType, aoDocument, aoChannel,
							aoFinalDocPropsBean, aoInitialDocPropsBean);
				}
			}
		} catch (ApplicationException aoEx) {
			String lsMessage = aoEx.getMessage();
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileInformation method:: ",
							aoEx);
			throw aoEx;
		} catch (Exception aoEx) {
			String lsMessage = aoEx.getMessage();
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileInformation method:: ",
							aoEx);
			throw new ApplicationException(lsMessage, aoEx);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validateAndProceedUploadForCharTypeDocs()");
		return asAction;
	}

	/**
	 * This method will set filter properties for query
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asDocType
	 *            a string value of document type
	 * @param asModifiedFrom
	 *            a string value of modified from
	 * @param asModifiedTo
	 *            a string value of modified to
	 * @param asProviderName
	 *            a string value of provider name
	 * @param asAgencyName
	 *            a string value of agency name
	 * @param asSampleCategory
	 *            a string value of sample document category
	 * @param asSampleType
	 *            a string value of sample document type
	 * @param aoDocument
	 *            a document object containing document properties
	 */
	private static void setFilteredProperties(String asDocCategory,
			String asDocType, String asModifiedFrom, String asModifiedTo,
			String asProviderName, String asAgencyName,
			String asSampleCategory, String asSampleType, Document aoDocument) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilteredProperties()");
		aoDocument.setDocCategory(asDocCategory);
		aoDocument.setDocType(asDocType);
		aoDocument.setFilterDocCategory(asDocCategory);
		aoDocument.setFilterDocType(asDocType);
		aoDocument.setFilterModifiedFrom(asModifiedFrom);
		aoDocument.setFilterModifiedTo(asModifiedTo);
		aoDocument.setFilterProviderId(StringEscapeUtils
				.escapeJavaScript(asProviderName));
		aoDocument.setFilterNYCAgency(StringEscapeUtils
				.escapeJavaScript(StringEscapeUtils
						.unescapeJavaScript(asAgencyName)));
		aoDocument.setSampleCategory(asSampleCategory);
		aoDocument.setFilterSampleCategory(asSampleCategory);
		aoDocument.setSampleType(asSampleType);
		aoDocument.setFilterSampleType(asSampleType);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilteredProperties()");
	}

	/**
	 * This method will set Filter properties for Provider and Agency
	 * 
	 * @param asDocCategory
	 *            a string value containing doc category
	 * @param asProviderName
	 *            a string value containing provider name
	 * @param asAgencyName
	 *            a string value containing agency name
	 * @param asUserOrgType
	 *            a string value containing user organization type
	 * @param asUserOrg
	 *            a string value containing user organization
	 * @param aoDocument
	 *            a Document Object
	 * @param aoFilterProps
	 *            a HashMap Object
	 * @param asAccessType
	 *            a string value containing access type
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setFilteredPropsForProviderAndAgency(
			String asDocCategory, String asProviderName, String asAgencyName,
			String asUserOrgType, String asUserOrg, Document aoDocument,
			HashMap aoFilterProps, String asAccessType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilteredPropsForProviderAndAgency()");
		String lsProviderId = getProviderId((List) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.PROV_LIST),
				asProviderName);
		String lsAgencyId = getAgencyId(
				(TreeSet<String>) BaseCacheManagerWeb.getInstance()
						.getCacheObject(ApplicationConstants.AGENCY_LIST),
				asAgencyName);

		if (null != asAccessType
				&& HHSConstants.AGENCY.equalsIgnoreCase(asAccessType)) {
			aoFilterProps.put(P8Constants.PROPERTY_CE_FILTER_NYC_ORG,
					lsAgencyId);
			getOrgTypeFilter(aoFilterProps, asProviderName, asUserOrgType);
		} else {
			aoFilterProps.put(P8Constants.PROPERTY_CE_FILTER_PROVIDER_ID,
					lsProviderId);
			aoFilterProps.put(P8Constants.PROPERTY_CE_FILTER_NYC_ORG,
					lsAgencyId);
			getOrgTypeFilter(aoFilterProps, asUserOrg, asUserOrgType);
		}
		setDocCategorynDocType(aoDocument, asDocCategory, asUserOrgType);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilteredPropsForProviderAndAgency()");
	}

	/**
	 * This method will create and set properties for filtering document
	 * 
	 * @param asDocCategory
	 *            a string value of document category
	 * @param asDocType
	 *            a string value of document type
	 * @param asModifiedFrom
	 *            a string value of modified from
	 * @param asModifiedTo
	 *            a string value of modified to
	 * @param asSampleCategory
	 *            a string value of sample document category
	 * @param asSampleType
	 *            a string value of sample document type
	 * @return
	 */
	// Added three parameters as docName, sharedWith, isFilter for Release 5
	public static void createAndSetFilteredProperties(String asDocCategory,
			String asDocType, String asModifiedFrom, String asModifiedTo,
			String asSampleCategory, String asSampleType, String asDocName,
			String asSharedWith, String asIsFilter, String asSubmittedFrom,
			String asSubmittedTo, String asSharedFlag,
			String[] aoCheckedAgencyItems, boolean lsFlag,
			HashMap<String, Object> aoFilterProp, String asSharedFrom,
			String asSharedTo) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.createAndSetFilteredProperties()");
		if (aoFilterProp.isEmpty()) {
			if(!StringUtils.isEmpty(asDocType))
				aoFilterProp.put(P8Constants.PROPERTY_CE_DOC_TYPE, asDocType);
			if(!StringUtils.isEmpty(asDocCategory))
				aoFilterProp.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
					asDocCategory);
			
			if(!StringUtils.isEmpty(asModifiedFrom))
			aoFilterProp
					.put(P8Constants.PROPERTY_MODIFIED_FROM, asModifiedFrom);
			if(!StringUtils.isEmpty(asModifiedTo))
			aoFilterProp.put(P8Constants.PROPERTY_MODIFIED_TO, asModifiedTo);
			if (null != asDocType && !asDocType.isEmpty()
					&& asDocType.startsWith(ApplicationConstants.DOC_SAMPLE)) {
				aoFilterProp.put(
						P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
						getSampleCategoryAndType(asDocType,
								ApplicationConstants.SAMPLE_DOC_CATEGORY));
				aoFilterProp.put(
						P8Constants.PROPERTY_CE_SAMPLE_TYPE,
						getSampleCategoryAndType(asDocType,
								ApplicationConstants.SAMPLE_DOC_TYPE));
			}
			// Added for Release 5- new search
			if(!StringUtils.isEmpty(asDocName))
				aoFilterProp.put(HHSR5Constants.DOC_TITLE, asDocName);
			if (null != asSharedWith && !asSharedWith.isEmpty()) {
				aoFilterProp.put(P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID,
						asSharedWith);
			}
			aoFilterProp.put(HHSR5Constants.IS_FILTER, asIsFilter);
			if(!StringUtils.isEmpty(asSubmittedFrom))
				aoFilterProp.put(HHSR5Constants.DELETED_FROM, asSubmittedFrom);
			if(!StringUtils.isEmpty(asSubmittedTo))
				aoFilterProp.put(HHSR5Constants.DELETED_TO, asSubmittedTo);
			if(!StringUtils.isEmpty(asSharedFlag))
				aoFilterProp.put(HHSConstants.SHARED_FLAG, asSharedFlag);
			if(aoCheckedAgencyItems!=null && aoCheckedAgencyItems.length>0)
				aoFilterProp.put(ApplicationConstants.AGENCY_LIST,
					aoCheckedAgencyItems);
			aoFilterProp.put(HHSR5Constants.IS_FIND_ORG_DOC_FLAG, lsFlag);
			if(!StringUtils.isEmpty(asSharedFrom))
				aoFilterProp.put(HHSR5Constants.SHARED_FROM, asSharedFrom);
			if(!StringUtils.isEmpty(asSharedTo))
				aoFilterProp.put(HHSR5Constants.SHARED_TO, asSharedTo);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.createAndSetFilteredProperties()");
	}

	/**
	 * This method will set Attributes for Launching work flow
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param asDocumentId
	 *            a string value of document Id
	 * @param abShortFiling
	 *            a boolean value for short filing
	 * @param asDocTitle
	 *            a string value of document title
	 * @param asUserId
	 *            a string value of user Id
	 * @param aoDocLapsingMasterMap
	 *            a map containing document lapsing master details
	 * @param aoProviderBeanList
	 *            a list of provider bean
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	private static void setAttributesToLaunchWorkFlow(String asUserOrg,
			String asDocumentId, boolean abShortFiling, String asDocTitle,
			String asUserId, Map<String, String> aoDocLapsingMasterMap,
			List<ProviderBean> aoProviderBeanList) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setAttributesToLaunchWorkFlow()");
		aoDocLapsingMasterMap.put(HHSConstants.PROVIDER_ID, asUserOrg);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_APPLICTION_ID,
				asDocumentId);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_SECTION_ID,
				asDocumentId);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_TASK_NAME,
				P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_PROVIDER_NAME,
				getProviderName(aoProviderBeanList, asUserOrg));
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_PROVIDER_ID,
				asUserOrg);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_LAUNCH_BY, asUserId);
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_IS_SHORT_FILING,
				String.valueOf(abShortFiling));
		aoDocLapsingMasterMap.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_NAME,
				asDocTitle);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setAttributesToLaunchWorkFlow()");
	}

	/**
	 * This method will set document properties for uploading the document
	 * 
	 * @param asUserName
	 *            a String value containing user name
	 * @param asUserID
	 *            a String value containing user id
	 * @param asUserOrgType
	 *            a String value containing user organization type
	 * @param aoDocument
	 *            Document Object
	 * @param asCurrentDate
	 *            a String value containing current date
	 * @param aoPropertyMap
	 *            HashMap<String, Object> Object
	 * @param asFileRetrievalName
	 *            a String value containing file retrieval name
	 */
	private static void setUploadProperties(String asUserName, String asUserID,
			String asUserOrgType, Document aoDocument, String asCurrentDate,
			HashMap<String, Object> aoPropertyMap, String asFileRetrievalName,
			String asUserOrg) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setUploadProperties()");
		aoPropertyMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				aoDocument.getDocName());
		aoPropertyMap.put(P8Constants.PROPERTY_CE_MIME_TYPE,
				aoDocument.getContentType());
		aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				aoDocument.getDocType());
		aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				aoDocument.getDocCategory());
		aoPropertyMap.put(P8Constants.PROPERTY_CE_ORGANIZATION_ID,
				asUserOrgType);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY,
				asUserName);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				asUserName);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID,
				asUserID);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID,
				asUserID);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				asCurrentDate);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_FILE_TYPE,
				aoDocument.getFileType());
		if (null != asUserOrgType
				&& asUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG)) {
			asUserOrg = asUserOrgType;
		}
		aoPropertyMap.put(HHSR5Constants.Org_Id, asUserOrg);
		aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
				aoDocument.isLinkToApplication());
		if (HHSConstants.FINANCIALS.equalsIgnoreCase(aoDocument
				.getDocCategory())) {
			aoPropertyMap.put(P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC,
					Boolean.TRUE);
		}
		aoPropertyMap
				.put(HHSConstants.FILE_NAME_PARAMETER, asFileRetrievalName);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setUploadProperties()");
	}

	/**
	 * This method will set Upload properties for Accelerator
	 * 
	 * @param aoRequest
	 *            ActionRequest Object
	 * @param asUserOrgType
	 *            a String value containing user organization type
	 * @param aoDocument
	 *            Document Object
	 * @param asCurrentDate
	 *            a String value containing current date
	 * @param aoPropertyMap
	 *            HashMap<String, Object> Object
	 * @param asFileRetrievalName
	 *            a String value containing file retrieval name
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	public static void setUploadPropertiesForAccelerator(
			ActionRequest aoRequest, String asUserOrgType, Document aoDocument,
			String asCurrentDate, HashMap<String, Object> aoPropertyMap,
			String asFileRetrievalName) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setUploadPropertiesForAccelerator()");
		String lsHelpCategory = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.HELP_CATEGORY);
		if (null != aoDocument.getDocCategory()
				&& !aoDocument.getDocCategory().isEmpty()
				&& aoDocument.getDocCategory().equalsIgnoreCase(
						ApplicationConstants.HELP)) {
			org.jdom.Document loXMLDoc = null;
			String lsVisibleTo = null;
			try {
				loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
						.getInstance().getCacheObject(
								ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
				String lsXPath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asUserOrgType + "\"] //"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
						+ aoDocument.getDocCategory() + "\"] //"
						+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
						+ ApplicationConstants.DOCUMENT_TYPE_PROVIDER_HELP
						+ "\"]/HelpCategory" + "[@name=\"" + lsHelpCategory
						+ "\"]";
				Element loHelpElement = XMLUtil.getElement(lsXPath, loXMLDoc);
				if (null != loHelpElement) {
					lsVisibleTo = loHelpElement
							.getAttributeValue(ApplicationConstants.VISIBLE_TO);
				}
			} catch (Exception aoEx) {
				LOG_OBJECT
						.Error("Exception occured in FileNetOperationsUtils: setUploadPropertiesForAccelerator method::",
								aoEx);
				throw new ApplicationException(
						"Exception occured in FileNetOperationsUtils: setUploadPropertiesForAccelerator method::",
						aoEx);
			}
			if (null != lsVisibleTo
					&& lsVisibleTo.equals(ApplicationConstants.PROVIDER)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.FALSE);
			} else if (null != lsVisibleTo
					&& lsVisibleTo
							.equals(ApplicationConstants.BOTH_AGENCY_PROVIDER)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.TRUE);
			} else {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.FALSE);
			}
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HELP_CATEGORY,
					aoRequest.getParameter(ApplicationConstants.HELP_CATEGORY));
			aoPropertyMap
					.put(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION,
							aoRequest
									.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
					asCurrentDate);
			String lsRadio = aoRequest.getParameter(ApplicationConstants.HELP);
			if (HHSConstants.YES_LOWERCASE.equalsIgnoreCase(lsRadio)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DISPLAY_HELP_ON_APP,
						true);
			} else {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DISPLAY_HELP_ON_APP,
						false);
			}
		}
		if (ApplicationConstants.DOC_SAMPLE.equalsIgnoreCase(aoDocument
				.getDocCategory())) {
			aoPropertyMap.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
					aoDocument.getSampleCategory());
			aoPropertyMap.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE,
					aoDocument.getSampleType());
			aoPropertyMap.put(P8Constants.PROPERTY_CE_DOC_TYPE,
					aoDocument.getMaskedDocType());
		}
		// Updated for R5- for sample doc type
		if (aoDocument.getDocType().equalsIgnoreCase(
				ApplicationConstants.DOCUMENT_TYPE_HELP)
				|| aoDocument.getDocType().startsWith(
						ApplicationConstants.DOC_SAMPLE)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A)
				|| ApplicationConstants.DOCUMENT_TYPE_PROVIDER_HELP
						.equalsIgnoreCase(aoDocument.getDocType())
				|| ApplicationConstants.DOCUMENT_TYPE_AGENCY_HELP
						.equalsIgnoreCase(aoDocument.getDocType())) {
			aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY,
					Boolean.TRUE);
		}
		aoPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrgType);
		aoPropertyMap
				.put(HHSConstants.FILE_NAME_PARAMETER, asFileRetrievalName);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setUploadPropertiesForAccelerator()");
	}

	/**
	 * This method will set Filter properties for shared status
	 * 
	 * @param aoRadioArray
	 *            a string array of radio buttons
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param aoDocument
	 *            a document object containing document properties
	 * @param aoFilterProps
	 *            a map containing filter properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setFilteredPropertiesForSharedStatus(
			String[] aoRadioArray, String asUserOrgType, String asUserOrg,
			Document aoDocument, HashMap aoFilterProps) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilteredPropertiesForSharedStatus()");
		aoDocument.setDocSharedStatus(aoRadioArray[0]);
		if (ApplicationConstants.SHARED_STATUS
				.equalsIgnoreCase(aoRadioArray[0])) {
			aoFilterProps.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, true);
			getOrgTypeFilter(aoFilterProps, asUserOrg, asUserOrgType);

		} else if (ApplicationConstants.UN_SHARED_STATUS
				.equalsIgnoreCase(aoRadioArray[0])) {
			aoFilterProps
					.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED, false);
			getOrgTypeFilter(aoFilterProps, asUserOrg, asUserOrgType);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilteredPropertiesForSharedStatus()");
	}

	/**
	 * This method is used to set the agency and provider list with whom
	 * documents are shared
	 * 
	 * @param asUserOrg
	 *            user organization name
	 * @param aoDocument
	 *            document object
	 * @param aoRequest
	 *            render request object
	 * @param aoUserSession
	 *            filenet session object
	 * @throws ApplicationException
	 *             application exception
	 */
	@SuppressWarnings("unchecked")
	public static boolean setSharedWithAgencyAndProvider(String asUserOrg,
			RenderRequest aoRequest, P8UserSession aoUserSession)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setSharedWithAgencyAndProvider()");
		TreeSet loProviderShareSet = new TreeSet();
		TreeSet loAgencyShareSet = new TreeSet();
		boolean loIsShared = false;
		loProviderShareSet = getSharedAgencyProviderList(
				(List<ProviderBean>) BaseCacheManagerWeb.getInstance()
						.getCacheObject(ApplicationConstants.PROV_LIST),
				aoUserSession, P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID,
				asUserOrg);
		// Start of Changes done for Defect QC 5997
		if (!loProviderShareSet.isEmpty()) {
			loIsShared = true;
		}
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_SET, StringUtils
				.join(loProviderShareSet.iterator(),
						ApplicationConstants.KEY_SEPARATOR));
		loAgencyShareSet = getSharedAgencyProviderList(
				(List<ProviderBean>) BaseCacheManagerWeb.getInstance()
						.getCacheObject(ApplicationConstants.PROV_LIST),
				aoUserSession, P8Constants.PROPERTY_CE_SHARED_AGENCY_ID,
				asUserOrg);
		aoRequest.setAttribute(ApplicationConstants.AGENCY_SET,
				loAgencyShareSet);
		if (!loAgencyShareSet.isEmpty()) {
			loIsShared = true;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setSharedWithAgencyAndProvider()");
		return loIsShared;
		// End of Changes done for Defect QC 5997
	}

	/**
	 * This method will get application status for organization Type
	 * <ul>
	 * <li>Execute <b>getApplicationStatus_DB</b> Transaction in DB</li>
	 * </ul>
	 * 
	 * @param asUserOrg
	 *            user organization id
	 * @return String status of the br application
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String getApplicationStatus(String asUserOrg)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getApplicationStatus()");
		Channel loChannel = new Channel();
		HashMap loAppStatusMap = new HashMap();
		loAppStatusMap.put(ApplicationConstants.ORG_ID, asUserOrg);
		loChannel.setData(HHSR5Constants.AO_APP_MAP, loAppStatusMap);
		TransactionManager.executeTransaction(loChannel,
				"getApplicationStatus_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		String lsStatus = (String) loChannel
				.getData(HHSR5Constants.LS_APP_STATUS);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getApplicationStatus()");
		return lsStatus;
	}

	/**
	 * This method will get the details of the business application
	 * corresponding to the provider passed in the argument
	 * <ul>
	 * <li>
	 * This method executed the transaction
	 * 'getBusinessAndServiceAndDeleteSuperSeding_DB'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param asProviderId
	 *            provider id
	 * @param asEmailId
	 *            email id of the provider
	 * @throws ApplicationException
	 *             application exception
	 */
	public static void getAndUpdateProviderStatus(String asEmailId,
			String asProviderId) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getAndUpdateProviderStatus()");
		HashMap<String, String> loOrganizationStatusMap = null;
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		Map<String, String> loSuperSedingProps = new HashMap<String, String>();
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		List<String> loLServiceApplicationStatuses = new ArrayList<String>();
		HashMap<String, String> loArgumentMap = new HashMap<String, String>();
		ProviderStatusBean loProviderStatusBean = null;
		String lsBrStatus = null;
		String lsBRAppId = null;
		String lsNewProviderStatus = null;
		String lsProviderName = null;
		String lsCurrentOrgStatus = null;
		Channel loChannel = new Channel();
		loArgumentMap.put(HHSConstants.AS_ORGANIZATION_ID, asProviderId);
		loArgumentMap.put(HHSR5Constants.APP_STATUS,
				ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED);
		loChannel.setData(HHSR5Constants.ARGUMENT_MAP, loArgumentMap);
		TransactionManager.executeTransaction(loChannel,
				"getProviderStatusDetails_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loOrganizationStatusMap = (HashMap<String, String>) loChannel
				.getData("asProviderStatusMap");
		if (null != loOrganizationStatusMap) {
			lsCurrentOrgStatus = loOrganizationStatusMap
					.get("ORGANIZATION_STATUS");
			if (lsCurrentOrgStatus
					.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED)) {
				lsProviderName = loOrganizationStatusMap
						.get(HHSConstants.ORGANIZATION_NAME);
				lsBRAppId = loOrganizationStatusMap
						.get(ApplicationConstants.BUSINESS_APPID_STRING);
				loRequiredProps.put(ApplicationConstants.PROVIDER_ID,
						asProviderId);
				loRequiredProps.put(HHSR5Constants.BUSINESS_APP_ID, lsBRAppId);
				loChannel.setData(ApplicationConstants.REQUIRED_PROPS,
						loRequiredProps);
				loSuperSedingProps.put(HHSConstants.ENTITY_ID, lsBRAppId);
				loChannel.setData(HHSR5Constants.ID_PROPS, loSuperSedingProps);
				TransactionManager.executeTransaction(loChannel,
						"getBusinessAndServiceAndDeleteSuperSeding_DB",
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel
						.getData(HHSConstants.STATUS_LIST);
				Iterator loIterator = loProviderStatusBeanList.iterator();
				while (loIterator.hasNext()) {
					loProviderStatusBean = (ProviderStatusBean) loIterator
							.next();
					if (loProviderStatusBean.getApplicationId()
							.equalsIgnoreCase(lsBRAppId)) {
						if (loProviderStatusBean.getSupersedingStatus() != null) {
							lsBrStatus = loProviderStatusBean
									.getSupersedingStatus();
						} else {
							lsBrStatus = loProviderStatusBean
									.getApplicationStatus();
						}
						break;
					}
				}
				loIterator = loProviderStatusBeanList.iterator();
				while (loIterator.hasNext()) {
					loProviderStatusBean = (ProviderStatusBean) loIterator
							.next();
					if (!loProviderStatusBean.getApplicationId()
							.equalsIgnoreCase(lsBRAppId)) {
						if (loProviderStatusBean.getSupersedingStatus() == null) {
							loLServiceApplicationStatuses
									.add(loProviderStatusBean
											.getApplicationStatus());
						} else {
							loLServiceApplicationStatuses
									.add(loProviderStatusBean
											.getSupersedingStatus());
						}
					}
				}
				lsNewProviderStatus = ProviderStatusBusinessRules
						.getProviderStatusOnBRWithdrawalRejection(lsBrStatus,
								loLServiceApplicationStatuses);
				updateProviderStatus(lsBRAppId, asEmailId, lsNewProviderStatus,
						lsCurrentOrgStatus, asProviderId, lsProviderName);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getAndUpdateProviderStatus()");
	}

	/**
	 * This method will get the details of the business application
	 * corresponding to the provider passed in the argument
	 * <ul>
	 * <li>Execute <b>conditionallyApprovedExpiredBatchForBusinessApp</b>
	 * transaction in case of entity type = Business Application</li>
	 * <li>Execute <b>conditionallyApprovedExpiredBatchForServiceApp</b>
	 * transaction in case of entity type = Service Application</li>
	 * </ul>
	 * 
	 * @param asProviderId
	 *            a String value containing provider id
	 * @param asEntityType
	 *            a String value containing entity type
	 * @param asEntityId
	 *            a String value containing entity id
	 * @throws ApplicationException
	 *             If Application Exception occurs
	 */
	public static void getAndUpdateProviderStatusForBatch(String asProviderId,
			String asEntityType, String asEntityId) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getAndUpdateProviderStatusForBatch()");
		Map<String, String> loSuperSedingProps = new HashMap<String, String>();
		HashMap<String, String> loArgumentMap = new HashMap<String, String>();
		String lsBRAppId = null;
		Channel loChannel = new Channel();

		if (null != asEntityType) {
			loArgumentMap.put(HHSConstants.AS_ORGANIZATION_ID, asProviderId);
			loArgumentMap.put(HHSR5Constants.APP_STATUS,
					ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
			loArgumentMap.put(ApplicationConstants.SUPER_SEDING_KEY_CA,
					ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
			loArgumentMap.put(ApplicationConstants.SUPER_SEDING_KEY_DRAFT,
					ApplicationConstants.APP_STATUS_DRAFT);
			loArgumentMap.put(HHSConstants.PROVIDER_ID, asProviderId);
			loArgumentMap.put(HHSR5Constants.BUSINESS_APP_ID, lsBRAppId);
			loArgumentMap.put(HHSConstants.ENTITY_ID, asEntityId);
			loChannel.setData(HHSR5Constants.ARGUMENT_MAP, loArgumentMap);
			loSuperSedingProps.put(HHSConstants.ENTITY_ID, lsBRAppId);
			loChannel.setData(HHSR5Constants.ID_PROPS, loSuperSedingProps);

			setAuditValuesInHashMap(asProviderId, HHSR5Constants.EMPTY_STRING,
					asEntityId, loChannel);
			// Update audit table for provider status change
			// Check the entity type for SA or BR
			if (asEntityType
					.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION)) {
				// delete from superseeding_status table for all BR and SA
				TransactionManager.executeTransaction(loChannel,
						"conditionallyApprovedExpiredBatchForBusinessApp");
			} else if (asEntityType
					.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION)) {
				// delete from superseeding_status table for only conditionally
				// approved, expired SA
				TransactionManager.executeTransaction(loChannel,
						"conditionallyApprovedExpiredBatchForServiceApp");
			}
			LOG_OBJECT.Debug("Updated audit info for provider status change");
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getAndUpdateProviderStatusForBatch()");
	}

	/**
	 * Method to Update current provider status.
	 * 
	 * @param asBRAppId
	 *            a string value containing BR application id
	 * @param asEmail
	 *            a string value containing email id of the provider
	 * @param asNewProviderStatus
	 *            a string value containing New Provider Status
	 * @param asCurrentProviderStatus
	 *            a string value containing Current Provider Status
	 * @param asProviderId
	 *            a string value containing Provider ID
	 * @param asProviderName
	 *            a string value containing Provider name
	 * @throws ApplicationException
	 *             If Application Exception occurs
	 */
	private static void updateProviderStatus(String asBRAppId, String asEmail,
			String asNewProviderStatus, String asCurrentProviderStatus,
			String asProviderId, String asProviderName)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.updateProviderStatus()");
		HashMap loRequiredProps = new HashMap();
		String lsEntityIdentifier = asProviderName;
		String lsData = "Status Changed To ".concat(asNewProviderStatus);
		Channel loChannel = new Channel();
		if ((!asNewProviderStatus.equalsIgnoreCase(asCurrentProviderStatus))
				&& (!asNewProviderStatus.isEmpty())) {
			// Update Provider Status in Organization Table
			loRequiredProps.put(HHSConstants.PROVIDER_ID, asProviderId);
			loRequiredProps.put(HHSR5Constants.ORG_STATUS, asNewProviderStatus);
			loRequiredProps.put(HHSR5Constants.STATUS_CHANGE_DATE, new Date());
			loRequiredProps.put(HHSConstants.MODIFIED_BY,
					P8Constants.PROPERTY_PE_TH_TASK_ACTIVITY_BY_SYSTEM);
			CommonUtil.addAuditDataToChannel(loChannel, asProviderId,
					ApplicationConstants.PROVIDER_STATUS_CHANGE,
					ApplicationConstants.STATUS_CHANGE, new Date(), asEmail,
					lsData, HHSConstants.PROVIDER, asProviderId,
					ApplicationConstants.FALSE, asBRAppId,
					HHSR5Constants.EMPTY_STRING,
					ApplicationConstants.AUDIT_TYPE_APPLICATION);
			loChannel.setData(HHSR5Constants.ENTITY_IDENTIFIER,
					lsEntityIdentifier);
			loChannel.setData(ApplicationConstants.REQUIRED_PROPS,
					loRequiredProps);
			TransactionManager.executeTransaction(loChannel,
					"updateOrganizationStatus",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.updateProviderStatus()");
	}

	/**
	 * Method to Update current provider status.
	 * <ul>
	 * <li>Execute <b>deleteDueDateReminderEntry_DB</b> Transaction to update
	 * current provider status</li>
	 * </ul>
	 * 
	 * @param asProviderId
	 *            a String containing provider id
	 * @throws ApplicationException
	 *             If Application Exception occurs
	 */
	private static void deleteDueDateReminder(String asProviderId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.deleteDueDateReminder()");
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		TransactionManager.executeTransaction(loChannel,
				"deleteDueDateReminderEntry_DB",
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.deleteDueDateReminder()");
	}

	/**
	 * This method will set the due date of the char500 id if is less then 60
	 * days
	 * 
	 * @param aoRequest
	 *            render request object
	 * @return lsDueDate a String value containing due date
	 * @throws ApplicationException
	 *             when any application exception Occurred
	 */
	public static String setDueDateMessage(RenderRequest aoRequest)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setDueDateMessage()");
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsOrgId = (String) loPortletSession.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loPortletSession
				.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
						PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		loChannel.setData(HHSConstants.PROVIDER_ID, lsOrgId);
		TransactionManager.executeTransaction(loChannel,
				"getDueDateForCHAR500", HHSR5Constants.TRANSACTION_ELEMENT_R5);
		String lsDueDate = (String) loChannel
				.getData(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE);
		if (null != lsDueDate) {
			Calendar loCalendarDueDate = new GregorianCalendar();
			loCalendarDueDate.setTime(DateUtil.getDatewithMMMFormat(DateUtil
					.getDateddmmmyyyyFormat(lsDueDate)));
			Date loCurrentDate = new Date(System.currentTimeMillis());
			long llDays = DateUtil.getDateDifference(loCurrentDate,
					loCalendarDueDate.getTime());
			if (llDays > 60) {
				lsDueDate = null;
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setDueDateMessage()");
		return lsDueDate;
	}

	/**
	 * This Method update Document Details in Section
	 * 
	 * @param aoDocumentsDetailsList
	 *            Document List
	 * @param aoMyBatisSession
	 *            Sql Session
	 * @throws ApplicationException
	 */
	public static void updateDocumentDetailsInSections(
			List<HashMap<String, String>> aoDocumentsDetailsList,
			SqlSession aoMyBatisSession) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.updateDocumentDetailsInSections()");
		HashMap<String, String> loDocumentDetailsMap = null;
		Iterator<HashMap<String, String>> loDocDetailsItr = aoDocumentsDetailsList
				.iterator();
		String lsOrgId = HHSR5Constants.EMPTY_STRING;
		String lsUserId = HHSR5Constants.EMPTY_STRING;
		String lsSectionId = HHSR5Constants.EMPTY_STRING;
		String lsBappId = HHSR5Constants.EMPTY_STRING;
		String lsFormName = HHSR5Constants.EMPTY_STRING;
		String lsFormVersion = HHSR5Constants.EMPTY_STRING;
		String lsSerVcAppId = HHSR5Constants.EMPTY_STRING;
		ApplicationService loAppService = new ApplicationService();
		try {
			if (null != aoDocumentsDetailsList) {
				loDocDetailsItr = aoDocumentsDetailsList.iterator();
				while (loDocDetailsItr.hasNext()) {
					loDocumentDetailsMap = loDocDetailsItr.next();
					lsOrgId = loDocumentDetailsMap
							.get(HHSConstants.ORGANIZATION_ID_KEY);
					lsUserId = loDocumentDetailsMap
							.get(ApplicationConstants.EMPID_STRING);
					lsSectionId = loDocumentDetailsMap
							.get(ApplicationConstants.SECTION_ID);
					lsBappId = loDocumentDetailsMap
							.get(ApplicationConstants.BUSINESS_APPID_STRING);
					lsFormName = loDocumentDetailsMap
							.get(ApplicationConstants.FORM_NAME);
					lsFormVersion = loDocumentDetailsMap
							.get(HHSConstants.FORM_VERSION);
					lsSerVcAppId = loDocumentDetailsMap
							.get(HHSR5Constants.SERVICE_APPLICATION_ID);
					if (lsSerVcAppId == null) {
						loAppService.insertUpdateDocStatus(lsBappId, lsOrgId,
								lsFormName, lsFormVersion, lsUserId,
								lsSectionId, null,
								ApplicationConstants.STATUS_DRAFT,
								aoMyBatisSession);
					} else {
						loAppService.insertUpdateDocStatusService(lsBappId,
								lsOrgId, lsSerVcAppId, lsUserId, lsSectionId,
								ApplicationConstants.STATUS_DRAFT,
								aoMyBatisSession);
						lsSectionId = lsSerVcAppId;
					}
					Map<String, StatusBean> loBusinessAppM = loAppService
							.getStatusMapForBusinessApp(lsOrgId, lsBappId,
									aoMyBatisSession);
					Map<String, StatusBean> loServiceAppM = loAppService
							.getServiceApplicationsStatus(lsBappId, lsOrgId,
									aoMyBatisSession);
					loAppService.applicationSubmissionStatus(lsBappId, lsOrgId,
							lsUserId, lsSerVcAppId, loBusinessAppM,
							loServiceAppM, lsSectionId, aoMyBatisSession);
				}
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Error:convert():", aoExp);
			throw new ApplicationException(
					"Error occurred while deleting document", aoExp);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.updateDocumentDetailsInSections()");
	}

	/**
	 * This method will convert file input stream to byte array
	 * 
	 * @param aoIs
	 *            a file input stream object
	 * @return a byte array of uploaded file
	 * @throws ApplicationException
	 */
	public static byte[] convert(InputStream aoIs) throws ApplicationException,
			IOException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.convert()");
		byte[] lbByteArray = new byte[100];
		ByteArrayOutputStream loBuffer = new ByteArrayOutputStream();
		try {
			int liBytesRead = 0;
			while ((liBytesRead = aoIs.read(lbByteArray)) != -1) {
				loBuffer.write(lbByteArray, 0, liBytesRead);
			}
		} catch (IOException aoExp) {
			LOG_OBJECT.Error("Error:convert():", aoExp);
			throw new ApplicationException(
					"Error occurred while scanning the documents", aoExp);
		} finally {
			loBuffer.flush();
			loBuffer.close();
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.convert()");
		return loBuffer.toByteArray();
	}

	/**
	 * This method is used to get the requesting machine ip
	 * 
	 * @param request
	 *            http request object
	 * @return local ip of the requesting machine
	 */
	public static String getClientIpAddr(HttpServletRequest request) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getClientIpAddr()");
		String loLocalIP = request.getHeader("X-Forwarded-For");
		if (loLocalIP == null || loLocalIP.length() == 0
				|| "unknown".equalsIgnoreCase(loLocalIP)) {
			loLocalIP = request.getHeader("Proxy-Client-IP");
		}
		if (loLocalIP == null || loLocalIP.length() == 0
				|| "unknown".equalsIgnoreCase(loLocalIP)) {
			loLocalIP = request.getHeader("WL-Proxy-Client-IP");
		}
		if (loLocalIP == null || loLocalIP.length() == 0
				|| "unknown".equalsIgnoreCase(loLocalIP)) {
			loLocalIP = request.getHeader("HTTP_CLIENT_IP");
		}
		if (loLocalIP == null || loLocalIP.length() == 0
				|| "unknown".equalsIgnoreCase(loLocalIP)) {
			loLocalIP = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (loLocalIP == null || loLocalIP.length() == 0
				|| "unknown".equalsIgnoreCase(loLocalIP)) {
			loLocalIP = request.getRemoteAddr();
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getClientIpAddr() :" + loLocalIP);
		return loLocalIP;
	}

	/**
	 * This method is used to get the message to display while deleting the
	 * document
	 * <ul>
	 * <li>Get the document id and organization id from the parameter</li>
	 * <li>Execute the transaction <code>checkDocLinkToAnyObject</code></li>
	 * <li>Depending upon the object name received from transaction layer
	 * prepare the message to display to the user</li>
	 * </ul>
	 * 
	 * @param asOrgType
	 *            a string value containing organization type
	 * @param asDocumentId
	 *            a string value containing document id
	 * @param asPropertyPath
	 *            a string value containing property file path
	 * @param abIsUploadingDoc
	 *            a boolean value
	 * @return String message to display to user
	 * @throws ApplicationException
	 */
	public static String checkLinkToAnyOtherObject(String asOrgType,
			String asDocumentId, String asPropertyPath, Boolean abIsUploadingDoc)
			throws ApplicationException {
		Channel loChannel = new Channel();
		String lsMessage = ApplicationConstants.EMPTY_STRING;
		String lsReplaceObjectExpression = ApplicationConstants.EMPTY_STRING;
		String lsReplaceActionExpression = ApplicationConstants.EMPTY_STRING;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkLinkToAnyOtherObject()");
		try {
			loChannel.setData(ApplicationConstants.DOCUMENT_ID, asDocumentId);
			TransactionManager.executeTransaction(loChannel,
					ApplicationConstants.CHECK_DOC_LINK_TO_ANY_OBJECT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lsReplaceObjectExpression = (String) loChannel
					.getData(ApplicationConstants.ATTACHED_OBJECT_NAME);
			if (null != lsReplaceObjectExpression
					&& !lsReplaceObjectExpression.isEmpty()
					&& !abIsUploadingDoc) {
				if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT)) {
					lsReplaceActionExpression = ApplicationConstants.PROCUREMENT_STATUS_RELEASED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				}
				// Made changes for Enhancement #6429 for Release 3.4.0
				else if (ApplicationConstants.AGENCY_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(HHSConstants.AGENCY_AWARD_DOC)) {
					lsReplaceObjectExpression = HHSConstants.AGENCY_AWARD_ERROR_MSG;
				}
				lsMessage = PropertyLoader.getProperty(asPropertyPath,
						ApplicationConstants.ATTACHED_TO_OBJECT_ERROR_MESSAGE);
				lsMessage = lsMessage.replace(
						ApplicationConstants.ACTION_EXPRESSION,
						lsReplaceActionExpression);
				lsMessage = lsMessage.replace(
						ApplicationConstants.OBJECT_EXPRESSION,
						lsReplaceObjectExpression);
			} else if (null != lsReplaceObjectExpression
					&& !lsReplaceObjectExpression.isEmpty() && abIsUploadingDoc) {
				return lsReplaceObjectExpression;
			}

		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Error:checkLinkToAnyOtherObject():", aoExp);
			throw new ApplicationException(
					"Error occurred while checking link to any other object",
					aoExp);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkLinkToAnyOtherObject()");
		return lsMessage;
	}

	/**
	 * This method is used to get the message to display while deleting the
	 * document for Release 3.5.0, QC 5630
	 * <ul>
	 * <li>Get the document id and organization id from the parameter</li>
	 * <li>Execute the transaction
	 * <code>checkDocLinkToAnyObjectNotInDraft</code></li>
	 * <li>Depending upon the object name received from transaction layer
	 * prepare the message to display to the user</li>
	 * </ul>
	 * 
	 * @param asOrgType
	 *            a string value containing organization type
	 * @param asDocumentId
	 *            a string value containing document id
	 * @param asPropertyPath
	 *            a string value containing property file path
	 * @param abIsUploadingDoc
	 *            a boolean value
	 * @return String message to display to user
	 * @throws ApplicationException
	 */
	public static String checkLinkToAnyOtherObjectNotInDraft(String asOrgType,
			String asDocumentId, String asPropertyPath, Boolean abIsUploadingDoc)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.checkLinkToAnyOtherObjectNotInDraft()");
		Channel loChannel = new Channel();
		String lsMessage = ApplicationConstants.EMPTY_STRING;
		String lsReplaceObjectExpression = ApplicationConstants.EMPTY_STRING;
		String lsReplaceActionExpression = ApplicationConstants.EMPTY_STRING;
		try {
			loChannel.setData(ApplicationConstants.DOCUMENT_ID, asDocumentId);
			TransactionManager
					.executeTransaction(
							loChannel,
							ApplicationConstants.CHECK_DOC_LINK_TO_ANY_OBJECT_NOT_IN_DRAFT,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lsReplaceObjectExpression = (String) loChannel
					.getData(ApplicationConstants.ATTACHED_OBJECT_NAME);
			if (null != lsReplaceObjectExpression
					&& !lsReplaceObjectExpression.isEmpty()
					&& !abIsUploadingDoc) {
				if (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT)) {
					lsReplaceActionExpression = ApplicationConstants.PROCUREMENT_STATUS_RELEASED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				} else if (ApplicationConstants.PROVIDER_ORG
						.equalsIgnoreCase(asOrgType)
						&& lsReplaceObjectExpression
								.equalsIgnoreCase(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET)) {
					lsReplaceActionExpression = ApplicationConstants.STATUS_SUBMITTED;
				}
				lsMessage = PropertyLoader.getProperty(asPropertyPath,
						ApplicationConstants.ATTACHED_TO_OBJECT_ERROR_MESSAGE);
				lsMessage = lsMessage.replace(
						ApplicationConstants.ACTION_EXPRESSION,
						lsReplaceActionExpression);
				lsMessage = lsMessage.replace(
						ApplicationConstants.OBJECT_EXPRESSION,
						lsReplaceObjectExpression);
			} else if (null != lsReplaceObjectExpression
					&& !lsReplaceObjectExpression.isEmpty() && abIsUploadingDoc) {
				return lsReplaceObjectExpression;
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Error:checkLinkToAnyOtherObjectNotInDraft():",
					aoExp);
			throw new ApplicationException(
					"Error occurred while checking link to any other object",
					aoExp);
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkLinkToAnyOtherObjectNotInDraft()");
		return lsMessage;
	}

	/**
	 * This method is used to get the status of target object while deleting the
	 * document for Release 3.5.0, QC 5630
	 * <ul>
	 * <li>Get the document id and organization id from the parameter</li>
	 * <li>Execute the transaction
	 * <code>checkDocLinkToAnyObjectNotInDraft</code></li>
	 * <li>Depending upon the object name received from transaction layer
	 * prepare the message to display to the user</li>
	 * </ul>
	 * 
	 * @param asDocumentId
	 *            a string value containing document id
	 * @return String message to display to user
	 * @throws ApplicationException
	 */
	public static String checkStatusOfObject(String asDocumentId)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.checkStatusOfObject()");
		Channel loChannel = new Channel();
		try {
			loChannel.setData(ApplicationConstants.DOCUMENT_ID, asDocumentId);
			TransactionManager.executeTransaction(loChannel,
					ApplicationConstants.CHECK_DOC_LINK_TO_ANY_OBJECT_IN_DRAFT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			String loDraft = (String) loChannel
					.getData(ApplicationConstants.ATTACHED_OBJECT_NAME);

			if (loDraft != null
					&& !loDraft.isEmpty()
					&& ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL
							.contains(loDraft)) {
				LOG_OBJECT
						.Info("Exiting FileNetOperationsUtils.checkStatusOfObject()");
				return ApplicationConstants.DRAFT_STATE;
			} else {
				LOG_OBJECT
						.Info("Exiting FileNetOperationsUtils.checkStatusOfObject()");
				return ApplicationConstants.EMPTY_STRING;
			}
		} catch (Exception aoExp) {
			LOG_OBJECT.Error("Error:checkStatusOfObject():", aoExp);
			throw new ApplicationException(
					"Error occurred while checking status of an object", aoExp);
		}

	}

	/**
	 * This method validates if user renames document from Screen S260 already
	 * exist for the same Document type.
	 * 
	 * <ul>
	 * <li>This method checks if the renamed document already exist in Document
	 * Vault against same Document Type.</li>
	 * <li>The output is true if a document with same name else the output is
	 * false.</li>
	 * 
	 * @param asDocumentName
	 *            New Document Name
	 * @param aoDocument
	 *            Document Bean for existing document
	 * @param asOrgId
	 *            Organization Id
	 * @param asOrgType
	 *            Organization Type
	 * @param aoUserSession
	 *            User Session
	 * @return true is document exists/false if doesn't
	 * @throws ApplicationException
	 */
	public static boolean isValidRenameEditProperties(String asDocumentName,
			Document aoDocument, String asOrgId, String asOrgType,
			P8UserSession aoUserSession) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.isValidRenameEditProperties()");
		boolean lbSkipPropertySave = false;
		// Check if name is unchanged on Click of Save, Only to put validation
		// for checking existing Document if the name is changed on Edit
		// Properties Screen
		if ((null != asDocumentName && null != aoDocument && null != aoDocument
				.getDocName())
				&& !(asDocumentName.equalsIgnoreCase(aoDocument.getDocName()))) {
			String lsReturnedDocIdonCheckExisting = null;
			lsReturnedDocIdonCheckExisting = checkDocExist(aoUserSession,
					asOrgId, asDocumentName, aoDocument.getDocType(),
					aoDocument.getDocCategory(), asOrgType);

			if (null != lsReturnedDocIdonCheckExisting
					&& !lsReturnedDocIdonCheckExisting.isEmpty()) {
				lbSkipPropertySave = true;
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.isValidRenameEditProperties()");
		return lbSkipPropertySave;
	}

	/**
	 * This method set values in map for audit entries
	 * 
	 * @param asProviderId
	 *            - organization id
	 * @param asStatus
	 *            - provider status
	 * @param asBRAppId
	 *            - business application id
	 * @param aoChannel
	 * @throws ApplicationException
	 */
	private static void setAuditValuesInHashMap(String asProviderId,
			String asStatus, String asBRAppId, Channel aoChannel)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setAuditValuesInHashMap()");
		HashMap<String, Object> loAuditMapForBatch = new HashMap<String, Object>();
		loAuditMapForBatch.put(HHSR5Constants.ORG_ID, asProviderId);
		loAuditMapForBatch.put(HHSConstants.EVENT_NAME,
				ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		loAuditMapForBatch.put(HHSConstants.EVENT_TYPE,
				ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		loAuditMapForBatch.put(ApplicationConstants.AUDIT_DATE, DateUtil
				.getFormattedDated(HHSR5Constants.DATE_FORMAT, new Date()));
		loAuditMapForBatch.put(ApplicationConstants.KEY_SESSION_USER_ID,
				ApplicationConstants.BATCH);
		loAuditMapForBatch.put(HHSR5Constants.DATA,
				ApplicationConstants.STATUS_CHANGED_TO.concat(asStatus));
		if (null != asBRAppId && asBRAppId.contains(HHSR5Constants.BR)) {
			loAuditMapForBatch.put(ApplicationConstants.ENTITY_TYPE,
					ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		} else if (null != asBRAppId && asBRAppId.contains(HHSR5Constants.SR)) {
			loAuditMapForBatch.put(ApplicationConstants.ENTITY_TYPE,
					ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		} else {
			loAuditMapForBatch.put(ApplicationConstants.ENTITY_TYPE,
					ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		}
		loAuditMapForBatch.put(HHSConstants.ENTITY_ID, asBRAppId);
		loAuditMapForBatch.put(HHSConstants.STAT, asStatus);
		loAuditMapForBatch.put(HHSR5Constants.PROVIDER_FLAG,
				ApplicationConstants.FALSE);
		loAuditMapForBatch.put(HHSR5Constants.APP_ID, asBRAppId);
		loAuditMapForBatch.put(ApplicationConstants.SECTIONID, asBRAppId);
		loAuditMapForBatch.put(HHSR5Constants.ENTITY_IDENTIFIER,
				ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		aoChannel.setData(HHSR5Constants.BATCH_AUDIT_MAP, loAuditMapForBatch);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setAuditValuesInHashMap()");
	}

	/**
	 * This method used for validation of char500 covered period for draft
	 * application
	 * 
	 * @param aoInsertPropMap
	 *            - map containing start date and end date
	 * @return ldMonthGap - double value used for validation
	 * @throws ApplicationException
	 */
	private static double validatePeriodCoveredDateChar500ForDraftApp(
			HashMap aoInsertPropMap) throws ApplicationException {
		int liStartMonth = 0;
		int liStartYear = 0;
		int liEndMonth = 0;
		int liEndYear = 0;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validatePeriodCoveredDateChar500ForDraftApp()");
		Calendar loStartCalendar = null;
		Calendar loEndCalendar = null;
		double ldMonthGap = 0;
		liStartMonth = DocumentLapsingUtility.getMonth((String) aoInsertPropMap
				.get(ApplicationConstants.PERIOD_COVER_FROM_MONTH).toString());
		liEndMonth = DocumentLapsingUtility.getMonth((String) aoInsertPropMap
				.get(ApplicationConstants.PERIOD_COVER_TO_MONTH).toString());
		liStartYear = Integer.parseInt((String) aoInsertPropMap.get(
				ApplicationConstants.PERIOD_COVER_FROM_YEAR).toString());
		liEndYear = Integer.parseInt((String) aoInsertPropMap.get(
				ApplicationConstants.PERIOD_COVER_TO_YEAR).toString());
		loStartCalendar = new GregorianCalendar(liStartYear, liStartMonth, 1);
		loEndCalendar = new GregorianCalendar(liEndYear, liEndMonth, 1);
		ldMonthGap = DateUtil.calculateDateDifference(
				loStartCalendar.getTime(), loEndCalendar.getTime());
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.validatePeriodCoveredDateChar500ForDraftApp()");
		return ldMonthGap;
	}

	// Added below methods for Release 5
	/**
	 * This method will set parameters in filter map based on
	 * folderpath/folderId
	 * 
	 * @param aoFilterProps
	 * @param asFolderPath
	 */
	public static void setPropFilter(HashMap<String, Object> aoFilterProps,
			String asFolderPath, String asFolderId, String asUserOrgType,
			String asUserOrg) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setPropFilter()");
		if (null != asUserOrgType && null != asUserOrg) {
			asUserOrg = asUserOrg.trim();
			asUserOrgType = asUserOrgType.trim();
		}
		if (null != asFolderPath && !asFolderPath.isEmpty()) {
			aoFilterProps.put(HHSR5Constants.FOLDER_PATH, asFolderPath);
		}

		else if (null != asFolderId
				&& (asFolderId
						.equalsIgnoreCase(HHSR5Constants.DOCUMENT_VAULT_ID))) {
			aoFilterProps.put(
					HHSR5Constants.FOLDER_PATH,
					setFolderPath(asUserOrgType, asUserOrg,
							HHSR5Constants.DOCUMENT_VAULT));
		} else if (null != asFolderId
				&& asFolderId.contains(HHSR5Constants.RECYCLE_BIN_ID)) {
			aoFilterProps.put(
					HHSR5Constants.FOLDER_PATH,
					setFolderPath(asUserOrgType, asUserOrg,
							HHSR5Constants.RECYCLE_BIN));
		} else {
			aoFilterProps.put(HHSR5Constants.FOLDER_ID, asFolderId);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setPropFilter()");
	}

	/**
	 * This method will return folderpath for initial load of document vault
	 * screen
	 * 
	 * @param asUserOrgType
	 * @param asUserOrg
	 * @return folderpath
	 */
	public static String setFolderPath(String asUserOrgType, String asUserOrg,
			String asFolderName) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setFolderPath()");
		String lsFolderPath = null;
		if (null != asUserOrgType && !asUserOrgType.isEmpty()
				&& asUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG)) {
			lsFolderPath = P8Constants.STRING_SINGLE_SLASH
					+ HHSR5Constants.PROVIDER + asUserOrg
					+ P8Constants.STRING_SINGLE_SLASH + asFolderName;
		} else if (null != asUserOrgType && !asUserOrgType.isEmpty()
				&& asUserOrgType.equalsIgnoreCase(HHSConstants.USER_AGENCY)) {
			lsFolderPath = P8Constants.STRING_SINGLE_SLASH
					+ HHSR5Constants.AGENCY + asUserOrg
					+ P8Constants.STRING_SINGLE_SLASH + asFolderName;
		} else if (null != asUserOrgType && !asUserOrgType.isEmpty()
				&& asUserOrgType.equalsIgnoreCase(HHSConstants.USER_CITY)) {
			lsFolderPath = P8Constants.STRING_SINGLE_SLASH
					+ HHSR5Constants.CITY + asFolderName;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setFolderPath()");
		return lsFolderPath;
	}

	/**
	 * This method will create channel object for createFolder method in filenet
	 * 
	 * @param aoRequest
	 * @return channel Object for new folder
	 * @throws ApplicationException
	 */
	public static Channel createFolder(ActionRequest aoRequest,
			String asSelectedFolderId) throws ApplicationException {
		Channel loChannel = new Channel();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.createFolder()");
		List<String> loLockList = new ArrayList<String>();
		try {
			String lsFolderName = aoRequest
					.getParameter(HHSR5Constants.FOLDER_NAME);
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserOrg = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession
					.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
							PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& lsUserOrgType.equalsIgnoreCase(HHSR5Constants.CITY_ORG)) {
				lsUserOrg = lsUserOrgType;
			}
			// here we are checking lock state of coming entityId, if lick state
			// found in Cache, we are returning. - Release 5
			loLockList.add(asSelectedFolderId);
			loChannel.setData(HHSR5Constants.P8USER_SESSION, loUserSession);
			loChannel.setData(HHSR5Constants.FOLDER_ID, asSelectedFolderId);
			loChannel.setData(HHSR5Constants.NEW_FOLDER_NAME, lsFolderName);
			loChannel.setData(HHSConstants.LS_USER_ORG_TYPE, lsUserOrgType);
			loChannel.setData(HHSR5Constants.ORG_NAME, lsUserOrg);
			loChannel.setData(HHSR5Constants.USER, lsUserId);
			loChannel.setData("folderExistCheck", true);
			loChannel.setData("entityType", HHSR5Constants.EMPTY_STRING);
			loChannel.setData("filenetEntityName", "Folder Creation");
			LOG_OBJECT.Info("Exiting FileNetOperationsUtils.createFolder()");
			return loChannel;
		} catch (Exception loAppEx) {
			LOG_OBJECT.Error("Exception occured while creating folder ",
					loAppEx);
			throw new ApplicationException(
					"Exception occured while creating folder ", loAppEx);
		}

	}

	/**
	 * The method will add lock to folder.
	 * 
	 * @param aoLockPathList
	 *            folder id
	 * @return boolean lock status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static boolean addLock(List<String> aoLockPathList,
			ActionRequest aoRequest) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.addLock()");
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						HHSR5Constants.APPLICATION_SETTING);
		String lsLockingFlag = (String) loApplicationSettingMap
				.get(HHSR5Constants.DOCUMENT_VAULT_LOCKING_FLAG);
		if (Boolean.valueOf(lsLockingFlag)) {
			try {
				String lsUserOrg = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ORG,
								PortletSession.APPLICATION_SCOPE);
				String lsUserOrgType = (String) aoRequest.getPortletSession()
						.getAttribute(
								ApplicationConstants.KEY_SESSION_ORG_TYPE,
								PortletSession.APPLICATION_SCOPE);
				if (null != lsUserOrgType
						&& lsUserOrgType
								.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
					lsUserOrg = lsUserOrgType;
				}
				//Change after Release 4.0.2 added constant
				Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb
						.getInstance().getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
				for (Iterator<String> iterator = aoLockPathList.iterator(); iterator
						.hasNext();) {
					String lsEntityId = (String) iterator.next();
					if (!lsEntityId.equals(HHSR5Constants.SLASH_DOCUMENT_VAULT)) {
						List<String> loLockList = null;
						if (null != loLockMap) {
							loLockList = loLockMap.get(lsUserOrg);
							if (loLockList == null) {
								loLockList = new ArrayList<String>();
							}
						} else {
							loLockMap = new HashMap<String, List<String>>();
							loLockList = new ArrayList<String>();
						}
						if (!loLockList.contains(lsEntityId)) {
							loLockList.add(lsEntityId);
							loLockMap.put(lsUserOrg, loLockList);
						}

					}
				}
				//Change after Release 4.0.2 added constant
				BaseCacheManagerWeb.getInstance().putCacheObject(
						HHSR5Constants.DOCUMENT_VAULT_LOCKS, loLockMap);
			} catch (ApplicationException loAppEx) {
				LOG_OBJECT
						.Error("Exception occured in adding lock to document",
								loAppEx);
				throw new ApplicationException(
						"Exception occured in adding lock to document ",
						loAppEx);
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.addLock()");
		return true;
	}

	/**
	 * The method will remove lock from folder.
	 * 
	 * @param aoLockPathList
	 *            the folder id
	 */
	public static void removeLock(List<String> aoLockPathList,
			ActionRequest aoRequest) throws ApplicationException {
		try {
			LOG_OBJECT.Info("Entered FileNetOperationsUtils.removeLock()");
			String lsUserOrg = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession()
					.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
							PortletSession.APPLICATION_SCOPE);
			removeLock(aoLockPathList, lsUserOrg, lsUserOrgType);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT
					.Error("Exception occured in removing lock from document",
							loAppEx);
			throw new ApplicationException(
					"Exception occured in removing lock from document", loAppEx);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.removeLock()");
	}

	/**
	 * The method will remove lock from folder.
	 * 
	 * @param aoLockPathList
	 *            the folder id
	 * @param asUserOrg
	 *            String
	 * @param asUserOrgType
	 *            String
	 * @throws ApplicationException
	 *             - if any exception occurs
	 */
	public static void removeLock(List<String> aoLockPathList,
			String asUserOrg, String asUserOrgType) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.removeLock()");
		if (null != asUserOrgType
				&& asUserOrgType.equalsIgnoreCase(HHSR5Constants.USER_CITY)) {
			asUserOrg = asUserOrgType;
		}
		//Change after Release 4.0.2 added constant
		Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb
				.getInstance().getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
		if (null != loLockMap && !loLockMap.isEmpty()) {
			for (Iterator<String> iterator = aoLockPathList.iterator(); iterator
					.hasNext();) {
				String lsEntityId = (String) iterator.next();
				if (null != loLockMap && loLockMap.containsKey(asUserOrg)) {
					List<String> loLockList = loLockMap.get(asUserOrg);
					if (null != loLockList && !loLockList.isEmpty()) {
						loLockList.remove(lsEntityId);
						if (loLockList.size() != 0) {
							loLockMap.put(asUserOrg, loLockList);
						} else {
							loLockMap.remove(asUserOrg);
						}

					}
					//Change after Release 4.0.2 added constant
					BaseCacheManagerWeb.getInstance().putCacheObject(
							HHSR5Constants.DOCUMENT_VAULT_LOCKS, loLockMap);
				}

			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.removeLock()");
	}

	/**
	 * The method is used to get Lock Details.
	 * 
	 * @param aoLockPathList
	 *            the folder id
	 * @param asUserOrg
	 *            String
	 * @return loLockedByList List
	 * @throws ApplicationException
	 *             - if any exception occurs
	 */
	public static List<String> getLockDetails(List<String> aoLockPathList,
			String aoUserOrg) throws ApplicationException {
		List<String> loLockedByList = new ArrayList<String>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getLockDetails()");
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						HHSR5Constants.APPLICATION_SETTING);
		String lsLockingFlag = (String) loApplicationSettingMap
				.get(HHSR5Constants.DOCUMENT_VAULT_LOCKING_FLAG);
		if (Boolean.valueOf(lsLockingFlag)) {
			try {
				if (null != aoLockPathList) {
				//Change after Release 4.0.2 added constant
					Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb
							.getInstance().getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
					if (null != loLockMap) {
						List<String> loLockList = loLockMap.get(aoUserOrg);
						if (loLockList != null) {
							for (Iterator<String> iterator = aoLockPathList
									.iterator(); iterator.hasNext();) {
								String lsEntityId = (String) iterator.next();
								for (Iterator iterator2 = loLockList.iterator(); iterator2
										.hasNext();) {
									String lsLockedPath = (String) iterator2
											.next();
									if (!(lsLockedPath
											.equals(HHSR5Constants.SLASH_DOCUMENT_VAULT) || lsEntityId
											.equals(HHSR5Constants.SLASH_DOCUMENT_VAULT))
											&& (lsLockedPath
													.toLowerCase()
													.startsWith(
															lsEntityId
																	.toLowerCase()) || lsEntityId
													.toLowerCase()
													.startsWith(
															lsLockedPath
																	.toLowerCase()))) {
										loLockedByList.add(lsLockedPath);
									}
								}
							}
						}
					}
				}
			} catch (ApplicationException e) {
				throw e;
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getLockDetails()");
		return loLockedByList;

	}

	/**
	 * This method is added in release 5 to check the lock status of the folder.
	 * It will check the folder id in the cache lock list and give error message
	 * if it found a folder is locked.
	 * 
	 * @param aoLockPathList
	 *            folder id
	 * @return boolean lockstatus
	 * @throws ApplicationException
	 */
	public static boolean checkLock(List<String> aoLockPathList,
			String aoUserOrg) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.checkLock()");
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						HHSR5Constants.APPLICATION_SETTING);
		String lsLockingFlag = (String) loApplicationSettingMap
				.get(HHSR5Constants.DOCUMENT_VAULT_LOCKING_FLAG);
		if (Boolean.valueOf(lsLockingFlag)) {
			try {
				if (null != aoLockPathList) {
				//Change after Release 4.0.2 added constant
					Map<String, List<String>> loLockMap = (Map<String, List<String>>) BaseCacheManagerWeb
							.getInstance().getCacheObject(HHSR5Constants.DOCUMENT_VAULT_LOCKS);
					if (null != loLockMap) {
						List<String> loLockList = loLockMap.get(aoUserOrg);
						if (loLockList != null) {
							for (Iterator<String> iterator = aoLockPathList
									.iterator(); iterator.hasNext();) {
								String lsEntityId = (String) iterator.next();
								for (Iterator iterator2 = loLockList.iterator(); iterator2
										.hasNext();) {
									String lsLockedPath = (String) iterator2
											.next();
									if (!(lsLockedPath
											.equals(HHSR5Constants.SLASH_DOCUMENT_VAULT) || lsEntityId
											.equals(HHSR5Constants.SLASH_DOCUMENT_VAULT))
											&& (lsLockedPath
													.toLowerCase()
													.startsWith(
															lsEntityId
																	.toLowerCase()) || lsEntityId
													.toLowerCase()
													.startsWith(
															lsLockedPath
																	.toLowerCase()))) {
										return true;
									}
								}
							}
						}
					}
				}
			} catch (ApplicationException e) {
				throw e;
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.checkLock()");
		return false;

	}

	/**
	 * This is the method which will validates and uploads any document into
	 * Document Vault.
	 * 
	 * <ul>
	 * <li>This method calls the method 'uploadNonEmptyDocument' to upload the
	 * document to filenet</li>
	 * </ul>
	 * Method Updated in R4
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @throws PortletException
	 *             If an Portlet Exception occurs
	 * @throws MessagingException
	 *             If an Messaging Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 * @throws ApplicationException
	 *             If an ApplicationException occurs
	 */
	public static void actionFileInformation(ActionRequest aoRequest,
			ActionResponse aoResponse) throws PortletException,
			MessagingException, IOException, ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.actionFileInformation()");
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT,
				PortletSession.APPLICATION_SCOPE);
		// BAFO Document upload change so as to read the BAFO Document details
		// from DOCTYPE.xml as the login user is agency user
		String lsUserOrgType = HHSR5Constants.EMPTY_STRING;
		String lsUserOrg = HHSR5Constants.EMPTY_STRING;
		if (HHSConstants.BAFO.equalsIgnoreCase(HHSPortalUtil.parseQueryString(
				aoRequest, HHSConstants.UPLOAD_DOC_TYPE))) {
			lsUserOrgType = HHSConstants.PROVIDER_ORG;
			lsUserOrg = HHSPortalUtil.parseQueryString(aoRequest,
					HHSConstants.ORGA_ID);
		} else {
			lsUserOrgType = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			lsUserOrg = (String) loSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
		}
		boolean lbIsLinkedToApp = false;
		List<String> loCatList = new ArrayList<String>();
		MultipartActionRequestParser loFormdata = new MultipartActionRequestParser(
				aoRequest);
		MimeBodyPart[] loMimeparts = loFormdata
				.getMimeBodyParts(ApplicationConstants.MIME_UPLOAD_FILE);
		MimeBodyPart[] loMimepartsNewVersion = loFormdata
				.getMimeBodyParts(ApplicationConstants.MIME_UPLOAD_NEW_VERSION);
		// get and preserve filter values
		HashMap<String, String> loFilterHiddenMap = null;
		loFilterHiddenMap = getHiddenFilterMap(loFormdata);
		MimeBodyPart loFiledata = null;
		String lsFileType = null;
		String lsDocType = null;
		Document loDocument = new Document();
		Channel loChannel = new Channel();
		HashMap<String, String> loGetContentMap = new HashMap<String, String>();
		InputStream loInputstream = null;
		try {
			if (loMimeparts == null && loMimepartsNewVersion == null) {
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_PAGE,
						ApplicationConstants.ERROR_PAGE);
				return;
			}

			if (null == loMimepartsNewVersion) {
				loFiledata = loMimeparts[0];
				setMimePartValueForUpload(loDocument, loFormdata, lsUserOrgType);
			} else {
				loFiledata = loMimepartsNewVersion[0];
				loDocument = (Document) ApplicationSession.getAttribute(
						aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				if (ApplicationConstants.DOCUMENT_TYPE_HELP
						.equalsIgnoreCase(loDocument.getDocCategory())) {
					loDocument.setHelpCategoryList(getHelpCategory(
							lsUserOrgType, loDocument.getDocCategory(),
							loDocument.getDocType()));
				}
			}
			String lsContentType = loFiledata.getContentType();
			StringBuffer lsBfContentType = new StringBuffer();
			int liSemiColon = lsContentType
					.lastIndexOf(HHSR5Constants.DELIMITER_SEMICOLON);
			if (liSemiColon == -1) {
				lsBfContentType.append(lsContentType);
			} else {
				lsBfContentType.append(lsContentType.substring(0, liSemiColon));
			}
			String lsFileName = loFiledata.getFileName();
			int liExtension = lsFileName.lastIndexOf(HHSR5Constants.DOT);
			lsFileType = lsFileName.substring(liExtension + 1);
			StringBuffer lsBfRealPath = new StringBuffer();
			loDocument.setContentType(lsBfContentType.toString());
			loDocument.setFileType(lsFileType.toUpperCase());
			loDocument.setFiledata(loFiledata);
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.FILENETDOCTYPE);
			lsDocType = loDocument.getDocType();
			if (null != loDocument.getDocType()
					&& !loDocument.getDocType().isEmpty()
					&& !loDocument.getDocType().startsWith(
							ApplicationConstants.DOC_SAMPLE)) {
				loCatList = getDocCategoryFromXML(loXMLDoc, lsDocType,
						lsUserOrgType);
				setDocCategorynDocType(loDocument, loCatList.get(0),
						lsUserOrgType);
			}
			loInputstream = loFiledata.getInputStream();

			if (loFiledata.getSize() > 0) {
				uploadNonEmptyDocument(aoRequest, aoResponse, loSession,
						lsUserOrgType, loUserSession, lsUserOrg,
						lbIsLinkedToApp, loMimepartsNewVersion,
						loFilterHiddenMap, loFiledata, lsFileType, lsDocType,
						loDocument, loChannel, loGetContentMap, loInputstream,
						lsBfRealPath);
			} else {
				generateErrorMessageForEmptyFile(loSession);
			}
		} catch (ApplicationException aoEx) {
			String lsMessage = aoEx.getMessage();
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileInformation method:: ",
							aoEx);
			throw aoEx;
		} catch (IOException aoEx) {
			String lsMessage = aoEx.getMessage();
			lsMessage = PropertyLoader.getProperty(
					P8Constants.ERROR_PROPERTY_FILE,
					HHSConstants.FILE_UPLOAD_FAIL_MESSAGE);
			LOG_OBJECT
					.Error("Exception occured in FileNetOperationsUtils: actionFileInformation method:: ",
							aoEx);
			throw new ApplicationException(lsMessage, aoEx);
		} finally {
			if (null != loInputstream) {
				loInputstream.close();
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.actionFileInformation()");
	}

	/**
	 * The method will give list of all docTypes based on organization Id.
	 * 
	 * @param asUserOrg
	 *            as organisation Id
	 * @param asRequestingDocType
	 *            as type of DocType is "rfp_award" otherwise null
	 * @return List of Doctypes
	 * @throws ApplicationException
	 */
	public static List<String> getDocType(String asUserOrg,
			String asRequestingDocType, String lsPartialUoDenom)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocType()");
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.FILENETDOCTYPE);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocType()");
		return getDoctypes(loXMLDoc, asUserOrg, asRequestingDocType,
				lsPartialUoDenom);
	}

	/**
	 * The method will get the procurement title list for type ahead.
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of procurement title entered by user
	 * @return list of procurement title
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static List<ExtendedDocument> getProcurementTitle(String asUserOrg,
			String lsPartialUoDenom) throws ApplicationException {
		Channel loChannel = new Channel();
		List<ExtendedDocument> loProcTitleList = new ArrayList<ExtendedDocument>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getProcurementTitle()");
		try {
			loChannel.setData(HHSConstants.USER_ORG, asUserOrg);
			loChannel
					.setData(HHSR5Constants.PARTIAL_KEY_WORD, lsPartialUoDenom);
			TransactionManager.executeTransaction(loChannel,
					"getProcurementTitle",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loProcTitleList = (List<ExtendedDocument>) loChannel
					.getData(HHSR5Constants.PROCUREMENT_LIST);
		} catch (ApplicationException aoAppex) {
			throw aoAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getProcurementTitle()");
		return loProcTitleList;
	}

	/**
	 * The method will the list of award e-pin for type ahead.
	 * 
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of award e-pin entered by user
	 * @return list of award e-pin
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAwardEPin(String asUserOrgType,
			String asUserOrg, String lsPartialUoDenom)
			throws ApplicationException {
		Channel loChannel = new Channel();
		List<String> loAwardEPinList = new ArrayList<String>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getAwardEPin()");
		try {
			loChannel.setData(HHSConstants.USER_ORG, asUserOrg);
			loChannel.setData(HHSR5Constants.USER_ORG_TYPE, asUserOrgType);
			loChannel
					.setData(HHSR5Constants.PARTIAL_KEY_WORD, lsPartialUoDenom);

			TransactionManager.executeTransaction(loChannel, "getAwardEPin",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loAwardEPinList = (List<String>) loChannel
					.getData(HHSR5Constants.AWARD_EPIN_LIST);
		} catch (ApplicationException aoAppex) {
			throw aoAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getAwardEPin()");
		return loAwardEPinList;
	}

	/**
	 * The method will get list of Contract award e-pin for type ahead.
	 * 
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of award e-pin entered by user
	 * @return list of award e-pin
	 * @throws ApplicationException
	 *             - if any exception occurs
	 */
	public static List<String> getContractAwardEPin(String asUserOrgType,
			String asUserOrg, String lsPartialUoDenom)
			throws ApplicationException {

		Channel loChannel = new Channel();
		List<String> loAwardEPinList = new ArrayList<String>();
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getContractAwardEPin()");
		try {
			loChannel.setData(HHSConstants.USER_ORG, asUserOrg);
			loChannel.setData(HHSR5Constants.USER_ORG_TYPE, asUserOrgType);
			loChannel
					.setData(HHSR5Constants.PARTIAL_KEY_WORD, lsPartialUoDenom);

			TransactionManager.executeTransaction(loChannel,
					"getContractAwardEPin",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loAwardEPinList = (List<String>) loChannel
					.getData(HHSR5Constants.AWARD_EPIN_LIST);
		} catch (ApplicationException aoAppex) {
			throw aoAppex;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getContractAwardEPin()");
		return loAwardEPinList;
	}

	/**
	 * The method will read DocType.xml and will return the list of DocTypes
	 * when a user type anything in the type ahead text box based on
	 * organisation type
	 * 
	 * @param aoDocTypeXML
	 * @param asOrgId
	 * @param asRequestingDocType
	 * @return
	 * @throws ApplicationException
	 */
	public static List<String> getDoctypes(Object aoDocTypeXML, String asOrgId,
			String asRequestingDocType, String lsPartialUoDenom)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDoctypes()");
		LOG_OBJECT.Debug("asOrgId = "+asOrgId+"*******asRequestingDocType="+asRequestingDocType+"*******lsPartialUoDenom="+lsPartialUoDenom);
		HashMap<String, String> loReqExceProp = new HashMap<String, String>();
		List<String> loDoctypesList = new ArrayList<String>();
		List<String> loArrayListDistinct = new ArrayList<String>();
		List<Element> loChildrenElementList = new ArrayList<Element>();
		String lsDoctypeXpath = null;
		String lsElementText = null;
		loReqExceProp
				.put(HHSConstants.AO_DOC_TYPE_XML, aoDocTypeXML.toString());
		try {
			if (aoDocTypeXML == null
					|| aoDocTypeXML.toString().equals(
							HHSR5Constants.EMPTY_STRING)) {

				ApplicationException loAppex = new ApplicationException(
						"Error in getDoctypes Method. Required Parameters are missing");
				loAppex.setContextData(loReqExceProp);
				throw loAppex;
			}
			if (null != lsPartialUoDenom
					&& !lsPartialUoDenom.isEmpty()
					&& !lsPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
				lsPartialUoDenom = lsPartialUoDenom.toLowerCase();
			}
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;
			if (null != asRequestingDocType && !asRequestingDocType.isEmpty()) {
				if (asRequestingDocType
						.equalsIgnoreCase(HHSConstants.UPLOADING_DOCUMENT_TYPE_RFP)) {
					if (null != lsPartialUoDenom
							&& !lsPartialUoDenom.isEmpty()
							&& !lsPartialUoDenom
									.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& null != asOrgId
							&& (asOrgId.equalsIgnoreCase(HHSConstants.CITY_ORG))) {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@display=\"true\"]/DocType[contains(lower-case(@name),'"
								+ lsPartialUoDenom + "')][@type=\"rfp_award\"]";
					} else if (null != lsPartialUoDenom
							&& !lsPartialUoDenom.isEmpty()
							&& !lsPartialUoDenom
									.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)
							&& null != asOrgId
							&& (asOrgId
									.equalsIgnoreCase(HHSConstants.USER_AGENCY))) {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@displayInAward=\"true\"]/DocType[contains(lower-case(@name),'"
								+ lsPartialUoDenom
								+ "')][@displayInAward=\"true\"]";
					} else {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@display=\"true\" and @type=\"rfp_award\"]/DocType[@type=\"rfp_award\"]";
					}
				} else if (asRequestingDocType
						.equalsIgnoreCase(HHSR5Constants.AGENCY_AWARD_DOC)) {
					if (null != lsPartialUoDenom
							&& !lsPartialUoDenom.isEmpty()
							&& !lsPartialUoDenom
									.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@displayInAward=\"true\"]/DocType[contains(lower-case(@name),'"
								+ lsPartialUoDenom
								+ "')][@displayInAward=\"true\"]";
					} else {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@displayInAward=\"true\"]/DocType[@displayInAward=\"true\"]";
					}
				} else if (asRequestingDocType
						.equalsIgnoreCase(HHSR5Constants.FINANACIAL)) {
					if (null != lsPartialUoDenom
							&& !lsPartialUoDenom.isEmpty()
							&& !lsPartialUoDenom
									.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@onlyFinancial=\"true\"]/DocType[contains(lower-case(@name),'"
								+ lsPartialUoDenom + "')]";
					} else {
						lsDoctypeXpath = "//"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY
								+ "[@name=\""
								+ asOrgId
								+ "\"]/DocumentCategory[@onlyFinancial=\"true\"]/DocType";
					}
				} else if (asRequestingDocType
						.equalsIgnoreCase("city_find_org")) {
					lsDoctypeXpath = "//"
							+ P8Constants.XML_DOC_ORG_ID_PROPERTY
							+ "[@name!=\""
							+ asOrgId
							+ "\"]/DocumentCategory/DocType[contains(lower-case(@name),'"
							+ lsPartialUoDenom + "')]";
				} else if (asRequestingDocType
						.equalsIgnoreCase("DocTypeForOtherOrg")) {
					lsDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
							+ "[@name!=\"" + asOrgId
							+ "\"]/DocumentCategory/DocType";
				} else if (asRequestingDocType
						.equalsIgnoreCase("DocTypeForAgencyShared")) {
					if (null != lsPartialUoDenom
							&& !lsPartialUoDenom.isEmpty()
							&& !lsPartialUoDenom
									.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING)) {
						lsDoctypeXpath = "//"
								+ "DocumentCategory[@display=\"true\" or not(@displayInApp) or not(@display)]/DocType[contains(lower-case(@name),'"
								+ lsPartialUoDenom + "')]";
					} else {
						lsDoctypeXpath = "//"
								+ "DocumentCategory[@display=\"true\" or not(@displayInApp) or not(@display)]/DocType";
					}
				}
			}
			// case added for getting full list of doctype on basis of user type
			else if (null != lsPartialUoDenom
					&& (lsPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_LISTING) || lsPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.UPLOAD_DOC_TYPE))) {
				lsDoctypeXpath = getDocTypeForOrg(asOrgId, lsDoctypeXpath,
						lsPartialUoDenom);
			} else {
				if (null != asOrgId
						&& asOrgId.equalsIgnoreCase(HHSConstants.PROVIDER_ORG)) {
					lsDoctypeXpath = "//"
							+ P8Constants.XML_DOC_ORG_ID_PROPERTY
							+ "[@name=\""
							+ asOrgId
							+ "\"]/DocumentCategory/DocType[contains(lower-case(@name),'"
							+ lsPartialUoDenom + "')]";
				} else if (null != asOrgId
						&& (asOrgId.equalsIgnoreCase(HHSConstants.CITY_ORG))) {
					lsDoctypeXpath = "//"
							+ P8Constants.XML_DOC_ORG_ID_PROPERTY
							+ "[@name=\""
							+ asOrgId
							+ "\"]/DocumentCategory[@display=\"true\"]/DocType[contains(lower-case(@name),'"
							+ lsPartialUoDenom + "')]";
				} else if (asOrgId.equalsIgnoreCase(HHSConstants.USER_AGENCY)) {
					lsDoctypeXpath = "//"
							+ P8Constants.XML_DOC_ORG_ID_PROPERTY
							+ "[@name=\""
							+ asOrgId
							+ "\"]/DocumentCategory/DocType[contains(lower-case(@name),'"
							+ lsPartialUoDenom + "')]";
				}

			}
			LOG_OBJECT.Debug("#########lsDoctypeXpath="+lsDoctypeXpath+"############loDocTypeXML="+loDocTypeXML);
			loChildrenElementList = XMLUtil.getElementList(lsDoctypeXpath,
					loDocTypeXML);
			if (null != loChildrenElementList
					&& !loChildrenElementList.isEmpty()) {
				for (int liChildCount = 0; liChildCount < loChildrenElementList
						.size(); liChildCount++) {

					lsElementText = loChildrenElementList.get(liChildCount)
							.getAttributeValue(HHSR5Constants.NAME);
					loDoctypesList.add(lsElementText);
				}
			}
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Exception in FileNetOperationsUtils : getDoctypes Method.",
					aoEx);
			loAppex.setContextData(loReqExceProp);
			throw loAppex;
		}
		TreeSet<String> loTreeSet = new TreeSet<String>();
		loTreeSet.addAll(loDoctypesList);
		Iterator loItrDistinct = loTreeSet.iterator();
		while (loItrDistinct.hasNext()) {
			loArrayListDistinct.add((String) loItrDistinct.next());
		}
		if (null != loArrayListDistinct && !loArrayListDistinct.isEmpty())
			Collections.sort(loArrayListDistinct);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDoctypes()");
		return loArrayListDistinct;
	}

	/**
	 * The method will give the docTypexpath on the basis of organization id.
	 * 
	 * @param asOrgId
	 * @param asDoctypeXpath
	 * @return DoctypeXpath
	 */
	private static String getDocTypeForOrg(String asOrgId,
			String asDoctypeXpath, String asPartialUoDenom) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocTypeForOrg()");
		if (null != asOrgId
				&& asOrgId.equalsIgnoreCase(HHSConstants.PROVIDER_ORG)) {
			if (null != asPartialUoDenom
					&& asPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_LISTING)) {
				// Adding not(@displayInApp) for Emergency Release 4.0.1
				asDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgId
						+ "\"]/DocumentCategory/DocType[not(@displayInApp)]";
			} else if (null != asPartialUoDenom
					&& asPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.UPLOAD_DOC_TYPE)) {
				asDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgId
						+ "\"]/DocumentCategory/DocType[not(@uploadFlag)]";
			}
		} else if (null != asOrgId
				&& (asOrgId.equalsIgnoreCase(HHSConstants.CITY_ORG))) {
			asDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
					+ "[@name=\"" + asOrgId
					+ "\"]/DocumentCategory[@display=\"true\"]/DocType";
		} else if (asOrgId.equalsIgnoreCase(HHSConstants.USER_AGENCY)) {
			// Added fix for Defect #7809
			if (null != asPartialUoDenom
					&& asPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.DOC_TYPE_LISTING)) {
				asDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgId
						+ "\"]/DocumentCategory/DocType[not(@searchFlag)]";
			} else if (null != asPartialUoDenom
					&& asPartialUoDenom
							.equalsIgnoreCase(HHSR5Constants.UPLOAD_DOC_TYPE)) {
				asDoctypeXpath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asOrgId
						+ "\"]/DocumentCategory/DocType[not(@uploadFlag)]";
			}
			// End Defect #7809
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocTypeForOrg()");
		return asDoctypeXpath;
	}

	/**
	 * The method will create map of required properties in case of sharing.
	 * 
	 * @return the hashmap containing required properties.
	 */
	public static HashMap<String, String> requiredDocsPropsForSharedDoc() {
		// as values in loHmReqProps for Release 5, initially it was empty for
		// all keys.
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredDocsPropsForSharedDoc()");
		HashMap<String, String> loHmReqProps = new HashMap<String, String>();
		loHmReqProps.put(HHSR5Constants.TEMPLATE_IDEN,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_SHARED_BY_ID,
				HHSR5Constants.SHR);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredDocsPropsForSharedDoc()");
		return loHmReqProps;
	}

	/**
	 * The method will create map of required properties for an organization.
	 * 
	 * @return the hashmap containing required properties.
	 */
	public static HashMap<String, String> requiredDocsPropsForOrgDoc() {
		// Adding class alias(DOC for DOCUMENT class in Filenet and FO for
		// HHS_CUSTOM_FOLDER class)
		// as values in loHmReqProps for Release 5, initially it was empty for
		// all keys.
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredDocsPropsForOrgDoc()");
		HashMap<String, String> loHmReqProps = new HashMap<String, String>();
		loHmReqProps.put(HHSR5Constants.TEMPLATE_IDEN,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.PROVIDER_ID_HHS_SHARED,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// check added for agency
		loHmReqProps.put(HHSConstants.ORGANIZATION_ID_KEY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.SHARED_ENTITY_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// check added for agency
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredDocsPropsForOrgDoc()");
		return loHmReqProps;
	}

	/**
	 * The method will set the property map in case of HELP docType.
	 * 
	 * @param aoRequest
	 *            as action request
	 * @param asUserOrgType
	 *            as organization type
	 * @param aoDocument
	 *            as document object
	 * @param asCurrentDate
	 *            as modified date
	 * @param aoPropertyMap
	 *            the property map
	 * @param asFileRetrievalName
	 * @throws ApplicationException
	 */
	public static void setUploadPropertiesForAcceleratorInBean(
			ActionRequest aoRequest, String asUserOrgType, Document aoDocument,
			String asCurrentDate, HashMap<String, Object> aoPropertyMap,
			String asFileRetrievalName) throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setUploadPropertiesForAcceleratorInBean()");
		String lsHelpCategory = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.HELP_CATEGORY);
		if (null != aoDocument.getDocCategory()
				&& !aoDocument.getDocCategory().isEmpty()
				&& aoDocument.getDocCategory().equalsIgnoreCase(
						ApplicationConstants.HELP)) {
			org.jdom.Document loXMLDoc = null;
			String lsVisibleTo = null;
			try {
				loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
						.getInstance().getCacheObject(
								ApplicationConstants.FILENET_EXTENDED_DOC_TYPE);
				String lsXPath = "//" + P8Constants.XML_DOC_ORG_ID_PROPERTY
						+ "[@name=\"" + asUserOrgType + "\"] //"
						+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\""
						+ aoDocument.getDocCategory() + "\"] //"
						+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
						+ ApplicationConstants.DOCUMENT_TYPE_PROVIDER_HELP
						+ "\"]/HelpCategory" + "[@name=\"" + lsHelpCategory
						+ "\"]";
				Element loHelpElement = XMLUtil.getElement(lsXPath, loXMLDoc);
				if (null != loHelpElement) {
					lsVisibleTo = loHelpElement
							.getAttributeValue(ApplicationConstants.VISIBLE_TO);
				}
			} catch (Exception aoEx) {
				LOG_OBJECT
						.Error("Exception occured in FileNetOperationsUtils: setUploadPropertiesForAccelerator method::",
								aoEx);
				throw new ApplicationException(
						"Exception occured in FileNetOperationsUtils: setUploadPropertiesForAccelerator method::",
						aoEx);
			}
			if (null != lsVisibleTo
					&& lsVisibleTo.equals(ApplicationConstants.PROVIDER)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.FALSE);
			} else if (null != lsVisibleTo
					&& lsVisibleTo
							.equals(ApplicationConstants.BOTH_AGENCY_PROVIDER)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.TRUE);
			} else {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_AGENCY_HELP_DOC,
						Boolean.TRUE);
				aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_PROVIDER_HELP_DOC,
						Boolean.FALSE);
			}
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HELP_CATEGORY,
					aoRequest.getParameter(ApplicationConstants.HELP_CATEGORY));
			aoPropertyMap
					.put(P8Constants.PROPERTY_CE_DOCUMENT_DESCRIPTION,
							aoRequest
									.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			aoPropertyMap.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
					asCurrentDate);
			String lsRadio = aoRequest.getParameter(ApplicationConstants.HELP);
			if (HHSConstants.YES_LOWERCASE.equalsIgnoreCase(lsRadio)) {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DISPLAY_HELP_ON_APP,
						true);
			} else {
				aoPropertyMap.put(P8Constants.PROPERTY_CE_DISPLAY_HELP_ON_APP,
						false);
			}
		}
		if (ApplicationConstants.DOC_SAMPLE.equalsIgnoreCase(aoDocument
				.getDocCategory())) {
			aoPropertyMap.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
					aoDocument.getSampleCategory());
			aoPropertyMap.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE,
					aoDocument.getSampleType());
		}
		if (aoDocument.getDocType().equalsIgnoreCase(
				ApplicationConstants.DOCUMENT_TYPE_HELP)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOC_SAMPLE)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A)
				|| ApplicationConstants.DOCUMENT_TYPE_PROVIDER_HELP
						.equalsIgnoreCase(aoDocument.getDocType())
				|| ApplicationConstants.DOCUMENT_TYPE_AGENCY_HELP
						.equalsIgnoreCase(aoDocument.getDocType())) {
			aoPropertyMap.put(P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY,
					Boolean.TRUE);
		}
		aoPropertyMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrgType);
		aoPropertyMap
				.put(HHSConstants.FILE_NAME_PARAMETER, asFileRetrievalName);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setUploadPropertiesForAcceleratorInBean()");
	}

	/**
	 * This method is added in release 5 which will fetch the folder structure
	 * of the organization
	 * 
	 * @param asCustomOrgId
	 * @param asOrgId
	 * @return
	 * @throws IOException
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static String getOrgFolderStructure(String asOrgId, String asDivId,
			String asOrgType) throws ApplicationException, ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getOrgFolderStructure()");
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		List<FolderMappingBean> loListJsTreeBean = null;
		String lsFolderJson = null;
		try {
			loHashMap.put(HHSR5Constants.ORG_ID, asOrgId);
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSR5Constants.GET_TREE_DATA,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loListJsTreeBean = (List<FolderMappingBean>) loChannelObj
					.getData(HHSR5Constants.RETURN_TREE_LIST);
			JsonTreeBean loJsonBean;
			if (null != loListJsTreeBean && loListJsTreeBean.size() > 0) {
				if (null != asDivId
						&& asDivId.equalsIgnoreCase(HHSR5Constants.LEFT_TREE)) {

					for (Iterator iterator2 = loListJsTreeBean.iterator(); iterator2
							.hasNext();) {
						FolderMappingBean loBean = (FolderMappingBean) iterator2
								.next();
						loJsonBean = new JsonTreeBean();
						loJsonBean.setOrganizationId(asOrgId);
						loJsonBean.setOrganizationType(asOrgType);
						loBean.setData(loJsonBean);
					}

					FolderMappingBean loBean = new FolderMappingBean();
					loBean.setId(HHSR5Constants.RECYCLE_BIN_ID
							+ ApplicationConstants.TILD + asOrgId);
					loBean.setText(HHSR5Constants.RECYCLE_BIN);
					loBean.setType(HHSR5Constants.RECYCLE_BIN_TYPE);
					loBean.setParent(HHSConstants.DELIMITER_SINGLE_HASH);
					loJsonBean = new JsonTreeBean();
					loJsonBean.setOrganizationId(asOrgId);
					loJsonBean.setOrganizationType(asOrgType);
					loBean.setData(loJsonBean);
					loListJsTreeBean.add(loBean);
				}
				lsFolderJson = JSONUtility.convertToString(loListJsTreeBean);
			} else {
				if (null != asDivId
						&& asDivId.equalsIgnoreCase(HHSR5Constants.LEFT_TREE)) {
					lsFolderJson = "[{\"type\":\"folder\",\"id\":\"DocumentVault\",\"text\":\"Document Vault\",\"parent\":\"#\",\"data\":{\"organizationId\":\""
							+ asOrgId
							+ "\",\"organizationType\":\""
							+ asOrgType
							+ "\"}},{\"type\":\"recycleBin\",\"id\":\"RecycleBin"
							+ HHSR5Constants.TILD
							+ asOrgType
							+ HHSR5Constants.HYPHEN
							+ asOrgId
							+ "\",\"text\":\"Recycle Bin\",\"parent\":\"#\",\"data\":{\"organizationId\":\""
							+ asOrgId
							+ "\",\"organizationType\":\""
							+ asOrgType + "\"}}]";
				} else {
					lsFolderJson = "[{\"type\":\"folder\",\"id\":\"DocumentVault\",\"text\":\"Document Vault\",\"parent\":\"#\",\"data\":{\"organizationId\":\""
							+ asOrgId
							+ "\",\"organizationType\":\""
							+ asOrgType + "\"}}]";
				}

			}
			LOG_OBJECT
					.Info("Exiting FileNetOperationsUtils.getOrgFolderStructure()");
			return lsFolderJson;
		} catch (ApplicationException aoAppEx) {
			LOG_OBJECT
					.Error("Error Occured while fetching folder structure for organization",
							aoAppEx);
			throw aoAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT
					.Error("Error Occured while fetching folder structure for organization",
							aoExp);
			throw new ApplicationException(
					"Error Occured while fetching folder structure for organization",
					aoExp);
		}

	}

	/**
	 * This method is added as a part of release 5 , this method will be used to
	 * fetch the folder structure of the logged in user. Adding two parameters
	 * for defect 8150
	 * 
	 * @param aoList
	 * @param asUserOrgType
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getOtherOrgFolderStructure(Set<String> aoList,
			String asUserOrgType, P8UserSession aoUserSession, String asUserOrg)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getOtherOrgFolderStructure()");
		HashMap<String, List<String>> loSharedIdMap;
		List<FolderMappingBean> loListJsTreeBean;
		List<FolderMappingBean> loListJsTreeBeanMain;
		String lsRecycleBinParent = HHSR5Constants.EMPTY_STRING;
		String lsFirstJson = null;
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		StringBuffer loJsonString = new StringBuffer();
		JsonTreeBean loJsonBean = null;
		try {
			if (aoList != null) {
				loJsonString.append("[");
				Iterator loItr = aoList.iterator();
				while (loItr.hasNext()) {
					String lsCustmOrg = (String) loItr.next();
					String[] loTemp = lsCustmOrg
							.split(ApplicationConstants.TILD);
					loHashMap.put(HHSR5Constants.ORG_ID, loTemp[0]);
					loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
					HHSTransactionManager.executeTransaction(loChannelObj,
							HHSR5Constants.GET_TREE_DATA,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					loListJsTreeBeanMain = (List<FolderMappingBean>) loChannelObj
							.getData(HHSR5Constants.RETURN_TREE_LIST);
					if (null != asUserOrgType && !asUserOrgType.isEmpty()
							&& !asUserOrgType.equalsIgnoreCase("agency_org")
							&& !asUserOrgType.equalsIgnoreCase("provider_org")) {
						if (null != loListJsTreeBeanMain
								&& loListJsTreeBeanMain.size() > 0) {
							for (Iterator iterator2 = loListJsTreeBeanMain
									.iterator(); iterator2.hasNext();) {
								FolderMappingBean loBean = (FolderMappingBean) iterator2
										.next();
								loJsonBean = new JsonTreeBean();
								loJsonBean.setOrganizationId(loTemp[0]);
								loJsonBean.setOrganizationType(loTemp[1]);
								loBean.setData(loJsonBean);
								if (loBean.getText().equalsIgnoreCase(
										"Document Vault")) {
									loBean.setText(loTemp[2]);
									lsRecycleBinParent = loBean.getId();
								}
							}
							FolderMappingBean loBean = new FolderMappingBean();
							loJsonBean = new JsonTreeBean();
							loJsonBean.setOrganizationId(loTemp[0]);
							loJsonBean.setOrganizationType(loTemp[1]);
							loBean.setData(loJsonBean);
							loBean.setId(HHSR5Constants.RECYCLE_BIN_ID
									+ ApplicationConstants.TILD + loTemp[1]
									+ "-" + loTemp[0]);
							loBean.setText(HHSR5Constants.RECYCLE_BIN);
							loBean.setType("recycleBin");
							loBean.setParent(lsRecycleBinParent);
							loListJsTreeBeanMain.add(loBean);
							lsFirstJson = JSONUtility
									.convertToString(loListJsTreeBeanMain);
							lsFirstJson = lsFirstJson.substring(1,
									lsFirstJson.lastIndexOf("]"));
							loJsonString.append(lsFirstJson);
							if (loItr.hasNext()) {
								loJsonString.append(",");
							}
						} else {
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"DocumentVault");
							loJsonString.append("\",\"text\":\" ");
							loJsonString.append(loTemp[2]);
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": { \"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(loTemp[1]);
							loJsonString
									.append("\"}},{\"type\":\"recycleBin\",\"id\":\"RecycleBin");
							loJsonString.append(HHSR5Constants.TILD + loTemp[1]
									+ HHSR5Constants.HYPHEN + loTemp[0]);
							loJsonString
									.append("\",\"text\":\"RecycleBin\",\"parent\":\"DocumentVault\",\"data\": { \"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(loTemp[1]);
							loJsonString.append("\"}}");
							if (loItr.hasNext()) {
								loJsonString.append(",");
							}
						}
					} else {

						if (null != loListJsTreeBeanMain
								&& loListJsTreeBeanMain.size() > 0) {
							for (Iterator iterator2 = loListJsTreeBeanMain
									.iterator(); iterator2.hasNext();) {
								FolderMappingBean loBean = (FolderMappingBean) iterator2
										.next();
								if (loBean.getText().equalsIgnoreCase(
										"Document Vault")) {
									lsRecycleBinParent = loBean.getId();
								}
							}
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"");
							loJsonString.append(lsRecycleBinParent);
							loJsonString.append("\",\"text\":\" ");
							loJsonString.append(loTemp[2]);
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": {\"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(loTemp[1]);
							loJsonString.append("\"}}");
							// Adding below trasaction for defect 8150
							Channel loChannel = new Channel();
							String lsAgencyType = null;
							if (null != asUserOrgType
									&& asUserOrgType
											.equalsIgnoreCase("provider_org")) {
								lsAgencyType = "HHSProviderID";
							} else if (null != asUserOrgType
									&& asUserOrgType
											.equalsIgnoreCase("agency_org")) {
								lsAgencyType = "HHS_AGENCY_ID";
							}

							loChannel
									.setData("aoFilenetSession", aoUserSession);
							loChannel.setData("asAgencyType", lsAgencyType);
							loChannel.setData("asProviderId", loTemp[0]);
							loChannel.setData("asShareWith", asUserOrg);
							TransactionManager.executeTransaction(loChannel,"getSharedDocIdList",HHSR5Constants.TRANSACTION_ELEMENT_R5);
							loSharedIdMap = (HashMap<String, List<String>>) loChannel.getData("providerList");

                            loSharedIdMap = (HashMap<String, List<String>>) BaseCacheManagerWeb.getInstance().getCacheObject("SharedIdMap");

							List<String> loTempList = loSharedIdMap.get(loTemp[0]);
							
	                         //[Start] QC9283 R7.10.0 Wrapping with if statement for null point Exception 
                            if( loTempList != null && !loTempList.isEmpty() ){
                                Iterator iterator = loTempList.iterator();
    							while (iterator.hasNext()) {
    								String lsFolderId = (String) iterator.next();
    								loHashMap.put("folderFilenetId", lsFolderId);
    								loHashMap.put(HHSR5Constants.ORG_ID, loTemp[0]);
    								loChannelObj.setData(HHSConstants.AO_HASH_MAP,
    										loHashMap);
    								HHSTransactionManager.executeTransaction(
    										loChannelObj, "getJstreeDataOrg",
    										HHSR5Constants.TRANSACTION_ELEMENT_R5);
    								loListJsTreeBean = (List<FolderMappingBean>) loChannelObj
    										.getData(HHSR5Constants.RETURN_TREE_LIST);
    								for (int iCheck = 0; iCheck < loListJsTreeBean
    										.size(); iCheck++) {
    									FolderMappingBean loBean = loListJsTreeBean
    											.get(iCheck);
    									loJsonBean = new JsonTreeBean();
    									loJsonBean.setOrganizationId(loTemp[0]);
    									loJsonBean.setOrganizationType(loTemp[1]);
    									loBean.setData(loJsonBean);
    									if (loBean.getId().equalsIgnoreCase(
    											lsFolderId)) {
    										loBean.setParent(lsRecycleBinParent);
    									}
    								}
    								lsFirstJson = JSONUtility
    										.convertToString(loListJsTreeBean);
    								lsFirstJson = lsFirstJson.substring(1,
    										lsFirstJson.lastIndexOf("]"));
    								if (null != lsFirstJson
    										&& !lsFirstJson.isEmpty()) {
    									loJsonString.append(",");
    								}
    								loJsonString.append(lsFirstJson);
    
    							}
                            }//[End]  QC9283 R7.10.0 Wrapping with if statement for null point Exception
							if (loItr.hasNext()) {
								loJsonString.append(",");
							}
						} else {
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"DocumentVault");
							loJsonString.append("\",\"text\":\" ");
							loJsonString.append(loTemp[2]);
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": {\"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(loTemp[1]);
							loJsonString.append("\"}}");

							if (loItr.hasNext()) {
								loJsonString.append(",");
							}
						}
					}

				}
				loJsonString.append("]");
			}
			LOG_OBJECT
					.Info("Exiting FileNetOperationsUtils.getOtherOrgFolderStructure()");
			return loJsonString.toString();
		} catch (ApplicationException aoAppEx) {
			LOG_OBJECT
					.Error("Error Occured while fetching folder structure for other organization",
							aoAppEx);
			throw aoAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT
					.Error("Error Occured while fetching folder structure for other organization",
							aoExp);
			throw new ApplicationException(
					"Error Occured while fetching folder structure for nother organization",
					aoExp);
		}
	}

	/**
	 * The method will give the folder structure for provider and agency users.
	 * 
	 * @param aoListJsTreeBeanMain
	 *            as list of FolderMappingBean
	 * @param asRecycleBinParent
	 * @param aoHashMap
	 * @param aoChannelObj
	 * @param aoJsonString
	 * @param loTemp
	 * @param aoItr
	 * @return
	 * @throws ApplicationException
	 * @throws JsonProcessingException
	 */
	private static String getOrgFolderStructure(
			List<FolderMappingBean> aoListJsTreeBeanMain,
			String asRecycleBinParent, HashMap<String, String> aoHashMap,
			Channel aoChannelObj, StringBuffer aoJsonString, String[] loTemp,
			Iterator aoItr) throws ApplicationException,
			JsonProcessingException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getOrgFolderStructure()");
		HashMap<String, List<String>> loSharedIdMap;
		List<FolderMappingBean> loListJsTreeBean;
		String lsFirstJson;
		if (null != aoListJsTreeBeanMain && aoListJsTreeBeanMain.size() > 0) {
			for (Iterator iterator2 = aoListJsTreeBeanMain.iterator(); iterator2
					.hasNext();) {
				FolderMappingBean loBean = (FolderMappingBean) iterator2.next();
				if (loBean.getText().equalsIgnoreCase(
						HHSR5Constants.DOCUMENT_VAULT)) {
					asRecycleBinParent = loBean.getId();
				}
			}
			String[] lsTemp1 = loTemp[1].split(HHSConstants.HYPHEN);
			aoJsonString.append("{\"type\":\"folder\",\"id\":\"");
			aoJsonString.append(asRecycleBinParent);
			aoJsonString.append("\",\"text\":\"");
			aoJsonString.append(lsTemp1[1]);
			aoJsonString.append("\",\"parent\":\"#\"}");

			loSharedIdMap = (HashMap<String, List<String>>) BaseCacheManagerWeb
					.getInstance().getCacheObject(HHSR5Constants.SHARED_MAP_ID);
			List<String> loTempList = loSharedIdMap.get(loTemp[0]);
			Iterator iterator = loTempList.iterator();
			while (iterator.hasNext()) {
				String lsFolderId = (String) iterator.next();
				aoHashMap.put(HHSR5Constants.FOLDER_ID_FILENET, lsFolderId);
				aoHashMap.put(HHSR5Constants.ORG_ID, loTemp[0]);
				aoChannelObj.setData(HHSConstants.AO_HASH_MAP, aoHashMap);
				HHSTransactionManager.executeTransaction(aoChannelObj,
						HHSR5Constants.GET_TREE_ORG,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loListJsTreeBean = (List<FolderMappingBean>) aoChannelObj
						.getData(HHSR5Constants.RETURN_TREE_LIST);
				for (int iCheck = 0; iCheck < loListJsTreeBean.size(); iCheck++) {
					FolderMappingBean loBean = loListJsTreeBean.get(iCheck);
					if (loBean.getId().equalsIgnoreCase(lsFolderId)) {
						loBean.setParent(asRecycleBinParent);
					}
				}
				lsFirstJson = JSONUtility.convertToString(loListJsTreeBean);
				lsFirstJson = lsFirstJson.substring(1,
						lsFirstJson.lastIndexOf(HHSConstants.SQUARE_BRAC_END));
				aoJsonString.append(lsFirstJson);
				if (null != lsFirstJson && !lsFirstJson.isEmpty()) {
					aoJsonString.append(HHSConstants.COMMA);
				}
			}
		} else {
			String[] lsTemp1 = loTemp[1].split(HHSConstants.HYPHEN);
			aoJsonString.append("{\"type\":\"folder\",\"id\":\"/DocumentVault");
			aoJsonString.append(loTemp[0] + "\",\"text\":\"");
			aoJsonString.append(lsTemp1[1]);
			aoJsonString.append("\",\"parent\":\"#\"}");
			if (aoItr.hasNext()) {
				aoJsonString.append(HHSConstants.COMMA);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getOrgFolderStructure()");
		return asRecycleBinParent;
	}

	/**
	 * The method will give the folder sructure for city user.
	 * 
	 * @param aoListJsTreeBeanMain
	 * @param asRecycleBinParent
	 * @param aoJsonString
	 * @param aoItr
	 * @param aoTemp
	 * @return
	 * @throws JsonProcessingException
	 */
	private static String getCityFolderStructure(
			List<FolderMappingBean> aoListJsTreeBeanMain,
			String asRecycleBinParent, StringBuffer aoJsonString,
			Iterator aoItr, String[] aoTemp) throws JsonProcessingException {
		String lsFirstJson;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getCityFolderStructure()");
		if (null != aoListJsTreeBeanMain && aoListJsTreeBeanMain.size() > 0) {
			for (Iterator iterator2 = aoListJsTreeBeanMain.iterator(); iterator2
					.hasNext();) {
				FolderMappingBean loBean = (FolderMappingBean) iterator2.next();
				if (loBean.getText().equalsIgnoreCase(
						HHSR5Constants.DOCUMENT_VAULT)) {
					String[] lsTemp1 = aoTemp[1].split(HHSConstants.HYPHEN);
					loBean.setText(lsTemp1[1]);
					asRecycleBinParent = loBean.getId();
				}
			}
			String[] lsTemp2 = aoTemp[1].split(HHSConstants.HYPHEN);
			FolderMappingBean loBean = new FolderMappingBean();
			loBean.setId(HHSR5Constants.RECYCLE_BIN_ID
					+ ApplicationConstants.TILD + lsTemp2[0]
					+ HHSConstants.HYPHEN + lsTemp2[1]);
			loBean.setText(HHSR5Constants.RECYCLE_BIN);
			loBean.setType(HHSR5Constants.RECYCLE_BIN_TYPE);
			loBean.setParent(asRecycleBinParent);
			aoListJsTreeBeanMain.add(loBean);
			lsFirstJson = JSONUtility.convertToString(aoListJsTreeBeanMain);
			lsFirstJson = lsFirstJson.substring(1,
					lsFirstJson.lastIndexOf(HHSConstants.SQUARE_BRAC_END));
			aoJsonString.append(lsFirstJson);
			if (aoItr.hasNext()) {
				aoJsonString.append(HHSConstants.COMMA);
			}
		} else {
			String[] lsTemp1 = aoTemp[1].split(HHSConstants.HYPHEN);
			aoJsonString.append("{\"type\":\"folder\",\"id\":\"/DocumentVault");
			aoJsonString.append(aoTemp[0] + "\",\"text\":\"");
			aoJsonString.append(lsTemp1[1]);
			aoJsonString
					.append("\",\"parent\":\"#\"},{\"type\":\"recycleBin\",\"id\":\"/RecycleBin");
			aoJsonString.append(aoTemp[0]);
			aoJsonString
					.append("\",\"text\":\"RecycleBin\",\"parent\":\"/DocumentVault");
			aoJsonString.append(aoTemp[0] + "\"}");
			if (aoItr.hasNext()) {
				aoJsonString.append(HHSConstants.COMMA);
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getCityFolderStructure()");
		return asRecycleBinParent;
	}

	/**
	 * This method is added as a part of release 5 , this method will be used to
	 * fetch the folder structure of the logged in user. Adding two parameters
	 * for defect 8150
	 * 
	 * @param aoList
	 * @param asUserOrgType
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getManageOrgFolderStructure(Set<String> aoList,
			String asUserOrgType, P8UserSession aoUserSession, String asUserOrg)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getManageOrgFolderStructure()");
		HashMap<String, List<String>> loSharedIdMap;
		List<FolderMappingBean> loListJsTreeBean;
		JsonTreeBean loJsonBean = null;
		List<FolderMappingBean> loListJsTreeBeanMain;
		String lsRecycleBinParent = HHSR5Constants.EMPTY_STRING;
		String lsFirstJson = null;
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		String[] lsTemp1 = null;
		StringBuffer loJsonString = new StringBuffer();
		try {
			if (aoList != null) {
				loJsonString.append(HHSConstants.SQUARE_BRAC_BEGIN);
				Iterator loItr = aoList.iterator();
				while (loItr.hasNext()) {
					String lsCustmOrg = (String) loItr.next();
					String[] loTemp = lsCustmOrg
							.split(ApplicationConstants.TILD);
					lsTemp1 = loTemp[1].split("-");
					loHashMap.put(HHSR5Constants.ORG_ID, loTemp[0]);
					loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
					HHSTransactionManager.executeTransaction(loChannelObj,
							HHSR5Constants.GET_TREE_DATA,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					loListJsTreeBeanMain = (List<FolderMappingBean>) loChannelObj
							.getData(HHSR5Constants.RETURN_TREE_LIST);
					if (null != asUserOrgType
							&& !asUserOrgType.isEmpty()
							&& !asUserOrgType
									.equalsIgnoreCase(HHSConstants.USER_AGENCY)
							&& !asUserOrgType
									.equalsIgnoreCase(HHSConstants.PROVIDER_ORG)) {
						if (null != loListJsTreeBeanMain
								&& loListJsTreeBeanMain.size() > 0) {
							for (Iterator iterator2 = loListJsTreeBeanMain
									.iterator(); iterator2.hasNext();) {
								FolderMappingBean loBean = (FolderMappingBean) iterator2
										.next();
								lsTemp1 = loTemp[1].split("-");
								loJsonBean = new JsonTreeBean();
								loJsonBean.setOrganizationId(loTemp[0]);
								loJsonBean.setOrganizationType(lsTemp1[0]);
								loBean.setData(loJsonBean);
								if (loBean.getText().equalsIgnoreCase(
										HHSR5Constants.DOCUMENT_VAULT)) {
									lsRecycleBinParent = loBean.getId();
								}
							}
							String[] lsTemp2 = loTemp[1]
									.split(HHSConstants.HYPHEN);
							FolderMappingBean loBean = new FolderMappingBean();
							loJsonBean = new JsonTreeBean();
							loJsonBean.setOrganizationId(loTemp[0]);
							loJsonBean.setOrganizationType(lsTemp2[0]);
							loBean.setData(loJsonBean);
							loBean.setId(HHSR5Constants.RECYCLE_BIN_ID
									+ ApplicationConstants.TILD + lsTemp2[0]
									+ HHSConstants.HYPHEN + loTemp[0]);
							loBean.setText(HHSR5Constants.RECYCLE_BIN);
							loBean.setType(HHSR5Constants.RECYCLE_BIN_TYPE);
							loBean.setParent(HHSR5Constants.DELIMITER_SINGLE_HASH);
							loListJsTreeBeanMain.add(loBean);
							lsFirstJson = JSONUtility
									.convertToString(loListJsTreeBeanMain);
							lsFirstJson = lsFirstJson.substring(1, lsFirstJson
									.lastIndexOf(HHSConstants.SQUARE_BRAC_END));
							loJsonString.append(lsFirstJson);
							if (loItr.hasNext()) {
								loJsonString.append(HHSConstants.COMMA);
							}
						} else {
							lsTemp1 = loTemp[1].split(HHSConstants.HYPHEN);
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"DocumentVault");
							loJsonString.append("\",\"text\":\"");
							loJsonString.append(HHSR5Constants.DOCUMENT_VAULT);
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": { \"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(lsTemp1[0]);
							loJsonString
									.append("\"}},{\"type\":\"recycleBin\",\"id\":\"RecycleBin");
							loJsonString.append(HHSR5Constants.TILD
									+ lsTemp1[0] + HHSR5Constants.HYPHEN
									+ loTemp[0]);
							loJsonString
									.append("\",\"text\":\"Recycle Bin\",\"parent\":\"#\",\"data\": { \"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(lsTemp1[0]);
							loJsonString.append("\"}}");
							if (loItr.hasNext()) {
								loJsonString.append(HHSConstants.COMMA);
							}
						}
					} else {

						if (null != loListJsTreeBeanMain
								&& loListJsTreeBeanMain.size() > 0) {
							for (Iterator iterator2 = loListJsTreeBeanMain
									.iterator(); iterator2.hasNext();) {
								FolderMappingBean loBean = (FolderMappingBean) iterator2
										.next();
								if (loBean.getText().equalsIgnoreCase(
										HHSR5Constants.DOCUMENT_VAULT)) {
									lsRecycleBinParent = loBean.getId();
								}
							}
							lsTemp1 = loTemp[1].split(HHSConstants.HYPHEN);
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"");
							loJsonString.append(lsRecycleBinParent);
							loJsonString.append("\",\"text\":\"");
							loJsonString.append("Shared Documents");
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": {\"organizationId\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(lsTemp1[0]);
							loJsonString.append("\"}}");
							// Addign below trasaction for defect 8150
							Channel loChannel = new Channel();
							String lsAgencyType = null;
							if (null != asUserOrgType
									&& asUserOrgType
											.equalsIgnoreCase("provider_org")) {
								lsAgencyType = "HHSProviderID";
							} else if (null != asUserOrgType
									&& asUserOrgType
											.equalsIgnoreCase("agency_org")) {
								lsAgencyType = "HHS_AGENCY_ID";
							}

							loChannel
									.setData("aoFilenetSession", aoUserSession);
							loChannel.setData("asAgencyType", lsAgencyType);
							loChannel.setData("asProviderId", loTemp[0]);
							loChannel.setData("asShareWith", asUserOrg);
							TransactionManager.executeTransaction(loChannel,
									"getSharedDocIdList",
									HHSR5Constants.TRANSACTION_ELEMENT_R5);
							loSharedIdMap = (HashMap<String, List<String>>) loChannel
									.getData("providerList");
							List<String> loTempList = loSharedIdMap
									.get(loTemp[0]);
							if (null != loTempList && !loTempList.isEmpty()) {
								Iterator iterator = loTempList.iterator();
								while (iterator.hasNext()) {
									String lsFolderId = (String) iterator
											.next();
									loHashMap.put(
											HHSR5Constants.FOLDER_ID_FILENET,
											lsFolderId);
									loHashMap.put(HHSR5Constants.ORG_ID,
											loTemp[0]);
									loChannelObj
											.setData(HHSConstants.AO_HASH_MAP,
													loHashMap);
									HHSTransactionManager
											.executeTransaction(
													loChannelObj,
													HHSR5Constants.GET_TREE_ORG,
													HHSR5Constants.TRANSACTION_ELEMENT_R5);
									loListJsTreeBean = (List<FolderMappingBean>) loChannelObj
											.getData(HHSR5Constants.RETURN_TREE_LIST);
									for (int iCheck = 0; iCheck < loListJsTreeBean
											.size(); iCheck++) {
										FolderMappingBean loBean = loListJsTreeBean
												.get(iCheck);
										loJsonBean = new JsonTreeBean();
										loJsonBean.setOrganizationId(loTemp[0]);
										loJsonBean
												.setOrganizationType(lsTemp1[0]);
										loBean.setData(loJsonBean);
										if (loBean.getId().equalsIgnoreCase(
												lsFolderId)) {
											loBean.setParent(lsRecycleBinParent);
										}
									}
									lsFirstJson = JSONUtility
											.convertToString(loListJsTreeBean);
									lsFirstJson = lsFirstJson
											.substring(
													1,
													lsFirstJson
															.lastIndexOf(HHSConstants.SQUARE_BRAC_END));
									if (null != lsFirstJson
											&& !lsFirstJson.isEmpty()) {
										loJsonString.append(HHSConstants.COMMA);
									}
									loJsonString.append(lsFirstJson);

								}
							}
						} else {
							loJsonString
									.append("{\"type\":\"folder\",\"id\":\"DocumentVault");
							loJsonString.append("\",\"text\":\"");
							loJsonString.append("Shared Documents");
							loJsonString
									.append("\",\"parent\":\"#\",\"data\": {\"organizationId\":\"");
							// Changing below parameters in Emergency Release
							// 4.0.2
							loJsonString.append(lsTemp1[0]);
							loJsonString.append("\",\"organizationType\":\"");
							loJsonString.append(loTemp[0]);
							loJsonString.append("\"}}");
							if (loItr.hasNext()) {
								loJsonString.append(HHSConstants.COMMA);
							}
						}
					}

				}
				loJsonString.append(HHSConstants.SQUARE_BRAC_END);
			}
			LOG_OBJECT
					.Info("Exiting FileNetOperationsUtils.getManageOrgFolderStructure()");
			return loJsonString.toString();
		} catch (ApplicationException aoAppEx) {
			LOG_OBJECT
					.Error("Error Occured while fetching Manage Org Folder structure organization",
							aoAppEx);
			throw aoAppEx;
		} catch (Exception aoExp) {
			LOG_OBJECT
					.Error("Error Occured while fetching Manage Org Folder structure organization",
							aoExp);
			throw new ApplicationException(
					"Error Occured while fetching Manage Org Folder structure organization",
					aoExp);
		}
	}

	/**
	 * This method is used to check Filing Status.
	 * 
	 * @param aoOrgId
	 *            - String containing organization id
	 * @return lbFlag Boolean
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 */
	private static Boolean checkFilingStatus(String aoOrgId)
			throws ApplicationException {
		Boolean lbFlag = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.checkFilingStatus()");
		try {
			loChannel.setData(HHSR5Constants.Org_Id, aoOrgId);
			HHSTransactionManager.executeTransaction(loChannel,
					"checkFilingStatusInDb",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbFlag = (Boolean) loChannel
					.getData(ApplicationConstants.LINKED_TO_APP_FLAG);
		} catch (ApplicationException aoAppEx) {
			throw aoAppEx;
		} catch (Exception aoExp) {
			throw new ApplicationException(
					"Error Occured while fetching folder structure for nother organization",
					aoExp);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.checkFilingStatus()");
		return lbFlag;
	}

	/**
	 * The method will set the required map, order map for addFromVault.
	 * 
	 * @param loReqProps
	 *            as required map
	 * @param lsUserOrg
	 *            a string value of user organization
	 * @param loFilterProps
	 *            as filter map for where clause
	 * @param loChannel
	 *            channel object
	 * @param aoBussApp
	 * @throws ApplicationException
	 */
	public static void setFilterMapForAddDocsFromVault(
			HashMap<String, String> loReqProps, String lsUserOrg,
			HashMap loFilterProps, Channel loChannel, String aoBussApp)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setFilterMapForAddDocsFromVault()");
		String lsObjectsPerPageKey = null;
		HashMap<String, String> loApplicationSettingMap = new HashMap<String, String>();
		String lsObjectsPerPage = null;
		// Added for defect# 7901
		lsObjectsPerPageKey = HHSConstants.ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME
				+ "_" + ApplicationConstants.APPLICATION_DOCUMENT_VIEW_PER_PAGE;
		loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(
						ApplicationConstants.APPLICATION_SETTING);
		lsObjectsPerPage = (String) loApplicationSettingMap
				.get(lsObjectsPerPageKey);
		// End
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(HHSR5Constants.DELETED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps
				.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE
						+ " as DATE_CREATED_ALIAS",
						HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(HHSR5Constants.FOLDERS_FILED_IN,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(HHSConstants.TEMPLATE_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, "DOC");
		HashMap loHMOrderByMap = new HashMap();
		loHMOrderByMap.put(1, P8Constants.PROPERTY_CE_DOC_TYPE
				+ HHSConstants.SPACE + HHSConstants.ASCENDING
				+ HHSR5Constants.DOC_WITH_COMMA);
		loHMOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE
				+ HHSConstants.SPACE + HHSConstants.ASCENDING
				+ HHSR5Constants.DOC_WITH_COMMA);
		loChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP, loHMOrderByMap);
		loChannel.setData(HHSConstants.OBJECTS_PER_PAGE, lsObjectsPerPage);
		loFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, lsUserOrg);
		loFilterProps.put(P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY,
				Boolean.FALSE);
		loFilterProps.put(HHSR5Constants.SELECT_VAULT, "true");
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setFilterMapForAddDocsFromVault()");
	}

	/**
	 * This method is added in release 5, this method is used to fetch folder
	 * information
	 * 
	 * @param aoDocument
	 *            document bean
	 * @param asDocumentId
	 *            document id
	 * @param aoDocProps
	 *            document property map
	 * @throws ApplicationException
	 */
	private static void getFolderInfoMap(Document aoDocument,
			String asDocumentId, HashMap aoDocProps)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getFolderInfoMap()");
		aoDocument.setDocumentId(asDocumentId);
		aoDocument.setFolderCount((Integer) (aoDocProps
				.get(HHSR5Constants.FOLDER_COUNT)));
		aoDocument.setDocName((String) aoDocProps
				.get(HHSR5Constants.FOLDER_NAME));

		aoDocument.setCreatedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
		aoDocument.setCreatedDate(DateUtil.getDateByFormat(
				HHSConstants.NFCTH_DATE_FORMAT,
				HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
				(String) aoDocProps.get(P8Constants.PROPERTY_CE_DATE_CREATED)));
		aoDocument
				.setLastModifiedDate(DateUtil.getDatewithMMMFormatWithTimeStamp(DateUtil
						.getDateByFormat(
								HHSConstants.NFCTH_DATE_FORMAT,
								HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
								(String) aoDocProps
										.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE))));

		aoDocument.setLastModifiedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));

		if (null != aoDocProps.get(HHSR5Constants.FILENET_DELETED_BY)) {

			aoDocument.setDeletedBy((String) aoDocProps
					.get(HHSR5Constants.FILENET_DELETED_BY));
			aoDocument.setDeletedDate(DateUtil.getDateByFormat(
					HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
					HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
					(String) aoDocProps.get(HHSR5Constants.DELETED_DATE)));

		}
		if (null != aoDocProps.get(HHSR5Constants.FOLDER_PATH)) {
			String lsPath = (String) aoDocProps.get(HHSR5Constants.FOLDER_PATH);
			aoDocument.setFolderLocation(getFolderLocation(lsPath));
		}
		if (null != aoDocProps.get(HHSR5Constants.FILENET_MOVE_FROM)) {
			String lsPath = (String) aoDocProps
					.get(HHSR5Constants.FILENET_MOVE_FROM);
			aoDocument.setMoveFromPath(getFolderLocation(lsPath));
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getFolderInfoMap()");
	}

	/**
	 * This method is added in release 5, this will create the file information
	 * bean and will set the required information
	 * 
	 * @param aoDocument
	 *            document bean object
	 * @param asDocumentId
	 *            document id
	 * @param asUserOrg
	 *            user organization id
	 * @param aoDocProps
	 *            document property map
	 * @throws ApplicationException
	 */
	private static void getDocumentInfoBean(Document aoDocument,
			String asDocumentId, String asUserOrg, HashMap aoDocProps)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getDocumentInfoBean()");
		String lsProviderName = (String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY);
		aoDocument.setDocumentId(asDocumentId);
		aoDocument.setDocCategory((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
		aoDocument.setDocType((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOC_TYPE));
		aoDocument.setDocName((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
		// Added for Release 5- creator info
		String lsPath = (String) aoDocProps.get(HHSR5Constants.PARENT_PATH);
		if (null != lsPath && !lsPath.isEmpty()) {
			aoDocument.setFilePath(getDocLocation(lsPath));
		} else {
			aoDocument.setFilePath(HHSR5Constants.BACK_SLASHES_DV);
		}

		// end
		aoDocument.setDate(DateUtil.getDateByFormat(
				HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
				HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
				aoDocProps.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)
						.toString()));

		aoDocument.setFileType((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_FILE_TYPE));
		aoDocument
				.setDocumentProperties(getDocumentProperties(
						aoDocument.getDocCategory(), aoDocument.getDocType(),
						asUserOrg));
		aoDocument.setSampleCategory((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
		aoDocument.setSampleType((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
		aoDocument.setLastModifiedBy(lsProviderName);
		aoDocument.setCreatedBy((String) aoDocProps
				.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
		aoDocument.setCreatedDate(DateUtil
				.getDateByFormat(HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
						HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
						aoDocProps.get(P8Constants.PROPERTY_CE_DATE_CREATED)
								.toString()));
		if (!(aoDocument.getDocType().equalsIgnoreCase(
				ApplicationConstants.DOCUMENT_TYPE_HELP)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS)
				|| aoDocument
						.getDocType()
						.equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS)
				|| aoDocument.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT) || aoDocument
				.getDocType().equalsIgnoreCase(
						ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A))) {
			aoDocument.setDocumentProperties(getDocumentProperties(
					aoDocument.getDocCategory(), aoDocument.getDocType(),
					asUserOrg));
		}
		if (ApplicationConstants.DOC_SAMPLE.equalsIgnoreCase(aoDocument
				.getDocCategory())) {
			aoDocument.setSampleCategory((String) aoDocProps
					.get(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY));
			aoDocument.setSampleType((String) aoDocProps
					.get(P8Constants.PROPERTY_CE_SAMPLE_TYPE));
			aoDocument.setDocumentProperties(getDocumentProperties(
					aoDocument.getSampleCategory(), aoDocument.getSampleType(),
					asUserOrg));
		}
		if (null != aoDocProps.get(HHSR5Constants.FILENET_DELETED_BY)) {

			aoDocument.setDeletedBy((String) aoDocProps
					.get(HHSR5Constants.FILENET_DELETED_BY));

			aoDocument.setDeletedDate(DateUtil.getDateByFormat(
					HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,
					HHSR5Constants.NFCTH_TIMESTAMP_FORMAT,
					aoDocProps.get(HHSR5Constants.DELETED_DATE).toString()));

		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getDocumentInfoBean()");
	}

	/**
	 * Updated in R7 for defect 8698
	 * Method to load the Task Management and Task Inbox Based on the selected
	 * Filters
	 * 
	 * @param aoFilterDetails
	 *            HashMap OF Filter Details
	 * @param aoUserSession
	 *            Filenet Session
	 * @param abManagerRole
	 *            IS manager Task
	 * @param asCurrentTab
	 *            Weather management or inbox tab
	 * @param asUserID
	 *            User ID
	 * @return ArrayList<TaskQueue>
	 * @throws ApplicationException
	 */
	public static ArrayList<TaskQueue> generateInboxAndManagementFilterDetails(
			HashMap aoFilterDetails, P8UserSession aoUserSession,
			boolean abManagerRole, String asCurrentTab, String asUserID)
			throws ApplicationException {
		HashMap loRequiredProps = new HashMap();
		String lsAssignedTo = "";
		//R7 : Defect 8698 changes start
		String lsTaskType = null;
		LOG_OBJECT
				.Info("Entered WorkfloDetailController.generateInboxAndManagementFilterDetails() with parameters::"
						+ aoFilterDetails);

		/**** Begin QC 5446 ****/
		if (aoFilterDetails.containsKey(HHSConstants.PROPERTY_PE_TASK_TYPE)) {
			lsTaskType = (String) aoFilterDetails
					.get(HHSConstants.PROPERTY_PE_TASK_TYPE);
			// Changed for R5 added Approve PSR task
			if (HHSConstants.AWARD_APPROVAL_TASK.equals(lsTaskType)
					|| HHSR5Constants.TASK_APPROVE_PSR.equals(lsTaskType)
					|| HHSR5Constants.APPROVE_AWARD_AMOUNT.equals(lsTaskType))
				loRequiredProps.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE,
						"");
			if (!(HHSConstants.AWARD_APPROVAL_TASK.equals(lsTaskType) || HHSR5Constants.TASK_APPROVE_PSR
					.equals(lsTaskType)))
				loRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, "");
			
			//R7: Added for Defect #8698 added provider id to fetch business app status
			
			if(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION.equalsIgnoreCase(lsTaskType))
				loRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_ID, "");
			// R7 : Defect 8698 changes end
		}
		/**** End QC 5446 ****/

		loRequiredProps.put(P8Constants.PROPERTY_PE_LAUNCH_DATE, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_LAST_ASSIGNED, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_TASK_NAME, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		if (asCurrentTab == null
				|| asCurrentTab
						.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_INBOX)) {
			aoFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, asUserID);
		}
		HashMap loResultMap = null;
		String lsWob = null;
		ArrayList<TaskQueue> loAlTaskItemLIst = new ArrayList<TaskQueue>();
		if (null != aoFilterDetails && !aoFilterDetails.isEmpty()) {
			loResultMap = getFilteredRows(loChannel, loRequiredProps,
					aoFilterDetails);
			aoFilterDetails = null;
		} else {
			if (null != asCurrentTab
					&& asCurrentTab
							.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT)) {
				loChannel.setData("loRequiredProps", loRequiredProps);
				TransactionManager.executeTransaction(loChannel,
						"taskrows_filenet");
				loResultMap = (HashMap) loChannel.getData("result");
			} else {
				HashMap loUserNameHashMap = new HashMap();
				loUserNameHashMap.put(P8Constants.PROPERTY_PE_ASSIGNED_TO,
						asUserID);
				loResultMap = getFilteredRows(loChannel, loRequiredProps,
						loUserNameHashMap);
			}
		}
		setTaskQueueBean(loResultMap, loAlTaskItemLIst, lsWob, lsAssignedTo,
				abManagerRole);
		
		//R7 Change-8698 (Added 'BA Status' when task type is "Service Application")
		if(lsTaskType!=null && lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
			
        {
              getBusinessApplicationStatus(loAlTaskItemLIst);
        }
		LOG_OBJECT
				.Info("Exit WorkfloDetailController.generateInboxAndManagementFilterDetails()");
		return sortedList(loAlTaskItemLIst, asCurrentTab);
	}

	/**
	 * Method to fetch the filtered rows from filenet by using
	 * "filteredrows_filenet" transaction
	 * 
	 * @param aoChannel
	 *            Channel Object
	 * @param aoReqPropsMap
	 *            HashMap Of Required Props
	 * @param aoFilterMap
	 *            HashMap OF Filter Details
	 * @return HashMap
	 * @throws ApplicationException
	 */
	private static HashMap getFilteredRows(Channel aoChannel,
			HashMap aoReqPropsMap, HashMap aoFilterMap)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered WorkfloDetailController.getFilteredRows() with parameters::"
						+ aoReqPropsMap + "," + aoFilterMap);
		aoChannel.setData("loRequiredProps", aoReqPropsMap);
		aoChannel.setData("moFilterDetails", aoFilterMap);
		TransactionManager
				.executeTransaction(aoChannel, "filteredrows_filenet");
		LOG_OBJECT.Info("Exit WorkfloDetailController.getFilteredRows()");
		return (HashMap) aoChannel.getData("filteredresults");
	}

	/**
	 * This Method is used to set values in the TaskQueue Bean.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoResultMap
	 *            HashMap of Results
	 * @param aoAlTaskItemLIst
	 *            List of TaskQueue bean
	 * @param asWob
	 *            WorkFlow ID
	 * @param asAssignedTo
	 *            Task Assigned To
	 * @param abManagerRole
	 *            Weather manager Role
	 * @throws ApplicationException
	 */
	private static void setTaskQueueBean(HashMap aoResultMap,
			ArrayList<TaskQueue> aoAlTaskItemLIst, String asWob,
			String asAssignedTo, boolean abManagerRole)
			throws ApplicationException {
		Iterator loBaseItr = aoResultMap.keySet().iterator();
		LOG_OBJECT.Info("Entered WorkfloDetailController.setTaskQueueBean()");
		while (loBaseItr.hasNext()) {
			asWob = (String) loBaseItr.next();
			HashMap loWobHM = null;
			loWobHM = (HashMap) aoResultMap.get(asWob);
			Iterator loSubItr = loWobHM.keySet().iterator();
			TaskQueue loTQItem = new TaskQueue();
			loTQItem.setMsWobNumber(asWob);
			while (loSubItr.hasNext()) {
				String lsFieldName = (String) loSubItr.next();
				if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_LAUNCH_DATE)) {
					loTQItem.setMoDateCreated(DateUtil
							.getDateMMddYYYYHHMMFormat((Date) loWobHM
									.get(P8Constants.PROPERTY_PE_LAUNCH_DATE)));
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_STATUS)) {
					loTQItem.setMsStatus(loWobHM.get(
							P8Constants.PROPERTY_PE_TASK_STATUS).toString());
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_LAST_ASSIGNED)) {

					loTQItem.setMoLastAssigned(DateUtil.getDateMMddYYYYHHMMFormat((Date) loWobHM
							.get(P8Constants.PROPERTY_PE_LAST_ASSIGNED)));
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_NAME)) {
					loTQItem.setMsTaskName(loWobHM.get(
							P8Constants.PROPERTY_PE_TASK_NAME).toString());
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_PROVIDER_NAME)) {
					loTQItem.setMsProviderName(loWobHM.get(
							P8Constants.PROPERTY_PE_PROVIDER_NAME).toString());
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_PROVIDER_ID)) {
					loTQItem.setMsProviderId(loWobHM.get(
							P8Constants.PROPERTY_PE_PROVIDER_ID).toString());
				} 

				/**** Begin QC 5446 ****/
				else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE)) {
					loTQItem.setMsProcurementTitle(loWobHM.get(
							P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE)
							.toString());
				}
				/**** End QC 5446 ****/
				else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_ASSIGNED_TO)) {
					if (loWobHM.get(P8Constants.PROPERTY_PE_ASSIGNED_TO)
							.equals(P8Constants.PE_TASK_UNASSIGNED_MANAGER)
							|| loWobHM.get(P8Constants.PROPERTY_PE_ASSIGNED_TO)
									.equals(P8Constants.PE_TASK_UNASSIGNED)) {
						loTQItem.setMsAssignedTo(loWobHM.get(
								P8Constants.PROPERTY_PE_ASSIGNED_TO).toString());
					} else {
						asAssignedTo = (String) loWobHM
								.get(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME);
						loTQItem.setMsAssignedTo(asAssignedTo);
						if (asAssignedTo == null) {
							loTQItem.setMsAssignedTo("");
						}
					}
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_IS_TASK_LOCKED)) {
					if (loWobHM.get(P8Constants.PROPERTY_PE_IS_TASK_LOCKED)
							.toString().equalsIgnoreCase("1")) {
						loTQItem.setMbIsTaskLocked(true);
					} else {
						loTQItem.setMbIsTaskLocked(false);
					}
				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP)) {

					if (loWobHM
							.get(P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP)
							.toString().equalsIgnoreCase("1")) {
						loTQItem.setMbIsManagerReviewStep(true);
					} else {
						loTQItem.setMbIsManagerReviewStep(false);
					}

				} else if (lsFieldName
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_APPLICTION_ID)) {
					loTQItem.setMsApplicationId(loWobHM.get(
							P8Constants.PROPERTY_PE_APPLICTION_ID).toString());
				}
			}
			loTQItem.setMbManagerRole(abManagerRole);
			aoAlTaskItemLIst.add(loTQItem);
		}
		LOG_OBJECT.Info("Exit WorkfloDetailController.setTaskQueueBean()");
	}

	/**
	 * This Method is used to sort the list of tasks.
	 * 
	 * @param aoList
	 *            List to be sorted
	 * @param asCurrentTab
	 *            Current Tab(Management or Inbox)
	 * @return aoList
	 * @throws ApplicationException
	 */
	private static ArrayList<TaskQueue> sortedList(ArrayList<TaskQueue> aoList,
			String asCurrentTab) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.sortedList()");
		if (aoList != null) {
			if (null != asCurrentTab
					&& asCurrentTab
							.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT)) {
				Collections.sort(aoList, new Comparator<TaskQueue>() {
					int liReturn;

					@Override
					public int compare(TaskQueue aoC1, TaskQueue aoC2) {

						liReturn = (int) (DateUtil.getSortedDate(aoC2
								.getMoDateCreated())).compareTo(DateUtil
								.getSortedDate(aoC1.getMoDateCreated()));
						if (liReturn == 0) {
							liReturn = aoC1.getMsTaskName().compareTo(
									aoC2.getMsTaskName());
						}
						return liReturn;
					}
				});
			} else {
				Collections.sort(aoList, new Comparator<TaskQueue>() {
					int liReturn;

					@Override
					public int compare(TaskQueue aoC1, TaskQueue aoC2) {
						liReturn = (int) (DateUtil.getSortedDate(aoC2
								.getMoLastAssigned())).compareTo(DateUtil
								.getSortedDate(aoC1.getMoLastAssigned()));
						if (liReturn == 0) {
							liReturn = aoC1.getMsTaskName().compareTo(
									aoC2.getMsTaskName());
						}
						return liReturn;
					}
				});
			}
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.sortedList()");
		return aoList;
	}

	/**
	 * Method to set the different attributes in application session .
	 * 
	 * @param aoRequest
	 *            ActionRequest
	 * @param aoTaskQueueItems
	 *            List Of Task
	 * @param asApplicationId
	 *            Application ID
	 * @throws ApplicationException
	 */
	public static void setRequiredInformationForInbox(ActionRequest aoRequest,
			ArrayList aoTaskQueueItems, String asApplicationId)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.setRequiredInformationForInbox()");
		WorkItemInbox loWorkItemInbox = new WorkItemInbox();
		loWorkItemInbox.setTaskTypeList(FileNetOperationsUtils
				.getMasterData("fetchTaskTypeData"));
		loWorkItemInbox.setStatusList(FileNetOperationsUtils
				.getMasterData("fetchInboxFilterStatus"));

		/**** Begin QC 5446 ****/
		Channel channel = new Channel();
		HHSTransactionManager.executeTransaction(channel,
				HHSConstants.AS_GET_AGENCY_REVIEW_PROCESS_DATA);
		AgencySettingsBean loAgencySettingsBean = (AgencySettingsBean) channel
				.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
		ApplicationSession.setAttribute(loAgencySettingsBean, aoRequest,
				HHSConstants.AS_AGENCY_SETTING_BEAN);
		/**** End QC 5446 ****/

		ApplicationSession.setAttribute(loWorkItemInbox, aoRequest,
				"workItemInbox");
		ApplicationSession.setAttribute(asApplicationId, aoRequest, "appId");
		ApplicationSession
				.setAttribute(String.valueOf(aoTaskQueueItems.size()),
						aoRequest, "TotalTask");
		ApplicationSession.setAttribute(aoTaskQueueItems, aoRequest,
				"taskItemList");
		ApplicationSession
				.setAttribute(FileNetOperationsUtils.getProviderList(),
						aoRequest, "provList");
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.setRequiredInformationForInbox()");
	}

	/**
	 * The method will give list of agency/provider for a specific organization
	 * which has shared document with them.
	 * 
	 * @param lsPartialUoDenom
	 * @param aoUserSession
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getSharedWith(String lsPartialUoDenom,
			P8UserSession aoUserSession, HttpServletRequest aoRequest)
			throws Exception {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getSharedWith()");
		TreeSet loProviderShareSet = new TreeSet();
		ArrayList<String> loSharedList = new ArrayList<String>();
		HashMap<String, String> loDataMap = new HashMap<String, String>();
		try {
			loDataMap = (HashMap<String, String>) aoRequest.getSession()
					.getAttribute(HHSR5Constants.SHARED_WITH_DETAILS);
			for (Map.Entry<String, String> entry : loDataMap.entrySet()) {
				if (entry.getValue().toLowerCase()
						.contains(lsPartialUoDenom.toLowerCase())) {
					loSharedList.add(entry.getValue());
				}
			}
		} catch (Exception aoAppex) {
			LOG_OBJECT.Error("Error in getSharedWith Method.", aoAppex);
			throw aoAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getSharedWith()");
		return loSharedList;
	}

	/**
	 * The method will the list of Amendment e-pin for type ahead.
	 * 
	 * @param asUserOrgType
	 *            a string value of user organization type
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of award e-pin entered by user
	 * @return list of award e-pin
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAmendmentEpin(String asUserOrgType,
			String asUserOrg, String lsPartialUoDenom)
			throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getAmendmentEpin()");
		Channel loChannel = new Channel();
		List<String> loAwardEPinList = new ArrayList<String>();
		Map<String, String> loDataMap = new HashMap<String, String>();
		try {
			loDataMap.put(HHSConstants.USER_ORG, asUserOrg);
			loDataMap.put(HHSR5Constants.USER_ORG_TYPE, asUserOrgType);
			loDataMap.put(HHSR5Constants.PARTIAL_KEY_WORD, HHSConstants.PERCENT
					+ lsPartialUoDenom + HHSConstants.PERCENT);
			loChannel.setData(HHSR5Constants.CHANNEL_DATA_MAP, loDataMap);
			TransactionManager.executeTransaction(loChannel,
					HHSR5Constants.GET_AMENDMENT_EPIN,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loAwardEPinList = (List<String>) loChannel
					.getData(HHSR5Constants.AWARD_EPIN_LIST);
		} catch (ApplicationException aoAppex) {
			LOG_OBJECT.Error("Error in getAmendmentEpin Method.", aoAppex);
			throw aoAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getAmendmentEpin()");
		return loAwardEPinList;
	}

	/**
	 * This method will create required map for Document
	 * 
	 * @param asUserOrgType
	 * @param asDocCategory
	 * @return
	 */
	public static HashMap requiredDocsPropsForDocs(String asUserOrgType,
			String asDocCategory) {
		// Adding class alias(DOC for DOCUMENT class in Filenet and FO for
		// HHS_CUSTOM_FOLDER class)
		// as values in loHmReqProps for Release 5, initially it was empty for
		// all keys.
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.requiredDocsPropsForDocs()");
		HashMap<String, String> loHmReqProps = new HashMap<String, String>();
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.ORGANIZATION_ID_KEY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.DELETED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_IS_DOCUMENT_SHARED,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_HHS_LAST_MODIFIED_DATE,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSConstants.DATE_LAST_MODIFIED
				+ " as DOC_LAST_MODIFIED", HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.DATE_CREATED_ALIAS,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.PARENT_PATH,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// Added for Release 5
		loHmReqProps.put(HHSConstants.TEMPLATE_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.SHARING_FLAG_AS_DOC_SHARING_FLAG,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loHmReqProps.put(HHSR5Constants.SHARED_ENTITY_ID,
				HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// End Release 5
		if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG)
				&& (null == asDocCategory || asDocCategory.isEmpty() || !asDocCategory
						.equalsIgnoreCase(P8Constants.PROPERTY_CE_SOLICITATION_CATEGORY))) {
			// Added for Release 5
			loHmReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY,
					HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			loHmReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE,
					HHSR5Constants.DOCUMENT_CLASS_ALIAS);
			// End Release 5
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.requiredDocsPropsForDocs()");
		return loHmReqProps;
	}

	/**
	 * This method is added as a part of release 5, this will fetch the original
	 * document Type for any category which is masked
	 * 
	 * @param asDocumentType
	 * @param asOrgType
	 * @return
	 * @throws ApplicationException
	 */
	public static String getOriginalDocumentType(String asDocumentType)
			throws ApplicationException {
		String lsOriginalDocType = null;
		String lsXpath = null;
		Element loDocElement = null;
		String lsIsMasked = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getOriginalDocumentType()");
		try {
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.FILENETDOCTYPE);
			lsXpath = "//" + P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
					+ asDocumentType + "\"]";
			loDocElement = XMLUtil.getElement(lsXpath, loXMLDoc);
			lsIsMasked = loDocElement.getAttributeValue("ismasked");
			if (null != lsIsMasked && lsIsMasked.equalsIgnoreCase("true"))
				lsOriginalDocType = loDocElement
						.getChildText("originalDocType");
			else
				lsOriginalDocType = asDocumentType;
		} catch (ApplicationException aoAppExp) {
			LOG_OBJECT.Error("Error in getOriginalDocumentType Method.",
					aoAppExp);
			throw aoAppExp;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getOriginalDocumentType()");
		return lsOriginalDocType;
	}

	/**
	 * The method will get Sample Category And Type.
	 * 
	 * @param asMaskedSampleType
	 *            a string value
	 * @param asAttributeKey
	 *            a string value
	 * @return lsAttributeValue String
	 * @throws ApplicationException
	 */
	public static String getSampleCategoryAndType(String asMaskedSampleType,
			String asAttributeKey) throws ApplicationException {
		String lsAttributeValue = null;
		String lsXpath = null;
		Element loDocElement = null;
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSampleCategoryAndType()");
		try {
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.FILENETDOCTYPE);
			lsXpath = "//" + P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
					+ asMaskedSampleType + "\"]";
			loDocElement = XMLUtil.getElement(lsXpath, loXMLDoc);
			if (null != loDocElement)
				lsAttributeValue = loDocElement.getChildText(asAttributeKey);
			else
				throw new ApplicationException(
						"Could not able to find Sample Category/Type");
		} catch (ApplicationException aoAppExp) {
			LOG_OBJECT.Error("Error in getSampleCategoryAndType Method.",
					aoAppExp);
			throw aoAppExp;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getSampleCategoryAndType()");
		return lsAttributeValue;
	}

	/**
	 * The method will get Document Type for Sample Doc.
	 * 
	 * @param aoDoctypeDom
	 *            org.jdom.Document
	 * @param asSampleType
	 *            a string value
	 * @return lsAttributeValue String
	 * @throws ApplicationException
	 */
	public static String getDocumentTypeforSampleDoc(
			org.jdom.Document aoDoctypeDom, String asSampleType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getDocumentTypeforSampleDoc()");
		String lsAttributeValue = null;
		String lsXpath = null;
		Element loDocElement = null;
		Element loParentElement = null;
		try {
			lsXpath = "//" + ApplicationConstants.SAMPLE_DOC_TYPE
					+ "[text()=\"" + asSampleType + "\"]";
			loDocElement = XMLUtil.getElement(lsXpath, aoDoctypeDom);
			if (null != loDocElement) {
				loParentElement = loDocElement.getParentElement();
				lsAttributeValue = loParentElement
						.getAttributeValue(HHSConstants.NAME);

			} else
				throw new ApplicationException(
						"Could not able to find Sample Category/Type");
		} catch (ApplicationException aoAppExp) {
			LOG_OBJECT.Error("Error in getDocumentTypeforSampleDoc Method.",
					aoAppExp);
			throw aoAppExp;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getDocumentTypeforSampleDoc()");
		return lsAttributeValue;
	}

	// End Release 5

	/**
	 * This method will generate Error messages if document is linked to any
	 * object
	 * 
	 * @param aoRequest
	 *            a Action Request object
	 * @param aoResponse
	 *            a Action Response object
	 * @param aoSession
	 *            a PortletSession object
	 * @throws ApplicationException
	 *             If an Application Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	public static void generateErrorMessageForAttachedDocument(
			PortletSession aoSession, String asErrorMessage)
			throws ApplicationException, IOException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.generateErrorMessageForAttachedDocument()");
		aoSession.setAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				HHSConstants.CLC_CAP_ERROR, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				asErrorMessage, PortletSession.PORTLET_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationConstants.MESSAGE_FAIL_TYPE,
				PortletSession.PORTLET_SCOPE);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.generateErrorMessageForAttachedDocument()");
		throw new ApplicationException(asErrorMessage);
	}

	/**
	 * This method is added as a part of release 5, this will fetch the original
	 * document Type for any category which is masked
	 * 
	 * @param asDocumentType
	 * @param asOrgType
	 * @return
	 * @throws ApplicationException
	 */
	public static String getMaskedDocumentType(String asDocumentType)
			throws ApplicationException {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getMaskedDocumentType()");
		String lsMaskedDocType = null;
		String lsXpath = null;
		Element loDocElement = null;
		String lsIsMasked = null;
		String lsDocumentCategory = null;
		try {
			org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(
							ApplicationConstants.FILENETDOCTYPE);
			lsXpath = "//" + P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\""
					+ asDocumentType + "\"]";
			loDocElement = XMLUtil.getElement(lsXpath, loXMLDoc);
			lsIsMasked = loDocElement.getAttributeValue("ismasked");
			if (null != lsIsMasked && lsIsMasked.equalsIgnoreCase("true")) {
				lsDocumentCategory = loDocElement.getParentElement()
						.getAttributeValue("name");
			}
			lsMaskedDocType = lsDocumentCategory + " - " + asDocumentType;

		} catch (ApplicationException aoAppExp) {
			LOG_OBJECT
					.Error("Error in getMaskedDocumentType Method.", aoAppExp);
			throw aoAppExp;
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getMaskedDocumentType()");
		return lsMaskedDocType;
	}

	/**
	 * This method is added as a part of release 5, to set Key Value In Map
	 * 
	 * @param aoHashMap
	 *            HashMap
	 * @param asKey
	 *            String
	 * @param asValue
	 *            String
	 * @throws ApplicationException
	 */
	private static void setKeyValueInMap(HashMap aoHashMap, String asKey,
			String asValue) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setKeyValueInMap()");
		setKeyValueInMap(aoHashMap, asKey, asValue, true);
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setKeyValueInMap()");
	}

	/**
	 * This method is added as a part of release 5, to set Key Value In Map
	 * 
	 * @param aoHashMap
	 *            HashMap
	 * @param asKey
	 *            String
	 * @param asValue
	 *            String
	 * @param abCheckNull
	 *            boolean
	 * @throws ApplicationException
	 */
	private static void setKeyValueInMap(HashMap aoHashMap, String asKey,
			String asValue, boolean abCheckNull) {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.setKeyValueInMap()");
		if (abCheckNull) {
			if (asValue != null) {
				aoHashMap.put(asKey, asValue);
			}
		} else {
			aoHashMap.put(asKey, asValue);
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.setKeyValueInMap()");
	}

	/**
	 * Added in Release 5 The method will return list of Agency and Provider
	 * names by comparing Provider/Agency Cache list and the list of
	 * Provider/agency Ids.
	 * 
	 * @param aoListAgencyProvider
	 *            as list of Provider/agency Ids
	 * @return list of Provider/agency names
	 * @throws ApplicationException
	 */
	public static List<String> getProviderAndAgencyNameListById(
			List<String> aoListAgencyProvider) throws ApplicationException {

		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getProviderAndAgencyNameListById()");
		List loPList = new ArrayList();
		TreeSet<String> loAgencyList = null;
		String lsProvAgencyName = null;
		try {
			if (null != aoListAgencyProvider && !aoListAgencyProvider.isEmpty()) {
				List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb
						.getInstance().getCacheObject(
								ApplicationConstants.PROV_LIST);
				loAgencyList = (TreeSet<String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(
								ApplicationConstants.AGENCY_LIST);
				for (Iterator liIt = aoListAgencyProvider.iterator(); liIt
						.hasNext();) {
					String lsName = (String) liIt.next();
					if (lsName.startsWith(ApplicationConstants.PROVIDER
							.toUpperCase())) {
						lsProvAgencyName = FileNetOperationsUtils
								.getProviderName(
										loProviderBeanList,
										lsName.substring(
												lsName.indexOf(HHSConstants.COLON) + 1,
												lsName.length()));
						lsProvAgencyName = StringEscapeUtils
								.unescapeJavaScript(lsProvAgencyName);
						loPList.add(lsProvAgencyName);
					} else if (lsName.startsWith(ApplicationConstants.AGENCY
							.toUpperCase())) {
						lsProvAgencyName = FileNetOperationsUtils
								.getAgencyNameForManage(
										loAgencyList,
										lsName.substring(
												lsName.indexOf(HHSConstants.COLON) + 1,
												lsName.length()));
						lsProvAgencyName = StringEscapeUtils
								.unescapeJavaScript(lsProvAgencyName);
						loPList.add(lsProvAgencyName);
					}
				}
			}
		} catch (ApplicationException aoAppExp) {
			LOG_OBJECT.Error(
					"Error in getProviderAndAgencyNameListById Method.",
					aoAppExp);
			throw aoAppExp;
		}
		Collections.sort(loPList);
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.getProviderAndAgencyNameListById()");
		return loPList;
	}

	/**
	 * This method is added as a part of release 5, to check If Locked By
	 * Recycle Bin
	 * 
	 * @param aoLockedByList
	 *            List
	 * @return boolean lbRecycleBin
	 * @throws ApplicationException
	 */
	public static boolean checkIfLockedByRecycleBin(List<String> aoLockedByList) {
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.getSampleCategoryAndType()");
		boolean lbRecycleBin = false;
		for (String asLockedPath : aoLockedByList) {
			if (asLockedPath.contains("Recycle Bin")) {
				lbRecycleBin = true;
			}
		}
		LOG_OBJECT
				.Info("Exiting FileNetOperationsUtils.checkIfLockedByRecycleBin()");
		return lbRecycleBin;
	}

	/**
	 * The method is added in Release 5 for meta data validation. It will be
	 * executed in Upload Step 2.
	 * 
	 * @param aoPropertyMap
	 *            map containing document properties
	 * @param asDocType
	 * @throws ApplicationException
	 */
	public static void validatorForUpload(HashMap aoPropertyMap,
			String asDocType) throws ApplicationException {
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.validatorForUpload()");
		boolean lbIsValidDate = false;
		HashMap loHmReqExceProp = new HashMap();
		Date loProvidedToDate = null;
		Date loProvidedFromDate = null;
		Date loEffetiveDate = null;
		Date loLastUpdateDate = null;
		Date loMeetingDate = null;
		Date loTodaysDate = new Date(System.currentTimeMillis());
		loProvidedToDate = DateUtil.getDate((String) aoPropertyMap
				.get(HHSR5Constants.PERIOD_COVER_TO));
		loProvidedFromDate = DateUtil.getDate((String) aoPropertyMap
				.get(HHSR5Constants.PERIOD_COVER_FROM));
		loEffetiveDate = DateUtil.getDate((String) aoPropertyMap
				.get(HHSR5Constants.EFFECTIVE_Date));
		loLastUpdateDate = DateUtil.getDate((String) aoPropertyMap
				.get(HHSR5Constants.DATE_LAST_UPDATE));
		loMeetingDate = DateUtil.getDate((String) aoPropertyMap
				.get(HHSR5Constants.MEETING_Date));
		if (((P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE
				.equalsIgnoreCase(asDocType))
				|| (P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE
						.equalsIgnoreCase(asDocType))
				|| (P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE
						.equalsIgnoreCase(asDocType)) || (P8Constants.PROPERTY_CE_CHAR500_EXTENSION
				.equalsIgnoreCase(asDocType)))
				&& (aoPropertyMap.get(HHSR5Constants.PERIOD_COVER_FROM_YR) != null)) {
			validateDate(aoPropertyMap);
		}

		if (null != loProvidedToDate && null != loProvidedFromDate) {
			if (!((ApplicationConstants.DOCUMENT_TYPE_LICENSES)
					.equalsIgnoreCase(asDocType)
					|| (ApplicationConstants.LEASE_OR_RENTAL_AGREEMENT)
							.equalsIgnoreCase(asDocType)
					|| (ApplicationConstants.SUBCONTRACTOR_AGREEMENT)
							.equalsIgnoreCase(asDocType)
					|| (ApplicationConstants.CONSULTANT_AGREEMENT)
							.equalsIgnoreCase(asDocType)
					|| (ApplicationConstants.COST_ALLOCATION_PLAN)
							.equalsIgnoreCase(asDocType) || (ApplicationConstants.CERTIFICATE_OF_INSURANCE)
					.equalsIgnoreCase(asDocType))) {
				validateProvidedDate(loProvidedToDate, loProvidedFromDate);
			}
		}

		if (null != loLastUpdateDate) {
			if (loLastUpdateDate.after(loTodaysDate)) {
				ApplicationException loAppex = new ApplicationException(
						"Date Of Last Update can not be future date.");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
		}
		if (null != loMeetingDate) {
			if (loMeetingDate.after(loTodaysDate)) {
				ApplicationException loAppex = new ApplicationException(
						"Meeting Date can not be future date.");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			}
		}
		if (null != loEffetiveDate) {
			if (!(HHSConstants.PROVIDERS_BOARD_APPROVED_BUDGET)
					.equalsIgnoreCase(asDocType)
					&& !(HHSConstants.BOARD_RESOLUTION)
							.equalsIgnoreCase(asDocType)
					&& !(HHSConstants.CONTRACT_AGREEMENT)
							.equalsIgnoreCase(asDocType)) {
				if (loEffetiveDate.after(loTodaysDate)) {
					ApplicationException loAppex = new ApplicationException(
							"Effective Date can not be future date.");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
			}
		}

		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.validatorForUpload()");
		lbIsValidDate = compareToFromDate(
				(String) aoPropertyMap.get(HHSR5Constants.PERIOD_COVER_TO),
				(String) aoPropertyMap.get(HHSR5Constants.PERIOD_COVER_FROM));
	}

	/**
	 * This method is used for date validation
	 * 
	 * @param aoPropertyMap
	 *            A hash-map containing the properties * @param aoHmReqExceProp
	 *            A hash-map containing the method parameters
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public static void validateDate(HashMap aoPropertyMap)
			throws ApplicationException {
		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put(HHSR5Constants.BULK_UPLOAD_FILE_PROPS,
				aoPropertyMap);
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateDate() with parameters::"
						+ loHmReqExceProp.toString());
		try {
			Calendar loCalender = new GregorianCalendar();
			Date loCurrentDate = new Date(System.currentTimeMillis());
			loCalender.setTime(loCurrentDate);
			String lsStartYear = aoPropertyMap.get("periodcoveredfromyear")
					.toString();
			if (loCalender.get(Calendar.YEAR) < Integer.parseInt(lsStartYear)) {
				ApplicationException loAppex = new ApplicationException(
						PropertyLoader
								.getProperty(
										P8Constants.ERROR_PROPERTY_FILE,
										HHSR5Constants.PEROID_COVER_VALIDATION_FUTURE_CONSTANT));
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			} else {
				double ldMonthGap = FileNetOperationsUtils
						.validatePeriodCoveredDateChar500(aoPropertyMap,
								HHSR5Constants.EMPTY_STRING);
				if ((ldMonthGap < 0 || ldMonthGap > Double
						.valueOf(HHSR5Constants.EQUI_ENTRY_TYPE))) {
					ApplicationException aoCustomExp = new ApplicationException(
							PropertyLoader
									.getProperty(
											P8Constants.ERROR_PROPERTY_FILE,
											HHSR5Constants.PERIOD_COVERED_VALIDATION_CONSTANT));
					throw aoCustomExp;
				}
			}
		} catch (ApplicationException aoAppEx) {
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error(
					"Exception in FileNetOperationsUtils.validateDate()::",
					aoAppEx);
			throw aoAppEx;
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Exception in validateDate Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in validateDate Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited FileNetOperationsUtils.validateDate() ");
	}

	/**
	 * This method is used for validating the provided From-Date and To-Date
	 * 
	 * @param aoProvidedToDate
	 *            A Date object representing the To-Date
	 * @param aoProvidedFromDate
	 *            A Date object representing the From-Date
	 * @throws ApplicationException
	 */
	public static void validateProvidedDate(Date aoProvidedToDate,
			Date aoProvidedFromDate) throws ApplicationException {

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("aoProvidedToDate", aoProvidedToDate);
		loHmReqExceProp.put("aoProvidedFromDate", aoProvidedFromDate);
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.validateProvidedDate() with parameters::"
						+ loHmReqExceProp.toString());

		try {

			Date loTodaysDate = new Date(System.currentTimeMillis());

			if (null != aoProvidedFromDate
					&& aoProvidedFromDate.after(loTodaysDate)
					&& aoProvidedToDate.after(loTodaysDate)) {
				ApplicationException loAppex = new ApplicationException(
						"Period Covered From Date & Period Covered To Date can not be future date.");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			} else if (null != aoProvidedToDate
					&& aoProvidedToDate.after(loTodaysDate)) {
				ApplicationException loAppex = new ApplicationException(
						"Period Covered To Date can not be future date.");
				loAppex.setContextData(loHmReqExceProp);
				throw loAppex;
			} else if (null != aoProvidedFromDate
					&& aoProvidedFromDate.after(loTodaysDate)) {
				ApplicationException loAppex = new ApplicationException(
						"Period Covered From Date can not be future date.");
				throw loAppex;
			}

		} catch (ApplicationException aoAppEx) {
			aoAppEx.setContextData(loHmReqExceProp);
			LOG_OBJECT
					.Error("Exception in FileNetOperationsUtils.validateProvidedDate()::",
							aoAppEx);
			throw aoAppEx;
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Exception in validateProvidedDate Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Exception in validateProvidedDate Method", aoEx);
			throw loAppex;
		}

		LOG_OBJECT
				.Info("Exited FileNetOperationsUtils.validateProvidedDate() ");

	}

	/**
	 * This method is for comparing dates
	 * 
	 * @param asToDate
	 *            document valid to date
	 * @param asFromDate
	 *            document valid from date
	 * @return boolean variable true if compare is success
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean compareToFromDate(String asToDate, String asFromDate)
			throws ApplicationException {
		Date loToDate = null;
		Date loFromDate = null;

		HashMap loHmReqExceProp = new HashMap();
		loHmReqExceProp.put("asToDate", asToDate);
		loHmReqExceProp.put("asFromDate", asFromDate);
		LOG_OBJECT
				.Info("Entered FileNetOperationsUtils.compareToFromDate() with parameters::"
						+ loHmReqExceProp.toString());

		try {
			if ((null != asToDate && !asToDate.trim().equals(""))
					&& ((null != asFromDate && !asFromDate.trim().equals("")))) {
				loToDate = DateUtil.getDate(asToDate);
				loFromDate = DateUtil.getDate(asFromDate);
				if (loFromDate.after(loToDate) || loFromDate.equals(loToDate)) {
					ApplicationException loAppex = new ApplicationException(
							PropertyLoader.getProperty(
									P8Constants.ERROR_PROPERTY_FILE, "M60"));
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
			}
		} catch (ApplicationException aoAppex) {
			aoAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT
					.Error("Exception in FileNetOperationsUtils.compareToFromDate()::",
							aoAppex);
			throw aoAppex;
		} catch (Exception aoEx) {
			ApplicationException loAppex = new ApplicationException(
					"Error comparing toDate and fromDate :", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			LOG_OBJECT.Error("Error comparing toDate and fromDate :", aoEx);
			throw loAppex;
		}

		LOG_OBJECT.Info("Exited FileNetOperationsUtils.compareToFromDate()");
		return true;
	}

	/**
	 * The method will get the Invoice Number list for type ahead.
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of Invoice Number entered by user
	 * @return list of Invoice Number
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static List<ExtendedDocument> getInvoiceNumber(String asUserOrg,
			String lsPartialUoDenom) throws ApplicationException {
		Channel loChannel = new Channel();
		List<ExtendedDocument> loProcTitleList = new ArrayList<ExtendedDocument>();
		LOG_OBJECT.Info("Entered FileNetOperationsUtils.getInvoiceNumber()");
		try {
			loChannel.setData(HHSConstants.USER_ORG, asUserOrg);
			loChannel
					.setData(HHSR5Constants.PARTIAL_KEY_WORD, lsPartialUoDenom);
			TransactionManager.executeTransaction(loChannel,
					"getInvoiceNumber", HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loProcTitleList = (List<ExtendedDocument>) loChannel
					.getData(HHSR5Constants.PROCUREMENT_LIST);
		} catch (ApplicationException aoAppex) {
			throw aoAppex;
		}
		LOG_OBJECT.Info("Exiting FileNetOperationsUtils.getInvoiceNumber()");
		return loProcTitleList;
	}

	/**
	 * The method will remove Message From Session And Request.
	 * 
	 * @param asUserOrg
	 *            a string value of user organization
	 * @param lsPartialUoDenom
	 *            a string value of Invoice Number entered by user
	 */
	public static void removeMessageFromSessionAndRequest(
			ActionRequest aoRequest) {
		aoRequest.removeAttribute(ApplicationConstants.ERROR_MESSAGE);
		aoRequest.removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE);
		aoRequest
				.removeAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.ERROR_MESSAGE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.ERROR_MESSAGE_TYPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.ERROR_MESSAGE,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.ERROR_MESSAGE_TYPE,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().removeAttribute(
				ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
				PortletSession.APPLICATION_SCOPE);
	}

	public static List<Document> getUniqueListVal(List<Document> loDocList) {
		Set<Document> loSet = new HashSet<Document>();
		loSet.addAll(loDocList);
		loDocList.clear();
		loDocList.addAll(loSet);
		return loDocList;
	}

	/**
	 * This method will create filter for Notification Link Added in Emergency
	 * Release 4.0.2
	 * 
	 * @param aoFilterMap
	 * @param aoRequest
	 */
	public static void createFilterForNotificationLink(
			HashMap<String, Object> aoFilterMap, RenderRequest aoRequest) {
		aoFilterMap.put(HHSR5Constants.PARENT_ID, "root");
		aoFilterMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, HHSPortalUtil
				.parseQueryString(aoRequest,
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID));
		aoFilterMap.put(HHSR5Constants.CUSTOM_ORGANIZATION, HHSPortalUtil
				.parseQueryString(aoRequest,
						HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID));
		aoFilterMap.put(HHSR5Constants.CONTROLLER_NAME,
				HHSR5Constants.SELECT_ORGANIZATION);
		aoFilterMap.put(
				HHSR5Constants.FOLDER_PATH,
				setFolderPath(HHSPortalUtil.parseQueryString(aoRequest,
						HHSR5Constants.DOCUMENT_ORGINATOR), HHSPortalUtil
						.parseQueryString(aoRequest,
								HHSR5Constants.CITY_USER_SEARCH_PROVIDER_ID),
						HHSR5Constants.DOCUMENT_VAULT));

	}

	/**
	 * This method redirects city user to home page
	 * 
	 * @param aoRequest
	 *            to get screen parameters and next action to be performed
	 * @param aoResponse
	 *            decides the next execution flow
	 * @param aoSBOrg
	 *            organization of user
	 * @param aoSBOrgType
	 *            organization type of user
	 * @throws ApplicationException
	 */
	public static void redirectToHomePage(ActionRequest aoRequest,
			ActionResponse aoResponse, StringBuilder aoSBOrg,
			StringBuilder aoSBOrgType) throws ApplicationException {
		String lsUserRole = (String) aoRequest.getPortletSession()
				.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
						PortletSession.APPLICATION_SCOPE);
		if (aoSBOrg.toString().equalsIgnoreCase("city")
				|| aoSBOrgType.toString().equalsIgnoreCase("city_org")) {
			String lsTaskHomePagePath = aoRequest.getScheme() + "://"
					+ aoRequest.getServerName() + ":"
					+ aoRequest.getServerPort() + aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL
					+ "&_pageLabel=portlet_hhsweb_portal_page_city_home";
			try {
				aoResponse.sendRedirect(lsTaskHomePagePath);
			} catch (IOException aoExp) {
				throw new ApplicationException(
						"Not able to redirect on task home page after validating user.",
						aoExp);
			}
		} else if (aoSBOrg.toString().equalsIgnoreCase("agency")
				|| aoSBOrgType.toString().equalsIgnoreCase("agency_org")) {
			// portlet_hhsweb_portal_page_agency_home
			String lsAgencyHomePagePath = aoRequest.getScheme() + "://"
					+ aoRequest.getServerName() + ":"
					+ aoRequest.getServerPort() + aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL + "&_pageLabel=";

			if (lsUserRole != null
					&& (lsUserRole.equalsIgnoreCase("Manager") || lsUserRole
							.equalsIgnoreCase("Staff"))) {
				lsAgencyHomePagePath = lsAgencyHomePagePath
						.concat("portlet_hhsweb_agency_r1");
			} else {
				lsAgencyHomePagePath = lsAgencyHomePagePath
						.concat("portlet_hhsweb_portal_page_agency_home");
			}
			try {
				aoResponse.sendRedirect(lsAgencyHomePagePath);
			} catch (IOException aoExp) {
				throw new ApplicationException(
						"Not able to redirect on Agency home page after validating user.",
						aoExp);
			}
		} else {
			String lsProviderHomePagePath = aoRequest.getScheme() + "://"
					+ aoRequest.getServerName() + ":"
					+ aoRequest.getServerPort() + aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL
					+ "&_pageLabel=portlet_hhsweb_portal_page_provider_home";
			try {
				aoResponse.sendRedirect(lsProviderHomePagePath);
			} catch (IOException aoExp) {
				throw new ApplicationException(
						"Not able to redirect on provider home page after validating user.",
						aoExp);
			}
		}
	}

	/** This method was added from ManageDocumentController in Emergency build 4.0.2
	 * This method is used to get the shared document provider list
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoUserSession
	 *            user session
	 * @param asAgencyType
	 *            agency type
	 * @param asProviderId
	 *            provider id
	 * @param aoRequest
	 *            request object
	 * @return map
	 * @throws ApplicationException
	 *             application exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getSharedAgencyProviderList(
			P8UserSession aoUserSession, String asAgencyType,
			String asProviderId, RenderRequest aoRequest)
			throws ApplicationException {
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asAgencyType", asAgencyType);
		loChannel.setData("asProviderId", asProviderId);
		TransactionManager.executeTransaction(loChannel,
				"getSharedDocumentsOwnerList_filenet");
		TreeSet loTemploProviderSet = (TreeSet) loChannel
				.getData("providerList");
		List<ProviderBean> loProviderList = null;
		// First checking provided list in session, if not found then search the
		// DB
		if (aoRequest.getPortletSession().getAttribute("provList",
				PortletSession.APPLICATION_SCOPE) != null) {
			loProviderList = (List) aoRequest.getPortletSession().getAttribute(
					"provList", PortletSession.APPLICATION_SCOPE);
		} else {
			loProviderList = FileNetOperationsUtils.getProviderList();
		}
		// R4 Homepage changes: fetching agency list from cache.
		TreeSet<String> loAgencyList = (TreeSet<String>) BaseCacheManagerWeb
				.getInstance().getCacheObject(ApplicationConstants.AGENCY_LIST);
		if (null == loAgencyList || loAgencyList.isEmpty()) {
			loAgencyList = FileNetOperationsUtils.getNYCAgencyListFromDB();
		}
		Iterator loItr = loAgencyList.iterator();
		while (loItr.hasNext()) {
			loItr.next();
		}
		// R4 Homepage changes: Passing additional parameter : loAgencyList
		// since the sorted map would contain both provider and agency shared
		// list
		// which would be populated in 'Select an Organization' dropdown in
		// 'Documents shared with your Organization' section on Provider/Agency
		// Homepage.
		return convertToMap(loProviderList, loAgencyList, loTemploProviderSet);
	}

	/** This method was added from ManageDocumentController in Emergency build 4.0.2
	 * This method is used to sort the map
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoProviderList
	 *            provider list
	 * @param aoAgencyList
	 *            agency list
	 * @param aoProviderSet
	 *            provider set
	 * @return sorted map Updated Method in R4
	 */
	private static Map<String, String> convertToMap(
			List<ProviderBean> aoProviderList, TreeSet<String> aoAgencyList,
			TreeSet aoProviderSet) {
		Map<String, String> loProviderMap = new HashMap<String, String>();
		if (aoProviderList != null) {
			Iterator<ProviderBean> loItrProvider = aoProviderList.iterator();
			while (loItrProvider.hasNext()) {
				ProviderBean loProvider = loItrProvider.next();
				loProviderMap.put(loProvider.getHiddenValue(),
						loProvider.getDisplayValue());
			}
		}
		// R4 homepage changes
		Map<String, String> loAgencyMap = new HashMap<String, String>();
		if (aoAgencyList != null) {
			Iterator loIterator = aoAgencyList.iterator();
			while (loIterator.hasNext()) {
				String lsAgency = (String) loIterator.next();
				String[] loAgencyName = lsAgency
						.split(ApplicationConstants.TILD);
				loAgencyMap.put(loAgencyName[0], loAgencyName[1]);
			}
		}
		Iterator loSelectedProvider = aoProviderSet.iterator();
		Map<String, String> loProviderToDisplay = new HashMap<String, String>();
		while (loSelectedProvider.hasNext()) {
			String lsProviderId = (String) loSelectedProvider.next();
			if (loProviderMap.containsKey(lsProviderId)) {
				loProviderToDisplay.put(lsProviderId
						.concat((ApplicationConstants.TILD_PROVIDER)),
						StringEscapeUtils.unescapeJavaScript(loProviderMap
								.get(lsProviderId)));
			}
			if (loAgencyMap.containsKey(lsProviderId)) {
				loProviderToDisplay
						.put(lsProviderId
								.concat((ApplicationConstants.TILD_AGENCY)),
								StringEscapeUtils
										.unescapeJavaScript(loAgencyMap
												.get(lsProviderId)));
			}
			if (lsProviderId.equalsIgnoreCase(ApplicationConstants.CITY_ORG)) {
				loProviderToDisplay.put(ApplicationConstants.TILD_CITY,
						ApplicationConstants.CITY_USER_NAME);
			}
		}
		// Sort Map<String, String> loProviderToDisplay; Defect Fix #2554
		Map<String, String> loSortProviderMapToDisplay = new LinkedHashMap<String, String>();
		List<String> loKeyList = new ArrayList<String>(
				loProviderToDisplay.keySet());
		List<String> loValueList = new ArrayList<String>(
				loProviderToDisplay.values());
		Set<String> loSortedSet = new TreeSet<String>(
				String.CASE_INSENSITIVE_ORDER);// Additional
												// fix
												// for
												// defect
												// #2554
												// for
												// case
												// insensitive
												// sorting
		loSortedSet.addAll(loValueList);

		Object[] loSortedArray = loSortedSet.toArray();
		for (int loMapCounter = 0; loMapCounter < loSortedArray.length; loMapCounter++) {
			loSortProviderMapToDisplay.put(loKeyList.get(loValueList
					.indexOf(loSortedArray[loMapCounter])),
					(String) loSortedArray[loMapCounter]);
		}
		return loSortProviderMapToDisplay;
	}
	
/** This method is added to display 'BA Status when tasktype is "Service application"' 	
	 * <ul>
	 * <li>Method Added in R7</li>	
	 * @param loAlTaskItemLIst
	 * @throws ApplicationException
	 */
private static void getBusinessApplicationStatus(ArrayList loAlTaskItemLIst) throws ApplicationException
{
	Channel loChannel = new Channel();
	String lsBusinessAppId = null;
	String lsProviderId = null;
	try
	{
		loChannel.setData("aoTaskDetailList",loAlTaskItemLIst);
		HHSTransactionManager.executeTransaction(loChannel,
				HHSR5Constants.GET_BUSINESS_APPLICATION_STATUS,
				HHSR5Constants.TRANSACTION_ELEMENT_R5);
		loAlTaskItemLIst = (ArrayList<TaskQueue>)loChannel.getData(HHSR5Constants.RETURN_TASK_DETAIL_LIST);

	}
	catch(ApplicationException apex)
	{
		LOG_OBJECT.Error("Error in getBusinessApplicationStatus Method.", apex);
	}
}
}
// End Class
