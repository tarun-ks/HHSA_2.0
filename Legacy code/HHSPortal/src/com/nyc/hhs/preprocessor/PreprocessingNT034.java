/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;

/**
 * This class is used to preprocess the notification NT034
 * 
 */
public class PreprocessingNT034 implements PreProcessingNotificationAlert
{

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

		UserEmailIdBean loUserEmailIdBean = new UserEmailIdBean();
		loUserEmailIdBean.setUserEmailId(aoGroupNotificationBean.getEntityId());
		loUserEmailIdBean.setStaffId(HHSConstants.NEW_USER);
		loUserEmailList.add(loUserEmailIdBean);
		return loUserEmailList;
	}

}
