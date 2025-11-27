package com.nyc.hhs.controllers;

import java.io.IOException;
import java.util.ArrayList;
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

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.PortalUtil;

/**
 * This controller is used to manage user profile. It provides three basic
 * functionalities: 1. update password 2. update user security questions 3.
 * update name and email
 * 
 */

public class HomeAccountMaintenanceController extends AbstractController
{

    private static final LogInfo LOG_OBJECT = new LogInfo(HomeAccountMaintenanceController.class);

    private static final String THIS_REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
    private static final String REGISTER_NYC_ID_BEAN = "RegisterNycIdBean";
    private static final String TOKEN_GENERATED = "You will receive activation emails shortly at both your previous and updated email address."
            + " You may use the links in either email to validate your new email address.";
    private static final String EMAIL_ALREADY_EXIST_IN_NYC = "This email already exists for a NYC.ID Account. ";
    private static final String EMAIL_ALREADY_EXIST_IN_NYC_1 = "Please enter a different email or Cancel to return to the login page.";
    private static final String ONLY_NAME_UPDATED = " Your name has been successfully updated.";
    private static final String OLD_PWD_NOT_CORRECT = "! The entered password does not match our records.";
    private static final String EMAIL_CHANGE_DENIED = "You have recently changed your email address but it is not yet validated.";
    private static final String EMAIL_CHANGE_DENIED_1 = " Kindly check your email inbox.";
    private static final String PASSWORD_COMPOSITION_NOT_OK = "Your password cannot contain your first name, last name, email address"
            + " or the word 'password'. Please enter a new password.";
    private static final String OLD_NEW_PWD_CANNOT_SAME = "Password must be unique and cannot match a previous password.";

