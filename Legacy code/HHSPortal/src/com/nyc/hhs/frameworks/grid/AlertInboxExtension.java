package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.AlertInboxBean;

/**
 * This class generates an extension element which creates check-boxes so as to
 * select the required alerts.
 * 
 */

public class AlertInboxExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject
	 *            an object of list to be displayed in grid
	 * @param aoCol
	 *            a column object
	 * @param aiSeqNo
	 *            an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		AlertInboxBean loAlertInboxBean = (AlertInboxBean) aoEachObject;
		String lsControl = "";
		if ("msNotificationName".equalsIgnoreCase(aoCol.getColumnName()))
		{
			lsControl = "<input type=checkbox name=check id=checkbox" + aiSeqNo + " value=" + loAlertInboxBean.getMsNotificationId()
					+ " onclick=\"javascript:enabledisablebutton()\"/>";
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * @param aoCol
	 *            a column object
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsScript = "";
		lsScript = "<input type=checkbox name=selectAll id=selectAll value=selectAll onClick=\"javascript: selectAllCheck()\"/>";
		return lsScript;
	}

}
