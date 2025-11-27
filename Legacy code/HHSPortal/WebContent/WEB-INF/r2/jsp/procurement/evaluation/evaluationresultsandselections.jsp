<%--This screen displays a list of proposals and their evaluation scores. A proposal is 
only displayed on this screen when its proposal status is updated to "Evaluated" --%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" />
<%-- Navigation Tag Starts --%>
<nav:navigationSM screenName="EvaluationResultsandSelections">
	<portlet:defineObjects />
	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationresults.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
	<%--defining action url to be used while submitting the form --%>
	<portlet:actionURL var="evaluationResultsUrl" escapeXml="false">
		<portlet:param name="action" value="propEval" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<%-- defining resourceURL url to be used while making ajax call --%>
	<portlet:resourceURL var="getProviderNameList" id="getProviderNameList" escapeXml="false">
		<portlet:param name="providerName" value="getProviderNameList" />
	</portlet:resourceURL>
	<input type='hidden' value='${getProviderNameList}' id='getProviderNameList' />
	<%-- defining renderURL to be used to render view Response page from the current page --%>
	<portlet:renderURL var='viewResponse' id='viewResponse' escapeXml='false'>
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="viewResponse" />
		<portlet:param name="procurementId" value='${procurementId}' />
		<portlet:param name="proposalId" value='${proposalId}' />
		<portlet:param name="showBafoButton" value="true"/>
	</portlet:renderURL>
	<%-- finalize update results --%>
	<portlet:resourceURL var='finalizeOrUpdateResults' id='finalizeOrUpdateResults' escapeXml='false'>
		<portlet:param name="action" value="propEval" />
	</portlet:resourceURL>
	<input type='hidden' value='${finalizeOrUpdateResults}' id='hiddenFinalizeOrUpdateResultsOverlayContentUrl' />
	<%-- finalize update results Ends--%>
	
	<input type='hidden' value='${viewResponse}' id='hiddenViewResponse' />
	<%-- defining renderURL to be used to render Evaluation Summary page from the current page --%>
	<portlet:renderURL var='viewEvaluationSummary'  escapeXml='false'>
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="displayEvaluationSummary" />
		<portlet:param name="procurementId" value='${procurementId}' />
		<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
		<portlet:param name="evaluationStatusId" value='${evaluationStatusId}' />
	</portlet:renderURL>
	<input type='hidden' value='${viewEvaluationSummary}' id='hiddenViewEvaluationSummary' />
	<%-- defining renderURL to be used to render Mark Selected overlay from the current page --%>
	<portlet:resourceURL var='markSelected'  id = 'markSelected' escapeXml='false'>
		<portlet:param name="proposalId" value='${proposalId}' />
		<portlet:param name="procurementId" value='${procurementId}' />
		<%-- Code updated for R4 Starts --%>
		<portlet:param name="evaluationGroupId" value='${evaluationGroupId}' />
		<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
		<portlet:param name="competitionPoolId" value='${competitionPoolId}' />
	<%-- Code updated for R4 Ends --%>
	</portlet:resourceURL>
	<input type='hidden' value='${markSelected}' id='hiddenMarkSelected' />
	<%-- defining renderURL to be used to render Mark NotSelected overlay from the current page --%>
	<portlet:resourceURL var='markNotSelected'  id = 'markNotSelected' escapeXml='false'>
		<portlet:param name="proposalId" value='${proposalId}' />
		<portlet:param name="procurementId" value='${procurementId}' />
		<%-- Code updated for R4 Starts --%>
		<portlet:param name="evaluationGroupId" value='${evaluationGroupId}' />
		<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
		<portlet:param name="competitionPoolId" value='${competitionPoolId}' />
	<%-- Code updated for R4 Ends --%>
	</portlet:resourceURL>
	<input type='hidden' value='${markNotSelected}' id='hiddenMarkNotSelected' />
	<%-- defining renderURL to be used to render View Selection Comments overlay from the current page --%>
	<portlet:resourceURL var='viewSelectionComments' id="viewSelectionComments"  escapeXml='false'>
		<portlet:param name="proposalId" value='${proposalId}' />
	</portlet:resourceURL>
	<input type='hidden' value='${viewSelectionComments}' id='hiddenViewComments' />
	<%-- defining renderURL to be used to render View Award Review Comments overlay from the current page --%>
	<portlet:resourceURL var='viewAwardReviewComments' id = 'viewAwardReviewComments' escapeXml='false'>
		<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
	</portlet:resourceURL>
	<input type='hidden' value='${viewAwardReviewComments}' id='hiddenViewAwardComments' />
	<%-- defining renderURL to be used to render Request Score Amendment overlay from the current page --%>
	<portlet:actionURL var="sendRequestAmendment" id = 'sendRequestAmendment' escapeXml="false">
		<portlet:param name="action" value="propEval" />
		<portlet:param name="submit_action" value="requestScoreAmendment" />
		<portlet:param name="procurementId" value='${procurementId}' />
		<%-- Code updated for R4 Starts --%>
		<portlet:param name="evaluationGroupId" value='${evaluationGroupId}' />
		<portlet:param name="evaluationPoolMappingId" value='${evaluationPoolMappingId}' />
		<portlet:param name="competitionPoolId" value='${competitionPoolId}' />
		<%-- Code updated for R4 Ends --%>
		<portlet:param name="proposalId" value='${proposalId}' />
	</portlet:actionURL>
	
	<input type="hidden" value="${sendRequestAmendment}" id="sendRequestAmendment" name="sendRequestAmendment" />
	<input type='hidden' value='${markNotSelected}' id='hiddenMarkNotSelected' />
	<input type="hidden" value="${evaluationStatusId}" id="evaluationStatusId" name="evaluationStatusId" />
	<input type="hidden" value="${awardId}" id="awardId" name="awardId" />
	<input type="hidden" value="" id="nextAction" name="nextAction" />
	<input type = 'hidden' value='' id='proposalTitle1' name='proposalTitle' />
	<input type = 'hidden' value='' id='organizationName1' name='organizationName' />
	<input id="screenLockedFlag" type="hidden" value="${accessScreenEnable}"/>
	<%-- Form Tag Starts --%>
	<form:form id="evalResultform" action="${evaluationResultsUrl}" method="post" commandName="EvaluationFilterBean" name="evalResultform">
		<input type="hidden" name="procurementId" value="${procurementId}" />
		<%-- Code updated for R4 Starts --%>
		<input type = "hidden" value="${evaluationGroupId}" name="evaluationGroupId"/>
		<input type = "hidden" value="${evaluationPoolMappingId}" name="evaluationPoolMappingId" id="evaluationPoolMappingId"/>
		<input type = "hidden" value="${competitionPoolId}" name="competitionPoolId"/>
		<%-- Code updated for R4 Ends --%>
		<input type="hidden" value="${filtered}" id="filtered" />
		<input type="hidden" value="${proposalFilteredResult}" id="proposalFilteredResult" />
		<input type ="hidden"  id="awardAmountFromHidden" name="awardAmountFromHidden" />
		<input type ="hidden"  id="awardAmountToHidden" name="awardAmountToHidden" />
		<input type ="hidden"  id="scoreFromHidden" name="scoreFromHidden" />
		<input type ="hidden"  id="scoreToHidden" name="scoreToHidden" />
		<input type ="hidden"  id="userRole" value="${user_role}" />
		<input type = 'hidden' value='${hideExitProcurement}' name="hideExitProcurement"/>
		<div id='tabs-container'>
			<h2>Evaluation Results and Selections
				<a id="returnEvaluationSummary" class="floatRht returnButton" href="#">Proposals and Evaluations Summary</a>
			</h2>
		
		<div class="floatRht">
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
			<d:content section="${helpIconProvider}">
				<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
				<input type="hidden" id="screenName" value="Evaluation Results and Selections" name="screenName"/>
			</d:content>
			
			<%-- Award Review Comments are displayed on click of "Show Comments" hyper link if the value of showCommentVisibiltyStatus is true--%>
			<c:set var="sectionShowComments"><%=HHSComponentMappingConstant.S218_SHOW_COMMENTS%></c:set>
			<d:content section="${sectionShowComments}">
				<a href="javascript:void(0);" onclick="pageSpecificHelp('Procurement');"></a>
			</d:content>
		</div>
			<div class='hr'></div>
		<div class="floatRht"><%-- Code updated for R4 Starts --%>
			<c:if test="${awardReviewStatus.awardReviewStatus ne null}">
				<c:choose>
					<c:when test="${awardReviewStatus.awardReviewStatus ne 'Returned'}">
						<b>Award Review Status:</b>
						${awardReviewStatus.awardReviewStatus}
					</c:when>
					<c:otherwise>
						<b>Award Review Status:</b>
						<label href="#" class="iconComments" name="overlayLink" id="overlayLink"  onclick="showComments();">${awardReviewStatus.awardReviewStatus}</label>&nbsp;&nbsp;
					</c:otherwise>
				</c:choose>
			</c:if><%-- Code updated for R4 Ends --%>
		</div>
		<c:if test="${selectedproposalMsgFlag ne null and selectedproposalMsgFlag}">
			<c:set var="sectionSelectedProposalMsg"><%=HHSComponentMappingConstant.S218_SELECTED_PROPOSAL_MSG%></c:set>
			<d:content section="${sectionSelectedProposalMsg}">
				<div class='infoMessage' id="messagediv" style="display:block">${selectedproposalMsgKey}</div>
			</d:content>
		</c:if>
		<c:if test="${message ne null}">
			<div class="${messageType}" id="messagediv" style="display: block">${message}
				<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close"  onclick="showMe('messagediv', this)">
			</div>
		</c:if>
		<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
		</c:if>
		<p>Configure award selections.</p>
		<%-- Code updated for R4 Starts --%>
		<div class="formcontainer">
			<c:if test="${procurementBean.isOpenEndedRFP eq '1'}">
				<div class="row">
					<span class="label">Evaluation Group:</span>
					<span class="formfield">${groupTitleMap['EVALUATION_GROUP_TITLE']}</span>
				</div>
				<div class="row">
					<span class="label">Closing Date:</span>
					<span class="formfield">${groupTitleMap['SUBMISSION_CLOSE_DATE']}</span>
				</div>
			</c:if>
			<div class="row">
				<span class="label">Competition Pool:</span>
				<span class="formfield">${groupTitleMap['COMPETITION_POOL_TITLE']}</span>
			</div>
			<!-- R5 changes starts -->
			<c:if test="${EvaluationFilterBean.awardReviewStatus eq 'Approved'}">
			<div class="row">
				<span class="label">With Financials?:</span>
				<span class="formfield"> <c:choose>
								<c:when test="${contractTypeId eq 1}">Yes </c:when>
								<c:otherwise>No</c:otherwise>
							</c:choose> </span>
			</div>
			<div class="row">
				<span class="label">Is Award Amount Negotiable?:</span>
				<span class="formfield"><c:choose>
								<c:when test="${evaluationResultList[0].isNegotiationFlag eq 'no'}">No </c:when>
								<c:otherwise>Yes</c:otherwise>
							</c:choose></span>
			</div>
			</c:if>
			<!-- R5 changes ends -->
		</div>
		<%-- Code updated for R4 Ends --%>
				<%--Filter and Reassign section starts --%>
				<div class="taskButtons">
					<span class='floatLft'>
						<input type="button" value="Filter Items" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');" /> &nbsp;
					</span>
					
					<span class='floatLft'>
						<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
							<c:if test="${(!isProcClosedOrCancelled)  && showFinalizeButton}">
								<input type="button" value="Finalize Results" class="btnSend" onclick="finalizeOrUpdateResults(this, 'finalizeProcurement');"
									<c:if test="${not finalizeButtonActive}">disabled="disabled"</c:if> />
							</c:if>
						<c:if test="${user_role ne 'CFO'}">
							<c:set var="sectionUpdateResults"><%=HHSComponentMappingConstant.S218_UPDATE_RESULTS_BUTTON%></c:set>
							<d:content section="${sectionUpdateResults}">
								<c:choose>
								<c:when test="${((updateVisibiltyStatus eq 'enable') ||(updateVisibiltyStatus eq 'disable')) and !isProcClosedOrCancelled }">
									<input type="button" value="Update Results" class="btnSend"  onclick="finalizeOrUpdateResults(this, 'updateFinalizeProcurement');"
										<c:if test="${updateVisibiltyStatus eq 'disable'}">disabled="disabled"</c:if> />
								</c:when>
								<c:otherwise>
								<c:if test="${((updateafterApprovalStatus eq 'enable') ||(updateafterApprovalStatus eq 'disable')) and !isProcClosedOrCancelled }">
									<input type="button" value="Update Results" class="btnSend"  onclick="finalizeOrUpdateResults(this, 'updateAfterAwardApproval');"
									<c:if test="${updateafterApprovalStatus eq 'disable'}">disabled="disabled"</c:if> />
								</c:if>	
								</c:otherwise> 
								</c:choose>
							</d:content>
							</c:if>
						</d:content>
					</span>
							
					<%-- Popup for Filter Task Starts --%>
					<div id="documentValuePop" class='formcontainerFinance' style='width: 460px;'>
						<c:set var="proposalEvaluated">
							<fmt:message key='PROPOSAL_EVALUATED' />
						</c:set>
						<c:set var="scoresReturned">
							<fmt:message key='PROPOSAL_SCORES_RETURNED' />
						</c:set>
						<c:set var="proposaslSelected">
							<fmt:message key='PROPOSAL_SELECTED' />
						</c:set> <c:set var="notSelected">
							<fmt:message key='PROPOSAL_NOT_SELECTED' />
						</c:set>
						<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a>
						</div>
						<div class='row'>
							<span class='label'>Proposal Title:</span>
							<span class='formfield'> <form:input path="proposalTitle" onchange="enableDisableDefaultFilter()" cssClass="widthFull" id="proposalTitle" maxlength="60" title="Enter at least 5 characters of the Proposal Title." /></span>
							<span class="error"></span>
						</div>
						<div class='row'>
							<span class='label'>Provider Name:</span>
							<span class='formfield'> <form:input path="organizationName" onchange="enableDisableDefaultFilter()" cssClass="widthFull" id="organizationName" maxlength="100" title= "Enter at least 3 characters of the Provider Name and select from the suggestions listed."/>
							</span> <span class="error"></span>
						</div>
						<div class='row'><span class='label'>Score Range From:</span>
							<span class='formfield'><span>
							<form:input cssClass="datepicker" path="scoreRangeFrom" onchange="enableDisableDefaultFilter()" id="scoreRangeFrom" maxlength="3" validate="number" title="Enter values between 0 and 100"/></span>
							<span>To <form:input cssClass="datepicker" path="scoreRangeTo" onchange="enableDisableDefaultFilter()"  id="scoreRangeTo" maxlength="3" validate="number" title="Enter values between 0 and 100"/></span>
							</span> <span class="error"></span>
						</div>
						<div class='row'>
							<span class='label'>Proposal Status:</span>
							<span class='formfield' id='firstLevelCheckBox'>
								<span class='leftColumn'>
									<span><form:checkbox path="proposalStatusList" onchange="enableDisableDefaultFilter()" value="${proposalEvaluated}" id='chkNonStarted' />
										<label for='chkNonStarted'>Evaluated</label>
									</span>
									<span>
										<form:checkbox path="proposalStatusList" onchange="enableDisableDefaultFilter()" value="${scoresReturned}" id='chkInProgress' />
										<label for='chkInProgress'>Scores Returned</label>
									</span>
								</span>
								<span class='rightColumn'> <span><form:checkbox path="proposalStatusList" onchange="enableDisableDefaultFilter()" value="${proposaslSelected}" id='chkSelected' />
									<label for='chkSelected'>Selected</label>
									</span>
									<span>
										<form:checkbox path="proposalStatusList" onchange="enableDisableDefaultFilter()" value="${notSelected}" id='chknotSelected' />
										<label for='chknotSelected'>Not Selected</label>
									</span>
								</span>
							</span>
						</div>
						<div class='row'>
							<span class='label'>Award Amount ($) From:</span>
							<span class='formfield'>
								<span>
									<form:input cssClass="datepicker" path="awardAmountFrom" onchange="enableDisableDefaultFilter()" id="awardAmountFrom" />
								</span>
								<span>To
									<form:input cssClass="datepicker" path="awardAmountTo" onchange="enableDisableDefaultFilter()" id="awardAmountTo" />
								</span>
							</span>
							<span class="error"></span>
						</div>
						<div class="buttonholder">
							<input type="button" id="clearfilter" value="Set to Default Filters" onclick="clearEvaluationFilter()" class="graybtutton" />
							<input type="button" value="Filter"  id="filter" onclick="displayFilter()" />
						</div>
			</div>

			</div>
