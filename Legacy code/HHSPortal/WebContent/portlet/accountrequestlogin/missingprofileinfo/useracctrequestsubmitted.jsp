<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List,com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb"%>
<!-- $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ TEXT RESIZE CODE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ -->		
<form>
	<!-- Body Container Starts -->
	<div class="bodycontainer">
	
	<h2>Account Request Submitted</h2>
	<div class='hr'></div>
	
	<div class='container'>
		<div class='formcontainer'>
			<p>  An account request has been submitted to your organization's existing Account Administrator. 
			<br/>
			You should receive an email notification at <b> <%= request.getAttribute("emailAddr") != null ? request.getAttribute("emailAddr"): "" %></b> when your request has been reviewed. 
			<br/>	
			<br/>
			<a id="loginredirect" name="loginredirect" href='http://www.nyc.gov/hhsaccelerator' title="Click here to return to the HHS Accelerator Portal" class='blueLink'>Click here to return to the HHS Accelerator Portal</a>
			</p>
			<br/>
		</div>
	</div>
	<!-- Body Container Ends -->
</form>

					

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
function getmycookie(myname)
// this function is called by the function mydefaultsize()
// this function merely looks for any previously set cookie and then returns its value
{
	// if any cookies have been stored then
	if (document.cookie.length>0)
	{
		// where does our cookie begin its existence within the array of cookies  
		mystart=document.cookie.indexOf(myname + "=");
		// if we found our cookie name within the array then
		if (mystart!=-1)
		{
		// lets move to the end of the name thus the beginning of the value
		// the '+1' grabs the '=' symbol also
		mystart=mystart + myname.length+1;
		// because our document is only storing a single cookie, the end of the cookie is found easily
		myend=document.cookie.length;
		// return the value of the cookie which exists after the cookie name and before the end of the cookie
		return document.cookie.substring(mystart,myend);
		}
	}
	// if we didn't find a cookie then return nothing  
	return "";
}
	
function mydefaultsize(){
	// this function is called by the body onload event
	// this function is used by all sub pages visited by the user after the main page
	var div = document.getElementById("mymain");
	// call the function getmycookie() and pass it the name of the cookie we are searching for
	// if we found the cookie then
	if (getmycookie("mysize")>0)
	{
		// apply the text size change	
		div.style.fontSize = getmycookie("mysize") + "px";
	}
}
	
$(document).ready(function() {
	//this function is for navigation to login page	
	$('#loginredirect').click(function() {
		var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		location.href= url;
		});   
	});   
	  
</script>
 <%--[Start] R9.5.0 QC_9679 Terminate Session Token after EIN Registration --%>
	<c:if test="${!lsUserActiveStatusFlag}">
	<jsp:include page="/WEB-INF/jsp/logoutprovidersession.jsp">
	</jsp:include>
	</c:if>
 <%--[End] R9.5.0 QC_9679 Terminate Session Token after EIN Registration --%>