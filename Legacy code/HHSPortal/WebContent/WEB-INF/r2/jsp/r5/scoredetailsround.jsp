<%-- Added in R5 : This jsp displays score details corresponding to an evaluator in ReviewScore screen--%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<style>
.darkGray{
background-color: #EDEDED;
}
.lightGray{
background-color: #F8F8F8;
}
</style>

<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r5/js/scoredetailsround.js"></script>
<script type="text/javascript">
	function hideNotRequiredRound(round){
		<c:forEach var="iterationVar" items="${iteration}">
				$('#evaluationRoundDetail_'+'${iterationVar.versionNumber}').hide();
				$('#totalScore_'+'${iterationVar.versionNumber}').hide();
		</c:forEach>
		$('#evaluationRoundDetail_'+round).show();
		$('#totalScore_'+round).show();
	}
</script>
<div class="content">
<div id="newTabs">
<% pageContext.setAttribute("newLineChar", "\r\n"); %> 
<div class='tabularCustomHead'><span id="contractTypeId">Score Details for ${evaluatorName}</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
<div class="tabularContainer" style="border:0px;"  >
<c:if test="${null eq evaluationScoreDetailsList}">
<div id="transactionStatusDiv" class="failed" style="display: block"><fmt:message key='PROP_ERRORNEOUS_DATA'/></div>
</c:if>
<c:set var="genScoreCssClass" value="" />
<table width="100%"><tbody><tr>
<th width="75%" style="word-wrap:break-word;font-size:14px; line-height:21px;"><h2><label class='floatLft'>Score Details:&nbsp;<label>${evaluatorName}</label></label></h2></th>
<th width="25%" style="font-size:14px; line-height:21px;"><h2>
<c:if test="${evaluationScoreDetailsList[0].generalScoreChangeType eq 1}">
	<c:set var="genScoreCssClass" value="isModifiedDarkGray" />
</c:if>
<c:if test="${not empty iteration}">
	<c:forEach var="iterationVar" items="${iteration}">
		<c:choose>
		<c:when test="${iterationVar.versionNumber eq lastRound}">
			<label class='floatRht ${genScoreCssClass}' id="totalScore_${iterationVar.versionNumber}">Total Score: ${evaluationScoreDetailsList[0].score}</label>
		</c:when>
		<c:otherwise>
			<label class='floatRht' id="totalScore_${iterationVar.versionNumber}" style="display: none;"></label>
		</c:otherwise>
		</c:choose>
	</c:forEach>
</c:if>
<c:if test="${empty iteration}">
	<label class='floatRht' id="">Total Score: ${evaluationScoreDetailsList[0].score}</label>
</c:if>
</h2></th>
</tr>
<tr>
	<td>
	<c:if test="${not empty iteration}">
		<select  onchange="onChangeRound(this, '${evaluationStatusId}');">
		<c:forEach var="iterationVar" items="${iteration}" varStatus="scoreCounter">
			        <option value="${iterationVar.versionNumber}">${iterationVar.roundInfo}</option>
		</c:forEach>
		</select>
	</c:if>
	</td>
	<td class='clear'>&nbsp;</td>
