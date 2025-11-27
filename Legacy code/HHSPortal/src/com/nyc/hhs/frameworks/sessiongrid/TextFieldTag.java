package com.nyc.hhs.frameworks.sessiongrid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a
 * Text-Field element in the Grid.
 * 
 */

public class TextFieldTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(TextFieldTag.class);
	private static final long serialVersionUID = 1L;
	private String name;
	private int maxlength;
	private int size;
	private String defaultValue;
	
	

	public void setMaxlength(int maxlength)
	{
		this.maxlength = maxlength;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public void setSize(int size)
	{
		this.size = size;
	}



	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the start tag for this instance.
	 * @returns int SKIP_BODY is the valid return value for doStartTag and
	 *          signifies that tag does not wants to process the body.
	 */
	public int doStartTag()
	{

		try
		{
			TextField loTextFieldObj = new TextField(name);
			if (defaultValue != null && !defaultValue.trim().equals(""))
			{
				loTextFieldObj.setDefaultValue(defaultValue);
			}
			if (size > 0)
			{
				loTextFieldObj.setSize(size);
			}
			if (maxlength > 0)
			{
				loTextFieldObj.setMaxlength(maxlength);
			}

			ColumnTag loColumn = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumn == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumn.setElementType(loTextFieldObj);
			}
		}
		catch (Exception aoExc)
		{
			LOG_OBJECT.Error("Error occured while generating Text Field Tag", aoExc);
		}
		return (SKIP_BODY);
	}
}
