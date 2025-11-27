package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.StaffDetails;

/**
 * This class generates an extension which will be used to create custom select
 * element for staff grid view
 * 
 */

public class StaffNameExtension implements DecoratorInterface
{
	/**This method is used to show drop down on the service question page having staff grid
	 * @param aoEachObject 
	 * 				an object of list to be displayed in grid
	 * @param aoCol  
	 * 				a column object
	 * @param aoSeqNo 
	 * 				an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */
	
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo)
	{
		StaffDetails loStaffDetails = (StaffDetails) aoEachObject;
		String lsControl = "<div class='wordWrap'>"+loStaffDetails.getMsStaffFirstName()+"</div>";
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
		String lsStr = "RESUME";
		return lsStr;
	}

}
