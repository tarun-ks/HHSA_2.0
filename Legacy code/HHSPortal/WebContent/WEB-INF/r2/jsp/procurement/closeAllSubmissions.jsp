<%-- This jsp file has been added in R4--%>
<%-- JSP for overlay pop up for close all submissions  --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/closeAllSubmission.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="closeAllSubmissionUrl" escapeXml="false">
	<portlet:param name="submit_action" value="closeSubmissionsAction"/>
	<portlet:param name="action" value="propEval" />
</portlet:actionURL>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationSummary"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="action" value="propEval"/>
</portlet:renderURL>
<form:form id="closeAllSubmissionForm" action="${closeAllSubmissionUrl}" method="post"
		commandName="AuthenticationBean" name="closeAllSubmissionForm">
		<div class='tabularContainer'>
		<h2>Close ALL Submissions</h2>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
	<div class='hr'></div>
	<p>Are you sure you want to close ALL submissions for this procurement?</p>
	<p style="color:#FF0000">Closing ALL submissions for this procurement will close this remaining open evaluation group and will NOT
	allow eligible providers to submit proposals for this procurement any longer.
	</p>
	<p>The procurement will move to Proposals Received status and will generate the Accept Proposal tasks for
	the remaining proposals in this evaluation group.
	</p>
	<div id="errorPlacementWrapperAllSubmission"> 
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errorPlacementAllSubmission" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" title="Close" alt="Close"
				onclick="showMe('errorPlacementWrapperAllSubmission', this)"/>
			</div>
		</c:if>
	</div>
	<div class="formcontainer" id="closeSubmissions">
		<p><input type="checkbox" id='chkCloseSubmissions' onclick="hideUnhideUsername(this);"/>
		 
		<label for='chkCloseSubmissions'>Yes, I understand that closing this evaluation group will not allow proposals to be submitted anymore for this procurement.</label></p>
    	<div>&nbsp;</div>
		<div id="authenticateAllSubmission">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" id="userName" autocomplete="off"/>
				</span>
				<span class="error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" id="password" autocomplete="off"/>
				</span>
				<span class="error">
				</span>
			</div>
		</div>
	</div>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" id="doNotCloseallSubmission" value="No, do NOT close ALL submissions" onclick="closeOverLay();" />
    	<input type="submit" id="yesCloseAllSubmission" value="Yes, close ALL submissions"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
    <input type="hidden" name="evaluationGroupId" id="evaluationGroupId" value="${evaluationGroupId}"/>
    <input type="hidden" name="closeGroup" id="closeGroup" value="${closeGroup}"/>
    <input type="hidden" name="buttonValue" id="buttonValue" value="${buttonValue}"/>
 </form:form>
