package com.nyc.hhs.controllers;

import gov.nyc.saml.SAMLAttribute;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;
//import sun.misc.BASE64Decoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import weblogic.security.Security;

import com.bea.p13n.security.Authentication;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ActionStatusBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.security.SecurityTkn;
import com.nyc.hhs.security.impl.SAMLTknImpl;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.ActionStatusUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.CredentialVaultUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PortalUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This Controller is used to validate user at login and put required details in
 * session. It also manage access control.
 * 
 */

public class LoginController extends AbstractController {

    private static final LogInfo LOG_OBJECT                     = new LogInfo( LoginController.class);

    private static final String  REJECTED                       = "Rejected";
    private static final String  REQUEST_COULD_NOT_BE_COMPLETED = "This request could not be completed. Please try again in a few minutes.";
    private static final long    serialVersionUID               = 1L;
    private static final String  NOT_FOUND                      = "Not Found";
    private static final String  FOUND                          = "Found";
    private static final String  HOME_PAGE                      = "HomePage";
    private static final String  MISSING_PROFILE_INFO           = "missingProfileInfo";
    private static final String  TERMS_CONDITIONS               = "termsConditions";
    private static final String  PASSWORD_RESET_NOTIFICATION    = "A message with password reset instructions has been sent to your email address. ";
    private static final String  PASSWORD_RESET_NOTIFICATION_1  = "If you don't receive a message within a few minutes, ";
    private static final String  PASSWORD_RESET_NOTIFICATION_2  = "please check your email's spam and junk filters or try resending your request.";
    private static final String  NEW_TOKEN_GENERATED            = "Your activation link has expired."
                                                                        + " A new activation email with a validation link will be sent shortly to activate this account.";
    private static final String  NEW_GENERATED_TOKEN_SENT       = "You have clicked on old activation link."
                                                                        + " Please use new activation link to activate your account.";
    // fix done as a part of release 3.2.0 enhancement 6351
    private static final String  ACCOUNT_REQUEST_SUBMITTED      = "Organization Account Request Submitted."
                                                                        + "<br/>"
                                                                        + "<br/>"
                                                                        + "Thank you for requesting an HHS Accelerator Account."
                                                                        + "<br/>"
                                                                        + "<br/>"
                                                                        + "When a decision has been made regarding your account request, a notification will be sent to your email, #adminemail.";

    // +
    // ", and your organization's Executive Director/CEO or equivalent email, #ceoemail.";

    /**
     * This method is to render the next page depending on the action, login and
     * authentication process modified this method for log out issue Defect
     * #6432 fixed in release 3.1.3
     * 
     * @param aoRequest
     *            to get screen parameters and next page to be displayed
     * @param aoResponse
     *            setting response parameter for JSP variables
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
    protected ModelAndView handleRenderRequestInternal(RenderRequest aoRequest,   RenderResponse aoResponse) throws ApplicationException 
    {
    	LOG_OBJECT.Debug("handleRenderRequestInternal");
    	/*
        LOG_OBJECT.Debug("handleRenderRequestInternal aoRequest.getContextPath()"
                        + aoRequest.getContextPath()
                        + " : aoRequest.getScheme(): " + aoRequest.getScheme()
                        + " : aoRequest.getServerName(): "
                        + aoRequest.getServerName()
                        + " : aoRequest.getServerPort(:) "
                        + aoRequest.getServerPort()
                        + " : aoRequest.getAttribute(errorMsgConcurrent)  " +
                        aoRequest.getAttribute("errorMsgConcurrent"));
        */
        String tstMsgConcurrent = (String) aoRequest.getAttribute("errorMsg");
        
        long loStartTime = System.currentTimeMillis();

        // Putting Castor mapping file in cache
        if (!BaseCacheManagerWeb.getInstance().isPresent( HHSConstants.CASTER_CONFIGURATION_PATH)) 
        {
            PortletContext loContext = aoRequest.getPortletSession(true).getPortletContext();
            String lsCastorPath = loContext.getRealPath(HHSConstants.CASTOR_XML_PATH);
            BaseCacheManagerWeb.getInstance().putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
            
        }
        
        PortletContext loContext1 = aoRequest.getPortletSession(true).getPortletContext();
        String lsCastorPath222 = loContext1.getRealPath(HHSConstants.CASTOR_XML_PATH);
    	LOG_OBJECT.Debug("loContext1.getRealPath(HHSConstants.CASTOR_XML_PATH)");
    	LOG_OBJECT.Debug(lsCastorPath222);


        String lsLoginPagePath = "loginportlet";
        PortletSession loSession = aoRequest.getPortletSession(true);
        removeSessionVariables(loSession);

        HttpServletRequest loHttpRequest = (HttpServletRequest) PortalUtil.getServletRequest(aoRequest);
                
        P8UserSession loUserSession = setP8SessionVariables();
        setSystemProperty();
        String lsToken = (String) loHttpRequest.getHeader("Cookie");
        String lsHhsOrigin = (String) loHttpRequest.getHeader("hhsOrigin");
        // R 7.8.0 SAML logout
        String testMsg1 = (String) loHttpRequest.getAttribute("errorMsg");
        String testMsgConcurrent = (String) loHttpRequest.getAttribute("errorMsgConcurrent");
        LOG_OBJECT.Debug("hhsOrigin ::: " + lsHhsOrigin);
        LOG_OBJECT.Debug("lsToken ::: " + lsToken);

        

