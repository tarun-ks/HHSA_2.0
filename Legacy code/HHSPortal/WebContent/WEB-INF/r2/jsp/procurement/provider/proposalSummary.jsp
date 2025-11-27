<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" var="status"/>
<fmt:setBundle basename="com/nyc/hhs/properties/messages" var="messageprop"/>
<portlet:defineObjects/>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/proposalSummary.js"></script>
<nav:navigationSM screenName="ProposalSummary">
<portlet:renderURL var="fetchProviderEvaluationScores" id="fetchProviderEvaluationScores" escapeXml="false">
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="render_action" value="fetchProviderEvaluationScores"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="procurementId" value="${procurementId}" />
	<%-- Code updated for R4 Ends --%>
</portlet:renderURL>

<input type = 'hidden' value='${fetchProviderEvaluationScores}' id='fetchProviderEvaluationScoresResourceUrl'/>
<div class="" id="overlayDivId"></div>

<portlet:actionURL var="addProposal" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDetails" />
	<portlet:param name="action" value="propEval" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>

<portlet:renderURL var="editProposal" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDetails" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="procurementProposalDetails" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="uploadDocument" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDocuments" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="procurementProposalDocumentList" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="selectionDetailsURL" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="SelectionDetails" />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="midLevelFromRequest" value="SelectionDetailsScreen" />
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="action" value="selectionDetail" />
	<portlet:param name="render_action" value="viewSelectionDetails" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="submitProposal" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="SubmitProposal" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="renderProviderProposal" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="viewProposalDetails" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDocuments" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="procurementProposalDetails" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:renderURL var="viewProposalDocument" escapeXml="false">
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />						
	<portlet:param name="midLevelFromRequest" value="ProposalDocuments" />
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="render_action" value="procurementProposalDetails" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:renderURL>
<portlet:resourceURL var='launchProposalRetractOverlay'  id = 'launchProposalRetractOverlay' escapeXml='false'>
	<portlet:param name="action" value="propEval" />
	<portlet:param name="procurementId" value='${procurementId}' />
</portlet:resourceURL>
<portlet:resourceURL var='launchProposalCancelOverlay'  id = 'launchProposalCancelOverlay' escapeXml='false'>
	<portlet:param name="action" value="propEval" />
	<portlet:param name="procurementId" value='${procurementId}' />
</portlet:resourceURL>

<portlet:actionURL var="pagingURL" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="submit_action" value="pagingProposalSummary" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>


<div class="floatRht">
	<comSM:commonSolicitation topLevelStatus="true" procurementId="${procurementId}" providerId="${sessionScope.user_organization}"></comSM:commonSolicitation>
</div>
	<form:form name="proposalSummaryForm" id="proposalSummaryForm" action="${addProposal}" method ="post" commandName="ProposalDetailsBean">
	<form:hidden path="procurementId" value="${ProposalDetailsBean.procurementId}"/>
	<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<input type="hidden" name="procurementId" id="procurementId" value="${procurementId}"/>
	<input type="hidden" name="editProposal" id="editProposal" value="${editProposal}"/>
	<input type="hidden" name="selectionDetailsURL" id="selectionDetailsURL" value="${selectionDetailsURL}"/>
	<input type="hidden" name="uploadDocument" id="uploadDocument" value="${uploadDocument}"/>
	<input type="hidden" name="submitProposal" id="submitProposal" value="${submitProposal}"/>
	<input type="hidden" name="proposalId1" id="proposalId1" value=""/>
	<input type="hidden" name="viewProposalDetails" id="viewProposalDetails" value="${viewProposalDetails}"/>
	<input type="hidden" name="viewProposalDocument" id="viewProposalDocument" value="${viewProposalDocument}"/>
	<input type='hidden' value='${launchProposalRetractOverlay}' id='hiddenRetractProposal' />
	<input type='hidden' value='${launchProposalCancelOverlay}' id='hiddenCancelProposal' />
	<input type="hidden" name="nextPage" value="" id="nextPage"/>
	<input type="hidden" name="pagingURL" id="pagingURL" value="${pagingURL}"/>
	
		<div id='tabs-container' class='clear clearHeight'>
		<h2>Proposal Summary</h2>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
				<d:content section="${helpIconProvider}">
					<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
					<input type="hidden" id="screenName" value="Proposal Summary" name="screenName"/>
				</d:content>	
			<div class='hr'></div>
	<%-- Container Starts --%>
				<p>
					Use this section to manage Proposals to this RFP. For each Proposal you submit, your score and rank will be shared with your 
					organization leadership after the Contract Start Date is set and the Procurement status is closed. 
					If you are a user with Level 2 permissions, you will have access to this information.
				</p>
				
				
				
				
				<%--Filter and Reassign section starts --%>
				<fmt:message key="PROVIDER_ELIGIBLE_TO_PROPOSE" bundle="${status}" var="PROVIDER_ELIGIBLE_TO_PROPOSE"/>
				<%--If provider status is eligible to propose display message  --%>
				<c:if test="${providerStatusId eq PROVIDER_ELIGIBLE_TO_PROPOSE}">
					<div class="passedDisplay">
						You’re eligible to submit a proposal to this RFP. Use the section below to manage your Proposal(s).
					</div>
				</c:if>
				<c:if test="${message ne null}">
					<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" onclick="showMe('messagediv', this)"/></div>
				</c:if>
				<%-- Start Release 5 user notification --%>
				<c:set var="sectionProvider"><%=HHSComponentMappingConstant.S234_PROVIDER_SECTION%></c:set>
				<c:if test="${procurementRoadMapReadonlyFlag ne true}">
				<d:content section="${sectionProvider}">
				<div class="taskButtons widthFull" >
					<fmt:message key="PROCUREMENT_RELEASED" bundle="${status}" var="PROCUREMENT_RELEASED"/>
					<%--if procurement status is released then display button to add new proposal  --%>
					<c:if test="${(procurementStatus eq PROCUREMENT_RELEASED) and (restrictSubmit ne true)}">
						<input type="button" value="Add New Proposal"  id='addNewProposal' class='add floatLft' />
					</c:if><%-- Code updated for R4 Starts --%>
					<c:if test="${procurementBean.isOpenEndedRFP ne '1'}">
						<span class='floatRht'>
							<b>Proposal Due Date</b> : <fmt:formatDate pattern='MM/dd/yyyy' value='${proposalDueDate}'/> ${releaseTime}
						</span>
					</c:if>
				<%-- Code updated for R4 Ends --%></div>
				</d:content>
				</c:if>
				<%-- End Release 5 user notification --%>
