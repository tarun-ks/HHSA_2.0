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
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;

/**
 * Added for enhancement 6601 for Release 3.12.0
 * This class fetches the users for the Notification NT413
 * 
 */
public class PreprocessingNT413 implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingForBCCUsers.class);

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
		List<UserEmailIdBean> loNewUsersList = new ArrayList<UserEmailIdBean>();

		try
		{

			LOG_OBJECT.Debug("PreprocessingNT413-----loUserGroupMap-------------" + loUserGroupMap);
			
			UserEmailIdBean loNewUsersBean = null;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
			.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsNewEmailIds = loApplicationSettingMap.get(HHSConstants.NOTIFICATION_ALERT+aoGroupNotificationBean.getNotificationAlertId()+"_"+HHSConstants.NEW_USERS);
			String[] loNewUserEmailIds = lsNewEmailIds.split(",");
			for(String loEmailId : loNewUserEmailIds){
				loNewUsersBean = new UserEmailIdBean();
				loNewUsersBean.setBccFlag(HHSConstants.FALSE);
				loNewUsersBean.setStaffId(loEmailId);
				loNewUsersBean.setUserEmailId(loEmailId);
				loNewUsersBean.setSkipUserInNotification("true");
				loNewUsersList.add(loNewUsersBean);
			}

			LOG_OBJECT.Debug("PreprocessingNT413-----loNewUsers-------------" + loNewUsersList);
			
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while PreprocessingNT413:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while PreprocessingNT413:", loExp);
			throw new ApplicationException("Error occurred while PreprocessingNT413:", loExp);
		}

		return loNewUsersList;
	}
}
