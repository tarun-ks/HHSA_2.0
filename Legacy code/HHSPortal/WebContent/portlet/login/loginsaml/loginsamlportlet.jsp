<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<html>
<head>
<script type="text/javascript" src="../js/base64.js"></script>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">

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
	
	function submitApplication(){
		//var encryptedUserID = CryptoJS.AES.encrypt(document.getElementById("userNYCId").value, "password12345678"); 
		
		document.getElementById("login").disabled=true;
		var encryptedUserID = Base64.encode(document.getElementById("userNYCId").value);
		document.getElementById("userNYCId").value="";
		document.getElementById("hdnUserNYCId").value = encryptedUserID;
		var encryptedPassword = Base64.encode(document.getElementById("userNYCPassword").value); 
		document.getElementById("userNYCPassword").value = encryptedPassword;
		document.forms[0].submit();
	}
</script>
</head>
<div id="loginDiv" >
	<form action="<portlet:actionURL/>&loginportlet=loginportlet" method="post" id="loginForm" name="loginForm">
		<input type="hidden" name="next_action" value="validateUser"/>
		<input type="hidden" id="hdnUserNYCId" name="hdnUserNYCId" value=""/>
		<!-- Body Container Starts -->
		<div class="login_info">To log in, please enter your NYC.ID and Password and click the "Login" button. 
		A valid NYC.ID is required to become a user of the HHS Accelerator system.</div>
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
			<h1 class="logoWiden" title="NYC Human Health Services Accelerator">Human Health Services Accelerator</h1>
			<div class="login_holder_info">Login</div>
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
				<div class="buttonholder" style="margin-right: 28px;"><input type="button" value="Cancel" title="Cancel" type="reset" class="graybtutton" onclick="javascript:loginredirect();"/>
					<input value="Login" type="button" class="button" id="login" name="login" title="Login" onclick="submitApplication();"  />
				</div>
				<div class="row"></div>
				<div class="row"><span class="label nobackground"><a href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_nyc_registration&_nfls=false&removeNavigator=true&accoutRequestmodule=accoutRequestmodule&navigatefrom=registerNyc"
					title="Create New NYC.ID">Create New NYC.ID</a></span> <span
					class="formfield"><a href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_password_reset_email&_nfls=false&removeNavigator=true&accoutRequestmodule=accoutRequestmodule"
					title="Forgot Password">Forgot Password</a></span>
				</div>
			</div>
		</div>
	</form>
</div>
</html>
