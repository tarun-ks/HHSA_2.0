package com.nyc.hhs.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.security.auth.login.LoginException;

import org.jdom.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.bea.p13n.security.Authentication;
import com.filenet.api.util.Id;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.actions.BusinessApplication;
import com.nyc.hhs.controllers.actions.BusinessApplicationFactory;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.DocumentsSelFromDocVault;
import com.nyc.hhs.model.NYCAgency;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.RFPReleaseDocsUtil;
import com.nyc.hhs.webservice.restful.NYCIDWebServices;

/**
 * Service Application controller for Pre-Submission, Returned for
 * Revisions/Deferred state and other state. Controller works for service
 * questions, its related documents, specialization and service settings and
 * edit funder/staff to edit an existing funder/staff associated to a Service
 * application.
 * 
 */
@Controller(value = "ServiceApplication")
@RequestMapping("view")
public class ApplicationServiceController implements ResourceAwareController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(ApplicationServiceController.class);

	/**
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoResponseLogInfo - RenderResponse
	 * @return loModelAndView
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{   
		
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsJSPName = "home";
		Map<String, Object> loMapToRender = new HashMap<String, Object>();
		ModelAndView loModelAndView = null;
		try
		{   
			final String lsOrgIdFromCity = PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId");
			String lsFromServiceSummary = aoRequest.getPreferences().getValue(ApplicationConstants.AFTER_SUBMITION,
					"false");
			String lsHeaderAfterSubmission = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.POST_HEADER_SERVICE_APPLICATION);
			if (aoRequest.getParameter(ApplicationConstants.RENDER_ACTION) != null
					&& !aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).equals(ApplicationConstants.ERROR))
			{
				LOG_OBJECT.Debug("Internal Error occured in ApplicationServiceController action");
				loModelAndView = new ModelAndView("errorpage");
				return loModelAndView;
			}
			if (lsHeaderAfterSubmission != null && lsHeaderAfterSubmission.equalsIgnoreCase("true"))
			{
				aoRequest.getPortletSession().setAttribute("headerPostService", "true",
						PortletSession.APPLICATION_SCOPE);
			}
			String lsAddNewService = PortalUtil.parseQueryString(aoRequest, "createNewServiceApp");
			if (lsAddNewService != null && lsAddNewService.equalsIgnoreCase("true"))
			{
				aoRequest.getPortletSession().setAttribute("addNewService", "true", PortletSession.APPLICATION_SCOPE);
			}
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			aoRequest.removeAttribute(ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsOrgId = getOrgId(aoRequest, lsOrgIdFromCity, loPortletSession);
			String lsTempStr = aoRequest.getParameter("saveStaff");
			if (lsTempStr != null && lsTempStr.equalsIgnoreCase("saveStaff"))
			{
				aoRequest.setAttribute("saveStaff", "saveStaff");
			}
			setApplicationType(aoRequest, loMapToRender, loPortletSession);
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsBusnessAppId = getBusinessAppId(aoRequest, loPortletSession);
			aoRequest.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			loPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId,
					PortletSession.APPLICATION_SCOPE);
			final String loIsDisplay = PortalUtil.parseQueryString(aoRequest, "loIsDisplay");
			String lsAppStatus = getAppStatus(aoRequest, loPortletSession);
			String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID);
			aoRequest.setAttribute(ApplicationConstants.SERVICE_APPLICATION_ID, lsServiceAppId);
			String lsAppId = getAppId(aoRequest, loPortletSession);
			if (lsServiceAppId == null || lsServiceAppId.equalsIgnoreCase(""))
			{
				lsServiceAppId = (String) loPortletSession.getAttribute(ApplicationConstants.SERVICE_APPLICATION_ID);
			}
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lbWithdrawalVisibleFlag = "Invisible";
			if (lsServiceAppId != null && lsAction != null
					&& lsAction.equalsIgnoreCase("displayServiceApplicationHistory"))
			{
				lbWithdrawalVisibleFlag = doSelectWithdrawl(lsBusnessAppId, lsOrgId, lsUserId, lsAppStatus,
						lsServiceAppId, lsUserRole, lbWithdrawalVisibleFlag);
			}
			setValuesInPortletSession(loPortletSession, lsBusnessAppId, loIsDisplay, lsAppStatus, lsServiceAppId,
					lsAppId);
			if (lsBusnessAppId != null && !lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL))
			{
				doGetApplicationStatus(lsAppStatus, lsBusnessAppId, lsOrgId, lsUserId, lsServiceAppId, loMapToRender,
						aoRequest, lsFromServiceSummary);
			}
			Map<String, StatusBean> loServicesStatusBeanSectionMap = (Map<String, StatusBean>) loMapToRender
					.get("loServicesStatusBeanMap");
			StatusBean lsStatusBean = null;
			String lsSectionStatus = ApplicationConstants.NOT_STARTED_STATE;
			if (loServicesStatusBeanSectionMap != null && loServicesStatusBeanSectionMap.get(lsServiceAppId) != null
					&& !loServicesStatusBeanSectionMap.isEmpty())
			{
				lsStatusBean = (StatusBean) loServicesStatusBeanSectionMap.get(lsServiceAppId);
				lsSectionStatus = lsStatusBean.getMsSectionStatusToDisplay();
			}
			if (ApplicationSession.getAttribute(aoRequest, true, "serviceComments") != null)
			{
				loMapToRender.put("serviceComments",
						(List<Map<String, Object>>) ApplicationSession.getAttribute(aoRequest, "serviceComments"));
			}
			// R5 code start
			String lsPermissionType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
			Boolean loReadOnly = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsAppStatus, null,
					lsServiceAppId, "service", lsOrgnizationType, lsPermissionType);
			Boolean loReadOnlySection = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsSectionStatus,
					lsAppStatus, lsServiceAppId, lsSectionName, lsOrgnizationType, lsPermissionType);
			if (lsPermissionType != null && (lsPermissionType.equalsIgnoreCase("R"))
					&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
			{
				aoRequest.setAttribute("servicesReadOnlyUser", true);
				aoRequest.setAttribute("bappReadOnlyUser", "true");
			}
			// R5 code ends
			setServiceName(aoRequest, loPortletSession);
			if (lsAction != null
					&& lsAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE_NEXT))
			{
				String lsNoFunderStaff = (String) ApplicationSession.getAttribute(aoRequest, "noFunderStaff");
				if (lsNoFunderStaff != null && !lsNoFunderStaff.equalsIgnoreCase("no") && lsSubSectionName != null
						&& lsSubSectionName.equalsIgnoreCase("questions"))
				{
					lsAction = "showServiceQuestion";
				}
				else
				{
					lsSubSectionName = PropertyUtil.getServiceName(lsSectionName, lsSubSectionName, "nextaction");
					lsAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
				}
			}
			else if (lsAction != null
					&& lsAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
			{
				lsSubSectionName = PropertyUtil.getServiceName(lsSectionName, lsSubSectionName, "previousaction");
				lsAction = getActionName(lsSubSectionName);
			}
			aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SECTION, lsSectionName);
			aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION, lsSubSectionName);
			LOG_OBJECT.Debug("12 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			BusinessApplication loBrAppObj = BusinessApplicationFactory.getBusinessApplication(lsSectionName,
					lsSubSectionName);
			if (lsSubSectionName != null
					&& (lsSubSectionName.equalsIgnoreCase("summary")
							|| lsSubSectionName.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)
							|| lsSubSectionName.endsWith("servicesetting")
							|| lsSubSectionName.endsWith("specialization")
							|| lsSubSectionName.equalsIgnoreCase("servicehistory") || lsSubSectionName
								.equalsIgnoreCase("applicationSubmit")))
			{
				lsJSPName = getJSPNameForSubSection(aoRequest, lsJSPName, loMapToRender, lsFromServiceSummary,
						lsAction, lsSectionName, lsSubSectionName, loPortletSession, lsOrgId, lsUserId, lsBusnessAppId,
						lsAppStatus, lsServiceAppId, loReadOnlySection, loBrAppObj);
			}
			else if (null != lsAction
					&& (lsAction.equalsIgnoreCase("showServices") || lsAction.equalsIgnoreCase("showsimilarServices")
							|| lsAction.equalsIgnoreCase("showsimilarServicesAll") || lsAction.equals("documentupload")
							|| lsAction.equals("backrequest") || lsAction.equals("fileinformation")
							|| lsAction.equals("selectDocFromVault")
							|| lsAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO) || lsAction
								.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN)))
			{
				lsJSPName = getJSPNameForSection(aoRequest, lsJSPName, loMapToRender, lsAction, lsSectionName,
						lsSubSectionName, loPortletSession, lsOrgId, lsUserId, lsBusnessAppId, lsAppStatus,
						lsServiceAppId, loReadOnlySection, loBrAppObj, lsPermissionType, lsOrgnizationType);
			}
			else
			{
				return doOpenPrinterFriendly(loBrAppObj, lsOrgId, lsBusnessAppId, lsAction, lsUserId, aoRequest,
						lsSectionName, lsSubSectionName, lsAppStatus, loMapToRender, lsJSPName, lsServiceAppId,
						loReadOnlySection, lsAppId);
			}
			lsJSPName = getJSPName(aoRequest, lsJSPName, loMapToRender, lsFromServiceSummary, lsAction,
					loPortletSession, lbWithdrawalVisibleFlag, loReadOnly, loReadOnlySection);
			loMapToRender.put("isActiveCeo", ApplicationSession.getAttribute(aoRequest, true, "isActiveCeo"));
			if (ApplicationSession.getAttribute(aoRequest, true, "deactivatedService") != null)
			{
				loMapToRender.put("deactivatedService",
						ApplicationSession.getAttribute(aoRequest, "deactivatedService"));
			}
			if ((lsFromServiceSummary.equalsIgnoreCase("true") || aoRequest.getPortletSession().getAttribute(
					"headerPostService", PortletSession.APPLICATION_SCOPE) != null)
					&& !lsJSPName.equalsIgnoreCase("termsandconditions"))
			{
				lsJSPName = getJspNameForPostService(aoRequest, lsJSPName, loMapToRender, lsSubSectionName, lsAppStatus);
			}

			loModelAndView = new ModelAndView(lsJSPName, loMapToRender);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error(" Error occured in ApplicationServiceController ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			loModelAndView = new ModelAndView("errorHandler", loMapToRender);
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Internal Error occured in ApplicationServiceController ", aoAppExp);
			loModelAndView = new ModelAndView("errorpage", loMapToRender);
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in ApplicationServiceController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in ApplicationServiceController", aoEx);
			loModelAndView = new ModelAndView("errorpage", loMapToRender);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method sets Application Type.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoMapToRender - map holding data to be displayed
	 * @param aoPortletSession - PortletSession
	 */
	private void setApplicationType(RenderRequest aoRequest, Map<String, Object> aoMapToRender,
			PortletSession aoPortletSession)
	{   
		String lsApplicationType = PortalUtil.parseQueryString(aoRequest, "applicationType");
		if (lsApplicationType == null)
		{
			lsApplicationType = (String) aoPortletSession.getAttribute("applicationType");
			aoPortletSession.setAttribute("applicationType", lsApplicationType);
			aoMapToRender.put("applicationType", lsApplicationType);
		}
		else
		{
			aoPortletSession.setAttribute("applicationType", lsApplicationType);
			aoMapToRender.put("applicationType", lsApplicationType);
		}
	}

	/**
	 * Updated in 3.1.0. Added check for Defect 6346 This method gets Org Id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgIdFromCity - org id from city
	 * @param aoPortletSession - PortletSession
	 * @return lsOrgId - lsOrgId
	 */
	private String getOrgId(RenderRequest aoRequest, final String asOrgIdFromCity, PortletSession aoPortletSession)
	{   
		String lsOrgId = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		final String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
				"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
		if (lsCityUserSearchProviderId != null && !lsCityUserSearchProviderId.equalsIgnoreCase(""))
		{
			// Updated in 3.1.0. Added check for Defect 6346
			if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
			{
				lsOrgId = lsCityUserSearchProviderId.substring(0,
						lsCityUserSearchProviderId.indexOf(ApplicationConstants.TILD));
			}
			else
			{
				lsOrgId = lsCityUserSearchProviderId;
			}
			// Check for Defect 6346 Ends
		}
		else if (asOrgIdFromCity != null && !asOrgIdFromCity.equalsIgnoreCase(""))
		{
			// Updated in 3.1.0. Added check for Defect 6346
			if (asOrgIdFromCity.contains(ApplicationConstants.TILD))
			{
				lsOrgId = asOrgIdFromCity.substring(0, asOrgIdFromCity.indexOf(ApplicationConstants.TILD));
			}
			else
			{
				lsOrgId = asOrgIdFromCity;
			}
			// Check for Defect 6346 ends
		}
		return lsOrgId;
	}

	/**
	 * This method returns the jsp name.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asJSPName - name of the jsp
	 * @param aoMapToRender - map holding data to be displayed
	 * @param asSubSectionName - sub section anme
	 * @param asAppStatus - app status
	 * @return asJSPName - jsp name
	 */
	private String getJspNameForPostService(RenderRequest aoRequest, String asJSPName,
			Map<String, Object> aoMapToRender, String asSubSectionName, String asAppStatus)
	{
		if (asAppStatus.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT))
		{
			if (asSubSectionName.equalsIgnoreCase("summary"))
			{
				asJSPName = "home";
			}
			else
			{
				asJSPName = "home1";
			}
			aoRequest.setAttribute("removeTabs", "true");
		}
		else if (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
				|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)
				|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
				|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
		{
			aoRequest.setAttribute("section", "businessapplicationhistory");
			asJSPName = "finalview_header";
			aoRequest.setAttribute("applicationType", "service");
			aoMapToRender.put("applicationType", "service");
		}
		return asJSPName;
	}

	/**
	 * This method gets JSP Name For Section
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asJSPName - JSPName
	 * @param aoMapToRender - Map To Render
	 * @param asAction -Action
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - Org Id
	 * @param asUserId - User Id
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppStatus - App Status
	 * @param asServiceAppId - Service App Id
	 * @param aoReadOnlySection - Read Only Section
	 * @param aoBrAppObj - BusinessApplication
	 * @return - asJSPName
	 * @throws ApplicationException
	 */
	private String getJSPNameForSection(RenderRequest aoRequest, String asJSPName, Map<String, Object> aoMapToRender,
			String asAction, String asSectionName, String asSubSectionName, PortletSession aoPortletSession,
			String asOrgId, String asUserId, String asBusnessAppId, String asAppStatus, String asServiceAppId,
			Boolean aoReadOnlySection, BusinessApplication aoBrAppObj, String asPermissionType, String asOrgType)
			throws ApplicationException
	{
		if (null != asAction && asAction.equalsIgnoreCase("showServices"))
		{
			BusinessApplicationUtil.setIntoMapForRender(
					aoBrAppObj.getMapForRender(asAction, asSectionName, null, aoRequest), aoMapToRender);
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addAppService.jsp");
		}

		else if (asAction != null && asAction.equalsIgnoreCase("showsimilarServices"))
		{
			doShowSimilarServices(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, asAppStatus, aoMapToRender, asJSPName);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("showsimilarServicesAll"))
		{
			doShowSimilarServices(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, asAppStatus, aoMapToRender, asJSPName);
		}
		else if (null != asAction && asAction.equals("documentupload"))
		{
			asJSPName = doShowDocument(aoRequest, asJSPName);
		}
		else if (null != asAction && asAction.equals("backrequest"))
		{
			asJSPName = doDocumentBreakRequest(aoRequest, asJSPName);
		}
		else if (null != asAction && asAction.equals("fileinformation"))
		{
			asJSPName = doShowFileInformation(aoRequest, asJSPName);
		}
		else if (null != asAction && (asAction.equals("selectDocFromVault")))
		{
			aoPortletSession.setAttribute("selDocFromVaultItemList",
					ApplicationSession.getAttribute(aoRequest, "document_list_fromvault"),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute("docType", aoRequest.getParameter("docType"));
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.SELECT_DOC_FROM_VAULT);
			aoRequest.setAttribute(ApplicationConstants.ALLOWED_OBJECT_COUNT, ApplicationSession.getAttribute(aoRequest,false,ApplicationConstants.ALLOWED_OBJECT_COUNT));
			asJSPName = "home1";
		}
		else if (null == asAction || asAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
		{// for
			// Document
			asJSPName = doOpenDocument(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName, asServiceAppId,
					aoReadOnlySection, asPermissionType, asOrgType);
		}
		// below if will be executed when view document info action
		// executed successfully
		else if (asAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
		{
			asJSPName = getRenderViewDocument(aoRequest);
		}
		return asJSPName;
	}

	/**
	 * This method will be executed when a user select view document option from
	 * the drop down it will redirect user to the view document jsp page
	 * 
	 * @param aoRequest render request object
	 * @return lsFormPath name of the jsp to render
	 */
	private String getRenderViewDocument(RenderRequest aoRequest)
	{
		String lsFormPath = null;
		aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP,
				aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(com.nyc.hhs.model.Document) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ));
		aoRequest.setAttribute(ApplicationConstants.IS_LOCKED_STATUS,
				aoRequest.getParameter(ApplicationConstants.IS_LOCKED_STATUS));
		aoRequest.setAttribute("isViewDoc", "yes");
		aoRequest.setAttribute("cityToProvider", true);
		// Added for R5- jsp for viewing document property
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/viewdocumentinfo_overlay_bapp.jsp");
		// R5 end
		lsFormPath = "home1";
		return lsFormPath;
	}

	/**
	 * This method get jsp name.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asJSPName - jsp name
	 * @param aoMapToRender - Map To Render
	 * @param asFromServiceSummary - From Service Summary
	 * @param asAction - Action
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - Org Id
	 * @param asUserId - User Id
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppStatus - App Status
	 * @param asServiceAppId - Service App Id
	 * @param aoReadOnlySection - Read Only Section
	 * @param aoBrAppObj - BusinessApplication
	 * @return - asJSPName
	 * @throws ApplicationException
	 */
	private String getJSPNameForSubSection(RenderRequest aoRequest, String asJSPName,
			Map<String, Object> aoMapToRender, String asFromServiceSummary, String asAction, String asSectionName,
			String asSubSectionName, PortletSession aoPortletSession, String asOrgId, String asUserId,
			String asBusnessAppId, String asAppStatus, String asServiceAppId, Boolean aoReadOnlySection,
			BusinessApplication aoBrAppObj) throws ApplicationException
	{
		if (asSubSectionName != null && asSubSectionName.equalsIgnoreCase("summary"))
		{
			asJSPName = showSingleSummaryInfo(aoRequest, asJSPName, aoMapToRender, asFromServiceSummary, asAction,
					asSectionName, asSubSectionName, aoPortletSession, asOrgId, asBusnessAppId, asAppStatus,
					asServiceAppId, aoBrAppObj);
		}
		else if (asSubSectionName != null
				&& asSubSectionName.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
		{
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), asServiceAppId,
					HHSR5Constants.SERVICE_APPLICATION);
			// End R5 : set EntityId and EntityName for AutoSave

			asJSPName = doGetServiceQuestions(aoRequest, asJSPName, aoMapToRender, asAction, asSectionName,
					asSubSectionName, asOrgId, asUserId, asAppStatus, asBusnessAppId, aoReadOnlySection, aoBrAppObj);
		}
		else if (asSubSectionName != null && asSubSectionName.endsWith("servicesetting"))
		{
			asJSPName = doShowServiceSetting(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName, asServiceAppId);
		}
		else if (asSubSectionName != null && asSubSectionName.endsWith("specialization"))
		{
			asJSPName = doShowSpecilization(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName, asServiceAppId);
		}
		else if (null != asSubSectionName && (asSubSectionName.equalsIgnoreCase("servicehistory")))
		{
			if (null != aoRequest.getParameter("withdrawSuccessMsg"))
			{
				aoMapToRender.put("successMsg", aoRequest.getParameter("withdrawSuccessMsg"));
			}
			if (null != aoRequest.getParameter("withdrawErrorMsg"))
			{
				aoMapToRender.put("errorMsg", aoRequest.getParameter("withdrawErrorMsg"));
				aoMapToRender.put("lbWithdrawalVisibleFlag", "visible");
				aoMapToRender.put("org_type", "provider");
			}
			asJSPName = doShowServiceHistory(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName, asServiceAppId);
		}
		else if (null != asSubSectionName && (asSubSectionName.equalsIgnoreCase("applicationSubmit")))
		{
			asJSPName = doAfterApplicationSubmit(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName, asServiceAppId);
		}
		return asJSPName;
	}

	/**
	 * This method get Action Name
	 * 
	 * @param asSubSectionName - Sub Section Name
	 * @return lsAction - action to be performed
	 */
	private String getActionName(String asSubSectionName)
	{
		String lsAction;
		if (asSubSectionName.equalsIgnoreCase(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
		{
			lsAction = "showServiceQuestion";
		}
		else
		{
			lsAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
		}
		return lsAction;
	}

	/**
	 * This method gets the jsp name and sets the values in map to be displayed
	 * on jsp.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asJSPName - jsp name
	 * @param aoMapToRender - map to display data on the front end
	 * @param asFromServiceSummary - information From Service Summary
	 * @param asAction - action to be performed
	 * @param aoPortletSession - PortletSession
	 * @param abWithdrawalVisibleFlag - Withdrawal Visible Flag
	 * @param aoReadOnly - ReadOnly flag
	 * @param aoReadOnlySection - Read Only Section
	 * @return - asJSPName
	 */
	private String getJSPName(RenderRequest aoRequest, String asJSPName, Map<String, Object> aoMapToRender,
			String asFromServiceSummary, String asAction, PortletSession aoPortletSession,
			String abWithdrawalVisibleFlag, Boolean aoReadOnly, Boolean aoReadOnlySection) throws ApplicationException
	{
		if (asAction != null && asAction.equalsIgnoreCase("checkForServiceSummary"))
		{
			asJSPName = "postSubmitionHeader";
			aoRequest.setAttribute("checkForService", "true");
		}
		if (null != asAction && asAction.equalsIgnoreCase("checkForService") && asFromServiceSummary.equals("true"))
		{
			asJSPName = "postSubmitionHeader";
		}
		if (aoPortletSession.getAttribute("headerPostService", PortletSession.APPLICATION_SCOPE) != null
				&& !asJSPName.equalsIgnoreCase("termsandconditions")
				&& !asJSPName.equalsIgnoreCase("displayviewdocumentinfo"))
		{
			asJSPName = "postSubmitionHeader";
		}
		aoMapToRender.put("loReadOnly", aoReadOnly);
		aoMapToRender.put("loReadOnlySection", aoReadOnlySection);
		if (aoMapToRender.get("lbWithdrawalVisibleFlag") == null)
		{
			aoMapToRender.put("lbWithdrawalVisibleFlag", abWithdrawalVisibleFlag);
		}
		aoMapToRender.put("staffTitle", getMemberInfoTitle());
		aoMapToRender.put("contractType", CommonUtil.getContractType());
		aoMapToRender.put("NYCAgency", getNYCAgency());
		aoMapToRender.put("isActiveCeo", ApplicationSession.getAttribute(aoRequest, true, "isActiveCeo"));
		return asJSPName;
	}

	/**
	 * This method sets the service name
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @throws ApplicationException
	 */
	private void setServiceName(RenderRequest aoRequest, PortletSession aoPortletSession) throws ApplicationException
	{
		String lsElementId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ELEMENT_ID);
		if (lsElementId != null && !lsElementId.equalsIgnoreCase(""))
		{
			aoPortletSession.setAttribute(ApplicationConstants.ELEMENT_ID, lsElementId,
					PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			lsElementId = (String) aoPortletSession.getAttribute(ApplicationConstants.ELEMENT_ID,
					PortletSession.APPLICATION_SCOPE);
		}
		if (lsElementId != null)
		{
			aoRequest.setAttribute(ApplicationConstants.ELEMENT_ID, lsElementId);
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			if (loDoc != null)
			{
				String lsServiceName = BusinessApplicationUtil.getTaxonomyName(lsElementId, loDoc);
				if (null == lsServiceName || lsServiceName.equalsIgnoreCase(""))
				{
					Map<String, String> loActionMap = new HashMap<String, String>();
					loActionMap.put("lsElementId", lsElementId);
					Channel loChannel = new Channel();
					loChannel.setData("loActionMap", loActionMap);
					TransactionManager.executeTransaction(loChannel, "getDeletedServiceName");
					lsServiceName = (String) loChannel.getData("serviceName");
				}
				aoRequest.setAttribute("serviceName", lsServiceName);
			}
		}
	}

	/**
	 * This method shows the single summary info.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asJSPName - jsp name
	 * @param aoMapToRender - map to display data on the front end
	 * @param asFromServiceSummary - info from service summary
	 * @param asAction - action to be performed
	 * @param asSectionName - section name
	 * @param asSubSectionName - sub section name
	 * @param aoPortletSession - PortletSession
	 * @param asOrgId - organization id
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppStatus - App Status
	 * @param asServiceAppId - Service App Id
	 * @param aoBrAppObj - BusinessApplication object
	 * @return asJSPName - jsp name
	 * @throws ApplicationException
	 */
	private String showSingleSummaryInfo(RenderRequest aoRequest, String asJSPName, Map<String, Object> aoMapToRender,
			String asFromServiceSummary, String asAction, String asSectionName, String asSubSectionName,
			PortletSession aoPortletSession, String asOrgId, String asBusnessAppId, String asAppStatus,
			String asServiceAppId, BusinessApplication aoBrAppObj) throws ApplicationException
	{
		aoPortletSession.removeAttribute("contractSel");
		aoPortletSession.removeAttribute("staffSel");
		aoPortletSession.removeAttribute("noFunderStaff");
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, asAppStatus, null, asAction,
				null, aoRequest, null);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		String lsServiceStatus = null;
		if (asServiceAppId != null && !asServiceAppId.equalsIgnoreCase(HHSConstants.NULL))
		{
			if (((Map<String, StatusBean>) aoMapToRender.get("loServicesStatusBeanMap")).get(asServiceAppId) != null)
			{
				lsServiceStatus = ((Map<String, StatusBean>) aoMapToRender.get("loServicesStatusBeanMap")).get(
						asServiceAppId).getMsSectionStatusToDisplay();
			}
		}
		if (asServiceAppId != null
				&& !asAppStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)
				&& asFromServiceSummary.equals("true")
				&& !(lsServiceStatus != null && (lsServiceStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)
						|| lsServiceStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE) || lsServiceStatus
							.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)))
				&& !asAction.equalsIgnoreCase("removeService"))
		{
			lsTransactionName = "singleServiceInfo";
			loChannel.setData("asServiceId", asServiceAppId);
		}
		loChannel.setData("asAfterSubmissionAdd",
				aoPortletSession.getAttribute("headerPostService", PortletSession.APPLICATION_SCOPE));
		// Execute the transaction name obtained from navigation xml while
		// adding services in service summary.
		TransactionManager.executeTransaction(loChannel, lsTransactionName);// getSelectedService
		Boolean lbIsActiveCeoForService = (Boolean) loChannel.getData("getIsActiveCeoForService");
		ApplicationSession.setAttribute("false", aoRequest, "isActiveCeo");
		if (lbIsActiveCeoForService != null && lbIsActiveCeoForService)
		{
			ApplicationSession.setAttribute("true", aoRequest, "isActiveCeo");
		}
		// check if any service exit for this app open summary page else open
		// add service screen
		if (loChannel.getData("loServiceSummaryList") != null)
		{

			if (PortalUtil.parseQueryString(aoRequest, "action") == null)
			{
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/WEB-INF/jsp/businessapplication/serviceSummaryHome.jsp");
				if (aoMapToRender.get("deactivatedServiceList") != null
						&& ((List<Object>) aoMapToRender.get("deactivatedServiceList")).size() > 0)
				{
					loChannel.setData("deactivatedServiceList",
							(List<Object>) aoMapToRender.get("deactivatedServiceList"));
				}
				BusinessApplicationUtil.setIntoMapForRender(
						aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest), aoMapToRender);
			}
			else
			{
				asAction = "showServices";
				asSectionName = "servicessummary";
				asSubSectionName = "addservice";
				LOG_OBJECT.Debug("13 before get business application section::" + asSectionName + " Subsection::"
						+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
				BusinessApplication loBrAppObj = BusinessApplicationFactory.getBusinessApplication(asSectionName,
						asSubSectionName);
				getShowServices(loBrAppObj, asOrgId, asBusnessAppId, asAction, null, aoRequest, asSectionName,
						asSubSectionName);
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/WEB-INF/jsp/businessapplication/addAppService.jsp");
				BusinessApplicationUtil.setIntoMapForRender(
						loBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest), aoMapToRender);
			}
		}
		else if (loChannel.getData("loServiceSummaryList") == null
				&& aoPortletSession.getAttribute("headerPostService", PortletSession.APPLICATION_SCOPE) != null
				&& PortalUtil.parseQueryString(aoRequest, "action") != null)
		{
			asJSPName = setValuesInRequestPostService(aoRequest, aoPortletSession);
		}
		else if (loChannel.getData("loServiceSummary") != null)
		{
			List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
					.getData("serviceCommentsInfo");
			aoMapToRender.put("serviceComments", loServiceCommentsList);
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/singleServiceSummaryHome.jsp");
			BusinessApplicationUtil.setIntoMapForRender(
					aoBrAppObj.getMapForRender("singleService", asSectionName, loChannel, aoRequest), aoMapToRender);
		}
		else
		{
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addservice.jsp");
		}
		return asJSPName;
	}

	/**
	 * This method sets the values in request object
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return - lsJSPName
	 */
	private String setValuesInRequestPostService(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsJSPName;
		String lsDisplayTermsCondition = null; // T&C
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
			aoPortletSession.setAttribute("applicationId", BusinessApplicationUtil.generatAppId());
			aoPortletSession.setAttribute("addApplicationIdAfterSubmission", true);
		}
		catch (IOException aoAppex)
		{
			LOG_OBJECT.Error("IOException occurs while setting values in Post service", aoAppex);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		aoRequest.setAttribute("next_action", "showServices");
		lsJSPName = "termsandconditions";
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/termsandconditions.jsp");
		return lsJSPName;
	}

	/**
	 * This method sets the values in the portlet session
	 * 
	 * @param aoPortletSession - PortletSession
	 * @param asBusnessAppId - bus app id
	 * @param aoIsDisplay - string to be passed on to the jsp and other
	 *            controller
	 * @param asAppStatus - application status
	 * @param asServiceAppId - Service App Id
	 * @param asAppId - application id
	 */
	private void setValuesInPortletSession(PortletSession aoPortletSession, String asBusnessAppId,
			final String aoIsDisplay, String asAppStatus, String asServiceAppId, String asAppId)
	{
		aoPortletSession.setAttribute("loReadOnlyStatus", asAppStatus, PortletSession.APPLICATION_SCOPE);
		if (aoIsDisplay != null && !aoIsDisplay.isEmpty())
		{
			aoPortletSession.setAttribute("loReadOnlyStatus", ApplicationConstants.START_STATUS,
					PortletSession.APPLICATION_SCOPE);
		}
		aoPortletSession.setAttribute(ApplicationConstants.SERVICE_APPLICATION_ID, asServiceAppId);
		aoPortletSession.setAttribute("bussAppStatus", asAppStatus);
		aoPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, asBusnessAppId);
		aoPortletSession.setAttribute("applicationId", asAppId);
	}

	/**
	 * This method gets the Application status.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return - lsAppStatus
	 */
	private String getAppStatus(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAppStatus = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_APP_STATUS,
				PortletSession.APPLICATION_SCOPE);
		if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
		{
			lsAppStatus = PortalUtil.parseQueryString(aoRequest, "bussAppStatus");
			if (lsAppStatus != null && !lsAppStatus.equalsIgnoreCase(""))
			{
				aoPortletSession.setAttribute("bussAppStatus", lsAppStatus);
			}
		}
		// if null get value from session
		if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
		{
			lsAppStatus = (String) aoPortletSession.getAttribute("bussAppStatus");
			if (aoPortletSession.getAttribute("loReadOnlyStatus", PortletSession.APPLICATION_SCOPE) != null)
			{
				lsAppStatus = (String) aoPortletSession.getAttribute("loReadOnlyStatus",
						PortletSession.APPLICATION_SCOPE);
			}
		}
		return lsAppStatus;
	}

	/**
	 * This method gets the Business application id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return - lsBusnessAppId
	 */
	private String getBusinessAppId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
		// if null get value from session
		if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase(""))
		{
			lsBusnessAppId = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID);
		}
		return lsBusnessAppId;
	}

	/**
	 * This method gets the application id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return - lsAppId
	 */
	private String getAppId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAppId = PortalUtil.parseQueryString(aoRequest, "applicationId");
		// get value from session if null
		if (lsAppId == null || lsAppId.equalsIgnoreCase(""))
		{
			lsAppId = (String) aoPortletSession.getAttribute("applicationId");
		}
		return lsAppId;
	}

	/**
	 * This method is used to display the service question after adding the
	 * contract and staff
	 * 
	 * @param aoRequest request object
	 * @param asJSPName jsp need to display on the front end
	 * @param aoMapToRender map to display data on the front end
	 * @param asAction action needs to take care
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asOrgId organization id
	 * @param asUserId user id
	 * @param asAppStatus application status
	 * @param asBusnessAppId business application id
	 * @param aoReadOnlySection read only status
	 * @param aoBrAppObj factory object
	 * @return String
	 * @throws ApplicationException application exception
	 */
	private String doGetServiceQuestions(RenderRequest aoRequest, String asJSPName, Map<String, Object> aoMapToRender,
			String asAction, String asSectionName, String asSubSectionName, String asOrgId, String asUserId,
			String asAppStatus, String asBusnessAppId, Boolean aoReadOnlySection, BusinessApplication aoBrAppObj)
			throws ApplicationException
	{

		if (null != asAction && asAction.equals("showServiceQuestion"))
		{
			asJSPName = doShowServiceQuestion(aoBrAppObj, aoRequest, asSectionName, asOrgId, asBusnessAppId,
					asAppStatus, asAction, asSubSectionName, aoMapToRender, aoReadOnlySection, asJSPName);
		}
		// Associate Contract to a Service application.
		else if (null != asAction && asAction.equalsIgnoreCase("addContract"))
		{
			asJSPName = doShowAddContract(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, asAppStatus, aoMapToRender, asJSPName);
		}
		// Selected existing Contract from drop down.
		else if (null != asAction && asAction.equalsIgnoreCase("selectValue"))
		{
			aoRequest.setAttribute("getValue", ApplicationSession.getAttribute(aoRequest, true, "getValue"));
			aoRequest.setAttribute("getNYCAgency", ApplicationSession.getAttribute(aoRequest, true, "getNYCAgency"));
			aoRequest.setAttribute("reqContract", ApplicationSession.getAttribute(aoRequest, true, "reqContract"));
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addContract.jsp");
			asJSPName = "home1";
		}
		// Save new contract or update exisiting contract
		else if (null != asAction && asAction.equalsIgnoreCase("save"))
		{
			asJSPName = doShowSaveContract(aoRequest, asJSPName);
		}
		// Selected existing Staff from drop down.
		else if (null != asAction && asAction.equalsIgnoreCase("selectStaff"))
		{
			if (ApplicationSession.getAttribute(aoRequest, true, "currentStaff") != null)
			{
				StaffDetails loStaffDetails = (StaffDetails) ApplicationSession.getAttribute(aoRequest, false,
						"currentStaff");
				aoRequest.setAttribute("reqStaff", loStaffDetails);
				aoRequest.setAttribute("ceoExist", ApplicationConstants.ERROR_MSG);
			}
			else
			{
				aoRequest.setAttribute("reqStaff", ApplicationSession.getAttribute(aoRequest, "reqStaff"));
			}
			aoRequest.setAttribute("getValue", ApplicationSession.getAttribute(aoRequest, "getValue"));

			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addStaff.jsp");
			asJSPName = "home1";
		}
		// Associate Staff to a Service application.
		else if (null != asAction && asAction.equalsIgnoreCase("addStaff"))
		{
			if (ApplicationSession.getAttribute(aoRequest, true, "currentStaff") != null)
			{
				StaffDetails loStaffDetails = (StaffDetails) ApplicationSession.getAttribute(aoRequest, false,
						"currentStaff");
				aoRequest.setAttribute("reqStaff", loStaffDetails);
				aoRequest.setAttribute("ceoExist", ApplicationConstants.ERROR_MSG);
			}
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
					asAction, null, aoRequest, asSubSectionName);
			String lsTransactionName = (String) loChannel.getData("transaction_name");
			// Execute the transaction name obtained from the file
			// ServiceQuestion.java
			Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
			if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
			{
				loReqServiceInfo.put("serviceType",
						(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
				loChannel.setData("reqServiceInfo", loReqServiceInfo);
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			Boolean loDeactivatedService = (Boolean) loChannel.getData("deactivatedService");
			if (loDeactivatedService != null)
			{
				aoMapToRender.put("deactivatedService", loDeactivatedService);
			}
			List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
					.getData("serviceCommentsInfo");
			aoMapToRender.put("serviceComments", loServiceCommentsList);
			BusinessApplicationUtil.setIntoMapForRender(
					aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest), aoMapToRender);
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addStaff.jsp");
			asJSPName = "home1";
		}
		// Save new staff or update exsiting staff
		else if (null != asAction && (asAction.equalsIgnoreCase("saveStaff") || asAction.equalsIgnoreCase("cancel")))
		{
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/ServiceQuestion.jsp");
			asJSPName = "home1";
		}
		return asJSPName;
	}

	/**
	 * This method is used to open the printer friendly version for the business
	 * and service application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param abReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private ModelAndView doOpenPrinterFriendly(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId,
			Boolean abReadOnlySection, final String asAppId) throws ApplicationException
	{

		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, null, aoRequest, null);
		loChannel.setData(ApplicationConstants.SERVICE_APPLICATION_ID, asServiceAppId);
		loChannel.setData("asBussAppId", asBusnessAppId);
		loChannel.setData(ApplicationConstants.APPID, asAppId);
		String lsTransactionName = null;
		if (loChannel.getData("lsTransactionName") != null && lsTransactionName == null)
		{
			lsTransactionName = (String) loChannel.getData("lsTransactionName") + "Service";
			// Execute the transaction name obtained from the file
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
		}
		if (loChannel.getData("lsTransactionName") != null)
		{
			Map<String, Object> loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel,
					aoRequest);
			aoMapToRender.put("applicationType", "service");
			BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
			ModelAndView loModelAndView = new ModelAndView("printerfriendly", aoMapToRender);
			return loModelAndView;
		}
		return null;
	}

	/**
	 * This method is used to open document for the service application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param abReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doOpenDocument(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId,
			Boolean abReadOnlySection, String asPermissionType, String asOrgnizationType) throws ApplicationException
	{
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, null, aoRequest, asSubSectionName);
		// Execute the transaction name obtained from the file
		// DocumentbusinessApp.java
		Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
		if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
		{
			loReqServiceInfo.put("serviceType",
					(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
			loChannel.setData("reqServiceInfo", loReqServiceInfo);
		}
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loDeactivatService = (Boolean) loChannel.getData("deactivatedService");
		if (loDeactivatService != null)
		{
			aoMapToRender.put("deactivatedService", loDeactivatService);
		}
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		aoMapToRender.put("serviceComments", loServiceCommentsList);
		Map<String, Object> loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
		if (loMapToRender != null && !loMapToRender.isEmpty())
		{
			if (loMapToRender.containsKey("taskItemList"))
			{
				List<com.nyc.hhs.model.Document> loDocumentList = (List<com.nyc.hhs.model.Document>) loMapToRender
						.get("taskItemList");
				if (loDocumentList != null && !loDocumentList.isEmpty())
				{
					for (com.nyc.hhs.model.Document loDocument : loDocumentList)
					{
						if (abReadOnlySection)
						{
							loDocument.setReadOnly("disabled=disabled");
						}
						// Added for release 5
						if (asPermissionType != null
								&& asOrgnizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
								&& asPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY))
						{
							loDocument.setUserAccess(false);
						}
						// Added for release 5
					}
				}
			}
		}
		List<ContractDetails> loContractList = (List<ContractDetails>) loChannel.getData("allContractDetailsForGrid");
		ApplicationSession.setAttribute("no", aoRequest, "typeNyc");
		if (loContractList != null && !loContractList.isEmpty())
		{
			for (ContractDetails loContractDetails : loContractList)
			{
				if (loContractDetails.getMsContractFunderName() == null
						|| loContractDetails.getMsContractFunderName().equalsIgnoreCase("")
						|| loContractDetails.getMsContractFunderName().equalsIgnoreCase(HHSConstants.NULL))
				{
					loContractDetails.setMsContractFunderName(loContractDetails.getMsContractNYCAgency());
				}
			}
			for (ContractDetails loContractDetails : loContractList)
			{
				if (loContractDetails.getMsContractType() != null
						&& loContractDetails.getMsContractType().equalsIgnoreCase("NYC Government"))
				{
					ApplicationSession.setAttribute("yes", aoRequest, "typeNyc");
				}
				else
				{
					ApplicationSession.setAttribute("no", aoRequest, "typeNyc");
					break;
				}
			}
		}

		aoRequest.setAttribute("messagetypeNyc", ApplicationSession.getAttribute(aoRequest, "typeNyc"));
		aoRequest
				.setAttribute(
						"messageNyc",
						"No documents are required for this service since you indicated a Funder was an NYC Agency.Click the \"Next\" button to continue.");
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				ApplicationSession.getAttribute(aoRequest, "message"));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationSession.getAttribute(aoRequest, "messageType"));
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.DOCUMENT_LIST_PAGE);
		asJSPName = "home1";
		BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
		return asJSPName;
	}

	/**
	 * This method is used to perform the operation once submit button is
	 * clicked and display the terms and condition and display the jsp page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doAfterApplicationSubmit(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId)
			throws ApplicationException
	{
		if (null != aoRequest.getParameter("loginErrorMsg"))
		{
			aoMapToRender.put("errorMsg", aoRequest.getParameter("loginErrorMsg"));
		}
		String lsDisplayTermsCondition = null;
		Channel loChannel = new Channel();
		Map<String, String> loServiceInfoMap = new HashMap<String, String>();
		loServiceInfoMap.put("serviceAppId",
				PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID));
		loServiceInfoMap.put("orgId", asOrgId);
		loChannel.setData("reqServiceInfo", loServiceInfoMap);
		TransactionManager.executeTransaction(loChannel, "getServiceComments");
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		aoMapToRender.put("serviceComments", loServiceCommentsList);
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching Application Terms and Condition from Filenet ", aoAppEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/servicesubmit.jsp");
		asJSPName = "postSubmitionHeader";
		aoRequest.setAttribute("checkForService", "true");
		return asJSPName;
	}

	/**
	 * This method is used to show the service history for the application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doShowServiceHistory(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId)
			throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, null, aoRequest, asSubSectionName);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		List<Map<String, Object>> loApplicationHistory = (List<Map<String, Object>>) loChannel
				.getData("applicationHistoryInfo");
		aoRequest.setAttribute("applicationHistoryInfo", loApplicationHistory);
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/serviceWithdrawal.jsp");
		asJSPName = "postSubmitionHeader";
		aoRequest.setAttribute("checkForService", "true");
		aoRequest.setAttribute("displayHistory", "displayHistory");
		return asJSPName;
	}

	/**
	 * This method is used to show the file information from the document vault
	 * 
	 * @param aoRequest request object
	 * @param asJSPName jsp to show
	 */
	private String doShowFileInformation(RenderRequest aoRequest, String asJSPName)
	{
		aoRequest.setAttribute("document_category",
				ApplicationSession.getAttribute(aoRequest, true, "document_category"));
		aoRequest.setAttribute("document_type", ApplicationSession.getAttribute(aoRequest, true, "document_type"));
		aoRequest.setAttribute("form_name", ApplicationSession.getAttribute(aoRequest, true, "form_name"));
		aoRequest.setAttribute("form_version", ApplicationSession.getAttribute(aoRequest, true, "form_version"));
		aoRequest.setAttribute("service_app_id", ApplicationSession.getAttribute(aoRequest, true, "service_app_id"));
		aoRequest.setAttribute("section_id", ApplicationSession.getAttribute(aoRequest, true, "section_id"));
		aoRequest.setAttribute(ApplicationConstants.LINKED_TO_APP_FLAG,
				ApplicationSession.getAttribute(aoRequest, ApplicationConstants.LINKED_TO_APP));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ));
		// Made chages for defect id 5545.
		if (ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE) != null
				&& !((String) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.ERROR_MESSAGE))
						.isEmpty())
		{
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationSession.getAttribute(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE));
		}
		else if (aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
				PortletSession.APPLICATION_SCOPE) != null
				&& !((String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
						PortletSession.APPLICATION_SCOPE)).isEmpty())
		{
			aoRequest.setAttribute(
					ApplicationConstants.ERROR_MESSAGE,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
							PortletSession.APPLICATION_SCOPE));
			aoRequest.setAttribute(
					ApplicationConstants.ERROR_MESSAGE_TYPE,
					aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							PortletSession.APPLICATION_SCOPE));
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.DISPLAY_FILE_INFO_PAGE);
		asJSPName = "home1";
		return asJSPName;
	}

	/**
	 * This method is used to break the request when overlay goes from 1 step to
	 * other
	 * 
	 * @param aoRequest request object
	 * @param asJSPName jsp to show
	 */
	private String doDocumentBreakRequest(RenderRequest aoRequest, String asJSPName)
	{
		aoRequest.setAttribute("document_category", ApplicationSession.getAttribute(aoRequest, "document_category"));
		aoRequest.setAttribute("document_type", ApplicationSession.getAttribute(aoRequest, "document_type"));
		aoRequest.setAttribute("form_name", ApplicationSession.getAttribute(aoRequest, "form_name"));
		aoRequest.setAttribute("form_version", ApplicationSession.getAttribute(aoRequest, "form_version"));
		aoRequest.setAttribute("service_app_id", ApplicationSession.getAttribute(aoRequest, "service_app_id"));
		aoRequest.setAttribute("section_id", ApplicationSession.getAttribute(aoRequest, "section_id"));
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.UPLOAD_FILE_PAGE);
		asJSPName = "home1";
		return asJSPName;
	}

	/**
	 * This method is used to show the document list for the service application
	 * 
	 * @param aoRequest request object
	 * @param asJSPName jsp to show
	 */
	private String doShowDocument(RenderRequest aoRequest, String asJSPName)
	{
		aoRequest.setAttribute("document_category", ApplicationSession.getAttribute(aoRequest, "document_category"));
		aoRequest.setAttribute("document_type", ApplicationSession.getAttribute(aoRequest, "document_type"));
		aoRequest.setAttribute("form_name", ApplicationSession.getAttribute(aoRequest, "form_name"));
		aoRequest.setAttribute("form_version", ApplicationSession.getAttribute(aoRequest, "form_version"));
		aoRequest.setAttribute("section_id", ApplicationSession.getAttribute(aoRequest, "section_id"));
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, ApplicationConstants.UPLOAD_FILE_PAGE);
		asJSPName = "home1";
		return asJSPName;
	}

	/**
	 * This method is used to show the service specilization
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doShowSpecilization(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId)
			throws ApplicationException
	{
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/specialization.jsp");
		asJSPName = "home1";
		Map<String, Object> loMapToRender = null;
		if (null != aoRequest.getParameter("errorMsg"))
		{
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
					asAction, null, aoRequest, ApplicationConstants.SPECIALIZATION);
			loChannel.setData("asServiceAppId", asServiceAppId);
			loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
			loMapToRender.put("errorMsg", (String) aoRequest.getParameter("errorMsg"));
		}
		else
		{
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
					asAction, null, aoRequest, ApplicationConstants.SPECIALIZATION);
			loChannel.setData("asServiceAppId", asServiceAppId);
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.OPEN_TRANSACTION);
			// Execute the transaction name obtained from the file
			// Specialization.java
			Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
			if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
			{
				loReqServiceInfo.put("serviceType",
						(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
				loChannel.setData("reqServiceInfo", loReqServiceInfo);
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			Boolean loDeactivatedService = (Boolean) loChannel.getData("deactivatedService");
			if (loDeactivatedService != null)
			{
				aoMapToRender.put("deactivatedService", loDeactivatedService);
			}
			List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
					.getData("serviceCommentsInfo");
			aoMapToRender.put("serviceComments", loServiceCommentsList);
			loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
		}
		BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
		return asJSPName;
	}

	/**
	 * This method is used to show the service setting for the service
	 * application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doShowServiceSetting(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName, final String asServiceAppId)
			throws ApplicationException
	{
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/servicesetting.jsp");
		asJSPName = "home1";
		Map<String, Object> loMapToRender = null;
		if (null != aoRequest.getParameter("errorMsg"))
		{
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
					asAction, null, aoRequest, ApplicationConstants.SERVICE_SETTING);
			loChannel.setData("asServiceAppId", asServiceAppId);
			loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
			loMapToRender.put("errorMsg", (String) aoRequest.getParameter("errorMsg"));
		}
		else
		{
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
					asAction, null, aoRequest, ApplicationConstants.SERVICE_SETTING);
			loChannel.setData("asServiceAppId", asServiceAppId);
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.OPEN_TRANSACTION);
			// Execute the transaction name obtained from the file
			// ServiceSetting.java
			Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
			if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
			{
				loReqServiceInfo.put("serviceType",
						(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
				loChannel.setData("reqServiceInfo", loReqServiceInfo);
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			Boolean loDeactivatedService = (Boolean) loChannel.getData("deactivatedService");
			if (loDeactivatedService != null)
			{
				aoMapToRender.put("deactivatedService", loDeactivatedService);
			}
			List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
					.getData("serviceCommentsInfo");
			aoMapToRender.put("serviceComments", loServiceCommentsList);
			loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
		}
		BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
		return asJSPName;
	}

	/**
	 * This method is used to show the similar services for the service
	 * application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private void doShowSimilarServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName) throws ApplicationException
	{
		String lsSelectedService = (String) ApplicationSession.getAttribute(aoRequest, "selectedService");
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/relatedservices.jsp");
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, null,
				aoRequest, null);
		if (null != lsSelectedService && !lsSelectedService.isEmpty())
		{
			String[] loSelectedServiceArray = lsSelectedService.split(",");
			List<String> loSelectedServiceList = new ArrayList<String>();
			for (String lsStr : loSelectedServiceArray)
			{
				loSelectedServiceList.add(lsStr);
			}
			loChannel.setData("selectedServices", loSelectedServiceList);
		}
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file AddService.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		List<TaxonomyTree> loRelatedServicesList = (List<TaxonomyTree>) loChannel.getData("loRelatedServiceDetails");
		if (null != BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT))
		{
			BusinessApplicationUtil.joinParentNameWithChild(loRelatedServicesList, (Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT));
		}
		else
		{
			PropertyUtil loTaxonomyUtil = new PropertyUtil();
			loTaxonomyUtil.setTaxonomyInCache(BaseCacheManagerWeb.getInstance(), ApplicationConstants.TAXONOMY_ELEMENT);
			BusinessApplicationUtil.joinParentNameWithChild(loRelatedServicesList, (Document) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.TAXONOMY_ELEMENT));
		}
		Collections.sort(loRelatedServicesList, new Comparator<TaxonomyTree>()
		{
			@Override
			public int compare(TaxonomyTree aoObject1, TaxonomyTree aoObject2)
			{
				int liCounter = 0;
				if (aoObject1.getMsDisplayName() != null && aoObject2.getMsDisplayName() != null)
				{
					liCounter = aoObject1.getMsDisplayName().compareTo(aoObject2.getMsDisplayName());
				}
				return liCounter;
			}
		});
		aoMapToRender.put("loRelatedServicesList", loRelatedServicesList);
	}

	/**
	 * This method is used to show the contract once save the contract into the
	 * system
	 * 
	 * @param aoRequest request object
	 * @param asJSPName jsp to show
	 */
	private String doShowSaveContract(RenderRequest aoRequest, String asJSPName)
	{
		String lbValidPeriod = (String) ApplicationSession.getAttribute(aoRequest, "lbValidPeriod");
		if (aoRequest.getParameter("contractIdExist") != null
				&& aoRequest.getParameter("contractIdExist").equalsIgnoreCase("true"))
		{
			aoRequest.setAttribute("contractIdExist", aoRequest.getParameter("contractIdExist"));
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addContract.jsp");
		}
		else if (aoRequest.getParameter("contractIdNotExist") != null
				&& aoRequest.getParameter("contractIdNotExist").equalsIgnoreCase("false"))
		{
			aoRequest.setAttribute("contractIdExist", false);
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addContract.jsp");
		}
		else if (lbValidPeriod != null && lbValidPeriod.equalsIgnoreCase("true"))
		{
			aoRequest.setAttribute("getValue", ApplicationSession.getAttribute(aoRequest, true, "getValue"));
			aoRequest.setAttribute("getNYCAgency", ApplicationSession.getAttribute(aoRequest, true, "getNYCAgency"));
			aoRequest.setAttribute("reqContract", ApplicationSession.getAttribute(aoRequest, true, "reqContract"));
			aoRequest.setAttribute("aDate", "Start date should be less than end date");
			aoRequest.setAttribute(ApplicationConstants.MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/addContract.jsp");
		}
		else
		{
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/ServiceQuestion.jsp");
			asJSPName = "home1";
		}
		return asJSPName;
	}

	/**
	 * This method is used to show add contract page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param loReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doShowAddContract(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final String asAppStatus,
			Map<String, Object> aoMapToRender, String asJSPName) throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, null, aoRequest, asSubSectionName);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
		if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
		{
			loReqServiceInfo.put("serviceType",
					(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
			loChannel.setData("reqServiceInfo", loReqServiceInfo);
		}
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loDeactivatedService = (Boolean) loChannel.getData("deactivatedService");
		if (loDeactivatedService != null)
		{
			aoMapToRender.put("deactivatedService", loDeactivatedService);
		}
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		aoMapToRender.put("serviceComments", loServiceCommentsList);
		String lsContractExists = aoRequest.getParameter("contractExists");
		if (lsContractExists != null && lsContractExists.equalsIgnoreCase("true"))
		{
			ContractDetails loContractDetails = (ContractDetails) ApplicationSession.getAttribute(aoRequest, true,
					"reqContract");
			aoMapToRender.put("reqContract", loContractDetails);
			aoMapToRender.put("contractIdExist", true);
			aoMapToRender.put("contractGrant", aoRequest.getParameter("contractGrant"));
		}
		Map<String, Object> loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
		aoRequest
				.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, "/WEB-INF/jsp/businessapplication/addContract.jsp");
		asJSPName = "home1";
		BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
		return asJSPName;
	}

	/**
	 * This method is used to show the service question page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction user action
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName sub section name
	 * @param asAppStatus application status
	 * @param aoMapToRender map to render to the jsp
	 * @param asJSPName jsp to show
	 * @param asServiceAppId service application id
	 * @param abReadOnlySection read only status
	 * @param asAppId application id
	 * @return ModelAndView
	 * @throws ApplicationException Application exception
	 */
	private String doShowServiceQuestion(BusinessApplication aoBrAppObj, RenderRequest aoRequest,
			final String asSectionName, final String asOrgId, final String asBusnessAppId, final String asAppStatus,
			final String asAction, final String asSubSectionName, Map<String, Object> aoMapToRender,
			Boolean abReadOnlySection, String asJSPName) throws ApplicationException
	{
		aoRequest.setAttribute("contractSel", ApplicationSession.getAttribute(aoRequest, "contractSel"));
		aoRequest.setAttribute("staffSel", ApplicationSession.getAttribute(aoRequest, "staffSel"));
		aoRequest.setAttribute("appServiceStatus", asAppStatus);
		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, null, aoRequest, asSubSectionName);
		Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
		if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
		{
			loReqServiceInfo.put("serviceType",
					(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
			loChannel.setData("reqServiceInfo", loReqServiceInfo);
		}
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loIsDeactivatedService = (Boolean) loChannel.getData("deactivatedService");
		if (loIsDeactivatedService != null)
		{
			aoMapToRender.put("deactivatedService", loIsDeactivatedService);
		}
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		aoMapToRender.put("serviceComments", loServiceCommentsList);
		Map<String, Object> loMapToRender = aoBrAppObj.getMapForRender(asAction, asSectionName, loChannel, aoRequest);
		// this if condition is used to set the read only string in the object
		// to make the drop down disabled
		ApplicationSession.setAttribute("no", aoRequest, "typeNyc");
		if (loMapToRender != null && !loMapToRender.isEmpty())
		{
			if (loMapToRender.containsKey("aoContractList"))
			{
				List<ContractDetails> loContractDetailsList = (List<ContractDetails>) loMapToRender
						.get("aoContractList");
				if (loContractDetailsList != null && !loContractDetailsList.isEmpty())
				{
					for (ContractDetails loContractDetails : loContractDetailsList)
					{
						if (abReadOnlySection != null && abReadOnlySection)
						{
							loContractDetails.setReadOnly("disabled=disabled");
						}
						if (loContractDetails.getMsContractType() != null
								&& "NYC Government".equalsIgnoreCase(loContractDetails.getMsContractType()))
						{
							loContractDetails.setMsContractFunderName(loContractDetails.getMsContractNYCAgency());
						}
					}
				}
				if (loContractDetailsList != null && !loContractDetailsList.isEmpty())
				{
					for (ContractDetails loContractDetails : loContractDetailsList)
					{
						if (loContractDetails.getMsContractType() != null
								&& "NYC Government".equalsIgnoreCase(loContractDetails.getMsContractType()))
						{
							ApplicationSession.setAttribute("yes", aoRequest, "typeNyc");
						}
						else
						{
							ApplicationSession.setAttribute("no", aoRequest, "typeNyc");
							break;
						}
					}
				}
			}
			if (loMapToRender.containsKey("aoStaffDetailsList"))
			{
				List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loMapToRender.get("aoStaffDetailsList");
				if (loStaffDetailsList != null && !loStaffDetailsList.isEmpty())
				{
					for (StaffDetails loStaffDetail : loStaffDetailsList)
					{
						if (abReadOnlySection != null && abReadOnlySection)
						{
							loStaffDetail.setReadOnly("disabled=disabled");
						}
					}
				}
			}
		}
		aoRequest.setAttribute("message", ApplicationSession.getAttribute(aoRequest, "message"));
		aoRequest.setAttribute("messageType", ApplicationSession.getAttribute(aoRequest, "messageType"));
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/ServiceQuestion.jsp");
		asJSPName = "home1";
		BusinessApplicationUtil.setIntoMapForRender(loMapToRender, aoMapToRender);
		return asJSPName;
	}

	/**
	 * This method is used to display the withdrawn link based on the
	 * application or service status
	 * 
	 * @param asAppStatus business or service status
	 * @param asServiceAppId service application id
	 * @param asUserRole user role
	 * @param abWithdrawalVisibleFlag withdrawn link
	 * @return String
	 * @throws ApplicationException application exception
	 */
	private String doSelectWithdrawl(String asBusnessAppId, String asOrgId, String asUserId, final String asAppStatus,
			final String asServiceAppId, final String asUserRole, String abWithdrawalVisibleFlag)
			throws ApplicationException
	{

		Channel loChannelObj = new Channel();
		Map<String, String> loServiceAppInfo = new HashMap<String, String>();
		loServiceAppInfo.put("serviceAppId", asServiceAppId);
		loServiceAppInfo.put("orgId", asOrgId);
		loChannelObj.setData("serviceAppInfo", loServiceAppInfo);
		loChannelObj.setData(ApplicationConstants.APP_ID, asBusnessAppId);
		loChannelObj.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannelObj.setData(ApplicationConstants.SECTION, asServiceAppId);
		loChannelObj.setData(ApplicationConstants.USER_ID, asUserId);
		loChannelObj.setData("asServiceId", asServiceAppId);
		TransactionManager.executeTransaction(loChannelObj, "getCompleteStatusMap");
		loChannelObj.setData("serviceAppId", asServiceAppId);
		TransactionManager.executeTransaction(loChannelObj, "fetchServiceAppWithdrawStatus");
		String lsWithdrawStatus = (String) loChannelObj.getData("withdrawStatus");
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) loChannelObj
				.getData("loServiceStatusBeanMap");
		StatusBean loStatusBean = (StatusBean) loServicesStatusBeanMap.get(asServiceAppId);
		String lsSuperSeedingStatus = null;
		if (loStatusBean != null)
		{
			lsSuperSeedingStatus = loStatusBean.getMsSectionStatusToDisplay();
		}
		if ((lsWithdrawStatus != null && (lsWithdrawStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW) || lsWithdrawStatus
				.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)))
				|| (asAppStatus != null && asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)))
		{
			abWithdrawalVisibleFlag = "Invisible";
		}
		else if (lsSuperSeedingStatus != null
				&& ((lsSuperSeedingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN) || lsSuperSeedingStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)) || (asAppStatus != null && asAppStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED))))
		{
			abWithdrawalVisibleFlag = "Invisible";
		}
		else if (lsWithdrawStatus != null
				&& asAppStatus != null
				&& (lsWithdrawStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW) || asAppStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)))
		{
			abWithdrawalVisibleFlag = "Invisible";
		}
		else if (asUserRole != null
				&& (asUserRole.contains("manager") || asUserRole
						.contains(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER))
				&& !(asAppStatus != null && (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED) || asAppStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))))
		{
			abWithdrawalVisibleFlag = "visible";
		}
		else
		{
			abWithdrawalVisibleFlag = "Invisible";
		}
		return abWithdrawalVisibleFlag;
	}

	/**
	 * This method is used to get the application status for the running
	 * application
	 * 
	 * @param asBusnessAppId business application id
	 * @param asOrgId orgnization id
	 * @param asUserId user id
	 * @param asServiceAppId service application id
	 * @param aoMapToRender map to render on the jsp page
	 * @param aoRequest request object
	 * @param asFromServiceSummary postSubmission request
	 * @throws ApplicationException application exception
	 */
	private void doGetApplicationStatus(String asApplicationStatus, final String asBusnessAppId, final String asOrgId,
			final String asUserId, final String asServiceAppId, Map<String, Object> aoMapToRender,
			RenderRequest aoRequest, String asFromServiceSummary) throws ApplicationException
	{
		String lsServiceSummaryStatus = "";
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APP_ID, asBusnessAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannel.setData(ApplicationConstants.SECTION, asServiceAppId);
		loChannel.setData(ApplicationConstants.USER_ID, asUserId);
		loChannel.setData("asServiceId", asServiceAppId);
		// Execute the transaction name to get the business and services status
		TransactionManager.executeTransaction(loChannel, "getCompleteStatusMap");
		Map<String, StatusBean> loBusinessStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loBusinessStatusBeanMap");
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loServiceStatusBeanMap");
		Boolean lbApplicationStatus = ((Map<String, Boolean>) loChannel.getData("applicationStatus"))
				.get("completeStatus");
		if (asApplicationStatus != null
				&& (asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) || asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
		{
			StatusBean loStatusBeanObj = loServicesStatusBeanMap.get(asServiceAppId);
			if (loStatusBeanObj != null && loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase("complete"))
			{
				lbApplicationStatus = true;
			}
		}
		Collection<StatusBean> loStatusBean = loServicesStatusBeanMap.values();
		Iterator<StatusBean> loIterator = loStatusBean.iterator();
		lsServiceSummaryStatus = getServiceStatus(lsServiceSummaryStatus, loIterator);
		String lsNewService = "";
		Map<String, String> loBusinessInfo = new HashMap<String, String>();
		if (aoRequest != null)
		{
			lsNewService = aoRequest.getParameter("newService");
		}
		StatusBean loReqService = loServicesStatusBeanMap.get(asServiceAppId);
		String lsCheckForDefferedOrReturned = "";
		if (loReqService != null)
		{
			Map<String, String> loReqServiceInfoMap = loReqService.getMoHMSubSectionDetailsToDisplay();
			lsCheckForDefferedOrReturned = loReqServiceInfoMap.get("servicesetting");
		}
		loBusinessInfo.put("businessAppId", asBusnessAppId);
		if (asApplicationStatus != null
				&& (asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
						|| asApplicationStatus.equalsIgnoreCase(ApplicationConstants.DEACTIVATED) || asApplicationStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
				&& lsCheckForDefferedOrReturned != null
				&& !(lsCheckForDefferedOrReturned.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)))
		{
			loBusinessInfo.put("serviceType", "new");

			if (aoRequest != null)
			{
				ApplicationSession.setAttribute("new", aoRequest, "serviceType");
			}

		}
		else
		{
			loBusinessInfo.put("serviceType", "old");
			if (aoRequest != null)
			{
				ApplicationSession.setAttribute("old", aoRequest, "serviceType");
			}
		}
		loChannel.setData("reqBusinessInfo", loBusinessInfo);
		TransactionManager.executeTransaction(loChannel, "getDeactivatedServiceForApp");
		List<Object> loDeactivatedServiceList = (List<Object>) loChannel.getData("deactivatedServiceList");
		Boolean lbDeactivateFlag = (Boolean) loDeactivatedServiceList.get(loDeactivatedServiceList.size() - 1);
		loDeactivatedServiceList.remove(loDeactivatedServiceList.size() - 1);
		aoMapToRender.put("deactivatedServiceList", loDeactivatedServiceList);
		if (lbApplicationStatus)
		{
			lbApplicationStatus = setApplicationStatus(asApplicationStatus, loChannel, lbApplicationStatus);
		}
		if (lsServiceSummaryStatus.equalsIgnoreCase(""))
		{
			if (lsNewService != null && lsNewService.equalsIgnoreCase("newService"))
			{
				lbApplicationStatus = false;
			}
			lsServiceSummaryStatus = "notstarted";
		}
		if (asFromServiceSummary.equalsIgnoreCase("true")
				&& asApplicationStatus.equalsIgnoreCase(ApplicationConstants.APP_STATUS_DRAFT))
		{
			for (Entry<String, StatusBean> loEntry : loServicesStatusBeanMap.entrySet())
			{
				StatusBean lsStatusBean = loEntry.getValue();
				String lsStatusToDisplay = lsStatusBean.getMsSectionStatusToDisplay();
				String lsStatus = lsStatusBean.getMsSectionStatus();
				if (lsStatusToDisplay.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)
						|| lsStatusToDisplay.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)
						|| lsStatusToDisplay.equalsIgnoreCase("Partially Complete")
						|| lsStatusToDisplay.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
						|| lsStatusToDisplay.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
				{
					if (!lsStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						lbApplicationStatus = false;
						break;
					}
					else
					{
						lbApplicationStatus = true;
					}
				}
			}
		}
		if (lbApplicationStatus)
		{
			if (lbDeactivateFlag != null && lbDeactivateFlag)
			{
				lbApplicationStatus = false;
			}
		}
		if (lbDeactivateFlag != null && lbDeactivateFlag)
		{
			aoMapToRender.put("deactivatedService", lbDeactivateFlag);
		}
		if (aoRequest != null)
		{
			aoRequest.getPortletSession().setAttribute("serviceSummaryStatus", lsServiceSummaryStatus,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute("applicationStatus", lbApplicationStatus,
					PortletSession.APPLICATION_SCOPE);
		}
		aoMapToRender.put("applicationStatus", lbApplicationStatus);
		aoMapToRender.put("serviceSummaryStatus", lsServiceSummaryStatus);
		aoMapToRender.put("loBusinessStatusBeanMap", loBusinessStatusBeanMap);
		aoMapToRender.put("loServicesStatusBeanMap", loServicesStatusBeanMap);
	}

	/**
	 * This method sets Application Status.
	 * 
	 * @param asApplicationStatus - Application Status
	 * @param aoChannel - Channel
	 * @param abApplicationStatus - Application Status flag
	 * @return abApplicationStatus - Application Status flag
	 * @throws ApplicationException - throws ApplicationException
	 */
	private Boolean setApplicationStatus(String asApplicationStatus, Channel aoChannel, Boolean abApplicationStatus)
			throws ApplicationException
	{
		if (asApplicationStatus != null
				&& (asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
						|| asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asApplicationStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)))
		{
			String lsProviderStatus = "";
			TransactionManager.executeTransaction(aoChannel, "getProviderStatusFlag");
			WithdrawRequestDetails loWithdrawRequestDetails = (WithdrawRequestDetails) aoChannel
					.getData("providerStatus");
			if (loWithdrawRequestDetails != null)
			{// expired, not applied,
				// withdrawn, approved,
				// conditionally
				// approved, Rejected
				lsProviderStatus = loWithdrawRequestDetails.getMsProviderStatus();
				if (lsProviderStatus != null
						&& !(lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_NOT_APPLIED)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
								|| lsProviderStatus
										.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
								|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED) || lsProviderStatus
									.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED)))
				{
					abApplicationStatus = false;
				}
			}
		}
		else
		{
			abApplicationStatus = false;
		}
		return abApplicationStatus;
	}

	/**
	 * This method calculates the service status.
	 * 
	 * @param asServiceSummaryStatus - Service Summary Status
	 * @param aoIterator - Iterator reference
	 * @return asServiceSummaryStatus - Service Summary Status
	 */
	private String getServiceStatus(String asServiceSummaryStatus, Iterator<StatusBean> aoIterator)
	{
		boolean lbExitFlag = false;
		while (aoIterator.hasNext())
		{
			StatusBean loStatusBeanObj = (StatusBean) aoIterator.next();
			if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
					&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
							ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
			{
				Map<String, String> loSubSectionMap = loStatusBeanObj.getMoHMSubSectionDetailsToDisplay();
				Collection loCollectionRef = loSubSectionMap.values();
				Iterator loItr = loCollectionRef.iterator();
				while (loItr.hasNext())
				{
					String lsSubSecValue = (String) loItr.next();
					if (lsSubSecValue != null
							&& lsSubSecValue.equalsIgnoreCase(ApplicationConstants.STATUS_NOT_STARTED))
					{
						if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							lbExitFlag = true;
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replaceAll(
									" ", "");
						}
					}
					else if (lsSubSecValue != null
							&& lsSubSecValue.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE
								.toLowerCase().replaceAll(" ", "")))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							lbExitFlag = true;
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
						}
					}
				}
				if (lbExitFlag)
				{
					break;
				}
			}
			else
			{
				if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.PARTIALLY_COMPLETE_STATE))
				{
					asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
					break;
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.STATUS_NOT_STARTED))
				{
					if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.COMPLETED_STATE))
				{
					if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE.replaceAll(" ",
							"")))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else
					{
						asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
					}
				}
				else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
						&& loStatusBeanObj.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.DEACTIVATED))
				{
					if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
							&& loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
					{
						asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
						break;
					}
					else if (loStatusBeanObj.getMsSectionStatusToDisplay() != null
							&& loStatusBeanObj.getMsSectionStatus().equalsIgnoreCase("notstarted"))
					{
						if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replaceAll(
									" ", "");
						}
					}
					else if ((loStatusBeanObj.getMsSectionStatusToDisplay() != null && loStatusBeanObj
							.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
							&& (loStatusBeanObj.getMoHMSubSectionDetailsToDisplay().get("servicesetting") != null && !loStatusBeanObj
									.getMoHMSubSectionDetailsToDisplay().get("servicesetting")
									.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)))
					{
						if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE.replaceAll(
								" ", "")))
						{
							asServiceSummaryStatus = ApplicationConstants.DRAFT_STATE.toLowerCase();
							break;
						}
						else
						{
							asServiceSummaryStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase();
						}
					}
				}
			}
		}
		return asServiceSummaryStatus;
	}

	/**
	 * This method is used to show the service to the add services page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @throws ApplicationException application exception
	 */
	private void getShowServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName) throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		// Execute the transaction name obtained from the file AddService.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		// taxonomy tree to for the service
		String loTaxonomyTree = (String) loChannel.getData("loTaxonomyTree");
		// get the already saved services from the database
		List<TaxonomyServiceBean> loSelectedServiceListDB = (List<TaxonomyServiceBean>) loChannel
				.getData("saveServicesList");
		ApplicationSession.setAttribute(loSelectedServiceListDB, aoRequest, "loSelectedServiceListDB");
		// get all the services to display on the add service page
		ApplicationSession.setAttribute(loTaxonomyTree, aoRequest, "finalTreeAsString");
	}

	/**
	 * This method is used to show the service to the add services page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @throws ApplicationException application exception
	 */
	private void getShowServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, final String asAction, final String asUserId, final RenderRequest aoRequest,
			final String asSectionName, final String asSubSectionName) throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		// Execute the transaction name obtained from the file AddService.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		// taxonomy tree to for the service
		String loTaxonomyTree = (String) loChannel.getData("loTaxonomyTree");
		// get the already saved services from the database
		List<TaxonomyServiceBean> loSelectedServiceListDB = (List<TaxonomyServiceBean>) loChannel
				.getData("saveServicesList");
		ApplicationSession.setAttribute(loSelectedServiceListDB, aoRequest, "loSelectedServiceListDB");
		// get all the services to display on the add service page
		ApplicationSession.setAttribute(loTaxonomyTree, aoRequest, "finalTreeAsString");
	}

	/**
	 * This method is used to save the related service after the complete
	 * selection from the add service page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private String doSaveRelatedServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asApplicationId, PortletSession aoPortletSession) throws ApplicationException
	{
		// get the selected services value
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.SAVE_TRANSACTION);
		loChannel.setData("lsApplicationId", asApplicationId);
		// Execute the transaction name obtained from the file
		// AddService.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		asAction = "checkForService";
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION, "servicessummary");
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION, "summary");
		return asAction;
	}

	/**
	 * This method is used to save the related service after the complete
	 * selection from the add service page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * 
	 * @throws ApplicationException application exception
	 */
	private String doSaveServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asApplicationId) throws ApplicationException
	{
		// get the selected services value
		final String lsSelectedService = aoRequest.getParameter("selectedService");
		if (lsSelectedService == null || lsSelectedService.isEmpty())
		{
			// if no services is found from the database and jsp display an
			// error message
			aoResponse.setRenderParameter("error_msg", "Please select atleast one service");
			asAction = "showServices";
		}
		else
		{
			PortletSession loPortletSession = aoRequest.getPortletSession();
			Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction,
					asUserId, aoRequest, null);
			loChannel.setData("lsApplicationId", asApplicationId);
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.SAVE_TRANSACTION);
			LOG_OBJECT.Debug("APPLICATION ID set in channel before calling Transaction: " + lsTransactionName
					+ " , APPLICATION ID: " + asApplicationId);
			// Execute the transaction name obtained from the file
			// AddService.java
			if (loPortletSession.getAttribute("addApplicationIdAfterSubmission") != null
					&& (Boolean) loPortletSession.getAttribute("addApplicationIdAfterSubmission"))
			{
				loChannel.setData("aoAppId", asApplicationId);
				loChannel.setData("asOrgId", asOrgId);
				loChannel.setData("asUserID", asUserId);
				loChannel.setData("aoUpdatedBy", asUserId);
				loChannel.setData("aoUpdatedDate", (new java.sql.Date(System.currentTimeMillis()).toString()));
				lsTransactionName = "aoInsertUpdateDeleteExecutionwithApplication";
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			asAction = "showsimilarServices";
			loPortletSession.removeAttribute("addApplicationIdAfterSubmission");
		}
		return asAction;
	}

	/**
	 * This method is used to save the service setting
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private String doSaveServiceSetting(final BusinessApplication aoBrAppObj, final String asSectionName,
			final String asSubSectionName, final String asUserId, final String asServiceAppId, final String asOrgId,
			final String asBusnessAppId, String asAction, final ActionRequest aoRequest,
			final ActionResponse aoResponse, final String asServiceSetting) throws ApplicationException
	{

		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, null, null, asAction,
				null, aoRequest, asServiceSetting);
		if (null != loChannel.getData("errorMsg")
				&& !asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
		{
			aoResponse.setRenderParameter("errorMsg", (String) loChannel.getData("errorMsg"));
			asAction = "save";
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		}
		else if (asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
		{
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK;
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		}
		else
		{
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.SAVE_TRANSACTION);
			loChannel.setData(ApplicationConstants.APP_ID, asBusnessAppId);
			loChannel.setData("asSection", asSectionName);
			loChannel.setData("asUserID", asUserId);
			loChannel.setData("asServiceAppId", asServiceAppId);
			// Execute the transaction name obtained from the file
			// ServiceSetting.java
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
		}
		return asAction;
	}

	/**
	 * This method is used to save the service specilization setting
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private String doSaveServiceSpecilization(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId) throws ApplicationException
	{

		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, null, null, asAction,
				null, aoRequest, ApplicationConstants.SPECIALIZATION);
		if (null != loChannel.getData("errorMsg")
				&& !asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
		{
			aoResponse.setRenderParameter("errorMsg", (String) loChannel.getData("errorMsg"));
			asAction = "save";
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		}
		else if (asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
		{
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK;
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		}
		else
		{
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.SAVE_TRANSACTION);
			loChannel.setData(ApplicationConstants.APP_ID, asBusnessAppId);
			loChannel.setData("asSection", asSectionName);
			loChannel.setData("asUserID", asUserId);
			loChannel.setData("asServiceAppId", asServiceAppId);
			// Execute the transaction name obtained from the file
			// Specialization.java
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
		}
		return asAction;
	}

	/**
	 * This method is used to get the contract information when user select the
	 * existing contract from the drop down
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private void doSelectExistingContract(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse)
			throws ApplicationException
	{

		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
		if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
		{
			loReqServiceInfo.put("serviceType",
					(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
			loChannel.setData("reqServiceInfo", loReqServiceInfo);
		}
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loIsdeactivated = (Boolean) loChannel.getData("deactivatedService");
		if (loIsdeactivated != null)
		{
			ApplicationSession.setAttribute(loIsdeactivated, aoRequest, "deactivatedService");
		}
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		ApplicationSession.setAttribute(loServiceCommentsList, aoRequest, "serviceComments");
		List<ContractDetails> loContractList = (List<ContractDetails>) loChannel.getData("allContractDetailsOutput");
		List<NYCAgency> loAgencyList = (List<NYCAgency>) loChannel.getData("allNYCAgencyDetailsOutput");
		ContractDetails loReqContract = (ContractDetails) loChannel.getData("contractDetailsOutput");
		String lsContractId = aoRequest.getParameter("selectBoxValue");
		aoRequest.setAttribute("selectedContract", lsContractId);
		ApplicationSession.setAttribute(loContractList, aoRequest, "getValue");
		ApplicationSession.setAttribute(loAgencyList, aoRequest, "getNYCAgency");
		ApplicationSession.setAttribute(loReqContract, aoRequest, "reqContract");
	}

	/**
	 * This method is used to get the staff information when user select the
	 * existing staff from the drop down
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private void doSelectExistingStaff(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");

		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		Map<String, String> loReqServiceInfo = (Map<String, String>) loChannel.getData("reqServiceInfo");
		if (ApplicationSession.getAttribute(aoRequest, true, "serviceType") != null)
		{
			loReqServiceInfo.put("serviceType",
					(String) ApplicationSession.getAttribute(aoRequest, true, "serviceType"));
			loChannel.setData("reqServiceInfo", loReqServiceInfo);
		}
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loIsServiceDeactivated = (Boolean) loChannel.getData("deactivatedService");
		if (loIsServiceDeactivated != null)
		{
			ApplicationSession.setAttribute(loIsServiceDeactivated, aoRequest, "deactivatedService");
		}
		List<Map<String, Object>> loServiceCommentsList = (List<Map<String, Object>>) loChannel
				.getData("serviceCommentsInfo");
		ApplicationSession.setAttribute(loServiceCommentsList, aoRequest, "serviceComments");
		String lsStaffId = aoRequest.getParameter("selectBoxValue");
		List<StaffDetails> loStaffList = (List<StaffDetails>) loChannel.getData("allStaffDetailsOutput");
		StaffDetails loReqStaff = (StaffDetails) loChannel.getData("staffDetailsOutput");
		aoRequest.setAttribute("getValue", loStaffList);
		aoRequest.setAttribute("reqStaff", loReqStaff);
		aoRequest.setAttribute("selectedStaff", lsStaffId);
		ApplicationSession.setAttribute(loReqStaff, aoRequest, "reqStaff");
		ApplicationSession.setAttribute(loStaffList, aoRequest, "getValue");
	}

	/**
	 * This method is used to delete from the contract information when user
	 * select the contract from the drop down
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private void doDeleteContractInfo(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "showServiceQuestion");
		ApplicationSession.setAttribute("yes", aoRequest, "contractSel");
	}

	/**
	 * This method is used to delete from the contract information when user
	 * select the contract from the drop down
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @throws ApplicationException application exception
	 */
	private void doDeleteStaffInfo(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, null, null, asAction, asUserId,
				aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "showServiceQuestion");
		ApplicationSession.setAttribute("yes", aoRequest, "staffSel");
	}

	/**
	 * This method is used to save and update the contract information into the
	 * system
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private void doSaveUpdateContractInfo(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId) throws ApplicationException
	{
		Channel loChannel = null;
		String lsTransactionName = null;
		{
			aoRequest.setAttribute("checkForId", "save");
			aoResponse.setRenderParameter("contractIdExist", "false");
			aoRequest.setAttribute("contractIdExist", false);
			loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, asServiceAppId, null, asAction,
					asUserId, aoRequest, null);
			lsTransactionName = (String) loChannel.getData("transaction_name");
			ContractDetails loContractDetails = (ContractDetails) loChannel.getData("newContract");
			if (loContractDetails == null)
			{
				loContractDetails = (ContractDetails) loChannel.getData("existingContract");
			}
			// Execute the transaction name obtained from the file
			// ServiceQuestion.java
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, asServiceAppId, HHSR5Constants.SERVICE_APPLICATION);
			// End R5 : set EntityId and EntityName for AutoSave

			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			Boolean lbUpsertStatus = (Boolean) loChannel.getData("contractInsertStatus");

			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "showServiceQuestion");
			ApplicationSession.setAttribute("yes", aoRequest, "contractSel");
			if (!lbUpsertStatus)
			{
				aoResponse.setRenderParameter("contractGrant", aoRequest.getParameter("existingContract"));
				aoResponse.setRenderParameter("contractExists", "true");
				ApplicationSession.setAttribute(loContractDetails, aoRequest, "reqContract");
				aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "addContract");
			}
		}
	}

	/**
	 * This method is used to save and update the contract information into the
	 * system
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private void doSaveUpdateStaffInfo(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId) throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(null, asOrgId, asBusnessAppId, asServiceAppId, null, asAction,
				asUserId, aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction name obtained from the file
		// ServiceQuestion.java
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		Boolean loIsExisingStaff = false;
		if ((Boolean) loChannel.getData("abisExisingStaff"))
		{
			loIsExisingStaff = true;
		}
		if (loChannel.getData("staffInsertStatus") != null && (Boolean) loChannel.getData("staffInsertStatus"))
		{
			if (loChannel.getData("staffInsertStatus") != null)
			{
				loChannel = aoBrAppObj.getChannelObject("getRecentlyAddedStaff", asOrgId, asBusnessAppId,
						asServiceAppId, null, asAction, asUserId, aoRequest, null);
				lsTransactionName = (String) loChannel.getData("transaction_name");
				// Execute the transaction name obtained from the file
				// ServiceQuestion.java
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
			}

			if ((loChannel.getData("reqStaffId") != null) && (!loIsExisingStaff))
			{
				StaffDetails loStaffDetails = (StaffDetails) loChannel.getData("reqStaffId");
				String lsStaffId = loStaffDetails.getMsStaffId();
				loChannel = aoBrAppObj.getChannelObject(lsStaffId, asOrgId, asBusnessAppId, asServiceAppId, null,
						asAction, asUserId, aoRequest, null);
				lsTransactionName = (String) loChannel.getData("transaction_name");
				// Execute the transaction name obtained from the file
				// ServiceQuestion.java
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
			}
			ApplicationSession.setAttribute("yes", aoRequest, "staffSel");
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "showServiceQuestion");
		}
		else if (!(Boolean) loChannel.getData("staffInsertStatus"))
		{
			if (lsTransactionName.equalsIgnoreCase("insertStaffDetails"))
			{
				aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "addStaff");
				StaffDetails loStaffDetails = (StaffDetails) loChannel.getData("newStaff");
				ApplicationSession.setAttribute(loStaffDetails, aoRequest, "currentStaff");
			}
			else
			{
				aoResponse.setRenderParameter("ceoExist", "yes");
				asAction = "selectStaff";
				aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "selectStaff");
				doSelectExistingStaff(aoBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
						asSectionName, asSubSectionName, aoResponse);
				StaffDetails loStaffDetails = (StaffDetails) loChannel.getData("existingStaff");
				loStaffDetails.setMsStaffId(aoRequest.getParameter("existingStaff"));
				loStaffDetails.setMsStaffTitle(aoRequest.getParameter("oldTitle"));
				ApplicationSession.setAttribute(loStaffDetails, aoRequest, "currentStaff");
			}

		}

	}

	/**
	 * This method is used to remove the services from the service summary page
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private Integer doDeleteServices(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId, final String asAppStatus, String asAppId, Integer aoNoOfRemainingServices)
			throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(asAppId, asOrgId, asBusnessAppId, asAppStatus, null, asAction,
				null, aoRequest, null);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName, asAction);
		loChannel.setData("asServiceId", asServiceAppId);
		// Execute the transaction name obtained from navigation xml while
		// adding services in service summary.
		// Adding Filenet session bean in Channel for Defect # 8455 Scenario 1
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession()
		.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT,
				PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSR5Constants.P8USER_SESSION, loUserSession);
		TransactionManager.executeTransaction(loChannel, lsTransactionName);// getSelectedService
		if (loChannel.getData("loServiceSummaryList") != null)
		{
			Map loMap = ((Map) loChannel.getData("loServiceSummaryList"));// NoOfRemainingServices
			if (loMap.get("NoOfRemainingServices") != null)
			{
				aoNoOfRemainingServices = (Integer) loMap.get("NoOfRemainingServices");
			}
		}
		return aoNoOfRemainingServices;
	}

	/**
	 * This method is used to save the service questions for the services
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private void doSaveServicesQuestion(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId, final String asAppStatus) throws ApplicationException
	{
		Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, null, null,
				"saveServiceQuestion", asUserId, aoRequest, null);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		if (lsTransactionName != null)
		{
			// Execute the transaction name obtained from the file
			// ServiceQuestion.java
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
		}
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "showServiceQuestion");
	}

	/**
	 * This method is used to save the data for service withdrawn
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private void doSaveServiceWithdrawn(final BusinessApplication aoBrAppObj, final String asOrgId,
			final String asBusnessAppId, String asAction, final String asUserId, final ActionRequest aoRequest,
			final String asSectionName, final String asSubSectionName, final ActionResponse aoResponse,
			final String asServiceAppId, final String asAppStatus, final String asOrgType) throws ApplicationException
	{
		if (asAction != null && asAction.equalsIgnoreCase("servicewithdraw"))
		{
			Channel loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId, null, null,
					asAction, asUserId, aoRequest, null);
			String lsTransactionName = (String) loChannel.getData("transaction_name");
			// Execute the transaction name obtained from the file
			// ServiceHistory.java
			try
			{
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
			}
			catch (Exception aoAppex)
			{
				aoResponse.setRenderParameter("withdrawErrorMsg",
						"Your withdrawal request could not be submitted at this time. Please try again.");
				throw new ApplicationException("Error while launching Wrokflow." + aoAppex);
			}
			Boolean lbWithdrawFlag = (Boolean) loChannel.getData("withdrawlServUpdateStatus");
			if (lbWithdrawFlag != null && lbWithdrawFlag)
			{

				String lsUrl = null;
				if (asOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
				{
					lsUrl = aoRequest
							.getContextPath()
							.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&"
									+ ApplicationConstants.WORKFLOW_SUCCESS_KEY
									+ "=withdrawTrue&_st=&_windowLabel=portletInstance_12&_urlType=action#wlp_portletInstance_12");
				}
				else
				{
					lsUrl = aoRequest
							.getContextPath()
							.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
									+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=withdrawTrue");
				}
				try
				{
					aoResponse.sendRedirect(lsUrl);
				}
				catch (IOException aoAppEx)
				{
					LOG_OBJECT.Error("Error occured while launching Application Submission Workflow ", aoAppEx);
				}
			}
			else
			{
				aoResponse.setRenderParameter("withdrawErrorMsg",
						"Your withdrawal request could not be submitted at this time. Please try again.");
			}
		}
	}

	/**
	 * Updated in 3.1.0. Added check for Defect 6346 This method performs the
	 * required action, by setting the required values in the channel object and
	 * thereafter executing the transaction.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		try
		{
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsAjaxCall = aoRequest.getParameter(ApplicationConstants.IS_AJAX_CALL);
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			if (lsAction != null && (lsAction.equalsIgnoreCase("save_next")))
			{
				aoResponse.setRenderParameter("saveStaff", "saveStaff");
				ApplicationSession.setAttribute(null, aoRequest, "contractSel");
				ApplicationSession.setAttribute(null, aoRequest, "staffSel");
			}
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);

			final String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			if (lsCityUserSearchProviderId != null && !lsCityUserSearchProviderId.equalsIgnoreCase(""))
			{
				// Updated in 3.1.0. Added check for Defect 6346
				if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
				{
					lsOrgId = lsCityUserSearchProviderId.substring(0,
							lsCityUserSearchProviderId.indexOf(ApplicationConstants.TILD));
				}
				else
				{
					lsOrgId = lsCityUserSearchProviderId;
				}
				// Check for Defect 6346 ends
			}
			if (lsAction != null && (lsAction.equalsIgnoreCase("showServices")))
			{
				String lsNewService = aoRequest.getParameter("newService");
				if (lsNewService != null)
				{
					aoResponse.setRenderParameter("newService", lsNewService);
				}
			}
			String lsServiceAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.SERVICE_APPLICATION_ID);
			aoRequest.setAttribute(ApplicationConstants.SERVICE_APPLICATION_ID, lsServiceAppId);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsApplicationId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_APP_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsAppStatus = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_APP_STATUS, PortletSession.APPLICATION_SCOPE);
			String lsUserRoles = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserNameForAppSubmit = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);

			if (aoRequest.getParameter(ApplicationConstants.BUZ_APP_SUB_SECTION_SUBMIT_BUTTON) != null)
			{
				lsAction = aoRequest.getParameter(ApplicationConstants.BUZ_APP_SUB_SECTION_SUBMIT_BUTTON);
			}

			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			if (lsSectionName == null)
			{
				lsSectionName = aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
				lsSubSectionName = aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			}
			// Service type
			String lsApplicationType = PortalUtil.parseQueryString(aoRequest, "applicationType");
			if (lsApplicationType == null)
			{
				lsApplicationType = (String) loPortletSession.getAttribute("applicationType");
				loPortletSession.setAttribute("applicationType", lsApplicationType);
			}
			else
			{
				loPortletSession.setAttribute("applicationType", lsApplicationType);
			}
			String lsElementId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ELEMENT_ID);
			if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase(""))
			{
				lsBusnessAppId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID);
			}
			if (lsServiceAppId == null || lsServiceAppId.equalsIgnoreCase(""))
			{
				lsServiceAppId = (String) loPortletSession.getAttribute(ApplicationConstants.SERVICE_APPLICATION_ID);
			}
			if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
			{
				lsAppStatus = (String) loPortletSession.getAttribute("bussAppStatus");
			}
			lsApplicationId = (String) loPortletSession.getAttribute("applicationId");
			if (lsApplicationId == null)
			{
				lsApplicationId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_APP_ID,
						PortletSession.APPLICATION_SCOPE);
			}
			LOG_OBJECT.Debug("Retrieved APPLICATION ID from session, Service Selection screen: " + lsApplicationId);
			loPortletSession.setAttribute(ApplicationConstants.SERVICE_APPLICATION_ID, lsServiceAppId);
			loPortletSession.setAttribute("bussAppStatus", lsAppStatus);
			loPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			loPortletSession.setAttribute("applicationId", lsApplicationId);
			String lsOrgType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			Integer loNoOfRemaingServices = 0;
			doActionRequest(aoRequest, aoResponse, lsAjaxCall, lsAction, lsOrgId, lsServiceAppId, loPortletSession,
					lsApplicationId, lsBusnessAppId, lsUserId, lsAppStatus, lsUserRoles, loUserSession,
					lsUserNameForAppSubmit, lsSectionName, lsSubSectionName, lsElementId, lsOrgType,
					loNoOfRemaingServices);
			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in ApplicationServiceController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in ApplicationServiceController", aoEx);
			String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.IS_AJAX_CALL);
			String lsErrorMsg = aoEx.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			if (lsErrorMsg.contains("~"))
			{
				lsErrorMsg = lsErrorMsg.replace("~", ":");
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoEx);
			if (null != lsAjaxCall && lsAjaxCall.equalsIgnoreCase("true"))
			{
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
				try
				{
					aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
				}
				catch (IOException ioEx)
				{
					LOG_OBJECT.Error("Messaging Exception occurred during file info action", ioEx);
				}
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while execution of action Method in BusinessSummaryController", aoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method manages the action performed by the controller.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAjaxCall - Ajax Call
	 * @param asAction - Action
	 * @param asOrgId - Org Id
	 * @param asServiceAppId - Service App Id
	 * @param aoPortletSession - PortletSession
	 * @param asApplicationId - Application Id
	 * @param asBusnessAppId - Busness App Id
	 * @param asUserId - User Id
	 * @param asAppStatus - App Status
	 * @param asUserRoles - User Roles
	 * @param aoUserSession P8UserSession
	 * @param asUserNameForAppSubmit - User Name For App Submit
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param asElementId - Element Id
	 * @param asOrgType - Org Type
	 * @param aoNoOfRemaingServices - No Of Remaing Services
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void doActionRequest(ActionRequest aoRequest, ActionResponse aoResponse, String asAjaxCall,
			String asAction, String asOrgId, String asServiceAppId, PortletSession aoPortletSession,
			String asApplicationId, String asBusnessAppId, String asUserId, String asAppStatus, String asUserRoles,
			P8UserSession aoUserSession, String asUserNameForAppSubmit, String asSectionName, String asSubSectionName,
			String asElementId, String asOrgType, Integer aoNoOfRemaingServices) throws ApplicationException
	{
		LOG_OBJECT.Debug("doActionRequest");
		String lsServiceName;
		if (asElementId != null && asAction != null && !(asAction.equalsIgnoreCase("servicewithdraw")))
		{
			if (!asAction.equalsIgnoreCase("removeService") && !asAction.equalsIgnoreCase("fileinformation"))
			{
				aoResponse.setRenderParameter(ApplicationConstants.ELEMENT_ID, asElementId);
			}
			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			if (loDoc != null)
			{
				lsServiceName = BusinessApplicationUtil.getTaxonomyName(asElementId, loDoc);
				// code added for deleted service name
				if (null == lsServiceName || lsServiceName.equalsIgnoreCase(""))
				{
					Map<String, String> loActionMap = new HashMap<String, String>();
					loActionMap.put("lsElementId", asElementId);
					Channel loChannel = new Channel();
					loChannel.setData("loActionMap", loActionMap);
					TransactionManager.executeTransaction(loChannel, "getDeletedServiceName");
					lsServiceName = (String) loChannel.getData("serviceName");
				}
				// code added for deleted service name
				aoRequest.setAttribute("serviceName", lsServiceName);
			}
		}
		LOG_OBJECT.Debug("10 before get business application section::" + asSectionName + " Subsection::"
				+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
		BusinessApplication loBrAppObj = BusinessApplicationFactory.getBusinessApplication(asSectionName,
				asSubSectionName);
		if (asAction != null && asAction.equalsIgnoreCase("cancelStaff"))
		{
			ApplicationSession.setAttribute("yes", aoRequest, "staffSel");
			asAction = "showServiceQuestion";
		}
		else if (asAction != null && asAction.equalsIgnoreCase("cancelContract"))
		{
			ApplicationSession.setAttribute("yes", aoRequest, "contractSel");
			asAction = "showServiceQuestion";
		}
		else if (asAction != null && asAction.equalsIgnoreCase("showServices"))
		{
			getShowServices(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("saveRelatedServices"))
		{
			asAction = doSaveRelatedServices(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, aoResponse, asApplicationId, aoPortletSession);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("saveServices"))
		{
			asAction = doSaveServices(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, aoResponse, asApplicationId);
		}
		else if (asSubSectionName != null && asSubSectionName.endsWith("servicesetting"))
		{
			asAction = doSaveServiceSetting(loBrAppObj, asSectionName, asSubSectionName, asUserId, asServiceAppId,
					asOrgId, asBusnessAppId, asAction, aoRequest, aoResponse, ApplicationConstants.SERVICE_SETTING);
		}
		else if (asSubSectionName != null && asSubSectionName.endsWith("specialization"))
		{
			asAction = doSaveServiceSpecilization(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest,
					asSectionName, asSubSectionName, aoResponse, asServiceAppId);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("showsimilarServices"))
		{
		}
		else if (asAction != null && asAction.equalsIgnoreCase("selectValue"))
		{
			doSelectExistingContract(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("delSelectValue"))
		{
			doDeleteContractInfo(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("selectStaff"))
		{
			doSelectExistingStaff(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("delSelectStaff"))
		{
			doDeleteStaffInfo(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("save"))
		{
			doSaveUpdateContractInfo(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse, asServiceAppId);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("saveStaff"))
		{
			doSaveUpdateStaffInfo(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse, asServiceAppId);
		}
		else if (asAction != null && asAction.equalsIgnoreCase("removeService"))
		{
			aoNoOfRemaingServices = doDeleteServices(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId,
					aoRequest, asSectionName, asSubSectionName, aoResponse, asServiceAppId, asAppStatus,
					asApplicationId, aoNoOfRemaingServices);
		}
		else if (asAction != null
				&& !asAction.equalsIgnoreCase("showServiceQuestion")
				&& (asAction.equalsIgnoreCase("saveServiceQuestion") || asSubSectionName
						.equals(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)))
		{
			doSaveServicesQuestion(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse, asServiceAppId, asAppStatus);
		}
		else if (null != asSubSectionName && asSubSectionName.endsWith("servicehistory"))
		{
			doSaveServiceWithdrawn(loBrAppObj, asOrgId, asBusnessAppId, asAction, asUserId, aoRequest, asSectionName,
					asSubSectionName, aoResponse, asServiceAppId, asAppStatus, asOrgType);
		}
		else if (null != asSubSectionName && asSubSectionName.endsWith("applicationSubmit"))
		{  
			doSaveApplicationSubmit(aoRequest, aoResponse, asAction, asOrgId, asServiceAppId, asBusnessAppId, asUserId,
					asAppStatus, asUserNameForAppSubmit, asSectionName, loBrAppObj);
		}
		else if (null != asSubSectionName
				&& asSubSectionName.endsWith(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
		{
			asAction = doSaveBusinessDocuments(aoRequest, aoResponse, asAjaxCall, asAction, asOrgId, asBusnessAppId,
					asUserId, asAppStatus, asUserRoles, aoUserSession, asSectionName, asSubSectionName);
		}
		if (asSubSectionName != null
				&& asAction != null
				&& !asAction.equalsIgnoreCase("checkForService")
				&& ((asSubSectionName.equalsIgnoreCase("similarservice") && !asAction.equals("back")) || (asAction
						.equalsIgnoreCase("removeService") && aoNoOfRemaingServices == 0))
				&& aoPortletSession.getAttribute("headerPostService", PortletSession.APPLICATION_SCOPE) != null)
		{
			redirectToSummary(aoRequest, aoResponse);
		}
		else if (asAction != null
				&& !(asAction.equalsIgnoreCase("applicationSubmit") || asAction.equalsIgnoreCase("servicewithdraw")))
		{
			settingRenderParameter(aoResponse, asAction, asServiceAppId, asBusnessAppId, asSectionName);
		}
	}

	/**
	 * This method sets Render Parameter.
	 * 
	 * @param aoResponse - ActionResponse
	 * @param asAction - Action
	 * @param asServiceAppId - Service App Id
	 * @param asBusnessAppId - Busness App Id
	 * @param asSectionName - Section Name
	 */
	private void settingRenderParameter(ActionResponse aoResponse, String asAction, String asServiceAppId,
			String asBusnessAppId, String asSectionName)
	{
		if (asAction != null
				&& ((!asAction.equals("save") && !asAction.equals("saveStaff")
						&& !asAction.equalsIgnoreCase("fileinformation")
						&& !asAction.equalsIgnoreCase("saveServiceQuestion") && !asAction
							.equalsIgnoreCase("submitDocId"))
						&& !asAction.equalsIgnoreCase("fileupload")
						&& !asAction.equalsIgnoreCase("removeDocFromApplication")
						&& !asAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO)
						&& !asAction.equalsIgnoreCase("displayDocProp")
						&& !asAction.equalsIgnoreCase("delSelectValue")
						&& !asAction.equalsIgnoreCase("delSelectStaff") || (asAction.equals("back") && asSectionName
						.equals("servicessummary"))))
		{
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
			if (asBusnessAppId != null)
			{
				aoResponse.setRenderParameter(ApplicationConstants.KEY_BUSINESS_APP_ID, asBusnessAppId);
			}
			if (asServiceAppId != null)
			{
				aoResponse.setRenderParameter(ApplicationConstants.SERVICE_APPLICATION_ID, asServiceAppId);
			}
		}
	}

	/**
	 * This method redirect the control to the summary page.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void redirectToSummary(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		try
		{
			String lsUrl = aoRequest.getScheme()
					+ "://"
					+ aoRequest.getServerName()
					+ ":"
					+ aoRequest.getServerPort()
					+ aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL
					+ "&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider";
			aoResponse.sendRedirect(lsUrl);
		}
		catch (IOException aoAppex)
		{
			throw new ApplicationException(
					"Not able to dedirect to Business Summary screen after adding related service", aoAppex);
		}
	}

	/**
	 * This method is used to save the document for business application Updated
	 * for release 3.3.0, Defect 6451
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private String doSaveBusinessDocuments(final ActionRequest aoRequest, final ActionResponse aoResponse,
			final String asAjaxCall, String asAction, final String asOrgId, final String asBusnessAppId,
			final String asUserId, final String asAppStatus, final String asUserRoles,
			final P8UserSession aoUserSession, final String asSectionName, final String asSubSectionName)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("11 before get business application section::" + asSectionName + " Subsection::"
				+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
		BusinessApplication loBusiness = BusinessApplicationFactory.getBusinessApplication(asSectionName,
				asSubSectionName);
		String lsFilePath = null;
		// Open the overlay when we upload document from the drop down on basic,
		// board, filings and policies form for service summary.
		if (!("fileinformation".equalsIgnoreCase(asAction) || "fileupload".equalsIgnoreCase(asAction) || "backrequest"
				.equalsIgnoreCase(asAction)))
		{
			asAction = openOverlayForFileUpload(aoRequest, aoResponse, asAction, asOrgId, asBusnessAppId, asUserId,
					asAppStatus, asUserRoles, asSectionName, loBusiness, lsFilePath);
		}
		else
		{ // Second overlay while uploading document from the drop down.
			if ("fileinformation".equalsIgnoreCase(asAction))
			{
				try
				{
					String lsDoccategory = aoRequest.getParameter(ApplicationConstants.GET_DOCUMENT_CATEGORY);
					String lsDocType = aoRequest.getParameter(ApplicationConstants.DOC_TYPE_NODE);
					String lsFormName = aoRequest.getParameter("formName");
					String lsFormVer = aoRequest.getParameter("formVersion");
					String lsSerAppId = aoRequest.getParameter("service_app_id");
					String lsSecId = aoRequest.getParameter("section_id");
					ApplicationSession.setAttribute(lsFormName, aoRequest, "form_name");
					ApplicationSession.setAttribute(lsFormVer, aoRequest, "form_version");
					ApplicationSession.setAttribute(lsSerAppId, aoRequest, "service_app_id");
					ApplicationSession.setAttribute(lsSecId, aoRequest, "section_id");
					ApplicationSession.setAttribute(lsDoccategory, aoRequest, "document_category");
					ApplicationSession.setAttribute(lsDocType, aoRequest, "document_type");
					FileNetOperationsUtils.actionFileInformation(aoRequest, aoResponse);
				}
				catch (PortletException aoAppex)
				{
					LOG_OBJECT.Error("Error occurred", aoAppex);
				}
				catch (MessagingException aoAppex)
				{
					LOG_OBJECT.Error("Error occurred", aoAppex);
				}
				catch (IOException aoAppex)
				{
					LOG_OBJECT.Error("Error occurred", aoAppex);
				}
			}
			// First overlay while uploading document from the drop down.
			if ("fileupload".equalsIgnoreCase(asAction))
			{
				String lsEntityId = (String) aoRequest.getPortletSession().getAttribute("entityId");// Defect
																									// #1805
																									// fix
				doFileUpload(aoRequest, aoResponse, asAjaxCall, asOrgId, asBusnessAppId, asUserId, aoUserSession,
						asSectionName, lsEntityId);
			}
			// Updated for release 3.3.0, Defect 6451 Start
			if ("backrequest".equalsIgnoreCase(asAction))
			{
				BusinessApplicationUtil.backRequestAction(aoRequest);
			}
			// Updated for release 3.3.0, Defect 6451 End
		}
		return asAction;
	}

	/**
	 * This method do File Upload.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAjaxCall - Ajax Call
	 * @param asOrgId - Org Id
	 * @param asBusnessAppId - Busness App Id
	 * @param asUserId - User Id
	 * @param aoUserSession - P8UserSession
	 * @param asSectionName - Section Name
	 */
	private void doFileUpload(final ActionRequest aoRequest, final ActionResponse aoResponse, final String asAjaxCall,
			final String asOrgId, final String asBusnessAppId, final String asUserId,
			final P8UserSession aoUserSession, final String asSectionName, final String asEntityId)
	{
		Channel loChannel;
		try
		{ // Entry into document table for business application.
			String lsDocumentId = FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
			if (null != lsDocumentId && !lsDocumentId.equals(""))
			{
				com.nyc.hhs.model.Document loDocument = (com.nyc.hhs.model.Document) ApplicationSession.getAttribute(
						aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
				loChannel = new Channel();
				loChannel.setData("applicationId", asBusnessAppId);
				loChannel.setData(HHSR5Constants.DOC_ID, lsDocumentId);
				loChannel.setData("documentCategory", loDocument.getDocCategory());
				loChannel.setData("documentType", loDocument.getDocType());
				loChannel.setData("formName", loDocument.getFormName());
				loChannel.setData("formVersion", loDocument.getFormVersion());
				loChannel.setData("organizationId", asOrgId);
				loChannel.setData("userId", asUserId);
				loChannel.setData("asSection", asSectionName);
				loChannel.setData("docName", loDocument.getDocName());
				loChannel.setData("lastModifiedBy", asUserId);
				loChannel.setData("lastModifiedDate", DateUtil.getCurrentDate());
				loChannel.setData("submissionBy", asUserId);
				loChannel.setData("submissionDate", DateUtil.getCurrentDate());
				loChannel.setData("asServiceAppId", loDocument.getServiceAppID());
				loChannel.setData("sectionId", loDocument.getSectionId());
				loChannel.setData("asSubSectionIdNextTab", loDocument.getSectionId());
				loChannel.setData("asUserId", asUserId);
				loChannel.setData("asEntityId", asEntityId);// Defect #1805 fix
				HashMap loHmDocReqProps = new HashMap();
				loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
				loChannel.setData("hmReqProps", loHmDocReqProps);
				loChannel.setData("aoFilenetSession", aoUserSession);
				TransactionManager.executeTransaction(loChannel, "checkForDocId");
				String lsGetdocIdForDocType = null;
				lsGetdocIdForDocType = (String) loChannel.getData("lsGetdocIdForDocType");
				HashMap loHmDocReqProperties = new HashMap();
				loHmDocReqProperties.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
				loChannel.setData("asGetdocIdForDocType", lsGetdocIdForDocType);
				loChannel.setData("loHmDocReqProperties", loHmDocReqProperties);
				String lsIsLinkToApp = (String) aoRequest.getParameter("isLinkToApp");
				if (lsGetdocIdForDocType != null && !(lsIsLinkToApp != null && lsIsLinkToApp.equalsIgnoreCase("true")))
				{
					TransactionManager.executeTransaction(loChannel, "saveDocumentProperties_bapp");
				}
				if (!loDocument.getSectionId().equalsIgnoreCase("servicessummary") || loDocument.getSectionId() == null)
				{ // Execute the
					// transaction
					// to update
					// document
					// table
					// for business
					// application
					// while
					// uploading
					// file.
					TransactionManager.executeTransaction(loChannel, "fileupload_bapp");
				}
				else if (loDocument.getSectionId().equalsIgnoreCase("servicessummary")
						&& loDocument.getSectionId() != null)
				{ // Execute the
					// transaction
					// to update
					// document
					// table
					// for business
					// service
					// application
					// while
					// uploading
					// file.
					TransactionManager.executeTransaction(loChannel, "fileupload_bappServiceSummary");
				}
				ApplicationSession.setAttribute("The file was successfully uploaded to your Document Vault.",
						aoRequest, "message");
				ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_PASS_TYPE, aoRequest, "messageType");
			}
		}
		catch (ApplicationException aoExp)
		{
			try
			{
				LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
				String lsErrorMsg = aoExp.getMessage();
				if (null != asAjaxCall && asAjaxCall.equalsIgnoreCase("true"))
				{
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
							PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
					aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
				}
			}
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
		catch (IOException aoIoExp)
		{
			LOG_OBJECT.Error("IOException during file upload", aoIoExp);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
		}
	}

	/**
	 * This method opens the overlay.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAction - Action
	 * @param asOrgId - OrgId
	 * @param asBusnessAppId - BusnessAppId
	 * @param asUserId - UserId
	 * @param asAppStatus - AppStatus
	 * @param asUserRoles - UserRoles
	 * @param asSectionName - SectionName
	 * @param aoBusiness - Business
	 * @param asFilePath - FilePath
	 * @return asAction - asAction
	 * @throws ApplicationException
	 */
	private String openOverlayForFileUpload(final ActionRequest aoRequest, final ActionResponse aoResponse,
			String asAction, final String asOrgId, final String asBusnessAppId, final String asUserId,
			final String asAppStatus, final String asUserRoles, final String asSectionName,
			BusinessApplication aoBusiness, String asFilePath) throws ApplicationException
	{    
		LOG_OBJECT.Debug("openOverlayForFileUpload");
		Channel loChannel;
		loChannel = aoBusiness.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, asFilePath,
				asAction, asUserRoles, aoRequest, null);
		String lsXmlPath = null;
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (loChannel != null)
		{
			String lsTransactionName = null;
			if (loChannel.getData("errorpage") != null)
			{
				aoResponse.setRenderParameter("errorpage", "errorpage");
				asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE;
			}
			else if (loChannel.getData("transaction_name") != null)
			{
				HashMap loHmProps = null;
				loHmProps = (HashMap) loChannel.getData("hmReqProps");
				loHmProps.put("identificationKey", asAction);
				loChannel.setData("hmReqProps", loHmProps);
				lsTransactionName = (String) loChannel.getData("transaction_name");
				// Added for release 5
				lsXmlPath = (String) loChannel.getData("xmlNameForTransaction");
				// Added for release 5
				// Execute the transaction name obtained from the file
				// DocumentBusinessApp.java
				// Added for Release 5 - selectVault - transaction from
				// transactionDV.xml
				
				if (null != lsXmlPath && !lsXmlPath.isEmpty())
				{
					loChannel.setData("aoUserId", lsUserId);
					TransactionManager.executeTransaction(loChannel, "displayDocList_filenet", lsXmlPath);
				}
				else
				{
					TransactionManager.executeTransaction(loChannel, lsTransactionName);
				}

			}
		}
		if (null != asAction && asAction.equals("displayDocProp"))
		{
			aoResponse.setRenderParameter("next_action", "open");
		}
		// Overlay while selecting document from the vault from the drop
		// down.
		else if (null != asAction && asAction.equals("selectDocFromVault"))
		{
			Object loDocResultObject = loChannel.getData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER);
			// List of Document from Document Vault.
			if (loDocResultObject != null)
			{
				List loDocResult = (List) loDocResultObject;
				List<DocumentsSelFromDocVault> loLSelectedDocFromVault = new ArrayList<DocumentsSelFromDocVault>();
				Iterator loIterator = loDocResult.iterator();
				String lsFormName = aoRequest.getParameter("formName");
				String lsFormVesrion = aoRequest.getParameter("formVersion");
				String lsFormId = aoRequest.getParameter("formId");
				String lsLastModifiedBy = FileNetOperationsUtils.getUserName(asUserId);
				while (loIterator.hasNext())
				{
					HashMap loHMDocProps = (HashMap) loIterator.next();
					DocumentsSelFromDocVault loHMDocumentsObject = new DocumentsSelFromDocVault();
					if (loHMDocProps != null)
					{
						loHMDocumentsObject.setMsDocumentName((String) loHMDocProps
								.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
						loHMDocumentsObject.setMsLastModifiedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps
										.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE))))));
						loHMDocumentsObject.setMsLastModifiedBy((String) lsLastModifiedBy);
						loHMDocumentsObject.setMsDocumentId((Id) loHMDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID));
						loHMDocumentsObject.setMsSubmittedBy((String) loHMDocProps
								.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
						loHMDocumentsObject.setMsSubmittedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps
										.get(P8Constants.PROPERTY_CE_DATE_CREATED))))));
						if ((loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE) != null)
								&& ((loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE).toString()
										.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_CFO))
										|| (loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_TYPE).toString()
												.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_CEO)) || (loHMDocProps
										.get(P8Constants.PROPERTY_CE_DOC_TYPE).toString()
											.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_KEYSTAFF_RESUME))))
						{
							loHMDocumentsObject.setMsDocumnetType((String) loHMDocProps
									.get(P8Constants.PROPERTY_CE_DOC_TYPE));
						}
						else
						{
							loHMDocumentsObject.setMsDocumnetType(aoRequest.getParameter("docType"));
						}
						// Added for release 5
						if (null != aoRequest.getParameter("docCategory"))
							loHMDocumentsObject.setMsDocumnetCategory(aoRequest.getParameter("docCategory"));
						else if (null != loHMDocProps.get(P8Constants.PROPERTY_CE_DOC_CATEGORY))
							loHMDocumentsObject.setMsDocumnetCategory((String) loHMDocProps
									.get(P8Constants.PROPERTY_CE_DOC_CATEGORY));
						// Added for release 5
						loHMDocumentsObject.setMsFormName((String) lsFormName);
						loHMDocumentsObject.setMsFormVersion((String) lsFormVesrion);
						loHMDocumentsObject.setMsUserId((String) asUserId);
						loHMDocumentsObject.setMsDocumentTitle((String) loHMDocProps.get(P8Constants.DOCUMENT_TITLE));
						loHMDocumentsObject.setMsFormId((String) lsFormId);
						loHMDocumentsObject.setMsServiceAppId((String) aoRequest.getParameter("serviceAppID"));
						loHMDocumentsObject.setMsSectionId((String) aoRequest.getParameter("sectionId"));
						// Added for release 5
						String lsParent = (String) loHMDocProps.get(HHSR5Constants.FOLDERS_FILED_IN);
						if (null != lsParent && !lsParent.isEmpty())
						{
							loHMDocumentsObject.setFilePath(lsParent);
						}
						// Added for release 5
					}
					loLSelectedDocFromVault.add(loHMDocumentsObject);
				}
				ApplicationSession.setAttribute(loLSelectedDocFromVault, aoRequest, "document_list_fromvault");
				RFPReleaseDocsUtil.setReqRequestParameter(aoRequest.getPortletSession(), 
						(P8UserSession)loChannel.getData(HHSConstants.AO_FILENET_SESSION), (String)loChannel.getData(HHSConstants.NEXT_PAGE_PARAM));
				ApplicationSession.setAttribute(loChannel.getData(HHSConstants.OBJECTS_PER_PAGE), aoRequest,ApplicationConstants.ALLOWED_OBJECT_COUNT);
			}
			// Added null check for select vault
			if (null != aoRequest.getParameter("docType") && !aoRequest.getParameter("docType").isEmpty())
			{
				aoResponse.setRenderParameter("docType", aoRequest.getParameter("docType"));

			}
		}
		// Submit the selected document from the list of document from
		// vault.
		else if (null != asAction && asAction.equals("submitDocId"))
		{
			aoResponse.setRenderParameter("next_action", "open");
		}
		// Remove the document entry for a given document type.
		else if (null != asAction && asAction.equals("removeDocFromApplication"))
		{
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "open");
			ApplicationSession.setAttribute("The document was successfully removed from service application.",
					aoRequest, "message");
			ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_PASS_TYPE, aoRequest, "messageType");
		}
		/*
		 * When user selects view document information from drop down it calls
		 * actionViewDocumentInfo(Request,Response) method
		 */
		else if (null != asAction && asAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
		{
			// Added for Release 5- Parameters for viewing document property
			String lsDoccategory = aoRequest.getParameter("asDocCat");
			String lsDocType = aoRequest.getParameter("docType");
			ApplicationSession.setAttribute(lsDoccategory, aoRequest, "docCatHidden");
			ApplicationSession.setAttribute(lsDocType, aoRequest, "docTypeHidden");
			// Release 5 end
			aoRequest.setAttribute("orgType", ApplicationConstants.PROVIDER_ORG);
			FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.VIEW_DOCUMENT_INFO);
			aoRequest.removeAttribute("orgType");
		}
		return asAction;
	}

	/**
	 * This method is used to save the data once application is submitted for
	 * business and service application
	 * 
	 * @param aoBrAppObj business application factory object
	 * @param asOrgId organization id
	 * @param asBusnessAppId business application id
	 * @param asAction action coming from jsp
	 * @param asUserId user id
	 * @param aoRequest request object
	 * @param asSectionName section name
	 * @param asSubSectionName subsection name
	 * @param aoResponse response object
	 * @param asServiceAppId service application id
	 * @throws ApplicationException application exception
	 */
	private synchronized void doSaveApplicationSubmit(final ActionRequest aoRequest, final ActionResponse aoResponse,
			final String asAction, final String asOrgId, final String asServiceAppId, final String asBusnessAppId,
			final String asUserId, final String asAppStatus, final String asUserNameForAppSubmit,
			final String asSectionName, final BusinessApplication aoBrAppObj) throws ApplicationException
	{
		Map<String, Object> loMapToRender = new HashMap<String, Object>();
		String lsFromServiceSummary = "";
		doGetApplicationStatus(asAppStatus, asBusnessAppId, asOrgId, asUserId, asServiceAppId, loMapToRender, null,
				lsFromServiceSummary);
		Boolean lbApplicationStatus = (Boolean) loMapToRender.get("applicationStatus");
		if (lbApplicationStatus != null && lbApplicationStatus)
		{
			if (asAction != null && asAction.equalsIgnoreCase("applicationSubmit"))
			{
				String lsSubmitUserId = null;
				String lsUserOrgType = null;
				if (null != aoRequest.getParameter("userName"))
				{
					lsSubmitUserId = aoRequest.getParameter("userName");
					LOG_OBJECT.Debug("user to authenticate ::  ", lsSubmitUserId);
					//***Start SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
					lsUserOrgType = (String)aoRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
					LOG_OBJECT.Debug("Current User Type from session: "+lsUserOrgType);
					//***End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
				}
				if (null != asUserNameForAppSubmit && null != lsSubmitUserId
						&& asUserNameForAppSubmit.equalsIgnoreCase(lsSubmitUserId))
				{
					String lsSubmitUserPassword = null;
					if (null != aoRequest.getParameter("password"))
					{
						lsSubmitUserPassword = aoRequest.getParameter("password");
					}
					Boolean lbAuthenticated = false;
					//***Start SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
					lbAuthenticated = isAuthenticatedUser(lsSubmitUserId, lsSubmitUserPassword, lsUserOrgType);
					/*
					try
					{
							Authentication.authenticate(lsSubmitUserId, lsSubmitUserPassword);
							lbAuthenticated = true;
					
					}
					catch (LoginException lgAppEx)
					{
						LOG_OBJECT.Error("User authentication Failed on Application Submission ", lgAppEx);
						lbAuthenticated = false;
					}
					*/
					
					if (!lbAuthenticated && !ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
					{   
						lbAuthenticated = authenticateUser(lsSubmitUserId, lsSubmitUserPassword);
					}
					
					//***End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
					if (lbAuthenticated)
					{  
						Channel loChannel = new Channel();
						loChannel.setData("serviceAppId", asServiceAppId);
						TransactionManager.executeTransaction(loChannel, "fetchServiceAppStatus");
						String lsServiceAppStatus = (String) loChannel.getData("serviceStatus");
						if (lsServiceAppStatus != null
								&& lsServiceAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))
						{
							String lsUrl = aoRequest
									.getContextPath()
									.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
											+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=false");
							try
							{
								aoResponse.sendRedirect(lsUrl);
							}
							catch (IOException aoAppex)
							{
								LOG_OBJECT.Error("IOException occurred:", aoAppex);
							}
						}
						else
						{
							if (lsServiceAppStatus != null
									&& (lsServiceAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) || (lsServiceAppStatus
											.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))))
							{
								loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId,
										asAppStatus, lsServiceAppStatus, asAction, asUserId, aoRequest, null);
							}
							else
							{
								loChannel = aoBrAppObj.getChannelObject(asSectionName, asOrgId, asBusnessAppId,
										asAppStatus, null, asAction, asUserId, aoRequest, null);
							}
							String lsTransactionName = (String) loChannel.getData("transaction_name");
							loChannel.setData("serviceAppId", asServiceAppId);
							TransactionManager.executeTransaction(loChannel, lsTransactionName);
							if (loChannel.getData("lbWFLaunchStatus") != null
									&& (Boolean) loChannel.getData("lbWFLaunchStatus"))
							{
								String lsUrl = aoRequest
										.getContextPath()
										.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
												+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=true");
								try
								{
									aoResponse.sendRedirect(lsUrl);
								}
								catch (IOException aoAppex)
								{
									LOG_OBJECT.Error("IOException occurred:", aoAppex);
								}
							}
							else
							{
								aoResponse.setRenderParameter("loginErrorMsg",
										"!! Failed to submit Application, Workflow Launch Failed..");
							}
						}

					}
					// R 7.8
					else // if not outhenticated
					{
						aoResponse
								.setRenderParameter(
										"loginErrorMsg",
										"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
						//R 7.8
						aoResponse
						.setRenderParameter(
								"errorMsg",
								"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
					}
				}
				else
				{   
					aoResponse
							.setRenderParameter(
									"loginErrorMsg",
									"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
					//R 7.8
					aoResponse
					.setRenderParameter(
							"errorMsg",
							"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
				}
			}
		}
		else
		{   
			aoResponse.setRenderParameter("loginErrorMsg",
					"This application is not complete, hence can not be submitted. ");
		}
	}

	
	
	/**
	 * This method set all the member title into the map
	 * 
	 * @return map of member tilte
	 */
	private Map<String, String> getMemberInfoTitle()
	{
		Channel loChannel = new Channel();
		try
		{
			TransactionManager.executeTransaction(loChannel, "getMemberTitles");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("exception in getMemberInfoTitle", aoExp);
		}
		return (Map<String, String>) loChannel.getData("getMemberTitles");
	}

	/**
	 * This method set all the contract type into the map
	 * 
	 * @return Map contract type map
	 */
	private Map<String, String> getNYCAgency() throws ApplicationException

	{
		List<Map<String, String>> loProcuringNonProcuringAgencies = null;
		Channel loChannelObj = new Channel();
		Map<String, String> loNYCAgency = new LinkedHashMap<String, String>();
		TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.FETCH_PROCURING_NONPROCURING_AGENCIES);
		loProcuringNonProcuringAgencies = (List<Map<String, String>>) loChannelObj
				.getData(ApplicationConstants.PROCURING_NONPROCURING_AGENCIES);
		for (int liCount = 1; liCount <= loProcuringNonProcuringAgencies.size(); liCount++)
		{
			Map<String, String> loAgenciesMap = (Map<String, String>) loProcuringNonProcuringAgencies.get(liCount - 1);
			loNYCAgency.put(String.valueOf(liCount), loAgenciesMap.get("AGENCY_NAME")
					+ ApplicationConstants.BEGINNING_BRACKET + loAgenciesMap.get("AGENCY_ID")
					+ ApplicationConstants.CLOSING_BRACKET);

		}
		return loNYCAgency;

	}

	/**
	 * This method is used to validate the user when application is getting
	 * submitted
	 * 
	 * @param aoUserId user id
	 * @param aoPassword password for the user
	 * @return booleab true false
	 * @throws ApplicationException application exception
	 */
	private boolean authenticateUser(String aoUserId, String aoPassword) throws ApplicationException
	{
		boolean lbAutheticateUser = false;
		UserBean loUserBean = new UserBean();
		Channel loChannelObj = new Channel();
		loUserBean.setMsUserId(aoUserId);
		loUserBean.setMsPassword(aoPassword);
		loChannelObj.setData("aoUserBean", loUserBean);
		TransactionManager.executeTransaction(loChannelObj, "authenticateUser");
		lbAutheticateUser = (Boolean) loChannelObj.getData("lbAuthenticateUser");
		return lbAutheticateUser;
	}

	/**
	 * This method is used to read the data from file net when terms and
	 * condition comes
	 * 
	 * @param aoRequest request object
	 * @param asChannelInput channel object
	 * @return file net string
	 * @throws IOException IO exception
	 */
	private String getTermsAndCondition(RenderRequest aoRequest, String asChannelInput) throws IOException
	{
		Channel loChannel = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		loChannel.setData("aoUserSession", loUserSession);
		loChannel.setData(HHSR5Constants.AS_DOC_TYPE, asChannelInput);
		try
		{
			// Execute the transaction to get Document Content By Type
			TransactionManager.executeTransaction(loChannel, "getDocumentContentByType");
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Application Exception occurred while getting terms and conditions:", aoAppex);
		}
		HashMap<String, InputStream> loIoMap = (HashMap<String, InputStream>) loChannel.getData("contentByType");
		String lsSystemTermsAndCond = "";
		if (!loIoMap.isEmpty())
		{
			Writer loWriter = new StringWriter();
			char[] loBuffer = new char[1024];
			Reader loReader = null;
			String lsTemp = loIoMap.keySet().iterator().next();
			try
			{
				loReader = new BufferedReader(new InputStreamReader(loIoMap.get(lsTemp)));
				int liTempVar;
				while ((liTempVar = loReader.read(loBuffer)) != -1)
				{
					loWriter.write(loBuffer, 0, liTempVar);
				}
				lsSystemTermsAndCond = loWriter.toString();
			}
			finally
			{
				loIoMap.get(lsTemp).close();
				try
				{
					if (loReader != null)
					{
						loReader.close();
					}
					if (loWriter != null)
					{
						loWriter.close();
					}
				}
				catch (IOException aoExp)
				{
					LOG_OBJECT.Error("Error occured while getting terms and conditions", aoExp);
				}
			}
		}
		return lsSystemTermsAndCond;
	}

	// Added for release 5

	/**
	 * This method will redirect user to the next tab of the upload document
	 * screen step 2
	 * @param aoRequest
	 * @param aoResponse
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "submit_action=getFolderLocation")
	protected void getFolderLocation(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		com.nyc.hhs.model.Document loDocument = new com.nyc.hhs.model.Document();
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		try
		{
			loDocument = (com.nyc.hhs.model.Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null == loDocument)
			{
				loDocument = (com.nyc.hhs.model.Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			List<DocumentPropertiesBean> loDocumentPropsBeans = loDocument.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans.iterator();
			while (loDocPropsIt.hasNext())
			{
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN.equalsIgnoreCase(loDocProps.getPropertyType()))
				{
					if (HHSR5Constants.ON.equalsIgnoreCase(aoRequest.getParameter(loDocProps.getPropertyId())))
					{
						loPropertyMapInfo.put(loDocProps.getPropertyId(), true);
					}
					else
					{
						loPropertyMapInfo.put(loDocProps.getPropertyId(), false);
					}
				}
				else
				{
					loPropertyMapInfo.put(loDocProps.getPropertyId(),
							aoRequest.getParameter(loDocProps.getPropertyId()));
					loDocProps.setPropValue(aoRequest.getParameter(loDocProps.getPropertyId()));
				}
			}
			FileNetOperationsUtils.validatorForUpload((HashMap) loPropertyMapInfo,loDocument.getDocType());
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter("action", "applicationController");
			aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION, "servicessummary");
			aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION, "summary");
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.CREATE_TREE);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in action file upload screen in Document Vault", aoExp);
			
			String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.IS_AJAX_CALL);
			String lsErrorMsg = aoExp.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			// Setting the required attribute.
			if (null != lsAjaxCall && lsAjaxCall.equalsIgnoreCase("true"))
			{
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
				try
				{
					aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
				}
				catch (IOException e)
				{
					throw new ApplicationException("Error Occured while Processing your Request", aoExp);
				}
			}
		}
	}

	/**
	 * The method will create tree structure for upload step 3.
	 * @param aoRequest
	 * @param aoResponse
	 * @return formpath as document location
	 */
	@RenderMapping(params = "render_action=treeCreation")
	protected String documentupload(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsFormPath = null;

		lsFormPath = doShowFileInformation(aoRequest, null);
		// aoRequest.setAttribute(HHSR5Constants.JS_TREE_BEAN_LIST,
		// aoRequest.getPortletSession().getAttribute(HHSR5Constants.JS_TREE_BEAN_LIST));
		aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SECTION,
				aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION));
		aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION,
				aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION));
		lsFormPath = HHSR5Constants.DOC_LOC_BAAP;
		return lsFormPath;
	}

	// Added for release 5
	// Added for release 5
	/**
	 * This method will handle resource request for generating json for tree
	 * 
	 * @param aoResourceRequest
	 * @param aoResourceResponse
	 * @throws IOException
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("openTreeAjax")
	protected void handleResourceForTree(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws IOException, ApplicationException
	{
		PrintWriter loOut = null;
		loOut = aoResourceResponse.getWriter();
		String loJson = null;
		// Adding P8Session for Defect # 8150
		P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsCustmOrgId = null;
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		String lsUserOrg = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		Set<String> loList = (Set<String>) aoResourceRequest.getPortletSession().getAttribute("SharedOrgSessionList");
		String lsDivId = null;
		try
		{
			lsDivId = aoResourceRequest.getParameter("divId");
			lsCustmOrgId = aoResourceRequest.getParameter("orgId");
			if (null != lsCustmOrgId && !lsCustmOrgId.isEmpty())
			{
				// Adding extra parameter for Defect # 8150
				loJson = FileNetOperationsUtils.getOtherOrgFolderStructure(loList, lsUserOrgType,loUserSession,lsUserOrg);
				// getFolderStructureForOtherOrg(aoResourceRequest, loOut,
				// loChannelObj, aoHashMap, lsUserOrgType);
			}
			else
			{
				loJson = FileNetOperationsUtils.getOrgFolderStructure(lsUserOrg, lsDivId, lsUserOrgType);
			}

			loOut.print(loJson);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree", loAppEx);
			throw loAppEx;
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured   in handleResourceForTree", loAppEx);
			throw new ApplicationException("Exception occured while fetch tree structure ", loAppEx);
		}

	}
	/**
	 * This method will handle resource request since it implements ResourceAwareController
	 * 
	 * @param ResourceRequest arg0
	 * @param ResourceResponse arg1
	 * @throws Exception
	 * @return null
	 */
	// Added for release 5
	@Override
	public ModelAndView handleResourceRequest(ResourceRequest arg0, ResourceResponse arg1) throws Exception
	{
		return null;
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

	//***Start SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
	/*
	 * method authenticate user: 
	 * for member of Provider :: call NYCID WebService to authenticate
	 * for agency/city - use LDAP authentication
	 *  
	 */
	private Boolean isAuthenticatedUser(String lsSubmitUserId, String lsSubmitUserPassword, String lsUserOrgType)
	{   
		Boolean lbAuthenticated = false;
		try
		{   
			
			LOG_OBJECT.Debug("user to authenticate ::  ", lsSubmitUserId);
			if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
			{
				LOG_OBJECT.Debug("Call NYCID WebService to Authenticate ");
				NYCIDWebServices sws = new NYCIDWebServices();
				String jsonResponse = sws.authenticateUser(lsSubmitUserId, lsSubmitUserPassword); 
				LOG_OBJECT.Debug("jsonRespons:: "+jsonResponse);
				if(jsonResponse!=null && jsonResponse.contains(":"))
			    {
					String[] temp = jsonResponse.split(":", 2);
					LOG_OBJECT.Debug("\nloStrBuffer[0] "+temp[0]);
					LOG_OBJECT.Debug("loStrBuffer[1] "+temp[1]);
					if (temp[0].indexOf("authenticated") != -1 && temp[1].indexOf("true") != -1)
					{
						lbAuthenticated = true;
					}	
					else
					{
						lbAuthenticated = false;
						throw new LoginException("User authentication Failed on Application Submission ");
					}
			    }
		
			}
			else
			{
				Authentication.authenticate(lsSubmitUserId, lsSubmitUserPassword);
				lbAuthenticated = true;
			}
			
		}
		catch (LoginException aoAppEx)
		{
			LOG_OBJECT.Error("User authentication Failed on Application Submission ", aoAppEx);
			lbAuthenticated = false;
		}
		
		catch (ApplicationException aoAppEx)
		{
			if (!lbAuthenticated)
			{
				LOG_OBJECT.Error("User authentication Failed on Application Submission", aoAppEx);
			}	
		}
		return lbAuthenticated;
		
		//**End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
	}

}
