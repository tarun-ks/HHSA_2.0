package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.annotation.HHSExtToken;
import com.nyc.hhs.annotation.HHSTokenValidator;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.controllers.util.InvoiceUtils;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;
import com.nyc.hhs.util.PropertyLoader;

/**
 * <p>
 * This controller class will serve as a controller for Advance Payment Request
 * Task screens. Advance Payment Request task details will be shown using this
 * Controller. It handles Advance grid, Assignment Grid and Add Assignee
 * operations.
 * 
 * </p>
 */

public class AdvancePaymentController extends BaseController implements ResourceAwareController
{
	/**
	 * LogInfo object for Logging
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AdvancePaymentController.class);

	/**
	 * This method will be executed when screen will be loaded for first time
	 * and it will call service to fetch all details for Invoice screen.
	 * 
	 * <ul>
	 * <li>Transaction manager will be called to call the fetchInvoiceSummary
	 * method of InvoiceService class which will fetch all details for Invoice
	 * summary screen.</li>
	 * </ul>
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@RenderMapping
	/* Start QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/
	@HHSExtToken
	/* End QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		CBGridBean loCBGridBean = null;
		PortletSession loSession = null;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		// values are hard-code will get from BudgetList screen
		String lsBudgetAdvanceId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ADVANCE_ID);
		String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		String lsJspPath = HHSConstants.JSP_ADVANCE_PAYMENT_REQUEST_TASK;
		try
		{
			loSession = aoRequest.getPortletSession();
			String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
			if (null != lsWorkflowId)
			{
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				lsBudgetAdvanceId = loTaskDetailsBean.getBudgetAdvanceId();
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
			}
			loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID, lsBudgetAdvanceId);
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);

			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_CONTRACT_INFO_FOR_PAYMENT);
			ContractList loContractList = (ContractList) loChannelObj.getData(HHSConstants.LO_CONTRACT_LIST);
			// Set BudgetId and ContractId in loContractList
			loContractList.setBudgetId(lsBudgetId);
			loContractList.setContractId(lsContractId);
			// Set ContractInfo in session
			aoRequest.setAttribute(HHSConstants.CONTRACT_INFO, loContractList);

			loTaskDetailsBean.setBudgetId(loContractList.getBudgetId());
			loTaskDetailsBean.setContractId(loContractList.getContractId());
			loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
			BudgetDetails loBudgetDetails = (BudgetDetails) loChannelObj.getData(HHSConstants.LO_BUDGET_DETAILS);
			aoRequest.setAttribute(HHSConstants.FISCAL_BUDGET_INFO, loBudgetDetails);
			/*Change for R5 starts*/
			aoRequest.setAttribute(HHSConstants.BUDGET_ADVANCE_ID, HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ADVANCE_ID));
			/*Change for R5 ends*/
			List<CBGridBean> loSubBudgetList = (List<CBGridBean>) loChannelObj.getData(HHSConstants.SUB_BUDGET_LIST);
			HHSUtil.changeSubBudgetNameForHTMLView(loSubBudgetList);
			loSession.setAttribute(HHSConstants.BUDGET_ACCORDIAN_DATA, loSubBudgetList,
					PortletSession.APPLICATION_SCOPE);
			loCBGridBean = (CBGridBean) loChannelObj.getData(HHSConstants.LO_CB_GRID_BEAN);
			// Set Budget AdvanceId
			loCBGridBean.setBudgetAdvanceId(lsBudgetAdvanceId);
			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);

			// SET USERID in CBGridBean
			InvoiceUtils.setUserForUserType(loCBGridBean, lsUserId, lsUserOrgType);
			PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

			aoRequest.setAttribute(HHSConstants.CHANNEL_ACCESS, loChannelObj);

			// This method will fetch the document list for this screen
			generateDocumentSection(aoRequest);
			// Get errors messages after Submit confirmation transactions
			String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
			// SET TRANSACTION RESULT STATUS AND MESSAGE
			setResultStatusAndMessage(aoRequest);

		}
		// Application Exception handled here
		catch (ApplicationException aoAppEx)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT.Error("Error occured while processing Advance Payment Request", aoAppEx);
		}
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			lsJspPath = HHSConstants.ERROR_PAGE_JSP;
			LOG_OBJECT.Error("Error occured while processing Advance Payment Request", aoExp);
		}

		loModelAndView = new ModelAndView(lsJspPath);
		// Set TaskDetail Bean in session Required for comment and History
		// sections
		loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
				PortletSession.APPLICATION_SCOPE);

		return loModelAndView;
	}

	/**
	 * <ul>
	 * <li>This method sets Transaction result status and Error message in
	 * Render Request</li>
	 * </ul>
	 * @param aoRenderRequest RenderRequest
	 * @throws ApplicationException Application Exception
	 * 
	 */
	private void setResultStatusAndMessage(RenderRequest aoRenderRequest) throws ApplicationException
	{
		try
		{
			if (PortalUtil.parseQueryString(aoRenderRequest, HHSConstants.TRANSACTION_RSLT_STATUS) != null)
			{
				String lsTransStatus = PortalUtil.parseQueryString(aoRenderRequest,
						HHSConstants.TRANSACTION_RSLT_STATUS);
				// For transactionStatus = SUCCESS
				if (lsTransStatus.equalsIgnoreCase(HHSConstants.SUCCESS))
				{
					// Set Message
					aoRenderRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.SUCCESS);
					aoRenderRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.CONTRACT_INVOICE_SUBMITTED));
					aoRenderRequest.setAttribute(HHSConstants.CONTRACT_BUDGET_READ_ONLY, HHSConstants.TRUE);
				}
				else
				{
					// For transactionStatus=Failure
					aoRenderRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_STATUS, HHSConstants.FAIL_FLAG);
					aoRenderRequest.setAttribute(HHSConstants.TRANSACTION_RSLT_MSG, PropertyLoader.getProperty(
							HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.REQUEST_NOT_COMPLETED));
				}
			}
		}
		// Application exception handled here
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured in setResultStatusAndMessage while processing Advance Payment", aoAppEx);
			throw aoAppEx;

		}
	}

	/**
	 * This method is used to display the AddAssignee Overlay <li>1.Get the
	 * required view name from request</li>
	 * <ul>
	 * 
	 * @param aoResourceRequest Resource Request Object
	 * @param aoResourceResponse Resource Response Object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException Application Exception
	 */
	@ResourceMapping("addPaymentAssigneeOverlay")
	public ModelAndView addAssigneeOverlay(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		try
		{

			String lsJspName = aoResourceRequest.getParameter(HHSConstants.JSP_NAME);

			aoResourceRequest.setAttribute(HHSConstants.BUDGET_ID_KEY,
					aoResourceRequest.getParameter(HHSConstants.BUDGET_ID_WORKFLOW));

			String lsJspPath = HHSConstants.JSP_PAYMENT + lsJspName;
			loModelAndView = new ModelAndView(lsJspPath);

		}
		// Exception handled here
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while processing Advance Payment Request - addPaymentAssigneeOverlay",
					aoExp);
		}
		return loModelAndView;
	}

	/**
	 * This method pre fetches vendor list to show in AutoComplete in Add
	 * Assignee page. If user enters a Vendor name in text-box that does not
	 * exist in Database, it will display error message. Else, it will show
	 * suggestions after user has entered three characters in text-box
	 * 
	 * @param aoResourceRequest request
	 * @param aoResourceResponse response
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("unchecked")
	@ResourceMapping("getPaymentVendorListUrl")
	public void fetchVendorList(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{

		final String lsInputParam = aoResourceRequest.getParameter(HHSConstants.QUERY);
		PrintWriter loOut = null;
		try
		{
			Channel loChannel = new Channel();
			if (null != lsInputParam)
			{
				loChannel.setData(HHSConstants.QUERY, lsInputParam.trim());
			}
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_VENDOR_LIST);
			final List<AutoCompleteBean> loVendorList = (List<AutoCompleteBean>) loChannel
					.getData(HHSConstants.S431_CHANNEL_VENDOR_LIST);
			if ((lsInputParam != null) && (lsInputParam.length() >= Integer.parseInt(HHSConstants.THREE))
					&& (null != loVendorList))
			{
				loOut = aoResourceResponse.getWriter();
				aoResourceResponse.setContentType(HHSConstants.APPLICATION_JSON);
				final String lsOutputJSONaoResponse = HHSUtil
						.generateDelimitedAutoCompleteResponse(loVendorList, lsInputParam,
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
			LOG_OBJECT.Error("Exception Occured while getting the Vendor list from the database", aoAppEx);

		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Exception other than application exception occured while getting the Vendor list"
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
	 * This method is called when Add Assignee for is submitted. It validates
	 * and if assignee already exists, it shows an error message and if assignee
	 * does not exist it will add the Assignment
	 * 
	 * @param aoRequest Resource Request
	 * @param aoResponse Resource Response
	 * @return ModelAndView ModelAndView Object to render the View
	 * @throws ApplicationException ApplicationException
	 * 
	 */
	@ResourceMapping("addPaymentAssigneeSubmitUrl")
	public ModelAndView addAssigneeForBudget(ResourceRequest aoRequest, ResourceResponse aoResponse)
			throws ApplicationException
	{

		String lsMsg = HHSConstants.EMPTY_STRING;
		PrintWriter loOut = null;
		ModelAndView loModelAndView = null;
		aoResponse.setContentType(HHSConstants.TEXT_HTML);

		try
		{
			loOut = aoResponse.getWriter();

			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, Boolean.TRUE,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannel = new Channel();
			String lsVendorId = (String) aoRequest.getParameter(HHSConstants.PROVIDER_ID);
			String lsBudgetId = (String) aoRequest.getParameter(HHSConstants.BUDGET_ID_KEY);
			String lsUserId = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);

			// Set USerId in CBGridBean
			InvoiceUtils.setUserForUserType(loCBGridBean, lsUserId, lsUserOrgType);
			// Set vendorId, BudgetId and CBGridBean in Channel
			loChannel.setData(HHSConstants.S431_CHANNEL_VENDOR, lsVendorId);
			loChannel.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannel.setData(HHSConstants.LO_CB_GRID_BEAN, loCBGridBean);

			lsMsg = InvoiceUtils.addAssignee(loChannel);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoException)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("ApplicationException Occured while adding Assignee to the database", aoException);

		}
		// handling exception other than Application Exception.
		catch (Exception aoException)
		{
			lsMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
			LOG_OBJECT.Error("Exception Occured while adding Assignee to the database", aoException);
		}
		finally
		{
			catchTaskError(loOut, lsMsg);
		}
		return loModelAndView;
	}

	/**
	 * This method print the error using print Writer object
	 * 
	 * @param aoOut Print writer
	 * @param aoError Error message
	 */
	private void catchTaskError(PrintWriter aoOut, String aoError)
	{
		aoOut.print(aoError);
		aoOut.flush();
		aoOut.close();
	}
	
	/* Start QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/
	//this is to override the method in BaseController
	@ResourceMapping("finishTaskApprove")
	@HHSTokenValidator
	protected void finishTaskApprove(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		super.finishTaskApprove(aoResourceRequest, aoResourceResponse);
	}
	
	//this is to override the method in BaseController
	@ResourceMapping("finishTaskReturn")
	@HHSTokenValidator
	protected void finishTaskReturn(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
			throws ApplicationException
	{
		super.finishTaskReturn(aoResourceRequest, aoResourceResponse);
	}
	
	/* End QC 9499  Multi-Tab Browsing letting Invoice and Advance tasks to be Approved Multiple times causing Duplicate Payments*/



}
