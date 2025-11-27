<%--
This JSP file is used for Program Income Tab on Contract Budge Invoicing Page
--%>
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

<H3>Program Income</H3>
<c:if test="${param.entryTypeId eq '10'}">
<div class="formcontainer paymentFormWrapper widthFull">
	<div class="row">
	      <span class="label" >Indirect Rate - Program:<br><p style="margin: -2% 0% -2% 0%">(City Funded Budget + Program Income)</p></span>
	      <span class="formfield">
	      	<span class='indirectRate${subBudgetId}' id="indirectPIRate${subBudgetId}">${piIndirectPercentage}</span><span>%</span>
	      </span>
	</div>
</div>
<div class='clear'>&nbsp;</div>
</c:if> 
<div></div>
<%-- Added in R7 for new Program income grid UI --%>
<c:if test="${(oldPIFlag eq 0) and (param.entryTypeId eq null)}">
&nbsp;
<div>Please note that all changes to Program Income must be made in the grids located in the corresponding budget category tab</div>
&nbsp;
</c:if>
<%-- R7 changes end--%>

<%--getting Sub-Grid header  --%>
<portlet:resourceURL var='programIncomeInvoiceSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<%-- Added in R7 for new Program income grid UI --%>
<c:choose>
<c:when test="${(oldPIFlag eq 0) and !(param.entryTypeId eq null)}">
<portlet:param name="gridLabel" value="categoryProgramIncomeInvoice.grid"/>
</c:when>
<c:when test="${oldPIFlag eq 0}">
<portlet:param name="gridLabel" value="programIncomeInvoiceNew.grid"/>
</c:when>
<c:otherwise>
<portlet:param name="gridLabel" value="programIncomeInvoice.grid"/>
</c:otherwise>
</c:choose>
<%-- R7 changes end --%>
</portlet:resourceURL>
<%-- Code for Security matrix and readonly value and the last row for Other --%> 
<c:choose>
<%-- For showing PI grid in other budget categories--%>
<c:when test="${(oldPIFlag eq 0) and !(param.entryTypeId eq null)}">
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:set var="lastRowEditAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%-- Following if added for PS detail read-only --%>
<c:if test="${param.subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<c:set var="PIexportFileName" value=""></c:set>
<c:set var="isPaginationValue" value="true"></c:set>
<c:set var="rowsPerPageValue" value="5"></c:set>
</c:when>
<%-- For showing new PI tab--%>
<c:when test="${oldPIFlag eq 0}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:set var="rowsPerPageValue" value="10"></c:set>
<c:set var="lastRowEditAttribute" value="false"></c:set>
<c:set var="PIexportFileName" value="PROGRAM_INCOME_"></c:set>
<c:set var="isPaginationValue" value="true"></c:set>
</c:when>
<%-- For showing old PI tab--%>
<c:otherwise>
<c:set var="lastRowEditAttribute" value="true"></c:set>
<c:set var="rowsPerPageValue" value="7"></c:set>
<c:set var="PIexportFileName" value=""></c:set>
<c:set var="isPaginationValue" value="false"></c:set>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
<c:set var="lastRowEditAttribute" value="false"></c:set>
</c:if>
</c:otherwise>
</c:choose>

<%-- Added in R7 for new Program income grid UI --%>
<c:choose>
<%-- For showing PI grid in other budget categories--%>
<c:when test="${(oldPIFlag eq 0) and !(param.entryTypeId eq null)}">
<c:set var="gridColNames"><%=HHSUtil.getHeader("categoryProgramIncomeInvoice.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("categoryProgramIncomeInvoice.grid")%></c:set>
<c:set var="subGridColProp" value= "{editable:false, editrules:{isMandatoryField},edittype : 'select'},
                  {editable:false, editrules:{required:false},editoptions:{maxlength:30}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{required:true,number:true,allowBothSignCurrencyValue}}" > 
</c:set>
</c:when>
<%-- For showing new PI tab--%>
<c:when test="${oldPIFlag eq 0}">
<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncomeInvoiceNew.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncomeInvoiceNew.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncomeInvoiceNew.grid")%></c:set>
</c:when>
<%-- For showing old PI tab--%>
<c:otherwise>
<c:set var="gridColNames"><%=HHSUtil.getHeader("programIncomeInvoice.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("programIncomeInvoice.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("programIncomeInvoice.grid")%></c:set>
</c:otherwise>
</c:choose>

<%-- loading the page  --%>
<portlet:resourceURL var='loadBudgetProgramIncomeInvoice' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="programIncomeInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
<%-- Added in R7 for new Program income grid UI --%>
<c:choose>
<c:when test="${(oldPIFlag eq 0) and !(param.entryTypeId eq null)}">
<portlet:param name="gridLabel" value="categoryProgramIncomeInvoice.grid"/>
</c:when>
<c:when test="${oldPIFlag eq 0}">
<portlet:param name="gridLabel" value="programIncomeInvoiceNew.grid"/>
</c:when>
<c:otherwise>
<portlet:param name="gridLabel" value="programIncomeInvoice.grid"/>
</c:otherwise>
</c:choose>
<portlet:param name="entryTypeId" value="${param.entryTypeId}"/>
<portlet:param name="PIEntryTypeId" value="${param.entryTypeId}"/>
<%-- R7 changes end --%>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='programIncomeInvoiceOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="programIncomeInvoiceGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBProgramIncomeBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
<%-- Added in R7 --%>
<portlet:param name="entryTypeId" value="${param.entryTypeId}"/>
<portlet:param name="PIEntryTypeId" value="${param.entryTypeId}"/>
</portlet:resourceURL>

<%-- JQ Grid Starts--%>
<jq:grid id="programIncomeInvoiceGrid-${subBudgetId}-${param.entryTypeId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${programIncomeInvoiceSubGridHeaderRow}"
		 subGridUrl="${loadBudgetProgramIncomeInvoice}"
	     cellUrl="${programIncomeInvoiceOperationGrid}"
	     editUrl="${programIncomeInvoiceOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="${isPaginationValue}" 
	     rowsPerPage="${rowsPerPageValue}"
         isSubGrid="true"          
         negativeCurrency="income"
         exportFileName="${PIexportFileName}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true"
/>
<%-- JQ Grid Ends--%>
<c:if test="${param.entryTypeId eq null}">
<div>
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
	entityTypeTabLevel="TLC_programIncome_${subBudgetId}"
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
	entityTypeTabLevel="TLC_programIncome_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 ends--%>
</div>
</c:if>