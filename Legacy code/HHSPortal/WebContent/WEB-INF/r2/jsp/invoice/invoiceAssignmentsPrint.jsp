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

<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractInvoiceAssignment.grid"/>
</portlet:resourceURL>

<portlet:resourceURL var='AssignmentOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractInvoiceAssignmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AssignmentsSummaryBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
<portlet:param name="budgetID" value="${budgetID}"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractInvoiceAssignment.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractInvoiceAssignment.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractInvoiceAssignment.grid")%></c:set>
<portlet:resourceURL var='loadBudgetAssignment' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractInvoiceAssignmentGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AssignmentsSummaryBean"/>
<portlet:param name="gridLabel" value="contractInvoiceAssignment.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
 
<jq:grid id="invAssignmentGrid-${subBudgetId}" 
         isReadOnly="true"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadBudgetAssignment}"
	     cellUrl="${AssignmentOperationGrid}"
	     editUrl="${AssignmentOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="500"
         isSubGrid="true"
         nonEditColumnName=""
         checkForZeroAndDelete="ytdAssignmentAmount,INVOICE_ASSIGNMENT_DELETE_CHECK"
         positiveCurrency="invoiceAmount"
	     operations="del:false,edit:false,add:false,cancel:false,save:false"
	     isExpandOnLoad="true"
/>