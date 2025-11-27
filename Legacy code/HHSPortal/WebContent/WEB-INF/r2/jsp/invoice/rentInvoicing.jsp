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
<%-- 
This jsp is used for S335 Rent screen.
--%>

<c:set var="readOnlyStatusRent">
<fmt:message key='BUDGET_PENDING_APPROVAL'/>,
<fmt:message key="BUDGET_APPROVED"/>,
<fmt:message key="BUDGET_ACTIVE"/>,
<fmt:message key="BUDGET_CANCELLED"/>,
<fmt:message key="BUDGET_SUSPENDED"/>,
<fmt:message key="BUDGET_CLOSED"/>
</c:set>

<h3>OTPS - Rent</h3>
<%-- The portlet to display subGridHeaderRow, with the contractInvoiceRent subGrid --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractInvoiceRent.grid"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${(accessScreenEnable eq false) or subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%-- This is the loadGridData to load the rent grid with the columns specified in contractInvoiceRent Grid --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="contractInvoiceRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="gridLabel" value="contractInvoiceRent.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- The portlet to display the operation for rent invoice screen such as edit, save cancel  --%>
<portlet:resourceURL var='RentOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="contractInvoiceRent"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.Rent"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractInvoiceRent.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractInvoiceRent.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractInvoiceRent.grid")%></c:set>

        <jq:grid id="rentInvoicingGrid-${subBudgetId}" 
       	 isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="{editable:false, editrules:{required:true},editoptions:{maxlength:50}},
                 {editable:false, editrules:{required:true},editoptions:{maxlength:50}},
                 {editable:false, editrules:{required:true},editoptions:{maxlength:50}},
                 {editable:false, editrules:{required:true},edittype : 'select',editoptions : {value : '1:Yes;0:No'}},
                 {editable:false, editrules:{required:true,number:true,allowOnlyPercentValue},editoptions:{dataInit:function(elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'100.00'});},100);}}},
                 {editable:false, editrules:{required:true,number:true}},
                 {editable:true, editrules:{required:true,number:true,allowBothSignCurrencyValue}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${RentOperationGrid}"
	     editUrl="${RentOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
	     isSubGrid="true"
	     
	     nonEditColumnName="remainingAmt"     
         checkForTotalValue="lineItemInvoiceAmt,remainingAmt,invoiceAmountEnteredCheck"
         negativeCurrency="lineItemInvoiceAmt"
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="5" name="entryTypeId" />
</jsp:include>
</c:if> 
<%-- R7 changes end --%>

<div>
	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_INVOICE_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_rent_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
<!-- Updated Comments Length for Defect 8447-->
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_rent_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 ends--%>
</div>
