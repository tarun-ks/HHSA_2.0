<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%-- This jsp is an accordian jsp showing score details corresponding to an evaluator --%>
<style>
.darkGray{
background-color: #EDEDED;
}
.lightGray{
background-color: #F8F8F8;
}
</style>

<portlet:defineObjects />

<div class="" style="border:0px;"  >
<div>
<% pageContext.setAttribute("newLineChar", "\r\n"); %> 
<c:if test="${null eq evaluationScoreDetailsList}">
<div id="transactionStatusDiv" class="failed" style="display: block"><fmt:message key='PROP_ERRORNEOUS_DATA'/></div>
</c:if>
<c:if test="${null ne evaluationScoreDetailsList}">
<div style="border-top: 1px solid #e4e4e4;border-left: 1px solid #e4e4e4;border-right: 1px solid #e4e4e4; margin-bottom:5px;"  >
<c:forEach var="scoreData" items="${evaluationScoreDetailsList}" varStatus="scoreCounter">
<table width="100%" ><tr >
<td width="82%" class="lightGray" style="border-right:1px solid #e4e4e4; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>Criteria ${scoreCounter.count}:<label>&nbsp;${scoreData.scoreCriteria}</label></label></h2></td>
<td width="18%" class="darkGray"><h2><label class='floatRht'>Score: ${scoreData.evaluationScore}/${scoreData.maximumScore}</label></h2></td>
</tr>
<tr>
<td style="border-top:1px solid #e4e4e4;border-bottom:1px solid #e4e4e4; word-wrap:break-word;padding: 4px" colspan="2">${fn:replace(scoreData.comments, newLineChar, "<br/>")}</td>
</tr>
</table>
</c:forEach>
<table width="100%" ><tr >
<td width="80%"  class="lightGray" style="border-right:1px solid #e4e4e4; word-wrap:break-word;padding-left:5px"><h2><label class='floatLft'>General Comments</label></h2></td>
<td width="20%" class="darkGray"><h2><label class='floatRht'>Total Score: ${evaluationScoreDetailsList[0].score}</label></h2></td>
</tr>
<tr>
<td style="border-top:1px solid #e4e4e4;border-bottom:1px solid #e4e4e4; word-wrap:break-word;padding: 4px" colspan="2">${fn:replace(evaluationScoreDetailsList[0].generalComments, newLineChar, "<br/>")}</td>
</tr>
</table>
</div>
</c:if>
</div>
</div>
