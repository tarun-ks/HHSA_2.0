package com.nyc.hhs.controllers;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.PaymentBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;

/**
 * <p>
 * This controller class will serve as a controller for advance payment details
 * and advance payment review task detail screens. Payment details, Line details
 * and chart of allocation grid data for both screens will be shown using this
 * controller only.
 * 
 * All the actions on Advance Payment Details and Advance Payment Review Task
 * Details screens will be handled by this controller only.
 * </p>
 */
@Controller(value = "advancePaymentDetailHandler")
@RequestMapping("view")
public class AdvancePaymentReviewController extends BaseController
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AdvancePaymentReviewController.class);

	/**
	 * This method will be executed when Advance payment details and Advance
	 * payment review task details screens will be loaded for first time and it
	 * will call service to fetch all details for advance payment summary
	 * screen.
	 * 
	 * 
	 * @param aoRequest an Action Request object
	 * @param aoResponse an Action Response object
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 * @throws ApplicationException an application exception
	 */
	@SuppressWarnings("unchecked")
	@RenderMapping
	protected ModelAndView mainRenderMethod(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		ModelAndView loModelAndView = null;
		Channel loChannelObj = new Channel();
		String lsJspPath = HHSConstants.JSP_ADVANCE_PAYMENT_DETAILS;
		String lsTransactionName = HHSConstants.FETCH_ADVANCE_PAYMENT_DETAILS;
		String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		String lsBudgetAdvanceId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW);
		String lsPaymentId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PAYMENT_ID_WORKFLOW);
		PortletSession loSession = null;
		String lsFiscalYearId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_FISCAL_YEAR_ID);

		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		String lsWorkflowId = PortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);

		try
		{
			loSession = aoRequest.getPortletSession();
			P8UserSession loUserSession = (P8UserSession) loSession.getAttribute(
					ApplicationConstants.FILENET_SESSION_OBJECT, PortletSession.APPLICATION_SCOPE);
			loTaskDetailsBean.setP8UserSession(loUserSession);

			if (null != lsWorkflowId)
			{
				lsJspPath = HHSConstants.JSP_ADVANCE_PAYMENT_REVIEW_TASK;
				lsTransactionName = HHSConstants.FETCH_ADVANCE_PAYMENT_REVIEW_DETAILS;
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				// information for Task like Contract Id,Procurement Id etc.
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
				lsBudgetAdvanceId = loTaskDetailsBean.getBudgetAdvanceId();
			}

			HashMap<String, String> loHashMap = new HashMap<String, String>();
			// values are hard-code will get from BudgetList screen
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, lsBudgetAdvanceId);
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);

			aoRequest.setAttribute(HHSConstants.AO_HASH_MAP, loHashMap);
			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);

			// Start transaction
			HHSTransactionManager.executeTransaction(loChannelObj, lsTransactionName);

			PaymentBean loPaymentBean = (PaymentBean) loChannelObj.getData(HHSConstants.LO_PAYMENT_BEAN);
			aoRequest.setAttribute(HHSConstants.PAYMENT_HEADER_DETAIL, loPaymentBean);

			if (null != lsWorkflowId)
			{
				List<PaymentBean> loPaymentVoucher = (List<PaymentBean>) loChannelObj
						.getData(HHSConstants.LO_PAYMENT_VOUCHER);
				aoRequest.setAttribute(HHSConstants.PAYMENT_VOUCHER_LIST, loPaymentVoucher);
			}

			if (null != loPaymentBean)
			{
				lsPaymentId = loPaymentBean.getPaymentId();
				loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			}

			loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ADVANCE_PAYMENT_LINE_DETAILS);
			BudgetDetails loPaymentBudgetDetails = (BudgetDetails) loChannelObj
					.getData(HHSConstants.LO_PAYMENT_BUDGET_DETAILS);
			aoRequest.setAttribute(HHSConstants.PAYMENT_LINE_DETAIL, loPaymentBudgetDetails);

			// End transaction

			addBudgetAttributeToPayment(aoRequest, lsContractId, lsBudgetId, lsBudgetAdvanceId, lsPaymentId,
					lsFiscalYearId);
		}
		// Application Exception handled here
		catch (ApplicationException loExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			LOG_OBJECT.Error("Error occured while processing advance payment review", loExp);
			lsJspPath = getJspPath(loExp, loSession);
		}
		catch (Exception loExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			LOG_OBJECT.Error("Error occured while processing advance payment review", loExp);
			lsJspPath = getJspPath(new ApplicationException("Exception in Advance Payment Review", loExp), loSession);
		}
		// Set TaskDetail Bean in session Required for comment and History
		// sections
		loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
				PortletSession.APPLICATION_SCOPE);
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method adds the budget attribute for payment
	 * @param aoRequest
	 * @param asContractId
	 * @param asBudgetId
	 * @param asBudgetAdvanceId
	 * @param asPaymentId
	 * @param asFiscalYearId
	 */
	private void addBudgetAttributeToPayment(RenderRequest aoRequest, String asContractId, String asBudgetId,
			String asBudgetAdvanceId, String asPaymentId, String asFiscalYearId)
	{
		String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);

		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setContractID(asContractId);
		loCBGridBean.setContractBudgetID(asBudgetId);
		loCBGridBean.setBudgetAdvanceId(asBudgetAdvanceId);
		loCBGridBean.setBudgetTypeId(HHSConstants.EMPTY_STRING);
		loCBGridBean.setSubBudgetID(asPaymentId); // Here we are using
													// subbudget attribute
													// for payment Id
													// purpose
		if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsUserOrgType))
		{
			loCBGridBean.setModifyByProvider(lsUserId);
		}
		else
		{
			loCBGridBean.setModifyByAgency(lsUserId);
		}

		if (null != asFiscalYearId)
		{
			loCBGridBean.setFiscalYearID(asFiscalYearId);
		}
		// Start R5: set ContractId
		aoRequest.setAttribute(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
		aoRequest.setAttribute(HHSConstants.BUDGET_ID_WORKFLOW, asBudgetId);
		// End R5: set ContractId
		aoRequest.setAttribute(HHSConstants.PAYMENT_ID_WORKFLOW, asPaymentId);
		PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

		// Get errors messages after Submit CB confirmation transactions
		String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
		aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
	}

	/**
	 * This method will be return the error jsp page provide the logger error,
	 * and set error message
	 * 
	 * @param aoExp exception object
	 * @param aoSession PortletSession object
	 * @return lsJspPath error jsp page
	 */
	private String getJspPath(ApplicationException aoExp, PortletSession aoSession)
	{
		LOG_OBJECT.Error("Error occured while processing budgets", aoExp);
		String lsJspPath = HHSConstants.ERROR_PAGE_JSP;
		return lsJspPath;
	}

	/**
	 * <ul>
	 * <li>This method is triggered on change of payment voucher number on
	 * advance payment review task details screen</li>
	 * <li>Transaction Id : fetchAdvancePaymentLineDetails</li>
	 * 
	 * <li>Service Class : PaymentModuleService</li>
	 * </ul>
	 * 
	 * @param aoResourceRequest ResourceRequest
	 * @param aoResourceResponse ResourceResponse
	 * @return ModelAndView containing details of the page to be displayed to
	 *         the end user
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@ResourceMapping("refreshPaymentDetail")
	public ModelAndView refreshPaymentDetail(ResourceRequest aoResourceRequest, ResourceResponse aoResourceResponse)
	{

		ModelAndView loModelAndView = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsPaymentId = aoResourceRequest.getParameter(HHSConstants.PAYMENT_ID_WORKFLOW);
		PrintWriter loOut = null;
		boolean loTrueValue = true;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loOut = aoResourceResponse.getWriter();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, loTrueValue,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannel = new Channel();
			Map loInputMap = new HashMap();
			loInputMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, loCBGridBean.getContractID());
			loInputMap.put(HHSConstants.BUDGET_ID_WORKFLOW, loCBGridBean.getContractBudgetID());
			loInputMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, loCBGridBean.getBudgetAdvanceId());
			loInputMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			aoResourceRequest.setAttribute(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loInputMap);
			// Executing transaction to fetch payment Line details
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_ADVANCE_PAYMENT_LINE_DETAILS);

			BudgetDetails loPaymentBudgetDetails = (BudgetDetails) loChannel
					.getData(HHSConstants.LO_PAYMENT_BUDGET_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.PAYMENT_LINE_DETAIL, loPaymentBudgetDetails);

			lsErrorMsg = HHSConstants.EMPTY_STRING;
			loModelAndView = new ModelAndView(HHSConstants.JSP_ADVANCE_PAYMENT_LINE_DETAILS);
		}
		// Application Exception handled here
		catch (ApplicationException aoExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			loOut.print(lsErrorMsg);
			LOG_OBJECT.Error("Application Exception in fetchAdvancePaymentLineDetails method", aoExp);
		}
		// Exception handled here
		catch (Exception aoExp)
		{
			// Log is generated in case of any Error and Error message is set
			// for JSP
			if (null != loOut)
			{
				loOut.print(lsErrorMsg);
			}
			LOG_OBJECT.Error("Exception in fetchAdvancePaymentLineDetails method", aoExp);
		}
		finally
		{
			if (null != loOut)
			{
				loOut.flush();
				loOut.close();
			}
		}
		return loModelAndView;
	}

}
