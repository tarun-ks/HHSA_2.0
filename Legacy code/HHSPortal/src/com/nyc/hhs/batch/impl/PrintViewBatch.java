package com.nyc.hhs.batch.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.PrintViewGenerationBean;

/**
 * This class is being used for running batches for Print View Generation. It
 * reads entries from the table PRINT_VIEW_GENERATION and calls the
 * uploadPrintVersionInDVForBatch() function for HHSComponentService class If
 * the print view document is successfully created, then it updated the column
 * IS_PRINT_VIEW_GENERATED TO 1 for the same row in the table
 * PRINT_VIEW_GENERATION
 */

public class PrintViewBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PrintViewBatch.class);

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	public List<PrintViewGenerationBean> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * 
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		List<PrintViewGenerationBean> loPrintViewBeanList = null;
		LOG_OBJECT.Debug("Entered transaction for fetchPrintViewGeneration");
	
		Channel loChannelObj = new Channel();
		try
		{
		
			// Fetch entries for print view generation
			LOG_OBJECT.Debug("Starting transaction for fetchPrintViewGeneration");
			TransactionManager.executeTransaction(loChannelObj, "fetchPrintViewGeneration");
			LOG_OBJECT.Debug("Finished transaction fetchPrintViewGeneration");
			loPrintViewBeanList = (List<PrintViewGenerationBean>) loChannelObj.getData("printViewDetails");
			Iterator<PrintViewGenerationBean> loIterator = loPrintViewBeanList.iterator();
		
			while (loIterator.hasNext())
			{
				callPrintViewComponent(loIterator);
			} // wend
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in PrintViewBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Finished transaction for fetchPrintViewGeneration");
	} // end function executeQueue

	/**
	 * This method call txid : callPrintViewComponent
	 * 
	 * @param loIterator
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void callPrintViewComponent(Iterator<PrintViewGenerationBean> loIterator)
	{
		PrintViewGenerationBean loPrintViewBean;
		Channel loChannelObj;
		try
		{
			loPrintViewBean = loIterator.next();
			String lsProviderId = loPrintViewBean.getOrgId();
			String lsPrintViewId = loPrintViewBean.getPrintViewId();
			String lsTaskType = loPrintViewBean.getTaskType();
			Timestamp ldModifiedDate = loPrintViewBean.getMsModifiedDate();
			if (null != lsProviderId)
			{
				lsProviderId = lsProviderId.trim();
			}
			if (null != lsPrintViewId)
			{
				lsPrintViewId = lsPrintViewId.trim();
			}
			if (null != lsTaskType)
			{
				lsTaskType = lsTaskType.trim();
			}
			loChannelObj = new Channel();
			HashMap loHMap = new HashMap();
			loHMap.put("lsPrintViewId", lsPrintViewId);
			loHMap.put("lsProviderId", lsProviderId);
			loHMap.put("lsTaskType", lsTaskType);
			loHMap.put("ldModifiedDate", ldModifiedDate);
			loChannelObj.setData("loHMap", loHMap);
			// call print-view component
			LOG_OBJECT.Debug("Calling transaction callPrintViewComponent");
			TransactionManager.executeTransaction(loChannelObj, "callPrintViewComponent");
			Boolean loDocumentCreated = (Boolean) loChannelObj.getData("lbdocCreated");
			LOG_OBJECT
					.Debug("Finished transaction callPrintViewComponent. Returned lbdocCreated =" + loDocumentCreated);
			if (loDocumentCreated == true)
			{
				// update table PRINT_VIEW_GENERATION , set
				// IS_PRINT_GENERATED to 1 for that printviewid, orgid
				// and tasktype
				LOG_OBJECT.Debug("Calling transaction updatePrintViewGenerated");
				TransactionManager.executeTransaction(loChannelObj, "updatePrintViewGenerated");
				LOG_OBJECT.Debug("Finish  transaction updatePrintViewGenerated");
			}
			else
			{
				LOG_OBJECT.Debug("Error while generating print-view for " + lsPrintViewId);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in PrintViewBatch.executeQueue()", aoAppEx);
		}
	}

}