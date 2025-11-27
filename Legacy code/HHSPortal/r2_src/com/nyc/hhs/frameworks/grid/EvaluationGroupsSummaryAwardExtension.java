/**
 * Class Added in R4
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationGroupAwardBean;

/**
 * @author sumit.vasudeva
 * 
 */
public class EvaluationGroupsSummaryAwardExtension implements DecoratorInterface
{
	/**
	 * This method gets Control For Column
	 * @param aoEachObject Object
	 * @param aoCol Column
	 * @param aoSeqNo Integer
	 * @return string
	 * @throws ApplicationException if any exception occurred
	 */

	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{
		EvaluationGroupAwardBean loEvaluationGroupAwardBean = (EvaluationGroupAwardBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		lsControl.append("<a href=\"javascript: openEvaluationGroupAward(")
				.append(loEvaluationGroupAwardBean.getEvaluationGroupId()).append(");\">")
				.append(loEvaluationGroupAwardBean.getEvaluationGroupTitle()).append("</a>");
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
