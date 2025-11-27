package com.nyc.hhs.frameworks.sessiongrid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

/**
 * This class generates an extension that creates link to assign tasks to the
 * users.
 * 
 */

public class ReserveJobExtension implements DecoratorInterface
{

	/**
	 * This Method is used to generate the reserve link on the task list page.
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
		boolean lbIsManagerReserveStep = loTaskQueue.isMbIsManagerReviewStep();
		boolean lbIsManagerRole = loTaskQueue.isMbManagerRole();
		String lsAssignedTo = loTaskQueue.getMsAssignedTo();
		boolean lbReserveFlag = false;

		if (lsAssignedTo.equalsIgnoreCase(P8Constants.PE_TASK_UNASSIGNED)
				|| lsAssignedTo.equalsIgnoreCase(P8Constants.PE_TASK_UNASSIGNED_MANAGER)
				|| lsAssignedTo.equalsIgnoreCase("Unassigned - Accelerator Manager"))
		{
			if (lbIsManagerReserveStep == true)
			{
				if (lbIsManagerRole == true)
				{
					lbReserveFlag = true;
				}
				else
				{
					lbReserveFlag = false;
				}
			}
			else
			{
				lbReserveFlag = true;
			}
		}

		if (lbReserveFlag == true)
		{
			if (null != loTaskQueue.getMsTaskName()
					&& loTaskQueue.getMsTaskName().equalsIgnoreCase(ApplicationConstants.TASK_NAME_APPROVE_AWARD))
			{
				lsControl = "<a href=\"#\" title='Reserve' onclick=\"javascript: reserveTask(" + "'"
						+ loTaskQueue.getMsWobNumber() + "','" + loTaskQueue.getMsTaskName() + "');\">" + "Reserve"
						+ "</a>";
			}
			else
			{
				lsControl = "<a href=\"#\" title='Reserve' onclick=\"javascript: reserveJob(" + "'"
						+ loTaskQueue.getMsWobNumber() + "');\">" + "Reserve" + "</a>";
			}
		}
		else
		{
			lsControl = "";
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
		String lsControl = "RESUME";
		return lsControl;
	}

}
