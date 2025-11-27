<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.List, java.util.Set,weblogic.security.Security,javax.security.auth.Subject, javax.portlet.PortletSession, com.nyc.hhs.util.HHSUtil, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects />
<html>
<head>
<script type="text/javascript" src="../js/base64.js"></script>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<meta http-equiv="Clear-Site-Data" content="*">
<style>
	.failed {
	     display:block;
	}
	.passed{
	 display:block;
	}
</style>
<script type="text/javascript">
	var contextPathVariable = "<%=request.getContextPath()%>";
	
		$(document).ready(function (){
			$('#userNYCId').focus();
			$('#nyc_header_div').hide();
			$('.breadcrumb').hide();
			$("#userNYCId").keydown(function(event){
			    if(event.keyCode == 13){
			    	submitApplication();
			    }
			});
			$("#userNYCPassword").keydown(function(event){
			    if(event.keyCode == 13){
			    	submitApplication();
			    }
			});
		});
	function loginredirect() {
	    var url = "http://www.nyc.gov/hhsaccelerator";
	    location.href= url;
	    
	} 
	
	function profilepage(){
		console.log('profilepage1');
		var profilePageVar = "<%=HHSUtil.obtainNYCIDurl()%>";
		console.log(profilePageVar);
		location.href = profilePageVar;
		
		console.log('profilepage2');
		
	}
	
	function submitApplication(){
		//var encryptedUserID = CryptoJS.AES.encrypt(document.getElementById("userNYCId").value, "password12345678"); 
	    var url = "<%=request.getContextPath()%>/secure/secure.jsp";
	    location.href= url;
/* 		
		document.getElementById("login").disabled=true;
		var encryptedUserID = Base64.encode(document.getElementById("userNYCId").value);
		document.getElementById("userNYCId").value="";
		document.getElementById("hdnUserNYCId").value = encryptedUserID;
		var encryptedPassword = Base64.encode(document.getElementById("userNYCPassword").value); 
		document.getElementById("userNYCPassword").value = encryptedPassword;
		document.forms[0].submit(); */
	}
</script>
</head>
<div id="loginDiv" >
	<form action="<portlet:actionURL/>&loginportlet=loginportlet" method="post" id="loginForm" name="loginForm">
		<input type="hidden" name="next_action" value="validateUser"/>
		<input type="hidden" id="hdnUserNYCId" name="hdnUserNYCId" value=""/>
		<!-- Body Container Starts -->
<!-- 		<div class="login_info">To log in, please enter your NYC.ID and Password and click the "Login" button. 
		A valid NYC.ID is required to become a user of the HHS Accelerator system.</div> -->
		<!-- Login section starts -->
		<div class="notify"> 
		
		    <%
				if(renderRequest.getAttribute("errorMsg") != null){
			%>
				<div class="failed"><%=request.getAttribute("errorMsg")%></div>
			<%
				}else if(renderRequest.getAttribute("userActivated") != null){
			%>
				<div class="passed"><%=request.getAttribute("userActivated")%></div>
			<%				
				}
			%>			
			
		</div>
		
		
		
		
		<div class="login_holder">
			<h1 class="logoWiden" title="NYC Human Health Services Accelerator">NYC Human Health Services Accelerator</h1>
<!-- 			<div class="login_holder_info">Message Board</div> -->
<%-- 			
			<div class="logincontainer">
				<div class="row"><span class="label nobackground">NYC ID <span style='color:#999;'>(Johnsmith@provider.org)</span>:</span>
					<span
						class="formfield">
						<input type="text" id="userNYCId"  name="userNYCId" maxlength="128" size="30" autocomplete="off" placeholder="NYC ID" />
					</span>
				</div>
				<div class="row"><span class="label nobackground">Password:</span>
					<span
						class="formfield"><input type="password" placeholder="Password" id="userNYCPassword" name="userNYCPassword" maxlength="64" size="30" autocomplete="off"/>
					</span>
				</div>
				
				<div class="buttonholder" style="margin-right: 28px;">
				    <input type="button" value="Cancel" title="Cancel" type="reset" class="graybtutton" onclick="javascript:loginredirect();"/>
					<input value="Login" type="button" class="button" id="login" name="login" title="Login" onclick="submitApplication();"  />
				</div>
 				<div class="row"></div>
				<div class="row"><span class="label nobackground"><a href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_nyc_registration&_nfls=false&removeNavigator=true&accoutRequestmodule=accoutRequestmodule&navigatefrom=registerNyc"
					title="Create New NYC.ID">Create New NYC.ID</a></span> <span
					class="formfield"><a href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_password_reset_email&_nfls=false&removeNavigator=true&accoutRequestmodule=accoutRequestmodule"
					title="Forgot Password">Forgot Password</a></span>
				</div> 
--%>
				<div class="buttonholder" style="margin-right: 28px;">
<!-- 				    <input type="button" value="Cancel" title="Cancel" type="reset" class="graybtutton" onclick="javascript:loginredirect();"/> -->
					<%-- <input  value="Login" type="button" class="button" id="login" name="login" title="Login" onclick="<%=HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX) %>"  /> --%>
					<input type="button" value="Logout" class="graybtutton" title="Logout" onclick="location.href='/HHSPortal/portal/hhsweb.portal?_nfpb=true&amp;_pageLabel=portlet_hhsweb_portal_login_page&amp;_nfls=false&amp;logout=logout'" />
				</div>
			</div>
		
	</form>

<!-- 
	<form action="/portal/hhsweb.portal" class="inline">
	    <button class="float-left submit-button" >Home</button>
	    <div class="buttonholder" style="margin-right: 28px;">
			<button class="float-left submit-button" >Login</button>
		</div>
    </form> 
-->

</div>
<%--[Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration --%>
	<c:if test="${!lsUserActiveStatusFlag}">
	<jsp:include page="/WEB-INF/jsp/logoutprovidersession.jsp">
	</jsp:include>
	</c:if>
<%--[End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration --%>
</html>
