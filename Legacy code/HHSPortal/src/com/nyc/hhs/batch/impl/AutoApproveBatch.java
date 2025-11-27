package com.nyc.hhs.batch.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8services.P8HelperServices;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * Added for Release 7 This batch class is used for auto approval. Modification
 * submitted by the provider which falls under auto approval criteria will be
 * automatically approved. The respective Modification Review Task will be
 * approved at all levels. The merging of modification with the base budget is
 * also taken care by this class.
 */
public class AutoApproveBatch extends P8HelperServices implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AutoApproveBatch.class);

	@SuppressWarnings("rawtypes")
	@Override
	public List getQueue(Map aoMParameters)
	{

		return null;
	}

	/**
	 * This method execute queue by taking a list as input aoLQueue a List
	 * @param aoLQueue
	 * @throws aoEx an Exception object
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		Channel loChannel = new Channel();
		boolean lbFinalStat = false;
		TaskDetailsBean loTaskBeanMap = new TaskDetailsBean();
		List<TaskDetailsBean> loListOfTask = null;
		HashMap loHMap = new HashMap();
		try
		{
			LOG_OBJECT.Info("Entering into the batch:: AutoApproveBatch");
			BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.FILENETDOCTYPE,
					XMLUtil.getDomObj(this.getClass().getResourceAsStream("/com/nyc/hhs/config/DocType.xml")));
			String lsClassName = (new AutoApproveBatch()).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;

			String lsCastorPath = ((new AutoApproveBatch()).getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING)
					.replace(HHSR5Constants.MODIFICATION_REVIEW_AUTO_APPROVE_CLASS_PATH, HHSConstants.CASTOR_MAPPING);
System.out.println( "---[marshalObject]3   :   "+ lsCastorPath  );
			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
			BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION,XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSConstants.BULK_UPLOAD_TASK_AUDIT_ELEMENT_PATH)));
			P8UserSession loFilenetSession = filenetConnection.setP8SessionVariables();
			loChannel.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
			loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskBeanMap);
			// Start QC 9585 R 8.9  remove password from logs
			String param = CommonUtil.maskPassword(loChannel);
			//LOG_OBJECT.Info("Channel Data::" + loChannel);
			LOG_OBJECT.Info("Channel Data::" + param);
			// End QC 9585 R 8.9  remove password from logs
			
			TransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_GET_AUTO_APPROVER_LIST);
			loListOfTask = (List<TaskDetailsBean>) loChannel.getData(HHSR5Constants.TASK_BEAN_LIST);
			Iterator loItr = loListOfTask.iterator();
			while (loItr.hasNext())
			{
				TaskDetailsBean loTaskDetailBean = (TaskDetailsBean) loItr.next();
				loChannel.setData(ApplicationConstants.TASK_DETAILS_BEAN, loTaskDetailBean);
				loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
				loChannel.setData(HHSConstants.BULK_LOCAL_HASH_MAP, loHMap);
				TransactionManager.executeTransaction(loChannel,
						HHSR5Constants.TRANSACTION_MODIFICATION_REVIEW_AUTO_APPROVE_TASK);
				lbFinalStat = (Boolean) loChannel.getData(ApplicationConstants.FINISH_STATUS);
				String lsModificationDocId = (String) loChannel.getData(HHSConstants.BULK_UPLOAD_DOC_ID);
				if (null != lsModificationDocId && !lsModificationDocId.isEmpty())
				{
					LOG_OBJECT.Info("Document uploaded in filenet! DocId ::" + lsModificationDocId);
				}
			}
			if (!lbFinalStat)
			{
				LOG_OBJECT.Info("There is no Review workflow currently in auto approval state.");
			}
			LOG_OBJECT.Info("Final Approval status::" + lbFinalStat);
			
			// Start QC 9585 R 8.9  remove password from logs
			param = CommonUtil.maskPassword(loTaskBeanMap);
			//LOG_OBJECT.Info("Task Bean Details::" + loTaskBeanMap);
			LOG_OBJECT.Info("Task Bean Details::" + param);
			// End QC 9585 R 8.9  remove password from logs
			LOG_OBJECT.Info("Exited from the batch:: AutoApproveBatch");
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error:: AutoApproveBatch:" + "executeQueue method - "
					+ "Error Occured While auto approving the task through the batch ", aoEx);
		}
	}
}