<%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>			
	<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<%-- Grid Starts --%>
				<div class="tabularWrapper evaluationResultDiv clear" style='height: 500px !important'>
					<st:table objectName="evaluationResultList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows" pageSize="${allowedObjectCount}">
						<st:property headingName="Proposal Id" columnName="proposalId" size="20%" align="center" sortType="proposalId" sortValue="asc">
						</st:property>
						<st:property headingName="Proposal Title" columnName="proposalTitle" align="center" sortType="proposalTitle" sortValue="asc" size="15%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
						</st:property>
						<st:property headingName="Provider Name" columnName="organizationName" sortType="organizationName" sortValue="asc" align="right" size="15%" />
						<st:property headingName="Evaluation Score" columnName="evaluationScore" sortType="evaluationScore" sortValue="asc" align="left" size="10%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
						</st:property>
						<st:property headingName="Proposal Status" columnName="proposalStatus" sortType="proposalStatus" sortValue="asc" align="left" size="16%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
						</st:property>
						<st:property headingName="Award Amount($)" columnName="awardAmount" sortType="awardAmount" sortValue="asc"  align="right" size="12%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
						</st:property>
						<st:property headingName="Actions" columnName="actions" align="right" size="12%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationResultsExtension" />
						</st:property>
					</st:table>
