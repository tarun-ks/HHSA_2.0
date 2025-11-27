package com.nyc.hhs.frameworks.sessiongrid;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

public class GridTag extends BodyTagSupport
{

	private static final LogInfo LOG_OBJECT = new LogInfo(GridTag.class);
	/**
	 * This is a custom tag class which is executed by the JSTL to generate the
	 * Grid element.
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private String objectName;
	private int pageSize;
	private String cssClass;
	private String alternateCss1;
	private String alternateCss2;
	private String defaultSort;
	private String gridName;
	private String sortType;
	private int miTotalSize;
	private int miPageIndex;

	public void setDefaultSort(String defaultSort)
	{
		this.defaultSort = defaultSort;
	}

	public void setGridName(String gridName)
	{
		this.gridName = gridName;
	}

	public void setSortType(String sortType)
	{
		this.sortType = sortType;
	}

	private final List<Column> loListColumns = new ArrayList<Column>();

	public void addColumn(Column col)
	{
		this.loListColumns.add(col);
	}

	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public void setCssClass(String cssClass)
	{
		this.cssClass = cssClass;
	}

	public void setAlternateCss1(String alternateCss1)
	{
		this.alternateCss1 = alternateCss1;
	}

	public void setAlternateCss2(String alternateCss2)
	{
		this.alternateCss2 = alternateCss2;
	}

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.
	 * 
	 * @returns int SKIP_BODY is the valid return value for doEndTag and
	 *          signifies that tag does not wants to process the body.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public int doEndTag()
	{
		try
		{
			HttpSession loSession = pageContext.getSession();
			HttpServletRequest loRequest = (HttpServletRequest) pageContext.getRequest();
			String lsSortByColumn = this.gridName + "sortBy";
			String lsSortTypeColumn = this.gridName + "sortType";
			List loListOfObjects = (List) loSession.getAttribute(objectName);
			List loListOfObjectsSort = (List) loSession.getAttribute(objectName);
			List loListOfSubObjects;
			String lsColumnName;
			if (loRequest.getParameter("columnName") != null)
			{
				lsColumnName = loRequest.getParameter("columnName");
			}
			else
			{
				lsColumnName = loRequest.getParameter(lsSortByColumn) == null ? "" : (String) loRequest
						.getParameter(lsSortByColumn);
			}
			String lsSortType = loRequest.getParameter(lsSortTypeColumn) == null ? "" : (String) loRequest
					.getParameter(lsSortTypeColumn);
			miTotalSize = loListOfObjects.size();
			String loPageIndex = (String) loRequest.getAttribute("pageIndex");
			
			if (loPageIndex != null)
			{
				miPageIndex = Integer.parseInt(loPageIndex.trim());
			}
			else
			{
				miPageIndex = 1;
			}
			if (pageSize > 0)
			{
				int liMaxRecords = miPageIndex * pageSize > loListOfObjects.size() ? loListOfObjects.size()
						: miPageIndex * pageSize;
				loListOfSubObjects = loListOfObjects.subList(((miPageIndex - 1) * pageSize), liMaxRecords);
				loListOfObjects = loListOfSubObjects;
			}
			// If no column is clicked
			if (null != lsColumnName && lsColumnName.trim().isEmpty())
			{
				if (loRequest.getParameter("gridName") != null
						&& loRequest.getParameter("gridName").equalsIgnoreCase(this.gridName)
						&& loRequest.getParameter(lsSortByColumn) != null
						&& (!((String) loRequest.getParameter(lsSortByColumn)).trim().isEmpty()))
				{
					lsColumnName = (String) loRequest.getParameter(lsSortByColumn);
					lsSortType = (String) loRequest.getParameter(lsSortTypeColumn);
				}
				else
				{
					if (this.defaultSort != null && (lsColumnName.trim().isEmpty()))
					{
						lsColumnName = this.defaultSort;
						lsSortType = this.sortType;
					}
				}
			}
			else if (lsColumnName != null && "sort".equalsIgnoreCase(loRequest.getParameter("action"))
					&& lsColumnName.equalsIgnoreCase(loRequest.getParameter(lsSortByColumn)))
			{
				if (loRequest.getParameter(lsSortTypeColumn) != null)
				{
					if (loRequest.getParameter(lsSortTypeColumn) != null
							&& loRequest.getParameter(lsSortTypeColumn).equalsIgnoreCase("DESC"))
					{
						lsSortType = "ASC";
					}
					else
					{
						lsSortType = "DESC";
					}
				}
				else
				{
					lsSortType = this.sortType;
				}
			}
			else
			{
				lsSortType = this.sortType;
			}
			if (lsColumnName != null && !lsColumnName.trim().isEmpty() && !lsColumnName.equalsIgnoreCase("/"))
			{
				sortList(loListOfObjectsSort, lsColumnName, lsSortType);
			}
			StringBuffer loStringBuffer = new StringBuffer();
			loStringBuffer.append("<table border=1 width=100%>");
			loStringBuffer
					.append("<tr style='display:none;'><td><input type='hidden' id = 'gridName' name='gridName' value=");
			loStringBuffer.append(this.gridName);
			loStringBuffer.append(" /></td>");
			loStringBuffer.append("<td><input type='hidden' id = '");
			loStringBuffer.append(this.gridName);
			loStringBuffer.append("sortType' name='");
			loStringBuffer.append(this.gridName);
			loStringBuffer.append("sortType' value=");
			loStringBuffer.append(lsSortType);
			loStringBuffer.append(" /></td>");
			loStringBuffer.append("<td><input type='hidden' id='");
			loStringBuffer.append(this.gridName);
			loStringBuffer.append("sortBy' name='");
			loStringBuffer.append(this.gridName);
			loStringBuffer.append("sortBy' value=");
			loStringBuffer.append(lsColumnName);
			loStringBuffer.append(" /></td>");
			loStringBuffer.append("<td><input type='hidden' id='pageIndex' name='pageIndex' value=");
			loStringBuffer.append(miPageIndex);
			loStringBuffer.append(" /></td>");
			loStringBuffer.append("<td><input type='hidden' id='pageSize' name='pageSize' value=");
			loStringBuffer.append(pageSize);
			loStringBuffer.append(" /></td></tr>");
			if (loListColumns.isEmpty())
			{
				loStringBuffer.append(generateForAll(loListOfObjects));
			}
			else
			{
				loStringBuffer.append(generateForColumns(loListOfObjects, this.loListColumns, lsColumnName, lsSortType,
						loSession));
			}
			JspWriter loOut = pageContext.getOut();
			loOut.print(loStringBuffer.toString());
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in generating Grid", aoExp);
		}
		return (EVAL_BODY_AGAIN);
	}

	/**
	 * This method generated the data for the whole page.
	 * 
	 * @param aoListOfObjects is the list of the objects that are to be
	 *            generated on the page.
	 * @return the String representation of the generated item.
	 * @throws Exception
	 */
	private String generateForAll(List aoListOfObjects) throws Exception
	{
		StringBuffer loStringBuffer = new StringBuffer();
		for (int liObjectCount = 0; liObjectCount < aoListOfObjects.size(); liObjectCount++)
		{

			loStringBuffer.append("<tr>");
			Object loEachObject = aoListOfObjects.get(liObjectCount);
			final BeanInfo loBeanInfo = Introspector.getBeanInfo(loEachObject.getClass());
			for (final PropertyDescriptor property : loBeanInfo.getPropertyDescriptors())
			{
				String loPropertyName = property.getName().toString();
				if ("class".equalsIgnoreCase(loPropertyName))
				{
					continue;
				}
				String loPropertValue = property.getReadMethod().invoke(loEachObject).toString();
				loStringBuffer.append("<td>");
				loStringBuffer.append(loPropertValue);
				loStringBuffer.append("</td>");
			}
			loStringBuffer.append("</tr>");
			loStringBuffer.append("</table>");
		}
		return loStringBuffer.toString();
	}

