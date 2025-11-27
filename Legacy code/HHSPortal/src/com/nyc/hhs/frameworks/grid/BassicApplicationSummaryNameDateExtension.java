package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.BusinessApplicationSummary;

/**
 * This class generates an extension that will create links corresponding 
 * to the documents.
 *
 */

public class BassicApplicationSummaryNameDateExtension implements DecoratorInterface {

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
	public String getControlForColumn(Object aoEachObject, Column aoCol,
			Integer aoSeqNo) {
		BusinessApplicationSummary loBuisinessAppSummary = null;
		if(aoEachObject != null)
		{
			loBuisinessAppSummary = (BusinessApplicationSummary) aoEachObject;
		} 
		String lsControl=null;
		if(loBuisinessAppSummary!=null && loBuisinessAppSummary.getMsStatus().equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)){
			lsControl = "";
		}
		else
		{
			if(loBuisinessAppSummary != null)
			{
				lsControl = loBuisinessAppSummary.getMsModifiedDate();
			}
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

		String lsStr = "RESUME"; 
		return lsStr;
	}


}