<%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>					
					<c:if test="${fn:length(evaluationResultList) eq 0}">
						<p class='noRecord'>No Records Found</p>
					</c:if>
					
					<div class="floatLft"><span>Proposals: <label>${records}</label></span></div>
					<%-- Start : Changes in R5 --%>
					<div id="pendingAwardTipDiv"><p> <br></span><span class="red-ex-mark"/>= Final award amount pending</p></div>
					<%-- End : Changes in R5 --%>
					<div class='clear'>&nbsp;</div>
					<c:if test="${star}">
						<div><label class='required'>*</label>Indicates a Proposal that has changed since previous HHS Accelerator approval on
							<fmt:formatDate pattern='MM/dd/yyyy' value='${awardReviewStatus.awardApprovalDate}'/>
						</div>
					</c:if>
					<c:if test="${dStar}">
						<div>
							<label class='required'>**</label>Indicates a proposal that is currently assoicated to a provider with an active award/contract
						</div>
					</c:if>
					</div>
		</d:content>		
			<%-- Grid Ends --%></div>
	</form:form>
	<%-- Form Data Ends --%>
</nav:navigationSM>
<%-- Navigation Tag Starts --%>


<%-- Overlay Starts --%>
<div class="overlay"></div>
<%-- Overlay Popup for screen S223 Starts --%>
<div class="alert-box alert-box-markSelected">
	<div class='tabularCustomHead'>Confirm Selected Status</div>
	<a href="javascript:void(0);" class="exit-panel mark-Selected">&nbsp;</a>
	<div id="requestMarkSelected"></div>
