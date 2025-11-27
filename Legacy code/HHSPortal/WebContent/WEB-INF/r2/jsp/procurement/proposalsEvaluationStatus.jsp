<%-- This jsp renders the evaluation status data, that contains all the 
proposal that are in Submitted or in later status --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page import="javax.portlet.PortletContext"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<%
HttpServletResponse httpResponse = (HttpServletResponse)response;
httpResponse.setHeader("Cache-Control","no-cache, no-store, must-revalidate"); 
response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
response.addHeader("Cache-Control", "post-check=0, pre-check=0");
httpResponse.setHeader("Pragma","no-cache"); 
httpResponse.setDateHeader ("Expires", 0); 
%>

<!-- Starts R5 : Enhanced Evaluation -->
<portlet:resourceURL var='viewProgressOverlay' id='viewProgressOverlay' escapeXml='false'>
	<portlet:param name="action" value="propEval"/>
</portlet:resourceURL>
<input type = 'hidden' value='${viewProgressOverlay}' id='hiddenViewProgressUrl'/>
<!-- Ends R5 : Enhanced Evaluation -->

<%-- 258 screen - Mark Non Responsive  --%>
<portlet:actionURL var="markNonResponsive" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="submit_action" value="markProposalNonResponsive" />
	<portlet:param name="procurementId" value='${procurementId}' />
	<portlet:param name="proposalId" value='${proposalId}' />
</portlet:actionURL>
<%-- end 258 screen - Mark Non Responsive--%>
 
 	
 <%-- 276 - Mark Returned for Revision--%>
<portlet:actionURL var="markReturnedForRevision" escapeXml="false">
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="submit_action" value="confirmReturnForAction"/>
	<portlet:param name="procurementId" value='${procurementId}' />
	<portlet:param name="proposalId" value='${proposalId}' />
</portlet:actionURL>
<%--276 - Mark Returned for Revision Ends--%>

<%-- Cancel Evaluation Tasks Starts --%>
<portlet:resourceURL var='cancelEvaluationTasks' id='cancelEvaluationTasks' escapeXml='false'>
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:resourceURL>
<%-- Cancel Evaluation Tasks Ends --%>
<%-- Code updated for R4 Starts --%>
<%-- Unlock Proposal Task Starts --%>
<%-- competitionPoolTitle removed from action URL --%>
<%-- ProcurementTitle removed from URL as a part of release 3.7.0 defect 6530 --%>
<portlet:actionURL var='unlockProposal' escapeXml='false'>
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="submit_action" value="unlockProposal"/>
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="midLevelFromRequest" value="EvaluationStatus"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="procurementId" value='${procurementId}'/>
<%--<portlet:param name="competitionPoolTitle" value="${groupTitleMap['COMPETITION_POOL_TITLE']}"/>--%>
</portlet:actionURL>
<%-- Unlock Proposal Task Ends --%>
<%-- Code updated for R4 Ends --%>
<%-- Get DB Docs URL Starts --%>
<portlet:resourceURL var='getDBDDocs' id='getDBDDocs' escapeXml='false'>
	<portlet:param name="procurementId" value="${procurementId}" />
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>
<%-- Get DB Docs URL Ends --%>

<%-- Get Provider Name List URL Starts --%>
<portlet:resourceURL var="getProviderNameList" id="getProviderNameList" escapeXml="false">
	<portlet:param name="providerName" value="getProviderNameList"/>
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderNameList}' id='getProviderNameList'/>
<%-- Get Provider Name List URL Ends --%>

<%-- View Response URL Starts --%>
<portlet:renderURL var='viewResponse' id='viewResponse' escapeXml='false'>
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="render_action" value="viewResponse"/>
	<portlet:param name="procurementId" value='${procurementId}'/>
	<portlet:param name="showBafoButton" value="true"/>
</portlet:renderURL>
<input type = 'hidden' value='${viewResponse}' id='hiddenViewResponse'/>
<%-- View Response URL Ends --%>

<%-- View Evaluation Summary URL Starts --%>
<portlet:renderURL var='viewEvaluationSummary' id='viewEvaluationSummary' escapeXml='false'>
	<portlet:param name="action" value="propEval"/>
	<portlet:param name="render_action" value="displayEvaluationSummary"/>
	<portlet:param name="procurementId" value='${procurementId}'/>
	<portlet:param name="proposalId" value='${proposalId}'/>
