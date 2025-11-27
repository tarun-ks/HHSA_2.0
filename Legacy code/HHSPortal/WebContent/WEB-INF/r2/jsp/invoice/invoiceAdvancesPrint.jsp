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

<portlet:resourceURL var='AdvanceSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceAdvances.grid"/>
</portlet:resourceURL>

<portlet:resourceURL var='InvoiceAdvanceGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceAdvancesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AdvanceSummaryBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<portlet:resourceURL var='loadInvoiceAdvances' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceAdvancesGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.AdvanceSummaryBean"/>
<portlet:param name="gridLabel" value="invoiceAdvances.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceAdvances.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceAdvances.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceAdvances.grid")%></c:set>

<jq:grid id="advance-${subBudgetId}" 
         isReadOnly="true"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${AdvanceSubGridHeaderRow}"
		 subGridUrl="${loadInvoiceAdvances}"
	     cellUrl="${InvoiceAdvanceGrid}"
	     editUrl="${InvoiceAdvanceGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="500"
         isSubGrid="true"
         nonEditColumnName="" 
         positiveCurrency="invoiceRecoupedAmt"
	     operations="del:false,edit:false,add:false,cancel:false,save:false"
	     isExpandOnLoad="true"
/>