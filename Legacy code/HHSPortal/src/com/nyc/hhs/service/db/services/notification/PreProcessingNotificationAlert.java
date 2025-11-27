package com.nyc.hhs.service.db.services.notification;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;

/**
 * This interface will be implement for all notification/ alerts where we need
 * to execute specific business rule for specific notification/ alerts.
 * 
 */
public interface PreProcessingNotificationAlert
{

	/**
	 * 
	 * @param aoMyBatisSession myBatis Sql session
	 * @param aoGroupNotificationBean NotificationBean object
	 * @param aoNotificationAlertMasterBeanList NotificationAlertMasterBean List
	 * @param loUserGroupMap object map containing the user levels, permission
	 *            type and organization id if available
	 * @return
	 * @throws ApplicationException
	 */
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException;
}
