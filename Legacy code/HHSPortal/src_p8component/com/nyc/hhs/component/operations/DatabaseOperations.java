package com.nyc.hhs.component.operations;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.component.HHSComponentOperations;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AssignementDetailsBean;

public class DatabaseOperations
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSComponentOperations.class);

	/**
	 * Finds the number of assignments where the amount is > 0 and for the given
	 * invoice id Returns the number of assignments.
	 * @param asInvoiceId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<AssignementDetailsBean> fetchAssignments(String asInvoiceId, String asBudgetId, String asBudgetAdvanceId)
			throws ApplicationException
	{
		List<AssignementDetailsBean> loAssignmentList = null;
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.INVOICE_ID, asInvoiceId);
		loInputParam.put(HHSP8Constants.BUDGET_ID, asBudgetId);
		loInputParam.put(HHSP8Constants.BUDGET_ADVANCE_ID, asBudgetAdvanceId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_ASSIGNMENTS);
			loAssignmentList = (List<AssignementDetailsBean>) loChannel.getData(HHSP8Constants.ASSIGNMENT_LIST);

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the fetchNumberOfAssignments method in DatabaseOperations", aoAppEx);
		}
		return loAssignmentList;
	}

	/**
	 * Finds the Amount
	 * @param asWorkFlow
	 * @param asInvoiceId
	 * @param asBudgetAdvanceId
	 * @return
	 * @throws ApplicationException
	 */
	public Double fetchAmount(String asWorkFlow, String asInvoiceId, String asBudgetAdvanceId)
			throws ApplicationException
	{
		Double ldAmount = null;
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.WORKFLOW, asWorkFlow);
		loInputParam.put(HHSP8Constants.INVOICE_ID, asInvoiceId);
		loInputParam.put(HHSP8Constants.BUDGET_ADVANCE_ID, asBudgetAdvanceId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_AMOUNT);
			ldAmount = (Double) loChannel.getData(HHSP8Constants.AMOUNT);

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the fetchAmount method in DatabaseOperations", aoAppEx);
		}
		return ldAmount;
	}

	/**
	 * Inserts the record into the payment table with the required information.
	 * 
	 * @param loHmWFProperties
	 * @throws ApplicationException
	 */
	public String insertIntoPayment(HashMap<String, String> loHmWFProperties) throws ApplicationException
	{
		Channel loChannel = new Channel();
		String lsPaymentId = null;
		try
		{
			loChannel.setData(HHSP8Constants.LOHMAP, loHmWFProperties);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.INSERT_INTO_PAYMENT);
			lsPaymentId = (String) loChannel.getData(HHSP8Constants.PAYMENT_ID);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while insertIntoPayment:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the insertIntoPayment method in HHSComponentOperations", aoAppEx);
		}

		return lsPaymentId;
	}

	/**
	 * Finds the Period
	 * @param asBudgetId
	 * @return lsPeriod
	 * @throws ApplicationException
	 */
	public String retrievePeriod(String asBudgetId) throws ApplicationException
	{

		String lsPeriod = null;
		Integer loFiscalYear = 0;
		Integer loBudgetFiscalYear = 0;
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.BUDGET_ID, asBudgetId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);
		Calendar loCal = Calendar.getInstance();
		try
		{
			loCal.add(Calendar.MONTH, 6);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_FISCAL_YEAR_ID);
			loFiscalYear = (Integer) loChannel.getData(HHSP8Constants.FISCAL_YEAR_ID);
			loBudgetFiscalYear = loCal.get(Calendar.YEAR);

			if (loFiscalYear > loBudgetFiscalYear)
			{
				lsPeriod = "13";
			}
			else
			{
				if (null == lsPeriod)
				{
					Integer loMonth = loCal.get(Calendar.MONTH);
					lsPeriod = HHSP8Constants.MONTH_REPRESENTATION_MAP.get(loMonth);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while insertIntoPayment:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the insertIntoPayment method in HHSComponentOperations", aoAppEx);
		}

		return lsPeriod;
	}

}
