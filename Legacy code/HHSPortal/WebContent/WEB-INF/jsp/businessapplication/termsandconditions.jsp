<!--This will display the terms and condition page.-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<meta http-equiv="content-style-type" content="text/css">
<meta http-equiv="content-script-type" content="text/javascript">
<script type="text/javascript">
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

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S049_PAGE, request.getSession())){%>
	
	<h2>HHS Accelerator Application Terms and Conditions</h2>
	<portlet:defineObjects />
	<form method="post" id="backing" action="<portlet:actionURL/>" type="POST">
   		<div class="termspanel clear">
	   		${lsDisplayTermsCondition}	
	    </div>
	    <c:if test="${startNewApplication ne null and startNewApplication}">
			<input type="hidden" value="${prevBusinessAppId}" name="appId" id="appId"/>
			<c:set var="appId" value="${prevBusinessAppId}"></c:set>
		</c:if>
		<input type="hidden" name="newService" value="newService" />
		<input type="hidden" name="action_redirect" value="${next_action}" />
		<input type="hidden" name="next_action" value="${next_action}" />
		<input type="hidden" name="section" value="${section}" />
		<input type="hidden" name="subsection" value="addservice" />
		<div class="holder">
			<div class="left"><input type="checkbox" name="terms" id="checkbox"
				onclick="enablebutton()" />I have read the terms and conditions</div>
			<div class="right"><input id="start" class="button" title="Continue" value="Continue" type="submit" disabled="disabled" style="float:right;"></div>
		</div>
	</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>