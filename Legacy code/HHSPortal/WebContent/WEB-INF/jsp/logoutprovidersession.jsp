<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.List, weblogic.security.Security,javax.portlet.PortletSession, com.nyc.hhs.util.HHSUtil, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb"%>
<portlet:defineObjects />
<%
		//[Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration
		String redirectUrl ="";
 		redirectUrl = HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX);	             
		if(redirectUrl==null) redirectUrl="";  
 		redirectUrl = redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
                   HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX) ) ;
               	
		if(request!=null && request.getHeader("Cookie")!=null){
			
			Cookie[] delCookies = request.getCookies();
			try {
				for( Cookie cookie : delCookies) {						
					cookie.setMaxAge(0); //set to 0 as to expire the cookie
					response.addCookie(cookie);
				}
			}catch(Exception e) {
				System.out.println("delete cookieValue exception:::"+e);
			}
		}//if(request!=null && request.getHeader("Cookie")!=null)
        if(session!=null){
			if(BaseCacheManagerWeb.getInstance().getCacheObject(
                   ApplicationConstants.SESSION_LIST_REMOVE)!=null){
				List<String> loList = (List<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
                   ApplicationConstants.SESSION_LIST_REMOVE);						
				if(session!=null && session.getId()!=null){
					loList.remove(session.getId());						
				}// if(session!=null && session.getId()!=null)
			}
			session.removeAttribute(ApplicationConstants.KEY_SESSION_USER_ID);	
			session.invalidate();
			weblogic.servlet.security.ServletAuthentication.invalidateAll(request);		
	   	}// if(session!=null)
            %>                
          	<script>               		
           	setTimeout(function(){
               	window.location.href = "<%=redirectUrl%>";
           	 }, 2000);
			</script>
           <% 
	//[End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration
	%>

