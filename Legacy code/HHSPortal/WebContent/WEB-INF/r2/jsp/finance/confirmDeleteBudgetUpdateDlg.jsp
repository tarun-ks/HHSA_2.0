
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/confirmDeleteBudgetUpdateDlg.js"></script>

<portlet:defineObjects />

<portlet:resourceURL var='confirmDeleteBudgetUpdate' id='confirmDeleteBudgetUpdate' escapeXml='false'/>
	<input type="hidden" id="hdnConfirmDeleteBudgetUpdate" value="${confirmDeleteBudgetUpdate}"/>  

	
<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Confirm Contract Budget Update Removal
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Remove Contract Budget Update</h2>
	<div class='hr'></div>

	<div class="failed" id="ErrorDiv"></div>
	
	<p>Please enter a reason for removing the Contract Budget Update in the comments section below.</p>
	
	<p><label class="required">*</label>Indicates a required field</p>

<form:form id="confirmDeleteBudgetUpdateForm" action="" method="post" name="confirmDeleteBudgetUpdateForm">

	<input type="hidden" value="${loBudgetList.contractId}" name="contractId" id="contractId"/>	
	<input type="hidden" value="${loBudgetList.budgetType}" name="budgetType" id="budgetType"/>
	<input type="hidden" value="${loBudgetList.fiscalYearId}" name="fiscalYearIds" id="fiscalYearIds"/>
	<input type="hidden" value="" name="delBudgetUpdateComment" id="delBudgetUpdateComment"/>
	
	<div class="formcontainer">
		<div class="row">
		<span class='label' style='text-align:left; background:white'>
			<input name="" type="checkbox" id='chkDeleteBudgetUpdate' onclick="hideUnhideUsername(this);"/>
			<label for='chkDeleteBudgetUpdate'>I agree to delete this contract.</label></span>
			<span class="error"></span>
		</div>
		<div id="authenticate">
			<div class="row" id="deleteCommentDiv">
					<span class=" "></span>
				<span class="">
				<textarea name="deleteBudgetUpdateComment" id="deleteBudgetUpdateComment" cols="" rows="6" class="input proposalConfigDrpdwn" onkeyup="setMaxLength(this,500);" onkeypress="setMaxLength(this,500);" path="reason"></textarea><label class="required">*</label>
				<span class="error"></span></span>
			</div>
			<div class="row" id="usernameDiv">
				<span class="label">
					<label class="required">*</label><label for='txtUsername'>User Name:</label>
				</span> 
				<span class="formfield">
				    <input type="text" class='proposalConfigDrpdwn' name='userName' id="txtDeleteBudgetUpdateUserName" placeholder="UserName" />
				</span> 
				<span class="error"></span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label"><label lass="required">*</label><label for='txtPassword'>Password:</label></span>
				<span class="formfield">
				 <input type="password" class='proposalConfigDrpdwn' name='password' id="txtDeleteBudgetUpdatePassword" placeholder="Password" autocomplete="off" />
				</span>
				<span class="error"></span>
			</div>
		</div>

	</div>
	
<div class="buttonholder">
    <input type="button" class="graybtutton"  id="btnNoDeleteBudgetUpdate"title="Cancel" value="No, do not remove" onclick="clearAndCloseOverLay()" />
	<input type="submit" class="button" value="Yes, Remove" id="btnYesDeleteBudgetUpdate" />
</div>

</form:form>


</div>


</div>




