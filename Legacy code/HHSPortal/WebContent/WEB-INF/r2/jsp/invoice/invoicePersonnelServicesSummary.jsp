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
<script type="text/javascript">
$(document).ready(function(){
	var $subBudgetId = '${subBudgetId}';
	refreshNonGridData($subBudgetId);
	//Start: Added in Defect-8470
	$(".tabChange").unbind("click").click(function(event) {
		var idToSearch = '-'+$(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val()+'_';
		var validateFlag = false;
		for(var i=0; i< clickOnGridArr.length;i++ ){
		if(clickOnGridArr[i].indexOf(idToSearch) > 0){
			validateFlag = true;
			break;
		}
	}
	if(validateFlag){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						for(var i=clickOnGridArr.length-1; i>= 0;i-- ){
						if(clickOnGridArr[i].indexOf(idToSearch)!=-1){
							clickOnGridArr.splice(i,1);
						}
					}
						showPsSCreen($(event.target).attr("jspname"), $(event.target).closest(".accContainer").find("#hdnGridDivId").val(), $(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val(),$(event.target).closest(".accContainer").find("#hdnGridParentSubBudgetId").val());
						$(this).dialog("close");
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			return false;
		}
		else{
			showPsSCreen($(event.target).attr("jspname"), $(event.target).closest(".accContainer").find("#hdnGridDivId").val(), $(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val(),$(event.target).closest(".accContainer").find("#hdnGridParentSubBudgetId").val());	
		}
	});
	//End: Added in Defect-8470
});
</script>
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%-- Container Starts --%>
<div class="container">
<div class='complianceWrapper'>
<%-- Form Data Starts --%>
<h3>Personnel Services - Summary</h3>
<%--Start: Added in Defect-8470 --%>
<div class="buttonholder">
   	<input type="button" class="graybtutton tabChange"  value="Summary View" jspname='invoicePersonnelServicesSummary' id="summaryView${subBudgetId}"/>
	<input type="button" class="blackLock tabChange"  value="Detail View" jspname='invoicePersonnelServicesDetail' id="detailedView${subBudgetId}"/>
</div>
<%--End: Added in Defect-8470 --%>
<div class="formcontainer paymentFormWrapper widthFull">
 <div class="row">
      <span class="label">Invoice City Salary &amp; Fringe:</span>
      <span class="formfield">
      	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
      </span>
 </div>
  <div class="row">
      <span class="label">Invoice City Salary:</span>
      <span class="formfield">
      	<span class='lftAmount'><label id="val2${subBudgetId}" >${personnelServiceData.totalSalaryAmount}</label></span>
      </span>
    </div>  
<div class="row">
  <span class="label">Invoice City Fringe:</span>
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
<portlet:param name="gridLabel" value="invoiceSalariedEmployeeSummary.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on invoiceSalariedEmployee grid mapping --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="gridLabel" value="invoiceSalariedEmployeeSummary.grid"/>
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
<c:set var="gridColNames"><%=HHSUtil.getHeader("invoiceSalariedEmployeeSummary.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("invoiceSalariedEmployeeSummary.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("invoiceSalariedEmployeeSummary.grid")%></c:set>

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
	     rowsPerPage="10"
         isSubGrid="true"
         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     callbackFnAfterLoadGrid="formatPositionHeader('table_salariedEmployeeGridInvoiceGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(2)');"
   	     exportFileName="PS_SUMMARY_SALARIED_" 	     
/>
<%--</d:content> --%>
<%-- This portlet resource is for fetch hourlyEmployeeGridInvoice grid mapping --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceHourlyEmployeeSummary.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on hourlyEmployeeGridInvoice grid mapping --%>
<portlet:resourceURL var='loadGridData1' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="gridLabel" value="invoiceHourlyEmployeeSummary.grid"/>
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

<c:set var="gridColNames1"><%=HHSUtil.getHeader("invoiceHourlyEmployeeSummary.grid")%></c:set> 
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("invoiceHourlyEmployeeSummary.grid")%></c:set> 
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("invoiceHourlyEmployeeSummary.grid")%></c:set> 
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
	     rowsPerPage="10"
         isSubGrid="true"
         checkForTotalValue="invoicedAmount,remainingAmount,invoiceAmountEnteredCheck" 
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     negativeCurrency="invoicedAmount"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     callbackFnAfterLoadGrid="formatPositionHeader('table_hourlyEmployeeGridInvoiceGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(2)');"
   	     exportFileName="PS_SUMMARY_HOURLY_" 	     
/>

<%-- This portlet resource is for fetch fringeBenefitsGridInvoice mapping --%>
<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="invoiceFringeBenefitsSummary.grid"/>
</portlet:resourceURL>
<%-- This portlet resource is for operation on fringeBenefitsGridInvoice--%>
<portlet:resourceURL var='loadGridData3' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenefitsGridInvoice"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="invoiceFringeBenefitsSummary.grid"/>
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

<c:set var="gridColNames3"><%=HHSUtil.getHeader("invoiceFringeBenefitsSummary.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("invoiceFringeBenefitsSummary.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("invoiceFringeBenefitsSummary.grid")%></c:set> 
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
   	     callbackFnAfterLoadGrid="refreshFringGridHeader('${subBudgetId}');" 	     
/>
</div>
</div>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
<jsp:include page="programIncomeInvoice.jsp">
	<jsp:param value="1" name="entryTypeId" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>
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
			$('#summaryView'+subBudgetID).attr('disabled',true);
			$('#detailedView'+subBudgetID).attr('style','background-color: white !important;color: grey !important;');
		});

</script>
