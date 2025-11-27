<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ page errorPage="/error/errorpage.jsp" %> 
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationCriteria.js"></script>
<style>
h2{width:82%}
.alert-box {
	background:#FFF;
	display: none;
	position: fixed;
	margin-left: 8%;
	top: 25%;
	width: 60%;
	z-index: 1001;
}
.formcontainer .row span.formfield {
	padding:3px 0
}
 .formcontainer .row span.error{
	padding:0 !important;
	background:transparent;
	line-height:auto;
	font-size: 0px;
}
	.formcontainer .row span.error label.error{
		font-size: 12px;
	}
</style>

<portlet:actionURL var="evaluationCriteria" escapeXml="false">
	<portlet:param name="action" value="rfpRelease" />
	<portlet:param name="submit_action" value="processEvaluationCriteria"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
</portlet:actionURL>


<input type="hidden" name="procurementStatus" id="procurementStatus"  value="${procurementStatus}"/>
<nav:navigationSM screenName="EvaluationCriteria">
<form:form id="evaluationCriteriaform" name="evaluationCriteriaform" action="${evaluationCriteria}" method ="post" commandName="RFPReleaseBean">


<div class='clear'></div>
<input type="hidden" name="procurementId" value="${procurementId}"/>

<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="${topLevelFromRequest}"/>
<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="${midLevelFromRequest}"/>

<div id='tabs-container' class='clearHeight'>
<h2>Evaluation Criteria</h2>
<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement or isReadOnly) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Evaluation Criteria" name="screenName"/>
		</d:content>
<div id="totalScoreMsg" class="failedShow totalScoreMsg" style="display:none;">
 The sum of the evaluation criteria must total 100. Please adjust your scores</div>
<form:errors path="totalMaxScore"  cssClass="failedShow " element="div"></form:errors>
<div class='hr'></div>
	<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S252_UNPUBLISHED_INFORMATION%></c:set>
	<d:content section="${sectionUnpublishInformation}">
		<c:if test="${unPublishedDataMsg ne null}">
			<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
		</c:if>
	</d:content>
	
<c:if test="${message ne null}">
	<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
</c:if>
<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
	<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
</c:if>
<div id="maxScoreErrMsg" class="error"></div>
	<p>Indicate the evaluation criteria and corresponding maximum scores for this Procurement.
		<div>The total of all scores must equal 100. Any criteria that are not selected will not be displayed.</div>
	</p>
	<%-- Form Data Starts --%>
	<c:set var="listSize" value=""></c:set>
	<c:set var="sizeCheck" value="false"></c:set>
	<div class='formcontainer'>
		<div class='row'>
			<c:forEach var="i" begin="0" end="9">
				<c:set var="checked" value=""></c:set>
				<c:set var="checkedValue" value=""></c:set>
				<c:choose>
				<c:when test="${RFPReleaseBean ne null}">
					<c:choose>
						<c:when test="${
						(RFPReleaseBean.loEvaluationCriteriaBeanList[i].scoreFlag=='1' or fn:length(RFPReleaseBean.loEvaluationCriteriaBeanList) == 0
						or sizeCheck )}">
							<c:set var="checked" value="checked"></c:set>
							<c:set var="checkedValue" value="1"></c:set>
							<c:set var="sizeCheck" value="true"></c:set>
							<form:hidden
								path="loEvaluationCriteriaBeanList[${i}].scoreSeqNumber"
								value="${i+1}" id="scoreSeqNumber${i}" />
								
						</c:when>
						<c:otherwise>
						<c:set var="sizeCheck" value="false"></c:set>
							<form:hidden
								path="loEvaluationCriteriaBeanList[${i}].scoreSeqNumber"
								value="0" id="scoreSeqNumber${i}" />
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
						<form:hidden
							path="loEvaluationCriteriaBeanList[${i}].scoreSeqNumber"
							value="${i + 1}" id="scoreSeqNumber${i}" />
				</c:otherwise>
					
				</c:choose>
   					<div class='row'>
						<span class='label'>Score ${i+1} Criteria and Maximum Score:</span>
						<span class='formfield  proposalConfigFormfield'> 
							<form:checkbox
							path="loEvaluationCriteriaBeanList[${i}].scoreFlag"
							checked='${checked}' id="scoreFlag${i}" value="${checkedValue}"
							class="chechBoxClickAction proposalRecStatus"
							${checked} onclick="enableTextBox(this,'${i}')" /> 
							<form:input
							path="loEvaluationCriteriaBeanList[${i}].scoreCriteria"
							class='evaluationTxtBox proposalRecStatus' id="scoreCriteria${i}" maxlength="90" /> 
							<form:input
							path="loEvaluationCriteriaBeanList[${i}].maximumScore"
							id="maximumScore${i}" class='evaluationTxtBoxSm proposalRecStatus' validate="number"
							maxlength="3" /> 
						 </span>
						<span class="error" style='margin-left:36%'><label class="error" id="questionFlagError${i}"></label></span>
						<span class="error floatRht"><label class="error" id="scoreFlagError${i}"></label></span>
					</div>
			</c:forEach>
			<form:hidden path="totalMaxScore" id="totalMaxScore"/>
			<form:hidden path="nullCheck" id="nullCheck"/>	
		</div>
	</div>

<div class="buttonholder clearHeight">
<div class='floatLft'>
		<comSM:commonSolicitation screenName="EvaluationCriteria" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementStatus}"></comSM:commonSolicitation>
		</div>
		<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S252_SAVE_BUTTON%></c:set>
		<d:content section="${sectionUnpublishInformation}">
			<c:if test="${(procurementStatus eq '2' or procurementStatus eq '3') and (not isReadOnly)}">
				<input type="button" value="Save" id="save" class="proposalRecStatus"/>
			</c:if>
		</d:content>
</div>
</d:content>
</div>
</form:form>
</nav:navigationSM>
<div class="overlay"></div>
