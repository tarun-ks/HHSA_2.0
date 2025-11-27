package com.nyc.hhs.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

/**
 * This utility class has getters and setters for getting and setting the
 * attributes of the application session on the basis portlet session request.
 * 
 */

public class PortletSessionHandler extends ApplicationSession
{

	/**
	 * This method is to get attribute from PortletRequest session
	 * 
	 * @param aoRequest object to be put into application session
	 * @param abKeepIt whether value is to kept into application session or not
	 *            after using it
	 * @param aoKey string with which value is obtained from application
	 *            session\
	 * @return Object value from application session
	 */
	public static Object getAttribute(PortletRequest aoRequest, boolean abKeepIt, String aoKey)
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();
		Object loObj = loPortletSession.getAttribute(aoKey);
		if (!abKeepIt)
		{
			loPortletSession.removeAttribute(aoKey);
		}

		return loObj;
	}

	/**
	 * This method is to set attribute in application session for Portlet
	 * request
	 * 
	 * @param aoObjectToPreserve object to be put into application session
	 * @param aoRequest request attribute
	 * @param aoKey string with which value is stored in application session
	 */

	public static void setAttribute(Object aoObjectToPreserve, PortletRequest aoRequest, String aoKey)
	{
		PortletSession loPortletSession = aoRequest.getPortletSession();

		loPortletSession.setAttribute(aoKey, aoObjectToPreserve);
	}

	/**
	 * This method is to get attribute from application session
	 * 
	 * @param aoRequest object to be put into application session
	 * @param aoKey string with which value is obtained from application session
	 * @return Object value from application session
	 */
	public static Object getAttribute(PortletRequest aoRequest, String aoKey)
	{
		return getAttribute(aoRequest, false, aoKey);
	}

	/**
	 * This method is to set attribute in application session for resource
	 * request
	 * 
	 * @param aoObjectToPreserve object to be put into application session
	 * @param aoRequest request attribute
	 * @param aoKey string with which value is stored in application session
	 */
	public static void setAttribute(Object aoObjectToPreserve, ResourceRequest aoResourceRequest, String aoKey)
	{
		PortletSession loPortletSession = aoResourceRequest.getPortletSession();

		loPortletSession.setAttribute(aoKey, aoObjectToPreserve);
	}

}
