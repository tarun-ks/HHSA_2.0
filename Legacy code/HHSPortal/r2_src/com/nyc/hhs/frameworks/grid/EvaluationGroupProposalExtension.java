package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationGroupsProposalBean;

public class EvaluationGroupProposalExtension implements DecoratorInterface
{
	/**
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationGroupsProposalBean loEvaluationGroupsProposalBean = (EvaluationGroupsProposalBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		if (aoCol.getColumnName().equalsIgnoreCase("evaluationGroupTitle"))
		{
			if (!loEvaluationGroupsProposalBean.getEvaluationStatus().equalsIgnoreCase(HHSConstants.NO_PROPOSALS_RECEIVED))
			{
				lsControl.append("<a href=\"javascript: openEvaluationGroup("
						+ loEvaluationGroupsProposalBean.getEvaluationGroupId() + ");\">"
						+ loEvaluationGroupsProposalBean.getEvaluationGroupTitle() + "</a>");
			}
			else
			{
				lsControl.append(loEvaluationGroupsProposalBean.getEvaluationGroupTitle());
			}
		}
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
		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}
}