        //Set up message
        LOG_OBJECT.Debug("LOCAL ENV :: "+PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "PROPERTY_LOGIN_ENVIRONMENT"));

        if (!ApplicationConstants.LOCAL_ENVIRONMENT.equalsIgnoreCase(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "PROPERTY_LOGIN_ENVIRONMENT"))) 
        {
            if (null != lsHhsOrigin && lsHhsOrigin.contains("DMZ")) 
            {
                lsLoginPagePath = "loginportlet";
            } 
            else if (null != lsHhsOrigin && lsHhsOrigin.contains("CITY")&& !"logout".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "logout"))) 
            {
                lsLoginPagePath = "redirecthome";
                /****** Start QC 9205 R 8.0.0 SAML Internal */
                
                if (null!=aoRequest.getParameter("errorMsg") && !aoRequest.getParameter("errorMsg").isEmpty() )
                {
                	lsLoginPagePath = "loginportlet";
                	aoRequest.setAttribute("user_type", "");
                }
                //LOG_OBJECT.Debug("163===lsLoginPagePath :: "+lsLoginPagePath);
              
                /*
                if (lsToken != null && lsToken.contains("SMSESSION")) 
                {
                    try {
                        fetchSiteminderUserDetails(aoRequest, loSession,  loHttpRequest, loUserSession);
                    } catch (ApplicationException aoExp) {
                        aoRequest.setAttribute("user_type", "");
                        aoRequest.setAttribute("siteminderLogout",  "siteminderLogout");
                        aoRequest.setAttribute("siteminderLoginError", "siteminderLoginError");
                    } catch (Exception aoExp) {
                        aoRequest.setAttribute("user_type", "");
                        aoRequest.setAttribute("siteminderLogout",   "siteminderLogout");
                        aoRequest.setAttribute("siteminderLoginError", "siteminderLoginError");
                    }
                } 
                else 
                {
                    aoRequest.setAttribute("user_type", "");
                    aoRequest.setAttribute("siteminderLogout", "siteminderLogout");
                    aoRequest.setAttribute("siteminderLoginError", "siteminderLoginError");
                }
                */
                
                            
                /****** End QC 9205 R 8.0.0 SAML Internal */
            }
        }
        
        String lsErrorMsg = aoRequest.getParameter("errorMsg");
        String lsSuccessMsg = aoRequest.getParameter("successMsg");
        
        try {
            if ("validEmailToken".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "validEmailToken"))) 
            {
                Map loValidateEmailTokenServiceOutMap = (Map) validateEmailToken(aoRequest);
                String lsStatus = (String) loValidateEmailTokenServiceOutMap.get("serviceStatus");
                List loOutputList = (List) loValidateEmailTokenServiceOutMap.get("serviceOutput");
                LOG_OBJECT.Debug("loValidateEmailTokenServiceOutMap after validating EmailToken ::: "  + loValidateEmailTokenServiceOutMap);
                if ("error".equalsIgnoreCase(lsStatus)) 
                {
                    if (((String) loOutputList.get(0)).contains("Email Token Validation Failed because Invalid Token - Stored Token: null Received Token")) 
                    {
                        lsErrorMsg = "Your account is already activated";
                    } 
                    else if (((String) loOutputList.get(0)).contains("invalid Token Time - Token Expiration Time:")) 
                    {
                        // generate new token
                        String lsUserFirstName = "";
                        String lsUserMiddleName = "", lsUserLastName = "", lsUserEmailId = "";
                        String lsNewToken = "";

                        // start check flag if nyc activation flag is activated
                        lsUserEmailId = (String) PortalUtil.parseQueryString(aoRequest, "emailAddress");
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

                        Map loNewTokenMap = generateNewToken(lsUserEmailId);
                        lsNewToken = (String) loNewTokenMap.get("newToken");
                        lsStatus = (String) loNewTokenMap.get("transactionStatus");
                        lsErrorMsg = (String) loNewTokenMap.get("transactionMessage");

                        if ("passed".equalsIgnoreCase(lsStatus)) 
                        {
                            RegisterNycIdBean loRegisterNycIdBean = new RegisterNycIdBean();
                            loRegisterNycIdBean.setMsFirstName(lsUserFirstName);
                            loRegisterNycIdBean.setMsMiddleName(lsUserMiddleName);
                            loRegisterNycIdBean.setMsLastName(lsUserLastName);
                            loChannel = new Channel();
                            sendNT034Notification(aoRequest, loChannel, loRegisterNycIdBean, lsNewToken, lsUserEmailId);
                        } 
                        else if ("failed".equalsIgnoreCase(lsStatus)) 
                        {
                            lsErrorMsg = (String) loNewTokenMap.get("transactionMessage");
                        }
                    } 
                    else if (((String) loOutputList.get(0)).contains("Email Token Validation Failed because Invalid Token - Stored Token")
                            && !(((String) loOutputList.get(0)).contains(HHSConstants.NULL))) 
                    {
                        lsErrorMsg = LoginController.NEW_GENERATED_TOKEN_SENT;
                    } else {
                        lsErrorMsg = (String) loOutputList.get(0);
                    }
                } else {
                    lsSuccessMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M15");
                }
            } else if ("tokenvalidationafteremailchanged".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "tokenvalidationafteremailchanged"))) 
            {
                Map loValidateEmailTokenServiceOutMap = (Map) tokenvalidationafteremailchanged(aoRequest);
                String lsStatus = (String) loValidateEmailTokenServiceOutMap.get("serviceStatus");
                List loOutputList = (List) loValidateEmailTokenServiceOutMap.get("serviceOutput");

                if ("error".equalsIgnoreCase(lsStatus)) {
                    lsErrorMsg = (String) loOutputList.get(0);
                } else {
                    lsSuccessMsg = "Your email has been successfully updated.";
                }
            } else if ("resetPassword".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "resetPassword"))) 
            {
                lsSuccessMsg = PropertyLoader.getProperty(P8Constants.ERROR_PROPERTY_FILE, "M16");
                aoRequest.setAttribute("userActivated", lsSuccessMsg);
            } else if ("pwdResetNotification".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "pwdResetNotification"))) {
                lsSuccessMsg = LoginController.PASSWORD_RESET_NOTIFICATION
                        + PASSWORD_RESET_NOTIFICATION_1
                        + PASSWORD_RESET_NOTIFICATION_2;
                aoRequest.setAttribute("userActivated", lsSuccessMsg);
            } else if ("siteminderLogout".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest, "siteminderLogout"))) 
            {
                //** Start QC 9205 R 8.0.0 SAML Internal - in case of user validation error - redirect to loginportlet error page
            	//lsLoginPagePath = "redirecthome"; 
            	
                aoRequest.setAttribute("user_type", "");
                aoRequest.setAttribute("siteminderLogout", "siteminderLogout");
                aoRequest.setAttribute("siteminderLoginError", "");
               //** End  QC 9205 R 8.0.0 SAML Internal - in case of user validation error - redirect to loginportlet error page
            }
            if ("error".equalsIgnoreCase(PortalUtil.parseQueryString(aoRequest,
                    "error"))) {
                lsErrorMsg = "An error has occurred, Please try again.";
            }
            
            String lsOrgRequestSubmittedSuccessMsg = setParameterInSession(aoRequest);
            
            if (null != lsOrgRequestSubmittedSuccessMsg  && !lsOrgRequestSubmittedSuccessMsg.isEmpty()) 
            {
                aoRequest.setAttribute("userActivated",   lsOrgRequestSubmittedSuccessMsg);
                LOG_OBJECT.Debug("3592==aoRequest.setAttribute(userActivated, lsOrgRequestSubmittedSuccessMsg) ::  "+lsOrgRequestSubmittedSuccessMsg);
                // made changes for log out issue Defect #6432 fixed in release
                // 3.1.3
                Map<String, String> loDataMap = (Map<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject("sessionUserDetailsCache");
                //LOG_OBJECT.Debug("362==sessionUserDetailsCache ::  "+loDataMap);
                if (loDataMap != null) 
                {   
                    if (loDataMap.containsKey((String) loSession.getAttribute(ApplicationConstants.USER_DN, PortletSession.APPLICATION_SCOPE))) 
                    {
                        loDataMap.remove((String) loSession.getAttribute( ApplicationConstants.USER_DN,  PortletSession.APPLICATION_SCOPE));
                        BaseCacheManagerWeb.getInstance().putCacheObject( "sessionUserDetailsCache", loDataMap);
                        //LOG_OBJECT.Debug("370==sessionUserDetailsCache :: after remove USER_DN "+loDataMap);
                    }
                }
            }
        } catch (ApplicationException aoFbAppEx) {
            if (aoFbAppEx.toString().contains(
                            "Email Update Token Validation Failed because Email Update Token Validation Service Email not Found:")) {
                lsErrorMsg = "Your email has already been successfully updated.";
            } else if (aoFbAppEx.toString().contains(
                            "Email Update Token Validation Failed because Invalid Token - Stored Token:")) {
                lsErrorMsg = "Your email update validation link is invalid or has expired.";
            }

            LOG_OBJECT.Error("Error occured in Login page  ", aoFbAppEx);
        } catch (Exception aoFbAppEx) {
            LOG_OBJECT.Error("Error occured in Login page ", aoFbAppEx);
        }
        /*
         * check if any error message received redirect to login page with
         * relevant error message
         */
        if (lsErrorMsg != null && !lsErrorMsg.isEmpty()) 
        {
            aoRequest.setAttribute("errorMsg", lsErrorMsg);
            
        } 
        else if (lsSuccessMsg != null && !lsSuccessMsg.isEmpty()) 
        {
            aoRequest.setAttribute("userActivated", lsSuccessMsg);
        }
        
      
        ModelAndView loModelAndView = new ModelAndView(lsLoginPagePath);

        long loEndTimeTime = System.currentTimeMillis();
        try {
            LOG_OBJECT.Debug("TIME TAKEN for execution of render Method in LoginController = " + (loEndTimeTime - loStartTime));
        } catch (ApplicationException aoEx) {
            LOG_OBJECT.Error("Error while execution of render Method in LoginController ",  aoEx);
        }
        UserThreadLocal.unSet();
        return loModelAndView;
    }

    /**
     * This method is for setting parameters in session database
     * 
     * @param aoRequest
     *            to get screen parameters (email id of admin and ceo)
     * @return
     * @throws String
     *             success message to be displayed on the login page
     */
    private String setParameterInSession(RenderRequest aoRequest) {
        String lsSuccessMsg = ACCOUNT_REQUEST_SUBMITTED;
        final String lsAdminEmailId = PortalUtil.parseQueryString(aoRequest, "adminEmail");
        final String lsCeoEmailId = PortalUtil.parseQueryString(aoRequest, "ceoEmail");
        if (null != lsAdminEmailId && null != lsCeoEmailId) {
            lsSuccessMsg = lsSuccessMsg.replace("#adminemail", lsAdminEmailId);
            lsSuccessMsg = lsSuccessMsg.replace("#ceoemail", lsCeoEmailId);
            return lsSuccessMsg;
        } else {
            return "";
        }
    }

    /**
     * This method is for fetching/inserting CITY and AGENCY user's data from
     * database <li>This method was updated in R4</li>
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param aoHttpRequest
     * @param aoUserSession
     *            is the P8UserSession object
     * @return
     * @throws ApplicationException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void fetchSiteminderUserDetails(RenderRequest aoRequest, PortletSession aoSession, HttpServletRequest aoHttpRequest, P8UserSession aoUserSession) throws ApplicationException {
        javax.security.auth.Subject lsSubject = weblogic.security.Security.getCurrentSubject();
        Set<Principal> loAllPrincipals = lsSubject.getPrincipals();
        List loPrincipallist = new ArrayList();
        for (Principal loPrincipal : loAllPrincipals) 
        {
            loPrincipallist.add(loPrincipal.getName());
        }
        String lsLoginUser = (String) loPrincipallist.get(0), lsRole = (String) loPrincipallist.get(1);
        aoUserSession.setSubject(lsSubject);
        LOG_OBJECT.Debug("siteminder subject::: " + lsSubject);
        String lsCmUserDN = (String) aoHttpRequest.getHeader("SM_USERDN");
        String lsCmUserFirstName = (String) aoHttpRequest.getHeader("SMFN");
        String lsCmUserLastName = (String) aoHttpRequest.getHeader("SMLN");
        String lsCmUserEmailAddress = (String) aoHttpRequest.getHeader("SMEMAIL");
        lsCmUserEmailAddress = lsCmUserEmailAddress.replaceAll("\\s", "");
        UserBean loUserBean = new UserBean();

        // City user changes start
        if (null != lsCmUserFirstName && null != lsCmUserLastName) 
        {
            loUserBean.setMsUserName(lsCmUserFirstName.concat(" ").concat(
                    lsCmUserLastName));
        }
        loUserBean.setMsUserDN(lsCmUserDN);
        loUserBean.setMsUserEmail(lsCmUserEmailAddress);
        Map loSiteMinderUserDetailMap = null;
        Map loSiteMinderUserDetailUpdateMap = null;
        Channel loChannel = new Channel();
        loChannel.setData("asCmUserDN", lsCmUserDN);
        TransactionManager.executeTransaction(loChannel, "searchUserDnInUserDetails");
        loSiteMinderUserDetailMap = (Map) loChannel.getData("aoSiteMinderUserDetailMap");

        if (lsRole.toLowerCase().contains(ApplicationConstants.AGENCY)) {
            updateUserBeanAgency(lsRole, loUserBean);
        } else {
            updateUserbeanCity(lsRole, loUserBean);
        }

        String lsCmUserRole = loUserBean.getMsRole();
        String lsCmUserType = loUserBean.getMsOrgType();

        // log
        if (null != loSiteMinderUserDetailMap
                && !loSiteMinderUserDetailMap.isEmpty()) {
            String lsFirstNameFromDB = (String) loSiteMinderUserDetailMap.get("FIRST_NAME");
            String lsLastNameFromDB = (String) loSiteMinderUserDetailMap.get("LAST_NAME");
            String lsEmailFromDB = (String) loSiteMinderUserDetailMap.get("EMAIL_ID");
            String lsUserIdFromDB = (String) loSiteMinderUserDetailMap.get("USER_ID");
            String lsUserRoleFromDB = (String) loSiteMinderUserDetailMap.get("USER_ROLE");
            String lsUserTypeFromDB = (String) loSiteMinderUserDetailMap.get("USER_TYPE");
            String lsOrganizationFromDB = (String) loSiteMinderUserDetailMap.get("ORGANIZATION_NAME");
            String lsActiveFlagFromDB = (String) loSiteMinderUserDetailMap.get("ACTIVE_FLAG");
            if (lsCmUserEmailAddress != null
                    && lsLoginUser != null
                    && lsCmUserEmailAddress.trim().equalsIgnoreCase(lsLoginUser.trim())) 
            {
                LOG_OBJECT.Debug("User DN found in Database for user :::  "
                                + lsCmUserEmailAddress + " and user dn:: "
                                + lsCmUserDN);
                // update into SiteMinder_User_Details
                loSiteMinderUserDetailUpdateMap = new HashMap();
                loSiteMinderUserDetailUpdateMap.put("FirstName", lsCmUserFirstName);
                loSiteMinderUserDetailUpdateMap.put("LastName", lsCmUserLastName);
                loSiteMinderUserDetailUpdateMap.put("EmailAddress", lsCmUserEmailAddress);
                loSiteMinderUserDetailUpdateMap.put("UserRole", lsCmUserRole);
                loSiteMinderUserDetailUpdateMap.put("UserType", lsCmUserType);
                loSiteMinderUserDetailUpdateMap.put("UserDN", lsCmUserDN);
                if (null != lsCmUserType  && lsCmUserType.toLowerCase().contains("agency")) 
                {
                    loSiteMinderUserDetailUpdateMap.put("OrgName", loUserBean.getMsOrgId());
                } else {
                    loSiteMinderUserDetailUpdateMap.put("OrgName", loUserBean.getMsOrgName());
                }
                loSiteMinderUserDetailUpdateMap.put("ModifiedBy",  lsUserIdFromDB);
                if (null != lsCmUserFirstName
                        && null != lsCmUserLastName
                        && null != lsCmUserRole
                        && null != lsCmUserType
                        && (!(lsCmUserFirstName.equalsIgnoreCase(lsFirstNameFromDB)
                                && lsCmUserLastName.equalsIgnoreCase(lsLastNameFromDB)
                                && lsCmUserEmailAddress.equalsIgnoreCase(lsEmailFromDB)
                                && lsCmUserRole.equalsIgnoreCase(lsUserRoleFromDB) 
                                && lsCmUserType.equalsIgnoreCase(lsUserTypeFromDB)) 
                                || ApplicationConstants.ZERO.equalsIgnoreCase(lsActiveFlagFromDB))) 
                {
                    LOG_OBJECT.Debug("Updating city user details table for user ... "
                                    + lsCmUserEmailAddress
                                    + " and user dn:: "
                                    + lsCmUserDN);
                    loChannel = new Channel();
                    loChannel.setData("aoCmUserDetailMap", loSiteMinderUserDetailUpdateMap);
                    TransactionManager.executeTransaction(loChannel, "updateRoleInCityUserDetails");
                    LOG_OBJECT.Debug("Successfully Updated city user details table for user ... "
                                    + lsCmUserEmailAddress
                                    + " and user dn:: "
                                    + lsCmUserDN);
                }

            } else {
                LOG_OBJECT.Debug("Logged in user email and email retrieved from siteminder does not match for user :: "
                                + lsCmUserEmailAddress
                                + " and user dn:: "
                                + lsCmUserDN);
                if (lsUserTypeFromDB.toLowerCase().contains("agency")) {
                    loUserBean.setMsRole(lsUserRoleFromDB);
                    loUserBean.setMsOrgId(lsOrganizationFromDB);
                    loUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName(
                                            (TreeSet) BaseCacheManagerWeb.getInstance().getCacheObject( ApplicationConstants.AGENCY_LIST), lsOrganizationFromDB));
                    loUserBean.setMsOrgType(ApplicationConstants.AGENCY_ORG);
                } else {
                    loUserBean.setMsOrgName(ApplicationConstants.CITY);
                    loUserBean.setMsOrgId(ApplicationConstants.CITY);
                    loUserBean.setMsRole(lsUserRoleFromDB);
                    loUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);

                }
            }
            loUserBean.setMsUserId(lsUserIdFromDB);
            // City user update changes end

        } else {

            if (lsCmUserEmailAddress != null
                    && lsLoginUser != null
                    && !lsCmUserEmailAddress.trim().equalsIgnoreCase(lsLoginUser.trim())) 
            {
                LOG_OBJECT.Error("Can not insert siteminder details into database due to cookies issue. Cookies need to be clean for login user:: "
                                + lsCmUserEmailAddress
                                + " and email fetch from subject:: "
                                + lsLoginUser);
                throw new ApplicationException(
                        "Can not insert siteminder details into database due to cokkie issue. Cookies need to be clean for login user:: "
                                + lsCmUserEmailAddress
                                + " and email fetch from subject:: "
                                + lsLoginUser);
            }
            LOG_OBJECT.Debug("Inserting records in city user details table for user ... "
                            + lsCmUserEmailAddress
                            + " and user dn:: "
                            + lsCmUserDN);
            // insert into SiteMinder_User_Details
            loSiteMinderUserDetailMap = new HashMap();
            loSiteMinderUserDetailMap.put("FirstName", lsCmUserFirstName);
            loSiteMinderUserDetailMap.put("LastName", lsCmUserLastName);
            loSiteMinderUserDetailMap.put("EmailAddress", lsCmUserEmailAddress);
            loSiteMinderUserDetailMap.put("UserDN", lsCmUserDN);
            loSiteMinderUserDetailMap.put("UserRole", lsCmUserRole);
            loSiteMinderUserDetailMap.put("UserType", lsCmUserType);
            if (lsCmUserType.toLowerCase().contains("agency")) {
                loSiteMinderUserDetailMap.put("OrgName", loUserBean.getMsOrgId());
            } else {
                loSiteMinderUserDetailMap.put("OrgName", loUserBean.getMsOrgName());
            }
            loChannel = new Channel();
            String lsUserId = "";
            loChannel.setData("aoCmUserDetailMap", loSiteMinderUserDetailMap);
            TransactionManager.executeTransaction(loChannel, "insertIntoSiteMinderUserDetails");
            LOG_OBJECT.Debug("Successfully Inserted records in city user details table for user ... "
                            + lsCmUserEmailAddress
                            + " and user dn:: "
                            + lsCmUserDN);
            int liUserId = (Integer) loChannel.getData("liCurrentSeq");
            if (lsCmUserType.toLowerCase().contains("agency")) 
            {
                lsUserId = "agency_" + String.valueOf(liUserId);
            } 
            else 
            {
                lsUserId = "city_" + String.valueOf(liUserId);
            }
            LOG_OBJECT.Debug("lsUserId: " + lsUserId);
            loUserBean.setMsUserId(lsUserId);
        }
        // City user changes end
        aoRequest.setAttribute("user_type", loUserBean.getMsOrgType());
        LOG_OBJECT.Debug("user_type: " + loUserBean.getMsOrgType());

        HashMap<String, String> loUserHashMap = fetchCityUserDetails();
        loChannel = new Channel();
        StringBuilder loSBRole = new StringBuilder(), loSBOrg = new StringBuilder(), loSBOrgType = new StringBuilder(), loSBUserId = new StringBuilder(), loSBOrgName = new StringBuilder();
        loSBUserId.append(loUserBean.getMsUserId());
        loSBRole.append(loUserBean.getMsRole());
        loSBOrg.append(loUserBean.getMsOrgId());
        loSBOrgType.append(loUserBean.getMsOrgType());
        loSBOrgName.append(loUserBean.getMsOrgName());
        loUserBean.setMsLoginId(lsLoginUser);
        String oversightFlag = "";
        if (null != loUserBean.getMsOrgType()
                && ("agency_org".equalsIgnoreCase(loUserBean.getMsOrgType()) 
                    || "city_org".equalsIgnoreCase(loUserBean.getMsOrgType()))) 
        {
            LOG_OBJECT.Debug("UserID: " + loSBUserId.toString());
            // QC 8914 R 7.2.0 - read only role - get oversight flag
            oversightFlag = fetchCityAgencyUserOversightFlag(loSBUserId.toString());
            LOG_OBJECT.Debug("OversigtFlag: " + oversightFlag);
            aoRequest.setAttribute("user_type_original",  loUserBean.getMsOrgType());
        }
        LOG_OBJECT.Debug("lsUserId: " + loUserBean.getMsUserId());
        LOG_OBJECT.Debug("loSBRole: " + loUserBean.getMsRole());
        LOG_OBJECT.Debug("loSBOrg: " + loSBOrg);
        LOG_OBJECT.Debug("loSBOrgType: " + loSBOrgType);
        LOG_OBJECT.Debug("user_type: " + loUserBean.getMsOrgType());

        // QC 8914 R 7.2.0 - read only role - saved agency user in request and
        // session variables if oversight flag is 1

        settingSessionVariables(aoSession, loUserBean.getMsUserEmail(),
                loSBRole, loSBOrg, loSBOrgType, loSBUserId, loSBOrgName,
                loUserHashMap, loUserBean.getMsUserDN(), oversightFlag,
                loUserBean, loChannel, aoUserSession);

        createRoleMappingMap(aoSession, loUserBean);
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_VALIDATED,
                ApplicationConstants.TRUE, PortletSession.APPLICATION_SCOPE);
    }

    /**
     * This method update the UsderBean for city <li>This method was updated in
     * R4</li>
     * 
     * @param asRole
     *            Principle of the subject
     * @param aoUserBean
     *            Bean containing user details
     */
    private void updateUserbeanCity(String asRole, UserBean aoUserBean) throws ApplicationException 
    {
    	LOG_OBJECT.Debug("saml role::: " + asRole);
    	aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
        aoUserBean.setMsOrgName(ApplicationConstants.CITY);
        aoUserBean.setMsOrgId(asRole);
        if (asRole.toLowerCase().contains("staff")) {
            aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
        } else if (asRole.toLowerCase().contains("manager")) {
            aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
        } else {
            aoUserBean.setMsRole(ApplicationConstants.ROLE_EXECUTIVE);
        }
        LOG_OBJECT.Debug("616===UserBean ::: " + aoUserBean.toString());
    }

    /**
     * This method update the UsderBean for Agency
     * 
     * @param asRole
     *            Priciple of the subject
     * @param aoUserBean
     *            Bean containing user details
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
    private void updateUserBeanAgency(String asRole, UserBean aoUserBean) throws ApplicationException 
    {
        LOG_OBJECT.Debug("saml role::: " + asRole);
        String lsSplitName[] = asRole.split("_");
        aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
        if (null != lsSplitName && lsSplitName.length > 3)
        {
            aoUserBean.setMsOrgId(lsSplitName[3].trim().toUpperCase());
            if (lsSplitName.length > 4) 
            {
                String lsRoleString = lsSplitName[4].toLowerCase().trim();
                if (lsRoleString.indexOf(".") != -1) 
                {
                    lsRoleString = lsRoleString.substring(0,lsRoleString.indexOf("."));
                }
                String loAgencyRole = ApplicationConstants.ROLE_AGENCY.get(lsRoleString.toLowerCase().trim());
                LOG_OBJECT.Debug("Agency role assigned ::: " + loAgencyRole);
                if (null != loAgencyRole) {
                    aoUserBean.setMsRole(loAgencyRole);
                }
            }
        } 
        else 
        {
            aoUserBean.setMsOrgId(lsSplitName[2].trim());
        }

        aoUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName(
                (TreeSet) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.AGENCY_LIST), aoUserBean.getMsOrgId()));
        aoUserBean.setMsOrgType(ApplicationConstants.AGENCY_ORG);
        LOG_OBJECT.Debug("658===UserBean ::: " + aoUserBean.toString());
    }

    /**
     * This method decide the execution flow for login and authentication
     * process <li>This method was updated in R4</li> modified this method for
     * log out issue Defect #6432 fixed in release 3.1.3
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
    protected void handleActionRequestInternal(ActionRequest aoRequest, ActionResponse aoResponse) throws ApplicationException 
    {
        LOG_OBJECT.Debug("handleActionRequestInternal aoRequest");
        /*
                        + aoRequest.getContextPath()
                        + " aoRequest.getScheme() " + aoRequest.getScheme()
                        + " aoRequest.getServerName() "
                        + aoRequest.getServerName()
                        + " aoRequest.getServerPort()"
                        + aoRequest.getServerPort());
         */
     // Start QC 9205 R 8.00. SAML Internal
        String loHhsOrigin = (String) ((HttpServletRequest) PortalUtil.getServletRequest(aoRequest)).getAttribute("hhsOrigin");
         
     // End QC 9205 R 8.00. SAML Internal
        long loStartTime = System.currentTimeMillis();
        String lsLoginEnvironment = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "PROPERTY_LOGIN_ENVIRONMENT");
        setSystemProperty();
        PortletSession loSession = aoRequest.getPortletSession(true);

        /** [Start] QC 9165 R 7.8.0 */
        setSamlReqParameter((HttpServletRequest) PortalUtil.getServletRequest(aoRequest));
        
        SAMLTknImpl loSamlAttr = new SAMLTknImpl(extractAttributesFromSecurity(Security.getCurrentSubject()));
        LOG_OBJECT.Debug("SAMLTknImpl loSamlAttr :: "+loSamlAttr.toString());
        // Validate user attribute
        //String lsHhsOrigin = (String) loHttpRequest.getHeader("hhsOrigin");
        UserBean loUserBeanCity = new UserBean();
        boolean validUser = validateUserProfile(loSamlAttr, aoRequest, aoResponse, loHhsOrigin, loUserBeanCity);
        //if (!validateUserProfile(loSamlAttr, aoResponse, loHhsOrigin, loUserBeanCity)) 
       	if (!validUser)
        {   
       		// End QC 9205 R 8.00. SAML Internal
        	LOG_OBJECT.Debug("validateUserProfile is false");
        	UserThreadLocal.unSet();
            return;
        }
       	
        //LOG_OBJECT.Debug("707=========validateUserProfile is true");
        loSession.setAttribute(ApplicationConstants.SAML_ATTRIBUTE_FOR_NAME, loSamlAttr, PortletSession.APPLICATION_SCOPE);
        /** [End] QC 9165 R 7.8.0 */

        /*[Start] R9.6.4 QC9701 */
        Channel loActionChannel = new Channel();
        TransactionManager.executeTransaction(loActionChannel, "fetchActionStatusMap");
        List<ActionStatusBean> loActionStatuslst = (List<ActionStatusBean>) loActionChannel.getData("allActionStatusLst");
        if(loActionStatuslst != null)     LOG_OBJECT.Debug("@@@@@#####"+ loActionStatuslst.size() );
        else    LOG_OBJECT.Debug("@@@@@#####loActionStatuslst is null \n" );
        ActionStatusUtil.setMoActionLst(loActionStatuslst);
        /*[End] R9.6.4 QC9701 */

        String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.NEXT_ACTION);
        //LOG_OBJECT.Debug("721 ===##################  [handleActionRequestInternal]  lsAction:" + lsAction);
        //LOG_OBJECT.Debug("721 ==========removeSessionVariables========");
        removeSessionVariables(loSession);
        boolean lbRedirectFlag = false;
        boolean lbSelectOrgScreenFlag = false;
        try {
            if (lsAction != null && lsAction.equals("validateUser")) {

                /** [Start] QC 9165 R 7.8.0 */
                Map<String, String> loAttrMap = extractAttributesFromSecurity(Security.getCurrentSubject());
                //LOG_OBJECT.Debug("720===extractAttributesFromSecurity==loAttrMap:  " + loAttrMap);
                String lsSamlEmail = loAttrMap.get(ApplicationConstants.SAML_ATTR_EMAIL_KEY);
                String lsSamlGUID = loAttrMap.get(ApplicationConstants.SAML_ATTR_GUID_KEY);
                String lsEmail = lsSamlEmail;
                String lsPassword = lsSamlGUID;
                // [Start] QC 9205 R 8.00. SAML Internal
                String lsSamlUserDN = loAttrMap.get(ApplicationConstants.SAML_ATTR_ENTRY_DN);
                /* not needed anymore - email attribute has the same name for Internal and External SAML
                if(ApplicationConstants.CITY.equalsIgnoreCase(loHhsOrigin) || lsSamlUserDN != null)
                {
                	lsSamlEmail = loAttrMap.get(ApplicationConstants.SAML_ATTR_EMAIL);
                }
                */
                //[End]  QC 9205 R 8.00. SAML Internal
                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  Validating User:" + lsSamlEmail + "<>" + lsSamlGUID);
                /** [End] QC 9165 R 7.8.0 */

                boolean lbAuthenticate = false, 
                		lbMissingControllerFlag = false, 
                		lbLocalFlow = false, 
                		lbLdapUnavailable = false,                		
                		lsUserActiveStatusFlag =false;
                StringBuilder loSBRole = new StringBuilder(), 
                			  loSBOrg = new StringBuilder(), 
                			  loSBOrgType = new StringBuilder(), 
                			  loSBUserId = new StringBuilder(), 
                			  loSBOrgName = new StringBuilder();
                HashMap<String, String> loUserHashMap = fetchCityUserDetails();
               
                UserBean loUserBean = new UserBean();
                Channel loChannel = new Channel();
                P8UserSession loUserSession = setP8SessionVariables();
                //LOG_OBJECT.Debug("758==handleActionRequestInternal==Login ENVIRONMENT: " + lsLoginEnvironment);
                if (ApplicationConstants.LOCAL_ENVIRONMENT.equalsIgnoreCase(lsLoginEnvironment)) 
                {
                    lbAuthenticate = (boolean) authenticateUser(lsEmail,  lsPassword);
                    if (lbAuthenticate) 
                    {
                        lbLocalFlow = true;

                        loUserBean = fillAuthenticationDetail(lsEmail,
                                lsPassword, loSBRole, loSBOrg, loSBOrgType,
                                loSBUserId, loSBOrgName);

                    }
                } 
                else if (ApplicationConstants.PROVIDER_LDAP_ENVIRONMENT.equalsIgnoreCase(lsLoginEnvironment)) 
                {
                    /** [Start] QC 9165 R 7.8.0 */
                    //LOG_OBJECT.Debug("767====%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  :"  + lsSamlEmail + "<>" + lsSamlGUID);
                    /*
                     * try { //Authentication.authenticate(lsEmail, lsPassword);
                     */
                    // Provider
                    //LOG_OBJECT.Debug("758====lsSamlUserDN :"  + lsSamlUserDN);
                    loUserBean.setMsUserDN(lsSamlGUID);
                    //[Start] QC 9205  R 8.0.0 SAML Internal
                    if (ApplicationConstants.CITY.equalsIgnoreCase(loHhsOrigin) && lsSamlUserDN!=null && !lsSamlUserDN.isEmpty())
                    {
                    	loUserBean.setMsUserDN(lsSamlUserDN);
                    }
                    //[End] QC 9205  R 8.0.0 SAML Internal
                    // City
                    LOG_OBJECT.Debug("loSBOrgType :: " + loSBOrgType);
                    LOG_OBJECT.Debug("lsSamlEmail :: " + lsSamlEmail);
                    LOG_OBJECT.Debug("loHhsOrigin :: " + loHhsOrigin);
                    
                    lbAuthenticate = updateUserBean(lsSamlEmail, loSBOrgType,  loUserBean, loHhsOrigin, loUserBeanCity);
                   
                    LOG_OBJECT.Debug("Ortg Type :: " + loSBOrgType);
                    /*
                     * } catch (LoginException aoExp) { lbLdapUnavailable =
                     * catchLoginException(lbLdapUnavailable, aoExp); }
                     *//** [End] QC 9165 R 7.8.0 */
                }
                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  Validating User:  lbAuthenticate <>"   + lbAuthenticate);
                if (!lbAuthenticate) 
                {
                    loSession.removeAttribute( ApplicationConstants.KEY_SESSION_USER_VALIDATED, PortletSession.APPLICATION_SCOPE);
                    if (lbLdapUnavailable) 
                    {
                        aoResponse.setRenderParameter("errorMsg", LoginController.REQUEST_COULD_NOT_BE_COMPLETED);
                    } else {
                        aoResponse.setRenderParameter("errorMsg", "! The entered login information is not valid");
                    }
                } 
                else 
                {
                    // Start QC 8914 R 7.2.0
                    // get oversight flag
                    // if it is true - show oversight role switch icon - allow
                    // user to
                    // redirect to city home screen
                    aoRequest.setAttribute("user_type_original",  loUserBean.getMsOrgType());
                    aoRequest.setAttribute("user_type",   loUserBean.getMsOrgType());
                    LOG_OBJECT.Debug("After::lbAuthenticate     user_type: "    + loUserBean.getMsOrgType());
                    LOG_OBJECT.Debug("After::lbAuthenticate     UserID: "   + loSBUserId.toString());

                    String oversightFlag = "";
                    if (null != loUserBean.getMsOrgType()
                            && ("agency_org".equalsIgnoreCase(loUserBean.getMsOrgType()) || "city_org".equalsIgnoreCase(loUserBean.getMsOrgType()))) 
                    {
                        LOG_OBJECT.Debug("UserID: " + loSBUserId.toString());
                        /***[Start] QC 9205 R 8.0.0 */
                        LOG_OBJECT.Debug("UserID loUserBeanCity.getMsUserId(): " + loUserBeanCity.getMsUserId());
                        
                        loSBUserId.append(loUserBeanCity.getMsUserId());
                        loSBRole.append(loUserBeanCity.getMsRole());
                        loSBOrg.append(loUserBeanCity.getMsOrgId());
                        //loSBOrgType.append(loUserBeanCity.getMsOrgType());
                        loSBOrgName.append(loUserBeanCity.getMsOrgName());
                        //loUserBean.setMsLoginId(lsLoginUser);
                        loUserBean.setMsOrgName(loUserBeanCity.getMsOrgName());
                        loUserBean.setMsUserId(loUserBeanCity.getMsUserId());
                        loUserBean.setMsRole(loUserBeanCity.getMsRole());
                        loUserBean.setMsOrgId(loUserBeanCity.getMsOrgId());
                        loUserBean.setMsOrgType(loUserBeanCity.getMsOrgType());
                        loUserBean.setMsUserName(loUserBeanCity.getMsUserName());
                        LOG_OBJECT.Debug("lsUserId: " + loSBUserId);
                        LOG_OBJECT.Debug("loSBRole: " + loSBRole);
                        LOG_OBJECT.Debug("loSBOrg: " + loSBOrg);
                        LOG_OBJECT.Debug("loSBOrgType: " + loSBOrgType);
                        lsEmail = loUserBeanCity.getMsUserEmail();
                        loUserBean.setMsUserEmail(lsSamlEmail);
                        LOG_OBJECT.Debug("lsEmail: " + lsEmail);
                        LOG_OBJECT.Debug("loUserBean :: " + loUserBean);
                         /***[End] QC 9205 R 8.0.0  */                    
                        // QC 8914 R 7.2.0 - read only role - get oversight flag
                        oversightFlag = fetchCityAgencyUserOversightFlag(loSBUserId.toString());
                        LOG_OBJECT.Debug("OversigtFlag: " + oversightFlag);
                        
    	                aoRequest.setAttribute("user_type_original",  loUserBeanCity.getMsOrgType());
    	                aoRequest.setAttribute("user_type", loUserBeanCity.getMsOrgType());
    	                
                    }
                    // end QC 8914 R 7.2.0
                    LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleActionRequestInternal \n oversightFlag <>"
                                    + oversightFlag
                                    + "\n lsEmail                 >>"
                                    + lsEmail
                                    + "\n loSBRole                >>"
                                    + loSBRole
                                    + "\n loSBOrg                 >>"
                                    + loSBOrg
                                    + "\n loSBOrgType             >>"
                                    + loSBOrgType
                                    + "\n loSBUserId              >>"
                                    + loSBUserId
                                    + "\n loSBOrgName             >>"
                                    + loSBOrgName
                                    /*
                                     * + "\n loUserHashMap           >>" +
                                     * loUserHashMap
                                     */
                                    + "\n loUserBean.getMsUserDN()>>"
                                    + loUserBean.getMsUserDN()
                                    + "\n oversightFlag           >>"
                                    + oversightFlag
                                    + "\n loUserBean.toString()   >>"
                                    + loUserBean.toString());

                    settingSessionVariables(loSession, lsEmail, loSBRole, loSBOrg, loSBOrgType, loSBUserId, loSBOrgName,
                            				loUserHashMap, loUserBean.getMsUserDN(), oversightFlag, loUserBean, loChannel, loUserSession);
                    
                    
                    
                
                    // Missing Profile Information Start

                    if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(loSBOrgType.toString())) 
                    {
                    	LOG_OBJECT.Debug("PROVIDER_ORG ");
                    	Map loFlagMap = null;
                        loFlagMap = (Map) providerProcessingFlow(aoRequest, aoResponse, loSession, lsEmail, loUserBean.getMsUserDN(),
                                								 lbMissingControllerFlag, lbRedirectFlag, lbLocalFlow, loUserBean);

                        lbMissingControllerFlag = (Boolean) loFlagMap.get("MissingControllerFlag");
                        lbRedirectFlag = (Boolean) loFlagMap.get("RedirectFlag");
                        lbAuthenticate = (Boolean) loFlagMap.get("ActivationFlag");
                        lbSelectOrgScreenFlag = (Boolean) loFlagMap.get("MultiAccountFlag");
                        /*[Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */
                      
                        if(loFlagMap.get("lsUserActiveStatusFlag")!=null){
                        	lsUserActiveStatusFlag = (Boolean) loFlagMap.get("lsUserActiveStatusFlag");
                        }
                        loSession.setAttribute("lsUserActiveStatusFlag", lsUserActiveStatusFlag);
                        
                        // made changes for log out issue Defect#6432 fixed in
                        // release 3.1.3
                        if(lsUserActiveStatusFlag){
	                        if (null != loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)
	                                && !((String) loSession.getAttribute( ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)).isEmpty()) 
	                        {
	                            setUserIdInCache((String) loSession.getAttribute( ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE),
	                                    loSession.getId());
	                            
	                        } else if (null != loSession.getAttribute( ApplicationConstants.USER_DN,  PortletSession.APPLICATION_SCOPE) 
	                        		    && !((String) loSession.getAttribute( ApplicationConstants.USER_DN, PortletSession.APPLICATION_SCOPE)).isEmpty()) {
	                            setUserIdInCache((String) loSession.getAttribute( ApplicationConstants.USER_DN, PortletSession.APPLICATION_SCOPE),
	                                    loSession.getId());
	                            
	                        }
                        }
                        /*[End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */

                    } 
                    else  //City or Agency User
                    {   LOG_OBJECT.Debug("CITY/AGENCY_ORG "); 
                        createRoleMappingMap(loSession, loUserBean);
                       
                        // release 8.0.0
                        if (null != loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)
                                && !((String) loSession.getAttribute( ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE)).isEmpty()) 
                        {
                            setUserIdInCache((String) loSession.getAttribute( ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE),
                                    loSession.getId());
                        } 
                        else if (null != loSession.getAttribute( ApplicationConstants.USER_DN,  PortletSession.APPLICATION_SCOPE) 
                        		    && !((String) loSession.getAttribute( ApplicationConstants.USER_DN, PortletSession.APPLICATION_SCOPE)).isEmpty())
                        {
                            setUserIdInCache((String) loSession.getAttribute( ApplicationConstants.USER_DN, PortletSession.APPLICATION_SCOPE),
                                    loSession.getId());
                        }
                    }

                    if (lbAuthenticate) 
                    {
                        List<String> loCredentails = CredentialVaultUtil.getCredential(ApplicationConstants.CREDENTIAL_VAULT_KEY);
                        loUserSession.setUserId(loCredentails.get(0));
                        loUserSession.setPassword(loCredentails.get(1));
                        loSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_VALIDATED,
                                        		ApplicationConstants.TRUE,
                                        		PortletSession.APPLICATION_SCOPE);
                        String lsUrlHit = (String) aoRequest.getPortletSession().getAttribute( HHSConstants.SESION_URL,  PortletSession.APPLICATION_SCOPE);
                        
                        if (null != lsUrlHit   && !lbRedirectFlag
                                				&& !lbMissingControllerFlag
                                				&& ((!lbSelectOrgScreenFlag && !lsUrlHit.contains("notificationIdFromEmail"))
                                						|| (lbSelectOrgScreenFlag && lsUrlHit.contains("notificationIdFromEmail")) 
                                						|| (!lbSelectOrgScreenFlag	&& lsUrlHit.contains("notificationIdFromEmail")))) 
                        {                        	
                        	LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  lbMissingControllerFlag<"
                                    + lbMissingControllerFlag
                                    + ">  lbRedirectFlag<"
                                    + lbRedirectFlag
                                    + ">    lbSelectOrgScreenFlag<"
                                    + lbSelectOrgScreenFlag + "> lsUrlHit<" + lsUrlHit + ">\n");
                        	redirectToUrl(aoRequest, aoResponse, lsUrlHit);
                        } 
                        else 
                        {
                            LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  lbMissingControllerFlag<"
                                            + lbMissingControllerFlag
                                            + ">  lbRedirectFlag<"
                                            + lbRedirectFlag
                                            + ">    lbSelectOrgScreenFlag<"
                                            + lbSelectOrgScreenFlag + "> lsUrlHit<" + lsUrlHit + ">\n");

                            if (!lbMissingControllerFlag && !lbRedirectFlag && !lbSelectOrgScreenFlag) 
                            {
                                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  !lbMissingControllerFlag && !lbRedirectFlag && !lbSelectOrgScreenFlag<"
                                                + (!lbMissingControllerFlag && !lbRedirectFlag && !lbSelectOrgScreenFlag)
                                                + ">");
                                
                                redirectToHomePage(aoRequest, aoResponse, loSBOrg, loSBOrgType);
                                
                            } else if (!lbMissingControllerFlag  && lbSelectOrgScreenFlag) 
                            {
                                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  !lbMissingControllerFlag && lbSelectOrgScreenFlag<"
                                                + (!lbMissingControllerFlag && lbSelectOrgScreenFlag)
                                                + ">");
                                
                                redirectToSelectOrganization(aoRequest,  aoResponse);
                            }
                        }

                    }
                }
            }
        } catch (ApplicationException aoAppEx) {
            catchApplicationException(aoResponse, aoAppEx);
        } catch (Exception aoThrwEx) {
            catchThrowable(aoResponse, aoThrwEx);
        }

        long loEndTimeTime = System.currentTimeMillis();
        try {
            LOG_OBJECT.Debug("TIME TAKEN for execution of action Method in LoginController = " + (loEndTimeTime - loStartTime));
        } catch (ApplicationException aoEx) {
            LOG_OBJECT.Error("Error while execution of action Method in LoginController",  aoEx);
        }
        UserThreadLocal.unSet();
    }

    /**
     * @param abLdapUnavailable
     *            Boolean value telling ldap server is available or not
     * @param ae
     *            Login Exception Object
     * @return abLdapUnavailable
     */
    private boolean catchLoginException(boolean abLdapUnavailable,
            LoginException ae) {
        if (!ae.toString().toLowerCase().contains("failedloginexception")) {
            abLdapUnavailable = true;
        }
        LOG_OBJECT.Error("Error occured while authenticating", ae);
        return abLdapUnavailable;
    }

    /**
     * @param asEmail
     *            Email id of the user
     * @param aoSBOrgType
     *            Type of organization
     * @param aoUserBean
     *            Bean containing user attributes
     * @param aoHhsOrigin
     *            type of user : citi/agency= CITY or provider=DMZ // QC 9205 R 8.0.0 SAML Internal          
     * @return lbAuthenticate boolean value telling user is authenticated or not
     */
    private boolean updateUserBean(String asEmail, StringBuilder aoSBOrgType, UserBean aoUserBean, String aoHhsOrigin, UserBean loUserBeanCity) {
        boolean lbAuthenticate;
        lbAuthenticate = true;
        aoUserBean.setMsLoginId(asEmail);
        aoUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
        /** [Start] QC 9205 R 8.0.0 SAML Internal **/
        if(ApplicationConstants.CITY.equalsIgnoreCase(aoHhsOrigin) && loUserBeanCity!=null)
        {
        	 //aoUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);
        	 aoUserBean.setMsOrgType(loUserBeanCity.getMsOrgType());
        	 aoUserBean.setMsUserId(loUserBeanCity.getMsUserId());
        }
        /** [End] QC 9205 R 8.0.0 SAML Internal **/
        aoSBOrgType.append(aoUserBean.getMsOrgType());
        return lbAuthenticate;
    }

    /**
     * This method set P8 constants in set property method of the system
     * 
     * @throws ApplicationException
     */
    private void setSystemProperty() throws ApplicationException {
        System.setProperty( P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG,
                			PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,  
                			P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
        System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL,
                			PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
                			P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
        System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
                PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
    }

    /**
     * This method is called when Throwable Exception is caught to log the error
     * 
     * @param aoResponse
     *            is the decides the next execution flow
     * @param aoThrwEx
     *            is the Throwable Exception object
     */
    private void catchThrowable(ActionResponse aoResponse, Throwable aoThrwEx) {
        String lsErrorMsg = LoginController.REQUEST_COULD_NOT_BE_COMPLETED;
        aoResponse.setRenderParameter("errorMsg", lsErrorMsg);
        LOG_OBJECT.Error(
                "Error occurred while inserting Missing profile details",
                aoThrwEx);
    }

    /**
     * This method is called when ApplicationException is caught to log the
     * error
     * 
     * @param aoResponse
     *            decides the next execution flow
     * @param aoAppEx
     *            is the ApplicationException object
     */
    private void catchApplicationException(ActionResponse aoResponse,
            ApplicationException aoAppEx) {
        String lsErrorMsg = LoginController.REQUEST_COULD_NOT_BE_COMPLETED;
        aoResponse.setRenderParameter("errorMsg", lsErrorMsg);
        LOG_OBJECT.Error("Error occured while getting p8Session", aoAppEx);
    }

    /**
     * This method sets the variables for the P8userSession
     * 
     * @return loUserSession is the P8UserSession object
     * @throws ApplicationException
     */
    private P8UserSession setP8SessionVariables() throws ApplicationException {
        P8UserSession loUserSession = new P8UserSession();
        Channel loChannelObj = new Channel();
        loUserSession.setContentEngineUri(PropertyLoader.getProperty( P8Constants.PROPERTY_FILE, "FILENET_URI"));
        loUserSession.setObjectStoreName(PropertyLoader.getProperty( P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
        loUserSession.setIsolatedRegionName(PropertyLoader.getProperty( P8Constants.PROPERTY_FILE, "CONNECTION_POINT_NAME"));
        loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty( P8Constants.PROPERTY_FILE, "CONNECTION_POINT_NUMBER"));
        // Changes made in R5 to get the filnet session while logging into the
        // application Starts
        loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
        loUserSession = (P8UserSession) loChannelObj.getData(HHSConstants.AO_FILENET_SESSION);
        // Changes made in R5 to get the filnet session while logging into the
        // application Ends
        return loUserSession;
    }

    /**
     * This method is called when the user is authenticated to set the user
     * details (EmailId, Password,Role etc...)
     * 
     * @param asEmail
     *            is the user email id
     * @param asPassword
     *            is the user password
     * @param aoSBRole
     *            is the user role
     * @param aoSBOrg
     *            is the organization id
     * @param aoSBOrgType
     *            is the organization type
     * @param aoSBUserId
     *            is the user Id
     * @param aoSBOrgName
     *            is the organization type
     * @param aoUserSession
     *            is the P8UserSession object
     * @return loUserBean is the bean containing all the user details.
     * @throws ApplicationException
     */
    private UserBean fillAuthenticationDetail(String asEmail,
            String asPassword, StringBuilder aoSBRole, StringBuilder aoSBOrg,
            StringBuilder aoSBOrgType, StringBuilder aoSBUserId,
            StringBuilder aoSBOrgName) throws ApplicationException {

        UserBean loUserBean;
        loUserBean = (UserBean) getUserRole(asEmail, asPassword);
        if (loUserBean != null) {
            aoSBUserId.append(loUserBean.getMsUserId());
            aoSBRole.append(loUserBean.getMsRole());
            aoSBOrg.append(loUserBean.getMsOrgId());
            aoSBOrgType.append(loUserBean.getMsOrgType());
            aoSBOrgName.append(loUserBean.getMsOrgName());
            loUserBean.setMsLoginId(asEmail);
        }

        return loUserBean;
    }

    /**
     * This method redirects the user to the specified url
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param asUrlHit
     *            is the string representation of the url
     * @throws ApplicationException
     */
    private void redirectToUrl(ActionRequest aoRequest,  ActionResponse aoResponse, String asUrlHit)
            throws ApplicationException {
        try {
        	 aoRequest.getPortletSession().removeAttribute(HHSConstants.SESION_URL, PortletSession.APPLICATION_SCOPE);
             aoResponse.sendRedirect(asUrlHit + "&fromLoginPage=fromLoginPage");
        } catch (IOException aoIoExp) {
            throw new ApplicationException("Not able to redirect to this URL.",
                    aoIoExp);
        }
    }

    /**
     * This method is called for provider users to process their create new
     * organization flow/or request to their existing account and to check
     * whether they have accepted terms and conditions or not and re direct to
     * home page. <li>This method was updated in R4</li> Updated for R4: Adding
     * check to fetch list of all the organizations on initial login and change
     * the existing flow if multiple organizations are found. For single
     * organization flow will work as earlier and for multiple it will redirect
     * to Select A Organization Screen.
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param aoSession
     *            is the PortletSession object
     * @param asEmail
     *            is the user email Id
     * @param asUserDN
     *            is the unique user Id
     * @param abMissingControllerFlag
     *            is the boolean flag to decide whether MissingFlowController
     *            will execute or not
     * @param abLocalFlow
     *            is the flag to determine whether the environment is local or
     *            not
     * @param aoUserBean
     *            is the user detail bean
     * @param abRedirectFlag
     *            is the flag which determines whether next page is to be
     *            displayed or not
     * @return loFlagMap the map containing lbMissingControllerFlag and
     *         lbRedirectFlag
     * @throws ApplicationException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map providerProcessingFlow(ActionRequest aoRequest, ActionResponse aoResponse, PortletSession aoSession,
    									String asEmail, String asUserDN, boolean abMissingControllerFlag,
    									boolean abRedirectFlag, boolean abLocalFlow, UserBean aoUserBean)   throws ApplicationException 
    {
        Channel loChannel = new Channel();
        StaffDetails loStaffDetailsBean = new StaffDetails();
        String lsUserFirstName = "", lbUserProfileExistInDB = LoginController.NOT_FOUND;
        String lsUserMiddleName = "", lsUserLastName = "", lsUserActiveStatus = "", lsOrgActiveStatus = "", lsOrgProcStatus = "";
        String lsNextAction = "", lsUserEmailId = "", lbUserProfileExistInLdap = LoginController.NOT_FOUND, lsGlobalErrorMsg = "";
        String lsEmailValidationFlag = "";
        boolean lsValidationFlag = true;
        boolean lbMultiAccountFlag = false;
        boolean lsUserActiveStatusFlag=false;
        Map<String, String> loUserDetailRtrndMap = null;
        Map loFlagMap = new HashMap();
        // Adding this for local setup in R5
        lsUserEmailId = asEmail.toLowerCase();
        // End
        LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% providerProcessingFlow : start"
                        + "\n asEmail                     >>"
                        + asEmail
                        + "\n asUserDN                    >>"
                        + asUserDN
                        + "\n abMissingControllerFlag     >>"
                        + abMissingControllerFlag
                        + "\n abRedirectFlag              >>"
                        + abRedirectFlag
                        + "\n abLocalFlow                 >>"
                        + abLocalFlow
                        + "\n aoUserBean.toString()       >>"
                        + aoUserBean.toString());

        if (!abLocalFlow) 
        {
            loChannel = new Channel();
            loChannel.setData("asUserEmailID", asEmail);

            /** [Start] R7.8.0 SAML remove LDAP web service and add new code **/
            loChannel.setData("asUserDN", asUserDN);
            aoSession.setAttribute("userDN", asUserDN,   PortletSession.APPLICATION_SCOPE);

            SecurityTkn loSecurityAttr = (SecurityTkn) aoSession.getAttribute( ApplicationConstants.SAML_ATTRIBUTE_FOR_NAME,  PortletSession.APPLICATION_SCOPE);
            StaffDetails loStaffDetail = new StaffDetails(
                    						loSecurityAttr.getUserDn(), loSecurityAttr.getNycId(),
                    						loSecurityAttr.getFirstName(),
                    						loSecurityAttr.getMiddleName(),
                    						loSecurityAttr.getLastName());

            loChannel.setData(HHSConstants.NYC_STAFF_DETAILS_PARAM_KEY,    loStaffDetail);

            // Before R7.8.0
            // loChannel.setData("aoStaffDetails", new StaffDetails(asUserDN));
            // TransactionManager.executeTransaction(loChannel,
            // "userDnSearchVDX");
            // asUserDN = (String) loChannel.getData("asUserDN");
            // Web SerNvice Call to fetch user Profile in LDAP ..
            // TransactionManager.executeTransaction(loChannel,
            // "userDetailsSearch");
            // loUserDetailRtrndMap = (Map)
            // loChannel.getData("aoUserDetailRtrndMap");
            /*
             * lsUserFirstName = (String) loUserDetailRtrndMap.get("FirstName");
             * lsUserMiddleName = (String) loUserDetailRtrndMap.get("Initials");
             * lsUserLastName = (String) loUserDetailRtrndMap.get("LastName");
             * lsEmailValidationFlag = (String)
             * loUserDetailRtrndMap.get("nycExtEmailValidationFlag");
             */

            TransactionManager.executeTransaction(loChannel, HHSConstants.NYC_USER_ORG_DETAILS);
            List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loChannel.getData(HHSConstants.NYC_USER_ORG_DETAILS_RESULT);
            if (loStaffDetailsList != null && loStaffDetailsList.size() > 0) 
            {
                lsUserFirstName = loStaffDetailsList.get(0).getMsStaffFirstName();
                lsUserMiddleName = loStaffDetailsList.get(0).getMsStaffMidInitial();
                lsUserLastName = loStaffDetailsList.get(0).getMsStaffLastName();
                lsEmailValidationFlag = HHSConstants.SYSTEM_YES;
            } else {
                if (loSecurityAttr != null) 
                {
                    lsUserFirstName = loSecurityAttr.getFirstName();
                    lsUserMiddleName = loSecurityAttr.getMiddleName();
                    lsUserLastName = loSecurityAttr.getLastName();
                    lsEmailValidationFlag = (loSecurityAttr.getNycEmailValisationFlag().equalsIgnoreCase(HHSConstants.TRUE) ? HHSConstants.SYSTEM_YES
                            : loSecurityAttr.getNycEmailValisationFlag());
                }
            }
            LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% providerProcessingFlow: nycExtEmailValidationFlag "
                            + "\n lsUserFirstName                     >>"
                            + lsUserFirstName
                            + "\n lsUserMiddleName                    >>"
                            + lsUserMiddleName
                            + "\n lsUserLastName                      >>"
                            + lsUserLastName
                            + "\n lsEmailValidationFlag               >>"
                            + lsEmailValidationFlag
                            + "\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            /** [End] R7.8.0 SAML remove LDAP web service and add new code **/

            if ((null != lsEmailValidationFlag && !lsEmailValidationFlag.equalsIgnoreCase(HHSConstants.SYSTEM_YES))
                    || !loSecurityAttr.isProfileComplete()) {
                LOG_OBJECT
                        .Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% providerProcessingFlow: lsEmailValidationFlag  is NNNot Yes!!~~~!!!!!! "
                                + "\n lsUserFirstName                     >>"
                                + lsUserFirstName
                                + "\n lsUserMiddleName                    >>"
                                + lsUserMiddleName
                                + "\n lsUserLastName                      >>"
                                + lsUserLastName
                                + "\n lsEmailValidationFlag               >>"
                                + lsEmailValidationFlag
                                + "\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                if (loSecurityAttr.isProfileComplete()) {
                    lsGlobalErrorMsg = "Your profile has not been completed yet.";
                } else {
                    lsGlobalErrorMsg = "Your email Id has not been activated yet.";
                }
                abRedirectFlag = true;
                aoResponse.setRenderParameter("errorMsg", lsGlobalErrorMsg);
                abMissingControllerFlag = false;
                lsValidationFlag = false;
                removeSessionVariables(aoRequest.getPortletSession());
            } else {
                // LDAP Missing Name Check ..
                if (null != lsUserFirstName
                        && !lsUserFirstName.trim().equalsIgnoreCase("")
                        && null != lsUserLastName
                        && !lsUserLastName.trim().equalsIgnoreCase("")) {
                    aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_NAME, lsUserFirstName + " " + lsUserMiddleName + " "
                                    											+ lsUserLastName,   PortletSession.APPLICATION_SCOPE);
                    lbUserProfileExistInLdap = LoginController.FOUND;
                } else {
                    String lsNYCUserName = "";
                    lsNYCUserName = (null != lsUserFirstName ? lsUserFirstName
                            : " ")
                            + (null != lsUserLastName ? lsUserLastName : " ")
                            + (null != lsUserFirstName ? lsUserFirstName : " ");
                    aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_NAME, lsNYCUserName, PortletSession.APPLICATION_SCOPE);
                }
            }
        }
        LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% lsValidationFlag " + lsValidationFlag);
        if (lsValidationFlag) 
        {
            String lsLoginId = asEmail;
            loStaffDetailsBean.setMsStaffEmail(lsLoginId);
            /* [Start]R 7.8.0 SAML */
            loStaffDetailsBean.setMsNYCUserId(asEmail);
            /* [End]R 7.8.0 SAML */
            loStaffDetailsBean.setMsUserDN(asUserDN);
            List<StaffDetails> loStaffDetailsList = null;
            loChannel.setData("aoStaffDetails", loStaffDetailsBean);
            TransactionManager.executeTransaction(loChannel,  "getNYCUserOrganizationDetails");
            loStaffDetailsList = (List<StaffDetails>) loChannel.getData("aoStaffDetailBean");
            if (null != loStaffDetailsList && !loStaffDetailsList.isEmpty()) 
            {
                loStaffDetailsBean = loStaffDetailsList.get(0);
                loStaffDetailsBean.setMbMutiAccount(false);
                if (loStaffDetailsList.size() > 1) {
                    Iterator loStaffDetailsListItr = loStaffDetailsList.iterator();
                    List<StaffDetails> loStaffDetailListForMultiLogin = new ArrayList();
                    while (loStaffDetailsListItr.hasNext()) {
                        StaffDetails loTempStaffDetailObj = (StaffDetails) loStaffDetailsListItr.next();
                        if (null != loTempStaffDetailObj  && null != loTempStaffDetailObj.getMsStaffActiveFlag()
                                && ApplicationConstants.YES.equalsIgnoreCase(loTempStaffDetailObj.getMsStaffActiveFlag())) 
                        {
                            loStaffDetailListForMultiLogin.add(loTempStaffDetailObj);
                        }
                    }
                    if (loStaffDetailListForMultiLogin.size() > 1) 
                    {
                        loStaffDetailsBean.setMbMutiAccount(true);
                        aoSession.setAttribute( ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM,  loStaffDetailListForMultiLogin, PortletSession.APPLICATION_SCOPE);
                        aoRequest.getPortletSession().setAttribute("ShowAccountSwitchIcon", true,  PortletSession.APPLICATION_SCOPE);
                    }
                }
            }
            LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% loStaffDetailsBean " + loStaffDetailsBean);
            if (null != loStaffDetailsBean) {
                lbMultiAccountFlag = loStaffDetailsBean.getMbMutiAccount();
                lsOrgActiveStatus = loStaffDetailsBean.getMsOrgActiveFlag();
                lsOrgProcStatus = loStaffDetailsBean.getMsOrgProcStatus();
                lsUserActiveStatus = loStaffDetailsBean.getMsStaffActiveFlag();
                setBeanAndSessionAttribute(aoSession, aoUserBean,  loStaffDetailsBean);               
            }
            /*[Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */
            if(lsUserActiveStatus!=null && lsUserActiveStatus.equalsIgnoreCase("Yes") ){
            	lsUserActiveStatusFlag = true;
            }else{
            	lsUserActiveStatusFlag = false;
            }
            loFlagMap.put("lsUserActiveStatusFlag", lsUserActiveStatusFlag);
            
            if (null != lsOrgActiveStatus && !lsOrgActiveStatus.isEmpty()
                    && lsOrgActiveStatus.equalsIgnoreCase("false")) {
                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% lsOrgActiveStatus "  + lsOrgActiveStatus);
                if (null != lsOrgProcStatus   && !lsOrgProcStatus.isEmpty()
                        && lsOrgProcStatus.equalsIgnoreCase(LoginController.REJECTED)) 
                {
                    lsGlobalErrorMsg = "Your organization account request has been rejected by the Accelerator team.";
                } else {
                	lsGlobalErrorMsg = "Your organization's HHS Accelerator account request is currently in review." +
                			" You will be notified when a decision has been made. " +
                			"Please add noreplyplease@hhsaccelerator.nyc.gov to your email contacts safe list in order to receive HHS Accelerator notifications.";
                    /*lsGlobalErrorMsg = "Your organization account request is under review by the Accelerator team. "
                            + "The account administrator will receive an email when a decision has been made.";*/
                }
                abRedirectFlag = true;
                aoResponse.setRenderParameter("errorMsg", lsGlobalErrorMsg);
                abMissingControllerFlag = false;              
                /*[End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */  
            } else if (null != lsUserActiveStatus
                    && !lsUserActiveStatus.equalsIgnoreCase("Yes")
                    && !lsUserActiveStatus.equalsIgnoreCase("")) {
                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% lsUserActiveStatus "        + lsUserActiveStatus);
                lsGlobalErrorMsg = ApplicationConstants.USER_ACTIVE_STATUS_MESSAGE;
                abRedirectFlag = true;
                aoResponse.setRenderParameter("errorMsg", lsGlobalErrorMsg);
                abMissingControllerFlag = false;               
            } else {
                LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Else   "  + lsUserActiveStatus);
                abMissingControllerFlag = searchUser(aoRequest, aoResponse,
                        aoSession, asUserDN, abMissingControllerFlag,
                        abLocalFlow, aoUserBean, lsUserFirstName,
                        lbUserProfileExistInDB, lsUserMiddleName,
                        lsUserLastName, lsNextAction, lsUserEmailId,
                        lbUserProfileExistInLdap);

                if (lbMultiAccountFlag) 
                {
                	aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_ORG, null,  PortletSession.APPLICATION_SCOPE);
                    aoSession.setAttribute( ApplicationConstants.KEY_SESSION_ORG_NAME, null,   PortletSession.APPLICATION_SCOPE);
                    aoSession.setAttribute( ApplicationConstants.KEY_SESSION_ORG_ID, null, PortletSession.APPLICATION_SCOPE);
                }
            }
        }
        // Passing value of 'abMultiAccountFlag' as 'true' to permanently enable
        // Select Organization screen in Login Flow
        // To skip Switch Organization screen for user having single mapped
        // organization pass variable 'lbMultiAccountFlag' in method call.
        // To completely disable Select Organization screen and login using the
        // base organization pass the value as 'False' in the method call.
        setFlagMap(abMissingControllerFlag, abRedirectFlag, lsValidationFlag,
                lbMultiAccountFlag, loFlagMap);

        LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% providerProcessingFlow ENDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"
                        + "\n MissingControllerFlag                     >>"
                        + loFlagMap.get("MissingControllerFlag")
                        + "\n RedirectFlag                    >>"
                        + loFlagMap.get("RedirectFlag")
                        + "\n ActivationFlag     >>"
                        + loFlagMap.get("ActivationFlag")
                        + "\n MultiAccountFlag              >>"
                        + loFlagMap.get("MultiAccountFlag")
                        + "\n lsUserActiveStatusFlag              >>"
                        + loFlagMap.get("lsUserActiveStatusFlag"));

        return loFlagMap;
    }

    /**
     * This method update the session, staff detail bean and user bean
     * 
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param aoUserBean
     *            bean containing user detail attributes
     * @param aoStaffDetailsBean
     *            StaffDetailsBean with staff details(userdn, name, permission
     *            level, etc..)
     */
    private void setBeanAndSessionAttribute(PortletSession aoSession,
            UserBean aoUserBean, StaffDetails aoStaffDetailsBean) {
        aoStaffDetailsBean.getMsOrganisationName();
        aoStaffDetailsBean.getMsOrgId();
        aoUserBean.setMsOrgName(aoStaffDetailsBean.getMsOrganisationName());
        aoUserBean.setMsOrgId(aoStaffDetailsBean.getMsOrgId());
        aoUserBean.setMsPermissionLevel(aoStaffDetailsBean
                .getMsPermissionLevel());
        aoSession.setAttribute(
                ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL,
                aoUserBean.getMsPermissionLevel(),
                PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,
                aoStaffDetailsBean.getMsOrgId(),
                PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,
                aoStaffDetailsBean.getMsOrganisationName(),
                PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_ID,
                aoStaffDetailsBean.getMsOrgId(),
                PortletSession.APPLICATION_SCOPE);
    }

    /**
     * Thsi method update the map with the boolean flags
     * 
     * @param abMissingControllerFlag
     *            boolean values that states 1. organization not created for
     *            that user. 2. Terms and conditions are not checked, if any is
     *            true then missing profile flow will come in between
     * @param abRedirectFlag
     *            boolean value to decide whether any redirect to another
     *            controller action is performed
     * @param lsValidationFlag
     *            boolean value telling email is validated or not.
     * @param abMultiAccountFlag
     *            boolean value telling Select Organization screen to be
     *            displayed in Login Flow or not.
     * @param loFlagMap
     *            map containing boolean values
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setFlagMap(boolean abMissingControllerFlag,boolean abRedirectFlag, boolean lsValidationFlag,  boolean abMultiAccountFlag, Map loFlagMap) 
    {
        loFlagMap.put("MissingControllerFlag", abMissingControllerFlag);
        loFlagMap.put("RedirectFlag", abRedirectFlag);
        loFlagMap.put("ActivationFlag", lsValidationFlag);
        loFlagMap.put("MultiAccountFlag", abMultiAccountFlag);
    }

    /**
     * this method is used to search the user in db and follow action based on
     * that <li>This method was updated in R4</li>
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param asUserDN
     *            DN of the user
     * @param abMissingControllerFlag
     *            boolean values that states 1. organization not created for
     *            that user. 2. Terms and conditions are not checked, if any is
     *            true then missing profile flow will come in between
     * @param abLocalFlow
     *            boolean value that states process for city user flow
     * @param aoUserBean
     *            UserBean with user details (user name, role, etc..)
     * @param asUserFirstName
     *            first name of user who is logged in
     * @param abUserProfileExistInDB
     *            states whether user profile exists in database
     * @param asUserMiddleName
     *            middle name of user who is logged in
     * @param asUserLastName
     *            last name of user who is logged in
     * @param asNextAction
     * @param asUserEmailId
     *            email Id of the user
     * @param asUserProfileExistInLdap
     *            states whether user profile exists in Ldap
     * @return abMissingControllerFlag
     * @throws ApplicationException
     */
    private boolean searchUser(ActionRequest aoRequest,
            ActionResponse aoResponse, PortletSession aoSession,
            String asUserDN, boolean abMissingControllerFlag,
            boolean abLocalFlow, UserBean aoUserBean, String asUserFirstName,
            String abUserProfileExistInDB, String asUserMiddleName,
            String asUserLastName, String asNextAction, String asUserEmailId,
            String asUserProfileExistInLdap) throws ApplicationException {
        Channel loChannel;
        StaffDetails loStaffDetailsBean;
        String lsStaffUserDN;
        lsStaffUserDN = asUserDN;
        aoUserBean.setMsUserDN(asUserDN);
        loChannel = new Channel();
        // R4: Passing the UserBean to provide additional parameter OrgId to
        // extend existing query join on STAFF_ORGANIZATION_MAPPING table
        loChannel.setData("aoUserBean", aoUserBean);
        TransactionManager.executeTransaction(loChannel,  "searchUserDnInStaffDetails");
        loStaffDetailsBean = (StaffDetails) loChannel.getData("aoStaffDetailsBean");

        LOG_OBJECT.Debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^searchUser::: abMissingControllerFlag "  + abMissingControllerFlag);
        if (loStaffDetailsBean != null) {
            LOG_OBJECT.Debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^searchUser::: abMissingControllerFlag at if (loStaffDetailsBean != null)"
                            + abMissingControllerFlag);
            abMissingControllerFlag = recordsFoundInDb(aoRequest, aoResponse,
                    aoSession, abMissingControllerFlag, abLocalFlow,
                    aoUserBean, loStaffDetailsBean, asUserFirstName,
                    asUserMiddleName, asUserLastName, asUserEmailId,
                    asUserProfileExistInLdap, abUserProfileExistInDB);
        } else {
            LOG_OBJECT.Debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^searchUser::: abMissingControllerFlag at if (loStaffDetailsBean ===== NULL )"
                            + abMissingControllerFlag);

            abMissingControllerFlag = searchRecordsInDb(aoRequest, aoResponse,
                    abMissingControllerFlag, lsStaffUserDN, asUserFirstName,
                    asUserMiddleName, asUserLastName, asNextAction,
                    asUserEmailId, asUserProfileExistInLdap,
                    abUserProfileExistInDB);
        }

        LOG_OBJECT.Debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^searchUser::: abMissingControllerFlag "
                        + abMissingControllerFlag);
        createRoleMappingMap(aoSession, aoUserBean);

        return abMissingControllerFlag;
    }

    /**
     * This method checks whether records are present in database or not
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param abMissingControllerFlag
     *            boolean values that states 1. organization not created for
     *            that user. 2. Terms and conditions are not checked, if any is
     *            true then missing profile flow will come in between
     * @param abLocalFlow
     *            boolean value that states process for city user flow
     * @param aoUserBean
     *            UserBean with user details (user name, role, etc..)
     * @param aoStaffDetailsBean
     *            StaffDetailsBean with staff details(userdn, name, permission
     *            level, etc..)
     * @param asUserFirstName
     *            first name of user who is logged in
     * @param asUserMiddleName
     *            middle name of user who is logged in
     * @param asUserLastName
     *            last name of user who is logged in
     * @param asUserEmailId
     *            email id of user who is logged in
     * @param asUserProfileExistInLdap
     *            states whether user profile exists in Ldap
     * @param asUserProfileExistInDB
     *            states whether user profile exists in database
     * @return boolean status whether Missing profile flow should be followed or
     *         not
     * @throws ApplicationException
     */
    private boolean recordsFoundInDb(ActionRequest aoRequest,
            ActionResponse aoResponse, PortletSession aoSession,
            boolean abMissingControllerFlag, boolean abLocalFlow,
            UserBean aoUserBean, StaffDetails aoStaffDetailsBean,
            String asUserFirstName, String asUserMiddleName,
            String asUserLastName, String asUserEmailId,
            String asUserProfileExistInLdap, String asUserProfileExistInDB)
            throws ApplicationException {
        String lsNextForm, lsNextAction, lsMissingInfoControllerPath, lsStaffFirstName = aoStaffDetailsBean
                .getMsStaffFirstName();
        String lsStaffUserDN = aoStaffDetailsBean.getMsUserDN(), lsTermConditions = aoStaffDetailsBean
                .getMsTermConditionStatus();
        String lsStaffLastName = aoStaffDetailsBean.getMsStaffLastName(), lsStaffMiddleName = aoStaffDetailsBean
                .getMsStaffMidInitial();
        String lsStaffEmail = aoStaffDetailsBean.getMsStaffEmail(), lsPermissionLevel = aoStaffDetailsBean
                .getMsPermissionLevel(), lsPermissionType = aoStaffDetailsBean
                .getMsPermissionType();
        String lsAdminPermission = aoStaffDetailsBean.getMsAdminPermission(), lsStaffId = aoStaffDetailsBean
                .getMsStaffId(), lbTcRequired = "No";
        String lsModifiedBy = aoStaffDetailsBean.getMsStaffId();
        aoUserBean.setMsUserName(lsStaffId);
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ID,  lsStaffId, PortletSession.APPLICATION_SCOPE);
        UserThreadLocal.setUser(lsStaffId);
        if (null != lsPermissionLevel) {
            if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsPermissionLevel)
                    && ApplicationConstants.SYSTEM_YES.equalsIgnoreCase(lsAdminPermission)) 
            {
                aoUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF);
            } else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsPermissionLevel)) {
                aoUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
            } else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2
                    .equalsIgnoreCase(lsPermissionLevel)
                    && ApplicationConstants.SYSTEM_YES
                            .equalsIgnoreCase(lsAdminPermission)) {
                aoUserBean
                        .setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
            } else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2
                    .equalsIgnoreCase(lsPermissionLevel)) {
                aoUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
            }
        }
        if (null != lsPermissionType && !lsPermissionType.isEmpty()) {
            aoUserBean.setMsPermissionType(lsPermissionType);
        }
        if (abLocalFlow) {
            asUserProfileExistInLdap = LoginController.FOUND;
        }
        if (!abLocalFlow && !lsStaffEmail.equalsIgnoreCase(asUserEmailId)) {
            aoStaffDetailsBean = new StaffDetails();
            aoStaffDetailsBean.setMsStaffEmail(asUserEmailId);
            aoStaffDetailsBean.setMsUserDN(lsStaffUserDN);
            aoStaffDetailsBean.setMsModifiedBy(lsModifiedBy);
            Channel loChannel = new Channel();
            loChannel.setData("aoStaffDetailsBean", aoStaffDetailsBean);
            TransactionManager.executeTransaction(loChannel,
                    "updateEmailInStaffDetails");
        }
        if (null != lsStaffFirstName
                && !lsStaffFirstName.trim().equalsIgnoreCase("")
                && (null != lsStaffLastName && !lsStaffLastName.trim()
                        .equalsIgnoreCase(""))
                && asUserProfileExistInLdap == LoginController.FOUND) {
            String lsUserName = lsStaffFirstName + " " + lsStaffLastName;
            if (lsStaffMiddleName != null
                    && !"".equalsIgnoreCase(lsStaffMiddleName)) {
                lsUserName = lsStaffFirstName + " " + lsStaffMiddleName + " "
                        + lsStaffLastName;
            }
            aoUserBean.setMsUserName(lsUserName);
            // First Name , Last Name Found .. T&C ..Home Page
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
                    lsUserName, PortletSession.APPLICATION_SCOPE);
            asUserProfileExistInDB = LoginController.FOUND;
            if (null != lsTermConditions
                    && lsTermConditions.equalsIgnoreCase("Yes")) {
                abMissingControllerFlag = false;
            } else {
                lsNextForm = TERMS_CONDITIONS;
                lsNextAction = LoginController.HOME_PAGE;
                LOG_OBJECT.Debug("Terms And Condition Not Yes for ::: "
                        + asUserEmailId);
                abMissingControllerFlag = true;
                lsMissingInfoControllerPath = aoRequest.getScheme()
                        + "://"
                        + aoRequest.getServerName()
                        + ":"
                        + aoRequest.getServerPort()
                        + aoRequest.getContextPath()
                        + ApplicationConstants.PORTAL_URL
                        + "&_pageLabel=portlet_hhsweb_portal_page_missing_profile&next_form="
                        + lsNextForm
                        + "&accoutRequestmodule=accoutRequestmodule&useremail="
                        + asUserEmailId + "&firstName=" + asUserFirstName
                        + "&lastName=" + asUserLastName + "&middleName="
                        + asUserMiddleName + "&userDn=" + lsStaffUserDN
                        + "&nextAction=" + lsNextAction + "&ldap="
                        + asUserProfileExistInLdap + "&db="
                        + asUserProfileExistInDB + "&modifyBy=" + lsModifiedBy
                        + "&tcRequired=" + lbTcRequired;
                LOG_OBJECT.Debug("MI$$1 - lsMissingInfoControllerPath "
                        + lsMissingInfoControllerPath);
                try {
                    aoRequest.getPortletSession().setAttribute(
                            "MissinProfileHeader", "MissinProfileHeader",
                            PortletSession.APPLICATION_SCOPE);
                    aoResponse.sendRedirect(lsMissingInfoControllerPath);
                    abMissingControllerFlag = true;
                } catch (IOException loExp) {
                    throw new ApplicationException(
                            "Not able to redirect  to missing profile terms and condition screen after validating user.",
                            loExp);
                }
            }
        } else {
            if (null != lsTermConditions
                    && !lsTermConditions.equalsIgnoreCase("Yes")) {
                lbTcRequired = "Yes";
            }
            lsNextForm = MISSING_PROFILE_INFO;
            lsNextAction = LoginController.HOME_PAGE;
            LOG_OBJECT
                    .Debug("First Name And Last Name not found in Db for ::: "
                            + asUserEmailId);

            lsMissingInfoControllerPath = aoRequest.getScheme()
                    + "://"
                    + aoRequest.getServerName()
                    + ":"
                    + aoRequest.getServerPort()
                    + aoRequest.getContextPath()
                    + ApplicationConstants.PORTAL_URL
                    + "&_pageLabel=portlet_hhsweb_portal_page_missing_profile&next_form="
                    + lsNextForm
                    + "&accoutRequestmodule=accoutRequestmodule&useremail="
                    + asUserEmailId + "&firstName=" + asUserFirstName
                    + "&lastName=" + asUserLastName + "&middleName="
                    + asUserMiddleName + "&userDn=" + lsStaffUserDN
                    + "&nextAction=" + lsNextAction + "&ldap="
                    + asUserProfileExistInLdap + "&db="
                    + asUserProfileExistInDB + "&tcRequired=" + lbTcRequired
                    + "&modifyBy=" + lsModifiedBy;
            LOG_OBJECT.Debug("MI$$2 - lsMissingInfoControllerPath "
                    + lsMissingInfoControllerPath);
            try {
                aoRequest.getPortletSession().setAttribute(
                        "MissinProfileHeader", "MissinProfileHeader",
                        PortletSession.APPLICATION_SCOPE);
                aoResponse.sendRedirect(lsMissingInfoControllerPath);
                abMissingControllerFlag = true;
            } catch (IOException loExp) {
                throw new ApplicationException(
                        "Not able to redirect  to missing profile terms and condition screen after validating user.",
                        loExp);
            }

        }
        return abMissingControllerFlag;
    }

    /**
     * This method searches for a particular record in database <li>This method
     * was added in R4</li>
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param abMissingControllerFlag
     *            boolean values that states 1. organization not created for
     *            that user. 2. Terms and conditions are not checked, if any is
     *            true then missing profile flow will come in between sets the
     *            flag to true.
     * @param asStaffUserDN
     *            user dn of staff
     * @param asUserFirstName
     *            first name of user
     * @param asUserMiddleName
     *            middle name of user
     * @param asUserLastName
     *            last name of user
     * @param asNextAction
     *            next action to be taken when profile is missing or not
     * @param asUserEmailId
     *            email id of user
     * @param asUserProfileExistInLdap
     *            states whether user profile exists in Ldap
     * @param asUserProfileExistInDB
     *            states whether user profile exists in database
     * @return boolean status whether Missing profile flow should be followed or
     *         not
     * @throws ApplicationException
     */
    private boolean searchRecordsInDb(ActionRequest aoRequest,
            ActionResponse aoResponse, boolean abMissingControllerFlag,
            String asStaffUserDN, String asUserFirstName,
            String asUserMiddleName, String asUserLastName,
            String asNextAction, String asUserEmailId,
            String asUserProfileExistInLdap, String asUserProfileExistInDB)
            throws ApplicationException {
        String lsNextForm;
        String lsMissingInfoControllerPath;
        // Record not found in DB .. T&C, EIN Search,
        if (asUserProfileExistInLdap.equalsIgnoreCase(LoginController.FOUND)) {
            lsNextForm = TERMS_CONDITIONS; // Record Not found in DB, and
                                           // FName,LN exist in LDAP
        } else {
            lsNextForm = MISSING_PROFILE_INFO;
        }
        lsMissingInfoControllerPath = aoRequest.getScheme()
                + "://"
                + aoRequest.getServerName()
                + ":"
                + aoRequest.getServerPort()
                + aoRequest.getContextPath()
                + ApplicationConstants.PORTAL_URL
                + "&_pageLabel=portlet_hhsweb_portal_page_missing_profile&next_form="
                + lsNextForm
                + "&accoutRequestmodule=accoutRequestmodule&useremail="
                + asUserEmailId + "&firstName=" + asUserFirstName
                + "&lastName=" + asUserLastName + "&middleName="
                + asUserMiddleName + "&userDn=" + asStaffUserDN
                + "&nextAction=" + asNextAction + "&ldap="
                + asUserProfileExistInLdap + "&db=" + asUserProfileExistInDB;
        LOG_OBJECT.Debug("MI$$3 - lsMissingInfoControllerPath "
                + lsMissingInfoControllerPath);
        try {
            aoRequest.getPortletSession().setAttribute("MissinProfileHeader",
                    "MissinProfileHeader", PortletSession.APPLICATION_SCOPE);
            aoResponse.sendRedirect(lsMissingInfoControllerPath);
            abMissingControllerFlag = true;
        } catch (IOException aoIOExp) {
            throw new ApplicationException(
                    "Not able to redirect  to missing profile terms and condition screen after validating user.",
                    aoIOExp);
        }
        return abMissingControllerFlag;
    }

    /**
     * This method creates role mapping for user
     * 
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param aoUserBean
     *            UserBean with user details (user name, role, etc..)
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
    private void createRoleMappingMap(PortletSession aoSession,   UserBean aoUserBean) throws ApplicationException 
    {
        List loCompoRoleMappingList = (List) createRoleMappingMap(aoUserBean);
        Map loRoleComponentMap = CommonUtil.getComponentRoleMap(loCompoRoleMappingList);
        aoSession.setAttribute("roleMappingMap", loRoleComponentMap, PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_ROLE,  aoUserBean.getMsRole(), PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_PERMISSION_LEVEL,  aoUserBean.getMsPermissionLevel(), PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute( ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE, aoUserBean.getMsPermissionType(),  PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute("getUserRoles", aoUserBean,  PortletSession.APPLICATION_SCOPE);

    }

    /**
     * This method redirects city user to home page
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @param aoResponse
     *            decides the next execution flow
     * @param aoSBOrg
     *            organization of user
     * @param aoSBOrgType
     *            organization type of user
     * @throws ApplicationException
     */
    private void redirectToHomePage(ActionRequest aoRequest,  ActionResponse aoResponse, StringBuilder aoSBOrg, StringBuilder aoSBOrgType) throws ApplicationException 
    {
    	LOG_OBJECT.Debug("redirectToHomePage");
    	String lsUserRole = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
        if (aoSBOrg.toString().equalsIgnoreCase("city")
                || aoSBOrgType.toString().equalsIgnoreCase("city_org")) 
        {
            String lsTaskHomePagePath = aoRequest.getScheme() + "://"
                    + aoRequest.getServerName() + ":"
                    + aoRequest.getServerPort() + aoRequest.getContextPath()
                    + ApplicationConstants.PORTAL_URL
                    + "&_pageLabel=portlet_hhsweb_portal_page_city_home";
            
            try {
                aoResponse.sendRedirect(lsTaskHomePagePath);
            } catch (IOException aoExp) {
                throw new ApplicationException("Not able to redirect on task home page after validating user.", aoExp);
            }
        } else if (aoSBOrg.toString().equalsIgnoreCase("agency")
                || aoSBOrgType.toString().equalsIgnoreCase("agency_org")) 
        {
            // portlet_hhsweb_portal_page_agency_home
            String lsAgencyHomePagePath = aoRequest.getScheme() + "://"
                    + aoRequest.getServerName() + ":"
                    + aoRequest.getServerPort() + aoRequest.getContextPath()
                    + ApplicationConstants.PORTAL_URL + "&_pageLabel=";
            
            if (lsUserRole != null  && (lsUserRole.equalsIgnoreCase("Manager") || lsUserRole.equalsIgnoreCase("Staff"))) 
            {
                lsAgencyHomePagePath = lsAgencyHomePagePath.concat("portlet_hhsweb_agency_r1");

            } else {
                lsAgencyHomePagePath = lsAgencyHomePagePath.concat("portlet_hhsweb_portal_page_agency_home");
            }
            
            try {
                aoResponse.sendRedirect(lsAgencyHomePagePath);
            } catch (IOException aoExp) {
                throw new ApplicationException( "Not able to redirect on Agency home page after validating user.", aoExp);
            }
        } else 
        {
            String lsProviderHomePagePath = aoRequest.getScheme() + "://"
                    + aoRequest.getServerName() + ":"
                    + aoRequest.getServerPort() + aoRequest.getContextPath()
                    + ApplicationConstants.PORTAL_URL
                    + "&_pageLabel=portlet_hhsweb_portal_page_provider_home";
            
            try {
                aoResponse.sendRedirect(lsProviderHomePagePath);
            } catch (IOException aoExp) {
                throw new ApplicationException("Not able to redirect on provider home page after validating user.", aoExp);
            }
        }
    }

    /**
     * This method sets user variables in session <li>This method was added in
     * R4</li>
     * 
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @param asEmail
     *            email id of user
     * @param aoSBRole
     *            role of user
     * @param aoSBOrg
     *            organization of user
     * @param aoSBOrgType
     *            organization type of user
     * @param aoSBUserId
     *            unique id of user
     * @param aoSBOrgName
     *            organization name of user
     * @param aoUserHashMap
     *            has fields containing name, role, userid, emailid, etc..
     * @param asUserDN
     *            user dn(unique against email id)
     * @param aoUserBean
     *            UserBean with user details (user name, role, etc..)
     * @param aoChannel
     *            Channel Object that needs to be set to send and receive values
     *            across transaction
     * @param aoUserSession
     *            P8UserSession (file net)
     * @throws ApplicationException
     */
    @SuppressWarnings({ "unchecked" })
    private void settingSessionVariables(PortletSession aoSession,
            String asEmail, StringBuilder aoSBRole, StringBuilder aoSBOrg,
            StringBuilder aoSBOrgType, StringBuilder aoSBUserId,
            StringBuilder aoSBOrgName, HashMap<String, String> aoUserHashMap,
            String asUserDN, String aoOversightFlag, UserBean aoUserBean,
            Channel aoChannel, P8UserSession aoUserSession)   throws ApplicationException 
           
    {
    	 LOG_OBJECT.Debug("**************************** start of settingSessionVariables *******************  ");
    	
    	LOG_OBJECT.Debug("KEY_SESSION_USER_ID = aoSBUserId.toString(): "+aoSBUserId.toString());
    	String lsSessionTimeOut = PropertyLoader.getProperty( ApplicationConstants.HHS_PROPERTY_FILE_PATH, "SESSION_TIMEOUT");
        aoSession.setAttribute("sessionTimeOutValue", lsSessionTimeOut, PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute("sessionTimeOutValueLogin", true,   PortletSession.APPLICATION_SCOPE);
        // it fetches user profile information and adds to session
        aoSession.setAttribute("getUserRoles", aoUserBean,  PortletSession.APPLICATION_SCOPE);
        LOG_OBJECT.Debug("getUserRoles: "  + (String) aoSession.getAttribute("getUserRoles"));
        aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ID, aoSBUserId.toString(), PortletSession.APPLICATION_SCOPE);
        LOG_OBJECT.Debug("KEY_SESSION_USER_ID: "  + (String) aoSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID));
        // start QC 8914 R 7.2.0 read only role - save oversight flag in session
        // for agency and city user
        LOG_OBJECT.Debug("aoOversightFlag: " + aoOversightFlag);
        if (null != aoOversightFlag && aoOversightFlag != "") 
        {
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG, aoOversightFlag, PortletSession.APPLICATION_SCOPE);
            LOG_OBJECT.Debug("KEY_SESSION_OVERSIGHT_FLAG: "  + (String) aoSession.getAttribute( ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG,
                            																	PortletSession.APPLICATION_SCOPE));
        }

        // end QC 8914 R 7.2.0
        
        // QC 9205 R 8.0.0 Session set up later
        /*
        if (null != aoSBOrgType && !aoSBOrgType.toString().equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)) 
        {
        	LOG_OBJECT.Debug("2129===setUserIdInCache()) : "+aoSBUserId.toString());
        	setUserIdInCache(aoSBUserId.toString(), aoSession.getId());
        	
        }
        */
        UserThreadLocal.setUser(aoSBUserId.toString());
        if (null != aoSBOrgType  && ApplicationConstants.CITY_ORG.equalsIgnoreCase(aoSBOrgType.toString())) 
        {
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,  aoUserBean.getMsUserName(),  PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute("UserMap", aoUserHashMap,  PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,  aoSBRole.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,   aoSBOrg.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,   aoSBOrgType.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,   aoSBOrgName.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,   aoUserBean.getMsUserEmail(),   PortletSession.APPLICATION_SCOPE);
            
            // QC 8914 R 7.2.0 - read only role
            if ("1".equalsIgnoreCase(aoOversightFlag)) 
            {
                aoSession.setAttribute( ApplicationConstants.KEY_SESSION_ROLE_CURRENT, ApplicationConstants.ROLE_OBSERVER, PortletSession.APPLICATION_SCOPE);
                LOG_OBJECT.Debug("KEY_SESSION_ROLE_CURRENT: "  + (String) aoSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE));
            }
            LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  handleActionRequestInternal \n  <>  "
                            + "    KEY_SESSION_USER_NAME  ::  "
                            + aoUserBean.getMsUserName()
                            + "   KEY_SESSION_USER_ROLE   ::  "
                            + aoSBRole.toString()
                            + "   KEY_SESSION_USER_ORG    ::  "
                            + aoSBOrg.toString()
                            + "   KEY_SESSION_ORG_TYPE   ::  "
                            + aoSBOrgType.toString()
                            + "   KEY_SESSION_ORG_NAME   ::  "
                            + aoSBOrgName.toString()
                            + "   KEY_SESSION_EMAIL_ID   ::  "
                            + aoUserBean.getMsUserEmail());
        } else 
        {
            if (null != aoSBOrgType  && null != aoSBOrgType.toString()  && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(aoSBOrgType.toString())) 
            {
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME, aoUserBean.getMsUserName(), PortletSession.APPLICATION_SCOPE);
            }
            
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, aoSBRole.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,  aoSBOrg.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,  aoSBOrgType.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,  aoSBOrgName.toString(), PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID,   asEmail, PortletSession.APPLICATION_SCOPE);
            aoSession.setAttribute("userDN", asUserDN, PortletSession.APPLICATION_SCOPE);
            // start QC 8914 R 7.2.0 - read only role
            if ("1".equalsIgnoreCase(aoOversightFlag)) 
            {
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID_ORIGINAL, asEmail, PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE_ORIGINAL, aoSBOrgType.toString(),  PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL, aoSBOrgName.toString(),  PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_ORIGINAL, aoSBRole.toString(), PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT,  aoSBRole.toString(), PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_DN_ORIGINAL, asUserDN, PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ID_ORIGINAL, aoSBUserId.toString(),PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG_ORIGINAL, aoSBOrg.toString(), PortletSession.APPLICATION_SCOPE);
                aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_NAME_ORIGINAL, aoUserBean.getMsUserName(), PortletSession.APPLICATION_SCOPE);

                LOG_OBJECT.Debug("KEY_SESSION_ORG_TYPE_ORIGINAL: " + (String)aoSession.getAttribute( ApplicationConstants.KEY_SESSION_ORG_TYPE_ORIGINAL,
                                                PortletSession.APPLICATION_SCOPE));
                LOG_OBJECT.Debug("KEY_SESSION_ROLE_ORIGINAL: " + (String) aoSession.getAttribute( ApplicationConstants.KEY_SESSION_ROLE_ORIGINAL,
                                PortletSession.APPLICATION_SCOPE));
            }
            // end QC 8914 R 7.2.0 - read only role
            LOG_OBJECT.Debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  handleActionRequestInternal \n  <>  "
                            + "  KEY_SESSION_USER_NAME   ::  "
                            + aoUserBean.getMsUserName()
                            + "  KEY_SESSION_USER_ROLE    ::  "
                            + aoSBRole.toString()
                            + "  KEY_SESSION_USER_ORG   ::  "
                            + aoSBOrg.toString()
                            + "  KEY_SESSION_ORG_TYPE    ::  "
                            + aoSBOrgType.toString()
                            + "  KEY_SESSION_ORG_NAME   ::  "
                            + aoSBOrgName.toString()
                            + "  KEY_SESSION_EMAIL_ID    ::  "
                            + aoUserBean.getMsUserEmail()
                            + "  userDN    ::  " + asUserDN);

        }

        String lsAppSettingMapKey = P8Constants.PROPERTY_CE_DOCUMENT_VAULT_COMPONET_NAME + "_" + P8Constants.DOCUMENT_VAULT_ALLOWED_OBJECT_PER_PAGE;
        
        HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
        
        aoUserSession.setObjectsAllowedPerPage(loApplicationSettingMap.get(lsAppSettingMapKey));
        aoSession.setAttribute("P8FilenetSession", aoUserSession, PortletSession.APPLICATION_SCOPE);

        // Release 5 Made changes for web trends
        String lsAppSettingMapKeyWebtrends = HHSR5Constants.ALLOW_WEBTRENDS  + "_" + HHSR5Constants.FLAG;

        aoSession.setAttribute(HHSR5Constants.WEBTRENDS_FLAG, loApplicationSettingMap.get(lsAppSettingMapKeyWebtrends),
                PortletSession.APPLICATION_SCOPE);
        LOG_OBJECT.Debug("**************************** end of settingSessionVariables*************** \n  ");
    }

    /**
     * This method removes all the variables that are in Application
     * session(portlet session) <li>This method was updated in R4</li>
     * 
     * @param aoSession
     *            PortletSession to set variables in application scope
     * @throws ApplicationException
     */
    protected void removeSessionVariables(PortletSession aoSession)   throws ApplicationException 
    {
        aoSession.removeAttribute("userValidated", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("isForUpdate",  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("isFromPage", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("user_roles",  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_APP_ID, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.DOM_FOR_EDIT);
        aoSession.removeAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("roleMappingMap", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ID, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE,  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("MissinProfileHeader", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("userDN", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("UserMap", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("RegisterNycIdBean", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("getUserRoles", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("roleMappingMap", PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute("HomePage");
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_VALIDATED,  PortletSession.APPLICATION_SCOPE);
        aoSession.setAttribute("sessionTimeOutValueLogin", false,  PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.CAPCHA_REQUIRED,  PortletSession.APPLICATION_SCOPE);
        // begin QC 8914 R 7.2.0
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ROLE_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ROLE_CURRENT, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ID_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_DN_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_NAME_ORIGINAL,PortletSession.APPLICATION_SCOPE);
        aoSession.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ORG_ORIGINAL, PortletSession.APPLICATION_SCOPE);
        // end QC 8914 R 7.2.0
    }

    /**
     * This method retrieves role for user
     * 
     * @param aoUserId
     *            unique id of user
     * @param aoPassword
     *            password of user
     * @return Boolean status after authentication process
     * @throws ApplicationException
     */
    private boolean authenticateUser(String aoUserId, String aoPassword)
            throws ApplicationException {
        boolean lbAutheticateUser = false;
        UserBean loUserBean = new UserBean();
        Channel loChannelObj = new Channel();
        loUserBean.setMsUserId(aoUserId);
        loUserBean.setMsPassword(aoPassword);
        loChannelObj.setData("aoUserBean", loUserBean);
        TransactionManager.executeTransaction(loChannelObj, "authenticateUser");
        lbAutheticateUser = (Boolean) loChannelObj
                .getData("lbAuthenticateUser");

        return lbAutheticateUser;

    }

    /**
     * This method retrieves role for user
     * 
     * @param aoUserId
     *            unique id of user
     * @param aoPassword
     *            password of user
     * @return UserBean with user details (user name, role, etc..)
     * @throws ApplicationException
     */
    private UserBean getUserRole(String aoUserId, String aoPassword)
            throws ApplicationException {
        UserBean loUserBean = new UserBean();
        Channel loChannelObj = new Channel();
        loUserBean.setMsLoginId(aoUserId);
        loUserBean.setMsPassword(aoPassword);
        loChannelObj.setData("aoUserBean", loUserBean);
        TransactionManager.executeTransaction(loChannelObj, "fetchUserDetails");
        UserBean loUseRole = (UserBean) loChannelObj.getData("loUserRoleBean");
        return loUseRole;

    }

    /**
     * This method creates role mapping for user
     * 
     * @param aoUserBean
     *            UserBean with user details (user name, role, etc..)
     * @return List component role mapping list with component id and role
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
    private List createRoleMappingMap(UserBean aoUserBean)
            throws ApplicationException {
        List loCompoRoleMappingList = null;
        Channel loChannelObj = new Channel();
        loChannelObj.setData("userBean", aoUserBean);
        TransactionManager.executeTransaction(loChannelObj,
                "roleComponentMapping");
        loCompoRoleMappingList = (List) loChannelObj
                .getData("loCompoRoleMappingList");
        return loCompoRoleMappingList;

    }
	
	/* QC9713 */
	private void redirecttoLogoutPage(ActionRequest aoRequest, ActionResponse aoResponse, String msg) throws ApplicationException {
		LOG_OBJECT.Debug("redirect to logout...");
		String logoutPagePath;
		StringBuilder sb = new StringBuilder();
		
		sb.append(aoRequest.getScheme());		
		sb.append("://");
		sb.append(aoRequest.getServerName());
		sb.append(":");
		sb.append(aoRequest.getServerPort());
		sb.append(aoRequest.getContextPath());		
		sb.append(ApplicationConstants.LOGOUT_REDIRECT_URL);		
		
		if(msg != null && msg.trim().length() > 0){
			sb.append("&msg=");
			sb.append(msg);	
		}
		
		logoutPagePath = sb.toString();

		LOG_OBJECT.Debug("redirecting logoutPagePath -> " + logoutPagePath);

		try {
			aoResponse.sendRedirect(logoutPagePath);
		} catch (IOException e) {
			throw new ApplicationException("Not able to redirect logout page.", e);
		}
	}

    /**
     * This method validates token after new user is registered in NYC
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @return Map loValidateEmailTokenServiceOutMap has field containing status
     *         of transaction (success or failure)
     * @throws ApplicationException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map validateEmailToken(RenderRequest aoRequest)
            throws ApplicationException {
        String loToken = (String) PortalUtil.parseQueryString(aoRequest,
                "emailToken");
        String loEmailAddress = (String) PortalUtil.parseQueryString(aoRequest,
                "emailAddress");
        Channel loChannelObj = new Channel();
        Map loTokenMap = new HashMap();
        loTokenMap.put("emailToken", loToken);
        loTokenMap.put("emailAddress", loEmailAddress);
        loChannelObj.setData("aoTokenMap", loTokenMap);
        Map loValidateEmailTokenServiceOutMap = null;
        TransactionManager.executeTransaction(loChannelObj,
                "validateEmailToken");
        loValidateEmailTokenServiceOutMap = (Map) loChannelObj
                .getData("aoValidEmailTokenHM");
        return loValidateEmailTokenServiceOutMap;
    }

    /**
     * This method fetches details for city user(name, role, userid, emailid,
     * etc..)
     * 
     * Changes made as a part of Enhancement #6280 for Release 3.3.0
     * 
     * @return HashMap<String, String> has fields containing name, role, userid,
     *         emailid, etc..
     * @throws ApplicationException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private HashMap<String, String> fetchCityUserDetails()  throws ApplicationException {
        // Start || Changes made as a part of Enhancement #6280 for Release
        // 3.3.0
        LinkedHashMap<String, String> loUserHashMap = new LinkedHashMap<String, String>();
        // End || Changes made as a part of Enhancement #6280 for Release 3.3.0
        StaffDetails loUserDetails = null;
        List<StaffDetails> loCityUserDetails = null;
        Channel loChannel = new Channel();
        TransactionManager.executeTransaction(loChannel, "fetchCityUserDetails");
        loCityUserDetails = (List<StaffDetails>) loChannel.getData("masterUserList");

        Iterator loItr = loCityUserDetails.iterator();
        while (loItr.hasNext()) {
            loUserDetails = (StaffDetails) loItr.next();
            if (!loUserDetails.getMsUserType().toLowerCase().contains(ApplicationConstants.AGENCY)) 
            {
                if (loUserDetails.getMsStaffRole() != null && !(loUserDetails.getMsStaffRole().equalsIgnoreCase("staff"))) 
                {
                    loUserDetails.setMsStaffRole("manager");
                    /* [Start] R7.2.0 QC9055 Add indicator for Access control */
                    loUserHashMap.put(loUserDetails.getMsStaffRole() + "|"  + loUserDetails.getMsStaffId(),
                            		  loUserDetails.getMsStaffFirstName() + " " + loUserDetails.getMsStaffLastName());
                    /* [End] R7.2.0 QC9055 Add indicator for Access control */
                }
            }
        }

        return loUserHashMap;
    }

    /**
     * This method validates token after email is changed(home account
     * maintenance controller, manager user details flow)
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @return Map loValidateEmailTokenServiceOutMap has field containing status
     *         of transaction (success or failure)
     * @throws ApplicationException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map tokenvalidationafteremailchanged(RenderRequest aoRequest)
            throws ApplicationException {
        String lsToken = (String) PortalUtil.parseQueryString(aoRequest,
                "emailToken");
        String lsNewEmailAddress = (String) PortalUtil.parseQueryString(
                aoRequest, "newEmailAddress");
        String lsOldEmailAddress = (String) PortalUtil.parseQueryString(
                aoRequest, "oldEmailAddress");
        Channel loChannelObj = new Channel();
        Map loTokenMap = new HashMap();
        loTokenMap.put("lsToken", lsToken);
        loTokenMap.put("lsNewEmail", lsNewEmailAddress);
        loTokenMap.put("lsOldEmail", lsOldEmailAddress);
        loChannelObj.setData("aoEmailTokenMap", loTokenMap);
        // calls the db to update in staff details and organisation table and
        // after that
        // calls the web service to update email after validation
        Map loValidateEmailTokenServiceOutMap = null;
        TransactionManager.executeTransaction(loChannelObj,
                "updateNYCEmailTokenValidation");
        loValidateEmailTokenServiceOutMap = (Map) loChannelObj
                .getData("aoEmailUpdateRtrndMap");

        return loValidateEmailTokenServiceOutMap;
    }

    /**
     * This method decrypt the encryped username and password
     * 
     * @param aoRequest
     *            to get screen parameters and next action to be performed
     * @return lsOutput decryped String
     */
    public String decrypt(String asEncryptedStr) {

        byte[] lsOutput;
        /* [Start] R7.8.0 replace to Aphach common */
        /*
         * try { lsOutput = new BASE64Decoder().decodeBuffer(asEncryptedStr);
         * return new String(lsOutput); } catch (IOException aoExc) {
         * LOG_OBJECT.Error("Exception occur while decrypting ::: " +
         * asEncryptedStr, aoExc); }
         */
        lsOutput = new Base64().decode(asEncryptedStr);

        return new String(lsOutput);
        /* [End] R7.8.0 replace to Aphach common */

    }

    /**
     * @param aoRequest
     * @param loChannelObj
     * @param loRegisterNycIdBean
     * @param lsEmailToken
     * @param lsEmailAddress
     * @throws ApplicationException
     */
    private void sendNT034Notification(RenderRequest aoRequest,
            Channel loChannelObj, RegisterNycIdBean loRegisterNycIdBean,
            String lsEmailToken, String lsEmailAddress)
            throws ApplicationException {
        String lsUserName = loRegisterNycIdBean.getMsFirstName() + " "
                + loRegisterNycIdBean.getMsLastName();
        if (loRegisterNycIdBean.getMsMiddleName() != null
                && !"".equalsIgnoreCase(loRegisterNycIdBean.getMsMiddleName())) {
            lsUserName = loRegisterNycIdBean.getMsFirstName() + " "
                    + loRegisterNycIdBean.getMsMiddleName() + " "
                    + loRegisterNycIdBean.getMsLastName();
        }
        String lsEmailLink = aoRequest.getScheme()
                + "://"
                + aoRequest.getServerName()
                + ":"
                + aoRequest.getServerPort()
                + aoRequest.getContextPath()
                + ApplicationConstants.PORTAL_URL
                + "&_pageLabel=portlet_hhsweb_portal_login_page&validEmailToken=validEmailToken&emailToken="
                + lsEmailToken + "&emailAddress=" + lsEmailAddress;
        
        LOG_OBJECT.Debug("call to webservice for new user registration, coming in passed block, lsEmailLink: "  + lsEmailLink);

        Map loMapForNotification = new HashMap();
        List<String> loNotificationAlertList = new ArrayList<String>();
        loNotificationAlertList.add("NT034");
        loMapForNotification.put(HHSConstants.NOTIFICATION_ALERT_ID,
                loNotificationAlertList);

        NotificationDataBean loNotificationDataBean = new NotificationDataBean();
        HashMap<String, String> loLinkMap = new HashMap<String, String>();
        loLinkMap.put("LINK", lsEmailLink);
        loNotificationDataBean.setLinkMap(loLinkMap);
        loNotificationDataBean.setAgencyLinkMap(loLinkMap);

        HashMap loParamMap = new HashMap();
        loParamMap.put("USERNAME", lsUserName);
        loMapForNotification.put(
                TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loParamMap);
        loMapForNotification.put(ApplicationConstants.ENTITY_TYPE,
                "new_user_email");
        loMapForNotification
                .put(ApplicationConstants.ENTITY_ID, lsEmailAddress);
        loMapForNotification.put(HHSConstants.CREATED_BY_USER_ID,
                HHSConstants.SYSTEM_USER);
        loMapForNotification.put(HHSConstants.MODIFIED_BY,
                HHSConstants.SYSTEM_USER);
        loMapForNotification.put("NT034", loNotificationDataBean);
        loChannelObj.setData("loHmNotifyParam", loMapForNotification);
        LOG_OBJECT.Debug("call to webservice for new user registration, loMapForNotification: " + loMapForNotification);
        TransactionManager.executeTransaction(loChannelObj,
                "insertNotificationDetail");
        LOG_OBJECT.Debug("notification sent ");
    }

    /**
     * This method is used to generate New Token and returns map containing
     * transactionStatus, transactionMessage and newToken
     * 
     * @param asUserEmailId
     *            String
     * @return Map loNewTokenMap
     * @throws ApplicationException
     *             - if any exception occurs
     */
    private Map generateNewToken(String asUserEmailId)  throws ApplicationException 
    {
        Channel loChannelObj = new Channel();
        loChannelObj.setData("asUserEmailId", asUserEmailId);
        Map loRegisterServiceOutMap = null;
        Map loNewTokenMap = new HashMap();
        try {
            TransactionManager.executeTransaction(loChannelObj,  "newTokenGeneration");
            loRegisterServiceOutMap = (Map) loChannelObj.getData("aoNewTokenMap");
            String lsStatus = (String) loRegisterServiceOutMap.get("serviceStatus");
            List loOutputList = (List) loRegisterServiceOutMap.get("serviceOutput");
            String lsToken = (String) loOutputList.get(0);
            if ("error".equalsIgnoreCase(lsStatus)) 
            {
                loNewTokenMap.put("transactionStatus", "failed");
                loNewTokenMap.put("transactionMessage",  LoginController.REQUEST_COULD_NOT_BE_COMPLETED);

            } else if ("success".equalsIgnoreCase(lsStatus)) {
                if (lsToken != null && !"".equalsIgnoreCase(lsToken)
                        && lsToken.indexOf("[") >= 0) 
                {
                    lsToken = lsToken.substring(1, lsToken.length() - 1);
                    loNewTokenMap.put("newToken", lsToken);
                    loNewTokenMap.put("transactionStatus", "passed");
                    loNewTokenMap.put("transactionMessage", LoginController.NEW_TOKEN_GENERATED);
                }
            }

        } catch (ApplicationException aoFbAppEx) {
            LOG_OBJECT.Error("Error occured while generating new token  ",
                    aoFbAppEx);
        }
        return loNewTokenMap;
    }

    /**
     * This method sets the current user detail in cache, the session to be
     * cleaned
     * 
     * @param asUserId
     *            - Current User id
     * @param asSessionId
     *            - Current Session Id
     */
    @SuppressWarnings("unchecked")
    private void setUserIdInCache(String asUserId, String asSessionId) {
        try {
        	
            Map<String, String> loDataMap = (Map<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject("sessionUserDetailsCache");
           
            if (loDataMap != null)
            {  
                if (loDataMap.containsKey(asUserId)) 
                {  
                    List<String> loList = (List<String>) BaseCacheManagerWeb.getInstance().getCacheObject("sessionListToRemove");
                    
                    if (loList == null) 
                    {
                        loList = new ArrayList<String>();
                    }
                    loList.add(loDataMap.get(asUserId));
                    BaseCacheManagerWeb.getInstance().putCacheObject("sessionListToRemove", loList);
                   
                }
            } 
            else 
            {
                loDataMap = new HashMap<String, String>();
            }
            loDataMap.put(asUserId, asSessionId);
            BaseCacheManagerWeb.getInstance().putCacheObject("sessionUserDetailsCache", loDataMap);
            
            
        } 
        catch (ApplicationException aoAppExp) 
        {
            LOG_OBJECT.Error("Error occured while setUserIdInCache  ", aoAppExp);
        }
    }

    /**
     * This method redirects Provider user to Select Organization screen after
     * login to select Organization for Login.
     * 
     * @param aoRequest
     *            Action Request
     * @param aoResponse
     *            Action Response
     * @throws ApplicationException
     */
    private void redirectToSelectOrganization(ActionRequest aoRequest,
            ActionResponse aoResponse) throws ApplicationException {
        String lsSelectOrgPagePath = "";
        try {
            lsSelectOrgPagePath = aoRequest.getScheme()
                    + "://"
                    + aoRequest.getServerName()
                    + ":"
                    + aoRequest.getServerPort()
                    + aoRequest.getContextPath()
                    + ApplicationConstants.PORTAL_URL
                    + "&_urlType=render&_pageLabel=portlet_hhsweb_portal_page_chooseOrganization&render_action=selectOrganization";
            
           
            aoResponse.sendRedirect(lsSelectOrgPagePath);
        } catch (Exception loEx) {
            throw new ApplicationException(
                    "Not able to redirect on select organization page after validating user.",
                    loEx);
        }
    }

    // ** start QC 8914 R 7.2.1
    /**
     * This method retrieves Oversight_Flag for user
     * 
     * @param aoUserId
     *            unique id of user
     * @return Oversight_Flag
     * @throws ApplicationException
     */
    private String fetchCityAgencyUserOversightFlag(String aoUserId)
            throws ApplicationException {
        Channel loChannelObj = new Channel();
        loChannelObj.setData("asUserId", aoUserId);
        TransactionManager.executeTransaction(loChannelObj, "fetchCityAgencyUserOversightFlag");
        String loOversightFlag = (String) loChannelObj.getData("oversightFlag");
        return loOversightFlag;

    }

    // ** end QC 8914 R 7.2.1

    // ** [Start] QC 9165 R 7.8.0
    /**
     * This method retrieves email for user
     * 
     * @param aoUserId
     *            unique id of user
     * @return Oversight_Flag
     * @throws ApplicationException
     */
    private Map<String, String> extractAttributesFromSecurity(Subject subject) {
        Set<Object> allPrivatePrincipals = subject.getPrivateCredentials();
        HashMap<String, String> map = new HashMap<String, String>();

        for (Object principal : allPrivatePrincipals) 
        {
            // virtual user
            if (principal instanceof SAMLAttribute) 
            {
                SAMLAttribute attribute = (SAMLAttribute) principal;
                try {
                	
                    //Start  QC 9205 R 8.0.0 SAML Internal
                    if(attribute.getName().equalsIgnoreCase("groupMembership"))
                    {       String value = attribute.getValues().toString().replace(" ","");
                            value= value.replace("[","");
                            value= value.replace("]","");
                    		
                    		map.put(attribute.getName(), value) ;
                    		
                    }
                    else
                    {
                    	// End QC 9205 R 8.0.0 SAML Internal
                    	map.put(attribute.getName(), attribute.getValues().toArray()[0].toString()) ;
                    	
                    }
                   
                } catch (Exception e) {
                    LOG_OBJECT.Info("************[AttrName]:"+ attribute.getName() + "  NULLLLL [AttrValue]"+ attribute.getValues());
                    map.put(attribute.getName(), ApplicationConstants.EMPTY_STRING);
                }
            }
        }
        
        return map;
    }

    private void setSamlReqParameter(HttpServletRequest loReq) throws ApplicationException {
    	//LOG_OBJECT.Debug("==========  setSamlReqParameter ============");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_URL_TYPE, ApplicationConstants.SAML_LOGIN_PORTAL_URL_TYPE_VALUE);
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_HDN_NYC_NYCID_CYPERED, "cHJvdmlkZXJybUBtYWlsaW5hdG9yLmNvbQ==");
        loReq.setAttribute( ApplicationConstants.SAML_LOGIN_PORTAL_NYC_NYCID_CYPERED, "");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTLET, ApplicationConstants.SAML_LOGIN_PORTLET);
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_NEXT_ACTION, ApplicationConstants.SAML_LOGIN_NEXT_ACTION_VALUE);
        //LOG_OBJECT.Debug("==========  SAML_LOGIN_NEXT_ACTION ::  "+ApplicationConstants.SAML_LOGIN_NEXT_ACTION_VALUE);
        loReq.setAttribute(ApplicationConstants.SAML_PORTAL_FLAG, "true");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTLET_WINDOW_LABEL, ApplicationConstants.SAML_LOGIN_PORTLET_WINDOW_LABEL_VALUE);
       
    }
    
    //QC 9205 R 8.0.0 : add parameters String loHhsOrigin, UserBean loUserBeanCity
    private boolean validateUserProfile( SAMLTknImpl aoSamlAttr, ActionRequest aoRequest, ActionResponse aoResponse, String loHhsOrigin, UserBean loUserBeanCity ) throws ApplicationException  
    {
    	LOG_OBJECT.Debug("##################  [validateUserProfile]  aoSamlAttr:  " + aoSamlAttr.toString());
    	LOG_OBJECT.Debug("##################  [validateUserProfile]  aoSamlAttr.isNycIdValid():  " + aoSamlAttr.isNycIdValid());
    	LOG_OBJECT.Debug("##################  [validateUserProfile]  aoSamlAttr.isProfileComplete():  " + aoSamlAttr.isProfileComplete());
    	LOG_OBJECT.Debug("##################  [validateUserProfile]  aoSamlAttr.getEntryDN):  " + aoSamlAttr.getEntryDN());
        //[Start] QC 9528 R 8.5.0 - Null Pointer Exception thrown when logging into Accelerator Internal site
    	if(null != loHhsOrigin && loHhsOrigin.contains("CITY") && 
    			(aoSamlAttr.getEntryDN() == null || aoSamlAttr.getEntryDN().isEmpty()) )
    	{	
    		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG, ApplicationConstants.SAML_CITY_ENTRYDN_ERR_MSG);
    		LOG_OBJECT.Debug("##################  [validateUserProfile]  return ERROR_MSSG  " + ApplicationConstants.SAML_CITY_ENTRYDN_ERR_MSG );
    		return false;
    	}
    	 //[End] QC 9528 R 8.5.0 - Null Pointer Exception thrown when logging into Accelerator Internal site
        
    	
    	
    	if( !aoSamlAttr.isNycIdValid() )
        {
        	if(null != loHhsOrigin && loHhsOrigin.contains("DMZ"))
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_PROFILE_ERR_NO_EMAIL);
        		LOG_OBJECT.Debug("##################  [validateUserProfile]  return ERROR_MSSG  " + ApplicationConstants.SAML_PROFILE_ERR_NO_EMAIL );
        	}
        	else if(null != loHhsOrigin && loHhsOrigin.contains("CITY"))
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_CITY_PROFILE_ERR_MSG);
        		LOG_OBJECT.Debug("##################  [validateUserProfile]  return ERROR_MSSG  " + ApplicationConstants.SAML_CITY_PROFILE_ERR_MSG );
        	}
            return false;
        }

        if( !aoSamlAttr.isProfileComplete() ){
            
        	if(null != loHhsOrigin && loHhsOrigin.contains("DMZ"))
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_PROFILE_ERR_NO_NAME  );
                LOG_OBJECT.Debug("##################  [validateUserProfile]  return ERROR_MSSG  " + ApplicationConstants.SAML_PROFILE_ERR_NO_NAME );
        	}
        	else if(null != loHhsOrigin && loHhsOrigin.contains("CITY"))
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_CITY_PROFILE_ERR_MSG);
        		LOG_OBJECT.Debug("##################  [validateUserProfile]  return ERROR_MSSG  " + ApplicationConstants.SAML_CITY_PROFILE_ERR_MSG );
        	}
 
            return false;
        }
        
        // Start R 8.0.0 Nilesh
        HashMap<Boolean,Integer> map = aoSamlAttr.isGroupMembershipComplete();
        LOG_OBJECT.Debug("2722==== membership map :: "+ map);
        //if( !aoSamlAttr.isGroupMembershipComplete())
        Integer mwmbershipErrType = map.get(false); 	    
        if(mwmbershipErrType!=null)	
        {
			/* QC9713 show the same error messages to both the users */
			/*
        	if(mwmbershipErrType==0) // NOT HHSACCELERATOR GROUP
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_DEPROVISION_USER_ERR  );
        		LOG_OBJECT.Debug("##################  [DEPROVISION of User]  return ERROR_MSSG  " + ApplicationConstants.SAML_DEPROVISION_USER_ERR );
        	}
        	else
        	{	
        		aoResponse.setRenderParameter(ApplicationConstants.ERROR_MSSG,ApplicationConstants.SAML_MULTIGROUP_USER_ERR  );
        		LOG_OBJECT.Debug("##################  [MULTIGROUP of User]  return ERROR_MSSG  " + ApplicationConstants.SAML_MULTIGROUP_USER_ERR );
        	}
            // Start QC 9205 R 8.0.0 SAML Internal
            aoResponse.setRenderParameter("user_type", "");
   		    aoResponse.setRenderParameter("siteminderLogout", "siteminderLogout");
   		    aoResponse.setRenderParameter("siteminderLoginError", "siteminderLoginError");
            // End QC 9205 R 8.0.0 SAML Internal
			*/
			
			
			redirecttoLogoutPage(aoRequest, aoResponse, "mul");
			/* end of QC9713 */
			
        	return false;
        }
        // End R 8.0.0 Nilesh
        
        // [Start] QC 9205 R 8.0.0 SAML Internal
        
        //Check user profile to update  -- checkProviderNycIdUpdate  : CHECK_PROVIDER_NYC_ID_UPDATE
        //StaffDetails aoSD = new StaffDetails(aoSamlAttr.getLmGUID(), aoSamlAttr.getNycId() , aoSamlAttr.getFirstName(),aoSamlAttr.getLmMiddleName(), aoSamlAttr.getLastName());
               
        if(null != loHhsOrigin && loHhsOrigin.contains("DMZ"))	
        {
        	//[Start] QC 9205 R 8.0.0 SAML Internal
        	//Check user profile to update  -- checkProviderNycIdUpdate  : CHECK_PROVIDER_NYC_ID_UPDATE
            StaffDetails aoSD = new StaffDetails(aoSamlAttr.getLmGUID(), aoSamlAttr.getNycId() , aoSamlAttr.getFirstName(),aoSamlAttr.getLmMiddleName(), aoSamlAttr.getLastName());
            
        	//[End] QC 9205 R 8.0.0 SAML Internal
        	Channel loChannelObj = new Channel();
        	loChannelObj.setData(HHSConstants.NYC_STAFF_DETAILS_PARAM_KEY, aoSD );
        	TransactionManager.executeTransaction(loChannelObj, HHSConstants.CHECK_PROVIDER_NYC_ID_UPDATE);
        }
        // for City user :: different transaction
        else if(null != loHhsOrigin && loHhsOrigin.contains("CITY"))	
        {
        	//String lsLoginPagePath = "redirecthome";
            
        	try {
            	fetchCityAgencyUserDetailsFromSaml( aoSamlAttr, loUserBeanCity );
                } catch (ApplicationException aoExp) {
                	//aoResponse.setAttribute("user_type", "");
                	//aoResponse.setAttribute("siteminderLogout",  "siteminderLogout");
                	//aoResponse.setAttribute("siteminderLoginError", "siteminderLoginError");
                } catch (Exception aoExp) {
                	//aoResponse.setAttribute("user_type", "");
                	//aoResponse.setAttribute("siteminderLogout",   "siteminderLogout");
                	//aoResponse.setAttribute("siteminderLoginError", "siteminderLoginError");
                }
          } else {
            	//aoResponse.setAttribute("user_type", "");
            	//aoResponse.setAttribute("siteminderLogout", "siteminderLogout");
            	//aoResponse.setAttribute("siteminderLoginError", "siteminderLoginError");
        	  
           
        }
 // ** [End] QC 9165 R 7.8.0
        return true;
    }

