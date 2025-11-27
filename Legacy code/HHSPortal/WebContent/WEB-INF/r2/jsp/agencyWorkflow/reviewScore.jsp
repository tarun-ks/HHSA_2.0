<%-- This jsp used to dispaly the review score After Evaluation --%>
<%-- This Jsp was added as a part of R4 --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" var="status"/>
<%@page errorPage="/error/errorpage.jsp" %>
<style>
	.Column2{
		width:680px !important;
		height: 607px;
		min-height: 607px !important;
	}
.Column1{ 
		min-height: 570px;
	}
						<%--code updation for R4 starts--%>
.alert-box-amend-contract .ui-state-active {
	background: #4297E2 !important
}
.alert-box-amend-contract{
	background: #FFF;
    display: none;
    z-index: 1001;
    position: fixed
}
.tableheadReviewScore, .tableheadReviewScore>th{
	background-color: #E4E4E4 !important;
}
#evaluatorScoreTable th.evaluationScore, #evaluatorScoreTable th.totalScore, #evaluatorScoreTable td {
    border: 1px solid lightgrey !important;
}	
			
.container3{
	position: relative;
}

.container4{
	position: relative;
	right: 2px;
}
.bottom3 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
	border-left: 6px solid black;
	margin-top: 3px;
}

.bottom4 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
	border-top: 6px solid black;
	top: 5px;
}
</style>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/reviewScore.js"></script>  
<portlet:actionURL var="reviewScoresUrl" escapeXml="false"/>
<portlet:resourceURL var='organizationSummary' id="organizationSummary" escapeXml='false'>
	<portlet:param name="jspPath" value="procurement/"/>
</portlet:resourceURL>

<!-- Start : R5 - Added to view detail on change the rounds -->
<portlet:resourceURL var="getEvaluatorRoundDetails" id="getEvaluatorRoundDetails" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="getEvaluatorRoundDetails" id="getEvaluatorRoundDetailsUrl" value="${getEvaluatorRoundDetails}" />
<!-- End : R5 - Added to view detail on change the rounds -->

<%--renderURL for rendering to Agency Task List screen--%>
<portlet:renderURL var="reviewScoresRenderUrl" escapeXml="false"></portlet:renderURL>
<%--actionURL to view Proposal Summary--%>
<portlet:actionURL var="viewProposalSummaryUrl" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="submit_action" value="viewProposalSummary" />
	<portlet:param name="procurementId" value="${taskDetailsBean.procurementId}" />
</portlet:actionURL>
					<%--code updation for R4 starts--%>
<%--resource URL to view evaluator scores and comments added for enhancement 5415 starts--%>
<portlet:resourceURL var="viewEvaluatorCommentsUrl" id="viewEvaluatorCommentsForReviewScore" escapeXml="false">
		<portlet:param name="procurementId" value="${procurementId}" />
		<portlet:param name="proposalId" value="${proposalId}" />
</portlet:resourceURL>
<%--resource URL to view evaluator scores and comments added for enhancement 5415 ends--%> 
					<%--code updation for R4 ends--%>
