/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.batch.bulkupload.BulkUploadContractInfo;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;
import com.nyc.hhs.service.db.services.notification.ReplaceParams;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class will process the notification NT_BULK_UPLOAD that will send to the
 * user after bulk upload
 * 
 */
public class PreprocessingBulkUploadNotification implements PreProcessingNotificationAlert
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingBulkUploadNotification.class);

	/**
	 * <ul>
	 * <li>
	 * FIrst it will fetch the error_message rows from the table
	 * Bulk_upload_Data by executing the Transaction
	 * <b>getBulkUploadNotificationDetails</b></li>
	 * <li>Then it will replace the message body</li>
	 * <li>Then it will fetch the user data by executing the
	 * query<b>fetchBulkUploadNotificationData</b></li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession myBatis Sql session
	 * @param aoGroupNotificationBean NotificationBean object
	 * @param aoNotificationAlertMasterBeanList NotificationAlertMasterBean List
	 * @param loUserGroupMap object map containing the user levels, permission
	 *            type and organization id if available
	 * @return the List of type UserEmailIdBean to whom the notification is to
	 *         be send
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException
	{
		List<UserEmailIdBean> loUserEmailList = null;
		try
		{
			if (!aoGroupNotificationBean.getEntityId().equalsIgnoreCase(HHSConstants.DEFAULT))
			{
				loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession,
						aoGroupNotificationBean.getEntityId(), ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
						HHSConstants.FETCH_BULK_UPLOAD_NOTI_DATA, HHSConstants.JAVA_LANG_STRING);

				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.BULK_UPLOAD_DOC_ID, aoGroupNotificationBean.getEntityId());

				// Transaction to get the error_message rows with row id and
				// total
				// contracts to be replaced in message body
				TransactionManager.executeTransaction(loChannel, HHSConstants.TRAN_GET_BULK_UPLOAD_NOTI_DETAILS);
				List<BulkUploadContractInfo> loBulkUploadContractInfoList = (List<BulkUploadContractInfo>) loChannel
						.getData(HHSConstants.ERROR_LIST);
				StringBuffer loErrorString = new StringBuffer();
				if (loBulkUploadContractInfoList != null)
				{
					for (BulkUploadContractInfo loBulkUploadContractInfo : loBulkUploadContractInfoList)
					{
						loErrorString.append(loBulkUploadContractInfo.getErrorMessage()).append("<br></br>");
					}
				}

				HashMap<String, Object> loParamMap = new HashMap<String, Object>();
				loParamMap.put("TOTAL_CONTRACTS",
						String.valueOf((Integer) loChannel.getData(HHSConstants.TOTAL_CONTRACTS)));
				loParamMap.put(HHSConstants.REJECTED_RECORDS, loErrorString);
				String lsBody = ReplaceParams.replaceWithParams(aoGroupNotificationBean.getMessageBody(), loParamMap);
				aoGroupNotificationBean.setMessageBody(lsBody);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Bulk Upload Notification:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Bulk Upload Notification ", loExp);
		}

		return loUserEmailList;
	}

}
