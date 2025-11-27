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

<H3 class='autoWidth'>OTPS - Contracted Services</H3>
<div class='formcontainer paymentFormWrapper widthFull'>
	<div class="row">
	      <span class="label">Total Contracted Services:</span>
	      <span class="formfield">
	      	<span class='lftAmount'><label id="totCS${subBudgetId}">${contractedDisplay.totalContractedServices}</label></span>
	      </span>
	</div>
	<div class="row">
	      <span class="label">YTD Invoiced Amount:</span>
	      <span class="formfield">
	      	<span class='lftAmount'><label id="ytdIA${subBudgetId}">${contractedDisplay.ytdTotalInvoiceAmt}</label></span>
	      </span>
	</div>
</div>

<div class='clear'>&nbsp;</div>

<portlet:actionURL var="addProcurementUrl" escapeXml="false">
<portlet:param name="submitAction" value="addProcurement"/>
</portlet:actionURL>

<DIV></DIV>

<portlet:resourceURL var='contractServicesSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesConsultants.grid"/>
</portlet:resourceURL>


<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>
<%--Consultants Grid Load--%>
<portlet:resourceURL var='contractedServicesConsultantsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesConsultants.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesConsultants.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesConsultants.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesConsultants.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedConsultants' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesConsultants"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesConsultants.grid"/>
<portlet:param name="subHeader" value="1"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%--Consultants Grid Load Ends--%>

<%--JQGrid Mapping for Consultants Grid Operations--%>
<portlet:resourceURL var='contractedServicesConsultantsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServices"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="1"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for Consultants Grid Operations Ends--%>

<jq:grid id="consultantsGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${contractServicesSubGridHeaderRow}"
		 subGridUrl="${loadBudgetContractedConsultants}"
	     cellUrl="${contractedServicesConsultantsOperationGrid}"
	     editUrl="${contractedServicesConsultantsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoiceAmt"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
         positiveCurrency="fyBudget"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
	     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
/>
<%--Contracted Services Consultants Grid Ends--%>
<br>
<%--SubContractors Grid Load--%>
<portlet:resourceURL var='contractedServicesSubContractorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesSubContractors.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesSubContractors.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesSubContractors.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesSubContractors.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedSubContractors' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesSubContractors"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesSubContractors.grid"/>
<portlet:param name="subHeader" value="2"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%--SubContractors Grid Load Ends--%>

<%--JQGrid Mapping for SubContractors Grid Operations--%>
<portlet:resourceURL var='contractedServicesSubContractorsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServices"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="2"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for SubContractors Grid Operations Ends--%>
<%--Contracted Services Sub-Contractors Grid----%>
<jq:grid id="subContractorsGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${contractedServicesSubContractorsSubGridHeaderRow}"
		 subGridUrl="${loadBudgetContractedSubContractors}"
	     cellUrl="${contractedServicesSubContractorsOperationGrid}"
	     editUrl="${contractedServicesSubContractorsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoiceAmt"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
         positiveCurrency="fyBudget"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
	     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
/>
<%--Contracted Services Sub-Contractors Grid Ends----%>

<br>
<%--Vendors Grid Load--%>
<portlet:resourceURL var='contractedServicesVendorsSubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractedServicesVendors.grid"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractedServicesVendors.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractedServicesVendors.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractedServicesVendors.grid")%></c:set>

<portlet:resourceURL var='loadBudgetContractedVendors' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServicesVendors"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="gridLabel" value="contractedServicesVendors.grid"/>
<portlet:param name="subHeader" value="3"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--Vendors Grid Load Ends--%>
</portlet:resourceURL>
<%--Vendors Grid Load Ends--%>

<%--JQGrid Mapping for Vendors Grid Operations--%>
<portlet:resourceURL var='contractedServicesVendorsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="getContractedServices"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.ContractedServicesBean"/>
<portlet:param name="subHeader" value="3"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%--JQGrid Mapping for Vendors Grid Operations Ends--%>
<%--Contracted Services Vendors Grid----%>
<jq:grid id="vendorsGrid-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${contractedServicesVendorsSubGridHeaderRow}"
		 subGridUrl="${loadBudgetContractedVendors}"
	     cellUrl="${contractedServicesVendorsOperationGrid}"
	     editUrl="${contractedServicesVendorsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="fyBudget,-ytdInvoiceAmt"
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         nonEditColumnName="ytdInvoiceAmt,remaingAmt"
         positiveCurrency="fyBudget"
	     operations="del:true,edit:true,add:true,cancel:true,save:true"
	     callbackFunction="refreshNonGridContractedServicesData('${subBudgetId}');"
/>
<%--Contracted Services Vendors Grid Ends--%>
<div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncome.jsp">
	<jsp:param value="6" name="entryTypeId" />
</jsp:include>
</c:if>
<%--R7 changes end --%>

	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.BUDGET_TYPE3%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_REVIEW%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_contractedServices_${subBudgetId}"
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
	entityTypeTabLevel="TLC_contractedServices_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>	
</div>
	<%--code updation for R4 starts--%>
</div>
<script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#totCS"+subBudgetID).jqGridCurrency();
			$("#ytdIA"+subBudgetID).jqGridCurrency();
		});

</script>
