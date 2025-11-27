package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.model.ContractDetails;

/**
 * This class generates an extension which will be used to create custom select
 * element for contract grid view
 * 
 */

public class DocumentServiceSummaryContractExtension implements DecoratorInterface
{
	/**
	 * This method is used to show drop down on the service question page having
	 * funder grid
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo)
	{
		ContractDetails loContractDetails = (ContractDetails) aoEachObject;
		String lsReadOnly = null;
		if (null != loContractDetails)
		{
			lsReadOnly = loContractDetails.getReadOnly();
		}
		String lsControl = "";
		if (lsReadOnly != null && !lsReadOnly.equalsIgnoreCase("disabled=disabled"))
		{
			if (loContractDetails != null
					&& (null != loContractDetails.getMsContractDetailsId() && !loContractDetails
							.getMsContractDetailsId().isEmpty()))
			{
				lsControl = "<select " + " name=action" + aoSeqNo + " class='serviceSummaryDoc'  "
						+ " style='width: 231px' onChange=\"javascript: editOrRemoveDocumentContract('"
						+ loContractDetails.getMsOrgId() + "','" + loContractDetails.getMsContractDetailsId()
						+ "',this)\"><option value=I need to... >I need to...</option>"
						+ "<option value=edit>Edit Funder</option>" + "<option value=remove>Delete Funder</option>"
						+ "</select>";
			}
		}
		else
		{
			if (loContractDetails != null
					&& (null != loContractDetails.getMsContractDetailsId() && !loContractDetails
							.getMsContractDetailsId().isEmpty()))
			{
				lsControl = "<a " + " name=action" + aoSeqNo + " class='serviceSummaryDoc'  "
						+ " style='width: 231px;cursor:pointer' onclick=\"javascript: editOrRemoveDocumentContract('"
						+ loContractDetails.getMsOrgId() + "','" + loContractDetails.getMsContractDetailsId()
						+ "',this)\">View Funder" + "</a>";
			}
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * @param aoCol a column object
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsSrControl = "RESUME";
		return lsSrControl;
	}

}
