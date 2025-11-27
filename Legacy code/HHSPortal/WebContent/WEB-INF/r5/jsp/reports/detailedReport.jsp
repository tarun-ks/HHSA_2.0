<%@page import="com.nyc.hhs.frameworks.grid.ColumnTag"%>
<%@ taglib uri="/WEB-INF/tld/custom-birt.tld" prefix="cb"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<link rel="stylesheet" href="../css/report.css" type="text/css"></link>
	
	<script type="text/javascript">
		showHeaderSelected('report_icon');
	</script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r5/js/reports/birtReport.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>

<portlet:defineObjects />
<portlet:actionURL var="filterReportListUrl" escapeXml="false">
	<portlet:param name="submit_action" value="filterReports" />
</portlet:actionURL>

<portlet:resourceURL var="getContractNoListResourceUrl" id="getContractNoListResourceUrl" escapeXml="false">
</portlet:resourceURL>

<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>

<portlet:resourceURL var="getProcurementListResourceUrl" id="getProcurementListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${getProcurementListResourceUrl}' id='hiddenGetProcurementListResourceUrl' />	

<input type='hidden' value='${getProcurementListResourceUrl}' id='hiddenGetCompetitionListResourceUrl' />



<portlet:resourceURL var='dataGridReportPaging' id='dataGridReportPaging'
	escapeXml='false'>
</portlet:resourceURL>

<input type='hidden' value='${dataGridReportPaging}'
	id='dataGridReportPaging' />

<form:form id="birtReportForm" name="birtReportForm"
	action="${filterReportListUrl}" method="post" commandName="ReportBean"
	onkeydown="if (event.keyCode == 13) {displayFilter();}" style="min-height: 700px;" modelAttribute="ReportBean">
	
	<input type="hidden" id="procurementId" value="${procurementId}" name="procurementId" />
<c:if test="${fn:length(reportListObject) eq 0}">
<%-- Block for Error Message --%>
<c:if test="${requestReportType eq 'financials'}">
				<div class="failed" id="errorGlobalMsg" style="display: block;"><fmt:message key='REPORT_NO_DATA_ERROR_MESSAGE'/></div>	
</c:if>
<c:if test="${requestReportType eq 'procurement'}">
<div class="failed" id="errorGlobalMsg" style="display: block;"><fmt:message key='REPORT_NO_DATA_ERROR_MESSAGE_PROC'/></div>	
</c:if>
<%-- Block for Error Message Ends--%>
</c:if>
	

<div>
	<jsp:include page="reportFilter.jsp"></jsp:include>
</div>
<br/><br/>
<c:if test="${fn:length(reportListObject) ne 0}">

	<div style="position: relative;" >
		<cb:birt reportId="${loReportId}"></cb:birt>
	</div>
<div class="formcontainer paymentFormWrapper reportDataGrid">
<h2 class="reportDetailed">Detailed Report</h2>
</div>
<div class="formcontainer paymentFormWrapper reportDataGrid">
<div class="" style="padding-bottom: 10px;">
            <input type="button" class="reportButtonExport" value="EXPORT DETAILED REPORT" id="reportExportButton"/>
</div> 
</div>
<div class='clear'></div>
<br>

<%--code for budget utilization --%>
<c:if test="${loReportValue eq 'Budget Utilization'}">
	<jsp:include page="budgetUtlizationTabs.jsp"></jsp:include>
</c:if>
<%--code for budget utilization --%>

<div class='contractListDiv'
		style='min-height: 1500px !important' id="reportListDataGrid">

<jsp:include page="reportDataGrid.jsp"></jsp:include>

</div>
</c:if>
	<!-- Release 5 -->
<input type="hidden" value="${jspName}" id="jspName"/>
<input type = 'hidden' value='${getContractNoListResourceUrl}' id='getContractNoListResourceUrl'/>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<input type="hidden" value="${loReportId}" id="reportId"/>
</form:form>

<p>&nbsp;</p>