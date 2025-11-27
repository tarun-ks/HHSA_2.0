package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.AgencyTaskBean;

public class AgencyTaskExtention
{
	/**Modified by- Tanuj
	 * Change This Method for enhancement 5678. Fixed with Release 2.7.0
	 * Changed Name of task "Advance Payment Request" to "Advance Request Review"
	 * 
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
		AgencyTaskBean loTaskQueue = (AgencyTaskBean) aoEachObject;
		String lsControl = "";
		if ("wobNumber".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<input type=checkbox name=check id=check value=" + loTaskQueue.getWobNumber() + "#"
					+ loTaskQueue.getTaskName() + "#" + loTaskQueue.getEntityId()
					+ " onClick=\"javascript: enableSubmit()\"/>";
		}
		else
		{
			//Start || Change done for enhancement 6534 with Release 3.8.0
			//Change started for enhancement 5678. Fixed with Release 2.7.0
			if (loTaskQueue.getTaskName()!=null && loTaskQueue.getTaskName().equalsIgnoreCase(HHSConstants.TASK_ADVANCE_REVIEW))
			{
				lsControl = "<a href=\"javascript:;\" title='" + loTaskQueue.getTaskName()
				+ "' id='awardTaskName' onclick=\"javascript: submitForm(" + "'" + loTaskQueue.getWobNumber() + "','"  + loTaskQueue.getTaskName() + "','"  + loTaskQueue.getAgencyId() + "');\">" + HHSConstants.ADVANCE_REQUEST_REVIEW + "</a>";
			}
			//Change ended for enhancement 5678. Fixed with Release 2.7.0
			else
			{
				lsControl = "<a href=\"javascript:;\" title='" + loTaskQueue.getTaskName()
					+ "' id='awardTaskName' onclick=\"javascript: submitForm(" + "'" + loTaskQueue.getWobNumber() + "','"  + loTaskQueue.getTaskName() + "','"  + loTaskQueue.getAgencyId() + "');\">" + loTaskQueue.getTaskName() + "</a>";
			}
			//End || Change done for enhancement 6534 with Release 3.8.0
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControl = "RESUME";
		return lsControl;
	}
}
