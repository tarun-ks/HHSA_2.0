<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/closeContract.js"></script>

<portlet:defineObjects />
<portlet:resourceURL var="closeContract"
	id="closeContract" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${closeContract}'
	id='closeContractUrl' />

<div class="content">
<div class='tabularCustomHead'><span id="contractTypeId">Confirm
Contract Close </span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
<h2>Close Contract</h2>
<div class='hr'></div>

<div class="failed" id="errorMsg"></div>

<p>Are you sure you want to close the contract? Once a contract is closed, no further actions can be taken on 
the contract or its budgets. Please fill in the required information and 
then click on the Close Contract button to continue."</p>

<p><label class="required">*</label>Indicates a required field</p>

<div class="formcontainer">
	<form:form
	id="closeContractForm" action="" method="post"
	name="updateContractConfigForm">
	<input type="hidden" value="${ContractId}" name="contractId" />
	<div class="row">
		<span class='label' style='text-align:left; background:white'>
			<input name="" type="checkbox" id='chkCloseContract' />
			<label for='chkCloseContract'>I agree to close this contract.</label></span>
	<span class="error"></span>
	</div>
	<div class="row" id="closeCommentDiv">
		<textarea name="closeContractComment" id="closeContractComment" cols="" rows="6" class="input proposalConfigDrpdwn floatLft"
		 		maxlength="500px" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);"></textarea>
		<span class="error"></span>
		<div class="required">*</div>
	</div>
	<div class="row" id="usernameDiv">
		<span class="label"><label
		class="required">*</label><label for='txtUsername'>User Name:</label></span> 
		<span
		class="formfield"><input type="text"
		class='proposalConfigDrpdwn' name='userName'
		id="txtCloseContractUserName" placeholder="UserName" maxlength="128"/></span> 
		<span class="error"></span>
	</div>
	<div class="row" id="passwordDiv">
		<span class="label"><label
		class="required">*</label><label for='txtPassword'>Password:</label></span>
		<span class="formfield">
		<input type="password" class='proposalConfigDrpdwn'  name='password' id="txtCloseContractPassword" placeholder="Password" autocomplete="off"/></span>
		<span class="error" id="errorPwd"></span>
	</div>
</form:form></div>

<div class="buttonholder"><input type="button" class="graybtutton"
	title="Cancel" value="No, do NOT close this Contract" id="btnCancelCloseContract" /> <input
	type="button" class="button" title="Update Configuration"
	value="Yes, close this Contract" id="btnCloseContract" /></div>

</div>
</div>