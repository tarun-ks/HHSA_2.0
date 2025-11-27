/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.NotificationParamBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class is used to preprocess the notification NT039
 * 
 */
public class PreprocessingNT039 implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingNT039.class);

	/**
	 * <ul>
	 * <li>This will fetch the agency user details by executing the query
	 * <b>fetchDataForIndividualUser</b></li>
	 * <li>Then it will fetch the extra parmams and will add the email in the
	 * loUserEmailList object</li>
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

			List<NotificationParamBean> loNotificationParamBeanList = (List<NotificationParamBean>) DAOUtil.masterDAO(
					aoMyBatisSession, aoGroupNotificationBean.getGroupNotificationId(),
					ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_NOTIFICATION_PARAMS,
					HHSConstants.INTEGER_CLASS_PATH);
			// Fix done for NT039 as a part of release 3.1.0 defect 6386
			if (loNotificationParamBeanList != null && loNotificationParamBeanList.size() > 0)
			{
				Iterator<NotificationParamBean> loItr = loNotificationParamBeanList.iterator();
				while(loItr.hasNext()){
					NotificationParamBean loNotificationBeanObj = loItr.next();
					UserEmailIdBean loUserEmailIdBean = new UserEmailIdBean();
					loUserEmailIdBean.setStaffId(aoGroupNotificationBean.getEntityId());
					loUserEmailIdBean.setUserEmailId(loNotificationBeanObj.getParamValue());
					loUserEmailList.add(loUserEmailIdBean);
				}
				
				
				
			}
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT039:", aoExp);
		}

		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while Preprocessing NT039:", loExp);
		}
		return loUserEmailList;
	}

}
