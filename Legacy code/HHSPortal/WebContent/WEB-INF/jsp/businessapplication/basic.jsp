<!--This jsp is displayed when user is shown terms and condition page for business application-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
	"http://www.w3.org/TR/html4/strict.dtd">
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<html dir="ltr" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8">
	<meta http-equiv="content-style-type" content="text/css">
	<meta http-equiv="content-script-type" content="text/javascript">
	<title></title>
	<script type="text/javascript">
	//This method is used to enable button.
	function enablebutton(){
	  var e=document.getElementById("checkbox");
	  if(e.checked == true){
		document.getElementById("start").disabled=false;
	  } 
	  else{
		document.getElementById("start").disabled=true;
	  }
	}
	</script>
</head>
<body>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S049_PAGE, request.getSession())){%>
	
	<h2>HHS Prequalification Application Now in PASSPort</h2>
	<div class="clear"></div>
	<c:if test="${startNewApplication ne null and startNewApplication}">
		<input type="hidden" value="${prevBusinessAppId}" name="appId" id="appId"/>
		<c:set var="appId" value="${prevBusinessAppId}"></c:set>
	</c:if>
	<portlet:defineObjects />
	<form method="post" id="backing" action="<portlet:actionURL><portlet:param name='appId' value='${appId}'/></portlet:actionURL>" type="POST">
	   <hr class="clear" />
			<div style="border:1px solid grey; background: #E8E8E8; padding: 12px;  " >
			HHS Prequalification is now hosted in PASSPort – it is streamlined and easier than ever to complete! If your organization wishes to begin the HHS Prequalification application process or has a pending prequalification application in HHS Accelerator, you must submit a new application in PASSPort. To complete an HHS Prequalification Application in PASSPort, you must have a PASSPort account. Click <a class="link" title="PASSPort Login" target="_blank" href="https://passport.cityofnewyork.us/page.aspx/en/usr/login?blockSSORedirect=false&%20ReturnUrl=/page.aspx/en/buy/homepage">here</a> to create a PASSPort account using your NYC.ID or to log into an existing PASSPort account. The <Strong>same</Strong> NYC.ID login credentials you use to access HHS Accelerator may be used to login or create an account in PASSPort.  
            <br/><br/>
<!--             <font style="color:red"><b>Note</b></font>: The <b>same</b> NYC.ID credentials you use to access HHS Accelerator may be used to log in to PASSPort.
 -->            
<!--             <div class="buttonholder">
			       <input type="button" class="button" value="Get Started" onclick="window.open( 'https://www1.nyc.gov/site/mocs/systems/about-go-to-passport.page' )"  title = "Get Started" />
			</div>
 -->
<!--
			<div class="holder" >
 				<div class="left"><input type="checkbox" name="terms" id="checkbox"
				onclick="enablebutton()" />I Accept the above terms and conditions</div>
				<div class="right">
				<input id="start" value="Start New Accelerator Application" title="Start New Accelerator Application" type="submit" disabled="disabled" class="button" style="float:right;"></div> 
			</div>
			 </div>
-->

</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
</body>
</html>