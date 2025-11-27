package com.nyc.hhs.controllers.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ContentOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.RFPReleaseDocsUtil;

/**
 * This class implement the functionality like selecting document from vault,
 * uploading document, removing and displaying document on document screen for
 * business application .
 * 
 */

public class DocumentBusinessApp extends BusinessApplication
{
	private static final LogInfo LOG_OBJECT = new LogInfo(P8ContentOperations.class);

	/**
	 * Gets the channel object for action
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Action request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{

		Channel loChannel = null;

		/**
		 * Below section executes when user selects select doc from vault in the
		 * drop down on document screen and it displays the the list of document
		 * from the vault for that particular doc type.
		 */
		if (null != asAction && asAction.equals("selectDocFromVault"))
		{
			loChannel = selectDocFromVault(asOrgId, aoRequest);
		}
		else if (null != asAction && asAction.equals("displayDocProp"))
		{
			loChannel = new Channel();

		}
		else if (null != asAction && asAction.equals("submitDocId"))
		{
			loChannel = submitDocId(asSectionName, asOrgId, asAppId, aoRequest);
		}
		/*
		 * Below section executes when user selects upload document from the
		 * drop down on document screen and it displays the first overlay screen
		 * with doc type , category and browse to select the file to upload.
		 */
		else if (null != asAction && asAction.equals("documentupload"))
		{
			String lsDocCategory = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_DOC_CATEGORY_REQ_PARAMETER);
			String lsDocType = aoRequest.getParameter("docType");
			String lsFormName = aoRequest.getParameter(ApplicationConstants.FORM_NAME_SMALL_CAPS);
			String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FORM_VERSION_SMALL_CAPS);
			String lsServiceAppID = aoRequest.getParameter("serviceAppID");
			String lsSectionId = aoRequest.getParameter("sectionId");
			String lsEntityId = aoRequest.getParameter("entityId");// Defect
																	// #1805 fix
			ApplicationSession.setAttribute(lsDocCategory, aoRequest, "document_category");
			ApplicationSession.setAttribute(lsDocType, aoRequest, "document_type");
			ApplicationSession.setAttribute(lsFormName, aoRequest, "form_name");
			ApplicationSession.setAttribute(lsFormVersion, aoRequest, "form_version");
			ApplicationSession.setAttribute(lsServiceAppID, aoRequest, "service_app_id");
			ApplicationSession.setAttribute(lsSectionId, aoRequest, "section_id");
			ApplicationSession.setAttribute(lsEntityId, aoRequest, "entityId");// Defect
																				// #1805
																				// fix
		}
		/*
		 * Below section executes when user selects delete document from the
		 * drop down on document screen and update the document table. It
		 * display updated document screen with no document name not associated
		 * to that particular doc type.
		 */
		else if (null != asAction && asAction.equals("removeDocFromApplication"))
		{
			loChannel = removeDocFromApplication(asSectionName, asOrgId, asAppId, aoRequest);

		}
		return loChannel;
	}

	/**
	 * This method is updated for Release 3.8.0 defect #6469 This method will
	 * remove the document entry from document table (soft delete)
	 * 
	 * @param asSectionName - Name of the Section
	 * @param asOrgId - Organization ID
	 * @param asAppId - Application ID
	 * @param aoRequest - Action Request
	 * @return
	 * @throws ApplicationException
	 */
	private Channel removeDocFromApplication(String asSectionName, String asOrgId, String asAppId,
			ActionRequest aoRequest) throws ApplicationException
	{
		LOG_OBJECT
				.Debug("removeDocFromApplication:: Start Remove Document From Application in Business Applcaition Document");
		Channel loChannel;
		String lsFormName = aoRequest.getParameter(ApplicationConstants.FORM_NAME_SMALL_CAPS);
		String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FORM_VERSION_SMALL_CAPS);
		String lsOrgId = asOrgId;
		String lsDocType = aoRequest.getParameter(ApplicationConstants.DOC_TYPE);
		String lsDocIdVaultLinkUpdate = aoRequest.getParameter(HHSConstants.BULK_UPLOAD_DOC_ID);
		String lsDocCat = aoRequest.getParameter("asDocCat");
		String lsServiceAppId = aoRequest.getParameter("serviceAppID");
		String lsSectionId = aoRequest.getParameter("sectionId");
		String lsEntityId = aoRequest.getParameter("entityId");// Defect #1805
																// fix
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loChannel = new Channel();
		loChannel.setData(HHSConstants.BULK_UPLOAD_DOC_ID, "");
		loChannel.setData("asDocIdVaultLinkUpdate", lsDocIdVaultLinkUpdate);
		if (lsSectionId == null)
		{
			loChannel.setData("asFormName", lsFormName);
			loChannel.setData("asFormVersion", lsFormVersion);
		}
		else if (lsSectionId.equalsIgnoreCase("servicessummary"))
		{
			loChannel.setData("asFormName", lsServiceAppId);
			loChannel.setData("asFormVersion", lsServiceAppId);
		}
		loChannel.setData("asOrgId", lsOrgId);
		loChannel.setData("asAppId", asAppId);
		loChannel.setData(HHSR5Constants.AS_DOC_TYPE, lsDocType);
		loChannel.setData("asDocCat", lsDocCat);
		loChannel.setData("asLastModDate", "");
		loChannel.setData("asLastModBy", lsUserId);
		loChannel.setData("asSubmissionDate", "");
		loChannel.setData("asSubmissionBy", lsUserId);
		loChannel.setData(P8Constants.AS_DOC_TITLE, "");
		loChannel.setData("asDocStatus", ApplicationConstants.NOT_STARTED_STATE);
		loChannel.setData("asUserId", lsUserId);
		loChannel.setData("asSection", asSectionName);
		loChannel.setData("asServiceAppId", lsServiceAppId);
		loChannel.setData("asSubSectionIdNextTab", asSectionName);
		loChannel.setData("asSectionId", lsSectionId);
		loChannel.setData("entityId", lsEntityId);// Defect #1805 fix
		HashMap loHmDocReqProps = new HashMap();
		// start of change for release 3.8.0 defect #6469
		HashMap loAppStatusDocMap = new HashMap();
		loAppStatusDocMap.put(ApplicationConstants.DOCUMENT_ID, lsDocIdVaultLinkUpdate);
		loChannel.setData(HHSConstants.AO_APP_STATUS_DOC_MAP, loAppStatusDocMap);
		//New Filling Check after 4.0.2
		// Commenting below transaction, after Release 4.0.2 and adding new transaction,as old one was only checking row count in Document Table, but now we want row count from  union of lapsing,document and document view.
		//TransactionManager.executeTransaction(loChannel, HHSConstants.DOC_ID_COUNT);
		loChannel.setData(ApplicationConstants.DOCUMENT_ID, lsDocIdVaultLinkUpdate);
		HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.DOC_ID_COUNT_IN_ANY_TABLE);
		Boolean abDocExists = (Boolean) loChannel.getData(HHSR5Constants.DOC_EXISTS_IN_DB);
		// Added after Release 4.0.2 to make it unlinked when we are removing it form a entity specially from Business application
		if (!abDocExists)
		{
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		}
		// Added after Release 4.0.2
		else
		{
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
		}
		// end of change for release 3.8.0 defect #6469
		loChannel.setData("hmReqProps", loHmDocReqProps);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannel.setData("aoFilenetSession", loUserSession);
		if (lsSectionId == null || !lsSectionId.equalsIgnoreCase("servicessummary"))
		{
			// This transaction will update the document from the document table
			// when we select remove from the drop down for applications.
			loChannel.setData("transaction_name", "removeDocFromApplication");
		}
		else if (lsSectionId.equalsIgnoreCase("servicessummary"))
		{ // This
			// transaction
			// will
			// update
			// the
			// document
			// from
			// the
			// document
			// table
			// when
			// we
			// select
			// remove
			// from
			// the
			// drop
			// down
			// for
			// services
			// summary.
			loChannel.setData("transaction_name", "removeDocFromApplicationServiceSummary");
		}
		LOG_OBJECT
				.Debug("removeDocFromApplication:: End Remove Document From Application in Business Applcaition Document");
		return loChannel;
	}

	/**
	 * This method will update the document table when we submit the selected
	 * document from the list of vault
	 * 
	 * @param asSectionName - Name of the Section
	 * @param asOrgId - Organization ID
	 * @param asAppId - Application ID
	 * @param aoRequest - Action Request
	 * @return loChannel
	 * @throws ApplicationException
	 */
	private Channel submitDocId(String asSectionName, String asOrgId, String asAppId, ActionRequest aoRequest)
			throws ApplicationException
	{
		String lsDocumentIdWithName = aoRequest.getParameter("radio");
		String lsDelimiter = "^^&&^^";
		String lsDocId = (String) lsDocumentIdWithName.subSequence(0,
				lsDocumentIdWithName.lastIndexOf(lsDelimiter, lsDocumentIdWithName.length()));
		String lsDocTitle = lsDocumentIdWithName.substring(lsDocumentIdWithName.lastIndexOf(lsDelimiter) + 6,
				lsDocumentIdWithName.length());

		LOG_OBJECT.Debug("submitDocId:: Start Submit document on radio button select in Business Applcaition Document");
		Channel loChannel;
		loChannel = new Channel();
		String lsDocType = aoRequest.getParameter("docType");
		String lsDocCategory = aoRequest.getParameter("docCategory");
		String lsLastModifiedDate = aoRequest.getParameter("lastModifiedDate");
		String lsSubmissionDate = aoRequest.getParameter("submissionDate");
		String lsFormName = aoRequest.getParameter("formName");
		String lsFormVersion = aoRequest.getParameter("formVersion");
		String lsUserId = aoRequest.getParameter("userId");
		String lsFormId = aoRequest.getParameter("formId");
		String lsServiceAppId = aoRequest.getParameter("serviceAppID");
		// Added in Release 5
		if (lsServiceAppId.equalsIgnoreCase(HHSR5Constants.NULL))
		{
			lsServiceAppId = HHSPortalUtil.parseQueryString(aoRequest, "service_app_id");
		}
		// Added in Release 5
		String lsSectionId = aoRequest.getParameter("sectionID");
		String lsEntityId = (String) aoRequest.getPortletSession().getAttribute("entityId");// Defect
		// Added in Release 5
		if (lsSectionId.equalsIgnoreCase(HHSConstants.NULL))
		{
			lsSectionId = (String) aoRequest.getPortletSession().getAttribute("sectionId");
		}// #1805
			// Added in Release 5 // fix
		loChannel.setData(HHSConstants.BULK_UPLOAD_DOC_ID, lsDocId);
		if (lsLastModifiedDate != null)
		{
			loChannel.setData("asLastModifiedDate", DateUtil.getSqlDate(lsLastModifiedDate));
		}
		else
		{
			loChannel.setData("asLastModifiedDate", null);
		}
		if (lsLastModifiedDate != null)
		{
			loChannel.setData("asSubmissionDate", DateUtil.getSqlDate(lsSubmissionDate));
		}
		else
		{
			loChannel.setData("asSubmissionDate", null);
		}
		loChannel.setData("asLastModifiedBy", lsUserId);
		loChannel.setData("asSubmissionBy", lsUserId);
		loChannel.setData(HHSR5Constants.AS_DOC_TYPE, lsDocType);
		loChannel.setData("asFormName", lsFormName);
		loChannel.setData("asFormVersion", lsFormVersion);
		loChannel.setData("asFormId", lsFormId);
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("asAppId", asAppId);
		loChannel.setData("asDocCategory", lsDocCategory);
		loChannel.setData("asSection", asSectionName);
		loChannel.setData("asDocSatus", ApplicationConstants.COMPLETED_STATE);
		loChannel.setData(P8Constants.AS_DOC_TITLE, lsDocTitle);
		loChannel.setData("asUserId", lsUserId);
		loChannel.setData("asServiceAppId", lsServiceAppId);
		loChannel.setData("asSectionId", lsSectionId);
		loChannel.setData("applicationId", asAppId);
		loChannel.setData(HHSR5Constants.DOC_ID, lsDocId);
		loChannel.setData("documentCategory", lsDocCategory);
		loChannel.setData("documentType", lsDocType);
		loChannel.setData("organizationId", asOrgId);
		loChannel.setData("docName", lsDocTitle);
		loChannel.setData("sectionId", lsSectionId);
		loChannel.setData("asEntityId", lsEntityId);// Defect #1805 fix
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
		loChannel.setData("hmReqProps", loHmDocReqProps);
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannel.setData("aoFilenetSession", loUserSession);
		TransactionManager.executeTransaction(loChannel, "checkForDocId");
		String lsGetdocIdForDocType = null;
		lsGetdocIdForDocType = (String) loChannel.getData("lsGetdocIdForDocType");
		HashMap loHmDocReqProperties = new HashMap();
		loHmDocReqProperties.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		loChannel.setData("asGetdocIdForDocType", lsGetdocIdForDocType);
		loChannel.setData("loHmDocReqProperties", loHmDocReqProperties);
		if (lsGetdocIdForDocType != null)
		{
			TransactionManager.executeTransaction(loChannel, "saveDocumentProperties_bapp");
		}
		if (lsSectionId == null || !lsSectionId.equalsIgnoreCase("servicessummary"))
		{
			// This transaction will update the document table when we select a
			// document from document vault for Application.
			loChannel.setData("transaction_name", "updateDocIdOnRadioSelect");
		}
		else if (lsSectionId.equalsIgnoreCase("servicessummary"))
		{ // This
			// transaction
			// will
			// update
			// the
			// document
			// table
			// when
			// we
			// select
			// a
			// document
			// from
			// document
			// vault
			// for
			// services
			// summary.
			loChannel.setData("transaction_name", "updateDocIdOnRadioSelectServiceSummary");
		}
		LOG_OBJECT.Debug("submitDocId:: End Submit document on radio button select in Business Applcaition Document");
		return loChannel;
	}

	/**
	 * Updated for 3.1.0, enhancement 6021 - Adding Flag to identify Call from
	 * SelectFromVault Overlay This method is used to set the channel when we
	 * select document from vault in the drop down
	 * 
	 * @param asOrgId - organization ID
	 * @param aoRequest - Action Request
	 * @return loChannel
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Channel selectDocFromVault(String asOrgId, ActionRequest aoRequest) throws ApplicationException
	{
		LOG_OBJECT.Debug("selectDocFromVault:: Start Select document from valut in Business Applcaition Document");
		Channel loChannel;
		loChannel = new Channel();
		String lsFolderPath = null;
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		//get pagination size fron cache and set it in channel --- updated in release 5
		String lsObjectsPerPageKey = HHSConstants.ADD_DOCUMENT_FROM_VAULT_COMPONENT_NAME + "_"
				+ ApplicationConstants.APPLICATION_DOCUMENT_VIEW_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		String lsObjectsPerPage = (String) loApplicationSettingMap.get(lsObjectsPerPageKey);
		// This transaction get the list of document from the vault for that
		// particular doc type.
		loChannel.setData("transaction_name", "displayDocList_filenet");
		loChannel.setData("xmlNameForTransaction", HHSR5Constants.TRANSACTION_ELEMENT_R5);
		HashMap<String, String> loReqProps = new HashMap<String, String>();
		// Changed value from "" to "DOC"- Release 5 SelectVault
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_CATEGORY, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_DATE_CREATED, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.DOCUMENT_TITLE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(P8Constants.PROPERTY_CE_DOC_TYPE, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(HHSR5Constants.FOLDERS_FILED_IN, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		loReqProps.put(HHSConstants.TEMPLATE_ID, HHSR5Constants.DOCUMENT_CLASS_ALIAS);
		// End Release 5
		HashMap loHMFilterMap = new HashMap();
		loHMFilterMap.put("providerID", asOrgId);
		// Added for Release 5- Adding Flag to identify Call from
		// SelectFromVault in filterMap
		String lsSelectVault = aoRequest.getParameter(HHSR5Constants.SELECT_VAULT);
		loHMFilterMap.put(HHSR5Constants.SELECT_VAULT, lsSelectVault);
		String lsSelectAll = aoRequest.getParameter(HHSConstants.SELECT_ALL_FLAG);
		loHMFilterMap.put(HHSConstants.SELECT_ALL_FLAG, lsSelectAll);
		String lsFolderId = aoRequest.getParameter(HHSR5Constants.FOLDER_ID);
		// End Release 5
		// Updated for 3.1.0, enhancement 6021 - Adding Flag to identify Call
		// from SelectFromVault Overlay
		loHMFilterMap.put(HHSConstants.SELECT_FROM_VAULT_BUSINESS_APLLICATION_FLAG, ApplicationConstants.TRUE);
		HashMap loHMOrderByMap = new HashMap();
		// Added & commented loHMOrderByMap for Release 5
		// loHMOrderByMap.put(1, P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE +
		// " DESC");
		loHMOrderByMap.put(1, P8Constants.PROPERTY_CE_DOC_TYPE + HHSConstants.SPACE + HHSConstants.ASCENDING
				+ HHSR5Constants.DOC_WITH_COMMA);
		loHMOrderByMap.put(2, P8Constants.PROPERTY_CE_DOCUMENT_TITLE + HHSConstants.SPACE + HHSConstants.ASCENDING
				+ HHSR5Constants.DOC_WITH_COMMA);
		loChannel.setData(ApplicationConstants.DOC_TYPE, aoRequest.getParameter(HHSConstants.DOCTYPE));
		loChannel.setData(ApplicationConstants.REQ_PROPS, loReqProps);

		// changed to false for R5
		loChannel.setData(HHSConstants.INCLUDE_FILENET_FILTER, false);
		loChannel.setData(HHSConstants.FILENET_ORDER_BY_MAP, loHMOrderByMap);
		
		String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
		if (null == lsNextPage)
		{
			FileNetOperationsUtils.reInitializePageIterator(aoRequest.getPortletSession(), loUserSession);
		}
		else
		{
			loUserSession.setNextPageIndex(Integer.valueOf(lsNextPage) - HHSConstants.INT_ONE);
		}
		//FileNetOperationsUtils.reInitializePageIterator(aoRequest.getPortletSession(), loUserSession);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		if ((null == lsFolderId || lsFolderId.equalsIgnoreCase(HHSR5Constants.NULL))
				&& (null != lsSelectAll && !lsSelectAll.isEmpty()))
		{
			lsFolderPath = FileNetOperationsUtils
					.setFolderPath(lsUserOrgType, lsUserOrg, HHSR5Constants.DOCUMENT_VAULT);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.FOLDER_PATH, lsFolderPath);
		}
		FileNetOperationsUtils.setPropFilter(loHMFilterMap, lsFolderPath, lsFolderId, lsUserOrgType, lsUserOrg);
		loChannel.setData("filterMap", loHMFilterMap);
		LOG_OBJECT.Debug("selectDocFromVault:: End Select document from valut in Business Applcaition Document");
		String lsEntityId = aoRequest.getParameter("entityId");// Defect #1805
																// fix
		if (null != lsEntityId && !lsEntityId.isEmpty())
		{
			aoRequest.getPortletSession().setAttribute("entityId", lsEntityId, PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsEntityId = (String) aoRequest.getPortletSession().getAttribute("entityId",
					PortletSession.APPLICATION_SCOPE);
		}
		ApplicationSession.setAttribute(lsEntityId, aoRequest, "entityId");
		// Added for Release 5- selectVault
		String lsSectionId = aoRequest.getParameter("sectionId");
		if (null != lsSectionId && !lsSectionId.isEmpty())
		{
			aoRequest.getPortletSession().setAttribute("sectionId", lsSectionId, PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsSectionId = (String) aoRequest.getPortletSession().getAttribute("sectionId",
					PortletSession.APPLICATION_SCOPE);
		}
		ApplicationSession.setAttribute(lsSectionId, aoRequest, "sectionId");
		String lsServiceAppId = aoRequest.getParameter("serviceAppId");
		if (null != lsServiceAppId && !lsServiceAppId.isEmpty())
		{
			aoRequest.getPortletSession().setAttribute("serviceAppId", lsSectionId, PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsServiceAppId = (String) aoRequest.getPortletSession().getAttribute("serviceAppId",
					PortletSession.APPLICATION_SCOPE);
		}
		ApplicationSession.setAttribute(lsSectionId, aoRequest, "serviceAppId");
		loChannel.setData(HHSConstants.OBJECTS_PER_PAGE, lsObjectsPerPage);
		// End of Release 5
		return loChannel;
	}

	/**
	 * Gets the channel object for render
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Render request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{

		Channel loChannel = null;
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		String lsFormName = aoRequest.getParameter(ApplicationConstants.FORMNAME);
		String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FORM_VERSION);
		String lsTableName = null;
		if (!asSectionName.equalsIgnoreCase("servicessummary"))
		{
			lsTableName = PropertyUtil.getTableName(asSectionName);
		}
		loChannel = new Channel();
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrgId);
		loChannel.setData("reqServiceInfo", loServiceInfoMap);
		loChannel.setData("asapplicationid", asAppId);
		loChannel.setData("asFormName", lsFormName);
		loChannel.setData("asFormVersion", lsFormVersion);
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("asTableName", lsTableName);
		loChannel.setData("asServiceApplicationid",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));

		HashMap<String, String> loReqProps = new HashMap<String, String>();
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, ApplicationConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, ApplicationConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asOrgId);
		loReqProps.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, ApplicationConstants.EMPTY_STRING);
		loReqProps.put(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE, ApplicationConstants.EMPTY_STRING);

		loChannel.setData(ApplicationConstants.DOC_TYPE, null);
		loChannel.setData(ApplicationConstants.REQ_PROPS, loReqProps);
		loChannel.setData("includeFilter", true);
		loChannel.setData("aoFilenetSession", loUserSession);
		Map<String, String> loQuestionMap = new HashMap<String, String>();
		loQuestionMap.put("asOrgId", asOrgId);
		loQuestionMap.put("asAppId", asAppId);
		loQuestionMap.put("asBusinessAppid", asAppId);
		loQuestionMap.put("asServiceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loChannel.setData("aoQuestionDetails", loQuestionMap);
		String lsAppSettingMapKey = ApplicationConstants.APPLICATION_DOCUMENT_VIEW_COMPONENT + "_"
				+ ApplicationConstants.APPLICATION_DOCUMENT_VIEW_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		aoRequest.getPortletSession().setAttribute("allowedObjectCount",
				Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey)), PortletSession.APPLICATION_SCOPE);
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Render Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{
		LOG_OBJECT.Debug("Start render of Business Applcaition Document");
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);

		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.DOCUMENT_LIST_PAGE);

		HashMap loDocResult = (HashMap) aoChannel.getData("documentPropHM");
		List<Document> loDocumentDetails = (List<Document>) aoChannel
				.getData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER);
		String lsOrgId = (String) aoChannel.getData("asOrgId");
		PortletSession loPortletSession = aoRequest.getPortletSession();
		String lsOrgnizationType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (loDocumentDetails != null)
		{
			Iterator<Document> loItrDOcument = loDocumentDetails.iterator();
			while (loItrDOcument.hasNext())
			{
				Document loDocObject = loItrDOcument.next();
				if (!loDocObject.isMbIsCeo())
				{
					loMapForRender.put("message", PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M22"));
					loMapForRender.put("messageType", ApplicationConstants.MESSAGE_FAIL_TYPE);
				}
				loDocObject.setMsOrgType(lsOrgnizationType);
			}
		}
		if (loDocResult != null)
		{
			Iterator<Document> loItrDOcument = loDocumentDetails.iterator();
			while (loItrDOcument.hasNext())
			{
				Document loDocumentsObject = loItrDOcument.next();
				loDocumentsObject.setUserOrg(lsOrgId);
				String lsDocumentID = loDocumentsObject.getDocumentId();

				if (loDocResult.containsKey(lsDocumentID))
				{
					HashMap loHMDocInfo = (HashMap) loDocResult.get(lsDocumentID);
					if (loHMDocInfo != null)
					{
						String lsProviderName = (String) loHMDocInfo.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY);
						loDocumentsObject.setDocName((String) loHMDocInfo.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
						loDocumentsObject.setDate(DateUtil.getDateMMddYYYYFormat((java.util.Date) loHMDocInfo
								.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE)));
						loDocumentsObject.setLastModifiedBy(lsProviderName);
					}
					else
					{
						loDocumentsObject.setDocName("doc id is present in document table but not in filenet");
						loDocumentsObject.setDate(null);
						loDocumentsObject.setLastModifiedBy(null);
					}
				}

			}
		}

		loMapForRender.put("taskItemList", loDocumentDetails);
		LOG_OBJECT.Debug("End render of Business Applcaition Document");
		return loMapForRender;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{

		return null;
	}
}
