package com.nyc.hhs.frameworks.sessiongrid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a Radio
 * Button element in the Grid.
 * 
 */

public class RadioTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(RadioTag.class);
	private static final long serialVersionUID = 1L;
	private String groupName;
	private String value;

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
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
			RadioButton loLink = new RadioButton(groupName, value);
			ColumnTag loColumn = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumn == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumn.setElementType(loLink);
			}
		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occured while generating Radio Tag", aoException);
		}
		return (SKIP_BODY);
	}
}
