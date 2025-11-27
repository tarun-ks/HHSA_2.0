<%-- This jsp displays the evaluation summary details in the read only form--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%--Overwrite style  as per enhancement 5415 --%>
<%--Added accordianFunctions js as per enhancement 5415 --%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/viewEvaluationSummary.js"></script>
<portlet:defineObjects />
<%--resource URL to view evaluator scores and comments details added for enhancement 5415 starts--%>
<portlet:resourceURL var="viewEvaluatorCommentsUrl"
	id="viewEvaluatorComments" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}" />
	<portlet:param name="proposalId" value="${proposalId}" />
</portlet:resourceURL>
<%--resource URL to view evaluator scores and comments details added for enhancement 5415 ends--%>
<%--added for enhancement 5415 starts--%>
<input type="hidden" value="${viewEvaluatorCommentsUrl}"
	id="evaluatorCommentsUrlId" />
<%--added for enhancement 5415 starts--%>
<!-- release 5 changes for Evaluation starts -->
<style>
.accrodinWrapper {
	float: none;
	cursor: pointer;
	overflow: hidden !important;
	clear: both
}

#evaluatorScoreTable th.totalScore,#evaluatorScoreTable th.evaluationScore
	{
	background-color: #B6DDDC;
}

#evaluatorScoreTable th.totalScore,#evaluatorScoreTable th.totalScore alignCenter
	{
	background-color: #D5EBE9;
}

#evaluatorScoreTable th.headingtext {
	border: 1px solid #ddd;
	border-bottom: 0px;
	padding: 2px;
	background-color: #B6DDDC;
}

#evaluatorScoreTable th.evaluationScore,#evaluatorScoreTable th.totalScore,#evaluatorScoreTable td
	{
	border: 1px solid lightgrey !important;
}

#roundOption {
	border: 1px solid black;
	padding: 6px 4px 4px 4px;
	background-color: #E0F0FF;
	display: inline-block;
	margin-bottom: 10px;
}

.latestcheckbox {
	font-size: 11px;
}

.accrodinWrapper h5 {
	color: #fff;
	font-weight: bold;
	font-size: 1.1em;
	margin: 1.2% 0 1% 1% !important;
	padding-right: 20px
}

.providerEvaluationHeader {
	height: auto;
}

.logoWrapper {
	float: none !important;
}

.top1 {
	position: absolute;
	top: 2px;
	left: 1px;
	width: 0;
	height: 0;
	z-index: 100;
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
	border-right: 6px solid white;
}

.bottom1 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-top: 8px solid transparent;
	border-bottom: 8px solid transparent;
	border-right: 8px solid blue;
}

#container1 {
	position: relative;
	top: -15px;
	left: 131px;
	margin-left: 7px;
}

.top2 {
	position: absolute;
	top: 18px;
	left: 2px;
	width: 0;
	height: 0;
	z-index: 100;
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
	border-top: 6px solid white;
}

.bottom2 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-left: 8px solid transparent;
	border-right: 8px solid transparent;
	border-top: 8px solid blue;
	top: 17px;
}

.container3{
	position: relative;
}

.container4{
	position: relative;
	right: 2px;
}

.top3 {
	position: absolute;
	top: 2px;
	left: 1px;
	width: 0;
	height: 0;
	z-index: 100;
	border-top: 5px solid transparent;
	border-bottom: 5px solid transparent;
	border-left: 5px solid black;
}

.bottom3 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-top: 7px solid transparent;
	border-bottom: 7px solid transparent;
	border-left: 7px solid black;
}

.top4 {
	position: absolute;
	top: 6px;
	left: 2px;
	width: 0;
	height: 0;
	z-index: 100;
	border-left: 5px solid transparent;
	border-right: 5px solid transparent;
	border-top: 5px solid black;
}

.bottom4 {
	position: absolute;
	width: 0;
	height: 0;
	z-index: 99;
	border-left: 7px solid transparent;
	border-right: 7px solid transparent;
	border-top: 7px solid black;
	top: 5px;
}

#container2 {
	position: relative;
	top: -27px;
	left: 128px;
	margin-left: 5px;
}

