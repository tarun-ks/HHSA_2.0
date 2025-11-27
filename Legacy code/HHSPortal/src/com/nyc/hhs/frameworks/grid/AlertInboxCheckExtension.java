package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.AlertInboxBean;

/**
 * This extension class creates a link in the Grid for viewing the complete
 * details corresponding to an alert.
 * 
 */

public class AlertInboxCheckExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		AlertInboxBean loAlertInboxBean = (AlertInboxBean) aoEachObject;
		String lsScript = "";

		if ("msNotificationName".equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loAlertInboxBean.getMsNotificationRead().equalsIgnoreCase("Y"))
			{
				lsScript = "<a href=\"#\" onclick=\"javascript: submitForm(" + loAlertInboxBean.getMsNotificationId()
						+ ");\">" + loAlertInboxBean.getMsNotificationName() + "</a>";
			}
			else
			{
				lsScript = "<a href=\"#\" onclick=\"javascript: submitForm(" + loAlertInboxBean.getMsNotificationId()
						+ ");\"><b>" + loAlertInboxBean.getMsNotificationName() + "</b></a>";
			}
		}
		else if ("msNotificationDate".equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (loAlertInboxBean.getMsNotificationRead().equalsIgnoreCase("Y"))
			{
				lsScript = loAlertInboxBean.getMsNotificationDate();
			}
			else
			{
				lsScript = "<b>" + loAlertInboxBean.getMsNotificationDate() + "</b>";
			}
		}
		return lsScript;
	}

	/**
	 * This method will generate HTML code for a particular column header of
	 * table depending upon the input column name
	 * @param aoCol a column object object
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsScript = "RESUME";
		return lsScript;
	}
}
