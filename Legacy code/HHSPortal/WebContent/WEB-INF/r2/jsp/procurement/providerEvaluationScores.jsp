<%-- This jsp displays the read only version of provider's scores --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<portlet:defineObjects />
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMain.css" media="all" type="text/css" />
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMainR2.css" media="all" type="text/css" /> 
<script>
$(document).ready(function(){
	$(".wlp-bighorn-header").hide();
	$(".footer").hide();
});
</script>
<%-- Code updated for R4 Starts --%>
<style>
.providerEvaluationHeader{
	height:auto;
}
.logoWrapper{
	float:none !important;
}
</style>
<%-- Code updated for R4 Ends --%>
<c:set var="score" value="${(fn:length(evalCriteriaList))}" />
<input type="hidden" value="${score}" id="evaluationLength"/>
<%-- Code updated for R4 Starts --%>
 <%-- Evaluation Score Header--%>   
  <div class="hhs_header providerEvaluationHeader">
      <table width="100%">
      <tr>
      <td width="160px;">
                    <div class='logoWrapper'></div></td>
                    <td>

	      		<div class='headerFields'>
					<div class="print-td"><b>Procurement Title: </b><label>${headerDetails.PROCUREMENT_TITLE}</label></div>
					<div class="print-td"><b>Provider Name: </b><label>${headerDetails.ORGANIZATION_LEGAL_NAME}</label></div>
					<div class="print-td"><b>Competition Pool: </b><label>${headerDetails.COMPETITION_POOL_TITLE}</label></div>
					<div class="print-td"><b>Proposal Title: </b><label>${headerDetails.PROPOSAL_TITLE}</label></div>
				</div>
			 </td>	
			</tr>	
	       </table>
          </div>
 <%-- Evaluation Score Header Ends--%>  
<%-- Code updated for R4 Starts --%> 
<h2>Evaluation Scores & Rank</h2>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
			<d:content section="${helpIconProvider}">
				<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
				<input type="hidden" id="screenName" value="View Evaluation Scores" name="screenName"/>
			</d:content> 
            <div class='hr'></div>
            <div class='clear'>&nbsp;</div>
<%-- Code updated for R4 Starts --%>
<c:if test="${headerDetails.IS_OPEN_ENDED_RFP ne '1'}">
	This proposal was ranked ${headerDetails.RANK} out of ${headerDetails.TOTAL} proposals
	<br/><br/>
</c:if>
<%-- Code updated for R4 Ends --%>

<%-- Grid Starts --%>
<div class="scoreWrapper">
<table width="100%" cellspacing='0' cellpadding='0' border="1">
<%-- applied the width to columns to adjust the ui --%>
	<tbody> 
		<tr>
			<th width="10%">Score No.</th>
			<th width="56%">Score Criteria</th>
			<th class='evaluationScore' width="18%">Proposal Score</th>
			<th class='evaluationScore' width="16%">Maximum Score</th>
		</tr>

		<%-- Starts forEach --%>
		<c:set var="evaluationTotal" value="0" />
		<c:forEach items="${evalutionBeanList}" var="evalutionBeanList">
		<%-- applied the number format to restrict the decimal place to 2 digits. --%>
			<tr>
				<td>Score ${evalutionBeanList.scoreSeqNum}</td>
				<td>${evalutionBeanList.scoreCriteria}</td>
				<td class='evaluationScore'><fmt:formatNumber minFractionDigits="2" maxFractionDigits="2"   value="${evalutionBeanList.score}" /></label>
				<c:set var="evaluationTotal"
					value="${evalutionBeanList.score + evaluationTotal}" /></td>
				<td class='evaluationScore'><label>${evalutionBeanList.maximumScore} points</label>
				</td>
			</tr>
		</c:forEach>
		<%-- Ends forEach --%>
	</tbody>
	<tfoot>
		<tr>
			<td class='borderNone'></td>
			<td class='borderNone total'><label>Total Score</label></td>
			<td><fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${evaluationTotal}" /> </td>
			<td>100</td>
		</tr>
	</tfoot>
</table>
</div>

<div class="overlay"></div>
<%-- Below div is used to launch help overlay content for all jsps --%>
<div class="alert-box-help">
	<div class="tabularCustomHead toplevelheaderHelp"></div>
	<div id="helpPageDiv"></div>
	<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
	<div id="contactDiv"></div>
</div>

<%-- Overlay Popup Ends --%>



























