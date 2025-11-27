package com.nyc.hhs.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * This controller is used for displaying list of project with whom documents
 * are shared
 * 
 */

public class HomeDocumentSharedPortletView extends GenericPortlet
{
	/**
	 * This method is handle all the rendering activities. also method sets the
	 * values in the RenderRequest reference, so that same values can be
	 * displayed on the required jsp.
	 * 
	 * @param aoRequest
	 *            - RenderRequest
	 * @param aoResponse
	 *            - RenderResponse
	 * @return loModelAndView
	 * @throws PortletException
	 * @throws IOException
	 */
	public void doView(RenderRequest aoRequest, RenderResponse aoResponse) throws PortletException, IOException
	{
		PortletRequestDispatcher loPrd = getPortletContext().getRequestDispatcher(
				"/portlet/homeprovider/documentshared/homedocumentsharedinitial.jsp");
		loPrd.include(aoRequest, aoResponse);
	}
}
