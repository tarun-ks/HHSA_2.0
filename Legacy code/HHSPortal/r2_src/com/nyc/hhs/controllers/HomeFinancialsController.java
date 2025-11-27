/**
 * 
 */
package com.nyc.hhs.controllers;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.mvc.ResourceAwareController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.FinancialSummaryBean;
import com.nyc.hhs.util.HHSUtil;

/**
 * This Controller will be used to display financials summary counts on
 * Accelerator, Agency and Provider homepage. This will include count of count
 * of Contracts Pending Configuration, count of Contracts Pending Certification
 * of Funds, count of Contracts Pending Registration, count of Budgets and
 * Amendment Budgets pending Approval, count of Budget Modifications Pending
 * Approval, count of Invoices Pending Approval, count of Payments Pending
 * Approval, count of Payments with FMS error
 */
@Controller(value = "homeFinancialHandler")
@RequestMapping("view")
public class HomeFinancialsController extends BaseController implements ResourceAwareController
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HomeFinancialsController.class);

	/**
	 * This method will initialize the financial summary bean object
	 * 
	 * @return FinancialSummaryBean Object
	 */
	@ModelAttribute("financialSummaryBean")
	FinancialSummaryBean getCommandObject()
	{
		return new FinancialSummaryBean();
	}

	/**
	 * This method will be executed when the page will be loaded first time and
	 * then for rendering the actions
	 * @param aoRequest an Render Request object
	 * @param aoResponse an Render Response object
	 * @return a string value of view name
	 */
	@RenderMapping
	protected String handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		return HHSConstants.FINANCIAL_SUMMARY;
	}

	/**
	 * This method will handle all the resource request when refresh icon is
	 * clicked on procurement portlet for Accelerator, Agency and Provider users
	 * 
	 * <ul>
	 * <li>Get the Organization type from session variable</li>
	 * Set the User's Organization name in channel object from session variable
	 * <li>For any type of organization get the transaction name and executing
	 * the transaction on the basis of transaction name</li>
	 * </ul>
	 * @param aoRequest an Resource Request object
	 * @param aoResponse an Resource Response Object
	 * @return ModelAndView containing view name
	 */
	@ResourceMapping("financialSummaryPortlet")
	@Override
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		FinancialSummaryBean loFinancialSummaryBean = null;
		try
		{
			Channel loChannel = new Channel();
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			if (null != lsUserOrgType
					&& (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG) || lsUserOrgType
							.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
			{
				loChannel.setData(HHSConstants.USER_ORG, lsUserOrg);
			}
			String lsTransactionname = HHSUtil.homeFinancialTransaction(lsUserOrgType);
			HHSTransactionManager.executeTransaction(loChannel, lsTransactionname);
			loFinancialSummaryBean = (FinancialSummaryBean) loChannel.getData(HHSConstants.FINANCIAL_SUMMARY_BEAN);
			aoRequest.setAttribute(HHSConstants.FINANCIAL_SUMMARY, loFinancialSummaryBean);
		}
		// handling Application Exception while fetching counts for Financial
		// Portlet.
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching counts for Financial Portlet Homepage", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_WHILE_PROCESSING_REQUEST);
		}
		return new ModelAndView(HHSConstants.FINANCIAL_SUMMARY);
	}

}
