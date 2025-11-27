package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.novell.www.provisioning.service.DataItem;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.webservices.AccountRequestLoginStub;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.webservice.client.ManageUserClient;

/**
 * ManageUserAccountDetailsService: This class manages user profile and updates
 * the changed user data in the database.
 * 
 */

public class ManageUserAccountDetailsService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ManageUserAccountDetailsService.class);

	/**
	 * This method calls the web service to update the user data.
	 * 
	 * @param aoMapServiceData map contains all the user data.
	 * @return boolean lbUpdateStatus a status if the user data has been updated
	 *         successfully.
	 * @throws Exception
	 */

	public boolean updateNYCUserData(Map<String, Object> aoMapServiceData) throws Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		boolean lbUpdateStatus = false;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			lbUpdateStatus = loAccountRequestLoginStub.UpdateUserProfileInLdap(aoMapServiceData);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while updating pwd reset in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateNYCUserData method -Exception occurred in webservice call"
					+ " while updating manage user data in LDAP. /n");
			throw loAppEx;
		}

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: updateNYCUserData. Time Taken(seconds):: "
				+ liTimediff);

		return lbUpdateStatus;

	}

	/**
	 * This method is under construction .It is used to generate token.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form.
	 * @return Map loMapWebServRetrndParam map having values of the generated
	 *         token for the user.
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	public Map updateNYCEmailTokenGen(Map<String, Object> aoMapServiceData) throws Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		Map<String, Object> loMapWebServRetrndParam = new HashMap<String, Object>();
		String lsToken = "";
		boolean lbEmailNotUnique = false;
		try
		{
			// start, check user dn if it exist deny(use different id) else
			// proceed
			SecurityService loSecService = new SecurityService();
			String lsUserDn = loSecService.userSearchOnEmailIdVDX((String) aoMapServiceData.get("newEmailAddress"));

			if (null != lsUserDn && !"".equalsIgnoreCase(lsUserDn))
			{
				lbEmailNotUnique = true;
				loMapWebServRetrndParam.put("serviceStatus", "error");
				loMapWebServRetrndParam.put("serviceOutput", "Email ID not unique");
			}
			else
			{

				AccountRequestLoginStub loAccountRequestLoginStub = null;
				List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
				String lsUserEmailId = (String) aoMapServiceData.get("newEmailAddress");
				String lsUserOldEmailId = (String) aoMapServiceData.get("oldEmailAddress");
				loStartRequestDataItem.add(new DataItem("newEmailAddress", new String[]
				{ lsUserEmailId }));
				loStartRequestDataItem.add(new DataItem("oldEmailAddress", new String[]
				{ lsUserOldEmailId }));

				DataItem[] loDataItemArray = loStartRequestDataItem
						.toArray(new DataItem[loStartRequestDataItem.size()]);
				String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						ApplicationConstants.PROPERTY_PROV_DN_EMAIL_UPDATE_TOKEN_GEN);

				List<String> loMsgActivityList = new ArrayList<String>();
				loMsgActivityList.add("Approval_Email_Update_Token");

				loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
						lsProvisioningDN);

				if (null != loMapWebServRetrndParam)
				{
					lsToken = (String) ((ArrayList) loMapWebServRetrndParam.get("serviceOutput")).get(0);
					if (lsToken != null && !"".equalsIgnoreCase(lsToken) && lsToken.indexOf("[") >= 0)
					{
						lsToken = lsToken.substring(1, lsToken.length() - 1);
					}
					loMapWebServRetrndParam.put("serviceOutput", lsToken);
				}

			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateNYCEmailTokenGen method "
					+ "-Exception occurred in webservice call while generating token for the user in updating email address LDAP. /n");
			throw loAppEx;
		}
		if (lbEmailNotUnique)
		{
			setMoState("Transaction failure:: ManageUserAccountDetailsService: updateNYCEmailTokenGen method "
					+ "- new email id while updating is not unique./n");
		}
		else
		{
			setMoState("Transaction Success:: ManageUserAccountDetailsService: updateNYCEmailTokenGen method "
					+ "- Successfully generated token for the update email address of user./n");
		}

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: updateNYCEmailTokenGen. Time Taken(seconds):: "
				+ liTimediff);

		return loMapWebServRetrndParam;
	}

	/**
	 * This method is under construction .It is used to validate token.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form.
	 * @return Map loMapWebServRetrndParam map having values of the token
	 *         validation for the user.
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "unchecked", "static-access", "rawtypes" })
	public Map updateNYCEmailTokenValidation(Map<String, Object> aoMapServiceData) throws Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, Object> loMapWebServRetrndParam = null;

		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			String lsUserEmailId = (String) aoMapServiceData.get("lsNewEmail");
			String lsUserOldEmailId = (String) aoMapServiceData.get("lsOldEmail");
			String lsToken = (String) aoMapServiceData.get("lsToken");
			loStartRequestDataItem.add(new DataItem("newEmailAddress", new String[]
			{ lsUserEmailId }));
			loStartRequestDataItem.add(new DataItem("oldEmailAddress", new String[]
			{ lsUserOldEmailId }));
			loStartRequestDataItem.add(new DataItem("token", new String[]
			{ lsToken }));

			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);
			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_EMAIL_UPDATE_TOKEN_VALID);

			List<String> loMsgActivityList = new ArrayList<String>();
			loMsgActivityList.add("Approval_Email_Update_Token");
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);

			String lsStatus = (String) loMapWebServRetrndParam.get("serviceStatus");
			List loOutputList = (List) loMapWebServRetrndParam.get("serviceOutput");

			if ("error".equalsIgnoreCase(lsStatus))
			{
				String lsErrorMsg = (String) loOutputList.get(0);
				throw new ApplicationException(lsErrorMsg);
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while validating token (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateNYCEmailTokenValidation method "
					+ "-Exception occurred in webservice call while validating token for the user to update email in LDAP. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ManageUserAccountDetailsService: updateNYCEmailTokenValidation method"
				+ " - Successfully validated the token for the user for updating email address in LDAP. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("ManageUserAccountDetailsService: method: updateNYCEmailTokenValidation. Time Taken(seconds):: "
						+ liTimediff);

		return loMapWebServRetrndParam;
	}

	/**
	 * This method is used update username data of the user.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form
	 * @return String loMessage a success or failure message if the password is
	 *         updated or not.
	 * @throws Exception
	 */

	@SuppressWarnings("static-access")
	public String updateNYCUserPassword(Map<String, String> aoMapServiceData) throws Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		String lsMessage;
		try
		{
			String lsUserDN = (String) aoMapServiceData.get("UserDN");
			String lsCurrentPassword = (String) aoMapServiceData.get("userOldPassword");
			String lsNewPassword = (String) aoMapServiceData.get("userPassword");
			ManageUserClient loMangeuserclient = null;
			lsMessage = loMangeuserclient.executeChangePassword(lsUserDN, lsCurrentPassword, lsNewPassword);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateNYCUserPassword method"
					+ " -Exception occurred in webservice call while updating the password  in LDAP. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ManageUserAccountDetailsService: updateNYCUserPassword method"
				+ " - Successfully updated the user password  in LDAP. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: updateNYCUserPassword. Time Taken(seconds):: "
				+ liTimediff);

		return lsMessage;

	}

	/**
	 * This method is used update security Questions data of the user.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form
	 * @return String loMessage a success or failure message if the security
	 *         questions have been updated successfully or not.
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "unchecked" })
	public String updateNYCSecurityQuestions(Map<String, String> aoMapServiceData) throws Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		String lsMessage;
		try
		{
			String lsUserDN = (String) aoMapServiceData.get("UserDN");
			String lsCurrentPassword = (String) aoMapServiceData.get("currentPassword");
			String lsQuestion1 = (String) aoMapServiceData.get("secQues1");
			String lsQuestion2 = (String) aoMapServiceData.get("secQues2");
			String lsQuestion3 = (String) aoMapServiceData.get("secQues3");

			String lsAnswer1 = (String) aoMapServiceData.get("secAns1");
			String lsAnswer2 = (String) aoMapServiceData.get("secAns2");
			String lsAnswer3 = (String) aoMapServiceData.get("secAns3");

			@SuppressWarnings("rawtypes")
			List loQuestionList = new ArrayList();
			loQuestionList.add(lsQuestion1);
			loQuestionList.add(lsQuestion2);
			loQuestionList.add(lsQuestion3);

			@SuppressWarnings("rawtypes")
			List loAnswerList = new ArrayList();
			loAnswerList.add(lsAnswer1);
			loAnswerList.add(lsAnswer2);
			loAnswerList.add(lsAnswer3);

			lsMessage = ManageUserClient.setChallengeQuestions(lsUserDN, lsCurrentPassword, loQuestionList,
					loAnswerList);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while validating security question in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateNYCSecurityQuestions method"
					+ " -Exception occurred in webservice call while updating security questions in LDAP. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ManageUserAccountDetailsService: updateNYCSecurityQuestions method "
				+ "- Successfully validating security question in  LDAP. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: updateNYCSecurityQuestions. Time Taken(seconds):: "
				+ liTimediff);

		return lsMessage;
	}

	/**
	 * This method is used to get user data from DB.
	 * 
	 * @param asOrgID orgID depending upon the user login details
	 * @param asUserId asUserId depending upon the user login details
	 * @param aoMybatisSession
	 * @return Map loMapUserData map containing all the user data from the form
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public Map<String, String> getUserDataFromDB(String asOrgID, String asUserId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, String> loMapUserData = null;
		Map<String, String> loMapRequiredParam = new HashMap<String, String>();
		try
		{
			loMapRequiredParam.put("asUserId", asUserId);
			loMapRequiredParam.put("asOrgId", asOrgID);
			loMapUserData = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getUserDataFromDB", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in ManageUserAccountDetailsService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: getUserDataFromDB method"
					+ " -Exception occurred  while retrieving user data from DB. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: ManageUserAccountDetailsService: getUserDataFromDB method "
				+ "- Successfully retrieved user data from DB. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: getUserDataFromDB. Time Taken(seconds):: "
				+ liTimediff);

		return loMapUserData;
	}

	/**
	 * This method calls the to update the user data in DB.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form
	 * @param aoMyBatisSession
	 * @return boolean lbUpdateStatus status if the user data has been
	 *         successfully updated in database tables.
	 * @throws ApplicationException
	 */

	public boolean updateUserData(Map<String, String> aoMapServiceData, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		boolean lbUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoMapServiceData, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateUserData", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateUserData method "
					+ "-Exception occurred while updating user data in DB.. /n");
			throw loAppEx;
		}
		lbUpdateStatus = true;
		setMoState("Transaction Success:: ManageUserAccountDetailsService: updateUserData method - Successfully updated user data in db. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("ManageUserAccountDetailsService: method: updateUserData. Time Taken(seconds):: " + liTimediff);

		return lbUpdateStatus;
	}

	/**
	 * This method calls the to update the user data in DB.
	 * 
	 * @param aoMapServiceData map containing all the user data from the form
	 * @param aoMyBatisSession
	 * @return boolean lbUpdateStatus status if the user data has been
	 *         successfully updated in database tables.
	 * @throws ApplicationException
	 */

	public boolean updateUserProfileEmail(Map<String, String> aoMapServiceData, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		boolean lbUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoMapServiceData, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateUserProfileEmailStaff", "java.util.Map");
			DAOUtil.masterDAO(aoMyBatisSession, aoMapServiceData, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateUserProfileEmailOrg", "java.util.Map");
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: ManageUserAccountDetailsService: updateUserData method "
					+ "-Exception occurred while updating user data in DB.. /n");
			throw loAppEx;
		}
		lbUpdateStatus = true;
		setMoState("Transaction Success:: ManageUserAccountDetailsService: updateUserData method - Successfully updated user data in db. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("ManageUserAccountDetailsService: method: updateUserProfileEmail. Time Taken(seconds):: "
				+ liTimediff);
		return lbUpdateStatus;
	}

}
