package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.AgencyTaskBean;

/**
 * This class generates an extension that will create check-boxes corresponding
 * to tasks.
 * 
 */

public class TaskCheckBox implements DecoratorInterface
{

	/**
	 * This Method is used to generate the CheckBoxes on the task List Pages
	 * @param aoEachObject Bean Name
	 * @param aoCol Column name
	 * @param aiSeqNo Sequence Number
	 * @return String
	 * 
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		AgencyTaskBean loTaskQueue = (AgencyTaskBean) aoEachObject;
		String lsControl = "";
		String lsStatus = loTaskQueue.getStatus();
		boolean lbFinancialTask = false;
		String lsTaskLevel = loTaskQueue.getTaskLevel();
		String lsAgencyId = loTaskQueue.getAgencyId();
		if (HHSConstants.FINANCIAL_TASK_PROCESS_ID_MAP.containsKey(loTaskQueue.getTaskName()))
		{
			lbFinancialTask = true;
		}

		if ("wobNumber".equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loTaskQueue.getTaskName() && loTaskQueue.getTaskName().equals(HHSConstants.EVALUATE_PROPOSAL))
			{
				lsControl = "<input type=checkbox class=\"taskCheckBox\" disabled=disabled name=check id=check value=\""
						+ loTaskQueue.getWobNumber()
						+ "#"
						+ loTaskQueue.getTaskName()
						+ "#"
						+ loTaskQueue.getEntityId()
						+ "#"
						+ loTaskQueue.getTaskId()
						+ "\" taskType=\""
						+ loTaskQueue.getTaskName()
						+ "\" \" financialTask=\""
						+ lbFinancialTask
						+ "\" agencyId=\""
						+ lsAgencyId + "\" taskLevel=\"" + lsTaskLevel + "\"/>";
			}
			else
			{
				lsControl = "<input type=checkbox class=\"taskCheckBox\" name=check id=check value=\""
						+ loTaskQueue.getWobNumber() + "#" + loTaskQueue.getTaskName() + "#"
						+ loTaskQueue.getEntityId() + "#" + loTaskQueue.getTaskId() + "\" taskType=\""
						+ loTaskQueue.getTaskName() + "\" \" financialTask=\"" + lbFinancialTask + "\" agencyId=\""
						+ lsAgencyId + "\" taskLevel=\"" + lsTaskLevel + "\"/>";
			}
		}
		else
		{
			lsControl = "<a href=\"#\" onclick=\"javascript: submitForm(" + "'" + loTaskQueue.getWobNumber() + "','"
					+ lsStatus + "');\">" + loTaskQueue.getTaskName() + "</a>";
		}
		return lsControl;
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * @param aoCol column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsHTML  = "<input type=checkbox name=selectAll id=selectAll value=selectAll class=\"taskCheckBox\" onClick=\"javascript: selectAllCheck()\"/>";

		return lsHTML;
	}
}
