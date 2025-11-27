<script type="text/javascript">
$(document).ready(function(){
	var $subBudgetId = '${subBudgetId}';
	refreshPSSummaryNonGridData($subBudgetId,'PSSummaryScreen');
	$('#summaryView'+$subBudgetId).attr('disabled',true);
	//Start: Added in Defect-8470
	$('#summaryView'+$subBudgetId).removeAttr('style');
	$('#detailedView'+$subBudgetId).attr('disabled',false);
	
	$('#detailedView'+$subBudgetId).attr('style','background-color: white !important;color: grey !important;');
	//Start: Added for 8483
	if($('#hiddenApprovedDate').val() != '' && $('#hiddenTaskStatus').val() != 'Pending Submission' 
		&& $('#hiddenTaskStatus').val() != 'Returned for Revision')
	{
		$('#detailedView' + '${subBudgetId}').removeClass().addClass('blackLock tabChange');
	}
	//End: Added for 8483
	// Updated for 8468
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
This jsp is used for Personnel Services Summary shown in Contract Budget module.
 --%>

<%-- Changes for Defect-8470 --%> 
<c:set var="readOnlyPageAttribute" value="true"></c:set>

<h3>Personnel Services - Summary </h3> 

<div class="buttonholder">
   	<input type="button" class="graybtutton tabChange" value="Summary View" jspname='personnelServicesSummary' id="summaryView${subBudgetId}"/>
	<input type="button" class="graybtutton tabChange" value="Detail View" jspname='personnelServicesDetail' id="detailedView${subBudgetId}"/>
</div>

<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label">City Salary &amp; Fringe:</span> <span class="formfield"><label id="val1${subBudgetId}"></label></span></div>
	<div class="row"><span class="label">City Salary:</span> <span class="formfield"><label id="val2${subBudgetId}"></label></span></div>
	<div class="row"><span class="label">City Fringe:</span> <span class="formfield"><label id="val3${subBudgetId}"></label><label id="val4${subBudgetId}"></label></span></div>
	<div class="row"><span class="label">YTD Invoiced Amount:</span> <span class="formfield"><label id="val5${subBudgetId}"></label></span></div>
</div>
<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label" title="">Total Positions:</span> <span class="formfield"><label id="val6${subBudgetId}"></label></span></div>
</div>
<div class='clear'>&nbsp;</div>
<div id='summaryGrids' class="widthFull">
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetSalariedSummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedGridSummaryLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetSalariedSummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetSalariedSummary.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetSalariedSummary.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetSalariedSummary.grid")%></c:set>

<jq:grid id="contractBudgetSalariedSummary-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="{editable:false, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'99999.99'});}, 100);}}},
                  {editable:false, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${salariedGridSummaryLoadData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,-invoicedAmount"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount"   
         positiveCurrency="budgetAmount" 
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_SUMMARY_SALARIED_"
   	     callbackFnAfterLoadGrid="formatPositionHeader('table_contractBudgetSalariedSummary-${subBudgetId}>tbody>tr:eq(1)>td:eq(2)');"
/>

<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetHourlySummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='hourlyGridSummaryLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetHourlySummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="gridColNames1"><%=HHSUtil.getHeader("contractBudgetHourlySummary.grid")%></c:set> 
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("contractBudgetHourlySummary.grid")%></c:set> 
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("contractBudgetHourlySummary.grid")%></c:set> 
<br>

<jq:grid id="contractBudgetHourlySummary-${subBudgetId}" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="{editable:false, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMax:'99999.99'});}, 100);}}},
                  {editable:false, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${hourlyGridSummaryLoadData}"
	     cellUrl="${hourlyEmployeeOperationGrid}"
	     editUrl="${hourlyEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,-invoicedAmount"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount" 
         positiveCurrency="budgetAmount" 
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_SUMMARY_HOURLY_"
   	     callbackFnAfterLoadGrid="formatPositionHeader('table_contractBudgetHourlySummary-${subBudgetId}>tbody>tr:eq(1)>td:eq(2)');"
/>

<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetFringeSummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='fringeGridSummaryLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenifitsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetFringeSummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>

<c:set var="gridColNames3"><%=HHSUtil.getHeader("contractBudgetFringeSummary.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("contractBudgetFringeSummary.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("contractBudgetFringeSummary.grid")%></c:set> 
<br>
<jq:grid id="contractBudgetFringeSummary-${subBudgetId}_" 
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames3}" 
	     gridColProp="${gridColProp3}" 
	     subGridColProp="${subGridColProp3}" 
		 gridUrl="${fringeGridSummaryLoadData}"
		 subGridUrl=""
	     cellUrl="${fringeBenifitsOperationGrid}"
	     editUrl="${fringeBenifitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,-invoicedAmount"
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="false"
         positiveCurrency="budgetAmount" 
		 nonEditColumnName="invoicedAmount,remainingAmount"    
   	     operations="del:false,edit:false,add:false,cancel:false,save:false"
   	     callbackFnAfterLoadGrid="refreshFringGridHeader('${subBudgetId}','table_contractBudgetFringeSummary-${subBudgetId}_>tbody>tr:eq(1)>td:eq(1)',false);"
/>
</div>
<%-- Added in R7 for Program income grid in budget categories --%>
<d:content isReadOnly="true" >
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
<jsp:include page="programIncome.jsp">
	<jsp:param value="1" name="entryTypeId" />
	<jsp:param value="true" name="subGridReadonly" />
</jsp:include>
</d:content>
&nbsp;
</c:if>
<%-- R7 changes end --%>