</portlet:renderURL>
<%-- View Evaluation Summary URL Ends --%>
<%-- send Evaluation Tasks --%>
<%-- ProcurementTitle removed from URL as a part of release 3.6.0 defect 6498 --%>
<portlet:resourceURL var='sendEvaluationTasksOverlay' id='sendEvaluationTasksOverlay' escapeXml='false'>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="action" value="propEval"/>
<%-- Code updated for R4 Ends --%>
</portlet:resourceURL>
<input type = 'hidden' value='${sendEvaluationTasksOverlay}' id='hiddensendEvaluationTasksOverlayContentUrl'/>
<%-- send Evaluation Tasks Ends--%>

<%-- Creating hidden fields to get the data in the controller --%>
<input type = 'hidden' value='${viewEvaluationSummary}' id='hiddenViewEvaluationSummary'/>
<input type = 'hidden' value='${cancelEvaluationTasks}' id='hiddenCancelEvaluationTasksOverlayContentUrl'/>
<input type = 'hidden' value='${getDBDDocs}' id='hiddenDownloadDBDDocs'/>
<input type="hidden" value="${filtered}" id="filtered"/>
<input type = "hidden" value="${evaluationStatusId}" id="evaluationStatusId" name="evaluationStatusId"/>
<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
<input type = "hidden" value="${proposalId}" id="proposalId" name="proposalId"/>
<input type = 'hidden' value='${markReturnedForRevision}' id='hiddenMarkReturnedForRevision'/>
<input type = 'hidden'  value='${markNonResponsive}' id='hiddenMarkNonResponsive' />
<%-- Code updated for R4 Starts --%>
<input type = 'hidden'  value='${unlockProposal}' id='hiddenUnlockProposal' />
<%-- Code updated for R4 Ends --%>
<input type = 'hidden' value='' id='proposalTitle1' name='proposalTitle' />
<input type = 'hidden' value='' id='organizationName1' name='organizationName' />
<nav:navigationSM screenName="EvaluationStatus"> 	
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/EvaluationStatus.js"></script> 
<portlet:defineObjects />
<portlet:actionURL var="pagingURL" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>	
</portlet:actionURL>
<input id="screenLockedFlag" type="hidden" value="${accessScreenEnable}"/>
<%-- Form Tag Starts --%>
<form:form action="${pagingURL}" name="evaluationForm" id="evaluationForm" method="post" commandName="EvaluationBean">
<input type="hidden" id="topLevelFromRequest" name="topLevelFromRequest" value="${topLevelFromRequest}"/>
<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest" value="${midLevelFromRequest}"/>
<input type="hidden" name="nextPage" value="" id="nextPage"/>
<input type="hidden" name="procurementId" value="${procurementId}" />
<input type="hidden" name="proposalId" value="${proposalId}" />
<%-- Code updated for R4 Starts --%>
<input type = "hidden" value="${evaluationGroupId}" name="evaluationGroupId"/>
<input type = "hidden" value="${evaluationPoolMappingId}" name="evaluationPoolMappingId"/>
<input type = "hidden" value="${competitionPoolId}" name="competitionPoolId"/>
<%-- Code updated for R4 Ends --%>
<input type = 'hidden' value='${hideExitProcurement}' name="hideExitProcurement"/>
<div class='clear'></div>
<div id='tabs-container'>
<%-- Container Starts --%>
<%-- Code updated for R4 Starts --%>
<h2>Evaluation Status
	<a id="returnEvaluationSummary" class="floatRht returnButton" href="#">Proposals and Evaluations Summary</a>
</h2>
<%-- Code updated for R4 Ends --%>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Evaluation Status" name="screenName"/>
		</d:content>

<%--  Error Message shown below --%>
<div id="jsmessagediv" class="failed" style="display: none;">
				<div id="jsMessageContent"></div><img onclick="showMe('jsmessagediv', this)"  
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg"></div>
<%--Test to display the messages on the page  --%>
<c:if test="${message ne null}">
<%-- Code updated for R4 Starts --%>
<div id="messagediv" class="${messageType}" style="display: block;">

				${message}<img onclick="showMe('messagediv', this)" 

					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg"></div>
					<%-- Code updated for R4 End --%>

</c:if>
<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
</c:if>

<div class='hr'></div>

<portlet:actionURL var="evalFilterUrl" escapeXml="false">
</portlet:actionURL>
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
		<!-- Starts R5 : Enhanced Evaluation -->
		<c:if test="${totalEvaluationData ne null}">
		<div class="row">
			<span class="label">Evaluations Complete:</span>
			<span class="formfield">${totalEvaluationData.totalEvaluationCompleted}(<fmt:formatNumber type="number" maxFractionDigits="2" value="${totalEvaluationData.percCompleted}" />%)</span>
		</div>
		<div class="row">
			<span class="label">Evaluations In Progress:</span>
			<span class="formfield">${totalEvaluationData.totalEvaluationInProgess}(<fmt:formatNumber type="number" maxFractionDigits="2" value="${totalEvaluationData.percInProgress}" />%)</span>
		</div>
		</c:if>
		<!-- Ends R5 : Enhanced Evaluation -->
	</div>
	<%-- Code updated for R4 Ends --%>
