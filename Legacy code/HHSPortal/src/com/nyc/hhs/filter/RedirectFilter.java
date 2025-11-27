package com.nyc.hhs.filter;

import gov.nyc.saml.SAMLAttribute;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import weblogic.security.Security;
import weblogic.servlet.security.ServletAuthentication;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.NotificationURLBean;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.security.SecurityTkn;
import com.nyc.hhs.security.impl.SAMLTknImpl;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class provides functionality of servlet filter that implements login
 * redirect
 * 
 */

public class RedirectFilter implements Filter
{


    static class FilteredRequest extends HttpServletRequestWrapper
    {

        /* These are the characters allowed by the Javascript validation */

        public FilteredRequest(ServletRequest request)
        {
            super((HttpServletRequest) request);
        }

        public String getParameter(String aoParamName)
        {
            String lsValue = super.getParameter(aoParamName);
            if ("_nfpb".equalsIgnoreCase(aoParamName) || "menuVar".equalsIgnoreCase(aoParamName)
                    || "_nfls".equalsIgnoreCase(aoParamName) || "_pageLabel".equalsIgnoreCase(aoParamName)
                    || "app_menu_name".equalsIgnoreCase(aoParamName) || "removeNavigator".equalsIgnoreCase(aoParamName)
                    || "section".equalsIgnoreCase(aoParamName) || "subsection".equalsIgnoreCase(aoParamName))
            {
                lsValue = StringEscapeUtils.escapeXml(lsValue);
                
            }
            return lsValue;
        }
    }

    private static final LogInfo LOG_OBJECT = new LogInfo(RedirectFilter.class);
    private static Pattern[] msPatterns = new Pattern[]
    {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),

