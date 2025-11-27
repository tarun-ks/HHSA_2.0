package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.springframework.web.portlet.ModelAndView;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.HHSPortalUtil;
import com.nyc.hhs.util.PortletSessionHandler;

public class ContractBudgetAccessHandler extends BaseAccessHandler
{
	/**
	 * This method will generate Handler Id for Contract Budget Access
	 * This method will fetch taskDetailBean from request Object
	 * and corresponding Contact Id and Budget Id
	 * 
	 * @param aoRet
	 * @param aoRequest
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		String lsBudgetId = HHSConstants.EMPTY_STRING;
		String lsContractId = HHSConstants.EMPTY_STRING;
		TaskDetailsBean loTaskDetailsBean = null;
		String lsNextJSP = null;
		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);

		if (null != loCBGridBean)
		{
			lsBudgetId = loCBGridBean.getContractBudgetID();
			lsContractId = loCBGridBean.getContractID();
		}
		else
		{
			lsBudgetId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
			lsContractId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		}
		String lsWorkflowId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		if (null != lsWorkflowId)
		{
			loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			lsContractId = loTaskDetailsBean.getContractId();
			lsBudgetId = loTaskDetailsBean.getBudgetId();
		}
		if (aoRet instanceof String)
		{
			lsNextJSP = (String) aoRet;
		}
		else if (aoRet instanceof ModelAndView)
		{
			lsNextJSP = ((ModelAndView) aoRet).getViewName();
		}
		if (lsNextJSP != null
				&& (lsNextJSP.contains(HHSConstants.FORWARD_SLASH) || lsNextJSP.contains(HHSConstants.STRING_BACKSLASH)))
		{
			lsNextJSP = lsNextJSP.replace(HHSConstants.STRING_BACKSLASH, HHSConstants.FORWARD_SLASH);
			lsNextJSP = lsNextJSP.substring(lsNextJSP.lastIndexOf(HHSConstants.FORWARD_SLASH) + 1);
		}
		String lsReturnValueId = ApplicationConstants.LOCK_ID_START + HHSR5Constants.CONTRACT_BUDGET + lsContractId + HHSConstants.UNDERSCORE
				+ lsBudgetId + lsNextJSP + HHSR5Constants._JSP;
		return lsReturnValueId;
	}

}
