package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.util.PropertyLoader;

/**
 * The Class will be called while rendering Evaluation Status Screen
 * 
 */
public class EvaluationProposalTitleExtension implements DecoratorInterface
{

	/**
	 * The Method will check Procurement is in Released state will display
	 * Proposal title as plain text if its in advance stage to Released the will
	 * be display as Hyperlink else will display exact value
	 * <ul>
	 * <li>Updated Method in R4
	 * </ul>
	 * @param aoEachObject Object
	 * @param aoCol Column
	 * @param  aoSeqNo Integer
	 * @return String
	 * @throws ApplicationException If an Application Exception occurs
	 *
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;
		StringBuffer loControl = new StringBuffer();
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_TITLE))
		{
			//Start || Changes done for Enhancement #6577 for Release 3.10.0
			if (loEvaluationBean.getEvalPoolMappingStatus().equals(
					PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_COMPETITION_POOL_RELEASED)) || loEvaluationBean.getEvalPoolMappingStatus().equals(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_COMPETITION_POOL_CANCELLED)))
			{
				loControl.append(loEvaluationBean.getProposalTitle());
			}
			//End || Changes done for Enhancement #6577 for Release 3.10.0
			else
			{
				loControl.append("<a href=\"#\" ").append(" onclick=\"javascript: viewProposalDetail('")
						.append(loEvaluationBean.getProposalId()).append("' );\">")
						.append(loEvaluationBean.getProposalTitle()).append("</a>");

			}
		}
		return loControl.toString();
	}

	/**
	 * @param aoCol Column
	 * @return string
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

}
