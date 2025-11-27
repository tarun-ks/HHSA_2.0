package com.nyc.hhs.batch.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;

public class UpdateAccountingLineBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(UpdateAccountingLineBatch.class);

	/**
	 * This method is the entry point for the execution of the batch. it will
	 * start execution of the Payment Batch for interim period.
	 */
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in UpdateAccountingLineBatch.executeQueue method ::");
		Channel loChannel = new Channel();
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.SELECT_MAX_INTERIM_PERIOD_END_DATE);
			Date loMaxInterimPeriodEndDate = (Date)loChannel.getData(HHSConstants.AO_MAX_INTERIM_PERIOD_END_DATE);
			Calendar loCalendar = Calendar.getInstance();
			Date loSystemDate = loCalendar.getTime();
			if(loSystemDate.compareTo(loMaxInterimPeriodEndDate) > 0){
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.UPDATE_ACCOUNTING_LINE);
			}
			LOG_OBJECT.Info("UpdateAccountingLineBatch Batch completed sucessfully ::");
			
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in UpdateAccountingLineBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in UpdateAccountingLineBatch.executeQueue()", aoExp);
			throw new ApplicationException("Exception in UpdateAccountingLineBatch.executeQueue()", aoExp);
		}
	}

	/**
	 * This method is used to get queue
	 * It returns null
	 */
	@Override
	public List getQueue(Map aoMParameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
