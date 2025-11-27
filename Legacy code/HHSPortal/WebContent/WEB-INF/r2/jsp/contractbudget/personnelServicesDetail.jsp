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

<script type="text/javascript">
$(document).ready(function(){
	var $subBudgetId = '${subBudgetId}';
	$('#val8'+$subBudgetId).hide();
	refreshPSSummaryNonGridData($subBudgetId,'PSDetailScreen');
	if($('#hiddenApprovedDate').val() != '' && $('#hiddenTaskStatus').val() != 'Pending Submission' 
			&& $('#hiddenTaskStatus').val() != 'Returned for Revision')
	{
		//Start: Added for 8483
		$('#detailedView' + '${subBudgetId}').removeClass().addClass('blackLock tabChange');
		//End: Added for 8483
		$('#val8'+$subBudgetId).show();
	}
	$('#detailedView'+$subBudgetId).attr('disabled',true);
	//Start: Added in Defect-8470
	$('#detailedView'+$subBudgetId).removeAttr('style');
	$('#summaryView'+$subBudgetId).attr('disabled',false);
	
	$('#summaryView'+$subBudgetId).attr('style','background-color: white !important;color: grey !important;');
	
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
<style>
.iconPosition{
	background-position: 4px 22px !important;
	line-height: 18px;
}
</style>
<%-- 
This jsp is used for Personnel Services Summary shown in Contract Budget module.
 --%>
 <c:set var="readOnlyPageAttribute" value="false"></c:set>
<%-- Start: Updated in 8460--%>
<c:if test="${subGridReadonly ne null}">
<%-- End: Updated in 8460--%>
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<h3>Personnel Services - Detail</h3> 
<%-- Start: Updated in 8470--%>
<div class="buttonholder">
   	<input type="button" class="graybtutton tabChange" value="Summary View" jspname='personnelServicesSummary' id="summaryView${subBudgetId}"/>
	<input type="button" class="graybtutton tabChange" value="Detail View" jspname='personnelServicesDetail' id="detailedView${subBudgetId}"/>
</div>
<%-- End: Updated in 8470--%>
<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label">City Salary &amp; Fringe:</span> <span class="formfield"><label id="val1${subBudgetId}"></label></span></div>
	<div class="row"><span class="label">City Salary:</span> <span class="formfield"><label id="val2${subBudgetId}"></label></span></div>
	<div class="row"><span class="label">City Fringe:</span> <span class="formfield"><label id="val3${subBudgetId}"></label><label id="val4${subBudgetId}"></label></span></div>
</div>
<div class="formcontainer paymentFormWrapper">
	<div class="row"><span class="label" title="">Total Positions:</span> <span class="formfield"><label id="val6${subBudgetId}"></label></span></div>
	<div class="row"><span class="label" title="">Total City FTEs:</span> <span class="formfield"><label id="val7${subBudgetId}"></label></span></div>
</div>
<!-- Start: Updated for 8502 -->
<p id="val8${subBudgetId}" class="infoMessage iconPosition" style="display:block">Loading
</p>
<!-- End: Updated for 8502 -->
<div class='clear'>&nbsp;</div>
<!-- Detailed Grid -->
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetSalariedDetailed.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedGridDetailLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetSalariedDetailed.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salariedPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<c:set var="gridColNames"><%=HHSUtil.getHeader("contractBudgetSalariedDetailed.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("contractBudgetSalariedDetailed.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("contractBudgetSalariedDetailed.grid")%></c:set>
<!-- R6 - change for personnel services detailed view jqgrid alignment. -->
<%--Added setTooltipsOnColumnHeader for Defect-8500 --%>
<%-- Added numberCommaFormatTemplate and changeCurrency for Defect-8501--%>
<jq:grid id="salariedPositionDetailsGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         subGridRowNumbers="31"
         gridColNames="${gridColNames}" 
	     gridColProp="{name:'empPosition',width:'200'},{name:'internalTitle',width:'100'},{name:'annualSalary',width:'150',template:currencyTemplate},{name:'hourPerYear',width:'100',template:numberCommaFormatTemplate},{name:'budgetAmount',width:'150',template:currencyTemplate},{name:'invoicedAmount',width:'100',template:percentageTemplate}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:true, editrules:{required:false,text:true},editoptions:{maxlength:7}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${salariedGridDetailLoadData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName="budgetAmount,annualSalary,%"
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount"   
         positiveCurrency="budgetAmount,annualSalary,hourPerYear" 
   	     operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     callbackFunction="refreshPSSummaryNonGridData('${subBudgetId}','PSDetailScreen');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_DETAIL_SALARIED_"
   	     autoWidth="false"
   	     callbackFnAfterLoadGrid="$('#table_salariedPositionDetailsGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(5)').changeCurrency();setTooltipsOnColumnHeader($('#table_salariedPositionDetailsGrid-${subBudgetId}'),'5','Total Hours Worked for Organization. Fulltime employee is 2,087 hours.');setTooltipsOnColumnHeader($('#table_salariedPositionDetailsGrid-${subBudgetId}'),'6','City Funded Amount');"
/>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetHourlyDetailed.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='hourlyGridDetailLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetHourlyDetailed.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='hourlyPositionOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyPositionDetailsGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<!-- R6 - change for personnel services detailed view jqgrid alignment. -->
<c:set var="gridColNames1"><%=HHSUtil.getHeader("contractBudgetHourlyDetailed.grid")%></c:set>
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("contractBudgetHourlyDetailed.grid")%></c:set>
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("contractBudgetHourlyDetailed.grid")%></c:set>
<br>
<!-- Updated Rate column Template to currencyTemplate for Defect 8443 -->
<%--Added setTooltipsOnColumnHeader for Defect-8500 --%>
<%-- Added numberCommaFormatTemplate and changeCurrency for Defect-8501--%>
<jq:grid id="hourlyPositionDetailsGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         subGridRowNumbers="31"
         gridColNames="${gridColNames1}" 
	     gridColProp="{name:'empPosition',width:'200'},{name:'internalTitle',width:'100'},{name:'rate',width:'150',template:currencyTemplate},{name:'hourPerYear',width:'100',template:numberCommaFormatTemplate},{name:'budgetAmount',width:'150',template:currencyTemplate},{name:'invoicedAmount',width:'100',template:percentageTemplate}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:true, editrules:{required:false,text:true},editoptions:{maxlength:7}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:true, editrules:{required:true,number:true,allowOnlyPositiveValue}},
                  {editable:false, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${hourlyGridDetailLoadData}"
	     cellUrl="${hourlyPositionOperationGrid}"
	     editUrl="${hourlyPositionOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="10"
         isSubGrid="true"
         nonEditColumnName="invoicedAmount,remainingAmount"   
         positiveCurrency="budgetAmount,annualSalary,hourPerYear,rate" 
   	     operations="del:true,edit:true,add:true,cancel:true,save:true"
   	     callbackFunction="refreshPSSummaryNonGridData('${subBudgetId}','PSDetailScreen');"
   	     dropDownData="${personnelServiceMasterData}"
   	     exportFileName="PS_DETAIL_HOURLY_"
   	     callbackFnAfterLoadGrid="$('#table_hourlyPositionDetailsGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(5)').changeCurrency();refreshHourlyPositionHeader('${subBudgetId}',parentTotal);setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'4','Standard Hourly Wage');setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'5','Total Hours Worked for Organization. Fulltime employee is 2,087 hours.');setTooltipsOnColumnHeader($('#table_hourlyPositionDetailsGrid-${subBudgetId}'),'6','City Funded Amount');"
   	     autoWidth="false"
/>

<br>

<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="contractBudgetFringeBenifitsDetail.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='fringeGridDetailLoadData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenifitsDetailGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="contractBudgetFringeBenifitsDetail.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='fringeBenifitsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenifitsDetailGrid"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames3"><%=HHSUtil.getHeader("contractBudgetFringeBenifitsDetail.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("contractBudgetFringeBenifitsDetail.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("contractBudgetFringeBenifitsDetail.grid")%></c:set> 
<%--Added setTooltipsOnColumnHeader for Defect-8500 --%>
<jq:grid id="fringeBenifitsGrid-${subBudgetId}"
         isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames3}" 
	     gridColProp="${gridColProp3}" 
	     subGridColProp="${subGridColProp3}" 
		 gridUrl="${SubGridHeaderRow3}"
		 subGridUrl="${fringeGridDetailLoadData}"
	     cellUrl="${fringeBenifitsOperationGrid}"
	     editUrl="${fringeBenifitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="14"
         isSubGrid="true"
         positiveCurrency="budgetAmount" 
		 nonEditColumnName="invoicedAmount,remainingAmount"    
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     callbackFunction="refreshPSSummaryNonGridData('${subBudgetId}','PSDetailScreen');"
   	     callbackFnAfterLoadGrid="refreshFringGridHeader('${subBudgetId}','table_fringeBenifitsGrid-${subBudgetId}>tbody>tr:eq(1)>td:eq(2)', true);setTooltipsOnColumnHeader($('#table_fringeBenifitsGrid-${subBudgetId}'),'3','Total City Funded Amount');"
   	     exportFileName="PS_DETAIL_FRINGE_"
   	     autoWidth="true"
/>
<!-- Detailed ends -->
<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncome.jsp">
	<jsp:param value="1" name="entryTypeId" />
</jsp:include>
&nbsp;
</c:if>
<%-- R7 changes end --%>