<form:form id="reviewEvaluationForm" name="reviewEvaluationForm" action="" method ="post">
	<%--R5: Start for Enhanced Evaluation screens --%>
		<%--get maximum version number --%>
	<c:set var="versionNumber" value="1" />
	<c:forEach items="${evaluatorScoreList}" var="outNumber">
        <fmt:parseNumber var="i" value="${outNumber.versionNumber}" />
        <c:forEach items="${evaluatorScoreList}" var="inNumber">
            <fmt:parseNumber var="j" value="${inNumber.versionNumber}" />
            <c:if test="${i>j}">
                <c:set var="versionNumber" value="${i}" />
            </c:if>
        </c:forEach>
    </c:forEach>
	<%--R5: End for Enhanced Evaluation screens --%>

	<input type="hidden" id="submit_action" name="submit_action" value=""/>
	<input type="hidden" id="hiddenDocumentId" value="" name="documentId"/>
	<input type="hidden" value='${organizationSummary}' id="hiddenOrganizationSummary" />
	<!--Start || Changes done for enhancement QC : 5688 for Release 3.2.0-->
	<input type="hidden" id="controller_action" name="controller_action"/>
	<!--End || Changes done for enhancement QC : 5688 for Release 3.2.0-->	
	<input type="hidden" id="viewProposalSummaryUrl" value="${viewProposalSummaryUrl}"/>
	<input type="hidden" id="orgType" value="" name="orgType"/>	
	<input type="hidden" name="procurementId" id="procurementId" value="${taskDetailsBean.procurementId}"/>	
	<input type="hidden" name="proposalId" id="proposalId" value="${taskDetailsBean.proposalId}"/>
	<input type="hidden" name="organizationId" id="organizationId" value="${taskDetailsBean.organizationId}"/>
	<input type="hidden" name="workflowId" value="${workflowId}"/>
	<input type="hidden" name="taskId" value="${taskDetailsBean.taskId}"/>
	<input type="hidden" name="evaluationPoolMappingId" value="${taskDetailsBean.evaluationPoolMappingId}"/>
	<input type="hidden" name="actionUrl" id="actionUrl" value="${reviewScoresUrl}"/>
	<input type="hidden" id="proposalTaskStatus" name="proposalTaskStatus" value='${proposalTaskStatus}'/>
	<input type="hidden" id="tabSelected" value=""/>
	<c:set var="score" value="${(fn:length(evaluationResultList))}" />
	<%--Start:R5 - Set Total number of evaluators --%>
	<c:set var="TotalEvalScore" value = "${fn:length(evaluatorScoreList)}" />
	<%--End:R5 - Set Total number of evaluators --%>
	<input type="hidden" id="reassignedToUserName" name="reassignedToUserName" value=""/>
	<input type="hidden" id="reviewScoresRenderUrl" value="${reviewScoresRenderUrl}"/>
	<input type="hidden" value="${score}" id="evaluationLength"/>
	<input type="hidden" value="${screenReadOnly}" id="screenReadOnly"/>
	<input type="hidden" value="${disableReassignDropdown}" id="disableReassignDropdown"/>
	<input type="hidden" value="${taskDetailsBean.taskStatus}" id="onloadStatus"/>
	<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='procurementId' value='${taskDetailsBean.procurementId}' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
   <%--added for enhancement 5415 starts--%>
    <input type="hidden" value="${viewEvaluatorCommentsUrl}" id="evaluatorCommentsUrlId"/>
    <%--added for enhancement 5415 ends--%>
<%-- Body Container Starts --%>
<h2>
	<label class='floatLft'>
		Task Details: 
		<label>${taskDetailsBean.taskType} - ${taskDetailsBean.procurementTitle}</label>
	</label>
	<span class="linkReturnVault floatRht"><a href="javascript:returnToAgencyTaskList('${workflowId}');">Return</a></span>
</h2>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" class="localTabs" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Task Details - Review Scores" name="screenName"/>
		</d:content>	
 
<div class="complianceWrapper">
<div id="ErrorDiv" class="failed breakAll"> </div>
<%--Filter and Reassign section starts --%>
	<div class="tasktopfilter taskButtons">
		<div class="taskfilter">
				<select id="reassignDropDown" name="reassignedTo" onchange="enableDisableReassignButton()">
					<option value=""></option>
					<c:forEach var="reAssignUser" items="${reassignUserMap}">
				    	<option value="${reAssignUser.key}">${reAssignUser.value}</option>
					</c:forEach>					
				</select> 
			<input type="button" id="reassignButton" value="Reassign Task" onclick="reassignTask()" />			 
		</div>
		<div class="taskreassign">
			<span><b>Status:</b> <span id="reviewStatusFinish">${taskDetailsBean.taskStatus}</span></span> 
			<input type="hidden" id="taskStatus" name="taskStatus"/>
			<input type="button" class="button" id="finishTask" value="Finish Task" name="finish" />
		</div>
	</div>
	<%--Filter and Reassign section ends --%> 
 
