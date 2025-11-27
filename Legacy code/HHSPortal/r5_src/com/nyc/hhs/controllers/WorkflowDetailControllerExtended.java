package com.nyc.hhs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is added for release 5 This Controller is added for Release 5
 * Approve PSR workflow This controller class is used to handle all the task
 * related activities for the Approve PSR.It is used to Finish,Reassign,Return
 * tasks.
 * 
 */
@Controller(value = "inboxControllerExtended")
@RequestMapping("view")
public class WorkflowDetailControllerExtended extends BaseControllerSM implements ResourceAwareController
{
	/**
	 * Log object which is used to log any error into log file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(WorkflowDetailControllerExtended.class);
/**
 * 
 * @param aoReq RenderRequest object
 * @param aoRes RenderResponse object
 * @return ModelAndView object
 */
	@RenderMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoReq, RenderResponse aoRes)

	{
		return null;
	}
/**
 * 
 * @param aoRequest ActionRequest object
 * @param aoResponse ActionResponse object
 */
	@ActionMapping
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		CommonUtil.setSessionForAutoSaveData(aoRequest.getPortletSession(),
				aoRequest.getParameter(HHSR5Constants.TASK_ID), HHSR5Constants.TASKS);
		String lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
		if (null != lsTaskType && lsTaskType.equalsIgnoreCase(HHSR5Constants.TASK_APPROVE_PSR))
		{
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.APPROVE_PSR_TASK_DETAILS);
			aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSR5Constants.INBOX_CONTROLLER_EXTENDED);
		}
		else
		{
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.APPROVE_AWARD_TASK_DETAILS);
			aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSR5Constants.INBOX_CONTROLLER_EXTENDED);
		}
	}

	@ModelAttribute("PsrBean")
	public PsrBean getPsrBean()
	{
		return new PsrBean();
	}

	/**
	 * <li>This method handles request and display PSR form S472 in Read only
	 * mode.</li> <li>This method also sets required attributes in request for
	 * re-assigned task, return tasks,insert comments</li>
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse object
	 * @return ModelAndView object
	 */
	@RenderMapping(params = "render_action=showApprovePsrTaskDetails")
	protected ModelAndView showApprovePsrTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.TASK_ID);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		// fetching procurement and task details
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, CommonUtil.getTaskPropertiesHashMap());
		// fetching Task history details
		HashMap loAuditMap = new HashMap();
		loAuditMap.put(ApplicationConstants.ENTITY_TYPE, HHSR5Constants.TASK_APPROVE_PSR);

		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loAuditMap);
		// fetching unassigned Acco managers
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(ApplicationConstants.ROLE_MANAGER);
		loUserRoleList.add(ApplicationConstants.ROLE_EXECUTIVE);
		loChannel.setData(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, loUserRoleList);
		loChannel.setData(ApplicationConstants.ORGID, ApplicationConstants.CITY);
		// Fetching PSR Details
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setWorkFlowId(lsWobNumber);
		loTaskDetailsBean.setUserId(lsUserId);
		loTaskDetailsBean.setIsTaskScreen(false);
		loTaskDetailsBean.setEntityType(HHSR5Constants.TASK_APPROVE_PSR);
		loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_PSR_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<Procurement> loServiceList = (List<Procurement>) loChannel.getData(HHSR5Constants.PSR_SERVICES_LIST);
			aoRequest.setAttribute(HHSR5Constants.PSR_SERVICES_LIST, loServiceList);
			// get task and procurement details from channel
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			loTaskDetailsBean = CommonUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			PsrBean loPsr = (PsrBean) loChannel.getData(HHSR5Constants.AO_PSR_BEAN);
			String isOpenEnded = loPsr.getIsOpenEndedRFP();
			String lsIsOpenEndedRfp = HHSConstants.NO;
			if (isOpenEnded.equalsIgnoreCase(HHSConstants.ONE))
				lsIsOpenEndedRfp = HHSConstants.YES;
			loTaskDetailsBean.setIsOpenEndedRfp(lsIsOpenEndedRfp);
			// Funding GRID display or not
			Boolean loShowGrid = (Boolean) loChannel.getData(HHSConstants.FUNDING_OPERATION_GRID);
			aoRequest.setAttribute(HHSR5Constants.SHOW_PSR_FUNDING_SUBGRID, loShowGrid);
			ProcurementCOF loProcurementCOF = (ProcurementCOF) loChannel.getData(HHSConstants.AO_PROCUREMENTCOFBEAN);
			// jq Grid changes
			if (null != loProcurementCOF)
			{
				String startDate = DateUtil.getDateByFormat(HHSConstants.NFCTH_DATE_FORMAT, HHSConstants.MMDDYYFORMAT,
						loPsr.getContractStartDatePlanned());
				String endDate = DateUtil.getDateByFormat(HHSConstants.NFCTH_DATE_FORMAT, HHSConstants.MMDDYYFORMAT,
						loPsr.getContractEndDateUpdated());
				CBGridBean loCBGridBean = new CBGridBean();
				loCBGridBean.setProcurementID(loPsr.getProcurementId());
				PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

				setContractDatesInSession(aoRequest.getPortletSession(), startDate, endDate);
				setAccountGridDataInSession(aoRequest);
				setFundingGridDataInSession(aoRequest);
				Map<String, Object> loFiscalYrMap = null;
				loFiscalYrMap = getContractFiscalYears(aoRequest);
				Integer loFiscalStartYr = (Integer) loFiscalYrMap.get(HHSConstants.LI_START_YEAR);
				loCBGridBean.setFiscalYearID(String.valueOf(loFiscalStartYr));
				aoRequest.setAttribute(HHSConstants.PROC_COF, loProcurementCOF);
			}
			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, ApplicationConstants.BOOLEAN_FALSE);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.SCREEN_READ_ONLY, ApplicationConstants.BOOLEAN_TRUE);
			}
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			setTaskDetailsBeanProperties(aoRequest, lsWobNumber, loChannel);
			aoRequest.setAttribute(HHSR5Constants.AO_PSR_JSP_BEAN, loPsr);
			aoRequest.setAttribute(HHSConstants.WOB_NUMBER, lsWobNumber);
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occured while processing award approval task details", loExp);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE,
					ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		return new ModelAndView(HHSR5Constants.APPROVE_PSR_JSP);
	}

	/**
	 * This method handles action when reassign button is clicked from S454.03
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If provider and internal comments are not null, add audit bean
	 * object by calling method getBeanForSavingUserComments() for saving
	 * comments in user comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>calls the transaction 'reassignWFTask'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignApprovePsrTask")
	protected void reassignApprovePsrTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{

		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = (String) ApplicationSession.getAttribute(aoRequest, false, HHSConstants.TASK_ID);
		String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		String lsTaskComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COMMENTS);
		try
		{
			// Retain Filter
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
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
			// save comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsInternalComments && !lsInternalComments.equals(HHSConstants.EMPTY_STRING)
					&& null != lsTaskComments && !lsTaskComments.equalsIgnoreCase(lsInternalComments))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.TASK_APPROVE_PSR, lsInternalComments, HHSR5Constants.TASK_APPROVE_PSR,
						lsProcurementId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
				loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
						HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, null, null, lsInternalComments));
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					HHSR5Constants.TASK_APPROVE_PSR, ApplicationConstants.TASK_ASSIGNED_TO + HHSConstants.COLON_AOP
							+ lsAssignedToUserName, HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId,
					HHSConstants.ACCELERATOR_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning approve PSR task : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning approve PSR task : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}
	/**
	 * This method will handle resource request since it implements ResourceAwareController
	 * 
	 * @param aoRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResponse decides the next execution flow
	 * @return null
	 * @throws Exception If an Exception occurs
	 */
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse) throws Exception
	{
		return null;
	}

	/**
	 * This method handles call when save button is clicked from S455.10 It
	 * stores Accelerator comments
	 * @param aoRequest - ResourceRequest
	 * @param aoResponse - ResourceResponse object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ResourceMapping("savePsrComments")
	protected void savePsrApproveComments(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INTERNAL_COMMENTS);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsTaskComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COMMENTS);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsWobNumber = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WOB_NUMBER);
		String asTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
		Boolean loUpdateStatus = HHSConstants.BOOLEAN_FALSE;
		HhsAuditBean loAuditBean = new HhsAuditBean();
		if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING) && null != lsTaskComments
				&& !lsTaskComments.equalsIgnoreCase(lsComments))
		{
			loAuditBean.setInternalComments(lsComments);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setWorkflowId(lsWobNumber);
			loAuditBean.setEntityType(HHSR5Constants.TASK_APPROVE_PSR);
			loAuditBean.setEntityId(lsProcurementId);
			loAuditBean.setData(lsComments);
			loAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
			loAuditBean.setTaskId(asTaskId);
			loUpdateStatus = HHSConstants.BOOLEAN_TRUE;
		}
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSR5Constants.UPDATE_STATUS, loUpdateStatus);
			// Add comments to Task History
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING) && null != lsTaskComments
					&& !lsTaskComments.equalsIgnoreCase(lsComments))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.TASK_APPROVE_PSR, lsComments, HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId,
						lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			}
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_APPROVE_PSR_COMMENTS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Accelerator : ", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Accelerator : ", loExp);
		}
	}

	/**
	 * This method handles action when finish button is clicked from S455.05
	 * with Returned for Revision selected in S455.04
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set required details in channel</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for reassign PSR Workflow.</li>
	 * <li>calls the transaction 'returnApprovePsr'</LI>
	 * </ul>
	 * @param aoPsrBean PsrBean object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	@ActionMapping(params = "submit_action=returnApprovePSR")
	protected void returnApprovePSR(@ModelAttribute("PsrBean") PsrBean aoPsrBean, ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException
	{
		String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
		Channel loChannel = new Channel();
		String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.STATUS_PSR_RETURN_FOR_REVISION);
		String lsTaskComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COMMENTS);
		try
		{
			// Retain Filter
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			// Notification
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			getNotificationMapForReturnApprovePsr(aoRequest, loChannel, lsUserId);
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			String lsTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
			String lsProcurementId = (String) aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
			String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INTERNAL_COMMENTS);
			HhsAuditBean loAuditBean = new HhsAuditBean();
			Boolean loCommentStatus = HHSConstants.BOOLEAN_FALSE;
			if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING) && null != lsTaskComments
					&& !lsTaskComments.equalsIgnoreCase(lsComments))
			{
				loAuditBean.setInternalComments(lsComments);
				loAuditBean.setUserId(lsUserId);
				loAuditBean.setWorkflowId(lsWobNumber);
				loAuditBean.setEntityType(HHSR5Constants.TASK_APPROVE_PSR);
				loAuditBean.setEntityId(lsProcurementId);
				loAuditBean.setData(lsComments);
				loAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
				loAuditBean.setTaskId(lsTaskId);
				loCommentStatus = HHSConstants.BOOLEAN_TRUE;
			}
			// Setting Required Fields
			loTaskDetailsBean.setTaskId(lsTaskId);
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setTaskStatus(HHSConstants.TASK_IN_REVIEW);
			// Finish Task Required Fields
			Map loHmWFReqProps = new HashMap();
			loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setUserId(lsUserId);
			loTaskDetailsBean.setTaskName(HHSR5Constants.TASK_APPROVE_PSR);
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSConstants.STATUS_COLUMN, loCommentStatus);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			// Task View History WorkFlow
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.TASK_APPROVE_PSR, HHSR5Constants.STATUS_RETURN_FOR_REVISION,
					HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.TASK_COMPLETE_PSR, HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCO,
					HHSR5Constants.TASK_COMPLETE_PSR, lsProcurementId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.TASK_COMPLETE_PSR, HHSR5Constants.STATUS_RETURN_TO_REVIEW,
					HHSR5Constants.TASK_COMPLETE_PSR, lsProcurementId, lsUserId, HHSConstants.AGENCY_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			// Updating statusId for PSR
			loChannel.setData(HHSConstants.STATUS_ID, lsStatusId);
			loChannel.setData(ApplicationConstants.PROCUREMENT_ID, lsProcurementId);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.RETURN_APPROVE_PSR,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Returning approve PSR task : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Returning approve PSR task : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * This method handles action when Submit button is clicked from S480.10
	 * 
	 * <ul>
	 * <li>Validate Username/Password in Approve PSR Screen.</li>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set task details in channel for Complete PSR task</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for Complete PSR Workflow.</li>
	 * <li>calls the transaction 'approvePsrTask'</LI>
	 * </ul>
	 * @param aoPsrBean PsrBean object
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	@ActionMapping(params = "submit_action=finishApprovePSR")
	protected void finishApprovePSR(@ModelAttribute("PsrBean") PsrBean aoPsrBean, ActionRequest aoRequest,
			ActionResponse aoResponse) throws ApplicationException
	{
		String lsWobNumber = aoRequest.getParameter(HHSConstants.WOB_NUMBER);
		// validate user
		String lsComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsUserName = aoRequest.getParameter(HHSConstants.USER);
		String lsPassword = aoRequest.getParameter(HHSConstants.PASSWORD);
		Map loAuthenticateMap = validateUser(lsUserName, lsPassword, aoRequest);
		Boolean loAuthStatus = (Boolean) loAuthenticateMap.get(HHSConstants.IS_VALID_USER);
		if (loAuthStatus)
		{
			// validate user Workflow
			String lsTaskId = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.TASK_ID);
			String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			// Setting Required Fields
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setTaskId(lsTaskId);
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
			try
			{
				// Retain Filter
				boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.MANAGER_ROLE);
				String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.CHOOSEN_TAB);
				HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.FILTER_TAB);
				Channel loChannel = new Channel();
				String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSR5Constants.STATUS_PSR_APPROVE_SUBMITTED);
				String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
				// Notification
				getNotificationMapForFinishApprovePsr(aoRequest, loChannel, lsUserId, lsProcurementId);
				loTaskDetailsBean.setUserId(lsUserId);
				loTaskDetailsBean.setTaskName(HHSR5Constants.TASK_APPROVE_PSR);
				// Setting Required Fields
				Map loHmWFReqProps = new HashMap();
				loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_APPROVED);
				P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				loTaskDetailsBean.setWorkFlowId(lsWobNumber);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
				loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
				// Task View History WorkFlow
				List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
						HHSR5Constants.TASK_APPROVE_PSR, HHSConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
								+ HHSConstants.STR + HHSConstants.TASK_IN_REVIEW + HHSConstants.STR
								+ HHSConstants.SPACE + HHSR5Constants.STR_TO_TASK + HHSConstants.SPACE
								+ HHSConstants.STR + HHSR5Constants.STATUS_SUBMITTED_FOR_APPROVAL + HHSConstants.STR,
						HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
				if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING))
				{
					loAuditBeanList.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWobNumber,
							HHSR5Constants.TASK_APPROVE_PSR, lsProcurementId, lsUserId, null, null, lsComments));
					loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
							HHSR5Constants.TASK_APPROVE_PSR, lsComments, HHSR5Constants.TASK_APPROVE_PSR,
							lsProcurementId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
				}
				loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loAuditBeanList);
				// Updating statusId for PSR
				loChannel.setData(HHSConstants.STATUS_ID, lsStatusId);
				loChannel.setData(ApplicationConstants.PROCUREMENT_ID, lsProcurementId);
				loChannel.setData(HHSR5Constants.PDF_FLAG, HHSConstants.ONE);
				// Start R5 : set EntityId and EntityName for AutoSave
				CommonUtil.setChannelForAutoSaveData(loChannel, lsWobNumber, HHSR5Constants.TASKS);
				// Start R5 : set EntityId and EntityName for AutoSave
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.APPROVE_PSR_TASK,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
						loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
				FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
				aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Exception Occured while Approve PSR task : ", loExp);
				String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);

			}
			// handling exception other than Application Exception.
			catch (Exception loExp)
			{
				LOG_OBJECT.Error("Exception Occured while Approve PSR task : ", loExp);
				String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
				aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
		}
		else
		{
			String lsErrormsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.MESSAGE_M38);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrormsg);
			aoResponse.setRenderParameter(HHSR5Constants.TASK_ID, lsWobNumber);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.APPROVE_PSR_TASK_DETAILS);
			aoResponse.setRenderParameter(HHSConstants.CONTROLLER_ACTION, HHSR5Constants.INBOX_CONTROLLER_EXTENDED);
		}
	}

	/**
	 * This method sets the required Task History Details, internal comments and
	 * other required attributes to request.This is called by method
	 * 'showApprovePsrTaskDetails'
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param asWobNumber - Wobnumber String
	 * @param aoChannel - Channel object
	 * @return void
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	private void setTaskDetailsBeanProperties(RenderRequest aoRequest, String asWobNumber, Channel aoChannel)
			throws ApplicationException
	{
		TaskDetailsBean loTaskDetailBean;
		HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) aoChannel
				.getData(ApplicationConstants.TASK_DETAIL_MAP);
		TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, asWobNumber);
		// Get the permitted user list for reassign dropdown
		List<UserBean> loUserBeanList = (List<UserBean>) aoChannel.getData(ApplicationConstants.PERMITTED_USER_LIST);
		aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
		aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_ID, loTaskDetailsBean.getProcurementId());
		aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID, asWobNumber);
		aoRequest.setAttribute(ApplicationConstants.TASK_ID, loTaskDetailsBean.getTaskId());
		aoRequest.setAttribute(ApplicationConstants.TASK_HISTORY_LIST,
				aoChannel.getData(ApplicationConstants.TASK_HISTORY_LIST));
		loTaskDetailBean = (TaskDetailsBean) aoChannel.getData(HHSConstants.AS_TASK_COMMENT);
		aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskDetailBean.getInternalComment());
		aoRequest.setAttribute(HHSConstants.COMMENTS, loTaskDetailBean.getInternalComment());
		aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE));
	}

	/**
	 * <ul>
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
	 * @param aoRequest ActionRequest
	 * @param aoChannel Channel object
	 * @param asUserId a string value of user Id
	 */
	private void getNotificationMapForReturnApprovePsr(ActionRequest aoRequest, Channel aoChannel, String asUserId)
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		StringBuffer loApplicationUrl = new StringBuffer(256);
		loApplicationUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
				.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
				.append(aoRequest.getContextPath()).append(HHSConstants.AGENCY_TASK_INBOX_URL);
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSR5Constants.AL407);
		loNotificationAlertList.add(HHSR5Constants.NT407);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		NotificationDataBean loNotificationAL407 = new NotificationDataBean();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
		loAgencyLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
		loNotificationAL407.setAgencyLinkMap(loAgencyLinkMap);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
		loNotificationMap.put(HHSR5Constants.AL407, loNotificationAL407);
		loNotificationMap.put(HHSR5Constants.NT407, loNotificationAL407);
		aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
	}

	/**
	 * <ul>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the agency list,
	 * linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoChannel Channel object
	 * @param asUserId a string value of user Id
	 * @param asProcId ProcurementID
	 */
	private void getNotificationMapForFinishApprovePsr(ActionRequest aoRequest, Channel aoChannel, String asUserId,
			String asProcId)
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		StringBuffer loApplicationUrl = new StringBuffer(256);
		loApplicationUrl.append(aoRequest.getScheme()).append(HHSConstants.NOTIFICATION_HREF_1)
				.append(aoRequest.getServerName()).append(HHSConstants.COLON).append(aoRequest.getServerPort())
				.append(aoRequest.getContextPath()).append(HHSConstants.PROCUREMENT_SUMMARY_AGENCY_URL)
				.append(asProcId);
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(HHSR5Constants.AL406);
		loNotificationAlertList.add(HHSR5Constants.NT406);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		NotificationDataBean loNotificationAL406 = new NotificationDataBean();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
		loAgencyLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
		loNotificationAL406.setAgencyLinkMap(loAgencyLinkMap);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
		loNotificationMap.put(HHSR5Constants.AL406, loNotificationAL406);
		loNotificationMap.put(HHSR5Constants.NT406, loNotificationAL406);
		aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
	}

	/**
	 * <li>This method handles request and display Approve Award Amount Task.</li>
	 * <li>If the user is not assigned user,then<li> <li>The Screen is display
	 * in read-only mode for unassigned user.<li> <li>If the User is assigned
	 * user,The user can update/insert PSR details</li> <li>This method also
	 * sets required attributes in request for re-assigned task, return
	 * tasks,insert comments</li> calls the transaction
	 * 'fetchFinalizeAwardTaskDetails'
	 * 
	 * @param aoRequest - RenderRequest object
	 * @param aoResponse - RenderResponse
	 * @return ModelAndView object
	 */
	@RenderMapping(params = "render_action=showApproveAwardTaskDetails")
	protected ModelAndView showApproveAwardTaskDetails(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsWobNumber = HHSPortalUtil.parseQueryString(aoRequest, HHSR5Constants.TASK_ID);
		P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		HashMap loHmTaskDetails = HHSUtil.getTaskPropertiesHashMap();
		loHmTaskDetails.put(P8Constants.PE_WORKFLOW_CONTRACT_ID, HHSR5Constants.EMPTY_STRING);
		List<EvaluationBean> loEvalListOthers = null;
		// fetching procurement and task details
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.WOB_NUMBER, lsWobNumber);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		loChannel.setData(ApplicationConstants.REQ_PROPS_TASK_HASHMAP, loHmTaskDetails);
		// fetching Task history details
		HashMap loAuditMap = new HashMap();
		loAuditMap.put(ApplicationConstants.ENTITY_TYPE, HHSR5Constants.APPROVE_AWARD_AMOUNT);
		loAuditMap.put(ApplicationConstants.EVENT_NAME, ApplicationConstants.TASK_CREATION);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loAuditMap);
		// fetching internal comments
		TaskDetailsBean loTaskDetailBean = new TaskDetailsBean();
		loTaskDetailBean.setEntityType(HHSR5Constants.APPROVE_AWARD_AMOUNT);
		loTaskDetailBean.setEntityId(lsProcurementId);
		loTaskDetailBean.setWorkFlowId(lsWobNumber);
		loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN_KEY, loTaskDetailBean);
		// fetching unassigned Acco managers
		List<String> loUserRoleList = new ArrayList<String>();
		loUserRoleList.add(ApplicationConstants.ROLE_MANAGER);
		loUserRoleList.add(ApplicationConstants.ROLE_EXECUTIVE);
		loChannel.setData(HHSR5Constants.USER_ROLE_LIST, loUserRoleList);
		loChannel.setData(ApplicationConstants.ORGID, ApplicationConstants.CITY);
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_FINALIZE_AWARD_TASK_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			// get task and procurement details from channel
			HashMap<String, Object> loTaskDetailMap = (HashMap<String, Object>) loChannel
					.getData(ApplicationConstants.TASK_DETAIL_MAP);
			TaskDetailsBean loTaskDetailsBean = HHSUtil.getTaskDetailsBeanFromMap(loTaskDetailMap, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			// Get the permitted user list for reassign dropdown
			List<UserBean> loUserBeanList = (List<UserBean>) loChannel
					.getData(ApplicationConstants.PERMITTED_USER_LIST);
			aoRequest.setAttribute(ApplicationConstants.REASSIGN_USER_MAP, HHSUtil.getReassignUserMap(loUserBeanList));
			// Set Attributes in request object
			aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_ID, loTaskDetailsBean.getProcurementId());
			aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID, lsWobNumber);
			aoRequest.setAttribute(ApplicationConstants.TASK_ID, loTaskDetailsBean.getTaskId());
			// Set Evaluation Results list in request object
			HashMap loEvalMap = (HashMap) loChannel.getData(ApplicationConstants.EVALUATION_RESULTS_LIST);
			List<EvaluationBean> loEvalResultsList = (List<EvaluationBean>) loEvalMap
					.get(HHSR5Constants.FINALIZE_SAME_PROVIDER_LIST);
			if (null != loEvalMap.get(HHSR5Constants.FINALIZE_OTHER_PROVIDER_LIST))
			{
				loEvalListOthers = (List<EvaluationBean>) loEvalMap.get(HHSR5Constants.FINALIZE_OTHER_PROVIDER_LIST);
			}
			aoRequest.setAttribute(ApplicationConstants.EVALUATION_RESULTS_LIST, loEvalResultsList);
			aoRequest.setAttribute(HHSConstants.EVALUATION_LIST, loEvalListOthers);
			TaskDetailsBean loTaskCommentsBean;
			// Set task history list in request object
			aoRequest.setAttribute(ApplicationConstants.TASK_HISTORY_LIST,
					loChannel.getData(ApplicationConstants.TASK_HISTORY_LIST));
			// Set Internal Comments in request object
			loTaskCommentsBean = (TaskDetailsBean) loChannel.getData(ApplicationConstants.AS_TASK_COMMENT);
			if (null != loTaskCommentsBean)
			{
				aoRequest.setAttribute(ApplicationConstants.INTERNAL_COMMENTS, loTaskCommentsBean.getInternalComment());
				aoRequest.setAttribute(ApplicationConstants.COMMENTS, loTaskCommentsBean.getInternalComment());
			}
			// Check if User Id is same as User Assigned To
			if (null != loTaskDetailsBean && null != loTaskDetailsBean.getAssignedTo()
					&& loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
			{
				aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, false);
			}
			else
			{
				aoRequest.setAttribute(ApplicationConstants.SCREEN_READ_ONLY, true);
			}
			Procurement loProcurementBean = (Procurement) loChannel.getData(ApplicationConstants.PROCUREMENT_SUMMARY);
			aoRequest.setAttribute(ApplicationConstants.PROCUREMENT_BEAN, loProcurementBean);
			// Set error message and message type if an Exception occurs
			HashMap loHmTaskDetail = (HashMap) loChannel.getData(ApplicationConstants.TASK_DETAIL_MAP);
			HashMap loHmTaskDetailInfo = (HashMap) loHmTaskDetail.get(lsWobNumber);
			aoRequest.setAttribute(HHSConstants.ENTITY_ID,
					(String) loHmTaskDetailInfo.get(P8Constants.PROPERTY_PE_ENTITY_ID));
			String lsErrorMsg = (String) aoRequest.getParameter(ApplicationConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
		// Handling Application Exception thrown by Transaction layer
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoAppEx);
			setGenericErrorMessage(aoRequest);
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while processing configure award task details", aoEx);
			setGenericErrorMessage(aoRequest);
		}
		return new ModelAndView("approveAwardAmountTask");
	}

	/**
	 * This method handles action when reassign button is clicked from S452.03
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, ReassignedTo, ReassignedToUserName</li>
	 * <li>Set task details in channel for reassigning task</li>
	 * <li>If provider and internal comments are not null, add audit bean object
	 * by calling method addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If provider and internal comments are not null, add audit bean
	 * object by calling method getBeanForSavingUserComments() for saving
	 * comments in user comments table</li>
	 * <li>Add audit bean object for reassigning task by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>calls the transaction 'reassignWFTask'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignApproveAwardAmountTask")
	protected void reassignApproveAwardAmountTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsWorkflowId = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String lsTaskId = aoRequest.getParameter(ApplicationConstants.TASK_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsAssignedToUserId = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO);
		String lsAssignedToUserName = aoRequest.getParameter(ApplicationConstants.REASSIGNED_TO_USER_NAME);
		try
		{
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
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
			// save comments to task history
			String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
			String lsPreviousComments = aoRequest.getParameter(ApplicationConstants.COMMENTS);
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null == lsPreviousComments && null != lsInternalComments
					&& !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING))
			{
				loAuditBeanList
						.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
								HHSR5Constants.APPROVE_AWARD_AMOUNT, lsProcurementId, lsUserId, null, null,
								lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.APPROVE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.APPROVE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			}
			// save agency comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING)
					&& null != lsPreviousComments && !lsPreviousComments.equalsIgnoreCase(lsInternalComments))
			{
				loAuditBeanList
						.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWorkflowId,
								HHSR5Constants.APPROVE_AWARD_AMOUNT, lsProcurementId, lsUserId, null, null,
								lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.APPROVE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.APPROVE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			}
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(ApplicationConstants.TASK_ASSIGNMENT,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, HHSConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AOP
							+ lsAssignedToUserName, HHSR5Constants.APPROVE_AWARD_AMOUNT, lsEntityId, lsUserId,
					HHSR5Constants.ACCELERATOR_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, ApplicationConstants.REASSIGN_WF_TASK);
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		// handling exception other than Application Exception.
		catch (ApplicationException loExp)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(ApplicationConstants.WORKFLOW_ID, lsWorkflowId);
			loParamMap.put(HHSR5Constants.TASK_ID, lsTaskId);
			loParamMap.put(HHSConstants.ENTITY_ID, lsEntityId);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, loExp);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while reassigning complete PSR task details: ", loExp);
			setExceptionMessageFromAction(aoResponse, HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}

	/**
	 * This method handles call when save button is clicked from S452.10 It
	 * stores Accelerator comments.
	 * 
	 * @param aoRequest - ResourceRequest object
	 * @param aoResponse - ResourceRequest object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ResourceMapping("saveApproveAwardComments")
	protected void saveApproveAwardComments(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INTERNAL_COMMENTS);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsProcurementId = (String) aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		String asTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
		Boolean loUpdateStatus = HHSConstants.BOOLEAN_FALSE;
		HhsAuditBean loAuditBean = new HhsAuditBean();
		if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING))
		{
			loAuditBean.setInternalComments(lsComments);
			loAuditBean.setUserId(lsUserId);
			loAuditBean.setWorkflowId(lsWobNumber);
			loAuditBean.setEntityType(HHSR5Constants.APPROVE_AWARD_AMOUNT);
			loAuditBean.setEntityId(lsProcurementId);
			loAuditBean.setData(lsComments);
			loAuditBean.setAuditTableIdentifier(HHSConstants.NON_AUDIT_COMMENTS);
			loAuditBean.setTaskId(asTaskId);
			loUpdateStatus = HHSConstants.BOOLEAN_TRUE;
		}
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AUDIT_BEAN, loAuditBean);
			loChannel.setData(HHSR5Constants.UPDATE_STATUS, loUpdateStatus);
			// Add comments to Task History
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (null != lsComments && !lsComments.equals(HHSConstants.EMPTY_STRING))
			{
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.APPROVE_AWARD_AMOUNT, lsComments, HHSR5Constants.APPROVE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_APPROVE_PSR_COMMENTS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Accelerator : ", loExp);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while saving comments for Accelerator : ", loExp);
		}
	}

	/**
	 * This method handles action when finish button is clicked from S453.05
	 * with Returned for Revision selected in S453.04
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set required details in channel</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for reassign PSR Workflow.</li>
	 * <li>calls the transaction 'returnApproveAwardAmount'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	@ActionMapping(params = "submit_action=returnApproveAwardAmountTask")
	protected void returnApproveAwardAmountTask(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		Channel loChannel = new Channel();
		String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.STATUS_APPROVE_FINALIZE_RETURN);
		String lsContractStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.STATUS_CONTRACT_PENDING_FINAL_AWARD_AMOUNT_RETURNED);
		String lsPreviousComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COMMENTS);
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProviderId = aoRequest.getParameter(HHSConstants.PROVIDER_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		Boolean loCommentAuditStatus = HHSConstants.BOOLEAN_FALSE;
		String lsProviderName = aoRequest.getParameter(HHSConstants.ORGANIZATION_LEGAL_NAME);
		String lsCompetitionPoolName = aoRequest.getParameter(HHSConstants.COMPETITION_POOL);
		try
		{
			// Reatain Filters
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			// Notification
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			getNotificationMapForReturnAwardAmount(aoRequest, aoResponse, loChannel, lsUserId, lsProviderName,
					lsCompetitionPoolName, lsInternalComments);
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			String lsTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
			String lsProcurementId = (String) aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
			if (null == lsPreviousComments && null != lsInternalComments
					&& !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING))
			{
				loCommentAuditStatus = true;
			}
			// save agency comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING)
					&& null != lsPreviousComments && !lsPreviousComments.equalsIgnoreCase(lsInternalComments))
			{
				loCommentAuditStatus = true;
			}
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (loCommentAuditStatus)
			{
				loAuditBeanList
						.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWobNumber,
								HHSR5Constants.APPROVE_AWARD_AMOUNT, lsProcurementId, lsUserId, null, null,
								lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.APPROVE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.APPROVE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			// Setting Required Fields
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setTaskStatus(HHSConstants.TASK_IN_REVIEW);
			Map loAwardMap = new HashMap();
			loAwardMap.put(HHSConstants.AWARD_STATUS_ID, lsStatusId);
			loAwardMap.put(HHSConstants.CONTRACT_STATUS, lsContractStatus);
			loAwardMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loAwardMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loAwardMap.put(HHSConstants.PROVIDER_ID, lsProviderId);
			// Finish Task Required Fields
			Map loHmWFReqProps = new HashMap();
			loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setUserId(lsUserId);
			loTaskDetailsBean.setTaskName(HHSR5Constants.APPROVE_AWARD_AMOUNT);
			loChannel.setData(ApplicationConstants.AWARD_MAP, loAwardMap);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			// Task View History WorkFlow
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, HHSR5Constants.STATUS_RETURN_FOR_REVISION,
					HHSR5Constants.APPROVE_AWARD_AMOUNT, lsEntityId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.TASK_ASSIGNMENT,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCO,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsEntityId, lsUserId, HHSR5Constants.AGENCY_AUDIT));
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, HHSR5Constants.STATUS_RETURN_TO_REVIEW,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.RETURN_APPROVE_AWARD_AMOUNT,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while returnApproveAwardAmountTask : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while returnApproveAwardAmountTask : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * <ul>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the agency list,
	 * linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param aoChannel Channel object 
	 * @param asUserId User id string
	 * @param asProviderName Provider name string
	 * @param asCompetitionPoolName Competition Pool Name string
	 * @param asInternalComments Internal Comments string
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	private void getNotificationMapForReturnAwardAmount(ActionRequest aoRequest, ActionResponse aoResponse,
			Channel aoChannel, String asUserId, String asProviderName, String asCompetitionPoolName,
			String asInternalComments) throws ApplicationException
	{
		try
		{
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
			Map<String, Object> loNotificationMap = new HashMap<String, Object>();
			StringBuffer loApplicationUrl = new StringBuffer(256);
			loApplicationUrl.append(lsCityUrl).append(HHSConstants.AWARDS_CONTRACTS_URL)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID))
					.append(HHSR5Constants.CONTRACT_TAB_SCREEN_URL)
					.append(aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSR5Constants.AL229);
			loNotificationAlertList.add(HHSR5Constants.NT230);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			NotificationDataBean loNotificationAL229 = new NotificationDataBean();
			Map<String, String> loRequestMap = new HashMap<String, String>();
			loRequestMap.put(HHSConstants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
			loRequestMap.put(HHSConstants.COMPETITION_POOL, asCompetitionPoolName);
			loRequestMap.put(HHSConstants.ACCELERATOR_COMMENTS, asInternalComments);
			HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
			loAgencyLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
			loNotificationAL229.setAgencyLinkMap(loAgencyLinkMap);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsProcurementId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
			loNotificationMap.put(HHSR5Constants.AL229, loNotificationAL229);
			loNotificationMap.put(HHSR5Constants.NT230, loNotificationAL229);
			aoChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getNotificationMapForReturnAwardAmount: ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getNotificationMapForReturnAwardAmount: ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}

	}

	/**
	 * This method handles action when Finish button is clicked from S453.05
	 * 
	 * <ul>
	 * <li>Get request and session parameters like: userId, workflowId,taskId,
	 * procurmentId, P8UserSession, Internal comments</li>
	 * <li>Set task details in channel for Complete PSR task</li>
	 * <li>If comments are not null, add audit bean object by calling method
	 * addAuditDataToChannel() from HHSUtil</li>
	 * <li>Also If internal comments are not null, add audit bean object by
	 * calling method getBeanForSavingUserComments() for saving comments in user
	 * comments table</li>
	 * <li>Add TaskDetailsBean Object to channel for Approve Award Amount.</li>
	 * <li>calls the transaction 'finishApproveAwardAmount'</LI>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	@ActionMapping(params = "submit_action=finishApproveAwardAmountTask")
	protected void finishApproveAwardAmountTask(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
		Channel loChannel = new Channel();
		String lsStatusId = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSR5Constants.STATUS_APPROVE_FINALIZE_SUBMITTED);
		// Contract Status for Override: Approved Award
		String lsContractStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_CONTRACT_PENDING_REGISTARTION);
		String lsContractType = aoRequest.getParameter(HHSConstants.CONTRACT_TYPE_ID);
		if (null != lsContractType && lsContractType.equalsIgnoreCase(HHSConstants.ONE))
		{
			lsContractStatus = PropertyLoader.getProperty(HHSR5Constants.PROPERTIES_STATUS_CONSTANT,
					HHSR5Constants.STATUS_CONTRACT_PENDING_CONFIGURATION);
		}
		String lsPreviousComments = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.COMMENTS);
		String lsInternalComments = aoRequest.getParameter(ApplicationConstants.INTERNAL_COMMENTS);
		String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
		String lsProviderId = aoRequest.getParameter(HHSConstants.PROVIDER_ID);
		String lsEntityId = aoRequest.getParameter(HHSConstants.ENTITY_ID);
		Boolean loCommentAuditStatus = HHSConstants.BOOLEAN_FALSE;
		String lsProviderName = aoRequest.getParameter(HHSConstants.ORGANIZATION_LEGAL_NAME);
		String lsCompetitionPoolName = aoRequest.getParameter(HHSConstants.COMPETITION_POOL);
		try
		{
			// Retain Filters
			boolean lbManangerRole = (Boolean) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.MANAGER_ROLE);
			String lsChoosenTab = (String) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.CHOOSEN_TAB);
			HashMap loFilterDetails = (HashMap) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.FILTER_TAB);
			// Notification
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			getNotificationMapForFinishAwardAmount(aoRequest, aoResponse, loChannel, lsUserId, lsProviderName,
					lsCompetitionPoolName, lsInternalComments);
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			String lsTaskId = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.TASK_ID);
			String lsProcurementId = (String) aoRequest.getParameter(ApplicationConstants.PROCUREMENT_ID);
			if (null == lsPreviousComments && null != lsInternalComments
					&& !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING))
			{
				loCommentAuditStatus = true;
			}
			// save agency comments to user comments
			if (null != lsInternalComments && !lsInternalComments.equals(HHSR5Constants.EMPTY_STRING)
					&& null != lsPreviousComments && !lsPreviousComments.equalsIgnoreCase(lsInternalComments))
			{
				loCommentAuditStatus = true;
			}
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			if (loCommentAuditStatus)
			{
				loAuditBeanList
						.add(HHSUtil.getBeanForSavingUserComments(lsTaskId, lsWobNumber,
								HHSR5Constants.APPROVE_AWARD_AMOUNT, lsProcurementId, lsUserId, null, null,
								lsInternalComments));
				loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSR5Constants.APPROVE_AWARD_AMOUNT, lsInternalComments, HHSR5Constants.APPROVE_AWARD_AMOUNT,
						lsEntityId, lsUserId, HHSConstants.AGENCY_AUDIT));
			}
			// Setting Required Fields
			loTaskDetailsBean.setWorkFlowId(lsWobNumber);
			loTaskDetailsBean.setTaskStatus(HHSConstants.TASK_IN_REVIEW);
			Map loAwardMap = new HashMap();
			loAwardMap.put(HHSConstants.AWARD_STATUS_ID, lsStatusId);
			loAwardMap.put(HHSConstants.CONTRACT_STATUS, lsContractStatus);
			loAwardMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			loAwardMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			loAwardMap.put(HHSConstants.PROVIDER_ID, lsProviderId);
			// Finish Task Required Fields
			Map loHmWFReqProps = new HashMap();
			loHmWFReqProps.put(P8Constants.PROPERTY_PE_TASK_STATUS, ApplicationConstants.STATUS_APPROVED);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setUserId(lsUserId);
			loTaskDetailsBean.setTaskName(HHSR5Constants.APPROVE_AWARD_AMOUNT);
			loChannel.setData(ApplicationConstants.AWARD_MAP, loAwardMap);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannel.setData(HHSConstants.LO_HM_WF_REQ_PROPS, loHmWFReqProps);
			// Task View History WorkFlow
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, HHSConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
							+ HHSConstants.STR + HHSConstants.TASK_IN_REVIEW + HHSConstants.STR + HHSConstants.SPACE
							+ HHSR5Constants.STR_TO_TASK + HHSConstants.SPACE + HHSConstants.STR
							+ HHSR5Constants.STATUS_SUBMITTED_FOR_APPROVAL + HHSConstants.STR,
					HHSR5Constants.FINALIZE_AWARD_AMOUNT, lsEntityId, lsUserId, HHSConstants.ACCELERATOR_AUDIT));
			loChannel.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FINISH_APPROVE_AWARD_AMOUNT_TASK,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			ArrayList<TaskQueue> loTaskQueueItems = FileNetOperationsUtils.generateInboxAndManagementFilterDetails(
					loFilterDetails, loUserSession, lbManangerRole, lsChoosenTab, lsUserId);
			FileNetOperationsUtils.setRequiredInformationForInbox(aoRequest, loTaskQueueItems, null);
			aoResponse.setRenderParameter(ApplicationConstants.SHOW_MANAGEMENT_VIEW, ApplicationConstants.YES);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while finishApproveAwardAmountTask : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while finishApproveAwardAmountTask : ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * <ul>
	 * <li>Create a local object Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the agency list,
	 * linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest object
	 * @param aoResponse ActionResponse object
	 * @param aoChannel  Channel object
	 * @param asUserId User id strind
	 * @param asProviderName Provider name string
	 * @param asCompetitionPoolName Competition Pool Name string
	 * @param asInternalComments Internal Comments string 
	 * @throws ApplicationException when any exception occurred we wrap it into this custom exception
	 */
	private void getNotificationMapForFinishAwardAmount(ActionRequest aoRequest, ActionResponse aoResponse,
			Channel aoChannel, String asUserId, String asProviderName, String asCompetitionPoolName,
			String asInternalComments) throws ApplicationException
	{
		try
		{
			String lsProcurementId = aoRequest.getParameter(HHSConstants.PROCUREMENT_ID);
			String lsCityUrl = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSConstants.PROP_CITY_URL);
			Map<String, Object> loNotificationMap = new HashMap<String, Object>();
			StringBuffer loApplicationUrl = new StringBuffer(256);
			loApplicationUrl.append(lsCityUrl).append(HHSConstants.AWARDS_CONTRACTS_URL)
					.append(aoRequest.getParameter(HHSConstants.PROCUREMENT_ID))
					.append(HHSR5Constants.CONTRACT_TAB_SCREEN_URL)
					.append(aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
			List<String> loNotificationAlertList = new ArrayList<String>();
			loNotificationAlertList.add(HHSR5Constants.AL228);
			loNotificationAlertList.add(HHSR5Constants.NT229);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			NotificationDataBean loNotificationAL228 = new NotificationDataBean();
			Map<String, String> loRequestMap = new HashMap<String, String>();
			loRequestMap.put(HHSConstants.PROPERTY_PE_PROVIDER_NAME, asProviderName);
			loRequestMap.put(HHSConstants.COMPETITION_POOL, asCompetitionPoolName);
			loRequestMap.put(HHSConstants.ACCELERATOR_COMMENTS, asInternalComments);
			HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
			loAgencyLinkMap.put(HHSConstants.LINK, loApplicationUrl.toString());
			loNotificationAL228.setAgencyLinkMap(loAgencyLinkMap);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsProcurementId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
			loNotificationMap.put(HHSR5Constants.AL228, loNotificationAL228);
			loNotificationMap.put(HHSR5Constants.NT229, loNotificationAL228);
			aoChannel.setData(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getNotificationMapForFinishAwardAmount: ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getNotificationMapForFinishAwardAmount: ", loExp);
			String lsErrorMsg = ApplicationConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}
}
