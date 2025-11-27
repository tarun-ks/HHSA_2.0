package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.ComponentMappingConstant;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.ContactUsBean;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.OrgNameChangeBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.SectionBean;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.model.Task;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.model.WithdrawalBean;
import com.nyc.hhs.model.WorkFlowDetailBean;
import com.nyc.hhs.model.WorkItemInbox;
import com.nyc.hhs.model.WorkflowDetails;
import com.nyc.hhs.model.WorkflowIDServiceBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.DocumentLapsingUtility;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.ProviderStatusBusinessRules;

/**
 * This Controller Class is used to Display & Execute all FileNet Workflows.
 * This controller is also used whenever we are performing any task related
 * action through city users. Following are the transactions and actions which
 * are mainly used for this controller Finish Task : finishchildtask_filenet for
 * child task item and finishparenttask_filenet for parent task item Reassign
 * Task : assign_filenet transaction will get called Save Comment & Audit :
 * AuditInformation transaction Reserve : assign_filenet transaction will be
 * performed Validations Based on the Current user Logged In .
 * 
 */

public class WorkflowDetailController extends AbstractController implements ResourceAwareController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(WorkflowDetailController.class);

	@Override
	@SuppressWarnings(
	{ "rawtypes" })
	/**
	 * Spring Render Method used to fetch the view JSP and is been called automatically once the action method
	 * finishes.
	 * 
	 * @param aoReq 
	 *                      Render Request
	 * @param aoRes
	 *                      Render Response
	 * @return ModelAndView
	 * @throws ApplicationException
	 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoReq, RenderResponse aoRes)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		ModelAndView loModelAndView = null;
		PortletSession loSession = aoReq.getPortletSession();
		String lsSessionUserName = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserOrg = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
				PortletSession.APPLICATION_SCOPE);
		String lsEmailId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
				PortletSession.APPLICATION_SCOPE);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserId);
		String lsTaskName = aoReq.getParameter(ApplicationConstants.AWARD_TASK_NAME);
		if (null != lsTaskName && lsTaskName.equalsIgnoreCase(ApplicationConstants.TASK_NAME_APPROVE_AWARD))
		{
			aoReq.setAttribute(ApplicationConstants.TASK_NAME, lsTaskName);
			String lsRenderAction = aoReq.getParameter(ApplicationConstants.HANDLE_RENDER_ACTION);
			if (null != lsRenderAction && lsRenderAction.equalsIgnoreCase(ApplicationConstants.CONFIRM_OVERRIDE))
			{
				loModelAndView = null;
			}
			else
			{
				loModelAndView = showAwardApprovalTaskDetails(aoReq, aoRes);
			}
		}
		else
		{
			ApplicationSession.setAttribute(lsSessionUserName, aoReq, "loginUserName");
			ApplicationSession.setAttribute(lsUserId, aoReq, "loginUserId");
			ApplicationSession.setAttribute(lsEmailId, aoReq, "EmailAddress");
			String lsTaskId = (String) ApplicationSession.getAttribute(aoReq, true, "taskId");
			List<ApplicationAuditBean> loTaskHistoryList = null;
			String lsReturnInboxFromHttp = PortalUtil.parseQueryString(aoReq, "returninbox");
			String lsActionParamFromHttp = PortalUtil.parseQueryString(aoReq, "action");
			String lsSortWithPaging = PortalUtil.parseQueryString(aoReq, "paging_sorting");
			String lsFilterTrue = PortalUtil.parseQueryString(aoReq, "filteristrue");
			String lsShowManagementView = aoReq.getParameter("showmanagementview");
			HashMap loFilterDetails = new HashMap();
			boolean lbCheckManagerRole;
			int liPageIndex = 0;
			int liTaskCount = 0;
			int liPageSize = Integer.parseInt(P8Constants.PE_GRID_PAGE_SIZE);
			String lsAppMenuName = PortalUtil.parseQueryString(aoReq, "app_menu_name");
			try
			{
				if (null != lsAppMenuName
						&& (lsAppMenuName.equalsIgnoreCase("inbox_icon") || lsAppMenuName
								.equalsIgnoreCase("header_application")))
				{
					lsTaskId = null;
					if (lsAppMenuName.equalsIgnoreCase("inbox_icon"))
					{
						ApplicationSession.setAttribute("inbox", aoReq, "choosenTab");
					}
				}
				lbCheckManagerRole = checkUserRole(aoReq, lsUserOrg);
				lsShowManagementView = pagingAndSorting(aoReq, lsActionParamFromHttp, lsSortWithPaging,
						lsShowManagementView);
				aoReq.setAttribute("pageIndex", ApplicationSession.getAttribute(aoReq, true, "pageIndex"));
				if (lsReturnInboxFromHttp != null
						&& lsReturnInboxFromHttp.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_INBOX))
				{
					lsTaskId = null;
					lsFilterTrue = "yes";
					aoReq.setAttribute("pageIndex", ApplicationSession.getAttribute(aoReq, true, "pageIndex"));
				}
				if (lsFilterTrue == null)
				{
					lsFilterTrue = aoReq.getParameter("filteristrue");
				}
				HashMap loUserMap = (HashMap) loSession.getAttribute("UserMap", PortletSession.APPLICATION_SCOPE);
				aoReq.setAttribute("userMap", loUserMap);
				if (lsTaskId != null)
				{
					String lsAppId = (String) ApplicationSession.getAttribute(aoReq, "appId");
					String lsSectionId = (String) ApplicationSession.getAttribute(aoReq, "sectionId");
					String lsTaskType = (String) ApplicationSession.getAttribute(aoReq, "taskType");
					String lsProviderId = (String) ApplicationSession.getAttribute(aoReq, "providerId");
					setAttributeInSession(aoReq, loSession, lsTaskId, lsAppId, lsProviderId, lsSectionId, lsUserId);
					String lsSuspendOverlay = PortalUtil.parseQueryString(aoReq, "SuspendAction");

					if (lsTaskType != null)
					{
						loModelAndView = fetchTaskDetailsView(loModelAndView, aoReq, loSession, lsTaskType, lsAppId,
								lsTaskId, lsSectionId, loTaskHistoryList);
					}
					else if (null != lsSuspendOverlay && lsSuspendOverlay.equalsIgnoreCase("Suspended"))
					{
						aoReq.setAttribute("taskId", lsTaskId);
						loModelAndView = new ModelAndView("TaskSuspendCommentsOverlay");
					}
				}

				else
				{
					aoReq.setAttribute("loginUserID", lsUserId);
					String lsCurrentTab = (String) ApplicationSession.getAttribute(aoReq, true, "choosenTab");
					String lsInboxPath = "/WEB-INF/jsp/TaskDetails/inbox.jsp";
					String lsManagementPath = "/WEB-INF/jsp/TaskDetails/TaskManagement.jsp";
					loModelAndView = fetchTaskListView(loModelAndView, aoReq, loSession, loUserSession, lsCurrentTab,
							lsManagementPath, lsInboxPath, lsFilterTrue, lsShowManagementView, loFilterDetails,
							liPageIndex, liTaskCount, liPageSize, lbCheckManagerRole, lsUserId);
				}

			}
			catch (ApplicationException aoAppex)
			{
				LOG_OBJECT.Error("Application Exception in WorkFlow Detail Controller", aoAppex);
			}
			long loEndTimeTime = System.currentTimeMillis();
			try
			{
				LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in WorkflowDetailController = "
						+ (loEndTimeTime - loStartTime));
			}
			catch (ApplicationException aoEx)
			{
				LOG_OBJECT.Error("Error while execution of render Method in WorkflowDetailController ", aoEx);
			}
			UserThreadLocal.unSet();
		}
		setAgencyNames(aoReq);
		return loModelAndView;
	}

	/**
	 * Spring Action method Called every time when ever user takes any action on
	 * the application
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @throws ApplicationException
	 */
	@Override
	@SuppressWarnings("rawtypes")
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserIdThreadLocal = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		UserThreadLocal.setUser(lsUserIdThreadLocal);
		String lsSessionUserName = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserName");
		String lsEmailId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		String lsFinishChildStatus = aoRequest.getParameter("finishtaskchild");
		String lsIsComingFromTaskDetails = aoRequest.getParameter("iscomingfromtaskdetails");
		String lsTaskName = aoRequest.getParameter("taskName");
		String lsNextAction = aoRequest.getParameter("next_action");
		String lsFilterTrue = aoRequest.getParameter("filteristrue");
		String lsTaskId = aoRequest.getParameter("taskid");
		String lsActionParamFromHttp = aoRequest.getParameter("action");
		String lsSortWithPaging = aoRequest.getParameter("paging_sorting");
		String lsChoosenTab = aoRequest.getParameter("usewindow");
		ArrayList<TaskQueue> loTaskQueueItems = null;
		HashMap loFilterDetails = null;
		HashMap loSectionDetail = null;
		String lsProviderId = "";
		String lsProviderName = "";
		String lsSectionId = "";
		String lsTaskType = "";
		String lsApplicationId = "";
		String lsWFEntityID = "";
		boolean lbIsValidUser = true;
		HashMap loResultStatus = new HashMap();
		String lsReturnInboxFromHttp = aoRequest.getParameter("returninbox");
		boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true, "managerRole");
		if (null == lsEmailId)
		{
			lsEmailId = "";
		}
		OrgNameChangeBean loBean = (OrgNameChangeBean) ApplicationSession.getAttribute(aoRequest, true, "orgDetails");
		Task loTaskBean = (Task) loSession.getAttribute("filenetWorkItemDetails", PortletSession.APPLICATION_SCOPE);
		String lsAwardTaskName = aoRequest.getParameter(ApplicationConstants.AWARD_TASK_NAME);
		// added code for R2 task
		if (null != lsAwardTaskName && lsAwardTaskName.equalsIgnoreCase(ApplicationConstants.TASK_NAME_APPROVE_AWARD))
		{
			processActionForAwardApprovalTask(aoRequest, aoResponse, lsTaskId, lsAwardTaskName);
		}
		else
		{
			try
			{
				if (lsReturnInboxFromHttp != null && lsReturnInboxFromHttp.equalsIgnoreCase("BRApplication"))
				{
					String lsBRAppTaskID = (String) loSession.getAttribute("BRAppTaskID",
							PortletSession.APPLICATION_SCOPE);
					if (lsBRAppTaskID != null)
					{
						lsTaskId = lsBRAppTaskID;
					}
				}
				ApplicationSession.setAttribute(lsTaskId, aoRequest, "taskId");
				if (lsTaskName == null)
				{
					lsTaskName = "";
				}
				if (lsTaskId != null)
				{
					loSectionDetail = getTaskDetails(loUserSession, lsTaskId);
					if (loSectionDetail != null && !loSectionDetail.isEmpty())
					{
						lsProviderId = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_PROVIDER_ID);
						lsProviderName = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_PROVIDER_NAME);
						lsApplicationId = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_APPLICTION_ID);
						lsSectionId = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_SECTION_ID);
						lsTaskType = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_TASK_TYPE);
						lsWFEntityID = (String) loSectionDetail.get(P8Constants.PROPERTY_PE_ENTITY_ID);
					}
					lbIsValidUser = isValidUser(loUserSession, lsTaskId, lsUserId);
				}
				if (null == lsChoosenTab)
				{
					lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true, "choosenTab");
				}
				if (lsActionParamFromHttp != null)
				{
					if (lsActionParamFromHttp.equalsIgnoreCase("paging") || lsSortWithPaging.equalsIgnoreCase("true"))
					{
						String lsPageIndex = aoRequest.getParameter("pageIndex");
						ApplicationSession.setAttribute(lsPageIndex, aoRequest, "pageIndex");
					}
				}
				if (lsFilterTrue != null && lsFilterTrue.equalsIgnoreCase("yes"))
				{
					loFilterDetails = filterAction(aoRequest, aoResponse, loSession, loUserSession, lsUserId,
							lsFilterTrue, lsChoosenTab, lsApplicationId, lbManangerRole);
				}
				else
				{
					loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true, "filterTab");
					if (null != loFilterDetails
							&& lsTaskId == null
							&& !(aoRequest.getParameter("taskTabValue") != null && ((String) aoRequest
									.getParameter("taskTabValue")).equalsIgnoreCase("onTabClick")))
					{
						loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
								loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
						FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems,
								lsApplicationId);
					}
				}
				if (lsChoosenTab != null)
				{
					ApplicationSession.setAttribute(lsChoosenTab, aoRequest, "choosenTab");
				}
				if (lsNextAction != null)
				{
					lsTaskId = actionAfterNextAction(aoRequest, aoResponse, loSession, loUserSession,
							lsSessionUserName, lsEmailId, lsUserId, lsFinishChildStatus, lsIsComingFromTaskDetails,
							lsTaskName, lsNextAction, lsTaskId, loFilterDetails, lsProviderId, lsProviderName,
							lsSectionId, lsTaskType, lsApplicationId, lsWFEntityID, lbIsValidUser, loResultStatus,
							loBean, loTaskBean, lsChoosenTab, lbManangerRole);
				}
				if (lsTaskId != null)
				{
					fetchTaskDetails(aoRequest, aoResponse, lsTaskId, loUserSession, lsTaskType);
					// Start : R5 Added for AutoSave
					CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), lsTaskId, HHSR5Constants.TASKS);
					// End : R5 Added for AutoSave
				}
			}
			catch (ApplicationException aoAppex)
			{
				catchException(aoRequest, aoAppex);
			}
			catch (Exception aoExp)
			{
				LOG_OBJECT.Error(" Exception in WorkFlow Detail Controller", aoExp);
				ApplicationSession.setAttribute("Error Ocuured While Executing a transaction", aoRequest,
						ApplicationConstants.ERROR_MESSAGE);
				ApplicationSession.setAttribute("failed", aoRequest, "messagetype");
			}
			long loEndTimeTime = System.currentTimeMillis();
			try
			{
				LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in WorkflowDetailController = "
						+ (loEndTimeTime - loStartTime));
			}
			catch (ApplicationException aoEx)
			{
				LOG_OBJECT.Error("Error while execution of action Method in WorkflowDetailController ", aoEx);
			}
		}

	}

	/**
	 * Method to Update the Provider & Internal Comments in the DataBase using
	 * transaction "AuditInformation"
	 * 
	 * @param aoRequest ActionRequest
	 * @param asTaskName Name of the Task
	 * @param asProviderId Provider ID
	 * @param asSectionId Section ID
	 * @param asApplicationId Application ID
	 * @param asTaskType Type of the Task
	 * @param asUserEmailId User Email Id
	 * @param abOnReassign On Reassign
	 * @param asWFEntityID WF Entity ID
	 * @throws ApplicationException
	 */
	private void updateComments(ActionRequest aoRequest, String asTaskName, String asProviderId, String asSectionId,
			String asApplicationId, String asTaskType, String asUserEmailId, boolean abOnReassign, String asWFEntityID)
			throws ApplicationException
	{

		String lsPublicComment = aoRequest.getParameter("publicCommentArea");
		String lsInternalComment = aoRequest.getParameter("internalCommentArea");
		String lsEntityId;
		Date loDate = new Date();
		String lsEntityType = asTaskName;
		String lsEntityIdentifier = asTaskName;
		String lsProviderFlag = "false";
		String lsAuditType = "";
		boolean lbInsertStatus = false;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateComments() with parameters::" + asTaskName + ","
				+ asProviderId + "," + asSectionId + "," + asApplicationId + "," + asUserEmailId + "," + asWFEntityID);
		if (null == asWFEntityID || asWFEntityID.isEmpty())
		{
			lsEntityId = asApplicationId;
		}
		else
		{
			lsEntityId = asWFEntityID;
		}

		String lsSucessMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M43");

		HashMap loLastCommentReqProps = new HashMap();
		loLastCommentReqProps.put("appid", asApplicationId);
		loLastCommentReqProps.put("sectionId", asSectionId);
		loLastCommentReqProps.put("entityId", lsEntityId);
		loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
		String lsLastProviderComments = "";

		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
		{
			if (!abOnReassign)
			{
				lsLastProviderComments = getLastProviderCommentGeneral(loLastCommentReqProps);
			}
			lsAuditType = ApplicationConstants.AUDIT_TYPE_GENERAL;
		}
		else
		{
			if (!abOnReassign)
			{
				lsLastProviderComments = getLastProviderComment(loLastCommentReqProps, asTaskType);
			}
			lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;
		}
		if (null == lsLastProviderComments)
		{
			lsLastProviderComments = "";
		}
		/************ Save Comments in TaskHistory ***********/
		if (lsPublicComment != null && asApplicationId != null && (!lsPublicComment.trim().equalsIgnoreCase(""))
				&& (!lsPublicComment.equalsIgnoreCase(P8Constants.PROPERTY_PE_COMMENTS))
				&& (!lsPublicComment.trim().equalsIgnoreCase(lsLastProviderComments.trim())) && (!abOnReassign))
		{
			Channel loChannel = new Channel();
			CommonUtil.addAuditDataToChannel(loChannel, asProviderId, P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
					P8Constants.EVENT_TYPE_WORKFLOW, loDate, asUserEmailId, lsPublicComment, lsEntityType, lsEntityId,
					lsProviderFlag, asApplicationId, asSectionId, lsAuditType);
			loChannel.setData("EntityIdentifier", lsEntityIdentifier);
			TransactionManager.executeTransaction(loChannel, "AuditInformation");
			lbInsertStatus = true;
			updateCommentsInTransactionTable(asTaskName, asTaskType, asApplicationId, asSectionId, lsPublicComment,
					lsInternalComment, aoRequest);
		}
		if (lsInternalComment != null && (!lsInternalComment.trim().equalsIgnoreCase(""))
				&& (!lsInternalComment.equalsIgnoreCase(P8Constants.PROPERTY_PE_COMMENTS)))
		{
			if (asApplicationId != null)
			{
				Channel loChannel = new Channel();
				CommonUtil.addAuditDataToChannel(loChannel, asProviderId, P8Constants.PROPERTY_PE_TH_INTERNAL_COMMENT,
						P8Constants.EVENT_TYPE_WORKFLOW, loDate, asUserEmailId, lsInternalComment, lsEntityType,
						lsEntityId, lsProviderFlag, asApplicationId, asSectionId, lsAuditType);
				loChannel.setData("EntityIdentifier", lsEntityIdentifier);
				TransactionManager.executeTransaction(loChannel, "AuditInformation");
				lbInsertStatus = true;
				updateCommentsInTransactionTable(asTaskName, asTaskType, asApplicationId, asSectionId, lsPublicComment,
						lsInternalComment, aoRequest);
			}
		}
		if (!abOnReassign)
		{
			if (lbInsertStatus)
			{
				ApplicationSession.setAttribute(lsSucessMsg, aoRequest, "message");
				ApplicationSession.setAttribute("passed", aoRequest, "messagetype");
			}
			else
			{
				ApplicationSession.setAttribute(null, aoRequest, "message");
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateComments()");
	}

	/**
	 * This method update comments in Transaction Table
	 * 
	 * @param asTaskName TaskName
	 * @param asTaskType TaskType
	 * @param asAppId Application Id
	 * @param asSectionId Section Id
	 * @param asPublicComment Public Comments
	 * @param asInternalComment Internal Comments
	 * @param aoRequest Request
	 * @throws ApplicationException
	 */
	private void updateCommentsInTransactionTable(String asTaskName, String asTaskType, String asAppId,
			String asSectionId, String asPublicComment, String asInternalComment, ActionRequest aoRequest)
			throws ApplicationException
	{
		HashMap loRequiredPropsForDocStatus = new HashMap();
		String lsUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateCommentsInTransactionTable() with parameters::"
				+ asTaskName + "," + asAppId + "," + asSectionId + "," + asPublicComment + "," + asPublicComment);
		loRequiredPropsForDocStatus.put("taskName", asTaskName);
		loRequiredPropsForDocStatus.put("taskType", asTaskType);
		loRequiredPropsForDocStatus.put("brApplicationId", asAppId);
		loRequiredPropsForDocStatus.put("sectionId", asSectionId);
		loRequiredPropsForDocStatus.put("publicComment", asPublicComment);
		loRequiredPropsForDocStatus.put("internalComment", asInternalComment);
		loRequiredPropsForDocStatus.put("modifiedBy", lsUserId);
		loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, (String) aoRequest.getParameter(HHSR5Constants.TASK_ID),
				HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		TransactionManager.executeTransaction(loChannel, "updateCommentsInTransactionTable");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateCommentsInTransactionTable()");
	}

	/**
	 * Method to Update the Status and Comments of Different Sub Sections &
	 * Documents by using "updateProcSubSectionStatus_DB" and
	 * "documentUpdate_DB" Transaction
	 * 
	 * @param aoRequest ActionRequest
	 * @param asTaskName Name Of the Task
	 * @param asProviderName Name Of the Provider
	 * @param asSectionId Section Id
	 * @param asApplicationId Application Id
	 * @param asTaskType Type of the Task
	 * @param asUserEmailId User Email Id
	 * @param asWFEntityID WFEntity ID
	 * @throws ApplicationException
	 */
	private void updateDocStatusAndComments(ActionRequest aoRequest, String asTaskName, String asProviderName,
			String asSectionId, String asApplicationId, String asTaskType, String asUserEmailId, String asWFEntityID)
			throws ApplicationException
	{
		Date loCurrentDate = new Date();
		HashMap loRequiredPropsForDocStatus = new HashMap();
		String lsSessionUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		Date loDate = new Date();
		String lsEntityType = "";
		String lsEntityId = "";
		String lsEventName = "";
		String lsAuditType;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateDocStatusAndComments() with parameters::" + asTaskName
				+ "," + asProviderName + "," + asSectionId + "," + asApplicationId + "," + asUserEmailId + ","
				+ asWFEntityID);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
		{
			lsAuditType = ApplicationConstants.AUDIT_TYPE_GENERAL;
		}
		else
		{
			lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;
		}
		updateComments(aoRequest, asTaskName, asProviderName, asSectionId, asApplicationId, asTaskType, asUserEmailId,
				false, asWFEntityID);
		String[] lsDocStatus = aoRequest.getParameterValues("assignedstatus");
		if (lsDocStatus != null)
		{
			for (int liCounter = 0; liCounter < lsDocStatus.length; liCounter++)
			{
				if (lsDocStatus[liCounter] != null && !lsDocStatus[liCounter].isEmpty())
				{
					String lsDelimiter = "_";
					String[] loBreakStr;
					loBreakStr = lsDocStatus[liCounter].split(lsDelimiter);
					if (loBreakStr.length < 3)
					{
						break;
					}
					String lsSectionNameAfterSplit = loBreakStr[0];
					String lsStatusAfterSplit = loBreakStr[1];
					String lsPrevCurrentStatus = loBreakStr[2];
					String lsDocType = null;
					String lsDocNameForAudit = "";
					String lsData = "Status Changed from '" + lsPrevCurrentStatus + "' To '" + lsStatusAfterSplit + "'";
					if (loBreakStr.length >= 4)
					{
						lsDocType = loBreakStr[3];
						lsDocNameForAudit = loBreakStr[4];
					}
					loRequiredPropsForDocStatus.put("sectionStatus", lsStatusAfterSplit);
					loRequiredPropsForDocStatus.put("modifiedBy", lsSessionUserId);
					loRequiredPropsForDocStatus.put("modifiedDate", loCurrentDate);
					loRequiredPropsForDocStatus.put("sectionId", asSectionId);
					loRequiredPropsForDocStatus.put("applicationId", asApplicationId);
					Channel loChannel = new Channel();
					loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
					if (lsSectionNameAfterSplit.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_GEOGRAPHY)
							|| lsSectionNameAfterSplit
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_LANGUAGES)
							|| lsSectionNameAfterSplit
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_POPULATIONS)
							|| lsSectionNameAfterSplit.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_QUESTION)
							|| lsSectionNameAfterSplit
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_POLICIES_QUESTION)
							|| lsSectionNameAfterSplit
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_BOARD_QUESTION)
							|| lsSectionNameAfterSplit
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_BR_SUBSECTION_FILINGS_QUESTION))
					{
						updateDocStatusIfCondition(asProviderName, asSectionId, asApplicationId, asUserEmailId,
								loRequiredPropsForDocStatus, loDate, lsAuditType, lsSectionNameAfterSplit,
								lsStatusAfterSplit, lsPrevCurrentStatus, lsData, loChannel, asTaskName);
					}
					else
					{
						loRequiredPropsForDocStatus.put("documentId", lsSectionNameAfterSplit);
						loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
						if (lsDocType == null)
						{
							lsDocType = "";
						}
						lsEntityType = ApplicationConstants.ENTITY_TYPE_DOCUMENT;
						String lsEntityIdentifier = asTaskName + ":" + lsDocNameForAudit;
						lsEntityId = lsSectionNameAfterSplit;
						lsEventName = ApplicationConstants.EVENT_NAME_DOCUMENT;
						loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
						CommonUtil.addAuditDataToChannel(loChannel, asProviderName, lsEventName,
								P8Constants.EVENT_TYPE_WORKFLOW, loDate, asUserEmailId, lsData, lsEntityType,
								lsEntityId, "false", asApplicationId, asSectionId, lsAuditType);
						loChannel.setData("EntityIdentifier", lsEntityIdentifier);
						if (!lsPrevCurrentStatus.equalsIgnoreCase(lsStatusAfterSplit))
						{
							TransactionManager.executeTransaction(loChannel, "documentUpdate_DB");

						}
					}
				}
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateDocStatusAndComments()");
	}

	/**
	 * To Assign Task To a current User & internally Calling assignTask() method
	 * and depending on the result "ServicetaskUpdate_DB" transaction got
	 * executed
	 * <ul>
	 * <li>Execute Transaction id <b> ServicetaskUpdate_DB</b></li>
	 * </ul>
	 * @param aoRequest ActionRequest
	 * @param aoUserSession Filenet Session
	 * @param asTaskId WorkFlow ID
	 * @param aoSession Session
	 * @throws ApplicationException
	 */
	private void reserveTaskToCurrentUser(ActionRequest aoRequest, P8UserSession aoUserSession, String asTaskId,
			PortletSession aoSession) throws ApplicationException
	{
		String lsReserveWobNo = aoRequest.getParameter("reserveId");
		String lsSessionUserID = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		String lsSessionUserName = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserName");
		String lsEmail = (String) ApplicationSession.getAttribute(aoRequest, true, "EmailAddress");
		
		
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoUserSession);
		LOG_OBJECT.Debug("Entered WorkfloDetailController.reserveTaskToCurrentUser() with parameters::" + param
				+ "," + asTaskId);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		if (lsReserveWobNo != null)
		{
			ArrayList<String> loWobList = new ArrayList<String>();
			loWobList.add(lsReserveWobNo);
			Channel loChannel = new Channel();
			loChannel.setData("aoFilenetSession", aoUserSession);
			HashMap lbReserveResult = assignTask(loChannel, loWobList, lsSessionUserID, lsSessionUserName, lsEmail);

			if (lbReserveResult != null && !lbReserveResult.isEmpty())
			{

				Iterator loIt = lbReserveResult.keySet().iterator();
				while (loIt.hasNext())
				{
					String lsWobno = (String) loIt.next();
					HashMap loDetailsMap = (HashMap) lbReserveResult.get(lsWobno);
					String lsIsChildTaskLaunched = (String) loDetailsMap.get("isChildTaskLaunched");
					if (lsIsChildTaskLaunched.equalsIgnoreCase("true"))
					{

						loDetailsMap.remove("isChildTaskLaunched");
						Iterator loIt2 = loDetailsMap.keySet().iterator();
						while (loIt2.hasNext())
						{
							String lsSecId = (String) loIt2.next();
							String lsSecWobno = (String) loDetailsMap.get(lsSecId);
							loChannel = new Channel();
							HashMap loRequiredPropsForDocStatus = new HashMap();
							loRequiredPropsForDocStatus.put("sectionWobNo", lsSecWobno);
							loRequiredPropsForDocStatus.put("sectionId", lsSecId);
							loRequiredPropsForDocStatus.put("modifiedBy", lsSessionUserID);
							loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
							TransactionManager.executeTransaction(loChannel, "ServicetaskUpdate_DB");
						}
					}
				}

			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.reserveTaskToCurrentUser()");
	}

	/**
	 * To Assign Multiple Task To selected User & internally Calling
	 * assignTask() method and depending on the result "ServicetaskUpdate_DB"
	 * transaction got executed
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse Response
	 * @param aoUserSession Filenet Session
	 * @param asTaskId WorkFlow ID
	 * @param aoSession Session
	 * @throws ApplicationException
	 */
	private void assignTasksToUser(ActionRequest aoRequest, ActionResponse aoResponse, P8UserSession aoUserSession,
			String asTaskId, PortletSession aoSession) throws ApplicationException
	{
		boolean loIsInvalid;
		String lsReassignTaskFromDetails = aoRequest.getParameter("reassignTask");
		String lsReassignedUserName = aoRequest.getParameter("reassigntouserText");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		//*** Start QC 9585 R 8.9 do not expose password for service account in logs
		String param = CommonUtil.maskPassword(aoUserSession);
		LOG_OBJECT.Debug("Entered WorkfloDetailController.assignTasksToUser() with parameters::" + param + ","
				+ asTaskId);
		//*** End QC 9585 R 8.9 do not expose password for service account in logs

		
		loIsInvalid = validateAtServer(lsReassignedUserName, "assign");
		if (loIsInvalid)
		{
			String lsEmail = (String) ApplicationSession.getAttribute(aoRequest, true, "EmailAddress");
			String lsSessionUserName = "";
			HashMap loUserMap = (HashMap) aoSession.getAttribute("UserMap", PortletSession.APPLICATION_SCOPE);
			Collection loCol = loUserMap.keySet();
			Iterator loItr = loCol.iterator();
			String lsHashMapKey = "";
			while (loItr.hasNext())
			{
				lsHashMapKey = (String) loItr.next();
				String lsKey = lsHashMapKey.substring(lsHashMapKey.indexOf("|") + 1, lsHashMapKey.length());
				if (lsKey.equalsIgnoreCase(lsReassignedUserName))
				{
					lsSessionUserName = (String) loUserMap.get(lsHashMapKey);
					break;
				}
			}
			String[] loReassign = aoRequest.getParameterValues("check");
			if (null != lsReassignTaskFromDetails
					&& !lsReassignTaskFromDetails.isEmpty()
					&& (lsReassignTaskFromDetails.equalsIgnoreCase("reassignParentTask") || lsReassignTaskFromDetails
							.equalsIgnoreCase("reassignChildTask")))
			{
				loReassign = new String[1];
				loReassign[0] = asTaskId;
				asTaskId = null;
			}
			if (loReassign != null && !lsReassignedUserName.isEmpty())
			{
				String lsDelimiter = "_";
				String[] loWobNoForReassign = new String[loReassign.length];
				for (int liA = 0; liA < loReassign.length; liA++)
				{
					String[] loBreakStr;
					loBreakStr = loReassign[liA].split(lsDelimiter);
					loWobNoForReassign[liA] = loBreakStr[0];
				}
				// Reassign functionality
				ArrayList<String> loWobArray = new ArrayList(Arrays.asList(loWobNoForReassign));
				Channel loChannel = new Channel();
				loChannel.setData("aoFilenetSession", aoUserSession);
				HashMap loReserveResult = null;
				loReserveResult = assignTask(loChannel, loWobArray, lsReassignedUserName, lsSessionUserName, lsEmail);
				if (loReserveResult != null && !loReserveResult.isEmpty())
				{
					Iterator loIt = loReserveResult.keySet().iterator();
					while (loIt.hasNext())
					{
						String lsWobno = (String) loIt.next();
						HashMap loDetailsMap = (HashMap) loReserveResult.get(lsWobno);
						String lsIsChildTaskLaunched = (String) loDetailsMap.get("isChildTaskLaunched");
						if (lsIsChildTaskLaunched.equalsIgnoreCase("true"))
						{
							loDetailsMap.remove("isChildTaskLaunched");
							Iterator loIt2 = loDetailsMap.keySet().iterator();
							while (loIt2.hasNext())
							{
								String lsSecId = (String) loIt2.next();
								String lsSecWobno = (String) loDetailsMap.get(lsSecId);
								loChannel = new Channel();
								HashMap loRequiredPropsForDocStatus = new HashMap();
								loRequiredPropsForDocStatus.put("sectionWobNo", lsSecWobno);
								loRequiredPropsForDocStatus.put("sectionId", lsSecId);
								loRequiredPropsForDocStatus.put("modifiedBy", lsUserId);
								loChannel.setData("aoRequiredProps", loRequiredPropsForDocStatus);
								TransactionManager.executeTransaction(loChannel, "ServicetaskUpdate_DB");

							}
						}

					}
				}
			}
		}
		else
		{
			aoResponse.setRenderParameter("message", ApplicationConstants.ACTION_NOT_AUTHORIZED);
			aoResponse.setRenderParameter("messagetype", "failed");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.assignTasksToUser()");
	}

	/**
	 * To check user is valid or not by using "isValidUser_filenet" transaction
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asWobNo WorkFlow Number
	 * @param asCurrentUserName Current Logged In User Name
	 * @return Boolean lbIsValid
	 * @throws ApplicationException
	 */
	private boolean isValidUser(P8UserSession aoUserSession, String asWobNo, String asCurrentUserName)
			throws ApplicationException
	{
		boolean lbIsValid = true;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.isValidUser() with parameters::" + asWobNo + ","
				+ asCurrentUserName);
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asWobNumber", asWobNo);
		loChannel.setData("asCurrentUserName", asCurrentUserName);

		TransactionManager.executeTransaction(loChannel, "isValidUser_filenet");
		lbIsValid = (Boolean) loChannel.getData("resultValidUser");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.isValidUser");
		return lbIsValid;

	}

	/**
	 * Method to Finish all WorkFlow other then BR Parent WF by using
	 * "finishchildtask_filenet" transaction
	 * 
	 * @param aoRequest ActionRequest
	 * @param asChildTaskStatus Child Task Status
	 * @param asChildWobNo WorkFlow Number
	 * @param aoUserSession Filenet Session
	 * @param asIsManagerTask Weather is a manager task
	 * @param asSessionUserName User Name
	 * @param asAppId Application Id
	 * @param asSectionId Section ID
	 * @param asTaskName Name of the Task
	 * @param asProviderId Provider ID
	 * @param aoBean OrgNameChangeBean
	 * @param asTaskType Type Of the Task
	 * @param asProviderName ProviderName
	 * @param asUserEmailId User Email Id
	 * @return loFinishStatus
	 * @throws ApplicationException
	 */
	private HashMap finishChildTask(ActionRequest aoRequest, String asChildTaskStatus, String asChildWobNo,
			P8UserSession aoUserSession, String asIsManagerTask, String asSessionUserName, String asAppId,
			String asSectionId, String asTaskName, String asProviderId, OrgNameChangeBean aoBean, String asTaskType,
			String asProviderName, String asUserEmailId) throws ApplicationException
	{
		String lsSessionUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		String lsEventName = ApplicationConstants.EVENT_NAME_FINSH_STATUS;
		String lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + asChildTaskStatus;
		String lsEntityType = ApplicationConstants.ENTITY_TYPE_STATUS_CHANGE;
		String lsEntityIdentifier = asTaskName;
		String lsEntityId = asTaskType;
		Channel loChannel = new Channel();
		HashMap loHMSection = new HashMap();
		String lsProviderFlag = "true";
		Date loDate = new Date();
		String lsAuditType;
		String lsFailureMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M121");
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishChildTask with parameters::" + asChildTaskStatus + ","
				+ asChildWobNo + "," + asAppId + "," + asSectionId + "," + asProviderId + "," + asTaskType + ","
				+ asUserEmailId);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)
				|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
		{
			lsAuditType = ApplicationConstants.AUDIT_TYPE_GENERAL;
			lsEntityType = asTaskType;
			lsEntityId = asAppId;
		}
		else
		{
			lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;
		}
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("asWobNumber", asChildWobNo);
		loChannel.setData("asChildStatus", asChildTaskStatus);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			if (!(asIsManagerTask.equalsIgnoreCase("true") || asChildTaskStatus
					.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
			{
				lsProviderFlag = "false";
			}
		}
		CommonUtil
				.addAuditDataToChannel(loChannel, asProviderId, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW, loDate,
						asUserEmailId, lsAuditData, lsEntityType, lsEntityId, lsProviderFlag, asAppId, asSectionId,
						lsAuditType);
		loChannel.setData("EntityIdentifier", lsEntityIdentifier);
		HashMap loFinishStatus = new HashMap();
		int liContactUsId = 0;
		List loPropertyKey = new ArrayList();
		List loProIdAsList = new ArrayList();
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION)
				&& !asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			TransactionManager.executeTransaction(loChannel, "CheckServiceTaskStatus_db");
			String lsStatusResult = (String) loChannel.getData("resultServiceStatus");
			if (null != lsStatusResult && lsStatusResult.equalsIgnoreCase(ApplicationConstants.DEACTIVATED))
			{
				ApplicationSession.setAttribute(lsFailureMsg, aoRequest, "message");
				ApplicationSession.setAttribute("failed", aoRequest, "messagetype");
			}
			else
			{
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, asChildWobNo, HHSR5Constants.TASKS);
				// End R5 : set EntityId and EntityName for AutoSave

				TransactionManager.executeTransaction(loChannel, "finishchildtask_filenet");
				loFinishStatus = (HashMap) loChannel.getData("resultchildtaskfinish");
			}
		}
		else
		{
			// Start R5 : set EntityId and EntityName for AutoSave
			CommonUtil.setChannelForAutoSaveData(loChannel, asChildWobNo, HHSR5Constants.TASKS);
			// End R5 : set EntityId and EntityName for AutoSave

			TransactionManager.executeTransaction(loChannel, "finishchildtask_filenet");
			loFinishStatus = (HashMap) loChannel.getData("resultchildtaskfinish");
		}

		if (loFinishStatus != null && !loFinishStatus.isEmpty())
		{
			String lsTaskType = (String) loFinishStatus.get(P8Constants.PROPERTY_PE_TASK_TYPE);
			HashMap loHMReturnFromWorkflow = (HashMap) loFinishStatus.get("ReturnedItems");
			updateTaskAfterFinish(aoRequest, lsTaskType, asChildTaskStatus, lsSessionUserId, asSectionId, asAppId,
					loPropertyKey, asIsManagerTask, asProviderId, asProviderName, asTaskName, loProIdAsList,
					liContactUsId, loChannel, loHMSection, loHMReturnFromWorkflow, aoBean);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishChildTask()");
		return loFinishStatus;
	}

	/**
	 * Method to Terminate the Withdrawal task by using "terminateSCWithdrawl"
	 * transaction
	 * 
	 * @param aoUserSession Filenet Session
	 * @param aoRequest Request
	 * @param aoResponse Response
	 * @param asTaskType Type Of the Task
	 * @param asSectionId Section ID
	 * @param asFinishStatus Finish Status
	 * @param asAppId Application ID
	 * @param asTaskName Name of the Task
	 * @param asSessionUserName User Name
	 * @param asProviderId Provider ID
	 * @param asUserId UserId
	 * @param asEntityID EntityID
	 * @param asUserEmailId User EmailId
	 * @param asProviderName Provider Name
	 * @return lbTerminateStatus
	 * @throws ApplicationException
	 */
	private boolean terminateWithdrawlTask(ActionRequest aoRequest, ActionResponse aoResponse,
			P8UserSession aoUserSession, String asTaskType, String asSectionId, String asFinishStatus, String asAppId,
			String asTaskName, String asSessionUserName, String asProviderId, String asEntityID, String asUserId,
			String asProviderName, String asUserEmailId) throws ApplicationException
	{
		boolean lbTerminateStatus = false;
		Channel loChannel = new Channel();
		List loPropertyKey = new ArrayList();
		List loProIdAsList = new ArrayList();
		String lsEventName = ApplicationConstants.EVENT_NAME_FINSH_STATUS;
		String lsAuditData = "";
		LOG_OBJECT.Debug("Entered WorkfloDetailController.terminateWithdrawlTask() with parameters::" + asTaskType
				+ "," + asSectionId + "," + asAppId + "," + asEntityID + "," + asProviderId + "," + asTaskType + ","
				+ asUserEmailId);
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + ApplicationConstants.STATUS_WITHDRAWN;
		}
		else
		{
			lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + asFinishStatus;
		}
		String lsEntityType = asTaskName;
		String lsEntityIdentifier = asTaskName;
		Date loDate = new Date();
		String lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;

		CommonUtil.addAuditDataToChannel(loChannel, asProviderId, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW, loDate,
				asUserEmailId, lsAuditData, lsEntityType, asEntityID, "true", asAppId, asSectionId, lsAuditType);
		loChannel.setData("EntityIdentifier", lsEntityIdentifier);
		updateComments(aoRequest, asTaskName, asProviderId, asSectionId, asAppId, asTaskType, asUserEmailId, false,
				asEntityID);
		ApplicationSession.setAttribute(null, aoRequest, "message");
		ApplicationSession.setAttribute(null, aoRequest, "messagetype");
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
		{
			finishWithdrawlServiceApplication(aoRequest, loChannel, aoUserSession, asEntityID, asFinishStatus,
					asUserId, asProviderId, asProviderName, asAppId, loPropertyKey, asSectionId, loProIdAsList,
					asTaskName);
		}
		else if (asTaskType
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
		{
			finishWithdrawlBusinessApplication(aoRequest, loChannel, aoUserSession, asEntityID, asFinishStatus,
					asUserId, asProviderId, asProviderName, asAppId, loPropertyKey, asSectionId, loProIdAsList,
					asTaskName, asUserEmailId);
		}
		updateAuditFlag(asAppId, asEntityID, P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST);

		LOG_OBJECT.Debug("Exit WorkfloDetailController.terminateWithdrawlTask()");
		return lbTerminateStatus;
	}

	/**
	 * Method to finish the BR Parent Task by "finishparenttask_filenet"
	 * transaction.
	 * 
	 * @param aoRequest ActionRequest
	 * @param asParentWobNo WorkFlow Number
	 * @param aoUserSession Filenet Session
	 * @param asIsManagerTask Weather Is a Manager Task
	 * @param asSessionUserName User Name
	 * @param asAppId Application ID
	 * @param asSectionId Section Id
	 * @param asProviderId Provider ID
	 * @param asTaskName Name of the Task
	 * @param asTaskType Type of the Task
	 * @param aoTaskBean Task Bean
	 * @param asProviderName Provider Name
	 * @param asUserEmailId User EmailId
	 * @return lsFinishStatus string
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private String finishParentTask(ActionRequest aoRequest, String asParentWobNo, P8UserSession aoUserSession,
			String asIsManagerTask, String asSessionUserName, String asAppId, String asSectionId, String asProviderId,
			String asTaskName, String asTaskType, Task aoTaskBean, String asProviderName, String asUserEmailId)
			throws ApplicationException
	{
		String asUserID = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		String lsEventName = ApplicationConstants.EVENT_NAME_FINSH_STATUS;
		String lsSessionUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		String lsAuditData = "";
		String lsEntityType = ApplicationConstants.ENTITY_TYPE_BR_APP;
		String lsEntityIdentifier = ApplicationConstants.ENTITY_TYPE_BR_APP;
		String lsEntityId = asAppId;
		String lsChildTaskStatus = "";
		String lsSecId = "";
		String lsFinishStatus = "";
		Date loDate = new Date();
		String lsFlag = "false";
		String lsSnapshotData = "";
		List loPropertyKey = new ArrayList();
		List loProIdAsList = new ArrayList();
		HashMap loHMProcApp = new HashMap();
		StringBuffer lsSectionRFR = new StringBuffer();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishParentTask() with parameters::" + asTaskType + ","
				+ asSectionId + "," + asAppId + "," + asProviderId + "," + asTaskType + "," + asUserEmailId);
		loHMProcApp.put("modifiedBy", asSessionUserName);
		loHMProcApp.put("modifiedDate", new Date());
		loHMProcApp.put("brApplicationId", asAppId);
		loHMProcApp.put("applicationId", asAppId);
		loHMProcApp.put("userId", asUserID);
		loChannel.setData("asWobNumber", asParentWobNo);
		loChannel.setData("aoFilenetSession", aoUserSession);
		loChannel.setData("aoRequiredProps", loHMProcApp);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(loChannel, asParentWobNo, HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave

		TransactionManager.executeTransaction(loChannel, "finishparenttask_filenet");
		lsFinishStatus = (String) loChannel.getData("resultparenttaskfinish");
		if (asIsManagerTask != null
				&& "true".equals(asIsManagerTask)
				|| (lsFinishStatus != null && lsFinishStatus.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
		{
			lsFlag = "true";
		}
		lsAuditData = "Status Changed to " + lsFinishStatus;
		CommonUtil.addAuditDataToChannel(loChannel, asProviderId, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW, loDate,
				asUserEmailId, lsAuditData, lsEntityType, lsEntityId, lsFlag, asAppId, "", "application");
		loChannel.setData("EntityIdentifier", lsEntityIdentifier);
		TransactionManager.executeTransaction(loChannel, "AuditInformation");
		if ((asIsManagerTask != null && asIsManagerTask.equalsIgnoreCase("true"))
				&& (lsFinishStatus != null && (lsFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsFinishStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))))
		{
			loChannel = new Channel();
			HashMap loReqProps = new HashMap();
			loReqProps.put("entityId", lsEntityId);
			loChannel.setData("IDprops", loReqProps);
			TransactionManager.executeTransaction(loChannel, "deleteSuperSeding_DB");
			insertInPrintView(asAppId, asProviderId, asTaskType, lsFinishStatus, lsSessionUserId);
		}
		HashMap loHMSectionMap = new HashMap();
		loHMSectionMap.put("applicationId", asAppId);
		List<SectionBean> loSectionList = getSectionDetails(loHMSectionMap);
		Iterator loIter = loSectionList.iterator();
		SectionBean loBean = null;
		if (asIsManagerTask != null
				&& "true".equals(asIsManagerTask)
				|| (lsFinishStatus != null && lsFinishStatus.equals(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
		{
			while (loIter.hasNext())
			{
				loBean = (SectionBean) loIter.next();
				lsChildTaskStatus = loBean.getProcSectionStatus();
				lsSecId = loBean.getSectionId();
				HashMap loHMSubSectionStatusUpdate = new HashMap();
				loHMSubSectionStatusUpdate.put("sectionId", lsSecId);
				loHMSubSectionStatusUpdate.put("applicationId", asAppId);
				HashMap loHMStatusUpdate = new HashMap();
				subSectionStatusUpdate(loHMSubSectionStatusUpdate);
				loHMStatusUpdate.put("sectionStatus", lsChildTaskStatus);
				loHMStatusUpdate.put("modifiedBy", lsSessionUserId);
				loHMStatusUpdate.put("modifiedDate", new Date());
				loHMStatusUpdate.put("sectionId", lsSecId);
				loHMStatusUpdate.put("applicationId", asAppId);
				sectionUpdate(loHMStatusUpdate);
				updateAuditFlag(asAppId, lsSecId, P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
				if (lsChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				{
					if (lsSecId != null && !lsSecId.isEmpty() && lsSecId.length() > 2)
					{
						lsSecId = Character.toUpperCase(lsSecId.charAt(0)) + lsSecId.substring(1);
					}
					lsSectionRFR.append(lsSecId);
					lsSectionRFR.append("<br/>");
				}
			}
		}
		if ((asIsManagerTask != null && asIsManagerTask.equalsIgnoreCase("true"))
				|| (lsFinishStatus != null && lsFinishStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
		{
			updateDbAfterFinishingParentTask(aoRequest, aoUserSession, asAppId, asSectionId, lsFinishStatus,
					lsSessionUserId, asProviderId, asProviderName, asSessionUserName, loPropertyKey, lsSectionRFR,
					loProIdAsList, asTaskName);
			lsSnapshotData = BusinessApplicationUtil.convertHistoryToXML(asAppId);
			CommonUtil.addAuditDataToChannel(loChannel, asProviderId, "Snapshot", "Snapshot", loDate, asUserEmailId,
					lsSnapshotData, lsEntityType, lsEntityId, "true", asAppId, asSectionId, "application");
			loChannel.setData("EntityIdentifier", lsEntityIdentifier);
			TransactionManager.executeTransaction(loChannel, "AuditInformation");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishParentTask()");
		return lsFinishStatus;
	}

	/**
	 * Calculating & Inserting the Due Date in the Table by
	 * "getCHAR500docFromDocument_DB" Transaction.
	 * 
	 * @param aoHMReqdProp HashMap Of Required Props
	 * @param asProviderOrg Constant Provider ORg
	 * @param asProviderId Provider ID
	 * @param aoUserSession Filenet Session
	 * @param asSessionUserId Session UserId
	 * @throws ApplicationException
	 */
	private void addEntryforCHAR500Docs(HashMap aoHMReqdProp, String asProviderOrg, String asProviderId,
			P8UserSession aoUserSession, String asSessionUserId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		Document loDocument = null;
		String lsDocumentId = "";
		LOG_OBJECT.Debug("Entered WorkfloDetailController.addEntryforCHAR500Docs() with parameters::" + asProviderOrg
				+ "," + asProviderId + "," + asSessionUserId);
		aoHMReqdProp.put("documentCategory", ApplicationConstants.DOC_TYPE_CORPORATE_STRUCTURE);
		aoHMReqdProp.put("orgId", asProviderId);
		loChannel.setData("aoHMSection", aoHMReqdProp);
		TransactionManager.executeTransaction(loChannel, "getCHAR500docFromDocument_DB");
		List<String> loDocumentIds = (List) loChannel.getData("documentIds");
		Iterator<String> loListItr = loDocumentIds.iterator();
		if (loListItr.hasNext())
		{
			lsDocumentId = (String) loListItr.next();
			// Added extra parameters in Release 5
			loDocument = FileNetOperationsUtils.viewDocumentInfo(aoUserSession, asProviderOrg, lsDocumentId,
					P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, asProviderId,
					ApplicationConstants.DOC_TYPE_CORPORATE_STRUCTURE,null);
			List loDocPropsList = loDocument.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocPropsList.iterator();
			String lsPeriodStartMonth = "";
			String lsPeriodEndMonth = "";
			int lsPeriodStartYear = 0;
			int lsPeriodEndYear = 0;
			while (loDocPropsIt.hasNext())
			{
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				if ("PERIOD_COVER_FROM_MONTH".equalsIgnoreCase(loDocProps.getPropSymbolicName()))
				{
					lsPeriodStartMonth = (String) loDocProps.getPropValue();
				}
				else if ("PERIOD_COVER_TO_MONTH".equalsIgnoreCase(loDocProps.getPropSymbolicName()))
				{
					lsPeriodEndMonth = (String) loDocProps.getPropValue();
				}
				else if ("PERIOD_COVER_FROM_YEAR".equalsIgnoreCase(loDocProps.getPropSymbolicName()))
				{
					lsPeriodStartYear = Integer.valueOf(String.valueOf(loDocProps.getPropValue()));
				}
				else if ("PERIOD_COVER_TO_YEAR".equalsIgnoreCase(loDocProps.getPropSymbolicName()))
				{
					lsPeriodEndYear = Integer.valueOf(String.valueOf(loDocProps.getPropValue()));
				}
			}
			loChannel.setData("asOrgId", asProviderId);
			TransactionManager.executeTransaction(loChannel, "getLawType_DB");
			String lsLawType = (String) loChannel.getData("asLawType");
			if (null != lsLawType && !("".equalsIgnoreCase(lsLawType)))
			{
				Map loDocLapsingMasterMap = DocumentLapsingUtility.calculateDueDateonDocumentUpload(
						loDocument.getDocType(), lsPeriodStartMonth, lsPeriodStartYear, lsPeriodEndMonth,
						lsPeriodEndYear, lsLawType, false);
				loDocLapsingMasterMap.put("providerId", asProviderId);
				loDocLapsingMasterMap.put("approvedForStartYear", lsPeriodStartYear);
				loDocLapsingMasterMap.put("approvedForEndYear", lsPeriodEndYear);
				loDocLapsingMasterMap.put("approvedForStartMonth", lsPeriodStartMonth);
				loDocLapsingMasterMap.put("approvedForEndMonth", lsPeriodEndMonth);
				HashMap loReqdProp = (HashMap) loDocLapsingMasterMap;
				loReqdProp.put("dueDate", loDocLapsingMasterMap.get("sqlDueDate"));
				loReqdProp.put("documentId", lsDocumentId);
				loReqdProp.put("submittedDate", new Date());
				loReqdProp.put("procStatus", ApplicationConstants.STATUS_APPROVED);
				loReqdProp.put("modifiedBy", asSessionUserId);
				loReqdProp.put("modifiedDate", new Date());
				loChannel.setData("loDocLapsingMasterMap", loReqdProp);
				TransactionManager.executeTransaction(loChannel, "insertDocLapsingMaster");
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.addEntryforCHAR500Docs()");
	}

	/**
	 * Mehtod to Update the Date with Time for futher passing to filenet
	 * 
	 * @param asDate Date
	 * @param asClause Constant
	 * @return String
	 */
	private String updateTimeWithDate(String asDate, String asClause)
	{
		DateFormat loFormatter = new SimpleDateFormat("MM/dd/yyyy");
		Date loDateToBeModified = null;
		long loDatefrm = 0;
		try
		{
			loDateToBeModified = (Date) loFormatter.parse(asDate);
			if ("to".equalsIgnoreCase(asClause))
			{
				loDatefrm = (loDateToBeModified.getTime() / 1000) + 86400;
			}
			else
			{
				loDatefrm = loDateToBeModified.getTime() / 1000;
			}

		}
		catch (ParseException aoExp)
		{

			LOG_OBJECT.Error("Error occured while updating time with date", aoExp);
		}
		return String.valueOf(loDatefrm);
	}

	/**
	 * Method is used to get All Task History Details for a Task from
	 * APPLICATION_AUDIT table by using "taskHistoryShow_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @return loNewTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> getTaskHistoryDetails(HashMap aoRequiredProps) throws ApplicationException
	{
		List<ApplicationAuditBean> loTaskHistoryList = null;
		List<ApplicationAuditBean> loNewTaskHistoryList = new ArrayList<ApplicationAuditBean>();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getTaskHistoryDetails() with parameters::" + aoRequiredProps);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "taskHistoryShow_DB");
		loTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("taskHistoryList");

		Iterator<ApplicationAuditBean> loListItr = loTaskHistoryList.iterator();
		while (loListItr.hasNext())
		{
			ApplicationAuditBean loBean = loListItr.next();
			loBean.setMsUserid(loBean.getMsUserid());
			loNewTaskHistoryList.add(loBean);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getTaskHistoryDetails()");
		return loNewTaskHistoryList;
	}

	/**
	 * Method is used to get All Task History Details for a Task from
	 * GENERAL_AUDIT table by using "taskHistoryShowGeneral_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @return loNewTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> getTaskHistoryDetailsGeneral(HashMap aoRequiredProps)
			throws ApplicationException
	{
		List<ApplicationAuditBean> loTaskHistoryList = null;
		List<ApplicationAuditBean> loNewTaskHistoryList = new ArrayList<ApplicationAuditBean>();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getTaskHistoryDetailsGeneral() with parameters::"
				+ aoRequiredProps);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "taskHistoryShowGeneral_DB");
		loTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("taskHistoryListGeneral");
		if (null != loTaskHistoryList)
		{
			Iterator<ApplicationAuditBean> loListItr = loTaskHistoryList.iterator();
			while (loListItr.hasNext())
			{

				ApplicationAuditBean loBean = loListItr.next();
				loBean.setMsUserid(loBean.getMsUserid());
				loNewTaskHistoryList.add(loBean);

			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getTaskHistoryDetailsGeneral()");
		return loNewTaskHistoryList;

	}

	/**
	 * This method is used to update process status in SECTION table for child
	 * task by using "processSectionUpdate_DB" transaction
	 * <ul>
	 * <li>Execute transaction <b> processSectionUpdate_DB</b></li>
	 * </ul>
	 * @param aoRequiredPropsForDocStatus HashMap Of Required Props
	 * @return lbUpdateStatus boolean
	 * @throws ApplicationException
	 */
	private Boolean processSectionUpdate(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.processSectionUpdate() with parameters::"
				+ aoRequiredPropsForDocStatus);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "processSectionUpdate_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.processSectionUpdate()");
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update status in SECTION table for child task by
	 * using "sectionUpdate_DB" transaction
	 * 
	 * @param aoRequiredPropsForDocStatus HashMap Of Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean sectionUpdate(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.sectionUpdate() with parameters::"
				+ aoRequiredPropsForDocStatus);
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "sectionUpdate_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.sectionUpdate()");
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update status in SUB_SECTION_SUMMARY table for
	 * child task by using transactions "updateDocWithProcStatus_DB" and
	 * "updateSubSectionStatus_DB".
	 * 
	 * @param aoRequiredPropsForDocStatus HashMap Of Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean subSectionStatusUpdate(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.subSectionStatusUpdate() with parameters::"
				+ aoRequiredPropsForDocStatus);
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "updateDocWithProcStatus_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		TransactionManager.executeTransaction(loChannel, "updateSubSectionStatus_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.subSectionStatusUpdate()");
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update process status in BUSINESS_APPLICATION
	 * table for parent task by using "updateBRProcAppStatus_DB" transaction
	 * 
	 * @param aoRequiredPropsForDocStatus HashMap Of Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean updateBRProcAppStatus(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateBRProcAppStatus() with parameters::"
				+ aoRequiredPropsForDocStatus);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "updateBRProcAppStatus_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateBRProcAppStatus()");
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update process status in BUSINESS_APPLICATION
	 * table for parent task by using "updateBRApplicationStatus_DB" transaction
	 * 
	 * @param aoRequiredPropsForDocStatus HashMap Of Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean updateBRApplicationStatus(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateBRApplicationStatus() with parameters::"
				+ aoRequiredPropsForDocStatus);
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "updateBRApplicationStatus_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateBRApplicationStatus()");
		return lbUpdateStatus;
	}

	/**
	 * This Method is used to fetch Business Application Status.
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @return lsStatus
	 * @throws ApplicationException
	 */
	private String getBRAppStatus(HashMap aoRequiredProps) throws ApplicationException
	{
		String lsStatus = "";
		List<SectionBean> loBRAppStatus = null;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getBRAppStatus() with parameters::" + aoRequiredProps);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "fetchBRProcAppStatus_DB");
		loBRAppStatus = (List<SectionBean>) loChannel.getData("processStatus");
		Iterator<SectionBean> loListItr = loBRAppStatus.iterator();
		if (loListItr.hasNext())
		{
			SectionBean loBean = loListItr.next();
			lsStatus = loBean.getApplicationStatus();
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getBRAppStatus()");
		return lsStatus;
	}

	/**
	 * Method to get the Last Provider Comments from Application_Audit Table by
	 * "fetchLastProviderComments_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @param asTaskType Task Type
	 * @return lsLastComment string
	 * @throws ApplicationException
	 */
	private String getLastProviderComment(HashMap aoRequiredProps, String asTaskType) throws ApplicationException
	{
		String lsLastComment = "";
		List<ApplicationAuditBean> loTaskHistoryList = null;
		LOG_OBJECT
				.Debug("Entered WorkfloDetailController.getLastProviderComment() with parameters::" + aoRequiredProps);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
		{
			TransactionManager.executeTransaction(loChannel, "fetchLastProviderCommentsWithdrawal_DB");
		}
		else
		{
			TransactionManager.executeTransaction(loChannel, "fetchLastProviderComments_DB");
		}
		loTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("taskHistoryList");
		Iterator<ApplicationAuditBean> loListItr = loTaskHistoryList.iterator();
		if (loListItr.hasNext())
		{
			ApplicationAuditBean loBean = loListItr.next();
			if (null == loBean.getMsProviderFlag() || ((String) loBean.getMsProviderFlag()).equalsIgnoreCase("false"))
			{
				lsLastComment = (String) loBean.getMsData();
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getLastProviderComment()");
		return lsLastComment;
	}

	/**
	 * Method to get the Last Provider Comments from General_Audit Table by
	 * "fetchLastProviderCommentsGeneral_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @return lsLastComment
	 * @throws ApplicationException
	 */
	private String getLastProviderCommentGeneral(HashMap aoRequiredProps) throws ApplicationException
	{
		String lsLastComment = "";
		List<ApplicationAuditBean> loTaskHistoryList = null;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getLastProviderCommentGeneral() with parameters::"
				+ aoRequiredProps);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "fetchLastProviderCommentsGeneral_DB");
		loTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("lastProviderCommentGeneral");

		if (!loTaskHistoryList.isEmpty())
		{
			lsLastComment = loTaskHistoryList.get(0).getMsData();
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getLastProviderCommentGeneral()");
		return lsLastComment;
	}

	/**
	 * Method is used to get All Application Task History Details for a Task
	 * from APPLICATION_AUDIT table by using "taskApplicationHistoryShow_DB"
	 * transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @param asAppId Application ID
	 * @return loNewTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> getAppTaskHistoryDetails(HashMap aoRequiredProps, String asAppId)
			throws ApplicationException
	{
		List<ApplicationAuditBean> loTaskHistoryList = null;
		List<ApplicationAuditBean> loNewTaskHistoryList = new ArrayList<ApplicationAuditBean>();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getAppTaskHistoryDetails() with parameters::"
				+ aoRequiredProps);
		Channel loChannel = new Channel();
		aoRequiredProps.put("sectionID1", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
		aoRequiredProps.put("sectionID2", ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS);
		aoRequiredProps.put("sectionID3", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD);
		aoRequiredProps.put("sectionID4", ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES);
		aoRequiredProps.put("sectionID5", asAppId);
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "taskApplicationHistoryShow_DB");
		loTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("taskHistoryList");
		Iterator<ApplicationAuditBean> loListItr = loTaskHistoryList.iterator();
		while (loListItr.hasNext())
		{
			ApplicationAuditBean loBean = loListItr.next();
			loBean.setMsUserid(loBean.getMsUserid());
			loNewTaskHistoryList.add(loBean);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getAppTaskHistoryDetails()");
		return loNewTaskHistoryList;
	}

	/**
	 * Method to fetch the Section Details by using "sectionView_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap Of Required Props
	 * @return loSectionList
	 * @throws ApplicationException
	 */
	private List<SectionBean> getSectionDetails(HashMap aoRequiredProps) throws ApplicationException
	{
		List<SectionBean> loSectionList = null;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getSectionDetails() with parameters::" + aoRequiredProps);
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "sectionView_DB");
		loSectionList = (List<SectionBean>) loChannel.getData("sectionList");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getSectionDetails()");
		return loSectionList;
	}

	/**
	 * Method to create Filter Values
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param asCurrentTab Weather management or inbox tab
	 * @return loFilterDetails
	 * @throws ApplicationException
	 */
	private HashMap createFilter(ActionRequest aoRequest, ActionResponse aoResponse, String asCurrentTab)
			throws ApplicationException
	{
		HashMap loFilterDetails = new HashMap();
		String lsTaskType = aoRequest.getParameter("tasktype");
		String lsProviderName = aoRequest.getParameter("providername");
		/**** Begin QC 5446 ****/
		String lsProcurementTitle = aoRequest.getParameter(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE);
		String lsCompetitionPoolTitle = aoRequest.getParameter(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE);
		String lsAgencyName = aoRequest.getParameter(HHSConstants.AGENCY_NAME);
		/**** End QC 5446 ****/
		String lsStatus = aoRequest.getParameter("status");
		String lsSubmittedFrom = aoRequest.getParameter("datefrom");
		String lsSubmittedTo = aoRequest.getParameter("dateto");
		String lsDateAssignedFrom = aoRequest.getParameter("dateassignedfrom");
		String lsDateAssignedTo = aoRequest.getParameter("dateassignedto");
		String lsAssignedTo = aoRequest.getParameter("assignedto");
		String lsSessionUserName = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserName");
		LOG_OBJECT.Debug("Entered WorkfloDetailController.createFilter() with parameters::" + asCurrentTab);
		if (lsTaskType != null && lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS))
		{
			lsTaskType = P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS;
		}
		putFilterinSession(aoRequest, lsTaskType, lsProviderName, lsProcurementTitle, lsCompetitionPoolTitle,
				lsAgencyName, lsStatus, lsSubmittedFrom, lsSubmittedTo, lsDateAssignedFrom, lsDateAssignedTo,
				lsAssignedTo);
		if (null != asCurrentTab && !asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
		{
			loFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, lsSessionUserName);
		}
		if (lsTaskType != null && !lsTaskType.isEmpty())
		{
			loFilterDetails.put(P8Constants.PROPERTY_PE_TASK_TYPE, lsTaskType);
		}
		if (lsProviderName != null && !lsProviderName.isEmpty())
		{
			loFilterDetails.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, lsProviderName);
		}
		/**** Begin QC 5446 ****/
		if (lsProcurementTitle != null && !lsProcurementTitle.isEmpty())
		{
			loFilterDetails.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE, lsProcurementTitle);
		}
		if (lsCompetitionPoolTitle != null && !lsCompetitionPoolTitle.isEmpty())
		{
			loFilterDetails.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE, lsCompetitionPoolTitle);
		}
		if (lsAgencyName != null && !lsAgencyName.isEmpty())
		{
			loFilterDetails.put(HHSConstants.PROPERTY_PE_AGENCY_ID, lsAgencyName);
		}
		/**** End QC 5446 ****/
		if (lsStatus != null && !lsStatus.isEmpty())
		{
			loFilterDetails.put(P8Constants.PROPERTY_PE_TASK_STATUS, lsStatus);
		}
		if (lsSubmittedFrom != null && !lsSubmittedFrom.isEmpty())
		{
			String lsDateWithTime = updateTimeWithDate(lsSubmittedFrom, "from");
			loFilterDetails.put(P8Constants.PROPERTY_HMP_SUBMITTED_FROM, lsDateWithTime);
		}
		if (lsSubmittedTo != null && !lsSubmittedTo.isEmpty())
		{
			String lsDateWithTime = updateTimeWithDate(lsSubmittedTo, "to");
			loFilterDetails.put(P8Constants.PROPERTY_HMP_SUBMITTED_TO, lsDateWithTime);
		}
		if (lsDateAssignedFrom != null && !lsDateAssignedFrom.isEmpty())
		{
			String lsDateWithTime = updateTimeWithDate(lsDateAssignedFrom, "from");
			loFilterDetails.put(P8Constants.PROPERTY_HMP_ASSIGNED_FROM, lsDateWithTime);
		}
		if (lsDateAssignedTo != null && !lsDateAssignedTo.isEmpty())
		{
			String lsDateWithTime = updateTimeWithDate(lsDateAssignedTo, "to");
			loFilterDetails.put(P8Constants.PROPERTY_HMP_ASSIGNED_TO, lsDateWithTime);
		}
		if (lsAssignedTo != null && !lsAssignedTo.isEmpty())
		{
			lsAssignedTo = lsAssignedTo.substring(lsAssignedTo.indexOf("|") + 1, lsAssignedTo.length());
			loFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, lsAssignedTo);
		}

		if (!validatefFilterAtServer(aoRequest, loFilterDetails, asCurrentTab))
		{
			loFilterDetails = null;
			ApplicationException loAppex = new ApplicationException("Paramerts Modified At run time");
			LOG_OBJECT.Debug("Paramerts Modified At run time", loAppex);
			throw loAppex;
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.createFilter()");
		return loFilterDetails;
	}

	/**
	 * Method to Fetch all the task Details of the Current Task
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asTaskId WorkFlow ID
	 * @return loworkItemDetails
	 * @throws ApplicationException
	 */
	private HashMap getTaskDetails(P8UserSession aoUserSession, String asTaskId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getTaskDetails() with parameters::" + asTaskId);
		HashMap loRequiredProps = new HashMap();
		loRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_ID, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_SECTION_ID, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_TASK_TYPE, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_ENTITY_ID, "");
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		HashMap loWorkItemDetailMap = null;
		loWorkItemDetailMap = getTaskDetailMap(loChannel, loRequiredProps, asTaskId);
		HashMap loWorkItemDetails = (HashMap) loWorkItemDetailMap.get(asTaskId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getTaskDetails()");
		return loWorkItemDetails;
	}

	/**
	 * This Method is used to fetch the Details of a WorkFlow.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param asTaskId WorkFLow ID
	 * @param aoUserSession Filenet Session
	 * @param asTaskType Type Of the Task
	 * @throws ApplicationException
	 */
	private void fetchTaskDetails(ActionRequest aoRequest, ActionResponse aoResponse, String asTaskId,
			P8UserSession aoUserSession, String asTaskType) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		HashMap loRequiredProps = new HashMap();
		Task loTask = new Task();
		String lsDocTypeForFilling = "";
		String lsDocNameForFilling = "";
		List<WorkFlowDetailBean> loWorkFlowDetailBeanList = new ArrayList<WorkFlowDetailBean>();
		ArrayList<TaskQueue> loChildTaskItemLIst = new ArrayList<TaskQueue>();
		boolean lbFinishTask = true;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.fetchTaskDetails() with parameters::" + asTaskId + ","
				+ asTaskType);
		loRequiredProps = setRequiredPropsMap(loRequiredProps, asTaskType);
		Channel loChannel = new Channel();
		loChannel.setData("aoFilenetSession", aoUserSession);
		HashMap loWorkItemDetailMap = null;
		loWorkItemDetailMap = getTaskDetailMap(loChannel, loRequiredProps, asTaskId);
		HashMap loWorkItemDetails = (HashMap) loWorkItemDetailMap.get(asTaskId);
		String lsProviderName = loWorkItemDetails.get(P8Constants.PROPERTY_PE_PROVIDER_NAME).toString();
		String lsProviderId = loWorkItemDetails.get(P8Constants.PROPERTY_PE_PROVIDER_ID).toString();
		String lsAppId = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_APPLICTION_ID);
		String lsTaskType = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_TASK_TYPE);
		String lsSectionId = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_SECTION_ID);
		String lsLaunchBy = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_LAUNCH_BY);
		String lsEnityID = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_ENTITY_ID);
		String lsTaskAssignedToUserId = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_ASSIGNED_TO);
		if (null != asTaskType && asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
		{
			lsDocTypeForFilling = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE);
			lsDocNameForFilling = (String) loWorkItemDetails.get(P8Constants.PROPERTY_PE_UPLOADED_DOC_NAME);
		}
		setTaskBean(loTask, loWorkItemDetails);
		boolean lbIsTaskLocked = (Boolean) loWorkItemDetails.get(P8Constants.PROPERTY_PE_IS_TASK_LOCKED);
		if (lsProviderId != null)
		{
			getProviderDetails(loTask, lsProviderId, lsTaskType, lsAppId, lsLaunchBy);
		}
		createSubSectionAndDocuments(aoRequest, loWorkFlowDetailBeanList, lsAppId, lsSectionId, lsTaskType, asTaskId);
		loSession.setAttribute("loAssociatedDocs1", loWorkFlowDetailBeanList, PortletSession.APPLICATION_SCOPE);
		fetchTaskDetailsForDiffrentTaskTypes(aoRequest, aoUserSession, lsTaskType, asTaskId, lsSectionId, lsAppId,
				lsEnityID, loChildTaskItemLIst);
		Iterator<WorkFlowDetailBean> loIterator = loWorkFlowDetailBeanList.iterator();
		while (loIterator.hasNext())
		{
			String lsAssignedStatus = loIterator.next().getMsAssignedStatus();
			if (lsAssignedStatus == null)
			{
				lbFinishTask = false;
				break;
			}
			else if (lsAssignedStatus.equals(ApplicationConstants.STATUS_IN_REVIEW)
					|| lsAssignedStatus.trim().equals(""))
			{
				lbFinishTask = false;
				break;
			}
		}
		boolean lbIsAllVerified = true;
		Iterator<WorkFlowDetailBean> loIteratorVer = loWorkFlowDetailBeanList.iterator();
		while (loIteratorVer.hasNext())
		{
			String lsCurrentStatus = loIteratorVer.next().getMsAssignedStatus();

			if (lsCurrentStatus == null)
			{
				lsCurrentStatus = "";
			}
			if (!lsCurrentStatus.equalsIgnoreCase(P8Constants.PROPERTY_PE_VALUE_VERIFIED))
			{
				lbIsAllVerified = false;
				break;
			}
		}
		String lsProviderComments = "";
		if (lsAppId != null && lsSectionId != null)
		{
			HashMap loLastCommentReqProps = new HashMap();
			loLastCommentReqProps.put("appid", lsAppId);
			loLastCommentReqProps.put("sectionId", lsSectionId);
			loLastCommentReqProps.put("entityId", lsEnityID);
			loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)
					|| asTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)
					|| asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
			{
				lsProviderComments = getLastProviderCommentGeneral(loLastCommentReqProps);
			}
			else
			{
				lsProviderComments = getLastProviderComment(loLastCommentReqProps, asTaskType);
			}
		}
		HashMap loReqProps = new HashMap();
		String lsServiceFlag = "false";
		loReqProps.put("applicationId", lsAppId);
		String lsBRAppStatus = getBRAppStatus(loReqProps);
		if (lsBRAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
				|| lsBRAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))
		{
			lsServiceFlag = "true";
		}
		setTaskDetailsAttributeInSession(aoRequest, lsProviderName, lsSectionId, lsAppId, lsTaskType, lsServiceFlag,
				lbFinishTask, lbIsAllVerified, loTask, loWorkFlowDetailBeanList, lsProviderId, lsProviderComments,
				lbIsTaskLocked, loChildTaskItemLIst, lsDocNameForFilling, lsDocTypeForFilling, lsTaskAssignedToUserId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.fetchTaskDetails()");
	}

	/**
	 * Method to update the process Section for the service Task by using
	 * "processServicetaskSectionUpdate_DB" transaction
	 * 
	 * @param aoRequiredPropsForDocStatus HashMap OF Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean processSectionUpdateforServiceTask(HashMap aoRequiredPropsForDocStatus) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.processSectionUpdateforServiceTask() with parameters::"
				+ aoRequiredPropsForDocStatus);
		loChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		TransactionManager.executeTransaction(loChannel, "processServicetaskSectionUpdate_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateServicetaskStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.processSectionUpdateforServiceTask");
		return lbUpdateStatus;
	}

	/**
	 * Method to Update the Status the Service Task by using
	 * "updateServiceSectionStatus_DB" transaction
	 * 
	 * @param aoRequiredProps HashMap OF Required Props
	 * @return lbUpdateStatus
	 * @throws ApplicationException
	 */
	private Boolean statusUpdateforServiceTask(HashMap aoRequiredProps) throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.statusUpdateforServiceTask() with parameters::"
				+ aoRequiredProps);
		loChannel.setData("aoRequiredProps", aoRequiredProps);
		TransactionManager.executeTransaction(loChannel, "updateServiceSectionStatus_DB");
		lbUpdateStatus = (Boolean) loChannel.getData("updateServicetaskStatus");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.statusUpdateforServiceTask");
		return lbUpdateStatus;
	}

	/**
	 * Method to implement the Paging & Sorting
	 * 
	 * @param aoReq RenderRequest
	 * @param asActionParamFromHttp Weather from Http Request
	 * @param asPageWithSort Weather needed Paging with Sorting
	 * @param asShowManagementView Which view to Show
	 * @return asShowManagementView
	 */
	private String pagingAndSorting(RenderRequest aoReq, String asActionParamFromHttp, String asPageWithSort,
			String asShowManagementView)
	{

		if ((asActionParamFromHttp != null && asActionParamFromHttp.equalsIgnoreCase("paging"))
				|| (asPageWithSort != null && asPageWithSort.equalsIgnoreCase("true")))
		{
			aoReq.setAttribute("pageIndex", ApplicationSession.getAttribute(aoReq, true, "pageIndex"));
			if (asShowManagementView == null)
			{
				asShowManagementView = "yes";
			}
		}
		if (asActionParamFromHttp != null && asActionParamFromHttp.equalsIgnoreCase("sort"))
		{
			if (asShowManagementView == null)
			{
				asShowManagementView = "yes";
			}
		}
		return asShowManagementView;
	}

	/**
	 * Method to Check the Role of the USer Logged in
	 * 
	 * @param aoReq RenderRequest
	 * @param asUserRole Role Of the User
	 * @return lbManagerRole
	 */
	private boolean checkUserRole(RenderRequest aoReq, String asUserRole)
	{
		boolean lbManagerRole;

		if (asUserRole.equalsIgnoreCase("manager") || asUserRole.equalsIgnoreCase("executive"))
		{
			lbManagerRole = true;
		}
		else
		{
			lbManagerRole = false;
		}

		ApplicationSession.setAttribute(lbManagerRole, aoReq, "managerRole");
		return lbManagerRole;
	}

	/**
	 * Method to return to inbox and unlocking the task from filenet
	 * 
	 * @param aoUserSession Filenet Session
	 * @param asReturnInboxFromHttp Weather from Http Request
	 * @param asTaskWobFromReturnInboxButton WorkFlow Number
	 * @throws ApplicationException
	 */
	private void returnToInbox(P8UserSession aoUserSession, String asReturnInboxFromHttp,
			String asTaskWobFromReturnInboxButton) throws ApplicationException
	{

		Channel loChannel = new Channel();
		loChannel.setData("wobfromreturn", asTaskWobFromReturnInboxButton);
		loChannel.setData("aoFilenetSession", aoUserSession);
		TransactionManager.executeTransaction(loChannel, "unlocktask_Filenet");
	}

	/**
	 * Method to create the Subsections & Documents on the Task Details Page.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoWorkFlowDetailBeanList WorkFlow Detail Bean
	 * @param asAppId Application ID
	 * @param asSectionID Section ID
	 * @param asTaskType Type of the Task
	 * @param aoWobNo WorkFlow Number
	 * @throws ApplicationException
	 */
	private void createSubSectionAndDocuments(ActionRequest aoRequest,
			List<WorkFlowDetailBean> aoWorkFlowDetailBeanList, String asAppId, String asSectionID, String asTaskType,
			String aoWobNo) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.createSubSectionAndDocuments() with parameters::"
				+ aoWorkFlowDetailBeanList + "," + asSectionID + "," + asAppId + "," + asTaskType + "," + aoWobNo);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			getSubSectionAndDocForService(aoWorkFlowDetailBeanList, asAppId, asSectionID, asTaskType);
		}
		else if ((asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)))
		{
			getSubSectionAndDocForNewFiling(aoRequest, aoWorkFlowDetailBeanList, asAppId, asSectionID, asTaskType);
		}
		else if ((asTaskType
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION) || asTaskType
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION)))
		{

		}
		else if ((asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)))
		{
			getSubsectionForLegalNameUpdateReqTask(aoRequest, asAppId, aoWobNo);
		}
		else
		{
			getSubSectionAndDocForBRApp(aoWorkFlowDetailBeanList, asAppId, asSectionID, asTaskType);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.createSubSectionAndDocuments");
	}

	/**
	 * Method to get the Subsection for task Legal name update by executing the
	 * "resulttaskOrganizationInfo_DB" transaction
	 * 
	 * @param aoRequest ActionRequest
	 * @param asAppId Application ID
	 * @param asWobNo WorkFlow Number
	 * @throws ApplicationException
	 */
	private void getSubsectionForLegalNameUpdateReqTask(ActionRequest aoRequest, String asAppId, String asWobNo)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("appID", asAppId);
		loChannel.setData("lsTaskId", asWobNo);
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getSubsectionForLegalNameUpdateReqTask() with parameters::"
				+ asAppId + "," + asWobNo);
		TransactionManager.executeTransaction(loChannel, "taskOrganizationInfo_DB");
		List<OrgNameChangeBean> loWorkItemDetailMap = (List<OrgNameChangeBean>) loChannel
				.getData("resulttaskOrganizationInfo_DB");
		if (loWorkItemDetailMap != null && !loWorkItemDetailMap.isEmpty())
		{

			String lsUserName = loWorkItemDetailMap.get(0).getLsModifiedBy();
			String lsDate = loWorkItemDetailMap.get(0).getLsModifiedDate();
			loWorkItemDetailMap.get(0).setLsModifiedBy(lsUserName);
			loWorkItemDetailMap.get(0).setLsModifiedDate(DateUtil.getDateyyyymmddFormat(lsDate));
			ApplicationSession.setAttribute(loWorkItemDetailMap.get(0), aoRequest, "orgDetails");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getSubsectionForLegalNameUpdateReqTask");
	}

	/**
	 * Method to Fetch the Child Workflows of the BR WorkFlow by using
	 * "taskchilddetails_filenet" transaction
	 * 
	 * @param aoChildTaskItemLIst List Of Child Task Item
	 * @param aoUserSession Filenet Session
	 * @param asTaskId WorkFlow ID
	 * @throws ApplicationException
	 */
	private void getChildWorkFlows(ArrayList<TaskQueue> aoChildTaskItemLIst, P8UserSession aoUserSession,
			String asTaskId) throws ApplicationException
	{
		HashMap loRequiredProps = new HashMap();
		HashMap loWorkItemDetailMap = null;
		Channel loChannel;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getChildWorkFlows() with parameters::" + aoChildTaskItemLIst
				+ "," + asTaskId);
		loRequiredProps.put(P8Constants.PROPERTY_PE_LAST_ASSIGNED, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_TASK_NAME, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, "");
		loRequiredProps.put(P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP, "");

		loChannel = new Channel();
		loChannel.setData("loRequiredProps", loRequiredProps);
		loChannel.setData("lsTaskId", asTaskId);
		loChannel.setData("aoFilenetSession", aoUserSession);

		TransactionManager.executeTransaction(loChannel, "taskchilddetails_filenet");
		loWorkItemDetailMap = (HashMap) loChannel.getData("resultTaskchildDetails");

		Iterator loBaseItr = loWorkItemDetailMap.keySet().iterator();

		while (loBaseItr.hasNext())
		{
			String lsChildWob = (String) loBaseItr.next();
			HashMap loWobHM = (HashMap) loWorkItemDetailMap.get(lsChildWob);
			Iterator loSubItr = loWobHM.keySet().iterator();
			TaskQueue loTQItem = new TaskQueue();
			loTQItem.setMsWobNumber(lsChildWob);

			while (loSubItr.hasNext())
			{
				String lsFieldName = (String) loSubItr.next();
				if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_STATUS))
				{
					loTQItem.setMsStatus(loWobHM.get(P8Constants.PROPERTY_PE_TASK_STATUS).toString());
				}
				else if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_LAST_ASSIGNED))
				{
					loTQItem.setMoLastAssigned(DateUtil.getDateMMddYYYYHHMMFormat((Date) loWobHM
							.get(P8Constants.PROPERTY_PE_LAST_ASSIGNED)));

				}
				else if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_NAME))
				{
					loTQItem.setMsTaskName(loWobHM.get(P8Constants.PROPERTY_PE_TASK_NAME).toString());
				}
				else if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_ASSIGNED_TO))
				{

					loTQItem.setMsAssignedTo(loWobHM.get(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME).toString());

				}
				else if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_IS_TASK_LOCKED))
				{
					loTQItem.setMbIsTaskLocked(Boolean.parseBoolean(loWobHM.get(P8Constants.PROPERTY_PE_IS_TASK_LOCKED)
							.toString()));
				}
				else if (lsFieldName.equalsIgnoreCase(P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP))
				{
					loTQItem.setMbIsManagerReviewStep(Boolean.parseBoolean(loWobHM.get(
							P8Constants.PROPERTY_PE_IS_MANAGER_REVIEW_STEP).toString()));
				}
			}
			aoChildTaskItemLIst.add(loTQItem);

		}
		sortedBRChildTaskList(aoChildTaskItemLIst);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getChildWorkFlows");
	}

	/**
	 * This Method is used to sort the list of BR child tasks.
	 * 
	 * @param aoList List to be sorted
	 * @param asCurrentTab Current Tab(Management or Inbox)
	 * @return aoList
	 */
	private void sortedBRChildTaskList(ArrayList<TaskQueue> aoList) throws ApplicationException
	{
		int liIndex = -1;
		String lsTaskName = "";
		ArrayList<TaskQueue> loList = new ArrayList<TaskQueue>();
		loList.addAll(aoList);
		if (aoList.size() == 4)
		{
			Iterator loItr = loList.iterator();
			TaskQueue loTaskQueue = null;
			while (loItr.hasNext())
			{
				loTaskQueue = (TaskQueue) loItr.next();
				lsTaskName = loTaskQueue.getMsTaskName();
				if (lsTaskName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC))
				{
					liIndex = 0;
				}
				else if (lsTaskName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS))
				{
					liIndex = 1;
				}
				else if (lsTaskName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD))
				{
					liIndex = 2;
				}
				else if (lsTaskName.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
				{
					liIndex = 3;
				}
				if (liIndex != -1)
				{
					aoList.set(liIndex, loTaskQueue);
				}
			}
		}

	}

	/**
	 * Method to Fetch the Details of Contact Us WorkFlow by using
	 * "fetchContactUsDetails_DB" transaction
	 * 
	 * @param asSectionId Section ID
	 * @return loContactUsBean
	 * @throws ApplicationException
	 */
	private ContactUsBean getContactUsDetails(String asSectionId) throws ApplicationException
	{
		int liContactUsId = Integer.parseInt(asSectionId);
		List<ContactUsBean> loContactUsList = null;
		HashMap loRequiredProps = new HashMap();
		loRequiredProps.put("contactUsId", liContactUsId);
		ContactUsBean loContactUsBean = new ContactUsBean();

		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "fetchContactUsDetails_DB");
		loContactUsList = (List<ContactUsBean>) loChannel.getData("subContactUsList");
		Iterator loIter = loContactUsList.iterator();
		if (loIter.hasNext())
		{
			loContactUsBean = (ContactUsBean) loIter.next();

		}
		return loContactUsBean;

	}

	/**
	 * Method to Fetch the Details of Service Withdrawal WorkFlow by using
	 * "fetchServiceWithdrwalDetails_DB" transaction
	 * 
	 * @param asEntityID Entity ID
	 * @return loWithdrawalBean
	 * @throws ApplicationException
	 */
	private WithdrawalBean getServiceWithDrawlDetails(String asEntityID) throws ApplicationException
	{
		List<WithdrawalBean> loServiceWithDrawalList = null;
		HashMap loRequiredProps = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getServiceWithDrawlDetails() with parameters::" + asEntityID);
		loRequiredProps.put("saWithdrawalId", asEntityID);
		WithdrawalBean loWithdrawalBean = new WithdrawalBean();
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "fetchServiceWithdrwalDetails_DB");
		loServiceWithDrawalList = (List<WithdrawalBean>) loChannel.getData("serviceWithDrawalList");
		Iterator loIter = loServiceWithDrawalList.iterator();
		if (loIter.hasNext())
		{
			loWithdrawalBean = (WithdrawalBean) loIter.next();
		}
		loWithdrawalBean.setMsStatusList((ArrayList) FileNetOperationsUtils.getMasterData("fetchWithdrawalStatus"));
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getServiceWithDrawlDetails");
		return loWithdrawalBean;
	}

	/**
	 * Method to Fetch the Details of BR Withdrawal WorkFlow by using
	 * "fetchBRWithdrwalDetails_DB" transaction .
	 * 
	 * @param asEntityId Entity Id
	 * @return loWithdrawalBean
	 * @throws ApplicationException
	 */
	private WithdrawalBean getBRWithDrawlDetails(String asEntityId) throws ApplicationException
	{
		List<WithdrawalBean> loServiceWithDrawalList = null;
		HashMap loRequiredProps = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getBRWithDrawlDetails() with parameters::" + asEntityId);
		loRequiredProps.put("saWithdrawalId", asEntityId);
		WithdrawalBean loWithdrawalBean = new WithdrawalBean();
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "fetchBRWithdrwalDetails_DB");
		loServiceWithDrawalList = (List<WithdrawalBean>) loChannel.getData("brWithDrawalList");
		Iterator loIter = loServiceWithDrawalList.iterator();
		if (loIter.hasNext())
		{
			loWithdrawalBean = (WithdrawalBean) loIter.next();

		}
		loWithdrawalBean.setMsStatusList((ArrayList) FileNetOperationsUtils.getMasterData("fetchWithdrawalStatus"));
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getBRWithDrawlDetails()");
		return loWithdrawalBean;

	}

	/**
	 * Method to Fetch the Sub Sections & Documents for Service WorkFlows by
	 * using "documentservicedetails_db" and "sub_section_service_details"
	 * transactions
	 * 
	 * @param aoWorkFlowDetailBeanList WorkLFow Detail Bean
	 * @param asAppId Application ID
	 * @param asSectionID Section ID
	 * @param asTaskType Type Of the Task
	 * @throws ApplicationException
	 */
	private void getSubSectionAndDocForService(List<WorkFlowDetailBean> aoWorkFlowDetailBeanList, String asAppId,
			String asSectionID, String asTaskType) throws ApplicationException
	{

		Channel loChannel = new Channel();
		List<DocumentBean> loDocumentList = null;
		List<SubSectionBean> loSubSectionList = null;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getSubSectionAndDocForService() with parameters::" + asAppId
				+ "," + asSectionID + "," + asTaskType);
		loChannel.setData("AppID", asAppId);
		loChannel.setData("SectionID", asSectionID);
		TransactionManager.executeTransaction(loChannel, "documentservicedetails_db");
		// Get the data from channel and inserting it in lists
		loDocumentList = (List<DocumentBean>) loChannel.getData("resultDocDetails");
		TransactionManager.executeTransaction(loChannel, "sub_section_service_details");
		loSubSectionList = (List<SubSectionBean>) loChannel.getData("resultquesDetails");
		// Logic to create common List of Beans to display sub section and
		// documents together
		if (!CollectionUtils.isEmpty(loSubSectionList))
		{
			Iterator loListItr = loSubSectionList.iterator();
			while (loListItr.hasNext())
			{
				SubSectionBean loSubSection = (SubSectionBean) loListItr.next();
				String lsModifiedBy = "";
				if (null != loSubSection.getModifiedBy())
				{
					lsModifiedBy = loSubSection.getModifiedBy();
				}
				if (asSectionID.equalsIgnoreCase(loSubSection.getSectionId())
						|| asSectionID.equalsIgnoreCase(loSubSection.getServiceAppId()))
				{
					if (loSubSection.getSubSectionID().contains(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
					{
						WorkFlowDetailBean loWFCB = new WorkFlowDetailBean(loSubSection.getSubSectionID(),
								ApplicationConstants.EMPTY_STRING, "NA", loSubSection.getSubSectionStatus(),
								DateUtil.getDateMMddYYYYFormat(loSubSection.getModifiedDate()), lsModifiedBy,
								loSubSection.getProcSubSectionStatus(), loSubSection.getOrganizationId(), asSectionID
										+ " " + loSubSection.getSubSectionID());
						loWFCB.setMsQuestionDocumentName(P8Constants.PROPERTY_PE_SERVICE_SUBSECTION_NAME + " "
								+ loSubSection.getSubSectionID());
						aoWorkFlowDetailBeanList.add(loWFCB);
						if (!CollectionUtils.isEmpty(loDocumentList))
						{
							Iterator loDocListItr = loDocumentList.iterator();
							while (loDocListItr.hasNext())
							{
								DocumentBean loDocument = (DocumentBean) loDocListItr.next();
								if (loSubSection.getSectionId().equalsIgnoreCase(loDocument.getSectionID()))
								{
									loWFCB = new WorkFlowDetailBean(loDocument.getDocTitle(), loDocument.getDocType(),
											"Info", loDocument.getDocStatus(),
											DateUtil.getDateMMddYYYYFormat(loDocument.getModifiedDate()),
											loDocument.getModifiedBy(), loDocument.getProcDocStatus(),
											loDocument.getOrgID(), loDocument.getDocID());
									aoWorkFlowDetailBeanList.add(loWFCB);

								}
							}
						}
					}

				}
			}
			Iterator loListTwoItr = loSubSectionList.iterator();
			while (loListTwoItr.hasNext())
			{
				SubSectionBean loSubSection = (SubSectionBean) loListTwoItr.next();
				if (asSectionID.equalsIgnoreCase(loSubSection.getSectionId())
						|| asSectionID.equalsIgnoreCase(loSubSection.getServiceAppId()))
				{
					if (!loSubSection.getSubSectionID().contains(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)
							&& !loSubSection.getSubSectionID().contains(
									ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
					{
						WorkFlowDetailBean loWFCB = new WorkFlowDetailBean(loSubSection.getSubSectionID(),
								ApplicationConstants.EMPTY_STRING, "NA", loSubSection.getSubSectionStatus(),
								DateUtil.getDateMMddYYYYFormat(loSubSection.getModifiedDate()),
								loSubSection.getModifiedBy(), loSubSection.getProcSubSectionStatus(),
								loSubSection.getOrganizationId(), loSubSection.getSubSectionID());
						aoWorkFlowDetailBeanList.add(loWFCB);
					}
				}
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getSubSectionAndDocForService()");
	}

	/**
	 * Method to Fetch the Sub Sections & Documents For BR Application WorkFlow
	 * by using the "document_details" and "sub_section_details" transactions
	 * 
	 * @param aoWorkFlowDetailBeanList WorkLFow Detail Bean
	 * @param asAppId Application ID
	 * @param asSectionID Section ID
	 * @param asTaskType Type Of the Task
	 * @throws ApplicationException
	 */
	private void getSubSectionAndDocForBRApp(List<WorkFlowDetailBean> aoWorkFlowDetailBeanList, String asAppId,
			String asSectionID, String asTaskType) throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<DocumentBean> loDocumentList = null;
		List<SubSectionBean> loSubSectionList = null;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getSubSectionAndDocForBRApp() with parameters::" + asAppId
				+ "," + asSectionID + "," + asTaskType);
		loChannel.setData("AppID", asAppId);
		loChannel.setData("SectionID", asSectionID);
		// Execute 2 services to get data from Document and Sub Section Table
		// from database
		TransactionManager.executeTransaction(loChannel, "document_details");
		// Get the data from channel and inserting it in lists
		loDocumentList = (List<DocumentBean>) loChannel.getData("resultDocDetails");
		TransactionManager.executeTransaction(loChannel, "sub_section_details");
		loSubSectionList = (List<SubSectionBean>) loChannel.getData("resultquesDetails");
		// Logic to create common List of Beans to display sub section and
		// documents together
		if (!CollectionUtils.isEmpty(loSubSectionList))
		{
			Iterator loListItr = loSubSectionList.iterator();
			while (loListItr.hasNext())
			{
				SubSectionBean loSubSection = (SubSectionBean) loListItr.next();
				String lsModifiedBy = "";
				if (null != loSubSection.getModifiedBy())
				{
					lsModifiedBy = loSubSection.getModifiedBy();
				}
				if (asSectionID.equalsIgnoreCase(loSubSection.getSectionId())
						|| asSectionID.equalsIgnoreCase(loSubSection.getServiceAppId()))
				{
					if (loSubSection.getSubSectionID().contains(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
					{
						WorkFlowDetailBean loWFCB = new WorkFlowDetailBean(loSubSection.getSubSectionID(),
								ApplicationConstants.EMPTY_STRING, "NA", loSubSection.getSubSectionStatus(),
								DateUtil.getDateMMddYYYYFormat(loSubSection.getModifiedDate()), lsModifiedBy,
								loSubSection.getProcSubSectionStatus(), loSubSection.getOrganizationId(), asSectionID
										+ " " + loSubSection.getSubSectionID());
						loWFCB.setMsQuestionDocumentName(asSectionID + " " + loSubSection.getSubSectionID());
						aoWorkFlowDetailBeanList.add(loWFCB);
						if (!CollectionUtils.isEmpty(loDocumentList))
						{
							Iterator loDocListItr = loDocumentList.iterator();
							while (loDocListItr.hasNext())
							{
								DocumentBean loDocument = (DocumentBean) loDocListItr.next();
								if (loSubSection.getSectionId().equalsIgnoreCase(loDocument.getSectionID())
										&& loSubSection.getFormId().equalsIgnoreCase(loDocument.getFormID())
										&& loSubSection.getFormName().equalsIgnoreCase(loDocument.getFormName())
										&& loSubSection.getFormVersion().equalsIgnoreCase(loDocument.getFormVersion()))
								{
									loWFCB = new WorkFlowDetailBean(loDocument.getDocTitle(), loDocument.getDocType(),
											"Info", loDocument.getDocStatus(),
											DateUtil.getDateMMddYYYYFormat(loDocument.getModifiedDate()),
											loDocument.getModifiedBy(), loDocument.getProcDocStatus(),
											loDocument.getOrgID(), loDocument.getDocID());
									aoWorkFlowDetailBeanList.add(loWFCB);

								}
							}
						}
					}

				}
			}
			Iterator loListTwoItr = loSubSectionList.iterator();
			while (loListTwoItr.hasNext())
			{
				SubSectionBean loSubSection = (SubSectionBean) loListTwoItr.next();
				if (asSectionID.equalsIgnoreCase(loSubSection.getSectionId())
						|| asSectionID.equalsIgnoreCase(loSubSection.getServiceAppId()))
				{
					if (!loSubSection.getSubSectionID().contains(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)
							&& !loSubSection.getSubSectionID().contains(
									ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
					{
						WorkFlowDetailBean loWFCB = new WorkFlowDetailBean(loSubSection.getSubSectionID(),
								ApplicationConstants.EMPTY_STRING, "NA", loSubSection.getSubSectionStatus(),
								DateUtil.getDateMMddYYYYFormat(loSubSection.getModifiedDate()),
								loSubSection.getModifiedBy(), loSubSection.getProcSubSectionStatus(),
								loSubSection.getOrganizationId(), loSubSection.getSubSectionID());
						aoWorkFlowDetailBeanList.add(loWFCB);
					}
				}
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getSubSectionAndDocForBRApp()");
	}

	/**
	 * Method to Fetch the Sub Section & Documents For New Fillings WorkFlow by
	 * using "document_details_OnDocId" transaction
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoWorkFlowDetailBeanList WorkFlow Detail Bean
	 * @param asAppId Application ID
	 * @param asSectionID Section ID
	 * @param asTaskType Type of the Task
	 * @throws ApplicationException
	 */
	private void getSubSectionAndDocForNewFiling(ActionRequest aoRequest,
			List<WorkFlowDetailBean> aoWorkFlowDetailBeanList, String asAppId, String asSectionID, String asTaskType)
			throws ApplicationException
	{
		String lsUserOrg = null;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getSubSectionAndDocForNewFiling() with parameters::"
				+ asAppId + "," + asSectionID + "," + asTaskType);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		// Added extra parameters in Release 5
		lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		Document loDocument = FileNetOperationsUtils.viewDocumentInfo(loUserSession, ApplicationConstants.PROVIDER_ORG,
				asAppId, P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE, lsUserOrg,
				ApplicationConstants.DOC_TYPE_CORPORATE_STRUCTURE,null);

		if (null != loDocument)
		{
			WorkFlowDetailBean loWFCB = new WorkFlowDetailBean(loDocument.getDocName(), loDocument.getDocType(),
					"Info", null, loDocument.getDate(), loDocument.getLastModifiedBy(), null, null,
					loDocument.getDocumentId());
			aoWorkFlowDetailBeanList.add(loWFCB);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getSubSectionAndDocForNewFiling()");
	}

	/**
	 * This Method fetches the details of the provider for the particular
	 * WorkFlow.
	 * 
	 * @param aoTask Task Bean
	 * @param asProviderId Provider ID
	 * @param asTaskType Type of The task
	 * @param asAppId Application ID
	 * @param asLaunchBy Task Launch By from filenet
	 * @throws ApplicationException
	 */
	private void getProviderDetails(Task aoTask, String asProviderId, String asTaskType, String asAppId,
			String asLaunchBy) throws ApplicationException
	{
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getProviderDetails() with parameters::" + asAppId + ","
				+ asProviderId + "," + asTaskType);
		loChannel.setData("userId", asLaunchBy);
		loChannel.setData("orgId", asProviderId);
		TransactionManager.executeTransaction(loChannel, "providerdetails_db");
		List<WorkflowDetails> loProviderDetails = (List<WorkflowDetails>) loChannel.getData("resultProviderDetails");

		if (loProviderDetails.iterator().hasNext())
		{
			WorkflowDetails loWFDetails = loProviderDetails.iterator().next();
			aoTask.setProviderName(loWFDetails.getMsProvidername());
			aoTask.setEmailAdd(loWFDetails.getMsEmailAddress());
			aoTask.setSubmittedby(loWFDetails.getMsSubmittedBy());

			String lsPhoneNoToFormat = loWFDetails.getMsPhone();
			if (lsPhoneNoToFormat != null && lsPhoneNoToFormat.length() == P8Constants.PHONE_NO_LENGTH)
			{
				String lsPhoneAfterformat = lsPhoneNoToFormat.substring(0, 3) + "-" + lsPhoneNoToFormat.substring(3, 6)
						+ "-" + lsPhoneNoToFormat.substring(6, 10);
				aoTask.setPhone(lsPhoneAfterformat);
			}
			else if (lsPhoneNoToFormat == null)
			{
				aoTask.setPhone("");
			}
			else
			{
				aoTask.setPhone(lsPhoneNoToFormat);
			}

			aoTask.setCurrentProvStatus(loWFDetails.getMsProviderSatus());
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
			{
				loChannel = new Channel();
				loChannel.setData("appId", asAppId);
				TransactionManager.executeTransaction(loChannel, "numberservices_db");
				loProviderDetails = (List<WorkflowDetails>) loChannel.getData("noofservices");
				loWFDetails = loProviderDetails.iterator().next();
				aoTask.setNoOfServices(loWFDetails.getMiNoOfServices());
			}
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
			{
				HashMap loReqProps = new HashMap();
				loReqProps.put("applicationId", asAppId);
				String lsBRAppStatus = getBRAppStatus(loReqProps);
				aoTask.setMsBusinessAppSatus(lsBRAppStatus);
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getProviderDetails()");
	}

	/**
	 * Method to fetch the Task Details from Filenet by executing
	 * taskdetails_filenet transaction
	 * 
	 * @param aoChannel Channel Object
	 * @param aoReqPropsMap HashMap Of Required Props
	 * @param aoTaskId WorkFlow ID
	 * @return TaskMap
	 * @throws ApplicationException
	 */
	private HashMap getTaskDetailMap(Channel aoChannel, HashMap aoReqPropsMap, String aoTaskId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.getTaskDetailMap() with parameters::" + aoReqPropsMap + ","
				+ aoTaskId);
		aoChannel.setData("lsTaskId", aoTaskId);
		aoChannel.setData("loRequiredProps", aoReqPropsMap);
		TransactionManager.executeTransaction(aoChannel, "taskdetails_filenet");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.getTaskDetailMap()");
		return (HashMap) aoChannel.getData("resultTaskDetails");
	}

	/**
	 * Method to assign workflows to user's by using "assign_filenet"
	 * transaction
	 * 
	 * @param aoChannel Channel Object
	 * @param aoWobNoList List of WorkFlow Numbers
	 * @param asSessionUserID USer ID
	 * @param asSessionUserName Session User Name
	 * @return HashMap
	 * @throws ApplicationException
	 */
	private HashMap assignTask(Channel aoChannel, ArrayList aoWobNoList, String asSessionUserID,
			String asSessionUserName, String asNameForAudit) throws ApplicationException
	{
		aoChannel.setData("aoWobNumbers", aoWobNoList);
		aoChannel.setData("asUserName", asSessionUserID);
		aoChannel.setData("asSessionUserName", asSessionUserName);
		aoChannel.setData("NameForAudit", asNameForAudit);
		TransactionManager.executeTransaction(aoChannel, "assign_filenet");
		return (HashMap) aoChannel.getData("lbAssigned");
	}

	/**** Begin QC 5446 ****/
	/**
	 * Method to pull a list of agency and agency_id .
	 * 
	 * @param rRequest RenderRequest
	 * @throws ApplicationException
	 */
	private void setAgencyNames(RenderRequest rRequest) throws ApplicationException
	{
		Channel channel = new Channel();
		HHSTransactionManager.executeTransaction(channel, HHSConstants.AS_GET_AGENCY_REVIEW_PROCESS_DATA);
		AgencySettingsBean loAgencySettingsBean = (AgencySettingsBean) channel
				.getData(HHSConstants.AS_AGENCY_SETTING_BEAN_OBJ);
		rRequest.setAttribute(HHSConstants.AS_AGENCY_SETTING_BEAN, loAgencySettingsBean);
	}

	/**** End QC 5446 ****/

	/**
	 * Method to Calculate provider status on Finish of Business Application
	 * Task. Execute transaction fetchOrgExpirationDate and
	 * getBusinessAndServiceStatus
	 * @param aoReq Request
	 * @param asProviderId Provider ID
	 * @param asBRAppId Applicaion ID
	 * @param asProviderName Provider Name
	 * @param asSessionUserId Session UserId
	 * @throws ApplicationException
	 */
	private void updateProviderStatusForBR(ActionRequest aoReq, String asProviderId, String asBRAppId,
			String asProviderName, String asSessionUserId) throws ApplicationException
	{
		ProviderStatusBean loProviderStatusBean = null;
		String lsCurrentProviderStatus = getCurrentProviderStatus(asProviderId);
		String lsStatus = "";
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateProviderStatusForBR() with parameters::" + asProviderId
				+ "," + asBRAppId);
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		HashMap loRequiredProps = new HashMap();
		Date loExpirationDate;
		loChannel.setData("asOrgId", asProviderId);
		TransactionManager.executeTransaction(loChannel, "fetchOrgExpirationDate");
		loExpirationDate = (Date) loChannel.getData("aoExpirationDate");
		loRequiredProps.put("providerId", asProviderId);
		loRequiredProps.put("businessAppId", asBRAppId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsStatus = loProviderStatusBean.getApplicationStatus();
				}
				break;
			}

		}
		updateProviderStatus(aoReq, ProviderStatusBusinessRules.getProviderStatusOnBusinessApplicationCompletion(
				lsCurrentProviderStatus, lsStatus, loExpirationDate, loProviderStatusBeanList, asBRAppId),
				lsCurrentProviderStatus, asProviderId, asProviderName, asSessionUserId, asBRAppId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateProviderStatusForBR()");
	}

	/**
	 * Method to Calculate provider status on Finish of Service Application
	 * Task. Execute transactiongetBusinessAndServiceStatus
	 * @param aoReq Request
	 * @param asProviderId Provider ID
	 * @param asBRAppId BR Application ID
	 * @param asSRAppId Service Application ID
	 * @param asSRStatus Service Status
	 * @param asProviderName Provider Name
	 * @param asSessionUserId Session UserId
	 * @throws ApplicationException
	 */
	private void updateProviderStatusForSR(ActionRequest aoReq, String asProviderId, String asBRAppId,
			String asSRAppId, String asSRStatus, String asProviderName, String asSessionUserId)
			throws ApplicationException
	{
		ProviderStatusBean loProviderStatusBean = null;
		String lsCurrentProviderStatus = getCurrentProviderStatus(asProviderId);
		String lsBrStatus = "";
		List loLServiceApplicationStatuses = new ArrayList();
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateProviderStatusForSR() with parameters::" + asProviderId
				+ "," + asBRAppId + "," + asSRAppId);
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		HashMap loRequiredProps = new HashMap();
		loRequiredProps.put("providerId", asProviderId);
		loRequiredProps.put("businessAppId", asBRAppId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsBrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsBrStatus = loProviderStatusBean.getApplicationStatus();
				}
				break;
			}

		}
		loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (!(loProviderStatusBean.getApplicationId().equalsIgnoreCase(asSRAppId) || loProviderStatusBean
					.getApplicationId().equalsIgnoreCase(asBRAppId)))
			{
				if (loProviderStatusBean.getSupersedingStatus() == null)
				{
					loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
				}
				else
				{
					loLServiceApplicationStatuses.add(loProviderStatusBean.getSupersedingStatus());
				}
			}
		}
		updateProviderStatus(aoReq, ProviderStatusBusinessRules.getProviderStatusOnServiceApplicationCompletion(
				lsCurrentProviderStatus, lsBrStatus, asSRStatus, loLServiceApplicationStatuses),
				lsCurrentProviderStatus, asProviderId, asProviderName, asSessionUserId, asBRAppId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateProviderStatusForSR()");
	}

	/**
	 * Method to Calculate provider status on Finish of Business Application
	 * Execute transaction getBusinessAndServiceStatus Withdrawal Task.
	 * @param aoReq request
	 * @param asStatus Status
	 * @param asProviderId Provider ID
	 * @param asBRAppId Application ID
	 * @param asProviderName Provider Name
	 * @param asSessionUserId Session UserId
	 * @throws ApplicationException
	 */
	private void updateProviderStatusForBRWithdrawal(ActionRequest aoReq, String asStatus, String asProviderId,
			String asBRAppId, String asProviderName, String asSessionUserId) throws ApplicationException
	{
		String lsNewProviderStatus = "";
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		List loLServiceApplicationStatuses = new ArrayList();
		ProviderStatusBean loProviderStatusBean = null;
		HashMap loRequiredProps = new HashMap();
		Channel loChannel = new Channel();
		String lsBrStatus = "";
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateProviderStatusForBRWithdrawal() with parameters::"
				+ asProviderId + "," + asBRAppId + "," + asSessionUserId);
		if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnBRWithdrawal();

		}
		else
		{
			loRequiredProps.put("providerId", asProviderId);
			loRequiredProps.put("businessAppId", asBRAppId);
			loChannel.setData("aoRequiredProps", loRequiredProps);
			TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
			loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
			Iterator loIterator = loProviderStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId))
				{
					if (loProviderStatusBean.getSupersedingStatus() != null)
					{
						lsBrStatus = loProviderStatusBean.getSupersedingStatus();
					}
					else
					{
						lsBrStatus = loProviderStatusBean.getApplicationStatus();
					}
					break;
				}

			}
			loIterator = loProviderStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (!loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId))
				{
					if (loProviderStatusBean.getSupersedingStatus() == null)
					{
						loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
					}
					else
					{
						loLServiceApplicationStatuses.add(loProviderStatusBean.getSupersedingStatus());
					}

				}
			}
			lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnBRWithdrawalRejection(lsBrStatus,
					loLServiceApplicationStatuses);

		}
		updateProviderStatus(aoReq, lsNewProviderStatus, getCurrentProviderStatus(asProviderId), asProviderId,
				asProviderName, asSessionUserId, asBRAppId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateProviderStatusForBRWithdrawal()");
	}

	/**
	 * Method to Calculate provider status on Finish of Service Application
	 * Task. execute Transaction id getBusinessAndServiceStatus
	 * @param aoReq Request
	 * @param asStatus Status
	 * @param asProviderId Provider ID
	 * @param asBRAppId Business Application ID
	 * @param asSRAppId Service Application ID
	 * @param asProviderName Provider Name
	 * @param asSessionUserId Session UserId
	 * @throws ApplicationException
	 */
	private void updateProviderStatusForSRWithdrawal(ActionRequest aoReq, String asStatus, String asProviderId,
			String asBRAppId, String asSRAppId, String asProviderName, String asSessionUserId)
			throws ApplicationException
	{
		String lsNewProviderStatus = "";
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		List loLServiceApplicationStatuses = new ArrayList();
		ProviderStatusBean loProviderStatusBean = new ProviderStatusBean();
		HashMap loRequiredProps = new HashMap();
		Channel loChannel = new Channel();
		String lsBrStatus = "";
		String lsSrStatus = "";
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateProviderStatusForSRWithdrawal() with parameters::"
				+ asProviderId + "," + asBRAppId + "," + asSessionUserId);
		String lsCurrentProviderStatus = getCurrentProviderStatus(asProviderId);
		loRequiredProps.put("providerId", asProviderId);
		loRequiredProps.put("businessAppId", asBRAppId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsBrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsBrStatus = loProviderStatusBean.getApplicationStatus();
				}
				break;
			}

		}
		loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(asSRAppId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsSrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsSrStatus = loProviderStatusBean.getApplicationStatus();
				}
				break;
			}

		}

		loIterator = loProviderStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loProviderStatusBean = (ProviderStatusBean) loIterator.next();
			if (!(loProviderStatusBean.getApplicationId().equalsIgnoreCase(asBRAppId) || loProviderStatusBean
					.getApplicationId().equalsIgnoreCase(asSRAppId)))
			{
				if (loProviderStatusBean.getSupersedingStatus() == null)
				{
					loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
				}
				else
				{
					loLServiceApplicationStatuses.add(loProviderStatusBean.getSupersedingStatus());
				}

			}
		}
		if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnServiceApplicationWithdrawal(
					lsCurrentProviderStatus, lsBrStatus, lsSrStatus, loLServiceApplicationStatuses);
		}
		else
		{
			loLServiceApplicationStatuses.add(lsSrStatus);
			lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnServiceApplicationWithdrawalRejection(
					lsBrStatus, loLServiceApplicationStatuses);
		}
		updateProviderStatus(aoReq, lsNewProviderStatus, lsCurrentProviderStatus, asProviderId, asProviderName,
				asSessionUserId, asBRAppId);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateProviderStatusForSRWithdrawal()");
	}

	/**
	 * Method to Get current provider status. Execute Transaction
	 * getCurrentProviderStatus
	 * @param asProviderId Provider ID
	 * @return Status
	 * @throws ApplicationException
	 */
	private String getCurrentProviderStatus(String asProviderId) throws ApplicationException
	{
		HashMap loRequiredProps = new HashMap();
		Channel loChannel = new Channel();
		loRequiredProps.put("providerId", asProviderId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getCurrentProviderStatus");

		return (String) loChannel.getData("status");
	}

	/**
	 * Method to Update current provider status. Execute transaction
	 * updateOrganizationStatus
	 * @param aoReq Request
	 * @param asNewProviderStatus New Provider Status
	 * @param asCurrentProviderStatus Current Provider Status
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asSessionUserId Session UserId
	 * @param asAppId App Id
	 * @throws ApplicationException
	 */
	private void updateProviderStatus(ActionRequest aoReq, String asNewProviderStatus, String asCurrentProviderStatus,
			String asProviderId, String asProviderName, String asSessionUserId, String asAppId)
			throws ApplicationException
	{
		HashMap loRequiredProps = new HashMap();
		String lsData = "Status Changed To ".concat(asNewProviderStatus);
		String lsEntityIdentifier = asProviderName;
		String lsEmail = (String) ApplicationSession.getAttribute(aoReq, true, "EmailAddress");
		Channel loChannel = new Channel();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateProviderStatusForSRWithdrawal() with parameters::"
				+ asProviderId + "," + "," + asNewProviderStatus);
		if ((!asNewProviderStatus.equalsIgnoreCase(asCurrentProviderStatus)) && (!asNewProviderStatus.isEmpty()))
		{
			// Update Provider Status in Organization Table
			loRequiredProps.put("providerId", asProviderId);
			loRequiredProps.put("orgStatus", asNewProviderStatus);
			loRequiredProps.put("statusChangeDate", new Date());
			loRequiredProps.put("modifiedBy", asSessionUserId);
			loRequiredProps.put("modifiedDate", new Date());

			CommonUtil.addAuditDataToChannel(loChannel, asProviderId, ApplicationConstants.PROVIDER_STATUS_CHANGE,
					ApplicationConstants.STATUS_CHANGE, new Date(), lsEmail, lsData, "Provider", asProviderId, "false",
					asAppId, "", ApplicationConstants.AUDIT_TYPE_APPLICATION);
			loChannel.setData("EntityIdentifier", lsEntityIdentifier);
			loChannel.setData("aoRequiredProps", loRequiredProps);
			TransactionManager.executeTransaction(loChannel, "updateOrganizationStatus");

		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateProviderStatus()");
	}

	/**
	 * Method to Call Notification Service. execute transaction
	 * insertNotificationDetail
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * <li>Then call theTransaction <b>insertNotificationDetail</b></li>
	 * </ul>
	 * @param aoPropertyKey List of Notification ID's
	 * @param asProviderId Provider ID
	 * @param aoMessageBody Body of the message
	 * @param asUserId User Id who call the Notification
	 * @param asEntityId Entity ID
	 * @param asEntityType Entity Type
	 * @throws ApplicationException
	 */
	private void callNotificationService(List aoPropertyKey, List asProviderId, HashMap aoMessageBody, String asUserId,
			String asEntityId, String asEntityType) throws ApplicationException
	{
		HashMap lsHMReqdProp = new HashMap();
		for (Object lsALertId : aoPropertyKey)
		{
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			if (aoMessageBody.get(HHSConstants.LINK) != null)
			{
				loLinkMap.put(HHSConstants.LINK, aoMessageBody.get(HHSConstants.LINK).toString());
			}
			aoMessageBody.remove(HHSConstants.LINK);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationDataBean.setProviderList(asProviderId);
			lsHMReqdProp.put(lsALertId, loNotificationDataBean);
		}
		lsHMReqdProp.put(HHSConstants.NOTIFICATION_ALERT_ID, aoPropertyKey);
		lsHMReqdProp.put(ApplicationConstants.ENTITY_ID, asEntityId);
		lsHMReqdProp.put(ApplicationConstants.ENTITY_TYPE, asEntityType);
		lsHMReqdProp.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		lsHMReqdProp.put(HHSConstants.MODIFIED_BY, asUserId);
		Channel loChannelObj = new Channel();
		lsHMReqdProp.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoMessageBody);
		loChannelObj.setData("loHmNotifyParam", lsHMReqdProp);
		TransactionManager.executeTransaction(loChannelObj, HHSConstants.INSERT_NOTIFICATION_DETAILS);
	}

	/**
	 * This Method is used to update the flag in the audit tables. Execute
	 * Transaction FinishUpdateFlag_DB
	 * @param asTaskType Task Type
	 * @param asSectionID Section ID
	 * @param asTaskType TaskType
	 * @throws ApplicationException
	 */
	private void updateAuditFlag(String asAppID, String asSectionID, String asTaskType) throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loProps = new HashMap();
		loProps.put("status", "true");
		loProps.put("appId", asAppID);
		loProps.put("sectionId", asSectionID);
		loProps.put("taskType", asTaskType);
		loProps.put("eventname", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
		loChannel.setData("loProps", loProps);
		TransactionManager.executeTransaction(loChannel, "FinishUpdateFlag_DB");
	}

	/**
	 * This Method is used to fetch the Service Element Id by using
	 * "fetchServiceElementID_DB" transaction.
	 * 
	 * @param asSectionId Section ID
	 * @param asAppID Application ID
	 * @param aoRequest ActionRequest
	 * @throws ApplicationException
	 */
	private void fetchServiceElementId(String asSectionId, String asAppID, ActionRequest aoRequest)
			throws ApplicationException
	{
		HashMap loProps = new HashMap();
		Channel loChannel = new Channel();
		loProps.put("srID", asSectionId);
		loProps.put("appID", asAppID);
		loChannel.setData("loProps", loProps);
		TransactionManager.executeTransaction(loChannel, "fetchServiceElementID_DB");
		String lsServiceElementID = (String) loChannel.getData("resultServiceID");
		ApplicationSession.setAttribute(lsServiceElementID, aoRequest, "ServiceElementID");
	}

	/**
	 * This Method is used to fetch the Jsp(View) for the task details page
	 * called from the spring render method.
	 * 
	 * @param aoModelAndView ModelAndView
	 * @param aoReq RenderRequest
	 * @param aoSession PortletSession
	 * @param asTaskType Type Of Task
	 * @param asAppId Application ID
	 * @param asTaskId WorkFlow ID
	 * @param asSectionId Section ID
	 * @param aoTaskHistoryList ApplicationAuditBean
	 * @return ModelAndView
	 * @throws ApplicationException
	 */
	private ModelAndView fetchTaskDetailsView(ModelAndView aoModelAndView, RenderRequest aoReq,
			PortletSession aoSession, String asTaskType, String asAppId, String asTaskId, String asSectionId,
			List<ApplicationAuditBean> aoTaskHistoryList) throws ApplicationException
	{
		String lsEntityId = "";
		LOG_OBJECT.Debug("Entered WorkfloDetailController.fetchTaskDetailsView() with parameters::" + asTaskType + ","
				+ asAppId + "," + aoTaskHistoryList);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
		{
			aoModelAndView = fetchDetailsBRApplication(aoReq, aoSession, asAppId, asTaskId, aoTaskHistoryList);
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			aoReq.setAttribute("serviceFlag", ApplicationSession.getAttribute(aoReq, "serviceFlag"));
			aoTaskHistoryList = fetchTaskHistory(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("messagecom", ApplicationSession.getAttribute(aoReq, "message"));
			aoReq.setAttribute("messagetype", ApplicationSession.getAttribute(aoReq, "messagetype"));
			aoReq.setAttribute("managerRole", ApplicationSession.getAttribute(aoReq, true, "managerRole"));
			aoModelAndView = new ModelAndView("ServiceApplicationTaskDetails");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US))
		{
			aoTaskHistoryList = fetchTaskHistoryGeneral(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("contactUsDetails", ApplicationSession.getAttribute(aoReq, "contactUsDetails"));
			aoReq.setAttribute("sucessMsg", ApplicationSession.getAttribute(aoReq, "message"));
			aoModelAndView = new ModelAndView("TaskContactUs");
		}
		else if (asTaskType
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
		{
			lsEntityId = (String) ApplicationSession.getAttribute(aoReq, true, "withDrawalEntityId");
			aoTaskHistoryList = fetchTaskHistoryWithdrawal(asAppId, lsEntityId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("withDrawalDetails", ApplicationSession.getAttribute(aoReq, "withDrawalDetails"));
			aoReq.setAttribute("sucessMsg", ApplicationSession.getAttribute(aoReq, "message"));
			aoModelAndView = new ModelAndView("TaskServiceWithdrawl");
		}
		else if ((asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION)))
		{
			lsEntityId = (String) ApplicationSession.getAttribute(aoReq, true, "withDrawalEntityId");
			aoTaskHistoryList = fetchTaskHistoryWithdrawal(asAppId, lsEntityId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("withDrawalDetails", ApplicationSession.getAttribute(aoReq, "withDrawalDetails"));
			aoReq.setAttribute("sucessMsg", ApplicationSession.getAttribute(aoReq, "message"));
			aoModelAndView = new ModelAndView("TaskServiceWithdrawl");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
		{
			aoTaskHistoryList = fetchTaskHistoryGeneral(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("sucessMsg", ApplicationSession.getAttribute(aoReq, "message"));
			aoReq.setAttribute("finishStatusFiling", ApplicationSession.getAttribute(aoReq, "finishStatusFiling"));
			aoModelAndView = new ModelAndView("TaskNewFiling");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
		{
			aoTaskHistoryList = fetchTaskHistoryGeneral(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("message", ApplicationSession.getAttribute(aoReq, "message"));
			aoReq.setAttribute("withDrawalDetails", ApplicationSession.getAttribute(aoReq, "withDrawalDetails"));
			aoModelAndView = new ModelAndView("TaskDetailsOrganizationAccountRequest");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST))
		{
			aoTaskHistoryList = fetchTaskHistoryGeneral(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("withDrawalDetails", ApplicationSession.getAttribute(aoReq, "withDrawalDetails"));
			aoReq.setAttribute("orgDetails", ApplicationSession.getAttribute(aoReq, true, "orgDetails"));
			aoReq.setAttribute("message", ApplicationSession.getAttribute(aoReq, "message"));
			aoModelAndView = new ModelAndView("TaskDetailsOrganizationLegalNameChange");
		}
		else
		{
			aoTaskHistoryList = fetchTaskHistory(asAppId, asSectionId, aoTaskHistoryList);
			aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
			aoReq.setAttribute("messagecom", ApplicationSession.getAttribute(aoReq, "message"));
			aoModelAndView = new ModelAndView("compliance");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.fetchTaskDetailsView()");
		return aoModelAndView;

	}

	/**
	 * This Method is used to fetch the task History from the Audit table.
	 * 
	 * @param asAppId Application ID
	 * @param asSectionId Section ID
	 * @param aoTaskHistoryList List Of ApplicationAuditBean
	 * @return aoTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> fetchTaskHistory(String asAppId, String asSectionId,
			List<ApplicationAuditBean> aoTaskHistoryList) throws ApplicationException
	{
		if (asAppId != null && !asAppId.isEmpty())
		{
			HashMap loTaskAuditProp = new HashMap();
			loTaskAuditProp.put("appid", asAppId);
			loTaskAuditProp.put("sectionId", asSectionId);
			aoTaskHistoryList = getTaskHistoryDetails(loTaskAuditProp);

		}
		return aoTaskHistoryList;
	}

	/**
	 * This Method is used to fetch the task History from the Audit table.
	 * Execute transaction taskHistoryWithdrawalShow_DB
	 * @param asAppId Application ID
	 * @param asEntityId Entity ID
	 * @param aoTaskHistoryList List Of ApplicationAuditBean
	 * @return aoTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> fetchTaskHistoryWithdrawal(String asAppId, String asEntityId,
			List<ApplicationAuditBean> aoTaskHistoryList) throws ApplicationException
	{
		HashMap loTaskAuditProp = new HashMap();
		loTaskAuditProp.put("appid", asAppId);
		loTaskAuditProp.put("entityId", asEntityId);
		Channel loChannel = new Channel();
		loChannel.setData("aoRequiredProps", loTaskAuditProp);
		TransactionManager.executeTransaction(loChannel, "taskHistoryWithdrawalShow_DB");
		aoTaskHistoryList = (List<ApplicationAuditBean>) loChannel.getData("taskHistoryList");
		return aoTaskHistoryList;
	}

	/**
	 * This Method is used to fetch the task History from the General Audit
	 * table.
	 * 
	 * @param asAppId Application ID
	 * @param asSectionId Section ID
	 * @param aoTaskHistoryList List OF ApplicationAuditBean
	 * @return aoTaskHistoryList
	 * @throws ApplicationException
	 */
	private List<ApplicationAuditBean> fetchTaskHistoryGeneral(String asAppId, String asSectionId,
			List<ApplicationAuditBean> aoTaskHistoryList) throws ApplicationException
	{
		if (asAppId != null && !asAppId.isEmpty())
		{
			HashMap loTaskAuditProp = new HashMap();
			loTaskAuditProp.put("appid", asAppId);
			loTaskAuditProp.put("sectionId", asSectionId);
			aoTaskHistoryList = getTaskHistoryDetailsGeneral(loTaskAuditProp);

		}
		return aoTaskHistoryList;
	}

	/**
	 * This is method is used to set the different attributes in Request.
	 * 
	 * @param aoReq RenderRequest
	 * @param aoSession PortletSession
	 * @param asTaskId WorkFlow ID
	 * @param asAppId Application ID
	 * @param asProviderId Provider ID
	 * @param asSectionId Section ID
	 * @param asUserId User ID
	 * 
	 */
	private void setAttributeInSession(RenderRequest aoReq, PortletSession aoSession, String asTaskId, String asAppId,
			String asProviderId, String asSectionId, String asUserId)
	{

		aoReq.setAttribute("finishTaskStatus", ApplicationSession.getAttribute(aoReq, "finishTaskStatus"));
		aoReq.setAttribute("isAllVerified", ApplicationSession.getAttribute(aoReq, "isAllVerified"));
		aoReq.setAttribute("filenetWorkItemDetails", ApplicationSession.getAttribute(aoReq, "filenetWorkItemDetails"));
		aoReq.setAttribute("associatedDocumentsList", ApplicationSession.getAttribute(aoReq, "associatedDocumentsList"));
		aoReq.setAttribute("finishStatus", ApplicationSession.getAttribute(aoReq, "finishStatus"));
		aoReq.setAttribute("taskId", asTaskId);
		aoReq.setAttribute("appId", asAppId);
		aoReq.setAttribute("providerId", asProviderId);
		aoReq.setAttribute("providerComments", ApplicationSession.getAttribute(aoReq, "providerComments"));
		aoReq.setAttribute("isTaskLocked", ApplicationSession.getAttribute(aoReq, "isTaskLocked"));
		aoReq.setAttribute("validUser", ApplicationSession.getAttribute(aoReq, "validUser"));
		aoReq.setAttribute("lsSectionId", asSectionId);
		aoReq.setAttribute("loginUserID", asUserId);
		aoReq.setAttribute("lsTaskAssignedToUserId", ApplicationSession.getAttribute(aoReq, "lsTaskAssignedToUserId"));
		aoSession.setAttribute("filenetWorkItemDetails",
				ApplicationSession.getAttribute(aoReq, "filenetWorkItemDetails"), PortletSession.APPLICATION_SCOPE);
		aoReq.setAttribute(ApplicationConstants.ERROR_MESSAGE, aoReq.getAttribute(ApplicationConstants.ERROR_MESSAGE));
		aoReq.setAttribute("ServiceElementID", ApplicationSession.getAttribute(aoReq, "ServiceElementID"));

	}

	/**
	 * This Method is used to fetch the task inbox or management view depending
	 * on the user selection
	 * 
	 * @param aoModelAndView ModelAndView
	 * @param aoReq RenderRequest
	 * @param aoSession PortletSession
	 * @param aoUserSession Filenet Session
	 * @param asCurrentTab Current Tab(Management or Inbox)
	 * @param asManagementPath Path of management View
	 * @param asInboxPath Path of Inbox View
	 * @param asFilterTrue Weather Filter is true
	 * @param asShowManagementView Weather Management View
	 * @param aoFilterDetails HashMap OF filter Details
	 * @param aiPageIndex Pagination Page index
	 * @param aiTaskCount Total Task count
	 * @param aiPageSize Size of Page
	 * @param abCheckManagerRole Weather role is manager
	 * @param asUserId USer ID
	 * @param alTaskQueueItems List of Task Queue items
	 * @return aoModelAndView
	 * @throws ApplicationException
	 */
	private ModelAndView fetchTaskListView(ModelAndView aoModelAndView, RenderRequest aoReq, PortletSession aoSession,
			P8UserSession aoUserSession, String asCurrentTab, String asManagementPath, String asInboxPath,
			String asFilterTrue, String asShowManagementView, HashMap aoFilterDetails, int aiPageIndex,
			int aiTaskCount, int aiPageSize, boolean abCheckManagerRole, String asUserId) throws ApplicationException
	{
		String lsErrorMessage = aoReq.getParameter("message");
		String lsErrorMessage2 = aoReq.getParameter("messagetype");
		LOG_OBJECT.Debug("Entered WorkfloDetailController.fetchTaskListView()");
		if (null != PortalUtil.parseQueryString(aoReq, "app_menu_name")
				&& PortalUtil.parseQueryString(aoReq, "app_menu_name").equalsIgnoreCase("inbox_icon"))
		{
			asCurrentTab = P8Constants.PROPERTY_PAGE_INBOX;
		}
		if (asCurrentTab != null && asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
		{
			aoReq.setAttribute("IncludeManagementBox", asManagementPath);

		}
		else
		{
			aoReq.setAttribute("IncludeInbox", asInboxPath);

		}
		aoReq.setAttribute(ApplicationConstants.ERROR_MESSAGE,
				ApplicationSession.getAttribute(aoReq, ApplicationConstants.ERROR_MESSAGE));
		aoReq.setAttribute("loFilterToBeRetained", ApplicationSession.getAttribute(aoReq, true, "loFilterToBeRetained"));
		if ((asFilterTrue != null && asFilterTrue.equalsIgnoreCase("yes"))
				|| (null != asShowManagementView && asShowManagementView.equalsIgnoreCase("yes")))
		{
			aoReq.setAttribute("filterchecked", "display:block");
			aoReq.setAttribute("appId", ApplicationSession.getAttribute(aoReq, true, "appId"));
			aoReq.setAttribute("workItemInbox", ApplicationSession.getAttribute(aoReq, true, "workItemInbox"));
			aoReq.setAttribute(HHSConstants.AS_AGENCY_SETTING_BEAN,
					ApplicationSession.getAttribute(aoReq, true, HHSConstants.AS_AGENCY_SETTING_BEAN)); // QC5446
			aoReq.setAttribute("message", lsErrorMessage);
			aoReq.setAttribute("messagetype", lsErrorMessage2);
			String lsTaskCount = (String) ApplicationSession.getAttribute(aoReq, true, "TotalTask");
			aoReq.setAttribute("TotalTask", lsTaskCount);
			String lsPageIndex = (String) ApplicationSession.getAttribute(aoReq, "pageIndex");

			if (null != lsPageIndex && null != lsTaskCount)
			{
				aiPageIndex = Integer.parseInt(lsPageIndex);
				aiTaskCount = Integer.parseInt(lsTaskCount);
				if ((aiTaskCount % aiPageSize == 0) && (aiTaskCount < aiPageIndex * aiPageSize) && (aiPageIndex > 1))
				{
					lsPageIndex = String.valueOf(aiPageIndex - 1);
					aoReq.setAttribute("pageIndex", lsPageIndex);
				}
			}
			aoReq.setAttribute("PageSize", P8Constants.PE_GRID_PAGE_SIZE);
			aoSession.setAttribute("taskItemList", ApplicationSession.getAttribute(aoReq, true, "taskItemList"),
					PortletSession.APPLICATION_SCOPE);
			aoSession.setAttribute("provList", ApplicationSession.getAttribute(aoReq, true, "provList"),
					PortletSession.APPLICATION_SCOPE);
			asFilterTrue = null;
			aoModelAndView = new ModelAndView("TaskHome");
		}
		// Block to Show Filtered Tasks When coming from Task Home Page
		else if (null != PortalUtil.parseQueryString(aoReq, "taskType"))
		{
			aoModelAndView = filteredTaskFromHomePage(aoReq, aoSession, aoUserSession, asManagementPath, asInboxPath,
					aoFilterDetails, abCheckManagerRole, asUserId);
		}
		else
		{
			aoReq.setAttribute("loFilterToBeRetained", null);
			aoSession.removeAttribute("pageIndex");
			aoSession.setAttribute("taskItemList", null, PortletSession.APPLICATION_SCOPE);
			WorkItemInbox loWorkItemInbox = new WorkItemInbox();
			loWorkItemInbox.setTaskTypeList(FileNetOperationsUtils.getMasterData("fetchTaskTypeData"));
			loWorkItemInbox.setStatusList(FileNetOperationsUtils.getMasterData("fetchInboxFilterStatus"));
			setAgencyNames(aoReq); // QC5446
			aoReq.setAttribute("workItemInbox", loWorkItemInbox);
			aoReq.setAttribute("filterchecked", "display:none");
			aoSession.setAttribute("provList", FileNetOperationsUtils.getProviderList(),
					PortletSession.APPLICATION_SCOPE);
			aoModelAndView = new ModelAndView("TaskHome");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.fetchTaskDetailsView()");
		return aoModelAndView;
	}

	/**
	 * This Method is used to update the DB after finishing the task and it
	 * calls the notification Service Execute transaction
	 * updateLegalNameRejectedRequest_DB
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest ActionRequest
	 * @param asTaskType Type Of Task
	 * @param asChildTaskStatus Status of child Task
	 * @param asSessionUserId User ID
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param aoPropertyKey List of Notification Keys
	 * @param asIsManagerTask Weather a manager task
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asTaskName Name of the Task
	 * @param aoProIdAsList List of Provider iD's
	 * @param aiContactUsId Contact US Id
	 * @param aoChannel Channel Object
	 * @param aoHMSection HashMap of Sections
	 * @param aoHMReturnFromWorkflow HashMap of WorkFlow
	 * @param aoBean OrgNameChangeBean
	 * @throws ApplicationException
	 */
	private void updateTaskAfterFinish(ActionRequest aoRequest, String asTaskType, String asChildTaskStatus,
			String asSessionUserId, String asSectionId, String asAppId, List aoPropertyKey, String asIsManagerTask,
			String asProviderId, String asProviderName, String asTaskName, List aoProIdAsList, int aiContactUsId,
			Channel aoChannel, HashMap aoHMSection, HashMap aoHMReturnFromWorkflow, OrgNameChangeBean aoBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateTaskAfterFinish() with parameters::" + asTaskType + ","
				+ asAppId + "," + aoProIdAsList);
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			finishServiceApplication(aoRequest, asChildTaskStatus, asSessionUserId, asSectionId, asAppId,
					aoPropertyKey, asIsManagerTask, asProviderId, asProviderName, asTaskName, aoProIdAsList);
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US))
		{
			aiContactUsId = Integer.parseInt(asSectionId);
			aoHMSection = new HashMap();
			aoHMSection.put("contactUsStatus", asChildTaskStatus);
			aoHMSection.put("contactId", aiContactUsId);
			Channel loChannelObj = new Channel();
			loChannelObj.setData("aoHMSection", aoHMSection);
			TransactionManager.executeTransaction(loChannelObj, "updateContactStatus_DB");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
		{
			finishNewFilling(aoRequest, asChildTaskStatus, asSectionId, asAppId, aoPropertyKey, asProviderId,
					aoProIdAsList, aoChannel, aoHMReturnFromWorkflow, asSessionUserId);
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST))
		{
			finishProviderAccRequestTask(aoRequest, asChildTaskStatus, asSessionUserId, asSectionId, asAppId,
					aoPropertyKey, asProviderId, asProviderName, aoProIdAsList);
		}

		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST))
		{
			String lsProposedName = "";
			if (aoBean != null)
			{
				lsProposedName = aoBean.getLsProposesOrgLegalName();
			}
			aoHMSection = new HashMap();
			aoHMSection.put("status", asChildTaskStatus);
			aoHMSection.put("providerID", asProviderId);
			aoHMSection.put("newName", lsProposedName);
			aoHMSection.put("sectionId", asSectionId);
			aoHMSection.put("modifiedBy", asSessionUserId);
			aoHMSection.put("modifiedDate", new Date());
			Channel loChannelObj = new Channel();
			if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
			{
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				aoChannel.setData("aoFilenetSession", loUserSession);
				loChannelObj.setData("aoFilenetSession", loUserSession);
				loChannelObj.setData("aoHMSection", aoHMSection);
				loChannelObj.setData("nextAction","update");
				TransactionManager.executeTransaction(loChannelObj, "updateLegalNameApprovedRequest_DB");
				int loCount  = (Integer)loChannelObj.getData("loCount");
				LOG_OBJECT.Error("No. of org updated in Filenet:::"+loCount);
				List<ProviderBean> loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance()
						.getCacheObject(ApplicationConstants.PROV_LIST);
				ProviderBean loProviderBean = new ProviderBean();
				Iterator loIter = loProviderList.iterator();
				while (loIter.hasNext())
				{
					loProviderBean = (ProviderBean) loIter.next();
					if (asProviderId.equalsIgnoreCase(loProviderBean.getHiddenValue()))
					{
						loProviderList.remove(loProviderBean);
						loProviderBean.setDisplayValue(StringEscapeUtils.escapeJavaScript(lsProposedName));
						break;
					}
				}
				loProviderList.add(loProviderBean);

				synchronized (this)
				{
					BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.PROV_LIST, loProviderList);
				}
			}
			if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))
			{
				loChannelObj.setData("aoHMSection", aoHMSection);
				TransactionManager.executeTransaction(loChannelObj, "updateLegalNameRejectedRequest_DB");
			}

		}
		else
		{
			HashMap loHMProcStatusUpdate = new HashMap();
			loHMProcStatusUpdate.put("procSectionStatus", asChildTaskStatus);
			loHMProcStatusUpdate.put("modifiedBy", asSessionUserId);
			loHMProcStatusUpdate.put("modifiedDate", new Date());
			loHMProcStatusUpdate.put("sectionId", asSectionId);
			loHMProcStatusUpdate.put("applicationId", asAppId);
			processSectionUpdate(loHMProcStatusUpdate);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateTaskAfterFinish()");
	}

	/**
	 * This Method is used update the DB after finishing the Withdrawal Service
	 * Application and it calls the notification Service.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest Request
	 * @param aoChannel Channel Object
	 * @param aoUserSession Filenet Session
	 * @param asEntityID Entity ID
	 * @param asFinishStatus Finish Status
	 * @param asUserId User ID
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asAppId Application ID
	 * @param aoPropertyKey List of Notification Keys
	 * @param asSectionId SectionId
	 * @param asTaskName Task Name
	 * @throws ApplicationException
	 */
	private void finishWithdrawlServiceApplication(ActionRequest aoRequest, Channel aoChannel,
			P8UserSession aoUserSession, String asEntityID, String asFinishStatus, String asUserId,
			String asProviderId, String asProviderName, String asAppId, List aoPropertyKey, String asSectionId,
			List aoProIdAsList, String asTaskName) throws ApplicationException
	{

		HashMap loReqdProp = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishWithdrawlServiceApplication() with parameters::"
				+ asFinishStatus + "," + asAppId + "," + aoProIdAsList);
		WithdrawalBean loWithdrawalBean = getServiceWithDrawlDetails(asEntityID);
		loWithdrawalBean.setMsWithdrawStatus(asFinishStatus);
		loWithdrawalBean.setMsApprovedBy(asUserId);
		loWithdrawalBean.setMoApprovedDate(new Date());
		loWithdrawalBean.setMsBusinessApplicationId(asAppId);
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			loWithdrawalBean.setMsStatus(ApplicationConstants.STATUS_WITHDRAWN);
		}
		else
		{
			loWithdrawalBean.setMsStatus(asFinishStatus);
		}

		loWithdrawalBean.setMbToBeTerminate(true);
		loReqdProp.put("entitytype", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		loReqdProp.put("flag", "y");
		loReqdProp.put("event", "Withdrawal");
		loReqdProp.put("timestamp", new Date());
		loReqdProp.put("userid", asUserId);
		loReqdProp.put("orgid", asProviderId);
		aoChannel.setData("aoProps", loReqdProp);
		aoChannel.setData("loServiceWithdrawl", loWithdrawalBean);
		aoChannel.setData("aoFilenetSession", aoUserSession);
		TransactionManager.executeTransaction(aoChannel, "terminateSCWithdrawl");
		TransactionManager.executeTransaction(aoChannel, "AuditInformation");

		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			insertInPrintView(asSectionId, asProviderId,
					P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION, asFinishStatus, asUserId);
			aoPropertyKey.add("NT017");
			aoPropertyKey.add("AL017");

		}
		else
		{
			aoPropertyKey.add("NT018");
			aoPropertyKey.add("AL018");
		}
		updateProviderStatusForSRWithdrawal(aoRequest, asFinishStatus, asProviderId, asAppId,
				loWithdrawalBean.getMsServiceApplicationId(), asProviderName, asUserId);
		HashMap loLastCommentReqProps = new HashMap();
		HashMap loProps = new HashMap();
		loLastCommentReqProps.put("appid", asAppId);
		loLastCommentReqProps.put("sectionId", asSectionId);
		loLastCommentReqProps.put("entityId", asEntityID);
		loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
		String lsProviderComments = getLastProviderComment(loLastCommentReqProps,
				P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION);
		if (asTaskName != null
				&& asTaskName.contains(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION + " - "))
		{
			asTaskName = asTaskName.replace(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION
					+ " - ", "");
		}
		loProps.put("Provider Comments from the Task Details", lsProviderComments);
		loProps.put("SERVICE", asTaskName);
		loProps.put("LINK", getPortalLink(aoRequest, "application"));
		aoProIdAsList.add(asProviderId);
		callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asUserId, asEntityID,
				ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishWithdrawlServiceApplication()");
	}

	/**
	 * This Method is used update the DB after finishing the Withdrawal Business
	 * Application and it calls the notification Service.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest Request
	 * @param aoChannel Channel Object
	 * @param aoUserSession Filenet Session
	 * @param asEntityID Entity ID
	 * @param asFinishStatus Finish Status
	 * @param asUserId User ID
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asAppId Application ID
	 * @param aoPropertyKey List of Notification Keys
	 * @param asSectionId SectionId
	 * @param asTaskName TaskName
	 * @param aoProIdAsList ProIdAsList
	 * @param asUserEmailId UserEmailId
	 * @throws ApplicationException
	 */
	private void finishWithdrawlBusinessApplication(ActionRequest aoRequest, Channel aoChannel,
			P8UserSession aoUserSession, String asEntityID, String asFinishStatus, String asUserId,
			String asProviderId, String asProviderName, String asAppId, List aoPropertyKey, String asSectionId,
			List aoProIdAsList, String asTaskName, String asUserEmailId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishWithdrawlBusinessApplication() with parameters::"
				+ asFinishStatus + "," + asAppId + "," + aoProIdAsList);
		HashMap loReqdProp = new HashMap();
		String lsEventName = ApplicationConstants.EVENT_NAME_FINSH_STATUS;
		String lsAuditData = "";
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + ApplicationConstants.STATUS_WITHDRAWN;
		}
		else
		{
			lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + asFinishStatus;
		}
		String lsEntityType = P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION;
		String lsEntityIdentifier = P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION;
		Date loDate = new Date();
		String lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;

		WithdrawalBean loWithdrawalBean = getBRWithDrawlDetails(asEntityID);
		loWithdrawalBean.setMsWithdrawStatus(asFinishStatus);
		loWithdrawalBean.setMsApprovedBy(asUserId);
		loWithdrawalBean.setMoApprovedDate(new Date());

		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			loWithdrawalBean.setMsStatus(ApplicationConstants.STATUS_WITHDRAWN);
		}
		else
		{
			loWithdrawalBean.setMsStatus(asFinishStatus);
		}
		loWithdrawalBean.setMbToBeTerminate(false);
		loReqdProp.put("entitytype", "Business Application");
		loReqdProp.put("flag", "y");
		loReqdProp.put("event", "Withdrawal");
		loReqdProp.put("timestamp", new Date());
		loReqdProp.put("userid", asUserId);
		loReqdProp.put("orgid", asProviderId);
		aoChannel.setData("aoProps", loReqdProp);
		aoChannel.setData("loBusinessWithdrawl", loWithdrawalBean);
		aoChannel.setData("aoFilenetSession", aoUserSession);
		TransactionManager.executeTransaction(aoChannel, "terminateBRWithdrawl");
		loReqdProp.put("applicationId", loWithdrawalBean.getMsBusinessApplicationId());
		aoChannel.setData("aoHMSection", loReqdProp);
		TransactionManager.executeTransaction(aoChannel, "fetchServiceCapacityIds_DB");
		List loServiceCapacityWOBNums = (List<WorkflowIDServiceBean>) aoChannel.getData("wobNums");
		WorkflowIDServiceBean loWorkflowIDServiceBean = null;
		Iterator loIter = loServiceCapacityWOBNums.iterator();
		while (loIter.hasNext())
		{
			loWorkflowIDServiceBean = (WorkflowIDServiceBean) loIter.next();
			loWithdrawalBean.setMsPWOBNumber(loWorkflowIDServiceBean.getMsWorkFlowId());
			loWithdrawalBean.setMsServiceApplicationId(loWorkflowIDServiceBean.getMsServiceApplicationId());
			String lsServiceWithdrawalID = getServiceWithDrawalID(loWorkflowIDServiceBean.getMsServiceApplicationId());
			if (lsServiceWithdrawalID == null)
			{
				lsServiceWithdrawalID = loWorkflowIDServiceBean.getMsServiceApplicationId();
			}
			loReqdProp = new HashMap();
			loReqdProp.put("entitytype", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
			loReqdProp.put("flag", "y");
			loReqdProp.put("event", "Withdrawal");
			loReqdProp.put("timestamp", new Date());
			loReqdProp.put("userid", asUserId);
			loReqdProp.put("orgid", asProviderId);
			aoChannel.setData("aoProps", loReqdProp);
			aoChannel.setData("loServiceWithdrawl", loWithdrawalBean);
			aoChannel.setData("aoFilenetSession", aoUserSession);
			CommonUtil.addAuditDataToChannel(aoChannel, asProviderId, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW,
					loDate, asUserEmailId, lsAuditData, lsEntityType, lsServiceWithdrawalID, "true", asAppId,
					loWorkflowIDServiceBean.getMsServiceApplicationId(), lsAuditType);
			aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
			TransactionManager.executeTransaction(aoChannel, "terminateSCWithdrawl");
			if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
			{
				TransactionManager.executeTransaction(aoChannel, "AuditInformation");
			}

		}
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			insertInPrintView(asAppId, asProviderId,
					P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION, asFinishStatus,
					asUserId);
			aoPropertyKey.add("NT015");
			aoPropertyKey.add("AL015");
		}
		else
		{
			aoPropertyKey.add("NT016");
			aoPropertyKey.add("AL016");
		}
		updateProviderStatusForBRWithdrawal(aoRequest, asFinishStatus, asProviderId,
				loWithdrawalBean.getMsBusinessApplicationId(), asProviderName, asUserId);
		HashMap loLastCommentReqProps = new HashMap();
		HashMap loProps = new HashMap();
		loLastCommentReqProps.put("appid", asAppId);
		loLastCommentReqProps.put("sectionId", asSectionId);
		loLastCommentReqProps.put("entityId", asEntityID);
		loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
		String lsProviderComments = getLastProviderComment(loLastCommentReqProps,
				P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION);
		loProps.put("Provider Comments from the Task Details", lsProviderComments);
		loProps.put("SERVICE", asTaskName);
		loProps.put("LINK", getPortalLink(aoRequest, "application"));
		aoProIdAsList.add(asProviderId);
		callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asUserId, asEntityID,
				ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishWithdrawlBusinessApplication()");
	}

	/**
	 * This method was updated for Release 3.10.0 6573. Task Name parameter was
	 * added. This Method is used to update the DB after finishing the BR Parent
	 * Task and call the notification Service
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoUserSession Filenet Session
	 * @param asAppId Application ID
	 * @param asSectionId Section ID
	 * @param asFinishStatus Finish Status
	 * @param asSessionUserId User ID
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asSessionUserName User Name
	 * @param aoPropertyKey List OF Notification Keys
	 * @param asSectionRFR Returned for revision check
	 * @param aoProIdAsList List of provider ID's
	 * @throws ApplicationException
	 */
	private void updateDbAfterFinishingParentTask(ActionRequest aoRequest, P8UserSession aoUserSession, String asAppId,
			String asSectionId, String asFinishStatus, String asSessionUserId, String asProviderId,
			String asProviderName, String asSessionUserName, List aoPropertyKey, StringBuffer asSectionRFR,
			List aoProIdAsList, String asTaskName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateDbAfterFinishingParentTask() with parameters::"
				+ asFinishStatus + "," + asAppId + "," + aoProIdAsList);
		updateAuditFlag(asAppId, asSectionId, P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
		HashMap loHMApp = new HashMap();
		loHMApp.put("applicationStatus", asFinishStatus);
		loHMApp.put("modifiedBy", asSessionUserId);
		loHMApp.put("modifiedDate", new Date());
		loHMApp.put("brApplicationId", asAppId);
		loHMApp.put("applicationId", asAppId);
		/** Rel 4.1.0 QC 8280 Begin. we need org because we want to update expiry date, if BusAPP is approved*/
		loHMApp.put("orgId", asProviderId);
		/** Rel 4.1.0 QC 8280 End */
		updateBRApplicationStatus(loHMApp);
		// Add CHAR500 entry if Status is Approved
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			// Check whether application is already Approved
			Channel loChannel = new Channel();
			loHMApp.put("orgId", asProviderId);
			loHMApp.put("status", ApplicationConstants.STATUS_APPROVED);
			loChannel.setData("aoRequiredProps", loHMApp);
			TransactionManager.executeTransaction(loChannel, "approvedApplicationCount_DB");
			int liCount = (Integer) loChannel.getData("count");
			if (liCount >= 1)
			{
				// Start of changes for Release 3.10.0 : Enhancement 6573
				if (asTaskName.equalsIgnoreCase("Business Review Application"))
				{
					TransactionManager.executeTransaction(loChannel, "deleteFilingEntriesForCorporateStuctureChange");
				}
				// End of changes for Release 3.10.0 : Enhancement 6573
				addEntryforCHAR500Docs(loHMApp, ApplicationConstants.PROVIDER_ORG, asProviderId, aoUserSession,
						asSessionUserId);
			}
		}
		updateProviderStatusForBR(aoRequest, asProviderId, asAppId, asProviderName, asSessionUserId);
		HashMap loProps = new HashMap();
		if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			aoPropertyKey.add("NT001");
			aoPropertyKey.add("AL001");
		}
		else if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))
		{
			aoPropertyKey.add("NT002");
			aoPropertyKey.add("AL002");
		}
		else if (asFinishStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			aoPropertyKey.add("NT003");
			aoPropertyKey.add("AL005");
			loProps.put("INSERT SECTIONS HERE", asSectionRFR);
		}
		if (!aoPropertyKey.isEmpty())
		{
			loProps.put("LINK", getPortalLink(aoRequest, "application"));
			aoProIdAsList.add(asProviderId);
			callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asSessionUserId, asAppId,
					ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateDbAfterFinishingParentTask()");
	}

	/**
	 * This Method is used to put different variables in the HashMap called from
	 * fetchTaskDetails method.
	 * 
	 * @param aoRequiredProps HashMap of Required Props
	 * @param asTaskType Type Of Task
	 * @return aoRequiredProps
	 */
	private HashMap setRequiredPropsMap(HashMap aoRequiredProps, String asTaskType)
	{

		aoRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_ID, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_PROVIDER_NAME, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_TASK_NAME, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_LAUNCH_BY, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_LAST_ASSIGNED, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_APPLICTION_ID, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_TASK_TYPE, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_SECTION_ID, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_IS_TASK_LOCKED, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_LAUNCH_DATE, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_ENTITY_ID, "");
		aoRequiredProps.put(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE, "");
		if (null != asTaskType && asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
		{
			aoRequiredProps.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE, "");
			aoRequiredProps.put(P8Constants.PROPERTY_PE_UPLOADED_DOC_NAME, "");
		}
		return aoRequiredProps;
	}

	/**
	 * This Method is used to set values in the Task Bean.
	 * 
	 * @param aoTask Task Bean
	 * @param aoworkItemDetails HashMap OF workItem Details
	 * @throws ApplicationException
	 */
	private void setTaskBean(Task aoTask, HashMap aoworkItemDetails) throws ApplicationException
	{
		aoTask.setSubmittedby(aoworkItemDetails.get(P8Constants.PROPERTY_PE_PROVIDER_ID).toString());
		aoTask.setTaskName(aoworkItemDetails.get(P8Constants.PROPERTY_PE_TASK_NAME).toString());
		aoTask.setAssignedTo(aoworkItemDetails.get(P8Constants.PROPERTY_PE_ASSIGNED_TO_NAME).toString());
		aoTask.setDateAssigned(DateUtil.getDateMMDDYYYYFormat((Date) aoworkItemDetails
				.get(P8Constants.PROPERTY_PE_LAST_ASSIGNED)));
		aoTask.setMsProcessStatus(aoworkItemDetails.get(P8Constants.PROPERTY_PE_TASK_STATUS).toString());
		aoTask.setDateSubmitted(DateUtil.getDateMMDDYYYYFormat((Date) aoworkItemDetails
				.get(P8Constants.PROPERTY_PE_LAUNCH_DATE)));
		aoTask.setMsDateLastModified(DateUtil.getDateMMDDYYYYFormat((Date) aoworkItemDetails
				.get(P8Constants.PROPERTY_PE_TASK_MODIFIED_DATE)));

	}

	/**
	 * This Method is used to set different attributes in Session called from
	 * fetchtaskDetails method
	 * 
	 * @param aoRequest ActionRequest
	 * @param asProviderName Provider Name
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param asTaskType Type of Task
	 * @param asServiceFlag Service Flag
	 * @param abFinishTask Finish Task Flag
	 * @param abIsAllVerified All Verified Flag
	 * @param aoTask Task Bean
	 * @param aoWorkFlowDetailBeanList WorkFlowDetailBean
	 * @param asProviderId Provider ID
	 * @param asProviderComments Provider Comments
	 * @param abIsTaskLocked Weather Task Is locked
	 * @param aoChildTaskItemLIst List of TaskQueue bean
	 * @param asDocNameForFilling Document Name for Fillings
	 * @param asDocTypeForFilling Document Type for Filling
	 * @param asTaskAssignedToUserId Task Assigned to User ID's
	 */
	private void setTaskDetailsAttributeInSession(ActionRequest aoRequest, String asProviderName, String asSectionId,
			String asAppId, String asTaskType, String asServiceFlag, Boolean abFinishTask, Boolean abIsAllVerified,
			Task aoTask, List<WorkFlowDetailBean> aoWorkFlowDetailBeanList, String asProviderId,
			String asProviderComments, Boolean abIsTaskLocked, ArrayList<TaskQueue> aoChildTaskItemLIst,
			String asDocNameForFilling, String asDocTypeForFilling, String asTaskAssignedToUserId)
	{
		ApplicationSession.setAttribute(asProviderName, aoRequest, "providerName");
		ApplicationSession.setAttribute(asSectionId, aoRequest, "sectionId");
		ApplicationSession.setAttribute(asAppId, aoRequest, "applicationId");
		ApplicationSession.setAttribute(asTaskType, aoRequest, "taskType");
		ApplicationSession.setAttribute(asServiceFlag, aoRequest, "serviceFlag");
		ApplicationSession.setAttribute(abFinishTask, aoRequest, "finishTaskStatus");
		ApplicationSession.setAttribute(abIsAllVerified, aoRequest, "isAllVerified");
		ApplicationSession.setAttribute(aoTask, aoRequest, "filenetWorkItemDetails");
		ApplicationSession.setAttribute(aoWorkFlowDetailBeanList, aoRequest, "associatedDocumentsList");
		ApplicationSession.setAttribute(asAppId, aoRequest, "appId");
		ApplicationSession.setAttribute(asProviderId, aoRequest, "providerId");
		ApplicationSession.setAttribute(asProviderComments, aoRequest, "providerComments");
		ApplicationSession.setAttribute(abIsTaskLocked, aoRequest, "isTaskLocked");
		ApplicationSession.setAttribute(aoChildTaskItemLIst, aoRequest, "childTaskItemLIst");
		ApplicationSession.setAttribute(asDocNameForFilling, aoRequest, "lsDocNameForFilling");
		ApplicationSession.setAttribute(asDocTypeForFilling, aoRequest, "lsDocTypeForFilling");
		ApplicationSession.setAttribute(asTaskAssignedToUserId, aoRequest, "lsTaskAssignedToUserId");
		String lsChildSize = String.valueOf(aoChildTaskItemLIst.size());
		ApplicationSession.setAttribute(lsChildSize, aoRequest, "childTaskItemLIstSize");
	}

	/**
	 * This Method is used to fetch the different details according to the
	 * taskType.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoUserSession Filenet Session
	 * @param asTaskType Type Of Task
	 * @param asTaskId WorkFlow ID
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param asEnityID Entity ID
	 * @param aoChildTaskItemLIst List of TaskQueue bean
	 * @throws ApplicationException
	 */
	private void fetchTaskDetailsForDiffrentTaskTypes(ActionRequest aoRequest, P8UserSession aoUserSession,
			String asTaskType, String asTaskId, String asSectionId, String asAppId, String asEnityID,
			ArrayList<TaskQueue> aoChildTaskItemLIst) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered WorkfloDetailController.fetchTaskDetailsForDiffrentTaskTypes()");
		if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
		{
			getChildWorkFlows(aoChildTaskItemLIst, aoUserSession, asTaskId);
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US))
		{
			ApplicationSession.setAttribute(getContactUsDetails(asSectionId), aoRequest, "contactUsDetails");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING))
		{
			ApplicationSession.setAttribute(FileNetOperationsUtils.getMasterData("fetchBRStatus"), aoRequest,
					"finishStatusFiling");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
		{
			ApplicationSession.setAttribute(asEnityID, aoRequest, "withDrawalEntityId");
			ApplicationSession.setAttribute(getServiceWithDrawlDetails(asEnityID), aoRequest, "withDrawalDetails");
		}
		else if (asTaskType
				.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION))
		{
			ApplicationSession.setAttribute(asEnityID, aoRequest, "withDrawalEntityId");
			ApplicationSession.setAttribute(getBRWithDrawlDetails(asEnityID), aoRequest, "withDrawalDetails");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST)
				|| asTaskType
						.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST))
		{
			ApplicationSession.setAttribute(FileNetOperationsUtils.getMasterData("fetchWithdrawalStatus"), aoRequest,
					"withDrawalDetails");
		}
		else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			fetchServiceElementId(asSectionId, asAppId, aoRequest);
			ApplicationSession.setAttribute(FileNetOperationsUtils.getMasterData("fetchBRFinishStatus"), aoRequest,
					"finishStatus");
		}
		else
		{
			ApplicationSession.setAttribute(FileNetOperationsUtils.getMasterData("fetchBRFinishStatus"), aoRequest,
					"finishStatus");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.fetchTaskDetailsForDiffrentTaskTypes()");
	}

	/**
	 * This Method is called from the action method of the spring controller and
	 * used to take the action as been set in the parameter such as finish task,
	 * reserve task, save comments.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param aoSession PortletSession
	 * @param aoUserSession Filenet Session
	 * @param asSessionUserName User Name
	 * @param asEmailId Email Address
	 * @param asUserId USer ID
	 * @param asFinishChildStatus Finish Child Status
	 * @param asIsComingFromTaskDetails Weather Task Coming from Task Details
	 *            Page
	 * @param asTaskName Name of the Task
	 * @param asNextAction Next Action Parameter
	 * @param asTaskId WorkFlow ID
	 * @param aoFilterDetails Hashmap Of Filter Details
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asSectionId Section ID
	 * @param asTaskType Type of Task
	 * @param asApplicationId Application ID
	 * @param asWFEntityID WorkFlow Entity ID
	 * @param abIsValidUser Weather a User is valid
	 * @param aoResultStatus Status
	 * @param aoBean OrgNameChangeBean
	 * @param aoTaskBean Task bean
	 * @param asChoosenTab Which tab s chosed. (management or inbox)
	 * @param abManangerRole Weather task is at manager level
	 * @return asTaskId
	 * @throws ApplicationException
	 */

	private String actionAfterNextAction(ActionRequest aoRequest, ActionResponse aoResponse, PortletSession aoSession,
			P8UserSession aoUserSession, String asSessionUserName, String asEmailId, String asUserId,
			String asFinishChildStatus, String asIsComingFromTaskDetails, String asTaskName, String asNextAction,
			String asTaskId, HashMap aoFilterDetails, String asProviderId, String asProviderName, String asSectionId,
			String asTaskType, String asApplicationId, String asWFEntityID, boolean abIsValidUser,
			HashMap aoResultStatus, OrgNameChangeBean aoBean, Task aoTaskBean, String asChoosenTab,
			boolean abManangerRole) throws ApplicationException
	{
		ArrayList<TaskQueue> loTaskQueueItemsList;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.actionAfterNextAction()");
		if (asNextAction.equalsIgnoreCase("assignTask"))
		{
			assignTasksToUser(aoRequest, aoResponse, aoUserSession, asTaskId, aoSession);
			updateComments(aoRequest, asTaskName, asProviderId, asSectionId, asApplicationId, asTaskType, asEmailId,
					false, asWFEntityID);
			ApplicationSession.setAttribute(null, aoRequest, "message");
			ApplicationSession.setAttribute(null, aoRequest, "messagetype");
			asTaskId = null;
			ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
			loTaskQueueItemsList = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
					aoUserSession, abManangerRole, asChoosenTab, asUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItemsList, asApplicationId);
			aoResponse.setRenderParameter("showmanagementview", "yes");
		}
		else if (asNextAction.equalsIgnoreCase("reserveTask"))
		{
			reserveTaskToCurrentUser(aoRequest, aoUserSession, asTaskId, aoSession);
			loTaskQueueItemsList = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
					aoUserSession, abManangerRole, asChoosenTab, asUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItemsList, asApplicationId);
			aoResponse.setRenderParameter("showmanagementview", "yes");
		}
		else if ("saveCommentsOrStatus".equals(asNextAction))
		{
			if (abIsValidUser)
			{
				updateDocStatusAndComments(aoRequest, asTaskName, asProviderId, asSectionId, asApplicationId,
						asTaskType, asEmailId, asWFEntityID);
			}
			else
			{
				ApplicationSession.setAttribute(abIsValidUser, aoRequest, "validUser");
			}
		}
		else if (asNextAction.equalsIgnoreCase("finishchild"))
		{
			if (validateAtServer(asFinishChildStatus, "childtask"))
			{
				asTaskId = actionAfterFinishingChild(aoRequest, aoResponse, aoUserSession, asSessionUserName,
						asEmailId, asUserId, asFinishChildStatus, asTaskName, asTaskId, aoFilterDetails, asProviderId,
						asProviderName, asSectionId, asTaskType, asApplicationId, asWFEntityID, abIsValidUser,
						aoResultStatus, aoBean, asChoosenTab, abManangerRole);
			}
		}
		else if (asNextAction.equalsIgnoreCase("forcefullySuspend"))
		{
			asTaskId = actionAfterSuspend(aoRequest, aoResponse, aoUserSession, asSessionUserName, asEmailId, asUserId,
					asFinishChildStatus, asTaskName, asTaskId, aoFilterDetails, asProviderId, asProviderName,
					asSectionId, asTaskType, asApplicationId, asWFEntityID, abIsValidUser, aoResultStatus,
					asChoosenTab, abManangerRole);
		}
		else if (asNextAction.equalsIgnoreCase("taskFinishedParent"))
		{
			if (abIsValidUser)
			{
				updateComments(aoRequest, asTaskName, asProviderId, asSectionId, asApplicationId, asTaskType,
						asEmailId, false, asWFEntityID);
				String lsParentWob = (String) ApplicationSession.getAttribute(aoRequest, true, "brAppWOBNo");
				String lsIsManagerTask = aoRequest.getParameter("isManagerTask");
				if (lsIsManagerTask == null)
				{
					lsIsManagerTask = "";
				}
				if (lsIsManagerTask.equalsIgnoreCase("true"))
				{
					if (!CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_SECTION, aoSession))
					{
						throw new ApplicationException(PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
								"TASK_MANAGER_SECURITY_CHECK"));
					}
				}
				String lsFinshParentStatus = finishParentTask(aoRequest, lsParentWob, aoUserSession, lsIsManagerTask,
						asSessionUserName, asApplicationId, asSectionId, asProviderId, asTaskName, asTaskType,
						aoTaskBean, asProviderName, asEmailId);
				ApplicationSession.setAttribute(null, aoRequest, "message");
				if (lsFinshParentStatus != null && !lsFinshParentStatus.isEmpty())
				{
					asTaskId = null;
					ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
					loTaskQueueItemsList = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
							aoFilterDetails, aoUserSession, abManangerRole, asChoosenTab, asUserId);
					FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItemsList,
							asApplicationId);
					aoResponse.setRenderParameter("filteristrue", "yes");
				}
			}
			else
			{
				ApplicationSession.setAttribute(abIsValidUser, aoRequest, "validUser");
			}
		}
		if (asIsComingFromTaskDetails != null && !asIsComingFromTaskDetails.isEmpty()
				&& asIsComingFromTaskDetails.equalsIgnoreCase("trueflag"))
		{
			Channel loChannel = new Channel();
			loChannel.setData("wobNumberChild", asTaskId);
			loChannel.setData("aoFilenetSession", aoUserSession);
			TransactionManager.executeTransaction(loChannel, "unlockParenttask_Filenet");

		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.actionAfterNextAction()");
		return asTaskId;
	}

	/**
	 * This Method is called from the action method and is used to take further
	 * action such as calling the finish method of the child task.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param aoUserSession Filenet Session
	 * @param asSessionUserName User Name
	 * @param asEmailId Email Address
	 * @param asUserId User ID
	 * @param asFinishChildStatus Finish Child Status
	 * @param asTaskName Task Name
	 * @param asTaskId WorkFlow ID
	 * @param aoFilterDetails HashMap Of filter Details
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asSectionId Section ID
	 * @param asTaskType Type Of Task
	 * @param asApplicationId Application ID
	 * @param asWFEntityID WorkFlow Entity ID
	 * @param abIsValidUser Weather User is valid
	 * @param aoResultStatus Status
	 * @param aoBean OrgNameChangeBean
	 * @param asChoosenTab Which Tab is chosen(Management or Inbox)
	 * @param abManangerRole Weather Manager level Task return String
	 * @throws ApplicationException
	 */

	private String actionAfterSuspend(ActionRequest aoRequest, ActionResponse aoResponse, P8UserSession aoUserSession,
			String asSessionUserName, String asEmailId, String asUserId, String asFinishChildStatus, String asTaskName,
			String asTaskId, HashMap aoFilterDetails, String asProviderId, String asProviderName, String asSectionId,
			String asTaskType, String asApplicationId, String asWFEntityID, boolean abIsValidUser,
			HashMap aoResultStatus, String asChoosenTab, boolean abManangerRole) throws ApplicationException
	{
		Channel loChannel = new Channel();
		ArrayList<TaskQueue> loQueueItems;
		StringBuffer lsSectionRFR = new StringBuffer();
		List loPropertyKey = new ArrayList();
		List loProIdAsList = new ArrayList();
		String lsEventName = ApplicationConstants.EVENT_NAME_FINSH_STATUS;
		String lsAuditData = ApplicationConstants.AUDIT_DATA_STATUS_CHANGE + ApplicationConstants.STATUS_SUSPEND;
		String lsEntityType = ApplicationConstants.ENTITY_TYPE_STATUS_CHANGE;
		String lsEntityIdentifier = ApplicationConstants.ENTITY_TYPE_STATUS_CHANGE;
		String lsEntityId = asTaskType;
		HashMap loReqProps = new HashMap();
		Date loDate = new Date();
		String lsSessionUserId = (String) ApplicationSession.getAttribute(aoRequest, true, "loginUserId");
		String lsAuditType = ApplicationConstants.AUDIT_TYPE_APPLICATION;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.actionAfterSuspend() :TaskId" + asTaskId);
		if (abIsValidUser && abManangerRole)
		{
			loChannel.setData("aoFilenetSession", aoUserSession);
			loChannel.setData("asWobNumber", asTaskId);

			CommonUtil.addAuditDataToChannel(loChannel, asProviderId, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW,
					loDate, asEmailId, lsAuditData, lsEntityType, lsEntityId, "true", asApplicationId, asSectionId,
					lsAuditType);
			loChannel.setData("EntityIdentifier", lsEntityIdentifier);
			updateComments(aoRequest, asTaskType, asProviderId, asSectionId, asApplicationId, asTaskType, asEmailId,
					false, asWFEntityID);
			ApplicationSession.setAttribute(null, aoRequest, "message");
			ApplicationSession.setAttribute(null, aoRequest, "messagetype");
			if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
			{
				loChannel.setData("asTaskType", P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION);
				TransactionManager.executeTransaction(loChannel, "suspendtask_filenet");
				insertInPrintView(asApplicationId, asProviderId, asTaskType, ApplicationConstants.STATUS_SUSPEND,
						asUserId);
				HashMap loHMSectionMap = new HashMap();
				loHMSectionMap.put("applicationId", asApplicationId);
				List<SectionBean> loSectionList = getSectionDetails(loHMSectionMap);
				Iterator loIter = loSectionList.iterator();
				SectionBean loBean = null;
				while (loIter.hasNext())
				{
					loBean = (SectionBean) loIter.next();
					String lsSecId = loBean.getSectionId();
					HashMap loHMStatusUpdate = new HashMap();
					loHMStatusUpdate.put("sectionStatus", ApplicationConstants.STATUS_SUSPEND);
					loHMStatusUpdate.put("modifiedBy", lsSessionUserId);
					loHMStatusUpdate.put("modifiedDate", new Date());
					loHMStatusUpdate.put("sectionId", lsSecId);
					loHMStatusUpdate.put("applicationId", asApplicationId);
					sectionUpdate(loHMStatusUpdate);

				}
				loReqProps.put("entityId", asApplicationId);
				loChannel.setData("IDprops", loReqProps);
				TransactionManager.executeTransaction(loChannel, "deleteSuperSeding_DB");
				updateDbAfterFinishingParentTask(aoRequest, aoUserSession, asApplicationId, asSectionId,
						ApplicationConstants.STATUS_SUSPEND, lsSessionUserId, asProviderId, asProviderName,
						asSessionUserName, loPropertyKey, lsSectionRFR, loProIdAsList, asTaskName);
			}
			else if (asTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
			{
				loChannel.setData("asTaskType", P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
				TransactionManager.executeTransaction(loChannel, "suspendtask_filenet");
				insertInPrintView(asSectionId, asProviderId, asTaskType, ApplicationConstants.STATUS_SUSPEND, asUserId);
				updateAuditFlag(asApplicationId, asSectionId, P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
				HashMap loHMApp = new HashMap();
				loHMApp.put("serviceStatus", ApplicationConstants.STATUS_SUSPEND);
				loHMApp.put("modifiedBy", lsSessionUserId);
				loHMApp.put("modifiedDate", new Date());
				loHMApp.put("applicationId", asApplicationId);
				loHMApp.put("sectionId", asSectionId);
				loReqProps.put("entityId", asSectionId);
				loChannel.setData("IDprops", loReqProps);
				TransactionManager.executeTransaction(loChannel, "deleteSuperSeding_DB");
				statusUpdateforServiceTask(loHMApp);
				updateProviderStatusForSR(aoRequest, asProviderId, asApplicationId, asSectionId,
						ApplicationConstants.STATUS_SUSPEND, asProviderName, asUserId);
			}

			asTaskId = null;
			ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
			loQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
					aoUserSession, abManangerRole, asChoosenTab, asUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loQueueItems, asApplicationId);
			aoResponse.setRenderParameter("filteristrue", "yes");

		}
		else
		{
			ApplicationSession.setAttribute(abIsValidUser, aoRequest, "validUser");
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.actionAfterSuspend()");
		return asTaskId;

	}

	/**
	 * This Method is called from the action method and is used to take further
	 * action such as calling the finish method of the child task.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoResponse ActionResponse
	 * @param aoUserSession Filenet Session
	 * @param asSessionUserName User Name
	 * @param asEmailId Email Address
	 * @param asUserId User ID
	 * @param asFinishChildStatus Finish Child Status
	 * @param asTaskName Task Name
	 * @param asTaskId WorkFlow ID
	 * @param aoFilterDetails HashMap Of filter Details
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asSectionId Section ID
	 * @param asTaskType Type Of Task
	 * @param asApplicationId Application ID
	 * @param asWFEntityID WorkFlow Entity ID
	 * @param abIsValidUser Weather User is valid
	 * @param aoResultStatus Status
	 * @param aoBean OrgNameChangeBean
	 * @param asChoosenTab Which Tab is chosen(Management or Inbox)
	 * @param abManangerRole Weather Manager level Task return String
	 * @throws ApplicationException
	 */

	private String actionAfterFinishingChild(ActionRequest aoRequest, ActionResponse aoResponse,
			P8UserSession aoUserSession, String asSessionUserName, String asEmailId, String asUserId,
			String asFinishChildStatus, String asTaskName, String asTaskId, HashMap aoFilterDetails,
			String asProviderId, String asProviderName, String asSectionId, String asTaskType, String asApplicationId,
			String asWFEntityID, boolean abIsValidUser, HashMap aoResultStatus, OrgNameChangeBean aoBean,
			String asChoosenTab, boolean abManangerRole) throws ApplicationException
	{
		ArrayList<TaskQueue> loAlTaskQueueItems;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsReturnToBrDetails = aoRequest.getParameter("lsReturnToBrDetails");
		LOG_OBJECT.Debug("Entered WorkfloDetailController.actionAfterFinishingChild() :TaskId" + asTaskId);
		if (null != asFinishChildStatus && !asFinishChildStatus.isEmpty())
		{
			if (abIsValidUser)
			{
				String lsIsManagerTask = aoRequest.getParameter("isManagerTask");
				if (lsIsManagerTask == null)
				{
					lsIsManagerTask = "";
				}

				if (asTaskType != null)
				{
					if (asTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION)
							|| asTaskType
									.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION))
					{
						terminateWithdrawlTask(aoRequest, aoResponse, aoUserSession, asTaskType, asSectionId,
								asFinishChildStatus, asApplicationId, asTaskName, asSessionUserName, asProviderId,
								asWFEntityID, asUserId, asProviderName, asEmailId);
						asTaskId = null;
						ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
						loAlTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
								aoFilterDetails, aoUserSession, abManangerRole, asChoosenTab, asUserId);
						FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loAlTaskQueueItems,
								asApplicationId);
						aoResponse.setRenderParameter("filteristrue", "yes");

					}
					else
					{
						updateDocStatusAndComments(aoRequest, asTaskName, asProviderId, asSectionId, asApplicationId,
								asTaskType, asEmailId, asWFEntityID);
						aoResultStatus = finishChildTask(aoRequest, asFinishChildStatus, asTaskId, aoUserSession,
								lsIsManagerTask, asSessionUserName, asApplicationId, asSectionId, asTaskName,
								asProviderId, aoBean, asTaskType, asProviderName, asEmailId);
						if (!(lsReturnToBrDetails != null && lsReturnToBrDetails.equalsIgnoreCase("BRApplication")))
						{
							loAlTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
									aoFilterDetails, aoUserSession, abManangerRole, asChoosenTab, asUserId);
							FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loAlTaskQueueItems,
									asApplicationId);
						}
						aoResponse.setRenderParameter("filteristrue", "yes");
					}
				}

				if (aoResultStatus != null && !aoResultStatus.isEmpty())
				{

					if (lsReturnToBrDetails != null && lsReturnToBrDetails.equalsIgnoreCase("BRApplication"))
					{
						String lsBRAppTaskID = (String) loSession.getAttribute("BRAppTaskID",
								PortletSession.APPLICATION_SCOPE);
						if (lsBRAppTaskID != null)
						{
							asTaskId = lsBRAppTaskID;
						}
						ApplicationSession.setAttribute(null, aoRequest, "message");
						ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
					}
					else
					{
						asTaskId = null;
						ApplicationSession.setAttribute(null, aoRequest, "message");
						ApplicationSession.setAttribute(asTaskId, aoRequest, "taskId");
					}

				}
			}
			else
			{
				ApplicationSession.setAttribute(abIsValidUser, aoRequest, "validUser");
			}
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.actionAfterFinishingChild()");
		return asTaskId;

	}

	/**
	 * This Method is used to update the DB after finishing the Service
	 * Application task and it calls the notification Service. Execute
	 * transaction deleteSuperSeding_DB
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param asChildTaskStatus Child Task Status
	 * @param asSessionUserId USer ID
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param aoPropertyKey List of Notification Keys
	 * @param asIsManagerTask Weather a Manager Task
	 * @param asProviderId Provider ID
	 * @param asProviderName Provider Name
	 * @param asTaskName Name of Task
	 * @param aoProIdAsList List of Provider List
	 * @throws ApplicationException
	 */
	private void finishServiceApplication(ActionRequest aoRequest, String asChildTaskStatus, String asSessionUserId,
			String asSectionId, String asAppId, List aoPropertyKey, String asIsManagerTask, String asProviderId,
			String asProviderName, String asTaskName, List aoProIdAsList) throws ApplicationException
	{
		Channel loChannel;
		HashMap loHMProcStatusUpdate = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishServiceApplication() :" + asAppId + "," + asSectionId);
		loHMProcStatusUpdate.put("procSectionStatus", asChildTaskStatus);
		loHMProcStatusUpdate.put("modifiedBy", asSessionUserId);
		loHMProcStatusUpdate.put("modifiedDate", new Date());
		loHMProcStatusUpdate.put("sectionId", asSectionId);
		loHMProcStatusUpdate.put("applicationId", asAppId);
		processSectionUpdateforServiceTask(loHMProcStatusUpdate);
		if (asIsManagerTask.equalsIgnoreCase("true")
				|| asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			updateAuditFlag(asAppId, asSectionId, P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION);
			HashMap loHMApp = new HashMap();
			loHMApp.put("serviceStatus", asChildTaskStatus);
			loHMApp.put("modifiedBy", asSessionUserId);
			loHMApp.put("modifiedDate", new Date());
			loHMApp.put("applicationId", asAppId);
			loHMApp.put("sectionId", asSectionId);
			statusUpdateforServiceTask(loHMApp);
			updateProviderStatusForSR(aoRequest, asProviderId, asAppId, asSectionId, asChildTaskStatus, asProviderName,
					asSessionUserId);
			if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
			{
				aoPropertyKey.add("NT004");
				aoPropertyKey.add("AL006");
			}
			else if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED))
			{
				aoPropertyKey.add("NT005");
				aoPropertyKey.add("AL007");
			}
			else if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
			{
				aoPropertyKey.add("NT006");
				aoPropertyKey.add("AL010");
			}
			if (!aoPropertyKey.isEmpty())
			{
				HashMap loProps = new HashMap();
				if (asTaskName.contains(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
				{
					asTaskName = asTaskName.replace(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION + " -", "");
				}
				loProps.put("SERVICE", asTaskName);
				loProps.put("LINK", getPortalLink(aoRequest, "application"));
				aoProIdAsList.add(asProviderId);
				callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asSessionUserId, asAppId,
						ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);

			}
		}

		if (asIsManagerTask.equalsIgnoreCase("true")
				&& (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || asChildTaskStatus
						.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
		{
			loChannel = new Channel();
			HashMap loReqProps = new HashMap();
			loReqProps.put("entityId", asSectionId);
			loChannel.setData("IDprops", loReqProps);
			TransactionManager.executeTransaction(loChannel, "deleteSuperSeding_DB");
			insertInPrintView(asSectionId, asProviderId, P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION,
					asChildTaskStatus, asSessionUserId);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishServiceApplication()");
	}

	/**
	 * This Method is used to update the DB after finishing the New Filling task
	 * and it calls the notification Service. Execute transaction
	 * updateDocLapsingMasterForFiling_DB
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest ActionRequest
	 * @param asChildTaskStatus Status of Child Task
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param aoPropertyKey List of Notification Keys
	 * @param asProviderId Provider ID
	 * @param aoProIdAsList List Of Provider ID's
	 * @param aoChannel Channel Object
	 * @param aoHMReturnFromWorkflow HashMap Of WorkFlow
	 * @param asSessionUserId SessionUserId
	 * @throws ApplicationException
	 */
	private void finishNewFilling(ActionRequest aoRequest, String asChildTaskStatus, String asSectionId,
			String asAppId, List aoPropertyKey, String asProviderId, List aoProIdAsList, Channel aoChannel,
			HashMap aoHMReturnFromWorkflow, String asSessionUserId) throws ApplicationException
	{
		HashMap loHMSection;
		loHMSection = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishNewFilling() :" + asAppId + "," + asSectionId);
		loHMSection.put("documentStatus", asChildTaskStatus);
		loHMSection.put("documentId", asAppId);
		loHMSection.put("modifiedBy", asSessionUserId);
		loHMSection.put("modifiedDate", new Date());
		// Added for defect fix # 8504
		Integer linkedchangedCount = 0;

		aoChannel.setData("aoHMSection", loHMSection);
		TransactionManager.executeTransaction(aoChannel, "updateFilingDocStatus_DB");
		if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
		{
			loHMSection = null;

			loHMSection = (HashMap) DocumentLapsingUtility.rollbackDueDateOnRejection(
					(String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_UPLOADED_DOC_TYPE),
					(Date) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_CURRENT_DUE_DATE),
					(String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_MONTH),
					(Integer) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_START_YEAR),
					(String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_MONTH),
					(Integer) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_NEXT_PERIOD_COVERED_END_YEAR),
					(String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_LAST_UPLOADED_DOC_TYPE),
					(String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_APPLICABLE_LAW),
					(Boolean) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_IS_SHORT_FILING),
					Boolean.valueOf((String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_AFTER_SHORT_FILING)));

			loHMSection.put(P8Constants.PROPERTY_PE_IS_SHORT_FILING,
					(Boolean) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_IS_SHORT_FILING));
			loHMSection.put("modifiedBy", asSessionUserId);
			loHMSection.put("modifiedDate", new Date());
			loHMSection.put("providerId", (String) aoHMReturnFromWorkflow.get(P8Constants.PROPERTY_PE_PROVIDER_ID));
			aoChannel.setData("aoHMSection", loHMSection);
			TransactionManager.executeTransaction(aoChannel, "updateDocLapsingMasterForFiling_DB");
			aoPropertyKey.add("NT007");
			aoPropertyKey.add("AL011");
			HashMap loLastCommentReqProps = new HashMap();
			HashMap loProps = new HashMap();
			loLastCommentReqProps.put("appid", asAppId);
			loLastCommentReqProps.put("sectionId", asSectionId);
			loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
			String lsProviderComments = getLastProviderCommentGeneral(loLastCommentReqProps);
			loProps.put("Provider Comments from the Task Details", lsProviderComments);
			loProps.put("DOCUMENT TYPE", ApplicationSession.getAttribute(aoRequest, "lsDocTypeForFilling"));
			loProps.put("DOCUMENT NAME", ApplicationSession.getAttribute(aoRequest, "lsDocNameForFilling"));
			loProps.put("LINK", getDocumentVaultLink(aoRequest));
			aoProIdAsList.add(asProviderId);
			// Start QC 9626 R 9.1 - do not send NT007 & AL011 
			//callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asSessionUserId, asAppId,
			//		ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			// End QC 9626 R 9.1 - do not send NT007 & AL011 
			// Adding Transaction for make flag false into Filenet for defect fix # 8504
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			aoChannel.setData("aoFilenetSession",loUserSession);
			aoChannel.setData("documentId",asAppId);
			TransactionManager.executeTransaction(aoChannel, HHSR5Constants.UPDATE_LINKAGE_IN_FILENET,HHSR5Constants.TRANSACTION_ELEMENT_R5);
			linkedchangedCount = (Integer)aoChannel.getData("loDocCount");
			LOG_OBJECT.Info("updated count"+linkedchangedCount);
			// End

		}
		//LOG_OBJECT.Debug("Exit WorkfloDetailController.finishNewFilling():: with linkage change as"+linkedchangedFlag);
	}

	/**
	 * This method is used to fetch the view of BR Application.
	 * 
	 * @param aoReq RenderRequest
	 * @param aoSession PortletSession
	 * @param asAppId Application ID
	 * @param asTaskId WorkFlow ID
	 * @param aoTaskHistoryList List of ApplicationAuditBean
	 * @return loModelAndView
	 * @throws ApplicationException
	 */

	private ModelAndView fetchDetailsBRApplication(RenderRequest aoReq, PortletSession aoSession, String asAppId,
			String asTaskId, List<ApplicationAuditBean> aoTaskHistoryList) throws ApplicationException
	{
		ModelAndView loModelAndView;
		if (asAppId != null && !asAppId.isEmpty())
		{
			HashMap loTaskAuditProp = new HashMap();
			loTaskAuditProp.put("appid", asAppId);
			aoTaskHistoryList = getAppTaskHistoryDetails(loTaskAuditProp, asAppId);
		}
		ApplicationSession.setAttribute(asTaskId, aoReq, "brAppWOBNo");
		aoSession.setAttribute("BRAppTaskID", asTaskId, PortletSession.APPLICATION_SCOPE);
		aoReq.setAttribute("taskItemChildListSize", ApplicationSession.getAttribute(aoReq, "childTaskItemLIstSize"));
		aoReq.setAttribute("messagecom", ApplicationSession.getAttribute(aoReq, "message"));
		aoReq.setAttribute("messagecom", aoReq.getParameter("message"));
		aoReq.setAttribute("managerRole", ApplicationSession.getAttribute(aoReq, true, "managerRole"));
		aoSession.setAttribute("taskItemChildList", ApplicationSession.getAttribute(aoReq, "childTaskItemLIst"),
				PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute("taskHistoryList", aoTaskHistoryList, PortletSession.APPLICATION_SCOPE);
		loModelAndView = new ModelAndView("TaskDetailsBusinessApplication");
		return loModelAndView;
	}

	/**
	 * This Method is used to fetch the Home Screen View.
	 * 
	 * @param aoReq RenderRequest
	 * @param aoSession PortletSession
	 * @param aoUserSession Filenet Session
	 * @param asManagementPath Path of Management Tab
	 * @param asInboxPath Path Of inbox Tab
	 * @param aoFilterDetails HashMap of filter Details
	 * @param abCheckManagerRole Weather Task Role is manager
	 * @param asUserId User ID
	 * @return aoModelAndView
	 * @throws ApplicationException
	 */
	private ModelAndView filteredTaskFromHomePage(RenderRequest aoReq, PortletSession aoSession,
			P8UserSession aoUserSession, String asManagementPath, String asInboxPath, HashMap aoFilterDetails,
			boolean abCheckManagerRole, String asUserId) throws ApplicationException
	{
		ModelAndView loModelAndView;
		String lsCurrentTab;
		HashMap loFilterToBeRetained = new HashMap();
		ArrayList<TaskQueue> loTaskQueueItems;
		aoReq.setAttribute("pageIndex", null);
		LOG_OBJECT.Debug("Entered WorkfloDetailController.filteredTaskFromHomePage() ");
		if (null != PortalUtil.parseQueryString(aoReq, "taskScreen")
				&& P8Constants.PROPERTY_PAGE_INBOX.equalsIgnoreCase(PortalUtil.parseQueryString(aoReq, "taskScreen")))
		{
			if (aoReq.getAttribute("IncludeManagementBox") != null)
			{
				aoReq.setAttribute("IncludeManagementBox", null);
			}
			aoReq.setAttribute("IncludeInbox", asInboxPath);
			aoFilterDetails.put(P8Constants.PROPERTY_PE_TASK_TYPE, PortalUtil.parseQueryString(aoReq, "taskType"));
			loFilterToBeRetained.put("TaskType", PortalUtil.parseQueryString(aoReq, "taskType"));
			lsCurrentTab = P8Constants.PROPERTY_PAGE_INBOX;
			loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
					aoUserSession, abCheckManagerRole, lsCurrentTab, asUserId);
		}
		else
		{
			if (aoReq.getAttribute("IncludeInbox") != null)
			{
				aoReq.setAttribute("IncludeInbox", null);
			}
			aoReq.setAttribute("IncludeManagementBox", asManagementPath);
			aoFilterDetails.put(P8Constants.PROPERTY_PE_TASK_TYPE, PortalUtil.parseQueryString(aoReq, "taskType"));
			if ("assign".equalsIgnoreCase(PortalUtil.parseQueryString(aoReq, "action")))
			{
				aoFilterDetails.put(P8Constants.PROPERTY_PE_TASK_TYPE, PortalUtil.parseQueryString(aoReq, "taskType"));
				aoFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
				loFilterToBeRetained.put("TaskType", PortalUtil.parseQueryString(aoReq, "taskType"));
				loFilterToBeRetained.put("TaskOwner", P8Constants.PROPERTY_PE_VALUE_ALL_STAFF);
				lsCurrentTab = P8Constants.PROPERTY_PAGE_TASK_MANAGMENT;
				loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
						aoUserSession, abCheckManagerRole, lsCurrentTab, asUserId);
			}
			else
			{
				if (P8Constants.PROPERTY_PE_VALUE_UNASSIGN.equalsIgnoreCase(PortalUtil
						.parseQueryString(aoReq, "action")))
				{
					aoFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, P8Constants.PE_TASK_UNASSIGNED_MANAGER);
					loFilterToBeRetained.put("TaskOwner", P8Constants.PE_TASK_UNASSIGNED_MANAGER);
				}
				else
				{
					aoFilterDetails.put(P8Constants.PROPERTY_PE_ASSIGNED_TO, P8Constants.PE_TASK_UNASSIGNED);
					loFilterToBeRetained.put("TaskOwner", P8Constants.PE_TASK_UNASSIGNED);
				}
				aoFilterDetails.put(P8Constants.PROPERTY_PE_TASK_TYPE, PortalUtil.parseQueryString(aoReq, "taskType"));
				loFilterToBeRetained.put("TaskType", PortalUtil.parseQueryString(aoReq, "taskType"));
				lsCurrentTab = P8Constants.PROPERTY_PAGE_TASK_MANAGMENT;
				loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(aoFilterDetails,
						aoUserSession, abCheckManagerRole, lsCurrentTab, asUserId);
			}
		}
		ApplicationSession.setAttribute(loFilterToBeRetained, aoReq, "loFilterToBeRetained");
		aoReq.setAttribute("loFilterToBeRetained", loFilterToBeRetained);
		WorkItemInbox loWorkItemInbox = new WorkItemInbox();
		loWorkItemInbox.setTaskTypeList(FileNetOperationsUtils.getMasterData("fetchTaskTypeData"));
		loWorkItemInbox.setStatusList(FileNetOperationsUtils.getMasterData("fetchInboxFilterStatus"));
		setAgencyNames(aoReq); // QC 5446
		aoReq.setAttribute("filterchecked", "display:block");
		aoReq.setAttribute("appId", "");
		aoReq.setAttribute("workItemInbox", loWorkItemInbox);
		aoReq.setAttribute("TotalTask", String.valueOf(loTaskQueueItems.size()));
		aoReq.setAttribute("PageSize", P8Constants.PE_GRID_PAGE_SIZE);
		aoSession.setAttribute("taskItemList", loTaskQueueItems, PortletSession.APPLICATION_SCOPE);
		aoSession.setAttribute("provList", FileNetOperationsUtils.getProviderList(), PortletSession.APPLICATION_SCOPE);
		ApplicationSession.setAttribute(aoFilterDetails, aoReq, "filterTab");
		ApplicationSession.setAttribute(lsCurrentTab, aoReq, "choosenTab");
		loModelAndView = new ModelAndView("TaskHome");
		LOG_OBJECT.Debug("Exit WorkfloDetailController.filteredTaskFromHomePage()");

		ApplicationSession.setAttribute(loWorkItemInbox, aoReq, "workItemInbox");
		ApplicationSession.setAttribute("", aoReq, "appId");
		ApplicationSession.setAttribute(String.valueOf(loTaskQueueItems.size()), aoReq, "TotalTask");
		ApplicationSession.setAttribute(loTaskQueueItems, aoReq, "taskItemList");
		ApplicationSession.setAttribute(FileNetOperationsUtils.getProviderList(), aoReq, "provList");

		return loModelAndView;
	}

	/**
	 * This Method generates the URL for notifications
	 * 
	 * @param aoRequest Action Request
	 * @param asLink StringBuffer
	 * @return lsBfApplicationUrl
	 * @throws ApplicationException
	 */
	private StringBuffer getPortalLink(ActionRequest aoRequest, String asLink) throws ApplicationException
	{
		String lsNotificationLink;
		if (asLink.equalsIgnoreCase("login"))
		{
			lsNotificationLink = ApplicationConstants.LOGIN_NOTIFICATION_LINK;
		}
		else
		{
			lsNotificationLink = ApplicationConstants.TASK_ALERT_NOTIFICATION_LINK;
		}
		StringBuffer lsBfApplicationUrl = new StringBuffer();
		lsBfApplicationUrl.append(aoRequest.getScheme());
		lsBfApplicationUrl.append("://");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_NAME_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(":");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_PORT_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append("/");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONTEXT_PATH_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(lsNotificationLink);
		return lsBfApplicationUrl;
	}

	/**
	 * This Method Sets the Filter variables in session for further fetching in
	 * jsp.
	 * 
	 * @param aoRequest Action Request
	 * @param asTaskType Type Of the task
	 * @param asProviderName Name Of the Provider
	 * @param asStatus Status of the task
	 * @param asSubmittedFrom Submitted From date
	 * @param asSubmittedTo Submitted to date
	 * @param asDateAssignedFrom Assigned from date
	 * @param asDateAssignedTo Assigned to date
	 * @param asAssignedTo assigned to user name
	 * 
	 */

	private void putFilterinSession(ActionRequest aoRequest, String asTaskType, String asProviderName,
			String asProcurementTitle, String asCompetitionPoolTitle, String agencyName, String asStatus,
			String asSubmittedFrom, String asSubmittedTo, String asDateAssignedFrom, String asDateAssignedTo,
			String asAssignedTo)
	{
		HashMap loFilterToBeRetained = new HashMap();
		loFilterToBeRetained.put("TaskType", asTaskType);
		loFilterToBeRetained.put("ProviderName", asProviderName);

		/**** Begin QC 5446 ****/
		loFilterToBeRetained.put(P8Constants.PE_WORKFLOW_PROCUREMENT_TITLE, asProcurementTitle);
		loFilterToBeRetained.put(P8Constants.PE_WORKFLOW_COMPETITION_POOL_TITLE, asCompetitionPoolTitle);
		loFilterToBeRetained.put(HHSConstants.AGENCY_NAME, agencyName);
		/**** End QC 5446 ****/
		loFilterToBeRetained.put("TaskStatus", asStatus);
		loFilterToBeRetained.put("SubmittedFrom", asSubmittedFrom);
		loFilterToBeRetained.put("SubmittedTo", asSubmittedTo);
		loFilterToBeRetained.put("AssignedFrom", asDateAssignedFrom);
		loFilterToBeRetained.put("AssignedTo", asDateAssignedTo);
		loFilterToBeRetained.put("TaskOwner", asAssignedTo);

		ApplicationSession.setAttribute(loFilterToBeRetained, aoRequest, "loFilterToBeRetained");

	}

	/**
	 * This Method updates the Doc Status.
	 * 
	 * @param asProviderName Provider Name
	 * @param asSectionId Section Id
	 * @param asApplicationId Application ID
	 * @param asUserEmailId User Email ID
	 * @param aoRequiredPropsForDocStatus HashMap of diffrent Statues
	 * @param aoDate Date
	 * @param asAuditType Type Of Audit
	 * @param asSectionNameAfterSplit Section Name after Split
	 * @param asStatusAfterSplit Status After the Split
	 * @param asPrevCurrentStatus Current Status
	 * @param asData Data
	 * @param aoChannel Channel Object
	 * @param asTaskName TaskName
	 * @throws ApplicationException
	 */
	private void updateDocStatusIfCondition(String asProviderName, String asSectionId, String asApplicationId,
			String asUserEmailId, HashMap aoRequiredPropsForDocStatus, Date aoDate, String asAuditType,
			String asSectionNameAfterSplit, String asStatusAfterSplit, String asPrevCurrentStatus, String asData,
			Channel aoChannel, String asTaskName) throws ApplicationException
	{
		String lsEntityType;
		String lsEntityId;
		String lsEventName;
		LOG_OBJECT.Debug("Entered WorkfloDetailController.updateDocStatusIfCondition() ");
		if (asSectionNameAfterSplit.contains(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
		{
			aoRequiredPropsForDocStatus.put("subSectionId", ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
		}
		else
		{
			aoRequiredPropsForDocStatus.put("subSectionId", asSectionNameAfterSplit);
		}
		lsEntityType = ApplicationConstants.ENTITY_TYPE_SUB_SECTION;
		String lsEntityIdentifier = asTaskName + ":" + asSectionNameAfterSplit;
		lsEntityId = asSectionNameAfterSplit;
		lsEventName = ApplicationConstants.EVENT_NAME_SUB_SECTION;
		aoChannel.setData("aoRequiredProps", aoRequiredPropsForDocStatus);
		CommonUtil.addAuditDataToChannel(aoChannel, asProviderName, lsEventName, P8Constants.EVENT_TYPE_WORKFLOW,
				aoDate, asUserEmailId, asData, lsEntityType, lsEntityId, "false", asApplicationId, asSectionId,
				asAuditType);
		aoChannel.setData("EntityIdentifier", lsEntityIdentifier);
		if (!asPrevCurrentStatus.equalsIgnoreCase(asStatusAfterSplit))
		{
			TransactionManager.executeTransaction(aoChannel, "updateProcSubSectionStatus_DB");

		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.updateDocStatusIfCondition()");
	}

	/**
	 * This Method Catches the Exception & put it in logs.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest Action Request
	 * @param aoAppex Exception Object
	 */
	private void catchException(ActionRequest aoRequest, ApplicationException aoAppex)
	{
		String lsErrorMsg = aoAppex.getMessage();
		LOG_OBJECT.Error("Application Exception in WorkFlow Detail Controller", aoAppex);
		ApplicationSession.setAttribute(lsErrorMsg, aoRequest, ApplicationConstants.ERROR_MESSAGE);
		ApplicationSession.setAttribute("failed", aoRequest, "messagetype");
		ApplicationSession.setAttribute(null, aoRequest, "taskId");
	}

	/**
	 * This Method is called from the action method for getting the filter
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 * @param aoSession Portlet Session
	 * @param aoUserSession Filenet Session
	 * @param asUserId User ID
	 * @param asFilterTrue Weather Filter Is true
	 * @param asChoosenTab Which tab is chosen
	 * @param asApplicationId Application ID
	 * @param abManangerRole Weather role of user is manager\
	 * @return loFilterDetails
	 * @throws ApplicationException
	 */
	private HashMap filterAction(ActionRequest aoRequest, ActionResponse aoResponse, PortletSession aoSession,
			P8UserSession aoUserSession, String asUserId, String asFilterTrue, String asChoosenTab,
			String asApplicationId, boolean abManangerRole) throws ApplicationException
	{
		ArrayList<TaskQueue> loTaskQueueListItems;
		aoSession.removeAttribute("pageIndex");
		aoResponse.setRenderParameter("filteristrue", asFilterTrue);
		HashMap loFilterDetails = createFilter(aoRequest, aoResponse, asChoosenTab);
		ApplicationSession.setAttribute(loFilterDetails, aoRequest, "filterTab");
		loTaskQueueListItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(loFilterDetails,
				aoUserSession, abManangerRole, asChoosenTab, asUserId);
		FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueListItems, asApplicationId);
		return loFilterDetails;
	}

	/**
	 * This Method generates the URL for DocumentVault
	 * 
	 * @param aoRequest Action Request
	 * @return lsBfApplicationUrl
	 * @throws ApplicationException
	 */
	private StringBuffer getDocumentVaultLink(ActionRequest aoRequest) throws ApplicationException
	{
		StringBuffer lsBfApplicationUrl = new StringBuffer();
		lsBfApplicationUrl.append(aoRequest.getScheme());
		lsBfApplicationUrl.append("://");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_NAME_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(":");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"SERVER_PORT_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append("/");
		lsBfApplicationUrl.append(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONTEXT_PATH_FOR_PROVIDER_BATCH"));
		lsBfApplicationUrl.append(ApplicationConstants.SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK);
		return lsBfApplicationUrl;
	}

	/**
	 * This Method inserts the values in Print View Table Execute transaction
	 * insertInPrintView
	 * @param asPrintId Application Id
	 * @param asOrgId Organization Id
	 * @param asTaskType Type of the Task
	 * @param asStatus Status of Task
	 * @param asUserId User ID of logged in User
	 * @throws ApplicationException
	 */
	private void insertInPrintView(String asPrintId, String asOrgId, String asTaskType, String asStatus, String asUserId)
			throws ApplicationException
	{
		java.util.Date loDate = new java.util.Date();
		Channel loChannel = new Channel();
		HashMap loReqProps = new HashMap();
		loReqProps.put("printId", asPrintId);
		loReqProps.put("orgId", asOrgId);
		loReqProps.put("taskId", asTaskType);
		loReqProps.put("procStatus", asStatus);
		loReqProps.put("date", new Timestamp(loDate.getTime()));
		loReqProps.put("createdBy", asUserId);
		loChannel.setData("IDprops", loReqProps);
		TransactionManager.executeTransaction(loChannel, "insertInPrintView");
	}

	/**
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest Action Request
	 * @param asChildTaskStatus Status of task
	 * @param asSessionUserId User ID of logged in user
	 * @param asSectionId Section ID
	 * @param asAppId Application ID
	 * @param aoPropertyKey Key of property
	 * @param asProviderId Provider ID
	 * @param asProviderName Name of provider
	 * @param aoProIdAsList List of providers
	 * @throws ApplicationException
	 */
	private void finishProviderAccRequestTask(ActionRequest aoRequest, String asChildTaskStatus,
			String asSessionUserId, String asSectionId, String asAppId, List aoPropertyKey, String asProviderId,
			String asProviderName, List aoProIdAsList) throws ApplicationException
	{
		HashMap aoHMSection;
		aoHMSection = new HashMap();
		LOG_OBJECT.Debug("Entered WorkfloDetailController.finishProviderAccRequestTask() " + asAppId + ","
				+ asSectionId);
		aoHMSection.put("status", asChildTaskStatus);
		aoHMSection.put("providerID", asProviderId);
		aoHMSection.put("modifiedBy", asSessionUserId);
		aoHMSection.put("modifiedDate", new Date());
		aoHMSection.put("newName", asProviderName);
		Channel loChannelObj = new Channel();
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannelObj.setData("aoFilenetSession", loUserSession);
		loChannelObj.setData("aoHMSection", aoHMSection);
		loChannelObj.setData("nextAction","add");
		LOG_OBJECT.Error("Channel Val are::::"+loChannelObj);
		TransactionManager.executeTransaction(loChannelObj, "updateOrgRequest_DB");
		int loCount  = (Integer)loChannelObj.getData("loCount");
		LOG_OBJECT.Error("No. of org updated in Filenet:::"+loCount);
		if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			aoPropertyKey.add("NT011");
			List<ProviderBean> loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.PROV_LIST);
			ProviderBean loProviderBean = new ProviderBean();
			loProviderBean.setHiddenValue(asProviderId);
			loProviderBean.setDisplayValue(StringEscapeUtils.escapeJavaScript(asProviderName));
			loProviderList.add(loProviderBean);
			synchronized (this)
			{
				BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.PROV_LIST, loProviderList);
			}

		}
		if (asChildTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))
		{
			aoPropertyKey.add("NT012");
		}
		if (!aoPropertyKey.isEmpty())
		{
			HashMap loLastCommentReqProps = new HashMap();
			HashMap loProps = new HashMap();
			loLastCommentReqProps.put("appid", asAppId);
			loLastCommentReqProps.put("sectionId", asSectionId);
			loLastCommentReqProps.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
			String lsProviderComments = getLastProviderCommentGeneral(loLastCommentReqProps);
			loProps.put("Provider Comments from the Task Details", lsProviderComments);
			loProps.put("LINK", getPortalLink(aoRequest, "login"));
			aoProIdAsList.add(asProviderId);
			callNotificationService(aoPropertyKey, aoProIdAsList, loProps, asSessionUserId, asAppId,
					ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
		}
		LOG_OBJECT.Debug("Exit WorkfloDetailController.finishProviderAccRequestTask()");
	}

	/**
	 * This method Validates the button clicks at server side
	 * 
	 * @param aoObj Object Need to be checked
	 * @param asIdentifier Identifier
	 * @return Validated true or false
	 */
	private boolean validateAtServer(Object aoObj, String asIdentifier)
	{
		String lsObjCheck = (String) aoObj;
		if (asIdentifier.equalsIgnoreCase("assign"))
		{
			if (lsObjCheck == null || lsObjCheck.isEmpty() || lsObjCheck.equalsIgnoreCase("undefined"))
			{
				return false;
			}
			return true;
		}
		else if (asIdentifier.equalsIgnoreCase("childtask"))
		{
			if (lsObjCheck == null || lsObjCheck.isEmpty())
			{
				return false;
			}
			return true;
		}
		else
		{
			return true;
		}
	}

	/**
	 * This Method Fetches the Service WithDrawal ID from service Application
	 * Table
	 * 
	 * @param asServiceAppID Service Application ID
	 * @return Withdrawal ID
	 * @throws ApplicationException
	 */
	public String getServiceWithDrawalID(String asServiceAppID) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("ServiceAppID", asServiceAppID);
		if (asServiceAppID != null)
		{
			TransactionManager.executeTransaction(loChannel, "fetchWithdrawalID");
			return (String) loChannel.getData("result_fetchWithdrawalID");
		}
		return null;
	}

	/**
	 * This Method validates weather the inputed filter details are valid or
	 * not.
	 * @param aoRequest Action Request
	 * @param aoProps Filter Details
	 * @param asCurrentTab Current Logged in Tab
	 * @return boolean
	 */
	private boolean validatefFilterAtServer(ActionRequest aoRequest, HashMap aoProps, String asCurrentTab)
	{
		boolean lbAssignedToVerified = false;
		boolean lbTaskTypeVerified = false;
		boolean lbTaskStatusVerified = false;
		boolean lbValidated = false;
		HashMap loUserMap = (HashMap) aoRequest.getPortletSession().getAttribute("UserMap",
				PortletSession.APPLICATION_SCOPE);
		String lsAssignedTo = (String) aoProps.get(P8Constants.PROPERTY_PE_ASSIGNED_TO);
		String lsTaskType = (String) aoProps.get(P8Constants.PROPERTY_PE_TASK_TYPE);
		String lsTaskStatus = (String) aoProps.get(P8Constants.PROPERTY_PE_TASK_STATUS);
		// Changes for R5 - added approve psr task
		if (lsTaskType != null
				&& (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ALL_APPLICATIONS)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST)
						|| lsTaskType
								.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION)
						|| lsTaskType
								.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US)
						|| lsTaskType
								.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR)
						|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL) || lsTaskType
							.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_AWARD_AMOUNT)))
		{
			lbTaskTypeVerified = true;
		}

		if (asCurrentTab != null && asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_TASK_MANAGMENT))
		{

			Collection loCol = loUserMap.keySet();
			Iterator loItr = loCol.iterator();
			String lsHashMapKey = "";
			while (loItr.hasNext())
			{
				lsHashMapKey = (String) loItr.next();
				String lsKey = lsHashMapKey.substring(lsHashMapKey.indexOf("|") + 1, lsHashMapKey.length());
				if (lsKey.equalsIgnoreCase(lsAssignedTo)
						|| (lsAssignedTo != null && (lsAssignedTo.equalsIgnoreCase("All Staff")
								|| lsAssignedTo.equalsIgnoreCase("Unassigned") || lsAssignedTo
									.equalsIgnoreCase("Unassigned - Manager"))))
				{
					lbAssignedToVerified = true;
					break;
				}
			}
			if (lsAssignedTo == null)
			{
				return lbTaskTypeVerified;
			}
			else
			{
				if (lbTaskTypeVerified && lbAssignedToVerified)
				{
					lbValidated = true;
				}
			}
		}
		else if (asCurrentTab != null && asCurrentTab.equalsIgnoreCase(P8Constants.PROPERTY_PAGE_INBOX))
		{
			if (lsTaskStatus != null
					&& (lsTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
							|| lsTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
							|| lsTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_AT_PROVIDER)
							|| lsTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
							|| lsTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) || lsTaskStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
			{
				lbTaskStatusVerified = true;
			}

			if (lsTaskStatus == null)
			{
				return lbTaskTypeVerified;
			}
			else
			{
				if (lbTaskStatusVerified && lbTaskTypeVerified)
				{
					lbValidated = true;
				}
			}
		}
		return lbValidated;
	}

	/**
	 * This method handles all actions from task named
	 * ApplicationConstants.AWARD_APPROVAL_TASK
	 * 
	 * <ul>
	 * <li>Get the value of submit action from request</li>
	 * <li>If submit action value is "saveApproveAwardTaskDetails", call method
	 * saveApproveAwardTaskDetails() to save Approve award task details</li>
	 * <li>If submit action value is "reassignAwardApprovalTask", call method
	 * reassignAwardApprovalTask() to reassign Approve award task</li>
	 * <li>If submit action value is "confirmOverride", call method
	 * confirmOverride() to finish Approve award task for status
	 * "Override:Approved-No Financials"</li>
	 * <li>If submit action value is "finishAwardApprovalTask", call method
	 * finishAwardApprovalTask() to finish Approve award task for status
	 * "Approved" and "Returned"</li>
	 * <li>If submit action value is "viewProposalSummary", call method
	 * actionViewProposalSummary() to view proposal Summary of selected proposal
	 * </li>
	 * <li>If submit action value is null, render approve Award Task details jsp
	 * </li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request object
	 * @param aoResponse Action Response object
	 * @param asTaskId a string value of taskId
	 * @param asTaskName a string value of task name
	 */
	private void processActionForAwardApprovalTask(ActionRequest aoRequest, ActionResponse aoResponse, String asTaskId,
			String asTaskName)
	{
		LOG_OBJECT.Info("=====processActionForAwardApprovalTask====");
		LOG_OBJECT.Info("=====asTaskName :: "+asTaskName);
		String lsSubmitAction = aoRequest.getParameter(ApplicationConstants.SUBMIT_ACTION);
		LOG_OBJECT.Info("=====lsSubmitAction :: "+lsSubmitAction);
		if (null != lsSubmitAction)
		{
			// Start : R5 Added for AutoSave
			CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), asTaskId, HHSR5Constants.TASKS);
			// End : R5 Added for AutoSave
			if (lsSubmitAction.equalsIgnoreCase(ApplicationConstants.SAVE_AWARD_APPROVAL_TASK_DETAILS))
			{
				// call method saveApproveAwardTaskDetails() if submit action
				// value is "saveApproveAwardTaskDetails"
				saveApproveAwardTaskDetails(aoRequest, aoResponse);
				aoResponse.setRenderParameter(ApplicationConstants.AWARD_TASK_NAME, asTaskName);
			}
			else if (lsSubmitAction.equalsIgnoreCase(ApplicationConstants.REASSIGN_AWARD_APPROVAL_TASK))
			{
				// call method reassignAwardApprovalTask() if submit action
				// value is "reassignAwardApprovalTask"
				reassignAwardApprovalTask(aoRequest, aoResponse);
			}
			else if (lsSubmitAction.equalsIgnoreCase(ApplicationConstants.CONFIRM_OVERRIDE))
			{
				// call method confirmOverride() if submit action
				// value is "confirmOverride"
				confirmOverride(aoRequest, aoResponse);
				aoResponse.setRenderParameter(ApplicationConstants.AWARD_TASK_NAME, asTaskName);
			}
			else if (lsSubmitAction.equalsIgnoreCase(ApplicationConstants.FINISH_AWARD_APPROVAL_TASK))
			{
				// call method finishAwardApprovalTask() if submit action
				// value is "finishAwardApprovalTask"
				Boolean loErrorStatus = finishAwardApprovalTask(aoRequest, aoResponse);
				if (loErrorStatus)
				{
					aoResponse.setRenderParameter(ApplicationConstants.AWARD_TASK_NAME, asTaskName);
				}
			}
			else if (lsSubmitAction.equalsIgnoreCase(ApplicationConstants.RESERVE_TASK))
			{
				reserveAwardApprovalTask(aoRequest, aoResponse);
			}
		}
		else
		{
			// set wob number and task name in render parameters
			aoResponse.setRenderParameter(ApplicationConstants.WOB_NUMBER, asTaskId);
			aoResponse.setRenderParameter(ApplicationConstants.AWARD_TASK_NAME, asTaskName);
		}
	}

	/**
	 * This method is executed when an accelerator user opens Award Approval
	 * Task
	 * <ul>
	 * <li>Fetch required parameters from request like WorkflowId, UserId, User
	 * orgType</li>
	 * <li>Set channel with required parameters for fetching procurement and
	 * task details, evaluation results and scores, taskHistoryDetails, internal
	 * comments</li>
	 * <li>Invoke the transaction <b>fetchApproveAwardTaskDetails</b></li>
	 * <li>Set transaction outputs in request</li>
	 * </ul>
	 * @param aoRequest - Render Request
	 * @param aoResponse - Render Response
	 * @return model and view object containing jsp name
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private ModelAndView showAwardApprovalTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WOB_NUMBER);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		// R5 changes start
		HashMap loHmReqProps = CommonUtil.getTaskPropertiesHashMap();
		loHmReqProps.put(P8Constants.PROPERTY_WORKFLOW_IS_NEGOTIATION_REQUIRED, HHSConstants.EMPTY_STRING);
		// R5 changes end
		// fetching procurement and task details
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, loHmReqProps);
		// fetching Task history details
		HashMap loAuditMap = new HashMap();
		loAuditMap.put(ApplicationConstants.ENTITY_TYPE, ApplicationConstants.APPROVE_AWARD);
		loAuditMap.put(ApplicationConstants.EVENT_NAME, ApplicationConstants.TASK_CREATION);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loAuditMap);
		// fetching internal comments
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityType(ApplicationConstants.AWARD);
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
		// fetching unassigned Acco managers
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(ApplicationConstants.ROLE_MANAGER);
		loChannel.setData(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, loUserRoleList);
		loChannel.setData(ApplicationConstants.ORGID, ApplicationConstants.CITY);
		try
		{
			TransactionManager.executeTransaction(loChannel, ApplicationConstants.FETCH_AWARD_APPROVAL_TASK_DETAILS);
			// get task and procurement details from channel
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = CommonUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			// Get the permitted user list for reassign dropdown
			List<UserBean> loUserBeanList = (List<UserBean>) loChannel
					.getData(ApplicationConstants.PERMITTED_USER_LIST);
			aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP,
					CommonUtil.getReassignUserMap(loUserBeanList));
			// Set Attributes in request object
			aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_ID, loTaskDetailsBean.getProcurementId());
			aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_ID, loTaskDetailsBean.getTaskId());
			// Set Evaluation Results list in request object
			List<EvaluationBean> loEvalResultsList = (List<EvaluationBean>) loChannel
					.getData(ApplicationConstants.EVALUATION_RESULTS_LIST);
			aoRequest.setAttribute(ApplicationConstants.EVALUATION_RESULTS_LIST, loEvalResultsList);
			setAttributesinRequestForAwardTask(aoRequest, lsUserId, loChannel, loTaskDetailsBean);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occured while processing award approval task details", loExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		//Updated for Emergency build : 4.0.0.2 defect 8383
		CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(), lsWobNumber, HHSR5Constants.TASKS);
		return new ModelAndView(ApplicationConstants.AWARD_APPROVAL_TASK_JSP);
	}

	/**
	 * This method will set request attributes for award approval task
	 * 
	 * @param aoRequest RenderRequest object
	 * @param asUserId a string value of user Id
	 * @param aoChannel Channel object
	 * @param aoTaskDetailsBean TaskDetailsBean object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void setAttributesinRequestForAwardTask(RenderRequest aoRequest, String asUserId, Channel aoChannel,
			TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		TaskDetailsBean loTaskCommentsBean;
		// Set task history list in request object
		aoRequest.setAttribute(ApplicationConstants.TASK_HISTORY_LIST,
				aoChannel.getData(ApplicationConstants.TASK_HISTORY_LIST));
		// Set Internal Comments in request object
		loTaskCommentsBean = (TaskDetailsBean) aoChannel.getData(ApplicationConstants.AS_TASK_COMMENT);
		if (null != loTaskCommentsBean)
		{
			aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskCommentsBean.getInternalComment());
		}
		// Set finish drop down values
		String lsPreviousTaskStatus = null;
		if (null != aoTaskDetailsBean)
		{
			lsPreviousTaskStatus = aoTaskDetailsBean.getPreviousTaskStatus();
		}
		TreeMap<String, Object> aoTaskMap = new TreeMap<String, Object>();
		if ((null == lsPreviousTaskStatus || lsPreviousTaskStatus.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
				|| !(lsPreviousTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsPreviousTaskStatus
						.equalsIgnoreCase(ApplicationConstants.OVERRIDE)))
		{  
			//** Start QC 9674 R 9.5 Remove Approval (with Financials) Selection in the Dropdown of the Procurement Approve Awards Task 
			//aoTaskMap.put(PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
			//		ApplicationConstants.APPROVE_AWARD_TASK_APPROVED), ApplicationConstants.STATUS_APPROVED);
			//** End QC 9674 R 9.5 Remove Approval (with Financials) Selection in the Dropdown of the Procurement Approve Awards Task 
			aoTaskMap.put(PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					ApplicationConstants.APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS),
					ApplicationConstants.OVERRIDE_APPROVED_NO_FINANCIALS);
		}
		else if (lsPreviousTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			aoTaskMap.put(PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					ApplicationConstants.APPROVE_AWARD_TASK_APPROVED), ApplicationConstants.STATUS_APPROVED);
		}
		else if (lsPreviousTaskStatus.equalsIgnoreCase(ApplicationConstants.OVERRIDE))
		{
			aoTaskMap.put(PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					ApplicationConstants.APPROVE_AWARD_TASK_OVERRIDE_APPROVED_NO_FINANCIALS),
					ApplicationConstants.OVERRIDE_APPROVED_NO_FINANCIALS);
			lsPreviousTaskStatus = ApplicationConstants.OVERRIDE_APPROVED_NO_FINANCIALS;
		}
		aoTaskMap.put(PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
				ApplicationConstants.APPROVE_AWARD_TASK_RETURNED), ApplicationConstants.STATUS_RETURNED);
		aoRequest.setAttribute(ApplicationConstants.FINISH_STATUS_MAP, aoTaskMap);
		aoRequest.setAttribute(ApplicationConstants.PREVIOUS_STATUS, lsPreviousTaskStatus);
		// Check if User Id is same as User Assigned To
		if (null != aoTaskDetailsBean && null != aoTaskDetailsBean.getAssignedTo()
				&& aoTaskDetailsBean.getAssignedTo().equalsIgnoreCase(asUserId))
		{
			aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, false);
		}
		else
		{
			aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, true);
		}
		Procurement loProcurementBean = (Procurement) aoChannel.getData(ApplicationConstants.PROCUREMENT_SUMMARY);
		aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_BEAN, loProcurementBean);
		// Set error message and message type if an Exception occurs
		String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
	}

	/**
	 * This method handles action when save button is clicked from S244 screen
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId</li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method getBeanForSavingUserComments() for saving user comments</li>
	 * <li>Execute transaction with Id "saveApproveAwardTaskDetails"</li></li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	private void saveApproveAwardTaskDetails(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsProcurementId = aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsEntityId = null;
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			HhsAuditBean loHhsAuditBean = null;
			if (null != lsEvaluationPoolMappingId
					&& !lsEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				lsEntityId = lsEvaluationPoolMappingId;
			}
			else
			{
				lsEntityId = lsProcurementId;
			}
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loHhsAuditBean = CommonUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						ApplicationConstants.AWARD, lsEntityId, lsUserId, null, null, lsInternalComments);
				loChannel.setData(ApplicationConstants.AUDIT_BEAN, loHhsAuditBean);
				loChannel.setData(ApplicationConstants.AUDIT_STATUS, true);
			}
			if (null != loHhsAuditBean)
			{
				HashMap loHmWFProperties = new HashMap();
				loHmWFProperties.put(P8Constants.PE_WORKFLOW_LAST_MODIFIED_DATE, new Date());
				loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, loHmWFProperties);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
				loChannel.setData(ApplicationConstants.WORKFLOW_ID, lsWorkflowId);
				//Updated for Emergency build : 4.0.0.2 defect 8383
				CommonUtil.setChannelForAutoSaveData(loChannel, lsWorkflowId, HHSR5Constants.TASKS);
				TransactionManager.executeTransaction(loChannel, ApplicationConstants.SAVE_AWARD_APPROVAL_TASK_DETAILS);
			}
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving award approval task details: ", aoAppEx);
			String lsErrorMsg = "Error occurred while processing your request";
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while saving award approval task details: ", aoEx);
			String lsErrorMsg = "Error occurred while processing your request";
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		aoResponse.setRenderParameter(ApplicationConstants.WOB_NUMBER, lsWorkflowId);
	}

	/**
	 * This method handles resource request when Comments link is clicked from
	 * S244.
	 * 
	 * <ul>
	 * <li>Get proposal Id from request</li>
	 * <li>Set parameters in Comments map</li>
	 * <li>Execute transaction with Id "fetchSelectionCommentsForAwardTask" for
	 * fetching selection comments</li>
	 * <li>Set proposal Title, Comments and organization name in PrintWriter
	 * object and send to jsp</li>
	 * </ul>
	 * 
	 * @param aoRequest a Resource request object
	 * @param aoResponse a Resource response object
	 * @return model and view object containing jsp name
	 */
	@Override
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		/**** Begin QC 5446 ****/
		String fetchTypeAhead = aoRequest.getParameter("fetchTypeAhead");
		String lsSelectedAgency = aoRequest.getParameter("selectedAgency");

		if (lsSelectedAgency != null && !lsSelectedAgency.isEmpty())
		{
			try
			{
				exportAllTask(aoRequest, aoResponse);
			}
			catch (ApplicationException appExp)
			{
				LOG_OBJECT.Error("Exception Occurred while exporting task : " + appExp);
			}
		}
		else
		{
			if ("true".equals(fetchTypeAhead))
			{
				PrintWriter loOut = null;
				try
				{
					Channel loChannel = new Channel();
					String lsInputParam = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.QUERY);
					loChannel.setData(HHSConstants.PROCUREMENT_TITLE,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_TITLE));
					loChannel.setData(HHSConstants.INPUT_PARAM_MAP, lsInputParam);
					loChannel.setData(HHSConstants.QUERY_ID,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.QUERY_ID));
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_TYPEAHEAD_NAME_LIST);
					aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
					loOut = aoResponse.getWriter();
					loOut.print(HHSUtil.listMapToJSON(
							(List<Map<String, String>>) loChannel.getData(HHSConstants.NAME_LIST), lsInputParam,
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.KEY),
							HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.FEILD_VALUE), 3));
				}
				catch (Exception loExp)
				{
					LOG_OBJECT.Error("Exception Occurred while displaying type ahead : " + loExp);
				}
				finally
				{
					try
					{
						BaseControllerUtil.closingPrintWriter(loOut);
					}
					catch (ApplicationException appExp)
					{
						LOG_OBJECT.Error("Exception Occurred while displaying type ahead : " + appExp);
					}
				}
			}
			else
			/**** End QC 5446 ****/
			{
				try
				{
					String lsProposalId = aoRequest.getParameter(ApplicationConstants.PROPOSAL_ID);
					Channel loChannel = new Channel();
					HashMap<String, String> loCommentsMap = new HashMap<String, String>();
					loCommentsMap.put(ApplicationConstants.ENTITY_ID, lsProposalId);
					loCommentsMap.put(ApplicationConstants.ENTITY_TYPE, ApplicationConstants.SELECTION_COMMENTS);
					loChannel.setData(ApplicationConstants.COMMENTS_MAP, loCommentsMap);
					TransactionManager.executeTransaction(loChannel,
							ApplicationConstants.FETCH_SEL_COMMENT_FOR_AWARD_TASK);
					EvaluationBean loEvalBean = (EvaluationBean) loChannel
							.getData(ApplicationConstants.SELECTION_COMMENTS_LOWER);
					PrintWriter loOut = aoResponse.getWriter();
					StringBuffer loDataBuffer = new StringBuffer();
					if (null != loEvalBean)
					{
						aoResponse.setContentType(ApplicationConstants.TEXT_HTML);
						loDataBuffer.append("{\"selectionComments\": [{\"ProviderName\": \"");
						loDataBuffer.append(loEvalBean.getOrganizationName());
						loDataBuffer.append("\",\"ProposalTitle\": \"");
						loDataBuffer.append(loEvalBean.getProposalTitle());
						loDataBuffer.append("\", \"Comments\": \"");
						loDataBuffer.append(loEvalBean.getComments());
						loDataBuffer.append("\"}]}");

					}
					loOut.write(loDataBuffer.toString());
				}
				catch (Exception loExp)
				{
					LOG_OBJECT.Error("Error occurred while processing your request", loExp);
				}
			}
		}

		return null;
	}

	/**
	 * This method handles action when reassign button is clicked from S244
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * proposalId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	private void reassignAwardApprovalTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsProcurementId = aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.MANAGER_ROLE);
		String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.CHOOSEN_TAB);
		String lsEntityId = null;
		try
		{
			// Set user Id and user name to which task has been reassigned
			TaskDetailsBean loTaskBean = new TaskDetailsBean();
			loTaskBean.setWorkFlowId(lsWorkflowId);
			loTaskBean.setReassignUserId(lsAssignedToUserId);
			loTaskBean.setReassignUserName(lsAssignedToUserName);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskBean);
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			if (null != lsEvaluationPoolMappingId
					&& !lsEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				lsEntityId = lsEvaluationPoolMappingId;
			}
			else
			{
				lsEntityId = lsProcurementId;
			}
			// save internal comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						ApplicationConstants.APPROVE_AWARD, lsInternalComments, ApplicationConstants.APPROVE_AWARD,
						lsEntityId, lsUserId, ApplicationConstants.ACCELERATOR_AUDIT));
			}
			loAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					ApplicationConstants.APPROVE_AWARD, ApplicationConstants.TASK_ASSIGNED_TO
							+ ApplicationConstants.COLON_AOP + lsAssignedToUserName,
					ApplicationConstants.APPROVE_AWARD, lsEntityId, lsUserId, ApplicationConstants.ACCELERATOR_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			loChannel.setData(ApplicationConstants.REASSIGN_STATUS, true);
			TransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
			// for retaining filter values
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning award approval task : ", aoAppEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning award approval task : ", aoEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method handles action when yes button is clicked from
	 * "Confirm Override" overlay.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurementId, P8User session and previous status</li>
	 * <li>Get notification map by calling method
	 * getNotificationMapForProposalTask()</li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>add audit bean object for adding status change records to task
	 * history</li>
	 * <li>Execute transaction with id "confirmOverrideAwardApprovalWorkflow" to
	 * finish Award Approval task</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	private void confirmOverride(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		LOG_OBJECT.Info("======confirmOverride===");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.MANAGER_ROLE);
		String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.CHOOSEN_TAB);
		Boolean loIsFirstLaunch = Boolean.valueOf(aoRequest.getParameter(ApplicationConstants.IS_FIRST_LAUNCH));
		Boolean loIsFirstReach = Boolean.valueOf(aoRequest.getParameter(ApplicationConstants.IS_FIRST_REACHED));
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		// R5 change starts
		String lsApprovedStatus = null;
		String lsNegotiationRequired = aoRequest.getParameter(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
		lsNegotiationRequired = (lsNegotiationRequired == null ? HHSConstants.STRING_FALSE : lsNegotiationRequired);
		// R5 change ends
		try
		{
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			Channel loChannel = new Channel();
			// get notification map
			List<String> loAlertList = new ArrayList<String>();
			if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.STRING_FALSE))
			{
				loAlertList.add(ApplicationConstants.NT224);
				loAlertList.add(ApplicationConstants.AL223);
			}
			if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.STRING_TRUE))
			{
				loAlertList.add(HHSR5Constants.NT228);
				loAlertList.add(HHSR5Constants.AL227);
			}
			loChannel.setData(ApplicationConstants.LO_HM_NOTIFY_PARAM,
					getNotificationMapForAwardApprovalTask(aoRequest, loAlertList, lsProcurementId));
			HashMap<String, String> loProcMap = new HashMap<String, String>();
			loProcMap.put(ApplicationConstants.PROCUREMENT_ID, lsProcurementId);
			loProcMap.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
					ApplicationConstants.PROPERTIES_STATUS_CONSTANT, ApplicationConstants.STATUS_PROPOSAL_SELECTED));
			loChannel.setData(ApplicationConstants.PROCUREMENT_MAP, loProcMap);
			loChannel.setData(ApplicationConstants.PROCUREMENT_ID, lsProcurementId);
			// save internal comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						ApplicationConstants.APPROVE_AWARD, lsInternalComments, ApplicationConstants.APPROVE_AWARD,
						lsProcurementId, lsUserId, ApplicationConstants.ACCELERATOR_AUDIT));
			}
			// save accelerator comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(CommonUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						ApplicationConstants.AWARD, lsProcurementId, lsUserId, null, null, lsInternalComments));
			}
			// R5 Change starts

			if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.TRUE))
			{
				lsApprovedStatus = HHSR5Constants.APPROVED_OVERRIDE_WITH_NEGOTIATIONS;
			}
			else
			{
				lsApprovedStatus = HHSR5Constants.APPROVED_OVERRIDE_WITHOUT_NEGOTIATIONS;
			}
			// R5 Change ends
			// save status change records to task history
			StringBuffer loDataSb = new StringBuffer();
			loDataSb.append(ApplicationConstants.STATUS_CHANGED_FROM);
			loDataSb.append(ApplicationConstants.DOUBLE_QUOTE);
			loDataSb.append(ApplicationConstants.STATUS_IN_REVIEW);
			loDataSb.append(ApplicationConstants.STR);
			loDataSb.append(ApplicationConstants._TO_);
			loDataSb.append(ApplicationConstants.STR);
			loDataSb.append(lsApprovedStatus);
			loDataSb.append(ApplicationConstants.STR);

			loAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.PROC_STATUS_CHANGE,
					ApplicationConstants.APPROVE_AWARD, loDataSb.toString(), ApplicationConstants.APPROVE_AWARD,
					lsProcurementId, lsUserId, ApplicationConstants.ACCELERATOR_AUDIT));
			// R5 change starts
			LOG_OBJECT.Info("======Override wad confiremed :: processfinishTaskForConfirmOverride========");
			processfinishTaskForConfirmOverride(lsProcurementId, lsWorkflowId, loUserSession, loIsFirstLaunch,
					loAuditBeanList, loChannel, lsUserId, loIsFirstReach, lsEvaluationPoolMappingId,
					lsNegotiationRequired);
			// R5 change ends
			// for retaining filter values
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while finishing award approval task: ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling Exception other than Application Exception
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while finishing award approval task : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		aoResponse.setRenderParameter(ApplicationConstants.WOB_NUMBER, lsWorkflowId);
		aoResponse.setRenderParameter(ApplicationConstants.HANDLE_RENDER_ACTION, ApplicationConstants.CONFIRM_OVERRIDE);
	}

	/**
	 * This method will process finish task for task status confirm override
	 * Eaxcute Transaction id confirmOverrideAwardApprovalWorkflow
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param asProcurementId a string value of procurement Id
	 * @param asWorkflowId a string value of workflow Id
	 * @param aoUserSession P8UserSession object
	 * @param aiSelectedProposalCount a integer value of selected proposal count
	 * @param aoIsFirstLaunch a boolean value of first launch
	 * @param aoAuditBeanList audit bean list
	 * @param aoChannel Channel object
	 * @param asUserId UserId
	 * @param aoIsFirstReached IsFirstReached
	 * @param asEvaluationPoolMappingId EvaluationPoolMappingId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void processfinishTaskForConfirmOverride(String asProcurementId, String asWorkflowId,
			P8UserSession aoUserSession, Boolean aoIsFirstLaunch, List<HhsAuditBean> aoAuditBeanList,
			Channel aoChannel, String asUserId, Boolean aoIsFirstReached, String asEvaluationPoolMappingId,
			String asNegotiationRequired) throws ApplicationException
	{
		LOG_OBJECT.Info("======start processfinishTaskForConfirmOverride====");
		// save task details in filenet
		aoChannel.setData(ApplicationConstants.WORKFLOW_ID, asWorkflowId);
		aoChannel.setData(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.OVERRIDE);
		aoChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		aoChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, aoAuditBeanList);
		// set award details for updating award status and award approval
		// date
		HashMap<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loAwardMap.put(ApplicationConstants.AWARD_STATUS_ID, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT, ApplicationConstants.AWARD_REVIEW_APPROVED));
		loAwardMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		// R5 change starts
		if (asNegotiationRequired != null)
		{
			loAwardMap.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, asNegotiationRequired);
		}
		// R5 change ends
		aoChannel.setData(ApplicationConstants.AWARD_MAP, loAwardMap);
		// set details for inserting contract details
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		loContractMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loContractMap.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT, HHSR5Constants.STATUS_PROPOSAL_SELECTED));
		if (null != asNegotiationRequired && asNegotiationRequired.equalsIgnoreCase(HHSConstants.TRUE))
		{
			loContractMap.put(ApplicationConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
					ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_CONTRACT_PENDING_FINAL_AWARD_AMOUNT));
		}
		else
		{
			loContractMap.put(ApplicationConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
					ApplicationConstants.PROPERTIES_STATUS_CONSTANT, ApplicationConstants.PENDING_EPIN));
		}
		if (aoIsFirstLaunch)
		{
			loContractMap.put(ApplicationConstants.MODIFIED_FLAG, ApplicationConstants.ZERO);
		}
		else
		{
			loContractMap.put(ApplicationConstants.MODIFIED_FLAG, ApplicationConstants.ONE);
		}
		if (aoIsFirstReached)
		{
			aoChannel.setData(ApplicationConstants.MODIFIED_FLAG, true);
		}
		else
		{
			aoChannel.setData(ApplicationConstants.MODIFIED_FLAG, false);
		}
		loContractMap.put(ApplicationConstants.CONTRACT_TYPE_ID, ApplicationConstants.FIVE);
		loContractMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		loContractMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		aoChannel.setData(ApplicationConstants.CONTRACT_MAP, loContractMap);
		aoChannel.setData(ApplicationConstants.IS_SECOND_FLAG, !aoIsFirstReached);
		aoChannel.setData(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		aoChannel.setData(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		// set procurement details for updating procurement status
		Map loProcStatusMap = new HashMap();
		loProcStatusMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		loProcStatusMap.put(ApplicationConstants.PROC_STATUS_CODE, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
				ApplicationConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
		loProcStatusMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		aoChannel.setData(ApplicationConstants.STATUS_UPDATE_MAP, loProcStatusMap);
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loInputParamMap.put(ApplicationConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loInputParamMap.put(ApplicationConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
				ApplicationConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE));
		loInputParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		aoChannel.setData(ApplicationConstants.INPUT_PARAM_MAP, loInputParamMap);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(aoChannel, asWorkflowId, HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		
		TransactionManager.executeTransaction(aoChannel, ApplicationConstants.CONFIRM_OVERRIDE_AWARD_WORKFLOW);
		LOG_OBJECT.Info("======end processfinishTaskForConfirmOverride====");
	}

	/**
	 * This method generates notification map for Award Approval task depending
	 * upon input alert list
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoAlertList a list of notification ID to be sent like NT213
	 * @param asProcurementId Procurement Id
	 * @return a notification hashmap
	 */
	private HashMap<String, Object> getNotificationMapForAwardApprovalTask(ActionRequest aoRequest,
			List<String> aoAlertList, String asProcurementId)
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loAgencyIdList = new ArrayList<String>();
		loAgencyIdList.add(aoRequest.getParameter(ApplicationConstants.AGENCY_ID));
		StringBuffer lsBfApplicationUrl = new StringBuffer(256);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, aoAlertList);
		lsBfApplicationUrl.append(aoRequest.getScheme());
		lsBfApplicationUrl.append(ApplicationConstants.NOTIFICATION_HREF_1);
		lsBfApplicationUrl.append(aoRequest.getServerName());
		lsBfApplicationUrl.append(ApplicationConstants.NOTIFICATION_HREF_2);
		lsBfApplicationUrl.append(aoRequest.getServerPort());
		lsBfApplicationUrl.append(aoRequest.getContextPath());
		lsBfApplicationUrl.append(ApplicationConstants.EVALUATION_RESULTS_AGENCY_LINK);
		lsBfApplicationUrl.append(asProcurementId);
		lsBfApplicationUrl.append(ApplicationConstants.EVAL_POOL_MAPPING_ID_PARAM);
		lsBfApplicationUrl.append(lsEvaluationPoolMappingId);
		loRequestMap.put(ApplicationConstants.ACCELERATOR_COMMENTS,
				aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS));
		loRequestMap.put(ApplicationConstants.PROC_TITLE,
				aoRequest.getParameter(ApplicationConstants.PROCUREMENT_TITLE));
		for (String lsAlertId : aoAlertList)
		{
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loLinkMap.put(ApplicationConstants.LINK, lsBfApplicationUrl.toString());
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationMap.put(lsAlertId, loNotificationDataBean);
			loNotificationDataBean.setAgencyList(loAgencyIdList);
		}
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap.put(HHSConstants.ENTITY_ID, asProcurementId);
		loNotificationMap.put(HHSConstants.ENTITY_TYPE, ApplicationConstants.AWARD);
		return loNotificationMap;
	}

	/**
	 * This method handles action when finish button is clicked from S244
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurementId, P8User session and previous status</li>
	 * <li>Get notification map by calling method
	 * getNotificationMapForProposalTask()</li>
	 * <li>If internal comments are not null, add audit bean object by calling
	 * method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>add audit bean object for adding status change records to task
	 * history</li>
	 * <li>If award task status is "Approved", check if review levels has been
	 * set for procuring agency by executing transaction with id
	 * "fetchReviewLevelCB"</li>
	 * <li>If not, display top level error message on S244</li>
	 * <li>Else call method processFinishAwardApprovalTask() to finish Award
	 * Approval task</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object return boolean
	 * @return Boolean
	 */
	private Boolean finishAwardApprovalTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		
		LOG_OBJECT.Info("=======finishAwardApprovalTask======");
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsProcurementId = aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		String lsEntityId = null;
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsAwardTaskStatus = aoRequest.getParameter(ApplicationConstants.FINISH_STATUS);
		String lsAgencyId = aoRequest.getParameter(ApplicationConstants.AGENCY_ID);
		boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.MANAGER_ROLE);
		String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.CHOOSEN_TAB);
		String lsAwardReviewStatus = null;
		String lsNewStatus = null;
		Boolean loIsReturned = false;
		Boolean loErrorStatus = false;
		try
		{
			Channel loChannel = new Channel();
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			Boolean loFinishTask = false;
			if (null != lsEvaluationPoolMappingId
					&& !lsEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
			{
				lsEntityId = lsEvaluationPoolMappingId;
			}
			else
			{
				lsEntityId = lsProcurementId;
			}
			// save internal comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						ApplicationConstants.APPROVE_AWARD, lsInternalComments, ApplicationConstants.APPROVE_AWARD,
						lsEntityId, lsUserId, ApplicationConstants.ACCELERATOR_AUDIT));
			}
			// save accelerator comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(CommonUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						ApplicationConstants.AWARD, lsEntityId, lsUserId, null, null, lsInternalComments));
			}
			loErrorStatus = processFinishTaskForAwardTaskStatus(aoRequest, aoResponse, lsUserId, lsWorkflowId,
					lsProcurementId, loUserSession, lsAwardTaskStatus, lsAgencyId, lbManangerRole, lsChoosenTab,
					lsAwardReviewStatus, lsNewStatus, loIsReturned, loChannel, loAuditBeanList, loFinishTask,
					lsEntityId);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			loErrorStatus = true;
			LOG_OBJECT.Error("Exception Occured while finishing award approval task details: ", aoAppEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			loErrorStatus = true;
			LOG_OBJECT.Error("Exception Occured while finishing award approval task details: ", aoEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return loErrorStatus;
	}

	/**
	 * This method processes finish award approval task depending upon award
	 * task status selected from dropdown on S244 Transaction agencyId
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param asUserId a string value of user Id
	 * @param asWorkflowId a string value of workflowId
	 * @param asProcurementId a string value of procurement Id
	 * @param aoUserSession P8UserSession object
	 * @param asAwardTaskStatus a string value of award status
	 * @param asAgencyId a string value of agency Id
	 * @param abManangerRole a boolean value of manager role
	 * @param asChoosenTab a string value of choosen tab
	 * @param aiSelectedProposalCount an integer value of selected proposal
	 *            count
	 * @param asAwardReviewStatus a string value of award review status
	 * @param asNewStatus a string value of new status
	 * @param aoIsReturned a boolean value of is returned
	 * @param aoChannel Channel object
	 * @param aoAuditBeanList audit bean list
	 * @param aoFinishTask a boolean vaue of finish task return Boolean
	 * @param asEntityId Entity Id for Notification
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private Boolean processFinishTaskForAwardTaskStatus(ActionRequest aoRequest, ActionResponse aoResponse,
			String asUserId, String asWorkflowId, String asProcurementId, P8UserSession aoUserSession,
			String asAwardTaskStatus, String asAgencyId, boolean abManangerRole, String asChoosenTab,
			String asAwardReviewStatus, String asNewStatus, Boolean aoIsReturned, Channel aoChannel,
			List<HhsAuditBean> aoAuditBeanList, Boolean aoFinishTask, String asEntityId) throws ApplicationException
	{
		
		LOG_OBJECT.Info("=======processFinishTaskForAwardTaskStatus=====");
		Boolean loErrorStatus = false;
		if (null != asAwardTaskStatus
				&& (asAwardTaskStatus.equalsIgnoreCase(PropertyLoader.getProperty(
						ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
						ApplicationConstants.APPROVE_AWARD_TASK_RETURNED))))
		{
			// get notification map
			List<String> loAlertList = new ArrayList<String>();
			loAlertList.add(ApplicationConstants.NT208);
			loAlertList.add(ApplicationConstants.AL213);
			HashMap<String, Object> loNotificationMap = getNotificationMapForAwardApprovalTask(aoRequest, loAlertList,
					asProcurementId);
			List<String> loAgencyList = new ArrayList<String>();
			loAgencyList.add(asAgencyId);
			loNotificationMap.put(TransactionConstants.AGENCY_ID, loAgencyList);
			aoChannel.setData(ApplicationConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			asNewStatus = ApplicationConstants.STATUS_RETURNED;
			aoFinishTask = true;
			asAwardReviewStatus = PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					ApplicationConstants.AWARD_REVIEW_RETURNED);
		}
		else if (null != asAwardTaskStatus
				&& (asAwardTaskStatus.equalsIgnoreCase(PropertyLoader.getProperty(
						ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
						ApplicationConstants.APPROVE_AWARD_TASK_APPROVED))))
		{
			aoChannel.setData(ApplicationConstants.PROPERTY_PE_AGENCY_ID, asAgencyId);
			aoChannel.setData(ApplicationConstants.REVIEW_PROC_ID,
					ApplicationConstants.CONTRACT_CERTIFICATION_OF_FUNDS_ID);
			// check for review levels for procuring agency
			TransactionManager.executeTransaction(aoChannel, ApplicationConstants.FETCH_REVIEW_LEVEL_CB);
			Integer loReviewLevel = (Integer) aoChannel.getData(ApplicationConstants.REVIEW_LEVEL);
			if (null != loReviewLevel && loReviewLevel == 0)
			{
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, PropertyLoader.getProperty(
						ApplicationConstants.ERROR_MESSAGE_PROP_FILE, ApplicationConstants.REVIEW_LEVEL_NOT_SET));
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
				aoResponse.setRenderParameter(ApplicationConstants.AWARD_TASK_NAME,
						aoRequest.getParameter(ApplicationConstants.AWARD_TASK_NAME));
				aoResponse.setRenderParameter(ApplicationConstants.WOB_NUMBER, asWorkflowId);
				loErrorStatus = true;
			}
			else
			{
				// get notification map
				List<String> loAlertList = new ArrayList<String>();
				// R5 change starts
				String lsNegotiationRequired = aoRequest.getParameter(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
				if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.STRING_FALSE))
				{
					loAlertList.add(ApplicationConstants.NT224);
					loAlertList.add(ApplicationConstants.AL223);
				}
				if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.STRING_TRUE))
				{
					loAlertList.add(HHSR5Constants.NT228);
					loAlertList.add(HHSR5Constants.AL227);
				}
				// R5 change ends
				HashMap<String, Object> loNotificationMap = getNotificationMapForAwardApprovalTask(aoRequest,
						loAlertList, asProcurementId);
				List<String> loAgencyList = new ArrayList<String>();
				loAgencyList.add(asAgencyId);
				loNotificationMap.put(HHSConstants.ENTITY_ID, asEntityId);
				loNotificationMap.put(HHSConstants.ENTITY_TYPE, ApplicationConstants.AWARD);
				loNotificationMap.put(TransactionConstants.AGENCY_ID, loAgencyList);
				aoChannel.setData(ApplicationConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
				asNewStatus = ApplicationConstants.STATUS_APPROVED;
				aoFinishTask = true;
				aoIsReturned = true;
				asAwardReviewStatus = PropertyLoader.getProperty(ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
						ApplicationConstants.AWARD_REVIEW_APPROVED);
			}
		}
		if (aoFinishTask)
		{
			processFinishAwardApprovalTask(aoRequest, aoResponse, asUserId, asWorkflowId, asProcurementId,
					aoUserSession, abManangerRole, asChoosenTab, asAwardReviewStatus, aoIsReturned, aoChannel,
					asNewStatus, aoAuditBeanList);
		}
		return loErrorStatus;
	}

	/**
	 * This method will finish award approval task when award review status is
	 * Approved or Returned Execute transaction finishAwardApprovalWorkflow
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param asUserId a string value of user Id
	 * @param asWorkflowId a string value of workflow Id
	 * @param asProcurementId a string value of procurement Id
	 * @param aoUserSession P8UserSession object
	 * @param asPreviousStatus a string value of previous status
	 * @param abManangerRole a boolean value indicating manager role
	 * @param asChoosenTab a string value of choosen tab
	 * @param aiSelectedProposalCount an integer value indicating proposal with
	 *            selected status
	 * @param asAwardReviewStatus a string value of award review status
	 * @param aoIsStatusReturned a boolean value indicating is first launch
	 * @param aoChannel Channel object
	 * @param asNewStatus a string value of new selected status
	 * @param aoAuditBeanList audit bean list
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void processFinishAwardApprovalTask(ActionRequest aoRequest, ActionResponse aoResponse, String asUserId,
			String asWorkflowId, String asProcurementId, P8UserSession aoUserSession, boolean abManangerRole,
			String asChoosenTab, String asAwardReviewStatus, Boolean aoIsStatusReturned, Channel aoChannel,
			String asNewStatus, List<HhsAuditBean> aoAuditBeanList) throws ApplicationException
	{
		LOG_OBJECT.Info("=======processFinishAwardApprovalTask=====");
		// save status change records to task history
		StringBuffer loDataSb = new StringBuffer();
		Boolean loIsFirstLaunch = Boolean.valueOf(aoRequest.getParameter(ApplicationConstants.IS_FIRST_LAUNCH));
		Boolean loIsFirstReach = Boolean.valueOf(aoRequest.getParameter(ApplicationConstants.IS_FIRST_REACHED));
		String lsEvaluationPoolMappingId = aoRequest.getParameter(ApplicationConstants.EVALUATION_POOL_MAPPING_ID);
		String lsEntityId = null;
		// R5 change starts
		String lsNegotiationRequired = aoRequest.getParameter(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
		String lsApprovedStatus = asNewStatus;
		if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.TRUE)
				&& asNewStatus.equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
		{
			lsApprovedStatus = HHSR5Constants.STATUS_APPROVED_WITH_NEGOTIATIONS;
		}
		else if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.FALSE)
				&& asNewStatus.equalsIgnoreCase(HHSConstants.STATUS_APPROVED))
		{
			lsApprovedStatus = HHSR5Constants.STATUS_APPROVED_WITHOUT_NEGOTIATIONS;
		}
		// R5 change ends
		if (null != lsEvaluationPoolMappingId && !lsEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
		{
			lsEntityId = lsEvaluationPoolMappingId;
		}
		else
		{
			lsEntityId = asProcurementId;
		}
		loDataSb.append(ApplicationConstants.STATUS_CHANGED_FROM);
		loDataSb.append(ApplicationConstants.DOUBLE_QUOTE);
		loDataSb.append(ApplicationConstants.STATUS_IN_REVIEW);
		loDataSb.append(ApplicationConstants.STR);
		loDataSb.append(ApplicationConstants._TO_);
		loDataSb.append(ApplicationConstants.STR);
		loDataSb.append(lsApprovedStatus);
		loDataSb.append(ApplicationConstants.STR);
		aoAuditBeanList.add(CommonUtil.addAuditDataToChannel(ApplicationConstants.PROC_STATUS_CHANGE,
				ApplicationConstants.APPROVE_AWARD, loDataSb.toString(), ApplicationConstants.APPROVE_AWARD,
				lsEntityId, asUserId, ApplicationConstants.ACCELERATOR_AUDIT));
		// save task details in filenet
		aoChannel.setData(ApplicationConstants.WORKFLOW_ID, asWorkflowId);
		aoChannel.setData(P8Constants.PROPERTY_PE_TASK_STATUS, asNewStatus);
		aoChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		aoChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, aoAuditBeanList);
		// set award details for updating award status and award
		// approval date
		HashMap<String, String> loAwardMap = new HashMap<String, String>();
		loAwardMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loAwardMap.put(ApplicationConstants.AWARD_STATUS_ID, asAwardReviewStatus);
		loAwardMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		// R5 change starts
		loAwardMap.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, lsNegotiationRequired);
		// R5 change ends
		aoChannel.setData(ApplicationConstants.AWARD_MAP, loAwardMap);
		// set details for inserting contract details
		HashMap<String, String> loContractMap = new HashMap<String, String>();
		loContractMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		loContractMap.put(ApplicationConstants.STATUS_ID_KEY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT, ApplicationConstants.STATUS_PROPOSAL_SELECTED));
		if (loIsFirstLaunch)
		{
			loContractMap.put(ApplicationConstants.MODIFIED_FLAG, ApplicationConstants.ZERO);
		}
		else
		{
			loContractMap.put(ApplicationConstants.MODIFIED_FLAG, ApplicationConstants.ONE);
		}
		if (loIsFirstReach)
		{
			aoChannel.setData(ApplicationConstants.MODIFIED_FLAG, true);
		}
		else
		{
			aoChannel.setData(ApplicationConstants.MODIFIED_FLAG, false);
		}
		loContractMap.put(ApplicationConstants.CONTRACT_TYPE_ID, ApplicationConstants.ONE);
		loContractMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		if (null != lsNegotiationRequired && lsNegotiationRequired.equalsIgnoreCase(HHSConstants.TRUE))
		{
			loContractMap.put(ApplicationConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
					ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_CONTRACT_PENDING_FINAL_AWARD_AMOUNT));
		}
		else
		{
			loContractMap.put(ApplicationConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
					ApplicationConstants.PROPERTIES_STATUS_CONSTANT, ApplicationConstants.PENDING_WORKFLOW_LAUNCH));
		}
		loContractMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		aoChannel.setData(ApplicationConstants.CONTRACT_MAP, loContractMap);
		aoChannel.setData(ApplicationConstants.IS_SECOND_FLAG, !loIsFirstReach);
		aoChannel.setData(ApplicationConstants.IS_RETURNED, aoIsStatusReturned);
		aoChannel.setData(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		aoChannel.setData(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		// set procurement details for updating procurement status
		Map loProcStatusMap = new HashMap();
		loProcStatusMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		loProcStatusMap.put(ApplicationConstants.PROC_STATUS_CODE, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
				ApplicationConstants.STATUS_PROCUREMENT_SELECTIONS_MADE));
		loProcStatusMap.put(ApplicationConstants.PROCUREMENT_ID, asProcurementId);
		aoChannel.setData(ApplicationConstants.STATUS_UPDATE_MAP, loProcStatusMap);
		Map<String, Object> loInputParamMap = new HashMap<String, Object>();
		loInputParamMap.put(ApplicationConstants.EVALUATION_POOL_MAPPING_ID, lsEvaluationPoolMappingId);
		loInputParamMap.put(ApplicationConstants.PROCUREMENT_ID_KEY, asProcurementId);
		loInputParamMap.put(ApplicationConstants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTIES_STATUS_CONSTANT,
				ApplicationConstants.STATUS_COMPETITION_POOL_SELECTIONS_MADE));
		loInputParamMap.put(ApplicationConstants.KEY_SESSION_USER_ID, asUserId);
		aoChannel.setData(ApplicationConstants.INPUT_PARAM_MAP, loInputParamMap);
		// Start R5 : set EntityId and EntityName for AutoSave
		CommonUtil.setChannelForAutoSaveData(aoChannel, asWorkflowId, HHSR5Constants.TASKS);
		// End R5 : set EntityId and EntityName for AutoSave
		TransactionManager.executeTransaction(aoChannel, ApplicationConstants.FINISH_AWARD_APPROVAL_WORKFLOW);
		// for retaining filter values
		HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
				ApplicationConstants.FILTER_TAB);
		ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
				loFilterDetails, aoUserSession, abManangerRole, asChoosenTab, asUserId);
		FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
		aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
	}

	/**
	 * This method will reassign selected task to user himself who has logged
	 * into the system from task management view
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,username,
	 * P8User session, selected tab value</li>
	 * <li>Set user Id and user name to which task has been reassigned</li>
	 * <li>Execute transaction with Id "reassignWFTask" to save assigned to
	 * field for selected user</li>
	 * <li>Set session params for retaining filter params</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	private void reserveAwardApprovalTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);
			String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			String lsEmail = (String) ApplicationSession.getAttribute(aoRequest, true, "EmailAddress");
			ArrayList loWobNumberList = new ArrayList();
			loWobNumberList.add(lsWorkflowId);
			// save assigned to field for selected user
			Channel loChannel = new Channel();
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			assignTask(loChannel, loWobNumberList, lsUserId, lsUserName, lsEmail);
			// for retaining filter values
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while finishing award approval task details: ", aoAppEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// handling Exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while finishing award approval task details: ", aoEx);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method sets the success message which is displayed at the finish of
	 * Finalize Award Amount Task.It is called from finishFinalizeAmountTask.
	 * 
	 * @param aoResponse - ActionResponce Object
	 * @param asApproveAmountFlag - String Object
	 * @throws ApplicationException - ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("exportAllTask")
	public void exportAllTask(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		try
		{
			PortletSession loPortletSession = aoResourceRequest.getPortletSession();

			String lsOrgnizationType = (String) loPortletSession.getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsRole = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			HashMap loDefaultMap = new HashMap();
			loDefaultMap.put("statusId", PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.EXPORT_ALL_TASK_REQUESTED));
			if(null != aoResourceRequest.getParameter("selectedAgency").split("~")[0] && 
					!aoResourceRequest.getParameter("selectedAgency").split("~")[0].equalsIgnoreCase(HHSR5Constants.USER_CITY))
			{
				lsOrgnizationType = HHSR5Constants.USER_AGENCY;
			}
			loDefaultMap.put("userOrgType", lsOrgnizationType);
			loDefaultMap.put("orgType", aoResourceRequest.getParameter("selectedAgency").split("~")[0]);
			loDefaultMap.put("userRRole", lsRole);
			loDefaultMap.put("userId", lsUserId);
			//loDefaultMap.put("userOrgType", lsUserOrg);
			Channel loChannelObj = new Channel();
			loChannelObj.setData("loDefaultMap", loDefaultMap);
			TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.EXPORT_ALL_TASK,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in showFundingSubGrid while fetching data from database  ",
					aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in showFundingSubGrid while fetching data from database ", aoExe);
		}
	}

}
