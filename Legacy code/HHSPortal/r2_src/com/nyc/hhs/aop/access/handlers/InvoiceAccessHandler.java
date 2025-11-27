package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.springframework.web.portlet.ModelAndView;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.HHSPortalUtil;

public class InvoiceAccessHandler extends BaseAccessHandler
{

	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		TaskDetailsBean loTaskDetailsBean = null;
		String lsInvoiceId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.INVOICE_ID);
		String lsWorkflowId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		if (null != lsWorkflowId)
		{
			loTaskDetailsBean = (TaskDetailsBean) aoRequest.getPortletSession().getAttribute(
					HHSConstants.TASK_DETAIL_BEAN_SESSION, PortletSession.APPLICATION_SCOPE);
			lsInvoiceId = loTaskDetailsBean.getInvoiceId();
		}
		String lsNextJSP = null;
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
		}// added for release 5
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsInvoiceId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSConstants.INVOICE,
				PortletSession.APPLICATION_SCOPE);
		// added for release 5
		return ApplicationConstants.LOCK_ID_START + HHSR5Constants.INVOICE_ + lsInvoiceId;
	}

}
