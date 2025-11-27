package com.nyc.hhs.batch.impl;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.util.XMLUtil;

public class ShrinkNotificationInfoBatch implements IBatchQueue {
    private static final LogInfo LOG_OBJECT = new LogInfo(ShrinkNotificationInfoBatch.class);
    
    @Override
    public List getQueue(Map aoMParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void executeQueue(List aoLQueue)  {
        try{
            /* this block of code brings XML config from cached xml files  */
            ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
            Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
                    ApplicationConstants.BATCH_TRANSACTION_CONFIG));

            loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

            Channel loChannelObj = new Channel();

            TransactionManager.executeTransaction(loChannelObj, HHSConstants.SHRINK_NOTIFICATION_DATA  );

            Integer loUserNotiDataCnt = (Integer) loChannelObj.getData(HHSConstants.REMOVED_NOTIFICATION_DATA_CNT);
            Integer loAlertUrlDataCnt = (Integer) loChannelObj.getData(HHSConstants.REMOVED_ALERT_URL_DATA_CNT);
            //[Start]Added in R8.4.1 for QC9513  -->
            Integer loGroupNotifiDataCnt = (Integer) loChannelObj.getData(HHSConstants.REMOVED_GROUP_NOTIFICATION_DATA_CNT);
            //[End]Added in R8.4.1 for QC9513  -->
            ShrinkNotificationInfoBatch.LOG_OBJECT.Info("********** Notification Archive **********\n");
            ShrinkNotificationInfoBatch.LOG_OBJECT.Info("********** User Notification data : " + loUserNotiDataCnt + " **********\n");
            ShrinkNotificationInfoBatch.LOG_OBJECT.Info("********** Notification Alert URL data: " + loAlertUrlDataCnt + " **********\n");
          //[Start]Added in R8.4.1 for QC9513  -->
            ShrinkNotificationInfoBatch.LOG_OBJECT.Info("********** Group Notification data: " + loGroupNotifiDataCnt + " **********\n");
            //[End]Added in R8.4.1 for QC9513  -->
        }
        catch (ApplicationException aoExp){
            ShrinkNotificationInfoBatch.LOG_OBJECT.Error("Error occurred while Archiving Notification data batch:", aoExp);
        }
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
}
