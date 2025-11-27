package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.util.DateUtil;

/**
 * This class will be used to create custom select element for document grid
 * view
 * 
 */
public class ProposalSummaryDateExtension implements DecoratorInterface
{
	/**
	 * This method is used to display date in MM/DD/YYYY format in proposal
	 * summary table.
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aoSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = (ProposalDetailsBean) aoEachObject;
		String lsControl = DateUtil.getDateMMddYYYYFormat(loProposalDetailsBean.getModifiedDate());
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

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}
}
