<%-- This jsp is a pop up Jsp for showing Evaluator's progress details corresponding to an Competition Pool --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<portlet:defineObjects />
<p class="evalsummary">
The below table displays the current status of all evaluations for each proposal. A proposal/evaluator without
a checkmark indicates that a task is waiting in the evaluator's inbox to be completed.
</p>
<c:set var="totalEvaluators" value="0"></c:set>
<c:set var="tmpProposalId" value="${evaluationDetailList[0].proposalId}"></c:set>
<c:forEach var="evaluationDetailListVar" items="${evaluationDetailList}" varStatus="counter">
	<c:if test="${tmpProposalId eq evaluationDetailListVar.proposalId}">
		<c:set var="totalEvaluators" value="${totalEvaluators + 1}"></c:set>
	</c:if>
</c:forEach>
<c:set var="totalList" value="${(fn:length(evaluationDetailList))}" />

<div class="tabularWrapper" id="evalTableWrapper">
	<table id="evalTable" width="100%" cellspacing='0' cellpadding='0' border="0" >
		<thead>
			<tr>
				<th colspan="3"></th>
				<th colspan="${totalEvaluators}" style="width: 100%;">Evaluators</th>
			</tr>
			<tr>
				<th class="widthadjust nowrapping" style="width:3%;text-align:left;">Proposal ID</th>
				<th class="widthadjust nowrapping" style="width:3%;text-align:left;">Proposal Title</th>
				<th class="widthadjust nowrapping" style="width:3%;text-align:left;">Provider Name</th>
				<c:forEach var="evaluationDetailListVar" items="${evaluationDetailList}" varStatus="counter">
					<c:if test="${tmpProposalId eq evaluationDetailListVar.proposalId}">
						<c:choose>
							<c:when test="${role eq 'city_org'}">
								<th style="font-weight:200;word-break: break-word">Evaluator #${counter.count}</th>
							</c:when>
							<c:otherwise>
								<th style="font-weight:200;word-break: break-word">${evaluationDetailListVar.evaluatorName}</th>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach begin="0" end="${totalList-1}" step="${totalEvaluators}" varStatus="loop" >
			   <tr class="${loop.count%2==0?'oddRows':'evenRows' }">
					<td style="width:3%;text-align:left;">${evaluationDetailList[loop.index].proposalId}</td>
					<td style="width:3%;text-align:left;">${evaluationDetailList[loop.index].proposalTitle}</td>
					<td style="width:3%;text-align:left;">${evaluationDetailList[loop.index].providerName}</td>
					<c:set var="evalColumnIndex" value="${loop.index}"></c:set>
					<c:forEach begin="${loop.index}" end="${loop.index + totalEvaluators - 1}" varStatus="iLoop">
						<c:set var="cellValue" value=""></c:set>
						<c:choose>
							<c:when test="${evaluationDetailList[evalColumnIndex].evaluationCompletedStatus eq 1}">
								<c:set var="cellValue">&#10004;</c:set>
							</c:when>
							<c:otherwise>
								<c:set var="cellValue">&nbsp;</c:set>
							</c:otherwise>
						</c:choose> 
						<td>${cellValue}</td>
						<c:set var="evalColumnIndex" value="${evalColumnIndex+1}"></c:set>
					</c:forEach>
				</tr>
			</c:forEach> 
		</tbody>
	</table>
</div>