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
 * Added for enhancement 6620 for Release 3.11.0
 * This class is used to preprocess the notification NT034
 * 
 */
public class PreprocessingR3Invoice implements PreProcessingNotificationAlert
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingR3Invoice.class);
	/**
	 * <ul>
	 * <li>This will return the user email id in case when new user is
	 * registered</li>
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
	@Override
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException
	{
		List<UserEmailIdBean> loUserEmailList = new ArrayList<UserEmailIdBean>();
		try{
		String lsAgencyName = (String)DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean.getEntityId(),
				ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_NAME_FROM_INVOICE,
				HHSConstants.JAVA_LANG_STRING);
		aoGroupNotificationBean.setMessageBody(aoGroupNotificationBean.getMessageBody().replaceAll("\\{#AGENCY_NAME\\}", lsAgencyName));

		loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
				ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.GET_USER_EMAIL_IDS,
				HHSConstants.JAVA_UTIL_HASH_MAP);
	
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing:" + aoGroupNotificationBean.getNotificationAlertId(), aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing:" + aoGroupNotificationBean.getNotificationAlertId(), loExp);
		}
		return loUserEmailList;
	}

}
