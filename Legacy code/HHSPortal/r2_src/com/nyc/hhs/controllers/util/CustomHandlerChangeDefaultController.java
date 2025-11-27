/**
 * 
 */
package com.nyc.hhs.controllers.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.portlet.handler.ParameterHandlerMapping;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.util.HHSPortalUtil;

/**
 * @author vipul.garg
 * 
 */
public class CustomHandlerChangeDefaultController extends ParameterHandlerMapping
{

	@Override
	/**
	 * This method gets Handler Internal
	 * <ul>
	 * <li>fetching the value of controller bean id</li>
	 * </ul>
	 * @param request Request
	 */
	protected Object getHandlerInternal(PortletRequest request) throws Exception
	{
		HttpServletRequest loHTTPRequest = (HttpServletRequest) request.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST);
		String lsParameterValue = HHSPortalUtil.parseQueryString(loHTTPRequest,HHSConstants.CONTROLLER_BEAN_ID);
		if (lsParameterValue != null)
		{
			Object handler = getApplicationContext().getBean(lsParameterValue);
			super.getHandlerInternal(request);
			return handler;
		}
		return super.getHandlerInternal(request);
	}
}
