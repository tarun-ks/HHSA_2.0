package com.nyc.hhs.frameworks.grid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate custom
 * extension element in the Grid.
 * 
 */

public class ExtensionTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ExtensionTag.class);
	
	private static final long serialVersionUID = 1L;

	private String decoratorClass;

	public void setDecoratorClass(String decoratorClass)
	{
		this.decoratorClass = decoratorClass;
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
			Extension loExtension = new Extension(decoratorClass);

			ColumnTag loColumnTag = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumnTag == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumnTag.setElementType(loExtension);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while generating Extension Tag", aoExp);
		}
		return (SKIP_BODY);
	}
}
