package com.nyc.hhs.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * This controller is for Alert modules
 * 
 */

public class AlertPortletView extends GenericPortlet
{

	/**
	 * This method will be executed to redirect the user to the required jsp
	 * page after successfully processing the request This is an method from
	 * portlet frame work which we override to achieve out functionality
	 * 
	 * @param request
	 *            render request object
	 * @param response
	 *            render response object
	 * @throws PortletException
	 *             when portletException occurred while processing the request
	 * @throws IOException
	 *             when IOException occurred while processing the request
	 */
	public void doView(RenderRequest aoRequest, RenderResponse aoResponse) throws PortletException, IOException
	{
		PortletRequestDispatcher loPrd = getPortletContext().getRequestDispatcher("/portlet/homeprovider/alertportlet/alertportlet.jsp");
		loPrd.include(aoRequest, aoResponse);
	}
}
