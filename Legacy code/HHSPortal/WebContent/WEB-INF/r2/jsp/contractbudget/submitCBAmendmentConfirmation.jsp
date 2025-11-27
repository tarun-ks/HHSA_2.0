<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%-- This JSP open Budget amendment Overlay screen on click of submit button on Budget amendment main screen --%>
<%-- Submit Budget Update Overlay Popup Start --%>
<portlet:defineObjects />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/submitCBAmendmentConfirmation.js"></script>
<%--Define Action mapping on click of Submit Button Start  --%>	
<portlet:actionURL var="submitConfirmation" escapeXml="false">
		<portlet:param name="action" value="contractBudgetHandler"/>
		<portlet:param name="submit_action" value="submitAmendConfirmation"/>
</portlet:actionURL>
<input type="hidden" name="submitConfirmation" id="submitConfirmation"  value="${submitConfirmation}"/>
<%--Define Action mapping on click of Submit Button End  --%>

<%--Define resource Mapping to validate User start  --%>
<portlet:resourceURL var="validateUser" id="validateUser" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser"  value="${validateUser}"/> 
<%--Define resource Mapping to validate User End  --%>

<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Confirm Submission
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Submit Budget Amendment</h2>
	<div class='hr'></div>
	
		<div class="failed" id="errorMsg"></div>
		<p>Are you sure you want to submit the Contract Budget Amendment?</p>
		<p><label class="required">*</label>Indicates a required field</p>
		
<%-- Form tag Starts --%>

<form:form id="submitCBForm" action="" method="post" name="submitCBForm">

	<input type="hidden" value="${asContractId}" name="contractId" id="contractId"/>	
	<input type="hidden" value="${asBudgetId}" name="budgetId" id="budgetId"/>
	<input type="hidden" value="${Agency_ID}" name="agencyId" id="agencyId"/>
	
<div class="formcontainer">	
	<div class="row">
		<span class='label clearLabel autoWidth'> 
			<input name="" type="checkbox" id='chkSubmitCBForm'/> 
			<label for='chkSubmitCBForm'>I agree to submit this Budget Amendment to the Agency for review.</label>
		</span>
		<span class="error"></span>
	</div>	
	
	<div class="row" id="usernameDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitCBUserName'>User Name:</label>
		</span> 
		<span class="formfield">
			<input type="text" class='proposalConfigDrpdwn' id="txtSubmitCBUserName" name='userName' maxlength="128"/>
		</span> 
		<span class="error"></span>
	</div>
	
	<div class="row" id="passwordDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitCBPassword'>Password:</label>
		</span> 
		<span class="formfield">
			<input type="password" class='proposalConfigDrpdwn' name='password' id="txtSubmitCBPassword" autocomplete="off" />
		</span> 
		<span class="error"></span>
	</div>
</div>
</form:form>

<%-- Form tag End --%>
	<div class="buttonholder">
		<input type="button" class="graybtutton" title="" value="No, do NOT submit this Amendment" id="btnNotSubmitCB" /> 
		<input type="button" class="button" title=""	value="Yes, submit this Amendment" id="btnSubmitCB" />
	</div>
</div>

<%-- Overlay Popup Ends --%>

</div>