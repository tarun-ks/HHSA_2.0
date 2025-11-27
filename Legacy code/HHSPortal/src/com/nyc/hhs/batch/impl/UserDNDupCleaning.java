package com.nyc.hhs.batch.impl;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.util.XMLUtil;

public class UserDNDupCleaning implements IBatchQueue {

	@Override
	public List getQueue(Map aoMParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException {

		// TODO Auto-generated method stub
		try{
            ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
            Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
                    ApplicationConstants.BATCH_TRANSACTION_CONFIG));
            
            loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

            Channel loChannelObj = new Channel();
            /**
             * This transaction will get  Notification details. Will get
             * all the groupIds from the List and fetch userIds for ORgId and
             * GropuID. Then it will update User Notification with userIDs and
             * alert Notification details
             */
            
            TransactionManager.executeTransaction(loChannelObj,	HHSConstants.FETCH_DUP_BOTH_USER_N_ORG);
            
            TransactionManager.executeTransaction(loChannelObj,	HHSConstants.FETCH_DUP_USER_DN_ONLY);
            
		}
        catch (ApplicationException aoExp)
        {
            ProposalReportOnDueDateBatch.LOG_OBJECT.Error("Error occurred while running email reset User DN batch:", aoExp);
        }
		
	}

}
