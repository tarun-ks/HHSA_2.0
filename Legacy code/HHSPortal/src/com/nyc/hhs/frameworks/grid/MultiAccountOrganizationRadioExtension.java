package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.StaffDetails;

public class MultiAccountOrganizationRadioExtension implements DecoratorInterface
{

	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		StaffDetails loStaffDetails = (StaffDetails) aoEachObject;
		String lsUserOrg = loStaffDetails.getMsOrgId();
		String lsUserOrgName = loStaffDetails.getMsOrganisationName();
		String lsControlMsg = "<input type=\"radio\" name=\"orgDetails\" value=\""+lsUserOrg+ApplicationConstants.KEY_SEPARATOR+lsUserOrgName+"\">";
		return lsControlMsg;
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
