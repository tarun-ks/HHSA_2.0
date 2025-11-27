package com.nyc.hhs.batch.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

public class UpdateAccountingLinesAsPerFmsFeed extends P8HelperServices implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(UpdateAccountingLinesAsPerFmsFeed.class);
	private Channel moChannelObj;

	/**
	 * This method is the entry point for the execution of the batch. it will
	 * start execution of the UpdateAccountingLinesAsPerFmsFeed Batch
	 */
	@SuppressWarnings("rawtypes")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in UpdateAccountingLineBatch.executeQueue method ::");
		SqlSession loFilenetPEDBSession = null;
		try
		{
			// Collect and set supporting things for DB Queries and File-net
			moChannelObj = new Channel();

			// Get Filenet session
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetSession.setFilenetPEDBSession(loFilenetPEDBSession);

			moChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
			moChannelObj.setData(HHSConstants.LB_AUTH_STATUS_FLAG, true);

			LOG_OBJECT.Info("UpdateAccountingLinesAsPerFmsFeed Batch execution begins ::");
			HHSTransactionManager.executeTransaction(moChannelObj, HHSConstants.FETCH_PAYMENTS_AND_UPDATE_FLAG);
			List<String> loPendingApprovedPaymentIdList = (List<String>) moChannelObj
					.getData(HHSConstants.PENDING_APPROVED_PAYMENT_IDS_LIST);
			if (null != loPendingApprovedPaymentIdList && !loPendingApprovedPaymentIdList.isEmpty())
			{
				LOG_OBJECT
						.Info("Entered in UpdateAccountingLineBatch.executeQueue method :: + loPendingApprovedPaymentIdList.size "
								+ loPendingApprovedPaymentIdList.size());
				moChannelObj.setData(HHSConstants.PENDING_APPROVED_PAYMENT_IDS_LIST, loPendingApprovedPaymentIdList);
				HHSTransactionManager.executeTransaction(moChannelObj,
						HHSConstants.UPDATE_ACCOUNTING_LINES_AS_PER_FMS_FEED);
				HHSTransactionManager.executeTransaction(moChannelObj, HHSConstants.UPDATE_BATCH_IN_PROGRESS_FLAG);
				LOG_OBJECT.Info("UpdateAccountingLinesAsPerFmsFeed Batch completed sucessfully ::");
				// Start changes on 8th Feb (error in logs)
			}
			// End changes on 8th Feb (error in logs)

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in UpdateAccountingLinesAsPerFmsFeed.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in UpdateAccountingLinesAsPerFmsFeed.executeQueue()", aoExp);
			throw new ApplicationException("Exception in UpdateAccountingLinesAsPerFmsFeed.executeQueue()", aoExp);
		}
		finally
		{
			if (null != loFilenetPEDBSession)
			{
				loFilenetPEDBSession.close();
			}

		}
	}

	/**
	 * Method will convert Map to List
	 * @param aoMParameters
	 */
	@Override
	public List getQueue(Map aoMParameters)
	{
		return null;
	}

}
