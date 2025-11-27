package com.nyc.hhs.frameworks.sessiongrid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.frameworks.logger.LogInfo;

public class ColumnTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ColumnTag.class);
	/**
	 * This is a custom tag class which is executed by the JSTL to generate a
	 * Column element in the Grid.
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private String columnName;
	private String headingName;
	private String sort;
	private String msSortType;
	private String msSortValue;
	private String align;
	private String size;
	private FormElement elementType;

	public void setElementType(FormElement elementType)
	{
		this.elementType = elementType;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public void setHeadingName(String headingName)
	{
		this.headingName = headingName;
	}

	public void setAlign(String allign)
	{
		this.align = allign;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public void setSort(String sort)
	{
		this.sort = sort;
	}

	public void setSortType(String sortType)
	{
		this.msSortType = sortType;
	}

	public void setSortValue(String sortValue)
	{
		this.msSortValue = sortValue;
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
			Column loColumn = new Column(columnName, headingName);
			if (sort != null && !sort.trim().equals(""))
			{
				loColumn.setSort(true);
			}
			else
			{
				loColumn.setSort(false);
			}

			if (msSortType != null && !msSortType.trim().equals(""))
			{
				loColumn.setSortType(msSortType);
			}
			if (msSortValue != null && !msSortValue.trim().equals(""))
			{
				loColumn.setSortValue(msSortValue);
			}
			else
			{
				loColumn.setSortValue("ASC");
			}
			if (align != null && !align.trim().equals(""))
			{
				loColumn.setAlign(align);
			}
			if (size != null && !size.trim().equals(""))
			{
				loColumn.setSize(size);
			}
			if (elementType != null)
			{
				loColumn.setElementType(elementType);
			}
			GridTag loParentGrid = (GridTag) findAncestorWithClass(this, GridTag.class);
			if (loParentGrid == null)
			{
				throw new JspTagException("nesting error");
			}
			else
			{
				loParentGrid.addColumn(loColumn);
			}
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while generating Column Tag", aoExp);
		}
		return (SKIP_BODY);
	}
}