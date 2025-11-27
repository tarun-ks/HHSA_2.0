package com.nyc.hhs.portlet;

import java.util.HashMap;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaskCount;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * This controller Class is used to Get all the Task count Assigned to current
 * User for Task name Business Application Tasks,Service Application
 * Tasks,Withdrawal Request - Business Application Tasks, Withdrawal Request -
 * Service Application Tasks and Contact Us Tasks
 * 
 */

public class HomeInboxPortletView extends AbstractController implements ResourceAwareController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(HomeInboxPortletView.class);

	/**
	 * Render Method of Spring Executed every time & it loads the respective JSP
	 * for every Action
	 * 
	 * @param aoRequest RenderRequest
	 * @param aoResponse RenderResponse
	 * @return loModelAndView
	 */
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
	{
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserId);
		}
		// handling exception other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in HomeInboxTaskPortletView Controller", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, "Error occurred while processing your request");
		}
		ModelAndView loModelAndView = new ModelAndView(ApplicationConstants.HOME_INBOX_FINAL);
		UserThreadLocal.unSet();
		return loModelAndView;

	}

	/**
	 * This method handles request when refresh button is clicked from Task
	 * Inbox portlet
	 * 
	 * <ul>
	 * <li>Get the user Id and org type from session object</li>
	 * <li>If user is of city_org, call method getTaskCountMap() to get task
	 * count</li>
	 * <li>If user is of agency_org, call method getAgencyTaskCountMap() to get
	 * task count</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest object
	 * @param aoResponse ResourceResponse object
	 * @return ModelAndView object containing jsp name
	 */
	@Override
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		try
		{
			PortletSession loSession = aoRequest.getPortletSession();
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);
			UserThreadLocal.setUser(lsUserId);
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{
				getTaskCountMap(aoRequest, loUserSession, lsUserId);
			}
			else if (null != lsUserOrgType && lsUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				getAgencyTaskCountMap(aoRequest, loUserSession, lsUserId);
			}
		}
		// handling Application Exception thrown by transaction layer
		catch (ApplicationException aoAppEx)
		{
			String lsErrorMsg = aoAppEx.getMessage();
			LOG_OBJECT.Error("Application Exception in HomeInboxTaskPortletView Controller", aoAppEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
		}
		// handling exception other than Application Exception
		catch (Exception aoEx)
		{
			String lsErrorMsg = aoEx.getMessage();
			LOG_OBJECT.Error("Application Exception in HomeInboxTaskPortletView Controller", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, lsErrorMsg);
		}
		UserThreadLocal.unSet();
		ModelAndView loModelAndView = new ModelAndView(ApplicationConstants.HOME_INBOX_FINAL);
		return loModelAndView;
	}

	/**
	 * This Method fetches the actual task count for different task type for the
	 * current user logged in.
	 * 
	 * @param aoRequest ActionRequest
	 * @param aoUserSession Filenet Session
	 * @param asSessionUserId User ID
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private void getTaskCountMap(ResourceRequest aoRequest, P8UserSession aoUserSession, String asSessionUserId)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		TaskCount loTaskCount = new TaskCount();
		HashMap<String, Integer> loReqPropsMap = new HashMap<String, Integer>();
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION,
				ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION,
				ApplicationConstants.INT_ZERO);
		// QC9587 R 8.10.0 disable Contact Us task
		//loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US, ApplicationConstants.INT_ZERO);
		
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST,
				ApplicationConstants.INT_ZERO);
		// added for R5 module Manage Organization
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT, ApplicationConstants.INT_ZERO);
		// added for R5 module Manage Organization
		loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loReqPropsMap);
		loChannel.setData(ApplicationConstants.TASK_OWNER_NAME, asSessionUserId);
		loChannel.setData(ApplicationConstants.INCLUDE_NOT_FLAG, ApplicationConstants.BOOLEAN_FALSE);
		TransactionManager.executeTransaction(loChannel, ApplicationConstants.FETCH_AGENCY_HOME_PAGE_TASK_COUNT);
		loReqPropsMap = (HashMap<String, Integer>) loChannel.getData(ApplicationConstants.AGENCY_TASK_COUNT_RESULT);

		loTaskCount
				.setMsBRAppCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION));
		loTaskCount
				.setMsSRAppCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION));
		loTaskCount
				.setMsBRAppWithdrawalCount(null == loReqPropsMap
						.get(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap
								.get(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_BUSINESS_REVIEW_APPLICATION));
		loTaskCount
				.setMsSRWithdrawalAppCount(null == loReqPropsMap
						.get(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_WITHDRAWL_REQUEST_SERVICE_APPLICATION));
		// QC 9587 R 8.10.0 disable Contact Us task
		//loTaskCount
		//		.setMsContactCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US) ? ApplicationConstants.INT_ZERO
		//		: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_CONTACT_US));
		
		loTaskCount.setAwardApprovalTaskCount(null == loReqPropsMap
				.get(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL) ? ApplicationConstants.INT_ZERO : loReqPropsMap
				.get(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL));
		loTaskCount
				.setNewFilingCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_NEW_FILING));
		loTaskCount.setProvAccountReqCount(null == loReqPropsMap
				.get(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST) ? ApplicationConstants.INT_ZERO
				: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_PROVIDER_ACCOUNT_REQUEST));
		loTaskCount
				.setOrgLegalNameTasksCount(null == loReqPropsMap
						.get(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_ORGANIZATION_LEGAL_NAME_UPDATE_REQUEST));
		// added for R5 module Manage Organization
		loTaskCount
				.setApprovedPsrCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_APPROVE_PSR));
		loTaskCount.setAwardApprovalAmountTaskCount(null == loReqPropsMap
				.get(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT) ? ApplicationConstants.INT_ZERO
				: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_AWARD_APPROVAL_AMOUNT));
		// added for R5 module Manage Organization
		loTaskCount.setLbVisibilityFlag(ApplicationConstants.BOOLEAN_TRUE);
		aoRequest.setAttribute(ApplicationConstants.TASK_COUNT_BEAN, loTaskCount);
	}

	/**
	 * This Method fetches the actual task count for different task type for the
	 * current user logged in.
	 * 
	 * <ul>
	 * <li>Get Required Props map containing different task types</li>
	 * <li>Set required objects in channel</li>
	 * <li>Execute transaction with id "fetchAgencyHomePageTaskCount" to get
	 * task counts</li>
	 * <li>Set counts in TaskCount bean object and set in request attribute</li>
	 * </ul>
	 * 
	 * @param aoRequest ResourceRequest object
	 * @param aoUserSession Filenet Session object
	 * @param asSessionUserId a string value of user ID
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void getAgencyTaskCountMap(ResourceRequest aoRequest, P8UserSession aoUserSession, String asSessionUserId)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		TaskCount loTaskCount = new TaskCount();
		HashMap<String, Integer> loReqPropsMap = new HashMap();
		loReqPropsMap.put(P8Constants.TASK_ACCEPT_PROPOSAL, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_EVALUATE_PROPOSAL, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_REVIEW_SCORES, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_CONTRACT_CONFIGURATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_CONTRACT_COF, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_BUDGET_REVIEW, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_INVOICE_REVIEW, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.TASK_PAYMENT_REVIEW, ApplicationConstants.INT_ZERO);
		// task entries added as per release 2.7.0 enhancement 5678
		loReqPropsMap.put(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_ADVANCE_REVIEW, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_AMENDMENT_COF, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_BUDGET_AMENDMENT, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_BUDGET_MODIFICATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_BUDGET_UPDATE, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_AMENDMENT_CONFIGURATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_CONTRACT_UPDATE, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_NEW_FY_CONFIGURATION, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(HHSConstants.TASK_PROCUREMENT_COF, ApplicationConstants.INT_ZERO);
		//Added for R6: Return Payment Review Task
		loReqPropsMap.put(HHSConstants.TASK_RETURN_PAYMENT_REVIEW, ApplicationConstants.INT_ZERO);
		//Added for R6: Return Payment Review Task end
		// added for R5 module Manage Organization
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR, ApplicationConstants.INT_ZERO);
		loReqPropsMap.put(P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT, ApplicationConstants.INT_ZERO);
		// added for R5 module Manage Organization
		loChannel.setData(ApplicationConstants.FILENET_SESSION, aoUserSession);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loReqPropsMap);
		loChannel.setData(ApplicationConstants.TASK_OWNER_NAME, asSessionUserId);
		loChannel.setData(ApplicationConstants.INCLUDE_NOT_FLAG, ApplicationConstants.BOOLEAN_FALSE);
		loChannel.setData(
				ApplicationConstants.AGENCY_ID,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
						PortletSession.APPLICATION_SCOPE));
		TransactionManager.executeTransaction(loChannel, ApplicationConstants.FETCH_AGENCY_HOME_PAGE_TASK_COUNT);
		loReqPropsMap = (HashMap<String, Integer>) loChannel.getData(ApplicationConstants.AGENCY_TASK_COUNT_RESULT);

		loTaskCount
				.setAcceptPropTaskCount(null == loReqPropsMap.get(P8Constants.TASK_ACCEPT_PROPOSAL) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_ACCEPT_PROPOSAL));
		loTaskCount
				.setEvaluatePropTaskCount(null == loReqPropsMap.get(P8Constants.TASK_EVALUATE_PROPOSAL) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_EVALUATE_PROPOSAL));
		loTaskCount
				.setReviewScoresTaskCount(null == loReqPropsMap.get(P8Constants.TASK_REVIEW_SCORES) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_REVIEW_SCORES));
		loTaskCount
				.setAwardDocTaskCount(null == loReqPropsMap.get(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_CONFIGURE_AWARDS_DOCUMENTS));
		loTaskCount
				.setContConfigTaskCount(null == loReqPropsMap.get(P8Constants.TASK_CONTRACT_CONFIGURATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_CONTRACT_CONFIGURATION));
		loTaskCount
				.setCertOfFundsTaskCount(null == loReqPropsMap.get(P8Constants.TASK_CONTRACT_COF) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_CONTRACT_COF));
		loTaskCount
				.setBudgetReviewTaskCount(null == loReqPropsMap.get(P8Constants.TASK_BUDGET_REVIEW) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_BUDGET_REVIEW));
		loTaskCount
				.setInvoiceReviewTaskCount(null == loReqPropsMap.get(P8Constants.TASK_INVOICE_REVIEW) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_INVOICE_REVIEW));
		loTaskCount
				.setPaymentReviewTaskCount(null == loReqPropsMap.get(P8Constants.TASK_PAYMENT_REVIEW) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.TASK_PAYMENT_REVIEW));
		// task entries added as per release 2.7.0 enhancement 5678
		loTaskCount.setAdvancePaymentReviewTaskCount(null == loReqPropsMap
				.get(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW) ? ApplicationConstants.INT_ZERO : loReqPropsMap
				.get(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW));
		loTaskCount
				.setAdvancePaymentRequestTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_ADVANCE_REVIEW) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_ADVANCE_REVIEW));
		loTaskCount
				.setAmendmentCofTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_AMENDMENT_COF) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_AMENDMENT_COF));
		loTaskCount
				.setContractBudgetAmendmentTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_BUDGET_AMENDMENT) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_BUDGET_AMENDMENT));
		loTaskCount.setContractBudgetModificationTaskCount(null == loReqPropsMap
				.get(HHSConstants.TASK_BUDGET_MODIFICATION) ? ApplicationConstants.INT_ZERO : loReqPropsMap
				.get(HHSConstants.TASK_BUDGET_MODIFICATION));
		loTaskCount
				.setContractBudgetUpdateTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_BUDGET_UPDATE) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_BUDGET_UPDATE));
		loTaskCount.setContractConfigurationAmendmentTaskCount(null == loReqPropsMap
				.get(HHSConstants.TASK_AMENDMENT_CONFIGURATION) ? ApplicationConstants.INT_ZERO : loReqPropsMap
				.get(HHSConstants.TASK_AMENDMENT_CONFIGURATION));
		loTaskCount.setContractConfigurationUpdateTaskCount(null == loReqPropsMap
				.get(HHSConstants.TASK_CONTRACT_UPDATE) ? ApplicationConstants.INT_ZERO : loReqPropsMap
				.get(HHSConstants.TASK_CONTRACT_UPDATE));
		loTaskCount
				.setNewFyConfigurationTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_NEW_FY_CONFIGURATION) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_NEW_FY_CONFIGURATION));
		loTaskCount
				.setProcurementCofTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_PROCUREMENT_COF) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(HHSConstants.TASK_PROCUREMENT_COF));
		// added for R5 module Manage Organization
		loTaskCount
				.setCompletePsrCount(null == loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR) ? ApplicationConstants.INT_ZERO
						: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_COMPLETE_PSR));
		loTaskCount.setFinalizeAwardAmountCount(null == loReqPropsMap
				.get(P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT) ? ApplicationConstants.INT_ZERO
				: loReqPropsMap.get(P8Constants.PROPERTY_PE_TASK_TYPE_FINALIZE_AWARD_AMOUNT));
		// added for R5 module Manage Organization
		//Added for R6: Return Payment Review Task
		loTaskCount.setReturnPaymentReviewTaskCount(null == loReqPropsMap.get(HHSConstants.TASK_RETURN_PAYMENT_REVIEW) ? ApplicationConstants.INT_ZERO
				: loReqPropsMap.get(HHSConstants.TASK_RETURN_PAYMENT_REVIEW));
		//Added for R6: Return Payment Review Task
		loTaskCount.setLbVisibilityFlag(ApplicationConstants.BOOLEAN_TRUE);
		aoRequest.setAttribute(ApplicationConstants.TASK_COUNT_BEAN, loTaskCount);
	}

}
