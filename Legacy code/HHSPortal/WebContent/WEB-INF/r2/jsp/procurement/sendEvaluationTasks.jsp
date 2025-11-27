<%-- This JSP is used to create the send evaluation task popup --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/sendEvaluationTasks.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="sendEvaluationUrl" escapeXml="false">
<portlet:param name="action" value="propEval"/>
	<portlet:param name="submit_action" value="sendEvaluation"/>
	
</portlet:actionURL>

<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${sendEvaluationId}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
	<portlet:param name="competitionPoolId" value="${competitionPoolId}"/>
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationStatus"/>
	<portlet:param name="ES" value="1"/>
	<portlet:param name="paramValue" value="sendEvaluationTask"/>
	<portlet:param name="render_action" value="getEvaluationStatus"/>
	<portlet:param name="action" value="propEval"/>
</portlet:renderURL>


<form:form id="sendEvaluationForm" action="${sendEvaluationUrl}" method="post"
		commandName="AuthenticationBean" name="sendEvaluationform">
		<div class='tabularContainer'>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
		<h2>Send Evaluation Tasks</h2>
	<div class='hr'></div>
	<p>All of the Proposals for this competition pool have been marked as Accepted for Evaluation or Non-Responsive. 
		You can now send Proposals to Evaluators for scoring by entering your user name and password and clicking the Send Tasks button below.</p>
	<p>Please note that all Proposals marked Accepted for Evaluation will be sent to evaluators. 
	All Proposals marked Non-Responsive will not be sent to evaluators and will be disqualified from competing in this competition pool.</p>
	<p><b>You WILL NOT be able to undo the statuses once you click the Send Tasks button.</b>  
	If you wish to review or change Proposal statuses, click Cancel.</p>
	<div id="errorPlacementWrapper"> 
	<c:if test="${message ne null}">
		<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('errorPlacementWrapper', this)"/>
		</div>
	</c:if>
		</div>
	<div class="formcontainer" id="closeProcurement" onclick="checkboxEnable();">
		<p><input type="checkbox" id='chksendEvaluation1'/>
		<label for='chksendEvaluation1'>Yes, please send evaluation tasks to their assigned Evaluators.</label></p>
		
		<p><input type="checkbox" id='chksendEvaluation2' onclick="hideUnhideUsername(this);"/>
		<label for='chksendEvaluation2'>Yes, I understand that Non-Responsive Proposals will not be evaluated.</label></p>
    	
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" id="userName" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" id="password" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
		</div>
	</div>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" id="cancelSendEvaluation" value="Cancel" onclick="closeOverLay();" />
    	<input type="submit" class="" id="sendEvaluationTask" value="Send Tasks"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="sendEvaluationId" value="${sendEvaluationId}"/>
    <%-- Code updated for R4 Starts --%>
    <input type="hidden" name="evaluationPoolMappingId" id="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
    <input type="hidden" name="procurementTitle" value="${procurementTitle}"/>
 <%-- Code updated for R4 Ends --%>
 </form:form>
