package com.nyc.hhs.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This utility class has methods that provides the functionality like parsing
 * the portlet render request and return the value of given parameter if value
 * exist with request and fetching HTTP request/response from action
 * request/response
 * 
 */

public class HHSPortalUtil extends PortalUtil
{
	/**
	 * This method gets the Servlet Request.
	 * @return - HttpServletRequest reference
	 */
	/**
	 * @param aoPortletRequest portlet request object
	 * @return Servlet Request
	 */
	public static HttpServletRequest getServletRequest(PortletRequest aoPortletRequest)
	{
		return (HttpServletRequest) aoPortletRequest.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST);
	}

	/**
	 * This method gets the Servlet Response.
	 * 
	 * @param aoActionRequest - ActionRequest
	 * @return
	 */
	public static HttpServletResponse getServletResponse(PortletRequest aoPortletRequest)
	{
		return (HttpServletResponse) aoPortletRequest.getAttribute("javax.servlet.response");
	}

	/**
	 * Type cast portletRequest to ActionRequest
	 * 
	 * @return type casted portletRequest
	 */
	/**
	 * @param aoRequest request object
	 * @return Action Request
	 */
	public static javax.portlet.ActionRequest getActionRequest(PortletRequest aoRequest)
	{
		return (javax.portlet.ActionRequest) (aoRequest);
	}

	/**
	 * This method parses the Query String.
	 * 
	 * @param aoRenderRequest - RenderRequest
	 * @param asParameterName - Parameter Name
	 * @return - lsParameterValue
	 */
	public static String parseQueryString(PortletRequest aoRenderRequest, String asParameterName)
	{
		String lsParameterValue = null;
		HttpServletRequest loHTTPRequest = (HttpServletRequest) aoRenderRequest.getAttribute("javax.servlet.request");
		lsParameterValue = parseQueryString(loHTTPRequest, "overlay");
		if (lsParameterValue == null)
		{
			if (aoRenderRequest.getParameter(asParameterName) != null)
			{
				lsParameterValue = aoRenderRequest.getParameter(asParameterName);
			}
			else if (aoRenderRequest.getAttribute(asParameterName) != null)
			{
				lsParameterValue = aoRenderRequest.getAttribute(asParameterName).toString();
			}
			else
			{
				lsParameterValue = parseQueryString(loHTTPRequest, asParameterName);
			}
		}
		else
		{
			lsParameterValue = parseQueryString(loHTTPRequest, asParameterName);
		}
		return lsParameterValue;
	}
}