<%--Filter and Reassign section starts --%>
<div class="taskButtons">
		<span class="floatLft">
			<input type="button" value="Filter Items" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');" /> &nbsp;
		</span>
	<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
		<%--test to display send evaluation task button --%>
		<c:if test="${!lbCancelEvTaskVisibiltyFlag}">
				<c:if test="${showSendEvalTaskButton and !isProcClosedOrCancelled}">
				<span class="floatLft">
					<c:set var="sectionName"><%=HHSComponentMappingConstant.S215_SEND_EVALUATION_TASK_BUTTON%></c:set>
					<d:content section="${sectionName}">
						<input type="button" value="Send Evaluation Tasks" class='btnSend'  onclick="sendEvalutionTasks(this);"
							<c:if test="${not enableSendEvalTaskButton}">disabled="disabled"</c:if>/>
					</d:content>
				</span>
			</c:if>
		</c:if>
		<%--test to display download DBD Visibility button --%>
		<c:set var="sectionDownloadDB"><%=HHSComponentMappingConstant.S215_DOWNLOAD_DB_DOCS_BUTTON%></c:set>
			<d:content section="${sectionDownloadDB}">
				<c:if test="${downloadDBDVisibiltyFlag and !isProcClosedOrCancelled}">
					<span class="floatLft">
						<input type="button" id="DownloadDBDDocs" value="Download DBD Docs" style="margin-top: 10px;" class='downloadedDBD'></input>
					</span>
				</c:if>
			</d:content>
			<%--test to display Cancel Evaluation Task Visibility button --%>
			<c:set var="sectionCancelEvaluation"><%=HHSComponentMappingConstant.S215_CANCEL_EVALUATION_TASK_BUTTON%></c:set>
			<d:content section="${sectionCancelEvaluation}">
			 <%-- modified the check to hide save button if review score task has been generated once. --%>
				<c:if test="${lbCancelEvTaskVisibiltyFlag and !isProcClosedOrCancelled}">
					<span class="floatLft" style="padding-top: 6px;" >
					<input type="button" value="Cancel Evaluation Tasks" class='btnCancel' onclick="cancelEvalutionTasks(this);"></input>
				</span>
				</c:if>
			</d:content>
		</d:content>
		<!-- Starts R5 : Enhanced Evaluation -->
		<c:set var="sectionViewProgress"><%=HHSComponentMappingConstant.S215_VIEW_PROGRESS_BUTTON%></c:set>
		<!-- Begin QC8914 R7.2.0 Oversight Role Hide -->
		 <% 
		 if(! CommonUtil.hideForOversightRole(request.getSession()))
		 {%>
			<d:content section="${sectionViewProgress}">
				<c:if test="${totalEvaluationData ne null}">
					<span class="floatLft" style="padding-top: 6px;">
						<input class="viewprogressBtn" type="button" value="    View Progress" onclick="viewProgress('${procurementId}','${evaluationPoolMappingId}');"></input>
					</span>
				</c:if>
			</d:content>
		<%} %>
		<!-- End QC8914 R7.2.0 Oversight Role Hide -->	
		<!-- Ends R5 : Enhanced Evaluation -->
		
		<%--test to display total evaluation data above the table --%>
		<%--Start R5 : comment  --%>
		<%--End R5 : comment  --%>
		<%-- Popup for Filter Task Starts --%>
<div id="documentValuePop" class='formcontainerFinance' style='width:512px !important'>
				<c:set var="proposalSubmitted"><fmt:message key='PROPOSAL_SUBMITTED'/></c:set>
				<c:set var="returnedForRevision"><fmt:message key='PROPOSAL_RETURNED_FOR_REVISION'/></c:set>
				<c:set var="acceptedForEvaluation"><fmt:message key='PROPOSAL_ACCEPTED_FOR_EVALUATION'/></c:set>
				<c:set var="proposalEvaluated"><fmt:message key='PROPOSAL_EVALUATED'/></c:set>
				<c:set var="proposalscoresReturned"><fmt:message key='PROPOSAL_SCORES_RETURNED'/></c:set>
				<c:set var="proposaslSelected"><fmt:message key='PROPOSAL_SELECTED'/></c:set>
				<c:set var="notSelected"><fmt:message key='PROPOSAL_NOT_SELECTED'/></c:set>
				<c:set var="nonResponsive"><fmt:message key='PROPOSAL_NON_RESPONSIVE'/></c:set>
				<c:set var="inProgress"><fmt:message key='EVALUATE_PROPOSAL_TASK_IN_REVIEW'/></c:set>
				<c:set var="taskCompleted"><fmt:message key='EVALUATE_PROPOSAL_TASK_SCORES_COMPLETED'/></c:set>
				<%-- Code updated for R4 Starts --%>
				<c:set var="proposalPendingReassignment"><fmt:message key='PROPOSAL_PENDING_REASSIGNMENT'/></c:set>
				<%-- Code updated for R4 Ends --%>
