/**
 * 
 */
package com.nyc.hhs.preprocessor;

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
 * This class fetches the users for the Notification NT220
 * 
 */
public class PreprocessingNT220 implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingNT220.class);

	/**
	 * <ul>
	 * <li>This method will fetch the user list by executing the Query
	 * <b>fetchUserEmailIdsForNT220</b></li>
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
		List<UserEmailIdBean> loUserEmailList = null;

		try
		{
			loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoGroupNotificationBean.getEntityId(), ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
					HHSConstants.FETCH_USERS_NT220, HHSConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT220:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT220:", loExp);
		}

		return loUserEmailList;
	}
}
