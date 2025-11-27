package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.util.HHSPortalUtil;

public class EvaluationAccessHandler extends BaseAccessHandler
{
	/**
	 * This method generates the id specific to a module screens
	 * <ul>
	 * <li>1. Get EvaluationPoolMapping id from request</li>
	 * <li>2. Generate an id based on EvaluationPoolMapping id</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Generated id for the module
	 * 
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		String lsEvaluationPoolMappingId = HHSPortalUtil.parseQueryString(aoRequest,
				HHSConstants.EVALUATION_POOL_MAPPING_ID);
		// Added for release 5
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsEvaluationPoolMappingId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSConstants.REVIEW_EVALUATION_TASK,
				PortletSession.APPLICATION_SCOPE);
		// Added for release 5
		return ApplicationConstants.LOCK_ID_START + HHSConstants.EVALUATION_UNDERSCORE + lsEvaluationPoolMappingId;
	}
}