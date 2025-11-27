<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:bundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />
<%-- 
This jsp is used for S331 Personnel Services.
--%>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%-- Container Starts --%>
<div class="container">
<div class='complianceWrapper'>
<%-- Form Data Starts --%>
<h3>Personnel Services</h3>
<div class="formcontainer paymentFormWrapper widthFull">
 <div class="row">
      <span class="label">Total Salary &amp; Fringe:</span>
      <span class="formfield">
      	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
      </span>
 </div>
  <div class="row">
      <span class="label">Total Salary:</span>
      <span class="formfield">
      	<span class='lftAmount'><label id="val2${subBudgetId}" >${personnelServiceData.totalSalaryAmount}</label></span>
      </span>
    </div>  
<div class="row">
  <span class="label">Total Fringe:</span>
  <span class="formfield">
  	<span class='lftAmount'><label id="val3${subBudgetId}" >${personnelServiceData.totalFringeAmount}</label></span>
  	<span class='rhtAmount' id="val5${subBudgetId}">${personnelServiceData.fringePercentage}</span>
  </span>
</div>
<div class="row">
  <span class="label">YTD Invoiced Amount:</span>
  <span class="formfield">
  	<span class='lftAmount'><label id="val4${subBudgetId}" >${personnelServiceData.totalYtdInvoicedAmount}</label></span>
  </span>  
</div>
</div>

<div class='clear'>&nbsp;</div>

<%-- This portlet resource is for fetch invoiceSalariedEmployee grid mapping --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceSalariedEmployee.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on invoiceSalariedEmployee grid mapping --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="gridLabel" value="invoiceSalariedEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is for sub grid properties invoiceSalariedEmployee for mapping --%>
<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL>
<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceSalariedEmployee.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceSalariedEmployee.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceSalariedEmployee.grid")%></c:set>
<%-- <d:content section="12901" readonlyRoles="staff" readonlyStatuses="${readOnlyStatus}"> --%>

<jq:grid id="salariedEmployeeGridInvoiceGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"   	     
/>
<%--</d:content> --%>
<%-- This portlet resource is for fetch hourlyEmployeeGridInvoice grid mapping --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceHourlyEmployee.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on hourlyEmployeeGridInvoice grid mapping --%>
<portlet:resourceURL var='loadGridData1' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="gridLabel" value="invoiceHourlyEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is for fetch sub grid data for  hourlyEmployeeGridInvoice--%>
<portlet:resourceURL var='hourlyEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames1"><%=HHSUtil.getHeader("invoiceHourlyEmployee.grid")%></c:set> 
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("invoiceHourlyEmployee.grid")%></c:set> 
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("invoiceHourlyEmployee.grid")%></c:set> 
<br>
<jq:grid id="hourlyEmployeeGridInvoiceGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="${subGridColProp1}"
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadGridData1}"
	     cellUrl="${hourlyEmployeeOperationGrid}"
	     editUrl="${hourlyEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	      negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"   	     
/>
<%-- This portlet resource is for fetch invoiceSeasonalEmployee grid mapping --%>
<portlet:resourceURL var='SubGridHeaderRow2' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceSeasonalEmployee.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on invoiceSeasonalEmployee grid mapping --%>
<portlet:resourceURL var='loadGridData2' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="seasonalEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="gridLabel" value="invoiceSeasonalEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is for fetch sub grid data of invoiceSeasonalEmployee grid mapping --%>
<portlet:resourceURL var='seasonalEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="seasonalEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames2"><%=HHSUtil.getHeader("invoiceSeasonalEmployee.grid")%></c:set> 
<c:set var="gridColProp2"><%=HHSUtil.getHeaderProp("invoiceSeasonalEmployee.grid")%></c:set> 
<c:set var="subGridColProp2"><%=HHSUtil.getSubGridProp("invoiceSeasonalEmployee.grid")%></c:set> 
<br>
<jq:grid id="seasonalEmployeeGridInvoiceGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames2}" 
	     gridColProp="${gridColProp2}" 
	     subGridColProp="${subGridColProp2}"
		 gridUrl="${SubGridHeaderRow2}"
		 subGridUrl="${loadGridData2}"
	     cellUrl="${seasonalEmployeeOperationGrid}"
	     editUrl="${seasonalEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	      negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"   	     
/>
<%-- This portlet resource is for fetch fringeBenefitsGridInvoice mapping --%>
<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceFringeBenefits.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on fringeBenefitsGridInvoice--%>
<portlet:resourceURL var='loadGridData3' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenefitsGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="invoiceFringeBenefits.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<%-- This portlet resource is for fetch subgrid fringeBenefitsGridInvoice --%>
<portlet:resourceURL var='fringeBenefitsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenefitsGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<%--fix done as a part of release 3.1.2 defect 6420--%>
<portlet:param name="invoiceId" value="${invoiceId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames3"><%=HHSUtil.getHeader("invoiceFringeBenefits.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("invoiceFringeBenefits.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("invoiceFringeBenefits.grid")%></c:set> 
<br>
<jq:grid id="fringeBenefitsGridInvoiceGrid-${subBudgetId}_" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames3}" 
	     gridColProp="${gridColProp3}" 
	     subGridColProp="${subGridColProp3}" 
		 gridUrl="${loadGridData3}"
		 subGridUrl=""
	     cellUrl="${fringeBenefitsOperationGrid}"
	     editUrl="${fringeBenefitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="false"
         checkForTotalValue="Invoice Amount,Remaining Amount,invoiceAmountEnteredCheck"
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	      negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"   	     
/>
</div>
</div>
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
	entityTypeTabLevel="TLC_personnelServices_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_personnelServices_${subBudgetId}"
	level="footer">
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
			$("#val1" + subBudgetID).jqGridCurrency();
			$("#val2" + subBudgetID).jqGridCurrency();
			$("#val3" + subBudgetID).jqGridCurrency();
			$("#val4" + subBudgetID).jqGridCurrency();
			if($('#val5'+subBudgetID).html() == 0){
				$("#val5" + subBudgetID).html('(0.00%)');
			}else{
				if($('#val5'+subBudgetID).html().indexOf('E-') !== -1 || $('#val5'+subBudgetID).html().indexOf('e-') !== -1){
					$("#val5" + subBudgetID).html('('+new Big(Math.round($('#val5'+subBudgetID).html().replaceAll('e-',0).replaceAll('E-',0) * 100) / 100).toFixed(2)+'%)');
				}else{
					$("#val5" + subBudgetID).html('('+new Big(Math.round($('#val5'+subBudgetID).html() * 100) / 100).toFixed(2)+'%)');				
				}
			}
		});

</script>
