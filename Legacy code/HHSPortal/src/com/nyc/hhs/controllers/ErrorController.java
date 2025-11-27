package com.nyc.hhs.controllers;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.PortalUtil;

public class ErrorController extends GenericPortlet
{
	/**
	 * This method is to render the next page depending on the action, FAQ
	 * maintenance process
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws PortletException
	 * @throws IOException
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ErrorController.class);

	public void doView(RenderRequest aoRequest, RenderResponse aoResponse) throws PortletException, IOException
	{
		long loStartTime = System.currentTimeMillis();
		String lsFilePath = "/error/errorpage.jsp";
		// Added below if condition to check if error has occured due to
		// hazardous character sequence.
		if (HHSConstants.ERROR_INVALID_CHARACTERS.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
				HHSConstants.ERROR_INVALID_CHARACTERS)))
		{
			aoRequest.setAttribute(HHSConstants.INVALID_CHARACTERS, HHSConstants.INVALID_CHARACTERS);
		}
		else if ("userExitInSession".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "userExitInSession")))
		{
			aoRequest.setAttribute("userExitInSession", "userExitInSession");
			lsFilePath = "/error/errorpageuserexist.jsp";
		}
		else if ("userDoesNotBelong".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "userDoesNotBelong")))
		{
			aoRequest.setAttribute("userDoesNotBelong", "userDoesNotBelong");
			lsFilePath = "/error/errorpageuserexist.jsp";
		}
		else if ("userDoesNotBelongToOrg".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
				"userDoesNotBelongToOrg")))
		{
			aoRequest.setAttribute("userDoesNotBelongToOrg", "userDoesNotBelongToOrg");
			lsFilePath = "/error/errorpageuserexist.jsp";
		}
		PortletRequestDispatcher loReqDispatcher = getPortletContext().getRequestDispatcher(lsFilePath);
		loReqDispatcher.include(aoRequest, aoResponse);
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in ErrorController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in ErrorController", aoEx);
		}
	}
}
