package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;

@Controller(value = "multiAccountController")
@RequestMapping("view")
public class MultiAccountController extends BaseController
{
	/**
	 * This the log object which is used to log any error into log file when any
	 * exception occurred
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(MultiAccountController.class);

	/**
	 * Default Render Action Created for R4
	 * @param aoRequest Render Request
	 * @param aoResponse Render Response
	 * @return Model and View
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		return getSelectOrganizationRenderRequest(aoRequest, aoResponse);
	}

	/**
	 * Default Resource Action Created for R4
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * @throws ApplicationException return ModelAndView
	 */
	@Override
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelandView = new ModelAndView(generateSelectOrganizationScreen(aoRequest, aoResponse));
		return loModelandView;
	}

	/**
	 * Select Organization Render Action
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return Model And View Created for R4
	 */
	@RenderMapping(params = "render_action=" + ApplicationConstants.RENDER_ACTION_SELECT_ORGANIZATION)
	protected ModelAndView getSelectOrganizationRenderRequest(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		setExceptionMessageInResponse(aoRequest);
		ModelAndView loModelandView = new ModelAndView(generateSelectOrganizationScreen(aoRequest, aoResponse));
		return loModelandView;
	}

	/**
	 * This method generates Select Organization Screen for User with access to
	 * multiple accounts.
	 * <ul>
	 * <li>This method sets aoRequest</li>
	 * <li>If loStaffDetailsBeanList is not null then iterate the BeanList and
	 * set the map accordingly</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 */
	@SuppressWarnings("unchecked")
	protected String generateSelectOrganizationScreen(PortletRequest aoRequest, PortletResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsLoginPagePath = ApplicationConstants.SELECT_ORGANIZATION;
		Map<String, String> loOrganizationMap = new HashMap<String, String>();
		try
		{
			String lsOverlayLaunch = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.LAUNCH_OVERLAY);
			if (null != lsOverlayLaunch && ApplicationConstants.TRUE.equalsIgnoreCase(lsOverlayLaunch))
			{
				aoRequest.setAttribute(ApplicationConstants.LAUNCH_OVERLAY, lsOverlayLaunch);
			}
			aoRequest.setAttribute(ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, loSession.getAttribute(
					ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, PortletSession.APPLICATION_SCOPE));
			List<StaffDetails> loStaffDetailsBeanList = (List<StaffDetails>) loSession.getAttribute(
					ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, PortletSession.APPLICATION_SCOPE);
			if (null != loStaffDetailsBeanList)
			{
				Iterator<StaffDetails> loStaffDetailsListItr = loStaffDetailsBeanList.iterator();
				while (loStaffDetailsListItr.hasNext())
				{
					StaffDetails loTempObj = loStaffDetailsListItr.next();
					loOrganizationMap.put(loTempObj.getMsOrgId(), loTempObj.getMsOrganisationName());
				}
				aoRequest.setAttribute(ApplicationConstants.ORG_DETAILS_MAP, loOrganizationMap);
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_SELECT_ORGANIZATION_LOAD_FAIL + loEx);
			setGenericErrorMessage(aoRequest);
		}
		return lsLoginPagePath;
	}

	/**
	 * This is submit action for Multi-Account Login Created for R4
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@ActionMapping(params = "submit_action=multiAccount")
	/**
	 * This method selects Organization Login
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	protected void selectOrganizationLoginAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		selectSwitchOrganizationLoginAction(aoRequest, aoResponse);

	}

	/**
	 * This method select Switch Organization Login
	 * <ul>
	 * <li>If lsUserDn is not null and length is greater than 0 then set the
	 * StaffDetails list</li>
	 * <li>If the 'if' condition is satisfied then iterate the StaffDetails List
	 * </li>
	 * <li>The control is redirected to different page</li>
	 * </ul>
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response return String
	 */
	@SuppressWarnings("unchecked")
	protected String selectSwitchOrganizationLoginAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsOrgId = ApplicationConstants.EMPTY_STRING;
		String lsOrgName = ApplicationConstants.EMPTY_STRING;
		String lsProviderHomePagePath = ApplicationConstants.EMPTY_STRING;
		String lsActionStatus = ApplicationConstants.SUCCESS;
		String lsUserDn = null;
		List<StaffDetails> loStaffDetailsList = null;
		List<StaffDetails> loStaffDetailsListLogin = null;
		UserBean loUserBean = (UserBean) loSession.getAttribute(ApplicationConstants.GET_USER_ROLES,
				PortletSession.APPLICATION_SCOPE);
		try
		{
			lsOrgId = aoRequest.getParameter(ApplicationConstants.ORGANIZATIONIDKEY);
			lsOrgName = aoRequest.getParameter(ApplicationConstants.TYPEAHEADBOX);
			if (null != lsOrgId && null != lsOrgName)
			{
				lsUserDn = (String) loSession.getAttribute(ApplicationConstants.USER_DN,
						PortletSession.APPLICATION_SCOPE);
				if (null != lsUserDn && !lsUserDn.isEmpty())
				{
					loStaffDetailsListLogin = getProviderDetailsForSelectOrganization(lsUserDn);
				}
				else
				{
					lsActionStatus = HHSConstants.AS_FAILURE;
				}

				if (null != loStaffDetailsListLogin)
				{
					Iterator<StaffDetails> loStaffDetailsListItr = loStaffDetailsListLogin.iterator();
					while (loStaffDetailsListItr.hasNext())
					{
						StaffDetails loTempObj = loStaffDetailsListItr.next();
						lsActionStatus = HHSConstants.AS_FAILURE;
						if (lsOrgId.equalsIgnoreCase(loTempObj.getMsOrgId())
								&& lsOrgName.trim().equals(loTempObj.getMsOrganisationName().trim()))
						{
							lsActionStatus = ApplicationConstants.SUCCESS;
							break;
						}

					}
					if (null != lsActionStatus && ApplicationConstants.SUCCESS.equalsIgnoreCase(lsActionStatus))
					{
						loSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
						loSession.removeAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME);
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG, lsOrgId,
								PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME, lsOrgName,
								PortletSession.APPLICATION_SCOPE);
						loSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_ID, lsOrgId,
								PortletSession.APPLICATION_SCOPE);

						loStaffDetailsList = (List<StaffDetails>) loSession.getAttribute(
								ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM, PortletSession.APPLICATION_SCOPE);

						if (null != loStaffDetailsList)
						{
							loUserBean = setUserRolesForLogin(lsOrgId, loStaffDetailsList, loUserBean);
							createRoleMappingMap(loSession, loUserBean);
						}
						else
						{
							lsActionStatus = HHSConstants.AS_FAILURE;
						}
						String lsUrlHit = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.SESION_URL,
								PortletSession.APPLICATION_SCOPE);
						if (null != lsUrlHit)
						{
							redirectToUrl(aoRequest, aoResponse, lsUrlHit);
						}
						else
						{
							lsProviderHomePagePath = aoRequest.getScheme() + ApplicationConstants.NOTIFICATION_HREF_1
									+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
									+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
									+ ApplicationConstants.HOMEPAGE_REDIRECT_PATH;
							aoResponse.sendRedirect(lsProviderHomePagePath);
						}
					}
				}
			}
			else
			{
				lsActionStatus = HHSConstants.AS_FAILURE + ApplicationConstants.ERROR_MSG_UNABLE_TO_REDIRECT;
			}
		}
		catch (ApplicationException loExp)
		{
			setGenericErrorMessage(aoRequest);
		}
		catch (Exception loEx)
		{
			lsActionStatus = HHSConstants.AS_FAILURE;
			LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_UNABLE_TO_REDIRECT + loEx);
			setExceptionMessageFromAction((ActionResponse) aoResponse,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST, ApplicationConstants.MESSAGE_FAIL_TYPE, null,
					null);
			aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION,
					ApplicationConstants.RENDER_ACTION_SELECT_ORGANIZATION);
		}
		finally
		{
			if (null != lsActionStatus && lsActionStatus.startsWith(HHSConstants.AS_FAILURE))
			{
				LOG_OBJECT.Error(ApplicationConstants.ERROR_MSG_AUTHORIZATION1 + lsOrgName);
				setExceptionMessageFromAction((ActionResponse) aoResponse,
						ApplicationConstants.ERROR_MSG_AUTHORIZATION2, ApplicationConstants.MESSAGE_FAIL_TYPE, null,
						null);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION,
						ApplicationConstants.RENDER_ACTION_SELECT_ORGANIZATION);
			}
		}
		return lsActionStatus;
	}

	/**
	 * This method gets Provider details for Selected Organization based on the
	 * logged in User's USER DN. Created for R4 Transaction Invoked:
	 * "getNYCUserOrganizationDetailsMultiAccount"
	 * @param asUserDn User DN of Logged In User
	 * @return List of Staff Details
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	/**
	 * @param asUserDn User Dn
	 * <ul>
	 * <li> Execute Transaction <b> getNYCUserOrganizationDetailsMultiAccount </b></li>
	 * <li> Set the Channel with the following details 1. Application Constants 2. Bean details </li>
	 * </ul>
	 * return List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private List<StaffDetails> getProviderDetailsForSelectOrganization(String asUserDn) throws ApplicationException
	{
		StaffDetails loStaffDetailsBean = new StaffDetails();
		loStaffDetailsBean.setMsUserDN(asUserDn);
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.CHANNEL_ELEMET_SET_STAFF_DETAILS_BEAN, loStaffDetailsBean);
		TransactionManager.executeTransaction(loChannel,
				ApplicationConstants.TRANSACTION_GET_ORG_DETAILS_FOR_LOGIN_AUTHORIZATION);
		List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loChannel
				.getData(ApplicationConstants.CHANNEL_ELEMET_GET_STAFF_DETAILS_BEAN);
		return loStaffDetailsList;
	}

	/**
	 * This method sets role mapping for user
	 * <ul>
	 * <li>Set the UserBean attribute 'MsOrgType' with the Application Constant
	 * value</li>
	 * <li>If lsPermissionLevel is not equal to null and lsAdminPermission is
	 * not null then set MsRole attribute of UserBean according to their level</li>
	 * </ul>
	 * @param asOrgId Organization ID
	 * @param aoStaffDetailsList Staff Details List return UserBean Referenced
	 *            for R4 - Original Method (R1)
	 */
	@SuppressWarnings("rawtypes")
	private UserBean setUserRolesForLogin(String asOrgId, List<StaffDetails> aoStaffDetailsList, UserBean aoUserBean)
	{
		Iterator loIterator = aoStaffDetailsList.iterator();
		while (loIterator.hasNext())
		{
			StaffDetails loTemStaff = (StaffDetails) loIterator.next();
			if (null != loTemStaff && loTemStaff.getMsOrgId().equalsIgnoreCase(asOrgId))
			{
				String lsPermissionLevel = loTemStaff.getMsPermissionLevel();
				String lsPermissionType = loTemStaff.getMsPermissionType();
				String lsAdminPermission = loTemStaff.getMsAdminPermission();
				aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
				if (null != lsPermissionLevel && null != lsAdminPermission)
				{
					aoUserBean.setMsPermissionLevel(lsPermissionLevel);
					if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsPermissionLevel)
							&& ApplicationConstants.SYSTEM_YES.equalsIgnoreCase(lsAdminPermission))
					{
						aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF);
					}
					else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsPermissionLevel))
					{
						aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
					}
					else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2.equalsIgnoreCase(lsPermissionLevel)
							&& ApplicationConstants.SYSTEM_YES.equalsIgnoreCase(lsAdminPermission))
					{
						aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
					}
					else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2.equalsIgnoreCase(lsPermissionLevel))
					{
						aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
					}
				}
				if (null != lsPermissionType && !lsPermissionType.isEmpty())
				{
					aoUserBean.setMsPermissionType(lsPermissionType);
				}
			}
		}
		return aoUserBean;
	}

	/**
	 * This method creates role mapping for user
	 * <ul>
	 * <li>This method sets Portlet Session object</li>
	 * </ul>
	 * @param loSession PortletSession to set variables in application scope
	 * @param aoUserBean UserBean with user details (user name, role, etc..)
	 * @throws ApplicationException If an Application Exception occurs
	 *             Referenced for R4 - Original Method (R1)
	 */
	@SuppressWarnings("rawtypes")
	private void createRoleMappingMap(PortletSession loSession, UserBean aoUserBean) throws ApplicationException
	{
		List loCompoRoleMappingList = (List) createRoleMappingMap(aoUserBean);
		Map loRoleComponentMap = CommonUtil.getComponentRoleMap(loCompoRoleMappingList);
		loSession.setAttribute(ApplicationConstants.ATTRIBUTE_SET_ROLE_MAPPING_MAP, loRoleComponentMap,
				PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, aoUserBean.getMsRole(),
				PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL,
				aoUserBean.getMsPermissionLevel(), PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, aoUserBean.getMsPermissionType(),
				PortletSession.APPLICATION_SCOPE);
		loSession.setAttribute(ApplicationConstants.GET_USER_ROLES, aoUserBean, PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method creates role mapping for user
	 * <ul>
	 * <li>Execute Transaction id <b> roleComponentMapping </b></li>
	 * <li>Fetch the desired details from List</li>
	 * </ul>
	 * @param aoUserBean UserBean with user details (user name, role, etc..)
	 * @return List component role mapping list with component id and role
	 * @throws ApplicationException If an Application Exception occurs
	 *             Referenced for R4 - Original Method (R1)
	 */
	@SuppressWarnings("rawtypes")
	private List createRoleMappingMap(UserBean aoUserBean) throws ApplicationException
	{
		List loCompoRoleMappingList = null;
		Channel loChannelObj = new Channel();
		loChannelObj.setData(ApplicationConstants.CHANNEL_ELEMET_SET_USER_BEAN, aoUserBean);
		TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.TRANSACTION_FETCH_ROLE_MAPPING);
		loCompoRoleMappingList = (List) loChannelObj
				.getData(ApplicationConstants.CHANNEL_ELEMET_GET_COMPONENT_ROLE_MAPPING_LIST);
		return loCompoRoleMappingList;

	}

	/**
	 * This method redirects the user to the specified url Created for R4
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param asUrlHit is the string representation of the url
	 * @throws ApplicationException
	 */
	private void redirectToUrl(ActionRequest aoRequest, ActionResponse aoResponse, String asUrlHit)
			throws ApplicationException
	{
		try
		{
			aoRequest.getPortletSession().removeAttribute(HHSConstants.SESION_URL, PortletSession.APPLICATION_SCOPE);
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoResponse.sendRedirect(asUrlHit);
			aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(asUrlHit));
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		catch (IOException loExp)
		{
			throw new ApplicationException(HHSConstants.NOTIFICATION_REDIRECT_FILTER_LOGGER_ERROR, loExp);
		}
	}
}
