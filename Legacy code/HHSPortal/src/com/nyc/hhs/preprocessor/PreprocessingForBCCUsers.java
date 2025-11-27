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
import com.nyc.hhs.util.DAOUtil;

/**
 * Added for enhancement 5978 for Release 3.11.0
 * This class fetches the users for the Notification NT220
 * 
 */
public class PreprocessingForBCCUsers implements PreProcessingNotificationAlert
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
		List<UserEmailIdBean> loUserEmailList = null;

		try
		{
			String lsAgencyId = null;
			if(null != aoGroupNotificationBean.getEntityType() && !aoGroupNotificationBean.getEntityType().equalsIgnoreCase(HHSConstants.PROCUREMENT)){
			lsAgencyId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean.getEntityId(),
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_USER_NT225,
					HHSConstants.JAVA_LANG_STRING);
			}
			else{
				lsAgencyId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean.getEntityId(),
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_USER_FOR_PROC,
						HHSConstants.JAVA_LANG_STRING);
			}
			LOG_OBJECT.Debug("BCCUser-----lsAgencyId-------------" + lsAgencyId);

			List<String> loUserLevelsAgency = new ArrayList<String>();
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance()
			.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsBccUserLevel = loApplicationSettingMap.get(HHSConstants.NOTIFICATION_ALERT+aoGroupNotificationBean.getNotificationAlertId()+"_"+HHSConstants.BCC_USERS);
			String[] loBccUsers = lsBccUserLevel.split(",");
			for(String loEmailId : loBccUsers){
			loUserLevelsAgency.add(loEmailId);
			}
			LOG_OBJECT.Debug("BCCUser-----lsBccUserLevel-------------" + lsBccUserLevel);
			
			loUserGroupMap.put(HHSConstants.AGENCYID, lsAgencyId);
			loUserGroupMap.put(HHSConstants.USER_LEVELS_AGENCY, loUserLevelsAgency);
			
			loUserEmailList = (ArrayList<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_USERS_NT225,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (aoGroupNotificationBean.getNotificationAlertId().equalsIgnoreCase(HHSConstants.NT207))
			{
				String lsAgencyName = (String) DAOUtil.masterDAO(aoMyBatisSession, aoGroupNotificationBean.getEntityId(),
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_AGENCY_NAME_FOR_NT207_AL212,
						HHSConstants.JAVA_LANG_STRING);
				aoGroupNotificationBean.setMessageBody(aoGroupNotificationBean.getMessageBody().replaceAll("\\{#AGENCY_NAME\\}", lsAgencyName));
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing BCCUser:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing BCCUser:", loExp);
			throw new ApplicationException("Error occurred while Preprocessing BCCUser:", loExp);
		}

		return loUserEmailList;
	}
}
