package com.nyc.hhs.frameworks.sessiongrid;

import com.nyc.hhs.model.ApplicationAuditBean;

/**
 * This class generates an extension which creates a link which will display the
 * selected tasks.
 * 
 */

public class TaskDetailExtension implements DecoratorInterface
{

	/**
	 * This Method is used to generate the link on the Task Name and further
	 * fetching the task details page.
	 * 
	 * @param aoEachObject
	 *            Bean Name
	 * @param aoCol
	 *            Column name
	 * @param aiSeqNo
	 *            Sequence Number
	 * @return String
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		ApplicationAuditBean loTaskQueue = (ApplicationAuditBean) aoEachObject;
		String lsControl = "";

		if ("msData".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<div>" + loTaskQueue.getMsData() + "</div>"; // Fixed 1716 & removed breakall css class
		}
		return lsControl;
	}

	/**
	 * This Method is used to check all the check boxes if present.
	 * 
	 * @param aoCol
	 *            column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControlVal = "RESUME";
		return lsControlVal;
	}
}
