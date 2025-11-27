package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class will preprocess the Notification NT325 in case when the
 * Notification is sent to the agency user who initiate the request foe Task Export.
 * 
 */

public class PreprocessingNT325 implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingNT325.class);

	 /**
	 * This method is to get All Users For Notification Alert
	 * 
	 * @param aoMyBatisSession SqlSession
	 * @param aoGroupNotificationBean NotificationBean
	 * @param aoNotificationAlertMasterBeanList List<NotificationAlertMasterBean>
	 * @param loUserGroupMap HashMap<String, Object>
	 * @return List<UserEmailIdBean> loUserEmailList
	 * @throws ApplicationException If an Exception occurs
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public List<UserEmailIdBean> getAllUsersForNotificationAlert(SqlSession aoMyBatisSession,
			NotificationBean aoGroupNotificationBean,
			List<NotificationAlertMasterBean> aoNotificationAlertMasterBeanList, HashMap<String, Object> loUserGroupMap)
			throws ApplicationException
	{ 
		List<UserEmailIdBean> loUserEmailList = new ArrayList<UserEmailIdBean>();
		try
		{
			//updated for defect 8596
			loUserGroupMap.put(HHSR5Constants.REQUEST_ID, aoGroupNotificationBean.getGroupNotificationId());
			//defect 8596 end
			loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserGroupMap,
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, "fetchCityUserNT_BULK_ACK",
					HHSConstants.JAVA_UTIL_HASH_MAP);

		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while PreprocessingNT325:", aoExp);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while PreprocessingNT325:", loExp);
		}

		return loUserEmailList;
	}

}

