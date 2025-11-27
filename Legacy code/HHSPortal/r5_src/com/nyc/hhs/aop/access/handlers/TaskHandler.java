package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.HHSPortalUtil;

public class TaskHandler extends BaseAccessHandler
{
	 /**
	 * This method is used to generate Id
	 * 
	 * @param aoRet Object
	 * @param aoRequest PortletRequest
	 * @return null
	 * @throws ApplicationException If an Exception occurs
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest) throws ApplicationException
	{

		String taskId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WOB_NUMBER);
		if (null == taskId)
		{
			taskId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.WORKFLOW_ID);
		}
		if (null == taskId)
		{
			taskId = HHSPortalUtil.parseQueryString(aoRequest, "taskid");
		}
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, taskId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, "tasks",
				PortletSession.APPLICATION_SCOPE);
		return null;
	}

}