<br></br>
			
				<%-- Grid Starts --%>
			    <div  class="tabularWrapper" style='min-height:700px !important'>
			        <st:table objectName="proposalDetails"  cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows" displayTitle="no" pageSize='${allowedObjectCount}'>
						<st:property headingName="" columnName="latestVersionQues" align="center" size="2%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalTitleExtension" />
						</st:property>
						<st:property headingName="Proposal Title" columnName="proposalTitle" align="center" sortType="proposalTitle" sortValue="asc"
							size="22%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalTitleExtension" />
						</st:property>
						<st:property headingName="Competition Pool" columnName="competitionPoolTitle" align="center" sortType="competitionPoolTitle" sortValue="asc"
							size="22%"/>
						<st:property headingName="Status" columnName="proposalStatus" sortType="proposalStatus" sortValue="asc"
							align="left" size="14%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalTitleExtension" />
						</st:property>
						<st:property headingName="Last Modified" columnName="modifiedDate"
							sortType="modifiedDate" sortValue="desc" align="right" size="15%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalSummaryDateExtension" />
						</st:property>
						<st:property headingName="Last Modified By" columnName="modifiedBy" sortType="modifiedBy" sortValue="asc"
							 align="right" size="16%"/>
						<st:property headingName="Actions" columnName="action" align="right" size="10%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProposalTitleExtension" />
						</st:property>
					</st:table>
					<%--if no proposal is added then display message  --%>
					<c:if test="${proposalDetails eq null or (fn:length(proposalDetails)) eq 0}">
						<div class="messagedivNycMsg noRecord" id="messagedivNycMsg" >No Proposals have been added.</div>
					</c:if>
					<%--if procurement status is closed then display total no of proposals  --%>
				<fmt:message key="PROCUREMENT_CLOSED" bundle="${status}" var="PROCUREMENT_CLOSED"/>
				<c:set var="sectionProvider"><%=HHSComponentMappingConstant.S234_PROVIDER_SECTION%></c:set>
				</div>
				
			 </div>
		</form:form>	
</nav:navigationSM>

<%-- Overlay Starts --%>
<div class="overlay"></div>
<div class="alert-box alert-box-retractProposal">
	<div class='tabularCustomHead'>Retract Proposal</div>
	<a href="javascript:void(0);" class="exit-panel retract-Proposal">&nbsp;</a>
	<div id="requestRetractProposal"></div>
</div>
<div class="alert-box alert-box-cancelProposal">
	<div class='tabularCustomHead'>Cancel Proposal</div>
	<a href="javascript:void(0);" class="exit-panel cancel-Proposal">&nbsp;</a>
	<div id="requestCancelProposal"></div>
</div>