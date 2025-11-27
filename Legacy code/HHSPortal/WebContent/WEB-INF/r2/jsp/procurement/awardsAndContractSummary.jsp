<%-- This jsp file has been added in R4--%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects/>
<portlet:actionURL var="awardsandContractsSummaryURL" escapeXml="false">
	<portlet:param name="action" value="awardContract" />
	<portlet:param name="topLevelFromRequest" value="AwardsandContracts"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="render_action" value="awardsAndContracts"/>
	<portlet:param name="procurementId" value="${procurementBean.procurementId}" />
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}" />
</portlet:actionURL>
<nav:navigationSM screenName="AwardsandContractsSummary">
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/awardsandContractsSummary.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
	<form id="awardsandContractsSummaryForm" name="awardsandContractsSummaryForm" action="${awardsandContractsSummaryURL}" method ="post" commandName="AwardsContractSummaryBean">
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementBean.procurementId}"/>
		<c:if test="${procurementBean.isOpenEndedRFP eq '1' and fn:length(evaluationGroupList) > 1}">
			<select id="evalGroupDropDown" class="floatRht" >
				<option value="-1">Select Evaluation Group</option>
				<c:forEach var="entry" items="${evaluationGroupList}">
					<option value="${entry['EVALUATION_GROUP_ID']}">${entry['EVALUATION_GROUP_TITLE']}</option>
	            </c:forEach>
			</select>
		</c:if>
		<h2>Awards and Contracts Summary
		<c:if test="${procurementBean.isOpenEndedRFP eq '1'}">
			<a id="returnEvaluationGroupsAward" class="floatRht returnButton" href="#">Evaluation Groups</a>
		</c:if>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Awards and Contracts Summary" name="screenName"/>
		</d:content>	
			<div class='hr'></div>
			<%-- check for fail or success message --%>
		<c:if test="${message ne null}">
			<div class="${messageType}" id="messagediv" style="display:block">${message} <img
				src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
				class="message-close" 
				onclick="showMe('messagediv', this)">
			</div>
		</c:if>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
			</c:if>
				Select the competition pool below to view the associated awards
				<br /><br />
				<c:if test="${procurementBean.isOpenEndedRFP eq '1'}">
					<div class="formcontainer">
						<div class="row">
							<span class="label">Evaluation Group:</span>
							<span class="formfield">${groupTitleMap['EVALUATION_GROUP_TITLE']}</span>
						</div>
						<div class="row">
							<span class="label">Closing Date:</span>
							<span class="formfield">${groupTitleMap['SUBMISSION_CLOSE_DATE']}</span>
						</div>
					</div>
				</c:if>
				<%-- Grid Starts --%>
				<%-- Container Starts --%>
			   <div class="tabularWrapper gridfixedHeight">
			        <st:table objectName="groupAwardContractList" displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Competition Pool" columnName="competitionPoolTitle" align="left" size="40%" sortType="competitionPoolTitle" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AwardsandContractsSummaryExtension" />
						</st:property>
						<st:property headingName="# of Awards" columnName="numberOfAwards" align="left" size="20%" sortType="numberOfAwards" sortValue="asc"/>
						<st:property headingName="Total Award Amount ($)" columnName="totalAwardAmount" align="left" size="40%" sortType="totalAwardAmount" sortValue="asc"/>
					</st:table>
				</div>
			    <%-- Grid Ends --%>
		</form>
</nav:navigationSM>