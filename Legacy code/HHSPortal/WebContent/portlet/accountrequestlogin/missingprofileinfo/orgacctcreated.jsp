<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

 
<portlet:defineObjects />
<!-- $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ TEXT RESIZE CODE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ -->		

<form id="orgcreatedform" action="<portlet:actionURL/>" method ="post" name="orgcreatedform">
	<!-- Body Container Starts -->
	<div class="">
	
	<h2>Organization Account Created</h2>
	<div class='hr'></div>
	
	<div class='container'>
		<div class='formcontainer'>
		
		<p>Congratulations, an organization account has been created for <A class=blueLink href="#"><%=request.getAttribute("adminEmail") != null ? request.getAttribute("adminEmail"): ""%></A>.
		<div>&nbsp;</div>	
		Click the "Continue" button to get started and proceed to your organization's HHS Accelerator home page.
		</p>
		<div class="buttonholder">
			<input name="continueButn" id="continueButn" title="Continue" type="button" value="Continue" onclick="javascript:navigateToHomePage();" />
		</div> 
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
	//this function is for navigation to login page
	 function redirectToLogin(){
		var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
	    location.href= url;
	 }
	//this function is for navigation to organization account created page
	 function navigateToHomePage(){
	      document.orgcreatedform.action=document.orgcreatedform.action+'&next_action=HomePage&accoutRequestmodule=accoutRequestmodule';
		  document.orgcreatedform.submit();
		 } 

</script>