<%-- Left Column Start --%>

	<div class="Column1">
	<h4>Procurement Details</h4>
						<%--code updation for R4 starts--%>
	 	<label>Procurement Title:</label>
		<div style="word-break: break-all"><a href='javascript:viewProcurementSummary();' class='localTabs'>${taskDetailsBean.procurementTitle}</a></div>
 		<c:if test="${(taskDetailsBean.isOpenEndedRfp ne null) and (taskDetailsBean.isOpenEndedRfp eq '1')}">
			<label>Evaluation Group:</label>
			<div style="word-break: break-all">${taskDetailsBean.evaluationGroupTitle}</div>
		</c:if>
 		<label>Competition Pool:</label>
		<div style="word-break: break-all">${taskDetailsBean.competitionPoolTitle}</div>
		<label>Provider Name:</label>
		<div style="word-break: break-all"><a href='javascript:;' id='organizationLink' class='localTabs'>${taskDetailsBean.organizationName}</a></div>
        
        <label>Proposal Title:</label>
        <div style="word-break: break-all"><a href='javascript:viewProposalDetail(${taskDetailsBean.proposalId});' class='localTabs'>${taskDetailsBean.proposalTitle}</a></div>
 
 <%-- Start || Changes done for enahncement 6636 for Release 3.12.0 --%>	
        <label>Proposal Id:</label>
        <div style="word-break: break-all">${taskDetailsBean.proposalId}</div>
 <%-- End || Changes done for enahncement 6636 for Release 3.12.0 --%>	
		<label>Procurement E-PIN:</label>
		<div style="word-break: break-all">${taskDetailsBean.procurementEpin}</div>
		<c:if test="${(taskDetailsBean.isOpenEndedRfp eq null) or (taskDetailsBean.isOpenEndedRfp eq '0')}">
			<label>Finalize Evaluation Date:</label>
			<div>${taskDetailsBean.finalizeEvaluationDate}</div>
			
			<label>Award Selection Date:</label>
			<div>${taskDetailsBean.awardSelectionDate}</div>
 		</c:if>
 							<%--code updation for R4 ends--%>
		<div></div>
        
		<h4>Task Details</h4>
		<label>Task Name:</label>
		<div>${taskDetailsBean.taskType}</div>
 
		<label>Task Instructions:</label>
		<%-- 2nd part of below sentence is removed as part of defect 5654, release 2.7.0 --%>
		<div>Review evaluations and mark as Accept Score or Request Score Amendment.
		</div>
 
		<label>Assigned To:</label>
		<div>${taskDetailsBean.assignedToUserName}</div>
 
		<label>Date Assigned:</label>
		<div>${taskDetailsBean.assignedDate}</div>
		
		<label>Last Modified:</label>
		<div>${taskDetailsBean.lastModifiedDate}</div>
 
	</div>
<%-- Left Column End --%>
 <!--Start Added in R5 -->
 <div class='Column2'>
 <h2>Average Proposal Scores:</h2>
		<div class='hr'></div>
		<div class='clear'>&nbsp;</div>
		<div class="scoreWrapper">
		
<%--Start : logic to count total evaluators --%>
<c:set var="countEval" value="0" />
<c:forEach var="scoreList" items="${evaluatorScoreList}" varStatus="count">
		<c:if test="${scoreList.versionNumber eq 1}">
				<c:set var="concatId1" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}" />
				<c:if test="${nameExist1 ne concatId1}">
						<td class="alignCenter"><c:set var="countEval" value="${countEval + 1}"/></td>
						<c:set var="nameExist1" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}"/>
					</td>
				</c:if>
			</tr>
		</c:if>