.averageScore {
	display: none;
	width: 180px;
	height: 28px;
	font-weight: bold;
}

.iterations {
	cursor: pointer;
	width: 190px;
	height: 28px;
	font-weight: bold;
}

.displayScoreCriteria {
	margin-right: 25px;
	text-decoration: underline;
	color: blue;
}
</style>
<%--added for enhancement 5415 ends--%>
<%--Added below script as per enhancement 5415 --%>
<%-- Code updated for R4 Starts --%>
<script type='text/javascript'>
	$(document).ready(function() {
		$(function() {
			$("#accordion").accordion();
		});
	});

	//updated in R5 : This function will open the criteria and comments details on click of evaluator accordians, added as part of enhancement 5415.
	function showScoreDetails(tabName, tabId, evaluationStatusId, versionNumber) {
		pageGreyOutForSummary();
		var isChecked = $('input[type="checkbox"]').is(':checked');
		if (isChecked) {
			versionNumber--;
		}
		var v_parameter = "evaluationStatusId=" + evaluationStatusId + "&hdnTabName=" + tabName
		+"&proposalId="+${proposalId}+"&versionNumber="+versionNumber;
		var urlAppender = $("#evaluatorCommentsUrlId").val();
		jQuery.ajax({
			type : "POST",
			url : urlAppender,
			data : v_parameter,
			success : function(e) {
				var _html = e;
				if (isChecked) {
					_html = _html.replace(/isModifiedDarkGray/g, '');
				}
				$("#" + tabId).html(_html);
				removePageGreyOutForSummary();
			},
			beforeSend : function() {
			}
		});
	}

	function pageGreyOutForSummary() {
		$
				.blockUI({
					message : "<img src='../framework/skins/hhsa/images/loadingBlue.gif' />",
					overlayCSS : {
						opacity : 0.8
					}
				});

	}
	function removePageGreyOutForSummary() {
		$.unblockUI();
	}