	@SuppressWarnings("rawtypes")
	/**
	 * This method generates the data for the specified columns
	 * 
	 * @param aoListOfObjects is the list of the objects that are to be generated
	 * @param aoListOfColumns is the list of the columns where data is to be generated.
	 * @param asColumnToSort is the column name that is to be sort 
	 * @param asSortType specifies the sorting type to be implemented.
	 * @param aoSession is the HttpSession object
	 * @return the String representation of the generated item. 
	 * @throws Exception
	 */
	private String generateForColumns(List aoListOfObjects, List aoListOfColumns, String asColumnToSort,
			String asSortType, HttpSession aoSession) throws Exception
	{
		
		StringBuffer loStringBuffer = new StringBuffer();
		// section to generate heading rows
		loStringBuffer.append("<tr>");

		for (Object loObject : aoListOfColumns)
		{

			Column loColumnAtK = (Column) loObject;
			if (cssClass != null && !cssClass.trim().equals(""))
			{
				loStringBuffer.append("<th class=\"");
				loStringBuffer.append(cssClass);
				loStringBuffer.append("\"");
			}
			else
			{
				loStringBuffer.append("<th");
			}
			loStringBuffer.append(prepareTDStart(loColumnAtK));
			loStringBuffer.append("<b>");
			loStringBuffer.append(getControlForHeading(loColumnAtK, asColumnToSort, asSortType));
			loStringBuffer.append("</b></th>");
		}

		// section to generate data rows
		for (int liObjectCount = 0; liObjectCount < aoListOfObjects.size(); liObjectCount++)
		{
			loStringBuffer.append("<tr>");
			Object loEachObject = aoListOfObjects.get(liObjectCount);
			for (int liColumnCount = 0; liColumnCount < aoListOfColumns.size(); liColumnCount++)
			{
				Column loColumnAtJ = (Column) aoListOfColumns.get(liColumnCount);
				String lsAlternateCss1 = alternateCss1;
				String lsAlternateCss2 = alternateCss2;

				if (liObjectCount % 2 == 0 && lsAlternateCss1 != null && !lsAlternateCss1.trim().equals(""))
				{
					loStringBuffer.append("<td class=\"");
					loStringBuffer.append(lsAlternateCss1);
					loStringBuffer.append("\"");
				}
				else if (liObjectCount % 2 != 0 && lsAlternateCss2 != null && !lsAlternateCss2.trim().equals(""))
				{
					loStringBuffer.append("<td class=\"");
					loStringBuffer.append(lsAlternateCss2);
					loStringBuffer.append("\"");
				}
				else
				{
					loStringBuffer.append("<td>");
				}

				loStringBuffer.append(prepareTDStart(loColumnAtJ));
				loStringBuffer.append(getControlForType(loEachObject, loColumnAtJ, Integer.valueOf(liObjectCount)));
				loStringBuffer.append("</td>");
			}
			loStringBuffer.append("</tr>");
		}
		loStringBuffer.append("</table>");
		if (pageSize != 0 && aoListOfObjects.size() < miTotalSize)
		{
			loStringBuffer.append(movePrevNext());
		}
		return loStringBuffer.toString();
	}

