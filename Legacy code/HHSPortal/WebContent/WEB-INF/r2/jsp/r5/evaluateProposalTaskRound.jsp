<%-- Added in R5: This jsp displays score details corresponding to an evaluator in Evaluate Proposal Task Screen--%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%-- This jsp is a pop up Jsp for showing score details corresponding to an evaluator --%>
<style>
.darkGray{
background-color: #EDEDED;
}
.lightGray{
background-color: #F8F8F8;
}
</style>

<portlet:defineObjects />

<c:set var="totalScore" value="0"></c:set>
<c:forEach var="scoreData" items="${evaluationScoreDetailsList}" varStatus="scoreCounter">
	<c:set var="totalScore" value="${totalScore + scoreData.evaluationScore}"></c:set>
	<c:set var="scoreCssClass" value="" />
	<c:if test="${scoreData.scoreChangeType eq 1 || scoreData.scoreChangeType eq -1}">
		<c:set var="scoreCssClass" value="isModifiedDarkGray" />
	</c:if>
	
	<c:set var="commentsCssClass" value="" />
	<c:if test="${scoreData.commentChangeFlag eq 1}">
		<c:set var="commentsCssClass" value="isModifiedDarkGray" />
	</c:if>
	<table width="100%" class="evalScoreTable${scoreData.evaluationStatusId}">
		<tr>
			<td width="82%" class="lightGray" style="/* border-right:1px solid #e4e4e4; */ word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>Criteria ${scoreCounter.count}:<label>&nbsp;${scoreData.scoreCriteria}</label></label></h2></td>
			<td width="18%" class="darkGray ${scoreCssClass}"><h2><label class='floatRht'>Score: ${scoreData.evaluationScore}/${scoreData.maximumScore}</label></h2></td>
		</tr>
		<tr class="${commentsCssClass}">
			<td style="border-top:1px solid #e4e4e4;border-bottom:1px solid #e4e4e4; word-wrap:break-word;padding: 4px" colspan="2" >${fn:replace(scoreData.comments, newLineChar, "<br/>")}</td>
		</tr>
	</table>
</c:forEach>


<table width="100%" id="${evaluationScoreDetailsList[0].versionNumber}_evalScoreGenTable_${evaluationScoreDetailsList[0].evaluationStatusId}">
		<c:set var="genScoreCssClass" value="" />
		<c:if test="${evaluationScoreDetailsList[0].generalScoreChangeType eq 1}">
			<c:set var="genScoreCssClass" value="isModifiedDarkGray" />
		</c:if>
		
		<c:set var="genCommentsCssClass" value="" />
		<c:if test="${evaluationScoreDetailsList[0].generalCommentChangeFlag eq 1}">
			<c:set var="genCommentsCssClass" value="isModifiedDarkGray" />
		</c:if>
	<tr>
		<td width="80%"  class="lightGray" style="border-right:1px solid #e4e4e4; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>General Comments</label></h2></td>
		<td width="20%" class="darkGray ${genScoreCssClass}"><h2><label class='floatRht'>Total Score: ${evaluationScoreDetailsList[0].score}</label></h2></td>
	</tr>
<tr class="${genCommentsCssClass}">
<td style="border-top:1px solid #e4e4e4;border-bottom:1px solid #e4e4e4; word-wrap:break-word;padding: 4px" colspan="2" >${fn:replace(evaluationScoreDetailsList[0].generalComments, newLineChar, "<br/>")}</td>
</tr>
</table>
