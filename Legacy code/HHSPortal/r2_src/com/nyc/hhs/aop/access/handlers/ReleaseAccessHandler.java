package com.nyc.hhs.aop.access.handlers;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.sessionListener.SessionListener;

public class ReleaseAccessHandler extends BaseAccessHandler
{
	/**
	 * This method generates the id specific to a module screens
	 * <ul>
	 * <li>1. Get http session</li>
	 * <li>2. Invoke remove user methof of sessionlistener class to release any
	 * lock been added by current user</li>
	 * </ul>
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Empty string
	 * @throws ApplicationException - throw exception in case any
	 */
	@Override
	public String generateId(Object aoRet, PortletRequest aoRequest) throws ApplicationException
	{
		HttpSession loHttpSession = ((HttpServletRequest) aoRequest.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST))
				.getSession();
		SessionListener.removeUser(loHttpSession);
		return HHSConstants.EMPTY_STRING;
	}
}