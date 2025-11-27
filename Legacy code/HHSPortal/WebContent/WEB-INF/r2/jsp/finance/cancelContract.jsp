<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/cancelContract.js"></script>

<portlet:defineObjects />

<portlet:resourceURL var="cancelContract"
	id="cancelContract" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${cancelContract}'
	id='cancelContractUrl' />

<div class="content">
<div class='tabularCustomHead'><span id="contractTypeId">Confirm
Contract Cancellation</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
<h2>Cancel Contract</h2>
<div class='hr'></div>

<div class="failed" id="ErrorDiv"></div>

<p>Please enter the reason for cancelling the contract in the comments section below and then click on the Cancel Contract button.</p>

<p><label class="required">*</label>Indicates a required field</p>
<form:form
	id="cancelContractForm" action="" method="post"
	name="cancelContractForm">
<div class="formcontainer">
	
	<input type="hidden" value="${ContractId}" name="contractId" />
	<div class="row">
		<span class='label' style='text-align:left; background:white'>
			<input name="" type="checkbox" id='chkCancelContract' onclick="hideUnhideUsername(this);"/>
			<label for='chkCancelContract'>I agree to cancel this contract.</label></span>
	<span class="error"></span>
	</div>
	<div class="row">
		<span class='label' style='text-align:left; background:white'>
			<input name="reuseEpin" type="checkbox" id='reuseEpin' value="Y"/>
			<label for='reuseEpin'>Reuse E-PIN for this contract.</label></span>
	<span class="error"></span>
	</div>
	
	<div id="authenticate">
	<div class="row" id="cancelCommentDiv">
			<span class=" "></span>
		<span class="">
		<textarea name="cancelContractComment" id="cancelContractComment" cols="" rows="6" class="input proposalConfigDrpdwn" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);" path="reason"></textarea><label class="required">*</label>
		<span class="error"></span></span>
	</div>
	<div class="row" id="usernameDiv">
		<span class="label"><label
		class="required">*</label><label for='txtUsername'>User Name:</label></span> 
		<span
		class="formfield"><input type="text"
		class='proposalConfigDrpdwn' name='userName'
		id="txtCancelContractUserName" placeholder="UserName" /></span> 
		<span class="error"></span>
	</div>
	<div class="row" id="passwordDiv">
		<span class="label"><label
		class="required">*</label><label for='txtPassword'>Password:</label></span>
		<span
		class="formfield"><input type="password"
		class='proposalConfigDrpdwn' name='password'
		id="txtCancelContractPassword" placeholder="Password" autocomplete="off" /></span>
		<span class="error"></span>
	</div>
	</div>
</div>

<div class="buttonholder">
	<input type="button" class="graybtutton" value="No, do NOT cancel this Contract" id="btnNoCancelContract" onclick="clearAndCloseOverLay();"/> 
 	<input type="submit" class="button" value="Yes, cancel this Contract" id="btnYesCancelContract" />
 </div>
</form:form>
</div>
</div>