<div class='close'>
	<a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
	
<div class='row'>
	<span class='label'>Proposal Title:</span> 
	<span class='formfield'>
 		<form:input path="proposalTitle" onchange="enableDisableDefaultFilter()" cssClass="widthFull" id="proposalTitle" maxlength="60" title="Enter at least 5 characters of the Proposal Title."/> 
 	</span>
 	<span class="error"></span>
 </div>
<div class='row'>
	<span class='label'>Provider Name:</span> 
	<span class='formfield'> 
		<form:input path="organizationName" onchange="enableDisableDefaultFilter()" cssClass="widthFull" id="organizationName" maxlength="100" title= "Enter at least 3 characters of the Provider Name and select from the suggestions listed."/> </span>
</div>

	<div class='row'>
		<span class='label'>Proposal Status:</span>
			<span  class='formfield' id='firstLevelCheckBox'> 
			 	<span class='leftColumn'> 
				 <span>
				 	<form:checkbox path="proposalStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${proposalSubmitted}" id='chkSubmitted' />
					 <label for='chkSubmitted'>Submitted</label>
				 </span> 
				<span>
					<form:checkbox path="proposalStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${returnedForRevision}" id='chkReturned' />
					<label for='chkReturned'>Returned for Revision</label>
				</span> 
				<span>
					<form:checkbox path="proposalStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${acceptedForEvaluation}" id='chkAcceptedEvaluation' />
					<label 	for='chkAcceptedEvaluation'>Accepted for Evaluation</label>
				</span>
			</span> 
			
			<span class='rightColumn'>
				<span>
					<form:checkbox path="proposalStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${nonResponsive}" id='chknonResponsive' />
					<label for='chknonResponsive'>Non-Responsive</label>
				</span>
			<%-- Code updated for R4 Starts --%>
				<span>
					<form:checkbox path="proposalStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${proposalPendingReassignment}" id='chkPendingReassignment' />
					<label for='chkPendingReassignment'>Pending Reassignment</label>
				</span>
				<%-- Code updated for R4 Ends --%>
			</span>
		</span>
	</div>

		<div class='row'><span class='label'>Evaluation Status:</span> 
						<span class='formfield' id='secondLevelCheckBox'> 
						<span class='leftColumn'> 
							<span>
								<form:checkbox path="evaluationStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="" id='chkNonStarted' />
								<label for='chkNonStarted'>Not Started</label>
							</span>
						<span>
							<form:checkbox path="evaluationStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${inProgress}" id='chkInProgress' />
							<label for='chkInProgress'>In Progress</label>
						</span>
						<span>
							<form:checkbox path="evaluationStatusList"  onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${taskCompleted}" id='chkComplete' />
							<label for='chkComplete'>Completed</label>
						</span>
					</span> 
					
					<span class='rightColumn'> </span> 
				</span>
		</div>

	<div class="buttonholder">
		<input type="button" class='graybtutton' id="clearfilter" value="Set to Default Filters" onclick="clearEvaluationFilter();"  />
		<input type="button" value="Filter" onclick="displayFilter()" /></div>
	</div>
	
	<%-- Popup for Filter Task Ends --%> 
		</div>
		
<%--Filter and Reassign section ends --%>

<div class='clear'></div>
<%-- Grid Starts --%>
<%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>
<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
<div class="tabularWrapper gridfixedHeight">
<st:table 
	objectName="evaluationDetailList" displayTitle="no" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
	<st:property headingName="Proposal Id"
		columnName="proposalId" size="20%" align="center" sortType="proposalId" sortValue="asc">
	</st:property>
	<st:property headingName="Proposal Title"
		columnName="proposalTitle" size="20%" align="center" sortType="proposalTitle">
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationProposalTitleExtension" />
	</st:property>
	<st:property headingName="Provider Name" columnName="organizationName"
		size="20%" align="center" sortType="organizationName">
	</st:property>
	<st:property headingName="Proposal Status" columnName="proposalStatus" size="20%"
		align="center" sortType="proposalStatus">
	</st:property>
	<st:property headingName="Evaluations in Progress" columnName="evalutionsInProgress"
		size="15%" align="center" sortType="evalutionsInProgress" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationInProgressExtension" />
		
	</st:property>
	<st:property headingName="Evaluations Complete" columnName="evalutionsCompleted"
		size="15%" align="center" sortType="evalutionsCompleted" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationCompletedExtension" />
		
	</st:property>
 	<st:property headingName="Actions" columnName="actions"
		size="15%" align="center" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationActionExtension" />
	</st:property>
