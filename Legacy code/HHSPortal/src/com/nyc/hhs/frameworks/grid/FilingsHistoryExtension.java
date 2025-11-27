/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.model.ApplicationAuditBean;

/**
 * This class is used to generate an extension which creates a drop down for
 * provider users.
 * 
 */

public class FilingsHistoryExtension implements DecoratorInterface
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
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		ApplicationAuditBean loApplicationAuditBean = (ApplicationAuditBean) aoEachObject;
		String lsControl = "";
		//Start Emergency Build 4.0.1 defect 8365
		if (((loApplicationAuditBean.getMsEntityIdentifier().equalsIgnoreCase("New Filing") && loApplicationAuditBean.getMsEventname().equalsIgnoreCase("Status Changed"))
				|| (loApplicationAuditBean.getMsEntityIdentifier().contains("Business Application: Filings") && loApplicationAuditBean.getMsEventname().equalsIgnoreCase("Update Document Status")
						&& !loApplicationAuditBean.getMsData().equalsIgnoreCase("Status Changed from ' ' To 'Returned'")
						)  ) && loApplicationAuditBean.getMsEntityId()!=null )
		{
			lsControl = "<a href=\"javascript:void(0)\" onclick=\"javascript: viewDocument(" + "'" + loApplicationAuditBean.getMsEntityId() + "' );\">" + loApplicationAuditBean.getMsEntityIdentifier() + "</a>";
		}
		//End Emergency Build 4.0.1  defect 8365
		else
		{
			lsControl = loApplicationAuditBean.getMsEntityIdentifier();
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol
	 *            a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}
}
