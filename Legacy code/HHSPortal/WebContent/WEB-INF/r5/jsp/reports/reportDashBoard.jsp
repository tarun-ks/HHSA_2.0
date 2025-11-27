<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/tld/custom-birt.tld" prefix="cb"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

	<script type="text/javascript">
		showHeaderSelected('report_icon');
	</script>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r5/js/reports/birtReport.js"></script>
<portlet:renderURL var="exportReportData" id="exportReportData">
<portlet:param name="renderAction" value="exportReportData"/>
</portlet:renderURL>

<portlet:renderURL var="fundingSummaryReport" escapeXml="false" >
	<portlet:param name="renderAction" value="fundingSummaryReport"/>
</portlet:renderURL>

<portlet:actionURL var="filterReportListUrl" escapeXml="false">
	<portlet:param name="submit_action" value="filterReports"/>
</portlet:actionURL>

<form:form id="birtReportForm" name="birtReportForm" action="${filterReportListUrl}" method ="post" commandName="ReportBean" onkeydown="if (event.keyCode == 13) {displayFilter();}">
&nbsp;&nbsp;
	<jsp:include page="reportFilter.jsp"></jsp:include>
	<c:forEach items="${reportListOptions}" var="dashBoardReportList">
		<c:if test="${(dashBoardReportList.reportType eq requestReportType)}">
			<div id="BudgetSummary" style="position: relative;"><cb:birt reportId="${dashBoardReportList.reportId}"/></div>
			<br />
		</c:if>
		<div id ="clear"></div>
	</c:forEach>
	
</form:form>