//[Start] QC 9205 R 8.0.0 SAML Internal

/**
 * This method is for fetching/inserting CITY and AGENCY user's data from
 * database <li>This method was updated in R4</li>
 * 
 * @param aoRequest
 *            to get screen parameters and next action to be performed
 * @param aoSession
 *            PortletSession to set variables in application scope
 * @param aoHttpRequest
 * @param aoUserSession
 *            is the P8UserSession object
 * @return
 * @throws ApplicationException
 */
@SuppressWarnings({ "unchecked", "rawtypes" }) //ActionResponse aoResponse,
private void fetchCityAgencyUserDetailsFromSaml(SAMLTknImpl aoSamlAttr,  UserBean loUserBean ) throws ApplicationException 
{

	LOG_OBJECT.Debug("fetchCityAgencyUserDetailsFromSaml"); 
	
    javax.security.auth.Subject lsSubject = weblogic.security.Security.getCurrentSubject();
    Set<Principal> loAllPrincipals = lsSubject.getPrincipals();
    List loPrincipallist = new ArrayList();
    for (Principal loPrincipal : loAllPrincipals) 
    {
        loPrincipallist.add(loPrincipal.getName());
        LOG_OBJECT.Debug("siteminder lsRole1::: " + loPrincipal.getName());
    }
    
    //String lsLoginUser1 = (String) loPrincipallist.get(0), lsRole1 = (String) loPrincipallist.get(1);
    ///aoUserSession.setSubject(lsSubject);
    LOG_OBJECT.Debug("siteminder subject::: " + lsSubject);
    //LOG_OBJECT.Debug("siteminder lsLoginUser1::: " + lsLoginUser1);
    //LOG_OBJECT.Debug("siteminder lsRole1::: " + lsRole1);
    
    String lsCmUserDN =  aoSamlAttr.getEntryDN();
    String lsCmUserFirstName =  aoSamlAttr.getFirstName();
    String lsCmUserLastName =  aoSamlAttr.getLastName();
    String lsCmUserEmailAddress = aoSamlAttr.getLmMail();
    String lsGroupMembership = aoSamlAttr.getGroupMembership();
    String lsLoginUser = aoSamlAttr.getLmMail(); 
    // cn=hhsa_i_staff,ou=HHS-Accelerator,ou=DOITT,o=APPS]
    //cn=hhsa_i_agency_hra_CFO   
    String lsRole = null;
    String[] arrOfStr = lsGroupMembership.split(",", -2); 
    for (String a : arrOfStr) 
    {	
    	if(a.contains("cn=hhsa_i"))
    	{
    		lsRole = a.replace("cn=","");
    	    break;
    	}
    		
    }
    
    //====lsRole :: hhsa_i_agency_hra_CFO
    //UserBean loUserBean = new UserBean();

    // City user changes start
    if (null != lsCmUserFirstName && null != lsCmUserLastName) 
    {
        loUserBean.setMsUserName(lsCmUserFirstName.concat(" ").concat(
                lsCmUserLastName));
    }
    //LOG_OBJECT.Debug("====msUserName :: "+loUserBean.getMsUserName());
    loUserBean.setMsUserDN(lsCmUserDN);
    loUserBean.setMsUserEmail(lsCmUserEmailAddress);
    Map loUserDetailMap = null;
    Map loUserDetailUpdateMap = null;
    Channel loChannel = new Channel();
    loChannel.setData("asCmUserDN", lsCmUserDN);
    TransactionManager.executeTransaction(loChannel, "searchUserDnInUserDetails");
    loUserDetailMap = (Map) loChannel.getData("aoSiteMinderUserDetailMap");
    //LOG_OBJECT.Debug("====loSamlUserDetailMap :: "+loUserDetailMap);
    //?????
    
    //saml role ::: hhsa_i_agency_hra_CFO
    if (lsRole!=null && lsRole.toLowerCase().contains(ApplicationConstants.AGENCY)) {
        updateUserBeanAgency(lsRole, loUserBean);
    } 
    else if (lsRole!=null)
    {
        updateUserbeanCity(lsRole, loUserBean);
    }

    String lsCmUserRole = loUserBean.getMsRole();
    String lsCmUserType = loUserBean.getMsOrgType();

    // log
    if (null != loUserDetailMap
            && !loUserDetailMap.isEmpty()) {
        String lsFirstNameFromDB = (String) loUserDetailMap.get("FIRST_NAME");
        String lsLastNameFromDB = (String) loUserDetailMap.get("LAST_NAME");
        String lsEmailFromDB = (String) loUserDetailMap.get("EMAIL_ID");
        String lsUserIdFromDB = (String) loUserDetailMap.get("USER_ID");
        String lsUserRoleFromDB = (String) loUserDetailMap.get("USER_ROLE");
        String lsUserTypeFromDB = (String) loUserDetailMap.get("USER_TYPE");
        String lsOrganizationFromDB = (String) loUserDetailMap.get("ORGANIZATION_NAME");
        String lsActiveFlagFromDB = (String) loUserDetailMap.get("ACTIVE_FLAG");
        if (lsCmUserEmailAddress != null
                && lsLoginUser != null
                && lsCmUserEmailAddress.trim().equalsIgnoreCase(lsLoginUser.trim())) 
        {
            LOG_OBJECT.Debug("User DN found in Database for user :::  "
                            + lsCmUserEmailAddress + " and user dn:: "
                            + lsCmUserDN);
            // update into SiteMinder_User_Details
            loUserDetailUpdateMap = new HashMap();
            loUserDetailUpdateMap.put("FirstName", lsCmUserFirstName);
            loUserDetailUpdateMap.put("LastName", lsCmUserLastName);
            loUserDetailUpdateMap.put("EmailAddress", lsCmUserEmailAddress);
            loUserDetailUpdateMap.put("UserRole", lsCmUserRole);
            loUserDetailUpdateMap.put("UserType", lsCmUserType);
            loUserDetailUpdateMap.put("UserDN", lsCmUserDN);
            if (null != lsCmUserType  && lsCmUserType.toLowerCase().contains("agency")) 
            {
                loUserDetailUpdateMap.put("OrgName", loUserBean.getMsOrgId());
            } else {
                loUserDetailUpdateMap.put("OrgName", loUserBean.getMsOrgName());
            }
            loUserDetailUpdateMap.put("ModifiedBy",  lsUserIdFromDB);
            /*
            LOG_OBJECT.Debug("2930==========True or Fals:  : " + !(lsCmUserFirstName.equalsIgnoreCase(lsFirstNameFromDB)
                    && lsCmUserLastName.equalsIgnoreCase(lsLastNameFromDB)
                    && lsCmUserEmailAddress.equalsIgnoreCase(lsEmailFromDB)
                    && lsCmUserRole.equalsIgnoreCase(lsUserRoleFromDB) 
                    && lsCmUserType.equalsIgnoreCase(lsUserTypeFromDB)) );
            */
            if (null != lsCmUserFirstName
                    && null != lsCmUserLastName
                    && null != lsCmUserRole
                    && null != lsCmUserType
                    && (!(lsCmUserFirstName.equalsIgnoreCase(lsFirstNameFromDB)
                            && lsCmUserLastName.equalsIgnoreCase(lsLastNameFromDB)
                            && lsCmUserEmailAddress.equalsIgnoreCase(lsEmailFromDB)
                            && lsCmUserRole.equalsIgnoreCase(lsUserRoleFromDB) 
                            && lsCmUserType.equalsIgnoreCase(lsUserTypeFromDB)) 
                            || ApplicationConstants.ZERO.equalsIgnoreCase(lsActiveFlagFromDB))) 
            {
                LOG_OBJECT.Debug("Updating city user details table for user ... "
                                + lsCmUserEmailAddress
                                + " and user dn:: "
                                + lsCmUserDN);
                loChannel = new Channel();
                loChannel.setData("aoCmUserDetailMap", loUserDetailUpdateMap);
                TransactionManager.executeTransaction(loChannel, "updateRoleInCityUserDetails");
                LOG_OBJECT.Debug("Successfully Updated city user details table for user ... "
                                + lsCmUserEmailAddress
                                + " and user dn:: "
                                + lsCmUserDN);
            }

        } else {
            LOG_OBJECT.Debug("Logged in user email and email retrieved from siteminder does not match for user :: "
                            + lsCmUserEmailAddress
                            + " and user dn:: "
                            + lsCmUserDN);
            if (lsUserTypeFromDB.toLowerCase().contains("agency")) {
                loUserBean.setMsRole(lsUserRoleFromDB);
                loUserBean.setMsOrgId(lsOrganizationFromDB);
                loUserBean.setMsOrgName(FileNetOperationsUtils.getAgencyName(
                                        (TreeSet) BaseCacheManagerWeb.getInstance().getCacheObject( ApplicationConstants.AGENCY_LIST), lsOrganizationFromDB));
                loUserBean.setMsOrgType(ApplicationConstants.AGENCY_ORG);
            } else {
                loUserBean.setMsOrgName(ApplicationConstants.CITY);
                loUserBean.setMsOrgId(ApplicationConstants.CITY);
                loUserBean.setMsRole(lsUserRoleFromDB);
                loUserBean.setMsOrgType(ApplicationConstants.CITY_ORG);

            }
        }
        loUserBean.setMsUserId(lsUserIdFromDB);
        // City user update changes end

    } 
    else 
    {

        if (lsCmUserEmailAddress != null
                && lsLoginUser != null
                && !lsCmUserEmailAddress.trim().equalsIgnoreCase(lsLoginUser.trim())) 
        {
            LOG_OBJECT.Error("Can not insert city user details into database due to cookies issue. Cookies need to be clean for login user:: "
                            + lsCmUserEmailAddress
                            + " and email fetch from subject:: "
                            + lsLoginUser);
            throw new ApplicationException(
                    "Can not insert city user details into database due to cokkie issue. Cookies need to be clean for login user:: "
                            + lsCmUserEmailAddress
                            + " and email fetch from subject:: "
                            + lsLoginUser);
        }
        LOG_OBJECT.Debug("Inserting records in city user details table for user ... "
                        + lsCmUserEmailAddress
                        + " and user dn:: "
                        + lsCmUserDN);
        // insert into SiteMinder_User_Details
        loUserDetailMap = new HashMap();
        loUserDetailMap.put("FirstName", lsCmUserFirstName);
        loUserDetailMap.put("LastName", lsCmUserLastName);
        loUserDetailMap.put("EmailAddress", lsCmUserEmailAddress);
        loUserDetailMap.put("UserDN", lsCmUserDN);
        loUserDetailMap.put("UserRole", lsCmUserRole);
        loUserDetailMap.put("UserType", lsCmUserType);
        if (lsCmUserType.toLowerCase().contains("agency")) {
            loUserDetailMap.put("OrgName", loUserBean.getMsOrgId());
        } else {
            loUserDetailMap.put("OrgName", loUserBean.getMsOrgName());
        }
        loChannel = new Channel();
        String lsUserId = "";
        loChannel.setData("aoCmUserDetailMap", loUserDetailMap);
        TransactionManager.executeTransaction(loChannel, "insertIntoSiteMinderUserDetails");
        LOG_OBJECT.Debug("Successfully Inserted records in city user details table for user ... "
                        + lsCmUserEmailAddress
                        + " and user dn:: "
                        + lsCmUserDN);
        int liUserId = (Integer) loChannel.getData("liCurrentSeq");
        if (lsCmUserType.toLowerCase().contains("agency")) 
        {
            lsUserId = "agency_" + String.valueOf(liUserId);
        } 
        else 
        {
            lsUserId = "city_" + String.valueOf(liUserId);
        }
        LOG_OBJECT.Debug("lsUserId: " + lsUserId);
        loUserBean.setMsUserId(lsUserId);
    }
    // City user changes end
   // aoRequest.setAttribute("user_type", loUserBean.getMsOrgType());
    LOG_OBJECT.Debug("user_type: " + loUserBean.getMsOrgType());

    //HashMap<String, String> loUserHashMap = fetchCityUserDetails();
    
    loChannel = new Channel();
    StringBuilder loSBRole = new StringBuilder(), loSBOrg = new StringBuilder(), loSBOrgType = new StringBuilder(), loSBUserId = new StringBuilder(), loSBOrgName = new StringBuilder();
    loSBUserId.append(loUserBean.getMsUserId());
    loSBRole.append(loUserBean.getMsRole());
    loSBOrg.append(loUserBean.getMsOrgId());
    loSBOrgType.append(loUserBean.getMsOrgType());
    loSBOrgName.append(loUserBean.getMsOrgName());
    loUserBean.setMsLoginId(lsLoginUser);
    String oversightFlag = "";
    if (null != loUserBean.getMsOrgType()
            && ("agency_org".equalsIgnoreCase(loUserBean.getMsOrgType()) 
                || "city_org".equalsIgnoreCase(loUserBean.getMsOrgType()))) 
    {
        LOG_OBJECT.Debug("UserID: " + loSBUserId.toString());
        // QC 8914 R 7.2.0 - read only role - get oversight flag
        oversightFlag = fetchCityAgencyUserOversightFlag(loSBUserId.toString());
        LOG_OBJECT.Debug("OversigtFlag: " + oversightFlag);
       // aoRequest.setAttribute("user_type_original",  loUserBean.getMsOrgType());
    }
    LOG_OBJECT.Debug("lsUserId: " + loUserBean.getMsUserId());
    LOG_OBJECT.Debug("loSBRole: " + loUserBean.getMsRole());
    LOG_OBJECT.Debug("loSBOrg: " + loSBOrg);
    LOG_OBJECT.Debug("loSBOrgType: " + loSBOrgType);
    LOG_OBJECT.Debug("user_type: " + loUserBean.getMsOrgType());

    // QC 8914 R 7.2.0 - read only role - saved agency user in request and
    // session variables if oversight flag is 1
   /*
    aoRequest.setAttribute("user_type_original",  loUserBean.getMsOrgType());
    aoRequest.setAttribute("user_type", loUserBean.getMsOrgType());
   
    settingSessionVariables(aoSession, loUserBean.getMsUserEmail(),
            loSBRole, loSBOrg, loSBOrgType, loSBUserId, loSBOrgName,
            loUserHashMap, loUserBean.getMsUserDN(), oversightFlag,
            loUserBean, loChannel, aoUserSession);

    createRoleMappingMap(aoSession, loUserBean);
    aoSession.setAttribute(ApplicationConstants.KEY_SESSION_USER_VALIDATED,
            ApplicationConstants.TRUE, PortletSession.APPLICATION_SCOPE);
            
    */ 
    
}


    // ** [End] QC 9165 R 7.8.0

}
