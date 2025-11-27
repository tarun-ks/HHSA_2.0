package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This controller is for new User Registration that allows the provider user to
 * submit a request for a NYC.ID if they do not already have one.
 * 
 */

public class NYCRegistrationController extends AbstractController
{
	private static final LogInfo LOG_OBJECT = new LogInfo(NYCRegistrationController.class);

	private static final String REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
	private static final String CAPTCHA_VALIDATION_FAILED = "! The entered image value does not match.";
	private static final String BUTTON_HIT = "buttonHit";
	private static final String REGISTER_NYC = "registerNyc";
	private static final String FORWARD_TO_NEXT_JSP = "forwardToNextJsp";
	private static final String NEW_TOKEN_GENERATED = "This email already exists for a NYC.ID Account but has not been "
			+ "activated yet. A new activation email with a validation link will be sent shortly to activate this account.";

	String msAction = "";
	private static final String REGISTER_NYC_ID_BEAN = "RegisterNycIdBean";
	// QC 8366 R 7.1.2
	private static final String ALGORITHM = "HmacSHA1";

	/**
	 * This method is to render the next page depending on the action, NYC
	 * registration process r
	 * 
	 * @param aoRequest to get screen parameters and next page to be displayed
	 * @param aoResponse setting response parameter for JSP variables
	 * @throws ApplicationException
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("handleRenderRequestInternal aoRequest.getContextPath()" + aoRequest.getContextPath()
				+ " aoRequest.getScheme() " + aoRequest.getScheme() + " aoRequest.getServerName() "
				+ aoRequest.getServerName() + " aoRequest.getServerPort()" + aoRequest.getServerPort());
		long loStartTime = System.currentTimeMillis();
		aoResponse.setContentType("text/html");
		ModelAndView loModelAndView = null;
		String lsForwardMapping = ApplicationConstants.REGISTER_NYC_ID_FORM_JSP;
		try
		{
			/**
			 * This condition renders the register screen where the user enter
			 * all the details related to registration
			 */
			if (NYCRegistrationController.REGISTER_NYC.equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
					"navigatefrom")))
			{
				Channel loChannelObj = new Channel();
				navigateToRegisterForm(aoRequest, aoResponse, loChannelObj);

			}
			/**
			 * This condition sets the email attribute for the JSP
			 */
			if (aoRequest.getParameter(NYCRegistrationController.FORWARD_TO_NEXT_JSP) != null)
			{
				lsForwardMapping = aoRequest.getParameter(NYCRegistrationController.FORWARD_TO_NEXT_JSP);
				aoRequest.setAttribute("emailAddress", aoRequest.getParameter("emailAddress"));
			}
			/**
			 * This condition sets the top level status message of the
			 * transaction on screen
			 */
			if (aoRequest.getParameter("transactionMessage") != null
					&& !"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
			{
				aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
				aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
			}
			if (aoRequest.getParameter("captchaFailed") != null)
			{
				aoRequest.setAttribute("captchaFailed", aoRequest.getParameter("captchaFailed"));
			}
			String lsCatchaServiceJsPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"PROP_DOITT_CAPTCHA_SERVICE_UI");
			String lsCatchaRequired = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "Capcha_Required");
			aoRequest.getPortletSession().setAttribute(ApplicationConstants.PROPERTY_DOITT_CAPTCHA_SERVICE_UI,
					lsCatchaServiceJsPath, PortletSession.APPLICATION_SCOPE);
			
			LOG_OBJECT.Error("Capcha_Required", lsCatchaRequired);
			LOG_OBJECT.Error("PROP_DOITT_CAPTCHA_SERVICE_UI", lsCatchaServiceJsPath);
			
			if ("yes".equalsIgnoreCase(lsCatchaRequired))
			{
				aoRequest.getPortletSession().setAttribute(ApplicationConstants.CAPCHA_REQUIRED, lsCatchaRequired,
						PortletSession.APPLICATION_SCOPE);
			}
		}

		catch (ApplicationException aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured while retrieving security questions  ", aoFbAppEx);
		}
		catch (Exception aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured while retrieving security questions ", aoFbAppEx);
		}
		loModelAndView = new ModelAndView(lsForwardMapping);
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in NYCRegistrationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of render Method in NYCRegistrationController ", aoEx);
		}
		return loModelAndView;
	}

	/**
	 * This method decide the execution flow for NYC registration process
	 * 
	 * @param aoRequest to get screen parameters and next action to be performed
	 * @param aoResponse decides the next execution flow
	 * @throws ApplicationException
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes" })
	protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("handleActionRequestInternal aoRequest.getContextPath()" + aoRequest.getContextPath()
				+ " aoRequest.getScheme() " + aoRequest.getScheme() + " aoRequest.getServerName() "
				+ aoRequest.getServerName() + " aoRequest.getServerPort()" + aoRequest.getServerPort());
		long loStartTime = System.currentTimeMillis();
		String lsTransactionStatusMsg = "", lsTransactionStatus = "";
		Channel loChannelObj = new Channel();
		LOG_OBJECT.Debug("Before fetching user nyd id bean from portlet session ");
		RegisterNycIdBean loRegisterNycIdBean = (RegisterNycIdBean) aoRequest.getPortletSession().getAttribute(
				NYCRegistrationController.REGISTER_NYC_ID_BEAN, PortletSession.APPLICATION_SCOPE);
		LOG_OBJECT.Debug("After fetching user nyd id bean from portlet session ");
		if (aoRequest.getParameter(NYCRegistrationController.BUTTON_HIT) != null
				&& "register".equalsIgnoreCase(aoRequest.getParameter(NYCRegistrationController.BUTTON_HIT)))
		{
			try
			{
				loRegisterNycIdBean = populateRegisterBean(aoRequest, loRegisterNycIdBean);
				aoRequest.getPortletSession().setAttribute(NYCRegistrationController.REGISTER_NYC_ID_BEAN,
						loRegisterNycIdBean, PortletSession.APPLICATION_SCOPE);
				/** Below condition is for the captcha validation */
				LOG_OBJECT.Debug("Captcha validate call from NYC Controller start ");
				if (!validate(aoRequest, aoResponse, loRegisterNycIdBean))
				{
					aoResponse.setRenderParameter("captchaFailed", NYCRegistrationController.CAPTCHA_VALIDATION_FAILED);
				}
				else
				{
					lsTransactionStatus = "failed";
					lsTransactionStatusMsg = NYCRegistrationController.REQUEST_COULD_NOT_BE_COMPLETED;
					loChannelObj.setData("aoRegisterNycIdBean", loRegisterNycIdBean);
					LOG_OBJECT.Debug("call to webservice for new user registration with email id:: "
							+ loRegisterNycIdBean.getMsEmailAddress());
					Map loRegisterServiceOutMap = (Map) registerForNYCIDSave(aoRequest, aoResponse, loChannelObj);
					// Changes for Local in Release 5
					String lsStatus = (String) loRegisterServiceOutMap.get("serviceStatus");
					// String lsStatus = "R5";
					List loOutputList = (List) loRegisterServiceOutMap.get("serviceOutput");
					/*
					 * List loOutputList = new ArrayList();
					 * loOutputList.add("R5");
					 */
					if ("error".equalsIgnoreCase(lsStatus))
					{
						LOG_OBJECT
								.Debug("call to webservice for new user registration, coming in error block, email id:: "
										+ loRegisterNycIdBean.getMsEmailAddress()
										+ " lsStatus:: "
										+ lsStatus
										+ "   loOutputList:" + loOutputList);
						lsTransactionStatusMsg = (String) loOutputList.get(0);
						if (lsTransactionStatusMsg.contains("Email ID not unique"))
						{
							String lsUserFirstName = "";
							String lsUserMiddleName = "", lsUserLastName = "", lsUserEmailId = "", lsEmailValidationFlag = "";
							String lsNewToken = "";

							// start check flag if nyc activation flag is
							// activated
							lsUserEmailId = loRegisterNycIdBean.getMsEmailAddress().toLowerCase();
							Channel loChannel = new Channel();
							loChannel.setData("asUserEmailID", lsUserEmailId);
							TransactionManager.executeTransaction(loChannel, "userDnSearchVDX");
							String asUserDN = (String) loChannel.getData("asUserDN");

							// Web Service Call to fetch user Profile in LDAP ..
							loChannel = new Channel();
							loChannel.setData("asUserDN", asUserDN);
							TransactionManager.executeTransaction(loChannel, "userDetailsSearch");
							Map loUserDetailRtrndMap = (Map) loChannel.getData("aoUserDetailRtrndMap");
							lsUserFirstName = (String) loUserDetailRtrndMap.get("FirstName");
							lsUserMiddleName = (String) loUserDetailRtrndMap.get("Initials");
							lsUserLastName = (String) loUserDetailRtrndMap.get("LastName");
							lsEmailValidationFlag = (String) loUserDetailRtrndMap.get("nycExtEmailValidationFlag");
							if (null != lsEmailValidationFlag && !lsEmailValidationFlag.equalsIgnoreCase("Yes"))
							{
								lsTransactionStatus = "failed";
								lsTransactionStatusMsg = NYCRegistrationController.REQUEST_COULD_NOT_BE_COMPLETED;

								Map loNewTokenMap = generateNewToken(lsUserEmailId);
								lsNewToken = (String) loNewTokenMap.get("newToken");
								lsTransactionStatus = (String) loNewTokenMap.get("transactionStatus");
								lsTransactionStatusMsg = (String) loNewTokenMap.get("transactionMessage");

								if ("passed".equalsIgnoreCase(lsTransactionStatus))
								{
									lsTransactionStatus = "failed";
									loRegisterNycIdBean.setMsFirstName(lsUserFirstName);
									loRegisterNycIdBean.setMsMiddleName(lsUserMiddleName);
									loRegisterNycIdBean.setMsLastName(lsUserLastName);
									sendNT034Notification(aoRequest, loChannelObj, loRegisterNycIdBean, lsNewToken,
											lsUserEmailId);
								}

							}
							else
							{
								lsTransactionStatusMsg = "This email already exists for a NYC.ID Account."
										+ " Please enter a different email or Cancel to return to the login page.";
							}
							// end
						}
					}
					else
					{
						LOG_OBJECT
								.Debug("call to webservice for new user registration, coming in passed block email id:: "
										+ loRegisterNycIdBean.getMsEmailAddress()
										+ " lsStatus:: "
										+ lsStatus
										+ " loOutputList:");
						lsTransactionStatus = "failed";
						if (null != loOutputList && !loOutputList.isEmpty())
						{
							lsTransactionStatus = "passed";
							String lsEmailToken = (String) loOutputList.get(0);
							LOG_OBJECT
									.Debug("call to webservice for new user registration, coming in passed block, lsEmailToken: "
											+ lsEmailToken);
							String lsEmailAddress = (String) loRegisterNycIdBean.getMsEmailAddress();
							LOG_OBJECT
									.Debug("call to webservice for new user registration, coming in passed block, lsEmailToken: "
											+ lsEmailAddress);
							aoResponse.setRenderParameter("emailAddress", lsEmailAddress);
							aoRequest.getPortletSession().removeAttribute(
									NYCRegistrationController.REGISTER_NYC_ID_BEAN, PortletSession.APPLICATION_SCOPE);
							aoResponse.setRenderParameter(NYCRegistrationController.FORWARD_TO_NEXT_JSP,
									ApplicationConstants.ACCOUNT_REQUEST_SUBMITTED_JSP);
							sendNT034Notification(aoRequest, loChannelObj, loRegisterNycIdBean, lsEmailToken,
									lsEmailAddress);
						}
					}
				}
				aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
				aoResponse.setRenderParameter("transactionMessage", lsTransactionStatusMsg);
			}
			catch (ApplicationException aoFbAppEx)
			{
				catchException(aoResponse, lsTransactionStatusMsg, aoFbAppEx);
			}
			catch (Exception aoFbAppEx)
			{
				catchThrowable(aoResponse, lsTransactionStatusMsg, aoFbAppEx);
			}
		}
		long loEndTimeTime = System.currentTimeMillis();
		try
		{
			LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in NYCRegistrationController = "
					+ (loEndTimeTime - loStartTime));
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Error while execution of action Method in NYCRegistrationController ", aoEx);
		}
	}

	/**
	 * @param aoRequest
	 * @param loChannelObj
	 * @param loRegisterNycIdBean
	 * @param lsEmailToken
	 * @param lsEmailAddress
	 * @throws ApplicationException
	 */
	private void sendNT034Notification(ActionRequest aoRequest, Channel loChannelObj,
			RegisterNycIdBean loRegisterNycIdBean, String lsEmailToken, String lsEmailAddress)
			throws ApplicationException
	{
		String lsUserName = loRegisterNycIdBean.getMsFirstName() + " " + loRegisterNycIdBean.getMsLastName();
		if (loRegisterNycIdBean.getMsMiddleName() != null
				&& !"".equalsIgnoreCase(loRegisterNycIdBean.getMsMiddleName()))
		{
			lsUserName = loRegisterNycIdBean.getMsFirstName() + " " + loRegisterNycIdBean.getMsMiddleName() + " "
					+ loRegisterNycIdBean.getMsLastName();
		}
		String lsEmailLink = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
				+ aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
				+ "&_pageLabel=portlet_hhsweb_portal_login_page&validEmailToken=validEmailToken&emailToken="
				+ lsEmailToken + "&emailAddress=" + lsEmailAddress;
		LOG_OBJECT.Debug("call to webservice for new user registration, coming in passed block, lsEmailLink: "
				+ lsEmailLink);

		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT034");

		List<String> loProviderList = new ArrayList<String>();
		loProviderList.add("new Org");

		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put("LINK", lsEmailLink);

		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		loNotificationDataBean.setLinkMap(loLinkMap);
		loNotificationDataBean.setProviderList(loProviderList);

		HashMap loParamMap = new HashMap();
		loParamMap.put("USERNAME", lsUserName);

		List<String> loUserIdList = new ArrayList<String>();
		loUserIdList.add(lsEmailAddress);

		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		loNotificationMap.put("NT034", loNotificationDataBean);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		loNotificationMap.put(TransactionConstants.USER_ID, loUserIdList);
		loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
		loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "new_user_email");
		loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsEmailAddress);
		loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
		loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);

		loChannelObj.setData("loHmNotifyParam", loNotificationMap);
		LOG_OBJECT.Debug("call to webservice for new user registration, loMapForNotification: " + loNotificationMap);
		TransactionManager.executeTransaction(loChannelObj, "insertNotificationDetail");
		LOG_OBJECT.Debug("notification sent ");
	}

	/**
	 * This method is called when exception is thrown of throwable class
	 * transaction status and message are rendered properly
	 * 
	 * @param aoResponse setting response parameter for JSP variables
	 * @param asTransactionStatusMsg message to be displayed on jsp
	 * @param asTransactionStatus status of the transaction(failed)
	 * @param aoFbAppEx object of type ApplicationException
	 */
	private void catchThrowable(ActionResponse aoResponse, String asTransactionStatusMsg, Throwable aoFbAppEx)
	{
		String lsTransactionStatus;
		lsTransactionStatus = "failed";
		aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		LOG_OBJECT.Error("Throwable Error occurred while registering error message ", aoFbAppEx);
	}

	/**
	 * This method is called when exception is thrown of ApplicationException
	 * class transaction status and message are rendered properly
	 * 
	 * @param aoResponse setting response parameter for JSP variables
	 * @param asTransactionStatusMsg message to be displayed on jsp
	 * @param asTransactionStatus status of the transaction(failed)
	 * @param aoFbAppEx object of type ApplicationException
	 */
	private void catchException(ActionResponse aoResponse, String asTransactionStatusMsg, ApplicationException aoFbAppEx)
	{
		String lsTransactionStatus;
		lsTransactionStatus = "failed";
		aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
		aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
		LOG_OBJECT.Error("Error occurred while registering error message ", aoFbAppEx);
	}

	// start QC8366 R 7.1.2 - use CAPTCHA Version 2 
	/**
	 * This method validates captcha generated image and text entered are same
	 * and in case of voice, words spoken and generated are same. This method
	 * calls webservice for validation process
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse setting response parameter for JSP variables
	 * @param aoRegisterNycIdBean has fields with user details
	 * @return boolean status whether captcha entered and generated are same
	 * @throws ApplicationException
	 */
	
	private boolean validate(ActionRequest aoRequest, ActionResponse aoResponse, RegisterNycIdBean aoRegisterNycIdBean)
	throws ApplicationException
	{
			LOG_OBJECT.Debug("Inside captcha validate method");
			boolean lbIsValid = true;
			if (aoRequest.getPortletSession().getAttribute(ApplicationConstants.CAPCHA_REQUIRED,
					PortletSession.APPLICATION_SCOPE) != null)
			{
				String lsUserAnswer = aoRequest.getParameter("g-recaptcha-response");
				String url = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						ApplicationConstants.PROPERTY_DOITT_CAPTCHA_SERVICE);
				HttpMethod lsMethod = new GetMethod(url);
				
				String lsRemoteAddr = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
						ApplicationConstants.PROPERTY_DOITT_CAPTCHA_CLIENT);
				
				LOG_OBJECT.Debug("Capcha remoteClientAddress :: ", lsRemoteAddr);
				
				if (lsRemoteAddr==null || "".equalsIgnoreCase(lsRemoteAddr) || "null".equalsIgnoreCase(lsRemoteAddr))
				{
				   lsRemoteAddr = PortalUtil.getServletRequest(aoRequest).getRemoteAddr();
				}
				
				String lsPassword = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
						ApplicationConstants.PROPERTY_DOITT_CAPTCHA_PASSWORD);
				String lsSignatureParams = getSignatureParams(lsUserAnswer, lsRemoteAddr);
				String lsSignature = getSignature(lsSignatureParams, lsPassword);
												
				NameValuePair[] loNameValuePairs = new NameValuePair[6];
				loNameValuePairs[0] = new NameValuePair("userName", "hhsaccelerator");
				loNameValuePairs[1] = new NameValuePair("client_ip_address", lsRemoteAddr);
				loNameValuePairs[2] = new NameValuePair("user_answer", lsUserAnswer);
				loNameValuePairs[3] = new NameValuePair("json", "true");
				loNameValuePairs[4] = new NameValuePair("version", "2");
				loNameValuePairs[5] = new NameValuePair("signature", lsSignature);
								
				lsMethod.setQueryString(loNameValuePairs);
				
				HttpClient loClient = new HttpClient();
				
				String lsRresponse = "";
				lbIsValid = false;
				LOG_OBJECT.Debug("Before executing captcha validate method :: " + Arrays.toString(loNameValuePairs));
				
				try
				{					
					int returnCode = loClient.executeMethod(lsMethod);
					LOG_OBJECT.Debug("After executing captcha validate method :: returnCode :: " + returnCode);	
					LOG_OBJECT.Debug("After executing captcha validate method :: Response   :: " + lsMethod.getResponseBodyAsString());		
					if (returnCode == HttpStatus.SC_OK) 
					{
						lsRresponse = lsMethod.getResponseBodyAsString();
						String[] loStrBuffer = lsRresponse.split("\r?\n");
						if (loStrBuffer[0].indexOf("true") != -1)
						{
							lbIsValid = true;
						}	
						else
						{
							lbIsValid = false;
						}
					}
					
				}
				catch (HttpException aoExp)
				{
					LOG_OBJECT.Error("HttpException durin execution of captcha validate method :: " +aoExp.getMessage());
					LOG_OBJECT.Error("HttpException durin execution of captcha validate method :: " +aoExp);
					
					lbIsValid = false;
				}
				catch (IOException aoIOExp)
				{
					LOG_OBJECT.Error("IOException durin execution of captcha validate method :: " +aoIOExp.getMessage());
					LOG_OBJECT.Error("IOException durin execution of captcha validate method :: " +aoIOExp);
					
					lbIsValid = false;
				}
				catch (Exception e)
				{
					LOG_OBJECT.Error("Exception durin execution of captcha validate method :: " +e.getMessage());
					LOG_OBJECT.Error("Exception durin execution of captcha validate method :: " +e);
					
					lbIsValid = false;
				}
			}
			LOG_OBJECT.Debug("After executing captcha validate method :: " + lbIsValid);
			return lbIsValid;
			
	}
	/**
	 * This method generates the value of the Signature
	 * used in the getSignature method
	 * Concatenate all values onto the String in alphabetical order by key.
	 */
	
	private static String getSignatureParams(String userAnswer, String remoteAddr) {
        // Step 1: Start with an empty string.
        StringBuilder signatureParams = new StringBuilder();
        // Step 2: Concatenate the HTTP method to the above string.
        signatureParams.append("GET");
        // Step 3: Concatenate the request URI to the above string.
        signatureParams.append("/doittcaptchaservice/validate.htm");
        // Step 4: Concatenate all values onto the String in alphabetical order by key.
        signatureParams.append(remoteAddr);  		// client_ip_address lsRemoteAddr
        signatureParams.append("true");  			// json
        signatureParams.append("hhsaccelerator");  	// userName
        signatureParams.append(userAnswer); 		// user_answer
        signatureParams.append("2"); 				// version
        
        return signatureParams.toString();
    }
	
	/**
	 * Method generate the signature needed for the DoITT CAPTCHA Validation Service using the following algorithm.
	 * The key is the provisioned password. The value is generated by combining the request parameters.
	 */
	public static String getSignature(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, ALGORITHM);

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            // Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	// end QC8366 R 7.1.2 - use CAPTCHA Version 2 
	
	/**
	 * This method gets security question list that user has to set during
	 * registration process
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse setting response parameter for JSP variables
	 * @param aoChannelObj to get question list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void navigateToRegisterForm(RenderRequest aoRequest, RenderResponse aoResponse, Channel aoChannelObj)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		List<SecurityQuestionBean> loQuestionList = null;
		RegisterNycIdBean loRegisterNycIdBean = null;
		TransactionManager.executeTransaction(aoChannelObj, "getSecurityQuestions");

		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT.Debug("NYCRegistrationController: method: navigateToRegisterForm. Time Taken(seconds):: "
				+ liTimediff);

		loQuestionList = (List) aoChannelObj.getData("losecurityQuestionList");
		if (null != aoRequest.getPortletSession().getAttribute(NYCRegistrationController.REGISTER_NYC_ID_BEAN,
				PortletSession.APPLICATION_SCOPE))
		{
			loRegisterNycIdBean = (RegisterNycIdBean) aoRequest.getPortletSession().getAttribute(
					NYCRegistrationController.REGISTER_NYC_ID_BEAN, PortletSession.APPLICATION_SCOPE);
			loRegisterNycIdBean.setMoSecurityQuestion1List(loQuestionList);
		}
		else
		{
			loRegisterNycIdBean = new RegisterNycIdBean();
			loRegisterNycIdBean.setMoSecurityQuestion1List(loQuestionList);
		}
		aoRequest.getPortletSession().setAttribute(NYCRegistrationController.REGISTER_NYC_ID_BEAN, loRegisterNycIdBean,
				PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * This method calls web service transaction that saves/register user into
	 * LDAP
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoResponse setting response parameter for JSP variables
	 * @param aoChannelObj Channel Object that needs to be set to send and
	 *            receive values across transaction
	 * @return Map has fields related to transaction status and
	 *         output(serviceStatus, serviceOutput )
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	private Map registerForNYCIDSave(ActionRequest aoRequest, ActionResponse aoResponse, Channel aoChannelObj)
			throws ApplicationException
	{
		String lsStartTime = CommonUtil.getCurrentTimeInMilliSec();

		Map loRegisterServiceOutMap = null;
		TransactionManager.executeTransaction(aoChannelObj, "RegisterForNYCIDSave");
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		LOG_OBJECT
				.Debug("NYCRegistrationController: method: registerForNYCIDSave. Time Taken(seconds):: " + liTimediff);

		loRegisterServiceOutMap = (Map) aoChannelObj.getData("aoRegistServiceHM");
		return loRegisterServiceOutMap;
	}

	/**
	 * This method populates RegisterNycIdBean to persist data that user has
	 * entered during registration process
	 * 
	 * @param aoRequest to get screen parameters
	 * @param aoRegisterNycIdBean to set fields with aoRequest parameters
	 * @return RegisterNycIdBean which has populated fields with values to be
	 *         registered
	 */
	private RegisterNycIdBean populateRegisterBean(ActionRequest aoRequest, RegisterNycIdBean aoRegisterNycIdBean)
	{
		aoRegisterNycIdBean.setMsFirstName((String) aoRequest.getParameter("firstName").trim());
		aoRegisterNycIdBean.setMsMiddleName(aoRequest.getParameter("middleName").trim());
		aoRegisterNycIdBean.setMsLastName(aoRequest.getParameter("lastName").trim());
		aoRegisterNycIdBean.setMsEmailAddress(aoRequest.getParameter("email"));
		aoRegisterNycIdBean.setMsConfirmEmailAddress(aoRequest.getParameter("confirmEmail"));
		aoRegisterNycIdBean.setMsPassword(aoRequest.getParameter("password"));
		aoRegisterNycIdBean.setMsConfirmPassword(aoRequest.getParameter("confirmPassword"));
		aoRegisterNycIdBean.setMiSecurityQuestion1Id(Integer.parseInt(aoRequest.getParameter("securityQuestion1")));
		aoRegisterNycIdBean.setMsAnswer1(aoRequest.getParameter("answer1"));
		aoRegisterNycIdBean.setMiSecurityQuestion2Id(Integer.parseInt(aoRequest.getParameter("securityQuestion2")));
		aoRegisterNycIdBean.setMsAnswer2(aoRequest.getParameter("answer2"));
		aoRegisterNycIdBean.setMiSecurityQuestion3Id(Integer.parseInt(aoRequest.getParameter("securityQuestion3")));
		aoRegisterNycIdBean.setMsAnswer3(aoRequest.getParameter("answer3"));
		aoRegisterNycIdBean.setMsQues1Text(aoRequest.getParameter("ques1Text"));
		aoRegisterNycIdBean.setMsQues2Text(aoRequest.getParameter("ques2Text"));
		aoRegisterNycIdBean.setMsQues3Text(aoRequest.getParameter("ques3Text"));

		return aoRegisterNycIdBean;
	}

	   /**
		 * This Method is used to generate New Token and returns Map containing transactionStatus,
		 * transactionMessage and  newToken.
		 * <ul>
		 * <li>Execute transaction id <b> newTokenGeneration</b></li>
		 * </ul>
		 * @return loNewTokenMap Map
		 * @throws ApplicationException
		 */
	private Map generateNewToken(String asUserEmailId) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		loChannelObj.setData("asUserEmailId", asUserEmailId);
		Map loRegisterServiceOutMap = null;
		Map loNewTokenMap = new HashMap();
		try
		{
			TransactionManager.executeTransaction(loChannelObj, "newTokenGeneration");
			loRegisterServiceOutMap = (Map) loChannelObj.getData("aoNewTokenMap");
			String lsStatus = (String) loRegisterServiceOutMap.get("serviceStatus");
			List loOutputList = (List) loRegisterServiceOutMap.get("serviceOutput");
			String lsToken = (String) loOutputList.get(0);
			if ("error".equalsIgnoreCase(lsStatus))
			{
				loNewTokenMap.put("transactionStatus", "failed");
				loNewTokenMap.put("transactionMessage", NYCRegistrationController.REQUEST_COULD_NOT_BE_COMPLETED);

			}
			else if ("success".equalsIgnoreCase(lsStatus))
			{
				if (lsToken != null && !"".equalsIgnoreCase(lsToken) && lsToken.indexOf("[") >= 0)
				{
					lsToken = lsToken.substring(1, lsToken.length() - 1);
					loNewTokenMap.put("newToken", lsToken);
					loNewTokenMap.put("transactionStatus", "passed");
					loNewTokenMap.put("transactionMessage", NYCRegistrationController.NEW_TOKEN_GENERATED);
				}
			}

		}
		catch (ApplicationException aoFbAppEx)
		{
			LOG_OBJECT.Error("Error occured while generating new token :: ", aoFbAppEx);
		}
		return loNewTokenMap;
	}
}
