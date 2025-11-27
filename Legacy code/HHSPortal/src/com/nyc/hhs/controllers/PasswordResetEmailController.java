package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.novell.www.pwdmgt.service.ForgotPasswordWSBean;
import com.novell.www.pwdmgt.service.PasswordManagement;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.PortalUtil;

/**
 * This controller is for Password reset corresponding to email Id/ NYC Id.
 * password reset process: 1. via security questions, 2. via email
 */
public class PasswordResetEmailController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(PasswordResetEmailController.class);

	Boolean emailValidated;
	String lsQuestion1;
	String lsQuestion2;
	private static final String EMAIL_ALREADY_EXIST_IN_NYC = "This email already exists for a NYC.ID Account. ";
	private static final String EMAIL_ALREADY_EXIST_IN_NYC_1 = "Please enter a different email or Cancel to return to the login page.";
	private static final String EMAIL_DOES_NOT_EXIST_NYC = "This email is not associated with a NYC.ID account.";
	private static final String EMAIL_DOES_NOT_EXIST_NYC_1 = " Please enter an email associated with a NYC.ID Account.";
	private static final String REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	private static final String PASSWORD_NOT_OK = "Your password cannot contain your first name, last name, email address"
			+ " or the word 'password'. Please enter a new password.";

	/**
	 * This method is to render the next page depending on the action, password
	 * reset (via email or via security quest. process)
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		aoResponse.setContentType("text/html");
		ModelAndView loModelAndView = null;
		String lsFormPath = "passwordreset";
		String lsAction = "";
		try
		{

			// This condition gets the next action to be performed
			if (aoRequest.getParameter("lsAction") != null)
			{
				lsAction = aoRequest.getParameter("lsAction");
			}

			// This condition renders the security question screen while
			// changing
			// password (via security questions)
			if (null != lsAction && lsAction.equals(ApplicationConstants.PASSWOR_RESET_SECURITY_QUESTIONS))
			{
				lsFormPath = ApplicationConstants.PASSWOR_RESET_SECURITY_QUESTIONS;
				aoRequest.setAttribute("transactionStatus", "failed");
				aoRequest.setAttribute("transactionMessage",
						PasswordResetEmailController.REQUEST_COULD_NOT_BE_COMPLETED);
				aoRequest.setAttribute("Question1", "");
				aoRequest.setAttribute("Question2", "");
				ForgotPasswordWSBean loForgotPasswordWSBean = new ForgotPasswordWSBean();
				loForgotPasswordWSBean = (ForgotPasswordWSBean) ApplicationSession.getAttribute(aoRequest, true,
						"ForgotPasswordWSBean");
				if (null != loForgotPasswordWSBean && null != loForgotPasswordWSBean.getChallengeQuestions())
				{
					String lsQuestion[] = loForgotPasswordWSBean.getChallengeQuestions();
					lsQuestion1 = lsQuestion[0];
					lsQuestion2 = lsQuestion[1];
					aoRequest.setAttribute("Question1", lsQuestion1);
					aoRequest.setAttribute("Question2", lsQuestion2);
					aoRequest.setAttribute("transactionStatus", null);
				}
				else
				{
					lsFormPath = "passwordreset";
				}
			}
			String lsTknStatus = null;
			// This condition brings the password reset screen (via security
			// questions)
			if (null != lsAction && lsAction.equalsIgnoreCase("navigateToResetPwd"))
			{
				lsFormPath = "pwdresetnewpwd";
			}

			// This condition renders the password reset screen once the token
			// is
			// validated for password reset (via email)
			if ("validPasswordResetEmailToken".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					"validPasswordResetEmailToken")))
			{
				lsFormPath = "emailpwdresetnewpwd";
				Map loValidateEmailTokenServiceOutMap = (Map) validatePasswordResetEmailToken(aoRequest);
				String lsStatus = (String) loValidateEmailTokenServiceOutMap.get("serviceStatus");
				if (lsStatus.equalsIgnoreCase("success"))
				{
					lsFormPath = "emailpwdresetnewpwd";
				}
				/*****[Start]Add at R6.2 for QC 6710 *******/
				lsTknStatus = (String) loValidateEmailTokenServiceOutMap.get(HHSConstants.PWD_RESET_TOKEN_STATUS_ID);
				/*****[End]Add at R6.2 for QC 6710 *******/	
				
				/**** Start R6.3 QC8702 ****/
				try
				{
					String serviceOutput = loValidateEmailTokenServiceOutMap.get("serviceOutput").toString();
					LOG_OBJECT.Debug("PWD serviceOutput : "+serviceOutput) ;
									
					if (serviceOutput != null &&  serviceOutput.contains("Denial") &&  serviceOutput.contains("Stored Token:") && serviceOutput.contains("Received Token:"))
					{
					     lsTknStatus = "Denied";
					}
					LOG_OBJECT.Debug("PWD reset Token status : [New]"+ lsTknStatus +"--- [Old]"+lsStatus)  ;
				}
				catch (Exception e)	{ }
				/**** Start R6.3 QC8702 ****/
			}

			// This condition renders the password reset screen incase password
			// composition is not ok (while resetting pwd via email)
			if ("validateAndRedirectResetPwd".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					"validateAndRedirectResetPwd")))
			{
				lsFormPath = "emailpwdresetnewpwd";
			}
			
			LOG_OBJECT.Debug("TransactionMessage for pwd reset :[New:]"+ lsTknStatus +"--- [Old]" + aoRequest.getAttribute("transactionStatus") +"---"+aoRequest.getAttribute("transactionMessage"));
						
			/*****[Start]Add at R6.2 for QC 6710 *******/
			if(  lsTknStatus!= null && lsTknStatus.equalsIgnoreCase("Denied") ){
				// This condition is for pwd reset token expired 
				aoRequest.setAttribute("transactionStatus", "failed");
				aoRequest.setAttribute("transactionMessage", HHSConstants.INVALID_TOKEN_TO_RESET_PWD_MSG);
			/*****[End]Add at R6.2 for QC 6710 *******/
			}else{
				// This condition is for putting the status messages at top of the
				// screen
				if (!"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
				{
					aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
					aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
				}
			}
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in PasswordResetEmailController ", loEx);
		}
		loModelAndView = new ModelAndView(lsFormPath);
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in PasswordResetEmailController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in PasswordResetEmailController ", aoEx);
		}
		return loModelAndView;
	}

	/**
	 * This method decide the execution flow for password reset (via email or
	 * via security quest. process)
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		long loStartTime = System.currentTimeMillis();
		Channel loChannelObj = new Channel();
		String lsAction = "", lsTransactionStatusMsg = PasswordResetEmailController.REQUEST_COULD_NOT_BE_COMPLETED;
		String lsNextAction = aoRequest.getParameter("next_action"), lsTransactionStatus = "failed", lsEmailAddress = "";
		boolean lbViaEmail = false;
		boolean lbPasswordOK = true;
		try
		{
			// password reset VIA SECURITY QUESTIONS step:1 get security
			if (null != lsNextAction && lsNextAction.equals("viasecurityquestions"))
			{
				String lsEmailAddressForQues = aoRequest.getParameter("emailAddress");
				ApplicationSession.setAttribute(lsEmailAddressForQues, aoRequest, "emailID");
				ForgotPasswordWSBean loForgotPasswordWSBean = retrieveUserSecurityQuestion(aoRequest, loChannelObj);
				if (null != loForgotPasswordWSBean && null != loForgotPasswordWSBean.getUsers()
						&& loForgotPasswordWSBean.getUsers().length == 1)
				{
					lsTransactionStatus = "";
					lsTransactionStatusMsg = "";
					ApplicationSession.setAttribute(loForgotPasswordWSBean, aoRequest, "ForgotPasswordWSBean");
					aoResponse.setRenderParameter("lsAction", ApplicationConstants.PASSWOR_RESET_SECURITY_QUESTIONS);
				}
				else
				{
					lsTransactionStatusMsg = PasswordResetEmailController.EMAIL_DOES_NOT_EXIST_NYC
							+ EMAIL_DOES_NOT_EXIST_NYC_1;
					aoResponse.setRenderParameter("lsInvalidEmail", "emailNotValidated");
				}
			}
			// password reset VIA SECURITY QUESTIONS step:2 verify answers
			else if (null != lsNextAction && lsNextAction.equals("navigateToResetPwd"))
			{
				if (null != aoRequest.getParameter("securityAns1") && null != aoRequest.getParameter("securityAns2"))
				{
					aoResponse.setRenderParameter("lsAction", ApplicationConstants.PASSWOR_RESET_SECURITY_QUESTIONS);
					ForgotPasswordWSBean loForgotPasswordWSBean = validateUserAnswer(aoRequest, loChannelObj);
					if (null != loForgotPasswordWSBean && loForgotPasswordWSBean.isError())
					{
						lsTransactionStatusMsg = "Your answers are not correct";
						loForgotPasswordWSBean = retrieveUserSecurityQuestion(aoRequest, loChannelObj);
						if (loForgotPasswordWSBean.getUsers().length == 1)
						{
							ApplicationSession.setAttribute(loForgotPasswordWSBean, aoRequest, "ForgotPasswordWSBean");
						}
						aoResponse
								.setRenderParameter("lsAction", ApplicationConstants.PASSWOR_RESET_SECURITY_QUESTIONS);
					}
					else
					{
						lsTransactionStatus = "";
						lsTransactionStatusMsg = "";
						aoResponse.setRenderParameter("lsAction", "navigateToResetPwd");
					}
				}
			}
			// password reset VIA SECURITY QUESTIONS step:3
			else if (null != lsNextAction && lsNextAction.equals("navigateToLoginScreen"))
			{
				if (null != aoRequest.getParameter("newNycIdPwd") && null != aoRequest.getParameter("confirmNewPwd"))
				{
					lsAction = "pwdresetnewpwd";
					Map loReturnedMap = resetPwdViaQuesAndHandleStatus(aoRequest, aoResponse, loChannelObj, lbViaEmail);
					lbViaEmail = (Boolean) loReturnedMap.get("lbViaEmail");
					lbPasswordOK = (Boolean) loReturnedMap.get("lbPasswordOK");
					if (!lbPasswordOK)
					{
						lsTransactionStatusMsg = PasswordResetEmailController.PASSWORD_NOT_OK;
					}
				}
			}
			// password reset VIA EMAIL step:1 generate token
			else if (null != lsNextAction && lsNextAction.equals("viaemail"))
			{
				lsEmailAddress = aoRequest.getParameter("emailAddress");
				Map<String, Object> loPwdChangeDetailsMap = new HashMap<String, Object>();
				loPwdChangeDetailsMap.put("emailID", lsEmailAddress);
				loChannelObj.setData("aoPwdChangeDetailsMap", loPwdChangeDetailsMap);
				TransactionManager.executeTransaction(loChannelObj, "ForgotPasswordResetViaEmail");
				loPwdChangeDetailsMap = new HashMap<String, Object>();
				loPwdChangeDetailsMap = (Map<String, Object>) loChannelObj.getData("aoForgotPasswordRtrndMap");
				String lsServiceStatus = (String) loPwdChangeDetailsMap.get("serviceStatus");
				String loServiceOutputList = (String) loPwdChangeDetailsMap.get("serviceOutput");
				if ("error".equalsIgnoreCase(lsServiceStatus))
				{
					lsTransactionStatusMsg = loServiceOutputList;
					if (lsTransactionStatusMsg.contains("Email ID not unique"))
					{
						lsTransactionStatusMsg = PasswordResetEmailController.EMAIL_ALREADY_EXIST_IN_NYC
								+ EMAIL_ALREADY_EXIST_IN_NYC_1;
					}
					else if (lsTransactionStatusMsg.contains("Email not Found"))
					{
						lsTransactionStatusMsg = PasswordResetEmailController.EMAIL_DOES_NOT_EXIST_NYC
								+ EMAIL_DOES_NOT_EXIST_NYC_1;
					}
				}
				else
				{
					lsTransactionStatus = "passed";
					lbViaEmail = sendPwdChngViaEmailNotification(aoRequest, aoResponse, loChannelObj, lsEmailAddress,
							lbViaEmail, loServiceOutputList);
				}
			}
			// password reset VIA EMAIL step:2 validate the link received
			else if (null != lsNextAction && lsNextAction.equals("viaemailpwdresetToLoginScreen"))
			{
				if (null != aoRequest.getParameter("newNycIdPwd") && null != aoRequest.getParameter("confirmNewPwd"))
				{
					lsAction = "emailpwdresetnewpwd";
					Map loReturnedMap = validateAndRedirectResetPwd(aoRequest, aoResponse, loChannelObj, lbViaEmail);
					lbViaEmail = (Boolean) loReturnedMap.get("lbViaEmail");
					lbPasswordOK = (Boolean) loReturnedMap.get("lbPasswordOK");
					if (!lbPasswordOK)
					{
						lsTransactionStatusMsg = PasswordResetEmailController.PASSWORD_NOT_OK;
					}
				}
			}
			if (!lbViaEmail)
			{
				aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
				aoResponse.setRenderParameter("transactionMessage", lsTransactionStatusMsg);
			}
		}
		catch (ApplicationException aoFbAppEx)
		{
			catchApplicationException(aoResponse, lsAction, lsTransactionStatusMsg, aoFbAppEx);
		}
		catch (Exception loEx)
		{
			catchThrowable(aoResponse, lsAction, lsTransactionStatusMsg, loEx);
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in PasswordResetEmailController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in PasswordResetEmailController ", aoEx);
		}
	}

	/**
	 * This method validates token generated during password reset via email and
	 * if validated directs user to password reset screen This functional is
	 * updated in R4: Notification framework change
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param abViaEmail boolean value that states whether validation is success
	 *            or failure
	 * @return boolean status stating whether validation is success or failure
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Map validateAndRedirectResetPwd(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj,
			boolean abViaEmail) throws ApplicationException
	{
		String lsEmailAddress;
		String lsNycUserName = "";
		String lsFirstName = "";
		String lsMiddleName = "";
		String lsLastName = "";
		Map loReturnedMap = new HashMap();
		Map loNameMap = null;
		lsEmailAddress = (String) ApplicationSession.getAttribute(aoRequest, true, "loEmailAddress");
		loReturnedMap.put("lbPasswordOK", true);
		loReturnedMap.put("lbViaEmail", abViaEmail);
		loNameMap = getUserNameForEmailId(lsEmailAddress);
		lsFirstName = (String) loNameMap.get("FirstName");
		lsMiddleName = (String) loNameMap.get("Initials");
		lsLastName = (String) loNameMap.get("LastName");
		lsNycUserName = lsFirstName + " " + lsMiddleName + " " + lsLastName;
		Map<String, Object> loPwdChangeDetailsMap = resetNewPwdViaEmail(aoRequest, aoChannelObj, lsEmailAddress,
				lsFirstName, lsLastName);
		String lsPasswordOKStatus = (String) loPwdChangeDetailsMap.get("newPasswordOK");
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		if (lsPasswordOKStatus.equalsIgnoreCase("false"))
		{
			loReturnedMap.put("lbViaEmail", false);
			loReturnedMap.put("lbPasswordOK", false);
			aoResponse.setRenderParameter("lsAction", "emailpwdresetnewpwd");
			aoResponse.setRenderParameter("validateAndRedirectResetPwd", "validateAndRedirectResetPwd");
		}
		else
		{
			loReturnedMap.put("lbPasswordOK", true);
			String lsServiceStatus = (String) loPwdChangeDetailsMap.get("serviceStatus");
			if ("error".equalsIgnoreCase(lsServiceStatus))
			{
				aoResponse.setRenderParameter("lsAction", "emailpwdresetnewpwd");
			}
			else
			{
				try
				{
					generateNotification(aoChannelObj, lsUserId, lsNycUserName, lsOrgId);
				}
				catch (Exception aoExp)
				{
					LOG_OBJECT.Error("Error occured while sending  NT037 notification ", aoExp);
				}
				String lsLoginPagePath = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
						+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ "&_pageLabel=portlet_hhsweb_portal_login_page&resetPassword=resetPassword";
				try
				{
					loReturnedMap.put("lbViaEmail", true);
					abViaEmail = true;
					aoResponse.sendRedirect(lsLoginPagePath);
				}
				catch (IOException aoExp)
				{
					throw new ApplicationException("Not able to redirect to login page page after reseting password.",
							aoExp);
				}
			}
		}
		LOG_OBJECT.Debug("method validateAndRedirectResetPwd :: return loReturnedMap  ::: " + loReturnedMap); 
		return loReturnedMap;
	}

	/**
	 * This method catch Throwable class exception
	 * 
	 * @param aoResponse decides the next execution flow
	 * @param asAction states on which action the exception is thrown
	 * @param asTransactionStatusMsg message that needs to be displayed on
	 *            screen
	 * @param aoFbAppEx
	 */
	private void catchThrowable(ActionResponse aoResponse, String asAction, String asTransactionStatusMsg,
			Throwable aoFbAppEx)
	{
		aoResponse.setRenderParameter("transactionStatus", "failed");
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		aoResponse.setRenderParameter("lsAction", asAction);
		LOG_OBJECT.Error("Error occurred while registering error message", aoFbAppEx);
	}

	/**
	 * This method catch Application class exception
	 * 
	 * @param aoResponse decides the next execution flow
	 * @param asAction states on which action the exception is thrown
	 * @param asTransactionStatusMsg message that needs to be displayed on
	 *            screen
	 * @param aoFbAppEx
	 */
	private void catchApplicationException(ActionResponse aoResponse, String asAction, String asTransactionStatusMsg,
			ApplicationException aoFbAppEx)
	{
		aoResponse.setRenderParameter("transactionStatus", "failed");
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		aoResponse.setRenderParameter("lsAction", asAction);
		LOG_OBJECT.Error("Error occured while registering error message ", aoFbAppEx);
	}

	/**
	 * This method sends notification to user with token when user opted for
	 * password change via email
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param asEmailAddress email address of user
	 * @param abViaEmail boolean flag stating success or failure of transaction
	 * @param aoServiceOutputList states message of success or failure
	 * @return boolean status states success or failure of transaction
	 * @throws ApplicationException
	 */
	private boolean sendPwdChngViaEmailNotification(ActionRequest aoRequest, ActionResponse aoResponse,
			Channel aoChannelObj, String asEmailAddress, boolean abViaEmail, String aoServiceOutputList)
			throws ApplicationException
	{
		pwdChngViaEmailTokenNotification(aoRequest, aoChannelObj, asEmailAddress, aoServiceOutputList);
		String lsLoginPagePath = aoRequest.getScheme()
				+ "://"
				+ aoRequest.getServerName()
				+ ":"
				+ aoRequest.getServerPort()
				+ aoRequest.getContextPath()
				+ ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_login_page&TokenSentViaEmail=TokenSentViaEmail&pwdResetNotification="
				+ "pwdResetNotification&accoutRequestmodule=accoutRequestmodule";
		try
		{
			abViaEmail = true;
			aoResponse.sendRedirect(lsLoginPagePath);
		}
		catch (IOException aoExp)
		{
			throw new ApplicationException("Not able to redirect  to Login page after sending Email Token user.", aoExp);
		}
		return abViaEmail;
	}

	/**
	 * This method modified as a part of release 3.1.0 to fix NT037 issue
	 * 
	 * This method reset Password Via Questions and handles status (redirect to
	 * login page if success)
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param abViaEmail boolean flag stating success or failure of transaction
	 * @return boolean status states success or failure of transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Map resetPwdViaQuesAndHandleStatus(ActionRequest aoRequest, ActionResponse aoResponse,
			Channel aoChannelObj, boolean abViaEmail) throws ApplicationException
	{
		String lsNycUserName = "";
		String lsFirstName = "";
		String lsMiddleName = "";
		String lsLastName = "";
		Map loReturnedMap = new HashMap();
		Map loNameMap = null;
		Map loPwdResetMapViaSecQues = null;
		String lsEmail = (String) ApplicationSession.getAttribute(aoRequest, true, "emailID");
		String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
				PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		loReturnedMap.put("lbPasswordOK", true);
		loReturnedMap.put("lbViaEmail", abViaEmail);
		loNameMap = getUserNameForEmailId(lsEmail);
		lsFirstName = (String) loNameMap.get("FirstName");
		lsMiddleName = (String) loNameMap.get("Initials");
		lsLastName = (String) loNameMap.get("LastName");
		lsNycUserName = lsFirstName + " " + lsMiddleName + " " + lsLastName;
		loPwdResetMapViaSecQues = resetPwdViaSecurityQues(aoRequest, aoChannelObj, lsEmail, lsFirstName, lsLastName);
		String lsPasswordOKStatus = (String) loPwdResetMapViaSecQues.get("newPasswordOK");
		if (lsPasswordOKStatus.equalsIgnoreCase("false"))
		{
			loReturnedMap.put("lbViaEmail", false);
			loReturnedMap.put("lbPasswordOK", false);
			aoResponse.setRenderParameter("lsAction", "navigateToResetPwd");
		}
		else
		{
			loReturnedMap.put("lbPasswordOK", true);
			ForgotPasswordWSBean loForgotPasswordWSBean = (ForgotPasswordWSBean) loPwdResetMapViaSecQues
					.get("loForgotPasswordWSBean");
			if (loForgotPasswordWSBean.isError())
			{
				aoResponse.setRenderParameter("lsAction", "pwdresetnewpwd");
			}
			else
			{
				String lsLoginPage = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
						+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
						+ "&_pageLabel=portlet_hhsweb_portal_login_page&resetPassword=resetPassword";
				//fix for NT037 issue - start
				Map<String, String> loChangeEmailMap = (Map<String, String>) aoChannelObj.getData("aoPwdChangeDetailsMap");
				LOG_OBJECT.Debug("aoPwdChangeDetailsMap MAP 2 ::: " + loChangeEmailMap);
				LOG_OBJECT.Debug("lsEmail  ::: " + lsEmail);
				if(null!=loChangeEmailMap && null!=lsEmail && lsEmail!=""){
					loChangeEmailMap.put("emailID", lsEmail);
					aoChannelObj.setData("aoPwdChangeDetailsMap",loChangeEmailMap);
				}
				LOG_OBJECT.Debug("aoPwdChangeDetailsMap MAP 2 ::: " + loChangeEmailMap);
				//fix for NT037 issue - end
				generateNotification(aoChannelObj, lsUserId, lsNycUserName, lsOrgId);
				try
				{
					loReturnedMap.put("lbViaEmail", true);
					aoResponse.sendRedirect(lsLoginPage);
				}
				catch (IOException aoExp)
				{
					throw new ApplicationException("Not able to redirect to login page page after reseting password.",
							aoExp);
				}
			}
		}
		return loReturnedMap;
	}

	/**
	 * This method resets new password (via email process)
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param loChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param lsEmailAddress email address of the user/nyc id whose password is
	 *            to be changed
	 * @return Map<String, Object> which holds token , new password and email id
	 *         as values
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> resetNewPwdViaEmail(ActionRequest aoRequest, Channel aoChannelObj,
			String asEmailAddress, String asFirstName, String asLastName) throws ApplicationException
	{
		String lsNewIdPwd = aoRequest.getParameter("newNycIdPwd");
		Map<String, Object> loPwdChangeDetailsMap = new HashMap<String, Object>();

		if (lsNewIdPwd.toLowerCase().contains(asFirstName.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains(asLastName.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains(asEmailAddress.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains("password"))
		{
			loPwdChangeDetailsMap.put("newPasswordOK", "false");

		}
		else
		{

			String lsToken = (String) ApplicationSession.getAttribute(aoRequest, true, "loToken");
			loPwdChangeDetailsMap.put("newPassword", lsNewIdPwd);
			loPwdChangeDetailsMap.put("token", lsToken);
			loPwdChangeDetailsMap.put("emailID", asEmailAddress);
			aoChannelObj.setData("aoPwdChangeDetailsMap", loPwdChangeDetailsMap);
			TransactionManager.executeTransaction(aoChannelObj, "pwdResetViaEmail");
			loPwdChangeDetailsMap = null;
			loPwdChangeDetailsMap = (Map<String, Object>) aoChannelObj.getData("aoForgotPasswordRtrndMap");
			loPwdChangeDetailsMap.put("newPasswordOK", "true");
		}
		
		LOG_OBJECT.Debug("resetNewPwdViaEmail  return loPwdChangeDetailsMap::: " + loPwdChangeDetailsMap); 
		return loPwdChangeDetailsMap;
	}

	/**
	 * This method sends notification to user with embedded token after token
	 * generation during password reset via email
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param asEmailAddress email address where token needs to be sent
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void pwdChngViaEmailTokenNotification(ActionRequest aoRequest, Channel aoChannelObj, String asEmailAddress,
			String aoServiceOutputList) throws ApplicationException
	{
		LOG_OBJECT.Debug("PasswordResetEmailController :: pwdChngViaEmailTokenNotification "); //jm
		String lsUserName = "";
		String lsEmailToken = (String) aoServiceOutputList;
		Map loNameMap = new HashMap();

		String lsEmailLink = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
				+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_page_password_reset_email&validPasswordResetEmailToken="
				+ "validPasswordResetEmailToken&&accoutRequestmodule=accoutRequestmodule&emailToken=" + lsEmailToken
				+ "&emailAddress=" + asEmailAddress;
		try
		{
			loNameMap = getUserNameForEmailId(asEmailAddress);
			lsUserName = (String) loNameMap.get("FirstName") + " " + (String) loNameMap.get("Initials") + " "
					+ (String) loNameMap.get("LastName");
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while fetching user name from LDAP ", loEx);
		}

		String lsUserId = (String) loNameMap.get("staffId");
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT038");

		List<String> loProviderList = new ArrayList<String>();
		loProviderList.add("new");
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put("LINK", lsEmailLink);
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setProviderList(loProviderList);

		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		loParamMap.put("USERNAME", lsUserName);

		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap.put("NT038", loNotificationDataBean);
		if (lsUserId != null)
		{
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsUserId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "userId");
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
		}
		else
		{
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, asEmailAddress);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "user_emai");
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
		}
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
		aoChannelObj.setData("loHmNotifyParam", loNotificationMap);
		TransactionManager.executeTransaction(aoChannelObj, "insertNotificationDetail");
	}

	/**
	 * This method is used reset password via security questions
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction (with new password, web
	 *            service session object and userdn)
	 * @return ForgotPasswordWSBean has fields which state whether transaction
	 *         is success or failure
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Map resetPwdViaSecurityQues(ActionRequest aoRequest, Channel aoChannelObj, String asEmail,
			String asFirstName, String asLastName) throws ApplicationException
	{
		String lsNewIdPwd = aoRequest.getParameter("newNycIdPwd");
		Map loResetPwdMapViaSecQues = new HashMap();
		Map<String, Object> loPwdChangeDetailsMap = new HashMap<String, Object>();

		if (lsNewIdPwd.toLowerCase().contains(asFirstName.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains(asLastName.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains(asEmail.toLowerCase())
				|| lsNewIdPwd.toLowerCase().contains("password"))
		{
			loResetPwdMapViaSecQues.put("newPasswordOK", "false");

		}
		else
		{
			ForgotPasswordWSBean loForgotPasswordWSBean = new ForgotPasswordWSBean();
			loForgotPasswordWSBean = (ForgotPasswordWSBean) ApplicationSession.getAttribute(aoRequest, true,
					"ForgotPasswordWSBean");

			loPwdChangeDetailsMap.put("newPassword", lsNewIdPwd);
			loPwdChangeDetailsMap.put("userDN", loForgotPasswordWSBean.getUserDN());
			aoChannelObj.setData("aoPwdChangeDetailsMap", loPwdChangeDetailsMap);
			aoChannelObj.setData("aoPasswordManagementSession",
					(PasswordManagement) ApplicationSession.getAttribute(aoRequest, true, "loPasswordMgmtSession"));
			TransactionManager.executeTransaction(aoChannelObj, "ForgotPasswordChangeToNewPwd");

			loForgotPasswordWSBean = new ForgotPasswordWSBean();
			loForgotPasswordWSBean = (ForgotPasswordWSBean) aoChannelObj.getData("aoForgotPasswordWSBean");
			loResetPwdMapViaSecQues.put("loForgotPasswordWSBean", loForgotPasswordWSBean);
			loResetPwdMapViaSecQues.put("newPasswordOK", "true");

		}
		return loResetPwdMapViaSecQues;
	}

	/**
	 * This method validates user responses for the security questions(during
	 * password reset via security questions)
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction (with security answers and
	 *            user dn, returns status of transaction from web service)
	 * @return ForgotPasswordWSBean has fields which state whether transaction
	 *         is success or failure
	 * @throws ApplicationException
	 */
	private ForgotPasswordWSBean validateUserAnswer(ActionRequest aoRequest, Channel aoChannelObj)
			throws ApplicationException
	{
		Map<String, Object> loPwdChangeDetailsMap = new HashMap<String, Object>();
		ForgotPasswordWSBean loForgotPasswordWSBean = new ForgotPasswordWSBean();
		loForgotPasswordWSBean = (ForgotPasswordWSBean) ApplicationSession.getAttribute(aoRequest, true,
				"ForgotPasswordWSBean");

		String lsSecurityAnswer1 = aoRequest.getParameter("securityAns1");
		String lsSecurityAnswer2 = aoRequest.getParameter("securityAns2");

		loPwdChangeDetailsMap.put("Answer1", lsSecurityAnswer1);
		loPwdChangeDetailsMap.put("Answer2", lsSecurityAnswer2);
		loPwdChangeDetailsMap.put("userDN", loForgotPasswordWSBean.getUserDN());
		aoChannelObj.setData("aoPwdChangeDetailsMap", loPwdChangeDetailsMap);
		aoChannelObj.setData("aoPasswordManagementSession",
				(PasswordManagement) ApplicationSession.getAttribute(aoRequest, true, "loPasswordMgmtSession"));
		TransactionManager.executeTransaction(aoChannelObj, "ForgotPasswordValidateSecurityQuestion");

		loForgotPasswordWSBean = new ForgotPasswordWSBean();
		loForgotPasswordWSBean = (ForgotPasswordWSBean) aoChannelObj.getData("aoForgotPasswordWSBean");
		return loForgotPasswordWSBean;
	}

	/**
	 * This method gets security questions(during password reset via security
	 * questions)
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction (with email id as input)
	 * @return ForgotPasswordWSBean has fields which state whether transaction
	 *         is success or failure
	 * @throws ApplicationException
	 */
	private ForgotPasswordWSBean retrieveUserSecurityQuestion(ActionRequest aoRequest, Channel aoChannelObj)
			throws ApplicationException
	{
		Map<String, Object> loPwdChangeDetailsMap = new HashMap<String, Object>();
		ForgotPasswordWSBean loForgotPasswordWSBean = null;
		String lsEmailAddress = (String) ApplicationSession.getAttribute(aoRequest, true, "emailID");
		loPwdChangeDetailsMap.put("emailID", lsEmailAddress);

		aoChannelObj.setData("aoPwdChangeDetailsMap", loPwdChangeDetailsMap);

		TransactionManager.executeTransaction(aoChannelObj, "ForgotPasswordSecurityQuestion");
		loForgotPasswordWSBean = (ForgotPasswordWSBean) aoChannelObj.getData("aoForgotPasswordWSBean");
		PasswordManagement loService = (PasswordManagement) aoChannelObj.getData("aoPasswordManagementSession");
		ApplicationSession.setAttribute(loService, aoRequest, "loPasswordMgmtSession");
		return loForgotPasswordWSBean;
	}

	/**
	 * This method is updated in R4 for notification This method generates
	 * notification and brings user to login screen (password change via
	 * security questions)
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @param asUserId user id where notification needs to be sent
	 * @param aoUserName aoUserName name where notification needs to be sent
	 * @param asOrgId logged in organization
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void generateNotification(Channel aoChannelObj, String asUserId, String aoUserName, String asOrgId)
			throws ApplicationException
	{
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT037");

		List<String> loProviderList = new ArrayList<String>();
		loProviderList.add(asOrgId);
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		Map<String, String> loChangeEmailMap = (Map<String, String>) aoChannelObj.getData("aoPwdChangeDetailsMap");
		String lsEmail = null;
		if (loChangeEmailMap != null)
		{
			lsEmail = loChangeEmailMap.get("emailID");
		}
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setProviderList(loProviderList);

		HashMap<Object, String> loParamMap = new HashMap<Object, String>();
		loParamMap.put("USERNAME", aoUserName);

		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap.put("NT037", loNotificationDataBean);
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "user_email");
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsEmail);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
		aoChannelObj.setData("loHmNotifyParam", loNotificationMap);
		TransactionManager.executeTransaction(aoChannelObj, "insertNotificationDetail");
	}

	/**
	 * This method validates the token that user has received during password
	 * change via email (once the user clicks on the link this method is called)
	 * 
	 * @param aoRequest to get screen parameters
	 * @return Map with serviceStatus stating success or failure of transaction
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private Map validatePasswordResetEmailToken(RenderRequest aoRequest) throws ApplicationException
	{
		String loToken = (String) PortalUtil.parseQueryString(aoRequest, "emailToken");
		String loEmailAddress = (String) PortalUtil.parseQueryString(aoRequest, "emailAddress");
		ApplicationSession.setAttribute(loToken, aoRequest, "loToken");
		ApplicationSession.setAttribute(loEmailAddress, aoRequest, "loEmailAddress");

		Channel loChannelObj = new Channel();
		Map loTokenMap = new HashMap();
		loTokenMap.put("emailToken", loToken);
		loTokenMap.put("emailAddress", loEmailAddress);
		loChannelObj.setData("aoPwdChangeDetailsMap", loTokenMap);
		Map loValidateEmailTokenServiceOutMap = null;
		TransactionManager.executeTransaction(loChannelObj, "validPasswordResetEmailToken");
		loValidateEmailTokenServiceOutMap = (Map) loChannelObj.getData("aoForgotPasswordRtrndMap");
		return loValidateEmailTokenServiceOutMap;
	}

	/**
	 * This method gets map of user details -first name, initial and last name
	 * This function is modified in R4 for notification
	 * @param asEmailAddress - user's email id
	 * @return Map loUserDetailRtrndMap containing first name, initial and last
	 *         name
	 */
	@SuppressWarnings("rawtypes")
	private Map getUserNameForEmailId(String asEmailAddress) throws ApplicationException
	{
		Map loUserDetailRtrndMap = null;
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData("asUserEmailID", asEmailAddress);
			TransactionManager.executeTransaction(loChannel, "userDnSearchVDX");
			String lsUserDN = (String) loChannel.getData("asUserDN");
			// Web Service Call to fetch user Profile in LDAP ..
			loChannel = new Channel();
			loChannel.setData("asUserDN", lsUserDN);
			TransactionManager.executeTransaction(loChannel, "userDetailsSearch");
			loUserDetailRtrndMap = (Map) loChannel.getData("aoUserDetailRtrndMap");
			loChannel.setData("asEmail", asEmailAddress);
			HHSTransactionManager.executeTransaction(loChannel, "getStaffIdForNotification");
			String lsUserID = (String) loChannel.getData("staffId");
			loUserDetailRtrndMap.put("staffId", lsUserID);

		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Not able to get user name from web service based on email address.", loEx);
		}
		return loUserDetailRtrndMap;
	}
}
