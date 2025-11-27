package com.nyc.hhs.frameworks.sessiongrid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a
 * ListOption element in the Grid.
 * 
 */

public class ListOptionTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ListOptionTag.class);
	private static final long serialVersionUID = 1L;
	private String value;
	private String selected;

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setSelected(String selected)
	{
		this.selected = selected;
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
			ListOption loListOption = new ListOption(value);
			if (selected != null && !selected.trim().equals(""))
			{
				loListOption.setSelected(selected);
			}
			ListBoxTag loListBoxTag = (ListBoxTag) findAncestorWithClass(this, ListBoxTag.class);

			if (loListBoxTag == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loListBoxTag.addListValue(loListOption);
			}
		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occured while generating List Option Tag", aoException);
		}
		return (SKIP_BODY);
	}
}
