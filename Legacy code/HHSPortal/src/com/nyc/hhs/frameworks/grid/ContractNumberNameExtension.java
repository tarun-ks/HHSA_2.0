package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.ContractDetails;

/**
 * This class generates an extension which will be used to create custom select
 * element for contract grid view
 * 
 */

public class ContractNumberNameExtension implements DecoratorInterface
{
	/**This method is used to show drop down on the service question page having funder grid
	 * @param aoEachObject 
	 * 					an object of list to be displayed in grid
	 * @param aoCol  
	 * 					a column object
	 * @param aoSeqNo 	
	 * 					an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo)
	{
		ContractDetails loContractDetails = (ContractDetails) aoEachObject;
		String lsControl = "<div class='wordWrap'>"+loContractDetails.getMsContractID()+"</div>";
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
		String lsResume = "RESUME";
		return lsResume;
	}

}
