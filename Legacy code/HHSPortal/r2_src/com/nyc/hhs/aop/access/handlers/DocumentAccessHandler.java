package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.util.HHSPortalUtil;

public class DocumentAccessHandler extends BaseAccessHandler
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
	{
		String lsDocumentId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.DOC_ID);
		if (lsDocumentId != null && !lsDocumentId.isEmpty())
		{// added for r5
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsDocumentId,
					PortletSession.APPLICATION_SCOPE);
			aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSR5Constants.DOCUMENT_ACCESS,
					PortletSession.APPLICATION_SCOPE);
			// added for r5
			return ApplicationConstants.LOCK_ID_START + HHSConstants.PROCUREMENT_UNDERSCORE + lsDocumentId;
		}
		else
		{
			return HHSConstants.EMPTY_STRING;
		}
	}
}