</c:forEach>
<c:set var="totalEvaluators" value="${countEval}"/>
<%--End : logic to count total evaluators --%>
    
    <%-- R5 : added Table --%>
    <input type="hidden" id="totalVersion" value="${versionNumber}" >
	<table cellspacing='0' cellpadding='0' border="1" id="evaluatorScoreTable">
		<tbody>
		 	<tr>
		 		<th class='borderNone'></th>
				<th class='alignCenter bold' colspan="${score+1}" style="border: 1px solid #ddd; border-bottom: 0px; padding:2px">Score</th>
			<tr>
			<tr>
				<th class='borderNone'></th>
				<%--Start : Iterate Score Header --%>
				<c:forEach var="i" begin="1" end="${score}">
					<th class='evaluationScore' title="${evaluationResultList[i-1].scoreCriteria}">${i}</th>
				</c:forEach>
				<%--End : Iterate Score Header --%>
				
				<%--Start : Total Header Column--%>
				<th class='evaluationScore'>Total</th>
				<%--End : Total Header Column--%>
			</tr>
			<tr>
			<c:set var="maxTotal" value="0"/>
				<th class='totalScore alignCenter'>Maximum Score</th>
				<%--Start : Each Round's Maximum Score --%>
				<c:forEach var="evalBean" items="${evaluationResultList}" varStatus="counter">
					<th class='totalScore alignCenter'>${evalBean.maximumScore}</th>
					<c:set var="maxTotal" value="${maxTotal + evalBean.maximumScore}" />
				</c:forEach>
				<%--End : Each Round's Maximum Score --%>
				
				<%--Start : Total Score --%>
				<th class='totalScore alignCenter'>
					${maxTotal}
				</th>
				<%--End : Total Score --%>
			</tr>
			
			<%--R5 : Start Iterate Round--%>
			<c:forEach begin="1" end="${versionNumber}" varStatus="loop">
				<c:set var="currVersion" value="${loop.end - loop.count + 1}"/>
				
				<c:set var="currIterateDate" value=""/>
			  	<c:forEach var="dtScoreList" items="${evaluatorScoreList}" varStatus="count">
			  		<c:if test="${dtScoreList.versionNumber eq currVersion}">
		  				<c:set var="currIterateDate" value="${dtScoreList.submissionCloseDate}"/>
		  				<c:set var="count.index" value="${TotalEvalScore}"/>	
			  		</c:if>
				</c:forEach>
				
				<tr id="round${currVersion}" class="evaluationMainRow">
					<td onclick="showEvalInfo('eval${currVersion}','round${currVersion}');"  style="cursor: pointer;width:200px;" class="alignCenter"><div class="container3" ><div class="bottom3"></div></div>
									<div class="container4" style="display: none;"><div class="bottom4"></div></div>
					<b>Iteration ${currVersion} - ${currIterateDate}</b></td>
							<c:set var="TotalLast" value="0"></c:set>
							<c:set var="TotalRow" value="0"></c:set>
							<c:forEach var="i" begin="1" end="${score}">
								<c:set var="iterateTotal" value="0"></c:set>
								<c:forEach var="evalScore1" items="${evaluatorScoreList}" varStatus="counter">
								<c:if test = "${evalScore1.versionNumber eq currVersion && i eq evalScore1.scoreSeqNum}">
									<c:set var="iterateTotal" value="${iterateTotal + evalScore1.score}"></c:set>
									<c:set var="TotalRow" value="${TotalRow+1}"/>
									
								</c:if>
								</c:forEach>
								<td class="alignCenter">
									<c:set var="TotalLast" value="${TotalLast + iterateTotal}"></c:set>
									<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${iterateTotal/totalEvaluators}" />
								</td>
							</c:forEach>
							<td class="alignCenter"><fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${TotalLast/totalEvaluators}" /></td>
				</tr>
				
				<c:set var="nameExist1" value=""/>
				<c:set var="evalCount" value="1"/>
					<c:forEach var="scoreList" items="${evaluatorScoreList}" varStatus="count">
							<c:if test="${scoreList.versionNumber eq currVersion}">
							<tr style="display: none;background-color:#FFFACD;" class="eval${currVersion}">
									<c:set var="concatId1" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}" />
									<c:set var="evaluatorIdVar1" value="" />
									<c:set var="evaluatorIdVarTop" value="" />
									
									<c:if test="${nameExist1 ne concatId1}">
										<td>
											<c:choose>
													<c:when test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
														<c:choose>
															<c:when test="${scoreList.externalEvaluatorName eq 'internal'}">
																${scoreList.evaluatorFirstName}
															<c:set var="evaluatorIdVarTop" value="${scoreList.evaluatorDisplayId}"></c:set>
															</c:when>
															<c:otherwise>
																${scoreList.externalEvaluatorName} (via ${scoreList.evaluatorFirstName})
															<c:set var="evaluatorIdVarTop" value="${scoreList.evaluatorDisplayId}"></c:set>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														Evaluator #${evalCount}
														<c:set var="evalCount" value="${evalCount + 1}"/>
														<c:set var="evaluatorIdVarTop" value="${scoreList.evaluatorDisplayId}"></c:set>
													 </c:otherwise>
											</c:choose>
											<c:set var="iTotal" value="0"></c:set>
											<c:forEach var="i" begin="1" end="${score}">
												   <c:forEach var="iScoreList" items="${evaluatorScoreList}" varStatus="count">
												  			<c:if test="${i eq iScoreList.scoreSeqNum 
													 				&& currVersion eq iScoreList.versionNumber 
													 				&& evaluatorIdVarTop eq iScoreList.evaluatorDisplayId}">
													 				
													 				<c:set var="arrow" value=""/>
																	<c:if test="${iScoreList.scoreChangeType eq '1'}">
																			<c:set var="arrow" value="arrow-up"/>
																	</c:if>
																	<c:if test="${iScoreList.scoreChangeType eq '-1'}">
																			<c:set var="arrow" value="arrow-down"/>
																	</c:if>
																	
												  				<td class="alignCenter">${iScoreList.score} <div class="${arrow}"> </div></td>
												  				<c:set var="iTotal" value="${iTotal + iScoreList.score}"></c:set>
												  			</c:if>
												 	</c:forEach>
											</c:forEach>
											<td class="alignCenter">${iTotal}</td>
											<c:set var="nameExist1" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}"/>
										</td>
									</c:if>
								</tr>
							</c:if>
					</c:forEach>
				
			</c:forEach>
			<%--R5 : End Iterate Round--%>
		</tbody>
	</table>
	</div>
			
 
 <h2>Evaluation Criteria</h2>
		<div class='hr'></div>
		<div class='clear'>&nbsp;</div>
		<%-- Evaluation Criteria Start --%>
		<div class="tabularWrapper">
		<table width="100%" cellspacing='0' cellpadding='0' border="1"
			class="grid" id="evalCriteriaList">
			<thead>
				<tr>
					<th>Score No.</th>
					<th>Score Criteria</th>
					<th>Maximum Score</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="evalBean" items="${evaluationResultList}"
					varStatus="counter">
					<tr class="${counter.count%2==0?'oddRows':'evenRows' }">
						<%--Added Score and points per enhancement 5415 --%>
						<td>Score ${counter.count}</td>
						<td>${evalBean.scoreCriteria}</td>
						<td>${evalBean.maximumScore} points</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
 </div>
 <!--End Added in R5 -->
