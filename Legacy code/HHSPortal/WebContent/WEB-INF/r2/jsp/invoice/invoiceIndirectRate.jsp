<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />
<%-- 
This jsp is used for S406 Indirect Rate.
--%>

<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.*"%>

<c:set var="readOnlyStatusIndirectRate">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_ACTIVE"/>,
<fmt:message key="BUDGET_CANCELLED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>,
<fmt:message key="BUDGET_CLOSED"/>
</c:set>
<input type = 'hidden' value='${subBudgetId}' id='subBudgetId'/>
<%-- Below resource url is for indirect rate grid headers --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceIndirectRate.grid"/>
</portlet:resourceURL>
<%-- Below resource url is for fetching/onload of invoice indiirect rate--%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="indirectInvoicingGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
<portlet:param name="gridLabel" value="invoiceIndirectRate.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- Below resource url is for operations on indirect rate grid --%>
<portlet:resourceURL var='indirectRateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="indirectInvoicingGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBIndirectRateBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceIndirectRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceIndirectRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceIndirectRate.grid")%></c:set>


<h3>Indirect Rate</h3>

<div class="formcontainer paymentFormWrapper widthFull">
	<div class="row">
	      <span class="label">Indirect Rate - City Funded:</span>
	      <span class="formfield">
	      	<span id='indirectRate${subBudgetId}'>${indirectPercentage}</span><span>%</span>
	      </span>
	</div>
</div>

	<div class='clear'>&nbsp;</div>
	
<jq:grid id="indirectRateGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${indirectRateOperationGrid}"
	     editUrl="${indirectRateOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="true" 
         nonEditColumnName="indirectInvoiceAmount" 
         checkForTotalValue="indirectInvoiceAmount,indirectRemainingAmount,invoiceAmountEnteredCheck" 
         negativeCurrency="indirectInvoiceAmount"
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>
<%-- R7 changes --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">

<%-- Added in R7 for Program income grid in budget categories --%>
<div>&nbsp;</div>
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="10" name="entryTypeId" />
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
	entityTypeTabLevel="TLC_indirectRate_${subBudgetId}"
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
	entityTypeTabLevel="TLC_indirectRate_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
	<%--code updation for R4 ends--%>