	/**
	 * This method fetches the string value of the control where heading is to
	 * be generated.
	 * 
	 * @param aoCol is the Column name
	 * @param asColumnToSort is the column name which is to be sort
	 * @param asSortType is the String representation of the Sorting algorithm
	 * @return is the String representation of the control where heading is to
	 *         be generated.
	 * @throws Exception
	 */
	private String getControlForHeading(Column aoCol, String asColumnToSort, String asSortType) throws Exception
	{
		String lsControl = "";
		FormElement loFormElement = aoCol.getElementType();
		int liType = 0;
		if (null != loFormElement)
		{
			liType = loFormElement.getType();
		}
		switch (liType)
		{
			case 6:
				Extension loExtension = (Extension) loFormElement;
				Object[] loArguments =
				{ aoCol };
				lsControl = (String) Class.forName(loExtension.getDecoratorClass())
						.getMethod("getControlForHeading", Column.class)
						.invoke(Class.forName(loExtension.getDecoratorClass()).newInstance(), loArguments);

				if (!lsControl.equalsIgnoreCase("RESUME"))
				{
					break;
				}
			default:
				lsControl = getControlForSortable(aoCol, asColumnToSort, asSortType);
		}
		return lsControl;

	}

	/**
	 * This method fetches the string value of the form element type control
	 * which is to be generated corresponding to a column
	 * 
	 * @param aoEachObject is the bean object
	 * @param aoCol is the column name
	 * @param aoSeqNo an Integer value of Sequence number
	 * @return the a string value of generated control
	 * @throws Exception
	 */
	private String getControlForType(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws Exception
	{
		String loPropertyName = aoCol.getColumnName();
		Object loObject = new PropertyDescriptor(loPropertyName, aoEachObject.getClass()).getReadMethod().invoke(
				aoEachObject);
		String loPropertValue = "";
		if (null != loObject)
		{
			loPropertValue = loObject.toString();
		}

		String lsControl = "";
		FormElement loFormElement = aoCol.getElementType();
		int liType = 0;
		if (null != loFormElement)
		{
			liType = loFormElement.getType();
		}
		switch (liType)
		{
			case 1:
				RadioButton loRadio = (RadioButton) loFormElement;
				lsControl = "<input type=radio name=" + loRadio.getGroupName() + " value=" + loRadio.getValue() + ">";
				break;
			case 2:
				CheckBox loCheckBox = (CheckBox) loFormElement;
				if (loPropertyName.equalsIgnoreCase(loCheckBox.getValue()))
				{
					lsControl = "<input type=checkbox name=" + loCheckBox.getName() + " value=" + loPropertValue + ">";
				}
				else
				{
					lsControl = "<input type=checkbox name=" + loCheckBox.getName() + " value=" + null + ">";
				}
				break;
			case 3:
				Link loLink = (Link) loFormElement;
				lsControl = "<a name=" + loLink.getParamName() + " href=" + loLink.getHref() + " onClick="
						+ loLink.getOnClick() + ">" + loLink.getParamValue() + "</a>";
				break;
			case 4:
				TextField loTextField = (TextField) loFormElement;
				lsControl = "<input type=text name=" + loTextField.getName() + " value="
						+ loTextField.getDefaultValue() + ">";
				break;
			case 5:
				ListBox loListBox = (ListBox) loFormElement;
				int liCount = loListBox.getListValues().size();
				if (liCount > 0)
				{
					StringBuffer loStringBuffer = new StringBuffer();
					lsControl = "<select name=" + loListBox.getName() + " size=" + loListBox.getSize() + " "
							+ loListBox.getMultiple() + ">" + "";
					loStringBuffer.append(lsControl);
					for (ListOption loListOption : loListBox.getListValues())
					{
						String lsOption = "<option value=" + loListOption.getValue() + " " + loListOption.getSelected()
								+ ">" + loListOption.getValue().toUpperCase() + "</option>";
						loStringBuffer.append(lsOption);
					}
					loStringBuffer.append("</select>");
					lsControl = loStringBuffer.toString();
				}
				break;
			case 6:
				Extension loExtension = (Extension) loFormElement;
				Object[] loArguments =
				{ aoEachObject, aoCol, aoSeqNo };
				lsControl = (String) Class.forName(loExtension.getDecoratorClass())
						.getMethod("getControlForColumn", Object.class, Column.class, Integer.class)
						.invoke(Class.forName(loExtension.getDecoratorClass()).newInstance(), loArguments);

				break;
			default:
				lsControl = loPropertValue;
		}
		return lsControl;
	}

	/**
	 * This method generates the data corresponding to the table column
	 * 
	 * @param aoColumnAtK is the object of type Column
	 * @return the String representation of the generated item.
	 */
	private String prepareTDStart(Column aoColumnAtK)
	{
		StringBuffer loStringBuffer = new StringBuffer();
		String lsAlignForColumn = aoColumnAtK.getAlign();

		String lsSizeForColumn = aoColumnAtK.getSize();
		if (lsAlignForColumn != null && !lsAlignForColumn.trim().equals(""))
		{
			loStringBuffer.append(" align=\"" + lsAlignForColumn + "\"");
		}

		if (lsSizeForColumn != null && !lsSizeForColumn.trim().equals(""))
		{
			loStringBuffer.append(" width=\"" + lsSizeForColumn + "\"");
		}
		loStringBuffer.append(">");
		return loStringBuffer.toString();
	}

	/**
	 * This method fetches the string value of the control where sorting is to
	 * be implemented
	 * 
	 * @param aoColumn is the column name
	 * @param asColumnToSort is the column name where sorting is to be
	 *            implemented
	 * @param asSortType is the String representation of the Sorting algorithm
	 * @return is the String representation of the control where sorting is to
	 *         be implemented
	 * @throws Exception
	 */
	private String getControlForSortable(Column aoColumn, String asColumnToSort, String asSortType) throws Exception
	{
		String lsControl = "";
		if (aoColumn.getSort())
		{
			String lsClass = "";
			if (asColumnToSort.equalsIgnoreCase(aoColumn.getColumnName()))
			{
				if (asSortType.equalsIgnoreCase("asc"))
				{
					lsClass = "class=\"sort-ascending\"";
				}
				else
				{
					lsClass = "class=\"sort-descending\"";
				}
			}
			else
			{
				lsClass = "class=\"sort-default\"";
			}
			lsControl = "<a href='#' onclick=\"sort('" + aoColumn.getColumnName() + "','" + asSortType + "')\" "
					+ lsClass + ">" + aoColumn.getHeadingName() + "</a>";
		}
		else
		{
			lsControl = aoColumn.getHeadingName();
		}
		return lsControl;
	}

	/**
	 * This method generates the data for pagination.
	 * 
	 * @param aoPaginationWrapper is the String representation of
	 *            PaginationWrapper
	 * @return the String representation of the generated item.
	 * @throws ApplicationException
	 */
	private String movePrevNext() throws ApplicationException
	{
		long llLastPageIndex = Math.round(Math.ceil((double) miTotalSize / pageSize));
		StringBuffer loStringBuffer = new StringBuffer("<div class=\"paginationWrapper\">");
		loStringBuffer.append("<ul>");
		loStringBuffer.append("<li>");
		

		long llSetSize = 5;

		
		long llLowerIndex = miPageIndex <= llSetSize ? 1
				: (((long) Math.floor((miPageIndex / llSetSize)) * llSetSize) + 1);
		long llUpperIndex = llLowerIndex + (llSetSize - 1) > llLastPageIndex ? llLastPageIndex : llLowerIndex
				+ (llSetSize - 1);

		// Previous page
		if (miPageIndex == 1)
		{
			loStringBuffer.append("&nbsp;&nbsp;");
		}
		else
		{
			loStringBuffer.append("<a href='#' onclick=\"paging('" + (miPageIndex - 1)
					+ "')\">Previous</a>&nbsp;&nbsp;");
		}

		// Previous set of pages
		if (miPageIndex <= llSetSize)
		{
			loStringBuffer.append("&nbsp;&nbsp;");
		}
		else
		{
			loStringBuffer.append("<a href='#' onclick=\"paging('" + (llLowerIndex - llSetSize)
					+ "')\"><</a>&nbsp;&nbsp;");
		}

		// Index for current set of pages
		for (long llCOunt = llLowerIndex; llCOunt <= llUpperIndex; llCOunt++)
		{
			if (llCOunt == miPageIndex)
			{
				loStringBuffer.append(llCOunt);
				loStringBuffer.append("&nbsp;&nbsp;");
			}
			else
			{
				loStringBuffer.append("<a href='#' onclick=\"paging('" + (llCOunt) + "')\">" + (llCOunt) + "</a>&nbsp;&nbsp;");
			}
		}

		// Next set of pages
		if (llLastPageIndex <= llUpperIndex)
		{
			loStringBuffer.append("&nbsp;&nbsp;");
		}
		else
		{
			loStringBuffer.append("<a href='#' onclick=\"paging('" + (llUpperIndex + 1) + "')\">></a>&nbsp;&nbsp;");
		}

		// Next page
		if (miPageIndex == llLastPageIndex)
		{
			loStringBuffer.append("&nbsp;&nbsp;");
		}
		else
		{
			loStringBuffer.append("<a href='#' onclick=\"paging('" + (miPageIndex + 1) + "')\">Next</a>&nbsp;&nbsp;");
		}
		loStringBuffer.append("</li>");
		loStringBuffer.append("</ul>");
		loStringBuffer.append("</div>");
		JspWriter loOut = pageContext.getOut();
		try
		{
			loOut.print(loStringBuffer.toString());
		}
		catch (IOException aoIExp)
		{
			LOG_OBJECT.Error("Exception occured in GridTag: movePrevNext method:: ", aoIExp);
			throw new ApplicationException(aoIExp.getMessage(), aoIExp);
		}
		return loStringBuffer.toString();
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	/**
	 * This method sorts the input objects.
	 * 
	 * @param aoListOfObjects is the list of the input objects.
	 * @param asColumnName is the string representation of the column name
	 * @param asSortType is the string representation of the sorting algorithm
	 */
	private void sortList(List aoListOfObjects, String asColumnName, String asSortType) throws ApplicationException
	{
		String lsColumnName2;
		String loClassName = aoListOfObjects.get(0).getClass().toString();
		try
		{
			if (gridName.equalsIgnoreCase("taskmanagement") && (!asColumnName.equalsIgnoreCase("moDateCreated")))
			{
				lsColumnName2 = "moDateCreated";
				Collections.sort(aoListOfObjects, new SortComparator(asColumnName, loClassName, asSortType,
						lsColumnName2));
			}
			else if (gridName.equalsIgnoreCase("taskinbox") && (!asColumnName.equalsIgnoreCase("moLastAssigned")))
			{
				lsColumnName2 = "moLastAssigned";
				Collections.sort(aoListOfObjects, new SortComparator(asColumnName, loClassName, asSortType,
						lsColumnName2));
			}
			else
			{
				Collections.sort(aoListOfObjects, new SortComparator(asColumnName, loClassName, asSortType));
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Error Occored while doing sorting", aoExp);
		}
	}
}
