/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * This class fetches the users for the Notification NT210,NT204,AL205
 * 
 */
public class PreprocessingAssignedEvaluators implements PreProcessingNotificationAlert
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingAssignedEvaluators.class);

	/**
	 * <ul>
	 * <li>This method will fetch the user list for assigned evaluators by
	 * executing the query <b>fetchUserEmailIdsForNT210</b></li>
	 * <li>If Event ID is NT210 then the Entity type can be procurement and
	 * Evaluation pool mapping Id</li>
	 * <li>Else the Entity type will procurement only</li>
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
		List<UserEmailIdBean> loUserEmailList = new ArrayList<UserEmailIdBean>();
		HashMap<String, Object> loEntityMap = new HashMap<String, Object>();
		String lsSubject = new SimpleDateFormat("MM/dd/yyyy").format(new Date(System.currentTimeMillis()));
		try
		{
			
			if (!aoGroupNotificationBean.getEntityId().equalsIgnoreCase(HHSConstants.DEFAULT))
			{
				if (aoGroupNotificationBean.getNotificationAlertId().equalsIgnoreCase(HHSConstants.NT210))
				{
					if (aoGroupNotificationBean.getProviderId() == null
							|| aoGroupNotificationBean.getProviderId().isEmpty())
					{
						loEntityMap.put(aoGroupNotificationBean.getEntityType(), aoGroupNotificationBean.getEntityId());
					}
					
				}
				else
				{
					loEntityMap.put(HHSConstants.PROCUREMENT, aoGroupNotificationBean.getEntityId());
				}
				// Condition added for resolving issue for defect 6385 in
				// Release 3.1.0
				if (aoGroupNotificationBean.getProviderId() == null
						|| aoGroupNotificationBean.getProviderId().isEmpty())
				{
					loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession, loEntityMap,
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSConstants.FETCH_ASSIGNED_EVALUATORS, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else
				{
					// Start || Modified as a part of Enhancement 5905 for
					// Release 3.6.0
					loUserEmailList = (List<UserEmailIdBean>) DAOUtil.masterDAO(aoMyBatisSession,
							aoGroupNotificationBean.getProviderId(),
							ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER,
							HHSConstants.FETCH_USER_ID_FROM_EMAIL_ID, HHSConstants.JAVA_LANG_STRING);
					// loUserEmailList.add(loUserEmailIdBean);
					// End || Modified as a part of Enhancement 5905 for Release
					// 3.6.0
				}
				
				if(aoGroupNotificationBean.getNotificationAlertId().equalsIgnoreCase("NT233"))
				{
					aoGroupNotificationBean.setSubject(lsSubject+ " - " +aoGroupNotificationBean.getSubject());
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Assigned Evaluators:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing Assigned Evaluators:", loExp);
		}

		return loUserEmailList;
	}

}
