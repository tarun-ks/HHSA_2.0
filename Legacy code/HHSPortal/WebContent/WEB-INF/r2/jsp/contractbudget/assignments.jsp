<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>

<%@ page errorPage="/error/errorpage.jsp"%>
<portlet:defineObjects />
<%--This jsp is to show an assignment's grid --%>
<div></div>

<%--This portlet fetch the grid url for jqgrid's tld--%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow'
	escapeXml='false'>
	<portlet:param name="gridLabel" value="contractBudgetAssignment.grid" />
</portlet:resourceURL>

<%--set the value gridColNames for jqgrid's tld--%>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetAssignment.grid")%></c:set>

<%--set the value gridColProp for jqgrid's tld--%>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetAssignment.grid")%></c:set>

<%--set the value subGridColProp for jqgrid's tld--%>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetAssignment.grid")%></c:set>

<%--This portlet fetch the sub-grid url for jqgrid's tld--%>
<portlet:resourceURL var='loadBudgetRate' id='loadGridData'
	escapeXml='false'>
	<portlet:param name="transactionName"
		value="getContractBudgetAssignmentGrid" />
	<portlet:param name="beanName"
		value="com.nyc.hhs.model.AssignmentsSummaryBean" />
	<portlet:param name="gridLabel" value="contractBudgetAssignment.grid" />
	<portlet:param name="subBudgetId" value="${subBudgetId}" />
</portlet:resourceURL>

<jq:grid id="assignment-${subBudgetId}" isReadOnly="true"
	gridColNames="${gridColNames}" gridColProp="${gridColProp}"
	subGridColProp="${subGridColProp}" gridUrl="${RateSubGridHeaderRow}"
	subGridUrl="${loadBudgetRate}" cellUrl="${rateOperationGrid}"
	editUrl="${rateOperationGrid}" dataType="json" methodType="POST"
	columnTotalName="" isPagination="true" rowsPerPage="5" isSubGrid="true"
	nonEditColumnName="" autoWidth="false"
	operations="del:false,edit:false,add:false,cancel:false,save:false" />

<div></div>