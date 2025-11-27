<%-- This JSP is for S420 Invoice Submission Confirmation overlay --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/invoiceSubmissionConfirmation.js"></script>
	
<portlet:defineObjects />
<%-- This portlet resource maps the Action in controller on Invoice Overlay Submission --%>
<portlet:actionURL var="submitConfirmation" escapeXml="false">
		<portlet:param name="action" value="invoiceHandler"/>
		<portlet:param name="submit_action" value="submitConfirmation"/>
</portlet:actionURL>
<input type="hidden" name="submitConfirmation" id="submitConfirmation"  value="${submitConfirmation}"/>

<%-- This portlet resource maps the Ajax Resource Mapping in controller for Validating User --%>
<portlet:resourceURL var="validateUser" id="validateUser" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser"  value="${validateUser}"/> 

<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Confirm Submission
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Submit Invoice</h2>
	<div class='hr'></div>
		<div class="failed" id="errorMsg"></div>
		<p>Are you sure you want to submit this Invoice?</p>
	<div class="formcontainer">
	<form:form id="submitInvoiceForm" action="" method="post" name="submitInvoiceForm">

	<input type="hidden" value="${asContractId}" name="contractId" id="contractId"/>	
	<input type="hidden" value="${asBudgetId}" name="budgetId" id="budgetId"/>
	<input type="hidden" value="${asInvoiceId}" name="invoiceId" id="invoiceId"/>
	<input type="hidden" value="${publicCommentArea}" name="publicCommentArea" id="publicCommentArea"/>
<%--	<input type="hidden" value="${invoiceStatus}" name="invoiceStatus" id="invoiceStatus"/>--%>
	
	<div class="row">
	<span class='label' style='text-align: left; background: white; width: 800px;'> 
		
		<label for='chkSubmitInvoiceForm'>
		<p><i><input name="" type="checkbox" id='chkSubmitInvoiceForm'/>I hereby certify that the expenditures reported herein accurately correspond with the books and records of this organization and reflect only those expenses incurred and paid by the organization based solely on the contract and in accordance with a budget previously approved by the City.
		</i></p>
		</label>
	</span> 
		<span class="error"></span>
	</div>	
	<%-- <p><label class="required">*</label>Indicates a required field</p> --%>
	<div class="row" id="usernameDiv">
	<span class="label">
		<label class="required">*</label><label for='txtUsername'>User Name:</label>
	</span> 
		<span class="formfield">
			<input type="text" class='proposalConfigDrpdwn' id="txtSubmitCBUserName" name='userName' placeholder="UserName"  maxlength="128"/>
		</span>
		<span class="error" id="userNameErrorMsgHolder"></span>
	</div>
	<div class="row" id="passwordDiv">
	<span class="label">
		<label class="required">*</label><label for='txtPassword'>Password:</label>
	</span> 
	<span class="formfield">
		<input type="password" class='proposalConfigDrpdwn' name='password' id="txtSubmitCBPassword" placeholder="Password"	autocomplete="off" />
	</span>
	<span class="error" id="pwdErrorMsgHolder"></span>
	</div>
</form:form></div>
<br>
<div class="buttonholder">
	<input type="button" class="graybtutton" title='No, do NOT submit this Invoice' value="No, do NOT submit this Invoice" id="btnNotSubmitInvoice" /> 
	<input type="button" class="button" title="Yes, submit this Invoice" value="Yes, submit this Invoice" id="btnSubmitInvoice" />
</div>
</div>

<%-- Overlay Popup Ends --%>
</div>