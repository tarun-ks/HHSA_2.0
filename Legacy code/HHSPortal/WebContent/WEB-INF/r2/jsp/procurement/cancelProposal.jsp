<%-- This jsp implementsthe functionality of confirming a provider status as Selected corresponding to a procurement--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/retractCancelProposal.js"></script>
<portlet:defineObjects />

<%-- Portlet Action Url Starts --%>
<portlet:actionURL var="cancelProposal" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="submit_action" value="cancelProposal" />
	<portlet:param name="proposalId" value='${proposalId}' />
	<portlet:param name="procurementId" value='${procurementId}' />
</portlet:actionURL>
<%-- Portlet Action Url Starts Ends --%>

<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%-- Hidden variables Starts --%>
<input type = "hidden" value="${proposalId}" id="proposalId" name="proposalId"/>
<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
<%-- Hidden variables Ends --%>

<%-- Overlay Popup Starts --%>
<form:form id="cancelProposalForm" action="${cancelProposal}" method ="post" commandName="EvaluationBean" name="cancelProposalForm">
	<div class='tabularContainer'>
		<h2>Cancel Proposal</h2>
		<div class='hr'></div>
		<div id="cancelProposal">
			<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement}">
			<c:if test="${accessScreenEnable eq false}">
					<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
					<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
			</c:if>
				<p>Are you sure you want to cancel this proposal?</p>	
				<p>Canceling this proposal will erase any data your organization entered. If any documents are attached to this proposal,they will remain
					in your Document Vault.
				</p>				
				<div class="buttonholder">
			    	<input type="button" class="graybtutton"  id="doNotCancelProposal" value="No, do NOT Cancel this Proposal" onclick="javascript:cancelOverlay();" />
			    	<input type="submit" class="redbtutton"  id="yesCancelProposal" value="Yes, Cancel this Proposal"/>
			    </div>
		    </d:content>
		</div>
	</div>		
</form:form>
<%-- Overlay Popup Ends --%>
