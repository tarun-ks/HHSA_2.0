package com.nyc.hhs.batch.impl;

import java.util.HashMap;
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


public class LinkageConsistencyBatch extends P8HelperServices implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(LinkageConsistencyBatch.class);
	@Override
	public List getQueue(Map aoMParameters)
	{
		return null;
	}


	 /** 
	  * This Method is used to execute Queue.
	  * <ul>
	  * <li>create FilenetSession object and set data into channel </li>
	  * <li>Execute transaction id <b> linkageConsistency</b></li>
	  * </ul>
	  * @throws ApplicationException
	  * */
	@SuppressWarnings("unchecked")
	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		HashMap<String,Object> loMap = new HashMap<String,Object>();
		try
		{
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			Channel loChannel = new Channel();
			loChannel.setData("aoFilenetSession", loFilenetSession);
			HHSTransactionManager.executeTransaction(loChannel, "linkageConsistency",
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			
			loMap = (HashMap<String,Object>)loChannel.getData("loDbMap");
			int lsCountUpdatedInFilenet = (Integer)loMap.get("rowCount");
			LOG_OBJECT.Error("count updated for false flag in filenet is:::"+lsCountUpdatedInFilenet);
		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Exception", aoExp);
		}

	}

}
