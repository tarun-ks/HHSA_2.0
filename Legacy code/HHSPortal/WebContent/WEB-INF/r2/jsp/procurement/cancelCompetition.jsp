<%-- This JSP was added for R4--%>
<%-- JSP for overlay pop up for close group  --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelCompetition.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="cancelCompetitionUrl" escapeXml="false">
	<portlet:param name="submit_action" value="cancelCompetitionAction"/>
	<portlet:param name="action" value="propEval" />
</portlet:actionURL>
<!--<portlet:resourceURL var="cancelCompetitionAction" id="cancelCompetitionAction" escapeXml="false">
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>

--><portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationSummary"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="action" value="propEval"/>
</portlet:renderURL>
<form:form id="cancelCompetitionForm" action="${cancelCompetitionUrl}" method="post"
		commandName="AuthenticationBean" name="cancelCompetitionForm">
		<div class='tabularContainer'>
		<h2>Cancel Competition</h2>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
	<div class='hr'></div>
	
	<p>Please enter the reason for cancelling the competition in the comments section below and then click the Yes, Cancel this Competition button to continue or click the No, do Not Cancel this Competition button to go back to the Proposals and Evaluations Summary.</p>

<p><label class="required">*</label>Indicates a required field.</p>
	
	<div id="errorPlacementWrapper"> 
		<c:if test="${message ne null}">
			<div class="${messageType}" id="errorPlacement" style="display:block">${message} <img
				src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" title="Close" alt="Close"
				onclick="showMe('errorPlacementWrapper', this)"/>
			</div>
		</c:if>
	</div>
	<c:if test="${(cancelStatusFlag ne null) and (cancelStatusFlag ne true)}">
	<div class="formcontainer" id="cancelStatusFlag">
	<input type="hidden" value="${cancelStatusFlag}" id="cancelStatusFlag"/>
	</div>
	</c:if>
	<div class="formcontainer" id="cancelCompetition">
		<p><input type="checkbox" id='chkcancelCompetition' onclick="hideUnhideUsername(this);"/>
		 
		<label for='chkcancelCompetition'>I agree to cancel this competition</label><label class="required">*</label></p>
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row">
			<span class=" "></span>
				<span class="">
				<textarea name="comments" cols="" rows="" class='input' id='comments' style="width:575px;" onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)">${proposalDetails.comments}</textarea><label class="required">*</label>
				<span class="error"></span></span>
			</div>
	
			<div class="row" id="usernameDiv">
				<span class="label"><label
		class="required">*</label><label for='txtUsername'>User Name:</label></span>
				<span class="formfield">
					<form:input path="userName" id="userName" autocomplete="off"/>
				</span>
				<span class="error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label"><label
		class="required">*</label><label for='txtPassword'>Password:</label></span>
				<span class="formfield">
					<form:password path="password" id="password" autocomplete="off"/>
				</span>
				<span class="error">
				</span>
			</div>
		</div>
	</div>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" title="No, do NOT cancel this competition" id="doNotCancelCompetition" value="No, do NOT cancel this competition" onclick="closeOverLay();" />
    	<input type="submit" class="" title="Yes, cancel this competition" id="yesCancelCompetition" value="Yes, cancel this competition"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/>
    <input type="hidden" name="evaluationGroupId" id="evaluationGroupId" value="${evaluationGroupId}"/>
    <input type="hidden" name="competitionPoolId" id="competitionPoolId" value="${competitionPoolId}"/>
    <input type="hidden" name="evaluationPoolMappingId" id="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
 </form:form>
