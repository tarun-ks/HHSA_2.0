package com.nyc.hhs.daomanager.service;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.ibatis.session.SqlSession;

import com.novell.www.provisioning.service.DataItem;
import com.novell.www.pwdmgt.service.ForgotPasswordWSBean;
import com.novell.www.pwdmgt.service.PasswordManagement;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ComponentRoleMappingBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserDetailBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.webservices.AccountRequestLoginStub;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class is for all the web services that a are hit as part of
 * account request and login
 * 
 */

public class SecurityService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(SecurityService.class);

	/**
	 * This method validates Captcha image (user returned text and system
	 * generated text)
	 * 
	 * @param aoUserDetailBean has user specific details
	 * @return boolean status whether or not the Captcha image is validated.
	 * @throws ApplicationException
	 */

	public boolean validateCaptcha(UserDetailBean aoUserDetailBean) throws ApplicationException
	{
		boolean lbStatus = false;
		try
		{
			String lsCaptchaImg = aoUserDetailBean.getMsCaptchaImg();
			String lsCaptchaTxt = aoUserDetailBean.getMsCaptchaTxt();
			AccountRequestLoginStub lsAcctReqLoginStb = new AccountRequestLoginStub();
			lbStatus = lsAcctReqLoginStb.validateCaptcha(lsCaptchaImg, lsCaptchaTxt);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("msCaptchaTxt", aoUserDetailBean.getMsCaptchaTxt());
			LOG_OBJECT.Error("Exception occured while getting valid captcha ", loAppEx);
			setMoState("Transaction Failed:: SecurityService:validateCaptcha method - Exception occured while getting valid captcha with text "
					+ aoUserDetailBean.getMsCaptchaTxt() + "\n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService:validateCaptcha method - retreived valid recaptcha"
				+ aoUserDetailBean.getMsCaptchaTxt() + " \n");
		return lbStatus;
	}

	/**
	 * This method is used for authenticating login user
	 * 
	 * @param aoUserBean has user specific details
	 * @param aoMyBatisSession to connect to database
	 * @return boolean status
	 * @throws ApplicationException
	 */

	public boolean authenticateLoginUser(UserBean aoUserBean, SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbAuthenticateUser;
		try
		{
			String lsUserId = aoUserBean.getMsUserId();
			String lsPassword = aoUserBean.getMsPassword();
			AccountRequestLoginStub loAcctReqLoginStb = new AccountRequestLoginStub();
			lbAuthenticateUser = loAcctReqLoginStb.authenticateLoginUser(lsUserId, lsPassword);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoUserBean", CommonUtil.convertBeanToString(aoUserBean));
			LOG_OBJECT.Error("Exception occured while authenticating login user details  ", loAppEx);
			setMoState("Transaction Failed:: SecurityService:authenticateLoginUser method - while authenticating login details for user "
					+ aoUserBean.getMsLoginId() + "\n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService:authenticateLoginUser method -User Successfully Authenticated "
				+ aoUserBean.getMsLoginId() + "\n ");
		return lbAuthenticateUser;
	}

	/**
	 * This method is used to get role component based mapping
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoUserBean has user specific details
	 * @return List loCompoRoleMappingList, ComponentRoleMappingBean has fields
	 *         related to role, orgtype ... etc
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ComponentRoleMappingBean> getroleComponentMapping(SqlSession aoMyBatisSession, UserBean aoUserBean)
			throws ApplicationException
	{
		List<ComponentRoleMappingBean> loCompoRoleMappingList = null;
		try
		{
			loCompoRoleMappingList = (List) DAOUtil
					.masterDAO(aoMyBatisSession, aoUserBean, ApplicationConstants.MAPPER_CLASS_SECURITY,
							"getroleComponentMapping", "com.nyc.hhs.model.UserBean");

		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoUserBean", CommonUtil.convertBeanToString(aoUserBean));
			LOG_OBJECT.Error("Exception occured while retrieving role based component Mapping ", loAppEx);
			setMoState("Transaction Failed::SecurityService:getroleComponentMapping method - "
					+ "Exception occured while retrieving role based component Mapping for orgType:: "
					+ aoUserBean.getMsOrgType() + " and role:: " + aoUserBean.getMsRole() + "\n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService:getroleComponentMapping method -Role mapping  Successfully retrieved for org Type:: "
				+ aoUserBean.getMsOrgType() + " and role:: " + aoUserBean.getMsRole() + "\n ");
		return loCompoRoleMappingList;
	}

	/**
	 * This method is used for get fetching user details
	 * 
	 * @param aoMyBatisSession to connect to database
	 * @param aoUserBean has user specific details
	 * @return UserBean with user specific details
	 * @throws ApplicationException
	 */

	public UserBean fetchUserDetails(SqlSession aoMyBatisSession, UserBean aoUserBean) throws ApplicationException
	{
		UserBean loUserRoleBean = null;
		try
		{
			AccountRequestLoginStub loAcctReqLoginStb = new AccountRequestLoginStub();
			loUserRoleBean = loAcctReqLoginStb.fetchUserRoleDetails(aoUserBean);
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoUserBean", CommonUtil.convertBeanToString(aoUserBean));
			LOG_OBJECT.Error("Exception occured while authenticating user details  ", loAppEx);
			setMoState("Transaction Failed:: SecurityService:fetchUserDetails method - while fetching user details for user "
					+ aoUserBean.getMsLoginId() + "\n ");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService:fetchUserDetails method - while fetching user details for user "
				+ aoUserBean.getMsLoginId() + "\n ");
		return loUserRoleBean;
	}

	/**
	 * This method is used for new user registration in LDAP
	 * 
	 * @param aoRegisterNycIdBean contains details of new user while registering
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	public Map<String, Object> createUserInLDAP(RegisterNycIdBean aoRegisterNycIdBean) throws ApplicationException,
			ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::1:: ");
			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			loStartRequestDataItem.add(new DataItem("FirstName", new String[]
			{ aoRegisterNycIdBean.getMsFirstName() }));
			loStartRequestDataItem.add(new DataItem("MiddleInitial", new String[]
			{ aoRegisterNycIdBean.getMsMiddleName() }));
			loStartRequestDataItem.add(new DataItem("LastName", new String[]
			{ aoRegisterNycIdBean.getMsLastName() }));
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ aoRegisterNycIdBean.getMsEmailAddress() }));
			loStartRequestDataItem.add(new DataItem("UserID", new String[]
			{ "" })); // deprecated
			loStartRequestDataItem.add(new DataItem("Password", new String[]
			{ aoRegisterNycIdBean.getMsPassword() }));
			loStartRequestDataItem.add(new DataItem("q1", new String[]
			{ aoRegisterNycIdBean.getMsQues1Text() }));
			loStartRequestDataItem.add(new DataItem("r1", new String[]
			{ aoRegisterNycIdBean.getMsAnswer1() }));
			loStartRequestDataItem.add(new DataItem("q2", new String[]
			{ aoRegisterNycIdBean.getMsQues2Text() }));
			loStartRequestDataItem.add(new DataItem("r2", new String[]
			{ aoRegisterNycIdBean.getMsAnswer2() }));
			loStartRequestDataItem.add(new DataItem("q3", new String[]
			{ aoRegisterNycIdBean.getMsQues3Text() }));
			loStartRequestDataItem.add(new DataItem("r3", new String[]
			{ aoRegisterNycIdBean.getMsAnswer3() }));
			loStartRequestDataItem.add(new DataItem("q4", new String[]
			{ "" }));
			loStartRequestDataItem.add(new DataItem("r4", new String[]
			{ "" }));
			loStartRequestDataItem.add(new DataItem("q5", new String[]
			{ "" }));
			loStartRequestDataItem.add(new DataItem("r5", new String[]
			{ "" }));
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::2:: ");
			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::3:: ");

			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_ACCT_REG_TOKEN_GEN);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::4:: ");
			List loMsgActivityList = new ArrayList();
			loMsgActivityList.add("Approval_Email_Validation_Token");
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::5:: ");
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT
					.Error("MalformedURLException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loMalEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - MalformedURLException Exception "
					+ "occurred in SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - "
					+ "RemoteException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT
					.Error("ServiceException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loSerEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - ServiceException Exception occurred in"
					+ " SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("RegisterNycIdBean", CommonUtil.convertBeanToString(aoRegisterNycIdBean));
			LOG_OBJECT
					.Error("ApplicationException occurred in SecurityService in webservice call while creating/registering user in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLdap method -"
					+ "ApplicationException Exception occurred in webservice call while registering user in LDAP. /n");
			throw loAppEx;
		}

		setMoState("Transaction Success:: SecurityService: createUserInLdap method - Successfully registered a new user in LDAP. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();

		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. Time Taken(seconds):: "
						+ liTimediff);
		return loMapWebServRetrndParam;
	}

	/**
	 * This method is used for new user registration in LDAP
	 * 
	 * @param aoRegisterNycIdBean contains details of new user while registering
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	public Map<String, Object> newTokenGenerationForLostToken(String asEmailId, SqlSession aoMyBatisSession)
			throws ApplicationException, ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			LOG_OBJECT.Debug("SecurityService:  method:newTokenGenerationForLostToken ::1:: ");
			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ asEmailId }));
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::2:: ");
			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::3:: ");

			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_NEW_TOKEN_GEN_EMAIL);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::4:: ");
			List loMsgActivityList = new ArrayList();
			loMsgActivityList.add("Approval_Email_Validation_Token");
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);
			LOG_OBJECT
					.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. ::5:: ");
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT
					.Error("MalformedURLException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loMalEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - MalformedURLException Exception "
					+ "occurred in SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - "
					+ "RemoteException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT
					.Error("ServiceException Exception occurred in SecurityService in webservice call while creating/registering new user in LDAP:",
							loSerEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLDAP method - ServiceException Exception occurred in"
					+ " SecurityService in webservice call while creating/registering new user in LDAP:. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("RegisterNycIdBean", CommonUtil.convertBeanToString(asEmailId));
			LOG_OBJECT
					.Error("ApplicationException occurred in SecurityService in webservice call while creating/registering user in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: SecurityService: createUserInLdap method -"
					+ "ApplicationException Exception occurred in webservice call while registering user in LDAP. /n");
			throw loAppEx;
		}

		setMoState("Transaction Success:: SecurityService: createUserInLdap method - Successfully registered a new user in LDAP. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();

		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: registering new user - token generation. method:createUserInLDAP. Time Taken(seconds):: "
						+ liTimediff);
		return loMapWebServRetrndParam;
	}

	/**
	 * This method is used validating the email token (2nd step of user
	 * registration)
	 * 
	 * @param aoTokenMap has user email address and token(that was generated
	 *            during new user registration process)
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages, token)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	public Map<String, Object> validateEmailToken(Map<String, Object> aoTokenMap) throws ApplicationException,
			ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ (String) aoTokenMap.get("emailAddress") }));
			loStartRequestDataItem.add(new DataItem("EmailToken", new String[]
			{ (String) aoTokenMap.get("emailToken") }));
			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);

			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_ACCT_REG_TOKEN_VALID);
			List loMsgActivityList = null;
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in webservice call"
					+ " while validating email token (2nd step of user registration", loMalEx);
			setMoState("Transaction Failed:: SecurityService: validateEmailToken method "
					+ "-MalformedURLException Exception occurred in webservice call while validating user after registration. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error("RemoteException Exception occurred in SecurityService in webservice call "
					+ "while validating email token (2nd step of user registration", loRemEx);
			setMoState("Transaction Failed:: SecurityService: validateEmailToken method -RemoteException Exception occurred in "
					+ "webservice call while validating user after registration. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error("ServiceException Exception occurred in SecurityService in webservice call while "
					+ "validating email token (2nd step of user registration", loSerEx);
			setMoState("Transaction Failed:: SecurityService: validateEmailToken method -ServiceException "
					+ "Exception occurred in webservice call while validating user after registration. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			loAppEx.addContextData("aoTokenMap", aoTokenMap);
			LOG_OBJECT.Error(
					"ApplicationException Exception occurred in SecurityService in webservice call while validating email token "
							+ "(2nd step of user registration", loAppEx);
			setMoState("Transaction Failed:: SecurityService: validateEmailToken method "
					+ "-ApplicationExceptionException occurred in webservice call while validating user after registration. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: validateEmailToken method - Successfully validated user after registration. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: registering new user - Token validation. method:validateEmailToken. Time Taken(seconds):: "
						+ liTimediff);

		return loMapWebServRetrndParam;
	}

	/**
	 * This method gets the session for the password management
	 * 
	 * @return PasswordManagement bean, standard bean provided by web services
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public PasswordManagement getPasswordMgmtSession() throws MalformedURLException, RemoteException,
			ApplicationException, ServiceException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		PasswordManagement loService = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			loService = loAccountRequestLoginStub.getPasswordMgmtSession();
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in webservice call "
					+ "while getting password management session for password change via security questions", loMalEx);
			setMoState("Transaction Failed:: SecurityService: getPasswordMgmtSession method -MalformedURLException "
					+ "Exception occurred in SecurityService in webservice call"
					+ " while getting password management session for password change via security questions. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error(
					"RemoteException Exception occurred in SecurityService in webservice call while getting password management session "
							+ "for password change via security questions", loRemEx);
			setMoState("Transaction Failed:: SecurityService: getPasswordMgmtSession method -RemoteException"
					+ " Exception occurred in SecurityService in webservice call"
					+ " while getting password management session for password change via security questions. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error(
					"ServiceException Exception occurred in SecurityService in webservice call while getting password "
							+ "management session for password change via security questions", loSerEx);
			setMoState("Transaction Failed:: SecurityService: getPasswordMgmtSession method -"
					+ "ServiceException Exception occurred in SecurityService in webservice call while getting"
					+ " password management session for password change via security questions. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException Exception occurred in SecurityService in webservice call while getting"
							+ " password management session for password change via security questions", loAppEx);
			setMoState("Transaction Failed:: SecurityService: getPasswordMgmtSession method - ApplicationException "
					+ "Exception occurred in SecurityService in webservice call while getting password "
					+ "management session for password change via security questions. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: getPasswordMgmtSession method - "
				+ "Successfully got session for password change via security questions. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: pwd change via security questions step1. method:getPasswordMgmtSession. Time Taken(seconds):: "
						+ liTimediff);
		return loService;
	}

	/**
	 * This method retrieves challenge questions for the password change via
	 * security questions
	 * 
	 * @param aoPasswordManagementSession maintains sessions in different web
	 *            service calls during password change via security questions
	 *            (like getting challenge questions, submitting answers,
	 *            resetting new password)
	 * @param aoPwdChangeDetailsMap has fields containing user email id
	 * @return ForgotPasswordWSBean standard bean provided by web
	 *         services(contains challenge questions, status... etc fields)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public ForgotPasswordWSBean retrieveChallengeQuestions(PasswordManagement aoPasswordManagementSession,
			Map<String, Object> aoPwdChangeDetailsMap) throws ServiceException, MalformedURLException, RemoteException,
			Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		String lsUserEmailId = (String) aoPwdChangeDetailsMap.get("emailID");
		ForgotPasswordWSBean loForgotPasswordWSBean = null;
		try
		{
			// call VDX service to get the user dn
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			String lsUserDN = loAccountRequestLoginStub.userDnSearchVDX(lsUserEmailId);

			// call web service to retrieve challenge questions with USERDN as
			// input
			loForgotPasswordWSBean = new ForgotPasswordWSBean();
			if (null != lsUserDN && !lsUserDN.equalsIgnoreCase(""))
			{
				loForgotPasswordWSBean = loAccountRequestLoginStub.pwdChangeGetUserQuestions(
						aoPasswordManagementSession, lsUserDN);
			}
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in webservice call "
					+ "while retrieving Challenge Questions for password change via security questions ", loMalEx);
			setMoState("Transaction Failed:: SecurityService: retrieveChallengeQuestions method "
					+ "-MalformedURLException Exception occurred in SecurityService in webservice call while "
					+ "retrieving Challenge Questions for password change via security questions. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error("RemoteException Exception occurred in SecurityService in webservice call while "
					+ "retrieving Challenge Questions for password change via security questions ", loRemEx);
			setMoState("Transaction Failed:: SecurityService: retrieveChallengeQuestions method -"
					+ "RemoteException Exception occurred in SecurityService in webservice call while retrieving"
					+ " Challenge Questions for password change via security questions. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error(
					"ServiceException Exception occurred in SecurityService in webservice call while retrieving"
							+ "Challenge Questions for password change via security questions ", loSerEx);
			setMoState("Transaction Failed:: SecurityService: retrieveChallengeQuestions method -"
					+ "ServiceException Exception occurred in SecurityService in webservice call while retrieving "
					+ "Challenge Questions for password change via security questions. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException Exception occurred in SecurityService in webservice call while retrieving"
							+ " Challenge Questions for password change via security questions ", loAppEx);
			setMoState("Transaction Failed:: SecurityService: retrieveChallengeQuestions method -ApplicationException Exception occurred in "
					+ "SecurityService in webservice call while retrieving Challenge Questions for password change via security questions. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: retrieveChallengeQuestions method - Successfully got Challenge "
				+ "Questions for password change via security questions. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: pwd change via security questions step2. method: retrieveChallengeQuestions. Time Taken(seconds):: "
						+ liTimediff);

		return loForgotPasswordWSBean;
	}

	/**
	 * This method validates user security answers
	 * 
	 * @param aoPasswordManagementSession maintains sessions in different web
	 *            service calls during password change via security questions
	 *            (like getting challenge questions, submitting answers,
	 *            resetting new password)
	 * @param aoPwdChangeDetailsMap has fields containing user answers
	 * @return ForgotPasswordWSBean standard bean provided by web services
	 *         (contains challenge questions, status... etc fields)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public ForgotPasswordWSBean validateUserSecurityAnswers(PasswordManagement aoPasswordManagementSession,
			Map<String, Object> aoPwdChangeDetailsMap) throws ServiceException, MalformedURLException, RemoteException,
			Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		String lsUserDN = (String) aoPwdChangeDetailsMap.get("userDN");
		String[] lsUserResponses = new String[2];
		lsUserResponses[0] = (String) aoPwdChangeDetailsMap.get("Answer1");
		lsUserResponses[1] = (String) aoPwdChangeDetailsMap.get("Answer2");
		ForgotPasswordWSBean loForgotPasswordWSBean = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			loForgotPasswordWSBean = loAccountRequestLoginStub.pwdChangeSubmitAnswers(aoPasswordManagementSession,
					lsUserDN, lsUserResponses);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error(
					"MalformedURLException Exception occurred in SecurityService in webservice call while validating"
							+ " user security answers for password change via security questions ", loMalEx);
			setMoState("Transaction Failed:: SecurityService: validateUserSecurityAnswers method -MalformedURLException Exception occurred in "
					+ "SecurityService in webservice call while validating user security answers for password change via security questions . /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error(
					"RemoteException Exception occurred in SecurityService in webservice call while validating user security answers for"
							+ " password change via security questions ", loRemEx);
			setMoState("Transaction Failed:: SecurityService: validateUserSecurityAnswers method -RemoteException Exception occurred in "
					+ "SecurityService in webservice call while validating user security answers for password change via security questions . /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error(
					"ServiceException Exception occurred in SecurityService in webservice call while validating user security answers"
							+ " for password change via security questions ", loSerEx);
			setMoState("Transaction Failed:: SecurityService: validateUserSecurityAnswers method -ServiceException Exception occurred in "
					+ "SecurityService in webservice call while validating user security answers for password change via security questions . /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException Exception occurred in SecurityService in webservice call while validating user "
							+ "security answers for password change via security questions ", loAppEx);
			setMoState("Transaction Failed:: SecurityService: validateUserSecurityAnswers method -ApplicationException Exception occurred in "
					+ "SecurityService in webservice call while validating user security answers for password change via security questions . /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: validateUserSecurityAnswers method - Successfully call to webservice for "
				+ "validation of answers for password change via security questions. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: pwd change via security questions step3. method: validateUserSecurityAnswers. Time Taken(seconds):: "
						+ liTimediff);

		return loForgotPasswordWSBean;
	}

	/**
	 * This method reset user password
	 * 
	 * @param aoPasswordManagementSession maintains sessions in different web
	 *            service calls during password change via security questions
	 *            (like getting challenge questions, submitting answers,
	 *            resetting new password)
	 * @param aoPwdChangeDetailsMap has fields containing userdn and new
	 *            password
	 * @return ForgotPasswordWSBean standard bean provided by web services
	 *         (contains challenge questions, status... etc fields)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public ForgotPasswordWSBean resetUserPassword(PasswordManagement aoPasswordManagementSession,
			Map<String, Object> aoPwdChangeDetailsMap) throws ServiceException, MalformedURLException, RemoteException,
			Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		String lsUserDN = (String) aoPwdChangeDetailsMap.get("userDN");
		String lsPassword = (String) aoPwdChangeDetailsMap.get("newPassword");
		ForgotPasswordWSBean loForgotPasswordWSBean = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			loForgotPasswordWSBean = loAccountRequestLoginStub.pwdChangeSubmitNewPassword(aoPasswordManagementSession,
					lsUserDN, lsPassword);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error(
					"MalformedURLException Exception occurred in SecurityService in webservice call while resetting user password "
							+ "for password change via security questions ", loMalEx);
			setMoState("Transaction Failed:: SecurityService: resetUserPassword method -MalformedURLException Exception occurred in "
					+ "SecurityService in webservice call while resetting user password for password change via security questions. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error(
					"RemoteException Exception occurred in SecurityService in webservice call while resetting user password for "
							+ "password change via security questions ", loRemEx);
			setMoState("Transaction Failed:: SecurityService: resetUserPassword method -RemoteException Exception occurred in "
					+ "SecurityService in webservice call while resetting user password for password change via security questions. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error(
					"ServiceException Exception occurred in SecurityService in webservice call while resetting user password for "
							+ "password change via security questions ", loSerEx);
			setMoState("Transaction Failed:: SecurityService: resetUserPassword method -ServiceException Exception occurred in "
					+ "SecurityService in webservice call while resetting user password for password change via security questions. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"ApplicationException Exception occurred in SecurityService in webservice call while resetting user password for "
							+ "password change via security questions ", loAppEx);
			setMoState("Transaction Failed:: SecurityService: resetUserPassword method -ApplicationException Exception occurred in "
					+ "SecurityService in webservice call while resetting user password for password change via security questions. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: resetUserPassword method - Successfully call to webservice for "
				+ "validation of answers for password change via security questions. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("SecuritySerice: password reset. method:resetUserPassword. Time Taken(seconds):: "
				+ liTimediff);

		return loForgotPasswordWSBean;
	}

	/**
	 * This method generates the token when user opted for password change via
	 * email (Step 1)
	 * 
	 * @param loPwdChangeDetailsMap has fields containing user email id
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "unchecked", "static-access", "rawtypes" })
	public Map<String, Object> pwdResetTokenGenViaEmail(Map<String, Object> aoPwdChangeDetailsMap)
			throws ApplicationException, ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			String lsToken = "";
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			String lsUserEmailId = (String) aoPwdChangeDetailsMap.get("emailID");
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ lsUserEmailId }));
			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);

			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_EMAIL_PWD_RESET_TOKEN_GEN);

			List<String> loMsgActivityList = new ArrayList<String>();
			loMsgActivityList.add("Approval_Password_Reset_Token");

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
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT
					.Error("MalformedURLException Exception occurred in service call while creating token for password change via email",
							loMalEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenGenViaEmail method -MalformedURLException Exception occurred in"
					+ "webservice call while creating token for password change via email. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in webservice call while creating token for password change via email",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenGenViaEmail method -RemoteException Exception occurred in"
					+ " webservice call while creating token for password change via email. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT
					.Error("ServiceException Exception occurred in webservice call while creating token for password change via email",
							loSerEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenGenViaEmail method -ServiceException Exception occurred in "
					+ "webservice call while creating token for password change via email. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Exception occurred in webservice call while creating token for password change via email",
							loAppEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenGenViaEmail method -ApplicationException "
					+ "Exception occurred in webservice call while creating token for password change via email. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: pwdResetTokenGenViaEmail method - "
				+ "Successfully created token for password reset via email. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: pwd change via email step1. method: pwdResetTokenGenViaEmail. Time Taken(seconds):: "
						+ liTimediff);

		return loMapWebServRetrndParam;
	}

	/**
	 * This method validates the token generated as when user opted for password
	 * change via email (Step 2)
	 * 
	 * @param aoTokenMap has fields containing user email id and token that was
	 *            generated as part of password change via email
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked", "static-access" })
	public Map<String, Object> pwdResetTokenValidationViaEmail(Map<String, Object> aoTokenMap)
			throws ApplicationException, ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ (String) aoTokenMap.get("emailAddress") }));
			loStartRequestDataItem.add(new DataItem("token", new String[]
			{ (String) aoTokenMap.get("emailToken") }));
			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);
			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_EMAIL_PWD_RESET_TOKEN_VALID);

			List loMsgActivityList = new ArrayList();
			/*** Start R6.3 QC8702 ***/
			//loMsgActivityList.add("Approval_Email_Validation_Token");
			loMsgActivityList.add("Approval_Password_Reset_Token");
			/*** End R6.3 QC8702 ***/
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT
					.Error("MalformedURLException Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loMalEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenValidationViaEmail method -MalformedURLException "
					+ "Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenValidationViaEmail method "
					+ "-RemoteException Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT
					.Error("ServiceException Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loSerEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenValidationViaEmail method -ServiceException "
					+ "Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Exception occurred in SecurityService in webservice call while validating pwd reset (via mail) in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetTokenValidationViaEmail method -ApplicationException Exception occurred in "
					+ "SecurityService in webservice call while validating pwd reset (via mail) in LDAP. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: pwdResetTokenValidationViaEmail method - Successfully made web service call for "
				+ "validation of token, generated as part of password reset via email. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("SecuritySerice: pwd change via email step2. method: pwdResetTokenValidationViaEmail. Time Taken(seconds):: "
						+ liTimediff);
		return loMapWebServRetrndParam;

	}

	/**
	 * This method resets the user password when opted for change password via
	 * email
	 * 
	 * @param aoPwdChangeDetailsMap has fields related to password reset (token,
	 *            email id and new password)
	 * @return Map<String, Object> has status fields returned after call to web
	 *         service (passed, denied and status messages)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "unchecked", "static-access" })
	public Map<String, Object> pwdResetViaEmail(Map<String, Object> aoPwdChangeDetailsMap) throws ApplicationException,
			ServiceException, MalformedURLException, RemoteException, Exception
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map<String, Object> loMapWebServRetrndParam = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = null;

			List<DataItem> loStartRequestDataItem = new ArrayList<DataItem>();
			String lsUserToken = (String) aoPwdChangeDetailsMap.get("token");
			String lsUserEmailId = (String) aoPwdChangeDetailsMap.get("emailID");
			String lsUserNewPassword = (String) aoPwdChangeDetailsMap.get("newPassword");
			loStartRequestDataItem.add(new DataItem("EmailAddress", new String[]
			{ lsUserEmailId }));
			loStartRequestDataItem.add(new DataItem("token", new String[]
			{ lsUserToken }));
			loStartRequestDataItem.add(new DataItem("Password", new String[]
			{ lsUserNewPassword }));

			DataItem[] loDataItemArray = loStartRequestDataItem.toArray(new DataItem[loStartRequestDataItem.size()]);
			String lsProvisioningDN = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					ApplicationConstants.PROPERTY_PROV_DN_EMAIL_PASSWORD_RESET);

			List<String> loMsgActivityList = new ArrayList<String>();
			/*** Start R6.3 QC8702 ***/
			loMsgActivityList.add("Approval_Password_Reset_Token");
			/*** End R6.3 QC8702 ***/
			loMapWebServRetrndParam = loAccountRequestLoginStub.executeService(loDataItemArray, loMsgActivityList,
					lsProvisioningDN);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in webservice call "
					+ "while resetting password for password reset via email", loMalEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetViaEmail method -MalformedURLException "
					+ "Exception occurred in SecurityService in webservice call while resetting password for password reset via email. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in SecurityService in webservice call while resetting password for password reset via email",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetViaEmail method -RemoteException "
					+ "Exception occurred in SecurityService in webservice call while resetting password for password reset via email. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error("ServiceException Exception occurred in SecurityService in webservice "
					+ "call while resetting password for password reset via email", loSerEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetViaEmail method -"
					+ "ServiceException Exception occurred in SecurityService in webservice "
					+ "call while resetting password for password reset via email. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Exception occurred in SecurityService in webservice call"
					+ " while resetting password for password reset via email", loAppEx);
			setMoState("Transaction Failed:: SecurityService: pwdResetViaEmail method -ApplicationException "
					+ "Exception occurred in SecurityService in webservice call while resetting password for password reset via email. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: pwdResetViaEmail method - Successfully made web service"
				+ " call for resetting password for password change via email. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("SecuritySerice: pwd reset via email step3 . method: pwdResetViaEmail. Time Taken(seconds):: "
				+ liTimediff);

		return loMapWebServRetrndParam;

	}

	/**
	 * This method provides user details with input as userdn
	 * 
	 * @param asUserDN USERDN of the user whose details we want to search
	 * @return Map<String, Object> has fields related to user name( first name,
	 *         middle initial and last name)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Map<String, Object> userDetailsSearchOnUserDN(String asUserDN) throws MalformedURLException,
			RemoteException, ServiceException, ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		Map loUserDetailRtrndMap = null;
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			loUserDetailRtrndMap = loAccountRequestLoginStub.userDetailsSearchOnUserDN(asUserDN);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in"
					+ " webservice call while getting user details with search parameter as USERDN", loMalEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -MalformedURLException Exception occurred in "
					+ "SecurityService in webservice call while getting user details with search parameter as USERDN. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error("RemoteException Exception occurred in SecurityService in webservice call while "
					+ "getting user details with search parameter as USERDN", loRemEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -RemoteException Exception occurred in "
					+ "SecurityService in webservice call while getting user details with search parameter as USERDN. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error("ServiceException Exception occurred in SecurityService in webservice call while"
					+ " getting user details with search parameter as USERDN", loSerEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -ServiceException Exception occurred in "
					+ "SecurityService in webservice call while getting user details with search parameter as USERDN. /n");
			throw loSerEx;
		}
		setMoState("Transaction Success:: SecurityService: userDetailsSearchOnUserDN method - Successfully got user details, search on USERDN. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("SecuritySerice: method: userDetailsSearchOnUserDN. Time Taken(seconds):: " + liTimediff);
		return loUserDetailRtrndMap;
	}

	/**
	 * This method gives user dn with search on email id
	 * 
	 * @param asEmailId email id of the user, whose USERDN we want to search
	 * @return String contains USERDN
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public String userSearchOnEmailIdVDX(String asEmailId) throws MalformedURLException, RemoteException,
			ServiceException, ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		String lsUserDN = "";
		try
		{
			AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
			lsUserDN = loAccountRequestLoginStub.userDnSearchVDX(asEmailId);
		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT.Error("MalformedURLException Exception occurred in SecurityService in webservice call while "
					+ "getting USERDN, searching on user email id as parameter", loMalEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -MalformedURLException Exception occurred in "
					+ "SecurityService in webservice call while getting USERDN, searching on user email id as parameter. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT.Error(
					"RemoteException Exception occurred in SecurityService in webservice call while getting USERDN, "
							+ "searching on user email id as parameter", loRemEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -RemoteException Exception occurred in "
					+ "SecurityService in webservice call while getting USERDN, searching on user email id as parameter. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT.Error(
					"ServiceException Exception occurred in SecurityService in webservice call while getting USERDN,"
							+ " searching on user email id as parameter", loSerEx);
			setMoState("Transaction Failed:: SecurityService: userDetailsSearchOnUserDN method -ServiceException Exception occurred in"
					+ " SecurityService in webservice call while getting USERDN, searching on user email id as parameter. /n");
			throw loSerEx;
		}
		setMoState("Transaction Success:: SecurityService: userSearchOnEmailIdVDX method -"
				+ " Successfully got USERDN while searching on user email id. /n");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("SecuritySerice: method: userSearchOnEmailIdVDX. Time Taken(seconds):: " + liTimediff);
		return lsUserDN;

	}

	/**
	 * This method updates user profile(first, middle and last name in LDAP)
	 * when provided with USERDN
	 * 
	 * @param aoUserDetailUpdateMap has fields related to user profile(USERDN
	 *            and name to be updated -first name, middle initial, last name)
	 * @return boolean status whether the profile updation is success or failure
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public boolean UpdateUserProfileInLdap(Map<String, Object> aoUserDetailUpdateMap) throws MalformedURLException,
			RemoteException, ServiceException, ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		// added if else condition for Release 5 local changes
		boolean lbUpdateStatus = false;
		try
		{
			if (!ApplicationConstants.LOCAL_ENVIRONMENT.equalsIgnoreCase(PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, "PROPERTY_LOGIN_ENVIRONMENT")))
			{
				AccountRequestLoginStub loAccountRequestLoginStub = new AccountRequestLoginStub();
				lbUpdateStatus = loAccountRequestLoginStub.UpdateUserProfileInLdap(aoUserDetailUpdateMap);
			}
			else
			{
				lbUpdateStatus = true;
			}
			// R5 changes Ends

		}
		catch (MalformedURLException loMalEx)
		{
			LOG_OBJECT
					.Error("MalformedURLException Exception occurred in SecurityService in webservice call while updating user profile in LDAP",
							loMalEx);
			setMoState("Transaction Failed:: SecurityService: UpdateUserProfileInLdap method -MalformedURLException Exception occurred in "
					+ "SecurityService in webservice call while updating user profile in LDAP. /n");
			throw loMalEx;
		}
		catch (RemoteException loRemEx)
		{
			LOG_OBJECT
					.Error("RemoteException Exception occurred in SecurityService in webservice call while updating user profile in LDAP",
							loRemEx);
			setMoState("Transaction Failed:: SecurityService: UpdateUserProfileInLdap method -RemoteException Exception occurred in "
					+ "SecurityService in webservice call while updating user profile in LDAP. /n");
			throw loRemEx;
		}
		catch (ServiceException loSerEx)
		{
			LOG_OBJECT
					.Error("ServiceException Exception occurred in SecurityService in webservice call while updating user profile in LDAP",
							loSerEx);
			setMoState("Transaction Failed:: SecurityService: UpdateUserProfileInLdap method -ServiceException Exception occurred in "
					+ "SecurityService in webservice call while updating user profile in LDAP. /n");
			throw loSerEx;
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Exception occurred in SecurityService in webservice call while updating user profile in LDAP",
							loAppEx);
			setMoState("Transaction Failed:: SecurityService: UpdateUserProfileInLdap method -ApplicationException Exception occurred in "
					+ "SecurityService in webservice call while updating user profile in LDAP. /n");
			throw loAppEx;
		}
		setMoState("Transaction Success:: SecurityService: UpdateUserProfileInLdap method - Successfully updated user profile in LDAP. /n");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("SecuritySerice: method: UpdateUserProfileInLdap. Time Taken(seconds):: " + liTimediff);

		return lbUpdateStatus;
	}
}
