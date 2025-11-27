package com.nyc.hhs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jdom.Document;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.accenture.formtaglib.DomStatus;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.controllers.actions.BusinessApplication;
import com.nyc.hhs.controllers.actions.BusinessApplicationFactory;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyUtil;

/**
 * HHS Accelerator Organization Controller allows a provider organization to
 * manage its general information, indicate the geography, languages and
 * population their organization serves, and also identify members/users within
 * their organization.
 * 
 */

public class OrganizationQuestionsController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(OrganizationQuestionsController.class);

	/**
	 * This method is handle all the rendering activities for the service
	 * application, also method sets the values in the RenderRequest reference,
	 * so that same values can be displayed on the required jsp.
	 * 
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
		try
		{
			PortletSession loPortletSessionThread = aoRequest.getPortletSession();
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			String lsOrgId = getOrgId(aoRequest, loPortletSession);
			String lsAction = getAction(aoRequest, loPortletSession);
			String lsModuleName = getModuleName(aoRequest, loPortletSession);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lsHeaderJSPName = PortalUtil.parseQueryString(aoRequest, "headerJSPName");
			String lsUserRoles = null;
			if (aoRequest.getParameter(ApplicationConstants.RENDER_ACTION) != null
					&& !aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).isEmpty()
					&& aoRequest.getParameter(ApplicationConstants.RENDER_ACTION).equals(ApplicationConstants.ERROR))
			{
				LOG_OBJECT.Debug("Internal Error occured in OrganizationQuestionsController Action");
				loModelAndView = new ModelAndView("errorpage");
				return loModelAndView;
			}
			Map<String, Object> loMapToRender = new HashMap<String, Object>();
			String lsBusnessAppId = getBusnessAppId(aoRequest, loPortletSession);
			String lsAppStatus = getAppStatus(aoRequest, loPortletSession);
			String lsAppId = getAppId(aoRequest, loPortletSession);
			// find the readonly status
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			Map<String, StatusBean> loBusinessStatusBeanSectionMap = setValuesInPortletSession(aoRequest, lsOrgId,
					loPortletSession, lsUserRole, lsAppStatus, lsBusnessAppId, lsAppId, loMapToRender);
			String lsFormPath = null;
			StatusBean lsStatusBean = null;
			String lsSectionStatus = ApplicationConstants.NOT_STARTED_STATE;
			if (loBusinessStatusBeanSectionMap != null && loBusinessStatusBeanSectionMap.get(lsSectionName) != null
					&& !loBusinessStatusBeanSectionMap.isEmpty())
			{
				lsStatusBean = (StatusBean) loBusinessStatusBeanSectionMap.get(lsSectionName);
				lsSectionStatus = lsStatusBean.getMsSectionStatusToDisplay();
			}
			// R5 code start
			String lsPermissionType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
			Boolean loReadOnly = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsAppStatus, null, null,
					"business", lsOrgnizationType, lsPermissionType);
			Boolean loReadOnlySection = BusinessApplicationUtil.doCheckReadOnly(lsBusnessAppId, lsSectionStatus, null,
					null, lsSectionName, lsOrgnizationType, lsPermissionType);
			// R5 code ends
			if (lsAction != null
					&& lsAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE_NEXT))
			{
				lsSubSectionName = PropertyUtil.getServiceName(lsSectionName, lsSubSectionName, "nextaction");
				if (lsSubSectionName.startsWith("menu__"))
				{
					String[] loNextActionArr = lsSubSectionName.split("menu__");
					lsSectionName = loNextActionArr[1];
					lsSubSectionName = PropertyUtil.getDefaultSubSection(lsSectionName);
					lsAction = ApplicationConstants.SHOW_QUESTION;
				}
				else
				{
					lsAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
				}
				loPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
			}
			else if (lsAction != null
					&& lsAction.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_ACTION_BACK))
			{
				lsSubSectionName = PropertyUtil.getServiceName(lsSectionName, lsSubSectionName, "previousaction");
				if (lsSubSectionName.contains("_"))
				{
					String[] loPreviouseActionArr = lsSubSectionName.split("_");
					if (loPreviouseActionArr.length > 1)
					{
						lsSectionName = loPreviouseActionArr[0];
						lsSubSectionName = loPreviouseActionArr[1];
					}
				}
				lsAction = ApplicationConstants.BUSINESS_APPLICATION_ACTION_OPEN;
				if (ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION.equalsIgnoreCase(lsSubSectionName))
				{
					lsAction = ApplicationConstants.SHOW_QUESTION;
				}
				loPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
			}
			aoRequest.setAttribute("section", lsSectionName);
			aoRequest.setAttribute("subsection", lsSubSectionName);
			LOG_OBJECT.Debug("21 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			BusinessApplication loBusinessApp = BusinessApplicationFactory.getBusinessApplication(lsSectionName,
					lsSubSectionName);
			if (null != lsSubSectionName
					&& lsSubSectionName.endsWith(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
			{
				lsFormPath = setValuesForQuestion(aoRequest, lsOrgId, loPortletSession, lsAction, lsSectionName,
						lsSubSectionName, lsHeaderJSPName, lsAppStatus, lsUserRoles, lsBusnessAppId, lsFormPath,
						loBusinessApp);
				loMapToRender = loBusinessApp.getMapForRender(lsAction, lsSectionName, null, aoRequest);
			}
			String lsJSPFileName = setJSPFileName(aoRequest, lsOrgId, loPortletSession, lsModuleName, lsHeaderJSPName,
					lsAppStatus, loMapToRender, lsFormPath, loReadOnly, loReadOnlySection);
			loPortletSession.setAttribute("cityUserSearchProviderId", lsOrgId, PortletSession.APPLICATION_SCOPE);
			loModelAndView = new ModelAndView(lsJSPFileName, loMapToRender);

			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in OrganizationQuestionsController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in OrganizationQuestionsController ", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method fetch the application id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
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
	 * This method fetch the application status.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
	 */
	private String getAppStatus(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAppStatus = PortalUtil.parseQueryString(aoRequest, "bussAppStatus");
		// get value from session if null
		if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
		{
			lsAppStatus = (String) aoPortletSession.getAttribute("bussAppStatus");
		}
		return lsAppStatus;
	}

	/**
	 * This method fetch the business application id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
	 */
	private String getBusnessAppId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
		// get value from session if null
		if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase("") || lsBusnessAppId.equalsIgnoreCase("null"))
		{
			lsBusnessAppId = (String) aoPortletSession.getAttribute("business_app_id");
		}
		aoRequest.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
		return lsBusnessAppId;
	}

	/**
	 * This method fetch the organization id.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
	 */
	private String getOrgId(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsOrgId = PortalUtil.parseQueryString(aoRequest, "cityUserSearchProviderId");
		if (lsOrgId == null)
		{
			lsOrgId = (String) aoRequest.getPortletSession().getAttribute("cityUserSearchProviderId",
					PortletSession.APPLICATION_SCOPE);
		}
		if (lsOrgId == null)
		{
			lsOrgId = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
		}
		return lsOrgId;
	}

	/**
	 * This method fetch the module name.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
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
	 * This method fetch the action.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @return
	 */
	private String getAction(RenderRequest aoRequest, PortletSession aoPortletSession)
	{
		String lsAction = aoRequest.getParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		aoRequest.removeAttribute(ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		if (lsAction == null)
		{
			aoPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
			aoPortletSession.removeAttribute("corpStr", PortletSession.APPLICATION_SCOPE);
			lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		}
		return lsAction;
	}

	/**
	 * This method sets the values for question section.
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - current organization id
	 * @param aoPortletSession
	 * @param asAction
	 * @param asSectionName
	 * @param asSubSectionName
	 * @param asHeaderJSPName
	 * @param asAppStatus
	 * @param asUserRoles
	 * @param asBusnessAppId
	 * @param asFormPath
	 * @param aoBusinessApp
	 * @return
	 * @throws ApplicationException
	 */
	private String setValuesForQuestion(RenderRequest aoRequest, String asOrgId, PortletSession aoPortletSession,
			String asAction, String asSectionName, String asSubSectionName, String asHeaderJSPName, String asAppStatus,
			String asUserRoles, String asBusnessAppId, String asFormPath, BusinessApplication aoBusinessApp)
			throws ApplicationException
	{
		// check if document object exist to populate form
		if (null == asBusnessAppId && !"basics".equalsIgnoreCase(asSectionName))
		{// first
			// time
			asFormPath = PropertyUtil.getLiveFormLocation(asSectionName);
		}
		else if ((null != asBusnessAppId || "basics".equalsIgnoreCase(asSectionName))
				&& (asAction.equals(ApplicationConstants.SHOW_QUESTION) || null == aoPortletSession.getAttribute(
						ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE)))
		{
			// get data from db
			aoPortletSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT, PortletSession.APPLICATION_SCOPE);
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.OPEN_TRANSACTION);
			Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus,
					null, asAction, asUserRoles, aoRequest, null);
			// Execute the transaction name obtained from navigation xml
			// when questions is rendered for organization profile.
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			if (null != loChannel.getData("loFormInformation"))
			{
				asFormPath = setFormInformation(aoRequest, aoPortletSession, asSectionName, asHeaderJSPName,
						asAppStatus, asFormPath, loChannel);
			}
			else
			{
				asFormPath = PropertyUtil.getLiveFormLocation(asSectionName);
				aoPortletSession.setAttribute("user_roles", "edit", PortletSession.APPLICATION_SCOPE);// for
																										// edit
			}
		}
		else if (null != aoPortletSession.getAttribute(ApplicationConstants.DOM_FOR_EDIT,
				PortletSession.APPLICATION_SCOPE))
		{
			String lsFormName = aoRequest.getParameter(ApplicationConstants.FORMNAME);
			String lsFormVersion = aoRequest.getParameter(ApplicationConstants.FORM_VERSION);
			asFormPath = PropertyUtil.getFilePathToRender(lsFormName, lsFormVersion);
			aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE);// for
																										// semi
																										// edit
		}
		return asFormPath;
	}

	/**
	 * This method sets the name of the jsp file
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization id
	 * @param aoPortletSession - protlet session
	 * @param asModuleName - module name
	 * @param asHeaderJSPName - header jsp name
	 * @param asAppStatus - application status
	 * @param aoMapToRender - map containing info to be displayed on jsp
	 * @param asFormPath - form path
	 * @param aoReadOnly - read only flag
	 * @param aoReadOnlySection - read only section
	 * @return - lsJSPFileName
	 */
	private String setJSPFileName(RenderRequest aoRequest, String asOrgId, PortletSession aoPortletSession,
			String asModuleName, String asHeaderJSPName, String asAppStatus, Map<String, Object> aoMapToRender,
			String asFormPath, Boolean aoReadOnly, Boolean aoReadOnlySection)
	{
		String lsJSPFileName = "home";
		if (asModuleName != null && ApplicationConstants.MODULE_ORGANIZATION_INFORMATION.equals(asModuleName))
		{
			lsJSPFileName = "home_provider";
			aoPortletSession.setAttribute("user_roles", "admin", PortletSession.APPLICATION_SCOPE);// for
																									// admin
		}
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE, asFormPath);
		aoRequest.setAttribute("loReadOnly", aoReadOnly);
		aoMapToRender.put("loReadOnly", aoReadOnly);
		aoMapToRender.put("loReadOnlySection", aoReadOnlySection);

		if (asAppStatus != null && !asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT))
		{
			lsJSPFileName = "postSubmitionApplicationHeader";
		}
		if (asHeaderJSPName != null && !asHeaderJSPName.isEmpty())
		{
			lsJSPFileName = asHeaderJSPName;
			aoRequest.setAttribute("isForPrint", "true");
			aoMapToRender.put("ownerProviderId", asOrgId);
		}
		return lsJSPFileName;
	}

	/**
	 * This method sets required values in portlet session
	 * 
	 * @param aoRequest - RenderRequest
	 * @param asOrgId - organization id
	 * @param aoPortletSession - protlet session
	 * @param asUserRole - user role
	 * @param asAppStatus - application status
	 * @param asBusnessAppId - business application id
	 * @param asAppId - application id
	 * @param aoMapToRender - map containing info to be displayed on jsp
	 * @return - loBusinessStatusBeanSectionMap
	 * @throws ApplicationException
	 */
	private Map<String, StatusBean> setValuesInPortletSession(RenderRequest aoRequest, String asOrgId,
			PortletSession aoPortletSession, String asUserRole, String asAppStatus, String asBusnessAppId,
			String asAppId, Map<String, Object> aoMapToRender) throws ApplicationException
	{
		String lbWithdrawalVisibleFlag = "Invisible";
		if (asUserRole.contains("manager")
				&& asAppStatus != null
				&& !(asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)
						|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
						|| asAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
						|| asAppStatus.equalsIgnoreCase("withdraw requested") || asAppStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)))
		{
			lbWithdrawalVisibleFlag = "visible";
		}
		// set the value in session
		aoPortletSession.setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, asBusnessAppId);
		aoPortletSession.setAttribute("bussAppStatus", asAppStatus);
		aoPortletSession.setAttribute("applicationId", asAppId);
		// set the read only status in session to validate the link on page.jsp
		aoPortletSession.setAttribute("loReadOnlyStatus", asAppStatus, PortletSession.APPLICATION_SCOPE);
		// set the read only status in session to validate the link on page.jsp
		aoPortletSession.setAttribute("loReadOnlyStatus", asAppStatus, PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute("bussAppStatus", asAppStatus);
		if (asBusnessAppId != null && !asBusnessAppId.equalsIgnoreCase("null"))
		{
			setApplicationStatusToBeDisplayed(asOrgId, asBusnessAppId, aoMapToRender);
		}
		aoMapToRender.put("lbWithdrawalVisibleFlag", lbWithdrawalVisibleFlag);
		Map<String, StatusBean> loBusinessStatusBeanSectionMap = (Map<String, StatusBean>) aoMapToRender
				.get("loBusinessStatusBeanMap");
		return loBusinessStatusBeanSectionMap;
	}

	/**
	 * This method sets required Form information
	 * 
	 * @param aoRequest - RenderRequest
	 * @param aoPortletSession - PortletSession
	 * @param asSectionName - name of the section
	 * @param asHeaderJSPName - header jsp name
	 * @param asAppStatus - application status
	 * @param asFormPath - form path
	 * @param aoChannel - Channel
	 * @return - asFormPath
	 * @throws ApplicationException
	 */
	private String setFormInformation(RenderRequest aoRequest, PortletSession aoPortletSession, String asSectionName,
			String asHeaderJSPName, String asAppStatus, String asFormPath, Channel aoChannel)
			throws ApplicationException
	{
		PortletContext loContext = aoPortletSession.getPortletContext();
		// Assuming transaction will return map with value
		HashMap<String, Object> loHMAnswerMap = (HashMap<String, Object>) aoChannel.getData("loFormInformation");
		if (asSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS)
				&& loHMAnswerMap != null && loHMAnswerMap.containsKey("basic_cs_value"))
		{
			aoPortletSession.setAttribute("corpStr", loHMAnswerMap.get("basic_cs_value"),
					PortletSession.APPLICATION_SCOPE);
		}
		if (null != aoChannel.getData("taskHistoryList"))
		{
			ArrayList loTaskHistoryList = new ArrayList();
			loTaskHistoryList = (ArrayList) aoChannel.getData("taskHistoryList");
			aoPortletSession.setAttribute("aoTaskHistoryList", loTaskHistoryList);
		}
		if (null != loHMAnswerMap && loHMAnswerMap.containsKey("FORM_NAME"))
		{
			if (asHeaderJSPName != null && !asHeaderJSPName.isEmpty())
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
				StringBuffer loSbHTMLForPrintView = BusinessApplicationUtil.getFormHTML(loHMAnswerMap,
						lsPathToReadQuestions, false);
				aoRequest.setAttribute("printView", loSbHTMLForPrintView);
			}
			else
			{
				String lsFormName = (String) loHMAnswerMap.get("FORM_NAME");
				String lsFormVersion = (String) loHMAnswerMap.get("FORM_VERSION");
				asFormPath = PropertyUtil.getFilePathToRender(lsFormName, lsFormVersion);
				String lsFormTemplatePath = loContext.getRealPath(PropertyUtil.getDeployedFormLocation(lsFormName,
						lsFormVersion, false));
				Document loAnswerDom = PropertyUtil.converRequestMapToDom(lsFormTemplatePath, loHMAnswerMap);
				aoPortletSession.setAttribute(ApplicationConstants.DOM_FOR_EDIT, loAnswerDom,
						PortletSession.APPLICATION_SCOPE);
				if (asAppStatus != null
						&& asAppStatus.contains(ApplicationConstants.STATUS_DRAFT))
				{// for
					// semi
					// edit
					aoPortletSession.setAttribute("user_roles", "semi_edit", PortletSession.APPLICATION_SCOPE);
				}
				else
				{// for semi edit
					aoPortletSession.setAttribute("user_roles", "read_only", PortletSession.APPLICATION_SCOPE);
				}
			}

		}
		else
		{
			if (asHeaderJSPName != null && !asHeaderJSPName.isEmpty())
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
				loHMAnswerMap = PropertyUtil.getFormNameVersionMap(asSectionName);
				StringBuffer loSbHTMLForPrintView = BusinessApplicationUtil.getFormHTML(loHMAnswerMap,
						lsPathToReadQuestions, true);
				aoRequest.setAttribute("printView", loSbHTMLForPrintView);
			}
			else
			{
				asFormPath = PropertyUtil.getLiveFormLocation(asSectionName);
				aoPortletSession.setAttribute("user_roles", "edit", PortletSession.APPLICATION_SCOPE);// for
																										// edit
			}
		}
		return asFormPath;
	}

	/**
	 * This method sets the application status to be displayed
	 * 
	 * @param asOrgId - organization id
	 * @param asBusnessAppId - business application id
	 * @param aoMapToRender - map containing info to be displayed on jsp
	 * @throws ApplicationException
	 */
	private void setApplicationStatusToBeDisplayed(String asOrgId, String asBusnessAppId,
			Map<String, Object> aoMapToRender) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APP_ID, asBusnessAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		// Execute the transaction get complete status for business and
		// services
		TransactionManager.executeTransaction(loChannel, "getCompleteStatusMap");
		Map<String, StatusBean> loBusinessStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loBusinessStatusBeanMap");
		Map<String, StatusBean> loServicesStatusBeanMap = (Map<String, StatusBean>) loChannel
				.getData("loServiceStatusBeanMap");
		Boolean lbApplicationStatus = ((Map<String, Boolean>) loChannel.getData("applicationStatus"))
				.get("completeStatus");
		aoMapToRender.put("loBusinessStatusBeanMap", loBusinessStatusBeanMap);
		aoMapToRender.put("loServicesStatusBeanMap", loServicesStatusBeanMap);
		aoMapToRender.put("applicationStatus", lbApplicationStatus);
	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction.
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
			String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserIdThreadLocal);
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			String lsSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
			String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
					ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);

			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsAppStatus = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_APP_STATUS, PortletSession.APPLICATION_SCOPE);
			String lsUserRoles = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsBusnessAppId = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.KEY_BUSINESS_APP_ID);
			String lsAppId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_APP_ID, PortletSession.APPLICATION_SCOPE);
			boolean lbIsDomWithError = false;

			// get value from session if null
			if (lsBusnessAppId == null || lsBusnessAppId.equalsIgnoreCase("")
					|| lsBusnessAppId.equalsIgnoreCase("null"))
			{
				lsBusnessAppId = (String) aoRequest.getPortletSession().getAttribute("business_app_id");
			}
			// get value from session if null
			if (lsAppStatus == null || lsAppStatus.equalsIgnoreCase(""))
			{
				lsAppStatus = (String) aoRequest.getPortletSession().getAttribute("bussAppStatus");
			}

			// get value from session if null
			lsAppId = (String) aoRequest.getPortletSession().getAttribute("applicationId");

			// set the value in session
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			aoRequest.getPortletSession().setAttribute("bussAppStatus", lsAppStatus);
			aoRequest.getPortletSession().setAttribute("applicationId", lsAppId);
			LOG_OBJECT.Debug("20 before get business application section::" + lsSectionName + " Subsection::"
					+ lsSubSectionName + " action::" + lsAction + " url::" + aoRequest.getParameterMap());
			BusinessApplication loBusinessApp = BusinessApplicationFactory.getBusinessApplication(lsSectionName,
					lsSubSectionName);

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
				lbIsDomWithError = insertApplicationInformation(aoRequest, aoResponse, lsAction, lsSectionName,
						lsSubSectionName, lsOrgId, lsAppStatus, lsUserRoles, lsUserId, lsBusnessAppId,
						lbIsDomWithError, loBusinessApp);
			}
			if (lsAction != null && !lsAction.equals("removeDocFromApplication")
					&& !lsAction.equalsIgnoreCase("fileupload") && !lsAction.equalsIgnoreCase("submitDocId")
					&& !(lsAction.equals("displayDocument") || lsAction.equals("applicationSubmit")))
			{
				if (lbIsDomWithError)
				{
					lsAction = "save";
				}
				aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION, lsAction);
				aoResponse.setRenderParameter(ApplicationConstants.KEY_BUSINESS_APP_ID, lsBusnessAppId);
			}

			long loEndTimeTime = System.currentTimeMillis();
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in OrganizationQuestionsController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in OrganizationQuestionsController ", aoEx);
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
	 * This method insert the application information in tha database.
	 * 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @param asAction - action to be performed
	 * @param asSectionName - section name
	 * @param asSubSectionName - sub section name
	 * @param asOrgId - organization id
	 * @param asAppStatus - application status
	 * @param asUserRoles - user role
	 * @param asUserId - user id
	 * @param asBusnessAppId - business application id
	 * @param abIsDomWithError - boolean flag
	 * @param aoBusinessApp - BusinessApplication
	 * @return - abIsDomWithError
	 * @throws ApplicationException
	 */
	private boolean insertApplicationInformation(ActionRequest aoRequest, ActionResponse aoResponse, String asAction,
			String asSectionName, String asSubSectionName, String asOrgId, String asAppStatus, String asUserRoles,
			String asUserId, String asBusnessAppId, boolean abIsDomWithError, BusinessApplication aoBusinessApp)
			throws ApplicationException
	{
		Channel loChannel = aoBusinessApp.getChannelObject(asSectionName, asOrgId, asBusnessAppId, asAppStatus, null,
				asAction, asUserRoles, aoRequest, null);
		loChannel.setData("asUserId", asUserId);
		try
		{
			String lsTransactionName = PropertyUtil.getServiceName(asSectionName, asSubSectionName,
					ApplicationConstants.SAVE_TRANSACTION);
			TransactionManager.executeTransaction(loChannel, lsTransactionName);

			aoResponse.setRenderParameter(ApplicationConstants.FORM_VERSION,
					(String) loChannel.getData(ApplicationConstants.FORM_VERSION));
			aoResponse.setRenderParameter(ApplicationConstants.FORMNAME,
					(String) loChannel.getData(ApplicationConstants.FORMNAME));
			if (null != loChannel.getData(ApplicationConstants.DOM_RETURNED))
			{

				DomStatus loDomStatus = (DomStatus) loChannel.getData(ApplicationConstants.DOM_RETURNED);
				aoRequest.getPortletSession(true).setAttribute(ApplicationConstants.DOM_FOR_EDIT,
						loDomStatus.getDomObj(), PortletSession.APPLICATION_SCOPE);

				if (loDomStatus.isDomWithError())
				{
					abIsDomWithError = true;
					aoResponse.setRenderParameter(ApplicationConstants.BUSINESS_APPLICATION_ACTION,
							ApplicationConstants.BUSINESS_APPLICATION_ACTION_SAVE);
				}
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
}
