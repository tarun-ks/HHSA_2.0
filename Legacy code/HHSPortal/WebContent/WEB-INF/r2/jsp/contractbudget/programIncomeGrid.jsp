<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@page import="com.nyc.hhs.util.HHSUtil" %>

<%--getting Sub-Grid header  --%>
<portlet:resourceURL var='categoryProgramIncomeSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="categoryProgramIncome.grid"/>
</portlet:resourceURL>

<%-- loading the page  --%>
<portlet:resourceURL var='loadBudgetProgramIncome' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="categoryProgramIncomeGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
<portlet:param name="gridLabel" value="categoryProgramIncome.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='programIncomeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="categoryProgramIncomeGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:set var="lastRowEditAttribute" value="true"></c:set>

<c:set var="gridColNames"><%=HHSUtil.getHeader("categoryProgramIncome.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("categoryProgramIncome.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("categoryProgramIncome.grid")%></c:set>

<%-- JQ Grid Starts--%>
<jq:grid id="categoryProgramIncomeGrid-${subBudgetId}-${param.entryTypeId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${categoryProgramIncomeSubGridHeaderRow}"
		 subGridUrl="${loadBudgetProgramIncome}"
	     cellUrl="${programIncomeOperationGrid}"
	     editUrl="${programIncomeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" autoWidth="true"    
         lastRowEdit="${lastRowEditAttribute}" 
         positiveCurrency="fYBudget"        
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
/>
<%-- JQ Grid Ends--%>
