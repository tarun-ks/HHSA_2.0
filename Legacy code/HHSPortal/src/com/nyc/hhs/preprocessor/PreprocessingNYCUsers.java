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
 * This class will preprocess the Initiators for Notifications Id's -
 * NT038,NT037,NT021,NT020
 * 
 */
public class PreprocessingNYCUsers implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingNYCUsers.class);

	/** This method is modified as a part of release 3.1.0 incident HD02020228
	 * 
	 *  <ul>
	 *  <li>isEmpty() check is added on loUserEmailList</li>
	 *  </ul>
	 * 
	 * <ul>
	 * <li>This will fetch the user details by executing the query
	 * <b>fetchDataForIndividualUser</b></li>
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
			loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession,
					aoGroupNotificationBean.getEntityId(), ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
					HHSConstants.FETCH_NYC_USER_DETAILS, HHSConstants.JAVA_LANG_STRING);
			// fix done as a part of incident HD02020228 .. isEmpty() check is added on loUserEmailList
			if (null == loUserEmailList || loUserEmailList.isEmpty())
			{
				UserEmailIdBean loUserEmailIdBean = new UserEmailIdBean();
				loUserEmailIdBean.setUserEmailId(aoGroupNotificationBean.getEntityId());
				loUserEmailIdBean.setStaffId("NEW_USER");
				loUserEmailList = new ArrayList<UserEmailIdBean>();
				loUserEmailList.add(loUserEmailIdBean);
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NYC Users:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NYC Users ", loExp);
		}

		return loUserEmailList;
	}

}
