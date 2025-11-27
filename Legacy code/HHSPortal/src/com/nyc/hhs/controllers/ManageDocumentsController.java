package com.nyc.hhs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;

/**
 * ManageDocumentsController is used to control the flow of shared documents
 * screen for a provider/agency/accelerator. It makes filenet calls to obtain
 * shared documents for an Organization and also provides filter on those
 * documents.
 * 
 */

public class ManageDocumentsController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ManageDocumentsController.class);

	/**
	 * 
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp. <li>This method
	 * was updated in R4</li>
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView
	 * @throws ApplicationException
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		ModelAndView loModelAndView = null;
		String lsAction = null;
		String lsNextPageReqParam = null;
		String lsSortByReqParam = null;
		String lsSortTypeReqParam = null;
		String lsParentNodeReqParam = null;
		try
		{
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsHomeJSPName = "";
			PortletSession loPortletSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			lsAction = PortalUtil.parseQueryString(aoRequest, "return_action");
			if (null == lsAction || lsAction.isEmpty())
			{
				lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			}

			String lsOwnerProvider = (String) loPortletSession.getAttribute("cityUserSearchProviderId",
					PortletSession.APPLICATION_SCOPE);
			// R4 Hompage changes: Since Provider/Agency can view documents
			// shared by providers, agency and city users so the following
			// condition
			// sets DOC_ORIGINATOR in request with the value which identifies
			// whether to render agency, provider or city shared documents with
			// the user.
			if (null != lsOwnerProvider && lsOwnerProvider.contains(ApplicationConstants.TILD))
			{
				String lsDocOriginator = (lsOwnerProvider.split(ApplicationConstants.TILD))[1];
				lsOwnerProvider = (lsOwnerProvider.split(ApplicationConstants.TILD))[0];
				aoRequest.setAttribute(ApplicationConstants.DOC_ORIGINATOR, lsDocOriginator);
			}
			else if (null != PortalUtil.parseQueryString(aoRequest, "documentOriginator")
					&& (PortalUtil.parseQueryString(aoRequest, "documentOriginator").equals(
							ApplicationConstants.AGENCY_ORG) || PortalUtil.parseQueryString(aoRequest,
							"documentOriginator").equals(ApplicationConstants.CITY_ORG)))
			{
				aoRequest.setAttribute(ApplicationConstants.DOC_ORIGINATOR, ApplicationConstants.AGENCY_ORG);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.DOC_ORIGINATOR, ApplicationConstants.PROVIDER_ORG);
			}

			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			// R4 Hompage Changes: Agency name is fetched from cache
			// corresponding to the agency id and is set in the portlet session
			// which is
			// displayed on screen S101 – Organization Documents of agency when
			// the user access document vault from the Home Sreen.
			String loOrgName = FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), lsOwnerProvider);
			if (null != loOrgName && !loOrgName.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loPortletSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY, loOrgName,
						PortletSession.APPLICATION_SCOPE);
			}
			if (aoRequest.getParameter(ApplicationConstants.RENDER_ACTION) != null
					&& !aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).equals(ApplicationConstants.ERROR))
			{
				LOG_OBJECT.Debug("Internal Error occured in OrganizationQuestionsController Action ");
				loModelAndView = new ModelAndView("errorpage");
				return loModelAndView;
			}
			String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

			if (lsOwnerProvider == null && PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId") != null
					&& lsAction.equals("open"))
			{
				lsOwnerProvider = PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId");
				loPortletSession.setAttribute("cityUserSearchProviderId",
						PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId"),
						PortletSession.APPLICATION_SCOPE);
				Map<String, String> loMProvider = null;
				if (lsOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					loMProvider = getSharedAgencyProviderList(loUserSession, P8Constants.PROPERTY_CE_SHARED_AGENCY_ID,
							lsOrgId, aoRequest);
				}
				else
				{
					loMProvider = getSharedAgencyProviderList(loUserSession,
							P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, lsOrgId, aoRequest);
				}
				if (loMProvider != null && !loMProvider.isEmpty())
				{// setting provider name to session
					String lsProviderName = null;
					if (null != PortalUtil.parseQueryString(aoRequest, "documentOriginator")
							&& PortalUtil.parseQueryString(aoRequest, "documentOriginator").equalsIgnoreCase(
									ApplicationConstants.AGENCY_ORG))

					{
						lsProviderName = FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb
								.getInstance().getCacheObject(ApplicationConstants.AGENCY_LIST), PortalUtil
								.parseQueryString(aoRequest, "cityUserSearchProviderId"));
						ApplicationSession.setAttribute(lsProviderName, aoRequest, "providerNameForSharedDoc");
					}
					else if (null != PortalUtil.parseQueryString(aoRequest, "documentOriginator")
							&& PortalUtil.parseQueryString(aoRequest, "documentOriginator").equalsIgnoreCase(
									ApplicationConstants.PROVIDER_ORG))
					{
						lsProviderName = FileNetOperationsUtils.getProviderName((List) BaseCacheManagerWeb
								.getInstance().getCacheObject(ApplicationConstants.PROV_LIST), PortalUtil
								.parseQueryString(aoRequest, "cityUserSearchProviderId"));
						ApplicationSession.setAttribute(lsProviderName, aoRequest, "providerNameForSharedDoc");
					}
					else
					{
						ApplicationSession.setAttribute(ApplicationConstants.CITY_USER_NAME, aoRequest,
								"providerNameForSharedDoc");
					}
					ApplicationSession.setAttribute(loMProvider, aoRequest, "sharedDocForProvider");
				}
				ApplicationSession.setAttribute("true", aoRequest, "fromNotification");
			}
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);

			lsNextPageReqParam = (String) loPortletSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER, PortletSession.APPLICATION_SCOPE);

			lsSortByReqParam = (String) loPortletSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER, PortletSession.APPLICATION_SCOPE);
			lsSortTypeReqParam = (String) loPortletSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER, PortletSession.APPLICATION_SCOPE);
			lsParentNodeReqParam = (String) loPortletSession.getAttribute(
					ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER, PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute("subsection", lsSubSectionName);
			aoRequest.setAttribute("section", lsSectionName);
			aoRequest.setAttribute("documentOriginator", PortalUtil.parseQueryString(aoRequest, "documentOriginator"));
			aoRequest.setAttribute("cityUserSearchProviderId", lsOwnerProvider);
			Map<String, Object> loMapForRender = new HashMap<String, Object>();
			loMapForRender.put("providerId", lsOrgId);
			loMapForRender.put("ownerProviderId", lsOwnerProvider);
			String lsActionReqParam = aoRequest.getParameter(ApplicationConstants.ACTION);
			String lsParamAction = PortalUtil.parseQueryString(aoRequest, "action");
			if (null == lsActionReqParam)
			{
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ACTION);
			}
			List<Document> loDocumentBeanList = new ArrayList<Document>();
			Document loDocument = new Document();

			lsHomeJSPName = "shareDocheader";
			// This block renders the list of documents shared with a provider
			// by
			// making a call on filenet
			if (null != lsAction && lsAction.equalsIgnoreCase("open"))
			{
				FileNetOperationsUtils.reInitializePageIterator(loPortletSession, loUserSession);

				// Start of change for defect id : 6381
				lsSortByReqParam = (String) loPortletSession.getAttribute(
						ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER, PortletSession.APPLICATION_SCOPE);
				lsSortTypeReqParam = (String) loPortletSession.getAttribute(
						ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER, PortletSession.APPLICATION_SCOPE);
				lsParentNodeReqParam = (String) loPortletSession.getAttribute(
						ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER, PortletSession.APPLICATION_SCOPE);
				lsNextPageReqParam = (String) loPortletSession
						.getAttribute(ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER,
								PortletSession.APPLICATION_SCOPE);
				// End of change for defect id : 6381

				doShowOpen(aoRequest, loPortletSession, loUserSession, lsOwnerProvider, lsOrgId, lsOrgType,
						lsNextPageReqParam, lsSortByReqParam, lsSortTypeReqParam, lsParentNodeReqParam, loMapForRender,
						lsParamAction, loDocumentBeanList, loDocument);
			}
			else if (null != lsAction && lsAction.equalsIgnoreCase("openProviderView"))
			{
				// R4 Homepage Changes: Additional parameter is passed which
				// identifies whether to open the provider or agency view.
				doShowOpenProviderView(aoRequest, loPortletSession, loUserSession, lsOwnerProvider, lsOrgType,
						lsNextPageReqParam, lsSortByReqParam, lsSortTypeReqParam, lsParentNodeReqParam, loMapForRender,
						loDocumentBeanList, loDocument,
						(String) aoRequest.getAttribute(ApplicationConstants.DOC_ORIGINATOR), lsOrgId);
			}
			else if (null != lsAction && lsAction.equalsIgnoreCase("pagination"))
			{
				doShowOpen(aoRequest, loPortletSession, loUserSession, lsOwnerProvider, lsOrgId, lsOrgType,
						lsNextPageReqParam, lsSortByReqParam, lsSortTypeReqParam, lsParentNodeReqParam, loMapForRender,
						lsParamAction, loDocumentBeanList, loDocument);
				aoRequest.setAttribute("cityUserSearchProviderId", aoRequest.getParameter("cityUserSearchProviderId"));
			}

			// This block renders the changed document list on applying a filter
			// on
			// screen
			else if (null != lsAction && lsAction.equalsIgnoreCase(ApplicationConstants.FILTER_DOCUMENTS))
			{
				doShowFilterDoc(aoRequest, loPortletSession, loUserSession, lsOrgId, lsOrgType, loMapForRender,
						lsParamAction);

			}
			// This block renders the document information screen for the
			// selected
			// document
			else if (null != lsAction && lsAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
			{
				aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP,
						aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
				aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
						(Document) ApplicationSession.getAttribute(aoRequest, true,
								ApplicationConstants.SESSION_DOCUMENT_OBJ));
				aoRequest.setAttribute(ApplicationConstants.IS_LOCKED_STATUS,
						aoRequest.getParameter(ApplicationConstants.IS_LOCKED_STATUS));
				aoRequest.setAttribute("cityUserSearchProviderId", aoRequest.getParameter("cityUserSearchProviderId"));
				loMapForRender.put("isViewDocInfoOrg", "true");
				FileNetOperationsUtils.reInitializePageIterator(loPortletSession, loUserSession);
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/portlet/application/documentvault/viewdocumentinfo.jsp");
				aoRequest.setAttribute("cityToProvider", true);
			}
			aoRequest.setAttribute("cityUserSearchProviderId",
					PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId"));
			FileNetOperationsUtils.setReqRequestParameter(loPortletSession, aoRequest, loUserSession,
					lsNextPageReqParam, lsSortByReqParam, lsSortTypeReqParam, lsParentNodeReqParam);
			loMapForRender.put("lsAction", "openProviderView");
			loModelAndView = new ModelAndView(lsHomeJSPName, loMapForRender);
			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in OrganizationQuestionsController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in ManageDocumentsController ", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method shows filter documents.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param aoUserSession - P8UserSession
	 * @param asOrgId - organization id
	 * @param asOrgType - organization type
	 * @param aoMapForRender - map containing values required for the jsp
	 * @param asParamAction - action to be performed
	 * @throws ApplicationException
	 */
	private void doShowFilterDoc(RenderRequest aoRequest, PortletSession aoPortletSession, P8UserSession aoUserSession,
			String asOrgId, String asOrgType, Map<String, Object> aoMapForRender, String asParamAction)
			throws ApplicationException
	{
		if (null != aoRequest.getParameter(ApplicationConstants.SHARED_FLAG))
		{
			aoRequest.setAttribute(ApplicationConstants.SHARED_FLAG,
					aoRequest.getParameter(ApplicationConstants.SHARED_FLAG));
		}
		Document loDocObject = (Document) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocObject);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER,
				ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_LIST));
		aoRequest.setAttribute(ApplicationConstants.PROVIDER_SET, FileNetOperationsUtils.getSharedAgencyProviderList(
				(List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST),
				aoUserSession, P8Constants.PROPERTY_CE_SHARED_PROVIDER_ID, asOrgId));
		aoRequest.setAttribute(ApplicationConstants.AGENCY_SET, FileNetOperationsUtils.getSharedAgencyProviderList(
				(List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST),
				aoUserSession, P8Constants.PROPERTY_CE_SHARED_AGENCY_ID, asOrgId));
		if ((null != loDocObject)
				&& ((null != loDocObject.getFilterDocCategory() && !loDocObject.getFilterDocCategory().equals(""))
						|| (null != loDocObject.getFilterDocType() && !loDocObject.getFilterDocType().equals(""))
						|| (null != loDocObject.getFilterModifiedFrom() && !loDocObject.getFilterModifiedFrom().equals(
								"")) || (null != loDocObject.getFilterModifiedTo() && !loDocObject
						.getFilterModifiedTo().equals(""))))
		{
			aoRequest.setAttribute("filterStatus", "filtered");
		}
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/portlet/application/documentvault/sharedDocumentList.jsp");
	}

	/**
	 * This method shows open provider view
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param aoUserSession - P8UserSession
	 * @param asOwnerProvider - owner provider
	 * @param asOrgType - organization type
	 * @param asNextPageReqParam - Next Page Req Param
	 * @param asSortByReqParam - Sort By Req Param
	 * @param asSortTypeReqParam - Sort Type Req Param
	 * @param asParentNodeReqParam - Parent Node Req Param
	 * @param aoMapForRender - map containing values required for the jsp
	 * @param aoDocumentBeanList - Document Bean List
	 * @param aoDocument - Document
	 * @param aoDocOriginator - identifier for provider or agency user
	 * @throws ApplicationException
	 */
	private void doShowOpenProviderView(RenderRequest aoRequest, PortletSession aoPortletSession,
			P8UserSession aoUserSession, String asOwnerProvider, String asOrgType, String asNextPageReqParam,
			String asSortByReqParam, String asSortTypeReqParam, String asParentNodeReqParam,
			Map<String, Object> aoMapForRender, List<Document> aoDocumentBeanList, Document aoDocument,
			String aoDocOriginator, String asOrgId) throws ApplicationException
	{
		ArrayList loCategoryList;
		FileNetOperationsUtils.setReqRequestParameter(aoPortletSession, aoRequest, aoUserSession, asNextPageReqParam,
				asSortByReqParam, asSortTypeReqParam, asParentNodeReqParam);
		List lsDocumentList = FileNetOperationsUtils.getProviderViewDocumentList(aoUserSession, asOwnerProvider,
				asOrgType);
		FileNetOperationsUtils.generateDocumentBean(lsDocumentList, aoDocumentBeanList,
				ApplicationConstants.PROVIDER_ORG, aoUserSession, null, asOrgId);

		loCategoryList = null;
		// R4 Homepage Change
		loCategoryList = (ArrayList) FileNetOperationsUtils.getDocCategoryList(aoDocOriginator);
		loCategoryList.add(0, "");
		aoDocument.setCategoryList(loCategoryList);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, aoDocument);
		aoMapForRender.put(ApplicationConstants.SESSION_DOCUMENT_LIST, aoDocumentBeanList);
		/*
		 * Added code for Page size
		 */
		String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		aoPortletSession.setAttribute("allowedObjectCount",
				Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey)), PortletSession.APPLICATION_SCOPE);
		// End code for Page size
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/portlet/application/documentvault/sharedDocumentList.jsp");
		aoPortletSession.setAttribute("ownerProviderId", asOwnerProvider);
		aoRequest.setAttribute("action", "documentVault");
	}

	/**
	 * This method shows open provider view
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param aoUserSession - P8UserSession
	 * @param asOwnerProvider - owner provider
	 * @param asOrgId - organization id
	 * @param asOrgType - organization type
	 * @param asNextPageReqParam - Next Page Req Param
	 * @param asSortByReqParam - Sort By Req Param
	 * @param asSortTypeReqParam - Sort Type Req Param
	 * @param asParentNodeReqParam - Parent Node Req Param
	 * @param aoMapForRender - map containing values required for the jsp
	 * @param asParamAction - Param Action
	 * @param aoDocumentBeanList - Document Bean List
	 * @param aoDocument - Document
	 * @throws ApplicationException
	 */
	private void doShowOpen(RenderRequest aoRequest, PortletSession aoPortletSession, P8UserSession aoUserSession,
			String asOwnerProvider, String asOrgId, String asOrgType, String asNextPageReqParam,
			String asSortByReqParam, String asSortTypeReqParam, String asParentNodeReqParam,
			Map<String, Object> aoMapForRender, String asParamAction, List<Document> aoDocumentBeanList,
			Document aoDocument) throws ApplicationException
	{
		ArrayList loCategoryList;
		FileNetOperationsUtils.setReqRequestParameter(aoPortletSession, aoRequest, aoUserSession, asNextPageReqParam,
				asSortByReqParam, asSortTypeReqParam, asParentNodeReqParam);

		if (asParamAction != null && asParamAction.equalsIgnoreCase("documentVault")
				&& asOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
		{
			Channel loChannel = new Channel();
			loChannel.setData("aoFilenetSession", aoUserSession);
			loChannel.setData("findOrgFlag", false);
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			loFilterProps.put(P8Constants.PROPERTY_CE_PROVIDER_ID, asOwnerProvider.trim());
			loFilterProps.put(P8Constants.PROPERTY_CE_IS_CURRENT_VERSION, Boolean.TRUE);
			FileNetOperationsUtils.setOrderByParameter(loChannel, null, null);

			List lsDocumentList = FileNetOperationsUtils.getDocumentList(loChannel, null,
					FileNetOperationsUtils.requiredDocsProps(asOrgType, null), loFilterProps, true, null);
			FileNetOperationsUtils.generateDocumentBean(lsDocumentList, aoDocumentBeanList,
					ApplicationConstants.PROVIDER_ORG, aoUserSession, null, asOrgId);
		}
		else if (asOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
		{
			List lsDocumentList = FileNetOperationsUtils.getSharedDocumentList(aoUserSession, asOrgId, false,
					asOwnerProvider, asOrgType);
			FileNetOperationsUtils.generateDocumentBean(lsDocumentList, aoDocumentBeanList, asOrgType, aoUserSession,
					null, asOrgId);

		}
		else
		{
			// For Provide to Provider
			List lsDocumentList = FileNetOperationsUtils.getSharedDocumentList(aoUserSession, asOrgId, true,
					asOwnerProvider, asOrgType);
			FileNetOperationsUtils.generateDocumentBean(lsDocumentList, aoDocumentBeanList, asOrgId, aoUserSession,
					null, asOrgId);
		}
		// R4 Homepage change
		if (null != PortalUtil.parseQueryString(aoRequest, "documentOriginator")
				&& !PortalUtil.parseQueryString(aoRequest, "documentOriginator").isEmpty())
		{
			loCategoryList = (ArrayList) FileNetOperationsUtils.getDocCategoryList(PortalUtil.parseQueryString(
					aoRequest, "documentOriginator"));
		}
		else
		{
			loCategoryList = (ArrayList) FileNetOperationsUtils.getDocCategoryList((String) aoRequest
					.getAttribute(ApplicationConstants.DOC_ORIGINATOR));
		}
		loCategoryList.add(0, "");
		aoDocument.setCategoryList(loCategoryList);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, aoDocument);
		aoMapForRender.put(ApplicationConstants.SESSION_DOCUMENT_LIST, aoDocumentBeanList);
		/*
		 * Added code for Page size
		 */
		String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_"
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		aoPortletSession.setAttribute("allowedObjectCount",
				Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey)), PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/portlet/application/documentvault/sharedDocumentList.jsp");
	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction.
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @throws ApplicationException
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		try
		{
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			// R4 Homepage changes
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsOwnerProvider = (String) loPortletSession.getAttribute("cityUserSearchProviderId",
					PortletSession.APPLICATION_SCOPE);
			String lsDocOriginator = null;
			if (null != lsOwnerProvider && lsOwnerProvider.contains(ApplicationConstants.TILD))
			{
				lsDocOriginator = (lsOwnerProvider.split(ApplicationConstants.TILD))[1];
			}
			else if (null == lsDocOriginator && null != aoRequest.getParameter("documentOriginator")
					&& !aoRequest.getParameter("documentOriginator").isEmpty())
			{
				lsDocOriginator = aoRequest.getParameter("documentOriginator");
			}
			// if originator also not available than this request is for
			// provider type
			else if (null == lsDocOriginator || lsDocOriginator.isEmpty())
			{
				lsDocOriginator = ApplicationConstants.PROVIDER_ORG;
			}
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			aoRequest.setAttribute("subsection", lsSubSectionName);
			aoRequest.setAttribute("section", lsSectionName);
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			PortletSession loSession = aoRequest.getPortletSession();
			String lsNextAction = aoRequest.getParameter(ApplicationConstants.DOCUMENT_VAULT_NEXT_ACTION_PARAMETER);
			String lsNextPageReqParam = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_NEXT_PAGE_VALUE_PARAMETER);
			String lsSortByReqParam = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.DOCUMENT_VAULT_SORT_BY_PARAMETER);
			String lsSortTypeReqParam = aoRequest.getParameter(ApplicationConstants.DOCUMENT_VAULT_SORT_TYPE_PARAMETER);
			String lsParentNodeReqParam = aoRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_PARENT_NODE_PARAMETER);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

			if (null != lsNextPageReqParam)
			{
				FileNetOperationsUtils.setReqSessionParameter(loSession, lsNextPageReqParam, lsSortByReqParam,
						lsSortTypeReqParam, lsParentNodeReqParam);
				aoResponse.setRenderParameter("next_action", "pagination");
			}

			// Start of change for defect id : 6381
			else if (null != lsSortByReqParam && !lsSortByReqParam.equalsIgnoreCase("/"))
			{
				FileNetOperationsUtils.reInitializePageIterator(loSession, loUserSession);
			}
			// End of change for defect id : 6381

			// This block controls the operations of filter, it generates a list
			// of
			// documents specific to the applied filter
			if (null != lsAction && lsAction.equals(ApplicationConstants.FILTER_DOCUMENT))
			{
				if (lsOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
				{
					aoRequest.setAttribute("SharedReqType", "Agency");
				}
				else if (lsOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					aoRequest.setAttribute("SharedReqType", "City");
				}
				else
				{
					aoRequest.setAttribute("SharedReqType", "Provider");
				}
				aoRequest.setAttribute(ApplicationConstants.PROVIDER, (String) aoRequest.getPortletSession()
						.getAttribute("cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE));
				aoRequest.setAttribute(
						"sharedSearchDocument",
						(String) aoRequest.getPortletSession().getAttribute("cityUserSearchProviderId",
								PortletSession.APPLICATION_SCOPE));
				FileNetOperationsUtils.actionFilterDocument(aoRequest, aoResponse);
				aoResponse.setRenderParameter("next_action", ApplicationConstants.FILTER_DOCUMENT);

			}
			// This block gets the information of the selected document from
			// filenet
			else if (null != lsAction && lsNextAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
			{
				FileNetOperationsUtils.viewDocumentInformation(aoRequest, aoResponse, lsDocOriginator);
				aoResponse.setRenderParameter("next_action", ApplicationConstants.VIEW_DOCUMENT_INFO);

			}

			aoResponse.setRenderParameter("action", "documentVault");
			aoResponse.setRenderParameter("cityUserSearchProviderId",
					PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId"));
			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in ManageDocumentsController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in ManageDocumentsController ", aoEx);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while execution of action Method in BusinessSummaryController", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method is used to get the shared document provider list
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoUserSession user session
	 * @param asAgencyType agency type
	 * @param asProviderId provider id
	 * @param aoRequest request object
	 * @return map
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, String> getSharedAgencyProviderList(P8UserSession aoUserSession, String asAgencyType,
			String asProviderId, RenderRequest aoRequest) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asAgencyType", asAgencyType);
		loChannel.setData("asProviderId", asProviderId);
		TransactionManager.executeTransaction(loChannel, "getSharedDocumentsOwnerList_filenet");
		TreeSet loTemploProviderSet = (TreeSet) loChannel.getData("providerList");
		List<ProviderBean> loProviderList = null;
		// First checking provided list in session, if not found then search the
		// DB
		if (aoRequest.getPortletSession().getAttribute("provList", PortletSession.APPLICATION_SCOPE) != null)
		{
			loProviderList = (List) aoRequest.getPortletSession().getAttribute("provList",
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			loProviderList = FileNetOperationsUtils.getProviderList();
		}
		// R4 Homepage changes: fetching agency list from cache.
		TreeSet<String> loAgencyList = (TreeSet<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.AGENCY_LIST);
		if (null == loAgencyList || loAgencyList.isEmpty())
		{
			loAgencyList = FileNetOperationsUtils.getNYCAgencyListFromDB();
		}
		Iterator loItr = loAgencyList.iterator();
		while (loItr.hasNext())
		{
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

	/**
	 * This method is used to sort the map
	 * 
	 * @param aoProviderList provider list
	 * @param aoProviderSet provider set
	 * @return sorted map
	 */
	private Map<String, String> convertToMap(List<ProviderBean> aoProviderList, TreeSet aoProviderSet)
	{
		Map<String, String> loProviderMap = new HashMap<String, String>();
		if (aoProviderList != null)
		{
			Iterator<ProviderBean> loItrProvider = aoProviderList.iterator();
			while (loItrProvider.hasNext())
			{
				ProviderBean loProvider = loItrProvider.next();
				loProviderMap.put(loProvider.getHiddenValue(), loProvider.getDisplayValue());
			}
		}
		Iterator loSelectedProvider = aoProviderSet.iterator();
		Map<String, String> loProviderToDisplay = new HashMap<String, String>();
		while (loSelectedProvider.hasNext())
		{
			String lsProviderId = (String) loSelectedProvider.next();
			if (loProviderMap.containsKey(lsProviderId))
			{
				loProviderToDisplay.put(lsProviderId, loProviderMap.get(lsProviderId));
			}
		}
		return loProviderToDisplay;
	}

	/**
	 * This method is used to sort the map
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoProviderList provider list
	 * @param aoAgencyList agency list
	 * @param aoProviderSet provider set
	 * @return sorted map Updated Method in R4
	 */
	private Map<String, String> convertToMap(List<ProviderBean> aoProviderList, TreeSet<String> aoAgencyList,
			TreeSet aoProviderSet)
	{
		Map<String, String> loProviderMap = new HashMap<String, String>();
		if (aoProviderList != null)
		{
			Iterator<ProviderBean> loItrProvider = aoProviderList.iterator();
			while (loItrProvider.hasNext())
			{
				ProviderBean loProvider = loItrProvider.next();
				loProviderMap.put(loProvider.getHiddenValue(), loProvider.getDisplayValue());
			}
		}
		// R4 homepage changes
		Map<String, String> loAgencyMap = new HashMap<String, String>();
		if (aoAgencyList != null)
		{
			Iterator loIterator = aoAgencyList.iterator();
			while (loIterator.hasNext())
			{
				String lsAgency = (String) loIterator.next();
				String[] loAgencyName = lsAgency.split(ApplicationConstants.TILD);
				loAgencyMap.put(loAgencyName[0], loAgencyName[1]);
			}
		}
		Iterator loSelectedProvider = aoProviderSet.iterator();
		Map<String, String> loProviderToDisplay = new HashMap<String, String>();
		while (loSelectedProvider.hasNext())
		{
			String lsProviderId = (String) loSelectedProvider.next();
			if (loProviderMap.containsKey(lsProviderId))
			{
				loProviderToDisplay.put(lsProviderId.concat((ApplicationConstants.TILD_PROVIDER)),
						StringEscapeUtils.unescapeJavaScript(loProviderMap.get(lsProviderId)));
			}
			if (loAgencyMap.containsKey(lsProviderId))
			{
				loProviderToDisplay.put(lsProviderId.concat((ApplicationConstants.TILD_AGENCY)),
						StringEscapeUtils.unescapeJavaScript(loAgencyMap.get(lsProviderId)));
			}
			if (lsProviderId.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{
				loProviderToDisplay.put(ApplicationConstants.TILD_CITY, ApplicationConstants.CITY_USER_NAME);
			}
		}
		// Sort Map<String, String> loProviderToDisplay; Defect Fix #2554
		Map<String, String> loSortProviderMapToDisplay = new LinkedHashMap<String, String>();
		List<String> loKeyList = new ArrayList<String>(loProviderToDisplay.keySet());
		List<String> loValueList = new ArrayList<String>(loProviderToDisplay.values());
		Set<String> loSortedSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);// Additional
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
		for (int loMapCounter = 0; loMapCounter < loSortedArray.length; loMapCounter++)
		{
			loSortProviderMapToDisplay.put(loKeyList.get(loValueList.indexOf(loSortedArray[loMapCounter])),
					(String) loSortedArray[loMapCounter]);
		}
		return loSortProviderMapToDisplay;
	}
}
