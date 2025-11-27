package com.nyc.hhs.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
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
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.controllers.util.InvoiceUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.annotation.HHSExtToken;
import com.nyc.hhs.annotation.HHSTokenValidator;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This controller class will serve as a controller for all Invoice related
 * screens. Invoice and all invoice related details on various screens will be
 * shown using this controller only.
 * 
 * All the actions on Invoice screens will be handled by this controller only.
 * </p>
 * This file is updated in R7.
 */
@Controller
@RequestMapping("view")
public class InvoiceController extends BaseController implements ResourceAwareController {

	/**
	 * LogInfo object for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(InvoiceController.class);

	/**
	 * This method will be executed when Invoice screen will be loaded for first
	 * time and it will call service to fetch all details for Invoice screen. <br>
	 * Method updated in R4.
	 * <ul>
	 * <li>Transaction manager will be called to call the fetchInvoiceSummary
	 * method of InvoiceService class which will fetch all details for Invoice
	 * summary screen.</li>
	 * </ul>
	 * 
	 * @param aoRequest
	 *            an Action Request object
	 * @param aoResponse
	 *            an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException
	 *             application exception
	 */
	@SuppressWarnings({ "unchecked" })
	@RenderMapping
	/*
	 * Start QC 9499 Multi-Tab Browsing letting Invoice and Advance tasks to be
	 * Approved Multiple times causing Duplicate Payments
	 */
	@HHSExtToken
	/*
	 * End QC 9499 Multi-Tab Browsing letting Invoice and Advance tasks to be
	 * Approved Multiple times causing Duplicate Payments
	 */
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse) throws ApplicationException {
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		CBGridBean loCBGridBean = null;
		PortletSession loSession = null;
		TaskDetailsBean loTaskDetailsBean = null;
		// values are hard-code will get from BudgetList screen
		String lsInvoiceId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INVOICE_ID);
		String lsJspPath = HHSConstants.JSP_INVOICE_CONTRACT_INVOICE;
		try {
			loSession = aoRequest.getPortletSession();
			loTaskDetailsBean = (TaskDetailsBean) loSession.getAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			if (null == loTaskDetailsBean) {
				loTaskDetailsBean = new TaskDetailsBean();
			}
			String lsActionReqParam = PortalUtil.parseQueryString(aoRequest, HHSConstants.RENDER_ACTION);
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			if (null != lsWorkflowId) {
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				lsInvoiceId = loTaskDetailsBean.getInvoiceId();

			}
			// SET JSP NAME FOR InvoiceReview Task
			lsJspPath = InvoiceUtils.setJSPNameForInvoiceReviewTask(lsJspPath, lsActionReqParam);
			// Start Update in R5
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			loHashMap.put(HHSConstants.USER_ID_2, lsUserId);
			loHashMap.put(HHSConstants.AS_ORG_TYPE, lsUserOrgType);
			// End Update in R5
			loHashMap.put(HHSConstants.INVOICE_ID, lsInvoiceId);

			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_CONTRACT_INVOICE);
			ContractList loContractList = (ContractList) loChannelObj.getData(HHSConstants.LO_CONTRACT_LIST);
			aoRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, loContractList.getBudgetId());
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, loContractList.getContractId());
			loTaskDetailsBean.setBudgetId(loContractList.getBudgetId());
			loTaskDetailsBean.setContractId(loContractList.getContractId());
			loTaskDetailsBean.setInvoiceId(lsInvoiceId);
			BudgetDetails loBudgetDetails = (BudgetDetails) loChannelObj.getData(HHSConstants.LO_BUDGET_DETAILS);

			// R7 Changes
			String lsModificationUrlStatus = fetchModificationBudgetCount(loContractList.getBudgetId());
			if (Integer.parseInt(lsModificationUrlStatus) > 0) {
				loBudgetDetails.setBudgetModification(HHSR5Constants.TRUE);
			}
			// R7 Changes

			aoRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
			InvoiceList loInvoiceList = (InvoiceList) loChannelObj.getData(HHSConstants.CI_INVOICE_LIST);
			aoRequest.setAttribute(HHSConstants.INVOICE_INFO, loInvoiceList);
			List<CBGridBean> loSubBudgetList = (List<CBGridBean>) loChannelObj.getData(HHSConstants.SUB_BUDGET_LIST);
			HHSUtil.changeSubBudgetNameForHTMLView(loSubBudgetList);
			loSession.setAttribute(HHSConstants.BUDGET_ACCORDIAN_DATA, loSubBudgetList, PortletSession.APPLICATION_SCOPE);
			loCBGridBean = (CBGridBean) loChannelObj.getData(HHSConstants.LO_CB_GRID_BEAN);
			loCBGridBean.setInvoiceId(loHashMap.get(HHSConstants.INVOICE_ID));

			// SET USERID in CBGridBean
			InvoiceUtils.setUserForUserType(loCBGridBean, lsUserId, lsUserOrgType);
			setInvoiceParam(aoRequest, loChannelObj, loCBGridBean, loContractList, lsUserOrgType);

		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx) {
			// Log is generated in case of any Error and Error message is set
			// for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT.Error("Error occured while processing Invoice", aoAppEx);
		} catch (Exception aoExp) {
			// Log is generated in case of any Error and Error message is set
			// for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT.Error("Error occured while processing Invoice", aoExp);
		}

		loModelAndView = new ModelAndView(lsJspPath);
		// Set TaskDetail Bean in session Required for comment and History
		// sections
		loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean, PortletSession.APPLICATION_SCOPE);

		return loModelAndView;
	}

	/**
	 * This method set Invoice landing related Parameter
	 * 
	 * @param aoRequest
	 *            RenderRequest Object
	 * @param loChannelObj
	 *            Channel Object
	 * @param loCBGridBean
	 *            CBGridBean Object
	 * @param loContractList
	 *            ContractList Object
	 * @param lsUserOrgType
	 *            UserOrgType Object
	 * @throws ApplicationException
	 *             Application Exception object
	 */
	private void setInvoiceParam(RenderRequest aoRequest, Channel loChannelObj, CBGridBean loCBGridBean, ContractList loContractList, String lsUserOrgType) throws ApplicationException {
		PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

		// setting parameter for making screen readonly
		loChannelObj.setData(HHSConstants.ORG_TYPE, lsUserOrgType);
		loChannelObj.setData(HHSConstants.INVOICE_STATUS, loContractList.getInvoiceStatusId());
		aoRequest.setAttribute(HHSConstants.INVOICE_READ_ONLY, HHSConstants.FALSE);
		if (loContractList.getInvoiceStatusId().equalsIgnoreCase((String) PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_SUSPENDED))) {
			aoRequest.setAttribute(HHSConstants.INVOICE_READ_ONLY, HHSConstants.TRUE);
		}
		Boolean loReadOnlyCondition = Boolean.valueOf((String) Rule.evaluateRule(HHSConstants.READ_ONLY_PAGE_ATTRIBUTE_RULE_INVOICE, loChannelObj));
		// Start Release 5 user notification
		loReadOnlyCondition = getProcurementReadOnly(aoRequest, lsUserOrgType, loReadOnlyCondition);
		// End Release 5 user notification
		aoRequest.getPortletSession().setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY, loReadOnlyCondition.toString(), PortletSession.APPLICATION_SCOPE);
		aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);
		// R4 fetch EntryType for contractBudget
		aoRequest.setAttribute(HHSConstants.ENTRY_TYPE_ID, HHSUtil.getEntryTypeDetail(loContractList.getContractId(), loContractList.getBudgetId(), null, null, null));
		// This method will fetch the document list for this screen
		generateDocumentSection(aoRequest);
		// Get errors messages after Submit confirmation transactions
		String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
		aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
		// SET TRANSACTION RESULT STATUS AND MESSAGE
		setResultStatusAndMessage(aoRequest);
		if ((lsErrorMsg == null || HHSConstants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg)) && aoRequest.getParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS) != null) {
			aoRequest.setAttribute(HHSConstants.TRANSACTION_STATUS, HHSConstants.SUCCESS);
			aoRequest.setAttribute(HHSConstants.TRANSACTION_MESSAGE, PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_SUCCESSFULL_INVOICESUBMIT));
		}
	}

	/**
	 * @param aoRequest
	 *            RenderRequest
	 *            <ul>
	 *            <li>This method sets Transaction result status and Error
	 *            message in Render Request</li>
	 *            </ul>
	 * @throws ApplicationException
	 *             Application Exception
	 * 
	 */
	private void setResultStatusAndMessage(RenderRequest aoRequest) throws ApplicationException {
		try {
			if (PortalUtil.parseQueryString(aoRequest, HHSConstants.TRANSACTION_RSLT_STATUS) != null) {
				String lsTransactionStatus = PortalUtil.parseQueryString(aoRequest, HHSConstants.TRANSACTION_RSLT_STATUS);
				if (lsTransactionStatus.equalsIgnoreCase(HHSConstants.SUCCESS)) {
					aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.SUCCESS);
					aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CONTRACT_INVOICE_SUBMITTED));
					aoRequest.setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY, HHSConstants.TRUE);
				} else {
					aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.FAIL_FLAG);
					aoRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.REQUEST_NOT_COMPLETED));
				}
			}
		} catch (ApplicationException aoAppEx) {
			LOG_OBJECT.Error("Error occured in setResultStatusAndMessage while processing Invoice", aoAppEx);
			throw aoAppEx;

		}
	}

	/**
	 * <ul>
	 * <li>This method checks whether the invoice status is
	 * STATUS_INVOICE_PENDING_SUBMISSION and
	 * STATUS_INVOICE_RETURNED_FOR_REVISION</li>
	 * </ul>
	 * 
	 * @param aoInvoiceStatusId
	 *            Invoice Status Id
	 * @return String
	 * @throws ApplicationException
	 *             Application Exception
	 */

	private String getInvoiceStatus(String aoInvoiceStatusId) throws ApplicationException {
		if ((aoInvoiceStatusId.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_PENDING_SUBMISSION))) || aoInvoiceStatusId.equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_INVOICE_RETURNED_FOR_REVISION))) {
			return HHSConstants.FALSE;
		}
		return HHSConstants.TRUE;
	}

	/**
	 * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * This method is used to display the Submit Invoice Confirmation Overlay
	 * <ul>
	 * <li>1.Get the required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest
	 *            - Resource Request Object
	 * @param aoResourceResponse
	 *            - Resource Response Object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException
	 *             - Application Exception object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("invoiceSubmissionOverlay")
	public ModelAndView submitInvoiceOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		
		System.out.println("entering submitInvoiceOverlay...");
		
		ModelAndView loModelAndView = null;
		// fix done as a part of release 3.1.2 defect 6420 - start
		String lsPageLevelInvoiceId = aoResourceRequest.getParameter(HHSConstants.INVOICE_ID);
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
		if (lsPageLevelInvoiceId != null && null != loCBGridBean && loCBGridBean.getInvoiceId() != null && !lsPageLevelInvoiceId.equalsIgnoreCase(loCBGridBean.getInvoiceId())) {
			return null;
		}
		// fix done as a part of release 3.1.2 defect 6420 - end
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);
		aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
		aoResourceRequest.setAttribute(HHSConstants.CONTRACT_ID_KEY, aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		aoResourceRequest.setAttribute(HHSConstants.INVOICE_ID_KEY, aoResourceRequest.getParameter(HHSConstants.INVOICE_ID));
		aoResourceRequest.setAttribute(HHSConstants.PUBLIC_COMMENT_AREA, aoResourceRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA));

		PortletSession loSession = aoResourceRequest.getPortletSession();
		String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
		// set startDate, endDate and invoice number to DB
		Map loInputMap = new HashMap();
		Channel loChannel = new Channel();
		loInputMap.put(HHSConstants.CONTRACT_ID, aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW));
		loInputMap.put(HHSConstants.BUDGET_ID, aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
		loInputMap.put(HHSConstants.INVOICE_ID, aoResourceRequest.getParameter(HHSConstants.INVOICE_ID));
		loInputMap.put(HHSConstants.USER_ID, lsUserId);
		loInputMap.put(HHSConstants.USER_PROVIDER, aoResourceRequest.getParameter(HHSConstants.USER_PROVIDER));
		loInputMap.put(HHSConstants.INVOICE_START_DATE, aoResourceRequest.getParameter(HHSConstants.INVOICE_START_DATE));
		loInputMap.put(HHSConstants.INVOICE_END_DATE, aoResourceRequest.getParameter(HHSConstants.INVOICE_END_DATE));
		loChannel.setData(HHSConstants.IC_INVOICE_MAP, loInputMap);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_INVOICE_DETAILS);

		String lsJspPath = HHSConstants.BASE_JSP_INVOICE + lsJspName;
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method is used to validate the User log in on Submit button from S -
	 * 420 Contract Budget screen
	 * 
	 * @param aoResourceRequest
	 *            - Resource Request Object
	 * @param aoResourceResponse
	 *            - Resource Response Object
	 * @throws ApplicationException
	 *             - exception Object
	 */
	@SuppressWarnings("rawtypes")
	@ResourceMapping("validateUser")
	public void validateUser(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		Map loValidateMap = null;
		Boolean loUserValidation = false;
		String lsErrorMsg = HHSConstants.EMPTY_STRING;
		int liErrorCode = 0;

		try {
			String lsUserEmailId = aoResourceRequest.getParameter(HHSConstants.KEY_SESSION_USER_NAME);
			String lsPassword = aoResourceRequest.getParameter(HHSConstants.PASSWORD);

			loValidateMap = validateUser(lsUserEmailId, lsPassword, aoResourceRequest);
			loUserValidation = (Boolean) loValidateMap.get(HHSConstants.IS_VALID_USER);

			if (!loUserValidation) {
				// Validation error
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CB_VALIDATE_ERROR);
				// liErrorCode set to 1 notifies JSP that validation has failed
				liErrorCode = 1;
				setErrorMsgJSP(aoResourceResponse, lsErrorMsg, liErrorCode);
			}
		}
		// Application Exception is handled here
		catch (ApplicationException aoAppEx) {
			LOG_OBJECT.Error("Exception occured in validateUser", aoAppEx);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			liErrorCode = 2;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, liErrorCode);
		}
	}

	/**
	 * This method is used to perform invoice submission transaction and to
	 * launch WF305 – Invoice Review. <br>
	 * Method updated for R4.
	 * 
	 * @param aoActionRequest
	 *            - ActionRequest Object
	 * @param aoActionResponse
	 *            - ActionResponse Object
	 * @throws ApplicationException
	 *             - Application Exception Object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ActionMapping(params = "submit_action=submitConfirmation")
	public void submitInvoiceConfirmationOverlay(ActionRequest aoActionRequest, ActionResponse aoActionResponse) throws ApplicationException {
		
		System.out.println("entering submitInvoiceConfirmationOverlay...");
		
		Channel loChannel = new Channel();
		HashMap loHmRequiredProps = new HashMap();
		HashMap loHmInvoiceRequiredProps = new HashMap();
		// R4: Tab Level Comments
		Map<String, String> loTabLevelCommentsMap = null;
		try {
			// Get PortletSession
			PortletSession loSession = aoActionRequest.getPortletSession();
			// Set TransactionStatus in ActionResponse
			aoActionResponse.setRenderParameter(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.FAIL_FLAG);
			// Get contractId,BudgetId, invoiceId and ProviderComment from
			// ActionRequest
			String lsContractId = aoActionRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			String lsBudgetId = aoActionRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW);
			String lsInvoiceId = aoActionRequest.getParameter(HHSConstants.INVOICE_ID);
			String lsProviderComment = aoActionRequest.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);

			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);

			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			// Prepare Channel
			loChannel.setData(ApplicationConstants.FILENET_SESSION, loUserSession);
			loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHmRequiredProps.put(HHSConstants.INVOICE_ID, lsInvoiceId);
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, lsUserId);
			loHmRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
			loHmRequiredProps.put(HHSConstants.WORKFLOW_NAME, HHSConstants.WF_INVOICE_REVIEW);

			loHmInvoiceRequiredProps.put(HHSConstants.INVOICE_ID, lsInvoiceId);
			loHmInvoiceRequiredProps.put(HHSConstants.PUBLIC_CMNT_ID, lsProviderComment);
			loHmInvoiceRequiredProps.put(HHSConstants.USER_ID, lsUserId);

			loChannel.setData(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.COMMENTS, HHSConstants.EMPTY_STRING);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);
			loChannel.setData(HHSConstants.AO_HMWF_REQUIRED_PROPS, loHmRequiredProps);
			loChannel.setData(HHSConstants.AO_HM_INVOICE_REQUIRED_PROPS, loHmInvoiceRequiredProps);
			loChannel.setData(HHSConstants.CHANNEL_PARAM_TAB_LEVEL_COMMENTS_MAP_PROVIDER, loTabLevelCommentsMap);
			// InvoiceUtil method for Submit InvoiceConfirmationOverlay
			InvoiceUtils.submitInvoiceConfirmationOverlayUtil(loChannel);
			// Successfully returned from InvoiceUtils method
			aoActionResponse.setRenderParameter(HHSConstants.INVOICE_ID, lsInvoiceId);
			aoActionResponse.setRenderParameter(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.SUCCESS);
			aoActionResponse.setRenderParameter(HHSConstants.SUBMIT_OVERLAY_SUCCESS, HHSConstants.SUBMIT_OVERLAY_SUCCESS);
			loSession.setAttribute(HHSConstants.KEY_SESSION_INVOICE_IS_SUBMITTED, Boolean.TRUE, PortletSession.APPLICATION_SCOPE);
		}
		// Application Exception thrown while transaction in InvoiceUtil class
		// is handled here.
		// The exception thrown from the failed service method flows till here
		// and finally handled
		// in the catch block.
		catch (ApplicationException aoAppExe) {
			// Log is generated in case of any Error and Error message is set
			// for JSP
			String lsLevelErrorMessage = aoAppExe.getMessage();
			ApplicationSession.setAttribute(lsLevelErrorMessage, aoActionRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay", aoAppExe);
		}
		// Any non application Exception occurs catches here and default error
		// message is shown on page
		catch (Exception aoExe) {
			ApplicationSession.setAttribute(HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED, aoActionRequest, HHSConstants.ERROR_MESSAGE);
			LOG_OBJECT.Error("Exception occured in submitConfirmationOverlay  ", aoExe);
		}
	}

	/**
	 * This method write error message on jsp in JSON format
	 * 
	 * @param aoResourceResponse
	 *            Resource Response object
	 * @param asErrorMsg
	 *            Error message
	 * @param aiErrorCode
	 *            Error code
	 * @throws IOException
	 */
	private void setErrorMsgJSP(ResourceResponse aoResourceResponse, String asErrorMsg, int aiErrorCode) {
		PrintWriter loOut = null;
		try {
			asErrorMsg = HHSConstants.ERROR_1 + aiErrorCode + HHSConstants.MESSAGE_1 + asErrorMsg + HHSConstants.CLOSING_BRACE_1;
			aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
			loOut = aoResourceResponse.getWriter();
			loOut.print(asErrorMsg);
		}
		// IOException is handled here
		catch (IOException aoIOExp) {
			// Error is logged in case any IO error happens while writing to
			// response
			LOG_OBJECT.Error("Exception occured in setErrorMsgJSP", aoIOExp);
		} finally {
			if (null != loOut) {
				loOut.flush();
				loOut.close();
			}
		}
	}

	/**
	 * This method is modified as a part of Release 3.1.2 Defect 6420
	 * 
	 * <ul>
	 * <li>Comparing page level invoice id and session invoice id and returning
	 * null in case of discrepancy</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>This method is triggered on clicking save button on Contract Budget
	 * screens (Base/Amend/Modification/Update)</li>
	 * <li>Transaction Id :getInvoiceDetails</li>
	 * <li>Transaction id <b>invoiceAmountAssignmentValidation<b></li>
	 * 
	 * <li>Service Class : InvoiceService</li>
	 * 
	 * <li>Validations on click of save button:-</li>
	 * <ol>
	 * <li>Visible and enabled for provider users if Invoice Status = “Pending
	 * Submission” or “Returned for Revision”</li>
	 * <li>Hidden for provider users if Invoice Status = “Pending Approval”,
	 * “Approved”, “Withdrawn” or “Suspended”</li>
	 * <li>Hidden for Agency and Accelerator users</li>
	 * <li>Save all the data entered, including comments and an Active grid row
	 * if one was being edited.</li>
	 * <li>If comments exist, append Comments from S346.45 Comment Text Box to
	 * S346.49 View Comments History table.</li>
	 * <li>Stay on the same page and keep any expanded sections as expanded.</li>
	 * <li>If the budget section was open, then display the same tab which was
	 * displayed before save and refresh all screen elements.</li>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoResourceRequestForInvoice
	 *            - Resource Request Object
	 * @param aoResourceResponseForInvoice
	 *            - Resource Response Object
	 * @return loModelAndView - ModelAndView Object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("saveContractInvoice")
	public ModelAndView saveContractInvoice(ResourceRequest aoResourceRequestForInvoice, ResourceResponse aoResourceResponseForInvoice) {
		
		LOG_OBJECT.Info("entering saveContractInvoice...");		
		
		PrintWriter loOutForInvoice = null;
		ModelAndView loModelAndViewForInvoice = null;
		Map<String, String> loMap = null;
		
		String startDateStr = aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_START_DATE);
		String endDateStr = aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_END_DATE);
		
		String lsPageLevelInvoiceId = aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_ID);
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		try { // fix done as a part of release 3.1.2 defect 6420 - start
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequestForInvoice, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
			if (lsPageLevelInvoiceId != null && loCBGridBean.getInvoiceId() != null && !lsPageLevelInvoiceId.equalsIgnoreCase(loCBGridBean.getInvoiceId())) {
				return null;
			}
			// fix done as a part of release 3.1.2 defect 6420 - end
			aoResourceResponseForInvoice.setContentType(HHSConstants.TEXT_HTML);
			loMap = InvoiceUtils.validateInvoiceStatus(aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_ID));
			loOutForInvoice = aoResourceResponseForInvoice.getWriter();
			if (!loMap.containsKey(HHSConstants.SUCCESS_MESSAGE)) {
				lsErrorMsg = loMap.get(HHSConstants.ERROR_MESSAGE);
			} else {
				Boolean loValidatStatus = false;
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.INVOICE_ID_KEY, (String) aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_ID));
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INVOICE_AMOUNT_ZERO_VALIDATION);
				loValidatStatus = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
				
				LOG_OBJECT.Info("saveContractInvoice invoice service date from -> " + startDateStr);
				LOG_OBJECT.Info("saveContractInvoice invoice service date to -> " + endDateStr);
				
				if(!isDateValid(startDateStr)){
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_SERVICE_DATE_FROM);
					LOG_OBJECT.Info("saveContractInvoice start date was invalid, lsErrorMsg -> " + lsErrorMsg);
				}else if(!isDateValid(endDateStr)){
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_SERVICE_DATE_TO);
					LOG_OBJECT.Info("saveContractInvoice end date was invalid, lsErrorMsg -> " + lsErrorMsg);
				}else if(!isDateOneBeforeDateTwo(startDateStr, endDateStr)){
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_INVOICE_DATE);
					LOG_OBJECT.Info("saveContractInvoice end date was before start date, lsErrorMsg -> " + lsErrorMsg);
				}else if (loValidatStatus) {
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INVOICE_AMOUNT_ASSIGNEMENT_VALIDATION);
					loValidatStatus = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
					if (loValidatStatus) {
						String lsUserId = (String) aoResourceRequestForInvoice.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
						String lsProviderComment = aoResourceRequestForInvoice.getParameter(HHSConstants.PUBLIC_COMMENT_AREA);
						TaskDetailsBean loTaskDetailsBeanForInvoice = new TaskDetailsBean();
						loTaskDetailsBeanForInvoice.setEntityId(aoResourceRequestForInvoice.getParameter(HHSConstants.INVOICE_ID));
						loTaskDetailsBeanForInvoice.setEntityType(HHSConstants.AUDIT_INVOICES);
						loTaskDetailsBeanForInvoice.setIsTaskScreen(false);
						loTaskDetailsBeanForInvoice.setProviderComment(lsProviderComment);
						loTaskDetailsBeanForInvoice.setUserId(lsUserId);
						saveCommentNonAudit(loTaskDetailsBeanForInvoice);
						// fix done as a part of release 3.1.2 defect 6420 -
						// start
						/*
						 * CBGridBean loCBGridBean = (CBGridBean)
						 * PortletSessionHandler.getAttribute(
						 * aoResourceRequestForInvoice, true,
						 * HHSConstants.CBGRIDBEAN_IN_SESSION);
						 */
						// fix done as a part of release 3.1.2 defect 6420 - end
						Map loInputMapForInvoice = new HashMap();
						loInputMapForInvoice.put(HHSConstants.CONTRACT_ID, loCBGridBean.getContractID());
						loInputMapForInvoice.put(HHSConstants.BUDGET_ID, loCBGridBean.getContractBudgetID());
						loInputMapForInvoice.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
						loInputMapForInvoice.put(HHSConstants.USER_ID, lsUserId);
						loInputMapForInvoice.put(HHSConstants.USER_PROVIDER, aoResourceRequestForInvoice.getParameter(HHSConstants.USER_PROVIDER));
						
						loInputMapForInvoice.put(HHSConstants.INVOICE_START_DATE, startDateStr);
						loInputMapForInvoice.put(HHSConstants.INVOICE_END_DATE, endDateStr);
						
						loChannel.setData(HHSConstants.IC_INVOICE_MAP, loInputMapForInvoice);
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_INVOICE_DETAILS);
						BudgetDetails loBudgetDetails = (BudgetDetails) loChannel.getData(HHSConstants.LO_BUDGET_DETAILS);
						aoResourceRequestForInvoice.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
						ContractList loContractList = (ContractList) loChannel.getData(HHSConstants.LO_CONTRACT_LIST);
						aoResourceRequestForInvoice.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
						InvoiceList loInvoiceList = (InvoiceList) loChannel.getData(HHSConstants.CI_INVOICE_LIST);
						aoResourceRequestForInvoice.setAttribute(HHSConstants.INVOICE_INFO, loInvoiceList);
						lsErrorMsg = HHSConstants.EMPTY_STRING;
						loModelAndViewForInvoice = new ModelAndView(HHSConstants.JSP_PATH_INVOICE_ASSIGN_ADVANCE);
					} else {
						lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_INVOICE_EXCEED_ERR);
					}
				} else {
					lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_INVOICE_ZERO_ERR);
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp) {
			LOG_OBJECT.Error("Application Exception in saveContractInvoice method", aoExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp) {
			LOG_OBJECT.Error("Exception in saveContractInvoice method", aoExp);
		}
		// finally block print error message for validation
		finally {
			if (null != loOutForInvoice) {
				if (!HHSR5Constants.EMPTY_STRING.equalsIgnoreCase(lsErrorMsg)) {
					loOutForInvoice.print(lsErrorMsg);
				}
				loOutForInvoice.flush();
				loOutForInvoice.close();
			}
		}
		
		LOG_OBJECT.Info("lsErrorMsg -> " + lsErrorMsg);
		
		return loModelAndViewForInvoice;
	}

	/**
	 * <ul>
	 * <li>This method is triggered on clicking save button on Invoice Review
	 * Task Screen</li>
	 * <li>Transaction Id : saveContractInvoiceReview</li>
	 * 
	 * <li>Service Class : InvoiceService</li>
	 * 
	 * <li>Save Invoice number into database-</li>
	 * <ol>
	 * <li>Save all the data entered.</li>
	 * <li>Stay on the same page and keep any expanded sections as expanded.</li>
	 * <li>If the budget section was open</li>
	 * </ol>
	 * </ul>
	 * 
	 * @param aoResRequest
	 *            ResourceRequest
	 * @param aoResResponse
	 *            ResourceResponse
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResourceMapping("saveContractInvoiceRevew")
	public ModelAndView saveContractInvoiceReview(ResourceRequest aoResRequest, ResourceResponse aoResResponse) {
		
		

		ModelAndView loModelAndView = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsInvoiceNumber = aoResRequest.getParameter(HHSConstants.INVOICE_NUMBER);
		PrintWriter loOut = null;
		try {
			LOG_OBJECT.Debug("entering saveContractInvoiceReview...");
			aoResResponse.setContentType(HHSConstants.TEXT_HTML);
			loOut = aoResResponse.getWriter();
			String lsUserId = (String) aoResRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			CBGridBean loCBGridBeanForInvoiceReview = (CBGridBean) PortletSessionHandler.getAttribute(aoResRequest, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannel = new Channel();
			Map loInputMap = new HashMap();
			loInputMap.put(HHSConstants.CONTRACT_ID, loCBGridBeanForInvoiceReview.getContractID());
			loInputMap.put(HHSConstants.BUDGET_ID, loCBGridBeanForInvoiceReview.getContractBudgetID());
			loInputMap.put(HHSConstants.INVOICE_ID, loCBGridBeanForInvoiceReview.getInvoiceId());
			loInputMap.put(HHSConstants.INVOICE_NUMBER, lsInvoiceNumber);
			loInputMap.put(HHSConstants.USER_ID, lsUserId);
			loChannel.setData(HHSConstants.IC_INVOICE_MAP, loInputMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.IC_SAVE_INVOICE_REVIEW_TASK_DETAIL);
			BudgetDetails loBudgetDetails = (BudgetDetails) loChannel.getData(HHSConstants.LO_BUDGET_DETAILS);
			aoResRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
			ContractList loContractList = (ContractList) loChannel.getData(HHSConstants.LO_CONTRACT_LIST);
			aoResRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);
			InvoiceList loInvoiceList = (InvoiceList) loChannel.getData(HHSConstants.CI_INVOICE_LIST);
			aoResRequest.setAttribute(HHSConstants.INVOICE_INFO, loInvoiceList);
			lsErrorMsg = HHSConstants.EMPTY_STRING;
			loModelAndView = new ModelAndView(HHSConstants.JSP_INVOICE_ASSIGN_ADVANCETABLE);
		} catch (ApplicationException aoExp) {
			if (null != lsErrorMsg) {
				loOut.print(lsErrorMsg);
			}
			LOG_OBJECT.Error("Application Exception in saveContractInvoiceRevew method", aoExp);
		} catch (Exception aoExp) {
			if (null != lsErrorMsg) {
				loOut.print(lsErrorMsg);
			}
			LOG_OBJECT.Error("Exception in saveContractInvoiceRevew method", aoExp);
		} finally {
			if (null != loOut) {
				loOut.flush();
				loOut.close();
			}
		}
		return loModelAndView;
	}

	// Updated in R7 for Cost-Center
	/**
	 * This method is used to check the ‘Invoice Status’.
	 * 
	 * <ul>
	 * <li>Validates if InvoiceStatus is "Pending Submission” or “Returned for
	 * Revision” only then validate forward</li>
	 * <li>Validates the review level</li>
	 * <li>Execute transaction id <b>invoiceAmountZeroValidation </b></li>
	 * <li>Display top level error message if no review level is set.</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest
	 *            - Resource Request Object
	 * @param aoResourceResponse
	 *            - Resource Response Object
	 * @throws ApplicationException
	 *             - Application Exception Object
	 */
	@ResourceMapping("invoiceStatusValidation")
	public void validateInvoiceStatus(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		Map<String, String> loMap = null;
		Channel loChannel = new Channel();
		Integer loErrorCode = 0;
		String lsErrorMsg = null;

		try { // Get ContractId from ResourceRequest and set in Channel
			String lsContractId = (String) aoResourceRequest.getParameter(HHSConstants.CONTRACT_ID_WORKFLOW);
			
			String startDateStr = (String) aoResourceRequest.getParameter("startDate");
			String endDateStr = (String) aoResourceRequest.getParameter("endDate");
			
			
			loChannel.setData(HHSConstants.CONTRACT_ID_KEY, lsContractId);
			// Check for invoice status
			loMap = InvoiceUtils.validateInvoiceStatus(aoResourceRequest.getParameter(HHSConstants.INVOICE_ID));
			
			LOG_OBJECT.Info("validateInvoiceStatus invoice service date from -> " + startDateStr);
			LOG_OBJECT.Info("validateInvoiceStatus invoice service date to -> " + endDateStr);
			
			if(!isDateValid(startDateStr)){
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_SERVICE_DATE_FROM);
				loErrorCode = HHSConstants.INT_ONE;
				LOG_OBJECT.Info("validateInvoiceStatus start date was invalid, lsErrorMsg -> " + lsErrorMsg);
			}else if(!isDateValid(endDateStr)){
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_SERVICE_DATE_TO);
				loErrorCode = HHSConstants.INT_ONE;
				LOG_OBJECT.Info("validateInvoiceStatus end date was invalid, lsErrorMsg -> " + lsErrorMsg);
			}else if(!isDateOneBeforeDateTwo(startDateStr, endDateStr)){
				lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_INVALID_INVOICE_DATE);
				loErrorCode = HHSConstants.INT_ONE;
				LOG_OBJECT.Info("validateInvoiceStatus end date was before start date, lsErrorMsg -> " + lsErrorMsg);
			}else if (loMap != null) {
				if (loMap.containsKey(HHSConstants.ERROR_MESSAGE)) {
					lsErrorMsg = (String) loMap.get(HHSConstants.ERROR_MESSAGE);
					loErrorCode = HHSConstants.INT_ONE;
				} else {
					loChannel.setData(HHSConstants.BUDGET_ID_KEY, (String) aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INVOICE_NEGATIVE_AMEND_CHECK);
					Boolean loValidatStatus = false;
					loValidatStatus = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
					if (loValidatStatus) {
						loChannel.setData(HHSConstants.INVOICE_ID_KEY, (String) aoResourceRequest.getParameter(HHSConstants.INVOICE_ID));
						HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INVOICE_AMOUNT_ZERO_VALIDATION);
						loValidatStatus = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
						if (loValidatStatus) {
							HHSTransactionManager.executeTransaction(loChannel, HHSConstants.INVOICE_AMOUNT_ASSIGNEMENT_VALIDATION);
							loValidatStatus = (Boolean) loChannel.getData(HHSConstants.AB_VALIDATE_STATUS);
							// Start: Added in Release 7 for Cost-Center
							HashMap loServiceValid = (HashMap) loChannel.getData(HHSConstants.AO_HASH_MAP);
							String lsIsServicesSuccess = (String) loServiceValid.get(HHSConstants.SUCCESS);
							// End: Added in Release 7 for Cost-Center
							// Following condition updated in Release 7 for
							// Cost-Center
							if (loValidatStatus && Boolean.parseBoolean(lsIsServicesSuccess)) {
								loErrorCode = HHSConstants.INT_THREE;
							} else {
								if (!loValidatStatus) {
									lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_INVOICE_EXCEED_ERR);
									loErrorCode = HHSConstants.INT_ONE;
								} else {
									lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, (String) loServiceValid.get(HHSConstants.CBL_MESSAGE));
									loErrorCode = HHSConstants.INT_ONE;
								}
							}
						} else {
							lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_INVOICE_ZERO_ERR);
							loErrorCode = HHSConstants.INT_ONE;
						}
					} else {
						lsErrorMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.MSG_KEY_INVOICE_NEGATIVE_AMENDMENT);
						loErrorCode = HHSConstants.INT_ONE;
					}
				}
			}
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, loErrorCode);
		} catch (ApplicationException aoAppExe) {
			LOG_OBJECT.Error("Exception occured in validateInvoiceStatus", aoAppExe);
			lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			loErrorCode = HHSConstants.INT_TWO;
			setErrorMsgJSP(aoResourceResponse, lsErrorMsg, loErrorCode);
		}
	}

	/**
	 * This method pre fetches vendor list to show in AutoComplete in Add
	 * Assignee page. If user enters a Vendor name in text-box that does not
	 * exist in Database, it will display error message. Else, it will show
	 * suggestions after user has entered three characters in text-box
	 * 
	 * @param aoResRequest
	 *            request
	 * @param aoResourceResponse
	 *            response
	 * @throws ApplicationException
	 *             Application Exception
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getVendorListUrl")
	public void fetchVendorList(ResourceRequest aoResRequest, ResourceResponse aoResourceResponse) throws ApplicationException {

		final String lsInputParamQuery = aoResRequest.getParameter(HHSConstants.QUERY);
		PrintWriter loPrintWriteOut = null;
		try {
			Channel loChannelData = new Channel();
			loChannelData.setData(HHSConstants.QUERY, lsInputParamQuery);

			HHSTransactionManager.executeTransaction(loChannelData, HHSConstants.FETCH_VENDOR_LIST);
			final List<AutoCompleteBean> loVendorList = (List<AutoCompleteBean>) loChannelData.getData(HHSConstants.S431_CHANNEL_VENDOR_LIST);
			if ((lsInputParamQuery != null) && (lsInputParamQuery.length() >= Integer.parseInt(HHSConstants.THREE)) && loVendorList != null) {
				loPrintWriteOut = aoResourceResponse.getWriter();
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = HHSUtil.generateDelimitedAutoCompleteResponse(loVendorList, lsInputParamQuery, Integer.parseInt(HHSConstants.THREE)).toString().trim();
				loPrintWriteOut.print(lsOutputJSONaoResponse);
				loPrintWriteOut.flush();
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx) {
			LOG_OBJECT.Error("Exception Occured while getting the Vendor list from the database", aoAppEx);

		}
		// handling exception other than Application Exception.
		catch (Exception aoException) {
			LOG_OBJECT.Error("Exception other than application exception occured while getting the Vendor list" + "from the database", aoException);
		} finally {
			if (loPrintWriteOut != null) {
				loPrintWriteOut.close();
			}
		}
	}

	/**
	 * This method is called when Add Assignee for is submitted. It validates
	 * and if assignee already exists, it shows an error message and if assignee
	 * does not exist it will add the Assignment
	 * 
	 * @param aoRequest
	 *            Resource Request
	 * @param aoResponse
	 *            Resource Response
	 * @return ModelAndView ModelAndView Object to render the View
	 * @throws ApplicationException
	 *             ApplicationException
	 * 
	 * 
	 */
	@ResourceMapping("addAssigneeSubmitUrl")
	public ModelAndView addAssigneeForBudget(ResourceRequest aoRequest, ResourceResponse aoResponse) throws ApplicationException {

		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelView = null;
		aoResponse.setContentType(HHSConstants.TEXT_HTML);

		try {
			loOut = aoResponse.getWriter();

			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannelData = new Channel();
			String lsProviderId = (String) aoRequest.getParameter(HHSConstants.PROVIDER_ID);
			String lsBudgetId = (String) aoRequest.getParameter(HHSConstants.BUDGET_ID_KEY);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

			// Set USerId in CBGridBean
			InvoiceUtils.setUserForUserType(loCBGridBean, lsUserId, lsUserOrgType);
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannelData.setData(HHSConstants.S431_CHANNEL_VENDOR, lsProviderId);
			loChannelData.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannelData.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);

			lsMsg = InvoiceUtils.addAssignee(loChannelData);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoEx) {
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException Occured while adding Assignee to the database", aoEx);

		}
		// handling exception other than Application Exception.
		catch (Exception aoEx) {
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception Occured while adding Assignee to the database", aoEx);
		} finally {
			catchTaskError(loOut, lsMsg);
		}
		return loModelView;
	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param aoOut
	 *            Print writer
	 * @param aoError
	 *            Error message
	 */
	private void catchTaskError(PrintWriter aoOut, String aoError) {
		aoOut.print(aoError);
		aoOut.flush();
		aoOut.close();
	}

	/**
	 * This method is used to display the AddAssignee Overlay <li>1.Get the
	 * required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest
	 *            Resource Request Object
	 * @param aoResourceResponse
	 *            Resource Response Object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException
	 *             Application Exception
	 */
	@ResourceMapping("addAssigneeOverlay")
	public ModelAndView addAssigneeOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		ModelAndView loModelAndView = null;
		String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);

		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, true, HHSConstants.CBGRIDBEAN_IN_SESSION);
		aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY, loCBGridBean.getContractBudgetID());

		String lsJspPath = HHSConstants.BASE_JSP_INVOICE + lsJspName;
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method is added in R7. This method will fetch the count of
	 * modification Budget
	 * 
	 * @param asBudgetId
	 * @return
	 * @throws ApplicationException
	 */
	private String fetchModificationBudgetCount(String asBudgetId) throws ApplicationException {
		String lsModificationCount = HHSConstants.ZERO;
		try {
			Channel loChannel = new Channel();
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FETCH_MODIFICATION_BUDGETS_COUNT, HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lsModificationCount = (String) loChannel.getData(HHSConstants.RETURN_STATUS);
		} catch (ApplicationException loAppEx) {
			LOG_OBJECT.Error("Error while fetching Modification budget count :", loAppEx);
			throw loAppEx;
		} catch (Exception loExp) {
			LOG_OBJECT.Error("Error while fetching Modification budget count :", loExp);
			throw new ApplicationException("Error while fetching Details :", loExp);
		}
		return lsModificationCount;
	}

	/*
	 * Start QC 9499 Multi-Tab Browsing letting Invoice and Advance tasks to be
	 * Approved Multiple times causing Duplicate Payments
	 */
	// this is to override the method in BaseController
	@ResourceMapping("finishTaskApprove")
	@HHSTokenValidator
	protected void finishTaskApprove(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		super.finishTaskApprove(aoResourceRequest, aoResourceResponse);
	}

	// this is to override the method in BaseController
	@ResourceMapping("finishTaskReturn")
	@HHSTokenValidator
	protected void finishTaskReturn(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse) throws ApplicationException {
		super.finishTaskReturn(aoResourceRequest, aoResourceResponse);
	}

	/*
	 * End QC 9499 Multi-Tab Browsing letting Invoice and Advance tasks to be
	 * Approved Multiple times causing Duplicate Payments
	 */

}
