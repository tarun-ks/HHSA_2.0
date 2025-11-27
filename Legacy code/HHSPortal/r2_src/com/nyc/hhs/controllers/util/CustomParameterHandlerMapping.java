/**
 * 
 */
package com.nyc.hhs.controllers.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.portlet.handler.ParameterHandlerMapping;

import com.nyc.hhs.util.HHSPortalUtil;

/**
 * @author vipul.garg
 * 
 */
public class CustomParameterHandlerMapping extends ParameterHandlerMapping
{

	@Override
	/**
	 * This method gets Handler Internal
	 * <ul>
	 * <li>fetching the value of overlay</li>
	 * </ul>
	 * @param request Request
	 */
	protected Object getHandlerInternal(PortletRequest request) throws Exception
	{
		HttpServletRequest loHTTPRequest = (HttpServletRequest) request.getAttribute("javax.servlet.request");
		String lsParameterValue = HHSPortalUtil.parseQueryString(loHTTPRequest, "overlay");
		if (lsParameterValue != null && lsParameterValue.equalsIgnoreCase("true"))
		{
			super.getHandlerInternal(request);
			return null;
		}
		return super.getHandlerInternal(request);
	}
}
