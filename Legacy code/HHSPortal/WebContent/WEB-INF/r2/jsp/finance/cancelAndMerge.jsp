<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%-- This JSP open Cancel And Merge Overlay screen on click of submit button on budget list main screen --%>
<%-- Submit Budget Update Overlay Popup Start --%>
<portlet:defineObjects />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/cancelAndMerge.js"></script>
<%--Define Action mapping on click of Submit Button Start  --%>	
 <portlet:resourceURL var='CancelAndMergeOverlay' id='CancelAndMergeOverlay' escapeXml='false'/>
	<input type="hidden" id="CancelAndMergeOverlayUrl" value="${CancelAndMergeOverlay}"/>
	
<%--Define Action mapping on click of Submit Button End  --%>

<%--Define resource Mapping to validate User start  --%>
<portlet:resourceURL var="validateUser" id="validateUser" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser"  value="${validateUser}"/> 
<%--Define resource Mapping to validate User End  --%>

<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Cancel and Merge Confirmation
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Cancel and Merge Confirmation</h2>
	<div class='hr'></div>
	
		<div class="failed" id="errorMsg"></div>
		<p>Are you sure want to cancel and merge the Fiscal Year? Both the Amendment Budget and
		New FY Budget will be deleted permanently. You can select to have the New FY Configuration 
		Task launched automatically with merged values.</p>
		<p><label class="required">*</label>Indicates a required field</p>
		
<%-- Form tag Starts --%>

<form:form id="submitMRForm" action="" method="post" name="submitMRForm">
	<input type="hidden" value="${BudgetId}" name="budgetId" id="budgetId"/>
	<input type="hidden" value="${contractId}" name="contractId" id="contractId"/>
	<input type="hidden" value="${City_ID}" name="cityId" id="cityId"/>
	<input type="hidden" value="${fiscalYearId}" name="fiscalYearId" id="fiscalYearId"/>
	
<div class="formcontainer">	
<div class="row">
		<span class='label clearLabel autoWidth'> 
			<input name="" type="checkbox" id='chkSubmitForm'/>
			<label for='chkSubmitForm'>Re-launch New FY Configuration Task</label>
		</span>
		<span class="error"></span>
	</div>
	<div class="row">
		<span class='label clearLabel autoWidth'> 
			<input name="" type="checkbox" id='chkSubmitMRForm'/>
			<label for='chkSubmitMRForm'>I agree to Cancel and Merge Fiscal Year</label>
		</span>
		<span class="error"></span>
	</div>	
	
	<div class="row" id="usernameDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitMRUserName'>User Name:</label>
		</span> 
		<span class="formfield">
			<input type="text" class='proposalConfigDrpdwn' id="txtSubmitMRUserName" name='userName' maxlength="128"/>
		</span> 
		<span class="error"></span>
	</div>
	
	<div class="row" id="passwordDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitMRPassword'>Password:</label>
		</span> 
		<span class="formfield">
			<input type="password" class='proposalConfigDrpdwn' name='password' id="txtSubmitMRPassword" autocomplete="off" />
		</span> 
		<span class="error"></span>
	</div>
</div>
</form:form>

<%-- Form tag End --%>
	<div class="buttonholder">
		<input type="button" class="graybtutton" title="" value="No, Discard" id="btnNotSubmit" /> 
		<input type="button" class="button" title=""	value="Yes, Cancel and Merge Fiscal Year" id="btnMarkAsRegSubmit" />
	</div>
</div>

<%-- Overlay Popup Ends --%>

</div>

<portlet:resourceURL var="fyConfigConfirmUrl" id="fyConfigConfirm" escapeXml="false">	
	<portlet:param name="contractId" value="${ContractId}" />
	<portlet:param name="contractTypeId" value="${ContractTypeId}" />
	<portlet:param name="fyConfigFiscalYear" value="${fiscalYearId}" />
</portlet:resourceURL>
<input type="hidden" id="fyConfigConfirmUrl" value="${fyConfigConfirmUrl}"/>