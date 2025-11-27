package com.nyc.hhs.preprocessor;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.NotificationParamBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class will preprocess the Initiators for Notifications Id's -
 * NT315a,NT305a,NT315b,NT305b,AL315
 * 
 */
public class PreprocessingInitiators implements PreProcessingNotificationAlert
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingInitiators.class);

	/**
	 * <ul>
	 * <li> This method is modified for Release 3.2.0 Defect 6384 : Adding if condition for AL305.</li>
	 * <li>First it will fetch the addition params(userId) by executing the
	 * query <b>getAgencyContacts</b>from Notification_Param_value table</li>
	 * <li>Then it will fetch the user data by executing the
	 * query<b>fetchInitiatorsDetails</b></li>
	 * </ul>
	 * 
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
			List<NotificationParamBean> loNotificationParamBeanList = (List<NotificationParamBean>) DAOUtil.masterDAO(
					aoMyBatisSession, aoGroupNotificationBean.getGroupNotificationId(),
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_NOTIFICATION_PARAMS,
					HHSConstants.INTEGER_CLASS_PATH);
			String lsUserId = null;
			String lsAlertId = null;
			if (loNotificationParamBeanList != null && !loNotificationParamBeanList.isEmpty())
			{
				lsUserId = loNotificationParamBeanList.get(0).getParamValue();
			}
			// Start of enhancement 3.2.0 defect 6384
			if (aoNotificationAlertMasterBeanList != null && !aoNotificationAlertMasterBeanList.isEmpty())
			{
				lsAlertId = aoNotificationAlertMasterBeanList.get(0).getNotificationALertId();
			}
			//End of enhancement 3.2.0 defect 6384
			HashMap<String, String> loParamMap = new HashMap<String, String>();
			loParamMap.put(HHSConstants.USER_ID, lsUserId);
			loParamMap.put(HHSConstants.CONTRACT_ID1, aoGroupNotificationBean.getEntityId());
			
			// Start of enhancement 3.2.0 defect 6384 : Adding if condition for AL305
			if(null != lsAlertId &&  lsAlertId.equalsIgnoreCase(HHSP8Constants.AL305)){
				loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_INITIATORS_DETAILS_AL305,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			else{
				loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
						ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_INITIATORS_DETAILS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
			}
			//End of enhancement 3.2.0 defect 6384
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Initiators:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Initiators:", loExp);
		}

		return loUserEmailList;
	}

}