</st:table>
<%-- display message if no data found for the page --%>
<c:choose>

      <c:when test="${fn:length(evaluationDetailList) eq 0}">
      <div class='noRecord'>No Records Found</div>
      </c:when>
<c:otherwise>
	<div class="floatLft"><span>Proposals: <label>${noOfProposal}</label></span></div>
</c:otherwise>
</c:choose>
</div>
</d:content>
</div>
<%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>
</form:form>
<%-- Form Tag Ends --%>
</nav:navigationSM>
<%-- Navigation Tag Ends --%>

<div class="alert-box alert-box-evaluationTasks">
	<div class='tabularCustomHead'>Send Evaluation Tasks</div>
	<a href="javascript:void(0);" class="exit-panel cancel-evalutionTasks">&nbsp;</a>
	<div id="requestCancel"></div>
</div>
<div class="overlay"></div>
<%-- screen 258 - Mark Non responsive --%>
			<div class="overlay"></div>
			<div class="alert-box alert-box-nonResponsive">
				<div class="content">
					<div class="tabularCustomHead">Confirm Action</div> 
					<div class='tabularContainer'>
						<h2>Mark Non-Responsive</h2>
						<div class='hr'></div>
						<div id="markNonResponsive">
								<p>Are you sure you want to mark the following Proposal as Non-Responsive?</p>	
								<p>All Proposals marked Non-Responsive will not be sent to evaluators for review and will be disqualified from competing in this Procurement.
								</p>				
							<div class="buttonholder">
							<input type="button" class="graybtutton"  id="cancelMarkNonResponsive" value="Cancel" onclick="cancelOverlay();" />
							    	<input type="submit" class="redbtutton"  id="yesMarkNonResponsive" value="Yes, Mark Non-Responsive" onclick="markNonResponsive();"/>
							    </div>
						</div>
					</div>			
				</div>
				<a  href="javascript:void(0)" class="exit-panel cancel-nonResponsive" >&nbsp;</a>
			</div>
			
<%-- screen 258 - Mark Non responsive Ends--%>

<%-- 276 - Mark Returned for Revision Starts--%>
	<div class="overlay"></div>
	<div class="alert-box alert-box-returnedForRevision">
		<div class="content">
			<div class="tabularCustomHead">Confirm Action</div> 
			<div class='tabularContainer'>
				<h2>Mark Returned For Revision</h2>
				<div class='hr'></div>
				<div id="requestReturnedForRevision">
						<p>Are you sure you want to mark the following Proposal as Returned For Revision?</p>	
						<p>If you mark this Proposal Returned For Revision, the Provider will receive a <br>
							notification to correct their Proposal.</p>				
					<div class="buttonholder">
				    	<input type="button" class="graybtutton" id="cancelReturnedForRevision" value="Cancel" onclick="cancelReturnedForRevisionOverlay();" />
				    	<input type="submit" class="redbtutton" id="yesReturnForRevision" value="Yes, Return this Proposal for Revision" onclick="returnForRevision();" />
				    </div>
				</div>
			</div>			
		</div>
		<a  href="javascript:void(0)" class="exit-panel request-ReturnedForRevision" >&nbsp;</a>
	</div>
	
<%--276 - Mark Returned for Revision Ends--%>

<%--screen 258--%>
<div class="alert-box-contact">
      <div id="contactDiv"></div>
</div>
<div class="alert-box alert-box-cancelEvaluationTasks">
		<div class='tabularCustomHead'>Cancel Evaluation Tasks</div>
		<a href="javascript:void(0);" class="exit-panel cancelAllEvaluationTasks">&nbsp;</a>
		<div id="cancelAllEvaluationTasks"></div>
</div>

<!-- Starts R5 : Enhanced Evaluation -->
<div class="alert-box alert-box-viewProgress" style="left:auto;">
		<div class='tabularCustomHead'>View Evaluation Progress</div>
		<a href="javascript:void(0);" class="exit-panel viewAllProgress">&nbsp;</a>
		<div id="viewAllProgress" style="margin:10px;" ></div>
</div>
<!-- Ends R5 : Enhanced Evaluation -->
