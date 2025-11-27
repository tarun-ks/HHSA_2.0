package com.nyc.hhs.util;


import java.util.UUID;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.portlet.context.PortletRequestAttributes;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
/**
 * This class is added for R8.6.0. 
 */

public class HHSTokenUtil {
	private static final LogInfo LOG_OBJECT = new LogInfo(HHSTokenUtil.class);
	public static final String HHS_TOKEN_KEY_NAME = "hhstokenkey";
	public static final String HHS_TOKEN_VALUE_PREFIX = "hhstokenvalue_";
	public static final String HHS_TOKEN_FLAG="tokenFlag";

	public static String genTokenValue() {

		String token = HHS_TOKEN_VALUE_PREFIX + UUID.randomUUID();
		return token;
	}

	public static String getTokenKey() {
		return HHS_TOKEN_KEY_NAME;
	}

	public static String getHiddenTokenInput() {
		StringBuilder stb = new StringBuilder();
		Boolean tokenFlag =true; //to disable, use table APPLICATION_SETTINGS , value set to false (WHERE COMPONENT_NAME ='TokenSetting' AND SETTINGS_NAME = 'TokenFlag')
		String tokenFlagStr = "";
		try {
			HttpServletRequest request = HHSTokenUtil.getRequest();
			if (request.getSession() != null && request.getSession().getAttribute(
					HHSTokenUtil.getTokenKey())!=null) {
				
				//exist in session
				if(request.getSession().getAttribute(HHS_TOKEN_FLAG)!=null){
					tokenFlag =(Boolean) request.getSession().getAttribute(HHS_TOKEN_FLAG);
				}else{
					tokenFlagStr = getTokenFlagConfig();
					if(tokenFlagStr!=null && "false".equalsIgnoreCase(tokenFlagStr)){
						tokenFlag = false;
						request.getSession().setAttribute(HHS_TOKEN_FLAG, new Boolean(tokenFlag));
					}else{
						// without set in db, set to true
						request.getSession().setAttribute(HHS_TOKEN_FLAG, new Boolean(tokenFlag));
					}
				}				 
				
				if(tokenFlag){
					stb.append("<input type='hidden' "
						+ " name='"
						+ HHSTokenUtil.getTokenKey()
						+ "'"
						+ " id='"
						+ HHSTokenUtil.getTokenKey()
						+ "'"
						+ " value='"
						+ request.getSession().getAttribute(
								HHSTokenUtil.getTokenKey()) + "'" + "/>");
				}
			}
		} catch (Exception e) {
			LOG_OBJECT.Error("HHSTokenUtil getHiddenTokenInput error:" + e.getMessage(),e);
		}
		return stb.toString();
	}

	public static HttpServletRequest getRequest() {
		PortletRequest request = null;
		HttpServletRequest req = null;
		RequestAttributes requestAttributes = RequestContextHolder
				.getRequestAttributes();
		
		 if (requestAttributes != null
					&& requestAttributes instanceof ServletRequestAttributes) {
		        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		        return servletRequestAttributes.getRequest();
		 }
		 else if (requestAttributes != null
				&& requestAttributes instanceof PortletRequestAttributes) {
			PortletRequestAttributes portletRequestAttributes = (PortletRequestAttributes) requestAttributes;
			request = portletRequestAttributes.getRequest();			
			return (HttpServletRequest) request.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST);
			 
		} else{
			return req;
		}		
	}
	//use to disable the token feature
	public static String getTokenFlagConfig() {		
		String lsTokenFlagConfig=null;
		Channel loChannelObj = new Channel();		
		try{
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.GET_TOKEN_FLAG_CONFIG);		
			lsTokenFlagConfig = (String) loChannelObj.getData(HHS_TOKEN_FLAG);
		}catch (Exception e) {
			LOG_OBJECT.Error("HHSTokenUtil getTokenFlagConfig error:" + e.getMessage(),e);
		}
	    return lsTokenFlagConfig;
        
	}
	
}
