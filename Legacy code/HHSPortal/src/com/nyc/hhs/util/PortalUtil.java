package com.nyc.hhs.util;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.UserBean;

/**
 * This utility class has methods that provides the functionality like parsing
 * the portlet render request and return the value of given parameter if value
 * exist with request and fetching HTTP request/response from action
 * request/response
 * 
 */

@SuppressWarnings("deprecation")
public class PortalUtil
{
	/**
	 * This method parses the Query String.
	 * 
	 * @param aoRenderRequest - RenderRequest
	 * @param asParameterName - Parameter Name
	 * @return - lsParameterValue
	 * @throws ApplicationException
	 */
	public static String parseQueryString(RenderRequest aoRenderRequest, String asParameterName)
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

	/**
	 * This method parses the Query String.
	 * 
	 * @param aoActionRequest - ActionRequest
	 * @param asParameterName - Parameter name
	 * @return - lsParameterValue
	 * @throws ApplicationException
	 */
	public static String parseQueryString(ActionRequest aoActionRequest, String asParameterName)
	{
		String lsParameterValue = null;
		if (aoActionRequest.getParameter(asParameterName) != null)
		{
			lsParameterValue = aoActionRequest.getParameter(asParameterName);
		}
		else if (aoActionRequest.getAttribute(asParameterName) != null)
		{
			lsParameterValue = (String) aoActionRequest.getAttribute(asParameterName);
		}
		else
		{
			HttpServletRequest loHTTPRequest = (HttpServletRequest) aoActionRequest
					.getAttribute("javax.servlet.request");
			lsParameterValue = parseQueryString(loHTTPRequest, asParameterName);
		}

		return lsParameterValue;
	}

	/**
	 * This method parses the Query String.
	 * 
	 * @param aoRequest - HttpServletRequest
	 * @param asParameterName - Parameter name
	 * @return - lsParameterValue
	 */
	public static String parseQueryString(HttpServletRequest aoRequest, String asParameterName)
	{
		String lsParameterValue = null;
		if (aoRequest.getParameter(asParameterName) != null)
		{
			lsParameterValue = aoRequest.getParameter(asParameterName);
		}
		else if (aoRequest.getAttribute(asParameterName) != null)
		{
			lsParameterValue = (String) aoRequest.getAttribute(asParameterName);
		}
		return lsParameterValue;
	}

	/**
	 * This method gets the Servlet Request.
	 * 
	 * @param aoActionRequest - ActionRequest
	 * @return - HttpServletRequest reference
	 */
	public static HttpServletRequest getServletRequest(ActionRequest aoActionRequest)
	{
		return (HttpServletRequest) aoActionRequest.getAttribute("javax.servlet.request");
	}

	/**
	 * This method gets the Servlet Response.
	 * 
	 * @param aoActionRequest - ActionRequest
	 * @return
	 */
	public static HttpServletResponse getServletResponse(ActionRequest aoActionRequest)
	{
		return (HttpServletResponse) aoActionRequest.getAttribute("javax.servlet.response");
	}

	/**
	 * This method gets the Servlet Request.
	 * 
	 * @param aoRenderRequest - RenderRequest
	 * @return - HttpServletRequest reference
	 */
	public static HttpServletRequest getServletRequest(RenderRequest aoRenderRequest)
	{
		return (HttpServletRequest) aoRenderRequest.getAttribute("javax.servlet.request");
	}
	
	// Start QC9665 R 9.3.2
	public static void setUserInfoIntoHttp(RenderRequest aoRequest, UserBean aoUserBean ){

		HttpSession loHttpSession =   PortalUtil.getServletRequest(aoRequest).getSession();
		loHttpSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_TOKEN, aoUserBean);

		return;
	}
	
	public static UserBean getUserInfoIntoHttp(RenderRequest aoRequest, UserBean aoUserBean ){

		HttpSession loHttpSession =   PortalUtil.getServletRequest(aoRequest).getSession();

		return (UserBean) loHttpSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_TOKEN);
	}
	// End QC9665 R 9.3.2
}
