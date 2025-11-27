package com.nyc.hhs.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.controllers.util.IDateValidator;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.AssigneeList;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentPropertiesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.RFPReleaseDocsUtil;

/**
 * This controller will extend those controllers which comes under task related
 * functionalities and will handle all task related functionalities.
 * This file is updated in R7.
 */
public abstract class BaseController implements ResourceAwareController
{
	/**
	 * Constant for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BaseController.class);

	/**
	 * This method redirect the user to Task inbox or Task management page
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 */
	@ActionMapping(params = "taskcontrollerAction=redirectToTask")
	protected void redirectToTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsError = aoRequest.getParameter(HHSConstants.ERROR_FLAG);
		String lsTaskManagementPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
				+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
				+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL + HHSConstants.AGENCY_TASK_INBOX_PAGE
				+ HHSConstants.ERROR_EQUAL + lsError + HHSConstants.AND_SIGN + HHSConstants.RETURN_TO_AGENCY_TASK
				+ HHSConstants.EQUAL + HHSConstants.TRUE;
		try
		{
			/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
			//aoResponse.sendRedirect(lsTaskManagementPath);
			aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsTaskManagementPath));			
			/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		// IOException handled here
		// IOException is thrown from sendRedirect method
		catch (IOException aoIOex)
		{
			LOG_OBJECT.Error("Error occurred while redirecting to task", aoIOex);
		}
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved
	 * <ul>
	 * <li>
	 * it calls generic method finishTaskApproveReturn() which perform all its
	 * Functionality</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 */
	@ResourceMapping("finishTaskApprove")
	protected void finishTaskApprove(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		finishTaskApproveReturn(aoResourceRequest, aoResourceResponse, HHSConstants.FINISH_TASK_APPROVE);
	}

	/**
	 * Changed method - By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id:
	 * 5653 added message in exception block
	 * 
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Approved and Return for Revision
	 * <ul>
	 * <li>
	 * Gets the respective Handler based on TaskType from 'taskhandlers'
	 * properties file</li>
	 * <li>Get Provider or Internal comment from request and set into
	 * TaskDetailsBean object</li>
	 * <li>Execute 'taskApprove' method in Handler get from above</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 * @param aoMethodName Method Name
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void finishTaskApproveReturn(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse,
			String aoMethodName) throws ApplicationException
	{
		aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
		PrintWriter loOut = null;
		String lsError = HHSConstants.EMPTY_STRING;
		String lsTaskType = aoResourceRequest.getParameter(HHSConstants.HDN_TASK_TYPE);
		String lsPeriod = aoResourceRequest.getParameter(HHSConstants.PERIOD);
		HashMap loHMErrorMap = null;
		// Added for Release 6- Returned payment task
		HashMap<String, String> loHmReqPropsForReturnedPayment = new HashMap<String, String>();
		Channel loChannelForReturnedPayment = new Channel();
		// Added for Release 6- Returned payment task end
		try
		{
			String lsInternalComment = aoResourceRequest.getParameter(HHSConstants.INTERNAL_COMMENT_AREA);
			String lsProviderComment = aoResourceRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
			PortletSession loSession = aoResourceRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setP8UserSession(loUserSession);
			loTaskDetailsBean.setProviderComment(lsProviderComment);
			loTaskDetailsBean.setInternalComment(lsInternalComment);
			loTaskDetailsBean.setPeriod(lsPeriod);
			loOut = aoResourceResponse.getWriter();
			loHMErrorMap = validateFinisTask(aoResourceResponse, loUserSession, lsUserId,
					loTaskDetailsBean.getWorkFlowId());
			/*
			 * Added for Release 6- Returned payment review task The below if
			 * block will check whether the task is Returned Payment Review task
			 * and the request is to approve the task at level one.
			 */
			if (HHSR5Constants.TASK_RETURNED_PAYMENT_REVIEW.equalsIgnoreCase(lsTaskType)
					&& loTaskDetailsBean.getLevel().equals(HHSR5Constants.STRING_ONE)
					&& HHSConstants.FINISH_TASK_APPROVE.equals(aoMethodName))
			{
				saveReturnedPaymentInformation(aoResourceRequest, loHmReqPropsForReturnedPayment,
						loChannelForReturnedPayment, lsUserId);
			}
			// Added for Release 6- Returned payment task end
			if (!(Boolean) loHMErrorMap.get(HHSConstants.ERROR_FLAG))
			{

				if (HHSConstants.FINISH_TASK_APPROVE.equals(aoMethodName))
				{
					lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean,
							HHSConstants.FINISH_TASK_APPROVE);
				}
				else if (HHSConstants.FINISH_TASK_RETURN.equals(aoMethodName))
				{
					lsError = BaseControllerUtil.finishTaskApproveUtil(lsTaskType, loTaskDetailsBean,
							HHSConstants.FINISH_TASK_RETURN);
				}

			}
			else
			{
				lsError = HHSConstants.TASK_ERROR + HHSConstants.COLON
						+ (String) loHMErrorMap.get(HHSConstants.CBL_MESSAGE);
			}

		}
		// ApplicationException and Exception are thrown from various points
		// from getTaskHandler(thrown from 3 other exception
		// :ClassNotFoundException,InstantiationException,IllegalAccessException
		// ) and taskApprove or taskReturn
		catch (ApplicationException aoAppEx)
		{
			lsError = HHSConstants.PAGE_ERROR + HHSConstants.COLON + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			// build 2.6.0, defect id 5653. added error message check
			ApplicationException loAppEx = (ApplicationException) aoAppEx.getRootCause();
			if (null != loAppEx)
			{
				String lsErrorKey = (String) loAppEx.getContextData().get(HHSConstants.PCOF_VALIDATE_ERROR_MSG);
				if (null != lsErrorKey && lsErrorKey.equalsIgnoreCase(HHSConstants.PCOF_VALIDATE_ERROR_MSG))
				{
					lsError = HHSConstants.PAGE_ERROR
							+ HHSConstants.COLON
							+ PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
									HHSConstants.PCOF_VALIDATE_ERROR_MESSAGE);
				}
			}
			LOG_OBJECT.Error("ApplicationException occurred in finishTaskApprove", aoAppEx);
		}
		catch (Exception aoEx)
		{
			lsError = HHSConstants.PAGE_ERROR + HHSConstants.COLON + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in finishTaskApprove", aoEx);
		}
		finally
		{
			BaseControllerUtil.catchTaskError(loOut, lsError);
		}
	}

	/**
	 * The method is added in Release 6. The method will save Returned Payment
	 * information on clicking Finish button at level 1 with Approve status.
	 * @param aoResourceRequest
	 * @param loHmReqPropsForReturnedPayment
	 * @param loChannelForReturnedPayment
	 * @param lsUserId
	 * @throws ApplicationException
	 */
	private void saveReturnedPaymentInformation(ResourceRequest aoResourceRequest,
			HashMap<String, String> loHmReqPropsForReturnedPayment, Channel loChannelForReturnedPayment, String lsUserId)
			throws ApplicationException
	{
		try
		{
			loHmReqPropsForReturnedPayment.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
					aoResourceRequest.getParameter(HHSConstants.RETURN_PAYMENT_DETAIL_ID));
			loHmReqPropsForReturnedPayment.put(HHSR5Constants.CHECK_NUMBER,
					aoResourceRequest.getParameter(HHSR5Constants.CHECK_NUMBER));
			loHmReqPropsForReturnedPayment.put(HHSR5Constants.CHECK_DATE,
					aoResourceRequest.getParameter(HHSR5Constants.CHECK_DATE));
			loHmReqPropsForReturnedPayment.put(HHSR5Constants.RECEIVED_DATE_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.RECEIVED_DATE_FOR_RETURN_PAYMENT));
			loHmReqPropsForReturnedPayment.put(HHSR5Constants.DESCRIPTION_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.DESCRIPTION_FOR_RETURN_PAYMENT));
			loHmReqPropsForReturnedPayment.put(
					HHSR5Constants.CHECK_AMOUNT_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.CHECK_AMOUNT_FOR_RETURN_PAYMENT).replaceAll(
							HHSConstants.COMMA, HHSConstants.EMPTY_STRING));
			loHmReqPropsForReturnedPayment.put(HHSR5Constants.AGENCY_TRACKING_FOR_RETURN_PAYMENT,
					aoResourceRequest.getParameter(HHSR5Constants.AGENCY_TRACKING_FOR_RETURN_PAYMENT));
			loChannelForReturnedPayment.setData(HHSConstants.AO_HASH_MAP, loHmReqPropsForReturnedPayment);
			loHmReqPropsForReturnedPayment.put(HHSConstants.USER_ID, lsUserId);
			HHSTransactionManager.executeTransaction(loChannelForReturnedPayment,
					HHSR5Constants.TRANSACTION_FOR_SAVE_DETAILS_RETURN_PAYMENT_REVIEW,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in saveReturnedPaymentInformation method", aoExp);
			throw aoExp;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in saveReturnedPaymentInformation method", aoEx);
			throw new ApplicationException("Exception occured in saveReturnedPaymentInformation method", aoEx);
		}
	}

	/**
	 * This method decide the execution flow on click of Finish Task button with
	 * task status Return for Revision
	 * <ul>
	 * <li>
	 * it calls generic method finishTaskApproveReturn() which perform all its
	 * Functionality</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 */

	@ResourceMapping("finishTaskReturn")
	protected void finishTaskReturn(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		finishTaskApproveReturn(aoResourceRequest, aoResourceResponse, HHSConstants.FINISH_TASK_RETURN);
	}

	/**
	 * This method decide the execution flow on click of Reassign Task button
	 * <ul>
	 * <li>Get Workflow Id from request to find workflow task need to reassign</li>
	 * <li>Get User Id and User Name from request to whom task is need to
	 * reassign</li>
	 * <li>Get provider or internal comment from request to save comments in
	 * Audit</li>
	 * <li>Execute 'reassignTask' transaction to perform reassign operation</li>
	 * </ul>
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 */
	@ActionMapping(params = "taskcontrollerAction=reAssignTask")
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	protected void reAssignTask(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		String lsTaskHomePagePath = HHSConstants.EMPTY_STRING;
		HashMap loHMErrorMap = null;
		try
		{
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
			String lsError = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			lsTaskHomePagePath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1 + aoRequest.getServerName()
					+ HHSConstants.COLON + aoRequest.getServerPort() + aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL + HHSConstants.AGENCY_TASK_INBOX_PAGE + HHSConstants.ERROR_EQUAL
					+ lsError + HHSConstants.AND_SIGN + HHSConstants.RETURN_TO_AGENCY_TASK + HHSConstants.EQUAL
					+ HHSConstants.TRUE;
			HashMap loDefaultMap = new HashMap();
			// setting default parameters in default map
			loDefaultMap.put(HHSConstants.LS_TASK_TYPE, aoRequest.getParameter(HHSConstants.HDN_TASK_TYPE));
			loDefaultMap.put(HHSConstants.LS_WOB_NUM, aoRequest.getParameter(HHSConstants.HDN_WORK_FLOW_ID));
			loDefaultMap.put(HHSConstants.LS_REASSIGN_USER_ID, aoRequest.getParameter(HHSConstants.ASSIGN_USER));
			loDefaultMap.put(HHSConstants.LS_REASSIGN_USER_NAME,
					aoRequest.getParameter(HHSConstants.REASSIGNTOUSER_TEXT));
			loDefaultMap.put(HHSConstants.LS_PUBLIC_COMMENT, aoRequest.getParameter(HHSConstants.HDN_PROVIDER_COMMENT));
			loDefaultMap.put(HHSConstants.LS_INTERNAL_COMMENT,
					aoRequest.getParameter(HHSConstants.HDN_INTERNAL_COMMENT));
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			// Setting the default bean
			loHMErrorMap = validateAssignTaskUser(aoResponse, loUserSession, lsUserRole, lsUserId,
					aoRequest.getParameter(HHSConstants.HDN_WORK_FLOW_ID));
			// Start Added in R5
			DefaultAssignment loDefaultAssignment = new DefaultAssignment();
			String lsTaskType = loTaskDetailsBean.getEntityType();
			loDefaultAssignment.setDefaultAssignments(aoRequest.getParameter(HHSR5Constants.ASSIGNMENT));
			if (loDefaultAssignment.getDefaultAssignments() != null
					&& (loDefaultAssignment.getDefaultAssignments().equalsIgnoreCase(HHSConstants.YES) || loDefaultAssignment
							.getDefaultAssignments().equalsIgnoreCase(HHSConstants.NO)))
			{
				loDefaultAssignment.setEntityId(loTaskDetailsBean.getEntityId());
				lsTaskType = HHSUtil.setTaskType(lsTaskType);
				loDefaultAssignment.setTaskType(lsTaskType);
				loDefaultAssignment.setTaskLevel(HHSConstants.LEVEL + loTaskDetailsBean.getLevel());
				loDefaultAssignment.setIsfinancials(HHSR5Constants.TRUE);
				loDefaultAssignment.setKeepDefault(aoRequest.getParameter(HHSR5Constants.KEEP_CURRENT_DEFAULT));
				loDefaultAssignment.setAskFlag(aoRequest.getParameter(HHSR5Constants.ASK_AGAIN));
				loDefaultAssignment.setCreatedByUserId(lsUserId);
				loDefaultAssignment.setModifiedByUserId(lsUserId);
				loDefaultAssignment.setAssigneeUserId(aoRequest.getParameter(HHSConstants.ASSIGN_USER));
				loChannel.setData(HHSR5Constants.DEFAULT_ASSIGNEE_BEAN, loDefaultAssignment);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.UPDATE_DEFAULT_REASSIGNEE_USER,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			// End Added in R5
			if (!(Boolean) loHMErrorMap.get(HHSConstants.ERROR_FLAG))
			{
				BaseControllerUtil.reAssignTaskDefPara(loDefaultMap, loChannel, loTaskDetailsBean);
				BaseControllerUtil.reAssignTaskUtil(loDefaultMap, loTaskDetailsBean, lsUserId, loChannel);
				lsError = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsError = (String) loHMErrorMap.get(HHSConstants.CBL_MESSAGE);
			}
			lsTaskHomePagePath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1 + aoRequest.getServerName()
					+ HHSConstants.COLON + aoRequest.getServerPort() + aoRequest.getContextPath()
					+ ApplicationConstants.PORTAL_URL + HHSConstants.AGENCY_TASK_INBOX_PAGE + HHSConstants.ERROR_EQUAL
					+ lsError + HHSConstants.AND_SIGN + HHSConstants.RETURN_TO_AGENCY_TASK + HHSConstants.EQUAL
					+ HHSConstants.TRUE;
		}
		// ApplicationException and Exception are Thrown from executeTransaction
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData(HHSConstants.LEVEL_ERROR_MESSAGE, aoAppEx.toString());
			LOG_OBJECT.Error("ApplicationException occured in reAssignTask", aoAppEx);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseController: validateChartAllocationFYI:: ", aoEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, "validation failed");
			LOG_OBJECT.Error("Exception occured in reAssignTask", loAppEx);
		}
		finally
		{
			try
			{
				aoResponse.sendRedirect(lsTaskHomePagePath);
			}
			// IOException Thrown from sendRedirect
			catch (IOException aoIOEx)
			{
				ApplicationException loAppEx = new ApplicationException("Error occured in "
						+ "BaseController: validateChartAllocationFYI:: ", aoIOEx);
				loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, "validation failed");
				LOG_OBJECT.Error("Exception occured in reAssignTask Not able to redirect on task home page", loAppEx);
			}
		}
	}

	/**
	 * This method Validate whether Agency User can do assign operation or not
	 * .if not set error message.
	 * 
	 * @param aoResponse Response object
	 * @param aoUserSession Filenet session
	 * @param asUserRole User Role
	 * @param asUserId User Id
	 * @param asWobNum Workflow Id
	 * @return HashMap object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private HashMap validateAssignTaskUser(ActionResponse aoResponse, P8UserSession aoUserSession, String asUserRole,
			String asUserId, String asWobNum) throws ApplicationException
	{
		String lsCurrentTaskOwner = null;
		String lsCurrentTaskStatus = null;
		int liTaskCount = 0;
		Channel loChannel = new Channel();
		HashMap loHMReqdProp = new HashMap();
		HashMap loHMErrorProp = new HashMap();
		loHMErrorProp.put(HHSConstants.ERROR_FLAG, false);
		loHMErrorProp.put(HHSConstants.CBL_MESSAGE, HHSConstants.EMPTY_STRING);
		try
		{
			if ((asUserRole.equalsIgnoreCase(HHSConstants.PROGRAM_STAFF_ROLE)
					|| asUserRole.equalsIgnoreCase(HHSConstants.ACCO_STAFF_ROLE) || asUserRole
						.equalsIgnoreCase(HHSConstants.FINANCE_STAFF_ROLE)))
			{
				loHMReqdProp.put(HHSConstants.F_WOB_NUM, asWobNum);
				loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
				loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loHMReqdProp);
				loChannel.setData(HHSConstants.LS_WOB_NUM, asWobNum);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CURRENT_TASK_OWNER);
				liTaskCount = (Integer) loChannel.getData(HHSConstants.COUNT);
				if (liTaskCount > 0)
				{
					loHMReqdProp = (HashMap) loChannel.getData(HHSConstants.HM_REQIRED_PROPERTY_MAP);
					if (null != loHMReqdProp)
					{
						lsCurrentTaskOwner = (String) loHMReqdProp.get(P8Constants.PROPERTY_PE_ASSIGNED_TO);
						lsCurrentTaskStatus = (String) loHMReqdProp.get(HHSConstants.TASK_STATUS);
					}
					if (null != lsCurrentTaskOwner && !lsCurrentTaskOwner.contains(HHSConstants.WF_INITIAL_REVIEWER)
							&& !lsCurrentTaskOwner.equalsIgnoreCase(asUserId))
					{
						loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
						loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CAN_NOT_ASSIGN_TASK));
					}
					else if (null != lsCurrentTaskStatus
							&& lsCurrentTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
					{
						loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
						loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ERROR_FINISH_SUSPEND));
					}
				}
				else
				{
					loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
					loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.TASK_CANCELLED_ERROR));
				}
			}
		}
		// handling exception thrown by transaction layer.
		catch (ApplicationException aoAppEx)
		{
			Map<String, String> loParamMap = new HashMap<String, String>();
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, loParamMap, aoAppEx);
			LOG_OBJECT.Error("Exception Occured while validateAssignTaskUser : ", aoAppEx);
		}
		return loHMErrorProp;
	}

	/**
	 * This method validate whether user is task owner or not or task is exist
	 * or not if not set error message
	 * 
	 * @param aoResponse Response object
	 * @param aoUserSession Filenet session
	 * @param asUserId User Id
	 * @param asWobNum Workflow Id
	 * @return HashMap object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private HashMap validateFinisTask(ResourceResponse aoResponse, P8UserSession aoUserSession, String asUserId,
			String asWobNum) throws ApplicationException
	{
		String lsCurrentTaskOwner = null;
		String lsCurrentTaskStatus = null;
		int liTaskCount = 0;
		Channel loChannel = new Channel();
		HashMap loHMReqdProp = new HashMap();
		HashMap loHMErrorProp = new HashMap();
		loHMErrorProp.put(HHSConstants.ERROR_FLAG, false);
		loHMErrorProp.put(HHSConstants.CBL_MESSAGE, HHSConstants.EMPTY_STRING);
		loHMReqdProp.put(HHSConstants.F_WOB_NUM, asWobNum);
		loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loHMReqdProp);
		loChannel.setData(HHSConstants.LS_WOB_NUM, asWobNum);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CURRENT_TASK_OWNER);
		liTaskCount = (Integer) loChannel.getData(HHSConstants.COUNT);
		if (liTaskCount > 0)
		{
			loHMReqdProp = (HashMap) loChannel.getData(HHSConstants.HM_REQIRED_PROPERTY_MAP);
			if (null != loHMReqdProp)
			{
				lsCurrentTaskOwner = (String) loHMReqdProp.get(P8Constants.PROPERTY_PE_ASSIGNED_TO);
				lsCurrentTaskStatus = (String) loHMReqdProp.get(HHSConstants.TASK_STATUS);
			}
			if (null != lsCurrentTaskOwner && !lsCurrentTaskOwner.equalsIgnoreCase(asUserId))
			{
				loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
				loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ERROR_FINISH));
			}
			if (null != lsCurrentTaskStatus && lsCurrentTaskStatus.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED))
			{
				loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
				loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.ERROR_FINISH_SUSPEND));
			}
		}
		else
		{
			loHMErrorProp.put(HHSConstants.ERROR_FLAG, true);
			loHMErrorProp.put(HHSConstants.CBL_MESSAGE, PropertyLoader.getProperty(
					HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.TASK_CANCELLED_ERROR));
		}
		return loHMErrorProp;
	}

	/**
	 * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * This method decide the execution flow on click of Save button and save
	 * the Comments into database
	 * <ul>
	 * <li>Get Provider and Internal comment from request to save in Database</li>
	 * <li>Call 'saveCommentNonAudit' method to perform save operation</li>
	 * </ul>
	 * Method Updated in R4
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 */
	@ResourceMapping("saveComments")
	protected String saveComments(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		String lsMsg = null;
		PrintWriter loOut = null;
		try
		{
			loOut = aoResourceResponse.getWriter();
			PortletSession loSession = aoResourceRequest.getPortletSession();
			// fix done as a part of release 3.1.2 defect 6420 - start
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			String lsPageLevelInvoiceId = aoResourceRequest.getParameter(HHSR5Constants.INVOICE_ID_AT_PAGE);
			if (lsPageLevelInvoiceId == null
					|| (loTaskDetailsBean != null && loTaskDetailsBean.getInvoiceId() != null && lsPageLevelInvoiceId
							.equalsIgnoreCase(loTaskDetailsBean.getInvoiceId())))
			{
				// fix done as a part of release 3.1.2 defect 6420 - end
				String lsTabLevel = (String) aoResourceRequest.getParameter(HHSConstants.TAB_LEVEL_URL_PARAM);
				// R4: Tab Level Comments
				loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION,
						PortletSession.APPLICATION_SCOPE);
				String lsPublicComment = (String) aoResourceRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
				String lsInternalComment = (String) aoResourceRequest.getParameter(HHSConstants.INTERNAL_COMMENT_AREA);
				// R4: Tab Level Comments
				String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
						PortletSession.APPLICATION_SCOPE);
				String lsTabLevelEntityType = (String) aoResourceRequest
						.getParameter(HHSConstants.PARAMATER_ENTITY_TAB_LEVEL);
				String lsSubBudgetId = HHSUtil.getSubBudgetIdForTabLevelOnCommentsSave(lsTabLevelEntityType);
				loTaskDetailsBean.setLaunchOrgType(lsOrgType);
				if (null != lsTabLevel && lsTabLevel.length() > 0
						&& (ApplicationConstants.TRUE).equalsIgnoreCase(lsTabLevel)
						&& null != loTaskDetailsBean.getEntityTypeTabLevel()
						&& loTaskDetailsBean.getEntityTypeTabLevel().length() > 0)
				{
					loTaskDetailsBean.setIsEntityTypeTabLevel(true);
					if (null != lsTabLevelEntityType && !lsTabLevelEntityType.isEmpty())
					{
						loTaskDetailsBean.setEntityTypeTabLevel(lsTabLevelEntityType);
						if (null != lsSubBudgetId && !lsSubBudgetId.isEmpty())
						{
							loTaskDetailsBean.setSubBudgetId(lsSubBudgetId);
						}
					}
				}
				else
				{
					loTaskDetailsBean.setIsEntityTypeTabLevel(false);
				}
				// R4: Tab Level Comments End
				if (null != lsPublicComment)
				{
					loTaskDetailsBean.setProviderComment(lsPublicComment);
				}
				if (null != lsInternalComment)
				{
					loTaskDetailsBean.setInternalComment(lsInternalComment);
				}
				BaseControllerUtil.saveCommentNonAuditUtil(loTaskDetailsBean);
			}
			else
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.MULTIPLE_INVOICE_OPEN);
			}
		}
		// ApplicationException thrown from executeTransaction in setHhsAudit in
		// HHSUtil
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in saveComments", aoAppEx);
		}
		// Exception thrown from executeTransaction in setHhsAudit in HHSUtil
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in saveComments", aoEx);
		}
		finally
		{
			catchTaskError(loOut, lsMsg);
		}
		return lsMsg;
	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param loOut
	 * @param asError
	 */
	private void catchTaskError(PrintWriter loOut, String asError)
	{
		loOut.print(asError);
		loOut.flush();
		loOut.close();
	}

	/**
	 * This method save comments in comments table
	 * <ul>
	 * <li>Set All Audit bean property values .</li>
	 * <li>Call setHhsAudit Utility to save comments in Database.</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean Task Details bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void saveCommentNonAudit(TaskDetailsBean aoTaskDetailsBean) throws ApplicationException
	{
		BaseControllerUtil.saveCommentNonAuditUtil(aoTaskDetailsBean);
	}

	/**
	 * This method decide the execution flow on click of View Comments History
	 * Tab
	 * <ul>
	 * <li>Get TaskDetailsBean object from session which contains all
	 * information required to show history.</li>
	 * <li>Call 'viewCommentsHistoryUtil' method and set comment history details
	 * in request.</li>
	 * <li>Redirect to 'viewtaskhistory' jsp to display history</li>
	 * </ul>
	 * Method Updated in R4
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @return loModelAndView ModelAndView object
	 */
	@ResourceMapping("viewCommentsHistory")
	public ModelAndView viewCommentsHistory(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;
		try
		{
			PortletSession loSession = aoResourceRequest.getPortletSession();
			// R4: Tab Level Comments
			String lsTabLevel = (String) aoResourceRequest.getParameter(HHSConstants.TAB_LEVEL_URL_PARAM);
			String lsTabLevelEntityType = (String) aoResourceRequest
					.getParameter(HHSConstants.PARAMATER_ENTITY_TAB_LEVEL);
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			// R4: Tab Level Comments End
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			// R4: Tab Level Comments
			loTaskDetailsBean.setLaunchOrgType(lsOrgType);
			if (null != lsTabLevel && lsTabLevel.length() > 0
					&& (ApplicationConstants.TRUE).equalsIgnoreCase(lsTabLevel)
					&& null != loTaskDetailsBean.getEntityTypeTabLevel()
					&& loTaskDetailsBean.getEntityTypeTabLevel().length() > 0)
			{
				loTaskDetailsBean.setIsEntityTypeTabLevel(true);
				if (null != lsTabLevelEntityType && !lsTabLevelEntityType.isEmpty())
				{
					loTaskDetailsBean.setEventType(lsTabLevelEntityType);
				}
			}
			else
			{
				loTaskDetailsBean.setIsEntityTypeTabLevel(false);
			}
			// R4: Tab Level Comments End
			loTaskDetailsBean = BaseControllerUtil.viewCommentsHistoryUtil(loTaskDetailsBean);
			aoResourceRequest.setAttribute(HHSConstants.COMMENTS_HISTORY_BEAN, loTaskDetailsBean.getCommentsHistory());
			loModelAndView = new ModelAndView(HHSConstants.JSP_TASKS_VIEWTASKHISTORY);
		}
		// ApplicationException thrown from executeTransaction in
		// viewCommentsHistoryUtil in BaseControllerUtil
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in viewCommentsHistory", aoAppEx);
		}
		// Exception thrown from executeTransaction in
		// viewCommentsHistoryUtil in BaseControllerUtil
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in viewCommentsHistory", aoEx);
		}
		return loModelAndView;
	}

	/**
	 * <ul>
	 * Gets task header section details from database
	 * <li>Execute 'fetchTaskDetails' transaction to get task details like task
	 * name ,submitted by,submitted date etc from filenet</li>
	 * </ul>
	 * 
	 * @param aoRequest Render Request Object
	 * @param asWorkflowId Workflow Id
	 * @return TaskDetailsBean loTaskDetailsBean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public TaskDetailsBean fetchTaskDetailsFromFilenet(RenderRequest aoRequest, String asWorkflowId)
			throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setP8UserSession(loUserSession);
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

			loTaskDetailsBean = BaseControllerUtil.fetchTaskDetailsFromFilenetUtil(asWorkflowId, lsUserId,
					loTaskDetailsBean, loChannel);
			// Task is unassigned or login User is not task owner make whole
			// page
			// read only except reassign dropdown
			if ((!loTaskDetailsBean.getAssignedTo().equalsIgnoreCase(lsUserId))
					|| (null != loTaskDetailsBean.getCurrentTaskStatus() && loTaskDetailsBean.getCurrentTaskStatus()
							.equalsIgnoreCase(HHSConstants.STATUS_SUSPENDED)))
			{
				loTaskDetailsBean.setIsTaskAssigned(false);
			}
			else
			{
				loTaskDetailsBean.setIsTaskAssigned(true);
			}

			loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
					PortletSession.APPLICATION_SCOPE);
		}
		// ApplicationException thrown from executeTransaction in
		// viewCommentsHistoryUtil in BaseControllerUtil
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException occured in fetchTaskDetailsFromFilenet", aoAppEx);
			throw aoAppEx;
		}
		// Exception thrown from executeTransaction in
		// viewCommentsHistoryUtil in BaseControllerUtil
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Exception occured in fetchTaskDetailsFromFilenet",
					aoEx);
			LOG_OBJECT.Error("Exception occured in fetchTaskDetailsFromFilenet", loAppEx);
			loAppEx.addContextData("Exception occured in fetchTaskDetailsFromFilenet", loAppEx);
			throw loAppEx;
		}
		return loTaskDetailsBean;
	}

	/**
	 * This method is the default implementation of the ResourceAwareController
	 * 
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoResourceResponse decides the next execution flow
	 * @return null
	 * @throws Exception If an Exception occurs
	 */
	@Override
	@ResourceMapping
	public ModelAndView handleResourceRequest(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws Exception
	{
		LOG_OBJECT.Info("DeFault Implementation BaseController : method : handleResourceRequest");
		return null;
	}

	/**
	 * This method populates bean with paging parameters
	 * <ul>
	 * <li>
	 * Gets PortletSession object, BaseFilter bean object, next page value and
	 * page key as input.</li>
	 * <li>Gets allowedObjectCount from cache object by passing modified key as
	 * page key and set in session object</li>
	 * <li>Checks for next page value.</li>
	 * <li>If available, use the same for calculating start node and end node
	 * and set these values in BaseFilter bean object.</li>
	 * </ul>
	 * 
	 * @param aoSession Portal Session object
	 * @param aoFilterBean a bean object
	 * @param asNextPage a String value containing paging node
	 * @param asPageKey key used to map the value in db table.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("unchecked")
	protected void getPagingParams(PortletSession aoSession, BaseFilter aoFilterBean, String asNextPage,
			String asPageKey) throws ApplicationException
	{
		String lsAppSettingMapKey = asPageKey + HHSConstants.UNDERSCORE
				+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
		HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
				.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
		if (loApplicationSettingMap == null || null == loApplicationSettingMap.get(lsAppSettingMapKey))
		{
			throw new ApplicationException("Error occurred while getting cache object for key : "
					+ ApplicationConstants.APPLICATION_SETTING);
		}
		int loAllowedObjectCount = Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, loAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		int lsPageIndex = HHSConstants.INT_ONE;
		int liStartNode = HHSConstants.INT_ZERO;
		if (null != asNextPage)
		{
			lsPageIndex = Integer.valueOf(asNextPage);
		}
		if ((lsPageIndex - HHSConstants.INT_ONE) == HHSConstants.INT_ZERO)
		{
			liStartNode = HHSConstants.INT_ONE;
		}
		else
		{
			liStartNode = (lsPageIndex - HHSConstants.INT_ONE) * Integer.valueOf(String.valueOf(loAllowedObjectCount))
					+ HHSConstants.INT_ONE;
		}
		int liEndNode = lsPageIndex * loAllowedObjectCount;
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, String.valueOf(lsPageIndex),
				PortletSession.APPLICATION_SCOPE);
		aoFilterBean.setStartNode(liStartNode);
		aoFilterBean.setEndNode(liEndNode);
	}

	/**
	 * This method populates bean with paging parameters<br>
	 * Method added for R4.
	 * <ul>
	 * <li>
	 * Gets PortletSession object, BaseFilter bean object, next page value and
	 * page key as input.</li>
	 * <li>Gets allowedObjectCount from cache object by passing modified key as
	 * page key and set in session object</li>
	 * <li>Checks for next page value.</li>
	 * <li>If available, use the same for calculating start node and end node
	 * and set these values in BaseFilter bean object.</li>
	 * </ul>
	 * 
	 * @param aoSession Portal Session object
	 * @param aoFilterBean a bean object
	 * @param asNextPage a String value containing paging node
	 * @param asPageKey key used to map the value in db table.
	 * @param asSelectedOption string
	 * @throws ApplicationException If an Application Exception occurs
	 * 
	 */

	@SuppressWarnings("unchecked")
	protected void getPagingParamsTaxonomy(PortletSession aoSession, BaseFilter aoFilterBean, String asNextPage,
			String asPageKey, String asSelectedOption) throws ApplicationException
	{
		int loAllowedObjectCount = 0;
		if (asSelectedOption != null && !asSelectedOption.isEmpty()
				&& !asSelectedOption.equalsIgnoreCase(HHSR5Constants.EMPTY_STRING))
		{
			loAllowedObjectCount = Integer.valueOf(asSelectedOption);
		}
		else
		{
			String lsAppSettingMapKey = asPageKey + HHSConstants.UNDERSCORE
					+ P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			if (loApplicationSettingMap == null || null == loApplicationSettingMap.get(lsAppSettingMapKey))
			{
				throw new ApplicationException("Error occurred while getting cache object for key : "
						+ ApplicationConstants.APPLICATION_SETTING);
			}
			loAllowedObjectCount = Integer.valueOf(loApplicationSettingMap.get(lsAppSettingMapKey));
		}
		aoSession.setAttribute(HHSConstants.ALLOWED_OBJECT_COUNT, loAllowedObjectCount,
				PortletSession.APPLICATION_SCOPE);
		int lsPageIndex = HHSConstants.INT_ONE;
		int liStartNode = HHSConstants.INT_ZERO;
		if (null != asNextPage)
		{
			lsPageIndex = Integer.valueOf(asNextPage);
		}
		if ((lsPageIndex - HHSConstants.INT_ONE) == HHSConstants.INT_ZERO)
		{
			liStartNode = HHSConstants.INT_ONE;
		}
		else
		{
			liStartNode = (lsPageIndex - HHSConstants.INT_ONE) * Integer.valueOf(String.valueOf(loAllowedObjectCount))
					+ HHSConstants.INT_ONE;
		}
		int liEndNode = lsPageIndex * loAllowedObjectCount;
		aoSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_PAGE_INDEX, String.valueOf(lsPageIndex),
				PortletSession.APPLICATION_SCOPE);
		aoFilterBean.setStartNode(liStartNode);
		aoFilterBean.setEndNode(liEndNode);
	}

	/**
	 * This method will return a XML dom Element based on input parameters
	 * <ul>
	 * <li>Gets column name, user org, grid name and sort type as input
	 * parameters</li>
	 * <li>Reads SortConfoguration.xml from cache</li>
	 * <li>Gets column element object</li>
	 * <li>Populates SortOnColumnsBean bean object with sorting details</li>
	 * </ul>
	 * 
	 * @param asColumnName a string value of column name
	 * @param asUserOrg a string value of user org
	 * @param asGridName a string value of grid name
	 * @param aoBaseFilter - an object of type BaseFilter
	 * @param asSortType a string value of current sort type
	 * @return
	 * @throws ApplicationException If an Application Exception occurs
	 */
	protected void getSortDetailsFromXML(String asColumnName, String asUserOrg, String asGridName,
			BaseFilter aoBaseFilter, String asSortType) throws ApplicationException
	{
		BaseControllerUtil.getSortDetailsFromXMLUtil(asColumnName, asUserOrg, asGridName, aoBaseFilter, asSortType);
	}

	/**
	 * This method will get the Epin list from the cache when user types three
	 * characters in epin textbox
	 * <ul>
	 * <li>1.Get the String entered by the user from request</li>
	 * <li>2. Get the epinCall parameter from Request and assign it to
	 * lsEpinCalling</li>
	 * <li>3. Based on the value of lsEpinCalling, get the epin list from cache</li>
	 * <li>4. if cache does not contain anything, call the service method to get
	 * the epin list from database.</li>
	 * </ul>
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	protected void getEpinList(ResourceRequest aoRequest, ResourceResponse aoResponse) throws ApplicationException
	{
		PrintWriter loOut = null;
		try
		{
			String lsQueryStringFromReq = aoRequest.getParameter(HHSConstants.QUERY);
			final int liMinLength = Integer.valueOf(HHSConstants.THREE);
			List<String> loEpinList = new ArrayList<String>();
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID);
			UserThreadLocal.setUser(lsUserId);
			String lsEpinCalling = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EPIN_QUERY_ID);
			loEpinList = BaseControllerUtil.getEpinListUtil(loEpinList, lsEpinCalling, lsQueryStringFromReq);
			// if the query string from the request is not null then set the
			// content type to json type
			if ((lsQueryStringFromReq != null) && (lsQueryStringFromReq.length() >= liMinLength))
			{
				try
				{
					if (null != loEpinList)
					{
						loOut = aoResponse.getWriter();
						aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
						final String lsOutputJSONaoResponse = HHSUtil
								.generateDelimitedResponse(loEpinList, lsQueryStringFromReq, liMinLength).toString()
								.trim();
						loOut.print(lsOutputJSONaoResponse);
					}
				}
				// IOException thrown from getWriter
				catch (IOException aoExp)
				{
					throw new ApplicationException("IOException occurred while searching providers:", aoExp);
				}
			}
		}
		// ApplicationException is thrown from various points
		// from getEpinListUtil(while getting and putting data in cache) and
		// from executeTransaction (while executing transaction)
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/* Jquery Grid related functions starts */
	/**
	 * This method returns the fiscal years based on contract start and end date
	 * <ul>
	 * <li>Get Fiscal Start Year</li>
	 * <li>Get Fiscal End Year</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a HashMap object containing FY start year, End year and year
	 *         count.
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	protected Map getContractFiscalYears(PortletRequest aoRequest) throws ApplicationException
	{
		String lsContractStartDate = (String) aoRequest.getPortletSession().getAttribute(
				HHSConstants.CONTRACT_START_DATE_UPPERCASE, PortletSession.APPLICATION_SCOPE);
		String lsContractEndDate = (String) aoRequest.getPortletSession().getAttribute(
				HHSConstants.CONTRACT_END_DATE_UPPERCASE, PortletSession.APPLICATION_SCOPE);
		if (aoRequest.getAttribute(HHSConstants.IS_AMENDMENT_FLOW) != null)
		{
			lsContractStartDate = (String) aoRequest.getPortletSession().getAttribute(
					HHSConstants.AMENDMENT_START_DATE, PortletSession.APPLICATION_SCOPE);
			lsContractEndDate = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.AMENDMENT_END_DATE,
					PortletSession.APPLICATION_SCOPE);
		}

		Map loContractMap = new HashMap();
		BaseControllerUtil.getContractFiscalYearsUtil(lsContractStartDate, lsContractEndDate, loContractMap);
		return loContractMap;
	}

	/**
	 * <ul>
	 * <li>This method returns Header names of Chart of Account Grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header Names.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getAccountsMainHeader(PortletRequest aoRequest) throws ApplicationException
	{
		String lsAccountsMainHeader = getFiscalYearHeader(aoRequest);
		StringBuffer loBuffer = BaseControllerUtil.getFundingMainHeaderUtil(lsAccountsMainHeader,
				HHSConstants.GET_ACCOUNTS_MAIN_HEADER);
		return loBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>This method returns Header properties of Chart of Account grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header Properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getAccountsMainHeaderProp(PortletRequest aoRequest) throws ApplicationException
	{
		String lsAccountsMainHeaderProp = getFiscalYearHeaderProp(aoRequest);
		StringBuffer loBuffer = BaseControllerUtil.getFundingMainHeaderUtil(lsAccountsMainHeaderProp,
				HHSConstants.GET_ACCOUNTS_MAIN_HEADER_PROP);
		return loBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>This method returns Sub grid properties of Chart of Account Grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Sub grid Properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getAccountsSubGridProp(PortletRequest aoRequest) throws ApplicationException
	{
		StringBuffer loBuffer = null;
		if (aoRequest.getAttribute(HHSConstants.BMC_ACTION_REQ_PARAM) != null
				&& ((String) aoRequest.getAttribute(HHSConstants.BMC_ACTION_REQ_PARAM))
						.equalsIgnoreCase(HHSConstants.TASK_NEW_FY_CONFIGURATION))
		{
			loBuffer = new StringBuffer(
					HHSConstants.EDITABLE_FALSE_EDITRULES_REQUIRED_TRUE_EDITABLE_FALSE_EDITABLE_FALSE);
			loBuffer.append(getFiscalYearCustomSubGridProp(aoRequest));
			aoRequest.removeAttribute(HHSConstants.BMC_ACTION_REQ_PARAM);
		}
		else
		{
			loBuffer = new StringBuffer(HHSConstants.EDITABLE_TRUE_EDITRULES_REQUIRED_TRUE_EDITABLE_TRUE_EDITABLE_TRUE);
			loBuffer.append(getFiscalYearSubGridProp(aoRequest));
		}
		loBuffer.append(HHSConstants.EDITABLE_FALSE);
		return loBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>This method returns Header names of Funding Source Allocation Grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header Names.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getFundingMainHeader(PortletRequest aoRequest) throws ApplicationException
	{
		String lsFiscalYearHead = getFiscalYearHeader(aoRequest);
		StringBuffer loBuffer = BaseControllerUtil.getFundingMainHeaderUtil(lsFiscalYearHead,
				HHSConstants.GET_FUNDING_MAIN_HEADER);
		return loBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>This method returns Header properties of Funding Source Allocation
	 * Grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header Properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getFundingsMainHeaderProp(PortletRequest aoRequest) throws ApplicationException
	{
		String lsFundingsMainHeaderProp = getFiscalYearHeaderProp(aoRequest);
		StringBuffer loBuffer = BaseControllerUtil.getFundingMainHeaderUtil(lsFundingsMainHeaderProp,
				HHSConstants.GET_FUNDINGS_MAIN_HEADER_PROP);
		return loBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>This method returns Sub grid properties of Funding Source Allocation
	 * Grid</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Sub grid Properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	private String getFundingSubGridProp(PortletRequest aoRequest) throws ApplicationException
	{
		String lsFundingsMainHeaderProp = getFiscalYearSubGridProp(aoRequest);
		StringBuffer loBuffer = BaseControllerUtil.getFundingMainHeaderUtil(lsFundingsMainHeaderProp,
				HHSConstants.GET_FUNDING_SUB_GRID_PROP);
		return loBuffer.toString();
	}

	/**
	 * This method returns Header names of the Fiscal Years
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header Names.
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	private String getFiscalYearHeader(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap,
				HHSConstants.GET_FISCAL_YEAR_HEADER);
		return loStringBuffer.toString();
	}

	/**
	 * This method returns Header properties of the Fiscal Years
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	private String getFiscalYearHeaderProp(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap,
				HHSConstants.GET_FISCAL_YEAR_HEADER_PROP);
		return loStringBuffer.toString();
	}

	/**
	 * This method returns properties of the Fiscal Years in the subgrid
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of subgrid properties of fiscal year.
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	private String getFiscalYearSubGridProp(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap,
				HHSConstants.GET_FISCAL_YEAR_SUB_GRID_PROP);
		return loStringBuffer.toString();
	}

	/**
	 * This method returns Header properties of the Fiscal Years
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest Portal Session object
	 * @return a String of Header properties.
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	private String getFiscalYearGrid(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap,
				HHSConstants.GET_FISCAL_YEAR_GRID);
		return loStringBuffer.toString();
	}

	/**
	 * This method returns the string with FY year data populated from the bean
	 * Object
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoBeanObj Object of the Bean class
	 * @param aoRequest Portal Session object
	 * @return a String of FY year data.
	 */
	@SuppressWarnings("rawtypes")
	private String getFYRowData(Object aoBeanObj, PortletRequest aoRequest)
	{
		StringBuffer loStringBuffer = new StringBuffer();
		try
		{
			Map loContractMap = (Map) getContractFiscalYears(aoRequest);
			BaseControllerUtil.fYRowDataUtil(aoBeanObj, loStringBuffer, loContractMap);
		}
		// Exception is thrown
		// from getProperty (due to multiple
		// reasons:IllegalAccessException,InvocationTargetException,NoSuchMethodException)
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("IOException occured in getFYRowData in Base Controller", aoExe);
		}
		return loStringBuffer.toString();
	}

	/**
	 * This method populates the bean object with the FY years data from screen
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoBeanObj Object of the Bean class
	 * @param aoResourceRequest to get screen parameters
	 */
	@SuppressWarnings("rawtypes")
	private void addUpdateFYRowData(Object aoBeanObj, ResourceRequest aoResourceRequest)
	{
		try
		{
			Map loContractMap = (Map) getContractFiscalYears(aoResourceRequest);
			int liYearCount = (Integer) loContractMap.get(HHSConstants.LI_FYCOUNT);
			int liStartFYCounter = (Integer) loContractMap.get(HHSConstants.LI_START_FY_COUNTER);
			String lsFiscalYear = HHSConstants.EMPTY_STRING;
			for (int liCounter = HHSConstants.INT_ONE, liFYCounter = liStartFYCounter; liCounter <= liYearCount; liFYCounter++, liCounter++)
			{
				String lsMethodName = HHSConstants.SMALL_FY + liCounter;
				lsFiscalYear = aoResourceRequest.getParameter(HHSConstants.SMALL_FY
						+ HHSUtil.getFiscalYearCounter(liFYCounter));

				BeanUtils.setProperty(aoBeanObj, lsMethodName, lsFiscalYear);
			}
		}
		// Exception is thrown
		// from getProperty (due to multiple
		// reasons:IllegalAccessException,InvocationTargetException,NoSuchMethodException)
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("IOException occured in addUpdateFYRowData in Base Controller", aoExe);
		}

	}

	/**
	 * <ul>
	 * <li>This method populates the Chart of Account Allocation parent grid
	 * data</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException an Application Exception Object
	 */

	@ResourceMapping("mainAccountGrid")
	public void showAccountMainGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
		PrintWriter loOut = null;
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		StringBuffer loBuffer = new StringBuffer();
		BaseControllerUtil.showAccountMainGridUtil(lsScreen, loBuffer);
		if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
		{
			aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		}
		loBuffer.append(getFiscalYearGrid(aoResourceRequest));
		loBuffer.append("\"total\":\"0\"}]}");
		try
		{
			loOut = aoResourceResponse.getWriter();
			loOut.print(loBuffer.toString());
		}
		// IOException is thrown from sendRedirect in getWriter
		catch (IOException aoExe)
		{
			LOG_OBJECT.Error("IOException occured in showAccountMainGrid", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * This method fetch the sub grid data from database populates the Chart of
	 * Account Allocation Subgrid
	 * <ul>
	 * <li>Fetch the Sub grid data from DB in the form of List</li>
	 * <li>populate the data in the json format.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException ApplicationException object
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("subAccountGrid")
	public void showAccountSubGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
		String lsSelectedList = aoResourceRequest.getParameter(HHSConstants.LIST_NAME);
		String lsTransactionName = lsScreen + HHSConstants.FETCH;
		boolean loProcCerTaskScreen = Boolean.valueOf(aoResourceRequest
				.getParameter(HHSConstants.PROC_CERT_TASK_SCREEN));
		PrintWriter loOut = null;
		try
		{
			PortletSession loPortletSession = aoResourceRequest.getPortletSession();
			loOut = aoResourceResponse.getWriter();
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION, lsTransactionName);
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			StringBuffer loBuffer = new StringBuffer();
			String lsErrorMsg = HHSConstants.EMPTY_STRING;
			String lsAppSettingMapKey = HHSConstants.FINANCIAL_LIST_SCREEN + HHSConstants.UNDERSCORE
					+ HHSConstants.FINANCIAL_VIEW_PER_PAGE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsRowsPerPage = loApplicationSettingMap.get(lsAppSettingMapKey);
			String lsPage = HHSConstants.ONE;
			if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.PAGINATION))
			{
				lsPage = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false, HHSConstants.PAGINATION);
			}
			if (null != PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE, HHSConstants.GRID_ERROR))
			{
				lsErrorMsg = (String) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.FALSE,
						HHSConstants.GRID_ERROR);
			}
			List loAllocationList = null;
			CBGridBean loCBGridBean = null;
			// populate loCBGridBean from session in actual scenario set
			// all values from session
			loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			loCBGridBean.setAgencyId(lsOrgId);
			Channel loChannelObj = new Channel();
			if (lsTransactionName.equals(HHSConstants.NEW_FY_GRID_FETCH))
			{
				loCBGridBean.setIsNewFYScreen(true);
			}
			loCBGridBean.setIsProcCerTaskScreen(loProcCerTaskScreen);
			loCBGridBean.setCoaDocType(false);
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			loAllocationList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			if (lsSelectedList != null && HHSConstants.UPDATE_GRID.equals(lsSelectedList))
			{
				PortletSessionHandler.setAttribute(loAllocationList, aoResourceRequest,
						HHSConstants.CONTRACT_CONFIG_UPDATE_LIST);
			}
			else if (lsSelectedList != null && HHSConstants.ACTUAL_GRID.equals(lsSelectedList))
			{
				PortletSessionHandler.setAttribute(loAllocationList, aoResourceRequest,
						HHSConstants.CONTRACT_CONFIG_ACTUAL_LIST);
			}

			loBuffer.append("\"rows\":[");
			int liScreenRecordCount = HHSConstants.INT_ZERO;
			liScreenRecordCount = populateBuffer(aoResourceRequest, loBuffer, loAllocationList, liScreenRecordCount);
			loBuffer = BaseControllerUtil.showAccountSubGridUtil(loBuffer, lsErrorMsg, lsRowsPerPage, lsPage,
					liScreenRecordCount, HHSConstants.SHOW_ACCOUNT_SUB_GRID);
			LOG_OBJECT.Debug("json for grid: userid:: "
					+ (String) aoResourceRequest.getPortletSession().getAttribute(
							ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE) + "\n json:: "
					+ loBuffer.toString());
			loOut.print(loBuffer.toString());
		}
		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache)
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in showAccountSubGrid while fetching data from database  ",
					aoExe);
		}
		// Exception is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache) executeTransaction (while executing transaction) ,
		// getWriter and due to NumberFormatException from
		// showAccountSubGridUtil
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in showAccountSubGrid while fetching data from database ", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * This method is used to update Buffer for grid
	 * @param aoResourceRequest
	 * @param aoBuffer
	 * @param aoAllocationList
	 * @param aiScreenRecordCount
	 * @return
	 */
	private int populateBuffer(ResourceRequest aoResourceRequest, StringBuffer aoBuffer, List aoAllocationList,
			int aiScreenRecordCount)
	{
		AccountsAllocationBean loAccountsAllocationBean;
		if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
		{
			aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		}
		if (null != aoAllocationList && aoAllocationList.size() > HHSConstants.INT_ZERO)
		{
			for (int liListCounter = HHSConstants.INT_ZERO; liListCounter < aoAllocationList.size(); liListCounter++)
			{
				loAccountsAllocationBean = (AccountsAllocationBean) aoAllocationList.get(liListCounter);
				aoBuffer.append("{\"id\":\"");
				aoBuffer.append(loAccountsAllocationBean.getId());
				aoBuffer.append("\",\"uobc\":\"");
				aoBuffer.append(loAccountsAllocationBean.getChartOfAccount());
				aoBuffer.append("\",\"subOC\":\"");
				aoBuffer.append(loAccountsAllocationBean.getSubOc());
				aoBuffer.append("\",\"rc\":\"");
				aoBuffer.append(loAccountsAllocationBean.getRc());
				aoBuffer.append(getFYRowData(loAccountsAllocationBean, aoResourceRequest));
				aoBuffer.append("\",\"total\":\"");
				aoBuffer.append(loAccountsAllocationBean.getTotal());
				aoBuffer.append("\"},");
				aiScreenRecordCount++;
			}
		}
		return aiScreenRecordCount;
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Chart of Account Allocation Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse decides the next execution flow
	 * @throws ApplicationException ApplicationException object
	 */
	@ResourceMapping("accountOperationGrid")
	@SuppressWarnings("unchecked")
	public void accountOperationGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
		String lsValidationScreen = aoResourceRequest.getParameter(HHSConstants.VALIDATION_SCREEN_NAME);
		Boolean loProcCerTaskScreen = Boolean.valueOf(aoResourceRequest
				.getParameter(HHSConstants.PROC_CERT_TASK_SCREEN));

		try
		{
			Channel loChannelObj = new Channel();

			// initializing the component of chart of account allocation from
			// loAccAllocReadBean
			// An Agency user must enter the Chart of Accounts (CoA)i.e variable
			// lsCoa and the
			// fiscal year dollar amounts for the Contract. Unit of
			// Appropriation (UoA), Budget Code (BC), and Object Code (OC) field
			// is required. Sub-Object Code (SubOC)i.e variable lsSuboc
			// and Reporting Category (RC)i.e variable lsRc
			// are optional fields. Id is the complete chart of allocation
			// value.

			String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
			String lsUobc = aoResourceRequest.getParameter(HHSConstants.UOBC);
			String lsSubOC = aoResourceRequest.getParameter(HHSConstants.SUB_OC);
			String lsRc = aoResourceRequest.getParameter(HHSConstants.RC);
			String lsTotal = aoResourceRequest.getParameter(HHSConstants.TOTAL);
			lsId = StringEscapeUtils.unescapeHtml(lsId);
			lsUobc = StringEscapeUtils.unescapeHtml(lsUobc);
			lsSubOC = StringEscapeUtils.unescapeHtml(lsSubOC);
			lsRc = StringEscapeUtils.unescapeHtml(lsRc);
			if (lsOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation)
					&& !lsId.equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
			{
				lsOperation = HHSConstants.OPERATION_EDIT;
			}
			String lsTransactionName = lsScreen + BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation);
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION, lsTransactionName);
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setIsProcCerTaskScreen(loProcCerTaskScreen);
			loAccountsAllocationBean.setCoaDocType(false);
			CBGridBean loCBGridBean = (CBGridBean) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
			{
				aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
			}
			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoResourceRequest);

			BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(loAccountsAllocationBean, lsId, lsUobc, lsSubOC, lsRc,
					lsTotal, loCBGridBean, loFiscalYrMap);

			if (lsOperation != null
					&& (HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation) || HHSConstants.OPERATION_EDIT
							.equalsIgnoreCase(lsOperation)))
			{
				addNewFiscalYearToGrid(aoResourceRequest, lsOperation, lsValidationScreen, loChannelObj,
						lsTransactionName, loAccountsAllocationBean);
			}
			else if (lsOperation != null && HHSConstants.OPERATION_DELETE.equalsIgnoreCase(lsOperation))
			{
				int liEndIndex = loAccountsAllocationBean.getId().lastIndexOf(HHSConstants.NEW_RECORD_COA);
				if (liEndIndex != -1)
				{
					loAccountsAllocationBean.setId(loAccountsAllocationBean.getId().substring(0, liEndIndex));
				}
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loAccountsAllocationBean);
				HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			}
		}
		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and
		// executeTransaction (while executing transaction)
		// and // when Updated amount is greater than actual amount
		catch (ApplicationException aoExe)
		{
			ApplicationException loAppEx = (ApplicationException) aoExe.getRootCause();
			String lsGridErrorMessage = null;
			if (null != loAppEx)
			{
				lsGridErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			}
			if (null == lsGridErrorMessage || lsGridErrorMessage.trim().equals(HHSConstants.EMPTY_STRING))
			{
				lsGridErrorMessage = HHSConstants.BASE_GRID_ACTION_FAILED_PLEASE_TRY_AGAIN_LATER;
				LOG_OBJECT.Error("ApplicationException occured in accountOperationGrid"
						+ " while performing operation on grid  ", aoExe);
			}
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
		}
		catch (Exception aoExe)
		{
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in accountOperationGrid while performing operation on grid  ", aoExe);
		}
	}

	/**
	 * This method is used to add new fiscal year to grid
	 * @param aoResourceRequest a ResourceRequest object
	 * @param asOperation
	 * @param asValidationScreen
	 * @param aoChannelObj a Channel object
	 * @param asTransactionName
	 * @param aoAccountsAllocationBean AccountsAllocationBean object
	 * @throws ApplicationException
	 */
	private void addNewFiscalYearToGrid(ResourceRequest aoResourceRequest, String asOperation,
			String asValidationScreen, Channel aoChannelObj, String asTransactionName,
			AccountsAllocationBean aoAccountsAllocationBean) throws ApplicationException
	{
		if (HHSConstants.OPERATION_EDIT.equalsIgnoreCase(asOperation)
				&& aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
		{
			PortletSessionHandler.setAttribute(aoResourceRequest.getParameter(HHSConstants.PAGE), aoResourceRequest,
					HHSConstants.PAGINATION);
		}
		addUpdateFYRowData(aoAccountsAllocationBean, aoResourceRequest);
		aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoAccountsAllocationBean);
		if (asValidationScreen != null && HHSConstants.CONTRACT_CONFIG_UPDATE.equalsIgnoreCase(asValidationScreen))
		{
			validateChartAllocationFYI(aoResourceRequest, aoAccountsAllocationBean);
		}
		if (asTransactionName.equals(HHSConstants.NEW_FY_GRID_ADD))
		{
			String lsNewFiscalYear = (String) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR);
			aoAccountsAllocationBean.setNewFYFiscalYearId(lsNewFiscalYear);
		}
		HHSTransactionManager.executeTransaction(aoChannelObj, asTransactionName);
	}

	/**
	 * <ul>
	 * This method validate the chart Allocation FYI
	 * </ul>
	 * 
	 * @param aoResourceRequest resource request
	 * @param aoModifiedAllocationBean AccountsAllocationBean object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void validateChartAllocationFYI(ResourceRequest aoResourceRequest,
			AccountsAllocationBean aoModifiedAllocationBean) throws ApplicationException
	{
		try
		{
			List loActualGridList = (List) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE,
					HHSConstants.CONTRACT_CONFIG_ACTUAL_LIST);
			List loUpdateGridList = (List) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE,
					HHSConstants.CONTRACT_CONFIG_UPDATE_LIST);

			Map<String, Object> loFiscalYrMap = getContractFiscalYears(aoResourceRequest);
			BaseControllerUtil.validateChartAllocationFYIUtil(aoModifiedAllocationBean, loActualGridList,
					loUpdateGridList, loFiscalYrMap);

		}
		// ApplicationException and Exception are thrown from
		// validateChartAllocationFYIUtil
		// when Updated amount is greater than actual amount
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Application Exception occured in BaseController: validateChartAllocationFYI method.",
					aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured in "
					+ "BaseController: validateChartAllocationFYI:: ", loEx);
			loAppEx.addContextData(HHSConstants.GRID_ERROR_MESSAGE, "validation failed");
			LOG_OBJECT.Error("Exception occured in BaseController: validateChartAllocationFYI method:: ", loAppEx);

			throw loAppEx;
		}
	}

	/**
	 * <ul>
	 * <li>This method populates the Funding Source Allocation parent grid data
	 * </ul>
	 * </li>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException an Application Exception Object
	 */
	@ResourceMapping("mainFundingGrid")
	public void showFundingMainGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		PrintWriter loOut = null;
		String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		StringBuffer loBuffer = new StringBuffer();
		BaseControllerUtil.showFundingMainGridUtil(lsScreen, loBuffer);
		if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
		{
			aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		}
		loBuffer.append(getFiscalYearGrid(aoResourceRequest));
		loBuffer.append("\"totalFunding\":\"0\"}]}");
		try
		{
			loOut = aoResourceResponse.getWriter();
			loOut.print(loBuffer.toString());
		}
		// IOException is thrown from sendRedirect in getWriter
		catch (IOException aoExe)
		{
			LOG_OBJECT.Error("IOException occured in showFundingMainGrid", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * This method fetch the sub grid data from database populates the Funding
	 * Source Allocation Subgrid
	 * <ul>
	 * <li>Fetch the Sub grid data from DB in the form of List</li>
	 * <li>populate the data in the json format.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("subFundingGrid")
	public void showFundingSubGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
		String lsTransactionName = lsScreen + HHSConstants.FETCH;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		boolean loProcCerTaskScreen = Boolean.valueOf(aoResourceRequest
				.getParameter(HHSConstants.PROC_CERT_TASK_SCREEN));
		PrintWriter loOut = null;
		try
		{
			loOut = aoResourceResponse.getWriter();
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION, lsTransactionName);
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			StringBuffer loBuffer = new StringBuffer();

			String lsAppSettingMapKey = HHSConstants.FINANCIAL_LIST_SCREEN + HHSConstants.UNDERSCORE
					+ HHSConstants.FINANCIAL_VIEW_PER_PAGE;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsRowsPerPage = loApplicationSettingMap.get(lsAppSettingMapKey);
			String lsPage = HHSConstants.ONE;
			if (aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
			{
				lsPage = aoResourceRequest.getParameter(HHSConstants.PAGE);
			}
			if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.GRID_ERROR))
			{
				lsErrorMsg = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
						HHSConstants.GRID_ERROR);
			}
			FundingAllocationBean loFundingAllocationBean = null;
			List<FundingAllocationBean> loFundingList = null;
			CBGridBean loCBGridBean = null;
			// populate loCBGridBean from session in actual scenario set
			// all values from session
			loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			loCBGridBean.setIsProcCerTaskScreen(loProcCerTaskScreen);
			loCBGridBean.setCoaDocType(false);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			loFundingList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
			loBuffer.append("\"rows\":[");
			if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
			{
				aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
			}
			int liScreenRecordCount = HHSConstants.INT_ZERO;
			if (null != loFundingList)
			{
				for (int liListCounter = HHSConstants.INT_ZERO; liListCounter < loFundingList.size(); liListCounter++)
				{
					loFundingAllocationBean = (FundingAllocationBean) loFundingList.get(liListCounter);
					loBuffer.append("{\"id\":\"");
					loBuffer.append(loFundingAllocationBean.getId());
					loBuffer.append("\",\"fundingType\":\"");
					loBuffer.append(loFundingAllocationBean.getFundingSource());
					loBuffer.append(getFYRowData(loFundingAllocationBean, aoResourceRequest));
					loBuffer.append("\",\"total\":\"");
					loBuffer.append(loFundingAllocationBean.getTotal());
					loBuffer.append("\"},");
					liScreenRecordCount++;
				}
			}
			loBuffer = BaseControllerUtil.showAccountSubGridUtil(loBuffer, lsErrorMsg, lsRowsPerPage, lsPage,
					liScreenRecordCount, HHSConstants.SHOW_FUNDING_SUB_GRID);
			loOut.print(loBuffer.toString());
		}
		// ApplicationException and Exception are thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache)
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in showFundingSubGrid while fetching data from database  ",
					aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in showFundingSubGrid while fetching data from database ", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Funding Source Allocation Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse decides the next execution flow
	 */
	@ResourceMapping("fundingOperationGrid")
	public void fundingOperationGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		fundingOperationGridUtil(aoResourceRequest, HHSConstants.FUNDING_OPERATION_GRID);
	}

	/**
	 * This method perform generic actions for fundingOperationGrid and
	 * procFundingOperationGrid ,on database based on operation performed on
	 * Funding Source Allocation Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoMethodName calling method name
	 */
	public void fundingOperationGridUtil(ResourceRequest aoResourceRequest, String aoMethodName)
	{
		FundingAllocationBean loFundingAllocationBean = new FundingAllocationBean();
		CBGridBean loGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		Boolean loProcCerTaskScreen = Boolean.valueOf(aoResourceRequest
				.getParameter(HHSConstants.PROC_CERT_TASK_SCREEN));
		try
		{
			String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
			String lsFundingType = HHSConstants.EMPTY_STRING;
			if (HHSConstants.FUNDING_OPERATION_GRID.equals(aoMethodName))
			{
				lsFundingType = lsId.split(HHSConstants.HYPHEN)[HHSConstants.INT_ONE];
				String lsTotal = aoResourceRequest.getParameter(HHSConstants.TOTAL);
				loFundingAllocationBean.setTotal(lsTotal);
				loFundingAllocationBean.setModifyByProvider(loGridBean.getModifyByProvider());
				loFundingAllocationBean.setModifyByAgency(loGridBean.getModifyByAgency());
			}
			else if (HHSConstants.PROC_FUNDING_OPERATION_GRID.equals(aoMethodName))
			{
				lsFundingType = aoResourceRequest.getParameter(HHSConstants.FUNDING_TYPE);
			}
			String lsScreen = aoResourceRequest.getParameter(HHSConstants.SCREEN_NAME);
			loFundingAllocationBean.setId(lsId);
			loFundingAllocationBean.setFundingSource(lsFundingType);
			loFundingAllocationBean.setIsProcCerTaskScreen(loProcCerTaskScreen);
			loFundingAllocationBean.setCoaDocType(false);

			String lsTransactionName = lsScreen + BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation);
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION, lsTransactionName);
			if (aoResourceRequest.getParameter(HHSConstants.IS_AMENDMENT_FLOW) != null)
			{
				aoResourceRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
			}
			if (lsOperation != null && HHSConstants.OPERATION_EDIT.equalsIgnoreCase(lsOperation))
			{
				addUpdateFYRowData(loFundingAllocationBean, aoResourceRequest);
				if (lsScreen.equalsIgnoreCase(HHSConstants.CONTRACT_CONFIGURATION_FUNDING_GRID)
						&& HHSConstants.FUNDING_OPERATION_GRID.equals(aoMethodName))
				{
					loGridBean.setGridType(HHSConstants.WF_ENTITY_TYPE_CONTRACT);
				}
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.AO_CB_FUNDING_BEAN_OBJ, loFundingAllocationBean);
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loGridBean);
				HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
			}
		}
		// ApplicationException is thrown from various points
		// from getProperty(if property is not found)
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExe)
		{
			String lsGridErrorMessage = (String) aoExe.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error(
					"ApplicationException occured in" + aoMethodName + " while performing operation on grid  ", aoExe);
		}
		catch (Exception aoExe)
		{
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in fundingOperationGrid while performing operation on grid  ", aoExe);
		}
	}

	/**
	 * This method set the FY years column names which are required for total in
	 * session
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest PortletSession object
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	private void columnsForTotal(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearUtil(loContractMap,
				HHSConstants.COLUMNS_FOR_TOTAL_COUNT);
		if (aoRequest.getAttribute(HHSConstants.IS_AMENDMENT_FLOW) != null)
		{
			aoRequest.getPortletSession().setAttribute(HHSConstants.COLUMNS_FOR_TOTAL_AMENDMENT,
					loStringBuffer.toString(), PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			aoRequest.getPortletSession().setAttribute(HHSConstants.COLUMNS_FOR_TOTAL, loStringBuffer.toString(),
					PortletSession.APPLICATION_SCOPE);
		}
	}

	/**
	 * This method set the Funding Source Allocation grid header details,Header
	 * properties,sub grid header properties and column names which are required
	 * for total in session
	 * 
	 * @param aoSession PortletSession Object
	 * @throws ApplicationException an Application Exception Object
	 */
	protected void setAccountGridDataInSession(PortletRequest aoRequest) throws ApplicationException
	{
		String lsGridColNames = getAccountsMainHeader(aoRequest);
		String lsMainHeaderProp = getAccountsMainHeaderProp(aoRequest);
		String lsSubHeaderProp = getAccountsSubGridProp(aoRequest);
		aoRequest.getPortletSession().setAttribute(HHSConstants.GRID_COL_NAMES, lsGridColNames,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.MAIN_HEADER_PROP, lsMainHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.SUB_HEADER_PROP, lsSubHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		columnsForTotal(aoRequest);
	}

	/**
	 * This method set the Funding Source Allocation grid header details,Header
	 * properties,sub grid header properties and column names which are required
	 * for total in session
	 * 
	 * @param aoSession PortletSession Object
	 * @throws ApplicationException an Application Exception Object
	 */
	protected void setAmendmentAccountGridDataInSession(PortletRequest aoRequest) throws ApplicationException
	{
		aoRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		String loGridColNames = getAccountsMainHeader(aoRequest);
		String loMainHeaderProp = getAccountsMainHeaderProp(aoRequest);
		String loSubHeaderProp = getAccountsSubGridProp(aoRequest);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_GRID_COL_NAMES, loGridColNames,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_MAIN_HEADER_PROP, loMainHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_SUB_HEADER_PROP, loSubHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		columnsForTotal(aoRequest);
	}

	/**
	 * This method set the Chart of Account Allocation grid header
	 * details,Header properties,sub grid header properties and column names
	 * which are required for total in session
	 * 
	 * @param aoRequest PortletSession Object
	 * @throws ApplicationException an Application Exception Object
	 */
	protected void setFundingGridDataInSession(PortletRequest aoRequest) throws ApplicationException
	{
		String lsFundingGridColNames = getFundingMainHeader(aoRequest);
		String lsFundingMainHeaderProp = getFundingsMainHeaderProp(aoRequest);
		String lsFundingSubHeaderProp = getFundingSubGridProp(aoRequest);
		aoRequest.getPortletSession().setAttribute(HHSConstants.FUNDING_GRID_COL_NAMES, lsFundingGridColNames,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.FUNDING_MAIN_HEADER_PROP, lsFundingMainHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.FUNDING_SUB_HEADER_PROP, lsFundingSubHeaderProp,
				PortletSession.APPLICATION_SCOPE);
		columnsForTotal(aoRequest);
	}

	/**
	 * This method set the Chart of Account Allocation grid header
	 * details,Header properties,sub grid header properties and column names for
	 * an amendment which are required for total in session
	 * 
	 * @param aoRequest PortletSession Object
	 * @throws ApplicationException an Application Exception Object
	 */
	protected void setAmendmentFundingGridDataInSession(PortletRequest aoRequest) throws ApplicationException
	{
		aoRequest.setAttribute(HHSConstants.IS_AMENDMENT_FLOW, HHSConstants.TRUE);
		String loFundingGridColNames = getFundingMainHeader(aoRequest);
		String loFundingMainHeaderProp = getFundingsMainHeaderProp(aoRequest);
		String loFundingSubHeaderProp = getFundingSubGridProp(aoRequest);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_FUNDING_GRID_COL_NAMES,
				loFundingGridColNames, PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_FUNDING_MAIN_HEADER_PROP,
				loFundingMainHeaderProp, PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSConstants.AMENDMENT_FUNDING_SUB_HEADER_PROP,
				loFundingSubHeaderProp, PortletSession.APPLICATION_SCOPE);
		columnsForTotal(aoRequest);
	}

	/* Jquery Grid related functions end */

	/**
	 * This method populates the Sub grid header row
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException an Application Exception Object
	 */
	@ResourceMapping("SubGridHeaderRow")
	public void getSubGridHeaderRow(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		StringBuffer loBuffer = null;
		try
		{
			String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
			loBuffer = (StringBuffer) HHSUtil.getSubGridHeaderRow(lsGridLabel);
			aoResourceResponse.getWriter().print(loBuffer.toString());
			aoResourceResponse.getWriter().flush();
			aoResourceResponse.getWriter().close();
		}
		// Exception is thrown from various points
		// from getWriter and from getProperty(if property is not found)
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getSubGridHeaderRow", aoExe);
		}
	}

	/**
	 * This method populates the bean obj passed with the parameter values from
	 * request (jquery grid submission)
	 * <ul>
	 * <li>It will iterate the parameters NAmes and populate the bean object
	 * passed with param values</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @param aoBeanObj bean object to populate
	 * @throws ApplicationException an Application Exception Object
	 * 
	 */
	protected void populateBeanFromRequest(ResourceRequest aoResourceRequest, Object aoBeanObj)
			throws ApplicationException
	{
		Enumeration<String> loEnumeration = aoResourceRequest.getParameterNames();
		while (loEnumeration.hasMoreElements())
		{
			String lsParamName = loEnumeration.nextElement().trim();
			String lsParamValue = aoResourceRequest.getParameter(lsParamName).trim();
			BaseControllerUtil.populateBeanFromRequestUtil(aoBeanObj, lsParamName, lsParamValue);
		}
	}

	/*
	 * ######### GRid Flow starts for static header grids#################
	 */
	/**
	 * This method fetch the sub grid data from database populates the Subgrid
	 * <ul>
	 * <li>Fetch the Sub grid data from DB in the form of List</li>
	 * <li>populate the data in the json format.</li>
	 * </ul>
	 * * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * Method Updated in R4
	 * @param aoResourceRequest to get screen parameters
	 * @param aoResourceResponse used to get the PrintWriter object
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("loadGridData")
	public void loadGridData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		PrintWriter loOut = null;
		MasterBean loMasterBean = null;
		HashMap<String, Object> loHashMap = null;
		try
		{
			synchronized (this)
			{
				loOut = aoResourceResponse.getWriter();
				PortletSession loSession = aoResourceRequest.getPortletSession();
				P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
						ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
				String lsGridLabel = aoResourceRequest.getParameter(HHSConstants.GRID_LABEL);
				LOG_OBJECT.Debug("====GRID_LABEL :: " + lsGridLabel);
				String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
				LOG_OBJECT.Debug("====TRANSACTION_NAME :: " + lsTransactionName);
				String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
				StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
				loPropertyName.append(HHSConstants.FETCH);
				lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
						loPropertyName.toString());
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				String lsAppSettingMapKey = HHSConstants.FINANCIAL_LIST_SCREEN + HHSConstants.UNDERSCORE
						+ HHSConstants.FINANCIAL_VIEW_PER_PAGE;
				
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				
				String lsRowsPerPage = loApplicationSettingMap.get(lsAppSettingMapKey);
				String lsPage = HHSConstants.ONE;
				String lsPreserveInvoiceId = null;
				String lsPreserveBudgetId = null;
				if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.PAGINATION))
				{					
					lsPage = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
							HHSConstants.PAGINATION);
				}
				String lsErrorMsg = HHSConstants.EMPTY_STRING;
				if (null != PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.GRID_ERROR))
				{
					lsErrorMsg = (String) PortletSessionHandler.getAttribute(aoResourceRequest, false,
							HHSConstants.GRID_ERROR);
				}

				CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
						HHSConstants.CBGRIDBEAN_IN_SESSION);
				
				String lsPageLevelInvoiceId = (String) PortletSessionHandler.getAttribute(aoResourceRequest,
						HHSConstants.PAGE_LEVEL_INVOICE_ID);
				String lsPageLevelBudgetId = (String) PortletSessionHandler.getAttribute(aoResourceRequest,
						HHSConstants.BUDGET_ID);
								
				if (loCBGridBean == null)
				{
					loCBGridBean = new CBGridBean();
				}
				// fix done as a part of release 3.1.2 defect 6420 - start.
				lsPreserveInvoiceId = loCBGridBean.getInvoiceId();
				lsPreserveBudgetId = loCBGridBean.getContractBudgetID();
				if (lsPageLevelInvoiceId != null)
				{
					loCBGridBean.setInvoiceId(lsPageLevelInvoiceId);
					loCBGridBean.setContractBudgetID(lsPageLevelBudgetId);
				}

				// fix done as a part of release 3.1.2 defect 6420 - end.
				String lsUserId = (String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);

				loCBGridBean.setCreatedByUserId(lsUserId);
				loCBGridBean.setModifiedByUserId(lsUserId);
				loCBGridBean.setSubBudgetID(aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID));
				loCBGridBean.setParentSubBudgetId(aoResourceRequest.getParameter(HHSConstants.PARENT_SUBBUDGET_ID));
				loCBGridBean.setTransactionName(lsTransactionName);
				
				// Begin: Fix Multi-tab Browsing QC8691 ... this is for displaying on grid
				String subBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
				LOG_OBJECT.Debug("tab browsing grid subBudgetId = " + subBudgetId);
				if (subBudgetId != null && !subBudgetId.isEmpty()) {
					String budgetId = getBudgetIdFromSubBudget(aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID));
					LOG_OBJECT.Debug("tab browsing grid budgetId = " + budgetId);
					if (budgetId !=null && !budgetId.isEmpty())
						loCBGridBean.setContractBudgetID(budgetId);
					    aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID, budgetId);
				}
				// End: Fix Multi-tab Browsing QC8691

				String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_BUDGET_APPROVED);

				//R7 start::: Updated condition for approved modification
				if (null != loCBGridBean.getBudgetTypeId() && (loCBGridBean.getBudgetTypeId().equals(HHSConstants.ONE)||
						loCBGridBean.getBudgetTypeId().equals(HHSConstants.THREE))
						&& loCBGridBean.getBudgetStatusId().equals(lsBudgetStatus))
				{
					loHashMap = (HashMap<String, Object>) loSession.getAttribute(HHSConstants.MASTERBEAN_HASHMAP,
							PortletSession.APPLICATION_SCOPE);

					
					if ((loHashMap == null) || (!loHashMap.containsKey(loCBGridBean.getContractBudgetID())))
					{
						loHashMap = (loHashMap == null) ? new HashMap<String, Object>() : loHashMap;
						loMasterBean = BaseControllerUtil.generateMasterBean(loCBGridBean.getContractBudgetID(),
								loUserSession);
						loHashMap.put(loCBGridBean.getContractBudgetID(), loMasterBean);
						loSession.setAttribute(HHSConstants.MASTERBEAN_HASHMAP, loHashMap,
								PortletSession.APPLICATION_SCOPE);
					}
					loMasterBean = (MasterBean) loHashMap.get(loCBGridBean.getContractBudgetID());
				}
				//R7: Added for Program Income
				String lsEntryTypeId = aoResourceRequest.getParameter(HHSConstants.ENTRY_TYPE_ID);
				if(lsEntryTypeId!=null && lsEntryTypeId.equals(HHSConstants.EMPTY_STRING))
				{
					lsEntryTypeId=null;
				}
				loCBGridBean.setEntryTypeId(lsEntryTypeId);
				
				//R7: Added for inserting entry type id in invoice details  for Program Income grid in budget categories
				String lsPIEntryTypeId = aoResourceRequest.getParameter(HHSConstants.ENTRY_TYPE_ID);
				if (StringUtils.isNotBlank(lsPIEntryTypeId))
				{
					loCBGridBean.setPIEntryTypeId(lsPIEntryTypeId);
				}
				//R7 changes end
				Channel loChannelObj = new Channel();
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				loChannelObj.setData(HHSConstants.MASTERBEAN_OBJ, loMasterBean);
				HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);
				List loReturnedGridList = (List) loChannelObj.getData(HHSConstants.AO_RETURNED_GRID_LIST);
				//Start QC 8394 R 7.8.0 add/delete functionality for unallocated funds
				
				if (loReturnedGridList != null && lsTransactionName.contains("UnallocatedFunds"))
				{
					PortletSessionHandler.setAttribute(loReturnedGridList, aoResourceRequest,
							"UNALLOCATED_FUNDS_LIST");
					LOG_OBJECT.Debug("UNALLOCATED_FUNDS_LIST SET INTO SESSION:: "
										+PortletSessionHandler.getAttribute(aoResourceRequest, true, "UNALLOCATED_FUNDS_LIST"));
					//loSession.setAttribute("UNALLOCATED_FUNDS_LIST", loReturnedGridList,
							//PortletSession.APPLICATION_SCOPE);
				}
				//End QC 8394 R 7.8.0 add/delete functionality for unallocated funds
				/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
				lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
				/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
				Class loClass = Class.forName(lsClass);
				Object loBeanObj = loClass.newInstance();
				StringBuffer loBuffer = HHSUtil.populateSubGridRows(loBeanObj, loReturnedGridList, lsRowsPerPage,
						lsPage, lsErrorMsg, lsGridLabel);
				LOG_OBJECT.Debug("json for loadGridData: userid:: "
						+ (String) aoResourceRequest.getPortletSession().getAttribute(
								ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)
						+ "\n json:: " + loBuffer.toString());
				if ((lsTransactionName.equalsIgnoreCase(HHSConstants.FRINGE_BENEFITS_GRID_AMENDMENT_FETCH) || lsTransactionName
						.equalsIgnoreCase(HHSConstants.FRINGE_BENEFITS_GRID_MODIFICATION_FETCH))
						&& loBuffer.toString().contains(HHSConstants.FRINGE_BENEFITS)
						&& loBuffer.toString().contains(HHSConstants.NEW_RECORD))
				{
					loOut.print(loBuffer.toString().replaceAll(HHSConstants.NEW_RECORD, HHSConstants.EMPTY_STRING));
				}
				else
				{
					loOut.print(loBuffer.toString().replaceAll("\\\\'", "'"));
				}
				loCBGridBean.setInvoiceId(lsPreserveInvoiceId);
				loCBGridBean.setContractBudgetID(lsPreserveBudgetId);
			}
		}

		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache)
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExe)
		{
			// Set the error log if any application exception occurs
			LOG_OBJECT.Error("ApplicationException occured in loadGridData while fetching data from database ",
					aoAppExe);
		}
		// ApplicationException is thrown from various points
		// from getProperty(if property is not found) and getCacheObject(getting
		// object from cache),from forName and newInstance and from
		// executeTransaction (while executing transaction)
		catch (Exception aoExe)
		{
			// Set the error log if any application exception occurs
			LOG_OBJECT.Error("Exception occured in loadGridData while fetching data from database  ", aoExe);
		}
		finally
		{
			BaseControllerUtil.closingPrintWriter(loOut);
		}
	}

	/**
	 * This method perform actions on database based on operation performed on
	 * Subgrid
	 * <ul>
	 * <li>If add operation perform on the grid insert row into database</li>
	 * <li>If delete operation perform on the grid delete row from database</li>
	 * <li>If edit operation perform on the grid update row into database</li>
	 * </ul>
	 * * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @throws ApplicationException an Application Exception Object
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("gridOperation")
	public void gridOperation(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		String lsBudgetId = null;
		String lsOperation = aoResourceRequest.getParameter(HHSConstants.GRID_OPERATION);
		String lsTransactionName = aoResourceRequest.getParameter(HHSConstants.TRANSACTION_NAME);
		String lsClass = aoResourceRequest.getParameter(HHSConstants.BEAN_NAME);
		String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
		String lsParentSubBudgetId = aoResourceRequest.getParameter(HHSConstants.PARENT_SUBBUDGET_ID);
		String lsPageLevelInvoiceId = aoResourceRequest.getParameter(HHSConstants.INVOICE_ID);
		String lsPageLevelBudgetId = aoResourceRequest.getParameter(HHSConstants.BUDGET_ID);
		// Start: Added in R6
		String lsExistingBudget = aoResourceRequest.getParameter(HHSR5Constants.EXISTING_BUDGET);
		// End: Added in R6
		
		LOG_OBJECT.Debug("gridOperation = " + lsOperation);
		LOG_OBJECT.Debug("lsTransactionName = " + lsTransactionName);
		LOG_OBJECT.Debug("lsClass = " + lsClass);
		// Begin: Fix Multi-tab Browsing QC8691 ... this is for saving in db
		LOG_OBJECT.Debug("tab browsing saving db subBudgetId = " + lsSubBudgetId);
		if (lsSubBudgetId != null && !lsSubBudgetId.isEmpty()) 
			lsBudgetId = getBudgetIdFromSubBudget(lsSubBudgetId);
		// Start: QC 8394 R 7.8.0 Add multiple lines to UnallocatedFunds
		List<UnallocatedFunds> loUnallocatedFundsList = null;
		// End: QC 8394 R 7.8.0 Add multiple lines to UnallocatedFunds
		// End: Fix Multi-tab Browsing QC8691
		String lsId = aoResourceRequest.getParameter(HHSConstants.ID);
		StringBuffer loPropertyName = new StringBuffer(lsTransactionName);
		if (lsOperation != null && HHSConstants.OPERATION_ADD.equalsIgnoreCase(lsOperation)
				&& !lsId.equalsIgnoreCase(HHSConstants.NEW_ROW_IDENTIFIER))
		{			
			lsOperation = HHSConstants.OPERATION_EDIT;
		}
		if (aoResourceRequest.getParameter(HHSConstants.PAGE) != null)
		{
			PortletSessionHandler.setAttribute(aoResourceRequest.getParameter(HHSConstants.PAGE), aoResourceRequest,
					HHSConstants.PAGINATION);
		}
		loPropertyName.append(BaseControllerUtil.lsOperationUpperCaseUtil(lsOperation));
		try
		{
			Channel loChannelObj = new Channel();
			lsTransactionName = PropertyLoader.getProperty(HHSConstants.PROPERTIES_GRIDTRANSACTION,
					loPropertyName.toString());
			/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			lsClass = HHSUtil.checkClassAccessControl(lsClass); // throws ApplicationException if not valid class
			/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
			Class loClass = Class.forName(lsClass);
			Object loBeanObj = loClass.newInstance();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			//Start QC 8394 R 7.9.0 Add multiple lines to UnallocatedFunds
			if(lsTransactionName.contains("UnallocatedFunds"))
			{
				loUnallocatedFundsList =(List<UnallocatedFunds>) PortletSessionHandler.getAttribute(aoResourceRequest, true,
				 																					"UNALLOCATED_FUNDS_LIST");
				LOG_OBJECT.Debug("UNALLOCATED_FUNDS_LIST get from SESSION:: "+loUnallocatedFundsList);
			}
			//End QC 8394 R 7.9.0 Add multiple lines to UnallocatedFunds
			if (loCBGridBean == null)
			{
				loCBGridBean = new CBGridBean();
			}
			// R6 changes Starts
			if (StringUtils.isNotBlank(lsExistingBudget))
			{
				loCBGridBean.setUsesFte(lsExistingBudget);
				// Start: Added for Defect-8478
				loCBGridBean.setNewRecord(lsId);
				// End: Added for Defect-8478
			}
			// R6 changes ends
			//R7: Added for Program Income
			String lsEntryTypeId = aoResourceRequest.getParameter(HHSConstants.ENTRY_TYPE_ID);
			if (StringUtils.isNotBlank(lsEntryTypeId))
			{
				loCBGridBean.setEntryTypeId(lsEntryTypeId);
			}
			//R7: Added for inserting entry type id in invoice details  for Program Income grid in budget categories
			String lsPIEntryTypeId = aoResourceRequest.getParameter(HHSR5Constants.PI_ENTRY_TYPE_ID);
			if (StringUtils.isNotBlank(lsPIEntryTypeId))
			{
				loCBGridBean.setPIEntryTypeId(lsPIEntryTypeId);
			}
			//R7 changes end
			// fix done as a part of release 3.1.2 defect 6420 - start
			if (lsPageLevelInvoiceId == null
					|| (loCBGridBean.getInvoiceId() != null && lsPageLevelInvoiceId.equalsIgnoreCase(loCBGridBean
							.getInvoiceId())))
			{
				populateBeanFromRequest(aoResourceRequest, loBeanObj);
				BeanUtils.setProperty(loBeanObj, HHSConstants.MOD_BY_USER_ID, aoResourceRequest.getPortletSession()
						.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
				BeanUtils.setProperty(loBeanObj, HHSConstants.CREATED_BY_USER_ID, aoResourceRequest.getPortletSession()
						.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));
				BaseControllerUtil.settingGridBeanObj(lsTransactionName, lsSubBudgetId, lsBudgetId, lsParentSubBudgetId, loBeanObj,
						loCBGridBean);
				
				//QC 8394 R 7.9 Add multiple lines to UnallocatedFunds 
				if(loUnallocatedFundsList!=null && loBeanObj!=null)
				{    
					String beanId = BeanUtils.getSimpleProperty(loBeanObj, HHSConstants.ID);
					String tempId = null;
					//set the type: base or new 
					for (UnallocatedFunds uf : loUnallocatedFundsList)
					{       
							tempId = uf.getId();
							{
								tempId = tempId.replace("_newrecord", "").trim();
							}
							
							if ( null!=tempId && tempId.equals(beanId) )
							{
								BeanUtils.setProperty(loBeanObj, HHSConstants.TYPE, uf.getType());
								BeanUtils.setProperty(loBeanObj, HHSConstants.CHILDID, uf.getChildId());
								BeanUtils.setProperty(loBeanObj, HHSConstants.PARENTID, uf.getParentId());
								if(BeanUtils.getSimpleProperty(loBeanObj, HHSConstants.UNALLOCATED_FUND)== null)
								{
									BeanUtils.setProperty(loBeanObj, HHSConstants.UNALLOCATED_FUND, uf.getUnallocatedFund());
								}
								 break;
							}
					}
				}
				LOG_OBJECT.Debug("====loBeanObj ::  "+ loBeanObj);				
				BaseControllerUtil
						.executeStaticGridTransaction(lsOperation, lsTransactionName, loChannelObj, loBeanObj);
			}
			else
			{
				PortletSessionHandler.setAttribute(lsPageLevelBudgetId, aoResourceRequest, HHSConstants.BUDGET_ID);
				PortletSessionHandler.setAttribute(lsPageLevelInvoiceId, aoResourceRequest,
						HHSConstants.PAGE_LEVEL_INVOICE_ID);
				PortletSessionHandler.setAttribute(PropertyLoader.getProperty(
						HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MULTIPLE_INVOICE_OPEN),
						aoResourceRequest, HHSConstants.GRID_ERROR);
			}
			// fix done as a part of release 3.1.2 defect 6420 - ends
		}
		// Application exception is handled here.
		catch (ApplicationException aoAppExe)
		{
			// If Application exception occur then get root cause ,get its
			// context data, store message in session and add error log.
			String lsGridErrorMessage = null;
			ApplicationException loAppEx = (ApplicationException) aoAppExe.getRootCause();
			if (null != loAppEx)
			{
				lsGridErrorMessage = (String) loAppEx.getContextData().get(HHSConstants.GRID_ERROR_MESSAGE);
			}
			if (null == lsGridErrorMessage)
			{
				lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
				LOG_OBJECT
						.Error("ApplicationException occured in gridOperation method while performing operation on grid  "
								+ aoAppExe);
			}
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
		}
		// Exception is thrown from
		// executeTransaction (while executing transaction)
		catch (Exception aoExe)
		{
			// If exception occur then store the error message in session and
			// set error log.
			String lsGridErrorMessage = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			PortletSessionHandler.setAttribute(lsGridErrorMessage, aoResourceRequest, HHSConstants.GRID_ERROR);
			LOG_OBJECT.Error("Error occured in gridOperation method while performing operation on grid  ", aoExe);
		}
	}

	/*
	 * ######GRid Flow ends for static header grids######
	 */

	/**
	 * This method is called when any tab is clicked on contract budget screen.
	 * By Default when we click sub budget accordion, then summary screen is
	 * loaded. Updated for Release 3.4.0, #5681
	 * <ul>
	 * <li>Tab name, SubBudgetId and lsReadOnlyPage are taken from request.</li>
	 * <li>subBudgetId is set in session.</li>
	 * <li>On basis of tab which has been hit,corresponding transaction is
	 * called and data is set in session so that it can be displayed on jsp.</li>
	 * <li>Execute transaction <b>getNonGridContractedService</b></li>
	 * <li>like on click of rate in tab, it will redirect to the rate.jsp</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <li>Updated in Release 6</li>
	 * <li>Updated in Release 6 - Returned Payment for returned payment
	 * Accordion on contract budget landing screen.</li>
	 * </ul>
	 * @param aoResourceRequest to get screen parameters
	 * @return loModelAndView
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("showCBGridTabs")
	public ModelAndView showBudgetTabJsp(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,	HHSConstants.CBGRIDBEAN_IN_SESSION);
		Channel loChannelObj = showGridVariablePara(aoResourceRequest, loCBGridBean, HHSConstants.CONTRACT_BUDGET_NAME);
		try
		{
			if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.CONTRACT_BUDGET_SUMMARY))
			{
				String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
				String lsTabId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_ID);
				String lsIsPrinterFriendly = aoResourceRequest.getParameter(HHSConstants.BASE_PRINTER_FRIENDLY);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_BUDGET_SUMMARY);
				ContractBudgetSummary loBudgetSummary = (ContractBudgetSummary) loChannelObj
						.getData(HHSConstants.LO_BUDGET_SUMMARY);
				List<Integer> loTabToHighlightList = (List<Integer>) loChannelObj
						.getData(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST);
				loPortletSession.setAttribute(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST + lsSubBudgetId,
						loTabToHighlightList, PortletSession.APPLICATION_SCOPE);
				String lsIndirectRate = (String) loChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
				// Release 3.6.0 Enhancement id 6484
				List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) loChannelObj
						.getData(HHSConstants.SITE_DETAIL_LIST);
				String lsSubBudgetStatusId = (String) loChannelObj.getData(HHSConstants.LS_SUB_BUDGET_STATUS_ID);
				Boolean loRecordBeforeRelease = (Boolean) loChannelObj.getData(HHSConstants.RECORD_BEFORE_RELEASE);
				loPortletSession.setAttribute(HHSConstants.BUDGET_SUMMARY, loBudgetSummary,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate,
						PortletSession.APPLICATION_SCOPE);
				aoResourceRequest.setAttribute(HHSConstants.SUB_BUDGET_ID, loCBGridBean.getSubBudgetID());
				aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, loCBGridBean.getContractID());
				aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW, loCBGridBean.getContractBudgetID());
				aoResourceRequest.setAttribute(HHSConstants.BASE_HDN_TAB_ID, lsTabId);
				aoResourceRequest.setAttribute(HHSConstants.RECORD_BEFORE_RELEASE, loRecordBeforeRelease);
				aoResourceRequest.setAttribute(HHSConstants.LS_SUB_BUDGET_STATUS_ID, lsSubBudgetStatusId);
				aoResourceRequest.setAttribute(HHSConstants.BASE_PRINTER_FRIENDLY, lsIsPrinterFriendly);
				return new ModelAndView(HHSConstants.JSP_CONTRACTBUDGET + lsJspName,
						HHSConstants.SITE_DETAILS_BEAN_UPPERCASE, loSiteDetails);
			}
			// Added for Release 3.2.4, #5681 - Start
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.CONTRACT_BUDGET_PRINT_SUMMARY))
			{
				fetchbudgetPrintSummary(loCBGridBean, loChannelObj, aoResourceRequest);
			}
			// Added for Release 3.2.4, #5681 - Ends
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.INDIRECT_RATE_JSP_NAME))
			{
				fetchIndirectPercentage(loPortletSession, loCBGridBean, loChannelObj);
			}
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.OPERATION_AND_SUPPORT_JSP_NAME))
			{
				fetchOpAndSupportData(aoResourceRequest, loPortletSession, loChannelObj);
			}
			// Start : Updated in Release 6
			// Start: Added in Defect-8470
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.PERSONNEL_SERVICES_JSP_NAME)
					|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_TAB)
					|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERIVES_DETAIL_JSP)
					|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_SUMMARY))
			// End: Added in Defect-8470
			// End : Updated in Release 6
			{
				fetchPersonnelServicesData(loPortletSession, loChannelObj, aoResourceRequest);
			}
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSConstants.CONTRACTED_SERVICES))
			{
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObj
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				if (null != loCBContractedServicesBean)
				{
					loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
							PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					loCBContractedServicesBean = new ContractedServicesBean();
					loCBContractedServicesBean.setTotalContractedServices(HHSConstants.ZERO);
					loCBContractedServicesBean.setYtdTotalInvoiceAmt(HHSConstants.ZERO);
					loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
							PortletSession.APPLICATION_SCOPE);
				}

			}
			// Starts : Release 6 : Gets Returned Payment Accordion Data
			else if (null != lsJspName && lsJspName.equalsIgnoreCase(HHSR5Constants.STRING_RETURNED_PAYMENT))
			{
				ReturnedPayment loReturnedPayment = new ReturnedPayment();
				loReturnedPayment.setBudgetId(aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
				loChannelObj.setData(HHSR5Constants.LO_RETURNED_PAYMENT, loReturnedPayment);
				loChannelObj.setData(HHSR5Constants.LO_BUDGET_ID,aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));

				//[Start] QC9701
				PortletSession   aoSession = aoResourceRequest.getPortletSession();
	            String loOrg     = (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,   PortletSession.APPLICATION_SCOPE);

	            Map<Integer,String> loMap = ActionStatusUtil.getMoActionMapByAgency(loOrg);
	            
	            LOG_OBJECT.Debug("#########Org ID:"+loOrg + "     $$$$ Return Payment Action:"      + ActionStatusUtil.isActionEnabled(loOrg, HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX) );
	            if( (loMap != null &&  loMap.get(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX) != null ) ) {
	            	LOG_OBJECT.Debug("#########Org ID:"+loOrg + "     $$$$ Return Payment Action map:" + loMap.get(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX).equalsIgnoreCase("1"));
	            }

	            LOG_OBJECT.Debug(""+ActionStatusUtil.getMoActionMapByAgency(loOrg).toString()) ;
	            if( (loMap != null &&  loMap.get(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX) != null 
						&& loMap.get(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX).equalsIgnoreCase("1") ) ) {
					aoResourceRequest.setAttribute(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_STATUS, String.valueOf(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX) );
		            LOG_OBJECT.Debug("ACTION_DROPDOWN_RETURN_PAYMENT_INX     $$$$ Return Payment Action:"+ ActionStatusUtil.isActionEnabled(loOrg, HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX));
				}else {
					aoResourceRequest.setAttribute(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_STATUS, String.valueOf(HHSR5Constants.ACTION_DROPDOWN_AGENCY_ID_INX) );
		            LOG_OBJECT.Debug("ACTION_DROPDOWN_AGENCY_ID_INX     $$$$ Return Payment Action:"+ ActionStatusUtil.isActionEnabled(loOrg, HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX));
				}
				//[End] QC9701
				
				fetchReturnPaymentDetails(aoResourceRequest, loChannelObj);
			}
			// Fix for Defect : 8565 in R6 - Returned Payment
			else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY))
			{
				fetchReturnPaymentList(aoResourceRequest, loChannelObj);
			}
			// Ends : Release 6
          //Added for R7 PI
				fetchProgramIncomeData(loPortletSession, loChannelObj, aoResourceRequest);

		}
		// ApplicationException is thrown from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in showBudgetTabJsp method while performing actions on click of tab.",
					aoAppExp);
		}

		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Error occured in showBudgetTabJsp method while performing actions on click of tab.",
					aoExe);
		}

		String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method fetches Op and support data
	 * @param aoResourceRequest resource request
	 * @param aoChannelObj Channel object
	 * @param aoPortletSession portal Session
	 * @throws ApplicationException AccountsAllocationBean
	 */
	private void fetchOpAndSupportData(ResourceRequest aoResourceRequest, PortletSession aoPortletSession,
			Channel aoChannelObj) throws ApplicationException
	{
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_OPERATION_SUPP_PAGE_DATA);
		CBOperationSupportBean loCBOperationSupportBean = (CBOperationSupportBean) aoChannelObj
				.getData(HHSConstants.CB_OPERATION_SUPPORT_BEAN);
		aoPortletSession.setAttribute(HHSConstants.OPERATION_AND_SUPPORT_DATA, loCBOperationSupportBean,
				PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * <li>updated for Defect-8470</li>
	 * @param aoPortletSession Portal Session object
	 * @param aoChannelObj channel object
	 * @param aoResourceRequest
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void fetchPersonnelServicesData(PortletSession aoPortletSession, Channel aoChannelObj,
			ResourceRequest aoResourceRequest) throws ApplicationException
	{
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_PERSONNEL_SERVICES_DATA);
		PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) aoChannelObj
				.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
		String lsPersonnelServiceMasterData = (String) aoChannelObj.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
		aoPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
				PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA, lsPersonnelServiceMasterData,
				PortletSession.APPLICATION_SCOPE);
		// Start: Added in Defect-8470
		aoPortletSession.setAttribute(HHSConstants.BUDGET_STATUS,
				aoResourceRequest.getParameter(HHSR5Constants.HIDDEN_TASK_STATUS), PortletSession.APPLICATION_SCOPE);
		// End: Added in Defect-8470
	}

	/**
	 * This method is used to fetch indirect rate percentage on indirect and
	 * budget summary screen.
	 * 
	 * @param aoPortletSession Portal Session
	 * @param aoChannelObj channel object
	 * @param aoCBGridBean Grid Standard Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void fetchIndirectPercentage(PortletSession aoPortletSession, CBGridBean aoCBGridBean, Channel aoChannelObj)
			throws ApplicationException
	{
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_INDIRECT_RATE_PERCENTAGE);
		String lsIndirectRatePercentage = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
		aoPortletSession.setAttribute(HHSConstants.INDIRECT_PERCENTAGE, lsIndirectRatePercentage,
				PortletSession.APPLICATION_SCOPE);
		//Start : R7 Program Income changes to fetch program income indirect rate percentage
		aoPortletSession.setAttribute(HHSR5Constants.PI_INDIRECT_PERCENTAGE,
				(String) aoChannelObj.getData(HHSR5Constants.PI_INDIRECT_RATE_KEY), PortletSession.APPLICATION_SCOPE);
		//End : R7 Program Income changes
	}

	/**
	 * This method perform actions on click of tab.
	 * <ul>
	 * <li>On click of tab this method will redirect to the same name of jsp
	 * file.</li>
	 * <li>like on click of rate in tab, it will redirect to the rate.jsp</li>
	 * <li>if jsp name is modificationBudgetSummary then it will get subBudgetId
	 * and parentSUbBudgetid from request and will set it in CB Grid Bean</li>
	 * <li>then fetchModificationBudgetSummary transaction will be called which
	 * will get the modification summary data</li>
	 * <li>Execute transaction <b>getNonGridContractedServicesModification</b></li>
	 * <li>budgetSummary and indirectRate will be set in session so that data
	 * can be displayed on jsp.</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <li>Updated in Release 6</li>
	 * <li>Updated in Release 6 - Returned Payment for returned payment
	 * Accordion on contract budget landing screen.</li>
	 * <li>Updated in Release 7 - for showing approved modification.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @return loModelAndView
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("showCBModificationGridTabs")
	public ModelAndView showCBModificationGridTabs(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		//Added for R7 for showing approved modification
		MasterBean loMasterBean = null;
		HashMap<String, Object> loHashMap = null;
		P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);
		if (loCBGridBean.getBudgetTypeId().equals(HHSConstants.THREE)
				&& loCBGridBean.getBudgetStatusId().equals(lsBudgetStatus))
		{
			loHashMap = (HashMap<String, Object>) loPortletSession.getAttribute(HHSConstants.MASTERBEAN_HASHMAP,
					PortletSession.APPLICATION_SCOPE);

			if ((loHashMap == null) || (!loHashMap.containsKey(loCBGridBean.getContractBudgetID())))
			{
				loHashMap = (loHashMap == null) ? new HashMap<String, Object>() : loHashMap;
				loMasterBean = BaseControllerUtil.generateMasterBean(loCBGridBean.getContractBudgetID(), loUserSession);
				loHashMap.put(loCBGridBean.getContractBudgetID(), loMasterBean);
				loPortletSession.setAttribute(HHSConstants.MASTERBEAN_HASHMAP, loHashMap,
						PortletSession.APPLICATION_SCOPE);
			}
			loMasterBean = (MasterBean) loHashMap.get(loCBGridBean.getContractBudgetID());
		}
		//R7 end
		Channel loChannelObj = showGridVariablePara(aoResourceRequest, loCBGridBean, HHSConstants.MODIFICATION);
		//Added for R7 for showing approved modification
		loChannelObj.setData(HHSConstants.MASTERBEAN_OBJ, loMasterBean);
		//R7 end
		if (lsJspName.equalsIgnoreCase(HHSConstants.MODIFICATION_BUDGET_SUMMARY))
		{
			String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
			String lsTabId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_ID);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_MODIFICATION_BUDGET_SUMMARY);
			ContractBudgetSummary loBudgetSummary = (ContractBudgetSummary) loChannelObj
					.getData(HHSConstants.LO_BUDGET_SUMMARY);
			List<Integer> loTabToHighlightList = (List<Integer>) loChannelObj
					.getData(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST);
			loPortletSession.setAttribute(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST + lsSubBudgetId,
					loTabToHighlightList, PortletSession.APPLICATION_SCOPE);
			String lsIndirectRate = (String) loChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
			// Release 3.6.0 Enhancement id 6484
			List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) loChannelObj
					.getData(HHSConstants.SITE_DETAIL_LIST);
			String lsSubBudgetStatusId = (String) loChannelObj.getData(HHSConstants.LS_SUB_BUDGET_STATUS_ID);
			Boolean loRecordBeforeRelease = (Boolean) loChannelObj.getData(HHSConstants.RECORD_BEFORE_RELEASE);
			loPortletSession.setAttribute(HHSConstants.BUDGET_SUMMARY, loBudgetSummary,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(HHSConstants.BUDGET_TYPE, loCBGridBean.getBudgetTypeId(),
					PortletSession.APPLICATION_SCOPE);
			aoResourceRequest.setAttribute(HHSConstants.BASE_HDN_TAB_ID, lsTabId);
			aoResourceRequest.setAttribute(HHSConstants.RECORD_BEFORE_RELEASE, loRecordBeforeRelease);
			aoResourceRequest.setAttribute(HHSConstants.LS_SUB_BUDGET_STATUS_ID, lsSubBudgetStatusId);
			return new ModelAndView(HHSConstants.JSP_CONTRACTBUDGET + lsJspName,
					HHSConstants.SITE_DETAILS_BEAN_UPPERCASE, loSiteDetails);

		}
		// Start : Updated in R6
		// Update for Defect-8470
		else if (lsJspName.equalsIgnoreCase(HHSConstants.PERSONNEL_SERVICES_MODIFICATION_JSP_NAME)
				|| lsJspName.equalsIgnoreCase(HHSConstants.PERSONNEL_SERVICES_UPDATE_JSP_NAME)
				|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_MODIFICATION_SUMMARY)
				|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERIVES_UPDATE_SUMMARY_JSP)
				|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_UPDATE_TAB)
				|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_MODIFICATION_TAB))
		// End : Updated in R6
		{
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PERSONNEL_SERVICES_DATA);
			PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObj
					.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
			String lsPersonnelServiceMasterData = (String) loChannelObj.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
			loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
					PortletSession.APPLICATION_SCOPE);
			loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA, lsPersonnelServiceMasterData,
					PortletSession.APPLICATION_SCOPE);
		}
		else if (lsJspName.equalsIgnoreCase(HHSConstants.MODIFICATION_INDIRECT_RATE_JSP_NAME)
				|| lsJspName.equalsIgnoreCase(HHSConstants.UPDATE_INDIRECT_RATE_JSP_NAME))
		{
			fetchIndirectPercentage(loPortletSession, loCBGridBean, loChannelObj);
		}
		else if (lsJspName.equalsIgnoreCase(HHSConstants.CONTRACTED_MODIFICATION_SERVICES)
				|| lsJspName.equalsIgnoreCase(HHSConstants.CONTRACTED_UPDATE_SERVICES))
		{
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES_MODIFICATION);
			ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObj
					.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
			if (null != loCBContractedServicesBean)
			{
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
			}
			else
			{
				loCBContractedServicesBean = new ContractedServicesBean();
				loCBContractedServicesBean.setTotalContractedServices(HHSConstants.ZERO);
				loCBContractedServicesBean.setYtdTotalInvoiceAmt(HHSConstants.ZERO);
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
			}

		}
		else if (lsJspName.equalsIgnoreCase(HHSConstants.OPERATION_AND_SUPPORT_MODIFICATION)
				|| lsJspName.equalsIgnoreCase(HHSConstants.OPERATION_AND_SUPPORT_UPDATE))
		{
			fetchOpAndSupportModData(aoResourceRequest, loPortletSession, loChannelObj);
		}
		// Starts : Release 6 : Gets Returned Payment Accordian Data
		else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY))
		{
			fetchReturnPaymentList(aoResourceRequest, loChannelObj);
		}
		// Ends : Release 6
		//Added for R7 PI
		fetchProgramIncomeData(loPortletSession, loChannelObj, aoResourceRequest);
		//R7 changes end
		String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method perform actions on click of tab.
	 * <ul>
	 * <li>On click of tab this method will redirect to the same name of jsp
	 * file.</li>
	 * <li>like on click of rate in tab, it will redirect to the rate.jsp</li>
	 * <li>if jsp name is modificationBudgetSummary then it will get subBudgetId
	 * and parentSUbBudgetid from request and will set it in CB Grid Bean</li>
	 * <li>then fetchModificationBudgetSummary transaction will be called which
	 * will get the modification summary data</li>
	 * <li>budgetSummary and indirectRate will be set in session so that data
	 * can be displayed on jsp.</li>
	 * <li>Updated in Release 6</li>
	 * <li>Updated in Release 6 - Returned Payment for returned payment
	 * Accordion on contract budget landing screen.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @return loModelAndView
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("showCBAmendmentGridTabs")
	public ModelAndView showCBAmendmentGridTabs(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		MasterBean loMasterBean = null;
		HashMap<String, Object> loHashMap = null;

		String lsJspName = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loPortletSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		String lsBudgetStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
				HHSConstants.STATUS_BUDGET_APPROVED);

		if (loCBGridBean.getBudgetTypeId().equals(HHSConstants.ONE)
				&& loCBGridBean.getBudgetStatusId().equals(lsBudgetStatus))
		{
			loHashMap = (HashMap<String, Object>) loPortletSession.getAttribute(HHSConstants.MASTERBEAN_HASHMAP,
					PortletSession.APPLICATION_SCOPE);

			if ((loHashMap == null) || (!loHashMap.containsKey(loCBGridBean.getContractBudgetID())))
			{
				loHashMap = (loHashMap == null) ? new HashMap<String, Object>() : loHashMap;
				loMasterBean = BaseControllerUtil.generateMasterBean(loCBGridBean.getContractBudgetID(), loUserSession);
				loHashMap.put(loCBGridBean.getContractBudgetID(), loMasterBean);
				loPortletSession.setAttribute(HHSConstants.MASTERBEAN_HASHMAP, loHashMap,
						PortletSession.APPLICATION_SCOPE);
			}
			loMasterBean = (MasterBean) loHashMap.get(loCBGridBean.getContractBudgetID());
		}
		try
		{
			Channel loChannelObj = showGridVariablePara(aoResourceRequest, loCBGridBean, HHSConstants.MODIFICATION);
			loChannelObj.setData(HHSConstants.MASTERBEAN_OBJ, loMasterBean);

			if (lsJspName.equalsIgnoreCase(HHSConstants.MODIFICATION_BUDGET_SUMMARY))
			{
				return setAttributesForBudgetSummary(aoResourceRequest, loCBGridBean, loPortletSession, loChannelObj,
						lsJspName);

			}
			// Start : Updated in R6
			// Start: Update in Defect-8470
			else if (lsJspName.equalsIgnoreCase(HHSConstants.PERSONNEL_SERVICES_AMENDMENT_JSP_NAME)
					|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_AMENDMENT_SUMMARY_JSP_NAME)
					|| lsJspName.equalsIgnoreCase(HHSR5Constants.PERSONNEL_SERVICES_AMEND_TAB))
			// End: Update in Defect-8470
			// End : Updated in R6
			{
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PERSONNEL_SERVICES_DATA);
				PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObj
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				String lsPersonnelServiceMasterData = (String) loChannelObj
						.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA,
						lsPersonnelServiceMasterData, PortletSession.APPLICATION_SCOPE);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.AMENDMENT_INDIRECT_RATE_JSP_NAME))
			{
				fetchIndirectPercentage(loPortletSession, loCBGridBean, loChannelObj);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.OPERATION_AND_SUPPORT_AMENDMENT))
			{
				fetchOpAndSupportModData(aoResourceRequest, loPortletSession, loChannelObj);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.CONTRACTED_AMENDMENT_SERVICES))
			{
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES_AMENDMENT);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObj
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				if (null != loCBContractedServicesBean)
				{
					loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
							PortletSession.APPLICATION_SCOPE);
				}
				else
				{
					loCBContractedServicesBean = new ContractedServicesBean();
					loCBContractedServicesBean.setTotalContractedServices(HHSConstants.ZERO);
					loCBContractedServicesBean.setYtdTotalInvoiceAmt(HHSConstants.ZERO);
					loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
							PortletSession.APPLICATION_SCOPE);
				}
			}
			// Starts : Release 6 : Gets Returned Payment Accordian Data
			else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY))
			{
				fetchReturnPaymentList(aoResourceRequest, loChannelObj);
			}
			// Ends : Release 6
			aoResourceRequest.setAttribute(HHSConstants.AMENDMENT_TYPE, loCBGridBean.getAmendmentType());
			//Added for R7 Program Income 
			fetchProgramIncomeData(loPortletSession, loChannelObj, aoResourceRequest);
		}
		// ApplicationException is thrown from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT
					.Error("Error occured in showCBAmendmentGridTabs method while performing actions on click of tab."
							+ aoAppExp);
		}
		String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method sets the request attributes for budget modification summary
	 * page <li>Release 3.6.0 Enhancement id 6484</li>
	 * @param aoResourceRequest to get screen parameters.
	 * @param aoCBGridBean Grid bean as input.
	 * @param aoPortletSession Portlet session as input.
	 * @param aoChannelObj Channel object as input.
	 * @throws ApplicationException If an Application Exception occurs.
	 */
	private ModelAndView setAttributesForBudgetSummary(ResourceRequest aoResourceRequest, CBGridBean aoCBGridBean,
			PortletSession aoPortletSession, Channel aoChannelObj, String asJspName) throws ApplicationException
	{
		String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
		String lsTabId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_ID);
		String lsIsPrinterFriendly = aoResourceRequest.getParameter(HHSConstants.BASE_PRINTER_FRIENDLY);
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_MODIFICATION_BUDGET_SUMMARY);
		ContractBudgetSummary loBudgetSummary = (ContractBudgetSummary) aoChannelObj
				.getData(HHSConstants.LO_BUDGET_SUMMARY);
		List<Integer> loTabToHighlightList = (List<Integer>) aoChannelObj
				.getData(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST);
		aoPortletSession.setAttribute(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST + lsSubBudgetId,
				loTabToHighlightList, PortletSession.APPLICATION_SCOPE);
		String lsIndirectRate = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
		// Release 3.6.0 Enhancement id 6484
		List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) aoChannelObj
				.getData(HHSConstants.SITE_DETAIL_LIST);
		Boolean loRecordBeforeRelease = (Boolean) aoChannelObj.getData(HHSConstants.RECORD_BEFORE_RELEASE);
		String lsSubBudgetStatusId = (String) aoChannelObj.getData(HHSConstants.LS_SUB_BUDGET_STATUS_ID);
		aoPortletSession.setAttribute(HHSConstants.BUDGET_SUMMARY, loBudgetSummary, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute(HHSConstants.BUDGET_TYPE, aoCBGridBean.getBudgetTypeId(),
				PortletSession.APPLICATION_SCOPE);
		aoResourceRequest.setAttribute(HHSConstants.SUB_BUDGET_ID, aoCBGridBean.getSubBudgetID());
		aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, aoCBGridBean.getContractID());
		aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
		aoResourceRequest.setAttribute(HHSConstants.FISCAL_YEAR_ID, aoCBGridBean.getFiscalYearID());
		aoResourceRequest.setAttribute(HHSConstants.BASE_HDN_TAB_ID, lsTabId);
		aoResourceRequest.setAttribute(HHSConstants.RECORD_BEFORE_RELEASE, loRecordBeforeRelease);
		aoResourceRequest.setAttribute(HHSConstants.LS_SUB_BUDGET_STATUS_ID, lsSubBudgetStatusId);
		aoResourceRequest.setAttribute(HHSConstants.BASE_PRINTER_FRIENDLY, lsIsPrinterFriendly);
		return new ModelAndView(HHSConstants.JSP_CONTRACTBUDGET + asJspName, HHSConstants.SITE_DETAILS_BEAN_UPPERCASE,
				loSiteDetails);
	}

	/**
	 * This method has been written to refresh the non grid data using ajax. <li>
	 * 1. Take the nextAction parameter from request from which we basically
	 * identify the tab on which changes have been made in grid.</li> <li>2.
	 * Then on the basis of tab, we hit the transactions to get the data and put
	 * it into a json object.</li> <li>3. Which is then passed to javascript to
	 * refresh the data.
	 * <ul>
	 * <li>Execute Transaction <b>fetchPersonnelServiceData</b>and <b>
	 * getOpAndSupportModPageData</b></li>
	 * </ul>
	 * 
	 * @param aoResourceRequest a request object
	 * @param aoResourceResponse a response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an IO Exception occurs
	 */
	@ResourceMapping("getCallBackData")
	protected void getCallBackData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException, IOException
	{
		PrintWriter loOutForCallBack = null;
		try
		{
			loOutForCallBack = aoResourceResponse.getWriter();
			String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			Channel loChannelObjForCallBack = new Channel();
			if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_PERSONNEL_SERVICES_DATA))
			{
				loChannelObjForCallBack.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				HHSTransactionManager.executeTransaction(loChannelObjForCallBack,
						HHSConstants.FETCH_PERSONNEL_SERVICES_DATA);
				PersonnelServicesData loPersonnelServiceDataForCallBack = (PersonnelServicesData) loChannelObjForCallBack
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceDataForCallBack,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"TotalSalaryAndFringeAmount\":\""
						+ loPersonnelServiceDataForCallBack.getTotalSalaryAndFringeAmount()
						+ "\", \"TotalSalaryAmount\": \"" + loPersonnelServiceDataForCallBack.getTotalSalaryAmount()
						+ "\", \"TotalFringeAmount\": \"" + loPersonnelServiceDataForCallBack.getTotalFringeAmount()
						+ "\", \"FringePercentage\": \"" + loPersonnelServiceDataForCallBack.getFringePercentage()
						+ "\", \"TotalYtdInvoicedAmount\": \""
						+ loPersonnelServiceDataForCallBack.getTotalYtdInvoicedAmount() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForCallBack.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_OPERATION_SUPPORT_MOD_DATA))
			{
				loChannelObjForCallBack.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				HHSTransactionManager.executeTransaction(loChannelObjForCallBack,
						HHSConstants.GET_OPERATION_SUPP_MOD_PAGE_DATA);
				CBOperationSupportBean loCBOperationSupportBean = (CBOperationSupportBean) loChannelObjForCallBack
						.getData(HHSConstants.CB_OPERATION_SUPPORT_BEAN);
				loPortletSession.setAttribute(HHSConstants.OPERATION_AND_SUPPORT_DATA, loCBOperationSupportBean,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"keyFYBudgetModOTPS\":\"" + loCBOperationSupportBean.getFyBudget()
						+ "\", \"keyYTDInvAmtModOTPS\": \"" + loCBOperationSupportBean.getYtdInvoicedAmt()
						+ "\", \"SubBudgetId\": \"" + loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForCallBack.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_CONTRACTED_SERVICES_DATA))
			{
				loChannelObjForCallBack.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				HHSTransactionManager.executeTransaction(loChannelObjForCallBack,
						HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES_MODIFICATION);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObjForCallBack
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"ProposedTotalContractedServicesAmount\":\""
						+ loCBContractedServicesBean.getTotalContractedServices() + "\", \"TotalYtdInvoiceAmount\": \""
						+ loCBContractedServicesBean.getYtdTotalInvoiceAmt() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForCallBack = aoResourceResponse.getWriter();
				loOutForCallBack.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_INDIRECT_RATE_NON_GRID_DATA))
			{
				fetchIndirectPercentageEditNonGridData(aoResourceResponse, loOutForCallBack, loChannelObjForCallBack,
						loCBGridBean);
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in getCallBackData", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getCallBackData", aoExe);
		}
		finally
		{
			if (null != loOutForCallBack)
			{
				loOutForCallBack.flush();
				loOutForCallBack.close();
			}
		}
	}

	/**
	 * This method has been written to refresh the non grid data using ajax. <li>
	 * 1. Take the nextAction parameter from request from which we basically
	 * identify the tab on which changes have been made in grid.</li> <li>2.
	 * Then on the basis of tab, we hit the transactions to get the data and put
	 * it into a json object.</li> <li>3. Which is then passed to javascript to
	 * refresh the data. This is used for refreshing Amendment data.
	 * <ul>
	 * <li>Execute transaction <b> getNonGridContractedServicesAmendment</b></li>
	 * </ul>
	 * 
	 * @param aoResourceRequest a request object
	 * @param aoResourceResponse a response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an IO Exception occurs
	 */
	@ResourceMapping("getCallBackDataAmendment")
	protected void getCallBackDataAmendment(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException, IOException
	{
		PrintWriter loOut = null;
		try
		{
			loOut = aoResourceResponse.getWriter();
			String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			Channel loChannelObj = new Channel();
			if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_CONTRACTED_SERVICES_DATA))
			{
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				PortletSession loPortletSession = aoResourceRequest.getPortletSession();
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES_AMENDMENT);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObj
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"ProposedTotalContractedServicesAmount\":\""
						+ loCBContractedServicesBean.getTotalContractedServices() + "\", \"TotalYtdInvoiceAmount\": \""
						+ loCBContractedServicesBean.getYtdTotalInvoiceAmt() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut = aoResourceResponse.getWriter();
				loOut.print(lsJsonObj);
			}
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in getCallBackDataAmendment", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getCallBackDataAmendment", aoExe);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method has been written to refresh the non grid data using ajax. <li>
	 * 1. Take the nextAction parameter from request from which we basically
	 * identify the tab on which changes have been made in grid.</li> <li>2.
	 * Then on the basis of tab, we hit the transactions to get the data and put
	 * it into a json object.</li> <li>3. Which is then passed to javascript to
	 * refresh the data.
	 * <ul>
	 * <li>Execute transaction <b> getOpAndSupportPageData </b></li>
	 * <li>Updated in Release 6 </b></li>
	 * </ul>
	 * 
	 * @param aoResourceRequest a request object
	 * @param aoResourceResponse a response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an IO Exception occurs
	 */
	@ResourceMapping("getCallBackContractBudgetData")
	protected void getCallBackContractBudgetData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException, IOException
	{
		PrintWriter loOutForContractBudget = null;
		Channel loChannelObjForContractBudget = new Channel();
		try
		{
			loOutForContractBudget = aoResourceResponse.getWriter();
			PortletSession loPortletSession = aoResourceRequest.getPortletSession();
			String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_OPERATION_SUPPORT_DATA))
			{
				loChannelObjForContractBudget.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(loChannelObjForContractBudget,
						HHSConstants.GET_OPERATION_SUPP_PAGE_DATA);
				CBOperationSupportBean loCBOperationSupportBean = (CBOperationSupportBean) loChannelObjForContractBudget
						.getData(HHSConstants.CB_OPERATION_SUPPORT_BEAN);
				loPortletSession.setAttribute(HHSConstants.OPERATION_AND_SUPPORT_DATA, loCBOperationSupportBean,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"keyFYBudgetOTPS\":\"" + loCBOperationSupportBean.getFyBudget()
						+ "\", \"keyYTDInvAmtOTPS\": \"" + loCBOperationSupportBean.getYtdInvoicedAmt()
						+ "\", \"SubBudgetId\": \"" + loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForContractBudget.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_INDIRECT_RATE_NON_GRID_DATA))
			{
				fetchIndirectPercentageEditNonGridData(aoResourceResponse, loOutForContractBudget,
						loChannelObjForContractBudget, loCBGridBean);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_PERSONNEL_SERVICES_DATA))
			{
				loChannelObjForContractBudget.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(loChannelObjForContractBudget,
						HHSConstants.FETCH_PERSONNEL_SERVICES_DATA);
				PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObjForContractBudget
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"TotalSalaryAndFringeAmount\":\""
						+ loPersonnelServiceData.getTotalSalaryAndFringeAmount() + "\", \"TotalSalaryAmount\": \""
						+ loPersonnelServiceData.getTotalSalaryAmount() + "\", \"TotalFringeAmount\": \""
						+ loPersonnelServiceData.getTotalFringeAmount() + "\", \"FringePercentage\": \""
						+ loPersonnelServiceData.getFringePercentage() + "\", \"TotalYtdInvoicedAmount\": \""
						+ loPersonnelServiceData.getTotalYtdInvoicedAmount() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForContractBudget.print(lsJsonObj);
			}

			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_CONTRACTED_SERVICES_DATA))
			{
				loChannelObjForContractBudget.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(loChannelObjForContractBudget,
						HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObjForContractBudget
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"ProposedTotalContractedServicesAmount\":\""
						+ loCBContractedServicesBean.getTotalContractedServices() + "\", \"TotalYtdInvoiceAmount\": \""
						+ loCBContractedServicesBean.getYtdTotalInvoiceAmt() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForContractBudget = aoResourceResponse.getWriter();
				loOutForContractBudget.print(lsJsonObj);
			}
			// Start : Added in R6
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSR5Constants.GET_PS_SUMMARY_DATA)
					|| aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
							HHSR5Constants.GET_PS_DETAIL_DATA))
			{
				loChannelObjForContractBudget.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(
						loChannelObjForContractBudget,
						(aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
								HHSR5Constants.GET_PS_SUMMARY_DATA) ? HHSR5Constants.FETCH_NON_GRID_DATA_FOR_SUMMARY
								: HHSR5Constants.FETCH_NON_GRID_DATA_FOR_DETAIL));
				PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObjForContractBudget
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				String lsMessage = (String) loChannelObjForContractBudget.getData(HHSR5Constants.LS_MESSAGE);
				String lsJsonObj = "{\"CitySalaryAndFringeAmount\":\""
						+ loPersonnelServiceData.getTotalSalaryAndFringeAmount() + "\", \"CitySalaryAmount\": \""
						+ loPersonnelServiceData.getTotalSalaryAmount() + "\", \"CityFringeAmount\": \""
						+ loPersonnelServiceData.getTotalFringeAmount() + "\", \"FringePercentage\": \""
						+ loPersonnelServiceData.getFringePercentage() + "\", \"TotalYtdInvoicedAmount\": \""
						+ loPersonnelServiceData.getTotalYtdInvoicedAmount() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\",\"Position\":\""
						+ loPersonnelServiceData.getTotalPositions() + "\", \"totalCityFte\": \""
						+ loPersonnelServiceData.getTotalCityFte() + "\", \"DetailedScreenMessage\": \""
						+ StringEscapeUtils.escapeXml(lsMessage) + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOutForContractBudget = aoResourceResponse.getWriter();
				loOutForContractBudget.print(lsJsonObj);
			}
			// End : Added in R6
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in getCallBackContractBudgetData", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getCallBackContractBudgetData", aoExe);
		}
		finally
		{
			if (null != loOutForContractBudget)
			{
				loOutForContractBudget.flush();
				loOutForContractBudget.close();
			}
		}

	}

	/**
	 * This method has been written to refresh the non grid data using ajax. <li>
	 * 1. Take the nextAction parameter from request from which we basically
	 * identify the tab on which changes have been made in grid.</li> <li>2.
	 * Then on the basis of tab, we hit the transactions to get the data and put
	 * it into a json object.</li> <li>3. Which is then passed to javascript to
	 * refresh the data.
	 * <ul>
	 * <li>Execute transaction <b> fetchInvoiceOpSupportAmount </b></li>
	 * <li>Execute transaction <b>fetchNonGridDataForPersonnelService </b></li>
	 * </ul>
	 * 
	 * @param aoResourceRequest a request object
	 * @param aoResourceResponse a response object
	 * @throws ApplicationException If an Application Exception occurs
	 * @throws IOException If an IO Exception occurs
	 */
	@ResourceMapping("getCallBackContractInvoiceData")
	protected void getCallBackContractInvoiceData(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException, IOException
	{
		PrintWriter loOut = null;
		Channel loChannelObj = new Channel();
		try
		{
			loOut = aoResourceResponse.getWriter();
			PortletSession loPortletSession = aoResourceRequest.getPortletSession();
			String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.SUBBUDGET_ID);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_OPERATION_SUPPORT_DATA))
			{
				loChannelObj.setData(HHSConstants.INVOICE_ID_KEY, loCBGridBean.getInvoiceId());
				loChannelObj.setData(HHSConstants.SUB_BUDGET_ID_KEY, loCBGridBean.getSubBudgetID());

				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BASE_FETCH_INVOICE_OP_SUPPORT_AMOUNTS);
				String lsInvoiceTotalAmounts = (String) loChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS);
				String lsYtdInvoicedAmount = (String) loChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT);

				loPortletSession.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS, lsInvoiceTotalAmounts,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT, lsYtdInvoicedAmount,
						PortletSession.APPLICATION_SCOPE);

				String lsJsonObj = "{\"keyFYBudgetOTPS\":\"" + lsInvoiceTotalAmounts + "\", \"keyYTDInvAmtOTPS\": \""
						+ lsYtdInvoicedAmount + "\", \"SubBudgetId\": \"" + loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_CONTRACTED_SERVICES_DATA))
			{
				loChannelObj.setData(HHSConstants.INVOICE_ID_KEY, loCBGridBean.getInvoiceId());
				loChannelObj.setData(HHSConstants.SUB_BUDGET_ID_KEY, loCBGridBean.getSubBudgetID());

				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BASE_FETCH_INVOICE_CONTRACTED_SERVICES_AMOUNTS);

				String lsInvoiceTotalAmounts = (String) loChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS);
				String lsYtdInvoicedAmount = (String) loChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT);

				loPortletSession.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS, lsInvoiceTotalAmounts,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT, lsYtdInvoicedAmount,
						PortletSession.APPLICATION_SCOPE);

				String lsJsonObj = "{\"ProposedTotalContractedServicesAmount\":\"" + lsInvoiceTotalAmounts
						+ "\", \"TotalYtdInvoiceAmount\": \"" + lsYtdInvoicedAmount + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut = aoResourceResponse.getWriter();
				loOut.print(lsJsonObj);
			}
			else if (aoResourceRequest.getParameter(HHSConstants.NEXT_ACTION).equalsIgnoreCase(
					HHSConstants.GET_PERSONNEL_SERVICES_DATA))
			{
				loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.FETCH_NONGRID_DATA_PERSONNELSERVICES);
				PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObj
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
						PortletSession.APPLICATION_SCOPE);
				String lsJsonObj = "{\"TotalSalaryAndFringeAmount\":\""
						+ loPersonnelServiceData.getTotalSalaryAndFringeAmount() + "\", \"TotalSalaryAmount\": \""
						+ loPersonnelServiceData.getTotalSalaryAmount() + "\", \"TotalFringeAmount\": \""
						+ loPersonnelServiceData.getTotalFringeAmount() + "\", \"FringePercentage\": \""
						+ loPersonnelServiceData.getFringePercentage() + "\", \"TotalYtdInvoicedAmount\": \""
						+ loPersonnelServiceData.getTotalYtdInvoicedAmount() + "\", \"SubBudgetId\": \""
						+ loCBGridBean.getSubBudgetID() + "\",\"Position\":\"" // Added
																				// No.
																				// of
																				// total
																				// positions
																				// as
																				// part
																				// of
																				// R6
						+ loPersonnelServiceData.getTotalPositions() + "\"}";
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut.print(lsJsonObj);
			}
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in getCallBackContractInvoiceData", aoExe);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}

	}

	/**
	 * This channel get the non grid data while we are editing any grid.
	 * <ul>
	 * <li>Execute transaction <b>fetchIndirectRatePercentage</b></li>
	 * </ul>
	 * 
	 * @param aoResourceResponse ResourceResponse as input.
	 * @param aoOut Print writer object as input
	 * @param aoChannelObj channel object as input
	 * @param aoCBGridBean grid bean as input
	 * @throws ApplicationException ApplicationException object
	 */
	private void fetchIndirectPercentageEditNonGridData(ResourceResponse aoResourceResponse, PrintWriter aoOut,
			Channel aoChannelObj, CBGridBean aoCBGridBean) throws ApplicationException
	{
		aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoCBGridBean);
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_INDIRECT_RATE_PERCENTAGE);
		//Start : R7 Program Income changes to fetch program income indirect rate percentage
		String lsJsonObj = "{\"keyIndirectRatePercent\":\"" + (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY)
				+ "\", \"keyPIIndirectRatePercent\": \"" + (String) aoChannelObj.getData(HHSR5Constants.PI_INDIRECT_RATE_KEY)
				+ "\"}";
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		aoOut.print(lsJsonObj);
		//End : R7 Program Income changes
	}

	/**
	 * <p>
	 * This is a private method called to fetch data other than grid on s365
	 * screen for OTPS modification
	 * </p>
	 * <ul>
	 * <li>Execute transaction <b>getOpAndSupportModPageData </b></li>
	 * </ul>
	 * @param aoResourceRequest ResourceRequest object
	 * @param aoPortletSession PortletSession object
	 * @param aoChannelObj Channel object for to/fro of data via transaction
	 *            layer
	 * @throws ApplicationException ApplicationException object
	 */
	private void fetchOpAndSupportModData(ResourceRequest aoResourceRequest, PortletSession aoPortletSession,
			Channel aoChannelObj) throws ApplicationException
	{
		HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.GET_OPERATION_SUPP_MOD_PAGE_DATA);
		CBOperationSupportBean loCBOperationSupportBean = (CBOperationSupportBean) aoChannelObj
				.getData(HHSConstants.CB_OPERATION_SUPPORT_BEAN);
		aoPortletSession.setAttribute(HHSConstants.OPERATION_AND_SUPPORT_DATA, loCBOperationSupportBean,
				PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method perform actions on click of tabs in Invoice. Modified for
	 * Release 3.4.0, #5681
	 * <ul>
	 * <li>On click of tab this method will redirect to the same name of jsp
	 * file.</li>
	 * <li>like on click of rate in tab, it will redirect to the rate.jsp</li>
	 * <li>Execute transaction <b> fetchContractedServicesAmounts</b></li>
	 * <li>Execute transaction <b> fetchNonGridDataForPersonnelServices </b></li>
	 * 
	 * </ul>
	 * * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * <li>Updated in Release 6 - Returned Payment for returned payment
	 * Accordion on contract budget landing screen.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @return loModelAndView
	 */
	@ResourceMapping("showInvoiceGridTabs")
	public ModelAndView showInvoiceTabJsp(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		ModelAndView loModelAndView = null;
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();
		// Read hidden parameters from request
		Channel loChannelObj = showGridVariablePara(aoResourceRequest, loCBGridBean, HHSConstants.INVOICE_NAME);
		String lsPageLevelInvoiceId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_INVOICE_ID);
		try
		{
			// fix done as a part of release 3.1.2 defect 6420 - start
			if (lsPageLevelInvoiceId != null && loCBGridBean.getInvoiceId() != null && !lsPageLevelInvoiceId.isEmpty()
					&& !lsPageLevelInvoiceId.equalsIgnoreCase(loCBGridBean.getInvoiceId()))
			{
				return null;
			}
			// fix done as a part of release 3.1.2 defect 6420 - end
			if (lsJspName.equals(HHSConstants.BASE_INVOICE_OPERATION_SUPPORT))
			{
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BASE_FETCH_INVOICE_OP_SUPPORT_AMOUNTS);

				String lsInvoiceTotalAmounts = (String) loChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS);
				String lsYtdInvoicedAmount = (String) loChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT);

				loPortletSession.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS, lsInvoiceTotalAmounts,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT, lsYtdInvoicedAmount,
						PortletSession.APPLICATION_SCOPE);
			}
			else if (lsJspName.equals(HHSConstants.CONTRACTED_INVOICING_SERVICES))
			{
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.BASE_FETCH_INVOICE_CONTRACTED_SERVICES_AMOUNTS);

				String lsInvoiceTotalAmounts = (String) loChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS);
				String lsYtdInvoicedAmount = (String) loChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT);

				loPortletSession.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS, lsInvoiceTotalAmounts,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT, lsYtdInvoicedAmount,
						PortletSession.APPLICATION_SCOPE);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.INVOICE_SUMMARY))
			{
				String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.BASE_GET_INVOICE_SUMMARY);
				List<Integer> loTabToHighlightList = (List<Integer>) loChannelObj
						.getData(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST);
				ContractBudgetSummary loInvoiceSummary = (ContractBudgetSummary) loChannelObj
						.getData(HHSConstants.BASE_LO_CB_INVOICE_SUMMARY);
				String lsIndirectRate = (String) loChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
				// Release 3.6.0 Enhancement id 6484
				List<SiteDetailsBean> loSiteDetails = (List<SiteDetailsBean>) loChannelObj
						.getData(HHSConstants.SITE_DETAIL_LIST);
				String lsSubBudgetStatusId = (String) loChannelObj.getData(HHSConstants.LS_SUB_BUDGET_STATUS_ID);
				Boolean loRecordBeforeRelease = (Boolean) loChannelObj.getData(HHSConstants.RECORD_BEFORE_RELEASE);
				loPortletSession.setAttribute(HHSConstants.CHANNEL_PARAM_TAB_HIGHLOGHT_LIST + lsSubBudgetId,
						loTabToHighlightList, PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.BASE_CB_INVOICE_SUMMARY, loInvoiceSummary,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate,
						PortletSession.APPLICATION_SCOPE);
				aoResourceRequest.setAttribute(HHSConstants.RECORD_BEFORE_RELEASE, loRecordBeforeRelease);
				aoResourceRequest.setAttribute(HHSConstants.LS_SUB_BUDGET_STATUS_ID, lsSubBudgetStatusId);
				return new ModelAndView(HHSConstants.BASE_JSP_INVOICE + lsJspName,
						HHSConstants.SITE_DETAILS_BEAN_UPPERCASE, loSiteDetails);
			}
			// Added check for Release 3.4.0, #5681 - Starts
			else if (lsJspName.equalsIgnoreCase(HHSConstants.CONTRACT_INVOICE_PRINT_SUMMARY))
			{
				fetchInvoicePrintSummary(loCBGridBean, loChannelObj, aoResourceRequest);
			}
			// Added check for Release 3.4.0, #5681 - Ends
			else if (lsJspName.equalsIgnoreCase(HHSConstants.CONTRACTED_INVOICING_SERVICES))
			{
				HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_NONGRID_CONTRACTED_SERVICES);
				ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) loChannelObj
						.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
				loPortletSession.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean,
						PortletSession.APPLICATION_SCOPE);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.INVOICE_PERSONNEL_SUMMARY))
			{
				HHSTransactionManager.executeTransaction(loChannelObj,
						HHSConstants.FETCH_NONGRID_DATA_PERSONNELSERVICES);
				PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) loChannelObj
						.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
				String lsPersonnelServiceMasterData = (String) loChannelObj
						.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData,
						PortletSession.APPLICATION_SCOPE);
				loPortletSession.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA,
						lsPersonnelServiceMasterData, PortletSession.APPLICATION_SCOPE);
			}
			else if (lsJspName.equalsIgnoreCase(HHSConstants.INVOICE_INDIRECT_RATE_JSP_NAME))
			{
				fetchIndirectPercentage(loPortletSession, loCBGridBean, loChannelObj);

			}
			else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY))
			{
				fetchReturnPaymentList(aoResourceRequest, loChannelObj);
				String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
				return new ModelAndView(lsJspPath);
			}
			// Fix for defect : 8624 to display returned payment in print
			// invoice
			else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_PRINT_VIEW))
			{
				fetchReturnPaymentList(aoResourceRequest, loChannelObj);
				String lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
				return new ModelAndView(lsJspPath);
			}
			String lsJspPath = HHSConstants.BASE_JSP_INVOICE + lsJspName;
			// Ends : Release 6
			loModelAndView = new ModelAndView(lsJspPath);
		}
		// ApplicationException is thrown from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in showInvoiceTabJsp method"
					+ " while performing actions on click of tabs in Invoice.", aoAppExp);
		}
		return loModelAndView;
	}

	/**
	 * This method perform actions on click of tabs in Invoice.
	 * <ul>
	 * <li>On click of tab this method will redirect to the same name of jsp
	 * file.</li>
	 * <li>like on click of rate in tab, it will redirect to the rate.jsp</li>
	 * <li>Updated in Release 6 - Returned Payment for returned payment
	 * Accordion on contract budget landing screen.</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest to get screen parameters
	 * @return loModelAndView
	 */
	@ResourceMapping("showPaymentGridTabs")
	public ModelAndView showPaymenTabJsp(ResourceRequest aoResourceRequest)
	{
		ModelAndView loModelAndView = null;

		String lsJspName = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_TAB_NAME);
		String lsJspPath = null;
		try
		{
			if (lsJspName.equalsIgnoreCase(HHSConstants.PAYMENT_ASSIGNMENTS))
			{
				lsJspPath = HHSConstants.JSP_PAYMENT + lsJspName;

			}
			else if (lsJspName.equalsIgnoreCase(HHSR5Constants.RETURNED_PAYMENT_READ_ONLY))
			{
				Channel loChannelObj = new Channel();
				fetchReturnPaymentList(aoResourceRequest, loChannelObj);
				lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
			}
			else
			{
				lsJspPath = HHSConstants.JSP_CONTRACTBUDGET + lsJspName;
			}
			loModelAndView = new ModelAndView(lsJspPath);
		}
		// ApplicationException is thrown from
		// executeTransaction (while executing transaction)
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("Error occured in showPaymenTabJsp method"
					+ " while performing actions on click of tabs in Advance Payment Request.", aoAppExp);
		}
		return loModelAndView;
	}

	/**
	 * <ul>
	 * <li>Declare the generic variable for Show Grid method file.</li>
	 * </ul>
	 * Updated Method in R4
	 * @param aoResourceRequest resource request object
	 * @param aoCBGridBean Grid Bean
	 * @param aoMethodName Method Name
	 * @return Channel
	 */
	public Channel showGridVariablePara(ResourceRequest aoResourceRequest, CBGridBean aoCBGridBean, String aoMethodName)
	{
		String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
		String lsAmendedContractSubBudgetID = aoResourceRequest
				.getParameter(HHSConstants.BASE_HDN_AMENDED_CONTRACT_SUB_BUDGET_ID);
		String lsReadOnlyPage = aoResourceRequest.getParameter(HHSConstants.BASE_READ_ONLY_PAGE);
		aoResourceRequest.setAttribute(HHSConstants.BASE_SUB_GRID_READONLY, lsReadOnlyPage);
		aoResourceRequest.setAttribute(HHSConstants.SUBBUDGET_ID, lsSubBudgetId);
		aoResourceRequest.setAttribute(HHSConstants.AMENDED_CONTRACT_SUB_BUDGET_ID, lsAmendedContractSubBudgetID);
		aoResourceRequest.setAttribute(HHSConstants.ADVANCE_READ_ONLY,
				aoResourceRequest.getParameter(HHSConstants.ADVANCE_READ_ONLY));
		//Added in R7
		aoResourceRequest.setAttribute(HHSR5Constants.OLD_PI_FLAG,aoResourceRequest.getParameter(HHSR5Constants.HIDDEN_IS_OLD_PI));
		aoResourceRequest.setAttribute(HHSR5Constants.IS_PI_SELECTED,aoResourceRequest.getParameter(HHSR5Constants.HIDDEN_IS_PI_SELECTED));
		//R7 changes end
		Channel loChannelObj = new Channel();
		aoCBGridBean.setSubBudgetID(lsSubBudgetId);
		if (HHSConstants.INVOICE_NAME.equals(aoMethodName))
		{
			aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID, aoCBGridBean.getContractBudgetID());
			aoResourceRequest.setAttribute(HHSConstants.INVOICE_ID, aoCBGridBean.getInvoiceId());
			loChannelObj.setData(HHSConstants.INVOICE_ID_KEY, aoCBGridBean.getInvoiceId());
			loChannelObj.setData(HHSConstants.SUB_BUDGET_ID_KEY, lsSubBudgetId);
		}
		else if (HHSConstants.MODIFICATION.equals(aoMethodName))
		{
			String lsParentSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_PARENT_SUB_BUDGET_ID);
			aoResourceRequest.setAttribute(HHSConstants.PARENT_SUBBUDGET_ID, lsParentSubBudgetId);
			aoCBGridBean.setParentSubBudgetId(lsParentSubBudgetId);
		}

		loChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, aoCBGridBean);
		return loChannelObj;
	}

	/**
	 * This method performs the userName and password validation
	 * <ul>
	 * <li>Validate entered user is logged in user</li>
	 * <li>Validate username and password is correct</li>
	 * </ul>
	 * 
	 * @param asUserId entered userName
	 * @param asPassword entered password
	 * @param aoRequest Portal Request Object
	 * @return Map
	 * @throws ApplicationException an Application Exception Object
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Map validateUser(String asUserId, String asPassword, PortletRequest aoRequest) throws ApplicationException
	{
		String lsLoginUserEmail = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
		//***Start SAML R 7.9.0 QC 9165: invoke Web Service to Authenticate User for Provider only
		String lsUserOrgType = (String)aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Debug("Current User Type from session: "+lsUserOrgType);
		
		return BaseControllerUtil.validateUserUtil(asUserId, asPassword, lsLoginUserEmail, lsUserOrgType);
		//***End SAML R 7.9.0 QC 9165: invoke Web Service to Authenticate User for Provider only
	}

	/**
	 * This method is used to read the data from file net when terms and
	 * condition comes
	 * 
	 * @param aoChannel channel Object
	 * @return lsSystemTermsAndCond
	 * @throws ApplicationException
	 */
	public String getTermsAndCondition(Channel aoChannel) throws ApplicationException
	{
		String lsSystemTermsAndCond = BaseControllerUtil.getTermsAndCondition(aoChannel);
		return lsSystemTermsAndCond;
	}

	/**
	 * This method keep Contract Start and End Date in Session
	 * 
	 * @param aoPortletSession Portal Session Object
	 * @param asContStartDate Contract Start Date
	 * @param asContEndDate Contract End Date
	 */
	public void setContractDatesInSession(PortletSession aoPortletSession, String asContStartDate, String asContEndDate)
	{
		aoPortletSession.setAttribute(HHSConstants.CONTRACT_START_DATE_UPPERCASE, asContStartDate,
				PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute(HHSConstants.CONTRACT_END_DATE_UPPERCASE, asContEndDate,
				PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method keep Contract Start and End Date in Session
	 * 
	 * @param aoPortletSession Portal Session Object
	 * @param asContStartDate Contract Start Date
	 * @param asContEndDate Contract End Date
	 */
	public void setContractAmendmentDatesInSession(PortletSession aoPortletSession, String asContStartDate,
			String asContEndDate)
	{
		aoPortletSession.setAttribute(HHSConstants.AMENDMENT_START_DATE, asContStartDate,
				PortletSession.APPLICATION_SCOPE);
		aoPortletSession.setAttribute(HHSConstants.AMENDMENT_END_DATE, asContEndDate, PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method is used to set Header Columns of Chart Of Accounts for
	 * Contract Certification of Funds Document
	 * 
	 * @param aoRequest Request
	 * @return List loFiscalYears
	 */
	@SuppressWarnings("rawtypes")
	public List setCOFAccountHeaderDataInSession(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		List loFiscalYears = BaseControllerUtil.setCOFAccountHeaderDataInSessionUtil(loContractMap);
		return loFiscalYears;
	}

	/**
	 * This method is used to fetch data for Funding source grid in Procurement
	 * screen.
	 * 
	 * @param aoResourceRequest resource request Object
	 * @param aoResourceResponse resource response Object
	 */
	@ResourceMapping("procFundingOperationGrid")
	public void procFundingOperationGrid(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		fundingOperationGridUtil(aoResourceRequest, HHSConstants.PROC_FUNDING_OPERATION_GRID);
	}

	/* Document upload code starts */
	/**
	 * This method is used to get the pop up to upload the document document.
	 * <ul>
	 * <li>Execute <b>documentUploadAction</b> Method of
	 * <li>Set the render action parameter</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest Object
	 * @param aoResourceResponse ResourceResponse Object
	 * @return ModelAndView
	 */
	@ResourceMapping("uploadDocument")
	protected ModelAndView documentUploadAction(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		Document loDocument = new Document();
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			PortletSession loSession = aoResourceRequest.getPortletSession();
			String lsDocCategory = aoResourceRequest
					.getParameter(ApplicationConstants.DOCUMENT_VAULT_DOC_CATEGORY_REQ_PARAMETER);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResourceRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocument);
			RFPReleaseDocsUtil.setRFPDocCategorynDocType(loDocument, lsDocCategory, lsUserOrgType,
					HHSConstants.BASE_FINANCIALS);
			PortletSessionHandler
					.setAttribute(loDocument, aoResourceRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);

			aoResourceRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoResourceRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));

			aoResourceRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.UPLOAD_DOC_TYPE));
			if (aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortletSession.APPLICATION_SCOPE) != null)
			{
				aoResourceRequest.setAttribute(
						ApplicationConstants.ERROR_MESSAGE,
						aoResourceRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
								PortletSession.APPLICATION_SCOPE));
				aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
						ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// Added for R5- combo box for DocType - fix for Defect id 7270
			List<String> loDocTypeList = FileNetOperationsUtils.getDocType(lsUserOrgType, HHSR5Constants.FINANACIAL,
					null);
			// Added for R5 - In case of Agency Users- docType listing for
			// Agency Tasks
			if (lsUserOrgType.equalsIgnoreCase(HHSConstants.USER_AGENCY))
			{
				loDocTypeList = FileNetOperationsUtils.getDocType(lsUserOrgType, null, HHSR5Constants.DOC_TYPE_LISTING);
			}
			// R5 end
			aoResourceRequest.setAttribute(HHSR5Constants.DOC_TYPE_DROP_DOWN_COMBO, loDocTypeList);
			// R5 end
		}
		// ApplicationException and Exception are thrown from various points
		// from getRFPDocCategoryList(including list of document categories from
		// XML DOM object) and from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("ApplicationException occured in populateRenewContractPage", aoExe);
		}
		catch (Exception aoExe)
		{
			LOG_OBJECT.Error("Exception occured in populateRenewContractPage", aoExe);
		}
		return new ModelAndView(HHSConstants.BASENEW_JSP_TASKS_UPLOAD_DOCS);
	}

	/**
	 * This method is used to get the details properties of the selected
	 * document.
	 * <ul>
	 * <li>Execute <b>actionFileInformation</b> Method of
	 * <b>FileNetOperationsUtils</b> class</li>
	 * <li>Get the document bean from the ApplicationSession and set it in
	 * Request</li>
	 * <li>Set the render action parameter</li>
	 * </ul>
	 * 
	 * @param aoRequest ActionRequest Object
	 * @param aoResponse ActionResponse Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@ActionMapping(params = "submit_action=uploadingFinancialFileInformation")
	public void displayUploadingFileInformationAction(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		try
		{
			// below if will be executed when a logged in user belongs
			// to city organization
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUploadingDocumentType = getDocType(aoRequest, lsUserOrgType);
			aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocumentType);
			FileNetOperationsUtils.actionFileInformation(aoRequest, aoResponse);
			// extra check is added in the if block to identify that next step
			// to display. This is added as part of Release 2.6.0 defect:5612
			if ((null == loSession.getAttribute(ApplicationConstants.MESSAGE, PortletSession.APPLICATION_SCOPE) || HHSConstants.EMPTY_STRING
					.equalsIgnoreCase(loSession.getAttribute(ApplicationConstants.MESSAGE,
							PortletSession.APPLICATION_SCOPE).toString()))
					|| (null != loSession.getAttribute(ApplicationConstants.MESSAGE, PortletSession.APPLICATION_SCOPE)
							&& !HHSConstants.EMPTY_STRING.equalsIgnoreCase(loSession.getAttribute(
									ApplicationConstants.MESSAGE, PortletSession.APPLICATION_SCOPE).toString())
							&& null != aoRequest.getAttribute(HHSConstants.MOVE_TO_NEXT_PAGE) && ApplicationConstants.FILE_INFORMATION
								.equalsIgnoreCase((String) aoRequest.getAttribute(HHSConstants.MOVE_TO_NEXT_PAGE))))
			{
				Document loUploadingDocObj = (Document) ApplicationSession.getAttribute(aoRequest, true,
						ApplicationConstants.SESSION_DOCUMENT_OBJ);
				List<DocumentPropertiesBean> loInitialDocPropsBean = FileNetOperationsUtils.getDocumentProperties(
						loUploadingDocObj.getDocCategory(), loUploadingDocObj.getDocType(), lsUserOrgType);
				loUploadingDocObj.setDocumentProperties(loInitialDocPropsBean);
				aoRequest.setAttribute(HHSConstants.BASE_RFP_DOCUMENTS, loUploadingDocObj);
				String lsDocRefNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
				String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
				if (lsDocRefNum != null)
				{
					aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefNum);
				}
				if (lsProposalId != null)
				{
					aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
				}
				if (lsUploadingDocumentType != null)
				{
					aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocumentType);
				}
				aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
						HHSConstants.BASE_UPLOADING_FINANCIAL_FILE_INFORMATION);
			}
		}
		// ApplicationException and Exception are thrown from various points
		// from getDocumentProperties and actionFileInformation (which
		// include getting document info)and from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppEx)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoAppEx,
						HHSConstants.BASE_UPLOADING_FINANCIAL_FILE_INFORMATION);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("ApplicationException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
		catch (Exception aoExp)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp,
						HHSConstants.BASE_UPLOADING_FINANCIAL_FILE_INFORMATION);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("ApplicationException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
	}

	/**
	 * This method is used get upload type to set in request.
	 * 
	 * @param aoRequest action request object
	 * @param asUserOrgType OrganizationType
	 * @return lsDocType string
	 */
	public String getDocType(PortletRequest aoRequest, String asUserOrgType)
	{
		String lsDocType = HHSConstants.CONTRACT;
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
		{
			lsDocType = HHSConstants.BUDGET;
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			if (loCBGridBean != null && loCBGridBean.getInvoiceId() != null)
			{
				lsDocType = HHSConstants.INVOICE_NAME;
			}
		}

		return lsDocType;

	}

	/**
	 * This method will redirect user to the next tab of the upload document
	 * screen
	 * <ul>
	 * <li>Get the user organization type from the request</li>
	 * <li>If the user is is of provider organization type then redirect user to
	 * <b>displayUploadingDocumentInfoProvider</b> view</li>
	 * <li>Else redirect user to the <b>displayUploadingDocumentInfoAgency</b>
	 * view</li>
	 * </ul>
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * @return ModelAndView
	 */
	@RenderMapping(params = "render_action=uploadingFinancialFileInformation")
	protected ModelAndView displayUploadingFileInformationRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
		aoRequest.setAttribute(HHSConstants.PROPOSAL_ID,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID));
		aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
				HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
				(Document) ApplicationSession.getAttribute(aoRequest, true, ApplicationConstants.SESSION_DOCUMENT_OBJ));
		return new ModelAndView(HHSConstants.BASE_JSP_TASKS_DISPLAY_UPLOADING_DOCUMENT_INFO_PROVIDER);
	}

	/**
	 * This method is used to get the error message from the exception object if
	 * there is some exceptional case occurred which later displayed to user for
	 * information
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 * @param aoExp exception object
	 * @param asNextRender next action attribute
	 * @throws IOException when any IOException occurred
	 */
	public void setErrorMessageInResponse(ActionRequest aoRequest, ActionResponse aoResponse, Exception aoExp,
			String asNextRender) throws IOException
	{
		String lsErrorMsg = aoExp.toString();
		lsErrorMsg = lsErrorMsg.substring(lsErrorMsg.lastIndexOf(HHSConstants.COLON) + HHSConstants.INT_ONE,
				lsErrorMsg.length()).trim();
		if (lsErrorMsg.equals(HHSConstants.EMPTY_STRING))
		{
			lsErrorMsg = HHSConstants.INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST;
		}
		if (lsErrorMsg.contains("~"))
		{
			lsErrorMsg = lsErrorMsg.replace("~", ":");
		}
		String lsAjaxCall = aoRequest.getParameter(ApplicationConstants.IS_AJAX_CALL);
		PortletSession loSession = aoRequest.getPortletSession();
		LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);

		// This section executes when any exception occurred while executing any
		// operation on a overlay

		if (null != lsAjaxCall && lsAjaxCall.equalsIgnoreCase(HHSConstants.TRUE))
		{
			loSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
		}
		else if (HHSConstants.FALSE.equalsIgnoreCase(lsAjaxCall) || lsAjaxCall == null)
		{
			aoResponse.setRenderParameter(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
			loSession.removeAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					PortletSession.APPLICATION_SCOPE);
			loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE);
			loSession.removeAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, asNextRender);
		}
	}

	/**
	 * Modified as part of release 3.12.0 Enhancement 6602 This method executed
	 * when user click on upload document link from the document vault home
	 * screen
	 * <ul>
	 * <li>Get the document Category value from Request Object</li>
	 * <li>Depending on the document Category value Below logic</li>
	 * <li>If DocumentCategory is null<br/>
	 * Then<br/>
	 * Read document property details xml from cache Get the document Categories
	 * for the Organization Else<br/>
	 * Get the Document Type list according to the Selected Document Category</li>
	 * </ul>
	 * 
	 * @param aoRequest action request object
	 * @param aoResponse action response object
	 */
	@ActionMapping(params = "submit_action=uploadFinancialFile")
	protected void documentFinalUploadAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = null;
		String lsDocumentId = null;
		String lsProcurementId = null;
		String lsUserOrgType = null;
		try
		{
			loSession = aoRequest.getPortletSession();
			lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			lsDocumentId = FileNetOperationsUtils.actionFileUpload(aoRequest, aoResponse);
			lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserName = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsConfigurableFiscalYear = (String) loSession.getAttribute(
					HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR, PortletSession.PORTLET_SCOPE);

			if (null != lsDocumentId)
			{
				Map<String, Object> loParameterMap = null;
				Channel loChannel = new Channel();
				loParameterMap = RFPReleaseDocsUtil.getDocumentInfo(loUserSession, lsUserOrgType, lsDocumentId);
				Date loCreatedDate = (Date) loParameterMap.get(P8Constants.PROPERTY_CE_DATE_CREATED);
				CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
						HHSConstants.CBGRIDBEAN_IN_SESSION);
				loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
				TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
						HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
				String lsContractId = loCBGridBean.getContractID();
				if (loTaskDetailsBean != null
						&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(loTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
								.equalsIgnoreCase(loTaskDetailsBean.getTaskName())))
				{
					lsContractId = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.TASK_CONTRACT_ID,
							PortletSession.APPLICATION_SCOPE);
				}
				// Added for R6- Returned Payment Task
				if (null != loTaskDetailsBean && null != loTaskDetailsBean.getReturnPaymentDetailId()
						&& !loTaskDetailsBean.getReturnPaymentDetailId().isEmpty())
				{
					loChannel.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID,
							loTaskDetailsBean.getReturnPaymentDetailId());
				}

				// Added for R6- Returned Payment Task end
				// Fiscal Year id set in aoCBGridBean as a part of release
				// 3.12.0 Enhancement 6602
				loParameterMap.put(HHSConstants.NEW_FISCAL_YEAR_ID, lsConfigurableFiscalYear);
				BaseControllerUtil.insertDocumentDetailsInDBOnUploadUtil(lsDocumentId, lsProcurementId, lsUserOrgType,
						lsUserName, loParameterMap, loChannel, loCreatedDate, loCBGridBean, lsContractId);
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.BASE_DISPLAY_DOCUMENT_SUCCESS);
		}
		// ApplicationException and Exception are thrown from various points
		// from actionFileUpload and insertDocumentDetailsInDBOnUploadUtil
		// (which
		// include getting document info)and from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppEx)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoAppEx, HHSConstants.BASE_DISPLAY_DOCUMENT_SUCCESS);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
			}
		}
		catch (Exception aoEXP)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoEXP, HHSConstants.BASE_DISPLAY_DOCUMENT_SUCCESS);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
			}
		}
	}

	/**
	 * <ul>
	 * <li>This method is used to redirect the controller to the appropriate
	 * render action after uploading document successfully</li>
	 * </ul>
	 * 
	 * @return ModelAndView
	 * @param aoRequest RenderRequest object
	 * @param aoResponse RenderResponse object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@RenderMapping(params = "render_action=displayDocumentSuccess")
	protected ModelAndView displaySuccess(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		boolean lbIsUploadAllowed = true;
		List<ExtendedDocument> loFinancialDocumentList = null;
		String lsReturnedPaymentId = null;
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			P8UserSession loUserSession = (P8UserSession) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			String lsContractId = loCBGridBean.getContractID();
			if (null != loTaskDetailsBean
					&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(loTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
							.equalsIgnoreCase(loTaskDetailsBean.getTaskName())))
			{
				lsContractId = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.TASK_CONTRACT_ID,
						PortletSession.APPLICATION_SCOPE);
			}
			// Added for R6- Returned Payment Task
			if (null != loTaskDetailsBean
					&& HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(loTaskDetailsBean.getTaskName()))
			{
				lsReturnedPaymentId = loTaskDetailsBean.getReturnPaymentDetailId();
			}
			// Added for R6- Returned Payment Task end
			HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
			BaseControllerUtil.setRequiredParam(loRequiredParamMap);

			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			// Added for R6- Returned Payment Task
			loChannelObj.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			// Added for R6- Returned Payment Task end
			loFinancialDocumentList = BaseControllerUtil.displaySuccessUtil(lsUserOrgType, loCBGridBean,
					loRequiredParamMap, loChannelObj, lsContractId);

	        // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loFinancialDocumentList, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope at displaySuccess"+loFinancialDocumentList);

			if(loFinancialDocumentList != null ){
				for( ExtendedDocument eDoc : loFinancialDocumentList   ){
					LOG_OBJECT.Info("Document List Id:"+ eDoc.getDocumentId() );
				}
			}
			//End QC9665 R 9.3.2 List<ExtendedDocument>

			// Added for R6- Returned Payment Task
			if (null != loFinancialDocumentList && !loFinancialDocumentList.isEmpty())
			{
				for (ExtendedDocument extendedDocument : loFinancialDocumentList)
				{
					if (null != loTaskDetailsBean)
					{
						extendedDocument.setCurrentReviewLevel(loTaskDetailsBean.getLevel());
						extendedDocument.setTaskName(loTaskDetailsBean.getTaskName());
					}
				}
			}
			// Added for R6- Returned Payment Task end
			aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loFinancialDocumentList);
			aoRequest.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, lsUserOrgType);
			aoRequest.setAttribute(HHSConstants.SHOW_UPLOAD_DOC, lbIsUploadAllowed);
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, HHSConstants.EMPTY_STRING);
			if (null != aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortletSession.APPLICATION_SCOPE))
			{
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, (String) aoRequest.getPortletSession()
						.getAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, (String) aoRequest.getPortletSession()
						.getAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE));
			}
			if (null != PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS)
					&& PortalUtil.parseQueryString(aoRequest, HHSConstants.SUCCESS).equalsIgnoreCase(
							HHSConstants.UPLOAD))
			{
				aoRequest
						.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.BASE_FILE_UPLOADED_SUCCESSFULLY);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE);
			}
		}
		// ApplicationException is thrown from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExp)
		{
			String lsErrorMsg = HHSConstants.INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST;
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.ERROR_MAP, aoExp.getContextData(),
					PortletSession.APPLICATION_SCOPE);
		}
		/* fetch documents ends */

		return new ModelAndView(HHSConstants.BASE_JSP_TASKS_DOCUMENT);

	}

	/**
	 * This method will be executed when user click on cancel button while
	 * uploading a document
	 * <ul>
	 * <li>Delete the temporary file created from the context path</li>
	 * <li>Redirect user to the rfp document screen</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest resource request Object
	 */
	@ResourceMapping("cancelUploadDocument")
	public void cancelUploadDocumentAction(ResourceRequest aoResourceRequest) throws ApplicationException
	{
		File loFilePath = new File(aoResourceRequest.getParameter(ApplicationConstants.FILE_PATH));
		BaseControllerUtil.deleteTempFile(loFilePath);
	}

	/**
	 * This method will redirect user to the back screen while uploading a
	 * document
	 * <ul>
	 * <li>Get the temporary file path from the request</li>
	 * <li>Delete the temporary file from the path mentioned</li>
	 * <li>Redirect user to the back page</li>
	 * </ul>
	 * 
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@ActionMapping(params = "submit_action=goBackUploadAction")
	public void goBackAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			File loFilePath = new File(aoRequest.getParameter(ApplicationConstants.FILE_PATH));
			BaseControllerUtil.deleteTempFile(loFilePath);
			Document loDocument = new Document();
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			// Changes done for QC:5725
			loDocument.setDocCategory(aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			loDocument.setDocType(aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			loDocument.setCategoryList((ArrayList) FileNetOperationsUtils.getDocCategoryList(lsUserOrgType));
			loDocument.setTypeList((ArrayList) FileNetOperationsUtils.getDocTypeForDocCategory(
					aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY), lsUserOrgType, null));
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoResponse.setRenderParameter(ApplicationConstants.DOC_CATEGORY,
					aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			if (null != aoRequest.getParameter(ApplicationConstants.DOCS_TYPE))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOCS_TYPE,
						aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.BASE_UPLOAD_FINANCIAL_DOCUMENT);
		}
		// ApplicationException and Exception are thrown from various points
		// from getRFPDocCategoryList(including list of document categories from
		// XML DOM object) and from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppEx)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoAppEx, HHSConstants.BASE_UPLOAD_FINANCIAL_DOCUMENT);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
		catch (Exception aoEXP)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoEXP, HHSConstants.BASE_UPLOAD_FINANCIAL_DOCUMENT);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("IOException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
	}

	/**
	 * This method will redirect user to the upload screen with all the details
	 * <ul>
	 * <li>Get the document bean object from the session and set it in the
	 * request</li>
	 * <li>Redirect user to the upload document screen</li>
	 * </ul>
	 * 
	 * @param aoRequest Render Request Object
	 * @param aoResponse Response Request Object
	 * @return ModelAndView for upload File
	 */
	@RenderMapping(params = "render_action=uploadFinancialDocument")
	protected ModelAndView uploadDocumentRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, HHSConstants.EMPTY_STRING);
			aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, HHSConstants.EMPTY_STRING);
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
			aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE));
			if (aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
					PortletSession.APPLICATION_SCOPE) != null)
			{
				aoRequest.setAttribute(
						ApplicationConstants.ERROR_MESSAGE,
						aoRequest.getPortletSession().getAttribute(ApplicationConstants.ERROR_MESSAGE,
								PortletSession.APPLICATION_SCOPE));
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
		}
		// Exception handled here
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("ApplicationException during file upload", aoExp);
		}
		return new ModelAndView(HHSConstants.BASENEW_JSP_TASKS_UPLOAD_DOCS);
	}

	/**
	 * This method will fetch the details of the selected document and display
	 * the properties to the end user This method will execute the method
	 * <b>actionViewDocumentInfo</b> method of <b>FileNetOperationsUtils</b> and
	 * the above mention method will execute one R1 transaction with the
	 * transaction id <b>displayDocProp_filenet</b> class and then set the
	 * render action for document view
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@ActionMapping(params = "submit_action=viewFinancialDocumentInfo")
	protected void viewDocumentInfoAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			ApplicationSession.setAttribute(new Document(), aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			String lsHdnOrgType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BASE_HDN_ORG_TYPE);
			String lsEditable = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BASE_HDN_EDITABLE);
			aoRequest.setAttribute(HHSConstants.FINANCIAL_ORG_TYPE, lsHdnOrgType);
			aoResponse.setRenderParameter(HHSConstants.BASE_HDN_ORG_TYPE, lsHdnOrgType);
			FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.BASE_VIEW_FINANCIAL_DOCUMENT_INFO);
			aoResponse.setRenderParameter(HHSConstants.BASE_HDN_EDITABLE, lsEditable);
		}
		// ApplicationException is thrown from various points
		// from actionViewDocumentInfo (which include while getting object from
		// cache,getting document properties from XML DOM,implementation status
		// from XML DOM object,list of Sample document category,list of Sample
		// document type) and from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoAppExp,
						HHSConstants.BASE_VIEW_FINANCIAL_DOCUMENT_INFO);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("ApplicationException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
	}

	/**
	 * This method will set the document object bean into request and render
	 * forward the user to the view document info screen with all the details
	 * which later displayed to the user.
	 * 
	 * @param aoRequest RenderRequest Object
	 * @param aoResponse RenderResponse Object
	 * @return ModelAndView Object with details where to navigate the user
	 */
	@RenderMapping(params = "render_action=viewFinancialDocumentInfo")
	protected ModelAndView viewDocumentInfoRender(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		PortletSession loSession = null;
		try
		{
			loSession = aoRequest.getPortletSession();
			String lsProposalId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsDocumentStatus = PortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_STATUS);
			String lsProcurementDocId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsIsAddendum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.IS_ADD_TYPE);
			String lsHdnOrgType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BASE_HDN_ORG_TYPE);
			String lsEditable = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BASE_HDN_EDITABLE);
			aoRequest.setAttribute(HHSConstants.BASE_HDN_EDITABLE, lsEditable);
			aoRequest.setAttribute(HHSConstants.BASE_HDN_ORG_TYPE, lsHdnOrgType);
			aoRequest.setAttribute(ApplicationConstants.EDIT_VERSION_PROP,
					aoRequest.getParameter(ApplicationConstants.EDIT_VERSION_PROP));
			aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER,
					(Document) ApplicationSession.getAttribute(aoRequest, true,
							ApplicationConstants.SESSION_DOCUMENT_OBJ));
			aoRequest.setAttribute(ApplicationConstants.IS_LOCKED_STATUS,
					aoRequest.getParameter(ApplicationConstants.IS_LOCKED_STATUS));
			if (null != lsProcurementDocId)
			{
				aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsProcurementDocId);
			}
			aoRequest.setAttribute(HHSConstants.DOC_STATUS, lsDocumentStatus);
			aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, lsProposalId);
			aoRequest.setAttribute(HHSConstants.IS_ADD_TYPE, lsIsAddendum);
			aoRequest.setAttribute(HHSConstants.TOP_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ));
			aoRequest.setAttribute(HHSConstants.MID_LEVEL_FROM_REQ,
					PortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ));
		}
		// Exception handled here
		catch (Exception aoExp)
		{
			String lsErrorMsg = HHSConstants.INTERNAL_ERROR_OCCURED_WHILE_PROCESSING_YOUR_REQUEST;

			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
			loSession.setAttribute(ApplicationConstants.DOCUMET_VAULT_CONTROLLER_TYPE,
					ApplicationConstants.DOCUMENT_EXCEPTION, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		return new ModelAndView(HHSConstants.BASE_JSP_TASKS_VIEWDOCUMENTINFO);
	}

	/**
	 * This method will remove the document from the list
	 * <ul>
	 * <li>Get documentId and procurementId from the request object</li>
	 * <li>Execute Transaction with transaction id <b>removeRfpDocs_db</b> which
	 * will remove the file from the data base</li>
	 * <li>Set the user friendly message in the response which will be displayed
	 * to user</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest - ActionRequest Object
	 * @param aoResourceResponse - ActionResponse Object
	 * @return ModelAndView
	 */
	@ResourceMapping("removeDocumentFromList")
	@SuppressWarnings("rawtypes")
	protected ModelAndView actionRemoveDocumentFromList(ResourceRequest aoResourceRequest,
			ResourceResponse aoResourceResponse)
	{
		Channel loChannel = new Channel();
		HashMap loHmDocReqProps = new HashMap();
		String lsReturnedPaymentId = null;
		String lsUserOrgType = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		ModelAndView loModel = null;
		try
		{
			P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);

			String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;

			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);

			String lsUserName = (String) aoResourceRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.PROCUREMENT_ID);
			String lsProcurementStatus = (String) loChannel.getData(HHSConstants.PROCUREMENT_STATUS);

			String lsDeletedDocumentId = HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.DEL_DOC_ID);
			String lsDocumentSequence = HHSPortalUtil.parseQueryString(aoResourceRequest,
					HHSConstants.BASE_DELETE_DOCUMENT_SEQUENCE);
			String lsHdnTableName = HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.HDN_TABLE_NAME);
			// need to take documentUploadTo from CBgrid bean in Session
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			lsErrorMsg = BaseControllerUtil.actionRemoveDocChannelUtil(loChannel, loHmDocReqProps, lsUserOrgType,
					lsUserName, lsProcurementId, lsProcurementStatus, lsDeletedDocumentId, lsDocumentSequence,
					lsHdnTableName);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
			aoResourceRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_PASS_TYPE);
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			boolean lbIsUploadAllowed = true;
			loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoResourceRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			String lsContractId = loCBGridBean.getContractID();
			if (null != loTaskDetailsBean
					&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(loTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
							.equalsIgnoreCase(loTaskDetailsBean.getTaskName())))
			{
				lsContractId = (String) aoResourceRequest.getPortletSession().getAttribute(
						HHSConstants.TASK_CONTRACT_ID, PortletSession.APPLICATION_SCOPE);
			}
			// Added for R6- Returned Payment Task
			if (null != loTaskDetailsBean
					&& (HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(loTaskDetailsBean.getTaskName())))
			{
				lsReturnedPaymentId = loTaskDetailsBean.getReturnPaymentDetailId();
				loChannel.setData(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			}
			// Added for R6- Returned Payment Task end
			List<ExtendedDocument> loFinancialDocumentList = BaseControllerUtil.actionRemoveDocNxtChannelUtil(
					loChannel, lsUserOrgType, loCBGridBean, lsContractId);
			// Added for R6- Returned Payment Task
			if (null != loFinancialDocumentList && !loFinancialDocumentList.isEmpty())
			{
				for (ExtendedDocument extendedDocument : loFinancialDocumentList)
				{
					if (null != loTaskDetailsBean)
					{
						extendedDocument.setCurrentReviewLevel(loTaskDetailsBean.getLevel());
						extendedDocument.setTaskName(loTaskDetailsBean.getTaskName());
					}
				}
			}
			// Added for R6- Returned Payment Task end
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoResourceRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loFinancialDocumentList, PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			
			aoResourceRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loFinancialDocumentList);
			aoResourceRequest.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, lsUserOrgType);
			aoResourceRequest.setAttribute(HHSConstants.SHOW_UPLOAD_DOC, lbIsUploadAllowed);
			aoResourceRequest.setAttribute(HHSConstants.PROCUREMENT_ID, HHSConstants.EMPTY_STRING);
			loModel = new ModelAndView(HHSConstants.BASE_JSP_TASKS_DOCUMENT);
		}
		// ApplicationException and Exception is thrown from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("ApplicationException during file remove", aoAppExp);
		}
		catch (Exception aoAppExp)
		{
			LOG_OBJECT.Error("ApplicationException during file remove", aoAppExp);
		}
		return loModel;
	}

	/**
	 * <ul>
	 * <li>This method will save the edited details of the document</li>
	 * <li>Transaction id <b> updateFinancialDocumentProperties </b></li>
	 * </ul>
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@ActionMapping(params = "submit_action=saveFinancialDocumentProperties")
	@SuppressWarnings("rawtypes")
	protected void saveDocumentPropertiesAction(ActionRequest aoRequest, ActionResponse aoResponse) throws IOException
	{
		PortletSession loSession = null;
		HashMap loHmDocReqProps = new HashMap();
		try
		{
			loSession = aoRequest.getPortletSession();
			loHmDocReqProps = settingDefaultSessionVar(loHmDocReqProps, loSession,
					HHSConstants.SAVE_DOCUMENT_PROPERTIES_ACTION);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loHmDocReqProps = settingDefaultVariableDoc(loHmDocReqProps, aoRequest,
					HHSConstants.SAVE_DOCUMENT_PROPERTIES_ACTION);
			List<DocumentPropertiesBean> loNewPropertiesList = new ArrayList<DocumentPropertiesBean>();
			Document loDocument = (Document) ApplicationSession.getAttribute(aoRequest,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			String lsDocumentName = aoRequest.getParameter(HHSR5Constants.DOC_NAME);
			String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			boolean lbSkipPropertySave = false;
			if ((null != lsDocumentName && null != loDocument && null != loDocument.getDocName())
					&& !(lsDocumentName.equalsIgnoreCase(loDocument.getDocName())))
			{
				String lsReturnedDocIdonCheckExisting = null;
				lsReturnedDocIdonCheckExisting = FileNetOperationsUtils.checkDocExist(loUserSession, lsOrgId,
						lsDocumentName, loDocument.getDocType(), loDocument.getDocCategory(), lsOrgType);
				if (null != lsReturnedDocIdonCheckExisting && !lsReturnedDocIdonCheckExisting.isEmpty())
				{
					lbSkipPropertySave = true;// Doc Exists with same tile
												// already
												// exists for this Provider.
												// Stop
												// Save Properties and Give
												// Error
												// Message
					String lsMessage = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE,
							"ERR_LINKED_TO_APP_RENAME");
					loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage,
							PortletSession.APPLICATION_SCOPE);
					loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
							ApplicationConstants.MESSAGE_FAIL_TYPE, PortletSession.APPLICATION_SCOPE);
				}
			}
			if (lbSkipPropertySave)
			{
				aoResponse.sendRedirect(aoRequest.getContextPath() + ApplicationConstants.ERROR_HANDLER);
			}
			else
			{
				updateFinancialDocumentProperties(aoRequest, aoResponse, loSession, loHmDocReqProps, loUserSession,
						loNewPropertiesList, loDocument);
			}

		}
		// ApplicationException is thrown from various points
		// from getCurrentDate and getSqlDate in DateUtil (while parsing) from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoExp)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, HHSConstants.BASE_VIEW_FINANCIAL_DOCUMENT_INFO);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoEx)
			{
				LOG_OBJECT.Error("Io Exception in Document Vault", aoExp);
			}
			LOG_OBJECT.Error("Application Exception in Document Vault", aoExp);
		}
	}

	/**
	 * This method is used to update the financial document properties <li>The
	 * transaction used: updateFinancialDocumentProperties</li>
	 * @param aoRequest
	 * @param aoResponse
	 * @param aoSession
	 * @param aoHmDocReqProps
	 * @param aoUserSession
	 * @param aoNewPropertiesList
	 * @param aoDocument
	 * @throws ApplicationException
	 */
	private void updateFinancialDocumentProperties(ActionRequest aoRequest, ActionResponse aoResponse,
			PortletSession aoSession, HashMap aoHmDocReqProps, P8UserSession aoUserSession,
			List<DocumentPropertiesBean> aoNewPropertiesList, Document aoDocument) throws ApplicationException
	{
		FileNetOperationsUtils.setPropertyBeanForFileUpload(aoRequest, aoDocument, aoHmDocReqProps);
		List<DocumentPropertiesBean> loDocumentPropsBeans = aoDocument.getDocumentProperties();
		for (DocumentPropertiesBean loDocProps : loDocumentPropsBeans)
		{
			String loPropertyId = aoRequest.getParameter(loDocProps.getPropertyId());
			BaseControllerUtil.saveDocumentPropertiesActionUtil(aoHmDocReqProps, aoNewPropertiesList, loDocProps,
					loPropertyId);
		}
		// Updated in release 4.0.1- for removing mismatch in modified date
		String lsCurrentDate = DateUtil.getCurrentDateWithTimeStamp();
		// Updated in release 4.0.1- for removing mismatch in modified date end
		BaseControllerUtil.saveDocHashMapUtil(aoHmDocReqProps, aoDocument, lsCurrentDate);

		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
		loChannel.setData(ApplicationConstants.DOCUMENT_ID, aoDocument.getDocumentId());
		loChannel.setData(ApplicationConstants.DOCS_TYPE, aoDocument.getDocType());
		loChannel.setData(HHSConstants.AO_MODIFIED_INFO_MAP, aoHmDocReqProps);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.BASE_UPDATE_FINANCIAL_DOCUMENT_PROPERTIES);
		boolean lbDocPropUpdated = (Boolean) loChannel.getData(HHSConstants.SAVE_STATUS);
		if (lbDocPropUpdated)
		{
			String lsErrorMsg = HHSConstants.DOC_DETAILS_UPDATED_SUCCESFULLY;
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
		}
		else
		{
			String lsErrorMsg = HHSConstants.ERROR_OCCURED_SAVING_DOC_PROPERTIES;
			aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
		}
		// R5 change: start
		String lsHdnOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsEditable = HHSR5Constants.TRUE;
		FileNetOperationsUtils.actionViewDocumentInfo(aoRequest, aoResponse);
		// R5 change end
		aoSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE,
				PortletSession.APPLICATION_SCOPE);
		aoDocument.setDocumentProperties(aoNewPropertiesList);
		aoDocument.setDocName((String) aoHmDocReqProps.get(HHSConstants.DOC_NAME));
		aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.BASE_VIEW_FINANCIAL_DOCUMENT_INFO);
		// R5 change: start
		aoResponse.setRenderParameter(HHSConstants.BASE_HDN_ORG_TYPE, lsHdnOrgType);
		aoResponse.setRenderParameter(HHSConstants.BASE_HDN_EDITABLE, lsEditable);
		// R5 change end
	}

	/**
	 * This method will create the document bean with appropriate data and send
	 * it to the requested view
	 * <ul>
	 * <li>Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>displayDocList_filenet</b> Transaction to get the detail
	 * list of the document present in the vault</li>
	 * <li>Get the list of document objects list from Channel Object</li>
	 * <li>Set the list of document bean objects into the request object</li>
	 * <li>Create the model and view object of the <b>addDocumentFromVault</b>
	 * jsp and render user to the desired page</li>
	 * <li>It will open the add document screen with all the available document
	 * listed</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView with view name
	 */
	@ResourceMapping("addFinanceDocumentResource")
	public ModelAndView resourceAddRfpDocuments(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		String lsFolderPath = null;
		try
		{
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			List<ExtendedDocument> loRfpDocumentList = null;
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, lsProcurementId);

			String lsNextPage = aoRequest.getParameter(HHSConstants.NEXT_PAGE_PARAM);
			if (null == lsNextPage)
			{
				FileNetOperationsUtils.reInitializePageIterator(loSession, loUserSession);
			}
			else
			{
				loUserSession.setNextPageIndex(Integer.valueOf(lsNextPage) - HHSConstants.INT_ONE);
			}
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
					PortletSession.APPLICATION_SCOPE);
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			loChannel.setData(P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC, P8Constants.PROPERTY_PE_IS_FINANCIAL_DOC);
			// Start Added in R5
			HashMap<String, Object> loFilterProps = new HashMap<String, Object>();
			String lsvaultFilter = aoRequest.getParameter(HHSR5Constants.SELECT_VAULT);
			loFilterProps.put(HHSR5Constants.SELECT_VAULT, lsvaultFilter);
			String lsSelectAll = aoRequest.getParameter(HHSConstants.SELECT_ALL_FLAG);
			loFilterProps.put(HHSConstants.SELECT_ALL_FLAG, lsSelectAll);
			String lsFolderId = aoRequest.getParameter(HHSR5Constants.FOLDER_ID);
			if ((null == lsFolderId || lsFolderId.equalsIgnoreCase(HHSR5Constants.NULL))
					&& (null != lsSelectAll && !lsSelectAll.isEmpty()))
			{
				lsFolderPath = FileNetOperationsUtils.setFolderPath(lsOrgType, lsUserOrgType,
						HHSR5Constants.DOCUMENT_VAULT);
				aoRequest.getPortletSession().setAttribute(HHSR5Constants.FOLDER_PATH, lsFolderPath);
			}
			FileNetOperationsUtils.setPropFilter(loFilterProps, lsFolderPath, lsFolderId, lsOrgType, lsUserOrgType);
			RFPReleaseDocsUtil.getSelectDocFromVaultChannel(lsUserOrgType, loUserSession, loChannel, lsOrgType,
					loFilterProps, HHSR5Constants.EMPTY_STRING);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			loChannel.setData(HHSR5Constants.AO_USER_ID, lsUserId);
			// End Added in R5
			if (loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME) != null)
			{
				String lsTransactionName = (String) loChannel.getData(HHSConstants.BASE_TRANSACTION_NAME);
				// Execute the transaction obtained from the file
				// DocumentBusinessApp.java.
				// Added for Release 5
				TransactionManager.executeTransaction(loChannel, HHSConstants.DISPLAY_DOC_LIST_FILENET_TRANS_NAME,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);

				loRfpDocumentList = RFPReleaseDocsUtil.setSelectedDocumentBean(loChannel);
				RFPReleaseDocsUtil.setReqRequestParameter(loSession, loUserSession, lsNextPage);
				aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loRfpDocumentList);
			}
			if (null == loRfpDocumentList)
			{
				// added to remove pagination when there are no documents to
				// show
				loSession.setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS, 0,
						PortletSession.APPLICATION_SCOPE);
			}
		}
		// ApplicationException and Exception are thrown from various points
		// from getDateMMddYYYYFormat in DateUtil (while parsing),
		// setReqRequestParameter in
		// RFPReleaseDocsUtil (while taking data from
		// cache),setSelectedDocumentBean in
		// RFPReleaseDocsUtil (while parsing), from
		// executeTransaction (while executing transaction)
		catch (ApplicationException aoAppExp)
		{
			String lsMessage = aoAppExp.getMessage();
			resourceAddRfpDocumentsError(aoRequest, lsMessage);
		}
		catch (Exception aoExp)
		{
			String lsMessage = aoExp.getMessage();
			resourceAddRfpDocumentsError(aoRequest, lsMessage);
		}
		return new ModelAndView(HHSConstants.BASENEW_JSP_TASKS_ADD_DOCUMENT_FROM_VAULT);
	}

	/**
	 * <ul>
	 * <li>generic exception code for resourceAddRfpDocuments method</li>
	 * </ul>
	 * 
	 * @param aoRequest Resource request Object
	 * @param asMessage Message string
	 */
	private void resourceAddRfpDocumentsError(ResourceRequest aoRequest, String asMessage)
	{
		String lsMessage;
		if (asMessage == null || asMessage.isEmpty())
		{
			try
			{
				lsMessage = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
						HHSConstants.GENERIC_ERROR_MESSAGE);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsMessage);
				aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
			}
			// ApplicationException is thrown from getProperty in PropertyLoader
			// when no property is found
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured while fetching message", aoAppEx);
			}
		}
	}

	/**
	 * This method will insert all the details of the selected document into
	 * <b>Finance_DOCUMENT</b> table
	 * <ul>
	 * <li>Get the Organization type and OrganizationName from the request
	 * object</li>
	 * <li>Execute <b>insertFinanceDocumentDetails_db</b> transaction of
	 * contract budget Mapper <b>RFPReleaseDocsUtil</b> class</li>
	 * <li>After Successfully inserting the record redirect user to the screen
	 * no.<b>S212</b> with the updated document list</li>
	 * <li>R4 release add document from vault functionality for agency</li>
	 * </ul>
	 * Updated Method in R4
	 * 
	 * @param aoRequest - ActionRequest Object
	 * @param aoResponse - ActionResponse Object
	 */
	@ActionMapping(params = "submit_action=addFinanceDocumentFromVault")
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	protected void addDocumentFromVaultAction(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		PortletSession loSession = aoRequest.getPortletSession();
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		Channel loChannel = null;
		String lsReturnedPaymentId = null;
		Map<Object, Object> loParameterMap = new HashMap<Object, Object>();
		try
		{
			String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			// R4 release add document from vault functionality for agency.
			String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsConfigurableFiscalYear = (String) loSession.getAttribute(
					HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR, PortletSession.PORTLET_SCOPE);
			HashMap loDefaultValue = new HashMap();

			// Setting default request parameter
			loDefaultValue = settingDefaultVariableDoc(loDefaultValue, aoRequest,
					HHSConstants.ADD_DOCUMENT_FROM_VAULT_ACTION);

			Date loCreatedDate = DateUtil.getDate(aoRequest.getParameter(HHSConstants.CREATION_DATE));
			Date loDocModifiedDate = DateUtil.getDate(aoRequest.getParameter(HHSConstants.BASE_LAST_MODIFIED_DATE));
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			String lsContractId = loCBGridBean.getContractID();
			if (null != loTaskDetailsBean
					&& (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(loTaskDetailsBean.getTaskName()) || HHSConstants.TASK_CONTRACT_UPDATE
							.equalsIgnoreCase(loTaskDetailsBean.getTaskName())))
			{
				lsContractId = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.TASK_CONTRACT_ID,
						PortletSession.APPLICATION_SCOPE);
			}
			// Added for R6- Returned Payment Task
			if (null != loTaskDetailsBean
					&& HHSConstants.TASK_RETURN_PAYMENT_REVIEW.equalsIgnoreCase(loTaskDetailsBean.getTaskName()))
			{
				lsReturnedPaymentId = loTaskDetailsBean.getReturnPaymentDetailId();
				loDefaultValue.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
			}
			// Added for R6- Returned Payment Task end
			BaseControllerUtil.setParametersMapValue(loParameterMap, lsUserId, loDefaultValue, loCreatedDate,
					loDocModifiedDate, loCBGridBean, lsContractId);
			loParameterMap.put(HHSConstants.NEW_FISCAL_YEAR_ID, lsConfigurableFiscalYear);
			loDefaultValue.put(HHSConstants.AO_PARAMETER_MAP, loParameterMap);
			loChannel = BaseControllerUtil.settingDefaultChannel(loDefaultValue,
					HHSConstants.ADD_DOCUMENT_FROM_VAULT_ACTION);
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);

			loParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_COMPLETED));

			loParameterMap.put(HHSConstants.DATE_LAST_MODIFIED, loDocModifiedDate);
			// if the user organization type is provider
			// R4 release add document from vault functionality for agency
			BaseControllerUtil.addDocumentFromVaultActionUtil(loChannel, loCBGridBean, lsOrgType);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSConstants.BASE_DISPLAY_DOCUMENT_SUCCESS);
			lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE,
					HHSConstants.RFP_DOC_ADDED_SUCCESS);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_PASS_TYPE,
					PortletSession.APPLICATION_SCOPE);
		}
		// Exception is thrown from various points
		// from getDate in DateUtil (while parsing), getProperty in
		// PropertyLoader (when no property is found), from
		// executeTransaction in addDocumentFromVaultActionUtil in
		// BaseControllerUtil (while executing transaction)
		catch (Exception aoExp)
		{
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, HHSConstants.BASE_DISPLAY_DOCUMENT_SUCCESS);
			}
			// IOException is thrown from sendRedirect in
			// setErrorMessageInResponse
			catch (IOException aoIoExp)
			{
				LOG_OBJECT.Error("ApplicationException during file upload", aoIoExp);
				aoResponse.setRenderParameter(ApplicationConstants.RENDER_ACTION, ApplicationConstants.ERROR);
			}
		}
	}

	/**
	 * This method fetch the financial document list for the respective screen
	 * <ul>
	 * <li>Get the transaction name set in the Channel Object</li>
	 * <li>Execute <b>getFinancialDocuments_db</b> Transaction to get the detail
	 * list of the document present in the vault</li>
	 * </ul>
	 * 
	 * @param aoRequest PortletRequest Object
	 * @throws ApplicationException If an Application Exception occurs
	 */
	protected void generateDocumentSection(PortletRequest aoRequest) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		boolean lbIsUploadAllowed = true;
		List<ExtendedDocument> loFinancialDocumentList = null;
		PortletSession loSession = aoRequest.getPortletSession();
		String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);
		HashMap<String, String> loRequiredParamMap = new HashMap<String, String>();
		BaseControllerUtil.setRequiredParam(loRequiredParamMap);
		P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		String lsContractId = loCBGridBean.getContractID();
		// Execute getFinancialDocuments_db Transaction to get the detail
		// list of the document present in the vault
		loFinancialDocumentList = BaseControllerUtil.displaySuccessUtil(lsUserOrgType, loCBGridBean,
				loRequiredParamMap, loChannelObj, lsContractId);

        // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
		loSession.setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loFinancialDocumentList, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Info("save Document List in Session on Application scope at generateDocumentSection");
		if(loFinancialDocumentList != null ){
			for( ExtendedDocument eDoc : loFinancialDocumentList   ){
				LOG_OBJECT.Info("Document List Id:"+ eDoc.getDocumentId() );
			}
		}
		//End QC9665 R 9.3.2 List<ExtendedDocument>

		
		
		// setting the parameter in request along with Error and type
		aoRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST, loFinancialDocumentList);
		aoRequest.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, lsUserOrgType);
		aoRequest.setAttribute(HHSConstants.SHOW_UPLOAD_DOC, lbIsUploadAllowed);
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, HHSConstants.EMPTY_STRING);
		if (null != loSession.getAttribute(ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE))
		{
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, (String) loSession.getAttribute(
					ApplicationConstants.ERROR_MESSAGE, PortletSession.APPLICATION_SCOPE));
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, (String) loSession.getAttribute(
					ApplicationConstants.ERROR_MESSAGE_TYPE, PortletSession.APPLICATION_SCOPE));
		}
	}

	/* Document section Ends */

	/**
	 * This method returns properties of the Fiscal Years in the subgrid
	 * <ul>
	 * <li>Get Starting FY year and years count from Map</li>
	 * </ul>
	 * 
	 * @param aoRequest request
	 * @return a String of subgrid properties of fiscal year.
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("rawtypes")
	private String getFiscalYearCustomSubGridProp(PortletRequest aoRequest) throws ApplicationException
	{
		Map loContractMap = (Map) getContractFiscalYears(aoRequest);
		String lsConfigurableFiscalYear = ((String) aoRequest
				.getAttribute(HHSConstants.BASE_LS_CONFIGURABLE_FISCAL_YEAR)).substring(2);
		StringBuffer loStringBuffer = BaseControllerUtil.getFiscalYearCustomSubGridPropUtil(loContractMap,
				lsConfigurableFiscalYear);
		return loStringBuffer.toString();
	}

	/**
	 * This method is executed if an exception has occurred in action method
	 * <ul>
	 * <li>Check if exception, if yes set it to logger and set context data</li>
	 * <li>Set error message and message type in response</li>
	 * </ul>
	 * Updated Method in R4
	 * @param aoResponse - Action Response
	 * @param asErrorMessage - Error message to be set
	 * @param asErrorMessageType - Error message Type
	 * @param aoContextMap - Context Map to be set in exception
	 * @param aoException - Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void setExceptionMessageFromAction(ActionResponse aoResponse, String asErrorMessage,
			String asErrorMessageType, Map aoContextMap, ApplicationException aoException)
	{
		if (aoException != null)
		{
			aoException.setContextData(aoContextMap);
			LOG_OBJECT.Error(asErrorMessage, aoException);
			if (null != aoException.getRootCause() && null != aoException.getRootCause().getMessage()
					&& !aoException.getRootCause().getMessage().isEmpty())
			{
				try
				{
					// Start || Changes as a part of release 3.6.0 for
					// enhancement request 5905
					if ((PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
							HHSConstants.ERROR_MESSSAGE_UNAUTHORIZED_ACCESS)).equalsIgnoreCase(aoException
							.getRootCause().getMessage())
							|| (PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
									HHSConstants.ERROR_MESSSAGE_EVALUATION_PROGRESS)).equalsIgnoreCase(aoException
									.getRootCause().getMessage()))
					{
						asErrorMessage = aoException.getRootCause().getMessage();
					}
					// End || Changes as a part of release 3.6.0 for enhancement
					// request 5905
				}
				catch (Exception aoExcep)
				{
					LOG_OBJECT.Error("This property not found " + HHSConstants.ERROR_MESSSAGE_UNAUTHORIZED_ACCESS,
							aoExcep);
				}
			}
		}
		if (asErrorMessage != null)
		{
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE, asErrorMessage);
		}
		if (asErrorMessageType != null)
		{
			aoResponse.setRenderParameter(ApplicationConstants.ERROR_MESSAGE_TYPE, asErrorMessageType);
		}
	}

	/**
	 * This method gets error message if any from request and sets it into
	 * request attribute
	 * <ul>
	 * <li>Retrieve error specific parameters "ERROR_MESSAGE" and
	 * "ERROR_MESSAGE_TYPE" and sets them in request attribute</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render request
	 */
	protected void setExceptionMessageInResponse(PortletRequest aoRequest)
	{
		String lsErrorMessage = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MESSAGE);
		String lsErrorMessageType = HHSPortalUtil.parseQueryString(aoRequest, ApplicationConstants.ERROR_MESSAGE_TYPE);
		if (lsErrorMessage != null && !lsErrorMessage.isEmpty())
		{
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMessage);
		}
		if (lsErrorMessageType != null && !lsErrorMessageType.isEmpty())
		{
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, lsErrorMessageType);
		}
	}

	/**
	 * <ul>
	 * <li>Setting default session parameter</li>
	 * </ul>
	 * 
	 * @param aoDefaultValue Default Value Hashmap
	 * @param aoSession Portal Session Object
	 * @param aoMethodName Method name
	 * @return HashMap aoDefaultValue
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static HashMap settingDefaultSessionVar(HashMap aoDefaultValue, PortletSession aoSession, String aoMethodName)
	{
		aoDefaultValue.put(ApplicationConstants.KEY_SESSION_ORG_TYPE, (String) aoSession.getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
		aoDefaultValue.put(ApplicationConstants.KEY_SESSION_USER_ID, (String) aoSession.getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE));

		// setting default session variable for method
		// saveDocumentPropertiesAction
		if (HHSConstants.SAVE_DOCUMENT_PROPERTIES_ACTION.endsWith(aoMethodName))
		{
			aoDefaultValue.put(ApplicationConstants.KEY_SESSION_USER_NAME, (String) aoSession.getAttribute(
					ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE));
		}
		// setting default session variable for method documentFinalUploadAction
		else if (HHSConstants.DOCUMENT_FINAL_UPLOAD_ACTION.endsWith(aoMethodName))
		{
			aoDefaultValue.put(ApplicationConstants.FILENET_SESSION_OBJECT, (P8UserSession) aoSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE));
		}
		return aoDefaultValue;
	}

	/**
	 * <ul>
	 * <li>Setting default request parameter</li>
	 * </ul>
	 * 
	 * @param aoDefaultValue Default Value Hashmap
	 * @param aoResourceRequest Action Request Object
	 * @param aoMethodName Method name
	 * @return HashMap aoDefaultValue
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static HashMap<String, String> settingDefaultVariableDoc(HashMap aoDefaultValue,
			ActionRequest aoResourceRequest, String aoMethodName)
	{
		aoDefaultValue.put(HHSConstants.PROCUREMENT_ID, aoResourceRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoDefaultValue.put(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
				HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO));

		// setting default request variable for method
		// addDocumentFromVaultAction
		if (HHSConstants.ADD_DOCUMENT_FROM_VAULT_ACTION.endsWith(aoMethodName))
		{
			aoDefaultValue.put(HHSConstants.PROPOSAL_ID,
					HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.PROPOSAL_ID));
			aoDefaultValue.put(HHSConstants.DOC_TITLE, aoResourceRequest.getParameter(HHSConstants.DOC_TITLE));
			aoDefaultValue.put(HHSConstants.ADD_DOC_TYPE, aoResourceRequest.getParameter(HHSConstants.ADD_DOC_TYPE));
			aoDefaultValue.put(HHSConstants.DOC_ID, aoResourceRequest.getParameter(HHSConstants.DOC_ID));
			aoDefaultValue.put(HHSConstants.DOC_CATEGORY_LOWERCASE,
					aoResourceRequest.getParameter(HHSConstants.DOC_CATEGORY_LOWERCASE));
			aoDefaultValue.put(HHSConstants.SUBMISSION_BY, aoResourceRequest.getParameter(HHSConstants.SUBMISSION_BY));
			aoDefaultValue.put(HHSConstants.BASE_LAST_MODIFIED_BY,
					aoResourceRequest.getParameter(HHSConstants.BASE_LAST_MODIFIED_BY));
		}
		// setting default request variable for method
		// saveDocumentPropertiesAction
		else if (HHSConstants.SAVE_DOCUMENT_PROPERTIES_ACTION.endsWith(aoMethodName))
		{
			aoDefaultValue.put(HHSConstants.DOC_NAME, aoResourceRequest.getParameter(HHSConstants.DOC_NAME));
			aoDefaultValue.put(HHSConstants.IS_ADD_TYPE,
					HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.IS_ADD_TYPE));
		}
		// setting default request variable for method documentFinalUploadAction
		else if (HHSConstants.DOCUMENT_FINAL_UPLOAD_ACTION.endsWith(aoMethodName))
		{
			aoDefaultValue.put(HHSConstants.PROPOSAL_ID,
					HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.PROPOSAL_ID));
			aoDefaultValue.put(HHSConstants.UPLOAD_DOC_TYPE,
					HHSPortalUtil.parseQueryString(aoResourceRequest, HHSConstants.UPLOAD_DOC_TYPE));
		}
		return aoDefaultValue;
	}

	/**
	 * This method is used to navigate user to the base page with any message we
	 * need to display when user is doing something on a overlay <li>It will do
	 * nothing but redirect user to the default render of the controller</li>
	 * 
	 * @param aoRequest render request object
	 * @param aoResponse render resource object
	 * @return ModelAndView
	 */
	@RenderMapping(params = "render_action=solicitationDisplaySuccess")
	public ModelAndView renderProcurementSuccessMessage(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		ModelAndView loModelAndView = new ModelAndView();
		return loModelAndView;
	}

	/**
	 * This method gets error message if any from request and sets it into
	 * request attribute
	 * <ul>
	 * <li>Retrieve error specific parameters "ERROR_MESSAGE" and
	 * "ERROR_MESSAGE_TYPE" and sets them in request attribute</li>
	 * </ul>
	 * 
	 * @param aoRequest - Render request
	 */
	protected void setGenericErrorMessage(PortletRequest aoRequest)
	{
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
	}

	/**
	 * This method fetch the program list from the database.
	 * 
	 * <ul>
	 * <li>Gets agency id from request and set it into procurement bean
	 * reference.</li>
	 * <li>Set procurement bean reference in the channel object.</li>
	 * <li>Execute the transaction "fetchPragramNameList" and set the program
	 * Name List in request.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 */
	/**
	 * @param aoRequest ResourceRequest Object
	 * @param aoResponse ResourceResponse Object
	 * @return ModelAndView
	 */
	@ResourceMapping("getProgramListForAgency")
	protected ModelAndView getProgramListOnAjaxCall(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ModelAndView loModelAndView = null;
		List<ProgramNameInfo> loProgramNameList = null;
		String lsAgencyId = aoRequest.getParameter(HHSConstants.AGENCYID);
		Channel loChannel = new Channel();
		String lsOrgnizationType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		loChannel.setData(HHSConstants.ORG_ID, lsAgencyId);
		loChannel.setData(HHSConstants.AS_ORG_TYPE, lsOrgnizationType);
		try
		{
			loProgramNameList = ContractListUtils.getProgramNameList(loChannel);
			aoRequest.getPortletSession().setAttribute(HHSConstants.PROGRAM_NAME_LIST, loProgramNameList);
		}
		// Catch the exception thrown by transaction and set the error message
		// in request object and pass to jsp
		catch (ApplicationException loEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loEx);
		}
		// handling exception other than Application Exception.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting program name list for NYC Agency ", loExp);
		}
		loModelAndView = new ModelAndView(HHSConstants.PROGRAM_NAME);
		return loModelAndView;
	}

	/**
	 * This method will get the Provider list from the cache when user type
	 * three characters using getEpinList method defined in basecontroller. <li>
	 * The transaction used: fetchProviderList</li>
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 */
	@ResourceMapping("getProviderListResourceUrl")
	public void getProviderListResourceRequest(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{
		final String lsInputParam = aoResourceRequest.getParameter(HHSConstants.QUERY);
		PrintWriter loOut = null;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.QUERY, lsInputParam);

			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDER_LIST);
			@SuppressWarnings("unchecked")
			final List<AutoCompleteBean> loProviderList = (List<AutoCompleteBean>) loChannel
					.getData(HHSConstants.CHANNEL_PROVIDER_LIST);
			if ((lsInputParam != null) && (lsInputParam.length() >= Integer.parseInt(HHSConstants.THREE))
					&& loProviderList != null)
			{
				loOut = aoResourceResponse.getWriter();
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedAutoCompleteResponse(loProviderList, lsInputParam,
								Integer.parseInt(HHSConstants.THREE)).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
				loOut.flush();
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while getting the Provider list from the database", aoAppEx);

		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception other than application exception occured while getting the Provider list"
					+ "from the database", aoException);
		}
		finally
		{
			if (loOut != null)
			{
				loOut.close();
			}
		}
	}

	/**
	 * this method will get the files and zip into one zip file and let user
	 * browse and save the file
	 * <ul>
	 * <li>create the JSON array with the output and redirect user with download
	 * dialog</li>
	 * <li>display user the error message if any exception occurred.</li>
	 * </ul>
	 * @param aoResponse Resource Response Object
	 * @param asErrorMsg Error Message to display user
	 * @param asZipFilePath file path to store the zip file
	 * @param aoOut PrintWriter Object
	 */
	public void showDownloadDialogOrErrorForZip(ResourceResponse aoResponse, String asErrorMsg, String asZipFilePath,
			PrintWriter aoOut)
	{
		String lsOutputJSONObject;
		if (aoOut != null)
		{
			if (asErrorMsg != null)
			{
				aoResponse.setContentType("text/html");
				lsOutputJSONObject = "{\"output\": [{\"error\": \"" + asErrorMsg + "\"}]}";
				aoOut.write(lsOutputJSONObject);
			}
			else
			{
				File loFile = new File(asZipFilePath);
				if (loFile.exists())
				{
					aoResponse.setContentType("text/html");
					lsOutputJSONObject = "{\"output\": [{\"path\": \""
							+ StringEscapeUtils.escapeXml(loFile.getParentFile().getName() + "/" + loFile.getName())
							+ "\"}]}";
					aoOut.write(lsOutputJSONObject);
				}
				else
				{
					aoResponse.setContentType("text/html");
					lsOutputJSONObject = "{\"output\": [{\"error\": \"" + StringEscapeUtils.escapeXml(asZipFilePath)
							+ "\"}]}";
					aoOut.write(lsOutputJSONObject);
				}
			}
			aoOut.flush();
			aoOut.close();
		}
	}

	/**
	 * Changes for enhancement id 6461 and 6356 release 3.4.0. This method
	 * create a url and redirect to contract budget page on click of ct number
	 * link on invoice list screen and payment list screen.
	 * @param aoRequest request as input
	 * @param aoResponse response as input
	 */
	@ActionMapping(params = "submit_action=viewContractBudget")
	protected void actionViewProcurementSummary(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		try
		{
			String lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
					+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
					+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
					+ HHSConstants.PAGE_LABEL_PORTLET_URL + HHSConstants.CONTRACT_ID_URL
					+ aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW) + HHSConstants.BUDGET_ID_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW) + HHSConstants.FISCAL_YEAR_ID_URL
					+ aoRequest.getParameter(HHSConstants.CLC_FISCAL_YEAR_ID) + HHSConstants.BUDGET_TYPE_URL
					+ aoRequest.getParameter(HHSConstants.BUDGET_TYPE);
			 /** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
				//aoResponse.sendRedirect(lsProcurementSummaryPath);
				aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
			 /** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error(HHSConstants.CBL_EXCEPTION_CONTRACT_BUDGET_DETAILS, loExp);
			String lsErrorMsg = HHSConstants.ERROR_WHILE_PROCESSING_REQUEST;
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE, ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * Changes for enhancement id 6461 and 6356 release 3.4.0 This method create
	 * a url and redirect to invoice summary page on click of payment voucehr
	 * link on payment list screen and view link on invoice list screen.
	 * @param aoRequest
	 * @param aoResponse
	 * @throws IOException
	 */
	@ActionMapping(params = "viewInvoice=contractInvoiceScreen")
	public void viewInvoice(ActionRequest aoRequest, ActionResponse aoResponse) throws IOException

	{

		String lsProcurementSummaryPath = aoRequest.getScheme() + HHSConstants.NOTIFICATION_HREF_1
				+ aoRequest.getServerName() + HHSConstants.COLON + aoRequest.getServerPort()
				+ aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
				+ HHSConstants.PAGE_LABEL_PORTAL_CONTRACT_INVOICE_PAGE_URL + HHSConstants.INVOICE_ID_URL
				+ aoRequest.getParameter(HHSConstants.INVOICE_ID);
		/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
		//aoResponse.sendRedirect(lsProcurementSummaryPath);
		aoResponse.sendRedirect(HHSUtil.sanitizeCarriageReturns(lsProcurementSummaryPath));
		/** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/

	}

	/**
	 * This method is used to fetch budget print summary screen. Created for
	 * Release 3.2.4, #5681
	 * @param aoResourceRequest Portal Session
	 * @param aoChannelObj channel object
	 * @param aoCBGridBean Grid Standard Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void fetchbudgetPrintSummary(CBGridBean aoCBGridBean, Channel aoChannelObj,
			ResourceRequest aoResourceRequest) throws ApplicationException
	{
		String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
		String lsTabsToShow = aoResourceRequest.getParameter(HHSConstants.PRINT_TAB_TO_SHOW_LIST);
		/* R6 - This parameter is an identifier for old budgets for PS changes */
		String lsExistingBudget = aoResourceRequest.getParameter(HHSConstants.EXISTING_BUDGET);
		/*R7 - Cost center */
		String lsCostCenterOpted = aoResourceRequest.getParameter(HHSR5Constants.COST_CENTER_OPTED);
		//R7 changes end
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			loCBGridBean = (CBGridBean) BeanUtils.cloneBean(aoCBGridBean);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_PRINT_BUDGET_SUMMARY);
			ContractBudgetSummary loBudgetSummary = (ContractBudgetSummary) aoChannelObj
					.getData(HHSConstants.LO_BUDGET_SUMMARY);
			String lsIndirectRate = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSConstants.BUDGET_SUMMARY, loBudgetSummary);
			aoResourceRequest.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate);
			aoResourceRequest.setAttribute(HHSConstants.PRINT_TAB_TO_SHOW_LIST, lsTabsToShow);
			aoResourceRequest.setAttribute(HHSConstants.SUB_BUDGET_ID, lsSubBudgetId);
			aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, aoCBGridBean.getContractID());
			aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW, aoCBGridBean.getContractBudgetID());
			//added in R7 for PI indirect Rate Print
			String lsIndirectPIRate = (String) aoChannelObj.getData(HHSR5Constants.PI_INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSR5Constants.PI_INDIRECT_PERCENTAGE,lsIndirectPIRate);
			//R7 changes end	
			// Setting Tab specific Non-Grid data for Page Render
			// Indirect Rate
			String lsIndirectRatePercentage = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSConstants.INDIRECT_PERCENTAGE, lsIndirectRatePercentage);
			// Operations and Support
			CBOperationSupportBean loCBOperationSupportBean = (CBOperationSupportBean) aoChannelObj
					.getData(HHSConstants.CB_OPERATION_SUPPORT_BEAN);
			aoResourceRequest.setAttribute(HHSConstants.OPERATION_AND_SUPPORT_DATA, loCBOperationSupportBean);
			// PersonnelServices
			PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) aoChannelObj
					.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
			String lsPersonnelServiceMasterData = (String) aoChannelObj.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData);
			aoResourceRequest.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA, lsPersonnelServiceMasterData);
			// R6 - Added the attribute to check if the budget is old for PS
			// changes
			aoResourceRequest.setAttribute(HHSConstants.EXISTING_BUDGET, lsExistingBudget);
			// R7 - added in cost center
			aoResourceRequest.setAttribute(HHSR5Constants.COST_CENTER_OPTED, lsCostCenterOpted);
			//R7 changes end
			// ContractedServices
			//R7- added for setting isReadOnly true in print screen in program income grid
			aoResourceRequest.setAttribute(HHSConstants.BASE_SUB_GRID_READONLY,HHSR5Constants.TRUE);
			//R7 changes end
			ContractedServicesBean loCBContractedServicesBean = (ContractedServicesBean) aoChannelObj
					.getData(HHSConstants.BASE_AO_CB_CONTRACTED_SERVICES_BEAN);
			if (null != loCBContractedServicesBean)
			{
				aoResourceRequest.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean);
			}
			else
			{
				loCBContractedServicesBean = new ContractedServicesBean();
				loCBContractedServicesBean.setTotalContractedServices(HHSConstants.ZERO);
				loCBContractedServicesBean.setYtdTotalInvoiceAmt(HHSConstants.ZERO);
				aoResourceRequest.setAttribute(HHSConstants.CONTRACTED_SERVICES_DISPLAY, loCBContractedServicesBean);
			}
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Error occurred while getting data for fetchbudgetPrintSummary.", aoEx);
		}
	}

	/**
	 * This method is used to fetch invoice print summary screen. Created for
	 * Release 3.2.4, #5681
	 * @param aoPortletSession Portal Session
	 * @param aoChannelObj channel object
	 * @param aoCBGridBean Grid Standard Bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void fetchInvoicePrintSummary(CBGridBean aoCBGridBean, Channel aoChannelObj,
			ResourceRequest aoResourceRequest) throws ApplicationException
	{
		String lsSubBudgetId = aoResourceRequest.getParameter(HHSConstants.BASE_HDN_SUB_BUDGET_ID);
		String lsTabsToShow = aoResourceRequest.getParameter(HHSConstants.PRINT_TAB_TO_SHOW_LIST);
		/* R6 - This parameter is an identifier for old budgets for PS changes */
		String lsUsesFte = aoResourceRequest.getParameter(HHSConstants.EXISTING_BUDGET);
		/*R7 - Cost center */
		String lsCostCenterOpted = aoResourceRequest.getParameter(HHSR5Constants.COST_CENTER_OPTED);
		//R7 changes end
		CBGridBean loCBGridBean = new CBGridBean();
		try
		{
			loCBGridBean = (CBGridBean) BeanUtils.cloneBean(aoCBGridBean);
			loCBGridBean.setSubBudgetID(lsSubBudgetId);
			aoChannelObj.setData(HHSConstants.CB_GRID_BEAN_OBJ, loCBGridBean);
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSConstants.FETCH_PRINT_INVOICE_SUMMARY);
			ContractBudgetSummary loInvoiceSummary = (ContractBudgetSummary) aoChannelObj
					.getData(HHSConstants.BASE_LO_CB_INVOICE_SUMMARY);
			String lsIndirectRate = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSConstants.BASE_CB_INVOICE_SUMMARY, loInvoiceSummary);
			aoResourceRequest.setAttribute(HHSConstants.INDIRECT_RATE_KEY, lsIndirectRate);
			aoResourceRequest.setAttribute(HHSConstants.PRINT_TAB_TO_SHOW_LIST, lsTabsToShow);
			//added in R7 for PI indirect Rate Print
			String lsIndirectPIRate = (String) aoChannelObj.getData(HHSR5Constants.PI_INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSR5Constants.PI_INDIRECT_PERCENTAGE,lsIndirectPIRate);
			//
			// Operations and Support
			String lsInvoiceTotalAmounts = (String) aoChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS_OTPS);
			String lsYtdInvoicedAmount = (String) aoChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT_OTPS);
			aoResourceRequest.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS_OTPS, lsInvoiceTotalAmounts);
			aoResourceRequest.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT_OTPS, lsYtdInvoicedAmount);

			// Contracted Services
			String lsCSInvoiceTotalAmounts = (String) aoChannelObj.getData(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS_CS);
			String lsCSYtdInvoicedAmount = (String) aoChannelObj.getData(HHSConstants.BASE_YTD_INVOICED_AMOUNT_CS);
			aoResourceRequest.setAttribute(HHSConstants.BASE_INVOICE_TOTAL_AMOUNTS_CS, lsCSInvoiceTotalAmounts);
			aoResourceRequest.setAttribute(HHSConstants.BASE_YTD_INVOICED_AMOUNT_CS, lsCSYtdInvoicedAmount);
			// Contracted Services 2

			// Personnel Services
			PersonnelServicesData loPersonnelServiceData = (PersonnelServicesData) aoChannelObj
					.getData(HHSConstants.BASE_LO_PERSONNEL_SERVICE_DATA);
			String lsPersonnelServiceMasterData = (String) aoChannelObj.getData(HHSConstants.BASE_PS_MASTER_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.PERSONNEL_SERVICES_DATA, loPersonnelServiceData);
			aoResourceRequest.setAttribute(HHSConstants.PERSONNEL_SERVICES_MASTER_DATA, lsPersonnelServiceMasterData);
			// Start: Added for 8498
			aoResourceRequest.setAttribute(HHSR5Constants.USES_FTE, lsUsesFte);
			// End: Added for 8498
			// Indirect Rate
			String lsIndirectRatePercentage = (String) aoChannelObj.getData(HHSConstants.INDIRECT_RATE_KEY);
			aoResourceRequest.setAttribute(HHSConstants.INDIRECT_PERCENTAGE, lsIndirectRatePercentage);
			// R7 - added in cost center
		    aoResourceRequest.setAttribute(HHSR5Constants.COST_CENTER_OPTED, lsCostCenterOpted);
		    // R7 changes end
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Error occurred while getting data for fetchInvoicePrintSummary.", aoEx);
		}
	}

	/**
	 * <ul>
	 * <li>Create this method as part of release 3.6.0 Enhancement id 6484</li>
	 * </ul>
	 * @param aoResourceRequestForBudget
	 * @param aoResourceResponseForBudget
	 * @throws ApplicationException
	 */
	@ResourceMapping("saveSubBudgetDetails")
	public void saveSubBudgetDetails(ResourceRequest aoResourceRequestForBudget,
			ResourceResponse aoResourceResponseForBudget) throws ApplicationException
	{
		String lsError = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		try
		{
			loOut = aoResourceResponseForBudget.getWriter();
			String lsUserId = (String) aoResourceRequestForBudget.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			SiteDetailsBean loSiteDetailsBean = new SiteDetailsBean();
			loSiteDetailsBean.setSiteName(aoResourceRequestForBudget.getParameter(HHSConstants.SITE_NAME));
			loSiteDetailsBean.setAddress1(aoResourceRequestForBudget.getParameter(HHSConstants.ADDRESS1));
			loSiteDetailsBean.setAddress2(aoResourceRequestForBudget.getParameter(HHSConstants.ADDRESS2));
			loSiteDetailsBean.setCity(aoResourceRequestForBudget.getParameter(HHSConstants.CITY_BUDGET));
			loSiteDetailsBean.setZipCode(aoResourceRequestForBudget.getParameter(HHSConstants.ZIP_CODE));
			loSiteDetailsBean.setState(aoResourceRequestForBudget.getParameter(HHSConstants.STATE_BUDGET));
			loSiteDetailsBean.setActionTaken(aoResourceRequestForBudget.getParameter(HHSConstants.ACTION_TAKEN));
			loSiteDetailsBean.setSubBudgetId(aoResourceRequestForBudget.getParameter(HHSConstants.SUBBUDGET_ID));
			loSiteDetailsBean.setModifiedBy(lsUserId);
			loSiteDetailsBean.setAddressRelatedData(aoResourceRequestForBudget
					.getParameter(HHSConstants.ADDRESS_RELATED_DATA));
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.AO_SUB_BUDGET_DETAILS, loSiteDetailsBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SAVE_SUB_BUDGET_DETAILS);
		}
		catch (ApplicationException aoAppEx)
		{
			lsError = HHSConstants.PAGE_ERROR + HHSConstants.COLON + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException occurred in save of sub budget site", aoAppEx);
		}
		catch (Exception aoEx)
		{
			lsError = HHSConstants.PAGE_ERROR + HHSConstants.COLON + HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception occured in save of sub budget site", aoEx);
		}
		finally
		{
			BaseControllerUtil.catchTaskError(loOut, lsError);
		}
	}

	/**
	 * Added in Release 5 : This method will get the Contract No list from cache
	 * when user types three characters on Ct # textbox on contract filter
	 * screen. Modified this method for Enhancement id 6400 release 3.4.0.
	 * <ul>
	 * <li>1.Get the String entered by the user from request</li>
	 * <li>2. Get the contractNoQueryId parameter from Request and assign it to
	 * contractNoQueryId</li>
	 * <li>3. Based on the value of contractNoQueryId, get the contract no list
	 * from cache</li>
	 * <li>4. if cache does not contain anything, call the service method to get
	 * the epin list from database.</li>
	 * </ul>
	 * 
	 * @param aoRequest a ResourceRequest Object
	 * @param aoResponse a ResourceResponse Object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getContractNoListResourceUrl")
	public void getContractNoListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsPartialUoDenom = HHSConstants.EMPTY_STRING;
		lsPartialUoDenom = aoRequest.getParameter(HHSConstants.QUERY);
		final int liMinLength = HHSConstants.INT_THREE;
		List<String> loContractNoList = new ArrayList<String>();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		String lsContractNoQueryId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_CONTRACT_NO_QUERY_ID);
		// Get CT# based on inputs passed
		String lsQueryStringFromReq = aoRequest.getParameter(HHSConstants.QUERY);
		if (null != lsContractNoQueryId)
		{
			Channel loChannel = null;
			if (HHSConstants.FETCH_CONTRACT_NO_LIST.equalsIgnoreCase(lsContractNoQueryId)
					|| HHSConstants.FETCH_AMEND_CONTRACT_NO_LIST.equalsIgnoreCase(lsContractNoQueryId)
					// Release 5 start change done for provider centric reports
					|| HHSR5Constants.FETCH_CONTRACT_TITLE_LIST.equalsIgnoreCase(lsContractNoQueryId)
					|| HHSConstants.FETCH_PROVIDER_NAME_LIST_QUERY_ID.equalsIgnoreCase(lsContractNoQueryId))
			// Release 5 end change done for provider centric reports
			{
				loChannel = new Channel();
				loChannel.setData(HHSConstants.QUERY_ID, lsContractNoQueryId);
				loChannel.setData(HHSConstants.TO_SEARCH, lsQueryStringFromReq);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_CONTRACT_NO_LIST);
				loContractNoList = (List<String>) loChannel.getData(HHSConstants.AO_CONTRACT_NO_LIST);
			}
		}
		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			PrintWriter loOut = null;
			try
			{
				aoResponse.setContentType(HHSConstants.APPLICATION_JSON);
				loOut = aoResponse.getWriter();
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedResponse(loContractNoList, lsPartialUoDenom, liMinLength).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
			}
			catch (IOException aoExp)
			{
				LOG_OBJECT.Error("IOException occurred while searching providers:", aoExp);
				throw new ApplicationException("IOException occurred while searching providers:", aoExp);
			}
			finally
			{
				if (null != loOut)
				{
					loOut.flush();
					loOut.close();
				}
			}
		}
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module This method navigates to corresponding list screen selected from
	 * the navigation drop down option
	 * <ul>
	 * <li>get Parameters (contractId, budgetId, invoiceId, listAction,
	 * next_action) from Request</li>
	 * </ul>
	 * </p>
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "controllerAction=redirectFromList")
	protected void viewFinancialsSelectedList(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering viewFinancialsSelectedList");
		try
		{
			String lsContractId = aoRequest.getParameter(HHSR5Constants.CONTRACT_ID_WORKFLOW);
			String lsAmendContractId = aoRequest.getParameter(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW);
			String lsBudgetId = aoRequest.getParameter(HHSR5Constants.BUDGET_ID_WORKFLOW);
			String lsInvoiceId = aoRequest.getParameter(HHSR5Constants.INVOICE_ID);
			String lsListAction = aoRequest.getParameter(HHSR5Constants.LIST_ACTION);
			String lsNextAction = aoRequest.getParameter(ApplicationConstants.NEXT_ACTION);
			String lsBudgetType = aoRequest.getParameter(HHSR5Constants.BUDGET_TYPE);
			String lsDefaultFilterStatus = aoRequest.getParameter(HHSR5Constants.CHECK_DEFAULT_FILTER_STATUS);
			PortletSession loPortletSession = aoRequest.getPortletSession();
			// Added for Emergency Build 4.0.0.2 defect 8377
			removeRedirectFromListSessionData(loPortletSession);
			// Navigation Screen Changes: fetch status for suspended Invoice
			if (StringUtils.isNotBlank(lsDefaultFilterStatus))
			{
				loPortletSession.setAttribute(HHSR5Constants.CHECK_DEFAULT_FILTER_STATUS, lsDefaultFilterStatus,
						PortletSession.PORTLET_SCOPE);
			}
			// Navigation Screen Changes Ends:
			if (StringUtils.isNotBlank(lsContractId))
			{
				loPortletSession.setAttribute(HHSR5Constants.CONTRACT_ID_FOR_LIST, lsContractId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsAmendContractId))
			{
				loPortletSession.setAttribute(HHSConstants.AMEND_CONTRACT_ID_WORKFLOW, lsAmendContractId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsBudgetId))
			{
				loPortletSession.setAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST, lsBudgetId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsInvoiceId))
			{
				loPortletSession.setAttribute(HHSR5Constants.INVOICE_ID_FOR_LIST, lsInvoiceId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsBudgetType))
			{
				loPortletSession.setAttribute(HHSR5Constants.BUDGET_TYPE_FROM_LANDING, lsBudgetType,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsNextAction))
			{
				aoResponse.setRenderParameter(ApplicationConstants.NEXT_ACTION, lsNextAction);
			}
			if (StringUtils.isNotBlank(lsListAction))
			{
				aoResponse.setRenderParameter(HHSR5Constants.ACTION, lsListAction);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While Navigating from list screen",
					aoEx);
			LOG_OBJECT.Error("Error Occured While Navigating from list screen", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method will remove all session values used in Navigation part.Added
	 * for Emergency Build 4.0.0.2 defect 8377
	 * 
	 * @param aoPortletSession
	 */
	public void removeRedirectFromListSessionData(PortletSession aoPortletSession)
	{
		aoPortletSession.removeAttribute(HHSR5Constants.CONTRACT_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute(HHSR5Constants.AMEND_CONTRACT_ID_WORKFLOW, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute(HHSR5Constants.INVOICE_ID_FOR_LIST, PortletSession.APPLICATION_SCOPE);
		aoPortletSession.removeAttribute(HHSR5Constants.BUDGET_TYPE_FROM_LANDING, PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module This is a generic code to fecth Procurement Title and competition
	 * pool title List from type ahead.
	 * <ul>
	 * <li>get Parameters(procurementId, query, QueryId) from Request</li>
	 * <li>put data into HashMap</li>
	 * <li>Execute transaction for query id <b>QueryId</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoRequest a Resource Request object
	 * @param aoResponse a Resource Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getProcurementListResourceUrl")
	public void getTitleListResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering getTitleListResourceRequest");
		PrintWriter loOut = null;
		try
		{
			String lsQueryStringFromReq = aoRequest.getParameter(HHSR5Constants.QUERY);
			final int liMinLength = Integer.valueOf(HHSR5Constants.THREE);

			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserId);
			Channel loChannel = new Channel();
			String lsQueryId = aoRequest.getParameter(HHSR5Constants.QUERY_ID);
			HashMap<String, String> loQueryHashMap = new HashMap<String, String>();
			if (lsQueryStringFromReq != null)
			{
				loQueryHashMap.put(HHSR5Constants.TO_SEARCH,
						HHSR5Constants.PERCENT + lsQueryStringFromReq.toLowerCase() + HHSR5Constants.PERCENT);
			}
			loQueryHashMap.put(HHSR5Constants.USER_ID, lsUserId);
			loChannel.setData(HHSR5Constants.AO_INPUT_PARAMS, loQueryHashMap);
			String lsProcurementId = (String) aoRequest.getParameter(HHSR5Constants.PROCUREMENT_ID);
			if (StringUtils.isNotBlank(lsProcurementId))
			{
				loQueryHashMap.put(HHSR5Constants.PROCUREMENT_ID_KEY, lsProcurementId);
			}
			HHSTransactionManager.executeTransaction(loChannel, lsQueryId, HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<AutoCompleteBean> loDataList = (List<AutoCompleteBean>) loChannel.getData(HHSR5Constants.DATA_LIST);
			// content type to json type
			if ((lsQueryStringFromReq != null) && (lsQueryStringFromReq.length() >= liMinLength))
			{
				try
				{
					if (null != loDataList)
					{
						loOut = aoResponse.getWriter();
						aoResponse.setContentType(HHSR5Constants.APPLICATION_JSON);
						final String lsOutputJSONaoResponse = HHSUtil
								.generateDelimitedAutoCompleteResponse(loDataList, lsQueryStringFromReq, liMinLength)
								.toString().trim();
						loOut.print(lsOutputJSONaoResponse);
					}
				}
				// IOException thrown from getWriter
				catch (IOException aoExp)
				{
					throw new ApplicationException("IOException occurred while searching providers:", aoExp);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		finally
		{
			try
			{
				if (null != loOut)
				{
					loOut.flush();
					loOut.close();
				}
			}
			// Catch the Exception thrown at any instance and wrap it into
			// application exception and throw
			catch (Exception aoEx)
			{
				ApplicationException loAppEx = new ApplicationException(
						"Error Occured While functioning on Print Writer", aoEx);
				LOG_OBJECT.Error("Error Occured While functioning on Print Writer", loAppEx);
				throw loAppEx;
			}
		}
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module. This method navigate to the corresponding list screen selected
	 * from drop down option.
	 * <ul>
	 * <li>get Parameters(contractId,budgetType,listAction) from Request</li>
	 * <li>set session attribute</li>
	 * </ul>
	 * </p>
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@ActionMapping(params = "controllerAction=redirectFromLanding")
	protected void viewFinancialsActionList(ActionRequest aoActionRequest, ActionResponse aoActionResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering viewFinancialsActionList");
		try
		{
			String lsContractId = aoActionRequest.getParameter(HHSR5Constants.CONTRACT_ID_WORKFLOW);
			String lsBudgetType = aoActionRequest.getParameter(HHSR5Constants.BUDGET_TYPE);
			String lsInvoiceId = aoActionRequest.getParameter(HHSR5Constants.INVOICE_ID);
			String lsListAction = aoActionRequest.getParameter(HHSR5Constants.LIST_ACTION);
			String lsBudgetId = aoActionRequest.getParameter(HHSR5Constants.BUDGET_ID_WORKFLOW);
			PortletSession loPortletSession = aoActionRequest.getPortletSession();
			if (StringUtils.isNotBlank(lsBudgetId))
			{
				loPortletSession.setAttribute(HHSR5Constants.BUDGET_ID_FOR_LIST,
						aoActionRequest.getParameter(HHSR5Constants.BUDGET_ID_WORKFLOW),
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsContractId))
			{
				loPortletSession.setAttribute(HHSR5Constants.CONTRACT_ID_FOR_LIST, lsContractId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsInvoiceId))
			{
				loPortletSession.setAttribute(HHSR5Constants.INVOICE_ID_FOR_LIST, lsInvoiceId,
						PortletSession.APPLICATION_SCOPE);
			}
			if (StringUtils.isNotBlank(lsBudgetType))
			{
				loPortletSession.setAttribute(HHSR5Constants.BUDGET_TYPE_FROM_LANDING, lsBudgetType,
						PortletSession.APPLICATION_SCOPE);
			}
			StringBuilder lsListScreenPath = new StringBuilder();
			if (lsListAction.equalsIgnoreCase(HHSR5Constants.CONTRACT_LIST_ACTION))
			{
				lsListScreenPath.append(aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
						+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
						+ aoActionRequest.getContextPath() + HHSR5Constants.CONTRACT_LIST_URL);
			}
			else if (lsListAction.equalsIgnoreCase(HHSR5Constants.INVOICE_LIST_ACTION))
			{
				lsListScreenPath.append(aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
						+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
						+ aoActionRequest.getContextPath() + HHSR5Constants.INVOICE_LIST_URL);
			}
			else if (lsListAction.equalsIgnoreCase(HHSR5Constants.PY_PAYMENT_LIST_ACTION))
			{
				lsListScreenPath.append(aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
						+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
						+ aoActionRequest.getContextPath() + HHSR5Constants.PAYMENT_LIST_URL);
			}
			else if (lsListAction.equalsIgnoreCase(HHSR5Constants.CBL_BUDGET_LIST_ACTION))
			{
				lsListScreenPath.append(aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
						+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
						+ aoActionRequest.getContextPath() + HHSR5Constants.BUDGET_LIST_URL);
			}
			// R7 Start: redirect to modified budget list screen from invoice
			// screen
			else if (lsListAction.equalsIgnoreCase(HHSR5Constants.MODIFIED_BUDGET_LIST_REDIRECTION_URL))
			{
				String lsParentBudgetId = aoActionRequest.getParameter(HHSConstants.PARENT_BUDGET_ID);
				if (StringUtils.isNotBlank(lsParentBudgetId))
				{
					loPortletSession.setAttribute(HHSR5Constants.PARENT_BUDGET_ID, lsParentBudgetId,
							PortletSession.APPLICATION_SCOPE);
				}
				loPortletSession.setAttribute(HHSR5Constants.REDIRECT_TO_MODIFIED_BUDGET_LIST, HHSR5Constants.TRUE,
						PortletSession.APPLICATION_SCOPE);

				lsListScreenPath.append(aoActionRequest.getScheme() + HHSR5Constants.NOTIFICATION_HREF_1
						+ aoActionRequest.getServerName() + HHSR5Constants.COLON + aoActionRequest.getServerPort()
						+ aoActionRequest.getContextPath() + HHSR5Constants.BUDGET_LIST_URL);
			}
			// R7 End
			aoActionResponse.sendRedirect(lsListScreenPath.toString());
		}
		catch (Exception loExp)
		{
			LOG_OBJECT
					.Error("Exception Occured while navigation from Budget/Invoice/Payement Landing Screen : ", loExp);
			String lsErrorMsg = HHSR5Constants.ERROR_WHILE_PROCESSING_REQUEST;
			aoActionRequest.setAttribute(HHSR5Constants.ERROR_MESSAGE_BUDGET_LIST, lsErrorMsg);
			aoActionRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE_TYPE,
					ApplicationConstants.MESSAGE_FAIL_TYPE);
		}
	}

	/**
	 * Added in Release 5: This method will handle resource request for
	 * generating json for tree
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
		// Adding session for Defect # 8150
		P8UserSession loUserSession = (P8UserSession) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
		String loJson = null;
		String lsCustmOrgId = null;
		aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
		String lsUserOrg = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsUserOrgType = (String) aoResourceRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		Set<String> loList = (Set<String>) aoResourceRequest.getPortletSession().getAttribute(
				HHSR5Constants.SHARED_ORG_SESSION_LIST);
		String lsDivId = null;
		try
		{
			lsDivId = aoResourceRequest.getParameter(HHSR5Constants.DIV_ID);
			lsCustmOrgId = aoResourceRequest.getParameter(HHSR5Constants.ORG_ID);
			if (null != lsCustmOrgId && !lsCustmOrgId.isEmpty())
			{
				// Adding two parameters for Defect # 8150
				loJson = FileNetOperationsUtils.getOtherOrgFolderStructure(loList, lsUserOrgType, loUserSession,
						lsUserOrg);
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
	 * @param aoRequest
	 * @param lsUserOrgType
	 * @param loReadOnlyCondition
	 * @return
	 */
	public Boolean getProcurementReadOnly(RenderRequest aoRequest, String lsUserOrgType, Boolean loReadOnlyCondition)
	{
		// Release 5
		String lsPermissionType = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (lsPermissionType != null
				&& (lsPermissionType.equalsIgnoreCase(HHSR5Constants.PROVIDER_READ_ONLY) || lsPermissionType
						.equalsIgnoreCase(HHSR5Constants.PROVIDER_PROCUREMENT_ROLE))
				&& lsUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
		{
			loReadOnlyCondition = true;
		}
		// Release 5
		return loReadOnlyCondition;
	}

	/**
	 * @param aoRequest
	 * @param loProposalDetailsReadonlyFlag
	 * @param lsUserOrgType
	 * @return
	 */
	public Boolean getFinancialsReadOnly(RenderRequest aoRequest, String lsUserOrgType)
	{
		Boolean loProposalDetailsReadonlyFlag = false;
		String lsPermissionType = (String) aoRequest.getPortletSession().getAttribute(HHSConstants.PERMISSION_TYPE,
				PortletSession.APPLICATION_SCOPE);
		if (lsPermissionType != null
				&& (lsPermissionType.equalsIgnoreCase(HHSR5Constants.PROVIDER_READ_ONLY) || lsPermissionType
						.equalsIgnoreCase(HHSR5Constants.PROVIDER_FINANCIAL_ROLE))
				&& lsUserOrgType.equalsIgnoreCase(HHSConstants.PROVIDER_ORG))
		{
			loProposalDetailsReadonlyFlag = true;
		}
		return loProposalDetailsReadonlyFlag;
	}

	/**
	 * this method is added in release 5 method will fetch default reassigne
	 * list for all level transaction getDefaultReassingeeList
	 * @param aoRequest
	 * @param aoResponse
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@ResourceMapping("getReassigneeList")
	public ModelAndView reassignDefaultTaskAssignment(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannel = null;
		List<AssigneeList> loAssigneeList = null;
		Map<String, Object> loHashMap = null;
		String lsTaskType = null;
		String lsAgencyName = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
		String lsOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		try
		{
			loChannel = new Channel();
			loAssigneeList = new ArrayList<AssigneeList>();
			loHashMap = new HashMap<String, Object>();
			lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
			lsTaskType = HHSUtil.setTaskType(lsTaskType);
			loHashMap.put(HHSConstants.TASK_TYPE, lsTaskType);
			loHashMap.put(HHSConstants.TASK_LEVEL, aoRequest.getParameter(HHSConstants.TASK_LEVEL));
			loHashMap.put(HHSConstants.ENTITY_ID, aoRequest.getParameter(HHSConstants.ENTITY_ID));
			if (!(lsOrgType.equalsIgnoreCase(HHSConstants.USER_CITY)))
			{
				loHashMap.put(HHSConstants.AGENCY_NAME, lsAgencyName);
			}
			else
			{
				loHashMap.put(HHSConstants.AGENCY_NAME, aoRequest.getParameter(HHSConstants.AGENCYID));
			}
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_DEFAULT_REASSIGNEE_LIST,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			loAssigneeList = (List<AssigneeList>) loChannel.getData(HHSR5Constants.FETCH_REASSIGNEE_LIST);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured in fetching Details", aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in fetching Data for Task: " + lsTaskType, aoEx);
			throw new ApplicationException("Exception occured in returning Data ", aoEx);
		}
		aoRequest.setAttribute(HHSR5Constants.FETCH_REASSIGNEE_LIST, loAssigneeList);
		return new ModelAndView(HHSR5Constants.DEFAULT_REASSIGNEE_LIST_JSP);
	}

	/**
	 * this method is aaded in release 5 transaction- saveDefaultReassingeeList
	 * @param aoRequest- a ResourceRequest
	 * @param aoResponse- ResourceResponse
	 * @throws ApplicationException
	 */
	@ResourceMapping("submitDefaultAssignment")
	protected void submitDefaultAssignment(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		Channel loChannel = null;
		List<DefaultAssignment> loDefaultAssignmentList = null;
		String lsUserId = null;
		DefaultAssignment loDefaultAssignment = null;
		String[] loSelectedValues = null;
		List<String> loSelectedValuesList = null;
		String lsTaskType = null;
		try
		{
			loChannel = new Channel();
			lsTaskType = aoRequest.getParameter(HHSConstants.TASK_TYPE);
			lsTaskType = HHSUtil.setTaskType(lsTaskType);
			lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			loDefaultAssignmentList = new ArrayList<DefaultAssignment>();
			loDefaultAssignment = new DefaultAssignment();
			loSelectedValues = aoRequest
					.getParameterValues(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_QUESTION_ANSWER_SERVICE_SELECT);
			loSelectedValuesList = Arrays.asList(loSelectedValues);
			for (int i = 0; i < loSelectedValuesList.size(); i++)
			{
				loDefaultAssignment = new DefaultAssignment();
				loDefaultAssignment.setTaskType(lsTaskType);
				loDefaultAssignment.setTaskLevel(HHSConstants.LEVEL + (i + 1));
				loDefaultAssignment.setCreatedByUserId(lsUserId);
				loDefaultAssignment.setModifiedByUserId(lsUserId);
				loDefaultAssignment.setEntityId(aoRequest.getParameter(HHSConstants.ENTITY_ID));
				loDefaultAssignment.setAssigneeUserId(loSelectedValuesList.get(i));
				loDefaultAssignment.setAskFlag(aoRequest.getParameter(HHSR5Constants.ASK_CHECK_BOX + (i + 1)));
				loDefaultAssignmentList.add(loDefaultAssignment);
			}
			loChannel.setData(HHSR5Constants.DEFAULT_ASSIGNEE_LIST, loDefaultAssignmentList);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SAVE_DEFAULT_REASSIGNEE_LIST,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Exception occured in fetching Details for task: " + lsTaskType, aoAppExp);
			throw aoAppExp;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in fetching Data for Task: " + lsTaskType, aoEx);
			throw new ApplicationException("Exception occured in returning Data ", aoEx);
		}
	}

	/**
	 * this method is added in R5 this method will return jsp for
	 * defaultAssignee
	 * @param aoResourceRequest - a ResourceRequest object
	 * @param aoResourceResponse - a ResourceResponse object
	 * @return - a ModelAndView object containing the JSP name
	 *         defaultTaskAssignment.jsp
	 */
	@ResourceMapping("setDefaultUser")
	public ModelAndView setDefaultAssignee(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsContractId = (String) aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsTaskType = (String) aoRequest.getParameter(HHSConstants.TASK_FILTER_TYPE);
		String lsTaskLevel = (String) aoRequest.getParameter(HHSR5Constants.TASK_LEVEL);
		String lsAssignedTo = (String) aoRequest.getParameter(HHSR5Constants.ASSIGN_TO);
		HashMap<String, Object> loTaskDetail = new HashMap<String, Object>();
		try
		{
			loTaskDetail.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loTaskDetail.put(HHSConstants.TASK_FILTER_TYPE, lsTaskType);
			loTaskDetail.put(HHSR5Constants.TASK_LEVEL, lsTaskLevel);
			loTaskDetail.put(HHSR5Constants.ASSIGN_TO, lsAssignedTo);
			loTaskDetail.put(HHSConstants.USER_ID, lsUserId);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in returning Data for Task: " + lsTaskType, aoEx);
			throw new ApplicationException("Exception occured in returning Data for contractId: " + lsContractId, aoEx);
		}
		return new ModelAndView(HHSR5Constants.DEFAULT_TASK_ASSIGNMENT_JSP);
	}

	/**
	 * this method is added in R5 this method will update the details default
	 * assigne when user select new assignee from taskDetail form transaction-
	 * updateDefaultUser
	 * @param aoRequest - a ActionRequest object
	 * @param aoResponse - a ActionResponse object
	 */
	@ActionMapping(params = "submit_action=reassignAgencyTask")
	public void saveReassignAgencyTask(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException
	{
		DefaultAssignment loDefaultAssignment = new DefaultAssignment();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String lsTaskType = aoRequest.getParameter(HHSR5Constants.ASSIGNEE_TASKTYPE);
		Channel loChannel = new Channel();
		try
		{
			loDefaultAssignment.setEntityId(aoRequest.getParameter(HHSR5Constants.ENTITY_ID));
			lsTaskType = HHSUtil.setTaskType(lsTaskType);
			loDefaultAssignment.setTaskType(lsTaskType);
			loDefaultAssignment.setTaskLevel(aoRequest.getParameter(HHSR5Constants.ASSIGNEE_TASKLEVEL));
			loDefaultAssignment.setKeepDefault(aoRequest.getParameter(HHSR5Constants.KEEP_CURRENT_DEFAULT));
			loDefaultAssignment.setDefaultAssignments(aoRequest.getParameter(HHSR5Constants.ASSIGNMENT));
			loDefaultAssignment.setAskFlag(aoRequest.getParameter(HHSR5Constants.ASK_AGAIN));
			loDefaultAssignment.setCreatedByUserId(lsUserId);
			loDefaultAssignment.setModifiedByUserId(lsUserId);
			loDefaultAssignment.setAssigneeUserId(aoRequest.getParameter(HHSR5Constants.ASSIGNEE_USERID));
			loChannel.setData(HHSR5Constants.DEFAULT_ASSIGNEE_BEAN, loDefaultAssignment);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.UPDATE_DEFAULT_REASSIGNEE_USER,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in updating Default Reassignee Details", aoExe);
			throw aoExe;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in updating Default Reassignee Details", aoEx);
			throw new ApplicationException("Exception occured while updating Reassignee Details", aoEx);
		}
	}

	/**
	 * Added in Release 5: This method will redirect user to the next tab of the
	 * upload document screen
	 * @param aoRequest Render Request Object
	 * @param aoResponse Render Response Object
	 * 
	 */
	@ActionMapping(params = "submit_action=fileLocation")
	protected void displayUploadingFileLocationRender(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = new Document();
		Map<String, Object> loPropertyMapInfo = new HashMap<String, Object>();
		try
		{
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null == loDocument)
			{
				loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
			}
			String lsEvalPoolMappingId = aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID);
			String lsDocRefNum = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.HIDDEN_DOC_REF_SEQ_NO);
			String lsWobNumber = aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID);
			String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
			String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
			String lsUploadingDocType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.UPLOAD_DOC_TYPE);
			String lsReplacingDocumentId = HHSPortalUtil
					.parseQueryString(aoRequest, HHSConstants.REPLACING_DOCUMENT_ID);
			String lsOrganizationId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.ORGA_ID);
			String lsToplevelFromReq = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.TOP_LEVEL_FROM_REQ);
			String lsMiddlelevelFromReq = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.MID_LEVEL_FROM_REQ);
			loDocument.setHelpCategory(aoRequest.getParameter(ApplicationConstants.HELP_CATEGORY));
			loDocument.setHelpRadioButton(aoRequest.getParameter(ApplicationConstants.HELP));
			loDocument.setHelpDocDesc(aoRequest.getParameter(ApplicationConstants.DOCUMENT_DESCRIPTION));
			List<DocumentPropertiesBean> loDocumentPropsBeans = loDocument.getDocumentProperties();
			Iterator<DocumentPropertiesBean> loDocPropsIt = loDocumentPropsBeans.iterator();
			while (loDocPropsIt.hasNext())
			{
				DocumentPropertiesBean loDocProps = loDocPropsIt.next();
				if (ApplicationConstants.PROPERTY_TYPE_BOOLEAN.equalsIgnoreCase(loDocProps.getPropertyType()))
				{
					if (HHSR5Constants.ON.equalsIgnoreCase(aoRequest.getParameter(loDocProps.getPropertyId()))) // request.getParameter("accidentalCover").equals("checked"))
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
			FileNetOperationsUtils.validatorForUpload((HashMap) loPropertyMapInfo, loDocument.getDocType());
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.DOC_SESSION_BEAN, loDocument);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.PROPERTY_MAP_INFO, loPropertyMapInfo,
					PortletSession.APPLICATION_SCOPE);
			aoResponse.setRenderParameter(HHSConstants.ACTION, HHSConstants.RFP_RELEASE);
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION, HHSR5Constants.CREATE_TREE);
			if (lsEvalPoolMappingId != null && !lsEvalPoolMappingId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID, lsEvalPoolMappingId);
			}
			if (lsProcurementId != null && !lsProcurementId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROCUREMENT_ID, lsProcurementId);
			}
			if (lsUploadingDocType != null && !lsUploadingDocType.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.UPLOAD_DOC_TYPE, lsUploadingDocType);
			}
			if (lsReplacingDocumentId != null && !lsReplacingDocumentId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.REPLACING_DOCUMENT_ID, lsReplacingDocumentId);
			}
			if (lsOrganizationId != null && !lsOrganizationId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.ORGA_ID, lsOrganizationId);
			}
			if (lsWobNumber != null && !lsWobNumber.isEmpty())
			{
				aoResponse.setRenderParameter(ApplicationConstants.WORKFLOW_ID, lsWobNumber);
			}
			if (lsDocRefNum != null && !lsDocRefNum.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO, lsDocRefNum);
			}
			if (lsProposalId != null && !lsProposalId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.PROPOSAL_ID, lsProposalId);
			}
			if (lsToplevelFromReq != null && !lsToplevelFromReq.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.TOP_LEVEL_FROM_REQ, lsToplevelFromReq);
			}
			if (lsMiddlelevelFromReq != null && !lsMiddlelevelFromReq.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.MID_LEVEL_FROM_REQ, lsMiddlelevelFromReq);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in action file upload screen step 2 in Document Vault", aoExp);
			try
			{
				setErrorMessageInResponse(aoRequest, aoResponse, aoExp, null);
			}
			catch (IOException e)
			{
				setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
						ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
			}
		}
	}

	/**
	 * Added in Release 5: This method will will show the tree structure of
	 * Document vault in upload step3 for file location.
	 * @param aoRequest render request object
	 * @param aoResponse render response object
	 * @return formpath as document location
	 */
	@RenderMapping(params = "render_action=treeCreation")
	protected String documentupload(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		String lsFormPath = null;
		Document loDocument = (Document) aoRequest.getPortletSession().getAttribute(HHSR5Constants.DOC_SESSION_BEAN);
		ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
		aoRequest.setAttribute(ApplicationConstants.DOCUMENT_VAULT_DOCUMENT_PARAMETER, loDocument);
		aoRequest.setAttribute(HHSConstants.HIDDEN_DOC_REF_SEQ_NO,
				aoRequest.getParameter(HHSConstants.HIDDEN_DOC_REF_SEQ_NO));
		aoRequest.setAttribute(HHSConstants.EVALUATION_POOL_MAPPING_ID,
				aoRequest.getParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID));
		aoRequest.setAttribute(HHSConstants.IS_FINANCIAL, aoRequest.getParameter(HHSConstants.IS_FINANCIAL));
		aoRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW,
				aoRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		aoRequest.setAttribute(HHSConstants.PROCUREMENT_ID, aoRequest.getParameter(HHSConstants.PROCUREMENT_ID));
		aoRequest.setAttribute(HHSConstants.PROPOSAL_ID, aoRequest.getParameter(HHSConstants.PROPOSAL_ID));
		aoRequest.setAttribute(HHSConstants.IS_ADDENDUM_DOC, aoRequest.getParameter(HHSConstants.IS_ADDENDUM_DOC));
		aoRequest.setAttribute(HHSConstants.AWARD_ID, aoRequest.getParameter(HHSConstants.AWARD_ID));
		aoRequest.setAttribute(HHSConstants.UPLOAD_DOC_TYPE, aoRequest.getParameter(HHSConstants.UPLOAD_DOC_TYPE));
		aoRequest.setAttribute(HHSConstants.REPLACING_DOCUMENT_ID,
				aoRequest.getParameter(HHSConstants.REPLACING_DOCUMENT_ID));
		aoRequest.setAttribute(HHSConstants.ORGA_ID, aoRequest.getParameter(HHSConstants.ORGA_ID));
		aoRequest.setAttribute(HHSConstants.STAFF_ID, aoRequest.getParameter(HHSConstants.STAFF_ID));
		aoRequest.setAttribute(HHSConstants.HIDDEN_HDNCONTRACTID,
				aoRequest.getParameter(HHSConstants.HIDDEN_HDNCONTRACTID));
		aoRequest.setAttribute(ApplicationConstants.WORKFLOW_ID,
				aoRequest.getParameter(ApplicationConstants.WORKFLOW_ID));
		lsFormPath = HHSR5Constants.FINANCIAL_UPLOAD_LOCATION;
		return lsFormPath;
	}

	/**
	 * Added in Release 5: This method will redirect user to the back screen
	 * from step 3 while uploading a document.
	 * @param aoRequest Action Request
	 * @param aoResponse Action Response
	 */
	@ActionMapping(params = "submit_action=goBackActionFromStep3")
	public void goBackActionFromStep3(ActionRequest aoRequest, ActionResponse aoResponse)
	{
		Document loDocument = null;
		try
		{
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			loDocument = (Document) ApplicationSession.getAttribute(aoRequest, true,
					ApplicationConstants.SESSION_DOCUMENT_OBJ);
			ApplicationSession.setAttribute(loDocument, aoRequest, ApplicationConstants.SESSION_DOCUMENT_OBJ);
			if (null != HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID)
					&& !HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID).isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.EVALUATION_POOL_MAPPING_ID,
						HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_POOL_MAPPING_ID));
			}
			if (null != lsWorkflowId && !lsWorkflowId.isEmpty())
			{
				aoResponse.setRenderParameter(HHSConstants.WORKFLOW_ID, lsWorkflowId);
			}
			if (null != aoRequest.getParameter(ApplicationConstants.DOC_CATEGORY))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOC_CATEGORY,
						aoRequest.getParameter(ApplicationConstants.DOCS_CATEGORY));
			}
			if (null != aoRequest.getParameter(ApplicationConstants.DOCS_TYPE))
			{
				aoResponse.setRenderParameter(ApplicationConstants.DOCS_TYPE,
						aoRequest.getParameter(ApplicationConstants.DOCS_TYPE));
			}
			aoResponse.setRenderParameter(HHSConstants.RENDER_ACTION,
					HHSConstants.BASE_UPLOADING_FINANCIAL_FILE_INFORMATION);
		}
		catch (Exception aoEXP)
		{
			setExceptionMessageFromAction(aoResponse, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST,
					ApplicationConstants.MESSAGE_FAIL_TYPE, null, null);
		}
	}
	
	protected boolean isDateValid(String dateStr){		
		return BaseControllerUtil.isDateValid(dateStr);
	}
	
	protected boolean isDateOneBeforeDateTwo(String dateOneStr, String dateTwoStr){
		return BaseControllerUtil.isDateOneBeforeDateTwo(dateOneStr, dateTwoStr);
	}

	/**
	 * This method is added in Release 6 as part of Return payment It is used to
	 * fetch data from data base for return payment
	 * <ul>
	 * <li>1). Get required parameters from ResourceRequest.</li>
	 * <li>2). Set values in ResourceRequest object</li>
	 * <li>3). executeTransaction <b>fetchReturnPaymentDetails</b></li>
	 * </ul>
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoChannelObj Channel
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void fetchReturnPaymentDetails(ResourceRequest aoResourceRequest, Channel aoChannelObj)
	{
		List<ReturnedPayment> loReturnedPayments = new LinkedList<ReturnedPayment>();

		try
		{
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSR5Constants.FETCH_RETURN_PAYMENT_DETAILS,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loReturnedPayments = (List<ReturnedPayment>) aoChannelObj.getData(HHSR5Constants.LO_RETURNED_PAYMENTS);
			for (ReturnedPayment returnedPayment : loReturnedPayments)
			{
				returnedPayment.setLoggedInUserOrgType((String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
				returnedPayment.setLoggedInUserRole((String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE));
			}
			aoResourceRequest.setAttribute(HHSR5Constants.RETURNED_PAYMENT_CHECK_LIST, loReturnedPayments);
			aoResourceRequest.setAttribute(HHSR5Constants.UNRECOUPED_ADV_AMOUNT,
					aoChannelObj.getData(HHSR5Constants.LO_UNRECOUPED_AMOUNT));
			aoResourceRequest.setAttribute(HHSR5Constants.LAST_NOTIFIED_DATE,
					aoChannelObj.getData(HHSR5Constants.LO_LAST_NOTIFIED_DATE));
			Integer noOfRetPayments = (Integer) aoChannelObj.getData(HHSR5Constants.LO_COUNT_RETURNED_PAYMENT);
			aoResourceRequest.setAttribute(HHSConstants.TOTAL_COUNT, ((null == noOfRetPayments) ? 0 : noOfRetPayments));
			aoResourceRequest.getPortletSession().setAttribute(ApplicationConstants.ALERT_VIEW_PAGING_RECORDS,
					((noOfRetPayments == null) ? 0 : noOfRetPayments), PortletSession.APPLICATION_SCOPE);
			aoResourceRequest.setAttribute(HHSR5Constants.TOTAL_APPROVED_RET_PAY_AMOUNT,
					aoChannelObj.getData(HHSR5Constants.TOTAL_AMOUNT));
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoExe);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoEx);
		}
	}

	/**
	 * This method is added in Release 6 as part of Return payment It is used to
	 * fetch data from data base for return payment
	 * <ul>
	 * <li>1). Get required parameters from ResourceRequest.</li>
	 * <li>2). Set values in ResourceRequest object</li>
	 * <li>3). executeTransaction <b>fetchReturnPaymentDetails</b></li>
	 * </ul>
	 * @param aoResourceRequest to get screen parameters and next action to be
	 *            performed
	 * @param aoChannelObj Channel
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	@SuppressWarnings("unchecked")
	private void fetchReturnPaymentList(ResourceRequest aoResourceRequest, Channel aoChannelObj)
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		List<ReturnedPayment> loReturnedPayments = new ArrayList<ReturnedPayment>();

		try
		{
			loReturnedPayment.setBudgetId(aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			aoChannelObj.setData(HHSR5Constants.LO_RETURNED_PAYMENT, loReturnedPayment);
			aoChannelObj.setData(HHSR5Constants.LO_BUDGET_ID, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID));
			HHSTransactionManager.executeTransaction(aoChannelObj, HHSR5Constants.FETCH_RETURN_PAYMENT_LIST,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loReturnedPayments = (List<ReturnedPayment>) aoChannelObj.getData(HHSR5Constants.LO_RETURNED_PAYMENTS);
			for (ReturnedPayment returnedPayment : loReturnedPayments)
			{
				returnedPayment.setLoggedInUserOrgType((String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE));
				returnedPayment.setLoggedInUserRole((String) aoResourceRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE));
			}
			aoResourceRequest.setAttribute(HHSR5Constants.RETURNED_PAYMENT_CHECK_LIST, loReturnedPayments);
		}
		catch (ApplicationException aoExe)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoExe);
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception occured in inserting add Return Payment data", aoEx);
		}
	}

	/**
	 * This method is added in Release 6 as part of Return payment This method
	 * will display returned payment summary
	 * @param aoRetPayRequest
	 * @param aoRetPayResponse
	 * @return returnedPaymentDetails.jsp
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@ResourceMapping("viewReturnedPayment")
	protected ModelAndView displayReturnedPaymentSummary(ResourceRequest aoRetPayRequest,
			ResourceResponse aoRetPayResponse)
	{
		P8UserSession loUserSession = (P8UserSession) aoRetPayRequest.getPortletSession().getAttribute(
				ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);

		Channel loChannel = new Channel();
		Map loMap = new HashMap();
		String lsUserOrgType = (String) aoRetPayRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		String lsBudgetId = HHSPortalUtil.parseQueryString(aoRetPayRequest, HHSP8Constants.BUDGET_ID);
		String lsReturnedPaymentId = HHSPortalUtil
				.parseQueryString(aoRetPayRequest, HHSR5Constants.RETURNED_PAYMENT_ID);
		loChannel.setData(HHSConstants.RSLT_BUDGET_ID, lsBudgetId);
		loChannel.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
		loChannel.setData(HHSR5Constants.LS_RETURNED_PAYMENT_ID, lsReturnedPaymentId);

		HashMap<String, String> loHmRequiredProps = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> loHmReqProps = new HashMap<String, HashMap<String, String>>();
		loHmRequiredProps.put(HHSConstants.DOCUMENT_TITLE, HHSConstants.EMPTY_STRING);
		loMap.put(HHSConstants.RETURN_PAYMENT_DETAIL_ID, lsReturnedPaymentId);
		loMap.put(HHSConstants.ORGANIZATION_TYPE, lsUserOrgType);
		loChannel.setData(HHSConstants.HM_REQIRED_PROPERTY_MAP, loHmRequiredProps);
		loChannel.setData(HHSConstants.AO_PARAMETER_MAP, loMap);

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		PortletSession loSession = aoRetPayRequest.getPortletSession();
		String lsOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
				PortletSession.APPLICATION_SCOPE);

		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.DISPLAY_RETURNED_PAYMENT_SUMMARY,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);

			loTaskDetailsBean.setLaunchOrgType(lsOrgType);
			loTaskDetailsBean.setEntityId(lsReturnedPaymentId);
			loTaskDetailsBean.setEventName(HHSConstants.AGENCY_COMMENTS_DATA);
			loTaskDetailsBean.setEventType(HHSConstants.TASK_RETURN_PAYMENT_REVIEW);
			loTaskDetailsBean.setEntityType(HHSConstants.RETURN_PAYMENT);
			loTaskDetailsBean.setEntityTypeForAgency(HHSConstants.TASK_RETURN_PAYMENT_REVIEW);
			loTaskDetailsBean.setTaskName(HHSConstants.TASK_RETURN_PAYMENT_REVIEW);
			loTaskDetailsBean.setIsEntityTypeTabLevel(false);
			loTaskDetailsBean.setIsTaskScreen(false);

			loTaskDetailsBean = BaseControllerUtil.viewCommentsHistoryUtil(loTaskDetailsBean);

			aoRetPayRequest.setAttribute(HHSConstants.COMMENTS_HISTORY_BEAN, loTaskDetailsBean.getCommentsHistory());
			loReturnedPayment = (ReturnedPayment) loChannel.getData(HHSR5Constants.RETURNED_PAYMENT_SUMMARY);
			loReturnedPayment.setOrgType(lsOrgType);
			aoRetPayRequest.setAttribute(HHSR5Constants.RETURNED_BEAN, loReturnedPayment);
			aoRetPayRequest.setAttribute(ApplicationConstants.SESSION_DOCUMENT_LIST,
					loChannel.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST));
			
            // Start QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents
			aoRetPayRequest.getPortletSession().setAttribute(ApplicationConstants.SESSION_EXTENDED_DOCUMENT_LIST, loChannel.getData(HHSConstants.AO_FINANCIAL_DOCUMENT_LIST), PortletSession.APPLICATION_SCOPE);
			LOG_OBJECT.Info("save Document List in Session on Application scope");
			//End QC9665 R 9.3.2 Citywide cyber assessment program (CCAP) - vulnerabilities -- Users Can Access Other User's Uploaded Documents

			
			
		}

		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in displayReturnedPaymentSummary method", aoExp);
		}

		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in displayReturnedPaymentSummary method", aoEx);
		}

		String jspName = HHSConstants.JSP_CONTRACTBUDGET + HHSR5Constants.RETURNED_PAYMENT_DETAILS;

		return new ModelAndView(jspName);

	}

	/**
	 * This method is R6 added for Return Payment. This method will handle send
	 * notifications action. <br>
	 * <b>transaction: </b>sendBulkNotification<br>
	 * @param aoRequestInput contains all the parameter.
	 * @return process Notification status
	 * @throws Exception
	 */
	protected boolean sendBulkNotificationsAction(Map<String, String> aoRequestInput) throws ApplicationException
	{
		Boolean lbUpdateStatus = Boolean.FALSE;
		Channel loChannel = new Channel();
		aoRequestInput.put(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION, HHSR5Constants.NA_KEY);
		try
		{
			setChannelForNotification(loChannel, aoRequestInput);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.SEND_BULK_NOTIFICATION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lbUpdateStatus = (Boolean) loChannel.getData(HHSR5Constants.GET_UPDATE_NOTIFICATION_STATUS);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in sendBulkNotificationsAction method", aoExp);
			throw aoExp;
		}

		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in sendBulkNotificationsAction method", aoEx);
			throw new ApplicationException("Exception occured in sendBulkNotification transaction", aoEx);
		}
		return lbUpdateStatus;
	}

	/**
	 * This method is added in Release 6 as part of Return payment This method
	 * will set the parameter require to handle the bulk notifications action
	 * @param aoChannel
	 * @param aoRequestInput
	 * @throws Exception
	 */
	protected void setChannelForNotification(Channel aoChannel, Map<String, String> aoRequestInput)
			throws ApplicationException
	{

		String lsNotificationName = HHSConstants.EMPTY_STRING;
		try
		{

			String lsUserId = aoRequestInput.get(HHSR5Constants.USER_ID);
			String lsTargetUser = aoRequestInput.get(HHSR5Constants.TARGET_USER);
			// R6 Updated: for defect 8544
			String lsActionSelected = aoRequestInput.get(HHSR5Constants.ACTION_SELECTED);
			if (null != lsActionSelected && lsActionSelected.equalsIgnoreCase(HHSConstants.SEND_NOTFICATION))
			{
				lsNotificationName = HHSR5Constants.NT322;
				aoChannel.setData(HHSR5Constants.ROLE_ATT,
						HHSR5Constants.NOTIFICATION_PROVIDER_ROLE_MAP.get(HHSR5Constants.L2));
			}
			// Start:user specific check added for notifications
			else if (null != lsActionSelected && lsActionSelected.equalsIgnoreCase(HHSR5Constants.REMMITANCE_REQUEST))
			{
				if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED_L2))
				{
					lsNotificationName = HHSR5Constants.NT321_EDL2;
				}
				else if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED))
				{
					lsNotificationName = HHSR5Constants.NT321;
				}
				else
				{
					lsNotificationName = HHSR5Constants.NT321_L2;
				}
				aoChannel.setData(HHSR5Constants.ROLE_ATT,
						HHSR5Constants.NOTIFICATION_PROVIDER_ROLE_MAP.get(lsTargetUser));
			}
			else if (null != lsActionSelected && lsActionSelected.equalsIgnoreCase(HHSR5Constants.NOTIFY_PROVIDER))
			{
				if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED_L2))
				{
					lsNotificationName = HHSR5Constants.NT323;
				}
				else if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED))
				{
					lsNotificationName = HHSR5Constants.NT323_ED;
				}
				else
				{
					lsNotificationName = HHSR5Constants.NT323_L2;
				}

				aoChannel.setData(HHSR5Constants.ROLE_ATT,
						HHSR5Constants.NOTIFICATION_PROVIDER_ROLE_MAP.get(lsTargetUser));
			}
			aoChannel.setData(HHSR5Constants.ROLE_ADMINISTRATOR_PROV_STAFF, lsTargetUser);
			// End
			// defect 8544 End
			aoChannel.setData(HHSR5Constants.USER_ID, lsUserId);
			aoChannel.setData(HHSR5Constants.ORGANIZATION_TYPE, aoRequestInput.get(HHSR5Constants.ORGANIZATION_TYPE));
			aoChannel.setData(HHSR5Constants.FISCAL_YEAR, aoRequestInput.get(HHSR5Constants.FISCAL_YEAR));
			aoChannel.setData(HHSR5Constants.ACTION, aoRequestInput.get(HHSR5Constants.ACTION_SELECTED));
			aoChannel.setData(HHSConstants.PROGRAM_ID, aoRequestInput.get(HHSConstants.PROGRAM_ID));
			aoChannel.setData(HHSConstants.PROGRAM_NAME, aoRequestInput.get(HHSConstants.PROGRAM_NAME));
			aoChannel.setData(HHSConstants.USER_ORG, aoRequestInput.get(HHSConstants.USER_ORG));
			aoChannel.setData(HHSConstants.BUDGET_ID_WORKFLOW, aoRequestInput.get(HHSConstants.BUDGET_ID_WORKFLOW));
			HashMap<String, Object> loHashMap = new HashMap<String, Object>();
			loHashMap.put(HHSR5Constants.USER_NOTIFY_ID, lsNotificationName);
			aoChannel.setData(HHSR5Constants.USER_NOTIFY_ID, lsNotificationName);

			// Get Process notification input data
			loHashMap = setBulkNotificationsMap(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_NAME_FOR_PROVIDER_BATCH), PropertyLoader.getProperty(
					HHSConstants.HHS_SERVICE_PROPERTIES_PATH, HHSR5Constants.SERVER_PORT_FOR_PROVIDER_BATCH),
					PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							HHSR5Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH),
					PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
							HHSR5Constants.SERVER_PROTOCOL_FOR_PROVIDER_BATCH), aoChannel, lsUserId, lsNotificationName);
			String lsDescription = aoRequestInput.get(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION);
			// Added for defect 8544
			loHashMap.put(HHSR5Constants.REMITTANCE_AMOUNT, aoRequestInput.get(HHSR5Constants.REMITTANCE_AMOUNT));
			loHashMap.put(HHSR5Constants.ACCELERATOR_USER_ROLE, lsTargetUser);
			// defect 8544 End
			loHashMap.put(HHSR5Constants.RETURNED_PAYMENT_DESCRIPTION, lsDescription);
			aoChannel.setData(HHSR5Constants.LO_HASHMAP, loHashMap);
			aoChannel.setData(HHSR5Constants.NOTIFY_FLAG, HHSConstants.BOOLEAN_TRUE);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in sendBulkNotificationsAction method", aoExp);
			throw aoExp;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in sendBulkNotificationsAction method", aoEx);
			throw new ApplicationException("Exception occured in sendBulkNotificationsAction method", aoEx);
		}
	}

	/**
	 * This method is added in R6 - Returned Payment This method will populate
	 * the list of details which will require to process notifications.
	 * @param asServerName
	 * @param asServerPort
	 * @param asContextPath
	 * @param asAppProtocol
	 * @param aoChannel
	 * @param asUserId
	 * @param asNotificationId
	 * @return
	 */
	@SuppressWarnings("unused")
	private HashMap<String, Object> setBulkNotificationsMap(String asServerName, String asServerPort,
			String asContextPath, String asAppProtocol, Channel aoChannel, String asUserId, String asNotificationId)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();

		List<String> loNotificationAlertList = new ArrayList<String>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();

		try
		{
			String lsTargetUser = (String) aoChannel.getData(HHSR5Constants.ROLE_ADMINISTRATOR_PROV_STAFF);
			loNotificationAlertList.add(asNotificationId);
			// Start:user specific check added for notifications
			if (asNotificationId.contains(HHSR5Constants.NT321))
			{
				if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED_L2))
				{
					loNotificationAlertList.add(HHSR5Constants.AL321_EDL2);
				}
				else if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED))
				{
					loNotificationAlertList.add(HHSR5Constants.AL321);
				}
				else
				{
					loNotificationAlertList.add(HHSR5Constants.AL321_L2);
				}
			}
			else if (asNotificationId.equalsIgnoreCase(HHSR5Constants.NT322))
			{
				loNotificationAlertList.add(HHSR5Constants.AL322);
			}
			else
			{
				if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED_L2))
				{
					loNotificationAlertList.add(HHSR5Constants.AL323);
				}
				else if (lsTargetUser.equalsIgnoreCase(HHSR5Constants.ED))
				{
					loNotificationAlertList.add(HHSR5Constants.AL323_ED);
				}
				else
				{
					loNotificationAlertList.add(HHSR5Constants.AL323_L2);
				}
			}
			// End
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(HHSR5Constants.ACTION_SELECTED, aoChannel.getData(HHSR5Constants.ACTION));
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
			aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		}// No Application Exception Required.
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Application Exception in setBulkNotificationsMap method", loExp);
			throw new ApplicationException("Exception occured in setBulkNotificationsMap method", loExp);
		}
		return loNotificationMap;
	}
	// Added for R7 New Program Income tab to fetch data for dropdown
	private void fetchProgramIncomeData(PortletSession aoPortletSession, Channel aoChannelObj,
			ResourceRequest aoResourceRequest) throws ApplicationException
	{
		
		HHSTransactionManager.executeTransaction(aoChannelObj,HHSR5Constants.FETCH_PROGRAM_INCOME_DATA);
	    String lsProgramIncomeData = (String) aoChannelObj.getData(HHSR5Constants.PI_SOURCES);
		aoPortletSession.setAttribute(HHSR5Constants.PROGRAM_INCOME_DATA, lsProgramIncomeData,
				PortletSession.APPLICATION_SCOPE);
	}
	//R7 changes end
	
	/**
	 * Added for fixing Multi-Tab Browsing QC8691
	 * This method simply fetches BudgetId from table SUB_BUDGET
	 * @param subBudgetId
	 * @return BudgetId
	 */
	private String getBudgetIdFromSubBudget(String subBudgetId) throws ApplicationException{
		Channel channel = new Channel();
		channel.setData(HHSConstants.SUB_BUDGET_ID, subBudgetId);
		
		try
		{
			HHSTransactionManager.executeTransaction(channel, HHSConstants.GET_BUDGET_ID_FROM_SUB_BUDGET);
			String budgetId = (String) channel.getData(HHSConstants.BUDGET_ID_WORKFLOW);
			return budgetId;
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Application Exception in getBudgetIdFromSubBudget method", aoExp);
			throw aoExp;
		}

		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in sendBulkNotificationsAction method", aoEx);
			throw new ApplicationException("Exception occured in sendBulkNotification transaction", aoEx);
		}
		
	}
}
