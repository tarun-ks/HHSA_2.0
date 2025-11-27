package com.nyc.hhs.frameworks.common;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate the R2
 * Navigation
 */

public class NavigationSMTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NavigationSMTag.class);

	private static final long serialVersionUID = 1L;
	private String screenName;

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @returns EVAL_BODY_AGAIN if screen name doesnt matches, EVAL_BODY_INCLUDE
	 *          if screen name matches
	 */
	public int doStartTag()
	{
		JspWriter loOut = pageContext.getOut();
		int liValueToReturn = SKIP_BODY;
		try
		{
			HttpServletRequest loRequest = (HttpServletRequest) pageContext.getRequest();
			String lsSelectedScreenName = (String) loRequest.getAttribute(HHSConstants.SELECTED_CHILD_TAB);
			if (null != screenName)
			{
				String loScreenNames[] = screenName.split(HHSConstants.COMMA);
				if (lsSelectedScreenName != null && Arrays.asList(loScreenNames).contains(lsSelectedScreenName))
				{
					pageContext.include(HHSConstants.SM_HEADER_PATH);
					liValueToReturn = EVAL_BODY_INCLUDE;
				}
				else
				{
					loOut.print("<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>");
				}
			}
		}
		catch (Exception aoExp)
		{
			// Log the IO exceptions
			LOG_OBJECT.Error("Error in generating navigation R2", aoExp);
		}
		return liValueToReturn;
	}

	/**
	 * @param screenName the screenName to set
	 */
	public void setScreenName(String screenName)
	{
		this.screenName = screenName;
	}
}