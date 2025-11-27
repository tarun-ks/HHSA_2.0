<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/deleteContract.js"></script>

<portlet:defineObjects />

<portlet:resourceURL var="deleteContract"
	id="deleteContract" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${deleteContract}'
	id='deleteContractUrl' />

<div class="content">
<div class='tabularCustomHead'><span id="contractTypeId">Confirm
Contract Deletion</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
<h2>Delete Contract</h2>
<div class='hr'></div>

<div class="failed" id="ErrorDiv"></div>

<p>Please enter the reason for deleting the contract in the comments section below and then click on the Delete Contract button.</p>

<p><label class="required">*</label>Indicates a required field</p>
<form:form
	id="deleteContractForm" action="" method="post"
	name="deleteContractForm">
<div class="formcontainer">
	
	<input type="hidden" value="${ContractId}" name="contractId" />
	<div class="row">
		<span class='label' style='text-align:left; background:white'>
			<input name="" type="checkbox" id='chkDeleteContract' onclick="hideUnhideUsername(this);"/>
			<label for='chkDeleteContract'>I agree to delete this contract.</label></span>
	<span class="error"></span>
	</div>
	<div id="authenticate">
	<div class="row" id="deleteCommentDiv">
			<span class=" "></span>
		<span class="">
		<textarea name="deleteContractComment" id="deleteContractComment" cols="" rows="6" class="input proposalConfigDrpdwn" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);" path="reason"></textarea><label class="required">*</label>
		<span class="error"></span></span>
	</div>
	<div class="row" id="usernameDiv">
		<span class="label"><label
		class="required">*</label><label for='txtUsername'>User Name:</label></span> 
		<span
		class="formfield"><input type="text"
		class='proposalConfigDrpdwn' name='userName'
		id="txtDeleteContractUserName" placeholder="UserName" /></span> 
		<span class="error"></span>
	</div>
	<div class="row" id="passwordDiv">
		<span class="label"><label
		class="required">*</label><label for='txtPassword'>Password:</label></span>
		<span
		class="formfield"><input type="password"
		class='proposalConfigDrpdwn' name='password'
		id="txtDeleteContractPassword" placeholder="Password" autocomplete="off" /></span>
		<span class="error"></span>
	</div>
	</div>
</div>

<div class="buttonholder">
	<input type="button" class="graybtutton" value="No, do NOT delete this Contract" id="btnNoDeleteContract" onclick="clearAndCloseOverLay();"/> 
 	<input type="submit" class="button" value="Yes, delete this Contract" id="btnYesDeleteContract" />
 </div>
</form:form>
</div>
</div>