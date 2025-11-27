<%-- This jsp implementsthe functionality of confirming a provider status as Not Selected corresponding to a procurement--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<%-- Portlet Action Url Starts --%>
<portlet:actionURL var="confirmNotSelected" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="proposalId" value='${proposalId}' />
	<portlet:param name="procurementId" value='${procurementId}' />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value='${evaluationGroupId}' />
	<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
	<portlet:param name="competitionPoolId" value='${competitionPoolId}' />
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="submit_action" value="notSelectedProvider" />
</portlet:actionURL>
<%-- Portlet Action Url Ends --%>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/confirmSelectedProvider.js"></script>
<input type = "hidden" value="${proposalId}" id="proposalId" name="proposalId"/>
<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
<%-- Code updated for R4 Starts --%>
<input type = "hidden" value="${evaluationGroupId}" name="evaluationGroupId"/>
<input type = "hidden" value="${evaluationPoolMappingId}" name="evaluationPoolMappingId"/>
<input type = "hidden" value="${competitionPoolId}" name="competitionPoolId"/>
<%-- Code updated for R4 Ends --%>
<%-- Overlay Popup Starts --%>
<form:form id="confirmNotSelectedForm" action="${confirmNotSelected}" method ="post" commandName="EvaluationBean" name="confirmNotSelectedForm">
<div class='tabularContainer'>
<div id="ErrorDiv" class="failed breakAll"> </div>
	<p>Please confirm that this Proposal is not selected for award. For all skipped Proposals, you MUST enter a reason in the Comments field.</p>
	
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
				  <span class="label" style='height:100px'><label for='txtEnterComments'>Enter Comments:</label></span>
				  <span class="formfield">
				  	<textarea name="comments" cols="" rows=""  class='input' id='txtEnterComments' onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)">${proposalDetails.comments}</textarea>
				  </span>
			</div>
		</div>
		<%-- Button Holder Starts --%>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" value="Cancel" onclick="javascript:cancelNotSelectedOverlay();" />
    	<input type="submit" class="button" value="Confirm" id="Confirm" onclick="nonSelected('nonSelected')">
    </div>
    <%-- Button Holder Ends --%>
</div>
</form:form>
  <%-- Overlay Popup Ends --%>
