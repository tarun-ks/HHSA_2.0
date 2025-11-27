<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>

<portlet:defineObjects />
<%--  This jsp is used to display grids for Operation And Support  and Equipment in ContractInvoice Page. --%>

<h3>OTPS - Operations and Support</h3>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null || (accessScreenEnable eq false)}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>


<div class="formcontainer paymentFormWrapper" style='width:100%'>
				<div class="row">
					  <span class="label">Invoice Total Operations, Support and Equipment : </span>
					  <span class="formfield">
					  	<span class='lftAmount'>
					  		<label id="fyBudgetOTPS${subBudgetId}">${invoiceTotalAmounts}</label>
					  	</span>
					  </span>
				</div>
				<div class="row">
					  <span class="label">Total YTD Invoiced Amount : </span>
					  <span class="formfield"> 
					  <span class='lftAmount'>
					  		<label id="ytdInvAmtOTPS${subBudgetId}">${ytdInvoicedAmount}</label>
		</span>
		</span>
				</div>
			</div>
			<%-- Form Data Ends --%>	
			
			<%-- OPERATION SUPPORT GRID STARTS --%>
			<div class='clear'>&nbsp;</div>
<%-- Resource Mapping for header row of OPeration Support Grid --%>		
<portlet:resourceURL var='InvoiceSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceOperationSupport.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceOperationSupport.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceOperationSupport.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceOperationSupport.grid")%></c:set>

<%-- Resource Mapping to load data for Operation Support Grid --%>
<portlet:resourceURL var='loadInvoiceOperationSupportGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceOperationSupportGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="gridLabel" value="invoiceOperationSupport.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<%-- Resource Mapping for OPeration_Support grid operations --%>
<portlet:resourceURL var='invoiceOperationSupportGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceOperationSupportGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBOperationSupportBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<jq:grid id="invoiceGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${InvoiceSubGridHeaderRow}"
		 subGridUrl="${loadInvoiceOperationSupportGrid}"
	     cellUrl="${invoiceOperationSupportGridActions}"
	     editUrl="${invoiceOperationSupportGridActions}"
	     dataType="json" methodType="POST" autoWidth="true"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="21"
         isSubGrid="true" 
         checkForTotalValue="invoicedAmt,remainingAmt,invoiceAmountMoreThanRemaining"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true" 
	     negativeCurrency="invoicedAmt"
	     callbackFunction="refreshNonGridDataInvoiceOTPS('${subBudgetId}');"
/>  


<p>&nbsp;</p>

<%-- Resource Mapping for  header row of Equipment Grid --%>
<portlet:resourceURL var='EquipmentSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceEquipment.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceEquipment.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceEquipment.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceEquipment.grid")%></c:set>
<%-- Resource Mapping to load data for Equipment Grid --%>
<portlet:resourceURL var='loadInvoiceEquipmentGrid' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceEquipmentDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="gridLabel" value="invoiceEquipment.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<%-- Resource Mapping for Equipment grid operations --%>
<portlet:resourceURL var='invoiceEquipmentDetailsGridActions' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="invoiceEquipmentDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBEquipmentBean"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<jq:grid id="equipmentGrid-${subBudgetId}" 
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${EquipmentSubGridHeaderRow}"
		 subGridUrl="${loadInvoiceEquipmentGrid}"
	     cellUrl="${invoiceEquipmentDetailsGridActions}"
	     editUrl="${invoiceEquipmentDetailsGridActions}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true" 
         checkForTotalValue="invoicedAmt,remainingAmt,invoiceAmountMoreThanRemaining"
         isReadOnly="${readOnlyPageAttribute}"
	     operations="del:false,edit:true,add:false,cancel:true,save:true" 
	     negativeCurrency="invoicedAmt"
	     callbackFunction="refreshNonGridDataInvoiceOTPS('${subBudgetId}');"
/>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="2" name="entryTypeId" />
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
	entityTypeTabLevel="TLC_operationAndSupport_${subBudgetId}"
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
	entityTypeTabLevel="TLC_operationAndSupport_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>	
	<%--code updation for R4 ends--%>
<script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#fyBudgetOTPS"+subBudgetID).jqGridCurrency();
			$("#ytdInvAmtOTPS" + subBudgetID).jqGridCurrency();
		});

</script>	