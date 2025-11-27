/**
 * 
 */
package com.nyc.hhs.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FileUploadService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.UserEmailIdBean;
import com.nyc.hhs.service.db.services.notification.PreProcessingNotificationAlert;

/**
 * Added for Rel 6.3.0 QC 8693
 * This class will fetch the email address from Application_Settings, and send Contact_Us emails to this address
 * 
 */
public class PreprocessingContactUs implements PreProcessingNotificationAlert
{

	private static final LogInfo LOG_OBJECT = new LogInfo(PreprocessingContactUs.class);

	/** 
	 * This will set the email address for NT414.
	 * It's currently hard-coded, but will be refactored to fetch from Application-Settings
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
		FileUploadService fileUploadService = new FileUploadService();
		HashMap<String, String> lsInputParam = new HashMap<String, String>();
		lsInputParam.put(HHSConstants.COMPONENT_NAME, HHSConstants.CONTACT_US);
		lsInputParam.put(HHSConstants.CONTENT_NAME, HHSConstants.TASK_EMAIL);
		
		String emailAddress = fileUploadService.getApplicationSettingsAsString(aoMyBatisSession, lsInputParam);
		
		LOG_OBJECT.Info("ContactUS Task Email = " + emailAddress);
		List<UserEmailIdBean> loUserEmailList = null;

				UserEmailIdBean loUserEmailIdBean = new UserEmailIdBean();
				loUserEmailIdBean.setUserEmailId(emailAddress);
				loUserEmailIdBean.setStaffId("NEW_USER");
				loUserEmailList = new ArrayList<UserEmailIdBean>();
				loUserEmailList.add(loUserEmailIdBean);


		return loUserEmailList;
	}

}
