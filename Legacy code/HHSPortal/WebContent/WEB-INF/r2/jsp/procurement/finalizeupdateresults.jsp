<%--jsp page for Finalize/Update Award Selections details --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/finalizeupdateresults.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:actionURL var="finalizeUpdateAwardSelectionUrl" escapeXml="false">
	<portlet:param name="submit_action" value="finalizeProcurement"/>
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="procurementId" value="${procurementId}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
<%-- Code updated for R4 Ends --%>
</portlet:actionURL>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
	<portlet:param name="competitionPoolId" value="${competitionPoolId}"/>
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationResultsandSelections"/>
	<portlet:param name="paramValue" value="finalizeUpdateResults"/>
	<portlet:param name="render_action" value="fetchEvaluationResults"/>
	<portlet:param name="action" value="propEval"/>
</portlet:renderURL>
<form:form id="finalizeUpdateAwardForm" action="${finalizeUpdateAwardSelectionUrl}" method="post"
		commandName="AuthenticationBean" name="finalizeUpdateAwardForm">
<%--form details for Finalize/Update Award Selections details screen --%>		
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
		<input type="hidden" value="${nextAction}" id="nextAction" name="nextAction" />
	<div class='tabularContainer'>
	<h2>Finalize/Update Award Selections</h2>
	<div class='hr'></div>
		<div>&nbsp;</div>
	<div class="formcontainer">
	<div class="row"><span class="label">Procurement Value($):</span> <span
		class="formfield" id="procurementValue">${lsProcurementValue}</span></div>
	<div class="row"><span class="label">Total Awarded Amount($):</span> <span
		class="formfield" id="awardAmt">${lsAwardAmount}</span></div>
		<div class="row"><span class="label">Number of Awards:</span> <span
		class="formfield">${numberOfAwards}</span></div>
	<div class="row"><span class="label">Number of Providers:</span> <span
		class="formfield">${numberOfProviders}</span></div>
	</div>
	<p>Are you sure you want to finalize or update Award Selections?</p>
	<div id="errorPlacementWrapper">
	<c:if test="${message ne null}">
				<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('errorPlacementWrapper', this)">
				</div>
			</c:if>
		</div>
	<div class="formcontainer" id="cancelAward">
		<p><input type="checkbox" id='chkCancelAward' <c:if test="${disableStatusFlag}">disabled="disabled"</c:if>  onclick="hideUnhideUsername(this);"/>
		
		<label for='chkCancelAward'>Yes, send my Award Selections to HHS Accelerator for review.</label></p>
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" cssClass="input" id="userName" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" cssClass="input" id="password" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
		</div>
	</div>
	<div class="buttonholder">
    	<input type="button" class="graybtutton" id="doNotCancelAward" value="Cancel" onclick="cancelOverLay();" />
    	<input type="submit" class="button" id="yesCancelAward" value="Finalize"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
 </form:form>
<%-- Overlay Popup Ends --%>
  
