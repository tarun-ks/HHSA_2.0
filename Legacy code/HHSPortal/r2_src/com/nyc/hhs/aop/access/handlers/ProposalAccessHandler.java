package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.util.HHSPortalUtil;

public class ProposalAccessHandler extends BaseAccessHandler
{
	/**
	 * This method generates the id specific to a module screens
	 * <ul>
	 * <li>1. Get proposal id from request</li>
	 * <li>2. Generate an id based on proposal id</li>
	 * </ul>
	 * 
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Generated id for the module
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest)
	{
		String lsProposalId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROPOSAL_ID);
		// added for release 5
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsProposalId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSConstants.PROPOSAL,
				PortletSession.APPLICATION_SCOPE);
		return ApplicationConstants.LOCK_ID_START + HHSConstants.PROPOSAL_UNDERSCORE + lsProposalId;
	}// added for release 5
}