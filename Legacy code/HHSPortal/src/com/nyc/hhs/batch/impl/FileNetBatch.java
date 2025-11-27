package com.nyc.hhs.batch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

public class FileNetBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(FileNetBatch.class);
	 /**
		 * This method is used to get batch Queue
		 * 
		 * @param aoMParameters
		 * @return loQueue List
		 */
	@Override
	public List getQueue(Map aoMParameters)
	{

		String[] args = (String[]) aoMParameters.get("argumentsVal");
		List<String> loQueue = new ArrayList<String>();
		for (int i = 1; i < args.length; i++)
		{
			loQueue.add(args[i]);
		}
		return loQueue;
	}
	 /**
	 * This method is used to execute batch Queue
	 * 
	 * @param aoLQueue List
	 * @throws ApplicationException
	 */
	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		Channel loChannel = new Channel();
		int lorowInserted = 0;
		P8UserSession loP8UserSession = new P8UserSession();
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		LOG_OBJECT.Debug("Batch Started");
		try
		{
			loP8UserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());
			loChannel.setData("loUserSession", loP8UserSession);
			loChannel.setData("queueElements", aoLQueue);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.FILENET_BATCH,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			lorowInserted = (Integer) loChannel.getData(HHSConstants.ARGUMENT_ROWS_INSERTED);
			LOG_OBJECT.Debug("Row Inserted in FOLDER_MAPPING count is:::" + lorowInserted);

		}
		catch (ApplicationException apAppEx)
		{
			LOG_OBJECT.Error("Error while Executing FileNetBatch.executeQueue()", apAppEx);
			throw apAppEx;
		}
		catch (Exception apAppEx)
		{
			LOG_OBJECT.Error("Error while Executing FileNetBatch.executeQueue()", apAppEx);
			throw new ApplicationException("Error while Executing FileNetBatch.executeQueue()", apAppEx);
		}
	}

}
