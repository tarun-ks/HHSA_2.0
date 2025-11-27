package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.StaffDetails;

/**
 * This class generates an extension which will be used to create custom select
 * element for staff grid view
 * 
 */

public class DocumentServiceSummaryStaffExtension implements DecoratorInterface
{
	/**
	 * This method is used to show drop down on the service question page having
	 * staff grid
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo)
	{
		StaffDetails loStaffDetails = (StaffDetails) aoEachObject;
		String lsReadOnly = "";
		if (null != loStaffDetails)
		{
			lsReadOnly = loStaffDetails.getReadOnly();
		}
		String lsCtrl = "";
		if (lsReadOnly != null && !lsReadOnly.equalsIgnoreCase("disabled=disabled"))
		{
			if (loStaffDetails != null
					&& (null != loStaffDetails.getMsStaffId() && !loStaffDetails.getMsStaffId().isEmpty()))
			{
				lsCtrl = "<select "
						+ " name=action"
						+ aoSeqNo
						+ "  "
						+ " class='serviceSummaryDoc' style='width: 231px' onChange=\"javascript: editOrRemoveDocumentStaff('"
						+ loStaffDetails.getMsOrgId() + "','" + loStaffDetails.getMsStaffId()
						+ "',this)\"><option value=I need to... >I need to...</option>"
						+ "<option value=edit>Edit Staff</option>" + "<option value=remove>Delete Staff</option>"
						+ "</select>";
			}
		}
		else
		{
			if (loStaffDetails != null
					&& (null != loStaffDetails.getMsStaffId() && !loStaffDetails.getMsStaffId().isEmpty()))
			{
				lsCtrl = "<a "
						+ " name=action"
						+ aoSeqNo
						+ "  "
						+ " class='serviceSummaryDoc' style='width: 231px; cursor:pointer' onClick=\"javascript: editOrRemoveDocumentStaff('"
						+ loStaffDetails.getMsOrgId() + "','" + loStaffDetails.getMsStaffId() + "',this)\">View Staff"
						+ "</a>";
			}
		}
		return lsCtrl;
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
		String lsStrMsg = "RESUME";
		return lsStrMsg;
	}

}
