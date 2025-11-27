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
 * Added for enhancement 5978 for Release 3.11.0
 * This class fetches the users for the Notification NT220
 * 
 */
public class PreprocessingForAL212 implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingForAL212.class);

	/**
	 * <ul>
	 * <li>This method will fetch the user list by executing the Query
	 * <b>fetchUserEmailIdsForNT225</b></li>
	 * <ul>
	 * @param aoMyBatisSession myBatis Sql session
	 * @param aoGroupNotificationBean NotificationBean object
	 * @param aoNotificationAlertMasterBeanList NotificationAlertMasterBean List
	 * @param loUserGroupMap object map containing the user levels, permission
	 *            type and organization id if available
	 * @return the List of type UserEmailIdBean to whom the notification is to
	 *         be send
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	@Override
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException
	{
		List<UserEmailIdBean> loUserEmailList = new ArrayList<UserEmailIdBean>();

		try
		{
				String lsAgencyName = (String) DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean.getEntityId(),
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_NAME_FOR_NT207_AL212,
						HHSConstants.JAVA_LANG_STRING);
				aoGroupNotificationBean.setMessageBody(aoGroupNotificationBean.getMessageBody().replaceAll("\\{#AGENCY_NAME\\}", lsAgencyName));
				loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_USER_EMAIL_IDS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT225:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT225:", loExp);
		}

		return loUserEmailList;
	}
}
