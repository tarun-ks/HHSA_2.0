<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%-- Overlay Popup Starts --%>
<div class="content">
<div id="newTabs">
<portlet:resourceURL var="unSuspendContractUrl" id="unSuspendContractUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${unSuspendContractUrl}' id='unSuspendContractUrl'/>
<form:form action="" id="unSuspendContractForm" name="unSuspendContractForm"  commandName="AuthenticationBean">
    <div class="tabularCustomHead">
    
    <span id="contractTypeId">Confirm Unsuspend</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
    <div class="tabularContainer"> 
     <div id="ErrorDiv" class="failed breakAll"  > </div>
	<h2>Unsuspend Contract</h2>
	<div class='hr'></div>

<p>Are you sure you want to unsuspend this contract? All suspended
tasks and budgets associated with this contract will become active
again. Please fill in the required information and then click the
Unsuspend Contract button to continue.</p>

<p><label class="required">*</label>Indicates a required field.</p>
    	<div>&nbsp;</div>
		
	<div class="formcontainer">
		<div class="row">
			 <input name="" type="checkbox" id='chkUnSuspendContract' />
			 <label for='chkUnSuspendContract'>I agree to unsuspend this contract.</label> 
		</div>
		<div id="usrpsswdUnsuspend">
		<div class="row">
		<span class=" "></span>
		<span class="">
			<form:textarea name="usrpsswdUnsuspendReason" cols="" rows="6" class='input proposalConfigDrpdwn floatLft' 
					id="usrpsswdUnsuspendReason" path="reason" maxlength="500px" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);"/>
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
			  <form:input path="userName" cssClass='proposalConfigDrpdwn' id='txtUsernameUnsus' maxlength="128"/></span>
			  <span class="error"></span>
			  <form:errors path="userName"></form:errors>
		</div>
		<div class="row">
			  <span class="label"><label class="required">*</label><label for='txtPassword'>Password:</label></span>
			  <span class="formfield">
			  
			  <form:password path="password" cssClass='proposalConfigDrpdwn' id='txtPasswordUnsus' autocomplete="off" />
			  
			  </span>
			  <span class="error"></span>
			  <form:errors path="password"></form:errors>
		</div>
	</div>
		</div>
    <div class="buttonholder" id="buttonholderUnSuspend">
    	<input type="button" class="graybtutton" title="No, do NOT unsuspend this Contract" value="No, do NOT unsuspend this Contract" onclick="clearAndCloseOverLay();"/>
    	<input type="submit" class="button" title="Yes, unsuspend this Contract" value="Yes, unsuspend this Contract" id="unSuspendContractButton"/>
    </div>
  
    </div>
    <form:input type = 'hidden' value='${ContractId}' id='hdnContractId' name="hdnContractId" path="contractId"/>
    </form:form>
    </div></div>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unSuspendContract.js?v1"></script>
  <%-- Overlay Popup Ends --%>
