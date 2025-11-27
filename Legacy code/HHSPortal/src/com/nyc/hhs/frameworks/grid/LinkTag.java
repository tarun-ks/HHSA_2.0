package com.nyc.hhs.frameworks.grid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a link
 * element in the Grid.
 * 
 */

public class LinkTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(LinkTag.class);
	
	private static final long serialVersionUID = 1L;
	private String href;
	private String paramName;
	private String paramValue;
	private String onClick;

	public String getOnClick()
	{
		return onClick;
	}

	public void setOnClick(String onClick)
	{
		this.onClick = onClick;
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	public String getParamName()
	{
		return paramName;
	}

	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}

	public String getParamValue()
	{
		return paramValue;
	}

	public void setParamValue(String paramValue)
	{
		this.paramValue = paramValue;
	}

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the start tag for this instance.
	 * 
	 * @returns int SKIP_BODY is the valid return value for doStartTag and
	 *          signifies that tag does not wants to process the body.
	 */
	public int doStartTag()
	{

		try
		{
			Link loLinkObj = new Link(href, paramName, paramValue, onClick);
			ColumnTag loColumng = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumng == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumng.setElementType(loLinkObj);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while generating Link Tag", aoExp);
		}
		return (SKIP_BODY);
	}
}
