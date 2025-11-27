package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApprovedProvidersBean;
import com.nyc.hhs.util.DateUtil;

public class OrganizationNameExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ApprovedProvidersBean loProvidersBean = (ApprovedProvidersBean) aoEachObject;
		String lsControl = HHSConstants.EMPTY_STRING;
		if (HHSConstants.ORG_LEGAL_NAME.equals(aoCol.getColumnName()))
		{
			lsControl = "<a href=\"javascript: viewOrganizationSummary(" + "'" + loProvidersBean.getOrganizationId()
					+ "' );\">" + loProvidersBean.getOrganizationLegalName() + "</a>";
		}
		else if (HHSConstants.BA_EXP_DATE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loProvidersBean.getBaExpDate())
			{
				lsControl = DateUtil.getDateMMddYYYYFormat(loProvidersBean.getBaExpDate());
			}
			else
			{
				lsControl = HHSConstants.NA_KEY;
			}
		}
		else if (HHSConstants.FILLING_EXP_DATE.equalsIgnoreCase(aoCol.getColumnName()))
		{
			if (null != loProvidersBean.getFilingExpDate())
			{
				lsControl = DateUtil.getDateMMddYYYYFormat(loProvidersBean.getFilingExpDate());
			}
			else
			{
				lsControl = HHSConstants.NA_KEY;
			}
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
		return HHSConstants.RESUME;
	}
}
