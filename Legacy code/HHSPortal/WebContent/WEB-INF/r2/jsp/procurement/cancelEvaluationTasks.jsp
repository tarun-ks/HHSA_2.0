<%-- This jsp is used to create cancel evaluation task popup --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelEvaluationTasks.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="cancelEvaluationTasksUrl" escapeXml="false">
	<portlet:param name="submit_action" value="cancelEvaluationTasks"/>
	<portlet:param name="action" value="propEval"/>
</portlet:actionURL>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="topLevelFromRequest" value="ProcurementRoadmapDetails"/>
	<portlet:param name="midLevelFromRequest" value="ProcurementSummary"/>
	<portlet:param name="paramValue" value="cancelEvaluationTask"/>
	<portlet:param name="render_action" value="viewProcurement"/>
</portlet:renderURL>
<form:form id="cancelEvaluationTasksForm" action="${cancelEvaluationTasksUrl}" method="post"
		commandName="AuthenticationBean" name="cancelEvaluationTasksForm">
		<%-- Code updated for R4 Starts --%>
	<input type="hidden" value="${redirectURL}" id="redirectURL"/>
	<%-- Code updated for R4 Ends --%>
	<div class='tabularContainer'>
	<h2>Cancel Evaluation Tasks</h2>
	<div class='hr'></div>
	<p>Are you sure you want to cancel the entire Evaluation Process?</p>
	<p>Cancelling an Evaluation will erase all the existing Evaluations of the Evaluators on this team. If Evaluations have started please notify all Evaluators to stop working on any outstanding Evaluations and to record existing scores outside the system.
	</p>
	<div id="errorPlacementWrapper"> 
	<c:if test="${message ne null}">
				<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('errorPlacementWrapper', this)">
				</div>
			</c:if>
		</div>
	<div class="formcontainer" id="cancelEvaluationTask">
		<p><input type="checkbox" id='chkCancelEvaluationTask' onclick="hideUnhideUsername(this);"/>
		<label for='chkCancelEvaluationTask'>Yes, I understand that any evaluation progress made in the system will be lost.</label></p>
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
    	<input type="button" class="graybtutton" id="doNotCancelEvaluationTasks" value="Cancel" onclick="cancelOverLay();" />
    	<input type="submit" class="" id="yesCancelEvaluationTasks" value="Yes, cancel the evaluation process"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
    <%-- Code updated for R4 Starts --%>
    <input type="hidden" name="evaluationPoolMappingId" id="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
    <%-- Code updated for R4 Ends --%>
 </form:form>
  <%-- Overlay Popup Ends --%>
