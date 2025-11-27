<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/submitCBConfirmation.js"></script>
	
<portlet:actionURL var="submitConfirmation" escapeXml="false">
		<portlet:param name="action" value="contractBudgetHandler"/>
		<portlet:param name="submit_action" value="submitConfirmation"/>
</portlet:actionURL>
<input type="hidden" name="submitConfirmation" id="submitConfirmation"  value="${submitConfirmation}"/>

<portlet:resourceURL var="validateUser" id="validateUser" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser"  value="${validateUser}"/> 

<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Confirm Submission
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Submit Contract Budget</h2>
	<div class='hr'></div>
	
		<div class="failed" id="errorMsg"></div>
		<p>Are you sure you want to submit this Contract Budget?</p>
		<p><label class="required">*</label>Indicates a required field</p>
		

<form:form id="submitCBForm" action="" method="post" name="submitCBForm">

	<input type="hidden" value="${asContractId}" name="contractId" id="contractId"/>	
	<input type="hidden" value="${asBudgetId}" name="budgetId" id="budgetId"/>
	<input type="hidden" value="${Agency_ID}" name="agencyId" id="agencyId"/>

<div class="formcontainer">	
	<div class="row">
		<span class='label clearLabel autoWidth'> 
			<input name="" type="checkbox" id='chkSubmitCBForm'/> 
			<label for='chkSubmitCBForm'>I agree to submit this Budget to the Agency for review.</label>
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
			<input type="password" class='proposalConfigDrpdwn' name='password' id="txtSubmitCBPassword" autocomplete="off"/>
		</span> 
		<span class="error"></span>
	</div>
</div>
</form:form>


<div class="buttonholder">
	<input type="button" class="graybtutton" title="" value="No, do NOT submit this Budget" id="btnNotSubmitCB" /> 
	<input type="button" class="button" title="" value="Yes, submit this Budget" id="btnSubmitCB" />
</div>
</div>

<%-- Overlay Popup Ends --%>

</div>