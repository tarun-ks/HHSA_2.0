package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationBean;

public class EvaluationPropsalTitleExtenstion implements DecoratorInterface
{

	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;
		String lsControl = HHSConstants.EMPTY_STRING;
		if (aoCol.getColumnName().equalsIgnoreCase(HHSConstants.PROPOSAL_TITLE))
		{
			lsControl = "<a href=\"#\" title='Reserve' onclick=\"javascript: reserveJob(" + "'"
					+ loEvaluationBean.getProposalTitle() + "');\">" + "Reserve" + "</a>";
		}
		return lsControl;
	}

	/**
	 * @param aoCol
	 * @return
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

}
