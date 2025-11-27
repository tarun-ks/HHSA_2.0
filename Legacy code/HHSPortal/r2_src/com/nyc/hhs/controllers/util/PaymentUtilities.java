package com.nyc.hhs.controllers.util;

import java.util.Calendar;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.PaymentBean;

/**
 * This is utility class of payment module.
 * 
 */

public class PaymentUtilities
{

	/**
	 * LogInfo Object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PaymentUtilities.class);

	/**
	 * This method get drop down list of Period
	 * 
	 * @param aoPaymentHeaderDetail - PaymentBean object
	 * @return loStringBuffer.toString() - String
	 */
	public static String getPeriodDropDownList(PaymentBean aoPaymentHeaderDetail)
	{

		long loCurrFiscalYearId = Long.parseLong(aoPaymentHeaderDetail.getFiscalYearId());
		long loBudgetFiscalYearId = Long.parseLong(aoPaymentHeaderDetail.getBudgetFYId());
		StringBuffer loStringBuffer = new StringBuffer();
		try
		{
			Calendar loCal = Calendar.getInstance();
			int liCurrMonth = loCal.get(Calendar.MONTH) + HHSConstants.INT_ONE;
			int liFiscalMonth = HHSConstants.INT_ONE;

			if (liCurrMonth > HHSConstants.INT_SIX)
			{
				liFiscalMonth = liCurrMonth - HHSConstants.INT_SIX;
			}
			else
			{
				liFiscalMonth = liCurrMonth + HHSConstants.INT_SIX;
			}

			String lsMonth = HHSConstants.STRING_ZERO_ONE;
			for (int liItrMonth = HHSConstants.INT_ONE; liItrMonth <= HHSConstants.INT_THIRTEN; liItrMonth++)
			{
				if (liItrMonth < HHSConstants.INT_TEN)
				{
					lsMonth = HHSConstants.STRING_ZERO + liItrMonth;
				}
				else
				{
					lsMonth = HHSConstants.EMPTY_STRING + liItrMonth;
				}

				if (loCurrFiscalYearId > loBudgetFiscalYearId && liItrMonth == HHSConstants.INT_THIRTEN)
				{

					loStringBuffer.append("<option value=" + HHSConstants.INT_THIRTEN + " selected='selected'>"
							+ HHSConstants.INT_THIRTEN + "</option>");

				}
				else if ((liFiscalMonth == liItrMonth) && (loCurrFiscalYearId <= loBudgetFiscalYearId))
				{

					loStringBuffer.append("<option value=" + lsMonth + " selected>" + lsMonth + "</option>");

				}
				else
				{

					loStringBuffer.append("<option value=" + lsMonth + ">" + lsMonth + "</option>");

				}
			}
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured in while creating Period drop down list : ", aoEx);
		}

		return loStringBuffer.toString();
	}

}