<div>&nbsp;</div>
 
 <div class='clear'>&nbsp;</div>
	  <%-- Contract(s) Section Starts Here --%>
	<div id='contractTabs'>
	<div class="customtabs">
			<ul>
				<li><a href='#evaluationScores' class="showButton localTabs">Evaluation Scores</a></li>
				<li><a href='#comments' class="showButton localTabs">Comments</a></li>
				<li class='liBorderNone'></li>
				<div class='floatRht' id="saveDiv"><input type="button" id="saveButton" class="graybtutton" value="Save"/></div>				
			</ul>
			
		</div>	
			<div id='evaluationScores'>
				<div class="scoreWrapper">
					<table width="100%" cellspacing='0' cellpadding='0' border="1" id="evaluationScoresTable">
						<tr>
							<td class='nobdr'></td>							
							<td class='alignCenter bold' colspan="${score}" style="border: 1px solid #ddd; border-bottom: 0px; padding:2px">Score</td>					
							<td colspan="2" class='nobdr'></td>
						</tr>
						<tr class="tableheadReviewScore">
							<th>Evaluator</th>							
							<c:forEach var="i" begin="1" end="${score}">
								<th class='evaluationScore'>${i}</th>
							</c:forEach>						
							<th class='alignCenter'>Total</th>
							<th>Action</th>
						</tr>
						<c:set var="innerCounter" value="0"/>
						<c:set var="scoreListMain" value="${evaluatorScoreList}"/>
						<c:set var="color" value="1"/>
						<c:set var="nameExist" value=""/>
						<c:set var="avgScore" value="0"/>
		 				<c:set var="increment" value="1"/>
		 				<fmt:message key="REVIEW_PROPOSAL_TASK_ACCEPTED" bundle="${status}" var="REVIEW_PROPOSAL_TASK_ACCEPTED"/>
		 				<fmt:message key="REVIEW_PROPOSAL_TASK_SCORES_RETURNED" bundle="${status}" var="REVIEW_PROPOSAL_TASK_SCORES_RETURNED"/>
						<!-- Start R5: update logic to view evaluator's score -->
						<c:set var="tmptotalScore" value="0" />
						<c:set var="evalCount" value="1" />
						<c:forEach var="scoreList" items="${evaluatorScoreList}" varStatus="count">
							<!--Start Added in R5 -->
							<c:if test="${scoreList.versionNumber eq versionNumber}">
								<c:set var="concatId" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}" />
								<c:set var="evaluatorIdVar" value="" />
								<c:if test="${nameExist ne concatId}">
									<tr id="avgCount${count.index}" class="${innerCounter%2!=0?'oddRows':'evenRows'}">
										<td class='' >
											<c:choose>
												<c:when test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
													<a href="#" id="nameLink" class="showButton" onclick="showCommentsPopup('${scoreList.evaluationStatusId}','${scoreList.evaluatorFirstName}')">
														<c:choose>
															<c:when test="${scoreList.externalEvaluatorName eq 'internal'}">
																${scoreList.evaluatorFirstName}
															</c:when>
															<c:otherwise>
																${scoreList.externalEvaluatorName} (via ${scoreList.evaluatorFirstName})
															</c:otherwise>
														</c:choose>
													</a>
													<c:set var="evaluatorIdVar" value="${scoreList.evaluatorDisplayId}"></c:set>
													<c:set var="innerCounter" value="${innerCounter + 1}"></c:set>
												</c:when>
												<c:otherwise>
													<a href="#" id="nameLink" class="showButton" onclick="showCommentsPopup('${scoreList.evaluationStatusId}','Evaluator #${evalCount}');">
																Evaluator #${evalCount}
																<c:set var="evalCount" value="${evalCount + 1}"/>
													</a>
													<c:set var="evaluatorIdVar" value="${scoreList.evaluatorDisplayId}"></c:set>
													<c:set var="innerCounter" value="${innerCounter + 1}"></c:set>
												</c:otherwise>
											</c:choose>
										</td>
											<c:set var="totalScore" value="0"/>
											<c:forEach var="i" begin="1" end="${score}">
													<td id="individualsScore" class='alignCenter'>
													<c:forEach var="inScoreList" items="${evaluatorScoreList}" varStatus="count">
															<c:if test="${inScoreList.scoreSeqNum eq i && evaluatorIdVar eq inScoreList.evaluatorDisplayId && inScoreList.versionNumber eq versionNumber}">
																		
																		<c:set var="arrow" value=""/>
																		<c:if test="${inScoreList.scoreChangeType eq '1'}">
																				<c:set var="arrow" value="arrow-up"/>
																		</c:if>
																		<c:if test="${inScoreList.scoreChangeType eq '-1'}">
																				<c:set var="arrow" value="arrow-down"/>
																		</c:if>
																		${inScoreList.score} <div class="${arrow}"> </div>
																	<fmt:formatNumber var="score1" type="number" value="${inScoreList.score}" />
																	<c:set var="totalScore" value="${(totalScore + score1)}"/>
																	<%--Start break statement --%>
																	<c:set var="count.index" value="${TotalEvalScore}"/>
																	<%--End break statement --%>
															</c:if>
													</c:forEach> 
													</td>
											</c:forEach>
										<td class='alignCenter totalScoreInComment'>
											<fmt:formatNumber maxFractionDigits="1" type="number" value="${totalScore}" />
											<c:set var="tmptotalScore" value="${tmptotalScore + totalScore}"/>
										</td>
										<td class="" >
											<c:set var="acceptScore" value=""/>
											<c:set var="requestScoreAmendment" value=""/>
											<c:choose>
												<c:when test="${(requestAmendFlag eq '1') || (scoreList.procStatusId eq REVIEW_PROPOSAL_TASK_ACCEPTED) || (versionNumber > 1 && scoreList.returnFlag eq 0)}">
													<c:set var="acceptScore" value="selected"/>
													<input type="hidden" id="prevSelectedVal" value="1" />
												</c:when>
												<c:when test="${scoreList.procStatusId eq REVIEW_PROPOSAL_TASK_SCORES_RETURNED}">
													<c:set var="requestScoreAmendment" value="selected"/>
													<input type="hidden" id="prevSelectedVal" value="2" />
												</c:when>
												<c:otherwise>
													<input type="hidden" id="prevSelectedVal" value=" " />
												</c:otherwise>
											</c:choose>
											<select class="actionDropDown" id='actionTag${increment}' name="actionTag_${scoreList.evaluationStatusId}">
												<option value=" "> </option>
												<option value="1" ${acceptScore}>Accept Score</option>
												<option value="2" ${requestScoreAmendment}>Request Score Amendment</option>
											</select>
										</td>
									</tr>
									<c:set var="nameExist" value="${scoreList.evaluatorId}${scoreList.evaluationStatusId}"/>
									<c:set var="color" value="${color+1}"/>
								</c:if>
								<c:set var="increment" value="${increment+1}"/>
							<!--End Added in R5 -->
							</c:if>
						</c:forEach>
						<input type="hidden" id="avgtotalScore" name="avgtotalScore" value="<fmt:formatNumber maxFractionDigits="2" type="number" value="${tmptotalScore/innerCounter}" />" />
						<!-- End R5: update logic to view evaluator's score -->					
					</table>
				</div>				
			</div>
			
			<div id='comments'>
			<h3>Enter any internal comments</h3>					
				<p>These comments are for your reference only and will not be visible to evaluators. Click the "Save" button above to save your comments.</p> 
				<div class="failed" id="commentError">! Internal Comments are mandatory</div>
				<div><textarea name="internalComments" id="internalComments" rows="5" class='textarea' onkeyup="setMaxLengthForEvaluatePropasal(this,1000);" onkeypress="setMaxLengthForEvaluatePropasal(this,1000);" onblur="setMaxLengthForEvaluatePropasal(this,1000);">${aoTaskDetailsBean.internalComment}</textarea>
				</div>
			</div>
		</div>
</div>
 
</form:form>

<%--  Overlay Popup --%>
<div class="overlay"></div>
<%--Help Overlay --%>
<div class="alert-box-help">
	<div class="tabularCustomHead">Procurement - Help Documents</div>
    <div id="helpPageDiv"></div>
 	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-amend-contract" id="overlayDivId">
	
</div>

