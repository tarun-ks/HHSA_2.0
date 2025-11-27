<%-- This jsp is used to display invoicing for rate tab, screen S337--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />

<H3>Rate</H3>
<%-- Below resource url is for rate grid headers --%>
<portlet:resourceURL var='RateSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceRate.grid"/>
</portlet:resourceURL>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${(accessScreenEnable eq false) or subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%-- Below resource url is for fetching/onload of invoice rate--%>
<portlet:resourceURL var='loadInvoiceRate' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceRateGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="gridLabel" value="invoiceRate.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- Below resource url is for operations on rate grid --%>
<portlet:resourceURL var='invoiceRateOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceRateGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.RateBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceRate.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceRate.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceRate.grid")%></c:set>
<jq:grid id="invoiceRateGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${RateSubGridHeaderRow}"
		 subGridUrl="${loadInvoiceRate}"
	     cellUrl="${invoiceRateOperationGrid}"
	     editUrl="${invoiceRateOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="ytdInvoiceAmt"
         checkForTotalValue="ytdInvoiceAmt,remainAmt,invoiceAmountEnteredCheck"
         negativeCurrency="ytdInvoiceAmt"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="7" name="entryTypeId" />
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
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
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
	entityTypeTabLevel="TLC_rate_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
	<%--code updation for R4 ends--%>
<div>

</div>