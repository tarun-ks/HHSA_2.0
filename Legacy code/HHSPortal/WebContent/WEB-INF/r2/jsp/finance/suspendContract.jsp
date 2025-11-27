<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%-- Overlay Popup Starts --%>

<%-- This JSP Screen S412 is for Suspending contracts from list screen. --%>
<div class="content">
<div id="newTabs">
<portlet:resourceURL var="suspendContractUrl" id="suspendContractUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${suspendContractUrl}' id='suspendContractUrl'/>
<form:form action="" id="suspendContractForm" name="suspendContractForm"  commandName="AuthenticationBean">
    <div class="tabularCustomHead">
    
    <span id="contractTypeId">Confirm Suspend</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
    <div class="tabularContainer"> 
     <div id="ErrorDiv" class="failed breakAll"  > </div>
	<h2>Suspend Contract</h2>
	<div class='hr'></div>

<p>Are you sure you want to suspend this contract? 
All tasks and budgets associated with this contract will become suspended.
 Please fill in the required information and then click the Suspend Contract 
 button to continue.</p>

<p><label class="required">*</label>Indicates a required field.</p>
    	<div>&nbsp;</div>
		
	<div class="formcontainer">
		<div class="row">
			 <input name="" type="checkbox" id='chkSuspendContract' />
			 <label for='chkSuspendContract'>I agree to suspend this contract.</label> 
		</div>
		<div id="usrpsswdSuspend">
		<div class="row">
		<span class=" "></span>
		<span class="">
			<form:textarea name="usrpsswdSuspendReason" cols="" rows="6" class='input proposalConfigDrpdwn floatLft' 
					id="usrpsswdSuspendReason" path="reason" maxlength="500px" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);"/>
			<div class="required">*</div>  
			<form:errors path="reason"></form:errors>
		</span>
			  <span class="error"></span>
		</div>
		<div class="row">
			  
		</div>
		<div class="row">
			  <span class="label"><label class="required">*</label><label for='txtUsername'>User Name:</label></span>
			  <span class="formfield">
			  <form:input path="userName" name="txtUsernameSus" type="text"  cssClass='proposalConfigDrpdwn' id='txtUsernameSus' maxlength="128"/></span>
			  <span class="error"></span>
			  <form:errors path="userName"></form:errors>
		</div>
		<div class="row">
			  <span class="label"><label class="required">*</label><label for='txtPassword'>Password:</label></span>
			  <span class="formfield">
			  
			  <form:password path="password" name="txtPasswordSus" cssClass='proposalConfigDrpdwn' id='txtPasswordSus' autocomplete="off"/>
			  
			  </span>
			  <span class="error"></span>
			  <form:errors path="password"></form:errors>
		</div>
	</div>
		</div>
    <div class="buttonholder" id="buttonholderSuspend">
    	<input type="button" class="graybtutton" value="No, do NOT suspend this Contract" onclick="clearAndCloseOverLay();"/>
    	<input type="submit" class="button" value="Yes, suspend this Contract" id="suspendContractButton"/>
    </div>
  
    </div>
    <form:input type = 'hidden' value='${ContractId}' id='hdnContractId' name="hdnContractId" path="contractId"/>
    </form:form>
    </div></div>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/suspendContract.js?v1"></script>
  <%-- Overlay Popup Ends --%>
