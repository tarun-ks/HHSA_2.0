<%-- JSP for overlay popup for close submissions  --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/closeSubmission.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="closeSubmissionsUrl" escapeXml="false">
	<portlet:param name="submit_action" value="closeSubmissionsAction"/>
	<portlet:param name="action" value="propEval" />
</portlet:actionURL>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationSummary"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="action" value="propEval"/>
</portlet:renderURL>
<form:form id="closeSubmissionsForm" action="${closeSubmissionsUrl}" method="post"
		commandName="AuthenticationBean" name="closeSubmissionsForm">
		<div class='tabularContainer'>
		<h2>Close Submissions</h2>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
	<div class='hr'></div>
	<p>Are you sure you want to Close Submissions to the RFP?</p>
	<p>Closing submissions will enforce the proposal deadline and prevent providers from submitting and retracting proposals. 
	   After closing, ACCO will receive &quot;Accept Proposal&quot; tasks, and the Procurement status will be changed to &quot;Proposals Received&quot;.
	</p>
	<div>&nbsp;</div>
	<div class='formcontainer' id="proposalInfoDiv">
	<div class="row">
		<span class="label">Updated Proposal Due Date:</span> 
		<span class='formfield'><fmt:formatDate pattern="MM/dd/yyyy " type="both"  value="${currentTimeStamp}" /><fmt:message key="AT_THE_RATE_SYMBOL"/>  ${releaseTime}&nbsp;EST</span>
	</div>
	<div class="row">
		<span class="label">Number of Providers Proposed:</span> 
		<span class='formfield'>${loNoOfProviders}</span>
	</div>	
	<div class="row">	
		<span class="label">Number of Proposals Received:</span> 
		<span class='formfield'>${loNoOfProposals}</span>
	</div>
	</div>
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
		 
		<label for='chkCloseSubmissions'>Yes, I understand that closing submissions will prevent proposals from being submitted.</label></p>
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
    	<input type="button" class="graybtutton" title="No, do NOT close submissions" id="doNotCloseSubmissions" value="No, do NOT close submissions" onclick="closeOverLay();" />
    	<input type="submit" class="" title="Yes, close submissions" id="yesCloseSubmissions" value="Yes, close submissions"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
    <%-- Code updated for R4 Starts --%>
    <input type="hidden" name="evaluationGroupId" id="evaluationGroupId" value="${evaluationGroupId}"/>
    <input type="hidden" name="closeGroup" id="closeGroup" value="${closeGroup}"/>
    <input type="hidden" name="buttonValue" id="buttonValue" value="${buttonValue}"/>
 <%-- Code updated for R4 Ends --%>
 </form:form>
