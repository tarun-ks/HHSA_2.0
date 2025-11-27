package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.AwardsContractSummaryBean;
import com.nyc.hhs.util.PropertyLoader;

public class AwardsandContractsSummaryExtension implements DecoratorInterface
{
   /**
    * This method gets Control For Column
    * @param aoEachObject EachObject
    * @param aoCol Column
    * @param aoSeqNo Sequence Number
    */
	
	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		AwardsContractSummaryBean loAwardsContractSummaryBean = (AwardsContractSummaryBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		if(null != loAwardsContractSummaryBean.getEvalPoolStatus() && loAwardsContractSummaryBean.getEvalPoolStatus().equalsIgnoreCase(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
						HHSConstants.STATUS_COMPETITION_POOL_CANCELLED))){
			lsControl.append(loAwardsContractSummaryBean.getCompetitionPoolTitle());
	
		}
		else
		{
				lsControl.append("<a href=\"javascript: openAwardAndContract(")
				.append(loAwardsContractSummaryBean.getEvaluationPoolMappingId()).append(");\">")
				.append(loAwardsContractSummaryBean.getCompetitionPoolTitle()).append("</a>");
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
