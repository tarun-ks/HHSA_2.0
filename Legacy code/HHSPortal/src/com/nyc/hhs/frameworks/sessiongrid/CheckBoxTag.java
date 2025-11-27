package com.nyc.hhs.frameworks.sessiongrid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a check
 * box element in the Grid.
 * 
 */

public class CheckBoxTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CheckBoxTag.class);
	private static final long serialVersionUID = 1L;

	private String name;
	private String value;

	public void setName(String name)
	{
		this.name = name;
	}

	public void setValue(String value)
	{
		this.value = value;
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
			CheckBox loCheckBoxObj = new CheckBox(name, value);
			ColumnTag loColumn = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumn == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumn.setElementType(loCheckBoxObj);
			}
		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occured while generating Check Box Tag", aoException);
		}
		return (SKIP_BODY);
	}

}
