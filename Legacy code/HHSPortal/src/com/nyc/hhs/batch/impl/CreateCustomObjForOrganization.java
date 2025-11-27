package com.nyc.hhs.batch.impl;
/**
 * Added for Release 5
 * This class is part of filenet
 * Migration activity. It is a one time activity
 * to create object for filenet
 */
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

public class CreateCustomObjForOrganization extends P8HelperServices implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CreateCustomObjForOrganization.class);

	@Override
	public List getQueue(Map aoMParameters)
	{

		return null;
	}
	
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	/**
	 * This method takes a list as input
	 * and executes the queue
	 * loFilenetSession a filenet connection object
	 * loExp Application exception
	 */
	public void executeQueue(List aoLQueue) throws ApplicationException
	{

		Channel loChannel = new Channel();

		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loChannel.setData("aoFilenetSession", loFilenetSession);
			HHSTransactionManager.executeTransaction(loChannel, "createCustomObjInFilenet",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			Integer loCount = (Integer) loChannel.getData("loCount");
			LOG_OBJECT.Error("No. of Object Created:::" + loCount);
		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception in  executeQueue::" + loExp.getMessage());
			new ApplicationException("Exception in FileDownloadedAndZipBatch : " + loExp.getMessage());
		}
	}

}
