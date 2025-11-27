package com.nyc.hhs.frameworks.grid;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This is a custom tag class which is executed by the JSTL to generate a Column
 * element in the Grid.
 * 
 */

public class ColumnTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ColumnTag.class);
	
	private static final long serialVersionUID = 1L;
	private String columnName;
	private String headingName;
	private String sortType;
	private String sortValue;
	private String align;
	private String size;
	private FormElement elementType;
	//Added for R5
	private String toolTip;
	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
	//End

	public void setElementType(FormElement elementType)
	{
		this.elementType = elementType;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}
//added in R5
	public String getColumnName()
	{
		return columnName;
	}

	public String getHeadingName()
	{
		return headingName;
	}

	public String getSortType()
	{
		return sortType;
	}

	public String getSortValue()
	{
		return sortValue;
	}

	public String getSize()
	{
		return size;
	}

	public FormElement getElementType()
	{
		return elementType;
	}
//r5 changes ends
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

	public void setSortType(String sortType)
	{
		this.sortType = sortType;
	}

	public void setSortValue(String sortValue)
	{
		this.sortValue = sortValue;
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
			if (sortType != null && !sortType.trim().equals(""))
			{
				loColumn.setSortType(sortType);
			}
			if (sortValue != null && !sortValue.trim().equals(""))
			{
				loColumn.setSortValue(sortValue);
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
			//Added For R5
			if (StringUtils.isNotBlank(toolTip))
			{
				loColumn.setToolTip(toolTip);
			}
			//End
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
			LOG_OBJECT.Error("Error occured in Column Tag", aoExp);
		}
		return (SKIP_BODY);
	}
}