package com.nyc.hhs.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.accenture.constants.ObjectModelConstants;
import com.accenture.formtaglib.DomStatus;
import com.accenture.util.XmlUtil;
import com.bea.p13n.security.Authentication;
import com.filenet.api.util.Id;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.actions.BusinessApplication;
import com.nyc.hhs.controllers.actions.BusinessApplicationFactory;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.DocumentsSelFromDocVault;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.ProviderStatusBusinessRules;
import com.nyc.hhs.util.RFPReleaseDocsUtil;
import com.nyc.hhs.webservice.restful.NYCIDWebServices;

/**
 * HHS Accelerator Business Application controller for Pre-Submission, Returned
 * for Revisions/Deferred state and other state. Controller works for basic,
 * filings, board, policies questions, its related documents,language,geography
 * or population organization serve and history of actions taken on the Business
 * Application history.
 * 
 */
@Controller(value = "OrgInformation")
@RequestMapping("view")
public class BusinessApplicationController implements ResourceAwareController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(BusinessApplicationController.class);

	@ModelAttribute("ApplicationAuditBean")
	public ApplicationAuditBean getCommandObject()
	{
		return new ApplicationAuditBean();
	}

	/**
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp.
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse object
	 * @return loModelAndView
	 * @throws ApplicationException - throws ApplicationException
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		long loStartTime = System.currentTimeMillis();
		String lsOrgId = "";
		String lsAltCityUserSearchProviderId = "";
		ModelAndView loModelAndView = null;
		String lsJSPFileName = "shareDocheader";
		Map<String, Object> loMapToRender = new HashMap<String, Object>();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		try
		{
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) loPortletSessionThread.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsCityUserSearchProviderId = PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId");
			String lsServiceSummaryStatus = PortalUtil.parseQueryString(aoRequest, "serviceSummaryStatus");
			String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, "asAjaxCall");
			// Updated in 3.1.0. Added check for Defect 6346
			if (lsCityUserSearchProviderId == null)
			{
				lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
						"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			}
			String lsIsHeaderTab = PortalUtil.parseQueryString(aoRequest, "isHeaderTab");
			// Defect 6346 check ends
			// R4 HomePage Changes: Check added to identify whether the request
			// is coming from HomePage Screen.
			if (null != lsCityUserSearchProviderId)
			{
				if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
				{
					String lsDocOriginator = lsCityUserSearchProviderId.split(ApplicationConstants.TILD)[1];
					aoRequest.setAttribute(ApplicationConstants.DOC_ORIGINATOR, lsDocOriginator);
					lsAltCityUserSearchProviderId = lsCityUserSearchProviderId.split(ApplicationConstants.TILD)[0];
				}
				else
				{
					aoRequest.setAttribute(ApplicationConstants.DOC_ORIGINATOR, ApplicationConstants.PROVIDER_ORG);
					lsAltCityUserSearchProviderId = lsCityUserSearchProviderId;
				}
			}
			PortletSession loPortletSession = aoRequest.getPortletSession();
			if (aoRequest.getParameter(ApplicationConstants.RENDER_ACTION) != null
					&& !aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).equals(ApplicationConstants.ERROR))
			{
				LOG_OBJECT.Debug("Internal Error occured in BusinessApplicationController Action");
				loModelAndView = new ModelAndView("errorpage");
				return loModelAndView;
			}
			String lsPostSubmissionService = (String) aoRequest.getPortletSession().getAttribute("headerPostService",
					PortletSession.APPLICATION_SCOPE);
			String loOrgName = FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(),
					lsAltCityUserSearchProviderId);
			if (null != loOrgName)
			{
				loPortletSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY, loOrgName,
						PortletSession.APPLICATION_SCOPE);
			}
			removeValFromPortletSession(aoRequest, loPortletSession, lsAjaxCall);
			String lsHeaderAfterSubmission = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.POST_HEADER_BR_APPLICATION);
			if (lsHeaderAfterSubmission != null && lsHeaderAfterSubmission.equalsIgnoreCase("true"))
			{
				aoRequest.getPortletSession().setAttribute("headerPostBrApp", "true");
			}
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			String lsAction = getAction(aoRequest, loPortletSession);
			String lsUserRoles = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.removeAttribute(ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			String lsModuleName = getModuleName(aoRequest, loPortletSession);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsOldSectionName = lsSectionName;
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);

			if (null != lsAction && lsAction.equalsIgnoreCase("updateAccountingPeriod"))
			{
				lsAction = "showquestion";
				lsModuleName = "header_organization_information";
				loMapToRender.put("successMessage", aoRequest.getParameter("lbUpdateAccPeriodFlag"));
			}
			lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			if (lsCityUserSearchProviderId != null && !lsCityUserSearchProviderId.equalsIgnoreCase("")
					&& null != lsAltCityUserSearchProviderId && !lsAltCityUserSearchProviderId.isEmpty())
			{
				lsOrgId = lsAltCityUserSearchProviderId;
				// Updated in 3.1.0. Added check for Defect 6346
				if (lsIsHeaderTab != null)
				{
					lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
							PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().removeAttribute("cityUserSearchProviderId",
							PortletSession.APPLICATION_SCOPE);
				}
				// Defect 6346 check ends
			}
			if (null != lsAction && lsAction.equalsIgnoreCase("aaaValueToSet"))
			{
				loPortletSession.setAttribute("aaaValueToSet", PortalUtil.parseQueryString(aoRequest, "aaaValueToSet"),
						PortletSession.APPLICATION_SCOPE);
				return null;
			}
			String loUserAgent = ((HttpServletRequest) aoRequest.getAttribute("javax.servlet.request"))
					.getHeader("User-Agent");
			String lsHeightForForms = "";
			if (loUserAgent.toLowerCase().indexOf("firefox") > -1
					|| (loUserAgent.toLowerCase().indexOf("msie") > -1 && loUserAgent.toLowerCase().indexOf("msie 7") != -1))
			{
				lsHeightForForms = "height";
			}
			else
			{
				lsHeightForForms = "min-height";
			}
			aoRequest.setAttribute("lsOrgIdFromCity", lsOrgId);
			String lsBusnessAppId = getBusnessAppId(aoRequest, loPortletSession);
			aoRequest.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			String lsAppId = getAppId(aoRequest, loPortletSession);
			Channel loChannel = new Channel();
			String lsBappId = null;
			String lsAppStatus = ApplicationConstants.NOT_STARTED_STATE;
			ApplicationSummary loSummary = doOrgInfo(lsOrgId);
			boolean lbIsFirstBapp = loSummary.getMoreThanOne();
			if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL))
			{
				lsBappId = loSummary.getTopBusinessAppId();
				lsAppStatus = getAppStatus(lsOrgId, lsBappId, loChannel);
			}
			else
			{
				lsAppStatus = getAppStatus(lsOrgId, lsBusnessAppId, loChannel);
			}
			Map<String, StatusBean> loBusinessStatusBeanMap = null;
			if ((lsBappId != null && !lsBappId.equalsIgnoreCase(HHSConstants.NULL))
					|| (lsBusnessAppId != null && !lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL)))
			{
				doGetApplicationStatus(lsAppStatus, loPortletSession, lsUserId, lsOldSectionName, loChannel,
						loMapToRender);
				loBusinessStatusBeanMap = (Map<String, StatusBean>) loMapToRender.get("loBusinessStatusBeanMap");
			}
			String lsApplicationType = PortalUtil.parseQueryString(aoRequest, "applicationType");
			if (lsApplicationType == null || lsApplicationType.equalsIgnoreCase(""))
			{
				lsApplicationType = (String) loPortletSession.getAttribute("applicationType");
				LOG_OBJECT.Info("lsApplicationType is Empty" + lsApplicationType);
			}
			else
			{
				loPortletSession.setAttribute("applicationType", lsApplicationType);
			}
			if ((lsPostSubmissionService != null && lsPostSubmissionService.equalsIgnoreCase("true")))
			{
				if (lsAppStatus != null && !(lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
						&& lsSubSectionName != null && lsSubSectionName.equalsIgnoreCase("applicationsubmission"))
				{
					lsSectionName = "serviceapplicationsummary";
					lsSubSectionName = "applicationSubmit";
				}
			}
			String lbWithdrawalVisibleFlag = "Invisible";
			if (lsAction != null && lsSubSectionName.equalsIgnoreCase("applicationhistory"))
			{
				lbWithdrawalVisibleFlag = getWithDrawalFlagVisibleFlagRender(lsOrgId, lsUserRoles, lsBusnessAppId,
						lsAppStatus);
			}
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute("asOrgnizationType", lsOrgnizationType);
			PortletContext loContext = loPortletSession.getPortletContext();
			String lsFormPath = null;
			Map<String, StatusBean> loBusinessStatusBeanSectionMap = (Map<String, StatusBean>) loBusinessStatusBeanMap;
			StatusBean lsStatusBean = null;
			String lsSectionStatus = ApplicationConstants.NOT_STARTED_STATE;
			String lsSectionStatusInner = ApplicationConstants.NOT_STARTED_STATE;
			Map<String, String> loMap = getActionSecSubSecRender(lsAction, lsSectionName, lsSubSectionName, aoRequest);
			lsAction = loMap.get("asAction");
			lsSectionName = loMap.get("asSectionName");
			lsSubSectionName = loMap.get("asSubSectionName");
			if (loBusinessStatusBeanSectionMap != null && loBusinessStatusBeanSectionMap.get(lsSectionName) != null
					&& !loBusinessStatusBeanSectionMap.isEmpty())
			{
				lsStatusBean = (StatusBean) loBusinessStatusBeanSectionMap.get(lsSectionName);
				lsSectionStatus = lsStatusBean.getMsSectionStatusToDisplay();
				lsSectionStatusInner = lsStatusBean.getMsSectionStatusOnInnerSummary();
			}
			// R5 code start
			String lsPermissionType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
			Boolean loReadOnly = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsAppStatus, null, null,
					"business", lsOrgnizationType, lsPermissionType);
			Boolean loReadOnlySection = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsSectionStatus,
					lsSectionStatusInner, null, lsSectionName, lsOrgnizationType, lsPermissionType);
			// R5 code ends
			setValuesInSession(aoRequest, loPortletSession, lsBusnessAppId, lsAppId, lsAppStatus);
			LOG_OBJECT.Debug("1 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			LOG_OBJECT.Debug("2 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			BusinessApplication loBusinessApp = BusinessApplicationFactory.getBusinessApplication(lsSectionName,
					lsSubSectionName);
			// Check for Question as sub section
			if ((null != lsSubSectionName && (lsSubSectionName
					.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION) || lsSubSectionName
					.endsWith("applicationhistory")))
					|| (null != lsAction && (lsAction.equals("documentupload") || lsAction.equals("backrequest")
							|| lsAction.equals("fileinformation") || lsAction.equals("selectDocFromVault")
							|| lsAction.equals("businesswithdraw")
							|| lsAction.equals(ApplicationConstants.EDIT_DOCUMENT_PROPS) || lsAction
								.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))))
			{
				lsFormPath = getFormPath(aoRequest, lsOrgId, loPortletSession, lsAction, lsUserRoles, lsSectionName,
						lsSubSectionName, lsBusnessAppId, lsAppStatus, loMapToRender, loContext, lsFormPath,
						loReadOnlySection, loBusinessApp, lbIsFirstBapp);

			}
			else if (null != lsSectionName
					&& lsSectionName.equals(ApplicationConstants.BUSINESS_APPLICATION_SECTION_REVIEW_SUMMARY)
					&& (lsSubSectionName.equals("applicationsummary") || lsSubSectionName.equals("printfriendly")))
			{
				loChannel = showAppSummaryOrPrintFriendly(aoRequest, lsOrgId, lsAction, lsSectionName,
						lsSubSectionName, lsBusnessAppId, lsAppStatus, loBusinessApp);
				if (loChannel.getData("lsTransactionName") != null)
				{
					return doShowPrinterFriendlyRender(aoRequest, lsAction, lsSectionName, loChannel, loMapToRender,
							loBusinessApp);
				}
				loPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT);
				BusinessApplicationUtil.setIntoMapForRender(
						loBusinessApp.getMapForRender(lsAction, lsSectionName, loChannel, aoRequest), loMapToRender);
			}
			else
			{
				if (null == lsAction || lsAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN))
				{
					doShowOpenAction(aoRequest, lsOrgId, lsAction, lsSectionName, lsSubSectionName, lsBusnessAppId,
							lsAppStatus, loMapToRender, loReadOnlySection, loBusinessApp, lsPermissionType,
							lsOrgnizationType);
				}
				else if (lsAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE))
				{
					BusinessApplicationUtil.setIntoMapForRender(
							loBusinessApp.getMapForRender(lsAction, lsSectionName, null, aoRequest), loMapToRender);
				}
				else if (null != lsSubSectionName && lsSubSectionName.equalsIgnoreCase("applicationSubmit"))
				{
					return doShowAppSubmitSubSecRender(aoRequest, loMapToRender);
				}
				else if (lsAction.equalsIgnoreCase("applicationSubmit"))
				{
					return doShowAppSubmitActionRender(aoRequest, loMapToRender);
				}
				loPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT);
			}
			// Start of changes for Release 3.10.0 . Enhancement 6572
			if (null != lsSectionName && lsSectionName.equals(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
					&& null != lsBusnessAppId)
			{
				Channel loChanObj = new Channel();
				HashMap<String, Object> loParamMap = null;
				loChanObj.setData(ApplicationConstants.ORG_ID, lsOrgId);
				loChanObj.setData(ApplicationConstants.APP_ID, lsBusnessAppId);
				TransactionManager.executeTransaction(loChanObj, "getSubSectionStatusForFilings");
				loParamMap = (HashMap<String, Object>) loChanObj.getData("loParamMap");
				Boolean loFilingSubSectionFlag = (Boolean) loParamMap.get("filingSubSectionFlag");
				String lsCorporateStructure = (String) loParamMap.get("corporateStructure");
				aoRequest.setAttribute("filingSubSectionFlag", loFilingSubSectionFlag);
				aoRequest.setAttribute("corporateStructure", lsCorporateStructure);
			}
			// End of changes for Release 3.10.0 . Enhancement 6572

			lsJSPFileName = doShowSettings(aoRequest, lsOrgId, loPortletSession, lsUserRoles, lsModuleName,
					lsAppStatus, loMapToRender, lbWithdrawalVisibleFlag, lsFormPath, loReadOnly, loReadOnlySection,
					lbIsFirstBapp);
			String lsHeaderJSPName = PortalUtil.parseQueryString(aoRequest, "headerJSPName");
			String lsForPrint = PortalUtil.parseQueryString(aoRequest, "needPrintableView");

			if (lsHeaderJSPName != null && lsHeaderJSPName.equalsIgnoreCase("shareDocheader"))
			{
				lsJSPFileName = "shareDocheader";
				loMapToRender.put("ownerProviderId", lsCityUserSearchProviderId);
				loPortletSession.setAttribute("cityUserSearchProviderId", lsCityUserSearchProviderId,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.setAttribute("cityUserSearchProviderId", lsCityUserSearchProviderId);
			}
			else if (lsForPrint == null
					&& lsModuleName != null
					&& !lsModuleName.equalsIgnoreCase("header_organization_information")
					&& (lsAppStatus != null && (lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)
							|| lsAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN) || lsAppStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))))
			{
				aoRequest.setAttribute("section", "businessapplicationhistory");
				lsJSPFileName = "finalview_header";
				loMapToRender.put("applicationType", "business");
				loPortletSession.setAttribute("cityUserSearchProviderId", lsOrgId, PortletSession.APPLICATION_SCOPE);
			}
			loMapToRender.put("heightAttr", lsHeightForForms);
			Boolean loIsActiveCeo = Boolean.parseBoolean(PortalUtil.parseQueryString(aoRequest, "isActiveCeo"));
			if ((loIsActiveCeo == null || !loIsActiveCeo)
					&& (ApplicationSession.getAttribute(aoRequest, true, "isActiveCeo") != null && !((ApplicationSession
							.getAttribute(aoRequest, true, "isActiveCeo").getClass() == String.class) && ((String) ApplicationSession
							.getAttribute(aoRequest, true, "isActiveCeo")).equalsIgnoreCase(HHSConstants.NULL))))
			{

				if (ApplicationSession.getAttribute(aoRequest, true, "isActiveCeo").getClass() == String.class)
				{
					loIsActiveCeo = Boolean.parseBoolean((String) ApplicationSession.getAttribute(aoRequest, true,
							"isActiveCeo"));
				}
				else
				{
					loIsActiveCeo = (Boolean) ApplicationSession.getAttribute(aoRequest, true, "isActiveCeo");
				}
			}
			else
			{
				ApplicationSession.setAttribute(true, aoRequest, "isActiveCeo");
			}
			loMapToRender.put("isActiveCeo", loIsActiveCeo);
			if (lsServiceSummaryStatus != null)
			{
				loMapToRender.put("serviceSummaryStatus", lsServiceSummaryStatus);
			}

			// Release 5 read only changes
			if (lsPermissionType != null && (lsPermissionType.equalsIgnoreCase("R"))
					&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
			{
				aoRequest.setAttribute("bappReadOnlyUser", "true");
			}

			// Start of changes for Release 3.10.0 . Enhancement 6572
			aoRequest.setAttribute("appStatus", lsAppStatus);
			aoRequest.setAttribute("orgType", lsOrgType);
			aoRequest.setAttribute("sectionStatus", lsSectionStatus);
			// End of changes for Release 3.10.0 . Enhancement 6572
			loModelAndView = new ModelAndView(lsJSPFileName, loMapToRender);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in Bussiness Application page ", aoAppExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, ApplicationConstants.ERROR_MESSAGE_FILENET_DOWN);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			loModelAndView = new ModelAndView("errorBusinessHandler", loMapToRender);
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Internal Error occured in Bussiness Application page ", aoAppExp);
			loModelAndView = new ModelAndView("errorpage");
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in BusinessApplicationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in BusinessApplicationController", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method gets the Form path depending on the action,section or
	 * subsection.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization id
	 * @param aoPortletSession - PortletSession
	 * @param asAction - Action
	 * @param asUserRoles - User Role
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppStatus - Application Status
	 * @param aoMapToRender - Map To Render object
	 * @param aoContext - PortletContext
	 * @param asFormPath - Form Path
	 * @param aoReadOnly - Read Only
	 * @param aoBusinessApp - Business App
	 * @param abIsFirstBapp - flag is first bapp
	 * @return asFormPath - Form Path
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String getFormPath(RenderRequest aoRequest, String asOrgId, PortletSession aoPortletSession,
			String asAction, String asUserRoles, String asSectionName, String asSubSectionName, String asBusnessAppId,
			String asAppStatus, Map<String, Object> aoMapToRender, PortletContext aoContext, String asFormPath,
			Boolean aoReadOnly, BusinessApplication aoBusinessApp, boolean abIsFirstBapp) throws ApplicationException
	{
		if (null != asSubSectionName && asSubSectionName.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
		{
			asFormPath = doShowQuestionRender(aoRequest, asOrgId, aoPortletSession, asAction, asUserRoles,
					asSectionName, asSubSectionName, asBusnessAppId, asAppStatus, aoMapToRender, aoContext, asFormPath,
					aoReadOnly, aoBusinessApp, abIsFirstBapp);
		}

		else if (null != asSubSectionName && asSubSectionName.endsWith("applicationhistory"))
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
			asFormPath = doShowApplicationHistory(aoRequest, asOrgId, asAction, asSectionName, asSubSectionName,
					asBusnessAppId, asAppStatus, aoMapToRender, aoBusinessApp);
		}
		else if (null != asAction && asAction.equals("documentupload"))
		{
			asFormPath = doShowDocumentUploadRender(aoRequest);
		}
		else if (null != asAction && asAction.equals("backrequest"))
		{
			asFormPath = doShowDocumentUploadRender(aoRequest);
		}
		else if (null != asAction && asAction.equals("fileinformation"))
		{
			asFormPath = doShowFileActionRender(aoRequest);
		}
		else if (null != asAction && (asAction.equals("selectDocFromVault")))
		{
			aoPortletSession.setAttribute("selDocFromVaultItemList",
					ApplicationSession.getAttribute(aoRequest, "document_list_fromvault"),
					PortletSession.APPLICATION_SCOPE);
			aoRequest.setAttribute("docType", aoRequest.getParameter("docType"));
			aoRequest.setAttribute(ApplicationConstants.ALLOWED_OBJECT_COUNT, ApplicationSession.getAttribute(aoRequest,false,ApplicationConstants.ALLOWED_OBJECT_COUNT));
			asFormPath = ApplicationConstants.SELECT_DOC_FROM_VAULT;
		}
		else if (null != asAction && (asAction.equals("businesswithdraw")))
		{
			asFormPath = "/WEB-INF/jsp/businessapplication/businessWithdrawal.jsp";
		}
		// below if will be executed when view document info action
		// executed successfully
		else if (null != asAction && asAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
		{
			asFormPath = getRenderViewDocument(aoRequest);
		}
		// Start R5 : set EntityId and EntityName for AutoSave
		if (StringUtils.isNotBlank(asFormPath))
		{
			if (StringUtils.isNotBlank(asBusnessAppId))
			{
				CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), asBusnessAppId,
						asFormPath.replaceFirst(".*/(\\w+).*", "$1"));
			}
			else
			{
				CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), asOrgId,
						asFormPath.replaceFirst(".*/(\\w+).*", "$1"));
			}
		}

		// End R5 : set EntityId and EntityName for AutoSave
		return asFormPath;
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
		lsFormPath = "/WEB-INF/jsp/businessapplication/viewdocumentinfo_overlay_bapp.jsp";
		return lsFormPath;
	}

	/**
	 * This method gets the application status.
	 * 
	 * @param asOrgId - organization id
	 * @param asBusnessAppId - Busness App Id
	 * @param aoChannel - channel object
	 * @return lsAppStatus - App Status
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String getAppStatus(String asOrgId, String asBusnessAppId, Channel aoChannel) throws ApplicationException
	{
		aoChannel.setData(ApplicationConstants.APP_ID, asBusnessAppId);
		aoChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		TransactionManager.executeTransaction(aoChannel, "getBusinessApplicationStatus");
		String lsAppStatus = (String) aoChannel.getData("lsBussAppStatus");
		return lsAppStatus;
	}

	/**
	 * This method gets the application id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return lsAppId - application id
	 */
	private String getAppId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAppId = PortalUtil.parseQueryString(aoRequest, "applicationId");
		if (lsAppId == null || lsAppId.equalsIgnoreCase(""))
		{
			lsAppId = (String) aoPortletSession.getAttribute("applicationId");
		}
		return lsAppId;
	}

	/**
	 * This method sets Values In Session.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppId - application id
	 * @param asAppStatus - App Status
	 */
	private void setValuesInSession(RenderRequest aoRequest, PortletSession aoPortletSession, String asBusnessAppId,
			String asAppId, String asAppStatus)
	{
		// set the value in session
		aoPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, asBusnessAppId);
		aoPortletSession.setAttribute("bussAppStatus", asAppStatus);
		aoPortletSession.setAttribute("applicationId", asAppId);
		aoPortletSession.setAttribute("loReadOnlyStatus", asAppStatus, PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute("bussAppStatus", asAppStatus);
	}

	/**
	 * This method removes Values From Portlet Session.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param asAjaxCall - is ajax call
	 */
	private void removeValFromPortletSession(RenderRequest aoRequest, PortletSession aoPortletSession, String asAjaxCall)
	{
		aoPortletSession.removeAttribute("addNewService", PortletSession.APPLICATION_SCOPE);
		String lsRemoveSession = PortalUtil.parseQueryString(aoRequest, "headerPostService");
		if (lsRemoveSession != null && lsRemoveSession.equalsIgnoreCase("true"))
		{
			aoPortletSession.removeAttribute("headerPostService", PortletSession.APPLICATION_SCOPE);
		}
		if (null == asAjaxCall)
		{
			aoPortletSession.removeAttribute("user_roles", PortletSession.APPLICATION_SCOPE);
		}
	}

	/**
	 * This method shows Application Summary Or Print Friendly version.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization
	 * @param asAction - Action
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param asBusnessAppId - Busness App Id
	 * @param asAppStatus - App Status
	 * @param aoBusinessApp - Business App
	 * @return loChannel - Channel object
	 * @throws ApplicationException - throws ApplicationException
	 */
	private Channel showAppSummaryOrPrintFriendly(RenderRequest aoRequest, String asOrgId, String asAction,
			String asSectionName, String asSubSectionName, String asBusnessAppId, String asAppStatus,
			BusinessApplication aoBusinessApp) throws ApplicationException
	{
		Channel loChannel;
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null, asAction,
				null, aoRequest, asSubSectionName);
		// Execute transaction when transaction name is not null
		if (loChannel.getData("lsTransactionName") != null && lsTransactionName == null)
		{
			lsTransactionName = (String) loChannel.getData("lsTransactionName");
		}
		// Execute the transaction obtained from navigation xml when
		// business application summary is rendered.
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		// Get map to render.
		return loChannel;
	}

	/**
	 * This method gets Business App Id
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return lsBusnessAppId - Business App Id
	 */
	private String getBusnessAppId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
		if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase("") || lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL))
		{
			lsBusnessAppId = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID);
		}
		return lsBusnessAppId;
	}

	/**
	 * This method gets Business Module Name.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return lsModuleName - Module Name
	 */
	private String getModuleName(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsModuleName = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.KEY_SESSION_APPLICATION_MODULE);
		// get the module name when it is null.
		if (lsModuleName == null)
		{
			lsModuleName = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_APPLICATION_MODULE,
					PortletSession.APPLICATION_SCOPE);
		}
		return lsModuleName;
	}

	/**
	 * This method gets Business Action.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return lsAction - Action
	 */
	private String getAction(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAction = aoRequest.getParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		// get the action when action is null.
		if (lsAction == null)
		{
			lsAction = getActionNameRender(aoRequest, aoPortletSession);
		}
		return lsAction;
	}

	/**
	 * This method provides the render for the printer friendly view of
	 * application summary.
	 * 
	 * @param aoRequest
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param aoChannel
	 * @param aoMapToRender
	 * @param aoBusinessApp - Business Application factory
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private ModelAndView doShowPrinterFriendlyRender(RenderRequest aoRequest, String asAction, String asSectionName,
			Channel aoChannel, Map<String, Object> aoMapToRender, BusinessApplication aoBusinessApp)
			throws ApplicationException
	{
		BusinessApplicationUtil.setIntoMapForRender(
				aoBusinessApp.getMapForRender(asAction, asSectionName, aoChannel, aoRequest), aoMapToRender);
		aoMapToRender.put("applicationType", "business");
		ModelAndView loModelAndView = new ModelAndView("printerfriendly", aoMapToRender);
		return loModelAndView;
	}

	/**
	 * This method give us the jsp to be rendered on the basis of modules and
	 * user roles.
	 * 
	 * @param aoRequest
	 * @param asOrgId - Organization idFromCity
	 * @param aoPortletSession
	 * @param asUserRoles - Current User Role
	 * @param asModuleName
	 * @param asAppStatus - Current Application Status
	 * @param aoMapToRender
	 * @param abWithdrawalVisibleFlag
	 * @param asFormPath
	 * @param aoReadOnly
	 * @param aoReadOnlySection
	 * @param abIsFirstBapp - flag is first bapp
	 * @return
	 */
	private String doShowSettings(RenderRequest aoRequest, final String asOrgIdFromCity,
			PortletSession aoPortletSession, String asUserRoles, String asModuleName, String asAppStatus,
			Map<String, Object> aoMapToRender, String abWithdrawalVisibleFlag, String asFormPath, Boolean aoReadOnly,
			Boolean aoReadOnlySection, boolean abIsFirstBapp)
	{
		String lsJSPFileName = "home";
		// Header Organization Information
		if (asModuleName != null
				&& ApplicationConstants.MODULE_ORGANIZATION_INFORMATION.equals(asModuleName)
				&& (asUserRoles.equalsIgnoreCase(ApplicationConstants.ROLE_MANAGER) || asUserRoles
						.equalsIgnoreCase(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER)))
		{
			lsJSPFileName = "home_provider";
			if ((aoPortletSession.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE) != null && !((String) aoPortletSession
					.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE)).equalsIgnoreCase("read_only"))
					|| aoPortletSession.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE) == null)
			{
				if (!abIsFirstBapp
						&& (asAppStatus == null || asAppStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)))
				{
					aoPortletSession.setAttribute("user_roles", "first_time", PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					aoPortletSession.setAttribute("user_roles", "admin", PortletSession.APPLICATION_SCOPE);
					// for admin user
				}
			}
			aoMapToRender.put("app_menu_name", ApplicationConstants.MODULE_ORGANIZATION_INFORMATION);

		}
		else if (asModuleName != null && ApplicationConstants.MODULE_ORGANIZATION_INFORMATION.equals(asModuleName))
		{
			lsJSPFileName = "home_provider";
			if ((aoPortletSession.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE) != null && !((String) aoPortletSession
					.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE)).equalsIgnoreCase("read_only"))
					|| aoPortletSession.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE) == null)
			{
				if (!abIsFirstBapp
						&& (asAppStatus == null || asAppStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)))
				{
					aoPortletSession.setAttribute("user_roles", "first_time", PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE);
					// for staff
				}
			}
			aoMapToRender.put("app_menu_name", ApplicationConstants.MODULE_ORGANIZATION_INFORMATION);
		}
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, asFormPath);
		aoRequest.setAttribute("loReadOnly", aoReadOnly);
		aoMapToRender.put("loReadOnly", aoReadOnly);
		aoMapToRender.put("loReadOnlySection", aoReadOnlySection);
		if (aoMapToRender.get("lbWithdrawalVisibleFlag") == null)
		{
			aoMapToRender.put("lbWithdrawalVisibleFlag", abWithdrawalVisibleFlag);
		}

		if (asOrgIdFromCity == null && asAppStatus != null
				&& !asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
				&& !(asModuleName != null && ApplicationConstants.MODULE_ORGANIZATION_INFORMATION.equals(asModuleName)))
		{
			lsJSPFileName = "postSubmitionApplicationHeader";
		}
		return lsJSPFileName;
	}

	/**
	 * This block controls the render of application submission page load
	 * 
	 * @param aoRequest - Render request object
	 * @param aoMapToRender - Map To Render object
	 * @return
	 */
	private ModelAndView doShowAppSubmitActionRender(RenderRequest aoRequest, Map<String, Object> aoMapToRender)
	{
		if (null != aoRequest.getParameter("loginErrorMsg"))
		{
			aoMapToRender.put("errorMsg", aoRequest.getParameter("loginErrorMsg"));
		}
		String lsDisplayTermsCondition = null;
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching Application Terms and Condition from Filenet ", aoAppEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		ModelAndView loModelAndView = new ModelAndView("applicationsubmit", aoMapToRender);
		return loModelAndView;
	}

	/**
	 * This block controls the render of application submission page load
	 * 
	 * @param aoRequest - Render request object
	 * @param aoMapToRender - Map To Render object
	 * @return
	 */
	private ModelAndView doShowAppSubmitSubSecRender(RenderRequest aoRequest, Map<String, Object> aoMapToRender)
	{
		if (null != aoRequest.getParameter("loginErrorMsg"))
		{
			aoMapToRender.put("errorMsg", aoRequest.getParameter("loginErrorMsg"));
		}
		String lsDisplayTermsCondition = null;
		try
		{
			lsDisplayTermsCondition = getTermsAndCondition(aoRequest, "Application Terms & Conditions");
		}
		catch (IOException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while fetching Application Terms and Condition from Filenet ", aoAppEx);
		}
		aoRequest.setAttribute("lsDisplayTermsCondition", lsDisplayTermsCondition);
		ModelAndView loModelAndView = new ModelAndView("servicesubmit", aoMapToRender);
		return loModelAndView;
	}

	/**
	 * This method pre-populates the screen from user selected data by making a
	 * DB call and loads it on the page elements obtained from cached Taxonomy
	 * data.It also have the condition to make the page read only.
	 * 
	 * @param aoRequest - Render request object
	 * @param asOrgId - Organization id
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asBuisAppId - Business Application Id
	 * @param asAppStatus - Current Application Status
	 * @param aoMapToRender - Map To Render object
	 * @param aoReadOnlySection - Flag if section is read-only
	 * @param aoBusinessApp - Business Application factory
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void doShowOpenAction(RenderRequest aoRequest, String asOrgId, String asAction, String asSectionName,
			String asSubSectionName, String asBuisAppId, String asAppStatus, Map<String, Object> aoMapToRender,
			Boolean aoReadOnlySection, BusinessApplication aoBusinessApp, String asPermissionType,
			String asOrgnizationType) throws ApplicationException
	{
		Channel loChannel;
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.OPEN_TRANSACTION);
		loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null, asAction,
				null, aoRequest, asSubSectionName);
		// Execute the transaction obtained from navigation xml for
		// Document, geography, language and population screens when
		// their respective page is rendered.
		// Made changes in release 5 for module Proposal Activity History
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, asBuisAppId, HHSR5Constants.BASIC);
		// End R5 : set EntityId and EntityName for AutoSave

		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		BusinessApplicationUtil.setIntoMapForRender(
				aoBusinessApp.getMapForRender(asAction, asSectionName, loChannel, aoRequest), aoMapToRender);
		// this if condition is used to set the read only string in the
		// object to make the drop down disabled
		if (aoMapToRender != null && !aoMapToRender.isEmpty())
		{
			if (aoMapToRender.containsKey("taskItemList"))
			{
				List<com.nyc.hhs.model.Document> loDocumentList = (List<com.nyc.hhs.model.Document>) aoMapToRender
						.get("taskItemList");
				if (loDocumentList != null && !loDocumentList.isEmpty())
				{
					for (com.nyc.hhs.model.Document loDocument : loDocumentList)
					{
						if (aoReadOnlySection)
						{
							loDocument.setReadOnly("disabled=disabled");
						}
						if (asPermissionType != null
								&& asOrgnizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)
								&& asPermissionType.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY))
						{
							loDocument.setUserAccess(false);
						}
					}
				}
			}
		}
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				ApplicationSession.getAttribute(aoRequest, "message"));
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
				ApplicationSession.getAttribute(aoRequest, "messageType"));
	}

	/**
	 * This method open the second overlay while uploading document from the
	 * drop down.
	 * 
	 * @param aoRequest - Render request object
	 * @return lsFormPath
	 */
	private String doShowFileActionRender(RenderRequest aoRequest)
	{
		String lsFormPath;
		aoRequest.setAttribute("document_category", ApplicationSession.getAttribute(aoRequest, "document_category"));
		aoRequest.setAttribute("document_type", ApplicationSession.getAttribute(aoRequest, "document_type"));
		aoRequest.setAttribute("form_name", ApplicationSession.getAttribute(aoRequest, "form_name"));
		aoRequest.setAttribute("form_version", ApplicationSession.getAttribute(aoRequest, "form_version"));
		aoRequest.setAttribute("service_app_id", ApplicationSession.getAttribute(aoRequest, "service_app_id"));
		aoRequest.setAttribute("section_id", ApplicationSession.getAttribute(aoRequest, "section_id"));
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
		lsFormPath = ApplicationConstants.DISPLAY_FILE_INFO_PAGE;
		return lsFormPath;
	}

	/**
	 * This method open the first overlay while uploading document on the
	 * document page from the drop down.
	 * 
	 * @param aoRequest - Render request object
	 * @return
	 */
	private String doShowDocumentUploadRender(RenderRequest aoRequest)
	{
		String lsFormPath;
		aoRequest.setAttribute("document_category", ApplicationSession.getAttribute(aoRequest, "document_category"));
		aoRequest.setAttribute("document_type", ApplicationSession.getAttribute(aoRequest, "document_type"));
		aoRequest.setAttribute("form_name", ApplicationSession.getAttribute(aoRequest, "form_name"));
		aoRequest.setAttribute("form_version", ApplicationSession.getAttribute(aoRequest, "form_version"));
		aoRequest.setAttribute("service_app_id", ApplicationSession.getAttribute(aoRequest, "service_app_id"));
		aoRequest.setAttribute("section_id", ApplicationSession.getAttribute(aoRequest, "section_id"));
		lsFormPath = ApplicationConstants.UPLOAD_FILE_PAGE;
		return lsFormPath;
	}

	/**
	 * This method is used to show Application History for Business application
	 * 
	 * @param aoRequest - Render request object
	 * @param asOrgId - Organization id
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asBuisAppId - Business Application Id
	 * @param asAppStatus - Current Application Status
	 * @param aoMapToRender - Map to render object
	 * @param aoBusinessApp - Business Application factory
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String doShowApplicationHistory(RenderRequest aoRequest, String asOrgId, String asAction,
			String asSectionName, String asSubSectionName, String asBuisAppId, String asAppStatus,
			Map<String, Object> aoMapToRender, BusinessApplication aoBusinessApp) throws ApplicationException
	{
		Channel loChannel;
		String lsFormPath;
		lsFormPath = "/WEB-INF/jsp/businessapplication/businessWithdrawal.jsp";
		loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null, asAction,
				null, aoRequest, asSubSectionName);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
		List<Map<String, Object>> loApplicationHistory = (List<Map<String, Object>>) loChannel
				.getData("applicationHistoryInfo");
		List<Map<String, Object>> loApplicationHistoryXml = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> loMap : loApplicationHistory)
		{
			if (loMap.get("EVENT_NAME") != null && ((String) loMap.get("EVENT_NAME")).equalsIgnoreCase("Snapshot"))
			{
				loApplicationHistoryXml.addAll(readXML((String) loMap.get("DATA")));
			}
			else
			{
				loApplicationHistoryXml.add(loMap);
			}
		}
		aoRequest.setAttribute("applicationHistoryInfo", loApplicationHistoryXml);
		aoMapToRender.put("applicationHistoryInfo", loApplicationHistoryXml);
		return lsFormPath;
	}

	/**
	 * This method read the snapshot xml and display Application history and
	 * comments on jsp.
	 * 
	 * @param aoXmlRecords
	 * @return
	 */
	public List<Map<String, Object>> readXML(String aoXmlRecords)
	{
		List<Map<String, Object>> loApplicationHistoryList = new ArrayList<Map<String, Object>>();
		try
		{
			byte[] lbContent = aoXmlRecords.getBytes(ObjectModelConstants.BROWSER_ENCODING);
			ByteArrayInputStream loInput = new ByteArrayInputStream(lbContent);
			Document loDoc = XmlUtil.parse(loInput);
			Map<String, Object> loApplicationHistoryMap = new HashMap<String, Object>();
			setAttributeXml(loDoc, loApplicationHistoryMap);
			loApplicationHistoryList.add(loApplicationHistoryMap);
			List<Element> loEltList = loDoc.getRootElement().getChildren();
			for (int liCount = 0; liCount < loEltList.size(); liCount++)
			{
				Element loElt = loEltList.get(liCount);
				loApplicationHistoryMap = new HashMap<String, Object>();
				setChildAttributeXml(loApplicationHistoryMap, loElt);
				loApplicationHistoryList.add(loApplicationHistoryMap);
			}
		}
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("Error while reading sanpshot xml", aoEx);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while reading sanpshot xml", aoEx);
		}
		return loApplicationHistoryList;
	}

	/**
	 * This method set the attribute read from xml.
	 * 
	 * @param aoApplicationHistoryMap
	 * @param aoQuestionsElt
	 */
	private void setChildAttributeXml(Map<String, Object> aoApplicationHistoryMap, Element aoQuestionsElt)
	{
		aoApplicationHistoryMap.put("ENTITY_IDENTIFIER", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION + ": "
				+ BusinessApplicationUtil.toTitleCase(aoQuestionsElt.getName()));
		aoApplicationHistoryMap.put("EVENT_NAME", aoQuestionsElt.getAttributeValue("status"));
		aoApplicationHistoryMap.put("USER_ID", aoQuestionsElt.getAttributeValue("updateBy"));
		aoApplicationHistoryMap.put("AUDIT_DATE", aoQuestionsElt.getAttributeValue("date"));
		aoApplicationHistoryMap.put("DATA", aoQuestionsElt.getAttributeValue("comment"));
		aoApplicationHistoryMap.put("RETURNED_DOCUMENT", aoQuestionsElt.getAttributeValue("returnedDocuments"));
	}

	/**
	 * This method set the attribute read from xml.
	 * 
	 * @param aoDoc
	 * @param aoApplicationHistoryMap
	 */
	private void setAttributeXml(Document aoDoc, Map<String, Object> aoApplicationHistoryMap)
	{
		aoApplicationHistoryMap.put("ENTITY_IDENTIFIER", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		aoApplicationHistoryMap.put("EVENT_NAME", aoDoc.getRootElement().getAttributeValue("status"));
		aoApplicationHistoryMap.put("USER_ID", aoDoc.getRootElement().getAttributeValue("updateBy"));
		aoApplicationHistoryMap.put("AUDIT_DATE", aoDoc.getRootElement().getAttributeValue("date"));
		aoApplicationHistoryMap.put("DATA", aoDoc.getRootElement().getAttributeValue("comment"));
		aoApplicationHistoryMap.put("RETURNED_DOCUMENT", aoDoc.getRootElement().getAttributeValue("returnedDocuments"));
	}

	/**
	 * This method is used to populate the forms with pre-submitted values and
	 * get the comments while rendering. Also see if the question form is
	 * editable or non-editable.
	 * 
	 * @param aoRequest - Render request object
	 * @param asOrgId - Organization id
	 * @param aoPortletSession - Portlet Session object
	 * @param asAction - Action to be performed
	 * @param asUserRoles - Current User Role
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asBuisAppId - Business Application Id
	 * @param asAppStatus - Current Application Status
	 * @param aoMapToRender - Map to render object
	 * @param aoContext
	 * @param asFormPath
	 * @param aoReadOnly - Flag if application is read only
	 * @param aoBusinessApp - Business Application factory
	 * @param abIsFirstBapp - flag is first bapp
	 * @return asFormPath - Form Path
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String doShowQuestionRender(RenderRequest aoRequest, String asOrgId, PortletSession aoPortletSession,
			String asAction, String asUserRoles, String asSectionName, String asSubSectionName, String asBuisAppId,
			String asAppStatus, Map<String, Object> aoMapToRender, PortletContext aoContext, String asFormPath,
			Boolean aoReadOnly, BusinessApplication aoBusinessApp, boolean abIsFirstBapp) throws ApplicationException
	{
		Channel loChannel;
		String lsFromOrgProfile = PortalUtil.parseQueryString(aoRequest, "fb_formName");
		String lsTempSectionName = asSectionName;
		boolean lbIsFirstBasic = false;
		if (asSectionName != null
				&& asSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
				&& !abIsFirstBapp
				&& (asAppStatus == null || asAppStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)))
		{
			lbIsFirstBasic = true;
		}
		if (lsFromOrgProfile != null && lsFromOrgProfile.equalsIgnoreCase("OrgProfile"))
		{
			lsTempSectionName = "orgprofile";
		}
		// check if document object exist to populate form
		if (null == asBuisAppId && !asSectionName.equals(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS))
		{ // get
			// the
			// latest
			// form
			// path.
			asFormPath = PropertyUtil.getLiveFormLocation(lsTempSectionName);
		}
		else if ((null != asBuisAppId || asSectionName.equals(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS))
				&& (asAction.equals(ApplicationConstants.SHOW_QUESTION) || null == aoPortletSession.getAttribute(
						ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE)))
		{
			// get data from db
			aoPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.OPEN_TRANSACTION);
			loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null,
					asAction, asUserRoles, aoRequest, null);
			if (asBuisAppId != null)
			{
				lsTransactionName = "retrieve_questionanswer_withcomments";
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			if (null != loChannel.getData("loFormInformation"))
			{
				HashMap<String, Object> loHMAnswerMap = (HashMap<String, Object>) loChannel
						.getData("loFormInformation");
				// Assuming transaction will return map with value
				if (asSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS)
						&& loHMAnswerMap != null && loHMAnswerMap.containsKey("basic_cs_value"))
				{
					aoPortletSession.setAttribute("corpStr", loHMAnswerMap.get("basic_cs_value"),
							PortletSession.APPLICATION_SCOPE);
				}
				if (null != loChannel.getData("taskHistoryList"))
				{
					ArrayList<ApplicationAuditBean> loTaskHistoryList = (ArrayList<ApplicationAuditBean>) loChannel
							.getData("taskHistoryList");
					if (loTaskHistoryList != null && !loTaskHistoryList.isEmpty())
					{
						aoRequest.setAttribute("subSection", loTaskHistoryList.get(0).getMsSectionId());
					}
					aoPortletSession.setAttribute("aoTaskHistoryList", loTaskHistoryList);
				}
				// this check is to populate the forms with pre-submitted
				// values
				if (loHMAnswerMap != null && (loHMAnswerMap.containsKey("FORM_NAME") || !loHMAnswerMap.isEmpty()))
				{
					asFormPath = populatePreSubmittedForm(aoRequest, aoPortletSession, aoContext, asFormPath,
							aoReadOnly, lsFromOrgProfile, lsTempSectionName, loHMAnswerMap);
				}
				else
				{
					asFormPath = PropertyUtil.getLiveFormLocation(lsTempSectionName);
					if (!aoReadOnly)
					{
						aoPortletSession.setAttribute("user_roles", "edit", PortletSession.APPLICATION_SCOPE);
					}
					else
					{
						aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
					}
				}
			}
			else
			{
				asFormPath = PropertyUtil.getLiveFormLocation(lsTempSectionName);
				if (!aoReadOnly)
				{
					aoPortletSession.setAttribute("user_roles", "edit", PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
				}
			}
		}
		else if (null != aoPortletSession.getAttribute(ApplicationConstants.DOM_FOR_EDIT,
				PortletSession.APPLICATION_SCOPE))
		{
			asFormPath = defineRoleForFormView(aoRequest, aoPortletSession, asUserRoles, aoReadOnly, lsFromOrgProfile,
					lbIsFirstBasic);
		}
		BusinessApplicationUtil.setIntoMapForRender(
				aoBusinessApp.getMapForRender(asAction, asSectionName, null, aoRequest), aoMapToRender);
		return asFormPath;
	}

	/**
	 * This method defines Role For Form View.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param asUserRoles - User Roles
	 * @param aoReadOnly - Read Only flag
	 * @param asFromOrgProfile - From Org Profile
	 * @param abIsFirstBasic - is first basic flag
	 * @return asFormPath - Form Path
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String defineRoleForFormView(RenderRequest aoRequest, PortletSession aoPortletSession, String asUserRoles,
			Boolean aoReadOnly, String asFromOrgProfile, boolean abIsFirstBasic) throws ApplicationException
	{
		String lsFormName = aoRequest.getParameter(ApplicationConstants.FORMNAME);
		String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FORM_VERSION);
		if (asFromOrgProfile != null && asFromOrgProfile.equalsIgnoreCase("OrgProfile"))
		{
			lsFormName = "OrgProfile";
			if (!aoReadOnly)
			{
				if (asUserRoles.equalsIgnoreCase(ApplicationConstants.ROLE_MANAGER)
						|| asUserRoles.equalsIgnoreCase(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER))
				{
					aoPortletSession.setAttribute("user_roles", "admin", PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE);
				}
			}
			else
			{
				aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
			}
		}
		else
		{
			if (!aoReadOnly)
			{
				aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE);
			}
			else
			{
				aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
			}
		}
		return PropertyUtil.getFilePathToRender(lsFormName, lsFormVersion);
		// for semi edit
	}

	/**
	 * This method populates Pre Submitted Form.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param aoContext - PortletContext
	 * @param asFormPath - Form Path
	 * @param aoReadOnly - Read Only flag
	 * @param asFromOrgProfile - From Org Profile
	 * @param asTempSectionName - Temp Section Name
	 * @param aoHMAnswerMap - Answer Map
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String populatePreSubmittedForm(RenderRequest aoRequest, PortletSession aoPortletSession,
			PortletContext aoContext, String asFormPath, Boolean aoReadOnly, String asFromOrgProfile,
			String asTempSectionName, HashMap<String, Object> aoHMAnswerMap) throws ApplicationException
	{
		String lsNeedPrintableView = PortalUtil.parseQueryString(aoRequest, "needPrintableView");
		if (lsNeedPrintableView != null && lsNeedPrintableView.equalsIgnoreCase("true"))
		{
			String lsPathToReadQuestions = aoRequest.getPortletSession().getPortletContext()
					.getRealPath(ApplicationConstants.FORMS_FOLDER_NAME);
			if (lsPathToReadQuestions.lastIndexOf("/") > -1)
			{
				lsPathToReadQuestions = lsPathToReadQuestions.substring(0, lsPathToReadQuestions.lastIndexOf("/"));
			}
			if (lsPathToReadQuestions.lastIndexOf("\\") > -1)
			{
				lsPathToReadQuestions = lsPathToReadQuestions.substring(0, lsPathToReadQuestions.lastIndexOf("\\"));
			}
			if (aoHMAnswerMap.containsKey("FORM_NAME") && aoHMAnswerMap.get("FORM_NAME").equals("Basic"))
			{
				aoHMAnswerMap.put("FORM_NAME", "OrgProfile");
			}
			StringBuffer loSbHTMLForPrintView = BusinessApplicationUtil.getFormHTML(aoHMAnswerMap,
					lsPathToReadQuestions, false);
			aoRequest.setAttribute("printView", loSbHTMLForPrintView);
		}
		else
		{
			String lsFormTemplatePath = null;
			if (aoHMAnswerMap.containsKey("FORM_NAME"))
			{
				String lsFormName = (String) aoHMAnswerMap.get("FORM_NAME");
				String lsFormVersion = (String) aoHMAnswerMap.get("FORM_VERSION");
				String lsTempFormName = lsFormName;
				if (asFromOrgProfile != null && asFromOrgProfile.equalsIgnoreCase("OrgProfile"))
				{
					lsTempFormName = "OrgProfile";
				}
				asFormPath = PropertyUtil.getFilePathToRender(lsTempFormName, lsFormVersion);
				lsFormTemplatePath = aoContext.getRealPath(PropertyUtil.getDeployedFormLocation(lsTempFormName,
						lsFormVersion, false));
			}
			else
			{
				asFormPath = PropertyUtil.getLiveFormLocation(asTempSectionName);
				lsFormTemplatePath = aoContext.getRealPath(PropertyUtil.getDeployedFormLocation(asTempSectionName,
						false));
			}

			Document loAnswerDom = PropertyUtil.converRequestMapToDom(lsFormTemplatePath, aoHMAnswerMap);
			aoPortletSession.setAttribute(ApplicationConstants.DOM_FOR_EDIT, loAnswerDom,
					PortletSession.APPLICATION_SCOPE);
			if (!aoReadOnly)
			{
				aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE); // for
																											// semi
																											// edit
			}
			else
			{
				aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
			}
		}
		return asFormPath;
	}

	/**
	 * This method get the action when we perform save_next or back request
	 * 
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param aoRequest - Render request object
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private Map<String, String> getActionSecSubSecRender(String asAction, String asSectionName,
			String asSubSectionName, RenderRequest aoRequest) throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		if (asAction != null && asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE_NEXT))
		{
			// it checks if user click on save and next button if yes it check
			// the
			// navigation for next screen.
			asSubSectionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName, "nextaction");
			if (asSubSectionName.startsWith("menu__"))
			{
				String[] loNextActionArr = asSubSectionName.split("menu__");
				asSectionName = loNextActionArr[1];
				asSubSectionName = PropertyUtil.getDefaultSubSection(asSectionName);
				asAction = ApplicationConstants.SHOW_QUESTION;
			}
			else
			{
				asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
			}
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.DOM_FOR_EDIT,
					PortletSession.APPLICATION_SCOPE);
		}
		else if (asAction != null && asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
		{
			// it checks if user click on back button if yes it check the
			// navigation for previous screen.
			asSubSectionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName, "previousaction");
			if (asSubSectionName.contains("_"))
			{
				String[] loPreviouseActionArr = asSubSectionName.split("_");
				if (loPreviouseActionArr.length > 1)
				{
					asSectionName = loPreviouseActionArr[0];
					asSubSectionName = loPreviouseActionArr[1];
				}
			}
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
			if (ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION.equalsIgnoreCase(asSubSectionName))
			{
				asAction = ApplicationConstants.SHOW_QUESTION;
			}
			aoRequest.getPortletSession().removeAttribute(ApplicationConstants.DOM_FOR_EDIT,
					PortletSession.APPLICATION_SCOPE);
		}
		aoRequest.setAttribute("section", asSectionName);
		aoRequest.setAttribute("subsection", asSubSectionName);
		loMap.put("asAction", asAction);
		loMap.put("asSectionName", asSectionName);
		loMap.put("asSubSectionName", asSubSectionName);
		return loMap;
	}

	/**
	 * This method is used to set the flag that whether WithDrawal should be
	 * visible or invisible
	 * 
	 * @param asUserRoles - Current User Role
	 * @param asBuisAppId - Business Application Id
	 * @param asAppStatus - Current Application Status
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String getWithDrawalFlagVisibleFlagRender(String asOrgId, String asUserRoles, String asBuisAppId,
			String asAppStatus) throws ApplicationException
	{
		String lbWithdrawalVisibleFlag = "Invisible";
		// check whether Withdrawal link will be Visible or not
		if (asBuisAppId != null)
		{
			Channel loChannel = new Channel();
			loChannel.setData("businessAppId", asBuisAppId);
			loChannel.setData("lsOrgId", asOrgId);
			TransactionManager.executeTransaction(loChannel, "getBussAppUpdatedStatus");
			Map<String, Object> loUpdateStatusObj = (Map<String, Object>) loChannel.getData("bussAppUpdatedStatus");
			String lsBussAppStatus = "";
			if (loUpdateStatusObj != null && !loUpdateStatusObj.isEmpty())
			{
				String lsExpiredCheck = (String) loUpdateStatusObj.get("expired");
				if (lsExpiredCheck != null && lsExpiredCheck.equalsIgnoreCase("expired"))
				{
					lbWithdrawalVisibleFlag = "Invisible";
					return lbWithdrawalVisibleFlag;
				}
				lsBussAppStatus = (String) loUpdateStatusObj.get("STATUS");
			}
			Channel loChannelObj = new Channel();
			loChannelObj.setData("businessAppId", asBuisAppId);
			TransactionManager.executeTransaction(loChannelObj, "fetchBussAppWithdrawStatus");
			String lsWithdrawStatus = (String) loChannelObj.getData("withdrawStatus");
			if ((lsWithdrawStatus != null && (lsWithdrawStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW) || lsWithdrawStatus
					.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)))
					|| (asAppStatus != null && asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)))
			{
				lbWithdrawalVisibleFlag = "Invisible";
			}
			else if (lsBussAppStatus != null
					&& ((lsBussAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN) || lsBussAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)) || (asAppStatus != null && asAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED))))
			{
				lbWithdrawalVisibleFlag = "Invisible";
			}
			else if (lsWithdrawStatus != null
					&& asAppStatus != null
					&& (lsWithdrawStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW) || asAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)))
			{
				lbWithdrawalVisibleFlag = "Invisible";
			}
			else if (asUserRoles != null
					&& (asUserRoles.contains("manager") || asUserRoles
							.contains(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER))
					&& !(asAppStatus != null && (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED) || asAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))))
			{
				lbWithdrawalVisibleFlag = "visible";
			}
			else
			{
				lbWithdrawalVisibleFlag = "Invisible";
			}
		}
		return lbWithdrawalVisibleFlag;
	}

	/**
	 * This method have flag to check whether business application can be
	 * submitted or not
	 * 
	 * @param aoBusinessStatusBeanMap - Map which maintains the Status
	 *            information.
	 * @return
	 */
	private boolean getAppStatusFlagRender(Map<String, StatusBean> aoBusinessStatusBeanMap)
	{
		boolean lbAppStatusFlag = true;
		if (aoBusinessStatusBeanMap != null)
		{
			for (Entry<String, StatusBean> loEntry : aoBusinessStatusBeanMap.entrySet())
			{
				StatusBean loStatusBean = loEntry.getValue();
				if (!(loStatusBean != null && loStatusBean.getMsSectionStatusToDisplay() != null && (loStatusBean
						.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
						|| loStatusBean.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.STATUS_WITHDRAWN) || loStatusBean.getMsSectionStatusToDisplay()
						.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))))
				{
					lbAppStatusFlag = false;
					break;
				}
			}
		}
		return lbAppStatusFlag;
	}

	/**
	 * This method get the action when action is null.
	 * 
	 * @param aoRequest - Render request object
	 * @param aoPortletSession
	 * @return - lsAction action object
	 */
	private String getActionNameRender(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAction;
		aoPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute("corpStr", PortletSession.APPLICATION_SCOPE);
		lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		return lsAction;
	}

	/**
	 * This method get the status of application. The method updated for for
	 * Release 3.10.0 : Enhancement 6572
	 * 
	 * @param aoPortletSession - Portlet Session
	 * @param asUserId - User id of current User
	 * @param asOldSectionName - Section Name
	 * @param aoChannel - Channel Object
	 * @param aoMapToRender - Map to be rendered
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void doGetApplicationStatus(String asApplicationStatus, PortletSession aoPortletSession, String asUserId,
			String asOldSectionName, Channel aoChannel, Map<String, Object> aoMapToRender) throws ApplicationException
	{
		String lsServiceSummaryStatus = "";
		Boolean lbApplicationStatus;
		aoChannel.setData(ApplicationConstants.SECTION, asOldSectionName);
		aoChannel.setData(ApplicationConstants.USER_ID, asUserId);
		// Execute the transaction to get the status of application.
		TransactionManager.executeTransaction(aoChannel, "getCompleteStatusMap");
		Map<String, StatusBean> loBusinessStatusBeanMap = (Map<String, StatusBean>) aoChannel
				.getData("loBusinessStatusBeanMap");
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) aoChannel
				.getData("loServiceStatusBeanMap");
		Collection<StatusBean> loStatusBean = loServicesStatusBeanMap.values();
		Iterator<StatusBean> loIterator = loStatusBean.iterator();
		lsServiceSummaryStatus = getServiceStatus(lsServiceSummaryStatus, loIterator);
		lbApplicationStatus = ((Map<String, Boolean>) aoChannel.getData("applicationStatus")).get("completeStatus");
		if (lbApplicationStatus
				|| (asApplicationStatus != null && (asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))))
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
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
									|| lsProviderStatus
											.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED) || lsProviderStatus
										.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED)))
					{
						lbApplicationStatus = false;
					}
					else
					{
						lbApplicationStatus = true;
					}
				}
			}
			else
			{
				lbApplicationStatus = false;
			}
		}
		if (lsServiceSummaryStatus.equalsIgnoreCase(""))
		{
			lsServiceSummaryStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replaceAll(" ", "");
		}
		// Start of changes for Release 3.10.0 : Enhancement 6572
		if (asApplicationStatus != null
				&& (asApplicationStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asApplicationStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)))
		{
			lbApplicationStatus = ((Map<String, Boolean>) aoChannel.getData("applicationStatus"))
					.get("businessApplication");
		}
		// End of changes for Release 3.10.0 : Enhancement 6572
		aoPortletSession.setAttribute("serviceSummaryStatus", lsServiceSummaryStatus, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute("applicationStatus", lbApplicationStatus, PortletSession.APPLICATION_SCOPE);
		aoMapToRender.put("applicationStatus", lbApplicationStatus);
		aoMapToRender.put("loBusinessStatusBeanMap", loBusinessStatusBeanMap);
		aoMapToRender.put("loServicesStatusBeanMap", loServicesStatusBeanMap);
		aoMapToRender.put("serviceSummaryStatus", lsServiceSummaryStatus);
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
				Collection<String> loCollectionRef = loSubSectionMap.values();
				// obtain an Iterator for Collection
				Iterator<String> loItr = loCollectionRef.iterator();
				// iterate through HashMap values iterator
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
						if (asServiceSummaryStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE.replaceAll(
								" ", "")))
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
			}
		}
		return asServiceSummaryStatus;
	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction.
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
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			String lsAjaxCall = aoRequest.getParameter(ApplicationConstants.IS_AJAX_CALL);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			final String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			if (lsCityUserSearchProviderId != null && !lsCityUserSearchProviderId.equalsIgnoreCase(""))
			{
				if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
				{
					lsOrgId = lsCityUserSearchProviderId.substring(0,
							lsCityUserSearchProviderId.indexOf(ApplicationConstants.TILD));
				}
				else
				{
					lsOrgId = lsCityUserSearchProviderId;
				}
			}
			String lsAppStatus = getAppStatus(aoRequest);
			String lsUserRoles = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsUserNameForAppSubmit = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsBusnessAppId = getBusnessAppId(aoRequest);
			String lsAppId = getAppId(aoRequest);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			boolean lbIsDomWithError = false;
			String lsOrgType = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession(true).removeAttribute(ApplicationConstants.DOM_FOR_EDIT);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			aoRequest.getPortletSession().setAttribute("bussAppStatus", lsAppStatus);
			aoRequest.getPortletSession().setAttribute("applicationId", lsAppId);
			LOG_OBJECT.Debug("3 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			BusinessApplication loBusinessApp = BusinessApplicationFactory.getBusinessApplication(lsSectionName,
					lsSubSectionName);
			String lsOldSectionName = lsSectionName;
			Map<String, StatusBean> loBusinessStatusBeanMap = null;
			
			/** BEGIN Fix multi-tab Browsing QC6674 R7.1.0*/
			LOG_OBJECT.Debug("lsBusnessAppId = " + lsBusnessAppId);
			String orgIdForBusinessApp = getOrgFromBusinessApp(lsBusnessAppId);
			LOG_OBJECT.Debug("orgIdForBusinessApp = " + orgIdForBusinessApp);
			
			if ( (orgIdForBusinessApp != null) &&(! orgIdForBusinessApp.isEmpty()) && ( !orgIdForBusinessApp.equalsIgnoreCase(lsOrgId))) {
				throw new ApplicationException(ApplicationConstants.USER_EXIST_IN_SESSION);
			}
			/** END Fix multi-tab Browsing QC6674 R7.1.0*/
			
			if (lsBusnessAppId != null && !lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL))
			{
				loBusinessStatusBeanMap = doGetApplicationStatusAction(lsOrgId, lsAppStatus, loPortletSession,
						lsUserId, lsBusnessAppId, lsOldSectionName);
			}
			String lsModuleName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.KEY_SESSION_APPLICATION_MODULE);
			if (lsBusnessAppId == null && lsModuleName != null
					&& lsModuleName.equalsIgnoreCase(ApplicationConstants.MODULE_ORGANIZATION_INFORMATION))
			{
				lsBusnessAppId = doOrgInfo(lsOrgId).getTopBusinessAppId();
			}
			if ((lsAction != null && lsAction.equalsIgnoreCase("showBasicComments")))
			{
				aoResponse.setRenderParameter("next_action", "showBasicComments");
			}
			else if (lsAction != null && lsAction.equals(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
			{
				// no need to take any action
			}
			else if (null != lsSubSectionName
					&& lsSubSectionName.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
			{
				lbIsDomWithError = questionsSubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName,
						lsSubSectionName, lsOrgId, lsAppStatus, lsUserRoles, lsUserId, lsBusnessAppId,
						lbIsDomWithError, loBusinessApp, lsAjaxCall);
			}
			else if (null != lsSubSectionName
					&& lsSubSectionName.endsWith(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_ORG_LEGAL_NAME))
			{
				lsAction = leagalNameSubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName, lsSubSectionName,
						lsOrgId, lsAppStatus, lsUserRoles, lsBusnessAppId);
			}
			else if (null != lsSubSectionName
					&& lsSubSectionName.endsWith(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
			{
				lsAction = documentListSubSectionAction(aoRequest, aoResponse, lsAction, lsAjaxCall, lsSectionName,
						lsSubSectionName, lsOrgId, lsAppStatus, lsUserRoles, lsUserId, lsBusnessAppId, loUserSession);
			}
			else if (null != lsSubSectionName && lsSubSectionName.endsWith("applicationSubmit"))
			{
				applicationSubmitSubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName, lsOrgId, lsAppStatus,
						lsUserRoles, lsUserNameForAppSubmit, lsUserId, lsBusnessAppId, loUserSession, loBusinessApp,
						loBusinessStatusBeanMap, lsOldSectionName);
			}
			else if (null != lsAction && lsAction.equalsIgnoreCase("applicationSubmit"))
			{
				applicationSubmitAction(aoRequest, aoResponse, lsAction, lsSectionName, lsOrgId, lsAppStatus,
						lsUserRoles, lsUserNameForAppSubmit, lsUserId, lsBusnessAppId, lsAppId, loUserSession,
						loBusinessApp, loBusinessStatusBeanMap, lsOldSectionName);
			}
			else if (null != lsSubSectionName && lsSubSectionName.endsWith("applicationhistory"))
			{
				applicationHistorySubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName, lsSubSectionName,
						lsOrgId, lsAppStatus, lsBusnessAppId, loBusinessApp, lsOrgType);
			}
			else if (null != lsSubSectionName && lsSubSectionName.endsWith("newPeriodForm"))
			{
				lsAction = newPeriodFormSubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName,
						lsSubSectionName, lsOrgId, lsAppStatus, lsBusnessAppId, loBusinessApp);

			}
			// release 5
			else if (null != lsSubSectionName && lsSubSectionName.endsWith("filingsManageOrganization"))
			{
				ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
						HHSConstants.NEXT_PAGE_PARAM);
				ApplicationSession.setAttribute(aoRequest.getParameter(HHSConstants.NEXT_PAGE), aoRequest,
						HHSConstants.CLC_OVERLAY_PARAM);
				aoResponse.setRenderParameter(HHSConstants.NEXT_PAGE, aoRequest.getParameter(HHSConstants.NEXT_PAGE));
				aoResponse.setRenderParameter(HHSConstants.ACTION, "OrgInformation");
				aoResponse.setRenderParameter("cityUserSearchProviderId", aoRequest.getParameter("ownerProviderId"));

				// filter parameter
				aoResponse.setRenderParameter("activefilingssFrom", aoRequest.getParameter("activefilingssFrom"));
				aoResponse.setRenderParameter("activefilingssTo", aoRequest.getParameter("activefilingssTo"));
				aoResponse.setRenderParameter("fiscalYearFilterToMonth",
						aoRequest.getParameter("fiscalYearFilterToMonth"));
				aoResponse.setRenderParameter("fiscalYearFilterFromMonth",
						aoRequest.getParameter("fiscalYearFilterFromMonth"));
				aoResponse.setRenderParameter("filingPeriodToMonth", aoRequest.getParameter("filingPeriodToMonth"));
				aoResponse.setRenderParameter("filingPeriodFromMonth", aoRequest.getParameter("filingPeriodFromMonth"));

				aoResponse.setRenderParameter("section", "filings");
				aoResponse.setRenderParameter("headerJSPName", "shareDocheader");
				aoResponse.setRenderParameter("subsection", "filingsManageOrganization");
				aoResponse.setRenderParameter("next_action", "open");
			}
			else
			{
				lsAction = basicLanGeoPopSubSectionAction(aoRequest, aoResponse, lsAction, lsSectionName,
						lsSubSectionName, lsOrgId, lsAppStatus, lsUserRoles, lsUserId, lsBusnessAppId);
			}
			if (lsAction != null
					&& !lsAction.equals("removeDocFromApplication")
					&& !lsAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO)
					&& !lsAction.equalsIgnoreCase("fileupload")
					&& !lsAction.equalsIgnoreCase("submitDocId")
					&& !(lsAction.equals("displayDocument") || lsAction.equals("applicationSubmit") || lsAction
							.equals("businesswithdraw")))
			{
				setRenderParamsAction(aoRequest, aoResponse, lsAction, lsBusnessAppId, lbIsDomWithError, lsAjaxCall);
			}

			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in BusinessApplicationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			/** BEGIN Fix multi-tab Browsing QC6674 R7.1*/
			LOG_OBJECT.Error("BusinessApplicationController " + aoEx.toString());
			if (aoEx.toString().equalsIgnoreCase(ApplicationConstants.USER_EXIST_IN_SESSION)) {
				String lsRedirectPath = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
				+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";
				
				try {
					aoResponse.sendRedirect(lsRedirectPath);
				}
				catch (IOException ioEx)
				{
					LOG_OBJECT.Error("Exception while Redirect to ErrorPage, on detected dup-session for Tabbed Browsing", ioEx);
				}
			
			}
			else {
			/** END Fix multi-tab Browsing QC6674 R7.1*/
					LOG_OBJECT.Error("Error while execution of action Method in BusinessApplicationController", aoEx);
					String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.IS_AJAX_CALL);
					String lsErrorMsg = aoEx.toString();
					lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
					if (lsErrorMsg.contains("~"))
					{
						lsErrorMsg = lsErrorMsg.replace("~", ":");
					}
					LOG_OBJECT.Error("Application Exception in Document Vault", aoEx);
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
						catch (IOException ioEx)
						{
							LOG_OBJECT.Error("Messaging Exception occurred during file info action", ioEx);
						}
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
	 * This method manipulates organization info.
	 * 
	 * @param asOrgId - organization id
	 * @return - summary
	 * @throws ApplicationException - throws ApplicationException
	 */
	private ApplicationSummary doOrgInfo(String asOrgId) throws ApplicationException
	{
		Map<String, Object> loInputParams = new LinkedHashMap<String, Object>();
		Channel loChannelNew = new Channel();
		loInputParams.put("orgId", asOrgId);
		loChannelNew.setData("loInputParams", loInputParams);
		TransactionManager.executeTransaction(loChannelNew, "numberOfBrAppAgainstOrg");
		return (ApplicationSummary) loChannelNew.getData("loApplicationBean");
	}

	/**
	 * This method gets the app id.
	 * 
	 * @param aoRequest - ActionRequest
	 * @return - lsAppId
	 */
	private String getAppId(ActionRequest aoRequest)
	{
		String lsAppId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_APP_ID,
				PortletSession.APPLICATION_SCOPE);
		// get value from session if null
		if (lsAppId == null || lsAppId.equalsIgnoreCase(""))
		{
			lsAppId = (String) aoRequest.getPortletSession().getAttribute("applicationId");
		}
		return lsAppId;
	}

	/**
	 * This method gets the app status.
	 * 
	 * @param aoRequest - ActionRequest
	 * @return - lsAppStatus
	 */
	private String getAppStatus(ActionRequest aoRequest)
	{
		String lsAppStatus = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_APP_STATUS, PortletSession.APPLICATION_SCOPE);
		// get value from session if null
		if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
		{
			lsAppStatus = (String) aoRequest.getPortletSession().getAttribute("bussAppStatus");
		}
		return lsAppStatus;
	}

	/**
	 * This method gets the Business app id.
	 * 
	 * @param aoRequest - ActionRequest
	 * @return - lsBusnessAppId
	 */
	private String getBusnessAppId(ActionRequest aoRequest)
	{
		String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
		// get value from session if null
		if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase("") || lsBusnessAppId.equalsIgnoreCase(HHSConstants.NULL))
		{
			lsBusnessAppId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_BUSINESS_APP_ID);
		}
		return lsBusnessAppId;
	}

	/**
	 * This method set the action, business application id, module name
	 * parameter for modal and view
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asBuisAppId - Business Application Id
	 * @param abIsDomWithError - Dom Flag
	 * @param asAjaxCall - is ajax call
	 */
	private void setRenderParamsAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asBuisAppId, boolean abIsDomWithError, String asAjaxCall)
	{
		// Setting action, business application id, module name parameter
		// for modal and view
		if (abIsDomWithError)
		{
			asAction = "save";
		}
		if (asAjaxCall != null)
		{
			aoResponse.setRenderParameter("asAjaxCall", asAjaxCall);
		}
		if (null == (aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
				PortletSession.APPLICATION_SCOPE)))
		{
			aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
			if (asBuisAppId != null)
			{
				aoResponse.setRenderParameter(ApplicationConstants.KEY_BUSINESS_APP_ID, asBuisAppId);
			}

		}
	}

	/**
	 * This method is used to perform all the transaction related to language,
	 * geography and population
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @return asAction - Action object
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String basicLanGeoPopSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserId, String asBuisAppId) throws ApplicationException
	{
		// when other check box is checked for language section.
		String lsOtherChecked = (String) aoRequest.getParameter("other_checked");
		String lsLanguageInterpretation = (String) aoRequest.getParameter("language_interpretation_services");
		if (lsOtherChecked != null)
		{
			aoResponse.setRenderParameter("isOtherSelected", lsOtherChecked);
		}
		if (lsLanguageInterpretation != null)
		{
			aoResponse.setRenderParameter("isLanguageInterpretation", lsLanguageInterpretation);
		}
		LOG_OBJECT.Debug("4 before get business application section::" + asSectionName + " Subsection::"
				+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
		BusinessApplication loBusiness = BusinessApplicationFactory.getBusinessApplication(asSectionName,
				asSubSectionName);
		String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
				ApplicationConstants.SAVE_TRANSACTION);
		Channel loChannel = loBusiness.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null,
				asAction, asUserRoles, aoRequest, null);
		loChannel.setData("asAppId", asBuisAppId);
		loChannel.setData("asSection", asSectionName);
		loChannel.setData("asUserID", asUserId);
		// Other language selected list is not null.
		if (lsOtherChecked != null && !lsOtherChecked.equalsIgnoreCase("")
				&& ((ArrayList<String>) loChannel.getData("aoElementIdOtherList")).size() <= 0)
		{
			aoResponse.setRenderParameter("errorOtherMsg", ApplicationConstants.LANGUAGE_ERROR_OTHERS);
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE;
		}
		// Required language from check box is not null.
		if (loChannel.getData("aoElementIdCheckedList") != null
				&& ((ArrayList<String>) loChannel.getData("aoElementIdCheckedList")).size() <= 0
				&& (lsOtherChecked != null) && lsOtherChecked.equalsIgnoreCase(""))
		{
			aoResponse.setRenderParameter("errorMsg", "You must select at least one language.");
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE;
		}

		// Error list if language validation is not met.
		else if (loChannel.getData("errorList") != null
				&& ((ArrayList<String>) loChannel.getData("errorList")).size() > 0)
		{
			ApplicationSession.setAttribute((ArrayList<String>) loChannel.getData("errorList"), aoRequest, "errorList");
			asAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE;
		}
		else
		{ // Execute the transaction obtained from the file
			// langauge.java.
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			ApplicationSession.setAttribute((List<String>) loChannel.getData("loPopulation"), aoRequest,
					"populationListAfterSave");
			ApplicationSession.setAttribute((List<String>) loChannel.getData("loTaxonomyTreeList"), aoRequest,
					"taxonomyListAfterSave");

			ApplicationSession.setAttribute((List<String>) loChannel.getData("loTaxonomyIdList"), aoRequest,
					"returnGeographyList");
		}
		return asAction;
	}

	/**
	 * This method is used while updating accounting period
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asBuisAppId - Business Application Id
	 * @param aoBusinessApp - Business Application factory
	 * @return
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String newPeriodFormSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus, String asBuisAppId,
			BusinessApplication aoBusinessApp) throws ApplicationException
	{
		String lsNewFromMonth = null;
		String lsNewToMonth = null;
		String lsProviderName = null;
		String lsCityUrl = " ";
		if (null != aoRequest.getParameter("Frommonth"))
		{
			lsNewFromMonth = aoRequest.getParameter("Frommonth");
		}
		if (null != aoRequest.getParameter("Tomonth"))
		{
			lsNewToMonth = aoRequest.getParameter("Tomonth");
		}
		Channel loChannel;
		loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null, asAction,
				null, aoRequest, asSubSectionName);
		String lsTransactionName = (String) loChannel.getData("transaction_name");
		// Execute the transaction obtained from the file
		// NewPeriodForm.java.
		TransactionManager.executeTransaction(loChannel, lsTransactionName);

		String lsUpdateAccPeriodFlag = (String) loChannel.getData("dueDateSet");
		aoResponse.setRenderParameter("lbUpdateAccPeriodFlag", lsUpdateAccPeriodFlag);
		if (lsUpdateAccPeriodFlag.equalsIgnoreCase("yes"))
		{
			TransactionManager.executeTransaction(loChannel, "updateAudit");
			// Sending notification to Accelerator Manger NT036
			if (null != FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(), asOrgId))
			{
				lsProviderName = StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(
						FileNetOperationsUtils.getProviderList(), asOrgId));
			}
			HashMap<Object, String> loParamMap = new HashMap<Object, String>();
			String lsOldFromMonth = aoRequest.getParameter("oldFromMonth");
			String lsOldToMonth = aoRequest.getParameter("oldToMonth");
			loParamMap.put("PROVIDER", lsProviderName);
			loParamMap.put("OldFromMonth", lsOldFromMonth);
			loParamMap.put("OldToMonth", lsOldToMonth);
			loParamMap.put("NewFromMonth", lsNewFromMonth);
			loParamMap.put("NewToMonth", lsNewToMonth);

			lsCityUrl = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_CITY_URL);
			String lsURLNotificationBasicForm = lsCityUrl + "/portal/hhsweb.portal";
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add("NT036");
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loLinkMap.put("LINK", lsURLNotificationBasicForm);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loHmNotifyParam.put("NT036", loNotificationDataBean);
			loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loHmNotifyParam.put(HHSConstants.MODIFIED_BY, lsUserId);
			loHmNotifyParam.put(HHSConstants.ENTITY_ID, asBuisAppId);
			loHmNotifyParam.put(HHSConstants.ENTITY_TYPE, HHSConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
			loHmNotifyParam.put(TransactionConstants.ACCELERATOR_ID, ApplicationConstants.CITY_ORG);
			loChannel.setData("loHmNotifyParam", loHmNotifyParam);
			TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
		}
		asAction = "updateAccountingPeriod";
		aoResponse.setRenderParameter("fb_formName", "OrgProfile");
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION,
				ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION,
				ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
		return asAction;
	}

	/**
	 * This method is used to show application history for business application
	 * 
	 * @param aoRequest - Action Request
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asBuisAppId - Business Application Id
	 * @param aoBusinessApp - Business Application factory
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void applicationHistorySubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse,
			String asAction, String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus,
			String asBuisAppId, BusinessApplication aoBusinessApp, String asOrgType) throws ApplicationException
	{
		// Business Withdrawal
		if (asAction != null && asAction.equalsIgnoreCase("businesswithdraw"))
		{
			Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null,
					asAction, null, aoRequest, asSubSectionName);
			String lsTransactionName = (String) loChannel.getData("transaction_name");
			// Execute the transaction obtained from the file
			// ApplicationHistory.java.
			try
			{
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
			}
			catch (Exception aoEx)
			{
				aoResponse.setRenderParameter("withdrawErrorMsg",
						"Your withdrawal request could not be submitted at this time. Please try again.");
				LOG_OBJECT.Error("Error while launching Wrokflow.", aoEx);
			}

			Boolean lbWithdrawFlag = (Boolean) loChannel.getData("withdrawlBusUpdateStatus");
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
		// setting the new accounting period
	}

	/**
	 * This block controls the flow of application submission, checks for user
	 * validation, makes entry in Audit, launches workflow and redirects to
	 * summary page
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserNameForAppSubmit - User name of user submitting the form
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param asAppId - Application Id
	 * @param aoUserSession - File net session
	 * @param aoBusinessApp - Business Application factory
	 * @param aoBusinessStatusBeanMap - Current Business status map
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void applicationSubmitAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserNameForAppSubmit, String asUserId, String asBuisAppId, String asAppId,
			P8UserSession aoUserSession, BusinessApplication aoBusinessApp,
			Map<String, StatusBean> aoBusinessStatusBeanMap, String asOldSectionName) throws ApplicationException
	{
		
		LOG_OBJECT.Debug("applicationSubmitAction ");
		PortletSession loPortletSession = aoRequest.getPortletSession();
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APP_ID, asBuisAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		Map<String, Object> loMapToRender = new HashMap<String, Object>();
		doGetApplicationStatus(asAppStatus, loPortletSession, asUserId, asOldSectionName, loChannel, loMapToRender);
		Boolean lbApplicationStatus = (Boolean) loMapToRender.get("applicationStatus");
		if (lbApplicationStatus != null && lbApplicationStatus)
		{
			String lsSubmitUserId = null;
			String lsUserOrgType = null;
			if (null != aoRequest.getParameter("userName"))
			{
				lsSubmitUserId = aoRequest.getParameter("userName");
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
				// LDAP Authentication
				Boolean lbAuthenticated = false;
				//***Start SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
				lbAuthenticated = isAuthenticatedUser(lsSubmitUserId, lsSubmitUserPassword, lsUserOrgType);
				
				/*
				try
				{					
					Authentication.authenticate(lsSubmitUserId, lsSubmitUserPassword);
					lbAuthenticated = true;
					
				}
				catch (LoginException aoAppEx)
				{
					LOG_OBJECT.Error("User authentication Failed on Application Submission ", aoAppEx);
					lbAuthenticated = false;
				}
				*/				
				
				// check for user in DB if LDAP authentication fails
				if (!lbAuthenticated && !ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
				{
					lbAuthenticated = authenticateUser(lsSubmitUserId, lsSubmitUserPassword);
				}
				// LDAP Authentication END
				 
				
				//**End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
				
				
				LOG_OBJECT.Debug("User authentication Done! on Application Submission ");
				if (lbAuthenticated)
				{
					submitApplicationAfterAuthentication(aoRequest, aoResponse, asAction, asSectionName, asOrgId,
							asAppStatus, asUserRoles, asUserNameForAppSubmit, asUserId, asBuisAppId, asAppId,
							aoUserSession, aoBusinessApp, aoBusinessStatusBeanMap);
				}
				else
				{
					// LDAP Authentication failed
					aoResponse
							.setRenderParameter(
									"loginErrorMsg",
									"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
					// R 7.8
					aoResponse
							.setRenderParameter(
									"errorMsg",
									"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
				}

			}
			else
			{
				// Username enter is incorrect
				aoResponse
						.setRenderParameter(
								"loginErrorMsg",
								"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
				// R 7.8
				aoResponse
						.setRenderParameter(
								"errorMsg",
								"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
			}
		}
		else
		{
			aoResponse.setRenderParameter("loginErrorMsg",
					"This application is not complete, hence can not be submitted. ");
		}
	}

	

	/**
	 * This method submits Application After Authentication.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAction - Action
	 * @param asSectionName - Section Name
	 * @param asOrgId - Organization Id
	 * @param asAppStatus - App Status
	 * @param asUserRoles - User Roles
	 * @param asUserNameForAppSubmit - User Name For App Submit
	 * @param asUserId - User Id
	 * @param asBuisAppId - Business App Id
	 * @param asAppId - Application Id
	 * @param aoUserSession - P8UserSession
	 * @param aoBusinessApp - BusinessApplication
	 * @param aoBusinessStatusBeanMap - map of status bean
	 * @throws ApplicationException - throws ApplicationException
	 */
	private synchronized void submitApplicationAfterAuthentication(ActionRequest aoRequest, ActionResponse aoResponse,
			String asAction, String asSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserNameForAppSubmit, String asUserId, String asBuisAppId, String asAppId,
			P8UserSession aoUserSession, BusinessApplication aoBusinessApp,
			Map<String, StatusBean> aoBusinessStatusBeanMap) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("bussAppId", asBuisAppId);
		TransactionManager.executeTransaction(loChannel, "fetchAppStatus");
		asAppStatus = (String) loChannel.getData("appStatus");
		loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null, asAction,
				asUserRoles, aoRequest, null);
		List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.PROV_LIST);
		loChannel.setData("asAppId", asAppId);
		loChannel.setData("asUserId", asUserNameForAppSubmit);
		loChannel.setData("userEmailId", asUserId);
		loChannel.setData("asStaffId", asUserId);
		loChannel.setData("asProviderName",
				StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(loProviderBeanList, asOrgId)));
		loChannel.setData("aoUserSession", aoUserSession);
		loChannel.setData("workFlowName", P8Constants.PROPERTY_BR_APPLICATION_WORKFLOW_NAME);
		String lsTransactionName = "";

		Map<String, StatusBean> loBusinessStatusBeanSectionMap = (Map<String, StatusBean>) aoBusinessStatusBeanMap;
		StatusBean lsStatusBean = null;
		String lsSectionStatus = ApplicationConstants.NOT_STARTED_STATE;
		if (loBusinessStatusBeanSectionMap != null && loBusinessStatusBeanSectionMap.get("basics") != null
				&& !loBusinessStatusBeanSectionMap.isEmpty())
		{
			lsStatusBean = (StatusBean) loBusinessStatusBeanSectionMap.get("basics");
			lsSectionStatus = lsStatusBean.getMsSectionStatusToDisplay();
		}
		loChannel.setData("asSectionStatus", lsSectionStatus);
		if (asAppStatus != null && asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW))
		{
			String lsUrl = aoRequest
					.getContextPath()
					.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
							+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=false");
			try
			{
				aoResponse.sendRedirect(lsUrl);
			}
			catch (IOException aoEx)
			{
				LOG_OBJECT.Error("IOException occurred:", aoEx);
			}
		}
		else
		{
			if (asAppStatus != null
					&& (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) || asAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
			{
				loChannel.setData("asStatusIsReturned", "");
				loChannel.setData("asStatusIsReturnedSection", "");
				ArrayList<String> loSectionIdList = new ArrayList<String>();
				String lsStatusIsReturned = "";
				if (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))
				{
					String[] loSectionIds = ApplicationConstants.SECTION_NAMES;
					loSectionIdList = new ArrayList<String>(Arrays.asList(loSectionIds));
					lsStatusIsReturned = ApplicationConstants.STATUS_DEFFERED;
				}
				else if (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				{
					lsStatusIsReturned = iterateBussStatusMap(aoBusinessStatusBeanMap, loSectionIdList,
							lsStatusIsReturned);
				}
				loChannel.setData("asStatusIsReturnedSection", loSectionIdList);
				loChannel.setData("asStatusIsReturned", lsStatusIsReturned);
				loChannel.setData("aoSectionIds", loSectionIdList);
				loChannel.setData("aoFilenetSession", aoUserSession);
				loChannel.setData("asOrgId", asOrgId);
				loChannel.setData("asBussAppId", asBuisAppId);
				loChannel.setData("asUserId", asUserId);
				loChannel.setData("userEmailId", asUserId);
				loChannel.setData("asStaffId", asUserId);
				lsTransactionName = "getLaunchWorkflowMapDeferredRevision";
			}

			else
			{
				loChannel.setData("asUserId", asUserId);
				lsTransactionName = "getLaunchWorkflowMap";
			}
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			if (loChannel.getData("lbWFLaunchStatus") != null && (Boolean) loChannel.getData("lbWFLaunchStatus"))
			{
				Map<String, String> loOrgDetails = new HashMap<String, String>();
				loChannel.setData("orgId", asOrgId);
				TransactionManager.executeTransaction(loChannel, "fetchOrganizationStatus");
				String lsCurrentProviderStatus = (String) loChannel.getData("providerStatus");
				String lsNewProviderStatus = ProviderStatusBusinessRules
						.getProviderStatusAtBRApplicationSubmission(lsCurrentProviderStatus);
				if (lsNewProviderStatus != null && !lsNewProviderStatus.equalsIgnoreCase(""))
				{
					loOrgDetails.put("orgId", asOrgId);
					loOrgDetails.put("orgStatus", lsNewProviderStatus);
					loOrgDetails.put("userId", asUserId);
					loChannel.setData("orgDetails", loOrgDetails);
					String lsData = "Status Changed To ".concat(lsNewProviderStatus);
					String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(
							FileNetOperationsUtils.getProviderList(), asOrgId);
					CommonUtil.addAuditDataToChannel(loChannel, asOrgId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
							ApplicationConstants.STATUS_CHANGE, new Date(System.currentTimeMillis()),
							asUserNameForAppSubmit, lsData, "Provider", asOrgId, "false", asBuisAppId, "",
							ApplicationConstants.AUDIT_TYPE_APPLICATION);
					loChannel.setData("EntityIdentifier", lsEntityIdentifier);
					// Start R5 : set EntityId and EntityName for AutoSave
					CommonUtil.setChannelForAutoSaveData(loChannel, asBuisAppId, HHSR5Constants.BASIC);
					// End R5 : set EntityId and EntityName for AutoSave

					TransactionManager.executeTransaction(loChannel, "updateOrganizationTable");
				}
				String lsUrl = aoRequest
						.getContextPath()
						.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
								+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=true");
				try
				{
					aoResponse.sendRedirect(lsUrl);
				}
				catch (IOException aoAppEx)
				{
					LOG_OBJECT
							.Error("Error occured while redirecting to Application Summary Page after sucesfully submission of application ",
									aoAppEx);
				}
			}
			else
			{
				aoResponse.setRenderParameter("loginErrorMsg",
						"!! Failed to submit Application, Workflow Launch Failed.");
			}
		}
	}

	/**
	 * This method iterates Business Status Map.
	 * 
	 * @param aoBusinessStatusBeanMap - Business Status Bean Map
	 * @param aoSectionIdList - Section Id List
	 * @param asStatusIsReturned - Status Is Returned
	 * @return asStatusIsReturned - Status Is Returned
	 */
	private String iterateBussStatusMap(Map<String, StatusBean> aoBusinessStatusBeanMap,
			ArrayList<String> aoSectionIdList, String asStatusIsReturned)
	{
		Iterator loIterator = aoBusinessStatusBeanMap.entrySet().iterator();
		while (loIterator.hasNext())
		{
			Map.Entry loStatus = (Map.Entry) loIterator.next();
			StatusBean loStatusBean = (StatusBean) loStatus.getValue();
			if (loStatusBean.getMsSectionStatusToDisplay().equalsIgnoreCase(
					ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
					|| loStatusBean.getMsSectionStatusOnInnerSummary().equalsIgnoreCase(
							ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
					|| loStatusBean.getMsSectionStatusToDisplay()
							.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)
					|| loStatusBean.getMsSectionStatusOnInnerSummary().equalsIgnoreCase(
							ApplicationConstants.COMPLETED_STATE))
			{
				aoSectionIdList.add((String) loStatus.getKey());
				asStatusIsReturned = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
			}
			loIterator.remove();
		}
		return asStatusIsReturned;
	}

	/**
	 * This method submits the application
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserNameForAppSubmit - User name of user submitting the form
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param aoUserSession - File net session
	 * @param aoBusinessApp - Business Application factory
	 * @param aoBusinessStatusBeanMap - Current Business status map
	 * @throws ApplicationException - throws ApplicationException
	 */
	private synchronized void applicationSubmitSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse,
			String asAction, String asSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserNameForAppSubmit, String asUserId, String asBuisAppId, P8UserSession aoUserSession,
			BusinessApplication aoBusinessApp, Map<String, StatusBean> aoBusinessStatusBeanMap, String asOldSectionName)
			throws ApplicationException
	{
		LOG_OBJECT.Error("applicationSubmitSubSectionAction ");
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APP_ID, asBuisAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		Boolean lbApplicationStatus = applicationStatusBeforeSubmission(asBuisAppId, asOrgId);
		if (lbApplicationStatus != null && lbApplicationStatus)
		{
			if (asAction != null && asAction.equalsIgnoreCase("applicationSubmit"))
			{
				String lsSubmitUserId = null;
				String lsUserOrgType = null;
				if (null != aoRequest.getParameter("userName"))
				{
					lsSubmitUserId = aoRequest.getParameter("userName");
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
					catch (LoginException aoAppEx)
					{
						LOG_OBJECT.Error("User authentication Failed on Application Submission ", aoAppEx);
						lbAuthenticated = false;
					}
					*/
					if (!lbAuthenticated && !ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
					{
						lbAuthenticated = authenticateUser(lsSubmitUserId, lsSubmitUserPassword);
					}
					
					//**End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
					
					if (lbAuthenticated)
					{
						Channel loChannelObj = new Channel();
						loChannelObj.setData("asAppId", asBuisAppId);
						loChannelObj.setData("asOrgId", asOrgId);
						TransactionManager.executeTransaction(loChannelObj, "getCompleteStatusMap");
						Map loServiceStatusMap = (Map) loChannelObj.getData("loServiceStatusBeanMap");
						loChannelObj = new Channel();
						loChannelObj.setData("lsBusinessAppId", asBuisAppId);
						TransactionManager.executeTransaction(loChannelObj, "getDraftServiceApplicationId");
						List<WithdrawRequestDetails> loServiceIdList = (List<WithdrawRequestDetails>) loChannelObj
								.getData("serviceAppIds");
						if (loServiceIdList.isEmpty())
						{
							String lsUrl = aoRequest
									.getContextPath()
									.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
											+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=false");
							try
							{
								aoResponse.sendRedirect(lsUrl);
							}
							catch (IOException aoAppEx)
							{
								LOG_OBJECT.Error("Error occured while launching Application Submission Workflow ",
										aoAppEx);
							}
						}
						else
						{
							Iterator<WithdrawRequestDetails> loIterator = loServiceIdList.iterator();
							boolean lbSubmitAppFlag = false;
							while (loIterator.hasNext())
							{
								lbSubmitAppFlag = iterateServiceAppList(aoRequest, aoResponse, asAction, asSectionName,
										asOrgId, asAppStatus, asUserId, asBuisAppId, aoBusinessApp, loServiceStatusMap,
										loIterator, lbSubmitAppFlag);
							}
						}

					}
					else
					{
						aoResponse
								.setRenderParameter(
										"loginErrorMsg",
										"The username or password you have entered is incorrect. Please enter the correct username and password to e-sign this application");
						// R 7.8
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
					// R 7.8
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
					"This application cannot be submitted until the business application is completed.");
		}
	}

	/**
	 * This method iterates Service App List.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAction - Action
	 * @param asSectionName - Section Name
	 * @param asOrgId - Organization Id
	 * @param asAppStatus - App Status
	 * @param asUserId - User Id
	 * @param asBuisAppId - Business App Id
	 * @param aoBusinessApp - Business App
	 * @param aoServiceStatusMap - Service Status Map
	 * @param aoIterator - Iterator reference
	 * @param abSubmitAppFlag - Submit App Flag
	 * @return lbSubmitAppFlag - Submit App Flag
	 * @throws ApplicationException - throws ApplicationException
	 */
	private boolean iterateServiceAppList(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asOrgId, String asAppStatus, String asUserId, String asBuisAppId,
			BusinessApplication aoBusinessApp, Map aoServiceStatusMap, Iterator<WithdrawRequestDetails> aoIterator,
			boolean abSubmitAppFlag) throws ApplicationException
	{
		WithdrawRequestDetails loWithdrawRequestDetails = aoIterator.next();
		String lsServiceApplicationId = loWithdrawRequestDetails.getMsAppId();
		aoRequest.setAttribute("lsServiceApplicationId", lsServiceApplicationId);
		if (lsServiceApplicationId != null && !lsServiceApplicationId.equalsIgnoreCase(HHSConstants.NULL))
		{
			StatusBean loStatusBean = (StatusBean) aoServiceStatusMap.get(lsServiceApplicationId);
			Map<String, String> loHMSubSectionDetails = loStatusBean.getMoHMSubSectionDetails();
			Iterator loMapIterator = loHMSubSectionDetails.entrySet().iterator();
			while (loMapIterator.hasNext())
			{
				Map.Entry loEntry = (Map.Entry) loMapIterator.next();
				String lsSectionStatus = (String) loEntry.getValue();
				if (lsSectionStatus != null && lsSectionStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
				{
					abSubmitAppFlag = true;
				}
				else
				{
					abSubmitAppFlag = false;
					break;
				}
			}
			if (abSubmitAppFlag)
			{
				Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus,
						null, asAction, asUserId, aoRequest, null);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsServiceApplicationId,
						HHSR5Constants.SERVICE_APPLICATION);
				// End R5 : set EntityId and EntityName for AutoSave

				String lsTransactionName = (String) loChannel.getData("transaction_name");
				// Execute the transaction obtained from the
				// file ApplicationSubmission.java file.
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
				if (loChannel.getData("lbWFLaunchStatus") != null && (Boolean) loChannel.getData("lbWFLaunchStatus"))
				{
					Map<String, String> loOrgDetails = new HashMap<String, String>();
					loChannel.setData("orgId", asOrgId);
					TransactionManager.executeTransaction(loChannel, "fetchOrganizationStatus");
					String lsCurrentProviderStatus = (String) loChannel.getData("providerStatus");
					String lsNewProviderStatus = ProviderStatusBusinessRules
							.getProviderStatusAtServiceApplicationSubmission(lsCurrentProviderStatus, asAppStatus);
					if (lsNewProviderStatus != null
							&& !(lsNewProviderStatus.equalsIgnoreCase("") || lsNewProviderStatus
									.equalsIgnoreCase(lsCurrentProviderStatus)))
					{
						loOrgDetails.put("orgId", asOrgId);
						loOrgDetails.put("orgStatus", lsNewProviderStatus);
						loOrgDetails.put("userId", asUserId);
						loChannel.setData("orgDetails", loOrgDetails);
						String lsData = "Status Changed To ".concat(lsNewProviderStatus);
						String lsUserNameForAppSubmit = (String) aoRequest.getPortletSession().getAttribute(
								ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
						String lsEntityIdentifier = FileNetOperationsUtils.getProviderName(
								FileNetOperationsUtils.getProviderList(), asOrgId);
						CommonUtil.addAuditDataToChannel(loChannel, asOrgId,
								ApplicationConstants.PROVIDER_STATUS_CHANGE, ApplicationConstants.STATUS_CHANGE,
								new Date(System.currentTimeMillis()), lsUserNameForAppSubmit, lsData, "Provider",
								asOrgId, "false", asBuisAppId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
						loChannel.setData("EntityIdentifier", lsEntityIdentifier);
						TransactionManager.executeTransaction(loChannel, "updateOrganizationTable");
					}
					String lsUrl = aoRequest
							.getContextPath()
							.concat("/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider&"
									+ ApplicationConstants.WORKFLOW_SUCCESS_KEY + "=true");
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
					aoResponse.setRenderParameter("loginErrorMsg",
							"!! Failed to submit Application, Workflow Launch Failed.");
				}
			}
		}
		return abSubmitAppFlag;
	}

	/**
	 * This method processes the action for defered and returned for revision
	 * status
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param aoUserSession - File net session
	 * @param aoBusinessApp - Business Application factory
	 * @param aoBusinessStatusBeanMap - Current Business status map
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void appSubmitSubSecDefRevAction(ActionRequest aoRequest, String asAction, String asSectionName,
			String asOrgId, String asAppStatus, String asUserRoles, String asUserId, String asBuisAppId,
			P8UserSession aoUserSession, BusinessApplication aoBusinessApp,
			Map<String, StatusBean> aoBusinessStatusBeanMap) throws ApplicationException
	{
		Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null,
				asAction, asUserRoles, aoRequest, null);
		loChannel.setData("asStatusIsReturnedSection", "");
		ArrayList<String> loSectionIdList = new ArrayList<String>();
		String lsStatusIsReturned = "";
		if (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))
		{
			String[] loSectionIds = ApplicationConstants.WORKFLOW_LAUNCH_SECTION_IDS_SEQUENCE;
			loSectionIdList = new ArrayList<String>(Arrays.asList(loSectionIds));
			lsStatusIsReturned = ApplicationConstants.STATUS_DEFFERED;
		}
		else if (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			lsStatusIsReturned = iterateBussStatusMap(aoBusinessStatusBeanMap, loSectionIdList, lsStatusIsReturned);
		}
		loChannel.setData("asStatusIsReturnedSection", loSectionIdList);
		loChannel.setData("asStatusIsReturned", lsStatusIsReturned);
		loChannel.setData("aoSectionIds", loSectionIdList);
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asOrgId", asOrgId);
		loChannel.setData("asBussAppId", asBuisAppId);
		loChannel.setData("asUserId", asUserId);
		loChannel.setData("sectionId", asSectionName);
		loChannel.setData("aoUserSession", aoUserSession);
		String lsTransactionName = "getLaunchWorkflowMapDeferredRevisionService";
		// Execute the transaction to launch worlkflow.
		TransactionManager.executeTransaction(loChannel, lsTransactionName);
	}

	/**
	 * This method perform action related to document selection/upload Updated
	 * for release 3.3.0, Defect 6451
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asAjaxCall - String depicting if its an ajax call
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param aoUserSession - File net session
	 * @return action to be performed in render
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String documentListSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asAjaxCall, String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus,
			String asUserRoles, String asUserId, String asBuisAppId, P8UserSession aoUserSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("5 before get business application section::" + asSectionName + " Subsection::"
				+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
		BusinessApplication loBusiness = BusinessApplicationFactory.getBusinessApplication(asSectionName,
				asSubSectionName);
		String lsFilePath = null;
		Channel loChannel = null;

		// Open the overlay when we upload document from the drop down on
		// basic, board, filings and policies form.
		if (!("fileinformation".equalsIgnoreCase(asAction) || "fileupload".equalsIgnoreCase(asAction) || "backrequest"
				.equalsIgnoreCase(asAction)))
		{
			loChannel = loBusiness.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, lsFilePath,
					asAction, asUserRoles, aoRequest, null);
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
					lsTransactionName = (String) loChannel.getData("transaction_name");
					// Execute the transaction obtained from the file
					// DocumentBusinessApp.java.
					TransactionManager.executeTransaction(loChannel, lsTransactionName);
				}
			}
			if ("documentupload".equalsIgnoreCase(asAction))
			{
				aoResponse.setRenderParameter("action", "enahnceddocumentvault");
				aoResponse.setRenderParameter("render_action", asAction);
			}
			else if ("displayDocProp".equalsIgnoreCase(asAction))
			{
				aoResponse.setRenderParameter("next_action", "open");
			}
			// Overlay while selecting document from the vault from the drop
			// down.
			else if (null != asAction && asAction.equals("selectDocFromVault"))
			{
				selectDocFromVaultAction(aoRequest, aoResponse, asSectionName, asUserId, loChannel);
			}
			// Submit the selected document from the list of document from
			// vault.
			else if (null != asAction && asAction.equals("submitDocId"))
			{
				aoResponse.setRenderParameter("next_action", "open");
			}
			// Remove the document entry for a given document type.
			else if ("removeDocFromApplication".equalsIgnoreCase(asAction))
			{
				aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, "open");
				ApplicationSession.setAttribute("The document was successfully removed from business application.",
						aoRequest, "message");
				ApplicationSession.setAttribute(ApplicationConstants.MESSAGE_PASS_TYPE, aoRequest, "messageType");
			}
			/*
			 * When user selects view document information from drop down it
			 * calls actionViewDocumentInfo(Request,Response) method
			 */
			else if (asAction.equals(ApplicationConstants.VIEW_DOCUMENT_INFO))
			{
				aoRequest.setAttribute("orgType", ApplicationConstants.PROVIDER_ORG);
				FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
				aoResponse
						.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.VIEW_DOCUMENT_INFO);
				aoRequest.removeAttribute("orgType");
			}
		}
		else
		{
			// Second overlay while uploading document from the drop down.
			if ("fileinformation".equalsIgnoreCase(asAction))
			{
				fileInfoAction(aoRequest, aoResponse);
			}
			// Back request from the second overlay while uploading
			// document.
			else if ("backrequest".equalsIgnoreCase(asAction))
			{
				// Updated for release 3.3.0, Defect 6451
				BusinessApplicationUtil.backRequestAction(aoRequest);
			}
			// First overlay while uploading document from the drop down.
			else if ("fileupload".equalsIgnoreCase(asAction))
			{
				fileUploadAction(aoRequest, aoResponse, asAjaxCall, asSectionName, asOrgId, asUserId, asBuisAppId,
						aoUserSession);
			}
		}
		return asAction;
	}

	/**
	 * This method executes when a user click on the edit document properties
	 * action
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @throws ApplicationException when any exception oocurred we wrap it into
	 *             this custom exception
	 */
	public void editDocumentPropertiesAction(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		Map<String, String> loEditDocsMap = new HashMap<String, String>();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loEditDocsMap.put(lsUserId, aoRequest.getParameter(ApplicationConstants.DOCUMENT_ID).toString());
		synchronized (this)
		{
			BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.EDIT_DOC_LIST_MAP, loEditDocsMap);
		}
		FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
		aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.EDIT_DOCUMENT_PROPS);
	}

	/**
	 * This method gets document detail to be selected from vault
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asSectionName - Current Section Name
	 * @param asUserId - User id of current User
	 * @param aoChannel - Channel Object
	 * @throws ApplicationException
	 */
	private void selectDocFromVaultAction(ActionRequest aoRequest, ActionResponse aoResponse, String asSectionName,
			String asUserId, Channel aoChannel) throws ApplicationException
	{
		LOG_OBJECT
				.Debug("selectDocFromVaultAction:: Select Document From Application in Business Applcaition Document");
		Object loDocResultObject = aoChannel.getData(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_LIST_PARAMETER);
		// List of Document from Document Vault.
		if (loDocResultObject != null)
		{
			List loDocResult = (List) loDocResultObject;
			List<DocumentsSelFromDocVault> loLSelectedDocFromVault = new ArrayList<DocumentsSelFromDocVault>();
			Iterator loIterator = loDocResult.iterator();
			String lsFormName = aoRequest.getParameter("formName");
			String lsFormVesrion = aoRequest.getParameter("formVersion");
			String lsFormId = aoRequest.getParameter("formId");

			while (loIterator.hasNext())
			{
				HashMap loHMDocProps = (HashMap) loIterator.next();

				DocumentsSelFromDocVault loHMDocumentsObject = new DocumentsSelFromDocVault();

				if (loHMDocProps != null)
				{
					loHMDocumentsObject.setMsDocumentName((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_DOCUMENT_TITLE));
					// Added if else for Release 5
					if (null != loHMDocProps.get("DOC_LAST_MODIFIED"))
					{
						loHMDocumentsObject.setMsLastModifiedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps.get("DOC_LAST_MODIFIED"))))));
					}

					else
					{
						loHMDocumentsObject.setMsLastModifiedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps
										.get(P8Constants.PROPERTY_CE_LAST_MODIFIED_DATE))))));
					}
					loHMDocumentsObject.setMsLastModifiedBy((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY));
					loHMDocumentsObject.setMsDocumentId((Id) loHMDocProps.get(P8Constants.PROPERTY_CE_DOCUMENT_ID));
					loHMDocumentsObject.setMsSubmittedBy((String) loHMDocProps
							.get(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY));
					// Added if else for Release 5
					if (null != loHMDocProps.get("DOC_DATE_CREATED"))
					{
						loHMDocumentsObject.setMsSubmittedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps.get("DOC_DATE_CREATED"))))));
					}
					else
					{
						loHMDocumentsObject.setMsSubmittedDate(((DateUtil
								.getDateMMddYYYYFormat(((java.util.Date) loHMDocProps
										.get(P8Constants.PROPERTY_CE_DATE_CREATED))))));
					}
					if (null != loHMDocProps.get(HHSR5Constants.FOLDERS_FILED_IN))
					{
						loHMDocumentsObject.setFilePath(loHMDocProps.get(HHSR5Constants.FOLDERS_FILED_IN).toString());
					}
					loHMDocumentsObject.setMsDocumnetType(aoRequest.getParameter("docType"));
					loHMDocumentsObject.setMsDocumnetCategory(aoRequest.getParameter("docCategory"));
					loHMDocumentsObject.setMsFormName((String) lsFormName);
					loHMDocumentsObject.setMsFormVersion((String) lsFormVesrion);
					loHMDocumentsObject.setMsUserId((String) asUserId);
					loHMDocumentsObject.setMsDocumentTitle((String) loHMDocProps.get(P8Constants.DOCUMENT_TITLE));
					loHMDocumentsObject.setMsFormId((String) lsFormId);
					loHMDocumentsObject.setMsSectionId(asSectionName);
				}

				loLSelectedDocFromVault.add(loHMDocumentsObject);
			}
			ApplicationSession.setAttribute(loLSelectedDocFromVault, aoRequest, "document_list_fromvault");
			RFPReleaseDocsUtil.setReqRequestParameter(aoRequest.getPortletSession(), 
					(P8UserSession)aoChannel.getData(HHSConstants.AO_FILENET_SESSION), (String)aoChannel.getData(HHSConstants.NEXT_PAGE_PARAM));
			ApplicationSession.setAttribute(aoChannel.getData(HHSConstants.OBJECTS_PER_PAGE), aoRequest,ApplicationConstants.ALLOWED_OBJECT_COUNT);
		}
		aoResponse.setRenderParameter("docType", aoRequest.getParameter("docType"));
	}

	/**
	 * This method performs file upload action on document screen.
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAjaxCall - String depicting if its an ajax call
	 * @param asSectionName - Current Section Name
	 * @param asOrgId - Organization id
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param aoUserSession - File net session
	 */
	private void fileUploadAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAjaxCall,
			String asSectionName, String asOrgId, String asUserId, String asBuisAppId, P8UserSession aoUserSession)
	{
		Channel loChannel;
		String lsTransactionStartTime = CommonUtil.getCurrentTimeInMilliSec();
		try
		{
			LOG_OBJECT
					.Debug("fileUploadAction:: Upload File in Business Applcaition Document upload screen (Getting document id from filenet)");
			String lsDocumentId = FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
			// Entry into document table for business application.
			if (null != lsDocumentId && !lsDocumentId.equals(""))
			{
				com.nyc.hhs.model.Document loDocument = (com.nyc.hhs.model.Document) ApplicationSession.getAttribute(
						aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);

				loChannel = new Channel();
				loChannel.setData("applicationId", asBuisAppId);
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
				loChannel.setData("sectionId", asSectionName);
				loChannel.setData("asSubSectionIdNextTab", loDocument.getSectionId());
				loChannel.setData("asUserId", asUserId);
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
				LOG_OBJECT
						.Debug("fileUploadAction:: Upload File in Business Applcaition Document upload screen(Entering data into database) ");
				if (!loDocument.getSectionId().equalsIgnoreCase("servicessummary") || loDocument.getSectionId() == null)
				{
					// Execute the transaction to update document
					// table for business application while
					// uploading file.
					TransactionManager.executeTransaction(loChannel, "fileupload_bapp");
				}
				else if (loDocument.getSectionId().equalsIgnoreCase("servicessummary")
						&& loDocument.getSectionId() != null)
				{ // Execute the
					// transaction
					// to update
					// document
					// table for
					// business
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

				String lsErrorMsg = aoExp.toString();
				lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
				LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
				// Setting the required attribute.
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

		String lsTransactionEndTime1 = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
				CommonUtil.getItemDateInMIlisec(lsTransactionEndTime1));
		if (liTimediff > 3)
		{
			LOG_OBJECT.Error("!!!!!Ending fileUploadAction() in Business App Controller, TIME LAPSED," + liTimediff);
		}
		else
		{
			LOG_OBJECT.Error("Ending method fileUploadAction in Business App Controller :: TIME LAPSED :" + liTimediff);
		}
	}

	/**
	 * This method save the document related information in session to sustain
	 * its value.
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @throws ApplicationException - throws ApplicationException
	 */
	private void fileInfoAction(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
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
			ApplicationSession.setAttribute(lsDoccategory, aoRequest, "document_category");
			ApplicationSession.setAttribute(lsDocType, aoRequest, "document_type");
			ApplicationSession.setAttribute(lsSerAppId, aoRequest, "service_app_id");
			ApplicationSession.setAttribute(lsSecId, aoRequest, "section_id");
			LOG_OBJECT
					.Debug("fileInfoAction:: User click on the next button on first upload screen for business application(calling filenet actionFileInformation method)");
			FileNetOperationsUtils.actionFileInformation(aoRequest, aoResponse);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Messaging Exception occurred during file info action", aoEx);
			String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.IS_AJAX_CALL);
			String lsErrorMsg = aoEx.toString();
			lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
			LOG_OBJECT.Error("Application Exception in Document Vault", aoEx);
			// Setting the required attribute.
			if (null != lsAjaxCall && lsAjaxCall.equalsIgnoreCase(HHSR5Constants.TRUE))
			{
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
						ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
						PortletSession.APPLICATION_SCOPE);
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
				throw new ApplicationException("Exception occured while fetch file information ", aoEx);
			}
				
		}
		catch (PortletException aoEx)
		{
			LOG_OBJECT.Error("Portlet Exception occurred during file info action", aoEx);
		}
		catch (MessagingException aoEx)
		{
			LOG_OBJECT.Error("Messaging Exception occurred during file info action", aoEx);
		}
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("IO Exception occurred during file info action", aoEx);
		}
	}

	/**
	 * This method performs Legal name change related task on organization
	 * profile
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asBuisAppId - Business Application Id
	 * @return action to be passed to render
	 * @throws ApplicationException - throws ApplicationException
	 */
	private String leagalNameSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asBuisAppId) throws ApplicationException
	{
		LOG_OBJECT.Error("6 before get business application section::" + asSectionName + " Subsection::"
				+ asSubSectionName + " action::" + asAction + " url::" + aoRequest.getParameterMap());
		BusinessApplication loBusiness = BusinessApplicationFactory.getBusinessApplication(asSectionName,
				asSubSectionName);
		String lsFilePath = null;
		Channel loChannel = loBusiness.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, lsFilePath,
				asAction, asUserRoles, aoRequest, null);
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
				lsTransactionName = (String) loChannel.getData("transaction_name");
				// Execute the transaction obtained from the file
				// Questions.java.
				TransactionManager.executeTransaction(loChannel, lsTransactionName);
			}
		}
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION,
				ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
		aoResponse.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION,
				ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
		aoResponse.setRenderParameter("fb_formName", "OrgProfile");
		asAction = "updateAccountingPeriod";
		aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, asAction);
		return asAction;
	}

	/**
	 * This method performs Question related task
	 * 
	 * @param aoRequest - Action Request
	 * @param aoResponse - Action Response
	 * @param asAction - Action to be performed
	 * @param asSectionName - Current Section Name
	 * @param asSubSectionName - Current SubSection Name
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param asUserRoles - Current User Role
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param abIsDomWithError - boolean flag depicting if dom has error
	 * @param aoBusinessApp - Business Application factory
	 * @param asAjaxCall - String depicting if its an ajax call
	 * @return boolean flag depicting if dom has error
	 * @throws ApplicationException - throws ApplicationException
	 */
	private boolean questionsSubSectionAction(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserId, String asBuisAppId, boolean abIsDomWithError, BusinessApplication aoBusinessApp,
			String asAjaxCall) throws ApplicationException
	{
		String lsFormName = aoRequest.getParameter("fb_formName");
		aoResponse.setRenderParameter("fb_formName", lsFormName);
		Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBuisAppId, asAppStatus, null,
				asAction, asUserRoles, aoRequest, null);
		loChannel.setData("asUserId", asUserId);
		try
		{
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.SAVE_TRANSACTION);
			// Execute the transaction obtained from navigation xml to save
			// any sub section screen.
			loChannel.setData("asAjaxCall", asAjaxCall);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			loChannel.setData("asCorporateStructure",
					((HashMap<String, Object>) loChannel.getData("aoParameters")).get("CS"));
			// Start of changes for Release 3.10.0 : Enhancement 6572
			loChannel.setData("asSubSectionFilingFlag", aoRequest.getParameter("subSectionFilingFlag"));
			// End of changes for Release 3.10.0 : Enhancement 6572
			loChannel.setData("asUserRoleForDocument",
					loPortletSession.getAttribute("user_roles", PortletSession.APPLICATION_SCOPE));
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, asOrgId, HHSR5Constants.ORG_PROFILE);
			// End R5 : set EntityId and EntityName for AutoSave

			TransactionManager.executeTransaction(loChannel, lsTransactionName);

			aoResponse.setRenderParameter(ApplicationConstants.FORM_VERSION,
					(String) loChannel.getData(ApplicationConstants.FORM_VERSION));
			aoResponse.setRenderParameter(ApplicationConstants.FORMNAME,
					(String) loChannel.getData(ApplicationConstants.FORMNAME));

			// Set Dom Object in session.
			if (null != loChannel.getData(ApplicationConstants.DOM_RETURNED))
			{
				asAppStatus = (String) loChannel.getData("lsBussAppStatus");
				aoRequest.getPortletSession().setAttribute("bussAppStatus", asAppStatus);
				DomStatus loDomStatus = (DomStatus) loChannel.getData(ApplicationConstants.DOM_RETURNED);
				aoRequest.getPortletSession(true).setAttribute(ApplicationConstants.DOM_FOR_EDIT,
						loDomStatus.getDomObj(), PortletSession.APPLICATION_SCOPE);
				// Dom with error
				if (loDomStatus.isDomWithError())
				{
					abIsDomWithError = true;
					aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION,
							ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE);
				}
				// save and next for questions
				else if (asAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE_NEXT))
				{
					aoRequest.getPortletSession(true).removeAttribute(ApplicationConstants.DOM_FOR_EDIT);
					aoResponse.setRenderParameter("formname",
							(String) loChannel.getData(ApplicationConstants.FORM_VERSION));
					aoResponse.setRenderParameter("formversion",
							(String) loChannel.getData(ApplicationConstants.FORMNAME));
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while executing transaction for insertion of application information ",
					aoAppEx);
		}
		return abIsDomWithError;
	}

	/**
	 * This method gets complete Business Application status map and provide us
	 * the Status information for section or subsection
	 * 
	 * @param asOrgId - Organization id
	 * @param asAppStatus - Current Application Status
	 * @param aoPortletSession - Portlet Session
	 * @param asUserId - User id of current User
	 * @param asBuisAppId - Business Application Id
	 * @param asOldSectionName - Section Name
	 * @return Business Application status map
	 * @throws ApplicationException - throws ApplicationException
	 */
	private Map<String, StatusBean> doGetApplicationStatusAction(String asOrgId, String asAppStatus,
			PortletSession aoPortletSession, String asUserId, String asBuisAppId, String asOldSectionName)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APP_ID, asBuisAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannel.setData(ApplicationConstants.SECTION, asOldSectionName);
		loChannel.setData(ApplicationConstants.USER_ID, asUserId);
		// Execute the transaction to get the status of application.
		TransactionManager.executeTransaction(loChannel, "getCompleteStatusMap");
		Map<String, StatusBean> loBusinessStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loBusinessStatusBeanMap");
		Boolean lbApplicationStatus = ((Map<String, Boolean>) loChannel.getData("applicationStatus"))
				.get("completeStatus");
		if (lbApplicationStatus)
		{
			if (asAppStatus != null
					&& (asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
							|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || asAppStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)))
			{
				String lsProviderStatus = "";
				TransactionManager.executeTransaction(loChannel, "getProviderStatusFlag");
				WithdrawRequestDetails loWithdrawRequestDetails = (WithdrawRequestDetails) loChannel
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
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_NOT_APPLIED)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
									|| lsProviderStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
									|| lsProviderStatus
											.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED) || lsProviderStatus
										.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
					{
						lbApplicationStatus = false;
					}
				}
			}
			else
			{
				lbApplicationStatus = false;
			}
		}
		aoPortletSession.setAttribute("applicationStatus", lbApplicationStatus, PortletSession.APPLICATION_SCOPE);
		return loBusinessStatusBeanMap;
	}

	/**
	 * This method gets complete Service Application status map and provide us
	 * the Status information for services
	 * 
	 * @param asBuisAppId - Business Application Id
	 * @param asOrgId - Organization id
	 * @param asServiceApplicationId - Service Application Id
	 * @return Current Status of Service Application
	 * @throws ApplicationException - throws ApplicationException
	 */
	public String getCurrentServiceApplicationStatus(String asBuisAppId, String asOrgId, String asServiceApplicationId)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		loRequiredProps.put("providerId", asOrgId);
		loRequiredProps.put("businessAppId", asBuisAppId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator<ProviderStatusBean> loIterator = loProviderStatusBeanList.iterator();
		String lsServiceStatus = "";
		while (loIterator.hasNext())
		{
			ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asServiceApplicationId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsServiceStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsServiceStatus = loProviderStatusBean.getApplicationStatus();
				}
				return lsServiceStatus;
			}

		}
		return null;
	}

	/**
	 * This method gets the current application status
	 * 
	 * @param asBuisAppId - Business Application Id
	 * @param asOrgId - Organization id
	 * @return current status
	 * @throws lsBrStatus ApplicationException
	 */
	public String getCurrentApplicationStatus(String asBuisAppId, String asOrgId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		loRequiredProps.put("providerId", asOrgId);
		loRequiredProps.put("businessAppId", asBuisAppId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator<ProviderStatusBean> loIterator = loProviderStatusBeanList.iterator();
		String lsBrStatus = "";
		while (loIterator.hasNext())
		{
			ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBuisAppId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsBrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsBrStatus = loProviderStatusBean.getApplicationStatus();
				}
				return lsBrStatus;
			}

		}
		return null;
	}

	/**
	 * Method is used to read the content from the file net
	 * 
	 * @param aoRequest - Render Request
	 * @param asDocType - Doc Type to be fetched
	 * @return Content of document
	 * @throws IOException
	 */
	private String getTermsAndCondition(RenderRequest aoRequest, String asDocType) throws IOException
	{
		Channel loChannel = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		loChannel.setData("aoUserSession", loUserSession);
		loChannel.setData(HHSR5Constants.AS_DOC_TYPE, asDocType);
		try
		{
			// Execute the transaction to get Document Content By Type
			TransactionManager.executeTransaction(loChannel, "getDocumentContentByType");
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error occurred while getting terms and conditions", aoEx);
		}
		HashMap<String, InputStream> loIoMap = (HashMap<String, InputStream>) loChannel.getData("contentByType");
		String lsSystemTermsAndCond = "";
		if (!loIoMap.isEmpty())
		{
			Writer loWriter = new StringWriter();
			char[] loCharBuffer = new char[1024];
			Reader loReader = null;
			String loTempStr = loIoMap.keySet().iterator().next();
			try
			{
				loReader = new BufferedReader(new InputStreamReader(loIoMap.get(loTempStr)));
				int liTempVar;
				while ((liTempVar = loReader.read(loCharBuffer)) != -1)
				{
					loWriter.write(loCharBuffer, 0, liTempVar);
				}
				lsSystemTermsAndCond = loWriter.toString();
			}
			finally
			{
				loIoMap.get(loTempStr).close();
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

	/**
	 * This method authenticates user from db
	 * 
	 * @param aoUserId - User id to be authenticated
	 * @param aoPassword - User password
	 * @return boolean flag whether user is authentic user or not
	 * @throws ApplicationException - throws ApplicationException
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
	 * This method gets complete Service Application status map and provide us
	 * application Status Before Submission.
	 * 
	 * @param asBuisAppId - Business Application Id
	 * @param asOrgId - Organization id
	 * @return Boolean - lbApplicationStatus application Status Before Submission
	 * @throws ApplicationException - throws ApplicationException
	 */
	public Boolean applicationStatusBeforeSubmission(String asBusinessAppId, String asOrgId)
	{
		String lsServiceSummaryStatus = "";
		Channel loChannel = new Channel();// br id asServiceBusinessAppId
		loChannel.setData(ApplicationConstants.APP_ID, asBusinessAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		try
		{
			TransactionManager.executeTransaction(loChannel, "getCompleteStatusMap");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception occurred while executing transaction getCompleteStatusMap:", aoExp);
		}
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loServiceStatusBeanMap");
		Boolean lbApplicationStatus = ((Map<String, Boolean>) loChannel.getData("applicationStatus"))
				.get("completeStatus");
		Collection<StatusBean> loStatusBean = loServicesStatusBeanMap.values();
		Iterator<StatusBean> loIterator = loStatusBean.iterator();
		lsServiceSummaryStatus = getServiceStatus(lsServiceSummaryStatus, loIterator);
		if (lsServiceSummaryStatus.equalsIgnoreCase(""))
		{
			lbApplicationStatus = false;
			lsServiceSummaryStatus = "notstarted";
		}
		return lbApplicationStatus;
	}

	/**
	 * This method will redirect user to the next tab of the upload document
	 * screen step 2
	 * 
	 * @param aoRequest
	 * @param aoRes
	 * @throws ApplicationException
	 */
	@ActionMapping(params = "submit_action=getFolderLocation")
	protected void getFolderLocationForBA(ActionRequest aoRequest, ActionResponse aoRes) throws ApplicationException
	{
		// Added for R5- to get doc properties
		com.nyc.hhs.model.Document loDocument = new com.nyc.hhs.model.Document();
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		try
		{
			loDocument = (com.nyc.hhs.model.Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null == loDocument)
			{
				loDocument = (com.nyc.hhs.model.Document) aoRequest.getPortletSession().getAttribute(
						HHSR5Constants.DOC_SESSION_BEAN);
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
			// R5 end
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRes.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION,
					aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION));
			aoRes.setRenderParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION,
					aoRequest.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION));
			aoRes.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.CREATE_TREE);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in action file upload screen in Document Vault", aoEx);
			
				String lsAjaxCall = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.IS_AJAX_CALL);
				String lsErrorMsg = aoEx.toString();
				lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(":") + 1, lsErrorMsg.length()).trim();
				LOG_OBJECT.Error("Application Exception in Document Vault", aoEx);
				// Setting the required attribute.
				if (null != lsAjaxCall && lsAjaxCall.equalsIgnoreCase(HHSR5Constants.TRUE))
				{
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
							ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
							PortletSession.APPLICATION_SCOPE);
					aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
					try
					{
						aoRes.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
					}
					catch (IOException e)
					{
						LOG_OBJECT.Error("Exception in action file upload screen in Document Vault", aoEx);
					}
				}
			
		}
	}

	/**
	 * The method will create tree structure for upload step 3.
	 * 
	 * @param aoReq
	 * @param aoRes
	 * @return formpath as document location
	 */
	@RenderMapping(params = "render_action=treeCreation")
	protected String documentuploadForBA(RenderRequest aoReq, RenderResponse aoRes)
	{
		String lsFormPath = null;

		lsFormPath = doShowFileActionRender(aoReq);
		// aoRequest.setAttribute(HHSR5Constants.JS_TREE_BEAN_LIST,
		// aoRequest.getPortletSession().getAttribute(HHSR5Constants.JS_TREE_BEAN_LIST));
		aoReq.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SECTION,
				aoReq.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SECTION));
		aoReq.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION,
				aoReq.getParameter(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION));
		lsFormPath = HHSR5Constants.DOC_LOC_BAAP;
		return lsFormPath;
	}
	/**
	 * This method will handle resource request since it implements ResourceAwareController
	 * 
	 * @param ResourceRequest arg0
	 * @param ResourceResponse arg1
	 * @throws Exception
	 * @return null
	 */
  @Override
	public ModelAndView handleResourceRequest(ResourceRequest arg0, ResourceResponse arg1) throws Exception
	{
		return null;
	}

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
		// Adding P8Session for Defect # 8150
		P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String loJson = null;
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
				// Adding extra two parameters for Defect # 8150
				loJson = FileNetOperationsUtils.getOtherOrgFolderStructure(loList, lsUserOrgType,loUserSession,lsUserOrg);
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
	 * Added for fixing Multi-Tab Browsing QC6674
	 * This method simply fetches orgId for given BusinessAppId
	 * @param businessAppId
	 * @return orgId
	 */
	private String getOrgFromBusinessApp(String businessAppId) throws ApplicationException{
		Channel channel = new Channel();
		channel.setData(HHSR5Constants.BUSINESS_APP_ID, businessAppId);
		
		try
		{
			HHSTransactionManager.executeTransaction(channel, HHSConstants.GET_ORG_FROM_BUSINESS_APP);
			String orgId = (String) channel.getData(ApplicationConstants.ORGID);
			return orgId;
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in getOrgFromBusinessApp method", aoExp);
			throw aoExp;
		}

		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in getOrgFromBusinessApp method", aoEx);
			throw new ApplicationException("Exception occured in getOrgFromBusinessApp transaction", aoEx);
		}
		
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
		
	}

	//**End SAML R 7.8.0 QC 9165: invoke Web Service to Authenticate User for Provider only
	
}