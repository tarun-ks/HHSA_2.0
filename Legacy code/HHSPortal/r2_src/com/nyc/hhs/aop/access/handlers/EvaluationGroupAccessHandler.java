package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.util.HHSPortalUtil;

public class EvaluationGroupAccessHandler extends BaseAccessHandler
{
	/**
	 * This method generates the id specific to a module screens
	 * <ul>
	 * <li>1. Get Evaluation Group id from request</li>
	 * <li>2. Generate an id based on Evaluation Group id</li>
	 * </ul>
	 * 
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Generated id for the module
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		String lsEvaluationGroupId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.EVALUATION_GROUP_ID);
		String lsLockingId = null;
		// Added for r5
		if (StringUtils.isNotBlank(lsEvaluationGroupId))
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsEvaluationGroupId,
					PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSR5Constants.EVALUATION_GROUP,
				PortletSession.APPLICATION_SCOPE);
		lsLockingId = ApplicationConstants.LOCK_ID_START + HHSConstants.EVALUATION_GROUP_UNDERSCORE
				+ lsEvaluationGroupId;
		// Added for r5
		return lsLockingId;
	}
}