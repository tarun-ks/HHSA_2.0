package com.nyc.hhs.service.webservices;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.apache.axis.client.Call;

import com.novell.www.provisioning.service.AdminException;
import com.novell.www.provisioning.service.Comment;
import com.novell.www.provisioning.service.DataItem;
import com.novell.www.provisioning.service.GetCommentsByActivityRequest;
import com.novell.www.provisioning.service.GetCommentsByActivityResponse;
import com.novell.www.provisioning.service.GetProcessRequest;
import com.novell.www.provisioning.service.GetProcessResponse;
import com.novell.www.provisioning.service.Provisioning;
import com.novell.www.provisioning.service.ProvisioningServiceLocator;
import com.novell.www.provisioning.service.StartRequest;
import com.novell.www.provisioning.service.StartResponse;
import com.novell.www.pwdmgt.service.ForgotPasswordWSBean;
import com.novell.www.pwdmgt.service.PasswordManagement;
import com.novell.www.pwdmgt.service.PasswordManagementServiceLocator;
import com.novell.www.pwdmgt.service.ProcessChaResRequest;
import com.novell.www.pwdmgt.service.ProcessChgPwdRequest;
import com.novell.www.pwdmgt.service.ProcessUserRequest;
import com.novell.www.vdx.service.Attribute;
import com.novell.www.vdx.service.AttributeType;
import com.novell.www.vdx.service.GetAttributesRequest;
import com.novell.www.vdx.service.GetAttributesResponse;
import com.novell.www.vdx.service.GlobalQueryRequest;
import com.novell.www.vdx.service.GlobalQueryResponse;
import com.novell.www.vdx.service.IRemoteVdx;
import com.novell.www.vdx.service.SetAttributeRequest;
import com.novell.www.vdx.service.StringEntry;
import com.novell.www.vdx.service.StringMap;
import com.novell.www.vdx.service.VdxServiceLocator;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This Stub is used for calling web services related to account request and
 * user profile tasks The web services invoked are: -provisioning web services
 * -password management web services
 * 
 */

