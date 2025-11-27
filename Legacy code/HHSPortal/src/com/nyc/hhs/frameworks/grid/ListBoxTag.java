package com.nyc.hhs.frameworks.grid;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a List
 * box element in the Grid.
 * 
 */

public class ListBoxTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ListBoxTag.class);
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String size;
	private String multiple;

	private List<ListOption> listValues = new ArrayList<ListOption>();

	public void addListValue(ListOption col)
	{
		this.listValues.add(col);
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public void setMultiple(String multiple)
	{
		this.multiple = multiple;
	}

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.
	 * 
	 * @returns int SKIP_BODY is the valid return value for doEndTag and
	 *          signifies that tag does not wants to process the body.
	 */
	public int doEndTag()
	{

		try
		{
			ListBox loListBox = new ListBox(id);
			if (name != null && !name.trim().equals(""))
			{
				loListBox.setName(name);
			}
			if (size != null && !size.trim().equals(""))
			{
				loListBox.setSize(size);
			}
			if (multiple != null && !multiple.trim().equals(""))
			{
				loListBox.setMultiple(multiple);
			}
			loListBox.setListValues(listValues);
			ColumnTag loColumn = (ColumnTag) findAncestorWithClass(this, ColumnTag.class);

			if (loColumn == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loColumn.setElementType(loListBox);
			}
		}
		catch (Exception aoException)
		{
			LOG_OBJECT.Error("Error occured while generating List Box Tag", aoException);
		}
		return (SKIP_BODY);
	}
}
