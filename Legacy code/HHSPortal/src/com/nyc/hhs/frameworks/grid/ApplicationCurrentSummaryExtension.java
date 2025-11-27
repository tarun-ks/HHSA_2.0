package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.util.DateUtil;
/**
 * This class generates an extension which fetches data in the Grid
 * for the CurrentStatusSummary table
 *
 */

public class ApplicationCurrentSummaryExtension implements DecoratorInterface{
	
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
	public String getControlForColumn(Object aoEachObject, Column aoCol,Integer aoSeqNo) throws ApplicationException {
		ApplicationSummary loApplicationSummary=(ApplicationSummary) aoEachObject;		
		String lsColName=aoCol.getColumnName();
		String lsControl="";
		String lsStatus="";
		
		if("mdAppExpirationDate".equalsIgnoreCase(lsColName))
		{
			
			if("Business".equalsIgnoreCase(loApplicationSummary.getMsAppType())  
					&& "Approved".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
			{
				lsControl=DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdAppExpirationDate());
			}
			else if(loApplicationSummary.getMsAppType().equalsIgnoreCase("Business"))
			{
				lsControl="NA";
			}
			
		}
		if("msAppStatus".equalsIgnoreCase(lsColName))		
		{
			if("Approved".equalsIgnoreCase(loApplicationSummary.getMsWithdrawanStatus()))
			{
				lsStatus="Withdrawan";
			}
			else
			{
				lsStatus=loApplicationSummary.getMsAppStatus();
			}
			
			lsControl=lsStatus;
		}
	
		return lsControl;		
 }
}
