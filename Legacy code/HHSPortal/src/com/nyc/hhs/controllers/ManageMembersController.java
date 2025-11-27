package com.nyc.hhs.controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller is used to control the behavior of the organization profile
 * add staff , edit staff ,edit user and listing of the members and user
 * 
 */

public class ManageMembersController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ManageMembersController.class);

	/**
	 * Updated in 3.1.0. Added check for Defect 6346 This is render request to
	 * display the page on the front end
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{

		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		// portlet session
		PortletSession loPortletSession = aoRequest.getPortletSession();
		// action which flow needs to execute
		final String lsAction = PortalUtil
				.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
		// orgnization id from the session
		String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute("userEmail", lsUserId);
		// Updated in 3.1.0. Added check for Defect 6346
		String lsIsHeaderTab = aoRequest.getParameter("isHeaderTab");
		String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
				"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
		if (lsCityUserSearchProviderId != null)
		{
			if (lsCityUserSearchProviderId.contains(ApplicationConstants.TILD))
			{
				lsOrgId = lsCityUserSearchProviderId.substring(0,
						lsCityUserSearchProviderId.indexOf(ApplicationConstants.TILD));
				lsCityUserSearchProviderId = lsOrgId;
			}
			else
			{
				lsOrgId = lsCityUserSearchProviderId;
			}

			// Updated in 3.1.0. Added check for Defect 6346
			if (lsIsHeaderTab != null)
			{
				lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE);
				lsCityUserSearchProviderId = (String) loPortletSession.getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			}
			// Defect 6346 check ends
		}
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		// request variable from the jsp
		final String lsShowExistingMember = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.LS_SHOW_EXISTING_MEMBER);
		String lsJspName = "home_provider";
		// section from request
		final String lsSectionName = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.BUZ_APP_PARAMETER_SECTION);
		// sub section from request
		final String lsSubSectionName = PortalUtil.parseQueryString(aoRequest,
				ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION);
		aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SUB_SECTION, lsSubSectionName);
		aoRequest.setAttribute(ApplicationConstants.BUZ_APP_PARAMETER_SECTION, lsSectionName);
		try
		{
			// this condition execute for the listing of users and staffs
			if (null != lsAction && lsAction.equalsIgnoreCase(ApplicationConstants.DISPLAY_ORG_MEMBER))
			{
				lsJspName = doShowMembers(aoRequest, lsOrgId, loMapForRender, lsShowExistingMember, lsJspName);
			}
			// this condition execute when user save and edit the staff details
			// and return back to the display list page
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.SAVE_AND_DISPLAY_STAFF))
			{
				loMapForRender.put(ApplicationConstants.LO_ORG_MEMBER_LIST,
						ApplicationSession.getAttribute(aoRequest, ApplicationConstants.DISPLAY_LIST_AFTER_SAVE));
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/WEB-INF/jsp/businessapplication/displayOrgMembers.jsp");
			}
			// this condition execute when user wants to add new member in the
			// system
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.SAVE_ORG_MEMBER))
			{
				aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
						"/WEB-INF/jsp/businessapplication/addOrgMember.jsp");
				String lsErrorMsg = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MSG2);
				// this is to display the error message when user tries to add
				// the same CEO title again in case of save
				if (lsErrorMsg != null && lsErrorMsg.equalsIgnoreCase(ApplicationConstants.SHOW_ERROR_MSG))
				{
					loMapForRender.put(ApplicationConstants.ERROR_MSG2, ApplicationConstants.ERROR_MSG);
					loMapForRender.put(ApplicationConstants.LO_STAFF_DETAILS,
							ApplicationSession.getAttribute(aoRequest, ApplicationConstants.LO_STAFF_DETAILS));
				}
			}
			// this condition execute when user try to edit the organization
			// member
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.EDIT_ORG_MEMBER))
			{
				doShowEditMember(aoRequest, loMapForRender, lsOrgId);
			}
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.EDIT_USER_REQUEST))
			{
				doShowEditRequest(aoRequest, loMapForRender, lsOrgId);
			}

			// Release 5 read only check
			String lsPermissionType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

			if (lsPermissionType != null && (lsPermissionType.equalsIgnoreCase("R"))
					&& lsOrgnizationType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
			{
				aoRequest.setAttribute("ReadOnlyUser", true);
			}

			// this is for the member title combo box
			loMapForRender.put("memberTitle", getMemberInfoTitle());
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("exception in handleRenderRequestInternal", aoExp);
		}
		if (lsCityUserSearchProviderId != null && lsIsHeaderTab == null)
		{
			lsJspName = "shareDocheader";
			aoRequest.setAttribute("section", "basics");
		}
		ModelAndView loModelAndView = new ModelAndView(lsJspName, loMapForRender);

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in ManageMembersController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in ManageMembersController ", aoEx);
		}
		UserThreadLocal.unSet();
		return loModelAndView;
	}

	/**
	 * This method is used to show the organization members
	 * 
	 * @param aoRequest request object
	 * @param asOrgId organization id
	 * @param aoMapForRender map for render
	 * @param asShowExistingMember for the existing member
	 * @param asJspName jsp needs to display
	 * @return String
	 * @throws ApplicationException application exception
	 */
	private String doShowMembers(RenderRequest aoRequest, final String asOrgId, Map<String, Object> aoMapForRender,
			final String asShowExistingMember, String asJspName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("orgId", asOrgId);
		// Execute the transaction to get the organization member list for grid
		TransactionManager.executeTransaction(loChannel, ApplicationConstants.GET_ORG_MEMBER_LIST_FOR_GRID);
		List<StaffDetails> loOrgMemberList = (List<StaffDetails>) loChannel
				.getData(ApplicationConstants.ALL_ORG_MEMBER_LIST_FOR_GRID);
		aoMapForRender.put(ApplicationConstants.LO_ORG_MEMBER_LIST, loOrgMemberList);
		// this variable is used to open the pop up for existing members
		if (asShowExistingMember != null && asShowExistingMember.equalsIgnoreCase("true"))
		{
			asJspName = ApplicationConstants.EXISTING_MEMBER;
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/existingMember.jsp");
		}
		else
		{
			aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
					"/WEB-INF/jsp/businessapplication/displayOrgMembers.jsp");
		}
		return asJspName;
	}

	/**
	 * This method is used to show the edit members <li>This method was updated
	 * in R4</li>
	 * @param aoRequest request object
	 * @param aoMapForRender map for render
	 * @throws ApplicationException
	 */
	private void doShowEditMember(RenderRequest aoRequest, Map<String, Object> aoMapForRender, final String asOrgId)
			throws ApplicationException
	{
		String lsErrorMsg = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MSG2);

		if (lsErrorMsg != null && lsErrorMsg.equalsIgnoreCase(HHSR5Constants.SHOW_ERROR_MESSAGE_CONTRACT_RESTRICTION))
		{
			aoRequest
					.getPortletSession()
					.setAttribute(
							HHSConstants.ERROR_MESSAGE,
							"This user is the sole user with access to a contract."
									+ " Please replace this user for the applicable contract(s) before changing this user’s role.",
							PortletSession.APPLICATION_SCOPE);
		}
		// this is to display the error message when user tries to add the same
		// CEO title again in case of edit
		if (lsErrorMsg != null && lsErrorMsg.equalsIgnoreCase(ApplicationConstants.SHOW_ERROR_MSG))
		{
			aoMapForRender.put(ApplicationConstants.ERROR_MSG2, ApplicationConstants.ERROR_MSG);
			aoMapForRender.put(ApplicationConstants.LO_STAFF_DETAILS,
					ApplicationSession.getAttribute(aoRequest, ApplicationConstants.LO_STAFF_DETAILS));
			aoMapForRender.put(HHSR5Constants.ADMIN_COUNT,
					PortalUtil.parseQueryString(aoRequest, HHSR5Constants.ADMIN_COUNT));
			aoMapForRender.put(HHSR5Constants.MEMBER_AS_USER,
					ApplicationSession.getAttribute(aoRequest, HHSR5Constants.MEMBER_AS_USER));
			aoMapForRender.put(ApplicationConstants.DEACTIVATE_USER,
					ApplicationSession.getAttribute(aoRequest, ApplicationConstants.DEACTIVATE_USER));
		}
		else
		{
			Map<String, String> loParamMap = new LinkedHashMap<String, String>();
			// R4: Adding Map as parameter to provide additional data for
			// fetching data adding join to STAFF_ORGANIZATION_MAPPING table
			loParamMap.put("asOrgId", asOrgId);
			loParamMap.put("asAdminPermission", "Yes");
			loParamMap.put("asActiveFlag", "Yes");
			loParamMap.put("asUserStatus", "Yes");
			loParamMap.put("orgId", asOrgId);
			loParamMap.put(ApplicationConstants.MS_STAFF_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.MS_STAFF_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			loChannel.setData("loParamMap", loParamMap);

			String lsTransactionName = ApplicationConstants.GET_ORG_MEMBER_BY_ID;
			// Execute the transaction to get the organization member by id.
			TransactionManager.executeTransaction(loChannel, lsTransactionName);
			StaffDetails loStaffDetails = (StaffDetails) loChannel
					.getData(ApplicationConstants.ORG_MEMBER_DETAILS_OUTPUT);
			Integer lsAdminCount = (Integer) loChannel.getData("adminCount");
			aoMapForRender.put(HHSR5Constants.ADMIN_COUNT, lsAdminCount);
			aoMapForRender.put(ApplicationConstants.LO_STAFF_DETAILS, loStaffDetails);
			aoRequest.setAttribute("staffId", aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			aoRequest.setAttribute("memberUserTitle", loStaffDetails.getMsStaffTitle());
			aoRequest.setAttribute(HHSR5Constants.ADMIN_COUNT, lsAdminCount);
			if (loStaffDetails.getMsStaffActiveFlag() != null
					&& loStaffDetails.getMsStaffActiveFlag().equalsIgnoreCase("Yes"))
			{
				aoMapForRender.put(HHSR5Constants.MEMBER_AS_USER, true);
			}
			else if (loStaffDetails.getMsStaffActiveFlag() != null
					&& (loStaffDetails.getMsStaffActiveFlag().equalsIgnoreCase("No") && (loStaffDetails
							.getMsUserStatus() != null && loStaffDetails.getMsUserStatus().equalsIgnoreCase(
							ApplicationConstants.IN_ACTIVE))))
			{
				aoMapForRender.put(HHSR5Constants.MEMBER_AS_USER, true);// de
																		// activate
																		// case
			}
			ApplicationSession.setAttribute(loStaffDetails, aoRequest, "persistUserRequestTemp");
		}
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/editOrgMember.jsp");
	}

	/**
	 * This method is used to show the edit user request
	 * 
	 * <li>This method was updated in R4</li>
	 * 
	 * @param aoRequest request object
	 * @param aoMapForRender map for render
	 * @throws ApplicationException application exception
	 */
	private void doShowEditRequest(RenderRequest aoRequest, Map<String, Object> aoMapForRender, String asOrgId)
			throws ApplicationException
	{
		String lsErrorMsg = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MSG2);
		// this is to display the error message when user tries to add the same
		// CEO title again in case of edit
		if (lsErrorMsg != null && lsErrorMsg.equalsIgnoreCase(ApplicationConstants.SHOW_ERROR_MSG))
		{
			aoMapForRender.put(ApplicationConstants.ERROR_MSG2, ApplicationConstants.ERROR_MSG);
			aoMapForRender.put(ApplicationConstants.LO_STAFF_DETAILS,
					ApplicationSession.getAttribute(aoRequest, ApplicationConstants.LO_STAFF_DETAILS));
		}
		else
		{
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.MS_STAFF_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			// R4: Adding Map as parameter to provide additional data for
			// fetching data adding join to STAFF_ORGANIZATION_MAPPING table
			Map<String, String> loParamMap = new LinkedHashMap<String, String>();
			loParamMap.put("asOrgId", asOrgId);
			loParamMap.put("asAdminPermission", "Yes");
			loParamMap.put("asActiveFlag", "Yes");
			loParamMap.put("asUserStatus", "Yes");
			loParamMap.put("orgId", asOrgId);
			loParamMap.put(ApplicationConstants.MS_STAFF_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			loChannel.setData(ApplicationConstants.MS_STAFF_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID));
			loChannel.setData("loParamMap", loParamMap);

			// Execute the transaction to get the organization member list for
			// grid
			TransactionManager.executeTransaction(loChannel, ApplicationConstants.GET_ORG_MEMBER_BY_ID);
			StaffDetails loStaffDetails = (StaffDetails) loChannel
					.getData(ApplicationConstants.ORG_MEMBER_DETAILS_OUTPUT);
			loStaffDetails.setReadOnly("disabled='disabled'");
			aoMapForRender.put(ApplicationConstants.LO_STAFF_DETAILS, loStaffDetails);
			aoRequest.setAttribute("memberUserTitle", loStaffDetails.getMsStaffTitle());
			aoRequest.setAttribute("userRequestId", aoRequest.getParameter(ApplicationConstants.EDIT_USER_REQUEST_ID));
			ApplicationSession.setAttribute(loStaffDetails, aoRequest, "persistUserRequestTemp");
		}
		// show the edit user request jsp
		aoRequest.setAttribute(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/editUserRequest.jsp");
	}

	/**
	 * This is action request to handle all the request which are coming from
	 * the front end
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 */
	@Override
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loPortletSessionThread = aoRequest.getPortletSession();
		String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		// portlet session
		try
		{
			PortletSession loPortletSession = aoRequest.getPortletSession();
			// find the action that needs to be executed
			String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
			// organization id from the session
			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			final String lsCityUserSearchProviderId = (String) aoRequest.getPortletSession().getAttribute(
					"cityUserSearchProviderId", PortletSession.APPLICATION_SCOPE);
			if (lsCityUserSearchProviderId != null)
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
			// check if existing member
			String lsShowExistingMember = PortalUtil.parseQueryString(aoRequest, "showExistingMember");
			// title when edit the screen
			final String lsEditTitle = PortalUtil.parseQueryString(aoRequest, "memberUserTitle");
			// staff details object
			StaffDetails loStaffDetails = new StaffDetails();
			// if user want to add new staff member
			if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.SAVE_ORG_MEMBER))
			{
				doSaveOrgMembers(aoRequest, aoResponse, lsOrgId, loStaffDetails);
			}
			// if user wants to edit the organization members
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.SAVE_EDIT_MEMBERS))
			{
				doSaveEditMembers(aoRequest, aoResponse, lsOrgId, lsEditTitle, loStaffDetails);

			}
			// this condition execute when user click on the cancel button from
			// add and edit page
			else if (lsAction != null && lsAction.equalsIgnoreCase("cancleButton"))
			{
				// set the action to execute
				aoResponse
						.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.DISPLAY_ORG_MEMBER);
			}
			// this condition execute when user want to see the existing member
			// on the pop up page
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.DISPLAY_ORG_MEMBER)
					&& lsShowExistingMember.equalsIgnoreCase("true"))
			{
				// set the action to execute
				aoResponse
						.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.DISPLAY_ORG_MEMBER);
				aoResponse.setRenderParameter(ApplicationConstants.LS_SHOW_EXISTING_MEMBER, "true");
			}
			// this condition execute when user wants to deny the user request
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.DENY_USER_REQUEST))
			{
				doDenyUserRequest(aoRequest, aoResponse, lsOrgId);
			}
			// this condition execute when user wants to approve the user
			// request
			else if (lsAction != null && lsAction.equalsIgnoreCase(ApplicationConstants.APPROVE_USER_REQUEST))
			{
				doApproveUserRequest(aoRequest, aoResponse, lsOrgId, lsEditTitle, loStaffDetails);
			}
			// this action is for organization controllers
			Boolean lsDeactivateValue = (Boolean) ApplicationSession.getAttribute(aoRequest, "isDeactivateUser");
			if (lsDeactivateValue == null || !lsDeactivateValue)
			{
				aoResponse.setRenderParameter("action", "manageMembers");
			}
		}
		catch (ApplicationException aoAppex)
		{
			LOG_OBJECT.Error("Application Exception in ManageMembersController", aoAppex);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Application Exception in ManageMembersController", aoExp);
		}

		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in ManageMembersController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in ManageMembersController ", aoEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method is used to save the organization members
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 * @param asOrgId organization id
	 * @param aoStaffDetails staff details object
	 * @throws ApplicationException
	 */
	private void doSaveOrgMembers(ActionRequest aoRequest, ActionResponse aoResponse, String asOrgId,
			StaffDetails aoStaffDetails) throws ApplicationException
	{
		// check if member title already exist
		final Boolean lbIsExist = checkMemberTitle(aoRequest, aoStaffDetails, asOrgId);
		// if exist display error message
		if (lbIsExist)
		{
			// remain on the same page and display error message
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.SAVE_ORG_MEMBER);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSG2, ApplicationConstants.SHOW_ERROR_MSG);
		}
		else
		{
			// if no error save the data and return to the member list
			aoStaffDetails = new StaffDetails();
			// set the operation type
			aoStaffDetails.setOperationType(ApplicationConstants.INSERT_STAFF);
			// member status
			aoStaffDetails.setMsMemberStatus(ApplicationConstants.ACTIVE);
			// set the system user or the user status
			aoStaffDetails.setMsUserStatus("No");
			// user created date
			aoStaffDetails.setMsUserAcctCreationDate(new Date(System.currentTimeMillis()));
			// call the private method to save the organization members
			List<StaffDetails> loListMembers = addEditOrganigationMember(aoRequest, aoStaffDetails, asOrgId, true, null);
			// save the list in application scope and get this session scope on
			// render method
			ApplicationSession.setAttribute(loListMembers, aoRequest, ApplicationConstants.DISPLAY_LIST_AFTER_SAVE);
			// set the action to execute
			aoResponse
					.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.SAVE_AND_DISPLAY_STAFF);
		}
	}

	/**
	 * This method modified as a part of release 3.2.0 for enhancement 5650.
	 * <ul>
	 * <li>Code added to segregate DB queries for de-activate and Remove user</li>
	 * </ul>
	 * 
	 * This method is used to save the edit members
	 * 
	 * @param aoRequest request object
	 * @param aoResponse response object
	 * @param asOrgId organization id
	 * @param asEditTitle edit member title
	 * @param aoStaffDetails staff details object
	 * @throws ApplicationException
	 */
	private void doSaveEditMembers(ActionRequest aoRequest, ActionResponse aoResponse, String asOrgId,
			final String asEditTitle, StaffDetails aoStaffDetails) throws ApplicationException
	{
		LOG_OBJECT.Debug("Start of doSaveEditMembers method");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		boolean lbInactiveToActive = false;
		StaffDetails loDetailsTemp = (StaffDetails) ApplicationSession.getAttribute(aoRequest, true,
				"persistUserRequestTemp");
		String lsDeActivatedUser = PortalUtil.parseQueryString(aoRequest, "deActivatedUser");
		if (null != loDetailsTemp && null != aoStaffDetails
				&& loDetailsTemp.getMsMemberStatus().equalsIgnoreCase("InActive")
				&& aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) == null)
		{
			lbInactiveToActive = true;
		}
		// check if member title already exist
		Boolean lbIsExist = false;
		if (asEditTitle != null && !asEditTitle.equalsIgnoreCase("1") || lbInactiveToActive)
		{
			lbIsExist = checkMemberTitle(aoRequest, aoStaffDetails, asOrgId);
		}

		// Call transaction
		Channel loChannel = new Channel();
		loChannel.setData(HHSR5Constants.LO_PARAM, aoStaffDetails);
		aoStaffDetails.setMsStaffId(aoRequest.getParameter(ApplicationConstants.EDIT_STAFF_ID));
		TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_CONTRACT_RESTRICTION_COUNT_EDIT);
		Integer loSaveCount = (Integer) loChannel.getData(HHSR5Constants.COUNT_DEACTIVATOR);

		// if exist display error message
		if (lbIsExist)
		{
			// remain on the same page and display error message
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.EDIT_ORG_MEMBER);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSG2, ApplicationConstants.SHOW_ERROR_MSG);
			aoResponse.setRenderParameter(HHSR5Constants.ADMIN_COUNT,
					aoRequest.getParameter(HHSR5Constants.ADMIN_COUNT));
		}
		// Release 5
		else if (loSaveCount > 0
				&& ((lsDeActivatedUser != null && lsDeActivatedUser.equalsIgnoreCase(HHSConstants.ON))
						|| (aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) != null && aoRequest
								.getParameter(ApplicationConstants.REMOVE_MEMBER).equalsIgnoreCase(HHSConstants.ON)) || PortalUtil
						.parseQueryString(aoRequest, HHSR5Constants.PERMISSION_TYPE_VALUE) != null
						&& (PortalUtil.parseQueryString(aoRequest, HHSR5Constants.PERMISSION_TYPE_VALUE)
								.equalsIgnoreCase(ApplicationConstants.ROLE_PROCUREMENT) || PortalUtil
								.parseQueryString(aoRequest, HHSR5Constants.PERMISSION_TYPE_VALUE)
								.equalsIgnoreCase("R"))))
		{
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.EDIT_ORG_MEMBER);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSG2,
					"showErrorMsgContractRestrictionDeactivatedUser");
			aoResponse.setRenderParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID,
					aoRequest.getParameter(ApplicationConstants.EDIT_STAFF_ID));
		}
		else
		{
			// check the user flag to set the yes or no
			String lsUserFlag = PortalUtil.parseQueryString(aoRequest, "userFlag");
			final String lsUserStatus = PortalUtil.parseQueryString(aoRequest, "userStatus");
			String lsAdminValue = PortalUtil.parseQueryString(aoRequest, "adminValue");
			Boolean loIsDeactivateUser = false;
			Boolean loDeActivateUser = false;
			if (lsUserFlag != null
					&& (lsUserFlag.equalsIgnoreCase("No") && (lsUserStatus != null && lsUserStatus
							.equalsIgnoreCase(ApplicationConstants.IN_ACTIVE))))
			{
				loDeActivateUser = true;
			}
			// set the operation type
			aoStaffDetails.setOperationType(ApplicationConstants.EDIT_STAFF);

			if (lsAdminValue != null && lsAdminValue.equalsIgnoreCase("true"))
			{
				// check if remove check box is checked, if checked user needs
				// to be
				// in active
				if (aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) != null
						&& aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER).equalsIgnoreCase(HHSConstants.ON))
				{
					// changes made as part of release 3.2.0 defect 5650 - start
					aoStaffDetails.setMsUserAction(ApplicationConstants.REMOVE_USER);
					aoStaffDetails.setMsUserDN(null);
					// changes made as part of release 3.2.0 defect 5650 - end
					// set the in active status of member
					aoStaffDetails.setMsMemberStatus(ApplicationConstants.IN_ACTIVE);
					// set the user status
					if (lsUserFlag != null && lsUserFlag.equalsIgnoreCase("Yes"))
					{
						aoStaffDetails.setMsUserStatus("No");
						aoStaffDetails.setMsStaffActiveFlag("No");
					}
					else
					{
						aoStaffDetails.setMsUserStatus("No");
					}
					// in activation date
					aoStaffDetails.setMsMemberInactiveDate(DateUtil.getSqlDate(aoRequest.getParameter("datepicker")));
				}
				else
				{
					// set the status as active
					aoStaffDetails.setMsMemberStatus(ApplicationConstants.ACTIVE);
					// set the user status
					if (lsUserFlag != null && lsUserFlag.equalsIgnoreCase("Yes"))
					{
						aoStaffDetails.setMsUserStatus("Yes");
						aoStaffDetails.setMsStaffActiveFlag("Yes");
					}
					else
					{
						aoStaffDetails.setMsUserStatus("No");
					}
					aoStaffDetails.setMsMemberInactiveDate(null);
				}
				// String lsDeActivatedUser =
				// PortalUtil.parseQueryString(aoRequest, "deActivatedUser");
				StaffDetails loDetails = (StaffDetails) ApplicationSession.getAttribute(aoRequest,
						"persistUserRequestTemp");
				if (null != loDetails)
				{
					aoStaffDetails.setMsUserDN(loDetails.getMsUserDN());
				}
				// changes made as part of release 3.2.0 defect 5650 - start
				if (aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) != null
						&& aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER).equalsIgnoreCase(HHSConstants.ON))
				{
					aoStaffDetails.setMsUserDN(null);
				}
				// changes made as part of release 3.2.0 defect 5650 - end
				if (lsDeActivatedUser != null && lsDeActivatedUser.equalsIgnoreCase(HHSConstants.ON))
				{
					// changes made as part of release 3.2.0 defect 5650 - start
					aoStaffDetails.setMsUserAction(ApplicationConstants.DEACTIVATE_USER);
					// changes made as part of release 3.2.0 defect 5650 - end
					aoStaffDetails.setMsUserStatus("No");
					aoStaffDetails.setMsMemberStatus(ApplicationConstants.ACTIVE);
					aoStaffDetails.setMsStaffActiveFlag("No");
					aoStaffDetails.setMsUserDN(null);
					loIsDeactivateUser = true;
				}
				else
				{
					if (loDeActivateUser)
					{
						aoStaffDetails.setMsUserStatus("Yes");
						aoStaffDetails.setMsStaffActiveFlag("Yes");
					}
				}
				if (loIsDeactivateUser)
				{
					aoStaffDetails.setMsAdminPermission("No");
				}
				else
				{
					aoStaffDetails.setMsAdminPermission(PortalUtil.parseQueryString(aoRequest, "adminUserValue"));
				}
				// set the admin permission true or false
				aoStaffDetails.setMsPermissionLevel(PortalUtil.parseQueryString(aoRequest, "permissionLevelValue"));
				aoStaffDetails.setMsPermissionType(PortalUtil.parseQueryString(aoRequest,
						HHSR5Constants.PERMISSION_TYPE_VALUE));
			}
			else
			{
				StaffDetails loDetails = (StaffDetails) ApplicationSession.getAttribute(aoRequest,
						"persistUserRequestTemp");
				if (null != loDetails)
				{
					aoStaffDetails.setMsUserStatus(loDetails.getMsUserStatus());
					aoStaffDetails.setMsMemberStatus(loDetails.getMsMemberStatus());
					aoStaffDetails.setMsStaffActiveFlag(loDetails.getMsStaffActiveFlag());
					aoStaffDetails.setMsMemberInactiveDate(loDetails.getMsMemberInactiveDate());

					aoStaffDetails.setMsPermissionLevel(loDetails.getMsPermissionLevel());
					aoStaffDetails.setMsPermissionType(loDetails.getMsPermissionType());
					// set the admin permission true or false
					aoStaffDetails.setMsAdminPermission(loDetails.getMsAdminPermission());
					aoStaffDetails.setMsUserDN(loDetails.getMsUserDN());
				}

			}
			// member id that needs to be edit
			aoStaffDetails.setMsStaffId(aoRequest.getParameter(ApplicationConstants.EDIT_STAFF_ID));
			// call the private method to edit the organization members
			List<StaffDetails> loListMembers = addEditOrganigationMember(aoRequest, aoStaffDetails, asOrgId, true,
					asEditTitle);

			if (loIsDeactivateUser
					&& lsUserId.equalsIgnoreCase(aoRequest.getParameter(ApplicationConstants.EDIT_STAFF_ID)))
			{
				ApplicationSession.setAttribute(loIsDeactivateUser, aoRequest, "isDeactivateUser");
				String lsURLNotificationBasicForm = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
						+ aoRequest.getServerPort() + aoRequest.getContextPath() + "/portal/hhsweb.portal";
				try
				{
					aoResponse.sendRedirect(lsURLNotificationBasicForm);
				}
				catch (IOException aoExp)
				{
					LOG_OBJECT.Error("Unable to redirect to login screen in case of deactivate user", aoExp);
				}
			}
			else
			{
				// set the action to execute
				aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION,
						ApplicationConstants.SAVE_AND_DISPLAY_STAFF);
				// save the list in application scope and get this session scope
				// on
				// render method
				ApplicationSession.setAttribute(loListMembers, aoRequest, ApplicationConstants.DISPLAY_LIST_AFTER_SAVE);
			}
		}
		LOG_OBJECT.Debug("End of doSaveEditMembers method");
	}

	/**
	 * This method is used to approve the user request by the admin user This
	 * methods updated in R4 due to notification framework change
	 * @param aoRequest request object
	 * @param aoResponse response object
	 * @param asOrgId organization id
	 * @param asEditTitle edit member title
	 * @param aoStaffDetails staff detail object
	 * @throws ApplicationException
	 */
	private void doApproveUserRequest(ActionRequest aoRequest, ActionResponse aoResponse, String asOrgId,
			final String asEditTitle, StaffDetails aoStaffDetails) throws ApplicationException
	{
		// get the user id that needs to deny
		final String lsStaffId = aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID);
		final String lsStaffEmailId = aoRequest.getParameter("staffEmail");
		final String lsUserNameToDisplay = aoRequest.getParameter("staffFirstName").concat(" ")
				.concat(aoRequest.getParameter("staffMidInitial")).concat(" ")
				.concat(aoRequest.getParameter("staffLastName"));
		// check if member title already exist
		Boolean loIsExist = false;
		final String lsStaffTitle = aoRequest.getParameter("staffTitle");
		final String lsStaffTitleSelected = aoRequest.getParameter("existingStaffTitleId");
		if (asEditTitle != null && !asEditTitle.equalsIgnoreCase("1"))
		{
			if (!(null != lsStaffTitleSelected && lsStaffTitleSelected.equalsIgnoreCase(lsStaffTitle) && lsStaffTitle
					.equalsIgnoreCase("1")))
			{

				loIsExist = checkMemberTitle(aoRequest, aoStaffDetails, asOrgId);
			}

		}
		// if exist display error message
		if (loIsExist)
		{
			// set the action
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.EDIT_USER_REQUEST);
			// set the error message
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSG2, ApplicationConstants.SHOW_ERROR_MSG);
		}
		else
		{
			// set the operation type
			aoStaffDetails.setOperationType(ApplicationConstants.EDIT_STAFF);
			// set the user status as active
			aoStaffDetails.setMsUserStatus(ApplicationConstants.ACTIVE);
			// set the user id that needs to be approved
			aoStaffDetails.setMsStaffId(lsStaffId);
			// get the radio button selected value from the popup when edit user
			String lsMemberAsUser = PortalUtil.parseQueryString(aoRequest, "memberAsUser");
			// set value in object
			StaffDetails loDetails = (StaffDetails) ApplicationSession
					.getAttribute(aoRequest, "persistUserRequestTemp");
			loDetails.setMsUserStatus("Yes");
			if (lsMemberAsUser != null && !lsMemberAsUser.equalsIgnoreCase(""))
			{
				// delete this user request
				loDetails.setMemberAsUser(lsStaffId);
			}
			else
			{
				lsMemberAsUser = lsStaffId;
			}
			loDetails.setMsStaffActiveFlag("Yes");
			// set the system user or user status
			loDetails.setMsMemberStatus(ApplicationConstants.ACTIVE);
			// update the member details when link
			loDetails.setMsStaffId(lsMemberAsUser);

			loDetails.setMsPermissionLevel(PortalUtil.parseQueryString(aoRequest, "permissionLevelValue"));
			loDetails.setMsPermissionType(PortalUtil.parseQueryString(aoRequest, HHSR5Constants.PERMISSION_TYPE_VALUE));
			// set the admin permission true or false
			loDetails.setMsAdminPermission(PortalUtil.parseQueryString(aoRequest, "adminUserValue"));
			loDetails.setOperationType(ApplicationConstants.EDIT_STAFF);
			// call the private method to edit the organization members
			final List<StaffDetails> loListMembers;
			if (null != lsStaffTitleSelected && lsStaffTitleSelected.equalsIgnoreCase(lsStaffTitle)
					&& lsStaffTitle.equalsIgnoreCase("1"))
			{
				loListMembers = addEditOrganigationMember(aoRequest, loDetails, asOrgId, false, null);
			}
			else
			{
				loListMembers = addEditOrganigationMember(aoRequest, loDetails, asOrgId, true, null);
			}
			// Notification N020 - User account approved

			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add("NT020");

			List<String> loProviderList = new ArrayList<String>();
			loProviderList.add(asOrgId);

			String lsURLNotificationBasicForm = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
					+ aoRequest.getServerPort() + aoRequest.getContextPath() + "/portal/hhsweb.portal";
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loLinkMap.put("LINK", lsURLNotificationBasicForm);

			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setProviderList(loProviderList);

			HashMap<Object, String> loParamMap = new HashMap<Object, String>();
			loParamMap.put("LINK", lsURLNotificationBasicForm);
			loParamMap.put("USERNAME", lsUserNameToDisplay);

			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationMap.put("NT020", loNotificationDataBean);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "user_email");
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsStaffEmailId);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
			Channel loChannel = new Channel();
			loChannel.setData("loHmNotifyParam", loNotificationMap);
			TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
			// save the list in application scope and get this session scope on
			// render method
			ApplicationSession.setAttribute(loListMembers, aoRequest, ApplicationConstants.DISPLAY_LIST_AFTER_SAVE);
			// set the next action
			aoResponse
					.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.SAVE_AND_DISPLAY_STAFF);
		}
	}

	/**
	 * This method is used to deny the user request by the admin user This
	 * methods updated in R4 due to notification framework change
	 * @param aoRequest request object
	 * @param aoResponse response object
	 * @param asOrgId organization id
	 */
	private void doDenyUserRequest(ActionRequest aoRequest, ActionResponse aoResponse, final String asOrgId)
	{
		// get the user id that needs to deny
		final String lsStaffId = aoRequest.getParameter(ApplicationConstants.EDIT_ORG_MEMBER_ID);
		final String lsStaffEmailId = aoRequest.getParameter("staffEmail");
		final String lsUserNameToDisplay = aoRequest.getParameter("staffFirstName").concat(" ")
				.concat(aoRequest.getParameter("staffMidInitial")).concat(" ")
				.concat(aoRequest.getParameter("staffLastName"));
		Map<String, String> loParamMap = new LinkedHashMap<String, String>();
		// set all the values in map
		loParamMap.put("asOrgId", asOrgId);
		loParamMap.put("lsStaffId", lsStaffId);
		loParamMap.put(ApplicationConstants.IN_ACTIVE2, ApplicationConstants.IN_ACTIVE);
		Channel loChannel = new Channel();
		// set the map in channel
		loChannel.setData("loParamMap", loParamMap);
		try
		{

			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add("NT021");

			List<String> loProviderList = new ArrayList<String>();
			loProviderList.add(asOrgId);

			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			loNotificationDataBean.setProviderList(loProviderList);

			HashMap loParamMapUser = new HashMap();
			loParamMapUser.put("USERNAME", lsUserNameToDisplay);

			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationMap.put("NT021", loNotificationDataBean);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "user_email");
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsStaffEmailId);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMapUser);
			loChannel.setData("loHmNotifyParam", loNotificationMap);

			TransactionManager.executeTransaction(loChannel, ApplicationConstants.DENY_USER_REQUEST_PROFILE);
			aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, ApplicationConstants.DISPLAY_ORG_MEMBER);
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("exception in denyUserRequest", aoExp);
		}
	}

	/**
	 * This method is used to check the member title if already exist return
	 * false else return true
	 * 
	 * @param aoRequest request obhect
	 * @param aoStaffDetails staff detail object
	 * @param asOrgId organization id
	 * @return Boolean true false
	 */
	private Boolean checkMemberTitle(final ActionRequest aoRequest, StaffDetails aoStaffDetails, final String asOrgId)
	{
		Boolean loIsExist = false;
		// get the member tile from the request
		final String lsStaffTitle = aoRequest.getParameter("staffTitle");
		// if CEO selected
		if (lsStaffTitle != null && lsStaffTitle.equalsIgnoreCase("1"))
		{
			// create a parameter map
			Map<String, String> loParamMap = new LinkedHashMap<String, String>();
			// set all values in param map
			loParamMap.put("asOrgId", asOrgId);
			loParamMap.put("asCEOId", lsStaffTitle);
			Channel loChannel = new Channel();
			// set map in channel
			loChannel.setData("loParamMap", loParamMap);
			try
			{
				// execute the transaction to check the CEO title
				TransactionManager.executeTransaction(loChannel, "checkCEOOfficer");
				// get the data after the transaction executed
				aoStaffDetails = (StaffDetails) loChannel.getData("validateCEOOfficer");
				// check if CEO title already executed
				if (aoStaffDetails != null && aoStaffDetails.getMsStaffTitle() != null
						&& aoStaffDetails.getMsStaffTitle().equalsIgnoreCase("1"))
				{
					loIsExist = true;
					// set all the values in bean to persist on the page
					aoStaffDetails.setMsStaffFirstName(aoRequest.getParameter("staffFirstName"));
					aoStaffDetails.setMsStaffMidInitial(aoRequest.getParameter("staffMidInitial"));
					aoStaffDetails.setMsStaffLastName(aoRequest.getParameter("staffLastName"));
					aoStaffDetails.setMsStaffTitle(lsStaffTitle);
					aoStaffDetails.setMsStaffPhone(aoRequest.getParameter("staffPhone"));
					aoStaffDetails.setMsStaffEmail(aoRequest.getParameter("staffEmail"));
					// set the permission level
					aoStaffDetails.setMsPermissionLevel(PortalUtil.parseQueryString(aoRequest, "permissionLevelValue"));
					aoStaffDetails.setMsPermissionType(PortalUtil.parseQueryString(aoRequest,
							HHSR5Constants.PERMISSION_TYPE_VALUE));
					// set the admin permission true false
					aoStaffDetails.setMsAdminPermission(PortalUtil.parseQueryString(aoRequest, "adminUserValue"));
					aoStaffDetails.setMsUserStatus(PortalUtil.parseQueryString(aoRequest, "userStatus"));
					String lsDeActivatedUser = PortalUtil.parseQueryString(aoRequest, "deActivatedUser");
					if (lsDeActivatedUser != null && lsDeActivatedUser.equalsIgnoreCase(HHSConstants.ON))
					{
						ApplicationSession.setAttribute(lsDeActivatedUser, aoRequest, ApplicationConstants.DEACTIVATE_USER);
					}
					// check the member status
					if (aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) != null)
					{
						aoStaffDetails.setMsMemberStatus(ApplicationConstants.IN_ACTIVE);
						aoStaffDetails
								.setMsMemberInactiveDate(DateUtil.getSqlDate(aoRequest.getParameter("datepicker")));
					}
				}
				// set the object in the session scope
				ApplicationSession.setAttribute(PortalUtil.parseQueryString(aoRequest, HHSR5Constants.MEMBER_AS_USER),
						aoRequest, HHSR5Constants.MEMBER_AS_USER);
				ApplicationSession.setAttribute(aoStaffDetails, aoRequest, ApplicationConstants.LO_STAFF_DETAILS);
			}
			catch (ApplicationException aoExp)
			{
				LOG_OBJECT.Error("exception in checkMemberTitle", aoExp);
			}
		}
		return loIsExist;
	}

	/**
	 * This method is used to add and update the members and the users This
	 * methods updated in R4 due to notification framework change
	 * @param aoRequest request object
	 * @param aoStaffDetails staff detail object
	 * @param asOrgId organization id
	 * @param asEditUser editUser
	 * @return List of staff members
	 * @throws ApplicationException
	 */
	private List<StaffDetails> addEditOrganigationMember(final ActionRequest aoRequest, StaffDetails aoStaffDetails,
			final String asOrgId, final boolean asEditUser, String asEditTitle) throws ApplicationException
	{
		LOG_OBJECT.Debug("Start of addEditOrganigationMember method");
		Channel loChannel = new Channel();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		// set values in object from the request
		aoStaffDetails.setMsStaffFirstName(aoRequest.getParameter("staffFirstName"));
		aoStaffDetails.setMsStaffMidInitial(aoRequest.getParameter("staffMidInitial"));
		aoStaffDetails.setMsStaffLastName(aoRequest.getParameter("staffLastName"));
		aoStaffDetails.setMsStaffTitle(aoRequest.getParameter("staffTitle"));
		aoStaffDetails.setMsStaffPhone(aoRequest.getParameter("staffPhone"));
		aoStaffDetails.setMsStaffEmail(aoRequest.getParameter("staffEmail"));
		aoStaffDetails.setMsOrgId(asOrgId);
		aoStaffDetails.setMsSystemUser("No");
		aoStaffDetails.setMsCreatedBy(lsUserId);
		aoStaffDetails.setMsCreatedDate(new Date(System.currentTimeMillis()));
		// set object into channel
		loChannel.setData("newStaff", aoStaffDetails);
		loChannel.setData("orgId", asOrgId);
		String lsCityUrl = " ";
		boolean lbRemoveUser = false;
		if (aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER) != null
				&& aoRequest.getParameter(ApplicationConstants.REMOVE_MEMBER).equalsIgnoreCase(HHSConstants.ON))
		{
			lbRemoveUser = true;
		}
		try
		{
			// Execute the transaction to insert organization member details
			TransactionManager.executeTransaction(loChannel, "insertOrgMemberDetails");
			String lsStaffId = aoStaffDetails.getMsStaffId();
			if (!(null != asEditTitle && null != aoStaffDetails.getMsStaffTitle()
					&& asEditTitle.equalsIgnoreCase(aoStaffDetails.getMsStaffTitle()) && !lbRemoveUser))
			{
				if (asEditUser && "1".equalsIgnoreCase(aoStaffDetails.getMsStaffTitle()))
				{
					if (ApplicationConstants.ACTIVE.equalsIgnoreCase(aoStaffDetails.getMsMemberStatus()))
					{
						// Sending notification to Accelerator Manger NT033
						String lsProviderName = null;
						if (null != FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(),
								asOrgId))
						{
							lsProviderName = FileNetOperationsUtils.getProviderName(
									FileNetOperationsUtils.getProviderList(), asOrgId);
						}
						HashMap<Object, String> loParamMap = new HashMap<Object, String>();
						loParamMap.put("PROVIDER", lsProviderName);
						lsCityUrl = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
								ApplicationConstants.PROPERTY_CITY_URL);
						String lsURLNotificationBasicForm = lsCityUrl + "/portal/hhsweb.portal";
						// setting parameters to send notification

						HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
						List<String> loNotificationAlertList = new ArrayList<String>();
						loNotificationAlertList.add("NT033");
						NotificationDataBean loNotificationDataBean = new NotificationDataBean();
						HashMap<String, String> loLinkMap = new HashMap<String, String>();
						loLinkMap.put("LINK", lsURLNotificationBasicForm);
						loNotificationDataBean.setLinkMap(loLinkMap);
						loNotificationDataBean.setAgencyLinkMap(loLinkMap);
						loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
						loHmNotifyParam.put("NT033", loNotificationDataBean);
						loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
						loHmNotifyParam.put(HHSConstants.MODIFIED_BY, lsUserId);
						loHmNotifyParam.put(ApplicationConstants.ENTITY_TYPE, "userId");
						loHmNotifyParam.put(ApplicationConstants.ENTITY_ID, lsStaffId);
						List<String> loUserIdList = new ArrayList<String>();
						loUserIdList.add(aoStaffDetails.getMsStaffEmail());
						loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
						loHmNotifyParam.put(TransactionConstants.ACCELERATOR_ID, ApplicationConstants.CITY_ORG);
						loChannel.setData("loHmNotifyParam", loHmNotifyParam);
						TransactionManager.executeTransaction(loChannel, HHSConstants.INSERT_NOTIFICATION_DETAILS);
					}
					else if (ApplicationConstants.IN_ACTIVE.equalsIgnoreCase(aoStaffDetails.getMsMemberStatus()))
					{
						// Sending notification to Accelerator Manger NT032
						sendNotificationNT032(asOrgId, lsUserId, lsStaffId);
					}
				}
				else if (null != asEditTitle && asEditTitle.equalsIgnoreCase("1"))
				{

					sendNotificationNT032(asOrgId, lsUserId, lsStaffId);
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("exception in addEditOrganigationMember", aoExp);
		}
		LOG_OBJECT.Debug("End of addEditOrganigationMember method");
		// return the list of members
		return (List<StaffDetails>) loChannel.getData(ApplicationConstants.ALL_ORG_MEMBER_LIST_FOR_GRID);
	}

	/**
	 * This method is updated in R4 for notification change This method send
	 * notification NT032
	 * @param asOrgId
	 * @param asUserId
	 * @param asStaffId
	 * @throws ApplicationException
	 * @return void
	 */
	private void sendNotificationNT032(String asOrgId, String asUserId, String asStaffId) throws ApplicationException
	{
		// Sending notification to Accelerator Manger NT032
		Channel loChannel = new Channel();
		String lsProviderName = null;
		if (null != FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(), asOrgId))
		{
			lsProviderName = FileNetOperationsUtils.getProviderName(FileNetOperationsUtils.getProviderList(), asOrgId);
		}
		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		loParamMap.put("PROVIDER", lsProviderName);
		String lsCityUrl = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				ApplicationConstants.PROPERTY_CITY_URL);
		String lsURLNotificationBasicForm = lsCityUrl + "/portal/hhsweb.portal";

		HashMap<String, Object> loHmNotifyParam = new HashMap<String, Object>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT032");
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put("LINK", lsURLNotificationBasicForm);
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loHmNotifyParam.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		loHmNotifyParam.put(HHSConstants.ENTITY_TYPE, asUserId);
		loHmNotifyParam.put(HHSConstants.ENTITY_TYPE, HHSConstants.ATTRIBUTE_GET_STAFF_ID);
		loHmNotifyParam.put("NT032", loNotificationDataBean);
		loHmNotifyParam.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		loHmNotifyParam.put(HHSConstants.MODIFIED_BY, asUserId);
		loHmNotifyParam.put(ApplicationConstants.ENTITY_TYPE, "userId");
		loHmNotifyParam.put(ApplicationConstants.ENTITY_ID, asStaffId);
		loHmNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
		loHmNotifyParam.put(TransactionConstants.ACCELERATOR_ID, ApplicationConstants.CITY_ORG);
		loChannel.setData("loHmNotifyParam", loHmNotifyParam);
		TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
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
}