</div>
<%-- Overlay Popup for screen S223 Ends --%>

<%-- Overlay Popup for screen S224 Starts --%>
<div class="alert-box alert-box-markNotSelected">
	<div class='tabularCustomHead'>Confirm Not Selected Status</div>
	<a href="javascript:void(0);" class="exit-panel mark-NotSelected">&nbsp;</a>
	<div id="requestMarkNotSelected"></div>
</div>
<%-- Overlay Popup for screen S224 Ends --%>

<%-- Overlay Popup for screen S225 Starts --%>
<div class="alert-box alert-box-viewComments">
	<div class='tabularCustomHead'>View Comments</div>
	<a href="javascript:void(0);" class="exit-panel view-Comments">&nbsp;</a>
	<div id="requestViewComments"></div>
</div>
<%-- Overlay Popup for screen S225 Ends --%>

<%-- Overlay Popup for screen S259 Starts --%>
<div class="alert-box alert-box-viewAwardComments">
	<div class='tabularCustomHead'>Comments</div>
	<a href="javascript:void(0);" class="exit-panel request-AwardComments">&nbsp;</a>
	<div id="requestAwardComments"></div>
</div>
<%-- Overlay Popup for screen S259 Ends --%>

<%-- Overlay Popup for screen S262 Starts --%>
<div class="alert-box alert-box-requestScoreAmendment">
	<div class="content">
		<div class="tabularCustomHead">Request Score Amendment</div>
		<div class='tabularContainer'>
			<h2>Request Score Amendment</h2>
			<div class='hr'></div>
			<div id="requestScoreAmendment">
			<p>Are you sure you want to request a Score Amendment to this proposal?</p>
			<p>Requesting a Score Amendment will create a task for the Agency
			ACCO Manager to indicate which evaluators require a score amendment.</p>
			<div class="buttonholder">
				<input type="button" class="graybtutton" id="cancelRequestScoreAmendment" value="Cancel" onclick="cancelRequestAmendmentOverlay();" />
				<input type="submit" id="yesRequestScoreAmendment" value="Yes, request a score amendment" onclick="sendAmendmentRequest();" />
			</div>
			</div>
		</div>
	</div>
	<a href="javascript:void(0)" class="exit-panel request-ScoreAmendment" >&nbsp;</a>
</div>
<%-- Overlay Popup for screen S262 Ends --%>

<%-- Overlay Popup for Finalize/Update Starts --%>
<div class="alert-box alert-box-finalizeOrUpdateResults">
	<div class='tabularCustomHead'>Finalize/Update Award Selections</div>
	<a href="javascript:void(0);" class="exit-panel cancel-finalizeOrUpdateResults">&nbsp;</a>
	<div id="finalizeOrUpdateResults"></div>
</div>
<%-- Overlay Popup for Finalize/Update Ends --%>
<div id="overlayedJSPContent" style="display: none"></div>

