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
 * This class Fetches the Agency primary and Secondary contacts for the given
 * Alert ID NT202
 * 
 */
public class PreprocessingAgencyUsers implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingAgencyUsers.class);

	/**
	 * <ul>
	 * <li>This will fetch the agency user details by executing the query
	 * <b>getAgencyContacts</b></li>
	 * </ul>
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
						HHSConstants.GET_AGENCY_CONTACTS, HHSConstants.JAVA_LANG_STRING);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Agency Users:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Agency Users", loExp);
		}

		return loUserEmailList;
	}

}
