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

public class ContractBudgetModificationAccessHandler extends BaseAccessHandler
{

	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		String lsNextJSP = null;
		TaskDetailsBean loTaskDetailsBean = null;
		String lsContractId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CONTRACT_ID_WORKFLOW);
		String lsBudgetId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_ID_WORKFLOW);
		String lsBudgetType = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.BUDGET_TYPE);
		String lsFiscalYearId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.CLC_FISCAL_YEAR_ID);

		CBGridBean loCBGridBean = (CBGridBean) PortletSessionHandler.getAttribute(aoRequest, true,
				HHSConstants.CBGRIDBEAN_IN_SESSION);

		if (null != loCBGridBean)
		{
			lsBudgetId = loCBGridBean.getContractBudgetID();
			lsContractId = loCBGridBean.getContractID();
			lsBudgetType = loCBGridBean.getBudgetTypeId();
			lsFiscalYearId = loCBGridBean.getFiscalYearID();
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
				+ lsBudgetId + HHSConstants.UNDERSCORE + lsBudgetType + HHSConstants.UNDERSCORE + lsFiscalYearId;
		// added for r5
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsBudgetId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME,
				HHSConstants.BUDGET_ENTITY_TYPE_MAP.get(lsBudgetType), PortletSession.APPLICATION_SCOPE);
		// added for r5
		return lsReturnValueId;
	}

}
