<%-- This jsp implementsthe functionality of confirming a provider status as Selected corresponding to a procurement--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/confirmSelectedProvider.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:defineObjects />

<%-- Portlet Action Url Starts --%>
<portlet:actionURL var="confirmSelected" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="submit_action" value="selectedProvider" />
	<portlet:param name="proposalId" value='${proposalId}' />
	<portlet:param name="awardId" value='${awardId}' />
	<portlet:param name="procurementId" value='${procurementId}' />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value='${evaluationGroupId}' />
	<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
	<portlet:param name="competitionPoolId" value='${competitionPoolId}' />
<%-- Code updated for R4 Ends --%>  
</portlet:actionURL>
<%-- Portlet Action Url Starts Ends --%>


<%-- Hidden variables Starts --%>
<input type = "hidden" value="${proposalId}" id="proposalId" name="proposalId"/>
<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
<%-- Code updated for R4 Starts --%>
<input type = "hidden" value="${evaluationGroupId}" name="evaluationGroupId"/>
<input type = "hidden" value="${evaluationPoolMappingId}" name="evaluationPoolMappingId"/>
<input type = "hidden" value="${competitionPoolId}" name="competitionPoolId"/>
<%-- Code updated for R4 Ends --%>
<input type = "hidden" value="${awardId}" id="awardId" name="awardId"/>
<%-- Hidden variables Ends --%>

<%-- Overlay Popup Starts --%>
<form:form id="confirmSelectedForm" action="${confirmSelected}" method ="post" commandName="EvaluationBean" name="confirmSelectedForm">
	<div class='tabularContainer'>
	<div id="ErrorDiv" class="failed breakAll"> </div>
		<p>Please review the information below, enter the total Award amount, and confirm that this Proposal is selected for Award</p>
		<p><span class="required">*</span>Indicates required fields</p>
		<div class="formcontainer">
			<div class="row">
			  	<span class="label"><label for='orgName'>Provider Name:</label></span>
			  	<span class="formfield">${proposalDetails.organizationName}</span>
			</div>
			<div class="row">
			    <span class="label"><label for='proposalTitle'>Proposal Title:</label></span>
			    <span class="formfield">${proposalDetails.proposalTitle}</span>
			</div>
			<div class="row">
				<span class="label"><label for='evaluationScore'>Evaluation Score:</label></span>
		        <span class="formfield">${proposalDetails.score}</span>
			</div>
			<div class="row">
				<span class="label"><label for='txtAwardAmount'><label class="required">*</label>Award Amount ($):</label></span>
				<span class="formfield">
					<form:input name="awardAmount" path="awardAmount" class='input' id='txtAwardAmount' value="${proposalDetails.awardAmount}"></form:input>
				</span>
				<span class="error">
					<form:errors path="awardAmount" cssClass="ValidationError"></form:errors>
				</span>
			</div>
			<div class="row">
				  <span class="label" style='height:100px'><label for='txtEnterComments'>Enter Comments:</label></span>
				  <span class="formfield">
				  	<textarea name="comments" cols="" rows="" class='input' id='txtEnterComments' onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)">${proposalDetails.comments}</textarea>
				  </span>
			</div>
		</div>
		<%-- Button Holder Starts --%>
		<div class="buttonholder">
	    	<input type="button" class="graybtutton" value="Cancel" onclick="javascript:cancelOverlay();" />
	    	<input type="submit" class="button" value="Confirm" id="Confirm" />
	    </div>
	   <%-- Button Holder Ends --%>
	</div>
</form:form>
<%-- Overlay Popup Ends --%>
