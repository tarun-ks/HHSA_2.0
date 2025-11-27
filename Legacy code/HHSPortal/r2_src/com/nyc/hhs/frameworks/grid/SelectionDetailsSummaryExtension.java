/**
 * Class added in R4
 * This is a extension class for Selection Detail summary.
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.SelectionDetailsSummaryBean;

public class SelectionDetailsSummaryExtension implements DecoratorInterface
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nyc.hhs.frameworks.grid.DecoratorInterface#getControlForColumn(java
	 * .lang.Object, com.nyc.hhs.frameworks.grid.Column, java.lang.Integer)
	 */
	/**
	 * This method is used to get control for column
	 */
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		SelectionDetailsSummaryBean loSelectionDetailsSummaryBean = (SelectionDetailsSummaryBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.COMPETITION_POOL_TITLE))
		{
			lsControl.append("<a href=\"javascript: viewSelectionDetails(");
			lsControl.append(loSelectionDetailsSummaryBean.getEvaluationPoolMapingId());
			lsControl.append(",");
			lsControl.append(loSelectionDetailsSummaryBean.getProcurementId());
			lsControl.append(");\">");
			lsControl.append(loSelectionDetailsSummaryBean.getCompetitionPoolTitle());
			lsControl.append("</a>");
		}
		// R5 changes starts
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.AMOUNT))
		{
			if (HHSR5Constants.STATUS_NEGOTIATION_LIST.contains(loSelectionDetailsSummaryBean.getStatus()))
			{
				lsControl.append(HHSConstants.PENDING);
			}
			else
			{
				lsControl.append(loSelectionDetailsSummaryBean.getAmount());
			}
		}
		else if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.CONTRACT_STATUS))
		{
			if (HHSR5Constants.STATUS_NEGOTIATION_LIST.contains(loSelectionDetailsSummaryBean.getStatus()))
			{
				lsControl.append("Pending Final Award Amount");
			}
			else
			{
				lsControl.append(loSelectionDetailsSummaryBean.getContractStatus());
			}
		}
		// R5 changes ends
		return lsControl.toString();
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