public class AccountRequestLoginStub
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AccountRequestLoginStub.class);
	public static final int LOOP_COUNTER = 30;
	public static final int SLEEP_TIME = 2000;
	Map<String, Object> loMapWebServRetrndParam = new HashMap<String, Object>();

	/**
	 * This method authenticates the user text entered and system generated text
	 * who is trying to log in
	 * 
	 * @param asCaptchaImg system generated text
	 * @param asCaptchaTxt system generated image
	 * @return boolean status whether captcha image is validated or not
	 * @throws ApplicationException
	 */

	public boolean validateCaptcha(String asCaptchaImg, String asCaptchaTxt) throws ApplicationException
	{
		boolean lbStatus = false;
		if (asCaptchaImg != null && asCaptchaTxt != null)
		{
			if (asCaptchaImg.equalsIgnoreCase(asCaptchaTxt))
			{
				lbStatus = true;
			}
			else
			{
				lbStatus = false;
			}
		}
		return lbStatus;
	}

	/**
	 * This method authenticates the login credential of user
	 * 
	 * @param asUserId who is trying to login
	 * @param asPassword of the user who is trying to log in
	 * @return boolean status whether or not the user is authenticated.
	 * @throws ApplicationException
	 */
	public boolean authenticateLoginUser(String asUserId, String asPassword) throws ApplicationException
	{
		boolean lbUserAuthenticated = true;
		lbUserAuthenticated = authenticate(asUserId, asPassword);
		return lbUserAuthenticated;
	}

	/**
	 * This method gets the comments for all the messages (list)
	 * 
	 * @param aoDIArray DataItem[] containing all the input parameter(user data)
	 * @param List aoMsgActivity containing message list corresponding to which
	 *            webservices will return different status(process, approval
	 *            status)
	 * @param String asProvisioningDN web service DN which we want to invoke
	 * @return Map contains values for different status (process, approval
	 *         status)
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map executeService(DataItem[] aoDIArray, List aoMsgActivity, String asProvisioningDN)
			throws ApplicationException, ServiceException, MalformedURLException, RemoteException, Exception
	{
		final ProvisioningServiceLocator loLocater = new ProvisioningServiceLocator();
		final Provisioning loService = loLocater.getProvisioningPort(new URL(PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_URL_IDM_PROVISIONING)));
		((Stub) loService)._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		((Stub) loService)._setProperty(Call.USERNAME_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_DN));
		((Stub) loService)._setProperty(Call.PASSWORD_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_PW));

		StartRequest loStartRequest = new StartRequest();
		loStartRequest.setArg0(asProvisioningDN);
		loStartRequest.setArg1(PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
				ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_DN));
		loStartRequest.setArg2(aoDIArray);
		LOG_OBJECT.Debug("In executeService method, before loStartResponse:: ");
		StartResponse loStartResponse = loService.start(loStartRequest);
		LOG_OBJECT.Debug("In executeService method, after loStartResponse:: " + loStartResponse);

		String lsID = loStartResponse.getResult();
		LOG_OBJECT.Debug("In executeService method, lsID:: " + lsID);

		String lsApprovalStatus = getProcessStatus(lsID, loService);
		LOG_OBJECT.Debug("In executeService method, lsApprovalStatus:: " + lsApprovalStatus);
		return getMessages(loService, aoMsgActivity, lsApprovalStatus, lsID);
	}

	/**
	 * This method gets the comments for all the messages (list)
	 * 
	 * @param asProcessId id of the response.getResult - web service id of the
	 *            transaction
	 * @param aoService reference variable of provisioning web service
	 * @returns String process id for approval Status
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 * @throws InterruptedException
	 */

	public static String getProcessStatus(String asProcessId, Provisioning aoService) throws RemoteException,
			MalformedURLException, ServiceException, ApplicationException, InterruptedException
	{
		LOG_OBJECT.Debug("Entered in getProcessStatus method, asProcessId:: " + asProcessId + " aoService:: "
				+ aoService);
		GetProcessRequest loGetProcessesRequest = new GetProcessRequest();
		loGetProcessesRequest.setArg0(asProcessId);
		GetProcessResponse loResponse = null;
		com.novell.www.provisioning.service.Process loProcess = null;

		for (int liCount = 0; liCount < LOOP_COUNTER; liCount++)
		{
			loResponse = aoService.getProcess(loGetProcessesRequest);
			loProcess = loResponse.getProcess();
			LOG_OBJECT.Debug("In getProcessStatus, loopcounter:: " + liCount);
			if (loProcess == null)
			{
				LOG_OBJECT.Debug("In getProcessStatus method loProcess is NULL");
				break;
			}
			else if ("Completed".equalsIgnoreCase(loProcess.getProcessStatus())
					|| "Error".equalsIgnoreCase(loProcess.getProcessStatus()))
			{
				LOG_OBJECT.Debug("In getProcessStatus(FINAL VALUE) method loProcess.getProcessStatus():: "
						+ loProcess.getProcessStatus());
				break;
			}
			Thread.sleep(SLEEP_TIME);
		}

		String lsProcessStatus = loProcess == null ? "" : loProcess.getApprovalStatus();

		LOG_OBJECT.Debug("In getProcessStatus, before return loProcess.getApprovalStatus():: " + lsProcessStatus);
		return lsProcessStatus;
	}

	/**
	 * This method gets the comments for all the messages (list)
	 * 
	 * @param aoService reference variable of provisioning web service
	 * @param aoMsgActivity containing message list corresponding to which
	 *            webservices will return different status(process, approval
	 *            status)
	 * @param asApprovalStatus of the process id
	 * @param asProcessId of the response.getResult - web service id of the
	 *            transaction
	 * @return Map contains values for different status (process, approval
	 *         status)
	 * @throws Exception
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static Map getMessages(Provisioning aoService, List aoMsgActivity, String asApprovalStatus,
			String asProcessId) throws Exception
	{
		LOG_OBJECT.Debug("In getMessages method, aoMsgActivity:: " + aoMsgActivity + " asApprovalStatus::" + " "
				+ asApprovalStatus + " asProcessId:: " + asProcessId);
		String lsActivityId = "";
		List loServiceOutputList = new ArrayList();
		Map loServiceOutputMap = new HashMap();
		String lsApprovalStts = "success";
		String lsComments = "";
		if ("".equals(asApprovalStatus))
		{
			LOG_OBJECT.Debug("In getMessages method NULL block with NULL value:: ");
			lsApprovalStts = "error";
			loServiceOutputList.add("This service is currently unavailable. Please try after sometime.");
		}
		else if ("Denied".equalsIgnoreCase(asApprovalStatus))
		{
			LOG_OBJECT.Debug("In getMessages method denied block with asProcessId value:: " + asProcessId);
			lsActivityId = "Denial_Error_Message";
			/**** Start R6.3 QC8702  ******/
			/*
			//Start Add at R6.2 for QC 6710 
			loServiceOutputList.add(   HHSConstants.INVALID_TOKEN_TO_RESET_PWD_MSG );
			//End Add at R6.2 for QC 6710 
            */						
			loServiceOutputList.add(getComments(aoService, lsActivityId, asProcessId));
			LOG_OBJECT.Debug("In denied block with loServiceOutputList:: " + loServiceOutputList);
			
			/**** End R6.3 QC8702  ******/	
			lsApprovalStts = "error";
		}
		else
		{
			LOG_OBJECT.Debug("In getMessages method else block with aoMsgActivity:: " + aoMsgActivity
					+ " and asApprovalStatus:: " + asApprovalStatus);
			if (null != aoMsgActivity)
			{
				for (int liCntr = 0; liCntr < aoMsgActivity.size(); liCntr++)
				{
					LOG_OBJECT.Debug("In getMessages method else block before getting lsComments. asProcessId:: "
							+ asProcessId);
					lsComments = getComments(aoService, (String) aoMsgActivity.get(liCntr), asProcessId);
					LOG_OBJECT.Debug("In getMessages method else block with lsComments:: " + lsComments
							+ " and asApprovalStatus:: " + asApprovalStatus);
					if (null != lsComments)
					{
						LOG_OBJECT.Debug("In getMessages method else block when lscomments is not null");
						loServiceOutputList.add(lsComments);
					}
					else
					{
						LOG_OBJECT.Debug("In getMessages method else block when lscomments is NULL");
						continue;
					}

				}
			}
			else
			{
				LOG_OBJECT.Debug("In getMessages method adding blank to loServiceOutputList " + "asApprovalStatus:: "
						+ asApprovalStatus);
				loServiceOutputList.add("");
			}
		}
		/**** Start R6.3 QC8702  ******/
		/*
		//Start R6.2 Added for QC 6710: add error message for invalid token to reset password
		loServiceOutputMap.put(HHSConstants.PWD_RESET_TOKEN_STATUS_ID, asApprovalStatus);
		//End R6.2 Added for QC 6710: add error message for invalid token to reset password
		LOG_OBJECT.Debug("In getMessages method, loServiceOutputMap: token status :: "	+ loServiceOutputMap.get(HHSConstants.PWD_RESET_TOKEN_STATUS_ID));
		 */
		/**** End R6.3 QC8702  ******/
		

		loServiceOutputMap.put("serviceStatus", lsApprovalStts);
		loServiceOutputMap.put("serviceOutput", loServiceOutputList);
		LOG_OBJECT.Debug("In getMessages method before return loServiceOutputMap " + loServiceOutputMap);
		return loServiceOutputMap;
	}

	/**
	 * This method gets the comments from web services for the activity id
	 * provided
	 * 
	 * @param aoService reference variable of provisioning web service
	 * @param asActivityId is the id of the activity
	 * @param asProcessId of the response.getResult - web service id of the
	 *            transaction
	 * @return String comments for the process id
	 * @throws AdminException
	 * @throws RemoteException
	 */

	public static String getComments(Provisioning aoService, String asActivityId, String asProcessId)
			throws AdminException, RemoteException
	{
		GetCommentsByActivityRequest loCbar = new GetCommentsByActivityRequest();
		loCbar.setArg0(asProcessId);
		loCbar.setArg1(asActivityId);
		GetCommentsByActivityResponse loGcbar = aoService.getCommentsByActivity(loCbar);
		Comment[] loComments = loGcbar.getResult();

		return loComments.length > 0 ? loComments[0].getComment() : null;

	}

	/**
	 * This method gets the http session for password management during password
	 * change (via security questions)
	 * 
	 * @return PasswordManagement returns the session for the password
	 *         management that will be used across different method calls viz -
	 *         get questions, validate answers, reset new password
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */

	public PasswordManagement getPasswordMgmtSession() throws ApplicationException, ServiceException,
			MalformedURLException, RemoteException, Exception
	{
		// Setup Axis SOAP connection
		PasswordManagementServiceLocator loLocater = new PasswordManagementServiceLocator();
		PasswordManagement loService = loLocater.getPasswordManagementPort(new URL(PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_URL_IDM_PASSWORD)));
		// make sure the HTTP session is maintained over the three iterative API
		// calls.
		((Stub) loService)._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);

		return loService;
	}

	/**
	 * This method is called for getting random questions during password change
	 * (via security questions)
	 * 
	 * @param aoService This argument(object) maintains the http session during
	 *            password change process of getting questions, submitting
	 *            answers, changing password
	 * @param asUserDN distinct for each email id and user, unique key to
	 *            identify
	 * @return ForgotPasswordWSBean contains challenge questions, answers,
	 *         status that user has set while creating/updating id
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public ForgotPasswordWSBean pwdChangeGetUserQuestions(PasswordManagement aoService, String asUserDN)
			throws ApplicationException, ServiceException, MalformedURLException, RemoteException, Exception
	{
		ProcessUserRequest loUserRequest = new ProcessUserRequest(asUserDN);
		ForgotPasswordWSBean loProcessUserResponse = aoService.processUser(loUserRequest);
		return loProcessUserResponse;
	}

	/**
	 * This method is called while submitting answers during password change
	 * (via security questions)
	 * 
	 * @param aoService This argument(object) maintains the http session during
	 *            password change process of getting questions, submitting
	 *            answers, changing password
	 * @param asUserDN distinct for each email id and user, unique key to
	 *            identify
	 * @param aoUserResponses user answers to the questions on screen
	 * @return ForgotPasswordWSBean contains challenge questions, answers,
	 *         status that user has set while creating/updating id
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public ForgotPasswordWSBean pwdChangeSubmitAnswers(PasswordManagement aoService, String asUserDN,
			String[] aoUserResponses) throws ApplicationException, ServiceException, MalformedURLException,
			RemoteException, Exception
	{
		String lsUserDN = asUserDN;
		String loUserResponses[] = aoUserResponses;

		ProcessChaResRequest loChaResRequest = new ProcessChaResRequest(lsUserDN, loUserResponses);
		loChaResRequest.setUserDN(lsUserDN);
		ForgotPasswordWSBean loProcessChaResResponse = aoService.processChaRes(loChaResRequest);

		return loProcessChaResResponse;
	}

	/**
	 * This method is for VDX search that gives userdn for corresponding email
	 * Id
	 * 
	 * @param aoService This argument(object) maintains the http session during
	 *            password change process of getting questions, submitting
	 *            answers, changing password
	 * @param asUserDn distinct for each email id and user, unique key to
	 *            identify
	 * @param asPassword password of the user who is trying to login
	 * @return ForgotPasswordWSBean contains challenge questions, answers,
	 *         status that user has set while creating/updating id
	 * @throws ApplicationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public ForgotPasswordWSBean pwdChangeSubmitNewPassword(PasswordManagement aoService, String asUserDn,
			String asPassword) throws ApplicationException, ServiceException, MalformedURLException, RemoteException,
			Exception
	{
		String lsUserDn = asUserDn;
		String lsNewPassword = asPassword;
		String lsNewPasswordConfirm = asPassword;

		ProcessChgPwdRequest loChgPwdRequest = new ProcessChgPwdRequest(lsUserDn, lsNewPassword, lsNewPasswordConfirm);
		ForgotPasswordWSBean loProcessChgPwdResponse = aoService.processChgPwd(loChgPwdRequest);

		return loProcessChgPwdResponse;
	}

	/**
	 * This method is for VDX search that gives USERDN for corresponding email
	 * Id
	 * 
	 * @param asUserEmailId gets the USERDN against the email id
	 * @return String lsUserDN for corresponding email id
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws ApplicationException
	 */
	public String userDnSearchVDX(String asUserEmailId) throws ServiceException, MalformedURLException,
			RemoteException, ApplicationException
	{

		LOG_OBJECT.Debug("In userDnSearchVDX method, before loService 1");
		VdxServiceLocator loLocater = new VdxServiceLocator();
		IRemoteVdx loService = loLocater.getIRemoteVdxPort(new URL(PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_URL_IDM_VDX)));
		LOG_OBJECT.Debug("In userDnSearchVDX method, after  loService 2");

		LOG_OBJECT.Debug("In userDnSearchVDX method, before setting property 3");
		((Stub) loService)._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		((Stub) loService)._setProperty(Call.USERNAME_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_DN));
		((Stub) loService)._setProperty(Call.PASSWORD_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_PW));
		LOG_OBJECT.Debug("In userDnSearchVDX method, after setting property 4");

		StringMap loStringMap = new StringMap();
		StringEntry loStringEntry = new StringEntry();
		loStringEntry.setKey("email");
		loStringEntry.setValues(asUserEmailId);
		loStringMap.setEntries(new StringEntry[]
		{ loStringEntry });

		LOG_OBJECT.Debug("In userDnSearchVDX method, after setting entries 5");
		GlobalQueryRequest loRequest = new GlobalQueryRequest();
		loRequest.setArg0("email-query");
		loRequest.setArg1(loStringMap);
		LOG_OBJECT.Debug("In userDnSearchVDX method, after setting loRequest 6");

		GlobalQueryResponse loresponse = loService.globalQuery(loRequest);
		LOG_OBJECT.Debug("In userDnSearchVDX method, after query 7");
		String[] loEntityAttributeMap = loresponse.getResult();
		LOG_OBJECT.Debug("In userDnSearchVDX method, after result 8");
		String lsUserDN = "";

		for (String lsEntry : loEntityAttributeMap)
		{
			lsUserDN = lsEntry;
		}
		LOG_OBJECT.Debug("In userDnSearchVDX method, before method return 9");
		return lsUserDN;
	}

	/**
	 * This method retrieves user details (first, middle, last name) based upon
	 * USERDN
	 * 
	 * @param asUserDN distinct for each email id and user, unique key to
	 *            identify
	 * @return Map<String, Object> has fields containing user first, middle and
	 *         last name
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws ApplicationException
	 */
	public Map<String, Object> userDetailsSearchOnUserDN(String asUserDN) throws ServiceException,
			MalformedURLException, RemoteException, ApplicationException
	{
		Map<String, Object> loUserDetailRtrndMap = new HashMap<String, Object>();

		VdxServiceLocator loLocater = new VdxServiceLocator();

		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, before loService 1");
		IRemoteVdx loService = loLocater.getIRemoteVdxPort(new URL(PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_URL_IDM_VDX)));
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, after loService 2");

		((Stub) loService)._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		((Stub) loService)._setProperty(Call.USERNAME_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_DN));
		((Stub) loService)._setProperty(Call.PASSWORD_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_PW));

		String[] lsUserDet = new String[]
		{ "nycExtEmailValidationFlag", "FirstName", "Initials", "LastName" };
		int liCount = 0;
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, before loRequest 3");
		GetAttributesRequest loRequest = new GetAttributesRequest();
		loRequest.setArg0(asUserDN);
		loRequest.setArg1("user");
		loRequest.setArg2(lsUserDet);
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, after loRequest 4");

		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, before lsResponse 5");
		GetAttributesResponse lsResponse = loService.getAttributes(loRequest);
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, after lsResponse 6");

		for (Attribute loStr : lsResponse.getResult())
		{
			if (null == loStr)
			{
				loUserDetailRtrndMap.put(lsUserDet[liCount], "");
			}
			else
			{
				if (AttributeType.Boolean.equals(loStr.getType()))
				{
					if (loStr.getBooleans()[0])
					{
						loUserDetailRtrndMap.put(lsUserDet[liCount], "Yes");
					}
					else
					{
						loUserDetailRtrndMap.put(lsUserDet[liCount], "No");
					}
				}
				else
				{
					if (loStr.getStrings().length == 0)
					{
						loUserDetailRtrndMap.put(lsUserDet[liCount], "");
					}
					else
					{
						loUserDetailRtrndMap.put(lsUserDet[liCount], loStr.getStrings()[0]);
					}
				}
			}
			liCount++;
		}
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, after loop 7");

		if ("NA".equalsIgnoreCase((String) loUserDetailRtrndMap.get("Initials")))
		{
			loUserDetailRtrndMap.put("Initials", "");
		}
		LOG_OBJECT.Debug("In userDetailsSearchOnUserDN method, before method return 8");
		return loUserDetailRtrndMap;
	}

	/**
	 * This method updates user profile in LDAP (first name, middle name and
	 * last name)
	 * 
	 * @param aoUserDetailUpdateMap contains first middle and last name of user
	 *            while update call to name
	 * @return Boolean status whether user profile updation in LDAP is sucess or
	 *         failure.
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws ServiceException
	 * @throws ApplicationException
	 */

	public Boolean UpdateUserProfileInLdap(Map<String, Object> aoUserDetailUpdateMap) throws RemoteException,
			MalformedURLException, ServiceException, ApplicationException
	{
		final VdxServiceLocator loLocater = new VdxServiceLocator();
		final IRemoteVdx loService = loLocater.getIRemoteVdxPort(new URL(PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_URL_IDM_VDX)));

		((Stub) loService)._setProperty(javax.xml.rpc.Stub.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		((Stub) loService)._setProperty(Call.USERNAME_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_DN));
		((Stub) loService)._setProperty(Call.PASSWORD_PROPERTY, PropertyLoader.getProperty(
				ApplicationConstants.PROPERTY_FILE, ApplicationConstants.PROPERTY_SERVICE_ACCOUNT_PW));

		String lsMiddleInitial = "NA";
		if (null != (String) aoUserDetailUpdateMap.get("MiddleInitial"))
		{
			if ((!"".equalsIgnoreCase(((String) aoUserDetailUpdateMap.get("MiddleInitial")).trim())))
			{
				lsMiddleInitial = (String) aoUserDetailUpdateMap.get("MiddleInitial");
			}
		}
		String lsUserDn = (String) aoUserDetailUpdateMap.get("UserDN");
		String lsUserDetail[] = new String[]
		{ (String) aoUserDetailUpdateMap.get("FirstName"), lsMiddleInitial,
				(String) aoUserDetailUpdateMap.get("LastName") };
		String loMappingArray[] = new String[]
		{ "FirstName", "Initials", "LastName" };

		for (int liCntr = 0; liCntr < 3; liCntr++)
		{
			Attribute loAttrib = new Attribute();
			loAttrib.setType(AttributeType.String);
			loAttrib.setStrings(new String[]
			{ lsUserDetail[liCntr] });

			SetAttributeRequest lorequest = new SetAttributeRequest();
			lorequest.setArg0(lsUserDn);
			lorequest.setArg1("user");

			lorequest.setArg2(loMappingArray[liCntr]);
			lorequest.setArg3(loAttrib);
			loService.setAttribute(lorequest);
		}
		return true;
	}

	/**
	 * This method authenticate the user
	 * 
	 * @param asUserName name of the user who is trying to login
	 * @param asPwd password of the user who is trying to login
	 * @return boolean status whether user is authenticated or not
	 */

	private boolean authenticate(String asUserName, String asPwd)
	{
		boolean lbIsUserValid = false;

		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("citymanager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("Filenet1"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("citymanager1") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("Filenet1"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("city_executive") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("city"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("hhs_staff1") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("city"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("hhs_staff2") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("city"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("hhs_mgr1") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("city"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("hhs_mgr2") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("city"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("accenture_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("csc_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("test_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("csc_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("accenture_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("test_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_administrator_Staff")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_administrator_Manager")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("sunny_kumar@example.com") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("Help@2020"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager1@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager2@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager3@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager4@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager5@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager6@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager7@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		else if (asUserName != null && asUserName.trim().equalsIgnoreCase("provider_manager8@gmail.com")
				&& asPwd != null && asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_acco_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_acco_staffAdmin") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_acco_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		// Added in R5
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_acco_manager2") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		// R5 Ends
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_program_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_program_staffAdmin") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_program_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_finance_staff") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_finance_staffAdmin") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_finance_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("agency_cfo") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("agency"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("r3accenture_manager") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("r3accenture_merged") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("r3accenture_merged1") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		if (asUserName != null && asUserName.trim().equalsIgnoreCase("r3accenture_merged2") && asPwd != null
				&& asPwd.trim().equalsIgnoreCase("provider"))
		{
			lbIsUserValid = true;
		}
		return lbIsUserValid;
	}

	/**
	 * This method fetch user details
	 * 
	 * @param aoUserBean for setting the various user details
	 * @return UserBean with details like user id, user name... etc
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public UserBean fetchUserRoleDetails(UserBean aoUserBean) throws ApplicationException
	{
		if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("agency_staff"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agencyuse1@doitt.nyc.gov");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("hhs_staff1"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
			aoUserBean.setMsOrgName(ApplicationConstants.CITY);
			StaffDetails loUserDetails = fetchCityUserDetails("hhs_staff1@doitt.nyc.gov");
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsOrgId(loUserDetails.getMsUserType());
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("hhs_staff2"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
			aoUserBean.setMsOrgName(ApplicationConstants.CITY);
			StaffDetails loUserDetails = fetchCityUserDetails("hhs_staff2@doitt.nyc.gov");
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsOrgId(loUserDetails.getMsUserType());
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("hhs_mgr1"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
			aoUserBean.setMsOrgName(ApplicationConstants.CITY);
			// Changes made as a part of Enhancement #6280 for Release 3.3.0
			StaffDetails loUserDetails = fetchCityUserDetails("hhs_a@doitt.nyc.gov");
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsOrgId(loUserDetails.getMsUserType());
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("hhs_mgr2"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
			aoUserBean.setMsOrgName(ApplicationConstants.CITY);
			StaffDetails loUserDetails = fetchCityUserDetails("hhs_mgr2@doitt.nyc.gov");
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsOrgId(loUserDetails.getMsUserType());
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("provider_staff"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
			aoUserBean.setMsOrgId("provider");
			aoUserBean.setMsUserId("123");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers12,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("csc_staff"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
			aoUserBean.setMsOrgId("csc");
			aoUserBean.setMsUserId("223");
			aoUserBean.setMsOrgName("CSC");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers13,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("csc_manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("csc");
			aoUserBean.setMsUserId("193");
			aoUserBean.setMsOrgName("CSC");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers14,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("accenture_staff"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
			aoUserBean.setMsOrgId("accenture");
			aoUserBean.setMsUserId("999");
			aoUserBean.setMsOrgName("accenture M");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers15,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("accenture_manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("accenture");
			aoUserBean.setMsUserId("803");
			aoUserBean.setMsOrgName("accenture M");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers16,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("test_staff"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
			aoUserBean.setMsOrgId("test");
			aoUserBean.setMsUserId("163");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers17,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("test_manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("test");
			aoUserBean.setMsUserId("283");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers18,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider");
			aoUserBean.setMsUserId("170");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers19,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_administrator_Staff"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF);
			aoUserBean.setMsOrgId("csc");
			aoUserBean.setMsUserId("169");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers20,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_administrator_Manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("csc");
			aoUserBean.setMsUserId("269");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers21,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("sunny_kumar@example.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("provider_org");
			aoUserBean.setMsUserId("sunny_kumar@example.com");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager1@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider1");
			aoUserBean.setMsUserId("111");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers71,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager2@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider2");
			aoUserBean.setMsUserId("222");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers72,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager3@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider3");
			aoUserBean.setMsUserId("333");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers73,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager4@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider4");
			aoUserBean.setMsUserId("444");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers74,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager5@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider5");
			aoUserBean.setMsUserId("555");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers75,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager6@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider6");
			aoUserBean.setMsUserId("666");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers76,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager7@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider7");
			aoUserBean.setMsUserId("777");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers77,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("provider_manager8@gmail.com"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
			aoUserBean.setMsOrgId("provider8");
			aoUserBean.setMsUserId("888");
			aoUserBean.setMsOrgName(ApplicationConstants.PROVIDER);
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers78,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("r3accenture_manager"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("r3_org");
			aoUserBean.setMsUserId("909");
			aoUserBean.setMsOrgName("R3 dev team organization");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers99,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("r3accenture_merged"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("r3merged_org");
			aoUserBean.setMsUserId("90911");
			aoUserBean.setMsOrgName("R3 dev team organization merged");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers90911,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("r3accenture_merged1"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("r3merged_org1");
			aoUserBean.setMsUserId("90912");
			aoUserBean.setMsOrgName("R3 dev team organization merged1");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers90912,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("r3accenture_merged2"))
		{
			aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
			aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
			aoUserBean.setMsOrgId("r3merged_org2");
			aoUserBean.setMsUserId("90913");
			aoUserBean.setMsOrgName("R3 dev team organization merged2");
			aoUserBean.setMsUserDN("cn=YQASRX8F,ou=ExtUsers90913,o=External");
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("agency_acco_staff"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_acco_staff@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_acco_staffAdmin"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_acco_staffAdmin@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("agency_acco_manager"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_acco_manager@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		// Added in R5
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_acco_manager2"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_acco_manager2@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		// R5 changes Ends
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_program_staff"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_program_staff@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_program_staffAdmin"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_program_staffAdmin@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_program_manager"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_program_manager@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_finance_staff"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_finance_staff@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_finance_staffAdmin"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_finance_staffAdmin@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null
				&& aoUserBean.getMsLoginId().equalsIgnoreCase("agency_finance_manager"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_finance_manager@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}
		else if (aoUserBean.getMsLoginId() != null && aoUserBean.getMsLoginId().equalsIgnoreCase("agency_cfo"))
		{
			StaffDetails loUserDetails = fetchCityUserDetails("agency_cfo@nyc.com");
			aoUserBean.setMsOrgType(loUserDetails.getMsUserType());
			aoUserBean.setMsRole(loUserDetails.getMsStaffRole());
			aoUserBean.setMsUserId(loUserDetails.getMsStaffId());
			aoUserBean.setMsUserName(loUserDetails.getMsStaffFirstName().concat(" ")
					.concat(loUserDetails.getMsStaffLastName()));
			aoUserBean.setMsUserEmail(loUserDetails.getMsStaffEmail());
			aoUserBean.setMsOrgId(loUserDetails.getMsOrganisationName());
			aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName((TreeSet) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.AGENCY_LIST), loUserDetails.getMsOrganisationName()));
		}

		return aoUserBean;
	}

	/**
	 * This method fetches StaffDetails like first name, middle name, role,
	 * flags... etc when provided with correct credentials (login id)
	 * 
	 * @param asLoginId of the city user
	 * @return StaffDetails beans with details like first name, middle name,
	 *         role, flags... etc
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private StaffDetails fetchCityUserDetails(String asLoginId) throws ApplicationException
	{
		StaffDetails loUserDetails = new StaffDetails();
		List<StaffDetails> loCityUserDetails = null;
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, "fetchCityUserDetails");
		loCityUserDetails = (List<StaffDetails>) loChannel.getData("masterUserList");

		Iterator loItr = loCityUserDetails.iterator();
		while (loItr.hasNext())
		{
			loUserDetails = (StaffDetails) loItr.next();

			if (asLoginId.equalsIgnoreCase(loUserDetails.getMsStaffEmail()))
			{
				break;
			}
		}
		return loUserDetails;
	}
}
