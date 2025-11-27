<%-- This JSP was added for R4--%>
<%-- JSP for overlay pop up for close group  --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/closeGroup.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="closeGroupUrl" escapeXml="false">
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
<form:form id="closeGroupForm" action="${closeGroupUrl}" method="post"
		commandName="AuthenticationBean" name="closeGroupForm">
		<div class='tabularContainer'>
		<h2>Close Group: Allow Submissions</h2>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
	<div class='hr'></div>
	<p>Are you sure you want to close this evaluation group?</p>
	<p>Upon closure, proposals will no longer be able to be submitted for this specific evaluation group. However, the
	system will automatically create a new evaluation group that providers can continue to submit proposals for.
	</p>
	<p>Accept Proposal tasks will be generated for the proposals contained in this evaluation group.
	</p>
	<div id="errorPlacementWrapper"> 
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" title="Close" alt="Close"
				onclick="showMe('errorPlacementWrapper', this)"/>
			</div>
		</c:if>
	</div>
	<div class="formcontainer" id="closeSubmissions">
		<p><input type="checkbox" id='chkCloseSubmissions' onclick="hideUnhideUsername(this);"/>
		 
		<label for='chkCloseSubmissions'>Yes, I understand that closing this evaluation group will not allow proposals to be submitted anymore for this group.</label></p>
    	<div>&nbsp;</div>
		<div id="authenticate">
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
    	<input type="button" class="graybtutton" title="No, do NOT close evaluation group" id="doNotCloseGroup" value="No, do NOT close evaluation group" onclick="closeOverLay();" />
    	<input type="submit" class="" title="Yes, close evaluation group" id="yesCloseGroup" value="Yes, close evaluation group"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
    <input type="hidden" name="evaluationGroupId" id="evaluationGroupId" value="${evaluationGroupId}"/>
    <input type="hidden" name="closeGroup" id="closeGroup" value="${closeGroup}"/>
    <input type="hidden" name="buttonValue" id="buttonValue" value="${buttonValue}"/>
 </form:form>
