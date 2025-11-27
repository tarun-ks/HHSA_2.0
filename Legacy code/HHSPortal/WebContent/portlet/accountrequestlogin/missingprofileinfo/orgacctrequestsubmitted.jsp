<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


 
<portlet:defineObjects />

<form id="myform" action="<portlet:actionURL/>" method ="post" name="myform">
	<!-- Body Container Starts -->
	<div class="bodycontainer">
	
	<h2>Organization Account Request Submitted</h2>
	<div class='hr'></div>
	<div class='container'>
		<div class='formcontainer'>
			<p>Thank you for requesting an HHS Accelerator Account
			<br/><br/>	
			When a decision has been made regarding your account request, an email notification will be sent to your <a href='#' title="administrator email" class='blueLink'> <%= request.getAttribute("adminEmail") != null ? request.getAttribute("adminEmail"): "" %> </a> and your organization's Executive Director/CEO or equivalent <a href='#' title="ceoemail" class='blueLink'> <%= request.getAttribute("ceoEmail") != null ? request.getAttribute("ceoEmail"): ""  %> </a>.
			<br/><br/>
			<a id="loginredirect" name="loginredirect" href='http://www.nyc.gov/hhsaccelerator' title="Click here to return to the HHS Accelerator Portal" class='blueLink'>Click here to return to the HHS Accelerator Portal</a>
			</p>
		 <br/>
		</div>
	</div>
	<!-- Body Container Ends -->
</form>

<script type="text/javascript">
	var contextPathVariable = "<%=request.getContextPath()%>";
	//this function is for navigation to account admin page
	function navigateAdminConsole(){
	     document.myform.action=document.myform.action+'&next_action=acctAdmin';
		 document.myform.submit();
	}
	
	$(document).ready(function() {
		//this function is for navigation to login page 
		$('#loginredirect').click(function() {
		    var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		    location.href= url;
	  });            
	}); 
</script>
