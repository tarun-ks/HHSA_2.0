package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.util.PropertyLoader;

/**
 * The Class will be called while rendering Evaluation Status Screen
 * 
 */
public class EvaluationCompletedExtension implements DecoratorInterface
{
	/**
	 * The Method will check if there is any Proposal in state Non Responsive ,
	 * will display Evaluation Completed as underscore else will display exact
	 * value
	 * @param aoEachObject Object
	 * @param aoCol Column
	 * @param aoSeqNo Sequence Number
	 * @return lsControl String
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;
		String lsControl = HHSConstants.EMPTY_STRING;
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.EVALUATIONS_COMPLETED))
		{
			if (loEvaluationBean.getProposalStatusId() == Integer.parseInt(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE)))
			{
				lsControl = HHSConstants.DOUBLE_UNDER_SCORE;
			}
			else
			{
				lsControl = loEvaluationBean.getEvalutionsCompleted().toString();
			}
		}
		return lsControl;
	}

	/**
	 * @param aoCol Column
	 * @return lsControl string
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

}
