/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class will preprocess the Notification AL028 and NT031b in case when the
 * Notification is sent to all the agency users irrespective of their user role
 * 
 */
public class PreprocessingAL028 implements PreProcessingNotificationAlert
{
	/**
	 * This method is used to log info
	 * It returns loUserEmailList
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingAL028.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException
	{
		List<UserEmailIdBean> loUserEmailList = new ArrayList<UserEmailIdBean>();
		try
		{

			List<UserEmailIdBean> loAgencyUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_USERS_DOC_SHARING,
					HHSConstants.JAVA_UTIL_HASH_MAP);

			
			List<UserEmailIdBean> loProviderUserEmailList	 = (List<UserEmailIdBean>) DAOUtil.masterDAO(
						aoMyBatisSession, loUserGroupMap, ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
						HHSConstants.GET_USER_EMAIL_IDS, HHSConstants.JAVA_UTIL_HASH_MAP);
			
				if (loAgencyUserEmailList != null && !loAgencyUserEmailList.isEmpty())
				{
					loUserEmailList.addAll(loAgencyUserEmailList);
				}
				
				if (loProviderUserEmailList != null && !loProviderUserEmailList.isEmpty())
				{
					loUserEmailList.addAll(loProviderUserEmailList);
				}
			

		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing AL028 and NT031b:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing AL028 and NT031b:", loExp);
		}

		return loUserEmailList;
	}

}
