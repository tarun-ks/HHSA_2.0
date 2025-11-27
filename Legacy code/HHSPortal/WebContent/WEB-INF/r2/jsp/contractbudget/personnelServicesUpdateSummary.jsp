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
This jsp is used for Personnel Services update summary shown in Contract Budget update module.
 --%>
 <%-- Start: Update in Defect-8470--%>
<script type="text/javascript">
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
					for(var i=clickOnGridArr.length-1; i>= 0;i--){
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
</script>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<%-- Non-Grid data Starts --%>
<h3>Personnel Services - Summary</h3>

<div class="buttonholder">
   	<input type="button" class="graybtutton tabChange"  value="Summary View" jspname='personnelServicesUpdateSummary' id="summaryView${subBudgetId}"/>
   	<input type="button" class="blackLock tabChange"  value="Detail View" jspname='personnelServicesUpdateDetail' id="detailedView${subBudgetId}"/>
</div>
<%-- End: Update in Defect-8470--%>
<div class="formcontainer paymentFormWrapper widthFull">
	<div class="row">
	     <span class="label">Update City Salary &amp; Fringe:</span>
	     <span class="formfield">
	     	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
	     </span>
	</div>
	 <div class="row">
	     <span class="label">Update City Salary:</span>
	     <span class="formfield">
	     	<span class='lftAmount'><label id="val2${subBudgetId}">${personnelServiceData.totalSalaryAmount}</label></span>
	      </span>
	    </div>  
	<div class="row">
	  <span class="label">Update City Fringe:</span>
	  <span class="formfield">
	  	<span class='lftAmount'><label id="val3${subBudgetId}">${personnelServiceData.totalFringeAmount}</label></span>
	 	<span class='rhtAmount' id="val5${subBudgetId}">${personnelServiceData.fringePercentage}</span>
	  	</span>
	</div>
	<div class="row">
	  <span class="label">YTD Invoiced Amount:</span>
	  <span class="formfield">
	  	<span class='lftAmount'><label id="val4${subBudgetId}">${personnelServiceData.totalYtdInvoicedAmount}</label></span>
	  </span>  
	</div>
</div>
<%-- Non-Grid data Ends --%>

<div class='clear'>&nbsp;</div>

<%-- Salaried Employee Grid Starts --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="updationBudgetSalariedEmployeeSummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="updationBudgetSalariedEmployeeSummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%-- Added parameter existingBudget for Defect-8478 --%>
<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="existingBudget" value="0"/>
</portlet:resourceURL>
<c:set var="gridColNames"><%=HHSUtil.getHeader("updationBudgetSalariedEmployeeSummary.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("updationBudgetSalariedEmployeeSummary.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("updationBudgetSalariedEmployeeSummary.grid")%></c:set>

<%-- Added callbackFnAfterLoadGrid attribute for Defect-8485 --%>
<jq:grid id="salariedEmployeeGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
		 gridColProp="${gridColProp}"          
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMin:'-99999.99',vMax:'99999.99'});}, 100);}}},
                  {editable:true, editrules:{required:true,number:true,allowBothSignCurrencyValue}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,modificationAmount"	     
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="budgetAmount,remainingAmount,proposedBudgetAmount" 
         negativeCurrency="modificationAmount"                
   	     operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_SUMMARY_SALARIED_"
   	     callbackFnAfterLoadGrid="updateJsonForExport(parentTotal,'proposedBudgetAmount','budgetAmount:modificationAmount');"
/>
<%-- Salaried Employee Grid Ends --%>

<%-- Hourly Employee Grid Starts --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="updationBudgetHourlyEmployeeSummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData1' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="updationBudgetHourlyEmployeeSummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<%-- Added parameter existingBudget for Defect-8478 --%>
<portlet:resourceURL var='hourlyEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
<portlet:param name="existingBudget" value="0"/>
</portlet:resourceURL> 

<c:set var="gridColNames1"><%=HHSUtil.getHeader("updationBudgetHourlyEmployeeSummary.grid")%></c:set> 
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("updationBudgetHourlyEmployeeSummary.grid")%></c:set> 
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("updationBudgetHourlyEmployeeSummary.grid")%></c:set> 
<br>
 
<%-- Added callbackFnAfterLoadGrid attribute for Defect-8485 --%>
<jq:grid id="hourlyEmployeeGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{isMandatoryField},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMin:'-99999.99',vMax:'99999.99'});}, 100);}}},
                  {editable:true, editrules:{required:true,number:true,allowBothSignCurrencyValue}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadGridData1}"
	     cellUrl="${hourlyEmployeeOperationGrid}"
	     editUrl="${hourlyEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,modificationAmount"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="budgetAmount,remainingAmount,proposedBudgetAmount"
         negativeCurrency="modificationAmount"                 
   	     operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_SUMMARY_HOURLY_"
   	     callbackFnAfterLoadGrid="updateJsonForExport(parentTotal,'proposedBudgetAmount','budgetAmount:modificationAmount');"
/>
<%-- Hourly Employee Grid Ends --%>

<%-- Fringe Benefits Grid Starts --%>

<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="updationBudgetFringeBenifitsSummary.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData3' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenifitsGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="updationBudgetFringeBenifitsSummary.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='fringeBenifitsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenifitsGridModification"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames3"><%=HHSUtil.getHeader("updationBudgetFringeBenifitsSummary.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("updationBudgetFringeBenifitsSummary.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("updationBudgetFringeBenifitsSummary.grid")%></c:set> 
<br>

<jq:grid id="fringeBenifitsGrid-${subBudgetId}_" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="'Fringe Benefits','Approved FY Budget','Remaining Amount','Rate','Update Amount','Proposed Budget' " 
	     gridColProp="{name:'fringeBenifits',width:'330'},{name:'budgetAmount',template:currencyTemplate},{name:'remainingAmount',template:currencyTemplate},{name:''},{name:'modificationAmount',template:currencyTemplate},{name:'proposedBudgetAmount',template:currencyTemplate} " 
	     subGridColProp="{editable:false,editrules:{required:true}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{classes:'cellColor'},{editable:true,editrules:{required:true,number:true,allowBothSignCurrencyValue}},{editable:false,editrules:{required:true,number:true}}" 
		 gridUrl="${loadGridData3}"
		 subGridUrl=""
	     cellUrl="${fringeBenifitsOperationGrid}"
	     editUrl="${fringeBenifitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,modificationAmount"
         isPagination="false"
	     rowsPerPage="5"
         isSubGrid="false" 
         nonEditColumnName="budgetAmount,remainingAmount,proposedBudgetAmount"
         negativeCurrency="modificationAmount"           
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     callbackFnAfterLoadGrid="refreshFringGridHeader('${subBudgetId}');"
/>
<%-- Fringe Benefits Grid Ends --%>

<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeUpdate.jsp">
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
			//$('#summaryView'+subBudgetID).removeClass().addClass('button');
			$('#summaryView'+subBudgetID).attr('disabled',true);
			//Start: Added in Defect-8470
			$('#summaryView'+subBudgetID).removeAttr('style');
			$('#detailedView'+subBudgetID).attr('disabled',false);
			//End: Added in Defect-8470
			$('#detailedView'+subBudgetID).attr('style','background-color: white !important;color: grey !important;');
		});

</script>