</tr>
</tbody>
</table>
<br>
<c:if test="${null ne evaluationScoreDetailsList}">
<div style="padding-right:10px;height:500px;overflow-y:scroll;">
<div style="border-top: 1px solid #9E9E9E;border-left: 1px solid #9E9E9E;border-right: 1px solid #9E9E9E; margin-bottom:5px; width:795px;"  >
<c:if test="${not empty iteration}">
<c:forEach var="iterationVar" items="${iteration}">
	<c:choose>
	<c:when test="${iterationVar.versionNumber eq lastRound}">
		<div id="evaluationRoundDetail_${iterationVar.versionNumber}">
			<c:forEach var="scoreData" items="${evaluationScoreDetailsList}" varStatus="scoreCounter">
				<c:set var="scoreCssClass" value="" />
				<c:if test="${scoreData.scoreChangeType eq 1 || scoreData.scoreChangeType eq -1}">
					<c:set var="scoreCssClass" value="isModifiedDarkGray" />
				</c:if>
				
				<c:set var="commentsCssClass" value="" />
				<c:if test="${scoreData.commentChangeFlag eq 1}">
					<c:set var="commentsCssClass" value="isModifiedDarkGray" />
				</c:if>
				<table width="100%" >
					<tr>
						<td width="80%" class="lightGray" style="border-right:1px solid #9E9E9E; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>Criteria ${scoreData.scoreSeqNum}:&nbsp;<label>${scoreData.scoreCriteria}</label></label></h2></td>
						<td width="20%" class="darkGray ${scoreCssClass}"><h2><label class='floatRht'>Score: ${scoreData.evaluationScore}/${scoreData.maximumScore}</label></h2></td>
					</tr>
					<tr class ="${commentsCssClass}">
						<td style="border-top:1px solid #9E9E9E;border-bottom:1px solid #9E9E9E; word-wrap:break-word;padding: 4px" colspan="2" >${fn:replace(scoreData.comments, newLineChar, "<br/>")}</td>
					</tr>
				</table>
			</c:forEach>
				<table width="100%">
					<c:set var="genCommentsCssClass" value="" />
					<c:if test="${evaluationScoreDetailsList[0].generalCommentChangeFlag eq 1}">
						<c:set var="genCommentsCssClass" value="isModifiedDarkGray" />
					</c:if>
					<tr>
						<td width="78%"  class="lightGray" style="border-right:1px solid #9E9E9E; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>General Comments</label></h2></td>
						<td width="22%" class="darkGray ${genScoreCssClass}"><h2><label class='floatRht'>Total Score: ${evaluationScoreDetailsList[0].score}</label></h2></td>
					</tr>
					<tr class="${genCommentsCssClass}">
						<td style="border-top:1px solid #9E9E9E;border-bottom:1px solid #9E9E9E; word-wrap:break-word;padding: 4px" colspan="2" >${fn:replace(evaluationScoreDetailsList[0].generalComments, newLineChar, "<br/>")}</td>
					</tr>
				</table>
		</div>
	</c:when>
	<c:otherwise>
		<div id="evaluationRoundDetail_${iterationVar.versionNumber}" style="display: none;"></div>
	</c:otherwise>
	</c:choose>
</c:forEach>
</c:if>

<c:if test="${empty iteration}">
	<div>
			<c:forEach var="scoreData" items="${evaluationScoreDetailsList}" varStatus="scoreCounter">
				<table width="100%" >
					<tr>
						<td width="80%" class="lightGray" style="border-right:1px solid #9E9E9E; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>Criteria ${scoreData.scoreSeqNum}:&nbsp;<label>${scoreData.scoreCriteria}</label></label></h2></td>
						<td width="20%" class="darkGray"><h2><label class='floatRht'>Score: ${scoreData.evaluationScore}/${scoreData.maximumScore}</label></h2></td>
					</tr>
					<tr>
						<td style="border-top:1px solid #9E9E9E;border-bottom:1px solid #9E9E9E; word-wrap:break-word;padding: 4px" colspan="2">${fn:replace(scoreData.comments, newLineChar, "<br/>")}</td>
					</tr>
				</table>
			</c:forEach>
				<table width="100%">
					<tr>
						<td width="78%"  class="lightGray" style="border-right:1px solid #9E9E9E; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>General Comments</label></h2></td>
						<td width="22%" class="darkGray"><h2><label class='floatRht'>Total Score: ${evaluationScoreDetailsList[0].score}</label></h2></td>
					</tr>
					<tr>
						<td style="border-top:1px solid #9E9E9E;border-bottom:1px solid #9E9E9E; word-wrap:break-word;padding: 4px" colspan="2"">${fn:replace(evaluationScoreDetailsList[0].generalComments, newLineChar, "<br/>")}</td>
					</tr>
				</table>
		</div>
</c:if>
</div>
<br>
</div>
</c:if>
</div>
</div>
</div>
