<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<%-- 
This jsp is used as a poc for grids with static headers.
It will serve as a reference while creating page specs jsps having grids.
 --%>
 
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<style>
h2{width:82%}
</style>
<portlet:actionURL var="addProcurementUrl" escapeXml="false">
<portlet:param name="submitAction" value="addProcurement"/>
</portlet:actionURL>

<H3>POC GRID</H3>

<DIV></DIV>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="budgetAmendment.salary.grid"/>
</portlet:resourceURL>
<c:set var="subBudgetId" value="${budgetType123}" />
<c:choose>
<c:when test="${budgetType=='budgetAmendment'}">
<c:set var="gridColNames"><%=HHSUtil.getHeader("budgetAmendment.salary.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("budgetAmendment.salary.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("budgetAmendment.salary.grid")%></c:set>
<portlet:resourceURL var='subSalaryGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salaryAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.SalaryBean"/>
<portlet:param name="gridLabel" value="budgetAmendment.salary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='salaryOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salaryAmendmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.SalaryBean"/>
</portlet:resourceURL>
</c:when>
<c:otherwise>

<c:set var="gridColNames"><%=HHSUtil.getHeader("salary.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("salary.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("salary.grid")%></c:set>
<portlet:resourceURL var='subSalaryGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salaryGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.SalaryBean"/>
<portlet:param name="gridLabel" value="salary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='salaryOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salaryGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.SalaryBean"/>
</portlet:resourceURL>
</c:otherwise>
</c:choose>

<d:content section="12901" readonlyRoles="staff" isReadOnly="true" >
<jq:grid id="funding" 
         isReadOnly="false"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${subSalaryGrid}"
	     cellUrl="${salaryOperationGrid}"
	     editUrl="${salaryOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fte"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         checkForZeroAndDelete="fte"
         nonEditColumnName="fte"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
/>
</d:content>

<div>

</div>

