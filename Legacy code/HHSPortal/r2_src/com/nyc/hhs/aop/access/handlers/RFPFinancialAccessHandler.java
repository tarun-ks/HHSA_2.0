package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.HHSPortalUtil;

public class RFPFinancialAccessHandler extends BaseAccessHandler
{
	/**
	 * This method is used to generate Id
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest) throws ApplicationException
	{
		String lsProcurementId = HHSPortalUtil.parseQueryString(aoRequest, HHSConstants.PROCUREMENT_ID);
		// Added for release 5
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID, lsProcurementId,
				PortletSession.APPLICATION_SCOPE);
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME, HHSR5Constants.RFP_FINANCIAL,
				PortletSession.APPLICATION_SCOPE);
		// Added for release 5
		return ApplicationConstants.LOCK_ID_START + HHSConstants.RFP_FINANCIAL_UNDERSCORE + lsProcurementId;
	}

}
