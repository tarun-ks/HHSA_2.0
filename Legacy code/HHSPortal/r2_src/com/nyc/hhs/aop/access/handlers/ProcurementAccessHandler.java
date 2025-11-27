package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.util.HHSPortalUtil;

public class ProcurementAccessHandler extends BaseAccessHandler
{
	/**
	 * This method generates the id specific to a module screens
	 * <ul>
	 * <li>1. Get procurement id from request</li>
	 * <li>2. Generate an id based on procurement id</li>
	 * </ul>
	 * 
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Generated id for the module
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{ // added
		// for
		// R5
		String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		if (StringUtils.isNotBlank(lsProcurementId))
		{
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsProcurementId,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSConstants.PROCUREMENT,
					PortletSession.APPLICATION_SCOPE);
			return ApplicationConstants.LOCK_ID_START + HHSConstants.PROCUREMENT_UNDERSCORE + lsProcurementId;
		}
		else
		{
			aoRequest.getPortletSession().removeAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().removeAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME,
					PortletSession.APPLICATION_SCOPE);
			return HHSConstants.EMPTY_STRING;
			// added for R5
		}
	}
}