            // Avoid anything in a src='...' type of expression
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
                    | Pattern.DOTALL),

            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
                    | Pattern.DOTALL),

            // Remove any lonesome </script> tag
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("</iframe>", Pattern.CASE_INSENSITIVE),

            // Remove any lonesome <script ...> tag
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

            Pattern.compile("<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

            // Avoid eval(...) expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

            // Avoid expression(...) expressions
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

            // Avoid javascript:... expressions
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),

            // Avoid vbscript:... expressions
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),

            // Avoid onload= expressions
            // Added the specific on events patterns and remove the generic on regex pattern.
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onblur(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onchange(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("oncontextmenu(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onformchangeNew(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onforminputNew(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("oninputNew(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("oninvalidNew(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("ondblclick(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmousemove(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseup(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onkeydown(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onkeypress(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onkeyup(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onabort(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onresize(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onscroll(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onunload(.*?)=", Pattern.CASE_INSENSITIVE
                    | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("alert(.*?)\\(", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("tostring(.*?):", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("valueof(.*?):", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), 
            //Start QC 9463 R 8.4
            Pattern.compile("<a([^>]+)>(.+?)</a>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("\\s*href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("(<img\\s+)src=\"(.*?)\"|width=\"(.*?)\"|height=\"(.*?)\">", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            //End QC 9463 R 8.4
    		};
    private FilterConfig filterConfig;

    /**
     * This method provides the functionality for filter initialization
     * 
     * @param filterConfig is the initialization parameter
     */
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;
    }

    /**
     * This method provides the functionality for destroying filter object
     */
    public void destroy()
    {
        this.filterConfig = null;
    }

    /**
     * This method redirects the user to login page when user is not in session
     * <li>This method was updated in R4</li>
     * @param aoRequest is the Request object
     * @param aoResponse is the Response object
     * @param aoChain is the FilterChain object provided by the servlet
     *            container
     * 
     */
    public void doFilter( final ServletRequest aoRequest, final ServletResponse aoResponse, final FilterChain aoChain)
    {

        try
        {
            HttpServletRequest loReq = (HttpServletRequest) aoRequest;
            HttpServletResponse loRes = (HttpServletResponse) aoResponse;

            LOG_OBJECT.Debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );
            LOG_OBJECT.Debug("loReq.getRequestURL:" + loReq.getRequestURL());
            LOG_OBJECT.Debug("loReq.getQueryString:" + loReq.getQueryString());
            
            String lsHhsOrigin = (String) loReq.getHeader("hhsOrigin");
            LOG_OBJECT.Debug("getHeader hhsOrigin ::" + lsHhsOrigin);
            loReq.setAttribute("hhsOrigin", lsHhsOrigin);
            
            

            LOG_OBJECT.Debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );

            List<String> loList = (List<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
                    ApplicationConstants.SESSION_LIST_REMOVE);
            
            
            Map<String, String> loDataMap = (Map<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.SESSION_USER_DETAIL_CACHE);
            
            //LOG_OBJECT.Debug("===============   SESSION_LIST_REMOVE :: " +loList);
           	//LOG_OBJECT.Debug("===============   SESSION_USER_DETAIL_CACHE :: " +loDataMap);
           
            HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
                    .getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);

            if( loApplicationSettingMap == null ){
            //LOG_OBJECT.Debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   loApplicationSettingMap is NULL @@@@@@" );
            }

            String lsEnableLogout = loApplicationSettingMap.get(ApplicationConstants.ENABLE_LOGOUT_CONCURRENT);
          
            //LOG_OBJECT.Debug("275 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   TRACING --------------" );
            
            if (lsEnableLogout != null && lsEnableLogout.equalsIgnoreCase("true") && loList != null   && loList.contains(loReq.getSession().getId()) )
            {   
            	LOG_OBJECT.Debug("############### LOGOUT CONCURRENT SESSION ######################" );
            	//LOG_OBJECT.Debug("281 ====loList != null   && loList.contains(loReq.getSession().getId()) :: "  + loList.contains(loReq.getSession().getId()) );
            	//LOG_OBJECT.Debug("281 @@@  lsEnableLogout != null && lsEnableLogout.equalsIg   @@@" );
                String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":" + loReq.getServerPort()
                        + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                        + "&loginportlet=loginportlet&logout=logout&app_menu_name=logout_icon";
                //LOG_OBJECT.Debug("@@@ lsRedirectPath :: " +lsRedirectPath);
                
             // Start R 8.0.0 QC 9205 Internal SAML - siteminderLogout is irrelevant 
                /*
                if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE) != null
                        && !((String) loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))
                                .equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
                {
                	LOG_OBJECT.Debug("############### City User Logout ######################" );
                	lsRedirectPath = lsRedirectPath + "&siteminderLogout=siteminderLogout";
                    
                }
                */
             
                
                //loList.remove(loReq.getSession().getId());
                
                // End R 8.0.0 QC 9205 Internal SAML - siteminderLogout is irrelevant 
                
                
             // Start R 8.0.0 QC 9205 Internal SAML - put condition for External and Internal users
             //   for Internal user - on refresh action with concurrent session detected - just do nothing:
             //   - do not remove session from list 
             //   - do not kill the session
                if ("XMLHttpRequest".equals(loReq.getHeader("X-Requested-With"))) 
                {
                	LOG_OBJECT.Debug("############### XMLHttpRequest ######################" );
                	// old behavior for External User
                	if(lsHhsOrigin.equalsIgnoreCase("DMZ"))
                	{    
                		loList.remove(loReq.getSession().getId());
                		loRes.addHeader("REQUIRES_AUTH", "1");
                		loRes.addHeader("AUTH_PATH", lsRedirectPath);
                		loRes.sendRedirect(lsRedirectPath);
                        loReq.getSession().invalidate();
                	}
                	else if(lsHhsOrigin.equalsIgnoreCase("CITY"))
                	{
                		//LOG_OBJECT.Debug("############### DO NOTHING !!!! " );
                   	}
                	
                	/* Old code
                	
                    loRes.addHeader("REQUIRES_AUTH", "1");
                    loRes.addHeader("AUTH_PATH", lsRedirectPath);
                   
                    loRes.sendRedirect(lsRedirectPath);
                    loReq.getSession().invalidate();
                    */
                   
                 // End R 8.0.0 QC 9205 Internal SAML - put condition for External and Internal users
                }
                else
                {
                	loList.remove(loReq.getSession().getId());
                	                	
                	///put list of session to invalidate
                	
                    //loReq.getSession().invalidate();
                   
                	// 7.8.0 add concurrent error msg
                    //loReq.setAttribute(ApplicationConstants.ERROR_MSSG, ApplicationConstants.ERROR_MESSAGE_CONCURRENT  );
                
                    
                    lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
														+ loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
														+ "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";
                    
                    //LOG_OBJECT.Debug("==313====1.2   &_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession    \n ====== " +lsRedirectPath  );
                    // 7.8.0 end   concurrent error msg                                    
                                        
                    loRes.sendRedirect(lsRedirectPath);
                    loReq.getSession().invalidate();
                    
                    LOG_OBJECT.Debug("############### ElseLogout  ######################" );
                    
                }
                return;
            }
            
            //LOG_OBJECT.Debug("===329====loReq.getQueryString() :: " + loReq.getQueryString());

            if (loReq.getQueryString() != null && loReq.getQueryString().length() > 0)
            {
                
                String lsQueryString = loReq.getQueryString();
                                
                Enumeration<String> loParamNames = loReq.getParameterNames();
                while (loParamNames.hasMoreElements())
                {
                    String lsParamName = loParamNames.nextElement();
                    String lsParamValue = loReq.getParameter(lsParamName);
                    if (!"_rrparams".equalsIgnoreCase(lsParamName)
                            && stripXSS(lsParamValue, lsQueryString, lsParamName))
                    {
                        if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) == null)
                        {
                            loReq.getSession().setAttribute(HHSConstants.ERROR_INVALID_CHARACTERS, HHSConstants.ERROR_INVALID_CHARACTERS);
                        }
                        String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                                + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                                + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&errorInvalidCharacters=errorInvalidCharacters";
                        
                        loRes.sendRedirect(lsRedirectPath);
                        return;
                    }
                }
            }

            if (loReq.getRequestURL() != null && loReq.getQueryString() == null)
            {
                //LOG_OBJECT.Debug("===354====1:if (loReq.getRequestURL() != null && loReq.getQueryString() == null)@@@@@    \n %%%%%%% " );
                //LOG_OBJECT.Debug("===355====1: loReq.getRequestURL() :::      "+loReq.getRequestURL().toString() );
                //LOG_OBJECT.Debug("===356====1: loReq.getQueryString() :::     "+loReq.getQueryString() );
                //LOG_OBJECT.Debug("===365====1: loReq.getSession get Org_type :::      "+(String) loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE));
                
                if (loReq.getRequestURL().toString().contains("portal/hhsweb.portal")
                        && loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null
                        && loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG) != null
                        && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String) loReq.getSession().getAttribute(
                                ApplicationConstants.KEY_SESSION_ORG_TYPE)))
                {
                    String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                            + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&_pageLabel=portlet_hhsweb_portal_page_provider_home";
                    
                    LOG_OBJECT.Debug("1.1   :if (loReq.getRequestURL() != null && loReq.getQueryString() == null)@@@@@   \n %%%%%%% " +lsRedirectPath  );
                    
                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                else if (loReq.getRequestURL().toString().contains("portal/hhsweb.portal")
                        && loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null
                        && loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG) == null
                        && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String) loReq.getSession().getAttribute(
                                ApplicationConstants.KEY_SESSION_ORG_TYPE)))
                {

                    String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                            + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";

                    //LOG_OBJECT.Debug("399===1.2   &_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession    \n %%%%%%% " +lsRedirectPath  );

                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                /** [Start] QC 9165 R 7.8.0 */
                if(  isProviderOrg(loReq , loRes ) ) // now true for Provider and City users 
                //if("DMZ".contains(lsHhsOrigin)) // commented out in R 8.0.0 QC 9205 	
                {
                    //LOG_OBJECT.Debug("------------------------------- PROVIDR_ORG_ & CITY USER ----------------------" );
                    if( setTimeStampOnSecurityData( loReq , loRes ) )
                    {
                        String lsRedirectPath = buildReqParameter( loReq ,  loRes );
                        //LOG_OBJECT.Debug("403 -----lsRedirectPath :: " +lsRedirectPath);
                        loRes.sendRedirect(lsRedirectPath);
                        return;
                    }
                }
                /** [End] QC 9165 R 7.8.0 */
            }
            else if (loReq.getQueryString() != null
                    && (loReq.getQueryString().contains("portlet_hhsweb_portal_login_page")
                            || loReq.getQueryString().contains("loginportlet")
                            || loReq.getQueryString().contains("portlet_hhsweb_portal_page_nyc_registration") 
                            || loReq.getQueryString().contains("portlet_hhsweb_portal_page_password_reset_email")))
            {
                LOG_OBJECT.Debug("2 <::> loReq.getQueryString() != null && loReq.getQueryString().contains(portlet_hhsweb_portal_login_page)    \n %%%%%%% "  );

                /** [Start] QC 9165 R 7.8.0 */
                if( setTimeStampOnSecurityData( loReq , loRes ) 
                        && !loReq.getQueryString().contains("logout") 
                        && !loReq.getQueryString().contains("_windowLabel=portletInstance_30&_urlType=action&loginportlet=loginportlet")
                    )
                {
                    String lsRedirectPath = buildReqParameter( loReq ,  loRes );
                    //LOG_OBJECT.Debug("425======Provider  lsRedirectPath :: "+lsRedirectPath);
                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                /** [End] QC 9165 R 7.8.0 */

                if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null
                        && !loReq.getQueryString().contains("portlet_hhsweb_portal_page_nyc_registration")
                        && !loReq.getQueryString().contains("logout"))
                {
                    String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                            + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";
                    
                    LOG_OBJECT.Debug("2.1 <::> &_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession   \n %%%%%%% " + lsRedirectPath  );

                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                else if (loReq.getSession().getAttribute(HHSConstants.SESION_URL) == null )
                {
                    LOG_OBJECT.Debug("2.2 <::> loReq.getSession().getAttribute(HHSConstants.SESION_URL) == null   \n %%%%%%% "   );
                    String lsUserId = null;
                    HashMap<String, String> loLockedDocumentMap = null;
                    lsUserId = (String) loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
                    String lsSessionId = loReq.getSession().getId();
                    if (null != lsUserId && !lsUserId.isEmpty() && null != lsSessionId && !lsSessionId.isEmpty())
                    {
                        String lsLockedDocKey = lsSessionId + "_" + lsUserId;
                        loLockedDocumentMap = (HashMap<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.EDIT_DOC_LIST_MAP);
                        if (null != loLockedDocumentMap && loLockedDocumentMap.containsKey(lsLockedDocKey))
                        {
                            loLockedDocumentMap.remove(lsLockedDocKey);
                        }
                        synchronized (this)
                        {
                            BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.EDIT_DOC_LIST_MAP,
                                    loLockedDocumentMap);
                        }
                    }

                    LOG_OBJECT.Debug("logging out user with below details:: Session ID :: "+loReq.getSession().getId()+" :: USER ID :: "
                                    +loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID));
                    // Start QC 9205 R 8.0.0  Logout works for both sides loReq.getSession().invalidate() statement was out of the condition
                    /*
                    LOG_OBJECT.Debug("=476=============================logging out INVALIDATE SESSION " );
                    loReq.getSession().invalidate(); 
                     */
                   // End QC 9205 R 8.0.0  Logout works for both sides
                    LOG_OBJECT.Debug("2.2.1 <::> portlet_hhsweb_portal_login_page && logout=logout  " +  
                    		(loReq.getQueryString().contains("_pageLabel=portlet_hhsweb_portal_login_page") && loReq.getQueryString().contains("logout=logout")) );

                    /* [Start] R7.8.0 logout   */
                    if( loReq.getQueryString().contains("_pageLabel=portlet_hhsweb_portal_login_page") 
                         && loReq.getQueryString().contains("logout=logout") 
                        // Srart QC 9205 R 8.0.0  Logout works for both sides : sideminderLogout is irrelavent
                        // && !loReq.getQueryString().contains("siteminderLogout=siteminderLogout")
                        // End QC 9205 R 8.0.0  Logout works for both sides
                         )
                    {
                        
                    	LOG_OBJECT.Debug("2.2.2 <::> loReq.getQueryString().contains(\"logout=logout\")   \n %%%%%%% "   );
                    	// Start QC 9205 R 8.0.0 SAML Internal 
                    	
                    	//Start QC 9446 8.1 fix error 500 during logout 
                    	
                    	
                    	//        +loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID));
	                    //LOG_OBJECT.Debug("=496=============================logging out INVALIDATE SESSION " );
                    	
                    	// We do not need to invalidate individual session since we are invalidating all
                    	//if(loReq.getSession(false)!= null)
                    	//{	
                    	//	loReq.getSession().invalidate();
                    	//}
                    	
	                    //LOG_OBJECT.Debug("=498=============================logging out INVALIDATE ALL SESSION AND COOKIE" );
                    	// add try/catch block
                    	HttpSession s = loReq.getSession(false);
                    	try
                    	{
                    		if(s != null)
                        	{
                        		LOG_OBJECT.Debug("logging out user with below details:: Session Exists ::: " + s.getId());
                        		//s.invalidate();
                           		ServletAuthentication.invalidateAll(loReq);
                    			LOG_OBJECT.Debug("All user sessions have been invalidated");
                        		ServletAuthentication.killCookie(loReq);
                        		LOG_OBJECT.Debug("All Cookie have been killed");
                        	}
                    	} 
                    	catch(Exception e)
                    	{
                    		LOG_OBJECT.Error("Error during invalidating all sessions :: "+e.getMessage());
                    		e.getStackTrace();
                    	}
                    	
                    	// check if session still exists
                    	HttpSession ss = loReq.getSession(false);
                    	if(ss != null)
                    	{
                    		LOG_OBJECT.Debug(" Session still Exists ::: " + ss.getId());
                    	}
                    	
                    	//End QC 9446 8.1 fix error 500 during logout
                    	
	                    //String logoutUrl ="";
	                    //String hostUrl ="";
                    	String redirectUrl="";
                    	if(lsHhsOrigin.equalsIgnoreCase("DMZ"))
                    	{                    		
                    		redirectUrl = logoutSAML( loReq );
                    	}
                    	else if(lsHhsOrigin.equalsIgnoreCase("CITY"))
                    	{                    		
                    		// Start QC 9485 R 8.4.0 send logout request to DOITT and wait on response 
                    		// commented out old link
                    		/*  
                    		//Start QC 9383 R 8.0.0 appScan crossSite scripting issue
                    		redirectUrl=loReq.getContextPath() +"/logoutredirect.jsp";
                    		//End QC 9383 R 8.0.0 appScan crossSite scripting issue	
                    		*/
                    		String logoutUrl = HHSUtil.logoutSAMLcity( loReq );
                    		LOG_OBJECT.Debug("===from properies logoutURL:  "+logoutUrl );
                    	    String hostUrl = HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_CITY); 
                    	    LOG_OBJECT.Debug("===from properies hostURL:  "+hostUrl );
                    	    String destUrl = hostUrl +"HHSPortal/logoutredirect.jsp";
							if(loReq.getParameter("msg") != null){
								destUrl = destUrl + "?msg=" + loReq.getParameter("msg");
							}							
                    	    LOG_OBJECT.Debug("===destUrl:  "+destUrl );
                    	    redirectUrl= logoutUrl + "?logOutDest=" + destUrl;
                    	    LOG_OBJECT.Debug("===redirectUrl:  "+redirectUrl );
                            /*
                    	    LOG_OBJECT.Debug("!!!!===================TEST -----redirect to Log out static page");
                    	    String iframeUrl=logoutUrl + "?x-frames-allow-from=" + hostUrl;
                    	    LOG_OBJECT.Debug("===iframeUrl:  "+iframeUrl );
                            HttpClient loClient = new HttpClient();
                            String url = "https://accounts-nonprd.nyc.gov/account/idpLogout.htm";
                            HttpMethod method = new GetMethod(iframeUrl); //redirectUrl);
                            LOG_OBJECT.Debug("========execute GET request :: returnCode = loClient.executeMethod(method)");
                            int returnCode = loClient.executeMethod(method);
                            LOG_OBJECT.Debug("redirect to Log out static page ");
                            LOG_OBJECT.Debug("====Status Code = "+returnCode);
                            LOG_OBJECT.Debug("====QueryString>>> "+method.getQueryString());
                            LOG_OBJECT.Debug("===Status Text>>>" +HttpStatus.getStatusText(returnCode));
                             //Get data as a String
                            LOG_OBJECT.Debug(method.getResponseBodyAsString());
                            //OR as a byte array
                            byte [] res  = method.getResponseBody();
                            //release connection
                            method.releaseConnection();
                           */
                    	   
                           // End QC 9485 R 8.4.0 send logout request to DOITT and wait on resonse  
                    		 
                    	}
                    	// End QC 9205 R 8.0.0 SAML Internal 
                        if( redirectUrl!= null && !redirectUrl.isEmpty() )  
                        {
                        	LOG_OBJECT.Debug("logging out URL:  "+redirectUrl );
                        	// Start qc -9642 The SD Element id T1539 - Clear browser data on user logout
                        	loRes.setHeader( "Clear-Site-Data", "*" );//Clear browser data
                        	//End qc -9642 The SD Element id T1539 - Clear browser data on user logout
                            loRes.sendRedirect(redirectUrl);
                           
                        }else{
                        	//LOG_OBJECT.Debug("535===sendRedirect(HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_PROP_INX) "   );
                        	                          
                        	// Start QC 9205 R 8.0.0 SAML Internal 
                        	//loRes.sendRedirect(HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_PROP_INX) );
                        	// works for Provider  
                        	if(lsHhsOrigin.equalsIgnoreCase("DMZ"))
                        	{
                        		loRes.sendRedirect(HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_PROP_INX) );
                        	}
                        	else if(lsHhsOrigin.equalsIgnoreCase("CITY"))
                        	{
                        		loRes.sendRedirect(HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_CITY) );
                        	}
                        	// End QC 9205 R 8.0.0 SAML Internal 
                        }
                        
                    }
                    /* [End] R7.8.0      */
                }
            }
            //LOG_OBJECT.Debug("510=== excludeUrl(loReq.getRequestURL().toString().toLowerCase()) "  + loReq.getRequestURL().toString().toLowerCase() );
            if (excludeUrl(loReq.getRequestURL().toString().toLowerCase()))
            {
                
                LOG_OBJECT.Debug("3 <::> excludeUrl(loReq.getRequestURL().toString().toLowerCase()) "  + loReq.getRequestURL().toString().toLowerCase() );
                
                aoChain.doFilter(new FilteredRequest(aoRequest), aoResponse);
                
                return;

            }
            else if (loReq.getRequestURL() != null && loReq.getQueryString() != null
                    && loReq.getQueryString().toLowerCase().contains("pagelabel")
                    && "xyz".equalsIgnoreCase(loReq.getParameter("_pageLabel"))
                    && loReq.getQueryString().contains("notificationIdFromEmail")
                    && loReq.getParameter("notificationIdFromEmail") != null)
            {
                 LOG_OBJECT.Debug("4 <::> loReq.getRequestURL() != null && loReq.getQueryString() != null "   );

                if (null != loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID))
                {
                    String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                            + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";

                    LOG_OBJECT.Debug("4.1 <::> null != loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID)   \n %%%%%%% "  + lsRedirectPath   );

                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                else
                {
                    Channel loChannelobj = new Channel();
                    loChannelobj.setData("urlNotificationId", (String) loReq.getParameter("notificationIdFromEmail"));
                    HHSTransactionManager.executeTransaction(loChannelobj, "getUrlNotificationDetails");
                    NotificationURLBean loNotificationURLBean = (NotificationURLBean) loChannelobj
                            .getData("urlNotificationBean");
                    String lsRedirectPath = loNotificationURLBean.getUrl();
                    
                    LOG_OBJECT.Debug("4.2 <::> null != loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID)  \n %%%%%%%" + lsRedirectPath  );

                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
            }
            //CITY/AGENCY 
            // https://mstlva-hhsacei1.csc.nycnet:8443/HHSPortal/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_nfls=false
            else if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null
                    && loReq.getRequestURL() != null && loReq.getQueryString() != null
                    && !loReq.getQueryString().contains("portlet_hhsweb_portal_page_chooseOrganization")
                    && !loReq.getQueryString().contains("portlet_hhsweb_portal_page_errorpage")
                    && !loReq.getQueryString().contains("submit_action=multiAccount"))
            {
                LOG_OBJECT.Debug("5 <::> loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null \n %%%%%%%"   );
                //KEY_SESSION_USER_ORG must not be null!!!!
                if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG) == null
                        && !loReq.getQueryString().contains("portlet_hhsweb_portal_page_missing_profile")
                        && !loReq.getQueryString().contains("accoutRequestmodule")
                        && !loReq.getQueryString().contains("fromLoginPage"))
                {
                    String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":"
                            + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userExitInSession=userExitInSession";

                    LOG_OBJECT.Debug("5.1 <::> loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null \n %%%%%%%"  + lsRedirectPath );

                    loRes.sendRedirect(lsRedirectPath);
                    return;
                }
                
                else if (loReq.getQueryString().contains("notificationIdFromEmail") && loReq.getParameter("notificationIdFromEmail") != null)
                {
                    LOG_OBJECT.Debug("5-2 <::> loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) != null "  );

                    Channel loChannelobj = new Channel();
                    loChannelobj.setData("urlNotificationId", (String) loReq.getParameter("notificationIdFromEmail"));
                    HHSTransactionManager.executeTransaction(loChannelobj, "getUrlNotificationDetails");
                    NotificationURLBean loNotificationURLBean = (NotificationURLBean) loChannelobj
                            .getData("urlNotificationBean");
                    String lsOrgId = (String) loReq.getSession()
                            .getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
                    if (loNotificationURLBean == null
                            || (!ApplicationConstants.CITY_ORG.equalsIgnoreCase((String) loReq.getSession()
                                    .getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)) && lsOrgId != null && !loNotificationURLBean
                                    .getOrganizationId().equalsIgnoreCase(lsOrgId)))
                    {
                        LOG_OBJECT.Debug("5-2-1<::> if loNotificationURLBean"  );
                        
                        String lsRedirectPath = loReq.getScheme()
                                + "://"
                                + loReq.getServerName()
                                + ":"
                                + loReq.getServerPort()
                                + loReq.getContextPath()
                                + ApplicationConstants.PORTAL_URL
                                + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userDoesNotBelong=userDoesNotBelong";
                        
                        LOG_OBJECT.Debug("5-2-2<::> if loNotificationURLBean :>>>>>>>" + lsRedirectPath );

                        loRes.sendRedirect(lsRedirectPath);
                        return;
                    }
                    else
                    {
                        LOG_OBJECT.Debug("5-2-3<:Start:> else loNotificationURLBean"  );
                        String lsRedirectPath = "";
                        if (lsOrgId == null)
                        {
                            String lsOrgIdFromNotification = loNotificationURLBean.getOrganizationId();
                            List<StaffDetails> loStaffDetailsList = (List<StaffDetails>) loReq.getSession()
                                    .getAttribute(ApplicationConstants.STAFF_DETAILS_BEAN_LIST_PARAM);
                            boolean isValidOrganizattion = isValidMultiOrgUserForNotification(lsOrgIdFromNotification, loStaffDetailsList);
                            if(isValidOrganizattion)
                            {
                                LOG_OBJECT.Debug("5-2-3-1<::> if isValidOrganizattion  --"  );
                                UserBean loUserBean = (UserBean) loReq.getSession().getAttribute(  ApplicationConstants.GET_USER_ROLES);
                                loReq.getSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_ORG,               lsOrgIdFromNotification);
                                loReq.getSession().setAttribute(ApplicationConstants.KEY_SESSION_ORG_ID,                                    lsOrgIdFromNotification);
                                loUserBean = userRoleForNotificationMultiOrganization(lsOrgIdFromNotification,
                                        loStaffDetailsList, loUserBean);
                                setUserDataInSessionForNotificationURLMultiOrg(loReq, loUserBean);
                                lsRedirectPath = loNotificationURLBean.getUrl();
                                LOG_OBJECT.Debug("5-2-3-2<::> if isValidOrganizattion  --" + lsRedirectPath  );
                            }
                            else
                            {
                                lsRedirectPath = loReq.getScheme()
                                + "://"
                                + loReq.getServerName()
                                + ":"
                                + loReq.getServerPort()
                                + loReq.getContextPath()
                                + ApplicationConstants.PORTAL_URL
                                + "&_pageLabel=portlet_hhsweb_portal_page_errorpage&userDoesNotBelongToOrg=userDoesNotBelongToOrg";
                                
                                LOG_OBJECT.Debug("5-2-3-3<::> else isValidOrganizattion  --"  );

                            }
                        }
                        else
                        {
                            lsRedirectPath = loNotificationURLBean.getUrl();
                            LOG_OBJECT.Debug("5-2-4<::> else isValidOrganizattion  --"  );
   
                            
                        }
                        LOG_OBJECT.Debug("5-2-3<:End:>  else loNotificationURLBean :: "+lsRedirectPath);
                        loRes.sendRedirect(lsRedirectPath);
                        return;
                    }
                }
                else
                {
                    aoChain.doFilter(new FilteredRequest(aoRequest), aoResponse);
                    return;
                }
            }
            else if (loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) == null
                    && !(
                    		(loReq.getQueryString() == null && loReq.getRequestURL().toString().endsWith("hhsweb.portal")) 
                    		|| (loReq.getQueryString() != null && 
                    				(loReq.getQueryString().contains("portlet_hhsweb_portal_login_page")
                    						|| loReq.getQueryString().contains("loginportlet")
                    						|| loReq.getQueryString().contains("portlet_hhsweb_portal_page_nyc_registration")
                    						|| loReq.getQueryString().contains("accoutRequestmodule") 
                    						|| loReq.getQueryString().contains("aaaValueTo")
                    						|| loReq.getQueryString().contains("userExitInSession") // R.7.8 Concurrent Session detected
                    			) 
                    		)
                    	)
                    )
            {

                
            	//LOG_OBJECT.Debug("===657====6: loReq.getRequestURL() :::     \n  "+loReq.getRequestURL().toString() );
                //LOG_OBJECT.Debug("===658====6: loReq.getQueryString() :::     \n  "+loReq.getQueryString() );
            	            	
            	String lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":" + loReq.getServerPort()
                        + loReq.getContextPath() + ApplicationConstants.PORTAL_URL + "&loginportlet=loginportlet";
                
            	                
                if (loReq.getSession().getAttribute(HHSConstants.ERROR_INVALID_CHARACTERS) != null)
                {
                    lsRedirectPath = loReq.getScheme() + "://" + loReq.getServerName() + ":" + loReq.getServerPort()
                            + loReq.getContextPath() + ApplicationConstants.PORTAL_URL
                            + "&loginportlet=loginportlet&error=error";
                    loReq.getSession().removeAttribute(HHSConstants.ERROR_INVALID_CHARACTERS);
                }
                loReq.getSession().invalidate();
                String lsUrlHit = loReq.getRequestURL() + "?" + loReq.getQueryString();

                if ((lsUrlHit.toLowerCase().contains("pagelabel")
                    && !lsUrlHit.toLowerCase().contains("portlet_hhsweb_portal_login_page") 
                    && !lsUrlHit.toLowerCase().contains("portlet_hhsweb_portal_page_errorpage")))
                {
                	//Proposal Notification URL form Provider Email
                	if(lsUrlHit.toLowerCase().contains("pagelabel=abc")
                		&& lsUrlHit.toLowerCase().contains("notificationidfromemail") 
                		&& lsHhsOrigin.equalsIgnoreCase("DMZ"))
                	{

                		LOG_OBJECT.Debug(" <:External:> SKIP setting Session attribute 'SESION_URL'  \n %%%%%%%"  + lsRedirectPath   );

                        Channel loChannelobj = new Channel();
                        loChannelobj.setData("urlNotificationId", (String) loReq.getParameter("notificationIdFromEmail"));
                        HHSTransactionManager.executeTransaction(loChannelobj, "getUrlNotificationDetails");
                        NotificationURLBean loNotificationURLBean = (NotificationURLBean) loChannelobj
                                .getData("urlNotificationBean");
                        lsRedirectPath = loNotificationURLBean.getUrl();

                	}else{
	                    loReq.getSession().setAttribute(HHSConstants.SESION_URL, lsUrlHit);
	                    LOG_OBJECT.Debug(" <:Internal:> set Session attribute 'SESION_URL'  \n %%%%%%%"  + lsUrlHit   );
                	}
                }else{
                    LOG_OBJECT.Debug(" <::> Do NOT set Session attribute 'SESION_URL'  \n %%%%%%%"  + lsUrlHit   );
                }

                LOG_OBJECT.Debug("6 <::> loReq.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID) == null \n %%%%%%%"  + lsRedirectPath   );

                loRes.sendRedirect(lsRedirectPath);
                return;
            }
            else
            {
                aoChain.doFilter(new FilteredRequest(aoRequest), aoResponse);
                return;
            }

        }
        catch (final IOException aoIOException)
        {
            LOG_OBJECT.Error("IOException occurred in Redirect Filter ", aoIOException);
        }
        catch (ServletException aoExp)
        {
            LOG_OBJECT.Error("ServletException occurred in Redirect Filter ", aoExp);
        }
        catch (ApplicationException aoExp)
        {
            LOG_OBJECT.Error("Application Exception occurred while executing LoginController:", aoExp);
        }
    }

    /**
     * This getter method gets the FilterConfig object
     * 
     * @return FilterConfig object
     */
    public FilterConfig getFilterConfig()
    {
        return this.filterConfig;
    }

    /**
     * This setter method sets the FilterConfig object
     * 
     * @param filterConfig is the FilterConfig object
     */
    public void setFilterConfig(final FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    /**
     * This method return boolean flag based on request url
     * 
     * @param asStr is the request url
     * @return lbFlag is the boolean flag
     */
    public boolean excludeUrl(String asStr)
    {
        boolean lbFlag = false;
        if (asStr.endsWith("js") || asStr.endsWith("css") || asStr.endsWith("png") || asStr.endsWith("jpg")
                || asStr.endsWith("gif") || asStr.endsWith("jar") || asStr.endsWith("lic.v1") || asStr.endsWith(".v1")
                || asStr.endsWith("logoutredirect.jsp")
                /* [Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */
                || asStr.endsWith("loginportlet.jsp")
                || asStr.endsWith("useracctrequestsubmitted.jsp")               
                )
        	 	/* [End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration */
        {
            lbFlag = true;
        }
        return lbFlag;
    } 

    /**
     * Description : Remove Spaces
     * 
     * @param String
     */
    public static String removeSpace(String aoString)
    {
        String lsString = aoString.replaceAll("\\s", "").replaceAll("\\%20", "").replaceAll("\\+", "")
                .replaceAll("\\&#32;", "").replaceAll("\\&#x20", "");
        return lsString;
    }

    /**
     * Description : To Check hazardous  character
     * 
     * @param asValue request param value return lsInvalidCharacter boolean
     *            return whether string is valid or not
     */
    public static boolean stripXSS(String asValue, String asQueryString, String asParamName)
    {
        boolean lsInvalidCharacter = false;
        if (asValue != null)
        {
            try
            {
                // NOTE: It's highly recommended to use the ESAPI library and
                // uncomment the following line to
                // avoid encoded attacks.
                asValue = ESAPI.encoder().canonicalize(asValue);

                // Avoid null characters
                asValue = asValue.replaceAll("\0", "");
                if (asValue.contains("class=MsoNormal"))
                {
                    asValue = asValue.replaceAll("class=MsoNormal", "");
                }
                for (Pattern lsScriptPattern : msPatterns)
                {
                    Matcher lsMatcher = lsScriptPattern.matcher(removeSpace(asValue.toLowerCase()));
                    if (lsMatcher.find()
                            && !(asValue.toLowerCase().contains("cn=") && asValue.toLowerCase().contains("ou=")))
                    // Implementation to avoid User_DN value in redirect Filter
                    {
                    	//Start QC 9463 R 8.3
                    	LOG_OBJECT.Error("The request containing hazardous chars" +",lsMatcher:" +lsMatcher +",asParamName:"+asParamName +",asValue:" +asValue);
                    	//End QC 9463 R 8.3
                        throw new Exception();
                    }
                }

                if (asQueryString.indexOf(asParamName) > -1
                        && (asValue.indexOf("<") > -1 || asValue.indexOf(">") > -1 || (!"userDn"
                                .equalsIgnoreCase(asParamName)
                                && !"_rrparams".equalsIgnoreCase(asParamName)
                                && !"_rrparams".equalsIgnoreCase(asParamName)
                                && !"_portlet.contentType".equalsIgnoreCase(asParamName) && asValue.indexOf("=") > -1)))
                {
                	//Start QC 9463 R 8.3
                	LOG_OBJECT.Error("The request containing hazardous chars" +",asParamName:" +asParamName +",asValue:" +asValue);
                	//End QC 9463 R 8.3
                    throw new Exception();
                }

            }
            catch (EncodingException aoExc)
            {
                try
                {
                    LOG_OBJECT.Debug("The request containing " + asValue + " contains hazardous  chars");
                }
                catch (ApplicationException aoEx)
                {
                    LOG_OBJECT.Error("Error while execution of doFilter ", aoEx);
                }
                lsInvalidCharacter = true;
            }
            catch (Exception aoExc)
            {
                try
                {
                    LOG_OBJECT.Debug("The request containing " + asValue + " contains hazardous  chars");
                }
                catch (ApplicationException aoEx)
                {
                    LOG_OBJECT.Error("Error while execution of doFilter ", aoEx);
                }
                lsInvalidCharacter = true;
            }
        }
        return lsInvalidCharacter;
    }

    /**
     * This method sets role mapping for user
     * <ul>
     * <li>Set the UserBean attribute 'MsOrgType' with the Application Constant
     * value</li>
     * <li>If lsPermissionLevel is not equal to null and lsAdminPermission is
     * not null then set MsRole attribute of UserBean according to their level</li>
     * </ul>
     * @param asOrgId Organization ID
     * @param aoStaffDetailsList Staff Details List return UserBean Referenced
     * @param aoSessionUserBean User Bean from Session
     *            for R4 - Original Method (R1)
     */
    @SuppressWarnings("rawtypes")
    private UserBean userRoleForNotificationMultiOrganization(String asOrgId, List<StaffDetails> aoStaffDetailsList, UserBean aoSessionUserBean)
    {
        Iterator loStaffDetailsItr = aoStaffDetailsList.iterator();
        while (loStaffDetailsItr.hasNext())
        {
            StaffDetails loTempStaffDetails = (StaffDetails) loStaffDetailsItr.next();
            if (null != loTempStaffDetails && loTempStaffDetails.getMsOrgId().equalsIgnoreCase(asOrgId))
            {
                String lsUserLevel = loTempStaffDetails.getMsPermissionLevel();
                String lsUserPermission = loTempStaffDetails.getMsPermissionType();
                String lsUserAdminPermission = loTempStaffDetails.getMsAdminPermission();
                aoSessionUserBean.setMsOrgType(ApplicationConstants.PROVIDER_ORG);
                if (null != lsUserLevel && null != lsUserAdminPermission)
                {
                    if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsUserLevel)
                            && ApplicationConstants.SYSTEM_YES.equalsIgnoreCase(lsUserAdminPermission))
                    {
                        aoSessionUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF);
                    }
                    else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_1.equalsIgnoreCase(lsUserLevel))
                    {
                        aoSessionUserBean.setMsRole(ApplicationConstants.ROLE_STAFF);
                    }
                    else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2.equalsIgnoreCase(lsUserLevel)
                            && ApplicationConstants.SYSTEM_YES.equalsIgnoreCase(lsUserAdminPermission))
                    {
                        aoSessionUserBean.setMsRole(ApplicationConstants.ROLE_ADMINISTRATOR_PROV_MANAGER);
                    }
                    else if (ApplicationConstants.PROVIDER_PERMISSION_LEVEL_2.equalsIgnoreCase(lsUserLevel))
                    {
                        aoSessionUserBean.setMsRole(ApplicationConstants.ROLE_MANAGER);
                    }
                }
                if (null != lsUserPermission && !lsUserPermission.isEmpty())
                {
                    aoSessionUserBean.setMsPermissionType(lsUserPermission);
                }
            }
        }
        return aoSessionUserBean;
    }

    /**
     * This method sets Organization Name,  User bean and associated user role and permission details in
     * session when user with multiple organization hits a notification URL. This method is created for R4.
     * @param aoReq HTTP Servlet Request
     * @param aoUserBean User Bean
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    private void setUserDataInSessionForNotificationURLMultiOrg(HttpServletRequest aoReq, UserBean aoUserBean)
            throws ApplicationException
    {
        List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
                ApplicationConstants.PROV_LIST);
        String lsOrgName = FileNetOperationsUtils.getProviderName(loProviderBeanList, (String) aoReq.getSession()
                .getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG));
        if (null != lsOrgName && !lsOrgName.isEmpty())
        {
            aoReq.getSession().setAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME, lsOrgName);
        }
        aoReq.getSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE, aoUserBean.getMsRole());
        aoReq.getSession().setAttribute(ApplicationConstants.KEY_SESSION_USER_PERMISSION_TYPE,
                aoUserBean.getMsPermissionType());
        aoReq.getSession().setAttribute(ApplicationConstants.GET_USER_ROLES, aoUserBean);
    }
    
    /**
     * This method identifies if Organization retrieved from a notification for logged in user is valid or not
     * @param asOrgId Organization ID from session
     * @param aoStaffDetailsList Staff Details List for Logged in User
     * @return true is a valid organization for this user/ false if not a valid organization for this user 
     */
    private boolean isValidMultiOrgUserForNotification(String asOrgId, List<StaffDetails> aoStaffDetailsList)
    {
        boolean lbIsValidUser = false;
        if(null != aoStaffDetailsList && !aoStaffDetailsList.isEmpty())
        {
            Iterator loStaffDetailsItr = aoStaffDetailsList.iterator();
            while (loStaffDetailsItr.hasNext())
            {
                StaffDetails loTempStaffDetails = (StaffDetails) loStaffDetailsItr.next();
                if (null != loTempStaffDetails && null != asOrgId 
                        && asOrgId.equalsIgnoreCase(loTempStaffDetails.getMsOrgId()))
                {
                    lbIsValidUser = true;
                    break;
                }
            }
        }
        return lbIsValidUser;
    }

    //** [Start] QC 9165 R7.8.0
    private String buildReqParameter(HttpServletRequest loReq ,ServletResponse aoResponse  ){
    	   
    	//loRes.sendRedirect(lsRedirectPath);
        
        //loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_NYC_PASSWORD_CYPERED, "SGVscEAxMjM=");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_NYC_PASSWORD_CYPERED, "");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_URL_TYPE, ApplicationConstants.SAML_LOGIN_PORTAL_URL_TYPE_VALUE);
        //loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_HDN_NYC_NYCID_CYPERED,  "cHJvdmlkZXJybUBtYWlsaW5hdG9yLmNvbQ=="   );
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_HDN_NYC_NYCID_CYPERED,  ""   );
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTAL_NYC_NYCID_CYPERED, "");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTLET, ApplicationConstants.SAML_LOGIN_PORTLET);
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_NEXT_ACTION, ApplicationConstants.SAML_LOGIN_NEXT_ACTION_VALUE);
        loReq.setAttribute(ApplicationConstants.SAML_PORTAL_FLAG, "true");
        loReq.setAttribute(ApplicationConstants.SAML_LOGIN_PORTLET_WINDOW_LABEL, ApplicationConstants.SAML_LOGIN_PORTLET_WINDOW_LABEL_VALUE);
        LOG_OBJECT.Info("return  :: "+
        		loReq.getScheme() + "://" + loReq.getServerName() + ":"
                + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.SAML_LOGIN_PORTAL_URL
        		);    
        return loReq.getScheme() + "://" + loReq.getServerName() + ":"
                + loReq.getServerPort() + loReq.getContextPath() + ApplicationConstants.SAML_LOGIN_PORTAL_URL
              ;
    }

    private boolean isProviderOrg(HttpServletRequest loReq ,ServletResponse aoResponse){
    	
        Subject subj = Security.getCurrentSubject();
        Set<Object> allPrivatePrincipals = subj.getPrivateCredentials();

        for (Object principal : allPrivatePrincipals) 
        {
            if ( principal instanceof SAMLAttribute) 
            {
            	LOG_OBJECT.Info("principal instanceof SAMLAttribute ::  true");
                return true;
            }
       }

        return false;
    }
    
    private boolean setTimeStampOnSecurityData(HttpServletRequest loReq ,ServletResponse aoResponse){
        /** [Start] QC 9165 R 7.8.0 */
        LOG_OBJECT.Info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ setTimeStampOnSecurityData @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );             
        Subject subj = Security.getCurrentSubject();
        Set<Object> allPrivatePrincipals = subj.getPrivateCredentials();

        LOG_OBJECT.Info("Number of Attributes "+allPrivatePrincipals.size());
        LOG_OBJECT.Info("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>");

        if(loReq.getSession().getAttribute(ApplicationConstants.SAML_ATTRIBUTE_FOR_NAME) == null ){
            loReq.getSession().setAttribute(ApplicationConstants.SAML_ATTRIBUTE_FOR_NAME, extractSAMLAttr(allPrivatePrincipals) );
            
            LOG_OBJECT.Info("setTimeStampOnSecurityData in session "+loReq.getSession().getAttribute(ApplicationConstants.SAML_ATTRIBUTE_FOR_NAME) );
            return true;
        }
        
        return false;
    }

    private SecurityTkn extractSAMLAttr(Set<Object> allPrivatePrincipals) {
        HashMap<String, String>  map = new HashMap<String, String>();
        HashMap<String, String>  map1 = new HashMap<String, String>();

        for (Object principal : allPrivatePrincipals) {
            LOG_OBJECT.Info("*******************principal:" + principal.toString() );
            // virtual user
            if ( principal instanceof SAMLAttribute) {
                SAMLAttribute attribute = (SAMLAttribute)principal;

                LOG_OBJECT.Info("************[AttrName]:"+attribute.getName()+"   [AttrValue]"+attribute.getValues() );
                try{
                    //Start  QC 9205 R 8.0.0 SAML Internal
                    if(attribute.getName().equalsIgnoreCase("groupMembership"))
                    {       String value = attribute.getValues().toString().replace(" ","");
                            value= value.replace("[","");
                            value= value.replace("]","");
                    		LOG_OBJECT.Info("************groupMembership value :: "  + value );
                    		map.put(attribute.getName(), value) ;
                    		
                    }
                    else
                    {
                    	// End QC 9205 R 8.0.0 SAML Internal
                    	map.put(attribute.getName(), attribute.getValues().toArray()[0].toString() ) ;
                    }
                 
                }catch(Exception e){
                    LOG_OBJECT.Info("************[AttrName]:"+attribute.getName()+"  NULLLLL [AttrValue]"+attribute.getValues() );
                    map.put(attribute.getName(), ApplicationConstants.EMPTY_STRING ) ;
                }

/*                if( attribute.getValues() != null && attribute.getValues().size() > 1 ){
                    LOG_OBJECT.Info("************[AttrName]:"+attribute.getName()+"  Not NULLLLL [AttrValue]"+attribute.getValues() );
                    try{
                        map.put(attribute.getName(), attribute.getValues().toArray()[0].toString() ) ;
                    }catch(Exception e){
                        map.put(attribute.getName(), ApplicationConstants.EMPTY_STRING ) ;
                    }
                }else{
                    LOG_OBJECT.Info("************[AttrName]:"+attribute.getName()+"  NULLLLL [AttrValue]"+attribute.getValues() );
                    map.put(attribute.getName(), ApplicationConstants.EMPTY_STRING ) ;
                }
*/                

            }
        }

        if( map != null && !map.isEmpty() )
        {
        	LOG_OBJECT.Info("************map of SAML Attributes :: "  + map ); 
        	return new SAMLTknImpl( map  );
        }else{
            return null;
        }
    }

    private String logoutSAML(HttpServletRequest loReq ){
        String redirectUrl =     "";
            try {
                redirectUrl =   HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX)  ;
            } catch (Exception e) {
                LOG_OBJECT.Error("Exception occurs while getting "+ ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX + " from properties file!!!!!");
            }
        //new String("https://accounts-nonprd.nyc.gov/account/idpLogout.htm?x-frames-allow-from={https://msdlvw-hhs-acc5.csc.nycnet:8443/sampleSaml}");

        //String loUserName = null;
        //loUserName = loReq.getUserPrincipal().getName();
            
        // R 8.1  Logout error 500
        // session and cookie already have been killed   
        try {
            ServletAuthentication.invalidateAll(loReq);
            ServletAuthentication.killCookie(loReq);
        }catch (Exception e) {
        	LOG_OBJECT.Error("Exception occurs while invalidateAll or killCookie. But the action alredy done. ");
        }
        try {
            return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
                    HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX) ) ;
        } catch (ApplicationException e) {
            return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, loReq.getContextPath() ) ;
        }
    }
    //** [End] QC 9165 R7.8.0
    
  //** [Start] QC 9205 R 8.0.0 SAML Internal

    /*private String logoutSAMLcity(HttpServletRequest loReq )
    {
    	
    	String redirectUrl =     "";
        try {
            	//LOG_OBJECT.Debug("1109=====logoutSAMLcity======== ");
                redirectUrl =   HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX_CITY)  ;
                //LOG_OBJECT.Debug("=====redirectUrl ::  "+redirectUrl);
            } catch (Exception e) {
                LOG_OBJECT.Error("Exception occurs while getting "+ ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX_CITY + " from properties file!!!!!");
            }
  
        try {
        		redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
        				HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX_CITY) ) ;
        		//LOG_OBJECT.Debug("1131=====logoutSAMLcity====redirectUrl ::  "+redirectUrl);
        	
        		return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
                    HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX_CITY) ) ;
        } catch (ApplicationException e) {
            	return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, loReq.getContextPath() ) ;
        }
    }*/
   
    //** [End] QC 9205 R 8.0.0 SAML Internal    
}
