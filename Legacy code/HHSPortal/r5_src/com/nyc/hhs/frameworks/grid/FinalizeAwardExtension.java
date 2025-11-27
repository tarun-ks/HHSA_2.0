package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.EvaluationBean;

/**
 * This class is added for release 5 This class is used to generate html code
 * for a particular column of table depending upon the input column name
 */
public class FinalizeAwardExtension implements DecoratorInterface
{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		StringBuffer loControl = new StringBuffer();
		EvaluationBean loEvaluationBean = (EvaluationBean) aoEachObject;
		if (HHSR5Constants.EXT_EPIN.equals(aoCol.getColumnName()))
		{
			if (null == loEvaluationBean.getExtEpin())
			{
				loControl.append(HHSConstants.PENDING);
			}
			else
			{
				loControl.append(loEvaluationBean.getExtEpin());
			}
		}
		else if (HHSConstants.EXT_CT_NUMBER.equals(aoCol.getColumnName()))
		{
			if (null == loEvaluationBean.getExtCtNumber())
			{
				loControl.append(HHSConstants.PENDING);
			}
			else
			{
				loControl.append(loEvaluationBean.getExtCtNumber());
			}
		}
		else if (HHSR5Constants.NEGOTIATED_AMOUNT.equals(aoCol.getColumnName()))
		{
			if (null == loEvaluationBean.getNegotiatedAmount())
			{
				loControl.append("<label>" + HHSConstants.PENDING + "</label>");
			}
			else
			{
				loControl.append("<label class='tableAwardAmount'>" + loEvaluationBean.getNegotiatedAmount()
						+ "</label></a>");
			}
		}
		return loControl.toString();
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
