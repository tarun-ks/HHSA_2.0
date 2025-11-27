<%-- This JSP file is for Update Contract configuration Initiation Overlay popup --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%-- Include JS for this JSP --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/updateContractConfig.js"></script>

<portlet:defineObjects />

<%-- Overlay Popup Starts --%>

<%-- get the URL for submitting the request --%>
<portlet:resourceURL var="updateContractConfiguration"
	id="updateContractConfiguration" escapeXml="false">
</portlet:resourceURL>

<%-- set the URL into a hidden input block for use in JS for AJAX call --%>
<input type='hidden' value='${updateContractConfiguration}' id='updateContractConfigurationUrl' />

<div class="content">
	<div class='tabularCustomHead'>
		<span id="contractTypeId">Confirm Configuration Update </span> 
		<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>

	<div class="tabularContainer popupWrapper">
		<h2>Update Contract Configuration</h2>
		<div class='hr'></div>
	
		<%-- Error Message Block on Overlay - usually will be filled in and shown while getting an error to be shown on Overlay. e.g. Authentication failure --%>
		<div class="failed" id="errorMsg"></div>
	
		<p>Are you sure you want to update the contract configuration?
		Please fill in the required information and then click the Update
		Configuration button to continue.</p>
	
		<p><label class="required">*</label>Indicates a required field</p>
	
		<div class="formcontainer">
			<form:form id="updateContractConfigForm" action="" method="post" name="updateContractConfigForm">
				<input type="hidden" value="${ContractId}" name="contractId" />
				<div class="row">
					<span class='label clearLabel autoWidth'> 
						<input name="" type="checkbox" id='chkUpdateContractConfig' /> 
						<label for='chkUpdateContractConfig'>I agree to update the Contract Configuration.</label>
					</span> 
					<span class="error"></span>
				</div>
				<div class="row" id="usernameDiv">
					<span class="label">
						<label class="required">*</label><label for='txtUsername'>User Name:</label></span> 
						<span class="formfield">
							<input type="text" class='proposalConfigDrpdwn' name='userName' id="txtUpdateContractConfigUserName" placeholder="UserName" id='updateContractConfigUserName' onkeypress="return enterTabPressed(event)"/>
						</span> 
						<span class="error"></span>
				</div>
				<div class="row" id="passwordDiv">
					<span class="label">
						<label class="required">*</label><label for='txtPassword'>Password:</label>
					</span> 
					<span class="formfield">
						<input type="password" class='proposalConfigDrpdwn'  name='password' id="txtUpdateContractConfigPassword" placeholder="Password" id='updateContractConfigPassword' onkeypress="return enterTabPressed(event)" autocomplete="off" />
					</span> 
					<span class="error"></span>
				</div>
			</form:form>
		</div>
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel" id="btnCacelUpdateConfiguration" /> 
			<input type="button" class="button" value="Update Configuration" id="btnUpdateConfiguration"  disabled="disabled"/>
		</div>
	</div>
</div>