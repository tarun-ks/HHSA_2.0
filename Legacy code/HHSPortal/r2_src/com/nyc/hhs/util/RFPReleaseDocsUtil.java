package com.nyc.hhs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

public class RFPReleaseDocsUtil extends FileNetOperationsUtils
{

	/**
	 * This method is used to set the channel when we select document from vault
	 * Button is clicked
	 * <ul>
	 * <li>Create the required parameters map and put all required parameters
	 * into the map</li>
	 * <li>Set the transaction name and required properties map into Channel
	 * Object</li>
	 * <li>Create and put filter map if required for some specific filtration</li>
	 * <li>After setting all required parameters into Channel Object Return the
	 * Channel Object Back</li>
	 * </ul>
	 * 
	 * @param asUserOrgType - organization type
	 * @param aoUserSession - Session
	 * @param aoChannel - Channel Object
	 * @param aoFilterMap - filter map
	 * @param asDocType - docType
	 * @throws ApplicationException - for throwing the exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static void getSelectDocFromVaultChannel(String asUserOrgType, P8UserSession aoUserSession,
			Channel aoChannel, String asOrgType, HashMap aoFilterMap, String asDocType) throws ApplicationException
	{
		// This transaction get the list of document from the vault for that
		// particular doc type.
		String lsObjectsPerPageKey = HHSConstants.ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME + "_"
				+ ApplicationConstants.APPLICATION_DOCUMENT_VIEW_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String lsObjectsPerPage = (String) loApplicationSettingMap.get(lsObjectsPerPageKey);
		aoChannel.setData(HHSConstants.BASE_TRANSACTION_NAME, HHSConstants.DISPLAY_DOC_LIST_FILENET_TRANS_NAME);
		HashMap<String, String> loReqProps = new HashMap<String, String>();
		// Start Added in R5
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, "DOC");
		loReqProps.put(P8Constants.DOCUMENT_TITLE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "DOC");
		// added for parent path in Release5
		loReqProps.put(HHSR5Constants.FOLDERS_FILED_IN, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_ID, "DOC");
		HashMap<String, Object> loHMFilterMap = new HashMap<String, Object>();
		aoFilterMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrgType);
		aoFilterMap.put(P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY, Boolean.FALSE);
		if (StringUtils.isNotBlank(asDocType) && asDocType.equalsIgnoreCase(HHSConstants.OTHER))
		{
			String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType(asDocType, asUserOrgType);
			aoChannel.setData("docCategory", lsDocCategory);
		}
		// End Added in R5

		// Start of Changes done for defect QC : 5702
		if (asOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
		{
			if (P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC.equalsIgnoreCase((String) aoChannel
					.getData(P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC)))
			{
				loHMFilterMap.put(P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC, Boolean.TRUE);
			}
		}
		// End of Changes done for defect QC : 5702

		HashMap loHMOrderByMap = new HashMap();
		// Start Added in R5
		loHMOrderByMap.put(1, P8Constants.PROPERTY_CE_DOC_TYPE + HHSConstants.SPACE + HHSConstants.ASCENDING + ",DOC");
		loHMOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE + HHSConstants.SPACE + HHSConstants.ASCENDING
				+ ",DOC");
		aoChannel.setData(ApplicationConstants.DOC_TYPE, asDocType);
		aoChannel.setData(ApplicationConstants.REQ_PROPS, loReqProps);
		aoChannel.setData(HHSConstants.FILENET_FILTER_MAP, aoFilterMap);
		// End Added in R5

		aoChannel.setData(HHSConstants.INCLUDE_FILENET_FILTER, true);
		aoChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP, loHMOrderByMap);
		aoChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		aoChannel.setData(HHSConstants.OBJECTS_PER_PAGE, lsObjectsPerPage);
	}

	/**
	 * This method is used to set the channel when we select document from vault
	 * selected from the drop down from screen no S238
	 * <ul>
	 * <li>Create the required parameters map and put all required parameters
	 * into the map</li>
	 * <li>Set the transaction name and required properties map into Channel
	 * Object</li>
	 * <li>Create and put filter map if required for some specific filtration</li>
	 * <li>After setting all required parameters into Channel Object Return the
	 * Channel Object Back</li>
	 * </ul>
	 * Release 3.6.0 Enhancement 6485
	 * @param asUserOrgType - organization type
	 * @param asDocType - Document type
	 * @param aoUserSession - user session
	 * @param aoMap - HashMap
	 * @throws ApplicationException Exception to be thrown
	 * @return channel
	 */
	// Added one parameter aoMap for filter properties - Release 5
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static Channel getProposalDocsFromVaultChannel(String asUserOrg, String asDocType,
			P8UserSession aoUserSession, String asUserOrgType, HashMap aoMap) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap loHMOrderByMap = new HashMap();
		// This transaction get the list of document from the vault for that
		// particular doc type.
		String lsObjectsPerPageKey = HHSConstants.ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME + "_"
				+ ApplicationConstants.APPLICATION_DOCUMENT_VIEW_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String lsObjectsPerPage = (String) loApplicationSettingMap.get(lsObjectsPerPageKey);
		loChannelObj.setData(HHSConstants.BASE_TRANSACTION_NAME, HHSConstants.DISPLAY_PROPOSAL_DOC_LIST_TRANS_NAME);
		HashMap<String, String> loReqProps = new HashMap<String, String>();
		// update in R5
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, "DOC");
		loReqProps.put(P8Constants.DOCUMENT_TITLE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, "DOC");
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, "DOC");
		loReqProps.put("ID", "DOC");
		// added for parent path in Release5
		loReqProps.put(HHSR5Constants.FOLDERS_FILED_IN, "DOC");
		aoMap.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asUserOrg);
		aoMap.put(P8Constants.PROPERTY_CE_IS_SYSTEM_DOC_CATEGORY, Boolean.FALSE);
		loHMOrderByMap.put(1, P8Constants.PROPERTY_CE_DOC_TYPE + HHSConstants.SPACE + HHSConstants.ASCENDING + ",DOC");
		loHMOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE + HHSConstants.SPACE + HHSConstants.ASCENDING
				+ ",DOC");
		// Release 3.6.0 Enhancement 6485
		if (null != asDocType && asDocType.equalsIgnoreCase(HHSConstants.OTHER))
		{
			String lsDocCategory = RFPReleaseDocsUtil.getDocCategoryForRfpOtherDocType(asDocType, asUserOrgType);
			loChannelObj.setData("docCategory", lsDocCategory);
		}
		loChannelObj.setData(ApplicationConstants.DOC_TYPE, asDocType);
		loChannelObj.setData(ApplicationConstants.REQ_PROPS, loReqProps);
		loChannelObj.setData(HHSConstants.FILENET_FILTER_MAP, aoMap);
		// changing to false for R5
		loChannelObj.setData(HHSConstants.INCLUDE_FILENET_FILTER, true);
		loChannelObj.setData(HHSConstants.FILENET_ORDER_BY_MAP, loHMOrderByMap);
		loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannelObj.setData(HHSConstants.OBJECTS_PER_PAGE, lsObjectsPerPage);
		return loChannelObj;
	}

	/**
	 * This method gets document detail to be selected from vault
	 * <ul>
	 * <li>Get the documentProperty Map from Channel Object</li>
	 * <li>Traverse through the map and get the property values and add them
	 * into document bean</li>
	 * <li>Add each document Bean into the document bean list</li>
	 * <li>Return the list of document beans</li>
	 * </ul>
	 * 
	 * @param aoChannel - Channel Object
	 * @return loLSelectedDocFromVault list of DocumentBeans
	 * @throws ApplicationException throws application Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static List<ExtendedDocument> setSelectedDocumentBean(Channel aoChannel) throws ApplicationException
	{
		List<ExtendedDocument> loLSelectedDocFromVault = new ArrayList<ExtendedDocument>();
		Object loDocResultObject = aoChannel.getData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER);
		// List of Document from Document Vault.
		if (loDocResultObject != null)
		{
			List loDocResult = (List) loDocResultObject;
			Iterator<HashMap> loIterator = loDocResult.iterator();
			while (loIterator.hasNext())
			{
				HashMap loHMDocProps = (HashMap) loIterator.next();
				ExtendedDocument loDocumentObj = new ExtendedDocument();
				if (loHMDocProps != null)
				{
					loDocumentObj.setDocumentTitle((String) loHMDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
					loDocumentObj.setDocumentType((String) loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE));
					loDocumentObj.setDocumentCategory((String) loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
					loDocumentObj.setModifiedDate((DateUtil.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps
							.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)))));
					loDocumentObj.setLastModifiedById((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID));
					loDocumentObj.setLastModifiedByName((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
					if (loHMDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID) != null)
					{
						loDocumentObj.setDocumentId((loHMDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID)).toString());
					}
					loDocumentObj.setCreatedBy(((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID)));
					loDocumentObj.setCreatedDate((DateUtil.getDateMMddYYYYFormat((java.util.Date) loHMDocProps
							.get(P8Constants.PROPERTY_CE_DATE_CREATED))));
					// Start Added in R5
					String lsParent = (String) loHMDocProps.get(HHSR5Constants.FOLDERS_FILED_IN);
					if (null != lsParent)
					{
						loDocumentObj.setFilePath(lsParent);
					}// End Added in R5
				}

				loLSelectedDocFromVault.add(loDocumentObj);
			}
		}
		return loLSelectedDocFromVault;
	}

	/**
	 * This method is used to ge the required document properties for a specific
	 * document
	 * <ul>
	 * <li>Get the document id and filenet session object from the request</li>
	 * <li>execute transaction with transactionID <b>displayDocProp_filenet</b></li>
	 * <li>Return the map with the values of the required properties</li>
	 * </ul>
	 * 
	 * @param aoUserSession user session
	 * @param asUserOrgType organization type
	 * @param aoDocumentId document Id
	 * @return hash map
	 * @throws ApplicationException exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static HashMap<String, Object> getDocumentInfo(P8UserSession aoUserSession, String asUserOrgType,
			String aoDocumentId) throws ApplicationException
	{
		HashMap<String, String> loReqProps = new HashMap<String, String>();
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_FILE_TYPE, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_CATEGORY, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_SAMPLE_TYPE, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY_ID, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY_ID, HHSConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, HHSConstants.EMPTY_STRING);
		List loDocIdList = new ArrayList();
		loDocIdList.add(aoDocumentId);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loReqProps);
		loChannel.setData(HHSConstants.AO_DOC_ID_LIST, loDocIdList);
		TransactionManager.executeTransaction(loChannel, HHSConstants.DISPLAY_DOC_PROPERTIES_FILENET_TRANS_NAME);
		HashMap loHmDocProps = (HashMap) loChannel.getData(HHSConstants.DOCUMENT_PROPERTY_HASH_MAP);
		return (HashMap<String, Object>) loHmDocProps.get(aoDocumentId);
	}

	/**
	 * This method is used to get the document types list from doctype xml
	 * <ul>
	 * <li>Get the xml dom object and organization ID from the parameters</li>
	 * <li>Return the list of the document type back</li>
	 * </ul>
	 * 
	 * @param aoDocTypeXML valid xml dom object
	 * @param asOrgId organization ID
	 * @return document type list
	 * @throws ApplicationException exception
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getDoctypesFromXML(Object aoDocTypeXML, String asOrgId) throws ApplicationException
	{
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		List<String> loDoctypesList = new ArrayList<String>();

		if (aoDocTypeXML == null || aoDocTypeXML.toString().equals(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getDoctypesFromXML Method. Required Parameters are missing");
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		loHmReqExceProp.put(HHSConstants.AO_DOC_TYPE_XML, aoDocTypeXML.toString());
		loHmReqExceProp.put(HHSConstants.AO_DOC_TYPE_DOM, aoDocTypeXML.toString());
		try
		{
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;

			Element loElt = XMLUtil.getElement("//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asOrgId
					+ "\"] //" + P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@name=\"All\"]", loDocTypeXML);
			if (null != loElt)
			{
				List<Element> loTypeElementList = loElt.getChildren(P8Constants.XML_DOC_TYPE_NODE);
				for (Element loTypeElement : loTypeElementList)
				{
					loDoctypesList.add(loTypeElement.getAttributeValue(HHSConstants.NAME));
				}
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDoctypesFromXML Method.", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		Collections.sort(loDoctypesList);
		return loDoctypesList;
	}

	/**
	 * This method is used to reset and re initialize the pagination parameters
	 * in filenet session object
	 * <ul>
	 * <li>Get the parameter values and filenet session</li>
	 * <li>Reset all the values accordingly</li>
	 * </ul>
	 * 
	 * @param aoSession portlet session
	 * @param aoUserSession p8 user session
	 * @param asNextPage next page value
	 * @param asSortBy sort by values
	 * @param asSortType sort type value
	 * @param asParentNode parent node object
	 * @throws ApplicationException exception to be thrown
	 */
	public static void setReqRequestParameter(PortletSession aoSession, P8UserSession aoUserSession, String asNextPage)
			throws ApplicationException
	{
		String lsAppSettingMapKey = HHSConstants.ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME + HHSConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		@SuppressWarnings("unchecked")
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		int liAllowedObjectCount = Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, liAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		if (null != asNextPage)
		{
			aoUserSession.setNextPageIndex(Integer.valueOf(asNextPage) - HHSConstants.INT_ONE);

		}
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, asNextPage,
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, aoUserSession.getTotalPageCount()
				* liAllowedObjectCount, PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method will get a list of document categories from XML DOM object
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * @param aoDocTypeXML a doc type XML object
	 * @param asDocTypes a string value of document type
	 * @param asOrgId a string value of organization Id
	 * @param asFinancial a string value of Financial
	 * @return a list of document categories for input document type
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static List<String> getRFPDocCategoryFromXML(Object aoDocTypeXML, String asDocTypes, String asOrgId,
			String asFinancial) throws ApplicationException
	{
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		List<String> loDocCategoryList = new ArrayList<String>();
		loHmReqExceProp.put(HHSConstants.AS_DOC_TYPE, asDocTypes);
		if (null == aoDocTypeXML || aoDocTypeXML.toString().equals(HHSConstants.EMPTY_STRING) || asOrgId == null
				|| asOrgId.trim().equals(HHSConstants.EMPTY_STRING))
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in getDocCategoryFromXML Method. Required Parameters are missing");
			loHmReqExceProp.put(HHSConstants.ORGANIZATION_ID, asOrgId);
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		loHmReqExceProp.put(HHSConstants.AO_DOC_TYPE_XML, aoDocTypeXML.toString());
		String lsDocCategory = null;
		try
		{
			org.jdom.Document loDocTypeXML = (org.jdom.Document) aoDocTypeXML;
			if (null != asDocTypes && !asDocTypes.trim().equals(HHSConstants.EMPTY_STRING))
			{
				List<Element> loElementsList = null;

				// Start of Changes done for defect QC : 5725
				if (null != asFinancial)
				{
					loElementsList = XMLUtil.getElementList(
							"//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asOrgId + "\"] //"
									+ P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\"" + asDocTypes + "\"]",
							loDocTypeXML);
				}
				else
				{
					loElementsList = XMLUtil.getElementList("//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\""
							+ asOrgId + "\"] //" + P8Constants.XML_DOC_TYPE_PROPERTY + "[@name=\"" + asDocTypes
							+ "\" and @type=\"rfp_award\"]", loDocTypeXML);
				}
				if (null != loElementsList)
				{
					for (int liCount = HHSConstants.INT_ZERO; liCount < loElementsList.size(); liCount++)
					{
						Element loParentElement = loElementsList.get(liCount).getParentElement();
						lsDocCategory = loParentElement.getAttributeValue(HHSConstants.NAME);
						if (!HHSConstants.FALSE.equalsIgnoreCase(loElementsList.get(liCount).getAttributeValue(
								"displayInApp")))
						{
							loDocCategoryList.add(lsDocCategory);
						}
					}
				}
				else
				{
					ApplicationException loAppex = new ApplicationException(
							"Error in getDocCategoryFromXML Method Element object is null");
					loAppex.setContextData(loHmReqExceProp);
					throw loAppex;
				}
			}
			else
			{
				List<Element> loElementsList = null;
				if (null != asFinancial)
				{
					// Start || Changes done for Enhancement #6429 for Release
					// 3.4.0
					if (!asFinancial.equals(HHSConstants.STRING_FOR_AWARD_SCREEN))
					{
						loElementsList = XMLUtil.getElementList("/" + P8Constants.ROOT_NODE + "/"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asOrgId + "\"]/"
								+ P8Constants.XML_DOC_CATEGORY_PROPERTY, loDocTypeXML);
					}
					else
					{
						// End || Changes done for Enhancement #6429 for Release
						// 3.4.0
						loElementsList = XMLUtil.getElementList("/" + P8Constants.ROOT_NODE + "/"
								+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asOrgId + "\"]/"
								+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@displayInAward=\"true\"]", loDocTypeXML);
					}
				}
				else
				{
					loElementsList = XMLUtil.getElementList("/" + P8Constants.ROOT_NODE + "/"
							+ P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asOrgId + "\"]/"
							+ P8Constants.XML_DOC_CATEGORY_PROPERTY + "[@type=\"rfp_award\"]", loDocTypeXML);
				}
				for (int liCount = HHSConstants.INT_ZERO; liCount < loElementsList.size(); liCount++)
				{
					lsDocCategory = loElementsList.get(liCount).getAttributeValue(HHSConstants.NAME);
					if (!HHSConstants.FALSE.equalsIgnoreCase(loElementsList.get(liCount).getAttributeValue(
							"displayInApp")))
					{
						loDocCategoryList.add(lsDocCategory);
					}
				}
				// End of Changes done for defect QC : 5725
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDocCategoryFromXML Method", aoEx);
			loAppex.setContextData(loHmReqExceProp);
			throw loAppex;
		}
		Collections.sort(loDocCategoryList);
		return loDocCategoryList;
	}

	/**
	 * This method will set document categories and document type in document
	 * object based on document category
	 * 
	 * @param aoDoc a document object containing document properties
	 * @param asDocCategory a string value of document category
	 * @param asFinancial a string value of Financial
	 * @param asUserOrg a string value of user organization
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static void setRFPDocCategorynDocType(Document aoDoc, String asDocCategory, String asUserOrg,
			String asFinancial) throws ApplicationException
	{
		try
		{
			if (null == asDocCategory || (HHSConstants.EMPTY_STRING).equalsIgnoreCase(asDocCategory))
			{
				ArrayList<String> loCategoryList = new ArrayList<String>();
				loCategoryList = (ArrayList<String>) getRFPDocCategoryList(asUserOrg, asFinancial);
				loCategoryList.add(HHSConstants.INT_ZERO, HHSConstants.EMPTY_STRING);
				aoDoc.setCategoryList(loCategoryList);
			}
			else
			{
				if (CollectionUtils.isEmpty(aoDoc.getCategoryList()))
				{
					ArrayList<String> loCategoryList = new ArrayList<String>();
					loCategoryList = (ArrayList<String>) getRFPDocCategoryList(asUserOrg, asFinancial);
					loCategoryList.add(HHSConstants.INT_ZERO, HHSConstants.EMPTY_STRING);
					aoDoc.setCategoryList(loCategoryList);
				}
				ArrayList<String> loTypeList = new ArrayList<String>();
				loTypeList = (ArrayList<String>) getRFPDocTypeForDocCategory(asDocCategory, asUserOrg);
				aoDoc.setDocCategory(asDocCategory);
				loTypeList.add(HHSConstants.INT_ZERO, HHSConstants.EMPTY_STRING);
				aoDoc.setTypeList(loTypeList);
			}
		}
		catch (ApplicationException aoAppex)
		{
			throw aoAppex;
		}
	}

	/**
	 * This method will get a list of document categories from XML DOM object
	 * 
	 * @param asUserOrg a string value of user organization
	 * @param asFinancial a string value of Financial
	 * @return a list of document categories
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static List<String> getRFPDocCategoryList(String asUserOrg, String asFinancial) throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		return getRFPDocCategoryFromXML(loXMLDoc, null, asUserOrg, asFinancial);
	}

	/**
	 * This method will get a list of document types from XML DOM object based
	 * on document category
	 * 
	 * @param asDocCategory a string value of document category
	 * @param asUserOrg a string value of user organization
	 * @return a list of document types
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static List<String> getRFPDocTypeForDocCategory(String asDocCategory, String asUserOrg)
			throws ApplicationException
	{
		org.jdom.Document loXMLDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.FILENETDOCTYPE);
		return getDoctypesFromXML(loXMLDoc, asDocCategory, asUserOrg, null);
	}

	/**
	 * This method is used to ge thte document category from the document type
	 * <ul>
	 * <li>get the document type from the request</li>
	 * <li>Create the x path for the document type and the organization type</li>
	 * </ul>
	 * @param asDocType document type
	 * @param asUserOrg user organization type
	 * @return document category
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static String getDocCategoryForDocType(String asDocType, String asUserOrg) throws ApplicationException
	{

		String lsDocCategory = null;
		try
		{
			org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// returning document category node from DocType.xml File
			Element loElt = XMLUtil.getElement("//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asUserOrg
					+ "\"] //" + P8Constants.XML_DOC_TYPE_NODE + "[@name=\"" + asDocType + "\"]", loDocTypeXML);
			if (null != loElt)
			{
				Element loParentElement = loElt.getParentElement();
				lsDocCategory = loParentElement.getAttributeValue(HHSConstants.NAME);
			}
			else
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in getDocCategoryForDocType Method Element object is null");
				throw loAppex;
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDoctypesFromXML Method.", aoEx);
			throw loAppex;
		}
		return lsDocCategory;
	}

	/**
	 * This method is used to get the document category from the document type
	 * <ul>
	 * <li>get the document type from the request</li>
	 * <li>Create the x path for the document type and the organization type</li>
	 * </ul>
	 * @param asDocType document type
	 * @param asUserOrg user organization type
	 * @return document category
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public static String getDocCategoryForRfpOtherDocType(String asDocType, String asUserOrg)
			throws ApplicationException
	{

		String lsDocCategory = null;
		try
		{
			org.jdom.Document loDocTypeXML = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.FILENETDOCTYPE);
			// returning document category node from DocType.xml File
			Element loElt = XMLUtil.getElement(
					"//" + P8Constants.XML_DOC_ORG_ID_PROPERTY + "[@name=\"" + asUserOrg + "\"] //"
							+ P8Constants.XML_DOC_TYPE_NODE + "[@name=\"" + asDocType + "\"and @type=\"rfp_award\"]",
					loDocTypeXML);
			if (null != loElt)
			{
				Element loParentElement = loElt.getParentElement();
				lsDocCategory = loParentElement.getAttributeValue(HHSConstants.NAME);
			}
			else
			{
				ApplicationException loAppex = new ApplicationException(
						"Error in getDocCategoryForDocType Method Element object is null");
				throw loAppex;
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppex = new ApplicationException("Error in getDoctypesFromXML Method.", aoEx);
			throw loAppex;
		}
		return lsDocCategory;
	}
}