    /**
     * This method is to render the next page depending on the action, Manage
     * user details process
     * 
     * @param aoRequest to get screen parameters and next page to be displayed
     * @param aoResponse setting response parameter for JSP variables
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest, RenderResponse aoResponse)
            throws ApplicationException
    {
        long loStartTime = System.currentTimeMillis();
        PortletSession loPortletSessionThread = aoRequest.getPortletSession();
        String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
                ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
        UserThreadLocal.setUser(lsUserIdThreadLocal);
        Map<String, String> loMapUserData = null;
        PortletSession loPortletSession = aoRequest.getPortletSession();
        String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
                PortletSession.APPLICATION_SCOPE);
        aoResponse.setContentType("text/html");
        String lsFormPath = "homeaccountmaintenance";
        ModelAndView loModelAndView = null;
        Map<String, Object> loMapToRender = new HashMap<String, Object>();
        // Condition: 1: This condition evaluates if the user clicks on update
        // password link on provider homepage.
        if ("updatenycpassword".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "targetResource")))
        {
            if (null != aoRequest.getParameter("errorToDisplay")
                    && "true".equalsIgnoreCase(aoRequest.getParameter("errorToDisplay")))
            {
                loMapToRender.put("error_to_display", "true");
            }
            lsFormPath = "updatenycpassword";
        }
        // Condition: 2: This condition evaluates if the user clicks on update
        // security question link on provider homepage.
        else if ("updatenycsecurityquestion".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "targetResource")))
        {
            if (null != aoRequest.getParameter("errorToDisplay")
                    && "true".equalsIgnoreCase(aoRequest.getParameter("errorToDisplay")))
            {
                loMapToRender.put("error_to_display", "true");
            }
            aoRequest.getPortletSession().removeAttribute("RegisterNycIdBean", PortletSession.APPLICATION_SCOPE);
            Channel loChannelObj = new Channel();
            getSecurityQuestion(aoRequest, aoResponse, loChannelObj);
            lsFormPath = "updatenycsecurityquestion";
        }
        // Condition: 3: This condition evaluates if the user clicks on update
        // name and email link on provider homepage.
        else if ("updatenycnameandemail".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "targetResource")))
        {
            lsFormPath = "updatenycnameandemail";
            if (null != aoRequest.getParameter("errorToDisplay")
                    && "true".equalsIgnoreCase(aoRequest.getParameter("errorToDisplay")))
            {
                loMapToRender.put("error_to_display", "true");
                loMapUserData = (Map<String, String>) ApplicationSession.getAttribute(aoRequest, "loMapUserData");
            }
            else
            {
                loMapUserData = getNameAndEmailfromDB(aoRequest, loMapUserData, lsUserId, lsOrgId);
            }
            loMapToRender.put("userDataMap", loMapUserData);
            lsFormPath = "updatenycnameandemail";
        }
        // Condition: 3.1 : condition 3 passed, Token generated and user is
        // brought to same screen(update name email) with message
        else if ("updateEmailTokenGenerated".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "targetResource")))
        {
            lsFormPath = "updatenycnameandemail";
            loMapUserData = (Map) ApplicationSession.getAttribute(aoRequest, false, "loMapEmailUpdated");
            loMapToRender.put("userDataMap", loMapUserData);
        }
        // Condition: 3.2 : condition 3 passed, only name is updated, email is
        // same
        else if ("onlyNameUpdated".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "targetResource")))
        {
            lsFormPath = "updatenycnameandemail";
            loMapUserData = (Map) ApplicationSession.getAttribute(aoRequest, false, "loMapEmailUpdated");
            loMapToRender.put("userDataMap", loMapUserData);
        }
        // Condtition: 3.3 : user clicked save button without changing anything
        else if ("updatenycnameandemailnochange".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
                "targetResource")))
        {
            lsFormPath = "updatenycnameandemail";
            loMapUserData = (Map) ApplicationSession.getAttribute(aoRequest, false, "loMapEmailUpdated");
            loMapToRender.put("userDataMap", loMapUserData);
        }
        // condition for transaction status messages(passed or failed)
        if (aoRequest.getParameter("transactionMessage") != null
                && !"".equalsIgnoreCase(aoRequest.getParameter("transactionMessage")))
        {
            aoRequest.setAttribute("transactionStatus", aoRequest.getParameter("transactionStatus"));
            aoRequest.setAttribute("transactionMessage", aoRequest.getParameter("transactionMessage"));
        }
        /* Start : R5 Added */
        if (lsFormPath.equalsIgnoreCase(HHSR5Constants.HOME_ACCOUNT_MAINTENANCE))
        {
            Channel loChannel = new Channel();
            loChannel.setData(HHSR5Constants.AS_ORG_ID, lsOrgId);
            TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_USER_ACCOUNT_REQUEST);
            int liUSerAccountCount = (Integer) loChannel.getData(HHSR5Constants.USER_ACCOUNT_COUNT);
            aoRequest.setAttribute(HHSR5Constants.USER_ACCOUNT_COUNT, liUSerAccountCount);
        }
        /* End : R5 Added */
        loModelAndView = new ModelAndView(lsFormPath, loMapToRender);

        long loEndTimeTime = System.currentTimeMillis();
        try
        {
            LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in HomeAccountMaintenanceController = "
                    + (loEndTimeTime - loStartTime));
        }
        catch (ApplicationException aoEx)
        {
            LOG_OBJECT.Error("Error while execution of render Method in HomeAccountMaintenanceController", aoEx);
        }
        UserThreadLocal.unSet();
        return loModelAndView;
    }

    /**
     * This method retrieves name and email from database
     * 
     * @param aoRequest to get screen parameters and next action to be performed
     * @param aoMapUserData has fields containing name and email of user
     * @param asUserId unique id of user
     * @param asOrgId organization id of user
     * @return Map<String, String> loMapUserData has fields containing name and
     *         email of user
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getNameAndEmailfromDB(RenderRequest aoRequest, Map<String, String> aoMapUserData,
            String asUserId, String asOrgId) throws ApplicationException
    {
        Channel loChannel = new Channel();
        loChannel.setData("lsUserId", asUserId);
        loChannel.setData("lsOrgId", asOrgId);
        TransactionManager.executeTransaction(loChannel, "getUserDataFromDB");
        if (null != loChannel.getData("loMapUserData"))
        {
            aoMapUserData = (Map<String, String>) loChannel.getData("loMapUserData");
            String lsFirstName = aoMapUserData.get("FIRST_NAME");
            String lsLastName = aoMapUserData.get("LAST_NAME");
            String lsMiddleName = aoMapUserData.get("MIDDLE_INITIAL");
            String lsEmail = aoMapUserData.get("EMAIL");
            // setting in session (actual/old values retrieved from
            // database on page load, this will set as old values only during
            // page load)
            // incase of update name and email, if transaction passes then again
            // we need to set these values in session
            ApplicationSession.setAttribute(lsFirstName, aoRequest, "FirstName");
            ApplicationSession.setAttribute(lsLastName, aoRequest, "LastName");
            ApplicationSession.setAttribute(lsMiddleName, aoRequest, "MiddleName");
            ApplicationSession.setAttribute(lsEmail, aoRequest, "Email");
        }
        return aoMapUserData;
    }

    /**
     * This method decide the execution flow for manage user details process
     * 
     * @param aoRequest to get screen parameters and next action to be performed
     * @param aoResponse decides the next execution flow
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes" })
    protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse)
            throws ApplicationException
    {
        long loStartTime = System.currentTimeMillis();
        PortletSession loPortletSessionThread = aoRequest.getPortletSession();
        String lsUserIdThreadLocal = (String) loPortletSessionThread.getAttribute(
                ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
        UserThreadLocal.setUser(lsUserIdThreadLocal);
        Map<String, String> loUserMapFilledData = new HashMap<String, String>();
        String lsPassword = null, lsTransactionStatusMsg = "", lsTransactionStatus = "", lsUnhandledCase = null;
        boolean lbSendRedirectFlag = false, lbEmailupdated = false;
        PortletSession loPortletSession = aoRequest.getPortletSession();
        String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.BUSINESS_APPLICATION_ACTION);
        String lsUserId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        String lsOrgId = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
                PortletSession.APPLICATION_SCOPE);
        String lsUserDN = (String) loPortletSession.getAttribute(ApplicationConstants.USER_DN,
                PortletSession.APPLICATION_SCOPE);
        try
        {
            lsTransactionStatus = "failed";
            lsTransactionStatusMsg = THIS_REQUEST_COULD_NOT_BE_COMPLETED;
            if (null != lsAction && "updatenycnameandemail".equalsIgnoreCase(lsAction))
            {
                boolean lsNoChangeInNameEmail = true;
                aoResponse.setRenderParameter("targetResource", "updatenycnameandemailnochange");
                String lsFirstName = aoRequest.getParameter("fName").trim(), lsLastName = aoRequest.getParameter(
                        "lName").trim();
                String lsMiddleName = aoRequest.getParameter("mName").trim(), lsEmailAddress = aoRequest
                        .getParameter("emailAdd");
                String lsFirstNameOld = (String) ApplicationSession.getAttribute(aoRequest, true, "FirstName"), lsMiddleNameOld = "";
                String lsLastNameOld = (String) ApplicationSession.getAttribute(aoRequest, true, "LastName"), lsEmailDenied = "";
                if (null == ApplicationSession.getAttribute(aoRequest, true, "MiddleName"))
                {
                    ApplicationSession.setAttribute("", aoRequest, "MiddleName");
                }
                lsMiddleNameOld = (String) ApplicationSession.getAttribute(aoRequest, true, "MiddleName");
                String lsEmailNameOld = (String) ApplicationSession.getAttribute(aoRequest, true, "Email");
                Map<String, String> loUserEmailUPdatedTokenMap = populateMapForNameAndEmail(aoRequest,
                        loUserMapFilledData, lsUserId, lsOrgId, lsUserDN, lsFirstName, lsLastName, lsMiddleName,
                        lsEmailAddress, lsFirstNameOld, lsLastNameOld, lsMiddleNameOld, lsEmailNameOld);
                Channel loChannel = new Channel();
                loUserMapFilledData.put("modifiedBy", lsUserId);
                loChannel.setData("loUserMapFilledData", loUserMapFilledData);
                // Condition 3.a :If only email address is changed
                if (!lsEmailNameOld.equalsIgnoreCase(lsEmailAddress))
                {
                    lsNoChangeInNameEmail = false;
                    lsUnhandledCase = "updatenycnameandemail";
                    Map loStatusMap = new HashMap();
                    loStatusMap = (Map) emailAddressChangFlow(aoRequest, aoResponse, lsEmailAddress, lsFirstNameOld,
                            lsLastNameOld, lsMiddleNameOld, lsEmailNameOld, loUserEmailUPdatedTokenMap, loChannel,
                            lbEmailupdated, lsTransactionStatus);
                    lsTransactionStatus = (String) loStatusMap.get("TransactionStatus");
                    lsTransactionStatusMsg = (String) loStatusMap.get("TransactionStatusMsg");
                    lbEmailupdated = (Boolean) loStatusMap.get("Emailupdated");
                    lsEmailDenied = (String) loStatusMap.get("EmailChangeDenied");
                }
                if ((!lsFirstNameOld.equalsIgnoreCase(lsFirstName) || !lsLastNameOld.equalsIgnoreCase(lsLastName)// If
                                                                                                                    // only
                                                                                                                    // name
                                                                                                                    // is
                                                                                                                    // changed(
                                                                                                                    // any
                                                                                                                    // )
                || !lsMiddleNameOld.equalsIgnoreCase(lsMiddleName)) && "".equalsIgnoreCase(lsEmailDenied))
                {
                    lsNoChangeInNameEmail = false;
                    lsUnhandledCase = "updatenycnameandemail";
                    TransactionManager.executeTransaction(loChannel, "updateNYCUserData");
                    Boolean lbUpdateStatus = false;
                    if (null != loChannel.getData("lbUpdateStatus"))
                    {
                        lbUpdateStatus = (Boolean) loChannel.getData("lbUpdateStatus");
                    }
                    if (!lbUpdateStatus)
                    {
                        aoResponse.setRenderParameter("errorToDisplay", "true");
                        aoResponse.setRenderParameter("targetResource", "updatenycnameandemail");
                        ApplicationSession.setAttribute(loUserMapFilledData, aoRequest, "loMapUserData");
                    }
                    else
                    {
                        Boolean lbManageUserDataUpdated = (Boolean) loChannel.getData("lbManageUserDataUpdated");
                        if (lbManageUserDataUpdated)
                        {
                            lsTransactionStatus = "passed";
                            lsTransactionStatusMsg = persistDataAfterNameEmailUpdate(aoRequest, aoResponse,
                                    lbEmailupdated, lsFirstName, lsLastName, lsMiddleName, lsEmailAddress,
                                    lsEmailNameOld, loUserEmailUPdatedTokenMap);
                        }
                    }
                }
                if (lsNoChangeInNameEmail)
                {
                    lsTransactionStatus = "";
                    lsTransactionStatusMsg = "";
                }
            } // next block controls the flow of Updating NYC Password ---2
            else if (null != lsAction && "updatenycpassword".equalsIgnoreCase(lsAction))
            {
                lsUnhandledCase = "updatenycpassword";
                Map loStatusMap = new HashMap();
                loStatusMap = (Map) updateNycPassword(aoRequest, aoResponse, lsTransactionStatusMsg, lsUserDN,
                        lsTransactionStatus, lbSendRedirectFlag, loPortletSession);
                lsTransactionStatus = (String) loStatusMap.get("TransactionStatus");
                lsTransactionStatusMsg = (String) loStatusMap.get("TransactionStatusMsg");
                lbSendRedirectFlag = (Boolean) loStatusMap.get("SendRedirectFlag");
            }
            else if (null != lsAction && "updateSecurityQuestions".equalsIgnoreCase(lsAction))
            {
                lsUnhandledCase = "updatenycsecurityquestion";
                Map<String, String> loMapService = populateDataForUpdateSecQues(aoRequest, lsPassword,
                        loPortletSession, lsUserDN);
                Channel loChannel = new Channel();
                loChannel.setData("serviceDataMap", loMapService);
                TransactionManager.executeTransaction(loChannel, "updateNYCServiceQuestions");
                String lbUpdateStatus = "";
                if (null != loChannel.getData("lbUpdateStatus"))
                {
                    lbUpdateStatus = (String) loChannel.getData("lbUpdateStatus");
                }
                if (lbUpdateStatus.contains("Challenge responses were saved successfully"))
                {
                    lbSendRedirectFlag = redirectToProviderHomePage(aoRequest, aoResponse, lbSendRedirectFlag);
                }
                else if (!lbUpdateStatus.contains("Success"))
                {
                    lsTransactionStatusMsg = updateQuesFails(aoRequest, aoResponse, lbUpdateStatus);
                }
            } // end of condition 2 (security question change)
            if (!lbSendRedirectFlag)
            {
                aoResponse.setRenderParameter("transactionStatus", lsTransactionStatus);
                aoResponse.setRenderParameter("transactionMessage", lsTransactionStatusMsg);
            }
        }
        catch (ApplicationException aoFbAppEx)
        {
            catchApplicationException(aoResponse, lsTransactionStatusMsg, lsUnhandledCase, aoFbAppEx);
        }
        catch (Exception aoFbAppEx)
        {
            catchThrowable(aoResponse, lsTransactionStatusMsg, lsUnhandledCase, aoFbAppEx);
        }

        long loEndTimeTime = System.currentTimeMillis();
        try
        {
            LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in HomeAccountMaintenanceController = "
                    + (loEndTimeTime - loStartTime));
        }
        catch (ApplicationException aoEx)
        {
            LOG_OBJECT.Error("Error while execution of action Method in HomeAccountMaintenanceController ", aoEx);
        }
        UserThreadLocal.unSet();
    }

    /**
     * This method is used to update nyc password
     * @param aoRequest to get screen parameters and next action to be performed
     * @param aoResponse setting response parameter for JSP variables
     * @param asTransactionStatusMsg asTransactionStatusMsg stating success or
     *            failure messages
     * @param asUserDN lsUserDN unique value of logged in user
     * @param asTransactionStatus transaction status stating success or failure
     * @param abSendRedirectFlag abSendRedirectFlag stating redirect to another
     *            controller or not
     * @param aoPortletSession aoPortletSession a portlet session.
     * @return Map loStatusMap containing status field of transaction
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private Map updateNycPassword(ActionRequest aoRequest, ActionResponse aoResponse, String asTransactionStatusMsg,
            String asUserDN, String asTransactionStatus, boolean abSendRedirectFlag, PortletSession aoPortletSession)
            throws ApplicationException
    {
        // calling user details web service to check password composition is ok
        String lsPassword;
        boolean lbPasswordCompositionOK = true;
        lsPassword = aoRequest.getParameter("newPassword");
        Map loStatusMap = new HashMap();
        PortletSession loPortletSession = aoRequest.getPortletSession();
        String lsUserEmailAdd = (String) loPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
                PortletSession.APPLICATION_SCOPE);
        Map loUserDetailRtrndMap = fetchUserDetails(asUserDN);
        String lsUserFirstName = (String) loUserDetailRtrndMap.get("FirstName");
        String lsUserLastName = (String) loUserDetailRtrndMap.get("LastName");
        if (lsPassword.toLowerCase().contains(lsUserFirstName.toLowerCase())
                || lsPassword.toLowerCase().contains(lsUserLastName.toLowerCase())
                || lsPassword.toLowerCase().contains(lsUserEmailAdd.toLowerCase())
                || lsPassword.toLowerCase().contains("password"))
        {
            lbPasswordCompositionOK = false;
        }
        if (lbPasswordCompositionOK)
        {
            String lbUpdateStatus = "", lsOldPassword = aoRequest.getParameter("oldPassword");
            Map<String, String> loMapService = new HashMap<String, String>();
            loMapService.put("userPassword", lsPassword);
            loMapService.put("userOldPassword", lsOldPassword);
            loMapService.put("UserDN", asUserDN);
            Channel loChannel = new Channel();
            loChannel.setData("serviceDataMap", loMapService);
            TransactionManager.executeTransaction(loChannel, "updateNYCUserPassword");
            if (null != loChannel.getData("lbUpdateStatus"))
            {
                lbUpdateStatus = (String) loChannel.getData("lbUpdateStatus");
            }
            if (!lbUpdateStatus.contains("password has been changed successfully"))
            {
                aoResponse.setRenderParameter("errorToDisplay", "true");
                asTransactionStatusMsg = lbUpdateStatus;
                if (lbUpdateStatus.contains("Service not authorized"))
                {
                    asTransactionStatusMsg = HomeAccountMaintenanceController.OLD_PWD_NOT_CORRECT;
                }
                if (lbUpdateStatus.contains("Password must be unique and cannot match a previous password"))
                {
                    asTransactionStatusMsg = HomeAccountMaintenanceController.OLD_NEW_PWD_CANNOT_SAME;
                }
                aoResponse.setRenderParameter("targetResource", "updatenycpassword");
            }
            else
            {
                asTransactionStatus = "passed";
                abSendRedirectFlag = sendNotificationForPwd(aoRequest, aoResponse, abSendRedirectFlag,
                        aoPortletSession, loChannel);
            }
            loStatusMap.put("TransactionStatus", asTransactionStatus);
            loStatusMap.put("TransactionStatusMsg", asTransactionStatusMsg);
            loStatusMap.put("SendRedirectFlag", abSendRedirectFlag);
        }
        else
        {
            loStatusMap.put("TransactionStatus", "failed");
            loStatusMap.put("TransactionStatusMsg", HomeAccountMaintenanceController.PASSWORD_COMPOSITION_NOT_OK);
            loStatusMap.put("SendRedirectFlag", "false");
        }
        return loStatusMap;
    }

    /**
     * This method process the flow when only email address is changed
     * 
     * @param aoRequest to get screen parameters and next action to be performed
     * @param aoResponse setting response parameter for JSP variables
     * @param asEmailAddress new email address of user
     * @param asFirstNameOld old name of user
     * @param asLastNameOld last name of user
     * @param asMiddleNameOld middle initial of user
     * @param asEmailNameOld old email address of user
     * @param aoUserEmailUPdatedTokenMap map with fields when only email is
     *            updated (not name)
     * @param aoChannel channel object to send and receive values across
     *            transaction
     * @param abEmailupdated boolean status stating whether email is updated or
     *            not
     * @param asTransactionStatus transaction status stating success or failure
     * @return Map loStatusMap containing status field of transaction
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private Map emailAddressChangFlow(ActionRequest aoRequest, ActionResponse aoResponse, String asEmailAddress,
            String asFirstNameOld, String asLastNameOld, String asMiddleNameOld, String asEmailNameOld,
            Map<String, String> aoUserEmailUPdatedTokenMap, Channel aoChannel, boolean abEmailupdated,
            String asTransactionStatus) throws ApplicationException
    {
        String lsEmailDenied = "";
        String lsTransactionStatusMsg = HomeAccountMaintenanceController.THIS_REQUEST_COULD_NOT_BE_COMPLETED;
        Map loStatusMap = new HashMap();
        aoResponse.setRenderParameter("targetResource", "updatenycnameandemail");
        TransactionManager.executeTransaction(aoChannel, "updateNYCEmailTokenGen");
        Map loPwdChangeDetailsMap = null;
        loPwdChangeDetailsMap = (HashMap) aoChannel.getData("aoEmailUpdateRtrndMap");
        String lsServiceStatus = (String) loPwdChangeDetailsMap.get("serviceStatus");
        String lsServiceOutputList = (String) loPwdChangeDetailsMap.get("serviceOutput");
        if ("error".equalsIgnoreCase(lsServiceStatus))
        {
            lsTransactionStatusMsg = lsServiceOutputList;
            if (lsTransactionStatusMsg.contains("Email ID not unique"))
            {
                lsTransactionStatusMsg = HomeAccountMaintenanceController.EMAIL_ALREADY_EXIST_IN_NYC
                        + EMAIL_ALREADY_EXIST_IN_NYC_1;
                lsEmailDenied = HomeAccountMaintenanceController.EMAIL_ALREADY_EXIST_IN_NYC
                        + EMAIL_ALREADY_EXIST_IN_NYC_1;
            }
            else if (lsTransactionStatusMsg.contains("Email Update Token Generation Service Email not Found"))
            {
                lsEmailDenied = HomeAccountMaintenanceController.EMAIL_CHANGE_DENIED + EMAIL_CHANGE_DENIED_1;
                lsTransactionStatusMsg = HomeAccountMaintenanceController.EMAIL_CHANGE_DENIED + EMAIL_CHANGE_DENIED_1;
            }
            else if (lsTransactionStatusMsg.contains("Email not Found"))
            {
                lsTransactionStatusMsg = lsServiceOutputList;
            }
        }
        else
        {
            // setting values to persist
            abEmailupdated = true;
            asTransactionStatus = "passed";
            lsTransactionStatusMsg = sendNotifForEmailChange(aoRequest, aoResponse, asEmailAddress, asFirstNameOld,
                    asLastNameOld, asMiddleNameOld, asEmailNameOld, aoUserEmailUPdatedTokenMap, aoChannel,
                    lsServiceOutputList);
        }

        loStatusMap.put("TransactionStatus", asTransactionStatus);
        loStatusMap.put("TransactionStatusMsg", lsTransactionStatusMsg);
        loStatusMap.put("Emailupdated", abEmailupdated);
        loStatusMap.put("EmailChangeDenied", lsEmailDenied);
        return loStatusMap;
    }

    /**
     * This method is called when exception is thrown of Throwable class
     * transaction status and message are rendered properly
     * 
     * @param aoResponse setting response parameter for JSP variables
     * @param asTransactionStatusMsg message to be displayed on jsp
     * @param asUnhandledCase states event type (like update nyc security
     *            question, or update password , update name)
     * @param aoFbAppEx object of type Throwable
     */
    private void catchThrowable(ActionResponse aoResponse, String asTransactionStatusMsg, String asUnhandledCase,
            Throwable aoFbAppEx)
    {
        aoResponse.setRenderParameter("transactionStatus", "failed");
        if (asUnhandledCase != null)
        {
            aoResponse.setRenderParameter("targetResource", asUnhandledCase);
            aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
        }
        else
        {
            aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
        }
        LOG_OBJECT.Error("Error occured in HomeAccountMaintenance ", aoFbAppEx);
    }

    /**
     * This method is called when exception is thrown of ApplicationException
     * class transaction status and message are rendered properly
     * 
     * @param aoResponse setting response parameter for JSP variables
     * @param asTransactionStatusMsg message to be displayed on jsp
     * @param asUnhandledCase states event type (like update nyc security
     *            question, or update password , update name)
     * @param aoFbAppEx object of type ApplicationException
     */
    private void catchApplicationException(ActionResponse aoResponse, String asTransactionStatusMsg,
            String asUnhandledCase, ApplicationException aoFbAppEx)
    {
        if (asUnhandledCase != null)
        {
            aoResponse.setRenderParameter("targetResource", asUnhandledCase);
            aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
        }
        else
        {
            aoResponse.setRenderParameter("transactionMessage", asTransactionStatusMsg);
        }
        aoResponse.setRenderParameter("transactionStatus", "failed");
        LOG_OBJECT.Error("Error occured in HomeAccountMaintenance  ", aoFbAppEx);
    }

    /**
     * This method sends notification to the user when user has changed/updated
     * email This method is updated in R4 due to change in notification
     * framework
     * @param aoRequest to get screen parameters
     * @param aoResponse setting response parameter for JSP variables
     * @param asEmailAddress (new email address )
     * @param asFirstNameOld first name of user
     * @param asLastNameOld last name of user
     * @param asMiddleNameOld middle name of user
     * @param asEmailNameOld (old email address)
     * @param aoUserEmailUPdatedTokenMap has fields related to user details(
     *            name and email)
     * @param aoChannelObj Channel Object that needs to be set to send and
     *            receive values across transaction
     * @param aoServiceOutputList has status message of transaction
     *            (success/failure)
     * @return String status of notification transaction
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private String sendNotifForEmailChange(ActionRequest aoRequest, ActionResponse aoResponse, String asEmailAddress,
            String asFirstNameOld, String asLastNameOld, String asMiddleNameOld, String asEmailNameOld,
            Map<String, String> aoUserEmailUPdatedTokenMap, Channel aoChannel, String loServiceOutputList)
            throws ApplicationException
    {
        String lsTransactionStatusMsg = HomeAccountMaintenanceController.THIS_REQUEST_COULD_NOT_BE_COMPLETED;

        aoResponse.setRenderParameter("targetResource", "updateEmailTokenGenerated");
        aoUserEmailUPdatedTokenMap.put("FIRST_NAME", asFirstNameOld);
        aoUserEmailUPdatedTokenMap.put("LAST_NAME", asLastNameOld);
        aoUserEmailUPdatedTokenMap.put("MIDDLE_INITIAL", asMiddleNameOld);
        aoUserEmailUPdatedTokenMap.put("EMAIL", asEmailAddress);

        // over-riding old value with newly saved values
        ApplicationSession.setAttribute(asEmailAddress, aoRequest, "Email");

        ApplicationSession.setAttribute(aoUserEmailUPdatedTokenMap, aoRequest, "loMapEmailUpdated");
        // end
        lsTransactionStatusMsg = HomeAccountMaintenanceController.TOKEN_GENERATED;
        String lsEmailToken = (String) loServiceOutputList;
        String lsEmailLink = aoRequest.getScheme() + "://" + aoRequest.getServerName() + ":"
                + aoRequest.getServerPort() + aoRequest.getContextPath() + ApplicationConstants.PORTAL_URL
                + "&_pageLabel=portlet_hhsweb_portal_login_page&tokenvalidationafteremailchanged"
                + "=tokenvalidationafteremailchanged&&accoutRequestmodule=accoutRequestmodule&emailToken="
                + lsEmailToken + "&oldEmailAddress=" + asEmailNameOld + "&newEmailAddress=" + asEmailAddress;
        String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
                ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);

        String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);
        String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
                PortletSession.APPLICATION_SCOPE);
        List<String> loNotificationAlertList = new ArrayList<String>();
        loNotificationAlertList.add("NT039");

        List<String> loProviderList = new ArrayList<String>();
        loProviderList.add(lsOrgId);
        HashMap<String, String> loLinkMap = new HashMap<String, String>();
        loLinkMap.put("LINK", lsEmailLink);
        NotificationDataBean loNotificationDataBean = new NotificationDataBean();
        loNotificationDataBean.setLinkMap(loLinkMap);
        loNotificationDataBean.setProviderList(loProviderList);

        HashMap<String, String> loParamMap = new HashMap<String, String>();
        loParamMap.put("USERNAME", lsUserName);
        loParamMap.put("NEWEMAIL", asEmailAddress); // new email address
        loParamMap.put("OLDEMAIL", asEmailNameOld);

        HashMap<String, String> loAddParamMap = new HashMap<String, String>();
        loAddParamMap.put("NEWEMAIL", asEmailAddress);
        loAddParamMap.put("OLDEMAIL", asEmailNameOld);
        loNotificationDataBean.setAdditionalParameterMap(loAddParamMap);
        HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
        loNotificationMap.put("NT039", loNotificationDataBean);
        loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "userId");
        loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsUserId);
        loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
        loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
        loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
        loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
        aoChannel.setData("loHmNotifyParam", loNotificationMap);
        TransactionManager.executeTransaction(aoChannel, "insertNotificationDetail");
        return lsTransactionStatusMsg;
    }

    /**
     * This method is called when user opted to change the security questions
     * and web service denied for it
     * 
     * @param aoRequest to get screen parameters
     * @param aoResponse setting response parameter for JSP variables
     * @param abUpdateStatus returned status of web service
     * @return String lsTransactionStatusMsg that needs to be displayed on
     *         screen
     */
    private String updateQuesFails(ActionRequest aoRequest, ActionResponse aoResponse, String abUpdateStatus)
    {
        String lsTransactionStatusMsg;
        if (abUpdateStatus.contains("User Defined questions cannot be identical"))
        {
            lsTransactionStatusMsg = "User Defined questions cannot be identical";
        }
        else if (abUpdateStatus.contains("Service not authorized"))
        {
            lsTransactionStatusMsg = HomeAccountMaintenanceController.OLD_PWD_NOT_CORRECT;
        }
        else
        {
            lsTransactionStatusMsg = "This request could not be completed. Please try again in a few minutes.";
        }
        aoResponse.setRenderParameter("targetResource", "updatenycsecurityquestion");
        aoRequest.getPortletSession().removeAttribute(HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN,
                PortletSession.APPLICATION_SCOPE);
        return lsTransactionStatusMsg;
    }

    /**
     * This method redirect user to provider home page
     * 
     * @param aoRequest getting ContextPath path from request
     * @param aoResponse setting response parameter for JSP variables
     * @param abSendRedirectFlag with boolean value, set to true then do
     *            sendRedirect
     * @return
     */
    private boolean redirectToProviderHomePage(ActionRequest aoRequest, ActionResponse aoResponse,
            boolean abSendRedirectFlag)
    {
        String lsUrl = aoRequest.getContextPath()
                + "/portal/hhsweb.portal?_nfpb=true&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_provider_home";
        try
        {
            abSendRedirectFlag = true;
            aoResponse.sendRedirect(lsUrl);
        }
        catch (IOException aoExp)
        {
            LOG_OBJECT.Error("Error occured while redirecting to provider home page", aoExp);
        }
        return abSendRedirectFlag;
    }

    /**
     * This method modified as a part of release 3.1.0 to fix NT037 issue
     * 
     * This method send notification to user for password change
     * <ul>
     * <li>Method Updated in R4</li>
     * </ul>
     * @param aoResponse setting response parameter for JSP variables
     * @param abSendRedirectFlag boolean value
     * @param aoPortletSession that retains values saved in portlet session
     * @param aoChannelObj Channel Object that needs to be set to send and
     *            receive values across transaction
     * @return Boolean status of send redirect flag (true)
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    private boolean sendNotificationForPwd(ActionRequest aoRequest, ActionResponse aoResponse,
            boolean abSendRedirectFlag, PortletSession aoPortletSession, Channel aoChannel) throws ApplicationException
    {
        String lsEmailAddress = (String) aoPortletSession.getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
                PortletSession.APPLICATION_SCOPE);
        String lsEmailLink = ApplicationConstants.HHS_INFO_ID;
        String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
                ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);

        String lsOrgId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
                PortletSession.APPLICATION_SCOPE);
        String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
                PortletSession.APPLICATION_SCOPE);

        List<String> loNotificationAlertList = new ArrayList<String>();
        loNotificationAlertList.add("NT037");

        List<String> loProviderList = new ArrayList<String>();
        loProviderList.add(lsOrgId);

        HashMap<String, String> loLinkMap = new HashMap<String, String>();
        loLinkMap.put("LINK", lsEmailLink);

        NotificationDataBean loNotificationDataBean = new NotificationDataBean();
        loNotificationDataBean.setLinkMap(loLinkMap);
        loNotificationDataBean.setProviderList(loProviderList);

        HashMap<Object, String> loParamMap = new HashMap<Object, String>();
        loParamMap.put("USERNAME", lsUserName);

        HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
        loNotificationMap.put("NT037", loNotificationDataBean);

        if (lsUserId != null)
        {
            loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsUserId);
            loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "userid");
            loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, lsUserId);
            loNotificationMap.put(HHSConstants.MODIFIED_BY, lsUserId);
        }
        else
        {
            loNotificationMap.put(ApplicationConstants.ENTITY_ID, lsEmailAddress);
            loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, "user_emai");
            loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
            loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
        }
        loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
        loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
        Channel loChannel = new Channel();
        loChannel.setData("loHmNotifyParam", loNotificationMap);
        // fix for NT037 issue - start .. aoChannel changed to loChannel
        TransactionManager.executeTransaction(loChannel, "insertNotificationDetail");
        // fix for NT037 issue - end
        abSendRedirectFlag = redirectToProviderHomePage(aoRequest, aoResponse, abSendRedirectFlag);
        return abSendRedirectFlag;
    }

    /**
     * This method persist user data after name and email update is made on the
     * screen
     * 
     * @param aoRequest to get screen parameters
     * @param aoResponse setting response parameter for JSP variables
     * @param asEmailupdated new email address
     * @param asFirstName first name of user
     * @param asLastName last name of user
     * @param asMiddleName middle name of user
     * @param asEmailAddress new email address of user
     * @param asEmailNameOld old email address of user
     * @param aoUserEmailUPdatedTokenMap fields with user details (name, email
     *            address)
     * @return String lsTransactionStatusMsg that needs to be display to user on
     *         screen
     */
    private String persistDataAfterNameEmailUpdate(ActionRequest aoRequest, ActionResponse aoResponse,
            boolean abEmailupdated, String asFirstName, String asLastName, String asMiddleName, String asEmailAddress,
            String asEmailNameOld, Map<String, String> aoUserEmailUPdatedTokenMap)
    {
        String lsTransactionStatusMsg;
        lsTransactionStatusMsg = HomeAccountMaintenanceController.ONLY_NAME_UPDATED;
        aoResponse.setRenderParameter("targetResource", "onlyNameUpdated");
        // setting values to persist
        aoUserEmailUPdatedTokenMap.put("FIRST_NAME", asFirstName);
        aoUserEmailUPdatedTokenMap.put("LAST_NAME", asLastName);
        aoUserEmailUPdatedTokenMap.put("MIDDLE_INITIAL", asMiddleName);
        // over-riding previous old values as the values saved will now become
        // old values
        ApplicationSession.setAttribute(asFirstName, aoRequest, "FirstName");
        ApplicationSession.setAttribute(asLastName, aoRequest, "LastName");
        ApplicationSession.setAttribute(asMiddleName, aoRequest, "MiddleName");

        // over-riding user's name in portlet session
        if (null != asFirstName && !asFirstName.trim().equalsIgnoreCase("") && null != asLastName
                && !asLastName.trim().equalsIgnoreCase(""))
        {
            aoRequest.getPortletSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
                    asFirstName + " " + asMiddleName + " " + asLastName, PortletSession.APPLICATION_SCOPE);
        }
        if (abEmailupdated)
        {
            aoUserEmailUPdatedTokenMap.put("EMAIL", asEmailAddress);
            ApplicationSession.setAttribute(asEmailAddress, aoRequest, "Email");
            lsTransactionStatusMsg = HomeAccountMaintenanceController.ONLY_NAME_UPDATED + " "
                    + HomeAccountMaintenanceController.TOKEN_GENERATED;
        }
        else
        {
            ApplicationSession.setAttribute(asEmailNameOld, aoRequest, "Email");
            aoUserEmailUPdatedTokenMap.put("EMAIL", asEmailNameOld);
        }
        ApplicationSession.setAttribute(aoUserEmailUPdatedTokenMap, aoRequest, "loMapEmailUpdated");
        return lsTransactionStatusMsg;
    }

    /**
     * This method populates map for name and email to retain old values once
     * user successfully updates values then it becomes old values also for next
     * update.
     * 
     * @param aoRequest to get screen parameters
     * @param aoUserMapFilledData with user details (name, email, user dn,
     *            etc..)
     * @param alsUserId staff id of user
     * @param asOrgId organization id of user
     * @param asUserDN user dn taken from session
     * @param asFirstName new first name of user
     * @param asLastName new last name of user
     * @param asMiddleName new middle name of user
     * @param asEmailAddress new email address of user
     * @param asFirstNameOld old first name of user
     * @param asLastNameOld old last name of user
     * @param asMiddleNameOld old middle name of user
     * @param asEmailNameOld old email id of user
     * @return Map<String, String> with user related fields(name, email, org id,
     *         etc..)
     */
    private Map<String, String> populateMapForNameAndEmail(ActionRequest aoRequest,
            Map<String, String> aoUserMapFilledData, String asUserId, String asOrgId, String asUserDN,
            String asFirstName, String asLastName, String asMiddleName, String asEmailAddress, String asFirstNameOld,
            String asLastNameOld, String asMiddleNameOld, String asEmailNameOld)
    {
        Map<String, String> loUserEmailUPdatedTokenMap = new HashMap<String, String>();

        // set map incase of no change
        loUserEmailUPdatedTokenMap.put("FIRST_NAME", asFirstNameOld);
        loUserEmailUPdatedTokenMap.put("LAST_NAME", asLastNameOld);
        loUserEmailUPdatedTokenMap.put("MIDDLE_INITIAL", asMiddleNameOld);
        loUserEmailUPdatedTokenMap.put("EMAIL", asEmailNameOld);
        ApplicationSession.setAttribute(loUserEmailUPdatedTokenMap, aoRequest, "loMapEmailUpdated");

        // populating channel object with current values user has
        // entered on screen
        aoUserMapFilledData.put("FirstName", asFirstName);
        aoUserMapFilledData.put("LastName", asLastName);
        aoUserMapFilledData.put("MiddleInitial", asMiddleName);
        aoUserMapFilledData.put("newEmailAddress", asEmailAddress);
        aoUserMapFilledData.put(
                "oldEmailAddress",
                (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,
                        PortletSession.APPLICATION_SCOPE));
        aoUserMapFilledData.put("STAFF_ID", asUserId);
        aoUserMapFilledData.put("ORGANIZATION_ID", asOrgId);
        aoUserMapFilledData.put("UserDN", asUserDN);
        return loUserEmailUPdatedTokenMap;
    }

    /**
     * This method calls when user wants to update security questions and
     * answers
     * 
     * @param aoRequest to get screen parameters
     * @param asPassword current password (required to make call to web service)
     * @param aoPortletSession that retains values saved in portlet session
     * @param asUserDN dn of the user corresponding to email id
     * @return Map<String, String> with fields related to questions, answers,
     *         current password
     * @throws NumberFormatException
     */
    private Map<String, String> populateDataForUpdateSecQues(ActionRequest aoRequest, String asPassword,
            PortletSession aoPortletSession, String asUserDN) throws NumberFormatException
    {
        RegisterNycIdBean loRegisterNycIdBean = (RegisterNycIdBean) aoPortletSession.getAttribute(
                HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN, PortletSession.APPLICATION_SCOPE);
        String lsSecurityAns1 = null;
        String lsSecurityAns2 = null;
        String lsSecurityAns3 = null;
        String lsSecurityQues1 = null;
        String lsSecurityQues2 = null;
        String lsSecurityQues3 = null;
        Map<String, String> loMapService = new HashMap<String, String>();
        // Following if conditions evaluates the answer and questions
        // provided by the user.
        if (null != aoRequest.getParameter("answer1"))
        {
            lsSecurityAns1 = aoRequest.getParameter("answer1");
        }
        if (null != aoRequest.getParameter("answer2"))
        {
            lsSecurityAns2 = aoRequest.getParameter("answer2");
        }
        if (null != aoRequest.getParameter("answer3"))
        {
            lsSecurityAns3 = aoRequest.getParameter("answer3");
        }

        if (null != aoRequest.getParameter("ques1Text"))
        {
            lsSecurityQues1 = aoRequest.getParameter("ques1Text");
        }
        if (null != aoRequest.getParameter("ques2Text"))
        {
            lsSecurityQues2 = aoRequest.getParameter("ques2Text");
        }
        if (null != aoRequest.getParameter("ques3Text"))
        {
            lsSecurityQues3 = aoRequest.getParameter("ques3Text");
        }
        loRegisterNycIdBean.setMsAnswer1(lsSecurityAns1);
        loRegisterNycIdBean.setMsAnswer2(lsSecurityAns2);
        loRegisterNycIdBean.setMsAnswer3(lsSecurityAns3);
        loRegisterNycIdBean.setMsQues1Text(lsSecurityQues1);
        loRegisterNycIdBean.setMsQues2Text(lsSecurityQues2);
        loRegisterNycIdBean.setMsQues3Text(lsSecurityQues3);
        loRegisterNycIdBean.setMiSecurityQuestion1Id(Integer.parseInt(aoRequest.getParameter("securityQuestion1")));
        loRegisterNycIdBean.setMiSecurityQuestion2Id(Integer.parseInt(aoRequest.getParameter("securityQuestion2")));
        loRegisterNycIdBean.setMiSecurityQuestion3Id(Integer.parseInt(aoRequest.getParameter("securityQuestion3")));
        aoRequest.getPortletSession().setAttribute(HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN,
                loRegisterNycIdBean, PortletSession.APPLICATION_SCOPE);

        if (null != aoRequest.getParameter("currentPassword"))
        {
            asPassword = aoRequest.getParameter("currentPassword");
        }

        loMapService.put("secAns1", lsSecurityAns1);
        loMapService.put("secAns2", lsSecurityAns2);
        loMapService.put("secAns3", lsSecurityAns3);

        loMapService.put("secQues1", lsSecurityQues1);
        loMapService.put("secQues2", lsSecurityQues2);
        loMapService.put("secQues3", lsSecurityQues3);
        loMapService.put("currentPassword", asPassword);
        loMapService.put("UserDN", asUserDN);
        return loMapService;
    }

    /**
     * This method gets list of security questions for the user on the screen
     * 
     * @param aoRequest to get screen parameters
     * @param aoResponse setting response parameter for JSP variables
     * @param aoChannelObj Channel Object to receive user values(questions)
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void getSecurityQuestion(RenderRequest aoRequest, RenderResponse aoResponse, Channel aoChannelObj)
            throws ApplicationException
    {
        List<SecurityQuestionBean> loQuestionList = null;
        RegisterNycIdBean loRegisterNycIdBean = null;
        TransactionManager.executeTransaction(aoChannelObj, "getSecurityQuestions");
        loQuestionList = (List) aoChannelObj.getData("losecurityQuestionList");
        if (null != aoRequest.getPortletSession().getAttribute(HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN,
                PortletSession.APPLICATION_SCOPE))
        {
            loRegisterNycIdBean = (RegisterNycIdBean) aoRequest.getPortletSession().getAttribute(
                    HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN, PortletSession.APPLICATION_SCOPE);
            loRegisterNycIdBean.setMoSecurityQuestion1List(loQuestionList);
        }
        else
        {
            loRegisterNycIdBean = new RegisterNycIdBean();
            loRegisterNycIdBean.setMoSecurityQuestion1List(loQuestionList);
        }
        aoRequest.getPortletSession().setAttribute(HomeAccountMaintenanceController.REGISTER_NYC_ID_BEAN,
                loRegisterNycIdBean, PortletSession.APPLICATION_SCOPE);
    }

    /**
     * This method gets map of user details -first name, initial and last name
     * 
     * @param asUserDn - unique user dn that fetches user details
     * @return Map loUserDetailRtrndMap containing first name, initial and last
     *         name
     * @throws ApplicationException
     */
    @SuppressWarnings(
    { "rawtypes" })
    private Map fetchUserDetails(String asUserDN) throws ApplicationException
    {
        Channel loChannel = new Channel();
        loChannel.setData("asUserDN", asUserDN);
        TransactionManager.executeTransaction(loChannel, "userDetailsSearch");
        Map loUserDetailRtrndMap = (Map) loChannel.getData("aoUserDetailRtrndMap");
        return loUserDetailRtrndMap;
    }
}
