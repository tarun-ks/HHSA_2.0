package com.nyc.hhs.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

/**
 * This controller is for viewing shared document shared by other provider
 * 
 */

public class HomeManageProviderPortletView extends GenericPortlet
{

	/**
	 * This method is handle all the rendering activities.
	 * also method sets the values in the RenderRequest reference, so that same values can
	 * be displayed on the required jsp.
	 * @param aoRequest - RenderRequest
	 * @param aoResponse - RenderResponse
	 * @return loModelAndView
	 * @throws  PortletException
	 * @throws  IOException
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
	{
		PortletRequestDispatcher loPortletDispatcher = getPortletContext().getRequestDispatcher("/portlet/homeaccelerator/homemanageprovider.jsp");
		loPortletDispatcher.include(request, response);

	}

	/**
	 * This method performs the required action, by setting the required values
	 * in the channel object and thereafter executing the transaction. 
	 * @param aoRequest - ActionRequest
	 * @param aoResponse - ActionResponse
	 * @throws  PortletException
	 * @throws  IOException
	 * @throws  UnavailableException
	 */
	public void processAction(ActionRequest aoRequest, ActionResponse aResponse) throws PortletException, IOException, UnavailableException
	{
		String lsYourname = (String) aoRequest.getParameter("yourname");
		aResponse.setRenderParameter("yourname", lsYourname);
	}

}
