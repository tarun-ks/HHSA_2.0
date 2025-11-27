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
import com.nyc.hhs.constants.HHSR5Constants;
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
 * This controller class will serve as a controller for payment details and
 * payment review task detail screens. Payment details, Line details and chart
 * of allocation grid data for both screens will be shown using this controller
 * only.
 * 
 * All the actions on Payment Details and Payment Review Task Details screens
 * will be handled by this controller only.
 * </p>
 */
@Controller(value = "paymentDetailHandler")
@RequestMapping("view")
public class PaymentReviewController extends BaseController
{

	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentReviewController.class);

	/**
	 * This method will be executed when payment details and payment review task
	 * details screens will be loaded for first time and it will call service to
	 * fetch all details for payment summary screen.
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
		String lsJspPath = HHSConstants.JSP_PAYMENT_DETAILS;
		String lsTransactionName = HHSConstants.FETCH_PAYMENT_DETAILS;
		String lsContractId = PortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = PortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		String lsInvoiceId = PortalUtil.parseQueryString(aoRequest, HHSConstants.INVOICE_ID);
		String lsPaymentId = PortalUtil.parseQueryString(aoRequest, HHSConstants.PAYMENT_ID_WORKFLOW);
		/* Start : R5 Added */
		aoRequest.setAttribute(HHSR5Constants.CONTRACT_ID_WORKFLOW, lsContractId);
		aoRequest.setAttribute(HHSR5Constants.BUDGET_ID_WORKFLOW, lsBudgetId);
		/* End : R5 Added */
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
				lsJspPath = HHSConstants.JSP_PAYMENT_REVIEW_TASK_DETAILS;
				lsTransactionName = HHSConstants.FETCH_PAYMENT_REVIEW_DETAILS;
				loTaskDetailsBean = fetchTaskDetailsFromFilenet(aoRequest, lsWorkflowId);
				// loTaskDetailsBean object will contains all required
				// information for Task like Contract Id,Procurement Id etc.
				lsContractId = loTaskDetailsBean.getContractId();
				lsBudgetId = loTaskDetailsBean.getBudgetId();
				lsInvoiceId = loTaskDetailsBean.getInvoiceId();
			}

			HashMap<String, String> loHashMap = new HashMap<String, String>();
			// values are hard-code will get from BudgetList screen
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, lsContractId);
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, lsBudgetId);
			loHashMap.put(HHSConstants.INVOICE_ID, lsInvoiceId);
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
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_PAYMENT_LINE_DETAILS);
			BudgetDetails loPaymentBudgetDetails = (BudgetDetails) loChannelObj
					.getData(HHSConstants.LO_PAYMENT_BUDGET_DETAILS);
			aoRequest.setAttribute(HHSConstants.PAYMENT_LINE_DETAIL, loPaymentBudgetDetails);

			// End transaction

			String lsUserOrgType = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,
					PortletSession.APPLICATION_SCOPE);
			String lsUserId = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
					PortletSession.APPLICATION_SCOPE);

			CBGridBean loCBGridBean = new CBGridBean();
			loCBGridBean.setContractID(lsContractId);
			loCBGridBean.setContractBudgetID(lsBudgetId);
			loCBGridBean.setInvoiceId(lsInvoiceId);
			loCBGridBean.setBudgetTypeId(HHSConstants.EMPTY_STRING);
			loCBGridBean.setSubBudgetID(lsPaymentId); // Here we are using
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

			if (null != lsFiscalYearId)
			{
				loCBGridBean.setFiscalYearID(lsFiscalYearId);
			}
			aoRequest.setAttribute(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			PortletSessionHandler.setAttribute(loCBGridBean, aoRequest, HHSConstants.CBGRIDBEAN_IN_SESSION);

			// Get errors messages after Submit CB confirmation transactions
			String lsErrorMsg = (String) ApplicationSession.getAttribute(aoRequest, HHSConstants.ERROR_MESSAGE);
			aoRequest.setAttribute(HHSConstants.ERROR_MESSAGE, lsErrorMsg);
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException loExp)
		{
			lsJspPath = getJspPath(loExp, loSession);
			LOG_OBJECT.Error("Exception : ", loExp);
		}
		// Exception catch other than application exception
		catch (Exception loExp)
		{
			lsJspPath = getJspPath(new ApplicationException("Exception in Payment Review", loExp), loSession);
			LOG_OBJECT.Error("Exception : ", loExp);
		}
		loSession.setAttribute(HHSConstants.TASK_DETAIL_BEAN_SESSION, loTaskDetailsBean,
				PortletSession.APPLICATION_SCOPE);
		loModelAndView = new ModelAndView(lsJspPath);
		return loModelAndView;
	}

	/**
	 * This method will be return the error jsp page provide the logger error,
	 * and set error message
	 * 
	 * @param aoExp exception object
	 * @param aoSession PortletSession object
	 * @return lsJspPagePath error jsp page
	 */
	private String getJspPath(ApplicationException aoExp, PortletSession aoSession)
	{
		LOG_OBJECT.Error("Error occured while processing budgets", aoExp);
		String lsJspPagePath = HHSConstants.ERROR_PAGE_JSP;
		return lsJspPagePath;
	}

	/**
	 * <ul>
	 * <li>This method is triggered on change of payment voucher number on
	 * payment review task details screen</li>
	 * <li>Transaction Id : fetchPaymentLineDetails</li>
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

		ModelAndView loModelView = null;
		String lsErrorMsg = HHSConstants.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsPaymentId = aoResourceRequest.getParameter(HHSConstants.PAYMENT_ID);
		PrintWriter loPrintWriterOut = null;
		try
		{
			aoResourceResponse.setContentType(HHSConstants.TEXT_HTML);
			loPrintWriterOut = aoResourceResponse.getWriter();
			CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoResourceRequest, Boolean.TRUE,
					HHSConstants.CBGRIDBEAN_IN_SESSION);
			Channel loChannel = new Channel();
			Map loHMInput = new HashMap();
			loHMInput.put(HHSConstants.CONTRACT_ID_WORKFLOW, loCBGridBean.getContractID());
			loHMInput.put(HHSConstants.BUDGET_ID_WORKFLOW, loCBGridBean.getContractBudgetID());
			loHMInput.put(HHSConstants.INVOICE_ID, loCBGridBean.getInvoiceId());
			loHMInput.put(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			aoResourceRequest.setAttribute(HHSConstants.PAYMENT_ID_WORKFLOW, lsPaymentId);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHMInput);
			// Executing transaction to fetch payment Line details
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PAYMENT_LINE_DETAILS);

			BudgetDetails loPaymentBudgetDetails = (BudgetDetails) loChannel
					.getData(HHSConstants.LO_PAYMENT_BUDGET_DETAILS);
			aoResourceRequest.setAttribute(HHSConstants.PAYMENT_LINE_DETAIL, loPaymentBudgetDetails);

			lsErrorMsg = HHSConstants.EMPTY_STRING;
			loModelView = new ModelAndView(HHSConstants.JSP_PAYMENT_VOUCHER_LINE_DETAILS);
		}
		// Exception occur if database is down or getting null in required
		// parameter
		catch (ApplicationException aoExp)
		{
			if (null != lsErrorMsg)
			{
				loPrintWriterOut.print(lsErrorMsg);
			}
			LOG_OBJECT.Error("Application Exception in saveContractInvoiceRevew method", aoExp);
		}
		// Exception catch other than application exception
		catch (Exception aoExp)
		{
			if (null != lsErrorMsg)
			{
				loPrintWriterOut.print(lsErrorMsg);
			}
			LOG_OBJECT.Error("Exception in saveContractInvoiceRevew method", aoExp);
		}
		finally
		{
			if (null != loPrintWriterOut)
			{
				loPrintWriterOut.flush();
				loPrintWriterOut.close();
			}
		}
		return loModelView;
	}

}
