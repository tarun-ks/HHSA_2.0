<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />

<%-- This JSP is for Screen S338 Contract Invoicing Milestone grid. --%>

<H3>Milestone</H3>

<div></div>

<portlet:resourceURL var='MilestoneSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractInvoiceMilestone.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractInvoiceMilestone.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractInvoiceMilestone.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractInvoiceMilestone.grid")%></c:set>

<%-- Below portlet:resourceURL is for Grid load data --%>
<portlet:resourceURL var='loadInvoiceMilestone' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractInvoiceMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="gridLabel" value="contractInvoiceMilestone.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- Below portlet:resourceURL is for Grid Edit, Add and Delete operations on data --%>
<portlet:resourceURL var='milestoneInvoiceOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractInvoiceMilestoneGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBMileStoneBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>


<jq:grid id="milestoneInvoiceGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${MilestoneSubGridHeaderRow}"
		 subGridUrl="${loadInvoiceMilestone}"
	     cellUrl="${milestoneInvoiceOperationGrid}"
	     editUrl="${milestoneInvoiceOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="mileStone,remainAmt"
         checkForTotalValue="invoiceAmount,remainAmt,invoiceAmountEnteredCheck"
         negativeCurrency="invoiceAmount"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="8" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>

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
	entityTypeTabLevel="TLC_milestone_${subBudgetId}"
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
	entityTypeTabLevel="TLC_milestone_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 ends--%>