</script>
<%-- Code updated for R4 Ends --%>
<%-- Form Tag Begins--%>
<form:form id="viewResponseForm" name="viewResponseForm"
	action="${viewDocuementInfo}" method="post"
	commandName="EvaluationBean">
	<%--Start Set VersionNumber --%>
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
	<jsp:useBean id="evaluatorMap" class="java.util.HashMap" scope="request"/>
	<%--Start Set VersionNumber --%>

	<%--Start Set number of scores --%>
	<c:set var="score" value="${(fn:length(evalCriteriaList))}" />
	<%--Start Set number of scores --%>

	<%--Start Set Total number of evaluators --%>
	<c:set var="totalEvaluator" value="${fn:length(loEvaluatorsList)}" />
	<input type="hidden" name="totEval" id="totEval"
		value="${totalEvaluator}" />
	<%--Start Set Total number of evaluators --%>


	<%--Start Set Total number of evaluators --%>
	<c:set var="TotalEvalScore" value="${fn:length(evaluatorScoreList)}" />
	<%--Start Set Total number of evaluators --%>

	<%-- View Evaluation Summary Header--%>
	<div class="hhs_header providerEvaluationHeader">
		<table width="100%">
			<tr>
				<td width="160px;">
					<div class='logoWrapper'></div>
				</td>
				<td>
					<div class='headerFields'>
						<div class="print-td">
							<b>Procurement Title: </b><label>${procurementTitle}</label>
						</div>
						<div class="print-td">
							<b>Provider Name: </b><label id="organisation_name">${organizationName}</label>
						</div>
						<%-- COMPETITION_POOL_TITLE and EVALUATION_GROUP_TITLE 
						are fetched as a part of R4 Open procurement change--%>
						<%-- Code updated for R4 Starts --%>
						<c:if test="${isOpenEndedProc eq '1'}">
							<div class="print-td">
								<b>Evaluation Group: </b> <label>${evalGroupTitle}</label>
							</div>
						</c:if>
						<div class="print-td">
							<b>Competition Pool: </b> <label>${compPoolTitle}</label>
						</div>
						<%-- Code updated for R4 Ends --%>
						<div class="print-td">
							<b>Proposal Title: </b><label>${proposalTitle}</label>
						</div>
						<div class="print-td">
							<b>Proposal Id: </b><label>${proposalId}</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

	</div>
	<%-- View Evaluation Summary Header Ends--%>

	<h2>
		Evaluation Summary
		<%-- R5 : Start  added checkbox --%>
		<span class="floatRht latestcheckbox"><label><input
				type="checkbox" name="showCurrRound" /> Show latest submitted
				evaluations</label></span>
		<%-- R5 : End added checkbox --%>
	</h2>

	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
	<d:content section="${helpIconProvider}">
		<div id="helpIcon" class="iconQuestion">
			<a href="javascript:void(0);" title="Need Help?"
				onclick="smFinancePageSpecificHelp();"></a>
		</div>
		<input type="hidden" id="screenName" value="Evaluation Summary"
			name="screenName" />
	</d:content>
	<div class='hr'></div>
	<div class='clear'>&nbsp;</div>
	<%-- Evaluation Summary Starts --%>
	<div class="scoreWrapper">

		<%-- R5 : added Table --%>
		<input type="hidden" id="totalVersion" value="${versionNumber}">
		<input type="hidden" id="tmpFirstRow" value="">
		<table cellspacing='0' cellpadding='0' border="1"
			id="evaluatorScoreTable">
			<tbody>
				<tr>
					<th class='borderNone'></th>
					<th class='alignCenter bold headingtext' colspan="${score+1}">Score</th>
				</tr>
				<tr>
					<th class='borderNone'></th>
					<%--Start : Iterate Score Header --%>
					<c:forEach var="i" begin="1" end="${score}">
						<th class='evaluationScore'
							title="${evalCriteriaList[i-1].scoreCriteria}">${i}</th>
					</c:forEach>
					<%--End : Iterate Score Header --%>

					<%--Start : Total Header Column--%>
					<th class='evaluationScore'>Total</th>
					<%--End : Total Header Column--%>
				</tr>
				<tr>
					<th class='totalScore alignCenter' style="width: 200px;">Maximum Score</th>
					<%--Start : Each Round's Maximum Score --%>
					<c:forEach var="evalCriteria" items="${evalCriteriaList}"
						varStatus="counter">
						<th class='totalScore alignCenter'>${evalCriteria.maximumScore}</th>
					</c:forEach>
					<%--End : Each Round's Maximum Score --%>

					<%--Start : Total Score --%>
					<c:set var="maxTotal" value="0" />
					<th class='totalScore alignCenter'><c:forEach
							var="evalCriteria" items="${evalCriteriaList}"
							varStatus="counter">
							<c:set var="maxTotal"
								value="${maxTotal + evalCriteria.maximumScore}" />
						</c:forEach> ${maxTotal}</th>
					<%--End : Total Score --%>
				</tr>

				<%--R5 : Start Iterate Round--%>
				<c:set var="summMaxVersion" value="${versionNumber}" />
				<c:forEach begin="1" end="${versionNumber + 1}" varStatus="loop">
					<c:set var="currVersion" value="${loop.end - loop.count + 1}" />

					<c:set var="currIterateDate" value="" />
					<c:forEach var="scoreList" items="${evaluatorScoreList}"
						varStatus="count">
						<c:if test="${scoreList.versionNumber eq currVersion}">
							<c:set var="currIterateDate"
								value="${scoreList.submissionCloseDate}" />
							<c:set var="count.index" value="${TotalEvalScore}" />
						</c:if>


					</c:forEach>

					<c:set var="isAvgRow" value="abc" />
					<tr id="round${currVersion}" class="evaluationMainRow">
						<c:choose>
							<c:when test="${empty currIterateDate}">
								<c:set var="currVersion" value="${currVersion - 1}" />
								<td class='alignCenter averageScore'>Average Score</td>
								<c:set var="isAvgRow" value="display: none;" />
							</c:when>
							<c:otherwise>
								<td class='alignCenter iterations'
									onclick="showEvalInfo('round${currVersion}');"><div class="container3" ><div class="top3"></div><div class="bottom3"></div></div>
									<div class="container4" style="display: none;"><div class="top4"></div><div class="bottom4"></div></div>Iteration
									${currVersion} - ${currIterateDate}</td>
							</c:otherwise>
						</c:choose>

						<c:set var="eachRoundTotal" value="0" />
						<%--Start Average of round for each Score--%>
						<c:forEach var="i" begin="1" end="${score}">
							<c:set var="currScore" value="${i}" />
							<c:set var="avgEachRoundTotal" value="0" />
							<c:forEach var="scoreList" items="${evaluatorScoreList}"
								varStatus="count">
								<c:choose>

									<c:when
										test="${not empty currIterateDate && scoreList.versionNumber eq currVersion && scoreList.scoreSeqNum eq currScore && currVersion eq versionNumber && scoreList.statusId eq 42}">
										<c:set var="avgEachRoundTotal"
											value="${avgEachRoundTotal + 0}" />
									</c:when>
									<c:when
										test="${scoreList.versionNumber eq currVersion && scoreList.scoreSeqNum eq currScore && currVersion eq versionNumber && scoreList.statusId ne 42}">
										<c:set var="avgEachRoundTotal"
											value="${avgEachRoundTotal + scoreList.score}" />
									</c:when>
									<c:when
										test="${scoreList.versionNumber eq currVersion && scoreList.scoreSeqNum eq currScore}">
										<c:set var="avgEachRoundTotal"
											value="${avgEachRoundTotal + scoreList.score}" />
									</c:when>
								</c:choose>

							</c:forEach>
							<c:set var="avgEachRoundTotal"
								value="${avgEachRoundTotal / totalEvaluator}" />
							<td class='alignCenter' style="${isAvgRow}"><fmt:formatNumber
									type="number" minFractionDigits="0" maxFractionDigits="2"
									value="${avgEachRoundTotal}" /></td>
							<c:set var="eachRoundTotal"
								value="${eachRoundTotal + avgEachRoundTotal}" />
						</c:forEach>
						<%--End Average of round for each Score--%>
						<%--Start Total for round--%>
						<td class='alignCenter' style="${isAvgRow}"><fmt:formatNumber
								type="number" minFractionDigits="0" maxFractionDigits="2"
								value="${eachRoundTotal}" /></td>
						<%--End Total for round--%>
					</tr>

					<%--Start R5 : Start Iterate All Evaluators --%>
					<c:forEach var="evalutor" items="${loEvaluatorsList}"
						varStatus="count">
						<c:set var="evalRound" value="" />
						<c:choose>
							<c:when test="${not empty currIterateDate}">
							 <!-- Added for Emergency Build 4.0.1 defect 8361 -->
							 <c:set var="evaluatorMapProperty">${evalutor.evaluatorFirstName}${evalutor.externalEvaluatorName}${evalutor.evaluatorSecondName}</c:set>
							 <c:set target="${evaluatorMap}" property="${fn:replace(evaluatorMapProperty,' ', '')}" value="${count.count}"/>
								<c:set var="evalRound" value="eval${currVersion}" />
							</c:when>
							<c:otherwise>
								<c:set var="evalRound" value="eval${currVersion + 1}" />
							</c:otherwise>
						</c:choose>
						<tr style="display: none; background-color: #FFFACD;"
							class="${evalRound}">
							<td class='alignRht' style="padding-right: 20px;"><c:choose>
									<c:when
										test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
										<c:choose>
											<c:when
												test="${evalutor.externalEvaluatorName eq 'internal'}">
												<span>${evalutor.evaluatorFirstName}</span>
											</c:when>
											<c:otherwise>
												<span>${evalutor.externalEvaluatorName} (via
													${evalutor.evaluatorFirstName})</span>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
									Evaluator #${count.count}									
								</c:otherwise>
								</c:choose></td>
							<c:set var="roundTotal" value="0" />
							<c:set var="scoreNotNull" value="false" />
							<c:forEach var="i" begin="1" end="${score}">
								<c:set var="currScore" value="${i}" />
								<c:set value="true" var="isPresent" />
								<%--If Evaluator started evaluate proposal--%>
								<c:forEach var="scoreList" items="${evaluatorScoreList}"
									varStatus="count">
									<c:choose>
										<c:when
											test="${evalutor.evaluatorDisplayId eq scoreList.evaluatorDisplayId 
													   && scoreList.versionNumber eq currVersion 
													   && scoreList.scoreSeqNum eq currScore
													   && currVersion > 1}">
											<c:set var="arrow" value="" />
											<c:if test="${scoreList.scoreChangeType eq '1'}">
												<c:set var="arrow" value="arrow-up" />
											</c:if>
											<c:if test="${scoreList.scoreChangeType eq '-1'}">
												<c:set var="arrow" value="arrow-down" />
											</c:if>
											<td class='alignCenter'><c:choose>
													<c:when
														test="${currVersion eq summMaxVersion && scoreList.statusId eq 42 && not empty currIterateDate}">-<c:set
															var="roundTotal" value="${roundTotal + 0}" />
													</c:when>
													<c:otherwise>${scoreList.score}<c:set
															var="scoreNotNull" value="true" />
														<c:if test="${not empty currIterateDate}">
															<c:if test="${not empty arrow}">
																<div class="${arrow}"></div>
															</c:if>
														</c:if>
														<c:set var="roundTotal"
															value="${roundTotal + scoreList.score}" />
													</c:otherwise>
												</c:choose></td>
											<c:set value="false" var="isPresent" />
										</c:when>
										<c:when
											test="${evalutor.evaluatorDisplayId eq scoreList.evaluatorDisplayId 
													   && scoreList.versionNumber eq currVersion 
													   && scoreList.scoreSeqNum eq currScore}">
											<td class='alignCenter'><c:if
													test="${scoreList.score ne null}">${scoreList.score}<c:set
														var="scoreNotNull" value="true" />
													<c:set var="roundTotal"
														value="${roundTotal + scoreList.score}" />
												</c:if> <c:if test="${scoreList.score eq null}">-<c:set
														var="roundTotal" value="${roundTotal + 0}" />
												</c:if></td>
											<c:set value="false" var="isPresent" />
										</c:when>
									</c:choose>
								</c:forEach>
								<%--If Evaluator haven't started evaluate proposal --%>
								<c:if test="${isPresent}">
									<td class='alignCenter'>-</td>
								</c:if>
							</c:forEach>
							<td class='alignCenter'><c:choose>
									<c:when
										test="${roundTotal eq 0 && summMaxVersion eq currVersion && scoreNotNull eq 'false'}">-</c:when>
									<c:otherwise>${roundTotal}</c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
					<%--End Iterate All Evaluators --%>
				</c:forEach>
				<%--R5 : End Iterate Round--%>
			</tbody>
		</table>
	</div>
	<%-- Evaluation Summary End --%>

	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display: block">
			${message}<img src="../framework/skins/hhsa/images/iconClose.jpg"
				id="box" class="message-close" onclick="showMe('messagediv', this)">
		</div>
	</c:if>
	<%--  Evaluation Criteria Starts --%>
	<div class='clear'>&nbsp;</div>
	<a class="floatRht displayScoreCriteria" href="javascript:void(0);"
		onclick="showScoreCriteria();">Display Score Criteria
		<div id="container1">
			<div class="top1"></div>
			<div class="bottom1"></div>
		</div>
		<div id="container2" style="display: none;">
			<div class="top2"></div>
			<div class="bottom2"></div>
		</div>
	</a>
	<div id="evalCriteriaDiv" style="display: none;">
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
					<c:forEach var="evalCriteria" items="${evalCriteriaList}"
						varStatus="counter">
						<tr class="${counter.count%2==0?'oddRows':'evenRows' }">
							<%--Added Score and points per enhancement 5415 --%>
							<td>Score ${counter.count}</td>
							<td>${evalCriteria.scoreCriteria}</td>
							<td>${evalCriteria.maximumScore} points</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<%--  Evaluation Criteria End --%>

	<p class='clear'></p>
	<h2>View Comments</h2>
	<div class='hr'></div>
	<%-- Evaluator's Comments Starts --%>
	<div id="roundOption">
		Select Iteration: <select onchange="onChangeRound(this);">
			<c:forEach begin="1" end="${versionNumber}" varStatus="loop">
				<c:set var="currDropdownVer" value="${loop.end - loop.count + 1}" />
				<c:forEach var="scoreList" items="${evaluatorScoreList}"
					varStatus="count">
					<c:if test="${scoreList.versionNumber eq currDropdownVer}">
						<c:set var="currIterateDate"
							value="${scoreList.submissionCloseDate}" />
						<c:set var="count.index" value="${TotalEvalScore}" />
					</c:if>
				</c:forEach>
				<option value="${currDropdownVer}">Iteration
					${currDropdownVer} - ${currIterateDate}</option>
			</c:forEach>
		</select>

	</div>

	<c:set var="maxValue" value="${versionNumber + 1}" />

	<c:forEach begin="1" end="${maxValue}" varStatus="loop">
		<c:set var="currAccordianVer" value="${loop.end - loop.count + 1}" />
		<c:set var="isShow" value="" />
		<c:if test="${versionNumber ne currAccordianVer}">
			<c:set var="isShow" value="display:none;" />
		</c:if>
		<c:choose>
			<c:when
				test="${maxValue eq currAccordianVer && not empty evalCommentsList}">
				<div id="totalRoundAccordian${currAccordianVer}" style="${isShow}">
					<c:set var="evalCount" value="1" />
					<c:forEach var="evalutor" items="${loEvaluatorsList}"
						varStatus="counter">
						<c:set var="evalStatusId" value="0" />
						<c:forEach var="scoreList" items="${evaluatorScoreList}"
							varStatus="scoreCount">
							<c:if
								test="${evalutor.evaluatorDisplayId eq scoreList.evaluatorDisplayId}">
								<c:set var="evalStatusId"
									value="${scoreList.evaluationStatusId}" />
								<c:set var="scoreCount.index" value="${TotalEvalScore}" />
							</c:if>
						</c:forEach>
						<div id="accordionTopId${currAccordianVer}_${counter.count}">
							<div class="accrodinWrapper hdng"
								id="accordionHeaderId${currAccordianVer}_${counter.count}"
								onclick="displayAccordion(this);window.scrollTo(0,document.body.scrollHeight);if(divEmpty('budgetSummary${currAccordianVer}_${counter.count}')){showScoreDetails('summaryscoredetails','budgetSummary${currAccordianVer}_${counter.count}','${evalStatusId}','${currAccordianVer}');}">
								<h5>
									<c:choose>
										<c:when
											test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
											<c:choose>
												<c:when
													test="${evalutor.externalEvaluatorName eq 'internal'}">
											             Score Details: ${evalutor.evaluatorFirstName} &nbsp; ${evalutor.evaluatorSecondName}
											         </c:when>
												<c:otherwise>
														Score Details: ${evalutor.externalEvaluatorName}(via ${evalutor.evaluatorFirstName})
													</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
												       Score Details: Evaluator #${evalCount}
												       <c:set var="evalCount" value="${evalCount+1}" />
										</c:otherwise>
									</c:choose>
								</h5>
							</div>
							<div id="accordianId2${currAccordianVer}_${counter.count}"
								class="close">
								<div id='tabs-container${currAccordianVer}_${counter.count}'
									class="clearHeight">
									<div class="accContainer" style="width: 100%"
										id="budgetSummary${currAccordianVer}_${counter.count}">
									</div>
								</div>
							</div>
						</div>
						<p class='clear'></p>
					</c:forEach>
					<c:set var="accoComment" value="" />
					<c:choose>
						<c:when
							test="${not empty loAccoCommentsAndTitle[currAccordianVer-2].INTERNAL_COMMENT}">
							<c:set var="accoComment"
								value="${loAccoCommentsAndTitle[currAccordianVer-2].INTERNAL_COMMENT}" />
						</c:when>
						<c:otherwise>
							<c:set var="accoComment"
								value="${loAccoCommentsAndTitle[currAccordianVer-3].INTERNAL_COMMENT}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${not empty accoComment}">
						<div id="accordionTopIdAccoComments${currAccordianVer}">
							<div class="accrodinWrapper hdng"
								id="accordionHeaderIdAccoComments${currAccordianVer}"
								onclick="displayAccordion(this);window.scrollTo(0,document.body.scrollHeight);">
								<h5>ACCO Score Comments</h5>
							</div>
							<div id="accordianIdAccoComments${currAccordianVer}"
								class="close">
								<div id='tabs-container' class="clearHeight">
									<div class="accContainer wordWrap"
										id="AccoCommentsDiv${currAccordianVer}">
										<p align="justify">${accoComment}</p>
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div id="totalRoundAccordian${currAccordianVer}" style="${isShow}">
					<c:if test="${not empty evalCommentsList}">
						<c:forEach var="evalComments" items="${evalCommentsList}"
							varStatus="counter">
							<c:set var="showScoreDetailAccordian" value="false" />
							<c:forEach var="scoreList" items="${evaluatorScoreList}"
								varStatus="scoreCount">
								<c:choose>
									<c:when test="${currAccordianVer eq 1}">
										<c:if
											test="${evalComments.evaluationStatusId eq scoreList.evaluationStatusId}">
											<c:set var="showScoreDetailAccordian" value="true" />
											<c:set var="scoreCount.index" value="${TotalEvalScore}" />
										</c:if>
									</c:when>
									<c:when
										test="${currAccordianVer eq scoreList.versionNumber 
												&& evalComments.evaluationStatusId eq scoreList.evaluationStatusId 
												&& scoreList.returnFlag eq 1}">
										<c:if
											test="${maxValue - 1 eq currAccordianVer && scoreList.statusId ne 42}">
											<c:set var="showScoreDetailAccordian" value="true" />
											<c:set var="scoreCount.index" value="${TotalEvalScore}" />
										</c:if>
										<c:if test="${maxValue - 1 ne currAccordianVer}">
											<c:set var="showScoreDetailAccordian" value="true" />
											<c:set var="scoreCount.index" value="${TotalEvalScore}" />
										</c:if>
									</c:when>
								</c:choose>
							</c:forEach>
							<c:if test="${showScoreDetailAccordian}">
								<div id="accordionTopId${currAccordianVer}_${counter.count}">
									<div class="accrodinWrapper hdng"
										id="accordionHeaderId${currAccordianVer}_${counter.count}"
										onclick="displayAccordion(this);if(divEmpty('budgetSummary${currAccordianVer}_${counter.count}')){showScoreDetails('summaryscoredetails','budgetSummary${currAccordianVer}_${counter.count}','${evalComments.evaluationStatusId}','${currAccordianVer}');}">
										<h5>
											<c:choose>
												<c:when
													test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
													<c:choose>
														<c:when
															test="${evalComments.externalEvaluatorName eq null or evalComments.externalEvaluatorName eq ''}">
											             Score Details: ${evalComments.evaluatorFirstName} &nbsp; ${evalComments.evaluatorSecondName}
											         </c:when>
														<c:otherwise>
														Score Details: ${evalComments.externalEvaluatorName}(via ${evalComments.evaluatorFirstName} &nbsp;${evalComments.evaluatorSecondName})
													</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
												<c:set var="key" value="${evalComments.evaluatorFirstName}${evalComments.evaluatorSecondName}${empty evalComments.externalEvaluatorName ? 'internal' : evalComments.externalEvaluatorName}"/>
												<!-- Added for Emergency Build 4.0.1 defect 8361 -->
												Score Details: Evaluator #${evaluatorMap[fn:replace(key,' ', '')]}
												</c:otherwise>
											</c:choose>
										</h5>
									</div>
									<div id="accordianId2${currAccordianVer}_${counter.count}"
										class="close">
										<div id='tabs-container${currAccordianVer}_${counter.count}'
											class="clearHeight">
											<div class="accContainer" style="width: 100%"
												id="budgetSummary${currAccordianVer}_${counter.count}">
											</div>
										</div>
									</div>
								</div>
								<p class='clear'></p>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${empty evalCommentsList}">
						<c:forEach var="evalutor" items="${loEvaluatorsList}"
							varStatus="counter">
							<div id="accordionTopIdAccoComment${counter.count}"
								style="display: none;">
								<div class="accrodinWrapper hdng"
									id="accordionHeaderIdAccoComment${counter.count}"
									onclick="displayAccordion(this);window.scrollTo(0,document.body.scrollHeight);">
									<h5>
										<c:choose>
											<c:when
												test="${(role eq 'ACCO_MANAGER') or (role eq 'ACCO_ADMIN_STAFF') or (role eq 'ACCO_STAFF')}">
												<c:choose>
													<c:when
														test="${evalutor.externalEvaluatorName eq 'internal'}">
														Score Details: ${evalutor.evaluatorFirstName} &nbsp; ${evalutor.evaluatorSecondName}
													</c:when>
													<c:otherwise>
														Score Details: ${evalutor.externalEvaluatorName} (via ${evalutor.evaluatorFirstName})
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												Score Details: Evaluator #${counter.count}
											</c:otherwise>
										</c:choose>
									</h5>
								</div>
								<div id="accordianIdAccoComments${counter.count}" class="close">
									<div id='tabs-container' class="clearHeight">
										<div class="accContainer wordWrap"
											id="AccoCommentsDiv${counter.count}">
											<p align="justify">Evaluate Proposal Task has not been
												completed</p>
										</div>
									</div>
								</div>
							</div>
							<p class='clear'></p>
						</c:forEach>
					</c:if>
					<%-- Evaluator's Comments Ends --%>

					<%-- ACCO Comments Start--%>
					<c:choose>
						<c:when test="${currAccordianVer eq maxValue}">
							<c:set var="accoComment" value="" />
							<c:choose>
								<c:when
									test="${not empty loAccoCommentsAndTitle[currAccordianVer-2].INTERNAL_COMMENT}">
									<c:set var="accoComment"
										value="${loAccoCommentsAndTitle[currAccordianVer-2].INTERNAL_COMMENT}" />
								</c:when>
								<c:otherwise>
									<c:set var="accoComment"
										value="${loAccoCommentsAndTitle[currAccordianVer-3].INTERNAL_COMMENT}" />
								</c:otherwise>
							</c:choose>
							<c:if test="${not empty accoComment}">
								<div id="accordionTopIdAccoComments${currAccordianVer}">
									<div class="accrodinWrapper hdng"
										id="accordionHeaderIdAccoComments${currAccordianVer}"
										onclick="displayAccordion(this);window.scrollTo(0,document.body.scrollHeight);">
										<h5>ACCO Score Comments</h5>
									</div>
									<div id="accordianIdAccoComments${currAccordianVer}"
										class="close">
										<div id='tabs-container' class="clearHeight">
											<div class="accContainer wordWrap"
												id="AccoCommentsDiv${currAccordianVer}">
												<p align="justify">${accoComment}</p>
											</div>
										</div>
									</div>
								</div>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if
								test="${not empty loAccoCommentsAndTitle[currAccordianVer-1].INTERNAL_COMMENT}">
								<div id="accordionTopIdAccoComments${currAccordianVer}">
									<div class="accrodinWrapper hdng"
										id="accordionHeaderIdAccoComments${currAccordianVer}"
										onclick="displayAccordion(this);window.scrollTo(0,document.body.scrollHeight);">
										<h5>ACCO Score Comments</h5>
									</div>
									<div id="accordianIdAccoComments${currAccordianVer}"
										class="close">
										<div id='tabs-container' class="clearHeight">
											<div class="accContainer wordWrap"
												id="AccoCommentsDiv${currAccordianVer}">
												<p align="justify">${loAccoCommentsAndTitle[currAccordianVer-1].INTERNAL_COMMENT}</p>
											</div>
										</div>
									</div>
								</div>
							</c:if>
						</c:otherwise>
					</c:choose>
					<%-- ACCO Comments End--%>
				</div>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<br>
	<br>

</form:form>
<%-- Form Tag Ends--%>
<div class="overlay"></div>
<%-- Overlay Popup for Help Starts --%>
<div class="alert-box-help">
	<div class="tabularCustomHead toplevelheaderHelp"></div>
	<div id="helpPageDiv"></div>
	<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<!-- release 5 changes for Evaluation Ends -->