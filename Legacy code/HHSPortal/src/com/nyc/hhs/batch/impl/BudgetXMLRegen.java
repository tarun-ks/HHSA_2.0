package com.nyc.hhs.batch.impl;

import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.XMLUtil;

public class BudgetXMLRegen  implements IBatchQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public List getQueue(Map aoMParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeQueue(List aoLQueue) throws ApplicationException {

System.out.println( "---[BudgetXMLRegen] Batch Starts  !!"  );
        /* this block of code brings XML config from cached xml files  */
        ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
        Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
                ApplicationConstants.BATCH_TRANSACTION_CONFIG));

		Channel loChannelObj = new Channel();

        loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);
        try{
			BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.FILENETDOCTYPE,
					XMLUtil.getDomObj(this.getClass().getResourceAsStream("/com/nyc/hhs/config/DocType.xml")));

			String lsClassName = (new BudgetXMLRegen()).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;

			System.out.println( "---[marshalObject]1   :   "+ ((new BudgetXMLRegen()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING) + "  :[CASTER_CONFIGURATION_PATH] " + HHSConstants.CASTER_CONFIGURATION_PATH  + " ::  "+ BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.CASTER_CONFIGURATION_PATH)  );
			
			String lsCastorPath = ((new BudgetXMLRegen()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING)
					.replace(HHSR5Constants.BUDGET_XML_REGEN_CLASS_PATH, HHSConstants.CASTOR_MAPPING);
			System.out.println( "---[marshalObject]2   :   "+ lsCastorPath + "  :[CASTER_CONFIGURATION_PATH] " + HHSConstants.CASTER_CONFIGURATION_PATH  + " ::  "+ BaseCacheManagerWeb.getInstance().getCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH)  );
			
			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);

        	
            TransactionManager.executeTransaction(loChannelObj, HHSConstants.BUDGET_XML_REGEN  );

        }catch (ApplicationException apAppEx){
        	
        	System.out.println("Error while Executing FileNetBatch.executeQueue()");
			throw apAppEx;
		}
		catch (Exception apAppEx)
		{
			System.out.println("Error while Executing FileNetBatch.executeQueue()");
			throw new ApplicationException("Error while Executing FileNetBatch.executeQueue()", apAppEx);
		}

/*        Integer loUserNotiDataCnt = (Integer) loChannelObj.getData(HHSConstants.REMOVED_NOTIFICATION_DATA_CNT);
        Integer loAlertUrlDataCnt = (Integer) loChannelObj.getData(HHSConstants.REMOVED_ALERT_URL_DATA_CNT);*/

	}

}
