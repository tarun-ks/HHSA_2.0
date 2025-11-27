package com.nyc.hhs.frameworks.grid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;


/**
 * This is a custom tag class which is executed by the JSTL to generate a
 * checkbox element in the Grid.
 * 
 */

public class CheckBoxTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CheckBoxTag.class);
	
	private static final long serialVersionUID = 1L;

	private String msName;
	private String msValue;

	public void setName(String asName)
	{
		this.msName = asName;
	}

	public void setValue(String asValue)
	{
		this.msValue = asValue;
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
			CheckBox loCheckBox = new CheckBox(msName, msValue);
			ColumnTag loColumnTag = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumnTag == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumnTag.setElementType(loCheckBox);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured in Check box tag", aoExp);
		}
		return (SKIP_BODY);
	}
}
