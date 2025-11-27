<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@page import="com.nyc.hhs.model.BudgetAdvanceBean"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/budgetAdvance.js"></script>
<style type="text/css">
.tabularContainer .formcontainer .row span.error{
	width:100% !important
}
.tabularContainer .formcontainer .row span.error label{
	color:red;
	font-size:12px
}
</style>
<portlet:defineObjects />
<portlet:actionURL var="requestAdvanceUrl" escapeXml="false">
	<portlet:param name="action" value="budgetListAction" />
	<portlet:param name="submit_action" value="requestAdvance" />
</portlet:actionURL>

<portlet:resourceURL var="requestAdvanceUrl" id="requestAdvance" escapeXml="false">	
</portlet:resourceURL>

<input type="hidden" id="orgType" value="${org_type}"/>	
<input id="hdnRequestAdvanceUrl" type="hidden" value="${requestAdvanceUrl}" />
<script type="text/javascript">
var advanceSubmitted= "<fmt:message key='advanceSubmitted'/>";
</script>

<form:form id="requestAdvance" action="${requestAdvanceUrlR}"
	method="post" commandName="BudgetAdvance">
	<c:choose>
		<c:when test="${org_type eq 'provider_org'}">
			<div class='tabularCustomHead'><span>Request Advance</span><a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
			<div class='tabularContainer' style='border-bottom:none'>
				<div id="ErrorDiv" class="failed breakAll"> </div>
				<h2>Request Advance</h2>
				<div class='hr'></div>
				<p>To request an advance please enter the advance amount and
					description and click the 'Request Advance' button to send the request.
				</p>
			</div>
		</c:when>
		<c:otherwise>
			<div class='tabularCustomHead'><span>Initiate Advance</span><a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
			<div class='tabularContainer' style='border-bottom:none'>
				<div id="ErrorDiv" class="failed breakAll"> </div>
				<h2>Initiate Advance </h2>
				<div class='hr'></div>
				<p>To initiate an advance please enter the advance amount and description and click 
					the 'Initiate Advance' button to send the request.
				</p>
			</div>
		</c:otherwise>
	</c:choose>
	
	<div class='tabularContainer'>
		<p><label class="required">*</label>Indicates a required field</p>
		<div>&nbsp;</div>

	<div class="formcontainer">
	
		<div class="row">
			<span class="label equalForms"><label>CT#:</label></span>
			<span class="formfield equalForms">${BudgetAdvanceBean.ctNumber}</span>
		</div>
		<div class="row">
			<span class="label equalForms"><label>Provider:</label></span>
			<span class="formfield equalForms">${BudgetAdvanceBean.providerName}</span>
		</div>
		<div class="row">
			<span class="label equalForms"><label>FiscalYear:</label></span>
			<span class="formfield equalForms">FY${BudgetAdvanceBean.fiscalYear}</span>
		</div>
		<div class="row">
			<span class="label equalForms"><label>Advance Request Date:</label></span> 
			<span class="formfield equalForms">${BudgetAdvanceBean.advRequestedDate}</span>
		</div>
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>
				<label for='txtAdvAmtRequest'>Advance Amount Requested($):</label>
			</span> 
			<span class="formfield equalForms">
				<form:input path="advAmntRequested" cssClass="input" id="advAmntRequested" value="00.00" maxlength="19" validate="number"/>
				<form:errors path="advAmntRequested"  cssClass="ValidationError"></form:errors>
				<span class="error"></span>
			</span>
			
		</div>
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>
				<label for='txtAdvDescription'>Advance Description:</label>
			</span>
			<span class="formfield equalForms">
				<form:input path="description" cssClass="input" maxlength="50" id="description"/>
				<form:errors path="description"  cssClass="ValidationError"></form:errors>
				<span class="error"></span>
			</span>
		</div>
		
	</div>
	
	<form:hidden path="budgetId" value="${BudgetAdvanceBean.budgetId}"/>
	<form:hidden path="contractId" value="${BudgetAdvanceBean.contractId}"/>
	<form:hidden path="fiscalYear" value="${BudgetAdvanceBean.fiscalYear}"/>
	<form:hidden path="procId" value="${BudgetAdvanceBean.procId}"/>
	<form:hidden path="procTitle" value="${BudgetAdvanceBean.procTitle}"/>
	<form:hidden path="epin" value="${BudgetAdvanceBean.epin}"/>
	<form:hidden path="agencyId" value="${BudgetAdvanceBean.agencyId}"/>
	<form:hidden path="orgId" value="${BudgetAdvanceBean.orgId}"/>
	<form:hidden path="programId" value="${BudgetAdvanceBean.programId}"/>
	
	<div class="buttonholder">
		<input type="button" class="graybtutton" title="Cancel" value="Cancel" id="cancelBtn" /> 
		<c:choose>
			<c:when test="${org_type eq 'provider_org'}">
				<input type="submit" class="button" title="Request Advance" value="Request Advance" id="btnSubmit" />
			</c:when>
			<c:otherwise>
				<input type="submit" class="button" title="Initiate Advance" value="Initiate Advance" id="btnSubmit" />
			</c:otherwise>
		</c:choose>
	</div>
	</div>

	<div>
		<a href="javascript:void(0);" class="exit-panel"></a>
	</div>
	<%-- Overlay Popup Ends --%>
</form:form>
