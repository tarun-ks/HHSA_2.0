package com.nyc.hhs.frameworks.sessiongrid;

import com.nyc.hhs.model.TaskQueue;

/**
 * This class generates an extension which creates a link which will display the
 * selected tasks.
 * 
 */

public class TaskIDExtension implements DecoratorInterface
{

	/**
	 * This Method is used to generate the link on the Task Name and further
	 * fetching the task details page.
	 * 
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aiSeqNo Sequence Number
	 * @return String
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		TaskQueue loTaskQueue = (TaskQueue) aoEachObject;
		String lsControl = "";

		boolean lbTaskLock = loTaskQueue.isMbIsTaskLocked();
		String lsTaskLock = String.valueOf(lbTaskLock);
		boolean lbManagerTask = loTaskQueue.isMbIsManagerReviewStep();
		String lsManagerTask = String.valueOf(lbManagerTask);
		String lsStatus = loTaskQueue.getMsStatus();

		if ("msWobNumber".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<input type=checkbox name=check id=check value=" + loTaskQueue.getMsWobNumber() + "_"
					+ lsTaskLock + "_" + lsManagerTask + "_" + loTaskQueue.getMsTaskName()
					+ " onClick=\"javascript: enableSubmit()\"/>";
		}
		else
		{
			lsControl = "<a href=\"javascript:;\" title='" + loTaskQueue.getMsTaskName()
					+ "' id='awardTaskName' onclick=\"javascript: submitForm(" + "'" + loTaskQueue.getMsWobNumber() + "','" + lsTaskLock
					+ "','" + lsManagerTask + "','" + lsStatus + "');\">" + loTaskQueue.getMsTaskName() + "</a>";
		}
		return lsControl;
	}

	/**
	 * This Method is used to check all the check boxes if present.
	 * 
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsStr = "RESUME";
		return lsStr;
	}

}
