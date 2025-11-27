/**
 * 
 */
package com.nyc.hhs.controllers;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
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
import com.nyc.hhs.model.ProcurementSummaryBean;
import com.nyc.hhs.util.HHSUtil;

/**
 * This Controller will be used to display procurement summary counts on
 * Accelerator, Agency and Provider homepage. This will include count of RFPs
 * scheduled to be released within 10 days, count of RFPs scheduled to be
 * released within 60 days, count of RFPs in released status, count of RFPs with
 * proposal due dates within 10 days, count of RFPs with proposals received,
 * count of RFPs with evaluations complete, count of RFPs with selections made
 * and submitted to accelerator
 */
@Controller(value = "homeProcurementHandler")
@RequestMapping("view")
public class HomeProcurementController implements ResourceAwareController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(HomeProcurementController.class);

	/**
	 * This method will initialize the procurement summary bean object
	 * 
	 * @return ProcurementSummaryBean Object
	 */
	@ModelAttribute("procurementSummaryBean")
	ProcurementSummaryBean getCommandObject()
	{
		return new ProcurementSummaryBean();
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
	{
		// Start Added in R5
		// added for defect 7081
		try
		{
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			if (StringUtils.isNotBlank(lsUserOrgType)
					&& lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{
				ProcurementSummaryBean loProcurementSummaryBean = new ProcurementSummaryBean();
				Channel loChannel = new Channel();
				String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
						ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
				loChannel.setData(HHSConstants.USER_ORG, lsUserOrg);
				HHSTransactionManager
						.executeTransaction(loChannel, HHSConstants.FETCH_PROCUREMENT_COUNT_PROV_HOME_PAGE);
				ProcurementSummaryBean loTmpProcurementSummaryBean = (ProcurementSummaryBean) loChannel
						.getData(HHSConstants.PROC_SUMMARY_BEAN);
				loProcurementSummaryBean.setProposalReturnRevision(loTmpProcurementSummaryBean
						.getProposalReturnRevision());
				aoRequest.setAttribute(HHSConstants.PROC_SUMMARY_LOWERCASE, loProcurementSummaryBean);
			}
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching counts for Procurement Portlet Homepage", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_MESSAGES_PROPERTY_FILE);
		}
		// End Added in R5
		return HHSConstants.PROC_SUMMARY_LOWERCASE;
	}

	/**
	 * This method will handle all the resource request when refresh icon is
	 * clicked on procurement portlet for Accelerator, Agency and Provider users
	 * 
	 * <ul>
	 * <li>Get the Organization type from session variable</li>
	 * <li>Set the User's Organization name in channel object from session
	 * variable</li>
	 * <li>For any type of organization get the transaction name and executing
	 * the transaction on the basis of transaction name</li>
	 * </ul>
	 * 
	 * @param aoRequest an Resource Request object
	 * @param aoResponse an Resource Response Object
	 * @return model and view object containing view name
	 */
	@ResourceMapping("procurementSummaryPortlet")
	@Override
	public ModelAndView handleResourceRequest(ResourceRequest aoRequest, ResourceResponse aoResponse)
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		try
		{
			Channel loChannel = new Channel();
			String lsUserOrgType = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE, PortletSession.APPLICATION_SCOPE);
			String lsUserOrg = (String) aoRequest.getPortletSession().getAttribute(
					ApplicationConstants.KEY_SESSION_USER_ORG, PortletSession.APPLICATION_SCOPE);
			String lsTransactionname = HHSUtil.homeProcurementTransaction(lsUserOrgType);
			if (null != lsUserOrgType
					&& (lsUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG) || lsUserOrgType
							.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG)))
			{
				loChannel.setData(HHSConstants.USER_ORG, lsUserOrg);
			}
			HHSTransactionManager.executeTransaction(loChannel, lsTransactionname);
			loProcurementSummaryBean = (ProcurementSummaryBean) loChannel.getData(HHSConstants.PROC_SUMMARY_BEAN);
			aoRequest.setAttribute(HHSConstants.PROC_SUMMARY_LOWERCASE, loProcurementSummaryBean);
			aoRequest.setAttribute(HHSConstants.VISIBILITY_FLAG, true);
		}
		// handling Application Exception while fetching counts for Procurement
		// Portlet.
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching counts for Procurement Portlet Homepage", aoEx);
			aoRequest.setAttribute(ApplicationConstants.ERROR_MESSAGE, HHSConstants.ERROR_MESSAGES_PROPERTY_FILE);
		}
		return new ModelAndView(HHSConstants.PROC_SUMMARY_LOWERCASE);
	}
}
