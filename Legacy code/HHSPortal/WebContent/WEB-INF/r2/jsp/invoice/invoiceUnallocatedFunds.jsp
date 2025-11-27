<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<%@page	import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" />
<portlet:defineObjects />

<%-- 
This jsp is used for unallocated Funds in Contract Budget for grids with static headers.
Unallocated Funds tab allows for the holding of an increase or 
decrease of a budget where the specificity is not yet defined. 
 --%>


<H3>Unallocated Funds</H3>

<DIV></DIV>

<%-- getting the header for the Grid table  --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'
	escapeXml='false'>
	<portlet:param name="gridLabel"
		value="amendment.InvoiceUnallocatedFunds.grid" />
</portlet:resourceURL>

<%-- Setting the security matrix  --%>
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:if test="${subGridReadonly ne null}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("amendment.InvoiceUnallocatedFunds.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendment.InvoiceUnallocatedFunds.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendment.InvoiceUnallocatedFunds.grid")%></c:set>

<%-- loading the page  --%>
<portlet:resourceURL var='loadGridData' id='loadGridData'
	escapeXml='false'>
	<portlet:param name="transactionName" value="invoiceUnallocatedFunds" />
	<portlet:param name="beanName"
		value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="gridLabel"
		value="amendment.InvoiceUnallocatedFunds.grid" />
	<portlet:param name="subBudgetId" value="${subBudgetId}" />
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='UnallocatedFundsOperationGrid'
	id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="invoiceUnallocatedFunds" />
	<portlet:param name="beanName"
		value="com.nyc.hhs.model.UnallocatedFunds" />
	<portlet:param name="subBudgetId" value="${subBudgetId}" />
</portlet:resourceURL>

<%-- JGrid for adding dynamic table --%>
<jq:grid id="unallocatedFundsInGrid-${subBudgetId}"
		isReadOnly="${readOnlyPageAttribute}" 
		gridColNames="${gridColNames}"
		gridColProp="${gridColProp}" 
		subGridColProp="${subGridColProp}"
		gridUrl="${SubGridHeaderRow}" 
		subGridUrl="${loadGridData}"
		cellUrl="${UnallocatedFundsOperationGrid}"
		editUrl="${UnallocatedFundsOperationGrid}"
		dataType="json"
		methodType="POST" 
		columnTotalName="" 
		isPagination="false"
		rowsPerPage="5" 
		isSubGrid="true"
		operations="del:false,edit:false,add:false,cancel:false,save:false" />

			<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_INVOICES%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_INVOICE_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_unallocatedFunds_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
<!-- Updated in R6-->
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_unallocatedFunds_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>	
	<%--code updation for R4 ends--%>